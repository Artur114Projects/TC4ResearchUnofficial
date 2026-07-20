package com.wonginnovations.oldresearch.common.event;

import com.wonginnovations.oldresearch.common.event.managers.PlayerInteractManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class CommonEventsHandler {
    public static final PlayerInteractManager PLAYER_INTERACT_MANAGER = new PlayerInteractManager();

    @SubscribeEvent
    public static void playerRightClickEvent(PlayerInteractEvent.RightClickBlock e) {
        PLAYER_INTERACT_MANAGER.playerInteractEventRightClickBlock(e);
    }
}
