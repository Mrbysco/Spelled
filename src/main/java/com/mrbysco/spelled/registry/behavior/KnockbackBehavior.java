package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class KnockbackBehavior extends BaseBehavior {
	public KnockbackBehavior() {
		super("knockback");
	}

	@Override
	public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
		float rotationYaw = spell.getYRot();
		if (spell.getDeltaMovement() == Vec3.ZERO) {
			rotationYaw = entity.getYRot();
		}
		if (entity instanceof LivingEntity) {
			((LivingEntity) entity).knockback(0.5F, (double) Mth.sin(rotationYaw * ((float) Math.PI / 180F)), (double) (-Mth.cos(rotationYaw * ((float) Math.PI / 180F))));
		} else {
			entity.push((double) (-Mth.sin(rotationYaw * ((float) Math.PI / 180F)) * (float) 1 * 0.5F), 0.1D, (double) (Mth.cos(rotationYaw * ((float) Math.PI / 180F)) * (float) 1 * 0.5F));
		}
		entity.hurtMarked = true;
	}
}
