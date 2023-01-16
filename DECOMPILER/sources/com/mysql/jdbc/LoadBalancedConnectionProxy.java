package com.mysql.jdbc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;

public class LoadBalancedConnectionProxy extends MultiHostConnectionProxy implements PingTarget {
    public static final String BLACKLIST_TIMEOUT_PROPERTY_KEY = "loadBalanceBlacklistTimeout";
    public static final String HOST_REMOVAL_GRACE_PERIOD_PROPERTY_KEY = "loadBalanceHostRemovalGracePeriod";
    private static Class<?>[] INTERFACES_TO_PROXY;
    private static Constructor<?> JDBC_4_LB_CONNECTION_CTOR;
    private static Map<String, Long> globalBlacklist = new HashMap();
    private static LoadBalancedConnection nullLBConnectionInstance = null;
    private int autoCommitSwapThreshold = 0;
    private BalanceStrategy balancer;
    private ConnectionGroup connectionGroup = null;
    private long connectionGroupProxyID = 0;
    private Map<ConnectionImpl, String> connectionsToHostsMap;
    private LoadBalanceExceptionChecker exceptionChecker;
    private int globalBlacklistTimeout = 0;
    private int hostRemovalGracePeriod = 0;
    private Map<String, Integer> hostsToListIndexMap;
    private Set<String> hostsToRemove = new HashSet();
    private boolean inTransaction = false;
    protected Map<String, ConnectionImpl> liveConnections;
    private long[] responseTimes;
    private int retriesAllDown;
    private long totalPhysicalConnections = 0;
    private long transactionCount = 0;
    private long transactionStartTime = 0;

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: java.lang.Class<?>[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: java.lang.Class<?>[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    static {
        /*
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            globalBlacklist = r0
            boolean r0 = com.mysql.jdbc.Util.isJdbc4()
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x0048
            java.lang.String r0 = "com.mysql.jdbc.JDBC4LoadBalancedMySQLConnection"
            java.lang.Class r0 = java.lang.Class.forName(r0)     // Catch:{ SecurityException -> 0x0041, NoSuchMethodException -> 0x003a, ClassNotFoundException -> 0x0033 }
            java.lang.Class[] r3 = new java.lang.Class[r2]     // Catch:{ SecurityException -> 0x0041, NoSuchMethodException -> 0x003a, ClassNotFoundException -> 0x0033 }
            java.lang.Class<com.mysql.jdbc.LoadBalancedConnectionProxy> r4 = com.mysql.jdbc.LoadBalancedConnectionProxy.class
            r3[r1] = r4     // Catch:{ SecurityException -> 0x0041, NoSuchMethodException -> 0x003a, ClassNotFoundException -> 0x0033 }
            java.lang.reflect.Constructor r0 = r0.getConstructor(r3)     // Catch:{ SecurityException -> 0x0041, NoSuchMethodException -> 0x003a, ClassNotFoundException -> 0x0033 }
            JDBC_4_LB_CONNECTION_CTOR = r0     // Catch:{ SecurityException -> 0x0041, NoSuchMethodException -> 0x003a, ClassNotFoundException -> 0x0033 }
            r0 = 2
            java.lang.Class[] r0 = new java.lang.Class[r0]     // Catch:{ SecurityException -> 0x0041, NoSuchMethodException -> 0x003a, ClassNotFoundException -> 0x0033 }
            java.lang.Class<com.mysql.jdbc.LoadBalancedConnection> r3 = com.mysql.jdbc.LoadBalancedConnection.class
            r0[r1] = r3     // Catch:{ SecurityException -> 0x0041, NoSuchMethodException -> 0x003a, ClassNotFoundException -> 0x0033 }
            java.lang.String r1 = "com.mysql.jdbc.JDBC4MySQLConnection"
            java.lang.Class r1 = java.lang.Class.forName(r1)     // Catch:{ SecurityException -> 0x0041, NoSuchMethodException -> 0x003a, ClassNotFoundException -> 0x0033 }
            r0[r2] = r1     // Catch:{ SecurityException -> 0x0041, NoSuchMethodException -> 0x003a, ClassNotFoundException -> 0x0033 }
            INTERFACES_TO_PROXY = r0     // Catch:{ SecurityException -> 0x0041, NoSuchMethodException -> 0x003a, ClassNotFoundException -> 0x0033 }
            goto L_0x0050
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
            r0 = move-exception
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            r1.<init>(r0)
            throw r1
        L_0x0048:
            java.lang.Class[] r0 = new java.lang.Class[r2]
            java.lang.Class<com.mysql.jdbc.LoadBalancedConnection> r2 = com.mysql.jdbc.LoadBalancedConnection.class
            r0[r1] = r2
            INTERFACES_TO_PROXY = r0
        L_0x0050:
            r0 = 0
            nullLBConnectionInstance = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.LoadBalancedConnectionProxy.<clinit>():void");
    }

    public static LoadBalancedConnection createProxyInstance(List<String> hosts, Properties props) throws SQLException {
        return (LoadBalancedConnection) Proxy.newProxyInstance(LoadBalancedConnection.class.getClassLoader(), INTERFACES_TO_PROXY, new LoadBalancedConnectionProxy(hosts, props));
    }

    private LoadBalancedConnectionProxy(List<String> hosts, Properties props) throws SQLException {
        List<String> hosts2;
        Properties properties = props;
        String group = properties.getProperty("loadBalanceConnectionGroup", (String) null);
        String enableJMXAsString = properties.getProperty("loadBalanceEnableJMX", "false");
        try {
            boolean enableJMX = Boolean.parseBoolean(enableJMXAsString);
            if (group != null) {
                this.connectionGroup = ConnectionGroupManager.getConnectionGroupInstance(group);
                if (enableJMX) {
                    ConnectionGroupManager.registerJmx();
                }
                this.connectionGroupProxyID = this.connectionGroup.registerConnectionProxy(this, hosts);
                hosts2 = new ArrayList<>(this.connectionGroup.getInitialHosts());
            } else {
                hosts2 = hosts;
            }
            int numHosts = initializeHostsSpecs(hosts2, properties);
            this.liveConnections = new HashMap(numHosts);
            this.hostsToListIndexMap = new HashMap(numHosts);
            for (int i = 0; i < numHosts; i++) {
                this.hostsToListIndexMap.put(this.hostList.get(i), Integer.valueOf(i));
            }
            this.connectionsToHostsMap = new HashMap(numHosts);
            this.responseTimes = new long[numHosts];
            String retriesAllDownAsString = this.localProps.getProperty("retriesAllDown", "120");
            try {
                this.retriesAllDown = Integer.parseInt(retriesAllDownAsString);
                String blacklistTimeoutAsString = this.localProps.getProperty(BLACKLIST_TIMEOUT_PROPERTY_KEY, "0");
                try {
                    this.globalBlacklistTimeout = Integer.parseInt(blacklistTimeoutAsString);
                    String hostRemovalGracePeriodAsString = this.localProps.getProperty(HOST_REMOVAL_GRACE_PERIOD_PROPERTY_KEY, "15000");
                    try {
                        this.hostRemovalGracePeriod = Integer.parseInt(hostRemovalGracePeriodAsString);
                        String strategy = this.localProps.getProperty("loadBalanceStrategy", "random");
                        if ("random".equals(strategy)) {
                            this.balancer = (BalanceStrategy) Util.loadExtensions((Connection) null, properties, RandomBalanceStrategy.class.getName(), "InvalidLoadBalanceStrategy", (ExceptionInterceptor) null).get(0);
                            String str = group;
                        } else if ("bestResponseTime".equals(strategy)) {
                            this.balancer = (BalanceStrategy) Util.loadExtensions((Connection) null, properties, BestResponseTimeBalanceStrategy.class.getName(), "InvalidLoadBalanceStrategy", (ExceptionInterceptor) null).get(0);
                            String str2 = group;
                        } else if ("serverAffinity".equals(strategy)) {
                            this.balancer = (BalanceStrategy) Util.loadExtensions((Connection) null, properties, ServerAffinityStrategy.class.getName(), "InvalidLoadBalanceStrategy", (ExceptionInterceptor) null).get(0);
                            String str3 = group;
                        } else {
                            String str4 = group;
                            this.balancer = (BalanceStrategy) Util.loadExtensions((Connection) null, properties, strategy, "InvalidLoadBalanceStrategy", (ExceptionInterceptor) null).get(0);
                        }
                        String autoCommitSwapThresholdAsString = properties.getProperty("loadBalanceAutoCommitStatementThreshold", "0");
                        try {
                            this.autoCommitSwapThreshold = Integer.parseInt(autoCommitSwapThresholdAsString);
                            String autoCommitSwapRegex = properties.getProperty("loadBalanceAutoCommitStatementRegex", "");
                            if (!"".equals(autoCommitSwapRegex)) {
                                try {
                                    "".matches(autoCommitSwapRegex);
                                } catch (Exception e) {
                                    Exception exc = e;
                                    throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceAutoCommitStatementRegex", new Object[]{autoCommitSwapRegex}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
                                }
                            }
                            if (this.autoCommitSwapThreshold > 0) {
                                String statementInterceptors = this.localProps.getProperty("statementInterceptors");
                                if (statementInterceptors == null) {
                                    this.localProps.setProperty("statementInterceptors", "com.mysql.jdbc.LoadBalancedAutoCommitInterceptor");
                                    String str5 = statementInterceptors;
                                } else if (statementInterceptors.length() > 0) {
                                    String str6 = statementInterceptors;
                                    this.localProps.setProperty("statementInterceptors", statementInterceptors + ",com.mysql.jdbc.LoadBalancedAutoCommitInterceptor");
                                }
                                properties.setProperty("statementInterceptors", this.localProps.getProperty("statementInterceptors"));
                            }
                            this.balancer.init((Connection) null, properties);
                            this.exceptionChecker = (LoadBalanceExceptionChecker) Util.loadExtensions((Connection) null, properties, this.localProps.getProperty("loadBalanceExceptionChecker", "com.mysql.jdbc.StandardLoadBalanceExceptionChecker"), "InvalidLoadBalanceExceptionChecker", (ExceptionInterceptor) null).get(0);
                            pickNewConnection();
                        } catch (NumberFormatException e2) {
                            throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceAutoCommitStatementThreshold", new Object[]{autoCommitSwapThresholdAsString}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
                        }
                    } catch (NumberFormatException e3) {
                        String str7 = group;
                        throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceHostRemovalGracePeriod", new Object[]{hostRemovalGracePeriodAsString}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
                    }
                } catch (NumberFormatException e4) {
                    String str8 = group;
                    throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceBlacklistTimeout", new Object[]{blacklistTimeoutAsString}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
                }
            } catch (NumberFormatException e5) {
                String str9 = group;
                throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForRetriesAllDown", new Object[]{retriesAllDownAsString}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
            }
        } catch (Exception e6) {
            List<String> list = hosts;
            String str10 = group;
            Exception exc2 = e6;
            throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceEnableJMX", new Object[]{enableJMXAsString}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
        }
    }

    /* access modifiers changed from: package-private */
    public MySQLConnection getNewWrapperForThisAsConnection() throws SQLException {
        if (!Util.isJdbc4() && JDBC_4_LB_CONNECTION_CTOR == null) {
            return new LoadBalancedMySQLConnection(this);
        }
        return (MySQLConnection) Util.handleNewInstance(JDBC_4_LB_CONNECTION_CTOR, new Object[]{this}, (ExceptionInterceptor) null);
    }

    /* access modifiers changed from: protected */
    public void propagateProxyDown(MySQLConnection proxyConn) {
        for (ConnectionImpl c : this.liveConnections.values()) {
            c.setProxy(proxyConn);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean shouldExceptionTriggerConnectionSwitch(Throwable t) {
        return (t instanceof SQLException) && this.exceptionChecker.shouldExceptionTriggerFailover((SQLException) t);
    }

    /* access modifiers changed from: package-private */
    public boolean isMasterConnection() {
        return true;
    }

    /* access modifiers changed from: package-private */
    public synchronized void invalidateConnection(MySQLConnection conn) throws SQLException {
        super.invalidateConnection(conn);
        if (isGlobalBlacklistEnabled()) {
            addToGlobalBlacklist(this.connectionsToHostsMap.get(conn));
        }
        this.liveConnections.remove(this.connectionsToHostsMap.get(conn));
        Object mappedHost = this.connectionsToHostsMap.remove(conn);
        if (mappedHost != null && this.hostsToListIndexMap.containsKey(mappedHost)) {
            int hostIndex = this.hostsToListIndexMap.get(mappedHost).intValue();
            synchronized (this.responseTimes) {
                this.responseTimes[hostIndex] = 0;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void pickNewConnection() throws SQLException {
        if (this.isClosed && this.closedExplicitly) {
            return;
        }
        if (this.currentConnection == null) {
            this.currentConnection = this.balancer.pickConnection(this, Collections.unmodifiableList(this.hostList), Collections.unmodifiableMap(this.liveConnections), (long[]) this.responseTimes.clone(), this.retriesAllDown);
            return;
        }
        if (this.currentConnection.isClosed()) {
            invalidateCurrentConnection();
        }
        int pingTimeout = this.currentConnection.getLoadBalancePingTimeout();
        boolean pingBeforeReturn = this.currentConnection.getLoadBalanceValidateConnectionOnSwapServer();
        int hostsTried = 0;
        int hostsToTry = this.hostList.size();
        while (hostsTried < hostsToTry) {
            try {
                ConnectionImpl newConn = this.balancer.pickConnection(this, Collections.unmodifiableList(this.hostList), Collections.unmodifiableMap(this.liveConnections), (long[]) this.responseTimes.clone(), this.retriesAllDown);
                if (this.currentConnection != null) {
                    if (pingBeforeReturn) {
                        if (pingTimeout == 0) {
                            newConn.ping();
                        } else {
                            newConn.pingInternal(true, pingTimeout);
                        }
                    }
                    syncSessionState(this.currentConnection, newConn);
                }
                this.currentConnection = newConn;
                return;
            } catch (SQLException e) {
                if (shouldExceptionTriggerConnectionSwitch(e) && 0 != 0) {
                    invalidateConnection((MySQLConnection) null);
                }
                hostsTried++;
            }
        }
        this.isClosed = true;
        this.closedReason = "Connection closed after inability to pick valid new connection during load-balance.";
    }

    public synchronized ConnectionImpl createConnectionForHost(String hostPortSpec) throws SQLException {
        ConnectionImpl conn;
        conn = super.createConnectionForHost(hostPortSpec);
        this.liveConnections.put(hostPortSpec, conn);
        this.connectionsToHostsMap.put(conn, hostPortSpec);
        this.totalPhysicalConnections++;
        Iterator i$ = conn.getStatementInterceptorsInstances().iterator();
        while (true) {
            if (!i$.hasNext()) {
                break;
            }
            StatementInterceptorV2 stmtInterceptor = i$.next();
            if (stmtInterceptor instanceof LoadBalancedAutoCommitInterceptor) {
                ((LoadBalancedAutoCommitInterceptor) stmtInterceptor).resumeCounters();
                break;
            }
        }
        return conn;
    }

    /* JADX WARNING: type inference failed for: r2v2, types: [com.mysql.jdbc.StatementInterceptorV2] */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void syncSessionState(com.mysql.jdbc.Connection r5, com.mysql.jdbc.Connection r6, boolean r7) throws java.sql.SQLException {
        /*
            r4 = this;
            r0 = 0
            r1 = r6
            com.mysql.jdbc.MySQLConnection r1 = (com.mysql.jdbc.MySQLConnection) r1
            java.util.List r1 = r1.getStatementInterceptorsInstances()
            java.util.Iterator r1 = r1.iterator()
        L_0x000c:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x0024
            java.lang.Object r2 = r1.next()
            com.mysql.jdbc.StatementInterceptorV2 r2 = (com.mysql.jdbc.StatementInterceptorV2) r2
            boolean r3 = r2 instanceof com.mysql.jdbc.LoadBalancedAutoCommitInterceptor
            if (r3 == 0) goto L_0x0023
            r0 = r2
            com.mysql.jdbc.LoadBalancedAutoCommitInterceptor r0 = (com.mysql.jdbc.LoadBalancedAutoCommitInterceptor) r0
            r0.pauseCounters()
            goto L_0x0024
        L_0x0023:
            goto L_0x000c
        L_0x0024:
            super.syncSessionState(r5, r6, r7)
            if (r0 == 0) goto L_0x002c
            r0.resumeCounters()
        L_0x002c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.LoadBalancedConnectionProxy.syncSessionState(com.mysql.jdbc.Connection, com.mysql.jdbc.Connection, boolean):void");
    }

    private synchronized void closeAllConnections() {
        for (MySQLConnection c : this.liveConnections.values()) {
            try {
                c.close();
            } catch (SQLException e) {
            }
        }
        if (!this.isClosed) {
            this.balancer.destroy();
            ConnectionGroup connectionGroup2 = this.connectionGroup;
            if (connectionGroup2 != null) {
                connectionGroup2.closeConnectionProxy(this);
            }
        }
        this.liveConnections.clear();
        this.connectionsToHostsMap.clear();
    }

    /* access modifiers changed from: package-private */
    public synchronized void doClose() {
        closeAllConnections();
    }

    /* access modifiers changed from: package-private */
    public synchronized void doAbortInternal() {
        for (MySQLConnection c : this.liveConnections.values()) {
            try {
                c.abortInternal();
            } catch (SQLException e) {
            }
        }
        if (!this.isClosed) {
            this.balancer.destroy();
            ConnectionGroup connectionGroup2 = this.connectionGroup;
            if (connectionGroup2 != null) {
                connectionGroup2.closeConnectionProxy(this);
            }
        }
        this.liveConnections.clear();
        this.connectionsToHostsMap.clear();
    }

    /* access modifiers changed from: package-private */
    public synchronized void doAbort(Executor executor) {
        for (MySQLConnection c : this.liveConnections.values()) {
            try {
                c.abort(executor);
            } catch (SQLException e) {
            }
        }
        if (!this.isClosed) {
            this.balancer.destroy();
            ConnectionGroup connectionGroup2 = this.connectionGroup;
            if (connectionGroup2 != null) {
                connectionGroup2.closeConnectionProxy(this);
            }
        }
        this.liveConnections.clear();
        this.connectionsToHostsMap.clear();
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        	at java.util.ArrayList.get(ArrayList.java:435)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:693)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:693)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processExcHandler(RegionMaker.java:1043)
        	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:975)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
        */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:50:0x00c8=Splitter:B:50:0x00c8, B:77:0x011a=Splitter:B:77:0x011a} */
    public synchronized java.lang.Object invokeMore(java.lang.Object r13, java.lang.reflect.Method r14, java.lang.Object[] r15) throws java.lang.Throwable {
        /*
            r12 = this;
            monitor-enter(r12)
            java.lang.String r0 = r14.getName()     // Catch:{ all -> 0x016c }
            boolean r1 = r12.isClosed     // Catch:{ all -> 0x016c }
            r2 = 0
            if (r1 == 0) goto L_0x0051
            boolean r1 = r12.allowedOnClosedConnection(r14)     // Catch:{ all -> 0x016c }
            if (r1 != 0) goto L_0x0051
            java.lang.Class[] r1 = r14.getExceptionTypes()     // Catch:{ all -> 0x016c }
            int r1 = r1.length     // Catch:{ all -> 0x016c }
            if (r1 <= 0) goto L_0x0051
            boolean r1 = r12.autoReconnect     // Catch:{ all -> 0x016c }
            r3 = 0
            if (r1 == 0) goto L_0x002a
            boolean r1 = r12.closedExplicitly     // Catch:{ all -> 0x016c }
            if (r1 != 0) goto L_0x002a
            r12.currentConnection = r3     // Catch:{ all -> 0x016c }
            r12.pickNewConnection()     // Catch:{ all -> 0x016c }
            r12.isClosed = r2     // Catch:{ all -> 0x016c }
            r12.closedReason = r3     // Catch:{ all -> 0x016c }
            goto L_0x0051
        L_0x002a:
            java.lang.String r1 = "No operations allowed after connection closed."
            java.lang.String r2 = r12.closedReason     // Catch:{ all -> 0x016c }
            if (r2 == 0) goto L_0x004a
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x016c }
            r2.<init>()     // Catch:{ all -> 0x016c }
            java.lang.StringBuilder r2 = r2.append(r1)     // Catch:{ all -> 0x016c }
            java.lang.String r4 = " "
            java.lang.StringBuilder r2 = r2.append(r4)     // Catch:{ all -> 0x016c }
            java.lang.String r4 = r12.closedReason     // Catch:{ all -> 0x016c }
            java.lang.StringBuilder r2 = r2.append(r4)     // Catch:{ all -> 0x016c }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x016c }
            r1 = r2
        L_0x004a:
            java.lang.String r2 = "08003"
            java.sql.SQLException r2 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (com.mysql.jdbc.ExceptionInterceptor) r3)     // Catch:{ all -> 0x016c }
            throw r2     // Catch:{ all -> 0x016c }
        L_0x0051:
            boolean r1 = r12.inTransaction     // Catch:{ all -> 0x016c }
            if (r1 != 0) goto L_0x0065
            r1 = 1
            r12.inTransaction = r1     // Catch:{ all -> 0x016c }
            long r3 = java.lang.System.nanoTime()     // Catch:{ all -> 0x016c }
            r12.transactionStartTime = r3     // Catch:{ all -> 0x016c }
            long r3 = r12.transactionCount     // Catch:{ all -> 0x016c }
            r5 = 1
            long r3 = r3 + r5
            r12.transactionCount = r3     // Catch:{ all -> 0x016c }
        L_0x0065:
            r1 = 0
            com.mysql.jdbc.MySQLConnection r3 = r12.thisAsConnection     // Catch:{ InvocationTargetException -> 0x00cf }
            java.lang.Object r3 = r14.invoke(r3, r15)     // Catch:{ InvocationTargetException -> 0x00cf }
            r1 = r3
            if (r1 == 0) goto L_0x0082
            boolean r3 = r1 instanceof com.mysql.jdbc.Statement     // Catch:{ InvocationTargetException -> 0x00cf }
            if (r3 == 0) goto L_0x0079
            r3 = r1
            com.mysql.jdbc.Statement r3 = (com.mysql.jdbc.Statement) r3     // Catch:{ InvocationTargetException -> 0x00cf }
            r3.setPingTarget(r12)     // Catch:{ InvocationTargetException -> 0x00cf }
        L_0x0079:
            java.lang.Class r3 = r14.getReturnType()     // Catch:{ InvocationTargetException -> 0x00cf }
            java.lang.Object r3 = r12.proxyIfReturnTypeIsJdbcInterface(r3, r1)     // Catch:{ InvocationTargetException -> 0x00cf }
            r1 = r3
        L_0x0082:
            java.lang.String r3 = "commit"
            boolean r3 = r3.equals(r0)     // Catch:{ all -> 0x016c }
            if (r3 != 0) goto L_0x0093
            java.lang.String r3 = "rollback"
            boolean r3 = r3.equals(r0)     // Catch:{ all -> 0x016c }
            if (r3 == 0) goto L_0x011d
        L_0x0093:
            r12.inTransaction = r2     // Catch:{ all -> 0x016c }
            java.util.Map<com.mysql.jdbc.ConnectionImpl, java.lang.String> r2 = r12.connectionsToHostsMap     // Catch:{ all -> 0x016c }
            com.mysql.jdbc.MySQLConnection r3 = r12.currentConnection     // Catch:{ all -> 0x016c }
            java.lang.Object r2 = r2.get(r3)     // Catch:{ all -> 0x016c }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x016c }
            if (r2 == 0) goto L_0x00c9
            long[] r3 = r12.responseTimes     // Catch:{ all -> 0x016c }
            monitor-enter(r3)     // Catch:{ all -> 0x016c }
            java.util.Map<java.lang.String, java.lang.Integer> r4 = r12.hostsToListIndexMap     // Catch:{ all -> 0x00c6 }
            java.lang.Object r4 = r4.get(r2)     // Catch:{ all -> 0x00c6 }
            java.lang.Integer r4 = (java.lang.Integer) r4     // Catch:{ all -> 0x00c6 }
            if (r4 == 0) goto L_0x00c4
            int r5 = r4.intValue()     // Catch:{ all -> 0x00c6 }
            long[] r6 = r12.responseTimes     // Catch:{ all -> 0x00c6 }
            int r7 = r6.length     // Catch:{ all -> 0x00c6 }
            if (r5 >= r7) goto L_0x00c4
            int r5 = r4.intValue()     // Catch:{ all -> 0x00c6 }
            long r7 = java.lang.System.nanoTime()     // Catch:{ all -> 0x00c6 }
            long r9 = r12.transactionStartTime     // Catch:{ all -> 0x00c6 }
            long r7 = r7 - r9
            r6[r5] = r7     // Catch:{ all -> 0x00c6 }
        L_0x00c4:
            monitor-exit(r3)     // Catch:{ all -> 0x00c6 }
            goto L_0x00c9
        L_0x00c6:
            r4 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x00c6 }
        L_0x00c8:
            throw r4     // Catch:{ all -> 0x016c }
        L_0x00c9:
            r12.pickNewConnection()     // Catch:{ all -> 0x016c }
            goto L_0x011d
        L_0x00cd:
            r3 = move-exception
            goto L_0x0121
        L_0x00cf:
            r3 = move-exception
            r12.dealWithInvocationException(r3)     // Catch:{ all -> 0x00cd }
            java.lang.String r3 = "commit"
            boolean r3 = r3.equals(r0)     // Catch:{ all -> 0x016c }
            if (r3 != 0) goto L_0x00e4
            java.lang.String r3 = "rollback"
            boolean r3 = r3.equals(r0)     // Catch:{ all -> 0x016c }
            if (r3 == 0) goto L_0x011d
        L_0x00e4:
            r12.inTransaction = r2     // Catch:{ all -> 0x016c }
            java.util.Map<com.mysql.jdbc.ConnectionImpl, java.lang.String> r2 = r12.connectionsToHostsMap     // Catch:{ all -> 0x016c }
            com.mysql.jdbc.MySQLConnection r3 = r12.currentConnection     // Catch:{ all -> 0x016c }
            java.lang.Object r2 = r2.get(r3)     // Catch:{ all -> 0x016c }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x016c }
            if (r2 == 0) goto L_0x011a
            long[] r3 = r12.responseTimes     // Catch:{ all -> 0x016c }
            monitor-enter(r3)     // Catch:{ all -> 0x016c }
            java.util.Map<java.lang.String, java.lang.Integer> r4 = r12.hostsToListIndexMap     // Catch:{ all -> 0x0117 }
            java.lang.Object r4 = r4.get(r2)     // Catch:{ all -> 0x0117 }
            java.lang.Integer r4 = (java.lang.Integer) r4     // Catch:{ all -> 0x0117 }
            if (r4 == 0) goto L_0x0115
            int r5 = r4.intValue()     // Catch:{ all -> 0x0117 }
            long[] r6 = r12.responseTimes     // Catch:{ all -> 0x0117 }
            int r7 = r6.length     // Catch:{ all -> 0x0117 }
            if (r5 >= r7) goto L_0x0115
            int r5 = r4.intValue()     // Catch:{ all -> 0x0117 }
            long r7 = java.lang.System.nanoTime()     // Catch:{ all -> 0x0117 }
            long r9 = r12.transactionStartTime     // Catch:{ all -> 0x0117 }
            long r7 = r7 - r9
            r6[r5] = r7     // Catch:{ all -> 0x0117 }
        L_0x0115:
            monitor-exit(r3)     // Catch:{ all -> 0x0117 }
            goto L_0x011a
        L_0x0117:
            r4 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0117 }
            goto L_0x00c8
        L_0x011a:
            r12.pickNewConnection()     // Catch:{ all -> 0x016c }
        L_0x011d:
            monitor-exit(r12)
            return r1
        L_0x0121:
            java.lang.String r4 = "commit"
            boolean r4 = r4.equals(r0)     // Catch:{ all -> 0x016c }
            if (r4 != 0) goto L_0x0131
            java.lang.String r4 = "rollback"
            boolean r4 = r4.equals(r0)     // Catch:{ all -> 0x016c }
            if (r4 == 0) goto L_0x016a
        L_0x0131:
            r12.inTransaction = r2     // Catch:{ all -> 0x016c }
            java.util.Map<com.mysql.jdbc.ConnectionImpl, java.lang.String> r2 = r12.connectionsToHostsMap     // Catch:{ all -> 0x016c }
            com.mysql.jdbc.MySQLConnection r4 = r12.currentConnection     // Catch:{ all -> 0x016c }
            java.lang.Object r2 = r2.get(r4)     // Catch:{ all -> 0x016c }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x016c }
            if (r2 == 0) goto L_0x0167
            long[] r4 = r12.responseTimes     // Catch:{ all -> 0x016c }
            monitor-enter(r4)     // Catch:{ all -> 0x016c }
            java.util.Map<java.lang.String, java.lang.Integer> r5 = r12.hostsToListIndexMap     // Catch:{ all -> 0x0164 }
            java.lang.Object r5 = r5.get(r2)     // Catch:{ all -> 0x0164 }
            java.lang.Integer r5 = (java.lang.Integer) r5     // Catch:{ all -> 0x0164 }
            if (r5 == 0) goto L_0x0162
            int r6 = r5.intValue()     // Catch:{ all -> 0x0164 }
            long[] r7 = r12.responseTimes     // Catch:{ all -> 0x0164 }
            int r8 = r7.length     // Catch:{ all -> 0x0164 }
            if (r6 >= r8) goto L_0x0162
            int r6 = r5.intValue()     // Catch:{ all -> 0x0164 }
            long r8 = java.lang.System.nanoTime()     // Catch:{ all -> 0x0164 }
            long r10 = r12.transactionStartTime     // Catch:{ all -> 0x0164 }
            long r8 = r8 - r10
            r7[r6] = r8     // Catch:{ all -> 0x0164 }
        L_0x0162:
            monitor-exit(r4)     // Catch:{ all -> 0x0164 }
            goto L_0x0167
        L_0x0164:
            r3 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0164 }
            throw r3     // Catch:{ all -> 0x016c }
        L_0x0167:
            r12.pickNewConnection()     // Catch:{ all -> 0x016c }
        L_0x016a:
            throw r3     // Catch:{ all -> 0x016c }
        L_0x016c:
            r13 = move-exception
            monitor-exit(r12)
            throw r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.LoadBalancedConnectionProxy.invokeMore(java.lang.Object, java.lang.reflect.Method, java.lang.Object[]):java.lang.Object");
    }

    public synchronized void doPing() throws SQLException {
        SQLException se = null;
        boolean foundHost = false;
        int pingTimeout = this.currentConnection.getLoadBalancePingTimeout();
        for (String host : this.hostList) {
            ConnectionImpl conn = this.liveConnections.get(host);
            if (conn != null) {
                if (pingTimeout == 0) {
                    try {
                        conn.ping();
                    } catch (SQLException e) {
                        if (!host.equals(this.connectionsToHostsMap.get(this.currentConnection))) {
                            if (!e.getMessage().equals(Messages.getString("Connection.exceededConnectionLifetime"))) {
                                se = e;
                                if (isGlobalBlacklistEnabled()) {
                                    addToGlobalBlacklist(host);
                                }
                            } else if (se == null) {
                                se = e;
                            }
                            this.liveConnections.remove(this.connectionsToHostsMap.get(conn));
                        } else {
                            closeAllConnections();
                            this.isClosed = true;
                            this.closedReason = "Connection closed because ping of current connection failed.";
                            throw e;
                        }
                    }
                } else {
                    conn.pingInternal(true, pingTimeout);
                }
                foundHost = true;
            }
        }
        if (!foundHost) {
            closeAllConnections();
            this.isClosed = true;
            this.closedReason = "Connection closed due to inability to ping any active connections.";
            if (se == null) {
                ((ConnectionImpl) this.currentConnection).throwConnectionClosedException();
            } else {
                throw se;
            }
        }
    }

    public void addToGlobalBlacklist(String host, long timeout) {
        if (isGlobalBlacklistEnabled()) {
            synchronized (globalBlacklist) {
                globalBlacklist.put(host, Long.valueOf(timeout));
            }
        }
    }

    public void addToGlobalBlacklist(String host) {
        addToGlobalBlacklist(host, System.currentTimeMillis() + ((long) this.globalBlacklistTimeout));
    }

    public boolean isGlobalBlacklistEnabled() {
        return this.globalBlacklistTimeout > 0;
    }

    public synchronized Map<String, Long> getGlobalBlacklist() {
        if (isGlobalBlacklistEnabled()) {
            Map<String, Long> blacklistClone = new HashMap<>(globalBlacklist.size());
            synchronized (globalBlacklist) {
                blacklistClone.putAll(globalBlacklist);
            }
            Set<String> keys = blacklistClone.keySet();
            keys.retainAll(this.hostList);
            Iterator<String> i = keys.iterator();
            while (i.hasNext()) {
                String host = i.next();
                Long timeout = globalBlacklist.get(host);
                if (timeout != null && timeout.longValue() < System.currentTimeMillis()) {
                    synchronized (globalBlacklist) {
                        globalBlacklist.remove(host);
                    }
                    i.remove();
                }
            }
            if (keys.size() != this.hostList.size()) {
                return blacklistClone;
            }
            return new HashMap(1);
        } else if (this.hostsToRemove.isEmpty()) {
            return new HashMap(1);
        } else {
            HashMap<String, Long> fakedBlacklist = new HashMap<>();
            for (String h : this.hostsToRemove) {
                fakedBlacklist.put(h, Long.valueOf(System.currentTimeMillis() + 5000));
            }
            return fakedBlacklist;
        }
    }

    public void removeHostWhenNotInUse(String hostPortPair) throws SQLException {
        int timeBetweenChecks = this.hostRemovalGracePeriod;
        if (timeBetweenChecks <= 0) {
            removeHost(hostPortPair);
            return;
        }
        if (timeBetweenChecks > 1000) {
            timeBetweenChecks = 1000;
        }
        synchronized (this) {
            addToGlobalBlacklist(hostPortPair, System.currentTimeMillis() + ((long) this.hostRemovalGracePeriod) + ((long) timeBetweenChecks));
            long cur = System.currentTimeMillis();
            while (System.currentTimeMillis() < ((long) this.hostRemovalGracePeriod) + cur) {
                this.hostsToRemove.add(hostPortPair);
                if (!hostPortPair.equals(this.currentConnection.getHostPortPair())) {
                    removeHost(hostPortPair);
                    return;
                }
                try {
                    Thread.sleep((long) timeBetweenChecks);
                } catch (InterruptedException e) {
                }
            }
            removeHost(hostPortPair);
        }
    }

    public synchronized void removeHost(String hostPortPair) throws SQLException {
        ConnectionGroup connectionGroup2 = this.connectionGroup;
        if (connectionGroup2 != null && connectionGroup2.getInitialHosts().size() == 1) {
            if (this.connectionGroup.getInitialHosts().contains(hostPortPair)) {
                throw SQLError.createSQLException("Cannot remove only configured host.", (ExceptionInterceptor) null);
            }
        }
        this.hostsToRemove.add(hostPortPair);
        this.connectionsToHostsMap.remove(this.liveConnections.remove(hostPortPair));
        if (this.hostsToListIndexMap.remove(hostPortPair) != null) {
            long[] newResponseTimes = new long[(this.responseTimes.length - 1)];
            int newIdx = 0;
            for (String h : this.hostList) {
                if (!this.hostsToRemove.contains(h)) {
                    Integer idx = this.hostsToListIndexMap.get(h);
                    if (idx != null) {
                        int intValue = idx.intValue();
                        long[] jArr = this.responseTimes;
                        if (intValue < jArr.length) {
                            newResponseTimes[newIdx] = jArr[idx.intValue()];
                        }
                    }
                    this.hostsToListIndexMap.put(h, Integer.valueOf(newIdx));
                    newIdx++;
                }
            }
            this.responseTimes = newResponseTimes;
        }
        if (hostPortPair.equals(this.currentConnection.getHostPortPair())) {
            invalidateConnection(this.currentConnection);
            pickNewConnection();
        }
    }

    public synchronized boolean addHost(String hostPortPair) {
        if (this.hostsToListIndexMap.containsKey(hostPortPair)) {
            return false;
        }
        long[] jArr = this.responseTimes;
        long[] newResponseTimes = new long[(jArr.length + 1)];
        System.arraycopy(jArr, 0, newResponseTimes, 0, jArr.length);
        this.responseTimes = newResponseTimes;
        if (!this.hostList.contains(hostPortPair)) {
            this.hostList.add(hostPortPair);
        }
        this.hostsToListIndexMap.put(hostPortPair, Integer.valueOf(this.responseTimes.length - 1));
        this.hostsToRemove.remove(hostPortPair);
        return true;
    }

    public synchronized boolean inTransaction() {
        return this.inTransaction;
    }

    public synchronized long getTransactionCount() {
        return this.transactionCount;
    }

    public synchronized long getActivePhysicalConnectionCount() {
        return (long) this.liveConnections.size();
    }

    public synchronized long getTotalPhysicalConnectionCount() {
        return this.totalPhysicalConnections;
    }

    public synchronized long getConnectionGroupProxyID() {
        return this.connectionGroupProxyID;
    }

    public synchronized String getCurrentActiveHost() {
        Object o;
        MySQLConnection c = this.currentConnection;
        if (c == null || (o = this.connectionsToHostsMap.get(c)) == null) {
            return null;
        }
        return o.toString();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0017, code lost:
        return 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized long getCurrentTransactionDuration() {
        /*
            r5 = this;
            monitor-enter(r5)
            boolean r0 = r5.inTransaction     // Catch:{ all -> 0x0018 }
            r1 = 0
            if (r0 == 0) goto L_0x0016
            long r3 = r5.transactionStartTime     // Catch:{ all -> 0x0018 }
            int r0 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x0016
            long r0 = java.lang.System.nanoTime()     // Catch:{ all -> 0x0018 }
            long r2 = r5.transactionStartTime     // Catch:{ all -> 0x0018 }
            long r0 = r0 - r2
            monitor-exit(r5)
            return r0
        L_0x0016:
            monitor-exit(r5)
            return r1
        L_0x0018:
            r0 = move-exception
            monitor-exit(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.LoadBalancedConnectionProxy.getCurrentTransactionDuration():long");
    }

    private static class NullLoadBalancedConnectionProxy implements InvocationHandler {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            SQLException exceptionToThrow = SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.unusableConnection"), SQLError.SQL_STATE_INVALID_TRANSACTION_STATE, (int) MysqlErrorNumbers.ERROR_CODE_NULL_LOAD_BALANCED_CONNECTION, true, (ExceptionInterceptor) null);
            Class<?>[] arr$ = method.getExceptionTypes();
            int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                if (!arr$[i$].isAssignableFrom(exceptionToThrow.getClass())) {
                    i$++;
                } else {
                    throw exceptionToThrow;
                }
            }
            throw new IllegalStateException(exceptionToThrow.getMessage(), exceptionToThrow);
        }
    }

    static synchronized LoadBalancedConnection getNullLoadBalancedConnectionInstance() {
        LoadBalancedConnection loadBalancedConnection;
        synchronized (LoadBalancedConnectionProxy.class) {
            if (nullLBConnectionInstance == null) {
                nullLBConnectionInstance = (LoadBalancedConnection) Proxy.newProxyInstance(LoadBalancedConnection.class.getClassLoader(), INTERFACES_TO_PROXY, new NullLoadBalancedConnectionProxy());
            }
            loadBalancedConnection = nullLBConnectionInstance;
        }
        return loadBalancedConnection;
    }
}
