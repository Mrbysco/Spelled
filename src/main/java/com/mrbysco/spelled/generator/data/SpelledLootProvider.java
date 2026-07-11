package com.mrbysco.spelled.generator.data;

import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SpelledLootProvider extends LootTableProvider {
	public SpelledLootProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, Set.of(), List.of(
				new SubProviderEntry(Blocks::new, LootContextParamSets.BLOCK)
		), lookupProvider);
	}

	private static class Blocks extends BlockLootSubProvider {

		public Blocks(HolderLookup.Provider provider) {
			super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
		}

		@Override
		protected void generate() {
			this.dropSelf(SpelledRegistry.LEVELING_ALTAR.get());
		}

		@Override
		protected Iterable<Block> getKnownBlocks() {
			return SpelledRegistry.BLOCKS.getEntries().stream().map(holder -> (Block) holder.value())::iterator;
		}
	}
}