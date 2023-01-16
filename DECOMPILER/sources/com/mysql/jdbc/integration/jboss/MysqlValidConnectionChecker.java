package com.mysql.jdbc.integration.jboss;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.jboss.resource.adapter.jdbc.ValidConnectionChecker;

public final class MysqlValidConnectionChecker implements ValidConnectionChecker, Serializable {
    private static final long serialVersionUID = 8909421133577519177L;

    public SQLException isValidConnection(Connection conn) {
        Statement pingStatement = null;
        try {
            pingStatement = conn.createStatement();
            pingStatement.executeQuery("/* ping */ SELECT 1").close();
            if (pingStatement != null) {
                try {
                    pingStatement.close();
                } catch (SQLException e) {
                }
            }
            return null;
        } catch (SQLException e2) {
            if (pingStatement != null) {
                try {
                    pingStatement.close();
                } catch (SQLException e3) {
                }
            }
            SQLException sQLException = e2;
            return e2;
        } catch (Throwable th) {
            if (pingStatement != null) {
                try {
                    pingStatement.close();
                } catch (SQLException e4) {
                }
            }
            throw th;
        }
    }
}
