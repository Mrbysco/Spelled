package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class FireBehavior extends BaseBehavior {
    public FireBehavior() {
        super("fire");
    }

    @Override
    public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
        BlockState offState = spell.world.getBlockState(offPos);

        if (offState.getMaterial().isReplaceable()) {
            spell.world.setBlockState(offPos, AbstractFireBlock.getFireForPlacement(spell.world, offPos));
        }
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
        entity.setFire(5);
    }
}
