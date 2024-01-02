package pistonmc.vutoolbox.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import pistonmc.vutoolbox.core.BigItemStack;
import pistonmc.vutoolbox.core.Toolbox;
import pistonmc.vutoolbox.core.Upgrades;
import pistonmc.vutoolbox.low.SlotLayouter;
import pistonmc.vutoolbox.object.TileToolbox;

public class ContainerToolbox extends Container {
	private TileToolbox tile;
//	
//	private int[] lastInfCount;
//	private int[] lastInfMetadata;
//	private short lastUpgrades;
//	private int lastInfUpgrade;

	public ContainerToolbox(TileToolbox tile, IInventory playerInventory) {
		this.tile = tile;
		setupSlots(playerInventory);
		
//		lastInfCount = new int[2];
//		lastInfMetadata = new int[2];
	}
	
	public void setupSlots(IInventory playerInventory) {
		SlotLayouter layout = new SlotLayouter();
		// top slots can only put unstackable items
		layout.anchorTo(33, 21, Toolbox.NUM_TOP_SLOTS);
		for (int i = 0; i < Toolbox.NUM_TOP_SLOTS; i++) {
			SlotToolbox slot = new SlotToolbox(tile, layout.getIndex(), layout.getX(), layout.getY());
			slot.setOnlyAllowUnstackable();
			this.addSlotToContainer(slot);
			layout.next();
		}
		
		layout.anchorTo(11, 42, 5);
		for (int i = 0; i < Toolbox.NUM_MIDDLE_LEFT_SLOTS; i++) {
			SlotToolbox slot = new SlotToolbox(tile, layout.getIndex(), layout.getX(), layout.getY());
			this.addSlotToContainer(slot);
			layout.next();
		}
		
		layout.anchorTo(103, 42, 3);
		for (int i = 0; i < Toolbox.NUM_MIDDLE_SLOTS; i++) {
			SlotToolbox slot = new SlotToolbox(tile, layout.getIndex(), layout.getX(), layout.getY());
			this.addSlotToContainer(slot);
			layout.next();
		}
		
		// middle right can be upgraded to allow stackable
		layout.anchorTo(159, 42, 3);
		boolean upgraded = tile.getToolbox().getUpgrades().isEnabled(Upgrades.STORAGE);
		for (int i = 0; i < Toolbox.NUM_MIDDLE_RIGHT_SLOTS; i++) {
			SlotToolbox slot = new SlotToolbox(tile, layout.getIndex(), layout.getX(), layout.getY());
			if (upgraded) {
				slot.setAllowStackable();
			}
			this.addSlotToContainer(slot);
			layout.next();
		}
		
		// bottom slots always allow stackable
		layout.anchorTo(11, 99, Toolbox.NUM_BOTTOM_SLOTS);
		for (int i = 0; i < Toolbox.NUM_BOTTOM_SLOTS; i++) {
			SlotToolbox slot = new SlotToolbox(tile, layout.getIndex(), layout.getX(), layout.getY());
			slot.setAllowStackable();
			this.addSlotToContainer(slot);
			layout.next();
		}
	
		// upgrade slots can actually hold any item
		layout.anchorTo(177, 154, 2);
		for (int i = 0; i < Toolbox.NUM_UPGRADE_SLOTS; i++) {
			SlotToolbox slot = new SlotToolbox(tile, layout.getIndex(), layout.getX(), layout.getY());
			this.addSlotToContainer(slot);
			layout.next();
		}

		// infinity input
		layout.anchorTo(177, 99, Toolbox.NUM_INFINITY_SLOTS);
		for (int i = 0; i < Toolbox.NUM_INFINITY_SLOTS; i++) {
			SlotToolbox slot = new SlotToolbox(tile, layout.getIndex(), layout.getX(), layout.getY());
			slot.setAllowStackable();
			this.addSlotToContainer(slot);
			layout.next();
		}
		// output
		layout.anchorTo(177, 135, Toolbox.NUM_INFINITY_SLOTS);
		for (int i = 0; i < 2; i++) {
			Slot slot = new SlotOutput(tile, layout.getIndex(), layout.getX(), layout.getY());
			this.addSlotToContainer(slot);
			layout.next();
		}
		
		// player inventory
		SlotLayouter playerLayout = new SlotLayouter();
		playerLayout.anchorTo(11, 190, 9);
		for (int i = 0; i < 9; i++) {
			Slot slot = new Slot(playerInventory, playerLayout.getIndex(), playerLayout.getX(), playerLayout.getY());
			this.addSlotToContainer(slot);
			playerLayout.next();
		}
		playerLayout.anchorTo(11, 132, 9);
		for (int i = 0; i < 27; i++) {
			Slot slot = new Slot(playerInventory, playerLayout.getIndex(), playerLayout.getX(), playerLayout.getY());
			this.addSlotToContainer(slot);
			playerLayout.next();
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
		Slot slot = (Slot) this.inventorySlots.get(index);
		if (slot == null || !slot.getHasStack()) {
			return null;
		}
		int machineSlots = tile.getSizeInventory();
		ItemStack itemstack = null;
		
		ItemStack slotStack = slot.getStack();
		itemstack = slotStack.copy();

		if (index < machineSlots) {
			if (!this.mergeItemStack(slotStack, machineSlots, this.inventorySlots.size(), true)) {
				return null;
			}
		} else {
			// I don't remember what this logic is meant to do when I wrote it
			// just keeping it like this unless I see something weird
			
			int start = -1;
			int end = -1;
			Toolbox toolbox = tile.getToolbox();
			
			int stackableStart = toolbox.getStackableSlotsStart();
			for (int i = 0; i < Toolbox.NUM_INFINITY_SLOTS; i++) {
				BigItemStack infSlot = toolbox.getInfinityStack(i);
				ItemStack infStack = infSlot.getItemStack();
				if (infStack != null && infSlot.getCount() < toolbox.getUpgrades().getInfinityStackLimit()) {
					if (infStack.isItemEqual(slotStack) && ItemStack.areItemStackTagsEqual(infStack, slotStack)) {
						start = Toolbox.INFINITY_SLOTS_START + i;
						end = start + 1;
						break;
					}
				}
			}
			if (start == -1) {
				if (slotStack.isStackable()) {
					start = stackableStart;
					end = Toolbox.UPGRADE_SLOTS_START;
				} else {
					start = Toolbox.NUM_TOP_SLOTS;
					end = machineSlots;
				}
			}
			if (!this.mergeItemStack(slotStack, start, end, false)) {
				if (start > stackableStart && slotStack.isStackable()) {
					// try stackable slots
					if (!this.mergeItemStack(slotStack, stackableStart, Toolbox.UPGRADE_SLOTS_START, false)) {
						if (!this.mergeItemStack(slotStack, Toolbox.NUM_TOP_SLOTS, machineSlots, false)) {
							return null;
						}
					}
				} else if (!this.mergeItemStack(slotStack, Toolbox.NUM_TOP_SLOTS, machineSlots, false)) {
					return null;
				}
			}
		}

		if (slotStack.stackSize == 0) {
			slot.putStack((ItemStack) null);
		} else {
			slot.onSlotChanged();
		}
		

		return itemstack;
	}

}
