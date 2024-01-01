package com.tntp.tntptool.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.tntp.tntptool.RS2Blocks;
import com.tntp.tntptool.container.ContainerToolBox;
import com.tntp.tntptool.network.MessageWorkbench;
import com.tntp.tntptool.network.RS2Network;
import com.tntp.tntptool.tileentity.TileToolBox;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiToolBox extends GuiContainerEx {
	public static int guiID;
	private static final ResourceLocation background = new ResourceLocation("tntptool", "textures/gui/guiToolBox.png");
	private IInventory playerInventory;
	private TileToolbox box;
	private ArrayList<String> tooltipList;
	private int tooltipX;
	private int tooltipY;
	private FontRenderer tooltipFontRendererObj;

	public GuiToolBox(TileToolbox tile, IInventory player) {
		super(new ContainerToolBox(tile, player));
		box = tile;
		playerInventory = player;
		xSize = 222;
		ySize = 214;
		tooltipList = new ArrayList<String>();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mx, int my) {
		this.fontRendererObj.drawString(
				this.playerInventory.hasCustomInventoryName() ? this.playerInventory.getInventoryName()
						: I18n.format(this.playerInventory.getInventoryName(), new Object[0]),
				8, this.ySize - 96 + 2, 4210752);
		this.fontRendererObj.drawString(this.box.hasCustomInventoryName() ? this.box.getInventoryName()
				: I18n.format(this.box.getInventoryName(), new Object[0]), 8, 6, 4210752);
		super.drawGuiContainerForegroundLayer(mx, my);
		tooltipList.clear();
		tooltipFontRendererObj = null;
		int x = 11;
		int y = 21;

		// tool box
		ItemStack stack = new ItemStack(RS2Blocks.blockToolBox);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		RenderHelper.enableGUIStandardItemLighting();
		itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), stack, x, y);
		itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), stack, x, y, null);
		RenderHelper.disableStandardItemLighting();
		if (GuiUtil.isMouseOverSlot(x, y, mx, my, guiLeft, guiTop)) {
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glColorMask(true, true, true, false);
			this.drawGradientRect(x, y, x + 16, y + 16, -2130706433, -2130706433);
			GL11.glColorMask(true, true, true, true);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);

			for (int i = 0;; i++) {
				String line = StatCollector.translateToLocal("tntptool.gui.toolbox.line" + i);
				if (line.length() == 0)
					break;
				tooltipList.add(line);

			}
		}
		int infLimit = box.getInfLimit();
		for (int i = 0; i < 2; i++) {

			stack = box.getInfStorage(i);
			if (stack != null) {
				x = 177 + i * 18;
				y = 117;
				int c = box.getInfCount(i);
				if (c > 0) {
					String str = c >= infLimit ? "MAX" : c * 100 / infLimit + "%";
					RenderHelper.enableGUIStandardItemLighting();
					itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), stack, x,
							y);
					itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), stack, x, y,
							str);
					RenderHelper.disableStandardItemLighting();
				}
			}
		}

		// crafting table
		if (box.hasCraftUpgrade()) {
			stack = new ItemStack(Blocks.crafting_table);
			x = 195;
			y = 21;
			RenderHelper.enableGUIStandardItemLighting();
			itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), stack, x, y);
			itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), stack, x, y, null);
			RenderHelper.disableStandardItemLighting();
			if (GuiUtil.isMouseOverSlot(x, y, mx, my, guiLeft, guiTop)) {
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glColorMask(true, true, true, false);
				this.drawGradientRect(x, y, x + 16, y + 16, -2130706433, -2130706433);
				GL11.glColorMask(true, true, true, true);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);

				tooltipList.add(StatCollector.translateToLocal("tntptool.gui.toolbox.craft"));
			}
		}
		for (int i = 0; i < 2; i++) {
			x = 177 + i * 18;
			y = 117;
			stack = box.getInfStorage(i);
			int c = box.getInfCount(i);
			if (GuiUtil.isMouseOverSlot(x, y, mx, my, guiLeft, guiTop)) {
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glColorMask(true, true, true, false);
				this.drawGradientRect(x, y, x + 16, y + 16, -2130706433, -2130706433);
				GL11.glColorMask(true, true, true, true);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				if (stack != null && c > 0) {
					List<String> list = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

					for (int k = 0; k < list.size(); ++k) {
						if (k == 0) {
							tooltipList.add(stack.getRarity().rarityColor + list.get(k));
						} else {
							tooltipList.add(EnumChatFormatting.GRAY + list.get(k));
						}
					}
					tooltipList.add("");
					tooltipList.add(EnumChatFormatting.YELLOW + StatCollector
							.translateToLocalFormatted("tntptool.gui.toolbox.amount", c, box.getInfLimit()));
					tooltipFontRendererObj = stack.getItem().getFontRenderer(stack);
				}
			}
		}
		tooltipX = mx - guiLeft;
		tooltipY = my - guiTop;

		RenderHelper.enableGUIStandardItemLighting();

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int mx, int my) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(background);
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	@Deprecated
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		if (GuiUtil.isMouseOverSlot(195, 21, x, y, guiLeft, guiTop)) {
			if (box.hasCraftUpgrade()) {
				RS2Network.network.sendToServer(new MessageWorkbench(box.xCoord, box.yCoord, box.zCoord));
			}
		}
	}

	@Override
	protected void drawGuiContainerTopLayer(int mx, int my) {
		if (!tooltipList.isEmpty())
			drawHoveringText(tooltipList, tooltipX, tooltipY,
					tooltipFontRendererObj == null ? fontRendererObj : tooltipFontRendererObj);
	}
}
