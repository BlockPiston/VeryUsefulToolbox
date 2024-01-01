package com.tntp.tntptool.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class RS2Network {
	public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel("tntptool");
	private static int id = 0;

	public static void loadMessages() {
		network.registerMessage(MessageWorkbench.Handler.class, MessageWorkbench.class, id++, Side.SERVER);
		network.registerMessage(MessageToolBoxSecurity.Handler.class, MessageToolBoxSecurity.class, id++, Side.CLIENT);
		network.registerMessage(MessageToolBoxUpdate.Handler.class, MessageToolBoxUpdate.class, id++, Side.CLIENT);
	}

	public static <REQ extends MSuper<REQ>> void registerMessage(MSuper<REQ> message, Class<REQ> messageClass,
			Side side) {
		network.registerMessage(message, messageClass, id++, side);
	}

}
