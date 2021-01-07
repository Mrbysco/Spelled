package com.mrbysco.spelled.logic;

import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.config.ConfigCache;
import com.mrbysco.spelled.config.ConfigCache.ItemCost;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class LevelUpLogic {

    public void levelUp(ServerPlayerEntity player) {
        int currentLevel = SpelledAPI.getLevel(player);
        if(currentLevel != -1 && currentLevel < ConfigCache.maxLevel) {
            if(canlevelUp(player, currentLevel)) {
                SpelledAPI.forceSetLevel(player, currentLevel + 1);
            }
        }
    }

    /*
     * @return true if user can level up. If true it has already eaten the requirements
     */
    private boolean canlevelUp(ServerPlayerEntity player, int currentLevel) {
        boolean canLevelUp = false;

        final int newLevel = currentLevel + 1;
        final int individualXPCost = ConfigCache.individualLevelXPCosts.getOrDefault(newLevel, 5);
        final int xpCost = ConfigCache.xpMultiplier * newLevel;

        boolean hasXP = ConfigCache.individualLevels ? player.experienceLevel >= individualXPCost : player.experienceTotal >= xpCost;
        if(hasXP) {
            if(ConfigCache.requireItems) {
                final ItemCost itemCost = ConfigCache.individualItems ?
                        ConfigCache.individualLevelItemCosts.getOrDefault(newLevel, new ItemCost(ConfigCache.requiredItem, Math.min((10 + (2 * newLevel)), 64))) :
                        new ItemCost(ConfigCache.requiredItem, Math.min((10 + (2 * newLevel)), 64));

                boolean found = false;
                for (ItemStack stack : player.inventory.mainInventory) {
                    if(!stack.isEmpty()) {
                        if(stack.getItem() == itemCost.getItem() && stack.getCount() >= itemCost.getCost()
                                && player.experienceLevel >= individualXPCost) {
                            stack.shrink(itemCost.getCost());

                            if (ConfigCache.individualLevels)
                                player.addExperienceLevel(-individualXPCost);
                            else
                                player.giveExperiencePoints(-individualXPCost);

                            canLevelUp = true;
                            found = true;
                            break;
                        }
                    }
                }
                if(!found) {
                    levelUpFailItems(player);
                    canLevelUp = false;
                }
            } else {
                if(ConfigCache.individualLevels) {
                    player.addExperienceLevel(-individualXPCost);
                } else {
                    player.giveExperiencePoints(-individualXPCost);
                }
                canLevelUp = true;
            }
        } else {
            levelUpFailXP(player);
            canLevelUp = false;
        }
        return canLevelUp;
    }


    public void levelUpFailItems(ServerPlayerEntity player) {
        player.sendStatusMessage(new TranslationTextComponent("spelled.level_up.fail_item").mergeStyle(TextFormatting.GOLD), true);
    }

    public void levelUpFailXP(ServerPlayerEntity player) {
        player.sendStatusMessage(new TranslationTextComponent("spelled.level_up.fail_xp").mergeStyle(TextFormatting.GOLD), true);
    }
}
