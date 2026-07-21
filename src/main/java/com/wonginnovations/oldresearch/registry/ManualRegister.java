package com.wonginnovations.oldresearch.registry;

import com.artur114.bananalib.mc.cap.BananaCapStorage;
import com.wonginnovations.oldresearch.api.OldResearchApi;
import com.wonginnovations.oldresearch.common.research.curio.BaseCurio;
import com.wonginnovations.oldresearch.client.renderer.TileResearchTableRenderer;
import com.wonginnovations.oldresearch.common.container.OldResearchGuiHandler;
import com.wonginnovations.oldresearch.common.init.ModBlocks;
import com.wonginnovations.oldresearch.common.init.ModItems;
import com.wonginnovations.oldresearch.common.items.ItemResearchNote;
import com.wonginnovations.oldresearch.common.network.*;
import com.wonginnovations.oldresearch.common.research.OldResearchManager;
import com.wonginnovations.oldresearch.common.research.ResearchNoteData;
import com.wonginnovations.oldresearch.common.research.storage.IOldResStorage;
import com.wonginnovations.oldresearch.common.research.storage.OldResStorage;
import com.wonginnovations.oldresearch.common.tiles.TileResearchTable;
import com.wonginnovations.oldresearch.main.OldResearch;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.common.lib.crafting.DustTriggerSimple;

import java.awt.*;
import java.util.Objects;

public class ManualRegister {
    public void preInit(Side side) {
        MinecraftForge.EVENT_BUS.register(this);
        this.initCaps();
        OldResearchManager.initCurios();
        GameRegistry.registerTileEntity(TileResearchTable.class, new ResourceLocation("oldresearch:TileResearchTable"));
        MinecraftForge.EVENT_BUS.register(OldResearch.INSTANCE);
        this.initNetwork();
    }

    public void midInit(Side side) {
        NetworkRegistry.INSTANCE.registerGuiHandler(OldResearch.INSTANCE, new OldResearchGuiHandler());

        if (side == Side.CLIENT) {
            this.initTESR();
        }
    }

    public void postInit(Side side) {
        ResearchCategories.getResearchCategory("BASICS").research.remove("KNOWLEDGETYPES");
        ResearchCategories.getResearchCategory("BASICS").research.remove("THEORYRESEARCH");
        ResearchCategories.getResearchCategory("BASICS").research.remove("CELESTIALSCANNING");
        OldResearchManager.parseJsonResearch(new ResourceLocation("oldresearch", "research.json"));
        OldResearchManager.patchResearch();
        this.initPatterns();
        ThaumcraftApi.registerObjectTag(new ItemStack(ModBlocks.RESEARCH_TABLE, 1, 32767), new AspectList(new ItemStack(BlocksTC.researchTable)));
        OldResearchManager.computeAspectComplexity();
        IDustTrigger.registerDustTrigger(new DustTriggerSimple("", BlocksTC.tableWood, new ItemStack(BlocksTC.researchTable)));
    }

    private void initCaps() {
        CapabilityManager.INSTANCE.register(
            IOldResStorage.class,
            new BananaCapStorage<>(),
            () -> new OldResStorage(null)
        );
    }

    private void initNetwork() {
        int discriminator = 0;
        OldResearch.NETWORK.registerMessage(PacketAspectCombinationToServer.class, PacketAspectCombinationToServer.class, discriminator++, Side.SERVER);
        OldResearch.NETWORK.registerMessage(PacketAspectDiscovery.class, PacketAspectDiscovery.class, discriminator++, Side.CLIENT);
        OldResearch.NETWORK.registerMessage(PacketAspectDiscoveryError.class, PacketAspectDiscoveryError.class, discriminator++, Side.CLIENT);
        OldResearch.NETWORK.registerMessage(PacketAspectPlaceToServer.class, PacketAspectPlaceToServer.class, discriminator++, Side.SERVER);
        OldResearch.NETWORK.registerMessage(PacketAspectPool.class, PacketAspectPool.class, discriminator++, Side.CLIENT);
        OldResearch.NETWORK.registerMessage(PacketGivePlayerNoteToServer.class, PacketGivePlayerNoteToServer.class, discriminator++, Side.SERVER);
        OldResearch.NETWORK.registerMessage(PacketCopyPlayerNoteToServer.class, PacketCopyPlayerNoteToServer.class, discriminator++, Side.SERVER);
        OldResearch.NETWORK.registerMessage(PacketSyncAspects.HandlerSA.class, PacketSyncAspects.class, discriminator++, Side.CLIENT);
        OldResearch.NETWORK.registerMessage(PacketSyncResearchTableAspects.class, PacketSyncResearchTableAspects.class, discriminator++, Side.CLIENT);
    }

    private void initPatterns() {
        OldResearchApi.registerOldResearch(OldResearch.loc("patterns/alchemy"));
        OldResearchApi.registerOldResearch(OldResearch.loc("patterns/auromancy"));
        OldResearchApi.registerOldResearch(OldResearch.loc("patterns/basics"));
        OldResearchApi.registerOldResearch(OldResearch.loc("patterns/eldritch"));
        OldResearchApi.registerOldResearch(OldResearch.loc("patterns/golemancy"));
        OldResearchApi.registerOldResearch(OldResearch.loc("patterns/infusion"));
    }

    @SideOnly(Side.CLIENT)
    public void initTESR() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileResearchTable.class, new TileResearchTableRenderer());
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> e) {
        e.getRegistry().register(ModBlocks.RESEARCH_TABLE.item);
        e.getRegistry().register(ModItems.RESEARCH_NOTE);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> e) {
        e.getRegistry().register(ModBlocks.RESEARCH_TABLE);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent e) {
        int i = 0;
        for (BaseCurio curio : OldResearchManager.CURIOS) ModelLoader.setCustomModelResourceLocation(ItemsTC.curio, i++, new ModelResourceLocation(curio.getTexture().toString()));
        ModelLoader.setCustomModelResourceLocation(ModBlocks.RESEARCH_TABLE.item, 0, new ModelResourceLocation(Objects.requireNonNull(ModBlocks.RESEARCH_TABLE.item.getRegistryName()), "inventory"));
        ModItems.RESEARCH_NOTE.registerModels();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onColorHandlerEvent(ColorHandlerEvent.Item event) {
        event.getItemColors().registerItemColorHandler(((stack, tintIndex) -> {
            switch (tintIndex) {
                case 0: return Color.WHITE.getRGB();
                case 1: {
                    int c = 10066329;
                    ResearchNoteData rd = ItemResearchNote.noteData(stack);
                    if (rd != null) {
                        c = rd.color;
                    }

                    return c;
                }
                default: {
                    return Color.BLACK.getRGB();
                }
            }
        }), ModItems.RESEARCH_NOTE);
    }
}
