package com.wonginnovations.oldresearch.api;

import com.artur114.bananalib.mc.cap.BananaCaps;
import com.wonginnovations.oldresearch.common.init.InitCapabilities;
import com.wonginnovations.oldresearch.common.research.OldResearchManager;
import com.wonginnovations.oldresearch.common.research.OldResearchPattParser;
import com.wonginnovations.oldresearch.common.research.storage.IOldResStorage;
import com.wonginnovations.oldresearch.common.research.storage.OldResStorage;
import com.wonginnovations.oldresearch.main.OldResearch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class OldResearchApi {
    public static IOldResStorage oldResStorage(EntityPlayer player) {
        return BananaCaps.capability(player, InitCapabilities.OLD_RES_STORAGE).orElseGet(() -> {
            OldResearch.LOGGER.error("Player has no old research storage... wtf!?");
            return new OldResStorage(player);
        });
    }

    public static void registerOldResearch(ResourceLocation location) {
        OldResearchManager.registerNotePatterns(OldResearchPattParser.parse(location));
    }
}