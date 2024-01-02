package pistonmc.vutoolbox.low.config;

import net.minecraftforge.common.config.Property;

public class IntegerConfigContainer {
	private ConfigContainer container;
	private int defaultValue;
	public IntegerConfigContainer(ConfigContainer container, int defaultValue) {
		this.container = container;
		this.defaultValue = defaultValue;
	}
	
	public int get() {
		Property p = this.container.get();
		if (p == null) {
			p = this.container.set(Property.Type.INTEGER, String.valueOf(this.defaultValue));
			return this.defaultValue;
		}
		return p.getInt();
	}
}
