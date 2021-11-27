package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class SnowBehavior extends BaseBehavior {
    public SnowBehavior() {
        super("snow");
    }

    @Override
    public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
        BlockState hitState = spell.level.getBlockState(pos);
        BlockState offState = spell.level.getBlockState(offPos);

        if(offState.getBlock() instanceof SnowBlock && offState.getValue(SnowBlock.LAYERS) < 8) {
            int layers = offState.getValue(SnowBlock.LAYERS);
            spell.level.setBlockAndUpdate(offPos, offState.getBlock().defaultBlockState().setValue(SnowBlock.LAYERS, layers + 1));
        } else if(hitState.getBlock() instanceof SnowBlock && hitState.getValue(SnowBlock.LAYERS) < 8) {
            int layers = hitState.getValue(SnowBlock.LAYERS);
            spell.level.setBlockAndUpdate(pos, hitState.getBlock().defaultBlockState().setValue(SnowBlock.LAYERS, layers + 1));
        } else {
            BlockState snowState = Blocks.SNOW.defaultBlockState();
            if (offState.getMaterial().isReplaceable() && snowState.canSurvive(spell.level, offPos))
                spell.level.setBlockAndUpdate(offPos, snowState);
        }
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
        if(entity instanceof LivingEntity) {
            ((LivingEntity)entity).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 2*20));
        }
    }
}
