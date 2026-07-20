package com.wonginnovations.oldresearch.registry;

import com.artur114.bananalib.mc.cap.BananaCapStorage;
import com.wonginnovations.oldresearch.common.research.storage.IOldResStorage;
import com.wonginnovations.oldresearch.common.research.storage.OldResStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class ManualRegister {
    public void preInit() {
        this.initCaps();
    }

    public void midInit() {

    }

    public void postInit() {

    }

    private void initCaps() {
        CapabilityManager.INSTANCE.register(
            IOldResStorage.class,
            new BananaCapStorage<>(),
            () -> new OldResStorage(null)
        );
    }
}
