package pistonmc.vutoolbox.low.config;

import net.minecraftforge.common.config.ConfigCategory;

public class ConfigCategoryContainer {
	private ConfigRoot root;
	private String name;
	private String comment;
	
	public ConfigCategoryContainer(ConfigRoot root, String name, String comment) {
		this.root = root;
		this.name = name;
		this.comment = comment;
	}
	
	public ConfigCategory get() {
		ConfigCategory c = this.root.getCategory(this.name);
		String old = c.getComment();
		if (!this.comment.equals(old)) {
			c.setComment(this.comment);
			this.root.setDirty();
		}
		return c;
	}
	
	public void setDirty() {
		this.root.setDirty();
	}

}
