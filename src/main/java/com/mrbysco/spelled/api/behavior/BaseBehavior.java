package com.mrbysco.spelled.api.behavior;

import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

import javax.annotation.Nonnull;

public class BaseBehavior implements ISpellBehavior {
    private final String name;

    public BaseBehavior(String name) {
        this.name = name;
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {

    }

    @Override
    public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {

    }

    @Override
    public String getName() {
        return this.name;
    }
}
