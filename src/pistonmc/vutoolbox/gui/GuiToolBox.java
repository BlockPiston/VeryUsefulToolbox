package pistonmc.vutoolbox.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import pistonmc.vutoolbox.ModInfo;
import pistonmc.vutoolbox.ModNetwork;
import pistonmc.vutoolbox.ModObjects;
import pistonmc.vutoolbox.core.BigItemStack;
import pistonmc.vutoolbox.core.Toolbox;
import pistonmc.vutoolbox.core.Upgrades;
import pistonmc.vutoolbox.event.MessageWorkbenchOpen;
import pistonmc.vutoolbox.object.TileToolbox;

public class GuiToolBox extends GuiContainer {
	private static final int TOOLBOX_SLOT_X = 11;
	private static final int TOOLBOX_SLOT_Y = 21;
	private static final int WORKBENCH_SLOT_X = 195;
	private static final int WORKBENCH_SLOT_Y = 21;
	private static final int INFINITY_SLOT_X = 177;
	private static final int INFINITY_SLOT_Y = 117;
	private IInventory playerInventory;
	private TileToolbox tile;

	public GuiToolBox(TileToolbox tile, IInventory player) {
		super(new ContainerToolbox(tile, player));
		this.tile = tile;
		playerInventory = player;
		xSize = 222;
		ySize = 214;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		super.drawScreen(mouseX, mouseY, p_73863_3_);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		RenderHelper.disableStandardItemLighting();
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0.0F);
		
		this.drawTooltipLayer(mouseX, mouseY);
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		RenderHelper.enableStandardItemLighting();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mx, int my) {
		// player inventory name
		String playerInventoryname = this.playerInventory.getInventoryName();
		if (!this.playerInventory.hasCustomInventoryName()) {
			// translate the name if it's not custom
			playerInventoryname = I18n.format(playerInventoryname, new Object[0]);
		}
		this.fontRendererObj.drawString(
				playerInventoryname,
				8, this.ySize - 96 + 2, 4210752);
		
		// toolbox inventory name
		String toolboxInventoryName = this.tile.getInventoryName();
		if (!this.tile.hasCustomInventoryName()) {
			toolboxInventoryName = I18n.format(toolboxInventoryName, new Object[0]);
		}
		this.fontRendererObj.drawString(toolboxInventoryName, 8, 6, 4210752);
		
		super.drawGuiContainerForegroundLayer(mx, my);

		// tool box
		{
			ItemStack stack = new ItemStack(ModObjects.blockToolBox);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			RenderHelper.enableGUIStandardItemLighting();
			itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), stack, TOOLBOX_SLOT_X, TOOLBOX_SLOT_Y);
			itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), stack, TOOLBOX_SLOT_X, TOOLBOX_SLOT_Y, null);
			RenderHelper.disableStandardItemLighting();
			if (isOverToolboxInfoSlot(mx, my)) {
				this.drawItemHighlight(TOOLBOX_SLOT_X, TOOLBOX_SLOT_Y);
			}
		}
		
		int infLimit = tile.getToolbox().getUpgrades().getInfinityStackLimit();
		for (int i = 0; i < Toolbox.NUM_INFINITY_SLOTS; i++) {
			BigItemStack infStack = tile.getToolbox().getInfinityStack(i);
			ItemStack infItem = infStack.getItemStack();
			if (infItem == null) {
				continue;
			}
			
			int x = INFINITY_SLOT_X + i * 18;
			int y = INFINITY_SLOT_Y;
			int count = infStack.getCount();
			
			String str = count >= infLimit ? "MAX" : count * 100 / infLimit + "%";
			RenderHelper.enableGUIStandardItemLighting();
			itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), infItem, x,
					y);
			itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), infItem, x, y,
					str);
			RenderHelper.disableStandardItemLighting();
		}

		// crafting table
		if (tile.getToolbox().getUpgrades().isEnabled(Upgrades.CRAFT)) {
			ItemStack stack = new ItemStack(Blocks.crafting_table);
			RenderHelper.enableGUIStandardItemLighting();
			itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), stack, WORKBENCH_SLOT_X, WORKBENCH_SLOT_Y);
			itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), stack, WORKBENCH_SLOT_X, WORKBENCH_SLOT_Y, null);
			RenderHelper.disableStandardItemLighting();
			if (isOverWorkbenchSlot(mx, my)) {
				this.drawItemHighlight(WORKBENCH_SLOT_X, WORKBENCH_SLOT_Y);
			}
		}
		
		for (int i = 0; i < Toolbox.NUM_INFINITY_SLOTS; i++) {
			int x = INFINITY_SLOT_X + i * 18;
			int y = INFINITY_SLOT_Y;
			if (GuiUtil.isMouseOverSlot(x, y, mx, my, guiLeft, guiTop)) {
				this.drawItemHighlight(x, y);
				break;
			}
		}

		RenderHelper.enableGUIStandardItemLighting();

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int mx, int my) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(new ResourceLocation(ModInfo.ID, "textures/gui/guiToolbox.png"));
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
	
	private void drawTooltipLayer(int mx, int my) {
		if (isOverToolboxInfoSlot(mx, my)) {
			ArrayList<String> tooltipList = new ArrayList<>();
			for (int i = 0;; i++) {
				String line = StatCollector.translateToLocal(ModInfo.ID + ".gui.toolbox.line" + i);
				if (line.length() == 0)
					break;
				tooltipList.add(line);

			}
			drawHoveringText(tooltipList, mx - guiLeft, my - guiTop, fontRendererObj);
			return;
		}
		if (isOverWorkbenchSlot(mx, my)) {
			ArrayList<String> tooltipList = new ArrayList<>();
			tooltipList.add(StatCollector.translateToLocal(ModInfo.ID + ".gui.toolbox.craft"));
			drawHoveringText(tooltipList, mx - guiLeft, my - guiTop, fontRendererObj);
			return;
		}
		
		for (int i = 0; i < Toolbox.NUM_INFINITY_SLOTS; i++) {
			int x = INFINITY_SLOT_X + i * 18;
			int y = INFINITY_SLOT_Y;
			if (!GuiUtil.isMouseOverSlot(x, y, mx, my, guiLeft, guiTop)) {
				continue;
			}
			BigItemStack infStack = tile.getToolbox().getInfinityStack(i);
			ItemStack infItem = infStack.getItemStack();
			if (infItem == null) {
				continue;
			}
			
			@SuppressWarnings("unchecked")
			List<String> list = infItem.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
			ArrayList<String> tooltipList = new ArrayList<>();
			for (int k = 0; k < list.size(); ++k) {
				if (k == 0) {
					tooltipList.add(infItem.getRarity().rarityColor + list.get(k));
				} else {
					tooltipList.add(EnumChatFormatting.GRAY + list.get(k));
				}
			}
			tooltipList.add("");
			int infLimit = tile.getToolbox().getUpgrades().getInfinityStackLimit();
			tooltipList.add(EnumChatFormatting.YELLOW + StatCollector
						.translateToLocalFormatted(ModInfo.ID + ".gui.toolbox.amount", infStack.getCount(), infLimit));
			FontRenderer fontRenderer = infItem.getItem().getFontRenderer(infItem);
			if (fontRenderer == null) {
				fontRenderer = fontRendererObj;
			}
			drawHoveringText(tooltipList, mx - guiLeft, my - guiTop, fontRenderer);
			
		}
	}
	
	private void drawItemHighlight(int x, int y) {
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColorMask(true, true, true, false);
		
		this.drawGradientRect(x, y, x + 16, y + 16, -2130706433, -2130706433);
//		GL11.glColorMask(true, true, true, true);
//		GL11.glEnable(GL11.GL_LIGHTING);
//		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopAttrib();
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		if (isOverWorkbenchSlot(x, y)) {
			if (tile.getToolbox().getUpgrades().isEnabled(Upgrades.CRAFT)) {
				ModNetwork.network.sendToServer(new MessageWorkbenchOpen(tile.xCoord, tile.yCoord, tile.zCoord));
			}
		}
	}
	
	private boolean isOverToolboxInfoSlot(int mx, int my) {
		return GuiUtil.isMouseOverSlot(TOOLBOX_SLOT_X, TOOLBOX_SLOT_Y, mx, my, guiLeft, guiTop);
	}
	
	private boolean isOverWorkbenchSlot(int mx, int my) {
		return GuiUtil.isMouseOverSlot(WORKBENCH_SLOT_X, WORKBENCH_SLOT_Y, mx, my, guiLeft, guiTop);
	}

}
