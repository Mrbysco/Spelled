package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class HarvestBehavior extends BaseBehavior {
    public HarvestBehavior() {
        super("harvest");
    }

    @Override
    public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
        BlockState hitState = spell.world.getBlockState(pos);
        if(hitState.getHarvestLevel() <= 2) {
            spell.world.destroyBlock(pos, true);
        }
    }
}
