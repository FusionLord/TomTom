package net.fusionlord.tomtom.events;

import net.fusionlord.tomtom.configuration.ConfigurationFile;
import net.fusionlord.tomtom.helpers.ModInfo;
import net.fusionlord.tomtom.rendering.Arrow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class TomTomEvents implements IResourceManagerReloadListener
{
	public static TomTomEvents INSTANCE;
	private static List<ResourceLocation> universalTextures;
	public float x = 0;
	public float y = 0;
	private Minecraft mc = Minecraft.getMinecraft();
	private double dist;
	private int ticker;
	private int buffer = 40;
	private BlockPos pos;
	private String displayText;
	private List<Arrow> arrows;
	private int selectedIdx = 0;

	public TomTomEvents()
	{
		INSTANCE = this;
	}

	public static List<ResourceLocation> getUniversalTextures()
	{
		return universalTextures;
	}

	public void setPos(BlockPos pos)
	{
		this.pos = pos;
	}

	public void setDisplayText(String text)
	{
		this.displayText = StringUtils.isEmpty(text) ? "Destination" : text;
	}

	@Override
	@SuppressWarnings("all")
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
		try
		{
			universalTextures = new ArrayList<>();
			for(File texture : new File(TomTomEvents.class.getClassLoader().getResource("assets/tomtom/textures/arrows/universal/").toURI()).listFiles(pathname -> {
				return pathname.getName().endsWith(".png");
			}))
			{
				universalTextures.add(new ResourceLocation(ModInfo.MOD_ID, "arrows/universal/" + texture.getName().replace(".png", "")));
			}
			arrows = new ArrayList<>();
			OBJLoader.INSTANCE.addDomain(ModInfo.MOD_ID);
			for(File jsonFile : new File(TomTomEvents.class.getClassLoader().getResource("assets/tomtom/arrows/").toURI()).listFiles(pathname -> {
				return pathname.getAbsolutePath().endsWith(".json");
			}))
			{
				arrows.add(new Arrow(jsonFile));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void textureStitchEvent(TextureStitchEvent event)
	{
		universalTextures.forEach(event.getMap()::registerSprite);
		for(Arrow arrow : arrows)
		{
			arrow.stitchTextures(event);
		}
	}

	@SubscribeEvent
	public void updateHud(TickEvent.PlayerTickEvent event)
	{
		if(event.phase != TickEvent.Phase.END || pos == null)
		{ return; }
		mc = Minecraft.getMinecraft();
		dist = Math.sqrt(event.player.getDistanceSq(pos));

		if(mc.currentScreen != null && !(mc.currentScreen instanceof GuiGameOver) && dist < 2D && !event.player.isDead && ticker % 300 == 0)
		{
			setPos(null);
		}
	}

	@SubscribeEvent
	public void drawWorld(RenderWorldLastEvent event)
	{
		ticker++;
		if(mc.thePlayer == null || pos == null)
		{ return; }
		BlockPos target = pos;

		double playerX = mc.thePlayer.lastTickPosX + ((mc.thePlayer.posX) - mc.thePlayer.lastTickPosX) * event.getPartialTicks();
		double playerY = mc.thePlayer.lastTickPosY + ((mc.thePlayer.posY) - mc.thePlayer.lastTickPosY) * event.getPartialTicks();
		double playerZ = mc.thePlayer.lastTickPosZ + ((mc.thePlayer.posZ) - mc.thePlayer.lastTickPosZ) * event.getPartialTicks();

		if(ConfigurationFile.renderWaypoint)
		{
			GlStateManager.pushMatrix();
			GlStateManager.enableRescaleNormal();
			GlStateManager.pushMatrix();
			GlStateManager.rotate(180f, 1, 0, 0);
			RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.popMatrix();
			float bob = .5f * MathHelper.sin((float) ticker / 10f);
			GlStateManager.translate(-playerX, -playerY, -playerZ);
			GlStateManager.translate(target.getX() + .5f, mc.theWorld.getTopSolidOrLiquidBlock(target).getY() + 1.5f, target.getZ() + .5f);
			GlStateManager.translate(0, bob, 0);
			GlStateManager.rotate(-90, 1f, 0f, 0f);
			GlStateManager.rotate(90, 0f, 1f, 0f);
			GlStateManager.rotate((ticker % 360) * 3, 1, 0, 0);
			mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(getSelectedArrow().getModel(), 1f, 0f, 0f, 0f);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.popMatrix();
		}

		if(ConfigurationFile.renderFoot)
		{

			float distX = (float) target.getX() + .5f - (float) playerX;
			float distZ = (float) target.getZ() + .5f - (float) playerZ;

			double angleRadians = Math.atan2(distZ, distX);
			float angleDegrees = (float) Math.toDegrees(angleRadians);

			angleDegrees -= mc.thePlayer.rotationYaw - 180;

			GlStateManager.pushMatrix();
			RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.rotate(-mc.thePlayer.rotationYaw, 0, 1, 0);
			GlStateManager.rotate(180, 0, 1, 0);
			GlStateManager.rotate(-angleDegrees, 0f, 1f, 0f);
			mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(getSelectedArrow().getModel(), 1f, 0f, 0f, 0f);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.popMatrix();
		}
	}

	@SubscribeEvent
	public void drawHud(TickEvent.RenderTickEvent event)
	{
		if(event.phase != TickEvent.Phase.END || mc.thePlayer == null || pos == null || mc.gameSettings.hideGUI || !ConfigurationFile.renderHUD)
		{ return; }

		if(mc.currentScreen != null)
		{ return; }

		ScaledResolution scaledResolution = new ScaledResolution(mc);

		float scale = 20 * scaledResolution.getScaleFactor();

		double playerX = mc.thePlayer.lastTickPosX + ((mc.thePlayer.posX) - mc.thePlayer.lastTickPosX) * event.renderTickTime;
		double playerY = mc.thePlayer.lastTickPosY + ((mc.thePlayer.posY) - mc.thePlayer.lastTickPosY) * event.renderTickTime;
		double playerZ = mc.thePlayer.lastTickPosZ + ((mc.thePlayer.posZ) - mc.thePlayer.lastTickPosZ) * event.renderTickTime;

		BlockPos target = pos;

		float distX = (float) target.getX() + .5f - (float) playerX;
		float distY = (float) target.getY() + .5f - (float) playerY;
		float distZ = (float) target.getZ() + .5f - (float) playerZ;

		double angleRadians = Math.atan2(distZ, distX);
		float angleDegrees = (float) Math.toDegrees(angleRadians);

		angleDegrees -= mc.thePlayer.rotationYaw - 180;

		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		GlStateManager.enableRescaleNormal();
		GlStateManager.pushMatrix();
		GlStateManager.rotate(45, 1, 2, .5f);
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.popMatrix();

		GlStateManager.translate(scaledResolution.getScaledWidth() * x, scaledResolution.getScaledHeight() * y, 500);

		if (distX > -.75f && distX < .75f && distZ > -.75f && distZ < .75f)
		{
			float f = distY < 0 ? 90 : -90;
			float bob = 5 * MathHelper.sin((float)ticker / 10f);
			GlStateManager.translate(0, bob - 10, 0);
			GlStateManager.rotate(f, 1f, 0f, 0f);
			GlStateManager.rotate(90, 0f, 1f, 0f);
			GlStateManager.rotate((ticker % 360) * 3, 1, 0, 0);
		}
		else
		{
			GlStateManager.rotate(-25f, 1f, 0f, 0f);
			GlStateManager.rotate(-angleDegrees, 0f, 1f, 0f);
		}

		GlStateManager.scale(scale, scale, scale);

		GlStateManager.color(1f, 0, 0);

		mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(getSelectedArrow().getModel(), .75f, 0f, 0f, 1f);

		GlStateManager.scale(1, 1, 1);
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		RenderHelper.enableGUIStandardItemLighting();

		String s = String.format("%s - %sm", displayText, (int) dist);
		mc.fontRendererObj.drawStringWithShadow(s, scaledResolution.getScaledWidth() * x - mc.fontRendererObj.getStringWidth(s) / 2, scaledResolution.getScaledHeight() * y + 18, Color.white.hashCode());
		s = String.format("X:%s Y:%s Z:%s", target.getX(), target.getY(), target.getZ());
		mc.fontRendererObj.drawStringWithShadow(s, scaledResolution.getScaledWidth() * x - mc.fontRendererObj.getStringWidth(s) / 2, scaledResolution.getScaledHeight() * y + 28, Color.white.hashCode());

		GlStateManager.popMatrix();
	}

	public void setPointer(BlockPos pos, String s)
	{
		setPos(pos);
		setDisplayText(s);
	}

	public void nextArrow()
	{
		selectedIdx++;
	}

	public void prevArrow()
	{
		selectedIdx--;
	}

	public Arrow getSelectedArrow()
	{
		if(selectedIdx > arrows.size() - 1)
		{ selectedIdx = 0; }
		if(selectedIdx < 0)
		{ selectedIdx = arrows.size() - 1; }
		return arrows.get(selectedIdx);
	}

	public void setRenderPos(float x, float y)
	{
		this.x = x;
		this.y = y;
		ConfigurationFile.setGPSPos(x, y);
	}
}
