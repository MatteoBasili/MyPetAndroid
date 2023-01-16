package com.mysql.fabric.jdbc;

import com.mysql.jdbc.NonRegisteringDriver;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

public class FabricMySQLDataSource extends MysqlDataSource implements FabricMySQLConnectionProperties {
    private static final Driver driver;
    private static final long serialVersionUID = 1;
    private String fabricPassword;
    private String fabricProtocol = "http";
    private boolean fabricReportErrors = false;
    private String fabricServerGroup;
    private String fabricShardKey;
    private String fabricShardTable;
    private String fabricUsername;

    static {
        try {
            driver = new FabricMySQLDriver();
        } catch (Exception ex) {
            throw new RuntimeException("Can create driver", ex);
        }
    }

    /* access modifiers changed from: protected */
    public Connection getConnection(Properties props) throws SQLException {
        String jdbcUrlToUse;
        if (!this.explicitUrl) {
            StringBuilder jdbcUrl = new StringBuilder(FabricMySQLDriver.FABRIC_URL_PREFIX);
            if (this.hostName != null) {
                jdbcUrl.append(this.hostName);
            }
            jdbcUrl.append(":");
            jdbcUrl.append(this.port);
            jdbcUrl.append("/");
            if (this.databaseName != null) {
                jdbcUrl.append(this.databaseName);
            }
            jdbcUrlToUse = jdbcUrl.toString();
        } else {
            jdbcUrlToUse = this.url;
        }
        Properties urlProps = ((FabricMySQLDriver) driver).parseFabricURL(jdbcUrlToUse, (Properties) null);
        urlProps.remove(NonRegisteringDriver.DBNAME_PROPERTY_KEY);
        urlProps.remove(NonRegisteringDriver.HOST_PROPERTY_KEY);
        urlProps.remove(NonRegisteringDriver.PORT_PROPERTY_KEY);
        for (String key : urlProps.keySet()) {
            props.setProperty(key, urlProps.getProperty(key));
        }
        String str = this.fabricShardKey;
        if (str != null) {
            props.setProperty(FabricMySQLDriver.FABRIC_SHARD_KEY_PROPERTY_KEY, str);
        }
        String str2 = this.fabricShardTable;
        if (str2 != null) {
            props.setProperty(FabricMySQLDriver.FABRIC_SHARD_TABLE_PROPERTY_KEY, str2);
        }
        String str3 = this.fabricServerGroup;
        if (str3 != null) {
            props.setProperty(FabricMySQLDriver.FABRIC_SERVER_GROUP_PROPERTY_KEY, str3);
        }
        props.setProperty(FabricMySQLDriver.FABRIC_PROTOCOL_PROPERTY_KEY, this.fabricProtocol);
        String str4 = this.fabricUsername;
        if (str4 != null) {
            props.setProperty(FabricMySQLDriver.FABRIC_USERNAME_PROPERTY_KEY, str4);
        }
        String str5 = this.fabricPassword;
        if (str5 != null) {
            props.setProperty(FabricMySQLDriver.FABRIC_PASSWORD_PROPERTY_KEY, str5);
        }
        props.setProperty(FabricMySQLDriver.FABRIC_REPORT_ERRORS_PROPERTY_KEY, Boolean.toString(this.fabricReportErrors));
        return driver.connect(jdbcUrlToUse, props);
    }

    public void setFabricShardKey(String value) {
        this.fabricShardKey = value;
    }

    public String getFabricShardKey() {
        return this.fabricShardKey;
    }

    public void setFabricShardTable(String value) {
        this.fabricShardTable = value;
    }

    public String getFabricShardTable() {
        return this.fabricShardTable;
    }

    public void setFabricServerGroup(String value) {
        this.fabricServerGroup = value;
    }

    public String getFabricServerGroup() {
        return this.fabricServerGroup;
    }

    public void setFabricProtocol(String value) {
        this.fabricProtocol = value;
    }

    public String getFabricProtocol() {
        return this.fabricProtocol;
    }

    public void setFabricUsername(String value) {
        this.fabricUsername = value;
    }

    public String getFabricUsername() {
        return this.fabricUsername;
    }

    public void setFabricPassword(String value) {
        this.fabricPassword = value;
    }

    public String getFabricPassword() {
        return this.fabricPassword;
    }

    public void setFabricReportErrors(boolean value) {
        this.fabricReportErrors = value;
    }

    public boolean getFabricReportErrors() {
        return this.fabricReportErrors;
    }
}
