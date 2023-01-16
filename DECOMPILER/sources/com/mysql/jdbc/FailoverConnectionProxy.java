package com.mysql.jdbc;

import com.mysql.jdbc.MultiHostConnectionProxy;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

public class FailoverConnectionProxy extends MultiHostConnectionProxy {
    private static final int DEFAULT_PRIMARY_HOST_INDEX = 0;
    private static Class<?>[] INTERFACES_TO_PROXY = null;
    private static final String METHOD_COMMIT = "commit";
    private static final String METHOD_ROLLBACK = "rollback";
    private static final String METHOD_SET_AUTO_COMMIT = "setAutoCommit";
    private static final String METHOD_SET_READ_ONLY = "setReadOnly";
    private static final int NO_CONNECTION_INDEX = -1;
    private int currentHostIndex = -1;
    private boolean enableFallBackToPrimaryHost;
    /* access modifiers changed from: private */
    public boolean explicitlyAutoCommit;
    private Boolean explicitlyReadOnly;
    private boolean failoverReadOnly;
    private long primaryHostFailTimeMillis;
    private int primaryHostIndex;
    private long queriesBeforeRetryPrimaryHost;
    private long queriesIssuedSinceFailover;
    private int retriesAllDown;
    private int secondsBeforeRetryPrimaryHost;

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: java.lang.Class<?>[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    static {
        /*
            boolean r0 = com.mysql.jdbc.Util.isJdbc4()
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x001c
            java.lang.Class[] r0 = new java.lang.Class[r2]     // Catch:{ ClassNotFoundException -> 0x0015 }
            java.lang.String r2 = "com.mysql.jdbc.JDBC4MySQLConnection"
            java.lang.Class r2 = java.lang.Class.forName(r2)     // Catch:{ ClassNotFoundException -> 0x0015 }
            r0[r1] = r2     // Catch:{ ClassNotFoundException -> 0x0015 }
            INTERFACES_TO_PROXY = r0     // Catch:{ ClassNotFoundException -> 0x0015 }
            goto L_0x0024
        L_0x0015:
            r0 = move-exception
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            r1.<init>(r0)
            throw r1
        L_0x001c:
            java.lang.Class[] r0 = new java.lang.Class[r2]
            java.lang.Class<com.mysql.jdbc.MySQLConnection> r2 = com.mysql.jdbc.MySQLConnection.class
            r0[r1] = r2
            INTERFACES_TO_PROXY = r0
        L_0x0024:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.FailoverConnectionProxy.<clinit>():void");
    }

    class FailoverJdbcInterfaceProxy extends MultiHostConnectionProxy.JdbcInterfaceProxy {
        FailoverJdbcInterfaceProxy(Object toInvokeOn) {
            super(toInvokeOn);
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            boolean isExecute = method.getName().startsWith("execute");
            if (FailoverConnectionProxy.this.connectedToSecondaryHost() && isExecute) {
                FailoverConnectionProxy.this.incrementQueriesIssuedSinceFailover();
            }
            Object result = super.invoke(proxy, method, args);
            if (FailoverConnectionProxy.this.explicitlyAutoCommit && isExecute && FailoverConnectionProxy.this.readyToFallBackToPrimaryHost()) {
                FailoverConnectionProxy.this.fallBackToPrimaryIfAvailable();
            }
            return result;
        }
    }

    public static Connection createProxyInstance(List<String> hosts, Properties props) throws SQLException {
        return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), INTERFACES_TO_PROXY, new FailoverConnectionProxy(hosts, props));
    }

    private FailoverConnectionProxy(List<String> hosts, Properties props) throws SQLException {
        super(hosts, props);
        boolean z = false;
        this.primaryHostIndex = 0;
        this.explicitlyReadOnly = null;
        this.explicitlyAutoCommit = true;
        this.enableFallBackToPrimaryHost = true;
        this.primaryHostFailTimeMillis = 0;
        this.queriesIssuedSinceFailover = 0;
        ConnectionPropertiesImpl connProps = new ConnectionPropertiesImpl();
        connProps.initializeProperties(props);
        this.secondsBeforeRetryPrimaryHost = connProps.getSecondsBeforeRetryMaster();
        this.queriesBeforeRetryPrimaryHost = (long) connProps.getQueriesBeforeRetryMaster();
        this.failoverReadOnly = connProps.getFailOverReadOnly();
        this.retriesAllDown = connProps.getRetriesAllDown();
        this.enableFallBackToPrimaryHost = (this.secondsBeforeRetryPrimaryHost > 0 || this.queriesBeforeRetryPrimaryHost > 0) ? true : z;
        pickNewConnection();
        this.explicitlyAutoCommit = this.currentConnection.getAutoCommit();
    }

    /* access modifiers changed from: package-private */
    public MultiHostConnectionProxy.JdbcInterfaceProxy getNewJdbcInterfaceProxy(Object toProxy) {
        return new FailoverJdbcInterfaceProxy(toProxy);
    }

    /* access modifiers changed from: package-private */
    public boolean shouldExceptionTriggerConnectionSwitch(Throwable t) {
        if (!(t instanceof SQLException)) {
            return false;
        }
        String sqlState = ((SQLException) t).getSQLState();
        if ((sqlState == null || !sqlState.startsWith("08")) && !(t instanceof CommunicationsException)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean isMasterConnection() {
        return connectedToPrimaryHost();
    }

    /* access modifiers changed from: package-private */
    public synchronized void pickNewConnection() throws SQLException {
        if (!this.isClosed || !this.closedExplicitly) {
            if (isConnected()) {
                if (!readyToFallBackToPrimaryHost()) {
                    failOver();
                }
            }
            try {
                connectTo(this.primaryHostIndex);
            } catch (SQLException e) {
                resetAutoFallBackCounters();
                failOver(this.primaryHostIndex);
            }
        } else {
            return;
        }
        return;
    }

    /* access modifiers changed from: package-private */
    public synchronized ConnectionImpl createConnectionForHostIndex(int hostIndex) throws SQLException {
        return createConnectionForHost((String) this.hostList.get(hostIndex));
    }

    private synchronized void connectTo(int hostIndex) throws SQLException {
        try {
            switchCurrentConnectionTo(hostIndex, createConnectionForHostIndex(hostIndex));
        } catch (SQLException e) {
            if (this.currentConnection != null) {
                this.currentConnection.getLog().logWarn("Connection to " + (isPrimaryHostIndex(hostIndex) ? "primary" : "secondary") + " host '" + ((String) this.hostList.get(hostIndex)) + "' failed", e);
            }
            throw e;
        }
    }

    private synchronized void switchCurrentConnectionTo(int hostIndex, MySQLConnection connection) throws SQLException {
        boolean readOnly;
        invalidateCurrentConnection();
        if (isPrimaryHostIndex(hostIndex)) {
            Boolean bool = this.explicitlyReadOnly;
            readOnly = bool == null ? false : bool.booleanValue();
        } else if (this.failoverReadOnly) {
            readOnly = true;
        } else {
            Boolean bool2 = this.explicitlyReadOnly;
            if (bool2 != null) {
                readOnly = bool2.booleanValue();
            } else if (this.currentConnection != null) {
                readOnly = this.currentConnection.isReadOnly();
            } else {
                readOnly = false;
            }
        }
        syncSessionState(this.currentConnection, connection, readOnly);
        this.currentConnection = connection;
        this.currentHostIndex = hostIndex;
    }

    private synchronized void failOver() throws SQLException {
        failOver(this.currentHostIndex);
    }

    /* JADX WARNING: Removed duplicated region for block: B:55:0x0066 A[EDGE_INSN: B:55:0x0066->B:46:0x0066 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void failOver(int r14) throws java.sql.SQLException {
        /*
            r13 = this;
            monitor-enter(r13)
            int r0 = r13.currentHostIndex     // Catch:{ all -> 0x006c }
            r1 = 0
            int r2 = r13.nextHost(r14, r1)     // Catch:{ all -> 0x006c }
            r3 = r2
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = -1
            r8 = 1
            if (r0 == r7) goto L_0x0019
            boolean r7 = r13.isPrimaryHostIndex(r0)     // Catch:{ all -> 0x006c }
            if (r7 == 0) goto L_0x0017
            goto L_0x0019
        L_0x0017:
            r7 = r1
            goto L_0x001a
        L_0x0019:
            r7 = r8
        L_0x001a:
            if (r7 != 0) goto L_0x0027
            boolean r9 = r13.isPrimaryHostIndex(r2)     // Catch:{ SQLException -> 0x0025 }
            if (r9 == 0) goto L_0x0023
            goto L_0x0027
        L_0x0023:
            r9 = r1
            goto L_0x0028
        L_0x0025:
            r9 = move-exception
            goto L_0x0039
        L_0x0027:
            r9 = r8
        L_0x0028:
            r7 = r9
            r13.connectTo(r2)     // Catch:{ SQLException -> 0x0025 }
            if (r7 == 0) goto L_0x0037
            boolean r9 = r13.connectedToSecondaryHost()     // Catch:{ SQLException -> 0x0025 }
            if (r9 == 0) goto L_0x0037
            r13.resetAutoFallBackCounters()     // Catch:{ SQLException -> 0x0025 }
        L_0x0037:
            r6 = 1
            goto L_0x0060
        L_0x0039:
            r4 = r9
            boolean r10 = r13.shouldExceptionTriggerConnectionSwitch(r9)     // Catch:{ all -> 0x006c }
            if (r10 == 0) goto L_0x006b
            if (r5 <= 0) goto L_0x0044
            r10 = r8
            goto L_0x0045
        L_0x0044:
            r10 = r1
        L_0x0045:
            int r10 = r13.nextHost(r2, r10)     // Catch:{ all -> 0x006c }
            if (r10 != r3) goto L_0x005e
            int r11 = r13.nextHost(r2, r8)     // Catch:{ all -> 0x006c }
            r12 = r11
            if (r10 != r11) goto L_0x005d
            int r5 = r5 + 1
            r10 = 250(0xfa, double:1.235E-321)
            java.lang.Thread.sleep(r10)     // Catch:{ InterruptedException -> 0x005a }
            goto L_0x005b
        L_0x005a:
            r10 = move-exception
        L_0x005b:
            r10 = r12
            goto L_0x005e
        L_0x005d:
            r10 = r12
        L_0x005e:
            r2 = r10
        L_0x0060:
            int r9 = r13.retriesAllDown     // Catch:{ all -> 0x006c }
            if (r5 >= r9) goto L_0x0066
            if (r6 == 0) goto L_0x001a
        L_0x0066:
            if (r6 == 0) goto L_0x006a
            monitor-exit(r13)
            return
        L_0x006a:
            throw r4     // Catch:{ all -> 0x006c }
        L_0x006b:
            throw r9     // Catch:{ all -> 0x006c }
        L_0x006c:
            r14 = move-exception
            monitor-exit(r13)
            throw r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.FailoverConnectionProxy.failOver(int):void");
    }

    /* access modifiers changed from: package-private */
    public synchronized void fallBackToPrimaryIfAvailable() {
        MySQLConnection connection = null;
        try {
            connection = createConnectionForHostIndex(this.primaryHostIndex);
            switchCurrentConnectionTo(this.primaryHostIndex, connection);
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e2) {
                }
            }
            resetAutoFallBackCounters();
        }
        return;
    }

    private int nextHost(int currHostIdx, boolean vouchForPrimaryHost) {
        int nextHostIdx = (currHostIdx + 1) % this.hostList.size();
        if (!isPrimaryHostIndex(nextHostIdx) || !isConnected() || vouchForPrimaryHost || !this.enableFallBackToPrimaryHost || readyToFallBackToPrimaryHost()) {
            return nextHostIdx;
        }
        return nextHost(nextHostIdx, vouchForPrimaryHost);
    }

    /* access modifiers changed from: package-private */
    public synchronized void incrementQueriesIssuedSinceFailover() {
        this.queriesIssuedSinceFailover++;
    }

    /* access modifiers changed from: package-private */
    public synchronized boolean readyToFallBackToPrimaryHost() {
        return this.enableFallBackToPrimaryHost && connectedToSecondaryHost() && (secondsBeforeRetryPrimaryHostIsMet() || queriesBeforeRetryPrimaryHostIsMet());
    }

    /* access modifiers changed from: package-private */
    public synchronized boolean isConnected() {
        return this.currentHostIndex != -1;
    }

    /* access modifiers changed from: package-private */
    public synchronized boolean isPrimaryHostIndex(int hostIndex) {
        return hostIndex == this.primaryHostIndex;
    }

    /* access modifiers changed from: package-private */
    public synchronized boolean connectedToPrimaryHost() {
        return isPrimaryHostIndex(this.currentHostIndex);
    }

    /* access modifiers changed from: package-private */
    public synchronized boolean connectedToSecondaryHost() {
        int i;
        i = this.currentHostIndex;
        return i >= 0 && !isPrimaryHostIndex(i);
    }

    private synchronized boolean secondsBeforeRetryPrimaryHostIsMet() {
        boolean z;
        if (this.secondsBeforeRetryPrimaryHost > 0) {
            if (Util.secondsSinceMillis(this.primaryHostFailTimeMillis) >= ((long) this.secondsBeforeRetryPrimaryHost)) {
                z = true;
            }
        }
        z = false;
        return z;
    }

    private synchronized boolean queriesBeforeRetryPrimaryHostIsMet() {
        long j;
        j = this.queriesBeforeRetryPrimaryHost;
        return j > 0 && this.queriesIssuedSinceFailover >= j;
    }

    private synchronized void resetAutoFallBackCounters() {
        this.primaryHostFailTimeMillis = System.currentTimeMillis();
        this.queriesIssuedSinceFailover = 0;
    }

    /* access modifiers changed from: package-private */
    public synchronized void doClose() throws SQLException {
        this.currentConnection.close();
    }

    /* access modifiers changed from: package-private */
    public synchronized void doAbortInternal() throws SQLException {
        this.currentConnection.abortInternal();
    }

    /* access modifiers changed from: package-private */
    public synchronized void doAbort(Executor executor) throws SQLException {
        this.currentConnection.abort(executor);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00ac, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized java.lang.Object invokeMore(java.lang.Object r6, java.lang.reflect.Method r7, java.lang.Object[] r8) throws java.lang.Throwable {
        /*
            r5 = this;
            monitor-enter(r5)
            java.lang.String r0 = r7.getName()     // Catch:{ all -> 0x00ad }
            java.lang.String r1 = "setReadOnly"
            boolean r1 = r1.equals(r0)     // Catch:{ all -> 0x00ad }
            r2 = 0
            r3 = 0
            if (r1 == 0) goto L_0x0022
            r1 = r8[r3]     // Catch:{ all -> 0x00ad }
            java.lang.Boolean r1 = (java.lang.Boolean) r1     // Catch:{ all -> 0x00ad }
            r5.explicitlyReadOnly = r1     // Catch:{ all -> 0x00ad }
            boolean r1 = r5.failoverReadOnly     // Catch:{ all -> 0x00ad }
            if (r1 == 0) goto L_0x0022
            boolean r1 = r5.connectedToSecondaryHost()     // Catch:{ all -> 0x00ad }
            if (r1 == 0) goto L_0x0022
            monitor-exit(r5)
            return r2
        L_0x0022:
            boolean r1 = r5.isClosed     // Catch:{ all -> 0x00ad }
            if (r1 == 0) goto L_0x0066
            boolean r1 = r5.allowedOnClosedConnection(r7)     // Catch:{ all -> 0x00ad }
            if (r1 != 0) goto L_0x0066
            boolean r1 = r5.autoReconnect     // Catch:{ all -> 0x00ad }
            if (r1 == 0) goto L_0x003f
            boolean r1 = r5.closedExplicitly     // Catch:{ all -> 0x00ad }
            if (r1 != 0) goto L_0x003f
            r1 = -1
            r5.currentHostIndex = r1     // Catch:{ all -> 0x00ad }
            r5.pickNewConnection()     // Catch:{ all -> 0x00ad }
            r5.isClosed = r3     // Catch:{ all -> 0x00ad }
            r5.closedReason = r2     // Catch:{ all -> 0x00ad }
            goto L_0x0066
        L_0x003f:
            java.lang.String r1 = "No operations allowed after connection closed."
            java.lang.String r3 = r5.closedReason     // Catch:{ all -> 0x00ad }
            if (r3 == 0) goto L_0x005f
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ad }
            r3.<init>()     // Catch:{ all -> 0x00ad }
            java.lang.StringBuilder r3 = r3.append(r1)     // Catch:{ all -> 0x00ad }
            java.lang.String r4 = "  "
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ all -> 0x00ad }
            java.lang.String r4 = r5.closedReason     // Catch:{ all -> 0x00ad }
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ all -> 0x00ad }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00ad }
            r1 = r3
        L_0x005f:
            java.lang.String r3 = "08003"
            java.sql.SQLException r2 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r3, (com.mysql.jdbc.ExceptionInterceptor) r2)     // Catch:{ all -> 0x00ad }
            throw r2     // Catch:{ all -> 0x00ad }
        L_0x0066:
            r1 = 0
            com.mysql.jdbc.MySQLConnection r2 = r5.thisAsConnection     // Catch:{ InvocationTargetException -> 0x0078 }
            java.lang.Object r2 = r7.invoke(r2, r8)     // Catch:{ InvocationTargetException -> 0x0078 }
            r1 = r2
            java.lang.Class r2 = r7.getReturnType()     // Catch:{ InvocationTargetException -> 0x0078 }
            java.lang.Object r2 = r5.proxyIfReturnTypeIsJdbcInterface(r2, r1)     // Catch:{ InvocationTargetException -> 0x0078 }
            r1 = r2
            goto L_0x007c
        L_0x0078:
            r2 = move-exception
            r5.dealWithInvocationException(r2)     // Catch:{ all -> 0x00ad }
        L_0x007c:
            java.lang.String r2 = "setAutoCommit"
            boolean r2 = r2.equals(r0)     // Catch:{ all -> 0x00ad }
            if (r2 == 0) goto L_0x008e
            r2 = r8[r3]     // Catch:{ all -> 0x00ad }
            java.lang.Boolean r2 = (java.lang.Boolean) r2     // Catch:{ all -> 0x00ad }
            boolean r2 = r2.booleanValue()     // Catch:{ all -> 0x00ad }
            r5.explicitlyAutoCommit = r2     // Catch:{ all -> 0x00ad }
        L_0x008e:
            boolean r2 = r5.explicitlyAutoCommit     // Catch:{ all -> 0x00ad }
            if (r2 != 0) goto L_0x00a2
            java.lang.String r2 = "commit"
            boolean r2 = r2.equals(r0)     // Catch:{ all -> 0x00ad }
            if (r2 != 0) goto L_0x00a2
            java.lang.String r2 = "rollback"
            boolean r2 = r2.equals(r0)     // Catch:{ all -> 0x00ad }
            if (r2 == 0) goto L_0x00ab
        L_0x00a2:
            boolean r2 = r5.readyToFallBackToPrimaryHost()     // Catch:{ all -> 0x00ad }
            if (r2 == 0) goto L_0x00ab
            r5.fallBackToPrimaryIfAvailable()     // Catch:{ all -> 0x00ad }
        L_0x00ab:
            monitor-exit(r5)
            return r1
        L_0x00ad:
            r6 = move-exception
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.FailoverConnectionProxy.invokeMore(java.lang.Object, java.lang.reflect.Method, java.lang.Object[]):java.lang.Object");
    }
}
