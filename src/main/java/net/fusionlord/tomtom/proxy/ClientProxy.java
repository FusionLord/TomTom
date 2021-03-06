package net.fusionlord.tomtom.proxy;

import net.fusionlord.tomtom.commands.WaypointCommand;
import net.fusionlord.tomtom.configuration.ConfigurationFile;
import net.fusionlord.tomtom.events.TomTomEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by FusionLord on 2/23/2016.
 */
public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		new TomTomEvents();
		MinecraftForge.EVENT_BUS.register(new ConfigurationFile(event.getSuggestedConfigurationFile()));
		super.preInit(event);
	}

	@Override
	public void init (FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(TomTomEvents.INSTANCE);
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(TomTomEvents.INSTANCE);
//		MinecraftForge.EVENT_BUS.register(new SavingEvents());

		ClientCommandHandler.instance.registerCommand(new WaypointCommand());
	}
}
