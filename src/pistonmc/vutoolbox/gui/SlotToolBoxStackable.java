package com.tntp.tntptool.container;

import com.tntp.tntptool.RS2Blocks;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotToolBoxStackable extends Slot {

	public SlotToolBoxStackable(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
		super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		if (stack == null)
			return true;
		return stack.getItem() != Item.getItemFromBlock(RS2Blocks.blockToolBox)
				&& stack.getItem() != Item.getItemFromBlock(RS2Blocks.blockToolBoxResis);
	}

}
