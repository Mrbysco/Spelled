package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;

public class ProtectBehavior extends BaseBehavior {
    public ProtectBehavior() {
        super("protection");
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            if(livingEntity.getEffect(Effects.DAMAGE_RESISTANCE) == null) {
                livingEntity.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 200, 0, false, false));
            } else {
                int amplifier = livingEntity.getEffect(Effects.DAMAGE_RESISTANCE).getAmplifier();
                livingEntity.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 200, MathHelper.clamp(amplifier + 1, 0, 4), false, false));
            }
            livingEntity.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 200, 0, false, false));
        }
    }
}
