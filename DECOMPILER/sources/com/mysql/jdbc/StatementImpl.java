package com.mysql.jdbc;

import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class StatementImpl implements Statement {
    protected static final String[] ON_DUPLICATE_KEY_UPDATE_CLAUSE = {"ON", "DUPLICATE", "KEY", "UPDATE"};
    protected static final String PING_MARKER = "/* ping */";
    public static final byte USES_VARIABLES_FALSE = 0;
    public static final byte USES_VARIABLES_TRUE = 1;
    public static final byte USES_VARIABLES_UNKNOWN = -1;
    static int statementCounter = 1;
    protected List<Object> batchedArgs;
    protected ArrayList<ResultSetRow> batchedGeneratedKeys;
    protected Object cancelTimeoutMutex = new Object();
    protected SingleByteCharsetConverter charConverter = null;
    protected String charEncoding = null;
    protected boolean clearWarningsCalled;
    private boolean closeOnCompletion;
    protected volatile MySQLConnection connection = null;
    protected boolean continueBatchOnError;
    protected String currentCatalog = null;
    protected boolean doEscapeProcessing;
    private ExceptionInterceptor exceptionInterceptor;
    private int fetchSize;
    protected ResultSetInternalMethods generatedKeysResults;
    protected boolean holdResultsOpenOverClose;
    protected boolean isClosed;
    private boolean isImplicitlyClosingResults;
    private boolean isPoolable;
    protected long lastInsertId;
    protected boolean lastQueryIsOnDupKeyUpdate;
    private InputStream localInfileInputStream;
    protected int maxFieldSize;
    protected int maxRows;
    protected Set<ResultSetInternalMethods> openResults;
    private int originalFetchSize;
    private int originalResultSetType;
    protected boolean pedantic;
    protected Reference<MySQLConnection> physicalConnection = null;
    protected PingTarget pingTarget;
    protected boolean profileSQL;
    protected int resultSetConcurrency;
    protected int resultSetType;
    protected ResultSetInternalMethods results;
    protected boolean retrieveGeneratedKeys;
    protected boolean sendFractionalSeconds;
    protected final AtomicBoolean statementExecuting;
    protected int statementId;
    protected int timeoutInMillis;
    protected long updateCount;
    protected boolean useLegacyDatetimeCode;
    protected boolean useUsageAdvisor;
    protected final boolean version5013OrNewer;
    protected SQLWarning warningChain;
    protected boolean wasCancelled = false;
    protected boolean wasCancelledByTimeout = false;

    class CancelTask extends TimerTask {
        SQLException caughtWhileCancelling = null;
        long origConnId = 0;
        Properties origConnProps = null;
        String origConnURL = "";
        StatementImpl toCancel;

        CancelTask(StatementImpl cancellee) throws SQLException {
            this.toCancel = cancellee;
            this.origConnProps = new Properties();
            Properties props = StatementImpl.this.connection.getProperties();
            Enumeration<?> keys = props.propertyNames();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement().toString();
                this.origConnProps.setProperty(key, props.getProperty(key));
            }
            this.origConnURL = StatementImpl.this.connection.getURL();
            this.origConnId = StatementImpl.this.connection.getId();
        }

        public void run() {
            new Thread() {
                public void run() {
                    String sQLException;
                    Connection cancelConn = null;
                    Statement cancelStmt = null;
                    try {
                        MySQLConnection physicalConn = StatementImpl.this.physicalConnection.get();
                        if (physicalConn != null) {
                            if (physicalConn.getQueryTimeoutKillsConnection()) {
                                CancelTask.this.toCancel.wasCancelled = true;
                                CancelTask.this.toCancel.wasCancelledByTimeout = true;
                                physicalConn.realClose(false, false, true, new MySQLStatementCancelledException(Messages.getString("Statement.ConnectionKilledDueToTimeout")));
                            } else {
                                synchronized (StatementImpl.this.cancelTimeoutMutex) {
                                    if (CancelTask.this.origConnURL.equals(physicalConn.getURL())) {
                                        cancelConn = physicalConn.duplicate();
                                        cancelStmt = cancelConn.createStatement();
                                        cancelStmt.execute("KILL QUERY " + physicalConn.getId());
                                    } else {
                                        try {
                                            cancelConn = (Connection) DriverManager.getConnection(CancelTask.this.origConnURL, CancelTask.this.origConnProps);
                                            cancelStmt = cancelConn.createStatement();
                                            cancelStmt.execute("KILL QUERY " + CancelTask.this.origConnId);
                                        } catch (NullPointerException e) {
                                        }
                                    }
                                    CancelTask.this.toCancel.wasCancelled = true;
                                    CancelTask.this.toCancel.wasCancelledByTimeout = true;
                                }
                            }
                        }
                        if (cancelStmt != null) {
                            try {
                                cancelStmt.close();
                            } catch (SQLException sqlEx) {
                                throw new RuntimeException(sqlEx.toString());
                            }
                        }
                        if (cancelConn != null) {
                            try {
                                cancelConn.close();
                            } catch (SQLException sqlEx2) {
                                throw new RuntimeException(sqlEx2.toString());
                            }
                        }
                    } catch (SQLException sqlEx3) {
                        try {
                            CancelTask.this.caughtWhileCancelling = sqlEx3;
                            if (cancelStmt != null) {
                                try {
                                } catch (SQLException sqlEx4) {
                                    throw new RuntimeException(sQLException);
                                }
                            }
                            if (cancelConn != null) {
                                try {
                                    cancelConn.close();
                                } catch (SQLException sqlEx5) {
                                    throw new RuntimeException(sqlEx5.toString());
                                }
                            }
                        } finally {
                            if (cancelStmt != null) {
                                try {
                                    cancelStmt.close();
                                } catch (SQLException sqlEx42) {
                                    throw new RuntimeException(sqlEx42.toString());
                                }
                            }
                            if (cancelConn != null) {
                                try {
                                    cancelConn.close();
                                } catch (SQLException sqlEx6) {
                                    throw new RuntimeException(sqlEx6.toString());
                                }
                            }
                            CancelTask.this.toCancel = null;
                            CancelTask.this.origConnProps = null;
                            CancelTask.this.origConnURL = null;
                        }
                    } catch (NullPointerException e2) {
                        if (cancelStmt != null) {
                            try {
                                cancelStmt.close();
                            } catch (SQLException sqlEx7) {
                                throw new RuntimeException(sqlEx7.toString());
                            }
                        }
                        if (cancelConn != null) {
                            try {
                                cancelConn.close();
                            } catch (SQLException sqlEx8) {
                                throw new RuntimeException(sqlEx8.toString());
                            }
                        }
                    }
                    CancelTask.this.toCancel = null;
                    CancelTask.this.origConnProps = null;
                    CancelTask.this.origConnURL = null;
                }
            }.start();
        }
    }

    public StatementImpl(MySQLConnection c, String catalog) throws SQLException {
        boolean profiling = true;
        this.doEscapeProcessing = true;
        this.fetchSize = 0;
        this.isClosed = false;
        this.lastInsertId = -1;
        this.maxFieldSize = MysqlIO.getMaxBuf();
        this.maxRows = -1;
        this.openResults = new HashSet();
        this.pedantic = false;
        this.profileSQL = false;
        this.results = null;
        this.generatedKeysResults = null;
        this.resultSetConcurrency = 0;
        this.resultSetType = 0;
        this.timeoutInMillis = 0;
        this.updateCount = -1;
        this.useUsageAdvisor = false;
        this.warningChain = null;
        this.clearWarningsCalled = false;
        this.holdResultsOpenOverClose = false;
        this.batchedGeneratedKeys = null;
        this.retrieveGeneratedKeys = false;
        this.continueBatchOnError = false;
        this.pingTarget = null;
        this.lastQueryIsOnDupKeyUpdate = false;
        this.statementExecuting = new AtomicBoolean(false);
        this.isImplicitlyClosingResults = false;
        this.originalResultSetType = 0;
        this.originalFetchSize = 0;
        this.isPoolable = true;
        this.closeOnCompletion = false;
        if (c == null || c.isClosed()) {
            throw SQLError.createSQLException(Messages.getString("Statement.0"), SQLError.SQL_STATE_CONNECTION_NOT_OPEN, (ExceptionInterceptor) null);
        }
        this.connection = c;
        this.exceptionInterceptor = this.connection.getExceptionInterceptor();
        this.currentCatalog = catalog;
        this.pedantic = this.connection.getPedantic();
        this.continueBatchOnError = this.connection.getContinueBatchOnError();
        this.useLegacyDatetimeCode = this.connection.getUseLegacyDatetimeCode();
        this.sendFractionalSeconds = this.connection.getSendFractionalSeconds();
        this.doEscapeProcessing = this.connection.getEnableEscapeProcessing();
        if (!this.connection.getDontTrackOpenResources()) {
            this.connection.registerStatement(this);
        }
        this.maxFieldSize = this.connection.getMaxAllowedPacket();
        int defaultFetchSize = this.connection.getDefaultFetchSize();
        if (defaultFetchSize != 0) {
            setFetchSize(defaultFetchSize);
        }
        if (this.connection.getUseUnicode()) {
            this.charEncoding = this.connection.getEncoding();
            this.charConverter = this.connection.getCharsetConverter(this.charEncoding);
        }
        if (!this.connection.getProfileSql() && !this.connection.getUseUsageAdvisor() && !this.connection.getLogSlowQueries()) {
            profiling = false;
        }
        if (this.connection.getAutoGenerateTestcaseScript() || profiling) {
            int i = statementCounter;
            statementCounter = i + 1;
            this.statementId = i;
        }
        if (profiling) {
            this.profileSQL = this.connection.getProfileSql();
            this.useUsageAdvisor = this.connection.getUseUsageAdvisor();
        }
        int maxRowsConn = this.connection.getMaxRows();
        if (maxRowsConn != -1) {
            setMaxRows(maxRowsConn);
        }
        this.holdResultsOpenOverClose = this.connection.getHoldResultsOpenOverStatementClose();
        this.version5013OrNewer = this.connection.versionMeetsMinimum(5, 0, 13);
    }

    public void addBatch(String sql) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.batchedArgs == null) {
                this.batchedArgs = new ArrayList();
            }
            if (sql != null) {
                this.batchedArgs.add(sql);
            }
        }
    }

    public List<Object> getBatchedArgs() {
        List<Object> list = this.batchedArgs;
        if (list == null) {
            return null;
        }
        return Collections.unmodifiableList(list);
    }

    public void cancel() throws SQLException {
        if (this.statementExecuting.get() && !this.isClosed && this.connection != null && this.connection.versionMeetsMinimum(5, 0, 0)) {
            Connection cancelConn = null;
            Statement cancelStmt = null;
            try {
                cancelConn = this.connection.duplicate();
                cancelStmt = cancelConn.createStatement();
                cancelStmt.execute("KILL QUERY " + this.connection.getIO().getThreadId());
                this.wasCancelled = true;
            } finally {
                if (cancelStmt != null) {
                    cancelStmt.close();
                }
                if (cancelConn != null) {
                    cancelConn.close();
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public MySQLConnection checkClosed() throws SQLException {
        MySQLConnection c = this.connection;
        if (c != null) {
            return c;
        }
        throw SQLError.createSQLException(Messages.getString("Statement.49"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
    }

    /* access modifiers changed from: protected */
    public void checkForDml(String sql, char firstStatementChar) throws SQLException {
        if (firstStatementChar == 'I' || firstStatementChar == 'U' || firstStatementChar == 'D' || firstStatementChar == 'A' || firstStatementChar == 'C' || firstStatementChar == 'T' || firstStatementChar == 'R') {
            String noCommentSql = StringUtils.stripComments(sql, "'\"", "'\"", true, false, true, true);
            if (StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "INSERT") || StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "UPDATE") || StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "DELETE") || StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "DROP") || StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "CREATE") || StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "ALTER") || StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "TRUNCATE") || StringUtils.startsWithIgnoreCaseAndWs(noCommentSql, "RENAME")) {
                throw SQLError.createSQLException(Messages.getString("Statement.57"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void checkNullOrEmptyQuery(String sql) throws SQLException {
        if (sql == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.59"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        } else if (sql.length() == 0) {
            throw SQLError.createSQLException(Messages.getString("Statement.61"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
    }

    public void clearBatch() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            List<Object> list = this.batchedArgs;
            if (list != null) {
                list.clear();
            }
        }
    }

    public void clearWarnings() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            this.clearWarningsCalled = true;
            this.warningChain = null;
        }
    }

    public void close() throws SQLException {
        realClose(true, true);
    }

    /* access modifiers changed from: protected */
    public void closeAllOpenResults() throws SQLException {
        MySQLConnection locallyScopedConn = this.connection;
        if (locallyScopedConn != null) {
            synchronized (locallyScopedConn.getConnectionMutex()) {
                Set<ResultSetInternalMethods> set = this.openResults;
                if (set != null) {
                    for (ResultSetInternalMethods element : set) {
                        try {
                            element.realClose(false);
                        } catch (SQLException sqlEx) {
                            AssertionFailedException.shouldNotHappen(sqlEx);
                        }
                    }
                    this.openResults.clear();
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void implicitlyCloseAllOpenResults() throws SQLException {
        this.isImplicitlyClosingResults = true;
        try {
            if (!this.connection.getHoldResultsOpenOverStatementClose() && !this.connection.getDontTrackOpenResources() && !this.holdResultsOpenOverClose) {
                ResultSetInternalMethods resultSetInternalMethods = this.results;
                if (resultSetInternalMethods != null) {
                    resultSetInternalMethods.realClose(false);
                }
                ResultSetInternalMethods resultSetInternalMethods2 = this.generatedKeysResults;
                if (resultSetInternalMethods2 != null) {
                    resultSetInternalMethods2.realClose(false);
                }
                closeAllOpenResults();
            }
        } finally {
            this.isImplicitlyClosingResults = false;
        }
    }

    public void removeOpenResultSet(ResultSetInternalMethods rs) {
        try {
            synchronized (checkClosed().getConnectionMutex()) {
                Set<ResultSetInternalMethods> set = this.openResults;
                if (set != null) {
                    set.remove(rs);
                }
                boolean hasMoreResults = rs.getNextResultSet() != null;
                if (this.results == rs && !hasMoreResults) {
                    this.results = null;
                }
                if (this.generatedKeysResults == rs) {
                    this.generatedKeysResults = null;
                }
                if (!this.isImplicitlyClosingResults && !hasMoreResults) {
                    checkAndPerformCloseOnCompletionAction();
                }
            }
        } catch (SQLException e) {
        }
    }

    public int getOpenResultSetCount() {
        try {
            synchronized (checkClosed().getConnectionMutex()) {
                Set<ResultSetInternalMethods> set = this.openResults;
                if (set == null) {
                    return 0;
                }
                int size = set.size();
                return size;
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    private void checkAndPerformCloseOnCompletionAction() {
        ResultSetInternalMethods resultSetInternalMethods;
        ResultSetInternalMethods resultSetInternalMethods2;
        try {
            synchronized (checkClosed().getConnectionMutex()) {
                if (isCloseOnCompletion() && !this.connection.getDontTrackOpenResources() && getOpenResultSetCount() == 0 && (((resultSetInternalMethods = this.results) == null || !resultSetInternalMethods.reallyResult() || this.results.isClosed()) && ((resultSetInternalMethods2 = this.generatedKeysResults) == null || !resultSetInternalMethods2.reallyResult() || this.generatedKeysResults.isClosed()))) {
                    realClose(false, false);
                }
            }
        } catch (SQLException e) {
        }
    }

    private ResultSetInternalMethods createResultSetUsingServerFetch(String sql) throws SQLException {
        ResultSetInternalMethods rs;
        synchronized (checkClosed().getConnectionMutex()) {
            PreparedStatement pStmt = this.connection.prepareStatement(sql, this.resultSetType, this.resultSetConcurrency);
            pStmt.setFetchSize(this.fetchSize);
            int i = this.maxRows;
            if (i > -1) {
                pStmt.setMaxRows(i);
            }
            statementBegins();
            pStmt.execute();
            rs = ((StatementImpl) pStmt).getResultSetInternal();
            rs.setStatementUsedForFetchingRows((PreparedStatement) pStmt);
            this.results = rs;
        }
        return rs;
    }

    /* access modifiers changed from: protected */
    public boolean createStreamingResultSet() {
        return this.resultSetType == 1003 && this.resultSetConcurrency == 1007 && this.fetchSize == Integer.MIN_VALUE;
    }

    public void enableStreamingResults() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            this.originalResultSetType = this.resultSetType;
            this.originalFetchSize = this.fetchSize;
            setFetchSize(Integer.MIN_VALUE);
            setResultSetType(1003);
        }
    }

    public void disableStreamingResults() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.fetchSize == Integer.MIN_VALUE && this.resultSetType == 1003) {
                setFetchSize(this.originalFetchSize);
                setResultSetType(this.originalResultSetType);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setupStreamingTimeout(MySQLConnection con) throws SQLException {
        if (createStreamingResultSet() && con.getNetTimeoutForStreamingResults() > 0) {
            executeSimpleNonQuery(con, "SET net_write_timeout=" + con.getNetTimeoutForStreamingResults());
        }
    }

    public boolean execute(String sql) throws SQLException {
        return executeInternal(sql, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v41, resolved type: com.mysql.jdbc.exceptions.MySQLStatementCancelledException} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v42, resolved type: com.mysql.jdbc.exceptions.MySQLTimeoutException} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v52, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v28, resolved type: com.mysql.jdbc.EscapeProcessorResult} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v31, resolved type: java.lang.String} */
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
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:598)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x01a0 A[SYNTHETIC, Splitter:B:104:0x01a0] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0116 A[SYNTHETIC, Splitter:B:72:0x0116] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0129  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0131 A[SYNTHETIC, Splitter:B:82:0x0131] */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x014d  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0153 A[SYNTHETIC, Splitter:B:92:0x0153] */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0160  */
    private boolean executeInternal(java.lang.String r30, boolean r31) throws java.sql.SQLException {
        /*
            r29 = this;
            r11 = r29
            r1 = r30
            r12 = r31
            com.mysql.jdbc.MySQLConnection r13 = r29.checkClosed()
            java.lang.Object r14 = r13.getConnectionMutex()
            monitor-enter(r14)
            r29.checkClosed()     // Catch:{ all -> 0x02da }
            r29.checkNullOrEmptyQuery(r30)     // Catch:{ all -> 0x02da }
            r29.resetCancelledState()     // Catch:{ all -> 0x02da }
            r29.implicitlyCloseAllOpenResults()     // Catch:{ all -> 0x02da }
            r15 = 0
            char r0 = r1.charAt(r15)     // Catch:{ all -> 0x02da }
            r2 = 47
            r10 = 1
            if (r0 != r2) goto L_0x0032
            java.lang.String r0 = "/* ping */"
            boolean r0 = r1.startsWith(r0)     // Catch:{ all -> 0x02da }
            if (r0 == 0) goto L_0x0032
            r29.doPingInstead()     // Catch:{ all -> 0x02da }
            monitor-exit(r14)     // Catch:{ all -> 0x02da }
            return r10
        L_0x0032:
            int r0 = findStartOfStatement(r30)     // Catch:{ all -> 0x02da }
            char r0 = com.mysql.jdbc.StringUtils.firstAlphaCharUc(r1, r0)     // Catch:{ all -> 0x02da }
            r9 = r0
            r0 = 83
            if (r9 != r0) goto L_0x0041
            r0 = r10
            goto L_0x0042
        L_0x0041:
            r0 = r15
        L_0x0042:
            r16 = r0
            r11.retrieveGeneratedKeys = r12     // Catch:{ all -> 0x02da }
            if (r12 == 0) goto L_0x0054
            r0 = 73
            if (r9 != r0) goto L_0x0054
            boolean r0 = r29.containsOnDuplicateKeyInString(r30)     // Catch:{ all -> 0x02da }
            if (r0 == 0) goto L_0x0054
            r0 = r10
            goto L_0x0055
        L_0x0054:
            r0 = r15
        L_0x0055:
            r11.lastQueryIsOnDupKeyUpdate = r0     // Catch:{ all -> 0x02da }
            if (r16 != 0) goto L_0x0088
            boolean r0 = r13.isReadOnly()     // Catch:{ all -> 0x02da }
            if (r0 != 0) goto L_0x0060
            goto L_0x0088
        L_0x0060:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x02da }
            r0.<init>()     // Catch:{ all -> 0x02da }
            java.lang.String r2 = "Statement.27"
            java.lang.String r2 = com.mysql.jdbc.Messages.getString(r2)     // Catch:{ all -> 0x02da }
            java.lang.StringBuilder r0 = r0.append(r2)     // Catch:{ all -> 0x02da }
            java.lang.String r2 = "Statement.28"
            java.lang.String r2 = com.mysql.jdbc.Messages.getString(r2)     // Catch:{ all -> 0x02da }
            java.lang.StringBuilder r0 = r0.append(r2)     // Catch:{ all -> 0x02da }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x02da }
            java.lang.String r2 = "S1009"
            com.mysql.jdbc.ExceptionInterceptor r3 = r29.getExceptionInterceptor()     // Catch:{ all -> 0x02da }
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r0, (java.lang.String) r2, (com.mysql.jdbc.ExceptionInterceptor) r3)     // Catch:{ all -> 0x02da }
            throw r0     // Catch:{ all -> 0x02da }
        L_0x0088:
            boolean r0 = r13.isReadInfoMsgEnabled()     // Catch:{ all -> 0x02da }
            r8 = r0
            if (r12 == 0) goto L_0x0096
            r0 = 82
            if (r9 != r0) goto L_0x0096
            r13.setReadInfoMsgEnabled(r10)     // Catch:{ all -> 0x02da }
        L_0x0096:
            r11.setupStreamingTimeout(r13)     // Catch:{ all -> 0x02c8 }
            boolean r0 = r11.doEscapeProcessing     // Catch:{ all -> 0x02c8 }
            if (r0 == 0) goto L_0x00bd
            boolean r0 = r13.serverSupportsConvertFn()     // Catch:{ all -> 0x00b7 }
            java.lang.Object r0 = com.mysql.jdbc.EscapeProcessor.escapeSQL(r1, r0, r13)     // Catch:{ all -> 0x00b7 }
            boolean r2 = r0 instanceof java.lang.String     // Catch:{ all -> 0x00b7 }
            if (r2 == 0) goto L_0x00af
            r2 = r0
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x00b7 }
            r1 = r2
            r7 = r1
            goto L_0x00be
        L_0x00af:
            r2 = r0
            com.mysql.jdbc.EscapeProcessorResult r2 = (com.mysql.jdbc.EscapeProcessorResult) r2     // Catch:{ all -> 0x00b7 }
            java.lang.String r2 = r2.escapedSql     // Catch:{ all -> 0x00b7 }
            r1 = r2
            r7 = r1
            goto L_0x00be
        L_0x00b7:
            r0 = move-exception
            r5 = r1
            r6 = r8
            r4 = r9
            goto L_0x02cc
        L_0x00bd:
            r7 = r1
        L_0x00be:
            r1 = 0
            r17 = 0
            r0 = 0
            r11.batchedGeneratedKeys = r0     // Catch:{ all -> 0x02c3 }
            boolean r2 = r29.useServerFetch()     // Catch:{ all -> 0x02c3 }
            if (r2 == 0) goto L_0x00de
            com.mysql.jdbc.ResultSetInternalMethods r2 = r11.createResultSetUsingServerFetch(r7)     // Catch:{ all -> 0x00d8 }
            r26 = r7
            r27 = r8
            r28 = r9
            r20 = r10
            goto L_0x0202
        L_0x00d8:
            r0 = move-exception
            r5 = r7
            r6 = r8
            r4 = r9
            goto L_0x02cc
        L_0x00de:
            r2 = 0
            r3 = 0
            boolean r4 = r13.getEnableQueryTimeouts()     // Catch:{ all -> 0x02a8 }
            if (r4 == 0) goto L_0x0109
            int r4 = r11.timeoutInMillis     // Catch:{ all -> 0x0103 }
            if (r4 == 0) goto L_0x0109
            r4 = 5
            boolean r4 = r13.versionMeetsMinimum(r4, r15, r15)     // Catch:{ all -> 0x0103 }
            if (r4 == 0) goto L_0x0109
            com.mysql.jdbc.StatementImpl$CancelTask r4 = new com.mysql.jdbc.StatementImpl$CancelTask     // Catch:{ all -> 0x0103 }
            r4.<init>(r11)     // Catch:{ all -> 0x0103 }
            r2 = r4
            java.util.Timer r4 = r13.getCancelTimer()     // Catch:{ all -> 0x0103 }
            int r5 = r11.timeoutInMillis     // Catch:{ all -> 0x0103 }
            long r5 = (long) r5     // Catch:{ all -> 0x0103 }
            r4.schedule(r2, r5)     // Catch:{ all -> 0x0103 }
            r6 = r2
            goto L_0x010a
        L_0x0103:
            r0 = move-exception
            r5 = r7
            r6 = r8
            r4 = r9
            goto L_0x02ac
        L_0x0109:
            r6 = r2
        L_0x010a:
            java.lang.String r2 = r13.getCatalog()     // Catch:{ all -> 0x02a2 }
            java.lang.String r4 = r11.currentCatalog     // Catch:{ all -> 0x02a2 }
            boolean r2 = r2.equals(r4)     // Catch:{ all -> 0x02a2 }
            if (r2 != 0) goto L_0x0129
            java.lang.String r2 = r13.getCatalog()     // Catch:{ all -> 0x0122 }
            r3 = r2
            java.lang.String r2 = r11.currentCatalog     // Catch:{ all -> 0x0122 }
            r13.setCatalog(r2)     // Catch:{ all -> 0x0122 }
            r5 = r3
            goto L_0x012a
        L_0x0122:
            r0 = move-exception
            r2 = r6
            r5 = r7
            r6 = r8
            r4 = r9
            goto L_0x02ac
        L_0x0129:
            r5 = r3
        L_0x012a:
            r2 = 0
            boolean r3 = r13.getCacheResultSetMetadata()     // Catch:{ all -> 0x029a }
            if (r3 == 0) goto L_0x014d
            com.mysql.jdbc.CachedResultSetMetaData r3 = r13.getCachedMetaData(r7)     // Catch:{ all -> 0x0145 }
            r1 = r3
            if (r1 == 0) goto L_0x0140
            com.mysql.jdbc.Field[] r3 = r1.fields     // Catch:{ all -> 0x0145 }
            r2 = r3
            r18 = r1
            r19 = r2
            goto L_0x0151
        L_0x0140:
            r18 = r1
            r19 = r2
            goto L_0x0151
        L_0x0145:
            r0 = move-exception
            r3 = r5
            r2 = r6
            r5 = r7
            r6 = r8
            r4 = r9
            goto L_0x02ac
        L_0x014d:
            r18 = r1
            r19 = r2
        L_0x0151:
            if (r16 == 0) goto L_0x0160
            int r1 = r11.maxRows     // Catch:{ all -> 0x0156 }
            goto L_0x0161
        L_0x0156:
            r0 = move-exception
            r3 = r5
            r2 = r6
            r5 = r7
            r6 = r8
            r4 = r9
            r1 = r18
            goto L_0x02ac
        L_0x0160:
            r1 = -1
        L_0x0161:
            r13.setSessionMaxRows(r1)     // Catch:{ all -> 0x0290 }
            r29.statementBegins()     // Catch:{ all -> 0x0290 }
            int r4 = r11.maxRows     // Catch:{ all -> 0x0290 }
            r20 = 0
            int r3 = r11.resultSetType     // Catch:{ all -> 0x0290 }
            int r2 = r11.resultSetConcurrency     // Catch:{ all -> 0x0290 }
            boolean r21 = r29.createStreamingResultSet()     // Catch:{ all -> 0x0290 }
            java.lang.String r1 = r11.currentCatalog     // Catch:{ all -> 0x0290 }
            r22 = r1
            r1 = r13
            r23 = r2
            r2 = r29
            r24 = r3
            r3 = r7
            r15 = r5
            r5 = r20
            r25 = r6
            r6 = r24
            r26 = r7
            r7 = r23
            r27 = r8
            r8 = r21
            r28 = r9
            r9 = r22
            r20 = r10
            r10 = r19
            com.mysql.jdbc.ResultSetInternalMethods r1 = r1.execSQL(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x0283 }
            r17 = r1
            r2 = r25
            if (r2 == 0) goto L_0x01b9
            java.sql.SQLException r1 = r2.caughtWhileCancelling     // Catch:{ all -> 0x01ad }
            if (r1 != 0) goto L_0x01aa
            r2.cancel()     // Catch:{ all -> 0x01ad }
            r1 = 0
            r2 = r1
            goto L_0x01b9
        L_0x01aa:
            java.sql.SQLException r0 = r2.caughtWhileCancelling     // Catch:{ all -> 0x01ad }
            throw r0     // Catch:{ all -> 0x01ad }
        L_0x01ad:
            r0 = move-exception
            r3 = r15
            r1 = r18
            r5 = r26
            r6 = r27
            r4 = r28
            goto L_0x02ac
        L_0x01b9:
            java.lang.Object r1 = r11.cancelTimeoutMutex     // Catch:{ all -> 0x0278 }
            monitor-enter(r1)     // Catch:{ all -> 0x0278 }
            boolean r3 = r11.wasCancelled     // Catch:{ all -> 0x0268 }
            if (r3 == 0) goto L_0x01e0
            r0 = 0
            boolean r3 = r11.wasCancelledByTimeout     // Catch:{ all -> 0x01d7 }
            if (r3 == 0) goto L_0x01cc
            com.mysql.jdbc.exceptions.MySQLTimeoutException r3 = new com.mysql.jdbc.exceptions.MySQLTimeoutException     // Catch:{ all -> 0x01d7 }
            r3.<init>()     // Catch:{ all -> 0x01d7 }
            r0 = r3
            goto L_0x01d2
        L_0x01cc:
            com.mysql.jdbc.exceptions.MySQLStatementCancelledException r3 = new com.mysql.jdbc.exceptions.MySQLStatementCancelledException     // Catch:{ all -> 0x01d7 }
            r3.<init>()     // Catch:{ all -> 0x01d7 }
            r0 = r3
        L_0x01d2:
            r29.resetCancelledState()     // Catch:{ all -> 0x01d7 }
            throw r0     // Catch:{ all -> 0x01d7 }
        L_0x01d7:
            r0 = move-exception
            r5 = r26
            r6 = r27
            r4 = r28
            goto L_0x026f
        L_0x01e0:
            monitor-exit(r1)     // Catch:{ all -> 0x0268 }
            if (r2 == 0) goto L_0x01f8
            r2.cancel()     // Catch:{ all -> 0x01ef }
            java.util.Timer r1 = r13.getCancelTimer()     // Catch:{ all -> 0x01ef }
            r1.purge()     // Catch:{ all -> 0x01ef }
            goto L_0x01f8
        L_0x01ef:
            r0 = move-exception
            r5 = r26
            r6 = r27
            r4 = r28
            goto L_0x02cc
        L_0x01f8:
            if (r15 == 0) goto L_0x01fd
            r13.setCatalog(r15)     // Catch:{ all -> 0x01ef }
        L_0x01fd:
            r2 = r17
            r1 = r18
        L_0x0202:
            if (r2 == 0) goto L_0x0244
            long r3 = r2.getUpdateID()     // Catch:{ all -> 0x023b }
            r11.lastInsertId = r3     // Catch:{ all -> 0x023b }
            r11.results = r2     // Catch:{ all -> 0x023b }
            r4 = r28
            r2.setFirstCharOfQuery(r4)     // Catch:{ all -> 0x0234 }
            boolean r3 = r2.reallyResult()     // Catch:{ all -> 0x0234 }
            if (r3 == 0) goto L_0x0231
            if (r1 == 0) goto L_0x0221
            com.mysql.jdbc.ResultSetInternalMethods r0 = r11.results     // Catch:{ all -> 0x0234 }
            r5 = r26
            r13.initializeResultsMetadataFromCache(r5, r1, r0)     // Catch:{ all -> 0x0253 }
            goto L_0x0248
        L_0x0221:
            r5 = r26
            com.mysql.jdbc.MySQLConnection r3 = r11.connection     // Catch:{ all -> 0x0253 }
            boolean r3 = r3.getCacheResultSetMetadata()     // Catch:{ all -> 0x0253 }
            if (r3 == 0) goto L_0x0248
            com.mysql.jdbc.ResultSetInternalMethods r3 = r11.results     // Catch:{ all -> 0x0253 }
            r13.initializeResultsMetadataFromCache(r5, r0, r3)     // Catch:{ all -> 0x0253 }
            goto L_0x0248
        L_0x0231:
            r5 = r26
            goto L_0x0248
        L_0x0234:
            r0 = move-exception
            r5 = r26
            r6 = r27
            goto L_0x02cc
        L_0x023b:
            r0 = move-exception
            r5 = r26
            r4 = r28
            r6 = r27
            goto L_0x02cc
        L_0x0244:
            r5 = r26
            r4 = r28
        L_0x0248:
            if (r2 == 0) goto L_0x0258
            boolean r0 = r2.reallyResult()     // Catch:{ all -> 0x0253 }
            if (r0 == 0) goto L_0x0258
            r10 = r20
            goto L_0x0259
        L_0x0253:
            r0 = move-exception
            r6 = r27
            goto L_0x02cc
        L_0x0258:
            r10 = 0
        L_0x0259:
            r6 = r27
            r13.setReadInfoMsgEnabled(r6)     // Catch:{ all -> 0x02d7 }
            java.util.concurrent.atomic.AtomicBoolean r0 = r11.statementExecuting     // Catch:{ all -> 0x02d7 }
            r3 = 0
            r0.set(r3)     // Catch:{ all -> 0x02d7 }
            r0 = r1
            r1 = r2
            monitor-exit(r14)     // Catch:{ all -> 0x02d7 }
            return r10
        L_0x0268:
            r0 = move-exception
            r5 = r26
            r6 = r27
            r4 = r28
        L_0x026f:
            monitor-exit(r1)     // Catch:{ all -> 0x0276 }
            throw r0     // Catch:{ all -> 0x0271 }
        L_0x0271:
            r0 = move-exception
            r3 = r15
            r1 = r18
            goto L_0x02ac
        L_0x0276:
            r0 = move-exception
            goto L_0x026f
        L_0x0278:
            r0 = move-exception
            r5 = r26
            r6 = r27
            r4 = r28
            r3 = r15
            r1 = r18
            goto L_0x02ac
        L_0x0283:
            r0 = move-exception
            r2 = r25
            r5 = r26
            r6 = r27
            r4 = r28
            r3 = r15
            r1 = r18
            goto L_0x02ac
        L_0x0290:
            r0 = move-exception
            r15 = r5
            r2 = r6
            r5 = r7
            r6 = r8
            r4 = r9
            r3 = r15
            r1 = r18
            goto L_0x02ac
        L_0x029a:
            r0 = move-exception
            r15 = r5
            r2 = r6
            r5 = r7
            r6 = r8
            r4 = r9
            r3 = r15
            goto L_0x02ac
        L_0x02a2:
            r0 = move-exception
            r2 = r6
            r5 = r7
            r6 = r8
            r4 = r9
            goto L_0x02ac
        L_0x02a8:
            r0 = move-exception
            r5 = r7
            r6 = r8
            r4 = r9
        L_0x02ac:
            if (r2 == 0) goto L_0x02bb
            r2.cancel()     // Catch:{ all -> 0x02b9 }
            java.util.Timer r7 = r13.getCancelTimer()     // Catch:{ all -> 0x02b9 }
            r7.purge()     // Catch:{ all -> 0x02b9 }
            goto L_0x02bb
        L_0x02b9:
            r0 = move-exception
            goto L_0x02cc
        L_0x02bb:
            if (r3 == 0) goto L_0x02c0
            r13.setCatalog(r3)     // Catch:{ all -> 0x02b9 }
        L_0x02c0:
            throw r0     // Catch:{ all -> 0x02b9 }
        L_0x02c3:
            r0 = move-exception
            r5 = r7
            r6 = r8
            r4 = r9
            goto L_0x02cc
        L_0x02c8:
            r0 = move-exception
            r6 = r8
            r4 = r9
            r5 = r1
        L_0x02cc:
            r13.setReadInfoMsgEnabled(r6)     // Catch:{ all -> 0x02d7 }
            java.util.concurrent.atomic.AtomicBoolean r1 = r11.statementExecuting     // Catch:{ all -> 0x02d7 }
            r2 = 0
            r1.set(r2)     // Catch:{ all -> 0x02d7 }
            throw r0     // Catch:{ all -> 0x02d7 }
        L_0x02d7:
            r0 = move-exception
            r1 = r5
            goto L_0x02db
        L_0x02da:
            r0 = move-exception
        L_0x02db:
            monitor-exit(r14)     // Catch:{ all -> 0x02dd }
            throw r0
        L_0x02dd:
            r0 = move-exception
            goto L_0x02db
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.StatementImpl.executeInternal(java.lang.String, boolean):boolean");
    }

    /* access modifiers changed from: protected */
    public void statementBegins() {
        this.clearWarningsCalled = false;
        this.statementExecuting.set(true);
        MySQLConnection physicalConn = this.connection.getMultiHostSafeProxy().getActiveMySQLConnection();
        while (!(physicalConn instanceof ConnectionImpl)) {
            physicalConn = physicalConn.getMultiHostSafeProxy().getActiveMySQLConnection();
        }
        this.physicalConnection = new WeakReference(physicalConn);
    }

    /* access modifiers changed from: protected */
    public void resetCancelledState() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            Object obj = this.cancelTimeoutMutex;
            if (obj != null) {
                synchronized (obj) {
                    this.wasCancelled = false;
                    this.wasCancelledByTimeout = false;
                }
            }
        }
    }

    public boolean execute(String sql, int returnGeneratedKeys) throws SQLException {
        boolean z = true;
        if (returnGeneratedKeys != 1) {
            z = false;
        }
        return executeInternal(sql, z);
    }

    public boolean execute(String sql, int[] generatedKeyIndices) throws SQLException {
        return executeInternal(sql, generatedKeyIndices != null && generatedKeyIndices.length > 0);
    }

    public boolean execute(String sql, String[] generatedKeyNames) throws SQLException {
        return executeInternal(sql, generatedKeyNames != null && generatedKeyNames.length > 0);
    }

    public int[] executeBatch() throws SQLException {
        return Util.truncateAndConvertToInt(executeBatchInternal());
    }

    /* access modifiers changed from: protected */
    public long[] executeBatchInternal() throws SQLException {
        long[] jArr;
        boolean z;
        long[] updateCounts;
        int commandIndex;
        SQLException sqlEx;
        MySQLConnection locallyScopedConn = checkClosed();
        synchronized (locallyScopedConn.getConnectionMutex()) {
            if (!locallyScopedConn.isReadOnly()) {
                implicitlyCloseAllOpenResults();
                List<Object> list = this.batchedArgs;
                if (list != null) {
                    if (list.size() != 0) {
                        int individualStatementTimeout = this.timeoutInMillis;
                        this.timeoutInMillis = 0;
                        CancelTask timeoutTask = null;
                        try {
                            resetCancelledState();
                            statementBegins();
                            boolean z2 = true;
                            try {
                                this.retrieveGeneratedKeys = true;
                                long[] updateCounts2 = null;
                                List<Object> list2 = this.batchedArgs;
                                if (list2 != null) {
                                    int nbrCommands = list2.size();
                                    this.batchedGeneratedKeys = new ArrayList<>(this.batchedArgs.size());
                                    boolean multiQueriesEnabled = locallyScopedConn.getAllowMultiQueries();
                                    if (!locallyScopedConn.versionMeetsMinimum(4, 1, 1) || (!multiQueriesEnabled && (!locallyScopedConn.getRewriteBatchedStatements() || nbrCommands <= 4))) {
                                        if (locallyScopedConn.getEnableQueryTimeouts() && individualStatementTimeout != 0 && locallyScopedConn.versionMeetsMinimum(5, 0, 0)) {
                                            timeoutTask = new CancelTask(this);
                                            locallyScopedConn.getCancelTimer().schedule(timeoutTask, (long) individualStatementTimeout);
                                        }
                                        updateCounts = new long[nbrCommands];
                                        for (int i = 0; i < nbrCommands; i++) {
                                            updateCounts[i] = -3;
                                        }
                                        commandIndex = 0;
                                        sqlEx = null;
                                        while (commandIndex < nbrCommands) {
                                            String sql = (String) this.batchedArgs.get(commandIndex);
                                            updateCounts[commandIndex] = executeUpdateInternal(sql, z2, z2);
                                            getBatchedGeneratedKeys((this.results.getFirstCharOfQuery() != 'I' || !containsOnDuplicateKeyInString(sql)) ? 0 : 1);
                                            commandIndex++;
                                            z2 = true;
                                        }
                                        if (sqlEx == null) {
                                            updateCounts2 = updateCounts;
                                        } else {
                                            throw SQLError.createBatchUpdateException(sqlEx, updateCounts, getExceptionInterceptor());
                                        }
                                    } else {
                                        long[] executeBatchUsingMultiQueries = executeBatchUsingMultiQueries(multiQueriesEnabled, nbrCommands, individualStatementTimeout);
                                        this.statementExecuting.set(false);
                                        int i2 = nbrCommands;
                                        boolean z3 = multiQueriesEnabled;
                                        return executeBatchUsingMultiQueries;
                                    }
                                }
                                if (timeoutTask != null) {
                                    if (timeoutTask.caughtWhileCancelling == null) {
                                        timeoutTask.cancel();
                                        locallyScopedConn.getCancelTimer().purge();
                                        timeoutTask = null;
                                    } else {
                                        throw timeoutTask.caughtWhileCancelling;
                                    }
                                }
                                if (updateCounts2 != null) {
                                    jArr = updateCounts2;
                                    z = false;
                                } else {
                                    z = false;
                                    jArr = new long[0];
                                }
                                this.statementExecuting.set(z);
                                if (timeoutTask != null) {
                                    timeoutTask.cancel();
                                    locallyScopedConn.getCancelTimer().purge();
                                }
                                resetCancelledState();
                                this.timeoutInMillis = individualStatementTimeout;
                                clearBatch();
                                return jArr;
                            } catch (SQLException ex) {
                                updateCounts[commandIndex] = -3;
                                if (!this.continueBatchOnError || (ex instanceof MySQLTimeoutException) || (ex instanceof MySQLStatementCancelledException) || hasDeadlockOrTimeoutRolledBackTx(ex)) {
                                    long[] newUpdateCounts = new long[commandIndex];
                                    if (hasDeadlockOrTimeoutRolledBackTx(ex)) {
                                        for (int i3 = 0; i3 < newUpdateCounts.length; i3++) {
                                            newUpdateCounts[i3] = -3;
                                        }
                                    } else {
                                        System.arraycopy(updateCounts, 0, newUpdateCounts, 0, commandIndex);
                                    }
                                    throw SQLError.createBatchUpdateException(ex, newUpdateCounts, getExceptionInterceptor());
                                }
                                sqlEx = ex;
                            } catch (Throwable th) {
                                this.statementExecuting.set(false);
                                throw th;
                            }
                        } finally {
                            if (timeoutTask != null) {
                                timeoutTask.cancel();
                                locallyScopedConn.getCancelTimer().purge();
                            }
                            resetCancelledState();
                            this.timeoutInMillis = individualStatementTimeout;
                            clearBatch();
                        }
                    }
                }
                long[] jArr2 = new long[0];
                return jArr2;
            }
            throw SQLError.createSQLException(Messages.getString("Statement.34") + Messages.getString("Statement.35"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
    }

    /* access modifiers changed from: protected */
    public final boolean hasDeadlockOrTimeoutRolledBackTx(SQLException ex) {
        switch (ex.getErrorCode()) {
            case MysqlErrorNumbers.ER_LOCK_WAIT_TIMEOUT:
                return true ^ this.version5013OrNewer;
            case MysqlErrorNumbers.ER_LOCK_TABLE_FULL:
            case MysqlErrorNumbers.ER_LOCK_DEADLOCK:
                return true;
            default:
                return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:65:0x012f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0130, code lost:
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0131, code lost:
        if (r20 == false) goto L_0x0133;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:?, code lost:
        r4.getIO().disableMultiQueries();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x013b, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x016c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x016d, code lost:
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x016e, code lost:
        if (r20 == false) goto L_0x0170;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:?, code lost:
        r4.getIO().disableMultiQueries();
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:63:0x012b, B:85:0x0168] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private long[] executeBatchUsingMultiQueries(boolean r20, int r21, int r22) throws java.sql.SQLException {
        /*
            r19 = this;
            r1 = r19
            r2 = r21
            r3 = r22
            com.mysql.jdbc.MySQLConnection r4 = r19.checkClosed()
            java.lang.Object r5 = r4.getConnectionMutex()
            monitor-enter(r5)
            if (r20 != 0) goto L_0x001c
            com.mysql.jdbc.MysqlIO r0 = r4.getIO()     // Catch:{ all -> 0x0019 }
            r0.enableMultiQueries()     // Catch:{ all -> 0x0019 }
            goto L_0x001c
        L_0x0019:
            r0 = move-exception
            goto L_0x0186
        L_0x001c:
            r6 = 0
            r7 = 0
            long[] r0 = new long[r2]     // Catch:{ all -> 0x0156 }
            r8 = r0
            r0 = 0
        L_0x0022:
            if (r0 >= r2) goto L_0x002b
            r9 = -3
            r8[r0] = r9     // Catch:{ all -> 0x0156 }
            int r0 = r0 + 1
            goto L_0x0022
        L_0x002b:
            r0 = 0
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x0156 }
            r9.<init>()     // Catch:{ all -> 0x0156 }
            java.sql.Statement r10 = r4.createStatement()     // Catch:{ all -> 0x0156 }
            r6 = r10
            boolean r10 = r4.getEnableQueryTimeouts()     // Catch:{ all -> 0x0156 }
            if (r10 == 0) goto L_0x0057
            if (r3 == 0) goto L_0x0057
            r10 = 5
            r11 = 0
            boolean r10 = r4.versionMeetsMinimum(r10, r11, r11)     // Catch:{ all -> 0x0156 }
            if (r10 == 0) goto L_0x0057
            com.mysql.jdbc.StatementImpl$CancelTask r10 = new com.mysql.jdbc.StatementImpl$CancelTask     // Catch:{ all -> 0x0156 }
            r11 = r6
            com.mysql.jdbc.StatementImpl r11 = (com.mysql.jdbc.StatementImpl) r11     // Catch:{ all -> 0x0156 }
            r10.<init>(r11)     // Catch:{ all -> 0x0156 }
            r7 = r10
            java.util.Timer r10 = r4.getCancelTimer()     // Catch:{ all -> 0x0156 }
            long r11 = (long) r3     // Catch:{ all -> 0x0156 }
            r10.schedule(r7, r11)     // Catch:{ all -> 0x0156 }
        L_0x0057:
            r10 = 0
            r11 = 1
            java.lang.String r12 = r4.getEncoding()     // Catch:{ all -> 0x0156 }
            java.lang.String r13 = "utf"
            boolean r13 = com.mysql.jdbc.StringUtils.startsWithIgnoreCase(r12, r13)     // Catch:{ all -> 0x0156 }
            if (r13 == 0) goto L_0x0068
            r11 = 3
            goto L_0x006f
        L_0x0068:
            boolean r13 = com.mysql.jdbc.CharsetMapping.isMultibyteCharset(r12)     // Catch:{ all -> 0x0156 }
            if (r13 == 0) goto L_0x006f
            r11 = 2
        L_0x006f:
            r13 = 1
            boolean r14 = r1.doEscapeProcessing     // Catch:{ all -> 0x0156 }
            r6.setEscapeProcessing(r14)     // Catch:{ all -> 0x0156 }
            boolean r14 = r1.doEscapeProcessing     // Catch:{ all -> 0x0156 }
            if (r14 == 0) goto L_0x007a
            r13 = 2
        L_0x007a:
            r14 = 0
            r15 = 0
            r0 = 0
            r3 = r15
            r15 = r14
            r14 = r10
            r10 = r9
            r9 = r0
        L_0x0082:
            r16 = r15
            r15 = 1
            if (r9 >= r2) goto L_0x00dd
            java.util.List<java.lang.Object> r0 = r1.batchedArgs     // Catch:{ all -> 0x0156 }
            java.lang.Object r0 = r0.get(r9)     // Catch:{ all -> 0x0156 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ all -> 0x0156 }
            r17 = r0
            int r0 = r10.length()     // Catch:{ all -> 0x0156 }
            int r18 = r17.length()     // Catch:{ all -> 0x0156 }
            int r0 = r0 + r18
            int r0 = r0 * r11
            int r0 = r0 + r15
            int r0 = r0 + 4
            int r0 = r0 * r13
            int r0 = r0 + 32
            com.mysql.jdbc.MySQLConnection r15 = r1.connection     // Catch:{ all -> 0x0156 }
            int r15 = r15.getMaxAllowedPacket()     // Catch:{ all -> 0x0156 }
            if (r0 <= r15) goto L_0x00ca
            java.lang.String r0 = r10.toString()     // Catch:{ SQLException -> 0x00b5 }
            r15 = 1
            r6.execute(r0, r15)     // Catch:{ SQLException -> 0x00b5 }
            r15 = r16
            goto L_0x00ba
        L_0x00b5:
            r0 = move-exception
            java.sql.SQLException r15 = r1.handleExceptionForBatch(r9, r3, r8, r0)     // Catch:{ all -> 0x0156 }
        L_0x00ba:
            r0 = r6
            com.mysql.jdbc.StatementImpl r0 = (com.mysql.jdbc.StatementImpl) r0     // Catch:{ all -> 0x0156 }
            int r0 = r1.processMultiCountsAndKeys(r0, r14, r8)     // Catch:{ all -> 0x0156 }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x0156 }
            r14.<init>()     // Catch:{ all -> 0x0156 }
            r10 = r14
            r3 = 0
            r14 = r0
            goto L_0x00cc
        L_0x00ca:
            r15 = r16
        L_0x00cc:
            r2 = r17
            r10.append(r2)     // Catch:{ all -> 0x0156 }
            java.lang.String r0 = ";"
            r10.append(r0)     // Catch:{ all -> 0x0156 }
            r2 = 1
            int r3 = r3 + r2
            int r9 = r9 + 1
            r2 = r21
            goto L_0x0082
        L_0x00dd:
            int r0 = r10.length()     // Catch:{ all -> 0x0156 }
            if (r0 <= 0) goto L_0x00ff
            java.lang.String r0 = r10.toString()     // Catch:{ SQLException -> 0x00ee }
            r2 = 1
            r6.execute(r0, r2)     // Catch:{ SQLException -> 0x00ee }
            r15 = r16
            goto L_0x00f6
        L_0x00ee:
            r0 = move-exception
            int r2 = r9 + -1
            java.sql.SQLException r2 = r1.handleExceptionForBatch(r2, r3, r8, r0)     // Catch:{ all -> 0x0156 }
            r15 = r2
        L_0x00f6:
            r0 = r6
            com.mysql.jdbc.StatementImpl r0 = (com.mysql.jdbc.StatementImpl) r0     // Catch:{ all -> 0x0156 }
            int r0 = r1.processMultiCountsAndKeys(r0, r14, r8)     // Catch:{ all -> 0x0156 }
            r14 = r0
            goto L_0x0101
        L_0x00ff:
            r15 = r16
        L_0x0101:
            if (r7 == 0) goto L_0x0117
            java.sql.SQLException r0 = r7.caughtWhileCancelling     // Catch:{ all -> 0x0156 }
            if (r0 != 0) goto L_0x0114
            r7.cancel()     // Catch:{ all -> 0x0156 }
            java.util.Timer r0 = r4.getCancelTimer()     // Catch:{ all -> 0x0156 }
            r0.purge()     // Catch:{ all -> 0x0156 }
            r0 = 0
            r7 = r0
            goto L_0x0117
        L_0x0114:
            java.sql.SQLException r0 = r7.caughtWhileCancelling     // Catch:{ all -> 0x0156 }
            throw r0     // Catch:{ all -> 0x0156 }
        L_0x0117:
            if (r15 != 0) goto L_0x014d
            if (r7 == 0) goto L_0x0126
            r7.cancel()     // Catch:{ all -> 0x0019 }
            java.util.Timer r0 = r4.getCancelTimer()     // Catch:{ all -> 0x0019 }
            r0.purge()     // Catch:{ all -> 0x0019 }
        L_0x0126:
            r19.resetCancelledState()     // Catch:{ all -> 0x0019 }
            if (r6 == 0) goto L_0x013c
            r6.close()     // Catch:{ all -> 0x012f }
            goto L_0x013c
        L_0x012f:
            r0 = move-exception
            r2 = r0
            if (r20 != 0) goto L_0x013a
            com.mysql.jdbc.MysqlIO r0 = r4.getIO()     // Catch:{ all -> 0x0019 }
            r0.disableMultiQueries()     // Catch:{ all -> 0x0019 }
        L_0x013a:
        L_0x013b:
            throw r2     // Catch:{ all -> 0x0019 }
        L_0x013c:
            if (r20 != 0) goto L_0x0146
            com.mysql.jdbc.MysqlIO r0 = r4.getIO()     // Catch:{ all -> 0x0019 }
            r0.disableMultiQueries()     // Catch:{ all -> 0x0019 }
        L_0x0146:
            r0 = r15
            r2 = r13
            r13 = r8
            monitor-exit(r5)     // Catch:{ all -> 0x0019 }
            return r8
        L_0x014d:
            com.mysql.jdbc.ExceptionInterceptor r0 = r19.getExceptionInterceptor()     // Catch:{ all -> 0x0156 }
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createBatchUpdateException(r15, r8, r0)     // Catch:{ all -> 0x0156 }
            throw r0     // Catch:{ all -> 0x0156 }
        L_0x0156:
            r0 = move-exception
            if (r7 == 0) goto L_0x0163
            r7.cancel()     // Catch:{ all -> 0x0019 }
            java.util.Timer r2 = r4.getCancelTimer()     // Catch:{ all -> 0x0019 }
            r2.purge()     // Catch:{ all -> 0x0019 }
        L_0x0163:
            r19.resetCancelledState()     // Catch:{ all -> 0x0019 }
            if (r6 == 0) goto L_0x0178
            r6.close()     // Catch:{ all -> 0x016c }
            goto L_0x0178
        L_0x016c:
            r0 = move-exception
            r2 = r0
            if (r20 != 0) goto L_0x0177
            com.mysql.jdbc.MysqlIO r0 = r4.getIO()     // Catch:{ all -> 0x0019 }
            r0.disableMultiQueries()     // Catch:{ all -> 0x0019 }
        L_0x0177:
            goto L_0x013b
        L_0x0178:
            if (r20 != 0) goto L_0x0182
            com.mysql.jdbc.MysqlIO r2 = r4.getIO()     // Catch:{ all -> 0x0019 }
            r2.disableMultiQueries()     // Catch:{ all -> 0x0019 }
        L_0x0182:
            throw r0     // Catch:{ all -> 0x0019 }
        L_0x0186:
            monitor-exit(r5)     // Catch:{ all -> 0x0019 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.StatementImpl.executeBatchUsingMultiQueries(boolean, int, int):long[]");
    }

    /* access modifiers changed from: protected */
    public int processMultiCountsAndKeys(StatementImpl batchedStatement, int updateCountCounter, long[] updateCounts) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            int updateCountCounter2 = updateCountCounter + 1;
            try {
                updateCounts[updateCountCounter] = batchedStatement.getLargeUpdateCount();
                boolean doGenKeys = this.batchedGeneratedKeys != null;
                byte[][] bArr = null;
                if (doGenKeys) {
                    this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][]{StringUtils.getBytes(Long.toString(batchedStatement.getLastInsertID()))}, getExceptionInterceptor()));
                }
                while (true) {
                    if (!batchedStatement.getMoreResults()) {
                        if (batchedStatement.getLargeUpdateCount() == -1) {
                            return updateCountCounter2;
                        }
                    }
                    int updateCountCounter3 = updateCountCounter2 + 1;
                    try {
                        updateCounts[updateCountCounter2] = batchedStatement.getLargeUpdateCount();
                        if (doGenKeys) {
                            this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][]{StringUtils.getBytes(Long.toString(batchedStatement.getLastInsertID()))}, getExceptionInterceptor()));
                            updateCountCounter2 = updateCountCounter3;
                        } else {
                            updateCountCounter2 = updateCountCounter3;
                        }
                    } catch (Throwable th) {
                        th = th;
                        int i = updateCountCounter3;
                        throw th;
                    }
                }
            } catch (Throwable th2) {
                th = th2;
                throw th;
            }
        }
    }

    /* access modifiers changed from: protected */
    public SQLException handleExceptionForBatch(int endOfBatchIndex, int numValuesPerBatch, long[] updateCounts, SQLException ex) throws BatchUpdateException, SQLException {
        for (int j = endOfBatchIndex; j > endOfBatchIndex - numValuesPerBatch; j--) {
            updateCounts[j] = -3;
        }
        if (this.continueBatchOnError != 0 && !(ex instanceof MySQLTimeoutException) && !(ex instanceof MySQLStatementCancelledException) && !hasDeadlockOrTimeoutRolledBackTx(ex)) {
            return ex;
        }
        long[] newUpdateCounts = new long[endOfBatchIndex];
        System.arraycopy(updateCounts, 0, newUpdateCounts, 0, endOfBatchIndex);
        throw SQLError.createBatchUpdateException(ex, newUpdateCounts, getExceptionInterceptor());
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v42, resolved type: com.mysql.jdbc.exceptions.MySQLStatementCancelledException} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v43, resolved type: com.mysql.jdbc.exceptions.MySQLTimeoutException} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v62, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v21, resolved type: com.mysql.jdbc.EscapeProcessorResult} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v24, resolved type: java.lang.String} */
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
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:598)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00a9 A[SYNTHETIC, Splitter:B:41:0x00a9] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00bb  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00c3 A[SYNTHETIC, Splitter:B:51:0x00c3] */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00dc  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0110 A[SYNTHETIC, Splitter:B:66:0x0110] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x012c  */
    public java.sql.ResultSet executeQuery(java.lang.String r22) throws java.sql.SQLException {
        /*
            r21 = this;
            r11 = r21
            r1 = r22
            com.mysql.jdbc.MySQLConnection r0 = r21.checkClosed()
            java.lang.Object r12 = r0.getConnectionMutex()
            monitor-enter(r12)
            com.mysql.jdbc.MySQLConnection r0 = r11.connection     // Catch:{ all -> 0x01e0 }
            r13 = r0
            r14 = 0
            r11.retrieveGeneratedKeys = r14     // Catch:{ all -> 0x01e0 }
            r21.checkNullOrEmptyQuery(r22)     // Catch:{ all -> 0x01e0 }
            r21.resetCancelledState()     // Catch:{ all -> 0x01e0 }
            r21.implicitlyCloseAllOpenResults()     // Catch:{ all -> 0x01e0 }
            char r0 = r1.charAt(r14)     // Catch:{ all -> 0x01e0 }
            r2 = 47
            if (r0 != r2) goto L_0x0033
            java.lang.String r0 = "/* ping */"
            boolean r0 = r1.startsWith(r0)     // Catch:{ all -> 0x01e0 }
            if (r0 == 0) goto L_0x0033
            r21.doPingInstead()     // Catch:{ all -> 0x01e0 }
            com.mysql.jdbc.ResultSetInternalMethods r0 = r11.results     // Catch:{ all -> 0x01e0 }
            monitor-exit(r12)     // Catch:{ all -> 0x01e0 }
            return r0
        L_0x0033:
            r11.setupStreamingTimeout(r13)     // Catch:{ all -> 0x01e0 }
            boolean r0 = r11.doEscapeProcessing     // Catch:{ all -> 0x01e0 }
            if (r0 == 0) goto L_0x0056
            boolean r0 = r13.serverSupportsConvertFn()     // Catch:{ all -> 0x01e0 }
            com.mysql.jdbc.MySQLConnection r2 = r11.connection     // Catch:{ all -> 0x01e0 }
            java.lang.Object r0 = com.mysql.jdbc.EscapeProcessor.escapeSQL(r1, r0, r2)     // Catch:{ all -> 0x01e0 }
            boolean r2 = r0 instanceof java.lang.String     // Catch:{ all -> 0x01e0 }
            if (r2 == 0) goto L_0x004e
            r2 = r0
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x01e0 }
            r1 = r2
            r15 = r1
            goto L_0x0057
        L_0x004e:
            r2 = r0
            com.mysql.jdbc.EscapeProcessorResult r2 = (com.mysql.jdbc.EscapeProcessorResult) r2     // Catch:{ all -> 0x01e0 }
            java.lang.String r2 = r2.escapedSql     // Catch:{ all -> 0x01e0 }
            r1 = r2
            r15 = r1
            goto L_0x0057
        L_0x0056:
            r15 = r1
        L_0x0057:
            int r0 = findStartOfStatement(r15)     // Catch:{ all -> 0x01e4 }
            char r0 = com.mysql.jdbc.StringUtils.firstAlphaCharUc(r15, r0)     // Catch:{ all -> 0x01e4 }
            r10 = r0
            r11.checkForDml(r15, r10)     // Catch:{ all -> 0x01e4 }
            r1 = 0
            boolean r0 = r21.useServerFetch()     // Catch:{ all -> 0x01e4 }
            if (r0 == 0) goto L_0x0072
            com.mysql.jdbc.ResultSetInternalMethods r0 = r11.createResultSetUsingServerFetch(r15)     // Catch:{ all -> 0x01e4 }
            r11.results = r0     // Catch:{ all -> 0x01e4 }
            monitor-exit(r12)     // Catch:{ all -> 0x01e4 }
            return r0
        L_0x0072:
            r2 = 0
            r3 = 0
            boolean r0 = r13.getEnableQueryTimeouts()     // Catch:{ all -> 0x01c3 }
            if (r0 == 0) goto L_0x009c
            int r0 = r11.timeoutInMillis     // Catch:{ all -> 0x0097 }
            if (r0 == 0) goto L_0x009c
            r0 = 5
            boolean r0 = r13.versionMeetsMinimum(r0, r14, r14)     // Catch:{ all -> 0x0097 }
            if (r0 == 0) goto L_0x009c
            com.mysql.jdbc.StatementImpl$CancelTask r0 = new com.mysql.jdbc.StatementImpl$CancelTask     // Catch:{ all -> 0x0097 }
            r0.<init>(r11)     // Catch:{ all -> 0x0097 }
            r2 = r0
            java.util.Timer r0 = r13.getCancelTimer()     // Catch:{ all -> 0x0097 }
            int r4 = r11.timeoutInMillis     // Catch:{ all -> 0x0097 }
            long r4 = (long) r4     // Catch:{ all -> 0x0097 }
            r0.schedule(r2, r4)     // Catch:{ all -> 0x0097 }
            r9 = r2
            goto L_0x009d
        L_0x0097:
            r0 = move-exception
            r18 = r10
            goto L_0x01c6
        L_0x009c:
            r9 = r2
        L_0x009d:
            java.lang.String r0 = r13.getCatalog()     // Catch:{ all -> 0x01bd }
            java.lang.String r2 = r11.currentCatalog     // Catch:{ all -> 0x01bd }
            boolean r0 = r0.equals(r2)     // Catch:{ all -> 0x01bd }
            if (r0 != 0) goto L_0x00bb
            java.lang.String r0 = r13.getCatalog()     // Catch:{ all -> 0x00b5 }
            r3 = r0
            java.lang.String r0 = r11.currentCatalog     // Catch:{ all -> 0x00b5 }
            r13.setCatalog(r0)     // Catch:{ all -> 0x00b5 }
            r8 = r3
            goto L_0x00bc
        L_0x00b5:
            r0 = move-exception
            r2 = r9
            r18 = r10
            goto L_0x01c6
        L_0x00bb:
            r8 = r3
        L_0x00bc:
            r0 = 0
            boolean r2 = r13.getCacheResultSetMetadata()     // Catch:{ all -> 0x01b6 }
            if (r2 == 0) goto L_0x00dc
            com.mysql.jdbc.CachedResultSetMetaData r2 = r13.getCachedMetaData(r15)     // Catch:{ all -> 0x00d5 }
            r1 = r2
            if (r1 == 0) goto L_0x00d1
            com.mysql.jdbc.Field[] r2 = r1.fields     // Catch:{ all -> 0x00d5 }
            r0 = r2
            r16 = r0
            r7 = r1
            goto L_0x00df
        L_0x00d1:
            r16 = r0
            r7 = r1
            goto L_0x00df
        L_0x00d5:
            r0 = move-exception
            r3 = r8
            r2 = r9
            r18 = r10
            goto L_0x01c6
        L_0x00dc:
            r16 = r0
            r7 = r1
        L_0x00df:
            int r0 = r11.maxRows     // Catch:{ all -> 0x01ad }
            r13.setSessionMaxRows(r0)     // Catch:{ all -> 0x01ad }
            r21.statementBegins()     // Catch:{ all -> 0x01ad }
            int r4 = r11.maxRows     // Catch:{ all -> 0x01ad }
            r5 = 0
            int r6 = r11.resultSetType     // Catch:{ all -> 0x01ad }
            int r0 = r11.resultSetConcurrency     // Catch:{ all -> 0x01ad }
            boolean r17 = r21.createStreamingResultSet()     // Catch:{ all -> 0x01ad }
            java.lang.String r3 = r11.currentCatalog     // Catch:{ all -> 0x01ad }
            r1 = r13
            r2 = r21
            r18 = r3
            r3 = r15
            r19 = r7
            r7 = r0
            r20 = r8
            r8 = r17
            r14 = r9
            r9 = r18
            r18 = r10
            r10 = r16
            com.mysql.jdbc.ResultSetInternalMethods r0 = r1.execSQL(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x01a5 }
            r11.results = r0     // Catch:{ all -> 0x01a5 }
            if (r14 == 0) goto L_0x012c
            java.sql.SQLException r0 = r14.caughtWhileCancelling     // Catch:{ all -> 0x0124 }
            if (r0 != 0) goto L_0x0121
            r14.cancel()     // Catch:{ all -> 0x0124 }
            java.util.Timer r0 = r13.getCancelTimer()     // Catch:{ all -> 0x0124 }
            r0.purge()     // Catch:{ all -> 0x0124 }
            r0 = 0
            r2 = r0
            goto L_0x012d
        L_0x0121:
            java.sql.SQLException r0 = r14.caughtWhileCancelling     // Catch:{ all -> 0x0124 }
            throw r0     // Catch:{ all -> 0x0124 }
        L_0x0124:
            r0 = move-exception
            r2 = r14
            r1 = r19
            r3 = r20
            goto L_0x01c6
        L_0x012c:
            r2 = r14
        L_0x012d:
            java.lang.Object r1 = r11.cancelTimeoutMutex     // Catch:{ all -> 0x019e }
            monitor-enter(r1)     // Catch:{ all -> 0x019e }
            boolean r0 = r11.wasCancelled     // Catch:{ all -> 0x0192 }
            if (r0 == 0) goto L_0x0151
            r0 = 0
            boolean r3 = r11.wasCancelledByTimeout     // Catch:{ all -> 0x014b }
            if (r3 == 0) goto L_0x0140
            com.mysql.jdbc.exceptions.MySQLTimeoutException r3 = new com.mysql.jdbc.exceptions.MySQLTimeoutException     // Catch:{ all -> 0x014b }
            r3.<init>()     // Catch:{ all -> 0x014b }
            r0 = r3
            goto L_0x0146
        L_0x0140:
            com.mysql.jdbc.exceptions.MySQLStatementCancelledException r3 = new com.mysql.jdbc.exceptions.MySQLStatementCancelledException     // Catch:{ all -> 0x014b }
            r3.<init>()     // Catch:{ all -> 0x014b }
            r0 = r3
        L_0x0146:
            r21.resetCancelledState()     // Catch:{ all -> 0x014b }
            throw r0     // Catch:{ all -> 0x014b }
        L_0x014b:
            r0 = move-exception
            r4 = r19
            r3 = r20
            goto L_0x0197
        L_0x0151:
            monitor-exit(r1)     // Catch:{ all -> 0x0192 }
            java.util.concurrent.atomic.AtomicBoolean r0 = r11.statementExecuting     // Catch:{ all -> 0x01e4 }
            r1 = 0
            r0.set(r1)     // Catch:{ all -> 0x01e4 }
            if (r2 == 0) goto L_0x0165
            r2.cancel()     // Catch:{ all -> 0x01e4 }
            java.util.Timer r0 = r13.getCancelTimer()     // Catch:{ all -> 0x01e4 }
            r0.purge()     // Catch:{ all -> 0x01e4 }
        L_0x0165:
            r3 = r20
            if (r3 == 0) goto L_0x016c
            r13.setCatalog(r3)     // Catch:{ all -> 0x01e4 }
        L_0x016c:
            com.mysql.jdbc.ResultSetInternalMethods r0 = r11.results     // Catch:{ all -> 0x01e4 }
            long r0 = r0.getUpdateID()     // Catch:{ all -> 0x01e4 }
            r11.lastInsertId = r0     // Catch:{ all -> 0x01e4 }
            r4 = r19
            if (r4 == 0) goto L_0x0180
            com.mysql.jdbc.ResultSetInternalMethods r0 = r11.results     // Catch:{ all -> 0x01e4 }
            r13.initializeResultsMetadataFromCache(r15, r4, r0)     // Catch:{ all -> 0x01e4 }
            goto L_0x018e
        L_0x0180:
            com.mysql.jdbc.MySQLConnection r0 = r11.connection     // Catch:{ all -> 0x01e4 }
            boolean r0 = r0.getCacheResultSetMetadata()     // Catch:{ all -> 0x01e4 }
            if (r0 == 0) goto L_0x018e
            r0 = 0
            com.mysql.jdbc.ResultSetInternalMethods r1 = r11.results     // Catch:{ all -> 0x01e4 }
            r13.initializeResultsMetadataFromCache(r15, r0, r1)     // Catch:{ all -> 0x01e4 }
        L_0x018e:
            com.mysql.jdbc.ResultSetInternalMethods r0 = r11.results     // Catch:{ all -> 0x01e4 }
            monitor-exit(r12)     // Catch:{ all -> 0x01e4 }
            return r0
        L_0x0192:
            r0 = move-exception
            r4 = r19
            r3 = r20
        L_0x0197:
            monitor-exit(r1)     // Catch:{ all -> 0x019c }
            throw r0     // Catch:{ all -> 0x0199 }
        L_0x0199:
            r0 = move-exception
            r1 = r4
            goto L_0x01c6
        L_0x019c:
            r0 = move-exception
            goto L_0x0197
        L_0x019e:
            r0 = move-exception
            r4 = r19
            r3 = r20
            r1 = r4
            goto L_0x01c6
        L_0x01a5:
            r0 = move-exception
            r4 = r19
            r3 = r20
            r1 = r4
            r2 = r14
            goto L_0x01c6
        L_0x01ad:
            r0 = move-exception
            r4 = r7
            r3 = r8
            r14 = r9
            r18 = r10
            r1 = r4
            r2 = r14
            goto L_0x01c6
        L_0x01b6:
            r0 = move-exception
            r3 = r8
            r14 = r9
            r18 = r10
            r2 = r14
            goto L_0x01c6
        L_0x01bd:
            r0 = move-exception
            r14 = r9
            r18 = r10
            r2 = r14
            goto L_0x01c6
        L_0x01c3:
            r0 = move-exception
            r18 = r10
        L_0x01c6:
            java.util.concurrent.atomic.AtomicBoolean r4 = r11.statementExecuting     // Catch:{ all -> 0x01e4 }
            r5 = 0
            r4.set(r5)     // Catch:{ all -> 0x01e4 }
            if (r2 == 0) goto L_0x01d8
            r2.cancel()     // Catch:{ all -> 0x01e4 }
            java.util.Timer r4 = r13.getCancelTimer()     // Catch:{ all -> 0x01e4 }
            r4.purge()     // Catch:{ all -> 0x01e4 }
        L_0x01d8:
            if (r3 == 0) goto L_0x01dd
            r13.setCatalog(r3)     // Catch:{ all -> 0x01e4 }
        L_0x01dd:
            throw r0     // Catch:{ all -> 0x01e4 }
        L_0x01e0:
            r0 = move-exception
            r15 = r1
        L_0x01e2:
            monitor-exit(r12)     // Catch:{ all -> 0x01e4 }
            throw r0
        L_0x01e4:
            r0 = move-exception
            goto L_0x01e2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.StatementImpl.executeQuery(java.lang.String):java.sql.ResultSet");
    }

    /* access modifiers changed from: protected */
    public void doPingInstead() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            PingTarget pingTarget2 = this.pingTarget;
            if (pingTarget2 != null) {
                pingTarget2.doPing();
            } else {
                this.connection.ping();
            }
            this.results = generatePingResultSet();
        }
    }

    /* access modifiers changed from: protected */
    public ResultSetInternalMethods generatePingResultSet() throws SQLException {
        ResultSetInternalMethods resultSetInternalMethods;
        synchronized (checkClosed().getConnectionMutex()) {
            Field[] fields = {new Field((String) null, "1", -5, 1)};
            ArrayList<ResultSetRow> rows = new ArrayList<>();
            rows.add(new ByteArrayRow(new byte[][]{new byte[]{49}}, getExceptionInterceptor()));
            resultSetInternalMethods = (ResultSetInternalMethods) DatabaseMetaData.buildResultSet(fields, rows, this.connection);
        }
        return resultSetInternalMethods;
    }

    /* access modifiers changed from: protected */
    public void executeSimpleNonQuery(MySQLConnection c, String nonQuery) throws SQLException {
        c.execSQL(this, nonQuery, -1, (Buffer) null, 1003, 1007, false, this.currentCatalog, (Field[]) null, false).close();
    }

    public int executeUpdate(String sql) throws SQLException {
        return Util.truncateAndConvertToInt(executeLargeUpdate(sql));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v40, resolved type: com.mysql.jdbc.exceptions.MySQLStatementCancelledException} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v41, resolved type: com.mysql.jdbc.exceptions.MySQLTimeoutException} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v17, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v16, resolved type: com.mysql.jdbc.EscapeProcessorResult} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v19, resolved type: java.lang.String} */
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
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00ba A[SYNTHETIC, Splitter:B:49:0x00ba] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00cf  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0106 A[SYNTHETIC, Splitter:B:63:0x0106] */
    protected long executeUpdateInternal(java.lang.String r25, boolean r26, boolean r27) throws java.sql.SQLException {
        /*
            r24 = this;
            r12 = r24
            r1 = r25
            r13 = r27
            com.mysql.jdbc.MySQLConnection r0 = r24.checkClosed()
            java.lang.Object r14 = r0.getConnectionMutex()
            monitor-enter(r14)
            com.mysql.jdbc.MySQLConnection r0 = r12.connection     // Catch:{ all -> 0x0224 }
            r15 = r0
            r24.checkNullOrEmptyQuery(r25)     // Catch:{ all -> 0x0224 }
            r24.resetCancelledState()     // Catch:{ all -> 0x0224 }
            int r0 = findStartOfStatement(r25)     // Catch:{ all -> 0x0224 }
            char r0 = com.mysql.jdbc.StringUtils.firstAlphaCharUc(r1, r0)     // Catch:{ all -> 0x0224 }
            r11 = r0
            r12.retrieveGeneratedKeys = r13     // Catch:{ all -> 0x0224 }
            r0 = 1
            r10 = 0
            if (r13 == 0) goto L_0x0033
            r2 = 73
            if (r11 != r2) goto L_0x0033
            boolean r2 = r24.containsOnDuplicateKeyInString(r25)     // Catch:{ all -> 0x0224 }
            if (r2 == 0) goto L_0x0033
            r2 = r0
            goto L_0x0034
        L_0x0033:
            r2 = r10
        L_0x0034:
            r12.lastQueryIsOnDupKeyUpdate = r2     // Catch:{ all -> 0x0224 }
            r16 = 0
            boolean r2 = r12.doEscapeProcessing     // Catch:{ all -> 0x0224 }
            if (r2 == 0) goto L_0x005a
            com.mysql.jdbc.MySQLConnection r2 = r12.connection     // Catch:{ all -> 0x0224 }
            boolean r2 = r2.serverSupportsConvertFn()     // Catch:{ all -> 0x0224 }
            com.mysql.jdbc.MySQLConnection r3 = r12.connection     // Catch:{ all -> 0x0224 }
            java.lang.Object r2 = com.mysql.jdbc.EscapeProcessor.escapeSQL(r1, r2, r3)     // Catch:{ all -> 0x0224 }
            boolean r3 = r2 instanceof java.lang.String     // Catch:{ all -> 0x0224 }
            if (r3 == 0) goto L_0x0052
            r3 = r2
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x0224 }
            r1 = r3
            r9 = r1
            goto L_0x005b
        L_0x0052:
            r3 = r2
            com.mysql.jdbc.EscapeProcessorResult r3 = (com.mysql.jdbc.EscapeProcessorResult) r3     // Catch:{ all -> 0x0224 }
            java.lang.String r3 = r3.escapedSql     // Catch:{ all -> 0x0224 }
            r1 = r3
            r9 = r1
            goto L_0x005b
        L_0x005a:
            r9 = r1
        L_0x005b:
            boolean r1 = r15.isReadOnly(r10)     // Catch:{ all -> 0x0220 }
            if (r1 != 0) goto L_0x01f5
            java.lang.String r1 = "select"
            boolean r1 = com.mysql.jdbc.StringUtils.startsWithIgnoreCaseAndWs((java.lang.String) r9, (java.lang.String) r1)     // Catch:{ all -> 0x0220 }
            if (r1 != 0) goto L_0x01e1
            r24.implicitlyCloseAllOpenResults()     // Catch:{ all -> 0x0220 }
            r1 = 0
            r2 = 0
            boolean r3 = r15.isReadInfoMsgEnabled()     // Catch:{ all -> 0x0220 }
            r8 = r3
            if (r13 == 0) goto L_0x0082
            r3 = 82
            if (r11 != r3) goto L_0x0082
            r15.setReadInfoMsgEnabled(r0)     // Catch:{ all -> 0x007d }
            goto L_0x0082
        L_0x007d:
            r0 = move-exception
            r17 = r9
            goto L_0x0227
        L_0x0082:
            boolean r0 = r15.getEnableQueryTimeouts()     // Catch:{ all -> 0x01bd }
            if (r0 == 0) goto L_0x00ad
            int r0 = r12.timeoutInMillis     // Catch:{ all -> 0x00a5 }
            if (r0 == 0) goto L_0x00ad
            r0 = 5
            boolean r0 = r15.versionMeetsMinimum(r0, r10, r10)     // Catch:{ all -> 0x00a5 }
            if (r0 == 0) goto L_0x00ad
            com.mysql.jdbc.StatementImpl$CancelTask r0 = new com.mysql.jdbc.StatementImpl$CancelTask     // Catch:{ all -> 0x00a5 }
            r0.<init>(r12)     // Catch:{ all -> 0x00a5 }
            r1 = r0
            java.util.Timer r0 = r15.getCancelTimer()     // Catch:{ all -> 0x00a5 }
            int r3 = r12.timeoutInMillis     // Catch:{ all -> 0x00a5 }
            long r3 = (long) r3     // Catch:{ all -> 0x00a5 }
            r0.schedule(r1, r3)     // Catch:{ all -> 0x00a5 }
            r7 = r1
            goto L_0x00ae
        L_0x00a5:
            r0 = move-exception
            r4 = r8
            r17 = r9
            r6 = r10
            r13 = r11
            goto L_0x01c3
        L_0x00ad:
            r7 = r1
        L_0x00ae:
            java.lang.String r0 = r15.getCatalog()     // Catch:{ all -> 0x01b4 }
            java.lang.String r1 = r12.currentCatalog     // Catch:{ all -> 0x01b4 }
            boolean r0 = r0.equals(r1)     // Catch:{ all -> 0x01b4 }
            if (r0 != 0) goto L_0x00cf
            java.lang.String r0 = r15.getCatalog()     // Catch:{ all -> 0x00c6 }
            r2 = r0
            java.lang.String r0 = r12.currentCatalog     // Catch:{ all -> 0x00c6 }
            r15.setCatalog(r0)     // Catch:{ all -> 0x00c6 }
            r6 = r2
            goto L_0x00d0
        L_0x00c6:
            r0 = move-exception
            r1 = r7
            r4 = r8
            r17 = r9
            r6 = r10
            r13 = r11
            goto L_0x01c3
        L_0x00cf:
            r6 = r2
        L_0x00d0:
            r0 = -1
            r15.setSessionMaxRows(r0)     // Catch:{ all -> 0x01a9 }
            r24.statementBegins()     // Catch:{ all -> 0x01a9 }
            r4 = -1
            r5 = 0
            r0 = 1003(0x3eb, float:1.406E-42)
            r17 = 1007(0x3ef, float:1.411E-42)
            r18 = 0
            java.lang.String r3 = r12.currentCatalog     // Catch:{ all -> 0x01a9 }
            r19 = 0
            r1 = r15
            r2 = r24
            r20 = r3
            r3 = r9
            r21 = r6
            r6 = r0
            r22 = r7
            r7 = r17
            r23 = r8
            r8 = r18
            r17 = r9
            r9 = r20
            r10 = r19
            r13 = r11
            r11 = r26
            com.mysql.jdbc.ResultSetInternalMethods r0 = r1.execSQL(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ all -> 0x019e }
            r1 = r0
            r3 = r22
            if (r3 == 0) goto L_0x0125
            java.sql.SQLException r0 = r3.caughtWhileCancelling     // Catch:{ all -> 0x011a }
            if (r0 != 0) goto L_0x0117
            r3.cancel()     // Catch:{ all -> 0x011a }
            java.util.Timer r0 = r15.getCancelTimer()     // Catch:{ all -> 0x011a }
            r0.purge()     // Catch:{ all -> 0x011a }
            r0 = 0
            r3 = r0
            goto L_0x0125
        L_0x0117:
            java.sql.SQLException r0 = r3.caughtWhileCancelling     // Catch:{ all -> 0x011a }
            throw r0     // Catch:{ all -> 0x011a }
        L_0x011a:
            r0 = move-exception
            r16 = r1
            r1 = r3
            r2 = r21
            r4 = r23
            r6 = 0
            goto L_0x01c3
        L_0x0125:
            java.lang.Object r2 = r12.cancelTimeoutMutex     // Catch:{ all -> 0x0193 }
            monitor-enter(r2)     // Catch:{ all -> 0x0193 }
            boolean r0 = r12.wasCancelled     // Catch:{ all -> 0x0183 }
            if (r0 == 0) goto L_0x014a
            r0 = 0
            boolean r4 = r12.wasCancelledByTimeout     // Catch:{ all -> 0x0143 }
            if (r4 == 0) goto L_0x0138
            com.mysql.jdbc.exceptions.MySQLTimeoutException r4 = new com.mysql.jdbc.exceptions.MySQLTimeoutException     // Catch:{ all -> 0x0143 }
            r4.<init>()     // Catch:{ all -> 0x0143 }
            r0 = r4
            goto L_0x013e
        L_0x0138:
            com.mysql.jdbc.exceptions.MySQLStatementCancelledException r4 = new com.mysql.jdbc.exceptions.MySQLStatementCancelledException     // Catch:{ all -> 0x0143 }
            r4.<init>()     // Catch:{ all -> 0x0143 }
            r0 = r4
        L_0x013e:
            r24.resetCancelledState()     // Catch:{ all -> 0x0143 }
            throw r0     // Catch:{ all -> 0x0143 }
        L_0x0143:
            r0 = move-exception
            r5 = r21
            r4 = r23
            r6 = 0
            goto L_0x0189
        L_0x014a:
            monitor-exit(r2)     // Catch:{ all -> 0x0183 }
            r4 = r23
            r15.setReadInfoMsgEnabled(r4)     // Catch:{ all -> 0x0229 }
            if (r3 == 0) goto L_0x015d
            r3.cancel()     // Catch:{ all -> 0x0229 }
            java.util.Timer r0 = r15.getCancelTimer()     // Catch:{ all -> 0x0229 }
            r0.purge()     // Catch:{ all -> 0x0229 }
        L_0x015d:
            r5 = r21
            if (r5 == 0) goto L_0x0164
            r15.setCatalog(r5)     // Catch:{ all -> 0x0229 }
        L_0x0164:
            if (r26 != 0) goto L_0x016c
            java.util.concurrent.atomic.AtomicBoolean r0 = r12.statementExecuting     // Catch:{ all -> 0x0229 }
            r6 = 0
            r0.set(r6)     // Catch:{ all -> 0x0229 }
        L_0x016c:
            r12.results = r1     // Catch:{ all -> 0x0229 }
            r1.setFirstCharOfQuery(r13)     // Catch:{ all -> 0x0229 }
            long r6 = r1.getUpdateCount()     // Catch:{ all -> 0x0229 }
            r12.updateCount = r6     // Catch:{ all -> 0x0229 }
            long r6 = r1.getUpdateID()     // Catch:{ all -> 0x0229 }
            r12.lastInsertId = r6     // Catch:{ all -> 0x0229 }
            long r6 = r12.updateCount     // Catch:{ all -> 0x0229 }
            monitor-exit(r14)     // Catch:{ all -> 0x0229 }
            return r6
        L_0x0183:
            r0 = move-exception
            r5 = r21
            r4 = r23
            r6 = 0
        L_0x0189:
            monitor-exit(r2)     // Catch:{ all -> 0x0191 }
            throw r0     // Catch:{ all -> 0x018b }
        L_0x018b:
            r0 = move-exception
            r16 = r1
            r1 = r3
            r2 = r5
            goto L_0x01c3
        L_0x0191:
            r0 = move-exception
            goto L_0x0189
        L_0x0193:
            r0 = move-exception
            r5 = r21
            r4 = r23
            r6 = 0
            r16 = r1
            r1 = r3
            r2 = r5
            goto L_0x01c3
        L_0x019e:
            r0 = move-exception
            r5 = r21
            r3 = r22
            r4 = r23
            r6 = 0
            r1 = r3
            r2 = r5
            goto L_0x01c3
        L_0x01a9:
            r0 = move-exception
            r5 = r6
            r3 = r7
            r4 = r8
            r17 = r9
            r6 = r10
            r13 = r11
            r1 = r3
            r2 = r5
            goto L_0x01c3
        L_0x01b4:
            r0 = move-exception
            r3 = r7
            r4 = r8
            r17 = r9
            r6 = r10
            r13 = r11
            r1 = r3
            goto L_0x01c3
        L_0x01bd:
            r0 = move-exception
            r4 = r8
            r17 = r9
            r6 = r10
            r13 = r11
        L_0x01c3:
            r15.setReadInfoMsgEnabled(r4)     // Catch:{ all -> 0x0229 }
            if (r1 == 0) goto L_0x01d2
            r1.cancel()     // Catch:{ all -> 0x0229 }
            java.util.Timer r3 = r15.getCancelTimer()     // Catch:{ all -> 0x0229 }
            r3.purge()     // Catch:{ all -> 0x0229 }
        L_0x01d2:
            if (r2 == 0) goto L_0x01d7
            r15.setCatalog(r2)     // Catch:{ all -> 0x0229 }
        L_0x01d7:
            if (r26 != 0) goto L_0x01de
            java.util.concurrent.atomic.AtomicBoolean r3 = r12.statementExecuting     // Catch:{ all -> 0x0229 }
            r3.set(r6)     // Catch:{ all -> 0x0229 }
        L_0x01de:
            throw r0     // Catch:{ all -> 0x0229 }
        L_0x01e1:
            r17 = r9
            r13 = r11
            java.lang.String r0 = "Statement.46"
            java.lang.String r0 = com.mysql.jdbc.Messages.getString(r0)     // Catch:{ all -> 0x0229 }
            java.lang.String r1 = "01S03"
            com.mysql.jdbc.ExceptionInterceptor r2 = r24.getExceptionInterceptor()     // Catch:{ all -> 0x0229 }
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r0, (java.lang.String) r1, (com.mysql.jdbc.ExceptionInterceptor) r2)     // Catch:{ all -> 0x0229 }
            throw r0     // Catch:{ all -> 0x0229 }
        L_0x01f5:
            r17 = r9
            r13 = r11
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0229 }
            r0.<init>()     // Catch:{ all -> 0x0229 }
            java.lang.String r1 = "Statement.42"
            java.lang.String r1 = com.mysql.jdbc.Messages.getString(r1)     // Catch:{ all -> 0x0229 }
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ all -> 0x0229 }
            java.lang.String r1 = "Statement.43"
            java.lang.String r1 = com.mysql.jdbc.Messages.getString(r1)     // Catch:{ all -> 0x0229 }
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ all -> 0x0229 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0229 }
            java.lang.String r1 = "S1009"
            com.mysql.jdbc.ExceptionInterceptor r2 = r24.getExceptionInterceptor()     // Catch:{ all -> 0x0229 }
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r0, (java.lang.String) r1, (com.mysql.jdbc.ExceptionInterceptor) r2)     // Catch:{ all -> 0x0229 }
            throw r0     // Catch:{ all -> 0x0229 }
        L_0x0220:
            r0 = move-exception
            r17 = r9
            goto L_0x0227
        L_0x0224:
            r0 = move-exception
            r17 = r1
        L_0x0227:
            monitor-exit(r14)     // Catch:{ all -> 0x0229 }
            throw r0
        L_0x0229:
            r0 = move-exception
            goto L_0x0227
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.StatementImpl.executeUpdateInternal(java.lang.String, boolean, boolean):long");
    }

    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return Util.truncateAndConvertToInt(executeLargeUpdate(sql, autoGeneratedKeys));
    }

    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return Util.truncateAndConvertToInt(executeLargeUpdate(sql, columnIndexes));
    }

    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return Util.truncateAndConvertToInt(executeLargeUpdate(sql, columnNames));
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

    public Connection getConnection() throws SQLException {
        MySQLConnection mySQLConnection;
        synchronized (checkClosed().getConnectionMutex()) {
            mySQLConnection = this.connection;
        }
        return mySQLConnection;
    }

    public int getFetchDirection() throws SQLException {
        return 1000;
    }

    public int getFetchSize() throws SQLException {
        int i;
        synchronized (checkClosed().getConnectionMutex()) {
            i = this.fetchSize;
        }
        return i;
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.retrieveGeneratedKeys) {
                throw SQLError.createSQLException(Messages.getString("Statement.GeneratedKeysNotRequested"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            } else if (this.batchedGeneratedKeys != null) {
                Field[] fields = {new Field("", "GENERATED_KEY", -5, 20)};
                fields[0].setConnection(this.connection);
                ResultSetImpl instance = ResultSetImpl.getInstance(this.currentCatalog, fields, new RowDataStatic(this.batchedGeneratedKeys), this.connection, this, false);
                this.generatedKeysResults = instance;
                return instance;
            } else if (this.lastQueryIsOnDupKeyUpdate) {
                ResultSetInternalMethods generatedKeysInternal = getGeneratedKeysInternal(1);
                this.generatedKeysResults = generatedKeysInternal;
                return generatedKeysInternal;
            } else {
                ResultSetInternalMethods generatedKeysInternal2 = getGeneratedKeysInternal();
                this.generatedKeysResults = generatedKeysInternal2;
                return generatedKeysInternal2;
            }
        }
    }

    /* access modifiers changed from: protected */
    public ResultSetInternalMethods getGeneratedKeysInternal() throws SQLException {
        return getGeneratedKeysInternal(getLargeUpdateCount());
    }

    /* access modifiers changed from: protected */
    public ResultSetInternalMethods getGeneratedKeysInternal(long numKeys) throws SQLException {
        long numKeys2;
        String serverInfo;
        int i;
        synchronized (checkClosed().getConnectionMutex()) {
            int i2 = 1;
            try {
                Field[] fields = {new Field("", "GENERATED_KEY", -5, 20)};
                fields[0].setConnection(this.connection);
                fields[0].setUseOldNameMetadata(true);
                ArrayList<ResultSetRow> rowSet = new ArrayList<>();
                long beginAt = getLastInsertID();
                long j = 0;
                if (beginAt < 0) {
                    fields[0].setUnsigned();
                }
                ResultSetInternalMethods resultSetInternalMethods = this.results;
                if (resultSetInternalMethods != null) {
                    String serverInfo2 = resultSetInternalMethods.getServerInfo();
                    if (numKeys <= 0 || this.results.getFirstCharOfQuery() != 'R' || serverInfo2 == null || serverInfo2.length() <= 0) {
                        numKeys2 = numKeys;
                    } else {
                        numKeys2 = getRecordCountFromInfo(serverInfo2);
                    }
                    if (beginAt == 0 || numKeys2 <= 0) {
                        long j2 = beginAt;
                    } else {
                        int i3 = 0;
                        while (((long) i3) < numKeys2) {
                            byte[][] row = new byte[i2][];
                            if (beginAt > j) {
                                row[0] = StringUtils.getBytes(Long.toString(beginAt));
                                serverInfo = serverInfo2;
                                i = i2;
                            } else {
                                byte[] asBytes = new byte[8];
                                serverInfo = serverInfo2;
                                asBytes[7] = (byte) ((int) (beginAt & 255));
                                asBytes[6] = (byte) ((int) (beginAt >>> 8));
                                asBytes[5] = (byte) ((int) (beginAt >>> 16));
                                asBytes[4] = (byte) ((int) (beginAt >>> 24));
                                asBytes[3] = (byte) ((int) (beginAt >>> 32));
                                asBytes[2] = (byte) ((int) (beginAt >>> 40));
                                asBytes[1] = (byte) ((int) (beginAt >>> 48));
                                asBytes[0] = (byte) ((int) (beginAt >>> 56));
                                i = 1;
                                row[0] = new BigInteger(1, asBytes).toString().getBytes();
                            }
                            rowSet.add(new ByteArrayRow(row, getExceptionInterceptor()));
                            beginAt += (long) this.connection.getAutoIncrementIncrement();
                            i3++;
                            i2 = i;
                            serverInfo2 = serverInfo;
                            j = 0;
                        }
                        long j3 = beginAt;
                    }
                } else {
                    long j4 = beginAt;
                }
                ResultSetImpl gkRs = ResultSetImpl.getInstance(this.currentCatalog, fields, new RowDataStatic(rowSet), this.connection, this, false);
                return gkRs;
            } catch (Throwable th) {
                th = th;
                throw th;
            }
        }
    }

    public int getId() {
        return this.statementId;
    }

    public long getLastInsertID() {
        long j;
        try {
            synchronized (checkClosed().getConnectionMutex()) {
                j = this.lastInsertId;
            }
            return j;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long getLongUpdateCount() {
        try {
            synchronized (checkClosed().getConnectionMutex()) {
                ResultSetInternalMethods resultSetInternalMethods = this.results;
                if (resultSetInternalMethods == null) {
                    return -1;
                }
                if (resultSetInternalMethods.reallyResult()) {
                    return -1;
                }
                long j = this.updateCount;
                return j;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getMaxFieldSize() throws SQLException {
        int i;
        synchronized (checkClosed().getConnectionMutex()) {
            i = this.maxFieldSize;
        }
        return i;
    }

    public int getMaxRows() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            int i = this.maxRows;
            if (i <= 0) {
                return 0;
            }
            return i;
        }
    }

    public boolean getMoreResults() throws SQLException {
        return getMoreResults(1);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00b4, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean getMoreResults(int r8) throws java.sql.SQLException {
        /*
            r7 = this;
            com.mysql.jdbc.MySQLConnection r0 = r7.checkClosed()
            java.lang.Object r0 = r0.getConnectionMutex()
            monitor-enter(r0)
            com.mysql.jdbc.ResultSetInternalMethods r1 = r7.results     // Catch:{ all -> 0x00c4 }
            r2 = 0
            if (r1 != 0) goto L_0x0010
            monitor-exit(r0)     // Catch:{ all -> 0x00c4 }
            return r2
        L_0x0010:
            boolean r1 = r7.createStreamingResultSet()     // Catch:{ all -> 0x00c4 }
            if (r1 == 0) goto L_0x0027
            com.mysql.jdbc.ResultSetInternalMethods r3 = r7.results     // Catch:{ all -> 0x00c4 }
            boolean r3 = r3.reallyResult()     // Catch:{ all -> 0x00c4 }
            if (r3 == 0) goto L_0x0027
        L_0x001e:
            com.mysql.jdbc.ResultSetInternalMethods r3 = r7.results     // Catch:{ all -> 0x00c4 }
            boolean r3 = r3.next()     // Catch:{ all -> 0x00c4 }
            if (r3 == 0) goto L_0x0027
            goto L_0x001e
        L_0x0027:
            com.mysql.jdbc.ResultSetInternalMethods r3 = r7.results     // Catch:{ all -> 0x00c4 }
            com.mysql.jdbc.ResultSetInternalMethods r3 = r3.getNextResultSet()     // Catch:{ all -> 0x00c4 }
            switch(r8) {
                case 1: goto L_0x0065;
                case 2: goto L_0x0050;
                case 3: goto L_0x0034;
                default: goto L_0x0030;
            }     // Catch:{ all -> 0x00c4 }
        L_0x0030:
            java.lang.String r2 = "Statement.19"
            goto L_0x00b5
        L_0x0034:
            com.mysql.jdbc.ResultSetInternalMethods r4 = r7.results     // Catch:{ all -> 0x00c4 }
            if (r4 == 0) goto L_0x004c
            if (r1 != 0) goto L_0x0047
            com.mysql.jdbc.MySQLConnection r4 = r7.connection     // Catch:{ all -> 0x00c4 }
            boolean r4 = r4.getDontTrackOpenResources()     // Catch:{ all -> 0x00c4 }
            if (r4 != 0) goto L_0x0047
            com.mysql.jdbc.ResultSetInternalMethods r4 = r7.results     // Catch:{ all -> 0x00c4 }
            r4.realClose(r2)     // Catch:{ all -> 0x00c4 }
        L_0x0047:
            com.mysql.jdbc.ResultSetInternalMethods r4 = r7.results     // Catch:{ all -> 0x00c4 }
            r4.clearNextResult()     // Catch:{ all -> 0x00c4 }
        L_0x004c:
            r7.closeAllOpenResults()     // Catch:{ all -> 0x00c4 }
            goto L_0x007d
        L_0x0050:
            com.mysql.jdbc.MySQLConnection r4 = r7.connection     // Catch:{ all -> 0x00c4 }
            boolean r4 = r4.getDontTrackOpenResources()     // Catch:{ all -> 0x00c4 }
            if (r4 != 0) goto L_0x005f
            java.util.Set<com.mysql.jdbc.ResultSetInternalMethods> r4 = r7.openResults     // Catch:{ all -> 0x00c4 }
            com.mysql.jdbc.ResultSetInternalMethods r5 = r7.results     // Catch:{ all -> 0x00c4 }
            r4.add(r5)     // Catch:{ all -> 0x00c4 }
        L_0x005f:
            com.mysql.jdbc.ResultSetInternalMethods r4 = r7.results     // Catch:{ all -> 0x00c4 }
            r4.clearNextResult()     // Catch:{ all -> 0x00c4 }
            goto L_0x007d
        L_0x0065:
            com.mysql.jdbc.ResultSetInternalMethods r4 = r7.results     // Catch:{ all -> 0x00c4 }
            if (r4 == 0) goto L_0x007d
            if (r1 != 0) goto L_0x0078
            com.mysql.jdbc.MySQLConnection r4 = r7.connection     // Catch:{ all -> 0x00c4 }
            boolean r4 = r4.getDontTrackOpenResources()     // Catch:{ all -> 0x00c4 }
            if (r4 != 0) goto L_0x0078
            com.mysql.jdbc.ResultSetInternalMethods r4 = r7.results     // Catch:{ all -> 0x00c4 }
            r4.realClose(r2)     // Catch:{ all -> 0x00c4 }
        L_0x0078:
            com.mysql.jdbc.ResultSetInternalMethods r4 = r7.results     // Catch:{ all -> 0x00c4 }
            r4.clearNextResult()     // Catch:{ all -> 0x00c4 }
        L_0x007d:
            r7.results = r3     // Catch:{ all -> 0x00c4 }
            r4 = -1
            if (r3 != 0) goto L_0x0088
            r7.updateCount = r4     // Catch:{ all -> 0x00c4 }
            r7.lastInsertId = r4     // Catch:{ all -> 0x00c4 }
            goto L_0x00a3
        L_0x0088:
            boolean r6 = r3.reallyResult()     // Catch:{ all -> 0x00c4 }
            if (r6 == 0) goto L_0x0093
            r7.updateCount = r4     // Catch:{ all -> 0x00c4 }
            r7.lastInsertId = r4     // Catch:{ all -> 0x00c4 }
            goto L_0x00a3
        L_0x0093:
            com.mysql.jdbc.ResultSetInternalMethods r4 = r7.results     // Catch:{ all -> 0x00c4 }
            long r4 = r4.getUpdateCount()     // Catch:{ all -> 0x00c4 }
            r7.updateCount = r4     // Catch:{ all -> 0x00c4 }
            com.mysql.jdbc.ResultSetInternalMethods r4 = r7.results     // Catch:{ all -> 0x00c4 }
            long r4 = r4.getUpdateID()     // Catch:{ all -> 0x00c4 }
            r7.lastInsertId = r4     // Catch:{ all -> 0x00c4 }
        L_0x00a3:
            com.mysql.jdbc.ResultSetInternalMethods r4 = r7.results     // Catch:{ all -> 0x00c4 }
            if (r4 == 0) goto L_0x00ae
            boolean r4 = r4.reallyResult()     // Catch:{ all -> 0x00c4 }
            if (r4 == 0) goto L_0x00ae
            r2 = 1
        L_0x00ae:
            if (r2 != 0) goto L_0x00b3
            r7.checkAndPerformCloseOnCompletionAction()     // Catch:{ all -> 0x00c4 }
        L_0x00b3:
            monitor-exit(r0)     // Catch:{ all -> 0x00c4 }
            return r2
        L_0x00b5:
            java.lang.String r2 = com.mysql.jdbc.Messages.getString(r2)     // Catch:{ all -> 0x00c4 }
            java.lang.String r4 = "S1009"
            com.mysql.jdbc.ExceptionInterceptor r5 = r7.getExceptionInterceptor()     // Catch:{ all -> 0x00c4 }
            java.sql.SQLException r2 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r2, (java.lang.String) r4, (com.mysql.jdbc.ExceptionInterceptor) r5)     // Catch:{ all -> 0x00c4 }
            throw r2     // Catch:{ all -> 0x00c4 }
        L_0x00c4:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00c4 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.StatementImpl.getMoreResults(int):boolean");
    }

    public int getQueryTimeout() throws SQLException {
        int i;
        synchronized (checkClosed().getConnectionMutex()) {
            i = this.timeoutInMillis / 1000;
        }
        return i;
    }

    private long getRecordCountFromInfo(String serverInfo) {
        StringBuilder recordsBuf = new StringBuilder();
        char c = 0;
        int length = serverInfo.length();
        int i = 0;
        while (i < length) {
            c = serverInfo.charAt(i);
            if (Character.isDigit(c)) {
                break;
            }
            i++;
        }
        recordsBuf.append(c);
        int i2 = i + 1;
        while (i2 < length) {
            c = serverInfo.charAt(i2);
            if (!Character.isDigit(c)) {
                break;
            }
            recordsBuf.append(c);
            i2++;
        }
        long recordsCount = Long.parseLong(recordsBuf.toString());
        StringBuilder duplicatesBuf = new StringBuilder();
        while (i2 < length) {
            c = serverInfo.charAt(i2);
            if (Character.isDigit(c)) {
                break;
            }
            i2++;
        }
        duplicatesBuf.append(c);
        for (int i3 = i2 + 1; i3 < length; i3++) {
            char c2 = serverInfo.charAt(i3);
            if (!Character.isDigit(c2)) {
                break;
            }
            duplicatesBuf.append(c2);
        }
        return recordsCount - Long.parseLong(duplicatesBuf.toString());
    }

    public ResultSet getResultSet() throws SQLException {
        ResultSetInternalMethods resultSetInternalMethods;
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods resultSetInternalMethods2 = this.results;
            resultSetInternalMethods = (resultSetInternalMethods2 == null || !resultSetInternalMethods2.reallyResult()) ? null : this.results;
        }
        return resultSetInternalMethods;
    }

    public int getResultSetConcurrency() throws SQLException {
        int i;
        synchronized (checkClosed().getConnectionMutex()) {
            i = this.resultSetConcurrency;
        }
        return i;
    }

    public int getResultSetHoldability() throws SQLException {
        return 1;
    }

    /* access modifiers changed from: protected */
    public ResultSetInternalMethods getResultSetInternal() {
        ResultSetInternalMethods resultSetInternalMethods;
        try {
            synchronized (checkClosed().getConnectionMutex()) {
                resultSetInternalMethods = this.results;
            }
            return resultSetInternalMethods;
        } catch (SQLException e) {
            return this.results;
        }
    }

    public int getResultSetType() throws SQLException {
        int i;
        synchronized (checkClosed().getConnectionMutex()) {
            i = this.resultSetType;
        }
        return i;
    }

    public int getUpdateCount() throws SQLException {
        return Util.truncateAndConvertToInt(getLargeUpdateCount());
    }

    public SQLWarning getWarnings() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.clearWarningsCalled) {
                return null;
            }
            if (this.connection.versionMeetsMinimum(4, 1, 0)) {
                SQLWarning pendingWarningsFromServer = SQLError.convertShowWarningsToSQLWarnings(this.connection);
                SQLWarning sQLWarning = this.warningChain;
                if (sQLWarning != null) {
                    sQLWarning.setNextWarning(pendingWarningsFromServer);
                } else {
                    this.warningChain = pendingWarningsFromServer;
                }
                SQLWarning sQLWarning2 = this.warningChain;
                return sQLWarning2;
            }
            SQLWarning pendingWarningsFromServer2 = this.warningChain;
            return pendingWarningsFromServer2;
        }
    }

    /* access modifiers changed from: protected */
    public void realClose(boolean calledExplicitly, boolean closeOpenResults) throws SQLException {
        MySQLConnection locallyScopedConn = this.connection;
        if (locallyScopedConn != null && !this.isClosed) {
            if (!locallyScopedConn.getDontTrackOpenResources()) {
                locallyScopedConn.unregisterStatement(this);
            }
            if (this.useUsageAdvisor && !calledExplicitly) {
                this.connection.getProfilerEventHandlerInstance().processEvent((byte) 0, this.connection, this, (ResultSetInternalMethods) null, 0, new Throwable(), Messages.getString("Statement.63"));
            }
            if (closeOpenResults) {
                closeOpenResults = !this.holdResultsOpenOverClose && !this.connection.getDontTrackOpenResources();
            }
            if (closeOpenResults) {
                ResultSetInternalMethods resultSetInternalMethods = this.results;
                if (resultSetInternalMethods != null) {
                    try {
                        resultSetInternalMethods.close();
                    } catch (Exception e) {
                    }
                }
                ResultSetInternalMethods resultSetInternalMethods2 = this.generatedKeysResults;
                if (resultSetInternalMethods2 != null) {
                    try {
                        resultSetInternalMethods2.close();
                    } catch (Exception e2) {
                    }
                }
                closeAllOpenResults();
            }
            this.isClosed = true;
            this.results = null;
            this.generatedKeysResults = null;
            this.connection = null;
            this.warningChain = null;
            this.openResults = null;
            this.batchedGeneratedKeys = null;
            this.localInfileInputStream = null;
            this.pingTarget = null;
        }
    }

    public void setCursorName(String name) throws SQLException {
    }

    public void setEscapeProcessing(boolean enable) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            this.doEscapeProcessing = enable;
        }
    }

    public void setFetchDirection(int direction) throws SQLException {
        switch (direction) {
            case 1000:
            case 1001:
            case 1002:
                return;
            default:
                throw SQLError.createSQLException(Messages.getString("Statement.5"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
    }

    public void setFetchSize(int rows) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (rows >= 0 || rows == Integer.MIN_VALUE) {
                if (this.maxRows > 0) {
                    if (rows <= getMaxRows()) {
                    }
                }
                this.fetchSize = rows;
            }
            throw SQLError.createSQLException(Messages.getString("Statement.7"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
    }

    public void setHoldResultsOpenOverClose(boolean holdResultsOpenOverClose2) {
        try {
            synchronized (checkClosed().getConnectionMutex()) {
                this.holdResultsOpenOverClose = holdResultsOpenOverClose2;
            }
        } catch (SQLException e) {
        }
    }

    public void setMaxFieldSize(int max) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (max >= 0) {
                try {
                    int maxBuf = this.connection != null ? this.connection.getMaxAllowedPacket() : MysqlIO.getMaxBuf();
                    if (max <= maxBuf) {
                        this.maxFieldSize = max;
                    } else {
                        throw SQLError.createSQLException(Messages.getString("Statement.13", new Object[]{Long.valueOf((long) maxBuf)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                    }
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                throw SQLError.createSQLException(Messages.getString("Statement.11"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
    }

    public void setMaxRows(int max) throws SQLException {
        setLargeMaxRows((long) max);
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (seconds >= 0) {
                try {
                    this.timeoutInMillis = seconds * 1000;
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                throw SQLError.createSQLException(Messages.getString("Statement.21"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setResultSetConcurrency(int concurrencyFlag) {
        try {
            synchronized (checkClosed().getConnectionMutex()) {
                this.resultSetConcurrency = concurrencyFlag;
            }
        } catch (SQLException e) {
        }
    }

    /* access modifiers changed from: package-private */
    public void setResultSetType(int typeFlag) {
        try {
            synchronized (checkClosed().getConnectionMutex()) {
                this.resultSetType = typeFlag;
            }
        } catch (SQLException e) {
        }
    }

    /* access modifiers changed from: protected */
    public void getBatchedGeneratedKeys(Statement batchedStatement) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.retrieveGeneratedKeys) {
                ResultSet rs = null;
                try {
                    ResultSet rs2 = batchedStatement.getGeneratedKeys();
                    while (rs2.next()) {
                        this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][]{rs2.getBytes(1)}, getExceptionInterceptor()));
                    }
                    if (rs2 != null) {
                        rs2.close();
                    }
                } catch (Throwable th) {
                    if (rs != null) {
                        rs.close();
                    }
                    throw th;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void getBatchedGeneratedKeys(int maxKeys) throws SQLException {
        ResultSet rs;
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.retrieveGeneratedKeys) {
                ResultSet rs2 = null;
                if (maxKeys == 0) {
                    try {
                        rs = getGeneratedKeysInternal();
                    } catch (Throwable th) {
                        th = th;
                        this.isImplicitlyClosingResults = false;
                        throw th;
                    }
                } else {
                    rs = getGeneratedKeysInternal((long) maxKeys);
                }
                while (rs.next()) {
                    this.batchedGeneratedKeys.add(new ByteArrayRow(new byte[][]{rs.getBytes(1)}, getExceptionInterceptor()));
                }
                this.isImplicitlyClosingResults = true;
                if (rs != null) {
                    rs.close();
                }
                this.isImplicitlyClosingResults = false;
            }
        }
    }

    private boolean useServerFetch() throws SQLException {
        boolean z;
        synchronized (checkClosed().getConnectionMutex()) {
            z = this.connection.isCursorFetchEnabled() && this.fetchSize > 0 && this.resultSetConcurrency == 1007 && this.resultSetType == 1003;
        }
        return z;
    }

    public boolean isClosed() throws SQLException {
        boolean z;
        MySQLConnection locallyScopedConn = this.connection;
        if (locallyScopedConn == null) {
            return true;
        }
        synchronized (locallyScopedConn.getConnectionMutex()) {
            z = this.isClosed;
        }
        return z;
    }

    public boolean isPoolable() throws SQLException {
        return this.isPoolable;
    }

    public void setPoolable(boolean poolable) throws SQLException {
        this.isPoolable = poolable;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        checkClosed();
        return iface.isInstance(this);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        try {
            return iface.cast(this);
        } catch (ClassCastException e) {
            throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
    }

    protected static int findStartOfStatement(String sql) {
        if (StringUtils.startsWithIgnoreCaseAndWs(sql, "/*")) {
            int statementStartPos = sql.indexOf("*/");
            if (statementStartPos == -1) {
                return 0;
            }
            return statementStartPos + 2;
        } else if (!StringUtils.startsWithIgnoreCaseAndWs(sql, "--") && !StringUtils.startsWithIgnoreCaseAndWs(sql, "#")) {
            return 0;
        } else {
            int statementStartPos2 = sql.indexOf(10);
            if (statementStartPos2 != -1) {
                return statementStartPos2;
            }
            int statementStartPos3 = sql.indexOf(13);
            if (statementStartPos3 == -1) {
                return 0;
            }
            return statementStartPos3;
        }
    }

    public InputStream getLocalInfileInputStream() {
        return this.localInfileInputStream;
    }

    public void setLocalInfileInputStream(InputStream stream) {
        this.localInfileInputStream = stream;
    }

    public void setPingTarget(PingTarget pingTarget2) {
        this.pingTarget = pingTarget2;
    }

    public ExceptionInterceptor getExceptionInterceptor() {
        return this.exceptionInterceptor;
    }

    /* access modifiers changed from: protected */
    public boolean containsOnDuplicateKeyInString(String sql) {
        return getOnDuplicateKeyLocation(sql, this.connection.getDontCheckOnDuplicateKeyUpdateInSQL(), this.connection.getRewriteBatchedStatements(), this.connection.isNoBackslashEscapesSet()) != -1;
    }

    protected static int getOnDuplicateKeyLocation(String sql, boolean dontCheckOnDuplicateKeyUpdateInSQL, boolean rewriteBatchedStatements, boolean noBackslashEscapes) {
        if (dontCheckOnDuplicateKeyUpdateInSQL && !rewriteBatchedStatements) {
            return -1;
        }
        return StringUtils.indexOfIgnoreCase(0, sql, ON_DUPLICATE_KEY_UPDATE_CLAUSE, "\"'`", "\"'`", noBackslashEscapes ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
    }

    public void closeOnCompletion() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            this.closeOnCompletion = true;
        }
    }

    public boolean isCloseOnCompletion() throws SQLException {
        boolean z;
        synchronized (checkClosed().getConnectionMutex()) {
            z = this.closeOnCompletion;
        }
        return z;
    }

    public long[] executeLargeBatch() throws SQLException {
        return executeBatchInternal();
    }

    public long executeLargeUpdate(String sql) throws SQLException {
        return executeUpdateInternal(sql, false, false);
    }

    public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        boolean z = true;
        if (autoGeneratedKeys != 1) {
            z = false;
        }
        return executeUpdateInternal(sql, false, z);
    }

    public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return executeUpdateInternal(sql, false, columnIndexes != null && columnIndexes.length > 0);
    }

    public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
        return executeUpdateInternal(sql, false, columnNames != null && columnNames.length > 0);
    }

    public long getLargeMaxRows() throws SQLException {
        return (long) getMaxRows();
    }

    public long getLargeUpdateCount() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            ResultSetInternalMethods resultSetInternalMethods = this.results;
            if (resultSetInternalMethods == null) {
                return -1;
            }
            if (resultSetInternalMethods.reallyResult()) {
                return -1;
            }
            long updateCount2 = this.results.getUpdateCount();
            return updateCount2;
        }
    }

    public void setLargeMaxRows(long max) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (max > 50000000 || max < 0) {
                throw SQLError.createSQLException(Messages.getString("Statement.15") + max + " > " + 50000000 + ".", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
            if (max == 0) {
                max = -1;
            }
            this.maxRows = (int) max;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isCursorRequired() throws SQLException {
        return false;
    }
}
