package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nonnull;

public class WaterBehavior extends BaseBehavior {
	public WaterBehavior() {
		super("water");
	}

	@Override
	public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
		Level level = spell.level();
		BlockState hitState = level.getBlockState(pos);
		BlockState offState = level.getBlockState(offPos);

		Block block = hitState.getBlock();
		if (block instanceof LiquidBlockContainer && ((LiquidBlockContainer) block).canPlaceLiquid(level, pos, hitState, Fluids.WATER)) {
			((LiquidBlockContainer) block).placeLiquid(level, pos, hitState, Fluids.WATER.getSource(false));
		} else {
			if (hitState.getBlock() instanceof LiquidBlock && ((LiquidBlock) hitState.getBlock()).getFluid() == Fluids.LAVA) {
				Block fluidBlock = level.getFluidState(pos).isSource() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
				level.setBlockAndUpdate(pos, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(level, pos, pos, fluidBlock.defaultBlockState()));
			} else {
				if (hitState.canBeReplaced(Fluids.WATER)) {
					level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
				} else if (offState.canBeReplaced(Fluids.WATER)) {
					level.setBlockAndUpdate(offPos, Blocks.WATER.defaultBlockState());
				}
			}
		}
	}

	@Override
	public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
		Level level = entity.level();
		level.playSound((Player) null, entity.blockPosition(), SoundEvents.GENERIC_EXTINGUISH_FIRE, entity.getSoundSource(), 0.7F, 1.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);
		entity.clearFire();
	}
}
