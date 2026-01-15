package me.shreyjain.serverTp.gui;

import java.util.Map;

import javax.annotation.Nonnull;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import me.shreyjain.serverTp.config.ServerConfig;
import me.shreyjain.serverTp.manager.ServerManager;

/**
 * Server List GUI
 *
 * Interactive UI that displays available servers with join buttons
 */
public class ServersGui extends InteractiveCustomUIPage<ServersGui.ServerEventData> {

    private final ServerManager manager;
    private final ServerConfig config;

    /**
     * EventData class - holds the server ID when a join button is clicked
     */
    public static class ServerEventData {
        public String serverId;

        public static final BuilderCodec<ServerEventData> CODEC =
                BuilderCodec.builder(ServerEventData.class, ServerEventData::new)
                        .append(
                                new KeyedCodec<>("ServerId", Codec.STRING),
                                (ServerEventData obj, String val) -> obj.serverId = val,
                                (ServerEventData obj) -> obj.serverId
                        )
                        .add()
                        .build();
    }

    public ServersGui(@Nonnull PlayerRef playerRef, @Nonnull ServerManager manager, @Nonnull ServerConfig config) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, ServerEventData.CODEC);
        this.manager = manager;
        this.config = config;
    }

    @Override
    public void build(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull UICommandBuilder cmd,
            @Nonnull UIEventBuilder evt,
            @Nonnull Store<EntityStore> store
    ) {
        // Load the base UI file
        cmd.append("Pages/ServerList.ui");

        Map<String, ServerManager.ServerLocation> servers = manager.getServers();

        if (servers.isEmpty()) {
            cmd.appendInline("#ServerListContainer", "Label { Text: \"No servers configured\"; Anchor: (Height: 40); Style: (FontSize: 14, TextColor: #6e7da1, HorizontalAlignment: Center, VerticalAlignment: Center); }");
            return;
        }

        // Add each server entry dynamically
        int i = 0;
        for (Map.Entry<String, ServerManager.ServerLocation> entry : servers.entrySet()) {
            String serverId = entry.getKey();
            ServerManager.ServerLocation server = entry.getValue();
            String selector = "#ServerListContainer[" + i + "]";
            
            // Append a server entry UI
            cmd.append("#ServerListContainer", "Pages/ServerEntry.ui");
            
            // Set the server name, description, and address
            cmd.set(selector + " #ServerName.Text", serverId);
            cmd.set(selector + " #ServerDescription.Text", server.description);
            cmd.set(selector + " #ServerAddress.Text", server.ip + ":" + server.port);
            
            // Bind the join button to send the server ID
            evt.addEventBinding(
                CustomUIEventBindingType.Activating,
                selector + " #JoinButton",
                new EventData().append("ServerId", serverId),
                false
            );
            
            i++;
        }
    }

    @Override
    public void handleDataEvent(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store,
            @Nonnull ServerEventData data
    ) {
        Player player = store.getComponent(ref, Player.getComponentType());

        if (data.serverId == null || data.serverId.isEmpty()) {
            playerRef.sendMessage(Message.raw("Error: No server selected"));
            player.getPageManager().setPage(ref, store, Page.None);
            return;
        }

        // Get the server from manager
        ServerManager.ServerLocation server = manager.getServer(data.serverId.toLowerCase());
        
        if (server == null) {
            playerRef.sendMessage(Message.raw(String.format(config.serverNotFound(), data.serverId)));
            player.getPageManager().setPage(ref, store, Page.None);
            return;
        }

        // Send connection message
        playerRef.sendMessage(Message.raw(
                String.format(
                        config.connecting(),
                        server.id,
                        server.ip,
                        server.port
                )
        ));

        // Close the UI
        player.getPageManager().setPage(ref, store, Page.None);

        // Connect to the server
        playerRef.referToServer(server.ip, server.port);
    }
}