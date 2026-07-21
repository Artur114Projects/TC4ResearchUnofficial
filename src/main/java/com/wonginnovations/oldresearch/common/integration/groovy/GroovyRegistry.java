package com.wonginnovations.oldresearch.common.integration.groovy;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.wonginnovations.oldresearch.common.init.InitBlocks;
import com.wonginnovations.oldresearch.common.research.DefaultResearchComplexity;
import com.wonginnovations.oldresearch.common.research.OldResearchManager;
import groovy.lang.Closure;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.research.ResearchCategories;

public class GroovyRegistry extends VirtualizedRegistry<Boolean> {

    @Override
    @GroovyBlacklist
    public void onReload() {
        OldResearchManager.RESEARCH_COMPLEXITY_FUNCTION = new DefaultResearchComplexity();
    }

    @Override
    @GroovyBlacklist
    public void afterScriptLoad() {
        OldResearchManager.ASPECT_COMPLEXITY.clear();
        ResearchCategories.getResearchCategory("BASICS").research.remove("KNOWLEDGETYPES");
        ResearchCategories.getResearchCategory("BASICS").research.remove("THEORYRESEARCH");
        ResearchCategories.getResearchCategory("BASICS").research.remove("CELESTIALSCANNING");
        OldResearchManager.parseJsonResearch(new ResourceLocation("oldresearch", "research.json"));
        OldResearchManager.patchResearch();
        ThaumcraftApi.registerObjectTag(new ItemStack(InitBlocks.RESEARCH_TABLE, 1, 32767), new AspectList(new ItemStack(BlocksTC.researchTable)));
        OldResearchManager.computeAspectComplexity();
    }

    public void complexity(Closure<Integer> func) {
        OldResearchManager.RESEARCH_COMPLEXITY_FUNCTION = new GroovyResearchComplexity(func);
    }
}
