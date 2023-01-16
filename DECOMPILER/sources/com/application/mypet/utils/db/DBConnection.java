package com.application.mypet.utils.db;

import android.os.StrictMode;
import android.util.Log;
import com.application.mypet.BuildConfig;
import com.mysql.jdbc.CallableStatement;
import com.mysql.jdbc.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static final String SQL_EXCEPTION = "SQL Error: ";
    private final String db = BuildConfig.DB_NAME;
    private final String ip = BuildConfig.IP_ADDRESS;
    private final String port = BuildConfig.DB_PORT;
    private String timeout = "500";
    private final String username = BuildConfig.DB_USERNAME;
    private final String watchword = BuildConfig.DB_WATCHWORD;

    public DBConnection() {
    }

    public DBConnection(String timeout2) {
        this.timeout = timeout2;
    }

    public String getIp() {
        return this.ip;
    }

    public String getPort() {
        return this.port;
    }

    public String getDb() {
        return this.db;
    }

    public String getUsername() {
        return this.username;
    }

    public String getWatchword() {
        return this.watchword;
    }

    public Connection getConnection() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        try {
            DriverManager.registerDriver(new Driver());
            String connectionURL = "jdbc:mysql://" + getIp() + ":" + getPort() + "/" + getDb() + "?user=" + getUsername() + "&password=" + getWatchword();
            Properties properties = new Properties();
            properties.put("connectTimeout", this.timeout);
            return DriverManager.getConnection(connectionURL, properties);
        } catch (SQLException se) {
            Log.e(SQL_EXCEPTION, se.getMessage());
            return null;
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
    }

    public void closeStatement(CallableStatement stmt) {
        try {
            stmt.close();
        } catch (SQLException e) {
            Log.e(SQL_EXCEPTION, e.getMessage());
        }
    }

    public void closeConnection(Connection con) {
        try {
            con.close();
        } catch (SQLException e) {
            Log.e(SQL_EXCEPTION, e.getMessage());
        }
    }
}
