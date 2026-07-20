package com.artur114.bananalib.mc.cap;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A class that provides convenience methods for obtaining a capability
 * @author Artur114
 * @since 1.1.0
 */
public class BananaCaps {
    public static <T> Optional<T> capability(Object obj, @Nullable Capability<T> cap) {
        return capability(obj, cap, (EnumFacing) null);
    }

    public static <T> Optional<T> capability(Object obj, @Nullable Capability<T> cap, @Nullable EnumFacing face) {
        if (obj instanceof ICapabilityProvider) {
            return capability((ICapabilityProvider) obj, cap, face);
        }

        return Optional.empty();
    }

    public static <T> Optional<T> capability(ICapabilityProvider provider, @Nullable Capability<T> cap) {
        return capability(provider, cap, (EnumFacing) null);
    }

    public static <T> Optional<T> capability(ICapabilityProvider provider, @Nullable Capability<T> cap, @Nullable EnumFacing face) {
        if (cap == null) return Optional.empty();
        return Optional.ofNullable(provider.getCapability(cap, face));
    }

    public static <C, T> Optional<T> capability(Object obj, @Nullable Capability<C> cap, Class<T> clazz) {
        return capability(obj, cap, clazz, null);
    }

    public static <C, T> Optional<T> capability(Object obj, @Nullable Capability<C> cap, Class<T> clazz, @Nullable EnumFacing face) {
        if (obj instanceof ICapabilityProvider) {
            return capability((ICapabilityProvider) obj, cap, clazz, face);
        }

        return Optional.empty();
    }

    public static <C, T> Optional<T> capability(ICapabilityProvider provider, @Nullable Capability<C> cap, Class<T> clazz) {
        return capability(provider, cap, clazz, null);
    }

    public static <C, T> Optional<T> capability(ICapabilityProvider provider, @Nullable Capability<C> cap, Class<T> clazz, @Nullable EnumFacing face) {
        if (cap == null) return Optional.empty();
        C capObj = provider.getCapability(cap, face);
        if (capObj == null) return Optional.empty();
        if (clazz.isInstance(capObj)) {
            return Optional.of(clazz.cast(capObj));
        }
        return Optional.empty();
    }
}