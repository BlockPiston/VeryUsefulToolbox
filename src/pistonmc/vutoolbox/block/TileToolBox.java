package pistonmc.vutoolbox.block;

import java.util.UUID;

import com.tntp.tntptool.RS2Blocks;
import com.tntp.tntptool.RS2Items;
import com.tntp.tntptool.RecsyscletemItemStacks;
import com.tntp.tntptool.network.MessageToolBoxUpdate;
import com.tntp.tntptool.network.RS2Network;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;

public class TileToolBox extends TileAbstract {

    /**
     * Casts a TileEntity to a TileToolBox, return null if fail
     */
    public static TileToolBox cast(TileEntity tile) {
        if (tile instanceof TileToolBox) {
            return (TileToolBox) tile;
        }
        return null;
    }

	public static int cfgStorageLimit = 1024;

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
	private boolean craftUpgrade;
	private boolean explosionUpgrade;
	private boolean storageUpgrade;
	private boolean securityUpgrade;
	private boolean pickupUpgrade;
	private int infiniteUpgrade;
	private String ownerName;
	private UUID ownerUUID;

	private ItemStack[] infiniteStacks;
	private int[] infiniteCount;
	// for client caching
	private int[] infiniteMetadata;

	private int workCount;
	private String customName;
	private boolean invModified;

	public TileToolBox() {
		super(61);
		infiniteStacks = new ItemStack[2];
		infiniteCount = new int[2];
		infiniteMetadata = new int[2];
		invModified = true;
	}

	public void setInventoryModified() {
		invModified = true;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return customName != null && customName.length() > 0;
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

	@Override
	public String getInventoryName() {
		return hasCustomInventoryName() ? customName : "tile.blockToolBox.name";
	}

	public void setCustomInventoryName(String name) {
		customName = name;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		for (int i = 0; i < infiniteStacks.length; i++) {
			NBTTagCompound inf = new NBTTagCompound();
			ItemStack s = infiniteStacks[i];
			if (s == null) {
				RecsyscletemItemStacks.writeNullStackToNBT(inf);
			} else {
				s.writeToNBT(inf);
			}
			inf.setInteger("infCount", infiniteCount[i]);
			tag.setTag("inf" + i, inf);
		}
		tag.setBoolean("craft", craftUpgrade);
		tag.setBoolean("resis", explosionUpgrade);
		tag.setBoolean("stora", storageUpgrade);
		tag.setBoolean("secur", securityUpgrade);
		tag.setBoolean("picku", pickupUpgrade);
		tag.setInteger("infStorageUpgrade", infiniteUpgrade);
		tag.setBoolean("hasOwner", ownerName != null);
		if (ownerName != null) {
			tag.setString("ownerName", ownerName);
			tag.setString("ownerUUID", ownerUUID.toString());
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		for (int i = 0; i < infiniteStacks.length; i++) {
			NBTTagCompound inf = tag.getCompoundTag("inf" + i);
			infiniteStacks[i] = ItemStack.loadItemStackFromNBT(inf);
			infiniteCount[i] = inf.getInteger("infCount");
		}
		craftUpgrade = tag.getBoolean("craft");
		explosionUpgrade = tag.getBoolean("resis");
		storageUpgrade = tag.getBoolean("stora");
		securityUpgrade = tag.getBoolean("secur");
		infiniteUpgrade = tag.getInteger("infStorageUpgrade");
		pickupUpgrade = tag.getBoolean("picku");
		ownerName = null;
		if (tag.getBoolean("hasOwner")) {
			ownerName = tag.getString("ownerName");
			ownerUUID = UUID.fromString(tag.getString("ownerUUID"));
		}
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		return AUTO_SLOTS;
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack stack) {
		return true;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		return slot >= 42 && slot <= 50;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return slot >= 42 && slot <= 50;
	}

	public ItemStack getInfStorage(int i) {
		return infiniteStacks[i];
	}

	public int getInfCount(int i) {
		return infiniteCount[i];
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

	public boolean hasCraftUpgrade() {
		return craftUpgrade;
	}

	public boolean hasResistanceUpgrade() {
		return explosionUpgrade;
	}

	public boolean hasStorageUpgrade() {
		return storageUpgrade;
	}

	public boolean hasSecurityUpgrade() {
		return securityUpgrade;
	}

	public boolean hasPickupUpgrade() {
		return pickupUpgrade;
	}

	public String getOwner() {
		return ownerName;
	}

	public boolean isOwner(UUID uuid) {
		return ownerName == null ? true : ownerUUID.equals(uuid);
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if (!super.isUseableByPlayer(player)) {
			return false;
		}
		if (hasSecurityUpgrade()) {
			if (!isOwner(player.getUniqueID())) {
				return false;
			}
		}
		return true;
	}

}
