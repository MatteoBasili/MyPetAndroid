package com.mysql.jdbc;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executor;

public class ReplicationMySQLConnection extends MultiHostMySQLConnection implements ReplicationConnection {
    public ReplicationMySQLConnection(MultiHostConnectionProxy proxy) {
        super(proxy);
    }

    /* access modifiers changed from: protected */
    public ReplicationConnectionProxy getThisAsProxy() {
        return (ReplicationConnectionProxy) super.getThisAsProxy();
    }

    public MySQLConnection getActiveMySQLConnection() {
        return (MySQLConnection) getCurrentConnection();
    }

    public synchronized Connection getCurrentConnection() {
        return getThisAsProxy().getCurrentConnection();
    }

    public long getConnectionGroupId() {
        return getThisAsProxy().getConnectionGroupId();
    }

    public synchronized Connection getMasterConnection() {
        return getThisAsProxy().getMasterConnection();
    }

    private Connection getValidatedMasterConnection() {
        Connection conn = getThisAsProxy().masterConnection;
        if (conn == null) {
            return null;
        }
        try {
            if (conn.isClosed()) {
                return null;
            }
            return conn;
        } catch (SQLException e) {
            return null;
        }
    }

    public void promoteSlaveToMaster(String host) throws SQLException {
        getThisAsProxy().promoteSlaveToMaster(host);
    }

    public void removeMasterHost(String host) throws SQLException {
        getThisAsProxy().removeMasterHost(host);
    }

    public void removeMasterHost(String host, boolean waitUntilNotInUse) throws SQLException {
        getThisAsProxy().removeMasterHost(host, waitUntilNotInUse);
    }

    public boolean isHostMaster(String host) {
        return getThisAsProxy().isHostMaster(host);
    }

    public synchronized Connection getSlavesConnection() {
        return getThisAsProxy().getSlavesConnection();
    }

    private Connection getValidatedSlavesConnection() {
        Connection conn = getThisAsProxy().slavesConnection;
        if (conn == null) {
            return null;
        }
        try {
            if (conn.isClosed()) {
                return null;
            }
            return conn;
        } catch (SQLException e) {
            return null;
        }
    }

    public void addSlaveHost(String host) throws SQLException {
        getThisAsProxy().addSlaveHost(host);
    }

    public void removeSlave(String host) throws SQLException {
        getThisAsProxy().removeSlave(host);
    }

    public void removeSlave(String host, boolean closeGently) throws SQLException {
        getThisAsProxy().removeSlave(host, closeGently);
    }

    public boolean isHostSlave(String host) {
        return getThisAsProxy().isHostSlave(host);
    }

    public void setReadOnly(boolean readOnlyFlag) throws SQLException {
        getThisAsProxy().setReadOnly(readOnlyFlag);
    }

    public boolean isReadOnly() throws SQLException {
        return getThisAsProxy().isReadOnly();
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0020 A[Catch:{ SQLException -> 0x0024 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void ping() throws java.sql.SQLException {
        /*
            r3 = this;
            monitor-enter(r3)
            com.mysql.jdbc.Connection r0 = r3.getValidatedMasterConnection()     // Catch:{ SQLException -> 0x0011 }
            r1 = r0
            if (r0 == 0) goto L_0x000e
            r1.ping()     // Catch:{ SQLException -> 0x000c }
            goto L_0x000e
        L_0x000c:
            r0 = move-exception
            goto L_0x0013
        L_0x000e:
            goto L_0x0019
        L_0x000f:
            r0 = move-exception
            goto L_0x002f
        L_0x0011:
            r0 = move-exception
            r1 = 0
        L_0x0013:
            boolean r2 = r3.isMasterConnection()     // Catch:{ all -> 0x000f }
            if (r2 != 0) goto L_0x002e
        L_0x0019:
            com.mysql.jdbc.Connection r0 = r3.getValidatedSlavesConnection()     // Catch:{ SQLException -> 0x0024 }
            r1 = r0
            if (r0 == 0) goto L_0x0023
            r1.ping()     // Catch:{ SQLException -> 0x0024 }
        L_0x0023:
            goto L_0x002b
        L_0x0024:
            r0 = move-exception
            boolean r2 = r3.isMasterConnection()     // Catch:{ all -> 0x000f }
            if (r2 == 0) goto L_0x002d
        L_0x002b:
            monitor-exit(r3)
            return
        L_0x002d:
            throw r0     // Catch:{ all -> 0x000f }
        L_0x002e:
            throw r0     // Catch:{ all -> 0x000f }
        L_0x002f:
            monitor-exit(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ReplicationMySQLConnection.ping():void");
    }

    public synchronized void changeUser(String userName, String newPassword) throws SQLException {
        Connection validatedMasterConnection = getValidatedMasterConnection();
        Connection conn = validatedMasterConnection;
        if (validatedMasterConnection != null) {
            conn.changeUser(userName, newPassword);
        }
        Connection validatedSlavesConnection = getValidatedSlavesConnection();
        Connection conn2 = validatedSlavesConnection;
        if (validatedSlavesConnection != null) {
            conn2.changeUser(userName, newPassword);
        }
    }

    public synchronized void setStatementComment(String comment) {
        Connection validatedMasterConnection = getValidatedMasterConnection();
        Connection conn = validatedMasterConnection;
        if (validatedMasterConnection != null) {
            conn.setStatementComment(comment);
        }
        Connection validatedSlavesConnection = getValidatedSlavesConnection();
        Connection conn2 = validatedSlavesConnection;
        if (validatedSlavesConnection != null) {
            conn2.setStatementComment(comment);
        }
    }

    public boolean hasSameProperties(Connection c) {
        Connection connM = getValidatedMasterConnection();
        Connection connS = getValidatedSlavesConnection();
        if (connM == null && connS == null) {
            return false;
        }
        if (connM != null && !connM.hasSameProperties(c)) {
            return false;
        }
        if (connS == null || connS.hasSameProperties(c)) {
            return true;
        }
        return false;
    }

    public Properties getProperties() {
        Properties props = new Properties();
        Connection validatedMasterConnection = getValidatedMasterConnection();
        Connection conn = validatedMasterConnection;
        if (validatedMasterConnection != null) {
            props.putAll(conn.getProperties());
        }
        Connection validatedSlavesConnection = getValidatedSlavesConnection();
        Connection conn2 = validatedSlavesConnection;
        if (validatedSlavesConnection != null) {
            props.putAll(conn2.getProperties());
        }
        return props;
    }

    public void abort(Executor executor) throws SQLException {
        getThisAsProxy().doAbort(executor);
    }

    public void abortInternal() throws SQLException {
        getThisAsProxy().doAbortInternal();
    }

    public boolean getAllowMasterDownConnections() {
        return getThisAsProxy().allowMasterDownConnections;
    }

    public void setAllowMasterDownConnections(boolean connectIfMasterDown) {
        getThisAsProxy().allowMasterDownConnections = connectIfMasterDown;
    }

    public boolean getReplicationEnableJMX() {
        return getThisAsProxy().enableJMX;
    }

    public void setReplicationEnableJMX(boolean replicationEnableJMX) {
        getThisAsProxy().enableJMX = replicationEnableJMX;
    }

    public void setProxy(MySQLConnection proxy) {
        getThisAsProxy().setProxy(proxy);
    }
}
