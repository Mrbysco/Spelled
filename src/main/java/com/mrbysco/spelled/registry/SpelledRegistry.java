package com.mrbysco.spelled.registry;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.entity.SpellEntity;
import com.mrbysco.spelled.item.TomeItem;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SpelledRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Reference.MOD_ID);

    public static final RegistryObject<Item> KNOWLEDGE_TOME = ITEMS.register("knowledge_tome" , () -> new TomeItem(itemBuilder().group(ItemGroup.MISC)));

    public static final RegistryObject<EntityType<SpellEntity>> BALL_SPELL = ENTITIES.register("ball_spell", () ->
            register("transform_spell", EntityType.Builder.<SpellEntity>create(SpellEntity::new, EntityClassification.MISC)
                    .size(0.25F, 0.25F).trackingRange(4).func_233608_b_(10)));

    public static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
        return builder.build(id);
    }

    public static void entityAttributes() {
//        GlobalEntityTypeAttributes.put(LIVING_BLOCK.get(), LivingBlockEntity.registerAttributes().create());
    }

    private static Item.Properties itemBuilder() {
        return new Item.Properties();
    }
}
