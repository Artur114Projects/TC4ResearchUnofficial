package com.wonginnovations.oldresearch.common.research.hexmap;

import thaumcraft.api.aspects.Aspect;

public class HexEntry {
    public Aspect aspect;
    public Type type;

    public HexEntry(Aspect aspect, Type type) {
        this.aspect = aspect;
        this.type = type;
    }

    public boolean isEmpty() {
        return this.aspect == null;
    }

    public enum Type {
        CONTAINER, SOURCE;

        public static Type of(int id) {
            return values()[id];
        }
    }
}
