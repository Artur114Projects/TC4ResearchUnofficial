package com.wonginnovations.oldresearch.core.mixin;

import com.wonginnovations.oldresearch.common.research.ScanManager;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.common.lib.research.ScanGeneric;

@Mixin(value = ScanGeneric.class, remap = false)
public abstract class ScanGenericMixin {
    @Inject(method = "onSuccess", at = @At("HEAD"), cancellable = true)
    private void onSuccessInjection(EntityPlayer player, Object obj, CallbackInfo ci) {
        ScanManager.objScanAspects(player, obj).aspects.forEach((aspect, count) -> {
            ScanManager.checkAndSyncAspectKnowledge(player, aspect, count);
        });
        ci.cancel();
    }
}
