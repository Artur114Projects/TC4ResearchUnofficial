package com.wonginnovations.oldresearch.main;

import com.wonginnovations.oldresearch.asm.ASMHookOldRes;
import com.wonginnovations.oldresearch.common.creative.OldResCreativeTab;
import com.wonginnovations.oldresearch.proxy.IProxy;
import com.wonginnovations.oldresearch.registry.ManualRegister;
import com.wonginnovations.oldresearch.server.commands.CommandOldResearch;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.Thaumcraft;
import thaumcraft.api.research.ResearchStage;

//TODO: Переписать пакеты по нормальному
//TODO: Переписать миксины на asm
@Mod(modid = OldResearch.MODID, useMetadata = true)
public class OldResearch {
    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("oldresearch");
    public static final OldResCreativeTab CREATIVE_TAB = new OldResCreativeTab("oldresearch.creativetab");
    public static final ManualRegister MANUAL_REGISTER = new ManualRegister();
    public static final Logger LOGGER = LogManager.getLogger("OldResearchUn");
    public static final String MODID = "oldresearch";

    @Instance
    public static OldResearch INSTANCE;

    @SidedProxy(
        clientSide = "com.wonginnovations.oldresearch.proxy.ClientProxy",
        serverSide = "com.wonginnovations.oldresearch.proxy.ServerProxy"
    )
    public static IProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(MANUAL_REGISTER, event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(MANUAL_REGISTER, event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(MANUAL_REGISTER, event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent e) {
        e.registerServerCommand(new CommandOldResearch());
    }

    public static ResourceLocation loc(String loc) {
        return new ResourceLocation(OldResearch.MODID, loc);
    }

    public static ResourceLocation thaumLoc(String loc) {
        return new ResourceLocation(Thaumcraft.MODID, loc);
    }

    public static void t() {
        for (int i = 0; i != 10; i++) {
            GlStateManager.popMatrix();
            GlStateManager.popMatrix();

            GlStateManager.popMatrix();

            if (i == 1) {
                continue;
            }

            GlStateManager.popMatrix();
            GlStateManager.popMatrix();
            GlStateManager.popMatrix();
        }
    }
}
