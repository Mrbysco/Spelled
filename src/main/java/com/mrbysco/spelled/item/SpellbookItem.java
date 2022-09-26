package com.mrbysco.spelled.item;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.keywords.IKeyword;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.client.gui.book.AdjectiveEntry;
import com.mrbysco.spelled.util.SpellUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpellbookItem extends Item {
	public SpellbookItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (stack.hasTag() && stack.getTag().getBoolean("sealed")) {
			if (!world.isClientSide) {
				ServerPlayer serverPlayer = (ServerPlayer) player;
				String message = stack.getTag().getString("spell");
				final String regExp = "^[a-zA-Z\\s]*$";
				if (!message.isEmpty() && message.matches(regExp)) {
					Component component = new TranslatableComponent("chat.type.text", serverPlayer.getDisplayName(),
							net.minecraftforge.common.ForgeHooks.newChatWithLinks(message));
					component = SpellUtil.manualCastSpell(serverPlayer, message, component);
					if (component == null) {
						return InteractionResultHolder.fail(stack);
					} else {
						serverPlayer.getServer().getPlayerList().broadcastMessage(component, ChatType.CHAT, serverPlayer.getUUID());
					}
				}
			}
		} else {
			if (world.isClientSide) {
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
					player.sendMessage(new TranslatableComponent("spelled.spell_book.insufficient"), Util.NIL_UUID);
				} else {
					KeywordRegistry.instance().getTypes().forEach((adjective) -> {
						IKeyword word = KeywordRegistry.instance().getKeywordFromName(adjective);
						adjectives.add(new AdjectiveEntry(word, word.getKeyword(), word.getDescription(), word.getSlots()));
					});
					com.mrbysco.spelled.client.ClientHelper.openSpellBookScreen(adjectives, hand, player);
				}
			}
		}
		return super.use(world, player, hand);
	}

	public static boolean makeSureTagIsValid(Player player, @Nullable CompoundTag nbt) {
		if (!makeSureSpellIsValid(player, nbt)) {
			return false;
		} else if (!nbt.contains("title", 8)) {
			return false;
		} else {
			String s = nbt.getString("title");
			return s.length() <= 32 && nbt.contains("author", 8);
		}
	}

	public static boolean makeSureSpellIsValid(Player player, @Nullable CompoundTag nbt) {
		if (nbt != null && nbt.contains("spell")) {
			String currentSpell = nbt.getString("spell");
			String[] words = currentSpell.split(" ");
			List<String> wordList = Arrays.asList(words);
			wordList = wordList.subList(0, wordList.size() - 1);
			for (String word : wordList) {
				if (!SpelledAPI.isUnlocked(player, word)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public Component getName(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundTag compoundnbt = stack.getTag();
			String s = compoundnbt.getString("title");
			if (!StringUtil.isNullOrEmpty(s)) {
				return new TextComponent(s);
			}
		}

		return super.getName(stack);
	}

	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> textComponents, TooltipFlag flag) {
		if (stack.hasTag()) {
			CompoundTag compoundnbt = stack.getTag();
			String s = compoundnbt.getString("author");
			if (!StringUtil.isNullOrEmpty(s)) {
				textComponents.add((new TranslatableComponent("book.byAuthor", s)).withStyle(ChatFormatting.GRAY));
			}
		}
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return stack.hasTag() && stack.getTag().getBoolean("sealed");
	}
}
