package com.pey.customplayersmod.config;

import java.util.HashSet;
import java.util.Set;

public class PlayerStateManager {

    private static final Set<String> startedPlayers = new HashSet<>();

    public static void markStarted(String playerName) {
        startedPlayers.add(playerName);
    }

    public static void markStopped(String playerName) {
        startedPlayers.remove(playerName);
    }

    public static boolean isStarted(String playerName) {
        return startedPlayers.contains(playerName);
    }

}
