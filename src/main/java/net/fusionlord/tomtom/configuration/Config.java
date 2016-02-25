package net.fusionlord.tomtom.configuration;

import net.fusionlord.tomtom.helpers.ModInfo;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.Arrays;

public class Config extends GuiConfig {
	public Config(GuiScreen parentScreen) {
		super(parentScreen,
				Arrays.asList(new IConfigElement[]{
						new ConfigElement(ConfigurationFile.configuration.getCategory("general"))
				}),
				ModInfo.MOD_ID, false, false, "TomTom Configuration");
	}
}