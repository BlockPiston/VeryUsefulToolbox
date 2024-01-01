package pistonmc.vutoolbox.event;

import java.io.IOException;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import pistonmc.vutoolbox.ModUtils;
import pistonmc.vutoolbox.block.TileToolbox;
import pistonmc.vutoolbox.core.Toolbox;

/**
 * Message to update the displayed tools on the toolbox
 */
public class MessageToolBoxUpdate implements IMessage {
	private int x;
	private int y;
	private int z;
	private int dimension;
	private ItemStack[] tools;

	public MessageToolBoxUpdate() {

	}

	public MessageToolBoxUpdate(TileToolbox tile) {
		x = tile.xCoord;
		y = tile.yCoord;
		z = tile.zCoord;
		dimension = tile.getWorldObj().provider.dimensionId;
		tools = new ItemStack[Toolbox.NUM_TOP_SLOTS];
		for (int i = 0; i < tools.length; i++) {
			tools[i] = tile.getStackInSlot(i);
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		dimension = buf.readInt();
		tools = new ItemStack[9];
		for (int i = 0; i < 9; i++) {
			int id = buf.readInt();
			if (id == -1) {
				tools[i] = null;
			} else {
				int size = buf.readInt();
				int damage = buf.readInt();
				tools[i] = new ItemStack(Item.getItemById(id), size, damage);
				try {
					tools[i].setTagCompound(ModUtils.readNBTTagCompoundFromBuffer(buf));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(dimension);
		for (int i = 0; i < 9; i++) {
			if (tools[i] == null) {
				buf.writeInt(-1);
			} else {
				buf.writeInt(Item.getIdFromItem(tools[i].getItem()));
				buf.writeInt(tools[i].stackSize);
				buf.writeInt(tools[i].getItemDamage());
				try {
					ModUtils.writeNBTTagCompoundToBuffer(buf, tools[i].getTagCompound());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static class Handler implements IMessageHandler<MessageToolBoxUpdate, IMessage> {

		@Override
		public IMessage onMessage(MessageToolBoxUpdate message, MessageContext ctx) {
			if (Minecraft.getMinecraft().thePlayer.dimension == message.dimension) {
				World world = Minecraft.getMinecraft().theWorld;
				if (world.getTileEntity(message.x, message.y, message.z) instanceof TileToolbox) {
					TileToolbox tile = (TileToolbox) world.getTileEntity(message.x, message.y, message.z);
					for (int i = 0; i < Toolbox.NUM_TOP_SLOTS; i++) {
						tile.setInventorySlotContents(i, message.tools[i]);
					}
				}
			}
			return null;
		}

	}
}
