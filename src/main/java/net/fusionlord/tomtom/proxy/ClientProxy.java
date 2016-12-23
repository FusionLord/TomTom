package net.fusionlord.tomtom.proxy;

import net.fusionlord.tomtom.commands.WaypointCommand;
import net.fusionlord.tomtom.configuration.ConfigurationFile;
import net.fusionlord.tomtom.events.GuiEvents;
import net.fusionlord.tomtom.events.TomTomEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by FusionLord on 2/23/2016.
 */
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		new TomTomEvents();
		MinecraftForge.EVENT_BUS.register(new ConfigurationFile(event.getSuggestedConfigurationFile()));
		MinecraftForge.EVENT_BUS.register(new GuiEvents());
		super.preInit(event);
	}

	@Override
	public void init (FMLInitializationEvent event)
	{
		super.init(event);
		MinecraftForge.EVENT_BUS.register(TomTomEvents.INSTANCE);
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(TomTomEvents.INSTANCE);
//		MinecraftForge.EVENT_BUS.register(new SavingEvents());

		ClientCommandHandler.instance.registerCommand(new WaypointCommand());
	}
}
