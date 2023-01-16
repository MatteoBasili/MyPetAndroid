package com.mysql.jdbc;

import androidx.recyclerview.widget.ItemTouchHelper;
import java.sql.SQLException;

public class ResultSetMetaData implements java.sql.ResultSetMetaData {
    private ExceptionInterceptor exceptionInterceptor;
    Field[] fields;
    boolean treatYearAsDate = true;
    boolean useOldAliasBehavior = false;

    private static int clampedGetLength(Field f) {
        long fieldLength = f.getLength();
        if (fieldLength > 2147483647L) {
            fieldLength = 2147483647L;
        }
        return (int) fieldLength;
    }

    private static final boolean isDecimalType(int type) {
        switch (type) {
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
                return true;
            default:
                return false;
        }
    }

    public ResultSetMetaData(Field[] fields2, boolean useOldAliasBehavior2, boolean treatYearAsDate2, ExceptionInterceptor exceptionInterceptor2) {
        this.fields = fields2;
        this.useOldAliasBehavior = useOldAliasBehavior2;
        this.treatYearAsDate = treatYearAsDate2;
        this.exceptionInterceptor = exceptionInterceptor2;
    }

    public String getCatalogName(int column) throws SQLException {
        String database = getField(column).getDatabaseName();
        return database == null ? "" : database;
    }

    public String getColumnCharacterEncoding(int column) throws SQLException {
        String mysqlName = getColumnCharacterSet(column);
        if (mysqlName == null) {
            return null;
        }
        try {
            return CharsetMapping.getJavaEncodingForMysqlCharset(mysqlName);
        } catch (RuntimeException ex) {
            SQLException sqlEx = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
            sqlEx.initCause(ex);
            throw sqlEx;
        }
    }

    public String getColumnCharacterSet(int column) throws SQLException {
        return getField(column).getEncoding();
    }

    public String getColumnClassName(int column) throws SQLException {
        Field f = getField(column);
        return getClassNameForJavaType(f.getSQLType(), f.isUnsigned(), f.getMysqlType(), f.isBinary() || f.isBlob(), f.isOpaqueBinary(), this.treatYearAsDate);
    }

    public int getColumnCount() throws SQLException {
        return this.fields.length;
    }

    public int getColumnDisplaySize(int column) throws SQLException {
        Field f = getField(column);
        return clampedGetLength(f) / f.getMaxBytesPerCharacter();
    }

    public String getColumnLabel(int column) throws SQLException {
        if (this.useOldAliasBehavior) {
            return getColumnName(column);
        }
        return getField(column).getColumnLabel();
    }

    public String getColumnName(int column) throws SQLException {
        if (this.useOldAliasBehavior) {
            return getField(column).getName();
        }
        String name = getField(column).getNameNoAliases();
        if (name == null || name.length() != 0) {
            return name;
        }
        return getField(column).getName();
    }

    public int getColumnType(int column) throws SQLException {
        return getField(column).getSQLType();
    }

    public String getColumnTypeName(int column) throws SQLException {
        Field field = getField(column);
        int mysqlType = field.getMysqlType();
        int jdbcType = field.getSQLType();
        switch (mysqlType) {
            case 0:
            case 246:
                return field.isUnsigned() ? "DECIMAL UNSIGNED" : "DECIMAL";
            case 1:
                return field.isUnsigned() ? "TINYINT UNSIGNED" : "TINYINT";
            case 2:
                return field.isUnsigned() ? "SMALLINT UNSIGNED" : "SMALLINT";
            case 3:
                return field.isUnsigned() ? "INT UNSIGNED" : "INT";
            case 4:
                return field.isUnsigned() ? "FLOAT UNSIGNED" : "FLOAT";
            case 5:
                return field.isUnsigned() ? "DOUBLE UNSIGNED" : "DOUBLE";
            case 6:
                return "NULL";
            case 7:
                return "TIMESTAMP";
            case 8:
                return field.isUnsigned() ? "BIGINT UNSIGNED" : "BIGINT";
            case 9:
                return field.isUnsigned() ? "MEDIUMINT UNSIGNED" : "MEDIUMINT";
            case 10:
                return "DATE";
            case 11:
                return "TIME";
            case 12:
                return "DATETIME";
            case 13:
                return "YEAR";
            case 15:
                return "VARCHAR";
            case 16:
                return "BIT";
            case 245:
                return "JSON";
            case 247:
                return "ENUM";
            case 248:
                return "SET";
            case 249:
                return "TINYBLOB";
            case ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION:
                return "MEDIUMBLOB";
            case 251:
                return "LONGBLOB";
            case MysqlDefs.FIELD_TYPE_BLOB:
                if (getField(column).isBinary()) {
                    return "BLOB";
                }
                return "TEXT";
            case 253:
                if (jdbcType == -3) {
                    return "VARBINARY";
                }
                return "VARCHAR";
            case 254:
                if (jdbcType == -2) {
                    return "BINARY";
                }
                return "CHAR";
            case 255:
                return "GEOMETRY";
            default:
                return "UNKNOWN";
        }
    }

    /* access modifiers changed from: protected */
    public Field getField(int columnIndex) throws SQLException {
        if (columnIndex >= 1) {
            Field[] fieldArr = this.fields;
            if (columnIndex <= fieldArr.length) {
                return fieldArr[columnIndex - 1];
            }
        }
        throw SQLError.createSQLException(Messages.getString("ResultSetMetaData.46"), SQLError.SQL_STATE_INVALID_COLUMN_NUMBER, this.exceptionInterceptor);
    }

    public int getPrecision(int column) throws SQLException {
        Field f = getField(column);
        if (!isDecimalType(f.getSQLType())) {
            switch (f.getMysqlType()) {
                case 249:
                case ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION:
                case 251:
                case MysqlDefs.FIELD_TYPE_BLOB:
                    return clampedGetLength(f);
                default:
                    return clampedGetLength(f) / f.getMaxBytesPerCharacter();
            }
        } else if (f.getDecimals() > 0) {
            return (clampedGetLength(f) - 1) + f.getPrecisionAdjustFactor();
        } else {
            return clampedGetLength(f) + f.getPrecisionAdjustFactor();
        }
    }

    public int getScale(int column) throws SQLException {
        Field f = getField(column);
        if (isDecimalType(f.getSQLType())) {
            return f.getDecimals();
        }
        return 0;
    }

    public String getSchemaName(int column) throws SQLException {
        return "";
    }

    public String getTableName(int column) throws SQLException {
        String res = this.useOldAliasBehavior ? getField(column).getTableName() : getField(column).getTableNameNoAliases();
        return res == null ? "" : res;
    }

    public boolean isAutoIncrement(int column) throws SQLException {
        return getField(column).isAutoIncrement();
    }

    public boolean isCaseSensitive(int column) throws SQLException {
        Field field = getField(column);
        switch (field.getSQLType()) {
            case -7:
            case -6:
            case -5:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 91:
            case 92:
            case 93:
                return false;
            case -1:
            case 1:
            case 12:
                if (field.isBinary()) {
                    return true;
                }
                String collationName = field.getCollation();
                if (collationName == null || collationName.endsWith("_ci")) {
                    return false;
                }
                return true;
            default:
                return true;
        }
    }

    public boolean isCurrency(int column) throws SQLException {
        return false;
    }

    public boolean isDefinitelyWritable(int column) throws SQLException {
        return isWritable(column);
    }

    public int isNullable(int column) throws SQLException {
        if (!getField(column).isNotNull()) {
            return 1;
        }
        return 0;
    }

    public boolean isReadOnly(int column) throws SQLException {
        return getField(column).isReadOnly();
    }

    public boolean isSearchable(int column) throws SQLException {
        return true;
    }

    public boolean isSigned(int column) throws SQLException {
        Field f = getField(column);
        switch (f.getSQLType()) {
            case -6:
            case -5:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return !f.isUnsigned();
            case 91:
            case 92:
            case 93:
                return false;
            default:
                return false;
        }
    }

    public boolean isWritable(int column) throws SQLException {
        return !isReadOnly(column);
    }

    public String toString() {
        StringBuilder toStringBuf = new StringBuilder();
        toStringBuf.append(super.toString());
        toStringBuf.append(" - Field level information: ");
        for (Field field : this.fields) {
            toStringBuf.append("\n\t");
            toStringBuf.append(field.toString());
        }
        return toStringBuf.toString();
    }

    static String getClassNameForJavaType(int javaType, boolean isUnsigned, int mysqlTypeIfKnown, boolean isBinaryOrBlob, boolean isOpaqueBinary, boolean treatYearAsDate2) {
        switch (javaType) {
            case -7:
            case 16:
                return "java.lang.Boolean";
            case -6:
                return "java.lang.Integer";
            case -5:
                if (!isUnsigned) {
                    return "java.lang.Long";
                }
                return "java.math.BigInteger";
            case -4:
            case -3:
            case -2:
                if (mysqlTypeIfKnown != 255 && !isBinaryOrBlob) {
                    return "java.lang.String";
                }
                return "[B";
            case -1:
            case 1:
            case 12:
                if (!isOpaqueBinary) {
                    return "java.lang.String";
                }
                return "[B";
            case 2:
            case 3:
                return "java.math.BigDecimal";
            case 4:
                return (!isUnsigned || mysqlTypeIfKnown == 9) ? "java.lang.Integer" : "java.lang.Long";
            case 5:
                return "java.lang.Integer";
            case 6:
            case 8:
                return "java.lang.Double";
            case 7:
                return "java.lang.Float";
            case 91:
                return (treatYearAsDate2 || mysqlTypeIfKnown != 13) ? "java.sql.Date" : "java.lang.Short";
            case 92:
                return "java.sql.Time";
            case 93:
                return "java.sql.Timestamp";
            default:
                return "java.lang.Object";
        }
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        try {
            return iface.cast(this);
        } catch (ClassCastException e) {
            throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        }
    }
}
