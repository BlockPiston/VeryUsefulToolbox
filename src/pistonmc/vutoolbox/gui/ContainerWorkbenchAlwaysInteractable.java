package pistonmc.vutoolbox.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.world.World;

/**
 * Crating table container that is always interactable
 */
public class ContainerWorkbenchAlwaysInteractable extends ContainerWorkbench {

	public ContainerWorkbenchAlwaysInteractable(InventoryPlayer inventoryPlayer, World world, int x, int y,
			int z) {
		super(inventoryPlayer, world, x, y, z);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

}
