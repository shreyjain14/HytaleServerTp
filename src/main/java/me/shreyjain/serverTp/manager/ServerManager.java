package me.shreyjain.serverTp.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.logger.HytaleLogger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerManager {

    private static final String FILE = "servers.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE =
            new TypeToken<Map<String, ServerLocation>>() {}.getType();

    private final Path dataDirectory;
    private final HytaleLogger logger;
    private final Map<String, ServerLocation> servers = new ConcurrentHashMap<>();

    public ServerManager(@Nonnull Path dataDirectory, @Nonnull HytaleLogger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        load();
    }

    private void load() {
        Path file = dataDirectory.resolve(FILE);
        if (!Files.exists(file, new LinkOption[0])) {
            logger.atInfo().log("No servers file found, starting fresh.");
            return;
        }

        try {
            String json = Files.readString(file);
            Map<String, ServerLocation> loaded = GSON.fromJson(json, TYPE);
            if (loaded != null) {
                loaded.forEach((k, v) -> servers.put(k.toLowerCase(), v));
            }
            logger.atInfo().log("Loaded %d servers.", servers.size());
        } catch (IOException e) {
            logger.atSevere().log("Failed to load servers: %s", e.getMessage());
        }
    }

    public void save() {
        try {
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }
            Files.writeString(
                    dataDirectory.resolve(FILE),
                    GSON.toJson(servers, TYPE)
            );
        } catch (IOException e) {
            logger.atSevere().log("Failed to save servers: %s", e.getMessage());
        }
    }

    /* API */

    public void addServer(@Nonnull ServerLocation server) {
        servers.put(server.id.toLowerCase(), server);
        save();
    }

    public boolean removeServer(@Nonnull String id) {
        ServerLocation removed = servers.remove(id.toLowerCase());
        if (removed != null) {
            save();
            return true;
        }
        return false;
    }

    @Nullable
    public ServerLocation getServer(@Nonnull String id) {
        return servers.get(id.toLowerCase());
    }

    @Nonnull
    public Set<String> getServerIds() {
        return Collections.unmodifiableSet(servers.keySet());
    }

    @Nonnull
    public Map<String, ServerLocation> getServers() {
        return Collections.unmodifiableMap(servers);
    }

    /* Data */

    public static final class ServerLocation {
        public static final int DEFAULT_PORT = 5520;

        public final String id;
        public final String description;
        public final String ip;
        public final int port;

        public ServerLocation(
                @Nonnull String id,
                @Nonnull String description,
                @Nonnull String ip,
                int port
        ) {
            this.id = id;
            this.description = description;
            this.ip = ip;
            this.port = port <= 0 ? DEFAULT_PORT : port;
        }
    }
}
