package com.tntp.tntptool.gui;

import java.lang.reflect.Field;

import com.tntp.tntptool.container.ContainerToolBox;
import com.tntp.tntptool.tileentity.TileToolBox;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class RecsyscletemGuiHandler implements IGuiHandler {
	private static int nextID = 0;

	private static int getNextGuiID() {
		return nextID++;
	}

	public static void assignGuiID(Class<?> guiClass) {
		try {
			Field field = guiClass.getDeclaredField("guiID");
			field.setAccessible(true);
			field.setInt(null, getNextGuiID());
			return;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new RuntimeException("Fail");
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		IInventory playerInventory = player.inventory;
		TileEntity tile = world.getTileEntity(x, y, z);

//    if (ID == GuiRecycler.guiID) {
//      if (tile instanceof TileRecycler) {
//        return new ContainerRecycler((TileRecycler) tile, playerInventory);
//      }
//    } else if (ID == GuiComposter.guiID) {
//      if (tile instanceof TileComposter) {
//        return new ContainerComposter((TileComposter) tile, playerInventory);
//      }
//    } else if (ID == GuiIncinerator.guiID) {
//      if (tile instanceof TileIncinerator) {
//        return new ContainerIncinerator((TileIncinerator) tile, playerInventory);
//      }
//    } else if (ID == GuiPacker.guiID) {
//      if (tile instanceof TilePacker) {
//        return new ContainerPacker((TilePacker) tile, playerInventory);
//      }
//    } else if (ID == GuiMatcher.guiID) {
//      if (tile instanceof TileMatcher) {
//        return new ContainerMatcher((TileMatcher) tile, playerInventory);
//      }
//    } else if (ID == GuiReceiverBox.guiID) {
//      if (tile instanceof TileReceiverBox) {
//        return new ContainerReceiverBox((TileReceiverBox) tile, playerInventory);
//      }
//    } else if (ID == GuiRouter.guiID) {
//      if (tile instanceof TileRouter) {
//        return new ContainerRouter((TileRouter) tile, playerInventory);
//      }
//    } else if (ID == GuiTrashCanSmall.guiID) {
//      if (tile instanceof TileTrashCanSmall) {
//        return new ContainerTrashCanSmall((TileTrashCanSmall) tile, playerInventory);
//      }
//    } else if (ID == GuiTrashCan.guiID) {
//      if (tile instanceof TileTrashCan) {
//        return new ContainerTrashCan((TileTrashCan) tile, playerInventory);
//      }
//    } else if (ID == GuiTrashDumpster.guiID) {
//      if (tile instanceof TileTrashDumpster) {
//        return new ContainerTrashDumpster((TileTrashDumpster) tile, playerInventory);
//      }
//    } else if (ID == GuiHeatFurnace.guiID) {
//      if (tile instanceof TileHeatFurnace) {
//        return new ContainerHeatFurnace((TileHeatFurnace) tile, playerInventory);
//      }
//    } else

		if (ID == GuiToolBox.guiID) {
			if (tile instanceof TileToolbox) {
				return new ContainerToolBox((TileToolbox) tile, playerInventory);
			}
		}
//    	else if (ID == GuiTrashDumpsterIndustrial.guiID) {
//      if (tile instanceof TileTrashDumpsterIndustrial) {
//        return new ContainerTrashDumpsterIndustrial((TileTrashDumpsterIndustrial) tile, playerInventory);
//      }
//    } else if (ID == GuiRemoteBlockMonitor.guiID) {
//      if (tile instanceof TileRemoteBlockMonitor) {
//        return new ContainerRemoteBlockMonitor((TileRemoteBlockMonitor) tile, playerInventory);
//      }
//    } else if (ID == GuiAutoTrashCan.guiID) {
//      if (tile instanceof TileAutoTrashCan) {
//        return new ContainerAutoTrashCan((TileAutoTrashCan) tile, playerInventory);
//      }
//    }
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

		IInventory playerInventory = player.inventory;
		TileEntity tile = world.getTileEntity(x, y, z);

		if (ID == GuiToolBox.guiID) {
			if (tile instanceof TileToolbox) {
				return new GuiToolBox((TileToolbox) tile, playerInventory);
			}
		}

		return null;
	}

}
