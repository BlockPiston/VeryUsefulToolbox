package pistonmc.vutoolbox.object;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import pistonmc.vutoolbox.ModInfo;
import pistonmc.vutoolbox.ModObjects;
import pistonmc.vutoolbox.ModUtils;
import pistonmc.vutoolbox.core.ToolboxStatus;
import pistonmc.vutoolbox.core.ToolboxTooltip;
import pistonmc.vutoolbox.low.NBTToolbox;

public class ItemBlockToolbox extends ItemBlock {

	public ItemBlockToolbox(Block block) {
		super(block);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean extra) {
		Block b = this.field_150939_a;
		
		if (b != ModObjects.blockToolBox && b != ModObjects.blockToolBoxResis) {
			return;
		}

		NBTToolbox tagToolbox = NBTToolbox.fromItemStack(stack);
		if (tagToolbox == null) {
			return;
		}
		
		if (!ModUtils.isShiftDown()) {
			tooltip.add(StatCollector.translateToLocal("tooltip."+ModInfo.ID +".shift"));
		}
		
		ToolboxStatus status = new ToolboxStatus();
		status.readFromNBT(tagToolbox);
		new ToolboxTooltip(status).addTooltips(tagToolbox, tooltip);
	}

}
