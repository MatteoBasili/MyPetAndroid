package com.mysql.jdbc;

import androidx.core.internal.view.SupportMenu;
import com.application.mypet.BuildConfig;
import com.mysql.jdbc.CallableStatement;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.log.Log;
import com.mysql.jdbc.log.LogFactory;
import com.mysql.jdbc.log.NullLogger;
import com.mysql.jdbc.profiler.ProfilerEventHandler;
import com.mysql.jdbc.util.LRUCache;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLPermission;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Stack;
import java.util.TimeZone;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import kotlinx.coroutines.DebugKt;

public class ConnectionImpl extends ConnectionPropertiesImpl implements MySQLConnection {
    private static final SQLPermission ABORT_PERM = new SQLPermission("abort");
    private static final Object CHARSET_CONVERTER_NOT_AVAILABLE_MARKER = new Object();
    protected static final String DEFAULT_LOGGER_CLASS = "com.mysql.jdbc.log.StandardLogger";
    private static final int DEFAULT_RESULT_SET_CONCURRENCY = 1007;
    private static final int DEFAULT_RESULT_SET_TYPE = 1003;
    private static final int HISTOGRAM_BUCKETS = 20;
    private static final Constructor<?> JDBC_4_CONNECTION_CTOR;
    public static final String JDBC_LOCAL_CHARACTER_SET_RESULTS = "jdbc.local.character_set_results";
    private static final String LOGGER_INSTANCE_NAME = "MySQL";
    private static final Log NULL_LOGGER = new NullLogger(LOGGER_INSTANCE_NAME);
    private static final String SERVER_VERSION_STRING_VAR_NAME = "server_version_string";
    private static final SQLPermission SET_NETWORK_TIMEOUT_PERM = new SQLPermission("setNetworkTimeout");
    public static Map<?, ?> charsetMap = null;
    private static final Map<String, Map<String, Integer>> customCharsetToMblenMapByUrl = new HashMap();
    private static final Map<String, Map<Integer, String>> customIndexToCharsetMapByUrl = new HashMap();
    private static Map<String, Integer> mapTransIsolationNameToValue = null;
    private static final Random random = new Random();
    protected static Map<?, ?> roundRobinStatsMap = null;
    private static final long serialVersionUID = 2877471301981509474L;
    private boolean autoCommit;
    private int autoIncrementIncrement;
    private CacheAdapter<String, PreparedStatement.ParseInfo> cachedPreparedStatementParams;
    private transient Timer cancelTimer;
    private String characterSetMetadata;
    private String characterSetResultsOnServer;
    private final Map<String, Object> charsetConverterMap;
    private long connectionCreationTimeMillis;
    private long connectionId;
    private List<Extension> connectionLifecycleInterceptors;
    private String database;
    private DatabaseMetaData dbmd;
    private TimeZone defaultTimeZone;
    private String errorMessageEncoding;
    private ProfilerEventHandler eventSink;
    private ExceptionInterceptor exceptionInterceptor;
    private Throwable forceClosedReason;
    private boolean hasIsolationLevels;
    private boolean hasQuotedIdentifiers;
    private boolean hasTriedMasterFlag;
    private String host;
    private String hostPortPair;
    public Map<Integer, String> indexToCustomMysqlCharset;

    /* renamed from: io  reason: collision with root package name */
    private transient MysqlIO f4io;
    private boolean isClientTzUTC;
    private boolean isClosed;
    private boolean isInGlobalTx;
    private boolean isRunningOnJDK13;
    private boolean isServerTzUTC;
    private int isolationLevel;
    private long lastQueryFinishedTime;
    private transient Log log;
    private long longestQueryTimeMs;
    private boolean lowerCaseTableNames;
    private long maximumNumberTablesAccessed;
    private long metricsLastReportedMs;
    private long minimumNumberTablesAccessed;
    private String myURL;
    private Map<String, Integer> mysqlCharsetToCustomMblen;
    private boolean needsPing;
    private int netBufferLength;
    private boolean noBackslashEscapes;
    private long[] numTablesMetricsHistBreakpoints;
    private int[] numTablesMetricsHistCounts;
    private long numberOfPreparedExecutes;
    private long numberOfPrepares;
    private long numberOfQueriesIssued;
    private long numberOfResultSetsCreated;
    private long[] oldHistBreakpoints;
    private int[] oldHistCounts;
    private final CopyOnWriteArrayList<Statement> openStatements;
    private String origDatabaseToConnectTo;
    private String origHostToConnectTo;
    private int origPortToConnectTo;
    private LRUCache<CompoundCacheKey, CallableStatement.CallableStatementParamInfo> parsedCallableStatementCache;
    private boolean parserKnowsUnicode;
    private String password;
    private long[] perfMetricsHistBreakpoints;
    private int[] perfMetricsHistCounts;
    private int port;
    protected Properties props;
    private MySQLConnection proxy;
    private long queryTimeCount;
    private double queryTimeMean;
    private double queryTimeSum;
    private double queryTimeSumSquares;
    private boolean readInfoMsg;
    private boolean readOnly;
    private InvocationHandler realProxy;
    private boolean requiresEscapingEncoder;
    protected LRUCache<String, CachedResultSetMetaData> resultSetMetadataCache;
    /* access modifiers changed from: private */
    public CacheAdapter<String, Map<String, String>> serverConfigCache;
    private LRUCache<CompoundCacheKey, ServerPreparedStatement> serverSideStatementCache;
    private LRUCache<String, Boolean> serverSideStatementCheckCache;
    private TimeZone serverTimezoneTZ;
    private boolean serverTruncatesFracSecs;
    private Map<String, String> serverVariables;
    private Calendar sessionCalendar;
    private int sessionMaxRows;
    private long shortestQueryTimeMs;
    private String statementComment;
    private List<StatementInterceptorV2> statementInterceptors;
    private boolean storesLowerCaseTableName;
    private double totalQueryTimeMs;
    private boolean transactionsSupported;
    private Map<String, Class<?>> typeMap;
    private boolean useAnsiQuotes;
    private boolean usePlatformCharsetConverters;
    private boolean useServerPreparedStmts;
    private String user;
    private Calendar utcCalendar;

    static {
        mapTransIsolationNameToValue = null;
        HashMap hashMap = new HashMap(8);
        mapTransIsolationNameToValue = hashMap;
        hashMap.put("READ-UNCOMMITED", 1);
        mapTransIsolationNameToValue.put("READ-UNCOMMITTED", 1);
        mapTransIsolationNameToValue.put("READ-COMMITTED", 2);
        mapTransIsolationNameToValue.put("REPEATABLE-READ", 4);
        mapTransIsolationNameToValue.put("SERIALIZABLE", 8);
        if (Util.isJdbc4()) {
            try {
                JDBC_4_CONNECTION_CTOR = Class.forName("com.mysql.jdbc.JDBC4Connection").getConstructor(new Class[]{String.class, Integer.TYPE, Properties.class, String.class, String.class});
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e2) {
                throw new RuntimeException(e2);
            } catch (ClassNotFoundException e3) {
                throw new RuntimeException(e3);
            }
        } else {
            JDBC_4_CONNECTION_CTOR = null;
        }
    }

    public String getHost() {
        return this.host;
    }

    public String getHostPortPair() {
        String str = this.hostPortPair;
        return str != null ? str : this.host + ":" + this.port;
    }

    public boolean isProxySet() {
        return this.proxy != null;
    }

    public void setProxy(MySQLConnection proxy2) {
        this.proxy = proxy2;
        this.realProxy = proxy2 instanceof MultiHostMySQLConnection ? ((MultiHostMySQLConnection) proxy2).getThisAsProxy() : null;
    }

    private MySQLConnection getProxy() {
        MySQLConnection mySQLConnection = this.proxy;
        return mySQLConnection != null ? mySQLConnection : this;
    }

    @Deprecated
    public MySQLConnection getLoadBalanceSafeProxy() {
        return getMultiHostSafeProxy();
    }

    public MySQLConnection getMultiHostSafeProxy() {
        return getProxy();
    }

    public MySQLConnection getActiveMySQLConnection() {
        return this;
    }

    public Object getConnectionMutex() {
        InvocationHandler invocationHandler = this.realProxy;
        return invocationHandler != null ? invocationHandler : getProxy();
    }

    public class ExceptionInterceptorChain implements ExceptionInterceptor {
        private List<Extension> interceptors;

        ExceptionInterceptorChain(String interceptorClasses) throws SQLException {
            this.interceptors = Util.loadExtensions(ConnectionImpl.this, ConnectionImpl.this.props, interceptorClasses, "Connection.BadExceptionInterceptor", this);
        }

        /* access modifiers changed from: package-private */
        public void addRingZero(ExceptionInterceptor interceptor) throws SQLException {
            this.interceptors.add(0, interceptor);
        }

        public SQLException interceptException(SQLException sqlEx, Connection conn) {
            List<Extension> list = this.interceptors;
            if (list != null) {
                Iterator<Extension> iter = list.iterator();
                while (iter.hasNext()) {
                    sqlEx = ((ExceptionInterceptor) iter.next()).interceptException(sqlEx, ConnectionImpl.this);
                }
            }
            return sqlEx;
        }

        public void destroy() {
            List<Extension> list = this.interceptors;
            if (list != null) {
                Iterator<Extension> iter = list.iterator();
                while (iter.hasNext()) {
                    ((ExceptionInterceptor) iter.next()).destroy();
                }
            }
        }

        public void init(Connection conn, Properties properties) throws SQLException {
            List<Extension> list = this.interceptors;
            if (list != null) {
                Iterator<Extension> iter = list.iterator();
                while (iter.hasNext()) {
                    ((ExceptionInterceptor) iter.next()).init(conn, properties);
                }
            }
        }

        public List<Extension> getInterceptors() {
            return this.interceptors;
        }
    }

    static class CompoundCacheKey {
        final String componentOne;
        final String componentTwo;
        final int hashCode;

        CompoundCacheKey(String partOne, String partTwo) {
            this.componentOne = partOne;
            this.componentTwo = partTwo;
            this.hashCode = (((17 * 31) + (partOne != null ? partOne.hashCode() : 0)) * 31) + (partTwo != null ? partTwo.hashCode() : 0);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && CompoundCacheKey.class.isAssignableFrom(obj.getClass())) {
                CompoundCacheKey another = (CompoundCacheKey) obj;
                String str = this.componentOne;
                if (str != null ? str.equals(another.componentOne) : another.componentOne == null) {
                    String str2 = this.componentTwo;
                    if (str2 != null) {
                        return str2.equals(another.componentTwo);
                    }
                    if (another.componentTwo == null) {
                        return true;
                    }
                    return false;
                }
            }
            return false;
        }

        public int hashCode() {
            return this.hashCode;
        }
    }

    protected static SQLException appendMessageToException(SQLException sqlEx, String messageToAppend, ExceptionInterceptor interceptor) {
        String origMessage = sqlEx.getMessage();
        String sqlState = sqlEx.getSQLState();
        int vendorErrorCode = sqlEx.getErrorCode();
        StringBuilder messageBuf = new StringBuilder(origMessage.length() + messageToAppend.length());
        messageBuf.append(origMessage);
        messageBuf.append(messageToAppend);
        SQLException sqlExceptionWithNewMessage = SQLError.createSQLException(messageBuf.toString(), sqlState, vendorErrorCode, interceptor);
        try {
            Class<?> stackTraceElementArrayClass = Array.newInstance(Class.forName("java.lang.StackTraceElement"), new int[]{0}).getClass();
            Method getStackTraceMethod = Throwable.class.getMethod("getStackTrace", new Class[0]);
            Method setStackTraceMethod = Throwable.class.getMethod("setStackTrace", new Class[]{stackTraceElementArrayClass});
            if (getStackTraceMethod == null || setStackTraceMethod == null) {
                SQLException sQLException = sqlEx;
                return sqlExceptionWithNewMessage;
            }
            try {
                setStackTraceMethod.invoke(sqlExceptionWithNewMessage, new Object[]{getStackTraceMethod.invoke(sqlEx, new Object[0])});
            } catch (NoClassDefFoundError | NoSuchMethodException e) {
            }
            return sqlExceptionWithNewMessage;
        } catch (NoClassDefFoundError e2) {
            SQLException sQLException2 = sqlEx;
        } catch (NoSuchMethodException e3) {
            SQLException sQLException3 = sqlEx;
        } catch (Throwable th) {
            SQLException sQLException4 = sqlEx;
        }
    }

    public Timer getCancelTimer() {
        Timer timer;
        synchronized (getConnectionMutex()) {
            if (this.cancelTimer == null) {
                this.cancelTimer = new Timer("MySQL Statement Cancellation Timer", true);
            }
            timer = this.cancelTimer;
        }
        return timer;
    }

    protected static Connection getInstance(String hostToConnectTo, int portToConnectTo, Properties info, String databaseToConnectTo, String url) throws SQLException {
        if (!Util.isJdbc4()) {
            return new ConnectionImpl(hostToConnectTo, portToConnectTo, info, databaseToConnectTo, url);
        }
        return (Connection) Util.handleNewInstance(JDBC_4_CONNECTION_CTOR, new Object[]{hostToConnectTo, Integer.valueOf(portToConnectTo), info, databaseToConnectTo, url}, (ExceptionInterceptor) null);
    }

    protected static synchronized int getNextRoundRobinHostIndex(String url, List<?> hostList) {
        int index;
        synchronized (ConnectionImpl.class) {
            index = random.nextInt(hostList.size());
        }
        return index;
    }

    private static boolean nullSafeCompare(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return true;
        }
        if (s1 == null && s2 != null) {
            return false;
        }
        if (s1 == null || !s1.equals(s2)) {
            return false;
        }
        return true;
    }

    protected ConnectionImpl() {
        this.proxy = null;
        this.realProxy = null;
        this.autoCommit = true;
        this.characterSetMetadata = null;
        this.characterSetResultsOnServer = null;
        this.charsetConverterMap = new HashMap(CharsetMapping.getNumberOfCharsetsConfigured());
        this.connectionCreationTimeMillis = 0;
        this.database = null;
        this.dbmd = null;
        this.hasIsolationLevels = false;
        this.hasQuotedIdentifiers = false;
        this.host = null;
        this.indexToCustomMysqlCharset = null;
        this.mysqlCharsetToCustomMblen = null;
        this.f4io = null;
        this.isClientTzUTC = false;
        this.isClosed = true;
        this.isInGlobalTx = false;
        this.isRunningOnJDK13 = false;
        this.isolationLevel = 2;
        this.isServerTzUTC = false;
        this.lastQueryFinishedTime = 0;
        this.log = NULL_LOGGER;
        this.longestQueryTimeMs = 0;
        this.lowerCaseTableNames = false;
        this.maximumNumberTablesAccessed = 0;
        this.sessionMaxRows = -1;
        this.minimumNumberTablesAccessed = Long.MAX_VALUE;
        this.myURL = null;
        this.needsPing = false;
        this.netBufferLength = 16384;
        this.noBackslashEscapes = false;
        this.serverTruncatesFracSecs = false;
        this.numberOfPreparedExecutes = 0;
        this.numberOfPrepares = 0;
        this.numberOfQueriesIssued = 0;
        this.numberOfResultSetsCreated = 0;
        this.oldHistBreakpoints = null;
        this.oldHistCounts = null;
        this.openStatements = new CopyOnWriteArrayList<>();
        this.parserKnowsUnicode = false;
        this.password = null;
        this.port = 3306;
        this.props = null;
        this.readInfoMsg = false;
        this.readOnly = false;
        this.serverTimezoneTZ = null;
        this.serverVariables = null;
        this.shortestQueryTimeMs = Long.MAX_VALUE;
        this.totalQueryTimeMs = 0.0d;
        this.transactionsSupported = false;
        this.useAnsiQuotes = false;
        this.user = null;
        this.useServerPreparedStmts = false;
        this.errorMessageEncoding = "Cp1252";
        this.hasTriedMasterFlag = false;
        this.statementComment = null;
        this.autoIncrementIncrement = 0;
    }

    public ConnectionImpl(String hostToConnectTo, int portToConnectTo, Properties info, String databaseToConnectTo, String url) throws SQLException {
        this.proxy = null;
        this.realProxy = null;
        boolean z = true;
        this.autoCommit = true;
        this.characterSetMetadata = null;
        this.characterSetResultsOnServer = null;
        this.charsetConverterMap = new HashMap(CharsetMapping.getNumberOfCharsetsConfigured());
        this.connectionCreationTimeMillis = 0;
        this.database = null;
        this.dbmd = null;
        this.hasIsolationLevels = false;
        this.hasQuotedIdentifiers = false;
        this.host = null;
        this.indexToCustomMysqlCharset = null;
        this.mysqlCharsetToCustomMblen = null;
        this.f4io = null;
        this.isClientTzUTC = false;
        this.isClosed = true;
        this.isInGlobalTx = false;
        this.isRunningOnJDK13 = false;
        this.isolationLevel = 2;
        this.isServerTzUTC = false;
        this.lastQueryFinishedTime = 0;
        this.log = NULL_LOGGER;
        this.longestQueryTimeMs = 0;
        this.lowerCaseTableNames = false;
        this.maximumNumberTablesAccessed = 0;
        this.sessionMaxRows = -1;
        this.minimumNumberTablesAccessed = Long.MAX_VALUE;
        this.myURL = null;
        this.needsPing = false;
        this.netBufferLength = 16384;
        this.noBackslashEscapes = false;
        this.serverTruncatesFracSecs = false;
        this.numberOfPreparedExecutes = 0;
        this.numberOfPrepares = 0;
        this.numberOfQueriesIssued = 0;
        this.numberOfResultSetsCreated = 0;
        this.oldHistBreakpoints = null;
        this.oldHistCounts = null;
        this.openStatements = new CopyOnWriteArrayList<>();
        this.parserKnowsUnicode = false;
        this.password = null;
        this.port = 3306;
        this.props = null;
        this.readInfoMsg = false;
        this.readOnly = false;
        this.serverTimezoneTZ = null;
        this.serverVariables = null;
        this.shortestQueryTimeMs = Long.MAX_VALUE;
        this.totalQueryTimeMs = 0.0d;
        this.transactionsSupported = false;
        this.useAnsiQuotes = false;
        this.user = null;
        this.useServerPreparedStmts = false;
        this.errorMessageEncoding = "Cp1252";
        this.hasTriedMasterFlag = false;
        this.statementComment = null;
        this.autoIncrementIncrement = 0;
        this.connectionCreationTimeMillis = System.currentTimeMillis();
        databaseToConnectTo = databaseToConnectTo == null ? "" : databaseToConnectTo;
        this.origHostToConnectTo = hostToConnectTo;
        this.origPortToConnectTo = portToConnectTo;
        this.origDatabaseToConnectTo = databaseToConnectTo;
        try {
            Blob.class.getMethod("truncate", new Class[]{Long.TYPE});
            this.isRunningOnJDK13 = false;
        } catch (NoSuchMethodException e) {
            this.isRunningOnJDK13 = true;
        }
        this.sessionCalendar = new GregorianCalendar();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        this.utcCalendar = gregorianCalendar;
        gregorianCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        this.log = LogFactory.getLogger(getLogger(), LOGGER_INSTANCE_NAME, getExceptionInterceptor());
        if (NonRegisteringDriver.isHostPropertiesList(hostToConnectTo)) {
            Properties hostSpecificProps = NonRegisteringDriver.expandHostKeyValues(hostToConnectTo);
            Enumeration<?> propertyNames = hostSpecificProps.propertyNames();
            while (propertyNames.hasMoreElements()) {
                String propertyName = propertyNames.nextElement().toString();
                info.setProperty(propertyName, hostSpecificProps.getProperty(propertyName));
            }
        } else if (hostToConnectTo == null) {
            this.host = "localhost";
            this.hostPortPair = this.host + ":" + portToConnectTo;
        } else {
            this.host = hostToConnectTo;
            if (hostToConnectTo.indexOf(":") == -1) {
                this.hostPortPair = this.host + ":" + portToConnectTo;
            } else {
                this.hostPortPair = this.host;
            }
        }
        this.port = portToConnectTo;
        this.database = databaseToConnectTo;
        this.myURL = url;
        this.user = info.getProperty(NonRegisteringDriver.USER_PROPERTY_KEY);
        this.password = info.getProperty(NonRegisteringDriver.PASSWORD_PROPERTY_KEY);
        String str = this.user;
        if (str == null || str.equals("")) {
            this.user = "";
        }
        if (this.password == null) {
            this.password = "";
        }
        this.props = info;
        initializeDriverProperties(info);
        TimeZone defaultTimeZone2 = TimeUtil.getDefaultTimeZone(getCacheDefaultTimezone());
        this.defaultTimeZone = defaultTimeZone2;
        this.isClientTzUTC = (defaultTimeZone2.useDaylightTime() || this.defaultTimeZone.getRawOffset() != 0) ? false : z;
        try {
            this.dbmd = getMetaData(false, false);
            initializeSafeStatementInterceptors();
            createNewIO(false);
            unSafeStatementInterceptors();
            AbandonedConnectionCleanupThread.trackConnection(this, this.f4io.getNetworkResources());
        } catch (SQLException ex) {
            cleanup(ex);
            throw ex;
        } catch (Exception ex2) {
            cleanup(ex2);
            StringBuilder mesg = new StringBuilder(128);
            if (!getParanoid()) {
                mesg.append("Cannot connect to MySQL server on ");
                mesg.append(this.host);
                mesg.append(":");
                mesg.append(this.port);
                mesg.append(".\n\n");
                mesg.append("Make sure that there is a MySQL server ");
                mesg.append("running on the machine/port you are trying ");
                mesg.append("to connect to and that the machine this software is running on ");
                mesg.append("is able to connect to this host/port (i.e. not firewalled). ");
                mesg.append("Also make sure that the server has not been started with the --skip-networking ");
                mesg.append("flag.\n\n");
            } else {
                mesg.append("Unable to connect to database.");
            }
            SQLException sqlEx = SQLError.createSQLException(mesg.toString(), SQLError.SQL_STATE_COMMUNICATION_LINK_FAILURE, getExceptionInterceptor());
            sqlEx.initCause(ex2);
            throw sqlEx;
        }
    }

    public void unSafeStatementInterceptors() throws SQLException {
        ArrayList<StatementInterceptorV2> unSafedStatementInterceptors = new ArrayList<>(this.statementInterceptors.size());
        for (int i = 0; i < this.statementInterceptors.size(); i++) {
            unSafedStatementInterceptors.add(((NoSubInterceptorWrapper) this.statementInterceptors.get(i)).getUnderlyingInterceptor());
        }
        this.statementInterceptors = unSafedStatementInterceptors;
        MysqlIO mysqlIO = this.f4io;
        if (mysqlIO != null) {
            mysqlIO.setStatementInterceptors(unSafedStatementInterceptors);
        }
    }

    public void initializeSafeStatementInterceptors() throws SQLException {
        this.isClosed = false;
        List<Extension> unwrappedInterceptors = Util.loadExtensions(this, this.props, getStatementInterceptors(), "MysqlIo.BadStatementInterceptor", getExceptionInterceptor());
        this.statementInterceptors = new ArrayList(unwrappedInterceptors.size());
        for (int i = 0; i < unwrappedInterceptors.size(); i++) {
            Extension interceptor = unwrappedInterceptors.get(i);
            if (!(interceptor instanceof StatementInterceptor)) {
                this.statementInterceptors.add(new NoSubInterceptorWrapper((StatementInterceptorV2) interceptor));
            } else if (ReflectiveStatementInterceptorAdapter.getV2PostProcessMethod(interceptor.getClass()) != null) {
                this.statementInterceptors.add(new NoSubInterceptorWrapper(new ReflectiveStatementInterceptorAdapter((StatementInterceptor) interceptor)));
            } else {
                this.statementInterceptors.add(new NoSubInterceptorWrapper(new V1toV2StatementInterceptorAdapter((StatementInterceptor) interceptor)));
            }
        }
    }

    public List<StatementInterceptorV2> getStatementInterceptorsInstances() {
        return this.statementInterceptors;
    }

    private void addToHistogram(int[] histogramCounts, long[] histogramBreakpoints, long value, int numberOfTimes, long currentLowerBound, long currentUpperBound) {
        if (histogramCounts == null) {
            createInitialHistogram(histogramBreakpoints, currentLowerBound, currentUpperBound);
            return;
        }
        for (int i = 0; i < 20; i++) {
            if (histogramBreakpoints[i] >= value) {
                histogramCounts[i] = histogramCounts[i] + numberOfTimes;
                return;
            }
        }
    }

    private void addToPerformanceHistogram(long value, int numberOfTimes) {
        checkAndCreatePerformanceHistogram();
        int[] iArr = this.perfMetricsHistCounts;
        long[] jArr = this.perfMetricsHistBreakpoints;
        long j = this.shortestQueryTimeMs;
        if (j == Long.MAX_VALUE) {
            j = 0;
        }
        addToHistogram(iArr, jArr, value, numberOfTimes, j, this.longestQueryTimeMs);
    }

    private void addToTablesAccessedHistogram(long value, int numberOfTimes) {
        checkAndCreateTablesAccessedHistogram();
        int[] iArr = this.numTablesMetricsHistCounts;
        long[] jArr = this.numTablesMetricsHistBreakpoints;
        long j = this.minimumNumberTablesAccessed;
        if (j == Long.MAX_VALUE) {
            j = 0;
        }
        addToHistogram(iArr, jArr, value, numberOfTimes, j, this.maximumNumberTablesAccessed);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0108, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x010a, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:?, code lost:
        r4 = com.mysql.jdbc.SQLError.createSQLException(r5.toString(), com.mysql.jdbc.SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (com.mysql.jdbc.ExceptionInterceptor) null);
        r4.initCause(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x0119, code lost:
        throw r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x011d, code lost:
        if (r3 != null) goto L_0x011f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x0124, code lost:
        if (r2 != null) goto L_0x0126;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x012c, code lost:
        throw r4;
     */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x010a A[ExcHandler: RuntimeException (r5v0 'ex' java.lang.RuntimeException A[CUSTOM_DECLARE]), PHI: r2 r3 
      PHI: (r2v7 'stmt' java.sql.Statement) = (r2v6 'stmt' java.sql.Statement), (r2v9 'stmt' java.sql.Statement), (r2v9 'stmt' java.sql.Statement), (r2v9 'stmt' java.sql.Statement), (r2v9 'stmt' java.sql.Statement), (r2v9 'stmt' java.sql.Statement), (r2v9 'stmt' java.sql.Statement), (r2v9 'stmt' java.sql.Statement), (r2v9 'stmt' java.sql.Statement) binds: [B:17:0x003c, B:20:0x0051, B:66:0x00f4, B:67:?, B:41:0x00a1, B:49:0x00c8, B:33:0x008d, B:76:0x0107, B:77:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r3v2 'results' java.sql.ResultSet) = (r3v1 'results' java.sql.ResultSet), (r3v1 'results' java.sql.ResultSet), (r3v5 'results' java.sql.ResultSet), (r3v5 'results' java.sql.ResultSet), (r3v4 'results' java.sql.ResultSet), (r3v4 'results' java.sql.ResultSet), (r3v1 'results' java.sql.ResultSet), (r3v1 'results' java.sql.ResultSet), (r3v1 'results' java.sql.ResultSet) binds: [B:17:0x003c, B:20:0x0051, B:66:0x00f4, B:67:?, B:41:0x00a1, B:49:0x00c8, B:33:0x008d, B:76:0x0107, B:77:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:17:0x003c] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void buildCollationMapping() throws java.sql.SQLException {
        /*
            r9 = this;
            r0 = 0
            r1 = 0
            boolean r2 = r9.getCacheServerConfiguration()
            if (r2 == 0) goto L_0x0028
            java.util.Map<java.lang.String, java.util.Map<java.lang.Integer, java.lang.String>> r2 = customIndexToCharsetMapByUrl
            monitor-enter(r2)
            java.lang.String r3 = r9.getURL()     // Catch:{ all -> 0x0025 }
            java.lang.Object r3 = r2.get(r3)     // Catch:{ all -> 0x0025 }
            java.util.Map r3 = (java.util.Map) r3     // Catch:{ all -> 0x0025 }
            r0 = r3
            java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.lang.Integer>> r3 = customCharsetToMblenMapByUrl     // Catch:{ all -> 0x0025 }
            java.lang.String r4 = r9.getURL()     // Catch:{ all -> 0x0025 }
            java.lang.Object r3 = r3.get(r4)     // Catch:{ all -> 0x0025 }
            java.util.Map r3 = (java.util.Map) r3     // Catch:{ all -> 0x0025 }
            r1 = r3
            monitor-exit(r2)     // Catch:{ all -> 0x0025 }
            goto L_0x0028
        L_0x0025:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0025 }
            throw r3
        L_0x0028:
            if (r0 != 0) goto L_0x012d
            boolean r2 = r9.getDetectCustomCollations()
            if (r2 == 0) goto L_0x012d
            r2 = 4
            r3 = 1
            r4 = 0
            boolean r2 = r9.versionMeetsMinimum(r2, r3, r4)
            if (r2 == 0) goto L_0x012d
            r2 = 0
            r3 = 0
            r4 = 0
            java.util.HashMap r5 = new java.util.HashMap     // Catch:{ SQLException -> 0x011a, RuntimeException -> 0x010a }
            r5.<init>()     // Catch:{ SQLException -> 0x011a, RuntimeException -> 0x010a }
            r0 = r5
            java.util.HashMap r5 = new java.util.HashMap     // Catch:{ SQLException -> 0x011a, RuntimeException -> 0x010a }
            r5.<init>()     // Catch:{ SQLException -> 0x011a, RuntimeException -> 0x010a }
            r1 = r5
            java.sql.Statement r5 = r9.getMetadataSafeStatement()     // Catch:{ SQLException -> 0x011a, RuntimeException -> 0x010a }
            r2 = r5
            r5 = 1820(0x71c, float:2.55E-42)
            java.lang.String r6 = "SHOW COLLATION"
            java.sql.ResultSet r6 = r2.executeQuery(r6)     // Catch:{ SQLException -> 0x008c, RuntimeException -> 0x010a }
            r3 = r6
        L_0x0056:
            boolean r6 = r3.next()     // Catch:{ SQLException -> 0x008c, RuntimeException -> 0x010a }
            if (r6 == 0) goto L_0x008b
            r6 = 3
            int r6 = r3.getInt(r6)     // Catch:{ SQLException -> 0x008c, RuntimeException -> 0x010a }
            r7 = 2
            java.lang.String r7 = r3.getString(r7)     // Catch:{ SQLException -> 0x008c, RuntimeException -> 0x010a }
            r8 = 2048(0x800, float:2.87E-42)
            if (r6 >= r8) goto L_0x0078
            java.lang.Integer r8 = java.lang.Integer.valueOf(r6)     // Catch:{ SQLException -> 0x008c, RuntimeException -> 0x010a }
            java.lang.String r8 = com.mysql.jdbc.CharsetMapping.getMysqlCharsetNameForCollationIndex(r8)     // Catch:{ SQLException -> 0x008c, RuntimeException -> 0x010a }
            boolean r8 = r7.equals(r8)     // Catch:{ SQLException -> 0x008c, RuntimeException -> 0x010a }
            if (r8 != 0) goto L_0x007f
        L_0x0078:
            java.lang.Integer r8 = java.lang.Integer.valueOf(r6)     // Catch:{ SQLException -> 0x008c, RuntimeException -> 0x010a }
            r0.put(r8, r7)     // Catch:{ SQLException -> 0x008c, RuntimeException -> 0x010a }
        L_0x007f:
            java.util.Map<java.lang.String, com.mysql.jdbc.MysqlCharset> r8 = com.mysql.jdbc.CharsetMapping.CHARSET_NAME_TO_CHARSET     // Catch:{ SQLException -> 0x008c, RuntimeException -> 0x010a }
            boolean r8 = r8.containsKey(r7)     // Catch:{ SQLException -> 0x008c, RuntimeException -> 0x010a }
            if (r8 != 0) goto L_0x008a
            r1.put(r7, r4)     // Catch:{ SQLException -> 0x008c, RuntimeException -> 0x010a }
        L_0x008a:
            goto L_0x0056
        L_0x008b:
            goto L_0x0099
        L_0x008c:
            r6 = move-exception
            int r7 = r6.getErrorCode()     // Catch:{ SQLException -> 0x011a, RuntimeException -> 0x010a }
            if (r7 != r5) goto L_0x0106
            boolean r7 = r9.getDisconnectOnExpiredPasswords()     // Catch:{ SQLException -> 0x011a, RuntimeException -> 0x010a }
            if (r7 != 0) goto L_0x0106
        L_0x0099:
            int r6 = r1.size()     // Catch:{ SQLException -> 0x011a, RuntimeException -> 0x010a }
            if (r6 <= 0) goto L_0x00d7
            java.lang.String r6 = "SHOW CHARACTER SET"
            java.sql.ResultSet r6 = r2.executeQuery(r6)     // Catch:{ SQLException -> 0x00c7, RuntimeException -> 0x010a }
            r3 = r6
        L_0x00a6:
            boolean r6 = r3.next()     // Catch:{ SQLException -> 0x00c7, RuntimeException -> 0x010a }
            if (r6 == 0) goto L_0x00c6
            java.lang.String r6 = "Charset"
            java.lang.String r6 = r3.getString(r6)     // Catch:{ SQLException -> 0x00c7, RuntimeException -> 0x010a }
            boolean r7 = r1.containsKey(r6)     // Catch:{ SQLException -> 0x00c7, RuntimeException -> 0x010a }
            if (r7 == 0) goto L_0x00c5
            java.lang.String r7 = "Maxlen"
            int r7 = r3.getInt(r7)     // Catch:{ SQLException -> 0x00c7, RuntimeException -> 0x010a }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ SQLException -> 0x00c7, RuntimeException -> 0x010a }
            r1.put(r6, r7)     // Catch:{ SQLException -> 0x00c7, RuntimeException -> 0x010a }
        L_0x00c5:
            goto L_0x00a6
        L_0x00c6:
            goto L_0x00d7
        L_0x00c7:
            r6 = move-exception
            int r7 = r6.getErrorCode()     // Catch:{ SQLException -> 0x011a, RuntimeException -> 0x010a }
            if (r7 != r5) goto L_0x00d5
            boolean r5 = r9.getDisconnectOnExpiredPasswords()     // Catch:{ SQLException -> 0x011a, RuntimeException -> 0x010a }
            if (r5 != 0) goto L_0x00d5
            goto L_0x00d7
        L_0x00d5:
            throw r6     // Catch:{ SQLException -> 0x011a, RuntimeException -> 0x010a }
        L_0x00d7:
            boolean r5 = r9.getCacheServerConfiguration()     // Catch:{ SQLException -> 0x011a, RuntimeException -> 0x010a }
            if (r5 == 0) goto L_0x00f5
            java.util.Map<java.lang.String, java.util.Map<java.lang.Integer, java.lang.String>> r5 = customIndexToCharsetMapByUrl     // Catch:{ SQLException -> 0x011a, RuntimeException -> 0x010a }
            monitor-enter(r5)     // Catch:{ SQLException -> 0x011a, RuntimeException -> 0x010a }
            java.lang.String r6 = r9.getURL()     // Catch:{ all -> 0x00f2 }
            r5.put(r6, r0)     // Catch:{ all -> 0x00f2 }
            java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.lang.Integer>> r6 = customCharsetToMblenMapByUrl     // Catch:{ all -> 0x00f2 }
            java.lang.String r7 = r9.getURL()     // Catch:{ all -> 0x00f2 }
            r6.put(r7, r1)     // Catch:{ all -> 0x00f2 }
            monitor-exit(r5)     // Catch:{ all -> 0x00f2 }
            goto L_0x00f5
        L_0x00f2:
            r6 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x00f2 }
            throw r6     // Catch:{ SQLException -> 0x011a, RuntimeException -> 0x010a }
        L_0x00f5:
            if (r3 == 0) goto L_0x00fd
            r3.close()     // Catch:{ SQLException -> 0x00fc }
            goto L_0x00fd
        L_0x00fc:
            r4 = move-exception
        L_0x00fd:
            if (r2 == 0) goto L_0x0104
            r2.close()     // Catch:{ SQLException -> 0x0103 }
            goto L_0x0104
        L_0x0103:
            r4 = move-exception
        L_0x0104:
            goto L_0x012d
        L_0x0106:
            throw r6     // Catch:{ SQLException -> 0x011a, RuntimeException -> 0x010a }
        L_0x0108:
            r4 = move-exception
            goto L_0x011d
        L_0x010a:
            r5 = move-exception
            java.lang.String r6 = r5.toString()     // Catch:{ all -> 0x0108 }
            java.lang.String r7 = "S1009"
            java.sql.SQLException r4 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r6, (java.lang.String) r7, (com.mysql.jdbc.ExceptionInterceptor) r4)     // Catch:{ all -> 0x0108 }
            r4.initCause(r5)     // Catch:{ all -> 0x0108 }
            throw r4     // Catch:{ all -> 0x0108 }
        L_0x011a:
            r4 = move-exception
            throw r4     // Catch:{ all -> 0x0108 }
        L_0x011d:
            if (r3 == 0) goto L_0x0124
            r3.close()     // Catch:{ SQLException -> 0x0123 }
            goto L_0x0124
        L_0x0123:
            r5 = move-exception
        L_0x0124:
            if (r2 == 0) goto L_0x012b
            r2.close()     // Catch:{ SQLException -> 0x012a }
            goto L_0x012b
        L_0x012a:
            r5 = move-exception
        L_0x012b:
            throw r4
        L_0x012d:
            if (r0 == 0) goto L_0x0135
            java.util.Map r2 = java.util.Collections.unmodifiableMap(r0)
            r9.indexToCustomMysqlCharset = r2
        L_0x0135:
            if (r1 == 0) goto L_0x013d
            java.util.Map r2 = java.util.Collections.unmodifiableMap(r1)
            r9.mysqlCharsetToCustomMblen = r2
        L_0x013d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ConnectionImpl.buildCollationMapping():void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0043, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean canHandleAsServerPreparedStatement(java.lang.String r6) throws java.sql.SQLException {
        /*
            r5 = this;
            if (r6 == 0) goto L_0x004c
            int r0 = r6.length()
            if (r0 != 0) goto L_0x0009
            goto L_0x004c
        L_0x0009:
            boolean r0 = r5.useServerPreparedStmts
            if (r0 != 0) goto L_0x000f
            r0 = 0
            return r0
        L_0x000f:
            boolean r0 = r5.getCachePreparedStatements()
            if (r0 == 0) goto L_0x0047
            com.mysql.jdbc.util.LRUCache<java.lang.String, java.lang.Boolean> r0 = r5.serverSideStatementCheckCache
            monitor-enter(r0)
            com.mysql.jdbc.util.LRUCache<java.lang.String, java.lang.Boolean> r1 = r5.serverSideStatementCheckCache     // Catch:{ all -> 0x0044 }
            java.lang.Object r1 = r1.get(r6)     // Catch:{ all -> 0x0044 }
            java.lang.Boolean r1 = (java.lang.Boolean) r1     // Catch:{ all -> 0x0044 }
            if (r1 == 0) goto L_0x0028
            boolean r2 = r1.booleanValue()     // Catch:{ all -> 0x0044 }
            monitor-exit(r0)     // Catch:{ all -> 0x0044 }
            return r2
        L_0x0028:
            boolean r2 = r5.canHandleAsServerPreparedStatementNoCache(r6)     // Catch:{ all -> 0x0044 }
            int r3 = r6.length()     // Catch:{ all -> 0x0044 }
            int r4 = r5.getPreparedStatementCacheSqlLimit()     // Catch:{ all -> 0x0044 }
            if (r3 >= r4) goto L_0x0042
            com.mysql.jdbc.util.LRUCache<java.lang.String, java.lang.Boolean> r3 = r5.serverSideStatementCheckCache     // Catch:{ all -> 0x0044 }
            if (r2 == 0) goto L_0x003d
            java.lang.Boolean r4 = java.lang.Boolean.TRUE     // Catch:{ all -> 0x0044 }
            goto L_0x003f
        L_0x003d:
            java.lang.Boolean r4 = java.lang.Boolean.FALSE     // Catch:{ all -> 0x0044 }
        L_0x003f:
            r3.put(r6, r4)     // Catch:{ all -> 0x0044 }
        L_0x0042:
            monitor-exit(r0)     // Catch:{ all -> 0x0044 }
            return r2
        L_0x0044:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0044 }
            throw r1
        L_0x0047:
            boolean r0 = r5.canHandleAsServerPreparedStatementNoCache(r6)
            return r0
        L_0x004c:
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ConnectionImpl.canHandleAsServerPreparedStatement(java.lang.String):boolean");
    }

    private boolean canHandleAsServerPreparedStatementNoCache(String sql) throws SQLException {
        String str = sql;
        if (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(str, "CALL")) {
            return false;
        }
        boolean allowBackslashEscapes = !this.noBackslashEscapes;
        String quoteChar = this.useAnsiQuotes ? "\"" : "'";
        if (getAllowMultiQueries()) {
            if (StringUtils.indexOfIgnoreCase(0, sql, ";", quoteChar, quoteChar, allowBackslashEscapes ? StringUtils.SEARCH_MODE__ALL : StringUtils.SEARCH_MODE__MRK_COM_WS) != -1) {
                return false;
            }
            return true;
        } else if (!versionMeetsMinimum(5, 0, 7) && (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(str, "SELECT") || StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(str, "DELETE") || StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(str, "INSERT") || StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(str, "UPDATE") || StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(str, "REPLACE"))) {
            int currentPos = 0;
            int statementLength = sql.length();
            int lastPosToLook = statementLength - 7;
            boolean foundLimitWithPlaceholder = false;
            while (currentPos < lastPosToLook) {
                int limitStart = StringUtils.indexOfIgnoreCase(currentPos, sql, "LIMIT ", quoteChar, quoteChar, allowBackslashEscapes ? StringUtils.SEARCH_MODE__ALL : StringUtils.SEARCH_MODE__MRK_COM_WS);
                if (limitStart != -1) {
                    currentPos = limitStart + 7;
                    while (true) {
                        if (currentPos >= statementLength) {
                            break;
                        }
                        char c = str.charAt(currentPos);
                        if (!Character.isDigit(c) && !Character.isWhitespace(c) && c != ',' && c != '?') {
                            break;
                        } else if (c == '?') {
                            foundLimitWithPlaceholder = true;
                            break;
                        } else {
                            currentPos++;
                        }
                    }
                } else {
                    break;
                }
            }
            return !foundLimitWithPlaceholder;
        } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "XA ") || StringUtils.startsWithIgnoreCaseAndWs(str, "CREATE TABLE") || StringUtils.startsWithIgnoreCaseAndWs(str, "DO") || StringUtils.startsWithIgnoreCaseAndWs(str, "SET")) {
            return false;
        } else {
            if ((!StringUtils.startsWithIgnoreCaseAndWs(str, "SHOW WARNINGS") || !versionMeetsMinimum(5, 7, 2)) && !str.startsWith("/* ping */")) {
                return true;
            }
            return false;
        }
    }

    public void changeUser(String userName, String newPassword) throws SQLException {
        synchronized (getConnectionMutex()) {
            checkClosed();
            if (userName == null || userName.equals("")) {
                userName = "";
            }
            if (newPassword == null) {
                newPassword = "";
            }
            this.sessionMaxRows = -1;
            try {
                this.f4io.changeUser(userName, newPassword, this.database);
                this.user = userName;
                this.password = newPassword;
                if (versionMeetsMinimum(4, 1, 0)) {
                    configureClientCharacterSet(true);
                }
                setSessionVariables();
                setupServerForTruncationChecks();
            } catch (SQLException ex) {
                if (versionMeetsMinimum(5, 6, 13) && SQLError.SQL_STATE_INVALID_AUTH_SPEC.equals(ex.getSQLState())) {
                    cleanup(ex);
                }
                throw ex;
            }
        }
    }

    private boolean characterSetNamesMatches(String mysqlEncodingName) {
        return mysqlEncodingName != null && mysqlEncodingName.equalsIgnoreCase(this.serverVariables.get("character_set_client")) && mysqlEncodingName.equalsIgnoreCase(this.serverVariables.get("character_set_connection"));
    }

    private void checkAndCreatePerformanceHistogram() {
        if (this.perfMetricsHistCounts == null) {
            this.perfMetricsHistCounts = new int[20];
        }
        if (this.perfMetricsHistBreakpoints == null) {
            this.perfMetricsHistBreakpoints = new long[20];
        }
    }

    private void checkAndCreateTablesAccessedHistogram() {
        if (this.numTablesMetricsHistCounts == null) {
            this.numTablesMetricsHistCounts = new int[20];
        }
        if (this.numTablesMetricsHistBreakpoints == null) {
            this.numTablesMetricsHistBreakpoints = new long[20];
        }
    }

    public void checkClosed() throws SQLException {
        if (this.isClosed) {
            throwConnectionClosedException();
        }
    }

    public void throwConnectionClosedException() throws SQLException {
        SQLException ex = SQLError.createSQLException("No operations allowed after connection closed.", SQLError.SQL_STATE_CONNECTION_NOT_OPEN, getExceptionInterceptor());
        Throwable th = this.forceClosedReason;
        if (th != null) {
            ex.initCause(th);
        }
        throw ex;
    }

    private void checkServerEncoding() throws SQLException {
        if (!getUseUnicode() || getEncoding() == null) {
            String serverCharset = this.serverVariables.get("character_set");
            if (serverCharset == null) {
                serverCharset = this.serverVariables.get("character_set_server");
            }
            String mappedServerEncoding = null;
            if (serverCharset != null) {
                try {
                    mappedServerEncoding = CharsetMapping.getJavaEncodingForMysqlCharset(serverCharset);
                } catch (RuntimeException ex) {
                    SQLException sqlEx = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
                    sqlEx.initCause(ex);
                    throw sqlEx;
                }
            }
            if (!getUseUnicode() && mappedServerEncoding != null && getCharsetConverter(mappedServerEncoding) != null) {
                setUseUnicode(true);
                setEncoding(mappedServerEncoding);
            } else if (serverCharset != null) {
                if (mappedServerEncoding == null && Character.isLowerCase(serverCharset.charAt(0))) {
                    char[] ach = serverCharset.toCharArray();
                    ach[0] = Character.toUpperCase(serverCharset.charAt(0));
                    setEncoding(new String(ach));
                }
                if (mappedServerEncoding != null) {
                    try {
                        StringUtils.getBytes("abc", mappedServerEncoding);
                        setEncoding(mappedServerEncoding);
                        setUseUnicode(true);
                    } catch (UnsupportedEncodingException e) {
                        throw SQLError.createSQLException("The driver can not map the character encoding '" + getEncoding() + "' that your server is using " + "to a character encoding your JVM understands. You can specify this mapping manually by adding \"useUnicode=true\" " + "as well as \"characterEncoding=[an_encoding_your_jvm_understands]\" to your JDBC URL.", "0S100", getExceptionInterceptor());
                    }
                } else {
                    throw SQLError.createSQLException("Unknown character encoding on server '" + serverCharset + "', use 'characterEncoding=' property " + " to provide correct mapping", SQLError.SQL_STATE_INVALID_CONNECTION_ATTRIBUTE, getExceptionInterceptor());
                }
            }
        }
    }

    private void checkTransactionIsolationLevel() throws SQLException {
        Integer intTI;
        String s = this.serverVariables.get("transaction_isolation");
        if (s == null) {
            s = this.serverVariables.get("tx_isolation");
        }
        if (s != null && (intTI = mapTransIsolationNameToValue.get(s)) != null) {
            this.isolationLevel = intTI.intValue();
        }
    }

    public void abortInternal() throws SQLException {
        MysqlIO mysqlIO = this.f4io;
        if (mysqlIO != null) {
            try {
                mysqlIO.forceClose();
                this.f4io.releaseResources();
            } catch (Throwable th) {
            }
            this.f4io = null;
        }
        this.isClosed = true;
    }

    private void cleanup(Throwable whyCleanedUp) {
        try {
            if (this.f4io != null) {
                if (isClosed()) {
                    this.f4io.forceClose();
                } else {
                    realClose(false, false, false, whyCleanedUp);
                }
            }
        } catch (SQLException e) {
        }
        this.isClosed = true;
    }

    @Deprecated
    public void clearHasTriedMaster() {
        this.hasTriedMasterFlag = false;
    }

    public void clearWarnings() throws SQLException {
    }

    public java.sql.PreparedStatement clientPrepareStatement(String sql) throws SQLException {
        return clientPrepareStatement(sql, 1003, 1007);
    }

    public java.sql.PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
        java.sql.PreparedStatement pStmt = clientPrepareStatement(sql);
        PreparedStatement preparedStatement = (PreparedStatement) pStmt;
        boolean z = true;
        if (autoGenKeyIndex != 1) {
            z = false;
        }
        preparedStatement.setRetrieveGeneratedKeys(z);
        return pStmt;
    }

    public java.sql.PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return clientPrepareStatement(sql, resultSetType, resultSetConcurrency, true);
    }

    public java.sql.PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, boolean processEscapeCodesIfNeeded) throws SQLException {
        PreparedStatement pStmt;
        checkClosed();
        String nativeSql = (!processEscapeCodesIfNeeded || !getProcessEscapeCodesForPrepStmts()) ? sql : nativeSQL(sql);
        if (getCachePreparedStatements()) {
            PreparedStatement.ParseInfo pStmtInfo = this.cachedPreparedStatementParams.get(nativeSql);
            if (pStmtInfo == null) {
                pStmt = PreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database);
                this.cachedPreparedStatementParams.put(nativeSql, pStmt.getParseInfo());
            } else {
                pStmt = PreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database, pStmtInfo);
            }
        } else {
            pStmt = PreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database);
        }
        pStmt.setResultSetType(resultSetType);
        pStmt.setResultSetConcurrency(resultSetConcurrency);
        return pStmt;
    }

    public java.sql.PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
        PreparedStatement pStmt = (PreparedStatement) clientPrepareStatement(sql);
        pStmt.setRetrieveGeneratedKeys(autoGenKeyIndexes != null && autoGenKeyIndexes.length > 0);
        return pStmt;
    }

    public java.sql.PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
        PreparedStatement pStmt = (PreparedStatement) clientPrepareStatement(sql);
        pStmt.setRetrieveGeneratedKeys(autoGenKeyColNames != null && autoGenKeyColNames.length > 0);
        return pStmt;
    }

    public java.sql.PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return clientPrepareStatement(sql, resultSetType, resultSetConcurrency, true);
    }

    public void close() throws SQLException {
        synchronized (getConnectionMutex()) {
            if (this.connectionLifecycleInterceptors != null) {
                new IterateBlock<Extension>(this.connectionLifecycleInterceptors.iterator()) {
                    /* access modifiers changed from: package-private */
                    public void forEach(Extension each) throws SQLException {
                        ((ConnectionLifecycleInterceptor) each).close();
                    }
                }.doForAll();
            }
            realClose(true, true, false, (Throwable) null);
        }
    }

    private void closeAllOpenStatements() throws SQLException {
        SQLException postponedException = null;
        Iterator i$ = this.openStatements.iterator();
        while (i$.hasNext()) {
            try {
                ((StatementImpl) i$.next()).realClose(false, true);
            } catch (SQLException sqlEx) {
                postponedException = sqlEx;
            }
        }
        if (postponedException != null) {
            throw postponedException;
        }
    }

    private void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
            }
        }
    }

    public void commit() throws SQLException {
        synchronized (getConnectionMutex()) {
            checkClosed();
            try {
                if (this.connectionLifecycleInterceptors != null) {
                    IterateBlock<Extension> iter = new IterateBlock<Extension>(this.connectionLifecycleInterceptors.iterator()) {
                        /* access modifiers changed from: package-private */
                        public void forEach(Extension each) throws SQLException {
                            if (!((ConnectionLifecycleInterceptor) each).commit()) {
                                this.stopIterating = true;
                            }
                        }
                    };
                    iter.doForAll();
                    if (!iter.fullIteration()) {
                        this.needsPing = getReconnectAtTxEnd();
                        return;
                    }
                }
                if (this.autoCommit) {
                    if (!getRelaxAutoCommit()) {
                        throw SQLError.createSQLException("Can't call commit when autocommit=true", getExceptionInterceptor());
                    }
                }
                if (this.transactionsSupported) {
                    if (!getUseLocalTransactionState() || !versionMeetsMinimum(5, 0, 0) || this.f4io.inTransactionOnServer()) {
                        execSQL((StatementImpl) null, "commit", -1, (Buffer) null, 1003, 1007, false, this.database, (Field[]) null, false);
                    } else {
                        this.needsPing = getReconnectAtTxEnd();
                        return;
                    }
                }
                this.needsPing = getReconnectAtTxEnd();
            } catch (SQLException sqlException) {
                if (SQLError.SQL_STATE_COMMUNICATION_LINK_FAILURE.equals(sqlException.getSQLState())) {
                    throw SQLError.createSQLException("Communications link failure during commit(). Transaction resolution unknown.", SQLError.SQL_STATE_TRANSACTION_RESOLUTION_UNKNOWN, getExceptionInterceptor());
                }
                throw sqlException;
            } catch (Throwable th) {
                this.needsPing = getReconnectAtTxEnd();
                throw th;
            }
        }
    }

    private void configureCharsetProperties() throws SQLException {
        if (getEncoding() != null) {
            try {
                StringUtils.getBytes("abc", getEncoding());
            } catch (UnsupportedEncodingException e) {
                String oldEncoding = getEncoding();
                try {
                    setEncoding(CharsetMapping.getJavaEncodingForMysqlCharset(oldEncoding));
                    if (getEncoding() != null) {
                        try {
                            StringUtils.getBytes("abc", getEncoding());
                        } catch (UnsupportedEncodingException e2) {
                            throw SQLError.createSQLException("Unsupported character encoding '" + getEncoding() + "'.", SQLError.SQL_STATE_INVALID_CONNECTION_ATTRIBUTE, getExceptionInterceptor());
                        }
                    } else {
                        throw SQLError.createSQLException("Java does not support the MySQL character encoding '" + oldEncoding + "'.", SQLError.SQL_STATE_INVALID_CONNECTION_ATTRIBUTE, getExceptionInterceptor());
                    }
                } catch (RuntimeException ex) {
                    SQLException sqlEx = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
                    sqlEx.initCause(ex);
                    throw sqlEx;
                }
            }
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
    private boolean configureClientCharacterSet(boolean r45) throws java.sql.SQLException {
        /*
            r44 = this;
            r12 = r44
            java.lang.String r1 = "ISO8859_1"
            java.lang.String r13 = "₩"
            java.lang.String r2 = "Unknown initial character set index '"
            java.lang.String r0 = "com.mysql.jdbc.faultInjection.serverCharsetIndex"
            java.lang.String r14 = "S1009"
            java.lang.String r15 = "¥"
            java.lang.String r3 = ""
            java.lang.String r11 = "UTF-8"
            java.lang.String r4 = r44.getEncoding()
            r5 = 0
            r6 = 4
            r10 = 0
            r9 = 1
            boolean r7 = r12.versionMeetsMinimum(r6, r9, r10)     // Catch:{ all -> 0x0737 }
            java.lang.String r8 = "S1000"
            if (r7 == 0) goto L_0x0687
            r16 = 1
            r12.setUseUnicode(r9)     // Catch:{ all -> 0x0682 }
            r44.configureCharsetProperties()     // Catch:{ all -> 0x0682 }
            java.lang.String r5 = r44.getEncoding()     // Catch:{ all -> 0x0682 }
            r4 = r5
            r5 = r3
            boolean r7 = r44.getUseOldUTF8Behavior()     // Catch:{ all -> 0x0682 }
            if (r7 != 0) goto L_0x0089
            java.lang.String r7 = r44.getConnectionCollation()     // Catch:{ all -> 0x0682 }
            boolean r7 = com.mysql.jdbc.StringUtils.isNullOrEmpty(r7)     // Catch:{ all -> 0x0682 }
            if (r7 != 0) goto L_0x0089
            r7 = 1
        L_0x0044:
            java.lang.String[] r6 = com.mysql.jdbc.CharsetMapping.COLLATION_INDEX_TO_COLLATION_NAME     // Catch:{ all -> 0x0682 }
            int r6 = r6.length     // Catch:{ all -> 0x0682 }
            if (r7 >= r6) goto L_0x0084
            java.lang.String[] r6 = com.mysql.jdbc.CharsetMapping.COLLATION_INDEX_TO_COLLATION_NAME     // Catch:{ all -> 0x0682 }
            r6 = r6[r7]     // Catch:{ all -> 0x0682 }
            java.lang.String r9 = r44.getConnectionCollation()     // Catch:{ all -> 0x0682 }
            boolean r6 = r6.equals(r9)     // Catch:{ all -> 0x0682 }
            if (r6 == 0) goto L_0x007f
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0682 }
            r6.<init>()     // Catch:{ all -> 0x0682 }
            java.lang.String r9 = " COLLATE "
            java.lang.StringBuilder r6 = r6.append(r9)     // Catch:{ all -> 0x0682 }
            java.lang.String[] r9 = com.mysql.jdbc.CharsetMapping.COLLATION_INDEX_TO_COLLATION_NAME     // Catch:{ all -> 0x0682 }
            r9 = r9[r7]     // Catch:{ all -> 0x0682 }
            java.lang.StringBuilder r6 = r6.append(r9)     // Catch:{ all -> 0x0682 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0682 }
            r5 = r6
            com.mysql.jdbc.MysqlCharset[] r6 = com.mysql.jdbc.CharsetMapping.COLLATION_INDEX_TO_CHARSET     // Catch:{ all -> 0x0682 }
            r6 = r6[r7]     // Catch:{ all -> 0x0682 }
            java.lang.String r6 = r6.charsetName     // Catch:{ all -> 0x0682 }
            r3 = r6
            java.lang.Integer r6 = java.lang.Integer.valueOf(r7)     // Catch:{ all -> 0x0682 }
            java.lang.String r6 = com.mysql.jdbc.CharsetMapping.getJavaEncodingForCollationIndex(r6)     // Catch:{ all -> 0x0682 }
            r4 = r6
        L_0x007f:
            int r7 = r7 + 1
            r6 = 4
            r9 = 1
            goto L_0x0044
        L_0x0084:
            r19 = r3
            r9 = r4
            r7 = r5
            goto L_0x008d
        L_0x0089:
            r19 = r3
            r9 = r4
            r7 = r5
        L_0x008d:
            r6 = 0
            java.util.Properties r3 = r12.props     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0133, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            if (r3 == 0) goto L_0x00a6
            java.lang.String r3 = r3.getProperty(r0)     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0133, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            if (r3 == 0) goto L_0x00a6
            com.mysql.jdbc.MysqlIO r3 = r12.f4io     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0133, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            java.util.Properties r4 = r12.props     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0133, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            java.lang.String r0 = r4.getProperty(r0)     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0133, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0133, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            r3.serverCharsetIndex = r0     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0133, SQLException -> 0x0130, RuntimeException -> 0x0122 }
        L_0x00a6:
            com.mysql.jdbc.MysqlIO r0 = r12.f4io     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0133, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            int r0 = r0.serverCharsetIndex     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0133, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0133, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            java.lang.String r0 = com.mysql.jdbc.CharsetMapping.getJavaEncodingForCollationIndex(r0)     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0133, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            if (r0 == 0) goto L_0x00ba
            int r3 = r0.length()     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0133, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            if (r3 != 0) goto L_0x00bf
        L_0x00ba:
            if (r9 == 0) goto L_0x00f5
            r12.setEncoding(r9)     // Catch:{ ArrayIndexOutOfBoundsException -> 0x0133, SQLException -> 0x0130, RuntimeException -> 0x0122 }
        L_0x00bf:
            r3 = 4
            r5 = 1
            boolean r3 = r12.versionMeetsMinimum(r3, r5, r10)     // Catch:{ ArrayIndexOutOfBoundsException -> 0x011a, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            if (r3 == 0) goto L_0x00d0
            boolean r3 = r1.equalsIgnoreCase(r0)     // Catch:{ ArrayIndexOutOfBoundsException -> 0x011a, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            if (r3 == 0) goto L_0x00d0
            java.lang.String r3 = "Cp1252"
            r0 = r3
        L_0x00d0:
            java.lang.String r3 = "UnicodeBig"
            boolean r3 = r3.equalsIgnoreCase(r0)     // Catch:{ ArrayIndexOutOfBoundsException -> 0x011a, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            if (r3 != 0) goto L_0x00f0
            java.lang.String r3 = "UTF-16"
            boolean r3 = r3.equalsIgnoreCase(r0)     // Catch:{ ArrayIndexOutOfBoundsException -> 0x011a, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            if (r3 != 0) goto L_0x00f0
            java.lang.String r3 = "UTF-16LE"
            boolean r3 = r3.equalsIgnoreCase(r0)     // Catch:{ ArrayIndexOutOfBoundsException -> 0x011a, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            if (r3 != 0) goto L_0x00f0
            java.lang.String r3 = "UTF-32"
            boolean r3 = r3.equalsIgnoreCase(r0)     // Catch:{ ArrayIndexOutOfBoundsException -> 0x011a, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            if (r3 == 0) goto L_0x00f1
        L_0x00f0:
            r0 = r11
        L_0x00f1:
            r12.setEncoding(r0)     // Catch:{ ArrayIndexOutOfBoundsException -> 0x011a, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            goto L_0x013a
        L_0x00f5:
            r5 = 1
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ ArrayIndexOutOfBoundsException -> 0x011a, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            r3.<init>()     // Catch:{ ArrayIndexOutOfBoundsException -> 0x011a, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            java.lang.StringBuilder r3 = r3.append(r2)     // Catch:{ ArrayIndexOutOfBoundsException -> 0x011a, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            com.mysql.jdbc.MysqlIO r4 = r12.f4io     // Catch:{ ArrayIndexOutOfBoundsException -> 0x011a, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            int r4 = r4.serverCharsetIndex     // Catch:{ ArrayIndexOutOfBoundsException -> 0x011a, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ ArrayIndexOutOfBoundsException -> 0x011a, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            java.lang.String r4 = "' received from server. Initial client character set can be forced via the 'characterEncoding' property."
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ ArrayIndexOutOfBoundsException -> 0x011a, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            java.lang.String r3 = r3.toString()     // Catch:{ ArrayIndexOutOfBoundsException -> 0x011a, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            com.mysql.jdbc.ExceptionInterceptor r4 = r44.getExceptionInterceptor()     // Catch:{ ArrayIndexOutOfBoundsException -> 0x011a, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            java.sql.SQLException r3 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r3, (java.lang.String) r8, (com.mysql.jdbc.ExceptionInterceptor) r4)     // Catch:{ ArrayIndexOutOfBoundsException -> 0x011a, SQLException -> 0x0130, RuntimeException -> 0x0122 }
            throw r3     // Catch:{ ArrayIndexOutOfBoundsException -> 0x011a, SQLException -> 0x0130, RuntimeException -> 0x0122 }
        L_0x011a:
            r0 = move-exception
            goto L_0x0135
        L_0x011c:
            r0 = move-exception
            r4 = r9
            r5 = r16
            goto L_0x0738
        L_0x0122:
            r0 = move-exception
            java.lang.String r1 = r0.toString()     // Catch:{ all -> 0x011c }
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r14, (com.mysql.jdbc.ExceptionInterceptor) r6)     // Catch:{ all -> 0x011c }
            r1.initCause(r0)     // Catch:{ all -> 0x011c }
            throw r1     // Catch:{ all -> 0x011c }
        L_0x0130:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x011c }
        L_0x0133:
            r0 = move-exception
            r5 = 1
        L_0x0135:
            if (r9 == 0) goto L_0x064f
            r12.setEncoding(r9)     // Catch:{ all -> 0x0646 }
        L_0x013a:
            java.lang.String r0 = r44.getEncoding()     // Catch:{ all -> 0x0646 }
            if (r0 != 0) goto L_0x0144
            r12.setEncoding(r1)     // Catch:{ all -> 0x011c }
        L_0x0144:
            boolean r0 = r44.getUseUnicode()     // Catch:{ all -> 0x0646 }
            java.lang.String r4 = "utf8"
            java.lang.String r1 = "character_set_connection"
            java.lang.String r10 = "character_set_client"
            r18 = r10
            java.lang.String r10 = "latin1"
            if (r0 == 0) goto L_0x0489
            java.lang.String r0 = "SET NAMES "
            if (r9 == 0) goto L_0x0376
            boolean r20 = r9.equalsIgnoreCase(r11)     // Catch:{ all -> 0x036f }
            if (r20 != 0) goto L_0x024c
            java.lang.String r2 = "UTF8"
            boolean r2 = r9.equalsIgnoreCase(r2)     // Catch:{ all -> 0x011c }
            if (r2 == 0) goto L_0x0179
            r32 = r4
            r20 = r5
            r21 = r7
            r33 = r8
            r35 = r10
            r17 = r13
            r10 = r1
            r13 = r11
            r11 = r18
            goto L_0x025c
        L_0x0179:
            int r2 = r7.length()     // Catch:{ all -> 0x011c }
            if (r2 <= 0) goto L_0x0182
            r2 = r19
            goto L_0x018c
        L_0x0182:
            java.util.Locale r2 = java.util.Locale.ENGLISH     // Catch:{ all -> 0x011c }
            java.lang.String r2 = r9.toUpperCase(r2)     // Catch:{ all -> 0x011c }
            java.lang.String r2 = com.mysql.jdbc.CharsetMapping.getMysqlCharsetForJavaEncoding(r2, r12)     // Catch:{ all -> 0x011c }
        L_0x018c:
            if (r2 == 0) goto L_0x0220
            if (r45 != 0) goto L_0x01ac
            boolean r21 = r12.characterSetNamesMatches(r2)     // Catch:{ all -> 0x011c }
            if (r21 != 0) goto L_0x0197
            goto L_0x01ac
        L_0x0197:
            r32 = r4
            r20 = r5
            r21 = r7
            r33 = r8
            r34 = r9
            r35 = r10
            r17 = r13
            r10 = r1
            r1 = r2
            r13 = r11
            r11 = r18
            goto L_0x0233
        L_0x01ac:
            r21 = 0
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0217 }
            r3.<init>()     // Catch:{ all -> 0x0217 }
            java.lang.StringBuilder r0 = r3.append(r0)     // Catch:{ all -> 0x0217 }
            java.lang.StringBuilder r0 = r0.append(r2)     // Catch:{ all -> 0x0217 }
            java.lang.StringBuilder r0 = r0.append(r7)     // Catch:{ all -> 0x0217 }
            java.lang.String r3 = r0.toString()     // Catch:{ all -> 0x0217 }
            r0 = -1
            r23 = 0
            r24 = 1003(0x3eb, float:1.406E-42)
            r25 = 1007(0x3ef, float:1.411E-42)
            r26 = 0
            r27 = r8
            java.lang.String r8 = r12.database     // Catch:{ all -> 0x0217 }
            r28 = 0
            r29 = 0
            r30 = r1
            r1 = r44
            r31 = r2
            r2 = r21
            r32 = r4
            r4 = r0
            r20 = r5
            r5 = r23
            r6 = r24
            r21 = r7
            r7 = r25
            r0 = r8
            r33 = r27
            r8 = r26
            r34 = r9
            r9 = r0
            r35 = r10
            r36 = r18
            r10 = r28
            r17 = r13
            r13 = r11
            r11 = r29
            r1.execSQL(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ all -> 0x0210 }
            java.util.Map<java.lang.String, java.lang.String> r0 = r12.serverVariables     // Catch:{ all -> 0x0210 }
            r1 = r31
            r11 = r36
            r0.put(r11, r1)     // Catch:{ all -> 0x0210 }
            java.util.Map<java.lang.String, java.lang.String> r0 = r12.serverVariables     // Catch:{ all -> 0x0210 }
            r10 = r30
            r0.put(r10, r1)     // Catch:{ all -> 0x0210 }
            goto L_0x0233
        L_0x0210:
            r0 = move-exception
            r5 = r16
            r4 = r34
            goto L_0x0738
        L_0x0217:
            r0 = move-exception
            r34 = r9
            r5 = r16
            r4 = r34
            goto L_0x0738
        L_0x0220:
            r32 = r4
            r20 = r5
            r21 = r7
            r33 = r8
            r34 = r9
            r35 = r10
            r17 = r13
            r10 = r1
            r1 = r2
            r13 = r11
            r11 = r18
        L_0x0233:
            r9 = r34
            r12.setEncoding(r9)     // Catch:{ all -> 0x011c }
            r25 = r9
            r22 = r13
            r23 = r14
            r26 = r21
            r28 = r32
            r13 = r35
            r14 = r10
            r21 = r15
            r15 = r11
            r11 = 1820(0x71c, float:2.55E-42)
            goto L_0x04a1
        L_0x024c:
            r32 = r4
            r20 = r5
            r21 = r7
            r33 = r8
            r35 = r10
            r17 = r13
            r10 = r1
            r13 = r11
            r11 = r18
        L_0x025c:
            r1 = 2
            r8 = 5
            boolean r1 = r12.versionMeetsMinimum(r8, r8, r1)     // Catch:{ all -> 0x036f }
            r18 = r1
            int r1 = r21.length()     // Catch:{ all -> 0x036f }
            if (r1 <= 0) goto L_0x026d
            r4 = r19
            goto L_0x0275
        L_0x026d:
            if (r18 == 0) goto L_0x0273
            java.lang.String r4 = "utf8mb4"
            goto L_0x0275
        L_0x0273:
            r4 = r32
        L_0x0275:
            r7 = r4
            boolean r1 = r44.getUseOldUTF8Behavior()     // Catch:{ all -> 0x036f }
            if (r1 != 0) goto L_0x0327
            if (r45 != 0) goto L_0x02bd
            r6 = r32
            boolean r1 = r12.characterSetNamesMatches(r6)     // Catch:{ all -> 0x011c }
            if (r1 == 0) goto L_0x02bf
            if (r18 == 0) goto L_0x0291
            java.lang.String r1 = "utf8mb4"
            boolean r1 = r12.characterSetNamesMatches(r1)     // Catch:{ all -> 0x011c }
            if (r1 == 0) goto L_0x02bf
        L_0x0291:
            int r1 = r21.length()     // Catch:{ all -> 0x011c }
            if (r1 <= 0) goto L_0x02ac
            java.lang.String r1 = r44.getConnectionCollation()     // Catch:{ all -> 0x011c }
            java.util.Map<java.lang.String, java.lang.String> r2 = r12.serverVariables     // Catch:{ all -> 0x011c }
            java.lang.String r3 = "collation_server"
            java.lang.Object r2 = r2.get(r3)     // Catch:{ all -> 0x011c }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x011c }
            boolean r1 = r1.equalsIgnoreCase(r2)     // Catch:{ all -> 0x011c }
            if (r1 != 0) goto L_0x02ac
            goto L_0x02bf
        L_0x02ac:
            r28 = r6
            r0 = r7
            r37 = r9
            r23 = r14
            r27 = r21
            r14 = r10
            r21 = r15
            r15 = r11
            r11 = r35
            goto L_0x0357
        L_0x02bd:
            r6 = r32
        L_0x02bf:
            r2 = 0
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x031e }
            r1.<init>()     // Catch:{ all -> 0x031e }
            java.lang.StringBuilder r0 = r1.append(r0)     // Catch:{ all -> 0x031e }
            java.lang.StringBuilder r0 = r0.append(r7)     // Catch:{ all -> 0x031e }
            r5 = r21
            java.lang.StringBuilder r0 = r0.append(r5)     // Catch:{ all -> 0x031e }
            java.lang.String r3 = r0.toString()     // Catch:{ all -> 0x031e }
            r4 = -1
            r0 = 0
            r21 = 1003(0x3eb, float:1.406E-42)
            r22 = 1007(0x3ef, float:1.411E-42)
            r23 = 0
            java.lang.String r1 = r12.database     // Catch:{ all -> 0x031e }
            r24 = 0
            r25 = 0
            r26 = r1
            r1 = r44
            r27 = r5
            r5 = r0
            r28 = r6
            r6 = r21
            r0 = r7
            r7 = r22
            r8 = r23
            r37 = r9
            r9 = r26
            r38 = r10
            r10 = r24
            r21 = r15
            r15 = r11
            r11 = r25
            r1.execSQL(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ all -> 0x0317 }
            java.util.Map<java.lang.String, java.lang.String> r1 = r12.serverVariables     // Catch:{ all -> 0x0317 }
            r1.put(r15, r0)     // Catch:{ all -> 0x0317 }
            java.util.Map<java.lang.String, java.lang.String> r1 = r12.serverVariables     // Catch:{ all -> 0x0317 }
            r11 = r38
            r1.put(r11, r0)     // Catch:{ all -> 0x0317 }
            r23 = r14
            r14 = r11
            r11 = r35
            goto L_0x0357
        L_0x0317:
            r0 = move-exception
            r5 = r16
            r4 = r37
            goto L_0x0738
        L_0x031e:
            r0 = move-exception
            r37 = r9
            r5 = r16
            r4 = r37
            goto L_0x0738
        L_0x0327:
            r0 = r7
            r37 = r9
            r27 = r21
            r28 = r32
            r21 = r15
            r15 = r11
            r11 = r10
            r2 = 0
            java.lang.String r3 = "SET NAMES latin1"
            r4 = -1
            r5 = 0
            r6 = 1003(0x3eb, float:1.406E-42)
            r7 = 1007(0x3ef, float:1.411E-42)
            r8 = 0
            java.lang.String r9 = r12.database     // Catch:{ all -> 0x0367 }
            r10 = 0
            r22 = 0
            r1 = r44
            r23 = r14
            r14 = r11
            r11 = r22
            r1.execSQL(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ all -> 0x0367 }
            java.util.Map<java.lang.String, java.lang.String> r1 = r12.serverVariables     // Catch:{ all -> 0x0367 }
            r11 = r35
            r1.put(r15, r11)     // Catch:{ all -> 0x0367 }
            java.util.Map<java.lang.String, java.lang.String> r1 = r12.serverVariables     // Catch:{ all -> 0x0367 }
            r1.put(r14, r11)     // Catch:{ all -> 0x0367 }
        L_0x0357:
            r10 = r37
            r12.setEncoding(r10)     // Catch:{ all -> 0x03cf }
            r25 = r10
            r22 = r13
            r26 = r27
            r13 = r11
            r11 = 1820(0x71c, float:2.55E-42)
            goto L_0x04a1
        L_0x0367:
            r0 = move-exception
            r10 = r37
            r4 = r10
            r5 = r16
            goto L_0x0738
        L_0x036f:
            r0 = move-exception
            r10 = r9
            r4 = r10
            r5 = r16
            goto L_0x0738
        L_0x0376:
            r28 = r4
            r20 = r5
            r27 = r7
            r33 = r8
            r17 = r13
            r23 = r14
            r21 = r15
            r15 = r18
            r14 = r1
            r13 = r11
            r11 = r10
            r10 = r9
            java.lang.String r1 = r44.getEncoding()     // Catch:{ all -> 0x0480 }
            if (r1 == 0) goto L_0x0476
            int r1 = r27.length()     // Catch:{ all -> 0x0480 }
            if (r1 <= 0) goto L_0x0399
            r1 = r19
            goto L_0x03a5
        L_0x0399:
            boolean r1 = r44.getUseOldUTF8Behavior()     // Catch:{ all -> 0x0480 }
            if (r1 == 0) goto L_0x03a1
            r1 = r11
            goto L_0x03a5
        L_0x03a1:
            java.lang.String r1 = r44.getServerCharset()     // Catch:{ all -> 0x0480 }
        L_0x03a5:
            r2 = 0
            java.lang.String r3 = "ucs2"
            boolean r3 = r3.equalsIgnoreCase(r1)     // Catch:{ all -> 0x0480 }
            if (r3 != 0) goto L_0x03d5
            java.lang.String r3 = "utf16"
            boolean r3 = r3.equalsIgnoreCase(r1)     // Catch:{ all -> 0x03cf }
            if (r3 != 0) goto L_0x03d5
            java.lang.String r3 = "utf16le"
            boolean r3 = r3.equalsIgnoreCase(r1)     // Catch:{ all -> 0x03cf }
            if (r3 != 0) goto L_0x03d5
            java.lang.String r3 = "utf32"
            boolean r3 = r3.equalsIgnoreCase(r1)     // Catch:{ all -> 0x03cf }
            if (r3 == 0) goto L_0x03cb
            goto L_0x03d5
        L_0x03cb:
            r9 = r1
            r18 = r2
            goto L_0x03e4
        L_0x03cf:
            r0 = move-exception
            r4 = r10
            r5 = r16
            goto L_0x0738
        L_0x03d5:
            r1 = r28
            r2 = 1
            java.lang.String r3 = r44.getCharacterSetResults()     // Catch:{ all -> 0x0480 }
            if (r3 != 0) goto L_0x03e1
            r12.setCharacterSetResults(r13)     // Catch:{ all -> 0x03cf }
        L_0x03e1:
            r9 = r1
            r18 = r2
        L_0x03e4:
            if (r45 != 0) goto L_0x03fb
            boolean r1 = r12.characterSetNamesMatches(r9)     // Catch:{ all -> 0x03cf }
            if (r1 == 0) goto L_0x03fb
            if (r18 == 0) goto L_0x03ef
            goto L_0x03fb
        L_0x03ef:
            r1 = r9
            r25 = r10
            r22 = r13
            r26 = r27
            r13 = r11
            r11 = 1820(0x71c, float:2.55E-42)
            goto L_0x046d
        L_0x03fb:
            r2 = 0
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ SQLException -> 0x0456 }
            r1.<init>()     // Catch:{ SQLException -> 0x0456 }
            java.lang.StringBuilder r0 = r1.append(r0)     // Catch:{ SQLException -> 0x0456 }
            java.lang.StringBuilder r0 = r0.append(r9)     // Catch:{ SQLException -> 0x0456 }
            r8 = r27
            java.lang.StringBuilder r0 = r0.append(r8)     // Catch:{ SQLException -> 0x044c }
            java.lang.String r3 = r0.toString()     // Catch:{ SQLException -> 0x044c }
            r4 = -1
            r5 = 0
            r6 = 1003(0x3eb, float:1.406E-42)
            r7 = 1007(0x3ef, float:1.411E-42)
            r0 = 0
            java.lang.String r1 = r12.database     // Catch:{ SQLException -> 0x044c }
            r22 = 0
            r24 = 0
            r25 = r1
            r1 = r44
            r26 = r8
            r8 = r0
            r39 = r9
            r9 = r25
            r25 = r10
            r10 = r22
            r22 = r13
            r13 = r11
            r11 = r24
            r1.execSQL(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ SQLException -> 0x0448 }
            java.util.Map<java.lang.String, java.lang.String> r0 = r12.serverVariables     // Catch:{ SQLException -> 0x0448 }
            r1 = r39
            r0.put(r15, r1)     // Catch:{ SQLException -> 0x0446 }
            java.util.Map<java.lang.String, java.lang.String> r0 = r12.serverVariables     // Catch:{ SQLException -> 0x0446 }
            r0.put(r14, r1)     // Catch:{ SQLException -> 0x0446 }
            r11 = 1820(0x71c, float:2.55E-42)
            goto L_0x046d
        L_0x0446:
            r0 = move-exception
            goto L_0x045f
        L_0x0448:
            r0 = move-exception
            r1 = r39
            goto L_0x045f
        L_0x044c:
            r0 = move-exception
            r26 = r8
            r1 = r9
            r25 = r10
            r22 = r13
            r13 = r11
            goto L_0x045f
        L_0x0456:
            r0 = move-exception
            r1 = r9
            r25 = r10
            r22 = r13
            r26 = r27
            r13 = r11
        L_0x045f:
            int r2 = r0.getErrorCode()     // Catch:{ all -> 0x067b }
            r11 = 1820(0x71c, float:2.55E-42)
            if (r2 != r11) goto L_0x0474
            boolean r2 = r44.getDisconnectOnExpiredPasswords()     // Catch:{ all -> 0x067b }
            if (r2 != 0) goto L_0x0474
        L_0x046d:
            java.lang.String r0 = r44.getEncoding()     // Catch:{ all -> 0x067b }
            r25 = r0
            goto L_0x04a1
        L_0x0474:
            throw r0     // Catch:{ all -> 0x067b }
        L_0x0476:
            r25 = r10
            r22 = r13
            r26 = r27
            r13 = r11
            r11 = 1820(0x71c, float:2.55E-42)
            goto L_0x04a1
        L_0x0480:
            r0 = move-exception
            r25 = r10
            r5 = r16
            r4 = r25
            goto L_0x0738
        L_0x0489:
            r28 = r4
            r20 = r5
            r26 = r7
            r33 = r8
            r25 = r9
            r22 = r11
            r17 = r13
            r23 = r14
            r21 = r15
            r15 = r18
            r11 = 1820(0x71c, float:2.55E-42)
            r14 = r1
            r13 = r10
        L_0x04a1:
            r0 = 0
            r1 = 0
            java.util.Map<java.lang.String, java.lang.String> r2 = r12.serverVariables     // Catch:{ all -> 0x067b }
            if (r2 == 0) goto L_0x04ca
            java.lang.String r3 = "character_set_results"
            java.lang.Object r2 = r2.get(r3)     // Catch:{ all -> 0x067b }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x067b }
            r0 = r2
            if (r0 == 0) goto L_0x04c3
            java.lang.String r2 = "NULL"
            boolean r2 = r2.equalsIgnoreCase(r0)     // Catch:{ all -> 0x067b }
            if (r2 != 0) goto L_0x04c3
            int r2 = r0.length()     // Catch:{ all -> 0x067b }
            if (r2 != 0) goto L_0x04c1
            goto L_0x04c3
        L_0x04c1:
            r10 = 0
            goto L_0x04c5
        L_0x04c3:
            r10 = r20
        L_0x04c5:
            r1 = r10
            r10 = r0
            r18 = r1
            goto L_0x04cd
        L_0x04ca:
            r10 = r0
            r18 = r1
        L_0x04cd:
            java.lang.String r0 = r44.getCharacterSetResults()     // Catch:{ all -> 0x067b }
            java.lang.String r9 = "jdbc.local.character_set_results"
            if (r0 != 0) goto L_0x051d
            if (r18 != 0) goto L_0x0510
            r2 = 0
            java.lang.String r3 = "SET character_set_results = NULL"
            r4 = -1
            r5 = 0
            r6 = 1003(0x3eb, float:1.406E-42)
            r7 = 1007(0x3ef, float:1.411E-42)
            r8 = 0
            java.lang.String r0 = r12.database     // Catch:{ SQLException -> 0x04f4 }
            r13 = 0
            r14 = 0
            r1 = r44
            r15 = r9
            r9 = r0
            r40 = r10
            r10 = r13
            r13 = r11
            r11 = r14
            r1.execSQL(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ SQLException -> 0x04f2 }
            goto L_0x0505
        L_0x04f2:
            r0 = move-exception
            goto L_0x04f9
        L_0x04f4:
            r0 = move-exception
            r15 = r9
            r40 = r10
            r13 = r11
        L_0x04f9:
            int r1 = r0.getErrorCode()     // Catch:{ all -> 0x067b }
            if (r1 != r13) goto L_0x050e
            boolean r1 = r44.getDisconnectOnExpiredPasswords()     // Catch:{ all -> 0x067b }
            if (r1 != 0) goto L_0x050e
        L_0x0505:
            java.util.Map<java.lang.String, java.lang.String> r0 = r12.serverVariables     // Catch:{ all -> 0x067b }
            r1 = 0
            r0.put(r15, r1)     // Catch:{ all -> 0x067b }
            r2 = 0
            goto L_0x0618
        L_0x050e:
            throw r0     // Catch:{ all -> 0x067b }
        L_0x0510:
            r15 = r9
            r40 = r10
            java.util.Map<java.lang.String, java.lang.String> r0 = r12.serverVariables     // Catch:{ all -> 0x067b }
            r11 = r40
            r0.put(r15, r11)     // Catch:{ all -> 0x067b }
            r2 = 0
            goto L_0x0618
        L_0x051d:
            r43 = r11
            r11 = r10
            r10 = r43
            boolean r0 = r44.getUseOldUTF8Behavior()     // Catch:{ all -> 0x067b }
            if (r0 == 0) goto L_0x056b
            r2 = 0
            java.lang.String r3 = "SET NAMES latin1"
            r4 = -1
            r5 = 0
            r6 = 1003(0x3eb, float:1.406E-42)
            r7 = 1007(0x3ef, float:1.411E-42)
            r8 = 0
            java.lang.String r0 = r12.database     // Catch:{ SQLException -> 0x0555 }
            r24 = 0
            r27 = 0
            r1 = r44
            r41 = r9
            r9 = r0
            r10 = r24
            r42 = r11
            r11 = r27
            r1.execSQL(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ SQLException -> 0x0553 }
            java.util.Map<java.lang.String, java.lang.String> r0 = r12.serverVariables     // Catch:{ SQLException -> 0x0553 }
            r0.put(r15, r13)     // Catch:{ SQLException -> 0x0553 }
            java.util.Map<java.lang.String, java.lang.String> r0 = r12.serverVariables     // Catch:{ SQLException -> 0x0553 }
            r0.put(r14, r13)     // Catch:{ SQLException -> 0x0553 }
            r13 = 1820(0x71c, float:2.55E-42)
            goto L_0x0570
        L_0x0553:
            r0 = move-exception
            goto L_0x055a
        L_0x0555:
            r0 = move-exception
            r41 = r9
            r42 = r11
        L_0x055a:
            int r1 = r0.getErrorCode()     // Catch:{ all -> 0x067b }
            r13 = 1820(0x71c, float:2.55E-42)
            if (r1 != r13) goto L_0x0569
            boolean r1 = r44.getDisconnectOnExpiredPasswords()     // Catch:{ all -> 0x067b }
            if (r1 != 0) goto L_0x0569
            goto L_0x0570
        L_0x0569:
            throw r0     // Catch:{ all -> 0x067b }
        L_0x056b:
            r41 = r9
            r13 = r10
            r42 = r11
        L_0x0570:
            java.lang.String r0 = r44.getCharacterSetResults()     // Catch:{ all -> 0x067b }
            r14 = r0
            r0 = 0
            r1 = r22
            boolean r1 = r1.equalsIgnoreCase(r14)     // Catch:{ all -> 0x067b }
            if (r1 != 0) goto L_0x05a1
            java.lang.String r1 = "UTF8"
            boolean r1 = r1.equalsIgnoreCase(r14)     // Catch:{ all -> 0x067b }
            if (r1 == 0) goto L_0x0587
            goto L_0x05a1
        L_0x0587:
            java.lang.String r1 = "null"
            boolean r1 = r1.equalsIgnoreCase(r14)     // Catch:{ all -> 0x067b }
            if (r1 == 0) goto L_0x0594
            java.lang.String r1 = "NULL"
            r0 = r1
            r15 = r0
            goto L_0x05a4
        L_0x0594:
            java.util.Locale r1 = java.util.Locale.ENGLISH     // Catch:{ all -> 0x067b }
            java.lang.String r1 = r14.toUpperCase(r1)     // Catch:{ all -> 0x067b }
            java.lang.String r1 = com.mysql.jdbc.CharsetMapping.getMysqlCharsetForJavaEncoding(r1, r12)     // Catch:{ all -> 0x067b }
            r0 = r1
            r15 = r0
            goto L_0x05a4
        L_0x05a1:
            r0 = r28
            r15 = r0
        L_0x05a4:
            if (r15 == 0) goto L_0x0620
            java.util.Map<java.lang.String, java.lang.String> r0 = r12.serverVariables     // Catch:{ all -> 0x067b }
            java.lang.String r1 = "character_set_results"
            java.lang.Object r0 = r0.get(r1)     // Catch:{ all -> 0x067b }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ all -> 0x067b }
            boolean r0 = r15.equalsIgnoreCase(r0)     // Catch:{ all -> 0x067b }
            if (r0 != 0) goto L_0x060e
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x067b }
            java.lang.String r1 = "SET character_set_results = "
            int r1 = r1.length()     // Catch:{ all -> 0x067b }
            int r2 = r15.length()     // Catch:{ all -> 0x067b }
            int r1 = r1 + r2
            r0.<init>(r1)     // Catch:{ all -> 0x067b }
            r11 = r0
            java.lang.String r0 = "SET character_set_results = "
            java.lang.StringBuilder r0 = r11.append(r0)     // Catch:{ all -> 0x067b }
            r0.append(r15)     // Catch:{ all -> 0x067b }
            r2 = 0
            java.lang.String r3 = r11.toString()     // Catch:{ SQLException -> 0x05eb }
            r4 = -1
            r5 = 0
            r6 = 1003(0x3eb, float:1.406E-42)
            r7 = 1007(0x3ef, float:1.411E-42)
            r8 = 0
            java.lang.String r9 = r12.database     // Catch:{ SQLException -> 0x05eb }
            r10 = 0
            r0 = 0
            r1 = r44
            r22 = r11
            r11 = r0
            r1.execSQL(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ SQLException -> 0x05e9 }
            goto L_0x05fa
        L_0x05e9:
            r0 = move-exception
            goto L_0x05ee
        L_0x05eb:
            r0 = move-exception
            r22 = r11
        L_0x05ee:
            int r1 = r0.getErrorCode()     // Catch:{ all -> 0x067b }
            if (r1 != r13) goto L_0x060c
            boolean r1 = r44.getDisconnectOnExpiredPasswords()     // Catch:{ all -> 0x067b }
            if (r1 != 0) goto L_0x060c
        L_0x05fa:
            java.util.Map<java.lang.String, java.lang.String> r0 = r12.serverVariables     // Catch:{ all -> 0x067b }
            r1 = r41
            r0.put(r1, r15)     // Catch:{ all -> 0x067b }
            r1 = 5
            r2 = 0
            boolean r0 = r12.versionMeetsMinimum(r1, r1, r2)     // Catch:{ all -> 0x067b }
            if (r0 == 0) goto L_0x060b
            r12.errorMessageEncoding = r14     // Catch:{ all -> 0x067b }
        L_0x060b:
            goto L_0x0618
        L_0x060c:
            throw r0     // Catch:{ all -> 0x067b }
        L_0x060e:
            r1 = r41
            r2 = 0
            java.util.Map<java.lang.String, java.lang.String> r0 = r12.serverVariables     // Catch:{ all -> 0x067b }
            r3 = r42
            r0.put(r1, r3)     // Catch:{ all -> 0x067b }
        L_0x0618:
            r5 = r16
            r1 = r25
            r3 = r33
            goto L_0x0697
        L_0x0620:
            r3 = r42
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x067b }
            r0.<init>()     // Catch:{ all -> 0x067b }
            java.lang.String r1 = "Can't map "
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ all -> 0x067b }
            java.lang.StringBuilder r0 = r0.append(r14)     // Catch:{ all -> 0x067b }
            java.lang.String r1 = " given for characterSetResults to a supported MySQL encoding."
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ all -> 0x067b }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x067b }
            com.mysql.jdbc.ExceptionInterceptor r1 = r44.getExceptionInterceptor()     // Catch:{ all -> 0x067b }
            r2 = r23
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r0, (java.lang.String) r2, (com.mysql.jdbc.ExceptionInterceptor) r1)     // Catch:{ all -> 0x067b }
            throw r0     // Catch:{ all -> 0x067b }
        L_0x0646:
            r0 = move-exception
            r25 = r9
            r5 = r16
            r4 = r25
            goto L_0x0738
        L_0x064f:
            r26 = r7
            r33 = r8
            r25 = r9
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x067b }
            r1.<init>()     // Catch:{ all -> 0x067b }
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x067b }
            com.mysql.jdbc.MysqlIO r2 = r12.f4io     // Catch:{ all -> 0x067b }
            int r2 = r2.serverCharsetIndex     // Catch:{ all -> 0x067b }
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x067b }
            java.lang.String r2 = "' received from server. Initial client character set can be forced via the 'characterEncoding' property."
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x067b }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x067b }
            com.mysql.jdbc.ExceptionInterceptor r2 = r44.getExceptionInterceptor()     // Catch:{ all -> 0x067b }
            r3 = r33
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r3, (com.mysql.jdbc.ExceptionInterceptor) r2)     // Catch:{ all -> 0x067b }
            throw r1     // Catch:{ all -> 0x067b }
        L_0x067b:
            r0 = move-exception
            r5 = r16
            r4 = r25
            goto L_0x0738
        L_0x0682:
            r0 = move-exception
            r5 = r16
            goto L_0x0738
        L_0x0687:
            r3 = r8
            r20 = r9
            r2 = r10
            r17 = r13
            r21 = r15
            java.lang.String r0 = r44.getEncoding()     // Catch:{ all -> 0x0737 }
            r25 = r0
            r1 = r25
        L_0x0697:
            r12.setEncoding(r1)
            r4 = 92
            java.lang.String r0 = r44.getEncoding()     // Catch:{ UnsupportedCharsetException -> 0x06ee }
            java.nio.charset.Charset r0 = java.nio.charset.Charset.forName(r0)     // Catch:{ UnsupportedCharsetException -> 0x06ee }
            java.nio.charset.CharsetEncoder r0 = r0.newEncoder()     // Catch:{ UnsupportedCharsetException -> 0x06ee }
            java.nio.CharBuffer r6 = java.nio.CharBuffer.allocate(r20)     // Catch:{ UnsupportedCharsetException -> 0x06ee }
            java.nio.ByteBuffer r7 = java.nio.ByteBuffer.allocate(r20)     // Catch:{ UnsupportedCharsetException -> 0x06ee }
            r8 = r21
            r6.put(r8)     // Catch:{ UnsupportedCharsetException -> 0x06e8 }
            r6.position(r2)     // Catch:{ UnsupportedCharsetException -> 0x06e8 }
            r9 = r20
            r0.encode(r6, r7, r9)     // Catch:{ UnsupportedCharsetException -> 0x06e4 }
            byte r10 = r7.get(r2)     // Catch:{ UnsupportedCharsetException -> 0x06e4 }
            if (r10 != r4) goto L_0x06c8
            r12.requiresEscapingEncoder = r9     // Catch:{ UnsupportedCharsetException -> 0x06e4 }
            goto L_0x06e1
        L_0x06c8:
            r6.clear()     // Catch:{ UnsupportedCharsetException -> 0x06e4 }
            r7.clear()     // Catch:{ UnsupportedCharsetException -> 0x06e4 }
            r10 = r17
            r6.put(r10)     // Catch:{ UnsupportedCharsetException -> 0x06e2 }
            r6.position(r2)     // Catch:{ UnsupportedCharsetException -> 0x06e2 }
            r0.encode(r6, r7, r9)     // Catch:{ UnsupportedCharsetException -> 0x06e2 }
            byte r11 = r7.get(r2)     // Catch:{ UnsupportedCharsetException -> 0x06e2 }
            if (r11 != r4) goto L_0x06e1
            r12.requiresEscapingEncoder = r9     // Catch:{ UnsupportedCharsetException -> 0x06e2 }
        L_0x06e1:
            goto L_0x0715
        L_0x06e2:
            r0 = move-exception
            goto L_0x06f5
        L_0x06e4:
            r0 = move-exception
            r10 = r17
            goto L_0x06f5
        L_0x06e8:
            r0 = move-exception
            r10 = r17
            r9 = r20
            goto L_0x06f5
        L_0x06ee:
            r0 = move-exception
            r10 = r17
            r9 = r20
            r8 = r21
        L_0x06f5:
            r6 = r0
            java.lang.String r0 = r44.getEncoding()     // Catch:{ UnsupportedEncodingException -> 0x0716 }
            byte[] r0 = com.mysql.jdbc.StringUtils.getBytes((java.lang.String) r8, (java.lang.String) r0)     // Catch:{ UnsupportedEncodingException -> 0x0716 }
            byte r7 = r0[r2]     // Catch:{ UnsupportedEncodingException -> 0x0716 }
            if (r7 != r4) goto L_0x0705
            r12.requiresEscapingEncoder = r9     // Catch:{ UnsupportedEncodingException -> 0x0716 }
            goto L_0x0714
        L_0x0705:
            java.lang.String r7 = r44.getEncoding()     // Catch:{ UnsupportedEncodingException -> 0x0716 }
            byte[] r7 = com.mysql.jdbc.StringUtils.getBytes((java.lang.String) r10, (java.lang.String) r7)     // Catch:{ UnsupportedEncodingException -> 0x0716 }
            r0 = r7
            byte r2 = r0[r2]     // Catch:{ UnsupportedEncodingException -> 0x0716 }
            if (r2 != r4) goto L_0x0714
            r12.requiresEscapingEncoder = r9     // Catch:{ UnsupportedEncodingException -> 0x0716 }
        L_0x0714:
        L_0x0715:
            return r5
        L_0x0716:
            r0 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "Unable to use encoding: "
            java.lang.StringBuilder r2 = r2.append(r4)
            java.lang.String r4 = r44.getEncoding()
            java.lang.StringBuilder r2 = r2.append(r4)
            java.lang.String r2 = r2.toString()
            com.mysql.jdbc.ExceptionInterceptor r4 = r44.getExceptionInterceptor()
            java.sql.SQLException r2 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r2, (java.lang.String) r3, (java.lang.Throwable) r0, (com.mysql.jdbc.ExceptionInterceptor) r4)
            throw r2
        L_0x0737:
            r0 = move-exception
        L_0x0738:
            r12.setEncoding(r4)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ConnectionImpl.configureClientCharacterSet(boolean):boolean");
    }

    private void configureTimezone() throws SQLException {
        String configuredTimeZoneOnServer = this.serverVariables.get("timezone");
        if (configuredTimeZoneOnServer == null) {
            configuredTimeZoneOnServer = this.serverVariables.get("time_zone");
            if ("SYSTEM".equalsIgnoreCase(configuredTimeZoneOnServer)) {
                configuredTimeZoneOnServer = this.serverVariables.get("system_time_zone");
            }
        }
        String canonicalTimezone = getServerTimezone();
        if ((getUseTimezone() || !getUseLegacyDatetimeCode()) && configuredTimeZoneOnServer != null && (canonicalTimezone == null || StringUtils.isEmptyOrWhitespaceOnly(canonicalTimezone))) {
            try {
                canonicalTimezone = TimeUtil.getCanonicalTimezone(configuredTimeZoneOnServer, getExceptionInterceptor());
            } catch (IllegalArgumentException iae) {
                throw SQLError.createSQLException(iae.getMessage(), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
            }
        }
        if (canonicalTimezone != null && canonicalTimezone.length() > 0) {
            this.serverTimezoneTZ = TimeZone.getTimeZone(canonicalTimezone);
            if (canonicalTimezone.equalsIgnoreCase("GMT") || !this.serverTimezoneTZ.getID().equals("GMT")) {
                this.isServerTzUTC = !this.serverTimezoneTZ.useDaylightTime() && this.serverTimezoneTZ.getRawOffset() == 0;
                return;
            }
            throw SQLError.createSQLException("No timezone mapping entry for '" + canonicalTimezone + "'", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
    }

    private void createInitialHistogram(long[] breakpoints, long lowerBound, long upperBound) {
        double bucketSize = ((((double) upperBound) - ((double) lowerBound)) / 20.0d) * 1.25d;
        if (bucketSize < 1.0d) {
            bucketSize = 1.0d;
        }
        for (int i = 0; i < 20; i++) {
            breakpoints[i] = lowerBound;
            lowerBound = (long) (((double) lowerBound) + bucketSize);
        }
    }

    public void createNewIO(boolean isForReconnect) throws SQLException {
        synchronized (getConnectionMutex()) {
            Properties mergedProps = exposeAsProperties(this.props);
            if (!getHighAvailability()) {
                connectOneTryOnly(isForReconnect, mergedProps);
            } else {
                connectWithRetries(isForReconnect, mergedProps);
            }
        }
    }

    private void connectWithRetries(boolean z, Properties properties) throws SQLException {
        boolean z2;
        boolean autoCommit2;
        int i;
        boolean isReadOnly;
        String catalog;
        double initialTimeout = (double) getInitialTimeout();
        Stack stack = null;
        Exception exc = null;
        int i2 = 0;
        while (true) {
            if (i2 >= getMaxReconnects()) {
                z2 = false;
                break;
            }
            try {
                MysqlIO mysqlIO = this.f4io;
                if (mysqlIO != null) {
                    mysqlIO.forceClose();
                }
                try {
                    coreConnect(properties);
                    pingInternal(false, 0);
                    synchronized (getConnectionMutex()) {
                        this.connectionId = this.f4io.getThreadId();
                        this.isClosed = false;
                        autoCommit2 = getAutoCommit();
                        i = this.isolationLevel;
                        isReadOnly = isReadOnly(false);
                        catalog = getCatalog();
                        this.f4io.setStatementInterceptors(this.statementInterceptors);
                    }
                    initializePropsFromServer();
                    if (z) {
                        setAutoCommit(autoCommit2);
                        if (this.hasIsolationLevels) {
                            setTransactionIsolation(i);
                        }
                        setCatalog(catalog);
                        setReadOnly(isReadOnly);
                    }
                    z2 = true;
                } catch (Exception e) {
                    e = e;
                }
            } catch (Exception e2) {
                e = e2;
                Properties properties2 = properties;
                Exception exc2 = e;
                if (i2 > 0) {
                    try {
                        Thread.sleep(((long) initialTimeout) * 1000);
                    } catch (InterruptedException e3) {
                    }
                }
                i2++;
                exc = exc2;
            }
            i2++;
            exc = exc2;
        }
        if (z2) {
            if (getParanoid() && !getHighAvailability()) {
                this.password = null;
                this.user = null;
            }
            if (z) {
                Iterator<Statement> it = this.openStatements.iterator();
                while (it.hasNext()) {
                    Statement next = it.next();
                    if (next instanceof ServerPreparedStatement) {
                        if (stack == null) {
                            stack = new Stack();
                        }
                        stack.add(next);
                    }
                }
                if (stack != null) {
                    while (!stack.isEmpty()) {
                        ((ServerPreparedStatement) stack.pop()).rePrepare();
                    }
                    return;
                }
                return;
            }
            return;
        }
        SQLException createSQLException = SQLError.createSQLException(Messages.getString("Connection.UnableToConnectWithRetries", new Object[]{Integer.valueOf(getMaxReconnects())}), SQLError.SQL_STATE_UNABLE_TO_CONNECT_TO_DATASOURCE, getExceptionInterceptor());
        createSQLException.initCause(exc);
        throw createSQLException;
    }

    private void coreConnect(Properties mergedProps) throws SQLException, IOException {
        int newPort = 3306;
        String newHost = "localhost";
        String protocol = mergedProps.getProperty(NonRegisteringDriver.PROTOCOL_PROPERTY_KEY);
        if (protocol == null) {
            String[] parsedHostPortPair = NonRegisteringDriver.parseHostPortPair(this.hostPortPair);
            newHost = normalizeHost(parsedHostPortPair[0]);
            if (parsedHostPortPair[1] != null) {
                newPort = parsePortNumber(parsedHostPortPair[1]);
            }
        } else if ("tcp".equalsIgnoreCase(protocol)) {
            newHost = normalizeHost(mergedProps.getProperty(NonRegisteringDriver.HOST_PROPERTY_KEY));
            newPort = parsePortNumber(mergedProps.getProperty(NonRegisteringDriver.PORT_PROPERTY_KEY, BuildConfig.DB_PORT));
        } else if ("pipe".equalsIgnoreCase(protocol)) {
            setSocketFactoryClassName(NamedPipeSocketFactory.class.getName());
            String path = mergedProps.getProperty(NonRegisteringDriver.PATH_PROPERTY_KEY);
            if (path != null) {
                mergedProps.setProperty(NamedPipeSocketFactory.NAMED_PIPE_PROP_NAME, path);
            }
        } else {
            newHost = normalizeHost(mergedProps.getProperty(NonRegisteringDriver.HOST_PROPERTY_KEY));
            newPort = parsePortNumber(mergedProps.getProperty(NonRegisteringDriver.PORT_PROPERTY_KEY, BuildConfig.DB_PORT));
        }
        this.port = newPort;
        this.host = newHost;
        this.sessionMaxRows = -1;
        HashMap hashMap = new HashMap();
        this.serverVariables = hashMap;
        hashMap.put("character_set_server", "utf8");
        MysqlIO mysqlIO = new MysqlIO(newHost, newPort, mergedProps, getSocketFactoryClassName(), getProxy(), getSocketTimeout(), this.largeRowSizeThreshold.getValueAsInt());
        this.f4io = mysqlIO;
        mysqlIO.doHandshake(this.user, this.password, this.database);
        if (versionMeetsMinimum(5, 5, 0)) {
            this.errorMessageEncoding = this.f4io.getEncodingForHandshake();
        }
    }

    private String normalizeHost(String hostname) {
        if (hostname == null || StringUtils.isEmptyOrWhitespaceOnly(hostname)) {
            return "localhost";
        }
        return hostname;
    }

    private int parsePortNumber(String portAsString) throws SQLException {
        try {
            return Integer.parseInt(portAsString);
        } catch (NumberFormatException e) {
            throw SQLError.createSQLException("Illegal connection port value '" + portAsString + "'", SQLError.SQL_STATE_INVALID_CONNECTION_ATTRIBUTE, getExceptionInterceptor());
        }
    }

    private void connectOneTryOnly(boolean isForReconnect, Properties mergedProps) throws SQLException {
        try {
            coreConnect(mergedProps);
            this.connectionId = this.f4io.getThreadId();
            this.isClosed = false;
            boolean oldAutoCommit = getAutoCommit();
            int oldIsolationLevel = this.isolationLevel;
            boolean oldReadOnly = isReadOnly(false);
            String oldCatalog = getCatalog();
            this.f4io.setStatementInterceptors(this.statementInterceptors);
            initializePropsFromServer();
            if (isForReconnect) {
                setAutoCommit(oldAutoCommit);
                if (this.hasIsolationLevels) {
                    setTransactionIsolation(oldIsolationLevel);
                }
                setCatalog(oldCatalog);
                setReadOnly(oldReadOnly);
            }
        } catch (Exception EEE) {
            if (!(EEE instanceof SQLException) || ((SQLException) EEE).getErrorCode() != 1820 || getDisconnectOnExpiredPasswords()) {
                MysqlIO mysqlIO = this.f4io;
                if (mysqlIO != null) {
                    mysqlIO.forceClose();
                }
                Exception connectionNotEstablishedBecause = EEE;
                if (EEE instanceof SQLException) {
                    throw ((SQLException) EEE);
                }
                SQLException chainedEx = SQLError.createSQLException(Messages.getString("Connection.UnableToConnect"), SQLError.SQL_STATE_UNABLE_TO_CONNECT_TO_DATASOURCE, getExceptionInterceptor());
                chainedEx.initCause(connectionNotEstablishedBecause);
                throw chainedEx;
            }
        }
    }

    private void createPreparedStatementCaches() throws SQLException {
        synchronized (getConnectionMutex()) {
            int cacheSize = getPreparedStatementCacheSize();
            try {
                this.cachedPreparedStatementParams = ((CacheAdapterFactory) Class.forName(getParseInfoCacheFactory()).newInstance()).getInstance(this, this.myURL, getPreparedStatementCacheSize(), getPreparedStatementCacheSqlLimit(), this.props);
                if (getUseServerPreparedStmts()) {
                    this.serverSideStatementCheckCache = new LRUCache<>(cacheSize);
                    this.serverSideStatementCache = new LRUCache<CompoundCacheKey, ServerPreparedStatement>(cacheSize) {
                        private static final long serialVersionUID = 7692318650375988114L;

                        /* access modifiers changed from: protected */
                        public boolean removeEldestEntry(Map.Entry<CompoundCacheKey, ServerPreparedStatement> eldest) {
                            if (this.maxElements <= 1) {
                                return false;
                            }
                            boolean removeIt = super.removeEldestEntry(eldest);
                            if (removeIt) {
                                ServerPreparedStatement ps = eldest.getValue();
                                ps.isCached = false;
                                ps.setClosed(false);
                                try {
                                    ps.close();
                                } catch (SQLException e) {
                                }
                            }
                            return removeIt;
                        }
                    };
                }
            } catch (ClassNotFoundException e) {
                SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.CantFindCacheFactory", new Object[]{getParseInfoCacheFactory(), "parseInfoCacheFactory"}), getExceptionInterceptor());
                sqlEx.initCause(e);
                throw sqlEx;
            } catch (InstantiationException e2) {
                SQLException sqlEx2 = SQLError.createSQLException(Messages.getString("Connection.CantLoadCacheFactory", new Object[]{getParseInfoCacheFactory(), "parseInfoCacheFactory"}), getExceptionInterceptor());
                sqlEx2.initCause(e2);
                throw sqlEx2;
            } catch (IllegalAccessException e3) {
                SQLException sqlEx3 = SQLError.createSQLException(Messages.getString("Connection.CantLoadCacheFactory", new Object[]{getParseInfoCacheFactory(), "parseInfoCacheFactory"}), getExceptionInterceptor());
                sqlEx3.initCause(e3);
                throw sqlEx3;
            }
        }
    }

    public Statement createStatement() throws SQLException {
        return createStatement(1003, 1007);
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        checkClosed();
        StatementImpl stmt = new StatementImpl(getMultiHostSafeProxy(), this.database);
        stmt.setResultSetType(resultSetType);
        stmt.setResultSetConcurrency(resultSetConcurrency);
        return stmt;
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        if (!getPedantic() || resultSetHoldability == 1) {
            return createStatement(resultSetType, resultSetConcurrency);
        }
        throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
    }

    public void dumpTestcaseQuery(String query) {
        System.err.println(query);
    }

    public Connection duplicate() throws SQLException {
        return new ConnectionImpl(this.origHostToConnectTo, this.origPortToConnectTo, this.props, this.origDatabaseToConnectTo, this.myURL);
    }

    public ResultSetInternalMethods execSQL(StatementImpl callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata) throws SQLException {
        return execSQL(callingStatement, sql, maxRows, packet, resultSetType, resultSetConcurrency, streamResults, catalog, cachedMetadata, false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00b8, code lost:
        return r0;
     */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x017d A[Catch:{ all -> 0x018d }] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00d4 A[Catch:{ all -> 0x00b9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00e1 A[Catch:{ all -> 0x00b9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0135 A[Catch:{ all -> 0x0164 }] */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x013f A[Catch:{ all -> 0x0164 }] */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0153 A[Catch:{ all -> 0x0164 }] */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0171 A[Catch:{ all -> 0x018d }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.mysql.jdbc.ResultSetInternalMethods execSQL(com.mysql.jdbc.StatementImpl r30, java.lang.String r31, int r32, com.mysql.jdbc.Buffer r33, int r34, int r35, boolean r36, java.lang.String r37, com.mysql.jdbc.Field[] r38, boolean r39) throws java.sql.SQLException {
        /*
            r29 = this;
            r1 = r29
            r13 = r33
            java.lang.Object r14 = r29.getConnectionMutex()
            monitor-enter(r14)
            boolean r0 = r29.getGatherPerformanceMetrics()     // Catch:{ all -> 0x0188 }
            r2 = 0
            if (r0 == 0) goto L_0x0016
            long r4 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0188 }
            goto L_0x0017
        L_0x0016:
            r4 = r2
        L_0x0017:
            r15 = r4
            r0 = 0
            if (r13 == 0) goto L_0x0020
            int r4 = r33.getPosition()     // Catch:{ all -> 0x0188 }
            goto L_0x0021
        L_0x0020:
            r4 = r0
        L_0x0021:
            r12 = r4
            r1.lastQueryFinishedTime = r2     // Catch:{ all -> 0x0188 }
            boolean r2 = r29.getHighAvailability()     // Catch:{ all -> 0x0188 }
            r11 = 1
            if (r2 == 0) goto L_0x0045
            boolean r2 = r1.autoCommit     // Catch:{ all -> 0x0188 }
            if (r2 != 0) goto L_0x0035
            boolean r2 = r29.getAutoReconnectForPools()     // Catch:{ all -> 0x0188 }
            if (r2 == 0) goto L_0x0045
        L_0x0035:
            boolean r2 = r1.needsPing     // Catch:{ all -> 0x0188 }
            if (r2 == 0) goto L_0x0045
            if (r39 != 0) goto L_0x0045
            r1.pingInternal(r0, r0)     // Catch:{ Exception -> 0x0041 }
            r1.needsPing = r0     // Catch:{ Exception -> 0x0041 }
            goto L_0x0045
        L_0x0041:
            r0 = move-exception
            r1.createNewIO(r11)     // Catch:{ all -> 0x0188 }
        L_0x0045:
            if (r13 != 0) goto L_0x0082
            com.mysql.jdbc.MysqlIO r0 = r1.f4io     // Catch:{ SQLException -> 0x007c, Exception -> 0x0078, all -> 0x0072 }
            boolean r2 = r29.getUseUnicode()     // Catch:{ SQLException -> 0x007c, Exception -> 0x0078, all -> 0x0072 }
            if (r2 == 0) goto L_0x0054
            java.lang.String r2 = r29.getEncoding()     // Catch:{ SQLException -> 0x007c, Exception -> 0x0078, all -> 0x0072 }
            goto L_0x0055
        L_0x0054:
            r2 = 0
        L_0x0055:
            r20 = r2
            r21 = 0
            r17 = r0
            r18 = r30
            r19 = r31
            r22 = r32
            r23 = r34
            r24 = r35
            r25 = r36
            r26 = r37
            r27 = r38
            com.mysql.jdbc.ResultSetInternalMethods r0 = r17.sqlQueryDirect(r18, r19, r20, r21, r22, r23, r24, r25, r26, r27)     // Catch:{ SQLException -> 0x007c, Exception -> 0x0078, all -> 0x0072 }
            r28 = r12
            goto L_0x009c
        L_0x0072:
            r0 = move-exception
            r3 = r31
            r4 = r12
            goto L_0x016b
        L_0x0078:
            r0 = move-exception
            r28 = r12
            goto L_0x00ce
        L_0x007c:
            r0 = move-exception
            r2 = r11
            r28 = r12
            goto L_0x0101
        L_0x0082:
            com.mysql.jdbc.MysqlIO r2 = r1.f4io     // Catch:{ SQLException -> 0x00fd, Exception -> 0x00cb, all -> 0x00c5 }
            r4 = 0
            r5 = 0
            r3 = r30
            r6 = r33
            r7 = r32
            r8 = r34
            r9 = r35
            r10 = r36
            r11 = r37
            r28 = r12
            r12 = r38
            com.mysql.jdbc.ResultSetInternalMethods r0 = r2.sqlQueryDirect(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)     // Catch:{ SQLException -> 0x00c2, Exception -> 0x00c0 }
        L_0x009c:
            boolean r2 = r29.getMaintainTimeStats()     // Catch:{ all -> 0x0188 }
            if (r2 == 0) goto L_0x00a8
            long r2 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0188 }
            r1.lastQueryFinishedTime = r2     // Catch:{ all -> 0x0188 }
        L_0x00a8:
            boolean r2 = r29.getGatherPerformanceMetrics()     // Catch:{ all -> 0x0188 }
            if (r2 == 0) goto L_0x00b6
            long r2 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0188 }
            long r2 = r2 - r15
            r1.registerQueryExecutionTime(r2)     // Catch:{ all -> 0x0188 }
        L_0x00b6:
            monitor-exit(r14)     // Catch:{ all -> 0x0188 }
            return r0
        L_0x00b9:
            r0 = move-exception
            r3 = r31
            r4 = r28
            goto L_0x016b
        L_0x00c0:
            r0 = move-exception
            goto L_0x00ce
        L_0x00c2:
            r0 = move-exception
            r2 = 1
            goto L_0x0101
        L_0x00c5:
            r0 = move-exception
            r3 = r31
            r4 = r12
            goto L_0x016b
        L_0x00cb:
            r0 = move-exception
            r28 = r12
        L_0x00ce:
            boolean r2 = r29.getHighAvailability()     // Catch:{ all -> 0x00b9 }
            if (r2 == 0) goto L_0x00e1
            boolean r2 = r0 instanceof java.io.IOException     // Catch:{ all -> 0x00b9 }
            if (r2 == 0) goto L_0x00dd
            com.mysql.jdbc.MysqlIO r2 = r1.f4io     // Catch:{ all -> 0x00b9 }
            r2.forceClose()     // Catch:{ all -> 0x00b9 }
        L_0x00dd:
            r2 = 1
            r1.needsPing = r2     // Catch:{ all -> 0x00b9 }
            goto L_0x00e8
        L_0x00e1:
            boolean r2 = r0 instanceof java.io.IOException     // Catch:{ all -> 0x00b9 }
            if (r2 == 0) goto L_0x00e8
            r1.cleanup(r0)     // Catch:{ all -> 0x00b9 }
        L_0x00e8:
            java.lang.String r2 = "Connection.UnexpectedException"
            java.lang.String r2 = com.mysql.jdbc.Messages.getString(r2)     // Catch:{ all -> 0x00b9 }
            java.lang.String r3 = "S1000"
            com.mysql.jdbc.ExceptionInterceptor r4 = r29.getExceptionInterceptor()     // Catch:{ all -> 0x00b9 }
            java.sql.SQLException r2 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r2, (java.lang.String) r3, (com.mysql.jdbc.ExceptionInterceptor) r4)     // Catch:{ all -> 0x00b9 }
            r2.initCause(r0)     // Catch:{ all -> 0x00b9 }
            throw r2     // Catch:{ all -> 0x00b9 }
        L_0x00fd:
            r0 = move-exception
            r2 = r11
            r28 = r12
        L_0x0101:
            boolean r3 = r29.getDumpQueriesOnException()     // Catch:{ all -> 0x0166 }
            if (r3 == 0) goto L_0x0135
            r3 = r31
            r4 = r28
            java.lang.String r5 = r1.extractSqlFromPacket(r3, r13, r4)     // Catch:{ all -> 0x0164 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0164 }
            int r7 = r5.length()     // Catch:{ all -> 0x0164 }
            int r7 = r7 + 32
            r6.<init>(r7)     // Catch:{ all -> 0x0164 }
            java.lang.String r7 = "\n\nQuery being executed when exception was thrown:\n"
            r6.append(r7)     // Catch:{ all -> 0x0164 }
            r6.append(r5)     // Catch:{ all -> 0x0164 }
            java.lang.String r7 = "\n\n"
            r6.append(r7)     // Catch:{ all -> 0x0164 }
            java.lang.String r7 = r6.toString()     // Catch:{ all -> 0x0164 }
            com.mysql.jdbc.ExceptionInterceptor r8 = r29.getExceptionInterceptor()     // Catch:{ all -> 0x0164 }
            java.sql.SQLException r7 = appendMessageToException(r0, r7, r8)     // Catch:{ all -> 0x0164 }
            r0 = r7
            goto L_0x0139
        L_0x0135:
            r3 = r31
            r4 = r28
        L_0x0139:
            boolean r5 = r29.getHighAvailability()     // Catch:{ all -> 0x0164 }
            if (r5 == 0) goto L_0x0153
            java.lang.String r5 = "08S01"
            java.lang.String r6 = r0.getSQLState()     // Catch:{ all -> 0x0164 }
            boolean r5 = r5.equals(r6)     // Catch:{ all -> 0x0164 }
            if (r5 == 0) goto L_0x0150
            com.mysql.jdbc.MysqlIO r5 = r1.f4io     // Catch:{ all -> 0x0164 }
            r5.forceClose()     // Catch:{ all -> 0x0164 }
        L_0x0150:
            r1.needsPing = r2     // Catch:{ all -> 0x0164 }
            goto L_0x0162
        L_0x0153:
            java.lang.String r2 = "08S01"
            java.lang.String r5 = r0.getSQLState()     // Catch:{ all -> 0x0164 }
            boolean r2 = r2.equals(r5)     // Catch:{ all -> 0x0164 }
            if (r2 == 0) goto L_0x0162
            r1.cleanup(r0)     // Catch:{ all -> 0x0164 }
        L_0x0162:
            throw r0     // Catch:{ all -> 0x0164 }
        L_0x0164:
            r0 = move-exception
            goto L_0x016b
        L_0x0166:
            r0 = move-exception
            r3 = r31
            r4 = r28
        L_0x016b:
            boolean r2 = r29.getMaintainTimeStats()     // Catch:{ all -> 0x018d }
            if (r2 == 0) goto L_0x0177
            long r5 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x018d }
            r1.lastQueryFinishedTime = r5     // Catch:{ all -> 0x018d }
        L_0x0177:
            boolean r2 = r29.getGatherPerformanceMetrics()     // Catch:{ all -> 0x018d }
            if (r2 == 0) goto L_0x0185
            long r5 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x018d }
            long r5 = r5 - r15
            r1.registerQueryExecutionTime(r5)     // Catch:{ all -> 0x018d }
        L_0x0185:
            throw r0     // Catch:{ all -> 0x018d }
        L_0x0188:
            r0 = move-exception
            r3 = r31
        L_0x018b:
            monitor-exit(r14)     // Catch:{ all -> 0x018d }
            throw r0
        L_0x018d:
            r0 = move-exception
            goto L_0x018b
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ConnectionImpl.execSQL(com.mysql.jdbc.StatementImpl, java.lang.String, int, com.mysql.jdbc.Buffer, int, int, boolean, java.lang.String, com.mysql.jdbc.Field[], boolean):com.mysql.jdbc.ResultSetInternalMethods");
    }

    public String extractSqlFromPacket(String possibleSqlQuery, Buffer queryPacket, int endOfQueryPacketPosition) throws SQLException {
        String extractedSql = null;
        if (possibleSqlQuery != null) {
            if (possibleSqlQuery.length() > getMaxQuerySizeToLog()) {
                extractedSql = possibleSqlQuery.substring(0, getMaxQuerySizeToLog()) + Messages.getString("MysqlIO.25");
            } else {
                extractedSql = possibleSqlQuery;
            }
        }
        if (extractedSql != null) {
            return extractedSql;
        }
        int extractPosition = endOfQueryPacketPosition;
        boolean truncated = false;
        if (endOfQueryPacketPosition > getMaxQuerySizeToLog()) {
            extractPosition = getMaxQuerySizeToLog();
            truncated = true;
        }
        String extractedSql2 = StringUtils.toString(queryPacket.getByteBuffer(), 5, extractPosition - 5);
        if (truncated) {
            return extractedSql2 + Messages.getString("MysqlIO.25");
        }
        return extractedSql2;
    }

    public StringBuilder generateConnectionCommentBlock(StringBuilder buf) {
        buf.append("/* conn id ");
        buf.append(getId());
        buf.append(" clock: ");
        buf.append(System.currentTimeMillis());
        buf.append(" */ ");
        return buf;
    }

    public int getActiveStatementCount() {
        return this.openStatements.size();
    }

    public boolean getAutoCommit() throws SQLException {
        boolean z;
        synchronized (getConnectionMutex()) {
            z = this.autoCommit;
        }
        return z;
    }

    public Calendar getCalendarInstanceForSessionOrNew() {
        if (getDynamicCalendars()) {
            return Calendar.getInstance();
        }
        return getSessionLockedCalendar();
    }

    public String getCatalog() throws SQLException {
        String str;
        synchronized (getConnectionMutex()) {
            str = this.database;
        }
        return str;
    }

    public String getCharacterSetMetadata() {
        String str;
        synchronized (getConnectionMutex()) {
            str = this.characterSetMetadata;
        }
        return str;
    }

    public SingleByteCharsetConverter getCharsetConverter(String javaEncodingName) throws SQLException {
        SingleByteCharsetConverter converter;
        if (javaEncodingName == null || this.usePlatformCharsetConverters) {
            return null;
        }
        synchronized (this.charsetConverterMap) {
            Object asObject = this.charsetConverterMap.get(javaEncodingName);
            Object obj = CHARSET_CONVERTER_NOT_AVAILABLE_MARKER;
            if (asObject == obj) {
                return null;
            }
            converter = (SingleByteCharsetConverter) asObject;
            if (converter == null) {
                try {
                    converter = SingleByteCharsetConverter.getInstance(javaEncodingName, this);
                    if (converter == null) {
                        this.charsetConverterMap.put(javaEncodingName, obj);
                    } else {
                        this.charsetConverterMap.put(javaEncodingName, converter);
                    }
                } catch (UnsupportedEncodingException e) {
                    this.charsetConverterMap.put(javaEncodingName, CHARSET_CONVERTER_NOT_AVAILABLE_MARKER);
                    converter = null;
                }
            }
        }
        return converter;
    }

    @Deprecated
    public String getCharsetNameForIndex(int charsetIndex) throws SQLException {
        return getEncodingForIndex(charsetIndex);
    }

    public String getEncodingForIndex(int charsetIndex) throws SQLException {
        String cs;
        String javaEncoding = null;
        if (getUseOldUTF8Behavior()) {
            return getEncoding();
        }
        if (charsetIndex == -1) {
            return getEncoding();
        }
        try {
            Map<Integer, String> map = this.indexToCustomMysqlCharset;
            if (!(map == null || (cs = map.get(Integer.valueOf(charsetIndex))) == null)) {
                javaEncoding = CharsetMapping.getJavaEncodingForMysqlCharset(cs, getEncoding());
            }
            if (javaEncoding == null) {
                javaEncoding = CharsetMapping.getJavaEncodingForCollationIndex(Integer.valueOf(charsetIndex), getEncoding());
            }
            if (javaEncoding == null) {
                return getEncoding();
            }
            return javaEncoding;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw SQLError.createSQLException("Unknown character set index for field '" + charsetIndex + "' received from server.", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
        } catch (RuntimeException ex) {
            SQLException sqlEx = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
            sqlEx.initCause(ex);
            throw sqlEx;
        }
    }

    public TimeZone getDefaultTimeZone() {
        return getCacheDefaultTimezone() ? this.defaultTimeZone : TimeUtil.getDefaultTimeZone(false);
    }

    public String getErrorMessageEncoding() {
        return this.errorMessageEncoding;
    }

    public int getHoldability() throws SQLException {
        return 2;
    }

    public long getId() {
        return this.connectionId;
    }

    public long getIdleFor() {
        long j;
        synchronized (getConnectionMutex()) {
            j = 0;
            if (this.lastQueryFinishedTime != 0) {
                j = System.currentTimeMillis() - this.lastQueryFinishedTime;
            }
        }
        return j;
    }

    public MysqlIO getIO() throws SQLException {
        MysqlIO mysqlIO = this.f4io;
        if (mysqlIO != null && !this.isClosed) {
            return mysqlIO;
        }
        throw SQLError.createSQLException("Operation not allowed on closed connection", SQLError.SQL_STATE_CONNECTION_NOT_OPEN, getExceptionInterceptor());
    }

    public Log getLog() throws SQLException {
        return this.log;
    }

    public int getMaxBytesPerChar(String javaCharsetName) throws SQLException {
        return getMaxBytesPerChar((Integer) null, javaCharsetName);
    }

    public int getMaxBytesPerChar(Integer charsetIndex, String javaCharsetName) throws SQLException {
        String charset = null;
        try {
            Map<Integer, String> map = this.indexToCustomMysqlCharset;
            if (map != null) {
                charset = map.get(charsetIndex);
            }
            if (charset == null) {
                charset = CharsetMapping.getMysqlCharsetNameForCollationIndex(charsetIndex);
            }
            if (charset == null) {
                charset = CharsetMapping.getMysqlCharsetForJavaEncoding(javaCharsetName, this);
            }
            Integer mblen = null;
            Map<String, Integer> map2 = this.mysqlCharsetToCustomMblen;
            if (map2 != null) {
                mblen = map2.get(charset);
            }
            if (mblen == null) {
                mblen = Integer.valueOf(CharsetMapping.getMblen(charset));
            }
            if (mblen != null) {
                return mblen.intValue();
            }
            return 1;
        } catch (SQLException ex) {
            throw ex;
        } catch (RuntimeException ex2) {
            SQLException sqlEx = SQLError.createSQLException(ex2.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
            sqlEx.initCause(ex2);
            throw sqlEx;
        }
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return getMetaData(true, true);
    }

    private DatabaseMetaData getMetaData(boolean checkClosed, boolean checkForInfoSchema) throws SQLException {
        if (checkClosed) {
            checkClosed();
        }
        return DatabaseMetaData.getInstance(getMultiHostSafeProxy(), this.database, checkForInfoSchema);
    }

    public Statement getMetadataSafeStatement() throws SQLException {
        return getMetadataSafeStatement(0);
    }

    public Statement getMetadataSafeStatement(int maxRows) throws SQLException {
        Statement stmt = createStatement();
        stmt.setMaxRows(maxRows == -1 ? 0 : maxRows);
        stmt.setEscapeProcessing(false);
        if (stmt.getFetchSize() != 0) {
            stmt.setFetchSize(0);
        }
        return stmt;
    }

    public int getNetBufferLength() {
        return this.netBufferLength;
    }

    @Deprecated
    public String getServerCharacterEncoding() {
        return getServerCharset();
    }

    public String getServerCharset() {
        if (!this.f4io.versionMeetsMinimum(4, 1, 0)) {
            return this.serverVariables.get("character_set");
        }
        String charset = null;
        Map<Integer, String> map = this.indexToCustomMysqlCharset;
        if (map != null) {
            charset = map.get(Integer.valueOf(this.f4io.serverCharsetIndex));
        }
        if (charset == null) {
            charset = CharsetMapping.getMysqlCharsetNameForCollationIndex(Integer.valueOf(this.f4io.serverCharsetIndex));
        }
        return charset != null ? charset : this.serverVariables.get("character_set_server");
    }

    public int getServerMajorVersion() {
        return this.f4io.getServerMajorVersion();
    }

    public int getServerMinorVersion() {
        return this.f4io.getServerMinorVersion();
    }

    public int getServerSubMinorVersion() {
        return this.f4io.getServerSubMinorVersion();
    }

    public TimeZone getServerTimezoneTZ() {
        return this.serverTimezoneTZ;
    }

    public String getServerVariable(String variableName) {
        Map<String, String> map = this.serverVariables;
        if (map != null) {
            return map.get(variableName);
        }
        return null;
    }

    public String getServerVersion() {
        return this.f4io.getServerVersion();
    }

    public Calendar getSessionLockedCalendar() {
        return this.sessionCalendar;
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0043 A[Catch:{ all -> 0x009f }] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0092 A[Catch:{ all -> 0x009f }] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:54:0x00b1=Splitter:B:54:0x00b1, B:35:0x006c=Splitter:B:35:0x006c} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getTransactionIsolation() throws java.sql.SQLException {
        /*
            r8 = this;
            java.lang.Object r0 = r8.getConnectionMutex()
            monitor-enter(r0)
            boolean r1 = r8.hasIsolationLevels     // Catch:{ all -> 0x00b6 }
            if (r1 == 0) goto L_0x00b2
            boolean r1 = r8.getUseLocalSessionState()     // Catch:{ all -> 0x00b6 }
            if (r1 != 0) goto L_0x00b2
            r1 = 0
            r2 = 0
            int r3 = r8.sessionMaxRows     // Catch:{ all -> 0x009f }
            java.sql.Statement r3 = r8.getMetadataSafeStatement(r3)     // Catch:{ all -> 0x009f }
            r1 = r3
            r3 = 3
            r4 = 8
            r5 = 0
            boolean r3 = r8.versionMeetsMinimum(r4, r5, r3)     // Catch:{ all -> 0x009f }
            if (r3 != 0) goto L_0x0036
            r3 = 5
            r6 = 7
            r7 = 20
            boolean r3 = r8.versionMeetsMinimum(r3, r6, r7)     // Catch:{ all -> 0x009f }
            if (r3 == 0) goto L_0x0033
            boolean r3 = r8.versionMeetsMinimum(r4, r5, r5)     // Catch:{ all -> 0x009f }
            if (r3 != 0) goto L_0x0033
            goto L_0x0036
        L_0x0033:
            java.lang.String r3 = "SELECT @@session.tx_isolation"
            goto L_0x0038
        L_0x0036:
            java.lang.String r3 = "SELECT @@session.transaction_isolation"
        L_0x0038:
            java.sql.ResultSet r4 = r1.executeQuery(r3)     // Catch:{ all -> 0x009f }
            r2 = r4
            boolean r4 = r2.next()     // Catch:{ all -> 0x009f }
            if (r4 == 0) goto L_0x0092
            r4 = 1
            java.lang.String r4 = r2.getString(r4)     // Catch:{ all -> 0x009f }
            if (r4 == 0) goto L_0x006e
            java.util.Map<java.lang.String, java.lang.Integer> r5 = mapTransIsolationNameToValue     // Catch:{ all -> 0x009f }
            java.lang.Object r5 = r5.get(r4)     // Catch:{ all -> 0x009f }
            java.lang.Integer r5 = (java.lang.Integer) r5     // Catch:{ all -> 0x009f }
            if (r5 == 0) goto L_0x006e
            int r6 = r5.intValue()     // Catch:{ all -> 0x009f }
            r8.isolationLevel = r6     // Catch:{ all -> 0x009f }
            if (r2 == 0) goto L_0x0063
            r2.close()     // Catch:{ Exception -> 0x0061 }
            goto L_0x0062
        L_0x0061:
            r7 = move-exception
        L_0x0062:
            r2 = 0
        L_0x0063:
            if (r1 == 0) goto L_0x006b
            r1.close()     // Catch:{ Exception -> 0x0069 }
            goto L_0x006a
        L_0x0069:
            r7 = move-exception
        L_0x006a:
            r1 = 0
        L_0x006b:
            monitor-exit(r0)     // Catch:{ all -> 0x00b6 }
            return r6
        L_0x006e:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x009f }
            r5.<init>()     // Catch:{ all -> 0x009f }
            java.lang.String r6 = "Could not map transaction isolation '"
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ all -> 0x009f }
            java.lang.StringBuilder r5 = r5.append(r4)     // Catch:{ all -> 0x009f }
            java.lang.String r6 = " to a valid JDBC level."
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ all -> 0x009f }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x009f }
            java.lang.String r6 = "S1000"
            com.mysql.jdbc.ExceptionInterceptor r7 = r8.getExceptionInterceptor()     // Catch:{ all -> 0x009f }
            java.sql.SQLException r5 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r5, (java.lang.String) r6, (com.mysql.jdbc.ExceptionInterceptor) r7)     // Catch:{ all -> 0x009f }
            throw r5     // Catch:{ all -> 0x009f }
        L_0x0092:
            java.lang.String r4 = "Could not retrieve transaction isolation level from server"
            java.lang.String r5 = "S1000"
            com.mysql.jdbc.ExceptionInterceptor r6 = r8.getExceptionInterceptor()     // Catch:{ all -> 0x009f }
            java.sql.SQLException r4 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r4, (java.lang.String) r5, (com.mysql.jdbc.ExceptionInterceptor) r6)     // Catch:{ all -> 0x009f }
            throw r4     // Catch:{ all -> 0x009f }
        L_0x009f:
            r3 = move-exception
            if (r2 == 0) goto L_0x00a8
            r2.close()     // Catch:{ Exception -> 0x00a6 }
            goto L_0x00a7
        L_0x00a6:
            r4 = move-exception
        L_0x00a7:
            r2 = 0
        L_0x00a8:
            if (r1 == 0) goto L_0x00b0
            r1.close()     // Catch:{ Exception -> 0x00ae }
            goto L_0x00af
        L_0x00ae:
            r4 = move-exception
        L_0x00af:
            r1 = 0
        L_0x00b0:
            throw r3     // Catch:{ all -> 0x00b6 }
        L_0x00b2:
            int r1 = r8.isolationLevel     // Catch:{ all -> 0x00b6 }
            monitor-exit(r0)     // Catch:{ all -> 0x00b6 }
            return r1
        L_0x00b6:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00b6 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ConnectionImpl.getTransactionIsolation():int");
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        Map<String, Class<?>> map;
        synchronized (getConnectionMutex()) {
            if (this.typeMap == null) {
                this.typeMap = new HashMap();
            }
            map = this.typeMap;
        }
        return map;
    }

    public String getURL() {
        return this.myURL;
    }

    public String getUser() {
        return this.user;
    }

    public Calendar getUtcCalendar() {
        return this.utcCalendar;
    }

    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    public boolean hasSameProperties(Connection c) {
        return this.props.equals(c.getProperties());
    }

    public Properties getProperties() {
        return this.props;
    }

    @Deprecated
    public boolean hasTriedMaster() {
        return this.hasTriedMasterFlag;
    }

    public void incrementNumberOfPreparedExecutes() {
        if (getGatherPerformanceMetrics()) {
            this.numberOfPreparedExecutes++;
            this.numberOfQueriesIssued++;
        }
    }

    public void incrementNumberOfPrepares() {
        if (getGatherPerformanceMetrics()) {
            this.numberOfPrepares++;
        }
    }

    public void incrementNumberOfResultSetsCreated() {
        if (getGatherPerformanceMetrics()) {
            this.numberOfResultSetsCreated++;
        }
    }

    private void initializeDriverProperties(Properties info) throws SQLException {
        initializeProperties(info);
        String exceptionInterceptorClasses = getExceptionInterceptors();
        if (exceptionInterceptorClasses != null && !"".equals(exceptionInterceptorClasses)) {
            this.exceptionInterceptor = new ExceptionInterceptorChain(exceptionInterceptorClasses);
        }
        this.usePlatformCharsetConverters = getUseJvmCharsetConverters();
        this.log = LogFactory.getLogger(getLogger(), LOGGER_INSTANCE_NAME, getExceptionInterceptor());
        if (getProfileSql() || getLogSlowQueries() || getUseUsageAdvisor()) {
            this.eventSink = ProfilerEventHandlerFactory.getInstance(getMultiHostSafeProxy());
        }
        if (getCachePreparedStatements()) {
            createPreparedStatementCaches();
        }
        if (!getNoDatetimeStringSync() || !getUseTimezone()) {
            if (getCacheCallableStatements()) {
                this.parsedCallableStatementCache = new LRUCache<>(getCallableStatementCacheSize());
            }
            if (getAllowMultiQueries()) {
                setCacheResultSetMetadata(false);
            }
            if (getCacheResultSetMetadata()) {
                this.resultSetMetadataCache = new LRUCache<>(getMetadataCacheSize());
            }
            if (getSocksProxyHost() != null) {
                setSocketFactoryClassName("com.mysql.jdbc.SocksProxySocketFactory");
                return;
            }
            return;
        }
        throw SQLError.createSQLException("Can't enable noDatetimeStringSync and useTimezone configuration properties at the same time", SQLError.SQL_STATE_INVALID_CONNECTION_ATTRIBUTE, getExceptionInterceptor());
    }

    private void initializePropsFromServer() throws SQLException {
        String defaultMetadataCharset;
        String connectionInterceptorClasses = getConnectionLifecycleInterceptors();
        this.connectionLifecycleInterceptors = null;
        if (connectionInterceptorClasses != null) {
            this.connectionLifecycleInterceptors = Util.loadExtensions(this, this.props, connectionInterceptorClasses, "Connection.badLifecycleInterceptor", getExceptionInterceptor());
        }
        setSessionVariables();
        if (!versionMeetsMinimum(4, 1, 0)) {
            setTransformedBitIsBoolean(false);
        }
        this.parserKnowsUnicode = versionMeetsMinimum(4, 1, 0);
        if (getUseServerPreparedStmts() && versionMeetsMinimum(4, 1, 0)) {
            this.useServerPreparedStmts = true;
            if (versionMeetsMinimum(5, 0, 0) && !versionMeetsMinimum(5, 0, 3)) {
                this.useServerPreparedStmts = false;
            }
        }
        if (versionMeetsMinimum(3, 21, 22)) {
            loadServerVariables();
            if (versionMeetsMinimum(5, 0, 2)) {
                this.autoIncrementIncrement = getServerVariableAsInt("auto_increment_increment", 1);
            } else {
                this.autoIncrementIncrement = 1;
            }
            buildCollationMapping();
            if (this.f4io.serverCharsetIndex == 0) {
                String collationServer = this.serverVariables.get("collation_server");
                if (collationServer != null) {
                    int i = 1;
                    while (true) {
                        if (i >= CharsetMapping.COLLATION_INDEX_TO_COLLATION_NAME.length) {
                            break;
                        } else if (CharsetMapping.COLLATION_INDEX_TO_COLLATION_NAME[i].equals(collationServer)) {
                            this.f4io.serverCharsetIndex = i;
                            break;
                        } else {
                            i++;
                        }
                    }
                } else {
                    this.f4io.serverCharsetIndex = 45;
                }
            }
            LicenseConfiguration.checkLicenseType(this.serverVariables);
            String lowerCaseTables = this.serverVariables.get("lower_case_table_names");
            this.lowerCaseTableNames = DebugKt.DEBUG_PROPERTY_VALUE_ON.equalsIgnoreCase(lowerCaseTables) || "1".equalsIgnoreCase(lowerCaseTables) || "2".equalsIgnoreCase(lowerCaseTables);
            this.storesLowerCaseTableName = "1".equalsIgnoreCase(lowerCaseTables) || DebugKt.DEBUG_PROPERTY_VALUE_ON.equalsIgnoreCase(lowerCaseTables);
            configureTimezone();
            if (this.serverVariables.containsKey("max_allowed_packet")) {
                int serverMaxAllowedPacket = getServerVariableAsInt("max_allowed_packet", -1);
                if (serverMaxAllowedPacket != -1 && (serverMaxAllowedPacket < getMaxAllowedPacket() || getMaxAllowedPacket() <= 0)) {
                    setMaxAllowedPacket(serverMaxAllowedPacket);
                } else if (serverMaxAllowedPacket == -1 && getMaxAllowedPacket() == -1) {
                    setMaxAllowedPacket(SupportMenu.USER_MASK);
                }
                if (getUseServerPrepStmts()) {
                    int allowedBlobSendChunkSize = Math.min(getBlobSendChunkSize(), getMaxAllowedPacket()) - 8203;
                    if (allowedBlobSendChunkSize > 0) {
                        setBlobSendChunkSize(String.valueOf(allowedBlobSendChunkSize));
                    } else {
                        throw SQLError.createSQLException("Connection setting too low for 'maxAllowedPacket'. When 'useServerPrepStmts=true', 'maxAllowedPacket' must be higher than " + 8203 + ". Check also 'max_allowed_packet' in MySQL configuration files.", SQLError.SQL_STATE_INVALID_CONNECTION_ATTRIBUTE, getExceptionInterceptor());
                    }
                }
            }
            if (this.serverVariables.containsKey("net_buffer_length")) {
                this.netBufferLength = getServerVariableAsInt("net_buffer_length", 16384);
            }
            checkTransactionIsolationLevel();
            if (!versionMeetsMinimum(4, 1, 0)) {
                checkServerEncoding();
            }
            this.f4io.checkForCharsetMismatch();
            if (this.serverVariables.containsKey("sql_mode")) {
                String sqlModeAsString = this.serverVariables.get("sql_mode");
                if (StringUtils.isStrictlyNumeric(sqlModeAsString)) {
                    this.useAnsiQuotes = (Integer.parseInt(sqlModeAsString) & 4) > 0;
                } else if (sqlModeAsString != null) {
                    this.useAnsiQuotes = sqlModeAsString.indexOf("ANSI_QUOTES") != -1;
                    this.noBackslashEscapes = sqlModeAsString.indexOf("NO_BACKSLASH_ESCAPES") != -1;
                    this.serverTruncatesFracSecs = sqlModeAsString.indexOf("TIME_TRUNCATE_FRACTIONAL") != -1;
                }
            }
        }
        configureClientCharacterSet(false);
        try {
            this.errorMessageEncoding = CharsetMapping.getCharacterEncodingForErrorMessages(this);
            if (versionMeetsMinimum(3, 23, 15)) {
                this.transactionsSupported = true;
                handleAutoCommitDefaults();
            } else {
                this.transactionsSupported = false;
            }
            if (versionMeetsMinimum(3, 23, 36)) {
                this.hasIsolationLevels = true;
            } else {
                this.hasIsolationLevels = false;
            }
            this.hasQuotedIdentifiers = versionMeetsMinimum(3, 23, 6);
            this.f4io.resetMaxBuf();
            if (this.f4io.versionMeetsMinimum(4, 1, 0)) {
                String characterSetResultsOnServerMysql = this.serverVariables.get(JDBC_LOCAL_CHARACTER_SET_RESULTS);
                if (characterSetResultsOnServerMysql == null || StringUtils.startsWithIgnoreCaseAndWs(characterSetResultsOnServerMysql, "NULL") || characterSetResultsOnServerMysql.length() == 0) {
                    String defaultMetadataCharsetMysql = this.serverVariables.get("character_set_system");
                    if (defaultMetadataCharsetMysql != null) {
                        defaultMetadataCharset = CharsetMapping.getJavaEncodingForMysqlCharset(defaultMetadataCharsetMysql);
                    } else {
                        defaultMetadataCharset = "UTF-8";
                    }
                    this.characterSetMetadata = defaultMetadataCharset;
                } else {
                    String javaEncodingForMysqlCharset = CharsetMapping.getJavaEncodingForMysqlCharset(characterSetResultsOnServerMysql);
                    this.characterSetResultsOnServer = javaEncodingForMysqlCharset;
                    this.characterSetMetadata = javaEncodingForMysqlCharset;
                }
            } else {
                this.characterSetMetadata = getEncoding();
            }
            if (versionMeetsMinimum(4, 1, 0) && !versionMeetsMinimum(4, 1, 10) && getAllowMultiQueries() && isQueryCacheEnabled()) {
                setAllowMultiQueries(false);
            }
            if (versionMeetsMinimum(5, 0, 0) && ((getUseLocalTransactionState() || getElideSetAutoCommits()) && isQueryCacheEnabled() && !versionMeetsMinimum(5, 1, 32))) {
                setUseLocalTransactionState(false);
                setElideSetAutoCommits(false);
            }
            setupServerForTruncationChecks();
        } catch (SQLException ex) {
            throw ex;
        } catch (RuntimeException ex2) {
            SQLException sqlEx = SQLError.createSQLException(ex2.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
            sqlEx.initCause(ex2);
            throw sqlEx;
        }
    }

    public boolean isQueryCacheEnabled() {
        return "ON".equalsIgnoreCase(this.serverVariables.get("query_cache_type")) && !"0".equalsIgnoreCase(this.serverVariables.get("query_cache_size"));
    }

    private int getServerVariableAsInt(String variableName, int fallbackValue) throws SQLException {
        try {
            return Integer.parseInt(this.serverVariables.get(variableName));
        } catch (NumberFormatException e) {
            getLog().logWarn(Messages.getString("Connection.BadValueInServerVariables", new Object[]{variableName, this.serverVariables.get(variableName), Integer.valueOf(fallbackValue)}));
            return fallbackValue;
        }
    }

    private void handleAutoCommitDefaults() throws SQLException {
        boolean resetAutoCommitDefault = false;
        if (!getElideSetAutoCommits()) {
            String initConnectValue = this.serverVariables.get("init_connect");
            if (!versionMeetsMinimum(4, 1, 2) || initConnectValue == null || initConnectValue.length() <= 0) {
                resetAutoCommitDefault = true;
            } else {
                ResultSet rs = null;
                Statement stmt = null;
                try {
                    stmt = getMetadataSafeStatement();
                    rs = stmt.executeQuery("SELECT @@session.autocommit");
                    if (rs.next()) {
                        boolean z = rs.getBoolean(1);
                        this.autoCommit = z;
                        resetAutoCommitDefault = !z;
                    }
                } finally {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                        }
                    }
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (SQLException e2) {
                        }
                    }
                }
            }
        } else if (getIO().isSetNeededForAutoCommitMode(true)) {
            this.autoCommit = false;
            resetAutoCommitDefault = true;
        }
        if (resetAutoCommitDefault) {
            try {
                setAutoCommit(true);
            } catch (SQLException ex) {
                if (ex.getErrorCode() != 1820 || getDisconnectOnExpiredPasswords()) {
                    throw ex;
                }
            }
        }
    }

    public boolean isClientTzUTC() {
        return this.isClientTzUTC;
    }

    public boolean isClosed() {
        return this.isClosed;
    }

    public boolean isCursorFetchEnabled() throws SQLException {
        return versionMeetsMinimum(5, 0, 2) && getUseCursorFetch();
    }

    public boolean isInGlobalTx() {
        return this.isInGlobalTx;
    }

    public boolean isMasterConnection() {
        return false;
    }

    public boolean isNoBackslashEscapesSet() {
        return this.noBackslashEscapes;
    }

    public boolean isReadInfoMsgEnabled() {
        return this.readInfoMsg;
    }

    public boolean isReadOnly() throws SQLException {
        return isReadOnly(true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x004d A[Catch:{ SQLException -> 0x006a, all -> 0x0068 }] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x007c A[SYNTHETIC, Splitter:B:44:0x007c] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0084 A[SYNTHETIC, Splitter:B:49:0x0084] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isReadOnly(boolean r8) throws java.sql.SQLException {
        /*
            r7 = this;
            if (r8 == 0) goto L_0x00ab
            boolean r0 = r7.isClosed
            if (r0 != 0) goto L_0x00ab
            r0 = 6
            r1 = 5
            boolean r0 = r7.versionMeetsMinimum(r1, r0, r1)
            if (r0 == 0) goto L_0x00ab
            boolean r0 = r7.getUseLocalSessionState()
            if (r0 != 0) goto L_0x00ab
            boolean r0 = r7.getReadOnlyPropagatesToServer()
            if (r0 == 0) goto L_0x00ab
            r0 = 0
            r2 = 0
            int r3 = r7.sessionMaxRows     // Catch:{ SQLException -> 0x006a }
            java.sql.Statement r3 = r7.getMetadataSafeStatement(r3)     // Catch:{ SQLException -> 0x006a }
            r0 = r3
            r3 = 3
            r4 = 8
            r5 = 0
            boolean r3 = r7.versionMeetsMinimum(r4, r5, r3)     // Catch:{ SQLException -> 0x006a }
            if (r3 != 0) goto L_0x0040
            r3 = 7
            r6 = 20
            boolean r1 = r7.versionMeetsMinimum(r1, r3, r6)     // Catch:{ SQLException -> 0x006a }
            if (r1 == 0) goto L_0x003d
            boolean r1 = r7.versionMeetsMinimum(r4, r5, r5)     // Catch:{ SQLException -> 0x006a }
            if (r1 != 0) goto L_0x003d
            goto L_0x0040
        L_0x003d:
            java.lang.String r1 = "select @@session.tx_read_only"
            goto L_0x0042
        L_0x0040:
            java.lang.String r1 = "select @@session.transaction_read_only"
        L_0x0042:
            java.sql.ResultSet r1 = r0.executeQuery(r1)     // Catch:{ SQLException -> 0x006a }
            r2 = r1
            boolean r1 = r2.next()     // Catch:{ SQLException -> 0x006a }
            if (r1 == 0) goto L_0x0067
            r1 = 1
            int r3 = r2.getInt(r1)     // Catch:{ SQLException -> 0x006a }
            if (r3 == 0) goto L_0x0055
            r5 = r1
        L_0x0055:
            if (r2 == 0) goto L_0x005d
            r2.close()     // Catch:{ Exception -> 0x005b }
            goto L_0x005c
        L_0x005b:
            r1 = move-exception
        L_0x005c:
            r2 = 0
        L_0x005d:
            if (r0 == 0) goto L_0x0065
            r0.close()     // Catch:{ Exception -> 0x0063 }
            goto L_0x0064
        L_0x0063:
            r1 = move-exception
        L_0x0064:
            r0 = 0
        L_0x0065:
            return r5
        L_0x0067:
            goto L_0x0079
        L_0x0068:
            r1 = move-exception
            goto L_0x0099
        L_0x006a:
            r1 = move-exception
            int r3 = r1.getErrorCode()     // Catch:{ all -> 0x0068 }
            r4 = 1820(0x71c, float:2.55E-42)
            if (r3 != r4) goto L_0x008c
            boolean r3 = r7.getDisconnectOnExpiredPasswords()     // Catch:{ all -> 0x0068 }
            if (r3 != 0) goto L_0x008c
        L_0x0079:
            if (r2 == 0) goto L_0x0082
            r2.close()     // Catch:{ Exception -> 0x0080 }
            goto L_0x0081
        L_0x0080:
            r1 = move-exception
        L_0x0081:
            r2 = 0
        L_0x0082:
            if (r0 == 0) goto L_0x008a
            r0.close()     // Catch:{ Exception -> 0x0088 }
            goto L_0x0089
        L_0x0088:
            r1 = move-exception
        L_0x0089:
            r0 = 0
        L_0x008a:
            goto L_0x00ab
        L_0x008c:
            java.lang.String r3 = "Could not retrieve transaction read-only status from server"
            java.lang.String r4 = "S1000"
            com.mysql.jdbc.ExceptionInterceptor r5 = r7.getExceptionInterceptor()     // Catch:{ all -> 0x0068 }
            java.sql.SQLException r3 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r3, (java.lang.String) r4, (java.lang.Throwable) r1, (com.mysql.jdbc.ExceptionInterceptor) r5)     // Catch:{ all -> 0x0068 }
            throw r3     // Catch:{ all -> 0x0068 }
        L_0x0099:
            if (r2 == 0) goto L_0x00a1
            r2.close()     // Catch:{ Exception -> 0x009f }
            goto L_0x00a0
        L_0x009f:
            r3 = move-exception
        L_0x00a0:
            r2 = 0
        L_0x00a1:
            if (r0 == 0) goto L_0x00a9
            r0.close()     // Catch:{ Exception -> 0x00a7 }
            goto L_0x00a8
        L_0x00a7:
            r3 = move-exception
        L_0x00a8:
            r0 = 0
        L_0x00a9:
            throw r1
        L_0x00ab:
            boolean r0 = r7.readOnly
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ConnectionImpl.isReadOnly(boolean):boolean");
    }

    public boolean isRunningOnJDK13() {
        return this.isRunningOnJDK13;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0075, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isSameResource(com.mysql.jdbc.Connection r11) {
        /*
            r10 = this;
            java.lang.Object r0 = r10.getConnectionMutex()
            monitor-enter(r0)
            r1 = 0
            if (r11 != 0) goto L_0x000a
            monitor-exit(r0)     // Catch:{ all -> 0x0076 }
            return r1
        L_0x000a:
            r2 = 1
            r3 = r11
            com.mysql.jdbc.ConnectionImpl r3 = (com.mysql.jdbc.ConnectionImpl) r3     // Catch:{ all -> 0x0076 }
            java.lang.String r3 = r3.origHostToConnectTo     // Catch:{ all -> 0x0076 }
            r4 = r11
            com.mysql.jdbc.ConnectionImpl r4 = (com.mysql.jdbc.ConnectionImpl) r4     // Catch:{ all -> 0x0076 }
            java.lang.String r4 = r4.origDatabaseToConnectTo     // Catch:{ all -> 0x0076 }
            r5 = r11
            com.mysql.jdbc.ConnectionImpl r5 = (com.mysql.jdbc.ConnectionImpl) r5     // Catch:{ all -> 0x0076 }
            java.lang.String r5 = r5.database     // Catch:{ all -> 0x0076 }
            java.lang.String r6 = r10.origHostToConnectTo     // Catch:{ all -> 0x0076 }
            boolean r6 = nullSafeCompare(r3, r6)     // Catch:{ all -> 0x0076 }
            r7 = 1
            if (r6 != 0) goto L_0x0025
            r2 = 0
            goto L_0x0045
        L_0x0025:
            if (r3 == 0) goto L_0x0045
            r6 = 44
            int r6 = r3.indexOf(r6)     // Catch:{ all -> 0x0076 }
            r8 = -1
            if (r6 != r8) goto L_0x0045
            r6 = 58
            int r6 = r3.indexOf(r6)     // Catch:{ all -> 0x0076 }
            if (r6 != r8) goto L_0x0045
            r6 = r11
            com.mysql.jdbc.ConnectionImpl r6 = (com.mysql.jdbc.ConnectionImpl) r6     // Catch:{ all -> 0x0076 }
            int r6 = r6.origPortToConnectTo     // Catch:{ all -> 0x0076 }
            int r8 = r10.origPortToConnectTo     // Catch:{ all -> 0x0076 }
            if (r6 != r8) goto L_0x0043
            r6 = r7
            goto L_0x0044
        L_0x0043:
            r6 = r1
        L_0x0044:
            r2 = r6
        L_0x0045:
            if (r2 == 0) goto L_0x0058
            java.lang.String r6 = r10.origDatabaseToConnectTo     // Catch:{ all -> 0x0076 }
            boolean r6 = nullSafeCompare(r4, r6)     // Catch:{ all -> 0x0076 }
            if (r6 == 0) goto L_0x0057
            java.lang.String r6 = r10.database     // Catch:{ all -> 0x0076 }
            boolean r6 = nullSafeCompare(r5, r6)     // Catch:{ all -> 0x0076 }
            if (r6 != 0) goto L_0x0058
        L_0x0057:
            r2 = 0
        L_0x0058:
            if (r2 == 0) goto L_0x005c
            monitor-exit(r0)     // Catch:{ all -> 0x0076 }
            return r7
        L_0x005c:
            r6 = r11
            com.mysql.jdbc.ConnectionImpl r6 = (com.mysql.jdbc.ConnectionImpl) r6     // Catch:{ all -> 0x0076 }
            java.lang.String r6 = r6.getResourceId()     // Catch:{ all -> 0x0076 }
            java.lang.String r8 = r10.getResourceId()     // Catch:{ all -> 0x0076 }
            if (r6 != 0) goto L_0x006b
            if (r8 == 0) goto L_0x0074
        L_0x006b:
            boolean r9 = nullSafeCompare(r6, r8)     // Catch:{ all -> 0x0076 }
            r2 = r9
            if (r2 == 0) goto L_0x0074
            monitor-exit(r0)     // Catch:{ all -> 0x0076 }
            return r7
        L_0x0074:
            monitor-exit(r0)     // Catch:{ all -> 0x0076 }
            return r1
        L_0x0076:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0076 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ConnectionImpl.isSameResource(com.mysql.jdbc.Connection):boolean");
    }

    public boolean isServerTzUTC() {
        return this.isServerTzUTC;
    }

    private void createConfigCacheIfNeeded() throws SQLException {
        synchronized (getConnectionMutex()) {
            if (this.serverConfigCache == null) {
                try {
                    this.serverConfigCache = ((CacheAdapterFactory) Class.forName(getServerConfigCacheFactory()).newInstance()).getInstance(this, this.myURL, Integer.MAX_VALUE, Integer.MAX_VALUE, this.props);
                    ExceptionInterceptor evictOnCommsError = new ExceptionInterceptor() {
                        public void init(Connection conn, Properties config) throws SQLException {
                        }

                        public void destroy() {
                        }

                        public SQLException interceptException(SQLException sqlEx, Connection conn) {
                            if (sqlEx.getSQLState() == null || !sqlEx.getSQLState().startsWith("08")) {
                                return null;
                            }
                            ConnectionImpl.this.serverConfigCache.invalidate(ConnectionImpl.this.getURL());
                            return null;
                        }
                    };
                    ExceptionInterceptor exceptionInterceptor2 = this.exceptionInterceptor;
                    if (exceptionInterceptor2 == null) {
                        this.exceptionInterceptor = evictOnCommsError;
                    } else {
                        ((ExceptionInterceptorChain) exceptionInterceptor2).addRingZero(evictOnCommsError);
                    }
                } catch (ClassNotFoundException e) {
                    SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.CantFindCacheFactory", new Object[]{getParseInfoCacheFactory(), "parseInfoCacheFactory"}), getExceptionInterceptor());
                    sqlEx.initCause(e);
                    throw sqlEx;
                } catch (InstantiationException e2) {
                    SQLException sqlEx2 = SQLError.createSQLException(Messages.getString("Connection.CantLoadCacheFactory", new Object[]{getParseInfoCacheFactory(), "parseInfoCacheFactory"}), getExceptionInterceptor());
                    sqlEx2.initCause(e2);
                    throw sqlEx2;
                } catch (IllegalAccessException e3) {
                    SQLException sqlEx3 = SQLError.createSQLException(Messages.getString("Connection.CantLoadCacheFactory", new Object[]{getParseInfoCacheFactory(), "parseInfoCacheFactory"}), getExceptionInterceptor());
                    sqlEx3.initCause(e3);
                    throw sqlEx3;
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:116:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00be A[Catch:{ SQLException -> 0x01d0, all -> 0x01ce, SQLException -> 0x021b }] */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x017c A[Catch:{ SQLException -> 0x01d0, all -> 0x01ce, SQLException -> 0x021b }] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0198 A[Catch:{ SQLException -> 0x01d0, all -> 0x01ce, SQLException -> 0x021b }] */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x01ea A[Catch:{ SQLException -> 0x01d0, all -> 0x01ce, SQLException -> 0x021b }] */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0203 A[SYNTHETIC, Splitter:B:85:0x0203] */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x020a A[SYNTHETIC, Splitter:B:89:0x020a] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void loadServerVariables() throws java.sql.SQLException {
        /*
            r12 = this;
            boolean r0 = r12.getCacheServerConfiguration()
            java.lang.String r1 = "server_version_string"
            if (r0 == 0) goto L_0x0041
            r12.createConfigCacheIfNeeded()
            com.mysql.jdbc.CacheAdapter<java.lang.String, java.util.Map<java.lang.String, java.lang.String>> r0 = r12.serverConfigCache
            java.lang.String r2 = r12.getURL()
            java.lang.Object r0 = r0.get(r2)
            java.util.Map r0 = (java.util.Map) r0
            if (r0 == 0) goto L_0x0041
            java.lang.Object r2 = r0.get(r1)
            java.lang.String r2 = (java.lang.String) r2
            if (r2 == 0) goto L_0x0038
            com.mysql.jdbc.MysqlIO r3 = r12.f4io
            java.lang.String r3 = r3.getServerVersion()
            if (r3 == 0) goto L_0x0038
            com.mysql.jdbc.MysqlIO r3 = r12.f4io
            java.lang.String r3 = r3.getServerVersion()
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L_0x0038
            r12.serverVariables = r0
            return
        L_0x0038:
            com.mysql.jdbc.CacheAdapter<java.lang.String, java.util.Map<java.lang.String, java.lang.String>> r3 = r12.serverConfigCache
            java.lang.String r4 = r12.getURL()
            r3.invalidate(r4)
        L_0x0041:
            r0 = 0
            r2 = 0
            java.sql.Statement r3 = r12.getMetadataSafeStatement()     // Catch:{ SQLException -> 0x021b }
            r0 = r3
            java.sql.DatabaseMetaData r3 = r12.dbmd     // Catch:{ SQLException -> 0x021b }
            java.lang.String r3 = r3.getDriverVersion()     // Catch:{ SQLException -> 0x021b }
            if (r3 == 0) goto L_0x0082
            r4 = 42
            int r5 = r3.indexOf(r4)     // Catch:{ SQLException -> 0x021b }
            r6 = -1
            if (r5 == r6) goto L_0x0082
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ SQLException -> 0x021b }
            int r6 = r3.length()     // Catch:{ SQLException -> 0x021b }
            int r6 = r6 + 10
            r5.<init>(r6)     // Catch:{ SQLException -> 0x021b }
            r6 = 0
        L_0x0065:
            int r7 = r3.length()     // Catch:{ SQLException -> 0x021b }
            if (r6 >= r7) goto L_0x007d
            char r7 = r3.charAt(r6)     // Catch:{ SQLException -> 0x021b }
            if (r7 != r4) goto L_0x0077
            java.lang.String r8 = "[star]"
            r5.append(r8)     // Catch:{ SQLException -> 0x021b }
            goto L_0x007a
        L_0x0077:
            r5.append(r7)     // Catch:{ SQLException -> 0x021b }
        L_0x007a:
            int r6 = r6 + 1
            goto L_0x0065
        L_0x007d:
            java.lang.String r4 = r5.toString()     // Catch:{ SQLException -> 0x021b }
            r3 = r4
        L_0x0082:
            boolean r4 = r12.getParanoid()     // Catch:{ SQLException -> 0x021b }
            if (r4 != 0) goto L_0x00a5
            if (r3 != 0) goto L_0x008b
            goto L_0x00a5
        L_0x008b:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ SQLException -> 0x021b }
            r4.<init>()     // Catch:{ SQLException -> 0x021b }
            java.lang.String r5 = "/* "
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch:{ SQLException -> 0x021b }
            java.lang.StringBuilder r4 = r4.append(r3)     // Catch:{ SQLException -> 0x021b }
            java.lang.String r5 = " */"
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch:{ SQLException -> 0x021b }
            java.lang.String r4 = r4.toString()     // Catch:{ SQLException -> 0x021b }
            goto L_0x00a7
        L_0x00a5:
            java.lang.String r4 = ""
        L_0x00a7:
            java.util.HashMap r5 = new java.util.HashMap     // Catch:{ SQLException -> 0x021b }
            r5.<init>()     // Catch:{ SQLException -> 0x021b }
            r12.serverVariables = r5     // Catch:{ SQLException -> 0x021b }
            boolean r5 = r12.getJdbcCompliantTruncation()     // Catch:{ SQLException -> 0x021b }
            r6 = 0
            r12.setJdbcCompliantTruncation(r6)     // Catch:{ SQLException -> 0x021b }
            r7 = 1
            r8 = 5
            boolean r9 = r12.versionMeetsMinimum(r8, r7, r6)     // Catch:{ SQLException -> 0x01d0 }
            if (r9 == 0) goto L_0x0198
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ SQLException -> 0x01d0 }
            r7.<init>(r4)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r9 = "SELECT"
            java.lang.StringBuilder r7 = r7.append(r9)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r9 = "  @@session.auto_increment_increment AS auto_increment_increment"
            r7.append(r9)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r9 = ", @@character_set_client AS character_set_client"
            r7.append(r9)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r9 = ", @@character_set_connection AS character_set_connection"
            r7.append(r9)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r9 = ", @@character_set_results AS character_set_results"
            r7.append(r9)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r9 = ", @@character_set_server AS character_set_server"
            r7.append(r9)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r9 = ", @@collation_server AS collation_server"
            r7.append(r9)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r9 = ", @@collation_connection AS collation_connection"
            r7.append(r9)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r9 = ", @@init_connect AS init_connect"
            r7.append(r9)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r9 = ", @@interactive_timeout AS interactive_timeout"
            r7.append(r9)     // Catch:{ SQLException -> 0x01d0 }
            boolean r9 = r12.versionMeetsMinimum(r8, r8, r6)     // Catch:{ SQLException -> 0x01d0 }
            if (r9 != 0) goto L_0x0101
            java.lang.String r9 = ", @@language AS language"
            r7.append(r9)     // Catch:{ SQLException -> 0x01d0 }
        L_0x0101:
            java.lang.String r9 = ", @@license AS license"
            r7.append(r9)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r9 = ", @@lower_case_table_names AS lower_case_table_names"
            r7.append(r9)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r9 = ", @@max_allowed_packet AS max_allowed_packet"
            r7.append(r9)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r9 = ", @@net_buffer_length AS net_buffer_length"
            r7.append(r9)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r9 = ", @@net_write_timeout AS net_write_timeout"
            r7.append(r9)     // Catch:{ SQLException -> 0x01d0 }
            boolean r9 = r12.versionMeetsMinimum(r8, r8, r6)     // Catch:{ SQLException -> 0x01d0 }
            if (r9 == 0) goto L_0x0125
            java.lang.String r9 = ", @@performance_schema AS performance_schema"
            r7.append(r9)     // Catch:{ SQLException -> 0x01d0 }
        L_0x0125:
            r9 = 3
            r10 = 8
            boolean r11 = r12.versionMeetsMinimum(r10, r6, r9)     // Catch:{ SQLException -> 0x01d0 }
            if (r11 != 0) goto L_0x0138
            java.lang.String r11 = ", @@query_cache_size AS query_cache_size"
            r7.append(r11)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r11 = ", @@query_cache_type AS query_cache_type"
            r7.append(r11)     // Catch:{ SQLException -> 0x01d0 }
        L_0x0138:
            java.lang.String r11 = ", @@sql_mode AS sql_mode"
            r7.append(r11)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r11 = ", @@system_time_zone AS system_time_zone"
            r7.append(r11)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r11 = ", @@time_zone AS time_zone"
            r7.append(r11)     // Catch:{ SQLException -> 0x01d0 }
            boolean r9 = r12.versionMeetsMinimum(r10, r6, r9)     // Catch:{ SQLException -> 0x01d0 }
            if (r9 != 0) goto L_0x0163
            r9 = 7
            r11 = 20
            boolean r8 = r12.versionMeetsMinimum(r8, r9, r11)     // Catch:{ SQLException -> 0x01d0 }
            if (r8 == 0) goto L_0x015d
            boolean r6 = r12.versionMeetsMinimum(r10, r6, r6)     // Catch:{ SQLException -> 0x01d0 }
            if (r6 != 0) goto L_0x015d
            goto L_0x0163
        L_0x015d:
            java.lang.String r6 = ", @@tx_isolation AS transaction_isolation"
            r7.append(r6)     // Catch:{ SQLException -> 0x01d0 }
            goto L_0x0168
        L_0x0163:
            java.lang.String r6 = ", @@transaction_isolation AS transaction_isolation"
            r7.append(r6)     // Catch:{ SQLException -> 0x01d0 }
        L_0x0168:
            java.lang.String r6 = ", @@wait_timeout AS wait_timeout"
            r7.append(r6)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r6 = r7.toString()     // Catch:{ SQLException -> 0x01d0 }
            java.sql.ResultSet r6 = r0.executeQuery(r6)     // Catch:{ SQLException -> 0x01d0 }
            r2 = r6
            boolean r6 = r2.next()     // Catch:{ SQLException -> 0x01d0 }
            if (r6 == 0) goto L_0x0197
            java.sql.ResultSetMetaData r6 = r2.getMetaData()     // Catch:{ SQLException -> 0x01d0 }
            r8 = 1
        L_0x0181:
            int r9 = r6.getColumnCount()     // Catch:{ SQLException -> 0x01d0 }
            if (r8 > r9) goto L_0x0197
            java.util.Map<java.lang.String, java.lang.String> r9 = r12.serverVariables     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r10 = r6.getColumnLabel(r8)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r11 = r2.getString(r8)     // Catch:{ SQLException -> 0x01d0 }
            r9.put(r10, r11)     // Catch:{ SQLException -> 0x01d0 }
            int r8 = r8 + 1
            goto L_0x0181
        L_0x0197:
            goto L_0x01c5
        L_0x0198:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ SQLException -> 0x01d0 }
            r6.<init>()     // Catch:{ SQLException -> 0x01d0 }
            java.lang.StringBuilder r6 = r6.append(r4)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r8 = "SHOW VARIABLES"
            java.lang.StringBuilder r6 = r6.append(r8)     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r6 = r6.toString()     // Catch:{ SQLException -> 0x01d0 }
            java.sql.ResultSet r6 = r0.executeQuery(r6)     // Catch:{ SQLException -> 0x01d0 }
            r2 = r6
        L_0x01b0:
            boolean r6 = r2.next()     // Catch:{ SQLException -> 0x01d0 }
            if (r6 == 0) goto L_0x01c5
            java.util.Map<java.lang.String, java.lang.String> r6 = r12.serverVariables     // Catch:{ SQLException -> 0x01d0 }
            java.lang.String r8 = r2.getString(r7)     // Catch:{ SQLException -> 0x01d0 }
            r9 = 2
            java.lang.String r9 = r2.getString(r9)     // Catch:{ SQLException -> 0x01d0 }
            r6.put(r8, r9)     // Catch:{ SQLException -> 0x01d0 }
            goto L_0x01b0
        L_0x01c5:
            r2.close()     // Catch:{ SQLException -> 0x01d0 }
            r2 = 0
            r12.setJdbcCompliantTruncation(r5)     // Catch:{ SQLException -> 0x021b }
            goto L_0x01e3
        L_0x01ce:
            r1 = move-exception
            goto L_0x0214
        L_0x01d0:
            r6 = move-exception
            int r7 = r6.getErrorCode()     // Catch:{ all -> 0x01ce }
            r8 = 1820(0x71c, float:2.55E-42)
            if (r7 != r8) goto L_0x0212
            boolean r7 = r12.getDisconnectOnExpiredPasswords()     // Catch:{ all -> 0x01ce }
            if (r7 != 0) goto L_0x0212
            r12.setJdbcCompliantTruncation(r5)     // Catch:{ SQLException -> 0x021b }
        L_0x01e3:
            boolean r6 = r12.getCacheServerConfiguration()     // Catch:{ SQLException -> 0x021b }
            if (r6 == 0) goto L_0x0200
            java.util.Map<java.lang.String, java.lang.String> r6 = r12.serverVariables     // Catch:{ SQLException -> 0x021b }
            com.mysql.jdbc.MysqlIO r7 = r12.f4io     // Catch:{ SQLException -> 0x021b }
            java.lang.String r7 = r7.getServerVersion()     // Catch:{ SQLException -> 0x021b }
            r6.put(r1, r7)     // Catch:{ SQLException -> 0x021b }
            com.mysql.jdbc.CacheAdapter<java.lang.String, java.util.Map<java.lang.String, java.lang.String>> r1 = r12.serverConfigCache     // Catch:{ SQLException -> 0x021b }
            java.lang.String r6 = r12.getURL()     // Catch:{ SQLException -> 0x021b }
            java.util.Map<java.lang.String, java.lang.String> r7 = r12.serverVariables     // Catch:{ SQLException -> 0x021b }
            r1.put(r6, r7)     // Catch:{ SQLException -> 0x021b }
        L_0x0200:
            if (r2 == 0) goto L_0x0208
            r2.close()     // Catch:{ SQLException -> 0x0207 }
            goto L_0x0208
        L_0x0207:
            r1 = move-exception
        L_0x0208:
            if (r0 == 0) goto L_0x020f
            r0.close()     // Catch:{ SQLException -> 0x020e }
            goto L_0x020f
        L_0x020e:
            r1 = move-exception
        L_0x020f:
            return
        L_0x0212:
            throw r6     // Catch:{ all -> 0x01ce }
        L_0x0214:
            r12.setJdbcCompliantTruncation(r5)     // Catch:{ SQLException -> 0x021b }
            throw r1     // Catch:{ SQLException -> 0x021b }
        L_0x0219:
            r1 = move-exception
            goto L_0x021e
        L_0x021b:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0219 }
        L_0x021e:
            if (r2 == 0) goto L_0x0225
            r2.close()     // Catch:{ SQLException -> 0x0224 }
            goto L_0x0225
        L_0x0224:
            r3 = move-exception
        L_0x0225:
            if (r0 == 0) goto L_0x022c
            r0.close()     // Catch:{ SQLException -> 0x022b }
            goto L_0x022c
        L_0x022b:
            r3 = move-exception
        L_0x022c:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ConnectionImpl.loadServerVariables():void");
    }

    public int getAutoIncrementIncrement() {
        return this.autoIncrementIncrement;
    }

    public boolean lowerCaseTableNames() {
        return this.lowerCaseTableNames;
    }

    public String nativeSQL(String sql) throws SQLException {
        if (sql == null) {
            return null;
        }
        Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, serverSupportsConvertFn(), getMultiHostSafeProxy());
        if (escapedSqlResult instanceof String) {
            return (String) escapedSqlResult;
        }
        return ((EscapeProcessorResult) escapedSqlResult).escapedSql;
    }

    private CallableStatement parseCallableStatement(String sql) throws SQLException {
        String parsedSql;
        boolean isFunctionCall;
        Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, serverSupportsConvertFn(), getMultiHostSafeProxy());
        if (escapedSqlResult instanceof EscapeProcessorResult) {
            parsedSql = ((EscapeProcessorResult) escapedSqlResult).escapedSql;
            isFunctionCall = ((EscapeProcessorResult) escapedSqlResult).callingStoredFunction;
        } else {
            parsedSql = (String) escapedSqlResult;
            isFunctionCall = false;
        }
        return CallableStatement.getInstance(getMultiHostSafeProxy(), parsedSql, this.database, isFunctionCall);
    }

    public boolean parserKnowsUnicode() {
        return this.parserKnowsUnicode;
    }

    public void ping() throws SQLException {
        pingInternal(true, 0);
    }

    public void pingInternal(boolean checkForClosedConnection, int timeoutMillis) throws SQLException {
        if (checkForClosedConnection) {
            checkClosed();
        }
        long pingMillisLifetime = (long) getSelfDestructOnPingSecondsLifetime();
        int pingMaxOperations = getSelfDestructOnPingMaxOperations();
        if ((pingMillisLifetime <= 0 || System.currentTimeMillis() - this.connectionCreationTimeMillis <= pingMillisLifetime) && (pingMaxOperations <= 0 || pingMaxOperations > this.f4io.getCommandCount())) {
            this.f4io.sendCommand(14, (String) null, (Buffer) null, false, (String) null, timeoutMillis);
        } else {
            close();
            throw SQLError.createSQLException(Messages.getString("Connection.exceededConnectionLifetime"), SQLError.SQL_STATE_COMMUNICATION_LINK_FAILURE, getExceptionInterceptor());
        }
    }

    public java.sql.CallableStatement prepareCall(String sql) throws SQLException {
        return prepareCall(sql, 1003, 1007);
    }

    public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        CallableStatement cStmt;
        CallableStatement.CallableStatementParamInfo cachedParamInfo;
        if (versionMeetsMinimum(5, 0, 0)) {
            if (!getCacheCallableStatements()) {
                cStmt = parseCallableStatement(sql);
            } else {
                synchronized (this.parsedCallableStatementCache) {
                    CompoundCacheKey key = new CompoundCacheKey(getCatalog(), sql);
                    CallableStatement.CallableStatementParamInfo cachedParamInfo2 = (CallableStatement.CallableStatementParamInfo) this.parsedCallableStatementCache.get(key);
                    if (cachedParamInfo2 != null) {
                        cStmt = CallableStatement.getInstance(getMultiHostSafeProxy(), cachedParamInfo2);
                    } else {
                        cStmt = parseCallableStatement(sql);
                        synchronized (cStmt) {
                            cachedParamInfo = cStmt.paramInfo;
                        }
                        this.parsedCallableStatementCache.put(key, cachedParamInfo);
                    }
                }
            }
            cStmt.setResultSetType(resultSetType);
            cStmt.setResultSetConcurrency(resultSetConcurrency);
            return cStmt;
        }
        throw SQLError.createSQLException("Callable statements not supported.", SQLError.SQL_STATE_DRIVER_NOT_CAPABLE, getExceptionInterceptor());
    }

    public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        if (!getPedantic() || resultSetHoldability == 1) {
            return (CallableStatement) prepareCall(sql, resultSetType, resultSetConcurrency);
        }
        throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
    }

    public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException {
        return prepareStatement(sql, 1003, 1007);
    }

    public java.sql.PreparedStatement prepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
        java.sql.PreparedStatement pStmt = prepareStatement(sql);
        PreparedStatement preparedStatement = (PreparedStatement) pStmt;
        boolean z = true;
        if (autoGenKeyIndex != 1) {
            z = false;
        }
        preparedStatement.setRetrieveGeneratedKeys(z);
        return pStmt;
    }

    public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        PreparedStatement pStmt;
        synchronized (getConnectionMutex()) {
            checkClosed();
            boolean canServerPrepare = true;
            String nativeSql = getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql;
            if (this.useServerPreparedStmts && getEmulateUnsupportedPstmts()) {
                canServerPrepare = canHandleAsServerPreparedStatement(nativeSql);
            }
            if (!this.useServerPreparedStmts || !canServerPrepare) {
                pStmt = (PreparedStatement) clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
            } else if (getCachePreparedStatements()) {
                synchronized (this.serverSideStatementCache) {
                    pStmt = (PreparedStatement) this.serverSideStatementCache.remove(new CompoundCacheKey(this.database, sql));
                    if (pStmt != null) {
                        ((ServerPreparedStatement) pStmt).setClosed(false);
                        pStmt.clearParameters();
                    }
                    if (pStmt == null) {
                        try {
                            pStmt = ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database, resultSetType, resultSetConcurrency);
                            if (sql.length() < getPreparedStatementCacheSqlLimit()) {
                                ((ServerPreparedStatement) pStmt).isCached = true;
                            }
                            pStmt.setResultSetType(resultSetType);
                            pStmt.setResultSetConcurrency(resultSetConcurrency);
                        } catch (SQLException sqlEx) {
                            if (getEmulateUnsupportedPstmts()) {
                                pStmt = (PreparedStatement) clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
                                if (sql.length() < getPreparedStatementCacheSqlLimit()) {
                                    this.serverSideStatementCheckCache.put(sql, Boolean.FALSE);
                                }
                            } else {
                                throw sqlEx;
                            }
                        }
                    }
                }
            } else {
                try {
                    pStmt = ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), nativeSql, this.database, resultSetType, resultSetConcurrency);
                    pStmt.setResultSetType(resultSetType);
                    pStmt.setResultSetConcurrency(resultSetConcurrency);
                } catch (SQLException sqlEx2) {
                    if (getEmulateUnsupportedPstmts()) {
                        pStmt = (PreparedStatement) clientPrepareStatement(nativeSql, resultSetType, resultSetConcurrency, false);
                    } else {
                        throw sqlEx2;
                    }
                }
            }
        }
        return pStmt;
    }

    public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        if (!getPedantic() || resultSetHoldability == 1) {
            return prepareStatement(sql, resultSetType, resultSetConcurrency);
        }
        throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
    }

    public java.sql.PreparedStatement prepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
        java.sql.PreparedStatement pStmt = prepareStatement(sql);
        ((PreparedStatement) pStmt).setRetrieveGeneratedKeys(autoGenKeyIndexes != null && autoGenKeyIndexes.length > 0);
        return pStmt;
    }

    public java.sql.PreparedStatement prepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
        java.sql.PreparedStatement pStmt = prepareStatement(sql);
        ((PreparedStatement) pStmt).setRetrieveGeneratedKeys(autoGenKeyColNames != null && autoGenKeyColNames.length > 0);
        return pStmt;
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
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processExcHandler(RegionMaker.java:1043)
        	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:975)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
        */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00dd  */
    public void realClose(boolean r16, boolean r17, boolean r18, java.lang.Throwable r19) throws java.sql.SQLException {
        /*
            r15 = this;
            r10 = r15
            r1 = 0
            boolean r0 = r15.isClosed()
            if (r0 == 0) goto L_0x0009
            return
        L_0x0009:
            r11 = r19
            r10.forceClosedReason = r11
            r12 = 1
            r13 = 0
            if (r18 != 0) goto L_0x007d
            boolean r0 = r15.getAutoCommit()     // Catch:{ all -> 0x00d3 }
            if (r0 != 0) goto L_0x0023
            if (r17 == 0) goto L_0x0023
            r15.rollback()     // Catch:{ SQLException -> 0x001d }
            goto L_0x0023
        L_0x001d:
            r0 = move-exception
            r2 = r0
            r0 = r2
            r1 = r0
            r14 = r1
            goto L_0x0024
        L_0x0023:
            r14 = r1
        L_0x0024:
            boolean r0 = r15.getGatherPerfMetrics()     // Catch:{ all -> 0x007a }
            if (r0 == 0) goto L_0x002d
            r15.reportMetrics()     // Catch:{ all -> 0x007a }
        L_0x002d:
            boolean r0 = r15.getUseUsageAdvisor()     // Catch:{ all -> 0x007a }
            if (r0 == 0) goto L_0x0066
            if (r16 != 0) goto L_0x0047
            java.lang.String r9 = "Connection implicitly closed by Driver. You should call Connection.close() from your code to free resources more efficiently and avoid resource leaks."
            com.mysql.jdbc.profiler.ProfilerEventHandler r1 = r10.eventSink     // Catch:{ all -> 0x007a }
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            java.lang.Throwable r8 = new java.lang.Throwable     // Catch:{ all -> 0x007a }
            r8.<init>()     // Catch:{ all -> 0x007a }
            r3 = r15
            r1.processEvent(r2, r3, r4, r5, r6, r8, r9)     // Catch:{ all -> 0x007a }
        L_0x0047:
            long r0 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x007a }
            long r2 = r10.connectionCreationTimeMillis     // Catch:{ all -> 0x007a }
            long r0 = r0 - r2
            r2 = 500(0x1f4, double:2.47E-321)
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x0066
            java.lang.String r9 = "Connection lifetime of < .5 seconds. You might be un-necessarily creating short-lived connections and should investigate connection pooling to be more efficient."
            com.mysql.jdbc.profiler.ProfilerEventHandler r1 = r10.eventSink     // Catch:{ all -> 0x007a }
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            java.lang.Throwable r8 = new java.lang.Throwable     // Catch:{ all -> 0x007a }
            r8.<init>()     // Catch:{ all -> 0x007a }
            r3 = r15
            r1.processEvent(r2, r3, r4, r5, r6, r8, r9)     // Catch:{ all -> 0x007a }
        L_0x0066:
            r15.closeAllOpenStatements()     // Catch:{ SQLException -> 0x006b }
            r1 = r14
            goto L_0x0070
        L_0x006b:
            r0 = move-exception
            r1 = r0
            r0 = r1
        L_0x0070:
            com.mysql.jdbc.MysqlIO r0 = r10.f4io     // Catch:{ all -> 0x00d3 }
            if (r0 == 0) goto L_0x0082
            r0.quit()     // Catch:{ Exception -> 0x0078 }
            goto L_0x0079
        L_0x0078:
            r0 = move-exception
        L_0x0079:
            goto L_0x0082
        L_0x007a:
            r0 = move-exception
            r1 = r14
            goto L_0x00d4
        L_0x007d:
            com.mysql.jdbc.MysqlIO r0 = r10.f4io     // Catch:{ all -> 0x00d3 }
            r0.forceClose()     // Catch:{ all -> 0x00d3 }
        L_0x0082:
            java.util.List<com.mysql.jdbc.StatementInterceptorV2> r0 = r10.statementInterceptors     // Catch:{ all -> 0x00d3 }
            if (r0 == 0) goto L_0x009d
            r0 = 0
        L_0x0087:
            java.util.List<com.mysql.jdbc.StatementInterceptorV2> r2 = r10.statementInterceptors     // Catch:{ all -> 0x00d3 }
            int r2 = r2.size()     // Catch:{ all -> 0x00d3 }
            if (r0 >= r2) goto L_0x009d
            java.util.List<com.mysql.jdbc.StatementInterceptorV2> r2 = r10.statementInterceptors     // Catch:{ all -> 0x00d3 }
            java.lang.Object r2 = r2.get(r0)     // Catch:{ all -> 0x00d3 }
            com.mysql.jdbc.StatementInterceptorV2 r2 = (com.mysql.jdbc.StatementInterceptorV2) r2     // Catch:{ all -> 0x00d3 }
            r2.destroy()     // Catch:{ all -> 0x00d3 }
            int r0 = r0 + 1
            goto L_0x0087
        L_0x009d:
            com.mysql.jdbc.ExceptionInterceptor r0 = r10.exceptionInterceptor     // Catch:{ all -> 0x00d3 }
            if (r0 == 0) goto L_0x00a4
            r0.destroy()     // Catch:{ all -> 0x00d3 }
        L_0x00a4:
            java.util.concurrent.CopyOnWriteArrayList<com.mysql.jdbc.Statement> r0 = r10.openStatements
            r0.clear()
            com.mysql.jdbc.MysqlIO r0 = r10.f4io
            if (r0 == 0) goto L_0x00b3
            r0.releaseResources()
            r10.f4io = r13
        L_0x00b3:
            r10.statementInterceptors = r13
            r10.exceptionInterceptor = r13
            com.mysql.jdbc.ProfilerEventHandlerFactory.removeInstance(r15)
            r10.eventSink = r13
            java.lang.Object r2 = r15.getConnectionMutex()
            monitor-enter(r2)
            java.util.Timer r0 = r10.cancelTimer     // Catch:{ all -> 0x00d0 }
            if (r0 == 0) goto L_0x00c8
            r0.cancel()     // Catch:{ all -> 0x00d0 }
        L_0x00c8:
            monitor-exit(r2)     // Catch:{ all -> 0x00d0 }
            r10.isClosed = r12
            if (r1 != 0) goto L_0x00cf
            return
        L_0x00cf:
            throw r1
        L_0x00d0:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x00d0 }
            throw r0
        L_0x00d3:
            r0 = move-exception
        L_0x00d4:
            java.util.concurrent.CopyOnWriteArrayList<com.mysql.jdbc.Statement> r2 = r10.openStatements
            r2.clear()
            com.mysql.jdbc.MysqlIO r2 = r10.f4io
            if (r2 == 0) goto L_0x00e2
            r2.releaseResources()
            r10.f4io = r13
        L_0x00e2:
            r10.statementInterceptors = r13
            r10.exceptionInterceptor = r13
            com.mysql.jdbc.ProfilerEventHandlerFactory.removeInstance(r15)
            r10.eventSink = r13
            java.lang.Object r2 = r15.getConnectionMutex()
            monitor-enter(r2)
            java.util.Timer r3 = r10.cancelTimer     // Catch:{ all -> 0x00fb }
            if (r3 == 0) goto L_0x00f7
            r3.cancel()     // Catch:{ all -> 0x00fb }
        L_0x00f7:
            monitor-exit(r2)     // Catch:{ all -> 0x00fb }
            r10.isClosed = r12
            throw r0
        L_0x00fb:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x00fb }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ConnectionImpl.realClose(boolean, boolean, boolean, java.lang.Throwable):void");
    }

    public void recachePreparedStatement(ServerPreparedStatement pstmt) throws SQLException {
        synchronized (getConnectionMutex()) {
            if (getCachePreparedStatements() && pstmt.isPoolable()) {
                synchronized (this.serverSideStatementCache) {
                    Object oldServerPrepStmt = this.serverSideStatementCache.put(new CompoundCacheKey(pstmt.currentCatalog, pstmt.originalSql), pstmt);
                    if (!(oldServerPrepStmt == null || oldServerPrepStmt == pstmt)) {
                        ((ServerPreparedStatement) oldServerPrepStmt).isCached = false;
                        ((ServerPreparedStatement) oldServerPrepStmt).setClosed(false);
                        ((ServerPreparedStatement) oldServerPrepStmt).realClose(true, true);
                    }
                }
            }
        }
    }

    public void decachePreparedStatement(ServerPreparedStatement pstmt) throws SQLException {
        synchronized (getConnectionMutex()) {
            if (getCachePreparedStatements() && pstmt.isPoolable()) {
                synchronized (this.serverSideStatementCache) {
                    this.serverSideStatementCache.remove(new CompoundCacheKey(pstmt.currentCatalog, pstmt.originalSql));
                }
            }
        }
    }

    public void registerQueryExecutionTime(long queryTimeMs) {
        if (queryTimeMs > this.longestQueryTimeMs) {
            this.longestQueryTimeMs = queryTimeMs;
            repartitionPerformanceHistogram();
        }
        addToPerformanceHistogram(queryTimeMs, 1);
        if (queryTimeMs < this.shortestQueryTimeMs) {
            this.shortestQueryTimeMs = queryTimeMs == 0 ? 1 : queryTimeMs;
        }
        this.numberOfQueriesIssued++;
        this.totalQueryTimeMs += (double) queryTimeMs;
    }

    public void registerStatement(Statement stmt) {
        this.openStatements.addIfAbsent(stmt);
    }

    public void releaseSavepoint(Savepoint arg0) throws SQLException {
    }

    private void repartitionHistogram(int[] histCounts, long[] histBreakpoints, long currentLowerBound, long currentUpperBound) {
        int[] iArr = histCounts;
        long[] jArr = histBreakpoints;
        if (this.oldHistCounts == null) {
            this.oldHistCounts = new int[iArr.length];
            this.oldHistBreakpoints = new long[jArr.length];
        }
        System.arraycopy(histCounts, 0, this.oldHistCounts, 0, iArr.length);
        System.arraycopy(jArr, 0, this.oldHistBreakpoints, 0, jArr.length);
        createInitialHistogram(histBreakpoints, currentLowerBound, currentUpperBound);
        for (int i = 0; i < 20; i++) {
            addToHistogram(histCounts, histBreakpoints, this.oldHistBreakpoints[i], this.oldHistCounts[i], currentLowerBound, currentUpperBound);
        }
    }

    private void repartitionPerformanceHistogram() {
        checkAndCreatePerformanceHistogram();
        int[] iArr = this.perfMetricsHistCounts;
        long[] jArr = this.perfMetricsHistBreakpoints;
        long j = this.shortestQueryTimeMs;
        if (j == Long.MAX_VALUE) {
            j = 0;
        }
        repartitionHistogram(iArr, jArr, j, this.longestQueryTimeMs);
    }

    private void repartitionTablesAccessedHistogram() {
        checkAndCreateTablesAccessedHistogram();
        int[] iArr = this.numTablesMetricsHistCounts;
        long[] jArr = this.numTablesMetricsHistBreakpoints;
        long j = this.minimumNumberTablesAccessed;
        if (j == Long.MAX_VALUE) {
            j = 0;
        }
        repartitionHistogram(iArr, jArr, j, this.maximumNumberTablesAccessed);
    }

    private void reportMetrics() {
        String str;
        String str2;
        if (getGatherPerformanceMetrics()) {
            StringBuilder logMessage = new StringBuilder(256);
            logMessage.append("** Performance Metrics Report **\n");
            logMessage.append("\nLongest reported query: " + this.longestQueryTimeMs + " ms");
            logMessage.append("\nShortest reported query: " + this.shortestQueryTimeMs + " ms");
            logMessage.append("\nAverage query execution time: " + (this.totalQueryTimeMs / ((double) this.numberOfQueriesIssued)) + " ms");
            logMessage.append("\nNumber of statements executed: " + this.numberOfQueriesIssued);
            logMessage.append("\nNumber of result sets created: " + this.numberOfResultSetsCreated);
            logMessage.append("\nNumber of statements prepared: " + this.numberOfPrepares);
            logMessage.append("\nNumber of prepared statement executions: " + this.numberOfPreparedExecutes);
            String str3 = " and ";
            int i = 19;
            if (this.perfMetricsHistBreakpoints != null) {
                logMessage.append("\n\n\tTiming Histogram:\n");
                int highestCount = Integer.MIN_VALUE;
                for (int i2 = 0; i2 < 20; i2++) {
                    int[] iArr = this.perfMetricsHistCounts;
                    if (iArr[i2] > highestCount) {
                        highestCount = iArr[i2];
                    }
                }
                if (highestCount == 0) {
                    highestCount = 1;
                }
                int i3 = 0;
                while (true) {
                    if (i3 >= i) {
                        str2 = str3;
                        break;
                    }
                    if (i3 == 0) {
                        logMessage.append("\n\tless than " + this.perfMetricsHistBreakpoints[i3 + 1] + " ms: \t" + this.perfMetricsHistCounts[i3]);
                    } else {
                        logMessage.append("\n\tbetween " + this.perfMetricsHistBreakpoints[i3] + str3 + this.perfMetricsHistBreakpoints[i3 + 1] + " ms: \t" + this.perfMetricsHistCounts[i3]);
                    }
                    logMessage.append("\t");
                    str2 = str3;
                    int numPointsToGraph = (int) (((double) 20) * (((double) this.perfMetricsHistCounts[i3]) / ((double) highestCount)));
                    for (int j = 0; j < numPointsToGraph; j++) {
                        logMessage.append("*");
                    }
                    if (this.longestQueryTimeMs < ((long) this.perfMetricsHistCounts[i3 + 1])) {
                        break;
                    }
                    i3++;
                    str3 = str2;
                    i = 19;
                }
                if (this.perfMetricsHistBreakpoints[18] < this.longestQueryTimeMs) {
                    logMessage.append("\n\tbetween ");
                    logMessage.append(this.perfMetricsHistBreakpoints[18]);
                    str = str2;
                    logMessage.append(str);
                    logMessage.append(this.perfMetricsHistBreakpoints[19]);
                    logMessage.append(" ms: \t");
                    logMessage.append(this.perfMetricsHistCounts[19]);
                } else {
                    str = str2;
                }
            } else {
                str = str3;
            }
            if (this.numTablesMetricsHistBreakpoints != null) {
                logMessage.append("\n\n\tTable Join Histogram:\n");
                int highestCount2 = Integer.MIN_VALUE;
                for (int i4 = 0; i4 < 20; i4++) {
                    int[] iArr2 = this.numTablesMetricsHistCounts;
                    if (iArr2[i4] > highestCount2) {
                        highestCount2 = iArr2[i4];
                    }
                }
                if (highestCount2 == 0) {
                    highestCount2 = 1;
                }
                for (int i5 = 0; i5 < 19; i5++) {
                    if (i5 == 0) {
                        logMessage.append("\n\t" + this.numTablesMetricsHistBreakpoints[i5 + 1] + " tables or less: \t\t" + this.numTablesMetricsHistCounts[i5]);
                    } else {
                        logMessage.append("\n\tbetween " + this.numTablesMetricsHistBreakpoints[i5] + str + this.numTablesMetricsHistBreakpoints[i5 + 1] + " tables: \t" + this.numTablesMetricsHistCounts[i5]);
                    }
                    logMessage.append("\t");
                    int numPointsToGraph2 = (int) (((double) 20) * (((double) this.numTablesMetricsHistCounts[i5]) / ((double) highestCount2)));
                    for (int j2 = 0; j2 < numPointsToGraph2; j2++) {
                        logMessage.append("*");
                    }
                    if (this.maximumNumberTablesAccessed < this.numTablesMetricsHistBreakpoints[i5 + 1]) {
                        break;
                    }
                }
                if (this.numTablesMetricsHistBreakpoints[18] < this.maximumNumberTablesAccessed) {
                    logMessage.append("\n\tbetween ");
                    logMessage.append(this.numTablesMetricsHistBreakpoints[18]);
                    logMessage.append(str);
                    logMessage.append(this.numTablesMetricsHistBreakpoints[19]);
                    logMessage.append(" tables: ");
                    logMessage.append(this.numTablesMetricsHistCounts[19]);
                }
            }
            this.log.logInfo(logMessage);
            this.metricsLastReportedMs = System.currentTimeMillis();
        }
    }

    /* access modifiers changed from: protected */
    public void reportMetricsIfNeeded() {
        if (getGatherPerformanceMetrics() && System.currentTimeMillis() - this.metricsLastReportedMs > ((long) getReportMetricsIntervalMillis())) {
            reportMetrics();
        }
    }

    public void reportNumberOfTablesAccessed(int numTablesAccessed) {
        if (((long) numTablesAccessed) < this.minimumNumberTablesAccessed) {
            this.minimumNumberTablesAccessed = (long) numTablesAccessed;
        }
        if (((long) numTablesAccessed) > this.maximumNumberTablesAccessed) {
            this.maximumNumberTablesAccessed = (long) numTablesAccessed;
            repartitionTablesAccessedHistogram();
        }
        addToTablesAccessedHistogram((long) numTablesAccessed, 1);
    }

    public void resetServerState() throws SQLException {
        if (!getParanoid() && this.f4io != null && versionMeetsMinimum(4, 0, 6)) {
            changeUser(this.user, this.password);
        }
    }

    public void rollback() throws SQLException {
        synchronized (getConnectionMutex()) {
            checkClosed();
            if (this.connectionLifecycleInterceptors != null) {
                IterateBlock<Extension> iter = new IterateBlock<Extension>(this.connectionLifecycleInterceptors.iterator()) {
                    /* access modifiers changed from: package-private */
                    public void forEach(Extension each) throws SQLException {
                        if (!((ConnectionLifecycleInterceptor) each).rollback()) {
                            this.stopIterating = true;
                        }
                    }
                };
                iter.doForAll();
                if (!iter.fullIteration()) {
                    this.needsPing = getReconnectAtTxEnd();
                    return;
                }
            }
            try {
                if (this.autoCommit) {
                    if (!getRelaxAutoCommit()) {
                        throw SQLError.createSQLException("Can't call rollback when autocommit=true", SQLError.SQL_STATE_CONNECTION_NOT_OPEN, getExceptionInterceptor());
                    }
                }
                if (this.transactionsSupported) {
                    rollbackNoChecks();
                }
                this.needsPing = getReconnectAtTxEnd();
            } catch (SQLException sqlEx) {
                if (!getIgnoreNonTxTables() || sqlEx.getErrorCode() != 1196) {
                    throw sqlEx;
                }
                this.needsPing = getReconnectAtTxEnd();
            } catch (SQLException sqlException) {
                if (SQLError.SQL_STATE_COMMUNICATION_LINK_FAILURE.equals(sqlException.getSQLState())) {
                    throw SQLError.createSQLException("Communications link failure during rollback(). Transaction resolution unknown.", SQLError.SQL_STATE_TRANSACTION_RESOLUTION_UNKNOWN, getExceptionInterceptor());
                }
                throw sqlException;
            } catch (Throwable th) {
                this.needsPing = getReconnectAtTxEnd();
                throw th;
            }
        }
    }

    public void rollback(final Savepoint savepoint) throws SQLException {
        Statement stmt;
        String msg;
        synchronized (getConnectionMutex()) {
            if (!versionMeetsMinimum(4, 0, 14)) {
                if (!versionMeetsMinimum(4, 1, 1)) {
                    throw SQLError.createSQLFeatureNotSupportedException();
                }
            }
            checkClosed();
            try {
                if (this.connectionLifecycleInterceptors != null) {
                    IterateBlock<Extension> iter = new IterateBlock<Extension>(this.connectionLifecycleInterceptors.iterator()) {
                        /* access modifiers changed from: package-private */
                        public void forEach(Extension each) throws SQLException {
                            if (!((ConnectionLifecycleInterceptor) each).rollback(savepoint)) {
                                this.stopIterating = true;
                            }
                        }
                    };
                    iter.doForAll();
                    if (!iter.fullIteration()) {
                        this.needsPing = getReconnectAtTxEnd();
                        return;
                    }
                }
                stmt = null;
                stmt = getMetadataSafeStatement();
                stmt.executeUpdate("ROLLBACK TO SAVEPOINT " + '`' + savepoint.getSavepointName() + '`');
                closeStatement(stmt);
                this.needsPing = getReconnectAtTxEnd();
            } catch (SQLException sqlEx) {
                int errno = sqlEx.getErrorCode();
                if (errno == 1181 && (msg = sqlEx.getMessage()) != null) {
                    if (msg.indexOf("153") != -1) {
                        throw SQLError.createSQLException("Savepoint '" + savepoint.getSavepointName() + "' does not exist", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, errno, getExceptionInterceptor());
                    }
                }
                if (getIgnoreNonTxTables()) {
                    if (sqlEx.getErrorCode() != 1196) {
                        throw sqlEx;
                    }
                }
                if (SQLError.SQL_STATE_COMMUNICATION_LINK_FAILURE.equals(sqlEx.getSQLState())) {
                    throw SQLError.createSQLException("Communications link failure during rollback(). Transaction resolution unknown.", SQLError.SQL_STATE_TRANSACTION_RESOLUTION_UNKNOWN, getExceptionInterceptor());
                }
                throw sqlEx;
            } catch (Throwable th) {
                this.needsPing = getReconnectAtTxEnd();
                throw th;
            }
        }
    }

    private void rollbackNoChecks() throws SQLException {
        if (!getUseLocalTransactionState() || !versionMeetsMinimum(5, 0, 0) || this.f4io.inTransactionOnServer()) {
            execSQL((StatementImpl) null, "rollback", -1, (Buffer) null, 1003, 1007, false, this.database, (Field[]) null, false);
        }
    }

    public java.sql.PreparedStatement serverPrepareStatement(String sql) throws SQLException {
        return ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql, getCatalog(), 1003, 1007);
    }

    public java.sql.PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
        PreparedStatement pStmt = ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql, getCatalog(), 1003, 1007);
        boolean z = true;
        if (autoGenKeyIndex != 1) {
            z = false;
        }
        pStmt.setRetrieveGeneratedKeys(z);
        return pStmt;
    }

    public java.sql.PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return ServerPreparedStatement.getInstance(getMultiHostSafeProxy(), getProcessEscapeCodesForPrepStmts() ? nativeSQL(sql) : sql, getCatalog(), resultSetType, resultSetConcurrency);
    }

    public java.sql.PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        if (!getPedantic() || resultSetHoldability == 1) {
            return serverPrepareStatement(sql, resultSetType, resultSetConcurrency);
        }
        throw SQLError.createSQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
    }

    public java.sql.PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
        PreparedStatement pStmt = (PreparedStatement) serverPrepareStatement(sql);
        pStmt.setRetrieveGeneratedKeys(autoGenKeyIndexes != null && autoGenKeyIndexes.length > 0);
        return pStmt;
    }

    public java.sql.PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
        PreparedStatement pStmt = (PreparedStatement) serverPrepareStatement(sql);
        pStmt.setRetrieveGeneratedKeys(autoGenKeyColNames != null && autoGenKeyColNames.length > 0);
        return pStmt;
    }

    public boolean serverSupportsConvertFn() throws SQLException {
        return versionMeetsMinimum(4, 0, 2);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:44:0x008f, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setAutoCommit(final boolean r15) throws java.sql.SQLException {
        /*
            r14 = this;
            java.lang.Object r0 = r14.getConnectionMutex()
            monitor-enter(r0)
            r14.checkClosed()     // Catch:{ all -> 0x009c }
            java.util.List<com.mysql.jdbc.Extension> r1 = r14.connectionLifecycleInterceptors     // Catch:{ all -> 0x009c }
            if (r1 == 0) goto L_0x0022
            com.mysql.jdbc.ConnectionImpl$7 r1 = new com.mysql.jdbc.ConnectionImpl$7     // Catch:{ all -> 0x009c }
            java.util.List<com.mysql.jdbc.Extension> r2 = r14.connectionLifecycleInterceptors     // Catch:{ all -> 0x009c }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ all -> 0x009c }
            r1.<init>(r2, r15)     // Catch:{ all -> 0x009c }
            r1.doForAll()     // Catch:{ all -> 0x009c }
            boolean r2 = r1.fullIteration()     // Catch:{ all -> 0x009c }
            if (r2 != 0) goto L_0x0022
            monitor-exit(r0)     // Catch:{ all -> 0x009c }
            return
        L_0x0022:
            boolean r1 = r14.getAutoReconnectForPools()     // Catch:{ all -> 0x009c }
            if (r1 == 0) goto L_0x002c
            r1 = 1
            r14.setHighAvailability(r1)     // Catch:{ all -> 0x009c }
        L_0x002c:
            r1 = 0
            boolean r2 = r14.transactionsSupported     // Catch:{ all -> 0x0090 }
            if (r2 == 0) goto L_0x006a
            r2 = 1
            boolean r3 = r14.getUseLocalSessionState()     // Catch:{ all -> 0x0090 }
            if (r3 == 0) goto L_0x003e
            boolean r3 = r14.autoCommit     // Catch:{ all -> 0x0090 }
            if (r3 != r15) goto L_0x003e
            r2 = 0
            goto L_0x004d
        L_0x003e:
            boolean r3 = r14.getHighAvailability()     // Catch:{ all -> 0x0090 }
            if (r3 != 0) goto L_0x004d
            com.mysql.jdbc.MysqlIO r3 = r14.getIO()     // Catch:{ all -> 0x0090 }
            boolean r3 = r3.isSetNeededForAutoCommitMode(r15)     // Catch:{ all -> 0x0090 }
            r2 = r3
        L_0x004d:
            r14.autoCommit = r15     // Catch:{ all -> 0x0090 }
            if (r2 == 0) goto L_0x0069
            r4 = 0
            if (r15 == 0) goto L_0x0057
            java.lang.String r3 = "SET autocommit=1"
            goto L_0x0059
        L_0x0057:
            java.lang.String r3 = "SET autocommit=0"
        L_0x0059:
            r5 = r3
            r6 = -1
            r7 = 0
            r8 = 1003(0x3eb, float:1.406E-42)
            r9 = 1007(0x3ef, float:1.411E-42)
            r10 = 0
            java.lang.String r11 = r14.database     // Catch:{ all -> 0x0090 }
            r12 = 0
            r13 = 0
            r3 = r14
            r3.execSQL(r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)     // Catch:{ all -> 0x0090 }
        L_0x0069:
            goto L_0x0082
        L_0x006a:
            if (r15 != 0) goto L_0x0080
            boolean r2 = r14.getRelaxAutoCommit()     // Catch:{ all -> 0x0090 }
            if (r2 == 0) goto L_0x0073
            goto L_0x0080
        L_0x0073:
            java.lang.String r2 = "MySQL Versions Older than 3.23.15 do not support transactions"
            java.lang.String r3 = "08003"
            com.mysql.jdbc.ExceptionInterceptor r4 = r14.getExceptionInterceptor()     // Catch:{ all -> 0x0090 }
            java.sql.SQLException r2 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r2, (java.lang.String) r3, (com.mysql.jdbc.ExceptionInterceptor) r4)     // Catch:{ all -> 0x0090 }
            throw r2     // Catch:{ all -> 0x0090 }
        L_0x0080:
            r14.autoCommit = r15     // Catch:{ all -> 0x0090 }
        L_0x0082:
            boolean r2 = r14.getAutoReconnectForPools()     // Catch:{ all -> 0x009c }
            if (r2 == 0) goto L_0x008c
            r14.setHighAvailability(r1)     // Catch:{ all -> 0x009c }
        L_0x008c:
            monitor-exit(r0)     // Catch:{ all -> 0x009c }
            return
        L_0x0090:
            r2 = move-exception
            boolean r3 = r14.getAutoReconnectForPools()     // Catch:{ all -> 0x009c }
            if (r3 == 0) goto L_0x009a
            r14.setHighAvailability(r1)     // Catch:{ all -> 0x009c }
        L_0x009a:
            throw r2     // Catch:{ all -> 0x009c }
        L_0x009c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x009c }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ConnectionImpl.setAutoCommit(boolean):void");
    }

    public void setCatalog(String catalog) throws SQLException {
        final String str = catalog;
        synchronized (getConnectionMutex()) {
            checkClosed();
            if (str != null) {
                if (this.connectionLifecycleInterceptors != null) {
                    IterateBlock<Extension> iter = new IterateBlock<Extension>(this.connectionLifecycleInterceptors.iterator()) {
                        /* access modifiers changed from: package-private */
                        public void forEach(Extension each) throws SQLException {
                            if (!((ConnectionLifecycleInterceptor) each).setCatalog(str)) {
                                this.stopIterating = true;
                            }
                        }
                    };
                    iter.doForAll();
                    if (!iter.fullIteration()) {
                        return;
                    }
                }
                if (getUseLocalSessionState()) {
                    if (this.lowerCaseTableNames) {
                        if (this.database.equalsIgnoreCase(str)) {
                            return;
                        }
                    } else if (this.database.equals(str)) {
                        return;
                    }
                }
                String quotedId = this.dbmd.getIdentifierQuoteString();
                if (quotedId == null || quotedId.equals(" ")) {
                    quotedId = "";
                }
                execSQL((StatementImpl) null, "USE " + StringUtils.quoteIdentifier(str, quotedId, getPedantic()), -1, (Buffer) null, 1003, 1007, false, this.database, (Field[]) null, false);
                this.database = str;
                return;
            }
            throw SQLError.createSQLException("Catalog can not be null", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
        }
    }

    public void setFailedOver(boolean flag) {
    }

    public void setHoldability(int arg0) throws SQLException {
    }

    public void setInGlobalTx(boolean flag) {
        this.isInGlobalTx = flag;
    }

    @Deprecated
    public void setPreferSlaveDuringFailover(boolean flag) {
    }

    public void setReadInfoMsgEnabled(boolean flag) {
        this.readInfoMsg = flag;
    }

    public void setReadOnly(boolean readOnlyFlag) throws SQLException {
        checkClosed();
        setReadOnlyInternal(readOnlyFlag);
    }

    public void setReadOnlyInternal(boolean readOnlyFlag) throws SQLException {
        if (getReadOnlyPropagatesToServer() && versionMeetsMinimum(5, 6, 5) && (!getUseLocalSessionState() || readOnlyFlag != this.readOnly)) {
            execSQL((StatementImpl) null, "set session transaction " + (readOnlyFlag ? "read only" : "read write"), -1, (Buffer) null, 1003, 1007, false, this.database, (Field[]) null, false);
        }
        this.readOnly = readOnlyFlag;
    }

    public Savepoint setSavepoint() throws SQLException {
        MysqlSavepoint savepoint = new MysqlSavepoint(getExceptionInterceptor());
        setSavepoint(savepoint);
        return savepoint;
    }

    private void setSavepoint(MysqlSavepoint savepoint) throws SQLException {
        synchronized (getConnectionMutex()) {
            if (!versionMeetsMinimum(4, 0, 14)) {
                if (!versionMeetsMinimum(4, 1, 1)) {
                    throw SQLError.createSQLFeatureNotSupportedException();
                }
            }
            checkClosed();
            Statement stmt = null;
            try {
                stmt = getMetadataSafeStatement();
                stmt.executeUpdate("SAVEPOINT " + '`' + savepoint.getSavepointName() + '`');
            } finally {
                closeStatement(stmt);
            }
        }
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        MysqlSavepoint savepoint;
        synchronized (getConnectionMutex()) {
            savepoint = new MysqlSavepoint(name, getExceptionInterceptor());
            setSavepoint(savepoint);
        }
        return savepoint;
    }

    private void setSessionVariables() throws SQLException {
        if (versionMeetsMinimum(4, 0, 0) && getSessionVariables() != null) {
            List<String> variablesToSet = new ArrayList<>();
            for (String part : StringUtils.split(getSessionVariables(), ",", "\"'(", "\"')", "\"'", true)) {
                variablesToSet.addAll(StringUtils.split(part, ";", "\"'(", "\"')", "\"'", true));
            }
            if (!variablesToSet.isEmpty()) {
                Statement stmt = null;
                try {
                    stmt = getMetadataSafeStatement();
                    StringBuilder query = new StringBuilder("SET ");
                    String separator = "";
                    for (String variableToSet : variablesToSet) {
                        if (variableToSet.length() > 0) {
                            query.append(separator);
                            if (!variableToSet.startsWith("@")) {
                                query.append("SESSION ");
                            }
                            query.append(variableToSet);
                            separator = ",";
                        }
                    }
                    stmt.executeUpdate(query.toString());
                } finally {
                    if (stmt != null) {
                        stmt.close();
                    }
                }
            }
        }
    }

    public void setTransactionIsolation(int level) throws SQLException {
        String sql;
        synchronized (getConnectionMutex()) {
            checkClosed();
            if (this.hasIsolationLevels) {
                boolean shouldSendSet = false;
                if (getAlwaysSendSetIsolation()) {
                    shouldSendSet = true;
                } else if (level != this.isolationLevel) {
                    shouldSendSet = true;
                }
                if (getUseLocalSessionState()) {
                    shouldSendSet = this.isolationLevel != level;
                }
                if (shouldSendSet) {
                    switch (level) {
                        case 0:
                            throw SQLError.createSQLException("Transaction isolation level NONE not supported by MySQL", getExceptionInterceptor());
                        case 1:
                            sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED";
                            break;
                        case 2:
                            sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED";
                            break;
                        case 4:
                            sql = "SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ";
                            break;
                        case 8:
                            sql = "SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE";
                            break;
                        default:
                            throw SQLError.createSQLException("Unsupported transaction isolation level '" + level + "'", SQLError.SQL_STATE_DRIVER_NOT_CAPABLE, getExceptionInterceptor());
                    }
                    execSQL((StatementImpl) null, sql, -1, (Buffer) null, 1003, 1007, false, this.database, (Field[]) null, false);
                    this.isolationLevel = level;
                }
            } else {
                throw SQLError.createSQLException("Transaction Isolation Levels are not supported on MySQL versions older than 3.23.36.", SQLError.SQL_STATE_DRIVER_NOT_CAPABLE, getExceptionInterceptor());
            }
        }
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        synchronized (getConnectionMutex()) {
            this.typeMap = map;
        }
    }

    private void setupServerForTruncationChecks() throws SQLException {
        if (getJdbcCompliantTruncation() && versionMeetsMinimum(5, 0, 2)) {
            String currentSqlMode = this.serverVariables.get("sql_mode");
            boolean strictTransTablesIsSet = StringUtils.indexOfIgnoreCase(currentSqlMode, "STRICT_TRANS_TABLES") != -1;
            if (currentSqlMode == null || currentSqlMode.length() == 0 || !strictTransTablesIsSet) {
                StringBuilder commandBuf = new StringBuilder("SET sql_mode='");
                if (currentSqlMode != null && currentSqlMode.length() > 0) {
                    commandBuf.append(currentSqlMode);
                    commandBuf.append(",");
                }
                commandBuf.append("STRICT_TRANS_TABLES'");
                execSQL((StatementImpl) null, commandBuf.toString(), -1, (Buffer) null, 1003, 1007, false, this.database, (Field[]) null, false);
                setJdbcCompliantTruncation(false);
            } else if (strictTransTablesIsSet) {
                setJdbcCompliantTruncation(false);
            }
        }
    }

    public void shutdownServer() throws SQLException {
        try {
            if (versionMeetsMinimum(5, 7, 9)) {
                execSQL((StatementImpl) null, "SHUTDOWN", -1, (Buffer) null, 1003, 1007, false, this.database, (Field[]) null, false);
                return;
            }
            this.f4io.sendCommand(8, (String) null, (Buffer) null, false, (String) null, 0);
        } catch (Exception ex) {
            SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.UnhandledExceptionDuringShutdown"), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
            sqlEx.initCause(ex);
            throw sqlEx;
        }
    }

    public boolean supportsIsolationLevel() {
        return this.hasIsolationLevels;
    }

    public boolean supportsQuotedIdentifiers() {
        return this.hasQuotedIdentifiers;
    }

    public boolean supportsTransactions() {
        return this.transactionsSupported;
    }

    public void unregisterStatement(Statement stmt) {
        this.openStatements.remove(stmt);
    }

    public boolean useAnsiQuotedIdentifiers() {
        boolean z;
        synchronized (getConnectionMutex()) {
            z = this.useAnsiQuotes;
        }
        return z;
    }

    public boolean versionMeetsMinimum(int major, int minor, int subminor) throws SQLException {
        checkClosed();
        return this.f4io.versionMeetsMinimum(major, minor, subminor);
    }

    public CachedResultSetMetaData getCachedMetaData(String sql) {
        CachedResultSetMetaData cachedResultSetMetaData;
        LRUCache<String, CachedResultSetMetaData> lRUCache = this.resultSetMetadataCache;
        if (lRUCache == null) {
            return null;
        }
        synchronized (lRUCache) {
            cachedResultSetMetaData = (CachedResultSetMetaData) this.resultSetMetadataCache.get(sql);
        }
        return cachedResultSetMetaData;
    }

    public void initializeResultsMetadataFromCache(String sql, CachedResultSetMetaData cachedMetaData, ResultSetInternalMethods resultSet) throws SQLException {
        if (cachedMetaData == null) {
            CachedResultSetMetaData cachedMetaData2 = new CachedResultSetMetaData();
            resultSet.buildIndexMapping();
            resultSet.initializeWithMetadata();
            if (resultSet instanceof UpdatableResultSet) {
                ((UpdatableResultSet) resultSet).checkUpdatability();
            }
            resultSet.populateCachedMetaData(cachedMetaData2);
            this.resultSetMetadataCache.put(sql, cachedMetaData2);
            return;
        }
        resultSet.initializeFromCachedMetaData(cachedMetaData);
        resultSet.initializeWithMetadata();
        if (resultSet instanceof UpdatableResultSet) {
            ((UpdatableResultSet) resultSet).checkUpdatability();
        }
    }

    public String getStatementComment() {
        return this.statementComment;
    }

    public void setStatementComment(String comment) {
        this.statementComment = comment;
    }

    public void reportQueryTime(long millisOrNanos) {
        synchronized (getConnectionMutex()) {
            long j = this.queryTimeCount + 1;
            this.queryTimeCount = j;
            this.queryTimeSum += (double) millisOrNanos;
            this.queryTimeSumSquares += (double) (millisOrNanos * millisOrNanos);
            this.queryTimeMean = ((this.queryTimeMean * ((double) (j - 1))) + ((double) millisOrNanos)) / ((double) j);
        }
    }

    public boolean isAbonormallyLongQuery(long millisOrNanos) {
        boolean res;
        synchronized (getConnectionMutex()) {
            res = false;
            long j = this.queryTimeCount;
            if (j > 14) {
                double d = this.queryTimeSumSquares;
                double d2 = this.queryTimeSum;
                res = ((double) millisOrNanos) > this.queryTimeMean + (5.0d * Math.sqrt((d - ((d2 * d2) / ((double) j))) / ((double) (j - 1))));
            }
            reportQueryTime(millisOrNanos);
        }
        return res;
    }

    public void initializeExtension(Extension ex) throws SQLException {
        ex.init(this, this.props);
    }

    public void transactionBegun() throws SQLException {
        synchronized (getConnectionMutex()) {
            if (this.connectionLifecycleInterceptors != null) {
                new IterateBlock<Extension>(this.connectionLifecycleInterceptors.iterator()) {
                    /* access modifiers changed from: package-private */
                    public void forEach(Extension each) throws SQLException {
                        ((ConnectionLifecycleInterceptor) each).transactionBegun();
                    }
                }.doForAll();
            }
        }
    }

    public void transactionCompleted() throws SQLException {
        synchronized (getConnectionMutex()) {
            if (this.connectionLifecycleInterceptors != null) {
                new IterateBlock<Extension>(this.connectionLifecycleInterceptors.iterator()) {
                    /* access modifiers changed from: package-private */
                    public void forEach(Extension each) throws SQLException {
                        ((ConnectionLifecycleInterceptor) each).transactionCompleted();
                    }
                }.doForAll();
            }
        }
    }

    public boolean storesLowerCaseTableName() {
        return this.storesLowerCaseTableName;
    }

    public ExceptionInterceptor getExceptionInterceptor() {
        return this.exceptionInterceptor;
    }

    public boolean getRequiresEscapingEncoder() {
        return this.requiresEscapingEncoder;
    }

    public boolean isServerLocal() throws SQLException {
        synchronized (getConnectionMutex()) {
            SocketFactory factory = getIO().socketFactory;
            if (factory instanceof SocketMetadata) {
                boolean isLocallyConnected = ((SocketMetadata) factory).isLocallyConnected(this);
                return isLocallyConnected;
            }
            getLog().logWarn(Messages.getString("Connection.NoMetadataOnSocketFactory"));
            return false;
        }
    }

    public int getSessionMaxRows() {
        int i;
        synchronized (getConnectionMutex()) {
            i = this.sessionMaxRows;
        }
        return i;
    }

    public void setSessionMaxRows(int max) throws SQLException {
        synchronized (getConnectionMutex()) {
            if (this.sessionMaxRows != max) {
                this.sessionMaxRows = max;
                StringBuilder append = new StringBuilder().append("SET SQL_SELECT_LIMIT=");
                int i = this.sessionMaxRows;
                execSQL((StatementImpl) null, append.append(i == -1 ? "DEFAULT" : Integer.valueOf(i)).toString(), -1, (Buffer) null, 1003, 1007, false, this.database, (Field[]) null, false);
            }
        }
    }

    public void setSchema(String schema) throws SQLException {
        synchronized (getConnectionMutex()) {
            checkClosed();
        }
    }

    public String getSchema() throws SQLException {
        synchronized (getConnectionMutex()) {
            checkClosed();
        }
        return null;
    }

    public void abort(Executor executor) throws SQLException {
        SecurityManager sec = System.getSecurityManager();
        if (sec != null) {
            sec.checkPermission(ABORT_PERM);
        }
        if (executor != null) {
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        ConnectionImpl.this.abortInternal();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return;
        }
        throw SQLError.createSQLException("Executor can not be null", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        synchronized (getConnectionMutex()) {
            SecurityManager sec = System.getSecurityManager();
            if (sec != null) {
                sec.checkPermission(SET_NETWORK_TIMEOUT_PERM);
            }
            if (executor != null) {
                checkClosed();
                executor.execute(new NetworkTimeoutSetter(this, this.f4io, milliseconds));
            } else {
                throw SQLError.createSQLException("Executor can not be null", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
    }

    public int getNetworkTimeout() throws SQLException {
        int socketTimeout;
        synchronized (getConnectionMutex()) {
            checkClosed();
            socketTimeout = getSocketTimeout();
        }
        return socketTimeout;
    }

    public ProfilerEventHandler getProfilerEventHandlerInstance() {
        return this.eventSink;
    }

    public void setProfilerEventHandlerInstance(ProfilerEventHandler h) {
        this.eventSink = h;
    }

    public boolean isServerTruncatesFracSecs() {
        return this.serverTruncatesFracSecs;
    }

    private static class NetworkTimeoutSetter implements Runnable {
        private final WeakReference<ConnectionImpl> connImplRef;
        private final int milliseconds;
        private final WeakReference<MysqlIO> mysqlIoRef;

        public NetworkTimeoutSetter(ConnectionImpl conn, MysqlIO io2, int milliseconds2) {
            this.connImplRef = new WeakReference<>(conn);
            this.mysqlIoRef = new WeakReference<>(io2);
            this.milliseconds = milliseconds2;
        }

        public void run() {
            try {
                ConnectionImpl conn = (ConnectionImpl) this.connImplRef.get();
                if (conn != null) {
                    synchronized (conn.getConnectionMutex()) {
                        conn.setSocketTimeout(this.milliseconds);
                        MysqlIO io2 = (MysqlIO) this.mysqlIoRef.get();
                        if (io2 != null) {
                            io2.setSocketTimeout(this.milliseconds);
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getQueryTimingUnits() {
        MysqlIO mysqlIO = this.f4io;
        return mysqlIO != null ? mysqlIO.getQueryTimingUnits() : Constants.MILLIS_I18N;
    }
}
