package pistonmc.vutoolbox.event;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.WorldServer;
import pistonmc.vutoolbox.object.TileToolbox;

/**
 * Message to request update from a Toolbox
 * 
 * This is used to initialize a toolbox on first world load
 */
public class MessageToolboxRequest implements IMessage {
	private int x;
	private int y;
	private int z;
	private int dimension;

	public MessageToolboxRequest() {

	}

	public MessageToolboxRequest(TileToolbox tile) {
		this();
		x = tile.xCoord;
		y = tile.yCoord;
		z = tile.zCoord;
		dimension = tile.getWorldObj().provider.dimensionId;

	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		dimension = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(dimension);
	}

	public static class Handler implements IMessageHandler<MessageToolboxRequest, IMessage> {

		@Override
		public IMessage onMessage(MessageToolboxRequest message, MessageContext ctx) {
			WorldServer world = ctx.getServerHandler().playerEntity.mcServer.worldServerForDimension(message.dimension);
			TileToolbox tile = TileToolbox.cast(world.getTileEntity(message.x, message.y, message.z));
			if (tile == null) {
				return null;
			}
			return new MessageToolboxUpdate(tile);
		}

	}
}
