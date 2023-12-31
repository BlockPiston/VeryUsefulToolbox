package com.tntp.tntptool.item;

import java.util.List;

import com.tntp.tntptool.PistonToolbox;
import com.tntp.tntptool.RS2Blocks;
import com.tntp.tntptool.tileentity.TileToolBox;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class ItemBlockTooltip extends ItemBlock {

	public ItemBlockTooltip(Block block) {
		super(block);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean extra) {
		Block b = this.field_150939_a;

		if (b == RS2Blocks.blockToolBox || b == RS2Blocks.blockToolBoxResis) {
			if (stack.hasTagCompound()) {

				if (stack.getTagCompound().hasKey("recsyscletem|toolbox")) {
					tooltip.add(StatCollector.translateToLocal("tooltip.tntptool.toolbox.content"));
					if (PistonToolbox.isShiftDown()) {
						NBTTagCompound tag = (NBTTagCompound) stack.getTagCompound().getTag("recsyscletem|toolbox");
						boolean craftUpgrade = tag.getBoolean("craft");
						boolean resisUpgrade = tag.getBoolean("resis");
						boolean storaUpgrade = tag.getBoolean("stora");
						boolean securUpgrade = tag.getBoolean("secur");
						boolean pickupUpgrade = tag.getBoolean("picku");
						if (craftUpgrade) {
							tooltip.add(StatCollector.translateToLocal("tooltip.tntptool.toolbox.craft"));
						}
						if (resisUpgrade) {
							tooltip.add(StatCollector.translateToLocal("tooltip.tntptool.toolbox.exploresis"));
						}
						if (storaUpgrade) {
							tooltip.add(StatCollector.translateToLocal("tooltip.tntptool.toolbox.storage"));
						}
						if (securUpgrade) {
							tooltip.add(StatCollector.translateToLocal("tooltip.tntptool.toolbox.security"));
						}
						int inf = tag.getInteger("infStorageUpgrade");
						if (inf > 0) {
							tooltip.add(StatCollector.translateToLocalFormatted("tooltip.tntptool.toolbox.inf", inf));
						}
						if (pickupUpgrade) {
							tooltip.add(StatCollector.translateToLocal("tooltip.tntptool.toolbox.pickup"));
							int infLimit = (int) (TileToolBox.cfgStorageLimit * Math.pow(2, inf));
							for (int i = 0; i < 2; i++) {
								NBTTagCompound infTag = tag.getCompoundTag("inf" + i);
								int infc = infTag.getInteger("infCount");
								ItemStack infStack = ItemStack.loadItemStackFromNBT(infTag);
								if (infStack != null && infStack.stackSize > 0) {
									tooltip.add(EnumChatFormatting.YELLOW + infStack.getDisplayName());
									tooltip.add(EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
											"tooltip.tntptool.toolbox.inf_storage", infc, infLimit));
								}
							}
						}

					} else {
						tooltip.add(StatCollector.translateToLocal("tooltip.tntptool.shift"));
					}

				}
			}
		}

	}

}
