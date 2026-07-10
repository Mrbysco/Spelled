package com.mrbysco.spelled.api;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.capability.ISpellData;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.packets.message.SpellDataSyncPayload;
import com.mrbysco.spelled.util.AdvancementHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.EntityCapability;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class SpelledAPI {
	public static final EntityCapability<ISpellData, Void> SPELL_DATA_CAP = EntityCapability.createVoid(
			Reference.modLoc("spell_data"), ISpellData.class);

	public static Optional<ISpellData> getSpellDataCap(@Nonnull final Player player) {
		return Optional.ofNullable(player.getCapability(SpelledAPI.SPELL_DATA_CAP));
	}

	public static void forceLevelUp(Player player) {
		SpelledAPI.getSpellDataCap(player).ifPresent(cap -> cap.setLevel(cap.getLevel() + 1));
	}

	public static void forceLevelDown(Player player) {
		SpelledAPI.getSpellDataCap(player).ifPresent(cap -> {
			if (cap.getLevel() > 1) {
				cap.setLevel(cap.getLevel() - 1);
			}
		});
	}

	public static void forceSetLevel(Player player, int level) {
		SpelledAPI.getSpellDataCap(player).ifPresent(cap -> cap.setLevel(level));
	}

	public static int getLevel(Player player) {
		Optional<ISpellData> cap = SpelledAPI.getSpellDataCap(player);
		if (cap.isPresent()) {
			ISpellData data = cap.orElse(null);
			return data.getLevel();
		}
		return -1;
	}

	public static void resetUnlocks(Player player) {
		SpelledAPI.getSpellDataCap(player).ifPresent(ISpellData::resetUnlocks);
		if (!player.level().isClientSide()) {
			AdvancementHelper.removeAllAdjectiveAdvancements((ServerPlayer) player);
		}
	}

	public static List<String> getUnlocks(Player player) {
		Optional<ISpellData> cap = SpelledAPI.getSpellDataCap(player);
		if (cap.isPresent()) {
			ISpellData data = cap.orElse(null);
			List<String> unlocks = new ArrayList<>(data.getUnlocked().keySet());
			unlocks.removeAll(KeywordRegistry.instance().getTypes());
			return unlocks;
		}
		return new ArrayList<>();
	}

	public static boolean isUnlocked(Player player, String adjective) {
		List<String> unlocks = getUnlocks(player);
		return unlocks.contains(adjective.toLowerCase(Locale.ROOT));
	}

	public static void unlockKeyword(Player player, String keyword) {
		SpelledAPI.getSpellDataCap(player).ifPresent(cap -> cap.unlockKeyword(keyword));
		if (!player.level().isClientSide()) {
			AdvancementHelper.unlockAdjectiveAdvancement((ServerPlayer) player, keyword);
		}
	}

	public static void lockKeyword(Player player, String keyword) {
		SpelledAPI.getSpellDataCap(player).ifPresent(cap -> cap.lockKeyword(keyword));
		if (!player.level().isClientSide()) {
			AdvancementHelper.lockAdjectiveAdvancement((ServerPlayer) player, keyword);
		}
	}

	public static int getCooldown(Player player) {
		Optional<ISpellData> cap = SpelledAPI.getSpellDataCap(player);
		if (cap.isPresent()) {
			ISpellData data = cap.orElse(null);
			return data.getCastCooldown();
		}
		return 0;
	}

	public static void setCooldown(Player player, int amount) {
		SpelledAPI.getSpellDataCap(player).ifPresent(cap -> cap.setCastCooldown(amount));
	}

	public static void clearCooldown(Player player) {
		SpelledAPI.getSpellDataCap(player).ifPresent(cap -> cap.setCastCooldown(0));
	}

	public static void syncCap(ServerPlayer player) {
		SpelledAPI.getSpellDataCap(player).ifPresent(cap -> player.connection.send(new SpellDataSyncPayload(player.registryAccess(), cap, player.getGameProfile().id())));
	}
}
