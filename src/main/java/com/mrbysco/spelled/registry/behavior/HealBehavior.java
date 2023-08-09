package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;

public class HealBehavior extends BaseBehavior {
	public HealBehavior() {
		super("healing");
	}

	@Override
	public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
		if (entity instanceof LivingEntity) {
			float healAmount = 1.0F + spell.getPower();
			((LivingEntity) entity).heal(healAmount);
		}
	}
}
