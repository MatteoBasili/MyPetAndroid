package com.mysql.jdbc;

import androidx.core.internal.view.SupportMenu;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CallableStatement extends PreparedStatement implements java.sql.CallableStatement {
    protected static final Constructor<?> JDBC_4_CSTMT_2_ARGS_CTOR;
    protected static final Constructor<?> JDBC_4_CSTMT_4_ARGS_CTOR;
    private static final int NOT_OUTPUT_PARAMETER_INDICATOR = Integer.MIN_VALUE;
    private static final String PARAMETER_NAMESPACE_PREFIX = "@com_mysql_jdbc_outparam_";
    protected boolean callingStoredFunction = false;
    private ResultSetInternalMethods functionReturnValueResults;
    private boolean hasOutputParams = false;
    protected boolean outputParamWasNull = false;
    private ResultSetInternalMethods outputParameterResults;
    protected CallableStatementParamInfo paramInfo;
    private int[] parameterIndexToRsIndex;
    /* access modifiers changed from: private */
    public int[] placeholderToParameterIndexMap;
    private CallableStatementParam returnValueParam;

    static {
        if (Util.isJdbc4()) {
            try {
                String jdbc4ClassName = Util.isJdbc42() ? "com.mysql.jdbc.JDBC42CallableStatement" : "com.mysql.jdbc.JDBC4CallableStatement";
                JDBC_4_CSTMT_2_ARGS_CTOR = Class.forName(jdbc4ClassName).getConstructor(new Class[]{MySQLConnection.class, CallableStatementParamInfo.class});
                JDBC_4_CSTMT_4_ARGS_CTOR = Class.forName(jdbc4ClassName).getConstructor(new Class[]{MySQLConnection.class, String.class, String.class, Boolean.TYPE});
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e2) {
                throw new RuntimeException(e2);
            } catch (ClassNotFoundException e3) {
                throw new RuntimeException(e3);
            }
        } else {
            JDBC_4_CSTMT_4_ARGS_CTOR = null;
            JDBC_4_CSTMT_2_ARGS_CTOR = null;
        }
    }

    protected static class CallableStatementParam {
        int desiredJdbcType;
        int inOutModifier;
        int index;
        boolean isIn;
        boolean isOut;
        int jdbcType;
        short nullability;
        String paramName;
        int precision;
        int scale;
        String typeName;

        CallableStatementParam(String name, int idx, boolean in, boolean out, int jdbcType2, String typeName2, int precision2, int scale2, short nullability2, int inOutModifier2) {
            this.paramName = name;
            this.isIn = in;
            this.isOut = out;
            this.index = idx;
            this.jdbcType = jdbcType2;
            this.typeName = typeName2;
            this.precision = precision2;
            this.scale = scale2;
            this.nullability = nullability2;
            this.inOutModifier = inOutModifier2;
        }

        /* access modifiers changed from: protected */
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    protected class CallableStatementParamInfo implements ParameterMetaData {
        String catalogInUse;
        boolean isFunctionCall;
        boolean isReadOnlySafeChecked = false;
        boolean isReadOnlySafeProcedure = false;
        String nativeSql;
        int numParameters;
        List<CallableStatementParam> parameterList;
        Map<String, CallableStatementParam> parameterMap;

        CallableStatementParamInfo(CallableStatementParamInfo fullParamInfo) {
            this.nativeSql = CallableStatement.this.originalSql;
            this.catalogInUse = CallableStatement.this.currentCatalog;
            this.isFunctionCall = fullParamInfo.isFunctionCall;
            this.isReadOnlySafeProcedure = fullParamInfo.isReadOnlySafeProcedure;
            this.isReadOnlySafeChecked = fullParamInfo.isReadOnlySafeChecked;
            this.parameterList = new ArrayList(fullParamInfo.numParameters);
            this.parameterMap = new HashMap(fullParamInfo.numParameters);
            for (int i : CallableStatement.this.placeholderToParameterIndexMap) {
                CallableStatementParam param = fullParamInfo.parameterList.get(i);
                this.parameterList.add(param);
                this.parameterMap.put(param.paramName, param);
            }
            this.numParameters = this.parameterList.size();
        }

        CallableStatementParamInfo(ResultSet paramTypesRs) throws SQLException {
            boolean hadRows = paramTypesRs.last();
            this.nativeSql = CallableStatement.this.originalSql;
            this.catalogInUse = CallableStatement.this.currentCatalog;
            this.isFunctionCall = CallableStatement.this.callingStoredFunction;
            if (hadRows) {
                this.numParameters = paramTypesRs.getRow();
                this.parameterList = new ArrayList(this.numParameters);
                this.parameterMap = new HashMap(this.numParameters);
                paramTypesRs.beforeFirst();
                addParametersFromDBMD(paramTypesRs);
                return;
            }
            this.numParameters = 0;
        }

        private void addParametersFromDBMD(ResultSet paramTypesRs) throws SQLException {
            int inOutModifier;
            ResultSet resultSet = paramTypesRs;
            int i = 0;
            while (paramTypesRs.next() != 0) {
                String paramName = resultSet.getString(4);
                switch (resultSet.getInt(5)) {
                    case 1:
                        inOutModifier = 1;
                        break;
                    case 2:
                        inOutModifier = 2;
                        break;
                    case 4:
                    case 5:
                        inOutModifier = 4;
                        break;
                    default:
                        inOutModifier = 0;
                        break;
                }
                boolean isOutParameter = false;
                boolean isInParameter = false;
                if (i == 0 && this.isFunctionCall) {
                    isOutParameter = true;
                    isInParameter = false;
                } else if (inOutModifier == 2) {
                    isOutParameter = true;
                    isInParameter = true;
                } else if (inOutModifier == 1) {
                    isOutParameter = false;
                    isInParameter = true;
                } else if (inOutModifier == 4) {
                    isOutParameter = true;
                    isInParameter = false;
                }
                CallableStatementParam paramInfoToAdd = new CallableStatementParam(paramName, i, isInParameter, isOutParameter, resultSet.getInt(6), resultSet.getString(7), resultSet.getInt(8), resultSet.getInt(10), resultSet.getShort(12), inOutModifier);
                this.parameterList.add(paramInfoToAdd);
                this.parameterMap.put(paramName, paramInfoToAdd);
                i++;
            }
        }

        /* access modifiers changed from: protected */
        public void checkBounds(int paramIndex) throws SQLException {
            int localParamIndex = paramIndex - 1;
            if (paramIndex < 0 || localParamIndex >= CallableStatement.this.parameterCount) {
                throw SQLError.createSQLException(Messages.getString("CallableStatement.11") + paramIndex + Messages.getString("CallableStatement.12") + CallableStatement.this.parameterCount + Messages.getString("CallableStatement.13"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, CallableStatement.this.getExceptionInterceptor());
            }
        }

        /* access modifiers changed from: protected */
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        /* access modifiers changed from: package-private */
        public CallableStatementParam getParameter(int index) {
            return this.parameterList.get(index);
        }

        /* access modifiers changed from: package-private */
        public CallableStatementParam getParameter(String name) {
            return this.parameterMap.get(name);
        }

        public String getParameterClassName(int arg0) throws SQLException {
            String mysqlTypeName = getParameterTypeName(arg0);
            boolean isBinaryOrBlob = (StringUtils.indexOfIgnoreCase(mysqlTypeName, "BLOB") == -1 && StringUtils.indexOfIgnoreCase(mysqlTypeName, "BINARY") == -1) ? false : true;
            boolean isUnsigned = StringUtils.indexOfIgnoreCase(mysqlTypeName, "UNSIGNED") != -1;
            int mysqlTypeIfKnown = 0;
            if (StringUtils.startsWithIgnoreCase(mysqlTypeName, "MEDIUMINT")) {
                mysqlTypeIfKnown = 9;
            }
            return ResultSetMetaData.getClassNameForJavaType(getParameterType(arg0), isUnsigned, mysqlTypeIfKnown, isBinaryOrBlob, false, CallableStatement.this.connection.getYearIsDateType());
        }

        public int getParameterCount() throws SQLException {
            List<CallableStatementParam> list = this.parameterList;
            if (list == null) {
                return 0;
            }
            return list.size();
        }

        public int getParameterMode(int arg0) throws SQLException {
            checkBounds(arg0);
            return getParameter(arg0 - 1).inOutModifier;
        }

        public int getParameterType(int arg0) throws SQLException {
            checkBounds(arg0);
            return getParameter(arg0 - 1).jdbcType;
        }

        public String getParameterTypeName(int arg0) throws SQLException {
            checkBounds(arg0);
            return getParameter(arg0 - 1).typeName;
        }

        public int getPrecision(int arg0) throws SQLException {
            checkBounds(arg0);
            return getParameter(arg0 - 1).precision;
        }

        public int getScale(int arg0) throws SQLException {
            checkBounds(arg0);
            return getParameter(arg0 - 1).scale;
        }

        public int isNullable(int arg0) throws SQLException {
            checkBounds(arg0);
            return getParameter(arg0 - 1).nullability;
        }

        public boolean isSigned(int arg0) throws SQLException {
            checkBounds(arg0);
            return false;
        }

        /* access modifiers changed from: package-private */
        public Iterator<CallableStatementParam> iterator() {
            return this.parameterList.iterator();
        }

        /* access modifiers changed from: package-private */
        public int numberOfParameters() {
            return this.numParameters;
        }

        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            CallableStatement.this.checkClosed();
            return iface.isInstance(this);
        }

        public <T> T unwrap(Class<T> iface) throws SQLException {
            try {
                return iface.cast(this);
            } catch (ClassCastException e) {
                throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, CallableStatement.this.getExceptionInterceptor());
            }
        }
    }

    private static String mangleParameterName(String origParameterName) {
        if (origParameterName == null) {
            return null;
        }
        int offset = 0;
        if (origParameterName.length() > 0 && origParameterName.charAt(0) == '@') {
            offset = 1;
        }
        StringBuilder paramNameBuf = new StringBuilder(PARAMETER_NAMESPACE_PREFIX.length() + origParameterName.length());
        paramNameBuf.append(PARAMETER_NAMESPACE_PREFIX);
        paramNameBuf.append(origParameterName.substring(offset));
        return paramNameBuf.toString();
    }

    public CallableStatement(MySQLConnection conn, CallableStatementParamInfo paramInfo2) throws SQLException {
        super(conn, paramInfo2.nativeSql, paramInfo2.catalogInUse);
        this.paramInfo = paramInfo2;
        boolean z = paramInfo2.isFunctionCall;
        this.callingStoredFunction = z;
        if (z) {
            this.parameterCount++;
        }
        this.retrieveGeneratedKeys = true;
    }

    protected static CallableStatement getInstance(MySQLConnection conn, String sql, String catalog, boolean isFunctionCall) throws SQLException {
        if (!Util.isJdbc4()) {
            return new CallableStatement(conn, sql, catalog, isFunctionCall);
        }
        return (CallableStatement) Util.handleNewInstance(JDBC_4_CSTMT_4_ARGS_CTOR, new Object[]{conn, sql, catalog, Boolean.valueOf(isFunctionCall)}, conn.getExceptionInterceptor());
    }

    protected static CallableStatement getInstance(MySQLConnection conn, CallableStatementParamInfo paramInfo2) throws SQLException {
        if (!Util.isJdbc4()) {
            return new CallableStatement(conn, paramInfo2);
        }
        return (CallableStatement) Util.handleNewInstance(JDBC_4_CSTMT_2_ARGS_CTOR, new Object[]{conn, paramInfo2}, conn.getExceptionInterceptor());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x008f, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void generateParameterMap() throws java.sql.SQLException {
        /*
            r13 = this;
            com.mysql.jdbc.MySQLConnection r0 = r13.checkClosed()
            java.lang.Object r0 = r0.getConnectionMutex()
            monitor-enter(r0)
            com.mysql.jdbc.CallableStatement$CallableStatementParamInfo r1 = r13.paramInfo     // Catch:{ all -> 0x0090 }
            if (r1 != 0) goto L_0x000f
            monitor-exit(r0)     // Catch:{ all -> 0x0090 }
            return
        L_0x000f:
            int r1 = r1.getParameterCount()     // Catch:{ all -> 0x0090 }
            com.mysql.jdbc.CallableStatement$CallableStatementParamInfo r2 = r13.paramInfo     // Catch:{ all -> 0x0090 }
            if (r2 == 0) goto L_0x008e
            int r2 = r13.parameterCount     // Catch:{ all -> 0x0090 }
            if (r2 == r1) goto L_0x008e
            int r2 = r13.parameterCount     // Catch:{ all -> 0x0090 }
            int[] r2 = new int[r2]     // Catch:{ all -> 0x0090 }
            r13.placeholderToParameterIndexMap = r2     // Catch:{ all -> 0x0090 }
            r3 = 0
            boolean r4 = r13.callingStoredFunction     // Catch:{ all -> 0x0090 }
            if (r4 == 0) goto L_0x002a
            r5 = 0
            r2[r5] = r5     // Catch:{ all -> 0x0090 }
            r3 = 1
        L_0x002a:
            if (r4 == 0) goto L_0x0031
            java.lang.String r2 = r13.originalSql     // Catch:{ all -> 0x0090 }
            java.lang.String r4 = "SELECT"
            goto L_0x0035
        L_0x0031:
            java.lang.String r2 = r13.originalSql     // Catch:{ all -> 0x0090 }
            java.lang.String r4 = "CALL"
        L_0x0035:
            int r2 = com.mysql.jdbc.StringUtils.indexOfIgnoreCase(r2, r4)     // Catch:{ all -> 0x0090 }
            r4 = -1
            if (r2 == r4) goto L_0x008e
            java.lang.String r5 = r13.originalSql     // Catch:{ all -> 0x0090 }
            r6 = 40
            int r7 = r2 + 4
            int r5 = r5.indexOf(r6, r7)     // Catch:{ all -> 0x0090 }
            if (r5 == r4) goto L_0x008e
            java.lang.String r7 = r13.originalSql     // Catch:{ all -> 0x0090 }
            java.lang.String r8 = ")"
            java.lang.String r9 = "'"
            java.lang.String r10 = "'"
            java.util.Set<com.mysql.jdbc.StringUtils$SearchMode> r11 = com.mysql.jdbc.StringUtils.SEARCH_MODE__ALL     // Catch:{ all -> 0x0090 }
            r6 = r5
            int r6 = com.mysql.jdbc.StringUtils.indexOfIgnoreCase((int) r6, (java.lang.String) r7, (java.lang.String) r8, (java.lang.String) r9, (java.lang.String) r10, (java.util.Set<com.mysql.jdbc.StringUtils.SearchMode>) r11)     // Catch:{ all -> 0x0090 }
            if (r6 == r4) goto L_0x008e
            java.lang.String r4 = r13.originalSql     // Catch:{ all -> 0x0090 }
            int r7 = r5 + 1
            java.lang.String r4 = r4.substring(r7, r6)     // Catch:{ all -> 0x0090 }
            java.lang.String r7 = ","
            java.lang.String r8 = "'\""
            java.lang.String r9 = "'\""
            r10 = 1
            java.util.List r4 = com.mysql.jdbc.StringUtils.split(r4, r7, r8, r9, r10)     // Catch:{ all -> 0x0090 }
            int r7 = r4.size()     // Catch:{ all -> 0x0090 }
            r8 = r3
            r9 = 0
        L_0x0072:
            if (r9 >= r7) goto L_0x008e
            java.lang.Object r10 = r4.get(r9)     // Catch:{ all -> 0x0090 }
            java.lang.String r10 = (java.lang.String) r10     // Catch:{ all -> 0x0090 }
            java.lang.String r11 = "?"
            boolean r10 = r10.equals(r11)     // Catch:{ all -> 0x0090 }
            if (r10 == 0) goto L_0x008b
            int[] r10 = r13.placeholderToParameterIndexMap     // Catch:{ all -> 0x0090 }
            int r11 = r8 + 1
            int r12 = r3 + r9
            r10[r8] = r12     // Catch:{ all -> 0x0090 }
            r8 = r11
        L_0x008b:
            int r9 = r9 + 1
            goto L_0x0072
        L_0x008e:
            monitor-exit(r0)     // Catch:{ all -> 0x0090 }
            return
        L_0x0090:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0090 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.CallableStatement.generateParameterMap():void");
    }

    public CallableStatement(MySQLConnection conn, String sql, String catalog, boolean isFunctionCall) throws SQLException {
        super(conn, sql, catalog);
        this.callingStoredFunction = isFunctionCall;
        if (!isFunctionCall) {
            if (!StringUtils.startsWithIgnoreCaseAndWs(sql, "CALL")) {
                fakeParameterTypes(false);
            } else {
                determineParameterTypes();
            }
            generateParameterMap();
        } else {
            determineParameterTypes();
            this.parameterCount++;
            generateParameterMap();
        }
        this.retrieveGeneratedKeys = true;
    }

    public void addBatch() throws SQLException {
        setOutParams();
        super.addBatch();
    }

    private CallableStatementParam checkIsOutputParam(int paramIndex) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.callingStoredFunction || paramIndex != 1) {
                checkParameterIndexBounds(paramIndex);
                int localParamIndex = paramIndex - 1;
                int[] iArr = this.placeholderToParameterIndexMap;
                if (iArr != null) {
                    localParamIndex = iArr[localParamIndex];
                }
                CallableStatementParam paramDescriptor = this.paramInfo.getParameter(localParamIndex);
                if (this.connection.getNoAccessToProcedureBodies()) {
                    paramDescriptor.isOut = true;
                    paramDescriptor.isIn = true;
                    paramDescriptor.inOutModifier = 2;
                } else if (!paramDescriptor.isOut) {
                    throw SQLError.createSQLException(Messages.getString("CallableStatement.9") + paramIndex + Messages.getString("CallableStatement.10"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                }
                this.hasOutputParams = true;
                return paramDescriptor;
            }
            if (this.returnValueParam == null) {
                this.returnValueParam = new CallableStatementParam("", 0, false, true, 12, "VARCHAR", 0, 0, 2, 5);
            }
            CallableStatementParam callableStatementParam = this.returnValueParam;
            return callableStatementParam;
        }
    }

    private void checkParameterIndexBounds(int paramIndex) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            this.paramInfo.checkBounds(paramIndex);
        }
    }

    private void checkStreamability() throws SQLException {
        if (this.hasOutputParams && createStreamingResultSet()) {
            throw SQLError.createSQLException(Messages.getString("CallableStatement.14"), SQLError.SQL_STATE_DRIVER_NOT_CAPABLE, getExceptionInterceptor());
        }
    }

    public void clearParameters() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            super.clearParameters();
            try {
                ResultSetInternalMethods resultSetInternalMethods = this.outputParameterResults;
                if (resultSetInternalMethods != null) {
                    resultSetInternalMethods.close();
                }
            } finally {
                this.outputParameterResults = null;
            }
        }
    }

    private void fakeParameterTypes(boolean isReallyProcedure) throws SQLException {
        byte[] procNameAsBytes;
        synchronized (checkClosed().getConnectionMutex()) {
            int i = 13;
            int i2 = 1;
            int i3 = 2;
            char c = 3;
            Field[] fields = {new Field("", "PROCEDURE_CAT", 1, 0), new Field("", "PROCEDURE_SCHEM", 1, 0), new Field("", "PROCEDURE_NAME", 1, 0), new Field("", "COLUMN_NAME", 1, 0), new Field("", "COLUMN_TYPE", 1, 0), new Field("", "DATA_TYPE", 5, 0), new Field("", "TYPE_NAME", 1, 0), new Field("", "PRECISION", 4, 0), new Field("", "LENGTH", 4, 0), new Field("", "SCALE", 5, 0), new Field("", "RADIX", 5, 0), new Field("", "NULLABLE", 5, 0), new Field("", "REMARKS", 1, 0)};
            byte[] bArr = null;
            String procName = isReallyProcedure ? extractProcedureName() : null;
            if (procName == null) {
                procNameAsBytes = null;
            } else {
                try {
                    procNameAsBytes = StringUtils.getBytes(procName, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    procNameAsBytes = StringUtils.s2b(procName, this.connection);
                }
            }
            ArrayList<ResultSetRow> resultRows = new ArrayList<>();
            int numOfParameters = this.callingStoredFunction ? this.parameterCount + 1 : this.parameterCount;
            int i4 = 0;
            while (i4 < numOfParameters) {
                byte[][] row = new byte[i][];
                row[0] = bArr;
                row[i2] = bArr;
                row[i3] = procNameAsBytes;
                row[c] = StringUtils.s2b(String.valueOf(i4), this.connection);
                if (!this.callingStoredFunction || i4 != 0) {
                    row[4] = StringUtils.s2b(String.valueOf(i2), this.connection);
                } else {
                    row[4] = StringUtils.s2b(String.valueOf(4), this.connection);
                }
                row[5] = StringUtils.s2b(String.valueOf(12), this.connection);
                row[6] = StringUtils.s2b("VARCHAR", this.connection);
                row[7] = StringUtils.s2b(Integer.toString(SupportMenu.USER_MASK), this.connection);
                row[8] = StringUtils.s2b(Integer.toString(SupportMenu.USER_MASK), this.connection);
                row[9] = StringUtils.s2b(Integer.toString(0), this.connection);
                row[10] = StringUtils.s2b(Integer.toString(10), this.connection);
                row[11] = StringUtils.s2b(Integer.toString(i3), this.connection);
                row[12] = null;
                resultRows.add(new ByteArrayRow(row, getExceptionInterceptor()));
                i4++;
                bArr = null;
                i = 13;
                i3 = 2;
                c = 3;
                i2 = 1;
            }
            convertGetProcedureColumnsToInternalDescriptors(DatabaseMetaData.buildResultSet(fields, resultRows, this.connection));
        }
    }

    private void determineParameterTypes() throws SQLException {
        String procName;
        String quotedId;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSet paramTypesRs = null;
            try {
                procName = extractProcedureName();
                quotedId = "";
                quotedId = this.connection.supportsQuotedIdentifiers() ? this.connection.getMetaData().getIdentifierQuoteString() : "";
            } catch (SQLException sqlEx) {
                AssertionFailedException.shouldNotHappen(sqlEx);
            } catch (Throwable sqlExRethrow) {
                SQLException sqlExRethrow2 = null;
                if (paramTypesRs != null) {
                    try {
                        paramTypesRs.close();
                    } catch (SQLException sqlEx2) {
                        sqlExRethrow2 = sqlEx2;
                    }
                }
                if (sqlExRethrow2 != null) {
                    throw sqlExRethrow2;
                }
                throw sqlExRethrow;
            }
            List<String> splitDBdotName = StringUtils.splitDBdotName(procName, "", quotedId, this.connection.isNoBackslashEscapesSet());
            String tmpCatalog = "";
            if (splitDBdotName.size() == 2) {
                tmpCatalog = splitDBdotName.get(0);
                procName = splitDBdotName.get(1);
            }
            paramTypesRs = getParamTypes((!this.connection.versionMeetsMinimum(5, 0, 2) || !(tmpCatalog.length() <= 0)) ? tmpCatalog : this.currentCatalog, procName);
            boolean hasResults = false;
            try {
                if (paramTypesRs.next()) {
                    paramTypesRs.previous();
                    hasResults = true;
                }
            } catch (Exception e) {
            }
            if (hasResults) {
                convertGetProcedureColumnsToInternalDescriptors(paramTypesRs);
            } else {
                fakeParameterTypes(true);
            }
            SQLException sqlExRethrow3 = null;
            if (paramTypesRs != null) {
                try {
                    paramTypesRs.close();
                } catch (SQLException sqlEx3) {
                    sqlExRethrow3 = sqlEx3;
                }
            }
            if (sqlExRethrow3 != null) {
                throw sqlExRethrow3;
            }
        }
    }

    /* access modifiers changed from: protected */
    public ResultSet getParamTypes(String catalog, String routineName) throws SQLException {
        boolean getProcRetFuncsCurrentValue = this.connection.getGetProceduresReturnsFunctions();
        try {
            this.connection.setGetProceduresReturnsFunctions(this.callingStoredFunction);
            return this.connection.getMetaData().getProcedureColumns(catalog, (String) null, routineName, "%");
        } finally {
            this.connection.setGetProceduresReturnsFunctions(getProcRetFuncsCurrentValue);
        }
    }

    private void convertGetProcedureColumnsToInternalDescriptors(ResultSet paramTypesRs) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            this.paramInfo = new CallableStatementParamInfo(paramTypesRs);
        }
    }

    public boolean execute() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            checkStreamability();
            setInOutParamsOnServer();
            setOutParams();
            boolean returnVal = super.execute();
            if (this.callingStoredFunction) {
                ResultSetInternalMethods resultSetInternalMethods = this.results;
                this.functionReturnValueResults = resultSetInternalMethods;
                resultSetInternalMethods.next();
                this.results = null;
            }
            retrieveOutParams();
            if (!this.callingStoredFunction) {
                return returnVal;
            }
            return false;
        }
    }

    public ResultSet executeQuery() throws SQLException {
        ResultSet execResults;
        synchronized (checkClosed().getConnectionMutex()) {
            checkStreamability();
            setInOutParamsOnServer();
            setOutParams();
            execResults = super.executeQuery();
            retrieveOutParams();
        }
        return execResults;
    }

    public int executeUpdate() throws SQLException {
        return Util.truncateAndConvertToInt(executeLargeUpdate());
    }

    private String extractProcedureName() throws SQLException {
        String sanitizedSql = StringUtils.stripComments(this.originalSql, "`\"'", "`\"'", true, false, true, true);
        int endCallIndex = StringUtils.indexOfIgnoreCase(sanitizedSql, "CALL ");
        int offset = 5;
        if (endCallIndex == -1) {
            endCallIndex = StringUtils.indexOfIgnoreCase(sanitizedSql, "SELECT ");
            offset = 7;
        }
        if (endCallIndex != -1) {
            StringBuilder nameBuf = new StringBuilder();
            String trimmedStatement = sanitizedSql.substring(endCallIndex + offset).trim();
            int statementLength = trimmedStatement.length();
            for (int i = 0; i < statementLength; i++) {
                char c = trimmedStatement.charAt(i);
                if (Character.isWhitespace(c) || c == '(' || c == '?') {
                    break;
                }
                nameBuf.append(c);
            }
            return nameBuf.toString();
        }
        throw SQLError.createSQLException(Messages.getString("CallableStatement.1"), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000f, code lost:
        if (r5.length() == 0) goto L_0x0011;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String fixParameterName(java.lang.String r5) throws java.sql.SQLException {
        /*
            r4 = this;
            com.mysql.jdbc.MySQLConnection r0 = r4.checkClosed()
            java.lang.Object r0 = r0.getConnectionMutex()
            monitor-enter(r0)
            if (r5 == 0) goto L_0x0011
            int r1 = r5.length()     // Catch:{ all -> 0x006a }
            if (r1 != 0) goto L_0x0044
        L_0x0011:
            boolean r1 = r4.hasParametersView()     // Catch:{ all -> 0x006a }
            if (r1 != 0) goto L_0x0044
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x006a }
            r1.<init>()     // Catch:{ all -> 0x006a }
            java.lang.String r2 = "CallableStatement.0"
            java.lang.String r2 = com.mysql.jdbc.Messages.getString(r2)     // Catch:{ all -> 0x006a }
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x006a }
            java.lang.StringBuilder r1 = r1.append(r5)     // Catch:{ all -> 0x006a }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x006a }
            if (r1 != 0) goto L_0x0033
            java.lang.String r1 = "CallableStatement.15"
            goto L_0x0035
        L_0x0033:
            java.lang.String r1 = "CallableStatement.16"
        L_0x0035:
            java.lang.String r1 = com.mysql.jdbc.Messages.getString(r1)     // Catch:{ all -> 0x006a }
            java.lang.String r2 = "S1009"
            com.mysql.jdbc.ExceptionInterceptor r3 = r4.getExceptionInterceptor()     // Catch:{ all -> 0x006a }
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (com.mysql.jdbc.ExceptionInterceptor) r3)     // Catch:{ all -> 0x006a }
            throw r1     // Catch:{ all -> 0x006a }
        L_0x0044:
            if (r5 != 0) goto L_0x004f
            boolean r1 = r4.hasParametersView()     // Catch:{ all -> 0x006a }
            if (r1 == 0) goto L_0x004f
            java.lang.String r1 = "nullpn"
            r5 = r1
        L_0x004f:
            com.mysql.jdbc.MySQLConnection r1 = r4.connection     // Catch:{ all -> 0x006a }
            boolean r1 = r1.getNoAccessToProcedureBodies()     // Catch:{ all -> 0x006a }
            if (r1 != 0) goto L_0x005d
            java.lang.String r1 = mangleParameterName(r5)     // Catch:{ all -> 0x006a }
            monitor-exit(r0)     // Catch:{ all -> 0x006a }
            return r1
        L_0x005d:
            java.lang.String r1 = "No access to parameters by name when connection has been configured not to access procedure bodies"
            java.lang.String r2 = "S1009"
            com.mysql.jdbc.ExceptionInterceptor r3 = r4.getExceptionInterceptor()     // Catch:{ all -> 0x006a }
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (com.mysql.jdbc.ExceptionInterceptor) r3)     // Catch:{ all -> 0x006a }
            throw r1     // Catch:{ all -> 0x006a }
        L_0x006a:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x006a }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.CallableStatement.fixParameterName(java.lang.String):java.lang.String");
    }

    public Array getArray(int i) throws SQLException {
        Array retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(i);
            retValue = rs.getArray(mapOutputParameterIndexToRsIndex(i));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public Array getArray(String parameterName) throws SQLException {
        Array retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getArray(fixParameterName(parameterName));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        BigDecimal retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getBigDecimal(mapOutputParameterIndexToRsIndex(parameterIndex));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    @Deprecated
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        BigDecimal retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getBigDecimal(mapOutputParameterIndexToRsIndex(parameterIndex), scale);
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        BigDecimal retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getBigDecimal(fixParameterName(parameterName));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public Blob getBlob(int parameterIndex) throws SQLException {
        Blob retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getBlob(mapOutputParameterIndexToRsIndex(parameterIndex));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public Blob getBlob(String parameterName) throws SQLException {
        Blob retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getBlob(fixParameterName(parameterName));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public boolean getBoolean(int parameterIndex) throws SQLException {
        boolean retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getBoolean(mapOutputParameterIndexToRsIndex(parameterIndex));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public boolean getBoolean(String parameterName) throws SQLException {
        boolean retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getBoolean(fixParameterName(parameterName));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public byte getByte(int parameterIndex) throws SQLException {
        byte retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getByte(mapOutputParameterIndexToRsIndex(parameterIndex));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public byte getByte(String parameterName) throws SQLException {
        byte retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getByte(fixParameterName(parameterName));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public byte[] getBytes(int parameterIndex) throws SQLException {
        byte[] retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getBytes(mapOutputParameterIndexToRsIndex(parameterIndex));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public byte[] getBytes(String parameterName) throws SQLException {
        byte[] retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getBytes(fixParameterName(parameterName));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public Clob getClob(int parameterIndex) throws SQLException {
        Clob retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getClob(mapOutputParameterIndexToRsIndex(parameterIndex));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public Clob getClob(String parameterName) throws SQLException {
        Clob retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getClob(fixParameterName(parameterName));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public Date getDate(int parameterIndex) throws SQLException {
        Date retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getDate(mapOutputParameterIndexToRsIndex(parameterIndex));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        Date retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getDate(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public Date getDate(String parameterName) throws SQLException {
        Date retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getDate(fixParameterName(parameterName));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public Date getDate(String parameterName, Calendar cal) throws SQLException {
        Date retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getDate(fixParameterName(parameterName), cal);
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public double getDouble(int parameterIndex) throws SQLException {
        double retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getDouble(mapOutputParameterIndexToRsIndex(parameterIndex));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public double getDouble(String parameterName) throws SQLException {
        double retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getDouble(fixParameterName(parameterName));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public float getFloat(int parameterIndex) throws SQLException {
        float retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getFloat(mapOutputParameterIndexToRsIndex(parameterIndex));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public float getFloat(String parameterName) throws SQLException {
        float retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getFloat(fixParameterName(parameterName));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public int getInt(int parameterIndex) throws SQLException {
        int retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getInt(mapOutputParameterIndexToRsIndex(parameterIndex));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public int getInt(String parameterName) throws SQLException {
        int retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getInt(fixParameterName(parameterName));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public long getLong(int parameterIndex) throws SQLException {
        long retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getLong(mapOutputParameterIndexToRsIndex(parameterIndex));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public long getLong(String parameterName) throws SQLException {
        long retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getLong(fixParameterName(parameterName));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    /* access modifiers changed from: protected */
    public int getNamedParamIndex(String paramName, boolean forOut) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.connection.getNoAccessToProcedureBodies()) {
                throw SQLError.createSQLException("No access to parameters by name when connection has been configured not to access procedure bodies", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            } else if (paramName == null || paramName.length() == 0) {
                throw SQLError.createSQLException(Messages.getString("CallableStatement.2"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            } else {
                CallableStatementParamInfo callableStatementParamInfo = this.paramInfo;
                if (callableStatementParamInfo != null) {
                    CallableStatementParam parameter = callableStatementParamInfo.getParameter(paramName);
                    CallableStatementParam namedParamInfo = parameter;
                    if (parameter != null) {
                        if (forOut) {
                            if (!namedParamInfo.isOut) {
                                throw SQLError.createSQLException(Messages.getString("CallableStatement.5") + paramName + Messages.getString("CallableStatement.6"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                            }
                        }
                        if (this.placeholderToParameterIndexMap == null) {
                            int i = namedParamInfo.index + 1;
                            return i;
                        }
                        int i2 = 0;
                        while (true) {
                            int[] iArr = this.placeholderToParameterIndexMap;
                            if (i2 >= iArr.length) {
                                throw SQLError.createSQLException("Can't find local placeholder mapping for parameter named \"" + paramName + "\".", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                            } else if (iArr[i2] == namedParamInfo.index) {
                                int i3 = i2 + 1;
                                return i3;
                            } else {
                                i2++;
                            }
                        }
                    }
                }
                throw SQLError.createSQLException(Messages.getString("CallableStatement.3") + paramName + Messages.getString("CallableStatement.4"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
    }

    public Object getObject(int parameterIndex) throws SQLException {
        Object retVal;
        synchronized (checkClosed().getConnectionMutex()) {
            CallableStatementParam paramDescriptor = checkIsOutputParam(parameterIndex);
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retVal = rs.getObjectStoredProc(mapOutputParameterIndexToRsIndex(parameterIndex), paramDescriptor.desiredJdbcType);
            this.outputParamWasNull = rs.wasNull();
        }
        return retVal;
    }

    public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
        Object retVal;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retVal = rs.getObject(mapOutputParameterIndexToRsIndex(parameterIndex), map);
            this.outputParamWasNull = rs.wasNull();
        }
        return retVal;
    }

    public Object getObject(String parameterName) throws SQLException {
        Object retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getObject(fixParameterName(parameterName));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
        Object retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getObject(fixParameterName(parameterName), map);
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
        T retVal;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retVal = ((ResultSetImpl) rs).getObject(mapOutputParameterIndexToRsIndex(parameterIndex), type);
            this.outputParamWasNull = rs.wasNull();
        }
        return retVal;
    }

    public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        T retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = ((ResultSetImpl) rs).getObject(fixParameterName(parameterName), type);
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    /* access modifiers changed from: protected */
    public ResultSetInternalMethods getOutputParameters(int paramIndex) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            this.outputParamWasNull = false;
            if (paramIndex != 1 || !this.callingStoredFunction || this.returnValueParam == null) {
                ResultSetInternalMethods resultSetInternalMethods = this.outputParameterResults;
                if (resultSetInternalMethods != null) {
                    return resultSetInternalMethods;
                }
                if (this.paramInfo.numberOfParameters() == 0) {
                    throw SQLError.createSQLException(Messages.getString("CallableStatement.7"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                }
                throw SQLError.createSQLException(Messages.getString("CallableStatement.8"), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
            }
            ResultSetInternalMethods resultSetInternalMethods2 = this.functionReturnValueResults;
            return resultSetInternalMethods2;
        }
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.placeholderToParameterIndexMap == null) {
                CallableStatementParamInfo callableStatementParamInfo = this.paramInfo;
                return callableStatementParamInfo;
            }
            CallableStatementParamInfo callableStatementParamInfo2 = new CallableStatementParamInfo(this.paramInfo);
            return callableStatementParamInfo2;
        }
    }

    public Ref getRef(int parameterIndex) throws SQLException {
        Ref retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getRef(mapOutputParameterIndexToRsIndex(parameterIndex));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public Ref getRef(String parameterName) throws SQLException {
        Ref retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getRef(fixParameterName(parameterName));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public short getShort(int parameterIndex) throws SQLException {
        short retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getShort(mapOutputParameterIndexToRsIndex(parameterIndex));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public short getShort(String parameterName) throws SQLException {
        short retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getShort(fixParameterName(parameterName));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public String getString(int parameterIndex) throws SQLException {
        String retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getString(mapOutputParameterIndexToRsIndex(parameterIndex));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public String getString(String parameterName) throws SQLException {
        String retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getString(fixParameterName(parameterName));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public Time getTime(int parameterIndex) throws SQLException {
        Time retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getTime(mapOutputParameterIndexToRsIndex(parameterIndex));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        Time retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getTime(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public Time getTime(String parameterName) throws SQLException {
        Time retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getTime(fixParameterName(parameterName));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public Time getTime(String parameterName, Calendar cal) throws SQLException {
        Time retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getTime(fixParameterName(parameterName), cal);
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        Timestamp retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getTimestamp(mapOutputParameterIndexToRsIndex(parameterIndex));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        Timestamp retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getTimestamp(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public Timestamp getTimestamp(String parameterName) throws SQLException {
        Timestamp retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getTimestamp(fixParameterName(parameterName));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        Timestamp retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getTimestamp(fixParameterName(parameterName), cal);
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public URL getURL(int parameterIndex) throws SQLException {
        URL retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(parameterIndex);
            retValue = rs.getURL(mapOutputParameterIndexToRsIndex(parameterIndex));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    public URL getURL(String parameterName) throws SQLException {
        URL retValue;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods rs = getOutputParameters(0);
            retValue = rs.getURL(fixParameterName(parameterName));
            this.outputParamWasNull = rs.wasNull();
        }
        return retValue;
    }

    /* access modifiers changed from: protected */
    public int mapOutputParameterIndexToRsIndex(int paramIndex) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.returnValueParam != null && paramIndex == 1) {
                return 1;
            }
            checkParameterIndexBounds(paramIndex);
            int localParamIndex = paramIndex - 1;
            int[] iArr = this.placeholderToParameterIndexMap;
            if (iArr != null) {
                localParamIndex = iArr[localParamIndex];
            }
            int rsIndex = this.parameterIndexToRsIndex[localParamIndex];
            if (rsIndex != Integer.MIN_VALUE) {
                int i = rsIndex + 1;
                return i;
            }
            throw SQLError.createSQLException(Messages.getString("CallableStatement.21") + paramIndex + Messages.getString("CallableStatement.22"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
    }

    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        checkIsOutputParam(parameterIndex).desiredJdbcType = sqlType;
    }

    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        registerOutParameter(parameterIndex, sqlType);
    }

    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
        checkIsOutputParam(parameterIndex);
    }

    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            registerOutParameter(getNamedParamIndex(parameterName, true), sqlType);
        }
    }

    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        registerOutParameter(getNamedParamIndex(parameterName, true), sqlType);
    }

    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        registerOutParameter(getNamedParamIndex(parameterName, true), sqlType, typeName);
    }

    private void retrieveOutParams() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            int numParameters = this.paramInfo.numberOfParameters();
            this.parameterIndexToRsIndex = new int[numParameters];
            for (int i = 0; i < numParameters; i++) {
                this.parameterIndexToRsIndex[i] = Integer.MIN_VALUE;
            }
            int localParamIndex = 0;
            if (numParameters > 0) {
                StringBuilder outParameterQuery = new StringBuilder("SELECT ");
                boolean firstParam = true;
                boolean hadOutputParams = false;
                Iterator<CallableStatementParam> paramIter = this.paramInfo.iterator();
                while (paramIter.hasNext()) {
                    CallableStatementParam retrParamInfo = paramIter.next();
                    if (retrParamInfo.isOut) {
                        hadOutputParams = true;
                        int localParamIndex2 = localParamIndex + 1;
                        this.parameterIndexToRsIndex[retrParamInfo.index] = localParamIndex;
                        if (retrParamInfo.paramName == null && hasParametersView()) {
                            retrParamInfo.paramName = "nullnp" + retrParamInfo.index;
                        }
                        String outParameterName = mangleParameterName(retrParamInfo.paramName);
                        if (!firstParam) {
                            outParameterQuery.append(",");
                        } else {
                            firstParam = false;
                        }
                        if (!outParameterName.startsWith("@")) {
                            outParameterQuery.append('@');
                        }
                        outParameterQuery.append(outParameterName);
                        localParamIndex = localParamIndex2;
                    }
                }
                if (hadOutputParams) {
                    Statement outParameterStmt = null;
                    try {
                        Statement outParameterStmt2 = this.connection.createStatement();
                        ResultSetInternalMethods copy = ((ResultSetInternalMethods) outParameterStmt2.executeQuery(outParameterQuery.toString())).copy();
                        this.outputParameterResults = copy;
                        if (!copy.next()) {
                            this.outputParameterResults.close();
                            this.outputParameterResults = null;
                        }
                        if (outParameterStmt2 != null) {
                            outParameterStmt2.close();
                        }
                    } catch (Throwable th) {
                        if (outParameterStmt != null) {
                            outParameterStmt.close();
                        }
                        throw th;
                    }
                } else {
                    this.outputParameterResults = null;
                }
            } else {
                this.outputParameterResults = null;
            }
        }
    }

    public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
        setAsciiStream(getNamedParamIndex(parameterName, false), x, length);
    }

    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        setBigDecimal(getNamedParamIndex(parameterName, false), x);
    }

    public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
        setBinaryStream(getNamedParamIndex(parameterName, false), x, length);
    }

    public void setBoolean(String parameterName, boolean x) throws SQLException {
        setBoolean(getNamedParamIndex(parameterName, false), x);
    }

    public void setByte(String parameterName, byte x) throws SQLException {
        setByte(getNamedParamIndex(parameterName, false), x);
    }

    public void setBytes(String parameterName, byte[] x) throws SQLException {
        setBytes(getNamedParamIndex(parameterName, false), x);
    }

    public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
        setCharacterStream(getNamedParamIndex(parameterName, false), reader, length);
    }

    public void setDate(String parameterName, Date x) throws SQLException {
        setDate(getNamedParamIndex(parameterName, false), x);
    }

    public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
        setDate(getNamedParamIndex(parameterName, false), x, cal);
    }

    public void setDouble(String parameterName, double x) throws SQLException {
        setDouble(getNamedParamIndex(parameterName, false), x);
    }

    public void setFloat(String parameterName, float x) throws SQLException {
        setFloat(getNamedParamIndex(parameterName, false), x);
    }

    private void setInOutParamsOnServer() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.paramInfo.numParameters > 0) {
                Iterator<CallableStatementParam> paramIter = this.paramInfo.iterator();
                while (paramIter.hasNext()) {
                    CallableStatementParam inParamInfo = paramIter.next();
                    if (inParamInfo.isOut && inParamInfo.isIn) {
                        if (inParamInfo.paramName == null && hasParametersView()) {
                            inParamInfo.paramName = "nullnp" + inParamInfo.index;
                        }
                        String inOutParameterName = mangleParameterName(inParamInfo.paramName);
                        StringBuilder queryBuf = new StringBuilder(inOutParameterName.length() + 4 + 1 + 1);
                        queryBuf.append("SET ");
                        queryBuf.append(inOutParameterName);
                        queryBuf.append("=?");
                        PreparedStatement setPstmt = null;
                        try {
                            PreparedStatement setPstmt2 = (PreparedStatement) ((Wrapper) this.connection.clientPrepareStatement(queryBuf.toString())).unwrap(PreparedStatement.class);
                            if (this.isNull[inParamInfo.index]) {
                                setPstmt2.setBytesNoEscapeNoQuotes(1, "NULL".getBytes());
                            } else {
                                byte[] parameterAsBytes = getBytesRepresentation(inParamInfo.index);
                                if (parameterAsBytes == null) {
                                    setPstmt2.setNull(1, 0);
                                } else if (parameterAsBytes.length <= 8 || parameterAsBytes[0] != 95 || parameterAsBytes[1] != 98 || parameterAsBytes[2] != 105 || parameterAsBytes[3] != 110 || parameterAsBytes[4] != 97 || parameterAsBytes[5] != 114 || parameterAsBytes[6] != 121 || parameterAsBytes[7] != 39) {
                                    switch (inParamInfo.desiredJdbcType) {
                                        case -7:
                                        case -4:
                                        case -3:
                                        case -2:
                                        case 2000:
                                        case 2004:
                                            setPstmt2.setBytes(1, parameterAsBytes);
                                            break;
                                        default:
                                            setPstmt2.setBytesNoEscape(1, parameterAsBytes);
                                            break;
                                    }
                                } else {
                                    setPstmt2.setBytesNoEscapeNoQuotes(1, parameterAsBytes);
                                }
                            }
                            setPstmt2.executeUpdate();
                            if (setPstmt2 != null) {
                                setPstmt2.close();
                            }
                        } catch (Throwable th) {
                            if (setPstmt != null) {
                                setPstmt.close();
                            }
                            throw th;
                        }
                    }
                }
            }
        }
    }

    public void setInt(String parameterName, int x) throws SQLException {
        setInt(getNamedParamIndex(parameterName, false), x);
    }

    public void setLong(String parameterName, long x) throws SQLException {
        setLong(getNamedParamIndex(parameterName, false), x);
    }

    public void setNull(String parameterName, int sqlType) throws SQLException {
        setNull(getNamedParamIndex(parameterName, false), sqlType);
    }

    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        setNull(getNamedParamIndex(parameterName, false), sqlType, typeName);
    }

    public void setObject(String parameterName, Object x) throws SQLException {
        setObject(getNamedParamIndex(parameterName, false), x);
    }

    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        setObject(getNamedParamIndex(parameterName, false), x, targetSqlType);
    }

    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
    }

    private void setOutParams() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.paramInfo.numParameters > 0) {
                Iterator<CallableStatementParam> paramIter = this.paramInfo.iterator();
                while (paramIter.hasNext()) {
                    CallableStatementParam outParamInfo = paramIter.next();
                    if (!this.callingStoredFunction && outParamInfo.isOut) {
                        if (outParamInfo.paramName == null && hasParametersView()) {
                            outParamInfo.paramName = "nullnp" + outParamInfo.index;
                        }
                        String outParameterName = mangleParameterName(outParamInfo.paramName);
                        int outParamIndex = 0;
                        if (this.placeholderToParameterIndexMap == null) {
                            outParamIndex = outParamInfo.index + 1;
                        } else {
                            boolean found = false;
                            int i = 0;
                            while (true) {
                                int[] iArr = this.placeholderToParameterIndexMap;
                                if (i >= iArr.length) {
                                    break;
                                } else if (iArr[i] == outParamInfo.index) {
                                    outParamIndex = i + 1;
                                    found = true;
                                    break;
                                } else {
                                    i++;
                                }
                            }
                            if (!found) {
                                throw SQLError.createSQLException(Messages.getString("CallableStatement.21") + outParamInfo.paramName + Messages.getString("CallableStatement.22"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                            }
                        }
                        setBytesNoEscapeNoQuotes(outParamIndex, StringUtils.getBytes(outParameterName, this.charConverter, this.charEncoding, this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), getExceptionInterceptor()));
                    }
                }
            }
        }
    }

    public void setShort(String parameterName, short x) throws SQLException {
        setShort(getNamedParamIndex(parameterName, false), x);
    }

    public void setString(String parameterName, String x) throws SQLException {
        setString(getNamedParamIndex(parameterName, false), x);
    }

    public void setTime(String parameterName, Time x) throws SQLException {
        setTime(getNamedParamIndex(parameterName, false), x);
    }

    public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
        setTime(getNamedParamIndex(parameterName, false), x, cal);
    }

    public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
        setTimestamp(getNamedParamIndex(parameterName, false), x);
    }

    public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
        setTimestamp(getNamedParamIndex(parameterName, false), x, cal);
    }

    public void setURL(String parameterName, URL val) throws SQLException {
        setURL(getNamedParamIndex(parameterName, false), val);
    }

    public boolean wasNull() throws SQLException {
        boolean z;
        synchronized (checkClosed().getConnectionMutex()) {
            z = this.outputParamWasNull;
        }
        return z;
    }

    public int[] executeBatch() throws SQLException {
        return Util.truncateAndConvertToInt(executeLargeBatch());
    }

    /* access modifiers changed from: protected */
    public int getParameterIndexOffset() {
        if (this.callingStoredFunction) {
            return -1;
        }
        return super.getParameterIndexOffset();
    }

    public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
        setAsciiStream(getNamedParamIndex(parameterName, false), x);
    }

    public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
        setAsciiStream(getNamedParamIndex(parameterName, false), x, length);
    }

    public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
        setBinaryStream(getNamedParamIndex(parameterName, false), x);
    }

    public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
        setBinaryStream(getNamedParamIndex(parameterName, false), x, length);
    }

    public void setBlob(String parameterName, Blob x) throws SQLException {
        setBlob(getNamedParamIndex(parameterName, false), x);
    }

    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
        setBlob(getNamedParamIndex(parameterName, false), inputStream);
    }

    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        setBlob(getNamedParamIndex(parameterName, false), inputStream, length);
    }

    public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
        setCharacterStream(getNamedParamIndex(parameterName, false), reader);
    }

    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
        setCharacterStream(getNamedParamIndex(parameterName, false), reader, length);
    }

    public void setClob(String parameterName, Clob x) throws SQLException {
        setClob(getNamedParamIndex(parameterName, false), x);
    }

    public void setClob(String parameterName, Reader reader) throws SQLException {
        setClob(getNamedParamIndex(parameterName, false), reader);
    }

    public void setClob(String parameterName, Reader reader, long length) throws SQLException {
        setClob(getNamedParamIndex(parameterName, false), reader, length);
    }

    public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
        setNCharacterStream(getNamedParamIndex(parameterName, false), value);
    }

    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
        setNCharacterStream(getNamedParamIndex(parameterName, false), value, length);
    }

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
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:693)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:693)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:598)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    private boolean checkReadOnlyProcedure() throws java.sql.SQLException {
        /*
            r10 = this;
            com.mysql.jdbc.MySQLConnection r0 = r10.checkClosed()
            java.lang.Object r0 = r0.getConnectionMutex()
            monitor-enter(r0)
            com.mysql.jdbc.MySQLConnection r1 = r10.connection     // Catch:{ all -> 0x00fb }
            boolean r1 = r1.getNoAccessToProcedureBodies()     // Catch:{ all -> 0x00fb }
            r2 = 0
            if (r1 == 0) goto L_0x0014
            monitor-exit(r0)     // Catch:{ all -> 0x00fb }
            return r2
        L_0x0014:
            com.mysql.jdbc.CallableStatement$CallableStatementParamInfo r1 = r10.paramInfo     // Catch:{ all -> 0x00fb }
            boolean r1 = r1.isReadOnlySafeChecked     // Catch:{ all -> 0x00fb }
            if (r1 == 0) goto L_0x0020
            com.mysql.jdbc.CallableStatement$CallableStatementParamInfo r1 = r10.paramInfo     // Catch:{ all -> 0x00fb }
            boolean r1 = r1.isReadOnlySafeProcedure     // Catch:{ all -> 0x00fb }
            monitor-exit(r0)     // Catch:{ all -> 0x00fb }
            return r1
        L_0x0020:
            r1 = 0
            r3 = 0
            java.lang.String r4 = r10.extractProcedureName()     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            java.lang.String r5 = r10.currentCatalog     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            java.lang.String r6 = "."
            int r6 = r4.indexOf(r6)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            r7 = -1
            r8 = 1
            if (r6 == r7) goto L_0x0078
            java.lang.String r6 = "."
            int r6 = r4.indexOf(r6)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            java.lang.String r6 = r4.substring(r2, r6)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            r5 = r6
            java.lang.String r6 = "`"
            boolean r6 = com.mysql.jdbc.StringUtils.startsWithIgnoreCaseAndWs((java.lang.String) r5, (java.lang.String) r6)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            if (r6 == 0) goto L_0x005b
            java.lang.String r6 = r5.trim()     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            java.lang.String r7 = "`"
            boolean r6 = r6.endsWith(r7)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            if (r6 == 0) goto L_0x005b
            int r6 = r5.length()     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            int r6 = r6 - r8
            java.lang.String r6 = r5.substring(r8, r6)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            r5 = r6
        L_0x005b:
            java.lang.String r6 = "."
            int r6 = r4.indexOf(r6)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            int r6 = r6 + r8
            java.lang.String r6 = r4.substring(r6)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            r4 = r6
            byte[] r6 = com.mysql.jdbc.StringUtils.getBytes((java.lang.String) r4)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            java.lang.String r7 = "`"
            java.lang.String r9 = "`"
            byte[] r6 = com.mysql.jdbc.StringUtils.stripEnclosure(r6, r7, r9)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            java.lang.String r6 = com.mysql.jdbc.StringUtils.toString(r6)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            r4 = r6
        L_0x0078:
            com.mysql.jdbc.MySQLConnection r6 = r10.connection     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            java.lang.String r7 = "SELECT SQL_DATA_ACCESS FROM information_schema.routines WHERE routine_schema = ? AND routine_name = ?"
            java.sql.PreparedStatement r6 = r6.prepareStatement(r7)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            r3 = r6
            r3.setMaxRows(r2)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            r3.setFetchSize(r2)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            r3.setString(r8, r5)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            r6 = 2
            r3.setString(r6, r4)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            java.sql.ResultSet r6 = r3.executeQuery()     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            r1 = r6
            boolean r6 = r1.next()     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            if (r6 == 0) goto L_0x00ca
            java.lang.String r6 = r1.getString(r8)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            java.lang.String r7 = "READS SQL DATA"
            boolean r7 = r7.equalsIgnoreCase(r6)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            if (r7 != 0) goto L_0x00ad
            java.lang.String r7 = "NO SQL"
            boolean r7 = r7.equalsIgnoreCase(r6)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            if (r7 == 0) goto L_0x00ca
        L_0x00ad:
            com.mysql.jdbc.CallableStatement$CallableStatementParamInfo r7 = r10.paramInfo     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            monitor-enter(r7)     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
            com.mysql.jdbc.CallableStatement$CallableStatementParamInfo r9 = r10.paramInfo     // Catch:{ all -> 0x00c7 }
            r9.isReadOnlySafeChecked = r8     // Catch:{ all -> 0x00c7 }
            com.mysql.jdbc.CallableStatement$CallableStatementParamInfo r9 = r10.paramInfo     // Catch:{ all -> 0x00c7 }
            r9.isReadOnlySafeProcedure = r8     // Catch:{ all -> 0x00c7 }
            monitor-exit(r7)     // Catch:{ all -> 0x00c7 }
            if (r1 == 0) goto L_0x00bf
            r1.close()     // Catch:{ all -> 0x00fb }
        L_0x00bf:
            if (r3 == 0) goto L_0x00c4
            r3.close()     // Catch:{ all -> 0x00fb }
        L_0x00c4:
            r2 = r6
            monitor-exit(r0)     // Catch:{ all -> 0x00fb }
            return r8
        L_0x00c7:
            r8 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x00c7 }
            throw r8     // Catch:{ SQLException -> 0x00e3, all -> 0x00d6 }
        L_0x00ca:
            if (r1 == 0) goto L_0x00d0
            r1.close()     // Catch:{ all -> 0x00fb }
        L_0x00d0:
            if (r3 == 0) goto L_0x00ef
            r3.close()     // Catch:{ all -> 0x00fb }
            goto L_0x00ef
        L_0x00d6:
            r2 = move-exception
            if (r1 == 0) goto L_0x00dc
            r1.close()     // Catch:{ all -> 0x00fb }
        L_0x00dc:
            if (r3 == 0) goto L_0x00e1
            r3.close()     // Catch:{ all -> 0x00fb }
        L_0x00e1:
            throw r2     // Catch:{ all -> 0x00fb }
        L_0x00e3:
            r4 = move-exception
            if (r1 == 0) goto L_0x00ea
            r1.close()     // Catch:{ all -> 0x00fb }
        L_0x00ea:
            if (r3 == 0) goto L_0x00ef
            r3.close()     // Catch:{ all -> 0x00fb }
        L_0x00ef:
            com.mysql.jdbc.CallableStatement$CallableStatementParamInfo r4 = r10.paramInfo     // Catch:{ all -> 0x00fb }
            r4.isReadOnlySafeChecked = r2     // Catch:{ all -> 0x00fb }
            com.mysql.jdbc.CallableStatement$CallableStatementParamInfo r4 = r10.paramInfo     // Catch:{ all -> 0x00fb }
            r4.isReadOnlySafeProcedure = r2     // Catch:{ all -> 0x00fb }
            monitor-exit(r0)     // Catch:{ all -> 0x00fb }
            return r2
        L_0x00fb:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00fb }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.CallableStatement.checkReadOnlyProcedure():boolean");
    }

    /* access modifiers changed from: protected */
    public boolean checkReadOnlySafeStatement() throws SQLException {
        return super.checkReadOnlySafeStatement() || checkReadOnlyProcedure();
    }

    private boolean hasParametersView() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            try {
                if (!this.connection.versionMeetsMinimum(5, 5, 0)) {
                    return false;
                }
                boolean z = new DatabaseMetaDataUsingInfoSchema(this.connection, this.connection.getCatalog()).gethasParametersView();
                return z;
            } catch (SQLException e) {
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public long executeLargeUpdate() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            checkStreamability();
            if (this.callingStoredFunction) {
                execute();
                return -1;
            }
            setInOutParamsOnServer();
            setOutParams();
            long returnVal = super.executeLargeUpdate();
            retrieveOutParams();
            return returnVal;
        }
    }

    public long[] executeLargeBatch() throws SQLException {
        if (!this.hasOutputParams) {
            return super.executeLargeBatch();
        }
        throw SQLError.createSQLException("Can't call executeBatch() on CallableStatement with OUTPUT parameters", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
    }
}
