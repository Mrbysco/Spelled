package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class HarvestBehavior extends BaseBehavior {
	public HarvestBehavior() {
		super("harvest");
	}

	@Override
	public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
		Level level = spell.level();
		BlockState hitState = level.getBlockState(pos);
		float hardness = hitState.getDestroySpeed(level, pos);
		if (hardness > 0.0F && hitState.getBlock().getExplosionResistance() <= 6.0D) {
			level.destroyBlock(pos, true);
		}
	}

	@Override
	public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			for (EquipmentSlot slotType : EquipmentSlot.values()) {
				ItemStack stack = livingEntity.getItemBySlot(slotType);
				if (livingEntity.getRandom().nextBoolean() && !stack.isEmpty()) {
					stack.hurtAndBreak(1, livingEntity, (playerIn) -> {
						playerIn.broadcastBreakEvent(slotType);
					});
					break;
				}
			}
		}
	}
}
