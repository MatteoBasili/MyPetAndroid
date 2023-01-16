package com.mysql.jdbc;

import com.mysql.jdbc.exceptions.MySQLDataException;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;
import com.mysql.jdbc.exceptions.MySQLQueryInterruptedException;
import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;
import com.mysql.jdbc.exceptions.MySQLTransactionRollbackException;
import com.mysql.jdbc.exceptions.MySQLTransientConnectionException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.BatchUpdateException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

public class SQLError {
    private static final long DEFAULT_WAIT_TIMEOUT_SECONDS = 28800;
    private static final int DUE_TO_TIMEOUT_FALSE = 0;
    private static final int DUE_TO_TIMEOUT_MAYBE = 2;
    private static final int DUE_TO_TIMEOUT_TRUE = 1;
    static final int ER_WARNING_NOT_COMPLETE_ROLLBACK = 1196;
    private static final Constructor<?> JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR;
    public static final String SQL_STATE_ACTIVE_SQL_TRANSACTION = "25001";
    public static final String SQL_STATE_BASE_TABLE_NOT_FOUND = "S0002";
    public static final String SQL_STATE_BASE_TABLE_OR_VIEW_ALREADY_EXISTS = "S0001";
    public static final String SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND = "42S02";
    public static final String SQL_STATE_CARDINALITY_VIOLATION = "21000";
    public static final String SQL_STATE_CASE_NOT_FOUND_FOR_CASE_STATEMENT = "20000";
    public static final String SQL_STATE_CLI_SPECIFIC_CONDITION = "HY000";
    public static final String SQL_STATE_COLUMN_ALREADY_EXISTS = "S0021";
    public static final String SQL_STATE_COLUMN_NOT_FOUND = "S0022";
    public static final String SQL_STATE_COMMUNICATION_LINK_FAILURE = "08S01";
    public static final String SQL_STATE_CONNECTION_FAILURE = "08006";
    public static final String SQL_STATE_CONNECTION_IN_USE = "08002";
    public static final String SQL_STATE_CONNECTION_NOT_OPEN = "08003";
    public static final String SQL_STATE_CONNECTION_REJECTED = "08004";
    public static final String SQL_STATE_DATA_TRUNCATED = "01004";
    public static final String SQL_STATE_DATETIME_FIELD_OVERFLOW = "22008";
    public static final String SQL_STATE_DISCONNECT_ERROR = "01002";
    public static final String SQL_STATE_DIVISION_BY_ZERO = "22012";
    public static final String SQL_STATE_DRIVER_NOT_CAPABLE = "S1C00";
    public static final String SQL_STATE_ERROR_IN_ROW = "01S01";
    public static final String SQL_STATE_ER_BAD_FIELD_ERROR = "42S22";
    public static final String SQL_STATE_ER_DUP_FIELDNAME = "42S21";
    public static final String SQL_STATE_ER_NO_SUCH_INDEX = "42S12";
    public static final String SQL_STATE_ER_QUERY_INTERRUPTED = "70100";
    public static final String SQL_STATE_ER_TABLE_EXISTS_ERROR = "42S01";
    public static final String SQL_STATE_FEATURE_NOT_SUPPORTED = "0A000";
    public static final String SQL_STATE_GENERAL_ERROR = "S1000";
    public static final String SQL_STATE_ILLEGAL_ARGUMENT = "S1009";
    public static final String SQL_STATE_INDEX_ALREADY_EXISTS = "S0011";
    public static final String SQL_STATE_INDEX_NOT_FOUND = "S0012";
    public static final String SQL_STATE_INSERT_VALUE_LIST_NO_MATCH_COL_LIST = "21S01";
    public static final String SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION = "23000";
    public static final String SQL_STATE_INVALID_AUTH_SPEC = "28000";
    public static final String SQL_STATE_INVALID_CATALOG_NAME = "3D000";
    public static final String SQL_STATE_INVALID_CHARACTER_VALUE_FOR_CAST = "22018";
    public static final String SQL_STATE_INVALID_COLUMN_NUMBER = "S1002";
    public static final String SQL_STATE_INVALID_CONDITION_NUMBER = "35000";
    public static final String SQL_STATE_INVALID_CONNECTION_ATTRIBUTE = "01S00";
    public static final String SQL_STATE_INVALID_CURSOR_STATE = "24000";
    public static final String SQL_STATE_INVALID_DATETIME_FORMAT = "22007";
    public static final String SQL_STATE_INVALID_LOGARITHM_ARGUMENT = "2201E";
    public static final String SQL_STATE_INVALID_TRANSACTION_STATE = "25000";
    public static final String SQL_STATE_INVALID_TRANSACTION_TERMINATION = "2D000";
    public static final String SQL_STATE_MEMORY_ALLOCATION_ERROR = "HY001";
    public static final String SQL_STATE_MEMORY_ALLOCATION_FAILURE = "S1001";
    public static final String SQL_STATE_MORE_THAN_ONE_ROW_UPDATED_OR_DELETED = "01S04";
    public static final String SQL_STATE_NO_DATA = "02000";
    public static final String SQL_STATE_NO_DEFAULT_FOR_COLUMN = "S0023";
    public static final String SQL_STATE_NO_ROWS_UPDATED_OR_DELETED = "01S03";
    public static final String SQL_STATE_NULL_VALUE_NOT_ALLOWED = "22004";
    public static final String SQL_STATE_NUMERIC_VALUE_OUT_OF_RANGE = "22003";
    public static final String SQL_STATE_PRIVILEGE_NOT_REVOKED = "01006";
    public static final String SQL_STATE_READ_ONLY_SQL_TRANSACTION = "25006";
    public static final String SQL_STATE_RESIGNAL_WHEN_HANDLER_NOT_ACTIVE = "0K000";
    public static final String SQL_STATE_ROLLBACK_SERIALIZATION_FAILURE = "40001";
    public static final String SQL_STATE_SRE_FUNCTION_EXECUTED_NO_RETURN_STATEMENT = "2F005";
    public static final String SQL_STATE_SRE_PROHIBITED_SQL_STATEMENT_ATTEMPTED = "2F003";
    public static final String SQL_STATE_STACKED_DIAGNOSTICS_ACCESSED_WITHOUT_ACTIVE_HANDLER = "0Z002";
    public static final String SQL_STATE_STRING_DATA_RIGHT_TRUNCATION = "22001";
    public static final String SQL_STATE_SYNTAX_ERROR = "42000";
    public static final String SQL_STATE_TIMEOUT_EXPIRED = "S1T00";
    public static final String SQL_STATE_TRANSACTION_RESOLUTION_UNKNOWN = "08007";
    public static final String SQL_STATE_UNABLE_TO_CONNECT_TO_DATASOURCE = "08001";
    public static final String SQL_STATE_WARNING = "01000";
    public static final String SQL_STATE_WRONG_NO_OF_PARAMETERS = "07001";
    public static final String SQL_STATE_XAER_DUPID = "XAE08";
    public static final String SQL_STATE_XAER_INVAL = "XAE05";
    public static final String SQL_STATE_XAER_NOTA = "XAE04";
    public static final String SQL_STATE_XAER_OUTSIDE = "XAE09";
    public static final String SQL_STATE_XAER_RMFAIL = "XAE07";
    public static final String SQL_STATE_XA_RBDEADLOCK = "XA102";
    public static final String SQL_STATE_XA_RBROLLBACK = "XA100";
    public static final String SQL_STATE_XA_RBTIMEOUT = "XA106";
    public static final String SQL_STATE_XA_RMERR = "XAE03";
    private static Map<Integer, String> mysqlToSql99State;
    private static Map<Integer, String> mysqlToSqlState;
    private static Map<String, String> sqlStateMessages;

    static {
        if (Util.isJdbc4()) {
            try {
                JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR = Class.forName("com.mysql.jdbc.exceptions.jdbc4.CommunicationsException").getConstructor(new Class[]{MySQLConnection.class, Long.TYPE, Long.TYPE, Exception.class});
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e2) {
                throw new RuntimeException(e2);
            } catch (ClassNotFoundException e3) {
                throw new RuntimeException(e3);
            }
        } else {
            JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR = null;
        }
        HashMap hashMap = new HashMap();
        sqlStateMessages = hashMap;
        hashMap.put(SQL_STATE_DISCONNECT_ERROR, Messages.getString("SQLError.35"));
        sqlStateMessages.put(SQL_STATE_DATA_TRUNCATED, Messages.getString("SQLError.36"));
        sqlStateMessages.put(SQL_STATE_PRIVILEGE_NOT_REVOKED, Messages.getString("SQLError.37"));
        sqlStateMessages.put(SQL_STATE_INVALID_CONNECTION_ATTRIBUTE, Messages.getString("SQLError.38"));
        sqlStateMessages.put(SQL_STATE_ERROR_IN_ROW, Messages.getString("SQLError.39"));
        sqlStateMessages.put(SQL_STATE_NO_ROWS_UPDATED_OR_DELETED, Messages.getString("SQLError.40"));
        sqlStateMessages.put(SQL_STATE_MORE_THAN_ONE_ROW_UPDATED_OR_DELETED, Messages.getString("SQLError.41"));
        sqlStateMessages.put(SQL_STATE_WRONG_NO_OF_PARAMETERS, Messages.getString("SQLError.42"));
        sqlStateMessages.put(SQL_STATE_UNABLE_TO_CONNECT_TO_DATASOURCE, Messages.getString("SQLError.43"));
        sqlStateMessages.put(SQL_STATE_CONNECTION_IN_USE, Messages.getString("SQLError.44"));
        sqlStateMessages.put(SQL_STATE_CONNECTION_NOT_OPEN, Messages.getString("SQLError.45"));
        sqlStateMessages.put(SQL_STATE_CONNECTION_REJECTED, Messages.getString("SQLError.46"));
        sqlStateMessages.put(SQL_STATE_TRANSACTION_RESOLUTION_UNKNOWN, Messages.getString("SQLError.47"));
        sqlStateMessages.put(SQL_STATE_COMMUNICATION_LINK_FAILURE, Messages.getString("SQLError.48"));
        sqlStateMessages.put(SQL_STATE_INSERT_VALUE_LIST_NO_MATCH_COL_LIST, Messages.getString("SQLError.49"));
        sqlStateMessages.put(SQL_STATE_NUMERIC_VALUE_OUT_OF_RANGE, Messages.getString("SQLError.50"));
        sqlStateMessages.put(SQL_STATE_DATETIME_FIELD_OVERFLOW, Messages.getString("SQLError.51"));
        sqlStateMessages.put(SQL_STATE_DIVISION_BY_ZERO, Messages.getString("SQLError.52"));
        sqlStateMessages.put(SQL_STATE_ROLLBACK_SERIALIZATION_FAILURE, Messages.getString("SQLError.53"));
        sqlStateMessages.put(SQL_STATE_INVALID_AUTH_SPEC, Messages.getString("SQLError.54"));
        sqlStateMessages.put(SQL_STATE_SYNTAX_ERROR, Messages.getString("SQLError.55"));
        sqlStateMessages.put(SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND, Messages.getString("SQLError.56"));
        sqlStateMessages.put(SQL_STATE_BASE_TABLE_OR_VIEW_ALREADY_EXISTS, Messages.getString("SQLError.57"));
        sqlStateMessages.put(SQL_STATE_BASE_TABLE_NOT_FOUND, Messages.getString("SQLError.58"));
        sqlStateMessages.put(SQL_STATE_INDEX_ALREADY_EXISTS, Messages.getString("SQLError.59"));
        sqlStateMessages.put(SQL_STATE_INDEX_NOT_FOUND, Messages.getString("SQLError.60"));
        sqlStateMessages.put(SQL_STATE_COLUMN_ALREADY_EXISTS, Messages.getString("SQLError.61"));
        sqlStateMessages.put(SQL_STATE_COLUMN_NOT_FOUND, Messages.getString("SQLError.62"));
        sqlStateMessages.put(SQL_STATE_NO_DEFAULT_FOR_COLUMN, Messages.getString("SQLError.63"));
        sqlStateMessages.put(SQL_STATE_GENERAL_ERROR, Messages.getString("SQLError.64"));
        sqlStateMessages.put(SQL_STATE_MEMORY_ALLOCATION_FAILURE, Messages.getString("SQLError.65"));
        sqlStateMessages.put(SQL_STATE_INVALID_COLUMN_NUMBER, Messages.getString("SQLError.66"));
        sqlStateMessages.put(SQL_STATE_ILLEGAL_ARGUMENT, Messages.getString("SQLError.67"));
        sqlStateMessages.put(SQL_STATE_DRIVER_NOT_CAPABLE, Messages.getString("SQLError.68"));
        sqlStateMessages.put(SQL_STATE_TIMEOUT_EXPIRED, Messages.getString("SQLError.69"));
        Hashtable hashtable = new Hashtable();
        mysqlToSqlState = hashtable;
        hashtable.put(Integer.valueOf(MysqlErrorNumbers.ER_SELECT_REDUCED), SQL_STATE_WARNING);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WARN_TOO_FEW_RECORDS), SQL_STATE_WARNING);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WARN_TOO_MANY_RECORDS), SQL_STATE_WARNING);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WARN_DATA_TRUNCATED), SQL_STATE_WARNING);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_UNINIT_VAR), SQL_STATE_WARNING);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SIGNAL_WARN), SQL_STATE_WARNING);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_CON_COUNT_ERROR), SQL_STATE_CONNECTION_REJECTED);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NOT_SUPPORTED_AUTH_MODE), SQL_STATE_CONNECTION_REJECTED);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_BAD_HOST_ERROR), SQL_STATE_CONNECTION_REJECTED);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_HANDSHAKE_ERROR), SQL_STATE_CONNECTION_REJECTED);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_HOST_IS_BLOCKED), SQL_STATE_CONNECTION_REJECTED);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_HOST_NOT_PRIVILEGED), SQL_STATE_CONNECTION_REJECTED);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_UNKNOWN_COM_ERROR), SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SERVER_SHUTDOWN), SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_FORCING_CLOSE), SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_IPSOCK_ERROR), SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_ABORTING_CONNECTION), SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NET_PACKET_TOO_LARGE), SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NET_READ_ERROR_FROM_PIPE), SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NET_FCNTL_ERROR), SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NET_PACKETS_OUT_OF_ORDER), SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NET_UNCOMPRESS_ERROR), SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NET_READ_ERROR), SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NET_READ_INTERRUPTED), SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NET_ERROR_ON_WRITE), SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NET_WRITE_INTERRUPTED), SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NEW_ABORTING_CONNECTION), SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_MASTER_NET_READ), SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_MASTER_NET_WRITE), SQL_STATE_COMMUNICATION_LINK_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_CONNECT_TO_MASTER), SQL_STATE_COMMUNICATION_LINK_FAILURE);
        Map<Integer, String> map = mysqlToSqlState;
        Integer valueOf = Integer.valueOf(MysqlErrorNumbers.ER_SP_BADSELECT);
        Object obj = SQL_STATE_COMMUNICATION_LINK_FAILURE;
        map.put(valueOf, SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_BADSTATEMENT), SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_SUBSELECT_NYI), SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_STMT_NOT_ALLOWED_IN_SF_OR_TRG), SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_NO_RETSET), SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_ALTER_OPERATION_NOT_SUPPORTED), SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_ALTER_OPERATION_NOT_SUPPORTED_REASON), SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_DBACCESS_DENIED_ERROR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_BAD_DB_ERROR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_FIELD_WITH_GROUP), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_GROUP_FIELD), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_SUM_SELECT), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_LONG_IDENT), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_DUP_FIELDNAME), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_DUP_KEYNAME), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_DUP_ENTRY), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_FIELD_SPEC), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_PARSE_ERROR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_EMPTY_QUERY), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NONUNIQ_TABLE), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_INVALID_DEFAULT), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_MULTIPLE_PRI_KEY), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_MANY_KEYS), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_MANY_KEY_PARTS), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_LONG_KEY), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_KEY_COLUMN_DOES_NOT_EXITS), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_BLOB_USED_AS_KEY), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_BIG_FIELDLENGTH), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_AUTO_KEY), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NO_SUCH_INDEX), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_FIELD_TERMINATORS), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_BLOBS_AND_NO_TERMINATED), SQL_STATE_ILLEGAL_ARGUMENT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_CANT_REMOVE_ALL_FIELDS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_CANT_DROP_FIELD_OR_KEY), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_BLOB_CANT_HAVE_DEFAULT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_DB_NAME), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_TABLE_NAME), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_BIG_SELECT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_UNKNOWN_PROCEDURE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_PARAMCOUNT_TO_PROCEDURE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_FIELD_SPECIFIED_TWICE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_UNSUPPORTED_EXTENSION), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TABLE_MUST_HAVE_COLUMNS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_UNKNOWN_CHARACTER_SET), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_BIG_ROWSIZE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_OUTER_JOIN), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NULL_COLUMN_IN_INDEX), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_PASSWORD_ANONYMOUS_USER), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_PASSWORD_NOT_ALLOWED), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_PASSWORD_NO_MATCH), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_REGEXP_ERROR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_MIX_OF_GROUP_FUNC_AND_FIELDS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NONEXISTING_GRANT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TABLEACCESS_DENIED_ERROR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_COLUMNACCESS_DENIED_ERROR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_ILLEGAL_GRANT_FOR_TABLE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_GRANT_WRONG_HOST_OR_USER), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NONEXISTING_TABLE_GRANT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NOT_ALLOWED_COMMAND), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SYNTAX_ERROR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_LONG_STRING), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TABLE_CANT_HANDLE_BLOB), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TABLE_CANT_HANDLE_AUTO_INCREMENT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_COLUMN_NAME), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_KEY_COLUMN), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_BLOB_KEY_WITHOUT_LENGTH), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_PRIMARY_CANT_HAVE_NULL), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_MANY_ROWS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_REQUIRES_PRIMARY_KEY), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_KEY_DOES_NOT_EXITS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_CHECK_NO_SUCH_TABLE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_CHECK_NOT_IMPLEMENTED), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_MANY_USER_CONNECTIONS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NO_PERMISSION_TO_CREATE_USER), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_USER_LIMIT_REACHED), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SPECIFIC_ACCESS_DENIED_ERROR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NO_DEFAULT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_VALUE_FOR_VAR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_TYPE_FOR_VAR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_CANT_USE_OPTION_HERE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NOT_SUPPORTED_YET), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_FK_DEF), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_DERIVED_MUST_HAVE_ALIAS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TABLENAME_NOT_ALLOWED_HERE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SPATIAL_CANT_HAVE_NULL), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_COLLATION_CHARSET_MISMATCH), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_NAME_FOR_INDEX), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_NAME_FOR_CATALOG), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_UNKNOWN_STORAGE_ENGINE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_ALREADY_EXISTS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_DOES_NOT_EXIST), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_LILABEL_MISMATCH), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_LABEL_REDEFINE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_LABEL_MISMATCH), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_BADRETURN), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_UPDATE_LOG_DEPRECATED_IGNORED), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_UPDATE_LOG_DEPRECATED_TRANSLATED), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_WRONG_NO_OF_ARGS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_COND_MISMATCH), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_NORETURN), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_BAD_CURSOR_QUERY), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_BAD_CURSOR_SELECT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_CURSOR_MISMATCH), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_UNDECLARED_VAR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_DUP_PARAM), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_DUP_VAR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_DUP_COND), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_DUP_CURS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_VARCOND_AFTER_CURSHNDLR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_CURSOR_AFTER_HANDLER), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_PROCACCESS_DENIED_ERROR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NONEXISTING_PROC_GRANT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_BAD_SQLSTATE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_CANT_CREATE_USER_WITH_GRANT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_DUP_HANDLER), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_NOT_VAR_ARG), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_BIG_SCALE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_BIG_PRECISION), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_M_BIGGER_THAN_D), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_LONG_BODY), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_BIG_DISPLAYWIDTH), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_BAD_VAR_SHADOW), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_WRONG_NAME), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_NO_AGGREGATE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_MAX_PREPARED_STMT_COUNT_REACHED), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NON_GROUPING_FIELD_USED), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_PARAMCOUNT_TO_NATIVE_FCT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_PARAMETERS_TO_NATIVE_FCT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_PARAMETERS_TO_STORED_FCT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_FUNC_INEXISTENT_NAME_COLLISION), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_DUP_SIGNAL_SET), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SPATIAL_MUST_HAVE_GEOM_COL), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TRUNCATE_ILLEGAL_FK), SQL_STATE_SYNTAX_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_NUMBER_OF_COLUMNS_IN_SELECT), SQL_STATE_CARDINALITY_VIOLATION);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_OPERAND_COLUMNS), SQL_STATE_CARDINALITY_VIOLATION);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SUBQUERY_NO_1_ROW), SQL_STATE_CARDINALITY_VIOLATION);
        Map<Integer, String> map2 = mysqlToSqlState;
        Integer valueOf2 = Integer.valueOf(MysqlErrorNumbers.ER_DUP_KEY);
        Object obj2 = SQL_STATE_CARDINALITY_VIOLATION;
        map2.put(valueOf2, SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_BAD_NULL_ERROR), SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NON_UNIQ_ERROR), SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_DUP_UNIQUE), SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NO_REFERENCED_ROW), SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_ROW_IS_REFERENCED), SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_ROW_IS_REFERENCED_2), SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NO_REFERENCED_ROW_2), SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_FOREIGN_DUPLICATE_KEY), SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_DUP_ENTRY_WITH_KEY_NAME), SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_FOREIGN_DUPLICATE_KEY_WITH_CHILD_INFO), SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_FOREIGN_DUPLICATE_KEY_WITHOUT_CHILD_INFO), SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_DUP_UNKNOWN_IN_INDEX), SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION);
        Map<Integer, String> map3 = mysqlToSqlState;
        Integer valueOf3 = Integer.valueOf(MysqlErrorNumbers.ER_DATA_TOO_LONG);
        Object obj3 = SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION;
        map3.put(valueOf3, SQL_STATE_STRING_DATA_RIGHT_TRUNCATION);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WARN_DATA_OUT_OF_RANGE), SQL_STATE_WARNING);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_CANT_CREATE_GEOMETRY_OBJECT), SQL_STATE_NUMERIC_VALUE_OUT_OF_RANGE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_DATA_OUT_OF_RANGE), SQL_STATE_NUMERIC_VALUE_OUT_OF_RANGE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TRUNCATED_WRONG_VALUE), SQL_STATE_INVALID_DATETIME_FORMAT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_ILLEGAL_VALUE_FOR_TYPE), SQL_STATE_INVALID_DATETIME_FORMAT);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_DATETIME_FUNCTION_OVERFLOW), SQL_STATE_DATETIME_FIELD_OVERFLOW);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_DIVISION_BY_ZERO), SQL_STATE_DIVISION_BY_ZERO);
        Map<Integer, String> map4 = mysqlToSqlState;
        Integer valueOf4 = Integer.valueOf(MysqlErrorNumbers.ER_SP_CURSOR_ALREADY_OPEN);
        Object obj4 = SQL_STATE_DIVISION_BY_ZERO;
        map4.put(valueOf4, SQL_STATE_INVALID_CURSOR_STATE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_CURSOR_NOT_OPEN), SQL_STATE_INVALID_CURSOR_STATE);
        Map<Integer, String> map5 = mysqlToSqlState;
        Integer valueOf5 = Integer.valueOf(MysqlErrorNumbers.ER_CANT_DO_THIS_DURING_AN_TRANSACTION);
        Object obj5 = SQL_STATE_INVALID_CURSOR_STATE;
        map5.put(valueOf5, SQL_STATE_INVALID_TRANSACTION_STATE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_READ_ONLY_TRANSACTION), SQL_STATE_INVALID_TRANSACTION_STATE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_ACCESS_DENIED_ERROR), SQL_STATE_INVALID_AUTH_SPEC);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_ACCESS_DENIED_NO_PASSWORD_ERROR), SQL_STATE_INVALID_AUTH_SPEC);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_ACCESS_DENIED_CHANGE_USER_ERROR), SQL_STATE_INVALID_AUTH_SPEC);
        Map<Integer, String> map6 = mysqlToSqlState;
        Integer valueOf6 = Integer.valueOf(MysqlErrorNumbers.ER_DA_INVALID_CONDITION_NUMBER);
        Object obj6 = SQL_STATE_INVALID_AUTH_SPEC;
        map6.put(valueOf6, SQL_STATE_INVALID_CONDITION_NUMBER);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NO_DB_ERROR), SQL_STATE_INVALID_CATALOG_NAME);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_VALUE_COUNT), SQL_STATE_INSERT_VALUE_LIST_NO_MATCH_COL_LIST);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_VALUE_COUNT_ON_ROW), SQL_STATE_INSERT_VALUE_LIST_NO_MATCH_COL_LIST);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_TABLE_EXISTS_ERROR), SQL_STATE_ER_TABLE_EXISTS_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_BAD_TABLE_ERROR), SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_UNKNOWN_TABLE), SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_NO_SUCH_TABLE), SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_BAD_FIELD_ERROR), SQL_STATE_COLUMN_NOT_FOUND);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_ILLEGAL_REFERENCE), SQL_STATE_ER_BAD_FIELD_ERROR);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_OUTOFMEMORY), SQL_STATE_MEMORY_ALLOCATION_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_OUT_OF_SORTMEMORY), SQL_STATE_MEMORY_ALLOCATION_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_LOCK_WAIT_TIMEOUT), SQL_STATE_ROLLBACK_SERIALIZATION_FAILURE);
        mysqlToSqlState.put(Integer.valueOf(MysqlErrorNumbers.ER_LOCK_DEADLOCK), SQL_STATE_ROLLBACK_SERIALIZATION_FAILURE);
        HashMap hashMap2 = new HashMap();
        mysqlToSql99State = hashMap2;
        hashMap2.put(Integer.valueOf(MysqlErrorNumbers.ER_SELECT_REDUCED), SQL_STATE_WARNING);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WARN_TOO_FEW_RECORDS), SQL_STATE_WARNING);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WARN_TOO_MANY_RECORDS), SQL_STATE_WARNING);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WARN_DATA_TRUNCATED), SQL_STATE_WARNING);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WARN_NULL_TO_NOTNULL), SQL_STATE_WARNING);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WARN_DATA_OUT_OF_RANGE), SQL_STATE_WARNING);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_UNINIT_VAR), SQL_STATE_WARNING);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SIGNAL_WARN), SQL_STATE_WARNING);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_FETCH_NO_DATA), SQL_STATE_NO_DATA);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SIGNAL_NOT_FOUND), SQL_STATE_NO_DATA);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_CON_COUNT_ERROR), SQL_STATE_CONNECTION_REJECTED);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NOT_SUPPORTED_AUTH_MODE), SQL_STATE_CONNECTION_REJECTED);
        Object obj7 = obj;
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_BAD_HOST_ERROR), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_HANDSHAKE_ERROR), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_UNKNOWN_COM_ERROR), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SERVER_SHUTDOWN), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_FORCING_CLOSE), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_IPSOCK_ERROR), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_ABORTING_CONNECTION), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NET_PACKET_TOO_LARGE), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NET_READ_ERROR_FROM_PIPE), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NET_FCNTL_ERROR), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NET_PACKETS_OUT_OF_ORDER), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NET_UNCOMPRESS_ERROR), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NET_READ_ERROR), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NET_READ_INTERRUPTED), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NET_ERROR_ON_WRITE), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NET_WRITE_INTERRUPTED), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NEW_ABORTING_CONNECTION), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_MASTER_NET_READ), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_MASTER_NET_WRITE), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_CONNECT_TO_MASTER), obj7);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_BADSELECT), SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_BADSTATEMENT), SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_SUBSELECT_NYI), SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_STMT_NOT_ALLOWED_IN_SF_OR_TRG), SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_NO_RETSET), SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_ALTER_OPERATION_NOT_SUPPORTED), SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_ALTER_OPERATION_NOT_SUPPORTED_REASON), SQL_STATE_FEATURE_NOT_SUPPORTED);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_DBACCESS_DENIED_ERROR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_BAD_DB_ERROR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_FIELD_WITH_GROUP), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_GROUP_FIELD), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_SUM_SELECT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_LONG_IDENT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_DUP_KEYNAME), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_FIELD_SPEC), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_PARSE_ERROR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_EMPTY_QUERY), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NONUNIQ_TABLE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_INVALID_DEFAULT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_MULTIPLE_PRI_KEY), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_MANY_KEYS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_MANY_KEY_PARTS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_LONG_KEY), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_KEY_COLUMN_DOES_NOT_EXITS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_BLOB_USED_AS_KEY), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_BIG_FIELDLENGTH), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_AUTO_KEY), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_FIELD_TERMINATORS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_BLOBS_AND_NO_TERMINATED), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_CANT_REMOVE_ALL_FIELDS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_CANT_DROP_FIELD_OR_KEY), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_BLOB_CANT_HAVE_DEFAULT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_DB_NAME), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_TABLE_NAME), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_BIG_SELECT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_UNKNOWN_PROCEDURE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_PARAMCOUNT_TO_PROCEDURE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_FIELD_SPECIFIED_TWICE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_UNSUPPORTED_EXTENSION), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TABLE_MUST_HAVE_COLUMNS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_UNKNOWN_CHARACTER_SET), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_BIG_ROWSIZE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_OUTER_JOIN), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NULL_COLUMN_IN_INDEX), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_PASSWORD_ANONYMOUS_USER), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_PASSWORD_NOT_ALLOWED), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_PASSWORD_NO_MATCH), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_REGEXP_ERROR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_MIX_OF_GROUP_FUNC_AND_FIELDS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NONEXISTING_GRANT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TABLEACCESS_DENIED_ERROR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_COLUMNACCESS_DENIED_ERROR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_ILLEGAL_GRANT_FOR_TABLE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_GRANT_WRONG_HOST_OR_USER), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NONEXISTING_TABLE_GRANT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NOT_ALLOWED_COMMAND), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SYNTAX_ERROR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_LONG_STRING), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TABLE_CANT_HANDLE_BLOB), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TABLE_CANT_HANDLE_AUTO_INCREMENT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_COLUMN_NAME), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_KEY_COLUMN), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_BLOB_KEY_WITHOUT_LENGTH), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_PRIMARY_CANT_HAVE_NULL), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_MANY_ROWS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_REQUIRES_PRIMARY_KEY), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_KEY_DOES_NOT_EXITS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_CHECK_NO_SUCH_TABLE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_CHECK_NOT_IMPLEMENTED), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_MANY_USER_CONNECTIONS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NO_PERMISSION_TO_CREATE_USER), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_USER_LIMIT_REACHED), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SPECIFIC_ACCESS_DENIED_ERROR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NO_DEFAULT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_VALUE_FOR_VAR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_TYPE_FOR_VAR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_CANT_USE_OPTION_HERE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NOT_SUPPORTED_YET), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_FK_DEF), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_DERIVED_MUST_HAVE_ALIAS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TABLENAME_NOT_ALLOWED_HERE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SPATIAL_CANT_HAVE_NULL), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_COLLATION_CHARSET_MISMATCH), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_NAME_FOR_INDEX), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_NAME_FOR_CATALOG), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_UNKNOWN_STORAGE_ENGINE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_ALREADY_EXISTS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_DOES_NOT_EXIST), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_LILABEL_MISMATCH), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_LABEL_REDEFINE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_LABEL_MISMATCH), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_BADRETURN), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_UPDATE_LOG_DEPRECATED_IGNORED), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_UPDATE_LOG_DEPRECATED_TRANSLATED), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_WRONG_NO_OF_ARGS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_COND_MISMATCH), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_NORETURN), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_BAD_CURSOR_QUERY), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_BAD_CURSOR_SELECT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_CURSOR_MISMATCH), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_UNDECLARED_VAR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_DUP_PARAM), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_DUP_VAR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_DUP_COND), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_DUP_CURS), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_VARCOND_AFTER_CURSHNDLR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_CURSOR_AFTER_HANDLER), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_PROCACCESS_DENIED_ERROR), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NONEXISTING_PROC_GRANT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_BAD_SQLSTATE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_CANT_CREATE_USER_WITH_GRANT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_DUP_HANDLER), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_NOT_VAR_ARG), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_BIG_SCALE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_BIG_PRECISION), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_M_BIGGER_THAN_D), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_LONG_BODY), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TOO_BIG_DISPLAYWIDTH), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_BAD_VAR_SHADOW), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_WRONG_NAME), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_NO_AGGREGATE), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_MAX_PREPARED_STMT_COUNT_REACHED), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NON_GROUPING_FIELD_USED), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_PARAMCOUNT_TO_NATIVE_FCT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_PARAMETERS_TO_NATIVE_FCT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_PARAMETERS_TO_STORED_FCT), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_FUNC_INEXISTENT_NAME_COLLISION), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_DUP_SIGNAL_SET), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SPATIAL_MUST_HAVE_GEOM_COL), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TRUNCATE_ILLEGAL_FK), SQL_STATE_SYNTAX_ERROR);
        Object obj8 = obj2;
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_NUMBER_OF_COLUMNS_IN_SELECT), obj8);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_OPERAND_COLUMNS), obj8);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SUBQUERY_NO_1_ROW), obj8);
        Object obj9 = obj3;
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_DUP_KEY), obj9);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_BAD_NULL_ERROR), obj9);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NON_UNIQ_ERROR), obj9);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_DUP_ENTRY), obj9);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_DUP_UNIQUE), obj9);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NO_REFERENCED_ROW), obj9);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_ROW_IS_REFERENCED), obj9);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_ROW_IS_REFERENCED_2), obj9);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NO_REFERENCED_ROW_2), obj9);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_FOREIGN_DUPLICATE_KEY), obj9);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_DUP_ENTRY_WITH_KEY_NAME), obj9);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_FOREIGN_DUPLICATE_KEY_WITH_CHILD_INFO), obj9);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_FOREIGN_DUPLICATE_KEY_WITHOUT_CHILD_INFO), obj9);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_DUP_UNKNOWN_IN_INDEX), obj9);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_DATA_TOO_LONG), SQL_STATE_STRING_DATA_RIGHT_TRUNCATION);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_CANT_CREATE_GEOMETRY_OBJECT), SQL_STATE_NUMERIC_VALUE_OUT_OF_RANGE);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_DATA_OUT_OF_RANGE), SQL_STATE_NUMERIC_VALUE_OUT_OF_RANGE);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TRUNCATED_WRONG_VALUE), SQL_STATE_INVALID_DATETIME_FORMAT);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_ILLEGAL_VALUE_FOR_TYPE), SQL_STATE_INVALID_DATETIME_FORMAT);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_DATETIME_FUNCTION_OVERFLOW), SQL_STATE_DATETIME_FIELD_OVERFLOW);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_DIVISION_BY_ZERO), obj4);
        Object obj10 = obj5;
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_CURSOR_ALREADY_OPEN), obj10);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_CURSOR_NOT_OPEN), obj10);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_CANT_DO_THIS_DURING_AN_TRANSACTION), SQL_STATE_INVALID_TRANSACTION_STATE);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_READ_ONLY_TRANSACTION), SQL_STATE_INVALID_TRANSACTION_STATE);
        Object obj11 = obj6;
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_ACCESS_DENIED_ERROR), obj11);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_ACCESS_DENIED_NO_PASSWORD_ERROR), obj11);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_ACCESS_DENIED_CHANGE_USER_ERROR), obj11);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_DA_INVALID_CONDITION_NUMBER), SQL_STATE_INVALID_CONDITION_NUMBER);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NO_DB_ERROR), SQL_STATE_INVALID_CATALOG_NAME);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_RESIGNAL_WITHOUT_ACTIVE_HANDLER), SQL_STATE_RESIGNAL_WHEN_HANDLER_NOT_ACTIVE);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_GET_STACKED_DA_WITHOUT_ACTIVE_HANDLER), SQL_STATE_STACKED_DIAGNOSTICS_ACCESSED_WITHOUT_ACTIVE_HANDLER);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_CASE_NOT_FOUND), SQL_STATE_CASE_NOT_FOUND_FOR_CASE_STATEMENT);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_VALUE_COUNT), SQL_STATE_INSERT_VALUE_LIST_NO_MATCH_COL_LIST);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_WRONG_VALUE_COUNT_ON_ROW), SQL_STATE_INSERT_VALUE_LIST_NO_MATCH_COL_LIST);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_INVALID_USE_OF_NULL), SQL_STATE_SYNTAX_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_INVALID_ARGUMENT_FOR_LOGARITHM), SQL_STATE_INVALID_LOGARITHM_ARGUMENT);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_CANT_CHANGE_TX_ISOLATION), SQL_STATE_ACTIVE_SQL_TRANSACTION);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_CANT_EXECUTE_IN_READ_ONLY_TRANSACTION), SQL_STATE_READ_ONLY_SQL_TRANSACTION);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_NO_RECURSIVE_CREATE), SQL_STATE_SRE_PROHIBITED_SQL_STATEMENT_ATTEMPTED);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_SP_NORETURNEND), SQL_STATE_SRE_FUNCTION_EXECUTED_NO_RETURN_STATEMENT);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_TABLE_EXISTS_ERROR), SQL_STATE_ER_TABLE_EXISTS_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_BAD_TABLE_ERROR), SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_UNKNOWN_TABLE), SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NO_SUCH_TABLE), SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_NO_SUCH_INDEX), SQL_STATE_ER_NO_SUCH_INDEX);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_DUP_FIELDNAME), SQL_STATE_ER_DUP_FIELDNAME);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_BAD_FIELD_ERROR), SQL_STATE_ER_BAD_FIELD_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_ILLEGAL_REFERENCE), SQL_STATE_ER_BAD_FIELD_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_QUERY_INTERRUPTED), SQL_STATE_ER_QUERY_INTERRUPTED);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_OUTOFMEMORY), SQL_STATE_MEMORY_ALLOCATION_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_OUT_OF_SORTMEMORY), SQL_STATE_MEMORY_ALLOCATION_ERROR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_XA_RBROLLBACK), SQL_STATE_XA_RBROLLBACK);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_XA_RBDEADLOCK), SQL_STATE_XA_RBDEADLOCK);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_XA_RBTIMEOUT), SQL_STATE_XA_RBTIMEOUT);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_XA_RMERR), SQL_STATE_XA_RMERR);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_XAER_NOTA), SQL_STATE_XAER_NOTA);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_XAER_INVAL), SQL_STATE_XAER_INVAL);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_XAER_RMFAIL), SQL_STATE_XAER_RMFAIL);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_XAER_DUPID), SQL_STATE_XAER_DUPID);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_XAER_OUTSIDE), SQL_STATE_XAER_OUTSIDE);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_LOCK_WAIT_TIMEOUT), SQL_STATE_ROLLBACK_SERIALIZATION_FAILURE);
        mysqlToSql99State.put(Integer.valueOf(MysqlErrorNumbers.ER_LOCK_DEADLOCK), SQL_STATE_ROLLBACK_SERIALIZATION_FAILURE);
    }

    static SQLWarning convertShowWarningsToSQLWarnings(Connection connection) throws SQLException {
        return convertShowWarningsToSQLWarnings(connection, 0, false);
    }

    /* JADX INFO: finally extract failed */
    static SQLWarning convertShowWarningsToSQLWarnings(Connection connection, int warningCountIfKnown, boolean forTruncationOnly) throws SQLException {
        Statement stmt = null;
        ResultSet warnRs = null;
        SQLWarning currentWarning = null;
        if (warningCountIfKnown < 100) {
            try {
                stmt = connection.createStatement();
                stmt.setFetchSize(0);
                if (stmt.getMaxRows() != 0) {
                    stmt.setMaxRows(0);
                }
            } catch (Throwable reThrow) {
                SQLException reThrow2 = null;
                if (warnRs != null) {
                    try {
                        warnRs.close();
                    } catch (SQLException sqlEx) {
                        reThrow2 = sqlEx;
                    }
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException sqlEx2) {
                        reThrow2 = sqlEx2;
                    }
                }
                if (reThrow2 != null) {
                    throw reThrow2;
                }
                throw reThrow;
            }
        } else {
            stmt = connection.createStatement(1003, 1007);
            stmt.setFetchSize(Integer.MIN_VALUE);
        }
        warnRs = stmt.executeQuery("SHOW WARNINGS");
        while (warnRs.next()) {
            int code = warnRs.getInt("Code");
            if (!forTruncationOnly) {
                SQLWarning newWarning = new SQLWarning(warnRs.getString("Message"), mysqlToSqlState(code, connection.getUseSqlStateCodes()), code);
                if (currentWarning == null) {
                    currentWarning = newWarning;
                } else {
                    currentWarning.setNextWarning(newWarning);
                }
            } else if (code == 1265 || code == 1264) {
                SQLWarning newTruncation = new MysqlDataTruncation(warnRs.getString("Message"), 0, false, false, 0, 0, code);
                if (currentWarning == null) {
                    currentWarning = newTruncation;
                } else {
                    currentWarning.setNextWarning(newTruncation);
                }
            }
        }
        if (forTruncationOnly) {
            if (currentWarning != null) {
                throw currentWarning;
            }
        }
        SQLException reThrow3 = null;
        if (warnRs != null) {
            try {
                warnRs.close();
            } catch (SQLException sqlEx3) {
                reThrow3 = sqlEx3;
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException sqlEx4) {
                reThrow3 = sqlEx4;
            }
        }
        if (reThrow3 == null) {
            return currentWarning;
        }
        throw reThrow3;
    }

    public static void dumpSqlStatesMappingsAsXml() throws Exception {
        TreeMap<Integer, Integer> allErrorNumbers = new TreeMap<>();
        Map<Object, String> mysqlErrorNumbersToNames = new HashMap<>();
        for (Integer errorNumber : mysqlToSql99State.keySet()) {
            allErrorNumbers.put(errorNumber, errorNumber);
        }
        for (Integer errorNumber2 : mysqlToSqlState.keySet()) {
            allErrorNumbers.put(errorNumber2, errorNumber2);
        }
        Field[] possibleFields = MysqlErrorNumbers.class.getDeclaredFields();
        for (int i = 0; i < possibleFields.length; i++) {
            String fieldName = possibleFields[i].getName();
            if (fieldName.startsWith("ER_")) {
                mysqlErrorNumbersToNames.put(possibleFields[i].get((Object) null), fieldName);
            }
        }
        System.out.println("<ErrorMappings>");
        for (Integer errorNumber3 : allErrorNumbers.keySet()) {
            String sql92State = mysqlToSql99(errorNumber3.intValue());
            String oldSqlState = mysqlToXOpen(errorNumber3.intValue());
            PrintStream printStream = System.out;
            String str = "";
            StringBuilder append = new StringBuilder().append("   <ErrorMapping mysqlErrorNumber=\"").append(errorNumber3).append("\" mysqlErrorName=\"").append(mysqlErrorNumbersToNames.get(errorNumber3)).append("\" legacySqlState=\"").append(oldSqlState == null ? str : oldSqlState).append("\" sql92SqlState=\"");
            if (sql92State != null) {
                str = sql92State;
            }
            printStream.println(append.append(str).append("\"/>").toString());
        }
        System.out.println("</ErrorMappings>");
    }

    static String get(String stateCode) {
        return sqlStateMessages.get(stateCode);
    }

    private static String mysqlToSql99(int errno) {
        Integer err = Integer.valueOf(errno);
        if (mysqlToSql99State.containsKey(err)) {
            return mysqlToSql99State.get(err);
        }
        return SQL_STATE_CLI_SPECIFIC_CONDITION;
    }

    static String mysqlToSqlState(int errno, boolean useSql92States) {
        if (useSql92States) {
            return mysqlToSql99(errno);
        }
        return mysqlToXOpen(errno);
    }

    private static String mysqlToXOpen(int errno) {
        Integer err = Integer.valueOf(errno);
        if (mysqlToSqlState.containsKey(err)) {
            return mysqlToSqlState.get(err);
        }
        return SQL_STATE_GENERAL_ERROR;
    }

    public static SQLException createSQLException(String message, String sqlState, ExceptionInterceptor interceptor) {
        return createSQLException(message, sqlState, 0, interceptor);
    }

    public static SQLException createSQLException(String message, ExceptionInterceptor interceptor) {
        return createSQLException(message, interceptor, (Connection) null);
    }

    public static SQLException createSQLException(String message, ExceptionInterceptor interceptor, Connection conn) {
        return runThroughExceptionInterceptor(interceptor, new SQLException(message), conn);
    }

    public static SQLException createSQLException(String message, String sqlState, Throwable cause, ExceptionInterceptor interceptor) {
        return createSQLException(message, sqlState, cause, interceptor, (Connection) null);
    }

    public static SQLException createSQLException(String message, String sqlState, Throwable cause, ExceptionInterceptor interceptor, Connection conn) {
        SQLException sqlEx = createSQLException(message, sqlState, (ExceptionInterceptor) null);
        if (sqlEx.getCause() == null) {
            sqlEx.initCause(cause);
        }
        return runThroughExceptionInterceptor(interceptor, sqlEx, conn);
    }

    public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, ExceptionInterceptor interceptor) {
        return createSQLException(message, sqlState, vendorErrorCode, false, interceptor);
    }

    public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, boolean isTransient, ExceptionInterceptor interceptor) {
        return createSQLException(message, sqlState, vendorErrorCode, isTransient, interceptor, (Connection) null);
    }

    public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, boolean isTransient, ExceptionInterceptor interceptor, Connection conn) {
        SQLException sqlEx;
        if (sqlState != null) {
            try {
                if (sqlState.startsWith("08")) {
                    if (isTransient) {
                        if (!Util.isJdbc4()) {
                            sqlEx = new MySQLTransientConnectionException(message, sqlState, vendorErrorCode);
                        } else {
                            sqlEx = (SQLException) Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLTransientConnectionException", new Class[]{String.class, String.class, Integer.TYPE}, new Object[]{message, sqlState, Integer.valueOf(vendorErrorCode)}, interceptor);
                        }
                    } else if (!Util.isJdbc4()) {
                        sqlEx = new MySQLNonTransientConnectionException(message, sqlState, vendorErrorCode);
                    } else {
                        sqlEx = (SQLException) Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException", new Class[]{String.class, String.class, Integer.TYPE}, new Object[]{message, sqlState, Integer.valueOf(vendorErrorCode)}, interceptor);
                    }
                } else if (sqlState.startsWith("22")) {
                    if (!Util.isJdbc4()) {
                        sqlEx = new MySQLDataException(message, sqlState, vendorErrorCode);
                    } else {
                        sqlEx = (SQLException) Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLDataException", new Class[]{String.class, String.class, Integer.TYPE}, new Object[]{message, sqlState, Integer.valueOf(vendorErrorCode)}, interceptor);
                    }
                } else if (sqlState.startsWith("23")) {
                    if (!Util.isJdbc4()) {
                        sqlEx = new MySQLIntegrityConstraintViolationException(message, sqlState, vendorErrorCode);
                    } else {
                        sqlEx = (SQLException) Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException", new Class[]{String.class, String.class, Integer.TYPE}, new Object[]{message, sqlState, Integer.valueOf(vendorErrorCode)}, interceptor);
                    }
                } else if (sqlState.startsWith("42")) {
                    if (!Util.isJdbc4()) {
                        sqlEx = new MySQLSyntaxErrorException(message, sqlState, vendorErrorCode);
                    } else {
                        sqlEx = (SQLException) Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException", new Class[]{String.class, String.class, Integer.TYPE}, new Object[]{message, sqlState, Integer.valueOf(vendorErrorCode)}, interceptor);
                    }
                } else if (sqlState.startsWith("40")) {
                    if (!Util.isJdbc4()) {
                        sqlEx = new MySQLTransactionRollbackException(message, sqlState, vendorErrorCode);
                    } else {
                        sqlEx = (SQLException) Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLTransactionRollbackException", new Class[]{String.class, String.class, Integer.TYPE}, new Object[]{message, sqlState, Integer.valueOf(vendorErrorCode)}, interceptor);
                    }
                } else if (!sqlState.startsWith(SQL_STATE_ER_QUERY_INTERRUPTED)) {
                    sqlEx = new SQLException(message, sqlState, vendorErrorCode);
                } else if (!Util.isJdbc4()) {
                    sqlEx = new MySQLQueryInterruptedException(message, sqlState, vendorErrorCode);
                } else {
                    sqlEx = (SQLException) Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLQueryInterruptedException", new Class[]{String.class, String.class, Integer.TYPE}, new Object[]{message, sqlState, Integer.valueOf(vendorErrorCode)}, interceptor);
                }
            } catch (SQLException sqlEx2) {
                return runThroughExceptionInterceptor(interceptor, new SQLException("Unable to create correct SQLException class instance, error class/codes may be incorrect. Reason: " + Util.stackTraceToString(sqlEx2), SQL_STATE_GENERAL_ERROR), conn);
            }
        } else {
            sqlEx = new SQLException(message, sqlState, vendorErrorCode);
        }
        return runThroughExceptionInterceptor(interceptor, sqlEx, conn);
    }

    public static SQLException createCommunicationsException(MySQLConnection conn, long lastPacketSentTimeMs, long lastPacketReceivedTimeMs, Exception underlyingException, ExceptionInterceptor interceptor) {
        SQLException exToReturn;
        if (!Util.isJdbc4()) {
            exToReturn = new CommunicationsException(conn, lastPacketSentTimeMs, lastPacketReceivedTimeMs, underlyingException);
        } else {
            try {
                exToReturn = (SQLException) Util.handleNewInstance(JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR, new Object[]{conn, Long.valueOf(lastPacketSentTimeMs), Long.valueOf(lastPacketReceivedTimeMs), underlyingException}, interceptor);
            } catch (SQLException sqlEx) {
                return sqlEx;
            }
        }
        return runThroughExceptionInterceptor(interceptor, exToReturn, conn);
    }

    /* JADX WARNING: Removed duplicated region for block: B:32:0x00c8  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00f5  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00ff  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0114  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0126  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x014a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String createLinkFailureMessageBasedOnHeuristics(com.mysql.jdbc.MySQLConnection r25, long r26, long r28, java.lang.Exception r30) {
        /*
            r1 = r25
            r2 = 0
            r0 = 0
            if (r1 == 0) goto L_0x002f
            boolean r4 = r25.getInteractiveClient()
            r0 = 0
            if (r4 == 0) goto L_0x0016
            java.lang.String r5 = "interactive_timeout"
            java.lang.String r0 = r1.getServerVariable(r5)
            r5 = r0
            goto L_0x001e
        L_0x0016:
            java.lang.String r5 = "wait_timeout"
            java.lang.String r0 = r1.getServerVariable(r5)
            r5 = r0
        L_0x001e:
            if (r5 == 0) goto L_0x002e
            long r6 = java.lang.Long.parseLong(r5)     // Catch:{ NumberFormatException -> 0x0027 }
            r2 = r6
            r0 = r4
            goto L_0x002f
        L_0x0027:
            r0 = move-exception
            r6 = r0
            r0 = r6
            r2 = 0
            r0 = r4
            goto L_0x002f
        L_0x002e:
            r0 = r4
        L_0x002f:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            long r5 = java.lang.System.currentTimeMillis()
            r7 = 0
            int r9 = (r26 > r7 ? 1 : (r26 == r7 ? 0 : -1))
            if (r9 != 0) goto L_0x0040
            r9 = r5
            goto L_0x0042
        L_0x0040:
            r9 = r26
        L_0x0042:
            long r11 = r5 - r9
            r13 = 1000(0x3e8, double:4.94E-321)
            long r13 = r11 / r13
            long r15 = r5 - r28
            r17 = 0
            r18 = 0
            int r19 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r19 == 0) goto L_0x0082
            int r19 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1))
            if (r19 <= 0) goto L_0x00b7
            r17 = 1
            java.lang.StringBuilder r19 = new java.lang.StringBuilder
            r19.<init>()
            r26 = r19
            java.lang.String r18 = "CommunicationsException.2"
            java.lang.String r7 = com.mysql.jdbc.Messages.getString(r18)
            r8 = r26
            r8.append(r7)
            if (r0 != 0) goto L_0x0076
            java.lang.String r7 = "CommunicationsException.3"
            java.lang.String r7 = com.mysql.jdbc.Messages.getString(r7)
            r8.append(r7)
            goto L_0x007f
        L_0x0076:
            java.lang.String r7 = "CommunicationsException.4"
            java.lang.String r7 = com.mysql.jdbc.Messages.getString(r7)
            r8.append(r7)
        L_0x007f:
            r7 = r17
            goto L_0x00bb
        L_0x0082:
            r7 = 28800(0x7080, double:1.4229E-319)
            int r7 = (r13 > r7 ? 1 : (r13 == r7 ? 0 : -1))
            if (r7 <= 0) goto L_0x00b7
            r17 = 2
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "CommunicationsException.5"
            java.lang.String r8 = com.mysql.jdbc.Messages.getString(r8)
            r7.append(r8)
            java.lang.String r8 = "CommunicationsException.6"
            java.lang.String r8 = com.mysql.jdbc.Messages.getString(r8)
            r7.append(r8)
            java.lang.String r8 = "CommunicationsException.7"
            java.lang.String r8 = com.mysql.jdbc.Messages.getString(r8)
            r7.append(r8)
            java.lang.String r8 = "CommunicationsException.8"
            java.lang.String r8 = com.mysql.jdbc.Messages.getString(r8)
            r7.append(r8)
            r8 = r7
            r7 = r17
            goto L_0x00bb
        L_0x00b7:
            r7 = r17
            r8 = r18
        L_0x00bb:
            r17 = r0
            java.lang.String r0 = "CommunicationsException.ServerPacketTimingInfo"
            r20 = r2
            java.lang.String r2 = "CommunicationsException.ServerPacketTimingInfoNoRecv"
            r18 = 0
            r3 = 1
            if (r7 == r3) goto L_0x00f5
            r3 = 2
            if (r7 != r3) goto L_0x00d0
            r3 = r30
            r22 = r5
            goto L_0x00f9
        L_0x00d0:
            r3 = r30
            r22 = r5
            boolean r5 = r3 instanceof java.net.BindException
            if (r5 == 0) goto L_0x0144
            java.lang.String r5 = r25.getLocalSocketAddress()
            if (r5 == 0) goto L_0x00eb
            java.lang.String r5 = r25.getLocalSocketAddress()
            boolean r5 = com.mysql.jdbc.Util.interfaceExists(r5)
            if (r5 != 0) goto L_0x00eb
            java.lang.String r5 = "CommunicationsException.LocalSocketAddressNotAvailable"
            goto L_0x00ed
        L_0x00eb:
            java.lang.String r5 = "CommunicationsException.TooManyClientConnections"
        L_0x00ed:
            java.lang.String r5 = com.mysql.jdbc.Messages.getString(r5)
            r4.append(r5)
            goto L_0x0144
        L_0x00f5:
            r3 = r30
            r22 = r5
        L_0x00f9:
            r5 = 0
            int r24 = (r28 > r5 ? 1 : (r28 == r5 ? 0 : -1))
            if (r24 == 0) goto L_0x0114
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.Long r5 = java.lang.Long.valueOf(r15)
            r6[r18] = r5
            java.lang.Long r5 = java.lang.Long.valueOf(r11)
            r3 = 1
            r6[r3] = r5
            java.lang.String r5 = com.mysql.jdbc.Messages.getString(r0, r6)
            goto L_0x0121
        L_0x0114:
            r3 = 1
            java.lang.Object[] r5 = new java.lang.Object[r3]
            java.lang.Long r3 = java.lang.Long.valueOf(r11)
            r5[r18] = r3
            java.lang.String r5 = com.mysql.jdbc.Messages.getString(r2, r5)
        L_0x0121:
            r4.append(r5)
            if (r8 == 0) goto L_0x0129
            r4.append(r8)
        L_0x0129:
            java.lang.String r3 = "CommunicationsException.11"
            java.lang.String r3 = com.mysql.jdbc.Messages.getString(r3)
            r4.append(r3)
            java.lang.String r3 = "CommunicationsException.12"
            java.lang.String r3 = com.mysql.jdbc.Messages.getString(r3)
            r4.append(r3)
            java.lang.String r3 = "CommunicationsException.13"
            java.lang.String r3 = com.mysql.jdbc.Messages.getString(r3)
            r4.append(r3)
        L_0x0144:
            int r3 = r4.length()
            if (r3 != 0) goto L_0x0191
            java.lang.String r3 = "CommunicationsException.20"
            java.lang.String r3 = com.mysql.jdbc.Messages.getString(r3)
            r4.append(r3)
            if (r1 == 0) goto L_0x0191
            boolean r3 = r25.getMaintainTimeStats()
            if (r3 == 0) goto L_0x0191
            boolean r3 = r25.getParanoid()
            if (r3 != 0) goto L_0x0191
            java.lang.String r3 = "\n\n"
            r4.append(r3)
            r5 = 0
            int r3 = (r28 > r5 ? 1 : (r28 == r5 ? 0 : -1))
            if (r3 == 0) goto L_0x0181
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.Long r3 = java.lang.Long.valueOf(r15)
            r2[r18] = r3
            java.lang.Long r3 = java.lang.Long.valueOf(r11)
            r5 = 1
            r2[r5] = r3
            java.lang.String r0 = com.mysql.jdbc.Messages.getString(r0, r2)
            goto L_0x018e
        L_0x0181:
            r5 = 1
            java.lang.Object[] r0 = new java.lang.Object[r5]
            java.lang.Long r3 = java.lang.Long.valueOf(r11)
            r0[r18] = r3
            java.lang.String r0 = com.mysql.jdbc.Messages.getString(r2, r0)
        L_0x018e:
            r4.append(r0)
        L_0x0191:
            java.lang.String r0 = r4.toString()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.SQLError.createLinkFailureMessageBasedOnHeuristics(com.mysql.jdbc.MySQLConnection, long, long, java.lang.Exception):java.lang.String");
    }

    private static SQLException runThroughExceptionInterceptor(ExceptionInterceptor exInterceptor, SQLException sqlEx, Connection conn) {
        SQLException interceptedEx;
        if (exInterceptor == null || (interceptedEx = exInterceptor.interceptException(sqlEx, conn)) == null) {
            return sqlEx;
        }
        return interceptedEx;
    }

    public static SQLException createBatchUpdateException(SQLException underlyingEx, long[] updateCounts, ExceptionInterceptor interceptor) throws SQLException {
        SQLException newEx;
        if (Util.isJdbc42()) {
            newEx = (SQLException) Util.getInstance("java.sql.BatchUpdateException", new Class[]{String.class, String.class, Integer.TYPE, long[].class, Throwable.class}, new Object[]{underlyingEx.getMessage(), underlyingEx.getSQLState(), Integer.valueOf(underlyingEx.getErrorCode()), updateCounts, underlyingEx}, interceptor);
        } else {
            newEx = new BatchUpdateException(underlyingEx.getMessage(), underlyingEx.getSQLState(), underlyingEx.getErrorCode(), Util.truncateAndConvertToInt(updateCounts));
            newEx.initCause(underlyingEx);
        }
        return runThroughExceptionInterceptor(interceptor, newEx, (Connection) null);
    }

    public static SQLException createSQLFeatureNotSupportedException() throws SQLException {
        if (Util.isJdbc4()) {
            return (SQLException) Util.getInstance("java.sql.SQLFeatureNotSupportedException", (Class<?>[]) null, (Object[]) null, (ExceptionInterceptor) null);
        }
        return new NotImplemented();
    }

    public static SQLException createSQLFeatureNotSupportedException(String message, String sqlState, ExceptionInterceptor interceptor) throws SQLException {
        SQLException newEx;
        if (Util.isJdbc4()) {
            newEx = (SQLException) Util.getInstance("java.sql.SQLFeatureNotSupportedException", new Class[]{String.class, String.class}, new Object[]{message, sqlState}, interceptor);
        } else {
            newEx = new NotImplemented();
        }
        return runThroughExceptionInterceptor(interceptor, newEx, (Connection) null);
    }
}
