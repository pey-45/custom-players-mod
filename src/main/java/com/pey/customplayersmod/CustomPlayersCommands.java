package com.pey.customplayersmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.pey.customplayersmod.handlers.AfkHandler;
import com.pey.customplayersmod.handlers.GenericHandler;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class CustomPlayersCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("customplayers")
                .requires(source -> true)
                .then(CommandManager.argument("name", StringArgumentType.string())
                        .then(CommandManager.literal("join")
                                .executes(context -> {
                                    GenericHandler.join(context.getSource(), StringArgumentType.getString(context, "playername"));
                                    return 1;
                                })
                        )
                        .then(CommandManager.literal("start")
                                .executes(context -> {
                                    GenericHandler.start(context.getSource(), StringArgumentType.getString(context, "playername"));
                                    return 1;
                                })

                        )
                        .then(CommandManager.literal("stop")
                                .executes(context -> {
                                    GenericHandler.stop(context.getSource(), StringArgumentType.getString(context, "playername"));
                                    return 1;
                                })

                        )
                        .then(CommandManager.literal("leave")
                                .executes(context -> {
                                    GenericHandler.leave(context.getSource(), StringArgumentType.getString(context, "playername"));
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
                .then(CommandManager.literal("help")
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            source.sendFeedback(() -> Text.literal("""
                                [CustomPlayersMod]
                                To configure custom players, edit the server config file:
                                config/customplayersmod/commands.json
                                
                                Example:
                                {
                                  "CustomPlayer1": {
                                    "join": ["spawn at 1500 100 1500"],
                                    "start": ["sneak"],
                                    "stop": ["stop"],
                                    "leave": ["kill"]
                                  }
                                }
                                
                                These commands will be preceeded by "player {playername} " to prevent unwanted use.
                                """), false);

                        }))
        );
    }
}
