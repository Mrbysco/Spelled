package com.mrbysco.spelled.generator.data;

import com.mrbysco.spelled.registry.SpelledDamageTypes;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageType;

public class SpelledDamageTypeProvider {
	public static void bootstrap(BootstrapContext<DamageType> context) {
		context.register(SpelledDamageTypes.MAGIC, new DamageType("spelled.magic", 0.1F));
	}
}
