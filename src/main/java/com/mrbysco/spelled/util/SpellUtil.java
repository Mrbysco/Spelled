package com.mrbysco.spelled.util;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.capability.ISpellData;
import com.mrbysco.spelled.api.capability.SpellDataCapability;
import com.mrbysco.spelled.api.keywords.IKeyword;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.config.SpelledConfig;
import com.mrbysco.spelled.entity.SpellEntity;
import com.mrbysco.spelled.registry.keyword.TypeKeyword;
import com.mrbysco.spelled.registry.keyword.TypeKeyword.Type;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;
import net.minecraft.world.World;
import net.minecraftforge.event.ServerChatEvent;
import org.lwjgl.system.CallbackI.P;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SpellUtil {

	public static ITextComponent manualCastSpell(ServerPlayerEntity player, String spell, ITextComponent comp) {
		ServerChatEvent event = new ServerChatEvent(player, spell, comp);
		castSpell(event);
		if (event.isCanceled()) {
			return null;
		}
		return event.getComponent();
	}

	public static void castSpell(ServerChatEvent event) {
		ServerPlayerEntity player = event.getPlayer();
		SpelledAPI.getSpellDataCap(player).ifPresent(data -> {
			KeywordRegistry registry = KeywordRegistry.instance();
			String message = event.getMessage().toLowerCase(Locale.ROOT);
			String[] wordArray = message.split("\\s+");
			List<String> words = Arrays.asList(wordArray);

			boolean validSpellFormation = isValidSpellFormation(player, words);
			if(words.size() >= 2 && validSpellFormation) {
				if (SpellUtil.canCastSpell(player, words)) {
					if (isOnCooldown(player)) {
						event.setCanceled(true);
						return;
					}

					//Do our stuff
					IKeyword lastKeyword = registry.getKeywordFromName(words.get(words.size() - 1));
					World world = player.level;

					if (lastKeyword instanceof TypeKeyword) {
						TypeKeyword typeKeyword = (TypeKeyword) lastKeyword;
						SpellEntity spell = constructEntity(player, typeKeyword.getType());

						StringBuilder castText = new StringBuilder();
						IFormattableTextComponent descriptionComponent = new StringTextComponent("");
						int cooldown = -1;
						for (int i = 0; i < (words.size() - 1); i++) {
							IKeyword keyword = registry.getKeywordFromName(words.get(i));
							if (keyword != null) {
								cooldown += keyword.getSlots();
								castText.append(keyword.getKeyword()).append(" ");
								descriptionComponent.append(keyword.getDescription()).append(new StringTextComponent(" "));
								int previous = i - 1;
								if (previous >= 0 && previous < (words.size() - 1))
									keyword.cast(world, player, spell, registry.getKeywordFromName(words.get(previous)));
								else
									keyword.cast(world, player, spell, null);
							}
						}
						cooldown = MathHelper.clamp(cooldown, 1, Integer.MAX_VALUE);
						castText.append(lastKeyword.getKeyword());
						StringTextComponent castComponent = new StringTextComponent(castText.toString());
						descriptionComponent.append(typeKeyword.getDescription());
						descriptionComponent.withStyle(TextFormatting.GOLD);
						castComponent.setStyle(event.getComponent().getStyle().withHoverEvent(
								new HoverEvent(Action.SHOW_TEXT, descriptionComponent))).withStyle(TextFormatting.GOLD);

						IFormattableTextComponent finalMessage = new TranslationTextComponent("spelled.spell.cast", player.getDisplayName(), castComponent);
						if (spell != null) {
							if (!player.abilities.instabuild) {
								SpelledAPI.setCooldown(player, cooldown);
								SpelledAPI.syncCap(player);
							}
							if (typeKeyword.getType() != Type.SELF) {
								shootSpell(player, spell);
								world.addFreshEntity(spell);
							} else {
								spell.handleEntityHit(player);
								spell.remove(false);
							}
						}

						if (SpelledConfig.COMMON.proximity.get() > 0) {
							event.setCanceled(true);
							List<? extends PlayerEntity> playerEntities = world.players();
							for (PlayerEntity nearbyPlayer : playerEntities) {
								if (nearbyPlayer.getUUID().equals(player.getUUID()) ||
										(nearbyPlayer.level.dimension() == world.dimension() && player.distanceToSqr(nearbyPlayer) <= SpelledConfig.COMMON.proximity.get())) {
									player.sendMessage(finalMessage, player.getUUID());
								}
							}
						} else {
							event.setComponent(finalMessage);
						}
					}
				} else {
					event.setCanceled(true);
				}
			}
		});
	}

	public static boolean isValidSpellFormation(ServerPlayerEntity player, List<String> words) {
		ISpellData data = SpelledAPI.getSpellDataCap(player).orElse(new SpellDataCapability());
		final KeywordRegistry registry = KeywordRegistry.instance();

		//Check if every word matches a keyword
		for (String word : words) {
			//Unknown word. Probably not a spell
			if (!registry.containsKey(word))
				return false;
			//Doesn't know the word
			if(!data.knowsKeyword(word))
				return false;
		}

		return true;
	}

	public static boolean canCastSpell(ServerPlayerEntity player, List<String> words) {
		ISpellData data = SpelledAPI.getSpellDataCap(player).orElse(new SpellDataCapability());
		final KeywordRegistry registry = KeywordRegistry.instance();

		//If creative just return true if the chat message was a valid spell
		if(player.abilities.instabuild)
			return true;

		int currentLevel = data.getLevel();

		if(currentLevel == 0) {
			IFormattableTextComponent finalMessage = new TranslationTextComponent("spelled.spell.no_levels", player.getDisplayName())
					.withStyle(TextFormatting.RED);
			player.sendMessage(finalMessage, Util.NIL_UUID);
			return false;
		}

		int maxLevelWord = 0;
		for (String word : words) {
			IKeyword keyword = registry.getKeywordFromName(word);
			if(keyword != null && keyword.getLevel() > maxLevelWord)
				maxLevelWord = keyword.getLevel();
		}

		if(maxLevelWord > currentLevel) {
			IFormattableTextComponent errorMessage = new TranslationTextComponent("spelled.spell.insufficient_level", player.getDisplayName(), maxLevelWord, currentLevel)
					.withStyle(TextFormatting.RED);
			player.sendMessage(errorMessage, Util.NIL_UUID);
			return false;
		}

		int maxWordCount = LevelHelper.getAllowedWordCount(currentLevel);
		if(maxWordCount > 0 && words.size() <= maxWordCount) {
			return true;
		} else {
			IFormattableTextComponent errorMessage = new TranslationTextComponent("spelled.spell.too_many_words", player.getDisplayName(), maxWordCount)
					.withStyle(TextFormatting.RED);
			player.sendMessage(errorMessage, Util.NIL_UUID);
			return false;
		}
	}

	public static boolean isOnCooldown(ServerPlayerEntity player) {
		ISpellData data = SpelledAPI.getSpellDataCap(player).orElse(new SpellDataCapability());
		//Check if player is on cooldown
		int cooldown = data.getCastCooldown();
		if(cooldown > 0) {
			IFormattableTextComponent finalMessage = new TranslationTextComponent("spelled.spell.cooldown", player.getDisplayName(), cooldown);
			player.sendMessage(finalMessage, Util.NIL_UUID);
			return true;
		}
		return false;
	}

	public static SpellEntity constructEntity(ServerPlayerEntity player, @Nonnull Type type) {
		SpellEntity spell = new SpellEntity(player, player.level);
		spell.setSpellType(type.getId());

		return spell;
	}

	public static void shootSpell(ServerPlayerEntity player, SpellEntity spell) {
		spell.setOwner(player);
		spell.setPos(player.getX(), player.getEyeY() - (double)0.1F, player.getZ());
		if (spell.getSpellType() == 1) { //Projectile
			spell.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 4.0F, 0.0F);
		} else { //Ball (Self is handled elsewhere)
			spell.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 2.0F, 0.0F);
		}
	}
}
