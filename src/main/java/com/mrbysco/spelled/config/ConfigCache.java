package com.mrbysco.spelled.config;

import com.mrbysco.spelled.Spelled;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigCache {
	public static Item requiredItem;
	public static Map<Integer, Integer> individualLevelXPCosts;
	public static Map<Integer, ItemCost> individualLevelItemCosts;

	public static void refreshCache() {
		setRequiredItem(SpelledConfig.COMMON.requiredItem.get());

		generateLevelCostMap(SpelledConfig.COMMON.individualLevelCosts.get());
		generateItemCostMap(SpelledConfig.COMMON.individualItemCosts.get());
	}

	public static void setRequiredItem(String value) {
		if (value.isEmpty()) {
			Spelled.LOGGER.error("'requiredItem' is empty, using default");
			requiredItem = Items.LAPIS_LAZULI;
		} else {
			Item item = BuiltInRegistries.ITEM.getValue(Identifier.tryParse(value));
			if (item != null) {
				requiredItem = item;
			} else {
				Spelled.LOGGER.error("'requiredItem' couldn't be parsed, using default");
				requiredItem = Items.LAPIS_LAZULI;
			}
		}
	}

	public static void generateItemCostMap(List<? extends String> configValues) {
		HashMap<Integer, ItemCost> itemCostMap = new HashMap<>();
		Item defaultItem = requiredItem;

		if (configValues.isEmpty()) {
			Spelled.LOGGER.error("'individualItemCosts' is empty, supplying dummy values");
			for (int i = 0; i < 20; i++) {
				int level = i + 1;
				itemCostMap.put(level, new ItemCost(defaultItem, 10));
			}
		} else {
			for (int i = 0; i < configValues.size(); i++) {
				String configValue = configValues.get(i);
				if (!configValue.contains(",")) {
					Spelled.LOGGER.error("Invalid syntax '{}' found in 'individualLevelCosts' config values", configValue);
				} else {
					String[] values = configValue.split(",");
					if (values.length == 3) {
						int dummyLevel = i + 1;
						int dummyItemCost = 5;
						int level = NumberUtils.isParsable(values[0]) ? Integer.parseInt(values[0]) : -1;
						Item item = BuiltInRegistries.ITEM.getValue(Identifier.tryParse(values[1]));
						int itemCost = NumberUtils.isParsable(values[2]) ? Integer.parseInt(values[2]) : -1;

						if (level == -1) {
							Spelled.LOGGER.error("Couldn't parse level value for {}, using dummy value: {}", configValue, dummyLevel);
							level = dummyLevel;
						}
						if (item == null) {
							Spelled.LOGGER.error("Couldn't parse specified item {}, using dummy value: {}", values[1], BuiltInRegistries.ITEM.getKey(defaultItem));
							item = defaultItem;
						}
						if (itemCost == -1) {
							Spelled.LOGGER.error("Couldn't parse item cost value for {}, using dummy value: {}", configValue, dummyItemCost);
							itemCost = dummyItemCost;
						}
						itemCostMap.put(level, new ItemCost(item, itemCost));
					} else {
						Spelled.LOGGER.error("Not enough data specified. Couldn't locate Level, XP cost and item cost inside of specified {}. " +
								"Are you sure you used ,'s ?", configValue);
					}
				}
			}
		}

		int maxLevel = SpelledConfig.COMMON.maxLevel.get();
		if (SpelledConfig.COMMON.individualItems.get() && itemCostMap.size() < maxLevel) {
			Spelled.LOGGER.error("Individual items is enabled but there aren't enough items supplied in 'individualItemCosts'.");
			Spelled.LOGGER.error("Currently 'individualItemCosts' only has {} out of {} items supplied.", itemCostMap.size(), maxLevel);

			int currentAmount = itemCostMap.size();
			int amountMissing = maxLevel - itemCostMap.size();
			for (int i = 0; i < amountMissing; i++) {
				itemCostMap.put(currentAmount + (i + 1), new ItemCost(defaultItem, 10));
			}
		}

		individualLevelItemCosts = itemCostMap;
	}

	public static void generateLevelCostMap(List<? extends String> configValues) {
		HashMap<Integer, Integer> xpCostMap = new HashMap<>();
		if (configValues.isEmpty()) {
			Spelled.LOGGER.error("individualLevelCosts is empty, supplying dummy values");
			for (int i = 0; i < 20; i++) {
				int level = i + 1;
				xpCostMap.put(level, 5);
			}
		} else {
			for (int i = 0; i < configValues.size(); i++) {
				String configValue = configValues.get(i);
				if (!configValue.contains(",")) {
					Spelled.LOGGER.error("Invalid syntax '{}' found in 'individualLevelCosts' config values", configValue);
				} else {
					String[] values = configValue.split(",");
					if (values.length == 2) {
						int dummyLevel = i + 1;
						int dummyCost = 5;
						int level = NumberUtils.isParsable(values[0]) ? Integer.parseInt(values[0]) : -1;
						int cost = NumberUtils.isParsable(values[1]) ? Integer.parseInt(values[1]) : -1;
						if (level == -1) {
							Spelled.LOGGER.error("Couldn't parse level value for {}, using dummy value: {}", configValue, dummyLevel);
							level = dummyLevel;
						}
						if (cost == -1) {
							Spelled.LOGGER.error("Couldn't parse cost value for {}, using dummy value: {}", configValue, dummyCost);
							cost = dummyCost;
						}
						xpCostMap.put(level, cost);
					} else {
						Spelled.LOGGER.error("Not enough data specified. Couldn't locate Level and XP cost in specified {}. " +
								"Are you sure you separated with a , ?", configValue);
					}
				}
			}
		}

		int maxLevel = SpelledConfig.COMMON.maxLevel.get();
		if (SpelledConfig.COMMON.individualLevels.get() && xpCostMap.size() < maxLevel) {
			Spelled.LOGGER.error("Individual levels is enabled but there aren't enough costs supplied in 'individualLevelCosts'.");
			Spelled.LOGGER.error("Currently 'individualLevelCosts' only has {} out of {} levels supplied.", xpCostMap.size(), maxLevel);

			int currentAmount = xpCostMap.size();
			int amountMissing = maxLevel - xpCostMap.size();
			for (int i = 0; i < amountMissing; i++) {
				xpCostMap.put(currentAmount + (i + 1), 5);
			}
		}

		individualLevelXPCosts = xpCostMap;
	}

	public record ItemCost(Item item, int cost) {
		public Item getItem() {
			return item;
		}

		public int getCost() {
			return cost;
		}
	}
}
