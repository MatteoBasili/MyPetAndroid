package com.mysql.jdbc;

import com.application.mypet.BuildConfig;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

public abstract class MultiHostConnectionProxy implements InvocationHandler {
    private static Constructor<?> JDBC_4_MS_CONNECTION_CTOR = null;
    private static final String METHOD_ABORT = "abort";
    private static final String METHOD_ABORT_INTERNAL = "abortInternal";
    private static final String METHOD_CLOSE = "close";
    private static final String METHOD_EQUALS = "equals";
    private static final String METHOD_GET_AUTO_COMMIT = "getAutoCommit";
    private static final String METHOD_GET_CATALOG = "getCatalog";
    private static final String METHOD_GET_MULTI_HOST_SAFE_PROXY = "getMultiHostSafeProxy";
    private static final String METHOD_GET_SESSION_MAX_ROWS = "getSessionMaxRows";
    private static final String METHOD_GET_TRANSACTION_ISOLATION = "getTransactionIsolation";
    private static final String METHOD_HASH_CODE = "hashCode";
    private static final String METHOD_IS_CLOSED = "isClosed";
    boolean autoReconnect;
    boolean closedExplicitly;
    String closedReason;
    MySQLConnection currentConnection;
    List<String> hostList;
    boolean isClosed;
    protected Throwable lastExceptionDealtWith;
    Properties localProps;
    MySQLConnection proxyConnection;
    MySQLConnection thisAsConnection;

    /* access modifiers changed from: package-private */
    public abstract void doAbort(Executor executor) throws SQLException;

    /* access modifiers changed from: package-private */
    public abstract void doAbortInternal() throws SQLException;

    /* access modifiers changed from: package-private */
    public abstract void doClose() throws SQLException;

    /* access modifiers changed from: package-private */
    public abstract Object invokeMore(Object obj, Method method, Object[] objArr) throws Throwable;

    /* access modifiers changed from: package-private */
    public abstract boolean isMasterConnection();

    /* access modifiers changed from: package-private */
    public abstract void pickNewConnection() throws SQLException;

    /* access modifiers changed from: package-private */
    public abstract boolean shouldExceptionTriggerConnectionSwitch(Throwable th);

    static {
        if (Util.isJdbc4()) {
            try {
                JDBC_4_MS_CONNECTION_CTOR = Class.forName("com.mysql.jdbc.JDBC4MultiHostMySQLConnection").getConstructor(new Class[]{MultiHostConnectionProxy.class});
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e2) {
                throw new RuntimeException(e2);
            } catch (ClassNotFoundException e3) {
                throw new RuntimeException(e3);
            }
        }
    }

    class JdbcInterfaceProxy implements InvocationHandler {
        Object invokeOn = null;

        JdbcInterfaceProxy(Object toInvokeOn) {
            this.invokeOn = toInvokeOn;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result;
            if (MultiHostConnectionProxy.METHOD_EQUALS.equals(method.getName())) {
                return Boolean.valueOf(args[0].equals(this));
            }
            synchronized (MultiHostConnectionProxy.this) {
                result = null;
                try {
                    result = MultiHostConnectionProxy.this.proxyIfReturnTypeIsJdbcInterface(method.getReturnType(), method.invoke(this.invokeOn, args));
                } catch (InvocationTargetException e) {
                    MultiHostConnectionProxy.this.dealWithInvocationException(e);
                }
            }
            return result;
        }
    }

    MultiHostConnectionProxy() throws SQLException {
        this.autoReconnect = false;
        this.thisAsConnection = null;
        this.proxyConnection = null;
        this.currentConnection = null;
        this.isClosed = false;
        this.closedExplicitly = false;
        this.closedReason = null;
        this.lastExceptionDealtWith = null;
        this.thisAsConnection = getNewWrapperForThisAsConnection();
    }

    MultiHostConnectionProxy(List<String> hosts, Properties props) throws SQLException {
        this();
        initializeHostsSpecs(hosts, props);
    }

    /* access modifiers changed from: package-private */
    public int initializeHostsSpecs(List<String> hosts, Properties props) {
        this.autoReconnect = "true".equalsIgnoreCase(props.getProperty("autoReconnect")) || "true".equalsIgnoreCase(props.getProperty("autoReconnectForPools"));
        this.hostList = hosts;
        int numHosts = hosts.size();
        Properties properties = (Properties) props.clone();
        this.localProps = properties;
        properties.remove(NonRegisteringDriver.HOST_PROPERTY_KEY);
        this.localProps.remove(NonRegisteringDriver.PORT_PROPERTY_KEY);
        for (int i = 0; i < numHosts; i++) {
            this.localProps.remove("HOST." + (i + 1));
            this.localProps.remove("PORT." + (i + 1));
        }
        this.localProps.remove(NonRegisteringDriver.NUM_HOSTS_PROPERTY_KEY);
        return numHosts;
    }

    /* access modifiers changed from: package-private */
    public MySQLConnection getNewWrapperForThisAsConnection() throws SQLException {
        if (!Util.isJdbc4() && JDBC_4_MS_CONNECTION_CTOR == null) {
            return new MultiHostMySQLConnection(this);
        }
        return (MySQLConnection) Util.handleNewInstance(JDBC_4_MS_CONNECTION_CTOR, new Object[]{this}, (ExceptionInterceptor) null);
    }

    /* access modifiers changed from: protected */
    public MySQLConnection getProxy() {
        MySQLConnection mySQLConnection = this.proxyConnection;
        return mySQLConnection != null ? mySQLConnection : this.thisAsConnection;
    }

    /* access modifiers changed from: protected */
    public final void setProxy(MySQLConnection proxyConn) {
        this.proxyConnection = proxyConn;
        propagateProxyDown(proxyConn);
    }

    /* access modifiers changed from: protected */
    public void propagateProxyDown(MySQLConnection proxyConn) {
        this.currentConnection.setProxy(proxyConn);
    }

    /* access modifiers changed from: package-private */
    public Object proxyIfReturnTypeIsJdbcInterface(Class<?> returnType, Object toProxy) {
        if (toProxy == null || !Util.isJdbcInterface(returnType)) {
            return toProxy;
        }
        Class<?> toProxyClass = toProxy.getClass();
        return Proxy.newProxyInstance(toProxyClass.getClassLoader(), Util.getImplementedInterfaces(toProxyClass), getNewJdbcInterfaceProxy(toProxy));
    }

    /* access modifiers changed from: package-private */
    public InvocationHandler getNewJdbcInterfaceProxy(Object toProxy) {
        return new JdbcInterfaceProxy(toProxy);
    }

    /* access modifiers changed from: package-private */
    public void dealWithInvocationException(InvocationTargetException e) throws SQLException, Throwable, InvocationTargetException {
        Throwable t = e.getTargetException();
        if (t != null) {
            if (this.lastExceptionDealtWith != t && shouldExceptionTriggerConnectionSwitch(t)) {
                invalidateCurrentConnection();
                pickNewConnection();
                this.lastExceptionDealtWith = t;
            }
            throw t;
        }
        throw e;
    }

    /* access modifiers changed from: package-private */
    public synchronized void invalidateCurrentConnection() throws SQLException {
        invalidateConnection(this.currentConnection);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void invalidateConnection(com.mysql.jdbc.MySQLConnection r4) throws java.sql.SQLException {
        /*
            r3 = this;
            monitor-enter(r3)
            if (r4 == 0) goto L_0x001d
            boolean r0 = r4.isClosed()     // Catch:{ SQLException -> 0x001b, all -> 0x0018 }
            if (r0 != 0) goto L_0x001d
            boolean r0 = r4.getAutoCommit()     // Catch:{ SQLException -> 0x001b, all -> 0x0018 }
            r1 = 1
            if (r0 != 0) goto L_0x0012
            r0 = r1
            goto L_0x0013
        L_0x0012:
            r0 = 0
        L_0x0013:
            r2 = 0
            r4.realClose(r1, r0, r1, r2)     // Catch:{ SQLException -> 0x001b, all -> 0x0018 }
            goto L_0x001d
        L_0x0018:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        L_0x001b:
            r0 = move-exception
            goto L_0x001e
        L_0x001d:
        L_0x001e:
            monitor-exit(r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.MultiHostConnectionProxy.invalidateConnection(com.mysql.jdbc.MySQLConnection):void");
    }

    /* access modifiers changed from: package-private */
    public synchronized ConnectionImpl createConnectionForHost(String hostPortSpec) throws SQLException {
        ConnectionImpl conn;
        Properties connProps = (Properties) this.localProps.clone();
        String[] hostPortPair = NonRegisteringDriver.parseHostPortPair(hostPortSpec);
        String hostName = hostPortPair[0];
        String portNumber = hostPortPair[1];
        String dbName = connProps.getProperty(NonRegisteringDriver.DBNAME_PROPERTY_KEY);
        if (hostName != null) {
            if (portNumber == null) {
                portNumber = BuildConfig.DB_PORT;
            }
            connProps.setProperty(NonRegisteringDriver.HOST_PROPERTY_KEY, hostName);
            connProps.setProperty(NonRegisteringDriver.PORT_PROPERTY_KEY, portNumber);
            connProps.setProperty("HOST.1", hostName);
            connProps.setProperty("PORT.1", portNumber);
            connProps.setProperty(NonRegisteringDriver.NUM_HOSTS_PROPERTY_KEY, "1");
            connProps.setProperty("roundRobinLoadBalance", "false");
            conn = (ConnectionImpl) ConnectionImpl.getInstance(hostName, Integer.parseInt(portNumber), connProps, dbName, "jdbc:mysql://" + hostName + ":" + portNumber + "/");
            conn.setProxy(getProxy());
        } else {
            throw new SQLException("Could not find a hostname to start a connection to");
        }
        return conn;
    }

    /* access modifiers changed from: package-private */
    public void syncSessionState(Connection source, Connection target) throws SQLException {
        if (source != null && target != null) {
            boolean prevUseLocalSessionState = source.getUseLocalSessionState();
            source.setUseLocalSessionState(true);
            boolean readOnly = source.isReadOnly();
            source.setUseLocalSessionState(prevUseLocalSessionState);
            syncSessionState(source, target, readOnly);
        }
    }

    /* access modifiers changed from: package-private */
    public void syncSessionState(Connection source, Connection target, boolean readOnly) throws SQLException {
        if (target != null) {
            target.setReadOnly(readOnly);
        }
        if (source != null && target != null) {
            boolean prevUseLocalSessionState = source.getUseLocalSessionState();
            source.setUseLocalSessionState(true);
            target.setAutoCommit(source.getAutoCommit());
            target.setCatalog(source.getCatalog());
            target.setTransactionIsolation(source.getTransactionIsolation());
            target.setSessionMaxRows(source.getSessionMaxRows());
            source.setUseLocalSessionState(prevUseLocalSessionState);
        }
    }

    public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (METHOD_GET_MULTI_HOST_SAFE_PROXY.equals(methodName)) {
            return this.thisAsConnection;
        } else if (METHOD_EQUALS.equals(methodName)) {
            return Boolean.valueOf(args[0].equals(this));
        } else if (METHOD_HASH_CODE.equals(methodName)) {
            return Integer.valueOf(hashCode());
        } else if (METHOD_CLOSE.equals(methodName)) {
            doClose();
            this.isClosed = true;
            this.closedReason = "Connection explicitly closed.";
            this.closedExplicitly = true;
            return null;
        } else if (METHOD_ABORT_INTERNAL.equals(methodName)) {
            doAbortInternal();
            this.currentConnection.abortInternal();
            this.isClosed = true;
            this.closedReason = "Connection explicitly closed.";
            return null;
        } else if (METHOD_ABORT.equals(methodName) && args.length == 1) {
            doAbort(args[0]);
            this.isClosed = true;
            this.closedReason = "Connection explicitly closed.";
            return null;
        } else if (METHOD_IS_CLOSED.equals(methodName)) {
            return Boolean.valueOf(this.isClosed);
        } else {
            try {
                return invokeMore(proxy, method, args);
            } catch (InvocationTargetException e) {
                throw (e.getCause() != null ? e.getCause() : e);
            } catch (Exception e2) {
                Class<?>[] arr$ = method.getExceptionTypes();
                int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    if (!arr$[i$].isAssignableFrom(e2.getClass())) {
                        i$++;
                    } else {
                        throw e2;
                    }
                }
                throw new IllegalStateException(e2.getMessage(), e2);
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean allowedOnClosedConnection(Method method) {
        String methodName = method.getName();
        return methodName.equals(METHOD_GET_AUTO_COMMIT) || methodName.equals(METHOD_GET_CATALOG) || methodName.equals(METHOD_GET_TRANSACTION_ISOLATION) || methodName.equals(METHOD_GET_SESSION_MAX_ROWS);
    }
}
