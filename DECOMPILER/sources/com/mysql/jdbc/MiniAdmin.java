package com.mysql.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class MiniAdmin {
    private Connection conn;

    public MiniAdmin(Connection conn2) throws SQLException {
        if (conn2 == null) {
            throw SQLError.createSQLException(Messages.getString("MiniAdmin.0"), SQLError.SQL_STATE_GENERAL_ERROR, (ExceptionInterceptor) null);
        } else if (conn2 instanceof Connection) {
            this.conn = (Connection) conn2;
        } else {
            throw SQLError.createSQLException(Messages.getString("MiniAdmin.1"), SQLError.SQL_STATE_GENERAL_ERROR, ((ConnectionImpl) conn2).getExceptionInterceptor());
        }
    }

    public MiniAdmin(String jdbcUrl) throws SQLException {
        this(jdbcUrl, new Properties());
    }

    public MiniAdmin(String jdbcUrl, Properties props) throws SQLException {
        Connection connection = (Connection) new Driver().connect(jdbcUrl, props);
        Connection connection2 = connection;
        this.conn = connection;
    }

    public void shutdown() throws SQLException {
        this.conn.shutdownServer();
    }
}
