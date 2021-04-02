package com.mrbysco.spelled.registry;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.block.LevelingAltarBlock;
import com.mrbysco.spelled.container.AltarContainer;
import com.mrbysco.spelled.entity.SpellEntity;
import com.mrbysco.spelled.item.TomeItem;
import com.mrbysco.spelled.tile.LevelingAltarTile;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SpelledRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);
    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Reference.MOD_ID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Reference.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Reference.MOD_ID);

    public static final RegistryObject<Block> LEVELING_ALTAR = BLOCKS.register("leveling_altar", () ->  new LevelingAltarBlock(
            Block.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Item> LEVELING_ALTAR_ITEM  = ITEMS.register("leveling_altar", () -> new BlockItem(LEVELING_ALTAR.get(), itemBuilder().group(SpelledTab.TAB)));

    public static final RegistryObject<Item> KNOWLEDGE_TOME = ITEMS.register("ancient_knowledge_tome" , () -> new TomeItem(itemBuilder().group(ItemGroup.MISC)));

    public static final RegistryObject<TileEntityType<LevelingAltarTile>> LEVELING_ALTAR_TILE = TILES.register("leveling_altar_tile", () -> TileEntityType.Builder.create(() -> new LevelingAltarTile(), LEVELING_ALTAR.get()).build(null));
    public static final RegistryObject<ContainerType<AltarContainer>> ALTAR_CONTAINER = CONTAINERS.register("leveling_altar", () -> IForgeContainerType.create((windowId, inv, data) -> new AltarContainer(windowId, inv)));

    public static final RegistryObject<EntityType<SpellEntity>> SPELL = ENTITIES.register("spell", () ->
            register("spell", EntityType.Builder.<SpellEntity>create(SpellEntity::new, EntityClassification.MISC)
                    .size(0.25F, 0.25F).trackingRange(4).updateInterval(20).setCustomClientFactory(SpellEntity::new)));

    public static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
        return builder.build(id);
    }

    private static Item.Properties itemBuilder() {
        return new Item.Properties();
    }
}
