package com.wonginnovations.oldresearch.common.event.managers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import thaumcraft.common.items.tools.ItemThaumometer;

public class PlayerInteractManager {
    public void playerInteractEventRightClickBlock(PlayerInteractEvent.RightClickBlock e) {
        EntityPlayer player = e.getEntityPlayer();
        if (player.getActiveHand() != null && player.getHeldItem(player.getActiveHand()).getItem() instanceof ItemThaumometer) {
            e.setUseBlock(Event.Result.DENY);
        }
    }
}
