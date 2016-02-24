package net.fusionlord.tomtom.commands;

import com.sun.deploy.util.SessionState;
import com.sun.deploy.util.StringUtils;
import net.fusionlord.tomtom.events.ClientTickHandler;
import net.fusionlord.tomtom.network.PacketHandler;
import net.fusionlord.tomtom.network.messages.MessageSetDestination;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.Arrays;
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
		System.out.println("processing");
		if (argString.length < 3)
		{
			sender.addChatMessage(new ChatComponentText("Usage: " + getCommandUsage(sender)));
		}
		BlockPos pos = new BlockPos(Integer.parseInt(argString[0]), Integer.parseInt(argString[1]), Integer.parseInt(argString[2]));

		String displayText = "";

		for (int i = 3; i < argString.length; i++)
		{
			displayText = displayText + " " + argString[i];
		}

		ClientTickHandler.INSTANCE.setPos(pos);
		ClientTickHandler.INSTANCE.setDisplayText(displayText, "Destination");
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
