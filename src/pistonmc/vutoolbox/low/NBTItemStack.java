package pistonmc.vutoolbox.low;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Wrapper around NBTTagCompound for ItemStack
 */
public class NBTItemStack {
	private NBTTagCompound inner;
	
	/**
	 * Create NBTItemStack for the inner NBTTagCompound
	 * @param inner (non-null)
	 */
	public NBTItemStack(NBTTagCompound inner) {
		this.inner = inner;
	}
	
	/**
	 * Write the ItemStack (non-null)
	 */
	public void write(ItemStack stack) {
		stack.writeToNBT(inner);
	}
	
	/**
	 * Read and construct an ItemStack, may be null
	 * @return
	 */
	public ItemStack read() {
		if (!inner.hasKey("id")) {
			return null;
		}
		return ItemStack.loadItemStackFromNBT(inner);
	}
}
