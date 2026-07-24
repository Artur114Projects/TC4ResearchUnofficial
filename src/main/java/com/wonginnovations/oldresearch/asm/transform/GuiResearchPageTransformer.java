package com.wonginnovations.oldresearch.asm.transform;

import com.artur114.bananalib.asm.AbstractASMTransformer;
import com.artur114.bananalib.asm.patterns.InsnPatBuilder;
import com.artur114.bananalib.asm.patterns.InsnPattern;
import com.artur114.bananalib.asm.tree.ClassNodeAdv;
import com.artur114.bananalib.asm.util.IASMLogger;
import com.artur114.bananalib.asm.util.InsnBuilder;
import com.artur114.bananalib.asm.util.InsnCodes;
import com.artur114.bananalib.asm.util.InsnInterval;
import com.wonginnovations.oldresearch.asm.ASMTransformerOldRes;
import org.objectweb.asm.tree.*;

public class GuiResearchPageTransformer extends AbstractASMTransformer {

    public GuiResearchPageTransformer() {
        super("thaumcraft.client.gui.GuiResearchPage");
    }

    @Override
    protected ClassNodeAdv transform(IASMLogger logger, String className, ClassNodeAdv clazz) {
        for (FieldNode node : clazz.fields) {
            if (node.name.equals("oldresearch$renderedNotes")) {
                return clazz;
            }
        }

        FieldNode field = new FieldNode(
            ACC_PRIVATE | ACC_FINAL,
            "oldresearch$renderedNotes",
            "Ljava/util/Map;",
            "Ljava/util/Map<Ljava/awt/Point;Lnet/minecraft/item/ItemStack;>;",
            null
        );
        clazz.fields.add(field);

        clazz.findMethod("drawRequirements").ifPresent(method -> {
            InsnPatBuilder builder = InsnPattern.builder();
            InsnBuilder insn = new InsnBuilder();
            builder.thenMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glPushMatrix", "()V", false);
            builder.thenInsn(ICONST_0);
            builder.thenVarInsn(ISTORE, 6);
            builder.thenVarInsn(ALOAD, 4);
            builder.thenMethodInsn(INVOKEVIRTUAL, "thaumcraft/api/research/ResearchStage", "getResearch", "()[Ljava/lang/String;", false);
            builder.then(InsnCodes.JUMP_INSN.withOpcode(IFNULL));

            method.instructions.findPattern(builder.build(), 0).ifPresent(interval -> {
                logger.info("Injecting patches into method {}.{}{}", className, method.name, method.desc);
                insn.varInsn(ALOAD, 4);
                insn.invokeStatic(ASMTransformerOldRes.HOOK_CLASS, "hookStageContainsOnlyNotes", "(Lthaumcraft/api/research/ResearchStage;)Z");
                insn.jumpInsn(IFNE, ((JumpInsnNode) interval.end()).label);
                method.instructions.insert(interval.end(), insn.build());
            });

            builder.thenVarInsn(ALOAD, 4);
            builder.thenMethodInsn(INVOKEVIRTUAL, "thaumcraft/api/research/ResearchStage", "getKnow", "()[Lthaumcraft/api/research/ResearchStage$Knowledge;", false);
            builder.then(InsnCodes.JUMP_INSN.withOpcode(IFNULL));

            method.instructions.findPattern(builder.build(), 0).ifPresent(interval -> {
                logger.info("Injecting patches into method {}.{}{}", className, method.name, method.desc);
                insn.varInsn(ALOAD, 4);
                insn.invokeStatic(ASMTransformerOldRes.HOOK_CLASS, "hookStageContainsNote", "(Lthaumcraft/api/research/ResearchStage;)Z");
                insn.jumpInsn(IFEQ, ((JumpInsnNode) interval.end()).label);
                method.instructions.insert(interval.end(), insn.build());
            });

            builder.thenVarInsn(ILOAD, 7);
            builder.thenInsn(IADD);
            builder.thenVarInsn(ILOAD, 5);
            builder.thenVarInsn(ILOAD, 2);
            builder.thenVarInsn(ILOAD, 3);
            builder.thenVarInsn(ALOAD, 12);
            builder.thenMethodInsn(INVOKEVIRTUAL, "thaumcraft/client/gui/GuiResearchPage", "drawPopupAt", "(IIIILjava/lang/String;)V", false);
            builder.thenVarInsn(ILOAD, 7);
            builder.thenVarInsn(ILOAD, 9);
            builder.thenInsn(IADD);
            builder.thenVarInsn(ISTORE, 7);
            builder.thenAnyInsn();
            builder.then(InsnCodes.JUMP_INSN.withOpcode(GOTO));
            method.instructions.findPattern(builder.build(), 0).ifPresent(j -> {
                LabelNode label = ((JumpInsnNode) j.end()).label;

                builder.thenMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glPushMatrix", "()V", false);
                builder.thenIntInsn(SIPUSH, 3042);
                builder.thenMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glEnable", "(I)V", false);
                builder.thenIntInsn(SIPUSH, 770);
                builder.thenIntInsn(SIPUSH, 771);
                builder.thenMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glBlendFunc", "(II)V", false);
                builder.thenVarInsn(ALOAD, 8);

                method.instructions.findPattern(builder.build(), 0).ifPresent(interval -> {
                    insn.varInsn(ALOAD, 11);
                    insn.ldcInsn("rn_");
                    insn.methodInsn(INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false);

                    insn.thenIf((b) -> {
                        b.then(new IincInsnNode(10, 1));
                        b.jumpInsn(GOTO, label);
                    });

                    method.instructions.insertBefore(interval.start(), insn.build());
                });
            });

            builder.thenVarInsn(ALOAD, 4);
            builder.thenMethodInsn(INVOKEVIRTUAL, "thaumcraft/api/research/ResearchStage", "getKnow", "()[Lthaumcraft/api/research/ResearchStage$Knowledge;", false);
            builder.thenVarInsn(ILOAD, 9);
            builder.thenInsn(AALOAD);
            builder.thenVarInsn(ASTORE, 10);
            builder.thenInsn(FCONST_1);
            builder.thenInsn(FCONST_1);
            builder.thenInsn(FCONST_1);
            builder.thenInsn(FCONST_1);
            builder.thenMethodInsn(INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "color", "(FFFF)V", false);
            builder.thenMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glPushMatrix", "()V", false);
            builder.thenVarInsn(ALOAD, 0);
            builder.thenFieldInsn(GETFIELD, "thaumcraft/client/gui/GuiResearchPage", "mc", "Lnet/minecraft/client/Minecraft;");
            builder.thenFieldInsn(GETFIELD, "net/minecraft/client/Minecraft", "renderEngine", "Lnet/minecraft/client/renderer/texture/TextureManager;");
            builder.thenFieldInsn(GETSTATIC, "thaumcraft/client/lib/events/HudHandler", "KNOW_TYPE", "[Lnet/minecraft/util/ResourceLocation;");

            method.instructions.findPattern(builder.build(), 0).ifPresent(start -> {
                builder.thenVarInsn(ALOAD, 0);
                builder.thenVarInsn(ILOAD, 1);
                builder.thenIntInsn(BIPUSH, 15);
                builder.thenInsn(ISUB);
                builder.thenVarInsn(ILOAD, 7);
                builder.thenInsn(IADD);
                builder.thenVarInsn(ILOAD, 5);
                builder.thenVarInsn(ILOAD, 2);
                builder.thenVarInsn(ILOAD, 3);
                builder.thenVarInsn(ALOAD, 12);
                builder.thenMethodInsn(INVOKEVIRTUAL, "thaumcraft/client/gui/GuiResearchPage", "drawPopupAt", "(IIIILjava/lang/String;)V", false);
                builder.thenVarInsn(ILOAD, 7);
                builder.thenVarInsn(ILOAD, 8);
                builder.thenInsn(IADD);
                builder.thenVarInsn(ISTORE, 7);

                method.instructions.findPattern(builder.build(), 0).ifPresent(end -> {
                    AbstractInsnNode node = start.start().getPrevious();
                    method.instructions.replace(new InsnInterval(method.instructions, start.start(), end.end()), new InsnList());

                    insn.varInsn(ALOAD, 0);
                    insn.fieldInsn(GETFIELD, "thaumcraft/client/gui/GuiResearchPage", "oldresearch$renderedNotes", "Ljava/util/Map;");
                    insn.varInsn(ALOAD, 0);
                    insn.fieldInsn(GETFIELD, "thaumcraft/client/gui/GuiResearchPage", "tipText", "Ljava/util/List;");
                    insn.fieldInsn(GETSTATIC, "thaumcraft/client/gui/GuiResearchPage", "shownRecipe", "Lnet/minecraft/util/ResourceLocation;");
                    insn.varInsn(ALOAD, 0);
                    insn.fieldInsn(GETFIELD, "thaumcraft/client/gui/GuiResearchPage", "allowWithPagePopup", "Z");
                    insn.varInsn(ALOAD, 0);
                    insn.fieldInsn(GETFIELD, "thaumcraft/client/gui/GuiResearchPage", "hasResearch", "[Z");
                    insn.varInsn(ALOAD, 0);
                    insn.fieldInsn(GETFIELD, "thaumcraft/client/gui/GuiResearchPage", "tex1", "Lnet/minecraft/util/ResourceLocation;");
                    insn.varInsn(ALOAD, 0);
                    insn.varInsn(ALOAD, 4);

                    insn.varInsn(ILOAD, 7);
                    insn.varInsn(ILOAD, 8);
                    insn.varInsn(ILOAD, 1);
                    insn.varInsn(ILOAD, 5);
                    insn.varInsn(ILOAD, 2);
                    insn.varInsn(ILOAD, 3);
                    insn.varInsn(ILOAD, 9);

                    insn.invokeStatic(
                        ASMTransformerOldRes.HOOK_CLASS,
                        "hookRenderNotes1",
                        "(Ljava/util/Map;Ljava/util/List;Lnet/minecraft/util/ResourceLocation;Z[ZLnet/minecraft/util/ResourceLocation;Lthaumcraft/client/gui/GuiResearchPage;Lthaumcraft/api/research/ResearchStage;IIIIIII)I"
                    );

                    insn.varInsn(ISTORE, 7);

                    method.instructions.insert(node, insn.build());
                });
            });

            builder.thenVarInsn(ILOAD, 6);
            builder.then(JUMP_INSN.withOpcode(IFEQ));
            builder.thenAnyInsn();
            builder.thenInsn(FCONST_1);
            builder.thenInsn(FCONST_1);
            builder.thenInsn(FCONST_1);
            builder.thenInsn(FCONST_1);
            builder.thenMethodInsn(INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", "color", "(FFFF)V", false);
            builder.thenVarInsn(ALOAD, 0);
            builder.thenFieldInsn(GETFIELD, "thaumcraft/client/gui/GuiResearchPage", "mc", "Lnet/minecraft/client/Minecraft;");
            builder.thenFieldInsn(GETFIELD, "net/minecraft/client/Minecraft", "renderEngine", "Lnet/minecraft/client/renderer/texture/TextureManager;");
            builder.thenVarInsn(ALOAD, 0);
            builder.thenFieldInsn(GETFIELD, "thaumcraft/client/gui/GuiResearchPage", "tex1", "Lnet/minecraft/util/ResourceLocation;");
            builder.thenMethodInsn(INVOKEVIRTUAL, "net/minecraft/client/renderer/texture/TextureManager", "bindTexture", "(Lnet/minecraft/util/ResourceLocation;)V", false);

            method.instructions.findPattern(builder.build()).forEach(interval -> {
                LabelNode labelNode = new LabelNode();
                insn.varInsn(ALOAD, 4);
                insn.methodInsn(INVOKEVIRTUAL, "thaumcraft/api/research/ResearchStage", "getResearch", "()[Ljava/lang/String;", false);
                insn.jumpInsn(IFNONNULL, labelNode);

                insn.varInsn(ALOAD, 4);
                insn.methodInsn(INVOKEVIRTUAL, "thaumcraft/api/research/ResearchStage", "getObtain", "()[Ljava/lang/Object;", false);
                insn.jumpInsn(IFNONNULL, labelNode);

                insn.varInsn(ALOAD, 4);
                insn.methodInsn(INVOKEVIRTUAL, "thaumcraft/api/research/ResearchStage", "getCraft", "()[Ljava/lang/Object;", false);
                insn.jumpInsn(IFNONNULL, labelNode);

                insn.insn(ICONST_1);
                insn.varInsn(ISTORE, 6);

                insn.then(labelNode);

                method.instructions.insertBefore(interval.start(), insn.build());
            });


            builder.thenMethodInsn(INVOKEVIRTUAL, "thaumcraft/api/research/ResearchStage", "getKnow", "()[Lthaumcraft/api/research/ResearchStage$Knowledge;", false);
            method.instructions.findPattern(builder.build()).forEach(interval -> {
                method.instructions.replace(interval, insn.invokeVirtual("thaumcraft/api/research/ResearchStage", "getResearch", "()[Ljava/lang/String;", false).build());
            });

            builder.thenFieldInsn(GETFIELD, "thaumcraft/client/gui/GuiResearchPage", "hasKnow", "[Z");
            method.instructions.findPattern(builder.build()).forEach(interval -> {
                method.instructions.replace(interval, insn.fieldInsn(GETFIELD, "thaumcraft/client/gui/GuiResearchPage", "hasResearch", "[Z").build());
            });

            builder.thenFieldInsn(PUTFIELD, "thaumcraft/client/gui/GuiResearchPage", "hasKnow", "[Z");
            method.instructions.findPattern(builder.build()).forEach(interval -> {
                method.instructions.replace(interval, insn.fieldInsn(PUTFIELD, "thaumcraft/client/gui/GuiResearchPage", "hasResearch", "[Z").build());
            });
        });

        clazz.findMethod("<init>").ifPresent(method -> {
            InsnBuilder insn = new InsnBuilder();
            method.instructions.findPattern(InsnPattern.pattern(RETURN)).forEach(interval -> {
                insn.varInsn(ALOAD, 0);
                insn.varInsn(ALOAD, 0);
                insn.fieldInsn(GETFIELD, "thaumcraft/client/gui/GuiResearchPage", "knownPlayerAspects", "Lthaumcraft/api/aspects/AspectList;");
                insn.invokeStatic(ASMTransformerOldRes.HOOK_CLASS, "hookInitGuiPageData", "(Lthaumcraft/api/aspects/AspectList;)I");
                insn.fieldInsn(PUTFIELD, "thaumcraft/client/gui/GuiResearchPage", "maxAspectPages", "I");

                insn.varInsn(ALOAD, 0);
                insn.typeInsn(NEW, "java/util/HashMap");
                insn.insn(DUP);
                insn.invokeSpecial("java/util/HashMap", "<init>", "()V");
                insn.fieldInsn(PUTFIELD, "thaumcraft/client/gui/GuiResearchPage", "oldresearch$renderedNotes", "Ljava/util/Map;");

                insn.varInsn(ALOAD, 0);
                insn.typeInsn(NEW, "java/util/ArrayList");
                insn.insn(DUP);
                insn.invokeSpecial("java/util/ArrayList", "<init>", "()V");
                insn.fieldInsn(PUTFIELD, "thaumcraft/client/gui/GuiResearchPage", "tipText", "Ljava/util/List;");

                logger.info("Injecting patches into method {}.{}{}", className, method.name, method.desc);
                method.instructions.insertBefore(interval.start(), insn.build());
            });
        });

        clazz.findMethod("drawPage").ifPresent(method -> {
            InsnPatBuilder builder = InsnPattern.builder();
            InsnBuilder insn = new InsnBuilder();
            builder.thenMethodInsn(INVOKEINTERFACE, "thaumcraft/api/capabilities/IPlayerKnowledge", "isResearchComplete", "(Ljava/lang/String;)Z", true);
            method.instructions.findPattern(builder.build()).forEach(interval -> {
                logger.info("Injecting patches into method {}.{}{}", className, method.name, method.desc);
                method.instructions.replace(interval, insn.invokeStatic(ASMTransformerOldRes.HOOK_CLASS, "hookIsResearchComplete", "(Lthaumcraft/api/capabilities/IPlayerKnowledge;Ljava/lang/String;)Z").build());
            });
        });

        clazz.findMethod("parsePages").ifPresent(method-> {
            InsnPatBuilder builder = InsnPattern.builder();
            InsnBuilder insn = new InsnBuilder();
            builder.thenMethodInsn(INVOKEVIRTUAL, "thaumcraft/api/research/ResearchStage", "getKnow", "()[Lthaumcraft/api/research/ResearchStage$Knowledge;", false);
            method.instructions.findPattern(builder.build()).forEach(interval -> {
                logger.info("Injecting patches into method {}.{}{}", className, method.name, method.desc);
                method.instructions.replace(interval, insn.invokeStatic(ASMTransformerOldRes.HOOK_CLASS, "hookPagesGetKnow", "(Lthaumcraft/api/research/ResearchStage;)[Lthaumcraft/api/research/ResearchStage$Knowledge;").build());
            });
        });

        clazz.findMethod("mouseClicked").ifPresent(method -> {
            logger.info("Injecting patches into method {}.{}{}", className, method.name, method.desc);
            InsnBuilder insn = new InsnBuilder();
            insn.varInsn(ALOAD, 0);
            insn.fieldInsn(GETFIELD, "thaumcraft/client/gui/GuiResearchPage", "oldresearch$renderedNotes", "Ljava/util/Map;");
            insn.loadVars("I:1", "I:2");
            insn.invokeStatic(ASMTransformerOldRes.HOOK_CLASS, "hookMouseClicked", "(Ljava/util/Map;II)Z");
            insn.ifTrueReturn(RETURN);
            method.instructions.insert(insn.build());
        });
        
        clazz.findMethod("drawAspectPage").ifPresent(method -> {
            InsnPatBuilder builder = InsnPattern.builder();
            InsnBuilder insn = new InsnBuilder();

            builder.thenVarInsn(ALOAD, 0);
            builder.thenFieldInsn(GETFIELD, "thaumcraft/client/gui/GuiResearchPage", "playerKnowledge", "Lthaumcraft/api/capabilities/IPlayerKnowledge;");
            builder.thenTypeInsn(NEW, "java/lang/StringBuilder");
            builder.thenInsn(DUP);
            builder.thenMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            builder.thenLdcInsn("!");
            builder.thenMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            builder.thenVarInsn(ALOAD, 12);
            builder.thenMethodInsn(INVOKEVIRTUAL, "thaumcraft/api/aspects/Aspect", "getComponents", "()[Lthaumcraft/api/aspects/Aspect;", false);
            builder.thenInsn(ICONST_1);
            builder.thenInsn(AALOAD);
            builder.thenMethodInsn(INVOKEVIRTUAL, "thaumcraft/api/aspects/Aspect", "getTag", "()Ljava/lang/String;", false);
            builder.thenMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toLowerCase", "()Ljava/lang/String;", false);
            builder.thenMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            builder.thenMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            builder.thenMethodInsn(INVOKEINTERFACE, "thaumcraft/api/capabilities/IPlayerKnowledge", "isResearchKnown", "(Ljava/lang/String;)Z", true);

            method.instructions.findPattern(builder.build()).forEach(interval -> {
                insn.varInsn(ALOAD, 12);
                insn.insn(ICONST_1);
                insn.invokeStatic(ASMTransformerOldRes.HOOK_CLASS, "hookInKnowAspect", "(Lthaumcraft/api/aspects/Aspect;I)Z");
                method.instructions.replace(interval, insn.build());
            });

            builder.thenVarInsn(ALOAD, 0);
            builder.thenFieldInsn(GETFIELD, "thaumcraft/client/gui/GuiResearchPage", "playerKnowledge", "Lthaumcraft/api/capabilities/IPlayerKnowledge;");
            builder.thenTypeInsn(NEW, "java/lang/StringBuilder");
            builder.thenInsn(DUP);
            builder.thenMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            builder.thenLdcInsn("!");
            builder.thenMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            builder.thenVarInsn(ALOAD, 12);
            builder.thenMethodInsn(INVOKEVIRTUAL, "thaumcraft/api/aspects/Aspect", "getComponents", "()[Lthaumcraft/api/aspects/Aspect;", false);
            builder.thenInsn(ICONST_0);
            builder.thenInsn(AALOAD);
            builder.thenMethodInsn(INVOKEVIRTUAL, "thaumcraft/api/aspects/Aspect", "getTag", "()Ljava/lang/String;", false);
            builder.thenMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toLowerCase", "()Ljava/lang/String;", false);
            builder.thenMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            builder.thenMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            builder.thenMethodInsn(INVOKEINTERFACE, "thaumcraft/api/capabilities/IPlayerKnowledge", "isResearchKnown", "(Ljava/lang/String;)Z", true);

            method.instructions.findPattern(builder.build()).forEach(interval -> {
                insn.varInsn(ALOAD, 12);
                insn.insn(ICONST_0);
                insn.invokeStatic(ASMTransformerOldRes.HOOK_CLASS, "hookInKnowAspect", "(Lthaumcraft/api/aspects/Aspect;I)Z");
                method.instructions.replace(interval, insn.build());
            });
        });

        return clazz;
    }


    @Override
    public int priority() {
        return 0;
    }
}
