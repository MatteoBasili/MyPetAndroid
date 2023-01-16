package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.Util;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;

public class MysqlPooledConnection implements PooledConnection {
    public static final int CONNECTION_CLOSED_EVENT = 2;
    public static final int CONNECTION_ERROR_EVENT = 1;
    private static final Constructor<?> JDBC_4_POOLED_CONNECTION_WRAPPER_CTOR;
    private Map<ConnectionEventListener, ConnectionEventListener> connectionEventListeners;
    private ExceptionInterceptor exceptionInterceptor;
    private Connection logicalHandle = null;
    private com.mysql.jdbc.Connection physicalConn;

    static {
        if (Util.isJdbc4()) {
            try {
                JDBC_4_POOLED_CONNECTION_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4MysqlPooledConnection").getConstructor(new Class[]{com.mysql.jdbc.Connection.class});
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e2) {
                throw new RuntimeException(e2);
            } catch (ClassNotFoundException e3) {
                throw new RuntimeException(e3);
            }
        } else {
            JDBC_4_POOLED_CONNECTION_WRAPPER_CTOR = null;
        }
    }

    protected static MysqlPooledConnection getInstance(com.mysql.jdbc.Connection connection) throws SQLException {
        if (!Util.isJdbc4()) {
            return new MysqlPooledConnection(connection);
        }
        return (MysqlPooledConnection) Util.handleNewInstance(JDBC_4_POOLED_CONNECTION_WRAPPER_CTOR, new Object[]{connection}, connection.getExceptionInterceptor());
    }

    public MysqlPooledConnection(com.mysql.jdbc.Connection connection) {
        this.physicalConn = connection;
        this.connectionEventListeners = new HashMap();
        this.exceptionInterceptor = this.physicalConn.getExceptionInterceptor();
    }

    public synchronized void addConnectionEventListener(ConnectionEventListener connectioneventlistener) {
        Map<ConnectionEventListener, ConnectionEventListener> map = this.connectionEventListeners;
        if (map != null) {
            map.put(connectioneventlistener, connectioneventlistener);
        }
    }

    public synchronized void removeConnectionEventListener(ConnectionEventListener connectioneventlistener) {
        Map<ConnectionEventListener, ConnectionEventListener> map = this.connectionEventListeners;
        if (map != null) {
            map.remove(connectioneventlistener);
        }
    }

    public synchronized Connection getConnection() throws SQLException {
        return getConnection(true, false);
    }

    /* access modifiers changed from: protected */
    public synchronized Connection getConnection(boolean resetServerState, boolean forXa) throws SQLException {
        ConnectionWrapper instance;
        if (this.physicalConn != null) {
            try {
                Connection connection = this.logicalHandle;
                if (connection != null) {
                    ((ConnectionWrapper) connection).close(false);
                }
                if (resetServerState) {
                    this.physicalConn.resetServerState();
                }
                instance = ConnectionWrapper.getInstance(this, this.physicalConn, forXa);
                this.logicalHandle = instance;
            } catch (SQLException sqlException) {
                callConnectionEventListeners(1, sqlException);
                throw sqlException;
            }
        } else {
            SQLException sqlException2 = SQLError.createSQLException("Physical Connection doesn't exist", this.exceptionInterceptor);
            callConnectionEventListeners(1, sqlException2);
            throw sqlException2;
        }
        return instance;
    }

    public synchronized void close() throws SQLException {
        com.mysql.jdbc.Connection connection = this.physicalConn;
        if (connection != null) {
            connection.close();
            this.physicalConn = null;
        }
        Map<ConnectionEventListener, ConnectionEventListener> map = this.connectionEventListeners;
        if (map != null) {
            map.clear();
            this.connectionEventListeners = null;
        }
    }

    /* access modifiers changed from: protected */
    public synchronized void callConnectionEventListeners(int eventType, SQLException sqlException) {
        Map<ConnectionEventListener, ConnectionEventListener> map = this.connectionEventListeners;
        if (map != null) {
            ConnectionEvent connectionevent = new ConnectionEvent(this, sqlException);
            for (Map.Entry<ConnectionEventListener, ConnectionEventListener> value : map.entrySet()) {
                ConnectionEventListener connectioneventlistener = (ConnectionEventListener) value.getValue();
                if (eventType == 2) {
                    connectioneventlistener.connectionClosed(connectionevent);
                } else if (eventType == 1) {
                    connectioneventlistener.connectionErrorOccurred(connectionevent);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public ExceptionInterceptor getExceptionInterceptor() {
        return this.exceptionInterceptor;
    }
}
