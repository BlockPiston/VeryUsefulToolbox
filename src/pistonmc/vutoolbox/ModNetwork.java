package pistonmc.vutoolbox;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pistonmc.vutoolbox.event.MessageToolboxSecurity;
import pistonmc.vutoolbox.event.MessageToolboxSecurityHandler;
import pistonmc.vutoolbox.event.MessageToolboxUpdate;
import pistonmc.vutoolbox.event.MessageToolboxUpdateHandler;
import pistonmc.vutoolbox.event.MessageToolboxRequest;
import pistonmc.vutoolbox.event.MessageWorkbenchOpen;

public class ModNetwork {
	public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.ID);
	private static int id = 0;

	@SideOnly(Side.SERVER)
	public static void registerServerMessages() {
		network.registerMessage(MessageWorkbenchOpen.Handler.class, MessageWorkbenchOpen.class, id++, Side.SERVER);
		network.registerMessage(MessageToolboxRequest.Handler.class, MessageToolboxRequest.class, id++, Side.SERVER);
		network.registerMessage(MessageToolboxSecurity.StubHandler.class, MessageToolboxSecurity.class, id++, Side.CLIENT);
		network.registerMessage(MessageToolboxUpdate.StubHandler.class, MessageToolboxUpdate.class, id++, Side.CLIENT);
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerClientMessages() {
		network.registerMessage(MessageWorkbenchOpen.Handler.class, MessageWorkbenchOpen.class, id++, Side.SERVER);
		network.registerMessage(MessageToolboxRequest.Handler.class, MessageToolboxRequest.class, id++, Side.SERVER);
		network.registerMessage(MessageToolboxSecurityHandler.class, MessageToolboxSecurity.class, id++, Side.CLIENT);
		network.registerMessage(MessageToolboxUpdateHandler.class, MessageToolboxUpdate.class, id++, Side.CLIENT);
	}

}
