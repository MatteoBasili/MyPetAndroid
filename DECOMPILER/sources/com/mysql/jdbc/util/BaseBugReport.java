package com.mysql.jdbc.util;

import com.mysql.jdbc.Driver;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public abstract class BaseBugReport {
    private Connection conn;
    private Driver driver;

    public abstract void runTest() throws Exception;

    public abstract void setUp() throws Exception;

    public abstract void tearDown() throws Exception;

    public BaseBugReport() {
        try {
            this.driver = new Driver();
        } catch (SQLException ex) {
            throw new RuntimeException(ex.toString());
        }
    }

    public final void run() throws Exception {
        try {
            setUp();
            runTest();
        } finally {
            tearDown();
        }
    }

    /* access modifiers changed from: protected */
    public final void assertTrue(String message, boolean condition) throws Exception {
        if (!condition) {
            throw new Exception("Assertion failed: " + message);
        }
    }

    /* access modifiers changed from: protected */
    public final void assertTrue(boolean condition) throws Exception {
        assertTrue("(no message given)", condition);
    }

    public String getUrl() {
        return "jdbc:mysql:///test";
    }

    public final synchronized Connection getConnection() throws SQLException {
        Connection connection = this.conn;
        if (connection == null || connection.isClosed()) {
            this.conn = getNewConnection();
        }
        return this.conn;
    }

    public final synchronized Connection getNewConnection() throws SQLException {
        return getConnection(getUrl());
    }

    public final synchronized Connection getConnection(String url) throws SQLException {
        return getConnection(url, (Properties) null);
    }

    public final synchronized Connection getConnection(String url, Properties props) throws SQLException {
        return this.driver.connect(url, props);
    }
}
