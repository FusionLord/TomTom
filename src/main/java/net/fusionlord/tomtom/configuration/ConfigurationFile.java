package net.fusionlord.tomtom.configuration;

/**
 * Created by FusionLord on 2/24/2016.
 */


import net.fusionlord.tomtom.events.TomTomEvents;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;

@SideOnly(Side.CLIENT)
public class ConfigurationFile {
	public static Configuration configuration;

	public static boolean renderHUD, renderWaypoint, renderFoot;

	public ConfigurationFile(File configFile)
	{
		if (configuration == null) {
			configuration = new Configuration(configFile);
			loadConfiguration();
		}
	}

	public static void loadConfiguration() {
		// General Configuration
		configuration.setCategoryLanguageKey("general", "config.general");

		TomTomEvents.INSTANCE.x = configuration.getFloat("xPos", "hidden", .5f, 0f, 1f, "X position of the gps arrow.", "config.tomtom.xpos.comment");
		TomTomEvents.INSTANCE.y = configuration.getFloat("yPos", "hidden", .5f, 0f, 1f, "Y position of the gps arrow.", "config.tomtom.ypos.comment");
		TomTomEvents.INSTANCE.cooldownLimit = configuration.getInt("Timer", "general", 3, 0, Integer.MAX_VALUE, "Number of seconds until waypoint is removed", "config.tomtom.timer.comment");
		TomTomEvents.INSTANCE.drawCoords = configuration.getBoolean("drawCoords", "general", true, "Should the coordinates be drawn on the hud?");
		TomTomEvents.INSTANCE.textRed = configuration.getInt("textRed", "general", 255, 0, 255, "The red value of the HUD Text color.");
		TomTomEvents.INSTANCE.textGreen = configuration.getInt("textGreen", "general", 255, 0, 255, "The green value of the HUD Text color.");
		TomTomEvents.INSTANCE.textBlue = configuration.getInt("textBlue", "general", 255, 0, 255, "The blue value of the HUD Text color.");

		renderHUD = configuration.getBoolean("Render HUD", "general", true, "Render the Heads Up Display arrow?", "config.tomtom.renderhud.comment");
		renderWaypoint = configuration.getBoolean("Render Waypoint", "general", false, "Render the bobbing arrow waypoint?", "config.tomtom.renderwaypoint.comment");
		renderFoot = configuration.getBoolean("Render Foot", "general", false, "Render the foot arrow?", "config.tomtom.renderfoot.comment");

		configuration.save();
	}

	public static void setGPSPos(float x, float y)
	{
		configuration.getCategory("hidden").get("xPos").setValue(x);
		configuration.getCategory("hidden").get("yPos").setValue(y);
		configuration.save();
	}

	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
		loadConfiguration();
	}
}
