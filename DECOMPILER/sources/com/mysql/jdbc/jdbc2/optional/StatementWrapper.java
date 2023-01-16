package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.StatementImpl;
import com.mysql.jdbc.Util;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

public class StatementWrapper extends WrapperBase implements Statement {
    private static final Constructor<?> JDBC_4_STATEMENT_WRAPPER_CTOR;
    protected ConnectionWrapper wrappedConn;
    protected Statement wrappedStmt;

    static {
        if (Util.isJdbc4()) {
            try {
                JDBC_4_STATEMENT_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4StatementWrapper").getConstructor(new Class[]{ConnectionWrapper.class, MysqlPooledConnection.class, Statement.class});
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e2) {
                throw new RuntimeException(e2);
            } catch (ClassNotFoundException e3) {
                throw new RuntimeException(e3);
            }
        } else {
            JDBC_4_STATEMENT_WRAPPER_CTOR = null;
        }
    }

    protected static StatementWrapper getInstance(ConnectionWrapper c, MysqlPooledConnection conn, Statement toWrap) throws SQLException {
        if (!Util.isJdbc4()) {
            return new StatementWrapper(c, conn, toWrap);
        }
        return (StatementWrapper) Util.handleNewInstance(JDBC_4_STATEMENT_WRAPPER_CTOR, new Object[]{c, conn, toWrap}, conn.getExceptionInterceptor());
    }

    public StatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, Statement toWrap) {
        super(conn);
        this.wrappedStmt = toWrap;
        this.wrappedConn = c;
    }

    public Connection getConnection() throws SQLException {
        try {
            if (this.wrappedStmt != null) {
                return this.wrappedConn;
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return null;
        }
    }

    public void setCursorName(String name) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                statement.setCursorName(name);
                return;
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void setEscapeProcessing(boolean enable) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                statement.setEscapeProcessing(enable);
                return;
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void setFetchDirection(int direction) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                statement.setFetchDirection(direction);
                return;
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public int getFetchDirection() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.getFetchDirection();
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return 1000;
        }
    }

    public void setFetchSize(int rows) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                statement.setFetchSize(rows);
                return;
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public int getFetchSize() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.getFetchSize();
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return 0;
        }
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.getGeneratedKeys();
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return null;
        }
    }

    public void setMaxFieldSize(int max) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                statement.setMaxFieldSize(max);
                return;
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public int getMaxFieldSize() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.getMaxFieldSize();
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return 0;
        }
    }

    public void setMaxRows(int max) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                statement.setMaxRows(max);
                return;
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public int getMaxRows() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.getMaxRows();
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return 0;
        }
    }

    public boolean getMoreResults() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.getMoreResults();
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return false;
        }
    }

    public boolean getMoreResults(int current) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.getMoreResults(current);
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return false;
        }
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                statement.setQueryTimeout(seconds);
                return;
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public int getQueryTimeout() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.getQueryTimeout();
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return 0;
        }
    }

    public ResultSet getResultSet() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                ResultSet rs = statement.getResultSet();
                if (rs != null) {
                    ((ResultSetInternalMethods) rs).setWrapperStatement(this);
                }
                return rs;
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return null;
        }
    }

    public int getResultSetConcurrency() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.getResultSetConcurrency();
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return 0;
        }
    }

    public int getResultSetHoldability() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.getResultSetHoldability();
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return 1;
        }
    }

    public int getResultSetType() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.getResultSetType();
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return 1003;
        }
    }

    public int getUpdateCount() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.getUpdateCount();
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return -1;
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.getWarnings();
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return null;
        }
    }

    public void addBatch(String sql) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                statement.addBatch(sql);
            }
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void cancel() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                statement.cancel();
            }
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void clearBatch() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                statement.clearBatch();
            }
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void clearWarnings() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                statement.clearWarnings();
            }
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public void close() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                statement.close();
            }
            this.wrappedStmt = null;
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            this.wrappedStmt = null;
        } catch (Throwable th) {
            this.wrappedStmt = null;
            this.pooledConnection = null;
            throw th;
        }
        this.pooledConnection = null;
    }

    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.execute(sql, autoGeneratedKeys);
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return false;
        }
    }

    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.execute(sql, columnIndexes);
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return false;
        }
    }

    public boolean execute(String sql, String[] columnNames) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.execute(sql, columnNames);
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return false;
        }
    }

    public boolean execute(String sql) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.execute(sql);
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return false;
        }
    }

    public int[] executeBatch() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.executeBatch();
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return null;
        }
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                ResultSet rs = statement.executeQuery(sql);
                ((ResultSetInternalMethods) rs).setWrapperStatement(this);
                return rs;
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return null;
        }
    }

    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.executeUpdate(sql, autoGeneratedKeys);
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return -1;
        }
    }

    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.executeUpdate(sql, columnIndexes);
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return -1;
        }
    }

    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.executeUpdate(sql, columnNames);
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return -1;
        }
    }

    public int executeUpdate(String sql) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return statement.executeUpdate(sql);
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return -1;
        }
    }

    public void enableStreamingResults() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                ((com.mysql.jdbc.Statement) statement).enableStreamingResults();
                return;
            }
            throw SQLError.createSQLException("No operations allowed after statement closed", SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }

    public long[] executeLargeBatch() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return ((StatementImpl) statement).executeLargeBatch();
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return null;
        }
    }

    public long executeLargeUpdate(String sql) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return ((StatementImpl) statement).executeLargeUpdate(sql);
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return -1;
        }
    }

    public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return ((StatementImpl) statement).executeLargeUpdate(sql, autoGeneratedKeys);
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return -1;
        }
    }

    public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return ((StatementImpl) statement).executeLargeUpdate(sql, columnIndexes);
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return -1;
        }
    }

    public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return ((StatementImpl) statement).executeLargeUpdate(sql, columnNames);
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return -1;
        }
    }

    public long getLargeMaxRows() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return ((StatementImpl) statement).getLargeMaxRows();
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return 0;
        }
    }

    public long getLargeUpdateCount() throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                return ((StatementImpl) statement).getLargeUpdateCount();
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
            return -1;
        }
    }

    public void setLargeMaxRows(long max) throws SQLException {
        try {
            Statement statement = this.wrappedStmt;
            if (statement != null) {
                ((StatementImpl) statement).setLargeMaxRows(max);
                return;
            }
            throw SQLError.createSQLException("Statement already closed", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } catch (SQLException sqlEx) {
            checkAndFireConnectionError(sqlEx);
        }
    }
}
