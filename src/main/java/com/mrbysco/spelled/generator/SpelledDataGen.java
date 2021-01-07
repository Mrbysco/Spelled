package com.mrbysco.spelled.generator;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpelledDataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        /*ExistingFileHelper helper = event.getExistingFileHelper();

        if (event.includeServer()) {
            generator.addProvider(new Loots(generator));
        }
        */
        if (event.includeClient()) {
            generator.addProvider(new Language(generator));
            /*
            generator.addProvider(new BlockStates(generator, helper));
            generator.addProvider(new ItemModels(generator, helper));
            */
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

            add("spelled.level_up.fail_item", "You lack the required items to level up!");
            add("spelled.level_up.fail_xp", "You lack the required xp to level up!");

            addItem(SpelledRegistry.KNOWLEDGE_TOME, "Tome Of Knowledge");

            add("spelled.tome.description", "Contains knowledge of '%s'");
            add("spelled.tome.fail", "You already know the knowledge of the tome");
            add("spelled.tome.success", "You've obtained the knowledge the tome");

            add("spelled.keyword.ater.description", "Black");
            add("spelled.keyword.aureus.description", "Golden");
            add("spelled.keyword.caeruleus.description", "Blue");
            add("spelled.keyword.viridis.description", "Green");
            add("spelled.keyword.aqua.description", "Aqua");
            add("spelled.keyword.rubrum.description", "Red");
            add("spelled.keyword.roseus.description", "Pink");
            add("spelled.keyword.albus.description", "White");

            add("spelled.keyword.parvus.description", "Small");
            add("spelled.keyword.magnum.description", "Big");
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
        }
    }
}