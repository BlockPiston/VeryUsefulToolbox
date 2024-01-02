package pistonmc.vutoolbox.low.config;

public class ConfigFactory {
	private ConfigCategoryContainer category;
	
	public ConfigFactory(ConfigCategoryContainer category) {
		this.category = category;
	}
	
	public StringConfigContainer createString(String name, String comment, String defaultValue) {
		return new StringConfigContainer(this.create(name, comment), defaultValue);
	}
	
	public StringListConfigContainer createStringList(String name, String comment, String[] defaultValue) {
		return new StringListConfigContainer(this.create(name, comment), defaultValue);
	}
	
	public BooleanConfigContainer createBoolean(String name, String comment, boolean defaultValue) {
		return new BooleanConfigContainer(this.create(name, comment), defaultValue);
	}
	
	public BooleanListConfigContainer createBooleanList(String name, String comment, boolean[] defaultValue) {
		return new BooleanListConfigContainer(this.create(name, comment), defaultValue);
	}
	
	public IntegerConfigContainer createInteger(String name, String comment, int defaultValue) {
		return new IntegerConfigContainer(this.create(name, comment), defaultValue);
	}
	
	public IntegerListConfigContainer createIntegerList(String name, String comment, int[] defaultValue) {
		return new IntegerListConfigContainer(this.create(name, comment), defaultValue);
	}
	
	public DoubleConfigContainer createDouble(String name, String comment, double defaultValue) {
		return new DoubleConfigContainer(this.create(name, comment), defaultValue);
	}
	
	public DoubleListConfigContainer createDoubleList(String name, String comment, double[] defaultValue) {
		return new DoubleListConfigContainer(this.create(name, comment), defaultValue);
	}
	
	public ConfigContainer create(String name, String comment) {
		return new ConfigContainer(this.category, name, comment);
	}
}
