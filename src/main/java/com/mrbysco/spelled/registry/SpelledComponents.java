package com.mrbysco.spelled.registry;

import com.mojang.serialization.Codec;
import com.mrbysco.spelled.Reference;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SpelledComponents {
	public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Reference.MOD_ID);

	public static final Supplier<DataComponentType<String>> UNLOCK = DATA_COMPONENT_TYPES.registerComponentType("unlock", builder ->
			builder
					.persistent(Codec.STRING)
					.networkSynchronized(ByteBufCodecs.STRING_UTF8)
	);
	public static final Supplier<DataComponentType<String>> SPELL = DATA_COMPONENT_TYPES.registerComponentType("spell", builder ->
			builder
					.persistent(Codec.STRING)
					.networkSynchronized(ByteBufCodecs.STRING_UTF8)
	);
	public static final Supplier<DataComponentType<Boolean>> SEALED = DATA_COMPONENT_TYPES.registerComponentType("sealed", builder ->
			builder
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
	);
}
