package pistonmc.vutoolbox.low.config;

import java.io.File;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

/**
 * Factory for creating configuration objects
 */
public class ConfigRoot {
	private Configuration config;
	
	public ConfigRoot(File file) {
		this.config = new Configuration(file);
	}
	
	public void load() {
		this.config.load();
	}
	
	public ConfigCategory getCategory(String name) {
		return this.config.getCategory(name);
	}
	
	public void setDirty() {
		this.config.save();
	}

}
