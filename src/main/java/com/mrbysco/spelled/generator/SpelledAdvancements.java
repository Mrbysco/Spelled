package com.mrbysco.spelled.generator;

import com.mrbysco.spelled.Spelled;
import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public class SpelledAdvancements extends AdvancementProvider {
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
	public Advancement vis;
	public Advancement sericum;
	public Advancement maturis;

	public SpelledAdvancements(DataGenerator generatorIn, ExistingFileHelper fileHelper) {
		super(generatorIn, fileHelper);
		this.dataGenerator = generatorIn;
	}

	public Advancement getRoot() {
		return root;
	}

	@Override
	public void run(CachedOutput cache) {
		Path outputFolder = this.dataGenerator.getOutputFolder();
		Consumer<Advancement> consumer = advancement -> {
			Path path = outputFolder.resolve("data/" + advancement.getId().getNamespace() + "/advancements/" + advancement.getId().getPath() + ".json");
			try {
				DataProvider.saveStable(cache, advancement.deconstruct().serializeToJson(), path);
			} catch (IOException e) {
				Spelled.LOGGER.trace("Failed to save", e);
			}
		};
		registerAdvancements(consumer);
	}

	private void registerAdvancements(Consumer<Advancement> consumer) {
		root = Advancement.Builder.advancement()
				.display(SpelledRegistry.KNOWLEDGE_TOME.get(),
						Component.translatable("advancement.spelled.root"),
						Component.translatable("advancement.spelled.root.desc"),
						new ResourceLocation("minecraft:textures/block/bookshelf.png"), FrameType.TASK, true, false, false)
				.addCriterion("air", EnterBlockTrigger.TriggerInstance.entersBlock(Blocks.AIR))
				.save(consumer, "spelled:root");

		color_lore = Advancement.Builder.advancement()
				.display(SpelledRegistry.KNOWLEDGE_TOME.get(),
						Component.translatable("advancement.spelled.color_lore"),
						Component.translatable("advancement.spelled.color_lore.desc"),
						null, FrameType.TASK, false, false, false)
				.parent(root)
				.addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
				.save(consumer, "spelled:color_lore");

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
		vis = generateAdjectiveAdvancement("vis", fractionis, consumer);
		sericum = generateAdjectiveAdvancement("sericum", vis, consumer);
		maturis = generateAdjectiveAdvancement("maturis", praesidium, consumer);
	}

	private Advancement generateAdjectiveAdvancement(String adjective, Advancement parent, Consumer<Advancement> consumer) {
		return Advancement.Builder.advancement()
				.display(SpelledRegistry.KNOWLEDGE_TOME.get(),
						Component.translatable(String.format("advancement.spelled.%s", adjective)),
						Component.translatable(String.format("advancement.spelled.%s.desc", adjective)),
						null, FrameType.TASK, false, false, false)
				.parent(parent)
				.addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
				.save(consumer, "spelled:adjective_" + adjective);
	}
}
