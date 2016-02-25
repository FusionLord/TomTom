package net.fusionlord.tomtom.proxy;

import net.fusionlord.tomtom.configuration.ConfigurationFile;
import net.fusionlord.tomtom.events.IMCEvents;
import net.fusionlord.tomtom.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by FusionLord on 2/23/2016.
 */
public class CommonProxy implements IProxy
{
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new ConfigurationFile(event.getSuggestedConfigurationFile()));
	}

	@Override
	public void init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new IMCEvents());
		PacketHandler.init();
	}

	@Override
	public void postInit(FMLPostInitializationEvent event)
	{

	}
}
