package pistonmc.vutoolbox.low.config;

public class ConfigCategoryFactory {
	private ConfigRoot root;
	
	public ConfigCategoryFactory(ConfigRoot root) {
		this.root = root;
	}
	
	public ConfigCategoryContainer create(String name, String comment) {
		return new ConfigCategoryContainer(this.root, name, comment);
	}
}
