//package com.tntp.tntptool;
//
//import java.io.IOException;
//import java.util.Random;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.lwjgl.input.Keyboard;
//
//import com.tntp.tntptool.proxy.LaunchProxy;
//import com.tntp.tntptool.tileentity.TileToolBox;
//
////import com.tntp.recsyscletem.api.proxy.LaunchProxy;
////import com.tntp.recsyscletem.config.RS2Config;
//
//import cpw.mods.fml.common.Mod;
//import cpw.mods.fml.common.Mod.EventHandler;
//import cpw.mods.fml.common.SidedProxy;
//import cpw.mods.fml.common.event.FMLInitializationEvent;
//import cpw.mods.fml.common.event.FMLPostInitializationEvent;
//import cpw.mods.fml.common.event.FMLPreInitializationEvent;
//import cpw.mods.fml.common.registry.GameRegistry;
//import io.netty.buffer.ByteBuf;
//import net.minecraft.client.Minecraft;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.CompressedStreamTools;
//import net.minecraft.nbt.NBTSizeTracker;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.util.ChatComponentText;
//
//public class PistonToolbox {
//	public static final String MODID = "tntptool";
//	public static final String VERSION = "1.7.10-2.2.2.0.a1";
//
//	public static ModConfig cfg;
//
//	@SidedProxy(clientSide = "com.tntp.tntptool.proxy.LaunchProxyClient", serverSide = "com.tntp.tntptool.proxy.LaunchProxyServer")
//	public static LaunchProxy PROXY;
//
//	@EventHandler
//	public void preInit(FMLPreInitializationEvent event) {
//		
//
//	}
//
//	@EventHandler
//	public void init(FMLInitializationEvent event) {
//		PROXY.init(event);
//	}
//
//	@EventHandler
//	public void postInit(FMLPostInitializationEvent event) {
//		PROXY.postInit(event);
//		PROXY.finish();
//	}
//
////	public static String produceChannelId(String name, int x, int y, int z) {
////		// time name coord rand
////		StringBuilder build = new StringBuilder();
////		long time = System.currentTimeMillis();
////		build.append(Long.toHexString(time).toUpperCase());
////		build.append('-');
////		int hash;
////		if (name.length() > 0) {
////			hash = name.hashCode();
////		} else {
////			hash = UNIMPORTANT.nextInt();
////		}
////		build.append(Integer.toHexString(hash).toUpperCase());
////		build.append('-');
////		build.append(Integer.toHexString(x).toUpperCase()).append(Integer.toHexString(y).toUpperCase())
////				.append(Integer.toHexString(z).toUpperCase());
////		build.append('-');
////		build.append(Integer.toHexString(MATCH.nextInt(256)).toUpperCase());
////		return build.toString();
////	}
////
//
//
//
//}
