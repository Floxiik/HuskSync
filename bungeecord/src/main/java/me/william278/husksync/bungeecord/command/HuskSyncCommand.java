package me.william278.husksync.bungeecord.command;

import de.themoep.minedown.MineDown;
import me.william278.husksync.HuskSyncBungeeCord;
import me.william278.husksync.SynchronisationCluster;
import me.william278.husksync.bungeecord.util.BungeeUpdateChecker;
import me.william278.husksync.redis.MessageTarget;
import me.william278.husksync.util.MessageManager;
import me.william278.husksync.PlayerData;
import me.william278.husksync.Settings;
import me.william278.husksync.bungeecord.config.ConfigLoader;
import me.william278.husksync.bungeecord.config.ConfigManager;
import me.william278.husksync.bungeecord.data.DataManager;
import me.william278.husksync.redis.RedisMessage;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class HuskSyncCommand extends Command implements TabExecutor {

    private final static HuskSyncBungeeCord plugin = HuskSyncBungeeCord.getInstance();
    private final static SubCommand[] SUB_COMMANDS = {new SubCommand("about", null),
            new SubCommand("status", "husksync.command.admin"),
            new SubCommand("reload", "husksync.command.admin"),
            new SubCommand("update", "husksync.command.admin"),
            new SubCommand("invsee", "husksync.command.inventory"),
            new SubCommand("echest", "husksync.command.ender_chest")};

    public HuskSyncCommand() {
        super("husksync", null, "hs");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (HuskSyncBungeeCord.synchronisedServers.size() == 0) {
                player.sendMessage(new MineDown(MessageManager.getMessage("error_no_servers_proxied")).toComponent());
                return;
            }
            if (args.length >= 1) {
                String clusterId;
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "about":
                    case "info":
                        sendAboutInformation(player);
                        break;
                    case "invsee":
                    case "openinv":
                    case "inventory":
                        if (!player.hasPermission("husksync.command.inventory")) {
                            sender.sendMessage(new MineDown(MessageManager.getMessage("error_no_permission")).toComponent());
                            return;
                        }
                        if (Settings.clusters.size() > 1) {
                            if (args.length == 3) {
                                clusterId = args[2];
                            } else {
                                sender.sendMessage(new MineDown(MessageManager.getMessage("error_invalid_cluster")).toComponent());
                                return;
                            }
                        } else {
                            clusterId = "main";
                            for (SynchronisationCluster cluster : Settings.clusters) {
                                clusterId = cluster.clusterId();
                                break;
                            }
                        }
                        if (args.length == 2 || args.length == 3) {
                            String playerName = args[1];
                            openInventory(player, playerName, clusterId);
                        } else {
                            sender.sendMessage(new MineDown(MessageManager.getMessage("error_invalid_syntax").replaceAll("%1%",
                                    "/husksync invsee <player>")).toComponent());
                        }
                    break;
                    case "echest":
                    case "enderchest":
                        if (!player.hasPermission("husksync.command.ender_chest")) {
                            sender.sendMessage(new MineDown(MessageManager.getMessage("error_no_permission")).toComponent());
                            return;
                        }
                        if (Settings.clusters.size() > 1) {
                            if (args.length == 3) {
                                clusterId = args[2];
                            } else {
                                sender.sendMessage(new MineDown(MessageManager.getMessage("error_invalid_cluster")).toComponent());
                                return;
                            }
                        } else {
                            clusterId = "main";
                            for (SynchronisationCluster cluster : Settings.clusters) {
                                clusterId = cluster.clusterId();
                                break;
                            }
                        }
                        if (args.length == 2 || args.length == 3) {
                            String playerName = args[1];
                            openEnderChest(player, playerName, clusterId);
                        } else {
                            sender.sendMessage(new MineDown(MessageManager.getMessage("error_invalid_syntax")
                                    .replaceAll("%1%", "/husksync echest <player>")).toComponent());
                        }
                    break;
                    case "status":
                        if (!player.hasPermission("husksync.command.admin")) {
                            sender.sendMessage(new MineDown(MessageManager.getMessage("error_no_permission")).toComponent());
                            return;
                        }
                        int playerDataSize = 0;
                        for (SynchronisationCluster cluster : Settings.clusters) {
                            playerDataSize += DataManager.playerDataCache.get(cluster).playerData.size();
                        }
                        sender.sendMessage(new MineDown(MessageManager.PLUGIN_STATUS.toString()
                                .replaceAll("%1%", String.valueOf(HuskSyncBungeeCord.synchronisedServers.size()))
                                .replaceAll("%2%", String.valueOf(playerDataSize))).toComponent());
                    break;
                    case "reload":
                        if (!player.hasPermission("husksync.command.admin")) {
                            sender.sendMessage(new MineDown(MessageManager.getMessage("error_no_permission")).toComponent());
                            return;
                        }
                        ConfigManager.loadConfig();
                        ConfigLoader.loadSettings(Objects.requireNonNull(ConfigManager.getConfig()));

                        ConfigManager.loadMessages();
                        ConfigLoader.loadMessageStrings(Objects.requireNonNull(ConfigManager.getMessages()));

                        // Send reload request to all bukkit servers
                        try {
                            new RedisMessage(RedisMessage.MessageType.RELOAD_CONFIG,
                                    new MessageTarget(Settings.ServerType.BUKKIT, null, null),
                                    "reload")
                                    .send();
                        } catch (IOException e) {
                            plugin.getLogger().log(Level.WARNING, "Failed to serialize reload notification message data");
                        }

                        sender.sendMessage(new MineDown(MessageManager.getMessage("reload_complete")).toComponent());
                    break;
                    default:
                        sender.sendMessage(new MineDown(MessageManager.getMessage("error_invalid_syntax").replaceAll("%1%","/husksync <about/status/invsee/echest>")).toComponent());
                }
            } else {
                sendAboutInformation(player);
            }
        } else {
            sender.sendMessage(new MineDown("Error: Invalid syntax. Usage: husksync migrate <args>").toComponent());
        }
    }

    // View the inventory of a player specified by their name
    private void openInventory(ProxiedPlayer viewer, String targetPlayerName, String clusterId) {
        if (viewer.getName().equalsIgnoreCase(targetPlayerName)) {
            viewer.sendMessage(new MineDown(MessageManager.getMessage("error_cannot_view_own_ender_chest")).toComponent());
            return;
        }
        if (ProxyServer.getInstance().getPlayer(targetPlayerName) != null) {
            viewer.sendMessage(new MineDown(MessageManager.getMessage("error_cannot_view_inventory_online")).toComponent());
            return;
        }
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            for (SynchronisationCluster cluster : Settings.clusters) {
                if (!cluster.clusterId().equals(clusterId)) continue;
                PlayerData playerData = DataManager.getPlayerDataByName(targetPlayerName, cluster.clusterId());
                if (playerData == null) {
                    viewer.sendMessage(new MineDown(MessageManager.getMessage("error_invalid_player")).toComponent());
                    return;
                }
                try {
                    new RedisMessage(RedisMessage.MessageType.OPEN_INVENTORY,
                            new MessageTarget(Settings.ServerType.BUKKIT, viewer.getUniqueId(), null),
                            targetPlayerName, RedisMessage.serialize(playerData))
                            .send();
                    viewer.sendMessage(new MineDown(MessageManager.getMessage("viewing_inventory_of").replaceAll("%1%",
                            targetPlayerName)).toComponent());
                } catch (IOException e) {
                    plugin.getLogger().log(Level.WARNING, "Failed to serialize inventory-see player data", e);
                }
                return;
            }
            viewer.sendMessage(new MineDown(MessageManager.getMessage("error_invalid_cluster")).toComponent());
        });
    }

    // View the ender chest of a player specified by their name
    private void openEnderChest(ProxiedPlayer viewer, String targetPlayerName, String clusterId) {
        if (viewer.getName().equalsIgnoreCase(targetPlayerName)) {
            viewer.sendMessage(new MineDown(MessageManager.getMessage("error_cannot_view_own_ender_chest")).toComponent());
            return;
        }
        if (ProxyServer.getInstance().getPlayer(targetPlayerName) != null) {
            viewer.sendMessage(new MineDown(MessageManager.getMessage("error_cannot_view_ender_chest_online")).toComponent());
            return;
        }
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            for (SynchronisationCluster cluster : Settings.clusters) {
                if (!cluster.clusterId().equals(clusterId)) continue;
                PlayerData playerData = DataManager.getPlayerDataByName(targetPlayerName, cluster.clusterId());
                if (playerData == null) {
                    viewer.sendMessage(new MineDown(MessageManager.getMessage("error_invalid_player")).toComponent());
                    return;
                }
                try {
                    new RedisMessage(RedisMessage.MessageType.OPEN_ENDER_CHEST,
                            new MessageTarget(Settings.ServerType.BUKKIT, viewer.getUniqueId(), null),
                            targetPlayerName, RedisMessage.serialize(playerData))
                            .send();
                    viewer.sendMessage(new MineDown(MessageManager.getMessage("viewing_ender_chest_of").replaceAll("%1%",
                            targetPlayerName)).toComponent());
                } catch (IOException e) {
                    plugin.getLogger().log(Level.WARNING, "Failed to serialize inventory-see player data", e);
                }
                return;
            }
            viewer.sendMessage(new MineDown(MessageManager.getMessage("error_invalid_cluster")).toComponent());
        });
    }

    /**
     * Send information about the plugin
     *
     * @param player The player to send it to
     */
    private void sendAboutInformation(ProxiedPlayer player) {
        try {
            new RedisMessage(RedisMessage.MessageType.SEND_PLUGIN_INFORMATION,
                    new MessageTarget(Settings.ServerType.BUKKIT, player.getUniqueId(), null),
                    plugin.getProxy().getName(), plugin.getDescription().getVersion()).send();
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to serialize plugin information to send", e);
        }
    }

    // Tab completion
    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (args.length == 1) {
                final ArrayList<String> subCommands = new ArrayList<>();
                for (SubCommand subCommand : SUB_COMMANDS) {
                    if (subCommand.doesPlayerHavePermission(player)) {
                        subCommands.add(subCommand.command());
                    }
                }
                // Automatically filter the sub commands' order in tab completion by what the player has typed
                return subCommands.stream().filter(val -> val.startsWith(args[0]))
                        .sorted().collect(Collectors.toList());
            } else {
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

}
