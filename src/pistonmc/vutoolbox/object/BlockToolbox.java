package pistonmc.vutoolbox.object;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import pistonmc.vutoolbox.ModCreativeTab;
import pistonmc.vutoolbox.ModGui;
import pistonmc.vutoolbox.ModInfo;
import pistonmc.vutoolbox.ModObjects;
import pistonmc.vutoolbox.ModUtils;
import pistonmc.vutoolbox.core.Config;
import pistonmc.vutoolbox.core.Toolbox;
import pistonmc.vutoolbox.core.Upgrades;
import pistonmc.vutoolbox.gui.ContainerToolbox;
import pistonmc.vutoolbox.gui.SlotToolbox;
import pistonmc.vutoolbox.low.NBTToolbox;
import pistonmc.vutoolbox.render.ModelToolbox;

public class BlockToolbox extends BlockContainer {
	public static final String NAME = "blockToolbox";
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
		return ModelToolbox.rendererId;
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
		if (world.isRemote && Config.shouldDisplayInfoWhenPlacingToolbox()) {
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
			Block blockType = tile.getToolbox().getUpgrades().isEnabled(Upgrades.RESIS) ? ModObjects.blockToolboxResis : ModObjects.blockToolbox;
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

	/**
	 * On right click, either take the block (sneaking), open gui, or take tools (top side)
	 */
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xx, float yy,
			float zz) {
		if (world.isRemote) {
			// no special logic on client side
			return true;
		} 
		TileToolbox tile = TileToolbox.cast(world.getTileEntity(x, y, z));
		if (tile == null) {
			return true;
		}
		// check if the toolbox is accessible
		if (!tile.tryAccessByPlayer(player, true)) {
			return true;
		}
		
		if (side != 1) {
			// not clicking top
			if (player.isSneaking()) {
				// take block
				world.setBlockToAir(x, y, z);
			} else {
				player.openGui(ModInfo.ID, ModGui.GUI_ID_TOOLBOX, world, x, y, z);
			}
			return true;
		}
		
		// clicking top
		int subPixelX = (int) (xx * 16);
		int subPixelZ = (int) (zz * 16);
		if (subPixelX < 2 || subPixelZ < 2 || subPixelX > 13 || subPixelZ > 13) {
			// not clicking on a slot
			return true;
		}
		
		// determine slot based on rotation
		switch (world.getBlockMetadata(x, y, z)) {
			case 2:
				subPixelX = 15 - subPixelX;
				subPixelZ = 15 - subPixelZ;
				break;
			case 4: {
				int temp = subPixelX;
				subPixelX = subPixelZ;
				subPixelZ = 15 - temp;
				break;
			}
			case 5: {
				int temp = subPixelX;
				subPixelX = 15 - subPixelZ;
				subPixelZ = temp;
				break;
			}
		}
		subPixelX -= 2;
		subPixelZ -= 2;
		int slotIndex = subPixelX / 4 + subPixelZ / 4 * 3;
		// attempt to swap the slot and hand item
		ItemStack stackInToolbox = tile.getStackInSlot(slotIndex);
		ItemStack currentHolding = player.inventory.getStackInSlot(player.inventory.currentItem);
		SlotToolbox slot = new SlotToolbox(null, 0, 0, 0);
		slot.setOnlyAllowUnstackable();
		if (slot.isItemValid(currentHolding)) {
			// copy the stacks just to be safe
			ItemStack newInToolbox = currentHolding == null ? null : currentHolding.copy();
			ItemStack newHolding = stackInToolbox == null ? null : stackInToolbox.copy();
			tile.setInventorySlotContents(slotIndex, newInToolbox);
			player.inventory.setInventorySlotContents(player.inventory.currentItem, newHolding);
			player.inventory.markDirty();
		}
		
		return true;
		
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		if (world.isRemote) {
			// no special logic on client side
			return;
		}
		if (!player.isSneaking()) {
			return;
		}
		
		TileToolbox tile = TileToolbox.cast(world.getTileEntity(x, y, z));
		if (tile == null) {
			return;
		}
		
			
		// check if the toolbox is accessible
		if (!tile.tryAccessByPlayer(player, true)) {
			return;
		}
		// put the item in
		ContainerToolbox container = new ContainerToolbox(tile, player.inventory);
		container.transferStackInSlot(player, player.inventory.currentItem + 88);
		player.inventory.markDirty();
		tile.markDirty();
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

}
