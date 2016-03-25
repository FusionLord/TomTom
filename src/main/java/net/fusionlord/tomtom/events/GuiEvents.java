package net.fusionlord.tomtom.events;

import net.fusionlord.tomtom.gui.GuiTomTom;
import net.fusionlord.tomtom.helpers.ModInfo;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by FusionLord on 3/25/2016.
 */
public class GuiEvents
{
	private GuiButton guiTomTom;

	@SubscribeEvent
	public void onOpenGui(GuiScreenEvent.InitGuiEvent.Post event)
	{
		if(event.getGui() instanceof GuiConfig)
		{
			GuiConfig config = (GuiConfig) event.getGui();
			if(config.modID.equals(ModInfo.MOD_ID) && config.configID == null)
			{
				int strWidth = event.getGui().mc.fontRendererObj.getStringWidth(I18n.translateToLocal("tomtom.button.editarrow.name")) + 6;
				event.getButtonList().add(guiTomTom = new GuiButton(event.getButtonList().size(), 5, 2, strWidth, 20, I18n.translateToLocal("tomtom.button.editarrow.name")));
			}
		}
	}

	@SubscribeEvent
	public void onGuiAction(GuiScreenEvent.ActionPerformedEvent event)
	{
		if(event.getGui() instanceof GuiConfig)
		{
			GuiConfig config = (GuiConfig) event.getGui();
			if(config.modID.equals(ModInfo.MOD_ID) && config.configID == null)
			{
				if(event.getButton() == guiTomTom)
				{
					event.getGui().mc.displayGuiScreen(new GuiTomTom(event.getGui()));
				}
			}
		}
	}
}
