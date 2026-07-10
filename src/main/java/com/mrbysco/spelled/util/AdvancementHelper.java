package com.mrbysco.spelled.util;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementHelper {
	public static void removeAllAdjectiveAdvancements(ServerPlayer player) {
		KeywordRegistry registry = KeywordRegistry.instance();
		for (String adjective : registry.getAdjectives()) {
			lockAdjectiveAdvancement(player, adjective);
		}
		lockAdvancement(player, "color_lore");
	}

	public static void unlockAdjectiveAdvancement(ServerPlayer player, String adjective) {
		unlockAdvancement(player, "adjective_" + adjective);

		if (KeywordRegistry.instance().isColor(adjective) && hasAllColors(player)) {
			unlockAdvancement(player, "color_lore");
		}
	}

	public static void unlockAdvancement(ServerPlayer player, String name) {
		AdvancementHolder advancementHolder = player.level().getServer().getAdvancements().get(Reference.modLoc(name));
		if (advancementHolder != null) {
			AdvancementProgress advancementprogress = player.getAdvancements().getOrStartProgress(advancementHolder);
			if (!advancementprogress.isDone()) {
				for (String s : advancementprogress.getRemainingCriteria()) {
					player.getAdvancements().award(advancementHolder, s);
				}
			}
		}
	}

	public static void lockAdjectiveAdvancement(ServerPlayer player, String adjective) {
		lockAdvancement(player, "adjective_" + adjective);
	}

	public static void lockAdvancement(ServerPlayer player, String name) {
		AdvancementHolder advancementHolder = player.level().getServer().getAdvancements().get(Reference.modLoc(name));
		if (advancementHolder != null) {
			AdvancementProgress advancementprogress = player.getAdvancements().getOrStartProgress(advancementHolder);
			if (advancementprogress.hasProgress()) {
				for (String s : advancementprogress.getCompletedCriteria()) {
					player.getAdvancements().revoke(advancementHolder, s);
				}
			}
		}
	}

	public static boolean hasAllColors(ServerPlayer player) {
		KeywordRegistry registry = KeywordRegistry.instance();
		boolean flag = true;
		for (String color : registry.getColors()) {
			AdvancementHolder advancementHolder = player.level().getServer().getAdvancements().get(Reference.modLoc("adjective_" + color));
			AdvancementProgress advancementprogress = player.getAdvancements().getOrStartProgress(advancementHolder);
			if (!advancementprogress.isDone()) {
				flag = false;
				break;
			}
		}
		return flag;
	}
}
