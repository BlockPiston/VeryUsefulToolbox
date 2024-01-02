package pistonmc.vutoolbox.init;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;
import pistonmc.vutoolbox.ModGui;
import pistonmc.vutoolbox.ModInfo;
import pistonmc.vutoolbox.ModNetwork;
import pistonmc.vutoolbox.event.ToolBoxPickupUpgradeEventHandler;
import pistonmc.vutoolbox.object.BlockToolbox;
import pistonmc.vutoolbox.object.ItemBlockToolbox;
import pistonmc.vutoolbox.object.ItemChipset;
import pistonmc.vutoolbox.object.TileToolbox;

public class InitCommon implements Init {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		// Recsyscletem.log.info("Loading Configuration");
		//ModConfig.loadConfigLoaders();
		//PistonToolbox.cfg = ModConfig.loadConfiguration();
		// Recsyscletem.log.info("Finished.");

		// Recsyscletem.log.info("Loading Recsyscletem 2 API");
		// ElectronTypes.registerElectronType(ElectronImpl.class, "basic");
		// MinecraftForge.EVENT_BUS.register(new RS2EventCore());
		// Recsyscletem.log.info("Finished.");

		// Recsyscletem.log.info("Loading Blocks");
		// BlockSuper.loadBlocks();
		// Recsyscletem.log.info("Finished.");

		// Recsyscletem.log.info("Loading Items");
		// ItemSuper.loadItems();
		// Recsyscletem.log.info("Finished.");
		GameRegistry.registerItem(new ItemChipset(), ItemChipset.NAME);
		BlockToolbox blockToolBox = new BlockToolbox(false);
		BlockToolbox blockToolBoxResis = new BlockToolbox(true);
		GameRegistry.registerBlock(blockToolBox, ItemBlockToolbox.class, BlockToolbox.NAME);
		GameRegistry.registerBlock(blockToolBoxResis, ItemBlockToolbox.class, BlockToolbox.NAME+"Resis");

		GameRegistry.registerTileEntity(TileToolbox.class, "tileToolBox");
		
		NetworkRegistry.INSTANCE.registerGuiHandler(ModInfo.ID, new ModGui());

	}

	@Override
	public void init(FMLInitializationEvent event) {


	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		// Recsyscletem.log.info("Loading Stage 3");
		// log.info("Post Init");

		// log.info("Register Events");
		MinecraftForge.EVENT_BUS.register(new ToolBoxPickupUpgradeEventHandler());

		// log.info("Init Network");
		ModNetwork.registerMessages();

		// log.info("Apply Config");
		// Recsyscletem.log.info("Loading Config Stage 3");
		// RS2Config.setState(State.POST_INIT);
		// RS2Config.invokeLoaders(Recsyscletem.cfg);
		// Recsyscletem.log.info("Finished.");
		
		// tooltip

	}


}
