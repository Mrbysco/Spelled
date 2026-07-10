package com.mrbysco.spelled.generator;

import com.mrbysco.spelled.generator.assets.SpelledLanguageProvider;
import com.mrbysco.spelled.generator.data.SpelledAdvancements;
import com.mrbysco.spelled.generator.data.SpelledDamageTypeTags;
import com.mrbysco.spelled.generator.data.SpelledLootProvider;
import com.mrbysco.spelled.generator.data.SpelledPatchouliProvider;
import com.mrbysco.spelled.generator.data.SpelledRecipes;
import com.mrbysco.spelled.handler.LootHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class SpelledDataGen {
	private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
			.add(Registries.DAMAGE_TYPE, SpelledDamageTypeBootstrap::bootstrap)
			.add(Registries.VILLAGER_TRADE, LootHandler::tradeBootstrap);

	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		event.createDatapackRegistryObjects(BUILDER);

		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(true, new SpelledLootProvider(packOutput, lookupProvider));
		generator.addProvider(true, new SpelledRecipes.Runner(packOutput, lookupProvider));
		generator.addProvider(true, new SpelledAdvancements(packOutput, lookupProvider));
		generator.addProvider(true, new SpelledDamageTypeTags(packOutput, lookupProvider));
		generator.addProvider(true, new SpelledPatchouliProvider(packOutput, lookupProvider));

		generator.addProvider(true, new SpelledLanguageProvider(packOutput));
	}
}