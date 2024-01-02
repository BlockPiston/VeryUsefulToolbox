package pistonmc.vutoolbox.low.config;

import net.minecraftforge.common.config.Property;

public class StringConfigContainer {
	private ConfigContainer container;
	private String defaultValue;
	public StringConfigContainer(ConfigContainer container, String defaultValue) {
		this.container = container;
		this.defaultValue = defaultValue;
	}
	
	public String get() {
		Property p = this.container.get();
		if (p == null) {
			p = this.container.set(Property.Type.STRING, this.defaultValue);
			return this.defaultValue;
		}
		return p.getString();
	}
}
