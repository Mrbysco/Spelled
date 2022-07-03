package com.mrbysco.spelled.compat.jei;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.registry.SpelledRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
	private static final ResourceLocation UID = new ResourceLocation(Reference.MOD_ID, "jei_plugin");

	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, SpelledRegistry.KNOWLEDGE_TOME.get(), new TomeSubTypes());
	}

	private static class TomeSubTypes implements IIngredientSubtypeInterpreter<ItemStack> {
		@Override
		public String apply(ItemStack stack, UidContext context) {
			if (!stack.hasTag()) return IIngredientSubtypeInterpreter.NONE;
			String tomeUnlock = stack.getTag().getString(Reference.tomeUnlock);
			if (tomeUnlock.isEmpty()) return IIngredientSubtypeInterpreter.NONE;
			return ForgeRegistries.ITEMS.getKey(stack.getItem()) + "@" + tomeUnlock;
		}
	}
}
