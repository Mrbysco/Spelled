package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class HarvestBehavior extends BaseBehavior {
    public HarvestBehavior() {
        super("harvest");
    }

    @Override
    public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
        World world = spell.level;
        BlockState hitState = world.getBlockState(pos);
        float hardness = hitState.getDestroySpeed(world, pos);
        if(hardness > 0.0F && hitState.getHarvestLevel() <= 2) {
            spell.level.destroyBlock(pos, true);
        }
    }
}
