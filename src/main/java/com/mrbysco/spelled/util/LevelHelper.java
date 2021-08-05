package com.mrbysco.spelled.util;

import com.mrbysco.spelled.config.ConfigCache;
import com.mrbysco.spelled.config.ConfigCache.ItemCost;
import com.mrbysco.spelled.config.SpelledConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class LevelHelper {

    public static int getAllowedWordCount(int level) {
        return level > 0 ? level + 1 : 0;
    }

    public static ItemCost getItemCost(int level) {
        return SpelledConfig.COMMON.individualItems.get() ?
                ConfigCache.individualLevelItemCosts.getOrDefault(level, new ItemCost(ConfigCache.requiredItem, Math.min((10 + (2 * level)), 64))) :
                new ItemCost(ConfigCache.requiredItem, Math.min((10 + (2 * level)), 64));
    }

    public static int getXPCost(int level) {
        final int individualXPCost = ConfigCache.individualLevelXPCosts.getOrDefault(level, 5);
        final int xpCost = SpelledConfig.COMMON.xpMultiplier.get() * level;

        return SpelledConfig.COMMON.individualLevels.get() ? individualXPCost : xpCost;
    }

    public static void levelUpFailItems(PlayerEntity player) {
        player.sendStatusMessage(new TranslationTextComponent("spelled.level_up.fail_item").mergeStyle(TextFormatting.GOLD), true);
    }

    public static void levelUpFailXP(PlayerEntity player) {
        player.sendStatusMessage(new TranslationTextComponent("spelled.level_up.fail_xp").mergeStyle(TextFormatting.GOLD), true);
    }
}
