package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import javax.annotation.Nonnull;

public class InkBehavior extends BaseBehavior {
    public InkBehavior() {
        super("ink");
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
        if(entity instanceof LivingEntity) {
            ((LivingEntity)entity).addPotionEffect(new EffectInstance(Effects.BLINDNESS, 5*20));
        }
    }
}
