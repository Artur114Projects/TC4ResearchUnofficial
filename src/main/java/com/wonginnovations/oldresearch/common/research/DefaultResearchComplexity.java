package com.wonginnovations.oldresearch.common.research;

import com.wonginnovations.oldresearch.main.OldResearch;
import net.minecraft.entity.player.EntityPlayer;

public class DefaultResearchComplexity implements ResearchComplexityGenerator {
    @Override
    public Integer get(EntityPlayer player, String key) {
        int researchCompleted = OldResearch.proxy.getPlayerKnowledge().getResearchCompleted(player.getGameProfile().getName());
        return Math.floorDiv(researchCompleted, 10) + 1;
    }
}
