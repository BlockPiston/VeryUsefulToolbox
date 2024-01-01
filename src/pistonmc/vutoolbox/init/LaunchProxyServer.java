package com.tntp.tntptool.proxy;

import com.tntp.tntptool.PistonToolbox;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;

public class LaunchProxyServer extends LaunchProxyCommon {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);

	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		// Recsyscletem.log.info("Launching SERVER");

		// Recsyscletem.log.info("Loading Commands");
		// ((CommandHandler)
		// (MinecraftServer.getServer().getCommandManager())).registerCommand(new
		// CommandRecsyscletem());
		// Recsyscletem.log.info("Finished.");

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);

	}

	@Override
	public void identify() {
		PistonToolbox.log.info("Recsyscletem 2 is launching on the SERVER side");
	}

	@Override
	public void finish() {
		PistonToolbox.log.info("Recsyscletem 2 is loaded on the SERVER side");
	}

}
