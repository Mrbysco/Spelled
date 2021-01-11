package com.mrbysco.spelled.generator;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpelledDataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        if (event.includeServer()) {
            generator.addProvider(new Loots(generator));
            generator.addProvider(new SpelledAdvancements(generator));
        }
        if (event.includeClient()) {
            generator.addProvider(new Language(generator));
            /*
            generator.addProvider(new BlockStates(generator, helper));
            generator.addProvider(new ItemModels(generator, helper));
            */
        }
    }

    private static class Loots extends LootTableProvider {
        public Loots(DataGenerator gen) {
            super(gen);
        }

        @Override
        protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootParameterSet>> getTables() {
            return ImmutableList.of(
                    Pair.of(Blocks::new, LootParameterSets.BLOCK)
            );
        }

        @Override
        protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
            map.forEach((name, table) -> LootTableManager.validateLootTable(validationtracker, name, table));
        }

        private class Blocks extends BlockLootTables {
            @Override
            protected void addTables() {
                this.registerDropSelfLootTable(SpelledRegistry.LEVELING_ALTAR.get());
            }

            @Override
            protected Iterable<Block> getKnownBlocks() {
                return (Iterable<Block>)SpelledRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
            }
        }
    }

    private static class Language extends LanguageProvider {
        public Language(DataGenerator gen) {
            super(gen, Reference.MOD_ID, "en_us");
        }

        @Override
        protected void addTranslations() {
            add("itemGroup.spelled", "Spelled");

            add("key.spelled.category", "Spelled");
            add("key.spelled.use", "Use Spell");

            //Container
            add("spelled:container.altar", "Leveling Altar");
            add("spelled:container.altar.level.one", "1 Experience Level");
            add("spelled:container.altar.level.many", "%s Experience Levels");
            add("spelled:container.altar.level.requirement", "Level requirement: %s");
            add("spelled:container.altar.item.requirement", "Item requirement: %s %s");
            add("spelled:container.altar.item", "%s %s");

            add("spelled.level_up.fail_item", "You lack the required items to level up!");
            add("spelled.level_up.fail_xp", "You lack the required xp to level up!");

            addBlock(SpelledRegistry.LEVELING_ALTAR, "Leveling Altar");
            addItem(SpelledRegistry.KNOWLEDGE_TOME, "Tome Of Ancient Knowledge");

            //Tome
            add("spelled.tome.description", "Contains knowledge about \"%s\"");
            add("spelled.tome.fail", "You already know the knowledge of the tome");
            add("spelled.tome.success", "You've obtained the knowledge the tome");

            add("spelled.spell.cast", "* %s has cast \"%s\"");

            //Commands
            add("spelled.commands.level.get.message", "%s is currently level %s");
            add("spelled.commands.level.set.message", "%s's level has been set to %s");
            add("spelled.commands.level.set.invalid", "Couldn't set level. Invalid number supplied: %s");
            add("spelled.commands.knowledge.unlock.message", "Unlocked word \"%s\" for %s");
            add("spelled.commands.knowledge.unlock.invalid", "Couldn't unlock word. Invalid word supplied: %s");
            add("spelled.commands.knowledge.lock.message", "Locked word \"%s\" for %s");
            add("spelled.commands.knowledge.lock.invalid", "Couldn't locked word. Invalid word supplied: %s");
            add("spelled.commands.knowledge.reset.message", "Reset knowledge of %s");

            //Keyword Descriptions
            add("spelled.keyword.ater.description", "Black");
            add("spelled.keyword.aureus.description", "Golden");
            add("spelled.keyword.caeruleus.description", "Blue");
            add("spelled.keyword.viridis.description", "Green");
            add("spelled.keyword.aqua.description", "Aqua");
            add("spelled.keyword.rubrum.description", "Red");
            add("spelled.keyword.roseus.description", "Pink");
            add("spelled.keyword.flavus.description", "Yellow");
            add("spelled.keyword.albus.description", "White");

            add("spelled.keyword.parvus.description", "Small");
            add("spelled.keyword.magnum.description", "Big");
            add("spelled.keyword.grandis.description", "Large");
            add("spelled.keyword.immanis.description", "Monstrous");

            add("spelled.keyword.liquidus.description", "Liquid");
            add("spelled.keyword.nix.description", "Snow");
            add("spelled.keyword.frigus.description", "Cold");
            add("spelled.keyword.dissiliunt.description", "Exploding");
            add("spelled.keyword.sanitatem.description", "Healing");
            add("spelled.keyword.nocere.description", "Hurting");
            add("spelled.keyword.praesidium.description", "Protecting");
            add("spelled.keyword.fractionis.description", "Breaking");
            add("spelled.keyword.propellentibus.description", "Pushing");
            add("spelled.keyword.ignis.description", "Fiery");

            add("spelled.keyword.sphaera.description", "Ball");
            add("spelled.keyword.projectilis.description", "Projectile");

            add("spelled.keyword.sui.description", "Self");
            add("spelled.keyword.sese.description", "Self");

            //Advancements
            add("advancement.spelled.root", "Spelled");
            add("advancement.spelled.root.desc", "Magic but using latin words");

            add("advancement.spelled.ater", "Ater");
            add("advancement.spelled.ater.desc", "Black");
            add("advancement.spelled.aureus", "Aureus");
            add("advancement.spelled.aureus.desc", "Golden");
            add("advancement.spelled.caeruleus", "Caeruleus");
            add("advancement.spelled.caeruleus.desc", "Blue");
            add("advancement.spelled.viridis", "Viridis");
            add("advancement.spelled.viridis.desc", "Green");
            add("advancement.spelled.aqua", "Aqua");
            add("advancement.spelled.aqua.desc", "Aqua");
            add("advancement.spelled.rubrum", "Rubrum");
            add("advancement.spelled.rubrum.desc", "Red");
            add("advancement.spelled.roseus", "Roseus");
            add("advancement.spelled.roseus.desc", "Pink");
            add("advancement.spelled.flavus", "Flavus");
            add("advancement.spelled.flavus.desc", "Yellow");
            add("advancement.spelled.albus", "Albus");
            add("advancement.spelled.albus.desc", "White");

            add("advancement.spelled.parvus", "Parvus");
            add("advancement.spelled.parvus.desc", "Small");
            add("advancement.spelled.magnum", "Magnum");
            add("advancement.spelled.magnum.desc", "Big");
            add("advancement.spelled.grandis", "Grandis");
            add("advancement.spelled.grandis.desc", "Large");
            add("advancement.spelled.immanis", "Immanis");
            add("advancement.spelled.immanis.desc", "Monstrous");

            add("advancement.spelled.liquidus", "Liquidus");
            add("advancement.spelled.liquidus.desc", "Liquid");
            add("advancement.spelled.nix", "Nix");
            add("advancement.spelled.nix.desc", "Snow");
            add("advancement.spelled.frigus", "Frigus");
            add("advancement.spelled.frigus.desc", "Cold");
            add("advancement.spelled.dissiliunt", "Dissiliunt");
            add("advancement.spelled.dissiliunt.desc", "Exploding");
            add("advancement.spelled.sanitatem", "Sanitatem");
            add("advancement.spelled.sanitatem.desc", "Healing");
            add("advancement.spelled.nocere", "Nocere");
            add("advancement.spelled.nocere.desc", "Hurting");
            add("advancement.spelled.praesidium", "Praesidium");
            add("advancement.spelled.praesidium.desc", "Protecting");
            add("advancement.spelled.fractionis", "Fractionis");
            add("advancement.spelled.fractionis.desc", "Breaking");
            add("advancement.spelled.propellentibus", "Propellentibus");
            add("advancement.spelled.propellentibus.desc", "Pushing");
            add("advancement.spelled.ignis", "Ignis");
            add("advancement.spelled.ignis.desc", "Fiery");
        }
    }
}