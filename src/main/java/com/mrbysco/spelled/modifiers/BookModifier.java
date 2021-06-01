package com.mrbysco.spelled.modifiers;

import com.google.gson.JsonObject;
import com.mrbysco.spelled.Reference;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import javax.annotation.Nonnull;
import java.util.List;

@EventBusSubscriber(modid = Reference.MOD_ID, bus = Bus.MOD)
public class BookModifier {
	@SubscribeEvent
	public static void registerModifiers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
		event.getRegistry().register(
				new BookDropSerializer().setRegistryName(Reference.MOD_ID, "remove_book")
		);
	}

	public static class BookDropSerializer extends GlobalLootModifierSerializer<BookDropModifier> {
		@Override
		public BookDropModifier read(ResourceLocation location, JsonObject jsonObject, ILootCondition[] lootConditions) {
			return new BookDropModifier(lootConditions);
		}

		@Override
		public JsonObject write(BookDropModifier instance) {
			return new JsonObject();
		}
	}

	private static class BookDropModifier extends LootModifier {
		protected BookDropModifier(ILootCondition[] lootConditions) {
			super(lootConditions);
		}

		@Nonnull
		@Override
		protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
			generatedLoot.clear();

			return generatedLoot;
		}
	}
}
