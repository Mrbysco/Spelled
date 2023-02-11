package com.mrbysco.spelled.generator.data;

import com.mrbysco.spelled.registry.SpelledRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SpelledAdvancements extends ForgeAdvancementProvider {
	private static final List<AdvancementGenerator> subproviders = List.of(new SpelledAdvancementGenerator());

	public SpelledAdvancements(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries,
							   ExistingFileHelper existingFileHelper) {
		super(packOutput, registries,
				existingFileHelper, subproviders);
	}

	public static class SpelledAdvancementGenerator implements ForgeAdvancementProvider.AdvancementGenerator {

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

		@Override
		public void generate(HolderLookup.Provider registries, Consumer<Advancement> consumer, ExistingFileHelper existingFileHelper) {
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
}
