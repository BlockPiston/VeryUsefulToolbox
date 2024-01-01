package pistonmc.vutoolbox;

import java.io.IOException;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;

/**
 * Stuff that has no where better to go
 */
public class ModUtils {
	
	public static final Random RNG = new Random();

	/**
	 * Request `requestCount` from slot i. The inventory is modified and the 
	 * requested items are returned in a new stack
	 * @param slots (non-null with nullable elements)
	 * @param i (within bound)
	 * @param requestCount
	 * 
	 * @return may be null
	 */
	public static ItemStack decrStackSize(ItemStack[] slots, int i, int requestCount) {
		ItemStack current = slots[i];
		if (current == null) {
			return null;
		}
		
		if (current.stackSize <= requestCount) {
			ItemStack toReturn = current;
			slots[i] = null;
			return toReturn;
		} 
		
		ItemStack toReturn = current.splitStack(requestCount);
		if (current.stackSize == 0) {
			slots[i] = null;
		}

		return toReturn;
	}
	
	public static boolean isTileEntityUsableByPlayerByDistance(TileEntity t, EntityPlayer p) {
		return p.getDistanceSq(t.xCoord + 0.5D, t.yCoord + 0.5D, t.zCoord + 0.5D) <= 64.0D;
	}
	
	public static boolean isShiftDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}
	
	public static void printChatMessage(String message) {
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(message));
	}
	

	/**
	 * Writes a compressed NBTTagCompound to this buffer
	 */
	public static void writeNBTTagCompoundToBuffer(ByteBuf buf, NBTTagCompound p_150786_1_) throws IOException {
		if (p_150786_1_ == null) {
			buf.writeShort(-1);
		} else {
			byte[] abyte = CompressedStreamTools.compress(p_150786_1_);
			buf.writeShort((short) abyte.length);
			buf.writeBytes(abyte);
		}
	}

	/**
	 * Reads a compressed NBTTagCompound from this buffer
	 */
	public static NBTTagCompound readNBTTagCompoundFromBuffer(ByteBuf buf) throws IOException {
		short short1 = buf.readShort();
		if (short1 < 0) {
			return null;
		} else {
			byte[] abyte = new byte[short1];
			buf.readBytes(abyte);
			return CompressedStreamTools.func_152457_a(abyte, new NBTSizeTracker(2097152L));
		}
	}
}
