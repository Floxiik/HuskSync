package me.william278.husksync.bungeecord;

import java.util.UUID;

public class Server {

    private UUID serverUUID;
    private boolean hasMySqlPlayerDataBridge;
    private String huskSyncVersion;
    private String serverBrand;
    private String clusterId;

    public Server(UUID serverUUID, boolean hasMySqlPlayerDataBridge, String huskSyncVersion, String serverBrand, String clusterId) {
        this.serverUUID = serverUUID;
        this.hasMySqlPlayerDataBridge = hasMySqlPlayerDataBridge;
        this.huskSyncVersion = huskSyncVersion;
        this.serverBrand = serverBrand;
        this.clusterId = clusterId;
    }

    public UUID serverUUID() {
        return serverUUID;
    }

    public boolean hasMySqlPlayerDataBridge() {
        return hasMySqlPlayerDataBridge;
    }

    public String huskSyncVersion() {
        return huskSyncVersion;
    }

    public String serverBrand() {
        return serverBrand;
    }

    public String clusterId() {
        return clusterId;
    }
}
