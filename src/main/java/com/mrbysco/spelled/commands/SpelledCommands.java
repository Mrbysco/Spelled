package com.mrbysco.spelled.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.config.ConfigCache;
import com.mrbysco.spelled.config.SpelledConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SpelledCommands {
    public static void initializeCommands (CommandDispatcher<CommandSource> dispatcher) {
        final LiteralArgumentBuilder<CommandSource> root = Commands.literal("spelled");
        ArrayList<String> adjectives = KeywordRegistry.instance().getAdjectives();
        adjectives.add("all");

        root.requires((source) -> source.hasPermissionLevel(2))
                .then(Commands.literal("level")
                        .then(Commands.argument("player", EntityArgument.players())
                .then(Commands.literal("get").executes(SpelledCommands::getLevel))
                .then(Commands.literal("set").then(Commands.argument("level", IntegerArgumentType.integer(0))
                        .executes(SpelledCommands::setLevel)))))
                .then(Commands.literal("knowledge")
                        .then(Commands.argument("player", EntityArgument.players())
                                .then(Commands.literal("unlock")
                                        .then(Commands.argument("word", StringArgumentType.string())
                                                .suggests((cs, builder) -> ISuggestionProvider.suggest(SpelledCommands.getAdjectivesPlusAll(), builder))
                                                        .executes(SpelledCommands::unlockWord)))
                                .then(Commands.literal("lock")
                                        .then(Commands.argument("word", StringArgumentType.string())
                                                .suggests((cs, builder) -> ISuggestionProvider.suggest(SpelledCommands.getAdjectivesPlusAll(), builder))
                                                .executes(SpelledCommands::lockWord)))
                                .then(Commands.literal("reset").executes(SpelledCommands::resetWords))));

                                        dispatcher.register(root);
    }

    protected static ArrayList<String> getAdjectivesPlusAll() {
        ArrayList<String> adjectives = new ArrayList<>(KeywordRegistry.instance().getAdjectives());
        adjectives.add("all");
        return adjectives;
    }

    private static int getLevel(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        for(ServerPlayerEntity player : EntityArgument.getPlayers(ctx, "player")) {
            int level = SpelledAPI.getLevel(player);
            if(level >= 0) {
                ITextComponent levelText = new StringTextComponent(String.valueOf(level)).mergeStyle(TextFormatting.GOLD);
                ITextComponent text = new TranslationTextComponent("spelled.commands.level.get.message",
                        player.getDisplayName(), levelText);
                ctx.getSource().sendFeedback(text, false);
            }
        }

        return 0;
    }

    private static int setLevel(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        int level = IntegerArgumentType.getInteger(ctx, "level");
        if(level >= 0 && level <= SpelledConfig.COMMON.maxLevel.get()) {
            for(ServerPlayerEntity player : EntityArgument.getPlayers(ctx, "player")) {
                SpelledAPI.forceSetLevel(player, level);
                ITextComponent levelText = new StringTextComponent(String.valueOf(level)).mergeStyle(TextFormatting.GOLD);
                ITextComponent text = new TranslationTextComponent("spelled.commands.level.set.message",
                        player.getDisplayName(), levelText);
                ctx.getSource().sendFeedback(text, true);
            }
        } else {
            ITextComponent text = new TranslationTextComponent("spelled.commands.level.set.invalid", level)
                    .mergeStyle(TextFormatting.RED);
            ctx.getSource().sendFeedback(text, true);
        }

        return 0;
    }

    private static int unlockWord(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        String word = StringArgumentType.getString(ctx, "word").toLowerCase(Locale.ROOT);
        if(!word.isEmpty()) {
            if(word.equals("all")) {
                for(ServerPlayerEntity player : EntityArgument.getPlayers(ctx, "player")) {
                    List<String> adjectives = KeywordRegistry.instance().getAdjectives();
                    for(String adjective : adjectives) {
                        SpelledAPI.unlockKeyword(player, adjective);
                    }

                    ITextComponent text = new TranslationTextComponent("spelled.commands.knowledge.unlock.all", player.getDisplayName());
                    ctx.getSource().sendFeedback(text, true);
                }
            } else if(KeywordRegistry.instance().containsKey(word)) {
                for(ServerPlayerEntity player : EntityArgument.getPlayers(ctx, "player")) {
                    SpelledAPI.unlockKeyword(player, word);

                    ITextComponent wordComponent = new StringTextComponent(word).mergeStyle(TextFormatting.GOLD);
                    ITextComponent text = new TranslationTextComponent("spelled.commands.knowledge.unlock.message", wordComponent, player.getDisplayName());
                    ctx.getSource().sendFeedback(text, true);
                }
            }
        } else {
            ITextComponent text = new TranslationTextComponent("spelled.commands.knowledge.unlock.invalid", word)
                    .mergeStyle(TextFormatting.RED);
            ctx.getSource().sendFeedback(text, true);
        }

        return 0;
    }

    private static int lockWord(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        String word = StringArgumentType.getString(ctx, "word").toLowerCase(Locale.ROOT);
        if(!word.isEmpty()) {
            if(word.equals("all")) {
                for(ServerPlayerEntity player : EntityArgument.getPlayers(ctx, "player")) {
                    SpelledAPI.resetUnlocks(player);

                    ITextComponent text = new TranslationTextComponent("spelled.commands.knowledge.reset.message", player.getDisplayName());
                    ctx.getSource().sendFeedback(text, true);
                }
            } else if(KeywordRegistry.instance().containsKey(word)) {
                for(ServerPlayerEntity player : EntityArgument.getPlayers(ctx, "player")) {
                    SpelledAPI.lockKeyword(player, word);

                    ITextComponent wordComponent = new StringTextComponent(word).mergeStyle(TextFormatting.GOLD);
                    ITextComponent text = new TranslationTextComponent("spelled.commands.knowledge.lock.message", wordComponent, player.getDisplayName());
                    ctx.getSource().sendFeedback(text, true);
                }
            }
        } else {
            ITextComponent text = new TranslationTextComponent("spelled.commands.knowledge.lock.invalid", word)
                    .mergeStyle(TextFormatting.RED);
            ctx.getSource().sendFeedback(text, true);
        }

        return 0;
    }

    private static int resetWords(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        for(ServerPlayerEntity player : EntityArgument.getPlayers(ctx, "player")) {
            SpelledAPI.resetUnlocks(player);

            ITextComponent text = new TranslationTextComponent("spelled.commands.knowledge.reset.message", player.getDisplayName());
            ctx.getSource().sendFeedback(text, true);
        }

        return 0;
    }
}
