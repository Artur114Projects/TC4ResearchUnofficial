package com.wonginnovations.oldresearch.asm.transform;

import com.artur114.bananalib.asm.AbstractASMTransformer;
import com.artur114.bananalib.asm.patterns.InsnPattern;
import com.artur114.bananalib.asm.patterns.MethodPattern;
import com.artur114.bananalib.asm.tree.ClassNodeAdv;
import com.artur114.bananalib.asm.util.IASMLogger;
import com.artur114.bananalib.asm.util.InsnBuilder;
import com.wonginnovations.oldresearch.asm.ASMTransformerOldRes;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.util.Iterator;

public class BlockTableTransformer extends AbstractASMTransformer {

    public BlockTableTransformer() {
        super("thaumcraft.common.blocks.basic.BlockTable");
    }

    @Override
    protected ClassNodeAdv transform(IASMLogger logger, String className, ClassNodeAdv clazz) {
        boolean def = FMLLaunchHandler.isDeobfuscatedEnvironment();
        clazz.findMethod(def ? "onBlockActivated" : "func_180639_a").ifPresent((method) -> {
            InsnPattern pattern = InsnPattern.pattern(FIELD_INSN.withOpcode(GETSTATIC).withOwner("thaumcraft/api/blocks/BlocksTC").withName("researchTable"));
            method.instructions.findPattern(pattern).forEach(interval -> {
                logger.info("Injecting patches into method {}.{}{}", className, method.name, method.desc);
                method.instructions.replace(interval, new InsnBuilder().invokeStatic(ASMTransformerOldRes.HOOK_CLASS, "hookNewResearchTable", "()Lnet/minecraft/block/Block;").build());
            });

            pattern = InsnPattern.pattern(TYPE_INSN.withOpcode(CHECKCAST).withDesc("thaumcraft/common/tiles/crafting/TileResearchTable"));

            method.instructions.findPattern(pattern).forEach(interval -> {
                logger.info("Injecting patches into method {}.{}{}", className, method.name, method.desc);
                method.instructions.replace(interval, new InsnBuilder().typeInsn(CHECKCAST, "com/wonginnovations/oldresearch/common/tiles/TileResearchTable").build());
            });

            pattern = InsnPattern.pattern(METHOD_INSN.withOwner("thaumcraft/common/tiles/crafting/TileResearchTable"));

            method.instructions.findPattern(pattern).forEach(interval -> {
                logger.info("Injecting patches into method {}.{}{}", className, method.name, method.desc);
                MethodInsnNode node = (MethodInsnNode) interval.end();
                method.instructions.replace(interval, new InsnBuilder().methodInsn(node.getOpcode(), "com/wonginnovations/oldresearch/common/tiles/TileResearchTable", node.name, node.desc, node.itf).build());
            });
        });
        return clazz;
    }

    @Override
    public int priority() {
        return 0;
    }
}
