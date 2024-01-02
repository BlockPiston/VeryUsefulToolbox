package pistonmc.vutoolbox;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import pistonmc.vutoolbox.event.MessageToolBoxSecurity;
import pistonmc.vutoolbox.event.MessageToolBoxUpdate;
import pistonmc.vutoolbox.event.MessageWorkbenchOpen;

public class ModNetwork {
	public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.ID);
	private static int id = 0;

	public static void registerMessages() {
		network.registerMessage(MessageWorkbenchOpen.Handler.class, MessageWorkbenchOpen.class, id++, Side.SERVER);
		network.registerMessage(MessageToolBoxSecurity.Handler.class, MessageToolBoxSecurity.class, id++, Side.CLIENT);
		network.registerMessage(MessageToolBoxUpdate.Handler.class, MessageToolBoxUpdate.class, id++, Side.CLIENT);
	}

}
