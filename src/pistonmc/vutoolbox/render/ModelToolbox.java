package pistonmc.vutoolbox.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import pistonmc.vutoolbox.ModInfo;
import pistonmc.vutoolbox.ModObjects;
import pistonmc.vutoolbox.core.Upgrades;
import pistonmc.vutoolbox.object.TileToolbox;
import pistonmc.vutoolbox.render.ModelAbstract.ItemRenderer;
import pistonmc.vutoolbox.render.Toolbox2SpecialRenderRegistry.ToolBoxRenderType;

public class ModelToolbox extends ModelAbstract {
	public static int rendererId = 0;
	private ResourceLocation textureResis;

	public ModelToolbox() {
		super("textures/entity/toolbox.png", 64, 64);
		textureResis = new ResourceLocation(ModInfo.ID, "textures/entity/toolboxResis.png");
		ModelRenderer r = newRenderer(0, 0);
		r.addBox(1, 2, 1, 14, 14, 14);
		r.addBox(7, 4, 15, 2, 4, 1);
	}

	public void register() {
		rendererId = RenderingRegistry.getNextAvailableRenderId();
		ItemRenderer r = new ItemRenderer(new TileToolbox());
		RenderingRegistry.registerBlockHandler(r);
		ClientRegistry.bindTileEntitySpecialRenderer(TileToolbox.class, new TileRenderer(this));
	}

	@Override
	public ResourceLocation getTexture(TileEntity tile) {
		TileToolbox tileToolbox = TileToolbox.cast(tile);
		if (tileToolbox != null && tileToolbox.getToolbox().getUpgrades().isEnabled(Upgrades.RESIS)) {
			return textureResis;
		} else {
			return super.getTexture(tile);
		}
	}

	@Override
	public void render(TileEntity tile) {
		float rotate = 0;
		if (!tile.hasWorldObj()) {
			rotate = 90;
		} else {
			switch (tile.getBlockMetadata()) {
			case 2:
				rotate = 0;
				break;
			case 3:
				rotate = 180;
				break;
			case 4:
				rotate = -90;
				break;
			default:
				rotate = 90;
				break;
			}
		}

		GL11.glPushMatrix();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		GL11.glRotatef(rotate, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		super.render(tile);
		if (tile.hasWorldObj()) {
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					GL11.glPushMatrix();
					RenderHelper.enableStandardItemLighting();
					int i = y * 3 + x;
					TileToolbox box = (TileToolbox) tile;
					ItemStack stack = box.getStackInSlot(i);
					renderStack(stack, x, y);
					GL11.glPopMatrix();
				}
			}
		}
		GL11.glRotatef(-rotate, 0.0F, 1.0F, 0.0F);
		GL11.glPopMatrix();
	}

	private void renderStack(ItemStack stack, int x, int y) {
		if (stack != null) {
			TextureManager tm = Minecraft.getMinecraft().getTextureManager();
			ToolBoxRenderType type = Toolbox2SpecialRenderRegistry.getType(stack);
			boolean special = false;
			float above = 0.01f;
			if (type != ToolBoxRenderType.TEXTURE) {
				GL11.glPushMatrix();
				GL11.glTranslatef(1, 0, 0);
				GL11.glScalef(0.0625f, 1, 0.0625f);
				GL11.glRotatef(-90, 1, 0, 0);
				GL11.glRotatef(180, 0, 0, 1);
				GL11.glTranslatef(2 + x * 4, 2 + y * 4, 0.125f - above);
				GL11.glScalef(0.25f, 0.25f, 1);

				special = ForgeHooksClient.renderInventoryItem(RenderBlocks.getInstance(), tm, stack, true, 3, 0, 0);
				GL11.glPopMatrix();
			}
			if (type != ToolBoxRenderType.RENDERER) {
				if (!special) {
					if (stack.getItem().getSpriteNumber() != 1) {
						tm.bindTexture(TextureMap.locationBlocksTexture);
					} else {
						tm.bindTexture(TextureMap.locationItemsTexture);
					}
					IIcon icon = stack.getIconIndex();
					float maxX = 0.875f - x * 0.25f;
					float minZ = 0.125f + y * 0.25f;
					float minX = maxX - 0.25f;
					float maxZ = minZ + 0.25f;
					if (icon != null) {

						Tessellator tessellator = Tessellator.instance;
						tessellator.startDrawingQuads();
						float renderY = 0.125f - above;
						tessellator.addVertexWithUV(minX, renderY, minZ, icon.getMaxU(), icon.getMinV());
						tessellator.addVertexWithUV(maxX, renderY, minZ, icon.getMinU(), icon.getMinV());
						tessellator.addVertexWithUV(maxX, renderY, maxZ, icon.getMinU(), icon.getMaxV());
						tessellator.addVertexWithUV(minX, renderY, maxZ, icon.getMaxU(), icon.getMaxV());
						tessellator.draw();
					}
				}
			}

		}

	}

	@Override
	public TileEntity updateTileEntityForItemRendering(TileEntity tile, Block block) {
		TileToolbox tileToolbox = TileToolbox.cast(tile);
		if (tileToolbox != null) {
			tileToolbox.getToolbox().getUpgrades().set(Upgrades.RESIS, block == ModObjects.blockToolboxResis);
		}
		tile.setWorldObj(null);
		return tile;
	}

}
