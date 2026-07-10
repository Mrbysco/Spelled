package com.mrbysco.spelled.generator.data;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.registry.SpelledComponents;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import vazkii.patchouli.common.item.PatchouliDataComponents;

import java.util.concurrent.CompletableFuture;

public class SpelledRecipes extends RecipeProvider {

	public SpelledRecipes(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
		super(provider, recipeOutput);
	}

	@Override
	protected void buildRecipes() {
		shaped(RecipeCategory.REDSTONE, SpelledRegistry.LEVELING_ALTAR.get())
				.pattern("IRI")
				.pattern("RBR")
				.pattern("IRI")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('B', Items.BOOK)
				.unlockedBy("has_books", has(Items.BOOK))
				.save(output);

		shapeless(RecipeCategory.MISC, SpelledRegistry.SPELL_BOOK.get())
				.requires(Tags.Items.GEMS_LAPIS)
				.requires(Items.BOOK)
				.unlockedBy("has_books", has(Items.BOOK))
				.save(output);


		RecipeOutput conditionalOutput = output.withConditions(
				new ModLoadedCondition("patchouli")
		);
		ItemStackTemplate tomeTemplate = new ItemStackTemplate(SpelledRegistry.KNOWLEDGE_TOME.get(), DataComponentPatch.builder()
				.set(PatchouliDataComponents.BOOK, Reference.modLoc("knowledge_tome")).build());
		shapeless(RecipeCategory.MISC, tomeTemplate)
				.requires(Tags.Items.STORAGE_BLOCKS_LAPIS)
				.requires(Items.BOOK)
				.unlockedBy("has_books", has(Items.BOOK))
				.save(conditionalOutput);
	}

	public static class Runner extends RecipeProvider.Runner {
		public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
			super(output, completableFuture);
		}

		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
			return new SpelledRecipes(provider, recipeOutput);
		}

		@Override
		public String getName() {
			return "Spelled Recipes";
		}
	}
}
