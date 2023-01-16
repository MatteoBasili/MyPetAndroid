package com.mysql.jdbc;

import androidx.recyclerview.widget.ItemTouchHelper;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.regex.PatternSyntaxException;

public class Field {
    private static final int AUTO_INCREMENT_FLAG = 512;
    private static final int NO_CHARSET_INFO = -1;
    private byte[] buffer;
    private int colDecimals;
    private short colFlag;
    private int collationIndex;
    private String collationName;
    private MySQLConnection connection;
    private String databaseName;
    private int databaseNameLength;
    private int databaseNameStart;
    protected int defaultValueLength;
    protected int defaultValueStart;
    private String encoding;
    private String fullName;
    private String fullOriginalName;
    private boolean isImplicitTempTable;
    private boolean isSingleBit;
    private long length;
    private int maxBytesPerChar;
    private int mysqlType;
    private String name;
    private int nameLength;
    private int nameStart;
    private String originalColumnName;
    private int originalColumnNameLength;
    private int originalColumnNameStart;
    private String originalTableName;
    private int originalTableNameLength;
    private int originalTableNameStart;
    private int precisionAdjustFactor;
    private int sqlType;
    private String tableName;
    private int tableNameLength;
    private int tableNameStart;
    private boolean useOldNameMetadata;
    private final boolean valueNeedsQuoting;

    Field(MySQLConnection conn, byte[] buffer2, int databaseNameStart2, int databaseNameLength2, int tableNameStart2, int tableNameLength2, int originalTableNameStart2, int originalTableNameLength2, int nameStart2, int nameLength2, int originalColumnNameStart2, int originalColumnNameLength2, long length2, int mysqlType2, short colFlag2, int colDecimals2, int defaultValueStart2, int defaultValueLength2, int charsetIndex) throws SQLException {
        this.collationIndex = 0;
        this.encoding = null;
        this.collationName = null;
        this.connection = null;
        this.databaseName = null;
        this.databaseNameLength = -1;
        this.databaseNameStart = -1;
        this.defaultValueLength = -1;
        this.defaultValueStart = -1;
        this.fullName = null;
        this.fullOriginalName = null;
        this.isImplicitTempTable = false;
        this.mysqlType = -1;
        this.originalColumnName = null;
        this.originalColumnNameLength = -1;
        this.originalColumnNameStart = -1;
        this.originalTableName = null;
        this.originalTableNameLength = -1;
        this.originalTableNameStart = -1;
        this.precisionAdjustFactor = 0;
        this.sqlType = -1;
        this.useOldNameMetadata = false;
        this.connection = conn;
        this.buffer = buffer2;
        this.nameStart = nameStart2;
        this.nameLength = nameLength2;
        this.tableNameStart = tableNameStart2;
        this.tableNameLength = tableNameLength2;
        this.length = length2;
        this.colFlag = colFlag2;
        this.colDecimals = colDecimals2;
        this.mysqlType = mysqlType2;
        this.databaseNameStart = databaseNameStart2;
        this.databaseNameLength = databaseNameLength2;
        this.originalTableNameStart = originalTableNameStart2;
        this.originalTableNameLength = originalTableNameLength2;
        this.originalColumnNameStart = originalColumnNameStart2;
        this.originalColumnNameLength = originalColumnNameLength2;
        this.defaultValueStart = defaultValueStart2;
        this.defaultValueLength = defaultValueLength2;
        this.collationIndex = charsetIndex;
        this.sqlType = MysqlDefs.mysqlToJavaType(mysqlType2);
        checkForImplicitTemporaryTable();
        boolean isFromFunction = this.originalTableNameLength == 0;
        if (this.mysqlType == 252) {
            if (this.connection.getBlobsAreStrings() || (this.connection.getFunctionsNeverReturnBlobs() && isFromFunction)) {
                this.sqlType = 12;
                this.mysqlType = 15;
            } else if (this.collationIndex != 63 && this.connection.versionMeetsMinimum(4, 1, 0)) {
                this.mysqlType = 253;
                this.sqlType = -1;
            } else if (!this.connection.getUseBlobToStoreUTF8OutsideBMP() || !shouldSetupForUtf8StringInBlob()) {
                setBlobTypeBasedOnLength();
                this.sqlType = MysqlDefs.mysqlToJavaType(this.mysqlType);
            } else {
                setupForUtf8StringInBlob();
            }
        }
        if (this.sqlType == -6 && this.length == 1 && this.connection.getTinyInt1isBit() && conn.getTinyInt1isBit()) {
            if (conn.getTransformedBitIsBoolean()) {
                this.sqlType = 16;
            } else {
                this.sqlType = -7;
            }
        }
        if (isNativeNumericType() || isNativeDateTimeType()) {
            this.encoding = "US-ASCII";
        } else {
            String encodingForIndex = this.connection.getEncodingForIndex(this.collationIndex);
            this.encoding = encodingForIndex;
            if ("UnicodeBig".equals(encodingForIndex)) {
                this.encoding = "UTF-16";
            }
            if (this.mysqlType == 245) {
                this.encoding = "UTF-8";
            }
            boolean isBinary = isBinary();
            if (this.connection.versionMeetsMinimum(4, 1, 0) && this.mysqlType == 253 && isBinary && this.collationIndex == 63) {
                if (this.connection.getFunctionsNeverReturnBlobs() && isFromFunction) {
                    this.sqlType = 12;
                    this.mysqlType = 15;
                } else if (isOpaqueBinary()) {
                    this.sqlType = -3;
                }
            }
            if (this.connection.versionMeetsMinimum(4, 1, 0) && this.mysqlType == 254 && isBinary && this.collationIndex == 63 && isOpaqueBinary() && !this.connection.getBlobsAreStrings()) {
                this.sqlType = -2;
            }
            if (this.mysqlType == 16) {
                long j = this.length;
                boolean z = j == 0 || (j == 1 && (this.connection.versionMeetsMinimum(5, 0, 21) || this.connection.versionMeetsMinimum(5, 1, 10)));
                this.isSingleBit = z;
                if (!z) {
                    short s = (short) (this.colFlag | 128);
                    this.colFlag = s;
                    this.colFlag = (short) (s | 16);
                    isBinary = true;
                }
            }
            int i = this.sqlType;
            if (i == -4 && !isBinary) {
                this.sqlType = -1;
            } else if (i == -3 && !isBinary) {
                this.sqlType = 12;
            }
        }
        if (isUnsigned()) {
            switch (this.mysqlType) {
                case 4:
                case 5:
                    this.precisionAdjustFactor = 1;
                    break;
            }
        } else {
            switch (this.mysqlType) {
                case 0:
                case 246:
                    this.precisionAdjustFactor = -1;
                    break;
                case 4:
                case 5:
                    this.precisionAdjustFactor = 1;
                    break;
            }
        }
        this.valueNeedsQuoting = determineNeedsQuoting();
    }

    private boolean shouldSetupForUtf8StringInBlob() throws SQLException {
        String includePattern = this.connection.getUtf8OutsideBmpIncludedColumnNamePattern();
        String excludePattern = this.connection.getUtf8OutsideBmpExcludedColumnNamePattern();
        if (excludePattern != null && !StringUtils.isEmptyOrWhitespaceOnly(excludePattern)) {
            try {
                if (getOriginalName().matches(excludePattern)) {
                    if (includePattern == null || StringUtils.isEmptyOrWhitespaceOnly(includePattern)) {
                        return false;
                    }
                    if (getOriginalName().matches(includePattern)) {
                        return true;
                    }
                    return false;
                }
            } catch (PatternSyntaxException pse) {
                SQLException sqlEx = SQLError.createSQLException("Illegal regex specified for \"utf8OutsideBmpIncludedColumnNamePattern\"", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.connection.getExceptionInterceptor());
                if (!this.connection.getParanoid()) {
                    sqlEx.initCause(pse);
                }
                throw sqlEx;
            } catch (PatternSyntaxException pse2) {
                SQLException sqlEx2 = SQLError.createSQLException("Illegal regex specified for \"utf8OutsideBmpExcludedColumnNamePattern\"", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.connection.getExceptionInterceptor());
                if (!this.connection.getParanoid()) {
                    sqlEx2.initCause(pse2);
                }
                throw sqlEx2;
            }
        }
        return true;
    }

    private void setupForUtf8StringInBlob() {
        long j = this.length;
        if (j == 255 || j == 65535) {
            this.mysqlType = 15;
            this.sqlType = 12;
        } else {
            this.mysqlType = 253;
            this.sqlType = -1;
        }
        this.collationIndex = 33;
    }

    Field(MySQLConnection conn, byte[] buffer2, int nameStart2, int nameLength2, int tableNameStart2, int tableNameLength2, int length2, int mysqlType2, short colFlag2, int colDecimals2) throws SQLException {
        this(conn, buffer2, -1, -1, tableNameStart2, tableNameLength2, -1, -1, nameStart2, nameLength2, -1, -1, (long) length2, mysqlType2, colFlag2, colDecimals2, -1, -1, -1);
    }

    Field(String tableName2, String columnName, int jdbcType, int length2) {
        this.collationIndex = 0;
        this.encoding = null;
        this.collationName = null;
        this.connection = null;
        this.databaseName = null;
        this.databaseNameLength = -1;
        this.databaseNameStart = -1;
        this.defaultValueLength = -1;
        this.defaultValueStart = -1;
        this.fullName = null;
        this.fullOriginalName = null;
        this.isImplicitTempTable = false;
        this.mysqlType = -1;
        this.originalColumnName = null;
        this.originalColumnNameLength = -1;
        this.originalColumnNameStart = -1;
        this.originalTableName = null;
        this.originalTableNameLength = -1;
        this.originalTableNameStart = -1;
        this.precisionAdjustFactor = 0;
        this.sqlType = -1;
        this.useOldNameMetadata = false;
        this.tableName = tableName2;
        this.name = columnName;
        this.length = (long) length2;
        this.sqlType = jdbcType;
        this.colFlag = 0;
        this.colDecimals = 0;
        this.valueNeedsQuoting = determineNeedsQuoting();
    }

    Field(String tableName2, String columnName, int charsetIndex, int jdbcType, int length2) {
        this.collationIndex = 0;
        this.encoding = null;
        this.collationName = null;
        this.connection = null;
        this.databaseName = null;
        this.databaseNameLength = -1;
        this.databaseNameStart = -1;
        this.defaultValueLength = -1;
        this.defaultValueStart = -1;
        this.fullName = null;
        this.fullOriginalName = null;
        this.isImplicitTempTable = false;
        this.mysqlType = -1;
        this.originalColumnName = null;
        this.originalColumnNameLength = -1;
        this.originalColumnNameStart = -1;
        this.originalTableName = null;
        this.originalTableNameLength = -1;
        this.originalTableNameStart = -1;
        this.precisionAdjustFactor = 0;
        this.sqlType = -1;
        this.useOldNameMetadata = false;
        this.tableName = tableName2;
        this.name = columnName;
        this.length = (long) length2;
        this.sqlType = jdbcType;
        this.colFlag = 0;
        this.colDecimals = 0;
        this.collationIndex = charsetIndex;
        this.valueNeedsQuoting = determineNeedsQuoting();
        switch (this.sqlType) {
            case -3:
            case -2:
                short s = (short) (this.colFlag | 128);
                this.colFlag = s;
                this.colFlag = (short) (s | 16);
                return;
            default:
                return;
        }
    }

    private void checkForImplicitTemporaryTable() {
        boolean z;
        if (this.tableNameLength > 5) {
            byte[] bArr = this.buffer;
            int i = this.tableNameStart;
            if (bArr[i] == 35 && bArr[i + 1] == 115 && bArr[i + 2] == 113 && bArr[i + 3] == 108 && bArr[i + 4] == 95) {
                z = true;
                this.isImplicitTempTable = z;
            }
        }
        z = false;
        this.isImplicitTempTable = z;
    }

    public String getEncoding() throws SQLException {
        return this.encoding;
    }

    public void setEncoding(String javaEncodingName, Connection conn) throws SQLException {
        this.encoding = javaEncodingName;
        try {
            this.collationIndex = CharsetMapping.getCollationIndexForJavaEncoding(javaEncodingName, conn);
        } catch (RuntimeException ex) {
            SQLException sqlEx = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
            sqlEx.initCause(ex);
            throw sqlEx;
        }
    }

    public int getCollationIndex() throws SQLException {
        return this.collationIndex;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00ba, code lost:
        r8 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00bb, code lost:
        if (r7 != null) goto L_0x00bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00bd, code lost:
        r7.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00c1, code lost:
        if (r6 != null) goto L_0x00c3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00c3, code lost:
        r6.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00c8, code lost:
        throw r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00d3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:?, code lost:
        r1 = com.mysql.jdbc.SQLError.createSQLException(r0.toString(), com.mysql.jdbc.SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (com.mysql.jdbc.ExceptionInterceptor) null);
        r1.initCause(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00e2, code lost:
        throw r1;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:25:0x0081, B:44:0x00ca] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized java.lang.String getCollation() throws java.sql.SQLException {
        /*
            r9 = this;
            monitor-enter(r9)
            java.lang.String r0 = r9.collationName     // Catch:{ all -> 0x00e7 }
            if (r0 != 0) goto L_0x00e3
            com.mysql.jdbc.MySQLConnection r0 = r9.connection     // Catch:{ all -> 0x00e7 }
            if (r0 == 0) goto L_0x00e3
            r1 = 4
            r2 = 1
            r3 = 0
            boolean r0 = r0.versionMeetsMinimum(r1, r2, r3)     // Catch:{ all -> 0x00e7 }
            if (r0 == 0) goto L_0x00e3
            com.mysql.jdbc.MySQLConnection r0 = r9.connection     // Catch:{ all -> 0x00e7 }
            boolean r0 = r0.getUseDynamicCharsetInfo()     // Catch:{ all -> 0x00e7 }
            if (r0 == 0) goto L_0x00ca
            com.mysql.jdbc.MySQLConnection r0 = r9.connection     // Catch:{ all -> 0x00e7 }
            java.sql.DatabaseMetaData r0 = r0.getMetaData()     // Catch:{ all -> 0x00e7 }
            java.lang.String r1 = r0.getIdentifierQuoteString()     // Catch:{ all -> 0x00e7 }
            java.lang.String r2 = " "
            boolean r2 = r2.equals(r1)     // Catch:{ all -> 0x00e7 }
            if (r2 == 0) goto L_0x002f
            java.lang.String r2 = ""
            r1 = r2
        L_0x002f:
            java.lang.String r2 = r9.getDatabaseName()     // Catch:{ all -> 0x00e7 }
            java.lang.String r3 = r9.getOriginalTableName()     // Catch:{ all -> 0x00e7 }
            java.lang.String r4 = r9.getOriginalName()     // Catch:{ all -> 0x00e7 }
            if (r2 == 0) goto L_0x00c9
            int r5 = r2.length()     // Catch:{ all -> 0x00e7 }
            if (r5 == 0) goto L_0x00c9
            if (r3 == 0) goto L_0x00c9
            int r5 = r3.length()     // Catch:{ all -> 0x00e7 }
            if (r5 == 0) goto L_0x00c9
            if (r4 == 0) goto L_0x00c9
            int r5 = r4.length()     // Catch:{ all -> 0x00e7 }
            if (r5 == 0) goto L_0x00c9
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00e7 }
            int r6 = r2.length()     // Catch:{ all -> 0x00e7 }
            int r7 = r3.length()     // Catch:{ all -> 0x00e7 }
            int r6 = r6 + r7
            int r6 = r6 + 28
            r5.<init>(r6)     // Catch:{ all -> 0x00e7 }
            java.lang.String r6 = "SHOW FULL COLUMNS FROM "
            r5.append(r6)     // Catch:{ all -> 0x00e7 }
            r5.append(r1)     // Catch:{ all -> 0x00e7 }
            r5.append(r2)     // Catch:{ all -> 0x00e7 }
            r5.append(r1)     // Catch:{ all -> 0x00e7 }
            java.lang.String r6 = "."
            r5.append(r6)     // Catch:{ all -> 0x00e7 }
            r5.append(r1)     // Catch:{ all -> 0x00e7 }
            r5.append(r3)     // Catch:{ all -> 0x00e7 }
            r5.append(r1)     // Catch:{ all -> 0x00e7 }
            r6 = 0
            r7 = 0
            com.mysql.jdbc.MySQLConnection r8 = r9.connection     // Catch:{ all -> 0x00ba }
            java.sql.Statement r8 = r8.createStatement()     // Catch:{ all -> 0x00ba }
            r6 = r8
            java.lang.String r8 = r5.toString()     // Catch:{ all -> 0x00ba }
            java.sql.ResultSet r8 = r6.executeQuery(r8)     // Catch:{ all -> 0x00ba }
            r7 = r8
        L_0x0091:
            boolean r8 = r7.next()     // Catch:{ all -> 0x00ba }
            if (r8 == 0) goto L_0x00ab
            java.lang.String r8 = "Field"
            java.lang.String r8 = r7.getString(r8)     // Catch:{ all -> 0x00ba }
            boolean r8 = r4.equals(r8)     // Catch:{ all -> 0x00ba }
            if (r8 == 0) goto L_0x0091
            java.lang.String r8 = "Collation"
            java.lang.String r8 = r7.getString(r8)     // Catch:{ all -> 0x00ba }
            r9.collationName = r8     // Catch:{ all -> 0x00ba }
        L_0x00ab:
            if (r7 == 0) goto L_0x00b2
            r7.close()     // Catch:{ all -> 0x00e7 }
            r7 = 0
        L_0x00b2:
            if (r6 == 0) goto L_0x00b8
            r6.close()     // Catch:{ all -> 0x00e7 }
            r6 = 0
        L_0x00b8:
            goto L_0x00c9
        L_0x00ba:
            r8 = move-exception
            if (r7 == 0) goto L_0x00c1
            r7.close()     // Catch:{ all -> 0x00e7 }
            r7 = 0
        L_0x00c1:
            if (r6 == 0) goto L_0x00c7
            r6.close()     // Catch:{ all -> 0x00e7 }
            r6 = 0
        L_0x00c7:
            throw r8     // Catch:{ all -> 0x00e7 }
        L_0x00c9:
            goto L_0x00e3
        L_0x00ca:
            java.lang.String[] r0 = com.mysql.jdbc.CharsetMapping.COLLATION_INDEX_TO_COLLATION_NAME     // Catch:{ RuntimeException -> 0x00d3 }
            int r1 = r9.collationIndex     // Catch:{ RuntimeException -> 0x00d3 }
            r0 = r0[r1]     // Catch:{ RuntimeException -> 0x00d3 }
            r9.collationName = r0     // Catch:{ RuntimeException -> 0x00d3 }
            goto L_0x00e3
        L_0x00d3:
            r0 = move-exception
            java.lang.String r1 = r0.toString()     // Catch:{ all -> 0x00e7 }
            java.lang.String r2 = "S1009"
            r3 = 0
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (com.mysql.jdbc.ExceptionInterceptor) r3)     // Catch:{ all -> 0x00e7 }
            r1.initCause(r0)     // Catch:{ all -> 0x00e7 }
            throw r1     // Catch:{ all -> 0x00e7 }
        L_0x00e3:
            java.lang.String r0 = r9.collationName     // Catch:{ all -> 0x00e7 }
            monitor-exit(r9)
            return r0
        L_0x00e7:
            r0 = move-exception
            monitor-exit(r9)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.Field.getCollation():java.lang.String");
    }

    public String getColumnLabel() throws SQLException {
        return getName();
    }

    public String getDatabaseName() throws SQLException {
        int i;
        int i2;
        if (!(this.databaseName != null || (i = this.databaseNameStart) == -1 || (i2 = this.databaseNameLength) == -1)) {
            this.databaseName = getStringFromBytes(i, i2);
        }
        return this.databaseName;
    }

    /* access modifiers changed from: package-private */
    public int getDecimals() {
        return this.colDecimals;
    }

    public String getFullName() throws SQLException {
        if (this.fullName == null) {
            StringBuilder fullNameBuf = new StringBuilder(getTableName().length() + 1 + getName().length());
            fullNameBuf.append(this.tableName);
            fullNameBuf.append('.');
            fullNameBuf.append(this.name);
            this.fullName = fullNameBuf.toString();
        }
        return this.fullName;
    }

    public String getFullOriginalName() throws SQLException {
        getOriginalName();
        if (this.originalColumnName == null) {
            return null;
        }
        if (this.fullName == null) {
            StringBuilder fullOriginalNameBuf = new StringBuilder(getOriginalTableName().length() + 1 + getOriginalName().length());
            fullOriginalNameBuf.append(this.originalTableName);
            fullOriginalNameBuf.append('.');
            fullOriginalNameBuf.append(this.originalColumnName);
            this.fullOriginalName = fullOriginalNameBuf.toString();
        }
        return this.fullOriginalName;
    }

    public long getLength() {
        return this.length;
    }

    public synchronized int getMaxBytesPerCharacter() throws SQLException {
        if (this.maxBytesPerChar == 0) {
            this.maxBytesPerChar = this.connection.getMaxBytesPerChar(Integer.valueOf(this.collationIndex), getEncoding());
        }
        return this.maxBytesPerChar;
    }

    public int getMysqlType() {
        return this.mysqlType;
    }

    public String getName() throws SQLException {
        if (this.name == null) {
            this.name = getStringFromBytes(this.nameStart, this.nameLength);
        }
        return this.name;
    }

    public String getNameNoAliases() throws SQLException {
        if (this.useOldNameMetadata) {
            return getName();
        }
        MySQLConnection mySQLConnection = this.connection;
        if (mySQLConnection == null || !mySQLConnection.versionMeetsMinimum(4, 1, 0)) {
            return getName();
        }
        return getOriginalName();
    }

    public String getOriginalName() throws SQLException {
        int i;
        int i2;
        if (!(this.originalColumnName != null || (i = this.originalColumnNameStart) == -1 || (i2 = this.originalColumnNameLength) == -1)) {
            this.originalColumnName = getStringFromBytes(i, i2);
        }
        return this.originalColumnName;
    }

    public String getOriginalTableName() throws SQLException {
        int i;
        int i2;
        if (!(this.originalTableName != null || (i = this.originalTableNameStart) == -1 || (i2 = this.originalTableNameLength) == -1)) {
            this.originalTableName = getStringFromBytes(i, i2);
        }
        return this.originalTableName;
    }

    public int getPrecisionAdjustFactor() {
        return this.precisionAdjustFactor;
    }

    public int getSQLType() {
        return this.sqlType;
    }

    private String getStringFromBytes(int stringStart, int stringLength) throws SQLException {
        if (stringStart == -1 || stringLength == -1) {
            return null;
        }
        if (stringLength == 0) {
            return "";
        }
        MySQLConnection mySQLConnection = this.connection;
        if (mySQLConnection == null) {
            return StringUtils.toAsciiString(this.buffer, stringStart, stringLength);
        }
        if (!mySQLConnection.getUseUnicode()) {
            return StringUtils.toAsciiString(this.buffer, stringStart, stringLength);
        }
        String javaEncoding = this.connection.getCharacterSetMetadata();
        if (javaEncoding == null) {
            javaEncoding = this.connection.getEncoding();
        }
        if (javaEncoding == null) {
            return StringUtils.toAsciiString(this.buffer, stringStart, stringLength);
        }
        SingleByteCharsetConverter converter = null;
        MySQLConnection mySQLConnection2 = this.connection;
        if (mySQLConnection2 != null) {
            converter = mySQLConnection2.getCharsetConverter(javaEncoding);
        }
        if (converter != null) {
            return converter.toString(this.buffer, stringStart, stringLength);
        }
        try {
            return StringUtils.toString(this.buffer, stringStart, stringLength, javaEncoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(Messages.getString("Field.12") + javaEncoding + Messages.getString("Field.13"));
        }
    }

    public String getTable() throws SQLException {
        return getTableName();
    }

    public String getTableName() throws SQLException {
        if (this.tableName == null) {
            this.tableName = getStringFromBytes(this.tableNameStart, this.tableNameLength);
        }
        return this.tableName;
    }

    public String getTableNameNoAliases() throws SQLException {
        if (this.connection.versionMeetsMinimum(4, 1, 0)) {
            return getOriginalTableName();
        }
        return getTableName();
    }

    public boolean isAutoIncrement() {
        return (this.colFlag & 512) > 0;
    }

    public boolean isBinary() {
        return (this.colFlag & 128) > 0;
    }

    public boolean isBlob() {
        return (this.colFlag & 16) > 0;
    }

    private boolean isImplicitTemporaryTable() {
        return this.isImplicitTempTable;
    }

    public boolean isMultipleKey() {
        return (this.colFlag & 8) > 0;
    }

    /* access modifiers changed from: package-private */
    public boolean isNotNull() {
        return (this.colFlag & 1) > 0;
    }

    /* access modifiers changed from: package-private */
    public boolean isOpaqueBinary() throws SQLException {
        MySQLConnection mySQLConnection;
        if (this.collationIndex == 63 && isBinary() && (getMysqlType() == 254 || getMysqlType() == 253)) {
            if (this.originalTableNameLength != 0 || (mySQLConnection = this.connection) == null || mySQLConnection.versionMeetsMinimum(5, 0, 25)) {
                return !isImplicitTemporaryTable();
            }
            return false;
        } else if (!this.connection.versionMeetsMinimum(4, 1, 0) || !"binary".equalsIgnoreCase(getEncoding())) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isPrimaryKey() {
        return (this.colFlag & 2) > 0;
    }

    /* access modifiers changed from: package-private */
    public boolean isReadOnly() throws SQLException {
        if (!this.connection.versionMeetsMinimum(4, 1, 0)) {
            return false;
        }
        String orgColumnName = getOriginalName();
        String orgTableName = getOriginalTableName();
        if (orgColumnName == null || orgColumnName.length() <= 0 || orgTableName == null || orgTableName.length() <= 0) {
            return true;
        }
        return false;
    }

    public boolean isUniqueKey() {
        return (this.colFlag & 4) > 0;
    }

    public boolean isUnsigned() {
        return (this.colFlag & 32) > 0;
    }

    public void setUnsigned() {
        this.colFlag = (short) (this.colFlag | 32);
    }

    public boolean isZeroFill() {
        return (this.colFlag & 64) > 0;
    }

    private void setBlobTypeBasedOnLength() {
        long j = this.length;
        if (j == 255) {
            this.mysqlType = 249;
        } else if (j == 65535) {
            this.mysqlType = MysqlDefs.FIELD_TYPE_BLOB;
        } else if (j == 16777215) {
            this.mysqlType = ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION;
        } else if (j == 4294967295L) {
            this.mysqlType = 251;
        }
    }

    private boolean isNativeNumericType() {
        int i = this.mysqlType;
        return (i >= 1 && i <= 5) || i == 8 || i == 13;
    }

    private boolean isNativeDateTimeType() {
        int i = this.mysqlType;
        return i == 10 || i == 14 || i == 12 || i == 11 || i == 7;
    }

    public boolean isCharsetApplicableType() {
        int i = this.mysqlType;
        return i == 247 || i == 245 || i == 248 || i == 254 || i == 253 || i == 15;
    }

    public void setConnection(MySQLConnection conn) {
        this.connection = conn;
        if (this.encoding == null || this.collationIndex == 0) {
            this.encoding = conn.getEncoding();
        }
    }

    /* access modifiers changed from: package-private */
    public void setMysqlType(int type) {
        this.mysqlType = type;
        this.sqlType = MysqlDefs.mysqlToJavaType(type);
    }

    /* access modifiers changed from: protected */
    public void setUseOldNameMetadata(boolean useOldNameMetadata2) {
        this.useOldNameMetadata = useOldNameMetadata2;
    }

    public String toString() {
        try {
            StringBuilder asString = new StringBuilder();
            asString.append(super.toString());
            asString.append("[");
            asString.append("catalog=");
            asString.append(getDatabaseName());
            asString.append(",tableName=");
            asString.append(getTableName());
            asString.append(",originalTableName=");
            asString.append(getOriginalTableName());
            asString.append(",columnName=");
            asString.append(getName());
            asString.append(",originalColumnName=");
            asString.append(getOriginalName());
            asString.append(",mysqlType=");
            asString.append(getMysqlType());
            asString.append("(");
            asString.append(MysqlDefs.typeToName(getMysqlType()));
            asString.append(")");
            asString.append(",flags=");
            if (isAutoIncrement()) {
                asString.append(" AUTO_INCREMENT");
            }
            if (isPrimaryKey()) {
                asString.append(" PRIMARY_KEY");
            }
            if (isUniqueKey()) {
                asString.append(" UNIQUE_KEY");
            }
            if (isBinary()) {
                asString.append(" BINARY");
            }
            if (isBlob()) {
                asString.append(" BLOB");
            }
            if (isMultipleKey()) {
                asString.append(" MULTI_KEY");
            }
            if (isUnsigned()) {
                asString.append(" UNSIGNED");
            }
            if (isZeroFill()) {
                asString.append(" ZEROFILL");
            }
            asString.append(", charsetIndex=");
            asString.append(this.collationIndex);
            asString.append(", charsetName=");
            asString.append(this.encoding);
            asString.append("]");
            return asString.toString();
        } catch (Throwable th) {
            return super.toString();
        }
    }

    /* access modifiers changed from: protected */
    public boolean isSingleBit() {
        return this.isSingleBit;
    }

    /* access modifiers changed from: protected */
    public boolean getvalueNeedsQuoting() {
        return this.valueNeedsQuoting;
    }

    private boolean determineNeedsQuoting() {
        switch (this.sqlType) {
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
                return false;
            default:
                return true;
        }
    }
}
