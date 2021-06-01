package com.mrbysco.spelled.modifiers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.config.SpelledConfig;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class BookDisabledCondition implements ILootCondition {
	private static LootConditionType REMOVE_BOOK = register(new ResourceLocation(Reference.MOD_ID, "book_disabled"), BookDisabledCondition.Serializer.INSTANCE);

	private static LootConditionType register(ResourceLocation id, ILootSerializer<? extends ILootCondition> serializer) {
		return Registry.register(Registry.LOOT_CONDITION_TYPE, id, new LootConditionType(serializer));
	}

	public static void register() {
	}

	@Override
	public LootConditionType getConditionType() {
		return REMOVE_BOOK;
	}

	@Override
	public boolean test(LootContext lootContext) {
		return !SpelledConfig.COMMON.startWithBook.get();
	}

	public static class Serializer implements ILootSerializer<BookDisabledCondition> {
		public static final BookDisabledCondition.Serializer INSTANCE = new BookDisabledCondition.Serializer();

		@Override
		public void serialize(JsonObject p_230424_1_, BookDisabledCondition p_230424_2_, JsonSerializationContext p_230424_3_) {

		}

		@Override
		public BookDisabledCondition deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
			return new BookDisabledCondition();
		}
	}
}
