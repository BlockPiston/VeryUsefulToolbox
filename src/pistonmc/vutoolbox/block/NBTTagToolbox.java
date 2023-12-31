package pistonmc.vutoolbox.block;

import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Utilities for handling NBT data on a Toolbox
 */
public class NBTTagToolbox {
    /**
     * Get the Toolbox NBTTagCompound from a toolbox ItemStack
     *
     * Returns null if the stack has no such data
     */
    public static NBTTagToolbox fromItemStack(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        return fromCompound(stack.getTagCompound());
    }

    /**
     * Get the Toolbox NBTTagCompound from the NBT tag.
     *
     * Returns null if the tag has no such data
     */
    public static NBTTagToolbox fromCompound(NBTTagCompound tag) {
        if (tag == null) {
            return null;
        }
        // the tag is called this because the toolbox was originally
        // from my mod Recsyscletem
        NBTTagCompound tag2 = tag.getCompoundTag("recsyscletem|toolbox");
        if (tag2 == null) {
            return null;
        }
        return new NBTTagToolbox(tag2);
    }

    /**
     * The underlying tag to read from and write to
     */
    private NBTTagCompound delegate;

    private NBTTagToolbox(NBTTagCompound delegate) {
        this.delegate = delegate;
    }

    /**
     * Set the player as the owner
     *
     * If the entity is not an EntityPlayer, or is null, the owner is removed
     */
    public void setOwner(Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            this.delegate.setBoolean("hasOwner", true);
            this.delegate.setString("ownerName", player.getDisplayName());
            this.delegate.setString("ownerUUID", entity.getUniqueID().toString());
        } else {
            this.delegate.setBoolean("hasOwner", false);
        }
    }

}
