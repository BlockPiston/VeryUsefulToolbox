package pistonmc.vutoolbox.gui;

import com.tntp.tntptool.tileentity.TileToolBox;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ContainerToolBox2 extends ContainerAbstract<TileToolbox> {
	private int[] lastInfCount;
	private int[] lastInfMetadata;
	private short lastUpgrades;
	private int lastInfUpgrade;

	public ContainerToolBox2(TileToolbox tile, IInventory playerInventory) {
		super(tile, playerInventory, 11, 132);
		lastInfCount = new int[2];
		lastInfMetadata = new int[2];
	}

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

	@Override
	public void setupMachineSlots() {
		int i = 0;
		for (int j = 0; j < 9; j++) {
			this.addSlotToContainer(new SlotToolBoxUnstackable(machine, i, 33 + j * 18, 21));
			i++;
		}
		for (int k = 0; k < 3; k++) {
			for (int j = 0; j < 5; j++) {
				this.addSlotToContainer(new SlotToolBox(machine, i, 11 + j * 18, 42 + k * 18));
				i++;
			}
		}
		for (int k = 0; k < 3; k++) {
			for (int j = 0; j < 3; j++) {
				this.addSlotToContainer(new SlotToolBox(machine, i, 103 + j * 18, 42 + k * 18));
				i++;
			}
		}
		for (int k = 0; k < 3; k++) {
			for (int j = 0; j < 3; j++) {
				if (machine.hasStorageUpgrade())
					this.addSlotToContainer(new SlotToolBoxStackable(machine, i, 159 + j * 18, 42 + k * 18));
				else
					this.addSlotToContainer(new SlotToolBox(machine, i, 159 + j * 18, 42 + k * 18));
				i++;
			}
		}
		for (int j = 0; j < 9; j++) {
			this.addSlotToContainer(new SlotToolBoxStackable(machine, i, 11 + j * 18, 99));
			i++;
		}

		for (int k = 0; k < 3; k++) {
			for (int j = 0; j < 2; j++) {
				this.addSlotToContainer(new SlotToolBox(machine, i, 177 + j * 18, 154 + k * 18));
				i++;
			}
		}
		for (int j = 0; j < 2; j++) {
			this.addSlotToContainer(new SlotToolBoxStackable(machine, i, 177 + j * 18, 99));
			i++;
		}
		for (int j = 0; j < 2; j++) {
			this.addSlotToContainer(new SlotOutput(machine, i, 177 + j * 18, 135));
			i++;
		}
	}

}
