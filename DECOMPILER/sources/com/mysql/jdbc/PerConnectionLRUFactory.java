package com.mysql.jdbc;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.util.LRUCache;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

public class PerConnectionLRUFactory implements CacheAdapterFactory<String, PreparedStatement.ParseInfo> {
    public CacheAdapter<String, PreparedStatement.ParseInfo> getInstance(Connection forConnection, String url, int cacheMaxSize, int maxKeySize, Properties connectionProperties) throws SQLException {
        return new PerConnectionLRU(forConnection, cacheMaxSize, maxKeySize);
    }

    class PerConnectionLRU implements CacheAdapter<String, PreparedStatement.ParseInfo> {
        private final LRUCache<String, PreparedStatement.ParseInfo> cache;
        private final int cacheSqlLimit;
        private final Connection conn;

        protected PerConnectionLRU(Connection forConnection, int cacheMaxSize, int maxKeySize) {
            this.cacheSqlLimit = maxKeySize;
            this.cache = new LRUCache<>(cacheMaxSize);
            this.conn = forConnection;
        }

        public PreparedStatement.ParseInfo get(String key) {
            PreparedStatement.ParseInfo parseInfo;
            if (key == null || key.length() > this.cacheSqlLimit) {
                return null;
            }
            synchronized (this.conn.getConnectionMutex()) {
                parseInfo = (PreparedStatement.ParseInfo) this.cache.get(key);
            }
            return parseInfo;
        }

        public void put(String key, PreparedStatement.ParseInfo value) {
            if (key != null && key.length() <= this.cacheSqlLimit) {
                synchronized (this.conn.getConnectionMutex()) {
                    this.cache.put(key, value);
                }
            }
        }

        public void invalidate(String key) {
            synchronized (this.conn.getConnectionMutex()) {
                this.cache.remove(key);
            }
        }

        public void invalidateAll(Set<String> keys) {
            synchronized (this.conn.getConnectionMutex()) {
                for (String key : keys) {
                    this.cache.remove(key);
                }
            }
        }

        public void invalidateAll() {
            synchronized (this.conn.getConnectionMutex()) {
                this.cache.clear();
            }
        }
    }
}
