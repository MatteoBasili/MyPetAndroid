package com.mysql.jdbc;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.StatementImpl;
import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class ServerPreparedStatement extends PreparedStatement {
    protected static final int BLOB_STREAM_READ_BUF_SIZE = 8192;
    private static final Constructor<?> JDBC_4_SPS_CTOR;
    private boolean canRewrite = false;
    private Calendar defaultTzCalendar;
    private boolean detectedLongParameterSwitch = false;
    private int fieldCount;
    private boolean hasCheckedRewrite = false;
    private boolean hasOnDuplicateKeyUpdate = false;
    private boolean invalid = false;
    private SQLException invalidationException;
    protected boolean isCached = false;
    private int locationOfOnDuplicateKeyUpdate = -2;
    private Buffer outByteBuffer;
    private BindValue[] parameterBindings;
    private Field[] parameterFields;
    private Field[] resultFields;
    private boolean sendTypesToServer = false;
    private boolean serverNeedsResetBeforeEachExecution;
    private long serverStatementId;
    private Calendar serverTzCalendar;
    private int stringTypeCode = 254;
    private boolean useAutoSlowLog;

    static {
        if (Util.isJdbc4()) {
            try {
                JDBC_4_SPS_CTOR = Class.forName(Util.isJdbc42() ? "com.mysql.jdbc.JDBC42ServerPreparedStatement" : "com.mysql.jdbc.JDBC4ServerPreparedStatement").getConstructor(new Class[]{MySQLConnection.class, String.class, String.class, Integer.TYPE, Integer.TYPE});
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e2) {
                throw new RuntimeException(e2);
            } catch (ClassNotFoundException e3) {
                throw new RuntimeException(e3);
            }
        } else {
            JDBC_4_SPS_CTOR = null;
        }
    }

    public static class BatchedBindValues {
        public BindValue[] batchedParameterValues;

        BatchedBindValues(BindValue[] paramVals) {
            int numParams = paramVals.length;
            this.batchedParameterValues = new BindValue[numParams];
            for (int i = 0; i < numParams; i++) {
                this.batchedParameterValues[i] = new BindValue(paramVals[i]);
            }
        }
    }

    public static class BindValue {
        public long bindLength;
        public long boundBeforeExecutionNum = 0;
        public int bufferType;
        public Calendar calendar;
        public double doubleBinding;
        public float floatBinding;
        public boolean isLongData;
        public boolean isNull;
        public boolean isSet = false;
        public long longBinding;
        public Object value;

        BindValue() {
        }

        BindValue(BindValue copyMe) {
            this.value = copyMe.value;
            this.isSet = copyMe.isSet;
            this.isLongData = copyMe.isLongData;
            this.isNull = copyMe.isNull;
            this.bufferType = copyMe.bufferType;
            this.bindLength = copyMe.bindLength;
            this.longBinding = copyMe.longBinding;
            this.floatBinding = copyMe.floatBinding;
            this.doubleBinding = copyMe.doubleBinding;
            this.calendar = copyMe.calendar;
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            this.isNull = false;
            this.isSet = false;
            this.value = null;
            this.isLongData = false;
            this.longBinding = 0;
            this.floatBinding = 0.0f;
            this.doubleBinding = 0.0d;
            this.calendar = null;
        }

        public String toString() {
            return toString(false);
        }

        public String toString(boolean quoteIfNeeded) {
            if (this.isLongData) {
                return "' STREAM DATA '";
            }
            if (this.isNull) {
                return "NULL";
            }
            switch (this.bufferType) {
                case 1:
                case 2:
                case 3:
                case 8:
                    return String.valueOf(this.longBinding);
                case 4:
                    return String.valueOf(this.floatBinding);
                case 5:
                    return String.valueOf(this.doubleBinding);
                case 7:
                case 10:
                case 11:
                case 12:
                case 15:
                case 253:
                case 254:
                    if (quoteIfNeeded) {
                        return "'" + String.valueOf(this.value) + "'";
                    }
                    return String.valueOf(this.value);
                default:
                    Object obj = this.value;
                    if (obj instanceof byte[]) {
                        return "byte data";
                    }
                    if (quoteIfNeeded) {
                        return "'" + String.valueOf(this.value) + "'";
                    }
                    return String.valueOf(obj);
            }
        }

        /* access modifiers changed from: package-private */
        public long getBoundLength() {
            if (this.isNull) {
                return 0;
            }
            if (this.isLongData) {
                return this.bindLength;
            }
            switch (this.bufferType) {
                case 0:
                case 15:
                case 246:
                case 253:
                case 254:
                    Object obj = this.value;
                    if (obj instanceof byte[]) {
                        return (long) ((byte[]) obj).length;
                    }
                    return (long) ((String) obj).length();
                case 1:
                    return 1;
                case 2:
                    return 2;
                case 3:
                    return 4;
                case 4:
                    return 4;
                case 5:
                    return 8;
                case 7:
                case 12:
                    return 11;
                case 8:
                    return 8;
                case 10:
                    return 7;
                case 11:
                    return 9;
                default:
                    return 0;
            }
        }
    }

    /* JADX INFO: finally extract failed */
    private void storeTime(Buffer intoBuf, Time tm) throws SQLException {
        intoBuf.ensureCapacity(9);
        intoBuf.writeByte((byte) 8);
        intoBuf.writeByte((byte) 0);
        intoBuf.writeLong(0);
        Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
        synchronized (sessionCalendar) {
            Date oldTime = sessionCalendar.getTime();
            try {
                sessionCalendar.setTime(tm);
                intoBuf.writeByte((byte) sessionCalendar.get(11));
                intoBuf.writeByte((byte) sessionCalendar.get(12));
                intoBuf.writeByte((byte) sessionCalendar.get(13));
                sessionCalendar.setTime(oldTime);
            } catch (Throwable th) {
                sessionCalendar.setTime(oldTime);
                throw th;
            }
        }
    }

    protected static ServerPreparedStatement getInstance(MySQLConnection conn, String sql, String catalog, int resultSetType, int resultSetConcurrency) throws SQLException {
        if (!Util.isJdbc4()) {
            return new ServerPreparedStatement(conn, sql, catalog, resultSetType, resultSetConcurrency);
        }
        try {
            return (ServerPreparedStatement) JDBC_4_SPS_CTOR.newInstance(new Object[]{conn, sql, catalog, Integer.valueOf(resultSetType), Integer.valueOf(resultSetConcurrency)});
        } catch (IllegalArgumentException e) {
            throw new SQLException(e.toString(), SQLError.SQL_STATE_GENERAL_ERROR);
        } catch (InstantiationException e2) {
            throw new SQLException(e2.toString(), SQLError.SQL_STATE_GENERAL_ERROR);
        } catch (IllegalAccessException e3) {
            throw new SQLException(e3.toString(), SQLError.SQL_STATE_GENERAL_ERROR);
        } catch (InvocationTargetException e4) {
            Throwable target = e4.getTargetException();
            if (target instanceof SQLException) {
                throw ((SQLException) target);
            }
            throw new SQLException(target.toString(), SQLError.SQL_STATE_GENERAL_ERROR);
        }
    }

    protected ServerPreparedStatement(MySQLConnection conn, String sql, String catalog, int resultSetType, int resultSetConcurrency) throws SQLException {
        super(conn, catalog);
        checkNullOrEmptyQuery(sql);
        this.firstCharOfStmt = StringUtils.firstAlphaCharUc(sql, findStartOfStatement(sql));
        this.hasOnDuplicateKeyUpdate = this.firstCharOfStmt == 'I' && containsOnDuplicateKeyInString(sql);
        if (this.connection.versionMeetsMinimum(5, 0, 0)) {
            this.serverNeedsResetBeforeEachExecution = !this.connection.versionMeetsMinimum(5, 0, 3);
        } else {
            this.serverNeedsResetBeforeEachExecution = !this.connection.versionMeetsMinimum(4, 1, 10);
        }
        this.useAutoSlowLog = this.connection.getAutoSlowLog();
        this.useTrueBoolean = this.connection.versionMeetsMinimum(3, 21, 23);
        String statementComment = this.connection.getStatementComment();
        this.originalSql = statementComment == null ? sql : "/* " + statementComment + " */ " + sql;
        if (this.connection.versionMeetsMinimum(4, 1, 2)) {
            this.stringTypeCode = 253;
        } else {
            this.stringTypeCode = 254;
        }
        try {
            serverPrepare(sql);
            setResultSetType(resultSetType);
            setResultSetConcurrency(resultSetConcurrency);
            this.parameterTypes = new int[this.parameterCount];
        } catch (SQLException sqlEx) {
            realClose(false, true);
            throw sqlEx;
        } catch (Exception ex) {
            realClose(false, true);
            SQLException sqlEx2 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
            sqlEx2.initCause(ex);
            throw sqlEx2;
        }
    }

    public void addBatch() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.batchedArgs == null) {
                this.batchedArgs = new ArrayList();
            }
            this.batchedArgs.add(new BatchedBindValues(this.parameterBindings));
        }
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:28:0x0088=Splitter:B:28:0x0088, B:37:0x0098=Splitter:B:37:0x0098} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String asSql(boolean r10) throws java.sql.SQLException {
        /*
            r9 = this;
            com.mysql.jdbc.MySQLConnection r0 = r9.checkClosed()
            java.lang.Object r0 = r0.getConnectionMutex()
            monitor-enter(r0)
            r1 = 0
            com.mysql.jdbc.MySQLConnection r2 = r9.connection     // Catch:{ all -> 0x008a }
            java.lang.String r3 = r9.originalSql     // Catch:{ all -> 0x008a }
            java.lang.String r4 = r9.currentCatalog     // Catch:{ all -> 0x008a }
            com.mysql.jdbc.PreparedStatement r2 = com.mysql.jdbc.PreparedStatement.getInstance(r2, r3, r4)     // Catch:{ all -> 0x008a }
            r1 = r2
            int r2 = r1.parameterCount     // Catch:{ all -> 0x008a }
            int r3 = r9.parameterCount     // Catch:{ all -> 0x008a }
            r4 = 0
        L_0x001a:
            if (r4 >= r2) goto L_0x007c
            if (r4 >= r3) goto L_0x007c
            com.mysql.jdbc.ServerPreparedStatement$BindValue[] r5 = r9.parameterBindings     // Catch:{ all -> 0x008a }
            r5 = r5[r4]     // Catch:{ all -> 0x008a }
            if (r5 == 0) goto L_0x0079
            boolean r5 = r5.isNull     // Catch:{ all -> 0x008a }
            if (r5 == 0) goto L_0x002f
            int r5 = r4 + 1
            r6 = 0
            r1.setNull(r5, r6)     // Catch:{ all -> 0x008a }
            goto L_0x0079
        L_0x002f:
            com.mysql.jdbc.ServerPreparedStatement$BindValue[] r5 = r9.parameterBindings     // Catch:{ all -> 0x008a }
            r5 = r5[r4]     // Catch:{ all -> 0x008a }
            int r6 = r5.bufferType     // Catch:{ all -> 0x008a }
            switch(r6) {
                case 1: goto L_0x0068;
                case 2: goto L_0x005e;
                case 3: goto L_0x0055;
                case 4: goto L_0x004d;
                case 5: goto L_0x0045;
                case 6: goto L_0x0038;
                case 7: goto L_0x0038;
                case 8: goto L_0x003d;
                default: goto L_0x0038;
            }     // Catch:{ all -> 0x008a }
        L_0x0038:
            int r6 = r4 + 1
            com.mysql.jdbc.ServerPreparedStatement$BindValue[] r7 = r9.parameterBindings     // Catch:{ all -> 0x008a }
            goto L_0x0072
        L_0x003d:
            int r6 = r4 + 1
            long r7 = r5.longBinding     // Catch:{ all -> 0x008a }
            r1.setLong(r6, r7)     // Catch:{ all -> 0x008a }
            goto L_0x0079
        L_0x0045:
            int r6 = r4 + 1
            double r7 = r5.doubleBinding     // Catch:{ all -> 0x008a }
            r1.setDouble(r6, r7)     // Catch:{ all -> 0x008a }
            goto L_0x0079
        L_0x004d:
            int r6 = r4 + 1
            float r7 = r5.floatBinding     // Catch:{ all -> 0x008a }
            r1.setFloat(r6, r7)     // Catch:{ all -> 0x008a }
            goto L_0x0079
        L_0x0055:
            int r6 = r4 + 1
            long r7 = r5.longBinding     // Catch:{ all -> 0x008a }
            int r7 = (int) r7     // Catch:{ all -> 0x008a }
            r1.setInt(r6, r7)     // Catch:{ all -> 0x008a }
            goto L_0x0079
        L_0x005e:
            int r6 = r4 + 1
            long r7 = r5.longBinding     // Catch:{ all -> 0x008a }
            int r7 = (int) r7     // Catch:{ all -> 0x008a }
            short r7 = (short) r7     // Catch:{ all -> 0x008a }
            r1.setShort(r6, r7)     // Catch:{ all -> 0x008a }
            goto L_0x0079
        L_0x0068:
            int r6 = r4 + 1
            long r7 = r5.longBinding     // Catch:{ all -> 0x008a }
            int r7 = (int) r7     // Catch:{ all -> 0x008a }
            byte r7 = (byte) r7     // Catch:{ all -> 0x008a }
            r1.setByte(r6, r7)     // Catch:{ all -> 0x008a }
            goto L_0x0079
        L_0x0072:
            r7 = r7[r4]     // Catch:{ all -> 0x008a }
            java.lang.Object r7 = r7.value     // Catch:{ all -> 0x008a }
            r1.setObject(r6, r7)     // Catch:{ all -> 0x008a }
        L_0x0079:
            int r4 = r4 + 1
            goto L_0x001a
        L_0x007c:
            java.lang.String r4 = r1.asSql(r10)     // Catch:{ all -> 0x008a }
            if (r1 == 0) goto L_0x0087
            r1.close()     // Catch:{ SQLException -> 0x0086 }
            goto L_0x0087
        L_0x0086:
            r5 = move-exception
        L_0x0087:
            monitor-exit(r0)     // Catch:{ all -> 0x0091 }
            return r4
        L_0x008a:
            r2 = move-exception
            if (r1 == 0) goto L_0x0095
            r1.close()     // Catch:{ SQLException -> 0x0093 }
            goto L_0x0095
        L_0x0091:
            r1 = move-exception
            goto L_0x0099
        L_0x0093:
            r3 = move-exception
            goto L_0x0096
        L_0x0095:
        L_0x0096:
            throw r2     // Catch:{ all -> 0x0091 }
        L_0x0099:
            monitor-exit(r0)     // Catch:{ all -> 0x0091 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ServerPreparedStatement.asSql(boolean):java.lang.String");
    }

    /* access modifiers changed from: protected */
    public MySQLConnection checkClosed() throws SQLException {
        if (!this.invalid) {
            return super.checkClosed();
        }
        throw this.invalidationException;
    }

    public void clearParameters() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            clearParametersInternal(true);
        }
    }

    private void clearParametersInternal(boolean clearServerParameters) throws SQLException {
        boolean hadLongData = false;
        if (this.parameterBindings != null) {
            for (int i = 0; i < this.parameterCount; i++) {
                BindValue bindValue = this.parameterBindings[i];
                if (bindValue != null && bindValue.isLongData) {
                    hadLongData = true;
                }
                this.parameterBindings[i].reset();
            }
        }
        if (clearServerParameters && hadLongData) {
            serverResetStatement();
            this.detectedLongParameterSwitch = false;
        }
    }

    /* access modifiers changed from: protected */
    public void setClosed(boolean flag) {
        this.isClosed = flag;
    }

    public void close() throws SQLException {
        MySQLConnection locallyScopedConn = this.connection;
        if (locallyScopedConn != null) {
            synchronized (locallyScopedConn.getConnectionMutex()) {
                if (!this.isCached || !isPoolable() || this.isClosed) {
                    this.isClosed = false;
                    realClose(true, true);
                    return;
                }
                clearParameters();
                this.isClosed = true;
                this.connection.recachePreparedStatement(this);
            }
        }
    }

    private void dumpCloseForTestcase() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            StringBuilder buf = new StringBuilder();
            this.connection.generateConnectionCommentBlock(buf);
            buf.append("DEALLOCATE PREPARE debug_stmt_");
            buf.append(this.statementId);
            buf.append(";\n");
            this.connection.dumpTestcaseQuery(buf.toString());
        }
    }

    private void dumpExecuteForTestcase() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < this.parameterCount; i++) {
                this.connection.generateConnectionCommentBlock(buf);
                buf.append("SET @debug_stmt_param");
                buf.append(this.statementId);
                buf.append("_");
                buf.append(i);
                buf.append("=");
                if (this.parameterBindings[i].isNull) {
                    buf.append("NULL");
                } else {
                    buf.append(this.parameterBindings[i].toString(true));
                }
                buf.append(";\n");
            }
            this.connection.generateConnectionCommentBlock(buf);
            buf.append("EXECUTE debug_stmt_");
            buf.append(this.statementId);
            if (this.parameterCount > 0) {
                buf.append(" USING ");
                for (int i2 = 0; i2 < this.parameterCount; i2++) {
                    if (i2 > 0) {
                        buf.append(", ");
                    }
                    buf.append("@debug_stmt_param");
                    buf.append(this.statementId);
                    buf.append("_");
                    buf.append(i2);
                }
            }
            buf.append(";\n");
            this.connection.dumpTestcaseQuery(buf.toString());
        }
    }

    private void dumpPrepareForTestcase() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            StringBuilder buf = new StringBuilder(this.originalSql.length() + 64);
            this.connection.generateConnectionCommentBlock(buf);
            buf.append("PREPARE debug_stmt_");
            buf.append(this.statementId);
            buf.append(" FROM \"");
            buf.append(this.originalSql);
            buf.append("\";\n");
            this.connection.dumpTestcaseQuery(buf.toString());
        }
    }

    /* access modifiers changed from: protected */
    public long[] executeBatchSerially(int batchTimeout) throws SQLException {
        long[] jArr;
        int i = batchTimeout;
        synchronized (checkClosed().getConnectionMutex()) {
            MySQLConnection locallyScopedConn = this.connection;
            if (!locallyScopedConn.isReadOnly()) {
                clearWarnings();
                BindValue[] oldBindValues = this.parameterBindings;
                long[] updateCounts = null;
                try {
                    int i2 = 0;
                    if (this.batchedArgs != null) {
                        int nbrCommands = this.batchedArgs.size();
                        long[] updateCounts2 = new long[nbrCommands];
                        if (this.retrieveGeneratedKeys) {
                            this.batchedGeneratedKeys = new ArrayList(nbrCommands);
                        }
                        for (int i3 = 0; i3 < nbrCommands; i3++) {
                            updateCounts2[i3] = -3;
                        }
                        SQLException sqlEx = null;
                        BindValue[] previousBindValuesForBatch = null;
                        StatementImpl.CancelTask timeoutTask = null;
                        try {
                            if (locallyScopedConn.getEnableQueryTimeouts() && i != 0 && locallyScopedConn.versionMeetsMinimum(5, 0, 0)) {
                                timeoutTask = new StatementImpl.CancelTask(this);
                                locallyScopedConn.getCancelTimer().schedule(timeoutTask, (long) i);
                            }
                            int commandIndex = 0;
                            while (commandIndex < nbrCommands) {
                                Object arg = this.batchedArgs.get(commandIndex);
                                try {
                                    if (arg instanceof String) {
                                        updateCounts2[commandIndex] = executeUpdateInternal((String) arg, true, this.retrieveGeneratedKeys);
                                        getBatchedGeneratedKeys((this.results.getFirstCharOfQuery() != 'I' || !containsOnDuplicateKeyInString((String) arg)) ? i2 : 1);
                                    } else {
                                        this.parameterBindings = ((BatchedBindValues) arg).batchedParameterValues;
                                        if (previousBindValuesForBatch != null) {
                                            int j = 0;
                                            while (true) {
                                                BindValue[] bindValueArr = this.parameterBindings;
                                                if (j >= bindValueArr.length) {
                                                    break;
                                                } else if (bindValueArr[j].bufferType != previousBindValuesForBatch[j].bufferType) {
                                                    this.sendTypesToServer = true;
                                                    break;
                                                } else {
                                                    j++;
                                                }
                                            }
                                        }
                                        updateCounts2[commandIndex] = executeUpdateInternal(false, true);
                                        previousBindValuesForBatch = this.parameterBindings;
                                        getBatchedGeneratedKeys(containsOnDuplicateKeyUpdateInSQL() ? 1 : 0);
                                    }
                                } catch (SQLException ex) {
                                    updateCounts2[commandIndex] = -3;
                                    if (!this.continueBatchOnError || (ex instanceof MySQLTimeoutException) || (ex instanceof MySQLStatementCancelledException) || hasDeadlockOrTimeoutRolledBackTx(ex)) {
                                        long[] newUpdateCounts = new long[commandIndex];
                                        System.arraycopy(updateCounts2, 0, newUpdateCounts, 0, commandIndex);
                                        throw SQLError.createBatchUpdateException(ex, newUpdateCounts, getExceptionInterceptor());
                                    }
                                    sqlEx = ex;
                                } catch (Throwable th) {
                                    BindValue[] previousBindValuesForBatch2 = this.parameterBindings;
                                    throw th;
                                }
                                commandIndex++;
                                i2 = 0;
                            }
                            if (timeoutTask != null) {
                                timeoutTask.cancel();
                                locallyScopedConn.getCancelTimer().purge();
                            }
                            resetCancelledState();
                            if (sqlEx == null) {
                                updateCounts = updateCounts2;
                            } else {
                                throw SQLError.createBatchUpdateException(sqlEx, updateCounts2, getExceptionInterceptor());
                            }
                        } catch (Throwable th2) {
                            if (timeoutTask != null) {
                                timeoutTask.cancel();
                                locallyScopedConn.getCancelTimer().purge();
                            }
                            resetCancelledState();
                            throw th2;
                        }
                    }
                    jArr = updateCounts != null ? updateCounts : new long[0];
                } finally {
                    this.parameterBindings = oldBindValues;
                    this.sendTypesToServer = true;
                    clearBatch();
                }
            } else {
                throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.2") + Messages.getString("ServerPreparedStatement.3"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
        return jArr;
    }

    /* access modifiers changed from: protected */
    public ResultSetInternalMethods executeInternal(int maxRowsToRetrieve, Buffer sendPacket, boolean createStreamingResultSet, boolean queryIsSelectOnly, Field[] metadataFromCache, boolean isBatch) throws SQLException {
        ResultSetInternalMethods serverExecute;
        synchronized (checkClosed().getConnectionMutex()) {
            this.numberOfExecutions++;
            try {
                serverExecute = serverExecute(maxRowsToRetrieve, createStreamingResultSet, metadataFromCache);
            } catch (SQLException e) {
                sqlEx = e;
                if (this.connection.getEnablePacketDebug()) {
                    this.connection.getIO().dumpPacketRingBuffer();
                }
                if (this.connection.getDumpQueriesOnException()) {
                    String extractedSql = toString();
                    StringBuilder messageBuf = new StringBuilder(extractedSql.length() + 32);
                    messageBuf.append("\n\nQuery being executed when exception was thrown:\n");
                    messageBuf.append(extractedSql);
                    messageBuf.append("\n\n");
                    sqlEx = ConnectionImpl.appendMessageToException(sqlEx, messageBuf.toString(), getExceptionInterceptor());
                }
                throw sqlEx;
            } catch (Exception ex) {
                if (this.connection.getEnablePacketDebug()) {
                    this.connection.getIO().dumpPacketRingBuffer();
                }
                SQLException sqlEx = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
                if (this.connection.getDumpQueriesOnException()) {
                    String extractedSql2 = toString();
                    StringBuilder messageBuf2 = new StringBuilder(extractedSql2.length() + 32);
                    messageBuf2.append("\n\nQuery being executed when exception was thrown:\n");
                    messageBuf2.append(extractedSql2);
                    messageBuf2.append("\n\n");
                    sqlEx = ConnectionImpl.appendMessageToException(sqlEx, messageBuf2.toString(), getExceptionInterceptor());
                }
                sqlEx.initCause(ex);
                throw sqlEx;
            }
        }
        return serverExecute;
    }

    /* access modifiers changed from: protected */
    public Buffer fillSendPacket() throws SQLException {
        return null;
    }

    /* access modifiers changed from: protected */
    public Buffer fillSendPacket(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths) throws SQLException {
        return null;
    }

    /* access modifiers changed from: protected */
    public BindValue getBinding(int parameterIndex, boolean forLongData) throws SQLException {
        BindValue bindValue;
        synchronized (checkClosed().getConnectionMutex()) {
            BindValue[] bindValueArr = this.parameterBindings;
            if (bindValueArr.length != 0) {
                int parameterIndex2 = parameterIndex - 1;
                if (parameterIndex2 < 0 || parameterIndex2 >= bindValueArr.length) {
                    throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.9") + (parameterIndex2 + 1) + Messages.getString("ServerPreparedStatement.10") + this.parameterBindings.length, SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                }
                BindValue bindValue2 = bindValueArr[parameterIndex2];
                if (bindValue2 == null) {
                    bindValueArr[parameterIndex2] = new BindValue();
                } else if (bindValue2.isLongData && !forLongData) {
                    this.detectedLongParameterSwitch = true;
                }
                bindValue = this.parameterBindings[parameterIndex2];
            } else {
                throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.8"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
        return bindValue;
    }

    public BindValue[] getParameterBindValues() {
        return this.parameterBindings;
    }

    /* access modifiers changed from: package-private */
    public byte[] getBytes(int parameterIndex) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            BindValue bindValue = getBinding(parameterIndex, false);
            if (bindValue.isNull) {
                return null;
            }
            if (!bindValue.isLongData) {
                if (this.outByteBuffer == null) {
                    this.outByteBuffer = new Buffer(this.connection.getNetBufferLength());
                }
                this.outByteBuffer.clear();
                int originalPosition = this.outByteBuffer.getPosition();
                storeBinding(this.outByteBuffer, bindValue, this.connection.getIO());
                int length = this.outByteBuffer.getPosition() - originalPosition;
                byte[] valueAsBytes = new byte[length];
                System.arraycopy(this.outByteBuffer.getByteBuffer(), originalPosition, valueAsBytes, 0, length);
                return valueAsBytes;
            }
            throw SQLError.createSQLFeatureNotSupportedException();
        }
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.resultFields == null) {
                return null;
            }
            ResultSetMetaData resultSetMetaData = new ResultSetMetaData(this.resultFields, this.connection.getUseOldAliasMetadataBehavior(), this.connection.getYearIsDateType(), getExceptionInterceptor());
            return resultSetMetaData;
        }
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        MysqlParameterMetadata mysqlParameterMetadata;
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.parameterMetaData == null) {
                this.parameterMetaData = new MysqlParameterMetadata(this.parameterFields, this.parameterCount, getExceptionInterceptor());
            }
            mysqlParameterMetadata = this.parameterMetaData;
        }
        return mysqlParameterMetadata;
    }

    /* access modifiers changed from: package-private */
    public boolean isNull(int paramIndex) {
        throw new IllegalArgumentException(Messages.getString("ServerPreparedStatement.7"));
    }

    /* access modifiers changed from: protected */
    public void realClose(boolean calledExplicitly, boolean closeOpenResults) throws SQLException {
        MySQLConnection locallyScopedConn = this.connection;
        if (locallyScopedConn != null) {
            synchronized (locallyScopedConn.getConnectionMutex()) {
                if (this.connection != null) {
                    if (this.connection.getAutoGenerateTestcaseScript()) {
                        dumpCloseForTestcase();
                    }
                    SQLException exceptionDuringClose = null;
                    if (calledExplicitly && !this.connection.isClosed()) {
                        synchronized (this.connection.getConnectionMutex()) {
                            try {
                                MysqlIO mysql = this.connection.getIO();
                                Buffer packet = mysql.getSharedSendPacket();
                                packet.writeByte((byte) 25);
                                packet.writeLong(this.serverStatementId);
                                mysql.sendCommand(25, (String) null, packet, true, (String) null, 0);
                            } catch (SQLException sqlEx) {
                                exceptionDuringClose = sqlEx;
                            }
                        }
                    }
                    if (this.isCached) {
                        this.connection.decachePreparedStatement(this);
                        this.isCached = false;
                    }
                    super.realClose(calledExplicitly, closeOpenResults);
                    clearParametersInternal(false);
                    this.parameterBindings = null;
                    this.parameterFields = null;
                    this.resultFields = null;
                    if (exceptionDuringClose != null) {
                        throw exceptionDuringClose;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void rePrepare() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            this.invalidationException = null;
            try {
                serverPrepare(this.originalSql);
            } catch (SQLException sqlEx) {
                this.invalidationException = sqlEx;
            } catch (Exception ex) {
                SQLException createSQLException = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
                this.invalidationException = createSQLException;
                createSQLException.initCause(ex);
            }
            if (this.invalidationException != null) {
                this.invalid = true;
                this.parameterBindings = null;
                this.parameterFields = null;
                this.resultFields = null;
                if (this.results != null) {
                    try {
                        this.results.close();
                    } catch (Exception e) {
                    }
                }
                if (this.generatedKeysResults != null) {
                    try {
                        this.generatedKeysResults.close();
                    } catch (Exception e2) {
                    }
                }
                try {
                    closeAllOpenResults();
                } catch (Exception e3) {
                }
                if (this.connection != null && !this.connection.getDontTrackOpenResources()) {
                    this.connection.unregisterStatement(this);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isCursorRequired() throws SQLException {
        return this.resultFields != null && this.connection.isCursorFetchEnabled() && getResultSetType() == 1003 && getResultSetConcurrency() == 1007 && getFetchSize() > 0;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v71, resolved type: com.mysql.jdbc.exceptions.MySQLStatementCancelledException} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v72, resolved type: com.mysql.jdbc.exceptions.MySQLTimeoutException} */
    /* JADX WARNING: type inference failed for: r1v31, types: [com.mysql.jdbc.ResultSetInternalMethods] */
    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        	at java.util.ArrayList.get(ArrayList.java:435)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:693)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:693)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:598)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x0220 A[SYNTHETIC, Splitter:B:112:0x0220] */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x0239 A[Catch:{ SQLException -> 0x022f, all -> 0x0226 }] */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x023f A[Catch:{ SQLException -> 0x022f, all -> 0x0226 }] */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x0256  */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:210:0x03e0=Splitter:B:210:0x03e0, B:252:0x048d=Splitter:B:252:0x048d} */
    private com.mysql.jdbc.ResultSetInternalMethods serverExecute(int r40, boolean r41, com.mysql.jdbc.Field[] r42) throws java.sql.SQLException {
        /*
            r39 = this;
            r15 = r39
            com.mysql.jdbc.MySQLConnection r0 = r39.checkClosed()
            java.lang.Object r22 = r0.getConnectionMutex()
            monitor-enter(r22)
            com.mysql.jdbc.MySQLConnection r0 = r15.connection     // Catch:{ all -> 0x04a3 }
            com.mysql.jdbc.MysqlIO r0 = r0.getIO()     // Catch:{ all -> 0x04a3 }
            r14 = r0
            boolean r0 = r14.shouldIntercept()     // Catch:{ all -> 0x04a3 }
            r8 = 1
            if (r0 == 0) goto L_0x0023
            java.lang.String r0 = r15.originalSql     // Catch:{ all -> 0x04a3 }
            com.mysql.jdbc.ResultSetInternalMethods r0 = r14.invokeStatementInterceptorsPre(r0, r15, r8)     // Catch:{ all -> 0x04a3 }
            if (r0 == 0) goto L_0x0023
            monitor-exit(r22)     // Catch:{ all -> 0x04a3 }
            return r0
        L_0x0023:
            boolean r0 = r15.detectedLongParameterSwitch     // Catch:{ all -> 0x04a3 }
            if (r0 == 0) goto L_0x007b
            r0 = 0
            r1 = 0
            r3 = 0
        L_0x002b:
            int r4 = r15.parameterCount     // Catch:{ all -> 0x04a3 }
            int r4 = r4 - r8
            if (r3 >= r4) goto L_0x0078
            com.mysql.jdbc.ServerPreparedStatement$BindValue[] r4 = r15.parameterBindings     // Catch:{ all -> 0x04a3 }
            r4 = r4[r3]     // Catch:{ all -> 0x04a3 }
            boolean r4 = r4.isLongData     // Catch:{ all -> 0x04a3 }
            if (r4 == 0) goto L_0x0075
            if (r0 == 0) goto L_0x006d
            com.mysql.jdbc.ServerPreparedStatement$BindValue[] r4 = r15.parameterBindings     // Catch:{ all -> 0x04a3 }
            r4 = r4[r3]     // Catch:{ all -> 0x04a3 }
            long r4 = r4.boundBeforeExecutionNum     // Catch:{ all -> 0x04a3 }
            int r4 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r4 != 0) goto L_0x0045
            goto L_0x006d
        L_0x0045:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x04a3 }
            r4.<init>()     // Catch:{ all -> 0x04a3 }
            java.lang.String r5 = "ServerPreparedStatement.11"
            java.lang.String r5 = com.mysql.jdbc.Messages.getString(r5)     // Catch:{ all -> 0x04a3 }
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch:{ all -> 0x04a3 }
            java.lang.String r5 = "ServerPreparedStatement.12"
            java.lang.String r5 = com.mysql.jdbc.Messages.getString(r5)     // Catch:{ all -> 0x04a3 }
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch:{ all -> 0x04a3 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x04a3 }
            java.lang.String r5 = "S1C00"
            com.mysql.jdbc.ExceptionInterceptor r6 = r39.getExceptionInterceptor()     // Catch:{ all -> 0x04a3 }
            java.sql.SQLException r4 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r4, (java.lang.String) r5, (com.mysql.jdbc.ExceptionInterceptor) r6)     // Catch:{ all -> 0x04a3 }
            throw r4     // Catch:{ all -> 0x04a3 }
        L_0x006d:
            r0 = 1
            com.mysql.jdbc.ServerPreparedStatement$BindValue[] r4 = r15.parameterBindings     // Catch:{ all -> 0x04a3 }
            r4 = r4[r3]     // Catch:{ all -> 0x04a3 }
            long r4 = r4.boundBeforeExecutionNum     // Catch:{ all -> 0x04a3 }
            r1 = r4
        L_0x0075:
            int r3 = r3 + 1
            goto L_0x002b
        L_0x0078:
            r39.serverResetStatement()     // Catch:{ all -> 0x04a3 }
        L_0x007b:
            r0 = 0
        L_0x007c:
            int r1 = r15.parameterCount     // Catch:{ all -> 0x04a3 }
            if (r0 >= r1) goto L_0x00b9
            com.mysql.jdbc.ServerPreparedStatement$BindValue[] r1 = r15.parameterBindings     // Catch:{ all -> 0x04a3 }
            r1 = r1[r0]     // Catch:{ all -> 0x04a3 }
            boolean r1 = r1.isSet     // Catch:{ all -> 0x04a3 }
            if (r1 == 0) goto L_0x008b
            int r0 = r0 + 1
            goto L_0x007c
        L_0x008b:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x04a3 }
            r1.<init>()     // Catch:{ all -> 0x04a3 }
            java.lang.String r2 = "ServerPreparedStatement.13"
            java.lang.String r2 = com.mysql.jdbc.Messages.getString(r2)     // Catch:{ all -> 0x04a3 }
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x04a3 }
            int r2 = r0 + 1
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x04a3 }
            java.lang.String r2 = "ServerPreparedStatement.14"
            java.lang.String r2 = com.mysql.jdbc.Messages.getString(r2)     // Catch:{ all -> 0x04a3 }
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x04a3 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x04a3 }
            java.lang.String r2 = "S1009"
            com.mysql.jdbc.ExceptionInterceptor r3 = r39.getExceptionInterceptor()     // Catch:{ all -> 0x04a3 }
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (com.mysql.jdbc.ExceptionInterceptor) r3)     // Catch:{ all -> 0x04a3 }
            throw r1     // Catch:{ all -> 0x04a3 }
        L_0x00b9:
            r0 = 0
        L_0x00ba:
            int r1 = r15.parameterCount     // Catch:{ all -> 0x04a3 }
            if (r0 >= r1) goto L_0x00d0
            com.mysql.jdbc.ServerPreparedStatement$BindValue[] r1 = r15.parameterBindings     // Catch:{ all -> 0x04a3 }
            r1 = r1[r0]     // Catch:{ all -> 0x04a3 }
            boolean r1 = r1.isLongData     // Catch:{ all -> 0x04a3 }
            if (r1 == 0) goto L_0x00cd
            com.mysql.jdbc.ServerPreparedStatement$BindValue[] r1 = r15.parameterBindings     // Catch:{ all -> 0x04a3 }
            r1 = r1[r0]     // Catch:{ all -> 0x04a3 }
            r15.serverLongData(r0, r1)     // Catch:{ all -> 0x04a3 }
        L_0x00cd:
            int r0 = r0 + 1
            goto L_0x00ba
        L_0x00d0:
            com.mysql.jdbc.MySQLConnection r0 = r15.connection     // Catch:{ all -> 0x04a3 }
            boolean r0 = r0.getAutoGenerateTestcaseScript()     // Catch:{ all -> 0x04a3 }
            if (r0 == 0) goto L_0x00db
            r39.dumpExecuteForTestcase()     // Catch:{ all -> 0x04a3 }
        L_0x00db:
            com.mysql.jdbc.Buffer r0 = r14.getSharedSendPacket()     // Catch:{ all -> 0x04a3 }
            r13 = r0
            r13.clear()     // Catch:{ all -> 0x04a3 }
            r0 = 23
            r13.writeByte(r0)     // Catch:{ all -> 0x04a3 }
            long r0 = r15.serverStatementId     // Catch:{ all -> 0x04a3 }
            r13.writeLong(r0)     // Catch:{ all -> 0x04a3 }
            com.mysql.jdbc.MySQLConnection r0 = r15.connection     // Catch:{ all -> 0x04a3 }
            r9 = 2
            r10 = 4
            boolean r0 = r0.versionMeetsMinimum(r10, r8, r9)     // Catch:{ all -> 0x04a3 }
            r12 = 0
            if (r0 == 0) goto L_0x010a
            boolean r0 = r39.isCursorRequired()     // Catch:{ all -> 0x04a3 }
            if (r0 == 0) goto L_0x0102
            r13.writeByte(r8)     // Catch:{ all -> 0x04a3 }
            goto L_0x0105
        L_0x0102:
            r13.writeByte(r12)     // Catch:{ all -> 0x04a3 }
        L_0x0105:
            r0 = 1
            r13.writeLong(r0)     // Catch:{ all -> 0x04a3 }
        L_0x010a:
            int r0 = r15.parameterCount     // Catch:{ all -> 0x04a3 }
            int r0 = r0 + 7
            int r0 = r0 / 8
            r11 = r0
            int r0 = r13.getPosition()     // Catch:{ all -> 0x04a3 }
            r7 = r0
            r0 = 0
        L_0x0117:
            if (r0 >= r11) goto L_0x011f
            r13.writeByte(r12)     // Catch:{ all -> 0x04a3 }
            int r0 = r0 + 1
            goto L_0x0117
        L_0x011f:
            byte[] r0 = new byte[r11]     // Catch:{ all -> 0x04a3 }
            r6 = r0
            boolean r0 = r15.sendTypesToServer     // Catch:{ all -> 0x04a3 }
            if (r0 == 0) goto L_0x0128
            r0 = r8
            goto L_0x0129
        L_0x0128:
            r0 = r12
        L_0x0129:
            r13.writeByte(r0)     // Catch:{ all -> 0x04a3 }
            boolean r0 = r15.sendTypesToServer     // Catch:{ all -> 0x04a3 }
            if (r0 == 0) goto L_0x0141
            r0 = 0
        L_0x0131:
            int r1 = r15.parameterCount     // Catch:{ all -> 0x04a3 }
            if (r0 >= r1) goto L_0x0141
            com.mysql.jdbc.ServerPreparedStatement$BindValue[] r1 = r15.parameterBindings     // Catch:{ all -> 0x04a3 }
            r1 = r1[r0]     // Catch:{ all -> 0x04a3 }
            int r1 = r1.bufferType     // Catch:{ all -> 0x04a3 }
            r13.writeInt(r1)     // Catch:{ all -> 0x04a3 }
            int r0 = r0 + 1
            goto L_0x0131
        L_0x0141:
            r0 = 0
        L_0x0142:
            int r1 = r15.parameterCount     // Catch:{ all -> 0x04a3 }
            if (r0 >= r1) goto L_0x016d
            com.mysql.jdbc.ServerPreparedStatement$BindValue[] r1 = r15.parameterBindings     // Catch:{ all -> 0x04a3 }
            r1 = r1[r0]     // Catch:{ all -> 0x04a3 }
            boolean r1 = r1.isLongData     // Catch:{ all -> 0x04a3 }
            if (r1 != 0) goto L_0x016a
            com.mysql.jdbc.ServerPreparedStatement$BindValue[] r1 = r15.parameterBindings     // Catch:{ all -> 0x04a3 }
            r1 = r1[r0]     // Catch:{ all -> 0x04a3 }
            boolean r1 = r1.isNull     // Catch:{ all -> 0x04a3 }
            if (r1 != 0) goto L_0x015e
            com.mysql.jdbc.ServerPreparedStatement$BindValue[] r1 = r15.parameterBindings     // Catch:{ all -> 0x04a3 }
            r1 = r1[r0]     // Catch:{ all -> 0x04a3 }
            r15.storeBinding(r13, r1, r14)     // Catch:{ all -> 0x04a3 }
            goto L_0x016a
        L_0x015e:
            int r1 = r0 / 8
            byte r2 = r6[r1]     // Catch:{ all -> 0x04a3 }
            r3 = r0 & 7
            int r3 = r8 << r3
            r2 = r2 | r3
            byte r2 = (byte) r2     // Catch:{ all -> 0x04a3 }
            r6[r1] = r2     // Catch:{ all -> 0x04a3 }
        L_0x016a:
            int r0 = r0 + 1
            goto L_0x0142
        L_0x016d:
            int r0 = r13.getPosition()     // Catch:{ all -> 0x04a3 }
            r5 = r0
            r13.setPosition(r7)     // Catch:{ all -> 0x04a3 }
            r13.writeBytesNoNull(r6)     // Catch:{ all -> 0x04a3 }
            r13.setPosition(r5)     // Catch:{ all -> 0x04a3 }
            com.mysql.jdbc.MySQLConnection r0 = r15.connection     // Catch:{ all -> 0x04a3 }
            boolean r0 = r0.getLogSlowQueries()     // Catch:{ all -> 0x04a3 }
            r23 = r0
            com.mysql.jdbc.MySQLConnection r0 = r15.connection     // Catch:{ all -> 0x04a3 }
            boolean r0 = r0.getGatherPerformanceMetrics()     // Catch:{ all -> 0x04a3 }
            r24 = r0
            boolean r0 = r15.profileSQL     // Catch:{ all -> 0x04a3 }
            if (r0 != 0) goto L_0x0196
            if (r23 != 0) goto L_0x0196
            if (r24 == 0) goto L_0x0194
            goto L_0x0196
        L_0x0194:
            r0 = r12
            goto L_0x0197
        L_0x0196:
            r0 = r8
        L_0x0197:
            r25 = r0
            r18 = 0
            if (r25 == 0) goto L_0x01a2
            long r0 = r14.getCurrentTimeNanosOrMillis()     // Catch:{ all -> 0x04a3 }
            goto L_0x01a4
        L_0x01a2:
            r0 = r18
        L_0x01a4:
            r26 = r0
            r39.resetCancelledState()     // Catch:{ all -> 0x04a3 }
            r1 = 0
            if (r25 == 0) goto L_0x01cf
            java.lang.String r0 = r15.asSql(r8)     // Catch:{ SQLException -> 0x01bf, all -> 0x01b1 }
            goto L_0x01d1
        L_0x01b1:
            r0 = move-exception
            r29 = r5
            r30 = r6
            r31 = r7
            r37 = r11
            r38 = r13
            r11 = r14
            goto L_0x048d
        L_0x01bf:
            r0 = move-exception
            r34 = r1
            r29 = r5
            r30 = r6
            r31 = r7
            r37 = r11
            r38 = r13
            r11 = r14
            goto L_0x0477
        L_0x01cf:
            java.lang.String r0 = ""
        L_0x01d1:
            r4 = r0
            com.mysql.jdbc.MySQLConnection r0 = r15.connection     // Catch:{ SQLException -> 0x0469, all -> 0x045c }
            boolean r0 = r0.getEnableQueryTimeouts()     // Catch:{ SQLException -> 0x0469, all -> 0x045c }
            if (r0 == 0) goto L_0x01fb
            int r0 = r15.timeoutInMillis     // Catch:{ SQLException -> 0x01bf, all -> 0x01b1 }
            if (r0 == 0) goto L_0x01fb
            com.mysql.jdbc.MySQLConnection r0 = r15.connection     // Catch:{ SQLException -> 0x01bf, all -> 0x01b1 }
            r2 = 5
            boolean r0 = r0.versionMeetsMinimum(r2, r12, r12)     // Catch:{ SQLException -> 0x01bf, all -> 0x01b1 }
            if (r0 == 0) goto L_0x01fb
            com.mysql.jdbc.StatementImpl$CancelTask r0 = new com.mysql.jdbc.StatementImpl$CancelTask     // Catch:{ SQLException -> 0x01bf, all -> 0x01b1 }
            r0.<init>(r15)     // Catch:{ SQLException -> 0x01bf, all -> 0x01b1 }
            r1 = r0
            com.mysql.jdbc.MySQLConnection r0 = r15.connection     // Catch:{ SQLException -> 0x01bf, all -> 0x01b1 }
            java.util.Timer r0 = r0.getCancelTimer()     // Catch:{ SQLException -> 0x01bf, all -> 0x01b1 }
            int r2 = r15.timeoutInMillis     // Catch:{ SQLException -> 0x01bf, all -> 0x01b1 }
            long r2 = (long) r2     // Catch:{ SQLException -> 0x01bf, all -> 0x01b1 }
            r0.schedule(r1, r2)     // Catch:{ SQLException -> 0x01bf, all -> 0x01b1 }
            r3 = r1
            goto L_0x01fc
        L_0x01fb:
            r3 = r1
        L_0x01fc:
            r39.statementBegins()     // Catch:{ SQLException -> 0x044c, all -> 0x043d }
            r2 = 23
            r0 = 0
            r16 = 0
            r17 = 0
            r20 = 0
            r1 = r14
            r9 = r3
            r3 = r0
            r28 = r4
            r4 = r13
            r29 = r5
            r5 = r16
            r30 = r6
            r6 = r17
            r31 = r7
            r7 = r20
            com.mysql.jdbc.Buffer r17 = r1.sendCommand(r2, r3, r4, r5, r6, r7)     // Catch:{ SQLException -> 0x0434, all -> 0x042b }
            if (r25 == 0) goto L_0x0239
            long r0 = r14.getCurrentTimeNanosOrMillis()     // Catch:{ SQLException -> 0x022f, all -> 0x0226 }
            r2 = r0
            goto L_0x023b
        L_0x0226:
            r0 = move-exception
            r1 = r9
            r37 = r11
            r38 = r13
            r11 = r14
            goto L_0x048d
        L_0x022f:
            r0 = move-exception
            r34 = r9
            r37 = r11
            r38 = r13
            r11 = r14
            goto L_0x0477
        L_0x0239:
            r0 = r18
        L_0x023b:
            r32 = r0
            if (r9 == 0) goto L_0x0256
            r9.cancel()     // Catch:{ SQLException -> 0x022f, all -> 0x0226 }
            com.mysql.jdbc.MySQLConnection r0 = r15.connection     // Catch:{ SQLException -> 0x022f, all -> 0x0226 }
            java.util.Timer r0 = r0.getCancelTimer()     // Catch:{ SQLException -> 0x022f, all -> 0x0226 }
            r0.purge()     // Catch:{ SQLException -> 0x022f, all -> 0x0226 }
            java.sql.SQLException r0 = r9.caughtWhileCancelling     // Catch:{ SQLException -> 0x022f, all -> 0x0226 }
            if (r0 != 0) goto L_0x0253
            r0 = 0
            r34 = r0
            goto L_0x0258
        L_0x0253:
            java.sql.SQLException r0 = r9.caughtWhileCancelling     // Catch:{ SQLException -> 0x022f, all -> 0x0226 }
            throw r0     // Catch:{ SQLException -> 0x022f, all -> 0x0226 }
        L_0x0256:
            r34 = r9
        L_0x0258:
            java.lang.Object r1 = r15.cancelTimeoutMutex     // Catch:{ SQLException -> 0x0423, all -> 0x0419 }
            monitor-enter(r1)     // Catch:{ SQLException -> 0x0423, all -> 0x0419 }
            boolean r0 = r15.wasCancelled     // Catch:{ all -> 0x040a }
            if (r0 == 0) goto L_0x0280
            r0 = 0
            boolean r2 = r15.wasCancelledByTimeout     // Catch:{ all -> 0x0276 }
            if (r2 == 0) goto L_0x026b
            com.mysql.jdbc.exceptions.MySQLTimeoutException r2 = new com.mysql.jdbc.exceptions.MySQLTimeoutException     // Catch:{ all -> 0x0276 }
            r2.<init>()     // Catch:{ all -> 0x0276 }
            r0 = r2
            goto L_0x0271
        L_0x026b:
            com.mysql.jdbc.exceptions.MySQLStatementCancelledException r2 = new com.mysql.jdbc.exceptions.MySQLStatementCancelledException     // Catch:{ all -> 0x0276 }
            r2.<init>()     // Catch:{ all -> 0x0276 }
            r0 = r2
        L_0x0271:
            r39.resetCancelledState()     // Catch:{ all -> 0x0276 }
            throw r0     // Catch:{ all -> 0x0276 }
        L_0x0276:
            r0 = move-exception
            r37 = r11
            r38 = r13
            r11 = r14
            r2 = r28
            goto L_0x0412
        L_0x0280:
            monitor-exit(r1)     // Catch:{ all -> 0x040a }
            if (r25 == 0) goto L_0x0285
            long r18 = r32 - r26
        L_0x0285:
            r6 = r18
            r0 = 0
            if (r23 == 0) goto L_0x02fc
            boolean r1 = r15.useAutoSlowLog     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            if (r1 == 0) goto L_0x0295
            com.mysql.jdbc.MySQLConnection r1 = r15.connection     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            boolean r1 = r1.isAbonormallyLongQuery(r6)     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            goto L_0x02a3
        L_0x0295:
            com.mysql.jdbc.MySQLConnection r1 = r15.connection     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            int r1 = r1.getSlowQueryThresholdMillis()     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            long r1 = (long) r1     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            int r1 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r1 <= 0) goto L_0x02a2
            r1 = r8
            goto L_0x02a3
        L_0x02a2:
            r1 = r12
        L_0x02a3:
            r0 = r1
            if (r0 == 0) goto L_0x02e5
            com.mysql.jdbc.MySQLConnection r1 = r15.connection     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            com.mysql.jdbc.profiler.ProfilerEventHandler r1 = r1.getProfilerEventHandlerInstance()     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            r2 = 6
            com.mysql.jdbc.MySQLConnection r3 = r15.connection     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            java.lang.Throwable r9 = new java.lang.Throwable     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            r9.<init>()     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            java.lang.String r4 = "ServerPreparedStatement.15"
            java.lang.String[] r10 = new java.lang.String[r10]     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            long r18 = r14.getSlowQueryThreshold()     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            java.lang.String r16 = java.lang.String.valueOf(r18)     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            r10[r12] = r16     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            java.lang.String r16 = java.lang.String.valueOf(r6)     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            r10[r8] = r16     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            java.lang.String r8 = r15.originalSql     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            r16 = 2
            r10[r16] = r8     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            r8 = 3
            r5 = r28
            r10[r8] = r5     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            java.lang.String r10 = com.mysql.jdbc.Messages.getString(r4, r10)     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            r4 = r39
            r8 = r5
            r5 = 0
            r35 = r6
            r37 = r8
            r8 = r9
            r9 = r10
            r1.processEvent(r2, r3, r4, r5, r6, r8, r9)     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            goto L_0x0300
        L_0x02e5:
            r35 = r6
            r37 = r28
            goto L_0x0300
        L_0x02ea:
            r0 = move-exception
            r37 = r11
            r38 = r13
            r11 = r14
            r1 = r34
            goto L_0x048d
        L_0x02f4:
            r0 = move-exception
            r37 = r11
            r38 = r13
            r11 = r14
            goto L_0x0477
        L_0x02fc:
            r35 = r6
            r37 = r28
        L_0x0300:
            if (r24 == 0) goto L_0x030f
            com.mysql.jdbc.MySQLConnection r1 = r15.connection     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            r9 = r35
            r1.registerQueryExecutionTime(r9)     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            com.mysql.jdbc.MySQLConnection r1 = r15.connection     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            r1.incrementNumberOfPreparedExecutes()     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            goto L_0x0311
        L_0x030f:
            r9 = r35
        L_0x0311:
            boolean r1 = r15.profileSQL     // Catch:{ SQLException -> 0x0423, all -> 0x0419 }
            if (r1 == 0) goto L_0x033c
            com.mysql.jdbc.MySQLConnection r1 = r15.connection     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            com.mysql.jdbc.profiler.ProfilerEventHandler r1 = r1.getProfilerEventHandlerInstance()     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            r2 = 4
            com.mysql.jdbc.MySQLConnection r3 = r15.connection     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            r5 = 0
            long r6 = r14.getCurrentTimeNanosOrMillis()     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            long r6 = r6 - r26
            java.lang.Throwable r8 = new java.lang.Throwable     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            r8.<init>()     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            r4 = r37
            java.lang.String r16 = r15.truncateQueryToLog(r4)     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            r28 = r4
            r4 = r39
            r35 = r9
            r9 = r16
            r1.processEvent(r2, r3, r4, r5, r6, r8, r9)     // Catch:{ SQLException -> 0x02f4, all -> 0x02ea }
            goto L_0x0340
        L_0x033c:
            r35 = r9
            r28 = r37
        L_0x0340:
            int r1 = r15.resultSetType     // Catch:{ SQLException -> 0x0423, all -> 0x0419 }
            int r2 = r15.resultSetConcurrency     // Catch:{ SQLException -> 0x0423, all -> 0x0419 }
            java.lang.String r3 = r15.currentCatalog     // Catch:{ SQLException -> 0x0423, all -> 0x0419 }
            r18 = 1
            int r4 = r15.fieldCount     // Catch:{ SQLException -> 0x0423, all -> 0x0419 }
            long r4 = (long) r4
            r10 = r14
            r37 = r11
            r11 = r39
            r9 = r12
            r12 = r40
            r38 = r13
            r13 = r1
            r8 = r14
            r14 = r2
            r7 = r15
            r15 = r41
            r16 = r3
            r19 = r4
            r21 = r42
            com.mysql.jdbc.ResultSetImpl r4 = r10.readAllResults(r11, r12, r13, r14, r15, r16, r17, r18, r19, r21)     // Catch:{ SQLException -> 0x0404, all -> 0x03fc }
            boolean r1 = r8.shouldIntercept()     // Catch:{ SQLException -> 0x0404, all -> 0x03fc }
            if (r1 == 0) goto L_0x0389
            java.lang.String r2 = r7.originalSql     // Catch:{ SQLException -> 0x0383, all -> 0x037b }
            r5 = 1
            r6 = 0
            r1 = r8
            r3 = r39
            com.mysql.jdbc.ResultSetInternalMethods r1 = r1.invokeStatementInterceptorsPost(r2, r3, r4, r5, r6)     // Catch:{ SQLException -> 0x0383, all -> 0x037b }
            if (r1 == 0) goto L_0x0389
            r2 = r1
            r10 = r2
            goto L_0x038a
        L_0x037b:
            r0 = move-exception
            r15 = r7
            r11 = r8
            r12 = r9
            r1 = r34
            goto L_0x048d
        L_0x0383:
            r0 = move-exception
            r15 = r7
            r11 = r8
            r12 = r9
            goto L_0x0477
        L_0x0389:
            r10 = r4
        L_0x038a:
            boolean r1 = r7.profileSQL     // Catch:{ SQLException -> 0x0404, all -> 0x03fc }
            if (r1 == 0) goto L_0x03b0
            com.mysql.jdbc.MySQLConnection r1 = r7.connection     // Catch:{ SQLException -> 0x0404, all -> 0x03fc }
            com.mysql.jdbc.profiler.ProfilerEventHandler r1 = r1.getProfilerEventHandlerInstance()     // Catch:{ SQLException -> 0x0404, all -> 0x03fc }
            r2 = 5
            com.mysql.jdbc.MySQLConnection r3 = r7.connection     // Catch:{ SQLException -> 0x0404, all -> 0x03fc }
            r5 = 0
            long r11 = r8.getCurrentTimeNanosOrMillis()     // Catch:{ SQLException -> 0x0404, all -> 0x03fc }
            long r11 = r11 - r32
            java.lang.Throwable r13 = new java.lang.Throwable     // Catch:{ SQLException -> 0x0404, all -> 0x03fc }
            r13.<init>()     // Catch:{ SQLException -> 0x0404, all -> 0x03fc }
            r14 = 0
            r4 = r39
            r15 = r7
            r6 = r11
            r11 = r8
            r8 = r13
            r12 = r9
            r9 = r14
            r1.processEvent(r2, r3, r4, r5, r6, r8, r9)     // Catch:{ SQLException -> 0x0414 }
            goto L_0x03b3
        L_0x03b0:
            r15 = r7
            r11 = r8
            r12 = r9
        L_0x03b3:
            if (r0 == 0) goto L_0x03c7
            com.mysql.jdbc.MySQLConnection r1 = r15.connection     // Catch:{ SQLException -> 0x0414 }
            boolean r1 = r1.getExplainSlowQueries()     // Catch:{ SQLException -> 0x0414 }
            if (r1 == 0) goto L_0x03c7
            byte[] r1 = com.mysql.jdbc.StringUtils.getBytes((java.lang.String) r28)     // Catch:{ SQLException -> 0x0414 }
            r2 = r28
            r11.explainSlowQuery(r1, r2)     // Catch:{ SQLException -> 0x0414 }
            goto L_0x03c9
        L_0x03c7:
            r2 = r28
        L_0x03c9:
            if (r41 != 0) goto L_0x03d2
            boolean r1 = r15.serverNeedsResetBeforeEachExecution     // Catch:{ SQLException -> 0x0414 }
            if (r1 == 0) goto L_0x03d2
            r39.serverResetStatement()     // Catch:{ SQLException -> 0x0414 }
        L_0x03d2:
            r15.sendTypesToServer = r12     // Catch:{ SQLException -> 0x0414 }
            r15.results = r10     // Catch:{ SQLException -> 0x0414 }
            boolean r1 = r11.hadWarnings()     // Catch:{ SQLException -> 0x0414 }
            if (r1 == 0) goto L_0x03df
            r11.scanForAndThrowDataTruncation()     // Catch:{ SQLException -> 0x0414 }
        L_0x03df:
            java.util.concurrent.atomic.AtomicBoolean r1 = r15.statementExecuting     // Catch:{ all -> 0x04a3 }
            r1.set(r12)     // Catch:{ all -> 0x04a3 }
            if (r34 == 0) goto L_0x03f3
            r34.cancel()     // Catch:{ all -> 0x04a3 }
            com.mysql.jdbc.MySQLConnection r1 = r15.connection     // Catch:{ all -> 0x04a3 }
            java.util.Timer r1 = r1.getCancelTimer()     // Catch:{ all -> 0x04a3 }
            r1.purge()     // Catch:{ all -> 0x04a3 }
        L_0x03f3:
            r1 = r10
            r3 = r32
            r5 = r17
            r6 = r35
            monitor-exit(r22)     // Catch:{ all -> 0x04a3 }
            return r10
        L_0x03fc:
            r0 = move-exception
            r15 = r7
            r11 = r8
            r12 = r9
            r1 = r34
            goto L_0x048d
        L_0x0404:
            r0 = move-exception
            r15 = r7
            r11 = r8
            r12 = r9
            goto L_0x0477
        L_0x040a:
            r0 = move-exception
            r37 = r11
            r38 = r13
            r11 = r14
            r2 = r28
        L_0x0412:
            monitor-exit(r1)     // Catch:{ all -> 0x0417 }
            throw r0     // Catch:{ SQLException -> 0x0414 }
        L_0x0414:
            r0 = move-exception
            goto L_0x0477
        L_0x0417:
            r0 = move-exception
            goto L_0x0412
        L_0x0419:
            r0 = move-exception
            r37 = r11
            r38 = r13
            r11 = r14
            r1 = r34
            goto L_0x048d
        L_0x0423:
            r0 = move-exception
            r37 = r11
            r38 = r13
            r11 = r14
            goto L_0x0477
        L_0x042b:
            r0 = move-exception
            r37 = r11
            r38 = r13
            r11 = r14
            r1 = r9
            goto L_0x048d
        L_0x0434:
            r0 = move-exception
            r37 = r11
            r38 = r13
            r11 = r14
            r34 = r9
            goto L_0x0477
        L_0x043d:
            r0 = move-exception
            r9 = r3
            r29 = r5
            r30 = r6
            r31 = r7
            r37 = r11
            r38 = r13
            r11 = r14
            r1 = r9
            goto L_0x048d
        L_0x044c:
            r0 = move-exception
            r9 = r3
            r29 = r5
            r30 = r6
            r31 = r7
            r37 = r11
            r38 = r13
            r11 = r14
            r34 = r9
            goto L_0x0477
        L_0x045c:
            r0 = move-exception
            r29 = r5
            r30 = r6
            r31 = r7
            r37 = r11
            r38 = r13
            r11 = r14
            goto L_0x048d
        L_0x0469:
            r0 = move-exception
            r29 = r5
            r30 = r6
            r31 = r7
            r37 = r11
            r38 = r13
            r11 = r14
            r34 = r1
        L_0x0477:
            boolean r1 = r11.shouldIntercept()     // Catch:{ all -> 0x048a }
            if (r1 == 0) goto L_0x0488
            java.lang.String r2 = r15.originalSql     // Catch:{ all -> 0x048a }
            r4 = 0
            r5 = 1
            r1 = r11
            r3 = r39
            r6 = r0
            r1.invokeStatementInterceptorsPost(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x048a }
        L_0x0488:
            throw r0     // Catch:{ all -> 0x048a }
        L_0x048a:
            r0 = move-exception
            r1 = r34
        L_0x048d:
            java.util.concurrent.atomic.AtomicBoolean r2 = r15.statementExecuting     // Catch:{ all -> 0x04a3 }
            r2.set(r12)     // Catch:{ all -> 0x04a3 }
            if (r1 == 0) goto L_0x04a0
            r1.cancel()     // Catch:{ all -> 0x04a3 }
            com.mysql.jdbc.MySQLConnection r2 = r15.connection     // Catch:{ all -> 0x04a3 }
            java.util.Timer r2 = r2.getCancelTimer()     // Catch:{ all -> 0x04a3 }
            r2.purge()     // Catch:{ all -> 0x04a3 }
        L_0x04a0:
            throw r0     // Catch:{ all -> 0x04a3 }
        L_0x04a3:
            r0 = move-exception
            monitor-exit(r22)     // Catch:{ all -> 0x04a3 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ServerPreparedStatement.serverExecute(int, boolean, com.mysql.jdbc.Field[]):com.mysql.jdbc.ResultSetInternalMethods");
    }

    private void serverLongData(int parameterIndex, BindValue longData) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            MysqlIO mysql = this.connection.getIO();
            Buffer packet = mysql.getSharedSendPacket();
            Object value = longData.value;
            if (value instanceof byte[]) {
                packet.clear();
                packet.writeByte((byte) 24);
                packet.writeLong(this.serverStatementId);
                packet.writeInt(parameterIndex);
                packet.writeBytesNoNull((byte[]) longData.value);
                mysql.sendCommand(24, (String) null, packet, true, (String) null, 0);
            } else if (value instanceof InputStream) {
                storeStream(mysql, parameterIndex, packet, (InputStream) value);
            } else if (value instanceof Blob) {
                storeStream(mysql, parameterIndex, packet, ((Blob) value).getBinaryStream());
            } else if (value instanceof Reader) {
                storeReader(mysql, parameterIndex, packet, (Reader) value);
            } else {
                throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.18") + value.getClass().getName() + "'", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:69:0x014d A[Catch:{ all -> 0x0173 }] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:57:0x0130=Splitter:B:57:0x0130, B:72:0x0174=Splitter:B:72:0x0174} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void serverPrepare(java.lang.String r22) throws java.sql.SQLException {
        /*
            r21 = this;
            r10 = r21
            com.mysql.jdbc.MySQLConnection r0 = r21.checkClosed()
            java.lang.Object r11 = r0.getConnectionMutex()
            monitor-enter(r11)
            com.mysql.jdbc.MySQLConnection r0 = r10.connection     // Catch:{ all -> 0x017f }
            com.mysql.jdbc.MysqlIO r0 = r0.getIO()     // Catch:{ all -> 0x017f }
            r12 = r0
            com.mysql.jdbc.MySQLConnection r0 = r10.connection     // Catch:{ all -> 0x017f }
            boolean r0 = r0.getAutoGenerateTestcaseScript()     // Catch:{ all -> 0x017f }
            if (r0 == 0) goto L_0x001d
            r21.dumpPrepareForTestcase()     // Catch:{ all -> 0x017f }
        L_0x001d:
            com.mysql.jdbc.MySQLConnection r0 = r10.connection     // Catch:{ SQLException -> 0x0142, all -> 0x013e }
            boolean r0 = r0.getProfileSql()     // Catch:{ SQLException -> 0x0142, all -> 0x013e }
            if (r0 == 0) goto L_0x002b
            long r0 = java.lang.System.currentTimeMillis()     // Catch:{ SQLException -> 0x0142, all -> 0x013e }
            r2 = r0
            goto L_0x002d
        L_0x002b:
            r0 = 0
        L_0x002d:
            r13 = r0
            java.lang.String r0 = "LOAD DATA"
            r15 = r22
            boolean r0 = com.mysql.jdbc.StringUtils.startsWithIgnoreCaseAndWs((java.lang.String) r15, (java.lang.String) r0)     // Catch:{ SQLException -> 0x013c }
            r10.isLoadDataQuery = r0     // Catch:{ SQLException -> 0x013c }
            r0 = 0
            com.mysql.jdbc.MySQLConnection r1 = r10.connection     // Catch:{ SQLException -> 0x013c }
            java.lang.String r1 = r1.getEncoding()     // Catch:{ SQLException -> 0x013c }
            r16 = r1
            boolean r1 = r10.isLoadDataQuery     // Catch:{ SQLException -> 0x013c }
            if (r1 != 0) goto L_0x0051
            com.mysql.jdbc.MySQLConnection r1 = r10.connection     // Catch:{ SQLException -> 0x013c }
            boolean r1 = r1.getUseUnicode()     // Catch:{ SQLException -> 0x013c }
            if (r1 == 0) goto L_0x0051
            if (r16 == 0) goto L_0x0051
            r0 = r16
        L_0x0051:
            r2 = 22
            r4 = 0
            r5 = 0
            r7 = 0
            r1 = r12
            r3 = r22
            r6 = r0
            com.mysql.jdbc.Buffer r1 = r1.sendCommand(r2, r3, r4, r5, r6, r7)     // Catch:{ SQLException -> 0x013c }
            r9 = r1
            com.mysql.jdbc.MySQLConnection r1 = r10.connection     // Catch:{ SQLException -> 0x013c }
            r8 = 4
            r6 = 1
            boolean r1 = r1.versionMeetsMinimum(r8, r6, r6)     // Catch:{ SQLException -> 0x013c }
            r7 = 0
            if (r1 == 0) goto L_0x006e
            r9.setPosition(r6)     // Catch:{ SQLException -> 0x013c }
            goto L_0x0071
        L_0x006e:
            r9.setPosition(r7)     // Catch:{ SQLException -> 0x013c }
        L_0x0071:
            long r1 = r9.readLong()     // Catch:{ SQLException -> 0x013c }
            r10.serverStatementId = r1     // Catch:{ SQLException -> 0x013c }
            int r1 = r9.readInt()     // Catch:{ SQLException -> 0x013c }
            r10.fieldCount = r1     // Catch:{ SQLException -> 0x013c }
            int r1 = r9.readInt()     // Catch:{ SQLException -> 0x013c }
            r10.parameterCount = r1     // Catch:{ SQLException -> 0x013c }
            int r1 = r10.parameterCount     // Catch:{ SQLException -> 0x013c }
            com.mysql.jdbc.ServerPreparedStatement$BindValue[] r1 = new com.mysql.jdbc.ServerPreparedStatement.BindValue[r1]     // Catch:{ SQLException -> 0x013c }
            r10.parameterBindings = r1     // Catch:{ SQLException -> 0x013c }
            r1 = 0
        L_0x008a:
            int r2 = r10.parameterCount     // Catch:{ SQLException -> 0x013c }
            if (r1 >= r2) goto L_0x009a
            com.mysql.jdbc.ServerPreparedStatement$BindValue[] r2 = r10.parameterBindings     // Catch:{ SQLException -> 0x013c }
            com.mysql.jdbc.ServerPreparedStatement$BindValue r3 = new com.mysql.jdbc.ServerPreparedStatement$BindValue     // Catch:{ SQLException -> 0x013c }
            r3.<init>()     // Catch:{ SQLException -> 0x013c }
            r2[r1] = r3     // Catch:{ SQLException -> 0x013c }
            int r1 = r1 + 1
            goto L_0x008a
        L_0x009a:
            com.mysql.jdbc.MySQLConnection r1 = r10.connection     // Catch:{ SQLException -> 0x013c }
            r1.incrementNumberOfPrepares()     // Catch:{ SQLException -> 0x013c }
            boolean r1 = r10.profileSQL     // Catch:{ SQLException -> 0x013c }
            if (r1 == 0) goto L_0x00ca
            com.mysql.jdbc.MySQLConnection r1 = r10.connection     // Catch:{ SQLException -> 0x013c }
            com.mysql.jdbc.profiler.ProfilerEventHandler r1 = r1.getProfilerEventHandlerInstance()     // Catch:{ SQLException -> 0x013c }
            r2 = 2
            com.mysql.jdbc.MySQLConnection r3 = r10.connection     // Catch:{ SQLException -> 0x013c }
            r5 = 0
            long r17 = r12.getCurrentTimeNanosOrMillis()     // Catch:{ SQLException -> 0x013c }
            long r17 = r17 - r13
            java.lang.Throwable r19 = new java.lang.Throwable     // Catch:{ SQLException -> 0x013c }
            r19.<init>()     // Catch:{ SQLException -> 0x013c }
            java.lang.String r20 = r21.truncateQueryToLog(r22)     // Catch:{ SQLException -> 0x013c }
            r4 = r21
            r6 = r17
            r8 = r19
            r17 = r9
            r9 = r20
            r1.processEvent(r2, r3, r4, r5, r6, r8, r9)     // Catch:{ SQLException -> 0x013c }
            goto L_0x00cc
        L_0x00ca:
            r17 = r9
        L_0x00cc:
            boolean r1 = r12.isEOFDeprecated()     // Catch:{ SQLException -> 0x013c }
            if (r1 != 0) goto L_0x00d4
            r6 = 1
            goto L_0x00d5
        L_0x00d4:
            r6 = 0
        L_0x00d5:
            r1 = r6
            int r2 = r10.parameterCount     // Catch:{ SQLException -> 0x013c }
            if (r2 <= 0) goto L_0x010d
            com.mysql.jdbc.MySQLConnection r2 = r10.connection     // Catch:{ SQLException -> 0x013c }
            r3 = 2
            r4 = 4
            r5 = 1
            boolean r2 = r2.versionMeetsMinimum(r4, r5, r3)     // Catch:{ SQLException -> 0x013c }
            if (r2 == 0) goto L_0x010d
            r2 = 5
            r3 = 0
            boolean r2 = r12.isVersion(r2, r3, r3)     // Catch:{ SQLException -> 0x013c }
            if (r2 != 0) goto L_0x010e
            int r2 = r10.parameterCount     // Catch:{ SQLException -> 0x013c }
            com.mysql.jdbc.Field[] r2 = new com.mysql.jdbc.Field[r2]     // Catch:{ SQLException -> 0x013c }
            r10.parameterFields = r2     // Catch:{ SQLException -> 0x013c }
            r2 = 0
        L_0x00f4:
            int r4 = r10.parameterCount     // Catch:{ SQLException -> 0x013c }
            if (r2 >= r4) goto L_0x0107
            com.mysql.jdbc.Buffer r4 = r12.readPacket()     // Catch:{ SQLException -> 0x013c }
            com.mysql.jdbc.Field[] r5 = r10.parameterFields     // Catch:{ SQLException -> 0x013c }
            com.mysql.jdbc.Field r6 = r12.unpackField(r4, r3)     // Catch:{ SQLException -> 0x013c }
            r5[r2] = r6     // Catch:{ SQLException -> 0x013c }
            int r2 = r2 + 1
            goto L_0x00f4
        L_0x0107:
            if (r1 == 0) goto L_0x010e
            r12.readPacket()     // Catch:{ SQLException -> 0x013c }
            goto L_0x010e
        L_0x010d:
            r3 = 0
        L_0x010e:
            int r2 = r10.fieldCount     // Catch:{ SQLException -> 0x013c }
            if (r2 <= 0) goto L_0x012f
            com.mysql.jdbc.Field[] r2 = new com.mysql.jdbc.Field[r2]     // Catch:{ SQLException -> 0x013c }
            r10.resultFields = r2     // Catch:{ SQLException -> 0x013c }
            r2 = 0
        L_0x0117:
            int r4 = r10.fieldCount     // Catch:{ SQLException -> 0x013c }
            if (r2 >= r4) goto L_0x012a
            com.mysql.jdbc.Buffer r4 = r12.readPacket()     // Catch:{ SQLException -> 0x013c }
            com.mysql.jdbc.Field[] r5 = r10.resultFields     // Catch:{ SQLException -> 0x013c }
            com.mysql.jdbc.Field r6 = r12.unpackField(r4, r3)     // Catch:{ SQLException -> 0x013c }
            r5[r2] = r6     // Catch:{ SQLException -> 0x013c }
            int r2 = r2 + 1
            goto L_0x0117
        L_0x012a:
            if (r1 == 0) goto L_0x012f
            r12.readPacket()     // Catch:{ SQLException -> 0x013c }
        L_0x012f:
            com.mysql.jdbc.MySQLConnection r0 = r10.connection     // Catch:{ all -> 0x0184 }
            com.mysql.jdbc.MysqlIO r0 = r0.getIO()     // Catch:{ all -> 0x0184 }
            r0.clearInputStream()     // Catch:{ all -> 0x0184 }
            monitor-exit(r11)     // Catch:{ all -> 0x0184 }
            return
        L_0x013c:
            r0 = move-exception
            goto L_0x0145
        L_0x013e:
            r0 = move-exception
            r15 = r22
            goto L_0x0174
        L_0x0142:
            r0 = move-exception
            r15 = r22
        L_0x0145:
            com.mysql.jdbc.MySQLConnection r1 = r10.connection     // Catch:{ all -> 0x0173 }
            boolean r1 = r1.getDumpQueriesOnException()     // Catch:{ all -> 0x0173 }
            if (r1 == 0) goto L_0x0171
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0173 }
            java.lang.String r2 = r10.originalSql     // Catch:{ all -> 0x0173 }
            int r2 = r2.length()     // Catch:{ all -> 0x0173 }
            int r2 = r2 + 32
            r1.<init>(r2)     // Catch:{ all -> 0x0173 }
            java.lang.String r2 = "\n\nQuery being prepared when exception was thrown:\n\n"
            r1.append(r2)     // Catch:{ all -> 0x0173 }
            java.lang.String r2 = r10.originalSql     // Catch:{ all -> 0x0173 }
            r1.append(r2)     // Catch:{ all -> 0x0173 }
            java.lang.String r2 = r1.toString()     // Catch:{ all -> 0x0173 }
            com.mysql.jdbc.ExceptionInterceptor r3 = r21.getExceptionInterceptor()     // Catch:{ all -> 0x0173 }
            java.sql.SQLException r2 = com.mysql.jdbc.ConnectionImpl.appendMessageToException(r0, r2, r3)     // Catch:{ all -> 0x0173 }
            r0 = r2
        L_0x0171:
            throw r0     // Catch:{ all -> 0x0173 }
        L_0x0173:
            r0 = move-exception
        L_0x0174:
            com.mysql.jdbc.MySQLConnection r1 = r10.connection     // Catch:{ all -> 0x0184 }
            com.mysql.jdbc.MysqlIO r1 = r1.getIO()     // Catch:{ all -> 0x0184 }
            r1.clearInputStream()     // Catch:{ all -> 0x0184 }
            throw r0     // Catch:{ all -> 0x0184 }
        L_0x017f:
            r0 = move-exception
            r15 = r22
        L_0x0182:
            monitor-exit(r11)     // Catch:{ all -> 0x0184 }
            throw r0
        L_0x0184:
            r0 = move-exception
            goto L_0x0182
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ServerPreparedStatement.serverPrepare(java.lang.String):void");
    }

    private String truncateQueryToLog(String sql) throws SQLException {
        String query;
        synchronized (checkClosed().getConnectionMutex()) {
            if (sql.length() > this.connection.getMaxQuerySizeToLog()) {
                StringBuilder queryBuf = new StringBuilder(this.connection.getMaxQuerySizeToLog() + 12);
                queryBuf.append(sql.substring(0, this.connection.getMaxQuerySizeToLog()));
                queryBuf.append(Messages.getString("MysqlIO.25"));
                query = queryBuf.toString();
            } else {
                query = sql;
            }
        }
        return query;
    }

    private void serverResetStatement() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            MysqlIO mysql = this.connection.getIO();
            Buffer packet = mysql.getSharedSendPacket();
            packet.clear();
            packet.writeByte((byte) 26);
            packet.writeLong(this.serverStatementId);
            try {
                mysql.sendCommand(26, (String) null, packet, !this.connection.versionMeetsMinimum(4, 1, 2), (String) null, 0);
                mysql.clearInputStream();
            } catch (SQLException sqlEx) {
                throw sqlEx;
            } catch (Exception ex) {
                SQLException sqlEx2 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
                sqlEx2.initCause(ex);
                throw sqlEx2;
            } catch (Throwable th) {
                mysql.clearInputStream();
                throw th;
            }
        }
    }

    public void setArray(int i, Array x) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (x == null) {
                setNull(parameterIndex, -2);
            } else {
                BindValue binding = getBinding(parameterIndex, true);
                resetToType(binding, MysqlDefs.FIELD_TYPE_BLOB);
                binding.value = x;
                binding.isLongData = true;
                if (this.connection.getUseStreamLengthsInPrepStmts()) {
                    binding.bindLength = (long) length;
                } else {
                    binding.bindLength = -1;
                }
            }
        }
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (x == null) {
                setNull(parameterIndex, 3);
            } else {
                BindValue binding = getBinding(parameterIndex, false);
                if (this.connection.versionMeetsMinimum(5, 0, 3)) {
                    resetToType(binding, 246);
                } else {
                    resetToType(binding, this.stringTypeCode);
                }
                binding.value = StringUtils.fixDecimalExponent(StringUtils.consistentToString(x));
            }
        }
    }

    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (x == null) {
                setNull(parameterIndex, -2);
            } else {
                BindValue binding = getBinding(parameterIndex, true);
                resetToType(binding, MysqlDefs.FIELD_TYPE_BLOB);
                binding.value = x;
                binding.isLongData = true;
                if (this.connection.getUseStreamLengthsInPrepStmts()) {
                    binding.bindLength = (long) length;
                } else {
                    binding.bindLength = -1;
                }
            }
        }
    }

    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (x == null) {
                setNull(parameterIndex, -2);
            } else {
                BindValue binding = getBinding(parameterIndex, true);
                resetToType(binding, MysqlDefs.FIELD_TYPE_BLOB);
                binding.value = x;
                binding.isLongData = true;
                if (this.connection.getUseStreamLengthsInPrepStmts()) {
                    binding.bindLength = x.length();
                } else {
                    binding.bindLength = -1;
                }
            }
        }
    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        setByte(parameterIndex, x);
    }

    public void setByte(int parameterIndex, byte x) throws SQLException {
        checkClosed();
        BindValue binding = getBinding(parameterIndex, false);
        resetToType(binding, 1);
        binding.longBinding = (long) x;
    }

    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        checkClosed();
        if (x == null) {
            setNull(parameterIndex, -2);
            return;
        }
        BindValue binding = getBinding(parameterIndex, false);
        resetToType(binding, 253);
        binding.value = x;
    }

    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (reader == null) {
                setNull(parameterIndex, -2);
            } else {
                BindValue binding = getBinding(parameterIndex, true);
                resetToType(binding, MysqlDefs.FIELD_TYPE_BLOB);
                binding.value = reader;
                binding.isLongData = true;
                if (this.connection.getUseStreamLengthsInPrepStmts()) {
                    binding.bindLength = (long) length;
                } else {
                    binding.bindLength = -1;
                }
            }
        }
    }

    public void setClob(int parameterIndex, Clob x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (x == null) {
                setNull(parameterIndex, -2);
            } else {
                BindValue binding = getBinding(parameterIndex, true);
                resetToType(binding, MysqlDefs.FIELD_TYPE_BLOB);
                binding.value = x.getCharacterStream();
                binding.isLongData = true;
                if (this.connection.getUseStreamLengthsInPrepStmts()) {
                    binding.bindLength = x.length();
                } else {
                    binding.bindLength = -1;
                }
            }
        }
    }

    public void setDate(int parameterIndex, java.sql.Date x) throws SQLException {
        setDate(parameterIndex, x, (Calendar) null);
    }

    public void setDate(int parameterIndex, java.sql.Date x, Calendar cal) throws SQLException {
        if (x == null) {
            setNull(parameterIndex, 91);
            return;
        }
        BindValue binding = getBinding(parameterIndex, false);
        resetToType(binding, 10);
        binding.value = x;
        if (cal != null) {
            binding.calendar = (Calendar) cal.clone();
        }
    }

    public void setDouble(int parameterIndex, double x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.connection.getAllowNanAndInf()) {
                if (x == Double.POSITIVE_INFINITY || x == Double.NEGATIVE_INFINITY || Double.isNaN(x)) {
                    throw SQLError.createSQLException("'" + x + "' is not a valid numeric or approximate numeric value", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                }
            }
            BindValue binding = getBinding(parameterIndex, false);
            resetToType(binding, 5);
            binding.doubleBinding = x;
        }
    }

    public void setFloat(int parameterIndex, float x) throws SQLException {
        checkClosed();
        BindValue binding = getBinding(parameterIndex, false);
        resetToType(binding, 4);
        binding.floatBinding = x;
    }

    public void setInt(int parameterIndex, int x) throws SQLException {
        checkClosed();
        BindValue binding = getBinding(parameterIndex, false);
        resetToType(binding, 3);
        binding.longBinding = (long) x;
    }

    public void setLong(int parameterIndex, long x) throws SQLException {
        checkClosed();
        BindValue binding = getBinding(parameterIndex, false);
        resetToType(binding, 8);
        binding.longBinding = x;
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        checkClosed();
        BindValue binding = getBinding(parameterIndex, false);
        resetToType(binding, 6);
        binding.isNull = true;
    }

    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        checkClosed();
        BindValue binding = getBinding(parameterIndex, false);
        resetToType(binding, 6);
        binding.isNull = true;
    }

    public void setRef(int i, Ref x) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    public void setShort(int parameterIndex, short x) throws SQLException {
        checkClosed();
        BindValue binding = getBinding(parameterIndex, false);
        resetToType(binding, 2);
        binding.longBinding = (long) x;
    }

    public void setString(int parameterIndex, String x) throws SQLException {
        checkClosed();
        if (x == null) {
            setNull(parameterIndex, 1);
            return;
        }
        BindValue binding = getBinding(parameterIndex, false);
        resetToType(binding, this.stringTypeCode);
        binding.value = x;
    }

    public void setTime(int parameterIndex, Time x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            setTimeInternal(parameterIndex, x, (Calendar) null, this.connection.getDefaultTimeZone(), false);
        }
    }

    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            setTimeInternal(parameterIndex, x, cal, cal.getTimeZone(), true);
        }
    }

    private void setTimeInternal(int parameterIndex, Time x, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
        if (x == null) {
            setNull(parameterIndex, 92);
            return;
        }
        BindValue binding = getBinding(parameterIndex, false);
        resetToType(binding, 11);
        if (!this.useLegacyDatetimeCode) {
            binding.value = x;
            if (targetCalendar != null) {
                binding.calendar = (Calendar) targetCalendar.clone();
                return;
            }
            return;
        }
        Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
        binding.value = TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
    }

    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            setTimestampInternal(parameterIndex, x, (Calendar) null, this.connection.getDefaultTimeZone(), false);
        }
    }

    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            setTimestampInternal(parameterIndex, x, cal, cal.getTimeZone(), true);
        }
    }

    private void setTimestampInternal(int parameterIndex, Timestamp x, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
        if (x == null) {
            setNull(parameterIndex, 93);
            return;
        }
        BindValue binding = getBinding(parameterIndex, false);
        resetToType(binding, 12);
        if (!this.sendFractionalSeconds) {
            x = TimeUtil.truncateFractionalSeconds(x);
        }
        if (!this.useLegacyDatetimeCode) {
            binding.value = x;
        } else {
            binding.value = TimeUtil.changeTimezone(this.connection, TimeUtil.setProlepticIfNeeded(this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew(), targetCalendar), targetCalendar, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
        }
        if (targetCalendar != null) {
            binding.calendar = (Calendar) targetCalendar.clone();
        }
    }

    /* access modifiers changed from: protected */
    public void resetToType(BindValue oldValue, int bufferType) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            oldValue.reset();
            if (bufferType != 6 || oldValue.bufferType == 0) {
                if (oldValue.bufferType != bufferType) {
                    this.sendTypesToServer = true;
                    oldValue.bufferType = bufferType;
                }
            }
            oldValue.isSet = true;
            oldValue.boundBeforeExecutionNum = (long) this.numberOfExecutions;
        }
    }

    @Deprecated
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        checkClosed();
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    public void setURL(int parameterIndex, URL x) throws SQLException {
        checkClosed();
        setString(parameterIndex, x.toString());
    }

    private void storeBinding(Buffer packet, BindValue bindValue, MysqlIO mysql) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            try {
                Object value = bindValue.value;
                switch (bindValue.bufferType) {
                    case 0:
                    case 15:
                    case 246:
                    case 253:
                    case 254:
                        if (value instanceof byte[]) {
                            packet.writeLenBytes((byte[]) value);
                        } else if (!this.isLoadDataQuery) {
                            packet.writeLenString((String) value, this.charEncoding, this.connection.getServerCharset(), this.charConverter, this.connection.parserKnowsUnicode(), this.connection);
                        } else {
                            packet.writeLenBytes(StringUtils.getBytes((String) value));
                        }
                        return;
                    case 1:
                        packet.writeByte((byte) ((int) bindValue.longBinding));
                        return;
                    case 2:
                        packet.ensureCapacity(2);
                        packet.writeInt((int) bindValue.longBinding);
                        return;
                    case 3:
                        packet.ensureCapacity(4);
                        packet.writeLong((long) ((int) bindValue.longBinding));
                        return;
                    case 4:
                        packet.ensureCapacity(4);
                        packet.writeFloat(bindValue.floatBinding);
                        return;
                    case 5:
                        packet.ensureCapacity(8);
                        packet.writeDouble(bindValue.doubleBinding);
                        return;
                    case 7:
                    case 10:
                    case 12:
                        storeDateTime(packet, (Date) value, mysql, bindValue.bufferType, bindValue.calendar);
                        return;
                    case 8:
                        packet.ensureCapacity(8);
                        packet.writeLongLong(bindValue.longBinding);
                        return;
                    case 11:
                        storeTime(packet, (Time) value);
                        return;
                    default:
                        return;
                }
            } catch (UnsupportedEncodingException e) {
                throw SQLError.createSQLException(Messages.getString("ServerPreparedStatement.22") + this.connection.getEncoding() + "'", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: finally extract failed */
    private void storeDateTime412AndOlder(Buffer intoBuf, Date dt, int bufferType) throws SQLException {
        Calendar sessionCalendar;
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.useLegacyDatetimeCode) {
                sessionCalendar = (!(dt instanceof Timestamp) || !this.connection.getUseJDBCCompliantTimezoneShift()) ? getCalendarInstanceForSessionOrNew() : this.connection.getUtcCalendar();
            } else if (bufferType == 10) {
                sessionCalendar = getDefaultTzCalendar();
            } else {
                sessionCalendar = getServerTzCalendar();
            }
            Date oldTime = sessionCalendar.getTime();
            try {
                intoBuf.ensureCapacity(8);
                intoBuf.writeByte((byte) 7);
                sessionCalendar.setTime(dt);
                int year = sessionCalendar.get(1);
                int date = sessionCalendar.get(5);
                intoBuf.writeInt(year);
                intoBuf.writeByte((byte) (sessionCalendar.get(2) + 1));
                intoBuf.writeByte((byte) date);
                if (dt instanceof java.sql.Date) {
                    intoBuf.writeByte((byte) 0);
                    intoBuf.writeByte((byte) 0);
                    intoBuf.writeByte((byte) 0);
                } else {
                    intoBuf.writeByte((byte) sessionCalendar.get(11));
                    intoBuf.writeByte((byte) sessionCalendar.get(12));
                    intoBuf.writeByte((byte) sessionCalendar.get(13));
                }
                sessionCalendar.setTime(oldTime);
            } catch (Throwable th) {
                sessionCalendar.setTime(oldTime);
                throw th;
            }
        }
    }

    private void storeDateTime(Buffer intoBuf, Date dt, MysqlIO mysql, int bufferType, Calendar cal) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.connection.versionMeetsMinimum(4, 1, 3)) {
                storeDateTime413AndNewer(intoBuf, dt, bufferType, cal);
            } else {
                storeDateTime412AndOlder(intoBuf, dt, bufferType);
            }
        }
    }

    private void storeDateTime413AndNewer(Buffer intoBuf, Date dt, int bufferType, Calendar cal) throws SQLException {
        Calendar sessionCalendar;
        Date oldTime;
        Buffer buffer = intoBuf;
        Date date = dt;
        synchronized (checkClosed().getConnectionMutex()) {
            Calendar sessionCalendar2 = cal;
            if (cal == null) {
                try {
                    if (this.useLegacyDatetimeCode) {
                        int i = bufferType;
                        sessionCalendar = (!(date instanceof Timestamp) || !this.connection.getUseJDBCCompliantTimezoneShift()) ? getCalendarInstanceForSessionOrNew() : this.connection.getUtcCalendar();
                    } else if (bufferType == 10) {
                        sessionCalendar = getDefaultTzCalendar();
                    } else {
                        sessionCalendar = getServerTzCalendar();
                    }
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            } else {
                int i2 = bufferType;
                sessionCalendar = sessionCalendar2;
            }
            oldTime = sessionCalendar.getTime();
            sessionCalendar.setTime(date);
            if (date instanceof java.sql.Date) {
                sessionCalendar.set(11, 0);
                sessionCalendar.set(12, 0);
                sessionCalendar.set(13, 0);
            }
            byte length = 7;
            if (date instanceof Timestamp) {
                length = 11;
            }
            buffer.ensureCapacity(length);
            buffer.writeByte(length);
            int year = sessionCalendar.get(1);
            int date2 = sessionCalendar.get(5);
            buffer.writeInt(year);
            buffer.writeByte((byte) (sessionCalendar.get(2) + 1));
            buffer.writeByte((byte) date2);
            if (date instanceof java.sql.Date) {
                buffer.writeByte((byte) 0);
                buffer.writeByte((byte) 0);
                buffer.writeByte((byte) 0);
            } else {
                buffer.writeByte((byte) sessionCalendar.get(11));
                buffer.writeByte((byte) sessionCalendar.get(12));
                buffer.writeByte((byte) sessionCalendar.get(13));
            }
            if (length == 11) {
                buffer.writeLong((long) (((Timestamp) date).getNanos() / 1000));
            }
            sessionCalendar.setTime(oldTime);
        }
    }

    private Calendar getServerTzCalendar() throws SQLException {
        Calendar calendar;
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.serverTzCalendar == null) {
                this.serverTzCalendar = new GregorianCalendar(this.connection.getServerTimezoneTZ());
            }
            calendar = this.serverTzCalendar;
        }
        return calendar;
    }

    private Calendar getDefaultTzCalendar() throws SQLException {
        Calendar calendar;
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.defaultTzCalendar == null) {
                this.defaultTzCalendar = new GregorianCalendar(TimeZone.getDefault());
            }
            calendar = this.defaultTzCalendar;
        }
        return calendar;
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:71:0x019c=Splitter:B:71:0x019c, B:94:0x020c=Splitter:B:94:0x020c, B:78:0x01ad=Splitter:B:78:0x01ad, B:101:0x021f=Splitter:B:101:0x021f} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void storeReader(com.mysql.jdbc.MysqlIO r28, int r29, com.mysql.jdbc.Buffer r30, java.io.Reader r31) throws java.sql.SQLException {
        /*
            r27 = this;
            r1 = r27
            r2 = r29
            r10 = r30
            r11 = r31
            com.mysql.jdbc.MySQLConnection r0 = r27.checkClosed()
            java.lang.Object r12 = r0.getConnectionMutex()
            monitor-enter(r12)
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ all -> 0x0220 }
            java.lang.String r0 = r0.getClobCharacterEncoding()     // Catch:{ all -> 0x0220 }
            r13 = r0
            if (r13 != 0) goto L_0x0021
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ all -> 0x0220 }
            java.lang.String r0 = r0.getEncoding()     // Catch:{ all -> 0x0220 }
            goto L_0x0022
        L_0x0021:
            r0 = r13
        L_0x0022:
            r9 = r0
            r0 = 2
            if (r9 == 0) goto L_0x0043
            java.lang.String r3 = "UTF-16"
            boolean r3 = r9.equals(r3)     // Catch:{ all -> 0x0220 }
            if (r3 != 0) goto L_0x003f
            com.mysql.jdbc.MySQLConnection r3 = r1.connection     // Catch:{ all -> 0x0220 }
            int r3 = r3.getMaxBytesPerChar(r9)     // Catch:{ all -> 0x0220 }
            r0 = r3
            r3 = 1
            if (r0 != r3) goto L_0x003c
            r0 = 2
            r22 = r0
            goto L_0x0045
        L_0x003c:
            r22 = r0
            goto L_0x0045
        L_0x003f:
            r0 = 4
            r22 = r0
            goto L_0x0045
        L_0x0043:
            r22 = r0
        L_0x0045:
            r0 = 8192(0x2000, float:1.14794E-41)
            int r0 = r0 / r22
            char[] r0 = new char[r0]     // Catch:{ all -> 0x0220 }
            r8 = r0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ all -> 0x0220 }
            int r0 = r0.getBlobSendChunkSize()     // Catch:{ all -> 0x0220 }
            r7 = r0
            r30.clear()     // Catch:{ IOException -> 0x01d9, all -> 0x01d0 }
            r0 = 24
            r10.writeByte(r0)     // Catch:{ IOException -> 0x01d9, all -> 0x01d0 }
            long r14 = r1.serverStatementId     // Catch:{ IOException -> 0x01d9, all -> 0x01d0 }
            r10.writeLong(r14)     // Catch:{ IOException -> 0x01d9, all -> 0x01d0 }
            r10.writeInt(r2)     // Catch:{ IOException -> 0x01d9, all -> 0x01d0 }
            r14 = 0
            r23 = r4
            r26 = r6
            r6 = r5
            r5 = r26
        L_0x006f:
            int r4 = r11.read(r8)     // Catch:{ IOException -> 0x01c0, all -> 0x01af }
            r19 = r4
            r3 = -1
            if (r4 == r3) goto L_0x015a
            r24 = 1
            r15 = 0
            com.mysql.jdbc.MySQLConnection r3 = r1.connection     // Catch:{ IOException -> 0x0148, all -> 0x0135 }
            java.lang.String r17 = r3.getServerCharset()     // Catch:{ IOException -> 0x0148, all -> 0x0135 }
            r18 = 0
            com.mysql.jdbc.MySQLConnection r3 = r1.connection     // Catch:{ IOException -> 0x0148, all -> 0x0135 }
            boolean r20 = r3.parserKnowsUnicode()     // Catch:{ IOException -> 0x0148, all -> 0x0135 }
            com.mysql.jdbc.ExceptionInterceptor r21 = r27.getExceptionInterceptor()     // Catch:{ IOException -> 0x0148, all -> 0x0135 }
            r14 = r8
            r16 = r9
            byte[] r3 = com.mysql.jdbc.StringUtils.getBytes((char[]) r14, (com.mysql.jdbc.SingleByteCharsetConverter) r15, (java.lang.String) r16, (java.lang.String) r17, (int) r18, (int) r19, (boolean) r20, (com.mysql.jdbc.ExceptionInterceptor) r21)     // Catch:{ IOException -> 0x0148, all -> 0x0135 }
            r14 = r3
            r3 = 0
            int r4 = r14.length     // Catch:{ IOException -> 0x0148, all -> 0x0135 }
            r10.writeBytesNoNull(r14, r3, r4)     // Catch:{ IOException -> 0x0148, all -> 0x0135 }
            int r3 = r14.length     // Catch:{ IOException -> 0x0148, all -> 0x0135 }
            int r15 = r23 + r3
            int r3 = r14.length     // Catch:{ IOException -> 0x0124, all -> 0x0112 }
            int r16 = r6 + r3
            if (r15 < r7) goto L_0x00fc
            r17 = r16
            r4 = 24
            r5 = 0
            r18 = 1
            r20 = 0
            r21 = 0
            r3 = r28
            r6 = r30
            r25 = r7
            r7 = r18
            r18 = r8
            r8 = r20
            r20 = r9
            r9 = r21
            r3.sendCommand(r4, r5, r6, r7, r8, r9)     // Catch:{ IOException -> 0x00f2, all -> 0x00e7 }
            r4 = 0
            r30.clear()     // Catch:{ IOException -> 0x00de, all -> 0x00d4 }
            r10.writeByte(r0)     // Catch:{ IOException -> 0x00de, all -> 0x00d4 }
            long r5 = r1.serverStatementId     // Catch:{ IOException -> 0x00de, all -> 0x00d4 }
            r10.writeLong(r5)     // Catch:{ IOException -> 0x00de, all -> 0x00d4 }
            r10.writeInt(r2)     // Catch:{ IOException -> 0x00de, all -> 0x00d4 }
            r23 = r4
            r5 = r17
            goto L_0x0104
        L_0x00d4:
            r0 = move-exception
            r7 = r0
            r5 = r16
            r6 = r17
            r3 = r19
            goto L_0x020c
        L_0x00de:
            r0 = move-exception
            r5 = r16
            r6 = r17
            r3 = r19
            goto L_0x01e0
        L_0x00e7:
            r0 = move-exception
            r7 = r0
            r4 = r15
            r5 = r16
            r6 = r17
            r3 = r19
            goto L_0x020c
        L_0x00f2:
            r0 = move-exception
            r4 = r15
            r5 = r16
            r6 = r17
            r3 = r19
            goto L_0x01e0
        L_0x00fc:
            r25 = r7
            r18 = r8
            r20 = r9
            r23 = r15
        L_0x0104:
            r6 = r16
            r8 = r18
            r3 = r19
            r9 = r20
            r14 = r24
            r7 = r25
            goto L_0x006f
        L_0x0112:
            r0 = move-exception
            r25 = r7
            r18 = r8
            r20 = r9
            r7 = r0
            r4 = r15
            r3 = r19
            r26 = r6
            r6 = r5
            r5 = r26
            goto L_0x020c
        L_0x0124:
            r0 = move-exception
            r25 = r7
            r18 = r8
            r20 = r9
            r4 = r15
            r3 = r19
            r26 = r6
            r6 = r5
            r5 = r26
            goto L_0x01e0
        L_0x0135:
            r0 = move-exception
            r25 = r7
            r18 = r8
            r20 = r9
            r7 = r0
            r3 = r19
            r4 = r23
            r26 = r6
            r6 = r5
            r5 = r26
            goto L_0x020c
        L_0x0148:
            r0 = move-exception
            r25 = r7
            r18 = r8
            r20 = r9
            r3 = r19
            r4 = r23
            r26 = r6
            r6 = r5
            r5 = r26
            goto L_0x01e0
        L_0x015a:
            r25 = r7
            r18 = r8
            r20 = r9
            if (r6 == r5) goto L_0x0189
            r4 = 24
            r0 = 0
            r7 = 1
            r8 = 0
            r9 = 0
            r3 = r28
            r15 = r5
            r5 = r0
            r16 = r6
            r6 = r30
            r3.sendCommand(r4, r5, r6, r7, r8, r9)     // Catch:{ IOException -> 0x017f, all -> 0x0174 }
            goto L_0x018c
        L_0x0174:
            r0 = move-exception
            r7 = r0
            r6 = r15
            r5 = r16
            r3 = r19
            r4 = r23
            goto L_0x020c
        L_0x017f:
            r0 = move-exception
            r6 = r15
            r5 = r16
            r3 = r19
            r4 = r23
            goto L_0x01e0
        L_0x0189:
            r15 = r5
            r16 = r6
        L_0x018c:
            if (r14 != 0) goto L_0x019b
            r4 = 24
            r5 = 0
            r7 = 1
            r8 = 0
            r9 = 0
            r3 = r28
            r6 = r30
            r3.sendCommand(r4, r5, r6, r7, r8, r9)     // Catch:{ IOException -> 0x017f, all -> 0x0174 }
        L_0x019b:
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ all -> 0x0220 }
            boolean r0 = r0.getAutoClosePStmtStreams()     // Catch:{ all -> 0x0220 }
            if (r0 == 0) goto L_0x01ab
            if (r11 == 0) goto L_0x01ab
            r31.close()     // Catch:{ IOException -> 0x01aa }
            goto L_0x01ab
        L_0x01aa:
            r0 = move-exception
        L_0x01ab:
            monitor-exit(r12)     // Catch:{ all -> 0x0220 }
            return
        L_0x01af:
            r0 = move-exception
            r15 = r5
            r16 = r6
            r25 = r7
            r18 = r8
            r20 = r9
            r7 = r0
            r6 = r15
            r5 = r16
            r4 = r23
            goto L_0x020c
        L_0x01c0:
            r0 = move-exception
            r15 = r5
            r16 = r6
            r25 = r7
            r18 = r8
            r20 = r9
            r6 = r15
            r5 = r16
            r4 = r23
            goto L_0x01e0
        L_0x01d0:
            r0 = move-exception
            r25 = r7
            r18 = r8
            r20 = r9
            r7 = r0
            goto L_0x020c
        L_0x01d9:
            r0 = move-exception
            r25 = r7
            r18 = r8
            r20 = r9
        L_0x01e0:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x020a }
            r7.<init>()     // Catch:{ all -> 0x020a }
            java.lang.String r8 = "ServerPreparedStatement.24"
            java.lang.String r8 = com.mysql.jdbc.Messages.getString(r8)     // Catch:{ all -> 0x020a }
            java.lang.StringBuilder r7 = r7.append(r8)     // Catch:{ all -> 0x020a }
            java.lang.String r8 = r0.toString()     // Catch:{ all -> 0x020a }
            java.lang.StringBuilder r7 = r7.append(r8)     // Catch:{ all -> 0x020a }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x020a }
            java.lang.String r8 = "S1000"
            com.mysql.jdbc.ExceptionInterceptor r9 = r27.getExceptionInterceptor()     // Catch:{ all -> 0x020a }
            java.sql.SQLException r7 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r7, (java.lang.String) r8, (com.mysql.jdbc.ExceptionInterceptor) r9)     // Catch:{ all -> 0x020a }
            r7.initCause(r0)     // Catch:{ all -> 0x020a }
            throw r7     // Catch:{ all -> 0x020a }
        L_0x020a:
            r0 = move-exception
            r7 = r0
        L_0x020c:
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ all -> 0x0220 }
            boolean r0 = r0.getAutoClosePStmtStreams()     // Catch:{ all -> 0x0220 }
            if (r0 == 0) goto L_0x021c
            if (r11 == 0) goto L_0x021d
            r31.close()     // Catch:{ IOException -> 0x021a }
            goto L_0x021c
        L_0x021a:
            r0 = move-exception
            goto L_0x021d
        L_0x021c:
        L_0x021d:
            throw r7     // Catch:{ all -> 0x0220 }
        L_0x0220:
            r0 = move-exception
            monitor-exit(r12)     // Catch:{ all -> 0x0220 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ServerPreparedStatement.storeReader(com.mysql.jdbc.MysqlIO, int, com.mysql.jdbc.Buffer, java.io.Reader):void");
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:41:0x00d0=Splitter:B:41:0x00d0, B:50:0x0100=Splitter:B:50:0x0100, B:57:0x0113=Splitter:B:57:0x0113, B:34:0x00bf=Splitter:B:34:0x00bf} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void storeStream(com.mysql.jdbc.MysqlIO r22, int r23, com.mysql.jdbc.Buffer r24, java.io.InputStream r25) throws java.sql.SQLException {
        /*
            r21 = this;
            r1 = r21
            r2 = r23
            r10 = r24
            r11 = r25
            com.mysql.jdbc.MySQLConnection r0 = r21.checkClosed()
            java.lang.Object r12 = r0.getConnectionMutex()
            monitor-enter(r12)
            r0 = 8192(0x2000, float:1.14794E-41)
            byte[] r0 = new byte[r0]     // Catch:{ all -> 0x0114 }
            r13 = r0
            r3 = 0
            r0 = 0
            r4 = 0
            r5 = 0
            com.mysql.jdbc.MySQLConnection r6 = r1.connection     // Catch:{ IOException -> 0x00d5 }
            int r6 = r6.getBlobSendChunkSize()     // Catch:{ IOException -> 0x00d5 }
            r14 = r6
            r24.clear()     // Catch:{ IOException -> 0x00d5 }
            r15 = 24
            r10.writeByte(r15)     // Catch:{ IOException -> 0x00d5 }
            long r6 = r1.serverStatementId     // Catch:{ IOException -> 0x00d5 }
            r10.writeLong(r6)     // Catch:{ IOException -> 0x00d5 }
            r10.writeInt(r2)     // Catch:{ IOException -> 0x00d5 }
            r6 = 0
            r9 = r4
            r8 = r5
            r16 = r6
        L_0x0036:
            int r4 = r11.read(r13)     // Catch:{ IOException -> 0x00d5 }
            r7 = r4
            r3 = -1
            if (r4 == r3) goto L_0x008a
            r16 = 1
            r3 = 0
            r10.writeBytesNoNull(r13, r3, r7)     // Catch:{ IOException -> 0x0084, all -> 0x007c }
            int r0 = r0 + r7
            int r17 = r9 + r7
            if (r0 < r14) goto L_0x0075
            r18 = r17
            r4 = 24
            r5 = 0
            r8 = 1
            r9 = 0
            r19 = 0
            r3 = r22
            r6 = r24
            r20 = r7
            r7 = r8
            r8 = r9
            r9 = r19
            r3.sendCommand(r4, r5, r6, r7, r8, r9)     // Catch:{ IOException -> 0x00a8, all -> 0x00a3 }
            r0 = 0
            r24.clear()     // Catch:{ IOException -> 0x00a8, all -> 0x00a3 }
            r10.writeByte(r15)     // Catch:{ IOException -> 0x00a8, all -> 0x00a3 }
            long r3 = r1.serverStatementId     // Catch:{ IOException -> 0x00a8, all -> 0x00a3 }
            r10.writeLong(r3)     // Catch:{ IOException -> 0x00a8, all -> 0x00a3 }
            r10.writeInt(r2)     // Catch:{ IOException -> 0x00a8, all -> 0x00a3 }
            r9 = r17
            r8 = r18
            r3 = r20
            goto L_0x0036
        L_0x0075:
            r20 = r7
            r9 = r17
            r3 = r20
            goto L_0x0036
        L_0x007c:
            r0 = move-exception
            r20 = r7
            r4 = r0
            r3 = r20
            goto L_0x0100
        L_0x0084:
            r0 = move-exception
            r20 = r7
            r3 = r20
            goto L_0x00d6
        L_0x008a:
            r20 = r7
            if (r9 == r8) goto L_0x00ac
            r4 = 24
            r5 = 0
            r7 = 1
            r15 = 0
            r17 = 0
            r3 = r22
            r6 = r24
            r18 = r8
            r8 = r15
            r15 = r9
            r9 = r17
            r3.sendCommand(r4, r5, r6, r7, r8, r9)     // Catch:{ IOException -> 0x00a8, all -> 0x00a3 }
            goto L_0x00af
        L_0x00a3:
            r0 = move-exception
            r4 = r0
            r3 = r20
            goto L_0x0100
        L_0x00a8:
            r0 = move-exception
            r3 = r20
            goto L_0x00d6
        L_0x00ac:
            r18 = r8
            r15 = r9
        L_0x00af:
            if (r16 != 0) goto L_0x00be
            r4 = 24
            r5 = 0
            r7 = 1
            r8 = 0
            r9 = 0
            r3 = r22
            r6 = r24
            r3.sendCommand(r4, r5, r6, r7, r8, r9)     // Catch:{ IOException -> 0x00a8, all -> 0x00a3 }
        L_0x00be:
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ all -> 0x0114 }
            boolean r0 = r0.getAutoClosePStmtStreams()     // Catch:{ all -> 0x0114 }
            if (r0 == 0) goto L_0x00ce
            if (r11 == 0) goto L_0x00ce
            r25.close()     // Catch:{ IOException -> 0x00cd }
            goto L_0x00ce
        L_0x00cd:
            r0 = move-exception
        L_0x00ce:
            monitor-exit(r12)     // Catch:{ all -> 0x0114 }
            return
        L_0x00d2:
            r0 = move-exception
            r4 = r0
            goto L_0x0100
        L_0x00d5:
            r0 = move-exception
        L_0x00d6:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d2 }
            r4.<init>()     // Catch:{ all -> 0x00d2 }
            java.lang.String r5 = "ServerPreparedStatement.25"
            java.lang.String r5 = com.mysql.jdbc.Messages.getString(r5)     // Catch:{ all -> 0x00d2 }
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch:{ all -> 0x00d2 }
            java.lang.String r5 = r0.toString()     // Catch:{ all -> 0x00d2 }
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch:{ all -> 0x00d2 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00d2 }
            java.lang.String r5 = "S1000"
            com.mysql.jdbc.ExceptionInterceptor r6 = r21.getExceptionInterceptor()     // Catch:{ all -> 0x00d2 }
            java.sql.SQLException r4 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r4, (java.lang.String) r5, (com.mysql.jdbc.ExceptionInterceptor) r6)     // Catch:{ all -> 0x00d2 }
            r4.initCause(r0)     // Catch:{ all -> 0x00d2 }
            throw r4     // Catch:{ all -> 0x00d2 }
        L_0x0100:
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ all -> 0x0114 }
            boolean r0 = r0.getAutoClosePStmtStreams()     // Catch:{ all -> 0x0114 }
            if (r0 == 0) goto L_0x0110
            if (r11 == 0) goto L_0x0111
            r25.close()     // Catch:{ IOException -> 0x010e }
            goto L_0x0110
        L_0x010e:
            r0 = move-exception
            goto L_0x0111
        L_0x0110:
        L_0x0111:
            throw r4     // Catch:{ all -> 0x0114 }
        L_0x0114:
            r0 = move-exception
            monitor-exit(r12)     // Catch:{ all -> 0x0114 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ServerPreparedStatement.storeStream(com.mysql.jdbc.MysqlIO, int, com.mysql.jdbc.Buffer, java.io.InputStream):void");
    }

    public String toString() {
        StringBuilder toStringBuf = new StringBuilder();
        toStringBuf.append("com.mysql.jdbc.ServerPreparedStatement[");
        toStringBuf.append(this.serverStatementId);
        toStringBuf.append("] - ");
        try {
            toStringBuf.append(asSql());
        } catch (SQLException sqlEx) {
            toStringBuf.append(Messages.getString("ServerPreparedStatement.6"));
            toStringBuf.append(sqlEx);
        }
        return toStringBuf.toString();
    }

    /* access modifiers changed from: protected */
    public long getServerStatementId() {
        return this.serverStatementId;
    }

    public boolean canRewriteAsMultiValueInsertAtSqlLevel() throws SQLException {
        boolean z;
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.hasCheckedRewrite) {
                this.hasCheckedRewrite = true;
                this.canRewrite = canRewrite(this.originalSql, isOnDuplicateKeyUpdate(), getLocationOfOnDuplicateKeyUpdate(), 0);
                this.parseInfo = new PreparedStatement.ParseInfo(this.originalSql, this.connection, this.connection.getMetaData(), this.charEncoding, this.charConverter);
            }
            z = this.canRewrite;
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public int getLocationOfOnDuplicateKeyUpdate() throws SQLException {
        int i;
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.locationOfOnDuplicateKeyUpdate == -2) {
                this.locationOfOnDuplicateKeyUpdate = getOnDuplicateKeyLocation(this.originalSql, this.connection.getDontCheckOnDuplicateKeyUpdateInSQL(), this.connection.getRewriteBatchedStatements(), this.connection.isNoBackslashEscapesSet());
            }
            i = this.locationOfOnDuplicateKeyUpdate;
        }
        return i;
    }

    /* access modifiers changed from: protected */
    public boolean isOnDuplicateKeyUpdate() throws SQLException {
        boolean z;
        synchronized (checkClosed().getConnectionMutex()) {
            z = getLocationOfOnDuplicateKeyUpdate() != -1;
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public long[] computeMaxParameterSetSizeAndBatchSize(int numBatchedArgs) throws SQLException {
        long[] jArr;
        synchronized (checkClosed().getConnectionMutex()) {
            long sizeOfEntireBatch = 10;
            long maxSizeOfParameterSet = 0;
            for (int i = 0; i < numBatchedArgs; i++) {
                BindValue[] paramArg = ((BatchedBindValues) this.batchedArgs.get(i)).batchedParameterValues;
                long sizeOfParameterSet = 0 + ((long) ((this.parameterCount + 7) / 8)) + ((long) (this.parameterCount * 2));
                for (int j = 0; j < this.parameterBindings.length; j++) {
                    if (!paramArg[j].isNull) {
                        long size = paramArg[j].getBoundLength();
                        if (!paramArg[j].isLongData) {
                            sizeOfParameterSet += size;
                        } else if (size != -1) {
                            sizeOfParameterSet += size;
                        }
                    }
                }
                sizeOfEntireBatch += sizeOfParameterSet;
                if (sizeOfParameterSet > maxSizeOfParameterSet) {
                    maxSizeOfParameterSet = sizeOfParameterSet;
                }
            }
            jArr = new long[]{maxSizeOfParameterSet, sizeOfEntireBatch};
        }
        return jArr;
    }

    /* access modifiers changed from: protected */
    public int setOneBatchedParameterSet(java.sql.PreparedStatement batchedStatement, int batchedParamIndex, Object paramSet) throws SQLException {
        int batchedParamIndex2;
        BindValue[] paramArg = ((BatchedBindValues) paramSet).batchedParameterValues;
        for (int j = 0; j < paramArg.length; j++) {
            if (paramArg[j].isNull) {
                batchedStatement.setNull(batchedParamIndex, 0);
                batchedParamIndex++;
            } else if (paramArg[j].isLongData) {
                Object value = paramArg[j].value;
                if (value instanceof InputStream) {
                    batchedParamIndex2 = batchedParamIndex + 1;
                    batchedStatement.setBinaryStream(batchedParamIndex, (InputStream) value, (int) paramArg[j].bindLength);
                } else {
                    batchedParamIndex2 = batchedParamIndex + 1;
                    batchedStatement.setCharacterStream(batchedParamIndex, (Reader) value, (int) paramArg[j].bindLength);
                }
                batchedParamIndex = batchedParamIndex2;
            } else {
                switch (paramArg[j].bufferType) {
                    case 0:
                    case 15:
                    case 246:
                    case 253:
                    case 254:
                        Object value2 = paramArg[j].value;
                        if (value2 instanceof byte[]) {
                            batchedStatement.setBytes(batchedParamIndex, (byte[]) value2);
                        } else {
                            batchedStatement.setString(batchedParamIndex, (String) value2);
                        }
                        if (batchedStatement instanceof ServerPreparedStatement) {
                            ((ServerPreparedStatement) batchedStatement).getBinding(batchedParamIndex, false).bufferType = paramArg[j].bufferType;
                        }
                        batchedParamIndex++;
                        break;
                    case 1:
                        batchedStatement.setByte(batchedParamIndex, (byte) ((int) paramArg[j].longBinding));
                        batchedParamIndex++;
                        break;
                    case 2:
                        batchedStatement.setShort(batchedParamIndex, (short) ((int) paramArg[j].longBinding));
                        batchedParamIndex++;
                        break;
                    case 3:
                        batchedStatement.setInt(batchedParamIndex, (int) paramArg[j].longBinding);
                        batchedParamIndex++;
                        break;
                    case 4:
                        batchedStatement.setFloat(batchedParamIndex, paramArg[j].floatBinding);
                        batchedParamIndex++;
                        break;
                    case 5:
                        batchedStatement.setDouble(batchedParamIndex, paramArg[j].doubleBinding);
                        batchedParamIndex++;
                        break;
                    case 7:
                    case 12:
                        batchedStatement.setTimestamp(batchedParamIndex, (Timestamp) paramArg[j].value);
                        batchedParamIndex++;
                        break;
                    case 8:
                        batchedStatement.setLong(batchedParamIndex, paramArg[j].longBinding);
                        batchedParamIndex++;
                        break;
                    case 10:
                        batchedStatement.setDate(batchedParamIndex, (java.sql.Date) paramArg[j].value);
                        batchedParamIndex++;
                        break;
                    case 11:
                        batchedStatement.setTime(batchedParamIndex, (Time) paramArg[j].value);
                        batchedParamIndex++;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown type when re-binding parameter into batched statement for parameter index " + batchedParamIndex);
                }
            }
        }
        return batchedParamIndex;
    }

    /* access modifiers changed from: protected */
    public boolean containsOnDuplicateKeyUpdateInSQL() {
        return this.hasOnDuplicateKeyUpdate;
    }

    /* access modifiers changed from: protected */
    public PreparedStatement prepareBatchedInsertSQL(MySQLConnection localConn, int numBatches) throws SQLException {
        PreparedStatement pstmt;
        synchronized (checkClosed().getConnectionMutex()) {
            try {
                pstmt = (PreparedStatement) ((Wrapper) localConn.prepareStatement(this.parseInfo.getSqlForBatch(numBatches), this.resultSetType, this.resultSetConcurrency)).unwrap(PreparedStatement.class);
                pstmt.setRetrieveGeneratedKeys(this.retrieveGeneratedKeys);
            } catch (UnsupportedEncodingException e) {
                SQLException sqlEx = SQLError.createSQLException("Unable to prepare batch statement", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
                sqlEx.initCause(e);
                throw sqlEx;
            } catch (Throwable th) {
                throw th;
            }
        }
        return pstmt;
    }

    public void setPoolable(boolean poolable) throws SQLException {
        if (!poolable) {
            this.connection.decachePreparedStatement(this);
        }
        super.setPoolable(poolable);
    }
}
