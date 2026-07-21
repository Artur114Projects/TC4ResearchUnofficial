package com.wonginnovations.oldresearch.common.init;

import com.wonginnovations.oldresearch.common.blocks.BlockResearchTable;
import com.wonginnovations.oldresearch.main.OldResearch;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks {
    public static final BlockResearchTable RESEARCH_TABLE = new BlockResearchTable();
}
