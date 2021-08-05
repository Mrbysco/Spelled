package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;

public class KnockbackBehavior extends BaseBehavior {
    public KnockbackBehavior() {
        super("knockback");
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
        float rotationYaw = spell.rotationYaw;
        if (spell.getMotion() == Vector3d.ZERO) {
            rotationYaw = entity.rotationYaw;
        }
        if (entity instanceof LivingEntity) {
            ((LivingEntity)entity).applyKnockback((float)1 * 0.5F, (double)MathHelper.sin(rotationYaw * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(rotationYaw * ((float)Math.PI / 180F))));
        } else {
            entity.addVelocity((double)(-MathHelper.sin(rotationYaw * ((float)Math.PI / 180F)) * (float)1 * 0.5F), 0.1D, (double)(MathHelper.cos(rotationYaw * ((float)Math.PI / 180F)) * (float)1 * 0.5F));
        }
        entity.velocityChanged = true;
    }
}
