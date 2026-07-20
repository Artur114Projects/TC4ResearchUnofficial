package com.wonginnovations.oldresearch.common.init;

import com.wonginnovations.oldresearch.common.research.storage.IOldResStorage;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class InitCapabilities {
    @CapabilityInject(IOldResStorage.class)
    public static final Capability<IOldResStorage> OLD_RES_STORAGE = null;
}
