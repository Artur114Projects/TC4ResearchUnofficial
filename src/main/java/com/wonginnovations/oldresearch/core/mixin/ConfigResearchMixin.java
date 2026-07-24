package com.wonginnovations.oldresearch.core.mixin;

import com.wonginnovations.oldresearch.asm.ASMHookOldRes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.common.config.ConfigResearch;

@Mixin(value = ConfigResearch.class, remap = false)
public class ConfigResearchMixin {
    @Inject(method = "postInit", at = @At("RETURN"))
    private static void postInit(CallbackInfo ci) {
        ASMHookOldRes.hookAetherResearchInit();
    }
}
