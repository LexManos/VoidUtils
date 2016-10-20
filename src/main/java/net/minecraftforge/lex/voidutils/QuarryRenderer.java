package net.minecraftforge.lex.voidutils;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class QuarryRenderer extends TileEntitySpecialRenderer<TileEntityQuarry>
{
    @Override
    public void renderTileEntityAt(TileEntityQuarry te, double xIn, double yIn, double zIn, float partialTicks, int destroyStage)
    {
       super.renderTileEntityAt(te, xIn, yIn, zIn, partialTicks, destroyStage);

       EntityRenderer erender = Minecraft.getMinecraft().entityRenderer;
       EntityPlayer entityplayer = Minecraft.getMinecraft().thePlayer;
       BlockPos start = te.getStart();
       BlockPos end = te.getEnd();
       BlockPos current = te.getCurrent();
       Tessellator tes = Tessellator.getInstance();
       VertexBuffer buff = tes.getBuffer();

       Random rnd = new Random(te.getPos().toLong());

       float r = rnd.nextFloat();
       float g = rnd.nextFloat();
       float b = rnd.nextFloat();

       double driftX = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double)partialTicks;
       double driftY = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double)partialTicks;
       double driftZ = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double)partialTicks;
       double yMin = (double)(current.getY()) - driftY;
       double yMax = (double)(end.getY()+1) - driftY;
       double xMin = (double)(start.getX() < end.getX() ? start.getX() : end.getX()) - driftX;
       double zMin = (double)(start.getZ() < end.getZ() ? start.getZ() : end.getZ()) - driftZ;
       int xSize = Math.abs(start.getX() - end.getX());
       int zSize = Math.abs(start.getZ() - end.getZ());

       GlStateManager.disableTexture2D();
       GlStateManager.disableBlend();
       erender.disableLightmap();
       GlStateManager.glLineWidth(1.0F);
       buff.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

       int step = 2;
       for (int j = step; j <= xSize; j += step)
       {
           double xOff = xMin + j;
           //North Face
           buff.pos(xOff, yMin, zMin        ).color(r, g, b, 0.0F).endVertex();
           buff.pos(xOff, yMin, zMin        ).color(r, g, b, 1.0F).endVertex();
           buff.pos(xOff, yMax, zMin        ).color(r, g, b, 1.0F).endVertex();
           buff.pos(xOff, yMax, zMin        ).color(r, g, b, 0.0F).endVertex();
           //South Face
           buff.pos(xOff, yMin, zMin + 16.0D).color(r, g, b, 0.0F).endVertex();
           buff.pos(xOff, yMin, zMin + 16.0D).color(r, g, b, 1.0F).endVertex();
           buff.pos(xOff, yMax, zMin + 16.0D).color(r, g, b, 1.0F).endVertex();
           buff.pos(xOff, yMax, zMin + 16.0D).color(r, g, b, 0.0F).endVertex();
       }

       for (int j = step; j <= zSize; j += step)
       {
           double zOff = zMin + j;
           //West Face
           buff.pos(xMin,         yMin, zOff).color(r, g, b, 0.0F).endVertex();
           buff.pos(xMin,         yMin, zOff).color(r, g, b, 1.0F).endVertex();
           buff.pos(xMin,         yMax, zOff).color(r, g, b, 1.0F).endVertex();
           buff.pos(xMin,         yMax, zOff).color(r, g, b, 0.0F).endVertex();
           //East Face
           buff.pos(xMin + 16.0D, yMin, zOff).color(r, g, b, 0.0F).endVertex();
           buff.pos(xMin + 16.0D, yMin, zOff).color(r, g, b, 1.0F).endVertex();
           buff.pos(xMin + 16.0D, yMax, zOff).color(r, g, b, 1.0F).endVertex();
           buff.pos(xMin + 16.0D, yMax, zOff).color(r, g, b, 0.0F).endVertex();
       }

       //Horizontal Y rings
       for (int y = start.getY(); y <= end.getY(); y += step)
       {
           double yOff = (double)y - driftY;
           buff.pos(xMin,         yOff, zMin        ).color(r, g, b, 0.0F).endVertex();
           buff.pos(xMin,         yOff, zMin        ).color(r, g, b, 1.0F).endVertex();
           buff.pos(xMin,         yOff, zMin + 16.0D).color(r, g, b, 1.0F).endVertex();
           buff.pos(xMin + 16.0D, yOff, zMin + 16.0D).color(r, g, b, 1.0F).endVertex();
           buff.pos(xMin + 16.0D, yOff, zMin        ).color(r, g, b, 1.0F).endVertex();
           buff.pos(xMin,         yOff, zMin        ).color(r, g, b, 1.0F).endVertex();
           buff.pos(xMin,         yOff, zMin        ).color(r, g, b, 0.0F).endVertex();
       }

       tes.draw();

       GlStateManager.glLineWidth(2.0F);
       buff.begin(3, DefaultVertexFormats.POSITION_COLOR);

       //Vertical corners
       for (int x = 0; x <= 16; x += 16)
       {
           for (int z = 0; z <= 16; z += 16)
           {
               buff.pos(xMin + (double)x, yMin, zMin + (double)z).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
               buff.pos(xMin + (double)x, yMin, zMin + (double)z).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
               buff.pos(xMin + (double)x, yMax, zMin + (double)z).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
               buff.pos(xMin + (double)x, yMax, zMin + (double)z).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
           }
       }

       //Horizontal chunk corners
       for (int y = 0; y <= 256; y += 16)
       {
           if (y < start.getY() || y > end.getY())
               continue;
           double yOff = (double)y - driftY;
           buff.pos(xMin,         yOff, zMin        ).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
           buff.pos(xMin,         yOff, zMin        ).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
           buff.pos(xMin,         yOff, zMin + 16.0D).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
           buff.pos(xMin + 16.0D, yOff, zMin + 16.0D).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
           buff.pos(xMin + 16.0D, yOff, zMin        ).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
           buff.pos(xMin,         yOff, zMin        ).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
           buff.pos(xMin,         yOff, zMin        ).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
       }
       if (start.getY() % 16 != 0)
       {
           double yOff = (double)start.getY() - driftY;
           buff.pos(xMin,         yOff, zMin        ).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
           buff.pos(xMin,         yOff, zMin        ).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
           buff.pos(xMin,         yOff, zMin + 16.0D).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
           buff.pos(xMin + 16.0D, yOff, zMin + 16.0D).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
           buff.pos(xMin + 16.0D, yOff, zMin        ).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
           buff.pos(xMin,         yOff, zMin        ).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
           buff.pos(xMin,         yOff, zMin        ).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
       }
       if (end.getY() % 16 != 0)
       {
           double yOff = (double)end.getY() + 1 - driftY;
           buff.pos(xMin,         yOff, zMin        ).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
           buff.pos(xMin,         yOff, zMin        ).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
           buff.pos(xMin,         yOff, zMin + 16.0D).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
           buff.pos(xMin + 16.0D, yOff, zMin + 16.0D).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
           buff.pos(xMin + 16.0D, yOff, zMin        ).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
           buff.pos(xMin,         yOff, zMin        ).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
           buff.pos(xMin,         yOff, zMin        ).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
       }

       tes.draw();

       GlStateManager.glLineWidth(1.0F);
       erender.enableLightmap();
       GlStateManager.enableBlend();
       GlStateManager.enableTexture2D();
    }
}
