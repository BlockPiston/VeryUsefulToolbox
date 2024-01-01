package pistonmc.vutoolbox.core;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import pistonmc.vutoolbox.ModInfo;
import pistonmc.vutoolbox.low.NBTToolbox;

public class ToolboxTooltip {
	private ToolboxStatus status;

	public ToolboxTooltip(ToolboxStatus status) {
		this.status = status;
	}

	/**
	 * Add detailed tooltips for Toolbox items
	 * @param tag
	 * @param tooltips
	 */
	public void addTooltips(NBTToolbox tag, List<String> tooltips) {
		String prefix = "tooltip." + ModInfo.ID + ".toolbox.";
		tooltips.add(StatCollector.translateToLocal(prefix + "content"));
		
		Upgrades upgrades = status.getUpgrades();
		if (upgrades.isEnabled(Upgrades.CRAFT)) {
			tooltips.add(StatCollector.translateToLocal(prefix + "craft"));
		}
		if (upgrades.isEnabled(Upgrades.RESIS)) {
			tooltips.add(StatCollector.translateToLocal(prefix + "exploresis"));
		}
		if (upgrades.isEnabled(Upgrades.STORAGE)) {
			tooltips.add(StatCollector.translateToLocal(prefix + "storage"));
		}
		if (upgrades.isEnabled(Upgrades.SECURITY)) {
			tooltips.add(StatCollector.translateToLocal(prefix + "security"));
		}
		
		int inf = upgrades.getInfinityCount();
		if (inf > 0) {
			tooltips.add(StatCollector.translateToLocalFormatted(prefix + "inf", inf));
		}
		
		if (upgrades.isEnabled(Upgrades.PICKUP)) {
			tooltips.add(StatCollector.translateToLocal(prefix + "pickup"));
			int infLimit = upgrades.getInfinityStackLimit();
			Toolbox toolbox = new Toolbox();
			toolbox.readFromNBT(tag);
			for (int i = 0; i< Toolbox.NUM_INFINITY_SLOTS; i++) {
				BigItemStack stack = toolbox.getInfinityStack(i);
				ItemStack item = stack.getItemStack();
				if (item == null) {
					continue;
				}
				tooltips.add(EnumChatFormatting.YELLOW + item.getDisplayName());
				tooltips.add(EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
					prefix + "inf_storage", stack.getCount(), infLimit));
			}
		}

	}
}
