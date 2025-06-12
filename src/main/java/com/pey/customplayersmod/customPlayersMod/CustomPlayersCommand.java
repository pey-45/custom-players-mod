package com.pey.customplayersmod.customPlayersMod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.pey.customplayersmod.customPlayersMod.handlers.AfkHandler;
import com.pey.customplayersmod.customPlayersMod.handlers.SleepHandler;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class CustomPlayersCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("customplayers")
                .requires(source -> true)
                .then(CommandManager.literal("sleep")
                        .then(CommandManager.literal("join")
                                .executes(context -> {
                                    SleepHandler.join(context.getSource());
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("start")
                                .executes(context -> {
                                    SleepHandler.start(context.getSource());
                                    return 1;
                                })

                        )
                        .then(CommandManager.literal("stop")
                                .executes(context -> {
                                    SleepHandler.stop(context.getSource());
                                    return 1;
                                })

                        )
                        .then(CommandManager.literal("leave")
                                .executes(context -> {
                                    SleepHandler.leave(context.getSource());
                                    return 1;
                                })
                        )
                )
                .then(CommandManager.literal("afk")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .then(CommandManager.literal("join")
                                        .executes(context -> {
                                            AfkHandler.join(context.getSource(), StringArgumentType.getString(context, "name"));
                                            return 1;
                                        })
                                )
                                .then(CommandManager.literal("leave")
                                        .executes(context -> {
                                            AfkHandler.leave(context.getSource(), StringArgumentType.getString(context, "name"));
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}
