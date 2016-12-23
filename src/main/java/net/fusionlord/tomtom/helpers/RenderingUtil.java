package net.fusionlord.tomtom.helpers;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderingUtil
{
	public static void renderWithIcon(OBJModel model, TextureAtlasSprite texture, VertexBuffer buffer)
	{
		for(OBJModel.Group go : model.getMatLib().getGroups().values())
		{
			for(OBJModel.Face f : go.getFaces())
			{
				for(int i = 0; i < f.getVertices().length; i++)
				{
					OBJModel.Vertex v = f.getVertices()[i];
					OBJModel.TextureCoordinate t = v.getTextureCoordinate();
					buffer.pos(
							v.getPos3().x,
							v.getPos().y,
							v.getPos3().z
					).tex(
							t.u,
							t.v
					).normal(
							f.getNormal().x,
							f.getNormal().y,
							f.getNormal().z
					).endVertex();
				}
			}
		}
		Tessellator.getInstance().draw();
	}
}