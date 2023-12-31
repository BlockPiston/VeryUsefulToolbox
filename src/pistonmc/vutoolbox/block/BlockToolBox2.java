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

public class BlockToolbox extends BlockContainer {
	public int modelRenderID = 0;
	public static final boolean infoOnPlaced = false;

	public BlockToolbox(boolean resistance) {
		super(Material.rock);
        String name = resistance ? "blockToolBoxResis" : "blockToolBox";
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
		return new TileToolBox();
	}

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
        TileToolBox te = TileToolBox.cast(world.getTileEntity(x, y, z));
        if (te == null) {
            ModInfo.log.error("Cannot cast to TileToolbox!");
            return;
        }
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileToolBox) {
			TileToolBox box = (TileToolBox) te;
			if (stack.hasDisplayName()) {
				box.setCustomInventoryName(stack.getDisplayName());
			}
			// if (!world.isRemote) {
			NBTTagCompound c = stack.getTagCompound();
			if (c != null) {
				NBTTagCompound tag = c.getCompoundTag("recsyscletem|toolbox");
				if (tag != null) {
					tag.setInteger("x", x);
					tag.setInteger("y", y);
					tag.setInteger("z", z);
					if (entity instanceof EntityPlayer) {
						tag.setBoolean("hasOwner", true);
						tag.setString("ownerName", ((EntityPlayer) entity).getDisplayName());
						tag.setString("ownerUUID", entity.getUniqueID().toString());
					} else {
						tag.setBoolean("hasOwner", false);
					}
					box.readFromNBT(tag);
				}
			}
			// }
		}
		if (world.isRemote && OtherConfig.displayInfoWhenPlacingToolBox) {
			PistonToolbox.printChatMessage(
					StatCollector.translateToLocalFormatted("message.tntptool.toolbox_place", x, y, z));
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileToolBox tile = (TileToolBox) world.getTileEntity(x, y, z);

		if (tile != null) {

			float f = PistonToolbox.UNIMPORTANT.nextFloat() * 0.8F + 0.1F;
			float f1 = PistonToolbox.UNIMPORTANT.nextFloat() * 0.8F + 0.1F;
			float f2 = PistonToolbox.UNIMPORTANT.nextFloat() * 0.8F + 0.1F;
			Block blockType = tile.hasResistanceUpgrade() ? RS2Blocks.blockToolBoxResis : RS2Blocks.blockToolBox;

			NBTTagCompound tag = new NBTTagCompound();
			NBTTagCompound nbt = new NBTTagCompound();
			tile.writeToNBT(nbt);
			nbt.setInteger("x", 0);
			nbt.setInteger("y", 0);
			nbt.setInteger("z", 0);
			String owner = tile.getOwner();

			tag.setTag("recsyscletem|toolbox", nbt);

			EntityItem entityitem = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(blockType, 1, 0));
			entityitem.getEntityItem().setTagCompound(tag);
			if (owner != null) {
				if (!tile.hasCustomInventoryName())
					entityitem.getEntityItem().setStackDisplayName(
							StatCollector.translateToLocalFormatted("tile.blockToolBoxWithUser.name", owner));
				else
					entityitem.getEntityItem().setStackDisplayName(tile.getInventoryName());
			}
			float f3 = 0.05F;
			entityitem.motionX = (float) PistonToolbox.UNIMPORTANT.nextGaussian() * f3;
			entityitem.motionY = (float) PistonToolbox.UNIMPORTANT.nextGaussian() * f3 + 0.2F;
			entityitem.motionZ = (float) PistonToolbox.UNIMPORTANT.nextGaussian() * f3;
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
			TileToolBox inventory = (TileToolBox) world.getTileEntity(x, y, z);

			if (inventory != null) {
				if (inventory.hasSecurityUpgrade()) {
					if (!inventory.isOwner(player.getUniqueID())) {
						RS2Network.network.sendTo(new MessageToolBoxSecurity(inventory.getOwner()),
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
	@Deprecated
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		if (!world.isRemote) {
			if (player.isSneaking()) {
				TileToolBox inventory = (TileToolBox) world.getTileEntity(x, y, z);

				if (inventory != null) {
					if (inventory.hasSecurityUpgrade()) {
						if (!inventory.isOwner(player.getUniqueID())) {
							RS2Network.network.sendTo(new MessageToolBoxSecurity(inventory.getOwner()),
									(EntityPlayerMP) player);
							return;
						}
					}
					ContainerToolBox container = new ContainerToolBox(inventory, player.inventory);
					container.transferStackInSlot(player, player.inventory.currentItem + 88);
					player.inventory.markDirty();
					inventory.markDirty();
				}
			}
		}
	}

}
