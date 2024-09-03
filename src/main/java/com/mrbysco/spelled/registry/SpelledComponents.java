package com.mrbysco.spelled.registry;

import com.mojang.serialization.Codec;
import com.mrbysco.spelled.Reference;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SpelledComponents {
	public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Reference.MOD_ID);

	public static final Supplier<DataComponentType<String>> UNLOCK = DATA_COMPONENT_TYPES.register("unlock", () ->
			DataComponentType.<String>builder()
					.persistent(Codec.STRING)
					.networkSynchronized(ByteBufCodecs.STRING_UTF8)
					.build());
	public static final Supplier<DataComponentType<String>> SPELL = DATA_COMPONENT_TYPES.register("spell", () ->
			DataComponentType.<String>builder()
					.persistent(Codec.STRING)
					.networkSynchronized(ByteBufCodecs.STRING_UTF8)
					.build());
	public static final Supplier<DataComponentType<Boolean>> SEALED = DATA_COMPONENT_TYPES.register("sealed", () ->
			DataComponentType.<Boolean>builder()
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
					.build());
}
