package com.pey.backupmod.customPlayersMod.handlers;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AfkHandler {

    public static void join(ServerCommandSource source, String name) {
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

        source.sendFeedback(() ->
                Text.literal("afk" + name + " joined").styled(style -> style.withColor(Formatting.GREEN)), false);
    }

    public static void leave(ServerCommandSource source, String name) {
        MinecraftServer server = source.getServer();

        ServerPlayerEntity player = server.getPlayerManager().getPlayer("afk" + name);

        if (player == null || !player.isAlive()) {
            source.sendFeedback(() ->
                    Text.literal("afk" + name + " is not connected").styled(style -> style.withColor(Formatting.RED)), false);
            return;
        }

        server.execute(() ->
                server.getCommandManager().executeWithPrefix(server.getCommandSource(), "player afk" + name + " kill"));

        source.sendFeedback(() ->
                Text.literal("afk" + name + " left").styled(style -> style.withColor(Formatting.GREEN)), false);
    }
}
