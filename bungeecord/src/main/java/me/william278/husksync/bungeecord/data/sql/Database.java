package me.william278.husksync.bungeecord.data.sql;

import me.william278.husksync.HuskSyncBungeeCord;
import me.william278.husksync.Settings;
import me.william278.husksync.SynchronisationCluster;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Database {
    protected HuskSyncBungeeCord plugin;

    public String dataPoolName;
    public SynchronisationCluster cluster;

    public Database(HuskSyncBungeeCord instance, SynchronisationCluster cluster) {
        this.plugin = instance;
        this.cluster = cluster;
        this.dataPoolName = "HuskSyncHikariPool-" + cluster.clusterId();
    }

    public abstract Connection getConnection() throws SQLException;

    public boolean isInactive() {
        try {
            return getConnection() == null;
        } catch (SQLException e) {
            return true;
        }
    }

    public abstract void load();

    public abstract void createTables();

    public abstract void close();

    public final int hikariMaximumPoolSize = Settings.hikariMaximumPoolSize;
    public final int hikariMinimumIdle = Settings.hikariMinimumIdle;
    public final long hikariMaximumLifetime = Settings.hikariMaximumLifetime;
    public final long hikariKeepAliveTime = Settings.hikariKeepAliveTime;
    public final long hikariConnectionTimeOut = Settings.hikariConnectionTimeOut;
}
