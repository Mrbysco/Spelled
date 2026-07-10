package com.mrbysco.spelled.item;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.keywords.IKeyword;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.client.gui.book.AdjectiveEntry;
import com.mrbysco.spelled.registry.SpelledComponents;
import com.mrbysco.spelled.util.SpellUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SpellbookItem extends Item {
	public SpellbookItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player == null) return InteractionResult.PASS;
		ItemStack stack = player.getItemInHand(hand);
		if (stack.has(SpelledComponents.SEALED)) {
			if (!level.isClientSide()) {
				ServerPlayer serverPlayer = (ServerPlayer) player;
				String message = stack.getOrDefault(SpelledComponents.SPELL, "");
				final String regExp = "^[a-zA-Z\\s]*$";
				if (!message.isEmpty() && message.matches(regExp)) {
					Component component = Component.translatable("chat.type.text", serverPlayer.getDisplayName(),
							CommonHooks.newChatWithLinks(message));

					component = SpellUtil.manualCastSpell(serverPlayer, message, component);
					if (component == null) {
						return InteractionResult.FAIL;
					} else {
						serverPlayer.level().getServer().getPlayerList().broadcastSystemMessage(component, true);
					}
				}
			}
		} else {
			if (level.isClientSide()) {
				List<AdjectiveEntry> adjectives = new ArrayList<>();
				SpelledAPI.getUnlocks(player).forEach((adjective) -> {
					if (!adjective.isEmpty()) {
						IKeyword word = KeywordRegistry.instance().getKeywordFromName(adjective);
						if (word != null) {
							adjectives.add(new AdjectiveEntry(word, word.getKeyword(), word.getDescription(), word.getSlots()));
						}
					}
				});
				if (adjectives.isEmpty()) {
					player.sendSystemMessage(Component.translatable("spelled.spell_book.insufficient"));
				} else {
					KeywordRegistry.instance().getTypes().forEach((adjective) -> {
						IKeyword word = KeywordRegistry.instance().getKeywordFromName(adjective);
						adjectives.add(new AdjectiveEntry(word, word.getKeyword(), word.getDescription(), word.getSlots()));
					});
					com.mrbysco.spelled.client.ClientHelper.openSpellBookScreen(adjectives, hand, player);
				}
			}
		}
		return super.use(level, player, hand);
	}

	@Override
	public Component getName(ItemStack stack) {
		WrittenBookContent writtenbookcontent = stack.get(DataComponents.WRITTEN_BOOK_CONTENT);
		if (writtenbookcontent != null) {
			String s = writtenbookcontent.title().raw();
			if (!StringUtil.isBlank(s)) {
				return Component.literal(s);
			}
		}

		return super.getName(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
		WrittenBookContent writtenbookcontent = stack.get(DataComponents.WRITTEN_BOOK_CONTENT);
		if (writtenbookcontent != null) {
			if (!StringUtil.isBlank(writtenbookcontent.author())) {
				builder.accept(Component.translatable("book.byAuthor", writtenbookcontent.author()).withStyle(ChatFormatting.GRAY));
			}
		}
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return stack.has(SpelledComponents.SEALED);
	}
}
