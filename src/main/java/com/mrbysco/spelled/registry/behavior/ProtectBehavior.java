package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;

public class ProtectBehavior extends BaseBehavior {
    public ProtectBehavior() {
        super("protection");
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            if(livingEntity.getEffect(MobEffects.DAMAGE_RESISTANCE) == null) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0, false, false));
            } else {
                int amplifier = livingEntity.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier();
                livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, Mth.clamp(amplifier + 1, 0, 4), false, false));
            }
            livingEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0, false, false));
        }
    }
}
