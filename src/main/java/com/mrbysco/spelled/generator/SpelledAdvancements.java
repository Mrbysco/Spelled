package com.mrbysco.spelled.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementRewards.Builder;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.EnterBlockTrigger;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.data.AdvancementProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public class SpelledAdvancements extends AdvancementProvider {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private final DataGenerator dataGenerator;

    public Advancement root;

    //Colors
    public Advancement color_lore;
    public Advancement ater;
    public Advancement aureus;
    public Advancement caeruleus;
    public Advancement viridis;
    public Advancement aqua;
    public Advancement rubrum;
    public Advancement roseus;
    public Advancement flavus;
    public Advancement albus;

    //Size
    public Advancement parvus;
    public Advancement magnum;
    public Advancement grandis;
    public Advancement immanis;

    //Informative
    public Advancement liquidus;
    public Advancement nix;
    public Advancement frigus;
    public Advancement dissiliunt;
    public Advancement sanitatem;
    public Advancement nocere;
    public Advancement praesidium;
    public Advancement fractionis;
    public Advancement propellentibus;
    public Advancement ignis;

    public SpelledAdvancements(DataGenerator generatorIn) {
        super(generatorIn);
        this.dataGenerator = generatorIn;
    }

    public Advancement getRoot() {
        return root;
    }

    @Override
    public void act(DirectoryCache cache) {
        Path outputFolder = this.dataGenerator.getOutputFolder();
        Consumer<Advancement> consumer = advancement -> {
            Path path = outputFolder.resolve("data/" + advancement.getId().getNamespace() + "/advancements/" + advancement.getId().getPath() + ".json");
            try {
                IDataProvider.save(GSON, cache, advancement.copy().serialize(), path);
            } catch (IOException e) {
                System.out.println(e);
            }
        };
        registerAdvancements(consumer);
    }

    private void registerAdvancements(Consumer<Advancement> consumer) {
        root = Advancement.Builder.builder()
                .withDisplay(SpelledRegistry.KNOWLEDGE_TOME.get(),
                        new TranslationTextComponent("advancement.spelled.root"),
                        new TranslationTextComponent("advancement.spelled.root.desc"),
                        new ResourceLocation("minecraft:textures/block/bookshelf.png"), FrameType.TASK, true, false, false)
                .withCriterion("air", EnterBlockTrigger.Instance.forBlock(Blocks.AIR))
                .withRewards(withLoot(new ResourceLocation(Reference.MOD_ID, "advancements/manual")))
                .register(consumer, "spelled:root");

        color_lore = Advancement.Builder.builder()
                .withDisplay(SpelledRegistry.KNOWLEDGE_TOME.get(),
                        new TranslationTextComponent("advancement.spelled.color_lore"),
                        new TranslationTextComponent("advancement.spelled.color_lore.desc"),
                        null, FrameType.TASK, false, false, false)
                .withParent(root)
                .withCriterion("impossible", new ImpossibleTrigger.Instance())
                .register(consumer, "spelled:color_lore");

        ater = generateAdjectiveAdvancement("ater", color_lore, consumer);
        aureus = generateAdjectiveAdvancement("aureus", ater, consumer);
        caeruleus = generateAdjectiveAdvancement("caeruleus", aureus, consumer);
        viridis = generateAdjectiveAdvancement("viridis", caeruleus, consumer);
        aqua = generateAdjectiveAdvancement("aqua", viridis, consumer);
        rubrum = generateAdjectiveAdvancement("rubrum", aqua, consumer);
        roseus = generateAdjectiveAdvancement("roseus", rubrum, consumer);
        flavus = generateAdjectiveAdvancement("flavus", roseus, consumer);
        albus = generateAdjectiveAdvancement("albus", flavus, consumer);

        parvus = generateAdjectiveAdvancement("parvus", root, consumer);
        magnum = generateAdjectiveAdvancement("magnum", parvus, consumer);
        grandis = generateAdjectiveAdvancement("grandis", magnum, consumer);
        immanis = generateAdjectiveAdvancement("immanis", grandis, consumer);

        liquidus = generateAdjectiveAdvancement("liquidus", root, consumer);
        nix = generateAdjectiveAdvancement("nix", liquidus, consumer);
        frigus = generateAdjectiveAdvancement("frigus", nix, consumer);
        dissiliunt = generateAdjectiveAdvancement("dissiliunt", frigus, consumer);
        sanitatem = generateAdjectiveAdvancement("sanitatem", dissiliunt, consumer);
        nocere = generateAdjectiveAdvancement("nocere", sanitatem, consumer);
        praesidium = generateAdjectiveAdvancement("praesidium", nocere, consumer);
        fractionis = generateAdjectiveAdvancement("fractionis", praesidium, consumer);
        propellentibus = generateAdjectiveAdvancement("propellentibus", fractionis, consumer);
        ignis = generateAdjectiveAdvancement("ignis", propellentibus, consumer);
    }

    private AdvancementRewards.Builder withLoot(ResourceLocation loot) {
        Builder builder = (new AdvancementRewards.Builder());
        builder.loot.add(loot);
        return builder;
    }

    private Advancement generateAdjectiveAdvancement(String adjective, Advancement parent, Consumer<Advancement> consumer) {
        return Advancement.Builder.builder()
                .withDisplay(SpelledRegistry.KNOWLEDGE_TOME.get(),
                        new TranslationTextComponent(String.format("advancement.spelled.%s", adjective)),
                        new TranslationTextComponent(String.format("advancement.spelled.%s.desc", adjective)),
                        null, FrameType.TASK, false, false, false)
                .withParent(parent)
                .withCriterion("impossible", new ImpossibleTrigger.Instance())
                .register(consumer, "spelled:adjective_" + adjective);
    }
}