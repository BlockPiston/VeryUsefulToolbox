package pistonmc.vutoolbox.init;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import pistonmc.vutoolbox.ModNetwork;

public class InitServer implements Init {
	InitCommon common = new InitCommon();

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		common.preInit(event);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		common.init(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		common.postInit(event);
		ModNetwork.registerServerMessages();
	}

}
