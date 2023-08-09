package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class MatureBehavior extends BaseBehavior {
	public MatureBehavior() {
		super("mature");
	}

	@Override
	public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
		Level world = spell.level;
		BlockPos abovePos = pos.above();
		BlockState aboveState = world.getBlockState(abovePos);
		int bonemealCount = 1 + spell.getPower();
		if (!world.isClientSide && aboveState.getBlock() instanceof BonemealableBlock bonemealableBlock) {
			for (int i = 0; i < bonemealCount; i++) {
				bonemealableBlock.performBonemeal((ServerLevel) world, world.random, abovePos, aboveState);
			}
		}
	}

	@Override
	public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
		if (entity instanceof AgeableMob ageableMob && ageableMob.isBaby()) {
			int age = ageableMob.getAge();
			ageableMob.ageUp(ageableMob.getSpeedUpSecondsWhenFeeding(-age), true);
		}
	}
}
