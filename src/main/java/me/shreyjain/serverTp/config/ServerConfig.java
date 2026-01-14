package me.shreyjain.serverTp.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.logger.HytaleLogger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ServerConfig {

    private static final String FILE = "config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path dataDirectory;
    private final HytaleLogger logger;
    private ConfigData config;

    public ServerConfig(@Nonnull Path dir, @Nonnull HytaleLogger logger) {
        this.dataDirectory = dir;
        this.logger = logger;
        load();
    }

    private void load() {
        Path file = dataDirectory.resolve(FILE);
        if (!Files.exists(file)) {
            config = new ConfigData();
            save();
            return;
        }

        try {
            config = GSON.fromJson(Files.readString(file), ConfigData.class);
        } catch (IOException e) {
            logger.atSevere().log("Failed to load config: %s", e.getMessage());
            config = new ConfigData();
        }
    }

    public void save() {
        try {
            Files.createDirectories(dataDirectory);
            Files.writeString(
                    dataDirectory.resolve(FILE),
                    GSON.toJson(config)
            );
        } catch (IOException e) {
            logger.atSevere().log("Failed to save config: %s", e.getMessage());
        }
    }

    public String serverAdded() { return config.serverAdded; }
    public String serverRemoved() { return config.serverRemoved; }
    public String serverNotFound() { return config.serverNotFound; }
    public String serverList() { return config.serverList; }
    public String connecting() { return config.connecting; }

    public static class ConfigData {
        public String serverAdded = "Server '%s' added.";
        public String serverRemoved = "Server '%s' removed.";
        public String serverNotFound = "Server '%s' not found.";
        public String serverList = "Servers (%d): %s";
        public String connecting = "Connecting to %s (%s:%d)";
    }
}
