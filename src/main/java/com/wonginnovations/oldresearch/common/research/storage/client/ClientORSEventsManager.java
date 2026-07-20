package com.wonginnovations.oldresearch.common.research.storage.client;

import com.artur114.bananalib.mc.cap.BananaCapProvNoSave;
import com.wonginnovations.oldresearch.common.init.InitCapabilities;
import com.wonginnovations.oldresearch.common.research.storage.OldResStorage;
import com.wonginnovations.oldresearch.main.OldResearch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class ClientORSEventsManager {
    public void attachCapabilitiesEventEntity(AttachCapabilitiesEvent<Entity> e) {
        e.addCapability(OldResearch.loc("research_storage"), new BananaCapProvNoSave<>(new OldResStorage((EntityPlayer) e.getObject()), InitCapabilities.OLD_RES_STORAGE));
    }
}
