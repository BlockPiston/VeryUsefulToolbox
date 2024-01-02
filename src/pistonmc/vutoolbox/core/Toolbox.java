package pistonmc.vutoolbox.core;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import pistonmc.vutoolbox.ModObjects;
import pistonmc.vutoolbox.ModUtils;
import pistonmc.vutoolbox.low.NBTToolbox;
import pistonmc.vutoolbox.object.ItemChipset;

/**
 * The core Toolbox logic
 */
public class Toolbox {
	public static final int NUM_TOP_SLOTS = 9;
	public static final int NUM_MIDDLE_LEFT_SLOTS = 15;
	public static final int NUM_MIDDLE_SLOTS = 9;
	public static final int NUM_MIDDLE_RIGHT_SLOTS = 9;
	public static final int NUM_BOTTOM_SLOTS = 9;
	// @formatter:off
	public static final int UPGRADE_SLOTS_START =
		NUM_TOP_SLOTS + 
		NUM_MIDDLE_LEFT_SLOTS +
		NUM_MIDDLE_SLOTS +
		NUM_MIDDLE_RIGHT_SLOTS + 
		NUM_BOTTOM_SLOTS;
	// @formatter:on
	
	public static final int NUM_UPGRADE_SLOTS = 6;
	
	public static final int INFINITY_SLOTS_START = UPGRADE_SLOTS_START + NUM_UPGRADE_SLOTS;
	public static final int NUM_INFINITY_SLOTS = 2;
	// @formatter:off
	public static final int NUM_TOTAL_SLOTS = 
		NUM_TOP_SLOTS +
		NUM_MIDDLE_LEFT_SLOTS +
		NUM_MIDDLE_SLOTS +
		NUM_MIDDLE_RIGHT_SLOTS +
		NUM_BOTTOM_SLOTS +
		NUM_UPGRADE_SLOTS +
		NUM_INFINITY_SLOTS * 2; // times 2 for input and output
	// @formatter:on
	
	// @formatter:off
	private static final int AUTOMATION_SLOT_START = 
		NUM_TOP_SLOTS +
		NUM_MIDDLE_LEFT_SLOTS +
		NUM_MIDDLE_SLOTS +
		NUM_BOTTOM_SLOTS;
	// @formatter:on
	public static final int[] AUTOMATION_SLOTS;
	static {
		AUTOMATION_SLOTS = new int[NUM_BOTTOM_SLOTS];
		for (int i = 0; i < AUTOMATION_SLOTS.length; i++) {
			AUTOMATION_SLOTS[i] = AUTOMATION_SLOT_START + i;
		}
	}
	public static boolean isAutomationSlot(int slot) {
		return slot >= AUTOMATION_SLOT_START && slot < AUTOMATION_SLOT_START + NUM_BOTTOM_SLOTS;
	}
	
	private ItemStack[] slots;
	private BigItemStack[] infinitySlots;
	private ToolboxStatus status;
	
	public Toolbox() {
		slots = new ItemStack[NUM_TOTAL_SLOTS];
		infinitySlots = new BigItemStack[NUM_INFINITY_SLOTS];
		for (int i = 0; i<NUM_INFINITY_SLOTS; i++) {
			infinitySlots[i] = new BigItemStack();
		}
		status = new ToolboxStatus();
	}
	
	public ItemStack getStackInSlot(int slot) {
		return slots[slot];
	}
	
	public ItemStack decrStackSize(int slot, int requestCount) {
		return ModUtils.decrStackSize(slots, slot, requestCount);
	}
	
	/**
	 * Take out the slot, leaving null in its place
	 * @param slot (within bound)
	 * @return
	 */
	public ItemStack takeStack(int slot) {
		ItemStack stack = slots[slot];
		slots[slot] = null;
		return stack;
	}
	
	public void setSlot(int slot, ItemStack stack) {
		slots[slot] = stack;
	}
	
	public BigItemStack getInfinityStack(int slot) {
		return infinitySlots[slot];
	}
	
	/**
	 * Attempt to add to infinity stacks. Will decrease stack size of input stack on success
	 * @param stack
	 */
	public void addToInfinityStack(ItemStack stack) {
		int infLimit = status.getUpgrades().getInfinityStackLimit();
		for (int i = 0; i<infinitySlots.length && stack.stackSize > 0; i++) {
			BigItemStack slot = getInfinityStack(i);
			ItemStack stackInSlot = slot.getItemStack();
			// can only add if slot already has that item
			if (stackInSlot == null) {
				continue;
			}
			if (!stack.isItemEqual(stackInSlot)) {
				continue;
			}
			if (!ItemStack.areItemStackTagsEqual(stack, stackInSlot)) {
				continue;
			}
			int count = slot.getCount();
			if (count >= infLimit) {
				continue;
			}
			int putIn = Math.min(stack.stackSize, infLimit - count);
			slot.addCount(putIn);
			stack.stackSize -= putIn;
			if (stack.stackSize <= 0) {
				break;
			}
		}
	}
	
	public int getStackableSlotsStart() {
		int start = NUM_TOP_SLOTS + NUM_MIDDLE_LEFT_SLOTS + NUM_MIDDLE_SLOTS;
		if (status.getUpgrades().isEnabled(Upgrades.STORAGE)) {
			return start;
		}
		return start + NUM_MIDDLE_RIGHT_SLOTS;
	}
		
	public boolean canUse(UUID uuid) {
		return status.canUse(uuid);
	}
	
    /**
     * Set the player as the owner
     *
     * If the entity is not an EntityPlayer, or is null, the owner is removed
     */
    public void setOwner(Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            status.setOwner(player.getDisplayName(), player.getUniqueID());
        } else {
            status.setOwner(null, null);
        }
    }
    
    public String getOwner() {
    	return status.getOwner();
    }
    
    public void setCustomName(String name) {
    	status.setCustomName(name);
    }
    
    public String getCustomName() {
    	return status.getCustomName();
    }
    
	public Upgrades getUpgrades() {
		return status.getUpgrades();
	}
	
	/**
	 * Detect upgrade items in the upgrade slots
	 */
	public void detectUpgrades() {
		Upgrades upgrades = status.getUpgrades();
		upgrades.clear();
		int infinite = 0;

		for (int i = UPGRADE_SLOTS_START; i < UPGRADE_SLOTS_START + NUM_UPGRADE_SLOTS; i++) {
			ItemStack stack = getStackInSlot(i);
			if (stack == null) {
				continue;
			}

			Item item = stack.getItem();
			if (item != ModObjects.itemChipset) {
				continue;
			}
			int meta = stack.getItemDamage();
			switch (meta) {
				case ItemChipset.METADATA_CRAFT_UPGRADE:
					upgrades.set(Upgrades.CRAFT, true);
					break;
				case ItemChipset.METADATA_INFINITY_UPGRADE:
					infinite++;
					break;
				case ItemChipset.METADATA_RESIS_UPGRADE:
					upgrades.set(Upgrades.RESIS, true);
					break;
				case ItemChipset.METADATA_STORAGE_UPGRADE:
					upgrades.set(Upgrades.STORAGE, true);
					break;
				case ItemChipset.METADATA_SECURITY_UPGRADE:
					upgrades.set(Upgrades.SECURITY, true);
					break;
				case ItemChipset.METADATA_PICKUP_UPGRADE:
					upgrades.set(Upgrades.PICKUP, true);
					break;
				default:
					break;
			}

		}
		upgrades.setInfinityCount(Math.min(3, infinite));
	}
	
	/**
	 * Process infinity input and output slots
	 */
	public boolean processInfinitySlots() {
		boolean changed = false;
		for (int i = 0; i < NUM_INFINITY_SLOTS; i++) {
			int indexIn = INFINITY_SLOTS_START + i;
			int indexOut = indexIn + NUM_INFINITY_SLOTS;
			if (processInfinityInput(i, indexIn)) {
				changed = true;
			}
			if (processInfinityOutput(i, indexOut)) {
				changed = true;
			}
		}
		return changed;
	}
	
	private boolean processInfinityInput(int infSlot, int inputSlot) {
		ItemStack inputStack = slots[inputSlot];
		if (inputStack == null) {
			// no input to process
			return false;
		}
		int infLimit = status.getUpgrades().getInfinityStackLimit();
		BigItemStack infStack = infinitySlots[infSlot];
		ItemStack currentInfItem = infStack.getItemStack();
		int currentInfCount = infStack.getCount();
		if (currentInfItem == null) {
			// inf slot is empty, put in the stack
			int putIn = Math.min(inputStack.stackSize, infLimit);
			inputStack.stackSize -= putIn;
			infStack.set(inputStack, putIn);
		} else {
			if (!inputStack.isItemEqual(currentInfItem)) {
				return false;
			}
			if (!ItemStack.areItemStackTagsEqual(inputStack, currentInfItem)) {
				return false;
			}
			int putIn = Math.min(inputStack.stackSize, infLimit - currentInfCount);
			inputStack.stackSize -= putIn;
			infStack.addCount(putIn);
		}
		if (inputStack.stackSize == 0) {
			slots[inputSlot] = null;
		}
		return true;
	}
	
	private boolean processInfinityOutput(int infSlot, int outputSlot) {
		BigItemStack infStack = infinitySlots[infSlot];
		ItemStack currentInfItem = infStack.getItemStack();
		if (currentInfItem == null) {
			// no item to output
			return false;
		}
		ItemStack outputStack = slots[outputSlot];
		if (outputStack == null) {
			outputStack = currentInfItem.copy();
			int takeOut = Math.min(outputStack.getMaxStackSize(), infStack.getCount());
			infStack.addCount(takeOut);
			outputStack.stackSize = takeOut;
			slots[outputSlot] = outputStack;
			return true;
		}
		
		if (!outputStack.isItemEqual(currentInfItem)) {
			return false;
		}
		if (!ItemStack.areItemStackTagsEqual(outputStack, currentInfItem)) {
			return false;
		}
		int maxStackSize = outputStack.getMaxStackSize();
		if (outputStack.stackSize >= maxStackSize) {
			return false;
		}
		
		int takeOut = Math.min(infStack.getCount(), maxStackSize - outputStack.stackSize);
		infStack.addCount(takeOut);
		outputStack.stackSize += takeOut;
		return true;
	}
	
	/**
	 * Write content to a NBT tag
	 * @param tag (non-null)
	 */
	public void writeToNBT(NBTToolbox tagToolbox) {
		tagToolbox.writeItems(slots);
		tagToolbox.writeInfinityItems(infinitySlots);
		status.writeToNBT(tagToolbox);
	}
	
	public void readFromNBT(NBTToolbox tagToolbox) {
		for (int i = 0; i< NUM_TOTAL_SLOTS;i++) {
			slots[i] = null;
		}
		tagToolbox.readItemsInto(slots);
		tagToolbox.readInfinityItemsInto(infinitySlots);
		status.readFromNBT(tagToolbox);
	}

}
