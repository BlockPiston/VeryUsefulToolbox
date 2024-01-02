package pistonmc.vutoolbox.low.config;

import net.minecraftforge.common.config.Property;

public class DoubleConfigContainer {
	private ConfigContainer container;
	private double defaultValue;
	public DoubleConfigContainer(ConfigContainer container, double defaultValue) {
		this.container = container;
		this.defaultValue = defaultValue;
	}
	
	public double get() {
		Property p = this.container.get();
		if (p == null) {
			p = this.container.set(Property.Type.INTEGER, String.valueOf(this.defaultValue));
			return this.defaultValue;
		}
		return p.getDouble();
	}
}
