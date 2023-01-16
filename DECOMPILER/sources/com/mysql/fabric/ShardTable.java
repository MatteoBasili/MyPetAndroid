package com.mysql.fabric;

public class ShardTable {
    private String column;
    private String database;
    private String table;

    public ShardTable(String database2, String table2, String column2) {
        this.database = database2;
        this.table = table2;
        this.column = column2;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getTable() {
        return this.table;
    }

    public String getColumn() {
        return this.column;
    }
}
