package com.artur114.bananalib.mc.cap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BananaCapProv<C> implements ICapabilitySerializable<NBTTagCompound> {
    protected Capability<C> capability;
    protected C instance;

    public BananaCapProv(C instance, Capability<C> capability) {
        this.capability = capability;
        this.instance = instance;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.capability.readNBT(this.instance, null, nbt);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound) this.capability.writeNBT(this.instance, null);
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == this.capability;
    }

    @Nullable
    @Override
    public <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == this.capability ? this.capability.cast(this.instance) : null;
    }
}