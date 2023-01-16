package com.mysql.jdbc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class StandardLoadBalanceExceptionChecker implements LoadBalanceExceptionChecker {
    private List<Class<?>> sqlExClassList;
    private List<String> sqlStateList;

    public boolean shouldExceptionTriggerFailover(SQLException ex) {
        String sqlState = ex.getSQLState();
        if (sqlState != null) {
            if (sqlState.startsWith("08")) {
                return true;
            }
            List<String> list = this.sqlStateList;
            if (list != null) {
                for (String str : list) {
                    if (sqlState.startsWith(str.toString())) {
                        return true;
                    }
                }
            }
        }
        if (ex instanceof CommunicationsException) {
            return true;
        }
        List<Class<?>> list2 = this.sqlExClassList;
        if (list2 == null) {
            return false;
        }
        for (Class<?> isInstance : list2) {
            if (isInstance.isInstance(ex)) {
                return true;
            }
        }
        return false;
    }

    public void destroy() {
    }

    public void init(Connection conn, Properties props) throws SQLException {
        configureSQLStateList(props.getProperty("loadBalanceSQLStateFailover", (String) null));
        configureSQLExceptionSubclassList(props.getProperty("loadBalanceSQLExceptionSubclassFailover", (String) null));
    }

    private void configureSQLStateList(String sqlStates) {
        if (sqlStates != null && !"".equals(sqlStates)) {
            List<String> states = StringUtils.split(sqlStates, ",", true);
            List<String> newStates = new ArrayList<>();
            for (String state : states) {
                if (state.length() > 0) {
                    newStates.add(state);
                }
            }
            if (newStates.size() > 0) {
                this.sqlStateList = newStates;
            }
        }
    }

    private void configureSQLExceptionSubclassList(String sqlExClasses) {
        if (sqlExClasses != null && !"".equals(sqlExClasses)) {
            List<String> classes = StringUtils.split(sqlExClasses, ",", true);
            List<Class<?>> newClasses = new ArrayList<>();
            for (String exClass : classes) {
                try {
                    newClasses.add(Class.forName(exClass));
                } catch (Exception e) {
                }
            }
            if (newClasses.size() > 0) {
                this.sqlExClassList = newClasses;
            }
        }
    }
}
