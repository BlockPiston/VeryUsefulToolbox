package pistonmc.vutoolbox;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;

import com.tntp.tntptool.tileentity.TileToolBox;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ModConfig {
	public static final String CONFIG_NAME = "Recsyscletem2.cfg";
	public static final String CONFIG_BACKUP_NAME = "Recsyscletem2.cfg.bak";
	private static ArrayList<Class<?>> loaders = new ArrayList<Class<?>>();
	private boolean override = false;
	public String version = "";
	public Configuration config = null;
	private Hashtable<String, Object> table;
	private static State state;

	public enum State {
		PRE_INIT, INIT, POST_INIT
	}

	private ModConfig() {
		table = new Hashtable<String, Object>();
		table.put("versionChange", false);
		table.put("versionGen", false);
	}

	public static void setState(State s) {
		state = s;
	}

	public static void addConfigLoader(Class<?> clazz) {
		loaders.add(clazz);
	}

	public static ModConfig loadConfiguration() {
		File configFile = new File("").getAbsoluteFile().toPath().resolve("config").resolve("iTNTPiston")
				.resolve(CONFIG_NAME).toFile();
		Configuration config = new Configuration(configFile);
		config.load();
		config = checkReset(config);

		ModConfig c = new ModConfig();
		c.config = config;
		c.override = false;
		ConfigCategory cateBasic = c.loadCategory("basic", "Basic Configuration");
		// c.loadCategoryBasic(config, cateBasic);
		state = State.PRE_INIT;
		// ConfigCategory cateGuideBook = config.getCategory("guidebook");
		// c.loadCategoryGuideBook(cateGuideBook);
		invokeLoaders(c);
		if (config.hasChanged() || c.override) {
			config.save();
		}

		return c;
	}

	private static Configuration checkReset(Configuration config) {
		ConfigCategory reset = config.getCategory("Reset");
		reset.setComment("A Reset Button. Don't worry, everybody screws up.");
		Property p = reset.get("SWITCH");
		if (p == null) {
			p = new Property("SWITCH", "false", Property.Type.BOOLEAN);
			reset.put("SWITCH", p);
			p.comment = "SET TO TRUE TO RESET THE CONFIGURATION TO ITS INITIAL STATE";
			config.save();
		}

		if (p.getBoolean() == true) {
			Path backupP = new File("").getAbsoluteFile().toPath().resolve("config").resolve("iTNTPiston")
					.resolve(CONFIG_BACKUP_NAME);
			Path configP = new File("").getAbsoluteFile().toPath().resolve("config").resolve("iTNTPiston")
					.resolve(CONFIG_NAME);
			try {
				Files.copy(configP, backupP, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				PistonToolbox.log.warn("Cannot back up config!");
			}
			File configFile = configP.toFile();
			configFile.delete();
			config = new Configuration(configFile);
			config.load();
			config = checkReset(config);
		}
		return config;
	}

	// private void loadCategoryGuideBook(ConfigCategory cateGuideBook) {
	// Property propVolume = loadProperty(cateGuideBook, "volume", "-1",
	// Property.Type.INTEGER,
	// "The volume that the player views when right-click");
	// Property propChapter = loadProperty(cateGuideBook, "chapter", "-1",
	// Property.Type.INTEGER,
	// "The chapter that the player views when right-click");
	// Property propPage = loadProperty(cateGuideBook, "page", "-1",
	// Property.Type.INTEGER,
	// "The page that the player views when right-click");
	// manualDefaultVolume = propVolume.getInt();
	// manualDefaultChapter = propChapter.getInt();
	// manualDefaultPage = propPage.getInt();
	//
	// Property propFlipSpeed = loadProperty(cateGuideBook, "flip_speed", new
	// String[] { "1", "5", "10", "50", "100" },
	// Property.Type.INTEGER, "Different speeds that player can choose when viewing
	// the manual.");
	//
	// manualPageFlipSpeeds = propFlipSpeed.getIntList();
	// if (manualPageFlipSpeeds.length <= 0) {
	// MBWP.log.warn("Detect Configuration problem, it will be reset.");
	// propFlipSpeed.set(new int[] { 1 });
	// }
	// }

//  public void loadCategoryBasic(Configuration config, ConfigCategory cate) {
//    Property propVersion = loadProperty(cate, "version", Recsyscletem.VERSION, Property.Type.STRING, "Mod Version");
//    if (!propVersion.getString().equals(Recsyscletem.VERSION)) {
//      propVersion.set(Recsyscletem.VERSION);
//      override = true;
//      table.put("versionChange", true);
//      Recsyscletem.log.info("Update in version detected.");
//    }
//    table.put("basic.version", Recsyscletem.VERSION);
//  }

	public void putArray(ConfigCategory category, Property prop) {
		Object obj = null;
		switch (prop.getType()) {
		case BOOLEAN:
			obj = prop.getBooleanList();
		case DOUBLE:
			obj = prop.getDoubleList();
			break;
		case INTEGER:
			obj = prop.getIntList();
			break;
		case STRING:
			obj = prop.getStringList();
			break;
		default:
			break;
		}
		if (obj != null)
			table.put(category.getName() + "." + prop.getName(), obj);
	}

	public void put(ConfigCategory category, Property prop) {
		Object obj = null;
		switch (prop.getType()) {
		case BOOLEAN:
			obj = prop.getBoolean();
			break;
		case DOUBLE:
			obj = prop.getDouble();
			break;
		case INTEGER:
			obj = prop.getInt();
			break;
		case STRING:
			obj = prop.getString();
			break;
		default:
			break;

		}
		if (obj != null)
			table.put(category.getName() + "." + prop.getName(), obj);
	}

	public ConfigCategory loadCategory(String name, String comment) {
		ConfigCategory c = config.getCategory(name);
		String old = c.getComment();
		if ((comment != null && !comment.equals(old)) || (comment == null && old != null)) {
			c.setComment(comment);
			override = true;
		}
		return c;
	}

	public Property loadProperty(ConfigCategory category, String propertyName, String defaultValue, Property.Type type,
			String com) {
		Property prop = category.get(propertyName);
		if (prop == null) {
			prop = new Property(propertyName, defaultValue, type);
			category.put(propertyName, prop);
			override = true;
			if (propertyName.equals("version")) {
				table.put("versionGen", true);
			}
		}
		prop.comment = com;
		return prop;
	}

	public Property loadProperty(ConfigCategory category, String propertyName, String[] defaultValue,
			Property.Type type, String com) {
		Property prop = category.get(propertyName);
		if (prop == null) {
			prop = new Property(propertyName, defaultValue, type);
			category.put(propertyName, prop);
			override = true;
		}
		prop.comment = com;
		return prop;
	}

	public String getS(String name) {
		Object str = table.get(name);
		if (str instanceof String)
			return (String) str;
		else
			throw new RuntimeException("Recsyscletem: Unknown Configuration Entry: " + name);
	}

	public boolean getB(String name) {
		Object str = table.get(name);
		if (str instanceof Boolean)
			return (Boolean) str;
		else
			throw new RuntimeException("Recsyscletem: Unknown Configuration Entry: " + name);
	}

	public int getI(String name) {
		Object str = table.get(name);
		if (str instanceof Integer)
			return (Integer) str;
		else
			throw new RuntimeException("Recsyscletem: Unknown Configuration Entry: " + name);
	}

	public double getD(String name) {
		Object str = table.get(name);
		if (str instanceof Double)
			return (Double) str;
		else
			throw new RuntimeException("Recsyscletem: Unknown Configuration Entry: " + name);
	}

	public String[] getSList(String name) {
		Object str = table.get(name);
		if (str instanceof String[])
			return (String[]) str;
		else
			throw new RuntimeException("Recsyscletem: Unknown Configuration Entry: " + name);
	}

	public boolean[] getBList(String name) {
		Object str = table.get(name);
		if (str instanceof boolean[])
			return (boolean[]) str;
		else
			throw new RuntimeException("Recsyscletem: Unknown Configuration Entry: " + name);
	}

	public int[] getIList(String name) {
		Object str = table.get(name);
		if (str instanceof int[])
			return (int[]) str;
		else
			throw new RuntimeException("Recsyscletem: Unknown Configuration Entry: " + name);
	}

	public double[] getDList(String name) {
		Object str = table.get(name);
		if (str instanceof double[])
			return (double[]) str;
		else
			throw new RuntimeException("Recsyscletem: Unknown Configuration Entry: " + name);
	}

	public static void invokeLoaders(ModConfig c) {
		for (Class<?> clazz : loaders) {
			try {
				Method method = clazz.getMethod("loadConfig", ModConfig.class, State.class);
				method.setAccessible(true);
				PistonToolbox.log.info("Loading " + clazz);
				method.invoke(null, c, state);
			} catch (NoSuchMethodException e) {
				PistonToolbox.log.error("No loadConfig() in " + clazz);
			} catch (SecurityException e) {
				PistonToolbox.log.error("Security Error: " + clazz.getName());
			} catch (IllegalAccessException e) {
				PistonToolbox.log.error("Access Error: " + clazz.getName());
			} catch (IllegalArgumentException e) {
				PistonToolbox.log.error("Argument Error: " + clazz.getName());
			} catch (InvocationTargetException e) {
				PistonToolbox.log.error("Exception Error: " + clazz.getName());
				e.printStackTrace();
			}
		}
	}

	public static void loadConfigLoaders() {
		// addConfigLoader(WorldRecycleSaveData.class);
		// addConfigLoader(RecycleDictionary.class);
		// addConfigLoader(TileRecycler.class);
		// addConfigLoader(TileComposter.class);
		// addConfigLoader(TileIncinerator.class);
		// addConfigLoader(TilePacker.class);
		// addConfigLoader(TileHeatFurnace.class);
		// addConfigLoader(TileCompressor.class);
		// addConfigLoader(TileToolBox.class);
		addConfigLoader(ToolBoxSpecialRenderRegistry.class);
		// addConfigLoader(ItemMonitorCard.class);
		// addConfigLoader(TileTrashDumpsterIndustrial.class);
		// addConfigLoader(OtherConfig.class);
	}

	// public void debug() {
	// Recsyscletem.log.info("Config:");
	// for (Entry<String, Object> entry : table.entrySet()) {
	// Recsyscletem.log.info(entry.getKey() + " " + entry.getValue() + " " +
	// entry.getValue().getClass());
	// }
	// }

}
