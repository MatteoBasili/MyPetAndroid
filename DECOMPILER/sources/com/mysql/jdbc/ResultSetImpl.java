package com.mysql.jdbc;

import androidx.recyclerview.widget.ItemTouchHelper;
import com.mysql.jdbc.log.LogUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeMap;

public class ResultSetImpl implements ResultSetInternalMethods {
    static final char[] EMPTY_SPACE = new char[255];
    private static final Constructor<?> JDBC_4_RS_4_ARG_CTOR;
    private static final Constructor<?> JDBC_4_RS_5_ARG_CTOR;
    private static final Constructor<?> JDBC_4_UPD_RS_5_ARG_CTOR;
    protected static final double MAX_DIFF_PREC = (((double) Float.parseFloat(Float.toString(Float.MAX_VALUE))) - Double.parseDouble(Float.toString(Float.MAX_VALUE)));
    protected static final double MIN_DIFF_PREC = (((double) Float.parseFloat(Float.toString(Float.MIN_VALUE))) - Double.parseDouble(Float.toString(Float.MIN_VALUE)));
    static int resultCounter = 1;
    protected String catalog = null;
    protected Map<String, Integer> columnLabelToIndex = null;
    protected Map<String, Integer> columnNameToIndex = null;
    protected Map<String, Integer> columnToIndexCache = null;
    protected boolean[] columnUsed = null;
    protected volatile MySQLConnection connection;
    protected int currentRow = -1;
    protected boolean doingUpdates = false;
    private ExceptionInterceptor exceptionInterceptor;
    Calendar fastClientCal = null;
    Calendar fastDefaultCal = null;
    protected int fetchDirection = 1000;
    protected int fetchSize = 0;
    protected Field[] fields;
    protected char firstCharOfQuery;
    protected Map<String, Integer> fullColumnNameToIndex = null;
    protected Calendar gmtCalendar = null;
    protected boolean hasBuiltIndexMapping = false;
    private String invalidRowReason = null;
    protected boolean isBinaryEncoded = false;
    protected boolean isClosed = false;
    private boolean jdbcCompliantTruncationForReads;
    protected ResultSetInternalMethods nextResultSet = null;
    protected boolean onInsertRow = false;
    private boolean onValidRow = false;
    protected StatementImpl owningStatement;
    private boolean padCharsWithSpace = false;
    protected String pointOfOrigin;
    protected boolean reallyResult = false;
    protected int resultId;
    protected int resultSetConcurrency = 0;
    protected int resultSetType = 0;
    protected boolean retainOwningStatement;
    protected RowData rowData;
    protected String serverInfo = null;
    private TimeZone serverTimeZoneTz;
    PreparedStatement statementUsedForFetchingRows;
    protected ResultSetRow thisRow = null;
    protected long updateCount;
    protected long updateId = -1;
    private boolean useColumnNamesInFindColumn;
    protected boolean useFastDateParsing = false;
    private boolean useFastIntParsing = true;
    protected boolean useLegacyDatetimeCode;
    private boolean useStrictFloatingPoint = false;
    protected boolean useUsageAdvisor = false;
    protected SQLWarning warningChain = null;
    protected boolean wasNullFlag = false;
    protected Statement wrapperStatement;

    static {
        if (Util.isJdbc4()) {
            try {
                String jdbc4ClassName = Util.isJdbc42() ? "com.mysql.jdbc.JDBC42ResultSet" : "com.mysql.jdbc.JDBC4ResultSet";
                JDBC_4_RS_4_ARG_CTOR = Class.forName(jdbc4ClassName).getConstructor(new Class[]{Long.TYPE, Long.TYPE, MySQLConnection.class, StatementImpl.class});
                JDBC_4_RS_5_ARG_CTOR = Class.forName(jdbc4ClassName).getConstructor(new Class[]{String.class, Field[].class, RowData.class, MySQLConnection.class, StatementImpl.class});
                JDBC_4_UPD_RS_5_ARG_CTOR = Class.forName(Util.isJdbc42() ? "com.mysql.jdbc.JDBC42UpdatableResultSet" : "com.mysql.jdbc.JDBC4UpdatableResultSet").getConstructor(new Class[]{String.class, Field[].class, RowData.class, MySQLConnection.class, StatementImpl.class});
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e2) {
                throw new RuntimeException(e2);
            } catch (ClassNotFoundException e3) {
                throw new RuntimeException(e3);
            }
        } else {
            JDBC_4_RS_4_ARG_CTOR = null;
            JDBC_4_RS_5_ARG_CTOR = null;
            JDBC_4_UPD_RS_5_ARG_CTOR = null;
        }
        int i = 0;
        while (true) {
            char[] cArr = EMPTY_SPACE;
            if (i < cArr.length) {
                cArr[i] = ' ';
                i++;
            } else {
                return;
            }
        }
    }

    protected static BigInteger convertLongToUlong(long longVal) {
        byte[] asBytes = new byte[8];
        asBytes[7] = (byte) ((int) (255 & longVal));
        asBytes[6] = (byte) ((int) (longVal >>> 8));
        asBytes[5] = (byte) ((int) (longVal >>> 16));
        asBytes[4] = (byte) ((int) (longVal >>> 24));
        asBytes[3] = (byte) ((int) (longVal >>> 32));
        asBytes[2] = (byte) ((int) (longVal >>> 40));
        asBytes[1] = (byte) ((int) (longVal >>> 48));
        asBytes[0] = (byte) ((int) (longVal >>> 56));
        return new BigInteger(1, asBytes);
    }

    protected static ResultSetImpl getInstance(long updateCount2, long updateID, MySQLConnection conn, StatementImpl creatorStmt) throws SQLException {
        if (!Util.isJdbc4()) {
            return new ResultSetImpl(updateCount2, updateID, conn, creatorStmt);
        }
        return (ResultSetImpl) Util.handleNewInstance(JDBC_4_RS_4_ARG_CTOR, new Object[]{Long.valueOf(updateCount2), Long.valueOf(updateID), conn, creatorStmt}, conn.getExceptionInterceptor());
    }

    protected static ResultSetImpl getInstance(String catalog2, Field[] fields2, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt, boolean isUpdatable) throws SQLException {
        if (!Util.isJdbc4()) {
            if (!isUpdatable) {
                return new ResultSetImpl(catalog2, fields2, tuples, conn, creatorStmt);
            }
            return new UpdatableResultSet(catalog2, fields2, tuples, conn, creatorStmt);
        } else if (!isUpdatable) {
            return (ResultSetImpl) Util.handleNewInstance(JDBC_4_RS_5_ARG_CTOR, new Object[]{catalog2, fields2, tuples, conn, creatorStmt}, conn.getExceptionInterceptor());
        } else {
            return (ResultSetImpl) Util.handleNewInstance(JDBC_4_UPD_RS_5_ARG_CTOR, new Object[]{catalog2, fields2, tuples, conn, creatorStmt}, conn.getExceptionInterceptor());
        }
    }

    public ResultSetImpl(long updateCount2, long updateID, MySQLConnection conn, StatementImpl creatorStmt) {
        this.updateCount = updateCount2;
        this.updateId = updateID;
        this.reallyResult = false;
        this.fields = new Field[0];
        this.connection = conn;
        this.owningStatement = creatorStmt;
        this.retainOwningStatement = false;
        if (this.connection != null) {
            this.exceptionInterceptor = this.connection.getExceptionInterceptor();
            this.retainOwningStatement = this.connection.getRetainStatementAfterResultSetClose();
            this.serverTimeZoneTz = this.connection.getServerTimezoneTZ();
            this.padCharsWithSpace = this.connection.getPadCharsWithSpace();
            this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode();
            this.useUsageAdvisor = this.connection.getUseUsageAdvisor();
        }
    }

    public ResultSetImpl(String catalog2, Field[] fields2, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt) throws SQLException {
        this.connection = conn;
        this.retainOwningStatement = false;
        if (this.connection != null) {
            this.exceptionInterceptor = this.connection.getExceptionInterceptor();
            this.useStrictFloatingPoint = this.connection.getStrictFloatingPoint();
            this.useFastDateParsing = this.connection.getUseFastDateParsing();
            this.retainOwningStatement = this.connection.getRetainStatementAfterResultSetClose();
            this.jdbcCompliantTruncationForReads = this.connection.getJdbcCompliantTruncationForReads();
            this.useFastIntParsing = this.connection.getUseFastIntParsing();
            this.serverTimeZoneTz = this.connection.getServerTimezoneTZ();
            this.padCharsWithSpace = this.connection.getPadCharsWithSpace();
            this.useUsageAdvisor = this.connection.getUseUsageAdvisor();
        }
        this.owningStatement = creatorStmt;
        this.catalog = catalog2;
        this.fields = fields2;
        this.rowData = tuples;
        this.updateCount = (long) tuples.size();
        this.reallyResult = true;
        if (this.rowData.size() <= 0) {
            this.thisRow = null;
        } else if (this.updateCount == 1 && this.thisRow == null) {
            this.rowData.close();
            this.updateCount = -1;
        }
        this.rowData.setOwner(this);
        if (this.fields != null) {
            initializeWithMetadata();
        }
        this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode();
        this.useColumnNamesInFindColumn = this.connection.getUseColumnNamesInFindColumn();
        setRowPositionValidity();
    }

    public void initializeWithMetadata() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            this.rowData.setMetadata(this.fields);
            this.columnToIndexCache = new HashMap();
            if (this.useUsageAdvisor) {
                this.columnUsed = new boolean[this.fields.length];
                this.pointOfOrigin = LogUtils.findCallingClassAndMethod(new Throwable());
                int i = resultCounter;
                resultCounter = i + 1;
                this.resultId = i;
            }
            if (this.connection.getGatherPerformanceMetrics()) {
                this.connection.incrementNumberOfResultSetsCreated();
                Set<String> tableNamesSet = new HashSet<>();
                int i2 = 0;
                while (true) {
                    Field[] fieldArr = this.fields;
                    if (i2 >= fieldArr.length) {
                        break;
                    }
                    Field f = fieldArr[i2];
                    String tableName = f.getOriginalTableName();
                    if (tableName == null) {
                        tableName = f.getTableName();
                    }
                    if (tableName != null) {
                        if (this.connection.lowerCaseTableNames()) {
                            tableName = tableName.toLowerCase();
                        }
                        tableNamesSet.add(tableName);
                    }
                    i2++;
                }
                this.connection.reportNumberOfTablesAccessed(tableNamesSet.size());
            }
        }
    }

    private synchronized Calendar getFastDefaultCalendar() {
        if (this.fastDefaultCal == null) {
            GregorianCalendar gregorianCalendar = new GregorianCalendar(Locale.US);
            this.fastDefaultCal = gregorianCalendar;
            gregorianCalendar.setTimeZone(getDefaultTimeZone());
        }
        return this.fastDefaultCal;
    }

    private synchronized Calendar getFastClientCalendar() {
        if (this.fastClientCal == null) {
            this.fastClientCal = new GregorianCalendar(Locale.US);
        }
        return this.fastClientCal;
    }

    public boolean absolute(int row) throws SQLException {
        boolean b;
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.rowData.size() == 0) {
                b = false;
            } else {
                if (this.onInsertRow) {
                    this.onInsertRow = false;
                }
                if (this.doingUpdates) {
                    this.doingUpdates = false;
                }
                ResultSetRow resultSetRow = this.thisRow;
                if (resultSetRow != null) {
                    resultSetRow.closeOpenStreams();
                }
                if (row == 0) {
                    beforeFirst();
                    b = false;
                } else if (row == 1) {
                    b = first();
                } else if (row == -1) {
                    b = last();
                } else if (row > this.rowData.size()) {
                    afterLast();
                    b = false;
                } else if (row < 0) {
                    int newRowPosition = this.rowData.size() + row + 1;
                    if (newRowPosition <= 0) {
                        beforeFirst();
                        b = false;
                    } else {
                        b = absolute(newRowPosition);
                    }
                } else {
                    int row2 = row - 1;
                    this.rowData.setCurrentRow(row2);
                    this.thisRow = this.rowData.getAt(row2);
                    b = true;
                }
            }
            setRowPositionValidity();
        }
        return b;
    }

    public void afterLast() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.onInsertRow) {
                this.onInsertRow = false;
            }
            if (this.doingUpdates) {
                this.doingUpdates = false;
            }
            ResultSetRow resultSetRow = this.thisRow;
            if (resultSetRow != null) {
                resultSetRow.closeOpenStreams();
            }
            if (this.rowData.size() != 0) {
                this.rowData.afterLast();
                this.thisRow = null;
            }
            setRowPositionValidity();
        }
    }

    public void beforeFirst() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.onInsertRow) {
                this.onInsertRow = false;
            }
            if (this.doingUpdates) {
                this.doingUpdates = false;
            }
            if (this.rowData.size() != 0) {
                ResultSetRow resultSetRow = this.thisRow;
                if (resultSetRow != null) {
                    resultSetRow.closeOpenStreams();
                }
                this.rowData.beforeFirst();
                this.thisRow = null;
                setRowPositionValidity();
            }
        }
    }

    public void buildIndexMapping() throws SQLException {
        int numFields = this.fields.length;
        this.columnLabelToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        this.fullColumnNameToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        this.columnNameToIndex = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        for (int i = numFields - 1; i >= 0; i--) {
            Integer index = Integer.valueOf(i);
            String columnName = this.fields[i].getOriginalName();
            String columnLabel = this.fields[i].getName();
            String fullColumnName = this.fields[i].getFullName();
            if (columnLabel != null) {
                this.columnLabelToIndex.put(columnLabel, index);
            }
            if (fullColumnName != null) {
                this.fullColumnNameToIndex.put(fullColumnName, index);
            }
            if (columnName != null) {
                this.columnNameToIndex.put(columnName, index);
            }
        }
        this.hasBuiltIndexMapping = true;
    }

    public void cancelRowUpdates() throws SQLException {
        throw new NotUpdatable();
    }

    /* access modifiers changed from: protected */
    public final MySQLConnection checkClosed() throws SQLException {
        MySQLConnection c = this.connection;
        if (c != null) {
            return c;
        }
        throw SQLError.createSQLException(Messages.getString("ResultSet.Operation_not_allowed_after_ResultSet_closed_144"), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
    }

    /* access modifiers changed from: protected */
    public final void checkColumnBounds(int columnIndex) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (columnIndex >= 1) {
                try {
                    if (columnIndex > this.fields.length) {
                        throw SQLError.createSQLException(Messages.getString("ResultSet.Column_Index_out_of_range_high", new Object[]{Integer.valueOf(columnIndex), Integer.valueOf(this.fields.length)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                    } else if (this.useUsageAdvisor) {
                        this.columnUsed[columnIndex - 1] = true;
                    }
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                throw SQLError.createSQLException(Messages.getString("ResultSet.Column_Index_out_of_range_low", new Object[]{Integer.valueOf(columnIndex), Integer.valueOf(this.fields.length)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void checkRowPos() throws SQLException {
        checkClosed();
        if (!this.onValidRow) {
            throw SQLError.createSQLException(this.invalidRowReason, SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
        }
    }

    private void setRowPositionValidity() throws SQLException {
        if (!this.rowData.isDynamic() && this.rowData.size() == 0) {
            this.invalidRowReason = Messages.getString("ResultSet.Illegal_operation_on_empty_result_set");
            this.onValidRow = false;
        } else if (this.rowData.isBeforeFirst()) {
            this.invalidRowReason = Messages.getString("ResultSet.Before_start_of_result_set_146");
            this.onValidRow = false;
        } else if (this.rowData.isAfterLast()) {
            this.invalidRowReason = Messages.getString("ResultSet.After_end_of_result_set_148");
            this.onValidRow = false;
        } else {
            this.onValidRow = true;
            this.invalidRowReason = null;
        }
    }

    public synchronized void clearNextResult() {
        this.nextResultSet = null;
    }

    public void clearWarnings() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            this.warningChain = null;
        }
    }

    public void close() throws SQLException {
        realClose(true);
    }

    private int convertToZeroWithEmptyCheck() throws SQLException {
        if (this.connection.getEmptyStringsConvertToZero()) {
            return 0;
        }
        throw SQLError.createSQLException("Can't convert empty string ('') to numeric", SQLError.SQL_STATE_INVALID_CHARACTER_VALUE_FOR_CAST, getExceptionInterceptor());
    }

    private String convertToZeroLiteralStringWithEmptyCheck() throws SQLException {
        if (this.connection.getEmptyStringsConvertToZero()) {
            return "0";
        }
        throw SQLError.createSQLException("Can't convert empty string ('') to numeric", SQLError.SQL_STATE_INVALID_CHARACTER_VALUE_FOR_CAST, getExceptionInterceptor());
    }

    public ResultSetInternalMethods copy() throws SQLException {
        ResultSetImpl rs;
        synchronized (checkClosed().getConnectionMutex()) {
            rs = getInstance(this.catalog, this.fields, this.rowData, this.connection, this.owningStatement, false);
            if (this.isBinaryEncoded) {
                rs.setBinaryEncoded();
            }
        }
        return rs;
    }

    public void redefineFieldsForDBMD(Field[] f) {
        this.fields = f;
        int i = 0;
        while (true) {
            Field[] fieldArr = this.fields;
            if (i < fieldArr.length) {
                fieldArr[i].setUseOldNameMetadata(true);
                this.fields[i].setConnection(this.connection);
                i++;
            } else {
                return;
            }
        }
    }

    public void populateCachedMetaData(CachedResultSetMetaData cachedMetaData) throws SQLException {
        cachedMetaData.fields = this.fields;
        cachedMetaData.columnNameToIndex = this.columnLabelToIndex;
        cachedMetaData.fullColumnNameToIndex = this.fullColumnNameToIndex;
        cachedMetaData.metadata = getMetaData();
    }

    public void initializeFromCachedMetaData(CachedResultSetMetaData cachedMetaData) {
        this.fields = cachedMetaData.fields;
        this.columnLabelToIndex = cachedMetaData.columnNameToIndex;
        this.fullColumnNameToIndex = cachedMetaData.fullColumnNameToIndex;
        this.hasBuiltIndexMapping = true;
    }

    public void deleteRow() throws SQLException {
        throw new NotUpdatable();
    }

    private String extractStringFromNativeColumn(int columnIndex, int mysqlType) throws SQLException {
        int columnIndexMinusOne = columnIndex - 1;
        this.wasNullFlag = false;
        if (this.thisRow.isNull(columnIndexMinusOne)) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        return this.thisRow.getString(columnIndex - 1, this.fields[columnIndexMinusOne].getCollationIndex() == 63 ? this.connection.getEncoding() : this.fields[columnIndexMinusOne].getEncoding(), this.connection);
    }

    /* access modifiers changed from: protected */
    public Date fastDateCreate(Calendar cal, int year, int month, int day) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            Calendar targetCalendar = cal;
            if (cal == null) {
                if (this.connection.getNoTimezoneConversionForDateType()) {
                    targetCalendar = getFastClientCalendar();
                } else {
                    targetCalendar = getFastDefaultCalendar();
                }
            }
            if (!this.useLegacyDatetimeCode) {
                Date fastDateCreate = TimeUtil.fastDateCreate(year, month, day, targetCalendar);
                return fastDateCreate;
            }
            boolean useGmtMillis = cal == null && !this.connection.getNoTimezoneConversionForDateType() && this.connection.getUseGmtMillisForDatetimes();
            Date fastDateCreate2 = TimeUtil.fastDateCreate(useGmtMillis, useGmtMillis ? getGmtCalendar() : targetCalendar, targetCalendar, year, month, day);
            return fastDateCreate2;
        }
    }

    /* access modifiers changed from: protected */
    public Time fastTimeCreate(Calendar cal, int hour, int minute, int second) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.useLegacyDatetimeCode) {
                Time fastTimeCreate = TimeUtil.fastTimeCreate(hour, minute, second, cal, getExceptionInterceptor());
                return fastTimeCreate;
            }
            if (cal == null) {
                cal = getFastDefaultCalendar();
            }
            Time fastTimeCreate2 = TimeUtil.fastTimeCreate(cal, hour, minute, second, getExceptionInterceptor());
            return fastTimeCreate2;
        }
    }

    /* access modifiers changed from: protected */
    public Timestamp fastTimestampCreate(Calendar cal, int year, int month, int day, int hour, int minute, int seconds, int secondsPart, boolean useGmtMillis) throws SQLException {
        Calendar cal2;
        synchronized (checkClosed().getConnectionMutex()) {
            try {
                if (!this.useLegacyDatetimeCode) {
                    Timestamp fastTimestampCreate = TimeUtil.fastTimestampCreate(cal.getTimeZone(), year, month, day, hour, minute, seconds, secondsPart);
                    return fastTimestampCreate;
                }
                if (cal == null) {
                    cal2 = getFastDefaultCalendar();
                } else {
                    cal2 = cal;
                }
                Timestamp fastTimestampCreate2 = TimeUtil.fastTimestampCreate(useGmtMillis, useGmtMillis ? getGmtCalendar() : null, cal2, year, month, day, hour, minute, seconds, secondsPart);
                return fastTimestampCreate2;
            } catch (Throwable th) {
                th = th;
                throw th;
            }
        }
    }

    public int findColumn(String columnName) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.hasBuiltIndexMapping) {
                buildIndexMapping();
            }
            Integer index = this.columnToIndexCache.get(columnName);
            if (index != null) {
                int intValue = index.intValue() + 1;
                return intValue;
            }
            Integer index2 = this.columnLabelToIndex.get(columnName);
            if (index2 == null && this.useColumnNamesInFindColumn) {
                index2 = this.columnNameToIndex.get(columnName);
            }
            if (index2 == null) {
                index2 = this.fullColumnNameToIndex.get(columnName);
            }
            if (index2 != null) {
                this.columnToIndexCache.put(columnName, index2);
                int intValue2 = index2.intValue() + 1;
                return intValue2;
            }
            int i = 0;
            while (true) {
                Field[] fieldArr = this.fields;
                if (i >= fieldArr.length) {
                    throw SQLError.createSQLException(Messages.getString("ResultSet.Column____112") + columnName + Messages.getString("ResultSet.___not_found._113"), SQLError.SQL_STATE_COLUMN_NOT_FOUND, getExceptionInterceptor());
                } else if (fieldArr[i].getName().equalsIgnoreCase(columnName)) {
                    int i2 = i + 1;
                    return i2;
                } else if (this.fields[i].getFullName().equalsIgnoreCase(columnName)) {
                    int i3 = i + 1;
                    return i3;
                } else {
                    i++;
                }
            }
        }
    }

    public boolean first() throws SQLException {
        boolean b;
        synchronized (checkClosed().getConnectionMutex()) {
            b = true;
            if (this.rowData.isEmpty()) {
                b = false;
            } else {
                if (this.onInsertRow) {
                    this.onInsertRow = false;
                }
                if (this.doingUpdates) {
                    this.doingUpdates = false;
                }
                this.rowData.beforeFirst();
                this.thisRow = this.rowData.next();
            }
            setRowPositionValidity();
        }
        return b;
    }

    public Array getArray(int i) throws SQLException {
        checkColumnBounds(i);
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    public Array getArray(String colName) throws SQLException {
        return getArray(findColumn(colName));
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        checkRowPos();
        if (!this.isBinaryEncoded) {
            return getBinaryStream(columnIndex);
        }
        return getNativeBinaryStream(columnIndex);
    }

    public InputStream getAsciiStream(String columnName) throws SQLException {
        return getAsciiStream(findColumn(columnName));
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        if (this.isBinaryEncoded) {
            return getNativeBigDecimal(columnIndex);
        }
        String stringVal = getString(columnIndex);
        if (stringVal == null) {
            return null;
        }
        if (stringVal.length() == 0) {
            return new BigDecimal(convertToZeroLiteralStringWithEmptyCheck());
        }
        try {
            return new BigDecimal(stringVal);
        } catch (NumberFormatException e) {
            throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[]{stringVal, Integer.valueOf(columnIndex)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
    }

    @Deprecated
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        BigDecimal val;
        if (this.isBinaryEncoded) {
            return getNativeBigDecimal(columnIndex, scale);
        }
        String stringVal = getString(columnIndex);
        if (stringVal == null) {
            return null;
        }
        if (stringVal.length() == 0) {
            BigDecimal val2 = new BigDecimal(convertToZeroLiteralStringWithEmptyCheck());
            try {
                return val2.setScale(scale);
            } catch (ArithmeticException e) {
                try {
                    return val2.setScale(scale, 4);
                } catch (ArithmeticException e2) {
                    throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[]{stringVal, Integer.valueOf(columnIndex)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                }
            }
        } else {
            try {
                val = new BigDecimal(stringVal);
            } catch (NumberFormatException e3) {
                if (this.fields[columnIndex - 1].getMysqlType() == 16) {
                    val = new BigDecimal(getNumericRepresentationOfSQLBitType(columnIndex));
                } else {
                    throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[]{Integer.valueOf(columnIndex), stringVal}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                }
            }
            try {
                return val.setScale(scale);
            } catch (ArithmeticException e4) {
                try {
                    return val.setScale(scale, 4);
                } catch (ArithmeticException e5) {
                    throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[]{Integer.valueOf(columnIndex), stringVal}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                }
            }
        }
    }

    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        return getBigDecimal(findColumn(columnName));
    }

    @Deprecated
    public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
        return getBigDecimal(findColumn(columnName), scale);
    }

    private final BigDecimal getBigDecimalFromString(String stringVal, int columnIndex, int scale) throws SQLException {
        if (stringVal == null) {
            return null;
        }
        if (stringVal.length() == 0) {
            BigDecimal bdVal = new BigDecimal(convertToZeroLiteralStringWithEmptyCheck());
            try {
                return bdVal.setScale(scale);
            } catch (ArithmeticException e) {
                try {
                    return bdVal.setScale(scale, 4);
                } catch (ArithmeticException e2) {
                    throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[]{stringVal, Integer.valueOf(columnIndex)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT);
                }
            }
        } else {
            try {
                return new BigDecimal(stringVal).setScale(scale);
            } catch (ArithmeticException e3) {
                try {
                    return new BigDecimal(stringVal).setScale(scale, 4);
                } catch (ArithmeticException e4) {
                    throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[]{stringVal, Integer.valueOf(columnIndex)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT);
                } catch (NumberFormatException e5) {
                    if (this.fields[columnIndex - 1].getMysqlType() == 16) {
                        long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
                        try {
                            return new BigDecimal(valueAsLong).setScale(scale);
                        } catch (ArithmeticException e6) {
                            try {
                                return new BigDecimal(valueAsLong).setScale(scale, 4);
                            } catch (ArithmeticException e7) {
                                throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[]{stringVal, Integer.valueOf(columnIndex)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT);
                            }
                        }
                    } else if (this.fields[columnIndex - 1].getMysqlType() == 1 && this.connection.getTinyInt1isBit() && this.fields[columnIndex - 1].getLength() == 1) {
                        return new BigDecimal(stringVal.equalsIgnoreCase("true") ? 1 : 0).setScale(scale);
                    } else {
                        throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[]{stringVal, Integer.valueOf(columnIndex)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT);
                    }
                }
            }
        }
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        checkRowPos();
        if (this.isBinaryEncoded) {
            return getNativeBinaryStream(columnIndex);
        }
        checkColumnBounds(columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        if (this.thisRow.isNull(columnIndexMinusOne)) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        return this.thisRow.getBinaryInputStream(columnIndexMinusOne);
    }

    public InputStream getBinaryStream(String columnName) throws SQLException {
        return getBinaryStream(findColumn(columnName));
    }

    public Blob getBlob(int columnIndex) throws SQLException {
        if (this.isBinaryEncoded) {
            return getNativeBlob(columnIndex);
        }
        checkRowPos();
        checkColumnBounds(columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        if (this.thisRow.isNull(columnIndexMinusOne)) {
            this.wasNullFlag = true;
        } else {
            this.wasNullFlag = false;
        }
        if (this.wasNullFlag) {
            return null;
        }
        if (!this.connection.getEmulateLocators()) {
            return new Blob(this.thisRow.getColumnValue(columnIndexMinusOne), getExceptionInterceptor());
        }
        return new BlobFromLocator(this, columnIndex, getExceptionInterceptor());
    }

    public Blob getBlob(String colName) throws SQLException {
        return getBlob(findColumn(colName));
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        checkColumnBounds(columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        Field field = this.fields[columnIndexMinusOne];
        if (field.getMysqlType() == 16) {
            return byteArrayToBoolean(columnIndexMinusOne);
        }
        this.wasNullFlag = false;
        int sqlType = field.getSQLType();
        switch (sqlType) {
            case -7:
            case -6:
            case -5:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                long boolVal = getLong(columnIndex, false);
                if (boolVal == -1 || boolVal > 0) {
                    return true;
                }
                return false;
            case 16:
                if (field.getMysqlType() == -1) {
                    return getBooleanFromString(getString(columnIndex));
                }
                long boolVal2 = getLong(columnIndex, false);
                if (boolVal2 == -1 || boolVal2 > 0) {
                    return true;
                }
                return false;
            default:
                if (this.connection.getPedantic()) {
                    switch (sqlType) {
                        case -4:
                        case -3:
                        case -2:
                        case 70:
                        case 91:
                        case 92:
                        case 93:
                        case 2000:
                        case 2002:
                        case 2003:
                        case 2004:
                        case 2005:
                        case 2006:
                            throw SQLError.createSQLException("Required type conversion not allowed", SQLError.SQL_STATE_INVALID_CHARACTER_VALUE_FOR_CAST, getExceptionInterceptor());
                    }
                }
                if (sqlType == -2 || sqlType == -3 || sqlType == -4 || sqlType == 2004) {
                    return byteArrayToBoolean(columnIndexMinusOne);
                }
                if (this.useUsageAdvisor) {
                    issueConversionViaParsingWarning("getBoolean()", columnIndex, this.thisRow.getColumnValue(columnIndexMinusOne), this.fields[columnIndex], new int[]{16, 5, 1, 2, 3, 8, 4});
                }
                return getBooleanFromString(getString(columnIndex));
        }
    }

    private boolean byteArrayToBoolean(int columnIndexMinusOne) throws SQLException {
        byte[] columnValue = this.thisRow.getColumnValue(columnIndexMinusOne);
        if (columnValue == null) {
            this.wasNullFlag = true;
            return false;
        }
        this.wasNullFlag = false;
        if (columnValue.length == 0) {
            return false;
        }
        byte boolVal = columnValue[0];
        if (boolVal == 49) {
            return true;
        }
        if (boolVal == 48) {
            return false;
        }
        if (boolVal == -1 || boolVal > 0) {
            return true;
        }
        return false;
    }

    public boolean getBoolean(String columnName) throws SQLException {
        return getBoolean(findColumn(columnName));
    }

    private final boolean getBooleanFromString(String stringVal) throws SQLException {
        if (stringVal == null || stringVal.length() <= 0) {
            return false;
        }
        int c = Character.toLowerCase(stringVal.charAt(0));
        if (c == 116 || c == 121 || c == 49 || stringVal.equals("-1")) {
            return true;
        }
        return false;
    }

    public byte getByte(int columnIndex) throws SQLException {
        if (this.isBinaryEncoded) {
            return getNativeByte(columnIndex);
        }
        String stringVal = getString(columnIndex);
        if (this.wasNullFlag || stringVal == null) {
            return 0;
        }
        return getByteFromString(stringVal, columnIndex);
    }

    public byte getByte(String columnName) throws SQLException {
        return getByte(findColumn(columnName));
    }

    private final byte getByteFromString(String stringVal, int columnIndex) throws SQLException {
        if (stringVal != null && stringVal.length() == 0) {
            return (byte) convertToZeroWithEmptyCheck();
        }
        if (stringVal == null) {
            return 0;
        }
        String stringVal2 = stringVal.trim();
        try {
            if (stringVal2.indexOf(".") != -1) {
                double valueAsDouble = Double.parseDouble(stringVal2);
                if (this.jdbcCompliantTruncationForReads && (valueAsDouble < -128.0d || valueAsDouble > 127.0d)) {
                    throwRangeException(stringVal2, columnIndex, -6);
                }
                return (byte) ((int) valueAsDouble);
            }
            long valueAsLong = Long.parseLong(stringVal2);
            if (this.jdbcCompliantTruncationForReads && (valueAsLong < -128 || valueAsLong > 127)) {
                throwRangeException(String.valueOf(valueAsLong), columnIndex, -6);
            }
            return (byte) ((int) valueAsLong);
        } catch (NumberFormatException e) {
            throw SQLError.createSQLException(Messages.getString("ResultSet.Value____173") + stringVal2 + Messages.getString("ResultSet.___is_out_of_range_[-127,127]_174"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        return getBytes(columnIndex, false);
    }

    /* access modifiers changed from: protected */
    public byte[] getBytes(int columnIndex, boolean noConversion) throws SQLException {
        if (this.isBinaryEncoded) {
            return getNativeBytes(columnIndex, noConversion);
        }
        checkRowPos();
        checkColumnBounds(columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        if (this.thisRow.isNull(columnIndexMinusOne)) {
            this.wasNullFlag = true;
        } else {
            this.wasNullFlag = false;
        }
        if (this.wasNullFlag) {
            return null;
        }
        return this.thisRow.getColumnValue(columnIndexMinusOne);
    }

    public byte[] getBytes(String columnName) throws SQLException {
        return getBytes(findColumn(columnName));
    }

    private final byte[] getBytesFromString(String stringVal) throws SQLException {
        if (stringVal == null) {
            return null;
        }
        return StringUtils.getBytes(stringVal, this.connection.getEncoding(), this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), this.connection, getExceptionInterceptor());
    }

    public int getBytesSize() throws SQLException {
        RowData localRowData = this.rowData;
        checkClosed();
        if (!(localRowData instanceof RowDataStatic)) {
            return -1;
        }
        int bytesSize = 0;
        int numRows = localRowData.size();
        for (int i = 0; i < numRows; i++) {
            bytesSize += localRowData.getAt(i).getBytesSize();
        }
        return bytesSize;
    }

    /* access modifiers changed from: protected */
    public Calendar getCalendarInstanceForSessionOrNew() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.connection != null) {
                Calendar calendarInstanceForSessionOrNew = this.connection.getCalendarInstanceForSessionOrNew();
                return calendarInstanceForSessionOrNew;
            }
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            return gregorianCalendar;
        }
    }

    public Reader getCharacterStream(int columnIndex) throws SQLException {
        if (this.isBinaryEncoded) {
            return getNativeCharacterStream(columnIndex);
        }
        checkColumnBounds(columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        if (this.thisRow.isNull(columnIndexMinusOne)) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        return this.thisRow.getReader(columnIndexMinusOne);
    }

    public Reader getCharacterStream(String columnName) throws SQLException {
        return getCharacterStream(findColumn(columnName));
    }

    private final Reader getCharacterStreamFromString(String stringVal) throws SQLException {
        if (stringVal != null) {
            return new StringReader(stringVal);
        }
        return null;
    }

    public Clob getClob(int i) throws SQLException {
        if (this.isBinaryEncoded) {
            return getNativeClob(i);
        }
        String asString = getStringForClob(i);
        if (asString == null) {
            return null;
        }
        return new Clob(asString, getExceptionInterceptor());
    }

    public Clob getClob(String colName) throws SQLException {
        return getClob(findColumn(colName));
    }

    private final Clob getClobFromString(String stringVal) throws SQLException {
        return new Clob(stringVal, getExceptionInterceptor());
    }

    public int getConcurrency() throws SQLException {
        return 1007;
    }

    public String getCursorName() throws SQLException {
        throw SQLError.createSQLException(Messages.getString("ResultSet.Positioned_Update_not_supported"), SQLError.SQL_STATE_DRIVER_NOT_CAPABLE, getExceptionInterceptor());
    }

    public Date getDate(int columnIndex) throws SQLException {
        return getDate(columnIndex, (Calendar) null);
    }

    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        if (this.isBinaryEncoded) {
            return getNativeDate(columnIndex, cal);
        }
        if (!this.useFastDateParsing) {
            String stringVal = getStringInternal(columnIndex, false);
            if (stringVal == null) {
                return null;
            }
            return getDateFromString(stringVal, columnIndex, cal);
        }
        checkColumnBounds(columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        Date tmpDate = this.thisRow.getDateFast(columnIndexMinusOne, this.connection, this, cal);
        if (this.thisRow.isNull(columnIndexMinusOne) || tmpDate == null) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        return tmpDate;
    }

    public Date getDate(String columnName) throws SQLException {
        return getDate(findColumn(columnName));
    }

    public Date getDate(String columnName, Calendar cal) throws SQLException {
        return getDate(findColumn(columnName), cal);
    }

    private final Date getDateFromString(String stringVal, int columnIndex, Calendar targetCalendar) throws SQLException {
        String stringVal2;
        int day;
        int month;
        int year;
        int year2;
        int year3;
        int year4;
        int year5;
        Calendar calendar = targetCalendar;
        try {
            this.wasNullFlag = false;
            if (stringVal == null) {
                this.wasNullFlag = true;
                return null;
            }
            stringVal2 = stringVal.trim();
            try {
                int dec = stringVal2.indexOf(".");
                if (dec > -1) {
                    stringVal2 = stringVal2.substring(0, dec);
                }
                if (!stringVal2.equals("0") && !stringVal2.equals("0000-00-00") && !stringVal2.equals("0000-00-00 00:00:00") && !stringVal2.equals("00000000000000")) {
                    if (!stringVal2.equals("0")) {
                        if (this.fields[columnIndex - 1].getMysqlType() == 7) {
                            switch (stringVal2.length()) {
                                case 2:
                                    int year6 = Integer.parseInt(stringVal2.substring(0, 2));
                                    if (year6 <= 69) {
                                        year3 = year6 + 100;
                                    } else {
                                        year3 = year6;
                                    }
                                    return fastDateCreate(calendar, year3 + MysqlErrorNumbers.ER_SLAVE_SQL_THREAD_MUST_STOP, 1, 1);
                                case 4:
                                    int year7 = Integer.parseInt(stringVal2.substring(0, 4));
                                    if (year7 <= 69) {
                                        year4 = year7 + 100;
                                    } else {
                                        year4 = year7;
                                    }
                                    return fastDateCreate(calendar, year4 + MysqlErrorNumbers.ER_SLAVE_SQL_THREAD_MUST_STOP, Integer.parseInt(stringVal2.substring(2, 4)), 1);
                                case 6:
                                case 10:
                                case 12:
                                    int year8 = Integer.parseInt(stringVal2.substring(0, 2));
                                    if (year8 <= 69) {
                                        year5 = year8 + 100;
                                    } else {
                                        year5 = year8;
                                    }
                                    return fastDateCreate(calendar, year5 + MysqlErrorNumbers.ER_SLAVE_SQL_THREAD_MUST_STOP, Integer.parseInt(stringVal2.substring(2, 4)), Integer.parseInt(stringVal2.substring(4, 6)));
                                case 8:
                                case 14:
                                    return fastDateCreate(calendar, Integer.parseInt(stringVal2.substring(0, 4)), Integer.parseInt(stringVal2.substring(4, 6)), Integer.parseInt(stringVal2.substring(6, 8)));
                                case 19:
                                case 21:
                                    return fastDateCreate(calendar, Integer.parseInt(stringVal2.substring(0, 4)), Integer.parseInt(stringVal2.substring(5, 7)), Integer.parseInt(stringVal2.substring(8, 10)));
                                default:
                                    throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[]{stringVal2, Integer.valueOf(columnIndex)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                            }
                        } else if (this.fields[columnIndex - 1].getMysqlType() == 13) {
                            if (stringVal2.length() != 2) {
                                if (stringVal2.length() != 1) {
                                    year2 = Integer.parseInt(stringVal2.substring(0, 4));
                                    return fastDateCreate(calendar, year2, 1, 1);
                                }
                            }
                            int year9 = Integer.parseInt(stringVal2);
                            if (year9 <= 69) {
                                year9 += 100;
                            }
                            year2 = year9 + MysqlErrorNumbers.ER_SLAVE_SQL_THREAD_MUST_STOP;
                            return fastDateCreate(calendar, year2, 1, 1);
                        } else if (this.fields[columnIndex - 1].getMysqlType() == 11) {
                            return fastDateCreate(calendar, 1970, 1, 1);
                        } else {
                            if (stringVal2.length() >= 10) {
                                if (stringVal2.length() != 18) {
                                    year = Integer.parseInt(stringVal2.substring(0, 4));
                                    month = Integer.parseInt(stringVal2.substring(5, 7));
                                    day = Integer.parseInt(stringVal2.substring(8, 10));
                                } else {
                                    StringTokenizer st = new StringTokenizer(stringVal2, "- ");
                                    year = Integer.parseInt(st.nextToken());
                                    month = Integer.parseInt(st.nextToken());
                                    day = Integer.parseInt(st.nextToken());
                                }
                                return fastDateCreate(calendar, year, month, day);
                            } else if (stringVal2.length() == 8) {
                                return fastDateCreate(calendar, 1970, 1, 1);
                            } else {
                                throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[]{stringVal2, Integer.valueOf(columnIndex)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                            }
                        }
                    }
                }
                if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior())) {
                    this.wasNullFlag = true;
                    return null;
                } else if (!"exception".equals(this.connection.getZeroDateTimeBehavior())) {
                    return fastDateCreate(calendar, 1, 1, 1);
                } else {
                    throw SQLError.createSQLException("Value '" + stringVal2 + "' can not be represented as java.sql.Date", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                }
            } catch (SQLException e) {
                sqlEx = e;
                throw sqlEx;
            } catch (Exception e2) {
                e = e2;
                SQLException sqlEx = SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[]{stringVal2, Integer.valueOf(columnIndex)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                sqlEx.initCause(e);
                throw sqlEx;
            }
        } catch (SQLException e3) {
            sqlEx = e3;
            String str = stringVal;
            throw sqlEx;
        } catch (Exception e4) {
            e = e4;
            stringVal2 = stringVal;
            SQLException sqlEx2 = SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[]{stringVal2, Integer.valueOf(columnIndex)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            sqlEx2.initCause(e);
            throw sqlEx2;
        }
    }

    private TimeZone getDefaultTimeZone() {
        return this.useLegacyDatetimeCode ? this.connection.getDefaultTimeZone() : this.serverTimeZoneTz;
    }

    public double getDouble(int columnIndex) throws SQLException {
        if (!this.isBinaryEncoded) {
            return getDoubleInternal(columnIndex);
        }
        return getNativeDouble(columnIndex);
    }

    public double getDouble(String columnName) throws SQLException {
        return getDouble(findColumn(columnName));
    }

    private final double getDoubleFromString(String stringVal, int columnIndex) throws SQLException {
        return getDoubleInternal(stringVal, columnIndex);
    }

    /* access modifiers changed from: protected */
    public double getDoubleInternal(int colIndex) throws SQLException {
        return getDoubleInternal(getString(colIndex), colIndex);
    }

    /* access modifiers changed from: protected */
    public double getDoubleInternal(String stringVal, int colIndex) throws SQLException {
        if (stringVal == null) {
            return 0.0d;
        }
        try {
            if (stringVal.length() == 0) {
                return (double) convertToZeroWithEmptyCheck();
            }
            double d = Double.parseDouble(stringVal);
            if (!this.useStrictFloatingPoint) {
                return d;
            }
            if (d == 2.147483648E9d) {
                return 2.147483647E9d;
            }
            if (d == 1.0000000036275E-15d) {
                return 1.0E-15d;
            }
            if (d == 9.999999869911E14d) {
                return 9.99999999999999E14d;
            }
            if (d == 1.4012984643248E-45d || d == 1.4013E-45d) {
                return 1.4E-45d;
            }
            if (d == 3.4028234663853E37d) {
                return 3.4028235E37d;
            }
            if (d == -2.14748E9d) {
                return -2.147483648E9d;
            }
            if (d == 3.40282E37d) {
                return 3.4028235E37d;
            }
            return d;
        } catch (NumberFormatException e) {
            if (this.fields[colIndex - 1].getMysqlType() == 16) {
                return (double) getNumericRepresentationOfSQLBitType(colIndex);
            }
            throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_number", new Object[]{stringVal, Integer.valueOf(colIndex)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
    }

    public int getFetchDirection() throws SQLException {
        int i;
        synchronized (checkClosed().getConnectionMutex()) {
            i = this.fetchDirection;
        }
        return i;
    }

    public int getFetchSize() throws SQLException {
        int i;
        synchronized (checkClosed().getConnectionMutex()) {
            i = this.fetchSize;
        }
        return i;
    }

    public char getFirstCharOfQuery() {
        char c;
        try {
            synchronized (checkClosed().getConnectionMutex()) {
                c = this.firstCharOfQuery;
            }
            return c;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public float getFloat(int columnIndex) throws SQLException {
        if (!this.isBinaryEncoded) {
            return getFloatFromString(getString(columnIndex), columnIndex);
        }
        return getNativeFloat(columnIndex);
    }

    public float getFloat(String columnName) throws SQLException {
        return getFloat(findColumn(columnName));
    }

    private final float getFloatFromString(String val, int columnIndex) throws SQLException {
        if (val == null) {
            return 0.0f;
        }
        try {
            if (val.length() == 0) {
                return (float) convertToZeroWithEmptyCheck();
            }
            float f = Float.parseFloat(val);
            if (this.jdbcCompliantTruncationForReads && (f == Float.MIN_VALUE || f == Float.MAX_VALUE)) {
                double valAsDouble = Double.parseDouble(val);
                if (valAsDouble < 1.401298464324817E-45d - MIN_DIFF_PREC || valAsDouble > 3.4028234663852886E38d - MAX_DIFF_PREC) {
                    throwRangeException(String.valueOf(valAsDouble), columnIndex, 6);
                }
            }
            return f;
        } catch (NumberFormatException e) {
            try {
                Double valueAsDouble = new Double(val);
                float valueAsFloat = valueAsDouble.floatValue();
                boolean z = this.jdbcCompliantTruncationForReads;
                if (z && ((z && valueAsFloat == Float.NEGATIVE_INFINITY) || valueAsFloat == Float.POSITIVE_INFINITY)) {
                    throwRangeException(valueAsDouble.toString(), columnIndex, 6);
                }
                return valueAsFloat;
            } catch (NumberFormatException e2) {
                throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getFloat()_-____200") + val + Messages.getString("ResultSet.___in_column__201") + columnIndex, SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
    }

    public int getInt(int columnIndex) throws SQLException {
        checkRowPos();
        checkColumnBounds(columnIndex);
        if (this.isBinaryEncoded) {
            return getNativeInt(columnIndex);
        }
        int columnIndexMinusOne = columnIndex - 1;
        if (this.thisRow.isNull(columnIndexMinusOne)) {
            this.wasNullFlag = true;
            return 0;
        }
        this.wasNullFlag = false;
        if (this.fields[columnIndexMinusOne].getMysqlType() == 16) {
            long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
            if (this.jdbcCompliantTruncationForReads && (valueAsLong < -2147483648L || valueAsLong > 2147483647L)) {
                throwRangeException(String.valueOf(valueAsLong), columnIndex, 4);
            }
            return (int) valueAsLong;
        }
        if (this.useFastIntParsing) {
            if (this.thisRow.length(columnIndexMinusOne) == 0) {
                return convertToZeroWithEmptyCheck();
            }
            if (!this.thisRow.isFloatingPointNumber(columnIndexMinusOne)) {
                try {
                    return getIntWithOverflowCheck(columnIndexMinusOne);
                } catch (NumberFormatException e) {
                    try {
                        return parseIntAsDouble(columnIndex, this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getEncoding(), this.connection));
                    } catch (NumberFormatException e2) {
                        throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____74") + this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getEncoding(), this.connection) + "'", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                    }
                }
            }
        }
        try {
            String val = getString(columnIndex);
            if (val == null) {
                return 0;
            }
            if (val.length() == 0) {
                return convertToZeroWithEmptyCheck();
            }
            if (val.indexOf("e") == -1 && val.indexOf("E") == -1 && val.indexOf(".") == -1) {
                int intVal = Integer.parseInt(val);
                checkForIntegerTruncation(columnIndexMinusOne, (byte[]) null, intVal);
                return intVal;
            }
            int intVal2 = parseIntAsDouble(columnIndex, val);
            checkForIntegerTruncation(columnIndex, (byte[]) null, intVal2);
            return intVal2;
        } catch (NumberFormatException e3) {
            try {
                return parseIntAsDouble(columnIndex, (String) null);
            } catch (NumberFormatException e4) {
                throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____74") + null + "'", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
    }

    public int getInt(String columnName) throws SQLException {
        return getInt(findColumn(columnName));
    }

    private final int getIntFromString(String val, int columnIndex) throws SQLException {
        if (val == null) {
            return 0;
        }
        try {
            if (val.length() == 0) {
                return convertToZeroWithEmptyCheck();
            }
            if (val.indexOf("e") == -1 && val.indexOf("E") == -1 && val.indexOf(".") == -1) {
                String val2 = val.trim();
                int valueAsInt = Integer.parseInt(val2);
                if (this.jdbcCompliantTruncationForReads && (valueAsInt == Integer.MIN_VALUE || valueAsInt == Integer.MAX_VALUE)) {
                    long valueAsLong = Long.parseLong(val2);
                    if (valueAsLong < -2147483648L || valueAsLong > 2147483647L) {
                        throwRangeException(String.valueOf(valueAsLong), columnIndex, 4);
                    }
                }
                return valueAsInt;
            }
            double valueAsDouble = Double.parseDouble(val);
            if (this.jdbcCompliantTruncationForReads && (valueAsDouble < -2.147483648E9d || valueAsDouble > 2.147483647E9d)) {
                throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
            }
            return (int) valueAsDouble;
        } catch (NumberFormatException e) {
            try {
                double valueAsDouble2 = Double.parseDouble(val);
                if (this.jdbcCompliantTruncationForReads && (valueAsDouble2 < -2.147483648E9d || valueAsDouble2 > 2.147483647E9d)) {
                    throwRangeException(String.valueOf(valueAsDouble2), columnIndex, 4);
                }
                return (int) valueAsDouble2;
            } catch (NumberFormatException e2) {
                throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____206") + val + Messages.getString("ResultSet.___in_column__207") + columnIndex, SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
    }

    public long getLong(int columnIndex) throws SQLException {
        return getLong(columnIndex, true);
    }

    private long getLong(int columnIndex, boolean overflowCheck) throws SQLException {
        checkRowPos();
        checkColumnBounds(columnIndex);
        if (this.isBinaryEncoded) {
            return getNativeLong(columnIndex, overflowCheck, true);
        }
        int columnIndexMinusOne = columnIndex - 1;
        if (this.thisRow.isNull(columnIndexMinusOne)) {
            this.wasNullFlag = true;
            return 0;
        }
        this.wasNullFlag = false;
        if (this.fields[columnIndexMinusOne].getMysqlType() == 16) {
            return getNumericRepresentationOfSQLBitType(columnIndex);
        }
        if (this.useFastIntParsing) {
            if (this.thisRow.length(columnIndexMinusOne) == 0) {
                return (long) convertToZeroWithEmptyCheck();
            }
            if (!this.thisRow.isFloatingPointNumber(columnIndexMinusOne)) {
                try {
                    return getLongWithOverflowCheck(columnIndexMinusOne, overflowCheck);
                } catch (NumberFormatException e) {
                    try {
                        return parseLongAsDouble(columnIndexMinusOne, this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getEncoding(), this.connection));
                    } catch (NumberFormatException e2) {
                        throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____79") + this.thisRow.getString(columnIndexMinusOne, this.fields[columnIndexMinusOne].getEncoding(), this.connection) + "'", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                    }
                }
            }
        }
        try {
            String val = getString(columnIndex);
            if (val == null) {
                return 0;
            }
            if (val.length() == 0) {
                return (long) convertToZeroWithEmptyCheck();
            }
            if (val.indexOf("e") == -1 && val.indexOf("E") == -1) {
                return parseLongWithOverflowCheck(columnIndexMinusOne, (byte[]) null, val, overflowCheck);
            }
            return parseLongAsDouble(columnIndexMinusOne, val);
        } catch (NumberFormatException e3) {
            try {
                return parseLongAsDouble(columnIndexMinusOne, (String) null);
            } catch (NumberFormatException e4) {
                throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____79") + null + "'", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
    }

    public long getLong(String columnName) throws SQLException {
        return getLong(findColumn(columnName));
    }

    private final long getLongFromString(String val, int columnIndexZeroBased) throws SQLException {
        if (val == null) {
            return 0;
        }
        try {
            if (val.length() == 0) {
                return (long) convertToZeroWithEmptyCheck();
            }
            if (val.indexOf("e") == -1 && val.indexOf("E") == -1) {
                return parseLongWithOverflowCheck(columnIndexZeroBased, (byte[]) null, val, true);
            }
            return parseLongAsDouble(columnIndexZeroBased, val);
        } catch (NumberFormatException e) {
            try {
                return parseLongAsDouble(columnIndexZeroBased, val);
            } catch (NumberFormatException e2) {
                throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____211") + val + Messages.getString("ResultSet.___in_column__212") + (columnIndexZeroBased + 1), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        checkClosed();
        return new ResultSetMetaData(this.fields, this.connection.getUseOldAliasMetadataBehavior(), this.connection.getYearIsDateType(), getExceptionInterceptor());
    }

    /* access modifiers changed from: protected */
    public Array getNativeArray(int i) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    /* access modifiers changed from: protected */
    public InputStream getNativeAsciiStream(int columnIndex) throws SQLException {
        checkRowPos();
        return getNativeBinaryStream(columnIndex);
    }

    /* access modifiers changed from: protected */
    public BigDecimal getNativeBigDecimal(int columnIndex) throws SQLException {
        checkColumnBounds(columnIndex);
        return getNativeBigDecimal(columnIndex, this.fields[columnIndex - 1].getDecimals());
    }

    /* access modifiers changed from: protected */
    public BigDecimal getNativeBigDecimal(int columnIndex, int scale) throws SQLException {
        String stringVal;
        checkColumnBounds(columnIndex);
        Field f = this.fields[columnIndex - 1];
        byte[] columnValue = this.thisRow.getColumnValue(columnIndex - 1);
        if (columnValue == null) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        switch (f.getSQLType()) {
            case 2:
            case 3:
                stringVal = StringUtils.toAsciiString(columnValue);
                break;
            default:
                stringVal = getNativeString(columnIndex);
                break;
        }
        return getBigDecimalFromString(stringVal, columnIndex, scale);
    }

    /* access modifiers changed from: protected */
    public InputStream getNativeBinaryStream(int columnIndex) throws SQLException {
        checkRowPos();
        int columnIndexMinusOne = columnIndex - 1;
        if (this.thisRow.isNull(columnIndexMinusOne)) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        switch (this.fields[columnIndexMinusOne].getSQLType()) {
            case -7:
            case -4:
            case -3:
            case -2:
            case 2004:
                return this.thisRow.getBinaryInputStream(columnIndexMinusOne);
            default:
                byte[] b = getNativeBytes(columnIndex, false);
                if (b != null) {
                    return new ByteArrayInputStream(b);
                }
                return null;
        }
    }

    /* access modifiers changed from: protected */
    public Blob getNativeBlob(int columnIndex) throws SQLException {
        byte[] dataAsBytes;
        checkRowPos();
        checkColumnBounds(columnIndex);
        byte[] dataAsBytes2 = this.thisRow.getColumnValue(columnIndex - 1);
        if (dataAsBytes2 == null) {
            this.wasNullFlag = true;
        } else {
            this.wasNullFlag = false;
        }
        if (this.wasNullFlag) {
            return null;
        }
        switch (this.fields[columnIndex - 1].getMysqlType()) {
            case 249:
            case ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION:
            case 251:
            case MysqlDefs.FIELD_TYPE_BLOB:
                dataAsBytes = dataAsBytes2;
                break;
            default:
                dataAsBytes = getNativeBytes(columnIndex, false);
                break;
        }
        if (!this.connection.getEmulateLocators()) {
            return new Blob(dataAsBytes, getExceptionInterceptor());
        }
        return new BlobFromLocator(this, columnIndex, getExceptionInterceptor());
    }

    public static boolean arraysEqual(byte[] left, byte[] right) {
        if (left == null) {
            if (right == null) {
                return true;
            }
            return false;
        } else if (right == null || left.length != right.length) {
            return false;
        } else {
            for (int i = 0; i < left.length; i++) {
                if (left[i] != right[i]) {
                    return false;
                }
            }
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public byte getNativeByte(int columnIndex) throws SQLException {
        return getNativeByte(columnIndex, true);
    }

    /* access modifiers changed from: protected */
    public byte getNativeByte(int columnIndex, boolean overflowCheck) throws SQLException {
        checkRowPos();
        checkColumnBounds(columnIndex);
        byte[] columnValue = this.thisRow.getColumnValue(columnIndex - 1);
        if (columnValue == null) {
            this.wasNullFlag = true;
            return 0;
        }
        this.wasNullFlag = false;
        int columnIndex2 = columnIndex - 1;
        Field field = this.fields[columnIndex2];
        switch (field.getMysqlType()) {
            case 1:
                byte valueAsByte = columnValue[0];
                if (!field.isUnsigned()) {
                    return valueAsByte;
                }
                short valueAsShort = valueAsByte >= 0 ? (short) valueAsByte : (short) (valueAsByte + 256);
                if (overflowCheck && this.jdbcCompliantTruncationForReads && valueAsShort > 127) {
                    throwRangeException(String.valueOf(valueAsShort), columnIndex2 + 1, -6);
                }
                return (byte) valueAsShort;
            case 2:
            case 13:
                short valueAsShort2 = getNativeShort(columnIndex2 + 1);
                if (overflowCheck && this.jdbcCompliantTruncationForReads && (valueAsShort2 < -128 || valueAsShort2 > 127)) {
                    throwRangeException(String.valueOf(valueAsShort2), columnIndex2 + 1, -6);
                }
                return (byte) valueAsShort2;
            case 3:
            case 9:
                int valueAsInt = getNativeInt(columnIndex2 + 1, false);
                if (overflowCheck && this.jdbcCompliantTruncationForReads && (valueAsInt < -128 || valueAsInt > 127)) {
                    throwRangeException(String.valueOf(valueAsInt), columnIndex2 + 1, -6);
                }
                return (byte) valueAsInt;
            case 4:
                float valueAsFloat = getNativeFloat(columnIndex2 + 1);
                if (overflowCheck && this.jdbcCompliantTruncationForReads && (valueAsFloat < -128.0f || valueAsFloat > 127.0f)) {
                    throwRangeException(String.valueOf(valueAsFloat), columnIndex2 + 1, -6);
                }
                return (byte) ((int) valueAsFloat);
            case 5:
                double valueAsDouble = getNativeDouble(columnIndex2 + 1);
                if (overflowCheck && this.jdbcCompliantTruncationForReads && (valueAsDouble < -128.0d || valueAsDouble > 127.0d)) {
                    throwRangeException(String.valueOf(valueAsDouble), columnIndex2 + 1, -6);
                }
                return (byte) ((int) valueAsDouble);
            case 8:
                long valueAsLong = getNativeLong(columnIndex2 + 1, false, true);
                if (overflowCheck && this.jdbcCompliantTruncationForReads && (valueAsLong < -128 || valueAsLong > 127)) {
                    throwRangeException(String.valueOf(valueAsLong), columnIndex2 + 1, -6);
                }
                return (byte) ((int) valueAsLong);
            case 16:
                long valueAsLong2 = getNumericRepresentationOfSQLBitType(columnIndex2 + 1);
                if (overflowCheck && this.jdbcCompliantTruncationForReads && (valueAsLong2 < -128 || valueAsLong2 > 127)) {
                    throwRangeException(String.valueOf(valueAsLong2), columnIndex2 + 1, -6);
                }
                return (byte) ((int) valueAsLong2);
            default:
                if (this.useUsageAdvisor) {
                    issueConversionViaParsingWarning("getByte()", columnIndex2, this.thisRow.getColumnValue(columnIndex2 - 1), this.fields[columnIndex2], new int[]{5, 1, 2, 3, 8, 4});
                }
                return getByteFromString(getNativeString(columnIndex2 + 1), columnIndex2 + 1);
        }
    }

    /* access modifiers changed from: protected */
    public byte[] getNativeBytes(int columnIndex, boolean noConversion) throws SQLException {
        checkRowPos();
        checkColumnBounds(columnIndex);
        byte[] columnValue = this.thisRow.getColumnValue(columnIndex - 1);
        if (columnValue == null) {
            this.wasNullFlag = true;
        } else {
            this.wasNullFlag = false;
        }
        if (this.wasNullFlag) {
            return null;
        }
        Field field = this.fields[columnIndex - 1];
        int mysqlType = field.getMysqlType();
        if (noConversion) {
            mysqlType = MysqlDefs.FIELD_TYPE_BLOB;
        }
        switch (mysqlType) {
            case 15:
            case 253:
            case 254:
                if (columnValue instanceof byte[]) {
                    return columnValue;
                }
                break;
            case 16:
            case 249:
            case ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION:
            case 251:
            case MysqlDefs.FIELD_TYPE_BLOB:
                return columnValue;
        }
        int sqlType = field.getSQLType();
        if (sqlType == -3 || sqlType == -2) {
            return columnValue;
        }
        return getBytesFromString(getNativeString(columnIndex));
    }

    /* access modifiers changed from: protected */
    public Reader getNativeCharacterStream(int columnIndex) throws SQLException {
        int columnIndexMinusOne = columnIndex - 1;
        switch (this.fields[columnIndexMinusOne].getSQLType()) {
            case -1:
            case 1:
            case 12:
            case 2005:
                if (this.thisRow.isNull(columnIndexMinusOne)) {
                    this.wasNullFlag = true;
                    return null;
                }
                this.wasNullFlag = false;
                return this.thisRow.getReader(columnIndexMinusOne);
            default:
                String asString = getStringForClob(columnIndex);
                if (asString == null) {
                    return null;
                }
                return getCharacterStreamFromString(asString);
        }
    }

    /* access modifiers changed from: protected */
    public Clob getNativeClob(int columnIndex) throws SQLException {
        String stringVal = getStringForClob(columnIndex);
        if (stringVal == null) {
            return null;
        }
        return getClobFromString(stringVal);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:156:0x0233, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:157:0x0234, code lost:
        r17 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:0x0259, code lost:
        throw com.mysql.jdbc.SQLError.createSQLException(com.mysql.jdbc.Messages.getString("ResultSet.Bad_format_for_BigDecimal", new java.lang.Object[]{r15, java.lang.Integer.valueOf(r24)}), com.mysql.jdbc.SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x02d3, code lost:
        r16 = r4;
        r19 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x02d8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:?, code lost:
        r19 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x0308, code lost:
        throw com.mysql.jdbc.SQLError.createSQLException(com.mysql.jdbc.Messages.getString("ResultSet.Class_not_found___91") + r0.toString() + com.mysql.jdbc.Messages.getString("ResultSet._while_reading_serialized_object_92"), getExceptionInterceptor());
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:150:0x0227, B:185:0x02b9] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getNativeConvertToString(int r24, com.mysql.jdbc.Field r25) throws java.sql.SQLException {
        /*
            r23 = this;
            r1 = r23
            r2 = r24
            com.mysql.jdbc.MySQLConnection r0 = r23.checkClosed()
            java.lang.Object r3 = r0.getConnectionMutex()
            monitor-enter(r3)
            int r0 = r25.getSQLType()     // Catch:{ all -> 0x038f }
            r4 = r0
            int r0 = r25.getMysqlType()     // Catch:{ all -> 0x038f }
            r5 = r0
            r6 = 2
            r7 = 1
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r13 = 0
            switch(r4) {
                case -7: goto L_0x0381;
                case -6: goto L_0x035a;
                case -5: goto L_0x0323;
                case -4: goto L_0x0272;
                case -3: goto L_0x0272;
                case -2: goto L_0x0272;
                case -1: goto L_0x0262;
                case 1: goto L_0x0262;
                case 2: goto L_0x01fc;
                case 3: goto L_0x01fc;
                case 4: goto L_0x01c6;
                case 5: goto L_0x019d;
                case 6: goto L_0x0185;
                case 7: goto L_0x016d;
                case 8: goto L_0x0185;
                case 12: goto L_0x0262;
                case 16: goto L_0x015a;
                case 91: goto L_0x00cf;
                case 92: goto L_0x00ad;
                case 93: goto L_0x0038;
                default: goto L_0x0023;
            }     // Catch:{ all -> 0x038f }
        L_0x0023:
            r18 = r4
            r4 = 0
            r0 = r4
            r6 = 0
            r7 = r6
            r8 = r10
            r9 = r11
            r11 = r4
            r12 = r4
            r15 = r6
            r16 = r6
            r17 = r6
            java.lang.String r19 = r1.extractStringFromNativeColumn(r2, r5)     // Catch:{ all -> 0x038f }
            goto L_0x038d
        L_0x0038:
            r15 = r8
            r16 = r9
            r17 = r8
            r18 = r8
            r19 = r8
            r20 = r9
            r21 = r9
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ all -> 0x038f }
            boolean r0 = r0.getNoDatetimeStringSync()     // Catch:{ all -> 0x038f }
            if (r0 == 0) goto L_0x0077
            byte[] r0 = r1.getNativeBytes(r2, r7)     // Catch:{ all -> 0x038f }
            if (r0 != 0) goto L_0x0055
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r9
        L_0x0055:
            int r9 = r0.length     // Catch:{ all -> 0x038f }
            if (r9 != 0) goto L_0x005c
            java.lang.String r6 = "0000-00-00 00:00:00"
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r6
        L_0x005c:
            byte r9 = r0[r8]     // Catch:{ all -> 0x038f }
            r9 = r9 & 255(0xff, float:3.57E-43)
            byte r7 = r0[r7]     // Catch:{ all -> 0x038f }
            r7 = r7 & 255(0xff, float:3.57E-43)
            int r7 = r7 << 8
            r7 = r7 | r9
            byte r9 = r0[r6]     // Catch:{ all -> 0x038f }
            r22 = 3
            byte r22 = r0[r22]     // Catch:{ all -> 0x038f }
            if (r7 != 0) goto L_0x0077
            if (r9 != 0) goto L_0x0077
            if (r22 != 0) goto L_0x0077
            java.lang.String r6 = "0000-00-00 00:00:00"
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r6
        L_0x0077:
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ all -> 0x038f }
            java.util.TimeZone r0 = r0.getDefaultTimeZone()     // Catch:{ all -> 0x038f }
            r7 = 0
            java.sql.Timestamp r0 = r1.getNativeTimestamp(r2, r7, r0, r8)     // Catch:{ all -> 0x038f }
            if (r0 != 0) goto L_0x0086
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r7
        L_0x0086:
            java.lang.String r7 = java.lang.String.valueOf(r0)     // Catch:{ all -> 0x038f }
            com.mysql.jdbc.MySQLConnection r9 = r1.connection     // Catch:{ all -> 0x038f }
            boolean r9 = r9.getNoDatetimeStringSync()     // Catch:{ all -> 0x038f }
            if (r9 != 0) goto L_0x0094
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r7
        L_0x0094:
            java.lang.String r9 = ".0"
            boolean r9 = r7.endsWith(r9)     // Catch:{ all -> 0x038f }
            if (r9 == 0) goto L_0x00a7
            int r9 = r7.length()     // Catch:{ all -> 0x038f }
            int r9 = r9 - r6
            java.lang.String r6 = r7.substring(r8, r9)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r6
        L_0x00a7:
            java.lang.String r6 = r1.extractStringFromNativeColumn(r2, r5)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r6
        L_0x00ad:
            r0 = r8
            r6 = 0
            r7 = r6
            r9 = r10
            r10 = r11
            r12 = r8
            r15 = r8
            r16 = r8
            r17 = r6
            com.mysql.jdbc.MySQLConnection r6 = r1.connection     // Catch:{ all -> 0x038f }
            java.util.TimeZone r6 = r6.getDefaultTimeZone()     // Catch:{ all -> 0x038f }
            r18 = r4
            r4 = 0
            java.sql.Time r6 = r1.getNativeTime(r2, r4, r6, r8)     // Catch:{ all -> 0x038f }
            if (r6 != 0) goto L_0x00c9
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r4
        L_0x00c9:
            java.lang.String r4 = java.lang.String.valueOf(r6)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r4
        L_0x00cf:
            r18 = r4
            r0 = r8
            r4 = 0
            r9 = r4
            r4 = r10
            r10 = r11
            r12 = r8
            r15 = r8
            r16 = r8
            r6 = 13
            if (r5 != r6) goto L_0x0116
            short r6 = r23.getNativeShort(r24)     // Catch:{ all -> 0x038f }
            com.mysql.jdbc.MySQLConnection r8 = r1.connection     // Catch:{ all -> 0x038f }
            boolean r8 = r8.getYearIsDateType()     // Catch:{ all -> 0x038f }
            if (r8 != 0) goto L_0x00f7
            boolean r7 = r1.wasNullFlag     // Catch:{ all -> 0x038f }
            if (r7 == 0) goto L_0x00f1
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            r3 = 0
            return r3
        L_0x00f1:
            java.lang.String r7 = java.lang.String.valueOf(r6)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r7
        L_0x00f7:
            long r19 = r25.getLength()     // Catch:{ all -> 0x038f }
            r21 = 2
            int r8 = (r19 > r21 ? 1 : (r19 == r21 ? 0 : -1))
            if (r8 != 0) goto L_0x010b
            r8 = 69
            if (r6 > r8) goto L_0x0108
            int r8 = r6 + 100
            short r6 = (short) r8     // Catch:{ all -> 0x038f }
        L_0x0108:
            int r8 = r6 + 1900
            short r6 = (short) r8     // Catch:{ all -> 0x038f }
        L_0x010b:
            r8 = 0
            java.sql.Date r7 = r1.fastDateCreate(r8, r6, r7, r7)     // Catch:{ all -> 0x038f }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r7
        L_0x0116:
            com.mysql.jdbc.MySQLConnection r6 = r1.connection     // Catch:{ all -> 0x038f }
            boolean r6 = r6.getNoDatetimeStringSync()     // Catch:{ all -> 0x038f }
            if (r6 == 0) goto L_0x014b
            byte[] r6 = r1.getNativeBytes(r2, r7)     // Catch:{ all -> 0x038f }
            if (r6 != 0) goto L_0x0127
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            r3 = 0
            return r3
        L_0x0127:
            int r7 = r6.length     // Catch:{ all -> 0x038f }
            if (r7 != 0) goto L_0x012e
            java.lang.String r7 = "0000-00-00"
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r7
        L_0x012e:
            byte r7 = r6[r8]     // Catch:{ all -> 0x038f }
            r7 = r7 & 255(0xff, float:3.57E-43)
            r8 = 1
            byte r8 = r6[r8]     // Catch:{ all -> 0x038f }
            r8 = r8 & 255(0xff, float:3.57E-43)
            int r8 = r8 << 8
            r7 = r7 | r8
            r8 = 2
            byte r8 = r6[r8]     // Catch:{ all -> 0x038f }
            r17 = 3
            byte r17 = r6[r17]     // Catch:{ all -> 0x038f }
            if (r7 != 0) goto L_0x014b
            if (r8 != 0) goto L_0x014b
            if (r17 != 0) goto L_0x014b
            java.lang.String r19 = "0000-00-00"
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r19
        L_0x014b:
            java.sql.Date r6 = r23.getNativeDate(r24)     // Catch:{ all -> 0x038f }
            if (r6 != 0) goto L_0x0154
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            r3 = 0
            return r3
        L_0x0154:
            java.lang.String r7 = java.lang.String.valueOf(r6)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r7
        L_0x015a:
            r18 = r4
            boolean r0 = r23.getBoolean((int) r24)     // Catch:{ all -> 0x038f }
            boolean r4 = r1.wasNullFlag     // Catch:{ all -> 0x038f }
            if (r4 == 0) goto L_0x0167
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            r3 = 0
            return r3
        L_0x0167:
            java.lang.String r4 = java.lang.String.valueOf(r0)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r4
        L_0x016d:
            r18 = r4
            r0 = r8
            r4 = r8
            r6 = r8
            r9 = r13
            r7 = r8
            float r8 = r23.getNativeFloat(r24)     // Catch:{ all -> 0x038f }
            boolean r11 = r1.wasNullFlag     // Catch:{ all -> 0x038f }
            if (r11 == 0) goto L_0x017f
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            r3 = 0
            return r3
        L_0x017f:
            java.lang.String r11 = java.lang.String.valueOf(r8)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r11
        L_0x0185:
            r18 = r4
            r0 = r8
            r4 = r10
            r6 = r8
            r7 = r8
            r9 = r13
            double r11 = r23.getNativeDouble(r24)     // Catch:{ all -> 0x038f }
            boolean r13 = r1.wasNullFlag     // Catch:{ all -> 0x038f }
            if (r13 == 0) goto L_0x0197
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            r3 = 0
            return r3
        L_0x0197:
            java.lang.String r13 = java.lang.String.valueOf(r11)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r13
        L_0x019d:
            r18 = r4
            r0 = r8
            r4 = r8
            r6 = r8
            int r7 = r1.getNativeInt(r2, r8)     // Catch:{ all -> 0x038f }
            boolean r8 = r1.wasNullFlag     // Catch:{ all -> 0x038f }
            if (r8 == 0) goto L_0x01ad
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            r3 = 0
            return r3
        L_0x01ad:
            boolean r8 = r25.isUnsigned()     // Catch:{ all -> 0x038f }
            if (r8 == 0) goto L_0x01c0
            if (r7 < 0) goto L_0x01b6
            goto L_0x01c0
        L_0x01b6:
            r8 = 65535(0xffff, float:9.1834E-41)
            r7 = r7 & r8
            java.lang.String r8 = java.lang.String.valueOf(r7)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r8
        L_0x01c0:
            java.lang.String r8 = java.lang.String.valueOf(r7)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r8
        L_0x01c6:
            r18 = r4
            r0 = r8
            r4 = r8
            r6 = r8
            r7 = r8
            int r8 = r1.getNativeInt(r2, r8)     // Catch:{ all -> 0x038f }
            r6 = r8
            boolean r8 = r1.wasNullFlag     // Catch:{ all -> 0x038f }
            if (r8 == 0) goto L_0x01d8
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            r3 = 0
            return r3
        L_0x01d8:
            boolean r8 = r25.isUnsigned()     // Catch:{ all -> 0x038f }
            if (r8 == 0) goto L_0x01f6
            if (r6 >= 0) goto L_0x01f6
            int r8 = r25.getMysqlType()     // Catch:{ all -> 0x038f }
            r9 = 9
            if (r8 != r9) goto L_0x01e9
            goto L_0x01f6
        L_0x01e9:
            long r8 = (long) r6     // Catch:{ all -> 0x038f }
            r10 = 4294967295(0xffffffff, double:2.1219957905E-314)
            long r8 = r8 & r10
            java.lang.String r10 = java.lang.String.valueOf(r8)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r10
        L_0x01f6:
            java.lang.String r8 = java.lang.String.valueOf(r6)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r8
        L_0x01fc:
            r18 = r4
            r4 = r8
            r6 = r10
            r9 = r11
            r7 = r8
            r11 = r8
            r12 = r13
            r14 = r8
            com.mysql.jdbc.ResultSetRow r0 = r1.thisRow     // Catch:{ all -> 0x038f }
            int r15 = r2 + -1
            byte[] r0 = r0.getColumnValue(r15)     // Catch:{ all -> 0x038f }
            java.lang.String r0 = com.mysql.jdbc.StringUtils.toAsciiString(r0)     // Catch:{ all -> 0x038f }
            r15 = r0
            if (r15 == 0) goto L_0x025a
            r1.wasNullFlag = r8     // Catch:{ all -> 0x038f }
            int r0 = r15.length()     // Catch:{ all -> 0x038f }
            if (r0 != 0) goto L_0x0227
            java.math.BigDecimal r0 = new java.math.BigDecimal     // Catch:{ all -> 0x038f }
            r0.<init>(r8)     // Catch:{ all -> 0x038f }
            java.lang.String r8 = r0.toString()     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r8
        L_0x0227:
            java.math.BigDecimal r0 = new java.math.BigDecimal     // Catch:{ NumberFormatException -> 0x0233 }
            r0.<init>(r15)     // Catch:{ NumberFormatException -> 0x0233 }
            java.lang.String r8 = r0.toString()     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r8
        L_0x0233:
            r0 = move-exception
            r16 = 0
            java.lang.String r8 = "ResultSet.Bad_format_for_BigDecimal"
            r21 = r4
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x038f }
            r17 = 0
            r4[r17] = r15     // Catch:{ all -> 0x038f }
            java.lang.Integer r17 = java.lang.Integer.valueOf(r24)     // Catch:{ all -> 0x038f }
            r19 = 1
            r4[r19] = r17     // Catch:{ all -> 0x038f }
            java.lang.String r4 = com.mysql.jdbc.Messages.getString(r8, r4)     // Catch:{ all -> 0x038f }
            java.lang.String r8 = "S1009"
            r17 = r0
            com.mysql.jdbc.ExceptionInterceptor r0 = r23.getExceptionInterceptor()     // Catch:{ all -> 0x038f }
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r4, (java.lang.String) r8, (com.mysql.jdbc.ExceptionInterceptor) r0)     // Catch:{ all -> 0x038f }
            throw r0     // Catch:{ all -> 0x038f }
        L_0x025a:
            r21 = r4
            r4 = 1
            r1.wasNullFlag = r4     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            r3 = 0
            return r3
        L_0x0262:
            r18 = r4
            r4 = 0
            r0 = r4
            r6 = 0
            r7 = r10
            r8 = r11
            r10 = r4
            r11 = r4
            r12 = r13
            java.lang.String r14 = r1.extractStringFromNativeColumn(r2, r5)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r14
        L_0x0272:
            r18 = r4
            r4 = 0
            r6 = r4
            r7 = 0
            r8 = r10
            r9 = r11
            r11 = r4
            r12 = r4
            r15 = r4
            boolean r0 = r25.isBlob()     // Catch:{ all -> 0x038f }
            if (r0 != 0) goto L_0x0288
            java.lang.String r0 = r1.extractStringFromNativeColumn(r2, r5)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r0
        L_0x0288:
            boolean r0 = r25.isBinary()     // Catch:{ all -> 0x038f }
            if (r0 != 0) goto L_0x0294
            java.lang.String r0 = r1.extractStringFromNativeColumn(r2, r5)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r0
        L_0x0294:
            byte[] r0 = r23.getBytes((int) r24)     // Catch:{ all -> 0x038f }
            r4 = r0
            r16 = r4
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ all -> 0x038f }
            boolean r0 = r0.getAutoDeserialize()     // Catch:{ all -> 0x038f }
            if (r0 == 0) goto L_0x0319
            if (r4 == 0) goto L_0x0314
            int r0 = r4.length     // Catch:{ all -> 0x038f }
            r21 = r6
            r6 = 2
            if (r0 < r6) goto L_0x0311
            r6 = 0
            byte r0 = r4[r6]     // Catch:{ all -> 0x038f }
            r6 = -84
            if (r0 != r6) goto L_0x0309
            r6 = 1
            byte r0 = r4[r6]     // Catch:{ all -> 0x038f }
            r6 = -19
            if (r0 != r6) goto L_0x0309
            java.io.ByteArrayInputStream r0 = new java.io.ByteArrayInputStream     // Catch:{ ClassNotFoundException -> 0x02d8, IOException -> 0x02d2 }
            r0.<init>(r4)     // Catch:{ ClassNotFoundException -> 0x02d8, IOException -> 0x02d2 }
            java.io.ObjectInputStream r6 = new java.io.ObjectInputStream     // Catch:{ ClassNotFoundException -> 0x02d8, IOException -> 0x02d2 }
            r6.<init>(r0)     // Catch:{ ClassNotFoundException -> 0x02d8, IOException -> 0x02d2 }
            java.lang.Object r17 = r6.readObject()     // Catch:{ ClassNotFoundException -> 0x02d8, IOException -> 0x02d2 }
            r16 = r17
            r6.close()     // Catch:{ ClassNotFoundException -> 0x02d8, IOException -> 0x02d2 }
            r0.close()     // Catch:{ ClassNotFoundException -> 0x02d8, IOException -> 0x02d2 }
            r19 = r4
            goto L_0x030b
        L_0x02d2:
            r0 = move-exception
            r16 = r4
            r19 = r4
            goto L_0x030b
        L_0x02d8:
            r0 = move-exception
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x038f }
            r6.<init>()     // Catch:{ all -> 0x038f }
            java.lang.String r17 = "ResultSet.Class_not_found___91"
            r19 = r4
            java.lang.String r4 = com.mysql.jdbc.Messages.getString(r17)     // Catch:{ all -> 0x038f }
            java.lang.StringBuilder r4 = r6.append(r4)     // Catch:{ all -> 0x038f }
            java.lang.String r6 = r0.toString()     // Catch:{ all -> 0x038f }
            java.lang.StringBuilder r4 = r4.append(r6)     // Catch:{ all -> 0x038f }
            java.lang.String r6 = "ResultSet._while_reading_serialized_object_92"
            java.lang.String r6 = com.mysql.jdbc.Messages.getString(r6)     // Catch:{ all -> 0x038f }
            java.lang.StringBuilder r4 = r4.append(r6)     // Catch:{ all -> 0x038f }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x038f }
            com.mysql.jdbc.ExceptionInterceptor r6 = r23.getExceptionInterceptor()     // Catch:{ all -> 0x038f }
            java.sql.SQLException r4 = com.mysql.jdbc.SQLError.createSQLException(r4, r6)     // Catch:{ all -> 0x038f }
            throw r4     // Catch:{ all -> 0x038f }
        L_0x0309:
            r19 = r4
        L_0x030b:
            java.lang.String r0 = r16.toString()     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r0
        L_0x0311:
            r19 = r4
            goto L_0x031d
        L_0x0314:
            r19 = r4
            r21 = r6
            goto L_0x031d
        L_0x0319:
            r19 = r4
            r21 = r6
        L_0x031d:
            java.lang.String r0 = r1.extractStringFromNativeColumn(r2, r5)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r0
        L_0x0323:
            r18 = r4
            r4 = 0
            r0 = r4
            r6 = r4
            r7 = r4
            r8 = r13
            r10 = r4
            boolean r11 = r25.isUnsigned()     // Catch:{ all -> 0x038f }
            if (r11 != 0) goto L_0x0344
            r11 = 1
            long r11 = r1.getNativeLong(r2, r4, r11)     // Catch:{ all -> 0x038f }
            r8 = r11
            boolean r4 = r1.wasNullFlag     // Catch:{ all -> 0x038f }
            if (r4 == 0) goto L_0x033e
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            r3 = 0
            return r3
        L_0x033e:
            java.lang.String r4 = java.lang.String.valueOf(r8)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r4
        L_0x0344:
            r4 = 0
            long r8 = r1.getNativeLong(r2, r4, r4)     // Catch:{ all -> 0x038f }
            boolean r4 = r1.wasNullFlag     // Catch:{ all -> 0x038f }
            if (r4 == 0) goto L_0x0350
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            r3 = 0
            return r3
        L_0x0350:
            java.math.BigInteger r4 = convertLongToUlong(r8)     // Catch:{ all -> 0x038f }
            java.lang.String r4 = java.lang.String.valueOf(r4)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r4
        L_0x035a:
            r18 = r4
            r4 = 0
            r0 = r4
            byte r4 = r1.getNativeByte(r2, r4)     // Catch:{ all -> 0x038f }
            boolean r6 = r1.wasNullFlag     // Catch:{ all -> 0x038f }
            if (r6 == 0) goto L_0x0369
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            r3 = 0
            return r3
        L_0x0369:
            boolean r6 = r25.isUnsigned()     // Catch:{ all -> 0x038f }
            if (r6 == 0) goto L_0x037b
            if (r4 < 0) goto L_0x0372
            goto L_0x037b
        L_0x0372:
            r6 = r4 & 255(0xff, float:3.57E-43)
            short r6 = (short) r6     // Catch:{ all -> 0x038f }
            java.lang.String r7 = java.lang.String.valueOf(r6)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r7
        L_0x037b:
            java.lang.String r6 = java.lang.String.valueOf(r4)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r6
        L_0x0381:
            r18 = r4
            long r6 = r23.getNumericRepresentationOfSQLBitType(r24)     // Catch:{ all -> 0x038f }
            java.lang.String r0 = java.lang.String.valueOf(r6)     // Catch:{ all -> 0x038f }
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r0
        L_0x038d:
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            return r19
        L_0x038f:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x038f }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ResultSetImpl.getNativeConvertToString(int, com.mysql.jdbc.Field):java.lang.String");
    }

    /* access modifiers changed from: protected */
    public Date getNativeDate(int columnIndex) throws SQLException {
        return getNativeDate(columnIndex, (Calendar) null);
    }

    /* access modifiers changed from: protected */
    public Date getNativeDate(int columnIndex, Calendar cal) throws SQLException {
        Date dateToReturn;
        Calendar calendar = cal;
        checkRowPos();
        checkColumnBounds(columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
        if (mysqlType == 10) {
            dateToReturn = this.thisRow.getNativeDate(columnIndexMinusOne, this.connection, this, calendar);
        } else {
            TimeZone tz = calendar != null ? cal.getTimeZone() : getDefaultTimeZone();
            TimeZone timeZone = tz;
            dateToReturn = (Date) this.thisRow.getNativeDateTimeValue(columnIndexMinusOne, (Calendar) null, 91, mysqlType, tz, tz != null && !tz.equals(getDefaultTimeZone()), this.connection, this);
        }
        if (dateToReturn == null) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        return dateToReturn;
    }

    /* access modifiers changed from: package-private */
    public Date getNativeDateViaParseConversion(int columnIndex) throws SQLException {
        if (this.useUsageAdvisor) {
            issueConversionViaParsingWarning("getDate()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[columnIndex - 1], new int[]{10});
        }
        return getDateFromString(getNativeString(columnIndex), columnIndex, (Calendar) null);
    }

    /* access modifiers changed from: protected */
    public double getNativeDouble(int columnIndex) throws SQLException {
        checkRowPos();
        checkColumnBounds(columnIndex);
        int columnIndex2 = columnIndex - 1;
        if (this.thisRow.isNull(columnIndex2)) {
            this.wasNullFlag = true;
            return 0.0d;
        }
        this.wasNullFlag = false;
        Field f = this.fields[columnIndex2];
        switch (f.getMysqlType()) {
            case 1:
                if (!f.isUnsigned()) {
                    return (double) getNativeByte(columnIndex2 + 1);
                }
                return (double) getNativeShort(columnIndex2 + 1);
            case 2:
            case 13:
                if (!f.isUnsigned()) {
                    return (double) getNativeShort(columnIndex2 + 1);
                }
                return (double) getNativeInt(columnIndex2 + 1);
            case 3:
            case 9:
                if (!f.isUnsigned()) {
                    return (double) getNativeInt(columnIndex2 + 1);
                }
                return (double) getNativeLong(columnIndex2 + 1);
            case 4:
                return (double) getNativeFloat(columnIndex2 + 1);
            case 5:
                return this.thisRow.getNativeDouble(columnIndex2);
            case 8:
                long valueAsLong = getNativeLong(columnIndex2 + 1);
                if (!f.isUnsigned()) {
                    return (double) valueAsLong;
                }
                return convertLongToUlong(valueAsLong).doubleValue();
            case 16:
                return (double) getNumericRepresentationOfSQLBitType(columnIndex2 + 1);
            default:
                String stringVal = getNativeString(columnIndex2 + 1);
                if (this.useUsageAdvisor) {
                    issueConversionViaParsingWarning("getDouble()", columnIndex2, stringVal, this.fields[columnIndex2], new int[]{5, 1, 2, 3, 8, 4});
                }
                return getDoubleFromString(stringVal, columnIndex2 + 1);
        }
    }

    /* access modifiers changed from: protected */
    public float getNativeFloat(int columnIndex) throws SQLException {
        checkRowPos();
        checkColumnBounds(columnIndex);
        int columnIndex2 = columnIndex - 1;
        if (this.thisRow.isNull(columnIndex2)) {
            this.wasNullFlag = true;
            return 0.0f;
        }
        this.wasNullFlag = false;
        Field f = this.fields[columnIndex2];
        switch (f.getMysqlType()) {
            case 1:
                if (!f.isUnsigned()) {
                    return (float) getNativeByte(columnIndex2 + 1);
                }
                return (float) getNativeShort(columnIndex2 + 1);
            case 2:
            case 13:
                if (!f.isUnsigned()) {
                    return (float) getNativeShort(columnIndex2 + 1);
                }
                return (float) getNativeInt(columnIndex2 + 1);
            case 3:
            case 9:
                if (!f.isUnsigned()) {
                    return (float) getNativeInt(columnIndex2 + 1);
                }
                return (float) getNativeLong(columnIndex2 + 1);
            case 4:
                return this.thisRow.getNativeFloat(columnIndex2);
            case 5:
                Double valueAsDouble = new Double(getNativeDouble(columnIndex2 + 1));
                float valueAsFloat = valueAsDouble.floatValue();
                if ((this.jdbcCompliantTruncationForReads && valueAsFloat == Float.NEGATIVE_INFINITY) || valueAsFloat == Float.POSITIVE_INFINITY) {
                    throwRangeException(valueAsDouble.toString(), columnIndex2 + 1, 6);
                }
                return (float) getNativeDouble(columnIndex2 + 1);
            case 8:
                long valueAsLong = getNativeLong(columnIndex2 + 1);
                if (!f.isUnsigned()) {
                    return (float) valueAsLong;
                }
                return convertLongToUlong(valueAsLong).floatValue();
            case 16:
                return (float) getNumericRepresentationOfSQLBitType(columnIndex2 + 1);
            default:
                String stringVal = getNativeString(columnIndex2 + 1);
                if (this.useUsageAdvisor) {
                    issueConversionViaParsingWarning("getFloat()", columnIndex2, stringVal, this.fields[columnIndex2], new int[]{5, 1, 2, 3, 8, 4});
                }
                return getFloatFromString(stringVal, columnIndex2 + 1);
        }
    }

    /* access modifiers changed from: protected */
    public int getNativeInt(int columnIndex) throws SQLException {
        return getNativeInt(columnIndex, true);
    }

    /* access modifiers changed from: protected */
    public int getNativeInt(int columnIndex, boolean overflowCheck) throws SQLException {
        String stringVal;
        checkRowPos();
        checkColumnBounds(columnIndex);
        int columnIndex2 = columnIndex - 1;
        if (this.thisRow.isNull(columnIndex2)) {
            this.wasNullFlag = true;
            return 0;
        }
        this.wasNullFlag = false;
        Field f = this.fields[columnIndex2];
        switch (f.getMysqlType()) {
            case 1:
                byte tinyintVal = getNativeByte(columnIndex2 + 1, false);
                if (!f.isUnsigned() || tinyintVal >= 0) {
                    return tinyintVal;
                }
                return tinyintVal + 256;
            case 2:
            case 13:
                short asShort = getNativeShort(columnIndex2 + 1, false);
                if (!f.isUnsigned() || asShort >= 0) {
                    return asShort;
                }
                return 65536 + asShort;
            case 3:
            case 9:
                int valueAsInt = this.thisRow.getNativeInt(columnIndex2);
                if (!f.isUnsigned()) {
                    return valueAsInt;
                }
                long j = (long) valueAsInt;
                if (valueAsInt < 0) {
                    j += 4294967296L;
                }
                long valueAsLong = j;
                if (overflowCheck && this.jdbcCompliantTruncationForReads && valueAsLong > 2147483647L) {
                    throwRangeException(String.valueOf(valueAsLong), columnIndex2 + 1, 4);
                }
                return (int) valueAsLong;
            case 4:
                double valueAsDouble = (double) getNativeFloat(columnIndex2 + 1);
                if (overflowCheck && this.jdbcCompliantTruncationForReads && (valueAsDouble < -2.147483648E9d || valueAsDouble > 2.147483647E9d)) {
                    throwRangeException(String.valueOf(valueAsDouble), columnIndex2 + 1, 4);
                }
                return (int) valueAsDouble;
            case 5:
                double valueAsDouble2 = getNativeDouble(columnIndex2 + 1);
                if (overflowCheck && this.jdbcCompliantTruncationForReads && (valueAsDouble2 < -2.147483648E9d || valueAsDouble2 > 2.147483647E9d)) {
                    throwRangeException(String.valueOf(valueAsDouble2), columnIndex2 + 1, 4);
                }
                return (int) valueAsDouble2;
            case 8:
                long valueAsLong2 = getNativeLong(columnIndex2 + 1, false, true);
                if (overflowCheck && this.jdbcCompliantTruncationForReads && (valueAsLong2 < -2147483648L || valueAsLong2 > 2147483647L)) {
                    throwRangeException(String.valueOf(valueAsLong2), columnIndex2 + 1, 4);
                }
                return (int) valueAsLong2;
            case 16:
                long valueAsLong3 = getNumericRepresentationOfSQLBitType(columnIndex2 + 1);
                if (overflowCheck && this.jdbcCompliantTruncationForReads && (valueAsLong3 < -2147483648L || valueAsLong3 > 2147483647L)) {
                    throwRangeException(String.valueOf(valueAsLong3), columnIndex2 + 1, 4);
                }
                return (int) valueAsLong3;
            default:
                String stringVal2 = getNativeString(columnIndex2 + 1);
                if (this.useUsageAdvisor) {
                    Field field = f;
                    stringVal = stringVal2;
                    issueConversionViaParsingWarning("getInt()", columnIndex2, stringVal2, this.fields[columnIndex2], new int[]{5, 1, 2, 3, 8, 4});
                } else {
                    Field field2 = f;
                    stringVal = stringVal2;
                }
                return getIntFromString(stringVal, columnIndex2 + 1);
        }
    }

    /* access modifiers changed from: protected */
    public long getNativeLong(int columnIndex) throws SQLException {
        return getNativeLong(columnIndex, true, true);
    }

    /* access modifiers changed from: protected */
    public long getNativeLong(int columnIndex, boolean overflowCheck, boolean expandUnsignedLong) throws SQLException {
        checkRowPos();
        checkColumnBounds(columnIndex);
        int columnIndex2 = columnIndex - 1;
        if (this.thisRow.isNull(columnIndex2)) {
            this.wasNullFlag = true;
            return 0;
        }
        this.wasNullFlag = false;
        Field f = this.fields[columnIndex2];
        switch (f.getMysqlType()) {
            case 1:
                if (!f.isUnsigned()) {
                    return (long) getNativeByte(columnIndex2 + 1);
                }
                return (long) getNativeInt(columnIndex2 + 1);
            case 2:
                if (!f.isUnsigned()) {
                    return (long) getNativeShort(columnIndex2 + 1);
                }
                return (long) getNativeInt(columnIndex2 + 1, false);
            case 3:
            case 9:
                int asInt = getNativeInt(columnIndex2 + 1, false);
                if (!f.isUnsigned() || asInt >= 0) {
                    return (long) asInt;
                }
                return ((long) asInt) + 4294967296L;
            case 4:
                double valueAsDouble = (double) getNativeFloat(columnIndex2 + 1);
                if (overflowCheck && this.jdbcCompliantTruncationForReads && (valueAsDouble < -9.223372036854776E18d || valueAsDouble > 9.223372036854776E18d)) {
                    throwRangeException(String.valueOf(valueAsDouble), columnIndex2 + 1, -5);
                }
                return (long) valueAsDouble;
            case 5:
                double valueAsDouble2 = getNativeDouble(columnIndex2 + 1);
                if (overflowCheck && this.jdbcCompliantTruncationForReads && (valueAsDouble2 < -9.223372036854776E18d || valueAsDouble2 > 9.223372036854776E18d)) {
                    throwRangeException(String.valueOf(valueAsDouble2), columnIndex2 + 1, -5);
                }
                return (long) valueAsDouble2;
            case 8:
                long valueAsLong = this.thisRow.getNativeLong(columnIndex2);
                if (!f.isUnsigned() || !expandUnsignedLong) {
                    return valueAsLong;
                }
                BigInteger asBigInt = convertLongToUlong(valueAsLong);
                if (overflowCheck && this.jdbcCompliantTruncationForReads && (asBigInt.compareTo(new BigInteger(String.valueOf(Long.MAX_VALUE))) > 0 || asBigInt.compareTo(new BigInteger(String.valueOf(Long.MIN_VALUE))) < 0)) {
                    throwRangeException(asBigInt.toString(), columnIndex2 + 1, -5);
                }
                return getLongFromString(asBigInt.toString(), columnIndex2);
            case 13:
                return (long) getNativeShort(columnIndex2 + 1);
            case 16:
                return getNumericRepresentationOfSQLBitType(columnIndex2 + 1);
            default:
                String stringVal = getNativeString(columnIndex2 + 1);
                if (this.useUsageAdvisor) {
                    issueConversionViaParsingWarning("getLong()", columnIndex2, stringVal, this.fields[columnIndex2], new int[]{5, 1, 2, 3, 8, 4});
                }
                return getLongFromString(stringVal, columnIndex2 + 1);
        }
    }

    /* access modifiers changed from: protected */
    public Ref getNativeRef(int i) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    /* access modifiers changed from: protected */
    public short getNativeShort(int columnIndex) throws SQLException {
        return getNativeShort(columnIndex, true);
    }

    /* access modifiers changed from: protected */
    public short getNativeShort(int columnIndex, boolean overflowCheck) throws SQLException {
        String stringVal;
        checkRowPos();
        checkColumnBounds(columnIndex);
        int columnIndex2 = columnIndex - 1;
        if (this.thisRow.isNull(columnIndex2)) {
            this.wasNullFlag = true;
            return 0;
        }
        this.wasNullFlag = false;
        Field f = this.fields[columnIndex2];
        switch (f.getMysqlType()) {
            case 1:
                byte tinyintVal = getNativeByte(columnIndex2 + 1, false);
                if (!f.isUnsigned() || tinyintVal >= 0) {
                    return (short) tinyintVal;
                }
                return (short) (tinyintVal + 256);
            case 2:
            case 13:
                short asShort = this.thisRow.getNativeShort(columnIndex2);
                if (!f.isUnsigned()) {
                    return asShort;
                }
                int valueAsInt = 65535 & asShort;
                if (overflowCheck && this.jdbcCompliantTruncationForReads && valueAsInt > 32767) {
                    throwRangeException(String.valueOf(valueAsInt), columnIndex2 + 1, 5);
                }
                return (short) valueAsInt;
            case 3:
            case 9:
                if (!f.isUnsigned()) {
                    int valueAsInt2 = getNativeInt(columnIndex2 + 1, false);
                    if ((overflowCheck && this.jdbcCompliantTruncationForReads && valueAsInt2 > 32767) || valueAsInt2 < -32768) {
                        throwRangeException(String.valueOf(valueAsInt2), columnIndex2 + 1, 5);
                    }
                    return (short) valueAsInt2;
                }
                long valueAsLong = getNativeLong(columnIndex2 + 1, false, true);
                if (overflowCheck && this.jdbcCompliantTruncationForReads && valueAsLong > 32767) {
                    throwRangeException(String.valueOf(valueAsLong), columnIndex2 + 1, 5);
                }
                return (short) ((int) valueAsLong);
            case 4:
                float valueAsFloat = getNativeFloat(columnIndex2 + 1);
                if (overflowCheck && this.jdbcCompliantTruncationForReads && (valueAsFloat < -32768.0f || valueAsFloat > 32767.0f)) {
                    throwRangeException(String.valueOf(valueAsFloat), columnIndex2 + 1, 5);
                }
                return (short) ((int) valueAsFloat);
            case 5:
                double valueAsDouble = getNativeDouble(columnIndex2 + 1);
                if (overflowCheck && this.jdbcCompliantTruncationForReads && (valueAsDouble < -32768.0d || valueAsDouble > 32767.0d)) {
                    throwRangeException(String.valueOf(valueAsDouble), columnIndex2 + 1, 5);
                }
                return (short) ((int) valueAsDouble);
            case 8:
                long valueAsLong2 = getNativeLong(columnIndex2 + 1, false, false);
                if (!f.isUnsigned()) {
                    if (overflowCheck && this.jdbcCompliantTruncationForReads && (valueAsLong2 < -32768 || valueAsLong2 > 32767)) {
                        throwRangeException(String.valueOf(valueAsLong2), columnIndex2 + 1, 5);
                    }
                    return (short) ((int) valueAsLong2);
                }
                BigInteger asBigInt = convertLongToUlong(valueAsLong2);
                if (overflowCheck && this.jdbcCompliantTruncationForReads && (asBigInt.compareTo(new BigInteger(String.valueOf(32767))) > 0 || asBigInt.compareTo(new BigInteger(String.valueOf(-32768))) < 0)) {
                    throwRangeException(asBigInt.toString(), columnIndex2 + 1, 5);
                }
                return (short) getIntFromString(asBigInt.toString(), columnIndex2 + 1);
            case 16:
                long valueAsLong3 = getNumericRepresentationOfSQLBitType(columnIndex2 + 1);
                if (overflowCheck && this.jdbcCompliantTruncationForReads && (valueAsLong3 < -32768 || valueAsLong3 > 32767)) {
                    throwRangeException(String.valueOf(valueAsLong3), columnIndex2 + 1, 5);
                }
                return (short) ((int) valueAsLong3);
            default:
                String stringVal2 = getNativeString(columnIndex2 + 1);
                if (this.useUsageAdvisor) {
                    Field field = f;
                    stringVal = stringVal2;
                    issueConversionViaParsingWarning("getShort()", columnIndex2, stringVal2, this.fields[columnIndex2], new int[]{5, 1, 2, 3, 8, 4});
                } else {
                    Field field2 = f;
                    stringVal = stringVal2;
                }
                return getShortFromString(stringVal, columnIndex2 + 1);
        }
    }

    /* access modifiers changed from: protected */
    public String getNativeString(int columnIndex) throws SQLException {
        checkRowPos();
        checkColumnBounds(columnIndex);
        if (this.fields == null) {
            throw SQLError.createSQLException(Messages.getString("ResultSet.Query_generated_no_fields_for_ResultSet_133"), SQLError.SQL_STATE_INVALID_COLUMN_NUMBER, getExceptionInterceptor());
        } else if (this.thisRow.isNull(columnIndex - 1)) {
            this.wasNullFlag = true;
            return null;
        } else {
            this.wasNullFlag = false;
            Field field = this.fields[columnIndex - 1];
            String stringVal = getNativeConvertToString(columnIndex, field);
            int mysqlType = field.getMysqlType();
            if (mysqlType == 7 || mysqlType == 10 || !field.isZeroFill() || stringVal == null) {
                return stringVal;
            }
            int origLength = stringVal.length();
            StringBuilder zeroFillBuf = new StringBuilder(origLength);
            long numZeros = field.getLength() - ((long) origLength);
            for (long i = 0; i < numZeros; i++) {
                zeroFillBuf.append('0');
            }
            zeroFillBuf.append(stringVal);
            return zeroFillBuf.toString();
        }
    }

    private Time getNativeTime(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
        Time timeVal;
        checkRowPos();
        checkColumnBounds(columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
        if (mysqlType == 11) {
            timeVal = this.thisRow.getNativeTime(columnIndexMinusOne, targetCalendar, tz, rollForward, this.connection, this);
        } else {
            timeVal = (Time) this.thisRow.getNativeDateTimeValue(columnIndexMinusOne, (Calendar) null, 92, mysqlType, tz, rollForward, this.connection, this);
        }
        if (timeVal == null) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        return timeVal;
    }

    /* access modifiers changed from: package-private */
    public Time getNativeTimeViaParseConversion(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
        if (this.useUsageAdvisor) {
            issueConversionViaParsingWarning("getTime()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[columnIndex - 1], new int[]{11});
        }
        return getTimeFromString(getNativeString(columnIndex), targetCalendar, columnIndex, tz, rollForward);
    }

    private Timestamp getNativeTimestamp(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
        Timestamp tsVal;
        checkRowPos();
        checkColumnBounds(columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        int mysqlType = this.fields[columnIndexMinusOne].getMysqlType();
        switch (mysqlType) {
            case 7:
            case 12:
                tsVal = this.thisRow.getNativeTimestamp(columnIndexMinusOne, targetCalendar, tz, rollForward, this.connection, this);
                break;
            default:
                tsVal = (Timestamp) this.thisRow.getNativeDateTimeValue(columnIndexMinusOne, (Calendar) null, 93, mysqlType, tz, rollForward, this.connection, this);
                break;
        }
        if (tsVal == null) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        return tsVal;
    }

    /* access modifiers changed from: package-private */
    public Timestamp getNativeTimestampViaParseConversion(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
        if (this.useUsageAdvisor) {
            issueConversionViaParsingWarning("getTimestamp()", columnIndex, this.thisRow.getColumnValue(columnIndex - 1), this.fields[columnIndex - 1], new int[]{7, 12});
        }
        return getTimestampFromString(columnIndex, targetCalendar, getNativeString(columnIndex), tz, rollForward);
    }

    /* access modifiers changed from: protected */
    public InputStream getNativeUnicodeStream(int columnIndex) throws SQLException {
        checkRowPos();
        return getBinaryStream(columnIndex);
    }

    /* access modifiers changed from: protected */
    public URL getNativeURL(int colIndex) throws SQLException {
        String val = getString(colIndex);
        if (val == null) {
            return null;
        }
        try {
            return new URL(val);
        } catch (MalformedURLException e) {
            throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____141") + val + "'", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
    }

    public synchronized ResultSetInternalMethods getNextResultSet() {
        return this.nextResultSet;
    }

    public Object getObject(int columnIndex) throws SQLException {
        checkRowPos();
        checkColumnBounds(columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        if (this.thisRow.isNull(columnIndexMinusOne)) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        Field field = this.fields[columnIndexMinusOne];
        switch (field.getSQLType()) {
            case -7:
                if (field.getMysqlType() != 16 || field.isSingleBit()) {
                    return Boolean.valueOf(getBoolean(columnIndex));
                }
                return getObjectDeserializingIfNeeded(columnIndex);
            case -6:
                if (!field.isUnsigned()) {
                    return Integer.valueOf(getByte(columnIndex));
                }
                return Integer.valueOf(getInt(columnIndex));
            case -5:
                if (!field.isUnsigned()) {
                    return Long.valueOf(getLong(columnIndex));
                }
                String stringVal = getString(columnIndex);
                if (stringVal == null) {
                    return null;
                }
                try {
                    return new BigInteger(stringVal);
                } catch (NumberFormatException e) {
                    throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigInteger", new Object[]{Integer.valueOf(columnIndex), stringVal}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                }
            case -4:
            case -3:
            case -2:
                if (field.getMysqlType() == 255) {
                    return getBytes(columnIndex);
                }
                return getObjectDeserializingIfNeeded(columnIndex);
            case -1:
                if (!field.isOpaqueBinary()) {
                    return getStringForClob(columnIndex);
                }
                return getBytes(columnIndex);
            case 1:
            case 12:
                if (!field.isOpaqueBinary()) {
                    return getString(columnIndex);
                }
                return getBytes(columnIndex);
            case 2:
            case 3:
                String stringVal2 = getString(columnIndex);
                if (stringVal2 == null) {
                    return null;
                }
                if (stringVal2.length() == 0) {
                    return new BigDecimal(0);
                }
                try {
                    return new BigDecimal(stringVal2);
                } catch (NumberFormatException e2) {
                    throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[]{stringVal2, Integer.valueOf(columnIndex)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                }
            case 4:
                if (!field.isUnsigned() || field.getMysqlType() == 9) {
                    return Integer.valueOf(getInt(columnIndex));
                }
                return Long.valueOf(getLong(columnIndex));
            case 5:
                return Integer.valueOf(getInt(columnIndex));
            case 6:
            case 8:
                return new Double(getDouble(columnIndex));
            case 7:
                return new Float(getFloat(columnIndex));
            case 16:
                return Boolean.valueOf(getBoolean(columnIndex));
            case 91:
                if (field.getMysqlType() != 13 || this.connection.getYearIsDateType()) {
                    return getDate(columnIndex);
                }
                return Short.valueOf(getShort(columnIndex));
            case 92:
                return getTime(columnIndex);
            case 93:
                return getTimestamp(columnIndex);
            default:
                return getString(columnIndex);
        }
    }

    private Object getObjectDeserializingIfNeeded(int columnIndex) throws SQLException {
        Field field = this.fields[columnIndex - 1];
        if (!field.isBinary() && !field.isBlob()) {
            return getBytes(columnIndex);
        }
        byte[] data = getBytes(columnIndex);
        if (!this.connection.getAutoDeserialize()) {
            return data;
        }
        Object obj = data;
        if (data == null || data.length < 2) {
            return obj;
        }
        if (data[0] != -84 || data[1] != -19) {
            return getString(columnIndex);
        }
        try {
            ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
            ObjectInputStream objIn = new ObjectInputStream(bytesIn);
            Object obj2 = objIn.readObject();
            objIn.close();
            bytesIn.close();
            return obj2;
        } catch (ClassNotFoundException cnfe) {
            throw SQLError.createSQLException(Messages.getString("ResultSet.Class_not_found___91") + cnfe.toString() + Messages.getString("ResultSet._while_reading_serialized_object_92"), getExceptionInterceptor());
        } catch (IOException e) {
            return data;
        }
    }

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        if (type == null) {
            throw SQLError.createSQLException("Type parameter can not be null", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        } else if (type.equals(String.class)) {
            return getString(columnIndex);
        } else {
            if (type.equals(BigDecimal.class)) {
                return getBigDecimal(columnIndex);
            }
            if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
                return Boolean.valueOf(getBoolean(columnIndex));
            }
            if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
                return Integer.valueOf(getInt(columnIndex));
            }
            if (type.equals(Long.class) || type.equals(Long.TYPE)) {
                return Long.valueOf(getLong(columnIndex));
            }
            if (type.equals(Float.class) || type.equals(Float.TYPE)) {
                return Float.valueOf(getFloat(columnIndex));
            }
            if (type.equals(Double.class) || type.equals(Double.TYPE)) {
                return Double.valueOf(getDouble(columnIndex));
            }
            if (type.equals(byte[].class)) {
                return getBytes(columnIndex);
            }
            if (type.equals(Date.class)) {
                return getDate(columnIndex);
            }
            if (type.equals(Time.class)) {
                return getTime(columnIndex);
            }
            if (type.equals(Timestamp.class)) {
                return getTimestamp(columnIndex);
            }
            if (type.equals(Clob.class)) {
                return getClob(columnIndex);
            }
            if (type.equals(Blob.class)) {
                return getBlob(columnIndex);
            }
            if (type.equals(Array.class)) {
                return getArray(columnIndex);
            }
            if (type.equals(Ref.class)) {
                return getRef(columnIndex);
            }
            if (type.equals(URL.class)) {
                return getURL(columnIndex);
            }
            if (this.connection.getAutoDeserialize()) {
                try {
                    return type.cast(getObject(columnIndex));
                } catch (ClassCastException cce) {
                    SQLException sqlEx = SQLError.createSQLException("Conversion not supported for type " + type.getName(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                    sqlEx.initCause(cce);
                    throw sqlEx;
                }
            } else {
                throw SQLError.createSQLException("Conversion not supported for type " + type.getName(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
    }

    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return getObject(findColumn(columnLabel), type);
    }

    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
        return getObject(i);
    }

    public Object getObject(String columnName) throws SQLException {
        return getObject(findColumn(columnName));
    }

    public Object getObject(String colName, Map<String, Class<?>> map) throws SQLException {
        return getObject(findColumn(colName), map);
    }

    public Object getObjectStoredProc(int columnIndex, int desiredSqlType) throws SQLException {
        checkRowPos();
        checkColumnBounds(columnIndex);
        if (this.thisRow.getColumnValue(columnIndex - 1) == null) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        Field field = this.fields[columnIndex - 1];
        switch (desiredSqlType) {
            case -7:
            case 16:
                return Boolean.valueOf(getBoolean(columnIndex));
            case -6:
                return Integer.valueOf(getInt(columnIndex));
            case -5:
                if (field.isUnsigned()) {
                    return getBigDecimal(columnIndex);
                }
                return Long.valueOf(getLong(columnIndex));
            case -4:
            case -3:
            case -2:
                return getBytes(columnIndex);
            case -1:
                return getStringForClob(columnIndex);
            case 1:
            case 12:
                return getString(columnIndex);
            case 2:
            case 3:
                String stringVal = getString(columnIndex);
                if (stringVal == null) {
                    return null;
                }
                if (stringVal.length() == 0) {
                    return new BigDecimal(0);
                }
                try {
                    return new BigDecimal(stringVal);
                } catch (NumberFormatException e) {
                    throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[]{stringVal, Integer.valueOf(columnIndex)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                }
            case 4:
                if (!field.isUnsigned() || field.getMysqlType() == 9) {
                    return Integer.valueOf(getInt(columnIndex));
                }
                return Long.valueOf(getLong(columnIndex));
            case 5:
                return Integer.valueOf(getInt(columnIndex));
            case 6:
                if (!this.connection.getRunningCTS13()) {
                    return new Double((double) getFloat(columnIndex));
                }
                return new Float(getFloat(columnIndex));
            case 7:
                return new Float(getFloat(columnIndex));
            case 8:
                return new Double(getDouble(columnIndex));
            case 91:
                if (field.getMysqlType() != 13 || this.connection.getYearIsDateType()) {
                    return getDate(columnIndex);
                }
                return Short.valueOf(getShort(columnIndex));
            case 92:
                return getTime(columnIndex);
            case 93:
                return getTimestamp(columnIndex);
            default:
                return getString(columnIndex);
        }
    }

    public Object getObjectStoredProc(int i, Map<Object, Object> map, int desiredSqlType) throws SQLException {
        return getObjectStoredProc(i, desiredSqlType);
    }

    public Object getObjectStoredProc(String columnName, int desiredSqlType) throws SQLException {
        return getObjectStoredProc(findColumn(columnName), desiredSqlType);
    }

    public Object getObjectStoredProc(String colName, Map<Object, Object> map, int desiredSqlType) throws SQLException {
        return getObjectStoredProc(findColumn(colName), map, desiredSqlType);
    }

    public Ref getRef(int i) throws SQLException {
        checkColumnBounds(i);
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    public Ref getRef(String colName) throws SQLException {
        return getRef(findColumn(colName));
    }

    public int getRow() throws SQLException {
        checkClosed();
        int currentRowNumber = this.rowData.getCurrentRowNumber();
        if (this.rowData.isDynamic()) {
            return currentRowNumber + 1;
        }
        if (currentRowNumber < 0 || this.rowData.isAfterLast() || this.rowData.isEmpty()) {
            return 0;
        }
        return currentRowNumber + 1;
    }

    public String getServerInfo() {
        String str;
        try {
            synchronized (checkClosed().getConnectionMutex()) {
                str = this.serverInfo;
            }
            return str;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private long getNumericRepresentationOfSQLBitType(int columnIndex) throws SQLException {
        byte[] asBytes = this.thisRow.getColumnValue(columnIndex - 1);
        if (this.fields[columnIndex - 1].isSingleBit() || asBytes.length == 1) {
            return (long) asBytes[0];
        }
        byte[] asBytes2 = asBytes;
        int shift = 0;
        long[] steps = new long[asBytes2.length];
        for (int i = asBytes2.length - 1; i >= 0; i--) {
            steps[i] = ((long) (asBytes2[i] & 255)) << shift;
            shift += 8;
        }
        long valueAsLong = 0;
        for (int i2 = 0; i2 < asBytes2.length; i2++) {
            valueAsLong |= steps[i2];
        }
        return valueAsLong;
    }

    public short getShort(int columnIndex) throws SQLException {
        checkRowPos();
        checkColumnBounds(columnIndex);
        if (this.isBinaryEncoded) {
            return getNativeShort(columnIndex);
        }
        if (this.thisRow.isNull(columnIndex - 1)) {
            this.wasNullFlag = true;
            return 0;
        }
        this.wasNullFlag = false;
        if (this.fields[columnIndex - 1].getMysqlType() == 16) {
            long valueAsLong = getNumericRepresentationOfSQLBitType(columnIndex);
            if (this.jdbcCompliantTruncationForReads && (valueAsLong < -32768 || valueAsLong > 32767)) {
                throwRangeException(String.valueOf(valueAsLong), columnIndex, 5);
            }
            return (short) ((int) valueAsLong);
        }
        if (this.useFastIntParsing) {
            byte[] shortAsBytes = this.thisRow.getColumnValue(columnIndex - 1);
            if (shortAsBytes.length == 0) {
                return (short) convertToZeroWithEmptyCheck();
            }
            boolean needsFullParse = false;
            int i = 0;
            while (true) {
                if (i >= shortAsBytes.length) {
                    break;
                } else if (((char) shortAsBytes[i]) == 'e' || ((char) shortAsBytes[i]) == 'E') {
                    needsFullParse = true;
                } else {
                    i++;
                }
            }
            needsFullParse = true;
            if (!needsFullParse) {
                try {
                    return parseShortWithOverflowCheck(columnIndex, shortAsBytes, (String) null);
                } catch (NumberFormatException e) {
                    try {
                        return parseShortAsDouble(columnIndex, StringUtils.toString(shortAsBytes));
                    } catch (NumberFormatException e2) {
                        throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____96") + StringUtils.toString(shortAsBytes) + "'", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                    }
                }
            }
        }
        try {
            String val = getString(columnIndex);
            if (val == null) {
                return 0;
            }
            if (val.length() == 0) {
                return (short) convertToZeroWithEmptyCheck();
            }
            if (val.indexOf("e") == -1 && val.indexOf("E") == -1 && val.indexOf(".") == -1) {
                return parseShortWithOverflowCheck(columnIndex, (byte[]) null, val);
            }
            return parseShortAsDouble(columnIndex, val);
        } catch (NumberFormatException e3) {
            try {
                return parseShortAsDouble(columnIndex, (String) null);
            } catch (NumberFormatException e4) {
                throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____96") + null + "'", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
    }

    public short getShort(String columnName) throws SQLException {
        return getShort(findColumn(columnName));
    }

    private final short getShortFromString(String val, int columnIndex) throws SQLException {
        if (val == null) {
            return 0;
        }
        try {
            if (val.length() == 0) {
                return (short) convertToZeroWithEmptyCheck();
            }
            if (val.indexOf("e") == -1 && val.indexOf("E") == -1 && val.indexOf(".") == -1) {
                return parseShortWithOverflowCheck(columnIndex, (byte[]) null, val);
            }
            return parseShortAsDouble(columnIndex, val);
        } catch (NumberFormatException e) {
            try {
                return parseShortAsDouble(columnIndex, val);
            } catch (NumberFormatException e2) {
                throw SQLError.createSQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____217") + val + Messages.getString("ResultSet.___in_column__218") + columnIndex, SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
    }

    public Statement getStatement() throws SQLException {
        try {
            synchronized (checkClosed().getConnectionMutex()) {
                Statement statement = this.wrapperStatement;
                if (statement != null) {
                    return statement;
                }
                StatementImpl statementImpl = this.owningStatement;
                return statementImpl;
            }
        } catch (SQLException e) {
            if (this.retainOwningStatement) {
                Statement statement2 = this.wrapperStatement;
                if (statement2 != null) {
                    return statement2;
                }
                return this.owningStatement;
            }
            throw SQLError.createSQLException("Operation not allowed on closed ResultSet. Statements can be retained over result set closure by setting the connection property \"retainStatementAfterResultSetClose\" to \"true\".", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0019, code lost:
        r2 = ((int) r1.getLength()) / r1.getMaxBytesPerCharacter();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getString(int r9) throws java.sql.SQLException {
        /*
            r8 = this;
            r0 = 1
            java.lang.String r0 = r8.getStringInternal(r9, r0)
            boolean r1 = r8.padCharsWithSpace
            if (r1 == 0) goto L_0x003d
            if (r0 == 0) goto L_0x003d
            com.mysql.jdbc.Field[] r1 = r8.fields
            int r2 = r9 + -1
            r1 = r1[r2]
            int r2 = r1.getMysqlType()
            r3 = 254(0xfe, float:3.56E-43)
            if (r2 != r3) goto L_0x003d
            long r2 = r1.getLength()
            int r2 = (int) r2
            int r3 = r1.getMaxBytesPerCharacter()
            int r2 = r2 / r3
            int r3 = r0.length()
            if (r3 >= r2) goto L_0x003d
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>(r2)
            r4.append(r0)
            int r5 = r2 - r3
            char[] r6 = EMPTY_SPACE
            r7 = 0
            r4.append(r6, r7, r5)
            java.lang.String r0 = r4.toString()
        L_0x003d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ResultSetImpl.getString(int):java.lang.String");
    }

    public String getString(String columnName) throws SQLException {
        return getString(findColumn(columnName));
    }

    private String getStringForClob(int columnIndex) throws SQLException {
        byte[] asBytes;
        String forcedEncoding = this.connection.getClobCharacterEncoding();
        if (forcedEncoding != null) {
            try {
                if (!this.isBinaryEncoded) {
                    asBytes = getBytes(columnIndex);
                } else {
                    asBytes = getNativeBytes(columnIndex, true);
                }
                if (asBytes != null) {
                    return StringUtils.toString(asBytes, forcedEncoding);
                }
                return null;
            } catch (UnsupportedEncodingException e) {
                throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        } else if (!this.isBinaryEncoded) {
            return getString(columnIndex);
        } else {
            return getNativeString(columnIndex);
        }
    }

    /* access modifiers changed from: protected */
    public String getStringInternal(int columnIndex, boolean checkDateTypes) throws SQLException {
        int i = columnIndex;
        if (this.isBinaryEncoded) {
            return getNativeString(columnIndex);
        }
        checkRowPos();
        checkColumnBounds(columnIndex);
        if (this.fields != null) {
            int internalColumnIndex = i - 1;
            if (this.thisRow.isNull(internalColumnIndex)) {
                this.wasNullFlag = true;
                return null;
            }
            this.wasNullFlag = false;
            Field metadata = this.fields[internalColumnIndex];
            if (metadata.getMysqlType() != 16) {
                String stringVal = this.thisRow.getString(internalColumnIndex, metadata.getCollationIndex() == 63 ? this.connection.getEncoding() : metadata.getEncoding(), this.connection);
                if (metadata.getMysqlType() != 13) {
                    if (checkDateTypes && !this.connection.getNoDatetimeStringSync()) {
                        switch (metadata.getSQLType()) {
                            case 91:
                                Date dt = getDateFromString(stringVal, i, (Calendar) null);
                                if (dt == null) {
                                    this.wasNullFlag = true;
                                    return null;
                                }
                                this.wasNullFlag = false;
                                return dt.toString();
                            case 92:
                                Time tm = getTimeFromString(stringVal, (Calendar) null, columnIndex, getDefaultTimeZone(), false);
                                if (tm == null) {
                                    this.wasNullFlag = true;
                                    return null;
                                }
                                this.wasNullFlag = false;
                                return tm.toString();
                            case 93:
                                Timestamp ts = getTimestampFromString(columnIndex, (Calendar) null, stringVal, getDefaultTimeZone(), false);
                                if (ts == null) {
                                    this.wasNullFlag = true;
                                    return null;
                                }
                                this.wasNullFlag = false;
                                return ts.toString();
                        }
                    }
                    return stringVal;
                } else if (!this.connection.getYearIsDateType()) {
                    return stringVal;
                } else {
                    Date dt2 = getDateFromString(stringVal, i, (Calendar) null);
                    if (dt2 == null) {
                        this.wasNullFlag = true;
                        return null;
                    }
                    this.wasNullFlag = false;
                    return dt2.toString();
                }
            } else if (!metadata.isSingleBit()) {
                return String.valueOf(getNumericRepresentationOfSQLBitType(columnIndex));
            } else {
                byte[] value = this.thisRow.getColumnValue(internalColumnIndex);
                if (value.length == 0) {
                    return String.valueOf(convertToZeroWithEmptyCheck());
                }
                return String.valueOf(value[0]);
            }
        } else {
            throw SQLError.createSQLException(Messages.getString("ResultSet.Query_generated_no_fields_for_ResultSet_99"), SQLError.SQL_STATE_INVALID_COLUMN_NUMBER, getExceptionInterceptor());
        }
    }

    public Time getTime(int columnIndex) throws SQLException {
        return getTimeInternal(columnIndex, (Calendar) null, getDefaultTimeZone(), false);
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return getTimeInternal(columnIndex, cal, cal != null ? cal.getTimeZone() : getDefaultTimeZone(), true);
    }

    public Time getTime(String columnName) throws SQLException {
        return getTime(findColumn(columnName));
    }

    public Time getTime(String columnName, Calendar cal) throws SQLException {
        return getTime(findColumn(columnName), cal);
    }

    private Time getTimeFromString(String timeAsString, Calendar targetCalendar, int columnIndex, TimeZone tz, boolean rollForward) throws SQLException {
        String timeAsString2;
        int min;
        int hr;
        int sec;
        Calendar sessionCalendar;
        int sec2;
        int min2;
        int hr2;
        Calendar calendar = targetCalendar;
        int i = columnIndex;
        synchronized (checkClosed().getConnectionMutex()) {
            if (timeAsString == null) {
                try {
                    this.wasNullFlag = true;
                    try {
                        return null;
                    } catch (Throwable th) {
                        ex = th;
                        String str = timeAsString;
                        throw ex;
                    }
                } catch (RuntimeException e) {
                    ex = e;
                    String str2 = timeAsString;
                    SQLException sqlEx = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                    sqlEx.initCause(ex);
                    throw sqlEx;
                }
            } else {
                String timeAsString3 = timeAsString.trim();
                try {
                    int dec = timeAsString3.indexOf(".");
                    int sec3 = 0;
                    if (dec > -1) {
                        timeAsString2 = timeAsString3.substring(0, dec);
                    } else {
                        timeAsString2 = timeAsString3;
                    }
                    try {
                        if (!timeAsString2.equals("0") && !timeAsString2.equals("0000-00-00") && !timeAsString2.equals("0000-00-00 00:00:00")) {
                            if (!timeAsString2.equals("00000000000000")) {
                                this.wasNullFlag = false;
                                Field timeColField = this.fields[i - 1];
                                if (timeColField.getMysqlType() == 7) {
                                    int length = timeAsString2.length();
                                    switch (length) {
                                        case 10:
                                            hr2 = Integer.parseInt(timeAsString2.substring(6, 8));
                                            min2 = Integer.parseInt(timeAsString2.substring(8, 10));
                                            sec2 = 0;
                                            break;
                                        case 12:
                                        case 14:
                                            hr2 = Integer.parseInt(timeAsString2.substring(length - 6, length - 4));
                                            min2 = Integer.parseInt(timeAsString2.substring(length - 4, length - 2));
                                            sec2 = Integer.parseInt(timeAsString2.substring(length - 2, length));
                                            break;
                                        case 19:
                                            hr2 = Integer.parseInt(timeAsString2.substring(length - 8, length - 6));
                                            min2 = Integer.parseInt(timeAsString2.substring(length - 5, length - 3));
                                            sec2 = Integer.parseInt(timeAsString2.substring(length - 2, length));
                                            break;
                                        default:
                                            throw SQLError.createSQLException(Messages.getString("ResultSet.Timestamp_too_small_to_convert_to_Time_value_in_column__257") + i + "(" + this.fields[i - 1] + ").", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                                    }
                                    SQLWarning precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_TIMESTAMP_to_Time_with_getTime()_on_column__261") + i + "(" + this.fields[i - 1] + ").");
                                    SQLWarning sQLWarning = this.warningChain;
                                    if (sQLWarning == null) {
                                        this.warningChain = precisionLost;
                                    } else {
                                        sQLWarning.setNextWarning(precisionLost);
                                    }
                                    hr = hr2;
                                    min = min2;
                                    sec = sec2;
                                } else if (timeColField.getMysqlType() == 12) {
                                    int hr3 = Integer.parseInt(timeAsString2.substring(11, 13));
                                    int min3 = Integer.parseInt(timeAsString2.substring(14, 16));
                                    int sec4 = Integer.parseInt(timeAsString2.substring(17, 19));
                                    SQLWarning precisionLost2 = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_DATETIME_to_Time_with_getTime()_on_column__264") + i + "(" + this.fields[i - 1] + ").");
                                    SQLWarning sQLWarning2 = this.warningChain;
                                    if (sQLWarning2 == null) {
                                        this.warningChain = precisionLost2;
                                    } else {
                                        sQLWarning2.setNextWarning(precisionLost2);
                                    }
                                    hr = hr3;
                                    min = min3;
                                    sec = sec4;
                                } else if (timeColField.getMysqlType() == 10) {
                                    Time fastTimeCreate = fastTimeCreate(calendar, 0, 0, 0);
                                    try {
                                        return fastTimeCreate;
                                    } catch (Throwable th2) {
                                        ex = th2;
                                        throw ex;
                                    }
                                } else {
                                    if (timeAsString2.length() != 5) {
                                        if (timeAsString2.length() != 8) {
                                            throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Time____267") + timeAsString2 + Messages.getString("ResultSet.___in_column__268") + i, SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                                        }
                                    }
                                    int hr4 = Integer.parseInt(timeAsString2.substring(0, 2));
                                    int min4 = Integer.parseInt(timeAsString2.substring(3, 5));
                                    if (timeAsString2.length() != 5) {
                                        sec3 = Integer.parseInt(timeAsString2.substring(6));
                                    }
                                    hr = hr4;
                                    min = min4;
                                    sec = sec3;
                                }
                                try {
                                    sessionCalendar = getCalendarInstanceForSessionOrNew();
                                    Calendar calendar2 = sessionCalendar;
                                } catch (RuntimeException e2) {
                                    ex = e2;
                                    int i2 = hr;
                                    int i3 = min;
                                    int i4 = sec;
                                    SQLException sqlEx2 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                                    sqlEx2.initCause(ex);
                                    throw sqlEx2;
                                }
                                try {
                                    Time changeTimezone = TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, fastTimeCreate(sessionCalendar, hr, min, sec), this.connection.getServerTimezoneTZ(), tz, rollForward);
                                    return changeTimezone;
                                } catch (RuntimeException e3) {
                                    ex = e3;
                                    int i5 = hr;
                                    int i6 = min;
                                    int i7 = sec;
                                    SQLException sqlEx22 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                                    sqlEx22.initCause(ex);
                                    throw sqlEx22;
                                }
                            }
                        }
                        if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior())) {
                            this.wasNullFlag = true;
                            return null;
                        } else if (!"exception".equals(this.connection.getZeroDateTimeBehavior())) {
                            Time fastTimeCreate2 = fastTimeCreate(calendar, 0, 0, 0);
                            return fastTimeCreate2;
                        } else {
                            throw SQLError.createSQLException("Value '" + timeAsString2 + "' can not be represented as java.sql.Time", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                        }
                    } catch (RuntimeException e4) {
                        ex = e4;
                        SQLException sqlEx222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                        sqlEx222.initCause(ex);
                        throw sqlEx222;
                    }
                } catch (RuntimeException e5) {
                    ex = e5;
                    String str3 = timeAsString3;
                    SQLException sqlEx2222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                    sqlEx2222.initCause(ex);
                    throw sqlEx2222;
                } catch (Throwable th3) {
                    ex = th3;
                    String str4 = timeAsString3;
                    throw ex;
                }
            }
        }
    }

    private Time getTimeInternal(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
        checkRowPos();
        if (this.isBinaryEncoded) {
            return getNativeTime(columnIndex, targetCalendar, tz, rollForward);
        }
        if (!this.useFastDateParsing) {
            return getTimeFromString(getStringInternal(columnIndex, false), targetCalendar, columnIndex, tz, rollForward);
        }
        checkColumnBounds(columnIndex);
        int columnIndexMinusOne = columnIndex - 1;
        if (this.thisRow.isNull(columnIndexMinusOne)) {
            this.wasNullFlag = true;
            return null;
        }
        this.wasNullFlag = false;
        return this.thisRow.getTimeFast(columnIndexMinusOne, targetCalendar, tz, rollForward, this.connection, this);
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return getTimestampInternal(columnIndex, (Calendar) null, getDefaultTimeZone(), false);
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return getTimestampInternal(columnIndex, cal, cal != null ? cal.getTimeZone() : getDefaultTimeZone(), true);
    }

    public Timestamp getTimestamp(String columnName) throws SQLException {
        return getTimestamp(findColumn(columnName));
    }

    public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
        return getTimestamp(findColumn(columnName), cal);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:127:0x03e2, code lost:
        if (r11.useLegacyDatetimeCode != 0) goto L_0x03f9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x03f8, code lost:
        return com.mysql.jdbc.TimeUtil.fastTimestampCreate(r37, r0, r3, r5, r7, r1, r6, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x03fb, code lost:
        r24 = r4;
        r4 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x0429, code lost:
        return com.mysql.jdbc.TimeUtil.changeTimezone(r11.connection, r12, r35, fastTimestampCreate(r12, r0, r3, r5, r7, r1, r6, r2, r10), r11.connection.getServerTimezoneTZ(), r37, r38);
     */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00d9 A[SYNTHETIC, Splitter:B:52:0x00d9] */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0136 A[Catch:{ RuntimeException -> 0x0451 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.sql.Timestamp getTimestampFromString(int r34, java.util.Calendar r35, java.lang.String r36, java.util.TimeZone r37, boolean r38) throws java.sql.SQLException {
        /*
            r33 = this;
            r15 = r33
            r14 = r34
            java.lang.String r0 = "."
            java.lang.String r13 = "S1009"
            r1 = 0
            r15.wasNullFlag = r1     // Catch:{ RuntimeException -> 0x045a }
            r2 = 0
            r3 = 1
            if (r36 != 0) goto L_0x001a
            r15.wasNullFlag = r3     // Catch:{ RuntimeException -> 0x0012 }
            return r2
        L_0x0012:
            r0 = move-exception
            r8 = r36
            r9 = r13
            r4 = r14
            r11 = r15
            goto L_0x0460
        L_0x001a:
            java.lang.String r4 = r36.trim()     // Catch:{ RuntimeException -> 0x045a }
            r11 = r4
            int r4 = r11.length()     // Catch:{ RuntimeException -> 0x0454 }
            r9 = r4
            com.mysql.jdbc.MySQLConnection r4 = r15.connection     // Catch:{ RuntimeException -> 0x0454 }
            boolean r4 = r4.getUseJDBCCompliantTimezoneShift()     // Catch:{ RuntimeException -> 0x0454 }
            if (r4 == 0) goto L_0x003a
            com.mysql.jdbc.MySQLConnection r4 = r15.connection     // Catch:{ RuntimeException -> 0x0033 }
            java.util.Calendar r4 = r4.getUtcCalendar()     // Catch:{ RuntimeException -> 0x0033 }
            goto L_0x003e
        L_0x0033:
            r0 = move-exception
            r8 = r11
            r9 = r13
            r4 = r14
            r11 = r15
            goto L_0x0460
        L_0x003a:
            java.util.Calendar r4 = r33.getCalendarInstanceForSessionOrNew()     // Catch:{ RuntimeException -> 0x0454 }
        L_0x003e:
            r12 = r4
            com.mysql.jdbc.MySQLConnection r4 = r15.connection     // Catch:{ RuntimeException -> 0x0454 }
            boolean r10 = r4.getUseGmtMillisForDatetimes()     // Catch:{ RuntimeException -> 0x0454 }
            if (r9 <= 0) goto L_0x00c8
            char r4 = r11.charAt(r1)     // Catch:{ RuntimeException -> 0x0033 }
            r5 = 48
            if (r4 != r5) goto L_0x00c8
            java.lang.String r4 = "0000-00-00"
            boolean r4 = r11.equals(r4)     // Catch:{ RuntimeException -> 0x0033 }
            if (r4 != 0) goto L_0x0073
            java.lang.String r4 = "0000-00-00 00:00:00"
            boolean r4 = r11.equals(r4)     // Catch:{ RuntimeException -> 0x0033 }
            if (r4 != 0) goto L_0x0073
            java.lang.String r4 = "00000000000000"
            boolean r4 = r11.equals(r4)     // Catch:{ RuntimeException -> 0x0033 }
            if (r4 != 0) goto L_0x0073
            java.lang.String r4 = "0"
            boolean r4 = r11.equals(r4)     // Catch:{ RuntimeException -> 0x0033 }
            if (r4 == 0) goto L_0x0070
            goto L_0x0073
        L_0x0070:
            r23 = r9
            goto L_0x00ca
        L_0x0073:
            java.lang.String r0 = "convertToNull"
            com.mysql.jdbc.MySQLConnection r1 = r15.connection     // Catch:{ RuntimeException -> 0x0033 }
            java.lang.String r1 = r1.getZeroDateTimeBehavior()     // Catch:{ RuntimeException -> 0x0033 }
            boolean r0 = r0.equals(r1)     // Catch:{ RuntimeException -> 0x0033 }
            if (r0 == 0) goto L_0x0084
            r15.wasNullFlag = r3     // Catch:{ RuntimeException -> 0x0033 }
            return r2
        L_0x0084:
            java.lang.String r0 = "exception"
            com.mysql.jdbc.MySQLConnection r1 = r15.connection     // Catch:{ RuntimeException -> 0x0033 }
            java.lang.String r1 = r1.getZeroDateTimeBehavior()     // Catch:{ RuntimeException -> 0x0033 }
            boolean r0 = r0.equals(r1)     // Catch:{ RuntimeException -> 0x0033 }
            if (r0 != 0) goto L_0x00a4
            r2 = 0
            r3 = 1
            r4 = 1
            r5 = 1
            r6 = 0
            r7 = 0
            r8 = 0
            r0 = 0
            r1 = r33
            r23 = r9
            r9 = r0
            java.sql.Timestamp r0 = r1.fastTimestampCreate(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ RuntimeException -> 0x0033 }
            return r0
        L_0x00a4:
            r23 = r9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x0033 }
            r0.<init>()     // Catch:{ RuntimeException -> 0x0033 }
            java.lang.String r1 = "Value '"
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ RuntimeException -> 0x0033 }
            java.lang.StringBuilder r0 = r0.append(r11)     // Catch:{ RuntimeException -> 0x0033 }
            java.lang.String r1 = "' can not be represented as java.sql.Timestamp"
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ RuntimeException -> 0x0033 }
            java.lang.String r0 = r0.toString()     // Catch:{ RuntimeException -> 0x0033 }
            com.mysql.jdbc.ExceptionInterceptor r1 = r33.getExceptionInterceptor()     // Catch:{ RuntimeException -> 0x0033 }
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r0, (java.lang.String) r13, (com.mysql.jdbc.ExceptionInterceptor) r1)     // Catch:{ RuntimeException -> 0x0033 }
            throw r0     // Catch:{ RuntimeException -> 0x0033 }
        L_0x00c8:
            r23 = r9
        L_0x00ca:
            com.mysql.jdbc.Field[] r2 = r15.fields     // Catch:{ RuntimeException -> 0x0454 }
            int r3 = r14 + -1
            r2 = r2[r3]     // Catch:{ RuntimeException -> 0x0454 }
            int r2 = r2.getMysqlType()     // Catch:{ RuntimeException -> 0x0454 }
            r3 = 13
            r4 = 4
            if (r2 != r3) goto L_0x0136
            boolean r0 = r15.useLegacyDatetimeCode     // Catch:{ RuntimeException -> 0x012f }
            if (r0 != 0) goto L_0x00f2
            java.lang.String r0 = r11.substring(r1, r4)     // Catch:{ RuntimeException -> 0x0033 }
            int r2 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0033 }
            r3 = 1
            r4 = 1
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r1 = r37
            java.sql.Timestamp r0 = com.mysql.jdbc.TimeUtil.fastTimestampCreate(r1, r2, r3, r4, r5, r6, r7, r8)     // Catch:{ RuntimeException -> 0x0033 }
            return r0
        L_0x00f2:
            com.mysql.jdbc.MySQLConnection r0 = r15.connection     // Catch:{ RuntimeException -> 0x012f }
            java.lang.String r1 = r11.substring(r1, r4)     // Catch:{ RuntimeException -> 0x012f }
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ RuntimeException -> 0x012f }
            r2 = 1
            r3 = 1
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r8 = r11
            r11 = r33
            r9 = r13
            r13 = r1
            r7 = r14
            r14 = r2
            r6 = r15
            r15 = r3
            r20 = r10
            java.sql.Timestamp r4 = r11.fastTimestampCreate(r12, r13, r14, r15, r16, r17, r18, r19, r20)     // Catch:{ RuntimeException -> 0x012a }
            com.mysql.jdbc.MySQLConnection r1 = r6.connection     // Catch:{ RuntimeException -> 0x012a }
            java.util.TimeZone r5 = r1.getServerTimezoneTZ()     // Catch:{ RuntimeException -> 0x012a }
            r1 = r0
            r2 = r12
            r3 = r35
            r11 = r6
            r6 = r37
            r15 = r7
            r7 = r38
            java.sql.Timestamp r0 = com.mysql.jdbc.TimeUtil.changeTimezone((com.mysql.jdbc.MySQLConnection) r1, (java.util.Calendar) r2, (java.util.Calendar) r3, (java.sql.Timestamp) r4, (java.util.TimeZone) r5, (java.util.TimeZone) r6, (boolean) r7)     // Catch:{ RuntimeException -> 0x0451 }
            return r0
        L_0x012a:
            r0 = move-exception
            r11 = r6
            r4 = r7
            goto L_0x0460
        L_0x012f:
            r0 = move-exception
            r8 = r11
            r9 = r13
            r11 = r15
            r4 = r14
            goto L_0x0459
        L_0x0136:
            r8 = r11
            r9 = r13
            r11 = r15
            r15 = r14
            r2 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r13 = 0
            r14 = 0
            r16 = 0
            int r17 = r8.indexOf(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r36 = r17
            r3 = r23
            int r1 = r3 + -1
            r4 = r36
            if (r4 != r1) goto L_0x015b
            int r1 = r3 + -1
            r36 = r2
            r22 = r5
            r21 = r6
            r2 = r16
            goto L_0x01b2
        L_0x015b:
            r1 = -1
            if (r4 == r1) goto L_0x01a6
            int r1 = r4 + 2
            if (r1 > r3) goto L_0x0198
            int r1 = r4 + 1
            java.lang.String r1 = r8.substring(r1)     // Catch:{ RuntimeException -> 0x0451 }
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ RuntimeException -> 0x0451 }
            int r16 = r4 + 1
            r36 = r2
            int r2 = r3 - r16
            r23 = r3
            r3 = 9
            if (r2 >= r3) goto L_0x018b
            r3 = r5
            r21 = r6
            r5 = 4621819117588971520(0x4024000000000000, double:10.0)
            r22 = r3
            int r3 = 9 - r2
            r16 = r2
            double r2 = (double) r3     // Catch:{ RuntimeException -> 0x0451 }
            double r2 = java.lang.Math.pow(r5, r2)     // Catch:{ RuntimeException -> 0x0451 }
            int r2 = (int) r2     // Catch:{ RuntimeException -> 0x0451 }
            int r1 = r1 * r2
            goto L_0x0191
        L_0x018b:
            r16 = r2
            r22 = r5
            r21 = r6
        L_0x0191:
            r2 = r4
            r32 = r2
            r2 = r1
            r1 = r32
            goto L_0x01b2
        L_0x0198:
            r36 = r2
            r23 = r3
            r22 = r5
            r21 = r6
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException     // Catch:{ RuntimeException -> 0x0451 }
            r0.<init>()     // Catch:{ RuntimeException -> 0x0451 }
            throw r0     // Catch:{ RuntimeException -> 0x0451 }
        L_0x01a6:
            r36 = r2
            r23 = r3
            r22 = r5
            r21 = r6
            r2 = r16
            r1 = r23
        L_0x01b2:
            r5 = 7
            r6 = 5
            r3 = 2
            switch(r1) {
                case 2: goto L_0x03c9;
                case 3: goto L_0x01b8;
                case 4: goto L_0x03a8;
                case 5: goto L_0x01b8;
                case 6: goto L_0x037f;
                case 7: goto L_0x01b8;
                case 8: goto L_0x0327;
                case 9: goto L_0x01b8;
                case 10: goto L_0x02a6;
                case 11: goto L_0x01b8;
                case 12: goto L_0x0258;
                case 13: goto L_0x01b8;
                case 14: goto L_0x0210;
                case 15: goto L_0x01b8;
                case 16: goto L_0x01b8;
                case 17: goto L_0x01b8;
                case 18: goto L_0x01b8;
                case 19: goto L_0x01c1;
                case 20: goto L_0x01c1;
                case 21: goto L_0x01c1;
                case 22: goto L_0x01c1;
                case 23: goto L_0x01c1;
                case 24: goto L_0x01c1;
                case 25: goto L_0x01c1;
                case 26: goto L_0x01c1;
                default: goto L_0x01b8;
            }
        L_0x01b8:
            r31 = r1
            r24 = r4
            r4 = r15
            java.sql.SQLException r1 = new java.sql.SQLException     // Catch:{ RuntimeException -> 0x044f }
            goto L_0x042a
        L_0x01c1:
            r0 = 0
            r3 = 4
            java.lang.String r0 = r8.substring(r0, r3)     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0451 }
            java.lang.String r3 = r8.substring(r6, r5)     // Catch:{ RuntimeException -> 0x0451 }
            int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ RuntimeException -> 0x0451 }
            r5 = 10
            r6 = 8
            java.lang.String r5 = r8.substring(r6, r5)     // Catch:{ RuntimeException -> 0x0451 }
            int r5 = java.lang.Integer.parseInt(r5)     // Catch:{ RuntimeException -> 0x0451 }
            r6 = 11
            r36 = r0
            r0 = 13
            java.lang.String r0 = r8.substring(r6, r0)     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r7 = r0
            r0 = 16
            r6 = 14
            java.lang.String r0 = r8.substring(r6, r0)     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r13 = r0
            r0 = 17
            r6 = 19
            java.lang.String r0 = r8.substring(r0, r6)     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r14 = r0
            r0 = r36
            r31 = r1
            r1 = r13
            r6 = r14
            goto L_0x03e0
        L_0x0210:
            r0 = 0
            r3 = 4
            java.lang.String r0 = r8.substring(r0, r3)     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r5 = 6
            java.lang.String r3 = r8.substring(r3, r5)     // Catch:{ RuntimeException -> 0x0451 }
            int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ RuntimeException -> 0x0451 }
            r6 = 8
            java.lang.String r5 = r8.substring(r5, r6)     // Catch:{ RuntimeException -> 0x0451 }
            int r5 = java.lang.Integer.parseInt(r5)     // Catch:{ RuntimeException -> 0x0451 }
            r36 = r0
            r0 = 10
            java.lang.String r6 = r8.substring(r6, r0)     // Catch:{ RuntimeException -> 0x0451 }
            int r6 = java.lang.Integer.parseInt(r6)     // Catch:{ RuntimeException -> 0x0451 }
            r7 = r6
            r6 = 12
            java.lang.String r0 = r8.substring(r0, r6)     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r13 = r0
            r0 = 14
            java.lang.String r0 = r8.substring(r6, r0)     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r14 = r0
            r0 = r36
            r31 = r1
            r1 = r13
            r6 = r14
            goto L_0x03e0
        L_0x0258:
            r0 = 0
            java.lang.String r0 = r8.substring(r0, r3)     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r5 = 69
            if (r0 > r5) goto L_0x0267
            int r0 = r0 + 100
        L_0x0267:
            int r0 = r0 + 1900
            r5 = 4
            java.lang.String r3 = r8.substring(r3, r5)     // Catch:{ RuntimeException -> 0x0451 }
            int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ RuntimeException -> 0x0451 }
            r6 = 6
            java.lang.String r5 = r8.substring(r5, r6)     // Catch:{ RuntimeException -> 0x0451 }
            int r5 = java.lang.Integer.parseInt(r5)     // Catch:{ RuntimeException -> 0x0451 }
            r17 = r0
            r0 = 8
            java.lang.String r6 = r8.substring(r6, r0)     // Catch:{ RuntimeException -> 0x0451 }
            int r6 = java.lang.Integer.parseInt(r6)     // Catch:{ RuntimeException -> 0x0451 }
            r7 = r6
            r6 = 10
            java.lang.String r0 = r8.substring(r0, r6)     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r13 = r0
            r0 = 12
            java.lang.String r0 = r8.substring(r6, r0)     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r14 = r0
            r31 = r1
            r1 = r13
            r6 = r14
            r0 = r17
            goto L_0x03e0
        L_0x02a6:
            com.mysql.jdbc.Field[] r0 = r11.fields     // Catch:{ RuntimeException -> 0x0451 }
            int r16 = r15 + -1
            r0 = r0[r16]     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = r0.getMysqlType()     // Catch:{ RuntimeException -> 0x0451 }
            r5 = 10
            if (r0 == r5) goto L_0x02fe
            java.lang.String r0 = "-"
            int r0 = r8.indexOf(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r5 = -1
            if (r0 == r5) goto L_0x02c0
            r31 = r1
            goto L_0x0300
        L_0x02c0:
            r0 = 0
            java.lang.String r0 = r8.substring(r0, r3)     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r5 = 69
            if (r0 > r5) goto L_0x02cf
            int r0 = r0 + 100
        L_0x02cf:
            r5 = 4
            java.lang.String r3 = r8.substring(r3, r5)     // Catch:{ RuntimeException -> 0x0451 }
            int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ RuntimeException -> 0x0451 }
            r6 = 6
            java.lang.String r5 = r8.substring(r5, r6)     // Catch:{ RuntimeException -> 0x0451 }
            int r5 = java.lang.Integer.parseInt(r5)     // Catch:{ RuntimeException -> 0x0451 }
            r31 = r1
            r1 = 8
            java.lang.String r6 = r8.substring(r6, r1)     // Catch:{ RuntimeException -> 0x0451 }
            int r6 = java.lang.Integer.parseInt(r6)     // Catch:{ RuntimeException -> 0x0451 }
            r7 = r6
            r6 = 10
            java.lang.String r1 = r8.substring(r1, r6)     // Catch:{ RuntimeException -> 0x0451 }
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ RuntimeException -> 0x0451 }
            r13 = r1
            int r0 = r0 + 1900
            r6 = r14
            goto L_0x03e0
        L_0x02fe:
            r31 = r1
        L_0x0300:
            r0 = 0
            r1 = 4
            java.lang.String r0 = r8.substring(r0, r1)     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r1 = 7
            java.lang.String r1 = r8.substring(r6, r1)     // Catch:{ RuntimeException -> 0x0451 }
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ RuntimeException -> 0x0451 }
            r3 = r1
            r1 = 10
            r5 = 8
            java.lang.String r1 = r8.substring(r5, r1)     // Catch:{ RuntimeException -> 0x0451 }
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ RuntimeException -> 0x0451 }
            r5 = r1
            r7 = 0
            r13 = 0
            r1 = r13
            r6 = r14
            goto L_0x03e0
        L_0x0327:
            r31 = r1
            java.lang.String r0 = ":"
            int r0 = r8.indexOf(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r1 = -1
            if (r0 == r1) goto L_0x035a
            r0 = 0
            java.lang.String r0 = r8.substring(r0, r3)     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r7 = r0
            r0 = 3
            java.lang.String r0 = r8.substring(r0, r6)     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r13 = r0
            r0 = 8
            r1 = 6
            java.lang.String r0 = r8.substring(r1, r0)     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r14 = r0
            r0 = 1970(0x7b2, float:2.76E-42)
            r3 = 1
            r5 = 1
            r1 = r13
            r6 = r14
            goto L_0x03e0
        L_0x035a:
            r0 = 0
            r1 = 4
            java.lang.String r0 = r8.substring(r0, r1)     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r3 = 6
            java.lang.String r1 = r8.substring(r1, r3)     // Catch:{ RuntimeException -> 0x0451 }
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ RuntimeException -> 0x0451 }
            r5 = 8
            java.lang.String r3 = r8.substring(r3, r5)     // Catch:{ RuntimeException -> 0x0451 }
            int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ RuntimeException -> 0x0451 }
            r5 = r3
            int r0 = r0 + -1900
            r3 = -1
            int r3 = r3 + r1
            r1 = r13
            r6 = r14
            goto L_0x03e0
        L_0x037f:
            r31 = r1
            r0 = 0
            java.lang.String r0 = r8.substring(r0, r3)     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r1 = 69
            if (r0 > r1) goto L_0x0390
            int r0 = r0 + 100
        L_0x0390:
            int r0 = r0 + 1900
            r1 = 4
            java.lang.String r3 = r8.substring(r3, r1)     // Catch:{ RuntimeException -> 0x0451 }
            int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ RuntimeException -> 0x0451 }
            r5 = 6
            java.lang.String r1 = r8.substring(r1, r5)     // Catch:{ RuntimeException -> 0x0451 }
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ RuntimeException -> 0x0451 }
            r5 = r1
            r1 = r13
            r6 = r14
            goto L_0x03e0
        L_0x03a8:
            r31 = r1
            r0 = 0
            java.lang.String r0 = r8.substring(r0, r3)     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r1 = 69
            if (r0 > r1) goto L_0x03b9
            int r0 = r0 + 100
        L_0x03b9:
            int r0 = r0 + 1900
            r1 = 4
            java.lang.String r1 = r8.substring(r3, r1)     // Catch:{ RuntimeException -> 0x0451 }
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ RuntimeException -> 0x0451 }
            r3 = r1
            r5 = 1
            r1 = r13
            r6 = r14
            goto L_0x03e0
        L_0x03c9:
            r31 = r1
            r0 = 0
            java.lang.String r0 = r8.substring(r0, r3)     // Catch:{ RuntimeException -> 0x0451 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ RuntimeException -> 0x0451 }
            r1 = 69
            if (r0 > r1) goto L_0x03da
            int r0 = r0 + 100
        L_0x03da:
            int r0 = r0 + 1900
            r3 = 1
            r5 = 1
            r1 = r13
            r6 = r14
        L_0x03e0:
            boolean r13 = r11.useLegacyDatetimeCode     // Catch:{ RuntimeException -> 0x0451 }
            if (r13 != 0) goto L_0x03f9
            r23 = r37
            r24 = r0
            r25 = r3
            r26 = r5
            r27 = r7
            r28 = r1
            r29 = r6
            r30 = r2
            java.sql.Timestamp r9 = com.mysql.jdbc.TimeUtil.fastTimestampCreate(r23, r24, r25, r26, r27, r28, r29, r30)     // Catch:{ RuntimeException -> 0x0451 }
            return r9
        L_0x03f9:
            com.mysql.jdbc.MySQLConnection r14 = r11.connection     // Catch:{ RuntimeException -> 0x0451 }
            r13 = r33
            r23 = r14
            r14 = r12
            r24 = r4
            r4 = r15
            r15 = r0
            r16 = r3
            r17 = r5
            r18 = r7
            r19 = r1
            r20 = r6
            r21 = r2
            r22 = r10
            java.sql.Timestamp r16 = r13.fastTimestampCreate(r14, r15, r16, r17, r18, r19, r20, r21, r22)     // Catch:{ RuntimeException -> 0x044f }
            com.mysql.jdbc.MySQLConnection r13 = r11.connection     // Catch:{ RuntimeException -> 0x044f }
            java.util.TimeZone r17 = r13.getServerTimezoneTZ()     // Catch:{ RuntimeException -> 0x044f }
            r13 = r23
            r14 = r12
            r15 = r35
            r18 = r37
            r19 = r38
            java.sql.Timestamp r9 = com.mysql.jdbc.TimeUtil.changeTimezone((com.mysql.jdbc.MySQLConnection) r13, (java.util.Calendar) r14, (java.util.Calendar) r15, (java.sql.Timestamp) r16, (java.util.TimeZone) r17, (java.util.TimeZone) r18, (boolean) r19)     // Catch:{ RuntimeException -> 0x044f }
            return r9
        L_0x042a:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x044f }
            r3.<init>()     // Catch:{ RuntimeException -> 0x044f }
            java.lang.String r5 = "Bad format for Timestamp '"
            java.lang.StringBuilder r3 = r3.append(r5)     // Catch:{ RuntimeException -> 0x044f }
            java.lang.StringBuilder r3 = r3.append(r8)     // Catch:{ RuntimeException -> 0x044f }
            java.lang.String r5 = "' in column "
            java.lang.StringBuilder r3 = r3.append(r5)     // Catch:{ RuntimeException -> 0x044f }
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ RuntimeException -> 0x044f }
            java.lang.StringBuilder r0 = r3.append(r0)     // Catch:{ RuntimeException -> 0x044f }
            java.lang.String r0 = r0.toString()     // Catch:{ RuntimeException -> 0x044f }
            r1.<init>(r0, r9)     // Catch:{ RuntimeException -> 0x044f }
            throw r1     // Catch:{ RuntimeException -> 0x044f }
        L_0x044f:
            r0 = move-exception
            goto L_0x0460
        L_0x0451:
            r0 = move-exception
            r4 = r15
            goto L_0x0460
        L_0x0454:
            r0 = move-exception
            r8 = r11
            r9 = r13
            r4 = r14
            r11 = r15
        L_0x0459:
            goto L_0x0460
        L_0x045a:
            r0 = move-exception
            r9 = r13
            r4 = r14
            r11 = r15
            r8 = r36
        L_0x0460:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Cannot convert value '"
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.StringBuilder r1 = r1.append(r8)
            java.lang.String r2 = "' from column "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.StringBuilder r1 = r1.append(r4)
            java.lang.String r2 = " to TIMESTAMP."
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.mysql.jdbc.ExceptionInterceptor r2 = r33.getExceptionInterceptor()
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r9, (com.mysql.jdbc.ExceptionInterceptor) r2)
            r1.initCause(r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ResultSetImpl.getTimestampFromString(int, java.util.Calendar, java.lang.String, java.util.TimeZone, boolean):java.sql.Timestamp");
    }

    private Timestamp getTimestampInternal(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
        Timestamp tsVal;
        int i = columnIndex;
        if (this.isBinaryEncoded) {
            return getNativeTimestamp(columnIndex, targetCalendar, tz, rollForward);
        }
        if (!this.useFastDateParsing) {
            tsVal = getTimestampFromString(columnIndex, targetCalendar, getStringInternal(columnIndex, false), tz, rollForward);
        } else {
            checkClosed();
            checkRowPos();
            checkColumnBounds(columnIndex);
            tsVal = this.thisRow.getTimestampFast(i - 1, targetCalendar, tz, rollForward, this.connection, this, this.connection.getUseGmtMillisForDatetimes(), this.connection.getUseJDBCCompliantTimezoneShift());
        }
        if (tsVal == null) {
            this.wasNullFlag = true;
        } else {
            this.wasNullFlag = false;
        }
        return tsVal;
    }

    public int getType() throws SQLException {
        return this.resultSetType;
    }

    @Deprecated
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        if (this.isBinaryEncoded) {
            return getNativeBinaryStream(columnIndex);
        }
        checkRowPos();
        return getBinaryStream(columnIndex);
    }

    @Deprecated
    public InputStream getUnicodeStream(String columnName) throws SQLException {
        return getUnicodeStream(findColumn(columnName));
    }

    public long getUpdateCount() {
        return this.updateCount;
    }

    public long getUpdateID() {
        return this.updateId;
    }

    public URL getURL(int colIndex) throws SQLException {
        String val = getString(colIndex);
        if (val == null) {
            return null;
        }
        try {
            return new URL(val);
        } catch (MalformedURLException e) {
            throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____104") + val + "'", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
    }

    public URL getURL(String colName) throws SQLException {
        String val = getString(colName);
        if (val == null) {
            return null;
        }
        try {
            return new URL(val);
        } catch (MalformedURLException e) {
            throw SQLError.createSQLException(Messages.getString("ResultSet.Malformed_URL____107") + val + "'", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        SQLWarning sQLWarning;
        synchronized (checkClosed().getConnectionMutex()) {
            sQLWarning = this.warningChain;
        }
        return sQLWarning;
    }

    public void insertRow() throws SQLException {
        throw new NotUpdatable();
    }

    public boolean isAfterLast() throws SQLException {
        boolean b;
        synchronized (checkClosed().getConnectionMutex()) {
            b = this.rowData.isAfterLast();
        }
        return b;
    }

    public boolean isBeforeFirst() throws SQLException {
        boolean isBeforeFirst;
        synchronized (checkClosed().getConnectionMutex()) {
            isBeforeFirst = this.rowData.isBeforeFirst();
        }
        return isBeforeFirst;
    }

    public boolean isFirst() throws SQLException {
        boolean isFirst;
        synchronized (checkClosed().getConnectionMutex()) {
            isFirst = this.rowData.isFirst();
        }
        return isFirst;
    }

    public boolean isLast() throws SQLException {
        boolean isLast;
        synchronized (checkClosed().getConnectionMutex()) {
            isLast = this.rowData.isLast();
        }
        return isLast;
    }

    private void issueConversionViaParsingWarning(String methodName, int columnIndex, Object value, Field fieldInfo, int[] typesWithNoParseConversion) throws SQLException {
        String str;
        boolean z;
        int[] iArr = typesWithNoParseConversion;
        synchronized (checkClosed().getConnectionMutex()) {
            StringBuilder originalQueryBuf = new StringBuilder();
            StatementImpl statementImpl = this.owningStatement;
            if (statementImpl == null || !(statementImpl instanceof PreparedStatement)) {
                originalQueryBuf.append(".");
            } else {
                originalQueryBuf.append(Messages.getString("ResultSet.CostlyConversionCreatedFromQuery"));
                originalQueryBuf.append(((PreparedStatement) this.owningStatement).originalSql);
                originalQueryBuf.append("\n\n");
            }
            StringBuilder convertibleTypesBuf = new StringBuilder();
            for (int typeToName : iArr) {
                convertibleTypesBuf.append(MysqlDefs.typeToName(typeToName));
                convertibleTypesBuf.append("\n");
            }
            Object[] objArr = new Object[8];
            objArr[0] = methodName;
            objArr[1] = Integer.valueOf(columnIndex + 1);
            objArr[2] = fieldInfo.getOriginalName();
            objArr[3] = fieldInfo.getOriginalTableName();
            objArr[4] = originalQueryBuf.toString();
            if (value != null) {
                str = value.getClass().getName();
            } else {
                int sQLType = fieldInfo.getSQLType();
                boolean isUnsigned = fieldInfo.isUnsigned();
                int mysqlType = fieldInfo.getMysqlType();
                if (!fieldInfo.isBinary()) {
                    if (!fieldInfo.isBlob()) {
                        z = false;
                        str = ResultSetMetaData.getClassNameForJavaType(sQLType, isUnsigned, mysqlType, z, fieldInfo.isOpaqueBinary(), this.connection.getYearIsDateType());
                    }
                }
                z = true;
                str = ResultSetMetaData.getClassNameForJavaType(sQLType, isUnsigned, mysqlType, z, fieldInfo.isOpaqueBinary(), this.connection.getYearIsDateType());
            }
            objArr[5] = str;
            objArr[6] = MysqlDefs.typeToName(fieldInfo.getMysqlType());
            objArr[7] = convertibleTypesBuf.toString();
            this.connection.getProfilerEventHandlerInstance().processEvent((byte) 0, this.connection, this.owningStatement, this, 0, new Throwable(), Messages.getString("ResultSet.CostlyConversion", objArr));
        }
    }

    public boolean last() throws SQLException {
        boolean b;
        synchronized (checkClosed().getConnectionMutex()) {
            b = true;
            if (this.rowData.size() == 0) {
                b = false;
            } else {
                if (this.onInsertRow) {
                    this.onInsertRow = false;
                }
                if (this.doingUpdates) {
                    this.doingUpdates = false;
                }
                ResultSetRow resultSetRow = this.thisRow;
                if (resultSetRow != null) {
                    resultSetRow.closeOpenStreams();
                }
                this.rowData.beforeLast();
                this.thisRow = this.rowData.next();
            }
            setRowPositionValidity();
        }
        return b;
    }

    public void moveToCurrentRow() throws SQLException {
        throw new NotUpdatable();
    }

    public void moveToInsertRow() throws SQLException {
        throw new NotUpdatable();
    }

    public boolean next() throws SQLException {
        boolean b;
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.onInsertRow) {
                this.onInsertRow = false;
            }
            if (this.doingUpdates) {
                this.doingUpdates = false;
            }
            if (reallyResult()) {
                ResultSetRow resultSetRow = this.thisRow;
                if (resultSetRow != null) {
                    resultSetRow.closeOpenStreams();
                }
                if (this.rowData.size() == 0) {
                    b = false;
                } else {
                    ResultSetRow next = this.rowData.next();
                    this.thisRow = next;
                    if (next == null) {
                        b = false;
                    } else {
                        clearWarnings();
                        b = true;
                    }
                }
                setRowPositionValidity();
            } else {
                throw SQLError.createSQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
            }
        }
        return b;
    }

    private int parseIntAsDouble(int columnIndex, String val) throws NumberFormatException, SQLException {
        if (val == null) {
            return 0;
        }
        double valueAsDouble = Double.parseDouble(val);
        if (this.jdbcCompliantTruncationForReads && (valueAsDouble < -2.147483648E9d || valueAsDouble > 2.147483647E9d)) {
            throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
        }
        return (int) valueAsDouble;
    }

    private int getIntWithOverflowCheck(int columnIndex) throws SQLException {
        int intValue = this.thisRow.getInt(columnIndex);
        checkForIntegerTruncation(columnIndex, (byte[]) null, intValue);
        return intValue;
    }

    private void checkForIntegerTruncation(int columnIndex, byte[] valueAsBytes, int intValue) throws SQLException {
        if (!this.jdbcCompliantTruncationForReads) {
            return;
        }
        if (intValue == Integer.MIN_VALUE || intValue == Integer.MAX_VALUE) {
            String valueAsString = null;
            if (valueAsBytes == null) {
                valueAsString = this.thisRow.getString(columnIndex, this.fields[columnIndex].getEncoding(), this.connection);
            }
            long valueAsLong = Long.parseLong(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString);
            if (valueAsLong < -2147483648L || valueAsLong > 2147483647L) {
                throwRangeException(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString, columnIndex + 1, 4);
            }
        }
    }

    private long parseLongAsDouble(int columnIndexZeroBased, String val) throws NumberFormatException, SQLException {
        if (val == null) {
            return 0;
        }
        double valueAsDouble = Double.parseDouble(val);
        if (this.jdbcCompliantTruncationForReads && (valueAsDouble < -9.223372036854776E18d || valueAsDouble > 9.223372036854776E18d)) {
            throwRangeException(val, columnIndexZeroBased + 1, -5);
        }
        return (long) valueAsDouble;
    }

    private long getLongWithOverflowCheck(int columnIndexZeroBased, boolean doOverflowCheck) throws SQLException {
        long longValue = this.thisRow.getLong(columnIndexZeroBased);
        if (doOverflowCheck) {
            checkForLongTruncation(columnIndexZeroBased, (byte[]) null, longValue);
        }
        return longValue;
    }

    private long parseLongWithOverflowCheck(int columnIndexZeroBased, byte[] valueAsBytes, String valueAsString, boolean doCheck) throws NumberFormatException, SQLException {
        long longValue;
        if (valueAsBytes == null && valueAsString == null) {
            return 0;
        }
        if (valueAsBytes != null) {
            longValue = StringUtils.getLong(valueAsBytes);
        } else {
            longValue = Long.parseLong(valueAsString.trim());
        }
        if (doCheck && this.jdbcCompliantTruncationForReads) {
            checkForLongTruncation(columnIndexZeroBased, valueAsBytes, longValue);
        }
        return longValue;
    }

    private void checkForLongTruncation(int columnIndexZeroBased, byte[] valueAsBytes, long longValue) throws SQLException {
        if (longValue == Long.MIN_VALUE || longValue == Long.MAX_VALUE) {
            String valueAsString = null;
            if (valueAsBytes == null) {
                valueAsString = this.thisRow.getString(columnIndexZeroBased, this.fields[columnIndexZeroBased].getEncoding(), this.connection);
            }
            double valueAsDouble = Double.parseDouble(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString);
            if (valueAsDouble < -9.223372036854776E18d || valueAsDouble > 9.223372036854776E18d) {
                throwRangeException(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString, columnIndexZeroBased + 1, -5);
            }
        }
    }

    private short parseShortAsDouble(int columnIndex, String val) throws NumberFormatException, SQLException {
        if (val == null) {
            return 0;
        }
        double valueAsDouble = Double.parseDouble(val);
        if (this.jdbcCompliantTruncationForReads && (valueAsDouble < -32768.0d || valueAsDouble > 32767.0d)) {
            throwRangeException(String.valueOf(valueAsDouble), columnIndex, 5);
        }
        return (short) ((int) valueAsDouble);
    }

    private short parseShortWithOverflowCheck(int columnIndex, byte[] valueAsBytes, String valueAsString) throws NumberFormatException, SQLException {
        short shortValue;
        if (valueAsBytes == null && valueAsString == null) {
            return 0;
        }
        if (valueAsBytes != null) {
            shortValue = StringUtils.getShort(valueAsBytes);
        } else {
            valueAsString = valueAsString.trim();
            shortValue = Short.parseShort(valueAsString);
        }
        if (this.jdbcCompliantTruncationForReads && (shortValue == Short.MIN_VALUE || shortValue == Short.MAX_VALUE)) {
            long valueAsLong = Long.parseLong(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString);
            if (valueAsLong < -32768 || valueAsLong > 32767) {
                throwRangeException(valueAsString == null ? StringUtils.toString(valueAsBytes) : valueAsString, columnIndex, 5);
            }
        }
        return shortValue;
    }

    public boolean prev() throws SQLException {
        boolean b;
        synchronized (checkClosed().getConnectionMutex()) {
            int rowIndex = this.rowData.getCurrentRowNumber();
            ResultSetRow resultSetRow = this.thisRow;
            if (resultSetRow != null) {
                resultSetRow.closeOpenStreams();
            }
            if (rowIndex - 1 >= 0) {
                int rowIndex2 = rowIndex - 1;
                this.rowData.setCurrentRow(rowIndex2);
                this.thisRow = this.rowData.getAt(rowIndex2);
                b = true;
            } else if (rowIndex - 1 == -1) {
                this.rowData.setCurrentRow(rowIndex - 1);
                this.thisRow = null;
                b = false;
            } else {
                b = false;
            }
            setRowPositionValidity();
        }
        return b;
    }

    public boolean previous() throws SQLException {
        boolean prev;
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.onInsertRow) {
                this.onInsertRow = false;
            }
            if (this.doingUpdates) {
                this.doingUpdates = false;
            }
            prev = prev();
        }
        return prev;
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:93:0x01a0=Splitter:B:93:0x01a0, B:52:0x0139=Splitter:B:52:0x0139, B:63:0x014f=Splitter:B:63:0x014f} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void realClose(boolean r18) throws java.sql.SQLException {
        /*
            r17 = this;
            r10 = r17
            com.mysql.jdbc.MySQLConnection r11 = r10.connection
            if (r11 != 0) goto L_0x0007
            return
        L_0x0007:
            java.lang.Object r12 = r11.getConnectionMutex()
            monitor-enter(r12)
            boolean r0 = r10.isClosed     // Catch:{ all -> 0x01d9 }
            if (r0 == 0) goto L_0x0012
            monitor-exit(r12)     // Catch:{ all -> 0x01d9 }
            return
        L_0x0012:
            r13 = 0
            r14 = 1
            r15 = 0
            boolean r0 = r10.useUsageAdvisor     // Catch:{ all -> 0x0188 }
            if (r0 == 0) goto L_0x0138
            if (r18 != 0) goto L_0x0038
            com.mysql.jdbc.MySQLConnection r0 = r10.connection     // Catch:{ all -> 0x0188 }
            com.mysql.jdbc.profiler.ProfilerEventHandler r1 = r0.getProfilerEventHandlerInstance()     // Catch:{ all -> 0x0188 }
            r2 = 0
            com.mysql.jdbc.MySQLConnection r3 = r10.connection     // Catch:{ all -> 0x0188 }
            com.mysql.jdbc.StatementImpl r4 = r10.owningStatement     // Catch:{ all -> 0x0188 }
            r6 = 0
            java.lang.Throwable r8 = new java.lang.Throwable     // Catch:{ all -> 0x0188 }
            r8.<init>()     // Catch:{ all -> 0x0188 }
            java.lang.String r0 = "ResultSet.ResultSet_implicitly_closed_by_driver"
            java.lang.String r9 = com.mysql.jdbc.Messages.getString(r0)     // Catch:{ all -> 0x0188 }
            r5 = r17
            r1.processEvent(r2, r3, r4, r5, r6, r8, r9)     // Catch:{ all -> 0x0188 }
        L_0x0038:
            com.mysql.jdbc.RowData r0 = r10.rowData     // Catch:{ all -> 0x0188 }
            boolean r1 = r0 instanceof com.mysql.jdbc.RowDataStatic     // Catch:{ all -> 0x0188 }
            if (r1 == 0) goto L_0x00d6
            r9 = 2
            if (r0 == 0) goto L_0x0086
            int r0 = r0.size()     // Catch:{ all -> 0x0188 }
            com.mysql.jdbc.MySQLConnection r1 = r10.connection     // Catch:{ all -> 0x0188 }
            int r1 = r1.getResultSetSizeThreshold()     // Catch:{ all -> 0x0188 }
            if (r0 <= r1) goto L_0x0086
            com.mysql.jdbc.MySQLConnection r0 = r10.connection     // Catch:{ all -> 0x0188 }
            com.mysql.jdbc.profiler.ProfilerEventHandler r1 = r0.getProfilerEventHandlerInstance()     // Catch:{ all -> 0x0188 }
            r2 = 0
            com.mysql.jdbc.MySQLConnection r3 = r10.connection     // Catch:{ all -> 0x0188 }
            com.mysql.jdbc.StatementImpl r4 = r10.owningStatement     // Catch:{ all -> 0x0188 }
            r6 = 0
            java.lang.Throwable r8 = new java.lang.Throwable     // Catch:{ all -> 0x0188 }
            r8.<init>()     // Catch:{ all -> 0x0188 }
            java.lang.String r0 = "ResultSet.Too_Large_Result_Set"
            java.lang.Object[] r5 = new java.lang.Object[r9]     // Catch:{ all -> 0x0188 }
            com.mysql.jdbc.RowData r9 = r10.rowData     // Catch:{ all -> 0x0188 }
            int r9 = r9.size()     // Catch:{ all -> 0x0188 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x0188 }
            r5[r13] = r9     // Catch:{ all -> 0x0188 }
            com.mysql.jdbc.MySQLConnection r9 = r10.connection     // Catch:{ all -> 0x0188 }
            int r9 = r9.getResultSetSizeThreshold()     // Catch:{ all -> 0x0188 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x0188 }
            r5[r14] = r9     // Catch:{ all -> 0x0188 }
            java.lang.String r9 = com.mysql.jdbc.Messages.getString(r0, r5)     // Catch:{ all -> 0x0188 }
            r5 = r17
            r0 = 2
            r1.processEvent(r2, r3, r4, r5, r6, r8, r9)     // Catch:{ all -> 0x0188 }
            goto L_0x0087
        L_0x0086:
            r0 = r9
        L_0x0087:
            com.mysql.jdbc.RowData r1 = r10.rowData     // Catch:{ all -> 0x0188 }
            if (r1 == 0) goto L_0x00d6
            int r1 = r1.size()     // Catch:{ all -> 0x0188 }
            if (r1 == 0) goto L_0x00d6
            com.mysql.jdbc.RowData r1 = r10.rowData     // Catch:{ all -> 0x0188 }
            boolean r1 = r1.isLast()     // Catch:{ all -> 0x0188 }
            if (r1 != 0) goto L_0x00d6
            com.mysql.jdbc.RowData r1 = r10.rowData     // Catch:{ all -> 0x0188 }
            boolean r1 = r1.isAfterLast()     // Catch:{ all -> 0x0188 }
            if (r1 != 0) goto L_0x00d6
            com.mysql.jdbc.MySQLConnection r1 = r10.connection     // Catch:{ all -> 0x0188 }
            com.mysql.jdbc.profiler.ProfilerEventHandler r1 = r1.getProfilerEventHandlerInstance()     // Catch:{ all -> 0x0188 }
            r2 = 0
            com.mysql.jdbc.MySQLConnection r3 = r10.connection     // Catch:{ all -> 0x0188 }
            com.mysql.jdbc.StatementImpl r4 = r10.owningStatement     // Catch:{ all -> 0x0188 }
            r6 = 0
            java.lang.Throwable r8 = new java.lang.Throwable     // Catch:{ all -> 0x0188 }
            r8.<init>()     // Catch:{ all -> 0x0188 }
            java.lang.String r5 = "ResultSet.Possible_incomplete_traversal_of_result_set"
            java.lang.Object[] r0 = new java.lang.Object[r0]     // Catch:{ all -> 0x0188 }
            int r9 = r17.getRow()     // Catch:{ all -> 0x0188 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x0188 }
            r0[r13] = r9     // Catch:{ all -> 0x0188 }
            com.mysql.jdbc.RowData r9 = r10.rowData     // Catch:{ all -> 0x0188 }
            int r9 = r9.size()     // Catch:{ all -> 0x0188 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x0188 }
            r0[r14] = r9     // Catch:{ all -> 0x0188 }
            java.lang.String r9 = com.mysql.jdbc.Messages.getString(r5, r0)     // Catch:{ all -> 0x0188 }
            r5 = r17
            r1.processEvent(r2, r3, r4, r5, r6, r8, r9)     // Catch:{ all -> 0x0188 }
        L_0x00d6:
            com.mysql.jdbc.RowData r0 = r10.rowData     // Catch:{ all -> 0x0188 }
            if (r0 == 0) goto L_0x0138
            boolean[] r1 = r10.columnUsed     // Catch:{ all -> 0x0188 }
            int r1 = r1.length     // Catch:{ all -> 0x0188 }
            if (r1 <= 0) goto L_0x0138
            boolean r0 = r0.wasEmpty()     // Catch:{ all -> 0x0188 }
            if (r0 != 0) goto L_0x0138
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0188 }
            r0.<init>()     // Catch:{ all -> 0x0188 }
            r1 = 0
        L_0x00eb:
            boolean[] r2 = r10.columnUsed     // Catch:{ all -> 0x0188 }
            int r3 = r2.length     // Catch:{ all -> 0x0188 }
            if (r1 >= r3) goto L_0x010d
            boolean r2 = r2[r1]     // Catch:{ all -> 0x0188 }
            if (r2 != 0) goto L_0x010a
            int r2 = r0.length()     // Catch:{ all -> 0x0188 }
            if (r2 <= 0) goto L_0x00ff
            java.lang.String r2 = ", "
            r0.append(r2)     // Catch:{ all -> 0x0188 }
        L_0x00ff:
            com.mysql.jdbc.Field[] r2 = r10.fields     // Catch:{ all -> 0x0188 }
            r2 = r2[r1]     // Catch:{ all -> 0x0188 }
            java.lang.String r2 = r2.getFullName()     // Catch:{ all -> 0x0188 }
            r0.append(r2)     // Catch:{ all -> 0x0188 }
        L_0x010a:
            int r1 = r1 + 1
            goto L_0x00eb
        L_0x010d:
            int r1 = r0.length()     // Catch:{ all -> 0x0188 }
            if (r1 <= 0) goto L_0x0138
            com.mysql.jdbc.MySQLConnection r1 = r10.connection     // Catch:{ all -> 0x0188 }
            com.mysql.jdbc.profiler.ProfilerEventHandler r1 = r1.getProfilerEventHandlerInstance()     // Catch:{ all -> 0x0188 }
            r2 = 0
            com.mysql.jdbc.MySQLConnection r3 = r10.connection     // Catch:{ all -> 0x0188 }
            com.mysql.jdbc.StatementImpl r4 = r10.owningStatement     // Catch:{ all -> 0x0188 }
            r6 = 0
            java.lang.Throwable r8 = new java.lang.Throwable     // Catch:{ all -> 0x0188 }
            r8.<init>()     // Catch:{ all -> 0x0188 }
            java.lang.String r5 = "ResultSet.The_following_columns_were_never_referenced"
            java.lang.String[] r9 = new java.lang.String[r14]     // Catch:{ all -> 0x0188 }
            java.lang.String r16 = r0.toString()     // Catch:{ all -> 0x0188 }
            r9[r13] = r16     // Catch:{ all -> 0x0188 }
            java.lang.String r9 = com.mysql.jdbc.Messages.getString(r5, r9)     // Catch:{ all -> 0x0188 }
            r5 = r17
            r1.processEvent(r2, r3, r4, r5, r6, r8, r9)     // Catch:{ all -> 0x0188 }
        L_0x0138:
            com.mysql.jdbc.StatementImpl r0 = r10.owningStatement     // Catch:{ all -> 0x01d9 }
            if (r0 == 0) goto L_0x0142
            if (r18 == 0) goto L_0x0142
            r0.removeOpenResultSet(r10)     // Catch:{ all -> 0x01d9 }
        L_0x0142:
            r1 = 0
            com.mysql.jdbc.RowData r0 = r10.rowData     // Catch:{ all -> 0x01d9 }
            if (r0 == 0) goto L_0x014f
            r0.close()     // Catch:{ SQLException -> 0x014b }
            goto L_0x014f
        L_0x014b:
            r0 = move-exception
            r2 = r0
            r0 = r2
            r1 = r0
        L_0x014f:
            com.mysql.jdbc.PreparedStatement r0 = r10.statementUsedForFetchingRows     // Catch:{ all -> 0x01d9 }
            if (r0 == 0) goto L_0x0161
            r0.realClose(r14, r13)     // Catch:{ SQLException -> 0x0157 }
            goto L_0x0161
        L_0x0157:
            r0 = move-exception
            r2 = r0
            r0 = r2
            if (r1 == 0) goto L_0x0160
            r1.setNextException(r0)     // Catch:{ all -> 0x01d9 }
            goto L_0x0161
        L_0x0160:
            r1 = r0
        L_0x0161:
            r10.rowData = r15     // Catch:{ all -> 0x01d9 }
            r10.fields = r15     // Catch:{ all -> 0x01d9 }
            r10.columnLabelToIndex = r15     // Catch:{ all -> 0x01d9 }
            r10.fullColumnNameToIndex = r15     // Catch:{ all -> 0x01d9 }
            r10.columnToIndexCache = r15     // Catch:{ all -> 0x01d9 }
            r10.warningChain = r15     // Catch:{ all -> 0x01d9 }
            boolean r0 = r10.retainOwningStatement     // Catch:{ all -> 0x01d9 }
            if (r0 != 0) goto L_0x0173
            r10.owningStatement = r15     // Catch:{ all -> 0x01d9 }
        L_0x0173:
            r10.catalog = r15     // Catch:{ all -> 0x01d9 }
            r10.serverInfo = r15     // Catch:{ all -> 0x01d9 }
            r10.thisRow = r15     // Catch:{ all -> 0x01d9 }
            r10.fastDefaultCal = r15     // Catch:{ all -> 0x01d9 }
            r10.fastClientCal = r15     // Catch:{ all -> 0x01d9 }
            r10.connection = r15     // Catch:{ all -> 0x01d9 }
            r10.isClosed = r14     // Catch:{ all -> 0x01d9 }
            if (r1 != 0) goto L_0x0186
            monitor-exit(r12)     // Catch:{ all -> 0x01d9 }
            return
        L_0x0186:
            throw r1     // Catch:{ all -> 0x01d9 }
        L_0x0188:
            r0 = move-exception
            r1 = r0
            com.mysql.jdbc.StatementImpl r0 = r10.owningStatement     // Catch:{ all -> 0x01d9 }
            if (r0 == 0) goto L_0x0193
            if (r18 == 0) goto L_0x0193
            r0.removeOpenResultSet(r10)     // Catch:{ all -> 0x01d9 }
        L_0x0193:
            r2 = 0
            com.mysql.jdbc.RowData r0 = r10.rowData     // Catch:{ all -> 0x01d9 }
            if (r0 == 0) goto L_0x01a0
            r0.close()     // Catch:{ SQLException -> 0x019c }
            goto L_0x01a0
        L_0x019c:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r2 = r0
        L_0x01a0:
            com.mysql.jdbc.PreparedStatement r0 = r10.statementUsedForFetchingRows     // Catch:{ all -> 0x01d9 }
            if (r0 == 0) goto L_0x01b2
            r0.realClose(r14, r13)     // Catch:{ SQLException -> 0x01a8 }
            goto L_0x01b2
        L_0x01a8:
            r0 = move-exception
            r3 = r0
            r0 = r3
            if (r2 == 0) goto L_0x01b1
            r2.setNextException(r0)     // Catch:{ all -> 0x01d9 }
            goto L_0x01b2
        L_0x01b1:
            r2 = r0
        L_0x01b2:
            r10.rowData = r15     // Catch:{ all -> 0x01d9 }
            r10.fields = r15     // Catch:{ all -> 0x01d9 }
            r10.columnLabelToIndex = r15     // Catch:{ all -> 0x01d9 }
            r10.fullColumnNameToIndex = r15     // Catch:{ all -> 0x01d9 }
            r10.columnToIndexCache = r15     // Catch:{ all -> 0x01d9 }
            r10.warningChain = r15     // Catch:{ all -> 0x01d9 }
            boolean r0 = r10.retainOwningStatement     // Catch:{ all -> 0x01d9 }
            if (r0 != 0) goto L_0x01c4
            r10.owningStatement = r15     // Catch:{ all -> 0x01d9 }
        L_0x01c4:
            r10.catalog = r15     // Catch:{ all -> 0x01d9 }
            r10.serverInfo = r15     // Catch:{ all -> 0x01d9 }
            r10.thisRow = r15     // Catch:{ all -> 0x01d9 }
            r10.fastDefaultCal = r15     // Catch:{ all -> 0x01d9 }
            r10.fastClientCal = r15     // Catch:{ all -> 0x01d9 }
            r10.connection = r15     // Catch:{ all -> 0x01d9 }
            r10.isClosed = r14     // Catch:{ all -> 0x01d9 }
            if (r2 == 0) goto L_0x01d6
            throw r2     // Catch:{ all -> 0x01d9 }
        L_0x01d6:
            throw r1     // Catch:{ all -> 0x01d9 }
        L_0x01d9:
            r0 = move-exception
            monitor-exit(r12)     // Catch:{ all -> 0x01d9 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ResultSetImpl.realClose(boolean):void");
    }

    public boolean isClosed() throws SQLException {
        return this.isClosed;
    }

    public boolean reallyResult() {
        if (this.rowData != null) {
            return true;
        }
        return this.reallyResult;
    }

    public void refreshRow() throws SQLException {
        throw new NotUpdatable();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0044, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean relative(int r5) throws java.sql.SQLException {
        /*
            r4 = this;
            com.mysql.jdbc.MySQLConnection r0 = r4.checkClosed()
            java.lang.Object r0 = r0.getConnectionMutex()
            monitor-enter(r0)
            com.mysql.jdbc.RowData r1 = r4.rowData     // Catch:{ all -> 0x0045 }
            int r1 = r1.size()     // Catch:{ all -> 0x0045 }
            r2 = 0
            if (r1 != 0) goto L_0x0017
            r4.setRowPositionValidity()     // Catch:{ all -> 0x0045 }
            monitor-exit(r0)     // Catch:{ all -> 0x0045 }
            return r2
        L_0x0017:
            com.mysql.jdbc.ResultSetRow r1 = r4.thisRow     // Catch:{ all -> 0x0045 }
            if (r1 == 0) goto L_0x001e
            r1.closeOpenStreams()     // Catch:{ all -> 0x0045 }
        L_0x001e:
            com.mysql.jdbc.RowData r1 = r4.rowData     // Catch:{ all -> 0x0045 }
            r1.moveRowRelative(r5)     // Catch:{ all -> 0x0045 }
            com.mysql.jdbc.RowData r1 = r4.rowData     // Catch:{ all -> 0x0045 }
            int r3 = r1.getCurrentRowNumber()     // Catch:{ all -> 0x0045 }
            com.mysql.jdbc.ResultSetRow r1 = r1.getAt(r3)     // Catch:{ all -> 0x0045 }
            r4.thisRow = r1     // Catch:{ all -> 0x0045 }
            r4.setRowPositionValidity()     // Catch:{ all -> 0x0045 }
            com.mysql.jdbc.RowData r1 = r4.rowData     // Catch:{ all -> 0x0045 }
            boolean r1 = r1.isAfterLast()     // Catch:{ all -> 0x0045 }
            if (r1 != 0) goto L_0x0043
            com.mysql.jdbc.RowData r1 = r4.rowData     // Catch:{ all -> 0x0045 }
            boolean r1 = r1.isBeforeFirst()     // Catch:{ all -> 0x0045 }
            if (r1 != 0) goto L_0x0043
            r2 = 1
        L_0x0043:
            monitor-exit(r0)     // Catch:{ all -> 0x0045 }
            return r2
        L_0x0045:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0045 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ResultSetImpl.relative(int):boolean");
    }

    public boolean rowDeleted() throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    public boolean rowInserted() throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    public boolean rowUpdated() throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    /* access modifiers changed from: protected */
    public void setBinaryEncoded() {
        this.isBinaryEncoded = true;
    }

    public void setFetchDirection(int direction) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (direction == 1000 || direction == 1001 || direction == 1002) {
                this.fetchDirection = direction;
            } else {
                throw SQLError.createSQLException(Messages.getString("ResultSet.Illegal_value_for_fetch_direction_64"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
    }

    public void setFetchSize(int rows) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (rows >= 0) {
                try {
                    this.fetchSize = rows;
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                throw SQLError.createSQLException(Messages.getString("ResultSet.Value_must_be_between_0_and_getMaxRows()_66"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
    }

    public void setFirstCharOfQuery(char c) {
        try {
            synchronized (checkClosed().getConnectionMutex()) {
                this.firstCharOfQuery = c;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /* access modifiers changed from: protected */
    public synchronized void setNextResultSet(ResultSetInternalMethods nextResultSet2) {
        this.nextResultSet = nextResultSet2;
    }

    public void setOwningStatement(StatementImpl owningStatement2) {
        try {
            synchronized (checkClosed().getConnectionMutex()) {
                this.owningStatement = owningStatement2;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0013, code lost:
        r1 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void setResultSetConcurrency(int r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            com.mysql.jdbc.MySQLConnection r0 = r2.checkClosed()     // Catch:{ SQLException -> 0x0017 }
            java.lang.Object r0 = r0.getConnectionMutex()     // Catch:{ SQLException -> 0x0017 }
            monitor-enter(r0)     // Catch:{ SQLException -> 0x0017 }
            r2.resultSetConcurrency = r3     // Catch:{ all -> 0x0010 }
            monitor-exit(r0)     // Catch:{ all -> 0x0010 }
            monitor-exit(r2)
            return
        L_0x0010:
            r1 = move-exception
        L_0x0011:
            monitor-exit(r0)     // Catch:{ all -> 0x0013 }
            throw r1     // Catch:{ SQLException -> 0x0017 }
        L_0x0013:
            r1 = move-exception
            goto L_0x0011
        L_0x0015:
            r3 = move-exception
            goto L_0x001e
        L_0x0017:
            r0 = move-exception
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ all -> 0x0015 }
            r1.<init>(r0)     // Catch:{ all -> 0x0015 }
            throw r1     // Catch:{ all -> 0x0015 }
        L_0x001e:
            monitor-exit(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ResultSetImpl.setResultSetConcurrency(int):void");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0013, code lost:
        r1 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void setResultSetType(int r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            com.mysql.jdbc.MySQLConnection r0 = r2.checkClosed()     // Catch:{ SQLException -> 0x0017 }
            java.lang.Object r0 = r0.getConnectionMutex()     // Catch:{ SQLException -> 0x0017 }
            monitor-enter(r0)     // Catch:{ SQLException -> 0x0017 }
            r2.resultSetType = r3     // Catch:{ all -> 0x0010 }
            monitor-exit(r0)     // Catch:{ all -> 0x0010 }
            monitor-exit(r2)
            return
        L_0x0010:
            r1 = move-exception
        L_0x0011:
            monitor-exit(r0)     // Catch:{ all -> 0x0013 }
            throw r1     // Catch:{ SQLException -> 0x0017 }
        L_0x0013:
            r1 = move-exception
            goto L_0x0011
        L_0x0015:
            r3 = move-exception
            goto L_0x001e
        L_0x0017:
            r0 = move-exception
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ all -> 0x0015 }
            r1.<init>(r0)     // Catch:{ all -> 0x0015 }
            throw r1     // Catch:{ all -> 0x0015 }
        L_0x001e:
            monitor-exit(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ResultSetImpl.setResultSetType(int):void");
    }

    /* access modifiers changed from: protected */
    public void setServerInfo(String info) {
        try {
            synchronized (checkClosed().getConnectionMutex()) {
                this.serverInfo = info;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0013, code lost:
        r1 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void setStatementUsedForFetchingRows(com.mysql.jdbc.PreparedStatement r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            com.mysql.jdbc.MySQLConnection r0 = r2.checkClosed()     // Catch:{ SQLException -> 0x0017 }
            java.lang.Object r0 = r0.getConnectionMutex()     // Catch:{ SQLException -> 0x0017 }
            monitor-enter(r0)     // Catch:{ SQLException -> 0x0017 }
            r2.statementUsedForFetchingRows = r3     // Catch:{ all -> 0x0010 }
            monitor-exit(r0)     // Catch:{ all -> 0x0010 }
            monitor-exit(r2)
            return
        L_0x0010:
            r1 = move-exception
        L_0x0011:
            monitor-exit(r0)     // Catch:{ all -> 0x0013 }
            throw r1     // Catch:{ SQLException -> 0x0017 }
        L_0x0013:
            r1 = move-exception
            goto L_0x0011
        L_0x0015:
            r3 = move-exception
            goto L_0x001e
        L_0x0017:
            r0 = move-exception
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ all -> 0x0015 }
            r1.<init>(r0)     // Catch:{ all -> 0x0015 }
            throw r1     // Catch:{ all -> 0x0015 }
        L_0x001e:
            monitor-exit(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ResultSetImpl.setStatementUsedForFetchingRows(com.mysql.jdbc.PreparedStatement):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0013, code lost:
        r1 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void setWrapperStatement(java.sql.Statement r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            com.mysql.jdbc.MySQLConnection r0 = r2.checkClosed()     // Catch:{ SQLException -> 0x0017 }
            java.lang.Object r0 = r0.getConnectionMutex()     // Catch:{ SQLException -> 0x0017 }
            monitor-enter(r0)     // Catch:{ SQLException -> 0x0017 }
            r2.wrapperStatement = r3     // Catch:{ all -> 0x0010 }
            monitor-exit(r0)     // Catch:{ all -> 0x0010 }
            monitor-exit(r2)
            return
        L_0x0010:
            r1 = move-exception
        L_0x0011:
            monitor-exit(r0)     // Catch:{ all -> 0x0013 }
            throw r1     // Catch:{ SQLException -> 0x0017 }
        L_0x0013:
            r1 = move-exception
            goto L_0x0011
        L_0x0015:
            r3 = move-exception
            goto L_0x001e
        L_0x0017:
            r0 = move-exception
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ all -> 0x0015 }
            r1.<init>(r0)     // Catch:{ all -> 0x0015 }
            throw r1     // Catch:{ all -> 0x0015 }
        L_0x001e:
            monitor-exit(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ResultSetImpl.setWrapperStatement(java.sql.Statement):void");
    }

    private void throwRangeException(String valueAsString, int columnIndex, int jdbcType) throws SQLException {
        String datatype;
        switch (jdbcType) {
            case -6:
                datatype = "TINYINT";
                break;
            case -5:
                datatype = "BIGINT";
                break;
            case 3:
                datatype = "DECIMAL";
                break;
            case 4:
                datatype = "INTEGER";
                break;
            case 5:
                datatype = "SMALLINT";
                break;
            case 6:
                datatype = "FLOAT";
                break;
            case 7:
                datatype = "REAL";
                break;
            case 8:
                datatype = "DOUBLE";
                break;
            default:
                datatype = " (JDBC type '" + jdbcType + "')";
                break;
        }
        throw SQLError.createSQLException("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + ".", SQLError.SQL_STATE_NUMERIC_VALUE_OUT_OF_RANGE, getExceptionInterceptor());
    }

    public String toString() {
        if (this.reallyResult) {
            return super.toString();
        }
        return "Result set representing update count of " + this.updateCount;
    }

    public void updateArray(int arg0, Array arg1) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    public void updateArray(String arg0, Array arg1) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
        updateAsciiStream(findColumn(columnName), x, length);
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
        updateBigDecimal(findColumn(columnName), x);
    }

    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
        updateBinaryStream(findColumn(columnName), x, length);
    }

    public void updateBlob(int arg0, Blob arg1) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateBlob(String arg0, Blob arg1) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateBoolean(String columnName, boolean x) throws SQLException {
        updateBoolean(findColumn(columnName), x);
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateByte(String columnName, byte x) throws SQLException {
        updateByte(findColumn(columnName), x);
    }

    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateBytes(String columnName, byte[] x) throws SQLException {
        updateBytes(findColumn(columnName), x);
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
        updateCharacterStream(findColumn(columnName), reader, length);
    }

    public void updateClob(int arg0, Clob arg1) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    public void updateClob(String columnName, Clob clob) throws SQLException {
        updateClob(findColumn(columnName), clob);
    }

    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateDate(String columnName, Date x) throws SQLException {
        updateDate(findColumn(columnName), x);
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateDouble(String columnName, double x) throws SQLException {
        updateDouble(findColumn(columnName), x);
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateFloat(String columnName, float x) throws SQLException {
        updateFloat(findColumn(columnName), x);
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateInt(String columnName, int x) throws SQLException {
        updateInt(findColumn(columnName), x);
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateLong(String columnName, long x) throws SQLException {
        updateLong(findColumn(columnName), x);
    }

    public void updateNull(int columnIndex) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateNull(String columnName) throws SQLException {
        updateNull(findColumn(columnName));
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateObject(String columnName, Object x) throws SQLException {
        updateObject(findColumn(columnName), x);
    }

    public void updateObject(String columnName, Object x, int scale) throws SQLException {
        updateObject(findColumn(columnName), x);
    }

    public void updateRef(int arg0, Ref arg1) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    public void updateRef(String arg0, Ref arg1) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    public void updateRow() throws SQLException {
        throw new NotUpdatable();
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateShort(String columnName, short x) throws SQLException {
        updateShort(findColumn(columnName), x);
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateString(String columnName, String x) throws SQLException {
        updateString(findColumn(columnName), x);
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateTime(String columnName, Time x) throws SQLException {
        updateTime(findColumn(columnName), x);
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new NotUpdatable();
    }

    public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
        updateTimestamp(findColumn(columnName), x);
    }

    public boolean wasNull() throws SQLException {
        return this.wasNullFlag;
    }

    /* access modifiers changed from: protected */
    public Calendar getGmtCalendar() {
        if (this.gmtCalendar == null) {
            this.gmtCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        }
        return this.gmtCalendar;
    }

    /* access modifiers changed from: protected */
    public ExceptionInterceptor getExceptionInterceptor() {
        return this.exceptionInterceptor;
    }

    public int getId() {
        return this.resultId;
    }
}
