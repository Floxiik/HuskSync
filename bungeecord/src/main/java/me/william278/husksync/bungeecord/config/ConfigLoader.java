package me.william278.husksync.bungeecord.config;

import me.william278.husksync.SynchronisationCluster;
import me.william278.husksync.util.MessageManager;
import me.william278.husksync.Settings;
import net.md_5.bungee.config.Configuration;

import java.util.HashMap;

public class ConfigLoader {

    public static void loadSettings(Configuration config) throws IllegalArgumentException {
        Settings.language = config.getString("language", "en-gb");

        Settings.serverType = Settings.ServerType.BUNGEECORD;
        Settings.automaticUpdateChecks = config.getBoolean("check_for_updates", true);
        Settings.redisHost = config.getString("redis_settings.host", "localhost");
        Settings.redisPort = config.getInt("redis_settings.port", 6379);
        Settings.redisPassword = config.getString("redis_settings.password", "");

        Settings.dataStorageType = Settings.DataStorageType.valueOf(config.getString("data_storage_settings.database_type", "sqlite").toUpperCase());
        if (Settings.dataStorageType == Settings.DataStorageType.MYSQL) {
            Settings.mySQLHost = config.getString("data_storage_settings.mysql_settings.host", "localhost");
            Settings.mySQLPort = config.getInt("data_storage_settings.mysql_settings.port", 3306);
            Settings.mySQLDatabase = config.getString("data_storage_settings.mysql_settings.database", "HuskSync");
            Settings.mySQLUsername = config.getString("data_storage_settings.mysql_settings.username", "root");
            Settings.mySQLPassword = config.getString("data_storage_settings.mysql_settings.password", "pa55w0rd");
            Settings.mySQLParams = config.getString("data_storage_settings.mysql_settings.params", "?autoReconnect=true&useSSL=false");
        }

        Settings.hikariMaximumPoolSize = config.getInt("data_storage_settings.hikari_pool_settings.maximum_pool_size", 10);
        Settings.hikariMinimumIdle = config.getInt("data_storage_settings.hikari_pool_settings.minimum_idle", 10);
        Settings.hikariMaximumLifetime = config.getLong("data_storage_settings.hikari_pool_settings.maximum_lifetime", 1800000);
        Settings.hikariKeepAliveTime = config.getLong("data_storage_settings.hikari_pool_settings.keepalive_time", 0);
        Settings.hikariConnectionTimeOut = config.getLong("data_storage_settings.hikari_pool_settings.connection_timeout", 5000);

        // Read cluster data
        Configuration section = config.getSection("clusters");
        for (String clusterId : section.getKeys()) {
            final String playerTableName = config.getString("clusters." + clusterId + ".player_table", "husksync_players");
            final String dataTableName = config.getString("clusters." + clusterId + ".data_table", "husksync_data");
            final String databaseName = config.getString("clusters." + clusterId + ".database", Settings.mySQLDatabase);
            Settings.clusters.add(new SynchronisationCluster(clusterId, databaseName, playerTableName, dataTableName));
        }
    }

    public static void loadMessageStrings(Configuration config) {
        final HashMap<String,String> messages = new HashMap<>();
        for (String messageId : config.getKeys()) {
            messages.put(messageId, config.getString(messageId));
        }
        MessageManager.setMessages(messages);
    }

}
