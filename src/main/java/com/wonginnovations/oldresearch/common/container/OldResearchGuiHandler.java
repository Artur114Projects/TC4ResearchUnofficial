package com.wonginnovations.oldresearch.common.container;

import com.wonginnovations.oldresearch.client.gui.GuiResearchTable;
import com.wonginnovations.oldresearch.common.tiles.TileResearchTable;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import org.jetbrains.annotations.Nullable;

public class OldResearchGuiHandler implements IGuiHandler {
    @Override
    public @Nullable Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 1) {
            return new ContainerResearchTable(player.inventory, (TileResearchTable) world.getTileEntity(new BlockPos(x, y, z)));
        }
        return null;
    }

    @Override
    public @Nullable Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 1) {
            return new GuiResearchTable(player, (TileResearchTable) world.getTileEntity(new BlockPos(x, y, z)));
        }
        return null;
    }
}
