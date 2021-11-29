package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nonnull;

public class ColdBehavior extends BaseBehavior {
    public ColdBehavior() {
        super("cold");
    }

    @Override
    public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
        BlockState hitState = spell.level.getBlockState(pos);
        if(hitState.getBlock() instanceof LiquidBlock && ((LiquidBlock)hitState.getBlock()).getFluid() == Fluids.WATER)
            spell.level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
        if(hitState.getBlock() instanceof IceBlock)
            spell.level.setBlockAndUpdate(pos, Blocks.PACKED_ICE.defaultBlockState());
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
        if(entity instanceof LivingEntity) {
            ((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 4*20));
        }
    }
}
