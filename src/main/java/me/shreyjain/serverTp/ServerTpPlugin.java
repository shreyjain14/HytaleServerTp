package me.shreyjain.serverTp;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import me.shreyjain.serverTp.commands.*;
import me.shreyjain.serverTp.config.ServerConfig;
import me.shreyjain.serverTp.manager.ServerManager;

import javax.annotation.Nonnull;
import java.util.logging.Level;

public class ServerTpPlugin extends JavaPlugin {

    private ServerManager serverManager;
    private ServerConfig config;

    public ServerTpPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        getLogger().at(Level.INFO).log("ServerTP is setting up...");

        this.config = new ServerConfig(getDataDirectory(), getLogger());
        this.serverManager = new ServerManager(getDataDirectory(), getLogger());

        getCommandRegistry().registerCommand(new ServerCommand(serverManager, config));
        getCommandRegistry().registerCommand(new ServersCommand(serverManager, config));
        getCommandRegistry().registerCommand(new AddServerCommand(serverManager, config));
        getCommandRegistry().registerCommand(new DelServerCommand(serverManager, config));

        getLogger().at(Level.INFO).log("ServerTP loaded successfully.");
    }

    @Override
    protected void shutdown() {
        if (serverManager != null) {
            serverManager.save();
        }
    }
}
