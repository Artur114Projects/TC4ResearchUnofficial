package com.wonginnovations.oldresearch.asm;

import com.artur114.bananalib.mc.BananaMC;
import com.wonginnovations.oldresearch.api.OldResearchApi;
import com.wonginnovations.oldresearch.common.init.InitBlocks;
import com.wonginnovations.oldresearch.common.items.ItemResearchNote;
import com.wonginnovations.oldresearch.common.network.PacketGivePlayerNoteToServer;
import com.wonginnovations.oldresearch.common.research.OldResearchManager;
import com.wonginnovations.oldresearch.common.research.storage.IOldResStorage;
import com.wonginnovations.oldresearch.main.OldResearch;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.client.gui.GuiResearchPage;
import thaumcraft.common.lib.utils.InventoryUtils;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

public class ASMHookOldRes {
    public static Block hookNewResearchTable() {
        return InitBlocks.RESEARCH_TABLE;
    }

    public static boolean hookStageContainsOnlyNotes(ResearchStage stage) {
        return Arrays.stream(stage.getResearch()).allMatch(re -> re.startsWith("rn_"));
    }

    public static boolean hookStageContainsNote(ResearchStage stage) {
        return Arrays.stream(stage.getResearch()).anyMatch(re -> re.startsWith("rn_"));
    }

    public static boolean hookInKnowAspect(Aspect aspect, int id) {
        return OldResearchApi.oldResStorage(Minecraft.getMinecraft().player).isKnowAspect(aspect.getComponents()[id]);
    }

    public static int hookInitGuiPageData(AspectList knownPlayerAspects) {
        knownPlayerAspects.aspects.clear();

        for (Aspect a : OldResearchApi.oldResStorage(Minecraft.getMinecraft().player).aspectsPool().getAspects()) {
            knownPlayerAspects.add(a, OldResearchManager.getAspectComplexity(a));
        }

        return MathHelper.ceil((float)knownPlayerAspects.size() / 5.0F);
    }

    public static boolean hookMouseClicked(Map<Point, ItemStack> notePoints, int mx, int my) {
        for (Point p : notePoints.keySet()) {
            if ((mx >= p.x && mx <= p.x + 16) && (my >= p.y && my <= p.y + 16)) {
                OldResearch.NETWORK.sendToServer(new PacketGivePlayerNoteToServer(Objects.requireNonNull(notePoints.get(p).getTagCompound()).getString("key")));
                return true;
            }
        }
        return false;
    }

    public static ResearchStage.Knowledge[] hookPagesGetKnow(ResearchStage instance) {
        if (instance.getCraft() == null && instance.getObtain() == null && instance.getResearch() == null) {
            return new ResearchStage.Knowledge[0];
        }
        return null;
    }

    public static boolean hookIsResearchComplete(IPlayerKnowledge instance, String s) {
        return !"KNOWLEDGETYPES".equals(s) && instance.isResearchComplete(s);
    }

    public static int hookRenderNotes1(Map<Point, ItemStack> notePoints, List tipText, ResourceLocation shownRecipe, boolean allowWithPagePopup, boolean[] hasResearch, ResourceLocation tex1, GuiResearchPage self, ResearchStage stage, int shift, int ss, int x, int y, int mx, int my, int i) {
        String key = stage.getResearch()[i];

        if (!key.startsWith("rn_")) {
            return shift;
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack loc = OldResearchManager.noteStack(key);
        String s = I18n.format("tc.researchtheory", I18n.format("research." + OldResearchManager.getStrippedKey(key) + ".title"));

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);

        if (loc != null) {
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            notePoints.put(new Point(x - 15 + shift, y), loc);
            mc.getRenderItem().renderItemAndEffectIntoGUI(InventoryUtils.cycleItemStack(loc), x - 15 + shift, y);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
        }

        GlStateManager.popMatrix();
        if (hasResearch[i]) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.bindTexture(tex1);
            GlStateManager.disableDepth();
            self.drawTexturedModalRect(x - 15 + shift + 8, y, 159, 207, 10, 10);
            GlStateManager.enableDepth();
        }
        oldresearch$drawPopupAt(self, shownRecipe, allowWithPagePopup, x - 15 + shift, y, mx, my, s, I18n.format("researchnote.click"));
        return shift + ss;
    }

    private static void oldresearch$drawPopupAt(GuiResearchPage self, ResourceLocation shownRecipe, boolean allowWithPagePopup, int x, int y, int mx, int my, String... text) {
        if ((shownRecipe == null || allowWithPagePopup) && mx >= x && my >= y && mx < x + 16 && my < y + 16) {
            List<String> tipText = new ArrayList<>();
            for (String t : text) {
                tipText.add(I18n.format(t));
            }
            try {
                Field field = GuiResearchPage.class.getDeclaredField("tipText");
                field.setAccessible(true);
                field.set(self, tipText);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
