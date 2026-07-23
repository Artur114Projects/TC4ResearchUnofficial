package com.wonginnovations.oldresearch.common.research;

import com.artur114.bananalib.util.graphs.BananaGraphs;
import com.wonginnovations.oldresearch.common.config.OldConfig;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;

import java.util.concurrent.atomic.AtomicInteger;

public class DefaultResearchComplexity implements IResearchComplexity {
    @Override
    public int calculateComplexity(String key) {
        AtomicInteger ret = new AtomicInteger();
        BananaGraphs.bfs(OldResearchManager.getStrippedKey(key), OldResearchManager::parentsOfResearch, (res) -> {
            ResearchEntry research = ResearchCategories.getResearch(res);
            if (research != null) {
                for (ResearchStage stage : research.getStages()) {
                    if (stage == null || stage.getResearch() == null) {
                        continue;
                    }
                    int comp = 0;
                    for (String s : stage.getResearch()) {
                        if (s.startsWith("rn_")) {
                            comp += OldResearchManager.NOTES.get(s).getTagCompound().getInteger("mergedTeories");
                        }
                    }
                    ret.addAndGet(comp);
                }
            }

            return false;
        });
        return (int) ((ret.get() + 1) * OldConfig.researchDifficultyMultiplier);
    }
}
