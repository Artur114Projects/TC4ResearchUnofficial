package com.wonginnovations.oldresearch.common.research;

import net.minecraft.entity.player.EntityPlayer;

public interface ResearchComplexityGenerator {
    Integer get(EntityPlayer player, String key);
}
