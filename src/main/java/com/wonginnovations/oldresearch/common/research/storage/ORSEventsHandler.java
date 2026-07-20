package com.wonginnovations.oldresearch.common.research.storage;

import com.wonginnovations.oldresearch.common.research.storage.client.ClientORSEventsManager;
import com.wonginnovations.oldresearch.common.research.storage.server.ServerORSEventsManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber
public class ORSEventsHandler {
    public static final ClientORSEventsManager CLIENT_MANAGER = new ClientORSEventsManager();
    public static final ServerORSEventsManager SERVER_MANAGER = new ServerORSEventsManager();

    @SubscribeEvent
    public static void attachCapabilitiesChunk(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof EntityPlayer) {
            if (e.getObject().world != null &&  e.getObject().world.isRemote) CLIENT_MANAGER.attachCapabilitiesEventEntity(e);
            if (e.getObject().world != null && !e.getObject().world.isRemote) SERVER_MANAGER.attachCapabilitiesEventEntity(e);
        }
    }

    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.player != null) SERVER_MANAGER.playerEventPlayerLoggedInEvent(e);
    }
}
