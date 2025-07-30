package com.pey.customplayersmod.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.pey.customplayersmod.config.PlayerLoader;
import com.pey.customplayersmod.config.PlayerStateManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.CompletableFuture;

public class CommandSuggestions {

    public enum subcommand {JOIN, START, STOP, LEAVE}

    public static CompletableFuture<Suggestions> suggestPlayerNames(CommandSuggestions.subcommand subcommand, CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        MinecraftServer server = context.getSource().getServer();

        for (String playerName : PlayerLoader.getConfig().keySet()) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);

            if (((subcommand == CommandSuggestions.subcommand.JOIN && player == null)
                    || (subcommand == CommandSuggestions.subcommand.START && !PlayerStateManager.isStarted(playerName))
                    || (subcommand == CommandSuggestions.subcommand.STOP && player != null && PlayerStateManager.isStarted(playerName))
                    || (subcommand == CommandSuggestions.subcommand.LEAVE && player != null))
                    && playerName.startsWith(builder.getRemaining())) {
                builder.suggest(playerName);
            }
        }

        for (String playerName : server.getPlayerNames()) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);

            if (subcommand == CommandSuggestions.subcommand.LEAVE && player != null && playerName.startsWith("afk")) {
                builder.suggest(playerName);
            }
        }

        return builder.buildFuture();
    }
}
