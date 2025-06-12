package com.pey.customplayersmod.customPlayersMod.handlers;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SleepHandler {

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static boolean running;

    public static void join(ServerCommandSource source) {
        MinecraftServer server = source.getServer();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer("Sleep");

        if (player != null && player.isAlive()) {
            source.sendFeedback(() ->
                    Text.literal("Sleep is already connected").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        server.execute(() ->
                server.getCommandManager().executeWithPrefix(server.getCommandSource(), "player Sleep spawn at 1500 300 -1500"));

        server.getPlayerManager().broadcast(
                Text.literal("Sleep joined")
                        .styled(style -> style.withColor(Formatting.GREEN)),
                false
        );

        running = false;
    }

    public static void start(ServerCommandSource source) {
        MinecraftServer server = source.getServer();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer("Sleep");

        if (player == null || !player.isAlive()) {
            source.sendFeedback(() ->
                    Text.literal("Sleep is not connected").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        if (running) {
            source.sendFeedback(() ->
                    Text.literal("Sleep already running").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        // Ejecuta: look at
        runWithDelay(0, () ->
                server.execute(() ->
                        server.getCommandManager().executeWithPrefix(server.getCommandSource(), "player Sleep look at 1501.61 300.56 -1499.19")));

        // Ejecuta: sneak
        runWithDelay(1, () ->
                server.execute(() ->
                        server.getCommandManager().executeWithPrefix(server.getCommandSource(), "player Sleep sneak")));

        // Ejecuta: use continuous
        runWithDelay(2, () ->
                server.execute(() ->
                        server.getCommandManager().executeWithPrefix(server.getCommandSource(), "player Sleep use continuous")));

        server.getPlayerManager().broadcast(
                Text.literal("Sleep started")
                        .styled(style -> style.withColor(Formatting.GREEN)),
                false
        );

        running = true;
    }

    public static void stop(ServerCommandSource source) {
        MinecraftServer server = source.getServer();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer("Sleep");

        if (player == null || !player.isAlive()) {
            source.sendFeedback(() ->
                    Text.literal("Sleep is not connected").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        if (!running) {
            source.sendFeedback(() ->
                    Text.literal("Sleep not running").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        server.execute(() -> {
            server.getCommandManager().executeWithPrefix(server.getCommandSource(), "player Sleep stop");
        });

        server.getPlayerManager().broadcast(
                Text.literal("Sleep stopped")
                        .styled(style -> style.withColor(Formatting.GREEN)),
                false
        );

        running = false;
    }

    public static void leave(ServerCommandSource source) {
        MinecraftServer server = source.getServer();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer("Sleep");

        if (player == null || !player.isAlive()) {
            source.sendFeedback(() ->
                    Text.literal("Sleep is not connected").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        server.execute(() ->
                server.getCommandManager().executeWithPrefix(server.getCommandSource(), "player Sleep kill"));

        server.getPlayerManager().broadcast(
                Text.literal("Sleep left")
                        .styled(style -> style.withColor(Formatting.GREEN)),
                false
        );

        running = false;
    }

    private static void runWithDelay(int seconds, Runnable task) {
        scheduler.schedule(task, seconds, TimeUnit.SECONDS);
    }
}
