package pistonmc.vutoolbox.event;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.StatCollector;
import pistonmc.vutoolbox.ModInfo;
import pistonmc.vutoolbox.ModUtils;

/**
 * Message sent to the player trying to access someone else's toolbox
 */
public class MessageToolbox2Security implements IMessage {
	private String ownerName;

	public MessageToolbox2Security() {

	}

	public MessageToolbox2Security(String name) {
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

	public static class Handler implements IMessageHandler<MessageToolbox2Security, IMessage> {

		@Override
		public IMessage onMessage(MessageToolbox2Security message, MessageContext ctx) {
			ModUtils.printChatMessage(
					StatCollector.translateToLocalFormatted("message."+ModInfo.ID+".toolbox_security", message.ownerName));
			return null;
		}

	}

}
