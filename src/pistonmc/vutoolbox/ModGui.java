package pistonmc.vutoolbox;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pistonmc.vutoolbox.gui.ContainerToolbox;
import pistonmc.vutoolbox.gui.GuiToolBox;
import pistonmc.vutoolbox.object.TileToolbox;

public class ModGui implements IGuiHandler {
	public static final int GUI_ID_TOOLBOX = 1; /* arbitrary */

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		IInventory playerInventory = player.inventory;
		TileEntity tile = world.getTileEntity(x, y, z);

		if (id == GUI_ID_TOOLBOX) {
			if (tile instanceof TileToolbox) {
				return new ContainerToolbox((TileToolbox) tile, playerInventory);
			}
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

		IInventory playerInventory = player.inventory;
		TileEntity tile = world.getTileEntity(x, y, z);

		if (ID == GUI_ID_TOOLBOX) {
			if (tile instanceof TileToolbox) {
				return new GuiToolBox((TileToolbox) tile, playerInventory);
			}
		}

		return null;
	}

}
