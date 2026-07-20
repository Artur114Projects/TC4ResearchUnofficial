package com.wonginnovations.oldresearch.main;

import com.wonginnovations.oldresearch.api.registration.IModelRegister;
import com.wonginnovations.oldresearch.client.ResearchNoteColorHandler;
import com.wonginnovations.oldresearch.common.items.ModItems;
import com.wonginnovations.oldresearch.proxy.Proxy;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.Thaumcraft;

//TODO: Перенести регистрацию в ManualRegister
//TODO: Сделать консольные команды для исследований
//TODO: Использование curios умножает количество аспектов, починить
@Mod(modid = OldResearch.MODID, useMetadata = true)
@Mod.EventBusSubscriber(modid = OldResearch.MODID)
public class OldResearch {
    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("oldresearch");
    public static final Logger LOGGER = LogManager.getLogger("OldResearchUn");
    public static final String MODID = "oldresearch";

    @Instance
    public static OldResearch INSTANCE;

    @SidedProxy(
        clientSide = "com.wonginnovations.oldresearch.proxy.ClientProxy",
        serverSide = "com.wonginnovations.oldresearch.proxy.Proxy"
    )
    public static Proxy proxy;

    @Mod.EventHandler
    public void onConstruction(FMLConstructionEvent event) {
        proxy.onConstruction(event);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) { // TODO шиза, переделать и перенести
        for (Item item : Item.REGISTRY) {
            if (item.getRegistryName().getNamespace().equals(MODID) && item instanceof IModelRegister) {
                ((IModelRegister) item).registerModels();
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onColorHandlerEvent(ColorHandlerEvent.Item event) { // TODO перенести
        event.getItemColors().registerItemColorHandler(new ResearchNoteColorHandler(), ModItems.RESEARCH_NOTE);
    }

    public static ResourceLocation loc(String loc) {
        return new ResourceLocation(OldResearch.MODID, loc);
    }

    public static ResourceLocation thaumLoc(String loc) {
        return new ResourceLocation(Thaumcraft.MODID, loc);
    }
}
