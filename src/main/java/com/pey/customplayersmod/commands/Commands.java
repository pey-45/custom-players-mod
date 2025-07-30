package com.pey.customplayersmod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class Commands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("customplayer")
                .requires(source -> true)
                .then(CommandManager.literal("join")
                                .then(CommandManager.argument("playername", StringArgumentType.string()).suggests((context, builder) ->
                                                CommandSuggestions.suggestPlayerNames(CommandSuggestions.subcommand.JOIN, context, builder))
                                        .executes(context -> {
                                            CommandHandler.join(context.getSource(), StringArgumentType.getString(context, "playername"));
                                            return 1;
                                        })
                                )
                )
                .then(CommandManager.literal("start")
                                .then(CommandManager.argument("playername", StringArgumentType.string()).suggests((context, builder) ->
                                                CommandSuggestions.suggestPlayerNames(CommandSuggestions.subcommand.START, context, builder))
                                        .executes(context -> {
                                            CommandHandler.start(context.getSource(), StringArgumentType.getString(context, "playername"));
                                            return 1;
                                        })
                                )
                )
                .then(CommandManager.literal("stop")
                                .then(CommandManager.argument("playername", StringArgumentType.string()).suggests((context, builder) ->
                                                CommandSuggestions.suggestPlayerNames(CommandSuggestions.subcommand.STOP, context, builder))
                                        .executes(context -> {
                                            CommandHandler.stop(context.getSource(), StringArgumentType.getString(context, "playername"));
                                            return 1;
                                        })
                                )
                        )
                .then(CommandManager.literal("leave")
                                .then(CommandManager.argument("playername", StringArgumentType.string()).suggests((context, builder) ->
                                                CommandSuggestions.suggestPlayerNames(CommandSuggestions.subcommand.LEAVE, context, builder))
                                        .executes(context -> {
                                            CommandHandler.leave(context.getSource(), StringArgumentType.getString(context, "playername"));
                                            return 1;
                                        })
                                )
                )
                .then(CommandManager.literal("afkjoin")
                                .then(CommandManager.argument("playername", StringArgumentType.string())
                                        .executes(context -> {
                                            CommandHandler.joinAfk(context.getSource(), StringArgumentType.getString(context, "playername"));
                                            return 1;
                                        })
                                )
                )
                .then(CommandManager.literal("list")
                        .executes(context -> {
                            CommandHandler.listPlayers(context.getSource());
                            return 1;
                        })
                        .then(CommandManager.literal("full")
                                        .executes(context -> {
                                            CommandHandler.listPlayersFull(context.getSource());
                                            return 1;
                                        })
                        )
                )
                .then(CommandManager.literal("reload")
                        .executes(context -> {
                            CommandHandler.reload(context.getSource());
                            return 1;
                        }))
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
                                            
                                            Join commands are preceeded by 'player {playername} spawn '
                                            Start commands are preceeded by 'player {playername} '
                                            Stop and leave commands are 'player {playername} stop/kill'"""
                                    ), false);
                                    return 1;
                                })
                )
        );
    }
}
