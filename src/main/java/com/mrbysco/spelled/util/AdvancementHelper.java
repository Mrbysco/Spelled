package com.mrbysco.spelled.util;

import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;

public class AdvancementHelper {

    public static void removeAllAdjectiveAdvancements(ServerPlayer player) {
        KeywordRegistry registry = KeywordRegistry.instance();
        for(String adjective : registry.getAdjectives()) {
            lockAdjectiveAdvancement(player, adjective);
        }
        lockAdvancement(player, "color_lore");
    }

    public static void unlockAdjectiveAdvancement(ServerPlayer player, String adjective) {
        unlockAdvancement(player, "adjective_" + adjective);

        if(KeywordRegistry.instance().isColor(adjective) && hasAllColors(player)) {
            unlockAdvancement(player, "color_lore");
        }
    }

    public static void unlockAdvancement(ServerPlayer player, String name) {
        Advancement advancementIn = player.getServer().getAdvancements().getAdvancement(new ResourceLocation("spelled:" + name));
        if(advancementIn != null) {
            AdvancementProgress advancementprogress = player.getAdvancements().getOrStartProgress(advancementIn);
            if (!advancementprogress.isDone()) {
                for(String s : advancementprogress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancementIn, s);
                }
            }
        }
    }

    public static void lockAdjectiveAdvancement(ServerPlayer player, String adjective) {
        lockAdvancement(player, "adjective_" + adjective);
    }

    public static void lockAdvancement(ServerPlayer player, String name) {
        Advancement advancementIn = player.getServer().getAdvancements().getAdvancement(new ResourceLocation("spelled:" + name));
        if(advancementIn != null) {
            AdvancementProgress advancementprogress = player.getAdvancements().getOrStartProgress(advancementIn);
            if (advancementprogress.hasProgress()) {
                for(String s : advancementprogress.getCompletedCriteria()) {
                    player.getAdvancements().revoke(advancementIn, s);
                }
            }
        }
    }

    public static boolean hasAllColors(ServerPlayer player) {
        KeywordRegistry registry = KeywordRegistry.instance();
        boolean flag = true;
        for(String color : registry.getColors()) {
            Advancement advancementIn = player.getServer().getAdvancements().getAdvancement(new ResourceLocation("spelled:adjective_" + color));
            AdvancementProgress advancementprogress = player.getAdvancements().getOrStartProgress(advancementIn);
            if (!advancementprogress.isDone()) {
                flag = false;
                break;
            }
        }
        return flag;
    }
}
