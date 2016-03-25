package net.fusionlord.tomtom.rendering;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fusionlord.tomtom.events.TomTomEvents;
import net.fusionlord.tomtom.helpers.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by FusionLord on 3/20/2016.
 */
public class Arrow
{
	private IBakedModel bakedModel;
	private OBJModel model;
	private List<ResourceLocation> textures = new ArrayList<>();
	private int current = 0;
	private boolean useUniTextures = false;

	public Arrow(File jsonFile)
	{
		JsonParser parser = new JsonParser();
		try
		{
			JsonObject obj = (JsonObject) parser.parse(new FileReader(jsonFile));

			if(obj.has("useUniversalTextures"))
			{
				useUniTextures = obj.get("useUniversalTextures").getAsBoolean();
			}
			model = (OBJModel) OBJLoader.INSTANCE.loadModel(new ResourceLocation(ModInfo.MOD_ID, "models/" + obj.get("model").getAsString()));
			if(obj.has("textureFolder"))
			{
				for(File texture : new File(Arrow.class.getClassLoader().getResource("assets/tomtom/textures/" + obj.get("textureFolder").getAsString()).toURI()).listFiles(pathname -> {
					return pathname.getName().endsWith(".png");
				}))
				{
					textures.add(new ResourceLocation(ModInfo.MOD_ID, texture.getAbsolutePath().substring(texture.getAbsolutePath().lastIndexOf("\\tomtom\\textures\\") + "\\tomtom\\textures\\".length()).replace(".png", "")));
				}
			}
			if(textures.isEmpty() || textures.size() == 0)
			{ useUniTextures = true; }
			bake(model);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void bake(OBJModel model)
	{
		Function<ResourceLocation, TextureAtlasSprite> textureGetter = location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(getCurrentTexture().toString());
		bakedModel = model.bake(model.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter::apply);
	}

	public IBakedModel getModel()
	{
		return bakedModel;
	}

	private ResourceLocation getCurrentTexture()
	{
		if(current < 0)
		{ current = textures.size() + (useUniTextures ? TomTomEvents.getUniversalTextures().size() : 0); }
		if(current > textures.size() + (useUniTextures ? TomTomEvents.getUniversalTextures().size() : 0) - 1)
		{ current = 0; }
		if(current > textures.size() - 1)
		{
			return TomTomEvents.getUniversalTextures().get(current - textures.size());
		}
		return textures.get(current);
	}

	public void nextTexture()
	{
		current++;
		bake(model);
	}

	public void prevTexture()
	{
		current--;
		bake(model);
	}

	public void stitchTextures(TextureStitchEvent event)
	{
		textures.forEach(event.getMap()::registerSprite);
	}
}
