package com.mrbysco.spelled.util;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.capability.ISpellData;
import com.mrbysco.spelled.api.capability.SpellDataCapability;
import com.mrbysco.spelled.api.keywords.IKeyword;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.config.SpelledConfig;
import com.mrbysco.spelled.entity.SpellEntity;
import com.mrbysco.spelled.registry.SpelledRegistry;
import com.mrbysco.spelled.registry.keyword.TypeKeyword;
import com.mrbysco.spelled.registry.keyword.TypeKeyword.Type;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ServerChatEvent;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SpellUtil {

	public static Component manualCastSpell(ServerPlayer player, String spell, Component comp) {
		ServerChatEvent event = new ServerChatEvent(player, spell, comp);
		castSpell(event);
		if (event.isCanceled()) {
			return null;
		}
		return event.getMessage();
	}

	public static void castSpell(ServerChatEvent event) {
		ServerPlayer player = event.getPlayer();
		SpelledAPI.getSpellDataCap(player).ifPresent(data -> {
			KeywordRegistry registry = KeywordRegistry.instance();
			String message = event.getRawText().toLowerCase(Locale.ROOT);
			String[] wordArray = message.split("\\s+");
			List<String> words = Arrays.asList(wordArray);

			boolean validSpellFormation = isValidSpellFormation(player, words);
			if (words.size() >= 2 && validSpellFormation) {
				if (SpellUtil.canCastSpell(player, words)) {
					if (isOnCooldown(player)) {
						event.setCanceled(true);
						return;
					}

					//Do our stuff
					IKeyword lastKeyword = registry.getKeywordFromName(words.get(words.size() - 1));
					ServerLevel level = player.serverLevel();

					if (lastKeyword instanceof TypeKeyword typeKeyword) {
						SpellEntity spell = constructEntity(player, typeKeyword.getType());

						StringBuilder castText = new StringBuilder();
						MutableComponent descriptionComponent = Component.literal("");
						int cooldown = -1;
						for (int i = 0; i < (words.size() - 1); i++) {
							IKeyword keyword = registry.getKeywordFromName(words.get(i));
							if (keyword != null) {
								cooldown += keyword.getSlots();
								castText.append(keyword.getKeyword()).append(" ");
								descriptionComponent.append(keyword.getDescription()).append(Component.literal(" "));
								int previous = i - 1;
								if (previous >= 0 && previous < (words.size() - 1))
									keyword.cast(level, player, spell, registry.getKeywordFromName(words.get(previous)));
								else
									keyword.cast(level, player, spell, null);
							}
						}
						cooldown = Mth.clamp(cooldown, 1, Integer.MAX_VALUE);
						castText.append(lastKeyword.getKeyword());
						MutableComponent castComponent = Component.literal(castText.toString());
						descriptionComponent.append(typeKeyword.getDescription());
						descriptionComponent.withStyle(ChatFormatting.GOLD);
						castComponent.setStyle(event.getMessage().getStyle().withHoverEvent(
								new HoverEvent(Action.SHOW_TEXT, descriptionComponent))).withStyle(ChatFormatting.GOLD);

						MutableComponent finalMessage = Component.translatable("spelled.spell.cast", player.getDisplayName(), castComponent);
						if (spell != null) {
							if (!player.getAbilities().instabuild) {
								SpelledAPI.setCooldown(player, cooldown);
								SpelledAPI.syncCap(player);
							}
							if (typeKeyword.getType() != Type.SELF) {
								shootSpell(player, spell);
								level.addFreshEntity(spell);
							} else {
								spell.handleEntityHit(player);
								spell.discard();
							}
						}

						if (SpelledConfig.COMMON.proximity.get() > 0) {
							event.setCanceled(true);
							List<ServerPlayer> playerEntities = level.players();
							for (ServerPlayer nearbyPlayer : playerEntities) {
								if (nearbyPlayer.getUUID().equals(player.getUUID()) ||
										(nearbyPlayer.level().dimension() == level.dimension() && player.distanceToSqr(nearbyPlayer) <= SpelledConfig.COMMON.proximity.get())) {
									nearbyPlayer.sendSystemMessage(finalMessage, true);
								}
							}
						} else {
							event.setMessage(finalMessage);
						}
					}
				} else {
					event.setCanceled(true);
				}
			}
		});
	}

	public static boolean isValidSpellFormation(ServerPlayer player, List<String> words) {
		ISpellData data = SpelledAPI.getSpellDataCap(player).orElse(new SpellDataCapability());
		final KeywordRegistry registry = KeywordRegistry.instance();

		//Check if every word matches a keyword
		for (String word : words) {
			//Unknown word. Probably not a spell
			if (!registry.containsKey(word))
				return false;
			//Doesn't know the word
			if (!data.knowsKeyword(word))
				return false;
		}

		return true;
	}

	public static boolean canCastSpell(ServerPlayer player, List<String> words) {
		ISpellData data = SpelledAPI.getSpellDataCap(player).orElse(new SpellDataCapability());
		final KeywordRegistry registry = KeywordRegistry.instance();

		//If creative just return true if the chat message was a valid spell
		if (player.getAbilities().instabuild)
			return true;

		int currentLevel = data.getLevel();

		if (currentLevel == 0) {
			MutableComponent finalMessage = Component.translatable("spelled.spell.no_levels", player.getDisplayName()).withStyle(ChatFormatting.RED);
			player.sendSystemMessage(finalMessage);
			return false;
		}

		int maxLevelWord = 0;
		for (String word : words) {
			IKeyword keyword = registry.getKeywordFromName(word);
			if (keyword != null && keyword.getLevel() > maxLevelWord)
				maxLevelWord = keyword.getLevel();
		}

		if (maxLevelWord > currentLevel) {
			MutableComponent errorMessage = Component.translatable("spelled.spell.insufficient_level", player.getDisplayName(), maxLevelWord, currentLevel)
					.withStyle(ChatFormatting.RED);
			player.sendSystemMessage(errorMessage);
			return false;
		}

		int maxWordCount = LevelHelper.getAllowedWordCount(currentLevel);
		if (maxWordCount > 0 && words.size() <= maxWordCount) {
			return true;
		} else {
			MutableComponent errorMessage = Component.translatable("spelled.spell.too_many_words", player.getDisplayName(), maxWordCount)
					.withStyle(ChatFormatting.RED);
			player.sendSystemMessage(errorMessage);
			return false;
		}
	}

	public static boolean isOnCooldown(ServerPlayer player) {
		ISpellData data = SpelledAPI.getSpellDataCap(player).orElse(new SpellDataCapability());
		//Check if player is on cooldown
		int cooldown = data.getCastCooldown();
		if (cooldown > 0) {
			MutableComponent finalMessage = Component.translatable("spelled.spell.cooldown", player.getDisplayName(), cooldown);
			player.sendSystemMessage(finalMessage);
			return true;
		}
		return false;
	}

	public static SpellEntity constructEntity(ServerPlayer player, @Nonnull Type type) {
		SpellEntity spell = new SpellEntity(player, player.level());
		spell.setSpellType(type.getId());

		return spell;
	}

	public static void shootSpell(ServerPlayer player, SpellEntity spell) {
		spell.setOwner(player);
		spell.setPos(player.getX(), player.getEyeY() - (double) 0.1F, player.getZ());
		if (spell.getSpellType() == 1) { //Projectile
			spell.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 4.0F, 0.0F);
		} else { //Ball (Self is handled elsewhere)
			spell.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.0F, 0.0F);
		}
		player.level().playSound((Player) null, player.blockPosition(), SpelledRegistry.SHOOT_SPELL.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (player.level().random.nextFloat() * 0.4F + 1.2F) + 0.5F);
	}
}
