package com.wonginnovations.oldresearch.common.research;

import com.wonginnovations.oldresearch.api.OldResearchApi;
import com.wonginnovations.oldresearch.main.OldResearch;
import com.wonginnovations.oldresearch.common.network.PacketAspectDiscovery;
import com.wonginnovations.oldresearch.common.network.PacketAspectDiscoveryError;
import com.wonginnovations.oldresearch.common.network.PacketAspectPool;
import com.wonginnovations.oldresearch.common.config.OldConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ScanningManager;

public class ScanManager {
    public static void checkAndSyncAspectKnowledge(EntityPlayer player, Aspect aspect, int amount) {
        if (!OldResearchApi.oldResStorage(player).isKnowAspect(aspect)) {
            OldResearch.NETWORK.sendTo(new PacketAspectDiscovery(aspect.getTag()), (EntityPlayerMP) player);
            amount += 2;
        }

        if (OldResearchApi.oldResStorage(player).aspectCount(aspect) >= OldConfig.aspectTotalCap) {
            amount = (int) Math.sqrt(amount);
        }

        if (amount > 1 && (float)OldResearchApi.oldResStorage(player).aspectCount(aspect) >= (float) OldConfig.aspectTotalCap * 1.25F) {
            amount = 1;
        }

        if (OldResearchApi.oldResStorage(player).addToAspectPool(aspect, amount)) {
            OldResearch.NETWORK.sendTo(new PacketAspectPool(aspect.getTag(), amount, OldResearchApi.oldResStorage(player).aspectCount(aspect)), (EntityPlayerMP) player);
        }
    }

    public static AspectList objScanAspects(EntityPlayer player, Object obj) {
        if (obj == null) {
            return new AspectList();
        }
        if (obj instanceof Entity && !(obj instanceof EntityItem)) {
            return AspectHelper.getEntityAspects((Entity) obj);
        } else {
            ItemStack is = ScanningManager.getItemFromParms(player, obj);
            if (is != null && !is.isEmpty()) {
                return AspectHelper.getObjectAspects(is);
            }
        }
        return new AspectList();
    }

    public static boolean canScanThing(EntityPlayer player, Object thing, boolean notify) {
        AspectList al = objScanAspects(player, thing);
        if (al == null || al.size() < 0) return true;
        for (Aspect aspect : al.getAspects()) {
            if (aspect.getComponents() != null) {
                for (Aspect component : aspect.getComponents()) {
                    if (!OldResearchApi.oldResStorage(player).isKnowAspect(component)) {
                        if (notify && player instanceof EntityPlayerMP) OldResearch.NETWORK.sendTo(new PacketAspectDiscoveryError(component.getTag()), (EntityPlayerMP) player);
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
