package com.tntp.tntptool;

import java.io.IOException;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import com.tntp.tntptool.proxy.LaunchProxy;
import com.tntp.tntptool.tileentity.TileToolBox;

//import com.tntp.recsyscletem.api.proxy.LaunchProxy;
//import com.tntp.recsyscletem.config.RS2Config;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

public class PistonToolbox {
	public static final String MODID = "tntptool";
	public static final String VERSION = "1.7.10-2.2.2.0.a1";

	public static ModConfig cfg;

	@SidedProxy(clientSide = "com.tntp.tntptool.proxy.LaunchProxyClient", serverSide = "com.tntp.tntptool.proxy.LaunchProxyServer")
	public static LaunchProxy PROXY;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		PROXY.identify();
		PROXY.preInit(event);
		// log.info("Pre Init");
		// log.info("Load Config");

		//
		// log.info("Register Blocks");
		//
		// // GameRegistry.registerBlock(new BlockCreativeEMF(), "blockCreativeEMF");
		//
		// GameRegistry.registerBlock(new BlockRecycler(), ItemBlockTooltip.class,
		// "blockRecycler");
		// GameRegistry.registerBlock(new BlockIncinerator(), ItemBlockTooltip.class,
		// "blockIncinerator");
		// GameRegistry.registerBlock(new BlockComposter(), ItemBlockTooltip.class,
		// "blockComposter");
		// GameRegistry.registerBlock(new BlockPacker(), ItemBlockTooltip.class,
		// "blockPacker");
		// GameRegistry.registerBlock(new BlockMatcher(), "blockMatcher");
		// GameRegistry.registerBlock(new BlockReceiverBox(), "blockReceiverBox");
		// GameRegistry.registerBlock(new BlockRouter(), ItemBlockRouter.class,
		// "blockRouter");
		// BlockAbstract blockTrashCanSmall = new BlockTrashCanSmall();
		// GameRegistry.registerBlock(blockTrashCanSmall, "blockTrashCanSmall");
		// BlockAbstract blockTrashCan = new BlockTrashCan();
		// GameRegistry.registerBlock(blockTrashCan, "blockTrashCan");
		// GameRegistry.registerBlock(new BlockTrashDumpsterPart(),
		// ItemBlockTooltip.class, "blockTrashDumpsterPart");
		// BlockAbstract blockTrashDumpster = new BlockTrashDumpster();
		// GameRegistry.registerBlock(blockTrashDumpster, "blockTrashDumpster");
		// GameRegistry.registerBlock(new BlockCompostPile(), "blockCompostPile");
		// GameRegistry.registerBlock(new BlockDecayingCompostPile(),
		// "blockDecayingCompostPile");
		// GameRegistry.registerBlock(new BlockConductiveCasing(),
		// "blockConductiveCasing");
		// GameRegistry.registerBlock(new BlockHeatFurnace(), ItemBlockTooltip.class,
		// "blockHeatFurnace");
		// for (int i = 0; i < BlockMonitor.STYLES.length; i++)
		// GameRegistry.registerBlock(new BlockMonitor(i), ItemBlockTooltip.class,
		// "blockMonitor" + i);
		// GameRegistry.registerBlock(new BlockCompressor(), ItemBlockTooltip.class,
		// "blockCompressor");
		// BlockAbstract blockToolBox = new BlockToolBox(false);
		// BlockAbstract blockToolBoxResis = new BlockToolBox(true);
		// GameRegistry.registerBlock(blockToolBox, ItemBlockTooltip.class,
		// "blockToolBox");
		// GameRegistry.registerBlock(blockToolBoxResis, ItemBlockTooltip.class,
		// "blockToolBoxResis");
		// BlockAbstract blockTrashDumpsterIndustrial = new
		// BlockTrashDumpsterIndustrial();
		// GameRegistry.registerBlock(blockTrashDumpsterIndustrial,
		// "blockTrashDumpsterIndustrial");
		// GameRegistry.registerBlock(new BlockTrashDumpsterIndustrialPart(),
		// ItemBlockTooltip.class,
		// "blockTrashDumpsterIndustrialPart");
		// GameRegistry.registerBlock(new BlockRemoteBlockMonitor(),
		// "blockRemoteBlockMonitor");
		// GameRegistry.registerBlock(new BlockRedstoneDisplay(),
		// "blockRedstoneDisplay");
		// GameRegistry.registerBlock(new BlockMonitorSignalReceptor(),
		// "blockMonitorSignalReceptor");
		// GameRegistry.registerBlock(new BlockAutoTrashCan(), "blockAutoTrashCan");
		// GameRegistry.registerBlock(new BlockAdvancedMachine(),
		// ItemBlockAdvancedMachine.class, "blockAdvancedMachine");
		//
		// GameRegistry.registerBlock(new BlockTestBattery(), "blockTestBattery");
		// GameRegistry.registerBlock(new BlockTestResistor(false),
		// "blockTestResistorOff");
		// GameRegistry.registerBlock(new BlockTestResistor(true),
		// "blockTestResistorOn");
		//

		//

		//
		//
		//
		// GameRegistry.registerItem(new ItemGarbage(), "itemGarbage");
		// GameRegistry.registerItem(new ItemWireless(), "itemWireless");
		// GameRegistry.registerItem(new ItemDictionary(), "itemRecycleDictionary");
		// GameRegistry.registerItem(new ItemMonitorCard(), "itemMonitorCard");
		// GameRegistry.registerItem(new ItemChipset(), "itemChipset");
		// GameRegistry.registerItem(new ItemLocator(), "itemLocator");
		// GameRegistry.registerItem(new ItemGarbageProcessed(),
		// "itemGarbageProcessed");
		// log.info("Register Tile Entities");
		// // GameRegistry.registerTileEntity(TileCreativeEMF.class, "tileCreativeEMF");
		// GameRegistry.registerTileEntity(TileRecycler.class, "tileRecycler");
		// GameRegistry.registerTileEntity(TileComposter.class, "tileComposter");
		// GameRegistry.registerTileEntity(TileIncinerator.class, "tileIncinerator");
		// GameRegistry.registerTileEntity(TilePacker.class, "tilePacker");
		// GameRegistry.registerTileEntity(TileMatcher.class, "tileMatcher");
		// GameRegistry.registerTileEntity(TileReceiverBox.class, "tileReceiverBox");
		// GameRegistry.registerTileEntity(TileRouterLSR.class, "tileRouterLSR");
		// GameRegistry.registerTileEntity(TileRouterMSR.class, "tileRouterMSR");
		// GameRegistry.registerTileEntity(TileRouterHSR.class, "tileRouterHSR");
		// GameRegistry.registerTileEntity(TileRouterLSS.class, "tileRouterLSS");
		// GameRegistry.registerTileEntity(TileRouterMSS.class, "tileRouterMSS");
		// GameRegistry.registerTileEntity(TileRouterHSS.class, "tileRouterHSS");
		// GameRegistry.registerTileEntity(TileTrashCanSmall.class,
		// "tileTrashCanSmall");
		// GameRegistry.registerTileEntity(TileTrashCan.class, "tileTrashCan");
		// GameRegistry.registerTileEntity(TileTrashDumpster.class,
		// "tileTrashDumpster");
		// GameRegistry.registerTileEntity(TileHeatFurnace.class, "tileHeatFurnace");
		// GameRegistry.registerTileEntity(TileCompressor.class, "tileCompressor");

		// GameRegistry.registerTileEntity(TileTrashDumpsterIndustrial.class,
		// "tileTrashDumpsterIndustrial");
		// GameRegistry.registerTileEntity(TileRemoteBlockMonitor.class,
		// "tileRemoteBlockMonitor");
		// GameRegistry.registerTileEntity(TileAutoTrashCan.class, "tileAutoTrashCan");
		//
		// GameRegistry.registerTileEntity(TileWire.class, "tileWire");
		// GameRegistry.registerTileEntity(TileBatteryTest.class, "tileBatteryTest");
		// GameRegistry.registerTileEntity(TileResistorTest.class, "tileResistorTest");
		//

	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		PROXY.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		PROXY.postInit(event);
		PROXY.finish();
	}



	@Deprecated
	public static boolean isItemMatchable(ItemStack itemStack) {
		if (itemStack == null)
			return false;
		Item item = itemStack.getItem();
		// if (item == RS2Items.itemWireless && itemStack.getItemDamage() == 0)
		// return true;
		return false;
	}

	public static String produceChannelId(String name, int x, int y, int z) {
		// time name coord rand
		StringBuilder build = new StringBuilder();
		long time = System.currentTimeMillis();
		build.append(Long.toHexString(time).toUpperCase());
		build.append('-');
		int hash;
		if (name.length() > 0) {
			hash = name.hashCode();
		} else {
			hash = UNIMPORTANT.nextInt();
		}
		build.append(Integer.toHexString(hash).toUpperCase());
		build.append('-');
		build.append(Integer.toHexString(x).toUpperCase()).append(Integer.toHexString(y).toUpperCase())
				.append(Integer.toHexString(z).toUpperCase());
		build.append('-');
		build.append(Integer.toHexString(MATCH.nextInt(256)).toUpperCase());
		return build.toString();
	}




}
