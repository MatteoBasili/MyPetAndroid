package com.mysql.jdbc;

import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.internal.view.SupportMenu;
import com.mysql.jdbc.log.Log;
import com.mysql.jdbc.log.StandardLogger;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

public class ConnectionPropertiesImpl implements Serializable, ConnectionProperties {
    private static final String CONNECTION_AND_AUTH_CATEGORY;
    private static final String DEBUGING_PROFILING_CATEGORY;
    private static final String HA_CATEGORY;
    private static final String MISC_CATEGORY;
    private static final String NETWORK_CATEGORY;
    private static final String PERFORMANCE_CATEGORY;
    private static final String[] PROPERTY_CATEGORIES;
    private static final ArrayList<Field> PROPERTY_LIST = new ArrayList<>();
    private static final String SECURITY_CATEGORY;
    private static final String STANDARD_LOGGER_NAME = StandardLogger.class.getName();
    protected static final String ZERO_DATETIME_BEHAVIOR_CONVERT_TO_NULL = "convertToNull";
    protected static final String ZERO_DATETIME_BEHAVIOR_EXCEPTION = "exception";
    protected static final String ZERO_DATETIME_BEHAVIOR_ROUND = "round";
    private static final long serialVersionUID = 4257801713007640580L;
    private BooleanConnectionProperty allowLoadLocalInfile;
    private BooleanConnectionProperty allowMasterDownConnections;
    private BooleanConnectionProperty allowMultiQueries;
    private BooleanConnectionProperty allowNanAndInf;
    private BooleanConnectionProperty allowPublicKeyRetrieval;
    private BooleanConnectionProperty allowSlaveDownConnections;
    private BooleanConnectionProperty allowUrlInLocalInfile;
    private BooleanConnectionProperty alwaysSendSetIsolation;
    private StringConnectionProperty authenticationPlugins;
    private BooleanConnectionProperty autoClosePStmtStreams;
    private BooleanConnectionProperty autoDeserialize;
    private BooleanConnectionProperty autoGenerateTestcaseScript;
    private boolean autoGenerateTestcaseScriptAsBoolean = false;
    private BooleanConnectionProperty autoReconnect;
    private BooleanConnectionProperty autoReconnectForPools;
    private boolean autoReconnectForPoolsAsBoolean;
    private BooleanConnectionProperty autoSlowLog;
    private MemorySizeConnectionProperty blobSendChunkSize;
    private BooleanConnectionProperty blobsAreStrings;
    private BooleanConnectionProperty cacheCallableStatements;
    private BooleanConnectionProperty cacheDefaultTimezone;
    private BooleanConnectionProperty cachePreparedStatements;
    private boolean cacheResultSetMetaDataAsBoolean;
    private BooleanConnectionProperty cacheResultSetMetadata;
    private BooleanConnectionProperty cacheServerConfiguration;
    private IntegerConnectionProperty callableStatementCacheSize;
    private BooleanConnectionProperty capitalizeTypeNames;
    private StringConnectionProperty characterEncoding;
    private String characterEncodingAsString;
    protected boolean characterEncodingIsAliasForSjis;
    private StringConnectionProperty characterSetResults;
    private StringConnectionProperty clientCertificateKeyStorePassword;
    private StringConnectionProperty clientCertificateKeyStoreType;
    private StringConnectionProperty clientCertificateKeyStoreUrl;
    private StringConnectionProperty clientInfoProvider;
    private StringConnectionProperty clobCharacterEncoding;
    private BooleanConnectionProperty clobberStreamingResults;
    private BooleanConnectionProperty compensateOnDuplicateKeyUpdateCounts;
    private IntegerConnectionProperty connectTimeout;
    private StringConnectionProperty connectionAttributes;
    private StringConnectionProperty connectionCollation;
    private StringConnectionProperty connectionLifecycleInterceptors;
    private BooleanConnectionProperty continueBatchOnError;
    private BooleanConnectionProperty createDatabaseIfNotExist;
    private StringConnectionProperty defaultAuthenticationPlugin;
    private IntegerConnectionProperty defaultFetchSize;
    private BooleanConnectionProperty detectCustomCollations;
    private BooleanConnectionProperty detectServerPreparedStmts;
    private StringConnectionProperty disabledAuthenticationPlugins;
    private BooleanConnectionProperty disconnectOnExpiredPasswords;
    private BooleanConnectionProperty dontCheckOnDuplicateKeyUpdateInSQL;
    private BooleanConnectionProperty dontTrackOpenResources;
    private BooleanConnectionProperty dumpMetadataOnColumnNotFound;
    private BooleanConnectionProperty dumpQueriesOnException;
    private BooleanConnectionProperty dynamicCalendars;
    private BooleanConnectionProperty elideSetAutoCommits;
    private BooleanConnectionProperty emptyStringsConvertToZero;
    private BooleanConnectionProperty emulateLocators;
    private BooleanConnectionProperty emulateUnsupportedPstmts;
    private BooleanConnectionProperty enableEscapeProcessing;
    private BooleanConnectionProperty enablePacketDebug;
    private BooleanConnectionProperty enableQueryTimeouts;
    private StringConnectionProperty enabledSSLCipherSuites;
    private StringConnectionProperty enabledTLSProtocols;
    private StringConnectionProperty exceptionInterceptors;
    private BooleanConnectionProperty explainSlowQueries;
    private BooleanConnectionProperty failOverReadOnly;
    private BooleanConnectionProperty functionsNeverReturnBlobs;
    private BooleanConnectionProperty gatherPerformanceMetrics;
    private BooleanConnectionProperty generateSimpleParameterMetadata;
    private BooleanConnectionProperty getProceduresReturnsFunctions;
    private boolean highAvailabilityAsBoolean;
    private BooleanConnectionProperty holdResultsOpenOverStatementClose;
    private BooleanConnectionProperty ignoreNonTxTables;
    private BooleanConnectionProperty includeInnodbStatusInDeadlockExceptions;
    private BooleanConnectionProperty includeThreadDumpInDeadlockExceptions;
    private BooleanConnectionProperty includeThreadNamesAsStatementComment;
    private IntegerConnectionProperty initialTimeout;
    private BooleanConnectionProperty isInteractiveClient;
    private BooleanConnectionProperty jdbcCompliantTruncation;
    private boolean jdbcCompliantTruncationForReads;
    protected MemorySizeConnectionProperty largeRowSizeThreshold;
    private StringConnectionProperty loadBalanceAutoCommitStatementRegex;
    private IntegerConnectionProperty loadBalanceAutoCommitStatementThreshold;
    private IntegerConnectionProperty loadBalanceBlacklistTimeout;
    private StringConnectionProperty loadBalanceConnectionGroup;
    private BooleanConnectionProperty loadBalanceEnableJMX;
    private StringConnectionProperty loadBalanceExceptionChecker;
    private IntegerConnectionProperty loadBalanceHostRemovalGracePeriod;
    private IntegerConnectionProperty loadBalancePingTimeout;
    private StringConnectionProperty loadBalanceSQLExceptionSubclassFailover;
    private StringConnectionProperty loadBalanceSQLStateFailover;
    private StringConnectionProperty loadBalanceStrategy;
    private BooleanConnectionProperty loadBalanceValidateConnectionOnSwapServer;
    private StringConnectionProperty localSocketAddress;
    private MemorySizeConnectionProperty locatorFetchBufferSize;
    private BooleanConnectionProperty logSlowQueries;
    private BooleanConnectionProperty logXaCommands;
    private StringConnectionProperty loggerClassName;
    private BooleanConnectionProperty maintainTimeStats;
    private boolean maintainTimeStatsAsBoolean;
    private IntegerConnectionProperty maxAllowedPacket;
    private IntegerConnectionProperty maxQuerySizeToLog;
    private IntegerConnectionProperty maxReconnects;
    private IntegerConnectionProperty maxRows;
    private int maxRowsAsInt;
    private IntegerConnectionProperty metadataCacheSize;
    private IntegerConnectionProperty netTimeoutForStreamingResults;
    private BooleanConnectionProperty noAccessToProcedureBodies;
    private BooleanConnectionProperty noDatetimeStringSync;
    private BooleanConnectionProperty noTimezoneConversionForDateType;
    private BooleanConnectionProperty noTimezoneConversionForTimeType;
    private BooleanConnectionProperty nullCatalogMeansCurrent;
    private BooleanConnectionProperty nullNamePatternMatchesAll;
    private BooleanConnectionProperty overrideSupportsIntegrityEnhancementFacility;
    private IntegerConnectionProperty packetDebugBufferSize;
    private BooleanConnectionProperty padCharsWithSpace;
    private BooleanConnectionProperty paranoid;
    private StringConnectionProperty parseInfoCacheFactory;
    private StringConnectionProperty passwordCharacterEncoding;
    private BooleanConnectionProperty pedantic;
    private BooleanConnectionProperty pinGlobalTxToPhysicalConnection;
    private BooleanConnectionProperty populateInsertRowWithDefaultValues;
    private IntegerConnectionProperty preparedStatementCacheSize;
    private IntegerConnectionProperty preparedStatementCacheSqlLimit;
    private BooleanConnectionProperty processEscapeCodesForPrepStmts;
    private BooleanConnectionProperty profileSQL;
    private boolean profileSQLAsBoolean;
    private StringConnectionProperty profileSql;
    private StringConnectionProperty profilerEventHandler;
    private StringConnectionProperty propertiesTransform;
    private IntegerConnectionProperty queriesBeforeRetryMaster;
    private BooleanConnectionProperty queryTimeoutKillsConnection;
    private BooleanConnectionProperty readFromMasterWhenNoSlaves;
    private BooleanConnectionProperty readOnlyPropagatesToServer;
    private BooleanConnectionProperty reconnectAtTxEnd;
    private boolean reconnectTxAtEndAsBoolean;
    private BooleanConnectionProperty relaxAutoCommit;
    private StringConnectionProperty replicationConnectionGroup;
    private BooleanConnectionProperty replicationEnableJMX;
    private IntegerConnectionProperty reportMetricsIntervalMillis;
    private BooleanConnectionProperty requireSSL;
    private StringConnectionProperty resourceId;
    private IntegerConnectionProperty resultSetSizeThreshold;
    private BooleanConnectionProperty retainStatementAfterResultSetClose;
    private IntegerConnectionProperty retriesAllDown;
    private BooleanConnectionProperty rewriteBatchedStatements;
    private BooleanConnectionProperty rollbackOnPooledClose;
    private BooleanConnectionProperty roundRobinLoadBalance;
    private BooleanConnectionProperty runningCTS13;
    private IntegerConnectionProperty secondsBeforeRetryMaster;
    private IntegerConnectionProperty selfDestructOnPingMaxOperations;
    private IntegerConnectionProperty selfDestructOnPingSecondsLifetime;
    private BooleanConnectionProperty sendFractionalSeconds;
    private StringConnectionProperty serverAffinityOrder;
    private StringConnectionProperty serverConfigCacheFactory;
    private StringConnectionProperty serverRSAPublicKeyFile;
    private StringConnectionProperty serverTimezone;
    private StringConnectionProperty sessionVariables;
    private IntegerConnectionProperty slowQueryThresholdMillis;
    private LongConnectionProperty slowQueryThresholdNanos;
    private StringConnectionProperty socketFactoryClassName;
    private IntegerConnectionProperty socketTimeout;
    private StringConnectionProperty socksProxyHost;
    private IntegerConnectionProperty socksProxyPort;
    private StringConnectionProperty statementInterceptors;
    private BooleanConnectionProperty strictFloatingPoint;
    private BooleanConnectionProperty strictUpdates;
    private BooleanConnectionProperty tcpKeepAlive;
    private BooleanConnectionProperty tcpNoDelay;
    private IntegerConnectionProperty tcpRcvBuf;
    private IntegerConnectionProperty tcpSndBuf;
    private IntegerConnectionProperty tcpTrafficClass;
    private BooleanConnectionProperty tinyInt1isBit;
    protected BooleanConnectionProperty traceProtocol;
    private BooleanConnectionProperty transformedBitIsBoolean;
    private BooleanConnectionProperty treatUtilDateAsTimestamp;
    private StringConnectionProperty trustCertificateKeyStorePassword;
    private StringConnectionProperty trustCertificateKeyStoreType;
    private StringConnectionProperty trustCertificateKeyStoreUrl;
    private BooleanConnectionProperty useAffectedRows;
    private BooleanConnectionProperty useBlobToStoreUTF8OutsideBMP;
    private BooleanConnectionProperty useColumnNamesInFindColumn;
    private BooleanConnectionProperty useCompression;
    private StringConnectionProperty useConfigs;
    private BooleanConnectionProperty useCursorFetch;
    private BooleanConnectionProperty useDirectRowUnpack;
    private BooleanConnectionProperty useDynamicCharsetInfo;
    private BooleanConnectionProperty useFastDateParsing;
    private BooleanConnectionProperty useFastIntParsing;
    private BooleanConnectionProperty useGmtMillisForDatetimes;
    private BooleanConnectionProperty useHostsInPrivileges;
    private BooleanConnectionProperty useInformationSchema;
    private BooleanConnectionProperty useJDBCCompliantTimezoneShift;
    private BooleanConnectionProperty useJvmCharsetConverters;
    private BooleanConnectionProperty useLegacyDatetimeCode;
    private BooleanConnectionProperty useLocalSessionState;
    private BooleanConnectionProperty useLocalTransactionState;
    private BooleanConnectionProperty useNanosForElapsedTime;
    private BooleanConnectionProperty useOldAliasMetadataBehavior;
    private BooleanConnectionProperty useOldUTF8Behavior;
    private boolean useOldUTF8BehaviorAsBoolean;
    private BooleanConnectionProperty useOnlyServerErrorMessages;
    private BooleanConnectionProperty useReadAheadInput;
    private BooleanConnectionProperty useSSL;
    private BooleanConnectionProperty useSSPSCompatibleTimezoneShift;
    private BooleanConnectionProperty useSqlStateCodes;
    private BooleanConnectionProperty useStreamLengthsInPrepStmts;
    private BooleanConnectionProperty useTimezone;
    private BooleanConnectionProperty useUltraDevWorkAround;
    private BooleanConnectionProperty useUnbufferedInput;
    private BooleanConnectionProperty useUnicode;
    private boolean useUnicodeAsBoolean;
    private BooleanConnectionProperty useUsageAdvisor;
    private boolean useUsageAdvisorAsBoolean;
    private StringConnectionProperty utf8OutsideBmpExcludedColumnNamePattern;
    private StringConnectionProperty utf8OutsideBmpIncludedColumnNamePattern;
    private BooleanConnectionProperty verifyServerCertificate;
    private BooleanConnectionProperty yearIsDateType;
    private StringConnectionProperty zeroDateTimeBehavior;

    public ConnectionPropertiesImpl() {
        String string = Messages.getString("ConnectionProperties.loadDataLocal");
        String str = SECURITY_CATEGORY;
        this.allowLoadLocalInfile = new BooleanConnectionProperty("allowLoadLocalInfile", false, string, "3.0.3", str, Integer.MAX_VALUE);
        String str2 = str;
        this.allowMultiQueries = new BooleanConnectionProperty("allowMultiQueries", false, Messages.getString("ConnectionProperties.allowMultiQueries"), "3.1.1", str2, 1);
        String string2 = Messages.getString("ConnectionProperties.allowNANandINF");
        String str3 = MISC_CATEGORY;
        this.allowNanAndInf = new BooleanConnectionProperty("allowNanAndInf", false, string2, "3.1.5", str3, Integer.MIN_VALUE);
        this.allowUrlInLocalInfile = new BooleanConnectionProperty("allowUrlInLocalInfile", false, Messages.getString("ConnectionProperties.allowUrlInLoadLocal"), "3.1.4", str2, Integer.MAX_VALUE);
        String string3 = Messages.getString("ConnectionProperties.alwaysSendSetIsolation");
        String str4 = PERFORMANCE_CATEGORY;
        this.alwaysSendSetIsolation = new BooleanConnectionProperty("alwaysSendSetIsolation", true, string3, "3.1.7", str4, Integer.MAX_VALUE);
        String str5 = str3;
        this.autoClosePStmtStreams = new BooleanConnectionProperty("autoClosePStmtStreams", false, Messages.getString("ConnectionProperties.autoClosePstmtStreams"), "3.1.12", str5, Integer.MIN_VALUE);
        String string4 = Messages.getString("ConnectionProperties.replicationConnectionGroup");
        String str6 = HA_CATEGORY;
        this.replicationConnectionGroup = new StringConnectionProperty("replicationConnectionGroup", (String) null, string4, "5.1.27", str6, Integer.MIN_VALUE);
        String str7 = str6;
        this.allowMasterDownConnections = new BooleanConnectionProperty("allowMasterDownConnections", false, Messages.getString("ConnectionProperties.allowMasterDownConnections"), "5.1.27", str7, Integer.MAX_VALUE);
        this.allowSlaveDownConnections = new BooleanConnectionProperty("allowSlaveDownConnections", false, Messages.getString("ConnectionProperties.allowSlaveDownConnections"), "5.1.38", str7, Integer.MAX_VALUE);
        this.readFromMasterWhenNoSlaves = new BooleanConnectionProperty("readFromMasterWhenNoSlaves", false, Messages.getString("ConnectionProperties.readFromMasterWhenNoSlaves"), "5.1.38", str7, Integer.MAX_VALUE);
        this.autoDeserialize = new BooleanConnectionProperty("autoDeserialize", false, Messages.getString("ConnectionProperties.autoDeserialize"), "3.1.5", str5, Integer.MIN_VALUE);
        String string5 = Messages.getString("ConnectionProperties.autoGenerateTestcaseScript");
        String str8 = DEBUGING_PROFILING_CATEGORY;
        this.autoGenerateTestcaseScript = new BooleanConnectionProperty("autoGenerateTestcaseScript", false, string5, "3.1.9", str8, Integer.MIN_VALUE);
        String str9 = str6;
        this.autoReconnect = new BooleanConnectionProperty("autoReconnect", false, Messages.getString("ConnectionProperties.autoReconnect"), "1.1", str9, 0);
        this.autoReconnectForPools = new BooleanConnectionProperty("autoReconnectForPools", false, Messages.getString("ConnectionProperties.autoReconnectForPools"), "3.1.3", str9, 1);
        this.autoReconnectForPoolsAsBoolean = false;
        String str10 = str4;
        this.blobSendChunkSize = new MemorySizeConnectionProperty("blobSendChunkSize", 1048576, 0, 0, Messages.getString("ConnectionProperties.blobSendChunkSize"), "3.1.9", str10, Integer.MIN_VALUE);
        this.autoSlowLog = new BooleanConnectionProperty("autoSlowLog", true, Messages.getString("ConnectionProperties.autoSlowLog"), "5.1.4", str8, Integer.MIN_VALUE);
        String str11 = str3;
        this.blobsAreStrings = new BooleanConnectionProperty("blobsAreStrings", false, "Should the driver always treat BLOBs as Strings - specifically to work around dubious metadata returned by the server for GROUP BY clauses?", "5.0.8", str11, Integer.MIN_VALUE);
        this.functionsNeverReturnBlobs = new BooleanConnectionProperty("functionsNeverReturnBlobs", false, "Should the driver always treat data from functions returning BLOBs as Strings - specifically to work around dubious metadata returned by the server for GROUP BY clauses?", "5.0.8", str11, Integer.MIN_VALUE);
        String str12 = str4;
        this.cacheCallableStatements = new BooleanConnectionProperty("cacheCallableStmts", false, Messages.getString("ConnectionProperties.cacheCallableStatements"), "3.1.2", str12, Integer.MIN_VALUE);
        this.cachePreparedStatements = new BooleanConnectionProperty("cachePrepStmts", false, Messages.getString("ConnectionProperties.cachePrepStmts"), "3.0.10", str12, Integer.MIN_VALUE);
        this.cacheResultSetMetadata = new BooleanConnectionProperty("cacheResultSetMetadata", false, Messages.getString("ConnectionProperties.cacheRSMetadata"), "3.1.1", str12, Integer.MIN_VALUE);
        this.serverConfigCacheFactory = new StringConnectionProperty("serverConfigCacheFactory", PerVmServerConfigCacheFactory.class.getName(), Messages.getString("ConnectionProperties.serverConfigCacheFactory"), "5.1.1", str12, 12);
        this.cacheServerConfiguration = new BooleanConnectionProperty("cacheServerConfiguration", false, Messages.getString("ConnectionProperties.cacheServerConfiguration"), "3.1.5", str12, Integer.MIN_VALUE);
        this.callableStatementCacheSize = new IntegerConnectionProperty("callableStmtCacheSize", 100, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.callableStmtCacheSize"), "3.1.2", str10, 5);
        String str13 = str3;
        this.capitalizeTypeNames = new BooleanConnectionProperty("capitalizeTypeNames", true, Messages.getString("ConnectionProperties.capitalizeTypeNames"), "2.0.7", str13, Integer.MIN_VALUE);
        this.characterEncoding = new StringConnectionProperty("characterEncoding", (String) null, Messages.getString("ConnectionProperties.characterEncoding"), "1.1g", str13, 5);
        this.characterEncodingAsString = null;
        this.characterEncodingIsAliasForSjis = false;
        this.characterSetResults = new StringConnectionProperty("characterSetResults", (String) null, Messages.getString("ConnectionProperties.characterSetResults"), "3.0.13", str13, 6);
        this.connectionAttributes = new StringConnectionProperty("connectionAttributes", (String) null, Messages.getString("ConnectionProperties.connectionAttributes"), "5.1.25", str13, 7);
        this.clientInfoProvider = new StringConnectionProperty("clientInfoProvider", "com.mysql.jdbc.JDBC4CommentClientInfoProvider", Messages.getString("ConnectionProperties.clientInfoProvider"), "5.1.0", str8, Integer.MIN_VALUE);
        this.clobberStreamingResults = new BooleanConnectionProperty("clobberStreamingResults", false, Messages.getString("ConnectionProperties.clobberStreamingResults"), "3.0.9", str13, Integer.MIN_VALUE);
        this.clobCharacterEncoding = new StringConnectionProperty("clobCharacterEncoding", (String) null, Messages.getString("ConnectionProperties.clobCharacterEncoding"), "5.0.0", str13, Integer.MIN_VALUE);
        this.compensateOnDuplicateKeyUpdateCounts = new BooleanConnectionProperty("compensateOnDuplicateKeyUpdateCounts", false, Messages.getString("ConnectionProperties.compensateOnDuplicateKeyUpdateCounts"), "5.1.7", str13, Integer.MIN_VALUE);
        this.connectionCollation = new StringConnectionProperty("connectionCollation", (String) null, Messages.getString("ConnectionProperties.connectionCollation"), "3.0.13", str13, 7);
        String string6 = Messages.getString("ConnectionProperties.connectionLifecycleInterceptors");
        String str14 = CONNECTION_AND_AUTH_CATEGORY;
        this.connectionLifecycleInterceptors = new StringConnectionProperty("connectionLifecycleInterceptors", (String) null, string6, "5.1.4", str14, Integer.MAX_VALUE);
        this.connectTimeout = new IntegerConnectionProperty("connectTimeout", 0, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.connectTimeout"), "3.0.1", str14, 9);
        String str15 = str3;
        this.continueBatchOnError = new BooleanConnectionProperty("continueBatchOnError", true, Messages.getString("ConnectionProperties.continueBatchOnError"), "3.0.3", str15, Integer.MIN_VALUE);
        this.createDatabaseIfNotExist = new BooleanConnectionProperty("createDatabaseIfNotExist", false, Messages.getString("ConnectionProperties.createDatabaseIfNotExist"), "3.1.9", str15, Integer.MIN_VALUE);
        this.defaultFetchSize = new IntegerConnectionProperty("defaultFetchSize", 0, Messages.getString("ConnectionProperties.defaultFetchSize"), "3.1.9", str4, Integer.MIN_VALUE);
        this.detectServerPreparedStmts = new BooleanConnectionProperty("useServerPrepStmts", false, Messages.getString("ConnectionProperties.useServerPrepStmts"), "3.1.0", str3, Integer.MIN_VALUE);
        String str16 = str4;
        this.dontTrackOpenResources = new BooleanConnectionProperty("dontTrackOpenResources", false, Messages.getString("ConnectionProperties.dontTrackOpenResources"), "3.1.7", str16, Integer.MIN_VALUE);
        this.dumpQueriesOnException = new BooleanConnectionProperty("dumpQueriesOnException", false, Messages.getString("ConnectionProperties.dumpQueriesOnException"), "3.1.3", str8, Integer.MIN_VALUE);
        this.dynamicCalendars = new BooleanConnectionProperty("dynamicCalendars", false, Messages.getString("ConnectionProperties.dynamicCalendars"), "3.1.5", str16, Integer.MIN_VALUE);
        this.elideSetAutoCommits = new BooleanConnectionProperty("elideSetAutoCommits", false, Messages.getString("ConnectionProperties.eliseSetAutoCommit"), "3.1.3", str16, Integer.MIN_VALUE);
        String str17 = str3;
        this.emptyStringsConvertToZero = new BooleanConnectionProperty("emptyStringsConvertToZero", true, Messages.getString("ConnectionProperties.emptyStringsConvertToZero"), "3.1.8", str17, Integer.MIN_VALUE);
        this.emulateLocators = new BooleanConnectionProperty("emulateLocators", false, Messages.getString("ConnectionProperties.emulateLocators"), "3.1.0", str17, Integer.MIN_VALUE);
        this.emulateUnsupportedPstmts = new BooleanConnectionProperty("emulateUnsupportedPstmts", true, Messages.getString("ConnectionProperties.emulateUnsupportedPstmts"), "3.1.7", str17, Integer.MIN_VALUE);
        this.enablePacketDebug = new BooleanConnectionProperty("enablePacketDebug", false, Messages.getString("ConnectionProperties.enablePacketDebug"), "3.1.3", str8, Integer.MIN_VALUE);
        this.enableQueryTimeouts = new BooleanConnectionProperty("enableQueryTimeouts", true, Messages.getString("ConnectionProperties.enableQueryTimeouts"), "5.0.6", str4, Integer.MIN_VALUE);
        this.explainSlowQueries = new BooleanConnectionProperty("explainSlowQueries", false, Messages.getString("ConnectionProperties.explainSlowQueries"), "3.1.2", str8, Integer.MIN_VALUE);
        String str18 = str3;
        this.exceptionInterceptors = new StringConnectionProperty("exceptionInterceptors", (String) null, Messages.getString("ConnectionProperties.exceptionInterceptors"), "5.1.8", str18, Integer.MIN_VALUE);
        this.failOverReadOnly = new BooleanConnectionProperty("failOverReadOnly", true, Messages.getString("ConnectionProperties.failoverReadOnly"), "3.0.12", str6, 2);
        this.gatherPerformanceMetrics = new BooleanConnectionProperty("gatherPerfMetrics", false, Messages.getString("ConnectionProperties.gatherPerfMetrics"), "3.1.2", str8, 1);
        this.generateSimpleParameterMetadata = new BooleanConnectionProperty("generateSimpleParameterMetadata", false, Messages.getString("ConnectionProperties.generateSimpleParameterMetadata"), "5.0.5", str18, Integer.MIN_VALUE);
        this.highAvailabilityAsBoolean = false;
        this.holdResultsOpenOverStatementClose = new BooleanConnectionProperty("holdResultsOpenOverStatementClose", false, Messages.getString("ConnectionProperties.holdRSOpenOverStmtClose"), "3.1.7", str4, Integer.MIN_VALUE);
        String str19 = str8;
        this.includeInnodbStatusInDeadlockExceptions = new BooleanConnectionProperty("includeInnodbStatusInDeadlockExceptions", false, Messages.getString("ConnectionProperties.includeInnodbStatusInDeadlockExceptions"), "5.0.7", str19, Integer.MIN_VALUE);
        this.includeThreadDumpInDeadlockExceptions = new BooleanConnectionProperty("includeThreadDumpInDeadlockExceptions", false, Messages.getString("ConnectionProperties.includeThreadDumpInDeadlockExceptions"), "5.1.15", str19, Integer.MIN_VALUE);
        this.includeThreadNamesAsStatementComment = new BooleanConnectionProperty("includeThreadNamesAsStatementComment", false, Messages.getString("ConnectionProperties.includeThreadNamesAsStatementComment"), "5.1.15", str19, Integer.MIN_VALUE);
        this.ignoreNonTxTables = new BooleanConnectionProperty("ignoreNonTxTables", false, Messages.getString("ConnectionProperties.ignoreNonTxTables"), "3.0.9", str3, Integer.MIN_VALUE);
        this.initialTimeout = new IntegerConnectionProperty("initialTimeout", 2, 1, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.initialTimeout"), "1.1", str6, 5);
        this.isInteractiveClient = new BooleanConnectionProperty("interactiveClient", false, Messages.getString("ConnectionProperties.interactiveClient"), "3.1.0", str14, Integer.MIN_VALUE);
        BooleanConnectionProperty booleanConnectionProperty = new BooleanConnectionProperty("jdbcCompliantTruncation", true, Messages.getString("ConnectionProperties.jdbcCompliantTruncation"), "3.1.2", str3, Integer.MIN_VALUE);
        this.jdbcCompliantTruncation = booleanConnectionProperty;
        this.jdbcCompliantTruncationForReads = booleanConnectionProperty.getValueAsBoolean();
        this.largeRowSizeThreshold = new MemorySizeConnectionProperty("largeRowSizeThreshold", 2048, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.largeRowSizeThreshold"), "5.1.1", str10, Integer.MIN_VALUE);
        String str20 = str4;
        this.loadBalanceStrategy = new StringConnectionProperty("loadBalanceStrategy", "random", (String[]) null, Messages.getString("ConnectionProperties.loadBalanceStrategy"), "5.0.6", str20, Integer.MIN_VALUE);
        this.serverAffinityOrder = new StringConnectionProperty(ServerAffinityStrategy.AFFINITY_ORDER, "", (String[]) null, Messages.getString("ConnectionProperties.serverAffinityOrder"), "5.1.43", str20, Integer.MIN_VALUE);
        String str21 = str3;
        this.loadBalanceBlacklistTimeout = new IntegerConnectionProperty(LoadBalancedConnectionProxy.BLACKLIST_TIMEOUT_PROPERTY_KEY, 0, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.loadBalanceBlacklistTimeout"), "5.1.0", str21, Integer.MIN_VALUE);
        this.loadBalancePingTimeout = new IntegerConnectionProperty("loadBalancePingTimeout", 0, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.loadBalancePingTimeout"), "5.1.13", str21, Integer.MIN_VALUE);
        String str22 = str3;
        this.loadBalanceValidateConnectionOnSwapServer = new BooleanConnectionProperty("loadBalanceValidateConnectionOnSwapServer", false, Messages.getString("ConnectionProperties.loadBalanceValidateConnectionOnSwapServer"), "5.1.13", str22, Integer.MIN_VALUE);
        this.loadBalanceConnectionGroup = new StringConnectionProperty("loadBalanceConnectionGroup", (String) null, Messages.getString("ConnectionProperties.loadBalanceConnectionGroup"), "5.1.13", str22, Integer.MIN_VALUE);
        this.loadBalanceExceptionChecker = new StringConnectionProperty("loadBalanceExceptionChecker", "com.mysql.jdbc.StandardLoadBalanceExceptionChecker", (String[]) null, Messages.getString("ConnectionProperties.loadBalanceExceptionChecker"), "5.1.13", str3, Integer.MIN_VALUE);
        String str23 = str3;
        this.loadBalanceSQLStateFailover = new StringConnectionProperty("loadBalanceSQLStateFailover", (String) null, Messages.getString("ConnectionProperties.loadBalanceSQLStateFailover"), "5.1.13", str23, Integer.MIN_VALUE);
        this.loadBalanceSQLExceptionSubclassFailover = new StringConnectionProperty("loadBalanceSQLExceptionSubclassFailover", (String) null, Messages.getString("ConnectionProperties.loadBalanceSQLExceptionSubclassFailover"), "5.1.13", str23, Integer.MIN_VALUE);
        this.loadBalanceEnableJMX = new BooleanConnectionProperty("loadBalanceEnableJMX", false, Messages.getString("ConnectionProperties.loadBalanceEnableJMX"), "5.1.13", str23, Integer.MAX_VALUE);
        String str24 = str3;
        this.loadBalanceHostRemovalGracePeriod = new IntegerConnectionProperty(LoadBalancedConnectionProxy.HOST_REMOVAL_GRACE_PERIOD_PROPERTY_KEY, 15000, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.loadBalanceHostRemovalGracePeriod"), "5.1.39", str24, Integer.MAX_VALUE);
        this.loadBalanceAutoCommitStatementRegex = new StringConnectionProperty("loadBalanceAutoCommitStatementRegex", (String) null, Messages.getString("ConnectionProperties.loadBalanceAutoCommitStatementRegex"), "5.1.15", str3, Integer.MIN_VALUE);
        this.loadBalanceAutoCommitStatementThreshold = new IntegerConnectionProperty("loadBalanceAutoCommitStatementThreshold", 0, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.loadBalanceAutoCommitStatementThreshold"), "5.1.15", str24, Integer.MIN_VALUE);
        this.localSocketAddress = new StringConnectionProperty("localSocketAddress", (String) null, Messages.getString("ConnectionProperties.localSocketAddress"), "5.0.5", str14, Integer.MIN_VALUE);
        this.locatorFetchBufferSize = new MemorySizeConnectionProperty("locatorFetchBufferSize", 1048576, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.locatorFetchBufferSize"), "3.2.1", str4, Integer.MIN_VALUE);
        String str25 = STANDARD_LOGGER_NAME;
        String str26 = str8;
        this.loggerClassName = new StringConnectionProperty("logger", str25, Messages.getString("ConnectionProperties.logger", new Object[]{Log.class.getName(), str25}), "3.1.1", str26, 0);
        this.logSlowQueries = new BooleanConnectionProperty("logSlowQueries", false, Messages.getString("ConnectionProperties.logSlowQueries"), "3.1.2", str26, Integer.MIN_VALUE);
        this.logXaCommands = new BooleanConnectionProperty("logXaCommands", false, Messages.getString("ConnectionProperties.logXaCommands"), "5.0.5", str26, Integer.MIN_VALUE);
        this.maintainTimeStats = new BooleanConnectionProperty("maintainTimeStats", true, Messages.getString("ConnectionProperties.maintainTimeStats"), "3.1.9", str4, Integer.MAX_VALUE);
        this.maintainTimeStatsAsBoolean = true;
        this.maxQuerySizeToLog = new IntegerConnectionProperty("maxQuerySizeToLog", 2048, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.maxQuerySizeToLog"), "3.1.3", str8, 4);
        String str27 = str6;
        this.maxReconnects = new IntegerConnectionProperty("maxReconnects", 3, 1, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.maxReconnects"), "1.1", str27, 4);
        this.retriesAllDown = new IntegerConnectionProperty("retriesAllDown", 120, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.retriesAllDown"), "5.1.6", str27, 4);
        this.maxRows = new IntegerConnectionProperty("maxRows", -1, -1, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.maxRows"), Messages.getString("ConnectionProperties.allVersions"), str3, Integer.MIN_VALUE);
        this.maxRowsAsInt = -1;
        this.metadataCacheSize = new IntegerConnectionProperty("metadataCacheSize", 50, 1, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.metadataCacheSize"), "3.1.1", str4, 5);
        this.netTimeoutForStreamingResults = new IntegerConnectionProperty("netTimeoutForStreamingResults", 600, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.netTimeoutForStreamingResults"), "5.1.0", str3, Integer.MIN_VALUE);
        String str28 = str3;
        this.noAccessToProcedureBodies = new BooleanConnectionProperty("noAccessToProcedureBodies", false, "When determining procedure parameter types for CallableStatements, and the connected user  can't access procedure bodies through \"SHOW CREATE PROCEDURE\" or select on mysql.proc  should the driver instead create basic metadata (all parameters reported as IN VARCHARs, but allowing registerOutParameter() to be called on them anyway) instead of throwing an exception?", "5.0.3", str28, Integer.MIN_VALUE);
        this.noDatetimeStringSync = new BooleanConnectionProperty("noDatetimeStringSync", false, Messages.getString("ConnectionProperties.noDatetimeStringSync"), "3.1.7", str28, Integer.MIN_VALUE);
        this.noTimezoneConversionForTimeType = new BooleanConnectionProperty("noTimezoneConversionForTimeType", false, Messages.getString("ConnectionProperties.noTzConversionForTimeType"), "5.0.0", str28, Integer.MIN_VALUE);
        this.noTimezoneConversionForDateType = new BooleanConnectionProperty("noTimezoneConversionForDateType", true, Messages.getString("ConnectionProperties.noTzConversionForDateType"), "5.1.35", str28, Integer.MIN_VALUE);
        this.cacheDefaultTimezone = new BooleanConnectionProperty("cacheDefaultTimezone", true, Messages.getString("ConnectionProperties.cacheDefaultTimezone"), "5.1.35", str28, Integer.MIN_VALUE);
        this.nullCatalogMeansCurrent = new BooleanConnectionProperty("nullCatalogMeansCurrent", true, Messages.getString("ConnectionProperties.nullCatalogMeansCurrent"), "3.1.8", str28, Integer.MIN_VALUE);
        this.nullNamePatternMatchesAll = new BooleanConnectionProperty("nullNamePatternMatchesAll", true, Messages.getString("ConnectionProperties.nullNamePatternMatchesAll"), "3.1.8", str28, Integer.MIN_VALUE);
        this.packetDebugBufferSize = new IntegerConnectionProperty("packetDebugBufferSize", 20, 1, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.packetDebugBufferSize"), "3.1.3", str8, 7);
        this.padCharsWithSpace = new BooleanConnectionProperty("padCharsWithSpace", false, Messages.getString("ConnectionProperties.padCharsWithSpace"), "5.0.6", str3, Integer.MIN_VALUE);
        this.paranoid = new BooleanConnectionProperty("paranoid", false, Messages.getString("ConnectionProperties.paranoid"), "3.0.1", str, Integer.MIN_VALUE);
        String str29 = str3;
        this.pedantic = new BooleanConnectionProperty("pedantic", false, Messages.getString("ConnectionProperties.pedantic"), "3.0.0", str29, Integer.MIN_VALUE);
        this.pinGlobalTxToPhysicalConnection = new BooleanConnectionProperty("pinGlobalTxToPhysicalConnection", false, Messages.getString("ConnectionProperties.pinGlobalTxToPhysicalConnection"), "5.0.1", str29, Integer.MIN_VALUE);
        this.populateInsertRowWithDefaultValues = new BooleanConnectionProperty("populateInsertRowWithDefaultValues", false, Messages.getString("ConnectionProperties.populateInsertRowWithDefaultValues"), "5.0.5", str29, Integer.MIN_VALUE);
        String str30 = str4;
        this.preparedStatementCacheSize = new IntegerConnectionProperty("prepStmtCacheSize", 25, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.prepStmtCacheSize"), "3.0.10", str30, 10);
        this.preparedStatementCacheSqlLimit = new IntegerConnectionProperty("prepStmtCacheSqlLimit", 256, 1, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.prepStmtCacheSqlLimit"), "3.0.10", str30, 11);
        this.parseInfoCacheFactory = new StringConnectionProperty("parseInfoCacheFactory", PerConnectionLRUFactory.class.getName(), Messages.getString("ConnectionProperties.parseInfoCacheFactory"), "5.1.1", str4, 12);
        this.processEscapeCodesForPrepStmts = new BooleanConnectionProperty("processEscapeCodesForPrepStmts", true, Messages.getString("ConnectionProperties.processEscapeCodesForPrepStmts"), "3.1.12", str3, Integer.MIN_VALUE);
        String str31 = str8;
        this.profilerEventHandler = new StringConnectionProperty("profilerEventHandler", "com.mysql.jdbc.profiler.LoggingProfilerEventHandler", Messages.getString("ConnectionProperties.profilerEventHandler"), "5.1.6", str31, Integer.MIN_VALUE);
        this.profileSql = new StringConnectionProperty("profileSql", (String) null, Messages.getString("ConnectionProperties.profileSqlDeprecated"), "2.0.14", str31, 3);
        this.profileSQL = new BooleanConnectionProperty("profileSQL", false, Messages.getString("ConnectionProperties.profileSQL"), "3.1.0", str31, 1);
        this.profileSQLAsBoolean = false;
        this.propertiesTransform = new StringConnectionProperty(NonRegisteringDriver.PROPERTIES_TRANSFORM_KEY, (String) null, Messages.getString("ConnectionProperties.connectionPropertiesTransform"), "3.1.4", str14, Integer.MIN_VALUE);
        this.queriesBeforeRetryMaster = new IntegerConnectionProperty("queriesBeforeRetryMaster", 50, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.queriesBeforeRetryMaster"), "3.0.2", str6, 7);
        String str32 = str3;
        this.queryTimeoutKillsConnection = new BooleanConnectionProperty("queryTimeoutKillsConnection", false, Messages.getString("ConnectionProperties.queryTimeoutKillsConnection"), "5.1.9", str32, Integer.MIN_VALUE);
        this.reconnectAtTxEnd = new BooleanConnectionProperty("reconnectAtTxEnd", false, Messages.getString("ConnectionProperties.reconnectAtTxEnd"), "3.0.10", str6, 4);
        this.reconnectTxAtEndAsBoolean = false;
        this.relaxAutoCommit = new BooleanConnectionProperty("relaxAutoCommit", false, Messages.getString("ConnectionProperties.relaxAutoCommit"), "2.0.13", str32, Integer.MIN_VALUE);
        this.reportMetricsIntervalMillis = new IntegerConnectionProperty("reportMetricsIntervalMillis", 30000, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.reportMetricsIntervalMillis"), "3.1.2", str8, 3);
        String str33 = str;
        this.requireSSL = new BooleanConnectionProperty("requireSSL", false, Messages.getString("ConnectionProperties.requireSSL"), "3.1.0", str33, 3);
        this.resourceId = new StringConnectionProperty("resourceId", (String) null, Messages.getString("ConnectionProperties.resourceId"), "5.0.1", str6, Integer.MIN_VALUE);
        this.resultSetSizeThreshold = new IntegerConnectionProperty("resultSetSizeThreshold", 100, Messages.getString("ConnectionProperties.resultSetSizeThreshold"), "5.0.5", str8, Integer.MIN_VALUE);
        this.retainStatementAfterResultSetClose = new BooleanConnectionProperty("retainStatementAfterResultSetClose", false, Messages.getString("ConnectionProperties.retainStatementAfterResultSetClose"), "3.1.11", str3, Integer.MIN_VALUE);
        this.rewriteBatchedStatements = new BooleanConnectionProperty("rewriteBatchedStatements", false, Messages.getString("ConnectionProperties.rewriteBatchedStatements"), "3.1.13", str4, Integer.MIN_VALUE);
        String str34 = str3;
        this.rollbackOnPooledClose = new BooleanConnectionProperty("rollbackOnPooledClose", true, Messages.getString("ConnectionProperties.rollbackOnPooledClose"), "3.0.15", str34, Integer.MIN_VALUE);
        this.roundRobinLoadBalance = new BooleanConnectionProperty("roundRobinLoadBalance", false, Messages.getString("ConnectionProperties.roundRobinLoadBalance"), "3.1.2", str6, 5);
        this.runningCTS13 = new BooleanConnectionProperty("runningCTS13", false, Messages.getString("ConnectionProperties.runningCTS13"), "3.1.7", str34, Integer.MIN_VALUE);
        String str35 = str6;
        this.secondsBeforeRetryMaster = new IntegerConnectionProperty("secondsBeforeRetryMaster", 30, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.secondsBeforeRetryMaster"), "3.0.2", str35, 8);
        this.selfDestructOnPingSecondsLifetime = new IntegerConnectionProperty("selfDestructOnPingSecondsLifetime", 0, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.selfDestructOnPingSecondsLifetime"), "5.1.6", str35, Integer.MAX_VALUE);
        this.selfDestructOnPingMaxOperations = new IntegerConnectionProperty("selfDestructOnPingMaxOperations", 0, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.selfDestructOnPingMaxOperations"), "5.1.6", str35, Integer.MAX_VALUE);
        this.replicationEnableJMX = new BooleanConnectionProperty("replicationEnableJMX", false, Messages.getString("ConnectionProperties.loadBalanceEnableJMX"), "5.1.27", str6, Integer.MAX_VALUE);
        String str36 = str3;
        this.serverTimezone = new StringConnectionProperty("serverTimezone", (String) null, Messages.getString("ConnectionProperties.serverTimezone"), "3.0.2", str36, Integer.MIN_VALUE);
        this.sessionVariables = new StringConnectionProperty("sessionVariables", (String) null, Messages.getString("ConnectionProperties.sessionVariables"), "3.1.8", str36, Integer.MAX_VALUE);
        this.slowQueryThresholdMillis = new IntegerConnectionProperty("slowQueryThresholdMillis", 2000, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.slowQueryThresholdMillis"), "3.1.2", str8, 9);
        this.slowQueryThresholdNanos = new LongConnectionProperty("slowQueryThresholdNanos", 0, Messages.getString("ConnectionProperties.slowQueryThresholdNanos"), "5.0.7", str8, 10);
        this.socketFactoryClassName = new StringConnectionProperty("socketFactory", StandardSocketFactory.class.getName(), Messages.getString("ConnectionProperties.socketFactory"), "3.0.3", str14, 4);
        String string7 = Messages.getString("ConnectionProperties.socksProxyHost");
        String str37 = NETWORK_CATEGORY;
        this.socksProxyHost = new StringConnectionProperty("socksProxyHost", (String) null, string7, "5.1.34", str37, 1);
        this.socksProxyPort = new IntegerConnectionProperty("socksProxyPort", SocksProxySocketFactory.SOCKS_DEFAULT_PORT, 0, SupportMenu.USER_MASK, Messages.getString("ConnectionProperties.socksProxyPort"), "5.1.34", str37, 2);
        this.socketTimeout = new IntegerConnectionProperty("socketTimeout", 0, 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.socketTimeout"), "3.0.1", str14, 10);
        String str38 = str3;
        this.statementInterceptors = new StringConnectionProperty("statementInterceptors", (String) null, Messages.getString("ConnectionProperties.statementInterceptors"), "5.1.1", str38, Integer.MIN_VALUE);
        this.strictFloatingPoint = new BooleanConnectionProperty("strictFloatingPoint", false, Messages.getString("ConnectionProperties.strictFloatingPoint"), "3.0.0", str38, Integer.MIN_VALUE);
        this.strictUpdates = new BooleanConnectionProperty("strictUpdates", true, Messages.getString("ConnectionProperties.strictUpdates"), "3.0.4", str38, Integer.MIN_VALUE);
        this.overrideSupportsIntegrityEnhancementFacility = new BooleanConnectionProperty("overrideSupportsIntegrityEnhancementFacility", false, Messages.getString("ConnectionProperties.overrideSupportsIEF"), "3.1.12", str38, Integer.MIN_VALUE);
        String str39 = str37;
        this.tcpNoDelay = new BooleanConnectionProperty(StandardSocketFactory.TCP_NO_DELAY_PROPERTY_NAME, Boolean.valueOf("true").booleanValue(), Messages.getString("ConnectionProperties.tcpNoDelay"), "5.0.7", str39, Integer.MIN_VALUE);
        this.tcpKeepAlive = new BooleanConnectionProperty(StandardSocketFactory.TCP_KEEP_ALIVE_PROPERTY_NAME, Boolean.valueOf("true").booleanValue(), Messages.getString("ConnectionProperties.tcpKeepAlive"), "5.0.7", str39, Integer.MIN_VALUE);
        String str40 = str37;
        this.tcpRcvBuf = new IntegerConnectionProperty(StandardSocketFactory.TCP_RCV_BUF_PROPERTY_NAME, Integer.parseInt("0"), 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.tcpSoRcvBuf"), "5.0.7", str40, Integer.MIN_VALUE);
        this.tcpSndBuf = new IntegerConnectionProperty(StandardSocketFactory.TCP_SND_BUF_PROPERTY_NAME, Integer.parseInt("0"), 0, Integer.MAX_VALUE, Messages.getString("ConnectionProperties.tcpSoSndBuf"), "5.0.7", str40, Integer.MIN_VALUE);
        this.tcpTrafficClass = new IntegerConnectionProperty(StandardSocketFactory.TCP_TRAFFIC_CLASS_PROPERTY_NAME, Integer.parseInt("0"), 0, 255, Messages.getString("ConnectionProperties.tcpTrafficClass"), "5.0.7", str40, Integer.MIN_VALUE);
        String str41 = str3;
        this.tinyInt1isBit = new BooleanConnectionProperty("tinyInt1isBit", true, Messages.getString("ConnectionProperties.tinyInt1isBit"), "3.0.16", str41, Integer.MIN_VALUE);
        this.traceProtocol = new BooleanConnectionProperty("traceProtocol", false, Messages.getString("ConnectionProperties.traceProtocol"), "3.1.2", str8, Integer.MIN_VALUE);
        this.treatUtilDateAsTimestamp = new BooleanConnectionProperty("treatUtilDateAsTimestamp", true, Messages.getString("ConnectionProperties.treatUtilDateAsTimestamp"), "5.0.5", str41, Integer.MIN_VALUE);
        this.transformedBitIsBoolean = new BooleanConnectionProperty("transformedBitIsBoolean", false, Messages.getString("ConnectionProperties.transformedBitIsBoolean"), "3.1.9", str41, Integer.MIN_VALUE);
        this.useBlobToStoreUTF8OutsideBMP = new BooleanConnectionProperty("useBlobToStoreUTF8OutsideBMP", false, Messages.getString("ConnectionProperties.useBlobToStoreUTF8OutsideBMP"), "5.1.3", str41, 128);
        this.utf8OutsideBmpExcludedColumnNamePattern = new StringConnectionProperty("utf8OutsideBmpExcludedColumnNamePattern", (String) null, Messages.getString("ConnectionProperties.utf8OutsideBmpExcludedColumnNamePattern"), "5.1.3", str41, 129);
        this.utf8OutsideBmpIncludedColumnNamePattern = new StringConnectionProperty("utf8OutsideBmpIncludedColumnNamePattern", (String) null, Messages.getString("ConnectionProperties.utf8OutsideBmpIncludedColumnNamePattern"), "5.1.3", str41, 129);
        this.useCompression = new BooleanConnectionProperty("useCompression", false, Messages.getString("ConnectionProperties.useCompression"), "3.0.17", str14, Integer.MIN_VALUE);
        this.useColumnNamesInFindColumn = new BooleanConnectionProperty("useColumnNamesInFindColumn", false, Messages.getString("ConnectionProperties.useColumnNamesInFindColumn"), "5.1.7", str41, Integer.MAX_VALUE);
        this.useConfigs = new StringConnectionProperty(NonRegisteringDriver.USE_CONFIG_PROPERTY_KEY, (String) null, Messages.getString("ConnectionProperties.useConfigs"), "3.1.5", str14, Integer.MAX_VALUE);
        String str42 = str4;
        this.useCursorFetch = new BooleanConnectionProperty("useCursorFetch", false, Messages.getString("ConnectionProperties.useCursorFetch"), "5.0.0", str42, Integer.MAX_VALUE);
        this.useDynamicCharsetInfo = new BooleanConnectionProperty("useDynamicCharsetInfo", true, Messages.getString("ConnectionProperties.useDynamicCharsetInfo"), "5.0.6", str42, Integer.MIN_VALUE);
        this.useDirectRowUnpack = new BooleanConnectionProperty("useDirectRowUnpack", true, "Use newer result set row unpacking code that skips a copy from network buffers  to a MySQL packet instance and instead reads directly into the result set row data buffers.", "5.1.1", str42, Integer.MIN_VALUE);
        this.useFastIntParsing = new BooleanConnectionProperty("useFastIntParsing", true, Messages.getString("ConnectionProperties.useFastIntParsing"), "3.1.4", str42, Integer.MIN_VALUE);
        this.useFastDateParsing = new BooleanConnectionProperty("useFastDateParsing", true, Messages.getString("ConnectionProperties.useFastDateParsing"), "5.0.5", str42, Integer.MIN_VALUE);
        String str43 = str3;
        this.useHostsInPrivileges = new BooleanConnectionProperty("useHostsInPrivileges", true, Messages.getString("ConnectionProperties.useHostsInPrivileges"), "3.0.2", str43, Integer.MIN_VALUE);
        this.useInformationSchema = new BooleanConnectionProperty("useInformationSchema", false, Messages.getString("ConnectionProperties.useInformationSchema"), "5.0.0", str43, Integer.MIN_VALUE);
        this.useJDBCCompliantTimezoneShift = new BooleanConnectionProperty("useJDBCCompliantTimezoneShift", false, Messages.getString("ConnectionProperties.useJDBCCompliantTimezoneShift"), "5.0.0", str43, Integer.MIN_VALUE);
        String str44 = str4;
        this.useLocalSessionState = new BooleanConnectionProperty("useLocalSessionState", false, Messages.getString("ConnectionProperties.useLocalSessionState"), "3.1.7", str44, 5);
        this.useLocalTransactionState = new BooleanConnectionProperty("useLocalTransactionState", false, Messages.getString("ConnectionProperties.useLocalTransactionState"), "5.1.7", str44, 6);
        String str45 = str3;
        this.useLegacyDatetimeCode = new BooleanConnectionProperty("useLegacyDatetimeCode", true, Messages.getString("ConnectionProperties.useLegacyDatetimeCode"), "5.1.6", str45, Integer.MIN_VALUE);
        this.sendFractionalSeconds = new BooleanConnectionProperty("sendFractionalSeconds", true, Messages.getString("ConnectionProperties.sendFractionalSeconds"), "5.1.37", str45, Integer.MIN_VALUE);
        this.useNanosForElapsedTime = new BooleanConnectionProperty("useNanosForElapsedTime", false, Messages.getString("ConnectionProperties.useNanosForElapsedTime"), "5.0.7", str8, Integer.MIN_VALUE);
        this.useOldAliasMetadataBehavior = new BooleanConnectionProperty("useOldAliasMetadataBehavior", false, Messages.getString("ConnectionProperties.useOldAliasMetadataBehavior"), "5.0.4", str45, Integer.MIN_VALUE);
        this.useOldUTF8Behavior = new BooleanConnectionProperty("useOldUTF8Behavior", false, Messages.getString("ConnectionProperties.useOldUtf8Behavior"), "3.1.6", str45, Integer.MIN_VALUE);
        this.useOldUTF8BehaviorAsBoolean = false;
        this.useOnlyServerErrorMessages = new BooleanConnectionProperty("useOnlyServerErrorMessages", true, Messages.getString("ConnectionProperties.useOnlyServerErrorMessages"), "3.0.15", str45, Integer.MIN_VALUE);
        this.useReadAheadInput = new BooleanConnectionProperty("useReadAheadInput", true, Messages.getString("ConnectionProperties.useReadAheadInput"), "3.1.5", str4, Integer.MIN_VALUE);
        this.useSqlStateCodes = new BooleanConnectionProperty("useSqlStateCodes", true, Messages.getString("ConnectionProperties.useSqlStateCodes"), "3.1.3", str3, Integer.MIN_VALUE);
        this.useSSL = new BooleanConnectionProperty("useSSL", false, Messages.getString("ConnectionProperties.useSSL"), "3.0.2", str33, 2);
        String str46 = str3;
        this.useSSPSCompatibleTimezoneShift = new BooleanConnectionProperty("useSSPSCompatibleTimezoneShift", false, Messages.getString("ConnectionProperties.useSSPSCompatibleTimezoneShift"), "5.0.5", str46, Integer.MIN_VALUE);
        this.useStreamLengthsInPrepStmts = new BooleanConnectionProperty("useStreamLengthsInPrepStmts", true, Messages.getString("ConnectionProperties.useStreamLengthsInPrepStmts"), "3.0.2", str46, Integer.MIN_VALUE);
        this.useTimezone = new BooleanConnectionProperty("useTimezone", false, Messages.getString("ConnectionProperties.useTimezone"), "3.0.2", str46, Integer.MIN_VALUE);
        this.useUltraDevWorkAround = new BooleanConnectionProperty("ultraDevHack", false, Messages.getString("ConnectionProperties.ultraDevHack"), "2.0.3", str46, Integer.MIN_VALUE);
        this.useUnbufferedInput = new BooleanConnectionProperty("useUnbufferedInput", true, Messages.getString("ConnectionProperties.useUnbufferedInput"), "3.0.11", str46, Integer.MIN_VALUE);
        this.useUnicode = new BooleanConnectionProperty("useUnicode", true, Messages.getString("ConnectionProperties.useUnicode"), "1.1g", str46, 0);
        this.useUnicodeAsBoolean = true;
        this.useUsageAdvisor = new BooleanConnectionProperty("useUsageAdvisor", false, Messages.getString("ConnectionProperties.useUsageAdvisor"), "3.1.1", str8, 10);
        this.useUsageAdvisorAsBoolean = false;
        this.yearIsDateType = new BooleanConnectionProperty("yearIsDateType", true, Messages.getString("ConnectionProperties.yearIsDateType"), "3.1.9", str46, Integer.MIN_VALUE);
        this.zeroDateTimeBehavior = new StringConnectionProperty("zeroDateTimeBehavior", ZERO_DATETIME_BEHAVIOR_EXCEPTION, new String[]{ZERO_DATETIME_BEHAVIOR_EXCEPTION, ZERO_DATETIME_BEHAVIOR_ROUND, ZERO_DATETIME_BEHAVIOR_CONVERT_TO_NULL}, Messages.getString("ConnectionProperties.zeroDateTimeBehavior", new Object[]{ZERO_DATETIME_BEHAVIOR_EXCEPTION, ZERO_DATETIME_BEHAVIOR_ROUND, ZERO_DATETIME_BEHAVIOR_CONVERT_TO_NULL}), "3.1.4", str3, Integer.MIN_VALUE);
        this.useJvmCharsetConverters = new BooleanConnectionProperty("useJvmCharsetConverters", false, Messages.getString("ConnectionProperties.useJvmCharsetConverters"), "5.0.1", str4, Integer.MIN_VALUE);
        this.useGmtMillisForDatetimes = new BooleanConnectionProperty("useGmtMillisForDatetimes", false, Messages.getString("ConnectionProperties.useGmtMillisForDatetimes"), "3.1.12", str3, Integer.MIN_VALUE);
        this.dumpMetadataOnColumnNotFound = new BooleanConnectionProperty("dumpMetadataOnColumnNotFound", false, Messages.getString("ConnectionProperties.dumpMetadataOnColumnNotFound"), "3.1.13", str8, Integer.MIN_VALUE);
        this.clientCertificateKeyStoreUrl = new StringConnectionProperty("clientCertificateKeyStoreUrl", (String) null, Messages.getString("ConnectionProperties.clientCertificateKeyStoreUrl"), "5.1.0", str33, 5);
        this.trustCertificateKeyStoreUrl = new StringConnectionProperty("trustCertificateKeyStoreUrl", (String) null, Messages.getString("ConnectionProperties.trustCertificateKeyStoreUrl"), "5.1.0", str33, 8);
        this.clientCertificateKeyStoreType = new StringConnectionProperty("clientCertificateKeyStoreType", "JKS", Messages.getString("ConnectionProperties.clientCertificateKeyStoreType"), "5.1.0", str33, 6);
        this.clientCertificateKeyStorePassword = new StringConnectionProperty("clientCertificateKeyStorePassword", (String) null, Messages.getString("ConnectionProperties.clientCertificateKeyStorePassword"), "5.1.0", str33, 7);
        this.trustCertificateKeyStoreType = new StringConnectionProperty("trustCertificateKeyStoreType", "JKS", Messages.getString("ConnectionProperties.trustCertificateKeyStoreType"), "5.1.0", str33, 9);
        this.trustCertificateKeyStorePassword = new StringConnectionProperty("trustCertificateKeyStorePassword", (String) null, Messages.getString("ConnectionProperties.trustCertificateKeyStorePassword"), "5.1.0", str33, 10);
        this.verifyServerCertificate = new BooleanConnectionProperty("verifyServerCertificate", true, Messages.getString("ConnectionProperties.verifyServerCertificate"), "5.1.6", str33, 4);
        this.useAffectedRows = new BooleanConnectionProperty("useAffectedRows", false, Messages.getString("ConnectionProperties.useAffectedRows"), "5.1.7", str3, Integer.MIN_VALUE);
        this.passwordCharacterEncoding = new StringConnectionProperty("passwordCharacterEncoding", (String) null, Messages.getString("ConnectionProperties.passwordCharacterEncoding"), "5.1.7", str33, Integer.MIN_VALUE);
        this.maxAllowedPacket = new IntegerConnectionProperty("maxAllowedPacket", -1, Messages.getString("ConnectionProperties.maxAllowedPacket"), "5.1.8", str37, Integer.MIN_VALUE);
        String str47 = str14;
        this.authenticationPlugins = new StringConnectionProperty("authenticationPlugins", (String) null, Messages.getString("ConnectionProperties.authenticationPlugins"), "5.1.19", str47, Integer.MIN_VALUE);
        this.disabledAuthenticationPlugins = new StringConnectionProperty("disabledAuthenticationPlugins", (String) null, Messages.getString("ConnectionProperties.disabledAuthenticationPlugins"), "5.1.19", str47, Integer.MIN_VALUE);
        this.defaultAuthenticationPlugin = new StringConnectionProperty("defaultAuthenticationPlugin", "com.mysql.jdbc.authentication.MysqlNativePasswordPlugin", Messages.getString("ConnectionProperties.defaultAuthenticationPlugin"), "5.1.19", str47, Integer.MIN_VALUE);
        this.disconnectOnExpiredPasswords = new BooleanConnectionProperty("disconnectOnExpiredPasswords", true, Messages.getString("ConnectionProperties.disconnectOnExpiredPasswords"), "5.1.23", str47, Integer.MIN_VALUE);
        String str48 = str3;
        this.getProceduresReturnsFunctions = new BooleanConnectionProperty("getProceduresReturnsFunctions", true, Messages.getString("ConnectionProperties.getProceduresReturnsFunctions"), "5.1.26", str48, Integer.MIN_VALUE);
        this.detectCustomCollations = new BooleanConnectionProperty("detectCustomCollations", false, Messages.getString("ConnectionProperties.detectCustomCollations"), "5.1.29", str48, Integer.MIN_VALUE);
        this.serverRSAPublicKeyFile = new StringConnectionProperty("serverRSAPublicKeyFile", (String) null, Messages.getString("ConnectionProperties.serverRSAPublicKeyFile"), "5.1.31", str33, Integer.MIN_VALUE);
        this.allowPublicKeyRetrieval = new BooleanConnectionProperty("allowPublicKeyRetrieval", false, Messages.getString("ConnectionProperties.allowPublicKeyRetrieval"), "5.1.31", str33, Integer.MIN_VALUE);
        String str49 = str4;
        this.dontCheckOnDuplicateKeyUpdateInSQL = new BooleanConnectionProperty("dontCheckOnDuplicateKeyUpdateInSQL", false, Messages.getString("ConnectionProperties.dontCheckOnDuplicateKeyUpdateInSQL"), "5.1.32", str49, Integer.MIN_VALUE);
        this.readOnlyPropagatesToServer = new BooleanConnectionProperty("readOnlyPropagatesToServer", true, Messages.getString("ConnectionProperties.readOnlyPropagatesToServer"), "5.1.35", str49, Integer.MIN_VALUE);
        this.enabledSSLCipherSuites = new StringConnectionProperty("enabledSSLCipherSuites", (String) null, Messages.getString("ConnectionProperties.enabledSSLCipherSuites"), "5.1.35", str33, 11);
        this.enabledTLSProtocols = new StringConnectionProperty("enabledTLSProtocols", (String) null, Messages.getString("ConnectionProperties.enabledTLSProtocols"), "5.1.44", str33, 12);
        this.enableEscapeProcessing = new BooleanConnectionProperty("enableEscapeProcessing", true, Messages.getString("ConnectionProperties.enableEscapeProcessing"), "5.1.37", str4, Integer.MIN_VALUE);
    }

    static class BooleanConnectionProperty extends ConnectionProperty implements Serializable {
        private static final long serialVersionUID = 2540132501709159404L;

        BooleanConnectionProperty(String propertyNameToSet, boolean defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
            super(propertyNameToSet, Boolean.valueOf(defaultValueToSet), (String[]) null, 0, 0, descriptionToSet, sinceVersionToSet, category, orderInCategory);
        }

        /* access modifiers changed from: package-private */
        public String[] getAllowableValues() {
            return new String[]{"true", "false", "yes", "no"};
        }

        /* access modifiers changed from: package-private */
        public boolean getValueAsBoolean() {
            return ((Boolean) this.valueAsObject).booleanValue();
        }

        /* access modifiers changed from: package-private */
        public boolean hasValueConstraints() {
            return true;
        }

        /* access modifiers changed from: package-private */
        public void initializeFrom(String extractedValue, ExceptionInterceptor exceptionInterceptor) throws SQLException {
            if (extractedValue != null) {
                validateStringValues(extractedValue, exceptionInterceptor);
                this.valueAsObject = Boolean.valueOf(extractedValue.equalsIgnoreCase("TRUE") || extractedValue.equalsIgnoreCase("YES"));
                this.wasExplicitlySet = true;
            } else {
                this.valueAsObject = this.defaultValue;
            }
            this.updateCount++;
        }

        /* access modifiers changed from: package-private */
        public boolean isRangeBased() {
            return false;
        }

        /* access modifiers changed from: package-private */
        public void setValue(boolean valueFlag) {
            this.valueAsObject = Boolean.valueOf(valueFlag);
            this.wasExplicitlySet = true;
            this.updateCount++;
        }
    }

    static abstract class ConnectionProperty implements Serializable {
        static final long serialVersionUID = -6644853639584478367L;
        String[] allowableValues;
        String categoryName;
        Object defaultValue;
        String description;
        int lowerBound;
        int order;
        String propertyName;
        boolean required;
        String sinceVersion;
        int updateCount = 0;
        int upperBound;
        Object valueAsObject;
        boolean wasExplicitlySet = false;

        /* access modifiers changed from: package-private */
        public abstract boolean hasValueConstraints();

        /* access modifiers changed from: package-private */
        public abstract void initializeFrom(String str, ExceptionInterceptor exceptionInterceptor) throws SQLException;

        /* access modifiers changed from: package-private */
        public abstract boolean isRangeBased();

        public ConnectionProperty() {
        }

        ConnectionProperty(String propertyNameToSet, Object defaultValueToSet, String[] allowableValuesToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
            this.description = descriptionToSet;
            this.propertyName = propertyNameToSet;
            this.defaultValue = defaultValueToSet;
            this.valueAsObject = defaultValueToSet;
            this.allowableValues = allowableValuesToSet;
            this.lowerBound = lowerBoundToSet;
            this.upperBound = upperBoundToSet;
            this.required = false;
            this.sinceVersion = sinceVersionToSet;
            this.categoryName = category;
            this.order = orderInCategory;
        }

        /* access modifiers changed from: package-private */
        public String[] getAllowableValues() {
            return this.allowableValues;
        }

        /* access modifiers changed from: package-private */
        public String getCategoryName() {
            return this.categoryName;
        }

        /* access modifiers changed from: package-private */
        public Object getDefaultValue() {
            return this.defaultValue;
        }

        /* access modifiers changed from: package-private */
        public int getLowerBound() {
            return this.lowerBound;
        }

        /* access modifiers changed from: package-private */
        public int getOrder() {
            return this.order;
        }

        /* access modifiers changed from: package-private */
        public String getPropertyName() {
            return this.propertyName;
        }

        /* access modifiers changed from: package-private */
        public int getUpperBound() {
            return this.upperBound;
        }

        /* access modifiers changed from: package-private */
        public Object getValueAsObject() {
            return this.valueAsObject;
        }

        /* access modifiers changed from: package-private */
        public int getUpdateCount() {
            return this.updateCount;
        }

        /* access modifiers changed from: package-private */
        public boolean isExplicitlySet() {
            return this.wasExplicitlySet;
        }

        /* access modifiers changed from: package-private */
        public void initializeFrom(Properties extractFrom, ExceptionInterceptor exceptionInterceptor) throws SQLException {
            String extractedValue = extractFrom.getProperty(getPropertyName());
            extractFrom.remove(getPropertyName());
            initializeFrom(extractedValue, exceptionInterceptor);
        }

        /* access modifiers changed from: package-private */
        public void initializeFrom(Reference ref, ExceptionInterceptor exceptionInterceptor) throws SQLException {
            RefAddr refAddr = ref.get(getPropertyName());
            if (refAddr != null) {
                initializeFrom((String) refAddr.getContent(), exceptionInterceptor);
            }
        }

        /* access modifiers changed from: package-private */
        public void setCategoryName(String categoryName2) {
            this.categoryName = categoryName2;
        }

        /* access modifiers changed from: package-private */
        public void setOrder(int order2) {
            this.order = order2;
        }

        /* access modifiers changed from: package-private */
        public void setValueAsObject(Object obj) {
            this.valueAsObject = obj;
            this.updateCount++;
        }

        /* access modifiers changed from: package-private */
        public void storeTo(Reference ref) {
            if (getValueAsObject() != null) {
                ref.add(new StringRefAddr(getPropertyName(), getValueAsObject().toString()));
            }
        }

        /* access modifiers changed from: package-private */
        public DriverPropertyInfo getAsDriverPropertyInfo() {
            String str = null;
            DriverPropertyInfo dpi = new DriverPropertyInfo(this.propertyName, (String) null);
            dpi.choices = getAllowableValues();
            Object obj = this.valueAsObject;
            if (obj != null) {
                str = obj.toString();
            }
            dpi.value = str;
            dpi.required = this.required;
            dpi.description = this.description;
            return dpi;
        }

        /* access modifiers changed from: package-private */
        public void validateStringValues(String valueToValidate, ExceptionInterceptor exceptionInterceptor) throws SQLException {
            String[] validateAgainst = getAllowableValues();
            if (valueToValidate != null && validateAgainst != null && validateAgainst.length != 0) {
                int i = 0;
                while (i < validateAgainst.length) {
                    if (validateAgainst[i] == null || !validateAgainst[i].equalsIgnoreCase(valueToValidate)) {
                        i++;
                    } else {
                        return;
                    }
                }
                StringBuilder errorMessageBuf = new StringBuilder();
                errorMessageBuf.append("The connection property '");
                errorMessageBuf.append(getPropertyName());
                errorMessageBuf.append("' only accepts values of the form: ");
                if (validateAgainst.length != 0) {
                    errorMessageBuf.append("'");
                    errorMessageBuf.append(validateAgainst[0]);
                    errorMessageBuf.append("'");
                    for (int i2 = 1; i2 < validateAgainst.length - 1; i2++) {
                        errorMessageBuf.append(", ");
                        errorMessageBuf.append("'");
                        errorMessageBuf.append(validateAgainst[i2]);
                        errorMessageBuf.append("'");
                    }
                    errorMessageBuf.append(" or '");
                    errorMessageBuf.append(validateAgainst[validateAgainst.length - 1]);
                    errorMessageBuf.append("'");
                }
                errorMessageBuf.append(". The value '");
                errorMessageBuf.append(valueToValidate);
                errorMessageBuf.append("' is not in this set.");
                throw SQLError.createSQLException(errorMessageBuf.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
            }
        }
    }

    static class IntegerConnectionProperty extends ConnectionProperty implements Serializable {
        private static final long serialVersionUID = -3004305481796850832L;
        int multiplier;

        public IntegerConnectionProperty(String propertyNameToSet, Object defaultValueToSet, String[] allowableValuesToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
            super(propertyNameToSet, defaultValueToSet, allowableValuesToSet, lowerBoundToSet, upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
            this.multiplier = 1;
        }

        IntegerConnectionProperty(String propertyNameToSet, int defaultValueToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
            super(propertyNameToSet, Integer.valueOf(defaultValueToSet), (String[]) null, lowerBoundToSet, upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
            this.multiplier = 1;
        }

        IntegerConnectionProperty(String propertyNameToSet, int defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
            this(propertyNameToSet, defaultValueToSet, 0, 0, descriptionToSet, sinceVersionToSet, category, orderInCategory);
        }

        /* access modifiers changed from: package-private */
        public String[] getAllowableValues() {
            return null;
        }

        /* access modifiers changed from: package-private */
        public int getLowerBound() {
            return this.lowerBound;
        }

        /* access modifiers changed from: package-private */
        public int getUpperBound() {
            return this.upperBound;
        }

        /* access modifiers changed from: package-private */
        public int getValueAsInt() {
            return ((Integer) this.valueAsObject).intValue();
        }

        /* access modifiers changed from: package-private */
        public boolean hasValueConstraints() {
            return false;
        }

        /* access modifiers changed from: package-private */
        public void initializeFrom(String extractedValue, ExceptionInterceptor exceptionInterceptor) throws SQLException {
            if (extractedValue != null) {
                try {
                    setValue((int) (Double.valueOf(extractedValue).doubleValue() * ((double) this.multiplier)), extractedValue, exceptionInterceptor);
                } catch (NumberFormatException e) {
                    throw SQLError.createSQLException("The connection property '" + getPropertyName() + "' only accepts integer values. The value '" + extractedValue + "' can not be converted to an integer.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
                }
            } else {
                this.valueAsObject = this.defaultValue;
            }
            this.updateCount++;
        }

        /* access modifiers changed from: package-private */
        public boolean isRangeBased() {
            return getUpperBound() != getLowerBound();
        }

        /* access modifiers changed from: package-private */
        public void setValue(int intValue, ExceptionInterceptor exceptionInterceptor) throws SQLException {
            setValue(intValue, (String) null, exceptionInterceptor);
        }

        /* access modifiers changed from: package-private */
        public void setValue(int intValue, String valueAsString, ExceptionInterceptor exceptionInterceptor) throws SQLException {
            if (!isRangeBased() || (intValue >= getLowerBound() && intValue <= getUpperBound())) {
                this.valueAsObject = Integer.valueOf(intValue);
                this.wasExplicitlySet = true;
                this.updateCount++;
                return;
            }
            throw SQLError.createSQLException("The connection property '" + getPropertyName() + "' only accepts integer values in the range of " + getLowerBound() + " - " + getUpperBound() + ", the value '" + (valueAsString == null ? Integer.valueOf(intValue) : valueAsString) + "' exceeds this range.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
        }
    }

    public static class LongConnectionProperty extends IntegerConnectionProperty {
        private static final long serialVersionUID = 6068572984340480895L;

        LongConnectionProperty(String propertyNameToSet, long defaultValueToSet, long lowerBoundToSet, long upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
            super(propertyNameToSet, Long.valueOf(defaultValueToSet), (String[]) null, (int) lowerBoundToSet, (int) upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
        }

        LongConnectionProperty(String propertyNameToSet, long defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
            this(propertyNameToSet, defaultValueToSet, 0, 0, descriptionToSet, sinceVersionToSet, category, orderInCategory);
        }

        /* access modifiers changed from: package-private */
        public void setValue(long longValue, ExceptionInterceptor exceptionInterceptor) throws SQLException {
            setValue(longValue, (String) null, exceptionInterceptor);
        }

        /* access modifiers changed from: package-private */
        public void setValue(long longValue, String valueAsString, ExceptionInterceptor exceptionInterceptor) throws SQLException {
            if (!isRangeBased() || (longValue >= ((long) getLowerBound()) && longValue <= ((long) getUpperBound()))) {
                this.valueAsObject = Long.valueOf(longValue);
                this.wasExplicitlySet = true;
                this.updateCount++;
                return;
            }
            throw SQLError.createSQLException("The connection property '" + getPropertyName() + "' only accepts long integer values in the range of " + getLowerBound() + " - " + getUpperBound() + ", the value '" + (valueAsString == null ? Long.valueOf(longValue) : valueAsString) + "' exceeds this range.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
        }

        /* access modifiers changed from: package-private */
        public long getValueAsLong() {
            return ((Long) this.valueAsObject).longValue();
        }

        /* access modifiers changed from: package-private */
        public void initializeFrom(String extractedValue, ExceptionInterceptor exceptionInterceptor) throws SQLException {
            if (extractedValue != null) {
                try {
                    setValue(Double.valueOf(extractedValue).longValue(), extractedValue, exceptionInterceptor);
                } catch (NumberFormatException e) {
                    throw SQLError.createSQLException("The connection property '" + getPropertyName() + "' only accepts long integer values. The value '" + extractedValue + "' can not be converted to a long integer.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
                }
            } else {
                this.valueAsObject = this.defaultValue;
            }
            this.updateCount++;
        }
    }

    static class MemorySizeConnectionProperty extends IntegerConnectionProperty implements Serializable {
        private static final long serialVersionUID = 7351065128998572656L;
        private String valueAsString;

        MemorySizeConnectionProperty(String propertyNameToSet, int defaultValueToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
            super(propertyNameToSet, defaultValueToSet, lowerBoundToSet, upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
        }

        /* access modifiers changed from: package-private */
        public void initializeFrom(String extractedValue, ExceptionInterceptor exceptionInterceptor) throws SQLException {
            this.valueAsString = extractedValue;
            this.multiplier = 1;
            if (extractedValue != null) {
                if (extractedValue.endsWith("k") || extractedValue.endsWith("K") || extractedValue.endsWith("kb") || extractedValue.endsWith("Kb") || extractedValue.endsWith("kB") || extractedValue.endsWith("KB")) {
                    this.multiplier = 1024;
                    extractedValue = extractedValue.substring(0, StringUtils.indexOfIgnoreCase(extractedValue, "k"));
                } else if (extractedValue.endsWith("m") || extractedValue.endsWith("M") || extractedValue.endsWith("mb") || extractedValue.endsWith("Mb") || extractedValue.endsWith("mB") || extractedValue.endsWith("MB")) {
                    this.multiplier = 1048576;
                    extractedValue = extractedValue.substring(0, StringUtils.indexOfIgnoreCase(extractedValue, "m"));
                } else if (extractedValue.endsWith("g") || extractedValue.endsWith("G") || extractedValue.endsWith("gb") || extractedValue.endsWith("Gb") || extractedValue.endsWith("gB") || extractedValue.endsWith("GB")) {
                    this.multiplier = BasicMeasure.EXACTLY;
                    extractedValue = extractedValue.substring(0, StringUtils.indexOfIgnoreCase(extractedValue, "g"));
                }
            }
            super.initializeFrom(extractedValue, exceptionInterceptor);
        }

        /* access modifiers changed from: package-private */
        public void setValue(String value, ExceptionInterceptor exceptionInterceptor) throws SQLException {
            initializeFrom(value, exceptionInterceptor);
        }

        /* access modifiers changed from: package-private */
        public String getValueAsString() {
            return this.valueAsString;
        }
    }

    static class StringConnectionProperty extends ConnectionProperty implements Serializable {
        private static final long serialVersionUID = 5432127962785948272L;

        StringConnectionProperty(String propertyNameToSet, String defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
            this(propertyNameToSet, defaultValueToSet, (String[]) null, descriptionToSet, sinceVersionToSet, category, orderInCategory);
        }

        StringConnectionProperty(String propertyNameToSet, String defaultValueToSet, String[] allowableValuesToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory) {
            super(propertyNameToSet, defaultValueToSet, allowableValuesToSet, 0, 0, descriptionToSet, sinceVersionToSet, category, orderInCategory);
        }

        /* access modifiers changed from: package-private */
        public String getValueAsString() {
            return (String) this.valueAsObject;
        }

        /* access modifiers changed from: package-private */
        public boolean hasValueConstraints() {
            return this.allowableValues != null && this.allowableValues.length > 0;
        }

        /* access modifiers changed from: package-private */
        public void initializeFrom(String extractedValue, ExceptionInterceptor exceptionInterceptor) throws SQLException {
            if (extractedValue != null) {
                validateStringValues(extractedValue, exceptionInterceptor);
                this.valueAsObject = extractedValue;
                this.wasExplicitlySet = true;
            } else {
                this.valueAsObject = this.defaultValue;
            }
            this.updateCount++;
        }

        /* access modifiers changed from: package-private */
        public boolean isRangeBased() {
            return false;
        }

        /* access modifiers changed from: package-private */
        public void setValue(String valueFlag) {
            this.valueAsObject = valueFlag;
            this.wasExplicitlySet = true;
            this.updateCount++;
        }
    }

    static {
        String string = Messages.getString("ConnectionProperties.categoryConnectionAuthentication");
        CONNECTION_AND_AUTH_CATEGORY = string;
        String string2 = Messages.getString("ConnectionProperties.categoryNetworking");
        NETWORK_CATEGORY = string2;
        String string3 = Messages.getString("ConnectionProperties.categoryDebuggingProfiling");
        DEBUGING_PROFILING_CATEGORY = string3;
        String string4 = Messages.getString("ConnectionProperties.categorryHA");
        HA_CATEGORY = string4;
        String string5 = Messages.getString("ConnectionProperties.categoryMisc");
        MISC_CATEGORY = string5;
        String string6 = Messages.getString("ConnectionProperties.categoryPerformance");
        PERFORMANCE_CATEGORY = string6;
        String string7 = Messages.getString("ConnectionProperties.categorySecurity");
        SECURITY_CATEGORY = string7;
        PROPERTY_CATEGORIES = new String[]{string, string2, string4, string7, string6, string3, string5};
        try {
            Field[] declaredFields = ConnectionPropertiesImpl.class.getDeclaredFields();
            for (int i = 0; i < declaredFields.length; i++) {
                if (ConnectionProperty.class.isAssignableFrom(declaredFields[i].getType())) {
                    PROPERTY_LIST.add(declaredFields[i]);
                }
            }
        } catch (Exception ex) {
            RuntimeException rtEx = new RuntimeException();
            rtEx.initCause(ex);
            throw rtEx;
        }
    }

    public ExceptionInterceptor getExceptionInterceptor() {
        return null;
    }

    protected static DriverPropertyInfo[] exposeAsDriverPropertyInfo(Properties info, int slotsToReserve) throws SQLException {
        return new ConnectionPropertiesImpl() {
            private static final long serialVersionUID = 4257801713007640581L;
        }.exposeAsDriverPropertyInfoInternal(info, slotsToReserve);
    }

    /* access modifiers changed from: protected */
    public DriverPropertyInfo[] exposeAsDriverPropertyInfoInternal(Properties info, int slotsToReserve) throws SQLException {
        initializeProperties(info);
        int listSize = PROPERTY_LIST.size() + slotsToReserve;
        DriverPropertyInfo[] driverProperties = new DriverPropertyInfo[listSize];
        int i = slotsToReserve;
        while (i < listSize) {
            try {
                ConnectionProperty propToExpose = (ConnectionProperty) PROPERTY_LIST.get(i - slotsToReserve).get(this);
                if (info != null) {
                    propToExpose.initializeFrom(info, getExceptionInterceptor());
                }
                driverProperties[i] = propToExpose.getAsDriverPropertyInfo();
                i++;
            } catch (IllegalAccessException e) {
                throw SQLError.createSQLException(Messages.getString("ConnectionProperties.InternalPropertiesFailure"), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
            }
        }
        return driverProperties;
    }

    /* access modifiers changed from: protected */
    public Properties exposeAsProperties(Properties info) throws SQLException {
        if (info == null) {
            info = new Properties();
        }
        int numPropertiesToSet = PROPERTY_LIST.size();
        int i = 0;
        while (i < numPropertiesToSet) {
            try {
                ConnectionProperty propToGet = (ConnectionProperty) PROPERTY_LIST.get(i).get(this);
                Object propValue = propToGet.getValueAsObject();
                if (propValue != null) {
                    info.setProperty(propToGet.getPropertyName(), propValue.toString());
                }
                i++;
            } catch (IllegalAccessException e) {
                throw SQLError.createSQLException("Internal properties failure", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
            }
        }
        return info;
    }

    public Properties exposeAsProperties(Properties props, boolean explicitOnly) throws SQLException {
        if (props == null) {
            props = new Properties();
        }
        int numPropertiesToSet = PROPERTY_LIST.size();
        int i = 0;
        while (i < numPropertiesToSet) {
            try {
                ConnectionProperty propToGet = (ConnectionProperty) PROPERTY_LIST.get(i).get(this);
                Object propValue = propToGet.getValueAsObject();
                if (propValue != null && (!explicitOnly || propToGet.isExplicitlySet())) {
                    props.setProperty(propToGet.getPropertyName(), propValue.toString());
                }
                i++;
            } catch (IllegalAccessException iae) {
                throw SQLError.createSQLException("Internal properties failure", SQLError.SQL_STATE_GENERAL_ERROR, (Throwable) iae, getExceptionInterceptor());
            }
        }
        return props;
    }

    class XmlMap {
        protected Map<String, ConnectionProperty> alpha = new TreeMap();
        protected Map<Integer, Map<String, ConnectionProperty>> ordered = new TreeMap();

        XmlMap() {
        }
    }

    public String exposeAsXml() throws SQLException {
        int numCategories;
        Map<String, XmlMap> propertyListByCategory;
        int numPropertiesToSet;
        ConnectionPropertiesImpl connectionPropertiesImpl = this;
        StringBuilder xmlBuf = new StringBuilder();
        xmlBuf.append("<ConnectionProperties>");
        int numPropertiesToSet2 = PROPERTY_LIST.size();
        Map<String, XmlMap> propertyListByCategory2 = new HashMap<>();
        for (String put : PROPERTY_CATEGORIES) {
            propertyListByCategory2.put(put, new XmlMap());
        }
        String string = Messages.getString("ConnectionProperties.Username");
        String string2 = Messages.getString("ConnectionProperties.allVersions");
        String str = CONNECTION_AND_AUTH_CATEGORY;
        StringConnectionProperty userProp = new StringConnectionProperty(NonRegisteringDriver.USER_PROPERTY_KEY, (String) null, string, string2, str, -2147483647);
        String str2 = str;
        StringConnectionProperty passwordProp = new StringConnectionProperty(NonRegisteringDriver.PASSWORD_PROPERTY_KEY, (String) null, Messages.getString("ConnectionProperties.Password"), Messages.getString("ConnectionProperties.allVersions"), str2, -2147483646);
        XmlMap connectionSortMaps = propertyListByCategory2.get(str2);
        TreeMap treeMap = new TreeMap();
        treeMap.put(userProp.getPropertyName(), userProp);
        connectionSortMaps.ordered.put(Integer.valueOf(userProp.getOrder()), treeMap);
        TreeMap treeMap2 = new TreeMap();
        treeMap2.put(passwordProp.getPropertyName(), passwordProp);
        connectionSortMaps.ordered.put(new Integer(passwordProp.getOrder()), treeMap2);
        int i = 0;
        while (i < numPropertiesToSet2) {
            try {
                ConnectionProperty propToGet = (ConnectionProperty) PROPERTY_LIST.get(i).get(connectionPropertiesImpl);
                XmlMap sortMaps = (XmlMap) propertyListByCategory2.get(propToGet.getCategoryName());
                int orderInCategory = propToGet.getOrder();
                if (orderInCategory == Integer.MIN_VALUE) {
                    try {
                        sortMaps.alpha.put(propToGet.getPropertyName(), propToGet);
                        numPropertiesToSet = numPropertiesToSet2;
                    } catch (IllegalAccessException e) {
                        int i2 = numPropertiesToSet2;
                        int i3 = numCategories;
                        HashMap hashMap = propertyListByCategory2;
                        StringConnectionProperty stringConnectionProperty = userProp;
                        StringConnectionProperty stringConnectionProperty2 = passwordProp;
                        throw SQLError.createSQLException("Internal properties failure", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
                    }
                } else {
                    Integer order = Integer.valueOf(orderInCategory);
                    Map<String, ConnectionProperty> orderMap = sortMaps.ordered.get(order);
                    if (orderMap == null) {
                        orderMap = new TreeMap<>();
                        numPropertiesToSet = numPropertiesToSet2;
                        try {
                            sortMaps.ordered.put(order, orderMap);
                        } catch (IllegalAccessException e2) {
                            int i4 = numCategories;
                            HashMap hashMap2 = propertyListByCategory2;
                            StringConnectionProperty stringConnectionProperty3 = userProp;
                            StringConnectionProperty stringConnectionProperty4 = passwordProp;
                            throw SQLError.createSQLException("Internal properties failure", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
                        }
                    } else {
                        numPropertiesToSet = numPropertiesToSet2;
                    }
                    orderMap.put(propToGet.getPropertyName(), propToGet);
                }
                i++;
                connectionPropertiesImpl = this;
                numPropertiesToSet2 = numPropertiesToSet;
            } catch (IllegalAccessException e3) {
                int i5 = numPropertiesToSet2;
                int i6 = numCategories;
                HashMap hashMap3 = propertyListByCategory2;
                StringConnectionProperty stringConnectionProperty5 = userProp;
                StringConnectionProperty stringConnectionProperty6 = passwordProp;
                throw SQLError.createSQLException("Internal properties failure", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
            }
        }
        int j = 0;
        while (j < numCategories) {
            try {
                String[] strArr = PROPERTY_CATEGORIES;
                XmlMap sortMaps2 = propertyListByCategory2.get(strArr[j]);
                xmlBuf.append("\n <PropertyCategory name=\"");
                xmlBuf.append(strArr[j]);
                xmlBuf.append("\">");
                Iterator it = sortMaps2.ordered.values().iterator();
                while (true) {
                    numCategories = numCategories;
                    propertyListByCategory = propertyListByCategory2;
                    if (!it.hasNext()) {
                        break;
                    }
                    try {
                        for (ConnectionProperty propToGet2 : it.next().values()) {
                            xmlBuf.append("\n  <Property name=\"");
                            Iterator i$ = it;
                            xmlBuf.append(propToGet2.getPropertyName());
                            xmlBuf.append("\" required=\"");
                            ConnectionProperty propToGet3 = propToGet2;
                            StringConnectionProperty userProp2 = userProp;
                            try {
                                xmlBuf.append(propToGet3.required ? "Yes" : "No");
                                xmlBuf.append("\" default=\"");
                                if (propToGet3.getDefaultValue() != null) {
                                    try {
                                        xmlBuf.append(propToGet3.getDefaultValue());
                                    } catch (IllegalAccessException e4) {
                                        StringConnectionProperty stringConnectionProperty7 = passwordProp;
                                    }
                                }
                                xmlBuf.append("\" sortOrder=\"");
                                xmlBuf.append(propToGet3.getOrder());
                                xmlBuf.append("\" since=\"");
                                xmlBuf.append(propToGet3.sinceVersion);
                                xmlBuf.append("\">\n");
                                xmlBuf.append("    ");
                                String escapedDescription = propToGet3.description;
                                ConnectionProperty connectionProperty = propToGet3;
                                StringConnectionProperty passwordProp2 = passwordProp;
                                try {
                                    String str3 = escapedDescription;
                                    xmlBuf.append(escapedDescription.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"));
                                    xmlBuf.append("\n  </Property>");
                                    it = i$;
                                    userProp = userProp2;
                                    passwordProp = passwordProp2;
                                } catch (IllegalAccessException e5) {
                                    throw SQLError.createSQLException("Internal properties failure", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
                                }
                            } catch (IllegalAccessException e6) {
                                StringConnectionProperty stringConnectionProperty8 = passwordProp;
                                throw SQLError.createSQLException("Internal properties failure", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
                            }
                        }
                        Iterator it2 = it;
                        StringConnectionProperty stringConnectionProperty9 = userProp;
                        StringConnectionProperty stringConnectionProperty10 = passwordProp;
                        numCategories = numCategories;
                        propertyListByCategory2 = propertyListByCategory;
                    } catch (IllegalAccessException e7) {
                        StringConnectionProperty stringConnectionProperty11 = userProp;
                        StringConnectionProperty stringConnectionProperty12 = passwordProp;
                        throw SQLError.createSQLException("Internal properties failure", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
                    }
                }
                Iterator it3 = it;
                StringConnectionProperty userProp3 = userProp;
                StringConnectionProperty passwordProp3 = passwordProp;
                for (ConnectionProperty propToGet4 : sortMaps2.alpha.values()) {
                    xmlBuf.append("\n  <Property name=\"");
                    xmlBuf.append(propToGet4.getPropertyName());
                    xmlBuf.append("\" required=\"");
                    xmlBuf.append(propToGet4.required ? "Yes" : "No");
                    xmlBuf.append("\" default=\"");
                    if (propToGet4.getDefaultValue() != null) {
                        xmlBuf.append(propToGet4.getDefaultValue());
                    }
                    xmlBuf.append("\" sortOrder=\"alpha\" since=\"");
                    xmlBuf.append(propToGet4.sinceVersion);
                    xmlBuf.append("\">\n");
                    xmlBuf.append("    ");
                    xmlBuf.append(propToGet4.description);
                    xmlBuf.append("\n  </Property>");
                }
                xmlBuf.append("\n </PropertyCategory>");
                j++;
                numCategories = numCategories;
                propertyListByCategory2 = propertyListByCategory;
                userProp = userProp3;
                passwordProp = passwordProp3;
            } catch (IllegalAccessException e8) {
                int i7 = numCategories;
                Map<String, XmlMap> map = propertyListByCategory2;
                StringConnectionProperty stringConnectionProperty13 = userProp;
                StringConnectionProperty stringConnectionProperty14 = passwordProp;
                throw SQLError.createSQLException("Internal properties failure", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
            }
        }
        Map<String, XmlMap> map2 = propertyListByCategory2;
        StringConnectionProperty stringConnectionProperty15 = userProp;
        StringConnectionProperty stringConnectionProperty16 = passwordProp;
        xmlBuf.append("\n</ConnectionProperties>");
        return xmlBuf.toString();
    }

    public boolean getAllowLoadLocalInfile() {
        return this.allowLoadLocalInfile.getValueAsBoolean();
    }

    public boolean getAllowMultiQueries() {
        return this.allowMultiQueries.getValueAsBoolean();
    }

    public boolean getAllowNanAndInf() {
        return this.allowNanAndInf.getValueAsBoolean();
    }

    public boolean getAllowUrlInLocalInfile() {
        return this.allowUrlInLocalInfile.getValueAsBoolean();
    }

    public boolean getAlwaysSendSetIsolation() {
        return this.alwaysSendSetIsolation.getValueAsBoolean();
    }

    public boolean getAutoDeserialize() {
        return this.autoDeserialize.getValueAsBoolean();
    }

    public boolean getAutoGenerateTestcaseScript() {
        return this.autoGenerateTestcaseScriptAsBoolean;
    }

    public boolean getAutoReconnectForPools() {
        return this.autoReconnectForPoolsAsBoolean;
    }

    public int getBlobSendChunkSize() {
        return this.blobSendChunkSize.getValueAsInt();
    }

    public boolean getCacheCallableStatements() {
        return this.cacheCallableStatements.getValueAsBoolean();
    }

    public boolean getCachePreparedStatements() {
        return ((Boolean) this.cachePreparedStatements.getValueAsObject()).booleanValue();
    }

    public boolean getCacheResultSetMetadata() {
        return this.cacheResultSetMetaDataAsBoolean;
    }

    public boolean getCacheServerConfiguration() {
        return this.cacheServerConfiguration.getValueAsBoolean();
    }

    public int getCallableStatementCacheSize() {
        return this.callableStatementCacheSize.getValueAsInt();
    }

    public boolean getCapitalizeTypeNames() {
        return this.capitalizeTypeNames.getValueAsBoolean();
    }

    public String getCharacterSetResults() {
        return this.characterSetResults.getValueAsString();
    }

    public String getConnectionAttributes() {
        return this.connectionAttributes.getValueAsString();
    }

    public void setConnectionAttributes(String val) {
        this.connectionAttributes.setValue(val);
    }

    public boolean getClobberStreamingResults() {
        return this.clobberStreamingResults.getValueAsBoolean();
    }

    public String getClobCharacterEncoding() {
        return this.clobCharacterEncoding.getValueAsString();
    }

    public String getConnectionCollation() {
        return this.connectionCollation.getValueAsString();
    }

    public int getConnectTimeout() {
        return this.connectTimeout.getValueAsInt();
    }

    public boolean getContinueBatchOnError() {
        return this.continueBatchOnError.getValueAsBoolean();
    }

    public boolean getCreateDatabaseIfNotExist() {
        return this.createDatabaseIfNotExist.getValueAsBoolean();
    }

    public int getDefaultFetchSize() {
        return this.defaultFetchSize.getValueAsInt();
    }

    public boolean getDontTrackOpenResources() {
        return this.dontTrackOpenResources.getValueAsBoolean();
    }

    public boolean getDumpQueriesOnException() {
        return this.dumpQueriesOnException.getValueAsBoolean();
    }

    public boolean getDynamicCalendars() {
        return this.dynamicCalendars.getValueAsBoolean();
    }

    public boolean getElideSetAutoCommits() {
        return false;
    }

    public boolean getEmptyStringsConvertToZero() {
        return this.emptyStringsConvertToZero.getValueAsBoolean();
    }

    public boolean getEmulateLocators() {
        return this.emulateLocators.getValueAsBoolean();
    }

    public boolean getEmulateUnsupportedPstmts() {
        return this.emulateUnsupportedPstmts.getValueAsBoolean();
    }

    public boolean getEnablePacketDebug() {
        return this.enablePacketDebug.getValueAsBoolean();
    }

    public String getEncoding() {
        return this.characterEncodingAsString;
    }

    public boolean getExplainSlowQueries() {
        return this.explainSlowQueries.getValueAsBoolean();
    }

    public boolean getFailOverReadOnly() {
        return this.failOverReadOnly.getValueAsBoolean();
    }

    public boolean getGatherPerformanceMetrics() {
        return this.gatherPerformanceMetrics.getValueAsBoolean();
    }

    /* access modifiers changed from: protected */
    public boolean getHighAvailability() {
        return this.highAvailabilityAsBoolean;
    }

    public boolean getHoldResultsOpenOverStatementClose() {
        return this.holdResultsOpenOverStatementClose.getValueAsBoolean();
    }

    public boolean getIgnoreNonTxTables() {
        return this.ignoreNonTxTables.getValueAsBoolean();
    }

    public int getInitialTimeout() {
        return this.initialTimeout.getValueAsInt();
    }

    public boolean getInteractiveClient() {
        return this.isInteractiveClient.getValueAsBoolean();
    }

    public boolean getIsInteractiveClient() {
        return this.isInteractiveClient.getValueAsBoolean();
    }

    public boolean getJdbcCompliantTruncation() {
        return this.jdbcCompliantTruncation.getValueAsBoolean();
    }

    public int getLocatorFetchBufferSize() {
        return this.locatorFetchBufferSize.getValueAsInt();
    }

    public String getLogger() {
        return this.loggerClassName.getValueAsString();
    }

    public String getLoggerClassName() {
        return this.loggerClassName.getValueAsString();
    }

    public boolean getLogSlowQueries() {
        return this.logSlowQueries.getValueAsBoolean();
    }

    public boolean getMaintainTimeStats() {
        return this.maintainTimeStatsAsBoolean;
    }

    public int getMaxQuerySizeToLog() {
        return this.maxQuerySizeToLog.getValueAsInt();
    }

    public int getMaxReconnects() {
        return this.maxReconnects.getValueAsInt();
    }

    public int getMaxRows() {
        return this.maxRowsAsInt;
    }

    public int getMetadataCacheSize() {
        return this.metadataCacheSize.getValueAsInt();
    }

    public boolean getNoDatetimeStringSync() {
        return this.noDatetimeStringSync.getValueAsBoolean();
    }

    public boolean getNullCatalogMeansCurrent() {
        return this.nullCatalogMeansCurrent.getValueAsBoolean();
    }

    public boolean getNullNamePatternMatchesAll() {
        return this.nullNamePatternMatchesAll.getValueAsBoolean();
    }

    public int getPacketDebugBufferSize() {
        return this.packetDebugBufferSize.getValueAsInt();
    }

    public boolean getParanoid() {
        return this.paranoid.getValueAsBoolean();
    }

    public boolean getPedantic() {
        return this.pedantic.getValueAsBoolean();
    }

    public int getPreparedStatementCacheSize() {
        return ((Integer) this.preparedStatementCacheSize.getValueAsObject()).intValue();
    }

    public int getPreparedStatementCacheSqlLimit() {
        return ((Integer) this.preparedStatementCacheSqlLimit.getValueAsObject()).intValue();
    }

    public boolean getProfileSql() {
        return this.profileSQLAsBoolean;
    }

    public boolean getProfileSQL() {
        return this.profileSQL.getValueAsBoolean();
    }

    public String getPropertiesTransform() {
        return this.propertiesTransform.getValueAsString();
    }

    public int getQueriesBeforeRetryMaster() {
        return this.queriesBeforeRetryMaster.getValueAsInt();
    }

    public boolean getReconnectAtTxEnd() {
        return this.reconnectTxAtEndAsBoolean;
    }

    public boolean getRelaxAutoCommit() {
        return this.relaxAutoCommit.getValueAsBoolean();
    }

    public int getReportMetricsIntervalMillis() {
        return this.reportMetricsIntervalMillis.getValueAsInt();
    }

    public boolean getRequireSSL() {
        return this.requireSSL.getValueAsBoolean();
    }

    public boolean getRetainStatementAfterResultSetClose() {
        return this.retainStatementAfterResultSetClose.getValueAsBoolean();
    }

    public boolean getRollbackOnPooledClose() {
        return this.rollbackOnPooledClose.getValueAsBoolean();
    }

    public boolean getRoundRobinLoadBalance() {
        return this.roundRobinLoadBalance.getValueAsBoolean();
    }

    public boolean getRunningCTS13() {
        return this.runningCTS13.getValueAsBoolean();
    }

    public int getSecondsBeforeRetryMaster() {
        return this.secondsBeforeRetryMaster.getValueAsInt();
    }

    public String getServerTimezone() {
        return this.serverTimezone.getValueAsString();
    }

    public String getSessionVariables() {
        return this.sessionVariables.getValueAsString();
    }

    public int getSlowQueryThresholdMillis() {
        return this.slowQueryThresholdMillis.getValueAsInt();
    }

    public String getSocketFactoryClassName() {
        return this.socketFactoryClassName.getValueAsString();
    }

    public int getSocketTimeout() {
        return this.socketTimeout.getValueAsInt();
    }

    public boolean getStrictFloatingPoint() {
        return this.strictFloatingPoint.getValueAsBoolean();
    }

    public boolean getStrictUpdates() {
        return this.strictUpdates.getValueAsBoolean();
    }

    public boolean getTinyInt1isBit() {
        return this.tinyInt1isBit.getValueAsBoolean();
    }

    public boolean getTraceProtocol() {
        return this.traceProtocol.getValueAsBoolean();
    }

    public boolean getTransformedBitIsBoolean() {
        return this.transformedBitIsBoolean.getValueAsBoolean();
    }

    public boolean getUseCompression() {
        return this.useCompression.getValueAsBoolean();
    }

    public boolean getUseFastIntParsing() {
        return this.useFastIntParsing.getValueAsBoolean();
    }

    public boolean getUseHostsInPrivileges() {
        return this.useHostsInPrivileges.getValueAsBoolean();
    }

    public boolean getUseInformationSchema() {
        return this.useInformationSchema.getValueAsBoolean();
    }

    public boolean getUseLocalSessionState() {
        return this.useLocalSessionState.getValueAsBoolean();
    }

    public boolean getUseOldUTF8Behavior() {
        return this.useOldUTF8BehaviorAsBoolean;
    }

    public boolean getUseOnlyServerErrorMessages() {
        return this.useOnlyServerErrorMessages.getValueAsBoolean();
    }

    public boolean getUseReadAheadInput() {
        return this.useReadAheadInput.getValueAsBoolean();
    }

    public boolean getUseServerPreparedStmts() {
        return this.detectServerPreparedStmts.getValueAsBoolean();
    }

    public boolean getUseSqlStateCodes() {
        return this.useSqlStateCodes.getValueAsBoolean();
    }

    public boolean getUseSSL() {
        return this.useSSL.getValueAsBoolean();
    }

    public boolean isUseSSLExplicit() {
        return this.useSSL.wasExplicitlySet;
    }

    public boolean getUseStreamLengthsInPrepStmts() {
        return this.useStreamLengthsInPrepStmts.getValueAsBoolean();
    }

    public boolean getUseTimezone() {
        return this.useTimezone.getValueAsBoolean();
    }

    public boolean getUseUltraDevWorkAround() {
        return this.useUltraDevWorkAround.getValueAsBoolean();
    }

    public boolean getUseUnbufferedInput() {
        return this.useUnbufferedInput.getValueAsBoolean();
    }

    public boolean getUseUnicode() {
        return this.useUnicodeAsBoolean;
    }

    public boolean getUseUsageAdvisor() {
        return this.useUsageAdvisorAsBoolean;
    }

    public boolean getYearIsDateType() {
        return this.yearIsDateType.getValueAsBoolean();
    }

    public String getZeroDateTimeBehavior() {
        return this.zeroDateTimeBehavior.getValueAsString();
    }

    /* access modifiers changed from: protected */
    public void initializeFromRef(Reference ref) throws SQLException {
        int numPropertiesToSet = PROPERTY_LIST.size();
        int i = 0;
        while (i < numPropertiesToSet) {
            try {
                ConnectionProperty propToSet = (ConnectionProperty) PROPERTY_LIST.get(i).get(this);
                if (ref != null) {
                    propToSet.initializeFrom(ref, getExceptionInterceptor());
                }
                i++;
            } catch (IllegalAccessException e) {
                throw SQLError.createSQLException("Internal properties failure", SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
            }
        }
        postInitialization();
    }

    /* access modifiers changed from: protected */
    public void initializeProperties(Properties info) throws SQLException {
        if (info != null) {
            String profileSqlLc = info.getProperty("profileSql");
            if (profileSqlLc != null) {
                info.put("profileSQL", profileSqlLc);
            }
            Properties infoCopy = (Properties) info.clone();
            infoCopy.remove(NonRegisteringDriver.HOST_PROPERTY_KEY);
            infoCopy.remove(NonRegisteringDriver.USER_PROPERTY_KEY);
            infoCopy.remove(NonRegisteringDriver.PASSWORD_PROPERTY_KEY);
            infoCopy.remove(NonRegisteringDriver.DBNAME_PROPERTY_KEY);
            infoCopy.remove(NonRegisteringDriver.PORT_PROPERTY_KEY);
            infoCopy.remove("profileSql");
            int numPropertiesToSet = PROPERTY_LIST.size();
            int i = 0;
            while (i < numPropertiesToSet) {
                try {
                    ((ConnectionProperty) PROPERTY_LIST.get(i).get(this)).initializeFrom(infoCopy, getExceptionInterceptor());
                    i++;
                } catch (IllegalAccessException iae) {
                    throw SQLError.createSQLException(Messages.getString("ConnectionProperties.unableToInitDriverProperties") + iae.toString(), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
                }
            }
            postInitialization();
        }
    }

    /* access modifiers changed from: protected */
    public void postInitialization() throws SQLException {
        if (this.profileSql.getValueAsObject() != null) {
            this.profileSQL.initializeFrom(this.profileSql.getValueAsObject().toString(), getExceptionInterceptor());
        }
        this.reconnectTxAtEndAsBoolean = ((Boolean) this.reconnectAtTxEnd.getValueAsObject()).booleanValue();
        if (getMaxRows() == 0) {
            this.maxRows.setValueAsObject(-1);
        }
        String testEncoding = (String) this.characterEncoding.getValueAsObject();
        if (testEncoding != null) {
            try {
                StringUtils.getBytes("abc", testEncoding);
            } catch (UnsupportedEncodingException e) {
                throw SQLError.createSQLException(Messages.getString("ConnectionProperties.unsupportedCharacterEncoding", new Object[]{testEncoding}), "0S100", getExceptionInterceptor());
            }
        }
        if (((Boolean) this.cacheResultSetMetadata.getValueAsObject()).booleanValue()) {
            try {
                Class.forName("java.util.LinkedHashMap");
            } catch (ClassNotFoundException e2) {
                this.cacheResultSetMetadata.setValue(false);
            }
        }
        this.cacheResultSetMetaDataAsBoolean = this.cacheResultSetMetadata.getValueAsBoolean();
        this.useUnicodeAsBoolean = this.useUnicode.getValueAsBoolean();
        this.characterEncodingAsString = (String) this.characterEncoding.getValueAsObject();
        this.highAvailabilityAsBoolean = this.autoReconnect.getValueAsBoolean();
        this.autoReconnectForPoolsAsBoolean = this.autoReconnectForPools.getValueAsBoolean();
        this.maxRowsAsInt = ((Integer) this.maxRows.getValueAsObject()).intValue();
        this.profileSQLAsBoolean = this.profileSQL.getValueAsBoolean();
        this.useUsageAdvisorAsBoolean = this.useUsageAdvisor.getValueAsBoolean();
        this.useOldUTF8BehaviorAsBoolean = this.useOldUTF8Behavior.getValueAsBoolean();
        this.autoGenerateTestcaseScriptAsBoolean = this.autoGenerateTestcaseScript.getValueAsBoolean();
        this.maintainTimeStatsAsBoolean = this.maintainTimeStats.getValueAsBoolean();
        this.jdbcCompliantTruncationForReads = getJdbcCompliantTruncation();
        if (getUseCursorFetch()) {
            setDetectServerPreparedStmts(true);
        }
    }

    public void setAllowLoadLocalInfile(boolean property) {
        this.allowLoadLocalInfile.setValue(property);
    }

    public void setAllowMultiQueries(boolean property) {
        this.allowMultiQueries.setValue(property);
    }

    public void setAllowNanAndInf(boolean flag) {
        this.allowNanAndInf.setValue(flag);
    }

    public void setAllowUrlInLocalInfile(boolean flag) {
        this.allowUrlInLocalInfile.setValue(flag);
    }

    public void setAlwaysSendSetIsolation(boolean flag) {
        this.alwaysSendSetIsolation.setValue(flag);
    }

    public void setAutoDeserialize(boolean flag) {
        this.autoDeserialize.setValue(flag);
    }

    public void setAutoGenerateTestcaseScript(boolean flag) {
        this.autoGenerateTestcaseScript.setValue(flag);
        this.autoGenerateTestcaseScriptAsBoolean = this.autoGenerateTestcaseScript.getValueAsBoolean();
    }

    public void setAutoReconnect(boolean flag) {
        this.autoReconnect.setValue(flag);
    }

    public void setAutoReconnectForConnectionPools(boolean property) {
        this.autoReconnectForPools.setValue(property);
        this.autoReconnectForPoolsAsBoolean = this.autoReconnectForPools.getValueAsBoolean();
    }

    public void setAutoReconnectForPools(boolean flag) {
        this.autoReconnectForPools.setValue(flag);
    }

    public void setBlobSendChunkSize(String value) throws SQLException {
        this.blobSendChunkSize.setValue(value, getExceptionInterceptor());
    }

    public void setCacheCallableStatements(boolean flag) {
        this.cacheCallableStatements.setValue(flag);
    }

    public void setCachePreparedStatements(boolean flag) {
        this.cachePreparedStatements.setValue(flag);
    }

    public void setCacheResultSetMetadata(boolean property) {
        this.cacheResultSetMetadata.setValue(property);
        this.cacheResultSetMetaDataAsBoolean = this.cacheResultSetMetadata.getValueAsBoolean();
    }

    public void setCacheServerConfiguration(boolean flag) {
        this.cacheServerConfiguration.setValue(flag);
    }

    public void setCallableStatementCacheSize(int size) throws SQLException {
        this.callableStatementCacheSize.setValue(size, getExceptionInterceptor());
    }

    public void setCapitalizeDBMDTypes(boolean property) {
        this.capitalizeTypeNames.setValue(property);
    }

    public void setCapitalizeTypeNames(boolean flag) {
        this.capitalizeTypeNames.setValue(flag);
    }

    public void setCharacterEncoding(String encoding) {
        this.characterEncoding.setValue(encoding);
    }

    public void setCharacterSetResults(String characterSet) {
        this.characterSetResults.setValue(characterSet);
    }

    public void setClobberStreamingResults(boolean flag) {
        this.clobberStreamingResults.setValue(flag);
    }

    public void setClobCharacterEncoding(String encoding) {
        this.clobCharacterEncoding.setValue(encoding);
    }

    public void setConnectionCollation(String collation) {
        this.connectionCollation.setValue(collation);
    }

    public void setConnectTimeout(int timeoutMs) throws SQLException {
        this.connectTimeout.setValue(timeoutMs, getExceptionInterceptor());
    }

    public void setContinueBatchOnError(boolean property) {
        this.continueBatchOnError.setValue(property);
    }

    public void setCreateDatabaseIfNotExist(boolean flag) {
        this.createDatabaseIfNotExist.setValue(flag);
    }

    public void setDefaultFetchSize(int n) throws SQLException {
        this.defaultFetchSize.setValue(n, getExceptionInterceptor());
    }

    public void setDetectServerPreparedStmts(boolean property) {
        this.detectServerPreparedStmts.setValue(property);
    }

    public void setDontTrackOpenResources(boolean flag) {
        this.dontTrackOpenResources.setValue(flag);
    }

    public void setDumpQueriesOnException(boolean flag) {
        this.dumpQueriesOnException.setValue(flag);
    }

    public void setDynamicCalendars(boolean flag) {
        this.dynamicCalendars.setValue(flag);
    }

    public void setElideSetAutoCommits(boolean flag) {
        this.elideSetAutoCommits.setValue(flag);
    }

    public void setEmptyStringsConvertToZero(boolean flag) {
        this.emptyStringsConvertToZero.setValue(flag);
    }

    public void setEmulateLocators(boolean property) {
        this.emulateLocators.setValue(property);
    }

    public void setEmulateUnsupportedPstmts(boolean flag) {
        this.emulateUnsupportedPstmts.setValue(flag);
    }

    public void setEnablePacketDebug(boolean flag) {
        this.enablePacketDebug.setValue(flag);
    }

    public void setEncoding(String property) {
        this.characterEncoding.setValue(property);
        this.characterEncodingAsString = this.characterEncoding.getValueAsString();
    }

    public void setExplainSlowQueries(boolean flag) {
        this.explainSlowQueries.setValue(flag);
    }

    public void setFailOverReadOnly(boolean flag) {
        this.failOverReadOnly.setValue(flag);
    }

    public void setGatherPerformanceMetrics(boolean flag) {
        this.gatherPerformanceMetrics.setValue(flag);
    }

    /* access modifiers changed from: protected */
    public void setHighAvailability(boolean property) {
        this.autoReconnect.setValue(property);
        this.highAvailabilityAsBoolean = this.autoReconnect.getValueAsBoolean();
    }

    public void setHoldResultsOpenOverStatementClose(boolean flag) {
        this.holdResultsOpenOverStatementClose.setValue(flag);
    }

    public void setIgnoreNonTxTables(boolean property) {
        this.ignoreNonTxTables.setValue(property);
    }

    public void setInitialTimeout(int property) throws SQLException {
        this.initialTimeout.setValue(property, getExceptionInterceptor());
    }

    public void setIsInteractiveClient(boolean property) {
        this.isInteractiveClient.setValue(property);
    }

    public void setJdbcCompliantTruncation(boolean flag) {
        this.jdbcCompliantTruncation.setValue(flag);
    }

    public void setLocatorFetchBufferSize(String value) throws SQLException {
        this.locatorFetchBufferSize.setValue(value, getExceptionInterceptor());
    }

    public void setLogger(String property) {
        this.loggerClassName.setValueAsObject(property);
    }

    public void setLoggerClassName(String className) {
        this.loggerClassName.setValue(className);
    }

    public void setLogSlowQueries(boolean flag) {
        this.logSlowQueries.setValue(flag);
    }

    public void setMaintainTimeStats(boolean flag) {
        this.maintainTimeStats.setValue(flag);
        this.maintainTimeStatsAsBoolean = this.maintainTimeStats.getValueAsBoolean();
    }

    public void setMaxQuerySizeToLog(int sizeInBytes) throws SQLException {
        this.maxQuerySizeToLog.setValue(sizeInBytes, getExceptionInterceptor());
    }

    public void setMaxReconnects(int property) throws SQLException {
        this.maxReconnects.setValue(property, getExceptionInterceptor());
    }

    public void setMaxRows(int property) throws SQLException {
        this.maxRows.setValue(property, getExceptionInterceptor());
        this.maxRowsAsInt = this.maxRows.getValueAsInt();
    }

    public void setMetadataCacheSize(int value) throws SQLException {
        this.metadataCacheSize.setValue(value, getExceptionInterceptor());
    }

    public void setNoDatetimeStringSync(boolean flag) {
        this.noDatetimeStringSync.setValue(flag);
    }

    public void setNullCatalogMeansCurrent(boolean value) {
        this.nullCatalogMeansCurrent.setValue(value);
    }

    public void setNullNamePatternMatchesAll(boolean value) {
        this.nullNamePatternMatchesAll.setValue(value);
    }

    public void setPacketDebugBufferSize(int size) throws SQLException {
        this.packetDebugBufferSize.setValue(size, getExceptionInterceptor());
    }

    public void setParanoid(boolean property) {
        this.paranoid.setValue(property);
    }

    public void setPedantic(boolean property) {
        this.pedantic.setValue(property);
    }

    public void setPreparedStatementCacheSize(int cacheSize) throws SQLException {
        this.preparedStatementCacheSize.setValue(cacheSize, getExceptionInterceptor());
    }

    public void setPreparedStatementCacheSqlLimit(int cacheSqlLimit) throws SQLException {
        this.preparedStatementCacheSqlLimit.setValue(cacheSqlLimit, getExceptionInterceptor());
    }

    public void setProfileSql(boolean property) {
        this.profileSQL.setValue(property);
        this.profileSQLAsBoolean = this.profileSQL.getValueAsBoolean();
    }

    public void setProfileSQL(boolean flag) {
        this.profileSQL.setValue(flag);
    }

    public void setPropertiesTransform(String value) {
        this.propertiesTransform.setValue(value);
    }

    public void setQueriesBeforeRetryMaster(int property) throws SQLException {
        this.queriesBeforeRetryMaster.setValue(property, getExceptionInterceptor());
    }

    public void setReconnectAtTxEnd(boolean property) {
        this.reconnectAtTxEnd.setValue(property);
        this.reconnectTxAtEndAsBoolean = this.reconnectAtTxEnd.getValueAsBoolean();
    }

    public void setRelaxAutoCommit(boolean property) {
        this.relaxAutoCommit.setValue(property);
    }

    public void setReportMetricsIntervalMillis(int millis) throws SQLException {
        this.reportMetricsIntervalMillis.setValue(millis, getExceptionInterceptor());
    }

    public void setRequireSSL(boolean property) {
        this.requireSSL.setValue(property);
    }

    public void setRetainStatementAfterResultSetClose(boolean flag) {
        this.retainStatementAfterResultSetClose.setValue(flag);
    }

    public void setRollbackOnPooledClose(boolean flag) {
        this.rollbackOnPooledClose.setValue(flag);
    }

    public void setRoundRobinLoadBalance(boolean flag) {
        this.roundRobinLoadBalance.setValue(flag);
    }

    public void setRunningCTS13(boolean flag) {
        this.runningCTS13.setValue(flag);
    }

    public void setSecondsBeforeRetryMaster(int property) throws SQLException {
        this.secondsBeforeRetryMaster.setValue(property, getExceptionInterceptor());
    }

    public void setServerTimezone(String property) {
        this.serverTimezone.setValue(property);
    }

    public void setSessionVariables(String variables) {
        this.sessionVariables.setValue(variables);
    }

    public void setSlowQueryThresholdMillis(int millis) throws SQLException {
        this.slowQueryThresholdMillis.setValue(millis, getExceptionInterceptor());
    }

    public void setSocketFactoryClassName(String property) {
        this.socketFactoryClassName.setValue(property);
    }

    public void setSocketTimeout(int property) throws SQLException {
        this.socketTimeout.setValue(property, getExceptionInterceptor());
    }

    public void setStrictFloatingPoint(boolean property) {
        this.strictFloatingPoint.setValue(property);
    }

    public void setStrictUpdates(boolean property) {
        this.strictUpdates.setValue(property);
    }

    public void setTinyInt1isBit(boolean flag) {
        this.tinyInt1isBit.setValue(flag);
    }

    public void setTraceProtocol(boolean flag) {
        this.traceProtocol.setValue(flag);
    }

    public void setTransformedBitIsBoolean(boolean flag) {
        this.transformedBitIsBoolean.setValue(flag);
    }

    public void setUseCompression(boolean property) {
        this.useCompression.setValue(property);
    }

    public void setUseFastIntParsing(boolean flag) {
        this.useFastIntParsing.setValue(flag);
    }

    public void setUseHostsInPrivileges(boolean property) {
        this.useHostsInPrivileges.setValue(property);
    }

    public void setUseInformationSchema(boolean flag) {
        this.useInformationSchema.setValue(flag);
    }

    public void setUseLocalSessionState(boolean flag) {
        this.useLocalSessionState.setValue(flag);
    }

    public void setUseOldUTF8Behavior(boolean flag) {
        this.useOldUTF8Behavior.setValue(flag);
        this.useOldUTF8BehaviorAsBoolean = this.useOldUTF8Behavior.getValueAsBoolean();
    }

    public void setUseOnlyServerErrorMessages(boolean flag) {
        this.useOnlyServerErrorMessages.setValue(flag);
    }

    public void setUseReadAheadInput(boolean flag) {
        this.useReadAheadInput.setValue(flag);
    }

    public void setUseServerPreparedStmts(boolean flag) {
        this.detectServerPreparedStmts.setValue(flag);
    }

    public void setUseSqlStateCodes(boolean flag) {
        this.useSqlStateCodes.setValue(flag);
    }

    public void setUseSSL(boolean property) {
        this.useSSL.setValue(property);
    }

    public void setUseStreamLengthsInPrepStmts(boolean property) {
        this.useStreamLengthsInPrepStmts.setValue(property);
    }

    public void setUseTimezone(boolean property) {
        this.useTimezone.setValue(property);
    }

    public void setUseUltraDevWorkAround(boolean property) {
        this.useUltraDevWorkAround.setValue(property);
    }

    public void setUseUnbufferedInput(boolean flag) {
        this.useUnbufferedInput.setValue(flag);
    }

    public void setUseUnicode(boolean flag) {
        this.useUnicode.setValue(flag);
        this.useUnicodeAsBoolean = this.useUnicode.getValueAsBoolean();
    }

    public void setUseUsageAdvisor(boolean useUsageAdvisorFlag) {
        this.useUsageAdvisor.setValue(useUsageAdvisorFlag);
        this.useUsageAdvisorAsBoolean = this.useUsageAdvisor.getValueAsBoolean();
    }

    public void setYearIsDateType(boolean flag) {
        this.yearIsDateType.setValue(flag);
    }

    public void setZeroDateTimeBehavior(String behavior) {
        this.zeroDateTimeBehavior.setValue(behavior);
    }

    /* access modifiers changed from: protected */
    public void storeToRef(Reference ref) throws SQLException {
        int numPropertiesToSet = PROPERTY_LIST.size();
        int i = 0;
        while (i < numPropertiesToSet) {
            try {
                ConnectionProperty propToStore = (ConnectionProperty) PROPERTY_LIST.get(i).get(this);
                if (ref != null) {
                    propToStore.storeTo(ref);
                }
                i++;
            } catch (IllegalAccessException e) {
                throw SQLError.createSQLException(Messages.getString("ConnectionProperties.errorNotExpected"), getExceptionInterceptor());
            }
        }
    }

    public boolean useUnbufferedInput() {
        return this.useUnbufferedInput.getValueAsBoolean();
    }

    public boolean getUseCursorFetch() {
        return this.useCursorFetch.getValueAsBoolean();
    }

    public void setUseCursorFetch(boolean flag) {
        this.useCursorFetch.setValue(flag);
    }

    public boolean getOverrideSupportsIntegrityEnhancementFacility() {
        return this.overrideSupportsIntegrityEnhancementFacility.getValueAsBoolean();
    }

    public void setOverrideSupportsIntegrityEnhancementFacility(boolean flag) {
        this.overrideSupportsIntegrityEnhancementFacility.setValue(flag);
    }

    public boolean getNoTimezoneConversionForTimeType() {
        return this.noTimezoneConversionForTimeType.getValueAsBoolean();
    }

    public void setNoTimezoneConversionForTimeType(boolean flag) {
        this.noTimezoneConversionForTimeType.setValue(flag);
    }

    public boolean getNoTimezoneConversionForDateType() {
        return this.noTimezoneConversionForDateType.getValueAsBoolean();
    }

    public void setNoTimezoneConversionForDateType(boolean flag) {
        this.noTimezoneConversionForDateType.setValue(flag);
    }

    public boolean getCacheDefaultTimezone() {
        return this.cacheDefaultTimezone.getValueAsBoolean();
    }

    public void setCacheDefaultTimezone(boolean flag) {
        this.cacheDefaultTimezone.setValue(flag);
    }

    public boolean getUseJDBCCompliantTimezoneShift() {
        return this.useJDBCCompliantTimezoneShift.getValueAsBoolean();
    }

    public void setUseJDBCCompliantTimezoneShift(boolean flag) {
        this.useJDBCCompliantTimezoneShift.setValue(flag);
    }

    public boolean getAutoClosePStmtStreams() {
        return this.autoClosePStmtStreams.getValueAsBoolean();
    }

    public void setAutoClosePStmtStreams(boolean flag) {
        this.autoClosePStmtStreams.setValue(flag);
    }

    public boolean getProcessEscapeCodesForPrepStmts() {
        return this.processEscapeCodesForPrepStmts.getValueAsBoolean();
    }

    public void setProcessEscapeCodesForPrepStmts(boolean flag) {
        this.processEscapeCodesForPrepStmts.setValue(flag);
    }

    public boolean getUseGmtMillisForDatetimes() {
        return this.useGmtMillisForDatetimes.getValueAsBoolean();
    }

    public void setUseGmtMillisForDatetimes(boolean flag) {
        this.useGmtMillisForDatetimes.setValue(flag);
    }

    public boolean getDumpMetadataOnColumnNotFound() {
        return this.dumpMetadataOnColumnNotFound.getValueAsBoolean();
    }

    public void setDumpMetadataOnColumnNotFound(boolean flag) {
        this.dumpMetadataOnColumnNotFound.setValue(flag);
    }

    public String getResourceId() {
        return this.resourceId.getValueAsString();
    }

    public void setResourceId(String resourceId2) {
        this.resourceId.setValue(resourceId2);
    }

    public boolean getRewriteBatchedStatements() {
        return this.rewriteBatchedStatements.getValueAsBoolean();
    }

    public void setRewriteBatchedStatements(boolean flag) {
        this.rewriteBatchedStatements.setValue(flag);
    }

    public boolean getJdbcCompliantTruncationForReads() {
        return this.jdbcCompliantTruncationForReads;
    }

    public void setJdbcCompliantTruncationForReads(boolean jdbcCompliantTruncationForReads2) {
        this.jdbcCompliantTruncationForReads = jdbcCompliantTruncationForReads2;
    }

    public boolean getUseJvmCharsetConverters() {
        return this.useJvmCharsetConverters.getValueAsBoolean();
    }

    public void setUseJvmCharsetConverters(boolean flag) {
        this.useJvmCharsetConverters.setValue(flag);
    }

    public boolean getPinGlobalTxToPhysicalConnection() {
        return this.pinGlobalTxToPhysicalConnection.getValueAsBoolean();
    }

    public void setPinGlobalTxToPhysicalConnection(boolean flag) {
        this.pinGlobalTxToPhysicalConnection.setValue(flag);
    }

    public void setGatherPerfMetrics(boolean flag) {
        setGatherPerformanceMetrics(flag);
    }

    public boolean getGatherPerfMetrics() {
        return getGatherPerformanceMetrics();
    }

    public void setUltraDevHack(boolean flag) {
        setUseUltraDevWorkAround(flag);
    }

    public boolean getUltraDevHack() {
        return getUseUltraDevWorkAround();
    }

    public void setInteractiveClient(boolean property) {
        setIsInteractiveClient(property);
    }

    public void setSocketFactory(String name) {
        setSocketFactoryClassName(name);
    }

    public String getSocketFactory() {
        return getSocketFactoryClassName();
    }

    public void setUseServerPrepStmts(boolean flag) {
        setUseServerPreparedStmts(flag);
    }

    public boolean getUseServerPrepStmts() {
        return getUseServerPreparedStmts();
    }

    public void setCacheCallableStmts(boolean flag) {
        setCacheCallableStatements(flag);
    }

    public boolean getCacheCallableStmts() {
        return getCacheCallableStatements();
    }

    public void setCachePrepStmts(boolean flag) {
        setCachePreparedStatements(flag);
    }

    public boolean getCachePrepStmts() {
        return getCachePreparedStatements();
    }

    public void setCallableStmtCacheSize(int cacheSize) throws SQLException {
        setCallableStatementCacheSize(cacheSize);
    }

    public int getCallableStmtCacheSize() {
        return getCallableStatementCacheSize();
    }

    public void setPrepStmtCacheSize(int cacheSize) throws SQLException {
        setPreparedStatementCacheSize(cacheSize);
    }

    public int getPrepStmtCacheSize() {
        return getPreparedStatementCacheSize();
    }

    public void setPrepStmtCacheSqlLimit(int sqlLimit) throws SQLException {
        setPreparedStatementCacheSqlLimit(sqlLimit);
    }

    public int getPrepStmtCacheSqlLimit() {
        return getPreparedStatementCacheSqlLimit();
    }

    public boolean getNoAccessToProcedureBodies() {
        return this.noAccessToProcedureBodies.getValueAsBoolean();
    }

    public void setNoAccessToProcedureBodies(boolean flag) {
        this.noAccessToProcedureBodies.setValue(flag);
    }

    public boolean getUseOldAliasMetadataBehavior() {
        return this.useOldAliasMetadataBehavior.getValueAsBoolean();
    }

    public void setUseOldAliasMetadataBehavior(boolean flag) {
        this.useOldAliasMetadataBehavior.setValue(flag);
    }

    public String getClientCertificateKeyStorePassword() {
        return this.clientCertificateKeyStorePassword.getValueAsString();
    }

    public void setClientCertificateKeyStorePassword(String value) {
        this.clientCertificateKeyStorePassword.setValue(value);
    }

    public String getClientCertificateKeyStoreType() {
        return this.clientCertificateKeyStoreType.getValueAsString();
    }

    public void setClientCertificateKeyStoreType(String value) {
        this.clientCertificateKeyStoreType.setValue(value);
    }

    public String getClientCertificateKeyStoreUrl() {
        return this.clientCertificateKeyStoreUrl.getValueAsString();
    }

    public void setClientCertificateKeyStoreUrl(String value) {
        this.clientCertificateKeyStoreUrl.setValue(value);
    }

    public String getTrustCertificateKeyStorePassword() {
        return this.trustCertificateKeyStorePassword.getValueAsString();
    }

    public void setTrustCertificateKeyStorePassword(String value) {
        this.trustCertificateKeyStorePassword.setValue(value);
    }

    public String getTrustCertificateKeyStoreType() {
        return this.trustCertificateKeyStoreType.getValueAsString();
    }

    public void setTrustCertificateKeyStoreType(String value) {
        this.trustCertificateKeyStoreType.setValue(value);
    }

    public String getTrustCertificateKeyStoreUrl() {
        return this.trustCertificateKeyStoreUrl.getValueAsString();
    }

    public void setTrustCertificateKeyStoreUrl(String value) {
        this.trustCertificateKeyStoreUrl.setValue(value);
    }

    public boolean getUseSSPSCompatibleTimezoneShift() {
        return this.useSSPSCompatibleTimezoneShift.getValueAsBoolean();
    }

    public void setUseSSPSCompatibleTimezoneShift(boolean flag) {
        this.useSSPSCompatibleTimezoneShift.setValue(flag);
    }

    public boolean getTreatUtilDateAsTimestamp() {
        return this.treatUtilDateAsTimestamp.getValueAsBoolean();
    }

    public void setTreatUtilDateAsTimestamp(boolean flag) {
        this.treatUtilDateAsTimestamp.setValue(flag);
    }

    public boolean getUseFastDateParsing() {
        return this.useFastDateParsing.getValueAsBoolean();
    }

    public void setUseFastDateParsing(boolean flag) {
        this.useFastDateParsing.setValue(flag);
    }

    public String getLocalSocketAddress() {
        return this.localSocketAddress.getValueAsString();
    }

    public void setLocalSocketAddress(String address) {
        this.localSocketAddress.setValue(address);
    }

    public void setUseConfigs(String configs) {
        this.useConfigs.setValue(configs);
    }

    public String getUseConfigs() {
        return this.useConfigs.getValueAsString();
    }

    public boolean getGenerateSimpleParameterMetadata() {
        return this.generateSimpleParameterMetadata.getValueAsBoolean();
    }

    public void setGenerateSimpleParameterMetadata(boolean flag) {
        this.generateSimpleParameterMetadata.setValue(flag);
    }

    public boolean getLogXaCommands() {
        return this.logXaCommands.getValueAsBoolean();
    }

    public void setLogXaCommands(boolean flag) {
        this.logXaCommands.setValue(flag);
    }

    public int getResultSetSizeThreshold() {
        return this.resultSetSizeThreshold.getValueAsInt();
    }

    public void setResultSetSizeThreshold(int threshold) throws SQLException {
        this.resultSetSizeThreshold.setValue(threshold, getExceptionInterceptor());
    }

    public int getNetTimeoutForStreamingResults() {
        return this.netTimeoutForStreamingResults.getValueAsInt();
    }

    public void setNetTimeoutForStreamingResults(int value) throws SQLException {
        this.netTimeoutForStreamingResults.setValue(value, getExceptionInterceptor());
    }

    public boolean getEnableQueryTimeouts() {
        return this.enableQueryTimeouts.getValueAsBoolean();
    }

    public void setEnableQueryTimeouts(boolean flag) {
        this.enableQueryTimeouts.setValue(flag);
    }

    public boolean getPadCharsWithSpace() {
        return this.padCharsWithSpace.getValueAsBoolean();
    }

    public void setPadCharsWithSpace(boolean flag) {
        this.padCharsWithSpace.setValue(flag);
    }

    public boolean getUseDynamicCharsetInfo() {
        return this.useDynamicCharsetInfo.getValueAsBoolean();
    }

    public void setUseDynamicCharsetInfo(boolean flag) {
        this.useDynamicCharsetInfo.setValue(flag);
    }

    public String getClientInfoProvider() {
        return this.clientInfoProvider.getValueAsString();
    }

    public void setClientInfoProvider(String classname) {
        this.clientInfoProvider.setValue(classname);
    }

    public boolean getPopulateInsertRowWithDefaultValues() {
        return this.populateInsertRowWithDefaultValues.getValueAsBoolean();
    }

    public void setPopulateInsertRowWithDefaultValues(boolean flag) {
        this.populateInsertRowWithDefaultValues.setValue(flag);
    }

    public String getLoadBalanceStrategy() {
        return this.loadBalanceStrategy.getValueAsString();
    }

    public void setLoadBalanceStrategy(String strategy) {
        this.loadBalanceStrategy.setValue(strategy);
    }

    public String getServerAffinityOrder() {
        return this.serverAffinityOrder.getValueAsString();
    }

    public void setServerAffinityOrder(String hostsList) {
        this.serverAffinityOrder.setValue(hostsList);
    }

    public boolean getTcpNoDelay() {
        return this.tcpNoDelay.getValueAsBoolean();
    }

    public void setTcpNoDelay(boolean flag) {
        this.tcpNoDelay.setValue(flag);
    }

    public boolean getTcpKeepAlive() {
        return this.tcpKeepAlive.getValueAsBoolean();
    }

    public void setTcpKeepAlive(boolean flag) {
        this.tcpKeepAlive.setValue(flag);
    }

    public int getTcpRcvBuf() {
        return this.tcpRcvBuf.getValueAsInt();
    }

    public void setTcpRcvBuf(int bufSize) throws SQLException {
        this.tcpRcvBuf.setValue(bufSize, getExceptionInterceptor());
    }

    public int getTcpSndBuf() {
        return this.tcpSndBuf.getValueAsInt();
    }

    public void setTcpSndBuf(int bufSize) throws SQLException {
        this.tcpSndBuf.setValue(bufSize, getExceptionInterceptor());
    }

    public int getTcpTrafficClass() {
        return this.tcpTrafficClass.getValueAsInt();
    }

    public void setTcpTrafficClass(int classFlags) throws SQLException {
        this.tcpTrafficClass.setValue(classFlags, getExceptionInterceptor());
    }

    public boolean getUseNanosForElapsedTime() {
        return this.useNanosForElapsedTime.getValueAsBoolean();
    }

    public void setUseNanosForElapsedTime(boolean flag) {
        this.useNanosForElapsedTime.setValue(flag);
    }

    public long getSlowQueryThresholdNanos() {
        return this.slowQueryThresholdNanos.getValueAsLong();
    }

    public void setSlowQueryThresholdNanos(long nanos) throws SQLException {
        this.slowQueryThresholdNanos.setValue(nanos, getExceptionInterceptor());
    }

    public String getStatementInterceptors() {
        return this.statementInterceptors.getValueAsString();
    }

    public void setStatementInterceptors(String value) {
        this.statementInterceptors.setValue(value);
    }

    public boolean getUseDirectRowUnpack() {
        return this.useDirectRowUnpack.getValueAsBoolean();
    }

    public void setUseDirectRowUnpack(boolean flag) {
        this.useDirectRowUnpack.setValue(flag);
    }

    public String getLargeRowSizeThreshold() {
        return this.largeRowSizeThreshold.getValueAsString();
    }

    public void setLargeRowSizeThreshold(String value) throws SQLException {
        this.largeRowSizeThreshold.setValue(value, getExceptionInterceptor());
    }

    public boolean getUseBlobToStoreUTF8OutsideBMP() {
        return this.useBlobToStoreUTF8OutsideBMP.getValueAsBoolean();
    }

    public void setUseBlobToStoreUTF8OutsideBMP(boolean flag) {
        this.useBlobToStoreUTF8OutsideBMP.setValue(flag);
    }

    public String getUtf8OutsideBmpExcludedColumnNamePattern() {
        return this.utf8OutsideBmpExcludedColumnNamePattern.getValueAsString();
    }

    public void setUtf8OutsideBmpExcludedColumnNamePattern(String regexPattern) {
        this.utf8OutsideBmpExcludedColumnNamePattern.setValue(regexPattern);
    }

    public String getUtf8OutsideBmpIncludedColumnNamePattern() {
        return this.utf8OutsideBmpIncludedColumnNamePattern.getValueAsString();
    }

    public void setUtf8OutsideBmpIncludedColumnNamePattern(String regexPattern) {
        this.utf8OutsideBmpIncludedColumnNamePattern.setValue(regexPattern);
    }

    public boolean getIncludeInnodbStatusInDeadlockExceptions() {
        return this.includeInnodbStatusInDeadlockExceptions.getValueAsBoolean();
    }

    public void setIncludeInnodbStatusInDeadlockExceptions(boolean flag) {
        this.includeInnodbStatusInDeadlockExceptions.setValue(flag);
    }

    public boolean getBlobsAreStrings() {
        return this.blobsAreStrings.getValueAsBoolean();
    }

    public void setBlobsAreStrings(boolean flag) {
        this.blobsAreStrings.setValue(flag);
    }

    public boolean getFunctionsNeverReturnBlobs() {
        return this.functionsNeverReturnBlobs.getValueAsBoolean();
    }

    public void setFunctionsNeverReturnBlobs(boolean flag) {
        this.functionsNeverReturnBlobs.setValue(flag);
    }

    public boolean getAutoSlowLog() {
        return this.autoSlowLog.getValueAsBoolean();
    }

    public void setAutoSlowLog(boolean flag) {
        this.autoSlowLog.setValue(flag);
    }

    public String getConnectionLifecycleInterceptors() {
        return this.connectionLifecycleInterceptors.getValueAsString();
    }

    public void setConnectionLifecycleInterceptors(String interceptors) {
        this.connectionLifecycleInterceptors.setValue(interceptors);
    }

    public String getProfilerEventHandler() {
        return this.profilerEventHandler.getValueAsString();
    }

    public void setProfilerEventHandler(String handler) {
        this.profilerEventHandler.setValue(handler);
    }

    public boolean getVerifyServerCertificate() {
        return this.verifyServerCertificate.getValueAsBoolean();
    }

    public void setVerifyServerCertificate(boolean flag) {
        this.verifyServerCertificate.setValue(flag);
    }

    public boolean getUseLegacyDatetimeCode() {
        return this.useLegacyDatetimeCode.getValueAsBoolean();
    }

    public void setUseLegacyDatetimeCode(boolean flag) {
        this.useLegacyDatetimeCode.setValue(flag);
    }

    public boolean getSendFractionalSeconds() {
        return this.sendFractionalSeconds.getValueAsBoolean();
    }

    public void setSendFractionalSeconds(boolean flag) {
        this.sendFractionalSeconds.setValue(flag);
    }

    public int getSelfDestructOnPingSecondsLifetime() {
        return this.selfDestructOnPingSecondsLifetime.getValueAsInt();
    }

    public void setSelfDestructOnPingSecondsLifetime(int seconds) throws SQLException {
        this.selfDestructOnPingSecondsLifetime.setValue(seconds, getExceptionInterceptor());
    }

    public int getSelfDestructOnPingMaxOperations() {
        return this.selfDestructOnPingMaxOperations.getValueAsInt();
    }

    public void setSelfDestructOnPingMaxOperations(int maxOperations) throws SQLException {
        this.selfDestructOnPingMaxOperations.setValue(maxOperations, getExceptionInterceptor());
    }

    public boolean getUseColumnNamesInFindColumn() {
        return this.useColumnNamesInFindColumn.getValueAsBoolean();
    }

    public void setUseColumnNamesInFindColumn(boolean flag) {
        this.useColumnNamesInFindColumn.setValue(flag);
    }

    public boolean getUseLocalTransactionState() {
        return this.useLocalTransactionState.getValueAsBoolean();
    }

    public void setUseLocalTransactionState(boolean flag) {
        this.useLocalTransactionState.setValue(flag);
    }

    public boolean getCompensateOnDuplicateKeyUpdateCounts() {
        return this.compensateOnDuplicateKeyUpdateCounts.getValueAsBoolean();
    }

    public void setCompensateOnDuplicateKeyUpdateCounts(boolean flag) {
        this.compensateOnDuplicateKeyUpdateCounts.setValue(flag);
    }

    public int getLoadBalanceBlacklistTimeout() {
        return this.loadBalanceBlacklistTimeout.getValueAsInt();
    }

    public void setLoadBalanceBlacklistTimeout(int loadBalanceBlacklistTimeout2) throws SQLException {
        this.loadBalanceBlacklistTimeout.setValue(loadBalanceBlacklistTimeout2, getExceptionInterceptor());
    }

    public int getLoadBalancePingTimeout() {
        return this.loadBalancePingTimeout.getValueAsInt();
    }

    public void setLoadBalancePingTimeout(int loadBalancePingTimeout2) throws SQLException {
        this.loadBalancePingTimeout.setValue(loadBalancePingTimeout2, getExceptionInterceptor());
    }

    public void setRetriesAllDown(int retriesAllDown2) throws SQLException {
        this.retriesAllDown.setValue(retriesAllDown2, getExceptionInterceptor());
    }

    public int getRetriesAllDown() {
        return this.retriesAllDown.getValueAsInt();
    }

    public void setUseAffectedRows(boolean flag) {
        this.useAffectedRows.setValue(flag);
    }

    public boolean getUseAffectedRows() {
        return this.useAffectedRows.getValueAsBoolean();
    }

    public void setPasswordCharacterEncoding(String characterSet) {
        this.passwordCharacterEncoding.setValue(characterSet);
    }

    public String getPasswordCharacterEncoding() {
        String valueAsString = this.passwordCharacterEncoding.getValueAsString();
        String encoding = valueAsString;
        if (valueAsString != null) {
            return encoding;
        }
        if (!getUseUnicode()) {
            return "UTF-8";
        }
        String encoding2 = getEncoding();
        String encoding3 = encoding2;
        if (encoding2 != null) {
            return encoding3;
        }
        return "UTF-8";
    }

    public void setExceptionInterceptors(String exceptionInterceptors2) {
        this.exceptionInterceptors.setValue(exceptionInterceptors2);
    }

    public String getExceptionInterceptors() {
        return this.exceptionInterceptors.getValueAsString();
    }

    public void setMaxAllowedPacket(int max) throws SQLException {
        this.maxAllowedPacket.setValue(max, getExceptionInterceptor());
    }

    public int getMaxAllowedPacket() {
        return this.maxAllowedPacket.getValueAsInt();
    }

    public boolean getQueryTimeoutKillsConnection() {
        return this.queryTimeoutKillsConnection.getValueAsBoolean();
    }

    public void setQueryTimeoutKillsConnection(boolean queryTimeoutKillsConnection2) {
        this.queryTimeoutKillsConnection.setValue(queryTimeoutKillsConnection2);
    }

    public boolean getLoadBalanceValidateConnectionOnSwapServer() {
        return this.loadBalanceValidateConnectionOnSwapServer.getValueAsBoolean();
    }

    public void setLoadBalanceValidateConnectionOnSwapServer(boolean loadBalanceValidateConnectionOnSwapServer2) {
        this.loadBalanceValidateConnectionOnSwapServer.setValue(loadBalanceValidateConnectionOnSwapServer2);
    }

    public String getLoadBalanceConnectionGroup() {
        return this.loadBalanceConnectionGroup.getValueAsString();
    }

    public void setLoadBalanceConnectionGroup(String loadBalanceConnectionGroup2) {
        this.loadBalanceConnectionGroup.setValue(loadBalanceConnectionGroup2);
    }

    public String getLoadBalanceExceptionChecker() {
        return this.loadBalanceExceptionChecker.getValueAsString();
    }

    public void setLoadBalanceExceptionChecker(String loadBalanceExceptionChecker2) {
        this.loadBalanceExceptionChecker.setValue(loadBalanceExceptionChecker2);
    }

    public String getLoadBalanceSQLStateFailover() {
        return this.loadBalanceSQLStateFailover.getValueAsString();
    }

    public void setLoadBalanceSQLStateFailover(String loadBalanceSQLStateFailover2) {
        this.loadBalanceSQLStateFailover.setValue(loadBalanceSQLStateFailover2);
    }

    public String getLoadBalanceSQLExceptionSubclassFailover() {
        return this.loadBalanceSQLExceptionSubclassFailover.getValueAsString();
    }

    public void setLoadBalanceSQLExceptionSubclassFailover(String loadBalanceSQLExceptionSubclassFailover2) {
        this.loadBalanceSQLExceptionSubclassFailover.setValue(loadBalanceSQLExceptionSubclassFailover2);
    }

    public boolean getLoadBalanceEnableJMX() {
        return this.loadBalanceEnableJMX.getValueAsBoolean();
    }

    public void setLoadBalanceEnableJMX(boolean loadBalanceEnableJMX2) {
        this.loadBalanceEnableJMX.setValue(loadBalanceEnableJMX2);
    }

    public void setLoadBalanceHostRemovalGracePeriod(int loadBalanceHostRemovalGracePeriod2) throws SQLException {
        this.loadBalanceHostRemovalGracePeriod.setValue(loadBalanceHostRemovalGracePeriod2, getExceptionInterceptor());
    }

    public int getLoadBalanceHostRemovalGracePeriod() {
        return this.loadBalanceHostRemovalGracePeriod.getValueAsInt();
    }

    public void setLoadBalanceAutoCommitStatementThreshold(int loadBalanceAutoCommitStatementThreshold2) throws SQLException {
        this.loadBalanceAutoCommitStatementThreshold.setValue(loadBalanceAutoCommitStatementThreshold2, getExceptionInterceptor());
    }

    public int getLoadBalanceAutoCommitStatementThreshold() {
        return this.loadBalanceAutoCommitStatementThreshold.getValueAsInt();
    }

    public void setLoadBalanceAutoCommitStatementRegex(String loadBalanceAutoCommitStatementRegex2) {
        this.loadBalanceAutoCommitStatementRegex.setValue(loadBalanceAutoCommitStatementRegex2);
    }

    public String getLoadBalanceAutoCommitStatementRegex() {
        return this.loadBalanceAutoCommitStatementRegex.getValueAsString();
    }

    public void setIncludeThreadDumpInDeadlockExceptions(boolean flag) {
        this.includeThreadDumpInDeadlockExceptions.setValue(flag);
    }

    public boolean getIncludeThreadDumpInDeadlockExceptions() {
        return this.includeThreadDumpInDeadlockExceptions.getValueAsBoolean();
    }

    public void setIncludeThreadNamesAsStatementComment(boolean flag) {
        this.includeThreadNamesAsStatementComment.setValue(flag);
    }

    public boolean getIncludeThreadNamesAsStatementComment() {
        return this.includeThreadNamesAsStatementComment.getValueAsBoolean();
    }

    public void setAuthenticationPlugins(String authenticationPlugins2) {
        this.authenticationPlugins.setValue(authenticationPlugins2);
    }

    public String getAuthenticationPlugins() {
        return this.authenticationPlugins.getValueAsString();
    }

    public void setDisabledAuthenticationPlugins(String disabledAuthenticationPlugins2) {
        this.disabledAuthenticationPlugins.setValue(disabledAuthenticationPlugins2);
    }

    public String getDisabledAuthenticationPlugins() {
        return this.disabledAuthenticationPlugins.getValueAsString();
    }

    public void setDefaultAuthenticationPlugin(String defaultAuthenticationPlugin2) {
        this.defaultAuthenticationPlugin.setValue(defaultAuthenticationPlugin2);
    }

    public String getDefaultAuthenticationPlugin() {
        return this.defaultAuthenticationPlugin.getValueAsString();
    }

    public void setParseInfoCacheFactory(String factoryClassname) {
        this.parseInfoCacheFactory.setValue(factoryClassname);
    }

    public String getParseInfoCacheFactory() {
        return this.parseInfoCacheFactory.getValueAsString();
    }

    public void setServerConfigCacheFactory(String factoryClassname) {
        this.serverConfigCacheFactory.setValue(factoryClassname);
    }

    public String getServerConfigCacheFactory() {
        return this.serverConfigCacheFactory.getValueAsString();
    }

    public void setDisconnectOnExpiredPasswords(boolean disconnectOnExpiredPasswords2) {
        this.disconnectOnExpiredPasswords.setValue(disconnectOnExpiredPasswords2);
    }

    public boolean getDisconnectOnExpiredPasswords() {
        return this.disconnectOnExpiredPasswords.getValueAsBoolean();
    }

    public String getReplicationConnectionGroup() {
        return this.replicationConnectionGroup.getValueAsString();
    }

    public void setReplicationConnectionGroup(String replicationConnectionGroup2) {
        this.replicationConnectionGroup.setValue(replicationConnectionGroup2);
    }

    public boolean getAllowMasterDownConnections() {
        return this.allowMasterDownConnections.getValueAsBoolean();
    }

    public void setAllowMasterDownConnections(boolean connectIfMasterDown) {
        this.allowMasterDownConnections.setValue(connectIfMasterDown);
    }

    public boolean getAllowSlaveDownConnections() {
        return this.allowSlaveDownConnections.getValueAsBoolean();
    }

    public void setAllowSlaveDownConnections(boolean connectIfSlaveDown) {
        this.allowSlaveDownConnections.setValue(connectIfSlaveDown);
    }

    public boolean getReadFromMasterWhenNoSlaves() {
        return this.readFromMasterWhenNoSlaves.getValueAsBoolean();
    }

    public void setReadFromMasterWhenNoSlaves(boolean useMasterIfSlavesDown) {
        this.readFromMasterWhenNoSlaves.setValue(useMasterIfSlavesDown);
    }

    public boolean getReplicationEnableJMX() {
        return this.replicationEnableJMX.getValueAsBoolean();
    }

    public void setReplicationEnableJMX(boolean replicationEnableJMX2) {
        this.replicationEnableJMX.setValue(replicationEnableJMX2);
    }

    public void setGetProceduresReturnsFunctions(boolean getProcedureReturnsFunctions) {
        this.getProceduresReturnsFunctions.setValue(getProcedureReturnsFunctions);
    }

    public boolean getGetProceduresReturnsFunctions() {
        return this.getProceduresReturnsFunctions.getValueAsBoolean();
    }

    public void setDetectCustomCollations(boolean detectCustomCollations2) {
        this.detectCustomCollations.setValue(detectCustomCollations2);
    }

    public boolean getDetectCustomCollations() {
        return this.detectCustomCollations.getValueAsBoolean();
    }

    public String getServerRSAPublicKeyFile() {
        return this.serverRSAPublicKeyFile.getValueAsString();
    }

    public void setServerRSAPublicKeyFile(String serverRSAPublicKeyFile2) throws SQLException {
        if (this.serverRSAPublicKeyFile.getUpdateCount() <= 0) {
            this.serverRSAPublicKeyFile.setValue(serverRSAPublicKeyFile2);
        } else {
            throw SQLError.createSQLException(Messages.getString("ConnectionProperties.dynamicChangeIsNotAllowed", new Object[]{"'serverRSAPublicKeyFile'"}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
        }
    }

    public boolean getAllowPublicKeyRetrieval() {
        return this.allowPublicKeyRetrieval.getValueAsBoolean();
    }

    public void setAllowPublicKeyRetrieval(boolean allowPublicKeyRetrieval2) throws SQLException {
        if (this.allowPublicKeyRetrieval.getUpdateCount() <= 0) {
            this.allowPublicKeyRetrieval.setValue(allowPublicKeyRetrieval2);
        } else {
            throw SQLError.createSQLException(Messages.getString("ConnectionProperties.dynamicChangeIsNotAllowed", new Object[]{"'allowPublicKeyRetrieval'"}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
        }
    }

    public void setDontCheckOnDuplicateKeyUpdateInSQL(boolean dontCheckOnDuplicateKeyUpdateInSQL2) {
        this.dontCheckOnDuplicateKeyUpdateInSQL.setValue(dontCheckOnDuplicateKeyUpdateInSQL2);
    }

    public boolean getDontCheckOnDuplicateKeyUpdateInSQL() {
        return this.dontCheckOnDuplicateKeyUpdateInSQL.getValueAsBoolean();
    }

    public void setSocksProxyHost(String socksProxyHost2) {
        this.socksProxyHost.setValue(socksProxyHost2);
    }

    public String getSocksProxyHost() {
        return this.socksProxyHost.getValueAsString();
    }

    public void setSocksProxyPort(int socksProxyPort2) throws SQLException {
        this.socksProxyPort.setValue(socksProxyPort2, (ExceptionInterceptor) null);
    }

    public int getSocksProxyPort() {
        return this.socksProxyPort.getValueAsInt();
    }

    public boolean getReadOnlyPropagatesToServer() {
        return this.readOnlyPropagatesToServer.getValueAsBoolean();
    }

    public void setReadOnlyPropagatesToServer(boolean flag) {
        this.readOnlyPropagatesToServer.setValue(flag);
    }

    public String getEnabledSSLCipherSuites() {
        return this.enabledSSLCipherSuites.getValueAsString();
    }

    public void setEnabledSSLCipherSuites(String cipherSuites) {
        this.enabledSSLCipherSuites.setValue(cipherSuites);
    }

    public String getEnabledTLSProtocols() {
        return this.enabledTLSProtocols.getValueAsString();
    }

    public void setEnabledTLSProtocols(String protocols) {
        this.enabledTLSProtocols.setValue(protocols);
    }

    public boolean getEnableEscapeProcessing() {
        return this.enableEscapeProcessing.getValueAsBoolean();
    }

    public void setEnableEscapeProcessing(boolean flag) {
        this.enableEscapeProcessing.setValue(flag);
    }
}
