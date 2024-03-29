package com.mrbysco.spelled.registry;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.block.LevelingAltarBlock;
import com.mrbysco.spelled.blockentity.LevelingAltarBlockEntity;
import com.mrbysco.spelled.entity.SpellEntity;
import com.mrbysco.spelled.item.CreativeTomeItem;
import com.mrbysco.spelled.item.SpellbookItem;
import com.mrbysco.spelled.item.TomeItem;
import com.mrbysco.spelled.menu.AltarMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class SpelledRegistry {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Reference.MOD_ID);
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Reference.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Reference.MOD_ID);
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Reference.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Reference.MOD_ID);

	public static final RegistryObject<SoundEvent> SHOOT_SPELL = SOUND_EVENTS.register("shoot.spell", () ->
			SoundEvent.createVariableRangeEvent(new ResourceLocation(Reference.MOD_ID, "shoot.spell")));

	public static final RegistryObject<Block> LEVELING_ALTAR = BLOCKS.register("leveling_altar", () -> new LevelingAltarBlock(
			Block.Properties.of().strength(2.5F).sound(SoundType.WOOD)));
	public static final RegistryObject<Item> LEVELING_ALTAR_ITEM = ITEMS.register("leveling_altar", () -> new BlockItem(LEVELING_ALTAR.get(), itemBuilder()));

	public static final RegistryObject<Item> KNOWLEDGE_TOME = ITEMS.register("ancient_knowledge_tome", () -> new TomeItem(itemBuilder()));
	public static final RegistryObject<Item> CREATIVE_TOME = ITEMS.register("creative_tome", () -> new CreativeTomeItem(itemBuilder()));
	public static final RegistryObject<Item> SPELL_BOOK = ITEMS.register("spell_book", () -> new SpellbookItem(itemBuilder().stacksTo(1)));

	public static final RegistryObject<BlockEntityType<LevelingAltarBlockEntity>> LEVELING_ALTAR_TILE = BLOCK_ENTITY_TYPES.register("leveling_altar_tile", () -> BlockEntityType.Builder.of(LevelingAltarBlockEntity::new, LEVELING_ALTAR.get()).build(null));
	public static final RegistryObject<MenuType<AltarMenu>> ALTAR_CONTAINER = MENU_TYPES.register("leveling_altar", () -> IForgeMenuType.create((windowId, inv, data) -> new AltarMenu(windowId, inv)));

	public static final RegistryObject<EntityType<SpellEntity>> SPELL = ENTITY_TYPES.register("spell", () ->
			register("spell", EntityType.Builder.<SpellEntity>of(SpellEntity::new, MobCategory.MISC)
					.sized(0.25F, 0.25F)
					.clientTrackingRange(4).updateInterval(20)
					.setCustomClientFactory(SpellEntity::new)));

	public static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
		return builder.build(id);
	}

	private static Item.Properties itemBuilder() {
		return new Item.Properties();
	}

	public static final RegistryObject<CreativeModeTab> SPELLED_TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
			.icon(() -> new ItemStack(SpelledRegistry.LEVELING_ALTAR_ITEM.get()))
			.title(Component.translatable("itemGroup.spelled.tab"))
			.displayItems((displayParameters, output) -> {
				List<ItemStack> stacks = SpelledRegistry.ITEMS.getEntries().stream().map(reg -> new ItemStack(reg.get())).toList();
				output.acceptAll(stacks);

				for (String adjective : KeywordRegistry.instance().getAdjectives()) {
					CompoundTag nbt = new CompoundTag();
					nbt.putString(Reference.tomeUnlock, adjective);
					ItemStack stack = new ItemStack(SpelledRegistry.KNOWLEDGE_TOME.get(), 1, nbt);
					stack.setTag(nbt);
					output.accept(stack);
				}
			}).build());
}
