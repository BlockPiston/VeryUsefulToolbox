package pistonmc.vutoolbox.low.config;

import net.minecraftforge.common.config.Property;

public class BooleanListConfigContainer {
	private ConfigContainer container;
	private boolean[] defaultValue;
	public BooleanListConfigContainer(ConfigContainer container, boolean[] defaultValue) {
		this.container = container;
		this.defaultValue = defaultValue;
	}
	
	public boolean[] get() {
		Property p = this.container.get();
		if (p == null) {
			String[] values = new String[this.defaultValue.length];
			for (int i=0; i<values.length; i++) {
				values[i] = this.defaultValue[i] ? "true" : "false";
			}
			p = this.container.setList(Property.Type.BOOLEAN, values);
			return this.defaultValue;
		}
		return p.getBooleanList();
	}
}
