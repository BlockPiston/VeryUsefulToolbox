package pistonmc.vutoolbox.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;

public class GuiUtil {
	public static boolean isMouseOverSlot(int slotX, int slotY, int mx, int my, int guiLeft, int guiTop) {
		mx -= guiLeft;
		my -= guiTop;
		return mx >= slotX && mx <= slotX + 16 && my >= slotY && my <= slotY + 16;
	}

	public static void drawHighlightTile(int x, int y, float z) {
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColorMask(true, true, true, false);
		drawGradientRect(x, y, x + 16, y + 16, -2130706433, -2130706433, z);
		//GL11.glColorMask(true, true, true, true);
		//GL11.glEnable(GL11.GL_LIGHTING);
		//GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopAttrib();
	}

	/**
	 * Draws a rectangle with a vertical gradient between the specified colors.
	 */
	public static void drawGradientRect(int p_73733_1_, int p_73733_2_, int p_73733_3_, int p_73733_4_, int p_73733_5_,
			int p_73733_6_, float zLevel) {
		float f = (p_73733_5_ >> 24 & 255) / 255.0F;
		float f1 = (p_73733_5_ >> 16 & 255) / 255.0F;
		float f2 = (p_73733_5_ >> 8 & 255) / 255.0F;
		float f3 = (p_73733_5_ & 255) / 255.0F;
		float f4 = (p_73733_6_ >> 24 & 255) / 255.0F;
		float f5 = (p_73733_6_ >> 16 & 255) / 255.0F;
		float f6 = (p_73733_6_ >> 8 & 255) / 255.0F;
		float f7 = (p_73733_6_ & 255) / 255.0F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(f1, f2, f3, f);
		tessellator.addVertex(p_73733_3_, p_73733_2_, zLevel);
		tessellator.addVertex(p_73733_1_, p_73733_2_, zLevel);
		tessellator.setColorRGBA_F(f5, f6, f7, f4);
		tessellator.addVertex(p_73733_1_, p_73733_4_, zLevel);
		tessellator.addVertex(p_73733_3_, p_73733_4_, zLevel);
		tessellator.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
}
