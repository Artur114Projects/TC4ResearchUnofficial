package com.wonginnovations.oldresearch.common.research;

import thaumcraft.api.aspects.AspectList;

public class ResearchNotePattern {
    private final String targetResearch;
    private final int targetNote;
    private final AspectList aspects;
    private final int complexity;
    private final int hashDelta;

    public ResearchNotePattern(String targetResearch, int targetNote, AspectList aspects, int complexity, int hashDelta) {
        this.targetResearch = targetResearch;
        this.targetNote = targetNote;
        this.aspects = aspects;
        this.complexity = complexity;
        this.hashDelta = hashDelta;
    }

    public String oldResKey() {
        return "rn_" + this.targetResearch + "_" + this.targetNote;
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
