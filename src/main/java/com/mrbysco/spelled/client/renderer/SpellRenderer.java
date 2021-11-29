package com.mrbysco.spelled.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.awt.Color;

public class SpellRenderer extends EntityRenderer<SpellEntity> {
    private static final ResourceLocation PROJECTILE_TEXTURE = new ResourceLocation(Reference.MOD_ID,"textures/item/projectile.png");
    private static final ResourceLocation BALL_TEXTURE = new ResourceLocation(Reference.MOD_ID,"textures/item/ball.png");
    private static final ResourceLocation LAVA_TEXTURE = new ResourceLocation(Reference.MOD_ID,"textures/item/lava_ball.png");
    private static final ResourceLocation WATER_TEXTURE = new ResourceLocation(Reference.MOD_ID,"textures/item/water_ball.png");
    private static final RenderType renderType = RenderType.entityCutoutNoCull(BALL_TEXTURE);
    private static Color color = null;

    public SpellRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    protected int getBlockLightLevel(SpellEntity entityIn, BlockPos partialTicks) {
        return 15;
    }

    public void render(SpellEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        this.preRenderCallback(entityIn, poseStack, partialTicks);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        PoseStack.Pose last = poseStack.last();
        Matrix4f pose = last.pose();
        Matrix3f normal = last.normal();
        VertexConsumer buffer = bufferIn.getBuffer(renderType);
        vertex(buffer, pose, normal, packedLightIn, 0.0F, 0, 0, 1);
        vertex(buffer, pose, normal, packedLightIn, 1.0F, 0, 1, 1);
        vertex(buffer, pose, normal, packedLightIn, 1.0F, 1, 1, 0);
        vertex(buffer, pose, normal, packedLightIn, 0.0F, 1, 0, 0);
        poseStack.popPose();
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
    }

    private static void vertex(VertexConsumer vertexBuilder, Matrix4f pose, Matrix3f normal, int packedLightIn, float p_229045_4_, int p_229045_5_, int p_229045_6_, int p_229045_7_) {
        vertexBuilder.vertex(pose, p_229045_4_ - 0.5F, (float)p_229045_5_ - 0.25F, 0.0F).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv((float)p_229045_6_, (float)p_229045_7_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
    }

    protected void preRenderCallback(SpellEntity entityIn, PoseStack poseStack, float partialTickTime) {
        if (color == null)
            color = new Color(255, 255, 255, 255);

        if(entityIn.hasColor()) {
            if (!color.equals(new Color(entityIn.getColor().getAsInt())))
                color = new Color(entityIn.getColor().getAsInt());
        }

        float sizeMultiplier = entityIn.getSizeMultiplier() / 2;
        poseStack.scale(1.0F, 1.0F, 1.0F);
        poseStack.translate(0.0D, (double)0.001F, 0.0D);
        float f3 = 1.0F;
        poseStack.scale(f3 * sizeMultiplier, f3 * sizeMultiplier, f3 * sizeMultiplier);
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
        if(entity.getSpellType() == 1) {
            return PROJECTILE_TEXTURE;
        }
        return BALL_TEXTURE;
    }
}
