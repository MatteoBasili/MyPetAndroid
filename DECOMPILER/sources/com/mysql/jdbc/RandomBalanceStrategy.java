package com.mysql.jdbc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class RandomBalanceStrategy implements BalanceStrategy {
    public void destroy() {
    }

    public void init(Connection conn, Properties props) throws SQLException {
    }

    public ConnectionImpl pickConnection(LoadBalancedConnectionProxy proxy, List<String> configuredHosts, Map<String, ConnectionImpl> liveConnections, long[] responseTimes, int numRetries) throws SQLException {
        LoadBalancedConnectionProxy loadBalancedConnectionProxy = proxy;
        List<String> list = configuredHosts;
        int numHosts = configuredHosts.size();
        List<String> whiteList = new ArrayList<>(numHosts);
        whiteList.addAll(list);
        Map<String, Long> blackList = proxy.getGlobalBlacklist();
        whiteList.removeAll(blackList.keySet());
        int attempts = 0;
        Map<String, Integer> whiteListMap = getArrayIndexMap(whiteList);
        Map<String, Long> map = blackList;
        SQLException ex = null;
        while (attempts < numRetries) {
            int random = (int) Math.floor(Math.random() * ((double) whiteList.size()));
            if (whiteList.size() != 0) {
                String hostPortSpec = whiteList.get(random);
                ConnectionImpl conn = liveConnections.get(hostPortSpec);
                if (conn != null) {
                    return conn;
                }
                try {
                    return loadBalancedConnectionProxy.createConnectionForHost(hostPortSpec);
                } catch (SQLException e) {
                    SQLException sqlEx = e;
                    ex = sqlEx;
                    if (loadBalancedConnectionProxy.shouldExceptionTriggerConnectionSwitch(sqlEx)) {
                        Integer whiteListIndex = whiteListMap.get(hostPortSpec);
                        if (whiteListIndex != null) {
                            whiteList.remove(whiteListIndex.intValue());
                            whiteListMap = getArrayIndexMap(whiteList);
                        }
                        loadBalancedConnectionProxy.addToGlobalBlacklist(hostPortSpec);
                        if (whiteList.size() == 0) {
                            attempts++;
                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException e2) {
                            }
                            new HashMap(numHosts);
                            whiteList.addAll(list);
                            whiteList.removeAll(proxy.getGlobalBlacklist().keySet());
                            whiteListMap = getArrayIndexMap(whiteList);
                        }
                    } else {
                        throw sqlEx;
                    }
                }
            } else {
                Map<String, ConnectionImpl> map2 = liveConnections;
                throw SQLError.createSQLException("No hosts configured", (ExceptionInterceptor) null);
            }
        }
        Map<String, ConnectionImpl> map3 = liveConnections;
        if (ex == null) {
            return null;
        }
        throw ex;
    }

    private Map<String, Integer> getArrayIndexMap(List<String> l) {
        Map<String, Integer> m = new HashMap<>(l.size());
        for (int i = 0; i < l.size(); i++) {
            m.put(l.get(i), Integer.valueOf(i));
        }
        return m;
    }
}
