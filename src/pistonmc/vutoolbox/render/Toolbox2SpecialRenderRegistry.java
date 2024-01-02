package pistonmc.vutoolbox.render;

import java.util.Hashtable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Toolbox2SpecialRenderRegistry {
	private static Hashtable<String, Entry> registry;

	public static enum ToolBoxRenderType {
		AUTO, TEXTURE, RENDERER
	}

	public static ToolBoxRenderType getType(ItemStack stack) {
		if (registry == null)
			return ToolBoxRenderType.AUTO;

		Entry i = registry.get(Item.itemRegistry.getNameForObject(stack.getItem()));
		if (i == null)
			return ToolBoxRenderType.AUTO;
		return i.getType(stack.getItemDamage());
	}

	public static Item getItemFromText(String name) {
		return (Item) Item.itemRegistry.getObject(name);
	}

	//public static void loadConfig(ModConfig c, State s) {
//		if (s == State.PRE_INIT) {
//			ConfigCategory category = c.loadCategory("toolbox_special_rendering",
//					"Registry for those items that are not rendered correctly on top of the tool box. TEXTURE means render the item's texture directly, RENDERER means use the item's custom ItemRenderer. Try these two options when the item is not being rendered correctly. AUTO is the default value.");
//			c.putArray(category, c.loadProperty(category, "list", new String[0], Property.Type.STRING,
//					"Options: AUTO, TEXTURE, RENDERER. Format: modid:name option meta(optional)."));
//
//		} else if (s == State.POST_INIT) {
//			String[] list = c.getSList("toolbox_special_rendering.list");
//			if (list.length > 0) {
//				registry = new Hashtable<String, Entry>();
//				for (String entry : list) {
//					String[] split = entry.split(" ");
//					if (getItemFromText(split[0]) == null) {
//						PistonToolbox.log.warn("Warning: " + split[0] + " does not exist");
//						continue;
//					}
//					try {
//						ToolBoxRenderType t = ToolBoxRenderType.valueOf(split[1]);
//						if (t == null || t == ToolBoxRenderType.AUTO)
//							continue;
//						if (split.length > 2) {
//							// meta
//							int meta = Integer.parseInt(split[2]);
//							if (meta < 0)
//								throw new NumberFormatException();
//							register(split[0], t, meta);
//						} else {
//							register(split[0], t);
//						}
//
//					} catch (NumberFormatException e) {
//						PistonToolbox.log.warn("Warning: " + split[1] + " is not a valid number");
//						continue;
//					}
//				}
//			}
//		}
//	}

	public static void register(String name, ToolBoxRenderType value, int meta) {
		Entry entry = registry.get(name);
		if (entry == null) {
			entry = new Entry(value);
			registry.put(name, entry);
		}
		entry.addSpecial(meta, value);
	}

	public static void register(String name, ToolBoxRenderType value) {
		Entry entry = registry.get(name);
		if (entry == null) {
			entry = new Entry(value);
			registry.put(name, entry);
		}
		entry.type = value;

	}

	private static class Entry {
		ToolBoxRenderType type;
		Hashtable<Integer, ToolBoxRenderType> metaTable;

		public Entry(ToolBoxRenderType type) {
			this.type = type;
		}

		public void addSpecial(int meta, ToolBoxRenderType type) {
			if (metaTable == null) {
				metaTable = new Hashtable<Integer, ToolBoxRenderType>();
			}
			metaTable.put(meta, type);
		}

		public ToolBoxRenderType getType(int meta) {
			if (metaTable != null) {
				ToolBoxRenderType i = metaTable.get(meta);
				if (i != null)
					return i;
			}
			return type;
		}
	}
}
