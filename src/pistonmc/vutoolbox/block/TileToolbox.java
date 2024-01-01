package pistonmc.vutoolbox.block;

import java.util.UUID;

import com.tntp.tntptool.RS2Blocks;
import com.tntp.tntptool.RS2Items;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import pistonmc.vutoolbox.ModInfo;
import pistonmc.vutoolbox.ModUtils;
import pistonmc.vutoolbox.core.Toolbox;
import pistonmc.vutoolbox.event.MessageToolBoxUpdate;
import pistonmc.vutoolbox.event.RS2Network;
import pistonmc.vutoolbox.low.NBTToolbox;

public class TileToolbox extends TileEntity implements ISidedInventory {

    /**
     * Casts a TileEntity to a TileToolBox, return null if fail
     */
    public static TileToolbox cast(TileEntity tile) {
        if (tile instanceof TileToolbox) {
            return (TileToolbox) tile;
        }
        ModInfo.log.error("Cannot cast to TileToolbox!");
        return null;
    }

//  public static void loadConfig(RS2Config c, State s) {
//    if (s == State.PRE_INIT) {
//      ConfigCategory category = c.loadCategory("toolbox", "Tool Box");
//      c.put(category, c.loadProperty(category, "storage_limit", "1024", Property.Type.INTEGER,
//          "Space for large-quantity storage (without upgrade,every upgrade space x2)"));
//    } else if (s == State.POST_INIT) {
//      cfgStorageLimit = c.getI("toolbox.storage_limit");
//    }
//  }

	private static final int[] AUTO_SLOTS = { 42, 43, 44, 45, 46, 47, 48, 49, 50 };
	private static final int[] INF_IN = { 57, 58 };
	private static final int[] INF_OUT = { 59, 60 };

	// for client caching
	private int[] infiniteMetadata;

	private int workCount;
	private boolean invModified;
	
	private Toolbox toolbox;

	public TileToolbox() {
		super(61);
		infiniteStacks = new ItemStack[2];
		infiniteCount = new int[2];
		infiniteMetadata = new int[2];
		invModified = true;
		
		toolbox = new Toolbox();
	}
	
	@Override
	public int getSizeInventory() {
		return Toolbox.NUM_TOTAL_SLOTS;
	}
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		return toolbox.getStackInSlot(slot);
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int requestCount) {
		ItemStack stack = toolbox.decrStackSize(slot, requestCount);
		if (stack != null) {
			markDirty();
		}
		return stack;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		// don't know if this implementation is necessary
		// since we don't drop items on close
		// just for safety
		return toolbox.takeStack(slot);
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		toolbox.setSlot(slot, stack);
		this.markDirty();
	}

	public void setInventoryModified() {
		invModified = true;
	}
	
	@Override
	public String getInventoryName() {
		String customName = toolbox.getCustomName();
		if (customName == null) {
			return "tile."+BlockToolbox.NAME+".name";
		}
		return customName;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return toolbox.getCustomName() != null;
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		TileEntity t = this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord);
		if (t != this) {
			return false;
		}
		if (!ModUtils.isTileEntityUsableByPlayerByDistance(this, player)) {
			return false;
		}
		return toolbox.canUse(player.getUniqueID());
	}
	
	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return Toolbox.AUTOMATION_SLOTS;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return Toolbox.isAutomationSlot(slot);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		return Toolbox.isAutomationSlot(slot);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return Toolbox.isAutomationSlot(slot);
	}
	
	public Toolbox getToolbox() {
		return toolbox;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		setInventoryModified();
	}

	@Override
	@Deprecated
	public void updateEntity() {
		if (worldObj == null)
			return;
		if (!worldObj.isRemote) {
			workCount++;
			if (workCount == 10) {
				updateInfiniteStorage();
				checkUpgrade();
				workCount = 0;
			}
			if (invModified) {
				RS2Network.network.sendToAllAround(new MessageToolBoxUpdate(this),
						new TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 64));
				invModified = false;
			}
		} else {
			for (int i = 0; i < infiniteStacks.length; i++) {
				ItemStack in = getStackInSlot(INF_IN[i]);
				if (in != null && infiniteStacks[i] == null) {
					infiniteStacks[i] = in.copy();
				}
				if (infiniteStacks[i] != null) {
					infiniteStacks[i].setItemDamage(infiniteMetadata[i]);
				}
			}
			if (worldObj.getBlock(xCoord, yCoord, zCoord) == RS2Blocks.blockToolBoxResis) {
				explosionUpgrade = true;
			} else {
				explosionUpgrade = false;
			}
		}
	}

	private void updateInfiniteStorage() {
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

	@Deprecated
	private void checkUpgrade() {
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

	

	

	public void setInfStorageCount(int i, int count) {
		infiniteCount[i] = count;
		if (count == 0) {
			infiniteStacks[i] = null;
		}
	}

	public void setInfStorage(int i, ItemStack is) {
		infiniteStacks[i] = is;
		if (is == null) {
			infiniteCount[i] = 0;
		}
	}

	public int getInfStorageMetadata(int i) {
		if (infiniteStacks[i] == null)
			return 0;
		else
			return infiniteStacks[i].getItemDamage();
	}

	public void setInfStorageMetadata(int i, int meta) {
		infiniteMetadata[i] = meta;
	}

	public int getInfLimit() {
		return (int) (cfgStorageLimit * Math.pow(2, infiniteUpgrade));
	}

	public void setInfUpgrade(int upgrade) {
		infiniteUpgrade = upgrade;
	}

	public int getInfUpgrade() {
		return infiniteUpgrade;
	}

	public void setFourUpgradesFromShort(short b) {
		craftUpgrade = (b & 1) == 1;
		explosionUpgrade = (b & 2) == 2;
		storageUpgrade = (b & 4) == 4;
		securityUpgrade = (b & 8) == 8;
		pickupUpgrade = (b & 16) == 16;
	}

	public short getShortFromFourUpgrades() {
		short b = 0;
		if (craftUpgrade)
			b += 1;
		if (explosionUpgrade)
			b += 2;
		if (storageUpgrade)
			b += 4;
		if (securityUpgrade)
			b += 8;
		if (pickupUpgrade) {
			b += 16;
		}
		return b;

	}


	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		writeToolboxToNBT(new NBTToolbox(tag));
	}
	
	public void writeToolboxToNBT(NBTToolbox tag) {
		toolbox.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readToolboxFromNBT(new NBTToolbox(tag));
	}
	
	public void readToolboxFromNBT(NBTToolbox tag) {
		toolbox.readFromNBT(tag);
	}

}
