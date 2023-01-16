package com.mysql.jdbc;

import com.mysql.jdbc.jmx.LoadBalanceConnectionGroupManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConnectionGroupManager {
    private static HashMap<String, ConnectionGroup> GROUP_MAP = new HashMap<>();
    private static boolean hasRegisteredJmx = false;
    private static LoadBalanceConnectionGroupManager mbean = new LoadBalanceConnectionGroupManager();

    public static synchronized ConnectionGroup getConnectionGroupInstance(String groupName) {
        synchronized (ConnectionGroupManager.class) {
            if (GROUP_MAP.containsKey(groupName)) {
                ConnectionGroup connectionGroup = GROUP_MAP.get(groupName);
                return connectionGroup;
            }
            ConnectionGroup group = new ConnectionGroup(groupName);
            GROUP_MAP.put(groupName, group);
            return group;
        }
    }

    public static void registerJmx() throws SQLException {
        if (!hasRegisteredJmx) {
            mbean.registerJmx();
            hasRegisteredJmx = true;
        }
    }

    public static ConnectionGroup getConnectionGroup(String groupName) {
        return GROUP_MAP.get(groupName);
    }

    private static Collection<ConnectionGroup> getGroupsMatching(String group) {
        if (group == null || group.equals("")) {
            Set<ConnectionGroup> s = new HashSet<>();
            s.addAll(GROUP_MAP.values());
            return s;
        }
        Set<ConnectionGroup> s2 = new HashSet<>();
        ConnectionGroup o = GROUP_MAP.get(group);
        if (o != null) {
            s2.add(o);
        }
        return s2;
    }

    public static void addHost(String group, String hostPortPair, boolean forExisting) {
        for (ConnectionGroup cg : getGroupsMatching(group)) {
            cg.addHost(hostPortPair, forExisting);
        }
    }

    public static int getActiveHostCount(String group) {
        Set<String> active = new HashSet<>();
        for (ConnectionGroup cg : getGroupsMatching(group)) {
            active.addAll(cg.getInitialHosts());
        }
        return active.size();
    }

    public static long getActiveLogicalConnectionCount(String group) {
        int count = 0;
        for (ConnectionGroup cg : getGroupsMatching(group)) {
            count = (int) (((long) count) + cg.getActiveLogicalConnectionCount());
        }
        return (long) count;
    }

    public static long getActivePhysicalConnectionCount(String group) {
        int count = 0;
        for (ConnectionGroup cg : getGroupsMatching(group)) {
            count = (int) (((long) count) + cg.getActivePhysicalConnectionCount());
        }
        return (long) count;
    }

    public static int getTotalHostCount(String group) {
        Collection<ConnectionGroup> s = getGroupsMatching(group);
        Set<String> hosts = new HashSet<>();
        for (ConnectionGroup cg : s) {
            hosts.addAll(cg.getInitialHosts());
            hosts.addAll(cg.getClosedHosts());
        }
        return hosts.size();
    }

    public static long getTotalLogicalConnectionCount(String group) {
        long count = 0;
        for (ConnectionGroup cg : getGroupsMatching(group)) {
            count += cg.getTotalLogicalConnectionCount();
        }
        return count;
    }

    public static long getTotalPhysicalConnectionCount(String group) {
        long count = 0;
        for (ConnectionGroup cg : getGroupsMatching(group)) {
            count += cg.getTotalPhysicalConnectionCount();
        }
        return count;
    }

    public static long getTotalTransactionCount(String group) {
        long count = 0;
        for (ConnectionGroup cg : getGroupsMatching(group)) {
            count += cg.getTotalTransactionCount();
        }
        return count;
    }

    public static void removeHost(String group, String hostPortPair) throws SQLException {
        removeHost(group, hostPortPair, false);
    }

    public static void removeHost(String group, String host, boolean removeExisting) throws SQLException {
        for (ConnectionGroup cg : getGroupsMatching(group)) {
            cg.removeHost(host, removeExisting);
        }
    }

    public static String getActiveHostLists(String group) {
        int o;
        Collection<ConnectionGroup> s = getGroupsMatching(group);
        Map<String, Integer> hosts = new HashMap<>();
        for (ConnectionGroup cg : s) {
            for (String host : cg.getInitialHosts()) {
                Integer o2 = hosts.get(host);
                if (o2 == null) {
                    o = 1;
                } else {
                    o = Integer.valueOf(o2.intValue() + 1);
                }
                hosts.put(host, o);
            }
        }
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (String host2 : hosts.keySet()) {
            sb.append(sep);
            sb.append(host2);
            sb.append('(');
            sb.append(hosts.get(host2));
            sb.append(')');
            sep = ",";
        }
        return sb.toString();
    }

    public static String getRegisteredConnectionGroups() {
        Collection<ConnectionGroup> s = getGroupsMatching((String) null);
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (ConnectionGroup cg : s) {
            String group = cg.getGroupName();
            sb.append(sep);
            sb.append(group);
            sep = ",";
        }
        return sb.toString();
    }
}
