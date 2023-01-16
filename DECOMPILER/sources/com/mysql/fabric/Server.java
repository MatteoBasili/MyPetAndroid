package com.mysql.fabric;

public class Server implements Comparable<Server> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private String groupName;
    private String hostname;
    private ServerMode mode;
    private int port;
    private ServerRole role;
    private String uuid;
    private double weight;

    public Server(String groupName2, String uuid2, String hostname2, int port2, ServerMode mode2, ServerRole role2, double weight2) {
        this.groupName = groupName2;
        this.uuid = uuid2;
        this.hostname = hostname2;
        this.port = port2;
        this.mode = mode2;
        this.role = role2;
        this.weight = weight2;
        if (uuid2 == null || "".equals(uuid2)) {
            throw new AssertionError();
        } else if (hostname2 == null || "".equals(hostname2)) {
            throw new AssertionError();
        } else if (port2 <= 0) {
            throw new AssertionError();
        } else if (mode2 == null) {
            throw new AssertionError();
        } else if (role2 == null) {
            throw new AssertionError();
        } else if (weight2 <= 0.0d) {
            throw new AssertionError();
        }
    }

    public String getGroupName() {
        return this.groupName;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getHostname() {
        return this.hostname;
    }

    public int getPort() {
        return this.port;
    }

    public ServerMode getMode() {
        return this.mode;
    }

    public ServerRole getRole() {
        return this.role;
    }

    public double getWeight() {
        return this.weight;
    }

    public String getHostPortString() {
        return this.hostname + ":" + this.port;
    }

    public boolean isMaster() {
        return this.role == ServerRole.PRIMARY;
    }

    public boolean isSlave() {
        return this.role == ServerRole.SECONDARY || this.role == ServerRole.SPARE;
    }

    public String toString() {
        return String.format("Server[%s, %s:%d, %s, %s, weight=%s]", new Object[]{this.uuid, this.hostname, Integer.valueOf(this.port), this.mode, this.role, Double.valueOf(this.weight)});
    }

    public boolean equals(Object o) {
        if (!(o instanceof Server)) {
            return false;
        }
        return ((Server) o).getUuid().equals(getUuid());
    }

    public int hashCode() {
        return getUuid().hashCode();
    }

    public int compareTo(Server other) {
        return getUuid().compareTo(other.getUuid());
    }
}
