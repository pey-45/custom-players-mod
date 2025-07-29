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
        dispatcher.register(CommandManager.literal("customplayer")
                .requires(source -> true)
                .then(CommandManager.literal("player")
                        .then(CommandManager.argument("playername", StringArgumentType.string())
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
                )
                .then(CommandManager.literal("afk")
                        .then(CommandManager.argument("playername", StringArgumentType.string())
                                .then(CommandManager.literal("join")
                                        .executes(context -> {
                                            AfkHandler.join(context.getSource(), StringArgumentType.getString(context, "playername"));
                                            return 1;
                                        })
                                )
                                .then(CommandManager.literal("leave")
                                        .executes(context -> {
                                            AfkHandler.leave(context.getSource(), StringArgumentType.getString(context, "playername"));
                                            return 1;
                                        })
                                )
                        )
                )
                .then(CommandManager.literal("help")
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            source.sendFeedback(() -> Text.literal(
                                """
                                [CustomPlayersMod]
                                To configure custom players, edit the server config file:
                                config/customplayers.json
                                
                                Example:
                                {
                                  "ExamplePlayer1": {
                                    "join": ["at 1500 130 1500 facing 0 0 in the_nether"],
                                    "start": ["sneak", "use continuous"]
                                  },
                                  "ExamplePlayer2": {
                                    "join": ["in survival"],
                                    "start": ["turn right"]
                                  },
                                  ...
                                }
                                
                                Join commands are preceeded by "player {playername} spawn "
                                Start commands are preceeded by "player {playername} "
                                Stop and leave commands are "player {playername} stop/kill"
                                """
                            ), false);
                            return 1;
                        }))
        );
    }
}
