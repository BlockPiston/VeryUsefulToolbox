package pistonmc.vutoolbox;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import pistonmc.vutoolbox.event.MessageToolbox2Security;
import pistonmc.vutoolbox.event.MessageToolbox2Update;
import pistonmc.vutoolbox.event.MessageToolboxRequest;
import pistonmc.vutoolbox.event.MessageWorkbenchOpen;

public class ModNetwork {
	public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.ID);
	private static int id = 0;

	public static void registerMessages() {
		network.registerMessage(MessageWorkbenchOpen.Handler.class, MessageWorkbenchOpen.class, id++, Side.SERVER);
		network.registerMessage(MessageToolbox2Security.Handler.class, MessageToolbox2Security.class, id++, Side.CLIENT);
		network.registerMessage(MessageToolbox2Update.Handler.class, MessageToolbox2Update.class, id++, Side.CLIENT);
		network.registerMessage(MessageToolboxRequest.Handler.class, MessageToolboxRequest.class, id++, Side.SERVER);
	}

}
