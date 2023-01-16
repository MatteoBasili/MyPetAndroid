package com.mysql.fabric;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class HashShardMapping extends ShardMapping {
    private static final MessageDigest md5Hasher;

    private static class ReverseShardIndexSorter implements Comparator<ShardIndex> {
        public static final ReverseShardIndexSorter instance = new ReverseShardIndexSorter();

        private ReverseShardIndexSorter() {
        }

        public int compare(ShardIndex i1, ShardIndex i2) {
            return i2.getBound().compareTo(i1.getBound());
        }
    }

    static {
        try {
            md5Hasher = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public HashShardMapping(int mappingId, ShardingType shardingType, String globalGroupName, Set<ShardTable> shardTables, Set<ShardIndex> shardIndices) {
        super(mappingId, shardingType, globalGroupName, shardTables, new TreeSet(ReverseShardIndexSorter.instance));
        this.shardIndices.addAll(shardIndices);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0026, code lost:
        r1 = "0" + r1;
        r0 = r0 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x003c, code lost:
        r0 = r5.shardIndices.iterator();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0046, code lost:
        if (r0.hasNext() == false) goto L_0x005a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0048, code lost:
        r2 = (com.mysql.fabric.ShardIndex) r0.next();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0056, code lost:
        if (r2.getBound().compareTo(r1) > 0) goto L_0x0042;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0058, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0066, code lost:
        return (com.mysql.fabric.ShardIndex) r5.shardIndices.iterator().next();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x001d, code lost:
        r0 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0024, code lost:
        if (r0 >= (32 - r1.length())) goto L_0x003c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.mysql.fabric.ShardIndex getShardIndexForKey(java.lang.String r6) {
        /*
            r5 = this;
            java.security.MessageDigest r0 = md5Hasher
            monitor-enter(r0)
            r1 = 0
            java.math.BigInteger r2 = new java.math.BigInteger     // Catch:{ all -> 0x0067 }
            byte[] r3 = r6.getBytes()     // Catch:{ all -> 0x0067 }
            byte[] r3 = r0.digest(r3)     // Catch:{ all -> 0x0067 }
            r4 = 1
            r2.<init>(r4, r3)     // Catch:{ all -> 0x0067 }
            r3 = 16
            java.lang.String r2 = r2.toString(r3)     // Catch:{ all -> 0x0067 }
            java.lang.String r1 = r2.toUpperCase()     // Catch:{ all -> 0x0067 }
            monitor-exit(r0)     // Catch:{ all -> 0x006a }
            r0 = 0
        L_0x001e:
            int r2 = r1.length()
            int r2 = 32 - r2
            if (r0 >= r2) goto L_0x003c
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "0"
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.StringBuilder r2 = r2.append(r1)
            java.lang.String r1 = r2.toString()
            int r0 = r0 + 1
            goto L_0x001e
        L_0x003c:
            java.util.Set r0 = r5.shardIndices
            java.util.Iterator r0 = r0.iterator()
        L_0x0042:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L_0x005a
            java.lang.Object r2 = r0.next()
            com.mysql.fabric.ShardIndex r2 = (com.mysql.fabric.ShardIndex) r2
            java.lang.String r3 = r2.getBound()
            int r3 = r3.compareTo(r1)
            if (r3 > 0) goto L_0x0059
            return r2
        L_0x0059:
            goto L_0x0042
        L_0x005a:
            java.util.Set r0 = r5.shardIndices
            java.util.Iterator r0 = r0.iterator()
            java.lang.Object r0 = r0.next()
            com.mysql.fabric.ShardIndex r0 = (com.mysql.fabric.ShardIndex) r0
            return r0
        L_0x0067:
            r2 = move-exception
        L_0x0068:
            monitor-exit(r0)     // Catch:{ all -> 0x006a }
            throw r2
        L_0x006a:
            r2 = move-exception
            goto L_0x0068
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.fabric.HashShardMapping.getShardIndexForKey(java.lang.String):com.mysql.fabric.ShardIndex");
    }
}
