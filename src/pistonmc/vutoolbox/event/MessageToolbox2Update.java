package pistonmc.vutoolbox.event;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import pistonmc.vutoolbox.core.BigItemStack;
import pistonmc.vutoolbox.core.Toolbox;
import pistonmc.vutoolbox.low.BytesItemStack;
import pistonmc.vutoolbox.object.TileToolbox;

/**
 * Message to update the displayed tools on the toolbox and the infinity slots
 * 
 * The other slots are updated through GUI automatically
 */
public class MessageToolbox2Update implements IMessage {
	private int x;
	private int y;
	private int z;
	private int dimension;
	private ItemStack[] tools;
	private BigItemStack[] infinitySlots;

	public MessageToolbox2Update() {
		tools = new ItemStack[Toolbox.NUM_TOP_SLOTS];
		infinitySlots = new BigItemStack[Toolbox.NUM_INFINITY_SLOTS];
	}

	public MessageToolbox2Update(TileToolbox tile) {
		this();
		x = tile.xCoord;
		y = tile.yCoord;
		z = tile.zCoord;
		dimension = tile.getWorldObj().provider.dimensionId;
		
		for (int i = 0; i < tools.length; i++) {
			tools[i] = tile.getStackInSlot(i);
		}
		
		for (int i = 0; i< infinitySlots.length; i++) {
			infinitySlots[i] = tile.getToolbox().getInfinityStack(i);
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		dimension = buf.readInt();
		BytesItemStack stackReader = new BytesItemStack(buf);

		for (int i = 0; i < tools.length; i++) {
			tools[i] = stackReader.read();
		}
		for (int i = 0; i < infinitySlots.length; i++) {
			ItemStack stack = stackReader.read();
			int count = buf.readInt();
			BigItemStack slot = new BigItemStack();
			slot.set(stack, count);
			infinitySlots[i] = slot;
		}

	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(dimension);
		BytesItemStack stackWriter = new BytesItemStack(buf);
		for (ItemStack tool: tools) {
			stackWriter.write(tool);
		}
		for (BigItemStack slot: infinitySlots) {
			stackWriter.write(slot.getItemStack());
			buf.writeInt(slot.getCount());
		}

	}

	public static class Handler implements IMessageHandler<MessageToolbox2Update, IMessage> {

		@Override
		public IMessage onMessage(MessageToolbox2Update message, MessageContext ctx) {
			if (Minecraft.getMinecraft().thePlayer.dimension != message.dimension) {
				return null;
			}
			
			World world = Minecraft.getMinecraft().theWorld;
			TileToolbox tile = TileToolbox.cast(world.getTileEntity(message.x, message.y, message.z));
			if (tile == null) {
				return null;
			}
			
			for (int i = 0; i < Toolbox.NUM_TOP_SLOTS; i++) {
				tile.setInventorySlotContents(i, message.tools[i]);
			}
			for (int i = 0; i < Toolbox.NUM_INFINITY_SLOTS; i++) {
				BigItemStack slot = message.infinitySlots[i];
				tile.getToolbox().getInfinityStack(i).set(slot.getItemStack(), slot.getCount());
			}
			
			return null;
		}

	}
}
