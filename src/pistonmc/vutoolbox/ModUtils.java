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


}
