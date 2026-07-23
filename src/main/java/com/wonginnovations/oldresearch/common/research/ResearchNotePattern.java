package com.wonginnovations.oldresearch.common.research;

import thaumcraft.api.aspects.AspectList;

public class ResearchNotePattern {
    private final String targetResearch;
    private final int targetStage;
    private final AspectList aspects;
    private final int complexity;
    private final int hashDelta;
    private final int color;

    public ResearchNotePattern(String targetResearch, int targetStage, AspectList aspects, int complexity, int color, int hashDelta) {
        this.targetResearch = targetResearch;
        this.targetStage = targetStage;
        this.aspects = aspects;
        this.complexity = complexity;
        this.hashDelta = hashDelta;
        this.color = color;
    }

    public int color() {
        return this.color;
    }

    public String oldResKey() {
        return "rn_" + this.targetResearch + "_" + this.targetStage;
    }

    public AspectList aspects() {
        return this.aspects.copy();
    }

    public int complexity() {
        return this.complexity;
    }

    public long seed() {
        return 31 * ((31 * 2880321) + this.complexity) + this.hashDelta;
    }
}
