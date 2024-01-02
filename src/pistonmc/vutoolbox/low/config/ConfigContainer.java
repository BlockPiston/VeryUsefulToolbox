package pistonmc.vutoolbox.low.config;

import net.minecraftforge.common.config.Property;

public class ConfigContainer {
	private ConfigCategoryContainer category;
	private String name;
	private String comment;
	private Property property;
	
	public ConfigContainer(ConfigCategoryContainer category, String name, String comment) {
		this.category = category;
		this.name = name;
		this.comment = comment;
	}
	
	public Property get() {
		if (this.property != null) {
			return this.property;
		}
		
		Property p = this.category.get().get(this.name);
		if (p == null) {
			return p;
		}
		
		if (!this.comment.equals(p.comment)) {
			p.comment = this.comment;
			this.category.setDirty();
		}
		
		return p;
	}
	
	public Property set(Property.Type type, String value) {
		Property newProp = new Property(this.name, value, type);
		newProp.comment = this.comment;
		this.property = newProp;
		this.category.get().put(this.name, newProp);
		this.category.setDirty();
		return newProp;
	}
	
	public Property setList(Property.Type type, String[] values) {
		Property newProp = new Property(this.name, values, type);
		newProp.comment = this.comment;
		this.property = newProp;
		this.category.get().put(this.name, newProp);
		this.category.setDirty();
		return newProp;
	}
	
	
}
