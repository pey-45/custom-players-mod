package com.pey.customplayersmod.handlers;

import com.pey.customplayersmod.loaders.PlayerCommandsLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GenericHandler {

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static boolean running;


    public static void join(ServerCommandSource source, String playerName) {
        MinecraftServer server = source.getServer();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);

        if (player != null && player.isAlive()) {
            source.sendFeedback(() ->
                    Text.literal(playerName + " is already connected").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        List<String> commands = PlayerCommandsLoader.getCommands(playerName, "join");
        if (commands == null || commands.isEmpty()) {
            source.sendFeedback(() ->
                    Text.literal("No commands found for specified player").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }
        executeCommands(source, commands);

        server.getPlayerManager().broadcast(
                Text.literal(playerName + " joined").styled(style -> style.withColor(Formatting.GREEN)),
                false
        );

        running = false;
    }


    public static void start(ServerCommandSource source, String playerName) {
        MinecraftServer server = source.getServer();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);

        if (player == null || !player.isAlive()) {
            join(source, playerName);
        }

        if (running) {
            source.sendFeedback(() ->
                    Text.literal(playerName + " is already running").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        List<String> commands = PlayerCommandsLoader.getCommands(playerName, "start");
        if (commands == null || commands.isEmpty()) {
            source.sendFeedback(() ->
                    Text.literal("No commands found for specified player").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }
        executeCommands(source, commands);

        server.getPlayerManager().broadcast(
                Text.literal(playerName + " started").styled(style -> style.withColor(Formatting.GREEN)),
                false
        );

        running = true;
    }


    public static void stop(ServerCommandSource source, String playerName) {
        MinecraftServer server = source.getServer();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);

        if (player == null || !player.isAlive()) {
            source.sendFeedback(() ->
                    Text.literal(playerName + " is not connected").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        if (!running) {
            source.sendFeedback(() ->
                    Text.literal(playerName + " is not running").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        List<String> commands = PlayerCommandsLoader.getCommands(playerName, "stop");
        if (commands == null || commands.isEmpty()) {
            source.sendFeedback(() ->
                    Text.literal("No commands found for specified player").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }
        executeCommands(source, commands);

        server.getPlayerManager().broadcast(
                Text.literal(playerName + " stopped").styled(style -> style.withColor(Formatting.GREEN)),
                false
        );

        running = false;
    }


    public static void leave(ServerCommandSource source, String playerName) {
        MinecraftServer server = source.getServer();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);

        if (player == null || !player.isAlive()) {
            source.sendFeedback(() ->
                    Text.literal(playerName + " is not connected").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        List<String> commands = PlayerCommandsLoader.getCommands(playerName, "leave");
        if (commands == null || commands.isEmpty()) {
            source.sendFeedback(() ->
                    Text.literal("No commands found for specified player").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }
        executeCommands(source, commands);

        server.getPlayerManager().broadcast(
                Text.literal(playerName + " left").styled(style -> style.withColor(Formatting.GREEN)),
                false
        );

        running = false;
    }

    private static void executeCommands(ServerCommandSource source, List<String> commands) {
        MinecraftServer server = source.getServer();
        int delay = 0;
        for (String command : commands) {
            scheduler.schedule(
                    () -> server.execute(() -> server.getCommandManager().executeWithPrefix(
                            server.getCommandSource(),
                            command)
                    ),
                    delay++,
                    TimeUnit.SECONDS
            );
        }
    }
}