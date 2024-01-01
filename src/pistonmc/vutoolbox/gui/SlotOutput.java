package pistonmc.vutoolbox.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * A slot that player cannot put stuff in manually
 */
public class SlotOutput extends Slot {

	public SlotOutput(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}

}
