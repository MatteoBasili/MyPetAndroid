package com.mysql.jdbc;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.ViewCompat;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

public class DatabaseMetaData implements java.sql.DatabaseMetaData {
    private static final int DEFERRABILITY = 13;
    private static final int DELETE_RULE = 10;
    private static final int FKCOLUMN_NAME = 7;
    private static final int FKTABLE_CAT = 4;
    private static final int FKTABLE_NAME = 6;
    private static final int FKTABLE_SCHEM = 5;
    private static final int FK_NAME = 11;
    private static final Constructor<?> JDBC_4_DBMD_IS_CTOR;
    private static final Constructor<?> JDBC_4_DBMD_SHOW_CTOR;
    private static final int KEY_SEQ = 8;
    protected static final int MAX_IDENTIFIER_LENGTH = 64;
    private static final String[] MYSQL_KEYWORDS = {"ACCESSIBLE", "ADD", "ALL", "ALTER", "ANALYZE", "AND", "ARRAY", "AS", "ASC", "ASENSITIVE", "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE", "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION", "CONSTRAINT", "CONTINUE", "CONVERT", "CREATE", "CROSS", "CUBE", "CUME_DIST", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE", "DATABASES", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DENSE_RANK", "DESC", "DESCRIBE", "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE", "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "EMPTY", "ENCLOSED", "ESCAPED", "EXCEPT", "EXISTS", "EXIT", "EXPLAIN", "FALSE", "FETCH", "FIRST_VALUE", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "FUNCTION", "GENERATED", "GET", "GRANT", "GROUP", "GROUPING", "GROUPS", "HAVING", "HIGH_PRIORITY", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IN", "INDEX", "INFILE", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERVAL", "INTO", "IO_AFTER_GTIDS", "IO_BEFORE_GTIDS", "IS", "ITERATE", "JOIN", "JSON_TABLE", "KEY", "KEYS", "KILL", "LAG", "LAST_VALUE", "LATERAL", "LEAD", "LEADING", "LEAVE", "LEFT", "LIKE", "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY", "MASTER_BIND", "MASTER_SSL_VERIFY_SERVER_CERT", "MATCH", "MAXVALUE", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MEMBER", "MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", "MODIFIES", "NATURAL", "NOT", "NO_WRITE_TO_BINLOG", "NTH_VALUE", "NTILE", "NULL", "NUMERIC", "OF", "ON", "OPTIMIZE", "OPTIMIZER_COSTS", "OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", "OVER", "PARTITION", "PERCENT_RANK", "PRECISION", "PRIMARY", "PROCEDURE", "PURGE", "RANGE", "RANK", "READ", "READS", "READ_WRITE", "REAL", "RECURSIVE", "REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPEAT", "REPLACE", "REQUIRE", "RESIGNAL", "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", "ROW", "ROWS", "ROW_NUMBER", "SCHEMA", "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", "SIGNAL", "SMALLINT", "SPATIAL", "SPECIFIC", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", "SSL", "STARTING", "STORED", "STRAIGHT_JOIN", "SYSTEM", "TABLE", "TERMINATED", "THEN", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE", "USAGE", "USE", "USING", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", "VIRTUAL", "WHEN", "WHERE", "WHILE", "WINDOW", "WITH", "WRITE", "XOR", "YEAR_MONTH", "ZEROFILL"};
    private static final int PKCOLUMN_NAME = 3;
    private static final int PKTABLE_CAT = 0;
    private static final int PKTABLE_NAME = 2;
    private static final int PKTABLE_SCHEM = 1;
    private static final int PK_NAME = 12;
    private static final String[] SQL2003_KEYWORDS = {"ABS", "ALL", "ALLOCATE", "ALTER", "AND", "ANY", "ARE", "ARRAY", "AS", "ASENSITIVE", "ASYMMETRIC", "AT", "ATOMIC", "AUTHORIZATION", "AVG", "BEGIN", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOOLEAN", "BOTH", "BY", "CALL", "CALLED", "CARDINALITY", "CASCADED", "CASE", "CAST", "CEIL", "CEILING", "CHAR", "CHARACTER", "CHARACTER_LENGTH", "CHAR_LENGTH", "CHECK", "CLOB", "CLOSE", "COALESCE", "COLLATE", "COLLECT", "COLUMN", "COMMIT", "CONDITION", "CONNECT", "CONSTRAINT", "CONVERT", "CORR", "CORRESPONDING", "COUNT", "COVAR_POP", "COVAR_SAMP", "CREATE", "CROSS", "CUBE", "CUME_DIST", "CURRENT", "CURRENT_DATE", "CURRENT_DEFAULT_TRANSFORM_GROUP", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_TRANSFORM_GROUP_FOR_TYPE", "CURRENT_USER", "CURSOR", "CYCLE", "DATE", "DAY", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELETE", "DENSE_RANK", "DEREF", "DESCRIBE", "DETERMINISTIC", "DISCONNECT", "DISTINCT", "DOUBLE", "DROP", "DYNAMIC", "EACH", "ELEMENT", "ELSE", "END", "END-EXEC", "ESCAPE", "EVERY", "EXCEPT", "EXEC", "EXECUTE", "EXISTS", "EXP", "EXTERNAL", "EXTRACT", "FALSE", "FETCH", "FILTER", "FLOAT", "FLOOR", "FOR", "FOREIGN", "FREE", "FROM", "FULL", "FUNCTION", "FUSION", "GET", "GLOBAL", "GRANT", "GROUP", "GROUPING", "HAVING", "HOLD", "HOUR", "IDENTITY", "IN", "INDICATOR", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INTEGER", "INTERSECT", "INTERSECTION", "INTERVAL", "INTO", "IS", "JOIN", "LANGUAGE", "LARGE", "LATERAL", "LEADING", "LEFT", "LIKE", "LN", "LOCAL", "LOCALTIME", "LOCALTIMESTAMP", "LOWER", "MATCH", "MAX", "MEMBER", "MERGE", "METHOD", "MIN", "MINUTE", "MOD", "MODIFIES", "MODULE", "MONTH", "MULTISET", "NATIONAL", "NATURAL", "NCHAR", "NCLOB", "NEW", "NO", "NONE", "NORMALIZE", "NOT", "NULL", "NULLIF", "NUMERIC", "OCTET_LENGTH", "OF", "OLD", "ON", "ONLY", "OPEN", "OR", "ORDER", "OUT", "OUTER", "OVER", "OVERLAPS", "OVERLAY", "PARAMETER", "PARTITION", "PERCENTILE_CONT", "PERCENTILE_DISC", "PERCENT_RANK", "POSITION", "POWER", "PRECISION", "PREPARE", "PRIMARY", "PROCEDURE", "RANGE", "RANK", "READS", "REAL", "RECURSIVE", "REF", "REFERENCES", "REFERENCING", "REGR_AVGX", "REGR_AVGY", "REGR_COUNT", "REGR_INTERCEPT", "REGR_R2", "REGR_SLOPE", "REGR_SXX", "REGR_SXY", "REGR_SYY", "RELEASE", "RESULT", "RETURN", "RETURNS", "REVOKE", "RIGHT", "ROLLBACK", "ROLLUP", "ROW", "ROWS", "ROW_NUMBER", "SAVEPOINT", "SCOPE", "SCROLL", "SEARCH", "SECOND", "SELECT", "SENSITIVE", "SESSION_USER", "SET", "SIMILAR", "SMALLINT", "SOME", "SPECIFIC", "SPECIFICTYPE", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQRT", "START", "STATIC", "STDDEV_POP", "STDDEV_SAMP", "SUBMULTISET", "SUBSTRING", "SUM", "SYMMETRIC", "SYSTEM", "SYSTEM_USER", "TABLE", "TABLESAMPLE", "THEN", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRAILING", "TRANSLATE", "TRANSLATION", "TREAT", "TRIGGER", "TRIM", "TRUE", "UESCAPE", "UNION", "UNIQUE", "UNKNOWN", "UNNEST", "UPDATE", "UPPER", "USER", "USING", "VALUE", "VALUES", "VARCHAR", "VARYING", "VAR_POP", "VAR_SAMP", "WHEN", "WHENEVER", "WHERE", "WIDTH_BUCKET", "WINDOW", "WITH", "WITHIN", "WITHOUT", "YEAR"};
    private static final String[] SQL92_KEYWORDS = {"ABSOLUTE", "ACTION", "ADD", "ALL", "ALLOCATE", "ALTER", "AND", "ANY", "ARE", "AS", "ASC", "ASSERTION", "AT", "AUTHORIZATION", "AVG", "BEGIN", "BETWEEN", "BIT", "BIT_LENGTH", "BOTH", "BY", "CASCADE", "CASCADED", "CASE", "CAST", "CATALOG", "CHAR", "CHARACTER", "CHARACTER_LENGTH", "CHAR_LENGTH", "CHECK", "CLOSE", "COALESCE", "COLLATE", "COLLATION", "COLUMN", "COMMIT", "CONNECT", "CONNECTION", "CONSTRAINT", "CONSTRAINTS", "CONTINUE", "CONVERT", "CORRESPONDING", "COUNT", "CREATE", "CROSS", "CURRENT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATE", "DAY", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DEFERRABLE", "DEFERRED", "DELETE", "DESC", "DESCRIBE", "DESCRIPTOR", "DIAGNOSTICS", "DISCONNECT", "DISTINCT", "DOMAIN", "DOUBLE", "DROP", "ELSE", "END", "END-EXEC", "ESCAPE", "EXCEPT", "EXCEPTION", "EXEC", "EXECUTE", "EXISTS", "EXTERNAL", "EXTRACT", "FALSE", "FETCH", "FIRST", "FLOAT", "FOR", "FOREIGN", "FOUND", "FROM", "FULL", "GET", "GLOBAL", "GO", "GOTO", "GRANT", "GROUP", "HAVING", "HOUR", "IDENTITY", "IMMEDIATE", "IN", "INDICATOR", "INITIALLY", "INNER", "INPUT", "INSENSITIVE", "INSERT", "INT", "INTEGER", "INTERSECT", "INTERVAL", "INTO", "IS", "ISOLATION", "JOIN", "KEY", "LANGUAGE", "LAST", "LEADING", "LEFT", "LEVEL", "LIKE", "LOCAL", "LOWER", "MATCH", "MAX", "MIN", "MINUTE", "MODULE", "MONTH", "NAMES", "NATIONAL", "NATURAL", "NCHAR", "NEXT", "NO", "NOT", "NULL", "NULLIF", "NUMERIC", "OCTET_LENGTH", "OF", "ON", "ONLY", "OPEN", "OPTION", "OR", "ORDER", "OUTER", "OUTPUT", "OVERLAPS", "PAD", "PARTIAL", "POSITION", "PRECISION", "PREPARE", "PRESERVE", "PRIMARY", "PRIOR", "PRIVILEGES", "PROCEDURE", "PUBLIC", "READ", "REAL", "REFERENCES", "RELATIVE", "RESTRICT", "REVOKE", "RIGHT", "ROLLBACK", "ROWS", "SCHEMA", "SCROLL", "SECOND", "SECTION", "SELECT", "SESSION", "SESSION_USER", "SET", "SIZE", "SMALLINT", "SOME", "SPACE", "SQL", "SQLCODE", "SQLERROR", "SQLSTATE", "SUBSTRING", "SUM", "SYSTEM_USER", "TABLE", "TEMPORARY", "THEN", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRAILING", "TRANSACTION", "TRANSLATE", "TRANSLATION", "TRIM", "TRUE", "UNION", "UNIQUE", "UNKNOWN", "UPDATE", "UPPER", "USAGE", "USER", "USING", "VALUE", "VALUES", "VARCHAR", "VARYING", "VIEW", "WHEN", "WHENEVER", "WHERE", "WITH", "WORK", "WRITE", "YEAR", "ZONE"};
    private static final String SUPPORTS_FK = "SUPPORTS_FK";
    protected static final byte[] SYSTEM_TABLE_AS_BYTES = "SYSTEM TABLE".getBytes();
    protected static final byte[] TABLE_AS_BYTES = "TABLE".getBytes();
    private static final int UPDATE_RULE = 9;
    protected static final byte[] VIEW_AS_BYTES = "VIEW".getBytes();
    private static volatile String mysqlKeywords = null;
    protected MySQLConnection conn;
    protected String database = null;
    private ExceptionInterceptor exceptionInterceptor;
    protected final String quotedId;

    protected enum ProcedureType {
        PROCEDURE,
        FUNCTION
    }

    protected abstract class IteratorWithCleanup<T> {
        /* access modifiers changed from: package-private */
        public abstract void close() throws SQLException;

        /* access modifiers changed from: package-private */
        public abstract boolean hasNext() throws SQLException;

        /* access modifiers changed from: package-private */
        public abstract T next() throws SQLException;

        protected IteratorWithCleanup() {
        }
    }

    class LocalAndReferencedColumns {
        String constraintName;
        List<String> localColumnsList;
        String referencedCatalog;
        List<String> referencedColumnsList;
        String referencedTable;

        LocalAndReferencedColumns(List<String> localColumns, List<String> refColumns, String constName, String refCatalog, String refTable) {
            this.localColumnsList = localColumns;
            this.referencedColumnsList = refColumns;
            this.constraintName = constName;
            this.referencedTable = refTable;
            this.referencedCatalog = refCatalog;
        }
    }

    protected class ResultSetIterator extends IteratorWithCleanup<String> {
        int colIndex;
        ResultSet resultSet;

        ResultSetIterator(ResultSet rs, int index) {
            super();
            this.resultSet = rs;
            this.colIndex = index;
        }

        /* access modifiers changed from: package-private */
        public void close() throws SQLException {
            this.resultSet.close();
        }

        /* access modifiers changed from: package-private */
        public boolean hasNext() throws SQLException {
            return this.resultSet.next();
        }

        /* access modifiers changed from: package-private */
        public String next() throws SQLException {
            return this.resultSet.getObject(this.colIndex).toString();
        }
    }

    protected class SingleStringIterator extends IteratorWithCleanup<String> {
        boolean onFirst = true;
        String value;

        SingleStringIterator(String s) {
            super();
            this.value = s;
        }

        /* access modifiers changed from: package-private */
        public void close() throws SQLException {
        }

        /* access modifiers changed from: package-private */
        public boolean hasNext() throws SQLException {
            return this.onFirst;
        }

        /* access modifiers changed from: package-private */
        public String next() throws SQLException {
            this.onFirst = false;
            return this.value;
        }
    }

    class TypeDescriptor {
        int bufferLength;
        int charOctetLength;
        Integer columnSize;
        short dataType;
        Integer decimalDigits;
        String isNullable;
        int nullability;
        int numPrecRadix = 10;
        final /* synthetic */ DatabaseMetaData this$0;
        String typeName;

        TypeDescriptor(DatabaseMetaData databaseMetaData, String typeInfo, String nullabilityInfo) throws SQLException {
            String mysqlType;
            String fullMysqlType;
            DatabaseMetaData databaseMetaData2 = databaseMetaData;
            String str = typeInfo;
            String str2 = nullabilityInfo;
            this.this$0 = databaseMetaData2;
            if (str != null) {
                if (str.indexOf("(") != -1) {
                    mysqlType = str.substring(0, str.indexOf("(")).trim();
                } else {
                    mysqlType = typeInfo;
                }
                int indexOfUnsignedInMysqlType = StringUtils.indexOfIgnoreCase(mysqlType, "unsigned");
                mysqlType = indexOfUnsignedInMysqlType != -1 ? mysqlType.substring(0, indexOfUnsignedInMysqlType - 1) : mysqlType;
                boolean isUnsigned = false;
                if (StringUtils.indexOfIgnoreCase(str, "unsigned") == -1 || StringUtils.indexOfIgnoreCase(str, "set") == 0 || StringUtils.indexOfIgnoreCase(str, "enum") == 0) {
                    fullMysqlType = mysqlType;
                } else {
                    fullMysqlType = mysqlType + " unsigned";
                    isUnsigned = true;
                }
                fullMysqlType = databaseMetaData2.conn.getCapitalizeTypeNames() ? fullMysqlType.toUpperCase(Locale.ENGLISH) : fullMysqlType;
                this.dataType = (short) MysqlDefs.mysqlToJavaType(mysqlType);
                this.typeName = fullMysqlType;
                if (StringUtils.startsWithIgnoreCase(str, "enum")) {
                    StringTokenizer tokenizer = new StringTokenizer(str.substring(str.indexOf("("), str.lastIndexOf(")")), ",");
                    int maxLength = 0;
                    while (tokenizer.hasMoreTokens()) {
                        maxLength = Math.max(maxLength, tokenizer.nextToken().length() - 2);
                    }
                    this.columnSize = Integer.valueOf(maxLength);
                    this.decimalDigits = null;
                    String str3 = fullMysqlType;
                    String str4 = mysqlType;
                    int i = indexOfUnsignedInMysqlType;
                } else if (StringUtils.startsWithIgnoreCase(str, "set")) {
                    StringTokenizer tokenizer2 = new StringTokenizer(str.substring(str.indexOf("(") + 1, str.lastIndexOf(")")), ",");
                    int maxLength2 = 0;
                    int numElements = tokenizer2.countTokens();
                    int maxLength3 = numElements > 0 ? 0 + (numElements - 1) : maxLength2;
                    while (tokenizer2.hasMoreTokens()) {
                        String setMember = tokenizer2.nextToken().trim();
                        if (!setMember.startsWith("'") || !setMember.endsWith("'")) {
                            maxLength3 += setMember.length();
                        } else {
                            maxLength3 += setMember.length() - 2;
                        }
                    }
                    this.columnSize = Integer.valueOf(maxLength3);
                    this.decimalDigits = null;
                    String str5 = fullMysqlType;
                    String str6 = mysqlType;
                    int i2 = indexOfUnsignedInMysqlType;
                } else if (str.indexOf(",") != -1) {
                    this.columnSize = Integer.valueOf(str.substring(str.indexOf("(") + 1, str.indexOf(",")).trim());
                    this.decimalDigits = Integer.valueOf(str.substring(str.indexOf(",") + 1, str.indexOf(")")).trim());
                    String str7 = fullMysqlType;
                    String str8 = mysqlType;
                    int i3 = indexOfUnsignedInMysqlType;
                } else {
                    this.columnSize = null;
                    this.decimalDigits = null;
                    String str9 = fullMysqlType;
                    String str10 = mysqlType;
                    int i4 = indexOfUnsignedInMysqlType;
                    if ((StringUtils.indexOfIgnoreCase(str, "char") != -1 || StringUtils.indexOfIgnoreCase(str, "text") != -1 || StringUtils.indexOfIgnoreCase(str, "blob") != -1 || StringUtils.indexOfIgnoreCase(str, "binary") != -1 || StringUtils.indexOfIgnoreCase(str, "bit") != -1) && str.indexOf("(") != -1) {
                        int endParenIndex = str.indexOf(")");
                        this.columnSize = Integer.valueOf(str.substring(str.indexOf("(") + 1, endParenIndex == -1 ? typeInfo.length() : endParenIndex).trim());
                        if (databaseMetaData2.conn.getTinyInt1isBit() && this.columnSize.intValue() == 1 && StringUtils.startsWithIgnoreCase(str, 0, "tinyint")) {
                            if (databaseMetaData2.conn.getTransformedBitIsBoolean()) {
                                this.dataType = 16;
                                this.typeName = "BOOLEAN";
                            } else {
                                this.dataType = -7;
                                this.typeName = "BIT";
                            }
                        }
                    } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "tinyint")) {
                        if (!databaseMetaData2.conn.getTinyInt1isBit() || str.indexOf("(1)") == -1) {
                            this.columnSize = 3;
                            this.decimalDigits = 0;
                        } else if (databaseMetaData2.conn.getTransformedBitIsBoolean()) {
                            this.dataType = 16;
                            this.typeName = "BOOLEAN";
                        } else {
                            this.dataType = -7;
                            this.typeName = "BIT";
                        }
                    } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "smallint")) {
                        this.columnSize = 5;
                        this.decimalDigits = 0;
                    } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "mediumint")) {
                        this.columnSize = Integer.valueOf(isUnsigned ? 8 : 7);
                        this.decimalDigits = 0;
                    } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "int")) {
                        this.columnSize = 10;
                        this.decimalDigits = 0;
                    } else if (StringUtils.startsWithIgnoreCaseAndWs(str, TypedValues.Custom.S_INT)) {
                        this.columnSize = 10;
                        this.decimalDigits = 0;
                    } else {
                        int i5 = 19;
                        if (StringUtils.startsWithIgnoreCaseAndWs(str, "bigint")) {
                            this.columnSize = Integer.valueOf(isUnsigned ? 20 : i5);
                            this.decimalDigits = 0;
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "int24")) {
                            this.columnSize = 19;
                            this.decimalDigits = 0;
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "real")) {
                            this.columnSize = 12;
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, TypedValues.Custom.S_FLOAT)) {
                            this.columnSize = 12;
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "decimal")) {
                            this.columnSize = 12;
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "numeric")) {
                            this.columnSize = 12;
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "double")) {
                            this.columnSize = 22;
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "char")) {
                            this.columnSize = 1;
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "varchar")) {
                            this.columnSize = 255;
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "timestamp")) {
                            this.columnSize = 19;
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "datetime")) {
                            this.columnSize = 19;
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "date")) {
                            this.columnSize = 10;
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "time")) {
                            this.columnSize = 8;
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "tinyblob")) {
                            this.columnSize = 255;
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "blob")) {
                            this.columnSize = Integer.valueOf(SupportMenu.USER_MASK);
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "mediumblob")) {
                            this.columnSize = Integer.valueOf(ViewCompat.MEASURED_SIZE_MASK);
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "longblob")) {
                            this.columnSize = Integer.MAX_VALUE;
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "tinytext")) {
                            this.columnSize = 255;
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "text")) {
                            this.columnSize = Integer.valueOf(SupportMenu.USER_MASK);
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "mediumtext")) {
                            this.columnSize = Integer.valueOf(ViewCompat.MEASURED_SIZE_MASK);
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "longtext")) {
                            this.columnSize = Integer.MAX_VALUE;
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "enum")) {
                            this.columnSize = 255;
                        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "set")) {
                            this.columnSize = 255;
                        }
                    }
                }
                this.bufferLength = MysqlIO.getMaxBuf();
                this.numPrecRadix = 10;
                if (str2 == null) {
                    this.nullability = 0;
                    this.isNullable = "NO";
                } else if (str2.equals("YES")) {
                    this.nullability = 1;
                    this.isNullable = "YES";
                } else if (str2.equals("UNKNOWN")) {
                    this.nullability = 2;
                    this.isNullable = "";
                } else {
                    this.nullability = 0;
                    this.isNullable = "NO";
                }
            } else {
                throw SQLError.createSQLException("NULL typeinfo not supported.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, databaseMetaData.getExceptionInterceptor());
            }
        }
    }

    protected class IndexMetaDataKey implements Comparable<IndexMetaDataKey> {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        String columnIndexName;
        Boolean columnNonUnique;
        Short columnOrdinalPosition;
        Short columnType;

        static {
            Class<DatabaseMetaData> cls = DatabaseMetaData.class;
        }

        IndexMetaDataKey(boolean columnNonUnique2, short columnType2, String columnIndexName2, short columnOrdinalPosition2) {
            this.columnNonUnique = Boolean.valueOf(columnNonUnique2);
            this.columnType = Short.valueOf(columnType2);
            this.columnIndexName = columnIndexName2;
            this.columnOrdinalPosition = Short.valueOf(columnOrdinalPosition2);
        }

        public int compareTo(IndexMetaDataKey indexInfoKey) {
            int compareTo = this.columnNonUnique.compareTo(indexInfoKey.columnNonUnique);
            int compareResult = compareTo;
            if (compareTo != 0) {
                return compareResult;
            }
            int compareTo2 = this.columnType.compareTo(indexInfoKey.columnType);
            int compareResult2 = compareTo2;
            if (compareTo2 != 0) {
                return compareResult2;
            }
            int compareTo3 = this.columnIndexName.compareTo(indexInfoKey.columnIndexName);
            int compareResult3 = compareTo3;
            if (compareTo3 != 0) {
                return compareResult3;
            }
            return this.columnOrdinalPosition.compareTo(indexInfoKey.columnOrdinalPosition);
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if ((obj instanceof IndexMetaDataKey) && compareTo((IndexMetaDataKey) obj) == 0) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            throw new AssertionError("hashCode not designed");
        }
    }

    protected class TableMetaDataKey implements Comparable<TableMetaDataKey> {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        String tableCat;
        String tableName;
        String tableSchem;
        String tableType;

        static {
            Class<DatabaseMetaData> cls = DatabaseMetaData.class;
        }

        TableMetaDataKey(String tableType2, String tableCat2, String tableSchem2, String tableName2) {
            String str = "";
            this.tableType = tableType2 == null ? str : tableType2;
            this.tableCat = tableCat2 == null ? str : tableCat2;
            this.tableSchem = tableSchem2 == null ? str : tableSchem2;
            this.tableName = tableName2 != null ? tableName2 : str;
        }

        public int compareTo(TableMetaDataKey tablesKey) {
            int compareTo = this.tableType.compareTo(tablesKey.tableType);
            int compareResult = compareTo;
            if (compareTo != 0) {
                return compareResult;
            }
            int compareTo2 = this.tableCat.compareTo(tablesKey.tableCat);
            int compareResult2 = compareTo2;
            if (compareTo2 != 0) {
                return compareResult2;
            }
            int compareTo3 = this.tableSchem.compareTo(tablesKey.tableSchem);
            int compareResult3 = compareTo3;
            if (compareTo3 != 0) {
                return compareResult3;
            }
            return this.tableName.compareTo(tablesKey.tableName);
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if ((obj instanceof TableMetaDataKey) && compareTo((TableMetaDataKey) obj) == 0) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            throw new AssertionError("hashCode not designed");
        }
    }

    protected class ComparableWrapper<K extends Comparable<? super K>, V> implements Comparable<ComparableWrapper<K, V>> {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        K key;
        V value;

        static {
            Class<DatabaseMetaData> cls = DatabaseMetaData.class;
        }

        public ComparableWrapper(K key2, V value2) {
            this.key = key2;
            this.value = value2;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        public int compareTo(ComparableWrapper<K, V> other) {
            return ((Comparable) getKey()).compareTo(other.getKey());
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof ComparableWrapper)) {
                return false;
            }
            return this.key.equals(((ComparableWrapper) obj).getKey());
        }

        public int hashCode() {
            throw new AssertionError("hashCode not designed");
        }

        public String toString() {
            return "{KEY:" + this.key + "; VALUE:" + this.value + "}";
        }
    }

    protected enum TableType {
        LOCAL_TEMPORARY("LOCAL TEMPORARY"),
        SYSTEM_TABLE("SYSTEM TABLE"),
        SYSTEM_VIEW("SYSTEM VIEW"),
        TABLE("TABLE", new String[]{"BASE TABLE"}),
        VIEW("VIEW"),
        UNKNOWN("UNKNOWN");
        
        private String name;
        private byte[] nameAsBytes;
        private String[] synonyms;

        private TableType(String tableTypeName) {
            this(r2, r3, tableTypeName, (String[]) null);
        }

        private TableType(String tableTypeName, String[] tableTypeSynonyms) {
            this.name = tableTypeName;
            this.nameAsBytes = tableTypeName.getBytes();
            this.synonyms = tableTypeSynonyms;
        }

        /* access modifiers changed from: package-private */
        public String getName() {
            return this.name;
        }

        /* access modifiers changed from: package-private */
        public byte[] asBytes() {
            return this.nameAsBytes;
        }

        /* access modifiers changed from: package-private */
        public boolean equalsTo(String tableTypeName) {
            return this.name.equalsIgnoreCase(tableTypeName);
        }

        static TableType getTableTypeEqualTo(String tableTypeName) {
            for (TableType tableType : values()) {
                if (tableType.equalsTo(tableTypeName)) {
                    return tableType;
                }
            }
            return UNKNOWN;
        }

        /* access modifiers changed from: package-private */
        public boolean compliesWith(String tableTypeName) {
            if (equalsTo(tableTypeName)) {
                return true;
            }
            if (this.synonyms == null) {
                return false;
            }
            for (String synonym : this.synonyms) {
                if (synonym.equalsIgnoreCase(tableTypeName)) {
                    return true;
                }
            }
            return false;
        }

        static TableType getTableTypeCompliantWith(String tableTypeName) {
            for (TableType tableType : values()) {
                if (tableType.compliesWith(tableTypeName)) {
                    return tableType;
                }
            }
            return UNKNOWN;
        }
    }

    static {
        if (Util.isJdbc4()) {
            try {
                JDBC_4_DBMD_SHOW_CTOR = Class.forName("com.mysql.jdbc.JDBC4DatabaseMetaData").getConstructor(new Class[]{MySQLConnection.class, String.class});
                JDBC_4_DBMD_IS_CTOR = Class.forName("com.mysql.jdbc.JDBC4DatabaseMetaDataUsingInfoSchema").getConstructor(new Class[]{MySQLConnection.class, String.class});
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e2) {
                throw new RuntimeException(e2);
            } catch (ClassNotFoundException e3) {
                throw new RuntimeException(e3);
            }
        } else {
            JDBC_4_DBMD_IS_CTOR = null;
            JDBC_4_DBMD_SHOW_CTOR = null;
        }
    }

    protected static DatabaseMetaData getInstance(MySQLConnection connToSet, String databaseToSet, boolean checkForInfoSchema) throws SQLException {
        if (!Util.isJdbc4()) {
            if (!checkForInfoSchema || !connToSet.getUseInformationSchema() || !connToSet.versionMeetsMinimum(5, 0, 7)) {
                return new DatabaseMetaData(connToSet, databaseToSet);
            }
            return new DatabaseMetaDataUsingInfoSchema(connToSet, databaseToSet);
        } else if (!checkForInfoSchema || !connToSet.getUseInformationSchema() || !connToSet.versionMeetsMinimum(5, 0, 7)) {
            return (DatabaseMetaData) Util.handleNewInstance(JDBC_4_DBMD_SHOW_CTOR, new Object[]{connToSet, databaseToSet}, connToSet.getExceptionInterceptor());
        } else {
            return (DatabaseMetaData) Util.handleNewInstance(JDBC_4_DBMD_IS_CTOR, new Object[]{connToSet, databaseToSet}, connToSet.getExceptionInterceptor());
        }
    }

    protected DatabaseMetaData(MySQLConnection connToSet, String databaseToSet) {
        this.conn = connToSet;
        this.database = databaseToSet;
        this.exceptionInterceptor = connToSet.getExceptionInterceptor();
        String identifierQuote = null;
        try {
            identifierQuote = getIdentifierQuoteString();
        } catch (SQLException sqlEx) {
            AssertionFailedException.shouldNotHappen(sqlEx);
        } catch (Throwable th) {
            this.quotedId = null;
            throw th;
        }
        this.quotedId = identifierQuote;
    }

    public boolean allProceduresAreCallable() throws SQLException {
        return false;
    }

    public boolean allTablesAreSelectable() throws SQLException {
        return false;
    }

    private ResultSet buildResultSet(Field[] fields, ArrayList<ResultSetRow> rows) throws SQLException {
        return buildResultSet(fields, rows, this.conn);
    }

    static ResultSet buildResultSet(Field[] fields, ArrayList<ResultSetRow> rows, MySQLConnection c) throws SQLException {
        int fieldsLength = fields.length;
        for (int i = 0; i < fieldsLength; i++) {
            switch (fields[i].getSQLType()) {
                case -1:
                case 1:
                case 12:
                    fields[i].setEncoding(c.getCharacterSetMetadata(), c);
                    break;
            }
            fields[i].setConnection(c);
            fields[i].setUseOldNameMetadata(true);
        }
        return ResultSetImpl.getInstance(c.getCatalog(), fields, new RowDataStatic(rows), c, (StatementImpl) null, false);
    }

    /* access modifiers changed from: protected */
    public void convertToJdbcFunctionList(String catalog, ResultSet proceduresRs, boolean needsClientFiltering, String db, List<ComparableWrapper<String, ResultSetRow>> procedureRows, int nameIndex, Field[] fields) throws SQLException {
        byte[][] rowData;
        String str = catalog;
        ResultSet resultSet = proceduresRs;
        String str2 = db;
        Field[] fieldArr = fields;
        while (proceduresRs.next()) {
            boolean shouldAdd = true;
            if (needsClientFiltering) {
                shouldAdd = false;
                String procDb = resultSet.getString(1);
                if (str2 == null && procDb == null) {
                    shouldAdd = true;
                } else if (str2 != null && str2.equals(procDb)) {
                    shouldAdd = true;
                }
            }
            if (shouldAdd) {
                String functionName = resultSet.getString(nameIndex);
                byte[][] bArr = null;
                if (fieldArr == null || fieldArr.length != 9) {
                    rowData = new byte[6][];
                    rowData[0] = str == null ? null : s2b(catalog);
                    rowData[1] = null;
                    rowData[2] = s2b(functionName);
                    rowData[3] = s2b(resultSet.getString("comment"));
                    rowData[4] = s2b(Integer.toString(getJDBC4FunctionNoTableConstant()));
                    rowData[5] = s2b(functionName);
                } else {
                    rowData = new byte[9][];
                    rowData[0] = str == null ? null : s2b(catalog);
                    rowData[1] = null;
                    rowData[2] = s2b(functionName);
                    rowData[3] = null;
                    rowData[4] = null;
                    rowData[5] = null;
                    rowData[6] = s2b(resultSet.getString("comment"));
                    rowData[7] = s2b(Integer.toString(2));
                    rowData[8] = s2b(functionName);
                }
                procedureRows.add(new ComparableWrapper(getFullyQualifiedName(str, functionName), new ByteArrayRow(rowData, getExceptionInterceptor())));
            } else {
                List<ComparableWrapper<String, ResultSetRow>> list = procedureRows;
                int i = nameIndex;
            }
        }
        List<ComparableWrapper<String, ResultSetRow>> list2 = procedureRows;
        int i2 = nameIndex;
    }

    /* access modifiers changed from: protected */
    public String getFullyQualifiedName(String catalog, String entity) {
        return StringUtils.quoteIdentifier(catalog == null ? "" : catalog, this.quotedId, this.conn.getPedantic()) + '.' + StringUtils.quoteIdentifier(entity, this.quotedId, this.conn.getPedantic());
    }

    /* access modifiers changed from: protected */
    public int getJDBC4FunctionNoTableConstant() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public void convertToJdbcProcedureList(boolean fromSelect, String catalog, ResultSet proceduresRs, boolean needsClientFiltering, String db, List<ComparableWrapper<String, ResultSetRow>> procedureRows, int nameIndex) throws SQLException {
        while (proceduresRs.next()) {
            boolean shouldAdd = true;
            if (needsClientFiltering) {
                shouldAdd = false;
                String procDb = proceduresRs.getString(1);
                if (db == null && procDb == null) {
                    shouldAdd = true;
                } else if (db != null && db.equals(procDb)) {
                    shouldAdd = true;
                }
            }
            if (shouldAdd) {
                String procedureName = proceduresRs.getString(nameIndex);
                byte[][] rowData = new byte[9][];
                boolean isFunction = false;
                rowData[0] = catalog == null ? null : s2b(catalog);
                rowData[1] = null;
                rowData[2] = s2b(procedureName);
                rowData[3] = null;
                rowData[4] = null;
                rowData[5] = null;
                rowData[6] = s2b(proceduresRs.getString("comment"));
                if (fromSelect) {
                    isFunction = "FUNCTION".equalsIgnoreCase(proceduresRs.getString("type"));
                }
                rowData[7] = s2b(isFunction ? Integer.toString(2) : Integer.toString(1));
                rowData[8] = s2b(procedureName);
                procedureRows.add(new ComparableWrapper(getFullyQualifiedName(catalog, procedureName), new ByteArrayRow(rowData, getExceptionInterceptor())));
            }
        }
    }

    private ResultSetRow convertTypeDescriptorToProcedureRow(byte[] procNameAsBytes, byte[] procCatAsBytes, String paramName, boolean isOutParam, boolean isInParam, boolean isReturnParam, TypeDescriptor typeDesc, boolean forGetFunctionColumns, int ordinal) throws SQLException {
        TypeDescriptor typeDescriptor = typeDesc;
        boolean z = forGetFunctionColumns;
        byte[][] row = z ? new byte[17][] : new byte[20][];
        row[0] = procCatAsBytes;
        row[1] = null;
        row[2] = procNameAsBytes;
        row[3] = s2b(paramName);
        row[4] = s2b(String.valueOf(getColumnType(isOutParam, isInParam, isReturnParam, z)));
        row[5] = s2b(Short.toString(typeDescriptor.dataType));
        row[6] = s2b(typeDescriptor.typeName);
        row[7] = typeDescriptor.columnSize == null ? null : s2b(typeDescriptor.columnSize.toString());
        row[8] = row[7];
        row[9] = typeDescriptor.decimalDigits == null ? null : s2b(typeDescriptor.decimalDigits.toString());
        row[10] = s2b(Integer.toString(typeDescriptor.numPrecRadix));
        switch (typeDescriptor.nullability) {
            case 0:
                row[11] = s2b(String.valueOf(0));
                break;
            case 1:
                row[11] = s2b(String.valueOf(1));
                break;
            case 2:
                row[11] = s2b(String.valueOf(2));
                break;
            default:
                throw SQLError.createSQLException("Internal error while parsing callable statement metadata (unknown nullability value fount)", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
        }
        row[12] = null;
        if (z) {
            row[13] = null;
            row[14] = s2b(String.valueOf(ordinal));
            row[15] = s2b(typeDescriptor.isNullable);
            row[16] = procNameAsBytes;
        } else {
            row[13] = null;
            row[14] = null;
            row[15] = null;
            row[16] = null;
            row[17] = s2b(String.valueOf(ordinal));
            row[18] = s2b(typeDescriptor.isNullable);
            row[19] = procNameAsBytes;
        }
        return new ByteArrayRow(row, getExceptionInterceptor());
    }

    /* access modifiers changed from: protected */
    public int getColumnType(boolean isOutParam, boolean isInParam, boolean isReturnParam, boolean forGetFunctionColumns) {
        if (isInParam && isOutParam) {
            return 2;
        }
        if (isInParam) {
            return 1;
        }
        if (isOutParam) {
            return 4;
        }
        if (isReturnParam) {
            return 5;
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public ExceptionInterceptor getExceptionInterceptor() {
        return this.exceptionInterceptor;
    }

    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        return true;
    }

    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        return false;
    }

    public boolean deletesAreDetected(int type) throws SQLException {
        return false;
    }

    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x016d  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0173  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0176  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x017a  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x01ae  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x01bc A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.mysql.jdbc.ResultSetRow> extractForeignKeyForTable(java.util.ArrayList<com.mysql.jdbc.ResultSetRow> r30, java.sql.ResultSet r31, java.lang.String r32) throws java.sql.SQLException {
        /*
            r29 = this;
            r0 = r29
            r1 = r30
            r2 = r31
            r3 = 3
            byte[][] r3 = new byte[r3][]
            r4 = 1
            byte[] r5 = r2.getBytes(r4)
            r6 = 0
            r3[r6] = r5
            java.lang.String r5 = "SUPPORTS_FK"
            byte[] r5 = r0.s2b(r5)
            r3[r4] = r5
            r5 = 2
            java.lang.String r7 = r2.getString(r5)
            java.util.StringTokenizer r8 = new java.util.StringTokenizer
            java.lang.String r9 = "\n"
            r8.<init>(r7, r9)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            java.lang.String r10 = "comment; "
            r9.<init>(r10)
            r10 = 1
        L_0x002d:
            boolean r11 = r8.hasMoreTokens()
            if (r11 == 0) goto L_0x01c3
            java.lang.String r11 = r8.nextToken()
            java.lang.String r11 = r11.trim()
            r12 = 0
            java.lang.String r13 = "CONSTRAINT"
            boolean r13 = com.mysql.jdbc.StringUtils.startsWithIgnoreCase(r11, r13)
            r14 = -1
            if (r13 == 0) goto L_0x0080
            r13 = 1
            java.lang.String r15 = r0.quotedId
            int r15 = com.mysql.jdbc.StringUtils.indexOfQuoteDoubleAware(r11, r15, r6)
            java.lang.String r5 = "\""
            if (r15 != r14) goto L_0x0055
            int r15 = r11.indexOf(r5)
            r13 = 0
        L_0x0055:
            if (r15 == r14) goto L_0x0080
            r16 = -1
            if (r13 == 0) goto L_0x0064
            java.lang.String r5 = r0.quotedId
            int r6 = r15 + 1
            int r5 = com.mysql.jdbc.StringUtils.indexOfQuoteDoubleAware(r11, r5, r6)
            goto L_0x006a
        L_0x0064:
            int r6 = r15 + 1
            int r5 = com.mysql.jdbc.StringUtils.indexOfQuoteDoubleAware(r11, r5, r6)
        L_0x006a:
            if (r5 == r14) goto L_0x0080
            int r6 = r15 + 1
            java.lang.String r12 = r11.substring(r6, r5)
            int r6 = r5 + 1
            int r14 = r11.length()
            java.lang.String r6 = r11.substring(r6, r14)
            java.lang.String r11 = r6.trim()
        L_0x0080:
            java.lang.String r5 = "FOREIGN KEY"
            boolean r6 = r11.startsWith(r5)
            if (r6 == 0) goto L_0x01bc
            java.lang.String r6 = ","
            boolean r6 = r11.endsWith(r6)
            if (r6 == 0) goto L_0x009a
            int r6 = r11.length()
            int r6 = r6 - r4
            r13 = 0
            java.lang.String r11 = r11.substring(r13, r6)
        L_0x009a:
            int r6 = r11.indexOf(r5)
            r13 = 0
            java.lang.String r14 = r0.quotedId
            com.mysql.jdbc.MySQLConnection r15 = r0.conn
            boolean r15 = r15.getPedantic()
            r4 = r32
            java.lang.String r14 = com.mysql.jdbc.StringUtils.quoteIdentifier(r4, r14, r15)
            r15 = 0
            r23 = 0
            r2 = -1
            if (r6 == r2) goto L_0x0167
            int r2 = r5.length()
            int r2 = r2 + r6
            java.lang.String r5 = r0.quotedId
            java.util.Set<com.mysql.jdbc.StringUtils$SearchMode> r22 = com.mysql.jdbc.StringUtils.SEARCH_MODE__ALL
            java.lang.String r19 = "REFERENCES"
            r17 = r2
            r18 = r11
            r20 = r5
            r21 = r5
            int r5 = com.mysql.jdbc.StringUtils.indexOfIgnoreCase((int) r17, (java.lang.String) r18, (java.lang.String) r19, (java.lang.String) r20, (java.lang.String) r21, (java.util.Set<com.mysql.jdbc.StringUtils.SearchMode>) r22)
            r4 = -1
            if (r5 == r4) goto L_0x0160
            r4 = 40
            int r4 = r11.indexOf(r4, r2)
            r24 = r2
            java.lang.String r2 = r0.quotedId
            java.util.Set<com.mysql.jdbc.StringUtils$SearchMode> r22 = com.mysql.jdbc.StringUtils.SEARCH_MODE__ALL
            java.lang.String r19 = ")"
            r17 = r4
            r18 = r11
            r20 = r2
            r21 = r2
            int r2 = com.mysql.jdbc.StringUtils.indexOfIgnoreCase((int) r17, (java.lang.String) r18, (java.lang.String) r19, (java.lang.String) r20, (java.lang.String) r21, (java.util.Set<com.mysql.jdbc.StringUtils.SearchMode>) r22)
            r25 = r6
            int r6 = r4 + 1
            java.lang.String r13 = r11.substring(r6, r2)
            java.lang.String r6 = "REFERENCES"
            int r6 = r6.length()
            int r6 = r6 + r5
            r26 = r2
            java.lang.String r2 = r0.quotedId
            java.util.Set<com.mysql.jdbc.StringUtils$SearchMode> r22 = com.mysql.jdbc.StringUtils.SEARCH_MODE__ALL
            java.lang.String r19 = "("
            r17 = r6
            r18 = r11
            r20 = r2
            r21 = r2
            int r2 = com.mysql.jdbc.StringUtils.indexOfIgnoreCase((int) r17, (java.lang.String) r18, (java.lang.String) r19, (java.lang.String) r20, (java.lang.String) r21, (java.util.Set<com.mysql.jdbc.StringUtils.SearchMode>) r22)
            r27 = r4
            r4 = -1
            if (r2 == r4) goto L_0x0159
            java.lang.String r15 = r11.substring(r6, r2)
            int r17 = r2 + 1
            java.lang.String r4 = r0.quotedId
            java.util.Set<com.mysql.jdbc.StringUtils$SearchMode> r22 = com.mysql.jdbc.StringUtils.SEARCH_MODE__ALL
            java.lang.String r19 = ")"
            r18 = r11
            r20 = r4
            r21 = r4
            int r4 = com.mysql.jdbc.StringUtils.indexOfIgnoreCase((int) r17, (java.lang.String) r18, (java.lang.String) r19, (java.lang.String) r20, (java.lang.String) r21, (java.util.Set<com.mysql.jdbc.StringUtils.SearchMode>) r22)
            r28 = r5
            r5 = -1
            if (r4 == r5) goto L_0x0131
            int r5 = r2 + 1
            java.lang.String r23 = r11.substring(r5, r4)
        L_0x0131:
            r17 = 0
            java.lang.String r5 = r0.quotedId
            java.util.Set<com.mysql.jdbc.StringUtils$SearchMode> r22 = com.mysql.jdbc.StringUtils.SEARCH_MODE__ALL
            java.lang.String r19 = "."
            r18 = r15
            r20 = r5
            r21 = r5
            int r5 = com.mysql.jdbc.StringUtils.indexOfIgnoreCase((int) r17, (java.lang.String) r18, (java.lang.String) r19, (java.lang.String) r20, (java.lang.String) r21, (java.util.Set<com.mysql.jdbc.StringUtils.SearchMode>) r22)
            r17 = r2
            r2 = -1
            if (r5 == r2) goto L_0x0156
            r2 = 0
            java.lang.String r14 = r15.substring(r2, r5)
            int r2 = r5 + 1
            java.lang.String r15 = r15.substring(r2)
            r2 = r23
            goto L_0x016b
        L_0x0156:
            r2 = r23
            goto L_0x016b
        L_0x0159:
            r17 = r2
            r28 = r5
            r2 = r23
            goto L_0x016b
        L_0x0160:
            r24 = r2
            r28 = r5
            r25 = r6
            goto L_0x0169
        L_0x0167:
            r25 = r6
        L_0x0169:
            r2 = r23
        L_0x016b:
            if (r10 != 0) goto L_0x0173
            java.lang.String r4 = "; "
            r9.append(r4)
            goto L_0x0174
        L_0x0173:
            r10 = 0
        L_0x0174:
            if (r12 == 0) goto L_0x017a
            r9.append(r12)
            goto L_0x017f
        L_0x017a:
            java.lang.String r4 = "not_available"
            r9.append(r4)
        L_0x017f:
            java.lang.String r4 = "("
            r9.append(r4)
            r9.append(r13)
            java.lang.String r5 = ") REFER "
            r9.append(r5)
            r9.append(r14)
            java.lang.String r5 = "/"
            r9.append(r5)
            r9.append(r15)
            r9.append(r4)
            r9.append(r2)
            java.lang.String r4 = ")"
            r9.append(r4)
            int r4 = r11.lastIndexOf(r4)
            int r5 = r11.length()
            r6 = 1
            int r5 = r5 - r6
            if (r4 == r5) goto L_0x01bc
            int r5 = r4 + 1
            java.lang.String r5 = r11.substring(r5)
            java.lang.String r6 = " "
            r9.append(r6)
            r9.append(r5)
        L_0x01bc:
            r2 = r31
            r4 = 1
            r5 = 2
            r6 = 0
            goto L_0x002d
        L_0x01c3:
            java.lang.String r2 = r9.toString()
            byte[] r2 = r0.s2b(r2)
            r4 = 2
            r3[r4] = r2
            com.mysql.jdbc.ByteArrayRow r2 = new com.mysql.jdbc.ByteArrayRow
            com.mysql.jdbc.ExceptionInterceptor r4 = r29.getExceptionInterceptor()
            r2.<init>(r3, r4)
            r1.add(r2)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.DatabaseMetaData.extractForeignKeyForTable(java.util.ArrayList, java.sql.ResultSet, java.lang.String):java.util.List");
    }

    public ResultSet extractForeignKeyFromCreateTable(String catalog, String tableName) throws SQLException {
        ArrayList<String> tableList = new ArrayList<>();
        ResultSet rs = null;
        if (tableName != null) {
            tableList.add(tableName);
        } else {
            try {
                rs = getTables(catalog, "", "%", new String[]{"TABLE"});
                while (rs.next()) {
                    tableList.add(rs.getString("TABLE_NAME"));
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
        }
        ArrayList<ResultSetRow> rows = new ArrayList<>();
        Field[] fields = {new Field("", "Name", 1, Integer.MAX_VALUE), new Field("", "Type", 1, 255), new Field("", "Comment", 1, Integer.MAX_VALUE)};
        int numTables = tableList.size();
        Statement stmt = this.conn.getMetadataSafeStatement();
        for (int i = 0; i < numTables; i++) {
            try {
                rs = stmt.executeQuery("SHOW CREATE TABLE " + getFullyQualifiedName(catalog, tableList.get(i)));
                while (rs.next()) {
                    extractForeignKeyForTable(rows, rs, catalog);
                }
            } catch (SQLException sqlEx) {
                if (SQLError.SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND.equals(sqlEx.getSQLState())) {
                    continue;
                } else if (sqlEx.getErrorCode() != 1146) {
                    throw sqlEx;
                }
            } catch (Throwable th) {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                throw th;
            }
        }
        if (rs != null) {
            rs.close();
        }
        if (stmt != null) {
            stmt.close();
        }
        return buildResultSet(fields, rows);
    }

    public ResultSet getAttributes(String arg0, String arg1, String arg2, String arg3) throws SQLException {
        return buildResultSet(new Field[]{new Field("", "TYPE_CAT", 1, 32), new Field("", "TYPE_SCHEM", 1, 32), new Field("", "TYPE_NAME", 1, 32), new Field("", "ATTR_NAME", 1, 32), new Field("", "DATA_TYPE", 5, 32), new Field("", "ATTR_TYPE_NAME", 1, 32), new Field("", "ATTR_SIZE", 4, 32), new Field("", "DECIMAL_DIGITS", 4, 32), new Field("", "NUM_PREC_RADIX", 4, 32), new Field("", "NULLABLE ", 4, 32), new Field("", "REMARKS", 1, 32), new Field("", "ATTR_DEF", 1, 32), new Field("", "SQL_DATA_TYPE", 4, 32), new Field("", "SQL_DATETIME_SUB", 4, 32), new Field("", "CHAR_OCTET_LENGTH", 4, 32), new Field("", "ORDINAL_POSITION", 4, 32), new Field("", "IS_NULLABLE", 1, 32), new Field("", "SCOPE_CATALOG", 1, 32), new Field("", "SCOPE_SCHEMA", 1, 32), new Field("", "SCOPE_TABLE", 1, 32), new Field("", "SOURCE_DATA_TYPE", 5, 32)}, new ArrayList());
    }

    public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
        if (table != null) {
            Field[] fields = {new Field("", "SCOPE", 5, 5), new Field("", "COLUMN_NAME", 1, 32), new Field("", "DATA_TYPE", 4, 32), new Field("", "TYPE_NAME", 1, 32), new Field("", "COLUMN_SIZE", 4, 10), new Field("", "BUFFER_LENGTH", 4, 10), new Field("", "DECIMAL_DIGITS", 5, 10), new Field("", "PSEUDO_COLUMN", 5, 5)};
            ArrayList<ResultSetRow> rows = new ArrayList<>();
            Statement stmt = this.conn.getMetadataSafeStatement();
            try {
                final String str = table;
                final Statement statement = stmt;
                final ArrayList<ResultSetRow> arrayList = rows;
                new IterateBlock<String>(getCatalogIterator(catalog)) {
                    /* access modifiers changed from: package-private */
                    /* JADX WARNING: Removed duplicated region for block: B:44:0x0188  */
                    /* JADX WARNING: Removed duplicated region for block: B:49:0x0195 A[SYNTHETIC, Splitter:B:49:0x0195] */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void forEach(java.lang.String r17) throws java.sql.SQLException {
                        /*
                            r16 = this;
                            r1 = r16
                            java.lang.String r0 = "enum"
                            r2 = 0
                            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ SQLException -> 0x0178, all -> 0x0173 }
                            java.lang.String r4 = "SHOW COLUMNS FROM "
                            r3.<init>(r4)     // Catch:{ SQLException -> 0x0178, all -> 0x0173 }
                            java.lang.String r4 = r5     // Catch:{ SQLException -> 0x0178, all -> 0x0173 }
                            com.mysql.jdbc.DatabaseMetaData r5 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ SQLException -> 0x0178, all -> 0x0173 }
                            java.lang.String r5 = r5.quotedId     // Catch:{ SQLException -> 0x0178, all -> 0x0173 }
                            com.mysql.jdbc.DatabaseMetaData r6 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ SQLException -> 0x0178, all -> 0x0173 }
                            com.mysql.jdbc.MySQLConnection r6 = r6.conn     // Catch:{ SQLException -> 0x0178, all -> 0x0173 }
                            boolean r6 = r6.getPedantic()     // Catch:{ SQLException -> 0x0178, all -> 0x0173 }
                            java.lang.String r4 = com.mysql.jdbc.StringUtils.quoteIdentifier(r4, r5, r6)     // Catch:{ SQLException -> 0x0178, all -> 0x0173 }
                            r3.append(r4)     // Catch:{ SQLException -> 0x0178, all -> 0x0173 }
                            java.lang.String r4 = " FROM "
                            r3.append(r4)     // Catch:{ SQLException -> 0x0178, all -> 0x0173 }
                            com.mysql.jdbc.DatabaseMetaData r4 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ SQLException -> 0x0178, all -> 0x0173 }
                            java.lang.String r4 = r4.quotedId     // Catch:{ SQLException -> 0x0178, all -> 0x0173 }
                            com.mysql.jdbc.DatabaseMetaData r5 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ SQLException -> 0x0178, all -> 0x0173 }
                            com.mysql.jdbc.MySQLConnection r5 = r5.conn     // Catch:{ SQLException -> 0x0178, all -> 0x0173 }
                            boolean r5 = r5.getPedantic()     // Catch:{ SQLException -> 0x0178, all -> 0x0173 }
                            r6 = r17
                            java.lang.String r4 = com.mysql.jdbc.StringUtils.quoteIdentifier(r6, r4, r5)     // Catch:{ SQLException -> 0x0171 }
                            r3.append(r4)     // Catch:{ SQLException -> 0x0171 }
                            java.sql.Statement r4 = r6     // Catch:{ SQLException -> 0x0171 }
                            java.lang.String r5 = r3.toString()     // Catch:{ SQLException -> 0x0171 }
                            java.sql.ResultSet r4 = r4.executeQuery(r5)     // Catch:{ SQLException -> 0x0171 }
                            r2 = r4
                        L_0x0046:
                            boolean r4 = r2.next()     // Catch:{ SQLException -> 0x0171 }
                            if (r4 == 0) goto L_0x0167
                            java.lang.String r4 = "Key"
                            java.lang.String r4 = r2.getString(r4)     // Catch:{ SQLException -> 0x0171 }
                            if (r4 == 0) goto L_0x0165
                            java.lang.String r5 = "PRI"
                            boolean r5 = com.mysql.jdbc.StringUtils.startsWithIgnoreCase(r4, r5)     // Catch:{ SQLException -> 0x0171 }
                            if (r5 == 0) goto L_0x0165
                            r5 = 8
                            byte[][] r5 = new byte[r5][]     // Catch:{ SQLException -> 0x0171 }
                            r7 = 2
                            java.lang.String r8 = java.lang.Integer.toString(r7)     // Catch:{ SQLException -> 0x0171 }
                            byte[] r8 = r8.getBytes()     // Catch:{ SQLException -> 0x0171 }
                            r9 = 0
                            r5[r9] = r8     // Catch:{ SQLException -> 0x0171 }
                            java.lang.String r8 = "Field"
                            byte[] r8 = r2.getBytes(r8)     // Catch:{ SQLException -> 0x0171 }
                            r10 = 1
                            r5[r10] = r8     // Catch:{ SQLException -> 0x0171 }
                            java.lang.String r8 = "Type"
                            java.lang.String r8 = r2.getString(r8)     // Catch:{ SQLException -> 0x0171 }
                            int r11 = com.mysql.jdbc.MysqlIO.getMaxBuf()     // Catch:{ SQLException -> 0x0171 }
                            r12 = 0
                            int r13 = r8.indexOf(r0)     // Catch:{ SQLException -> 0x0171 }
                            java.lang.String r14 = ")"
                            r15 = -1
                            java.lang.String r9 = ","
                            java.lang.String r10 = "("
                            if (r13 == r15) goto L_0x00b9
                            int r10 = r8.indexOf(r10)     // Catch:{ SQLException -> 0x0171 }
                            int r13 = r8.indexOf(r14)     // Catch:{ SQLException -> 0x0171 }
                            java.lang.String r10 = r8.substring(r10, r13)     // Catch:{ SQLException -> 0x0171 }
                            java.util.StringTokenizer r13 = new java.util.StringTokenizer     // Catch:{ SQLException -> 0x0171 }
                            r13.<init>(r10, r9)     // Catch:{ SQLException -> 0x0171 }
                            r9 = r13
                            r13 = 0
                        L_0x00a0:
                            boolean r14 = r9.hasMoreTokens()     // Catch:{ SQLException -> 0x0171 }
                            if (r14 == 0) goto L_0x00b5
                            java.lang.String r14 = r9.nextToken()     // Catch:{ SQLException -> 0x0171 }
                            int r14 = r14.length()     // Catch:{ SQLException -> 0x0171 }
                            int r14 = r14 - r7
                            int r14 = java.lang.Math.max(r13, r14)     // Catch:{ SQLException -> 0x0171 }
                            r13 = r14
                            goto L_0x00a0
                        L_0x00b5:
                            r11 = r13
                            r12 = 0
                            r8 = r0
                            goto L_0x010a
                        L_0x00b9:
                            int r13 = r8.indexOf(r10)     // Catch:{ SQLException -> 0x0171 }
                            if (r13 == r15) goto L_0x010a
                            int r13 = r8.indexOf(r9)     // Catch:{ SQLException -> 0x0171 }
                            if (r13 == r15) goto L_0x00ec
                            int r13 = r8.indexOf(r10)     // Catch:{ SQLException -> 0x0171 }
                            r15 = 1
                            int r13 = r13 + r15
                            int r15 = r8.indexOf(r9)     // Catch:{ SQLException -> 0x0171 }
                            java.lang.String r13 = r8.substring(r13, r15)     // Catch:{ SQLException -> 0x0171 }
                            int r13 = java.lang.Integer.parseInt(r13)     // Catch:{ SQLException -> 0x0171 }
                            r11 = r13
                            int r9 = r8.indexOf(r9)     // Catch:{ SQLException -> 0x0171 }
                            r13 = 1
                            int r9 = r9 + r13
                            int r13 = r8.indexOf(r14)     // Catch:{ SQLException -> 0x0171 }
                            java.lang.String r9 = r8.substring(r9, r13)     // Catch:{ SQLException -> 0x0171 }
                            int r9 = java.lang.Integer.parseInt(r9)     // Catch:{ SQLException -> 0x0171 }
                            r12 = r9
                            goto L_0x00ff
                        L_0x00ec:
                            int r9 = r8.indexOf(r10)     // Catch:{ SQLException -> 0x0171 }
                            r13 = 1
                            int r9 = r9 + r13
                            int r13 = r8.indexOf(r14)     // Catch:{ SQLException -> 0x0171 }
                            java.lang.String r9 = r8.substring(r9, r13)     // Catch:{ SQLException -> 0x0171 }
                            int r9 = java.lang.Integer.parseInt(r9)     // Catch:{ SQLException -> 0x0171 }
                            r11 = r9
                        L_0x00ff:
                            int r9 = r8.indexOf(r10)     // Catch:{ SQLException -> 0x0171 }
                            r10 = 0
                            java.lang.String r9 = r8.substring(r10, r9)     // Catch:{ SQLException -> 0x0171 }
                            r8 = r9
                            goto L_0x010b
                        L_0x010a:
                        L_0x010b:
                            com.mysql.jdbc.DatabaseMetaData r9 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ SQLException -> 0x0171 }
                            int r10 = com.mysql.jdbc.MysqlDefs.mysqlToJavaType((java.lang.String) r8)     // Catch:{ SQLException -> 0x0171 }
                            java.lang.String r10 = java.lang.String.valueOf(r10)     // Catch:{ SQLException -> 0x0171 }
                            byte[] r9 = r9.s2b(r10)     // Catch:{ SQLException -> 0x0171 }
                            r5[r7] = r9     // Catch:{ SQLException -> 0x0171 }
                            r7 = 3
                            com.mysql.jdbc.DatabaseMetaData r9 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ SQLException -> 0x0171 }
                            byte[] r9 = r9.s2b(r8)     // Catch:{ SQLException -> 0x0171 }
                            r5[r7] = r9     // Catch:{ SQLException -> 0x0171 }
                            r7 = 4
                            int r9 = r11 + r12
                            java.lang.String r9 = java.lang.Integer.toString(r9)     // Catch:{ SQLException -> 0x0171 }
                            byte[] r9 = r9.getBytes()     // Catch:{ SQLException -> 0x0171 }
                            r5[r7] = r9     // Catch:{ SQLException -> 0x0171 }
                            r7 = 5
                            int r9 = r11 + r12
                            java.lang.String r9 = java.lang.Integer.toString(r9)     // Catch:{ SQLException -> 0x0171 }
                            byte[] r9 = r9.getBytes()     // Catch:{ SQLException -> 0x0171 }
                            r5[r7] = r9     // Catch:{ SQLException -> 0x0171 }
                            r7 = 6
                            java.lang.String r9 = java.lang.Integer.toString(r12)     // Catch:{ SQLException -> 0x0171 }
                            byte[] r9 = r9.getBytes()     // Catch:{ SQLException -> 0x0171 }
                            r5[r7] = r9     // Catch:{ SQLException -> 0x0171 }
                            r7 = 7
                            r9 = 1
                            java.lang.String r9 = java.lang.Integer.toString(r9)     // Catch:{ SQLException -> 0x0171 }
                            byte[] r9 = r9.getBytes()     // Catch:{ SQLException -> 0x0171 }
                            r5[r7] = r9     // Catch:{ SQLException -> 0x0171 }
                            java.util.ArrayList r7 = r7     // Catch:{ SQLException -> 0x0171 }
                            com.mysql.jdbc.ByteArrayRow r9 = new com.mysql.jdbc.ByteArrayRow     // Catch:{ SQLException -> 0x0171 }
                            com.mysql.jdbc.DatabaseMetaData r10 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ SQLException -> 0x0171 }
                            com.mysql.jdbc.ExceptionInterceptor r10 = r10.getExceptionInterceptor()     // Catch:{ SQLException -> 0x0171 }
                            r9.<init>(r5, r10)     // Catch:{ SQLException -> 0x0171 }
                            r7.add(r9)     // Catch:{ SQLException -> 0x0171 }
                        L_0x0165:
                            goto L_0x0046
                        L_0x0167:
                            if (r2 == 0) goto L_0x0191
                            r2.close()     // Catch:{ Exception -> 0x016e }
                            goto L_0x016f
                        L_0x016e:
                            r0 = move-exception
                        L_0x016f:
                            r2 = 0
                            goto L_0x0191
                        L_0x0171:
                            r0 = move-exception
                            goto L_0x017b
                        L_0x0173:
                            r0 = move-exception
                            r6 = r17
                        L_0x0176:
                            r3 = r0
                            goto L_0x0198
                        L_0x0178:
                            r0 = move-exception
                            r6 = r17
                        L_0x017b:
                            java.lang.String r3 = "42S02"
                            java.lang.String r4 = r0.getSQLState()     // Catch:{ all -> 0x0196 }
                            boolean r3 = r3.equals(r4)     // Catch:{ all -> 0x0196 }
                            if (r3 == 0) goto L_0x0194
                            if (r2 == 0) goto L_0x0191
                            r2.close()     // Catch:{ Exception -> 0x018e }
                            goto L_0x018f
                        L_0x018e:
                            r0 = move-exception
                        L_0x018f:
                            r0 = 0
                            r2 = r0
                        L_0x0191:
                            return
                        L_0x0194:
                            throw r0     // Catch:{ all -> 0x0196 }
                        L_0x0196:
                            r0 = move-exception
                            goto L_0x0176
                        L_0x0198:
                            if (r2 == 0) goto L_0x01a0
                            r2.close()     // Catch:{ Exception -> 0x019e }
                            goto L_0x019f
                        L_0x019e:
                            r0 = move-exception
                        L_0x019f:
                            r2 = 0
                        L_0x01a0:
                            throw r3
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.DatabaseMetaData.AnonymousClass1.forEach(java.lang.String):void");
                    }
                }.doForAll();
                return buildResultSet(fields, rows);
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        } else {
            throw SQLError.createSQLException("Table not specified.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
    }

    /*  JADX ERROR: NullPointerException in pass: CodeShrinkVisitor
        java.lang.NullPointerException
        	at jadx.core.dex.instructions.args.InsnArg.wrapInstruction(InsnArg.java:118)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.inline(CodeShrinkVisitor.java:146)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkBlock(CodeShrinkVisitor.java:71)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkMethod(CodeShrinkVisitor.java:43)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.visit(CodeShrinkVisitor.java:35)
        */
    private void getCallStmtParameterTypes(java.lang.String r46, java.lang.String r47, com.mysql.jdbc.DatabaseMetaData.ProcedureType r48, java.lang.String r49, java.util.List<com.mysql.jdbc.ResultSetRow> r50, boolean r51) throws java.sql.SQLException {
        /*
            r45 = this;
            r11 = r45
            r1 = r46
            r7 = r47
            r12 = r48
            r13 = r50
            java.lang.String r8 = "UTF-8"
            java.lang.String r14 = " "
            r2 = 0
            r9 = 0
            if (r49 != 0) goto L_0x002b
            com.mysql.jdbc.MySQLConnection r0 = r11.conn
            boolean r0 = r0.getNullNamePatternMatchesAll()
            if (r0 == 0) goto L_0x001e
            java.lang.String r0 = "%"
            r15 = r0
            goto L_0x002d
        L_0x001e:
            com.mysql.jdbc.ExceptionInterceptor r0 = r45.getExceptionInterceptor()
            java.lang.String r3 = "Parameter/Column name pattern can not be NULL or empty."
            java.lang.String r4 = "S1009"
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r3, (java.lang.String) r4, (com.mysql.jdbc.ExceptionInterceptor) r0)
            throw r0
        L_0x002b:
            r15 = r49
        L_0x002d:
            r16 = 0
            r10 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            com.mysql.jdbc.MySQLConnection r0 = r11.conn     // Catch:{ all -> 0x0647 }
            java.sql.Statement r0 = r0.getMetadataSafeStatement()     // Catch:{ all -> 0x0647 }
            r6 = r0
            com.mysql.jdbc.MySQLConnection r0 = r11.conn     // Catch:{ all -> 0x063a }
            java.lang.String r0 = r0.getCatalog()     // Catch:{ all -> 0x063a }
            r5 = r0
            com.mysql.jdbc.MySQLConnection r0 = r11.conn     // Catch:{ all -> 0x063a }
            boolean r0 = r0.lowerCaseTableNames()     // Catch:{ all -> 0x063a }
            r4 = 1
            if (r0 == 0) goto L_0x00ac
            if (r1 == 0) goto L_0x00ac
            int r0 = r46.length()     // Catch:{ all -> 0x00a1 }
            if (r0 == 0) goto L_0x00ac
            if (r5 == 0) goto L_0x00ac
            int r0 = r5.length()     // Catch:{ all -> 0x00a1 }
            if (r0 == 0) goto L_0x00ac
            r2 = 0
            com.mysql.jdbc.MySQLConnection r0 = r11.conn     // Catch:{ all -> 0x0093 }
            java.lang.String r3 = r11.quotedId     // Catch:{ all -> 0x0093 }
            java.lang.String r3 = com.mysql.jdbc.StringUtils.unQuoteIdentifier(r1, r3)     // Catch:{ all -> 0x0093 }
            r0.setCatalog(r3)     // Catch:{ all -> 0x0093 }
            java.lang.String r0 = "SELECT DATABASE()"
            java.sql.ResultSet r0 = r6.executeQuery(r0)     // Catch:{ all -> 0x0093 }
            r2 = r0
            r2.next()     // Catch:{ all -> 0x0093 }
            java.lang.String r0 = r2.getString(r4)     // Catch:{ all -> 0x0093 }
            r1 = r0
            com.mysql.jdbc.MySQLConnection r0 = r11.conn     // Catch:{ all -> 0x0088 }
            r0.setCatalog(r5)     // Catch:{ all -> 0x0088 }
            if (r2 == 0) goto L_0x0085
            r2.close()     // Catch:{ all -> 0x0088 }
        L_0x0085:
            r3 = r1
            goto L_0x00ad
        L_0x0088:
            r0 = move-exception
            r24 = r1
            r2 = r6
            r32 = r7
            r27 = r15
            r1 = r0
            goto L_0x0651
        L_0x0093:
            r0 = move-exception
            com.mysql.jdbc.MySQLConnection r3 = r11.conn     // Catch:{ all -> 0x00a1 }
            r3.setCatalog(r5)     // Catch:{ all -> 0x00a1 }
            if (r2 == 0) goto L_0x009e
            r2.close()     // Catch:{ all -> 0x00a1 }
        L_0x009e:
            throw r0     // Catch:{ all -> 0x00a1 }
        L_0x00a1:
            r0 = move-exception
            r24 = r1
            r2 = r6
            r32 = r7
            r27 = r15
            r1 = r0
            goto L_0x0651
        L_0x00ac:
            r3 = r1
        L_0x00ad:
            int r0 = r6.getMaxRows()     // Catch:{ all -> 0x062d }
            r2 = 0
            if (r0 == 0) goto L_0x00c3
            r6.setMaxRows(r2)     // Catch:{ all -> 0x00b8 }
            goto L_0x00c3
        L_0x00b8:
            r0 = move-exception
            r1 = r0
            r24 = r3
            r2 = r6
            r32 = r7
            r27 = r15
            goto L_0x0651
        L_0x00c3:
            r0 = -1
            java.lang.String r1 = r11.quotedId     // Catch:{ all -> 0x062d }
            boolean r1 = r14.equals(r1)     // Catch:{ all -> 0x062d }
            if (r1 != 0) goto L_0x010e
            java.lang.String r21 = "."
            java.lang.String r1 = r11.quotedId     // Catch:{ all -> 0x00fb }
            com.mysql.jdbc.MySQLConnection r2 = r11.conn     // Catch:{ all -> 0x00fb }
            boolean r2 = r2.isNoBackslashEscapesSet()     // Catch:{ all -> 0x00fb }
            if (r2 == 0) goto L_0x00db
            java.util.Set<com.mysql.jdbc.StringUtils$SearchMode> r2 = com.mysql.jdbc.StringUtils.SEARCH_MODE__MRK_COM_WS     // Catch:{ all -> 0x00b8 }
            goto L_0x00dd
        L_0x00db:
            java.util.Set<com.mysql.jdbc.StringUtils$SearchMode> r2 = com.mysql.jdbc.StringUtils.SEARCH_MODE__ALL     // Catch:{ all -> 0x00fb }
        L_0x00dd:
            r22 = r2
            r23 = r1
            r1 = 0
            r2 = r47
            r49 = r10
            r10 = r3
            r3 = r21
            r4 = r23
            r21 = r5
            r5 = r23
            r23 = r15
            r15 = r6
            r6 = r22
            int r1 = com.mysql.jdbc.StringUtils.indexOfIgnoreCase((int) r1, (java.lang.String) r2, (java.lang.String) r3, (java.lang.String) r4, (java.lang.String) r5, (java.util.Set<com.mysql.jdbc.StringUtils.SearchMode>) r6)     // Catch:{ all -> 0x0139 }
            r0 = r1
            r6 = r0
            goto L_0x011e
        L_0x00fb:
            r0 = move-exception
            r49 = r10
            r23 = r15
            r10 = r3
            r15 = r6
            r1 = r0
            r32 = r7
            r24 = r10
            r2 = r15
            r27 = r23
            r10 = r49
            goto L_0x0651
        L_0x010e:
            r21 = r5
            r49 = r10
            r23 = r15
            r10 = r3
            r15 = r6
            java.lang.String r1 = "."
            int r1 = r7.indexOf(r1)     // Catch:{ all -> 0x0621 }
            r0 = r1
            r6 = r0
        L_0x011e:
            r0 = 0
            r5 = -1
            if (r6 == r5) goto L_0x0146
            int r1 = r6 + 1
            int r2 = r47.length()     // Catch:{ all -> 0x0139 }
            if (r1 >= r2) goto L_0x0146
            r1 = 0
            java.lang.String r1 = r7.substring(r1, r6)     // Catch:{ all -> 0x0139 }
            r0 = r1
            int r1 = r6 + 1
            java.lang.String r1 = r7.substring(r1)     // Catch:{ all -> 0x0139 }
            r4 = r0
            r7 = r1
            goto L_0x0154
        L_0x0139:
            r0 = move-exception
            r1 = r0
            r32 = r7
            r24 = r10
            r2 = r15
            r27 = r23
            r10 = r49
            goto L_0x0651
        L_0x0146:
            java.lang.String r1 = r11.quotedId     // Catch:{ all -> 0x0621 }
            com.mysql.jdbc.MySQLConnection r2 = r11.conn     // Catch:{ all -> 0x0621 }
            boolean r2 = r2.getPedantic()     // Catch:{ all -> 0x0621 }
            java.lang.String r1 = com.mysql.jdbc.StringUtils.quoteIdentifier(r10, r1, r2)     // Catch:{ all -> 0x0621 }
            r0 = r1
            r4 = r0
        L_0x0154:
            java.lang.String r0 = r11.quotedId     // Catch:{ all -> 0x0615 }
            java.lang.String r0 = com.mysql.jdbc.StringUtils.unQuoteIdentifier(r7, r0)     // Catch:{ all -> 0x0615 }
            r1 = r0
            byte[] r0 = com.mysql.jdbc.StringUtils.getBytes((java.lang.String) r1, (java.lang.String) r8)     // Catch:{ UnsupportedEncodingException -> 0x016f, all -> 0x0162 }
            r22 = r0
            goto L_0x0178
        L_0x0162:
            r0 = move-exception
            r1 = r0
            r32 = r7
            r24 = r10
            r2 = r15
            r27 = r23
            r10 = r49
            goto L_0x0651
        L_0x016f:
            r0 = move-exception
            r2 = r0
            r0 = r2
            byte[] r2 = r11.s2b(r1)     // Catch:{ all -> 0x0615 }
            r22 = r2
        L_0x0178:
            java.lang.String r0 = r11.quotedId     // Catch:{ all -> 0x0609 }
            java.lang.String r0 = com.mysql.jdbc.StringUtils.unQuoteIdentifier(r4, r0)     // Catch:{ all -> 0x0609 }
            r3 = r0
            byte[] r0 = com.mysql.jdbc.StringUtils.getBytes((java.lang.String) r3, (java.lang.String) r8)     // Catch:{ UnsupportedEncodingException -> 0x0193 }
            r17 = r0
            goto L_0x019c
        L_0x0186:
            r0 = move-exception
            r1 = r0
            r32 = r7
            r24 = r10
            r2 = r15
            r10 = r22
            r27 = r23
            goto L_0x0651
        L_0x0193:
            r0 = move-exception
            r1 = r0
            r0 = r1
            byte[] r1 = r11.s2b(r3)     // Catch:{ all -> 0x0609 }
            r17 = r1
        L_0x019c:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0609 }
            r0.<init>()     // Catch:{ all -> 0x0609 }
            r2 = r0
            r2.append(r4)     // Catch:{ all -> 0x0609 }
            r0 = 46
            r2.append(r0)     // Catch:{ all -> 0x0609 }
            r2.append(r7)     // Catch:{ all -> 0x0609 }
            r0 = 0
            com.mysql.jdbc.DatabaseMetaData$ProcedureType r1 = com.mysql.jdbc.DatabaseMetaData.ProcedureType.PROCEDURE     // Catch:{ all -> 0x0609 }
            if (r12 != r1) goto L_0x01d4
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0186 }
            r1.<init>()     // Catch:{ all -> 0x0186 }
            java.lang.String r8 = "SHOW CREATE PROCEDURE "
            java.lang.StringBuilder r1 = r1.append(r8)     // Catch:{ all -> 0x0186 }
            java.lang.String r8 = r2.toString()     // Catch:{ all -> 0x0186 }
            java.lang.StringBuilder r1 = r1.append(r8)     // Catch:{ all -> 0x0186 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0186 }
            java.sql.ResultSet r1 = r15.executeQuery(r1)     // Catch:{ all -> 0x0186 }
            r9 = r1
            java.lang.String r1 = "Create Procedure"
            r0 = r1
            r1 = r9
            r9 = r0
            goto L_0x01f5
        L_0x01d4:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0609 }
            r1.<init>()     // Catch:{ all -> 0x0609 }
            java.lang.String r8 = "SHOW CREATE FUNCTION "
            java.lang.StringBuilder r1 = r1.append(r8)     // Catch:{ all -> 0x0609 }
            java.lang.String r8 = r2.toString()     // Catch:{ all -> 0x0609 }
            java.lang.StringBuilder r1 = r1.append(r8)     // Catch:{ all -> 0x0609 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0609 }
            java.sql.ResultSet r1 = r15.executeQuery(r1)     // Catch:{ all -> 0x0609 }
            r9 = r1
            java.lang.String r1 = "Create Function"
            r0 = r1
            r1 = r9
            r9 = r0
        L_0x01f5:
            boolean r0 = r1.next()     // Catch:{ all -> 0x05f8 }
            java.lang.String r8 = "YES"
            r24 = r10
            java.lang.String r10 = "`"
            r46 = r10
            java.lang.String r10 = "S1000"
            if (r0 == 0) goto L_0x0405
            java.lang.String r0 = r1.getString(r9)     // Catch:{ all -> 0x03f6 }
            r33 = r0
            com.mysql.jdbc.MySQLConnection r0 = r11.conn     // Catch:{ all -> 0x03f6 }
            boolean r0 = r0.getNoAccessToProcedureBodies()     // Catch:{ all -> 0x03f6 }
            if (r0 != 0) goto L_0x0227
            if (r33 == 0) goto L_0x021c
            int r0 = r33.length()     // Catch:{ all -> 0x0242 }
            if (r0 == 0) goto L_0x021c
            goto L_0x0227
        L_0x021c:
            java.lang.String r0 = "User does not have access to metadata required to determine stored procedure parameter types. If rights can not be granted, configure connection with \"noAccessToProcedureBodies=true\" to have driver generate parameters that represent INOUT strings irregardless of actual parameter types."
            com.mysql.jdbc.ExceptionInterceptor r5 = r45.getExceptionInterceptor()     // Catch:{ all -> 0x0242 }
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r0, (java.lang.String) r10, (com.mysql.jdbc.ExceptionInterceptor) r5)     // Catch:{ all -> 0x0242 }
            throw r0     // Catch:{ all -> 0x0242 }
        L_0x0227:
            java.lang.String r0 = "sql_mode"
            java.lang.String r0 = r1.getString(r0)     // Catch:{ SQLException -> 0x024e }
            java.lang.String r5 = "ANSI"
            int r5 = com.mysql.jdbc.StringUtils.indexOfIgnoreCase(r0, r5)     // Catch:{ SQLException -> 0x023d }
            r35 = r6
            r6 = -1
            if (r5 == r6) goto L_0x023c
            r5 = 1
            r18 = r5
        L_0x023c:
            goto L_0x0252
        L_0x023d:
            r0 = move-exception
            r35 = r6
            r6 = -1
            goto L_0x0252
        L_0x0242:
            r0 = move-exception
            r9 = r1
            r32 = r7
            r2 = r15
            r10 = r22
            r27 = r23
            r1 = r0
            goto L_0x0651
        L_0x024e:
            r0 = move-exception
            r35 = r6
            r6 = r5
        L_0x0252:
            if (r18 == 0) goto L_0x0257
            java.lang.String r0 = "`\""
            goto L_0x0259
        L_0x0257:
            r0 = r46
        L_0x0259:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x03f6 }
            r5.<init>()     // Catch:{ all -> 0x03f6 }
            java.lang.String r6 = "'"
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ all -> 0x03f6 }
            java.lang.StringBuilder r5 = r5.append(r0)     // Catch:{ all -> 0x03f6 }
            java.lang.String r27 = r5.toString()     // Catch:{ all -> 0x03f6 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x03f6 }
            r5.<init>()     // Catch:{ all -> 0x03f6 }
            java.lang.String r6 = "("
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ all -> 0x03f6 }
            java.lang.StringBuilder r5 = r5.append(r0)     // Catch:{ all -> 0x03f6 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x03f6 }
            r19 = r5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x03f6 }
            r5.<init>()     // Catch:{ all -> 0x03f6 }
            java.lang.String r6 = ")"
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ all -> 0x03f6 }
            java.lang.StringBuilder r5 = r5.append(r0)     // Catch:{ all -> 0x03f6 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x03f6 }
            r20 = r5
            if (r33 == 0) goto L_0x03dc
            int r5 = r33.length()     // Catch:{ all -> 0x03f6 }
            if (r5 == 0) goto L_0x03dc
            r29 = 1
            r30 = 0
            r31 = 1
            r32 = 1
            r26 = r33
            r28 = r27
            java.lang.String r5 = com.mysql.jdbc.StringUtils.stripComments(r26, r27, r28, r29, r30, r31, r32)     // Catch:{ all -> 0x03f6 }
            r6 = r5
            r36 = 0
            java.lang.String r38 = "("
            java.lang.String r5 = r11.quotedId     // Catch:{ all -> 0x03f6 }
            r47 = r0
            com.mysql.jdbc.MySQLConnection r0 = r11.conn     // Catch:{ all -> 0x03f6 }
            boolean r0 = r0.isNoBackslashEscapesSet()     // Catch:{ all -> 0x03f6 }
            if (r0 == 0) goto L_0x02c2
            java.util.Set<com.mysql.jdbc.StringUtils$SearchMode> r0 = com.mysql.jdbc.StringUtils.SEARCH_MODE__MRK_COM_WS     // Catch:{ all -> 0x0242 }
            goto L_0x02c4
        L_0x02c2:
            java.util.Set<com.mysql.jdbc.StringUtils$SearchMode> r0 = com.mysql.jdbc.StringUtils.SEARCH_MODE__ALL     // Catch:{ all -> 0x03f6 }
        L_0x02c4:
            r41 = r0
            r37 = r6
            r39 = r5
            r40 = r5
            int r0 = com.mysql.jdbc.StringUtils.indexOfIgnoreCase((int) r36, (java.lang.String) r37, (java.lang.String) r38, (java.lang.String) r39, (java.lang.String) r40, (java.util.Set<com.mysql.jdbc.StringUtils.SearchMode>) r41)     // Catch:{ all -> 0x03f6 }
            r5 = 0
            r49 = r1
            java.lang.String r1 = r11.quotedId     // Catch:{ all -> 0x03cd }
            int r1 = r11.endPositionOfParameterDeclaration(r0, r6, r1)     // Catch:{ all -> 0x03cd }
            r5 = r1
            com.mysql.jdbc.DatabaseMetaData$ProcedureType r1 = com.mysql.jdbc.DatabaseMetaData.ProcedureType.FUNCTION     // Catch:{ all -> 0x03cd }
            if (r12 != r1) goto L_0x0382
            r36 = 0
            java.lang.String r38 = " RETURNS "
            java.lang.String r1 = r11.quotedId     // Catch:{ all -> 0x03cd }
            r26 = r2
            com.mysql.jdbc.MySQLConnection r2 = r11.conn     // Catch:{ all -> 0x03cd }
            boolean r2 = r2.isNoBackslashEscapesSet()     // Catch:{ all -> 0x03cd }
            if (r2 == 0) goto L_0x02fe
            java.util.Set<com.mysql.jdbc.StringUtils$SearchMode> r2 = com.mysql.jdbc.StringUtils.SEARCH_MODE__MRK_COM_WS     // Catch:{ all -> 0x02f1 }
            goto L_0x0300
        L_0x02f1:
            r0 = move-exception
            r9 = r49
            r1 = r0
            r32 = r7
            r2 = r15
            r10 = r22
            r27 = r23
            goto L_0x0651
        L_0x02fe:
            java.util.Set<com.mysql.jdbc.StringUtils$SearchMode> r2 = com.mysql.jdbc.StringUtils.SEARCH_MODE__ALL     // Catch:{ all -> 0x03cd }
        L_0x0300:
            r41 = r2
            r37 = r6
            r39 = r1
            r40 = r1
            int r1 = com.mysql.jdbc.StringUtils.indexOfIgnoreCase((int) r36, (java.lang.String) r37, (java.lang.String) r38, (java.lang.String) r39, (java.lang.String) r40, (java.util.Set<com.mysql.jdbc.StringUtils.SearchMode>) r41)     // Catch:{ all -> 0x03cd }
            r2 = r1
            int r1 = r11.findEndOfReturnsClause(r6, r2)     // Catch:{ all -> 0x03cd }
            java.lang.String r28 = "RETURNS "
            int r28 = r28.length()     // Catch:{ all -> 0x03cd }
            int r28 = r2 + r28
            r29 = r10
            r10 = r28
        L_0x031d:
            r28 = r2
            int r2 = r6.length()     // Catch:{ all -> 0x03cd }
            if (r10 >= r2) goto L_0x0334
            char r2 = r6.charAt(r10)     // Catch:{ all -> 0x02f1 }
            boolean r2 = java.lang.Character.isWhitespace(r2)     // Catch:{ all -> 0x02f1 }
            if (r2 == 0) goto L_0x0334
            int r10 = r10 + 1
            r2 = r28
            goto L_0x031d
        L_0x0334:
            java.lang.String r2 = r6.substring(r10, r1)     // Catch:{ all -> 0x03cd }
            java.lang.String r2 = r2.trim()     // Catch:{ all -> 0x03cd }
            r30 = r1
            com.mysql.jdbc.DatabaseMetaData$TypeDescriptor r1 = new com.mysql.jdbc.DatabaseMetaData$TypeDescriptor     // Catch:{ all -> 0x03cd }
            r1.<init>(r11, r2, r8)     // Catch:{ all -> 0x03cd }
            r42 = r8
            r8 = r1
            java.lang.String r31 = ""
            r32 = 0
            r33 = 0
            r36 = 1
            r37 = 0
            r38 = r30
            r30 = r49
            r1 = r45
            r39 = r2
            r2 = r22
            r40 = r3
            r3 = r17
            r41 = r4
            r4 = r31
            r43 = r5
            r5 = r32
            r44 = r6
            r31 = r35
            r6 = r33
            r32 = r7
            r7 = r36
            r34 = r9
            r9 = r51
            r12 = r29
            r29 = r10
            r10 = r37
            com.mysql.jdbc.ResultSetRow r1 = r1.convertTypeDescriptorToProcedureRow(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x03c2 }
            r13.add(r1)     // Catch:{ all -> 0x03c2 }
            goto L_0x0397
        L_0x0382:
            r30 = r49
            r26 = r2
            r40 = r3
            r41 = r4
            r43 = r5
            r44 = r6
            r32 = r7
            r42 = r8
            r34 = r9
            r12 = r10
            r31 = r35
        L_0x0397:
            r1 = -1
            if (r0 == r1) goto L_0x03b3
            r2 = r43
            if (r2 == r1) goto L_0x03b0
            int r1 = r0 + 1
            r3 = r44
            java.lang.String r1 = r3.substring(r1, r2)     // Catch:{ all -> 0x03c2 }
            r16 = r1
            r10 = r16
            r9 = r19
            r7 = r20
            goto L_0x041c
        L_0x03b0:
            r3 = r44
            goto L_0x03b7
        L_0x03b3:
            r2 = r43
            r3 = r44
        L_0x03b7:
            java.lang.String r1 = "Internal error when parsing callable statement metadata"
            com.mysql.jdbc.ExceptionInterceptor r4 = r45.getExceptionInterceptor()     // Catch:{ all -> 0x03c2 }
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r12, (com.mysql.jdbc.ExceptionInterceptor) r4)     // Catch:{ all -> 0x03c2 }
            throw r1     // Catch:{ all -> 0x03c2 }
        L_0x03c2:
            r0 = move-exception
            r1 = r0
            r2 = r15
            r10 = r22
            r27 = r23
            r9 = r30
            goto L_0x0651
        L_0x03cd:
            r0 = move-exception
            r30 = r49
            r32 = r7
            r1 = r0
            r2 = r15
            r10 = r22
            r27 = r23
            r9 = r30
            goto L_0x0651
        L_0x03dc:
            r47 = r0
            r30 = r1
            r26 = r2
            r40 = r3
            r41 = r4
            r32 = r7
            r42 = r8
            r34 = r9
            r12 = r10
            r31 = r35
            r10 = r16
            r9 = r19
            r7 = r20
            goto L_0x041c
        L_0x03f6:
            r0 = move-exception
            r30 = r1
            r32 = r7
            r1 = r0
            r2 = r15
            r10 = r22
            r27 = r23
            r9 = r30
            goto L_0x0651
        L_0x0405:
            r30 = r1
            r26 = r2
            r40 = r3
            r41 = r4
            r31 = r6
            r32 = r7
            r42 = r8
            r34 = r9
            r12 = r10
            r10 = r16
            r9 = r19
            r7 = r20
        L_0x041c:
            r1 = 0
            if (r30 == 0) goto L_0x042b
            r30.close()     // Catch:{ SQLException -> 0x0424 }
            goto L_0x0428
        L_0x0424:
            r0 = move-exception
            r2 = r0
            r0 = r2
            r1 = r0
        L_0x0428:
            r0 = 0
            r30 = r0
        L_0x042b:
            if (r15 == 0) goto L_0x0437
            r15.close()     // Catch:{ SQLException -> 0x0431 }
            goto L_0x0435
        L_0x0431:
            r0 = move-exception
            r2 = r0
            r0 = r2
            r1 = r0
        L_0x0435:
            r6 = 0
            r15 = r6
        L_0x0437:
            if (r1 != 0) goto L_0x05f7
            if (r10 == 0) goto L_0x05ee
            r0 = 1
            java.lang.String r1 = ","
            r6 = 1
            java.util.List r5 = com.mysql.jdbc.StringUtils.split(r10, r1, r9, r7, r6)
            int r4 = r5.size()
            r1 = 0
            r16 = r0
            r0 = r1
        L_0x044c:
            if (r0 >= r4) goto L_0x05e1
            java.lang.Object r1 = r5.get(r0)
            java.lang.String r1 = (java.lang.String) r1
            java.lang.String r2 = r1.trim()
            int r2 = r2.length()
            if (r2 != 0) goto L_0x0468
            r20 = r7
            r38 = r10
            r27 = r23
            r23 = r9
            goto L_0x05f6
        L_0x0468:
            java.lang.String r2 = "[\\t\\n\\x0B\\f\\r]"
            java.lang.String r3 = r1.replaceAll(r2, r14)
            java.util.StringTokenizer r1 = new java.util.StringTokenizer
            java.lang.String r2 = " \t"
            r1.<init>(r3, r2)
            r19 = r1
            r1 = 0
            r2 = 0
            r8 = 0
            boolean r20 = r19.hasMoreTokens()
            if (r20 == 0) goto L_0x05d2
            r20 = r7
            java.lang.String r7 = r19.nextToken()
            java.lang.String r6 = "OUT"
            boolean r6 = r7.equalsIgnoreCase(r6)
            r47 = r1
            java.lang.String r1 = "Internal error when parsing callable statement metadata (missing parameter name)"
            if (r6 == 0) goto L_0x04ab
            r2 = 1
            boolean r6 = r19.hasMoreTokens()
            if (r6 == 0) goto L_0x04a2
            java.lang.String r1 = r19.nextToken()
            r21 = r2
            r26 = r8
            goto L_0x04f6
        L_0x04a2:
            com.mysql.jdbc.ExceptionInterceptor r6 = r45.getExceptionInterceptor()
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r12, (com.mysql.jdbc.ExceptionInterceptor) r6)
            throw r1
        L_0x04ab:
            java.lang.String r6 = "INOUT"
            boolean r6 = r7.equalsIgnoreCase(r6)
            if (r6 == 0) goto L_0x04cd
            r2 = 1
            r8 = 1
            boolean r6 = r19.hasMoreTokens()
            if (r6 == 0) goto L_0x04c4
            java.lang.String r1 = r19.nextToken()
            r21 = r2
            r26 = r8
            goto L_0x04f6
        L_0x04c4:
            com.mysql.jdbc.ExceptionInterceptor r6 = r45.getExceptionInterceptor()
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r12, (com.mysql.jdbc.ExceptionInterceptor) r6)
            throw r1
        L_0x04cd:
            java.lang.String r6 = "IN"
            boolean r6 = r7.equalsIgnoreCase(r6)
            if (r6 == 0) goto L_0x04ef
            r2 = 0
            r8 = 1
            boolean r6 = r19.hasMoreTokens()
            if (r6 == 0) goto L_0x04e6
            java.lang.String r1 = r19.nextToken()
            r21 = r2
            r26 = r8
            goto L_0x04f6
        L_0x04e6:
            com.mysql.jdbc.ExceptionInterceptor r6 = r45.getExceptionInterceptor()
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r12, (com.mysql.jdbc.ExceptionInterceptor) r6)
            throw r1
        L_0x04ef:
            r2 = 0
            r8 = 1
            r1 = r7
            r21 = r2
            r26 = r8
        L_0x04f6:
            r2 = 0
            boolean r6 = r19.hasMoreTokens()
            if (r6 == 0) goto L_0x05c3
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            java.lang.String r8 = r19.nextToken()
            r6.<init>(r8)
        L_0x0506:
            boolean r8 = r19.hasMoreTokens()
            if (r8 == 0) goto L_0x0517
            r6.append(r14)
            java.lang.String r8 = r19.nextToken()
            r6.append(r8)
            goto L_0x0506
        L_0x0517:
            java.lang.String r8 = r6.toString()
            r47 = r2
            com.mysql.jdbc.DatabaseMetaData$TypeDescriptor r2 = new com.mysql.jdbc.DatabaseMetaData$TypeDescriptor
            r49 = r7
            r7 = r42
            r2.<init>(r11, r8, r7)
            r8 = r2
            r6 = r46
            boolean r2 = r1.startsWith(r6)
            if (r2 == 0) goto L_0x0536
            boolean r2 = r1.endsWith(r6)
            if (r2 != 0) goto L_0x0546
        L_0x0536:
            if (r18 == 0) goto L_0x0554
            java.lang.String r2 = "\""
            boolean r27 = r1.startsWith(r2)
            if (r27 == 0) goto L_0x0554
            boolean r2 = r1.endsWith(r2)
            if (r2 == 0) goto L_0x0554
        L_0x0546:
            int r2 = r1.length()
            r46 = r6
            r6 = 1
            int r2 = r2 - r6
            java.lang.String r1 = r1.substring(r6, r2)
            r2 = r1
            goto L_0x0558
        L_0x0554:
            r46 = r6
            r6 = 1
            r2 = r1
        L_0x0558:
            r1 = r23
            boolean r23 = com.mysql.jdbc.StringUtils.wildCompareIgnoreCase(r2, r1)
            if (r23 == 0) goto L_0x0596
            r23 = 0
            int r25 = r16 + 1
            r27 = r1
            r1 = r45
            r28 = r2
            r2 = r22
            r29 = r3
            r3 = r17
            r31 = r4
            r4 = r28
            r33 = r5
            r5 = r21
            r34 = r46
            r35 = r6
            r6 = r26
            r36 = r49
            r37 = r7
            r7 = r23
            r23 = r9
            r9 = r51
            r38 = r10
            r10 = r16
            com.mysql.jdbc.ResultSetRow r1 = r1.convertTypeDescriptorToProcedureRow(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            r13.add(r1)
            r16 = r25
            goto L_0x05ac
        L_0x0596:
            r34 = r46
            r36 = r49
            r27 = r1
            r28 = r2
            r29 = r3
            r31 = r4
            r33 = r5
            r35 = r6
            r37 = r7
            r23 = r9
            r38 = r10
        L_0x05ac:
            int r0 = r0 + 1
            r7 = r20
            r9 = r23
            r23 = r27
            r4 = r31
            r5 = r33
            r46 = r34
            r6 = r35
            r42 = r37
            r10 = r38
            goto L_0x044c
        L_0x05c3:
            r47 = r2
            r29 = r3
            com.mysql.jdbc.ExceptionInterceptor r2 = r45.getExceptionInterceptor()
            java.lang.String r3 = "Internal error when parsing callable statement metadata (missing parameter type)"
            java.sql.SQLException r2 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r3, (java.lang.String) r12, (com.mysql.jdbc.ExceptionInterceptor) r2)
            throw r2
        L_0x05d2:
            r47 = r1
            r29 = r3
            com.mysql.jdbc.ExceptionInterceptor r1 = r45.getExceptionInterceptor()
            java.lang.String r3 = "Internal error when parsing callable statement metadata (unknown output from 'SHOW CREATE PROCEDURE')"
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r3, (java.lang.String) r12, (com.mysql.jdbc.ExceptionInterceptor) r1)
            throw r1
        L_0x05e1:
            r31 = r4
            r33 = r5
            r20 = r7
            r38 = r10
            r27 = r23
            r23 = r9
            goto L_0x05f6
        L_0x05ee:
            r20 = r7
            r38 = r10
            r27 = r23
            r23 = r9
        L_0x05f6:
            return
        L_0x05f7:
            throw r1
        L_0x05f8:
            r0 = move-exception
            r30 = r1
            r32 = r7
            r24 = r10
            r27 = r23
            r1 = r0
            r2 = r15
            r10 = r22
            r9 = r30
            goto L_0x0651
        L_0x0609:
            r0 = move-exception
            r32 = r7
            r24 = r10
            r27 = r23
            r1 = r0
            r2 = r15
            r10 = r22
            goto L_0x0651
        L_0x0615:
            r0 = move-exception
            r32 = r7
            r24 = r10
            r27 = r23
            r10 = r49
            r1 = r0
            r2 = r15
            goto L_0x0651
        L_0x0621:
            r0 = move-exception
            r24 = r10
            r27 = r23
            r10 = r49
            r1 = r0
            r32 = r7
            r2 = r15
            goto L_0x0651
        L_0x062d:
            r0 = move-exception
            r24 = r3
            r49 = r10
            r27 = r15
            r15 = r6
            r1 = r0
            r32 = r7
            r2 = r15
            goto L_0x0651
        L_0x063a:
            r0 = move-exception
            r49 = r10
            r27 = r15
            r15 = r6
            r24 = r1
            r32 = r7
            r2 = r15
            r1 = r0
            goto L_0x0651
        L_0x0647:
            r0 = move-exception
            r49 = r10
            r27 = r15
            r24 = r1
            r32 = r7
            r1 = r0
        L_0x0651:
            r3 = 0
            if (r9 == 0) goto L_0x065d
            r9.close()     // Catch:{ SQLException -> 0x0658 }
            goto L_0x065c
        L_0x0658:
            r0 = move-exception
            r4 = r0
            r0 = r4
            r3 = r0
        L_0x065c:
            r9 = 0
        L_0x065d:
            if (r2 == 0) goto L_0x0668
            r2.close()     // Catch:{ SQLException -> 0x0663 }
            goto L_0x0667
        L_0x0663:
            r0 = move-exception
            r4 = r0
            r0 = r4
            r3 = r0
        L_0x0667:
            r2 = 0
        L_0x0668:
            if (r3 == 0) goto L_0x066b
            throw r3
        L_0x066b:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.DatabaseMetaData.getCallStmtParameterTypes(java.lang.String, java.lang.String, com.mysql.jdbc.DatabaseMetaData$ProcedureType, java.lang.String, java.util.List, boolean):void");
    }

    private int endPositionOfParameterDeclaration(int beginIndex, String procedureDef, String quoteChar) throws SQLException {
        int currentPos = beginIndex + 1;
        int parenDepth = 1;
        while (parenDepth > 0 && currentPos < procedureDef.length()) {
            int closedParenIndex = StringUtils.indexOfIgnoreCase(currentPos, procedureDef, ")", quoteChar, quoteChar, this.conn.isNoBackslashEscapesSet() ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
            if (closedParenIndex != -1) {
                int nextOpenParenIndex = StringUtils.indexOfIgnoreCase(currentPos, procedureDef, "(", quoteChar, quoteChar, this.conn.isNoBackslashEscapesSet() ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
                if (nextOpenParenIndex == -1 || nextOpenParenIndex >= closedParenIndex) {
                    parenDepth--;
                    currentPos = closedParenIndex;
                } else {
                    parenDepth++;
                    currentPos = closedParenIndex + 1;
                }
            } else {
                throw SQLError.createSQLException("Internal error when parsing callable statement metadata", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
            }
        }
        return currentPos;
    }

    private int findEndOfReturnsClause(String procedureDefn, int positionOfReturnKeyword) throws SQLException {
        String openingMarkers = this.quotedId + "(";
        String closingMarkers = this.quotedId + ")";
        String[] tokens = {"LANGUAGE", "NOT", "DETERMINISTIC", "CONTAINS", "NO", "READ", "MODIFIES", "SQL", "COMMENT", "BEGIN", "RETURN"};
        int startLookingAt = positionOfReturnKeyword + "RETURNS".length() + 1;
        int endOfReturn = -1;
        for (String indexOfIgnoreCase : tokens) {
            int nextEndOfReturn = StringUtils.indexOfIgnoreCase(startLookingAt, procedureDefn, indexOfIgnoreCase, openingMarkers, closingMarkers, this.conn.isNoBackslashEscapesSet() ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
            if (nextEndOfReturn != -1 && (endOfReturn == -1 || nextEndOfReturn < endOfReturn)) {
                endOfReturn = nextEndOfReturn;
            }
        }
        if (endOfReturn != -1) {
            return endOfReturn;
        }
        int endOfReturn2 = StringUtils.indexOfIgnoreCase(startLookingAt, procedureDefn, ":", openingMarkers, closingMarkers, this.conn.isNoBackslashEscapesSet() ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
        if (endOfReturn2 != -1) {
            for (int i = endOfReturn2; i > 0; i--) {
                if (Character.isWhitespace(procedureDefn.charAt(i))) {
                    return i;
                }
            }
            String str = procedureDefn;
        } else {
            String str2 = procedureDefn;
        }
        throw SQLError.createSQLException("Internal error when parsing callable statement metadata", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
    }

    private int getCascadeDeleteOption(String cascadeOptions) {
        int onDeletePos = cascadeOptions.indexOf("ON DELETE");
        if (onDeletePos == -1) {
            return 3;
        }
        String deleteOptions = cascadeOptions.substring(onDeletePos, cascadeOptions.length());
        if (deleteOptions.startsWith("ON DELETE CASCADE")) {
            return 0;
        }
        if (deleteOptions.startsWith("ON DELETE SET NULL")) {
            return 2;
        }
        if (deleteOptions.startsWith("ON DELETE RESTRICT")) {
            return 1;
        }
        deleteOptions.startsWith("ON DELETE NO ACTION");
        return 3;
    }

    private int getCascadeUpdateOption(String cascadeOptions) {
        int onUpdatePos = cascadeOptions.indexOf("ON UPDATE");
        if (onUpdatePos == -1) {
            return 3;
        }
        String updateOptions = cascadeOptions.substring(onUpdatePos, cascadeOptions.length());
        if (updateOptions.startsWith("ON UPDATE CASCADE")) {
            return 0;
        }
        if (updateOptions.startsWith("ON UPDATE SET NULL")) {
            return 2;
        }
        if (updateOptions.startsWith("ON UPDATE RESTRICT")) {
            return 1;
        }
        updateOptions.startsWith("ON UPDATE NO ACTION");
        return 3;
    }

    /* access modifiers changed from: protected */
    public IteratorWithCleanup<String> getCatalogIterator(String catalogSpec) throws SQLException {
        if (catalogSpec != null) {
            if (catalogSpec.equals("")) {
                return new SingleStringIterator(this.database);
            }
            if (this.conn.getPedantic()) {
                return new SingleStringIterator(catalogSpec);
            }
            return new SingleStringIterator(StringUtils.unQuoteIdentifier(catalogSpec, this.quotedId));
        } else if (this.conn.getNullCatalogMeansCurrent()) {
            return new SingleStringIterator(this.database);
        } else {
            return new ResultSetIterator(getCatalogs(), 1);
        }
    }

    public ResultSet getCatalogs() throws SQLException {
        ResultSet results = null;
        Statement stmt = null;
        try {
            stmt = this.conn.getMetadataSafeStatement();
            results = stmt.executeQuery("SHOW DATABASES");
            int catalogsCount = 0;
            if (results.last()) {
                catalogsCount = results.getRow();
                results.beforeFirst();
            }
            List<String> resultsAsList = new ArrayList<>(catalogsCount);
            while (results.next()) {
                resultsAsList.add(results.getString(1));
            }
            Collections.sort(resultsAsList);
            Field[] fields = {new Field("", "TABLE_CAT", 12, results.getMetaData().getColumnDisplaySize(1))};
            ArrayList<ResultSetRow> tuples = new ArrayList<>(catalogsCount);
            for (String cat : resultsAsList) {
                tuples.add(new ByteArrayRow(new byte[][]{s2b(cat)}, getExceptionInterceptor()));
            }
            return buildResultSet(fields, tuples);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (SQLException sqlEx) {
                    AssertionFailedException.shouldNotHappen(sqlEx);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx2) {
                    AssertionFailedException.shouldNotHappen(sqlEx2);
                }
            }
        }
    }

    public String getCatalogSeparator() throws SQLException {
        return ".";
    }

    public String getCatalogTerm() throws SQLException {
        return "database";
    }

    /*  JADX ERROR: NullPointerException in pass: CodeShrinkVisitor
        java.lang.NullPointerException
        	at jadx.core.dex.instructions.args.InsnArg.wrapInstruction(InsnArg.java:118)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.inline(CodeShrinkVisitor.java:146)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkBlock(CodeShrinkVisitor.java:71)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkMethod(CodeShrinkVisitor.java:43)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.visit(CodeShrinkVisitor.java:35)
        */
    public java.sql.ResultSet getColumnPrivileges(java.lang.String r30, java.lang.String r31, java.lang.String r32, java.lang.String r33) throws java.sql.SQLException {
        /*
            r29 = this;
            r1 = r29
            r2 = r32
            r0 = 8
            com.mysql.jdbc.Field[] r3 = new com.mysql.jdbc.Field[r0]
            com.mysql.jdbc.Field r4 = new com.mysql.jdbc.Field
            java.lang.String r5 = ""
            java.lang.String r6 = "TABLE_CAT"
            r7 = 1
            r8 = 64
            r4.<init>(r5, r6, r7, r8)
            r6 = 0
            r3[r6] = r4
            com.mysql.jdbc.Field r4 = new com.mysql.jdbc.Field
            java.lang.String r9 = "TABLE_SCHEM"
            r4.<init>(r5, r9, r7, r7)
            r3[r7] = r4
            com.mysql.jdbc.Field r4 = new com.mysql.jdbc.Field
            java.lang.String r9 = "TABLE_NAME"
            r4.<init>(r5, r9, r7, r8)
            r9 = 2
            r3[r9] = r4
            com.mysql.jdbc.Field r4 = new com.mysql.jdbc.Field
            java.lang.String r10 = "COLUMN_NAME"
            r4.<init>(r5, r10, r7, r8)
            r10 = 3
            r3[r10] = r4
            com.mysql.jdbc.Field r4 = new com.mysql.jdbc.Field
            java.lang.String r11 = "GRANTOR"
            r12 = 77
            r4.<init>(r5, r11, r7, r12)
            r11 = 4
            r3[r11] = r4
            com.mysql.jdbc.Field r4 = new com.mysql.jdbc.Field
            java.lang.String r13 = "GRANTEE"
            r4.<init>(r5, r13, r7, r12)
            r12 = 5
            r3[r12] = r4
            com.mysql.jdbc.Field r4 = new com.mysql.jdbc.Field
            java.lang.String r13 = "PRIVILEGE"
            r4.<init>(r5, r13, r7, r8)
            r8 = 6
            r3[r8] = r4
            com.mysql.jdbc.Field r4 = new com.mysql.jdbc.Field
            java.lang.String r13 = "IS_GRANTABLE"
            r4.<init>(r5, r13, r7, r10)
            r5 = 7
            r3[r5] = r4
            java.lang.String r4 = "SELECT c.host, c.db, t.grantor, c.user, c.table_name, c.column_name, c.column_priv FROM mysql.columns_priv c, mysql.tables_priv t WHERE c.host = t.host AND c.db = t.db AND c.table_name = t.table_name AND c.db LIKE ? AND c.table_name = ? AND c.column_name LIKE ?"
            r13 = 0
            r14 = 0
            java.util.ArrayList r15 = new java.util.ArrayList
            r15.<init>()
            java.sql.PreparedStatement r16 = r1.prepareMetaDataSafeStatement(r4)     // Catch:{ all -> 0x01c4 }
            r13 = r16
            java.lang.String r16 = "%"
            if (r30 == 0) goto L_0x0082
            int r17 = r30.length()     // Catch:{ all -> 0x007a }
            if (r17 == 0) goto L_0x0082
            r12 = r30
            goto L_0x0084
        L_0x007a:
            r0 = move-exception
            r12 = r33
        L_0x007d:
            r2 = r0
            r27 = r4
            goto L_0x01ca
        L_0x0082:
            r12 = r16
        L_0x0084:
            r13.setString(r7, r12)     // Catch:{ all -> 0x01c4 }
            r13.setString(r9, r2)     // Catch:{ all -> 0x01c4 }
            r12 = r33
            r13.setString(r10, r12)     // Catch:{ all -> 0x01c2 }
            java.sql.ResultSet r18 = r13.executeQuery()     // Catch:{ all -> 0x01c2 }
            r14 = r18
        L_0x0095:
            boolean r18 = r14.next()     // Catch:{ all -> 0x01c2 }
            if (r18 == 0) goto L_0x01a9
            java.lang.String r18 = r14.getString(r7)     // Catch:{ all -> 0x01c2 }
            r19 = r18
            java.lang.String r18 = r14.getString(r9)     // Catch:{ all -> 0x01c2 }
            r20 = r18
            java.lang.String r18 = r14.getString(r10)     // Catch:{ all -> 0x01c2 }
            r21 = r18
            java.lang.String r18 = r14.getString(r11)     // Catch:{ all -> 0x01c2 }
            if (r18 == 0) goto L_0x00bf
            int r22 = r18.length()     // Catch:{ all -> 0x00bd }
            if (r22 != 0) goto L_0x00ba
            goto L_0x00bf
        L_0x00ba:
            r11 = r18
            goto L_0x00c3
        L_0x00bd:
            r0 = move-exception
            goto L_0x007d
        L_0x00bf:
            r18 = r16
            r11 = r18
        L_0x00c3:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x01c2 }
            r10.<init>(r11)     // Catch:{ all -> 0x01c2 }
            r9 = r19
            if (r9 == 0) goto L_0x00dc
            com.mysql.jdbc.MySQLConnection r7 = r1.conn     // Catch:{ all -> 0x00bd }
            boolean r7 = r7.getUseHostsInPrivileges()     // Catch:{ all -> 0x00bd }
            if (r7 == 0) goto L_0x00dc
            java.lang.String r7 = "@"
            r10.append(r7)     // Catch:{ all -> 0x00bd }
            r10.append(r9)     // Catch:{ all -> 0x00bd }
        L_0x00dc:
            java.lang.String r7 = r14.getString(r8)     // Catch:{ all -> 0x01c2 }
            java.lang.String r24 = r14.getString(r5)     // Catch:{ all -> 0x01c2 }
            r25 = r24
            r5 = r25
            if (r5 == 0) goto L_0x0182
            java.util.Locale r8 = java.util.Locale.ENGLISH     // Catch:{ all -> 0x01c2 }
            java.lang.String r8 = r5.toUpperCase(r8)     // Catch:{ all -> 0x01c2 }
            r5 = r8
            java.util.StringTokenizer r8 = new java.util.StringTokenizer     // Catch:{ all -> 0x01c2 }
            java.lang.String r6 = ","
            r8.<init>(r5, r6)     // Catch:{ all -> 0x01c2 }
            r6 = r8
        L_0x00f9:
            boolean r8 = r6.hasMoreTokens()     // Catch:{ all -> 0x01c2 }
            if (r8 == 0) goto L_0x016f
            java.lang.String r8 = r6.nextToken()     // Catch:{ all -> 0x01c2 }
            java.lang.String r8 = r8.trim()     // Catch:{ all -> 0x01c2 }
            r27 = r4
            byte[][] r4 = new byte[r0][]     // Catch:{ all -> 0x016c }
            r0 = r20
            byte[] r20 = r1.s2b(r0)     // Catch:{ all -> 0x016c }
            r26 = 0
            r4[r26] = r20     // Catch:{ all -> 0x016c }
            r20 = 0
            r19 = 1
            r4[r19] = r20     // Catch:{ all -> 0x016c }
            byte[] r28 = r1.s2b(r2)     // Catch:{ all -> 0x016c }
            r23 = 2
            r4[r23] = r28     // Catch:{ all -> 0x016c }
            byte[] r28 = r1.s2b(r7)     // Catch:{ all -> 0x016c }
            r18 = 3
            r4[r18] = r28     // Catch:{ all -> 0x016c }
            r28 = r0
            r0 = r21
            if (r0 == 0) goto L_0x013a
            byte[] r21 = r1.s2b(r0)     // Catch:{ all -> 0x016c }
            r22 = 4
            r4[r22] = r21     // Catch:{ all -> 0x016c }
            goto L_0x013e
        L_0x013a:
            r22 = 4
            r4[r22] = r20     // Catch:{ all -> 0x016c }
        L_0x013e:
            r21 = r0
            java.lang.String r0 = r10.toString()     // Catch:{ all -> 0x016c }
            byte[] r0 = r1.s2b(r0)     // Catch:{ all -> 0x016c }
            r17 = 5
            r4[r17] = r0     // Catch:{ all -> 0x016c }
            byte[] r0 = r1.s2b(r8)     // Catch:{ all -> 0x016c }
            r25 = 6
            r4[r25] = r0     // Catch:{ all -> 0x016c }
            r0 = 7
            r4[r0] = r20     // Catch:{ all -> 0x016c }
            com.mysql.jdbc.ByteArrayRow r0 = new com.mysql.jdbc.ByteArrayRow     // Catch:{ all -> 0x016c }
            com.mysql.jdbc.ExceptionInterceptor r2 = r29.getExceptionInterceptor()     // Catch:{ all -> 0x016c }
            r0.<init>(r4, r2)     // Catch:{ all -> 0x016c }
            r15.add(r0)     // Catch:{ all -> 0x016c }
            r2 = r32
            r4 = r27
            r20 = r28
            r0 = 8
            goto L_0x00f9
        L_0x016c:
            r0 = move-exception
            r2 = r0
            goto L_0x01ca
        L_0x016f:
            r27 = r4
            r28 = r20
            r17 = 5
            r18 = 3
            r19 = 1
            r22 = 4
            r23 = 2
            r25 = 6
            r26 = 0
            goto L_0x0194
        L_0x0182:
            r27 = r4
            r26 = r6
            r25 = r8
            r28 = r20
            r17 = 5
            r18 = 3
            r19 = 1
            r22 = 4
            r23 = 2
        L_0x0194:
            r2 = r32
            r10 = r18
            r7 = r19
            r11 = r22
            r9 = r23
            r8 = r25
            r6 = r26
            r4 = r27
            r0 = 8
            r5 = 7
            goto L_0x0095
        L_0x01a9:
            r27 = r4
            if (r14 == 0) goto L_0x01b3
            r14.close()     // Catch:{ Exception -> 0x01b1 }
            goto L_0x01b2
        L_0x01b1:
            r0 = move-exception
        L_0x01b2:
            r14 = 0
        L_0x01b3:
            if (r13 == 0) goto L_0x01bb
            r13.close()     // Catch:{ Exception -> 0x01b9 }
            goto L_0x01ba
        L_0x01b9:
            r0 = move-exception
        L_0x01ba:
            r13 = 0
        L_0x01bb:
            java.sql.ResultSet r0 = r1.buildResultSet(r3, r15)
            return r0
        L_0x01c2:
            r0 = move-exception
            goto L_0x01c7
        L_0x01c4:
            r0 = move-exception
            r12 = r33
        L_0x01c7:
            r27 = r4
            r2 = r0
        L_0x01ca:
            if (r14 == 0) goto L_0x01d2
            r14.close()     // Catch:{ Exception -> 0x01d0 }
            goto L_0x01d1
        L_0x01d0:
            r0 = move-exception
        L_0x01d1:
            r14 = 0
        L_0x01d2:
            if (r13 == 0) goto L_0x01da
            r13.close()     // Catch:{ Exception -> 0x01d8 }
            goto L_0x01d9
        L_0x01d8:
            r0 = move-exception
        L_0x01d9:
            r13 = 0
        L_0x01da:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.DatabaseMetaData.getColumnPrivileges(java.lang.String, java.lang.String, java.lang.String, java.lang.String):java.sql.ResultSet");
    }

    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        String columnNamePattern2;
        if (columnNamePattern != null) {
            columnNamePattern2 = columnNamePattern;
        } else if (this.conn.getNullNamePatternMatchesAll()) {
            columnNamePattern2 = "%";
        } else {
            throw SQLError.createSQLException("Column name pattern can not be NULL or empty.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
        final String colPattern = columnNamePattern2;
        Field[] fields = createColumnsFields();
        ArrayList<ResultSetRow> rows = new ArrayList<>();
        Statement stmt = this.conn.getMetadataSafeStatement();
        try {
            final String str = tableNamePattern;
            final String str2 = schemaPattern;
            final Statement statement = stmt;
            final ArrayList<ResultSetRow> arrayList = rows;
            new IterateBlock<String>(getCatalogIterator(catalog)) {
                /* JADX INFO: finally extract failed */
                /* access modifiers changed from: package-private */
                /* JADX WARNING: Removed duplicated region for block: B:128:0x032a A[Catch:{ all -> 0x0175, all -> 0x0400 }] */
                /* JADX WARNING: Removed duplicated region for block: B:129:0x032b A[Catch:{ all -> 0x0175, all -> 0x0400 }] */
                /* JADX WARNING: Removed duplicated region for block: B:133:0x0338 A[Catch:{ all -> 0x0175, all -> 0x0400 }] */
                /* JADX WARNING: Removed duplicated region for block: B:134:0x0346 A[Catch:{ all -> 0x0175, all -> 0x0400 }] */
                /* JADX WARNING: Removed duplicated region for block: B:139:0x038b A[Catch:{ all -> 0x0175, all -> 0x0400 }] */
                /* JADX WARNING: Removed duplicated region for block: B:165:0x040b A[SYNTHETIC, Splitter:B:165:0x040b] */
                /* JADX WARNING: Removed duplicated region for block: B:189:0x03bc A[SYNTHETIC] */
                /* JADX WARNING: Removed duplicated region for block: B:98:0x0244 A[Catch:{ all -> 0x0175, all -> 0x0400 }] */
                /* JADX WARNING: Removed duplicated region for block: B:99:0x0253 A[Catch:{ all -> 0x0175, all -> 0x0400 }] */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void forEach(java.lang.String r27) throws java.sql.SQLException {
                    /*
                        r26 = this;
                        r1 = r26
                        r2 = r27
                        java.lang.String r3 = "Extra"
                        java.lang.String r4 = " FROM "
                        java.lang.String r5 = "COLUMNS FROM "
                        java.lang.String r6 = "SHOW "
                        java.util.ArrayList r0 = new java.util.ArrayList
                        r0.<init>()
                        r7 = r0
                        java.lang.String r0 = r4
                        java.lang.String r8 = "TABLE_NAME"
                        java.lang.String r9 = "%"
                        r10 = 0
                        if (r0 != 0) goto L_0x0057
                        r11 = 0
                        com.mysql.jdbc.DatabaseMetaData r0 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0047 }
                        java.lang.String r12 = r5     // Catch:{ all -> 0x0047 }
                        java.lang.String[] r13 = new java.lang.String[r10]     // Catch:{ all -> 0x0047 }
                        java.sql.ResultSet r0 = r0.getTables(r2, r12, r9, r13)     // Catch:{ all -> 0x0047 }
                        r11 = r0
                    L_0x0027:
                        boolean r0 = r11.next()     // Catch:{ all -> 0x0047 }
                        if (r0 == 0) goto L_0x0036
                        java.lang.String r0 = r11.getString(r8)     // Catch:{ all -> 0x0047 }
                        r7.add(r0)     // Catch:{ all -> 0x0047 }
                        goto L_0x0027
                    L_0x0036:
                        if (r11 == 0) goto L_0x0044
                        r11.close()     // Catch:{ Exception -> 0x003d }
                        goto L_0x0043
                    L_0x003d:
                        r0 = move-exception
                        r8 = r0
                        r0 = r8
                        com.mysql.jdbc.AssertionFailedException.shouldNotHappen(r0)
                    L_0x0043:
                        r11 = 0
                    L_0x0044:
                        goto L_0x0088
                    L_0x0047:
                        r0 = move-exception
                        r3 = r0
                        if (r11 == 0) goto L_0x0056
                        r11.close()     // Catch:{ Exception -> 0x004f }
                        goto L_0x0055
                    L_0x004f:
                        r0 = move-exception
                        r4 = r0
                        r0 = r4
                        com.mysql.jdbc.AssertionFailedException.shouldNotHappen(r0)
                    L_0x0055:
                        r11 = 0
                    L_0x0056:
                        throw r3
                    L_0x0057:
                        r11 = 0
                        com.mysql.jdbc.DatabaseMetaData r12 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0413 }
                        java.lang.String r13 = r5     // Catch:{ all -> 0x0413 }
                        java.lang.String[] r14 = new java.lang.String[r10]     // Catch:{ all -> 0x0413 }
                        java.sql.ResultSet r0 = r12.getTables(r2, r13, r0, r14)     // Catch:{ all -> 0x0413 }
                        r11 = r0
                    L_0x0063:
                        boolean r0 = r11.next()     // Catch:{ all -> 0x0413 }
                        if (r0 == 0) goto L_0x0078
                        java.lang.String r0 = r11.getString(r8)     // Catch:{ all -> 0x0072 }
                        r7.add(r0)     // Catch:{ all -> 0x0072 }
                        goto L_0x0063
                    L_0x0072:
                        r0 = move-exception
                        r2 = r0
                        r18 = r7
                        goto L_0x0417
                    L_0x0078:
                        if (r11 == 0) goto L_0x0086
                        r11.close()     // Catch:{ SQLException -> 0x007f }
                        goto L_0x0085
                    L_0x007f:
                        r0 = move-exception
                        r8 = r0
                        r0 = r8
                        com.mysql.jdbc.AssertionFailedException.shouldNotHappen(r0)
                    L_0x0085:
                        r11 = 0
                    L_0x0086:
                    L_0x0088:
                        java.util.Iterator r8 = r7.iterator()
                    L_0x008c:
                        boolean r0 = r8.hasNext()
                        if (r0 == 0) goto L_0x0412
                        java.lang.Object r0 = r8.next()
                        r11 = r0
                        java.lang.String r11 = (java.lang.String) r11
                        r12 = 0
                        java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0403 }
                        r0.<init>(r6)     // Catch:{ all -> 0x0403 }
                        r13 = r0
                        com.mysql.jdbc.DatabaseMetaData r0 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0403 }
                        com.mysql.jdbc.MySQLConnection r0 = r0.conn     // Catch:{ all -> 0x0403 }
                        r14 = 4
                        r15 = 1
                        boolean r0 = r0.versionMeetsMinimum(r14, r15, r10)     // Catch:{ all -> 0x0403 }
                        java.lang.String r10 = "FULL "
                        if (r0 == 0) goto L_0x00ba
                        r13.append(r10)     // Catch:{ all -> 0x00b2 }
                        goto L_0x00ba
                    L_0x00b2:
                        r0 = move-exception
                        r2 = r0
                        r18 = r7
                        r21 = r8
                        goto L_0x0409
                    L_0x00ba:
                        r13.append(r5)     // Catch:{ all -> 0x0403 }
                        com.mysql.jdbc.DatabaseMetaData r0 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0403 }
                        java.lang.String r0 = r0.quotedId     // Catch:{ all -> 0x0403 }
                        com.mysql.jdbc.DatabaseMetaData r14 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0403 }
                        com.mysql.jdbc.MySQLConnection r14 = r14.conn     // Catch:{ all -> 0x0403 }
                        boolean r14 = r14.getPedantic()     // Catch:{ all -> 0x0403 }
                        java.lang.String r0 = com.mysql.jdbc.StringUtils.quoteIdentifier(r11, r0, r14)     // Catch:{ all -> 0x0403 }
                        r13.append(r0)     // Catch:{ all -> 0x0403 }
                        r13.append(r4)     // Catch:{ all -> 0x0403 }
                        com.mysql.jdbc.DatabaseMetaData r0 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0403 }
                        java.lang.String r0 = r0.quotedId     // Catch:{ all -> 0x0403 }
                        com.mysql.jdbc.DatabaseMetaData r14 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0403 }
                        com.mysql.jdbc.MySQLConnection r14 = r14.conn     // Catch:{ all -> 0x0403 }
                        boolean r14 = r14.getPedantic()     // Catch:{ all -> 0x0403 }
                        java.lang.String r0 = com.mysql.jdbc.StringUtils.quoteIdentifier(r2, r0, r14)     // Catch:{ all -> 0x0403 }
                        r13.append(r0)     // Catch:{ all -> 0x0403 }
                        java.lang.String r0 = " LIKE "
                        r13.append(r0)     // Catch:{ all -> 0x0403 }
                        java.lang.String r0 = r6     // Catch:{ all -> 0x0403 }
                        java.lang.String r14 = "'"
                        java.lang.String r0 = com.mysql.jdbc.StringUtils.quoteIdentifier(r0, r14, r15)     // Catch:{ all -> 0x0403 }
                        r13.append(r0)     // Catch:{ all -> 0x0403 }
                        r0 = 0
                        r14 = 0
                        java.lang.String r15 = r6     // Catch:{ all -> 0x0403 }
                        boolean r15 = r15.equals(r9)     // Catch:{ all -> 0x0403 }
                        r18 = r7
                        java.lang.String r7 = "Field"
                        if (r15 != 0) goto L_0x017b
                        r0 = 1
                        java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ all -> 0x0175 }
                        r15.<init>(r6)     // Catch:{ all -> 0x0175 }
                        r19 = r0
                        com.mysql.jdbc.DatabaseMetaData r0 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0175 }
                        com.mysql.jdbc.MySQLConnection r0 = r0.conn     // Catch:{ all -> 0x0175 }
                        r20 = r6
                        r21 = r8
                        r22 = r9
                        r6 = 4
                        r8 = 0
                        r9 = 1
                        boolean r0 = r0.versionMeetsMinimum(r6, r9, r8)     // Catch:{ all -> 0x0400 }
                        if (r0 == 0) goto L_0x0122
                        r15.append(r10)     // Catch:{ all -> 0x0400 }
                    L_0x0122:
                        r15.append(r5)     // Catch:{ all -> 0x0400 }
                        com.mysql.jdbc.DatabaseMetaData r0 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        java.lang.String r0 = r0.quotedId     // Catch:{ all -> 0x0400 }
                        com.mysql.jdbc.DatabaseMetaData r6 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        com.mysql.jdbc.MySQLConnection r6 = r6.conn     // Catch:{ all -> 0x0400 }
                        boolean r6 = r6.getPedantic()     // Catch:{ all -> 0x0400 }
                        java.lang.String r0 = com.mysql.jdbc.StringUtils.quoteIdentifier(r11, r0, r6)     // Catch:{ all -> 0x0400 }
                        r15.append(r0)     // Catch:{ all -> 0x0400 }
                        r15.append(r4)     // Catch:{ all -> 0x0400 }
                        com.mysql.jdbc.DatabaseMetaData r0 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        java.lang.String r0 = r0.quotedId     // Catch:{ all -> 0x0400 }
                        com.mysql.jdbc.DatabaseMetaData r6 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        com.mysql.jdbc.MySQLConnection r6 = r6.conn     // Catch:{ all -> 0x0400 }
                        boolean r6 = r6.getPedantic()     // Catch:{ all -> 0x0400 }
                        java.lang.String r0 = com.mysql.jdbc.StringUtils.quoteIdentifier(r2, r0, r6)     // Catch:{ all -> 0x0400 }
                        r15.append(r0)     // Catch:{ all -> 0x0400 }
                        java.sql.Statement r0 = r7     // Catch:{ all -> 0x0400 }
                        java.lang.String r6 = r15.toString()     // Catch:{ all -> 0x0400 }
                        java.sql.ResultSet r0 = r0.executeQuery(r6)     // Catch:{ all -> 0x0400 }
                        r12 = r0
                        java.util.HashMap r0 = new java.util.HashMap     // Catch:{ all -> 0x0400 }
                        r0.<init>()     // Catch:{ all -> 0x0400 }
                        r14 = r0
                        r0 = 1
                    L_0x0160:
                        boolean r6 = r12.next()     // Catch:{ all -> 0x0400 }
                        if (r6 == 0) goto L_0x0183
                        java.lang.String r6 = r12.getString(r7)     // Catch:{ all -> 0x0400 }
                        int r8 = r0 + 1
                        java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x0400 }
                        r14.put(r6, r0)     // Catch:{ all -> 0x0400 }
                        r0 = r8
                        goto L_0x0160
                    L_0x0175:
                        r0 = move-exception
                        r21 = r8
                        r2 = r0
                        goto L_0x0409
                    L_0x017b:
                        r20 = r6
                        r21 = r8
                        r22 = r9
                        r19 = r0
                    L_0x0183:
                        java.sql.Statement r0 = r7     // Catch:{ all -> 0x0400 }
                        java.lang.String r6 = r13.toString()     // Catch:{ all -> 0x0400 }
                        java.sql.ResultSet r0 = r0.executeQuery(r6)     // Catch:{ all -> 0x0400 }
                        r12 = r0
                        r0 = 1
                        r6 = r0
                    L_0x0190:
                        boolean r0 = r12.next()     // Catch:{ all -> 0x0400 }
                        if (r0 == 0) goto L_0x03e1
                        r0 = 24
                        byte[][] r0 = new byte[r0][]     // Catch:{ all -> 0x0400 }
                        r8 = r0
                        com.mysql.jdbc.DatabaseMetaData r0 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        byte[] r0 = r0.s2b(r2)     // Catch:{ all -> 0x0400 }
                        r9 = 0
                        r8[r9] = r0     // Catch:{ all -> 0x0400 }
                        r9 = 0
                        r10 = 1
                        r8[r10] = r9     // Catch:{ all -> 0x0400 }
                        r0 = 2
                        com.mysql.jdbc.DatabaseMetaData r10 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        byte[] r10 = r10.s2b(r11)     // Catch:{ all -> 0x0400 }
                        r8[r0] = r10     // Catch:{ all -> 0x0400 }
                        r0 = 3
                        byte[] r10 = r12.getBytes(r7)     // Catch:{ all -> 0x0400 }
                        r8[r0] = r10     // Catch:{ all -> 0x0400 }
                        com.mysql.jdbc.DatabaseMetaData$TypeDescriptor r0 = new com.mysql.jdbc.DatabaseMetaData$TypeDescriptor     // Catch:{ all -> 0x0400 }
                        com.mysql.jdbc.DatabaseMetaData r10 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        java.lang.String r15 = "Type"
                        java.lang.String r15 = r12.getString(r15)     // Catch:{ all -> 0x0400 }
                        java.lang.String r9 = "Null"
                        java.lang.String r9 = r12.getString(r9)     // Catch:{ all -> 0x0400 }
                        r0.<init>(r10, r15, r9)     // Catch:{ all -> 0x0400 }
                        r9 = r0
                        short r0 = r9.dataType     // Catch:{ all -> 0x0400 }
                        java.lang.String r0 = java.lang.Short.toString(r0)     // Catch:{ all -> 0x0400 }
                        byte[] r0 = r0.getBytes()     // Catch:{ all -> 0x0400 }
                        r10 = 4
                        r8[r10] = r0     // Catch:{ all -> 0x0400 }
                        r0 = 5
                        com.mysql.jdbc.DatabaseMetaData r10 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        java.lang.String r15 = r9.typeName     // Catch:{ all -> 0x0400 }
                        byte[] r10 = r10.s2b(r15)     // Catch:{ all -> 0x0400 }
                        r8[r0] = r10     // Catch:{ all -> 0x0400 }
                        java.lang.Integer r0 = r9.columnSize     // Catch:{ all -> 0x0400 }
                        java.lang.String r10 = "TEXT"
                        if (r0 != 0) goto L_0x01f2
                        r23 = 6
                        r24 = 0
                        r8[r23] = r24     // Catch:{ all -> 0x0400 }
                        goto L_0x026d
                    L_0x01f2:
                        java.lang.String r0 = "Collation"
                        java.lang.String r0 = r12.getString(r0)     // Catch:{ all -> 0x0400 }
                        r25 = 1
                        if (r0 == 0) goto L_0x023f
                        java.lang.String r15 = r9.typeName     // Catch:{ all -> 0x0400 }
                        boolean r15 = r10.equals(r15)     // Catch:{ all -> 0x0400 }
                        if (r15 != 0) goto L_0x0218
                        java.lang.String r15 = "TINYTEXT"
                        java.lang.String r2 = r9.typeName     // Catch:{ all -> 0x0400 }
                        boolean r2 = r15.equals(r2)     // Catch:{ all -> 0x0400 }
                        if (r2 != 0) goto L_0x0218
                        java.lang.String r2 = "MEDIUMTEXT"
                        java.lang.String r15 = r9.typeName     // Catch:{ all -> 0x0400 }
                        boolean r2 = r2.equals(r15)     // Catch:{ all -> 0x0400 }
                        if (r2 == 0) goto L_0x023f
                    L_0x0218:
                        java.lang.String r2 = "ucs2"
                        int r2 = r0.indexOf(r2)     // Catch:{ all -> 0x0400 }
                        r15 = -1
                        if (r2 > r15) goto L_0x023a
                        java.lang.String r2 = "utf16"
                        int r2 = r0.indexOf(r2)     // Catch:{ all -> 0x0400 }
                        if (r2 <= r15) goto L_0x022c
                        goto L_0x023a
                    L_0x022c:
                        java.lang.String r2 = "utf32"
                        int r2 = r0.indexOf(r2)     // Catch:{ all -> 0x0400 }
                        if (r2 <= r15) goto L_0x023f
                        r25 = 4
                        r2 = r25
                        goto L_0x0241
                    L_0x023a:
                        r25 = 2
                        r2 = r25
                        goto L_0x0241
                    L_0x023f:
                        r2 = r25
                    L_0x0241:
                        r15 = 1
                        if (r2 != r15) goto L_0x0253
                        com.mysql.jdbc.DatabaseMetaData r15 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        r25 = r0
                        java.lang.Integer r0 = r9.columnSize     // Catch:{ all -> 0x0400 }
                        java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0400 }
                        byte[] r0 = r15.s2b(r0)     // Catch:{ all -> 0x0400 }
                        goto L_0x026a
                    L_0x0253:
                        r25 = r0
                        com.mysql.jdbc.DatabaseMetaData r0 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        java.lang.Integer r15 = r9.columnSize     // Catch:{ all -> 0x0400 }
                        int r15 = r15.intValue()     // Catch:{ all -> 0x0400 }
                        int r15 = r15 / r2
                        java.lang.Integer r15 = java.lang.Integer.valueOf(r15)     // Catch:{ all -> 0x0400 }
                        java.lang.String r15 = r15.toString()     // Catch:{ all -> 0x0400 }
                        byte[] r0 = r0.s2b(r15)     // Catch:{ all -> 0x0400 }
                    L_0x026a:
                        r15 = 6
                        r8[r15] = r0     // Catch:{ all -> 0x0400 }
                    L_0x026d:
                        r0 = 7
                        com.mysql.jdbc.DatabaseMetaData r2 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        int r15 = r9.bufferLength     // Catch:{ all -> 0x0400 }
                        java.lang.String r15 = java.lang.Integer.toString(r15)     // Catch:{ all -> 0x0400 }
                        byte[] r2 = r2.s2b(r15)     // Catch:{ all -> 0x0400 }
                        r8[r0] = r2     // Catch:{ all -> 0x0400 }
                        r0 = 8
                        java.lang.Integer r2 = r9.decimalDigits     // Catch:{ all -> 0x0400 }
                        if (r2 != 0) goto L_0x0284
                        r2 = 0
                        goto L_0x0290
                    L_0x0284:
                        com.mysql.jdbc.DatabaseMetaData r2 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        java.lang.Integer r15 = r9.decimalDigits     // Catch:{ all -> 0x0400 }
                        java.lang.String r15 = r15.toString()     // Catch:{ all -> 0x0400 }
                        byte[] r2 = r2.s2b(r15)     // Catch:{ all -> 0x0400 }
                    L_0x0290:
                        r8[r0] = r2     // Catch:{ all -> 0x0400 }
                        r0 = 9
                        com.mysql.jdbc.DatabaseMetaData r2 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        int r15 = r9.numPrecRadix     // Catch:{ all -> 0x0400 }
                        java.lang.String r15 = java.lang.Integer.toString(r15)     // Catch:{ all -> 0x0400 }
                        byte[] r2 = r2.s2b(r15)     // Catch:{ all -> 0x0400 }
                        r8[r0] = r2     // Catch:{ all -> 0x0400 }
                        r0 = 10
                        com.mysql.jdbc.DatabaseMetaData r2 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        int r15 = r9.nullability     // Catch:{ all -> 0x0400 }
                        java.lang.String r15 = java.lang.Integer.toString(r15)     // Catch:{ all -> 0x0400 }
                        byte[] r2 = r2.s2b(r15)     // Catch:{ all -> 0x0400 }
                        r8[r0] = r2     // Catch:{ all -> 0x0400 }
                        com.mysql.jdbc.DatabaseMetaData r0 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ Exception -> 0x02d7 }
                        com.mysql.jdbc.MySQLConnection r0 = r0.conn     // Catch:{ Exception -> 0x02d7 }
                        r25 = r4
                        r2 = 0
                        r4 = 1
                        r15 = 4
                        boolean r0 = r0.versionMeetsMinimum(r15, r4, r2)     // Catch:{ Exception -> 0x02d5 }
                        if (r0 == 0) goto L_0x02cc
                        java.lang.String r0 = "Comment"
                        byte[] r0 = r12.getBytes(r0)     // Catch:{ Exception -> 0x02d5 }
                        r2 = 11
                        r8[r2] = r0     // Catch:{ Exception -> 0x02d5 }
                        goto L_0x02d4
                    L_0x02cc:
                        byte[] r0 = r12.getBytes(r3)     // Catch:{ Exception -> 0x02d5 }
                        r2 = 11
                        r8[r2] = r0     // Catch:{ Exception -> 0x02d5 }
                    L_0x02d4:
                        goto L_0x02e2
                    L_0x02d5:
                        r0 = move-exception
                        goto L_0x02db
                    L_0x02d7:
                        r0 = move-exception
                        r25 = r4
                        r15 = 4
                    L_0x02db:
                        r2 = 0
                        byte[] r4 = new byte[r2]     // Catch:{ all -> 0x0400 }
                        r2 = 11
                        r8[r2] = r4     // Catch:{ all -> 0x0400 }
                    L_0x02e2:
                        r0 = 12
                        java.lang.String r2 = "Default"
                        byte[] r2 = r12.getBytes(r2)     // Catch:{ all -> 0x0400 }
                        r8[r0] = r2     // Catch:{ all -> 0x0400 }
                        r0 = 13
                        r2 = 1
                        byte[] r4 = new byte[r2]     // Catch:{ all -> 0x0400 }
                        r17 = 48
                        r16 = 0
                        r4[r16] = r17     // Catch:{ all -> 0x0400 }
                        r8[r0] = r4     // Catch:{ all -> 0x0400 }
                        r0 = 14
                        byte[] r4 = new byte[r2]     // Catch:{ all -> 0x0400 }
                        r4[r16] = r17     // Catch:{ all -> 0x0400 }
                        r8[r0] = r4     // Catch:{ all -> 0x0400 }
                        java.lang.String r0 = r9.typeName     // Catch:{ all -> 0x0400 }
                        java.lang.String r4 = "CHAR"
                        int r0 = com.mysql.jdbc.StringUtils.indexOfIgnoreCase(r0, r4)     // Catch:{ all -> 0x0400 }
                        r4 = 15
                        r2 = -1
                        if (r0 != r2) goto L_0x032f
                        java.lang.String r0 = r9.typeName     // Catch:{ all -> 0x0400 }
                        java.lang.String r15 = "BLOB"
                        int r0 = com.mysql.jdbc.StringUtils.indexOfIgnoreCase(r0, r15)     // Catch:{ all -> 0x0400 }
                        if (r0 != r2) goto L_0x032f
                        java.lang.String r0 = r9.typeName     // Catch:{ all -> 0x0400 }
                        int r0 = com.mysql.jdbc.StringUtils.indexOfIgnoreCase(r0, r10)     // Catch:{ all -> 0x0400 }
                        if (r0 != r2) goto L_0x032f
                        java.lang.String r0 = r9.typeName     // Catch:{ all -> 0x0400 }
                        java.lang.String r10 = "BINARY"
                        int r0 = com.mysql.jdbc.StringUtils.indexOfIgnoreCase(r0, r10)     // Catch:{ all -> 0x0400 }
                        if (r0 == r2) goto L_0x032b
                        goto L_0x032f
                    L_0x032b:
                        r2 = 0
                        r8[r4] = r2     // Catch:{ all -> 0x0400 }
                        goto L_0x0334
                    L_0x032f:
                        r2 = 6
                        r0 = r8[r2]     // Catch:{ all -> 0x0400 }
                        r8[r4] = r0     // Catch:{ all -> 0x0400 }
                    L_0x0334:
                        r0 = 16
                        if (r19 != 0) goto L_0x0346
                        int r2 = r6 + 1
                        java.lang.String r4 = java.lang.Integer.toString(r6)     // Catch:{ all -> 0x0400 }
                        byte[] r4 = r4.getBytes()     // Catch:{ all -> 0x0400 }
                        r8[r0] = r4     // Catch:{ all -> 0x0400 }
                        r6 = r2
                        goto L_0x035c
                    L_0x0346:
                        java.lang.String r2 = r12.getString(r7)     // Catch:{ all -> 0x0400 }
                        java.lang.Object r4 = r14.get(r2)     // Catch:{ all -> 0x0400 }
                        java.lang.Integer r4 = (java.lang.Integer) r4     // Catch:{ all -> 0x0400 }
                        if (r4 == 0) goto L_0x03d2
                        java.lang.String r10 = r4.toString()     // Catch:{ all -> 0x0400 }
                        byte[] r10 = r10.getBytes()     // Catch:{ all -> 0x0400 }
                        r8[r0] = r10     // Catch:{ all -> 0x0400 }
                    L_0x035c:
                        r0 = 17
                        com.mysql.jdbc.DatabaseMetaData r2 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        java.lang.String r4 = r9.isNullable     // Catch:{ all -> 0x0400 }
                        byte[] r2 = r2.s2b(r4)     // Catch:{ all -> 0x0400 }
                        r8[r0] = r2     // Catch:{ all -> 0x0400 }
                        r0 = 18
                        r2 = 0
                        r8[r0] = r2     // Catch:{ all -> 0x0400 }
                        r0 = 19
                        r8[r0] = r2     // Catch:{ all -> 0x0400 }
                        r0 = 20
                        r8[r0] = r2     // Catch:{ all -> 0x0400 }
                        r0 = 21
                        r8[r0] = r2     // Catch:{ all -> 0x0400 }
                        com.mysql.jdbc.DatabaseMetaData r0 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        java.lang.String r2 = ""
                        byte[] r0 = r0.s2b(r2)     // Catch:{ all -> 0x0400 }
                        r2 = 22
                        r8[r2] = r0     // Catch:{ all -> 0x0400 }
                        java.lang.String r0 = r12.getString(r3)     // Catch:{ all -> 0x0400 }
                        if (r0 == 0) goto L_0x03bc
                        com.mysql.jdbc.DatabaseMetaData r4 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        java.lang.String r10 = "auto_increment"
                        int r10 = com.mysql.jdbc.StringUtils.indexOfIgnoreCase(r0, r10)     // Catch:{ all -> 0x0400 }
                        java.lang.String r15 = "YES"
                        java.lang.String r23 = "NO"
                        r2 = -1
                        if (r10 == r2) goto L_0x039c
                        r2 = r15
                        goto L_0x039e
                    L_0x039c:
                        r2 = r23
                    L_0x039e:
                        byte[] r2 = r4.s2b(r2)     // Catch:{ all -> 0x0400 }
                        r4 = 22
                        r8[r4] = r2     // Catch:{ all -> 0x0400 }
                        com.mysql.jdbc.DatabaseMetaData r4 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        java.lang.String r10 = "generated"
                        int r10 = com.mysql.jdbc.StringUtils.indexOfIgnoreCase(r0, r10)     // Catch:{ all -> 0x0400 }
                        r2 = -1
                        if (r10 == r2) goto L_0x03b2
                        goto L_0x03b4
                    L_0x03b2:
                        r15 = r23
                    L_0x03b4:
                        byte[] r2 = r4.s2b(r15)     // Catch:{ all -> 0x0400 }
                        r4 = 23
                        r8[r4] = r2     // Catch:{ all -> 0x0400 }
                    L_0x03bc:
                        java.util.ArrayList r2 = r8     // Catch:{ all -> 0x0400 }
                        com.mysql.jdbc.ByteArrayRow r4 = new com.mysql.jdbc.ByteArrayRow     // Catch:{ all -> 0x0400 }
                        com.mysql.jdbc.DatabaseMetaData r10 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        com.mysql.jdbc.ExceptionInterceptor r10 = r10.getExceptionInterceptor()     // Catch:{ all -> 0x0400 }
                        r4.<init>(r8, r10)     // Catch:{ all -> 0x0400 }
                        r2.add(r4)     // Catch:{ all -> 0x0400 }
                        r2 = r27
                        r4 = r25
                        goto L_0x0190
                    L_0x03d2:
                        java.lang.String r0 = "Can not find column in full column list to determine true ordinal position."
                        java.lang.String r3 = "S1000"
                        com.mysql.jdbc.DatabaseMetaData r5 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0400 }
                        com.mysql.jdbc.ExceptionInterceptor r5 = r5.getExceptionInterceptor()     // Catch:{ all -> 0x0400 }
                        java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r0, (java.lang.String) r3, (com.mysql.jdbc.ExceptionInterceptor) r5)     // Catch:{ all -> 0x0400 }
                        throw r0     // Catch:{ all -> 0x0400 }
                    L_0x03e1:
                        r25 = r4
                        r16 = 0
                        if (r12 == 0) goto L_0x03ee
                        r12.close()     // Catch:{ Exception -> 0x03ec }
                        goto L_0x03ed
                    L_0x03ec:
                        r0 = move-exception
                    L_0x03ed:
                        r12 = 0
                    L_0x03ee:
                        r2 = r27
                        r10 = r16
                        r7 = r18
                        r6 = r20
                        r8 = r21
                        r9 = r22
                        r4 = r25
                        goto L_0x008c
                    L_0x0400:
                        r0 = move-exception
                        r2 = r0
                        goto L_0x0409
                    L_0x0403:
                        r0 = move-exception
                        r18 = r7
                        r21 = r8
                        r2 = r0
                    L_0x0409:
                        if (r12 == 0) goto L_0x0411
                        r12.close()     // Catch:{ Exception -> 0x040f }
                        goto L_0x0410
                    L_0x040f:
                        r0 = move-exception
                    L_0x0410:
                        r12 = 0
                    L_0x0411:
                        throw r2
                    L_0x0412:
                        return
                    L_0x0413:
                        r0 = move-exception
                        r18 = r7
                        r2 = r0
                    L_0x0417:
                        if (r11 == 0) goto L_0x0424
                        r11.close()     // Catch:{ SQLException -> 0x041d }
                        goto L_0x0423
                    L_0x041d:
                        r0 = move-exception
                        r3 = r0
                        r0 = r3
                        com.mysql.jdbc.AssertionFailedException.shouldNotHappen(r0)
                    L_0x0423:
                        r11 = 0
                    L_0x0424:
                        throw r2
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.DatabaseMetaData.AnonymousClass2.forEach(java.lang.String):void");
                }
            }.doForAll();
            return buildResultSet(fields, rows);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    /* access modifiers changed from: protected */
    public Field[] createColumnsFields() {
        return new Field[]{new Field("", "TABLE_CAT", 1, 255), new Field("", "TABLE_SCHEM", 1, 0), new Field("", "TABLE_NAME", 1, 255), new Field("", "COLUMN_NAME", 1, 32), new Field("", "DATA_TYPE", 4, 5), new Field("", "TYPE_NAME", 1, 16), new Field("", "COLUMN_SIZE", 4, Integer.toString(Integer.MAX_VALUE).length()), new Field("", "BUFFER_LENGTH", 4, 10), new Field("", "DECIMAL_DIGITS", 4, 10), new Field("", "NUM_PREC_RADIX", 4, 10), new Field("", "NULLABLE", 4, 10), new Field("", "REMARKS", 1, 0), new Field("", "COLUMN_DEF", 1, 0), new Field("", "SQL_DATA_TYPE", 4, 10), new Field("", "SQL_DATETIME_SUB", 4, 10), new Field("", "CHAR_OCTET_LENGTH", 4, Integer.toString(Integer.MAX_VALUE).length()), new Field("", "ORDINAL_POSITION", 4, 10), new Field("", "IS_NULLABLE", 1, 3), new Field("", "SCOPE_CATALOG", 1, 255), new Field("", "SCOPE_SCHEMA", 1, 255), new Field("", "SCOPE_TABLE", 1, 255), new Field("", "SOURCE_DATA_TYPE", 5, 10), new Field("", "IS_AUTOINCREMENT", 1, 3), new Field("", "IS_GENERATEDCOLUMN", 1, 3)};
    }

    public Connection getConnection() throws SQLException {
        return this.conn;
    }

    public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
        if (primaryTable != null) {
            Field[] fields = createFkMetadataFields();
            ArrayList<ResultSetRow> tuples = new ArrayList<>();
            if (this.conn.versionMeetsMinimum(3, 23, 0)) {
                Statement stmt = this.conn.getMetadataSafeStatement();
                try {
                    final Statement statement = stmt;
                    final String str = foreignTable;
                    final String str2 = primaryTable;
                    final String str3 = foreignCatalog;
                    final String str4 = foreignSchema;
                    final String str5 = primaryCatalog;
                    final String str6 = primarySchema;
                    final ArrayList<ResultSetRow> arrayList = tuples;
                    new IterateBlock<String>(getCatalogIterator(foreignCatalog)) {
                        /* JADX INFO: finally extract failed */
                        /* access modifiers changed from: package-private */
                        public void forEach(String catalogStr) throws SQLException {
                            ResultSet fkresults;
                            Object obj;
                            String foreignTableWithCase;
                            String foreignTableWithCase2;
                            Object obj2;
                            String foreignTableWithCase3;
                            byte[] bArr;
                            String str = catalogStr;
                            ResultSet fkresults2 = null;
                            try {
                                Object obj3 = null;
                                if (DatabaseMetaData.this.conn.versionMeetsMinimum(3, 23, 50)) {
                                    fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(str, (String) null);
                                } else {
                                    fkresults = statement.executeQuery("SHOW TABLE STATUS FROM " + StringUtils.quoteIdentifier(str, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
                                }
                                String foreignTableWithCase4 = DatabaseMetaData.this.getTableNameWithCase(str);
                                String primaryTableWithCase = DatabaseMetaData.this.getTableNameWithCase(str2);
                                while (fkresults.next()) {
                                    String tableType = fkresults.getString("Type");
                                    if (tableType != null) {
                                        if (!tableType.equalsIgnoreCase("innodb")) {
                                            if (!tableType.equalsIgnoreCase(DatabaseMetaData.SUPPORTS_FK)) {
                                                foreignTableWithCase2 = foreignTableWithCase;
                                                obj2 = obj;
                                            }
                                        }
                                        String comment = fkresults.getString("Comment").trim();
                                        if (comment != null) {
                                            boolean z = false;
                                            StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
                                            if (commentTokens.hasMoreTokens()) {
                                                commentTokens.nextToken();
                                            }
                                            while (commentTokens.hasMoreTokens()) {
                                                String keys = commentTokens.nextToken();
                                                LocalAndReferencedColumns parsedInfo = DatabaseMetaData.this.parseTableStatusIntoLocalAndReferencedColumns(keys);
                                                int keySeq = 0;
                                                Iterator<String> referencedColumns = parsedInfo.referencedColumnsList.iterator();
                                                for (String unQuoteIdentifier : parsedInfo.localColumnsList) {
                                                    String referencingColumn = StringUtils.unQuoteIdentifier(unQuoteIdentifier, DatabaseMetaData.this.quotedId);
                                                    byte[][] tuple = new byte[14][];
                                                    String str2 = str3;
                                                    tuple[4] = str2 == null ? null : DatabaseMetaData.this.s2b(str2);
                                                    String str3 = str4;
                                                    tuple[5] = str3 == null ? null : DatabaseMetaData.this.s2b(str3);
                                                    String dummy = fkresults.getString("Name");
                                                    if (dummy.compareTo(foreignTableWithCase) != 0) {
                                                        foreignTableWithCase3 = foreignTableWithCase;
                                                    } else {
                                                        tuple[6] = DatabaseMetaData.this.s2b(dummy);
                                                        tuple[7] = DatabaseMetaData.this.s2b(referencingColumn);
                                                        String str4 = str5;
                                                        if (str4 == null) {
                                                            foreignTableWithCase3 = foreignTableWithCase;
                                                            bArr = null;
                                                        } else {
                                                            foreignTableWithCase3 = foreignTableWithCase;
                                                            bArr = DatabaseMetaData.this.s2b(str4);
                                                        }
                                                        tuple[0] = bArr;
                                                        String str5 = str6;
                                                        tuple[1] = str5 == null ? null : DatabaseMetaData.this.s2b(str5);
                                                        if (parsedInfo.referencedTable.compareTo(primaryTableWithCase) == 0) {
                                                            tuple[2] = DatabaseMetaData.this.s2b(parsedInfo.referencedTable);
                                                            String str6 = dummy;
                                                            tuple[3] = DatabaseMetaData.this.s2b(StringUtils.unQuoteIdentifier(referencedColumns.next(), DatabaseMetaData.this.quotedId));
                                                            tuple[8] = Integer.toString(keySeq).getBytes();
                                                            int[] actions = DatabaseMetaData.this.getForeignKeyActions(keys);
                                                            tuple[9] = Integer.toString(actions[1]).getBytes();
                                                            tuple[10] = Integer.toString(actions[0]).getBytes();
                                                            tuple[11] = null;
                                                            tuple[12] = null;
                                                            tuple[13] = Integer.toString(7).getBytes();
                                                            int[] iArr = actions;
                                                            arrayList.add(new ByteArrayRow(tuple, DatabaseMetaData.this.getExceptionInterceptor()));
                                                            keySeq++;
                                                            String str7 = catalogStr;
                                                            z = false;
                                                            obj = null;
                                                            foreignTableWithCase = foreignTableWithCase3;
                                                        }
                                                    }
                                                    String dummy2 = catalogStr;
                                                    foreignTableWithCase = foreignTableWithCase3;
                                                    obj = null;
                                                    z = false;
                                                }
                                                Object obj4 = obj;
                                                boolean z2 = z;
                                                String str8 = catalogStr;
                                            }
                                            foreignTableWithCase2 = foreignTableWithCase;
                                            obj2 = obj;
                                        } else {
                                            foreignTableWithCase2 = foreignTableWithCase;
                                            obj2 = obj;
                                        }
                                    } else {
                                        foreignTableWithCase2 = foreignTableWithCase;
                                        obj2 = obj;
                                    }
                                    String str9 = catalogStr;
                                    obj3 = obj2;
                                    foreignTableWithCase4 = foreignTableWithCase2;
                                }
                                if (fkresults != null) {
                                    try {
                                        fkresults.close();
                                    } catch (Exception e) {
                                        AssertionFailedException.shouldNotHappen(e);
                                    }
                                }
                            } catch (Throwable th) {
                                Throwable th2 = th;
                                if (fkresults2 != null) {
                                    try {
                                        fkresults2.close();
                                    } catch (Exception e2) {
                                        AssertionFailedException.shouldNotHappen(e2);
                                    }
                                }
                                throw th2;
                            }
                        }
                    }.doForAll();
                } finally {
                    if (stmt != null) {
                        stmt.close();
                    }
                }
            }
            return buildResultSet(fields, tuples);
        }
        throw SQLError.createSQLException("Table not specified.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
    }

    /* access modifiers changed from: protected */
    public Field[] createFkMetadataFields() {
        return new Field[]{new Field("", "PKTABLE_CAT", 1, 255), new Field("", "PKTABLE_SCHEM", 1, 0), new Field("", "PKTABLE_NAME", 1, 255), new Field("", "PKCOLUMN_NAME", 1, 32), new Field("", "FKTABLE_CAT", 1, 255), new Field("", "FKTABLE_SCHEM", 1, 0), new Field("", "FKTABLE_NAME", 1, 255), new Field("", "FKCOLUMN_NAME", 1, 32), new Field("", "KEY_SEQ", 5, 2), new Field("", "UPDATE_RULE", 5, 2), new Field("", "DELETE_RULE", 5, 2), new Field("", "FK_NAME", 1, 0), new Field("", "PK_NAME", 1, 0), new Field("", "DEFERRABILITY", 5, 2)};
    }

    public int getDatabaseMajorVersion() throws SQLException {
        return this.conn.getServerMajorVersion();
    }

    public int getDatabaseMinorVersion() throws SQLException {
        return this.conn.getServerMinorVersion();
    }

    public String getDatabaseProductName() throws SQLException {
        return "MySQL";
    }

    public String getDatabaseProductVersion() throws SQLException {
        return this.conn.getServerVersion();
    }

    public int getDefaultTransactionIsolation() throws SQLException {
        if (this.conn.supportsIsolationLevel()) {
            return 2;
        }
        return 0;
    }

    public int getDriverMajorVersion() {
        return NonRegisteringDriver.getMajorVersionInternal();
    }

    public int getDriverMinorVersion() {
        return NonRegisteringDriver.getMinorVersionInternal();
    }

    public String getDriverName() throws SQLException {
        return NonRegisteringDriver.NAME;
    }

    public String getDriverVersion() throws SQLException {
        return "mysql-connector-java-5.1.49 ( Revision: ad86f36e100e104cd926c6b81c8cab9565750116 )";
    }

    public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
        if (table != null) {
            Field[] fields = createFkMetadataFields();
            ArrayList<ResultSetRow> rows = new ArrayList<>();
            if (this.conn.versionMeetsMinimum(3, 23, 0)) {
                Statement stmt = this.conn.getMetadataSafeStatement();
                try {
                    final Statement statement = stmt;
                    final String str = table;
                    final ArrayList<ResultSetRow> arrayList = rows;
                    new IterateBlock<String>(getCatalogIterator(catalog)) {
                        /* access modifiers changed from: package-private */
                        public void forEach(String catalogStr) throws SQLException {
                            String comment;
                            ResultSet fkresults;
                            ResultSet fkresults2 = null;
                            try {
                                if (DatabaseMetaData.this.conn.versionMeetsMinimum(3, 23, 50)) {
                                    fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(catalogStr, (String) null);
                                } else {
                                    fkresults = statement.executeQuery("SHOW TABLE STATUS FROM " + StringUtils.quoteIdentifier(catalogStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
                                }
                                String tableNameWithCase = DatabaseMetaData.this.getTableNameWithCase(str);
                                while (fkresults2.next()) {
                                    String tableType = fkresults2.getString("Type");
                                    if (tableType != null && ((tableType.equalsIgnoreCase("innodb") || tableType.equalsIgnoreCase(DatabaseMetaData.SUPPORTS_FK)) && (comment = fkresults2.getString("Comment").trim()) != null)) {
                                        StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
                                        if (commentTokens.hasMoreTokens()) {
                                            commentTokens.nextToken();
                                            while (commentTokens.hasMoreTokens()) {
                                                String str = catalogStr;
                                                DatabaseMetaData.this.getExportKeyResults(str, tableNameWithCase, commentTokens.nextToken(), arrayList, fkresults2.getString("Name"));
                                            }
                                        }
                                    }
                                }
                            } finally {
                                if (fkresults2 != null) {
                                    try {
                                        fkresults2.close();
                                    } catch (SQLException sqlEx) {
                                        AssertionFailedException.shouldNotHappen(sqlEx);
                                    }
                                }
                            }
                        }
                    }.doForAll();
                } finally {
                    if (stmt != null) {
                        stmt.close();
                    }
                }
            }
            return buildResultSet(fields, rows);
        }
        throw SQLError.createSQLException("Table not specified.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
    }

    /* access modifiers changed from: protected */
    public void getExportKeyResults(String catalog, String exportingTable, String keysComment, List<ResultSetRow> tuples, String fkTableName) throws SQLException {
        getResultsImpl(catalog, exportingTable, keysComment, tuples, fkTableName, true);
    }

    public String getExtraNameCharacters() throws SQLException {
        return "#@";
    }

    /* access modifiers changed from: protected */
    public int[] getForeignKeyActions(String commentString) {
        int[] actions = {3, 3};
        int lastParenIndex = commentString.lastIndexOf(")");
        if (lastParenIndex != commentString.length() - 1) {
            String cascadeOptions = commentString.substring(lastParenIndex + 1).trim().toUpperCase(Locale.ENGLISH);
            actions[0] = getCascadeDeleteOption(cascadeOptions);
            actions[1] = getCascadeUpdateOption(cascadeOptions);
        }
        return actions;
    }

    public String getIdentifierQuoteString() throws SQLException {
        if (this.conn.supportsQuotedIdentifiers()) {
            return this.conn.useAnsiQuotedIdentifiers() ? "\"" : "`";
        }
        return " ";
    }

    public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
        if (table != null) {
            Field[] fields = createFkMetadataFields();
            ArrayList<ResultSetRow> rows = new ArrayList<>();
            if (this.conn.versionMeetsMinimum(3, 23, 0)) {
                Statement stmt = this.conn.getMetadataSafeStatement();
                try {
                    final String str = table;
                    final Statement statement = stmt;
                    final ArrayList<ResultSetRow> arrayList = rows;
                    new IterateBlock<String>(getCatalogIterator(catalog)) {
                        /* access modifiers changed from: package-private */
                        public void forEach(String catalogStr) throws SQLException {
                            String comment;
                            ResultSet fkresults;
                            ResultSet fkresults2 = null;
                            try {
                                if (DatabaseMetaData.this.conn.versionMeetsMinimum(3, 23, 50)) {
                                    fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(catalogStr, str);
                                } else {
                                    fkresults = statement.executeQuery("SHOW TABLE STATUS " + " FROM " + StringUtils.quoteIdentifier(catalogStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()) + " LIKE " + StringUtils.quoteIdentifier(str, "'", true));
                                }
                                while (fkresults2.next()) {
                                    String tableType = fkresults2.getString("Type");
                                    if (tableType != null && ((tableType.equalsIgnoreCase("innodb") || tableType.equalsIgnoreCase(DatabaseMetaData.SUPPORTS_FK)) && (comment = fkresults2.getString("Comment").trim()) != null)) {
                                        StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
                                        if (commentTokens.hasMoreTokens()) {
                                            commentTokens.nextToken();
                                            while (commentTokens.hasMoreTokens()) {
                                                DatabaseMetaData.this.getImportKeyResults(catalogStr, str, commentTokens.nextToken(), arrayList);
                                            }
                                        }
                                    }
                                }
                            } finally {
                                if (fkresults2 != null) {
                                    try {
                                        fkresults2.close();
                                    } catch (SQLException sqlEx) {
                                        AssertionFailedException.shouldNotHappen(sqlEx);
                                    }
                                }
                            }
                        }
                    }.doForAll();
                } finally {
                    if (stmt != null) {
                        stmt.close();
                    }
                }
            }
            return buildResultSet(fields, rows);
        }
        throw SQLError.createSQLException("Table not specified.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
    }

    /* access modifiers changed from: protected */
    public void getImportKeyResults(String catalog, String importingTable, String keysComment, List<ResultSetRow> tuples) throws SQLException {
        getResultsImpl(catalog, importingTable, keysComment, tuples, (String) null, false);
    }

    public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
        Field[] fields = createIndexInfoFields();
        final SortedMap<IndexMetaDataKey, ResultSetRow> sortedRows = new TreeMap<>();
        ArrayList arrayList = new ArrayList();
        Statement stmt = this.conn.getMetadataSafeStatement();
        try {
            final String str = table;
            final Statement statement = stmt;
            final boolean z = unique;
            new IterateBlock<String>(getCatalogIterator(catalog)) {
                /* access modifiers changed from: package-private */
                public void forEach(String catalogStr) throws SQLException {
                    String str;
                    DatabaseMetaData databaseMetaData;
                    long cardinality;
                    String str2 = catalogStr;
                    ResultSet results = null;
                    try {
                        results = statement.executeQuery("SHOW INDEX FROM " + StringUtils.quoteIdentifier(str, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()) + " FROM " + StringUtils.quoteIdentifier(str2, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
                    } catch (SQLException sqlEx) {
                        int errorCode = sqlEx.getErrorCode();
                        if (!SQLError.SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND.equals(sqlEx.getSQLState())) {
                            if (errorCode != 1146) {
                                throw sqlEx;
                            }
                        }
                    } catch (Throwable th) {
                        Throwable th2 = th;
                        if (results != null) {
                            try {
                                results.close();
                            } catch (Exception e) {
                            }
                        }
                        throw th2;
                    }
                    while (results != null && results.next()) {
                        byte[][] row = new byte[14][];
                        row[0] = str2 == null ? new byte[0] : DatabaseMetaData.this.s2b(str2);
                        row[1] = null;
                        row[2] = results.getBytes("Table");
                        boolean indexIsUnique = results.getInt("Non_unique") == 0;
                        if (!indexIsUnique) {
                            databaseMetaData = DatabaseMetaData.this;
                            str = "true";
                        } else {
                            databaseMetaData = DatabaseMetaData.this;
                            str = "false";
                        }
                        row[3] = databaseMetaData.s2b(str);
                        row[4] = new byte[0];
                        row[5] = results.getBytes("Key_name");
                        row[6] = Integer.toString(3).getBytes();
                        row[7] = results.getBytes("Seq_in_index");
                        row[8] = results.getBytes("Column_name");
                        row[9] = results.getBytes("Collation");
                        long cardinality2 = results.getLong("Cardinality");
                        if (Util.isJdbc42() || cardinality2 <= 2147483647L) {
                            cardinality = cardinality2;
                        } else {
                            cardinality = 2147483647L;
                        }
                        row[10] = DatabaseMetaData.this.s2b(String.valueOf(cardinality));
                        row[11] = DatabaseMetaData.this.s2b("0");
                        row[12] = null;
                        IndexMetaDataKey indexInfoKey = new IndexMetaDataKey(!indexIsUnique, 3, results.getString("Key_name").toLowerCase(), results.getShort("Seq_in_index"));
                        if (!z) {
                            sortedRows.put(indexInfoKey, new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor()));
                        } else if (indexIsUnique) {
                            sortedRows.put(indexInfoKey, new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor()));
                        }
                    }
                    if (results != null) {
                        try {
                            results.close();
                        } catch (Exception e2) {
                        }
                    }
                }
            }.doForAll();
            for (ResultSetRow add : sortedRows.values()) {
                arrayList.add(add);
            }
            ResultSet buildResultSet = buildResultSet(fields, arrayList);
            ResultSet resultSet = buildResultSet;
            return buildResultSet;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    /* access modifiers changed from: protected */
    public Field[] createIndexInfoFields() {
        Field[] fields = new Field[13];
        fields[0] = new Field("", "TABLE_CAT", 1, 255);
        fields[1] = new Field("", "TABLE_SCHEM", 1, 0);
        fields[2] = new Field("", "TABLE_NAME", 1, 255);
        fields[3] = new Field("", "NON_UNIQUE", 16, 4);
        fields[4] = new Field("", "INDEX_QUALIFIER", 1, 1);
        fields[5] = new Field("", "INDEX_NAME", 1, 32);
        fields[6] = new Field("", "TYPE", 5, 32);
        fields[7] = new Field("", "ORDINAL_POSITION", 5, 5);
        fields[8] = new Field("", "COLUMN_NAME", 1, 32);
        fields[9] = new Field("", "ASC_OR_DESC", 1, 1);
        if (Util.isJdbc42()) {
            fields[10] = new Field("", "CARDINALITY", -5, 20);
            fields[11] = new Field("", "PAGES", -5, 20);
        } else {
            fields[10] = new Field("", "CARDINALITY", 4, 20);
            fields[11] = new Field("", "PAGES", 4, 10);
        }
        fields[12] = new Field("", "FILTER_CONDITION", 1, 32);
        return fields;
    }

    public int getJDBCMajorVersion() throws SQLException {
        return 4;
    }

    public int getJDBCMinorVersion() throws SQLException {
        return 0;
    }

    public int getMaxBinaryLiteralLength() throws SQLException {
        return 16777208;
    }

    public int getMaxCatalogNameLength() throws SQLException {
        return 32;
    }

    public int getMaxCharLiteralLength() throws SQLException {
        return 16777208;
    }

    public int getMaxColumnNameLength() throws SQLException {
        return 64;
    }

    public int getMaxColumnsInGroupBy() throws SQLException {
        return 64;
    }

    public int getMaxColumnsInIndex() throws SQLException {
        return 16;
    }

    public int getMaxColumnsInOrderBy() throws SQLException {
        return 64;
    }

    public int getMaxColumnsInSelect() throws SQLException {
        return 256;
    }

    public int getMaxColumnsInTable() throws SQLException {
        return 512;
    }

    public int getMaxConnections() throws SQLException {
        return 0;
    }

    public int getMaxCursorNameLength() throws SQLException {
        return 64;
    }

    public int getMaxIndexLength() throws SQLException {
        return 256;
    }

    public int getMaxProcedureNameLength() throws SQLException {
        return 0;
    }

    public int getMaxRowSize() throws SQLException {
        return 2147483639;
    }

    public int getMaxSchemaNameLength() throws SQLException {
        return 0;
    }

    public int getMaxStatementLength() throws SQLException {
        return MysqlIO.getMaxBuf() - 4;
    }

    public int getMaxStatements() throws SQLException {
        return 0;
    }

    public int getMaxTableNameLength() throws SQLException {
        return 64;
    }

    public int getMaxTablesInSelect() throws SQLException {
        return 256;
    }

    public int getMaxUserNameLength() throws SQLException {
        return 16;
    }

    public String getNumericFunctions() throws SQLException {
        return "ABS,ACOS,ASIN,ATAN,ATAN2,BIT_COUNT,CEILING,COS,COT,DEGREES,EXP,FLOOR,LOG,LOG10,MAX,MIN,MOD,PI,POW,POWER,RADIANS,RAND,ROUND,SIN,SQRT,TAN,TRUNCATE";
    }

    public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
        Field[] fields = {new Field("", "TABLE_CAT", 1, 255), new Field("", "TABLE_SCHEM", 1, 0), new Field("", "TABLE_NAME", 1, 255), new Field("", "COLUMN_NAME", 1, 32), new Field("", "KEY_SEQ", 5, 5), new Field("", "PK_NAME", 1, 32)};
        if (table != null) {
            ArrayList<ResultSetRow> rows = new ArrayList<>();
            Statement stmt = this.conn.getMetadataSafeStatement();
            try {
                final String str = table;
                final Statement statement = stmt;
                final ArrayList<ResultSetRow> arrayList = rows;
                new IterateBlock<String>(getCatalogIterator(catalog)) {
                    /* access modifiers changed from: package-private */
                    public void forEach(String catalogStr) throws SQLException {
                        ResultSet rs = null;
                        try {
                            rs = statement.executeQuery("SHOW KEYS FROM " + StringUtils.quoteIdentifier(str, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()) + " FROM " + StringUtils.quoteIdentifier(catalogStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
                            TreeMap<String, byte[][]> sortMap = new TreeMap<>();
                            while (rs.next()) {
                                String keyType = rs.getString("Key_name");
                                if (keyType != null && (keyType.equalsIgnoreCase("PRIMARY") || keyType.equalsIgnoreCase("PRI"))) {
                                    byte[][] tuple = new byte[6][];
                                    tuple[0] = catalogStr == null ? new byte[0] : DatabaseMetaData.this.s2b(catalogStr);
                                    tuple[1] = null;
                                    tuple[2] = DatabaseMetaData.this.s2b(str);
                                    String columnName = rs.getString("Column_name");
                                    tuple[3] = DatabaseMetaData.this.s2b(columnName);
                                    tuple[4] = DatabaseMetaData.this.s2b(rs.getString("Seq_in_index"));
                                    tuple[5] = DatabaseMetaData.this.s2b(keyType);
                                    sortMap.put(columnName, tuple);
                                }
                            }
                            for (byte[][] byteArrayRow : sortMap.values()) {
                                arrayList.add(new ByteArrayRow(byteArrayRow, DatabaseMetaData.this.getExceptionInterceptor()));
                            }
                        } finally {
                            if (rs != null) {
                                try {
                                    rs.close();
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }.doForAll();
                return buildResultSet(fields, rows);
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        } else {
            throw SQLError.createSQLException("Table not specified.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
    }

    public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
        return getProcedureOrFunctionColumns(createProcedureColumnsFields(), catalog, schemaPattern, procedureNamePattern, columnNamePattern, true, true);
    }

    /* access modifiers changed from: protected */
    public Field[] createProcedureColumnsFields() {
        return new Field[]{new Field("", "PROCEDURE_CAT", 1, 512), new Field("", "PROCEDURE_SCHEM", 1, 512), new Field("", "PROCEDURE_NAME", 1, 512), new Field("", "COLUMN_NAME", 1, 512), new Field("", "COLUMN_TYPE", 1, 64), new Field("", "DATA_TYPE", 5, 6), new Field("", "TYPE_NAME", 1, 64), new Field("", "PRECISION", 4, 12), new Field("", "LENGTH", 4, 12), new Field("", "SCALE", 5, 12), new Field("", "RADIX", 5, 6), new Field("", "NULLABLE", 5, 6), new Field("", "REMARKS", 1, 512), new Field("", "COLUMN_DEF", 1, 512), new Field("", "SQL_DATA_TYPE", 4, 12), new Field("", "SQL_DATETIME_SUB", 4, 12), new Field("", "CHAR_OCTET_LENGTH", 4, 12), new Field("", "ORDINAL_POSITION", 4, 12), new Field("", "IS_NULLABLE", 1, 512), new Field("", "SPECIFIC_NAME", 1, 512)};
    }

    /* access modifiers changed from: protected */
    public ResultSet getProcedureOrFunctionColumns(Field[] fields, String catalog, String schemaPattern, String procedureOrFunctionNamePattern, String columnNamePattern, boolean returnProcedures, boolean returnFunctions) throws SQLException {
        int idx;
        String procNameToCall;
        String catalog2;
        Field[] fieldArr = fields;
        String str = procedureOrFunctionNamePattern;
        List<ComparableWrapper<String, ProcedureType>> procsOrFuncsToExtractList = new ArrayList<>();
        ResultSet procsAndOrFuncsRs = null;
        if (supportsStoredProcedures()) {
            String tmpProcedureOrFunctionNamePattern = null;
            if (str != null) {
                try {
                    if (!str.equals("%")) {
                        tmpProcedureOrFunctionNamePattern = StringUtils.sanitizeProcOrFuncName(procedureOrFunctionNamePattern);
                    }
                } catch (Throwable th) {
                    Throwable th2 = th;
                    SQLException rethrowSqlEx = null;
                    if (procsAndOrFuncsRs != null) {
                        try {
                            procsAndOrFuncsRs.close();
                        } catch (SQLException e) {
                            rethrowSqlEx = e;
                        }
                    }
                    if (rethrowSqlEx != null) {
                        throw rethrowSqlEx;
                    }
                    throw th2;
                }
            }
            if (tmpProcedureOrFunctionNamePattern == null) {
                tmpProcedureOrFunctionNamePattern = procedureOrFunctionNamePattern;
            } else {
                List<String> parseList = StringUtils.splitDBdotName(tmpProcedureOrFunctionNamePattern, catalog, this.quotedId, this.conn.isNoBackslashEscapesSet());
                if (parseList.size() == 2) {
                    String tmpCatalog = parseList.get(0);
                    tmpProcedureOrFunctionNamePattern = parseList.get(1);
                }
            }
            ResultSet procsAndOrFuncsRs2 = getProceduresAndOrFunctions(createFieldMetadataForGetProcedures(), catalog, schemaPattern, tmpProcedureOrFunctionNamePattern, returnProcedures, returnFunctions);
            boolean hasResults = false;
            while (procsAndOrFuncsRs2.next()) {
                procsOrFuncsToExtractList.add(new ComparableWrapper(getFullyQualifiedName(procsAndOrFuncsRs2.getString(1), procsAndOrFuncsRs2.getString(3)), procsAndOrFuncsRs2.getShort(8) == 1 ? ProcedureType.PROCEDURE : ProcedureType.FUNCTION));
                hasResults = true;
            }
            if (hasResults) {
                Collections.sort(procsOrFuncsToExtractList);
            }
            SQLException rethrowSqlEx2 = null;
            if (procsAndOrFuncsRs2 != null) {
                try {
                    procsAndOrFuncsRs2.close();
                } catch (SQLException e2) {
                    rethrowSqlEx2 = e2;
                }
            }
            if (rethrowSqlEx2 != null) {
                throw rethrowSqlEx2;
            }
        }
        ArrayList<ResultSetRow> resultRows = new ArrayList<>();
        Object obj = "";
        String catalog3 = catalog;
        for (ComparableWrapper<String, ProcedureType> procOrFunc : procsOrFuncsToExtractList) {
            String procName = procOrFunc.getKey();
            ProcedureType procType = procOrFunc.getValue();
            if (!" ".equals(this.quotedId)) {
                String str2 = this.quotedId;
                idx = StringUtils.indexOfIgnoreCase(0, procName, ".", str2, str2, this.conn.isNoBackslashEscapesSet() ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
            } else {
                idx = procName.indexOf(".");
            }
            if (idx > 0) {
                catalog2 = StringUtils.unQuoteIdentifier(procName.substring(0, idx), this.quotedId);
                procNameToCall = procName;
            } else {
                catalog2 = catalog3;
                procNameToCall = procName;
            }
            String str3 = procName;
            getCallStmtParameterTypes(catalog2, procNameToCall, procType, columnNamePattern, resultRows, fieldArr.length == 17);
            catalog3 = catalog2;
            int i = idx;
        }
        return buildResultSet(fieldArr, resultRows);
    }

    public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
        return getProceduresAndOrFunctions(createFieldMetadataForGetProcedures(), catalog, schemaPattern, procedureNamePattern, true, true);
    }

    /* access modifiers changed from: protected */
    public Field[] createFieldMetadataForGetProcedures() {
        return new Field[]{new Field("", "PROCEDURE_CAT", 1, 255), new Field("", "PROCEDURE_SCHEM", 1, 255), new Field("", "PROCEDURE_NAME", 1, 255), new Field("", "reserved1", 1, 0), new Field("", "reserved2", 1, 0), new Field("", "reserved3", 1, 0), new Field("", "REMARKS", 1, 255), new Field("", "PROCEDURE_TYPE", 5, 6), new Field("", "SPECIFIC_NAME", 1, 255)};
    }

    /* access modifiers changed from: protected */
    public ResultSet getProceduresAndOrFunctions(Field[] fields, String catalog, String schemaPattern, String procedureNamePattern, boolean returnProcedures, boolean returnFunctions) throws SQLException {
        String procedureNamePattern2;
        if (procedureNamePattern != null && procedureNamePattern.length() != 0) {
            procedureNamePattern2 = procedureNamePattern;
        } else if (this.conn.getNullNamePatternMatchesAll()) {
            procedureNamePattern2 = "%";
        } else {
            Field[] fieldArr = fields;
            String str = catalog;
            throw SQLError.createSQLException("Procedure name pattern can not be NULL or empty.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
        ArrayList arrayList = new ArrayList();
        if (supportsStoredProcedures()) {
            final String procNamePattern = procedureNamePattern2;
            final List<ComparableWrapper<String, ResultSetRow>> procedureRowsToSort = new ArrayList<>();
            String str2 = catalog;
            final boolean z = returnProcedures;
            final boolean z2 = returnFunctions;
            final Field[] fieldArr2 = fields;
            new IterateBlock<String>(getCatalogIterator(catalog)) {
                /* access modifiers changed from: package-private */
                /* JADX WARNING: Code restructure failed: missing block: B:34:0x0093, code lost:
                    r15 = 1;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:35:0x0095, code lost:
                    r0 = move-exception;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:36:0x0096, code lost:
                    r7 = r0;
                    r3 = r11;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:82:?, code lost:
                    r3.close();
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:83:0x0133, code lost:
                    r0 = move-exception;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:84:0x0134, code lost:
                    r8 = r0;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:87:?, code lost:
                    r6.close();
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:88:0x013d, code lost:
                    r0 = move-exception;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:89:0x013e, code lost:
                    r8 = r0;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:91:0x0143, code lost:
                    throw r8;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:92:0x0145, code lost:
                    throw r7;
                 */
                /* JADX WARNING: Failed to process nested try/catch */
                /* JADX WARNING: Removed duplicated region for block: B:35:0x0095 A[ExcHandler: all (r0v19 'th' java.sql.SQLException A[CUSTOM_DECLARE]), Splitter:B:21:0x0063] */
                /* JADX WARNING: Removed duplicated region for block: B:43:0x00aa A[Catch:{ all -> 0x012a }] */
                /* JADX WARNING: Removed duplicated region for block: B:44:0x00ac A[Catch:{ all -> 0x012a }] */
                /* JADX WARNING: Removed duplicated region for block: B:47:0x00b1 A[Catch:{ all -> 0x012a }] */
                /* JADX WARNING: Removed duplicated region for block: B:53:0x00da  */
                /* JADX WARNING: Removed duplicated region for block: B:57:0x00df A[Catch:{ all -> 0x012a }] */
                /* JADX WARNING: Removed duplicated region for block: B:65:0x0113 A[SYNTHETIC, Splitter:B:65:0x0113] */
                /* JADX WARNING: Removed duplicated region for block: B:70:0x011d A[SYNTHETIC, Splitter:B:70:0x011d] */
                /* JADX WARNING: Removed duplicated region for block: B:75:0x0128 A[RETURN] */
                /* JADX WARNING: Removed duplicated region for block: B:76:0x0129  */
                /* JADX WARNING: Removed duplicated region for block: B:81:0x012f A[SYNTHETIC, Splitter:B:81:0x012f] */
                /* JADX WARNING: Removed duplicated region for block: B:86:0x0139 A[SYNTHETIC, Splitter:B:86:0x0139] */
                /* JADX WARNING: Removed duplicated region for block: B:91:0x0143  */
                /* JADX WARNING: Removed duplicated region for block: B:92:0x0145  */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void forEach(java.lang.String r24) throws java.sql.SQLException {
                    /*
                        r23 = this;
                        r1 = r23
                        r2 = r24
                        r3 = 0
                        r4 = 1
                        java.lang.StringBuilder r0 = new java.lang.StringBuilder
                        r0.<init>()
                        r5 = r0
                        java.lang.String r0 = "SELECT name, type, comment FROM mysql.proc WHERE "
                        r5.append(r0)
                        boolean r0 = r3
                        if (r0 == 0) goto L_0x0020
                        boolean r6 = r4
                        if (r6 != 0) goto L_0x0020
                        java.lang.String r0 = "type = 'PROCEDURE' AND "
                        r5.append(r0)
                        goto L_0x002c
                    L_0x0020:
                        if (r0 != 0) goto L_0x002c
                        boolean r0 = r4
                        if (r0 == 0) goto L_0x002c
                        java.lang.String r0 = "type = 'FUNCTION' AND "
                        r5.append(r0)
                    L_0x002c:
                        java.lang.String r0 = "name LIKE ? AND db <=> ? ORDER BY name, type"
                        r5.append(r0)
                        com.mysql.jdbc.DatabaseMetaData r0 = com.mysql.jdbc.DatabaseMetaData.this
                        java.lang.String r6 = r5.toString()
                        java.sql.PreparedStatement r6 = r0.prepareMetaDataSafeStatement(r6)
                        r7 = 2
                        if (r2 == 0) goto L_0x0051
                        com.mysql.jdbc.DatabaseMetaData r0 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x012a }
                        com.mysql.jdbc.MySQLConnection r0 = r0.conn     // Catch:{ all -> 0x012a }
                        boolean r0 = r0.lowerCaseTableNames()     // Catch:{ all -> 0x012a }
                        if (r0 == 0) goto L_0x004d
                        java.lang.String r0 = r2.toLowerCase()     // Catch:{ all -> 0x012a }
                        r2 = r0
                    L_0x004d:
                        r6.setString(r7, r2)     // Catch:{ all -> 0x012a }
                        goto L_0x0056
                    L_0x0051:
                        r0 = 12
                        r6.setNull(r7, r0)     // Catch:{ all -> 0x012a }
                    L_0x0056:
                        r20 = 1
                        java.lang.String r0 = r5     // Catch:{ all -> 0x012a }
                        r15 = 1
                        r6.setString(r15, r0)     // Catch:{ all -> 0x012a }
                        java.sql.ResultSet r11 = r6.executeQuery()     // Catch:{ SQLException -> 0x009d }
                        r4 = 0
                        boolean r0 = r3     // Catch:{ SQLException -> 0x009a, all -> 0x0095 }
                        if (r0 == 0) goto L_0x0076
                        com.mysql.jdbc.DatabaseMetaData r8 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ SQLException -> 0x009a, all -> 0x0095 }
                        r9 = 1
                        java.util.List r14 = r6     // Catch:{ SQLException -> 0x009a, all -> 0x0095 }
                        r10 = r2
                        r12 = r4
                        r13 = r2
                        r3 = r15
                        r15 = r20
                        r8.convertToJdbcProcedureList(r9, r10, r11, r12, r13, r14, r15)     // Catch:{ SQLException -> 0x0092, all -> 0x0095 }
                        goto L_0x0077
                    L_0x0076:
                        r3 = r15
                    L_0x0077:
                        boolean r0 = r4     // Catch:{ SQLException -> 0x0092, all -> 0x0095 }
                        if (r0 == 0) goto L_0x008f
                        com.mysql.jdbc.DatabaseMetaData r12 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ SQLException -> 0x0092, all -> 0x0095 }
                        java.util.List r0 = r6     // Catch:{ SQLException -> 0x0092, all -> 0x0095 }
                        com.mysql.jdbc.Field[] r8 = r7     // Catch:{ SQLException -> 0x0092, all -> 0x0095 }
                        r13 = r2
                        r14 = r11
                        r15 = r4
                        r16 = r2
                        r17 = r0
                        r18 = r20
                        r19 = r8
                        r12.convertToJdbcFunctionList(r13, r14, r15, r16, r17, r18, r19)     // Catch:{ SQLException -> 0x0092, all -> 0x0095 }
                    L_0x008f:
                        r3 = r11
                        goto L_0x010f
                    L_0x0092:
                        r0 = move-exception
                        r15 = r3
                        goto L_0x009b
                    L_0x0095:
                        r0 = move-exception
                        r7 = r0
                        r3 = r11
                        goto L_0x012c
                    L_0x009a:
                        r0 = move-exception
                    L_0x009b:
                        r3 = r11
                        goto L_0x009e
                    L_0x009d:
                        r0 = move-exception
                    L_0x009e:
                        com.mysql.jdbc.DatabaseMetaData r8 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x012a }
                        com.mysql.jdbc.MySQLConnection r8 = r8.conn     // Catch:{ all -> 0x012a }
                        r9 = 5
                        r10 = 0
                        boolean r8 = r8.versionMeetsMinimum(r9, r10, r15)     // Catch:{ all -> 0x012a }
                        if (r8 == 0) goto L_0x00ac
                        r14 = r7
                        goto L_0x00ad
                    L_0x00ac:
                        r14 = r15
                    L_0x00ad:
                        boolean r7 = r4     // Catch:{ all -> 0x012a }
                        if (r7 == 0) goto L_0x00da
                        r6.close()     // Catch:{ all -> 0x012a }
                        com.mysql.jdbc.DatabaseMetaData r7 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x012a }
                        java.lang.String r8 = "SHOW FUNCTION STATUS LIKE ?"
                        java.sql.PreparedStatement r7 = r7.prepareMetaDataSafeStatement(r8)     // Catch:{ all -> 0x012a }
                        r6 = r7
                        java.lang.String r7 = r5     // Catch:{ all -> 0x012a }
                        r6.setString(r15, r7)     // Catch:{ all -> 0x012a }
                        java.sql.ResultSet r10 = r6.executeQuery()     // Catch:{ all -> 0x012a }
                        com.mysql.jdbc.DatabaseMetaData r8 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x00d6 }
                        java.util.List r13 = r6     // Catch:{ all -> 0x00d6 }
                        com.mysql.jdbc.Field[] r3 = r7     // Catch:{ all -> 0x00d6 }
                        r9 = r2
                        r11 = r4
                        r12 = r2
                        r7 = r15
                        r15 = r3
                        r8.convertToJdbcFunctionList(r9, r10, r11, r12, r13, r14, r15)     // Catch:{ all -> 0x00d6 }
                        r3 = r10
                        goto L_0x00db
                    L_0x00d6:
                        r0 = move-exception
                        r7 = r0
                        r3 = r10
                        goto L_0x012c
                    L_0x00da:
                        r7 = r15
                    L_0x00db:
                        boolean r8 = r3     // Catch:{ all -> 0x012a }
                        if (r8 == 0) goto L_0x010f
                        r6.close()     // Catch:{ all -> 0x012a }
                        com.mysql.jdbc.DatabaseMetaData r8 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x012a }
                        java.lang.String r9 = "SHOW PROCEDURE STATUS LIKE ?"
                        java.sql.PreparedStatement r8 = r8.prepareMetaDataSafeStatement(r9)     // Catch:{ all -> 0x012a }
                        r6 = r8
                        java.lang.String r8 = r5     // Catch:{ all -> 0x012a }
                        r6.setString(r7, r8)     // Catch:{ all -> 0x012a }
                        java.sql.ResultSet r18 = r6.executeQuery()     // Catch:{ all -> 0x012a }
                        com.mysql.jdbc.DatabaseMetaData r15 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x010a }
                        r16 = 0
                        java.util.List r3 = r6     // Catch:{ all -> 0x010a }
                        r17 = r2
                        r19 = r4
                        r20 = r2
                        r21 = r3
                        r22 = r14
                        r15.convertToJdbcProcedureList(r16, r17, r18, r19, r20, r21, r22)     // Catch:{ all -> 0x010a }
                        r3 = r18
                        goto L_0x010f
                    L_0x010a:
                        r0 = move-exception
                        r7 = r0
                        r3 = r18
                        goto L_0x012c
                    L_0x010f:
                        r7 = 0
                        if (r3 == 0) goto L_0x011b
                        r3.close()     // Catch:{ SQLException -> 0x0117 }
                        goto L_0x011b
                    L_0x0117:
                        r0 = move-exception
                        r8 = r0
                        r0 = r8
                        r7 = r0
                    L_0x011b:
                        if (r6 == 0) goto L_0x0125
                        r6.close()     // Catch:{ SQLException -> 0x0121 }
                        goto L_0x0125
                    L_0x0121:
                        r0 = move-exception
                        r8 = r0
                        r0 = r8
                        r7 = r0
                    L_0x0125:
                        if (r7 != 0) goto L_0x0129
                        return
                    L_0x0129:
                        throw r7
                    L_0x012a:
                        r0 = move-exception
                        r7 = r0
                    L_0x012c:
                        r8 = 0
                        if (r3 == 0) goto L_0x0137
                        r3.close()     // Catch:{ SQLException -> 0x0133 }
                        goto L_0x0137
                    L_0x0133:
                        r0 = move-exception
                        r9 = r0
                        r0 = r9
                        r8 = r0
                    L_0x0137:
                        if (r6 == 0) goto L_0x0141
                        r6.close()     // Catch:{ SQLException -> 0x013d }
                        goto L_0x0141
                    L_0x013d:
                        r0 = move-exception
                        r9 = r0
                        r0 = r9
                        r8 = r0
                    L_0x0141:
                        if (r8 == 0) goto L_0x0144
                        throw r8
                    L_0x0144:
                        throw r7
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.DatabaseMetaData.AnonymousClass8.forEach(java.lang.String):void");
                }
            }.doForAll();
            Collections.sort(procedureRowsToSort);
            for (ComparableWrapper<String, ResultSetRow> procRow : procedureRowsToSort) {
                arrayList.add(procRow.getValue());
            }
        } else {
            String str3 = catalog;
        }
        Field[] fieldArr3 = fields;
        return buildResultSet(fields, arrayList);
    }

    public String getProcedureTerm() throws SQLException {
        return "PROCEDURE";
    }

    public int getResultSetHoldability() throws SQLException {
        return 1;
    }

    private void getResultsImpl(String catalog, String table, String keysComment, List<ResultSetRow> tuples, String fkTableName, boolean isExport) throws SQLException {
        String str;
        String str2 = keysComment;
        LocalAndReferencedColumns parsedInfo = parseTableStatusIntoLocalAndReferencedColumns(str2);
        if (isExport) {
            str = table;
            if (!parsedInfo.referencedTable.equals(str)) {
                return;
            }
        } else {
            str = table;
        }
        if (parsedInfo.localColumnsList.size() == parsedInfo.referencedColumnsList.size()) {
            Iterator<String> referColumnNames = parsedInfo.referencedColumnsList.iterator();
            int keySeqIndex = 1;
            for (String unQuoteIdentifier : parsedInfo.localColumnsList) {
                byte[][] tuple = new byte[14][];
                String lColumnName = StringUtils.unQuoteIdentifier(unQuoteIdentifier, this.quotedId);
                String rColumnName = StringUtils.unQuoteIdentifier(referColumnNames.next(), this.quotedId);
                tuple[4] = catalog == null ? new byte[0] : s2b(catalog);
                tuple[5] = null;
                tuple[6] = s2b(isExport ? fkTableName : str);
                tuple[7] = s2b(lColumnName);
                tuple[0] = s2b(parsedInfo.referencedCatalog);
                tuple[1] = null;
                tuple[2] = s2b(isExport ? str : parsedInfo.referencedTable);
                tuple[3] = s2b(rColumnName);
                int keySeqIndex2 = keySeqIndex + 1;
                tuple[8] = s2b(Integer.toString(keySeqIndex));
                int[] actions = getForeignKeyActions(str2);
                tuple[9] = s2b(Integer.toString(actions[1]));
                tuple[10] = s2b(Integer.toString(actions[0]));
                tuple[11] = s2b(parsedInfo.constraintName);
                tuple[12] = null;
                tuple[13] = s2b(Integer.toString(7));
                tuples.add(new ByteArrayRow(tuple, getExceptionInterceptor()));
                keySeqIndex = keySeqIndex2;
            }
            List<ResultSetRow> list = tuples;
            return;
        }
        List<ResultSetRow> list2 = tuples;
        throw SQLError.createSQLException("Error parsing foreign keys definition, number of local and referenced columns is not the same.", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
    }

    public ResultSet getSchemas() throws SQLException {
        return buildResultSet(new Field[]{new Field("", "TABLE_SCHEM", 1, 0), new Field("", "TABLE_CATALOG", 1, 0)}, new ArrayList<>());
    }

    public String getSchemaTerm() throws SQLException {
        return "";
    }

    public String getSearchStringEscape() throws SQLException {
        return "\\";
    }

    public String getSQLKeywords() throws SQLException {
        if (mysqlKeywords != null) {
            return mysqlKeywords;
        }
        synchronized (DatabaseMetaData.class) {
            if (mysqlKeywords != null) {
                String str = mysqlKeywords;
                return str;
            }
            Set<String> mysqlKeywordSet = new TreeSet<>();
            StringBuilder mysqlKeywordsBuffer = new StringBuilder();
            Collections.addAll(mysqlKeywordSet, MYSQL_KEYWORDS);
            mysqlKeywordSet.removeAll(Arrays.asList(Util.isJdbc4() ? SQL2003_KEYWORDS : SQL92_KEYWORDS));
            for (String keyword : mysqlKeywordSet) {
                mysqlKeywordsBuffer.append(",").append(keyword);
            }
            mysqlKeywords = mysqlKeywordsBuffer.substring(1);
            String str2 = mysqlKeywords;
            return str2;
        }
    }

    public int getSQLStateType() throws SQLException {
        return (!this.conn.versionMeetsMinimum(4, 1, 0) && !this.conn.getUseSqlStateCodes()) ? 1 : 2;
    }

    public String getStringFunctions() throws SQLException {
        return "ASCII,BIN,BIT_LENGTH,CHAR,CHARACTER_LENGTH,CHAR_LENGTH,CONCAT,CONCAT_WS,CONV,ELT,EXPORT_SET,FIELD,FIND_IN_SET,HEX,INSERT,INSTR,LCASE,LEFT,LENGTH,LOAD_FILE,LOCATE,LOCATE,LOWER,LPAD,LTRIM,MAKE_SET,MATCH,MID,OCT,OCTET_LENGTH,ORD,POSITION,QUOTE,REPEAT,REPLACE,REVERSE,RIGHT,RPAD,RTRIM,SOUNDEX,SPACE,STRCMP,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING_INDEX,TRIM,UCASE,UPPER";
    }

    public ResultSet getSuperTables(String arg0, String arg1, String arg2) throws SQLException {
        return buildResultSet(new Field[]{new Field("", "TABLE_CAT", 1, 32), new Field("", "TABLE_SCHEM", 1, 32), new Field("", "TABLE_NAME", 1, 32), new Field("", "SUPERTABLE_NAME", 1, 32)}, new ArrayList());
    }

    public ResultSet getSuperTypes(String arg0, String arg1, String arg2) throws SQLException {
        return buildResultSet(new Field[]{new Field("", "TYPE_CAT", 1, 32), new Field("", "TYPE_SCHEM", 1, 32), new Field("", "TYPE_NAME", 1, 32), new Field("", "SUPERTYPE_CAT", 1, 32), new Field("", "SUPERTYPE_SCHEM", 1, 32), new Field("", "SUPERTYPE_NAME", 1, 32)}, new ArrayList());
    }

    public String getSystemFunctions() throws SQLException {
        return "DATABASE,USER,SYSTEM_USER,SESSION_USER,PASSWORD,ENCRYPT,LAST_INSERT_ID,VERSION";
    }

    /* access modifiers changed from: protected */
    public String getTableNameWithCase(String table) {
        return this.conn.lowerCaseTableNames() ? table.toLowerCase() : table;
    }

    /*  JADX ERROR: NullPointerException in pass: CodeShrinkVisitor
        java.lang.NullPointerException
        	at jadx.core.dex.instructions.args.InsnArg.wrapInstruction(InsnArg.java:118)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.inline(CodeShrinkVisitor.java:146)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkBlock(CodeShrinkVisitor.java:71)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkMethod(CodeShrinkVisitor.java:43)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.visit(CodeShrinkVisitor.java:35)
        */
    public java.sql.ResultSet getTablePrivileges(java.lang.String r31, java.lang.String r32, java.lang.String r33) throws java.sql.SQLException {
        /*
            r30 = this;
            r1 = r30
            r2 = r31
            if (r33 != 0) goto L_0x001f
            com.mysql.jdbc.MySQLConnection r0 = r1.conn
            boolean r0 = r0.getNullNamePatternMatchesAll()
            if (r0 == 0) goto L_0x0012
            java.lang.String r0 = "%"
            r3 = r0
            goto L_0x0021
        L_0x0012:
            com.mysql.jdbc.ExceptionInterceptor r0 = r30.getExceptionInterceptor()
            java.lang.String r3 = "Table name pattern can not be NULL or empty."
            java.lang.String r4 = "S1009"
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r3, (java.lang.String) r4, (com.mysql.jdbc.ExceptionInterceptor) r0)
            throw r0
        L_0x001f:
            r3 = r33
        L_0x0021:
            r0 = 7
            com.mysql.jdbc.Field[] r4 = new com.mysql.jdbc.Field[r0]
            com.mysql.jdbc.Field r0 = new com.mysql.jdbc.Field
            java.lang.String r5 = ""
            java.lang.String r6 = "TABLE_CAT"
            r7 = 1
            r8 = 64
            r0.<init>(r5, r6, r7, r8)
            r6 = 0
            r4[r6] = r0
            com.mysql.jdbc.Field r0 = new com.mysql.jdbc.Field
            java.lang.String r9 = "TABLE_SCHEM"
            r0.<init>(r5, r9, r7, r7)
            r4[r7] = r0
            com.mysql.jdbc.Field r0 = new com.mysql.jdbc.Field
            java.lang.String r9 = "TABLE_NAME"
            r0.<init>(r5, r9, r7, r8)
            r9 = 2
            r4[r9] = r0
            com.mysql.jdbc.Field r0 = new com.mysql.jdbc.Field
            java.lang.String r10 = "GRANTOR"
            r11 = 77
            r0.<init>(r5, r10, r7, r11)
            r10 = 3
            r4[r10] = r0
            com.mysql.jdbc.Field r0 = new com.mysql.jdbc.Field
            java.lang.String r12 = "GRANTEE"
            r0.<init>(r5, r12, r7, r11)
            r11 = 4
            r4[r11] = r0
            com.mysql.jdbc.Field r0 = new com.mysql.jdbc.Field
            java.lang.String r12 = "PRIVILEGE"
            r0.<init>(r5, r12, r7, r8)
            r8 = 5
            r4[r8] = r0
            com.mysql.jdbc.Field r0 = new com.mysql.jdbc.Field
            java.lang.String r12 = "IS_GRANTABLE"
            r0.<init>(r5, r12, r7, r10)
            r5 = 6
            r4[r5] = r0
            java.lang.String r12 = "SELECT host,db,table_name,grantor,user,table_priv FROM mysql.tables_priv WHERE db LIKE ? AND table_name LIKE ?"
            r13 = 0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r14 = r0
            r15 = 0
            java.sql.PreparedStatement r0 = r1.prepareMetaDataSafeStatement(r12)     // Catch:{ all -> 0x0269 }
            r15 = r0
            java.lang.String r6 = "%"
            if (r2 == 0) goto L_0x0093
            int r0 = r31.length()     // Catch:{ all -> 0x008b }
            if (r0 == 0) goto L_0x0093
            r0 = r2
            goto L_0x0094
        L_0x008b:
            r0 = move-exception
            r7 = r32
            r2 = r0
            r18 = r3
            goto L_0x026f
        L_0x0093:
            r0 = r6
        L_0x0094:
            r15.setString(r7, r0)     // Catch:{ all -> 0x0269 }
            r15.setString(r9, r3)     // Catch:{ all -> 0x0269 }
            java.sql.ResultSet r0 = r15.executeQuery()     // Catch:{ all -> 0x0269 }
            r13 = r0
        L_0x009f:
            boolean r0 = r13.next()     // Catch:{ all -> 0x0269 }
            if (r0 == 0) goto L_0x024e
            java.lang.String r0 = r13.getString(r7)     // Catch:{ all -> 0x0269 }
            r16 = r0
            java.lang.String r0 = r13.getString(r9)     // Catch:{ all -> 0x0269 }
            r17 = r0
            java.lang.String r0 = r13.getString(r10)     // Catch:{ all -> 0x0269 }
            r18 = r0
            java.lang.String r0 = r13.getString(r11)     // Catch:{ all -> 0x0269 }
            r19 = r0
            java.lang.String r0 = r13.getString(r8)     // Catch:{ all -> 0x0269 }
            if (r0 == 0) goto L_0x00cc
            int r20 = r0.length()     // Catch:{ all -> 0x008b }
            if (r20 != 0) goto L_0x00ca
            goto L_0x00cc
        L_0x00ca:
            r8 = r0
            goto L_0x00ce
        L_0x00cc:
            r0 = r6
            r8 = r0
        L_0x00ce:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0269 }
            r0.<init>(r8)     // Catch:{ all -> 0x0269 }
            r21 = r0
            r11 = r16
            if (r11 == 0) goto L_0x00ec
            com.mysql.jdbc.MySQLConnection r0 = r1.conn     // Catch:{ all -> 0x008b }
            boolean r0 = r0.getUseHostsInPrivileges()     // Catch:{ all -> 0x008b }
            if (r0 == 0) goto L_0x00ec
            java.lang.String r0 = "@"
            r10 = r21
            r10.append(r0)     // Catch:{ all -> 0x008b }
            r10.append(r11)     // Catch:{ all -> 0x008b }
            goto L_0x00ee
        L_0x00ec:
            r10 = r21
        L_0x00ee:
            java.lang.String r0 = r13.getString(r5)     // Catch:{ all -> 0x0269 }
            if (r0 == 0) goto L_0x0224
            java.util.Locale r5 = java.util.Locale.ENGLISH     // Catch:{ all -> 0x0269 }
            java.lang.String r5 = r0.toUpperCase(r5)     // Catch:{ all -> 0x0269 }
            java.util.StringTokenizer r0 = new java.util.StringTokenizer     // Catch:{ all -> 0x0269 }
            java.lang.String r9 = ","
            r0.<init>(r5, r9)     // Catch:{ all -> 0x0269 }
            r9 = r0
        L_0x0102:
            boolean r0 = r9.hasMoreTokens()     // Catch:{ all -> 0x0269 }
            if (r0 == 0) goto L_0x020b
            java.lang.String r0 = r9.nextToken()     // Catch:{ all -> 0x0269 }
            java.lang.String r0 = r0.trim()     // Catch:{ all -> 0x0269 }
            r24 = r0
            r25 = 0
            r7 = r32
            r29 = r18
            r18 = r3
            r3 = r29
            java.sql.ResultSet r0 = r1.getColumns(r2, r7, r3, r6)     // Catch:{ all -> 0x01f1 }
            r25 = r0
        L_0x0122:
            boolean r0 = r25.next()     // Catch:{ all -> 0x01f1 }
            if (r0 == 0) goto L_0x01c4
            r0 = 8
            byte[][] r0 = new byte[r0][]     // Catch:{ all -> 0x01f1 }
            r2 = r17
            byte[] r17 = r1.s2b(r2)     // Catch:{ all -> 0x01b9 }
            r27 = 0
            r0[r27] = r17     // Catch:{ all -> 0x01b9 }
            r17 = 0
            r26 = 1
            r0[r26] = r17     // Catch:{ all -> 0x01b9 }
            byte[] r28 = r1.s2b(r3)     // Catch:{ all -> 0x01b9 }
            r23 = 2
            r0[r23] = r28     // Catch:{ all -> 0x01b9 }
            r28 = r2
            r2 = r19
            if (r2 == 0) goto L_0x015d
            byte[] r19 = r1.s2b(r2)     // Catch:{ all -> 0x0153 }
            r16 = 3
            r0[r16] = r19     // Catch:{ all -> 0x0153 }
            goto L_0x0161
        L_0x0153:
            r0 = move-exception
            r33 = r2
            r22 = r3
            r17 = r24
            r2 = r0
            goto L_0x01fb
        L_0x015d:
            r16 = 3
            r0[r16] = r17     // Catch:{ all -> 0x01b0 }
        L_0x0161:
            r33 = r2
            java.lang.String r2 = r10.toString()     // Catch:{ all -> 0x01a9 }
            byte[] r2 = r1.s2b(r2)     // Catch:{ all -> 0x01a9 }
            r19 = 4
            r0[r19] = r2     // Catch:{ all -> 0x01a9 }
            r2 = r24
            byte[] r22 = r1.s2b(r2)     // Catch:{ all -> 0x01a2 }
            r20 = 5
            r0[r20] = r22     // Catch:{ all -> 0x01a2 }
            r21 = 6
            r0[r21] = r17     // Catch:{ all -> 0x01a2 }
            r17 = r2
            com.mysql.jdbc.ByteArrayRow r2 = new com.mysql.jdbc.ByteArrayRow     // Catch:{ all -> 0x019c }
            r22 = r3
            com.mysql.jdbc.ExceptionInterceptor r3 = r30.getExceptionInterceptor()     // Catch:{ all -> 0x0198 }
            r2.<init>(r0, r3)     // Catch:{ all -> 0x0198 }
            r14.add(r2)     // Catch:{ all -> 0x0198 }
            r2 = r31
            r19 = r33
            r24 = r17
            r3 = r22
            r17 = r28
            goto L_0x0122
        L_0x0198:
            r0 = move-exception
            r2 = r0
            goto L_0x01fb
        L_0x019c:
            r0 = move-exception
            r22 = r3
            r2 = r0
            goto L_0x01fb
        L_0x01a2:
            r0 = move-exception
            r17 = r2
            r22 = r3
            r2 = r0
            goto L_0x01fb
        L_0x01a9:
            r0 = move-exception
            r22 = r3
            r17 = r24
            r2 = r0
            goto L_0x01fb
        L_0x01b0:
            r0 = move-exception
            r33 = r2
            r22 = r3
            r17 = r24
            r2 = r0
            goto L_0x01fb
        L_0x01b9:
            r0 = move-exception
            r28 = r2
            r22 = r3
            r33 = r19
            r17 = r24
            r2 = r0
            goto L_0x01fb
        L_0x01c4:
            r22 = r3
            r28 = r17
            r33 = r19
            r17 = r24
            r16 = 3
            r19 = 4
            r20 = 5
            r21 = 6
            r23 = 2
            r26 = 1
            r27 = 0
            if (r25 == 0) goto L_0x01e1
            r25.close()     // Catch:{ Exception -> 0x01e0 }
            goto L_0x01e1
        L_0x01e0:
            r0 = move-exception
        L_0x01e1:
            r2 = r31
            r19 = r33
            r3 = r18
            r18 = r22
            r7 = r26
            r17 = r28
            goto L_0x0102
        L_0x01f1:
            r0 = move-exception
            r22 = r3
            r28 = r17
            r33 = r19
            r17 = r24
            r2 = r0
        L_0x01fb:
            if (r25 == 0) goto L_0x0207
            r25.close()     // Catch:{ Exception -> 0x0205 }
            goto L_0x0207
        L_0x0201:
            r0 = move-exception
            r2 = r0
            goto L_0x026f
        L_0x0205:
            r0 = move-exception
            goto L_0x0208
        L_0x0207:
        L_0x0208:
            throw r2     // Catch:{ all -> 0x0201 }
        L_0x020b:
            r26 = r7
            r28 = r17
            r22 = r18
            r33 = r19
            r16 = 3
            r19 = 4
            r20 = 5
            r21 = 6
            r23 = 2
            r27 = 0
            r7 = r32
            r18 = r3
            goto L_0x023c
        L_0x0224:
            r21 = r5
            r26 = r7
            r23 = r9
            r28 = r17
            r22 = r18
            r33 = r19
            r16 = 3
            r19 = 4
            r20 = 5
            r27 = 0
            r7 = r32
            r18 = r3
        L_0x023c:
            r2 = r31
            r10 = r16
            r3 = r18
            r11 = r19
            r8 = r20
            r5 = r21
            r9 = r23
            r7 = r26
            goto L_0x009f
        L_0x024e:
            r7 = r32
            r18 = r3
            if (r13 == 0) goto L_0x025a
            r13.close()     // Catch:{ Exception -> 0x0258 }
            goto L_0x0259
        L_0x0258:
            r0 = move-exception
        L_0x0259:
            r13 = 0
        L_0x025a:
            if (r15 == 0) goto L_0x0262
            r15.close()     // Catch:{ Exception -> 0x0260 }
            goto L_0x0261
        L_0x0260:
            r0 = move-exception
        L_0x0261:
            r15 = 0
        L_0x0262:
            java.sql.ResultSet r0 = r1.buildResultSet(r4, r14)
            return r0
        L_0x0269:
            r0 = move-exception
            r7 = r32
            r18 = r3
            r2 = r0
        L_0x026f:
            if (r13 == 0) goto L_0x0277
            r13.close()     // Catch:{ Exception -> 0x0275 }
            goto L_0x0276
        L_0x0275:
            r0 = move-exception
        L_0x0276:
            r13 = 0
        L_0x0277:
            if (r15 == 0) goto L_0x027f
            r15.close()     // Catch:{ Exception -> 0x027d }
            goto L_0x027e
        L_0x027d:
            r0 = move-exception
        L_0x027e:
            r15 = 0
        L_0x027f:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.DatabaseMetaData.getTablePrivileges(java.lang.String, java.lang.String, java.lang.String):java.sql.ResultSet");
    }

    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        String tableNamePattern2;
        String tmpCat;
        String tableNamePat;
        if (tableNamePattern != null) {
            tableNamePattern2 = tableNamePattern;
        } else if (this.conn.getNullNamePatternMatchesAll()) {
            tableNamePattern2 = "%";
        } else {
            throw SQLError.createSQLException("Table name pattern can not be NULL or empty.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
        final SortedMap<TableMetaDataKey, ResultSetRow> sortedRows = new TreeMap<>();
        ArrayList arrayList = new ArrayList();
        Statement stmt = this.conn.getMetadataSafeStatement();
        if (catalog != null && catalog.length() != 0) {
            tmpCat = catalog;
        } else if (this.conn.getNullCatalogMeansCurrent()) {
            tmpCat = this.database;
        } else {
            tmpCat = "";
        }
        List<String> parseList = StringUtils.splitDBdotName(tableNamePattern2, tmpCat, this.quotedId, this.conn.isNoBackslashEscapesSet());
        if (parseList.size() == 2) {
            tableNamePat = parseList.get(1);
        } else {
            tableNamePat = tableNamePattern2;
        }
        try {
            final Statement statement = stmt;
            final String str = tableNamePat;
            final String[] strArr = types;
            new IterateBlock<String>(getCatalogIterator(catalog)) {
                /* access modifiers changed from: package-private */
                /* JADX WARNING: Removed duplicated region for block: B:105:0x034c A[SYNTHETIC, Splitter:B:105:0x034c] */
                /* JADX WARNING: Removed duplicated region for block: B:146:? A[RETURN, SYNTHETIC] */
                /* JADX WARNING: Removed duplicated region for block: B:46:0x00f1  */
                /* JADX WARNING: Removed duplicated region for block: B:57:0x011d  */
                /* JADX WARNING: Removed duplicated region for block: B:61:0x012a A[Catch:{ all -> 0x0354 }] */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void forEach(java.lang.String r29) throws java.sql.SQLException {
                    /*
                        r28 = this;
                        r1 = r28
                        r8 = r29
                        java.lang.String r0 = "information_schema"
                        boolean r0 = r0.equalsIgnoreCase(r8)
                        r9 = 0
                        r10 = 1
                        if (r0 != 0) goto L_0x0021
                        java.lang.String r0 = "mysql"
                        boolean r0 = r0.equalsIgnoreCase(r8)
                        if (r0 != 0) goto L_0x0021
                        java.lang.String r0 = "performance_schema"
                        boolean r0 = r0.equalsIgnoreCase(r8)
                        if (r0 == 0) goto L_0x001f
                        goto L_0x0021
                    L_0x001f:
                        r0 = r9
                        goto L_0x0022
                    L_0x0021:
                        r0 = r10
                    L_0x0022:
                        r11 = r0
                        r2 = 0
                        java.sql.Statement r0 = r4     // Catch:{ SQLException -> 0x035b }
                        java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ SQLException -> 0x035b }
                        r3.<init>()     // Catch:{ SQLException -> 0x035b }
                        com.mysql.jdbc.DatabaseMetaData r4 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ SQLException -> 0x035b }
                        com.mysql.jdbc.MySQLConnection r4 = r4.conn     // Catch:{ SQLException -> 0x035b }
                        r12 = 2
                        r13 = 5
                        boolean r4 = r4.versionMeetsMinimum(r13, r9, r12)     // Catch:{ SQLException -> 0x035b }
                        if (r4 != 0) goto L_0x003a
                        java.lang.String r4 = "SHOW TABLES FROM "
                        goto L_0x003c
                    L_0x003a:
                        java.lang.String r4 = "SHOW FULL TABLES FROM "
                    L_0x003c:
                        java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ SQLException -> 0x035b }
                        com.mysql.jdbc.DatabaseMetaData r4 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ SQLException -> 0x035b }
                        java.lang.String r4 = r4.quotedId     // Catch:{ SQLException -> 0x035b }
                        com.mysql.jdbc.DatabaseMetaData r5 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ SQLException -> 0x035b }
                        com.mysql.jdbc.MySQLConnection r5 = r5.conn     // Catch:{ SQLException -> 0x035b }
                        boolean r5 = r5.getPedantic()     // Catch:{ SQLException -> 0x035b }
                        java.lang.String r4 = com.mysql.jdbc.StringUtils.quoteIdentifier(r8, r4, r5)     // Catch:{ SQLException -> 0x035b }
                        java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ SQLException -> 0x035b }
                        java.lang.String r4 = " LIKE "
                        java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ SQLException -> 0x035b }
                        java.lang.String r4 = r5     // Catch:{ SQLException -> 0x035b }
                        java.lang.String r5 = "'"
                        java.lang.String r4 = com.mysql.jdbc.StringUtils.quoteIdentifier(r4, r5, r10)     // Catch:{ SQLException -> 0x035b }
                        java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ SQLException -> 0x035b }
                        java.lang.String r3 = r3.toString()     // Catch:{ SQLException -> 0x035b }
                        java.sql.ResultSet r0 = r0.executeQuery(r3)     // Catch:{ SQLException -> 0x035b }
                        r14 = r0
                        r0 = 0
                        r2 = 0
                        r3 = 0
                        r4 = 0
                        r5 = 0
                        java.lang.String[] r6 = r6     // Catch:{ all -> 0x0354 }
                        if (r6 == 0) goto L_0x00d5
                        int r6 = r6.length     // Catch:{ all -> 0x0354 }
                        if (r6 != 0) goto L_0x007d
                        goto L_0x00d5
                    L_0x007d:
                        r6 = 0
                    L_0x007e:
                        java.lang.String[] r7 = r6     // Catch:{ all -> 0x0354 }
                        int r7 = r7.length     // Catch:{ all -> 0x0354 }
                        if (r6 >= r7) goto L_0x00cb
                        com.mysql.jdbc.DatabaseMetaData$TableType r7 = com.mysql.jdbc.DatabaseMetaData.TableType.TABLE     // Catch:{ all -> 0x0354 }
                        java.lang.String[] r15 = r6     // Catch:{ all -> 0x0354 }
                        r15 = r15[r6]     // Catch:{ all -> 0x0354 }
                        boolean r7 = r7.equalsTo(r15)     // Catch:{ all -> 0x0354 }
                        if (r7 == 0) goto L_0x0091
                        r0 = 1
                        goto L_0x00c8
                    L_0x0091:
                        com.mysql.jdbc.DatabaseMetaData$TableType r7 = com.mysql.jdbc.DatabaseMetaData.TableType.VIEW     // Catch:{ all -> 0x0354 }
                        java.lang.String[] r15 = r6     // Catch:{ all -> 0x0354 }
                        r15 = r15[r6]     // Catch:{ all -> 0x0354 }
                        boolean r7 = r7.equalsTo(r15)     // Catch:{ all -> 0x0354 }
                        if (r7 == 0) goto L_0x009f
                        r2 = 1
                        goto L_0x00c8
                    L_0x009f:
                        com.mysql.jdbc.DatabaseMetaData$TableType r7 = com.mysql.jdbc.DatabaseMetaData.TableType.SYSTEM_TABLE     // Catch:{ all -> 0x0354 }
                        java.lang.String[] r15 = r6     // Catch:{ all -> 0x0354 }
                        r15 = r15[r6]     // Catch:{ all -> 0x0354 }
                        boolean r7 = r7.equalsTo(r15)     // Catch:{ all -> 0x0354 }
                        if (r7 == 0) goto L_0x00ad
                        r3 = 1
                        goto L_0x00c8
                    L_0x00ad:
                        com.mysql.jdbc.DatabaseMetaData$TableType r7 = com.mysql.jdbc.DatabaseMetaData.TableType.SYSTEM_VIEW     // Catch:{ all -> 0x0354 }
                        java.lang.String[] r15 = r6     // Catch:{ all -> 0x0354 }
                        r15 = r15[r6]     // Catch:{ all -> 0x0354 }
                        boolean r7 = r7.equalsTo(r15)     // Catch:{ all -> 0x0354 }
                        if (r7 == 0) goto L_0x00bb
                        r4 = 1
                        goto L_0x00c8
                    L_0x00bb:
                        com.mysql.jdbc.DatabaseMetaData$TableType r7 = com.mysql.jdbc.DatabaseMetaData.TableType.LOCAL_TEMPORARY     // Catch:{ all -> 0x0354 }
                        java.lang.String[] r15 = r6     // Catch:{ all -> 0x0354 }
                        r15 = r15[r6]     // Catch:{ all -> 0x0354 }
                        boolean r7 = r7.equalsTo(r15)     // Catch:{ all -> 0x0354 }
                        if (r7 == 0) goto L_0x00c8
                        r5 = 1
                    L_0x00c8:
                        int r6 = r6 + 1
                        goto L_0x007e
                    L_0x00cb:
                        r15 = r0
                        r16 = r2
                        r17 = r3
                        r18 = r4
                        r19 = r5
                        goto L_0x00e3
                    L_0x00d5:
                        r0 = 1
                        r2 = 1
                        r3 = 1
                        r4 = 1
                        r5 = 1
                        r15 = r0
                        r16 = r2
                        r17 = r3
                        r18 = r4
                        r19 = r5
                    L_0x00e3:
                        r2 = 1
                        r3 = 0
                        com.mysql.jdbc.DatabaseMetaData r0 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.MySQLConnection r0 = r0.conn     // Catch:{ all -> 0x0354 }
                        boolean r0 = r0.versionMeetsMinimum(r13, r9, r12)     // Catch:{ all -> 0x0354 }
                        r20 = 0
                        if (r0 == 0) goto L_0x011d
                        java.lang.String r0 = "table_type"
                        int r0 = r14.findColumn(r0)     // Catch:{ SQLException -> 0x0101 }
                        r2 = r0
                        r3 = 1
                        r21 = r3
                        r23 = r9
                        r22 = r20
                        goto L_0x0124
                    L_0x0101:
                        r0 = move-exception
                        r4 = r0
                        java.lang.String r0 = "Type"
                        int r0 = r14.findColumn(r0)     // Catch:{ SQLException -> 0x0113 }
                        r2 = r0
                        r3 = 1
                        r0 = r2
                        r21 = r3
                        r23 = r9
                        r22 = r20
                        goto L_0x0124
                    L_0x0113:
                        r0 = move-exception
                        r3 = 0
                        r0 = r2
                        r21 = r3
                        r23 = r9
                        r22 = r20
                        goto L_0x0124
                    L_0x011d:
                        r0 = r2
                        r21 = r3
                        r23 = r9
                        r22 = r20
                    L_0x0124:
                        boolean r2 = r14.next()     // Catch:{ all -> 0x0354 }
                        if (r2 == 0) goto L_0x0347
                        r2 = 10
                        byte[][] r2 = new byte[r2][]     // Catch:{ all -> 0x0354 }
                        r7 = r2
                        if (r8 != 0) goto L_0x0134
                        r2 = r20
                        goto L_0x013a
                    L_0x0134:
                        com.mysql.jdbc.DatabaseMetaData r2 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0354 }
                        byte[] r2 = r2.s2b(r8)     // Catch:{ all -> 0x0354 }
                    L_0x013a:
                        r7[r9] = r2     // Catch:{ all -> 0x0354 }
                        r7[r10] = r20     // Catch:{ all -> 0x0354 }
                        byte[] r2 = r14.getBytes(r10)     // Catch:{ all -> 0x0354 }
                        r7[r12] = r2     // Catch:{ all -> 0x0354 }
                        r2 = 4
                        byte[] r3 = new byte[r9]     // Catch:{ all -> 0x0354 }
                        r7[r2] = r3     // Catch:{ all -> 0x0354 }
                        r7[r13] = r20     // Catch:{ all -> 0x0354 }
                        r2 = 6
                        r7[r2] = r20     // Catch:{ all -> 0x0354 }
                        r2 = 7
                        r7[r2] = r20     // Catch:{ all -> 0x0354 }
                        r2 = 8
                        r7[r2] = r20     // Catch:{ all -> 0x0354 }
                        r2 = 9
                        r7[r2] = r20     // Catch:{ all -> 0x0354 }
                        r2 = 3
                        if (r21 == 0) goto L_0x030b
                        java.lang.String r3 = r14.getString(r0)     // Catch:{ all -> 0x0354 }
                        r24 = r3
                        int[] r3 = com.mysql.jdbc.DatabaseMetaData.AnonymousClass11.$SwitchMap$com$mysql$jdbc$DatabaseMetaData$TableType     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData$TableType r4 = com.mysql.jdbc.DatabaseMetaData.TableType.getTableTypeCompliantWith(r24)     // Catch:{ all -> 0x0354 }
                        int r4 = r4.ordinal()     // Catch:{ all -> 0x0354 }
                        r3 = r3[r4]     // Catch:{ all -> 0x0354 }
                        switch(r3) {
                            case 1: goto L_0x0274;
                            case 2: goto L_0x0237;
                            case 3: goto L_0x01ff;
                            case 4: goto L_0x01b9;
                            case 5: goto L_0x017c;
                            default: goto L_0x0171;
                        }     // Catch:{ all -> 0x0354 }
                    L_0x0171:
                        r27 = r0
                        r13 = r7
                        r0 = r22
                        r9 = r23
                        com.mysql.jdbc.DatabaseMetaData$TableType r3 = com.mysql.jdbc.DatabaseMetaData.TableType.TABLE     // Catch:{ all -> 0x0354 }
                        goto L_0x02d9
                    L_0x017c:
                        if (r19 == 0) goto L_0x01b4
                        com.mysql.jdbc.DatabaseMetaData$TableType r3 = com.mysql.jdbc.DatabaseMetaData.TableType.LOCAL_TEMPORARY     // Catch:{ all -> 0x0354 }
                        byte[] r3 = r3.asBytes()     // Catch:{ all -> 0x0354 }
                        r7[r2] = r3     // Catch:{ all -> 0x0354 }
                        java.util.SortedMap r6 = r7     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey r5 = new com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData r3 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData$TableType r2 = com.mysql.jdbc.DatabaseMetaData.TableType.LOCAL_TEMPORARY     // Catch:{ all -> 0x0354 }
                        java.lang.String r4 = r2.getName()     // Catch:{ all -> 0x0354 }
                        r25 = 0
                        java.lang.String r26 = r14.getString(r10)     // Catch:{ all -> 0x0354 }
                        r2 = r5
                        r9 = r5
                        r5 = r29
                        r12 = r6
                        r6 = r25
                        r13 = r7
                        r7 = r26
                        r2.<init>(r4, r5, r6, r7)     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.ByteArrayRow r2 = new com.mysql.jdbc.ByteArrayRow     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData r3 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.ExceptionInterceptor r3 = r3.getExceptionInterceptor()     // Catch:{ all -> 0x0354 }
                        r2.<init>(r13, r3)     // Catch:{ all -> 0x0354 }
                        r12.put(r9, r2)     // Catch:{ all -> 0x0354 }
                        goto L_0x01b5
                    L_0x01b4:
                        r13 = r7
                    L_0x01b5:
                        r27 = r0
                        goto L_0x0309
                    L_0x01b9:
                        r13 = r7
                        r9 = r22
                        r12 = r23
                        if (r18 == 0) goto L_0x01f7
                        com.mysql.jdbc.DatabaseMetaData$TableType r3 = com.mysql.jdbc.DatabaseMetaData.TableType.SYSTEM_VIEW     // Catch:{ all -> 0x0354 }
                        byte[] r3 = r3.asBytes()     // Catch:{ all -> 0x0354 }
                        r13[r2] = r3     // Catch:{ all -> 0x0354 }
                        java.util.SortedMap r7 = r7     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey r6 = new com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData r3 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData$TableType r2 = com.mysql.jdbc.DatabaseMetaData.TableType.SYSTEM_VIEW     // Catch:{ all -> 0x0354 }
                        java.lang.String r4 = r2.getName()     // Catch:{ all -> 0x0354 }
                        r22 = 0
                        java.lang.String r23 = r14.getString(r10)     // Catch:{ all -> 0x0354 }
                        r2 = r6
                        r5 = r29
                        r10 = r6
                        r6 = r22
                        r27 = r0
                        r0 = r7
                        r7 = r23
                        r2.<init>(r4, r5, r6, r7)     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.ByteArrayRow r2 = new com.mysql.jdbc.ByteArrayRow     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData r3 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.ExceptionInterceptor r3 = r3.getExceptionInterceptor()     // Catch:{ all -> 0x0354 }
                        r2.<init>(r13, r3)     // Catch:{ all -> 0x0354 }
                        r0.put(r10, r2)     // Catch:{ all -> 0x0354 }
                        goto L_0x01f9
                    L_0x01f7:
                        r27 = r0
                    L_0x01f9:
                        r22 = r9
                        r23 = r12
                        goto L_0x0309
                    L_0x01ff:
                        r27 = r0
                        r13 = r7
                        r0 = r22
                        r9 = r23
                        if (r17 == 0) goto L_0x026e
                        com.mysql.jdbc.DatabaseMetaData$TableType r3 = com.mysql.jdbc.DatabaseMetaData.TableType.SYSTEM_TABLE     // Catch:{ all -> 0x0354 }
                        byte[] r3 = r3.asBytes()     // Catch:{ all -> 0x0354 }
                        r13[r2] = r3     // Catch:{ all -> 0x0354 }
                        java.util.SortedMap r10 = r7     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey r12 = new com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData r3 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData$TableType r2 = com.mysql.jdbc.DatabaseMetaData.TableType.SYSTEM_TABLE     // Catch:{ all -> 0x0354 }
                        java.lang.String r4 = r2.getName()     // Catch:{ all -> 0x0354 }
                        r6 = 0
                        r2 = 1
                        java.lang.String r7 = r14.getString(r2)     // Catch:{ all -> 0x0354 }
                        r2 = r12
                        r5 = r29
                        r2.<init>(r4, r5, r6, r7)     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.ByteArrayRow r2 = new com.mysql.jdbc.ByteArrayRow     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData r3 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.ExceptionInterceptor r3 = r3.getExceptionInterceptor()     // Catch:{ all -> 0x0354 }
                        r2.<init>(r13, r3)     // Catch:{ all -> 0x0354 }
                        r10.put(r12, r2)     // Catch:{ all -> 0x0354 }
                        goto L_0x026e
                    L_0x0237:
                        r27 = r0
                        r13 = r7
                        r0 = r22
                        r9 = r23
                        if (r16 == 0) goto L_0x026e
                        com.mysql.jdbc.DatabaseMetaData$TableType r3 = com.mysql.jdbc.DatabaseMetaData.TableType.VIEW     // Catch:{ all -> 0x0354 }
                        byte[] r3 = r3.asBytes()     // Catch:{ all -> 0x0354 }
                        r13[r2] = r3     // Catch:{ all -> 0x0354 }
                        java.util.SortedMap r10 = r7     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey r12 = new com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData r3 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData$TableType r2 = com.mysql.jdbc.DatabaseMetaData.TableType.VIEW     // Catch:{ all -> 0x0354 }
                        java.lang.String r4 = r2.getName()     // Catch:{ all -> 0x0354 }
                        r6 = 0
                        r2 = 1
                        java.lang.String r7 = r14.getString(r2)     // Catch:{ all -> 0x0354 }
                        r2 = r12
                        r5 = r29
                        r2.<init>(r4, r5, r6, r7)     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.ByteArrayRow r2 = new com.mysql.jdbc.ByteArrayRow     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData r3 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.ExceptionInterceptor r3 = r3.getExceptionInterceptor()     // Catch:{ all -> 0x0354 }
                        r2.<init>(r13, r3)     // Catch:{ all -> 0x0354 }
                        r10.put(r12, r2)     // Catch:{ all -> 0x0354 }
                    L_0x026e:
                        r22 = r0
                        r23 = r9
                        goto L_0x0309
                    L_0x0274:
                        r27 = r0
                        r13 = r7
                        r0 = 0
                        r9 = 0
                        if (r11 == 0) goto L_0x029e
                        if (r17 == 0) goto L_0x029e
                        com.mysql.jdbc.DatabaseMetaData$TableType r3 = com.mysql.jdbc.DatabaseMetaData.TableType.SYSTEM_TABLE     // Catch:{ all -> 0x0354 }
                        byte[] r3 = r3.asBytes()     // Catch:{ all -> 0x0354 }
                        r13[r2] = r3     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey r10 = new com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData r3 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData$TableType r2 = com.mysql.jdbc.DatabaseMetaData.TableType.SYSTEM_TABLE     // Catch:{ all -> 0x0354 }
                        java.lang.String r4 = r2.getName()     // Catch:{ all -> 0x0354 }
                        r6 = 0
                        r2 = 1
                        java.lang.String r7 = r14.getString(r2)     // Catch:{ all -> 0x0354 }
                        r2 = r10
                        r5 = r29
                        r2.<init>(r4, r5, r6, r7)     // Catch:{ all -> 0x0354 }
                        r9 = r10
                        r0 = 1
                        goto L_0x02c2
                    L_0x029e:
                        if (r11 != 0) goto L_0x02c2
                        if (r15 == 0) goto L_0x02c2
                        com.mysql.jdbc.DatabaseMetaData$TableType r3 = com.mysql.jdbc.DatabaseMetaData.TableType.TABLE     // Catch:{ all -> 0x0354 }
                        byte[] r3 = r3.asBytes()     // Catch:{ all -> 0x0354 }
                        r13[r2] = r3     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey r10 = new com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData r3 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData$TableType r2 = com.mysql.jdbc.DatabaseMetaData.TableType.TABLE     // Catch:{ all -> 0x0354 }
                        java.lang.String r4 = r2.getName()     // Catch:{ all -> 0x0354 }
                        r6 = 0
                        r2 = 1
                        java.lang.String r7 = r14.getString(r2)     // Catch:{ all -> 0x0354 }
                        r2 = r10
                        r5 = r29
                        r2.<init>(r4, r5, r6, r7)     // Catch:{ all -> 0x0354 }
                        r9 = r10
                        r0 = 1
                    L_0x02c2:
                        if (r0 == 0) goto L_0x02d4
                        java.util.SortedMap r2 = r7     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.ByteArrayRow r3 = new com.mysql.jdbc.ByteArrayRow     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData r4 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.ExceptionInterceptor r4 = r4.getExceptionInterceptor()     // Catch:{ all -> 0x0354 }
                        r3.<init>(r13, r4)     // Catch:{ all -> 0x0354 }
                        r2.put(r9, r3)     // Catch:{ all -> 0x0354 }
                    L_0x02d4:
                        r23 = r0
                        r22 = r9
                        goto L_0x0309
                    L_0x02d9:
                        byte[] r3 = r3.asBytes()     // Catch:{ all -> 0x0354 }
                        r13[r2] = r3     // Catch:{ all -> 0x0354 }
                        java.util.SortedMap r10 = r7     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey r12 = new com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData r3 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData$TableType r2 = com.mysql.jdbc.DatabaseMetaData.TableType.TABLE     // Catch:{ all -> 0x0354 }
                        java.lang.String r4 = r2.getName()     // Catch:{ all -> 0x0354 }
                        r6 = 0
                        r2 = 1
                        java.lang.String r7 = r14.getString(r2)     // Catch:{ all -> 0x0354 }
                        r2 = r12
                        r5 = r29
                        r2.<init>(r4, r5, r6, r7)     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.ByteArrayRow r2 = new com.mysql.jdbc.ByteArrayRow     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData r3 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.ExceptionInterceptor r3 = r3.getExceptionInterceptor()     // Catch:{ all -> 0x0354 }
                        r2.<init>(r13, r3)     // Catch:{ all -> 0x0354 }
                        r10.put(r12, r2)     // Catch:{ all -> 0x0354 }
                        r22 = r0
                        r23 = r9
                    L_0x0309:
                        r10 = 1
                        goto L_0x0340
                    L_0x030b:
                        r27 = r0
                        r13 = r7
                        if (r15 == 0) goto L_0x033f
                        com.mysql.jdbc.DatabaseMetaData$TableType r0 = com.mysql.jdbc.DatabaseMetaData.TableType.TABLE     // Catch:{ all -> 0x0354 }
                        byte[] r0 = r0.asBytes()     // Catch:{ all -> 0x0354 }
                        r13[r2] = r0     // Catch:{ all -> 0x0354 }
                        java.util.SortedMap r0 = r7     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey r9 = new com.mysql.jdbc.DatabaseMetaData$TableMetaDataKey     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData r3 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData$TableType r2 = com.mysql.jdbc.DatabaseMetaData.TableType.TABLE     // Catch:{ all -> 0x0354 }
                        java.lang.String r4 = r2.getName()     // Catch:{ all -> 0x0354 }
                        r6 = 0
                        r10 = 1
                        java.lang.String r7 = r14.getString(r10)     // Catch:{ all -> 0x0354 }
                        r2 = r9
                        r5 = r29
                        r2.<init>(r4, r5, r6, r7)     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.ByteArrayRow r2 = new com.mysql.jdbc.ByteArrayRow     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.DatabaseMetaData r3 = com.mysql.jdbc.DatabaseMetaData.this     // Catch:{ all -> 0x0354 }
                        com.mysql.jdbc.ExceptionInterceptor r3 = r3.getExceptionInterceptor()     // Catch:{ all -> 0x0354 }
                        r2.<init>(r13, r3)     // Catch:{ all -> 0x0354 }
                        r0.put(r9, r2)     // Catch:{ all -> 0x0354 }
                        goto L_0x0340
                    L_0x033f:
                        r10 = 1
                    L_0x0340:
                        r0 = r27
                        r9 = 0
                        r12 = 2
                        r13 = 5
                        goto L_0x0124
                    L_0x0347:
                        r27 = r0
                        if (r14 == 0) goto L_0x0352
                        r14.close()     // Catch:{ Exception -> 0x0350 }
                        goto L_0x0351
                    L_0x0350:
                        r0 = move-exception
                    L_0x0351:
                        r14 = 0
                    L_0x0352:
                        return
                    L_0x0354:
                        r0 = move-exception
                        r3 = r0
                        r2 = r14
                        goto L_0x0376
                    L_0x0358:
                        r0 = move-exception
                        r3 = r0
                        goto L_0x0376
                    L_0x035b:
                        r0 = move-exception
                        r3 = r0
                        java.lang.String r0 = "08S01"
                        java.lang.String r4 = r3.getSQLState()     // Catch:{ all -> 0x0358 }
                        boolean r0 = r0.equals(r4)     // Catch:{ all -> 0x0358 }
                        if (r0 != 0) goto L_0x0374
                        if (r2 == 0) goto L_0x0372
                        r2.close()     // Catch:{ Exception -> 0x0370 }
                        goto L_0x0371
                    L_0x0370:
                        r0 = move-exception
                    L_0x0371:
                        r2 = 0
                    L_0x0372:
                        r0 = r3
                        return
                    L_0x0374:
                        throw r3     // Catch:{ all -> 0x0358 }
                    L_0x0376:
                        if (r2 == 0) goto L_0x037e
                        r2.close()     // Catch:{ Exception -> 0x037c }
                        goto L_0x037d
                    L_0x037c:
                        r0 = move-exception
                    L_0x037d:
                        r2 = 0
                    L_0x037e:
                        throw r3
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.DatabaseMetaData.AnonymousClass9.forEach(java.lang.String):void");
                }
            }.doForAll();
            arrayList.addAll(sortedRows.values());
            return buildResultSet(createTablesFields(), arrayList);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    /* renamed from: com.mysql.jdbc.DatabaseMetaData$11  reason: invalid class name */
    static /* synthetic */ class AnonymousClass11 {
        static final /* synthetic */ int[] $SwitchMap$com$mysql$jdbc$DatabaseMetaData$TableType;

        static {
            int[] iArr = new int[TableType.values().length];
            $SwitchMap$com$mysql$jdbc$DatabaseMetaData$TableType = iArr;
            try {
                iArr[TableType.TABLE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$mysql$jdbc$DatabaseMetaData$TableType[TableType.VIEW.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$mysql$jdbc$DatabaseMetaData$TableType[TableType.SYSTEM_TABLE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$mysql$jdbc$DatabaseMetaData$TableType[TableType.SYSTEM_VIEW.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$mysql$jdbc$DatabaseMetaData$TableType[TableType.LOCAL_TEMPORARY.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    /* access modifiers changed from: protected */
    public Field[] createTablesFields() {
        return new Field[]{new Field("", "TABLE_CAT", 12, 255), new Field("", "TABLE_SCHEM", 12, 0), new Field("", "TABLE_NAME", 12, 255), new Field("", "TABLE_TYPE", 12, 5), new Field("", "REMARKS", 12, 0), new Field("", "TYPE_CAT", 12, 0), new Field("", "TYPE_SCHEM", 12, 0), new Field("", "TYPE_NAME", 12, 0), new Field("", "SELF_REFERENCING_COL_NAME", 12, 0), new Field("", "REF_GENERATION", 12, 0)};
    }

    public ResultSet getTableTypes() throws SQLException {
        ArrayList<ResultSetRow> tuples = new ArrayList<>();
        Field[] fields = {new Field("", "TABLE_TYPE", 12, 256)};
        boolean minVersion5_0_1 = this.conn.versionMeetsMinimum(5, 0, 1);
        tuples.add(new ByteArrayRow(new byte[][]{TableType.LOCAL_TEMPORARY.asBytes()}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{TableType.SYSTEM_TABLE.asBytes()}, getExceptionInterceptor()));
        if (minVersion5_0_1) {
            tuples.add(new ByteArrayRow(new byte[][]{TableType.SYSTEM_VIEW.asBytes()}, getExceptionInterceptor()));
        }
        tuples.add(new ByteArrayRow(new byte[][]{TableType.TABLE.asBytes()}, getExceptionInterceptor()));
        if (minVersion5_0_1) {
            tuples.add(new ByteArrayRow(new byte[][]{TableType.VIEW.asBytes()}, getExceptionInterceptor()));
        }
        return buildResultSet(fields, tuples);
    }

    public String getTimeDateFunctions() throws SQLException {
        return "DAYOFWEEK,WEEKDAY,DAYOFMONTH,DAYOFYEAR,MONTH,DAYNAME,MONTHNAME,QUARTER,WEEK,YEAR,HOUR,MINUTE,SECOND,PERIOD_ADD,PERIOD_DIFF,TO_DAYS,FROM_DAYS,DATE_FORMAT,TIME_FORMAT,CURDATE,CURRENT_DATE,CURTIME,CURRENT_TIME,NOW,SYSDATE,CURRENT_TIMESTAMP,UNIX_TIMESTAMP,FROM_UNIXTIME,SEC_TO_TIME,TIME_TO_SEC";
    }

    public ResultSet getTypeInfo() throws SQLException {
        Field[] fields = {new Field("", "TYPE_NAME", 1, 32), new Field("", "DATA_TYPE", 4, 5), new Field("", "PRECISION", 4, 10), new Field("", "LITERAL_PREFIX", 1, 4), new Field("", "LITERAL_SUFFIX", 1, 4), new Field("", "CREATE_PARAMS", 1, 32), new Field("", "NULLABLE", 5, 5), new Field("", "CASE_SENSITIVE", 16, 3), new Field("", "SEARCHABLE", 5, 3), new Field("", "UNSIGNED_ATTRIBUTE", 16, 3), new Field("", "FIXED_PREC_SCALE", 16, 3), new Field("", "AUTO_INCREMENT", 16, 3), new Field("", "LOCAL_TYPE_NAME", 1, 32), new Field("", "MINIMUM_SCALE", 5, 5), new Field("", "MAXIMUM_SCALE", 5, 5), new Field("", "SQL_DATA_TYPE", 4, 10), new Field("", "SQL_DATETIME_SUB", 4, 10), new Field("", "NUM_PREC_RADIX", 4, 10)};
        byte[][] bArr = null;
        ArrayList<ResultSetRow> tuples = new ArrayList<>();
        tuples.add(new ByteArrayRow(new byte[][]{s2b("BIT"), Integer.toString(-7).getBytes(), s2b("1"), s2b(""), s2b(""), s2b(""), Integer.toString(1).getBytes(), s2b("true"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("BIT"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("BOOL"), Integer.toString(-7).getBytes(), s2b("1"), s2b(""), s2b(""), s2b(""), Integer.toString(1).getBytes(), s2b("true"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("BOOL"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("TINYINT"), Integer.toString(-6).getBytes(), s2b("3"), s2b(""), s2b(""), s2b("[(M)] [UNSIGNED] [ZEROFILL]"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("true"), s2b("false"), s2b("true"), s2b("TINYINT"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("TINYINT UNSIGNED"), Integer.toString(-6).getBytes(), s2b("3"), s2b(""), s2b(""), s2b("[(M)] [UNSIGNED] [ZEROFILL]"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("true"), s2b("false"), s2b("true"), s2b("TINYINT UNSIGNED"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("BIGINT"), Integer.toString(-5).getBytes(), s2b("19"), s2b(""), s2b(""), s2b("[(M)] [UNSIGNED] [ZEROFILL]"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("true"), s2b("false"), s2b("true"), s2b("BIGINT"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("BIGINT UNSIGNED"), Integer.toString(-5).getBytes(), s2b("20"), s2b(""), s2b(""), s2b("[(M)] [ZEROFILL]"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("true"), s2b("false"), s2b("true"), s2b("BIGINT UNSIGNED"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("LONG VARBINARY"), Integer.toString(-4).getBytes(), s2b("16777215"), s2b("'"), s2b("'"), s2b(""), Integer.toString(1).getBytes(), s2b("true"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("LONG VARBINARY"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("MEDIUMBLOB"), Integer.toString(-4).getBytes(), s2b("16777215"), s2b("'"), s2b("'"), s2b(""), Integer.toString(1).getBytes(), s2b("true"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("MEDIUMBLOB"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("LONGBLOB"), Integer.toString(-4).getBytes(), Integer.toString(Integer.MAX_VALUE).getBytes(), s2b("'"), s2b("'"), s2b(""), Integer.toString(1).getBytes(), s2b("true"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("LONGBLOB"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("BLOB"), Integer.toString(-4).getBytes(), s2b("65535"), s2b("'"), s2b("'"), s2b(""), Integer.toString(1).getBytes(), s2b("true"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("BLOB"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("TINYBLOB"), Integer.toString(-4).getBytes(), s2b("255"), s2b("'"), s2b("'"), s2b(""), Integer.toString(1).getBytes(), s2b("true"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("TINYBLOB"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        byte[][] rowVal = new byte[18][];
        rowVal[0] = s2b("VARBINARY");
        rowVal[1] = Integer.toString(-3).getBytes();
        rowVal[2] = s2b(this.conn.versionMeetsMinimum(5, 0, 3) ? "65535" : "255");
        rowVal[3] = s2b("'");
        rowVal[4] = s2b("'");
        rowVal[5] = s2b("(M)");
        rowVal[6] = Integer.toString(1).getBytes();
        rowVal[7] = s2b("true");
        rowVal[8] = Integer.toString(3).getBytes();
        rowVal[9] = s2b("false");
        rowVal[10] = s2b("false");
        rowVal[11] = s2b("false");
        rowVal[12] = s2b("VARBINARY");
        rowVal[13] = s2b("0");
        rowVal[14] = s2b("0");
        rowVal[15] = s2b("0");
        rowVal[16] = s2b("0");
        rowVal[17] = s2b("10");
        tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("BINARY"), Integer.toString(-2).getBytes(), s2b("255"), s2b("'"), s2b("'"), s2b("(M)"), Integer.toString(1).getBytes(), s2b("true"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("BINARY"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("LONG VARCHAR"), Integer.toString(-1).getBytes(), s2b("16777215"), s2b("'"), s2b("'"), s2b(""), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("LONG VARCHAR"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("MEDIUMTEXT"), Integer.toString(-1).getBytes(), s2b("16777215"), s2b("'"), s2b("'"), s2b(""), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("MEDIUMTEXT"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("LONGTEXT"), Integer.toString(-1).getBytes(), Integer.toString(Integer.MAX_VALUE).getBytes(), s2b("'"), s2b("'"), s2b(""), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("LONGTEXT"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("TEXT"), Integer.toString(-1).getBytes(), s2b("65535"), s2b("'"), s2b("'"), s2b(""), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("TEXT"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("TINYTEXT"), Integer.toString(-1).getBytes(), s2b("255"), s2b("'"), s2b("'"), s2b(""), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("TINYTEXT"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("CHAR"), Integer.toString(1).getBytes(), s2b("255"), s2b("'"), s2b("'"), s2b("(M)"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("CHAR"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        int decimalPrecision = 254;
        if (this.conn.versionMeetsMinimum(5, 0, 3)) {
            if (this.conn.versionMeetsMinimum(5, 0, 6)) {
                decimalPrecision = 65;
            } else {
                decimalPrecision = 64;
            }
        }
        tuples.add(new ByteArrayRow(new byte[][]{s2b("NUMERIC"), Integer.toString(2).getBytes(), s2b(String.valueOf(decimalPrecision)), s2b(""), s2b(""), s2b("[(M[,D])] [ZEROFILL]"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("true"), s2b("NUMERIC"), s2b("-308"), s2b("308"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("DECIMAL"), Integer.toString(3).getBytes(), s2b(String.valueOf(decimalPrecision)), s2b(""), s2b(""), s2b("[(M[,D])] [ZEROFILL]"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("true"), s2b("DECIMAL"), s2b("-308"), s2b("308"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("INTEGER"), Integer.toString(4).getBytes(), s2b("10"), s2b(""), s2b(""), s2b("[(M)] [UNSIGNED] [ZEROFILL]"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("true"), s2b("false"), s2b("true"), s2b("INTEGER"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("INTEGER UNSIGNED"), Integer.toString(4).getBytes(), s2b("10"), s2b(""), s2b(""), s2b("[(M)] [ZEROFILL]"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("true"), s2b("false"), s2b("true"), s2b("INTEGER UNSIGNED"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("INT"), Integer.toString(4).getBytes(), s2b("10"), s2b(""), s2b(""), s2b("[(M)] [UNSIGNED] [ZEROFILL]"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("true"), s2b("false"), s2b("true"), s2b("INT"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("INT UNSIGNED"), Integer.toString(4).getBytes(), s2b("10"), s2b(""), s2b(""), s2b("[(M)] [ZEROFILL]"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("true"), s2b("false"), s2b("true"), s2b("INT UNSIGNED"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("MEDIUMINT"), Integer.toString(4).getBytes(), s2b("7"), s2b(""), s2b(""), s2b("[(M)] [UNSIGNED] [ZEROFILL]"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("true"), s2b("false"), s2b("true"), s2b("MEDIUMINT"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("MEDIUMINT UNSIGNED"), Integer.toString(4).getBytes(), s2b("8"), s2b(""), s2b(""), s2b("[(M)] [ZEROFILL]"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("true"), s2b("false"), s2b("true"), s2b("MEDIUMINT UNSIGNED"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("SMALLINT"), Integer.toString(5).getBytes(), s2b("5"), s2b(""), s2b(""), s2b("[(M)] [UNSIGNED] [ZEROFILL]"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("true"), s2b("false"), s2b("true"), s2b("SMALLINT"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("SMALLINT UNSIGNED"), Integer.toString(5).getBytes(), s2b("5"), s2b(""), s2b(""), s2b("[(M)] [ZEROFILL]"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("true"), s2b("false"), s2b("true"), s2b("SMALLINT UNSIGNED"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("FLOAT"), Integer.toString(7).getBytes(), s2b("10"), s2b(""), s2b(""), s2b("[(M,D)] [ZEROFILL]"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("true"), s2b("FLOAT"), s2b("-38"), s2b("38"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("DOUBLE"), Integer.toString(8).getBytes(), s2b("17"), s2b(""), s2b(""), s2b("[(M,D)] [ZEROFILL]"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("true"), s2b("DOUBLE"), s2b("-308"), s2b("308"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("DOUBLE PRECISION"), Integer.toString(8).getBytes(), s2b("17"), s2b(""), s2b(""), s2b("[(M,D)] [ZEROFILL]"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("true"), s2b("DOUBLE PRECISION"), s2b("-308"), s2b("308"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("REAL"), Integer.toString(8).getBytes(), s2b("17"), s2b(""), s2b(""), s2b("[(M,D)] [ZEROFILL]"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("true"), s2b("REAL"), s2b("-308"), s2b("308"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        byte[][] rowVal2 = new byte[18][];
        rowVal2[0] = s2b("VARCHAR");
        rowVal2[1] = Integer.toString(12).getBytes();
        rowVal2[2] = s2b(this.conn.versionMeetsMinimum(5, 0, 3) ? "65535" : "255");
        rowVal2[3] = s2b("'");
        rowVal2[4] = s2b("'");
        rowVal2[5] = s2b("(M)");
        rowVal2[6] = Integer.toString(1).getBytes();
        rowVal2[7] = s2b("false");
        rowVal2[8] = Integer.toString(3).getBytes();
        rowVal2[9] = s2b("false");
        rowVal2[10] = s2b("false");
        rowVal2[11] = s2b("false");
        rowVal2[12] = s2b("VARCHAR");
        rowVal2[13] = s2b("0");
        rowVal2[14] = s2b("0");
        rowVal2[15] = s2b("0");
        rowVal2[16] = s2b("0");
        rowVal2[17] = s2b("10");
        tuples.add(new ByteArrayRow(rowVal2, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("ENUM"), Integer.toString(12).getBytes(), s2b("65535"), s2b("'"), s2b("'"), s2b(""), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("ENUM"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("SET"), Integer.toString(12).getBytes(), s2b("64"), s2b("'"), s2b("'"), s2b(""), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("SET"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("DATE"), Integer.toString(91).getBytes(), s2b("0"), s2b("'"), s2b("'"), s2b(""), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("DATE"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("TIME"), Integer.toString(92).getBytes(), s2b("0"), s2b("'"), s2b("'"), s2b(""), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("TIME"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("DATETIME"), Integer.toString(93).getBytes(), s2b("0"), s2b("'"), s2b("'"), s2b(""), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("DATETIME"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        tuples.add(new ByteArrayRow(new byte[][]{s2b("TIMESTAMP"), Integer.toString(93).getBytes(), s2b("0"), s2b("'"), s2b("'"), s2b("[(M)]"), Integer.toString(1).getBytes(), s2b("false"), Integer.toString(3).getBytes(), s2b("false"), s2b("false"), s2b("false"), s2b("TIMESTAMP"), s2b("0"), s2b("0"), s2b("0"), s2b("0"), s2b("10")}, getExceptionInterceptor()));
        return buildResultSet(fields, tuples);
    }

    public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
        return buildResultSet(new Field[]{new Field("", "TYPE_CAT", 12, 32), new Field("", "TYPE_SCHEM", 12, 32), new Field("", "TYPE_NAME", 12, 32), new Field("", "CLASS_NAME", 12, 32), new Field("", "DATA_TYPE", 4, 10), new Field("", "REMARKS", 12, 32), new Field("", "BASE_TYPE", 5, 10)}, new ArrayList<>());
    }

    public String getURL() throws SQLException {
        return this.conn.getURL();
    }

    public String getUserName() throws SQLException {
        if (!this.conn.getUseHostsInPrivileges()) {
            return this.conn.getUser();
        }
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = this.conn.getMetadataSafeStatement();
            rs = stmt.executeQuery("SELECT USER()");
            rs.next();
            return rs.getString(1);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    AssertionFailedException.shouldNotHappen(ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex2) {
                    AssertionFailedException.shouldNotHappen(ex2);
                }
            }
        }
    }

    public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
        if (table != null) {
            Field[] fields = {new Field("", "SCOPE", 5, 5), new Field("", "COLUMN_NAME", 1, 32), new Field("", "DATA_TYPE", 4, 5), new Field("", "TYPE_NAME", 1, 16), new Field("", "COLUMN_SIZE", 4, 16), new Field("", "BUFFER_LENGTH", 4, 16), new Field("", "DECIMAL_DIGITS", 5, 16), new Field("", "PSEUDO_COLUMN", 5, 5)};
            ArrayList<ResultSetRow> rows = new ArrayList<>();
            Statement stmt = this.conn.getMetadataSafeStatement();
            try {
                final String str = table;
                final Statement statement = stmt;
                final ArrayList<ResultSetRow> arrayList = rows;
                new IterateBlock<String>(getCatalogIterator(catalog)) {
                    /* access modifiers changed from: package-private */
                    public void forEach(String catalogStr) throws SQLException {
                        int endPos;
                        String str = catalogStr;
                        ResultSet results = null;
                        boolean with_where = DatabaseMetaData.this.conn.versionMeetsMinimum(5, 0, 0);
                        try {
                            StringBuilder whereBuf = new StringBuilder(" Extra LIKE '%on update CURRENT_TIMESTAMP%'");
                            List<String> rsFields = new ArrayList<>();
                            int i = 2;
                            if (!DatabaseMetaData.this.conn.versionMeetsMinimum(5, 1, 23)) {
                                whereBuf = new StringBuilder();
                                boolean firstTime = true;
                                results = statement.executeQuery("SHOW CREATE TABLE " + DatabaseMetaData.this.getFullyQualifiedName(str, str));
                                while (results.next()) {
                                    StringTokenizer lineTokenizer = new StringTokenizer(results.getString(i), "\n");
                                    while (lineTokenizer.hasMoreTokens()) {
                                        String line = lineTokenizer.nextToken().trim();
                                        if (StringUtils.indexOfIgnoreCase(line, "on update CURRENT_TIMESTAMP") > -1) {
                                            boolean usingBackTicks = true;
                                            int beginPos = line.indexOf(DatabaseMetaData.this.quotedId);
                                            if (beginPos == -1) {
                                                beginPos = line.indexOf("\"");
                                                usingBackTicks = false;
                                            }
                                            if (beginPos != -1) {
                                                if (usingBackTicks) {
                                                    endPos = line.indexOf(DatabaseMetaData.this.quotedId, beginPos + 1);
                                                } else {
                                                    endPos = line.indexOf("\"", beginPos + 1);
                                                }
                                                if (endPos != -1) {
                                                    if (with_where) {
                                                        if (!firstTime) {
                                                            whereBuf.append(" or");
                                                        } else {
                                                            firstTime = false;
                                                        }
                                                        whereBuf.append(" Field='");
                                                        whereBuf.append(line.substring(beginPos + 1, endPos));
                                                        whereBuf.append("'");
                                                    } else {
                                                        rsFields.add(line.substring(beginPos + 1, endPos));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    i = 2;
                                }
                            }
                            if (whereBuf.length() > 0 || rsFields.size() > 0) {
                                StringBuilder queryBuf = new StringBuilder("SHOW COLUMNS FROM ");
                                queryBuf.append(StringUtils.quoteIdentifier(str, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
                                queryBuf.append(" FROM ");
                                queryBuf.append(StringUtils.quoteIdentifier(str, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
                                if (with_where) {
                                    queryBuf.append(" WHERE");
                                    queryBuf.append(whereBuf.toString());
                                }
                                results = statement.executeQuery(queryBuf.toString());
                                while (results.next()) {
                                    if (!with_where) {
                                        if (!rsFields.contains(results.getString("Field"))) {
                                        }
                                    }
                                    TypeDescriptor typeDesc = new TypeDescriptor(DatabaseMetaData.this, results.getString("Type"), results.getString("Null"));
                                    byte[][] rowVal = new byte[8][];
                                    byte[] bArr = null;
                                    rowVal[0] = null;
                                    rowVal[1] = results.getBytes("Field");
                                    rowVal[2] = Short.toString(typeDesc.dataType).getBytes();
                                    rowVal[3] = DatabaseMetaData.this.s2b(typeDesc.typeName);
                                    rowVal[4] = typeDesc.columnSize == null ? null : DatabaseMetaData.this.s2b(typeDesc.columnSize.toString());
                                    rowVal[5] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.bufferLength));
                                    if (typeDesc.decimalDigits != null) {
                                        bArr = DatabaseMetaData.this.s2b(typeDesc.decimalDigits.toString());
                                    }
                                    rowVal[6] = bArr;
                                    rowVal[7] = Integer.toString(1).getBytes();
                                    arrayList.add(new ByteArrayRow(rowVal, DatabaseMetaData.this.getExceptionInterceptor()));
                                }
                            }
                            if (results != null) {
                                try {
                                    results.close();
                                } catch (Exception e) {
                                }
                            }
                        } catch (SQLException sqlEx) {
                            if (!SQLError.SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND.equals(sqlEx.getSQLState())) {
                                throw sqlEx;
                            } else if (results != null) {
                                try {
                                    results.close();
                                } catch (Exception e2) {
                                }
                            }
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            if (results != null) {
                                try {
                                    results.close();
                                } catch (Exception e3) {
                                }
                            }
                            throw th2;
                        }
                    }
                }.doForAll();
                return buildResultSet(fields, rows);
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        } else {
            throw SQLError.createSQLException("Table not specified.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
    }

    public boolean insertsAreDetected(int type) throws SQLException {
        return false;
    }

    public boolean isCatalogAtStart() throws SQLException {
        return true;
    }

    public boolean isReadOnly() throws SQLException {
        return false;
    }

    public boolean locatorsUpdateCopy() throws SQLException {
        return !this.conn.getEmulateLocators();
    }

    public boolean nullPlusNonNullIsNull() throws SQLException {
        return true;
    }

    public boolean nullsAreSortedAtEnd() throws SQLException {
        return false;
    }

    public boolean nullsAreSortedAtStart() throws SQLException {
        return this.conn.versionMeetsMinimum(4, 0, 2) && !this.conn.versionMeetsMinimum(4, 0, 11);
    }

    public boolean nullsAreSortedHigh() throws SQLException {
        return false;
    }

    public boolean nullsAreSortedLow() throws SQLException {
        return !nullsAreSortedHigh();
    }

    public boolean othersDeletesAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean othersInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean ownDeletesAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean ownInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        return false;
    }

    /* access modifiers changed from: protected */
    public LocalAndReferencedColumns parseTableStatusIntoLocalAndReferencedColumns(String keysComment) throws SQLException {
        String str = keysComment;
        String str2 = this.quotedId;
        int indexOfOpenParenLocalColumns = StringUtils.indexOfIgnoreCase(0, keysComment, "(", str2, str2, StringUtils.SEARCH_MODE__ALL);
        if (indexOfOpenParenLocalColumns != -1) {
            String constraintName = StringUtils.unQuoteIdentifier(str.substring(0, indexOfOpenParenLocalColumns).trim(), this.quotedId);
            String keysCommentTrimmed = str.substring(indexOfOpenParenLocalColumns, keysComment.length()).trim();
            String str3 = this.quotedId;
            int indexOfCloseParenLocalColumns = StringUtils.indexOfIgnoreCase(0, keysCommentTrimmed, ")", str3, str3, StringUtils.SEARCH_MODE__ALL);
            if (indexOfCloseParenLocalColumns != -1) {
                String localColumnNamesString = keysCommentTrimmed.substring(1, indexOfCloseParenLocalColumns);
                String str4 = this.quotedId;
                int indexOfRefer = StringUtils.indexOfIgnoreCase(0, keysCommentTrimmed, "REFER ", str4, str4, StringUtils.SEARCH_MODE__ALL);
                if (indexOfRefer != -1) {
                    String str5 = this.quotedId;
                    int indexOfOpenParenReferCol = StringUtils.indexOfIgnoreCase(indexOfRefer, keysCommentTrimmed, "(", str5, str5, StringUtils.SEARCH_MODE__MRK_COM_WS);
                    if (indexOfOpenParenReferCol != -1) {
                        String referCatalogTableString = keysCommentTrimmed.substring("REFER ".length() + indexOfRefer, indexOfOpenParenReferCol);
                        String str6 = this.quotedId;
                        int indexOfSlash = StringUtils.indexOfIgnoreCase(0, referCatalogTableString, "/", str6, str6, StringUtils.SEARCH_MODE__MRK_COM_WS);
                        if (indexOfSlash != -1) {
                            String referCatalog = StringUtils.unQuoteIdentifier(referCatalogTableString.substring(0, indexOfSlash), this.quotedId);
                            String referTable = StringUtils.unQuoteIdentifier(referCatalogTableString.substring(indexOfSlash + 1).trim(), this.quotedId);
                            String str7 = this.quotedId;
                            int i = indexOfSlash;
                            String str8 = referCatalogTableString;
                            int indexOfOpenParenReferCol2 = indexOfOpenParenReferCol;
                            int indexOfCloseParenRefer = StringUtils.indexOfIgnoreCase(indexOfOpenParenReferCol, keysCommentTrimmed, ")", str7, str7, StringUtils.SEARCH_MODE__ALL);
                            if (indexOfCloseParenRefer != -1) {
                                String referColumnNamesString = keysCommentTrimmed.substring(indexOfOpenParenReferCol2 + 1, indexOfCloseParenRefer);
                                String str9 = this.quotedId;
                                List<String> referColumnsList = StringUtils.split(referColumnNamesString, ",", str9, str9, false);
                                String str10 = this.quotedId;
                                int i2 = indexOfRefer;
                                String str11 = localColumnNamesString;
                                int i3 = indexOfCloseParenLocalColumns;
                                String str12 = keysCommentTrimmed;
                                return new LocalAndReferencedColumns(StringUtils.split(localColumnNamesString, ",", str10, str10, false), referColumnsList, constraintName, referCatalog, referTable);
                            }
                            throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find end of referenced columns list.", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
                        }
                        throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find name of referenced catalog.", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
                    }
                    throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find start of referenced columns list.", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
                }
                throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find start of referenced tables list.", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
            }
            throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find end of local columns list.", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
        }
        throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find start of local columns list.", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
    }

    /* access modifiers changed from: protected */
    public byte[] s2b(String s) throws SQLException {
        if (s == null) {
            return null;
        }
        return StringUtils.getBytes(s, this.conn.getCharacterSetMetadata(), this.conn.getServerCharset(), this.conn.parserKnowsUnicode(), this.conn, getExceptionInterceptor());
    }

    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return this.conn.storesLowerCaseTableName();
    }

    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return this.conn.storesLowerCaseTableName();
    }

    public boolean storesMixedCaseIdentifiers() throws SQLException {
        return !this.conn.storesLowerCaseTableName();
    }

    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return !this.conn.storesLowerCaseTableName();
    }

    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return false;
    }

    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return true;
    }

    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return true;
    }

    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return true;
    }

    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return true;
    }

    public boolean supportsANSI92FullSQL() throws SQLException {
        return false;
    }

    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        return false;
    }

    public boolean supportsBatchUpdates() throws SQLException {
        return true;
    }

    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return this.conn.versionMeetsMinimum(3, 22, 0);
    }

    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return this.conn.versionMeetsMinimum(3, 22, 0);
    }

    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return this.conn.versionMeetsMinimum(3, 22, 0);
    }

    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return this.conn.versionMeetsMinimum(3, 22, 0);
    }

    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return this.conn.versionMeetsMinimum(3, 22, 0);
    }

    public boolean supportsColumnAliasing() throws SQLException {
        return true;
    }

    public boolean supportsConvert() throws SQLException {
        return false;
    }

    public boolean supportsConvert(int fromType, int toType) throws SQLException {
        switch (fromType) {
            case -7:
                return false;
            case -6:
            case -5:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                switch (toType) {
                    case -6:
                    case -5:
                    case -4:
                    case -3:
                    case -2:
                    case -1:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 12:
                        return true;
                    default:
                        return false;
                }
            case -4:
            case -3:
            case -2:
            case -1:
            case 1:
            case 12:
                switch (toType) {
                    case -6:
                    case -5:
                    case -4:
                    case -3:
                    case -2:
                    case -1:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 12:
                    case 91:
                    case 92:
                    case 93:
                    case MysqlErrorNumbers.ER_INVALID_GROUP_FUNC_USE /*1111*/:
                        return true;
                    default:
                        return false;
                }
            case 0:
                return false;
            case 91:
                switch (toType) {
                    case -4:
                    case -3:
                    case -2:
                    case -1:
                    case 1:
                    case 12:
                        return true;
                    default:
                        return false;
                }
            case 92:
                switch (toType) {
                    case -4:
                    case -3:
                    case -2:
                    case -1:
                    case 1:
                    case 12:
                        return true;
                    default:
                        return false;
                }
            case 93:
                switch (toType) {
                    case -4:
                    case -3:
                    case -2:
                    case -1:
                    case 1:
                    case 12:
                    case 91:
                    case 92:
                        return true;
                    default:
                        return false;
                }
            case MysqlErrorNumbers.ER_INVALID_GROUP_FUNC_USE /*1111*/:
                switch (toType) {
                    case -4:
                    case -3:
                    case -2:
                    case -1:
                    case 1:
                    case 12:
                        return true;
                    default:
                        return false;
                }
            default:
                return false;
        }
    }

    public boolean supportsCoreSQLGrammar() throws SQLException {
        return true;
    }

    public boolean supportsCorrelatedSubqueries() throws SQLException {
        return this.conn.versionMeetsMinimum(4, 1, 0);
    }

    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        return false;
    }

    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        return false;
    }

    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        return true;
    }

    public boolean supportsExpressionsInOrderBy() throws SQLException {
        return true;
    }

    public boolean supportsExtendedSQLGrammar() throws SQLException {
        return false;
    }

    public boolean supportsFullOuterJoins() throws SQLException {
        return false;
    }

    public boolean supportsGetGeneratedKeys() {
        return true;
    }

    public boolean supportsGroupBy() throws SQLException {
        return true;
    }

    public boolean supportsGroupByBeyondSelect() throws SQLException {
        return true;
    }

    public boolean supportsGroupByUnrelated() throws SQLException {
        return true;
    }

    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        if (!this.conn.getOverrideSupportsIntegrityEnhancementFacility()) {
            return false;
        }
        return true;
    }

    public boolean supportsLikeEscapeClause() throws SQLException {
        return true;
    }

    public boolean supportsLimitedOuterJoins() throws SQLException {
        return true;
    }

    public boolean supportsMinimumSQLGrammar() throws SQLException {
        return true;
    }

    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        return !this.conn.lowerCaseTableNames();
    }

    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        return !this.conn.lowerCaseTableNames();
    }

    public boolean supportsMultipleOpenResults() throws SQLException {
        return true;
    }

    public boolean supportsMultipleResultSets() throws SQLException {
        return this.conn.versionMeetsMinimum(4, 1, 0);
    }

    public boolean supportsMultipleTransactions() throws SQLException {
        return true;
    }

    public boolean supportsNamedParameters() throws SQLException {
        return false;
    }

    public boolean supportsNonNullableColumns() throws SQLException {
        return true;
    }

    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return false;
    }

    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return false;
    }

    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return false;
    }

    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return false;
    }

    public boolean supportsOrderByUnrelated() throws SQLException {
        return false;
    }

    public boolean supportsOuterJoins() throws SQLException {
        return true;
    }

    public boolean supportsPositionedDelete() throws SQLException {
        return false;
    }

    public boolean supportsPositionedUpdate() throws SQLException {
        return false;
    }

    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
        switch (type) {
            case 1003:
                if (concurrency == 1007 || concurrency == 1008) {
                    return true;
                }
                throw SQLError.createSQLException("Illegal arguments to supportsResultSetConcurrency()", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            case 1004:
                if (concurrency == 1007 || concurrency == 1008) {
                    return true;
                }
                throw SQLError.createSQLException("Illegal arguments to supportsResultSetConcurrency()", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            case MysqlErrorNumbers.ER_CANT_CREATE_TABLE /*1005*/:
                return false;
            default:
                throw SQLError.createSQLException("Illegal arguments to supportsResultSetConcurrency()", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
    }

    public boolean supportsResultSetHoldability(int holdability) throws SQLException {
        return holdability == 1;
    }

    public boolean supportsResultSetType(int type) throws SQLException {
        return type == 1004;
    }

    public boolean supportsSavepoints() throws SQLException {
        return this.conn.versionMeetsMinimum(4, 0, 14) || this.conn.versionMeetsMinimum(4, 1, 1);
    }

    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return false;
    }

    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return false;
    }

    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsSelectForUpdate() throws SQLException {
        return this.conn.versionMeetsMinimum(4, 0, 0);
    }

    public boolean supportsStatementPooling() throws SQLException {
        return false;
    }

    public boolean supportsStoredProcedures() throws SQLException {
        return this.conn.versionMeetsMinimum(5, 0, 0);
    }

    public boolean supportsSubqueriesInComparisons() throws SQLException {
        return this.conn.versionMeetsMinimum(4, 1, 0);
    }

    public boolean supportsSubqueriesInExists() throws SQLException {
        return this.conn.versionMeetsMinimum(4, 1, 0);
    }

    public boolean supportsSubqueriesInIns() throws SQLException {
        return this.conn.versionMeetsMinimum(4, 1, 0);
    }

    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return this.conn.versionMeetsMinimum(4, 1, 0);
    }

    public boolean supportsTableCorrelationNames() throws SQLException {
        return true;
    }

    public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
        if (!this.conn.supportsIsolationLevel()) {
            return false;
        }
        switch (level) {
            case 1:
            case 2:
            case 4:
            case 8:
                return true;
            default:
                return false;
        }
    }

    public boolean supportsTransactions() throws SQLException {
        return this.conn.supportsTransactions();
    }

    public boolean supportsUnion() throws SQLException {
        return this.conn.versionMeetsMinimum(4, 0, 0);
    }

    public boolean supportsUnionAll() throws SQLException {
        return this.conn.versionMeetsMinimum(4, 0, 0);
    }

    public boolean updatesAreDetected(int type) throws SQLException {
        return false;
    }

    public boolean usesLocalFilePerTable() throws SQLException {
        return false;
    }

    public boolean usesLocalFiles() throws SQLException {
        return false;
    }

    public ResultSet getClientInfoProperties() throws SQLException {
        return buildResultSet(new Field[]{new Field("", "NAME", 12, 255), new Field("", "MAX_LEN", 4, 10), new Field("", "DEFAULT_VALUE", 12, 255), new Field("", "DESCRIPTION", 12, 255)}, new ArrayList(), this.conn);
    }

    public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
        return getProcedureOrFunctionColumns(createFunctionColumnsFields(), catalog, schemaPattern, functionNamePattern, columnNamePattern, false, true);
    }

    /* access modifiers changed from: protected */
    public Field[] createFunctionColumnsFields() {
        return new Field[]{new Field("", "FUNCTION_CAT", 12, 512), new Field("", "FUNCTION_SCHEM", 12, 512), new Field("", "FUNCTION_NAME", 12, 512), new Field("", "COLUMN_NAME", 12, 512), new Field("", "COLUMN_TYPE", 12, 64), new Field("", "DATA_TYPE", 5, 6), new Field("", "TYPE_NAME", 12, 64), new Field("", "PRECISION", 4, 12), new Field("", "LENGTH", 4, 12), new Field("", "SCALE", 5, 12), new Field("", "RADIX", 5, 6), new Field("", "NULLABLE", 5, 6), new Field("", "REMARKS", 12, 512), new Field("", "CHAR_OCTET_LENGTH", 4, 32), new Field("", "ORDINAL_POSITION", 4, 32), new Field("", "IS_NULLABLE", 12, 12), new Field("", "SPECIFIC_NAME", 12, 64)};
    }

    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
        return getProceduresAndOrFunctions(new Field[]{new Field("", "FUNCTION_CAT", 1, 255), new Field("", "FUNCTION_SCHEM", 1, 255), new Field("", "FUNCTION_NAME", 1, 255), new Field("", "REMARKS", 1, 255), new Field("", "FUNCTION_TYPE", 5, 6), new Field("", "SPECIFIC_NAME", 1, 255)}, catalog, schemaPattern, functionNamePattern, false, true);
    }

    public boolean providesQueryObjectGenerator() throws SQLException {
        return false;
    }

    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        return buildResultSet(new Field[]{new Field("", "TABLE_SCHEM", 12, 255), new Field("", "TABLE_CATALOG", 12, 255)}, new ArrayList());
    }

    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        return true;
    }

    /* access modifiers changed from: protected */
    public PreparedStatement prepareMetaDataSafeStatement(String sql) throws SQLException {
        PreparedStatement pStmt = this.conn.clientPrepareStatement(sql);
        if (pStmt.getMaxRows() != 0) {
            pStmt.setMaxRows(0);
        }
        ((Statement) pStmt).setHoldResultsOpenOverClose(true);
        return pStmt;
    }

    public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        return buildResultSet(new Field[]{new Field("", "TABLE_CAT", 12, 512), new Field("", "TABLE_SCHEM", 12, 512), new Field("", "TABLE_NAME", 12, 512), new Field("", "COLUMN_NAME", 12, 512), new Field("", "DATA_TYPE", 4, 12), new Field("", "COLUMN_SIZE", 4, 12), new Field("", "DECIMAL_DIGITS", 4, 12), new Field("", "NUM_PREC_RADIX", 4, 12), new Field("", "COLUMN_USAGE", 12, 512), new Field("", "REMARKS", 12, 512), new Field("", "CHAR_OCTET_LENGTH", 4, 12), new Field("", "IS_NULLABLE", 12, 512)}, new ArrayList());
    }

    public boolean generatedKeyAlwaysReturned() throws SQLException {
        return true;
    }
}
