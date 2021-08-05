package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;

public class KnockbackBehavior extends BaseBehavior {
    public KnockbackBehavior() {
        super("knockback");
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
        Vector3d vector3d = spell.getMotion().mul(1.0D, 0.0D, 1.0D).normalize().scale((double)1 * 0.6D);
        if (vector3d.lengthSquared() > 0.0D)
            entity.addVelocity(vector3d.x, 0.1D, vector3d.z);
    }
}
