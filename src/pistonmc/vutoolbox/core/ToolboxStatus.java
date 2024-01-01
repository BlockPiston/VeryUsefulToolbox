package pistonmc.vutoolbox.core;

import java.util.UUID;

import pistonmc.vutoolbox.low.NBTToolbox;

/**
 * The non-item data for a Toolbox
 */
public class ToolboxStatus {
	private String customName;
	private Upgrades upgrades;
	/** Owner status, maybe null */
	private String ownerName;
	private UUID ownerUUID;
	
	public ToolboxStatus() {
		customName = null;
		upgrades = new Upgrades();
		ownerName = "";
	}
	
	public String getCustomName() {
		return customName;
	}
	
	public void setCustomName(String s) {
		if (s != null && s.isEmpty()) {
			customName = null;
			return;
		}
		customName = s;
	}
	
	/**
	 * Return if the toolbox can be accessed by the uuid
	 * @param uuid
	 * @return
	 */
	public boolean canUse(UUID uuid) {
		if (!upgrades.isEnabled(Upgrades.SECURITY)) {
			return true;
		}
		if (ownerName.isEmpty()) {
			return true;
		}
		return ownerUUID == null || ownerUUID.equals(uuid);
	}
	
    public void setOwner(String owner, UUID uuid) {
    	if (owner == null) {
    		ownerName = "";
    		ownerUUID = null;
    		return;
    	}
    	ownerName = owner;
    	ownerUUID = uuid;
    }
    
    public String getOwner() {
    	return ownerName;
    }
	
	public void writeToNBT(NBTToolbox tagToolbox) {
		tagToolbox.writeCustomName(customName);
		tagToolbox.writeOwner(ownerName, ownerUUID);
		upgrades.writeToNBT(tagToolbox.getInner());
	}
	
	public void readFromNBT(NBTToolbox tagToolbox) {
		setCustomName(tagToolbox.readCustomName());
		ownerName = tagToolbox.readOwnerName();
		ownerUUID = tagToolbox.readOwnerUUID();
		upgrades.readFromNBT(tagToolbox.getInner());
	}
	
	public Upgrades getUpgrades() {
		return upgrades;
	}
}
