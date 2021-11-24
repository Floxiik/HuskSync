package me.william278.husksync.redis;

import me.william278.husksync.Settings;

import java.io.Serializable;
import java.util.UUID;

public class MessageTarget implements Serializable {

    private Settings.ServerType targetServerType;
    private UUID targetPlayerUUID;
    private String targetClusterId;

    public MessageTarget(Settings.ServerType targetServerType, UUID targetPlayerUUID, String targetClusterId)  {
        this.targetServerType=targetServerType;
        this.targetPlayerUUID=targetPlayerUUID;
        this.targetClusterId=targetClusterId;
    }

    public Settings.ServerType targetServerType() {
        return targetServerType;
    }

    public UUID targetPlayerUUID() {
        return targetPlayerUUID;
    }

    public String targetClusterId() {
        return targetClusterId;
    }

    public boolean equals(String clusterId) {
        return clusterId.equals(targetClusterId);
    }
}
