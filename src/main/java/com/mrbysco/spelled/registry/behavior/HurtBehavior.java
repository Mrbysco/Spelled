package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;

public class HurtBehavior extends BaseBehavior {
    public HurtBehavior() {
        super("hurt");
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
        if(entity instanceof LivingEntity) {
            entity.hurt(Reference.causeMagicDamage(spell), 1);
        }
    }
}
