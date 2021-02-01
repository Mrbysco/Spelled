package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class WaterBehavior extends BaseBehavior {
    public WaterBehavior() {
        super("water");
    }

    @Override
    public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
        BlockState hitState = spell.world.getBlockState(pos);
        BlockState offState = spell.world.getBlockState(offPos);

        Block block = hitState.getBlock();
        if (block instanceof ILiquidContainer && ((ILiquidContainer)block).canContainFluid(spell.world, pos, hitState, Fluids.WATER)) {
            ((ILiquidContainer) block).receiveFluid(spell.world, pos, hitState, Fluids.WATER.getStillFluidState(false));
        } else {
            if(hitState.getBlock() instanceof FlowingFluidBlock && ((FlowingFluidBlock)hitState.getBlock()).getFluid() == Fluids.LAVA) {
                Block fluidBlock = spell.world.getFluidState(pos).isSource() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
                spell.world.setBlockState(pos, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(spell.world, pos, pos, fluidBlock.getDefaultState()));
            } else {
                if(hitState.isReplaceable(Fluids.WATER)) {
                    spell.world.setBlockState(pos, Blocks.WATER.getDefaultState());
                } else if (offState.isReplaceable(Fluids.WATER) ) {
                    spell.world.setBlockState(offPos, Blocks.WATER.getDefaultState());
                }
            }
        }
    }
}
