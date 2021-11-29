package com.mrbysco.spelled.handler;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.keywords.IKeyword;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.config.SpelledConfig;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

public class LootHandler {

    private static final String hasBookTag = Reference.MOD_PREFIX + ":hasBook";

    @SubscribeEvent
    public void firstJoin(PlayerLoggedInEvent event) {
        Player player = event.getPlayer();

        if(!player.level.isClientSide && SpelledConfig.COMMON.startWithBook.get()) {
            CompoundTag playerData = player.getPersistentData();

            if(!playerData.getBoolean(hasBookTag)) {
                Item guideBook = ForgeRegistries.ITEMS.getValue(new ResourceLocation("patchouli", "guide_book"));
                if(guideBook != null) {
                    ItemStack guideStack = new ItemStack(guideBook);
                    CompoundTag tag = new CompoundTag();
                    tag.putString("patchouli:book", "spelled:knowledge_tome");
                    guideStack.setTag(tag);
                    player.getInventory().add(guideStack);
                    playerData.putBoolean(hasBookTag, true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event) {
        String prefix = "minecraft:chests/";
        String name = event.getName().toString();

        if (name.startsWith(prefix)) {
            String file = name.substring(name.indexOf(prefix) + prefix.length());
            switch (file) {
                case "stronghold_library", "jungle_temple", "underwater_ruin_big", "end_city_treasure",
                        "buried_treasure", "woodland_mansion", "bastion_treasure", "village_cartographer" -> event.getTable().addPool(getInjectPool());
                default -> {
                }
            }
        }
    }

    public static LootPool getInjectPool() {
        LootPool.Builder builder = LootPool.lootPool();
        KeywordRegistry registry = KeywordRegistry.instance();
        if(registry.getAdjectives().isEmpty()) {
            registry.initializeKeywords();
        }
        for(String adjective : registry.getAdjectives()) {
            builder.add(injectTome(adjective));
        }
        builder.add(EmptyLootItem.emptyItem().setWeight(1));

       builder.setBonusRolls(UniformGenerator.between(0, 1))
               .name("spelled_inject");

        return builder.build();
    }

    private static LootPoolEntryContainer.Builder injectTome(String adjective) {
        ItemStack stack = new ItemStack(SpelledRegistry.KNOWLEDGE_TOME.get(), 1);
        CompoundTag tag = new CompoundTag();
        tag.putString(Reference.tomeUnlock, adjective);
        stack.setTag(tag);
        LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(SpelledRegistry.KNOWLEDGE_TOME.get())
                .apply(SetNbtFunction.setTag(tag))
                .when(LootItemRandomChanceCondition.randomChance(0.2F))
                .setWeight(1);

        return entry;
    }

    @SubscribeEvent
    public void onWandererTradesEvent(WandererTradesEvent event) {
        KeywordRegistry registry = KeywordRegistry.instance();
        if(registry.getAdjectives().isEmpty()) {
            registry.initializeKeywords();
        }
        ItemStack stack = new ItemStack(SpelledRegistry.KNOWLEDGE_TOME.get());
        for(String adjective : registry.getAdjectives()) {
            IKeyword keyword = registry.getKeywordFromName(adjective);
            if(keyword != null) {
                CompoundTag tag = new CompoundTag();
                tag.putString(Reference.tomeUnlock, adjective);
                event.getRareTrades().add(new ItemsForEmeraldsTrade(stack, keyword.getLevel() + 2, 1, tag, 1, keyword.getLevel()));
            }
        }
    }

    public static class ItemsForEmeraldsTrade implements ItemListing {
        private final ItemStack outputStack;
        private final int outputAmount;
        private final CompoundTag outputTag;
        private final int priceAmount;
        private final int maxUses;
        private final int givenExp;
        private final float priceMultiplier;

        public ItemsForEmeraldsTrade(ItemStack outputStack, int priceAmount, int outputAmount, CompoundTag outputTag, int maxUses, int givenExp) {
            this(outputStack, priceAmount, outputAmount, outputTag, maxUses, givenExp, 0.05F);
        }

        public ItemsForEmeraldsTrade(ItemStack outputStack, int priceAmount, int outputAmount, CompoundTag outputTag, int maxUses, int givenExp, float priceMultiplier) {
            this.priceAmount = priceAmount;
            this.outputStack = outputStack;
            this.outputAmount = outputAmount;
            this.outputTag = outputTag;
            this.maxUses = maxUses;
            this.givenExp = givenExp;
            this.priceMultiplier = priceMultiplier;
        }

        public MerchantOffer getOffer(Entity trader, Random rand) {
            ItemStack stack = new ItemStack(this.outputStack.getItem(), this.outputAmount);
            stack.setTag(this.outputTag);
            return new MerchantOffer(new ItemStack(Items.EMERALD, this.priceAmount), stack, this.maxUses, this.givenExp, this.priceMultiplier);
        }
    }
}
