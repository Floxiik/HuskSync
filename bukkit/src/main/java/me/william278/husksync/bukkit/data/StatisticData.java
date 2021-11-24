package me.william278.husksync.bukkit.data;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

import java.io.Serializable;
import java.util.HashMap;

public class StatisticData implements Serializable {

    private HashMap<Statistic, Integer> untypedStatisticValues;
    private HashMap<Statistic, HashMap<Material, Integer>> blockStatisticValues;
    private HashMap<Statistic, HashMap<Material, Integer>> itemStatisticValues;
    private HashMap<Statistic, HashMap<EntityType, Integer>> entityStatisticValues;

    public StatisticData(HashMap<Statistic, Integer> untypedStatisticValues, HashMap<Statistic, HashMap<Material, Integer>> blockStatisticValues, HashMap<Statistic, HashMap<Material, Integer>> itemStatisticValues, HashMap<Statistic, HashMap<EntityType, Integer>> entityStatisticValues) {
        this.untypedStatisticValues = untypedStatisticValues;
        this.blockStatisticValues = blockStatisticValues;
        this.itemStatisticValues = itemStatisticValues;
        this.entityStatisticValues = entityStatisticValues;
    }

    public HashMap<Statistic, Integer> untypedStatisticValues() {
        return untypedStatisticValues;
    }

    public HashMap<Statistic, HashMap<Material, Integer>> blockStatisticValues() {
        return blockStatisticValues;
    }

    public HashMap<Statistic, HashMap<Material, Integer>> itemStatisticValues() {
        return itemStatisticValues;
    }

    public HashMap<Statistic, HashMap<EntityType, Integer>> entityStatisticValues() {
        return entityStatisticValues;
    }
}
