package com.mrbysco.spelled.registry;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class SpelledTab {
	private static CreativeModeTab TAB;

	@SubscribeEvent
	public void registerCreativeTabs(final CreativeModeTabEvent.Register event) {
		TAB = event.registerCreativeModeTab(new ResourceLocation(Reference.MOD_ID, "tab"), builder ->
				builder.icon(() -> new ItemStack(SpelledRegistry.LEVELING_ALTAR_ITEM.get()))
						.title(Component.translatable("itemGroup.spelled.tab"))
						.displayItems((features, output, hasPermissions) -> {
							List<ItemStack> stacks = SpelledRegistry.ITEMS.getEntries().stream().map(reg -> new ItemStack(reg.get())).toList();
							output.acceptAll(stacks);

							for (String adjective : KeywordRegistry.instance().getAdjectives()) {
								CompoundTag nbt = new CompoundTag();
								nbt.putString(Reference.tomeUnlock, adjective);
								ItemStack stack = new ItemStack(SpelledRegistry.KNOWLEDGE_TOME.get(), 1, nbt);
								stack.setTag(nbt);
								output.accept(stack);
							}
						}));
	}
}
