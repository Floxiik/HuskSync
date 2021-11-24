package me.william278.husksync.bukkit.data;

import me.william278.husksync.redis.RedisMessage;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class AdvancementRecord implements Serializable {

    private String advancementKey;
    private ArrayList<String> awardedAdvancementCriteria;

    public AdvancementRecord(String advancementKey, ArrayList<String> awardedAdvancementCriteria) {
        this.advancementKey = advancementKey;
        this.awardedAdvancementCriteria = awardedAdvancementCriteria;
    }

    public String advancementKey() {
        return advancementKey;
    }

    public ArrayList<String> awardedAdvancementCriteria() {
        return awardedAdvancementCriteria;
    }


}
