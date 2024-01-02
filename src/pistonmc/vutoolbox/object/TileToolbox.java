package pistonmc.vutoolbox.object;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import pistonmc.vutoolbox.ModInfo;
import pistonmc.vutoolbox.ModNetwork;
import pistonmc.vutoolbox.ModUtils;
import pistonmc.vutoolbox.core.Toolbox;
import pistonmc.vutoolbox.event.MessageToolboxSecurity;
import pistonmc.vutoolbox.event.MessageToolboxUpdate;
import pistonmc.vutoolbox.event.MessageToolboxRequest;
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
	
	private Toolbox toolbox;
	private boolean inventoryChanged;
	/** track if self has requested server to update */
	private boolean isClientInitialized;

	public TileToolbox() {
		toolbox = new Toolbox();
		inventoryChanged = true;
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
	
	/**
	 * Check if player can access. If not because of security, send a message to them
	 * @param player
	 * @return
	 */
	public boolean tryAccessByPlayer(EntityPlayer player, boolean sendMessage) {
		if (!this.isUseableByPlayer(player)) {
			if (sendMessage) {
				if (!toolbox.canUse(player.getUniqueID())) {
					// if the toolbox is not accessible because of security, send a message
					ModNetwork.network.sendTo(new MessageToolboxSecurity(toolbox.getOwner()),
							(EntityPlayerMP) player);
				}
			}
			return false;
		}
		return true;
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
		inventoryChanged = true;
	}

	@Override
	public void updateEntity() {
		if (worldObj == null) {
			return;
		}
		
		if (worldObj.isRemote && !this.isClientInitialized) {
			this.isClientInitialized = true;
			ModNetwork.network.sendToServer(new MessageToolboxRequest(this));
		}
		
		if (!inventoryChanged) {
			return;
		}
		
		inventoryChanged = false;
		
		toolbox.detectUpgrades();
		if (!worldObj.isRemote) {
			if (toolbox.processInfinitySlots()) {
				markDirty();
			}
			ModNetwork.network.sendToAllAround(new MessageToolboxUpdate(this),
					new TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 64));
		}
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
