package com.mrbysco.spelled.generator.data;

import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class SpelledRecipes extends RecipeProvider {

	public SpelledRecipes(PackOutput packOutput) {
		super(packOutput);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SpelledRegistry.LEVELING_ALTAR.get())
				.pattern("IRI")
				.pattern("RBR")
				.pattern("IRI")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('B', Items.BOOK)
				.unlockedBy("has_books", has(Items.BOOK))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SpelledRegistry.SPELL_BOOK.get())
				.requires(Tags.Items.GEMS_LAPIS)
				.requires(Items.BOOK)
				.unlockedBy("has_books", has(Items.BOOK))
				.save(consumer);
	}
}
