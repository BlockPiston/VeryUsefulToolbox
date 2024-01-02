package pistonmc.vutoolbox;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import pistonmc.vutoolbox.event.MessageToolboxSecurity;
import pistonmc.vutoolbox.event.MessageToolboxUpdate;
import pistonmc.vutoolbox.event.MessageToolboxRequest;
import pistonmc.vutoolbox.event.MessageWorkbenchOpen;

public class ModNetwork {
	public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.ID);
	private static int id = 0;

	public static void registerMessages() {
		network.registerMessage(MessageWorkbenchOpen.Handler.class, MessageWorkbenchOpen.class, id++, Side.SERVER);
		network.registerMessage(MessageToolboxSecurity.Handler.class, MessageToolboxSecurity.class, id++, Side.CLIENT);
		network.registerMessage(MessageToolboxUpdate.Handler.class, MessageToolboxUpdate.class, id++, Side.CLIENT);
		network.registerMessage(MessageToolboxRequest.Handler.class, MessageToolboxRequest.class, id++, Side.SERVER);
	}

}
