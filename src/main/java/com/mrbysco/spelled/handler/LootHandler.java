package com.mrbysco.spelled.handler;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.keywords.IKeyword;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.config.SpelledConfig;
import com.mrbysco.spelled.registry.SpelledComponents;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import vazkii.patchouli.common.item.PatchouliDataComponents;

import java.util.List;
import java.util.Optional;

public class LootHandler {

	private static final String hasBookTag = Reference.MOD_PREFIX + ":hasBook";

	@SubscribeEvent
	public void firstJoin(PlayerLoggedInEvent event) {
		Player player = event.getEntity();

		if (!player.level().isClientSide() && SpelledConfig.COMMON.startWithBook.get()) {
			CompoundTag playerData = player.getPersistentData();

			if (!player.hasData(SpelledRegistry.HAS_BOOK_ATTACHMENT)) {
				if (playerData.getBooleanOr(hasBookTag, false)) { // Convert from old tag
					player.setData(SpelledRegistry.HAS_BOOK_ATTACHMENT, true);
					return;
				}

				Item guideBook = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("patchouli", "guide_book"));
				if (guideBook != null) {
					ItemStack guideStack = new ItemStack(guideBook);
					guideStack.set(PatchouliDataComponents.BOOK, Reference.modLoc("knowledge_tome"));
					player.addItem(guideStack);
					player.setData(SpelledRegistry.HAS_BOOK_ATTACHMENT, true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		String prefix = "minecraft:chests/";
		String name = event.getName().toString();

		if (name.startsWith(prefix)) {
			String file = name.substring(name.indexOf(prefix) + prefix.length());
			switch (file) {
				case "stronghold_library", "jungle_temple", "underwater_ruin_big", "end_city_treasure",
				     "buried_treasure", "woodland_mansion", "bastion_treasure", "village_cartographer" ->
						event.getTable().addPool(getInjectPool());
				default -> {
				}
			}
		}
	}

	public static LootPool getInjectPool() {
		LootPool.Builder builder = LootPool.lootPool();
		KeywordRegistry registry = KeywordRegistry.instance();
		if (registry.getAdjectives().isEmpty()) {
			registry.initializeKeywords();
		}
		for (String adjective : registry.getAdjectives()) {
			builder.add(injectTome(adjective));
		}
		builder.add(EmptyLootItem.emptyItem().setWeight(1));

		builder.setBonusRolls(UniformGenerator.between(0, 1))
				.name("spelled_inject");

		return builder.build();
	}

	private static LootPoolEntryContainer.Builder injectTome(String adjective) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(SpelledRegistry.KNOWLEDGE_TOME.get())
				.apply(SetComponentsFunction.setComponent(SpelledComponents.UNLOCK.get(), adjective))
				.when(LootItemRandomChanceCondition.randomChance(0.2F))
				.setWeight(1);

		return entry;
	}

	public static void tradeBootstrap(BootstrapContext<VillagerTrade> context) {
		KeywordRegistry registry = KeywordRegistry.instance();
		if (registry.getAdjectives().isEmpty()) {
			registry.initializeKeywords();
		}
		for (String adjective : registry.getAdjectives()) {
			IKeyword keyword = registry.getKeywordFromName(adjective);
			if (keyword != null) {
				context.register(createKey("wandering_trader/" + adjective),
						new VillagerTrade(new TradeCost(Items.EMERALD, keyword.getLevel() + 2),
								new ItemStackTemplate(SpelledRegistry.KNOWLEDGE_TOME.get(), DataComponentPatch.builder()
										.set(SpelledComponents.UNLOCK.get(), adjective).build()), 1, keyword.getLevel(),
								0.05F, Optional.empty(), List.of()));
			}
		}
	}

	private static ResourceKey<VillagerTrade> createKey(String name) {
		return ResourceKey.create(Registries.VILLAGER_TRADE, Reference.modLoc(name));
	}
}
