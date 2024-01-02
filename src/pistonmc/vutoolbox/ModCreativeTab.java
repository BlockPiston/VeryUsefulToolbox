package pistonmc.vutoolbox;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ModCreativeTab extends CreativeTabs {
	public static final ModCreativeTab instance = new ModCreativeTab();

	public ModCreativeTab() {
		super(ModInfo.ID+".creativetab");
	}

	@Override
	public Item getTabIconItem() {
		return Item.getItemFromBlock(ModObjects.blockToolbox);
	}

}
