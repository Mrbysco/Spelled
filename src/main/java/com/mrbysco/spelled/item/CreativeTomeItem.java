package com.mrbysco.spelled.item;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class CreativeTomeItem extends Item {
	public CreativeTomeItem(Properties builder) {
		super(builder);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
		if (!level.isClientSide) {
			ItemStack itemstack = playerIn.getItemInHand(handIn);
			playerIn.startUsingItem(handIn);
			List<String> adjectives = KeywordRegistry.instance().getAdjectives();
			for (String adjective : adjectives) {
				SpelledAPI.unlockKeyword((ServerPlayer) playerIn, adjective);
			}
			SpelledAPI.syncCap((ServerPlayer) playerIn);
			playerIn.displayClientMessage(Component.translatable("spelled.tome.success"), true);
			return InteractionResultHolder.consume(itemstack);
		}
		return super.use(level, playerIn, handIn);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(Component.translatable("spelled.creative_tome.description").withStyle(ChatFormatting.DARK_PURPLE));
		super.appendHoverText(stack, level, tooltip, flagIn);
	}
}
