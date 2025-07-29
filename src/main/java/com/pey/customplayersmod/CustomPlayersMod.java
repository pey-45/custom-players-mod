package com.pey.customplayersmod;

import com.pey.customplayersmod.config.ExampleConfigCreator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class CustomPlayersMod implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> CustomPlayersCommands.register(dispatcher));
        ExampleConfigCreator.createExampleConfig();
    }
}
