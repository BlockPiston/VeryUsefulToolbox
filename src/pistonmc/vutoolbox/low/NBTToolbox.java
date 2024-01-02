package pistonmc.vutoolbox.low;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import pistonmc.vutoolbox.core.BigItemStack;

/**
 * Utilities for handling NBT data on a Toolbox
 */
public class NBTToolbox {
	private static final String ITEMSTACK_KEY = "vutoolbox";
	public static NBTToolbox fromItemStack(ItemStack stack) {
		if (stack == null) {
			return null;
		}
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			return null;
		}
		
		if (!tag.hasKey(ITEMSTACK_KEY)) {
			return null;
		}
		
		return new NBTToolbox(tag.getCompoundTag(ITEMSTACK_KEY));
	}
    /**
     * The underlying tag to read from and write to (non-null)
     */
    private NBTTagCompound inner;

    public NBTToolbox(NBTTagCompound inner) {
        this.inner = inner;
    }
    
    public void writeOwner(String name, UUID uuid) {
    	if (name == null || name.isEmpty() || uuid == null) {
    		inner.removeTag("ownername");
    	} else {
    		inner.setString("ownername", name);
    		inner.setLong("owneruuidmsb", uuid.getMostSignificantBits());
    		inner.setLong("owneruuidlsb", uuid.getLeastSignificantBits());
    	}
    }
    
    public String readOwnerName() {
    	return inner.getString("ownername");
    }
    
    public UUID readOwnerUUID() {
    	long msb = inner.getLong("owneruuidmsb");
    	long lsb = inner.getLong("owneruuidlsb");
    	return new UUID(msb, lsb);
    }
    /**
     * Set the items in the data
     * @param slots (non-null)
     */
    public void writeItems(ItemStack[] slots) {
    	NBTTagList tag = new NBTTagList();
    	NBTItemStackList tagList = new NBTItemStackList(tag);
    	tagList.append(slots);
    	inner.setTag("items", tag);
    }
    
    public void readItemsInto(ItemStack[] slots) {
    	NBTTagList tag = inner.getTagList("items", NBT.TAG_COMPOUND);
    	// tag is guaranteed non null
    	NBTItemStackList tagList = new NBTItemStackList(tag);
    	tagList.readInto(slots);
    }
    
    /**
     * Write the infinity slots
     * @param slots (non-null with non-null elements)
     */
    public void writeInfinityItems(BigItemStack[] slots) {
    	NBTTagList tag = new NBTTagList();
    	for (int i = 0; i < slots.length; i++) {
    		NBTTagCompound t = new NBTTagCompound();
    		BigItemStack stack = slots[i];
    		ItemStack item = stack.getItemStack();
    		if (item != null) {
    			NBTItemStack tagItem = new NBTItemStack(t);
    			tagItem.write(item);
    			t.setInteger("infcount", stack.getCount());
    		}
    		tag.appendTag(t);
    	}
    	inner.setTag("infitems", tag);
    }
    
    /**
     * Read the infinity slots
     * 
     * Existing items are replaced
     * @param slots (non-null with non-null elements)
     */
    public void readInfinityItemsInto(BigItemStack[] slots) {
    	NBTTagList tag = inner.getTagList("infitems", NBT.TAG_COMPOUND);
    	for (int i = 0; i < slots.length; i++) {
    		// NBTTagList returns default for OOB
    		NBTTagCompound t = tag.getCompoundTagAt(i);
    		int infCount = t.getInteger("infcount");
    		if (infCount <= 0) {
    			slots[i].clear();
    			continue;
    		}
    		NBTItemStack tagItem = new NBTItemStack(t);
    		slots[i].set(tagItem.read(), infCount);
    	}
    }
    
    public void writeCustomName(String name) {
    	if (name == null || name.isEmpty()) {
    		inner.removeTag("customname");
    	} else {
    		inner.setString("customname", name);
    	}
    }
    
    public String readCustomName() {
    	return inner.getString("customname");
    }
    
    public NBTTagCompound getInner() {
    	return inner;
    }
    
	public void setToItemStack(ItemStack stack) {
		if (stack == null) {
			return;
		}
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		
		tag.setTag(ITEMSTACK_KEY, this.inner);
	}

}
