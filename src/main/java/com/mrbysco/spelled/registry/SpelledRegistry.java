package com.mrbysco.spelled.registry;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.block.LevelingAltarBlock;
import com.mrbysco.spelled.container.AltarContainer;
import com.mrbysco.spelled.entity.SpellEntity;
import com.mrbysco.spelled.item.CreativeTomeItem;
import com.mrbysco.spelled.item.SpellbookItem;
import com.mrbysco.spelled.item.TomeItem;
import com.mrbysco.spelled.tile.LevelingAltarTile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SpelledRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Reference.MOD_ID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Reference.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Reference.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Reference.MOD_ID);

    public static final RegistryObject<SoundEvent> SHOOT_SPELL = SOUND_EVENTS.register("shoot.spell", () ->
            new SoundEvent(new ResourceLocation(Reference.MOD_ID, "shoot.spell")));

    public static final RegistryObject<Block> LEVELING_ALTAR = BLOCKS.register("leveling_altar", () ->  new LevelingAltarBlock(
            Block.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Item> LEVELING_ALTAR_ITEM  = ITEMS.register("leveling_altar", () -> new BlockItem(LEVELING_ALTAR.get(), itemBuilder().tab(SpelledTab.TAB)));

    public static final RegistryObject<Item> KNOWLEDGE_TOME = ITEMS.register("ancient_knowledge_tome" , () -> new TomeItem(itemBuilder().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> CREATIVE_TOME = ITEMS.register("creative_tome" , () -> new CreativeTomeItem(itemBuilder().tab(SpelledTab.TAB)));
    public static final RegistryObject<Item> SPELL_BOOK = ITEMS.register("spell_book" , () -> new SpellbookItem(itemBuilder().stacksTo(1).tab(SpelledTab.TAB)));

    public static final RegistryObject<BlockEntityType<LevelingAltarTile>> LEVELING_ALTAR_TILE = BLOCK_ENTITIES.register("leveling_altar_tile", () -> BlockEntityType.Builder.of(LevelingAltarTile::new, LEVELING_ALTAR.get()).build(null));
    public static final RegistryObject<MenuType<AltarContainer>> ALTAR_CONTAINER = CONTAINERS.register("leveling_altar", () -> IForgeMenuType.create((windowId, inv, data) -> new AltarContainer(windowId, inv)));

    public static final RegistryObject<EntityType<SpellEntity>> SPELL = ENTITIES.register("spell", () ->
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
}
