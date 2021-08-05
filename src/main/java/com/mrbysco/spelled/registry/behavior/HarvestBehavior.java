package com.mrbysco.spelled.registry.behavior;

import com.mrbysco.spelled.api.behavior.BaseBehavior;
import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class HarvestBehavior extends BaseBehavior {
    public HarvestBehavior() {
        super("harvest");
    }

    @Override
    public void onBlockHit(@Nonnull SpellEntity spell, BlockPos pos, BlockPos offPos) {
        World world = spell.world;
        BlockState hitState = world.getBlockState(pos);
        float hardness = hitState.getBlockHardness(world, pos);
        if(hardness > 0.0F && hitState.getHarvestLevel() <= 2) {
            spell.world.destroyBlock(pos, true);
        }
    }

    @Override
    public void onEntityHit(@Nonnull SpellEntity spell, Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            for(EquipmentSlotType slotType : EquipmentSlotType.values()) {
                ItemStack stack = livingEntity.getItemStackFromSlot(slotType);
                if(livingEntity.getRNG().nextBoolean() && !stack.isEmpty()) {
                    stack.damageItem(1, livingEntity, (playerIn) -> {
                        playerIn.sendBreakAnimation(slotType);
                    });
                    break;
                }
            }
        }
    }
}
