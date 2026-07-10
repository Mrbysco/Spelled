package com.mrbysco.spelled.item;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.capability.ISpellData;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.config.SpelledConfig;
import com.mrbysco.spelled.registry.SpelledComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.function.Consumer;

public class TomeItem extends Item {
	public TomeItem(Properties builder) {
		super(builder);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player == null) return InteractionResult.PASS;
		if (!level.isClientSide()) {
			ItemStack itemstack = player.getItemInHand(hand);

			if (itemstack.has(SpelledComponents.UNLOCK)) {
				Optional<ISpellData> cap = SpelledAPI.getSpellDataCap(player);
				ISpellData data = cap.orElseGet(null);
				if (cap.isPresent()) {
					String word = itemstack.getOrDefault(SpelledComponents.UNLOCK, "");
					if (!data.knowsKeyword(word)) {
						player.startUsingItem(hand);
						SpelledAPI.unlockKeyword((ServerPlayer) player, word);
						SpelledAPI.syncCap((ServerPlayer) player);
						player.sendOverlayMessage(Component.translatable("spelled.tome.success"));
						return InteractionResult.CONSUME;
					} else {
						player.sendOverlayMessage(Component.translatable("spelled.tome.fail"));
						return InteractionResult.FAIL;
					}
				}
			} else {
				String adjective = KeywordRegistry.instance().getRandomAdjective();
				if (!adjective.isEmpty()) {
					itemstack.set(SpelledComponents.UNLOCK, KeywordRegistry.instance().getRandomAdjective());
				}
			}
		}

		return super.use(level, player, hand);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
		if (!SpelledConfig.COMMON.hideKnowledgeTomeInfo.get()) {
			if (stack.has(SpelledComponents.UNLOCK)) {
				String word = stack.getOrDefault(SpelledComponents.UNLOCK, "");
				builder.accept(Component.translatable("spelled.tome.description", Component.literal(word)
						.withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.YELLOW));
			} else {
				builder.accept(Component.translatable("spelled.tome.description.invalid").withStyle(ChatFormatting.RED));
			}
		}
		super.appendHoverText(stack, context, display, builder, tooltipFlag);
	}
}
