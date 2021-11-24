package me.william278.husksync;

public class SynchronisationCluster {

    private String clusterId;
    private String databaseName;
    private String playerTableName;
    private String dataTableName;

    public SynchronisationCluster(String clusterId, String databaseName, String playerTableName, String dataTableName) {
        this.clusterId = clusterId;
        this.databaseName = databaseName;
        this.playerTableName = playerTableName;
        this.dataTableName = dataTableName;
    }

    public String clusterId() {
        return clusterId;
    }

    public String databaseName() {
        return databaseName;
    }

    public String playerTableName() {
        return playerTableName;
    }

    public String dataTableName() {
        return dataTableName;
    }
}
