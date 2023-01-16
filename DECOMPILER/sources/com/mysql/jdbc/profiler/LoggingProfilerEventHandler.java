package com.mysql.jdbc.profiler;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Constants;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.log.Log;
import java.sql.SQLException;
import java.util.Properties;

public class LoggingProfilerEventHandler implements ProfilerEventHandler {
    private Log log;

    public void consumeEvent(ProfilerEvent evt) {
        switch (evt.getEventType()) {
            case 0:
                this.log.logWarn(evt);
                return;
            default:
                this.log.logInfo(evt);
                return;
        }
    }

    public void destroy() {
        this.log = null;
    }

    public void init(Connection conn, Properties props) throws SQLException {
        this.log = conn.getLog();
    }

    public void processEvent(byte eventType, MySQLConnection conn, Statement stmt, ResultSetInternalMethods resultSet, long eventDuration, Throwable eventCreationPoint, String message) {
        String catalog = "";
        if (conn != null) {
            try {
                catalog = conn.getCatalog();
            } catch (SQLException e) {
            }
        }
        String host = conn == null ? "" : conn.getHost();
        long id = conn == null ? -1 : conn.getId();
        int i = -1;
        int id2 = stmt == null ? -1 : stmt.getId();
        if (resultSet != null) {
            i = resultSet.getId();
        }
        int i2 = i;
        consumeEvent(new ProfilerEvent(eventType, host, catalog, id, id2, i2, eventDuration, conn == null ? Constants.MILLIS_I18N : conn.getQueryTimingUnits(), eventCreationPoint, message));
    }
}
