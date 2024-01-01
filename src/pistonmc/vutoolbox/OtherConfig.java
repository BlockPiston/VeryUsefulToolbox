package com.tntp.tntptool;

//import com.tntp.recsyscletem.config.RS2Config.State;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;

public class OtherConfig {
	public static boolean useSteelInsteadOfIron = false;
	public static boolean displayInfoWhenPlacingToolBox = true;

//  public static void loadConfig(RS2Config c, State s) {
//    if (s == State.PRE_INIT) {
//      ConfigCategory category = c.loadCategory("other", "Some other configs");
//      c.put(category, c.loadProperty(category, "use_steel_in_crafting", "false", Property.Type.BOOLEAN,
//          "Replace iron with steel in all crafting recipes"));
//      c.put(category, c.loadProperty(category, "display_info_when_placing_toolbox", "false", Property.Type.BOOLEAN,
//          "This option helps you to find your toolbox"));
//
//    } else if (s == State.INIT) {
//      useSteelInsteadOfIron = c.getB("other.use_steel_in_crafting");
//      displayInfoWhenPlacingToolBox = c.getB("other.display_info_when_placing_toolbox");
//
//    }
//  }
}
