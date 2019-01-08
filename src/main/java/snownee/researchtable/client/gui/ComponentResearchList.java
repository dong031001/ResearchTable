package snownee.researchtable.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import snownee.kiwi.client.AdvancedFontRenderer;
import snownee.kiwi.client.gui.GuiControl;
import snownee.kiwi.util.Util;
import snownee.researchtable.ModConfig;
import snownee.researchtable.core.DataStorage;
import snownee.researchtable.core.Research;
import snownee.researchtable.core.ResearchCategory;
import snownee.researchtable.core.ResearchList;

public class ComponentResearchList extends GuiList
{
    final List<Research> researches = new ArrayList<>();
    ResearchCategory category;

    private final int slotHeight;

    public ComponentResearchList(GuiControl parent, int width, int height, int left, int top, int entryHeight, int screenWidth, int screenHeight)
    {
        super(parent, width, height, left, top, screenWidth, screenHeight);
        this.slotHeight = entryHeight;
    }

    public void setCategory(ResearchCategory category)
    {
        this.category = category;
        researches.clear();

        List<Research> researchesAvailable = new ArrayList<>();
        List<Research> researchesUnavailable = new ArrayList<>();
        List<Research> researchesCompleted = new ArrayList<>();

        for (Research research : ResearchList.LIST.stream().filter(e -> e.getCategory().equals(category)).collect(Collectors.toList()))
        {
            if (research.canResearch(parent.mc.player))
            {
                researchesAvailable.add(research);
            }
            else if (!ModConfig.hide)
            {
                if (DataStorage.count(parent.mc.player.getName(), research) >= research.getMaxCount())
                {
                    researchesCompleted.add(research);
                }
                else
                {
                    researchesUnavailable.add(research);
                }
            }
        }
        researches.addAll(researchesAvailable);
        researches.addAll(researchesUnavailable);
        researches.addAll(researchesCompleted);
    }

    @Override
    protected int getSize()
    {
        return researches.size();
    }

    @Override
    protected void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
    {
        sendMessage(index, 0);
    }

    @Override
    protected void drawBackground()
    {
    }

    @Override
    protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess)
    {
        boolean hovering = slotIdx == hoveringIndex;
        Gui.drawRect(left, slotTop, left + width, slotTop + slotHeight - 1, hovering ? 0xFFDDDDDD : 0xFFEEEEEE);
        Research research = researches.get(slotIdx);
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        GlStateManager.disableLighting();
        RenderHelper.enableGUIStandardItemLighting();
        renderItem.renderItemAndEffectIntoGUI(research.getIcon(), offsetX + 6, offsetY + slotTop + 1);
        renderItem.renderItemOverlayIntoGUI(parent.mc.fontRenderer, research.getIcon(), offsetX + 6, offsetY + slotTop + 1, null);
        String title = research.getTitle();
        if (I18n.hasKey(title))
        {
            title = I18n.format(title);
        }
        if (!research.canResearch(Minecraft.getMinecraft().player))
        {
            title = Util.color(0x808080) + title;
        }
        AdvancedFontRenderer.INSTANCE.drawString(title, offsetX + 32, offsetY + slotTop + 6, 0x000000);
    }

    @Override
    protected void drawGradientRect(int left, int top, int right, int bottom, int color1, int color2)
    {
        super.drawGradientRect(left, top, right, bottom, color1, color2);
    }

    @Override
    protected int getSlotHeight(int index)
    {
        return slotHeight;
    }

}
