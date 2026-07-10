package com.mrbysco.spelled.generator.assets;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;

public class SpelledModelProvider extends ModelProvider {
	public SpelledModelProvider(PackOutput output) {
		super(output, Reference.MOD_ID);
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		itemModels.generateFlatItem(SpelledRegistry.CREATIVE_TOME.get(), ModelTemplates.FLAT_ITEM);
		itemModels.generateFlatItem(SpelledRegistry.KNOWLEDGE_TOME.get(), ModelTemplates.FLAT_ITEM);
		itemModels.generateFlatItem(SpelledRegistry.SPELL_BOOK.get(), ModelTemplates.FLAT_ITEM);

		blockModels.blockStateOutput.accept(
				BlockModelGenerators.createSimpleBlock(
						SpelledRegistry.LEVELING_ALTAR.get(),
						BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(SpelledRegistry.LEVELING_ALTAR.get()))
				).with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING)
		);
	}
}
