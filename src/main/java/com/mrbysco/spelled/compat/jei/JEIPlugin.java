package com.mrbysco.spelled.compat.jei;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.registry.SpelledRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    private static final ResourceLocation UID = new ResourceLocation(Reference.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(SpelledRegistry.KNOWLEDGE_TOME.get(), new tomeSubTypes());
    }

    private class tomeSubTypes implements ISubtypeInterpreter {
        @Override
        public String apply(ItemStack stack) {
            if (!stack.hasTag()) return ISubtypeInterpreter.NONE;
                String tomeUnlock = stack.getTag().getString(Reference.tomeUnlock);
            if (tomeUnlock.isEmpty()) return ISubtypeInterpreter.NONE;
                return stack.getItem().getRegistryName() + "@" + tomeUnlock;
        }
    }
}
