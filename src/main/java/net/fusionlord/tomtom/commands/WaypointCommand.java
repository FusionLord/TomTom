package net.fusionlord.tomtom.commands;

import net.fusionlord.tomtom.events.TomTomEvents;
import net.fusionlord.tomtom.network.PacketHandler;
import net.fusionlord.tomtom.network.messages.VillageRequestPacket;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FusionLord on 2/23/2016.
 */
public class WaypointCommand implements ICommand
{
	private final List aliases;

	public WaypointCommand()
	{
		aliases = new ArrayList();
		aliases.add("tomtom");
		aliases.add("waypoint");
		aliases.add("way");

	}

	@Override
	public String getCommandName()
	{
		return "way";
	}

	@Override
	public String getCommandUsage(ICommandSender var1)
	{
		return "way <x> <y> <x> [display text]";

	}

	@Override
	public List getCommandAliases()
	{
		return this.aliases;

	}

	@Override
	public void processCommand(ICommandSender sender, String[] argString)
	{
		if (argString.length < 3 && argString.length != 1)
		{
			fail(sender);
			return;
		}

		if (argString.length == 1)
		{
			if (argString[0].equalsIgnoreCase("clear"))
			{
				TomTomEvents.INSTANCE.setPos(null);
				TomTomEvents.INSTANCE.setDisplayText("");
				return;
			}
			if (argString[0].equalsIgnoreCase("edit"))
			{
				TomTomEvents.INSTANCE.enableEditMode();
				return;
			}
			if(argString[0].equalsIgnoreCase("struct"))
			{
				PacketHandler.INSTANCE.sendToServer(new VillageRequestPacket());
			}
		}
		else
		{
			int x, y, z;
			if (StringUtils.isNumeric(argString[0]) || argString[0].equalsIgnoreCase("~"))
			{
				x = argString[0].equalsIgnoreCase("~") ? sender.getPosition().getX() : Integer.parseInt(argString[0]);
			}
			else
			{
				fail(sender);
				return;
			}

			if (StringUtils.isNumeric(argString[1]) || argString[1].equalsIgnoreCase("~"))
			{
				y = argString[1].equalsIgnoreCase("~") ? sender.getPosition().getY() : Integer.parseInt(argString[1]);
			}
			else
			{
				fail(sender);
				return;
			}

			if (StringUtils.isNumeric(argString[2]) || argString[2].equalsIgnoreCase("~"))
			{
				z = argString[2].equalsIgnoreCase("~") ? sender.getPosition().getZ() : Integer.parseInt(argString[2]);
			}
			else
			{
				fail(sender);
				return;
			}

			String displayText = "";

			for(int i = 3; i < argString.length; i++)
			{
				displayText = displayText + " " + argString[i];
			}

			TomTomEvents.INSTANCE.setPos(new BlockPos(x, y, z));
			TomTomEvents.INSTANCE.setDisplayText(displayText);
		}
	}

	private void fail(ICommandSender sender)
	{
		sender.addChatMessage(new ChatComponentText("Usage: " + getCommandUsage(sender)));
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1)
	{
		return true;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
	{
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2)
	{
		return false;

	}

	@Override
	public int compareTo(ICommand o)
	{
		return 0;
	}
}
