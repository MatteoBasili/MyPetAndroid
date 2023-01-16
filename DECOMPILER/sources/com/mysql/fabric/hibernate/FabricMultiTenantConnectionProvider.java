package com.mysql.fabric.hibernate;

import com.mysql.fabric.FabricCommunicationException;
import com.mysql.fabric.FabricConnection;
import com.mysql.fabric.Server;
import com.mysql.fabric.ServerGroup;
import com.mysql.fabric.ServerMode;
import com.mysql.fabric.ShardMapping;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;

public class FabricMultiTenantConnectionProvider implements MultiTenantConnectionProvider {
    private static final long serialVersionUID = 1;
    private String database;
    private FabricConnection fabricConnection;
    private ServerGroup globalGroup;
    private String password;
    private ShardMapping shardMapping;
    private String table;
    private String user;

    public FabricMultiTenantConnectionProvider(String fabricUrl, String database2, String table2, String user2, String password2, String fabricUser, String fabricPassword) {
        try {
            FabricConnection fabricConnection2 = new FabricConnection(fabricUrl, fabricUser, fabricPassword);
            this.fabricConnection = fabricConnection2;
            this.database = database2;
            this.table = table2;
            this.user = user2;
            this.password = password2;
            ShardMapping shardMapping2 = fabricConnection2.getShardMapping(database2, table2);
            this.shardMapping = shardMapping2;
            this.globalGroup = this.fabricConnection.getServerGroup(shardMapping2.getGlobalGroupName());
        } catch (FabricCommunicationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Connection getReadWriteConnectionFromServerGroup(ServerGroup serverGroup) throws SQLException {
        for (Server s : serverGroup.getServers()) {
            if (ServerMode.READ_WRITE.equals(s.getMode())) {
                return DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", new Object[]{s.getHostname(), Integer.valueOf(s.getPort()), this.database}), this.user, this.password);
            }
        }
        throw new SQLException("Unable to find r/w server for chosen shard mapping in group " + serverGroup.getName());
    }

    public Connection getAnyConnection() throws SQLException {
        return getReadWriteConnectionFromServerGroup(this.globalGroup);
    }

    public Connection getConnection(String tenantIdentifier) throws SQLException {
        return getReadWriteConnectionFromServerGroup(this.fabricConnection.getServerGroup(this.shardMapping.getGroupNameForKey(tenantIdentifier)));
    }

    public void releaseAnyConnection(Connection connection) throws SQLException {
        try {
            connection.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        releaseAnyConnection(connection);
    }

    public boolean supportsAggressiveRelease() {
        return false;
    }

    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    public <T> T unwrap(Class<T> cls) {
        return null;
    }
}
