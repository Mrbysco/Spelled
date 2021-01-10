package com.mrbysco.spelled.util;

import com.mrbysco.spelled.config.ConfigCache;
import com.mrbysco.spelled.config.ConfigCache.ItemCost;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class LevelHelper {

    public static ItemCost getItemCost(int level) {
        return ConfigCache.individualItems ?
                ConfigCache.individualLevelItemCosts.getOrDefault(level, new ItemCost(ConfigCache.requiredItem, Math.min((10 + (2 * level)), 64))) :
                new ItemCost(ConfigCache.requiredItem, Math.min((10 + (2 * level)), 64));
    }

    public static int getXPCost(int level) {
        final int individualXPCost = ConfigCache.individualLevelXPCosts.getOrDefault(level, 5);
        final int xpCost = ConfigCache.xpMultiplier * level;

        return ConfigCache.individualLevels ? individualXPCost : xpCost;
    }

    public static void levelUpFailItems(PlayerEntity player) {
        player.sendStatusMessage(new TranslationTextComponent("spelled.level_up.fail_item").mergeStyle(TextFormatting.GOLD), true);
    }

    public static void levelUpFailXP(PlayerEntity player) {
        player.sendStatusMessage(new TranslationTextComponent("spelled.level_up.fail_xp").mergeStyle(TextFormatting.GOLD), true);
    }
}
