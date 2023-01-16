package com.mysql.jdbc;

import java.util.HashMap;
import java.util.Map;

public final class MysqlDefs {
    static final int COM_BINLOG_DUMP = 18;
    static final int COM_CHANGE_USER = 17;
    static final int COM_CLOSE_STATEMENT = 25;
    static final int COM_CONNECT_OUT = 20;
    static final int COM_END = 29;
    static final int COM_EXECUTE = 23;
    static final int COM_FETCH = 28;
    static final int COM_LONG_DATA = 24;
    static final int COM_PREPARE = 22;
    static final int COM_REGISTER_SLAVE = 21;
    static final int COM_RESET_STMT = 26;
    static final int COM_SET_OPTION = 27;
    static final int COM_TABLE_DUMP = 19;
    static final int CONNECT = 11;
    static final int CREATE_DB = 5;
    static final int DEBUG = 13;
    static final int DELAYED_INSERT = 16;
    static final int DROP_DB = 6;
    static final int FIELD_LIST = 4;
    static final int FIELD_TYPE_BIT = 16;
    public static final int FIELD_TYPE_BLOB = 252;
    static final int FIELD_TYPE_DATE = 10;
    static final int FIELD_TYPE_DATETIME = 12;
    static final int FIELD_TYPE_DECIMAL = 0;
    static final int FIELD_TYPE_DOUBLE = 5;
    static final int FIELD_TYPE_ENUM = 247;
    static final int FIELD_TYPE_FLOAT = 4;
    static final int FIELD_TYPE_GEOMETRY = 255;
    static final int FIELD_TYPE_INT24 = 9;
    static final int FIELD_TYPE_JSON = 245;
    static final int FIELD_TYPE_LONG = 3;
    static final int FIELD_TYPE_LONGLONG = 8;
    static final int FIELD_TYPE_LONG_BLOB = 251;
    static final int FIELD_TYPE_MEDIUM_BLOB = 250;
    static final int FIELD_TYPE_NEWDATE = 14;
    static final int FIELD_TYPE_NEW_DECIMAL = 246;
    static final int FIELD_TYPE_NULL = 6;
    static final int FIELD_TYPE_SET = 248;
    static final int FIELD_TYPE_SHORT = 2;
    static final int FIELD_TYPE_STRING = 254;
    static final int FIELD_TYPE_TIME = 11;
    static final int FIELD_TYPE_TIMESTAMP = 7;
    static final int FIELD_TYPE_TINY = 1;
    static final int FIELD_TYPE_TINY_BLOB = 249;
    static final int FIELD_TYPE_VARCHAR = 15;
    static final int FIELD_TYPE_VAR_STRING = 253;
    static final int FIELD_TYPE_YEAR = 13;
    static final int INIT_DB = 2;
    static final long LENGTH_BLOB = 65535;
    static final long LENGTH_LONGBLOB = 4294967295L;
    static final long LENGTH_MEDIUMBLOB = 16777215;
    static final long LENGTH_TINYBLOB = 255;
    static final int MAX_ROWS = 50000000;
    public static final int NO_CHARSET_INFO = -1;
    static final byte OPEN_CURSOR_FLAG = 1;
    static final int PING = 14;
    static final int PROCESS_INFO = 10;
    static final int PROCESS_KILL = 12;
    static final int QUERY = 3;
    static final int QUIT = 1;
    static final int RELOAD = 7;
    static final int SHUTDOWN = 8;
    static final int SLEEP = 0;
    static final int STATISTICS = 9;
    static final int TIME = 15;
    private static Map<String, Integer> mysqlToJdbcTypesMap;

    static int mysqlToJavaType(int mysqlType) {
        switch (mysqlType) {
            case 0:
            case FIELD_TYPE_NEW_DECIMAL /*246*/:
                return 3;
            case 1:
                return -6;
            case 2:
                return 5;
            case 3:
                return 4;
            case 4:
                return 7;
            case 5:
                return 8;
            case 6:
                return 0;
            case 7:
                return 93;
            case 8:
                return -5;
            case 9:
                return 4;
            case 10:
                return 91;
            case 11:
                return 92;
            case 12:
                return 93;
            case 13:
                return 91;
            case 14:
                return 91;
            case 15:
            case FIELD_TYPE_VAR_STRING /*253*/:
                return 12;
            case 16:
                return -7;
            case FIELD_TYPE_JSON /*245*/:
            case FIELD_TYPE_STRING /*254*/:
                return 1;
            case FIELD_TYPE_ENUM /*247*/:
                return 1;
            case FIELD_TYPE_SET /*248*/:
                return 1;
            case FIELD_TYPE_TINY_BLOB /*249*/:
                return -3;
            case 250:
                return -4;
            case FIELD_TYPE_LONG_BLOB /*251*/:
                return -4;
            case FIELD_TYPE_BLOB /*252*/:
                return -4;
            case 255:
                return -2;
            default:
                return 12;
        }
    }

    static int mysqlToJavaType(String mysqlType) {
        if (mysqlType.equalsIgnoreCase("BIT")) {
            return mysqlToJavaType(16);
        }
        if (mysqlType.equalsIgnoreCase("TINYINT")) {
            return mysqlToJavaType(1);
        }
        if (mysqlType.equalsIgnoreCase("SMALLINT")) {
            return mysqlToJavaType(2);
        }
        if (mysqlType.equalsIgnoreCase("MEDIUMINT")) {
            return mysqlToJavaType(9);
        }
        if (mysqlType.equalsIgnoreCase("INT") || mysqlType.equalsIgnoreCase("INTEGER")) {
            return mysqlToJavaType(3);
        }
        if (mysqlType.equalsIgnoreCase("BIGINT")) {
            return mysqlToJavaType(8);
        }
        if (mysqlType.equalsIgnoreCase("INT24")) {
            return mysqlToJavaType(9);
        }
        if (mysqlType.equalsIgnoreCase("REAL")) {
            return mysqlToJavaType(5);
        }
        if (mysqlType.equalsIgnoreCase("FLOAT")) {
            return mysqlToJavaType(4);
        }
        if (mysqlType.equalsIgnoreCase("DECIMAL")) {
            return mysqlToJavaType(0);
        }
        if (mysqlType.equalsIgnoreCase("NUMERIC")) {
            return mysqlToJavaType(0);
        }
        if (mysqlType.equalsIgnoreCase("DOUBLE")) {
            return mysqlToJavaType(5);
        }
        if (mysqlType.equalsIgnoreCase("CHAR")) {
            return mysqlToJavaType((int) FIELD_TYPE_STRING);
        }
        if (mysqlType.equalsIgnoreCase("VARCHAR")) {
            return mysqlToJavaType((int) FIELD_TYPE_VAR_STRING);
        }
        if (mysqlType.equalsIgnoreCase("DATE")) {
            return mysqlToJavaType(10);
        }
        if (mysqlType.equalsIgnoreCase("TIME")) {
            return mysqlToJavaType(11);
        }
        if (mysqlType.equalsIgnoreCase("YEAR")) {
            return mysqlToJavaType(13);
        }
        if (mysqlType.equalsIgnoreCase("TIMESTAMP")) {
            return mysqlToJavaType(7);
        }
        if (mysqlType.equalsIgnoreCase("DATETIME")) {
            return mysqlToJavaType(12);
        }
        if (mysqlType.equalsIgnoreCase("TINYBLOB")) {
            return -2;
        }
        if (mysqlType.equalsIgnoreCase("BLOB") || mysqlType.equalsIgnoreCase("MEDIUMBLOB") || mysqlType.equalsIgnoreCase("LONGBLOB")) {
            return -4;
        }
        if (mysqlType.equalsIgnoreCase("TINYTEXT")) {
            return 12;
        }
        if (mysqlType.equalsIgnoreCase("TEXT") || mysqlType.equalsIgnoreCase("MEDIUMTEXT") || mysqlType.equalsIgnoreCase("LONGTEXT")) {
            return -1;
        }
        if (mysqlType.equalsIgnoreCase("ENUM")) {
            return mysqlToJavaType((int) FIELD_TYPE_ENUM);
        }
        if (mysqlType.equalsIgnoreCase("SET")) {
            return mysqlToJavaType((int) FIELD_TYPE_SET);
        }
        if (mysqlType.equalsIgnoreCase("GEOMETRY")) {
            return mysqlToJavaType(255);
        }
        if (mysqlType.equalsIgnoreCase("BINARY")) {
            return -2;
        }
        if (mysqlType.equalsIgnoreCase("VARBINARY")) {
            return -3;
        }
        if (mysqlType.equalsIgnoreCase("BIT")) {
            return mysqlToJavaType(16);
        }
        if (mysqlType.equalsIgnoreCase("JSON")) {
            return mysqlToJavaType((int) FIELD_TYPE_JSON);
        }
        return MysqlErrorNumbers.ER_INVALID_GROUP_FUNC_USE;
    }

    public static String typeToName(int mysqlType) {
        switch (mysqlType) {
            case 0:
                return "FIELD_TYPE_DECIMAL";
            case 1:
                return "FIELD_TYPE_TINY";
            case 2:
                return "FIELD_TYPE_SHORT";
            case 3:
                return "FIELD_TYPE_LONG";
            case 4:
                return "FIELD_TYPE_FLOAT";
            case 5:
                return "FIELD_TYPE_DOUBLE";
            case 6:
                return "FIELD_TYPE_NULL";
            case 7:
                return "FIELD_TYPE_TIMESTAMP";
            case 8:
                return "FIELD_TYPE_LONGLONG";
            case 9:
                return "FIELD_TYPE_INT24";
            case 10:
                return "FIELD_TYPE_DATE";
            case 11:
                return "FIELD_TYPE_TIME";
            case 12:
                return "FIELD_TYPE_DATETIME";
            case 13:
                return "FIELD_TYPE_YEAR";
            case 14:
                return "FIELD_TYPE_NEWDATE";
            case 15:
                return "FIELD_TYPE_VARCHAR";
            case 16:
                return "FIELD_TYPE_BIT";
            case FIELD_TYPE_JSON /*245*/:
                return "FIELD_TYPE_JSON";
            case FIELD_TYPE_ENUM /*247*/:
                return "FIELD_TYPE_ENUM";
            case FIELD_TYPE_SET /*248*/:
                return "FIELD_TYPE_SET";
            case FIELD_TYPE_TINY_BLOB /*249*/:
                return "FIELD_TYPE_TINY_BLOB";
            case 250:
                return "FIELD_TYPE_MEDIUM_BLOB";
            case FIELD_TYPE_LONG_BLOB /*251*/:
                return "FIELD_TYPE_LONG_BLOB";
            case FIELD_TYPE_BLOB /*252*/:
                return "FIELD_TYPE_BLOB";
            case FIELD_TYPE_VAR_STRING /*253*/:
                return "FIELD_TYPE_VAR_STRING";
            case FIELD_TYPE_STRING /*254*/:
                return "FIELD_TYPE_STRING";
            case 255:
                return "FIELD_TYPE_GEOMETRY";
            default:
                return " Unknown MySQL Type # " + mysqlType;
        }
    }

    static {
        HashMap hashMap = new HashMap();
        mysqlToJdbcTypesMap = hashMap;
        hashMap.put("BIT", Integer.valueOf(mysqlToJavaType(16)));
        mysqlToJdbcTypesMap.put("TINYINT", Integer.valueOf(mysqlToJavaType(1)));
        mysqlToJdbcTypesMap.put("SMALLINT", Integer.valueOf(mysqlToJavaType(2)));
        mysqlToJdbcTypesMap.put("MEDIUMINT", Integer.valueOf(mysqlToJavaType(9)));
        mysqlToJdbcTypesMap.put("INT", Integer.valueOf(mysqlToJavaType(3)));
        mysqlToJdbcTypesMap.put("INTEGER", Integer.valueOf(mysqlToJavaType(3)));
        mysqlToJdbcTypesMap.put("BIGINT", Integer.valueOf(mysqlToJavaType(8)));
        mysqlToJdbcTypesMap.put("INT24", Integer.valueOf(mysqlToJavaType(9)));
        mysqlToJdbcTypesMap.put("REAL", Integer.valueOf(mysqlToJavaType(5)));
        mysqlToJdbcTypesMap.put("FLOAT", Integer.valueOf(mysqlToJavaType(4)));
        mysqlToJdbcTypesMap.put("DECIMAL", Integer.valueOf(mysqlToJavaType(0)));
        mysqlToJdbcTypesMap.put("NUMERIC", Integer.valueOf(mysqlToJavaType(0)));
        mysqlToJdbcTypesMap.put("DOUBLE", Integer.valueOf(mysqlToJavaType(5)));
        mysqlToJdbcTypesMap.put("CHAR", Integer.valueOf(mysqlToJavaType((int) FIELD_TYPE_STRING)));
        mysqlToJdbcTypesMap.put("VARCHAR", Integer.valueOf(mysqlToJavaType((int) FIELD_TYPE_VAR_STRING)));
        mysqlToJdbcTypesMap.put("DATE", Integer.valueOf(mysqlToJavaType(10)));
        mysqlToJdbcTypesMap.put("TIME", Integer.valueOf(mysqlToJavaType(11)));
        mysqlToJdbcTypesMap.put("YEAR", Integer.valueOf(mysqlToJavaType(13)));
        mysqlToJdbcTypesMap.put("TIMESTAMP", Integer.valueOf(mysqlToJavaType(7)));
        mysqlToJdbcTypesMap.put("DATETIME", Integer.valueOf(mysqlToJavaType(12)));
        mysqlToJdbcTypesMap.put("TINYBLOB", -2);
        mysqlToJdbcTypesMap.put("BLOB", -4);
        mysqlToJdbcTypesMap.put("MEDIUMBLOB", -4);
        mysqlToJdbcTypesMap.put("LONGBLOB", -4);
        mysqlToJdbcTypesMap.put("TINYTEXT", 12);
        mysqlToJdbcTypesMap.put("TEXT", -1);
        mysqlToJdbcTypesMap.put("MEDIUMTEXT", -1);
        mysqlToJdbcTypesMap.put("LONGTEXT", -1);
        mysqlToJdbcTypesMap.put("ENUM", Integer.valueOf(mysqlToJavaType((int) FIELD_TYPE_ENUM)));
        mysqlToJdbcTypesMap.put("SET", Integer.valueOf(mysqlToJavaType((int) FIELD_TYPE_SET)));
        mysqlToJdbcTypesMap.put("GEOMETRY", Integer.valueOf(mysqlToJavaType(255)));
        mysqlToJdbcTypesMap.put("JSON", Integer.valueOf(mysqlToJavaType((int) FIELD_TYPE_JSON)));
    }

    static final void appendJdbcTypeMappingQuery(StringBuilder buf, String mysqlTypeColumnName) {
        buf.append("CASE ");
        Map<String, Integer> typesMap = new HashMap<>();
        typesMap.putAll(mysqlToJdbcTypesMap);
        typesMap.put("BINARY", -2);
        typesMap.put("VARBINARY", -3);
        for (String mysqlTypeName : typesMap.keySet()) {
            buf.append(" WHEN UPPER(");
            buf.append(mysqlTypeColumnName);
            buf.append(")='");
            buf.append(mysqlTypeName);
            buf.append("' THEN ");
            buf.append(typesMap.get(mysqlTypeName));
            if (mysqlTypeName.equalsIgnoreCase("DOUBLE") || mysqlTypeName.equalsIgnoreCase("FLOAT") || mysqlTypeName.equalsIgnoreCase("DECIMAL") || mysqlTypeName.equalsIgnoreCase("NUMERIC")) {
                buf.append(" WHEN ");
                buf.append(mysqlTypeColumnName);
                buf.append("='");
                buf.append(mysqlTypeName);
                buf.append(" UNSIGNED' THEN ");
                buf.append(typesMap.get(mysqlTypeName));
            }
        }
        buf.append(" ELSE ");
        buf.append(MysqlErrorNumbers.ER_INVALID_GROUP_FUNC_USE);
        buf.append(" END ");
    }
}
