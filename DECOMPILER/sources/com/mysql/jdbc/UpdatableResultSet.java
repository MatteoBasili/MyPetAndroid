package com.mysql.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UpdatableResultSet extends ResultSetImpl {
    static final byte[] STREAM_DATA_MARKER = StringUtils.getBytes("** STREAM DATA **");
    protected SingleByteCharsetConverter charConverter;
    private String charEncoding;
    private Map<String, Map<String, Map<String, Integer>>> databasesUsedToTablesUsed = null;
    private byte[][] defaultColumnValue;
    private String deleteSQL = null;
    private PreparedStatement deleter = null;
    private boolean initializedCharConverter = false;
    private String insertSQL = null;
    protected PreparedStatement inserter = null;
    private boolean isUpdatable = false;
    private String notUpdatableReason = null;
    private boolean populateInserterWithDefaultValues = false;
    private List<Integer> primaryKeyIndicies = null;
    private String qualifiedAndQuotedTableName;
    private String quotedIdChar = null;
    private String refreshSQL = null;
    private PreparedStatement refresher;
    private ResultSetRow savedCurrentRow;
    private String updateSQL = null;
    protected PreparedStatement updater = null;

    protected UpdatableResultSet(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt) throws SQLException {
        super(catalog, fields, tuples, conn, creatorStmt);
        checkUpdatability();
        this.populateInserterWithDefaultValues = this.connection.getPopulateInsertRowWithDefaultValues();
    }

    public boolean absolute(int row) throws SQLException {
        return super.absolute(row);
    }

    public void afterLast() throws SQLException {
        super.afterLast();
    }

    public void beforeFirst() throws SQLException {
        super.beforeFirst();
    }

    public void cancelRowUpdates() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.doingUpdates) {
                this.doingUpdates = false;
                this.updater.clearParameters();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void checkRowPos() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                super.checkRowPos();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void checkUpdatability() throws SQLException {
        ResultSet rs;
        String originalName;
        try {
            if (this.fields != null) {
                int primaryKeyCount = 0;
                if (this.catalog == null || this.catalog.length() == 0) {
                    this.catalog = this.fields[0].getDatabaseName();
                    if (this.catalog == null || this.catalog.length() == 0) {
                        throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.43"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                    }
                }
                if (this.fields.length > 0) {
                    String singleTableName = this.fields[0].getOriginalTableName();
                    String catalogName = this.fields[0].getDatabaseName();
                    if (singleTableName == null) {
                        singleTableName = this.fields[0].getTableName();
                        catalogName = this.catalog;
                    }
                    if (singleTableName == null || singleTableName.length() != 0) {
                        if (this.fields[0].isPrimaryKey()) {
                            primaryKeyCount = 0 + 1;
                        }
                        int i = 1;
                        while (i < this.fields.length) {
                            String otherTableName = this.fields[i].getOriginalTableName();
                            String otherCatalogName = this.fields[i].getDatabaseName();
                            if (otherTableName == null) {
                                otherTableName = this.fields[i].getTableName();
                                otherCatalogName = this.catalog;
                            }
                            if (otherTableName == null || otherTableName.length() != 0) {
                                if (singleTableName != null) {
                                    if (otherTableName.equals(singleTableName)) {
                                        if (catalogName != null) {
                                            if (otherCatalogName.equals(catalogName)) {
                                                if (this.fields[i].isPrimaryKey()) {
                                                    primaryKeyCount++;
                                                }
                                                i++;
                                            }
                                        }
                                        this.isUpdatable = false;
                                        this.notUpdatableReason = Messages.getString("NotUpdatableReason.1");
                                        return;
                                    }
                                }
                                this.isUpdatable = false;
                                this.notUpdatableReason = Messages.getString("NotUpdatableReason.0");
                                return;
                            }
                            this.isUpdatable = false;
                            this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
                            return;
                        }
                        if (singleTableName != null) {
                            if (singleTableName.length() != 0) {
                                if (this.connection.getStrictUpdates()) {
                                    DatabaseMetaData dbmd = this.connection.getMetaData();
                                    rs = null;
                                    HashMap<String, String> primaryKeyNames = new HashMap<>();
                                    ResultSet rs2 = dbmd.getPrimaryKeys(catalogName, (String) null, singleTableName);
                                    while (rs2.next()) {
                                        String keyName = rs2.getString(4).toUpperCase();
                                        primaryKeyNames.put(keyName, keyName);
                                    }
                                    if (rs2 != null) {
                                        try {
                                            rs2.close();
                                        } catch (Exception ex) {
                                            AssertionFailedException.shouldNotHappen(ex);
                                        }
                                    }
                                    int existingPrimaryKeysCount = primaryKeyNames.size();
                                    if (existingPrimaryKeysCount == 0) {
                                        this.isUpdatable = false;
                                        this.notUpdatableReason = Messages.getString("NotUpdatableReason.5");
                                        return;
                                    }
                                    int i2 = 0;
                                    while (i2 < this.fields.length) {
                                        if (!this.fields[i2].isPrimaryKey() || primaryKeyNames.remove(this.fields[i2].getName().toUpperCase()) != null || (originalName = this.fields[i2].getOriginalName()) == null || primaryKeyNames.remove(originalName.toUpperCase()) != null) {
                                            i2++;
                                        } else {
                                            this.isUpdatable = false;
                                            this.notUpdatableReason = Messages.getString("NotUpdatableReason.6", new Object[]{originalName});
                                            return;
                                        }
                                    }
                                    boolean isEmpty = primaryKeyNames.isEmpty();
                                    this.isUpdatable = isEmpty;
                                    if (!isEmpty) {
                                        if (existingPrimaryKeysCount > 1) {
                                            this.notUpdatableReason = Messages.getString("NotUpdatableReason.7");
                                            return;
                                        } else {
                                            this.notUpdatableReason = Messages.getString("NotUpdatableReason.4");
                                            return;
                                        }
                                    }
                                }
                                if (primaryKeyCount == 0) {
                                    this.isUpdatable = false;
                                    this.notUpdatableReason = Messages.getString("NotUpdatableReason.4");
                                    return;
                                }
                                this.isUpdatable = true;
                                this.notUpdatableReason = null;
                                return;
                            }
                        }
                        this.isUpdatable = false;
                        this.notUpdatableReason = Messages.getString("NotUpdatableReason.2");
                        return;
                    }
                    this.isUpdatable = false;
                    this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
                    return;
                }
                this.isUpdatable = false;
                this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
            }
        } catch (SQLException sqlEx) {
            this.isUpdatable = false;
            this.notUpdatableReason = sqlEx.getMessage();
        } catch (Throwable th) {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex2) {
                    AssertionFailedException.shouldNotHappen(ex2);
                }
            }
            throw th;
        }
    }

    public void deleteRow() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.isUpdatable) {
                throw new NotUpdatable(this.notUpdatableReason);
            } else if (this.onInsertRow) {
                throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.1"), getExceptionInterceptor());
            } else if (this.rowData.size() == 0) {
                throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.2"), getExceptionInterceptor());
            } else if (isBeforeFirst()) {
                throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.3"), getExceptionInterceptor());
            } else if (!isAfterLast()) {
                if (this.deleter == null) {
                    if (this.deleteSQL == null) {
                        generateStatements();
                    }
                    this.deleter = (PreparedStatement) this.connection.clientPrepareStatement(this.deleteSQL);
                }
                this.deleter.clearParameters();
                int numKeys = this.primaryKeyIndicies.size();
                for (int i = 0; i < numKeys; i++) {
                    int index = this.primaryKeyIndicies.get(i).intValue();
                    setParamValue(this.deleter, i + 1, this.thisRow, index, this.fields[index]);
                }
                this.deleter.executeUpdate();
                this.rowData.removeRow(this.rowData.getCurrentRowNumber());
                previous();
            } else {
                throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.4"), getExceptionInterceptor());
            }
        }
    }

    private void setParamValue(PreparedStatement ps, int psIdx, ResultSetRow row, int rsIdx, Field field) throws SQLException {
        PreparedStatement preparedStatement = ps;
        int i = psIdx;
        ResultSetRow resultSetRow = row;
        int i2 = rsIdx;
        byte[] val = row.getColumnValue(rsIdx);
        if (val == null) {
            preparedStatement.setNull(i, 0);
            return;
        }
        switch (field.getSQLType()) {
            case -6:
            case 4:
            case 5:
                preparedStatement.setInt(i, row.getInt(rsIdx));
                return;
            case -5:
                preparedStatement.setLong(i, row.getLong(rsIdx));
                return;
            case -1:
            case 1:
            case 2:
            case 3:
            case 12:
                preparedStatement.setString(i, resultSetRow.getString(i2, this.fields[i2].getEncoding(), this.connection));
                return;
            case 0:
                preparedStatement.setNull(i, 0);
                return;
            case 6:
            case 7:
            case 8:
            case 16:
                preparedStatement.setBytesNoEscapeNoQuotes(i, val);
                return;
            case 91:
                preparedStatement.setDate(i, resultSetRow.getDateFast(i2, this.connection, this, this.fastDefaultCal), this.fastDefaultCal);
                return;
            case 92:
                preparedStatement.setTime(i, row.getTimeFast(rsIdx, this.fastDefaultCal, this.connection.getServerTimezoneTZ(), false, this.connection, this));
                return;
            case 93:
                PreparedStatement preparedStatement2 = ps;
                int i3 = psIdx;
                preparedStatement2.setTimestampInternal(i3, row.getTimestampFast(rsIdx, this.fastDefaultCal, this.connection.getServerTimezoneTZ(), false, this.connection, this, false, false), (Calendar) null, this.connection.getDefaultTimeZone(), false, field.getDecimals(), false);
                return;
            default:
                preparedStatement.setBytes(i, val);
                return;
        }
    }

    private void extractDefaultValues() throws SQLException {
        DatabaseMetaData dbmd = this.connection.getMetaData();
        this.defaultColumnValue = new byte[this.fields.length][];
        ResultSet columnsResultSet = null;
        for (Map.Entry<String, Map<String, Map<String, Integer>>> dbEntry : this.databasesUsedToTablesUsed.entrySet()) {
            for (Map.Entry<String, Map<String, Integer>> tableEntry : dbEntry.getValue().entrySet()) {
                String tableName = tableEntry.getKey();
                Map<String, Integer> columnNamesToIndices = tableEntry.getValue();
                try {
                    columnsResultSet = dbmd.getColumns(this.catalog, (String) null, tableName, "%");
                    while (columnsResultSet.next()) {
                        String columnName = columnsResultSet.getString("COLUMN_NAME");
                        byte[] defaultValue = columnsResultSet.getBytes("COLUMN_DEF");
                        if (columnNamesToIndices.containsKey(columnName)) {
                            this.defaultColumnValue[columnNamesToIndices.get(columnName).intValue()] = defaultValue;
                        }
                    }
                } finally {
                    if (columnsResultSet != null) {
                        columnsResultSet.close();
                    }
                }
            }
        }
    }

    public boolean first() throws SQLException {
        return super.first();
    }

    /* access modifiers changed from: protected */
    public void generateStatements() throws SQLException {
        Map<String, String> tableNamesSoFar;
        StringBuilder columnNames;
        StringBuilder insertPlaceHolders;
        Map<String, Integer> updColumnNameToIndex;
        String columnName;
        String tableName;
        Map<Integer, String> columnIndicesToTable;
        StringBuilder fqcnBuf;
        String equalsStr;
        StringBuilder columnNames2;
        StringBuilder insertPlaceHolders2;
        if (this.isUpdatable) {
            String quotedId = getQuotedIdChar();
            if (this.connection.lowerCaseTableNames()) {
                tableNamesSoFar = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                this.databasesUsedToTablesUsed = new TreeMap(String.CASE_INSENSITIVE_ORDER);
            } else {
                tableNamesSoFar = new TreeMap<>();
                this.databasesUsedToTablesUsed = new TreeMap();
            }
            this.primaryKeyIndicies = new ArrayList();
            StringBuilder fieldValues = new StringBuilder();
            StringBuilder keyValues = new StringBuilder();
            StringBuilder columnNames3 = new StringBuilder();
            StringBuilder columnNames4 = new StringBuilder();
            StringBuilder allTablesBuf = new StringBuilder();
            Map<Integer, String> columnIndicesToTable2 = new HashMap<>();
            boolean firstTime = true;
            boolean keysFirstTime = true;
            String equalsStr2 = this.connection.versionMeetsMinimum(3, 23, 0) ? "<=>" : "=";
            int i = 0;
            while (i < this.fields.length) {
                StringBuilder tableNameBuffer = new StringBuilder();
                if (this.fields[i].getOriginalTableName() != null) {
                    String databaseName = this.fields[i].getDatabaseName();
                    if (databaseName != null && databaseName.length() > 0) {
                        tableNameBuffer.append(quotedId);
                        tableNameBuffer.append(databaseName);
                        tableNameBuffer.append(quotedId);
                        tableNameBuffer.append('.');
                    }
                    String tableOnlyName = this.fields[i].getOriginalTableName();
                    tableNameBuffer.append(quotedId);
                    tableNameBuffer.append(tableOnlyName);
                    tableNameBuffer.append(quotedId);
                    insertPlaceHolders = columnNames4;
                    String fqTableName = tableNameBuffer.toString();
                    if (!tableNamesSoFar.containsKey(fqTableName)) {
                        if (!tableNamesSoFar.isEmpty()) {
                            columnNames = columnNames3;
                            allTablesBuf.append(',');
                        } else {
                            columnNames = columnNames3;
                        }
                        allTablesBuf.append(fqTableName);
                        tableNamesSoFar.put(fqTableName, fqTableName);
                    } else {
                        columnNames = columnNames3;
                    }
                    columnIndicesToTable2.put(Integer.valueOf(i), fqTableName);
                    updColumnNameToIndex = getColumnsToIndexMapForTableAndDB(databaseName, tableOnlyName);
                } else {
                    columnNames = columnNames3;
                    insertPlaceHolders = columnNames4;
                    String tableOnlyName2 = this.fields[i].getTableName();
                    if (tableOnlyName2 != null) {
                        tableNameBuffer.append(quotedId);
                        tableNameBuffer.append(tableOnlyName2);
                        tableNameBuffer.append(quotedId);
                        String fqTableName2 = tableNameBuffer.toString();
                        if (!tableNamesSoFar.containsKey(fqTableName2)) {
                            if (!tableNamesSoFar.isEmpty()) {
                                allTablesBuf.append(',');
                            }
                            allTablesBuf.append(fqTableName2);
                            tableNamesSoFar.put(fqTableName2, fqTableName2);
                        }
                        columnIndicesToTable2.put(Integer.valueOf(i), fqTableName2);
                        updColumnNameToIndex = getColumnsToIndexMapForTableAndDB(this.catalog, tableOnlyName2);
                    } else {
                        updColumnNameToIndex = null;
                    }
                }
                String originalColumnName = this.fields[i].getOriginalName();
                if (!this.connection.getIO().hasLongColumnInfo() || originalColumnName == null || originalColumnName.length() <= 0) {
                    columnName = this.fields[i].getName();
                } else {
                    columnName = originalColumnName;
                }
                if (!(updColumnNameToIndex == null || columnName == null)) {
                    updColumnNameToIndex.put(columnName, Integer.valueOf(i));
                }
                String originalTableName = this.fields[i].getOriginalTableName();
                Map<String, String> tableNamesSoFar2 = tableNamesSoFar;
                if (!this.connection.getIO().hasLongColumnInfo() || originalTableName == null || originalTableName.length() <= 0) {
                    tableName = this.fields[i].getTableName();
                } else {
                    tableName = originalTableName;
                }
                StringBuilder fqcnBuf2 = new StringBuilder();
                String str = originalColumnName;
                String databaseName2 = this.fields[i].getDatabaseName();
                if (databaseName2 == null || databaseName2.length() <= 0) {
                    columnIndicesToTable = columnIndicesToTable2;
                    fqcnBuf = fqcnBuf2;
                    String str2 = databaseName2;
                } else {
                    columnIndicesToTable = columnIndicesToTable2;
                    fqcnBuf = fqcnBuf2;
                    fqcnBuf.append(quotedId);
                    fqcnBuf.append(databaseName2);
                    fqcnBuf.append(quotedId);
                    String str3 = databaseName2;
                    fqcnBuf.append('.');
                }
                fqcnBuf.append(quotedId);
                fqcnBuf.append(tableName);
                fqcnBuf.append(quotedId);
                fqcnBuf.append('.');
                fqcnBuf.append(quotedId);
                fqcnBuf.append(columnName);
                fqcnBuf.append(quotedId);
                String qualifiedColumnName = fqcnBuf.toString();
                String quotedId2 = quotedId;
                String str4 = tableName;
                if (this.fields[i].isPrimaryKey()) {
                    String str5 = columnName;
                    this.primaryKeyIndicies.add(Integer.valueOf(i));
                    if (!keysFirstTime) {
                        keyValues.append(" AND ");
                    } else {
                        keysFirstTime = false;
                    }
                    keyValues.append(qualifiedColumnName);
                    keyValues.append(equalsStr2);
                    keyValues.append("?");
                }
                if (firstTime) {
                    fieldValues.append("SET ");
                    firstTime = false;
                    columnNames2 = columnNames;
                    equalsStr = equalsStr2;
                    insertPlaceHolders2 = insertPlaceHolders;
                } else {
                    fieldValues.append(",");
                    columnNames2 = columnNames;
                    columnNames2.append(",");
                    equalsStr = equalsStr2;
                    insertPlaceHolders2 = insertPlaceHolders;
                    insertPlaceHolders2.append(",");
                }
                insertPlaceHolders2.append("?");
                columnNames2.append(qualifiedColumnName);
                fieldValues.append(qualifiedColumnName);
                fieldValues.append("=?");
                i++;
                columnNames3 = columnNames2;
                tableNamesSoFar = tableNamesSoFar2;
                quotedId = quotedId2;
                columnIndicesToTable2 = columnIndicesToTable;
                columnNames4 = insertPlaceHolders2;
                equalsStr2 = equalsStr;
            }
            String str6 = equalsStr2;
            Map<String, String> map = tableNamesSoFar;
            StringBuilder insertPlaceHolders3 = columnNames4;
            StringBuilder columnNames5 = columnNames3;
            this.qualifiedAndQuotedTableName = allTablesBuf.toString();
            this.updateSQL = "UPDATE " + this.qualifiedAndQuotedTableName + " " + fieldValues.toString() + " WHERE " + keyValues.toString();
            this.insertSQL = "INSERT INTO " + this.qualifiedAndQuotedTableName + " (" + columnNames5.toString() + ") VALUES (" + insertPlaceHolders3.toString() + ")";
            this.refreshSQL = "SELECT " + columnNames5.toString() + " FROM " + this.qualifiedAndQuotedTableName + " WHERE " + keyValues.toString();
            this.deleteSQL = "DELETE FROM " + this.qualifiedAndQuotedTableName + " WHERE " + keyValues.toString();
            return;
        }
        this.doingUpdates = false;
        this.onInsertRow = false;
        throw new NotUpdatable(this.notUpdatableReason);
    }

    private Map<String, Integer> getColumnsToIndexMapForTableAndDB(String databaseName, String tableName) {
        Map<String, Map<String, Integer>> tablesUsedToColumnsMap = this.databasesUsedToTablesUsed.get(databaseName);
        if (tablesUsedToColumnsMap == null) {
            if (this.connection.lowerCaseTableNames()) {
                tablesUsedToColumnsMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            } else {
                tablesUsedToColumnsMap = new TreeMap<>();
            }
            this.databasesUsedToTablesUsed.put(databaseName, tablesUsedToColumnsMap);
        }
        Map<String, Integer> nameToIndex = tablesUsedToColumnsMap.get(tableName);
        if (nameToIndex != null) {
            return nameToIndex;
        }
        Map<String, Integer> nameToIndex2 = new HashMap<>();
        tablesUsedToColumnsMap.put(tableName, nameToIndex2);
        return nameToIndex2;
    }

    private SingleByteCharsetConverter getCharConverter() throws SQLException {
        if (!this.initializedCharConverter) {
            this.initializedCharConverter = true;
            if (this.connection.getUseUnicode()) {
                this.charEncoding = this.connection.getEncoding();
                this.charConverter = this.connection.getCharsetConverter(this.charEncoding);
            }
        }
        return this.charConverter;
    }

    public int getConcurrency() throws SQLException {
        int i;
        synchronized (checkClosed().getConnectionMutex()) {
            i = this.isUpdatable ? 1008 : 1007;
        }
        return i;
    }

    private String getQuotedIdChar() throws SQLException {
        if (this.quotedIdChar == null) {
            if (this.connection.supportsQuotedIdentifiers()) {
                this.quotedIdChar = this.connection.getMetaData().getIdentifierQuoteString();
            } else {
                this.quotedIdChar = "";
            }
        }
        return this.quotedIdChar;
    }

    public void insertRow() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.onInsertRow) {
                this.inserter.executeUpdate();
                long autoIncrementId = this.inserter.getLastInsertID();
                int numFields = this.fields.length;
                byte[][] newRow = new byte[numFields][];
                for (int i = 0; i < numFields; i++) {
                    if (this.inserter.isNull(i)) {
                        newRow[i] = null;
                    } else {
                        newRow[i] = this.inserter.getBytesRepresentation(i);
                    }
                    if (this.fields[i].isAutoIncrement() && autoIncrementId > 0) {
                        newRow[i] = StringUtils.getBytes(String.valueOf(autoIncrementId));
                        this.inserter.setBytesNoEscapeNoQuotes(i + 1, newRow[i]);
                    }
                }
                ResultSetRow resultSetRow = new ByteArrayRow(newRow, getExceptionInterceptor());
                refreshRow(this.inserter, resultSetRow);
                this.rowData.addRow(resultSetRow);
                resetInserter();
            } else {
                throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.7"), getExceptionInterceptor());
            }
        }
    }

    public boolean isAfterLast() throws SQLException {
        return super.isAfterLast();
    }

    public boolean isBeforeFirst() throws SQLException {
        return super.isBeforeFirst();
    }

    public boolean isFirst() throws SQLException {
        return super.isFirst();
    }

    public boolean isLast() throws SQLException {
        return super.isLast();
    }

    /* access modifiers changed from: package-private */
    public boolean isUpdatable() {
        return this.isUpdatable;
    }

    public boolean last() throws SQLException {
        return super.last();
    }

    public void moveToCurrentRow() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.isUpdatable) {
                throw new NotUpdatable(this.notUpdatableReason);
            } else if (this.onInsertRow) {
                this.onInsertRow = false;
                this.thisRow = this.savedCurrentRow;
            }
        }
    }

    public void moveToInsertRow() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.isUpdatable) {
                if (this.inserter == null) {
                    if (this.insertSQL == null) {
                        generateStatements();
                    }
                    PreparedStatement preparedStatement = (PreparedStatement) this.connection.clientPrepareStatement(this.insertSQL);
                    this.inserter = preparedStatement;
                    preparedStatement.parameterMetaData = new MysqlParameterMetadata(this.fields, this.fields.length, getExceptionInterceptor());
                    if (this.populateInserterWithDefaultValues) {
                        extractDefaultValues();
                    }
                    resetInserter();
                } else {
                    resetInserter();
                }
                int numFields = this.fields.length;
                this.onInsertRow = true;
                this.doingUpdates = false;
                this.savedCurrentRow = this.thisRow;
                byte[][] newRowData = new byte[numFields][];
                this.thisRow = new ByteArrayRow(newRowData, getExceptionInterceptor());
                this.thisRow.setMetadata(this.fields);
                for (int i = 0; i < numFields; i++) {
                    if (!this.populateInserterWithDefaultValues) {
                        this.inserter.setBytesNoEscapeNoQuotes(i + 1, StringUtils.getBytes("DEFAULT"));
                        byte[][] bArr = null;
                        newRowData = null;
                    } else if (this.defaultColumnValue[i] != null) {
                        switch (this.fields[i].getMysqlType()) {
                            case 7:
                            case 10:
                            case 11:
                            case 12:
                            case 14:
                                byte[] bArr2 = this.defaultColumnValue[i];
                                if (bArr2.length <= 7 || bArr2[0] != 67 || bArr2[1] != 85 || bArr2[2] != 82 || bArr2[3] != 82 || bArr2[4] != 69 || bArr2[5] != 78 || bArr2[6] != 84 || bArr2[7] != 95) {
                                    this.inserter.setBytes(i + 1, bArr2, false, false);
                                    break;
                                } else {
                                    this.inserter.setBytesNoEscapeNoQuotes(i + 1, bArr2);
                                    break;
                                }
                                break;
                            default:
                                this.inserter.setBytes(i + 1, this.defaultColumnValue[i], false, false);
                                break;
                        }
                        byte[] bArr3 = this.defaultColumnValue[i];
                        byte[] defaultValueCopy = new byte[bArr3.length];
                        System.arraycopy(bArr3, 0, defaultValueCopy, 0, defaultValueCopy.length);
                        newRowData[i] = defaultValueCopy;
                    } else {
                        this.inserter.setNull(i + 1, 0);
                        newRowData[i] = null;
                    }
                }
            } else {
                throw new NotUpdatable(this.notUpdatableReason);
            }
        }
    }

    public boolean next() throws SQLException {
        return super.next();
    }

    public boolean prev() throws SQLException {
        return super.prev();
    }

    public boolean previous() throws SQLException {
        return super.previous();
    }

    public void realClose(boolean calledExplicitly) throws SQLException {
        if (this.connection != null) {
            synchronized (checkClosed().getConnectionMutex()) {
                SQLException sqlEx = null;
                if (this.useUsageAdvisor && this.deleter == null && this.inserter == null && this.refresher == null && this.updater == null) {
                    this.connection.getProfilerEventHandlerInstance().processEvent((byte) 0, this.connection, this.owningStatement, this, 0, new Throwable(), Messages.getString("UpdatableResultSet.34"));
                }
                try {
                    PreparedStatement preparedStatement = this.deleter;
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                } catch (SQLException ex) {
                    sqlEx = ex;
                }
                try {
                    PreparedStatement preparedStatement2 = this.inserter;
                    if (preparedStatement2 != null) {
                        preparedStatement2.close();
                    }
                } catch (SQLException ex2) {
                    sqlEx = ex2;
                }
                try {
                    PreparedStatement preparedStatement3 = this.refresher;
                    if (preparedStatement3 != null) {
                        preparedStatement3.close();
                    }
                } catch (SQLException ex3) {
                    sqlEx = ex3;
                }
                try {
                    PreparedStatement preparedStatement4 = this.updater;
                    if (preparedStatement4 != null) {
                        preparedStatement4.close();
                    }
                } catch (SQLException ex4) {
                    sqlEx = ex4;
                }
                super.realClose(calledExplicitly);
                if (sqlEx != null) {
                    throw sqlEx;
                }
            }
        }
    }

    public void refreshRow() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.isUpdatable) {
                throw new NotUpdatable();
            } else if (this.onInsertRow) {
                throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.8"), getExceptionInterceptor());
            } else if (this.rowData.size() == 0) {
                throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.9"), getExceptionInterceptor());
            } else if (isBeforeFirst()) {
                throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.10"), getExceptionInterceptor());
            } else if (!isAfterLast()) {
                refreshRow(this.updater, this.thisRow);
            } else {
                throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.11"), getExceptionInterceptor());
            }
        }
    }

    private void refreshRow(PreparedStatement updateInsertStmt, ResultSetRow rowToRefresh) throws SQLException {
        byte[] dataFrom;
        byte[] dataFrom2;
        if (this.refresher == null) {
            if (this.refreshSQL == null) {
                generateStatements();
            }
            PreparedStatement preparedStatement = (PreparedStatement) this.connection.clientPrepareStatement(this.refreshSQL);
            this.refresher = preparedStatement;
            preparedStatement.parameterMetaData = new MysqlParameterMetadata(this.fields, this.fields.length, getExceptionInterceptor());
        }
        this.refresher.clearParameters();
        int numKeys = this.primaryKeyIndicies.size();
        if (numKeys == 1) {
            int index = this.primaryKeyIndicies.get(0).intValue();
            if (this.doingUpdates || this.onInsertRow) {
                byte[] dataFrom3 = updateInsertStmt.getBytesRepresentation(index);
                if (updateInsertStmt.isNull(index) || dataFrom3.length == 0) {
                    dataFrom2 = rowToRefresh.getColumnValue(index);
                } else {
                    dataFrom2 = stripBinaryPrefix(dataFrom3);
                }
            } else {
                dataFrom2 = rowToRefresh.getColumnValue(index);
            }
            if (!this.fields[index].getvalueNeedsQuoting() || this.connection.isNoBackslashEscapesSet()) {
                this.refresher.setBytesNoEscapeNoQuotes(1, dataFrom2);
            } else {
                this.refresher.setBytesNoEscape(1, dataFrom2);
            }
        } else {
            for (int i = 0; i < numKeys; i++) {
                int index2 = this.primaryKeyIndicies.get(i).intValue();
                if (this.doingUpdates || this.onInsertRow) {
                    byte[] dataFrom4 = updateInsertStmt.getBytesRepresentation(index2);
                    if (updateInsertStmt.isNull(index2) || dataFrom4.length == 0) {
                        dataFrom = rowToRefresh.getColumnValue(index2);
                    } else {
                        dataFrom = stripBinaryPrefix(dataFrom4);
                    }
                } else {
                    dataFrom = rowToRefresh.getColumnValue(index2);
                }
                this.refresher.setBytesNoEscape(i + 1, dataFrom);
            }
        }
        ResultSet rs = null;
        try {
            rs = this.refresher.executeQuery();
            int numCols = rs.getMetaData().getColumnCount();
            if (rs.next()) {
                for (int i2 = 0; i2 < numCols; i2++) {
                    if (rs.getBytes(i2 + 1) != null) {
                        if (!rs.wasNull()) {
                            rowToRefresh.setColumnValue(i2, rs.getBytes(i2 + 1));
                        }
                    }
                    rowToRefresh.setColumnValue(i2, (byte[]) null);
                }
            } else {
                throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.12"), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public boolean relative(int rows) throws SQLException {
        return super.relative(rows);
    }

    private void resetInserter() throws SQLException {
        this.inserter.clearParameters();
        for (int i = 0; i < this.fields.length; i++) {
            this.inserter.setNull(i + 1, 0);
        }
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
    public void setResultSetConcurrency(int concurrencyFlag) {
        super.setResultSetConcurrency(concurrencyFlag);
    }

    private byte[] stripBinaryPrefix(byte[] dataFrom) {
        return StringUtils.stripEnclosure(dataFrom, "_binary'", "'");
    }

    /* access modifiers changed from: protected */
    public void syncUpdate() throws SQLException {
        if (this.updater == null) {
            if (this.updateSQL == null) {
                generateStatements();
            }
            PreparedStatement preparedStatement = (PreparedStatement) this.connection.clientPrepareStatement(this.updateSQL);
            this.updater = preparedStatement;
            preparedStatement.parameterMetaData = new MysqlParameterMetadata(this.fields, this.fields.length, getExceptionInterceptor());
        }
        int numFields = this.fields.length;
        this.updater.clearParameters();
        for (int i = 0; i < numFields; i++) {
            if (this.thisRow.getColumnValue(i) == null) {
                this.updater.setNull(i + 1, 0);
            } else if (!this.fields[i].getvalueNeedsQuoting()) {
                this.updater.setBytesNoEscapeNoQuotes(i + 1, this.thisRow.getColumnValue(i));
            } else if (!this.fields[i].isCharsetApplicableType() || this.fields[i].getEncoding().equals(this.connection.getEncoding())) {
                this.updater.setBytes(i + 1, this.thisRow.getColumnValue(i), this.fields[i].isBinary(), false);
            } else {
                this.updater.setString(i + 1, this.thisRow.getString(i, this.fields[i].getEncoding(), this.connection));
            }
        }
        int numKeys = this.primaryKeyIndicies.size();
        for (int i2 = 0; i2 < numKeys; i2++) {
            int idx = this.primaryKeyIndicies.get(i2).intValue();
            setParamValue(this.updater, numFields + i2 + 1, this.thisRow, idx, this.fields[idx]);
        }
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                if (!this.doingUpdates) {
                    this.doingUpdates = true;
                    syncUpdate();
                }
                this.updater.setAsciiStream(columnIndex, x, length);
            } else {
                this.inserter.setAsciiStream(columnIndex, x, length);
                this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
            }
        }
    }

    public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
        updateAsciiStream(findColumn(columnName), x, length);
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                if (!this.doingUpdates) {
                    this.doingUpdates = true;
                    syncUpdate();
                }
                this.updater.setBigDecimal(columnIndex, x);
            } else {
                this.inserter.setBigDecimal(columnIndex, x);
                if (x == null) {
                    this.thisRow.setColumnValue(columnIndex - 1, (byte[]) null);
                } else {
                    this.thisRow.setColumnValue(columnIndex - 1, StringUtils.getBytes(x.toString()));
                }
            }
        }
    }

    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
        updateBigDecimal(findColumn(columnName), x);
    }

    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                if (!this.doingUpdates) {
                    this.doingUpdates = true;
                    syncUpdate();
                }
                this.updater.setBinaryStream(columnIndex, x, length);
            } else {
                this.inserter.setBinaryStream(columnIndex, x, length);
                if (x == null) {
                    this.thisRow.setColumnValue(columnIndex - 1, (byte[]) null);
                } else {
                    this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
                }
            }
        }
    }

    public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
        updateBinaryStream(findColumn(columnName), x, length);
    }

    public void updateBlob(int columnIndex, Blob blob) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                if (!this.doingUpdates) {
                    this.doingUpdates = true;
                    syncUpdate();
                }
                this.updater.setBlob(columnIndex, blob);
            } else {
                this.inserter.setBlob(columnIndex, blob);
                if (blob == null) {
                    this.thisRow.setColumnValue(columnIndex - 1, (byte[]) null);
                } else {
                    this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
                }
            }
        }
    }

    public void updateBlob(String columnName, Blob blob) throws SQLException {
        updateBlob(findColumn(columnName), blob);
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                if (!this.doingUpdates) {
                    this.doingUpdates = true;
                    syncUpdate();
                }
                this.updater.setBoolean(columnIndex, x);
            } else {
                this.inserter.setBoolean(columnIndex, x);
                this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
            }
        }
    }

    public void updateBoolean(String columnName, boolean x) throws SQLException {
        updateBoolean(findColumn(columnName), x);
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                if (!this.doingUpdates) {
                    this.doingUpdates = true;
                    syncUpdate();
                }
                this.updater.setByte(columnIndex, x);
            } else {
                this.inserter.setByte(columnIndex, x);
                this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
            }
        }
    }

    public void updateByte(String columnName, byte x) throws SQLException {
        updateByte(findColumn(columnName), x);
    }

    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                if (!this.doingUpdates) {
                    this.doingUpdates = true;
                    syncUpdate();
                }
                this.updater.setBytes(columnIndex, x);
            } else {
                this.inserter.setBytes(columnIndex, x);
                this.thisRow.setColumnValue(columnIndex - 1, x);
            }
        }
    }

    public void updateBytes(String columnName, byte[] x) throws SQLException {
        updateBytes(findColumn(columnName), x);
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                if (!this.doingUpdates) {
                    this.doingUpdates = true;
                    syncUpdate();
                }
                this.updater.setCharacterStream(columnIndex, x, length);
            } else {
                this.inserter.setCharacterStream(columnIndex, x, length);
                if (x == null) {
                    this.thisRow.setColumnValue(columnIndex - 1, (byte[]) null);
                } else {
                    this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
                }
            }
        }
    }

    public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
        updateCharacterStream(findColumn(columnName), reader, length);
    }

    public void updateClob(int columnIndex, Clob clob) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (clob == null) {
                updateNull(columnIndex);
            } else {
                updateCharacterStream(columnIndex, clob.getCharacterStream(), (int) clob.length());
            }
        }
    }

    public void updateDate(int columnIndex, Date x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                if (!this.doingUpdates) {
                    this.doingUpdates = true;
                    syncUpdate();
                }
                this.updater.setDate(columnIndex, x);
            } else {
                this.inserter.setDate(columnIndex, x);
                this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
            }
        }
    }

    public void updateDate(String columnName, Date x) throws SQLException {
        updateDate(findColumn(columnName), x);
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                if (!this.doingUpdates) {
                    this.doingUpdates = true;
                    syncUpdate();
                }
                this.updater.setDouble(columnIndex, x);
            } else {
                this.inserter.setDouble(columnIndex, x);
                this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
            }
        }
    }

    public void updateDouble(String columnName, double x) throws SQLException {
        updateDouble(findColumn(columnName), x);
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                if (!this.doingUpdates) {
                    this.doingUpdates = true;
                    syncUpdate();
                }
                this.updater.setFloat(columnIndex, x);
            } else {
                this.inserter.setFloat(columnIndex, x);
                this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
            }
        }
    }

    public void updateFloat(String columnName, float x) throws SQLException {
        updateFloat(findColumn(columnName), x);
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                if (!this.doingUpdates) {
                    this.doingUpdates = true;
                    syncUpdate();
                }
                this.updater.setInt(columnIndex, x);
            } else {
                this.inserter.setInt(columnIndex, x);
                this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
            }
        }
    }

    public void updateInt(String columnName, int x) throws SQLException {
        updateInt(findColumn(columnName), x);
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                if (!this.doingUpdates) {
                    this.doingUpdates = true;
                    syncUpdate();
                }
                this.updater.setLong(columnIndex, x);
            } else {
                this.inserter.setLong(columnIndex, x);
                this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
            }
        }
    }

    public void updateLong(String columnName, long x) throws SQLException {
        updateLong(findColumn(columnName), x);
    }

    public void updateNull(int columnIndex) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                if (!this.doingUpdates) {
                    this.doingUpdates = true;
                    syncUpdate();
                }
                this.updater.setNull(columnIndex, 0);
            } else {
                this.inserter.setNull(columnIndex, 0);
                this.thisRow.setColumnValue(columnIndex - 1, (byte[]) null);
            }
        }
    }

    public void updateNull(String columnName) throws SQLException {
        updateNull(findColumn(columnName));
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        updateObjectInternal(columnIndex, x, (Integer) null, 0);
    }

    public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
        updateObjectInternal(columnIndex, x, (Integer) null, scale);
    }

    /* access modifiers changed from: protected */
    public void updateObjectInternal(int columnIndex, Object x, Integer targetType, int scaleOrLength) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                if (!this.doingUpdates) {
                    this.doingUpdates = true;
                    syncUpdate();
                }
                if (targetType == null) {
                    this.updater.setObject(columnIndex, x);
                } else {
                    this.updater.setObject(columnIndex, x, targetType.intValue());
                }
            } else {
                if (targetType == null) {
                    this.inserter.setObject(columnIndex, x);
                } else {
                    this.inserter.setObject(columnIndex, x, targetType.intValue());
                }
                this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
            }
        }
    }

    public void updateObject(String columnName, Object x) throws SQLException {
        updateObject(findColumn(columnName), x);
    }

    public void updateObject(String columnName, Object x, int scale) throws SQLException {
        updateObject(findColumn(columnName), x);
    }

    public void updateRow() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.isUpdatable) {
                if (this.doingUpdates) {
                    this.updater.executeUpdate();
                    refreshRow();
                    this.doingUpdates = false;
                } else if (this.onInsertRow) {
                    throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.44"), getExceptionInterceptor());
                }
                syncUpdate();
            } else {
                throw new NotUpdatable(this.notUpdatableReason);
            }
        }
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                if (!this.doingUpdates) {
                    this.doingUpdates = true;
                    syncUpdate();
                }
                this.updater.setShort(columnIndex, x);
            } else {
                this.inserter.setShort(columnIndex, x);
                this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
            }
        }
    }

    public void updateShort(String columnName, short x) throws SQLException {
        updateShort(findColumn(columnName), x);
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                if (!this.doingUpdates) {
                    this.doingUpdates = true;
                    syncUpdate();
                }
                this.updater.setString(columnIndex, x);
            } else {
                this.inserter.setString(columnIndex, x);
                if (x == null) {
                    this.thisRow.setColumnValue(columnIndex - 1, (byte[]) null);
                } else if (getCharConverter() != null) {
                    this.thisRow.setColumnValue(columnIndex - 1, StringUtils.getBytes(x, this.charConverter, this.charEncoding, this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), getExceptionInterceptor()));
                } else {
                    this.thisRow.setColumnValue(columnIndex - 1, StringUtils.getBytes(x));
                }
            }
        }
    }

    public void updateString(String columnName, String x) throws SQLException {
        updateString(findColumn(columnName), x);
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                if (!this.doingUpdates) {
                    this.doingUpdates = true;
                    syncUpdate();
                }
                this.updater.setTime(columnIndex, x);
            } else {
                this.inserter.setTime(columnIndex, x);
                this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
            }
        }
    }

    public void updateTime(String columnName, Time x) throws SQLException {
        updateTime(findColumn(columnName), x);
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.onInsertRow) {
                if (!this.doingUpdates) {
                    this.doingUpdates = true;
                    syncUpdate();
                }
                this.updater.setTimestamp(columnIndex, x);
            } else {
                this.inserter.setTimestamp(columnIndex, x);
                this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
            }
        }
    }

    public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
        updateTimestamp(findColumn(columnName), x);
    }
}
