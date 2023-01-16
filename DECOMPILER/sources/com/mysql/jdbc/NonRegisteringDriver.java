package com.mysql.jdbc;

import com.application.mypet.BuildConfig;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;

public class NonRegisteringDriver implements Driver {
    private static final String ALLOWED_QUOTES = "\"'";
    public static final String DBNAME_PROPERTY_KEY = "DBNAME";
    public static final boolean DEBUG = false;
    public static final int HOST_NAME_INDEX = 0;
    public static final String HOST_PROPERTY_KEY = "HOST";
    public static final String LICENSE = "GPL";
    public static final String LOADBALANCE_URL_PREFIX = "jdbc:mysql:loadbalance://";
    private static final String MXJ_URL_PREFIX = "jdbc:mysql:mxj://";
    public static final String NAME = "MySQL Connector Java";
    public static final String NUM_HOSTS_PROPERTY_KEY = "NUM_HOSTS";
    public static final String OS = getOSName();
    public static final String PASSWORD_PROPERTY_KEY = "password";
    public static final String PATH_PROPERTY_KEY = "PATH";
    public static final String PLATFORM = getPlatform();
    public static final int PORT_NUMBER_INDEX = 1;
    public static final String PORT_PROPERTY_KEY = "PORT";
    public static final String PROPERTIES_TRANSFORM_KEY = "propertiesTransform";
    public static final String PROTOCOL_PROPERTY_KEY = "PROTOCOL";
    private static final String REPLICATION_URL_PREFIX = "jdbc:mysql:replication://";
    public static final String RUNTIME_VENDOR = System.getProperty("java.vendor");
    public static final String RUNTIME_VERSION = System.getProperty("java.version");
    public static final boolean TRACE = false;
    private static final String URL_PREFIX = "jdbc:mysql://";
    public static final String USER_PROPERTY_KEY = "user";
    public static final String USE_CONFIG_PROPERTY_KEY = "useConfigs";
    public static final String VERSION = "5.1.49";

    static {
        try {
            Class.forName(AbandonedConnectionCleanupThread.class.getName());
        } catch (ClassNotFoundException e) {
        }
    }

    public static String getOSName() {
        return System.getProperty("os.name");
    }

    public static String getPlatform() {
        return System.getProperty("os.arch");
    }

    static int getMajorVersionInternal() {
        return safeIntParse("5");
    }

    static int getMinorVersionInternal() {
        return safeIntParse("1");
    }

    protected static String[] parseHostPortPair(String hostPortPair) throws SQLException {
        String[] splitValues = new String[2];
        if (StringUtils.startsWithIgnoreCaseAndWs(hostPortPair, "address=")) {
            splitValues[0] = hostPortPair.trim();
            splitValues[1] = null;
            return splitValues;
        }
        int portIndex = hostPortPair.indexOf(":");
        if (portIndex == -1) {
            splitValues[0] = hostPortPair;
            splitValues[1] = null;
        } else if (portIndex + 1 < hostPortPair.length()) {
            String portAsString = hostPortPair.substring(portIndex + 1);
            splitValues[0] = hostPortPair.substring(0, portIndex);
            splitValues[1] = portAsString;
        } else {
            throw SQLError.createSQLException(Messages.getString("NonRegisteringDriver.37"), SQLError.SQL_STATE_INVALID_CONNECTION_ATTRIBUTE, (ExceptionInterceptor) null);
        }
        return splitValues;
    }

    private static int safeIntParse(String intAsString) {
        try {
            return Integer.parseInt(intAsString);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean acceptsURL(String url) throws SQLException {
        if (url != null) {
            return parseURL(url, (Properties) null) != null;
        }
        throw SQLError.createSQLException(Messages.getString("NonRegisteringDriver.1"), SQLError.SQL_STATE_UNABLE_TO_CONNECT_TO_DATASOURCE, (ExceptionInterceptor) null);
    }

    public Connection connect(String url, Properties info) throws SQLException {
        if (url == null) {
            throw SQLError.createSQLException(Messages.getString("NonRegisteringDriver.1"), SQLError.SQL_STATE_UNABLE_TO_CONNECT_TO_DATASOURCE, (ExceptionInterceptor) null);
        } else if (StringUtils.startsWithIgnoreCase(url, LOADBALANCE_URL_PREFIX)) {
            return connectLoadBalanced(url, info);
        } else {
            if (StringUtils.startsWithIgnoreCase(url, REPLICATION_URL_PREFIX)) {
                return connectReplicationConnection(url, info);
            }
            Properties parseURL = parseURL(url, info);
            Properties props = parseURL;
            if (parseURL == null) {
                return null;
            }
            if (!"1".equals(props.getProperty(NUM_HOSTS_PROPERTY_KEY))) {
                return connectFailover(url, info);
            }
            try {
                return ConnectionImpl.getInstance(host(props), port(props), props, database(props), url);
            } catch (SQLException sqlEx) {
                throw sqlEx;
            } catch (Exception ex) {
                SQLException sqlEx2 = SQLError.createSQLException(Messages.getString("NonRegisteringDriver.17") + ex.toString() + Messages.getString("NonRegisteringDriver.18"), SQLError.SQL_STATE_UNABLE_TO_CONNECT_TO_DATASOURCE, (ExceptionInterceptor) null);
                sqlEx2.initCause(ex);
                throw sqlEx2;
            }
        }
    }

    private Connection connectLoadBalanced(String url, Properties info) throws SQLException {
        Properties parsedProps = parseURL(url, info);
        if (parsedProps == null) {
            return null;
        }
        parsedProps.remove("roundRobinLoadBalance");
        int numHosts = Integer.parseInt(parsedProps.getProperty(NUM_HOSTS_PROPERTY_KEY));
        List<String> hostList = new ArrayList<>();
        for (int i = 0; i < numHosts; i++) {
            int index = i + 1;
            hostList.add(parsedProps.getProperty("HOST." + index) + ":" + parsedProps.getProperty("PORT." + index));
        }
        return LoadBalancedConnectionProxy.createProxyInstance(hostList, parsedProps);
    }

    private Connection connectFailover(String url, Properties info) throws SQLException {
        Properties parsedProps = parseURL(url, info);
        if (parsedProps == null) {
            return null;
        }
        parsedProps.remove("roundRobinLoadBalance");
        int numHosts = Integer.parseInt(parsedProps.getProperty(NUM_HOSTS_PROPERTY_KEY));
        List<String> hostList = new ArrayList<>();
        for (int i = 0; i < numHosts; i++) {
            int index = i + 1;
            hostList.add(parsedProps.getProperty("HOST." + index) + ":" + parsedProps.getProperty("PORT." + index));
        }
        return FailoverConnectionProxy.createProxyInstance(hostList, parsedProps);
    }

    /* access modifiers changed from: protected */
    public Connection connectReplicationConnection(String url, Properties info) throws SQLException {
        Properties parsedProps = parseURL(url, info);
        if (parsedProps == null) {
            return null;
        }
        Properties masterProps = (Properties) parsedProps.clone();
        Properties slavesProps = (Properties) parsedProps.clone();
        slavesProps.setProperty("com.mysql.jdbc.ReplicationConnection.isSlave", "true");
        int numHosts = Integer.parseInt(parsedProps.getProperty(NUM_HOSTS_PROPERTY_KEY));
        if (numHosts >= 2) {
            List<String> slaveHostList = new ArrayList<>();
            List<String> masterHostList = new ArrayList<>();
            boolean usesExplicitServerType = isHostPropertiesList(masterProps.getProperty("HOST.1") + ":" + masterProps.getProperty("PORT.1"));
            for (int i = 0; i < numHosts; i++) {
                int index = i + 1;
                masterProps.remove("HOST." + index);
                masterProps.remove("PORT." + index);
                slavesProps.remove("HOST." + index);
                slavesProps.remove("PORT." + index);
                String host = parsedProps.getProperty("HOST." + index);
                String port = parsedProps.getProperty("PORT." + index);
                if (!usesExplicitServerType) {
                    if (i == 0) {
                        masterHostList.add(host + ":" + port);
                    } else {
                        slaveHostList.add(host + ":" + port);
                    }
                } else if (isHostMaster(host)) {
                    masterHostList.add(host);
                } else {
                    slaveHostList.add(host);
                }
            }
            slavesProps.remove(NUM_HOSTS_PROPERTY_KEY);
            masterProps.remove(NUM_HOSTS_PROPERTY_KEY);
            masterProps.remove(HOST_PROPERTY_KEY);
            masterProps.remove(PORT_PROPERTY_KEY);
            slavesProps.remove(HOST_PROPERTY_KEY);
            slavesProps.remove(PORT_PROPERTY_KEY);
            return ReplicationConnectionProxy.createProxyInstance(masterHostList, masterProps, slaveHostList, slavesProps);
        }
        throw SQLError.createSQLException("Must specify at least one slave host to connect to for master/slave replication load-balancing functionality", SQLError.SQL_STATE_INVALID_CONNECTION_ATTRIBUTE, (ExceptionInterceptor) null);
    }

    private boolean isHostMaster(String host) {
        if (!isHostPropertiesList(host)) {
            return false;
        }
        Properties hostSpecificProps = expandHostKeyValues(host);
        if (!hostSpecificProps.containsKey("type") || !"master".equalsIgnoreCase(hostSpecificProps.get("type").toString())) {
            return false;
        }
        return true;
    }

    public String database(Properties props) {
        return props.getProperty(DBNAME_PROPERTY_KEY);
    }

    public int getMajorVersion() {
        return getMajorVersionInternal();
    }

    public int getMinorVersion() {
        return getMinorVersionInternal();
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        if (info == null) {
            info = new Properties();
        }
        if (url != null && url.startsWith(URL_PREFIX)) {
            info = parseURL(url, info);
        }
        DriverPropertyInfo hostProp = new DriverPropertyInfo(HOST_PROPERTY_KEY, info.getProperty(HOST_PROPERTY_KEY));
        hostProp.required = true;
        hostProp.description = Messages.getString("NonRegisteringDriver.3");
        DriverPropertyInfo portProp = new DriverPropertyInfo(PORT_PROPERTY_KEY, info.getProperty(PORT_PROPERTY_KEY, BuildConfig.DB_PORT));
        portProp.required = false;
        portProp.description = Messages.getString("NonRegisteringDriver.7");
        DriverPropertyInfo dbProp = new DriverPropertyInfo(DBNAME_PROPERTY_KEY, info.getProperty(DBNAME_PROPERTY_KEY));
        dbProp.required = false;
        dbProp.description = "Database name";
        DriverPropertyInfo userProp = new DriverPropertyInfo(USER_PROPERTY_KEY, info.getProperty(USER_PROPERTY_KEY));
        userProp.required = true;
        userProp.description = Messages.getString("NonRegisteringDriver.13");
        DriverPropertyInfo passwordProp = new DriverPropertyInfo(PASSWORD_PROPERTY_KEY, info.getProperty(PASSWORD_PROPERTY_KEY));
        passwordProp.required = true;
        passwordProp.description = Messages.getString("NonRegisteringDriver.16");
        DriverPropertyInfo[] dpi = ConnectionPropertiesImpl.exposeAsDriverPropertyInfo(info, 5);
        dpi[0] = hostProp;
        dpi[1] = portProp;
        dpi[2] = dbProp;
        dpi[3] = userProp;
        dpi[4] = passwordProp;
        return dpi;
    }

    public String host(Properties props) {
        return props.getProperty(HOST_PROPERTY_KEY, "localhost");
    }

    public boolean jdbcCompliant() {
        return false;
    }

    public Properties parseURL(String url, Properties defaults) throws SQLException {
        String hostStuff;
        int numHosts;
        String configNames;
        int beginningOfSlashes;
        int index;
        String parameter;
        String url2 = url;
        Properties properties = defaults;
        Properties urlProps = properties != null ? new Properties(properties) : new Properties();
        if (url2 == null) {
            return null;
        }
        if (!StringUtils.startsWithIgnoreCase(url2, URL_PREFIX) && !StringUtils.startsWithIgnoreCase(url2, MXJ_URL_PREFIX) && !StringUtils.startsWithIgnoreCase(url2, LOADBALANCE_URL_PREFIX) && !StringUtils.startsWithIgnoreCase(url2, REPLICATION_URL_PREFIX)) {
            return null;
        }
        int beginningOfSlashes2 = url2.indexOf("//");
        if (StringUtils.startsWithIgnoreCase(url2, MXJ_URL_PREFIX)) {
            urlProps.setProperty("socketFactory", "com.mysql.management.driverlaunched.ServerLauncherSocketFactory");
        }
        int index2 = url2.indexOf("?");
        int i = 0;
        if (index2 != -1) {
            String paramString = url2.substring(index2 + 1, url.length());
            String url3 = url2.substring(0, index2);
            StringTokenizer queryParams = new StringTokenizer(paramString, "&");
            while (queryParams.hasMoreTokens()) {
                String parameterValuePair = queryParams.nextToken();
                int indexOfEquals = StringUtils.indexOfIgnoreCase(i, parameterValuePair, "=");
                String value = null;
                if (indexOfEquals != -1) {
                    String parameter2 = parameterValuePair.substring(i, indexOfEquals);
                    if (indexOfEquals + 1 < parameterValuePair.length()) {
                        value = parameterValuePair.substring(indexOfEquals + 1);
                        parameter = parameter2;
                    } else {
                        parameter = parameter2;
                    }
                } else {
                    parameter = null;
                }
                if (value != null && value.length() > 0 && parameter != null && parameter.length() > 0) {
                    try {
                        urlProps.setProperty(parameter, URLDecoder.decode(value, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        urlProps.setProperty(parameter, URLDecoder.decode(value));
                    } catch (NoSuchMethodError e2) {
                        urlProps.setProperty(parameter, URLDecoder.decode(value));
                    }
                }
                i = 0;
            }
            url2 = url3;
        }
        String url4 = url2.substring(beginningOfSlashes2 + 2);
        int slashIndex = StringUtils.indexOfIgnoreCase(0, url4, "/", ALLOWED_QUOTES, ALLOWED_QUOTES, StringUtils.SEARCH_MODE__ALL);
        if (slashIndex != -1) {
            String hostStuff2 = url4.substring(0, slashIndex);
            if (slashIndex + 1 < url4.length()) {
                urlProps.put(DBNAME_PROPERTY_KEY, url4.substring(slashIndex + 1, url4.length()));
            }
            hostStuff = hostStuff2;
        } else {
            hostStuff = url4;
        }
        int numHosts2 = 0;
        if (hostStuff == null || hostStuff.trim().length() <= 0) {
            int i2 = beginningOfSlashes2;
            int i3 = index2;
            urlProps.setProperty("HOST.1", "localhost");
            urlProps.setProperty("PORT.1", BuildConfig.DB_PORT);
            numHosts = 1;
        } else {
            String str = url4;
            Iterator i$ = StringUtils.split(hostStuff, ",", ALLOWED_QUOTES, ALLOWED_QUOTES, false).iterator();
            while (i$.hasNext()) {
                numHosts2++;
                String[] hostPortPair = parseHostPortPair(i$.next());
                Iterator it = i$;
                if (hostPortPair[0] == null || hostPortPair[0].trim().length() <= 0) {
                    beginningOfSlashes = beginningOfSlashes2;
                    urlProps.setProperty("HOST." + numHosts2, "localhost");
                } else {
                    beginningOfSlashes = beginningOfSlashes2;
                    urlProps.setProperty("HOST." + numHosts2, hostPortPair[0]);
                }
                if (hostPortPair[1] != null) {
                    index = index2;
                    urlProps.setProperty("PORT." + numHosts2, hostPortPair[1]);
                } else {
                    index = index2;
                    urlProps.setProperty("PORT." + numHosts2, BuildConfig.DB_PORT);
                }
                beginningOfSlashes2 = beginningOfSlashes;
                i$ = it;
                index2 = index;
            }
            int i4 = beginningOfSlashes2;
            int i5 = index2;
            numHosts = numHosts2;
        }
        urlProps.setProperty(NUM_HOSTS_PROPERTY_KEY, String.valueOf(numHosts));
        urlProps.setProperty(HOST_PROPERTY_KEY, urlProps.getProperty("HOST.1"));
        urlProps.setProperty(PORT_PROPERTY_KEY, urlProps.getProperty("PORT.1"));
        String propertiesTransformClassName = urlProps.getProperty(PROPERTIES_TRANSFORM_KEY);
        if (propertiesTransformClassName != null) {
            try {
                urlProps = ((ConnectionPropertiesTransform) Class.forName(propertiesTransformClassName).newInstance()).transformProperties(urlProps);
            } catch (InstantiationException e3) {
                throw SQLError.createSQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e3.toString(), SQLError.SQL_STATE_INVALID_CONNECTION_ATTRIBUTE, (ExceptionInterceptor) null);
            } catch (IllegalAccessException e4) {
                throw SQLError.createSQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e4.toString(), SQLError.SQL_STATE_INVALID_CONNECTION_ATTRIBUTE, (ExceptionInterceptor) null);
            } catch (ClassNotFoundException e5) {
                throw SQLError.createSQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e5.toString(), SQLError.SQL_STATE_INVALID_CONNECTION_ATTRIBUTE, (ExceptionInterceptor) null);
            }
        }
        if (Util.isColdFusion() && urlProps.getProperty("autoConfigureForColdFusion", "true").equalsIgnoreCase("true")) {
            String configs = urlProps.getProperty(USE_CONFIG_PROPERTY_KEY);
            StringBuilder newConfigs = new StringBuilder();
            if (configs != null) {
                newConfigs.append(configs);
                newConfigs.append(",");
            }
            newConfigs.append("coldFusion");
            urlProps.setProperty(USE_CONFIG_PROPERTY_KEY, newConfigs.toString());
        }
        String configNames2 = null;
        if (properties != null) {
            configNames2 = properties.getProperty(USE_CONFIG_PROPERTY_KEY);
        }
        if (configNames2 == null) {
            configNames = urlProps.getProperty(USE_CONFIG_PROPERTY_KEY);
        } else {
            configNames = configNames2;
        }
        if (configNames != null) {
            List<String> splitNames = StringUtils.split(configNames, ",", true);
            Properties configProps = new Properties();
            for (String configName : splitNames) {
                try {
                    InputStream configAsStream = getClass().getResourceAsStream("configs/" + configName + ".properties");
                    if (configAsStream != null) {
                        configProps.load(configAsStream);
                    } else {
                        throw SQLError.createSQLException("Can't find configuration template named '" + configName + "'", SQLError.SQL_STATE_INVALID_CONNECTION_ATTRIBUTE, (ExceptionInterceptor) null);
                    }
                } catch (IOException ioEx) {
                    SQLException sqlEx = SQLError.createSQLException("Unable to load configuration template '" + configName + "' due to underlying IOException: " + ioEx, SQLError.SQL_STATE_INVALID_CONNECTION_ATTRIBUTE, (ExceptionInterceptor) null);
                    sqlEx.initCause(ioEx);
                    throw sqlEx;
                }
            }
            for (Object obj : urlProps.keySet()) {
                String key = obj.toString();
                configProps.setProperty(key, urlProps.getProperty(key));
            }
            urlProps = configProps;
        }
        if (properties != null) {
            for (Object obj2 : defaults.keySet()) {
                String key2 = obj2.toString();
                if (!key2.equals(NUM_HOSTS_PROPERTY_KEY)) {
                    urlProps.setProperty(key2, properties.getProperty(key2));
                }
            }
        }
        return urlProps;
    }

    public int port(Properties props) {
        return Integer.parseInt(props.getProperty(PORT_PROPERTY_KEY, BuildConfig.DB_PORT));
    }

    public String property(String name, Properties props) {
        return props.getProperty(name);
    }

    public static Properties expandHostKeyValues(String host) {
        Properties hostProps = new Properties();
        if (isHostPropertiesList(host)) {
            for (String propDef : StringUtils.split(host.substring("address=".length() + 1), ")", "'\"", "'\"", true)) {
                if (propDef.startsWith("(")) {
                    propDef = propDef.substring(1);
                }
                List<String> kvp = StringUtils.split(propDef, "=", "'\"", "'\"", true);
                String key = kvp.get(0);
                String value = kvp.size() > 1 ? kvp.get(1) : null;
                if (value != null && ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'")))) {
                    value = value.substring(1, value.length() - 1);
                }
                if (value != null) {
                    if (HOST_PROPERTY_KEY.equalsIgnoreCase(key) || DBNAME_PROPERTY_KEY.equalsIgnoreCase(key) || PORT_PROPERTY_KEY.equalsIgnoreCase(key) || PROTOCOL_PROPERTY_KEY.equalsIgnoreCase(key) || PATH_PROPERTY_KEY.equalsIgnoreCase(key)) {
                        key = key.toUpperCase(Locale.ENGLISH);
                    } else if (USER_PROPERTY_KEY.equalsIgnoreCase(key) || PASSWORD_PROPERTY_KEY.equalsIgnoreCase(key)) {
                        key = key.toLowerCase(Locale.ENGLISH);
                    }
                    hostProps.setProperty(key, value);
                }
            }
        }
        return hostProps;
    }

    public static boolean isHostPropertiesList(String host) {
        return host != null && StringUtils.startsWithIgnoreCase(host, "address=");
    }
}
