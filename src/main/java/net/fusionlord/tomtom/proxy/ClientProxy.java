package net.fusionlord.tomtom.proxy;

import net.fusionlord.tomtom.events.ClientTickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * Created by FusionLord on 2/23/2016.
 */
public class ClientProxy extends CommonProxy
{
	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
		ClientTickHandler tickHandler = new ClientTickHandler();
		MinecraftForge.EVENT_BUS.register(tickHandler);
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(tickHandler);
	}
}
