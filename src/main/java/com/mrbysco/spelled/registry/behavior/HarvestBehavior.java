package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import com.mrbysco.spelled.util.LootHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
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
		Level world = spell.level;
		BlockState hitState = world.getBlockState(pos);
		float hardness = hitState.getDestroySpeed(world, pos);
		float power = 1.0F + spell.getPower();
		if (!world.isClientSide && hardness <= power && hitState.getBlock().getExplosionResistance() <= 1200.0F) {
			if (spell.isSilky()) {
				world.getBlockState(pos).getDrops(LootHelper.silkContextBuilder((ServerLevel) world, pos, spell))
						.forEach(i -> world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), i)));
				world.destroyBlock(pos, false);
			} else {
				world.destroyBlock(pos, true);
			}
		}
	}

	@Override
	public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			for (EquipmentSlot slotType : EquipmentSlot.values()) {
				ItemStack stack = livingEntity.getItemBySlot(slotType);
				if (livingEntity.getRandom().nextBoolean() && !stack.isEmpty()) {
					int power = 1 + spell.getPower();
					stack.hurtAndBreak(power, livingEntity, (playerIn) -> {
						playerIn.broadcastBreakEvent(slotType);
					});
					break;
				}
			}
		}
	}
}
