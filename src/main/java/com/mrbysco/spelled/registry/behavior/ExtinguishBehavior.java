package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class ExtinguishBehavior extends BaseBehavior {
    public ExtinguishBehavior() {
        super("extinguish");
    }

    @Override
    public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
        Level world = spell.level;
        extinguishFires(world, pos);
        extinguishFires(world, offPos);
    }

    private void extinguishFires(Level world, BlockPos pos) {
        BlockState blockstate = world.getBlockState(pos);
        if (blockstate.is(BlockTags.FIRE)) {
            world.levelEvent((Player)null, 1009, pos, 0);
            world.removeBlock(pos, false);
        } else if (CampfireBlock.isLitCampfire(blockstate)) {
            world.levelEvent((Player)null, 1009, pos, 0);
            CampfireBlock.dowse(null, world, pos, blockstate);
            world.setBlockAndUpdate(pos, blockstate.setValue(CampfireBlock.LIT, Boolean.FALSE));
        }
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
        Level world = entity.level;
        world.playSound((Player) null, entity.blockPosition(), SoundEvents.GENERIC_EXTINGUISH_FIRE, entity.getSoundSource(),0.7F,1.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
        entity.clearFire();
    }
}
