package net.fusionlord.tomtom.network;

import net.fusionlord.tomtom.helpers.ModInfo;
import net.fusionlord.tomtom.network.messages.MessageSetDestination;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by FusionLord on 2/23/2016.
 */
public class PacketHandler
{
	public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.MOD_ID.toLowerCase());

	public static void init()
	{
		INSTANCE.registerMessage(MessageSetDestination.HANDLER.class, MessageSetDestination.class, 0, Side.CLIENT);
	}
}
