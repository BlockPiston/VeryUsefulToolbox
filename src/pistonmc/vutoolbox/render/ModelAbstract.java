package pistonmc.vutoolbox.render;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import pistonmc.vutoolbox.object.BlockToolbox;
import pistonmc.vutoolbox.object.TileToolbox;

/**
 * Ideally we don't need this. However when migrating from an older mod,
 * I am too lazy to correctly get rid of this
 */
public abstract class ModelAbstract extends ModelBase {
	private ArrayList<ModelRenderer> rendererList = new ArrayList<ModelRenderer>();
	private ResourceLocation texture;
	private TileRenderer tesr;
	private int texWidth;
	private int texHeight;

	public ModelAbstract(String textureLocation, int textureWidth, int textureHeight) {
		texture = new ResourceLocation("tntptool", textureLocation);
		tesr = new TileRenderer(this);
		texWidth = textureWidth;
		texHeight = textureHeight;
	}

	public ModelRenderer newRenderer(int offX, int offY) {
		ModelRenderer r = new ModelRenderer(this, offX, offY).setTextureSize(texWidth, texHeight);
		rendererList.add(r);
		return r;
	}

	public void render(TileEntity tile) {
		for (ModelRenderer r : rendererList) {
			r.render(0.0625f);
		}
	}

	public ResourceLocation getTexture(TileEntity tile) {
		return texture;
	}

	public TileEntitySpecialRenderer getSpecialRenderer() {
		return tesr;
	}

	public ModelAbstract updateModel(TileEntity tile) {
		return this;
	}

	public TileEntity updateTileEntityForItemRendering(TileEntity tile, Block block) {
		return tile;
	}

	public void bind(TileToolbox tileItem, BlockToolbox... block) {
		ItemRenderer r = new ItemRenderer(tileItem);
		RenderingRegistry.registerBlockHandler(r);
		ClientRegistry.bindTileEntitySpecialRenderer(TileToolbox.class, getSpecialRenderer());
		for (BlockToolbox b : block)
			b.modelRenderID = r.getRenderId();
	}

	private static class TileRenderer extends TileEntitySpecialRenderer {
		private ModelAbstract model;

		public TileRenderer(ModelAbstract modelAbstract) {
			model = modelAbstract;
		}

		@Override
		public void renderTileEntityAt(TileEntity tile, double p_147500_2_, double p_147500_4_, double p_147500_6_,
				float p_147500_8_) {
			if (model == null)
				return;
			model = model.updateModel(tile);
			this.bindTexture(model.getTexture(tile));
			GL11.glPushMatrix();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef((float) p_147500_2_, (float) p_147500_4_ + 1.0F, (float) p_147500_6_ + 1.0F);
			GL11.glScalef(1.0F, -1.0F, -1.0F);
			model.render(tile);
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			if (tile.hasWorldObj())
				RenderHelper.enableStandardItemLighting();
		}

	}

	private class ItemRenderer implements ISimpleBlockRenderingHandler {
		private int id;
		private TileEntity tile;

		public ItemRenderer(TileEntity t) {
			tile = t;
			id = RenderingRegistry.getNextAvailableRenderId();
		}

		@Override
		public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			tile = updateTileEntityForItemRendering(tile, block);
			TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, 0.0D, 0.0D, 0.0D, 0.0F);
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}

		@Override
		public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
				RenderBlocks renderer) {
			return false;
		}

		@Override
		public boolean shouldRender3DInInventory(int modelId) {
			return true;
		}

		@Override
		public int getRenderId() {
			return id;
		}
	}
}
