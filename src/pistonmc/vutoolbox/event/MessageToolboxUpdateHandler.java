package pistonmc.vutoolbox.event;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import pistonmc.vutoolbox.core.BigItemStack;
import pistonmc.vutoolbox.core.Toolbox;
import pistonmc.vutoolbox.object.TileToolbox;

public class MessageToolboxUpdateHandler implements IMessageHandler<MessageToolboxUpdate, IMessage>{

	@Override
	public IMessage onMessage(MessageToolboxUpdate message, MessageContext ctx) {
		if (Minecraft.getMinecraft().thePlayer.dimension != message.dimension) {
			return null;
		}
		
		World world = Minecraft.getMinecraft().theWorld;
		TileToolbox tile = TileToolbox.cast(world.getTileEntity(message.x, message.y, message.z));
		if (tile == null) {
			return null;
		}
		
		for (int i = 0; i < Toolbox.NUM_TOP_SLOTS; i++) {
			tile.setInventorySlotContents(i, message.tools[i]);
		}
		for (int i = 0; i < Toolbox.NUM_UPGRADE_SLOTS; i++) {
			tile.setInventorySlotContents(i+Toolbox.UPGRADE_SLOTS_START, message.upgrades[i]);
		}
		for (int i = 0; i < Toolbox.NUM_INFINITY_SLOTS; i++) {
			BigItemStack slot = message.infinitySlots[i];
			tile.getToolbox().getInfinityStack(i).set(slot.getItemStack(), slot.getCount());
		}
		
		return null;
	}

}
