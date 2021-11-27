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
            generator.addProvider(new SpelledPatchouliProvider(generator));
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
            map.forEach((name, table) -> LootTableManager.validate(validationtracker, name, table));
        }

        private static class Blocks extends BlockLootTables {
            @Override
            protected void addTables() {
                this.dropSelf(SpelledRegistry.LEVELING_ALTAR.get());
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
            add("spelled.tome.description.invalid", "Tome invalid! Right click to fix");
            add("spelled.tome.fail", "You already know the knowledge of the tome");
            add("spelled.tome.success", "You've obtained the knowledge the tome");

            add("spelled.spell.cast", "* %s has cast \"%s\"");

            //Commands
            add("spelled.commands.level.get.message", "%s is currently level %s");
            add("spelled.commands.level.set.message", "%s's level has been set to %s");
            add("spelled.commands.level.set.invalid", "Couldn't set level. Invalid number supplied: %s");
            add("spelled.commands.knowledge.unlock.message", "Unlocked word \"%s\" for %s");
            add("spelled.commands.knowledge.unlock.all", "Unlocked all word(s) for %s");
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

            add("advancement.spelled.color_lore", "Color Lore");
            add("advancement.spelled.color_lore.desc", "You've unlocked the lore behind colors");
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

            //Patchouli
            add("item.spelled.book.name", "Tome Of Knowledge");
            add("info.spelled.book.subtitle", "Magic in latin words");
            add("info.spelled.book.landing", "“The pen is mightier than the sword” $(br)Usually a myth, but now reality! You can change the world using mere words. $(br)Be careful, with power, comes responsibility!");

            add("info.spelled.book.leveling.name", "Leveling");
            add("info.spelled.book.leveling.desc", "In order to make use of the magical words you've unlocked you first need to level yourself using the $(item)Leveling Altar$()");
            add("info.spelled.book.leveling.entry.name", "Leveling Altar");
            add("info.spelled.book.leveling_recipe.text", "You craft the $(item)Leveling Altar$() as following");
            add("info.spelled.book.leveling_info.text", "With the $(item)Leveling Altar$() you can level yourself using Experience and Items or just Experience depending on how it's configured.");

            add("info.spelled.book.types.name", "Types");
            add("info.spelled.book.types.desc", "There's a few types you can end your spells with");
            add("info.spelled.book.types.self.name", "Self");
            add("info.spelled.book.types.self.text", "Using \"sui\" or \"sese\" you can apply the spell to yourself. You don't use spells on yourself? Why not?! It's time you started doing that. I recommend pairing this with the healing spell for some quick post creeper healing but the possibilities are endless. ");
            add("info.spelled.book.types.self.text2", "Just don't use it with the fireball spell unless you like to smell extra crispy for a week. This spell was originally called touch by the wielder who discovered it, but for reasons lost to time the power was transferred over to this word");
            add("info.spelled.book.types.ball.name", "Ball");
            add("info.spelled.book.types.ball.text", "It's time to play ball, and I don't mean basketball! Or football! Or what you yanks call football, I mean actual spheres! Shaping your spells like a ball makes them easier to handle and aim, even though such a shape is considered heretical.");
            add("info.spelled.book.types.ball.text2", "This word was banned in the original community due to such shapes being considered unnatural and even now it's still a controversial use. That said I can keep a secret if you can");
            add("info.spelled.book.types.projectilis.name", "Projectile");
            add("info.spelled.book.types.projectilis.text", "Humans have always had an affinity for throwing things, and having spells blow up in your face is never fun. Initially many word wielders used to stick to using their fists to actually fight, and using words to defend themselves, however upon the discovery of these words, word combat was changed forever.");
            add("info.spelled.book.types.projectilis.text2", "The origin is not yet known, though a common unproved myth states that the first wielder was inspired when an apple fell on their head, and they accused the guilty three of assaulting them by throwing an apple at them.");

            add("info.spelled.book.colors.name", "Colors");
            add("info.spelled.book.colors.desc", "Tired of your old drab black spells? Well now you don't have to anymore! $(br)Make all your spell fabulous and use a wide array of colors to really make things pop! Add in the correct word during incantation and you can really make your enemies taste the rainbow");
            add("info.spelled.book.colors.ater.name", "Ater");
            add("info.spelled.book.colors.ater.text", "You've learned how to say $(0)\"Black\"$() in Latin");
            add("info.spelled.book.colors.aureus.name", "Aureus");
            add("info.spelled.book.colors.aureus.text", "You've learned how to say $(6)\"Gold\"$() in Latin");
            add("info.spelled.book.colors.caeruleus.name", "Caeruleus");
            add("info.spelled.book.colors.caeruleus.text", "You've learned how to say $(9)\"Blue\"$() in Latin");
            add("info.spelled.book.colors.viridis.name", "Viridis");
            add("info.spelled.book.colors.viridis.text", "You've learned how to say $(a)\"Green\"$() in Latin");
            add("info.spelled.book.colors.aqua.name", "Aqua");
            add("info.spelled.book.colors.aqua.text", "You've learned how to say $(b)\"Aqua\"$() in Latin");
            add("info.spelled.book.colors.rubrum.name", "Rubrum");
            add("info.spelled.book.colors.rubrum.text", "You've learned how to say $(c)\"Red\"$() in Latin");
            add("info.spelled.book.colors.roseus.name", "Roseus");
            add("info.spelled.book.colors.roseus.text", "You've learned how to say $(d)\"Pink\"$() in Latin");
            add("info.spelled.book.colors.flavus.name", "Flavus");
            add("info.spelled.book.colors.flavus.text", "You've learned how to say $(e)\"Yellow\"$() in Latin");
            add("info.spelled.book.colors.albus.name", "Albus");
            add("info.spelled.book.colors.albus.text", "You've learned how to say \"White\" in Latin");
            add("info.spelled.book.colors.lore.name", "Lore");
            add("info.spelled.book.colors.lore.text", "One of the earliest words discovered, being able to paint spells was considered an incredible step forward for the fledgling word combat field. While colors themselves are harmless, everyone knows that adding the color orange to your spells makes them go faster!");
            add("info.spelled.book.colors.lore.text2", "Rumour has it the first color discovered was red, and came as a result of a word wielder cursing to himself having stubbed their toe. Their angry utterance describing the damaged digit turned the wielder red, and soon enough the spell wielding community learnt the fabulousness brought on by rainbow colors!");

            add("info.spelled.book.size.name", "Size");
            add("info.spelled.book.size.desc", "Whoever said size doesn’t matter lied. Size does matter, especially when you trying to throw a fireball at your enemy! Discovered when a shaman was trying cheap out on bonemeal. A mispronunciation meant that the wrong word was said, transforming a normal carrot into a giant one. Experiments lead to the discovery of multiple words to achieve this!");
            add("info.spelled.book.size.parvus.name", "Parvus");
            add("info.spelled.book.size.parvus.text", "What? You actually want *smaller* spells? Fine I guess then this is the word for you! Useful when you're shooting a fireball in a dungeon and don't want to blow you and your allies up. This word is said to have been discovered by a very disgruntled tomcat who was tired of being unable to catch a mouse that lurked in a hole. When mewed the cat is said to have shrunk down and");
            add("info.spelled.book.size.parvus.text2", "then learnt the hard way why mice hide in holes. Due to the cat to person translation this effect doesn't apply to entities, but it's a small price to pay for pea sized spells");
            add("info.spelled.book.size.magnum.name", "Magnum");
            add("info.spelled.book.size.magnum.text", "Make spells great again! This work ensures that we have the best spells, the very best! Don't let those other words fool you, this is the one and only spell word you need! Said to have been found by an orator with a penchant for exaggeration, it seems that lying enough times can actually make something real. Who'd have known?!");
            add("info.spelled.book.size.grandis.name", "Grandis");
            add("info.spelled.book.size.grandis.text", "Go grand or go home I always say and it's about damn time you listen to me for once! I mean seriously does anyone even read these? Bah whatever, if you are listening, this word does what it says on the tin; makes your spells grand! What does that mean? Beats me! Maybe you should have paid more attention when I spoke earlier! Anyway this spell is said to have been found by a");
            add("info.spelled.book.size.grandis.text2", "person known as the greatest show person, whose name became synonymous with the word. Used as part of his introduction it made the light spells he used all the cooler looking");
            add("info.spelled.book.size.immanis.name", "Immanis");
            add("info.spelled.book.size.immanis.text", "Bigger is always better, and it's about time that gets applied to your spells! This word allows you to maximise your spells giving you larger coverage. Thankfully for most people and to the disappointment of creepers everywhere, this work does not work on entities, but who knows, maybe you'll find one that does?");
            add("info.spelled.book.size.immanis.text2", "This word is said to have been discovered by rather lazy word wielder who didn't want to risk missing a target");

            add("info.spelled.book.descriptive.name", "Descriptive");
            add("info.spelled.book.descriptive.desc", "While many of the words remain lost, these category of words can be used to determine specific spell effects.");
            add("info.spelled.book.descriptive.liquidus.name", "Liquidus");
            add("info.spelled.book.descriptive.liquidus.text", "In a world with infinite water playing with water may seem useless, but do not underestimate the powers of fluid control! You can use your newfound power to wash away your enemies while also keeping your spells nice and clean! If you want to live dangerously lava is also a more !FUN! option since everyone knows the risk is what creates !FUN!. This word");
            add("info.spelled.book.descriptive.liquidus.text2", "was discovered by a rather ticked off parent who demanded their child go shower. The child did, but in the process of doing so their bedroom was flooded.");
            add("info.spelled.book.descriptive.nix.name", "Nix");
            add("info.spelled.book.descriptive.nix.text", "Do you wanna make a snowman? No? Well too bad! This word gives you the ability to harness the power of winter itself, and put your enemies on thin ice. Discovered during a particularly harsh winter, the word is said to have originally been part of a catchy song that has thankfully been let go, however the repeated utterances of the song being stuck in a word ");
            add("info.spelled.book.descriptive.nix.text2", "wielders head meant that the word was imbued with the aforementioned powers. Their disgruntled parents opted to use the old “bucket of water” method of waking the wielder up, and in the following angry outburst the wielder accidentally froze their parents. This lead to a frosty relationship between them.");
            add("info.spelled.book.descriptive.frigus.name", "Frigus");
            add("info.spelled.book.descriptive.frigus.text", "“Wait cold? I thought this was part of snow!” I hear you exclaim! Well no. That’s not how magic works! As you’d expect, this words relates more specifically to the freezing aspect of things, and was discovered when a young word wielder found themselves waking up on the wrong side of the word one morning.");
            add("info.spelled.book.descriptive.dissiliunt.name", "Dissiliunt");
            add("info.spelled.book.descriptive.dissiliunt.text", "Cool people don’t look at explosions, and neither will you! While the effect is fairly straightforward, the actual source of this word is heavily disputed. Some scholars argue that it came after a word wielder accidentally lit some explosives in the wrong location, while others say it came when a sentient plant snuck up on a word wielder and nearly killed them.");
            add("info.spelled.book.descriptive.dissiliunt.text2", "Regardless of origin, take extra care when speaking this word!");
            add("info.spelled.book.descriptive.sanitatem.name", "Sanitatem");
            add("info.spelled.book.descriptive.sanitatem.text", "The world can be dangerous, so take this! Using this word can help heal you and your allies, and was a favourite of dedicated healers. Being paired up other spell effects lead to some interesting combinations, however most of these combinations were lost to the sands of time. That said, the word appears to have found its origins with a female word wielder known as ");
            add("info.spelled.book.descriptive.sanitatem.text2", "Caterina. A supposed world renowned healer, it is said at her peak she was healing up to 40 people an hour!");
            add("info.spelled.book.descriptive.nocere.name", "Nocere");
            add("info.spelled.book.descriptive.nocere.text", "Sticks and stones will break my bones but words will never hurt me! Wrong. Often used as an emergency escape tool, very few word wielders chose to use it due to the less than flashy nature of this spell. Still it is not one to be overlooked, and if push comes to shove it could very well save your life! Due to the controversial nature of this word very little remains of its origin,");
            add("info.spelled.book.descriptive.nocere.text2", "with the most commonly accepted theory is that it started as a harmless retort to the aforementioned rhyme.");
            add("info.spelled.book.descriptive.praesidium.name", "Praesidium");
            add("info.spelled.book.descriptive.praesidium.text", "Who needs armour when you have a spell to do it for you? Not a badass word wielder like you! With this word you can forgo that clunky diamond armour and run free as nature intended! Said to have been created when a word wielder screamed to the heavens as they were being swarmed by zombies, this is one spell that you should not forgo,");
            add("info.spelled.book.descriptive.praesidium.text2", "as it has saved even the most careless word wielders.");
            add("info.spelled.book.descriptive.fractionis.name", "Fractionis");
            add("info.spelled.book.descriptive.fractionis.text", "Can’t make an omelette without breaking some eggs, and the same goes for magic. This spell lets you break through walls like a hypothetical automorphic jar filled with flavored water, while still looking awesome! Unusually this spell was not discovered by a word wielder, but by an angry shop keeper. A clumsy client was less than careful in a glass blowers shop,");
            add("info.spelled.book.descriptive.fractionis.text2", "and the repeated destruction of expensive glass products lead the furious shopkeeper to yell out “YOU BREAK IT YOU BUY IT” with such force that the world itself bent to their will, and the clumsy client shattered on the spot. Repeated use on entities has not verified this claim, however the word was still found to be useful on inanimate objects.");
            add("info.spelled.book.descriptive.propellentibus.name", "Propellentibus");
            add("info.spelled.book.descriptive.propellentibus.text", "What happens when an unstoppable force meets an immovable object? Beats me but now you also have access to that unstoppable force! This word lets you shove people aside without even needing to touch them, particularly useful if say the world is experiencing a hundred-year plague. Rumoured to have been created by a haughty upper class nobleman,");
            add("info.spelled.book.descriptive.propellentibus.text2", "this word found prominence with people in a hurry who used it to get rid of others loitering in the middle of the market aisles during peak traffic.");
            add("info.spelled.book.descriptive.ignis.name", "Ignis");
            add("info.spelled.book.descriptive.ignis.text", "Harnessing the sun has long been a dream, and now you kinda can! Sure it’s barely warm enough to boil water in your kettle, but in a world where fire has taken on a life of its own in literal hell you take what little power you can get. Discovered by accident when a frustrated word wielder yelled at a pile of sticks, this spell is a favourite for many a pyromaniac word wielder,");
            add("info.spelled.book.descriptive.ignis.text2", "who finds the ability to set things on fire at will too tempting to give up. Please take care when using this word, and remember, only you can stop forest fires!");
        }
    }
}