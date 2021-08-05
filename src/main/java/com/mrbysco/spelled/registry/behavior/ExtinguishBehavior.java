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
        World world = spell.world;
        extinguishFires(world, pos);
        extinguishFires(world, offPos);
    }

    private void extinguishFires(World world, BlockPos pos) {
        BlockState blockstate = world.getBlockState(pos);
        if (blockstate.isIn(BlockTags.FIRE)) {
            world.playEvent((PlayerEntity)null, 1009, pos, 0);
            world.removeBlock(pos, false);
        } else if (CampfireBlock.isLit(blockstate)) {
            world.playEvent((PlayerEntity)null, 1009, pos, 0);
            CampfireBlock.extinguish(world, pos, blockstate);
            world.setBlockState(pos, blockstate.with(CampfireBlock.LIT, Boolean.valueOf(false)));
        }
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
        World world = entity.world;
        world.playSound((PlayerEntity) null, entity.getPosition(), SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, entity.getSoundCategory(),0.7F,1.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.4F);
        entity.extinguish();
    }
}
