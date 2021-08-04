package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class WaterBehavior extends BaseBehavior {
    public WaterBehavior() {
        super("water");
    }

    @Override
    public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
        BlockState hitState = spell.level.getBlockState(pos);
        BlockState offState = spell.level.getBlockState(offPos);

        Block block = hitState.getBlock();
        if (block instanceof ILiquidContainer && ((ILiquidContainer)block).canPlaceLiquid(spell.level, pos, hitState, Fluids.WATER)) {
            ((ILiquidContainer) block).placeLiquid(spell.level, pos, hitState, Fluids.WATER.getSource(false));
        } else {
            if(hitState.getBlock() instanceof FlowingFluidBlock && ((FlowingFluidBlock)hitState.getBlock()).getFluid() == Fluids.LAVA) {
                Block fluidBlock = spell.level.getFluidState(pos).isSource() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
                spell.level.setBlockAndUpdate(pos, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(spell.level, pos, pos, fluidBlock.defaultBlockState()));
            } else {
                if(hitState.canBeReplaced(Fluids.WATER)) {
                    spell.level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
                } else if (offState.canBeReplaced(Fluids.WATER) ) {
                    spell.level.setBlockAndUpdate(offPos, Blocks.WATER.defaultBlockState());
                }
            }
        }
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
        entity.clearFire();
    }
}
