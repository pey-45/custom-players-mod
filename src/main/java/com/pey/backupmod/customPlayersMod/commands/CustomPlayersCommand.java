package com.pey.backupmod.customPlayersMod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class RootCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("customplayers")
                .then(CommandManager.literal("join")
                        .then(CommandManager.argument("player", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    SleepHandler.join(context.getSource());
                                    return 1;
                                })
                        )
                )
                .then(CommandManager.literal("start")
                        .then(CommandManager.argument("player", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    SleepHandler.start(context.getSource());
                                    return 1;
                                })
                        )
                )
                .then(CommandManager.literal("stop")
                        .then(CommandManager.argument("player", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    SleepHandler.stop(context.getSource());
                                    return 1;
                                })
                        )
                )
                .then(CommandManager.literal("leave")
                        .then(CommandManager.argument("player", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    SleepHandler.leave(context.getSource());
                                    return 1;
                                })
                        )
                )
        );
    }
}
