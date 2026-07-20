package com.wonginnovations.oldresearch.common.research;

import net.minecraft.entity.player.EntityPlayer;

public interface IResearchComplexity {
    int calculateComplexity(EntityPlayer player, String key);
}
