package pistonmc.vutoolbox.core;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import pistonmc.vutoolbox.ModUtils;
import pistonmc.vutoolbox.low.NBTToolbox;

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
	
	public void detectUpgrades() {
		boolean craft = false;
		boolean storage = false;
		boolean resis = false;
		boolean security = false;
		boolean pickup = false;
		int infinite = 0;

		for (int i = 51; i < 57; i++) {
			ItemStack is = getStackInSlot(i);
			if (is != null) {
				if (is.getItem() == RS2Items.itemChipset && is.getItemDamage() == 3) {
					craft = true;
				} else if (is.getItem() == RS2Items.itemChipset && is.getItemDamage() == 9) {
					storage = true;
				} else if (is.getItem() == RS2Items.itemChipset && is.getItemDamage() == 7) {
					resis = true;
				} else if (is.getItem() == RS2Items.itemChipset && is.getItemDamage() == 11) {
					security = true;
				} else if (is.getItem() == RS2Items.itemChipset && is.getItemDamage() == 13) {
					pickup = true;
				} else if (is.getItem() == RS2Items.itemChipset && is.getItemDamage() == 5) {
					infinite++;
				}
			}
		}
		craftUpgrade = craft;
		storageUpgrade = storage;
		explosionUpgrade = resis;
		securityUpgrade = security;
		pickupUpgrade = pickup;
		infiniteUpgrade = Math.min(3, infinite);
	}
	
	public void processInfinitySlots() {
		for (int i = 0; i < infiniteStacks.length; i++) {
			// check in
			ItemStack in = getStackInSlot(INF_IN[i]);
			if (in == null)
				continue;
			if (infiniteStacks[i] == null) {
				infiniteStacks[i] = in.copy();
				infiniteCount[i] = 0;
			}
			if (in.isItemEqual(infiniteStacks[i]) && ItemStack.areItemStackTagsEqual(in, infiniteStacks[i])
					&& infiniteCount[i] < getInfLimit()) {
				int putIn = Math.min(in.stackSize, getInfLimit() - infiniteCount[i]);
				infiniteCount[i] += putIn;
				in.stackSize -= putIn;
				if (in.stackSize == 0) {
					in = null;
				}
				setInventorySlotContents(INF_IN[i], in);
				markDirty();
			}
		}
		for (int i = 0; i < infiniteStacks.length; i++) {
			// check out
			if (infiniteStacks[i] == null || infiniteCount[i] == 0) {
				continue;
			}
			ItemStack out = getStackInSlot(INF_OUT[i]);
			if (out == null) {
				out = infiniteStacks[i].copy();
				out.stackSize = 0;
			}
			if (out.isItemEqual(infiniteStacks[i]) && ItemStack.areItemStackTagsEqual(out, infiniteStacks[i])
					&& out.stackSize < out.getMaxStackSize()) {
				int takeOut = Math.min(out.getMaxStackSize() - out.stackSize, infiniteCount[i]);
				out.stackSize += takeOut;
				infiniteCount[i] -= takeOut;
				if (infiniteCount[i] <= 0) {
					infiniteCount[i] = 0;
					infiniteStacks[i] = null;
				}
				setInventorySlotContents(INF_OUT[i], out);
				markDirty();
			}
		}
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
