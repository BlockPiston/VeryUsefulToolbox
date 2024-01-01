package pistonmc.vutoolbox.core;

import net.minecraft.nbt.NBTTagCompound;

public class Upgrades {
	public static final int CRAFT = 1;
	public static final int RESIS = 1 << 1;
	public static final int STORAGE = 1 << 2;
	public static final int SECURITY = 1 << 3;
	public static final int PICKUP = 1 << 4;

	private int infinityCount;
	private int flags;
	
	public Upgrades() {
		infinityCount = 0;
		flags = 0;
	}
	
	public void set(int upgrade, boolean enabled) {
		if (enabled) {
			flags |= upgrade;
		} else {
			flags &= ~upgrade;
		}
	}

	public boolean isEnabled(int upgrade) {
		return (flags & upgrade) != 0;
	}
	
	public void setInfinityCount(int i) {
		infinityCount = i;
	}
	
	public int getInfinityCount() {
		return infinityCount;
	}
	
	public int getInfinityStackLimit() {
		return (int) (Config.getInfinityStorageLimit() * Math.pow(2, infinityCount));
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("upgrades", flags);
		tag.setInteger("infinityupgrades", infinityCount);
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		flags = tag.getInteger("upgrades");
		infinityCount = tag.getInteger("infinityupgrades");
	}
}
