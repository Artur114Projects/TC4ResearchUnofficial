package com.wonginnovations.oldresearch.proxy;

import com.wonginnovations.oldresearch.main.OldResearch;
import com.wonginnovations.oldresearch.common.blocks.ModBlocks;
import com.wonginnovations.oldresearch.common.network.PacketHandler;
import com.wonginnovations.oldresearch.common.research.OldResearchManager;
import com.wonginnovations.oldresearch.common.tiles.TileResearchTable;
import com.wonginnovations.oldresearch.registry.ManualRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.common.lib.crafting.DustTriggerSimple;

public class Proxy implements IGuiHandler {
    public final ManualRegister register = new ManualRegister();
    ProxyGUI proxyGUI = new ProxyGUI();

    public void registerModel(ItemBlock itemBlock) {
    }

    public void onConstruction(FMLConstructionEvent event) {
    }

    public void preInit(FMLPreInitializationEvent event) {
        OldResearchManager.initCurios();
        PacketHandler.preInit();
        GameRegistry.registerTileEntity(TileResearchTable.class, new ResourceLocation("oldresearch:TileResearchTable"));
        register.preInit();

        MinecraftForge.EVENT_BUS.register(OldResearch.INSTANCE);
    }

    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(OldResearch.INSTANCE, this);
        this.registerDisplayInformation();
    }

    public void postInit(FMLPostInitializationEvent event) {
        ResearchCategories.getResearchCategory("BASICS").research.remove("KNOWLEDGETYPES");
        ResearchCategories.getResearchCategory("BASICS").research.remove("THEORYRESEARCH");
        ResearchCategories.getResearchCategory("BASICS").research.remove("CELESTIALSCANNING");
        OldResearchManager.parseJsonResearch(new ResourceLocation("oldresearch", "research.json"));
        OldResearchManager.patchResearch();
        ThaumcraftApi.registerObjectTag(new ItemStack(ModBlocks.RESEARCHTABLE, 1, 32767), new AspectList(new ItemStack(BlocksTC.researchTable)));
        OldResearchManager.computeAspectComplexity();
        IDustTrigger.registerDustTrigger(new DustTriggerSimple("", BlocksTC.tableWood, new ItemStack(BlocksTC.researchTable)));
    }

    public void registerDisplayInformation() {
    }

    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return this.proxyGUI.getClientGuiElement(ID, player, world, x, y, z);
    }

    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return this.proxyGUI.getServerGuiElement(ID, player, world, x, y, z);
    }

    public boolean isClient() {
        return false;
    }

    public boolean isServer() {
        return true;
    }

    public World getClientWorld() {
        return null;
    }

}
