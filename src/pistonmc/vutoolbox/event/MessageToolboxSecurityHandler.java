package pistonmc.vutoolbox.event;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.util.StatCollector;
import pistonmc.vutoolbox.ModInfo;
import pistonmc.vutoolbox.ModUtils;

public class MessageToolboxSecurityHandler implements IMessageHandler<MessageToolboxSecurity, IMessage> {

	@Override
	public IMessage onMessage(MessageToolboxSecurity message, MessageContext ctx) {
		ModUtils.printChatMessage(
				StatCollector.translateToLocalFormatted("message."+ModInfo.ID+".toolbox_security", message.ownerName));
		return null;
	}

}
