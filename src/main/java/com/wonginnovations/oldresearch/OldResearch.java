package com.wonginnovations.oldresearch;

import com.wonginnovations.oldresearch.api.registration.IModelRegister;
import com.wonginnovations.oldresearch.client.ResearchNoteColorHandler;
import com.wonginnovations.oldresearch.common.items.ModItems;
import com.wonginnovations.oldresearch.proxy.Proxy;
import net.minecraft.item.Item;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = OldResearch.MODID, useMetadata = true)
@Mod.EventBusSubscriber(modid = OldResearch.MODID)
public class OldResearch {
    public static boolean aspectShift = false; // this may have to be non-static
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
    public void registerModels(ModelRegistryEvent event) { // TODO шиза, переделать
        for (Item item : Item.REGISTRY) {
            if (item.getRegistryName().getNamespace().equals(MODID) && item instanceof IModelRegister) {
                ((IModelRegister) item).registerModels();
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onColorHandlerEvent(ColorHandlerEvent.Item event) { // TODO перенести
        event.getItemColors().registerItemColorHandler(new ResearchNoteColorHandler(), ModItems.RESEARCHNOTE);
    }
}
