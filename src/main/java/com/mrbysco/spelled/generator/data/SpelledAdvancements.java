package com.mrbysco.spelled.generator.data;

import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.criterion.EnterBlockTrigger;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SpelledAdvancements extends AdvancementProvider {
	private static final List<AdvancementSubProvider> subproviders = List.of(new SpelledAdvancementGenerator());

	public SpelledAdvancements(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
		super(packOutput, registries, subproviders);
	}


	public static class SpelledAdvancementGenerator implements AdvancementSubProvider {

		public AdvancementHolder root;

		//Colors
		public AdvancementHolder color_lore;
		public AdvancementHolder ater;
		public AdvancementHolder aureus;
		public AdvancementHolder caeruleus;
		public AdvancementHolder viridis;
		public AdvancementHolder aqua;
		public AdvancementHolder rubrum;
		public AdvancementHolder roseus;
		public AdvancementHolder flavus;
		public AdvancementHolder albus;

		//Size
		public AdvancementHolder parvus;
		public AdvancementHolder magnum;
		public AdvancementHolder grandis;
		public AdvancementHolder immanis;

		//Informative
		public AdvancementHolder liquidus;
		public AdvancementHolder nix;
		public AdvancementHolder frigus;
		public AdvancementHolder dissiliunt;
		public AdvancementHolder sanitatem;
		public AdvancementHolder nocere;
		public AdvancementHolder praesidium;
		public AdvancementHolder fractionis;
		public AdvancementHolder propellentibus;
		public AdvancementHolder ignis;
		public AdvancementHolder vis;
		public AdvancementHolder sericum;
		public AdvancementHolder maturis;

		@Override
		public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer) {
			root = Advancement.Builder.advancement()
					.display(SpelledRegistry.KNOWLEDGE_TOME.get(),
							Component.translatable("advancement.spelled.root"),
							Component.translatable("advancement.spelled.root.desc"),
							Identifier.withDefaultNamespace("textures/block/bookshelf.png"), AdvancementType.TASK, true, false, false)
					.addCriterion("air", EnterBlockTrigger.TriggerInstance.entersBlock(Blocks.AIR))
					.save(consumer, "spelled:root");

			color_lore = Advancement.Builder.advancement()
					.display(SpelledRegistry.KNOWLEDGE_TOME.get(),
							Component.translatable("advancement.spelled.color_lore"),
							Component.translatable("advancement.spelled.color_lore.desc"),
							null, AdvancementType.TASK, false, false, false)
					.parent(root)
					.addCriterion("impossible", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
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

		private AdvancementHolder generateAdjectiveAdvancement(String adjective, AdvancementHolder parent, Consumer<AdvancementHolder> consumer) {
			return Advancement.Builder.advancement()
					.display(SpelledRegistry.KNOWLEDGE_TOME.get(),
							Component.translatable(String.format("advancement.spelled.%s", adjective)),
							Component.translatable(String.format("advancement.spelled.%s.desc", adjective)),
							null, AdvancementType.TASK, false, false, false)
					.parent(parent)
					.addCriterion("impossible", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
					.save(consumer, "spelled:adjective_" + adjective);
		}
	}
}
