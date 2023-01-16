package com.mysql.fabric;

import java.util.Collections;
import java.util.Set;

public abstract class ShardMapping {
    private String globalGroupName;
    private int mappingId;
    protected Set<ShardIndex> shardIndices;
    protected Set<ShardTable> shardTables;
    private ShardingType shardingType;

    /* access modifiers changed from: protected */
    public abstract ShardIndex getShardIndexForKey(String str);

    public ShardMapping(int mappingId2, ShardingType shardingType2, String globalGroupName2, Set<ShardTable> shardTables2, Set<ShardIndex> shardIndices2) {
        this.mappingId = mappingId2;
        this.shardingType = shardingType2;
        this.globalGroupName = globalGroupName2;
        this.shardTables = shardTables2;
        this.shardIndices = shardIndices2;
    }

    public String getGroupNameForKey(String key) {
        return getShardIndexForKey(key).getGroupName();
    }

    public int getMappingId() {
        return this.mappingId;
    }

    public ShardingType getShardingType() {
        return this.shardingType;
    }

    public String getGlobalGroupName() {
        return this.globalGroupName;
    }

    public Set<ShardTable> getShardTables() {
        return Collections.unmodifiableSet(this.shardTables);
    }

    public Set<ShardIndex> getShardIndices() {
        return Collections.unmodifiableSet(this.shardIndices);
    }
}
