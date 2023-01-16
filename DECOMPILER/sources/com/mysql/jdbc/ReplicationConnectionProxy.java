package com.mysql.jdbc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

public class ReplicationConnectionProxy extends MultiHostConnectionProxy implements PingTarget {
    private static Class<?>[] INTERFACES_TO_PROXY;
    private static Constructor<?> JDBC_4_REPL_CONNECTION_CTOR;
    protected boolean allowMasterDownConnections = false;
    protected boolean allowSlaveDownConnections = false;
    ReplicationConnectionGroup connectionGroup;
    private long connectionGroupID = -1;
    private NonRegisteringDriver driver;
    protected boolean enableJMX = false;
    protected LoadBalancedConnection masterConnection;
    private List<String> masterHosts;
    private Properties masterProperties;
    protected boolean readFromMasterWhenNoSlaves = false;
    protected boolean readFromMasterWhenNoSlavesOriginal = false;
    protected boolean readOnly = false;
    private List<String> slaveHosts;
    private Properties slaveProperties;
    protected LoadBalancedConnection slavesConnection;
    private ReplicationConnection thisAsReplicationConnection = ((ReplicationConnection) this.thisAsConnection);

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: java.lang.Class<?>[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: java.lang.Class<?>[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    static {
        /*
            boolean r0 = com.mysql.jdbc.Util.isJdbc4()
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x0041
            java.lang.String r0 = "com.mysql.jdbc.JDBC4ReplicationMySQLConnection"
            java.lang.Class r0 = java.lang.Class.forName(r0)     // Catch:{ SecurityException -> 0x003a, NoSuchMethodException -> 0x0033, ClassNotFoundException -> 0x002c }
            java.lang.Class[] r3 = new java.lang.Class[r2]     // Catch:{ SecurityException -> 0x003a, NoSuchMethodException -> 0x0033, ClassNotFoundException -> 0x002c }
            java.lang.Class<com.mysql.jdbc.ReplicationConnectionProxy> r4 = com.mysql.jdbc.ReplicationConnectionProxy.class
            r3[r1] = r4     // Catch:{ SecurityException -> 0x003a, NoSuchMethodException -> 0x0033, ClassNotFoundException -> 0x002c }
            java.lang.reflect.Constructor r0 = r0.getConstructor(r3)     // Catch:{ SecurityException -> 0x003a, NoSuchMethodException -> 0x0033, ClassNotFoundException -> 0x002c }
            JDBC_4_REPL_CONNECTION_CTOR = r0     // Catch:{ SecurityException -> 0x003a, NoSuchMethodException -> 0x0033, ClassNotFoundException -> 0x002c }
            r0 = 2
            java.lang.Class[] r0 = new java.lang.Class[r0]     // Catch:{ SecurityException -> 0x003a, NoSuchMethodException -> 0x0033, ClassNotFoundException -> 0x002c }
            java.lang.Class<com.mysql.jdbc.ReplicationConnection> r3 = com.mysql.jdbc.ReplicationConnection.class
            r0[r1] = r3     // Catch:{ SecurityException -> 0x003a, NoSuchMethodException -> 0x0033, ClassNotFoundException -> 0x002c }
            java.lang.String r1 = "com.mysql.jdbc.JDBC4MySQLConnection"
            java.lang.Class r1 = java.lang.Class.forName(r1)     // Catch:{ SecurityException -> 0x003a, NoSuchMethodException -> 0x0033, ClassNotFoundException -> 0x002c }
            r0[r2] = r1     // Catch:{ SecurityException -> 0x003a, NoSuchMethodException -> 0x0033, ClassNotFoundException -> 0x002c }
            INTERFACES_TO_PROXY = r0     // Catch:{ SecurityException -> 0x003a, NoSuchMethodException -> 0x0033, ClassNotFoundException -> 0x002c }
            goto L_0x0049
        L_0x002c:
            r0 = move-exception
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            r1.<init>(r0)
            throw r1
        L_0x0033:
            r0 = move-exception
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            r1.<init>(r0)
            throw r1
        L_0x003a:
            r0 = move-exception
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            r1.<init>(r0)
            throw r1
        L_0x0041:
            java.lang.Class[] r0 = new java.lang.Class[r2]
            java.lang.Class<com.mysql.jdbc.ReplicationConnection> r2 = com.mysql.jdbc.ReplicationConnection.class
            r0[r1] = r2
            INTERFACES_TO_PROXY = r0
        L_0x0049:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ReplicationConnectionProxy.<clinit>():void");
    }

    public static ReplicationConnection createProxyInstance(List<String> masterHostList, Properties masterProperties2, List<String> slaveHostList, Properties slaveProperties2) throws SQLException {
        return (ReplicationConnection) Proxy.newProxyInstance(ReplicationConnection.class.getClassLoader(), INTERFACES_TO_PROXY, new ReplicationConnectionProxy(masterHostList, masterProperties2, slaveHostList, slaveProperties2));
    }

    private ReplicationConnectionProxy(List<String> masterHostList, Properties masterProperties2, List<String> slaveHostList, Properties slaveProperties2) throws SQLException {
        LoadBalancedConnection loadBalancedConnection;
        String enableJMXAsString = masterProperties2.getProperty("replicationEnableJMX", "false");
        try {
            this.enableJMX = Boolean.parseBoolean(enableJMXAsString);
            String allowMasterDownConnectionsAsString = masterProperties2.getProperty("allowMasterDownConnections", "false");
            try {
                this.allowMasterDownConnections = Boolean.parseBoolean(allowMasterDownConnectionsAsString);
                String allowSlaveDownConnectionsAsString = masterProperties2.getProperty("allowSlaveDownConnections", "false");
                try {
                    this.allowSlaveDownConnections = Boolean.parseBoolean(allowSlaveDownConnectionsAsString);
                    String readFromMasterWhenNoSlavesAsString = masterProperties2.getProperty("readFromMasterWhenNoSlaves");
                    try {
                        this.readFromMasterWhenNoSlavesOriginal = Boolean.parseBoolean(readFromMasterWhenNoSlavesAsString);
                        String group = masterProperties2.getProperty("replicationConnectionGroup", (String) null);
                        if (group != null) {
                            this.connectionGroup = ReplicationConnectionGroupManager.getConnectionGroupInstance(group);
                            if (this.enableJMX) {
                                ReplicationConnectionGroupManager.registerJmx();
                            }
                            this.connectionGroupID = this.connectionGroup.registerReplicationConnection(this.thisAsReplicationConnection, masterHostList, slaveHostList);
                            this.slaveHosts = new ArrayList(this.connectionGroup.getSlaveHosts());
                            this.masterHosts = new ArrayList(this.connectionGroup.getMasterHosts());
                        } else {
                            this.slaveHosts = new ArrayList(slaveHostList);
                            this.masterHosts = new ArrayList(masterHostList);
                        }
                        this.driver = new NonRegisteringDriver();
                        this.slaveProperties = slaveProperties2;
                        this.masterProperties = masterProperties2;
                        resetReadFromMasterWhenNoSlaves();
                        try {
                            initializeSlavesConnection();
                        } catch (SQLException e) {
                            if (!this.allowSlaveDownConnections) {
                                ReplicationConnectionGroup replicationConnectionGroup = this.connectionGroup;
                                if (replicationConnectionGroup != null) {
                                    replicationConnectionGroup.handleCloseConnection(this.thisAsReplicationConnection);
                                }
                                throw e;
                            }
                        }
                        SQLException exCaught = null;
                        try {
                            this.currentConnection = initializeMasterConnection();
                        } catch (SQLException e2) {
                            exCaught = e2;
                        }
                        if (this.currentConnection != null) {
                            return;
                        }
                        if (!this.allowMasterDownConnections || (loadBalancedConnection = this.slavesConnection) == null) {
                            ReplicationConnectionGroup replicationConnectionGroup2 = this.connectionGroup;
                            if (replicationConnectionGroup2 != null) {
                                replicationConnectionGroup2.handleCloseConnection(this.thisAsReplicationConnection);
                            }
                            if (exCaught != null) {
                                throw exCaught;
                            }
                            throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.initializationWithEmptyHostsLists"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
                        }
                        this.readOnly = true;
                        this.currentConnection = loadBalancedConnection;
                    } catch (Exception e3) {
                        throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.badValueForReadFromMasterWhenNoSlaves", new Object[]{readFromMasterWhenNoSlavesAsString}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
                    }
                } catch (Exception e4) {
                    throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.badValueForAllowSlaveDownConnections", new Object[]{allowSlaveDownConnectionsAsString}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
                }
            } catch (Exception e5) {
                throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.badValueForAllowMasterDownConnections", new Object[]{allowMasterDownConnectionsAsString}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
            }
        } catch (Exception e6) {
            throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.badValueForReplicationEnableJMX", new Object[]{enableJMXAsString}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
        }
    }

    /* access modifiers changed from: package-private */
    public MySQLConnection getNewWrapperForThisAsConnection() throws SQLException {
        if (!Util.isJdbc4() && JDBC_4_REPL_CONNECTION_CTOR == null) {
            return new ReplicationMySQLConnection(this);
        }
        return (MySQLConnection) Util.handleNewInstance(JDBC_4_REPL_CONNECTION_CTOR, new Object[]{this}, (ExceptionInterceptor) null);
    }

    /* access modifiers changed from: protected */
    public void propagateProxyDown(MySQLConnection proxyConn) {
        LoadBalancedConnection loadBalancedConnection = this.masterConnection;
        if (loadBalancedConnection != null) {
            loadBalancedConnection.setProxy(proxyConn);
        }
        LoadBalancedConnection loadBalancedConnection2 = this.slavesConnection;
        if (loadBalancedConnection2 != null) {
            loadBalancedConnection2.setProxy(proxyConn);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean shouldExceptionTriggerConnectionSwitch(Throwable t) {
        return false;
    }

    public boolean isMasterConnection() {
        return this.currentConnection != null && this.currentConnection == this.masterConnection;
    }

    public boolean isSlavesConnection() {
        return this.currentConnection != null && this.currentConnection == this.slavesConnection;
    }

    /* access modifiers changed from: package-private */
    public void pickNewConnection() throws SQLException {
    }

    /* access modifiers changed from: package-private */
    public void syncSessionState(Connection source, Connection target, boolean readOnlyStatus) throws SQLException {
        try {
            super.syncSessionState(source, target, readOnlyStatus);
        } catch (SQLException e) {
            try {
                super.syncSessionState(source, target, readOnlyStatus);
            } catch (SQLException e2) {
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void doClose() throws SQLException {
        LoadBalancedConnection loadBalancedConnection = this.masterConnection;
        if (loadBalancedConnection != null) {
            loadBalancedConnection.close();
        }
        LoadBalancedConnection loadBalancedConnection2 = this.slavesConnection;
        if (loadBalancedConnection2 != null) {
            loadBalancedConnection2.close();
        }
        ReplicationConnectionGroup replicationConnectionGroup = this.connectionGroup;
        if (replicationConnectionGroup != null) {
            replicationConnectionGroup.handleCloseConnection(this.thisAsReplicationConnection);
        }
    }

    /* access modifiers changed from: package-private */
    public void doAbortInternal() throws SQLException {
        this.masterConnection.abortInternal();
        this.slavesConnection.abortInternal();
        ReplicationConnectionGroup replicationConnectionGroup = this.connectionGroup;
        if (replicationConnectionGroup != null) {
            replicationConnectionGroup.handleCloseConnection(this.thisAsReplicationConnection);
        }
    }

    /* access modifiers changed from: package-private */
    public void doAbort(Executor executor) throws SQLException {
        this.masterConnection.abort(executor);
        this.slavesConnection.abort(executor);
        ReplicationConnectionGroup replicationConnectionGroup = this.connectionGroup;
        if (replicationConnectionGroup != null) {
            replicationConnectionGroup.handleCloseConnection(this.thisAsReplicationConnection);
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x004f, code lost:
        continue;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x004f, code lost:
        continue;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x004f, code lost:
        continue;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object invokeMore(java.lang.Object r5, java.lang.reflect.Method r6, java.lang.Object[] r7) throws java.lang.Throwable {
        /*
            r4 = this;
            r4.checkConnectionCapabilityForMethod(r6)
            r0 = 0
        L_0x0004:
            com.mysql.jdbc.MySQLConnection r1 = r4.thisAsConnection     // Catch:{ InvocationTargetException -> 0x0017 }
            java.lang.Object r1 = r6.invoke(r1, r7)     // Catch:{ InvocationTargetException -> 0x0017 }
            if (r1 == 0) goto L_0x0016
            boolean r2 = r1 instanceof com.mysql.jdbc.Statement     // Catch:{ InvocationTargetException -> 0x0017 }
            if (r2 == 0) goto L_0x0016
            r2 = r1
            com.mysql.jdbc.Statement r2 = (com.mysql.jdbc.Statement) r2     // Catch:{ InvocationTargetException -> 0x0017 }
            r2.setPingTarget(r4)     // Catch:{ InvocationTargetException -> 0x0017 }
        L_0x0016:
            return r1
        L_0x0017:
            r1 = move-exception
            if (r0 == 0) goto L_0x001c
            r0 = 0
            goto L_0x004f
        L_0x001c:
            java.lang.Throwable r2 = r1.getCause()
            if (r2 == 0) goto L_0x004f
            java.lang.Throwable r2 = r1.getCause()
            boolean r2 = r2 instanceof java.sql.SQLException
            if (r2 == 0) goto L_0x004f
            java.lang.Throwable r2 = r1.getCause()
            java.sql.SQLException r2 = (java.sql.SQLException) r2
            java.lang.String r2 = r2.getSQLState()
            java.lang.String r3 = "25000"
            if (r2 != r3) goto L_0x004f
            java.lang.Throwable r2 = r1.getCause()
            java.sql.SQLException r2 = (java.sql.SQLException) r2
            int r2 = r2.getErrorCode()
            r3 = 1000001(0xf4241, float:1.4013E-39)
            if (r2 != r3) goto L_0x004f
            boolean r2 = r4.readOnly     // Catch:{ SQLException -> 0x004e }
            r4.setReadOnly(r2)     // Catch:{ SQLException -> 0x004e }
            r0 = 1
            goto L_0x004f
        L_0x004e:
            r2 = move-exception
        L_0x004f:
            if (r0 == 0) goto L_0x0052
            goto L_0x0004
        L_0x0052:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ReplicationConnectionProxy.invokeMore(java.lang.Object, java.lang.reflect.Method, java.lang.Object[]):java.lang.Object");
    }

    private void checkConnectionCapabilityForMethod(Method method) throws Throwable {
        if (this.masterHosts.isEmpty() && this.slaveHosts.isEmpty() && !ReplicationConnection.class.isAssignableFrom(method.getDeclaringClass())) {
            throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.noHostsInconsistentState"), SQLError.SQL_STATE_INVALID_TRANSACTION_STATE, (int) MysqlErrorNumbers.ERROR_CODE_REPLICATION_CONNECTION_WITH_NO_HOSTS, true, (ExceptionInterceptor) null);
        }
    }

    public void doPing() throws SQLException {
        boolean isMasterConn = isMasterConnection();
        SQLException mastersPingException = null;
        SQLException slavesPingException = null;
        LoadBalancedConnection loadBalancedConnection = this.masterConnection;
        if (loadBalancedConnection != null) {
            try {
                loadBalancedConnection.ping();
            } catch (SQLException e) {
                mastersPingException = e;
            }
        } else {
            initializeMasterConnection();
        }
        LoadBalancedConnection loadBalancedConnection2 = this.slavesConnection;
        if (loadBalancedConnection2 != null) {
            try {
                loadBalancedConnection2.ping();
            } catch (SQLException e2) {
                slavesPingException = e2;
            }
        } else {
            try {
                initializeSlavesConnection();
                if (switchToSlavesConnectionIfNecessary()) {
                    isMasterConn = false;
                }
            } catch (SQLException e3) {
                if (this.masterConnection == null || !this.readFromMasterWhenNoSlaves) {
                    throw e3;
                }
            }
        }
        if (isMasterConn && mastersPingException != null) {
            LoadBalancedConnection loadBalancedConnection3 = this.slavesConnection;
            if (loadBalancedConnection3 != null && slavesPingException == null) {
                this.masterConnection = null;
                this.currentConnection = loadBalancedConnection3;
                this.readOnly = true;
            }
            throw mastersPingException;
        } else if (isMasterConn) {
        } else {
            if (slavesPingException != null || this.slavesConnection == null) {
                LoadBalancedConnection loadBalancedConnection4 = this.masterConnection;
                if (loadBalancedConnection4 != null && this.readFromMasterWhenNoSlaves && mastersPingException == null) {
                    this.slavesConnection = null;
                    this.currentConnection = loadBalancedConnection4;
                    this.readOnly = true;
                    this.currentConnection.setReadOnly(true);
                }
                if (slavesPingException != null) {
                    throw slavesPingException;
                }
            }
        }
    }

    private MySQLConnection initializeMasterConnection() throws SQLException {
        this.masterConnection = null;
        if (this.masterHosts.size() == 0) {
            return null;
        }
        LoadBalancedConnection newMasterConn = (LoadBalancedConnection) this.driver.connect(buildURL(this.masterHosts, this.masterProperties), this.masterProperties);
        newMasterConn.setProxy(getProxy());
        this.masterConnection = newMasterConn;
        return newMasterConn;
    }

    private MySQLConnection initializeSlavesConnection() throws SQLException {
        this.slavesConnection = null;
        if (this.slaveHosts.size() == 0) {
            return null;
        }
        LoadBalancedConnection newSlavesConn = (LoadBalancedConnection) this.driver.connect(buildURL(this.slaveHosts, this.slaveProperties), this.slaveProperties);
        newSlavesConn.setProxy(getProxy());
        newSlavesConn.setReadOnly(true);
        this.slavesConnection = newSlavesConn;
        return newSlavesConn;
    }

    private String buildURL(List<String> hosts, Properties props) {
        StringBuilder url = new StringBuilder(NonRegisteringDriver.LOADBALANCE_URL_PREFIX);
        boolean firstHost = true;
        for (String host : hosts) {
            if (!firstHost) {
                url.append(',');
            }
            url.append(host);
            firstHost = false;
        }
        url.append("/");
        String masterDb = props.getProperty(NonRegisteringDriver.DBNAME_PROPERTY_KEY);
        if (masterDb != null) {
            url.append(masterDb);
        }
        return url.toString();
    }

    private synchronized boolean switchToMasterConnection() throws SQLException {
        LoadBalancedConnection loadBalancedConnection = this.masterConnection;
        if (loadBalancedConnection == null || loadBalancedConnection.isClosed()) {
            try {
                if (initializeMasterConnection() == null) {
                    return false;
                }
            } catch (SQLException e) {
                this.currentConnection = null;
                throw e;
            }
        }
        if (!isMasterConnection() && this.masterConnection != null) {
            syncSessionState(this.currentConnection, this.masterConnection, false);
            this.currentConnection = this.masterConnection;
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002c, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized boolean switchToSlavesConnection() throws java.sql.SQLException {
        /*
            r3 = this;
            monitor-enter(r3)
            com.mysql.jdbc.LoadBalancedConnection r0 = r3.slavesConnection     // Catch:{ all -> 0x0032 }
            if (r0 == 0) goto L_0x000b
            boolean r0 = r0.isClosed()     // Catch:{ all -> 0x0032 }
            if (r0 == 0) goto L_0x0015
        L_0x000b:
            com.mysql.jdbc.MySQLConnection r0 = r3.initializeSlavesConnection()     // Catch:{ SQLException -> 0x002d }
            if (r0 != 0) goto L_0x0014
            r0 = 0
            monitor-exit(r3)
            return r0
        L_0x0014:
        L_0x0015:
            boolean r0 = r3.isSlavesConnection()     // Catch:{ all -> 0x0032 }
            r1 = 1
            if (r0 != 0) goto L_0x002b
            com.mysql.jdbc.LoadBalancedConnection r0 = r3.slavesConnection     // Catch:{ all -> 0x0032 }
            if (r0 == 0) goto L_0x002b
            com.mysql.jdbc.MySQLConnection r0 = r3.currentConnection     // Catch:{ all -> 0x0032 }
            com.mysql.jdbc.LoadBalancedConnection r2 = r3.slavesConnection     // Catch:{ all -> 0x0032 }
            r3.syncSessionState(r0, r2, r1)     // Catch:{ all -> 0x0032 }
            com.mysql.jdbc.LoadBalancedConnection r0 = r3.slavesConnection     // Catch:{ all -> 0x0032 }
            r3.currentConnection = r0     // Catch:{ all -> 0x0032 }
        L_0x002b:
            monitor-exit(r3)
            return r1
        L_0x002d:
            r0 = move-exception
            r1 = 0
            r3.currentConnection = r1     // Catch:{ all -> 0x0032 }
            throw r0     // Catch:{ all -> 0x0032 }
        L_0x0032:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ReplicationConnectionProxy.switchToSlavesConnection():boolean");
    }

    private boolean switchToSlavesConnectionIfNecessary() throws SQLException {
        if (this.currentConnection == null || ((isMasterConnection() && (this.readOnly || (this.masterHosts.isEmpty() && this.currentConnection.isClosed()))) || (!isMasterConnection() && this.currentConnection.isClosed()))) {
            return switchToSlavesConnection();
        }
        return false;
    }

    public synchronized Connection getCurrentConnection() {
        return this.currentConnection == null ? LoadBalancedConnectionProxy.getNullLoadBalancedConnectionInstance() : this.currentConnection;
    }

    public long getConnectionGroupId() {
        return this.connectionGroupID;
    }

    public synchronized Connection getMasterConnection() {
        return this.masterConnection;
    }

    public synchronized void promoteSlaveToMaster(String hostPortPair) throws SQLException {
        this.masterHosts.add(hostPortPair);
        removeSlave(hostPortPair);
        LoadBalancedConnection loadBalancedConnection = this.masterConnection;
        if (loadBalancedConnection != null) {
            loadBalancedConnection.addHost(hostPortPair);
        }
        if (!this.readOnly && !isMasterConnection()) {
            switchToMasterConnection();
        }
    }

    public synchronized void removeMasterHost(String hostPortPair) throws SQLException {
        removeMasterHost(hostPortPair, true);
    }

    public synchronized void removeMasterHost(String hostPortPair, boolean waitUntilNotInUse) throws SQLException {
        removeMasterHost(hostPortPair, waitUntilNotInUse, false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003c, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void removeMasterHost(java.lang.String r3, boolean r4, boolean r5) throws java.sql.SQLException {
        /*
            r2 = this;
            monitor-enter(r2)
            if (r5 == 0) goto L_0x000b
            java.util.List<java.lang.String> r0 = r2.slaveHosts     // Catch:{ all -> 0x0041 }
            r0.add(r3)     // Catch:{ all -> 0x0041 }
            r2.resetReadFromMasterWhenNoSlaves()     // Catch:{ all -> 0x0041 }
        L_0x000b:
            java.util.List<java.lang.String> r0 = r2.masterHosts     // Catch:{ all -> 0x0041 }
            r0.remove(r3)     // Catch:{ all -> 0x0041 }
            com.mysql.jdbc.LoadBalancedConnection r0 = r2.masterConnection     // Catch:{ all -> 0x0041 }
            r1 = 0
            if (r0 == 0) goto L_0x003d
            boolean r0 = r0.isClosed()     // Catch:{ all -> 0x0041 }
            if (r0 == 0) goto L_0x001c
            goto L_0x003d
        L_0x001c:
            if (r4 == 0) goto L_0x0024
            com.mysql.jdbc.LoadBalancedConnection r0 = r2.masterConnection     // Catch:{ all -> 0x0041 }
            r0.removeHostWhenNotInUse(r3)     // Catch:{ all -> 0x0041 }
            goto L_0x0029
        L_0x0024:
            com.mysql.jdbc.LoadBalancedConnection r0 = r2.masterConnection     // Catch:{ all -> 0x0041 }
            r0.removeHost(r3)     // Catch:{ all -> 0x0041 }
        L_0x0029:
            java.util.List<java.lang.String> r0 = r2.masterHosts     // Catch:{ all -> 0x0041 }
            boolean r0 = r0.isEmpty()     // Catch:{ all -> 0x0041 }
            if (r0 == 0) goto L_0x003b
            com.mysql.jdbc.LoadBalancedConnection r0 = r2.masterConnection     // Catch:{ all -> 0x0041 }
            r0.close()     // Catch:{ all -> 0x0041 }
            r2.masterConnection = r1     // Catch:{ all -> 0x0041 }
            r2.switchToSlavesConnectionIfNecessary()     // Catch:{ all -> 0x0041 }
        L_0x003b:
            monitor-exit(r2)
            return
        L_0x003d:
            r2.masterConnection = r1     // Catch:{ all -> 0x0041 }
            monitor-exit(r2)
            return
        L_0x0041:
            r3 = move-exception
            monitor-exit(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ReplicationConnectionProxy.removeMasterHost(java.lang.String, boolean, boolean):void");
    }

    public boolean isHostMaster(String hostPortPair) {
        if (hostPortPair == null) {
            return false;
        }
        for (String masterHost : this.masterHosts) {
            if (masterHost.equalsIgnoreCase(hostPortPair)) {
                return true;
            }
        }
        return false;
    }

    public synchronized Connection getSlavesConnection() {
        return this.slavesConnection;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0020, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void addSlaveHost(java.lang.String r2) throws java.sql.SQLException {
        /*
            r1 = this;
            monitor-enter(r1)
            boolean r0 = r1.isHostSlave(r2)     // Catch:{ all -> 0x0021 }
            if (r0 == 0) goto L_0x0009
            monitor-exit(r1)
            return
        L_0x0009:
            java.util.List<java.lang.String> r0 = r1.slaveHosts     // Catch:{ all -> 0x0021 }
            r0.add(r2)     // Catch:{ all -> 0x0021 }
            r1.resetReadFromMasterWhenNoSlaves()     // Catch:{ all -> 0x0021 }
            com.mysql.jdbc.LoadBalancedConnection r0 = r1.slavesConnection     // Catch:{ all -> 0x0021 }
            if (r0 != 0) goto L_0x001c
            r1.initializeSlavesConnection()     // Catch:{ all -> 0x0021 }
            r1.switchToSlavesConnectionIfNecessary()     // Catch:{ all -> 0x0021 }
            goto L_0x001f
        L_0x001c:
            r0.addHost(r2)     // Catch:{ all -> 0x0021 }
        L_0x001f:
            monitor-exit(r1)
            return
        L_0x0021:
            r2 = move-exception
            monitor-exit(r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ReplicationConnectionProxy.addSlaveHost(java.lang.String):void");
    }

    public synchronized void removeSlave(String hostPortPair) throws SQLException {
        removeSlave(hostPortPair, true);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0042, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void removeSlave(java.lang.String r3, boolean r4) throws java.sql.SQLException {
        /*
            r2 = this;
            monitor-enter(r2)
            java.util.List<java.lang.String> r0 = r2.slaveHosts     // Catch:{ all -> 0x0047 }
            r0.remove(r3)     // Catch:{ all -> 0x0047 }
            r2.resetReadFromMasterWhenNoSlaves()     // Catch:{ all -> 0x0047 }
            com.mysql.jdbc.LoadBalancedConnection r0 = r2.slavesConnection     // Catch:{ all -> 0x0047 }
            r1 = 0
            if (r0 == 0) goto L_0x0043
            boolean r0 = r0.isClosed()     // Catch:{ all -> 0x0047 }
            if (r0 == 0) goto L_0x0015
            goto L_0x0043
        L_0x0015:
            if (r4 == 0) goto L_0x001d
            com.mysql.jdbc.LoadBalancedConnection r0 = r2.slavesConnection     // Catch:{ all -> 0x0047 }
            r0.removeHostWhenNotInUse(r3)     // Catch:{ all -> 0x0047 }
            goto L_0x0022
        L_0x001d:
            com.mysql.jdbc.LoadBalancedConnection r0 = r2.slavesConnection     // Catch:{ all -> 0x0047 }
            r0.removeHost(r3)     // Catch:{ all -> 0x0047 }
        L_0x0022:
            java.util.List<java.lang.String> r0 = r2.slaveHosts     // Catch:{ all -> 0x0047 }
            boolean r0 = r0.isEmpty()     // Catch:{ all -> 0x0047 }
            if (r0 == 0) goto L_0x0041
            com.mysql.jdbc.LoadBalancedConnection r0 = r2.slavesConnection     // Catch:{ all -> 0x0047 }
            r0.close()     // Catch:{ all -> 0x0047 }
            r2.slavesConnection = r1     // Catch:{ all -> 0x0047 }
            r2.switchToMasterConnection()     // Catch:{ all -> 0x0047 }
            boolean r0 = r2.isMasterConnection()     // Catch:{ all -> 0x0047 }
            if (r0 == 0) goto L_0x0041
            com.mysql.jdbc.MySQLConnection r0 = r2.currentConnection     // Catch:{ all -> 0x0047 }
            boolean r1 = r2.readOnly     // Catch:{ all -> 0x0047 }
            r0.setReadOnly(r1)     // Catch:{ all -> 0x0047 }
        L_0x0041:
            monitor-exit(r2)
            return
        L_0x0043:
            r2.slavesConnection = r1     // Catch:{ all -> 0x0047 }
            monitor-exit(r2)
            return
        L_0x0047:
            r3 = move-exception
            monitor-exit(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ReplicationConnectionProxy.removeSlave(java.lang.String, boolean):void");
    }

    public boolean isHostSlave(String hostPortPair) {
        if (hostPortPair == null) {
            return false;
        }
        for (String test : this.slaveHosts) {
            if (test.equalsIgnoreCase(hostPortPair)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void setReadOnly(boolean readOnly2) throws SQLException {
        boolean switched;
        boolean switched2;
        if (readOnly2) {
            if (!isSlavesConnection() || this.currentConnection.isClosed()) {
                SQLException exceptionCaught = null;
                try {
                    switched2 = switchToSlavesConnection();
                } catch (SQLException e) {
                    switched2 = false;
                    exceptionCaught = e;
                }
                if (!switched2) {
                    if (this.readFromMasterWhenNoSlaves && switchToMasterConnection()) {
                        exceptionCaught = null;
                    }
                }
                if (exceptionCaught != null) {
                    throw exceptionCaught;
                }
            }
        } else if (!isMasterConnection() || this.currentConnection.isClosed()) {
            SQLException exceptionCaught2 = null;
            try {
                switched = switchToMasterConnection();
            } catch (SQLException e2) {
                switched = false;
                exceptionCaught2 = e2;
            }
            if (!switched) {
                if (switchToSlavesConnectionIfNecessary()) {
                    exceptionCaught2 = null;
                }
            }
            if (exceptionCaught2 != null) {
                throw exceptionCaught2;
            }
        }
        this.readOnly = readOnly2;
        if (this.readFromMasterWhenNoSlaves && isMasterConnection()) {
            this.currentConnection.setReadOnly(this.readOnly);
        }
    }

    public boolean isReadOnly() throws SQLException {
        return !isMasterConnection() || this.readOnly;
    }

    private void resetReadFromMasterWhenNoSlaves() {
        this.readFromMasterWhenNoSlaves = this.slaveHosts.isEmpty() || this.readFromMasterWhenNoSlavesOriginal;
    }
}
