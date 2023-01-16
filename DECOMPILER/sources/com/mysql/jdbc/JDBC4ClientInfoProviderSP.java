package com.mysql.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

public class JDBC4ClientInfoProviderSP implements JDBC4ClientInfoProvider {
    PreparedStatement getClientInfoBulkSp;
    PreparedStatement getClientInfoSp;
    PreparedStatement setClientInfoSp;

    public synchronized void initialize(Connection conn, Properties configurationProps) throws SQLException {
        String identifierQuote = conn.getMetaData().getIdentifierQuoteString();
        String setClientInfoSpName = configurationProps.getProperty("clientInfoSetSPName", "setClientInfo");
        String getClientInfoSpName = configurationProps.getProperty("clientInfoGetSPName", "getClientInfo");
        String getClientInfoBulkSpName = configurationProps.getProperty("clientInfoGetBulkSPName", "getClientInfoBulk");
        String clientInfoCatalog = configurationProps.getProperty("clientInfoCatalog", "");
        String catalog = "".equals(clientInfoCatalog) ? conn.getCatalog() : clientInfoCatalog;
        this.setClientInfoSp = ((Connection) conn).clientPrepareStatement("CALL " + identifierQuote + catalog + identifierQuote + "." + identifierQuote + setClientInfoSpName + identifierQuote + "(?, ?)");
        this.getClientInfoSp = ((Connection) conn).clientPrepareStatement("CALL" + identifierQuote + catalog + identifierQuote + "." + identifierQuote + getClientInfoSpName + identifierQuote + "(?)");
        this.getClientInfoBulkSp = ((Connection) conn).clientPrepareStatement("CALL " + identifierQuote + catalog + identifierQuote + "." + identifierQuote + getClientInfoBulkSpName + identifierQuote + "()");
    }

    public synchronized void destroy() throws SQLException {
        PreparedStatement preparedStatement = this.setClientInfoSp;
        if (preparedStatement != null) {
            preparedStatement.close();
            this.setClientInfoSp = null;
        }
        PreparedStatement preparedStatement2 = this.getClientInfoSp;
        if (preparedStatement2 != null) {
            preparedStatement2.close();
            this.getClientInfoSp = null;
        }
        PreparedStatement preparedStatement3 = this.getClientInfoBulkSp;
        if (preparedStatement3 != null) {
            preparedStatement3.close();
            this.getClientInfoBulkSp = null;
        }
    }

    public synchronized Properties getClientInfo(Connection conn) throws SQLException {
        Properties props;
        ResultSet rs = null;
        props = new Properties();
        try {
            this.getClientInfoBulkSp.execute();
            rs = this.getClientInfoBulkSp.getResultSet();
            while (rs.next()) {
                try {
                    props.setProperty(rs.getString(1), rs.getString(2));
                } catch (Throwable th) {
                    th = th;
                }
            }
            if (rs != null) {
                rs.close();
            }
        } catch (Throwable th2) {
            th = th2;
            if (rs != null) {
                rs.close();
            }
            throw th;
        }
        return props;
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x002d A[SYNTHETIC, Splitter:B:16:0x002d] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized java.lang.String getClientInfo(java.sql.Connection r5, java.lang.String r6) throws java.sql.SQLException {
        /*
            r4 = this;
            monitor-enter(r4)
            r0 = 0
            r1 = 0
            java.sql.PreparedStatement r2 = r4.getClientInfoSp     // Catch:{ all -> 0x002a }
            r3 = 1
            r2.setString(r3, r6)     // Catch:{ all -> 0x002a }
            java.sql.PreparedStatement r2 = r4.getClientInfoSp     // Catch:{ all -> 0x002a }
            r2.execute()     // Catch:{ all -> 0x002a }
            java.sql.PreparedStatement r2 = r4.getClientInfoSp     // Catch:{ all -> 0x002a }
            java.sql.ResultSet r2 = r2.getResultSet()     // Catch:{ all -> 0x002a }
            r0 = r2
            boolean r2 = r0.next()     // Catch:{ all -> 0x002a }
            if (r2 == 0) goto L_0x0023
            java.lang.String r2 = r0.getString(r3)     // Catch:{ all -> 0x0021 }
            r1 = r2
            goto L_0x0023
        L_0x0021:
            r2 = move-exception
            goto L_0x002b
        L_0x0023:
            if (r0 == 0) goto L_0x0028
            r0.close()     // Catch:{ all -> 0x0031 }
        L_0x0028:
            monitor-exit(r4)
            return r1
        L_0x002a:
            r2 = move-exception
        L_0x002b:
            if (r0 == 0) goto L_0x0030
            r0.close()     // Catch:{ all -> 0x0031 }
        L_0x0030:
            throw r2     // Catch:{ all -> 0x0031 }
        L_0x0031:
            r5 = move-exception
            monitor-exit(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.JDBC4ClientInfoProviderSP.getClientInfo(java.sql.Connection, java.lang.String):java.lang.String");
    }

    public synchronized void setClientInfo(Connection conn, Properties properties) throws SQLClientInfoException {
        try {
            Enumeration<?> propNames = properties.propertyNames();
            while (propNames.hasMoreElements()) {
                String name = (String) propNames.nextElement();
                setClientInfo(conn, name, properties.getProperty(name));
            }
        } catch (SQLException sqlEx) {
            SQLClientInfoException clientInfoEx = new SQLClientInfoException();
            clientInfoEx.initCause(sqlEx);
            throw clientInfoEx;
        }
    }

    public synchronized void setClientInfo(Connection conn, String name, String value) throws SQLClientInfoException {
        try {
            this.setClientInfoSp.setString(1, name);
            this.setClientInfoSp.setString(2, value);
            this.setClientInfoSp.execute();
        } catch (SQLException sqlEx) {
            SQLClientInfoException clientInfoEx = new SQLClientInfoException();
            clientInfoEx.initCause(sqlEx);
            throw clientInfoEx;
        }
    }
}
