package com.mrbysco.spelled.util;

import com.mrbysco.spelled.registry.KeywordRegistry;
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
    }

    public static void unlockAdjectiveAdvancement(ServerPlayerEntity player, String adjective) {
        Advancement advancementIn = player.getServer().getAdvancementManager().getAdvancement(new ResourceLocation("spelled:adjective_" + adjective));
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
        Advancement advancementIn = player.getServer().getAdvancementManager().getAdvancement(new ResourceLocation("spelled:adjective_" + adjective));
        if(advancementIn != null) {
            AdvancementProgress advancementprogress = player.getAdvancements().getProgress(advancementIn);
            if (advancementprogress.hasProgress()) {
                for(String s : advancementprogress.getCompletedCriteria()) {
                    player.getAdvancements().revokeCriterion(advancementIn, s);
                }
            }
        }
    }
}
