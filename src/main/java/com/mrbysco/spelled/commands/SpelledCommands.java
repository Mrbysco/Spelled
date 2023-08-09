package com.mrbysco.spelled.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrbysco.spelled.api.SpelledAPI;
import com.mrbysco.spelled.api.keywords.KeywordRegistry;
import com.mrbysco.spelled.config.SpelledConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SpelledCommands {
	public static void initializeCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
		final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("spelled");
		ArrayList<String> adjectives = KeywordRegistry.instance().getAdjectives();
		adjectives.add("all");

		root.requires((source) -> source.hasPermission(2))
				.then(Commands.literal("level")
						.then(Commands.argument("player", EntityArgument.players())
								.then(Commands.literal("get").executes(SpelledCommands::getLevel))
								.then(Commands.literal("set").then(Commands.argument("level", IntegerArgumentType.integer(0))
										.executes(SpelledCommands::setLevel)))))
				.then(Commands.literal("knowledge")
						.then(Commands.argument("player", EntityArgument.players())
								.then(Commands.literal("unlock")
										.then(Commands.argument("word", StringArgumentType.string())
												.suggests((cs, builder) -> SharedSuggestionProvider.suggest(SpelledCommands.getAdjectivesPlusAll(), builder))
												.executes(SpelledCommands::unlockWord)))
								.then(Commands.literal("lock")
										.then(Commands.argument("word", StringArgumentType.string())
												.suggests((cs, builder) -> SharedSuggestionProvider.suggest(SpelledCommands.getAdjectivesPlusAll(), builder))
												.executes(SpelledCommands::lockWord)))
								.then(Commands.literal("reset").executes(SpelledCommands::resetWords))));

		dispatcher.register(root);
	}

	protected static ArrayList<String> getAdjectivesPlusAll() {
		ArrayList<String> adjectives = new ArrayList<>(KeywordRegistry.instance().getAdjectives());
		adjectives.add("all");
		return adjectives;
	}

	@SuppressWarnings("SameReturnValue")
	private static int getLevel(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		for (ServerPlayer player : EntityArgument.getPlayers(ctx, "player")) {
			int level = SpelledAPI.getLevel(player);
			if (level >= 0) {
				Component levelText = Component.literal(String.valueOf(level)).withStyle(ChatFormatting.GOLD);
				Component text = Component.translatable("spelled.commands.level.get.message",
						player.getDisplayName(), levelText);
				ctx.getSource().sendSuccess(text, false);
			}
		}

		return 0;
	}

	@SuppressWarnings("SameReturnValue")
	private static int setLevel(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		int level = IntegerArgumentType.getInteger(ctx, "level");
		if (level >= 0 && level <= SpelledConfig.COMMON.maxLevel.get()) {
			for (ServerPlayer player : EntityArgument.getPlayers(ctx, "player")) {
				SpelledAPI.forceSetLevel(player, level);
				SpelledAPI.syncCap(player);
				Component levelText = Component.literal(String.valueOf(level)).withStyle(ChatFormatting.GOLD);
				Component text = Component.translatable("spelled.commands.level.set.message",
						player.getDisplayName(), levelText);
				ctx.getSource().sendSuccess(text, true);
			}
		} else {
			Component text = Component.translatable("spelled.commands.level.set.invalid", level, SpelledConfig.COMMON.maxLevel.get())
					.withStyle(ChatFormatting.RED);
			ctx.getSource().sendSuccess(text, true);
		}

		return 0;
	}

	@SuppressWarnings("SameReturnValue")
	private static int unlockWord(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		String word = StringArgumentType.getString(ctx, "word").toLowerCase(Locale.ROOT);
		if (!word.isEmpty()) {
			if (word.equals("all")) {
				for (ServerPlayer player : EntityArgument.getPlayers(ctx, "player")) {
					List<String> adjectives = KeywordRegistry.instance().getAdjectives();
					for (String adjective : adjectives) {
						SpelledAPI.unlockKeyword(player, adjective);
					}
					SpelledAPI.syncCap(player);

					Component text = Component.translatable("spelled.commands.knowledge.unlock.all", player.getDisplayName());
					ctx.getSource().sendSuccess(text, true);
				}
			} else if (KeywordRegistry.instance().containsKey(word)) {
				for (ServerPlayer player : EntityArgument.getPlayers(ctx, "player")) {
					SpelledAPI.unlockKeyword(player, word);
					SpelledAPI.syncCap(player);

					Component wordComponent = Component.literal(word).withStyle(ChatFormatting.GOLD);
					Component text = Component.translatable("spelled.commands.knowledge.unlock.message", wordComponent, player.getDisplayName());
					ctx.getSource().sendSuccess(text, true);
				}
			}
		} else {
			Component text = Component.translatable("spelled.commands.knowledge.unlock.invalid", word)
					.withStyle(ChatFormatting.RED);
			ctx.getSource().sendSuccess(text, true);
		}

		return 0;
	}

	@SuppressWarnings("SameReturnValue")
	private static int lockWord(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		String word = StringArgumentType.getString(ctx, "word").toLowerCase(Locale.ROOT);
		if (!word.isEmpty()) {
			if (word.equals("all")) {
				for (ServerPlayer player : EntityArgument.getPlayers(ctx, "player")) {
					SpelledAPI.resetUnlocks(player);
					SpelledAPI.syncCap(player);

					Component text = Component.translatable("spelled.commands.knowledge.reset.message", player.getDisplayName());
					ctx.getSource().sendSuccess(text, true);
				}
			} else if (KeywordRegistry.instance().containsKey(word)) {
				for (ServerPlayer player : EntityArgument.getPlayers(ctx, "player")) {
					SpelledAPI.lockKeyword(player, word);
					SpelledAPI.syncCap(player);

					Component wordComponent = Component.literal(word).withStyle(ChatFormatting.GOLD);
					Component text = Component.translatable("spelled.commands.knowledge.lock.message", wordComponent, player.getDisplayName());
					ctx.getSource().sendSuccess(text, true);
				}
			}
		} else {
			Component text = Component.translatable("spelled.commands.knowledge.lock.invalid", word)
					.withStyle(ChatFormatting.RED);
			ctx.getSource().sendSuccess(text, true);
		}

		return 0;
	}

	@SuppressWarnings("SameReturnValue")
	private static int resetWords(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		for (ServerPlayer player : EntityArgument.getPlayers(ctx, "player")) {
			SpelledAPI.resetUnlocks(player);
			SpelledAPI.syncCap(player);

			Component text = Component.translatable("spelled.commands.knowledge.reset.message", player.getDisplayName());
			ctx.getSource().sendSuccess(text, true);
		}

		return 0;
	}
}
