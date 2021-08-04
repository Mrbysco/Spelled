package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class HarvestBehavior extends BaseBehavior {
    public HarvestBehavior() {
        super("harvest");
    }

    @Override
    public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
        Level world = spell.level;
        BlockState hitState = world.getBlockState(pos);
        float hardness = hitState.getDestroySpeed(world, pos);
        if(hardness > 0.0F && hitState.getHarvestLevel() <= 2) {
            spell.level.destroyBlock(pos, true);
        }
    }
}
