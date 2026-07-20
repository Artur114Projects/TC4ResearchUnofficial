package com.wonginnovations.oldresearch.common.items;

import com.wonginnovations.oldresearch.main.OldResearch;
import com.wonginnovations.oldresearch.api.research.curio.BaseCurio;
import com.wonginnovations.oldresearch.common.research.OldResearchManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import thaumcraft.api.items.ItemsTC;

@Mod.EventBusSubscriber(modid = OldResearch.MODID)
public class ModItems {
    public static final Item RESEARCH_NOTE = new ItemResearchNote();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> r = event.getRegistry();
        r.register(RESEARCH_NOTE);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        int i = 0;
        for (BaseCurio curio : OldResearchManager.CURIOS) {
            ModelLoader.setCustomModelResourceLocation(ItemsTC.curio, i++, new ModelResourceLocation(curio.getTexture().toString()));
        }
    }
}
