package net.fusionlord.tomtom.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by FusionLord on 2/29/2016.
 */
public class VillageRequestPacket implements IMessage
{
	public VillageRequestPacket()
	{
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(0);
	}

	public static class HANDLER implements IMessageHandler<VillageRequestPacket, SetDestinationPacket>
	{
		@Override
		public SetDestinationPacket onMessage(VillageRequestPacket message, MessageContext ctx)
		{
			BlockPos pos;
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			World world = player.getEntityWorld();
			VillageCollection vc = world.getVillageCollection();
			pos = vc.getNearestVillage(player.getPosition(), 255).getCenter();
			if(pos != null)
			{
				String s = "[\"\",{\"text\":\"This is \"},{\"text\":\"[CLOSE]\",\"color\":\"dark_purple\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"As per Mojang.\",\"color\":\"blue\"}]}}},{\"text\":\" to a village.\",\"color\":\"none\"}]";
				ITextComponent chatComponent = ITextComponent.Serializer.jsonToComponent(s);
				player.addChatComponentMessage(chatComponent);
				return new SetDestinationPacket(pos, "Village");
			}
			return null;
		}
	}
}
