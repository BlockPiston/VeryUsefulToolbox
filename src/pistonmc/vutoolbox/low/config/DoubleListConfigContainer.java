package pistonmc.vutoolbox.low.config;

import net.minecraftforge.common.config.Property;

public class DoubleListConfigContainer {
	private ConfigContainer container;
	private double[] defaultValue;
	public DoubleListConfigContainer(ConfigContainer container, double[] defaultValue) {
		this.container = container;
		this.defaultValue = defaultValue;
	}
	
	public double[] get() {
		Property p = this.container.get();
		if (p == null) {
			String[] values = new String[this.defaultValue.length];
			for (int i=0; i<values.length; i++) {
				values[i] = String.valueOf(this.defaultValue[i]);
			}
			p = this.container.setList(Property.Type.INTEGER, values);
			return this.defaultValue;
		}
		return p.getDoubleList();
	}
}
