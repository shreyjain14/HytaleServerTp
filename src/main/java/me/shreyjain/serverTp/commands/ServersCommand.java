package me.shreyjain.serverTp.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.shreyjain.serverTp.config.ServerConfig;
import me.shreyjain.serverTp.manager.ServerManager;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

public class ServersCommand extends AbstractPlayerCommand {

    private final ServerManager manager;
    private final ServerConfig config;

    public ServersCommand(
            @Nonnull ServerManager manager,
            @Nonnull ServerConfig config
    ) {
        super("servers", "List all available servers");
        this.manager = manager;
        this.config = config;
    }

    @Override
    protected void execute(
            @Nonnull CommandContext commandContext,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
    ) {
        if (manager.getServers().isEmpty()) {
            commandContext.sendMessage(
                    Message.raw("No servers available.")
            );
            return;
        }

        String list = manager.getServers().values().stream()
                .map(s -> s.id + " - " + s.description)
                .collect(Collectors.joining(", "));

        commandContext.sendMessage(
                Message.raw(
                        String.format(
                                config.serverList(),
                                manager.getServers().size(),
                                list
                        )
                )
        );
    }
}
