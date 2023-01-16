package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.jdbc2.optional.WrapperBase;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Proxy;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.HashMap;

public class JDBC4PreparedStatementWrapper extends PreparedStatementWrapper {
    public JDBC4PreparedStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, PreparedStatement toWrap) {
        super(c, conn, toWrap);
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:30:0x0034=Splitter:B:30:0x0034, B:49:0x0064=Splitter:B:49:0x0064, B:35:0x003b=Splitter:B:35:0x003b} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void close() throws java.sql.SQLException {
        /*
            r5 = this;
            monitor-enter(r5)
            com.mysql.jdbc.jdbc2.optional.MysqlPooledConnection r0 = r5.pooledConnection     // Catch:{ all -> 0x006c }
            if (r0 != 0) goto L_0x0007
            monitor-exit(r5)
            return
        L_0x0007:
            com.mysql.jdbc.jdbc2.optional.MysqlPooledConnection r0 = r5.pooledConnection     // Catch:{ all -> 0x006c }
            r1 = 0
            super.close()     // Catch:{ all -> 0x003e }
            javax.sql.StatementEvent r2 = new javax.sql.StatementEvent     // Catch:{ all -> 0x003a }
            r2.<init>(r0, r5)     // Catch:{ all -> 0x003a }
            boolean r3 = r0 instanceof com.mysql.jdbc.jdbc2.optional.JDBC4MysqlPooledConnection     // Catch:{ all -> 0x003a }
            if (r3 == 0) goto L_0x001f
            r3 = r0
            com.mysql.jdbc.jdbc2.optional.JDBC4MysqlPooledConnection r3 = (com.mysql.jdbc.jdbc2.optional.JDBC4MysqlPooledConnection) r3     // Catch:{ all -> 0x001d }
            r3.fireStatementEvent(r2)     // Catch:{ all -> 0x001d }
            goto L_0x0034
        L_0x001d:
            r2 = move-exception
            goto L_0x003b
        L_0x001f:
            boolean r3 = r0 instanceof com.mysql.jdbc.jdbc2.optional.JDBC4MysqlXAConnection     // Catch:{ all -> 0x003a }
            if (r3 == 0) goto L_0x002a
            r3 = r0
            com.mysql.jdbc.jdbc2.optional.JDBC4MysqlXAConnection r3 = (com.mysql.jdbc.jdbc2.optional.JDBC4MysqlXAConnection) r3     // Catch:{ all -> 0x001d }
            r3.fireStatementEvent(r2)     // Catch:{ all -> 0x001d }
            goto L_0x0034
        L_0x002a:
            boolean r3 = r0 instanceof com.mysql.jdbc.jdbc2.optional.JDBC4SuspendableXAConnection     // Catch:{ all -> 0x003a }
            if (r3 == 0) goto L_0x0034
            r3 = r0
            com.mysql.jdbc.jdbc2.optional.JDBC4SuspendableXAConnection r3 = (com.mysql.jdbc.jdbc2.optional.JDBC4SuspendableXAConnection) r3     // Catch:{ all -> 0x001d }
            r3.fireStatementEvent(r2)     // Catch:{ all -> 0x001d }
        L_0x0034:
            r5.unwrappedInterfaces = r1     // Catch:{ all -> 0x006c }
            monitor-exit(r5)
            return
        L_0x003a:
            r2 = move-exception
        L_0x003b:
            r5.unwrappedInterfaces = r1     // Catch:{ all -> 0x006c }
        L_0x003d:
            throw r2     // Catch:{ all -> 0x006c }
        L_0x003e:
            r2 = move-exception
            javax.sql.StatementEvent r3 = new javax.sql.StatementEvent     // Catch:{ all -> 0x0068 }
            r3.<init>(r0, r5)     // Catch:{ all -> 0x0068 }
            boolean r4 = r0 instanceof com.mysql.jdbc.jdbc2.optional.JDBC4MysqlPooledConnection     // Catch:{ all -> 0x0068 }
            if (r4 != 0) goto L_0x005e
            boolean r4 = r0 instanceof com.mysql.jdbc.jdbc2.optional.JDBC4MysqlXAConnection     // Catch:{ all -> 0x0068 }
            if (r4 != 0) goto L_0x0057
            boolean r4 = r0 instanceof com.mysql.jdbc.jdbc2.optional.JDBC4SuspendableXAConnection     // Catch:{ all -> 0x0068 }
            if (r4 == 0) goto L_0x0064
            r4 = r0
            com.mysql.jdbc.jdbc2.optional.JDBC4SuspendableXAConnection r4 = (com.mysql.jdbc.jdbc2.optional.JDBC4SuspendableXAConnection) r4     // Catch:{ all -> 0x0068 }
            r4.fireStatementEvent(r3)     // Catch:{ all -> 0x0068 }
            goto L_0x0064
        L_0x0057:
            r4 = r0
            com.mysql.jdbc.jdbc2.optional.JDBC4MysqlXAConnection r4 = (com.mysql.jdbc.jdbc2.optional.JDBC4MysqlXAConnection) r4     // Catch:{ all -> 0x0068 }
            r4.fireStatementEvent(r3)     // Catch:{ all -> 0x0068 }
            goto L_0x0064
        L_0x005e:
            r4 = r0
            com.mysql.jdbc.jdbc2.optional.JDBC4MysqlPooledConnection r4 = (com.mysql.jdbc.jdbc2.optional.JDBC4MysqlPooledConnection) r4     // Catch:{ all -> 0x0068 }
            r4.fireStatementEvent(r3)     // Catch:{ all -> 0x0068 }
        L_0x0064:
            r5.unwrappedInterfaces = r1     // Catch:{ all -> 0x006c }
            throw r2     // Catch:{ all -> 0x006c }
        L_0x0068:
            r2 = move-exception
            r5.unwrappedInterfaces = r1     // Catch:{ all -> 0x006c }
            goto L_0x003d
        L_0x006c:
            r0 = move-exception
            monitor-exit(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.jdbc2.optional.JDBC4PreparedStatementWrapper.close():void");
    }

    public boolean isClosed() throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                return this.wrappedStmt.isClosed();
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return false;
        }
    }

    public void setPoolable(boolean poolable) throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                this.wrappedStmt.setPoolable(poolable);
                return;
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public boolean isPoolable() throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                return this.wrappedStmt.isPoolable();
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return false;
        }
    }

    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                ((PreparedStatement) this.wrappedStmt).setRowId(parameterIndex, x);
                return;
            }
            throw SQLError.createSQLException("No operations allowed after statement closed", SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                ((PreparedStatement) this.wrappedStmt).setNClob(parameterIndex, value);
                return;
            }
            throw SQLError.createSQLException("No operations allowed after statement closed", SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                ((PreparedStatement) this.wrappedStmt).setSQLXML(parameterIndex, xmlObject);
                return;
            }
            throw SQLError.createSQLException("No operations allowed after statement closed", SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void setNString(int parameterIndex, String value) throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                ((PreparedStatement) this.wrappedStmt).setNString(parameterIndex, value);
                return;
            }
            throw SQLError.createSQLException("No operations allowed after statement closed", SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                ((PreparedStatement) this.wrappedStmt).setNCharacterStream(parameterIndex, value, length);
                return;
            }
            throw SQLError.createSQLException("No operations allowed after statement closed", SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                ((PreparedStatement) this.wrappedStmt).setClob(parameterIndex, reader, length);
                return;
            }
            throw SQLError.createSQLException("No operations allowed after statement closed", SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                ((PreparedStatement) this.wrappedStmt).setBlob(parameterIndex, inputStream, length);
                return;
            }
            throw SQLError.createSQLException("No operations allowed after statement closed", SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                ((PreparedStatement) this.wrappedStmt).setNClob(parameterIndex, reader, length);
                return;
            }
            throw SQLError.createSQLException("No operations allowed after statement closed", SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                ((PreparedStatement) this.wrappedStmt).setAsciiStream(parameterIndex, x, length);
                return;
            }
            throw SQLError.createSQLException("No operations allowed after statement closed", SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                ((PreparedStatement) this.wrappedStmt).setBinaryStream(parameterIndex, x, length);
                return;
            }
            throw SQLError.createSQLException("No operations allowed after statement closed", SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                ((PreparedStatement) this.wrappedStmt).setCharacterStream(parameterIndex, reader, length);
                return;
            }
            throw SQLError.createSQLException("No operations allowed after statement closed", SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                ((PreparedStatement) this.wrappedStmt).setAsciiStream(parameterIndex, x);
                return;
            }
            throw SQLError.createSQLException("No operations allowed after statement closed", SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                ((PreparedStatement) this.wrappedStmt).setBinaryStream(parameterIndex, x);
                return;
            }
            throw SQLError.createSQLException("No operations allowed after statement closed", SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                ((PreparedStatement) this.wrappedStmt).setCharacterStream(parameterIndex, reader);
                return;
            }
            throw SQLError.createSQLException("No operations allowed after statement closed", SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                ((PreparedStatement) this.wrappedStmt).setNCharacterStream(parameterIndex, value);
                return;
            }
            throw SQLError.createSQLException("No operations allowed after statement closed", SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                ((PreparedStatement) this.wrappedStmt).setClob(parameterIndex, reader);
                return;
            }
            throw SQLError.createSQLException("No operations allowed after statement closed", SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                ((PreparedStatement) this.wrappedStmt).setBlob(parameterIndex, inputStream);
                return;
            }
            throw SQLError.createSQLException("No operations allowed after statement closed", SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                ((PreparedStatement) this.wrappedStmt).setNClob(parameterIndex, reader);
                return;
            }
            throw SQLError.createSQLException("No operations allowed after statement closed", SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return true;
        }
        String interfaceClassName = iface.getName();
        if (interfaceClassName.equals("com.mysql.jdbc.Statement") || interfaceClassName.equals("java.sql.Statement") || interfaceClassName.equals("java.sql.PreparedStatement") || interfaceClassName.equals("java.sql.Wrapper")) {
            return true;
        }
        return false;
    }

    public synchronized <T> T unwrap(Class<T> iface) throws SQLException {
        try {
            if (!"java.sql.Statement".equals(iface.getName()) && !"java.sql.PreparedStatement".equals(iface.getName())) {
                if (!"java.sql.Wrapper.class".equals(iface.getName())) {
                    if (this.unwrappedInterfaces == null) {
                        this.unwrappedInterfaces = new HashMap();
                    }
                    Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
                    if (cachedUnwrapped == null) {
                        if (cachedUnwrapped == null) {
                            cachedUnwrapped = Proxy.newProxyInstance(this.wrappedStmt.getClass().getClassLoader(), new Class[]{iface}, new WrapperBase.ConnectionErrorFiringInvocationHandler(this.wrappedStmt));
                            this.unwrappedInterfaces.put(iface, cachedUnwrapped);
                        }
                        this.unwrappedInterfaces.put(iface, cachedUnwrapped);
                    }
                    return iface.cast(cachedUnwrapped);
                }
            }
            return iface.cast(this);
        } catch (ClassCastException e) {
            throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        }
    }
}
