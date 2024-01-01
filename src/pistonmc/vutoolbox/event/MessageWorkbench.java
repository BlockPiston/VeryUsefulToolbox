package com.tntp.tntptool.network;

import com.tntp.tntptool.container.ContainerWorkbenchEx;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S2DPacketOpenWindow;

public class MessageWorkbench implements IMessage {
	private int x;
	private int y;
	private int z;

	public MessageWorkbench(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;

	}

	public MessageWorkbench() {

	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();

	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);

	}

	public static class Handler implements IMessageHandler<MessageWorkbench, IMessage> {

		@Override
		public IMessage onMessage(MessageWorkbench message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;

			player.getNextWindowId();
			player.playerNetServerHandler
					.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, 1, "Crafting", 9, true));
			player.openContainer = new ContainerWorkbenchAlwaysInteractable(player.inventory, player.worldObj, message.x, message.y,
					message.z);
			player.openContainer.windowId = player.currentWindowId;
			player.openContainer.addCraftingToCrafters(player);
			return null;
		}
	}

}
