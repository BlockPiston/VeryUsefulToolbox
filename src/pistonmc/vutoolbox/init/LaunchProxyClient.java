package com.tntp.tntptool.proxy;

import com.tntp.tntptool.PistonToolbox;
import com.tntp.tntptool.RS2Blocks;
import com.tntp.tntptool.block.BlockAbstract;
import com.tntp.tntptool.block.BlockToolBox;
import com.tntp.tntptool.gui.GuiToolBox;
import com.tntp.tntptool.gui.RecsyscletemGuiHandler;
import com.tntp.tntptool.item.ItemBlockTooltip;
import com.tntp.tntptool.item.ItemChipset;
import com.tntp.tntptool.model.ModelToolBox;
import com.tntp.tntptool.tileentity.TileToolBox;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.client.ClientCommandHandler;

public class LaunchProxyClient implements Init {
	InitCommon common = new InitCommon();

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		common.preInit(event);


		// RecsyscletemGuiHandler.assignGuiID(GuiDictionary.class);
		// RecsyscletemGuiHandler.assignGuiID(GuiRecycler.class);
		// RecsyscletemGuiHandler.assignGuiID(GuiComposter.class);
		// RecsyscletemGuiHandler.assignGuiID(GuiIncinerator.class);
		// RecsyscletemGuiHandler.assignGuiID(GuiPacker.class);
		// RecsyscletemGuiHandler.assignGuiID(GuiMatcher.class);
		// RecsyscletemGuiHandler.assignGuiID(GuiReceiverBox.class);
		// RecsyscletemGuiHandler.assignGuiID(GuiRouter.class);
		// RecsyscletemGuiHandler.assignGuiID(GuiTrashCanSmall.class);
		// RecsyscletemGuiHandler.assignGuiID(GuiTrashCan.class);
		// RecsyscletemGuiHandler.assignGuiID(GuiTrashDumpster.class);
		// RecsyscletemGuiHandler.assignGuiID(GuiHeatFurnace.class);
		//ModGui.assignGuiID(GuiToolBox.class);
		// RecsyscletemGuiHandler.assignGuiID(GuiTrashDumpsterIndustrial.class);
		// RecsyscletemGuiHandler.assignGuiID(GuiRemoteBlockMonitor.class);
		// RecsyscletemGuiHandler.assignGuiID(GuiAutoTrashCan.class);
		//
		// log.info("Register Models");
		// new ModelTrashCanSmall().bind(TileTrashCanSmall.class, new
		// TileTrashCanSmall(), blockTrashCanSmall);
		// new ModelTrashCan().bind(TileTrashCan.class, new TileTrashCan(),
		// blockTrashCan);
		// new ModelTrashDumpster().bind(TileTrashDumpster.class, new
		// TileTrashDumpster(), blockTrashDumpster);
		ModelToolbox2.register();
		new ModelToolbox().bind(new TileToolbox(), blockToolBox, blockToolBoxResis);
		// new ModelTrashDumpsterIndustrial().bind(TileTrashDumpsterIndustrial.class,
		// new TileTrashDumpsterIndustrial(),
		// blockTrashDumpsterIndustrial);
		//
		// log.info("Register Special Renderers");
		// RenderingRegistry.registerBlockHandler(new
		// BlockTrashDumpsterPart.SpecialRenderer());
		// RenderingRegistry.registerBlockHandler(new
		// BlockTrashDumpsterIndustrialPart.SpecialRenderer());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		// TODO Auto-generated method stub
		super.init(event);
		PistonToolbox.log.info("Launching CLIENT");

		// Recsyscletem.log.info("Loading Commands");
		// ClientCommandHandler.instance.registerCommand(new CommandRecsyscletem());
		// Recsyscletem.log.info("Finished.");
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		// if (Recsyscletem.cfg.getB("versionChange") ||
		// Recsyscletem.cfg.getB("versionGen"))
		// MinecraftForge.EVENT_BUS.register(new VersionUpdateEventHandler());

	}

	@Override
	public void identify() {
		PistonToolbox.log.info("Recsyscletem 2 is launching on the CLIENT side");
	}

	@Override
	public void finish() {
		PistonToolbox.log.info("Recsyscletem 2 is loaded on the CLIENT side");
	}

}
