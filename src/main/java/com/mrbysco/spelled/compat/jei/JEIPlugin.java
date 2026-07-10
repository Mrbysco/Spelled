package com.mrbysco.spelled.compat.jei;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.registry.SpelledComponents;
import com.mrbysco.spelled.registry.SpelledRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
	private static final Identifier UID = Reference.modLoc("jei_plugin");

	@Override
	public Identifier getPluginUid() {
		return UID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, SpelledRegistry.KNOWLEDGE_TOME.get(), new TomeSubTypes());
	}

	private static class TomeSubTypes implements ISubtypeInterpreter<ItemStack> {
		@Override
		@Nullable
		public Object getSubtypeData(ItemStack ingredient, UidContext context) {
			return ingredient.get(SpelledComponents.UNLOCK);
		}

	}
}
