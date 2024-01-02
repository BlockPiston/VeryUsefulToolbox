package pistonmc.vutoolbox.event;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import pistonmc.vutoolbox.core.BigItemStack;
import pistonmc.vutoolbox.core.Toolbox;
import pistonmc.vutoolbox.low.BytesItemStack;
import pistonmc.vutoolbox.object.TileToolbox;

/**
 * Message to update the displayed tools on the toolbox and the infinity slots
 * 
 * The other slots are updated through GUI automatically
 */
public class MessageToolboxUpdate implements IMessage {
	public int x;
	public int y;
	public int z;
	public int dimension;
	public ItemStack[] tools;
	public ItemStack[] upgrades;
	public BigItemStack[] infinitySlots;

	public MessageToolboxUpdate() {
		tools = new ItemStack[Toolbox.NUM_TOP_SLOTS];
		upgrades = new ItemStack[Toolbox.NUM_UPGRADE_SLOTS];
		infinitySlots = new BigItemStack[Toolbox.NUM_INFINITY_SLOTS];
	}

	public MessageToolboxUpdate(TileToolbox tile) {
		this();
		x = tile.xCoord;
		y = tile.yCoord;
		z = tile.zCoord;
		dimension = tile.getWorldObj().provider.dimensionId;
		
		for (int i = 0; i < tools.length; i++) {
			tools[i] = tile.getStackInSlot(i);
		}
		
		for (int i = 0; i < Toolbox.NUM_UPGRADE_SLOTS; i++) {
			upgrades[i] = tile.getStackInSlot(Toolbox.UPGRADE_SLOTS_START+i);
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
		for (int i = 0; i < upgrades.length; i++) {
			upgrades[i] = stackReader.read();
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
		for (ItemStack upgrade: upgrades) {
			stackWriter.write(upgrade);
		}
		for (BigItemStack slot: infinitySlots) {
			stackWriter.write(slot.getItemStack());
			buf.writeInt(slot.getCount());
		}

	}
	
	public static class StubHandler implements IMessageHandler<MessageToolboxUpdate, IMessage>{

		@Override
		public IMessage onMessage(MessageToolboxUpdate message, MessageContext ctx) {
			return null;
		}

	}


}
