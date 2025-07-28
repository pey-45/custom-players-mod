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

    public static void join(ServerCommandSource source, String playername) {
        MinecraftServer server = source.getServer();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playername);

        if (player != null && player.isAlive()) {
            source.sendFeedback(() ->
                    Text.literal(playername + " is already connected").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        executeCommands(server, playername, PlayerCommandsLoader.getCommands(playername, "leave"));

        server.getPlayerManager().broadcast(
                Text.literal(playername + " joined")
                        .styled(style -> style.withColor(Formatting.GREEN)),
                false
        );

        running = false;
    }

    public static void start(ServerCommandSource source, String playername) {
        MinecraftServer server = source.getServer();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playername);

        if (player == null || !player.isAlive()) {
            join(source, playername);
        }

        if (running) {
            source.sendFeedback(() ->
                    Text.literal(playername + " is already running").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        executeCommands(server, playername, PlayerCommandsLoader.getCommands(playername, "start"));

        server.getPlayerManager().broadcast(
                Text.literal(playername + " started")
                        .styled(style -> style.withColor(Formatting.GREEN)),
                false
        );

        running = true;
    }

    public static void stop(ServerCommandSource source, String playername) {
        MinecraftServer server = source.getServer();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playername);

        if (player == null || !player.isAlive()) {
            source.sendFeedback(() ->
                    Text.literal(playername + " is not connected").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        if (!running) {
            source.sendFeedback(() ->
                    Text.literal(playername + " is not running").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        executeCommands(server, playername, PlayerCommandsLoader.getCommands(playername, "stop"));

        server.getPlayerManager().broadcast(
                Text.literal(playername + " stopped")
                        .styled(style -> style.withColor(Formatting.GREEN)),
                false
        );

        running = false;
    }

    public static void leave(ServerCommandSource source, String playername) {
        MinecraftServer server = source.getServer();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playername);

        if (player == null || !player.isAlive()) {
            source.sendFeedback(() ->
                    Text.literal(playername + " is not connected").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        executeCommands(server, playername, PlayerCommandsLoader.getCommands(playername, "leave"));

        server.getPlayerManager().broadcast(
                Text.literal(playername + " left")
                        .styled(style -> style.withColor(Formatting.GREEN)),
                false
        );

        running = false;
    }

    private static void executeCommands(MinecraftServer server, String playername, List<String> commands) {
        int delay = 0;
        for (String command : commands) {
            scheduler.schedule(
                    () -> server.execute(() -> server.getCommandManager().executeWithPrefix(
                            server.getCommandSource(),
                            "player " + playername + " " + command)
                    ),
                    delay++,
                    TimeUnit.SECONDS
            );
        }
    }
}