package pistonmc.vutoolbox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Injected info values
 */
public interface ModInfo {
	public static Logger log = LogManager.getLogger("VUToolbox");

    String ID = "@modid@";
    String VERSION = "@version@";
    String GROUP = "@group@";
    String GROUP_INTERNAL = "@groupInternal@";
}

