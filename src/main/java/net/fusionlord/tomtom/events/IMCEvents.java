package net.fusionlord.tomtom.events;

import com.google.common.collect.ImmutableList;
import net.fusionlord.tomtom.TomTom;
import net.fusionlord.tomtom.helpers.LogHelper;
import net.fusionlord.tomtom.network.PacketHandler;
import net.fusionlord.tomtom.network.messages.SetDestinationPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

/**
 * Created by FusionLord on 2/22/2016.
 */
public class IMCEvents
{
	@SubscribeEvent
	public void onIMCMessage(TickEvent event)
	{
		if (event.phase != TickEvent.Phase.END) return;
		ImmutableList<FMLInterModComms.IMCMessage> messages = FMLInterModComms.fetchRuntimeMessages(TomTom.INSTANCE);
		if (messages.size() > 0)
		{
			FMLInterModComms.IMCMessage message = messages.get(0);
			if(message.key.equalsIgnoreCase("setPointer"))
			{
				if(message.isNBTMessage())
				{
					NBTTagCompound tag = message.getNBTValue();
					if(!tag.hasKey("location"))
					{ return; }

					LogHelper.info("Valid gps message.");

					if (event.side == Side.SERVER)
					{
						EntityPlayerMP player = (EntityPlayerMP) MinecraftServer.getServer().getEntityFromUuid(new UUID(tag.getLong("uuid-most"), tag.getLong("uuid-least")));
						PacketHandler.INSTANCE.sendTo(new SetDestinationPacket(tag), player);
						return;
					}

					TomTomEvents.INSTANCE.setPos(BlockPos.fromLong(tag.getLong("location")));
					if(tag.hasKey("text"))
					{
						TomTomEvents.INSTANCE.setDisplayText(tag.getString("text"));
					}
					LogHelper.info(String.format("BlockPos: %s DisplayText: %s", TomTomEvents.INSTANCE.getPos().toString(), TomTomEvents.INSTANCE.getDisplayText()));
				}
			}
		}
	}
}
