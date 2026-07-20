package com.wonginnovations.oldresearch.core.mixin;

import com.wonginnovations.oldresearch.common.research.ScanManager;
import com.wonginnovations.oldresearch.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.research.ScanningManager;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.client.lib.events.RenderEventHandler;
import thaumcraft.common.items.tools.ItemThaumometer;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;

@Mixin(ItemThaumometer.class)
public abstract class ItemThaumometerMixin extends Item {
    @Shadow(remap = false)
    protected abstract RayTraceResult getRayTraceResultFromPlayerWild(World worldIn, EntityPlayer playerIn, boolean useLiquids);

    @Shadow(remap = false)
    private void updateAura(ItemStack stack, World world, EntityPlayerMP player) {}

    @Shadow(remap = false)
    public abstract void doScan(World worldIn, EntityPlayer playerIn);

    @Shadow(remap = false)
    protected abstract void drawFX(World worldIn, EntityPlayer playerIn);

    @Inject(method = "onItemRightClick", at = @At("HEAD"), cancellable = true)
    public void onItemRightClickInject(World world, EntityPlayer player, EnumHand hand, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        if (!ModConfig.instantScans) {
            ItemStack stack = player.getHeldItem(hand);
            player.setActiveHand(hand);
            cir.setReturnValue(new ActionResult<>(EnumActionResult.PASS, stack));
        }
    }

    @Override
    public void onUsingTick(@NotNull ItemStack stack, @NotNull EntityLivingBase entity, int count) {
        if (!(entity instanceof EntityPlayer) || ModConfig.instantScans) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        if(player.world.isRemote) {
            if (count <= 1) {
                this.oldresearch$updatePickUp();
            }
            if (count % 5 == 0) {
                this.drawFX(player.world, player);
            }
        } else {
            if (count <= 1) {
                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundsTC.scan, SoundCategory.PLAYERS, 1F, 1F);
                this.doScan(player.world, player);
            }
            if (count % 2 == 0) {
                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundsTC.ticks, SoundCategory.PLAYERS, 0.2F, 0.45F + player.world.rand.nextFloat() * 0.1F);
            }
        }
    }

    @Unique
    @SideOnly(Side.CLIENT)
    private void oldresearch$updatePickUp() {
        Minecraft mc = Minecraft.getMinecraft();
        mc.entityRenderer.itemRenderer.resetEquippedProgress(mc.player.getActiveHand());
    }

    @Override
    public int getMaxItemUseDuration(@NotNull ItemStack itemstack) {
        return 20;
    }

    @Override
    public @NotNull EnumAction getItemUseAction(@NotNull ItemStack stack) {
        return EnumAction.NONE;
    }

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    public void onUpdateInjection(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected, CallbackInfo ci) {
        if (isSelected && !world.isRemote && entity.ticksExisted % 20 == 0 && entity instanceof EntityPlayerMP) {
            this.updateAura(stack, world, (EntityPlayerMP)entity);
        }

        if (isSelected && world.isRemote && entity.ticksExisted % 5 == 0 && entity instanceof EntityPlayer) {
            Entity target = EntityUtils.getPointedEntity(world, entity, 1.0, 16.0, 5.0F, true);
            if (target != null && ScanningManager.isThingStillScannable((EntityPlayer)entity, target)) {
                FXDispatcher.INSTANCE.scanHighlight(target);
            }

            RenderEventHandler.thaumTarget = target;
            RayTraceResult mop = this.getRayTraceResultFromPlayerWild(world, (EntityPlayer)entity, true);
            if (mop != null && ScanningManager.isThingStillScannable((EntityPlayer) entity, mop.getBlockPos())) {
                FXDispatcher.INSTANCE.scanHighlight(mop.getBlockPos());
            }
        }

        ci.cancel();
    }

    @Inject(method = "doScan", at = @At("HEAD"), cancellable = true, remap = false)
    private void doScanInjection(World worldIn, EntityPlayer playerIn, CallbackInfo ci) {
        if (!worldIn.isRemote) {
            Entity target = EntityUtils.getPointedEntity(worldIn, playerIn, 1.0, 9.0, 0.0F, true);
            if (target != null && ScanManager.canScanThing(playerIn, target, true)) {
                ScanningManager.scanTheThing(playerIn, target);
            } else {
                RayTraceResult mop = this.rayTrace(worldIn, playerIn, true);
                if (mop != null && mop.getBlockPos() != null && ScanManager.canScanThing(playerIn, mop.getBlockPos(), true)) {
                    ScanningManager.scanTheThing(playerIn, mop.getBlockPos());
                } else {
                    // don't prevaildate this scan so things like the sky can still be scanned
                    // wait that isn't needed sky is for celestial events, idk tbh
                    // Hopefully this won't cause bugs D:
                    ScanningManager.scanTheThing(playerIn, null);
                }
            }
        }
        ci.cancel();
    }
}
