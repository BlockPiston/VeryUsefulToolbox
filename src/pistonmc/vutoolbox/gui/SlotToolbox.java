package pistonmc.vutoolbox.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import pistonmc.vutoolbox.ModObjects;

public class SlotToolbox extends Slot {
	
	private boolean onlyAllowUnstackable;
	private boolean isSingleItemSlot;

	public SlotToolbox(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
		isSingleItemSlot = true;
	}
	
	public void setOnlyAllowUnstackable() {
		onlyAllowUnstackable = true;
	}
	
	public void setAllowStackable() {
		isSingleItemSlot = false;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		if (stack == null) {
			return true;
		}
		// toolboxes cannot be put in
		Item item = stack.getItem();
		if (item == Item.getItemFromBlock(ModObjects.blockToolBox)) {
			return false;
		}
		if (item == Item.getItemFromBlock(ModObjects.blockToolBoxResis)) {
			return false;
		}
		if (onlyAllowUnstackable) {
			if (stack.isStackable()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int getSlotStackLimit() {
		if (isSingleItemSlot) {
			return 1;
		}
		return super.getSlotStackLimit();
	}

}
