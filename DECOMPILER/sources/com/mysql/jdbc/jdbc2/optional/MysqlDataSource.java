package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.ConnectionPropertiesImpl;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.NonRegisteringDriver;
import com.mysql.jdbc.SQLError;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.DataSource;

public class MysqlDataSource extends ConnectionPropertiesImpl implements DataSource, Referenceable, Serializable {
    protected static final NonRegisteringDriver mysqlDriver;
    static final long serialVersionUID = -5515846944416881264L;
    protected String databaseName = null;
    protected String encoding = null;
    protected boolean explicitUrl = false;
    protected String hostName = null;
    protected transient PrintWriter logWriter = null;
    protected String password = null;
    protected int port = 3306;
    protected String profileSql = "false";
    protected String url = null;
    protected String user = null;

    static {
        try {
            mysqlDriver = new NonRegisteringDriver();
        } catch (Exception e) {
            throw new RuntimeException("Can not load Driver class com.mysql.jdbc.Driver");
        }
    }

    public Connection getConnection() throws SQLException {
        return getConnection(this.user, this.password);
    }

    public Connection getConnection(String userID, String pass) throws SQLException {
        Properties props = new Properties();
        if (userID != null) {
            props.setProperty(NonRegisteringDriver.USER_PROPERTY_KEY, userID);
        }
        if (pass != null) {
            props.setProperty(NonRegisteringDriver.PASSWORD_PROPERTY_KEY, pass);
        }
        exposeAsProperties(props);
        return getConnection(props);
    }

    public void setDatabaseName(String dbName) {
        this.databaseName = dbName;
    }

    public String getDatabaseName() {
        String str = this.databaseName;
        return str != null ? str : "";
    }

    public void setLogWriter(PrintWriter output) throws SQLException {
        this.logWriter = output;
    }

    public PrintWriter getLogWriter() {
        return this.logWriter;
    }

    public void setLoginTimeout(int seconds) throws SQLException {
    }

    public int getLoginTimeout() {
        return 0;
    }

    public void setPassword(String pass) {
        this.password = pass;
    }

    public void setPort(int p) {
        this.port = p;
    }

    public int getPort() {
        return this.port;
    }

    public void setPortNumber(int p) {
        setPort(p);
    }

    public int getPortNumber() {
        return getPort();
    }

    public void setPropertiesViaRef(Reference ref) throws SQLException {
        super.initializeFromRef(ref);
    }

    public Reference getReference() throws NamingException {
        Reference ref = new Reference(getClass().getName(), "com.mysql.jdbc.jdbc2.optional.MysqlDataSourceFactory", (String) null);
        ref.add(new StringRefAddr(NonRegisteringDriver.USER_PROPERTY_KEY, getUser()));
        ref.add(new StringRefAddr(NonRegisteringDriver.PASSWORD_PROPERTY_KEY, this.password));
        ref.add(new StringRefAddr("serverName", getServerName()));
        ref.add(new StringRefAddr("port", "" + getPort()));
        ref.add(new StringRefAddr("databaseName", getDatabaseName()));
        ref.add(new StringRefAddr("url", getUrl()));
        ref.add(new StringRefAddr("explicitUrl", String.valueOf(this.explicitUrl)));
        try {
            storeToRef(ref);
            return ref;
        } catch (SQLException sqlEx) {
            throw new NamingException(sqlEx.getMessage());
        }
    }

    public void setServerName(String serverName) {
        this.hostName = serverName;
    }

    public String getServerName() {
        String str = this.hostName;
        return str != null ? str : "";
    }

    public void setURL(String url2) {
        setUrl(url2);
    }

    public String getURL() {
        return getUrl();
    }

    public void setUrl(String url2) {
        this.url = url2;
        this.explicitUrl = true;
    }

    public String getUrl() {
        if (!this.explicitUrl) {
            return "jdbc:mysql://" + getServerName() + ":" + getPort() + "/" + getDatabaseName();
        }
        return this.url;
    }

    public void setUser(String userID) {
        this.user = userID;
    }

    public String getUser() {
        return this.user;
    }

    /* access modifiers changed from: protected */
    public Connection getConnection(Properties props) throws SQLException {
        String jdbcUrlToUse;
        if (!this.explicitUrl) {
            StringBuilder jdbcUrl = new StringBuilder("jdbc:mysql://");
            String str = this.hostName;
            if (str != null) {
                jdbcUrl.append(str);
            }
            jdbcUrl.append(":");
            jdbcUrl.append(this.port);
            jdbcUrl.append("/");
            String str2 = this.databaseName;
            if (str2 != null) {
                jdbcUrl.append(str2);
            }
            jdbcUrlToUse = jdbcUrl.toString();
        } else {
            jdbcUrlToUse = this.url;
        }
        Properties urlProps = mysqlDriver.parseURL(jdbcUrlToUse, (Properties) null);
        if (urlProps != null) {
            urlProps.remove(NonRegisteringDriver.DBNAME_PROPERTY_KEY);
            urlProps.remove(NonRegisteringDriver.HOST_PROPERTY_KEY);
            urlProps.remove(NonRegisteringDriver.PORT_PROPERTY_KEY);
            for (String key : urlProps.keySet()) {
                props.setProperty(key, urlProps.getProperty(key));
            }
            return mysqlDriver.connect(jdbcUrlToUse, props);
        }
        throw SQLError.createSQLException(Messages.getString("MysqlDataSource.BadUrl", new Object[]{jdbcUrlToUse}), SQLError.SQL_STATE_CONNECTION_FAILURE, (ExceptionInterceptor) null);
    }

    public Properties exposeAsProperties(Properties props) throws SQLException {
        return exposeAsProperties(props, true);
    }
}
