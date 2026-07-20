package com.wonginnovations.oldresearch.common.research.storage;

import com.wonginnovations.oldresearch.common.network.PacketSyncAspects;
import com.wonginnovations.oldresearch.main.OldResearch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OldResStorage implements IOldResStorage {
    private final Map<Aspect, Integer> aspects = new HashMap<>();
    private final EntityPlayer player;
    private int finishedNotes = 0;
    
    public OldResStorage(EntityPlayer player) {
        Aspect.getPrimalAspects().forEach(this::researchAspect);
        this.player = player;
    }

    @Override
    public boolean isKnowAspect(Aspect aspect) {
        if (aspect == null) {
            return false;
        }
        return this.aspects.containsKey(aspect);
    }

    @Override
    public boolean isKnowParentAspect(Aspect aspect) {
        if (aspect == null) {
            return false;
        }
        Aspect[] components = aspect.getComponents();
        if (components == null) {
            return true;
        }
        return this.aspects.keySet().containsAll(Arrays.asList(components));
    }

    @Override
    public boolean researchAspect(Aspect aspect) {
        if (aspect == null) {
            return false;
        }
        if (!this.aspects.containsKey(aspect)) {
            this.aspects.put(aspect, 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean setAspectCount(Aspect aspect, int count) {
        if (aspect == null) {
            return false;
        }
        this.aspects.put(aspect, count);
        return true;
    }

    @Override
    public boolean addToAspectPool(Aspect aspect, int count) {
        if (aspect == null) {
            return false;
        }
        if (count == 0) {
            return true;
        }
        if (count > 0) {
            this.aspects.put(aspect, this.aspectCount(aspect) + count);
        } else {
            if (!this.aspects.containsKey(aspect)) {
                return false;
            }
            int curr = this.aspectCount(aspect);
            if (curr + count < 0) {
                return false;
            }
            this.aspects.put(aspect, curr + count);
        }
        return true;
    }

    @Override
    public int aspectCount(Aspect aspect) {
        if (aspect == null) {
            return 0;
        }
        if (!this.isKnowAspect(aspect)) {
            return 0;
        }
        return this.aspects.getOrDefault(aspect, 0);
    }

    @Override
    public AspectList aspectsPool() {
        AspectList list = new AspectList();
        list.aspects.putAll(this.aspects);
        return list;
    }

    @Override
    public void incrementFinishedNotes() {
        this.finishedNotes++;
    }

    @Override
    public int finishedNotes() {
        return this.finishedNotes;
    }

    @Override
    public void sync() {
        if (!this.player.world.isRemote) {
            OldResearch.NETWORK.sendTo(new PacketSyncAspects(this), (EntityPlayerMP) this.player);
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound rootTag = new NBTTagCompound();
        NBTTagList aspectList = new NBTTagList();

        this.aspects.forEach((aspect, amount) -> {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("aspect", aspect.getTag());
            tag.setInteger("amount", amount);
            aspectList.appendTag(tag);
        });

        rootTag.setTag("aspectList", aspectList);
        rootTag.setInteger("finishedNotes", this.finishedNotes);

        return rootTag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        NBTTagList aspectList = nbt.getTagList("aspectList", 10);
        this.finishedNotes = nbt.getInteger("finishedNotes");
        for (int i = 0; i != aspectList.tagCount(); i++) {
            NBTTagCompound tag = aspectList.getCompoundTagAt(i);
            this.aspects.put(Aspect.getAspect(tag.getString("aspect")), tag.getInteger("amount"));
        }
    }
}
