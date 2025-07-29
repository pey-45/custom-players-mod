package com.pey.customplayersmod.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExampleConfigCreator {

    public static void createExampleConfig() {
        File configFile = new File("config/customplayers.json");

        // Crea la carpeta si no existe
        configFile.getParentFile().mkdirs();

        // Crea el archivo solo si no existe
        if (!configFile.exists()) {
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write(
                        """
                        {
                            "ExamplePlayer1": {
                                "join": ["at 1500 130 1500 facing 0 0 in the_nether"],
                                "start": ["sneak", "use continuous"]
                            },
                            "ExamplePlayer2": {
                                "join": ["in survival"]
                                "start": ["turn right"]
                            }
                        }
                        """);
                System.out.println("Example config file for CustomPlayersMod created");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
