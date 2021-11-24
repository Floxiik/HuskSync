package me.william278.husksync.bukkit.data;

import org.bukkit.World;

import java.io.Serializable;

public class PlayerLocation implements Serializable {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private String worldName;
    private World.Environment environment;

    public PlayerLocation(double x, double y, double z, float yaw, float pitch, String worldName, World.Environment environment) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.worldName = worldName;
        this.environment = environment;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public float yaw() {
        return yaw;
    }

    public float pitch() {
        return pitch;
    }

    public String worldName() {
        return worldName;
    }

    public World.Environment environment() {
        return environment;
    }
}
