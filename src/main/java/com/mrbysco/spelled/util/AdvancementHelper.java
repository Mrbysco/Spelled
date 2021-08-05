package com.mrbysco.spelled.util;

import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class AdvancementHelper {

    public static void removeAllAdjectiveAdvancements(ServerPlayerEntity player) {
        KeywordRegistry registry = KeywordRegistry.instance();
        for(String adjective : registry.getAdjectives()) {
            lockAdjectiveAdvancement(player, adjective);
        }
        lockAdvancement(player, "color_lore");
    }

    public static void unlockAdjectiveAdvancement(ServerPlayerEntity player, String adjective) {
        unlockAdvancement(player, "adjective_" + adjective);

        if(KeywordRegistry.instance().isColor(adjective) && hasAllColors(player)) {
            unlockAdvancement(player, "color_lore");
        }
    }

    public static void unlockAdvancement(ServerPlayerEntity player, String name) {
        Advancement advancementIn = player.getServer().getAdvancementManager().getAdvancement(new ResourceLocation("spelled:" + name));
        if(advancementIn != null) {
            AdvancementProgress advancementprogress = player.getAdvancements().getProgress(advancementIn);
            if (!advancementprogress.isDone()) {
                for(String s : advancementprogress.getRemaningCriteria()) {
                    player.getAdvancements().grantCriterion(advancementIn, s);
                }
            }
        }
    }

    public static void lockAdjectiveAdvancement(ServerPlayerEntity player, String adjective) {
        lockAdvancement(player, "adjective_" + adjective);
    }

    public static void lockAdvancement(ServerPlayerEntity player, String name) {
        Advancement advancementIn = player.getServer().getAdvancementManager().getAdvancement(new ResourceLocation("spelled:" + name));
        if(advancementIn != null) {
            AdvancementProgress advancementprogress = player.getAdvancements().getProgress(advancementIn);
            if (advancementprogress.hasProgress()) {
                for(String s : advancementprogress.getCompletedCriteria()) {
                    player.getAdvancements().revokeCriterion(advancementIn, s);
                }
            }
        }
    }

    public static boolean hasAllColors(ServerPlayerEntity player) {
        KeywordRegistry registry = KeywordRegistry.instance();
        boolean flag = true;
        for(String color : registry.getColors()) {
            Advancement advancementIn = player.getServer().getAdvancementManager().getAdvancement(new ResourceLocation("spelled:adjective_" + color));
            AdvancementProgress advancementprogress = player.getAdvancements().getProgress(advancementIn);
            if (!advancementprogress.isDone()) {
                flag = false;
                break;
            }
        }
        return flag;
    }
}
