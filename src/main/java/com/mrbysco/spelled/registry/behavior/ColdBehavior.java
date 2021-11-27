package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.IceBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class ColdBehavior extends BaseBehavior {
    public ColdBehavior() {
        super("cold");
    }

    @Override
    public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
        BlockState hitState = spell.level.getBlockState(pos);
        if(hitState.getBlock() instanceof FlowingFluidBlock && ((FlowingFluidBlock)hitState.getBlock()).getFluid() == Fluids.WATER)
            spell.level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
        if(hitState.getBlock() instanceof IceBlock)
            spell.level.setBlockAndUpdate(pos, Blocks.PACKED_ICE.defaultBlockState());
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
        if(entity instanceof LivingEntity) {
            ((LivingEntity)entity).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 4*20));
        }
    }
}
