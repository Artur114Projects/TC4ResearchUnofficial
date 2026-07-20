package com.wonginnovations.oldresearch.common.research;

import com.wonginnovations.oldresearch.api.OldResearchApi;
import net.minecraft.entity.player.EntityPlayer;

public class DefaultResearchComplexity implements IResearchComplexity {
    @Override
    public int calculateComplexity(EntityPlayer player, String key) {
        return OldResearchApi.oldResStorage(player).finishedNotes() / 10 + 1;
    }
}
