package pistonmc.vutoolbox.low.config;

import net.minecraftforge.common.config.Property;

public class IntegerListConfigContainer {
	private ConfigContainer container;
	private int[] defaultValue;
	public IntegerListConfigContainer(ConfigContainer container, int[] defaultValue) {
		this.container = container;
		this.defaultValue = defaultValue;
	}
	
	public int[] get() {
		Property p = this.container.get();
		if (p == null) {
			String[] values = new String[this.defaultValue.length];
			for (int i=0; i<values.length; i++) {
				values[i] = String.valueOf(this.defaultValue[i]);
			}
			p = this.container.setList(Property.Type.INTEGER, values);
			return this.defaultValue;
		}
		return p.getIntList();
	}
}
