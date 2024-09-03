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
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class TomeItem extends Item {
	public TomeItem(Properties builder) {
		super(builder);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
		if (!level.isClientSide) {
			ItemStack itemstack = playerIn.getItemInHand(handIn);

			if (itemstack.has(SpelledComponents.UNLOCK)) {
				Optional<ISpellData> cap = SpelledAPI.getSpellDataCap(playerIn);
				ISpellData data = cap.orElseGet(null);
				if (cap.isPresent()) {
					String word = itemstack.getOrDefault(SpelledComponents.UNLOCK, "");
					if (!data.knowsKeyword(word)) {
						playerIn.startUsingItem(handIn);
						SpelledAPI.unlockKeyword((ServerPlayer) playerIn, word);
						SpelledAPI.syncCap((ServerPlayer) playerIn);
						playerIn.displayClientMessage(Component.translatable("spelled.tome.success"), true);
						return InteractionResultHolder.consume(itemstack);
					} else {
						playerIn.displayClientMessage(Component.translatable("spelled.tome.fail"), true);
						return InteractionResultHolder.fail(itemstack);
					}
				}
			} else {
				String adjective = KeywordRegistry.instance().getRandomAdjective();
				if (!adjective.isEmpty()) {
					itemstack.set(SpelledComponents.UNLOCK, KeywordRegistry.instance().getRandomAdjective());
				}
			}
		}
		return super.use(level, playerIn, handIn);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
		if (!SpelledConfig.COMMON.hideKnowledgeTomeInfo.get()) {
			if (stack.has(SpelledComponents.UNLOCK)) {
				String word = stack.getOrDefault(SpelledComponents.UNLOCK, "");
				tooltip.add(Component.translatable("spelled.tome.description", Component.literal(word)
						.withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.YELLOW));
			} else {
				tooltip.add(Component.translatable("spelled.tome.description.invalid").withStyle(ChatFormatting.RED));
			}
		}
		super.appendHoverText(stack, context, tooltip, tooltipFlag);
	}
}
