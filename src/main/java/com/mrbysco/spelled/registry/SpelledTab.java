package com.mrbysco.spelled.registry;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SpelledTab {
    public static final CreativeModeTab TAB = new CreativeModeTab(Reference.MOD_ID) {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(SpelledRegistry.LEVELING_ALTAR.get());
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> items) {
            super.fillItemList(items);
            for(String adjective : KeywordRegistry.instance().getAdjectives()) {
                CompoundTag nbt = new CompoundTag();
                nbt.putString(Reference.tomeUnlock, adjective);
                ItemStack stack = new ItemStack(SpelledRegistry.KNOWLEDGE_TOME.get(), 1, nbt);
                stack.setTag(nbt);
                items.add(stack);
            }
        }
    };
}
