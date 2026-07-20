package com.wonginnovations.oldresearch.common.research;

import com.wonginnovations.oldresearch.api.OldResearchApi;
import com.wonginnovations.oldresearch.main.OldResearch;
import com.wonginnovations.oldresearch.common.network.PacketAspectDiscovery;
import com.wonginnovations.oldresearch.common.network.PacketAspectDiscoveryError;
import com.wonginnovations.oldresearch.common.network.PacketAspectPool;
import com.wonginnovations.oldresearch.common.config.ModConfig;
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

//    Not yet ( ._.)

//    private static AspectList generateNodeAspects(World world, String node) {
//        AspectList tags = new AspectList();
//        ArrayList<Integer> loc = (ArrayList)TileNode.locations.get(node);
//        if(loc != null && loc.size() > 0) {
//            int dim = ((Integer)loc.get(0)).intValue();
//            int x = ((Integer)loc.get(1)).intValue();
//            int y = ((Integer)loc.get(2)).intValue();
//            int z = ((Integer)loc.get(3)).intValue();
//            if(dim == world.provider.dimensionId) {
//                TileEntity tnb = world.getTileEntity(x, y, z);
//                if(tnb != null && tnb instanceof INode) {
//                    AspectList ta = ((INode)tnb).getAspects();
//
//                    for(Aspect a : ta.getAspectsSorted()) {
//                        tags.merge(a, Math.max(4, ta.getAmount(a) / 10));
//                    }
//
//                    switch(((INode)tnb).getNodeType()) {
//                        case UNSTABLE:
//                            tags.merge(Aspect.ENTROPY, 4);
//                            break;
//                        case HUNGRY:
//                            tags.merge(Aspect.HUNGER, 4);
//                            break;
//                        case TAINTED:
//                            tags.merge(Aspect.TAINT, 4);
//                            break;
//                        case PURE:
//                            tags.merge(Aspect.HEAL, 2);
//                            tags.add(Aspect.ORDER, 2);
//                            break;
//                        case DARK:
//                            tags.merge(Aspect.DEATH, 2);
//                            tags.add(Aspect.DARKNESS, 2);
//                    }
//                }
//            }
//        }
//
//        return tags.size() > 0?tags:null;
//    }

    public static int checkAndSyncAspectKnowledge(EntityPlayer player, Aspect aspect, int amount) {
        int save = 0;
        if (!OldResearchApi.oldResStorage(player).isKnowAspect(aspect)) {
            OldResearch.NETWORK.sendTo(new PacketAspectDiscovery(aspect.getTag()), (EntityPlayerMP) player);
            amount += 2;
            save = amount;
        }

        if (OldResearchApi.oldResStorage(player).aspectCount(aspect) >= ModConfig.aspectTotalCap) {
            amount = (int)Math.sqrt(amount);
        }

        if (amount > 1 && (float)OldResearchApi.oldResStorage(player).aspectCount(aspect) >= (float)ModConfig.aspectTotalCap * 1.25F) {
            amount = 1;
        }

        if (OldResearchApi.oldResStorage(player).addToAspectPool(aspect, amount)) {
            OldResearch.NETWORK.sendTo(new PacketAspectPool(aspect.getTag(), amount, OldResearchApi.oldResStorage(player).aspectCount(aspect)), (EntityPlayerMP)player);
            save = amount;
        }

        if (save > 0) {
            OldResearchManager.completeAspect(player, aspect, OldResearchApi.oldResStorage(player).aspectCount(aspect));
        }

        return save;
    }

    public static AspectList getScanAspects(EntityPlayer player, Object scan) {
        if (scan instanceof Entity && !(scan instanceof EntityItem)) {
            return AspectHelper.getEntityAspects((Entity) scan);
        } else {
            ItemStack is = ScanningManager.getItemFromParms(player, scan);
            if (is != null && !is.isEmpty()) {
                return AspectHelper.getObjectAspects(is);
            }
        }
        return new AspectList();
    }

    public static boolean canScanThing(EntityPlayer player, Object thing, boolean notify) {
        AspectList al = getScanAspects(player, thing);
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
