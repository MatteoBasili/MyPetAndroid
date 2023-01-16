package com.mysql.jdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SequentialBalanceStrategy implements BalanceStrategy {
    private int currentHostIndex = -1;

    public void destroy() {
    }

    public void init(Connection conn, Properties props) throws SQLException {
    }

    public ConnectionImpl pickConnection(LoadBalancedConnectionProxy proxy, List<String> configuredHosts, Map<String, ConnectionImpl> liveConnections, long[] responseTimes, int numRetries) throws SQLException {
        boolean foundGoodHost;
        LoadBalancedConnectionProxy loadBalancedConnectionProxy = proxy;
        List<String> list = configuredHosts;
        int numHosts = configuredHosts.size();
        int attempts = 0;
        Map<String, Long> blackList = proxy.getGlobalBlacklist();
        SQLException ex = null;
        while (attempts < numRetries) {
            if (numHosts == 1) {
                this.currentHostIndex = 0;
            } else {
                int i = this.currentHostIndex;
                if (i == -1) {
                    int random = (int) Math.floor(Math.random() * ((double) numHosts));
                    int i2 = random;
                    while (true) {
                        if (i2 >= numHosts) {
                            break;
                        } else if (!blackList.containsKey(list.get(i2))) {
                            this.currentHostIndex = i2;
                            break;
                        } else {
                            i2++;
                        }
                    }
                    if (this.currentHostIndex == -1) {
                        int i3 = 0;
                        while (true) {
                            if (i3 >= random) {
                                break;
                            } else if (!blackList.containsKey(list.get(i3))) {
                                this.currentHostIndex = i3;
                                break;
                            } else {
                                i3++;
                            }
                        }
                    }
                    if (this.currentHostIndex == -1) {
                        blackList = proxy.getGlobalBlacklist();
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                        }
                    }
                } else {
                    int i4 = i + 1;
                    boolean foundGoodHost2 = false;
                    while (true) {
                        if (i4 >= numHosts) {
                            break;
                        } else if (!blackList.containsKey(list.get(i4))) {
                            this.currentHostIndex = i4;
                            foundGoodHost2 = true;
                            break;
                        } else {
                            i4++;
                        }
                    }
                    if (!foundGoodHost2) {
                        int i5 = 0;
                        while (true) {
                            if (i5 >= this.currentHostIndex) {
                                foundGoodHost = foundGoodHost2;
                                int i6 = i5;
                                break;
                            } else if (!blackList.containsKey(list.get(i5))) {
                                this.currentHostIndex = i5;
                                foundGoodHost = true;
                                int i7 = i5;
                                break;
                            } else {
                                i5++;
                            }
                        }
                    } else {
                        foundGoodHost = foundGoodHost2;
                        int i8 = i4;
                    }
                    if (!foundGoodHost) {
                        blackList = proxy.getGlobalBlacklist();
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e2) {
                        }
                    }
                }
                Map<String, ConnectionImpl> map = liveConnections;
                attempts++;
            }
            String hostPortSpec = list.get(this.currentHostIndex);
            ConnectionImpl conn = liveConnections.get(hostPortSpec);
            if (conn != null) {
                return conn;
            }
            try {
                return loadBalancedConnectionProxy.createConnectionForHost(hostPortSpec);
            } catch (SQLException e3) {
                SQLException sqlEx = e3;
                ex = sqlEx;
                if (loadBalancedConnectionProxy.shouldExceptionTriggerConnectionSwitch(sqlEx)) {
                    loadBalancedConnectionProxy.addToGlobalBlacklist(hostPortSpec);
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e4) {
                    }
                } else {
                    throw sqlEx;
                }
            }
        }
        Map<String, ConnectionImpl> map2 = liveConnections;
        if (ex == null) {
            return null;
        }
        throw ex;
    }
}
