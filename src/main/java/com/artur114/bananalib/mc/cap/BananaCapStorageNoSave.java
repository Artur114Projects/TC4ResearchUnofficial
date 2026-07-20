package com.artur114.bananalib.mc.cap;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

public class BananaCapStorageNoSave<T> implements Capability.IStorage<T> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {return null;}
    @Override
    public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {}
}
