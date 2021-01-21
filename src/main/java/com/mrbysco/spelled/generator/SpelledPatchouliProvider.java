package com.mrbysco.spelled.generator;

import com.mrbysco.spelled.Reference;
import com.mrbysco.spelled.registry.KeywordRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import xyz.brassgoggledcoders.patchouliprovider.BookBuilder;
import xyz.brassgoggledcoders.patchouliprovider.CategoryBuilder;
import xyz.brassgoggledcoders.patchouliprovider.EntryBuilder;
import xyz.brassgoggledcoders.patchouliprovider.PatchouliBookProvider;

import java.util.function.Consumer;

public class SpelledPatchouliProvider extends PatchouliBookProvider {
    public SpelledPatchouliProvider(DataGenerator gen) {
        super(gen, Reference.MOD_ID, "en_us");
    }

    @Override
    protected void addBooks(Consumer<BookBuilder> consumer) {
        BookBuilder bookBuilder = createBookBuilder("knowledge_tome", "item.spelled.book.name", "info.spelled.book.landing")
                .setSubtitle("info.spelled.book.subtitle")
                .setAdvancementsTab("spelled:root")
                .setCreativeTab("spelled")
                .setModel("spelled:knowledge_tome")
                .setBookTexture("spelled:textures/gui/tome.png")
                .setShowProgress(false)
                .setI18n(true)
//                .setUseBlockyFont(true)
                .setFillerTexture("spelled:textures/gui/page_filler.png")
                .addMacro("$(item)", "$(#94fcf5)")
                .addMacro("$(thing)", "$(#94fcf5)")

                //Leveling category
                .addCategory("leveling", "info.spelled.book.leveling.name", "info.spelled.book.leveling.desc", "spelled:leveling_altar")
                    //Add leveling altar entry
                    .addEntry("leveling/leveling", "info.spelled.book.leveling.entry.name", "spelled:leveling_altar")
                    .addTextPage("info.spelled.book.leveling_info.text").build()
                    .addCraftingPage(new ResourceLocation(Reference.MOD_ID, "leveling_altar")).setText("info.spelled.book.leveling_recipe.text").build()
                .build().build() //Back to the bookbuilder

                //Types
                .addCategory("types", "info.spelled.book.types.name", "info.spelled.book.types.desc", "spelled:ancient_knowledge_tome")
                    .addEntry("types/self", "info.spelled.book.types.self.name", "spelled:ancient_knowledge_tome")
                        .addTextPage("info.spelled.book.types.self.text").build()
                        .addTextPage("info.spelled.book.types.self.text2").build().build()
                    .addEntry("types/ball", "info.spelled.book.types.ball.name", "spelled:ancient_knowledge_tome")
                        .addTextPage("info.spelled.book.types.ball.text").build()
                        .addTextPage("info.spelled.book.types.ball.text2").build().build()
                    .addEntry("types/projectilis", "info.spelled.book.types.projectilis.name", "spelled:ancient_knowledge_tome")
                        .addTextPage("info.spelled.book.types.projectilis.text").build()
                        .addTextPage("info.spelled.book.types.projectilis.text2").build()
                .build().build(); //Back to the bookbuilder

                //Colors
                bookBuilder = addColors(bookBuilder);

                //Size
                bookBuilder = addSizes(bookBuilder);

                //Others
                bookBuilder = addDescriptions(bookBuilder);

                //Finish book
                bookBuilder.build(consumer);

    }

    public BookBuilder addColors(BookBuilder builder) {
        //Colors
        CategoryBuilder colorCategory = builder.addCategory("colors", "info.spelled.book.colors.name", "info.spelled.book.colors.desc", "spelled:ancient_knowledge_tome")
                .setSecret(true);


        colorCategory.addEntry("colors/lore", "info.spelled.book.colors.lore.name", "spelled:ancient_knowledge_tome")
                .setSecret(true)
                .setAdvancement("spelled:color_lore")
                    .addTextPage("info.spelled.book.colors.lore.text").build()
                    .addTextPage("info.spelled.book.colors.lore.text2").build()
                .build();

        for(String color : KeywordRegistry.instance().getColors()) {
            colorCategory.addEntry("colors/" + color, String.format("info.spelled.book.colors.%s.name", color), "spelled:ancient_knowledge_tome")
                    .setSecret(true)
                    .setAdvancement("spelled:adjective_" + color)
                    .addTextPage(String.format("info.spelled.book.colors.%s.text", color)).build().build();
        }
        builder = colorCategory.build();
        return builder;
    }

    public BookBuilder addSizes(BookBuilder builder) {
        CategoryBuilder sizeCategory = builder.addCategory("size", "info.spelled.book.size.name", "info.spelled.book.size.desc", "spelled:ancient_knowledge_tome")
                .setSecret(true);

        final String[] sizes = new String[] {
                "parvus",
                "magnum",
                "grandis",
                "immanis"
        };
        for(String size : sizes) {
            EntryBuilder sizeEntry = sizeCategory.addEntry("size/" + size, String.format("info.spelled.book.size.%s.name", size), "spelled:ancient_knowledge_tome")
                    .setSecret(true)
                    .setAdvancement("spelled:adjective_" + size)
                    .addTextPage(String.format("info.spelled.book.size.%s.text", size)).build();

                    if(!size.equals("magnum")) {
                        sizeEntry.addTextPage(String.format("info.spelled.book.size.%s.text2", size)).build().build();
                    }

            sizeCategory = sizeEntry.build();
        }
        builder = sizeCategory.build();
        return builder;
    }

    public BookBuilder addDescriptions(BookBuilder builder) {
        CategoryBuilder descriptionCategory = builder.addCategory("descriptions", "info.spelled.book.descriptive.name", "info.spelled.book.descriptive.desc", "spelled:ancient_knowledge_tome")
                .setSecret(true);

        final String[] descriptions = new String[] {
                "liquidus",
                "nix",
                "frigus",
                "dissiliunt",
                "sanitatem",
                "nocere",
                "praesidium",
                "fractionis",
                "propellentibus",
                "ignis"
        };
        for(String descriptive : descriptions) {
            EntryBuilder descriptiveEntry = descriptionCategory.addEntry("descriptive/" + descriptive, String.format("info.spelled.book.descriptive.%s.name", descriptive), "spelled:ancient_knowledge_tome")
                    .setSecret(true)
                    .setAdvancement("spelled:adjective_" + descriptive)
                    .addTextPage(String.format("info.spelled.book.descriptive.%s.text", descriptive)).build();

                    if(!descriptive.equals("frigus")) {
                        descriptiveEntry.addTextPage(String.format("info.spelled.book.descriptive.%s.text2", descriptive)).build();
                    }
            descriptionCategory = descriptiveEntry.build();
        }
        builder = descriptionCategory.build();
        return builder;
    }
}
