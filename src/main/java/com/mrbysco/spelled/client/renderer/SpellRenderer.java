package com.mrbysco.spelled.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class SpellRenderer extends EntityRenderer<SpellEntity, SpellRenderState> {
	private static final Identifier PROJECTILE_TEXTURE = Reference.modLoc("textures/item/projectile.png");
	private static final Identifier BALL_TEXTURE = Reference.modLoc("textures/item/ball.png");
	private static final Identifier LAVA_TEXTURE = Reference.modLoc("textures/item/lava_ball.png");
	private static final Identifier WATER_TEXTURE = Reference.modLoc("textures/item/water_ball.png");

	private static final RenderType PROJECTILE_RENDER_TYPE = RenderTypes.entityCutout(PROJECTILE_TEXTURE);
	private static final RenderType BALL_RENDER_TYPE = RenderTypes.entityCutout(BALL_TEXTURE);
	private static final RenderType LAVA_RENDER_TYPE = RenderTypes.entityCutout(LAVA_TEXTURE);
	private static final RenderType WATER_RENDER_TYPE = RenderTypes.entityCutout(WATER_TEXTURE);
	private static int color = -1;

	public SpellRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	protected int getBlockLightLevel(SpellEntity entityIn, BlockPos partialTicks) {
		return 15;
	}

	@Override
	public SpellRenderState createRenderState() {
		return new SpellRenderState();
	}

	@Override
	public void extractRenderState(SpellEntity spell, SpellRenderState state, float partialTicks) {
		super.extractRenderState(spell, state, partialTicks);
		state.color = spell.hasColor() ? spell.getColor().getAsInt() : null;
		state.sizeMultiplier = spell.getSizeMultiplier();
		state.isWater = spell.isWater();
		state.isLava = spell.isLava();
		state.isProjectile = spell.getSpellType() == 1;
	}

	@Override
	public void submit(SpellRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		poseStack.pushPose();
		preSubmitCallback(state, poseStack);
		poseStack.scale(2.0F, 2.0F, 2.0F);
		poseStack.mulPose(camera.orientation);
		submitNodeCollector.submitCustomGeometry(poseStack, getRenderType(state), (pose, buffer) -> {
			vertex(buffer, pose, state.lightCoords, 0.0F, 0, 0, 1);
			vertex(buffer, pose, state.lightCoords, 1.0F, 0, 1, 1);
			vertex(buffer, pose, state.lightCoords, 1.0F, 1, 1, 0);
			vertex(buffer, pose, state.lightCoords, 0.0F, 1, 0, 0);
		});
		poseStack.popPose();
		super.submit(state, poseStack, submitNodeCollector, camera);
	}

	private static void vertex(VertexConsumer builder, PoseStack.Pose pose, int lightCoords, float x, int y, int u, int v) {
		builder.addVertex(pose, x - 0.5F, y - 0.25F, 0.0F)
				.setColor(-1)
				.setUv(u, v)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(lightCoords)
				.setNormal(pose, 0.0F, 1.0F, 0.0F);
	}


	protected void preSubmitCallback(SpellRenderState state, PoseStack poseStack) {
		if (color == -1)
			color = ARGB.color(255, 255, 255, 255);

		if (state.color != null) {
			int currentColor = ARGB.fromABGR(state.color.intValue());
			if (color != currentColor)
				color = currentColor;
		}

		float sizeMultiplier = state.sizeMultiplier / 2;
		if (sizeMultiplier > 8F)
			sizeMultiplier = 8F;
		poseStack.scale(1.0F, 1.0F, 1.0F);
		poseStack.translate(0.0D, (double) 0.001F, 0.0D);
		float f3 = 1.0F;
		poseStack.scale(f3 * sizeMultiplier, f3 * sizeMultiplier, f3 * sizeMultiplier);
	}

	/**
	 * Returns the correct render type.
	 */
	public RenderType getRenderType(SpellRenderState entity) {
		if (entity.isWater) {
			return WATER_RENDER_TYPE;
		}
		if (entity.isLava) {
			return LAVA_RENDER_TYPE;
		}
		if (entity.isProjectile) {
			return PROJECTILE_RENDER_TYPE;
		}
		return BALL_RENDER_TYPE;
	}
}
