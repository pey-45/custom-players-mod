package com.pey.customplayersmod.commands;

import com.pey.customplayersmod.config.PlayerLoader;
import com.pey.customplayersmod.config.PlayerStateManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CommandHandler {

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


    public static void join(ServerCommandSource source, String playerName) {
        MinecraftServer server = source.getServer();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);

        if (player != null && player.isAlive()) {
            source.sendFeedback(() ->
                    Text.literal(playerName + " is already connected").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        List<String> commands = PlayerLoader.getCommands(playerName, "join");
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
    }


    public static void start(ServerCommandSource source, String playerName) {
        MinecraftServer server = source.getServer();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);

        if (player == null || !player.isAlive()) {
            join(source, playerName);
            waitForPlayerConnection(source, playerName,
                    () -> start(source, playerName),
                    () -> source.sendFeedback(() -> Text.literal(playerName + " could not connect")
                                    .styled(s -> s.withColor(Formatting.RED)), false));
            return;
        }

        if (PlayerStateManager.isStarted(playerName)) {
            source.sendFeedback(() ->
                    Text.literal(playerName + " is already running").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        List<String> commands = PlayerLoader.getCommands(playerName, "start");
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

        PlayerStateManager.markStarted(playerName);
    }


    public static void stop(ServerCommandSource source, String playerName) {
        MinecraftServer server = source.getServer();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);

        if (player == null || !player.isAlive()) {
            source.sendFeedback(() ->
                    Text.literal(playerName + " is not connected").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        if (!PlayerStateManager.isStarted(playerName)) {
            source.sendFeedback(() ->
                    Text.literal(playerName + " is not running").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        List<String> commands = PlayerLoader.getCommands(playerName, "stop");
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

        PlayerStateManager.markStopped(playerName);
    }


    public static void leave(ServerCommandSource source, String playerName) {
        MinecraftServer server = source.getServer();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);

        if (player == null || !player.isAlive()) {
            source.sendFeedback(() ->
                    Text.literal(playerName + " is not connected").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        List<String> commands = PlayerLoader.getCommands(playerName, "leave");
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

    }

    public static void joinAfk(ServerCommandSource source, String name) {
        MinecraftServer server = source.getServer();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer("afk" + name);

        if (player != null && player.isAlive()) {
            source.sendFeedback(() ->
                    Text.literal("afk" + name + " is already connected").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        ServerPlayerEntity sourceplayer = source.getPlayer();

        server.execute(() ->
                {
                    assert sourceplayer != null;
                    server.getCommandManager().executeWithPrefix(server.getCommandSource(),
                            "player afk" + name + " spawn at " +
                                    sourceplayer.getPos().getX() + " " +
                                    sourceplayer.getPos().getY() + " " +
                                    sourceplayer.getPos().getZ() + " facing 0 0 in " +
                                    sourceplayer.getWorld().getRegistryKey().getValue().toString()
                    );
                }
        );

        server.getPlayerManager().broadcast(
                Text.literal("afk" + name + " joined")
                        .styled(style -> style.withColor(Formatting.GREEN)),
                false
        );
    }

    public static void listPlayers(ServerCommandSource source) {

        Map<String, Map<String, List<String>>> config = PlayerLoader.getConfig();

        source.sendFeedback(() -> Text.literal("Found " + config.size() + " players:"), false);
        for (String playerName : config.keySet()) {
            source.sendFeedback(() -> Text.literal(playerName), false);
        }
    }

    public static void listPlayersFull(ServerCommandSource source) {

        Map<String, Map<String, List<String>>> config = PlayerLoader.getConfig();

        source.sendFeedback(() -> Text.literal("Found " + config.size() + " players:"), false);
        config.forEach((playerName, subcommands) -> {
            List<String> joinActions = subcommands.getOrDefault("join", List.of());
            List<String> startActions = subcommands.getOrDefault("start", List.of());

            source.sendFeedback(() -> Text.literal("[" + playerName + "]"), false);
            source.sendFeedback(() -> Text.literal("- Join:"), false);
            for (String joinAction : joinActions) {
                source.sendFeedback(() -> Text.literal("    " + joinAction), false);
            }
            source.sendFeedback(() -> Text.literal("- Start:"), false);
            for (String startAction : startActions) {
                source.sendFeedback(() -> Text.literal("    " + startAction), false);
            }
        });
    }

    public static void reload(ServerCommandSource source) {
        PlayerLoader.load();
        source.sendFeedback(() -> Text.literal("Custom players reloaded"), false);
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

    public static void waitForPlayerConnection(ServerCommandSource source, String playerName, Runnable onSuccess, Runnable onTimeout) {
        MinecraftServer server = source.getServer();
        final int[] remaining = {5}; // usamos array para que sea modificable desde la lambda

        final ScheduledFuture<?>[] future = new ScheduledFuture[1]; // para poder cancelarlo desde dentro

        future[0] = scheduler.scheduleAtFixedRate(() -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);

            if (player != null && player.isAlive()) {
                future[0].cancel(false); // Detener el ciclo
                onSuccess.run();
            } else {
                remaining[0]--;
                if (remaining[0] <= 0) {
                    future[0].cancel(false);
                    onTimeout.run();
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }


}