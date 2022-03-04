package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class SnowBehavior extends BaseBehavior {
	public SnowBehavior() {
		super("snow");
	}

	@Override
	public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
		BlockState hitState = spell.level.getBlockState(pos);
		BlockState offState = spell.level.getBlockState(offPos);

		if (offState.getBlock() instanceof SnowLayerBlock && offState.getValue(SnowLayerBlock.LAYERS) < 8) {
			int layers = offState.getValue(SnowLayerBlock.LAYERS);
			spell.level.setBlockAndUpdate(offPos, offState.getBlock().defaultBlockState().setValue(SnowLayerBlock.LAYERS, layers + 1));
		} else if (hitState.getBlock() instanceof SnowLayerBlock && hitState.getValue(SnowLayerBlock.LAYERS) < 8) {
			int layers = hitState.getValue(SnowLayerBlock.LAYERS);
			spell.level.setBlockAndUpdate(pos, hitState.getBlock().defaultBlockState().setValue(SnowLayerBlock.LAYERS, layers + 1));
		} else {
			BlockState snowState = Blocks.SNOW.defaultBlockState();
			if (offState.getMaterial().isReplaceable() && snowState.canSurvive(spell.level, offPos))
				spell.level.setBlockAndUpdate(offPos, snowState);
		}
	}

	@Override
	public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
		if (entity instanceof LivingEntity) {
			((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2 * 20));
		}
	}
}
