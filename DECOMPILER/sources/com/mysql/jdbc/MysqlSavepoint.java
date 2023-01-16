package com.mysql.jdbc;

import java.rmi.server.UID;
import java.sql.SQLException;
import java.sql.Savepoint;

public class MysqlSavepoint implements Savepoint {
    private ExceptionInterceptor exceptionInterceptor;
    private String savepointName;

    private static String getUniqueId() {
        String uidStr = new UID().toString();
        int uidLength = uidStr.length();
        StringBuilder safeString = new StringBuilder(uidLength + 1);
        safeString.append('_');
        for (int i = 0; i < uidLength; i++) {
            char c = uidStr.charAt(i);
            if (Character.isLetter(c) || Character.isDigit(c)) {
                safeString.append(c);
            } else {
                safeString.append('_');
            }
        }
        return safeString.toString();
    }

    MysqlSavepoint(ExceptionInterceptor exceptionInterceptor2) throws SQLException {
        this(getUniqueId(), exceptionInterceptor2);
    }

    MysqlSavepoint(String name, ExceptionInterceptor exceptionInterceptor2) throws SQLException {
        if (name == null || name.length() == 0) {
            throw SQLError.createSQLException("Savepoint name can not be NULL or empty", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor2);
        }
        this.savepointName = name;
        this.exceptionInterceptor = exceptionInterceptor2;
    }

    public int getSavepointId() throws SQLException {
        throw SQLError.createSQLException("Only named savepoints are supported.", SQLError.SQL_STATE_DRIVER_NOT_CAPABLE, this.exceptionInterceptor);
    }

    public String getSavepointName() throws SQLException {
        return this.savepointName;
    }
}
