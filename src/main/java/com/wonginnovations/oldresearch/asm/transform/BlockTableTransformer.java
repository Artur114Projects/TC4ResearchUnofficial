package com.wonginnovations.oldresearch.asm.transform;

import com.artur114.bananalib.asm.AbstractASMTransformer;
import com.artur114.bananalib.asm.patterns.InsnPattern;
import com.artur114.bananalib.asm.patterns.MethodPattern;
import com.artur114.bananalib.asm.tree.ClassNodeAdv;
import com.artur114.bananalib.asm.util.IASMLogger;
import com.artur114.bananalib.asm.util.InsnBuilder;
import com.wonginnovations.oldresearch.asm.ASMTransformerOldRes;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

public class BlockTableTransformer extends AbstractASMTransformer {

    public BlockTableTransformer() {
        super("thaumcraft.common.blocks.basic.BlockTable");
    }

    @Override
    protected ClassNodeAdv transform(IASMLogger logger, String className, ClassNodeAdv clazz) {
        clazz.findMethod(FMLLaunchHandler.isDeobfuscatedEnvironment() ? "onBlockActivated" : "func_180639_a").ifPresent((method) -> {
            InsnPattern pattern = InsnPattern.pattern(FIELD_INSN.withOpcode(GETSTATIC).withOwner("thaumcraft/api/blocks/BlocksTC").withName("researchTable"));
            method.instructions.findPattern(pattern, 0).ifPresent(interval -> {
                logger.info("Injecting patches into method {}.{}{}", className, method.name, method.desc);
                method.instructions.replace(interval, new InsnBuilder().invokeStatic(ASMTransformerOldRes.HOOK_CLASS, "hookNewResearchTable", "()Lnet/minecraft/block/Block;").build());
            });
            method.instructions.findPattern(pattern, 1).ifPresent(interval -> {
                logger.info("Injecting patches into method {}.{}{}", className, method.name, method.desc);
                method.instructions.replace(interval, new InsnBuilder().invokeStatic(ASMTransformerOldRes.HOOK_CLASS, "hookNewResearchTable", "()Lnet/minecraft/block/Block;").build());
            });
        });
        return clazz;
    }

    @Override
    public int priority() {
        return 0;
    }
}
