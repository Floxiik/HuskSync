package me.william278.husksync.bukkit.data;

import me.william278.husksync.PlayerData;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class DataView {

    private PlayerData playerData;
    private String ownerName;
    private InventoryType inventoryType;

    public DataView(PlayerData playerData, String ownerName, InventoryType inventoryType) {
        this.playerData = playerData;
        this.ownerName = ownerName;
        this.inventoryType = inventoryType;
    }

    public PlayerData playerData() {
        return playerData;
    }

    public String ownerName() {
        return ownerName;
    }

    public InventoryType inventoryType() {
        return inventoryType;
    }

    public ItemStack[] getDeserializedData() throws IOException, ClassNotFoundException {
        switch (inventoryType) {
            case INVENTORY:
                return DataSerializer.deserializeInventory(playerData.getSerializedInventory());
            case ENDER_CHEST:
                return DataSerializer.deserializeInventory(playerData.getSerializedEnderChest());
            default:
                return DataSerializer.deserializeInventory(playerData.getSerializedInventory());
        }
    }
}
