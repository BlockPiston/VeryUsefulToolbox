package pistonmc.vutoolbox.event;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/**
 * Message sent to the player trying to access someone else's toolbox
 */
public class MessageToolboxSecurity implements IMessage {
	public String ownerName;

	public MessageToolboxSecurity() {

	}

	public MessageToolboxSecurity(String name) {
		ownerName = name;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		char[] chars = new char[buf.readInt()];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = buf.readChar();
		}
		ownerName = String.valueOf(chars);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(ownerName.length());
		for (char c : ownerName.toCharArray()) {
			buf.writeChar(c);
		}
	}

	public static class StubHandler implements IMessageHandler<MessageToolboxSecurity, IMessage> {

		@Override
		public IMessage onMessage(MessageToolboxSecurity message, MessageContext ctx) {
			return null;
		}

	}

}
