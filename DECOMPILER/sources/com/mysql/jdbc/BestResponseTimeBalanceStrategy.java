package com.mysql.jdbc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BestResponseTimeBalanceStrategy implements BalanceStrategy {
    public void destroy() {
    }

    public void init(Connection conn, Properties props) throws SQLException {
    }

    public ConnectionImpl pickConnection(LoadBalancedConnectionProxy proxy, List<String> configuredHosts, Map<String, ConnectionImpl> liveConnections, long[] responseTimes, int numRetries) throws SQLException {
        Map<String, Long> blackList;
        LoadBalancedConnectionProxy loadBalancedConnectionProxy = proxy;
        long[] jArr = responseTimes;
        List<String> whiteList = new ArrayList<>(configuredHosts.size());
        whiteList.addAll(configuredHosts);
        Map<String, Long> blackList2 = proxy.getGlobalBlacklist();
        whiteList.removeAll(blackList2.keySet());
        SQLException ex = null;
        int attempts = 0;
        while (attempts < numRetries) {
            long minResponseTime = Long.MAX_VALUE;
            int bestHostIndex = 0;
            if (blackList2.size() == configuredHosts.size()) {
                blackList = proxy.getGlobalBlacklist();
            } else {
                blackList = blackList2;
            }
            int i = 0;
            while (true) {
                if (i >= jArr.length) {
                    break;
                }
                long candidateResponseTime = jArr[i];
                if (candidateResponseTime < minResponseTime && !blackList.containsKey(whiteList.get(i))) {
                    if (candidateResponseTime == 0) {
                        bestHostIndex = i;
                        break;
                    }
                    bestHostIndex = i;
                    minResponseTime = candidateResponseTime;
                }
                i++;
            }
            String bestHost = whiteList.get(bestHostIndex);
            ConnectionImpl conn = liveConnections.get(bestHost);
            if (conn != null) {
                return conn;
            }
            try {
                return loadBalancedConnectionProxy.createConnectionForHost(bestHost);
            } catch (SQLException e) {
                SQLException sqlEx = e;
                ex = sqlEx;
                SQLException sqlEx2 = sqlEx;
                if (loadBalancedConnectionProxy.shouldExceptionTriggerConnectionSwitch(sqlEx2)) {
                    loadBalancedConnectionProxy.addToGlobalBlacklist(bestHost);
                    blackList.put(bestHost, (Object) null);
                    if (blackList.size() == configuredHosts.size()) {
                        attempts++;
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e2) {
                        }
                        blackList2 = proxy.getGlobalBlacklist();
                    } else {
                        blackList2 = blackList;
                    }
                    loadBalancedConnectionProxy = proxy;
                } else {
                    throw sqlEx2;
                }
            }
        }
        Map<String, ConnectionImpl> map = liveConnections;
        if (ex == null) {
            return null;
        }
        throw ex;
    }
}
