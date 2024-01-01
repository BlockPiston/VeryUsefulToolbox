package pistonmc.vutoolbox.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import pistonmc.vutoolbox.ModCreativeTab;
import pistonmc.vutoolbox.ModInfo;
import pistonmc.vutoolbox.ModNetwork;
import pistonmc.vutoolbox.ModObjects;
import pistonmc.vutoolbox.ModUtils;
import pistonmc.vutoolbox.OtherConfig;
import pistonmc.vutoolbox.core.Toolbox;
import pistonmc.vutoolbox.core.Upgrades;
import pistonmc.vutoolbox.event.MessageToolBoxSecurity;
import pistonmc.vutoolbox.gui.ContainerToolbox;
import pistonmc.vutoolbox.gui.GuiToolBox;
import pistonmc.vutoolbox.gui.SlotToolBoxUnstackable;
import pistonmc.vutoolbox.low.NBTToolbox;

public class BlockToolbox extends BlockContainer {
	public static final String NAME = "blockToolbox";
	public int modelRenderID = 0;
	public static final boolean infoOnPlaced = false;

	public BlockToolbox(boolean resistance) {
		super(Material.rock);
        String name = resistance ? (NAME + "Resis") : NAME;
		this.setBlockName(name);
		this.setCreativeTab(ModCreativeTab.instance);
		this.setBlockTextureName(ModInfo.ID + ":" + name);
		this.setHardness(8.0f);
		this.setResistance(5.0f);
		this.setHarvestLevel("pickaxe", 2);
		setBlockBounds(0.0625f, 0, 0.0625f, 0.9375f, 0.875f, 0.9375f);
		setBlockUnbreakable();
		setResistance(resistance ? 6000000.0F : 0.0F);
	}
	@Override
	public int getRenderType() {
		return modelRenderID;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_) {
		return new TileToolbox();
	}

	/**
	 * Transfer the data from item to tile on place
	 */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {

		int l = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		int facing = 2;

		if (l == 1) {
			facing = 5;
		}
		if (l == 2) {
			facing = 3;
		}
		if (l == 3) {
			facing = 4;
		}
		world.setBlockMetadataWithNotify(x, y, z, facing, 2);
        TileToolbox tile = TileToolbox.cast(world.getTileEntity(x, y, z));
        if (tile == null) {
            return;
        }
        Toolbox toolbox = tile.getToolbox();

        NBTToolbox tag = NBTToolbox.fromItemStack(stack);
        if (tag != null) {
            tile.readToolboxFromNBT(tag);
            toolbox.setOwner(entity);
        }
        if (stack.hasDisplayName()) {
        	toolbox.setCustomName(stack.getDisplayName());
        }
		if (world.isRemote && OtherConfig.displayInfoWhenPlacingToolBox) {
			ModUtils.printChatMessage(
					StatCollector.translateToLocalFormatted("message."+ModInfo.ID+".toolbox_place", x, y, z));
		}
	}

	/**
	 * Transfer the data from tile to item on break
	 */
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileToolbox tile = TileToolbox.cast(world.getTileEntity(x, y, z));
		if (tile != null) {
			Block blockType = tile.getToolbox().getUpgrades().isEnabled(Upgrades.RESIS) ? ModObjects.blockToolBoxResis : ModObjects.blockToolBox;
			NBTToolbox tagToolbox = new NBTToolbox(new NBTTagCompound());
			tile.writeToolboxToNBT(tagToolbox);
			float dx = ModUtils.RNG.nextFloat() * 0.8F + 0.1F;
			float dy = ModUtils.RNG.nextFloat() * 0.8F + 0.1F;
			float dz = ModUtils.RNG.nextFloat() * 0.8F + 0.1F;
			EntityItem entityitem = new EntityItem(world, x + dx, y + dy, z + dz, new ItemStack(blockType, 1, 0));
			tagToolbox.setToItemStack(entityitem.getEntityItem());
			
			if (tile.hasCustomInventoryName()) {
				entityitem.getEntityItem().setStackDisplayName(tile.getInventoryName());
			} else {
				String ownerName = tile.getToolbox().getOwner();
				if (ownerName == null || ownerName.isEmpty()) {
					entityitem.getEntityItem().setStackDisplayName(
							StatCollector.translateToLocalFormatted("tile."+NAME+"WithUser.name", ownerName));
				}
			}
			
			float f3 = 0.05F;
			entityitem.motionX = (float) ModUtils.RNG.nextGaussian() * f3;
			entityitem.motionY = (float) ModUtils.RNG.nextGaussian() * f3 + 0.2F;
			entityitem.motionZ = (float) ModUtils.RNG.nextGaussian() * f3;
			world.spawnEntityInWorld(entityitem);
			world.func_147453_f(x, y, z, block);
		}
	
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xx, float yy,
			float zz) {
		if (world.isRemote) {
			return true;
		} else {
			TileToolbox inventory = (TileToolbox) world.getTileEntity(x, y, z);

			if (inventory != null) {
				if (inventory.hasSecurityUpgrade()) {
					if (!inventory.isOwner(player.getUniqueID())) {
						ModNetwork.network.sendTo(new MessageToolBoxSecurity(inventory.getOwner()),
								(EntityPlayerMP) player);
						return true;
					}
				}
				boolean special = false;
				int specialI = -1;
				if (side == 1) {
					int ix = (int) (xx * 16);
					int iz = (int) (zz * 16);
					int temp;
					int meta = world.getBlockMetadata(x, y, z);
					switch (meta) {
					case 2:
						ix = 15 - ix;
						iz = 15 - iz;
						break;
					case 4:
						temp = ix;
						ix = iz;
						iz = 15 - temp;
						break;
					case 5:
						temp = ix;
						ix = 15 - iz;
						iz = temp;
						break;
					}
					if (ix >= 2 && iz >= 2 && ix < 14 && iz < 14) {
						ix -= 2;
						iz -= 2;
						special = true;
						specialI = ix / 4 + iz / 4 * 3;
					}
				}
				if (special) {
					ItemStack stackInInventory = inventory.getStackInSlot(specialI);
					ItemStack currentHolding = player.inventory.getStackInSlot(player.inventory.currentItem);
					if (new SlotToolBoxUnstackable(null, 0, 0, 0).isItemValid(currentHolding)) {
						ItemStack newInInventory = currentHolding == null ? null : currentHolding.copy();
						ItemStack newHolding = stackInInventory == null ? null : stackInInventory.copy();
						inventory.setInventorySlotContents(specialI, newInInventory);
						player.inventory.setInventorySlotContents(player.inventory.currentItem, newHolding);
						player.inventory.markDirty();
						inventory.markDirty();
					}
				} else {
					if (player.isSneaking()) {
						// take block
						world.setBlockToAir(x, y, z);
					} else {
						player.openGui("tntptool", GuiToolBox.guiID, world, x, y, z);
					}
				}

			}
			return true;
		}
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube? This determines whether or
	 * not to render the shared face of two adjacent blocks and also whether the
	 * player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False
	 * (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return null;
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		if (!world.isRemote) {
			if (player.isSneaking()) {
				TileToolbox inventory = (TileToolbox) world.getTileEntity(x, y, z);

				if (inventory != null) {
					if (inventory.hasSecurityUpgrade()) {
						if (!inventory.isOwner(player.getUniqueID())) {
							ModNetwork.network.sendTo(new MessageToolBoxSecurity(inventory.getOwner()),
									(EntityPlayerMP) player);
							return;
						}
					}
					ContainerToolbox container = new ContainerToolbox(inventory, player.inventory);
					container.transferStackInSlot(player, player.inventory.currentItem + 88);
					player.inventory.markDirty();
					inventory.markDirty();
				}
			}
		}
	}

}
