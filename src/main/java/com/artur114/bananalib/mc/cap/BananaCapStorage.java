package com.artur114.bananalib.mc.cap;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

public class BananaCapStorage<T> implements Capability.IStorage<T> {
    private final Class<? extends NBTBase> targetNBT;

    public BananaCapStorage(Class<? extends NBTBase> targetNBT) {
        this.targetNBT = targetNBT;
    }

    public BananaCapStorage() {
        this(NBTTagCompound.class);
    }

    @Override
    public @Nullable NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
        if (!(instance instanceof INBTSerializable)) {
            return null;
        }

        return ((INBTSerializable<?>) instance).serializeNBT();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
        if (!(instance instanceof INBTSerializable) || !(this.targetNBT.isInstance(nbt))) {
            return;
        }

        ((INBTSerializable<NBTBase>) instance).deserializeNBT(nbt);
    }
}
