package com.mysql.jdbc;

import java.sql.SQLException;

public class RowDataDynamic implements RowData {
    private int columnCount;
    private ExceptionInterceptor exceptionInterceptor;
    private int index = -1;

    /* renamed from: io  reason: collision with root package name */
    private MysqlIO f5io;
    private boolean isAfterEnd = false;
    private boolean isBinaryEncoded = false;
    private Field[] metadata;
    private boolean moreResultsExisted;
    private ResultSetRow nextRow;
    private boolean noMoreRows = false;
    private ResultSetImpl owner;
    private boolean streamerClosed = false;
    private boolean useBufferRowExplicit;
    private boolean wasEmpty = false;

    public RowDataDynamic(MysqlIO io2, int colCount, Field[] fields, boolean isBinaryEncoded2) throws SQLException {
        this.f5io = io2;
        this.columnCount = colCount;
        this.isBinaryEncoded = isBinaryEncoded2;
        this.metadata = fields;
        this.exceptionInterceptor = io2.getExceptionInterceptor();
        this.useBufferRowExplicit = MysqlIO.useBufferRowExplicit(this.metadata);
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

    /*  JADX ERROR: NullPointerException in pass: CodeShrinkVisitor
        java.lang.NullPointerException
        */
    public void close() throws java.sql.SQLException {
        /*
            r17 = this;
            r1 = r17
            r0 = r17
            r2 = 0
            com.mysql.jdbc.ResultSetImpl r3 = r1.owner
            if (r3 == 0) goto L_0x0017
            com.mysql.jdbc.MySQLConnection r2 = r3.connection
            if (r2 == 0) goto L_0x0014
            java.lang.Object r0 = r2.getConnectionMutex()
            r3 = r2
            r2 = r0
            goto L_0x0019
        L_0x0014:
            r3 = r2
            r2 = r0
            goto L_0x0019
        L_0x0017:
            r3 = r2
            r2 = r0
        L_0x0019:
            r0 = 0
            r4 = 0
            monitor-enter(r2)
            r5 = r4
            r4 = r0
        L_0x001e:
            com.mysql.jdbc.ResultSetRow r0 = r17.next()     // Catch:{ all -> 0x00c5 }
            if (r0 == 0) goto L_0x002f
            r4 = 1
            int r5 = r5 + 1
            int r0 = r5 % 100
            if (r0 != 0) goto L_0x001e
            java.lang.Thread.yield()     // Catch:{ all -> 0x00c5 }
            goto L_0x001e
        L_0x002f:
            if (r3 == 0) goto L_0x00be
            boolean r0 = r3.getClobberStreamingResults()     // Catch:{ all -> 0x00c5 }
            if (r0 != 0) goto L_0x0086
            int r0 = r3.getNetTimeoutForStreamingResults()     // Catch:{ all -> 0x00c5 }
            if (r0 <= 0) goto L_0x0086
            java.lang.String r0 = "net_write_timeout"
            java.lang.String r0 = r3.getServerVariable(r0)     // Catch:{ all -> 0x00c5 }
            if (r0 == 0) goto L_0x004e
            int r6 = r0.length()     // Catch:{ all -> 0x00c5 }
            if (r6 != 0) goto L_0x004c
            goto L_0x004e
        L_0x004c:
            r6 = r0
            goto L_0x0052
        L_0x004e:
            java.lang.String r6 = "60"
            r0 = r6
            r6 = r0
        L_0x0052:
            com.mysql.jdbc.MysqlIO r0 = r1.f5io     // Catch:{ all -> 0x00c5 }
            r0.clearInputStream()     // Catch:{ all -> 0x00c5 }
            r7 = 0
            java.sql.Statement r0 = r3.createStatement()     // Catch:{ all -> 0x007e }
            r7 = r0
            r0 = r7
            com.mysql.jdbc.StatementImpl r0 = (com.mysql.jdbc.StatementImpl) r0     // Catch:{ all -> 0x007e }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x007e }
            r8.<init>()     // Catch:{ all -> 0x007e }
            java.lang.String r9 = "SET net_write_timeout="
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ all -> 0x007e }
            java.lang.StringBuilder r8 = r8.append(r6)     // Catch:{ all -> 0x007e }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x007e }
            r0.executeSimpleNonQuery(r3, r8)     // Catch:{ all -> 0x007e }
            if (r7 == 0) goto L_0x007c
            r7.close()     // Catch:{ all -> 0x00c5 }
        L_0x007c:
            goto L_0x0086
        L_0x007e:
            r0 = move-exception
            if (r7 == 0) goto L_0x0084
            r7.close()     // Catch:{ all -> 0x00c5 }
        L_0x0084:
            throw r0     // Catch:{ all -> 0x00c5 }
        L_0x0086:
            boolean r0 = r3.getUseUsageAdvisor()     // Catch:{ all -> 0x00c5 }
            if (r0 == 0) goto L_0x00be
            if (r4 == 0) goto L_0x00be
            com.mysql.jdbc.ResultSetImpl r0 = r1.owner     // Catch:{ all -> 0x00c5 }
            com.mysql.jdbc.MySQLConnection r0 = r0.connection     // Catch:{ all -> 0x00c5 }
            com.mysql.jdbc.profiler.ProfilerEventHandler r6 = r0.getProfilerEventHandlerInstance()     // Catch:{ all -> 0x00c5 }
            r7 = 0
            com.mysql.jdbc.ResultSetImpl r0 = r1.owner     // Catch:{ all -> 0x00c5 }
            com.mysql.jdbc.MySQLConnection r8 = r0.connection     // Catch:{ all -> 0x00c5 }
            com.mysql.jdbc.ResultSetImpl r0 = r1.owner     // Catch:{ all -> 0x00c5 }
            com.mysql.jdbc.StatementImpl r9 = r0.owningStatement     // Catch:{ all -> 0x00c5 }
            r10 = 0
            r11 = 0
            r13 = 0
            java.lang.String r0 = "RowDataDynamic.1"
            r14 = 2
            java.lang.String[] r14 = new java.lang.String[r14]     // Catch:{ all -> 0x00c5 }
            r15 = 0
            java.lang.String r16 = java.lang.String.valueOf(r5)     // Catch:{ all -> 0x00c5 }
            r14[r15] = r16     // Catch:{ all -> 0x00c5 }
            com.mysql.jdbc.ResultSetImpl r15 = r1.owner     // Catch:{ all -> 0x00c5 }
            java.lang.String r15 = r15.pointOfOrigin     // Catch:{ all -> 0x00c5 }
            r16 = 1
            r14[r16] = r15     // Catch:{ all -> 0x00c5 }
            java.lang.String r14 = com.mysql.jdbc.Messages.getString(r0, r14)     // Catch:{ all -> 0x00c5 }
            r6.processEvent(r7, r8, r9, r10, r11, r13, r14)     // Catch:{ all -> 0x00c5 }
        L_0x00be:
            monitor-exit(r2)     // Catch:{ all -> 0x00c5 }
            r0 = 0
            r1.metadata = r0
            r1.owner = r0
            return
        L_0x00c5:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x00c5 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.RowDataDynamic.close():void");
    }

    public ResultSetRow getAt(int ind) throws SQLException {
        notSupported();
        return null;
    }

    public int getCurrentRowNumber() throws SQLException {
        notSupported();
        return -1;
    }

    public ResultSetInternalMethods getOwner() {
        return this.owner;
    }

    public boolean hasNext() throws SQLException {
        boolean hasNext = this.nextRow != null;
        if (!hasNext && !this.streamerClosed) {
            this.f5io.closeStreamer(this);
            this.streamerClosed = true;
        }
        return hasNext;
    }

    public boolean isAfterLast() throws SQLException {
        return this.isAfterEnd;
    }

    public boolean isBeforeFirst() throws SQLException {
        return this.index < 0;
    }

    public boolean isDynamic() {
        return true;
    }

    public boolean isEmpty() throws SQLException {
        notSupported();
        return false;
    }

    public boolean isFirst() throws SQLException {
        notSupported();
        return false;
    }

    public boolean isLast() throws SQLException {
        notSupported();
        return false;
    }

    public void moveRowRelative(int rows) throws SQLException {
        notSupported();
    }

    public ResultSetRow next() throws SQLException {
        int i;
        nextRecord();
        if (this.nextRow == null && !this.streamerClosed && !this.moreResultsExisted) {
            this.f5io.closeStreamer(this);
            this.streamerClosed = true;
        }
        ResultSetRow resultSetRow = this.nextRow;
        if (!(resultSetRow == null || (i = this.index) == Integer.MAX_VALUE)) {
            this.index = i + 1;
        }
        return resultSetRow;
    }

    private void nextRecord() throws SQLException {
        try {
            if (!this.noMoreRows) {
                ResultSetRow nextRow2 = this.f5io.nextRow(this.metadata, this.columnCount, this.isBinaryEncoded, 1007, true, this.useBufferRowExplicit, true, (Buffer) null);
                this.nextRow = nextRow2;
                if (nextRow2 == null) {
                    this.noMoreRows = true;
                    this.isAfterEnd = true;
                    this.moreResultsExisted = this.f5io.tackOnMoreStreamingResults(this.owner);
                    if (this.index == -1) {
                        this.wasEmpty = true;
                        return;
                    }
                    return;
                }
                return;
            }
            this.nextRow = null;
            this.isAfterEnd = true;
        } catch (SQLException sqlEx) {
            if (sqlEx instanceof StreamingNotifiable) {
                ((StreamingNotifiable) sqlEx).setWasStreamingResults();
            }
            this.noMoreRows = true;
            throw sqlEx;
        } catch (Exception ex) {
            SQLException sqlEx2 = SQLError.createSQLException(Messages.getString("RowDataDynamic.2", new String[]{ex.getClass().getName(), ex.getMessage(), Util.stackTraceToString(ex)}), SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
            sqlEx2.initCause(ex);
            throw sqlEx2;
        }
    }

    private void notSupported() throws SQLException {
        throw new OperationNotSupportedException();
    }

    public void removeRow(int ind) throws SQLException {
        notSupported();
    }

    public void setCurrentRow(int rowNumber) throws SQLException {
        notSupported();
    }

    public void setOwner(ResultSetImpl rs) {
        this.owner = rs;
    }

    public int size() {
        return -1;
    }

    public boolean wasEmpty() {
        return this.wasEmpty;
    }

    public void setMetadata(Field[] metadata2) {
        this.metadata = metadata2;
    }
}
