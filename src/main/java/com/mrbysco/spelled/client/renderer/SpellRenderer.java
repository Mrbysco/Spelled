package com.mrbysco.spelled.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import java.awt.Color;

public class SpellRenderer extends EntityRenderer<SpellEntity> {
    private static final ResourceLocation PROJECTILE_TEXTURE = new ResourceLocation(Reference.MOD_ID,"textures/item/projectile.png");
    private static final ResourceLocation BALL_TEXTURE = new ResourceLocation(Reference.MOD_ID,"textures/item/ball.png");
    private static final ResourceLocation LAVA_TEXTURE = new ResourceLocation(Reference.MOD_ID,"textures/item/lava_ball.png");
    private static final ResourceLocation WATER_TEXTURE = new ResourceLocation(Reference.MOD_ID,"textures/item/water_ball.png");
    private static final RenderType renderType = RenderType.entityCutoutNoCull(BALL_TEXTURE);
    private static Color color = null;

    public SpellRenderer(EntityRenderDispatcher renderManagerIn) {
        super(renderManagerIn);
    }

    protected int getBlockLightLevel(SpellEntity entityIn, BlockPos partialTicks) {
        return 15;
    }

    public void render(SpellEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        this.preRenderCallback(entityIn, matrixStackIn, partialTicks);
        matrixStackIn.mulPose(this.entityRenderDispatcher.cameraOrientation());
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        PoseStack.Pose matrixstack$entry = matrixStackIn.last();
        Matrix4f matrix4f = matrixstack$entry.pose();
        Matrix3f matrix3f = matrixstack$entry.normal();
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(renderType);
        vertex(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 0.0F, 0, 0, 1);
        vertex(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 1.0F, 0, 1, 1);
        vertex(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 1.0F, 1, 1, 0);
        vertex(ivertexbuilder, matrix4f, matrix3f, packedLightIn, 0.0F, 1, 0, 0);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    private static void vertex(VertexConsumer p_229045_0_, Matrix4f p_229045_1_, Matrix3f p_229045_2_, int p_229045_3_, float p_229045_4_, int p_229045_5_, int p_229045_6_, int p_229045_7_) {
        p_229045_0_.vertex(p_229045_1_, p_229045_4_ - 0.5F, (float)p_229045_5_ - 0.25F, 0.0F).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv((float)p_229045_6_, (float)p_229045_7_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_229045_3_).normal(p_229045_2_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    protected void preRenderCallback(SpellEntity entityIn, PoseStack matrixStackIn, float partialTickTime) {
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
    public ResourceLocation getTextureLocation(SpellEntity entity) {
        if(entity.isWater()) {
            return WATER_TEXTURE;
        }
        if(entity.isLava()) {
            return LAVA_TEXTURE;
        }
        return BALL_TEXTURE;
    }
}
