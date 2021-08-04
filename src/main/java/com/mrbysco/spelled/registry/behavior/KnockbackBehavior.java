package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class KnockbackBehavior extends BaseBehavior {
    public KnockbackBehavior() {
        super("knockback");
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
        Vec3 vector3d = spell.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double)1 * 0.6D);
        if (vector3d.lengthSqr() > 0.0D)
            entity.push(vector3d.x, 0.1D, vector3d.z);
    }
}
