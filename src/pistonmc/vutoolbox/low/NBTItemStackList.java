package pistonmc.vutoolbox.low;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * Wrapper for an array of ItemStack
 */
public class NBTItemStackList {
	private NBTTagList inner;
	
	public NBTItemStackList(NBTTagList inner) {
		this.inner = inner;
	}
	
	/**
	 * Write a list of items
	 * 
	 * The items are stored sparsely
	 * @param list (non-null)
	 */
	public void append(ItemStack[] slots) {
		for (int i = 0; i < slots.length; i++) {
			ItemStack stack = slots[i];
			if (stack == null || stack.stackSize <= 0) {
				continue;
			}
			NBTTagCompound tag = new NBTTagCompound();
			NBTItemStack tagStack = new NBTItemStack(tag);
			tagStack.write(stack);
			tag.setInteger("slot", i+1); // 0 is used for invalid value
			inner.appendTag(tag);
		}
	}
	
	/**
	 * Read a list of items
	 * 
	 * Original items will be overwritten only if another item is loaded
	 * into that slot. null the array manually before calling if needed.
	 * @param slots (non-null)
	 */
	public void readInto(ItemStack[] slots) {
		int count = inner.tagCount();
		for (int i = 0; i < count; i++) {
			NBTTagCompound tag = inner.getCompoundTagAt(i);
			int slot = tag.getInteger("slot");
			if (slot == 0) {
				continue;
			}
			NBTItemStack tagStack = new NBTItemStack(tag);
			ItemStack stack = tagStack.read();
			if (stack != null) {
				slots[slot-1] = stack;
			}
		}
	}
}
