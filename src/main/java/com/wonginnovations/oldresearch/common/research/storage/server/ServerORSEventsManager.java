package com.wonginnovations.oldresearch.common.research.storage.server;

import com.artur114.bananalib.mc.cap.BananaCapProv;
import com.wonginnovations.oldresearch.api.OldResearchApi;
import com.wonginnovations.oldresearch.common.init.InitCapabilities;
import com.wonginnovations.oldresearch.common.research.storage.OldResStorage;
import com.wonginnovations.oldresearch.main.OldResearch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class ServerORSEventsManager {
    public void attachCapabilitiesEventEntity(AttachCapabilitiesEvent<Entity> e) {
        e.addCapability(OldResearch.loc("research_storage"), new BananaCapProv<>(new OldResStorage((EntityPlayer) e.getObject()), InitCapabilities.OLD_RES_STORAGE));
    }

    public void playerEventPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent e) {
        OldResearchApi.oldResStorage(e.player).sync();
    }
}
