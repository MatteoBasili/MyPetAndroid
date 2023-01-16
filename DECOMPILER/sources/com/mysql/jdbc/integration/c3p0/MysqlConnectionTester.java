package com.mysql.jdbc.integration.c3p0;

import com.mchange.v2.c3p0.QueryConnectionTester;
import com.mysql.jdbc.CommunicationsException;
import com.mysql.jdbc.Connection;
import java.lang.reflect.Method;
import java.sql.SQLException;

public final class MysqlConnectionTester implements QueryConnectionTester {
    private static final Object[] NO_ARGS_ARRAY = new Object[0];
    private static final long serialVersionUID = 3256444690067896368L;
    private transient Method pingMethod;

    public MysqlConnectionTester() {
        try {
            Class[] clsArr = null;
            this.pingMethod = Connection.class.getMethod("ping", (Class[]) null);
        } catch (Exception e) {
        }
    }

    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int activeCheckConnection(java.sql.Connection r5) {
        /*
            r4 = this;
            java.lang.reflect.Method r0 = r4.pingMethod     // Catch:{ Exception -> 0x0039 }
            if (r0 == 0) goto L_0x001b
            boolean r1 = r5 instanceof com.mysql.jdbc.Connection     // Catch:{ Exception -> 0x0039 }
            if (r1 == 0) goto L_0x000f
            r0 = r5
            com.mysql.jdbc.Connection r0 = (com.mysql.jdbc.Connection) r0     // Catch:{ Exception -> 0x0039 }
            r0.ping()     // Catch:{ Exception -> 0x0039 }
            goto L_0x002f
        L_0x000f:
            r1 = r5
            com.mchange.v2.c3p0.C3P0ProxyConnection r1 = (com.mchange.v2.c3p0.C3P0ProxyConnection) r1     // Catch:{ Exception -> 0x0039 }
            java.lang.Object r2 = com.mchange.v2.c3p0.C3P0ProxyConnection.RAW_CONNECTION     // Catch:{ Exception -> 0x0039 }
            java.lang.Object[] r3 = NO_ARGS_ARRAY     // Catch:{ Exception -> 0x0039 }
            r1.rawConnectionOperation(r0, r2, r3)     // Catch:{ Exception -> 0x0039 }
            goto L_0x002f
        L_0x001b:
            r0 = 0
            java.sql.Statement r1 = r5.createStatement()     // Catch:{ all -> 0x0031 }
            r0 = r1
            java.lang.String r1 = "SELECT 1"
            java.sql.ResultSet r1 = r0.executeQuery(r1)     // Catch:{ all -> 0x0031 }
            r1.close()     // Catch:{ all -> 0x0031 }
            if (r0 == 0) goto L_0x002f
            r0.close()     // Catch:{ Exception -> 0x0039 }
        L_0x002f:
            r0 = 0
            return r0
        L_0x0031:
            r1 = move-exception
            if (r0 == 0) goto L_0x0037
            r0.close()     // Catch:{ Exception -> 0x0039 }
        L_0x0037:
            throw r1     // Catch:{ Exception -> 0x0039 }
        L_0x0039:
            r0 = move-exception
            r1 = -1
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.integration.c3p0.MysqlConnectionTester.activeCheckConnection(java.sql.Connection):int");
    }

    public int statusOnException(java.sql.Connection arg0, Throwable throwable) {
        if ((throwable instanceof CommunicationsException) || "com.mysql.jdbc.exceptions.jdbc4.CommunicationsException".equals(throwable.getClass().getName()) || !(throwable instanceof SQLException)) {
            return -1;
        }
        String sqlState = ((SQLException) throwable).getSQLState();
        if (sqlState == null || !sqlState.startsWith("08")) {
            return 0;
        }
        return -1;
    }

    public int activeCheckConnection(java.sql.Connection arg0, String arg1) {
        return 0;
    }
}
