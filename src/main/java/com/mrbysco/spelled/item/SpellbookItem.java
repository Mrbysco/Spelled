package com.mrbysco.spelled.item;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.keywords.IKeyword;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.client.gui.book.AdjectiveEntry;
import com.mrbysco.spelled.util.SpellUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpellbookItem extends Item {
	public SpellbookItem(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if(stack.hasTag() && stack.getTag().getBoolean("sealed")) {
			if(!world.isClientSide) {
				ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
				String message = stack.getTag().getString("spell");
				final String regExp = "^[a-zA-Z\\s]*$";
				if (!message.isEmpty() && message.matches(regExp)) {
					ITextComponent itextcomponent = new TranslationTextComponent("chat.type.text", serverPlayer.getDisplayName(),
							net.minecraftforge.common.ForgeHooks.newChatWithLinks(message));
					itextcomponent = SpellUtil.manualCastSpell(serverPlayer, message, itextcomponent);
					if (itextcomponent == null) {
						return ActionResult.fail(stack);
					} else {
						serverPlayer.getServer().getPlayerList().broadcastMessage(itextcomponent, ChatType.CHAT, serverPlayer.getUUID());
					}
				}
			}
		} else {
			if(world.isClientSide) {
				List<AdjectiveEntry> adjectives = new ArrayList<>();
				SpelledAPI.getUnlocks(player).forEach((adjective) -> {
					if(!adjective.isEmpty()) {
						IKeyword word = KeywordRegistry.instance().getKeywordFromName(adjective);
						if(word != null) {
							adjectives.add(new AdjectiveEntry(word, word.getKeyword(), word.getDescription(), word.getSlots()));
						}
					}
				});
				if(adjectives.isEmpty()) {
					player.sendMessage(new TranslationTextComponent("spelled.spell_book.insufficient"), Util.NIL_UUID);
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

	public static boolean makeSureTagIsValid(PlayerEntity player, @Nullable CompoundNBT nbt) {
		if (!makeSureSpellIsValid(player, nbt)) {
			return false;
		} else if (!nbt.contains("title", 8)) {
			return false;
		} else {
			String s = nbt.getString("title");
			return s.length() <= 32 && nbt.contains("author", 8);
		}
	}

	public static boolean makeSureSpellIsValid(PlayerEntity player, @Nullable CompoundNBT nbt) {
		if(nbt != null && nbt.contains("spell")) {
			String currentSpell = nbt.getString("spell");
			String[] words = currentSpell.split(" ");
			List<String> wordList = Arrays.asList(words);
			wordList = wordList.subList(0, wordList.size() - 1);
			for(String word : wordList) {
				if(!SpelledAPI.isUnlocked(player, word)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public ITextComponent getName(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundNBT compoundnbt = stack.getTag();
			String s = compoundnbt.getString("title");
			if (!StringUtils.isNullOrEmpty(s)) {
				return new StringTextComponent(s);
			}
		}

		return super.getName(stack);
	}

	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> textComponents, ITooltipFlag flag) {
		if (stack.hasTag()) {
			CompoundNBT compoundnbt = stack.getTag();
			String s = compoundnbt.getString("author");
			if (!StringUtils.isNullOrEmpty(s)) {
				textComponents.add((new TranslationTextComponent("book.byAuthor", s)).withStyle(TextFormatting.GRAY));
			}
		}
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return stack.hasTag() && stack.getTag().getBoolean("sealed");
	}
}
