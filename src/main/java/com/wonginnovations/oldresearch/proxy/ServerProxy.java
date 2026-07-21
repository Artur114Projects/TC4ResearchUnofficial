package com.wonginnovations.oldresearch.proxy;

import com.wonginnovations.oldresearch.registry.ManualRegister;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ServerProxy implements IProxy {
    @Override
    public void preInit(ManualRegister register, FMLPreInitializationEvent e) {
        register.preInit(Side.SERVER);
    }

    @Override
    public void init(ManualRegister register, FMLInitializationEvent e) {
        register.midInit(Side.SERVER);
    }

    @Override
    public void postInit(ManualRegister register, FMLPostInitializationEvent e) {
        register.postInit(Side.SERVER);
    }
}
