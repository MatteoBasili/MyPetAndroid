package com.mysql.fabric;

public class ShardIndex {
    private String bound;
    private String groupName;
    private Integer shardId;

    public ShardIndex(String bound2, Integer shardId2, String groupName2) {
        this.bound = bound2;
        this.shardId = shardId2;
        this.groupName = groupName2;
    }

    public String getBound() {
        return this.bound;
    }

    public Integer getShardId() {
        return this.shardId;
    }

    public String getGroupName() {
        return this.groupName;
    }
}
