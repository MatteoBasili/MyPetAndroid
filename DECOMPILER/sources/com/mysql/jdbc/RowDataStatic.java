package com.mysql.jdbc;

import java.sql.SQLException;
import java.util.List;

public class RowDataStatic implements RowData {
    private int index = -1;
    private Field[] metadata;
    ResultSetImpl owner;
    private List<ResultSetRow> rows;

    public RowDataStatic(List<ResultSetRow> rows2) {
        this.rows = rows2;
    }

    public void addRow(ResultSetRow row) {
        this.rows.add(row);
    }

    public void afterLast() {
        if (this.rows.size() > 0) {
            this.index = this.rows.size();
        }
    }

    public void beforeFirst() {
        if (this.rows.size() > 0) {
            this.index = -1;
        }
    }

    public void beforeLast() {
        if (this.rows.size() > 0) {
            this.index = this.rows.size() - 2;
        }
    }

    public void close() {
    }

    public ResultSetRow getAt(int atIndex) throws SQLException {
        if (atIndex < 0 || atIndex >= this.rows.size()) {
            return null;
        }
        return this.rows.get(atIndex).setMetadata(this.metadata);
    }

    public int getCurrentRowNumber() {
        return this.index;
    }

    public ResultSetInternalMethods getOwner() {
        return this.owner;
    }

    public boolean hasNext() {
        boolean hasMore = true;
        if (this.index + 1 >= this.rows.size()) {
            hasMore = false;
        }
        return hasMore;
    }

    public boolean isAfterLast() {
        return this.index >= this.rows.size() && this.rows.size() != 0;
    }

    public boolean isBeforeFirst() {
        return this.index == -1 && this.rows.size() != 0;
    }

    public boolean isDynamic() {
        return false;
    }

    public boolean isEmpty() {
        return this.rows.size() == 0;
    }

    public boolean isFirst() {
        return this.index == 0;
    }

    public boolean isLast() {
        if (this.rows.size() != 0 && this.index == this.rows.size() - 1) {
            return true;
        }
        return false;
    }

    public void moveRowRelative(int rowsToMove) {
        if (this.rows.size() > 0) {
            int i = this.index + rowsToMove;
            this.index = i;
            if (i < -1) {
                beforeFirst();
            } else if (i > this.rows.size()) {
                afterLast();
            }
        }
    }

    public ResultSetRow next() throws SQLException {
        int i = this.index + 1;
        this.index = i;
        if (i > this.rows.size()) {
            afterLast();
            return null;
        } else if (this.index < this.rows.size()) {
            return this.rows.get(this.index).setMetadata(this.metadata);
        } else {
            return null;
        }
    }

    public void removeRow(int atIndex) {
        this.rows.remove(atIndex);
    }

    public void setCurrentRow(int newIndex) {
        this.index = newIndex;
    }

    public void setOwner(ResultSetImpl rs) {
        this.owner = rs;
    }

    public int size() {
        return this.rows.size();
    }

    public boolean wasEmpty() {
        List<ResultSetRow> list = this.rows;
        return list != null && list.size() == 0;
    }

    public void setMetadata(Field[] metadata2) {
        this.metadata = metadata2;
    }
}
