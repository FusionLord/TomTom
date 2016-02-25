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
