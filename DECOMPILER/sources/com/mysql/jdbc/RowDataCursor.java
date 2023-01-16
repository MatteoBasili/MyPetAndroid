package com.mysql.jdbc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RowDataCursor implements RowData {
    private static final int BEFORE_START_OF_ROWS = -1;
    private static final int SERVER_STATUS_LAST_ROW_SENT = 128;
    private int currentPositionInEntireResult;
    private int currentPositionInFetchedRows;
    private List<ResultSetRow> fetchedRows;
    private boolean firstFetchCompleted;
    private boolean lastRowFetched;
    private Field[] metadata;
    private MysqlIO mysql;
    private ResultSetImpl owner;
    private ServerPreparedStatement prepStmt;
    private long statementIdOnServer;
    private boolean useBufferRowExplicit;
    private boolean wasEmpty;

    public RowDataCursor(MysqlIO ioChannel, ServerPreparedStatement creatingStatement, Field[] metadata2) {
        this.currentPositionInEntireResult = -1;
        this.currentPositionInFetchedRows = -1;
        this.lastRowFetched = false;
        this.firstFetchCompleted = false;
        this.wasEmpty = false;
        this.useBufferRowExplicit = false;
        this.currentPositionInEntireResult = -1;
        this.metadata = metadata2;
        this.mysql = ioChannel;
        this.statementIdOnServer = creatingStatement.getServerStatementId();
        this.prepStmt = creatingStatement;
        this.useBufferRowExplicit = MysqlIO.useBufferRowExplicit(this.metadata);
    }

    public boolean isAfterLast() {
        return this.lastRowFetched && this.currentPositionInFetchedRows > this.fetchedRows.size();
    }

    public ResultSetRow getAt(int ind) throws SQLException {
        notSupported();
        return null;
    }

    public boolean isBeforeFirst() throws SQLException {
        return this.currentPositionInEntireResult < 0;
    }

    public void setCurrentRow(int rowNumber) throws SQLException {
        notSupported();
    }

    public int getCurrentRowNumber() throws SQLException {
        return this.currentPositionInEntireResult + 1;
    }

    public boolean isDynamic() {
        return true;
    }

    public boolean isEmpty() throws SQLException {
        return isBeforeFirst() && isAfterLast();
    }

    public boolean isFirst() throws SQLException {
        return this.currentPositionInEntireResult == 0;
    }

    public boolean isLast() throws SQLException {
        return this.lastRowFetched && this.currentPositionInFetchedRows == this.fetchedRows.size() - 1;
    }

    public void addRow(ResultSetRow row) throws SQLException {
        notSupported();
    }

    public void afterLast() throws SQLException {
        notSupported();
    }

    public void beforeFirst() throws SQLException {
        notSupported();
    }

    public void beforeLast() throws SQLException {
        notSupported();
    }

    public void close() throws SQLException {
        this.metadata = null;
        this.owner = null;
    }

    public boolean hasNext() throws SQLException {
        int maxRows;
        List<ResultSetRow> list = this.fetchedRows;
        if (list != null && list.size() == 0) {
            return false;
        }
        ResultSetImpl resultSetImpl = this.owner;
        if (resultSetImpl != null && resultSetImpl.owningStatement != null && (maxRows = this.owner.owningStatement.maxRows) != -1 && this.currentPositionInEntireResult + 1 > maxRows) {
            return false;
        }
        if (this.currentPositionInEntireResult == -1) {
            fetchMoreRows();
            if (this.fetchedRows.size() > 0) {
                return true;
            }
            return false;
        } else if (this.currentPositionInFetchedRows < this.fetchedRows.size() - 1) {
            return true;
        } else {
            if (this.currentPositionInFetchedRows == this.fetchedRows.size() && this.lastRowFetched) {
                return false;
            }
            fetchMoreRows();
            if (this.fetchedRows.size() > 0) {
                return true;
            }
            return false;
        }
    }

    public void moveRowRelative(int rows) throws SQLException {
        notSupported();
    }

    public ResultSetRow next() throws SQLException {
        if (this.fetchedRows == null && this.currentPositionInEntireResult != -1) {
            throw SQLError.createSQLException(Messages.getString("ResultSet.Operation_not_allowed_after_ResultSet_closed_144"), SQLError.SQL_STATE_GENERAL_ERROR, this.mysql.getExceptionInterceptor());
        } else if (!hasNext()) {
            return null;
        } else {
            this.currentPositionInEntireResult++;
            this.currentPositionInFetchedRows++;
            List<ResultSetRow> list = this.fetchedRows;
            if (list != null && list.size() == 0) {
                return null;
            }
            List<ResultSetRow> list2 = this.fetchedRows;
            if (list2 == null || this.currentPositionInFetchedRows > list2.size() - 1) {
                fetchMoreRows();
                this.currentPositionInFetchedRows = 0;
            }
            ResultSetRow row = this.fetchedRows.get(this.currentPositionInFetchedRows);
            row.setMetadata(this.metadata);
            return row;
        }
    }

    private void fetchMoreRows() throws SQLException {
        if (this.lastRowFetched) {
            this.fetchedRows = new ArrayList(0);
            return;
        }
        synchronized (this.owner.connection.getConnectionMutex()) {
            boolean z = this.firstFetchCompleted;
            boolean oldFirstFetchCompleted = z;
            if (!z) {
                this.firstFetchCompleted = true;
            }
            int numRowsToFetch = this.owner.getFetchSize();
            if (numRowsToFetch == 0) {
                numRowsToFetch = this.prepStmt.getFetchSize();
            }
            if (numRowsToFetch == Integer.MIN_VALUE) {
                numRowsToFetch = 1;
            }
            this.fetchedRows = this.mysql.fetchRowsViaCursor(this.fetchedRows, this.statementIdOnServer, this.metadata, numRowsToFetch, this.useBufferRowExplicit);
            this.currentPositionInFetchedRows = -1;
            if ((this.mysql.getServerStatus() & 128) != 0) {
                this.lastRowFetched = true;
                if (!oldFirstFetchCompleted && this.fetchedRows.size() == 0) {
                    this.wasEmpty = true;
                }
            }
        }
    }

    public void removeRow(int ind) throws SQLException {
        notSupported();
    }

    public int size() {
        return -1;
    }

    /* access modifiers changed from: protected */
    public void nextRecord() throws SQLException {
    }

    private void notSupported() throws SQLException {
        throw new OperationNotSupportedException();
    }

    public void setOwner(ResultSetImpl rs) {
        this.owner = rs;
    }

    public ResultSetInternalMethods getOwner() {
        return this.owner;
    }

    public boolean wasEmpty() {
        return this.wasEmpty;
    }

    public void setMetadata(Field[] metadata2) {
        this.metadata = metadata2;
    }
}
