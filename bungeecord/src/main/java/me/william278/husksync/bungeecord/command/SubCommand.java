package me.william278.husksync.bungeecord.command;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SubCommand {

    private String command;
    private String permission;

    public SubCommand(String command, String permission) {
        this.command = command;
        this.permission = permission;
    }

    public String command() {
        return command;
    }

    public String permission() {
        return permission;
    }

    public boolean doesPlayerHavePermission(ProxiedPlayer player) {
        return permission == null || player.hasPermission(permission);
    }
}
