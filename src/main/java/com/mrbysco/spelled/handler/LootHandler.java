package com.mrbysco.spelled.handler;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.api.keywords.IKeyword;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.config.SpelledConfig;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerTrades.ITrade;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.loot.EmptyLootEntry;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.conditions.RandomChance;
import net.minecraft.loot.functions.SetNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
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
        PlayerEntity player = event.getPlayer();

        if(!player.level.isClientSide && SpelledConfig.COMMON.startWithBook.get()) {
            CompoundNBT playerData = player.getPersistentData();

            if(!playerData.getBoolean(hasBookTag)) {
                Item guideBook = ForgeRegistries.ITEMS.getValue(new ResourceLocation("patchouli", "guide_book"));
                if(guideBook != null) {
                    ItemStack guideStack = new ItemStack(guideBook);
                    CompoundNBT tag = new CompoundNBT();
                    tag.putString("patchouli:book", "spelled:knowledge_tome");
                    guideStack.setTag(tag);
                    player.inventory.add(guideStack);
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
                case "stronghold_library":
                case "jungle_temple":
                case "underwater_ruin_big":
                case "end_city_treasure":
                case "buried_treasure":
                case "woodland_mansion":
                case "bastion_treasure":
                case "village_cartographer":
                    event.getTable().addPool(getInjectPool());
                    break;
                default:
                    break;
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
        builder.add(EmptyLootEntry.emptyItem().setWeight(1));

       builder.bonusRolls(0, 1)
               .name("spelled_inject");

        return builder.build();
    }

    private static LootEntry.Builder injectTome(String adjective) {
        ItemStack stack = new ItemStack(SpelledRegistry.KNOWLEDGE_TOME.get(), 1);
        CompoundNBT tag = new CompoundNBT();
        tag.putString(Reference.tomeUnlock, adjective);
        stack.setTag(tag);
        LootEntry.Builder<?> entry = ItemLootEntry.lootTableItem(SpelledRegistry.KNOWLEDGE_TOME.get())
                .apply(SetNBT.setTag(tag))
                .when(RandomChance.randomChance(0.2F))
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
                CompoundNBT tag = new CompoundNBT();
                tag.putString(Reference.tomeUnlock, adjective);
                event.getRareTrades().add(new ItemsForEmeraldsTrade(stack, keyword.getLevel() + 2, 1, tag, 1, keyword.getLevel()));
            }
        }
    }

    public static class ItemsForEmeraldsTrade implements ITrade {
        private final ItemStack outputStack;
        private final int outputAmount;
        private final CompoundNBT outputTag;
        private final int priceAmount;
        private final int maxUses;
        private final int givenExp;
        private final float priceMultiplier;

        public ItemsForEmeraldsTrade(ItemStack outputStack, int priceAmount, int outputAmount, CompoundNBT outputTag, int maxUses, int givenExp) {
            this(outputStack, priceAmount, outputAmount, outputTag, maxUses, givenExp, 0.05F);
        }

        public ItemsForEmeraldsTrade(ItemStack outputStack, int priceAmount, int outputAmount, CompoundNBT outputTag, int maxUses, int givenExp, float priceMultiplier) {
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
