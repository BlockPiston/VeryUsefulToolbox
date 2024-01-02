package pistonmc.vutoolbox.core;

import java.io.File;

import pistonmc.vutoolbox.low.config.BooleanConfigContainer;
import pistonmc.vutoolbox.low.config.ConfigCategoryContainer;
import pistonmc.vutoolbox.low.config.ConfigCategoryFactory;
import pistonmc.vutoolbox.low.config.ConfigFactory;
import pistonmc.vutoolbox.low.config.ConfigRoot;
import pistonmc.vutoolbox.low.config.IntegerConfigContainer;
import pistonmc.vutoolbox.low.config.StringListConfigContainer;

public class Config {
	public static final String CONFIG_NAME = "VeryUsefulToolbox.cfg";

	public static IntegerConfigContainer infinityStorageLimit;
	public static BooleanConfigContainer placeToolboxInfo;
	public static StringListConfigContainer toolboxSpecialRenderingTypes;
	
	public static void load() {
		File configFile = new File("").getAbsoluteFile().toPath().resolve("config").resolve("BlockPiston")
				.resolve(CONFIG_NAME).toFile();
		ConfigRoot root = new ConfigRoot(configFile);
		ConfigCategoryFactory categoryFactory = new ConfigCategoryFactory(root);
		{	
			ConfigCategoryContainer category = categoryFactory.create("Basic", "Basic stuff");
			ConfigFactory factory = new ConfigFactory(category);
			infinityStorageLimit = factory.createInteger("InfinityStorageLimit", "Base maximum number of items in an Infinity slot in the Toolbox", 1024);
			placeToolboxInfo = factory.createBoolean("PlaceToolboxInfo", "If a message should be displayed (to the player who placed the toolbox only) when a toolbox is placed.", true);
		}
		{
			ConfigCategoryContainer category = categoryFactory.create("ToolboxSpecialRenderingTypes", "Registry for those items that are not rendered correctly on top of the tool box. TEXTURE means render the item's texture directly, RENDERER means use the item's custom ItemRenderer. Try these two options when the item is not being rendered correctly. AUTO is the default value.");
			ConfigFactory factory = new ConfigFactory(category);
			toolboxSpecialRenderingTypes = factory.createStringList("Registry", "Options: AUTO, TEXTURE, RENDERER. Format (meta is optional): modid:name option meta", new String[0]);
		}
		root.load();
	}

}
