package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nonnull;

public class LavaBehavior extends BaseBehavior {
	public LavaBehavior() {
		super("lava");
	}

	@Override
	public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
		BlockState offState = spell.level.getBlockState(offPos);

		if (offState.canBeReplaced(Fluids.LAVA)) {
			spell.level.setBlockAndUpdate(offPos, Blocks.LAVA.defaultBlockState());
		}
	}

	@Override
	public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
		entity.setSecondsOnFire(5);
	}
}
