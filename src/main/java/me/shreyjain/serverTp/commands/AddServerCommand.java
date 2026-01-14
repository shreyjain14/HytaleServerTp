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

public class AddServerCommand extends AbstractPlayerCommand {

    private final ServerManager manager;
    private final ServerConfig config;

    private final RequiredArg<String> idArg;
    private final RequiredArg<String> ipArg;
    private final RequiredArg<String> portArg;
    private final RequiredArg<String> descArg;

    public AddServerCommand(
            @Nonnull ServerManager manager,
            @Nonnull ServerConfig config
    ) {
        super("serveradd", "Add a server. Usage: /serveradd <id> <ip> <port> <desc>");
        this.manager = manager;
        this.config = config;

        this.idArg = withRequiredArg("id", "Server id", ArgTypes.STRING);
        this.ipArg = withRequiredArg("ip", "Server IP", ArgTypes.STRING);
        this.portArg = withRequiredArg("port", "Server port", ArgTypes.STRING);
        this.descArg = withRequiredArg("desc", "Description", ArgTypes.STRING);
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
        String ip = commandContext.get(ipArg);
        String portStr = commandContext.get(portArg);
        String desc = commandContext.get(descArg);

        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            commandContext.sendMessage(
                    Message.raw("Invalid port number: " + portStr)
            );
            return;
        }

        manager.addServer(
                new ServerManager.ServerLocation(id, desc, ip, port)
        );

        commandContext.sendMessage(
                Message.raw(String.format(config.serverAdded(), id))
        );
    }
}
