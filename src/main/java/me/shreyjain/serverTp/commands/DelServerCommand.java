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

public class DelServerCommand extends AbstractPlayerCommand {

    private final ServerManager manager;
    private final ServerConfig config;
    private final RequiredArg<String> idArg;

    public DelServerCommand(
            @Nonnull ServerManager manager,
            @Nonnull ServerConfig config
    ) {
        super("serverdel", "Delete a server");
        this.manager = manager;
        this.config = config;
        this.idArg = withRequiredArg("id", "Server id", ArgTypes.STRING);
    }

    @Override
    protected void execute(
            @Nonnull CommandContext commandContext,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
    ) {
        if (!commandContext.sender().hasPermission("servertp.manage")) {
            commandContext.sendMessage(Message.raw("You do not have permission."));
            return;
        }

        String id = commandContext.get(idArg).toLowerCase();

        if (!manager.removeServer(id)) {
            commandContext.sendMessage(
                    Message.raw(String.format(config.serverNotFound(), id))
            );
            return;
        }

        commandContext.sendMessage(
                Message.raw(String.format(config.serverRemoved(), id))
        );
    }
}
