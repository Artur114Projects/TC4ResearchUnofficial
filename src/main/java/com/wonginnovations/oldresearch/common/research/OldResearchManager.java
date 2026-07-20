package com.wonginnovations.oldresearch.common.research;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import com.wonginnovations.oldresearch.api.OldResearchApi;
import com.wonginnovations.oldresearch.api.research.curio.BaseCurio;
import com.wonginnovations.oldresearch.api.research.curio.RitesCurio;
import com.wonginnovations.oldresearch.common.OldResearchUtils;
import com.wonginnovations.oldresearch.common.items.ModItems;
import com.wonginnovations.oldresearch.core.mixin.ResearchManagerAccessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import org.apache.commons.lang3.ArrayUtils;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.common.lib.utils.HexUtils;

public abstract class OldResearchManager {
    public static ArrayList<BaseCurio> CURIOS = new ArrayList<>();
    private static final Map<String, ItemStack> NOTES = new HashMap<>();
    public static final Map<Aspect, Integer> ASPECT_COMPLEXITY = new HashMap<>();

    public static IResearchComplexity RESEARCH_COMPLEXITY_FUNCTION = new DefaultResearchComplexity();
    public static Map<String, AspectList> RESEARCH_ASPECTS = new HashMap<>(); // to be populated by external libs like GrS or CT

    private static final Random RANDOM = new Random(69420);

    public static void computeAspectComplexity() {
        for (Aspect aspect : Aspect.aspects.values()) {
            ASPECT_COMPLEXITY.put(aspect, computeAspectComplexity(aspect, 0));
        }
    }

    private static int computeAspectComplexity(Aspect aspect, int depth) {
        if (aspect.isPrimal()) return depth;
        ArrayList<Integer> childDepths = new ArrayList<>();
        for (Aspect asp : aspect.getComponents()) {
            childDepths.add(computeAspectComplexity(asp, depth + 1));
        }
        return Collections.max(childDepths);
    }

    public static int getAspectComplexity(Aspect a) {
        return ASPECT_COMPLEXITY.get(a);
    }

//    public static Aspect getRandomAspect(Random rand, int complexity) {
//        List<Aspect> possible = ASPECT_COMPLEXITY.keySet().stream().filter(aspect -> ASPECT_COMPLEXITY.get(aspect) <= complexity).toList();
//        return possible.get(rand.nextInt(possible.size()));
//    }

    public static AspectList getRandomAspects(Random rand, int maxComplexity, int quantity) {
        List<Aspect> possible = ASPECT_COMPLEXITY.keySet().stream().filter(aspect -> ASPECT_COMPLEXITY.get(aspect) <= maxComplexity).collect(Collectors.toList());
        AspectList selected = new AspectList();
        int upto = Math.min(quantity, possible.size());
        for (int i = 0; i < upto; i++) {
            int toadd = rand.nextInt(possible.size());
            selected.add(possible.get(toadd), 1);
            possible.remove(toadd);
        }

        return selected;
    }

    public static void patchResearch() {
        for (ResearchCategory category : ResearchCategories.researchCategories.values()) {
            for (ResearchEntry entry : category.research.values()) {
                int i = 0;
                for (ResearchStage stage : entry.getStages()) {
                    if (stage == null || stage.getKnow() == null || stage.getKnow().length == 0) continue;
                    for (ResearchStage.Knowledge knowledge : stage.getKnow()) {
                        if (knowledge.type == IPlayerKnowledge.EnumKnowledgeType.THEORY) {
                            String key = "rn_" + entry.getKey() + "_" + (++i);
                            stage.setResearch(ArrayUtils.add(stage.getResearch(), key));
                            NOTES.put(key, createNote(key));
                            if (stage.getResearchIcon() == null) stage.setResearchIcon(new String[]{null});
                            else stage.setResearchIcon(ArrayUtils.add(stage.getResearchIcon(), null));
                        }
                    }
                    stage.setKnow(null);
                }
            }
        }
    }

    private static ItemStack createNote(String key) {
        ItemStack note = new ItemStack(ModItems.RESEARCH_NOTE);
        ResearchNoteData data = new ResearchNoteData();
        data.key = key;
        Aspect[] asps = Aspect.aspects.values().toArray(new Aspect[0]);
        data.color =  asps[RANDOM.nextInt(asps.length)].getColor();
        updateData(note, data);
        return note;
    }

    public static ItemStack getNote(String key) {
        return NOTES.get(key);
    }

    public static int getResearchComplexity(EntityPlayer player, String key) {
        return 14; //return RESEARCH_COMPLEXITY_FUNCTION.get(player, key);
    }

    public static void givePlayerResearchNote(World world, EntityPlayer player, String key) {
        if (!hasResearchNote(player, key) && (player.isCreative() || (consumeInkFromPlayer(player, false) && OldResearchUtils.consumeInventoryItem(player, Items.PAPER)))) {
            consumeInkFromPlayer(player, true);
            ItemStack note = NOTES.get(key).copy();
            int complexity = getResearchComplexity(player, key);
            ResearchNoteData data = getData(note);
            Random rand = new Random(world.getSeed());
            AspectList aspects = (RESEARCH_ASPECTS.containsKey(key)) ? RESEARCH_ASPECTS.get(key) : getRandomAspects(rand, complexity, complexity + 2);
            data.generateHexes(rand, player, aspects, complexity);
            updateData(note, data);
            if (!player.inventory.addItemStackToInventory(note)) {
                ForgeHooks.onPlayerTossEvent(player, note, false);
            }

            player.inventoryContainer.detectAndSendChanges();
        }
    }

    public static boolean consumeInkFromPlayer(EntityPlayer player, boolean doit) {
        ItemStack[] inv = player.inventory.mainInventory.toArray(new ItemStack[0]);

        for (ItemStack itemStack : inv) {
            if (itemStack != null && itemStack.getItem() instanceof IScribeTools && itemStack.getItemDamage() < itemStack.getMaxDamage()) {
                if (doit) {
                    itemStack.damageItem(1, player);
                }

                return true;
            }
        }

        return false;
    }

    public static boolean hasResearchNote(EntityPlayer player, String key) {
        ItemStack[] inv = player.inventory.mainInventory.toArray(new ItemStack[0]);
        for (ItemStack itemStack : inv) {
            if (itemStack != null && itemStack.getItem() == ModItems.RESEARCH_NOTE && getData(itemStack) != null && getData(itemStack).key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static String getStrippedKey(ItemStack stack) {
        ResearchNoteData data = getData(stack);
        return (data != null)? getStrippedKey(data.key) : null;
    }

    public static String getStrippedKey(String key) {
        return key.substring(key.indexOf('_') + 1, key.lastIndexOf('_'));
    }

    public static boolean checkResearchCompletion(ItemStack contents, ResearchNoteData note, EntityPlayer player) {
        ArrayList<String> checked = new ArrayList<>();
        ArrayList<String> main = new ArrayList<>();
        ArrayList<String> remains = new ArrayList<>();

        for(HexUtils.Hex hex : note.hexes.values()) {
            if(note.hexEntries.get(hex.toString()).type == 1) {
                main.add(hex.toString());
            }
        }

        for(HexUtils.Hex hex : note.hexes.values()) {
            if(note.hexEntries.get(hex.toString()).type == 1) {
                main.remove(hex.toString());
                checkConnections(note, hex, checked, main, remains, player);
                break;
            }
        }

        if(main.size() != 0) {
            return false;
        } else {
            ArrayList<String> remove = new ArrayList<>();

            for(HexUtils.Hex hex : note.hexes.values()) {
                if(note.hexEntries.get(hex.toString()).type != 1 && !remains.contains(hex.toString())) {
                    remove.add(hex.toString());
                }
            }

            for(String s : remove) {
                note.hexEntries.remove(s);
                note.hexes.remove(s);
            }

            note.complete = true;
            updateData(contents, note);
            return true;
        }
    }

    private static void checkConnections(ResearchNoteData note, HexUtils.Hex hex, ArrayList<String> checked, ArrayList<String> main, ArrayList<String> remains, EntityPlayer player) {
        checked.add(hex.toString());

        for(int a = 0; a < 6; ++a) {
            HexUtils.Hex target = hex.getNeighbour(a);
            if(!checked.contains(target.toString()) && note.hexEntries.containsKey(target.toString()) && note.hexEntries.get(target.toString()).type >= 1) {
                Aspect aspect1 = note.hexEntries.get(hex.toString()).aspect;
                Aspect aspect2 = note.hexEntries.get(target.toString()).aspect;
                if (OldResearchApi.oldResStorage(player).isKnowAspect(aspect1) && OldResearchApi.oldResStorage(player).isKnowAspect(aspect2) && (!aspect1.isPrimal() && (aspect1.getComponents()[0] == aspect2 || aspect1.getComponents()[1] == aspect2) || !aspect2.isPrimal() && (aspect2.getComponents()[0] == aspect1 || aspect2.getComponents()[1] == aspect1))) {
                    remains.add(target.toString());
                    if(note.hexEntries.get(target.toString()).type == 1) {
                        main.remove(target.toString());
                    }

                    checkConnections(note, target, checked, main, remains, player);
                }
            }
        }

    }

    public static ResearchNoteData getData(ItemStack stack) {
        if(stack != null && stack.getItem() == ModItems.RESEARCH_NOTE) {
            ResearchNoteData data = new ResearchNoteData();
            if(stack.getTagCompound() == null) {
                return null;
            } else {
                data.key = stack.getTagCompound().getString("key");
                data.color = stack.getTagCompound().getInteger("color");
                data.complete = stack.getTagCompound().getBoolean("complete");
                data.copies = stack.getTagCompound().getInteger("copies");
                NBTTagList grid = stack.getTagCompound().getTagList("hexgrid", 10);
                data.hexEntries = new HashMap<>();

                for(int x = 0; x < grid.tagCount(); ++x) {
                    NBTTagCompound nbt = grid.getCompoundTagAt(x);
                    int q = nbt.getByte("hexq");
                    int r = nbt.getByte("hexr");
                    int type = nbt.getByte("type");
                    String tag = nbt.getString("aspect");
                    Aspect aspect = Aspect.getAspect(tag);
                    HexUtils.Hex hex = new HexUtils.Hex(q, r);
                    data.hexEntries.put(hex.toString(), new OldResearchManager.HexEntry(aspect, type));
                    data.hexes.put(hex.toString(), hex);
                }

                NBTTagList aspects = stack.getTagCompound().getTagList("aspects", 10);
                data.aspects = new AspectList();

                for (int x = 0; x < aspects.tagCount(); x++) {
                    NBTTagCompound nbt = aspects.getCompoundTagAt(x);
                    String tag = nbt.getString("aspect");
                    data.aspects.add(Aspect.getAspect(tag), 1);
                }

                return data;
            }
        }
        return null;
    }

    public static void updateData(ItemStack stack, ResearchNoteData data) {
        if(stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setString("key", data.key);
        stack.getTagCompound().setInteger("color", data.color);
        stack.getTagCompound().setBoolean("complete", data.complete);
        stack.getTagCompound().setInteger("copies", data.copies);
        NBTTagList gridtag = new NBTTagList();

        for(HexUtils.Hex hex : data.hexes.values()) {
            NBTTagCompound gt = new NBTTagCompound();
            gt.setByte("hexq", (byte)hex.q);
            gt.setByte("hexr", (byte)hex.r);
            gt.setByte("type", (byte) data.hexEntries.get(hex.toString()).type);
            if(data.hexEntries.get(hex.toString()).aspect != null) {
                gt.setString("aspect", data.hexEntries.get(hex.toString()).aspect.getTag());
            }

            gridtag.appendTag(gt);
        }

        stack.getTagCompound().setTag("hexgrid", gridtag);

        NBTTagList aspects = new NBTTagList();

        for (Aspect aspect : data.aspects.getAspects()) {
            NBTTagCompound asp = new NBTTagCompound();
            asp.setString("aspect", aspect.getTag());
            aspects.appendTag(asp);
        }

        stack.getTagCompound().setTag("aspects", aspects);
    }

    public static Aspect getCombinationResult(Aspect aspect1, Aspect aspect2) {
        for(Aspect aspect : Aspect.aspects.values()) {
            if(aspect.getComponents() != null && (aspect.getComponents()[0] == aspect1 && aspect.getComponents()[1] == aspect2 || aspect.getComponents()[0] == aspect2 && aspect.getComponents()[1] == aspect1)) {
                return aspect;
            }
        }

        return null;
    }

    public static boolean completeAspectUnsaved(EntityPlayer player, Aspect aspect, int amount) {
        if(aspect == null) {
            return false;
        } else {
            OldResearchApi.oldResStorage(player).researchAspect(aspect);
            OldResearchApi.oldResStorage(player).addToAspectPool(aspect, amount);
            return true;
        }
    }

    public static void completeAspect(EntityPlayer player, Aspect aspect, int amount) {
        completeAspectUnsaved(player, aspect, amount);
    }

    public static void parseJsonResearch(ResourceLocation loc) {
        JsonParser parser = new JsonParser();
        String s = "/assets/" + loc.getNamespace() + "/" + loc.getPath();
        InputStream stream = OldResearchManager.class.getResourceAsStream(s);
        if (stream != null) {
            try {
                InputStreamReader reader = new InputStreamReader(stream);
                JsonObject obj = parser.parse(reader).getAsJsonObject();
                JsonArray entries = obj.get("entries").getAsJsonArray();
                int a = 0;

                for (JsonElement element : entries) {
                    ++a;

                    try {
                        JsonObject entry = element.getAsJsonObject();
                        ResearchEntry researchEntry = ResearchManagerAccessor.parseResearchJson(entry);
                        if (researchEntry != null && ResearchCategories.getResearchCategory(researchEntry.getCategory()) != null) {
                            ResearchManagerAccessor.addResearchToCategory(researchEntry);
                        }
                    } catch (Exception var13) {
                        var13.printStackTrace();
                        Thaumcraft.log.warn("Invalid research entry [" + a + "] found in " + loc);
                        --a;
                    }
                }

                Thaumcraft.log.info("Loaded " + a + " research entries from " + loc);
            } catch (Exception var14) {
                Thaumcraft.log.warn("Invalid research file: " + loc);
            }
        } else {
            Thaumcraft.log.warn("Research file not found: " + loc);
        }
    }

    public static void initCurios() {
        CURIOS.add((new BaseCurio("arcane")).setCategory("AUROMANCY"));
        CURIOS.add((new BaseCurio("preserved")).setCategory("ALCHEMY"));
        CURIOS.add((new BaseCurio("ancient")).setCategory("GOLEMANCY"));
        CURIOS.add(
            (new BaseCurio("eldritch"))
                .setCategory("ELDRITCH")
                .setWarp(IPlayerWarp.EnumWarpType.NORMAL, 1)
                .setWarp(IPlayerWarp.EnumWarpType.TEMPORARY, 5)
        );
        CURIOS.add((new BaseCurio("knowledge")).setCategory("INFUSION"));
        CURIOS.add((new BaseCurio("twisted")).setCategory("ARTIFICE"));
        CURIOS.add(new RitesCurio());
        BaseCurio basic = new BaseCurio("basic");
        for (Aspect aspect : Aspect.getPrimalAspects()) basic.aspect(aspect, 15);
        CURIOS.add(basic);
    }

    public static class HexEntry {
        public Aspect aspect;
        public int type;

        public HexEntry(Aspect aspect, int type) {
            this.aspect = aspect;
            this.type = type;
        }
    }
}