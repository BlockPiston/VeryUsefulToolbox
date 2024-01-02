package pistonmc.vutoolbox.low.config;

import net.minecraftforge.common.config.Property;

public class StringListConfigContainer {
	private ConfigContainer container;
	private String[] defaultValue;
	public StringListConfigContainer(ConfigContainer container, String[] defaultValue) {
		this.container = container;
		this.defaultValue = defaultValue;
	}
	
	public String[] get() {
		Property p = this.container.get();
		if (p == null) {
			p = this.container.setList(Property.Type.STRING, this.defaultValue);
			return this.defaultValue;
		}
		return p.getStringList();
	}
}
