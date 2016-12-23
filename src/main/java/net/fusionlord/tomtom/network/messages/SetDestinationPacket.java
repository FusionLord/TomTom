package net.fusionlord.tomtom.network.messages;

import io.netty.buffer.ByteBuf;
import net.fusionlord.tomtom.events.TomTomEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by FusionLord on 2/23/2016.
 */
public class SetDestinationPacket implements IMessage
{
	private NBTTagCompound tagCompound;

	public SetDestinationPacket()
	{
	}

	public SetDestinationPacket(NBTTagCompound tagCompound)
	{
		this.tagCompound = tagCompound;
	}

	public SetDestinationPacket(BlockPos pos, String label)
	{
		tagCompound = new NBTTagCompound();
		tagCompound.setFloat("location", pos.toLong());
		tagCompound.setString("text", label);
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

	public static class HANDLER implements IMessageHandler<SetDestinationPacket, IMessage>
	{
		@Override
		public IMessage onMessage(SetDestinationPacket message, MessageContext ctx)
		{
			TomTomEvents.INSTANCE.setDisplayText(message.tagCompound.getString("text"));
			TomTomEvents.INSTANCE.setPos(BlockPos.fromLong(message.tagCompound.getLong("location")));
			return null;
		}
	}
}
