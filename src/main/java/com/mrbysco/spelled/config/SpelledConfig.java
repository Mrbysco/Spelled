package com.mrbysco.spelled.config;

import com.mrbysco.spelled.Spelled;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public class SpelledConfig {
    public static class Common {

        public final BooleanValue individualLevels;
        public final BooleanValue individualItems;
        public final ConfigValue<? extends String> requiredItem;
        public final BooleanValue requireItems;
        public final IntValue maxLevel;
        public final ConfigValue<List<? extends String>> individualLevelCosts;
        public final ConfigValue<List<? extends String>> individualItemCosts;
        public final IntValue xpMultiplier;
        public final BooleanValue hideKnowledgeTomeInfo;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("General settings")
                    .push("general");

            individualLevels = builder
                    .comment("Decides whether you use the individual level cost (Cost to level up can be different per level) or use a scaling level cost")
                    .define("individualLevels", true);

            individualItems = builder
                    .comment("Decides whether you use the individual item cost (Cost to level up can be different per level) or use a scaling item cost")
                    .define("individualItems", true);

            requireItems = builder
                    .comment("Decides whether you need specific items to level up (Default: true)")
                    .define("requireItems", true);

            requiredItem = builder
                    .comment("Decides whether you need specific items to level up (Default: minecraft:lapis_lazuli)")
                    .define("requiredItem", "minecraft:lapis_lazuli", o -> o instanceof String);

            String[] levelCost = new String[]
                    {
                            "1,5",
                            "2,5",
                            "3,5",
                            "4,5",
                            "5,5",
                            "6,5",
                            "7,5",
                            "8,5",
                            "9,5",
                            "10,5",
                            "11,5",
                            "12,5",
                            "13,5",
                            "14,5",
                            "15,5",
                            "16,5",
                            "17,5",
                            "18,5",
                            "19,5",
                            "20,5"
                    };

            individualLevelCosts = builder
                    .comment("Determines how much xp you need to pay per individual level when enabled (XP Levels) [Syntax: level,xp_cost]",
                    "If you have changed the maxLevel and enabled individualLevels you'll need supply the new individual level costs")
                    .defineList("individualLevelCosts", Arrays.asList(levelCost), o -> (o instanceof String));

            String[] itemCost = new String[]
                    {
                        "1,minecraft:lapis_lazuli,10",
                        "2,minecraft:lapis_lazuli,10",
                        "3,minecraft:lapis_lazuli,10",
                        "4,minecraft:lapis_lazuli,10",
                        "5,minecraft:lapis_lazuli,10",
                        "6,minecraft:lapis_lazuli,10",
                        "7,minecraft:lapis_lazuli,10",
                        "8,minecraft:lapis_lazuli,10",
                        "9,minecraft:lapis_lazuli,10",
                        "10,minecraft:lapis_lazuli,10",
                        "11,minecraft:lapis_lazuli,10",
                        "12,minecraft:lapis_lazuli,10",
                        "13,minecraft:lapis_lazuli,10",
                        "14,minecraft:lapis_lazuli,10",
                        "15,minecraft:lapis_lazuli,10",
                        "16,minecraft:lapis_lazuli,10",
                        "17,minecraft:lapis_lazuli,10",
                        "18,minecraft:lapis_lazuli,10",
                        "19,minecraft:lapis_lazuli,10",
                        "20,minecraft:lapis_lazuli,10"
                    };

            individualItemCosts = builder
                    .comment("Determines how which item each level costs when 'requireItems' is enabled (XP Levels) [Syntax: level,modid:item_name,amount]",
                    "If you have changed the maxLevel and enabled requireItems you'll need supply the new individual item costs")
                    .defineList("individualItemCosts", Arrays.asList(itemCost), o -> (o instanceof String));

            maxLevel = builder
                    .comment("Decides the highest level a player can become (Default: 20)")
                    .defineInRange("maxLevel", 20, 1, Integer.MAX_VALUE);

            xpMultiplier = builder
                    .comment("Decides how much xp you need to pay per level (cost is multiplied * level)")
                    .defineInRange("xpMultiplier", 100, 1, Integer.MAX_VALUE);

            hideKnowledgeTomeInfo = builder
                    .comment("Decides whether the tooltip of the Tome of Knowledge tells you what's inside (Default: true)")
                    .define("hideKnowledgeTomeInfo", true);

            builder.pop();
        }
    }

    public static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        Spelled.LOGGER.debug("Loaded Spelled's config file {}", configEvent.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfig.Reloading configEvent) {
        Spelled.LOGGER.debug("Spelled's config just got changed on the file system!");
        ConfigCache.refreshCache();
    }
}
