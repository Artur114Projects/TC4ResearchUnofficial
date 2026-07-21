package com.wonginnovations.oldresearch.common.items;

import com.wonginnovations.oldresearch.api.OldResearchApi;
import com.wonginnovations.oldresearch.common.init.InitItems;
import com.wonginnovations.oldresearch.main.OldResearch;
import com.wonginnovations.oldresearch.client.gui.ResearchNoteToast;
import com.wonginnovations.oldresearch.common.research.OldResearchManager;
import com.wonginnovations.oldresearch.common.research.ResearchNoteData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.HexUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public class ItemResearchNote extends Item {
    public ItemResearchNote() {
        this.setRegistryName(OldResearch.MODID + ":researchnote");
        this.setTranslationKey("researchnote");
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        this.setNoRepair();
    }

    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(@NotNull World world, EntityPlayer player, @NotNull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        ResearchNoteData data = noteData(stack);
        if (data != null && data.isComplete() && !ThaumcraftCapabilities.getKnowledge(player).isResearchComplete(data.key)) {
            if (!world.isRemote) {
                OldResearchApi.oldResStorage(player).incrementFinishedNotes();
                ResearchManager.progressResearch(player, data.key);
                world.playSound(null, player.posX, player.posY, player.posZ, SoundsTC.learn, SoundCategory.PLAYERS, 0.75F, 1.0F);
            } else {
                displayToast(ResearchCategories.getResearch(OldResearchManager.getStrippedKey(stack)));
            }

            return new ActionResult<>(EnumActionResult.SUCCESS, ItemStack.EMPTY);
        }

        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }

    @SideOnly(Side.CLIENT)
    public static void displayToast(ResearchEntry entry) {
        Minecraft.getMinecraft().getToastGui().add(new ResearchNoteToast(entry));
    }

    @SideOnly(Side.CLIENT)
    public static int getColorFromItemStack(ItemStack stack) {
        int c = 2337949;
        ResearchNoteData rd = noteData(stack);
        if(rd != null) {
            c = rd.color;
        }

        return c;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public @NotNull String getItemStackDisplayName(ItemStack itemstack) {
        return itemstack.getItemDamage() < 64 ? I18n.format("item.researchnote.name") : I18n.format("item.discovery.name");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip, @NotNull ITooltipFlag flagIn) {
        if(stack.getItemDamage() == 24 || stack.getItemDamage() == 42) {
            tooltip.add(TextFormatting.GOLD + I18n.format("item.researchnote.unknown.1"));
            tooltip.add(TextFormatting.BLUE + I18n.format("item.researchnote.unknown.2"));
        }

        ResearchNoteData rd = noteData(stack);
        ResearchEntry re = ResearchCategories.getResearch(OldResearchManager.getStrippedKey(stack));
        if (rd != null && rd.key != null && re != null) {
            tooltip.add(TextFormatting.GOLD + re.getLocalizedName());
//            int warp = OldResearchApi.getWarp(rd.key);
//            if(warp > 0) {
//                if(warp > 5) {
//                    warp = 5;
//                }
//
//                String ws = I18n.format("tc.forbidden");
//                String wr = I18n.format("tc.forbidden.level." + warp);
//                String wte = ws.replaceAll("%n", wr);
//                tooltip.add(TextFormatting.DARK_PURPLE + wte);
//            }
        }
    }

    @Override
    public @NotNull EnumRarity getRarity(ItemStack itemstack) {
        return itemstack.getItemDamage() < 64 ? EnumRarity.RARE : EnumRarity.EPIC;
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        ModelResourceLocation location0 = new ModelResourceLocation(OldResearch.MODID + ":researchnote", "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, location0);

        ModelResourceLocation location2 = new ModelResourceLocation(OldResearch.MODID + ":discovery", "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 64, location2);
    }

    public static ResearchNoteData noteData(ItemStack stack) {
        if (stack != null && stack.getItem() == InitItems.RESEARCH_NOTE) {
            ResearchNoteData data = new ResearchNoteData();
            if (stack.getTagCompound() == null) {
                return null;
            } else {
                data.key = stack.getTagCompound().getString("key");
                data.color = stack.getTagCompound().getInteger("color");
                data.complete = stack.getTagCompound().getBoolean("complete");
                data.copies = stack.getTagCompound().getInteger("copies");
                NBTTagList grid = stack.getTagCompound().getTagList("hexgrid", 10);
                data.hexEntries = new HashMap<>();

                for (int x = 0; x < grid.tagCount(); ++x) {
                    NBTTagCompound nbt = grid.getCompoundTagAt(x);
                    int q = nbt.getByte("hexq");
                    int r = nbt.getByte("hexr");
                    int type = nbt.getByte("type");
                    String tag = nbt.getString("aspect");
                    Aspect aspect = Aspect.getAspect(tag);
                    HexUtils.Hex hex = new HexUtils.Hex(q, r);
                    data.hexEntries.put(hex.toString(), new OldResearchManager.HexEntry(aspect, type));
                    data.hexes.put(hex.toString(), hex);
                }

                NBTTagList aspects = stack.getTagCompound().getTagList("aspects", 10);
                data.aspects = new AspectList();

                for (int x = 0; x < aspects.tagCount(); x++) {
                    NBTTagCompound nbt = aspects.getCompoundTagAt(x);
                    String tag = nbt.getString("aspect");
                    data.aspects.add(Aspect.getAspect(tag), 1);
                }

                return data;
            }
        }
        return null;
    }

    public static void setNoteData(ItemStack stack, ResearchNoteData data) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setString("key", data.key);
        stack.getTagCompound().setInteger("color", data.color);
        stack.getTagCompound().setBoolean("complete", data.complete);
        stack.getTagCompound().setInteger("copies", data.copies);
        NBTTagList gridtag = new NBTTagList();

        for (HexUtils.Hex hex : data.hexes.values()) {
            NBTTagCompound gt = new NBTTagCompound();
            gt.setByte("hexq", (byte)hex.q);
            gt.setByte("hexr", (byte)hex.r);
            gt.setByte("type", (byte) data.hexEntries.get(hex.toString()).type);
            if(data.hexEntries.get(hex.toString()).aspect != null) {
                gt.setString("aspect", data.hexEntries.get(hex.toString()).aspect.getTag());
            }

            gridtag.appendTag(gt);
        }

        stack.getTagCompound().setTag("hexgrid", gridtag);

        NBTTagList aspects = new NBTTagList();

        for (Aspect aspect : data.aspects.getAspects()) {
            NBTTagCompound asp = new NBTTagCompound();
            asp.setString("aspect", aspect.getTag());
            aspects.appendTag(asp);
        }

        stack.getTagCompound().setTag("aspects", aspects);
    }
}

