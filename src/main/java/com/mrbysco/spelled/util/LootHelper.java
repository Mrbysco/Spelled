package com.mrbysco.spelled.util;

import com.mrbysco.spelled.entity.SpellEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class LootHelper {
	public static ItemStack getDummyTool() {
		return new ItemStack(Items.NETHERITE_PICKAXE);
	}

	public static LootParams.Builder silkContextBuilder(ServerLevel level, BlockPos pos, SpellEntity spell) {
		ItemStack dummy = getDummyTool();
		dummy.enchant(Enchantments.SILK_TOUCH, 1);

		return new LootParams.Builder(level)
				.withParameter(LootContextParams.ORIGIN, new Vec3(pos.getX(), pos.getY(), pos.getZ()))
				.withOptionalParameter(LootContextParams.THIS_ENTITY, spell.getOwner())
				.withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos))
				.withParameter(LootContextParams.TOOL, dummy);
	}
}
