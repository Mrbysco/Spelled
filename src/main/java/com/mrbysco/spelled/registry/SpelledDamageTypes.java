package com.mrbysco.spelled.registry;

import com.mrbysco.spelled.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class SpelledDamageTypes {
	public static final ResourceKey<DamageType> MAGIC = register("magic");

	private static ResourceKey<DamageType> register(String name) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Reference.MOD_ID, name));
	}
}
