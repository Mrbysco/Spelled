package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class ExplodeBehavior extends BaseBehavior {
    public ExplodeBehavior() {
        super("explode");
    }

    @Override
    public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
        spell.explode();
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
        spell.explode();
    }

    @Override
    public boolean appliedMultiple() {
        return false;
    }
}
