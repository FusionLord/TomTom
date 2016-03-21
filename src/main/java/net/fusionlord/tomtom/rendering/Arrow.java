package net.fusionlord.tomtom.rendering;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fusionlord.tomtom.helpers.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
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
	private List<ResourceLocation> textures = new ArrayList<>();
	private int current = 0;

	public Arrow(File jsonFile)
	{
		JsonParser parser = new JsonParser();
		try
		{
			JsonObject obj = (JsonObject) parser.parse(new FileReader(jsonFile));

			OBJModel model = (OBJModel) OBJLoader.instance.loadModel(new ResourceLocation(ModInfo.MOD_ID, obj.get("model").getAsString()));
			Function<ResourceLocation, TextureAtlasSprite> textureGetter = location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
			bakedModel = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, textureGetter::apply);

			JsonArray array = obj.getAsJsonArray("textures");
			for(JsonElement texture : array)
			{
				textures.add(new ResourceLocation(ModInfo.MOD_ID, texture.getAsString()));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public IBakedModel getModel()
	{
		return bakedModel;
	}

	public ResourceLocation getCurrentTexture()
	{
		return textures.get(current);
	}

	public void nextTexture()
	{
		if(current++ == textures.size())
		{ current = 0; }
	}

	public void prevTexture()
	{
		if(current-- == 0)
		{ current = textures.size() - 1; }
	}
}