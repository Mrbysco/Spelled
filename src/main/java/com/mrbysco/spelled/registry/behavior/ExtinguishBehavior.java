package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class ExtinguishBehavior extends BaseBehavior {
	public ExtinguishBehavior() {
		super("extinguish");
	}

	@Override
	public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
		Level level = spell.level();
		extinguishFires(level, pos);
		extinguishFires(level, offPos);
	}

	private void extinguishFires(Level level, BlockPos pos) {
		BlockState blockstate = level.getBlockState(pos);
		if (blockstate.is(BlockTags.FIRE)) {
			level.levelEvent((Player) null, 1009, pos, 0);
			level.removeBlock(pos, false);
		} else if (CampfireBlock.isLitCampfire(blockstate)) {
			level.levelEvent((Player) null, 1009, pos, 0);
			CampfireBlock.dowse(null, level, pos, blockstate);
			level.setBlockAndUpdate(pos, blockstate.setValue(CampfireBlock.LIT, Boolean.FALSE));
		}
	}

	@Override
	public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
		Level level = entity.level();
		level.playSound((Player) null, entity.blockPosition(), SoundEvents.GENERIC_EXTINGUISH_FIRE, entity.getSoundSource(), 0.7F, 1.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);
		entity.clearFire();
	}
}
