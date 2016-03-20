package net.fusionlord.tomtom;

import net.fusionlord.tomtom.helpers.ModInfo;
import net.fusionlord.tomtom.proxy.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by FusionLord on 2/22/2016.
 */

@SuppressWarnings("ALL")
@Mod(modid = ModInfo.MOD_ID, name = ModInfo.MOD_NAME, version = ModInfo.VERSION, guiFactory = ModInfo.GUI_FACTORY)
public class TomTom
{
	@Mod.Instance(ModInfo.MOD_ID)
	public static TomTom INSTANCE;

	@SidedProxy(clientSide = "net.fusionlord.tomtom.proxy.ClientProxy", serverSide = "net.fusionlord.tomtom.proxy.CommonProxy")
	public static IProxy proxy;

	@EventHandler
	public void PreInit(FMLPreInitializationEvent event)
	{
		proxy.preInit(event);
	}

	@EventHandler
	public void Init(FMLInitializationEvent event)
	{
		proxy.init(event);
	}

	@EventHandler
	public void PostInit(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
	}
}
