package me.shreyjain.serverTp.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.shreyjain.serverTp.config.ServerConfig;
import me.shreyjain.serverTp.manager.ServerManager;

import javax.annotation.Nonnull;

public class ServerCommand extends AbstractPlayerCommand {

    private final ServerManager manager;
    private final ServerConfig config;
    private final RequiredArg<String> serverIdArg;

    public ServerCommand(
            @Nonnull ServerManager manager,
            @Nonnull ServerConfig config
    ) {
        super("server", "Connect to a server. Usage: /server <name>");
        this.manager = manager;
        this.config = config;
        this.serverIdArg = this.withRequiredArg(
                "name",
                "Server name",
                ArgTypes.STRING
        );
    }

    @Override
    protected void execute(
            @Nonnull CommandContext commandContext,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
    ) {
        String id = commandContext.get(serverIdArg).toLowerCase();
        ServerManager.ServerLocation server = manager.getServer(id);

        if (server == null) {
            commandContext.sendMessage(
                    Message.raw(String.format(config.serverNotFound(), id))
            );
            return;
        }

        commandContext.sendMessage(
                Message.raw(
                        String.format(
                                config.connecting(),
                                server.id,
                                server.ip,
                                server.port
                        )
                )
        );

        playerRef.referToServer(server.ip, server.port);
    }
}
