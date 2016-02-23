package net.fusionlord.tomtom;

import net.fusionlord.tomtom.helpers.LogHelper;
import net.fusionlord.tomtom.helpers.ModInfo;
import net.fusionlord.tomtom.proxy.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by FusionLord on 2/22/2016.
 */

@SuppressWarnings("ALL")
@Mod(modid = ModInfo.MOD_ID, name = ModInfo.MOD_NAME, version = ModInfo.VERSION)
public class TomTom
{
	@Mod.Instance(ModInfo.MOD_ID)
	public static TomTom INSTANCE;

	@SidedProxy(clientSide = "net.fusionlord.tomtom.proxy.ClientProxy", serverSide = "net.fusionlord.tomtom.proxy.CommonProxy")
	public static IProxy proxy;

	@EventHandler
	public void PreInit(FMLPreInitializationEvent event)
	{
		if (event.getSide() == Side.SERVER)
			LogHelper.info("This mod is client side only and makes no changes on the server.");
	}

	@EventHandler
	public void Init(FMLInitializationEvent event)
	{
		proxy.init(event);
	}

	@EventHandler
	public void PostInit(FMLPostInitializationEvent event)
	{

	}
}
