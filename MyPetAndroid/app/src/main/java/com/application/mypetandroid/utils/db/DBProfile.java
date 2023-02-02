package com.application.mypetandroid.utils.db;

import android.os.StrictMode;

import com.application.mypetandroid.BuildConfig;
import com.mysql.jdbc.Driver;

import org.apache.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBProfile {

    private static final Logger logger = Logger.getLogger(DBProfile.class);
    private static final String SQL_EXCEPTION = "SQL Error: ";

    private static final String IP = BuildConfig.DB_IP_ADDRESS;
    private static final String PORT = BuildConfig.DB_PORT;
    private static final String DB = BuildConfig.DB_NAME;
    private static final String USERNAME = BuildConfig.DB_USERNAME;
    private static final String WATCHWORD = BuildConfig.DB_WATCHWORD;

    public String getIp() {
        return IP;
    }

    public String getPort() {
        return PORT;
    }

    public String getDb() {
        return DB;
    }

    public String getUsername() {
        return USERNAME;
    }

    public String getWatchword() {
        return WATCHWORD;
    }

    public Connection getConnection() {
        Connection connection = null;
        String connectionURL;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        try {
            DriverManager.registerDriver(new Driver());
            connectionURL = "jdbc:mysql://" + getIp() + ":" + getPort() + "/" + getDb() + "?user=" + getUsername() + "&password=" + getWatchword();
            Properties properties = new Properties();
            String timeout = "500";
            properties.put("connectTimeout", timeout);
            connection = DriverManager.getConnection(connectionURL, properties);
        } catch (SQLException se) {
            logger.error(SQL_EXCEPTION, se);
        } catch (Exception e) {
            logger.error("Error: ", e);
        }

        return connection;
    }

    public void closeStatement(CallableStatement stmt) {
        try {
            stmt.close();
        } catch (SQLException e) {
            logger.error(SQL_EXCEPTION, e);
        }
    }

    public void closeConnection(Connection con) {
        try {
            con.close();
        } catch (SQLException e) {
            logger.error(SQL_EXCEPTION, e);
        }
    }

}
