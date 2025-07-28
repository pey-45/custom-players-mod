package com.pey.customplayersmod.loaders;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class PlayerCommandsLoader {
    private static Map<String, Map<String, List<String>>> config = Map.of(); // jugador → subcomando → acciones

    static {
        load();
    }

    private static void load() {
        try {
            var file = Paths.get("config", "customplayersmod", "commands.json").toFile();
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Map<String, List<String>>>>() {}.getType();
            config = gson.fromJson(new FileReader(file), type);
        } catch (Exception e) {
            e.printStackTrace();
            config = Map.of();
        }
    }

    public static List<String> getCommands(String playerName, String subcommand) {
        return config.getOrDefault(playerName, Map.of())
                .getOrDefault(subcommand, List.of());
    }
}
