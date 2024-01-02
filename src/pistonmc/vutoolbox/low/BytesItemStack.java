package pistonmc.vutoolbox.low;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import pistonmc.vutoolbox.ModUtils;

public class BytesItemStack {
	private ByteBuf buf;
	
	public BytesItemStack(ByteBuf buf) {
		this.buf = buf;
	}
	
	/**
	 * Write the stack to the buffer.
	 * @param stack (nullable)
	 */
	public void write(ItemStack stack) {
		if (stack == null) {
			buf.writeInt(-1);
			return;
		}
		buf.writeInt(Item.getIdFromItem(stack.getItem()));
		buf.writeInt(stack.stackSize);
		buf.writeInt(stack.getItemDamage());
		
		writeNBT(stack.getTagCompound());	
	}
	
	public void writeNBT(NBTTagCompound tag) {
		if (tag == null) {
			buf.writeInt(-1);
			return;
		}
		try {
			byte[] abyte = CompressedStreamTools.compress(tag);
			buf.writeInt(abyte.length);
			buf.writeBytes(abyte);
		} catch (IOException e) {
			e.printStackTrace();
			buf.writeInt(-1);
		}
		
	}
	
	public ItemStack read() {
		int id = buf.readInt();
		if (id == -1) {
			return null;
		}
		int size = buf.readInt();
		int damage = buf.readInt();
		ItemStack stack = new ItemStack(Item.getItemById(id), size, damage);
		NBTTagCompound tag = readNBT();
		stack.setTagCompound(tag);
		return stack;
	}
	
	private NBTTagCompound readNBT() {
		int nbtSize = buf.readInt();
		if (nbtSize < 0) {
			return null;
		}
		byte[] bytes = new byte[nbtSize];
		buf.readBytes(bytes);
		try {
			return CompressedStreamTools.func_152457_a(bytes, new NBTSizeTracker(200000));
		} catch (IOException e) {
			e.printStackTrace();
			return new NBTTagCompound();
		}
	}
}
