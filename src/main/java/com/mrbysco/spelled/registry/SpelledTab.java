package com.mrbysco.spelled.registry;

import com.mrbysco.spelled.Reference;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SpelledTab {
    public static final ItemGroup TAB = new ItemGroup(Reference.MOD_ID) {
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(SpelledRegistry.LEVELING_ALTAR.get());
        }

        @Override
        public void fill(NonNullList<ItemStack> items) {
            super.fill(items);
            for(String adjective : KeywordRegistry.instance().getAdjectives()) {
                CompoundNBT nbt = new CompoundNBT();
                nbt.putString(Reference.tomeUnlock, adjective);
                ItemStack stack = new ItemStack(SpelledRegistry.KNOWLEDGE_TOME.get(), 1, nbt);
                stack.setTag(nbt);
                items.add(stack);
            }
        }
    };
}
