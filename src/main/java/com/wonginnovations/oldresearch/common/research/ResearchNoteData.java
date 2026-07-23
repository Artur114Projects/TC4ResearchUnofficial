package com.wonginnovations.oldresearch.common.research;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.wonginnovations.oldresearch.api.OldResearchApi;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.utils.HexUtils;

public class ResearchNoteData {
    public HashMap<String, OldResearchManager.HexEntry> hexEntries = new HashMap<>();
    public HashMap<String, HexUtils.Hex> hexes = new HashMap<>();
    public AspectList aspects = new AspectList();
    public int mergedTeories;
    public boolean complete;
    public boolean generic;
    public String key;
    public int copies;
    public int color;

    public boolean isComplete() {
        return this.complete;
    }

    public void generateHexes(Random rand, AspectList aspects, int complexity) {
        int radius = 1 + Math.min(3, complexity);
        ArrayList<HexUtils.Hex> outerRing = HexUtils.distributeRingRandomly(radius, aspects.size(), rand);
        HashMap<String, HexUtils.Hex> hexLocations = HexUtils.generateHexes(radius);
        this.aspects = aspects;

        for (HexUtils.Hex hex : hexLocations.values()) {
            this.hexEntries.put(hex.toString(), new OldResearchManager.HexEntry(null, 0));
            this.hexes.put(hex.toString(), hex);
        }

        for (int i = 0; i != Math.min(outerRing.size(), aspects.size()); i++) {
            HexUtils.Hex hex = outerRing.get(i);
            this.hexEntries.put(hex.toString(), new OldResearchManager.HexEntry(aspects.getAspects()[i], 1));
            this.hexes.put(hex.toString(), hex);
        }

        if (complexity > 1) {
            int blanks = complexity;
            HexUtils.Hex[] temp = this.hexes.values().toArray(new HexUtils.Hex[0]);

            while (blanks > 0) {
                int randHex = rand.nextInt(temp.length);
                OldResearchManager.HexEntry randEntry = this.hexEntries.get(temp[randHex].toString());
                if (randEntry != null && randEntry.type == 0) {
                    boolean doRemove = true;

                    for (int n = 0; n != 6; n++) {
                        HexUtils.Hex neighbour = temp[randHex].getNeighbour(n);
                        OldResearchManager.HexEntry neighbourEntry = this.hexEntries.get(neighbour.toString());
                        if (neighbourEntry != null && neighbourEntry.type == 1) {
                            doRemove = false;
                        }
                    }

                    if (doRemove) {
                        this.hexes.remove(temp[randHex].toString());
                        this.hexEntries.remove(temp[randHex].toString());
                        temp = this.hexes.values().toArray(new HexUtils.Hex[0]);
                        blanks--;
                    }
                }
            }
        }
    }
}
