package com.wonginnovations.oldresearch.proxy;

import com.wonginnovations.oldresearch.registry.ManualRegister;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IProxy {
    void preInit(ManualRegister register, FMLPreInitializationEvent e);
    void init(ManualRegister register, FMLInitializationEvent e);
    void postInit(ManualRegister register, FMLPostInitializationEvent e);
}