package net.fusionlord.tomtom.rendering;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fusionlord.tomtom.events.TomTomEvents;
import net.fusionlord.tomtom.helpers.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by FusionLord on 3/20/2016.
 */
@SideOnly(Side.CLIENT)
public class Arrow
{
	private IBakedModel bakedModel;
	private OBJModel model;
	private List<ResourceLocation> textures = new ArrayList<>();
	private int current = 0;

    public Arrow(String jsonFile)
	{
		JsonParser parser = new JsonParser();
		try
		{
			JsonObject obj = (JsonObject) parser.parse(new InputStreamReader(Arrow.class.getClassLoader().getResourceAsStream(jsonFile)));

            boolean useUniTextures = false;
            if(obj.has("useUniversalTextures"))
			{
				useUniTextures = obj.get("useUniversalTextures").getAsBoolean();
			}
			model = (OBJModel) OBJLoader.INSTANCE.loadModel(new ResourceLocation(ModInfo.MOD_ID, "models/" + obj.get("model").getAsString()));
			if(obj.has("textureFolder"))
			{
				textures.addAll(TomTomEvents.getFilesInJarDir("assets/tomtom/textures/" + obj.get("textureFolder").getAsString(), ".png").stream().map(texture -> new ResourceLocation(ModInfo.MOD_ID, obj.get("textureFolder").getAsString().replace("/tomtom/textures/", "") + texture.replace(".png", ""))).collect(Collectors.toList()));
			}
			if(textures.isEmpty() || textures.size() == 0)
			{ useUniTextures = true; }
            if (useUniTextures)
            {
                textures.addAll(TomTomEvents.getUniversalTextures());
            }
			bake(model);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void bake(OBJModel model)
	{
		Function<ResourceLocation, TextureAtlasSprite> textureGetter = location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(textures.get(current).toString());
        bakedModel = model.bake(model.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter::apply);
	}

	public IBakedModel getModel()
	{
		return bakedModel;
	}

	public void nextTexture()
	{
        if (++current > textures.size() - 1) current = 0;
		bake(model);
	}

	public void prevTexture()
	{
		if (--current < 0) current = textures.size() - 1;
        bake(model);
	}

	public void stitchTextures(TextureStitchEvent event)
	{
		textures.forEach(event.getMap()::registerSprite);
	}

	public void render()
    {
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(getModel(), 1f, 0f, 0f, 0f);
        GlStateManager.popMatrix();
    }
}
