package net.fusionlord.tomtom.network.messages;

import io.netty.buffer.ByteBuf;
import net.fusionlord.tomtom.events.ClientTickHandler;
import net.fusionlord.tomtom.helpers.LogHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by FusionLord on 2/23/2016.
 */
public class MessageSetDestination implements IMessage
{
	private NBTTagCompound tagCompound;

	public MessageSetDestination() {}

	public MessageSetDestination(NBTTagCompound tagCompound)
	{
		this.tagCompound = tagCompound;
		LogHelper.info(">>> Sending packet!");
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.tagCompound = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeTag(buf, tagCompound);
	}

	public static class HANDLER implements IMessageHandler<MessageSetDestination, IMessage>
	{
		@Override
		public IMessage onMessage(MessageSetDestination message, MessageContext ctx)
		{
			LogHelper.info(">>> packet recieved!");
			ClientTickHandler.INSTANCE.setDisplayText(message.tagCompound.getString("text"), "Destination!");
			ClientTickHandler.INSTANCE.setPos(BlockPos.fromLong(message.tagCompound.getLong("location")));
			return null;
		}
	}
}
