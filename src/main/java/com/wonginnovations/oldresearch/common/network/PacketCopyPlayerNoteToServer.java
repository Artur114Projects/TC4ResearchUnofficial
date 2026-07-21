package com.wonginnovations.oldresearch.common.network;

import com.wonginnovations.oldresearch.api.OldResearchApi;
import com.wonginnovations.oldresearch.common.items.ItemResearchNote;
import com.wonginnovations.oldresearch.main.OldResearch;
import com.wonginnovations.oldresearch.common.OldResearchUtils;
import com.wonginnovations.oldresearch.common.init.InitItems;
import com.wonginnovations.oldresearch.common.research.ResearchNoteData;
import com.wonginnovations.oldresearch.common.tiles.TileResearchTable;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.SoundsTC;

public class PacketCopyPlayerNoteToServer implements IMessage, IMessageHandler<PacketCopyPlayerNoteToServer, IMessage> {
    private long pos;

    public PacketCopyPlayerNoteToServer() {
    }

    public PacketCopyPlayerNoteToServer(BlockPos pos) {
        this.pos = pos.toLong();
    }

    public void toBytes(ByteBuf buffer) {
        buffer.writeLong(this.pos);
    }

    public void fromBytes(ByteBuf buffer) {
        this.pos = buffer.readLong();
    }

    public IMessage onMessage(PacketCopyPlayerNoteToServer message, MessageContext ctx) {
        IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            public void run() {
                World world = ctx.getServerHandler().player.world;
                EntityPlayerMP player = ctx.getServerHandler().player;
                if(world == null || player == null) return;

                BlockPos blockPos = BlockPos.fromLong(message.pos);
                TileEntity te = world.getTileEntity(blockPos);
                if (!(te instanceof TileResearchTable)) return;

                ItemStack tools = ((IInventory) te).getStackInSlot(0);
                ItemStack note = ((IInventory) te).getStackInSlot(1);
                if (note == null || note.isEmpty() || note.getItem() != InitItems.RESEARCH_NOTE) return;

                boolean failed = false;
                if (tools == null || tools.isEmpty()) {
                    player.sendMessage(new TextComponentString("§c" + I18n.format("researchnote.missing.tools")));
                    failed = true;
                } else if (!((TileResearchTable) te).canConsumeInkFromTable()) {
                    player.sendMessage(new TextComponentString("§c" + I18n.format("tile.researchtable.noink.0")));
                    player.sendMessage(new TextComponentString("§c" + I18n.format("tile.researchtable.noink.1")));
                    failed = true;
                }
                if (!OldResearchUtils.isPlayerCarrying(player, Items.PAPER)) {
                    player.sendMessage(new TextComponentString("§c" + I18n.format("researchnote.missing.paper")));
                    failed = true;
                }
                ResearchNoteData data = ItemResearchNote.noteData(note);
                for (Aspect aspect : data.aspects.getAspects()) {
                    if (OldResearchApi.oldResStorage(player).aspectCount(aspect) < 1 && ((TileResearchTable) te).bonusAspects.getAmount(aspect) < 1) {
                        player.sendMessage(new TextComponentString("§c" + I18n.format("tc.research.copy.failure", aspect.getName())));
                        failed = true;
                    }
                }
                if (!failed) {
                    ((TileResearchTable) te).consumeInkFromTable();
                    OldResearchUtils.consumeInventoryItem(player, Items.PAPER);

                    for (Aspect aspect : data.aspects.getAspects()) {
                        if (OldResearchApi.oldResStorage(player).aspectCount(aspect) >= 1) {
                            OldResearchApi.oldResStorage(player).addToAspectPool(aspect, -1);
                            OldResearch.NETWORK.sendTo(new PacketAspectPool(aspect.getTag(), 0, OldResearchApi.oldResStorage(player).aspectCount(aspect)), player);
                        } else {
                            ((TileResearchTable) te).bonusAspects.remove(aspect, 1);
                            player.world.notifyBlockUpdate(blockPos, world.getBlockState(blockPos), world.getBlockState(blockPos), 3);
                            te.markDirty();
                        }
                    }

                    data.copies = data.copies + 1;
                    ItemResearchNote.setNoteData(note, data);
                    if(!player.inventory.addItemStackToInventory(note.copy())) {
                        ForgeHooks.onPlayerTossEvent(player, note.copy(), false);
                    }

                    player.inventoryContainer.detectAndSendChanges();
                    world.playSound(player, blockPos, SoundsTC.write, SoundCategory.MASTER, 0.75F, 1.0F);
                }
            }
        });
        return null;
    }
}
