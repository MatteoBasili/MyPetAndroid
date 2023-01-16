package com.mysql.jdbc;

import java.sql.ParameterMetaData;
import java.sql.SQLException;

public class MysqlParameterMetadata implements ParameterMetaData {
    private ExceptionInterceptor exceptionInterceptor;
    ResultSetMetaData metadata = null;
    int parameterCount = 0;
    boolean returnSimpleMetadata = false;

    MysqlParameterMetadata(Field[] fieldInfo, int parameterCount2, ExceptionInterceptor exceptionInterceptor2) {
        this.metadata = new ResultSetMetaData(fieldInfo, false, true, exceptionInterceptor2);
        this.parameterCount = parameterCount2;
        this.exceptionInterceptor = exceptionInterceptor2;
    }

    MysqlParameterMetadata(int count) {
        this.parameterCount = count;
        this.returnSimpleMetadata = true;
    }

    public int getParameterCount() throws SQLException {
        return this.parameterCount;
    }

    public int isNullable(int arg0) throws SQLException {
        checkAvailable();
        return this.metadata.isNullable(arg0);
    }

    private void checkAvailable() throws SQLException {
        ResultSetMetaData resultSetMetaData = this.metadata;
        if (resultSetMetaData == null || resultSetMetaData.fields == null) {
            throw SQLError.createSQLException("Parameter metadata not available for the given statement", SQLError.SQL_STATE_DRIVER_NOT_CAPABLE, this.exceptionInterceptor);
        }
    }

    public boolean isSigned(int arg0) throws SQLException {
        if (this.returnSimpleMetadata) {
            checkBounds(arg0);
            return false;
        }
        checkAvailable();
        return this.metadata.isSigned(arg0);
    }

    public int getPrecision(int arg0) throws SQLException {
        if (this.returnSimpleMetadata) {
            checkBounds(arg0);
            return 0;
        }
        checkAvailable();
        return this.metadata.getPrecision(arg0);
    }

    public int getScale(int arg0) throws SQLException {
        if (this.returnSimpleMetadata) {
            checkBounds(arg0);
            return 0;
        }
        checkAvailable();
        return this.metadata.getScale(arg0);
    }

    public int getParameterType(int arg0) throws SQLException {
        if (this.returnSimpleMetadata) {
            checkBounds(arg0);
            return 12;
        }
        checkAvailable();
        return this.metadata.getColumnType(arg0);
    }

    public String getParameterTypeName(int arg0) throws SQLException {
        if (this.returnSimpleMetadata) {
            checkBounds(arg0);
            return "VARCHAR";
        }
        checkAvailable();
        return this.metadata.getColumnTypeName(arg0);
    }

    public String getParameterClassName(int arg0) throws SQLException {
        if (this.returnSimpleMetadata) {
            checkBounds(arg0);
            return "java.lang.String";
        }
        checkAvailable();
        return this.metadata.getColumnClassName(arg0);
    }

    public int getParameterMode(int arg0) throws SQLException {
        return 1;
    }

    private void checkBounds(int paramNumber) throws SQLException {
        if (paramNumber < 1) {
            throw SQLError.createSQLException("Parameter index of '" + paramNumber + "' is invalid.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } else if (paramNumber > this.parameterCount) {
            throw SQLError.createSQLException("Parameter index of '" + paramNumber + "' is greater than number of parameters, which is '" + this.parameterCount + "'.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        }
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        try {
            return iface.cast(this);
        } catch (ClassCastException e) {
            throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        }
    }
}
