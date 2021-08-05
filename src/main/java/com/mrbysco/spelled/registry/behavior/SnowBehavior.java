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
        BlockState hitState = spell.world.getBlockState(pos);
        BlockState offState = spell.world.getBlockState(offPos);

        if(offState.getBlock() instanceof SnowBlock && offState.get(SnowBlock.LAYERS) < 8) {
            int layers = offState.get(SnowBlock.LAYERS);
            spell.world.setBlockState(offPos, offState.getBlock().getDefaultState().with(SnowBlock.LAYERS, layers + 1));
        } else if(hitState.getBlock() instanceof SnowBlock && hitState.get(SnowBlock.LAYERS) < 8) {
            int layers = hitState.get(SnowBlock.LAYERS);
            spell.world.setBlockState(pos, hitState.getBlock().getDefaultState().with(SnowBlock.LAYERS, layers + 1));
        } else {
            BlockState snowState = Blocks.SNOW.getDefaultState();
            if (offState.getMaterial().isReplaceable() && snowState.isValidPosition(spell.world, offPos))
                spell.world.setBlockState(offPos, snowState);
        }
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
        if(entity instanceof LivingEntity) {
            ((LivingEntity)entity).addPotionEffect(new EffectInstance(Effects.SLOWNESS, 1*20));
        }
    }
}
