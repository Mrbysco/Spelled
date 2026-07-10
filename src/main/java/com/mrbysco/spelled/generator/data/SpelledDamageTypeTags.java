package com.mrbysco.spelled.generator.data;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.registry.SpelledDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;

import java.util.concurrent.CompletableFuture;

public class SpelledDamageTypeTags extends DamageTypeTagsProvider {
	public SpelledDamageTypeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, Reference.MOD_ID);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		this.tag(DamageTypeTags.ALWAYS_TRIGGERS_SILVERFISH).add(SpelledDamageTypes.MAGIC);
		this.tag(DamageTypeTags.AVOIDS_GUARDIAN_THORNS).add(SpelledDamageTypes.MAGIC);
		this.tag(DamageTypeTags.BYPASSES_ARMOR).add(SpelledDamageTypes.MAGIC);
		this.tag(DamageTypeTags.IS_PROJECTILE).add(SpelledDamageTypes.MAGIC);
	}
}
