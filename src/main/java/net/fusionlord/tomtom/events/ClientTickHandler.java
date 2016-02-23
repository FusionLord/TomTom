package net.fusionlord.tomtom.events;

import com.google.common.base.Function;
import net.fusionlord.tomtom.helpers.LogHelper;
import net.fusionlord.tomtom.helpers.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.IOException;

public class ClientTickHandler implements IResourceManagerReloadListener
{
	public static ClientTickHandler INSTANCE;

	private Minecraft mc = Minecraft.getMinecraft();
	ResourceLocation texture;
	IBakedModel model;
	private double dist;
	private int ticker;

	private BlockPos pos;
	private String displayText;

	public ClientTickHandler()
	{
		INSTANCE = this;
	}

	public void setDisplayText(String text, String def)
	{
		this.displayText = StringUtils.isEmpty(text) ? def : text;
	}

	public void setPos(BlockPos pos)
	{
		this.pos = pos;
	}

	public BlockPos getPos()
	{
		return pos;
	}

	public String getDisplayText()
	{
		return displayText;
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
		try
		{
			OBJLoader.instance.addDomain(ModInfo.MOD_ID);
			OBJModel gpsArrow = (OBJModel) OBJLoader.instance.loadModel(new ResourceLocation(ModInfo.MOD_ID, "models/gps_arrow.obj"));
			Function<ResourceLocation, TextureAtlasSprite> textureGetter = location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
			model = gpsArrow.bake(gpsArrow.getDefaultState(), DefaultVertexFormats.ITEM, textureGetter);

			texture = new ResourceLocation(ModInfo.MOD_ID, "textures/arrow.png");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void updateHud(TickEvent.PlayerTickEvent event)
	{
		if (event.phase != TickEvent.Phase.END || getPos() == null) return;
		mc = Minecraft.getMinecraft();
		dist = Math.sqrt(event.player.getDistanceSq(getPos()));
		if (dist < 3D && !event.player.isDead && ticker++ > 300)
		{
			setPos(null);
			ticker = 0;
		}
	}

	@SubscribeEvent
	public void drawHud(TickEvent.RenderTickEvent event)
	{
		if (event.phase != TickEvent.Phase.END || mc.thePlayer == null || getPos() == null /*|| mc.currentScreen != null*/) return;

		ScaledResolution scaledResolution = new ScaledResolution(mc);

		float scale = 50 * scaledResolution.getScaleFactor();

		EntityPlayer player = mc.thePlayer;
		BlockPos pos = player.getPosition();
		BlockPos target = getPos();

		if (target == null) return;
		float distX = (float)target.getX() - (float)pos.getX();
		float distZ = (float)target.getZ() - (float)pos.getZ();

		double angleRadians = Math.atan2(distZ, distX);
		float angleDegrees = (float) Math.toDegrees(angleRadians);

		angleDegrees -= player.rotationYaw - 180;

		mc.getTextureManager().bindTexture(texture);
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();

		GlStateManager.translate(scaledResolution.getScaledWidth() / 2, scaledResolution.getScaledHeight() - 105, 500);
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.rotate(-45f, 1f, 0f, 0f);
		GlStateManager.rotate(-angleDegrees, 0f, 1f, 0f);

		RenderHelper.enableStandardItemLighting();
		mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(model, 1f, 1f, 1f, 1f);
		RenderHelper.disableStandardItemLighting();

		GlStateManager.popMatrix();

//		String s = getDisplayText();
//		mc.fontRendererObj.drawStringWithShadow(s, scaledResolution.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(s) / 2, scaledResolution.getScaledHeight() - 82, Color.white.hashCode());
//		s = String.format("%sm", (int)dist);
//		mc.fontRendererObj.drawStringWithShadow(s, scaledResolution.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(s) / 2, scaledResolution.getScaledHeight() - 72, Color.white.hashCode());
//		s = String.format("X:%s Y:%s Z:%s", target.getX(), target.getY(), target.getZ());
//		mc.fontRendererObj.drawStringWithShadow(s, scaledResolution.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(s) / 2, scaledResolution.getScaledHeight() - 62, Color.white.hashCode());
	}
}
