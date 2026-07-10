package com.mrbysco.spelled.registry;

import com.mrbysco.spelled.Reference;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

public class SpelledSerializers {
	public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZER = DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, Reference.MOD_ID);

	public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<List<String>>> STRING_LIST = ENTITY_DATA_SERIALIZER.register("string_list", () ->
			EntityDataSerializer.forValueType(ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list())));
}
