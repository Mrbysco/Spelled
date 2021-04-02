package com.mrbysco.spelled.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

import java.awt.Color;

public class SpellRenderer extends EntityRenderer<SpellEntity> {
    private static final ResourceLocation PROJECTILE_TEXTURE = new ResourceLocation(Reference.MOD_ID,"textures/item/projectile.png");
    private static final ResourceLocation BALL_TEXTURE = new ResourceLocation(Reference.MOD_ID,"textures/item/ball.png");
    private static final ResourceLocation LAVA_TEXTURE = new ResourceLocation(Reference.MOD_ID,"textures/item/lava_ball.png");
    private static final ResourceLocation WATER_TEXTURE = new ResourceLocation(Reference.MOD_ID,"textures/item/water_ball.png");
    private static final RenderType renderType = RenderType.getEntityCutoutNoCull(BALL_TEXTURE);
    private static Color color = null;

    public SpellRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    protected int getBlockLight(SpellEntity entityIn, BlockPos partialTicks) {
        return 15;
    }

    public void render(SpellEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        this.preRenderCallback(entityIn, matrixStackIn, partialTicks);
        matrixStackIn.rotate(this.renderManager.getCameraOrientation());
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
        MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
        Matrix4f matrix4f = matrixstack$entry.getMatrix();
        Matrix3f matrix3f = matrixstack$entry.getNormal();
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(renderType);
        func_229045_a_(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 0.0F, 0, 0, 1);
        func_229045_a_(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 1.0F, 0, 1, 1);
        func_229045_a_(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 1.0F, 1, 1, 0);
        func_229045_a_(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 0.0F, 1, 0, 0);
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    private static void func_229045_a_(IVertexBuilder p_229045_0_, Matrix4f p_229045_1_, Matrix3f p_229045_2_, int p_229045_3_, float p_229045_4_, int p_229045_5_, int p_229045_6_, int p_229045_7_) {
        p_229045_0_.pos(p_229045_1_, p_229045_4_ - 0.5F, (float)p_229045_5_ - 0.25F, 0.0F).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).tex((float)p_229045_6_, (float)p_229045_7_).overlay(OverlayTexture.NO_OVERLAY).lightmap(p_229045_3_).normal(p_229045_2_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    protected void preRenderCallback(SpellEntity entityIn, MatrixStack matrixStackIn, float partialTickTime) {
        if(entityIn.hasColor()) {
            if (color != new Color(entityIn.getColor().getAsInt()))
                color = new Color(entityIn.getColor().getAsInt());
        } else {
            if (color == null)
                color = new Color(255, 255, 255, 255);
        }

        float sizeMultiplier = entityIn.getSizeMultiplier() / 2;
        matrixStackIn.scale(1.0F, 1.0F, 1.0F);
        matrixStackIn.translate(0.0D, (double)0.001F, 0.0D);
        float f3 = 1.0F;
        matrixStackIn.scale(f3 * sizeMultiplier, f3 * sizeMultiplier, f3 * sizeMultiplier);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getEntityTexture(SpellEntity entity) {
        if(entity.isWater()) {
            return WATER_TEXTURE;
        }
        if(entity.isLava()) {
            return LAVA_TEXTURE;
        }
        return BALL_TEXTURE;
    }
}
