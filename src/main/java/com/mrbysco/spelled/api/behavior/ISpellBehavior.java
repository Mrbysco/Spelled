package com.mrbysco.spelled.api.behavior;

import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nonnull;

public interface ISpellBehavior {

	void onEntityHit(@Nonnull SpellEntity spell, Entity entity);

	void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos);

	boolean appliedMultiple();

	/*
	 * @return behavior
	 */
	String getName();
}
