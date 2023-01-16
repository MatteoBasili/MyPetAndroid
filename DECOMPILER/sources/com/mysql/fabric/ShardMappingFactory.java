package com.mysql.fabric;

import java.util.Set;

public class ShardMappingFactory {

    /* renamed from: com.mysql.fabric.ShardMappingFactory$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$mysql$fabric$ShardingType;

        static {
            int[] iArr = new int[ShardingType.values().length];
            $SwitchMap$com$mysql$fabric$ShardingType = iArr;
            try {
                iArr[ShardingType.RANGE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$mysql$fabric$ShardingType[ShardingType.HASH.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public ShardMapping createShardMapping(int mappingId, ShardingType shardingType, String globalGroupName, Set<ShardTable> shardTables, Set<ShardIndex> shardIndices) {
        switch (AnonymousClass1.$SwitchMap$com$mysql$fabric$ShardingType[shardingType.ordinal()]) {
            case 1:
                return new RangeShardMapping(mappingId, shardingType, globalGroupName, shardTables, shardIndices);
            case 2:
                return new HashShardMapping(mappingId, shardingType, globalGroupName, shardTables, shardIndices);
            default:
                throw new IllegalArgumentException("Invalid ShardingType");
        }
    }
}
