package com.mysql.jdbc;

import java.sql.SQLException;
import java.sql.SQLType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.format.DateTimeParseException;

public class JDBC42UpdatableResultSet extends JDBC4UpdatableResultSet {
    public JDBC42UpdatableResultSet(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt) throws SQLException {
        super(catalog, fields, tuples, conn, creatorStmt);
    }

    private int translateAndCheckSqlType(SQLType sqlType) throws SQLException {
        return JDBC42Helper.translateAndCheckSqlType(sqlType, getExceptionInterceptor());
    }

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (type != null) {
                try {
                    if (type.equals(LocalDate.class)) {
                        T cast = type.cast(getDate(columnIndex).toLocalDate());
                        return cast;
                    } else if (type.equals(LocalDateTime.class)) {
                        T cast2 = type.cast(getTimestamp(columnIndex).toLocalDateTime());
                        return cast2;
                    } else if (type.equals(LocalTime.class)) {
                        T cast3 = type.cast(getTime(columnIndex).toLocalTime());
                        return cast3;
                    } else if (type.equals(OffsetDateTime.class)) {
                        T cast4 = type.cast(OffsetDateTime.parse(getString(columnIndex)));
                        return cast4;
                    } else {
                        if (type.equals(OffsetTime.class)) {
                            try {
                                T cast5 = type.cast(OffsetTime.parse(getString(columnIndex)));
                                return cast5;
                            } catch (DateTimeParseException e) {
                            }
                        }
                        T object = super.getObject(columnIndex, type);
                        return object;
                    }
                } catch (DateTimeParseException e2) {
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                throw SQLError.createSQLException("Type parameter can not be null", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        super.updateObject(columnIndex, JDBC42Helper.convertJavaTimeToJavaSql(x));
    }

    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        super.updateObject(columnIndex, JDBC42Helper.convertJavaTimeToJavaSql(x), scaleOrLength);
    }

    public void updateObject(String columnLabel, Object x) throws SQLException {
        super.updateObject(columnLabel, JDBC42Helper.convertJavaTimeToJavaSql(x));
    }

    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        super.updateObject(columnLabel, JDBC42Helper.convertJavaTimeToJavaSql(x), scaleOrLength);
    }

    public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
        super.updateObjectInternal(columnIndex, JDBC42Helper.convertJavaTimeToJavaSql(x), Integer.valueOf(translateAndCheckSqlType(targetSqlType)), 0);
    }

    public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        super.updateObjectInternal(columnIndex, JDBC42Helper.convertJavaTimeToJavaSql(x), Integer.valueOf(translateAndCheckSqlType(targetSqlType)), scaleOrLength);
    }

    public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
        super.updateObjectInternal(findColumn(columnLabel), JDBC42Helper.convertJavaTimeToJavaSql(x), Integer.valueOf(translateAndCheckSqlType(targetSqlType)), 0);
    }

    public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        super.updateObjectInternal(findColumn(columnLabel), JDBC42Helper.convertJavaTimeToJavaSql(x), Integer.valueOf(translateAndCheckSqlType(targetSqlType)), scaleOrLength);
    }
}
