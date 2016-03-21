package net.fusionlord.tomtom.events;

import net.fusionlord.tomtom.configuration.ConfigurationFile;
import net.fusionlord.tomtom.helpers.ModInfo;
import net.fusionlord.tomtom.rendering.Arrow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TomTomEvents implements IResourceManagerReloadListener
{
	public static TomTomEvents INSTANCE;
	public float x = 0;
	public float y = 0;
	private Minecraft mc = Minecraft.getMinecraft();
	private boolean editMode;
	private boolean onMouse;
	private double dist;
	private int ticker;
	private int selectedIdx = 0;
	private int buffer = 40;
	private BlockPos pos;
	private String displayText;
	private List<Arrow> arrows;
	private Arrow selectedArrow;

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

	String getDisplayText()
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
		try
		{
			arrows = new ArrayList<>();
			OBJLoader.instance.addDomain(ModInfo.MOD_ID);
			for(File file : new File(TomTomEvents.class.getClassLoader().getResource("assets/tomtom/arrows/").toURI()).listFiles(File::isDirectory))
			{
				for(File jsonFile : file.listFiles(pathname -> {
					return pathname.getAbsolutePath().endsWith(".json");
				}))
				{
					arrows.add(new Arrow(jsonFile));
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		if(selectedArrow == null)
		{ selectedArrow = arrows.get(0); }
	}

	@SubscribeEvent
	public void mouse(MouseEvent event)
	{
		if(!editMode)
		{ return; }

		int mouseY = Display.getHeight() - event.y;
		int mouseX = event.x;
		int posY = (int) (y * Display.getHeight());
		int posX = (int) (x * Display.getWidth());

		if(Mouse.isButtonDown(mc.gameSettings.mainHand.opposite().ordinal()))
		{
			if(onMouse)
			{
				onMouse = false;
			}
			else
			{
				if(mouseX > posX - buffer && mouseX < posX + buffer && mouseY > posY - buffer && mouseY < posY + buffer)
				{
					onMouse = true;
				}
			}
		}

		boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
		if(shift)
		{
			if(event.dwheel < 0)
			{ selectedArrow.prevTexture(); }
			if(event.dwheel > 0)
			{ selectedArrow.nextTexture(); }
		}
		else
		{
			if(event.dwheel < 0)
			{
				if(selectedIdx - 1 < 0)
				{
					selectedIdx = arrows.size() - 1;
					selectedArrow = arrows.get(selectedIdx);
				}
				else
				{
					selectedIdx -= 1;
					selectedArrow = arrows.get(selectedIdx);
				}
			}
			if(event.dwheel > 0)
			{
				if(selectedIdx + 1 >= arrows.size())
				{
					selectedIdx = 0;
					selectedArrow = arrows.get(selectedIdx);
				}
				else
				{
					selectedIdx += 1;
					selectedArrow = arrows.get(selectedIdx);
				}
			}
		}

		if(onMouse)
		{
			y = MathHelper.clamp_float((float) mouseY / (float) Display.getHeight(), 0, 1);
			x = MathHelper.clamp_float((float) mouseX / (float) Display.getWidth(), 0, 1);
		}

	}

	@SubscribeEvent
	public void update(TickEvent.ClientTickEvent event)
	{
		ticker++;
		if (editMode)
		{
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			{
				disableEditMode();
			}

			boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
			boolean ctrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
			float a = (shift ? .005f * (ctrl ? 10f : 1f) : .001f);

			if(Keyboard.isKeyDown(Keyboard.KEY_UP))
			{
				y = MathHelper.clamp_float(y - a, 0, 1);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			{
				y = MathHelper.clamp_float(y + a, 0, 1);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			{
				x = MathHelper.clamp_float(x - a, 0, 1);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			{
				x = MathHelper.clamp_float(x + a, 0, 1);
			}
		}
	}

	@SubscribeEvent
	public void updateHud(TickEvent.PlayerTickEvent event)
	{
		if(!editMode)
		{
			if(event.phase != TickEvent.Phase.END || getPos() == null)
			{ return; }
			mc = Minecraft.getMinecraft();
			dist = Math.sqrt(event.player.getDistanceSq(getPos()));

			if(mc.currentScreen != null && !(mc.currentScreen instanceof GuiGameOver) && dist < 2D && !event.player.isDead && ticker % 300 == 0)
			{
				setPos(null);
			}
		}
	}

	@SubscribeEvent
	public void drawWorld(RenderWorldLastEvent event)
	{
		if(mc.thePlayer == null || (getPos() == null && !editMode))
		{ return; }
		BlockPos target = getPos();

		if(target == null)
		{ return; }

		double playerX = mc.thePlayer.lastTickPosX + ((mc.thePlayer.posX) - mc.thePlayer.lastTickPosX) * event.partialTicks;
		double playerY = mc.thePlayer.lastTickPosY + ((mc.thePlayer.posY) - mc.thePlayer.lastTickPosY) * event.partialTicks;
		double playerZ = mc.thePlayer.lastTickPosZ + ((mc.thePlayer.posZ) - mc.thePlayer.lastTickPosZ) * event.partialTicks;

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
			GlStateManager.rotate((ticker % 180) * 3, 1, 0, 0);
			mc.getTextureManager().bindTexture(selectedArrow.getCurrentTexture());
			mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(selectedArrow.getModel(), 1f, 0f, 0f, 0f);
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
			mc.getTextureManager().bindTexture(selectedArrow.getCurrentTexture());
			mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(selectedArrow.getModel(), 1f, 0f, 0f, 0f);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.popMatrix();
		}
	}

	@SubscribeEvent
	public void drawHud(TickEvent.RenderTickEvent event)
	{
		if(event.phase != TickEvent.Phase.END || mc.thePlayer == null || (getPos() == null && !editMode) || mc.gameSettings.hideGUI || !ConfigurationFile.renderHUD)
		{ return; }

		if(mc.currentScreen != null)
		{ return; }

		ScaledResolution scaledResolution = new ScaledResolution(mc);

		float scale = 20 * scaledResolution.getScaleFactor();

		double playerX = mc.thePlayer.lastTickPosX + ((mc.thePlayer.posX) - mc.thePlayer.lastTickPosX) * event.renderTickTime;
		double playerY = mc.thePlayer.lastTickPosY + ((mc.thePlayer.posY) - mc.thePlayer.lastTickPosY) * event.renderTickTime;
		double playerZ = mc.thePlayer.lastTickPosZ + ((mc.thePlayer.posZ) - mc.thePlayer.lastTickPosZ) * event.renderTickTime;

		BlockPos target = editMode ? mc.thePlayer.getPosition() : getPos();

		if (target == null) return;
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

		if(editMode)
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, -500);
			Gui.drawRect(-buffer, -buffer, buffer, buffer, Color.RED.hashCode());
			Gui.drawRect(-buffer + 1, -buffer + 1, buffer - 1, buffer - 1, Color.black.hashCode());
			GlStateManager.popMatrix();
		}

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

		GlStateManager.color(1f, 0, 0);

		mc.getTextureManager().bindTexture(selectedArrow.getCurrentTexture());
		mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(selectedArrow.getModel(), .75f, 0f, 0f, 1f);


		GlStateManager.scale(1, 1, 1);
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		RenderHelper.enableGUIStandardItemLighting();

		String s = String.format("%s - %sm", editMode ? "Edit Mode!" : getDisplayText(), (int) dist);
		mc.fontRendererObj.drawStringWithShadow(s, scaledResolution.getScaledWidth() * x - mc.fontRendererObj.getStringWidth(s) / 2, scaledResolution.getScaledHeight() * y + 18, Color.white.hashCode());
		s = String.format("X:%s Y:%s Z:%s", target.getX(), target.getY(), target.getZ());
		mc.fontRendererObj.drawStringWithShadow(s, scaledResolution.getScaledWidth() * x - mc.fontRendererObj.getStringWidth(s) / 2, scaledResolution.getScaledHeight() * y + 28, Color.white.hashCode());

		if (editMode)
		{
			s = "Use arrow keys to move the arrow and then press space when done!";
			mc.fontRendererObj.drawStringWithShadow(s, scaledResolution.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(s) / 2, 10, Color.RED.hashCode());
			s = "Tip: shift = x5, shift + control = x10";
			mc.fontRendererObj.drawStringWithShadow(s, scaledResolution.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(s) / 2, 20, Color.RED.hashCode());
		}

		GlStateManager.popMatrix();

	}

	public void enableEditMode()
	{
		if(!ConfigurationFile.renderHUD)
		{ return; }
		editMode = true;
		System.setProperty("fml.noGrab", "true");
		mc.mouseHelper.ungrabMouseCursor();
		mc.displayGuiScreen(new GuiScreen()
		{
		});
	}

	private void disableEditMode()
	{
		editMode = false;
		onMouse = false;
		System.setProperty("fml.noGrab", "false");
		mc.mouseHelper.grabMouseCursor();
		ConfigurationFile.setGPSPos(x, y);
	}


	public void setPointer(BlockPos pos, String s)
	{
		setPos(pos);
		setDisplayText(s);
	}
}
