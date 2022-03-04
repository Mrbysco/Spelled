package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;

public class GlowBehavior extends BaseBehavior {
	public GlowBehavior() {
		super("glow");
	}

	@Override
	public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
		if (entity instanceof LivingEntity) {
			((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.GLOWING, 5 * 20, 0, false, false));
		}
	}
}
