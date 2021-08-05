package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class LavaBehavior extends BaseBehavior {
    public LavaBehavior() {
        super("lava");
    }

    @Override
    public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
        BlockState offState = spell.world.getBlockState(offPos);

        if (offState.isReplaceable(Fluids.LAVA)) {
            spell.world.setBlockState(offPos, Blocks.LAVA.getDefaultState());
        }
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
        entity.setFire(5);
    }
}
