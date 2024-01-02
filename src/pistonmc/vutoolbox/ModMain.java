package pistonmc.vutoolbox;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import pistonmc.vutoolbox.init.Init;

@Mod(modid = ModInfo.ID, version = ModInfo.VERSION)
public class ModMain
{
	
	@SidedProxy(clientSide = ModInfo.GROUP + ".init.InitClient", serverSide = ModInfo.GROUP + ".init.InitServer")
	public static Init init;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		init.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		init.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		init.postInit(event);
	}

}
