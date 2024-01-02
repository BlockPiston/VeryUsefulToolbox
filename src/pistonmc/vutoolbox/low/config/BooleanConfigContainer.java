package pistonmc.vutoolbox.low.config;

import net.minecraftforge.common.config.Property;

public class BooleanConfigContainer {
	private ConfigContainer container;
	private boolean defaultValue;
	public BooleanConfigContainer(ConfigContainer container, boolean defaultValue) {
		this.container = container;
		this.defaultValue = defaultValue;
	}
	
	public boolean get() {
		Property p = this.container.get();
		if (p == null) {
			p = this.container.set(Property.Type.BOOLEAN, this.defaultValue ? "true" : "false");
			return this.defaultValue;
		}
		return p.getBoolean();
	}
}
