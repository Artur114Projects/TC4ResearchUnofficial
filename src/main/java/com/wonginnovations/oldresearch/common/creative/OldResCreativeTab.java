package com.wonginnovations.oldresearch.common.creative;

import com.wonginnovations.oldresearch.common.init.InitItems;
import com.wonginnovations.oldresearch.common.items.ItemResearchNote;
import com.wonginnovations.oldresearch.common.research.OldResearchManager;
import com.wonginnovations.oldresearch.common.research.ResearchNoteData;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;
import java.util.List;

public class OldResCreativeTab extends CreativeTabs {
    public OldResCreativeTab(String label) {
        super(label);

        this.setBackgroundImageName("item_search.png");
        this.setNoTitle();
    }

    @Override
    public void displayAllRelevantItems(@NotNull NonNullList<ItemStack> list) {
        List<ItemStack> completed = new ArrayList<>();
        for (ItemStack stack : OldResearchManager.allNotes()) {
            ResearchNoteData data = ItemResearchNote.noteData(stack);
            data.generic = true;
            ItemStack complete = stack.copy();
            ItemResearchNote.setNoteData(stack, data);
            list.add(stack);
            data.generic = false;
            data.complete = true;
            complete.setItemDamage(64);
            ItemResearchNote.setNoteData(complete, data);
            completed.add(complete);
        }

        list.addAll(completed);
    }

    @Override
    public boolean hasSearchBar() {
        return true;
    }

    @Override
    public @NotNull ItemStack createIcon() {
        ItemStack stack = new ItemStack(InitItems.RESEARCH_NOTE);
        ResearchNoteData data = new ResearchNoteData();
        data.key = "";
        data.color = Aspect.DARKNESS.getColor();
        ItemResearchNote.setNoteData(stack, data);
        stack.setItemDamage(64);
        return stack;
    }
}
