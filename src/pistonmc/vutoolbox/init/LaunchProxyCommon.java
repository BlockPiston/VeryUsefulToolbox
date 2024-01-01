package com.tntp.tntptool.proxy;

import com.tntp.tntptool.PistonToolbox;
import com.tntp.tntptool.RS2Config;
import com.tntp.tntptool.block.BlockAbstract;
import com.tntp.tntptool.block.BlockToolBox;
import com.tntp.tntptool.event.ToolBoxPickupUpgradeEventHandler;
import com.tntp.tntptool.item.ItemBlockTooltip;
import com.tntp.tntptool.item.ItemChipset;
import com.tntp.tntptool.network.RS2Network;
import com.tntp.tntptool.tileentity.TileToolBox;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;

public class LaunchProxyCommon implements LaunchProxy {

	@Override
	public void identify() {
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		PistonToolbox.log.info("Loading Stage 1");
		// Recsyscletem.log.info("Loading Configuration");
		ModConfig.loadConfigLoaders();
		PistonToolbox.cfg = ModConfig.loadConfiguration();
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
		GameRegistry.registerItem(new ItemChipset(), "itemChipset");
		BlockAbstract blockToolBox = new BlockToolbox(false);
		BlockAbstract blockToolBoxResis = new BlockToolbox(true);
		GameRegistry.registerBlock(blockToolBox, ItemBlockTooltip.class, "blockToolBox");
		GameRegistry.registerBlock(blockToolBoxResis, ItemBlockTooltip.class, "blockToolBoxResis");

		GameRegistry.registerTileEntity(TileToolbox.class, "tileToolBox");
	}

	@Override
	public void init(FMLInitializationEvent event) {
		PistonToolbox.log.info("Loading Stage 2");
		// Recsyscletem.log.info("Loading Config Stage 2");
		// RS2Config.setState(State.INIT);
		// RS2Config.invokeLoaders(Recsyscletem.cfg);
		// Recsyscletem.log.info("Finished.");

		// Recsyscletem.log.info("Loading ItemStacks");
		// RS2Items.loadItemStacks();
		// Recsyscletem.log.info("Finished.");
		// log.info("Init ItemStacks");
		// RecsyscletemItemStacks.init();
		// log.info("Register Recipes");
		// RecsyscletemRecipes.registerRecipes();
		// log.info("Register Other");
		// GameRegistry.registerFuelHandler(new RecsyscletemFuelHandler());
		// Recsyscletem.log.info("Loading Achievements");
		// AchievementSuper.loadAchievements();
		// Recsyscletem.log.info("Finished.");

		// Recsyscletem.log.info("Loading Network");
		// RS2Network.loadMessages();
		// Recsyscletem.log.info("Finished.");
		// ((CommandHandler)
		// (MinecraftServer.getServer().getCommandManager())).registerCommand(new
		// CommandRecsyscletem());

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		// Recsyscletem.log.info("Loading Stage 3");
		// log.info("Post Init");

		// log.info("Register Events");
		MinecraftForge.EVENT_BUS.register(new ToolBoxPickupUpgradeEventHandler());

		// log.info("Init Network");
		RS2Network.loadMessages();

		// log.info("Apply Config");
		// Recsyscletem.log.info("Loading Config Stage 3");
		// RS2Config.setState(State.POST_INIT);
		// RS2Config.invokeLoaders(Recsyscletem.cfg);
		// Recsyscletem.log.info("Finished.");
		// 工具箱自动拾取满
		// tooltip

	}

	@Override
	public void finish() {
	}

}
