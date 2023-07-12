package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class FireBehavior extends BaseBehavior {
	public FireBehavior() {
		super("fire");
	}

	@Override
	public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
		Level level = spell.level();
		BlockState offState = level.getBlockState(offPos);

		if (offState.canBeReplaced()) {
			level.setBlockAndUpdate(offPos, BaseFireBlock.getState(level, offPos));
		}
	}

	@Override
	public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
		entity.setSecondsOnFire(5);
	}
}
