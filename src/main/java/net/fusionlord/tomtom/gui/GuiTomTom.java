package net.fusionlord.tomtom.gui;

import net.fusionlord.tomtom.events.TomTomEvents;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;

import java.awt.*;
import java.io.IOException;

/**
 * Created by FusionLord on 3/24/2016.
 */
public class GuiTomTom extends GuiScreen
{
	private int ticker = 0;
	private GuiScreen parentScreen;
	private GuiButton nextArrow, prevArrow, nextSkin, prevSkin, moveHUD, close;
	private boolean moveMode, prevMoveMode;

	public GuiTomTom(GuiScreen gui)
	{
		this.parentScreen = gui;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		ScaledResolution resolution = new ScaledResolution(mc);
		width = resolution.getScaledWidth();
		height = resolution.getScaledHeight();

		nextArrow = new GuiButton(buttonList.size(), width / 2 + 35, height / 2 - 50, 85, 20, I18n.translateToLocal("tomtom.button.nextarrow.name"));
		prevArrow = new GuiButton(buttonList.size(), width / 2 - 120, height / 2 - 50, 85, 20, I18n.translateToLocal("tomtom.button.prevarrow.name"));
		nextSkin = new GuiButton(buttonList.size(), width / 2 + 35, height / 2 + 25, 85, 20, I18n.translateToLocal("tomtom.button.nextskin.name"));
		prevSkin = new GuiButton(buttonList.size(), width / 2 - 120, height / 2 + 25, 85, 20, I18n.translateToLocal("tomtom.button.prevskin.name"));

		moveHUD = new GuiButton(buttonList.size(), width / 2 - 120, height / 2 + 50, 240, 20, I18n.translateToLocal("tomtom.button.movehud.name"));
		close = new GuiButton(buttonList.size(), width / 2 - 120, height / 2 + 80, 240, 20, I18n.translateToLocal("tomtom.button.close.name"));

		buttonList.add(nextArrow);
		buttonList.add(prevArrow);
		buttonList.add(nextSkin);
		buttonList.add(prevSkin);

		buttonList.add(moveHUD);
		buttonList.add(close);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		if(moveMode && !prevMoveMode && mouseButton == 0)
		{
			prevMoveMode = true;
			moveMode = false;
			TomTomEvents.INSTANCE.setRenderPos((float) mouseX / (float) width, (float) mouseY / (float) height);
			for(GuiButton button1 : buttonList)
			{
				button1.enabled = true;
			}
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if(button == nextArrow)
		{
			TomTomEvents.INSTANCE.nextArrow();
		}
		if(button == prevArrow)
		{
			TomTomEvents.INSTANCE.prevArrow();
		}
		if(button == nextSkin)
		{
			TomTomEvents.INSTANCE.getSelectedArrow().nextTexture();
		}
		if(button == prevSkin)
		{
			TomTomEvents.INSTANCE.getSelectedArrow().prevTexture();
		}
		if(button == moveHUD)
		{
			moveMode = true;
			for(GuiButton button1 : buttonList)
			{
				button1.enabled = false;
			}
		}
		if(button == close)
		{
			mc.displayGuiScreen(parentScreen);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		ticker++;
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);

		ScaledResolution resolution = new ScaledResolution(mc);
		float scale = 20 * resolution.getScaleFactor();
		int x = resolution.getScaledWidth() / 2;
		int y = resolution.getScaledHeight() / 2;

		drawArrow(x, y, scale);

		//		LogHelper.info("moveMode: " + moveMode);
		if(moveMode)
		{
			if(prevMoveMode)
			{ prevMoveMode = false; }

			drawRect(0, 0, width, height, new Color(255, 255, 255, 64).hashCode());
			drawHorizontalLine(0, width, mouseY, Color.red.hashCode());
			drawVerticalLine(mouseX, 0, height, Color.red.hashCode());
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, 100);
			drawArrow(mouseX, mouseY, scale);
			GlStateManager.popMatrix();

			drawCenteredString(fontRendererObj, I18n.translateToLocal("tomtom.label.editmode.name"), mouseX, mouseY + 15, Color.white.hashCode());
			drawCenteredString(fontRendererObj, I18n.translateToLocal("tomtom.label.editmode.coords.name"), mouseX, mouseY + 25, Color.white.hashCode());

			drawCenteredString(fontRendererObj, I18n.translateToLocal("tomtom.label.movemessage.name"), width / 2, 5, Color.GREEN.hashCode());
		}
	}

	private void drawArrow(int x, int y, float scale)
	{
		GlStateManager.pushMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.rotate(45, 1, 2, .5f);
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.popMatrix();
		GlStateManager.enableDepth();
		GlStateManager.disableCull();
		GlStateManager.enableRescaleNormal();
		GlStateManager.translate(x, y, 50);
		GlStateManager.translate(0, 5 * MathHelper.sin((float) ticker / 10f) - 10, 0);
		GlStateManager.rotate(90, 1f, 0f, 0f);
		GlStateManager.rotate(90, 0f, 1f, 0f);
		GlStateManager.rotate((ticker % 360) * 3, 1, 0, 0);

		GlStateManager.scale(scale, scale, scale);

		GlStateManager.color(0, 0, 0, 1);
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(TomTomEvents.INSTANCE.getSelectedArrow().getModel(), 1f, 0f, 0f, 0f);
		GlStateManager.scale(1, 1, 1);
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableDepth();
		GlStateManager.popMatrix();
	}
}
