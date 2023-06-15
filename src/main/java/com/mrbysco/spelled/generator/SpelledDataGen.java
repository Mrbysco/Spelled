package com.mrbysco.spelled.generator;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.generator.assets.SpelledLanguageProvider;
import com.mrbysco.spelled.generator.data.SpelledAdvancements;
import com.mrbysco.spelled.generator.data.SpelledDamageTypeProvider;
import com.mrbysco.spelled.generator.data.SpelledLootProvider;
import com.mrbysco.spelled.generator.data.SpelledPatchouliProvider;
import com.mrbysco.spelled.generator.data.SpelledRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpelledDataGen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		ExistingFileHelper helper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		if (event.includeServer()) {
			generator.addProvider(event.includeServer(), new SpelledLootProvider(packOutput));
			generator.addProvider(event.includeServer(), new SpelledRecipes(packOutput));
			generator.addProvider(event.includeServer(), new SpelledAdvancements(packOutput, lookupProvider, helper));
			generator.addProvider(event.includeServer(), new SpelledPatchouliProvider(packOutput));

			generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(
					packOutput, CompletableFuture.supplyAsync(SpelledDataGen::getProvider), Set.of(Reference.MOD_ID)));
		}
		if (event.includeClient()) {
			generator.addProvider(event.includeServer(), new SpelledLanguageProvider(packOutput));
		}
	}

	private static HolderLookup.Provider getProvider() {
		final RegistrySetBuilder registryBuilder = new RegistrySetBuilder();
		registryBuilder.add(Registries.DAMAGE_TYPE, SpelledDamageTypeProvider::bootstrap);
		RegistryAccess.Frozen regAccess = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
		return registryBuilder.buildPatch(regAccess, VanillaRegistries.createLookup());
	}
}