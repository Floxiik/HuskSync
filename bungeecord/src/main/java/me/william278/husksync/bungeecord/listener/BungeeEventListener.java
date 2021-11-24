package me.william278.husksync.bungeecord.listener;

import me.william278.husksync.HuskSyncBungeeCord;
import me.william278.husksync.PlayerData;
import me.william278.husksync.SynchronisationCluster;
import me.william278.husksync.bungeecord.data.DataManager;
import me.william278.husksync.Settings;
import me.william278.husksync.redis.MessageTarget;
import me.william278.husksync.redis.RedisMessage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

public class BungeeEventListener implements Listener {

    private static final HuskSyncBungeeCord plugin = HuskSyncBungeeCord.getInstance();

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            // Ensure the player has data on SQL and that it is up-to-date
            DataManager.ensurePlayerExists(player.getUniqueId(), player.getName());

            // Get the player's data from SQL
            final Map<SynchronisationCluster,PlayerData> data = DataManager.getPlayerData(player.getUniqueId());

            // Update the player's data from SQL onto the cache
            assert data != null;
            for (SynchronisationCluster cluster : data.keySet()) {
                DataManager.playerDataCache.get(cluster).updatePlayer(data.get(cluster));
            }

            // Send a message asking the bukkit to request data on join
            try {
                new RedisMessage(RedisMessage.MessageType.REQUEST_DATA_ON_JOIN,
                        new MessageTarget(Settings.ServerType.BUKKIT, null, null),
                        RedisMessage.RequestOnJoinUpdateType.ADD_REQUESTER.toString(), player.getUniqueId().toString()).send();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to serialize request data on join message data");
                e.printStackTrace();
            }
        });
    }

}
