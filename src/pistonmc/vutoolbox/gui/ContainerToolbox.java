package pistonmc.vutoolbox.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import pistonmc.vutoolbox.block.TileToolbox;
import pistonmc.vutoolbox.core.Toolbox;

public class ContainerToolbox extends Container {
	private TileToolbox tile;
	
	private int[] lastInfCount;
	private int[] lastInfMetadata;
	private short lastUpgrades;
	private int lastInfUpgrade;

	public ContainerToolbox(TileToolbox tile, IInventory playerInventory) {
		super(tile, playerInventory, 11, 132);
		this.tile = tile;
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
		lastInfCount = new int[2];
		lastInfMetadata = new int[2];
	}
	
	@Override
	public void setupMachineSlots() {
		int i = 0;
		// top slots can only put unstackable items
		for (int j = 0; j < Toolbox.NUM_TOP_SLOTS; j++) {
			SlotToolbox slot =
			this.addSlotToContainer(new SlotToolBoxUnstackable(machine, i, 33 + j * 18, 21));
			//setonlyallowunstackable
			i++;
		}
		for (int k = 0; k < 3; k++) {
			for (int j = 0; j < 5; j++) {
				this.addSlotToContainer(new SlotToolbox2(machine, i, 11 + j * 18, 42 + k * 18));
				i++;
			}
		}
		for (int k = 0; k < 3; k++) {
			for (int j = 0; j < 3; j++) {
				this.addSlotToContainer(new SlotToolbox2(machine, i, 103 + j * 18, 42 + k * 18));
				i++;
			}
		}
		for (int k = 0; k < 3; k++) {
			for (int j = 0; j < 3; j++) {
				if (machine.hasStorageUpgrade()) {
					this.addSlotToContainer(new SlotToolBoxStackable(machine, i, 159 + j * 18, 42 + k * 18));
					//set allow stackable
				}
					
				else {
					this.addSlotToContainer(new SlotToolbox2(machine, i, 159 + j * 18, 42 + k * 18));
				}
					
				i++;
			}
		}
		for (int j = 0; j < 9; j++) {
			this.addSlotToContainer(new SlotToolBoxStackable(machine, i, 11 + j * 18, 99));
			//set allow stackable
			i++;
		}
	
		for (int k = 0; k < 3; k++) {
			for (int j = 0; j < 2; j++) {
				this.addSlotToContainer(new SlotToolbox2(machine, i, 177 + j * 18, 154 + k * 18));
				i++;
			}
		}
		for (int j = 0; j < 2; j++) {
			this.addSlotToContainer(new SlotToolBoxStackable(machine, i, 177 + j * 18, 99));
			//set allow stackable
			i++;
		}
		for (int j = 0; j < 2; j++) {
			this.addSlotToContainer(new SlotOutput(machine, i, 177 + j * 18, 135));
			i++;
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tile.isUseableByPlayer(player);
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		tile.closeInventory();
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

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		int machineSlots = tile.getSizeInventory();
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

				int start = -1;
				int end = -1;
				int stackableStart = machine.hasStorageUpgrade() ? 33 : 42;
				for (int i = 0; i < 2; i++) {
					ItemStack inf = machine.getInfStorage(i);
					if (inf != null && machine.getInfCount(i) < machine.getInfLimit()) {
						if (inf.isItemEqual(itemstack1) && ItemStack.areItemStackTagsEqual(inf, itemstack1)) {
							start = 57 + i;
							end = 58 + i;
							break;
						}
					}
				}
				if (start == -1) {
					if (itemstack1.isStackable()) {
						start = stackableStart;
						end = 51;
					} else {
						start = 9;
						end = machineSlots;
					}
				}
				if (!this.mergeItemStack(itemstack1, start, end, false)) {
					if (start > stackableStart && itemstack1.isStackable()) {
						// try stackable slots
						if (!this.mergeItemStack(itemstack1, stackableStart, 51, false)) {
							if (!this.mergeItemStack(itemstack1, 9, machineSlots, false)) {
								return null;
							}
						}
					} else if (!this.mergeItemStack(itemstack1, 9, machineSlots, false)) {
						return null;
					}
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

	@Override
	public void addCraftingToCrafters(ICrafting craft) {
		super.addCraftingToCrafters(craft);
		craft.sendProgressBarUpdate(this, 0, machine.getInfCount(0));
		craft.sendProgressBarUpdate(this, 1, machine.getInfCount(1));
		for (int i = 0; i < 2; i++) {
			ItemStack is = machine.getInfStorage(i);
			int id = is == null ? -1 : Item.getIdFromItem(is.getItem());
			craft.sendProgressBarUpdate(this, 2 + i, id);
			if (is != null)
				craft.sendProgressBarUpdate(this, 4 + i, is.getItemDamage());
		}
		craft.sendProgressBarUpdate(this, 6, machine.getShortFromFourUpgrades());
		craft.sendProgressBarUpdate(this, 7, machine.getInfUpgrade());
	}

	@Override
	public void updateProgressBar(int bar, int progress) {
		if (bar <= 1) {
			machine.setInfStorageCount(bar, progress);
		} else if (bar <= 3) {
			ItemStack s = progress == -1 ? null : new ItemStack(Item.getItemById(progress));
			machine.setInfStorage(bar - 2, s);
		} else if (bar <= 5) {
			machine.setInfStorageMetadata(bar - 4, progress);
		} else if (bar == 6) {
			machine.setFourUpgradesFromShort((short) progress);
		} else if (bar == 7) {
			machine.setInfUpgrade(progress);
		}

	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (Object obj : this.crafters) {
			ICrafting c = (ICrafting) obj;
			for (int i = 0; i < 2; i++) {
				if (lastInfCount[i] != machine.getInfCount(i)) {
					c.sendProgressBarUpdate(this, i, machine.getInfCount(i));
				}
				if (lastInfMetadata[i] != machine.getInfStorageMetadata(i)) {
					c.sendProgressBarUpdate(this, i + 4, machine.getInfStorageMetadata(i));
				}
			}
			if (lastUpgrades != machine.getShortFromFourUpgrades()) {
				c.sendProgressBarUpdate(this, 6, machine.getShortFromFourUpgrades());
			}
			if (lastInfUpgrade != machine.getInfUpgrade()) {
				c.sendProgressBarUpdate(this, 7, machine.getInfUpgrade());
			}
		}
		for (int i = 0; i < 2; i++) {
			lastInfCount[i] = machine.getInfCount(i);
			lastInfMetadata[i] = machine.getInfStorageMetadata(i);
		}
		lastUpgrades = machine.getShortFromFourUpgrades();
		lastInfUpgrade = machine.getInfUpgrade();
	}

}
