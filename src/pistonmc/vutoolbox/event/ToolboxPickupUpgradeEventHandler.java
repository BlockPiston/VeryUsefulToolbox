package pistonmc.vutoolbox.event;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import pistonmc.vutoolbox.core.Toolbox;
import pistonmc.vutoolbox.core.ToolboxStatus;
import pistonmc.vutoolbox.core.Upgrades;
import pistonmc.vutoolbox.low.NBTToolbox;
import pistonmc.vutoolbox.object.BlockToolbox;

public class ToolboxPickupUpgradeEventHandler {
	@SubscribeEvent
	public void on(EntityItemPickupEvent event) {
		if (event.isCanceled()) {
			return;
		}
		if (handle(event.entityPlayer, event.item)) {
			event.setResult(Event.Result.ALLOW);
		}

	}

	private static boolean handle(EntityPlayer player, EntityItem entityItem) {
		ItemStack stack = entityItem.getEntityItem();
		if (stack == null || stack.stackSize <= 0) {
			return false;
		}
		boolean taken = false;
		for (ItemStack item : player.inventory.mainInventory) {
			if (stack.stackSize <= 0) {
				// all items picked up
				break;
			}
			if (item == null || item.stackSize <= 0) {
				continue;
			}
			if (!(Block.getBlockFromItem(item.getItem()) instanceof BlockToolbox)) {
				continue;
			}
			// for the tool box to have pick up upgrade, it must have a nbt tag
			NBTToolbox tagBox = NBTToolbox.fromItemStack(item);
			if (tagBox == null) {
				continue;
			}
			ToolboxStatus status = new ToolboxStatus();
			status.readFromNBT(tagBox);
			if (!status.getUpgrades().isEnabled(Upgrades.PICKUP)) {
				continue;
			}
			Toolbox toolbox = new Toolbox();
			toolbox.readFromNBT(tagBox);
			
			int oldSize = stack.stackSize;
			toolbox.addToInfinityStack(stack);
			if (stack.stackSize != oldSize) {
				taken = true;
			}
			if (stack.stackSize == 0) {
				break;
			}
			toolbox.writeToNBT(tagBox);
			tagBox.setToItemStack(item);

		}
	
		if (stack.stackSize <= 0) {
			entityItem.setDead();
		}
		return taken;
	}
}
