package pistonmc.vutoolbox.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import pistonmc.vutoolbox.ModCreativeTab;
import pistonmc.vutoolbox.ModInfo;

/**
 * Chipset item used in crafting, and as upgrades
 */
public class ItemChipset extends Item {
    private static final String NAME = "itemChipset";
    // @formatter:off
    private static final String[] NAMES = {
        "", 
        "Seal", 
        "CoreCraft", 
        "UpgradeCraft", 
        "CoreInfinity",
        "UpgradeInfinity", 
        "CoreResis", 
        "UpgradeResis", 
        "CoreStorage", 
        "UpgradeStorage",
        "CoreSecurity", 
        "UpgradeSecurity", 
        "CorePickup", 
        "UpgradePickup"
    };
    // @formatter:on
    private static final IIcon[] ICONS = new IIcon[NAMES.length];

    public ItemChipset() {
        this.setHasSubtypes(true);
        this.setCreativeTab(ModCreativeTab.instance);
        this.setUnlocalizedName(NAME);
        this.setTextureName(ModInfo.ID + ":" + NAME);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item." + NAME + NAMES[stack.getItemDamage()];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        for (int i = 0; i < NAMES.length; i++) {
            ICONS[i] = reg.registerIcon(ModInfo.ID + ":" + NAME + NAMES[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return ICONS[meta < ICONS.length ? meta : 0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < NAMES.length; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean extra) {
        int damage = stack.getItemDamage();
        if (damage == 7) {
            String key = "tooltip." + ModInfo.ID + ".resistance_upgrade";
            tooltip.add(StatCollector.translateToLocal(key));
        } else if (damage == 9) {
            String key = "tooltip." + ModInfo.ID + ".storage_upgrade";
            tooltip.add(StatCollector.translateToLocal(key));
        }
    }
}
