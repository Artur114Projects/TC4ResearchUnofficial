package com.wonginnovations.oldresearch.common.research.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public interface IOldResStorage extends INBTSerializable<NBTTagCompound> {
    boolean isKnowAspect(Aspect aspect);
    boolean isKnowParentAspect(Aspect aspect);
    boolean researchAspect(Aspect aspect);
    boolean setAspectCount(Aspect aspect, int count);
    boolean addToAspectPool(Aspect aspect, int count);
    int aspectCount(Aspect aspect);
    AspectList aspectsPool();
    void incrementFinishedNotes();
    int finishedNotes();
    void sync();
}
