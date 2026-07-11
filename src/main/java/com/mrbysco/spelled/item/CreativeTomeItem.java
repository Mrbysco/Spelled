package com.mrbysco.spelled.item;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

public class CreativeTomeItem extends Item {
	public CreativeTomeItem(Properties builder) {
		super(builder.rarity(Rarity.EPIC));
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player == null) return InteractionResult.PASS;
		if (!level.isClientSide()) {
			player.startUsingItem(hand);
			List<String> adjectives = KeywordRegistry.instance().getAdjectives();
			for (String adjective : adjectives) {
				SpelledAPI.unlockKeyword(player, adjective);
			}
			SpelledAPI.syncCap((ServerPlayer) player);
			player.sendOverlayMessage(Component.translatable("spelled.tome.success"));
			return InteractionResult.SUCCESS;
		}
		return super.use(level, player, hand);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
		builder.accept(Component.translatable("spelled.creative_tome.description").withStyle(ChatFormatting.DARK_PURPLE));
	}
}
