package pistonmc.vutoolbox.core;

import net.minecraft.item.ItemStack;

/**
 * Big ItemStack used for infinity storage in the Toolbox
 */
public class BigItemStack {
	/** The item, may be null. The count on it should always be 1 */
	private ItemStack stack;
	private int count;
	
	public BigItemStack() {
		stack = null;
		count = 0;
	}
	
	public ItemStack getItemStack() {
		if (count == 0) {
			return null;
		}
		return stack;
	}
	
	public int getCount() {
		if (stack == null) {
			return 0;
		}
		return count;
	}
	
	public void set(ItemStack stack, int count) {
		if (stack == null || count == 0) {
			this.stack = null;
			this.count = 0;
			return;
		}
		
		this.stack = stack.copy();
		this.stack.stackSize = 1;
		this.count = count;
	}
	
	public void addCount(int delta) {
		count += delta;
	}
	
	public void clear() {
		stack = null;
		count = 0;
	}
}
