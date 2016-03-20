package net.fusionlord.tomtom.events;

import com.google.common.base.Function;
import net.fusionlord.tomtom.configuration.ConfigurationFile;
import net.fusionlord.tomtom.helpers.LogHelper;
import net.fusionlord.tomtom.helpers.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class TomTomEvents implements IResourceManagerReloadListener
{
	public static TomTomEvents INSTANCE;
	public float x = 0;
	public float y = 0;
	private Minecraft mc = Minecraft.getMinecraft();
	private ResourceLocation texture;
	private OBJModel.OBJBakedModel bakedModel;
	private double dist;
	private int ticker;
	private BlockPos pos;
	private String displayText;
	private boolean editMode;

	public TomTomEvents()
	{
		INSTANCE = this;
	}

	public BlockPos getPos()
	{
		return pos;
	}

	public void setPos(BlockPos pos)
	{
		this.pos = pos;
	}

	public String getDisplayText()
	{
		return displayText;
	}

	public void setDisplayText(String text)
	{
		this.displayText = StringUtils.isEmpty(text) ? "Destination" : text;
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
		LogHelper.info(">>>: Reloading arrow model now...");
		try
		{
			OBJLoader.instance.addDomain(ModInfo.MOD_ID);
			OBJModel model = (OBJModel) OBJLoader.instance.loadModel(new ResourceLocation(ModInfo.MOD_ID, "models/gps_arrow3.obj"));
			Function<ResourceLocation, TextureAtlasSprite> textureGetter = location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
			bakedModel = (OBJModel.OBJBakedModel) model.bake(model.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter);

			texture = new ResourceLocation(ModInfo.MOD_ID, "textures/arrow.png");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void update(TickEvent.ClientTickEvent event)
	{
		ticker++;
		if (editMode)
		{
			boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
			boolean ctrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
			float a = (shift ? .005f * (ctrl ? 10f : 1f) : .001f);
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				editMode = false;
				System.out.println("exiting edit mode");
				ConfigurationFile.setGPSPos(x, y);
				mc.displayGuiScreen(null);
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_UP))
			{
				y = MathHelper.clamp_float(y - a, 0, 1);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			{
				y = MathHelper.clamp_float(y + a, 0, 1);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			{
				x = MathHelper.clamp_float(x - a, 0,1);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			{
				x = MathHelper.clamp_float(x + a, 0, 1);
			}
		}
	}

	@SubscribeEvent
	public void updateHud(TickEvent.PlayerTickEvent event)
	{
		if (event.phase != TickEvent.Phase.END || getPos() == null) return;
		mc = Minecraft.getMinecraft();
		dist = Math.sqrt(event.player.getDistanceSq(getPos()));

		if (dist < 2D && !event.player.isDead && ticker % 300 == 0)
		{
			setPos(null);
		}
	}

	@SubscribeEvent
	public void drawHud(TickEvent.RenderTickEvent event)
	{
		if (!editMode)
		{
			if(event.phase != TickEvent.Phase.END || mc.thePlayer == null || getPos() == null)
				return;

			//if (mc.currentScreen != null && !editMode) return;
		}

		ScaledResolution scaledResolution = new ScaledResolution(mc);

		float scale = 20 * scaledResolution.getScaleFactor();

		EntityPlayer player = mc.thePlayer;
		BlockPos pos = player.getPosition();
		BlockPos target = editMode ? new BlockPos(0, 0, 0) : getPos();

		if (target == null) return;
		float distX = (float)target.getX() + .5f - (float)pos.getX();
		float distZ = (float)target.getZ() + .5f - (float)pos.getZ();
		float distY = (float)target.getY() + .5f - (float)pos.getY();

		double angleRadians = Math.atan2(distZ, distX);
		float angleDegrees = (float) Math.toDegrees(angleRadians);

		angleDegrees -= player.rotationYaw - 180;

		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		GlStateManager.enableRescaleNormal();
		RenderHelper.enableGUIStandardItemLighting();

		GlStateManager.translate(scaledResolution.getScaledWidth() * x, scaledResolution.getScaledHeight() * y, 500);
		if (distX > -.75f && distX < .75f && distZ > -.75f && distZ < .75f)
		{
			float f = distY < 0 ? 90 : -90;
			float bob = 5 * MathHelper.sin((float)ticker / 10f);
			GlStateManager.translate(0, bob - 10, 0);
			GlStateManager.rotate(f, 1f, 0f, 0f);
			GlStateManager.rotate(90, 0f, 1f, 0f);
			GlStateManager.rotate((ticker % 180) * 3, 1, 0, 0);
		}
		else
		{
			GlStateManager.rotate(-25f, 1f, 0f, 0f);
			GlStateManager.rotate(-angleDegrees, 0f, 1f, 0f);
		}
		GlStateManager.scale(scale, scale, scale);

		mc.getTextureManager().bindTexture(texture);
//		for(BakedQuad quad : bakedModel.)
//		{
//			LightUtil.renderQuadColor(Tessellator.getInstance().getBuffer(), quad, 0xFF0000);
//		}
		//mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(bakedModel, 1f, 0f, 0f, 1f);


		GlStateManager.scale(1, 1, 1);
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();

		if (editMode)
		{
			String s = "Use arrow keys to move the arrow and then press escape when done!";
			mc.fontRendererObj.drawStringWithShadow(s, scaledResolution.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(s) / 2, 10, Color.RED.hashCode());
			s = "Tip: shift = x5, shift + control = x10";
			mc.fontRendererObj.drawStringWithShadow(s, scaledResolution.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(s) / 2, 20, Color.RED.hashCode());
		}

		String s = String.format("%s - %sm", editMode ? "Edit Mode!" : getDisplayText(), (int)dist);
		mc.fontRendererObj.drawStringWithShadow(s, scaledResolution.getScaledWidth() * x - mc.fontRendererObj.getStringWidth(s) / 2, scaledResolution.getScaledHeight() * y + 18, Color.white.hashCode());
		s = String.format("X:%s Y:%s Z:%s", target.getX(), target.getY(), target.getZ());
		mc.fontRendererObj.drawStringWithShadow(s, scaledResolution.getScaledWidth() * x - mc.fontRendererObj.getStringWidth(s) / 2, scaledResolution.getScaledHeight() * y + 28, Color.white.hashCode());

	}

	public void enableEditMode()
	{
		editMode = true;
	}

	public void setPointer(BlockPos pos, String s)
	{
		setPos(pos);
		setDisplayText(s);
	}
}
