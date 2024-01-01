package com.tntp.tntptool.container;

import com.tntp.tntptool.tileentity.TileAbstract;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerAbstract<TILE extends TileAbstract> extends Container {
	protected TILE machine;
	protected int machineSlots;

	public ContainerAbstract(TILE machineInv, IInventory playerInventory, int playerInvX, int playerInvY) {
		machine = machineInv;
		setupMachineSlots();
		machineSlots = machine.getSizeInventory();
		machine.openInventory();
		for (int k = 0; k < 3; k++) {
			for (int j = 0; j < 9; j++) {
				this.addSlotToContainer(
						new Slot(playerInventory, j + 9 + k * 9, playerInvX + j * 18, playerInvY + k * 18));
			}
		}
		for (int j = 0; j < 9; j++) {
			this.addSlotToContainer(new Slot(playerInventory, j, playerInvX + j * 18, 58 + playerInvY));
		}
	}

	public abstract void setupMachineSlots();

	

}
