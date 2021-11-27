package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ExtinguishBehavior extends BaseBehavior {
    public ExtinguishBehavior() {
        super("extinguish");
    }

    @Override
    public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
        World world = spell.level;
        extinguishFires(world, pos);
        extinguishFires(world, offPos);
    }

    private void extinguishFires(World world, BlockPos pos) {
        BlockState blockstate = world.getBlockState(pos);
        if (blockstate.is(BlockTags.FIRE)) {
            world.levelEvent((PlayerEntity)null, 1009, pos, 0);
            world.removeBlock(pos, false);
        } else if (CampfireBlock.isLitCampfire(blockstate)) {
            world.levelEvent((PlayerEntity)null, 1009, pos, 0);
            CampfireBlock.dowse(world, pos, blockstate);
            world.setBlockAndUpdate(pos, blockstate.setValue(CampfireBlock.LIT, Boolean.valueOf(false)));
        }
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
        World world = entity.level;
        world.playSound((PlayerEntity) null, entity.blockPosition(), SoundEvents.GENERIC_EXTINGUISH_FIRE, entity.getSoundSource(),0.7F,1.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
        entity.clearFire();
    }
}
