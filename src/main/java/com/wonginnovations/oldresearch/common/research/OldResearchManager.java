package com.wonginnovations.oldresearch.common.research;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import com.wonginnovations.oldresearch.api.OldResearchApi;
import com.wonginnovations.oldresearch.common.research.curio.BaseCurio;
import com.wonginnovations.oldresearch.common.research.curio.RitesCurio;
import com.wonginnovations.oldresearch.common.OldResearchUtils;
import com.wonginnovations.oldresearch.common.items.ItemResearchNote;
import com.wonginnovations.oldresearch.common.init.InitItems;
import com.wonginnovations.oldresearch.core.mixin.ResearchManagerAccessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
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

public class OldResearchManager {
    protected static final Map<String, ItemStack> NOTES = new HashMap<>();
    private static final Map<String, List<String>> IMPLICIT_PARENTS = new HashMap<>();
    private static final Map<String, ResearchNotePattern> NOTE_PATTERNS = new HashMap<>();
    public static ArrayList<BaseCurio> CURIOS = new ArrayList<>();
    public static final Map<Aspect, Integer> ASPECT_COMPLEXITY = new HashMap<>();
    public static IResearchComplexity RESEARCH_COMPLEXITY_FUNCTION = new DefaultResearchComplexity();
    private static final Random RANDOM = new Random(69420);

    public static void registerNotePattern(ResearchNotePattern pattern) {
        NOTE_PATTERNS.put(pattern.oldResKey(), pattern);
    }

    public static void registerImplicitParents(String research, String... parents) {
        IMPLICIT_PARENTS.put(research, Arrays.asList(parents));
    }

    public static void registerNotePatterns(List<ResearchNotePattern> patterns) {
        patterns.forEach(OldResearchManager::registerNotePattern);
    }

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

    public static List<String> parentsOfResearch(String research) {
        ResearchEntry res = ResearchCategories.getResearch(research);

        if (res != null) {
            String[] parents = res.getParentsClean();

            if (parents != null) {
                List<String> ret = new ArrayList<>(Arrays.asList(parents));
                List<String> implicit = IMPLICIT_PARENTS.get(research);

                if (implicit != null) {
                    ret.addAll(implicit);
                }

                return ret;
            }
        }

        return Collections.emptyList();
    }

    public static AspectList getRandomAspects(Random rand, int maxComplexity, int quantity) {
        List<Aspect> possible = ASPECT_COMPLEXITY.keySet().stream().filter(aspect -> {
            int comp = ASPECT_COMPLEXITY.get(aspect);
            return comp <= maxComplexity && comp >= MathHelper.clamp((maxComplexity / 4) - 2, 0, 2);
        }).collect(Collectors.toList());
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
                ResearchStage[] stages = entry.getStages();
                for (int i = 0; i != stages.length; i++) {
                    ResearchStage stage = stages[i];
                    if (stage == null || stage.getKnow() == null) {
                        continue;
                    }

                    int theoryCount = 0;
                    for (ResearchStage.Knowledge knowledge : stage.getKnow()) {
                        if (knowledge.type == IPlayerKnowledge.EnumKnowledgeType.THEORY) {
                            theoryCount++;
                        }
                    }

                    if (theoryCount == 0) {
                        continue;
                    }

                    String key = "rn_" + entry.getKey() + "_" + i;
                    stage.setResearch(ArrayUtils.add(stage.getResearch(), key));
                    NOTES.put(key, createNote(key, theoryCount));

                    if (stage.getResearchIcon() == null) {
                        stage.setResearchIcon(new String[] {null});
                    } else {
                        stage.setResearchIcon(ArrayUtils.add(stage.getResearchIcon(), null));
                    }

                    stage.setKnow(null);
                }
            }
        }
    }

    private static ItemStack createNote(String key, int teoriesCount) {
        ItemStack note = new ItemStack(InitItems.RESEARCH_NOTE);
        ResearchNoteData data = new ResearchNoteData();
        data.key = key;
        data.mergedTeories = teoriesCount;
        Aspect[] asps = Aspect.aspects.values().toArray(new Aspect[0]);
        RANDOM.setSeed(key.hashCode());
        data.color =  asps[RANDOM.nextInt(asps.length)].getColor();
        ItemResearchNote.setNoteData(note, data);
        return note;
    }

    public static ItemStack noteStack(String key) {
        return NOTES.get(key).copy();
    }

    public static int getResearchComplexity(EntityPlayer player, String key) {
        return RESEARCH_COMPLEXITY_FUNCTION.calculateComplexity(player, key);
    }

    public static void givePlayerResearchNote(World world, EntityPlayer player, String key) {
        if (!hasResearchNote(player, key) && (player.isCreative() || (consumeInkFromPlayer(player, false) && OldResearchUtils.consumeInventoryItem(player, Items.PAPER)))) {
            consumeInkFromPlayer(player, true);


            ResearchNotePattern pattern = NOTE_PATTERNS.get(key);
            ItemStack note;

            if (pattern != null) {
                note = createNoteFromPattern(pattern, key);
            } else {
                note = createNoteFromRandom(player, world, key);
            }

            if (!player.inventory.addItemStackToInventory(note)) {
                ForgeHooks.onPlayerTossEvent(player, note, false);
            }

            player.inventoryContainer.detectAndSendChanges();
        }
    }

    private static ItemStack createNoteFromPattern(ResearchNotePattern pattern, String key) {
        ItemStack note = noteStack(key);
        ResearchNoteData data = ItemResearchNote.noteData(note);
        Random rand = new Random((31 * pattern.seed()) + key.hashCode());
        data.generateHexes(rand, pattern.aspects(), pattern.complexity());
        ItemResearchNote.setNoteData(note, data);
        return note;
    }

    private static ItemStack createNoteFromRandom(EntityPlayer player, World world, String key) {
        ItemStack note = noteStack(key);
        ResearchNoteData data = ItemResearchNote.noteData(note);
        Random rand = new Random(31 * ((31 * world.getSeed()) + key.hashCode()) + data.color);
        int complexity = getResearchComplexity(player, key) + data.mergedTeories;
        int complexityClamped = MathHelper.clamp(complexity, 0, 12);
        AspectList aspects = getRandomAspects(rand, complexity, Math.min(11, complexityClamped + 2));
        data.generateHexes(rand, aspects, complexityClamped);
        ItemResearchNote.setNoteData(note, data);
        return note;
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
            if (itemStack != null && itemStack.getItem() == InitItems.RESEARCH_NOTE && ItemResearchNote.noteData(itemStack) != null && ItemResearchNote.noteData(itemStack).key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static String getStrippedKey(ItemStack stack) {
        ResearchNoteData data = ItemResearchNote.noteData(stack);
        return (data != null)? getStrippedKey(data.key) : null;
    }

    public static String getStrippedKey(String key) {
        return key.substring(key.indexOf('_') + 1, key.lastIndexOf('_'));
    }

    public static boolean checkResearchCompletion(ItemStack contents, ResearchNoteData note, EntityPlayer player) {
        ArrayList<String> checked = new ArrayList<>();
        ArrayList<String> main = new ArrayList<>();
        ArrayList<String> remains = new ArrayList<>();

        for (HexUtils.Hex hex : note.hexes.values()) {
            if(note.hexEntries.get(hex.toString()).type == 1) {
                main.add(hex.toString());
            }
        }

        for (HexUtils.Hex hex : note.hexes.values()) {
            if(note.hexEntries.get(hex.toString()).type == 1) {
                main.remove(hex.toString());
                checkConnections(note, hex, checked, main, remains, player);
                break;
            }
        }

        if (!main.isEmpty()) {
            return false;
        } else {
            ArrayList<String> remove = new ArrayList<>();

            for (HexUtils.Hex hex : note.hexes.values()) {
                if(note.hexEntries.get(hex.toString()).type != 1 && !remains.contains(hex.toString())) {
                    remove.add(hex.toString());
                }
            }

            for (String s : remove) {
                note.hexEntries.remove(s);
                note.hexes.remove(s);
            }

            note.complete = true;
            ItemResearchNote.setNoteData(contents, note);
            return true;
        }
    }

    private static void checkConnections(ResearchNoteData note, HexUtils.Hex hex, ArrayList<String> checked, ArrayList<String> main, ArrayList<String> remains, EntityPlayer player) {
        checked.add(hex.toString());

        for (int a = 0; a < 6; ++a) {
            HexUtils.Hex target = hex.getNeighbour(a);
            if (!checked.contains(target.toString()) && note.hexEntries.containsKey(target.toString()) && note.hexEntries.get(target.toString()).type >= 1) {
                Aspect aspect1 = note.hexEntries.get(hex.toString()).aspect;
                Aspect aspect2 = note.hexEntries.get(target.toString()).aspect;
                if (OldResearchApi.oldResStorage(player).isKnowAspect(aspect1) && OldResearchApi.oldResStorage(player).isKnowAspect(aspect2) && (!aspect1.isPrimal() && (aspect1.getComponents()[0] == aspect2 || aspect1.getComponents()[1] == aspect2) || !aspect2.isPrimal() && (aspect2.getComponents()[0] == aspect1 || aspect2.getComponents()[1] == aspect1))) {
                    remains.add(target.toString());
                    if (note.hexEntries.get(target.toString()).type == 1) {
                        main.remove(target.toString());
                    }

                    checkConnections(note, target, checked, main, remains, player);
                }
            }
        }

    }

    public static Aspect getCombinationResult(Aspect aspect1, Aspect aspect2) {
        for (Aspect aspect : Aspect.aspects.values()) {
            if (aspect.getComponents() != null && (aspect.getComponents()[0] == aspect1 && aspect.getComponents()[1] == aspect2 || aspect.getComponents()[0] == aspect2 && aspect.getComponents()[1] == aspect1)) {
                return aspect;
            }
        }

        return null;
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
                    try {
                        JsonObject entry = element.getAsJsonObject();
                        ResearchEntry researchEntry = ResearchManagerAccessor.parseResearchJson(entry);
                        if (researchEntry != null && ResearchCategories.getResearchCategory(researchEntry.getCategory()) != null) {
                            ResearchManagerAccessor.addResearchToCategory(researchEntry);
                        }
                        a++;
                    } catch (Exception var13) {
                        Thaumcraft.log.warn("Invalid research entry [{}] found in {}", a, loc, var13);
                    }
                }

                Thaumcraft.log.info("Loaded {} research entries from {}", a, loc);
            } catch (Exception var14) {
                Thaumcraft.log.warn("Invalid research file: {}", loc);
            }
        } else {
            Thaumcraft.log.warn("Research file not found: {}", loc);
        }
    }

    public static void initCurios() {
        CURIOS.add((new BaseCurio("arcane")).setCategory("AUROMANCY"));
        CURIOS.add((new BaseCurio("preserved")).setCategory("ALCHEMY"));
        CURIOS.add((new BaseCurio("ancient")).setCategory("GOLEMANCY"));
        CURIOS.add((new BaseCurio("eldritch")).setCategory("ELDRITCH").setWarp(IPlayerWarp.EnumWarpType.NORMAL, 1).setWarp(IPlayerWarp.EnumWarpType.TEMPORARY, 5));
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