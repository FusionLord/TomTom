package net.fusionlord.tomtom.configuration;

/**
 * Created by FusionLord on 2/24/2016.
 */


import net.fusionlord.tomtom.events.TomTomEvents;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

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

		TomTomEvents.INSTANCE.x = configuration.getFloat("xPos", "general", .5f, 0f, 1f, "X position of the gps arrow.", "config.tomtom.xpos.comment");
		TomTomEvents.INSTANCE.y = configuration.getFloat("yPos", "general", .5f, 0f, 1f, "Y position of the gps arrow.", "config.tomtom.ypos.comment");
		renderHUD = configuration.getBoolean("renderHUD", "general", true, "Render the Heads Up Display arrow?", "config.tomtom.renderhud.comment");
		renderWaypoint = configuration.getBoolean("renderWaypoint", "general", false, "Render the bobbing arrow waypoint?", "config.tomtom.renderwaypoint.comment");
		renderFoot = configuration.getBoolean("renderFoot", "general", false, "Render the foot arrow?", "config.tomtom.renderfoot.comment");

		configuration.save();
	}

	public static void setGPSPos(float x, float y)
	{
		configuration.getCategory("general").get("xPos").setValue(x);
		configuration.getCategory("general").get("yPos").setValue(x);
		configuration.save();
	}

	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
		loadConfiguration();
	}
}
