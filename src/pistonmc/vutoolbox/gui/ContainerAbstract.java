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

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return machine.isUseableByPlayer(player);
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		machine.closeInventory();
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or you
	 * will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < machineSlots) {
				if (!this.mergeItemStack(itemstack1, machineSlots, this.inventorySlots.size(), true)) {
					return null;
				}
			} else {
				int start = 0;
				int end = machineSlots;
				if (!this.mergeItemStack(itemstack1, start, end, false)) {
					return null;
				}
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	/**
	 * merges provided ItemStack with the first avaliable one in the
	 * container/player inventory
	 */
	// add slot validity check
	// add slot stack limit check
	@Override
	protected boolean mergeItemStack(ItemStack stack, int start, int end, boolean increasingOrder) {
		boolean putIn = false;
		int k = start;

		if (increasingOrder) {
			k = end - 1;
		}

		Slot slot;
		ItemStack itemstack1;

		if (stack.isStackable()) {
			while (stack.stackSize > 0 && (!increasingOrder && k < end || increasingOrder && k >= start)) {
				slot = (Slot) this.inventorySlots.get(k);
				itemstack1 = slot.getStack();
				if (itemstack1 != null && itemstack1.getItem() == stack.getItem()
						&& (!stack.getHasSubtypes() || stack.getItemDamage() == itemstack1.getItemDamage())
						&& ItemStack.areItemStackTagsEqual(stack, itemstack1)) {
					int l = itemstack1.stackSize + stack.stackSize;
					int maxSize = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());// add: respect slot limit

					if (l <= maxSize) {
						stack.stackSize = 0;
						itemstack1.stackSize = l;
						slot.onSlotChanged();
						putIn = true;
					} else if (itemstack1.stackSize < maxSize) {
						stack.stackSize -= maxSize - itemstack1.stackSize;
						itemstack1.stackSize = maxSize;
						slot.onSlotChanged();
						putIn = true;
					}
				}

				if (increasingOrder) {
					--k;
				} else {
					++k;
				}
			}
		}

		if (stack.stackSize > 0) {
			if (increasingOrder) {
				k = end - 1;
			} else {
				k = start;
			}

			while (stack.stackSize > 0 && (!increasingOrder && k < end || increasingOrder && k >= start)) {
				slot = (Slot) this.inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (itemstack1 == null && slot.isItemValid(stack))// add
				{
					ItemStack putStack = stack.copy();
					int putSize = Math.min(slot.getSlotStackLimit(), stack.stackSize);
					putStack.stackSize = putSize;
					slot.putStack(putStack);
					slot.onSlotChanged();
					stack.stackSize -= putSize;
					putIn = true;
					break;
				}

				if (increasingOrder) {
					--k;
				} else {
					++k;
				}
			}
		}

		return putIn;
	}

}
