package com.wonginnovations.oldresearch.asm;

import com.artur114.bananalib.asm.ASMTransformBus;
import com.artur114.bananalib.mc.asm.ASMLoggerLog4j;
import com.wonginnovations.oldresearch.asm.transform.BlockTableTransformer;
import com.wonginnovations.oldresearch.asm.transform.GuiResearchPageTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;

public class ASMTransformerOldRes implements IClassTransformer {
    public static final String HOOK_CLASS = "com/wonginnovations/oldresearch/asm/ASMHookOldRes";
    private final ASMLoggerLog4j logger = new ASMLoggerLog4j(LogManager.getLogger("OldResearchUnoff/ASM"));
    private final ASMTransformBus bus = new ASMTransformBus();

    public ASMTransformerOldRes() {
        this.bus.registerTransformer(
            new BlockTableTransformer(),
            new GuiResearchPageTransformer()
        );
        this.bus.registerDownListener(((tr, e) -> {
            logger.error("An exception occurred in transformer {}", tr, e);
        }));
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        return this.bus.transform(this.logger, transformedName, basicClass);
    }
}