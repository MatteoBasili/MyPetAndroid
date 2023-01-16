package com.mysql.jdbc;

import androidx.core.internal.view.SupportMenu;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.mysql.jdbc.authentication.CachingSha2PasswordPlugin;
import com.mysql.jdbc.authentication.MysqlClearPasswordPlugin;
import com.mysql.jdbc.authentication.MysqlNativePasswordPlugin;
import com.mysql.jdbc.authentication.MysqlOldPasswordPlugin;
import com.mysql.jdbc.authentication.Sha256PasswordPlugin;
import com.mysql.jdbc.util.ResultSetUtil;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.ref.SoftReference;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.Deflater;
import kotlin.text.Typography;

public class MysqlIO {
    protected static final int AUTH_411_OVERHEAD = 33;
    private static final int CLIENT_CAN_HANDLE_EXPIRED_PASSWORD = 4194304;
    private static final int CLIENT_COMPRESS = 32;
    private static final int CLIENT_CONNECT_ATTRS = 1048576;
    protected static final int CLIENT_CONNECT_WITH_DB = 8;
    private static final int CLIENT_DEPRECATE_EOF = 16777216;
    private static final int CLIENT_FOUND_ROWS = 2;
    private static final int CLIENT_INTERACTIVE = 1024;
    private static final int CLIENT_LOCAL_FILES = 128;
    private static final int CLIENT_LONG_FLAG = 4;
    private static final int CLIENT_LONG_PASSWORD = 1;
    private static final int CLIENT_MULTI_RESULTS = 131072;
    private static final int CLIENT_MULTI_STATEMENTS = 65536;
    private static final int CLIENT_PLUGIN_AUTH = 524288;
    private static final int CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA = 2097152;
    private static final int CLIENT_PROTOCOL_41 = 512;
    protected static final int CLIENT_RESERVED = 16384;
    protected static final int CLIENT_SECURE_CONNECTION = 32768;
    private static final int CLIENT_SESSION_TRACK = 8388608;
    protected static final int CLIENT_SSL = 2048;
    private static final int CLIENT_TRANSACTIONS = 8192;
    private static final String CODE_PAGE_1252 = "Cp1252";
    protected static final int COMP_HEADER_LENGTH = 3;
    private static final String EXPLAINABLE_STATEMENT = "SELECT";
    private static final String[] EXPLAINABLE_STATEMENT_EXTENSION = {"INSERT", "UPDATE", "REPLACE", "DELETE"};
    private static final String FALSE_SCRAMBLE = "xxxxxxxx";
    protected static final int HEADER_LENGTH = 4;
    protected static final int INITIAL_PACKET_SIZE = 1024;
    private static final int MAX_PACKET_DUMP_LENGTH = 1024;
    protected static final int MAX_QUERY_SIZE_TO_EXPLAIN = 1048576;
    protected static final int MAX_QUERY_SIZE_TO_LOG = 1024;
    protected static final int MIN_COMPRESS_LEN = 50;
    private static final String NONE = "none";
    protected static final int NULL_LENGTH = -1;
    public static final int SEED_LENGTH = 20;
    static final int SERVER_MORE_RESULTS_EXISTS = 8;
    private static final int SERVER_QUERY_NO_GOOD_INDEX_USED = 16;
    private static final int SERVER_QUERY_NO_INDEX_USED = 32;
    private static final int SERVER_QUERY_WAS_SLOW = 2048;
    private static final int SERVER_STATUS_AUTOCOMMIT = 2;
    private static final int SERVER_STATUS_CURSOR_EXISTS = 64;
    private static final int SERVER_STATUS_IN_TRANS = 1;
    protected static final String ZERO_DATETIME_VALUE_MARKER = "0000-00-00 00:00:00";
    protected static final String ZERO_DATE_VALUE_MARKER = "0000-00-00";
    private static String jvmPlatformCharset;
    private static int maxBufferSize = SupportMenu.USER_MASK;
    private int authPluginDataLength = 0;
    private Map<String, AuthenticationPlugin> authenticationPlugins = null;
    private boolean autoGenerateTestcaseScript;
    private boolean checkPacketSequence = false;
    private String clientDefaultAuthenticationPlugin = null;
    private String clientDefaultAuthenticationPluginName = null;
    protected long clientParam = 0;
    private boolean colDecimalNeedsBump = false;
    private int commandCount = 0;
    private SoftReference<Buffer> compressBufRef;
    private byte compressedPacketSequence = 0;
    protected MySQLConnection connection;
    private Deflater deflater = null;
    private List<String> disabledAuthenticationPlugins = null;
    private boolean enablePacketDebug = false;
    private ExceptionInterceptor exceptionInterceptor;
    private boolean hadWarnings = false;
    private boolean has41NewNewProt = false;
    private boolean hasLongColumnInfo = false;
    protected String host = null;
    private boolean isInteractiveClient = false;
    protected long lastPacketReceivedTimeMs = 0;
    protected long lastPacketSentTimeMs = 0;
    private SoftReference<Buffer> loadFileBufRef;
    private boolean logSlowQueries = false;
    private int maxAllowedPacket = 1048576;
    protected int maxThreeBytes = 16581375;
    public Socket mysqlConnection = null;
    protected InputStream mysqlInput = null;
    protected BufferedOutputStream mysqlOutput = null;
    private boolean needToGrabQueryFromPacket;
    private int oldServerStatus = 0;
    private LinkedList<StringBuilder> packetDebugRingBuffer = null;
    private byte[] packetHeaderBuf = new byte[4];
    private byte packetSequence = 0;
    private boolean packetSequenceReset = false;
    private boolean platformDbCharsetMatches = true;
    protected int port = 3306;
    private boolean profileSql = false;
    private byte protocolVersion = 0;
    private boolean queryBadIndexUsed = false;
    private boolean queryNoIndexUsed = false;
    private String queryTimingUnits;
    private byte readPacketSequence = -1;
    private Buffer reusablePacket = null;
    protected String seed;
    private Buffer sendPacket = null;
    protected int serverCapabilities;
    protected int serverCharsetIndex;
    private String serverDefaultAuthenticationPluginName = null;
    private int serverMajorVersion = 0;
    private int serverMinorVersion = 0;
    private boolean serverQueryWasSlow = false;
    private int serverStatus = 0;
    private int serverSubMinorVersion = 0;
    private String serverVersion = null;
    private Buffer sharedSendPacket = null;
    private long slowQueryThreshold;
    protected SocketFactory socketFactory = null;
    private String socketFactoryClassName = null;
    private SoftReference<Buffer> splitBufRef;
    private int statementExecutionDepth = 0;
    private List<StatementInterceptorV2> statementInterceptors;
    private RowData streamingData = null;
    private long threadId;
    private boolean traceProtocol = false;
    private boolean use41Extensions = false;
    private boolean useAutoSlowLog;
    private int useBufferRowSizeThreshold;
    private boolean useCompression = false;
    private boolean useConnectWithDb;
    private boolean useDirectRowUnpack = true;
    private boolean useNanosForElapsedTime;
    private boolean useNewLargePackets = false;
    private boolean useNewUpdateCounts = false;
    private int warningCount = 0;

    static {
        jvmPlatformCharset = null;
        OutputStreamWriter outWriter = null;
        try {
            outWriter = new OutputStreamWriter(new ByteArrayOutputStream());
            jvmPlatformCharset = outWriter.getEncoding();
            try {
                outWriter.close();
            } catch (IOException e) {
            }
        } catch (Throwable th) {
            if (outWriter != null) {
                try {
                    outWriter.close();
                } catch (IOException e2) {
                }
            }
            throw th;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:33:0x0181 A[Catch:{ IOException -> 0x0199 }] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0184 A[Catch:{ IOException -> 0x0199 }] */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0194 A[Catch:{ IOException -> 0x0199 }] */
    /* JADX WARNING: Removed duplicated region for block: B:42:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public MysqlIO(java.lang.String r9, int r10, java.util.Properties r11, java.lang.String r12, com.mysql.jdbc.MySQLConnection r13, int r14, int r15) throws java.io.IOException, java.sql.SQLException {
        /*
            r8 = this;
            r8.<init>()
            r0 = 0
            r8.packetSequenceReset = r0
            r1 = 0
            r8.reusablePacket = r1
            r8.sendPacket = r1
            r8.sharedSendPacket = r1
            r8.mysqlOutput = r1
            r8.deflater = r1
            r8.mysqlInput = r1
            r8.packetDebugRingBuffer = r1
            r8.streamingData = r1
            r8.mysqlConnection = r1
            r8.socketFactory = r1
            r8.host = r1
            r8.serverVersion = r1
            r8.socketFactoryClassName = r1
            r2 = 4
            byte[] r2 = new byte[r2]
            r8.packetHeaderBuf = r2
            r8.colDecimalNeedsBump = r0
            r8.hadWarnings = r0
            r8.has41NewNewProt = r0
            r8.hasLongColumnInfo = r0
            r8.isInteractiveClient = r0
            r8.logSlowQueries = r0
            r2 = 1
            r8.platformDbCharsetMatches = r2
            r8.profileSql = r0
            r8.queryBadIndexUsed = r0
            r8.queryNoIndexUsed = r0
            r8.serverQueryWasSlow = r0
            r8.use41Extensions = r0
            r8.useCompression = r0
            r8.useNewLargePackets = r0
            r8.useNewUpdateCounts = r0
            r8.packetSequence = r0
            r8.compressedPacketSequence = r0
            r3 = -1
            r8.readPacketSequence = r3
            r8.checkPacketSequence = r0
            r8.protocolVersion = r0
            r3 = 1048576(0x100000, float:1.469368E-39)
            r8.maxAllowedPacket = r3
            r3 = 16581375(0xfd02ff, float:2.3235455E-38)
            r8.maxThreeBytes = r3
            r3 = 3306(0xcea, float:4.633E-42)
            r8.port = r3
            r8.serverMajorVersion = r0
            r8.serverMinorVersion = r0
            r8.oldServerStatus = r0
            r8.serverStatus = r0
            r8.serverSubMinorVersion = r0
            r8.warningCount = r0
            r3 = 0
            r8.clientParam = r3
            r8.lastPacketSentTimeMs = r3
            r8.lastPacketReceivedTimeMs = r3
            r8.traceProtocol = r0
            r8.enablePacketDebug = r0
            r8.useDirectRowUnpack = r2
            r8.commandCount = r0
            r8.authPluginDataLength = r0
            r8.authenticationPlugins = r1
            r8.disabledAuthenticationPlugins = r1
            r8.clientDefaultAuthenticationPlugin = r1
            r8.clientDefaultAuthenticationPluginName = r1
            r8.serverDefaultAuthenticationPluginName = r1
            r8.statementExecutionDepth = r0
            r8.connection = r13
            boolean r1 = r13.getEnablePacketDebug()
            if (r1 == 0) goto L_0x0096
            java.util.LinkedList r1 = new java.util.LinkedList
            r1.<init>()
            r8.packetDebugRingBuffer = r1
        L_0x0096:
            com.mysql.jdbc.MySQLConnection r1 = r8.connection
            boolean r1 = r1.getTraceProtocol()
            r8.traceProtocol = r1
            com.mysql.jdbc.MySQLConnection r1 = r8.connection
            boolean r1 = r1.getAutoSlowLog()
            r8.useAutoSlowLog = r1
            r8.useBufferRowSizeThreshold = r15
            com.mysql.jdbc.MySQLConnection r1 = r8.connection
            boolean r1 = r1.getUseDirectRowUnpack()
            r8.useDirectRowUnpack = r1
            com.mysql.jdbc.MySQLConnection r1 = r8.connection
            boolean r1 = r1.getLogSlowQueries()
            r8.logSlowQueries = r1
            com.mysql.jdbc.Buffer r1 = new com.mysql.jdbc.Buffer
            r3 = 1024(0x400, float:1.435E-42)
            r1.<init>((int) r3)
            r8.reusablePacket = r1
            com.mysql.jdbc.Buffer r1 = new com.mysql.jdbc.Buffer
            r1.<init>((int) r3)
            r8.sendPacket = r1
            r8.port = r10
            r8.host = r9
            r8.socketFactoryClassName = r12
            com.mysql.jdbc.SocketFactory r1 = r8.createSocketFactory()
            r8.socketFactory = r1
            com.mysql.jdbc.MySQLConnection r1 = r8.connection
            com.mysql.jdbc.ExceptionInterceptor r1 = r1.getExceptionInterceptor()
            r8.exceptionInterceptor = r1
            com.mysql.jdbc.SocketFactory r1 = r8.socketFactory     // Catch:{ IOException -> 0x0199 }
            java.lang.String r3 = r8.host     // Catch:{ IOException -> 0x0199 }
            int r4 = r8.port     // Catch:{ IOException -> 0x0199 }
            java.net.Socket r1 = r1.connect(r3, r4, r11)     // Catch:{ IOException -> 0x0199 }
            r8.mysqlConnection = r1     // Catch:{ IOException -> 0x0199 }
            if (r14 == 0) goto L_0x00ef
            r1.setSoTimeout(r14)     // Catch:{ Exception -> 0x00ee }
            goto L_0x00ef
        L_0x00ee:
            r1 = move-exception
        L_0x00ef:
            com.mysql.jdbc.SocketFactory r1 = r8.socketFactory     // Catch:{ IOException -> 0x0199 }
            java.net.Socket r1 = r1.beforeHandshake()     // Catch:{ IOException -> 0x0199 }
            r8.mysqlConnection = r1     // Catch:{ IOException -> 0x0199 }
            com.mysql.jdbc.MySQLConnection r1 = r8.connection     // Catch:{ IOException -> 0x0199 }
            boolean r1 = r1.getUseReadAheadInput()     // Catch:{ IOException -> 0x0199 }
            r3 = 16384(0x4000, float:2.2959E-41)
            if (r1 == 0) goto L_0x011b
            com.mysql.jdbc.util.ReadAheadInputStream r1 = new com.mysql.jdbc.util.ReadAheadInputStream     // Catch:{ IOException -> 0x0199 }
            java.net.Socket r4 = r8.mysqlConnection     // Catch:{ IOException -> 0x0199 }
            java.io.InputStream r4 = r4.getInputStream()     // Catch:{ IOException -> 0x0199 }
            com.mysql.jdbc.MySQLConnection r5 = r8.connection     // Catch:{ IOException -> 0x0199 }
            boolean r5 = r5.getTraceProtocol()     // Catch:{ IOException -> 0x0199 }
            com.mysql.jdbc.MySQLConnection r6 = r8.connection     // Catch:{ IOException -> 0x0199 }
            com.mysql.jdbc.log.Log r6 = r6.getLog()     // Catch:{ IOException -> 0x0199 }
            r1.<init>(r4, r3, r5, r6)     // Catch:{ IOException -> 0x0199 }
            r8.mysqlInput = r1     // Catch:{ IOException -> 0x0199 }
            goto L_0x0139
        L_0x011b:
            com.mysql.jdbc.MySQLConnection r1 = r8.connection     // Catch:{ IOException -> 0x0199 }
            boolean r1 = r1.useUnbufferedInput()     // Catch:{ IOException -> 0x0199 }
            if (r1 == 0) goto L_0x012c
            java.net.Socket r1 = r8.mysqlConnection     // Catch:{ IOException -> 0x0199 }
            java.io.InputStream r1 = r1.getInputStream()     // Catch:{ IOException -> 0x0199 }
            r8.mysqlInput = r1     // Catch:{ IOException -> 0x0199 }
            goto L_0x0139
        L_0x012c:
            java.io.BufferedInputStream r1 = new java.io.BufferedInputStream     // Catch:{ IOException -> 0x0199 }
            java.net.Socket r4 = r8.mysqlConnection     // Catch:{ IOException -> 0x0199 }
            java.io.InputStream r4 = r4.getInputStream()     // Catch:{ IOException -> 0x0199 }
            r1.<init>(r4, r3)     // Catch:{ IOException -> 0x0199 }
            r8.mysqlInput = r1     // Catch:{ IOException -> 0x0199 }
        L_0x0139:
            java.io.BufferedOutputStream r1 = new java.io.BufferedOutputStream     // Catch:{ IOException -> 0x0199 }
            java.net.Socket r4 = r8.mysqlConnection     // Catch:{ IOException -> 0x0199 }
            java.io.OutputStream r4 = r4.getOutputStream()     // Catch:{ IOException -> 0x0199 }
            r1.<init>(r4, r3)     // Catch:{ IOException -> 0x0199 }
            r8.mysqlOutput = r1     // Catch:{ IOException -> 0x0199 }
            com.mysql.jdbc.MySQLConnection r1 = r8.connection     // Catch:{ IOException -> 0x0199 }
            boolean r1 = r1.getInteractiveClient()     // Catch:{ IOException -> 0x0199 }
            r8.isInteractiveClient = r1     // Catch:{ IOException -> 0x0199 }
            com.mysql.jdbc.MySQLConnection r1 = r8.connection     // Catch:{ IOException -> 0x0199 }
            boolean r1 = r1.getProfileSql()     // Catch:{ IOException -> 0x0199 }
            r8.profileSql = r1     // Catch:{ IOException -> 0x0199 }
            com.mysql.jdbc.MySQLConnection r1 = r8.connection     // Catch:{ IOException -> 0x0199 }
            boolean r1 = r1.getAutoGenerateTestcaseScript()     // Catch:{ IOException -> 0x0199 }
            r8.autoGenerateTestcaseScript = r1     // Catch:{ IOException -> 0x0199 }
            boolean r3 = r8.profileSql     // Catch:{ IOException -> 0x0199 }
            if (r3 != 0) goto L_0x016b
            boolean r3 = r8.logSlowQueries     // Catch:{ IOException -> 0x0199 }
            if (r3 != 0) goto L_0x016b
            if (r1 == 0) goto L_0x0169
            goto L_0x016b
        L_0x0169:
            r1 = r0
            goto L_0x016c
        L_0x016b:
            r1 = r2
        L_0x016c:
            r8.needToGrabQueryFromPacket = r1     // Catch:{ IOException -> 0x0199 }
            com.mysql.jdbc.MySQLConnection r1 = r8.connection     // Catch:{ IOException -> 0x0199 }
            boolean r1 = r1.getUseNanosForElapsedTime()     // Catch:{ IOException -> 0x0199 }
            if (r1 == 0) goto L_0x017d
            boolean r1 = com.mysql.jdbc.TimeUtil.nanoTimeAvailable()     // Catch:{ IOException -> 0x0199 }
            if (r1 == 0) goto L_0x017d
            r0 = r2
        L_0x017d:
            r8.useNanosForElapsedTime = r0     // Catch:{ IOException -> 0x0199 }
            if (r0 == 0) goto L_0x0184
            java.lang.String r0 = "Nanoseconds"
            goto L_0x0186
        L_0x0184:
            java.lang.String r0 = "Milliseconds"
        L_0x0186:
            java.lang.String r0 = com.mysql.jdbc.Messages.getString(r0)     // Catch:{ IOException -> 0x0199 }
            r8.queryTimingUnits = r0     // Catch:{ IOException -> 0x0199 }
            com.mysql.jdbc.MySQLConnection r0 = r8.connection     // Catch:{ IOException -> 0x0199 }
            boolean r0 = r0.getLogSlowQueries()     // Catch:{ IOException -> 0x0199 }
            if (r0 == 0) goto L_0x0197
            r8.calculateSlowQueryThreshold()     // Catch:{ IOException -> 0x0199 }
        L_0x0197:
            return
        L_0x0199:
            r0 = move-exception
            r6 = r0
            com.mysql.jdbc.MySQLConnection r1 = r8.connection
            r2 = 0
            r4 = 0
            com.mysql.jdbc.ExceptionInterceptor r7 = r8.getExceptionInterceptor()
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createCommunicationsException(r1, r2, r4, r6, r7)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.MysqlIO.<init>(java.lang.String, int, java.util.Properties, java.lang.String, com.mysql.jdbc.MySQLConnection, int, int):void");
    }

    public boolean hasLongColumnInfo() {
        return this.hasLongColumnInfo;
    }

    /* access modifiers changed from: protected */
    public boolean isDataAvailable() throws SQLException {
        try {
            return this.mysqlInput.available() > 0;
        } catch (IOException e) {
            throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, e, getExceptionInterceptor());
        }
    }

    /* access modifiers changed from: protected */
    public long getLastPacketSentTimeMs() {
        return this.lastPacketSentTimeMs;
    }

    /* access modifiers changed from: protected */
    public long getLastPacketReceivedTimeMs() {
        return this.lastPacketReceivedTimeMs;
    }

    /* access modifiers changed from: protected */
    public ResultSetImpl getResultSet(StatementImpl callingStatement, long columnCount, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, boolean isBinaryEncoded, Field[] metadataFromCache) throws SQLException {
        Field[] fields;
        RowData rowData;
        boolean usingCursor;
        long j = columnCount;
        boolean z = isBinaryEncoded;
        boolean usingCursor2 = false;
        if (metadataFromCache == null) {
            Field[] fields2 = new Field[((int) j)];
            for (int i = 0; ((long) i) < j; i++) {
                fields2[i] = unpackField(readPacket(), false);
            }
            fields = fields2;
        } else {
            for (int i2 = 0; ((long) i2) < j; i2++) {
                skipPacket();
            }
            fields = null;
        }
        if (!isEOFDeprecated() || (this.connection.versionMeetsMinimum(5, 0, 2) && callingStatement != null && z && callingStatement.isCursorRequired())) {
            readServerStatusForResultSets(reuseAndReadPacket(this.reusablePacket));
        }
        if (this.connection.versionMeetsMinimum(5, 0, 2) && this.connection.getUseCursorFetch() && z && callingStatement != null && callingStatement.getFetchSize() != 0 && callingStatement.getResultSetType() == 1003) {
            ServerPreparedStatement prepStmt = (ServerPreparedStatement) callingStatement;
            if (this.connection.versionMeetsMinimum(5, 0, 5)) {
                if ((this.serverStatus & 64) != 0) {
                    usingCursor2 = true;
                }
                usingCursor = usingCursor2;
            } else {
                usingCursor = true;
            }
            if (usingCursor) {
                ResultSetImpl rs = buildResultSetWithRows(callingStatement, catalog, fields, new RowDataCursor(this, prepStmt, fields), resultSetType, resultSetConcurrency, isBinaryEncoded);
                if (usingCursor) {
                    rs.setFetchSize(callingStatement.getFetchSize());
                }
                return rs;
            }
        }
        if (!streamResults) {
            rowData = readSingleRowSet(columnCount, maxRows, resultSetConcurrency, isBinaryEncoded, metadataFromCache == null ? fields : metadataFromCache);
        } else {
            RowData rowDataDynamic = new RowDataDynamic(this, (int) j, metadataFromCache == null ? fields : metadataFromCache, z);
            this.streamingData = rowDataDynamic;
            rowData = rowDataDynamic;
        }
        return buildResultSetWithRows(callingStatement, catalog, metadataFromCache == null ? fields : metadataFromCache, rowData, resultSetType, resultSetConcurrency, isBinaryEncoded);
    }

    /* access modifiers changed from: protected */
    public NetworkResources getNetworkResources() {
        return new NetworkResources(this.mysqlConnection, this.mysqlInput, this.mysqlOutput);
    }

    /* access modifiers changed from: protected */
    public final void forceClose() {
        try {
            getNetworkResources().forceClose();
        } finally {
            this.mysqlConnection = null;
            this.mysqlInput = null;
            this.mysqlOutput = null;
        }
    }

    /* access modifiers changed from: protected */
    public final void skipPacket() throws SQLException {
        try {
            if (readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4) >= 4) {
                byte[] bArr = this.packetHeaderBuf;
                int packetLength = (bArr[0] & 255) + ((bArr[1] & 255) << 8) + ((bArr[2] & 255) << 16);
                if (this.traceProtocol) {
                    this.connection.getLog().logTrace(Messages.getString("MysqlIO.2") + packetLength + Messages.getString("MysqlIO.3") + StringUtils.dumpAsHex(this.packetHeaderBuf, 4));
                }
                byte multiPacketSeq = this.packetHeaderBuf[3];
                if (this.packetSequenceReset) {
                    this.packetSequenceReset = false;
                } else if (this.enablePacketDebug && this.checkPacketSequence) {
                    checkPacketSequencing(multiPacketSeq);
                }
                this.readPacketSequence = multiPacketSeq;
                skipFully(this.mysqlInput, (long) packetLength);
                return;
            }
            forceClose();
            throw new IOException(Messages.getString("MysqlIO.1"));
        } catch (IOException e) {
            throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, e, getExceptionInterceptor());
        } catch (OutOfMemoryError oom) {
            try {
                this.connection.realClose(false, false, true, oom);
            } catch (Exception e2) {
            }
            throw oom;
        }
    }

    /* access modifiers changed from: protected */
    public final Buffer readPacket() throws SQLException {
        try {
            if (readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4) >= 4) {
                byte[] bArr = this.packetHeaderBuf;
                int packetLength = (bArr[0] & 255) + ((bArr[1] & 255) << 8) + ((bArr[2] & 255) << 16);
                if (packetLength <= this.maxAllowedPacket) {
                    if (this.traceProtocol) {
                        this.connection.getLog().logTrace(Messages.getString("MysqlIO.2") + packetLength + Messages.getString("MysqlIO.3") + StringUtils.dumpAsHex(this.packetHeaderBuf, 4));
                    }
                    byte multiPacketSeq = this.packetHeaderBuf[3];
                    if (this.packetSequenceReset) {
                        this.packetSequenceReset = false;
                    } else if (this.enablePacketDebug && this.checkPacketSequence) {
                        checkPacketSequencing(multiPacketSeq);
                    }
                    this.readPacketSequence = multiPacketSeq;
                    byte[] buffer = new byte[packetLength];
                    int numBytesRead = readFully(this.mysqlInput, buffer, 0, packetLength);
                    if (numBytesRead == packetLength) {
                        Buffer packet = new Buffer(buffer);
                        if (this.traceProtocol) {
                            this.connection.getLog().logTrace(Messages.getString("MysqlIO.4") + getPacketDumpToLog(packet, packetLength));
                        }
                        if (this.enablePacketDebug) {
                            enqueuePacketForDebugging(false, false, 0, this.packetHeaderBuf, packet);
                        }
                        if (this.connection.getMaintainTimeStats()) {
                            this.lastPacketReceivedTimeMs = System.currentTimeMillis();
                        }
                        return packet;
                    }
                    throw new IOException("Short read, expected " + packetLength + " bytes, only read " + numBytesRead);
                }
                throw new PacketTooBigException((long) packetLength, (long) this.maxAllowedPacket);
            }
            forceClose();
            throw new IOException(Messages.getString("MysqlIO.1"));
        } catch (IOException e) {
            throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, e, getExceptionInterceptor());
        } catch (OutOfMemoryError oom) {
            try {
                this.connection.realClose(false, false, true, oom);
            } catch (Exception e2) {
            }
            throw oom;
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final com.mysql.jdbc.Field unpackField(com.mysql.jdbc.Buffer r42, boolean r43) throws java.sql.SQLException {
        /*
            r41 = this;
            r0 = r41
            boolean r1 = r0.use41Extensions
            if (r1 == 0) goto L_0x00ec
            boolean r1 = r0.has41NewNewProt
            if (r1 == 0) goto L_0x0017
            int r1 = r42.getPosition()
            int r1 = r1 + 1
            int r2 = r42.fastSkipLenString()
            r0.adjustStartForFieldLength(r1, r2)
        L_0x0017:
            int r1 = r42.getPosition()
            int r1 = r1 + 1
            int r15 = r42.fastSkipLenString()
            int r1 = r0.adjustStartForFieldLength(r1, r15)
            int r2 = r42.getPosition()
            int r2 = r2 + 1
            int r14 = r42.fastSkipLenString()
            int r23 = r0.adjustStartForFieldLength(r2, r14)
            int r2 = r42.getPosition()
            int r2 = r2 + 1
            int r13 = r42.fastSkipLenString()
            int r24 = r0.adjustStartForFieldLength(r2, r13)
            int r2 = r42.getPosition()
            int r2 = r2 + 1
            int r12 = r42.fastSkipLenString()
            int r25 = r0.adjustStartForFieldLength(r2, r12)
            int r2 = r42.getPosition()
            int r2 = r2 + 1
            int r11 = r42.fastSkipLenString()
            int r26 = r0.adjustStartForFieldLength(r2, r11)
            r42.readByte()
            int r2 = r42.readInt()
            short r10 = (short) r2
            r2 = 0
            boolean r4 = r0.has41NewNewProt
            if (r4 == 0) goto L_0x0072
            long r2 = r42.readLong()
            r27 = r2
            goto L_0x0079
        L_0x0072:
            int r4 = r42.readLongInt()
            long r2 = (long) r4
            r27 = r2
        L_0x0079:
            byte r2 = r42.readByte()
            r9 = r2 & 255(0xff, float:3.57E-43)
            r2 = 0
            boolean r3 = r0.hasLongColumnInfo
            if (r3 == 0) goto L_0x008c
            int r3 = r42.readInt()
            short r2 = (short) r3
            r29 = r2
            goto L_0x0095
        L_0x008c:
            byte r3 = r42.readByte()
            r3 = r3 & 255(0xff, float:3.57E-43)
            short r2 = (short) r3
            r29 = r2
        L_0x0095:
            byte r2 = r42.readByte()
            r8 = r2 & 255(0xff, float:3.57E-43)
            r2 = -1
            r3 = -1
            if (r43 == 0) goto L_0x00ae
            int r4 = r42.getPosition()
            int r2 = r4 + 1
            int r3 = r42.fastSkipLenString()
            r30 = r2
            r31 = r3
            goto L_0x00b2
        L_0x00ae:
            r30 = r2
            r31 = r3
        L_0x00b2:
            com.mysql.jdbc.Field r32 = new com.mysql.jdbc.Field
            r2 = r32
            com.mysql.jdbc.MySQLConnection r3 = r0.connection
            byte[] r4 = r42.getByteBuffer()
            r5 = r1
            r6 = r15
            r7 = r23
            r33 = r8
            r8 = r14
            r34 = r9
            r9 = r24
            r35 = r10
            r10 = r13
            r36 = r11
            r11 = r25
            r37 = r12
            r38 = r13
            r13 = r26
            r39 = r14
            r14 = r36
            r40 = r15
            r15 = r27
            r17 = r34
            r18 = r29
            r19 = r33
            r20 = r30
            r21 = r31
            r22 = r35
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r17, r18, r19, r20, r21, r22)
            return r2
        L_0x00ec:
            int r1 = r42.getPosition()
            int r1 = r1 + 1
            int r13 = r42.fastSkipLenString()
            int r1 = r0.adjustStartForFieldLength(r1, r13)
            int r2 = r42.getPosition()
            int r2 = r2 + 1
            int r14 = r42.fastSkipLenString()
            int r15 = r0.adjustStartForFieldLength(r2, r14)
            int r16 = r42.readnBytes()
            int r17 = r42.readnBytes()
            r42.readByte()
            r2 = 0
            boolean r3 = r0.hasLongColumnInfo
            if (r3 == 0) goto L_0x0120
            int r3 = r42.readInt()
            short r2 = (short) r3
            r18 = r2
            goto L_0x0129
        L_0x0120:
            byte r3 = r42.readByte()
            r3 = r3 & 255(0xff, float:3.57E-43)
            short r2 = (short) r3
            r18 = r2
        L_0x0129:
            byte r2 = r42.readByte()
            r2 = r2 & 255(0xff, float:3.57E-43)
            boolean r3 = r0.colDecimalNeedsBump
            if (r3 == 0) goto L_0x0138
            int r2 = r2 + 1
            r19 = r2
            goto L_0x013a
        L_0x0138:
            r19 = r2
        L_0x013a:
            com.mysql.jdbc.Field r20 = new com.mysql.jdbc.Field
            com.mysql.jdbc.MySQLConnection r3 = r0.connection
            byte[] r4 = r42.getByteBuffer()
            r2 = r20
            r5 = r15
            r6 = r14
            r7 = r1
            r8 = r13
            r9 = r16
            r10 = r17
            r11 = r18
            r12 = r19
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.MysqlIO.unpackField(com.mysql.jdbc.Buffer, boolean):com.mysql.jdbc.Field");
    }

    private int adjustStartForFieldLength(int nameStart, int nameLength) {
        if (nameLength < 251) {
            return nameStart;
        }
        if (nameLength >= 251 && nameLength < 65536) {
            return nameStart + 2;
        }
        if (nameLength < 65536 || nameLength >= 16777216) {
            return nameStart + 8;
        }
        return nameStart + 3;
    }

    /* access modifiers changed from: protected */
    public boolean isSetNeededForAutoCommitMode(boolean autoCommitFlag) {
        if (!this.use41Extensions || !this.connection.getElideSetAutoCommits()) {
            return true;
        }
        boolean autoCommitModeOnServer = (this.serverStatus & 2) != 0;
        if (!autoCommitFlag && versionMeetsMinimum(5, 0, 0)) {
            return true ^ inTransactionOnServer();
        }
        if (autoCommitModeOnServer != autoCommitFlag) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean inTransactionOnServer() {
        return (this.serverStatus & 1) != 0;
    }

    /* access modifiers changed from: protected */
    public void changeUser(String userName, String password, String database) throws SQLException {
        String str = userName;
        String str2 = password;
        String str3 = database;
        this.packetSequence = -1;
        this.compressedPacketSequence = -1;
        boolean localUseConnectWithDb = false;
        int packLength = (((str != null ? userName.length() : 0) + 16 + (str3 != null ? database.length() : 0)) * 3) + 7 + 4 + 33;
        int i = this.serverCapabilities;
        if ((524288 & i) != 0) {
            proceedHandshakeWithPluggableAuthentication(str, str2, str3, (Buffer) null);
        } else if ((i & 32768) != 0) {
            Buffer changeUserPacket = new Buffer(packLength + 1);
            changeUserPacket.writeByte((byte) 17);
            if (versionMeetsMinimum(4, 1, 1)) {
                Buffer buffer = changeUserPacket;
                secureAuth411(changeUserPacket, packLength, userName, password, database, false, true);
                return;
            }
            secureAuth(changeUserPacket, packLength, userName, password, database, false);
        } else {
            Buffer packet = new Buffer(packLength);
            packet.writeByte((byte) 17);
            packet.writeString(str);
            if (this.protocolVersion > 9) {
                packet.writeString(Util.newCrypt(str2, this.seed, this.connection.getPasswordCharacterEncoding()));
            } else {
                packet.writeString(Util.oldCrypt(str2, this.seed));
            }
            if (this.useConnectWithDb && str3 != null && database.length() > 0) {
                localUseConnectWithDb = true;
            }
            if (localUseConnectWithDb) {
                packet.writeString(str3);
            }
            send(packet, packet.getPosition());
            checkErrorPacket();
            if (!localUseConnectWithDb) {
                changeDatabaseTo(str3);
            }
        }
    }

    /* access modifiers changed from: protected */
    public Buffer checkErrorPacket() throws SQLException {
        return checkErrorPacket(-1);
    }

    /* access modifiers changed from: protected */
    public void checkForCharsetMismatch() {
        if (this.connection.getUseUnicode() && this.connection.getEncoding() != null) {
            String encodingToCheck = jvmPlatformCharset;
            if (encodingToCheck == null) {
                encodingToCheck = System.getProperty("file.encoding");
            }
            if (encodingToCheck == null) {
                this.platformDbCharsetMatches = false;
            } else {
                this.platformDbCharsetMatches = encodingToCheck.equals(this.connection.getEncoding());
            }
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP_START, MTH_ENTER_BLOCK, SYNTHETIC, Splitter:B:0:0x0000] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void clearInputStream() throws java.sql.SQLException {
        /*
            r8 = this;
        L_0x0000:
            java.io.InputStream r0 = r8.mysqlInput     // Catch:{ IOException -> 0x0019 }
            int r0 = r0.available()     // Catch:{ IOException -> 0x0019 }
            r1 = r0
            if (r0 <= 0) goto L_0x0017
            java.io.InputStream r0 = r8.mysqlInput     // Catch:{ IOException -> 0x0019 }
            long r2 = (long) r1     // Catch:{ IOException -> 0x0019 }
            long r2 = r0.skip(r2)     // Catch:{ IOException -> 0x0019 }
            r4 = 0
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x0017
            goto L_0x0000
        L_0x0017:
            return
        L_0x0019:
            r0 = move-exception
            r6 = r0
            com.mysql.jdbc.MySQLConnection r1 = r8.connection
            long r2 = r8.lastPacketSentTimeMs
            long r4 = r8.lastPacketReceivedTimeMs
            com.mysql.jdbc.ExceptionInterceptor r7 = r8.getExceptionInterceptor()
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createCommunicationsException(r1, r2, r4, r6, r7)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.MysqlIO.clearInputStream():void");
    }

    /* access modifiers changed from: protected */
    public void resetReadPacketSequence() {
        this.readPacketSequence = 0;
    }

    /* access modifiers changed from: protected */
    public void dumpPacketRingBuffer() throws SQLException {
        if (this.packetDebugRingBuffer != null && this.connection.getEnablePacketDebug()) {
            StringBuilder dumpBuffer = new StringBuilder();
            dumpBuffer.append("Last " + this.packetDebugRingBuffer.size() + " packets received from server, from oldest->newest:\n");
            dumpBuffer.append("\n");
            Iterator<StringBuilder> ringBufIter = this.packetDebugRingBuffer.iterator();
            while (ringBufIter.hasNext()) {
                dumpBuffer.append(ringBufIter.next());
                dumpBuffer.append("\n");
            }
            this.connection.getLog().logTrace(dumpBuffer.toString());
        }
    }

    /* access modifiers changed from: protected */
    public void explainSlowQuery(byte[] querySQL, String truncatedQuery) throws SQLException {
        if (StringUtils.startsWithIgnoreCaseAndWs(truncatedQuery, EXPLAINABLE_STATEMENT) || (versionMeetsMinimum(5, 6, 3) && StringUtils.startsWithIgnoreCaseAndWs(truncatedQuery, EXPLAINABLE_STATEMENT_EXTENSION) != -1)) {
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try {
                stmt = (PreparedStatement) this.connection.clientPrepareStatement("EXPLAIN ?");
                stmt.setBytesNoEscapeNoQuotes(1, querySQL);
                rs = stmt.executeQuery();
                StringBuilder explainResults = new StringBuilder(Messages.getString("MysqlIO.8") + truncatedQuery + Messages.getString("MysqlIO.9"));
                ResultSetUtil.appendResultSetSlashGStyle(explainResults, rs);
                this.connection.getLog().logWarn(explainResults.toString());
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
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
    }

    static int getMaxBuf() {
        return maxBufferSize;
    }

    /* access modifiers changed from: package-private */
    public final int getServerMajorVersion() {
        return this.serverMajorVersion;
    }

    /* access modifiers changed from: package-private */
    public final int getServerMinorVersion() {
        return this.serverMinorVersion;
    }

    /* access modifiers changed from: package-private */
    public final int getServerSubMinorVersion() {
        return this.serverSubMinorVersion;
    }

    /* access modifiers changed from: package-private */
    public String getServerVersion() {
        return this.serverVersion;
    }

    /* access modifiers changed from: package-private */
    public void doHandshake(String user, String password, String database) throws SQLException {
        byte b;
        byte b2;
        StringBuilder newSeed;
        String seedPart2;
        String str = user;
        String str2 = password;
        String str3 = database;
        int databaseLength = 0;
        this.checkPacketSequence = false;
        this.readPacketSequence = 0;
        Buffer buf = readPacket();
        byte readByte = buf.readByte();
        this.protocolVersion = readByte;
        if (readByte != -1) {
            String readString = buf.readString("ASCII", getExceptionInterceptor());
            this.serverVersion = readString;
            int point = readString.indexOf(46);
            if (point != -1) {
                try {
                    this.serverMajorVersion = Integer.parseInt(this.serverVersion.substring(0, point));
                } catch (NumberFormatException e) {
                }
                String str4 = this.serverVersion;
                String remaining = str4.substring(point + 1, str4.length());
                int point2 = remaining.indexOf(46);
                if (point2 != -1) {
                    try {
                        this.serverMinorVersion = Integer.parseInt(remaining.substring(0, point2));
                    } catch (NumberFormatException e2) {
                    }
                    String remaining2 = remaining.substring(point2 + 1, remaining.length());
                    int pos = 0;
                    while (pos < remaining2.length() && remaining2.charAt(pos) >= '0' && remaining2.charAt(pos) <= '9') {
                        pos++;
                    }
                    try {
                        this.serverSubMinorVersion = Integer.parseInt(remaining2.substring(0, pos));
                    } catch (NumberFormatException e3) {
                    }
                }
                int i = point2;
            } else {
                int i2 = point;
            }
            if (versionMeetsMinimum(4, 0, 8)) {
                this.maxThreeBytes = ViewCompat.MEASURED_SIZE_MASK;
                this.useNewLargePackets = true;
            } else {
                this.maxThreeBytes = 16581375;
                this.useNewLargePackets = false;
            }
            this.colDecimalNeedsBump = versionMeetsMinimum(3, 23, 0);
            this.colDecimalNeedsBump = !versionMeetsMinimum(3, 23, 15);
            this.useNewUpdateCounts = versionMeetsMinimum(3, 22, 5);
            this.threadId = buf.readLong();
            if (this.protocolVersion > 9) {
                this.seed = buf.readString("ASCII", getExceptionInterceptor(), 8);
                buf.readByte();
            } else {
                this.seed = buf.readString("ASCII", getExceptionInterceptor());
            }
            this.serverCapabilities = 0;
            if (buf.getPosition() < buf.getBufLength()) {
                this.serverCapabilities = buf.readInt();
            }
            if (versionMeetsMinimum(4, 1, 1) || (this.protocolVersion > 9 && (this.serverCapabilities & 512) != 0)) {
                this.serverCharsetIndex = buf.readByte() & 255;
                this.serverStatus = buf.readInt();
                checkTransactionState(0);
                int readInt = this.serverCapabilities | (buf.readInt() << 16);
                this.serverCapabilities = readInt;
                if ((readInt & 524288) != 0) {
                    this.authPluginDataLength = buf.readByte() & 255;
                } else {
                    buf.readByte();
                }
                buf.setPosition(buf.getPosition() + 10);
                if ((this.serverCapabilities & 32768) != 0) {
                    if (this.authPluginDataLength > 0) {
                        seedPart2 = buf.readString("ASCII", getExceptionInterceptor(), this.authPluginDataLength - 8);
                        newSeed = new StringBuilder(this.authPluginDataLength);
                    } else {
                        seedPart2 = buf.readString("ASCII", getExceptionInterceptor());
                        newSeed = new StringBuilder(20);
                    }
                    newSeed.append(this.seed);
                    newSeed.append(seedPart2);
                    this.seed = newSeed.toString();
                }
            }
            if ((this.serverCapabilities & 32) != 0 && this.connection.getUseCompression()) {
                this.clientParam |= 32;
            }
            boolean z = str3 != null && database.length() > 0 && !this.connection.getCreateDatabaseIfNotExist();
            this.useConnectWithDb = z;
            if (z) {
                this.clientParam |= 8;
            }
            if (versionMeetsMinimum(5, 7, 0) && !this.connection.getUseSSL() && !this.connection.isUseSSLExplicit()) {
                this.connection.setUseSSL(true);
                this.connection.setVerifyServerCertificate(false);
                this.connection.getLog().logWarn(Messages.getString("MysqlIO.SSLWarning"));
            }
            if ((this.serverCapabilities & 2048) == 0 && this.connection.getUseSSL()) {
                if (!this.connection.getRequireSSL()) {
                    this.connection.setUseSSL(false);
                } else {
                    this.connection.close();
                    forceClose();
                    throw SQLError.createSQLException(Messages.getString("MysqlIO.15"), SQLError.SQL_STATE_UNABLE_TO_CONNECT_TO_DATASOURCE, getExceptionInterceptor());
                }
            }
            if ((this.serverCapabilities & 4) != 0) {
                this.clientParam |= 4;
                this.hasLongColumnInfo = true;
            }
            if (!this.connection.getUseAffectedRows()) {
                this.clientParam |= 2;
            }
            if (this.connection.getAllowLoadLocalInfile()) {
                this.clientParam |= 128;
            }
            if (this.isInteractiveClient) {
                this.clientParam |= 1024;
            }
            int i3 = this.serverCapabilities;
            if ((16777216 & i3) != 0) {
                this.clientParam |= 16777216;
            }
            if ((i3 & 524288) != 0) {
                proceedHandshakeWithPluggableAuthentication(str, str2, str3, buf);
                return;
            }
            if (this.protocolVersion > 9) {
                this.clientParam |= 1;
            } else {
                this.clientParam &= -2;
            }
            if (versionMeetsMinimum(4, 1, 0) || (this.protocolVersion > 9 && (this.serverCapabilities & 16384) != 0)) {
                if (versionMeetsMinimum(4, 1, 1) || (this.protocolVersion > 9 && (this.serverCapabilities & 512) != 0)) {
                    long j = this.clientParam | 512;
                    this.clientParam = j;
                    this.has41NewNewProt = true;
                    long j2 = j | 8192;
                    this.clientParam = j2;
                    this.clientParam = j2 | 131072;
                    if (this.connection.getAllowMultiQueries()) {
                        this.clientParam |= 65536;
                    }
                } else {
                    this.clientParam |= 16384;
                    this.has41NewNewProt = false;
                }
                this.use41Extensions = true;
            }
            int userLength = str != null ? user.length() : 0;
            if (str3 != null) {
                databaseLength = database.length();
            }
            int packLength = ((userLength + 16 + databaseLength) * 3) + 7 + 4 + 33;
            if (this.connection.getUseSSL()) {
                int packLength2 = packLength;
                negotiateSSLConnection(str, str2, str3, packLength2);
                if ((this.serverCapabilities & 32768) == 0) {
                    b = 9;
                    Buffer packet = new Buffer(packLength2);
                    if (this.use41Extensions) {
                        packet.writeLong(this.clientParam);
                        packet.writeLong((long) this.maxThreeBytes);
                    } else {
                        packet.writeInt((int) this.clientParam);
                        packet.writeLongInt(this.maxThreeBytes);
                    }
                    packet.writeString(str);
                    if (this.protocolVersion > 9) {
                        packet.writeString(Util.newCrypt(str2, this.seed, this.connection.getPasswordCharacterEncoding()));
                    } else {
                        packet.writeString(Util.oldCrypt(str2, this.seed));
                    }
                    if (!((this.serverCapabilities & 8) == 0 || str3 == null || database.length() <= 0)) {
                        packet.writeString(str3);
                    }
                    send(packet, packet.getPosition());
                    Buffer buffer = packet;
                } else if (versionMeetsMinimum(4, 1, 1)) {
                    b = 9;
                    secureAuth411((Buffer) null, packLength2, user, password, database, true, false);
                } else {
                    b = 9;
                    secureAuth411((Buffer) null, packLength2, user, password, database, true, false);
                }
            } else if ((this.serverCapabilities & 32768) != 0) {
                this.clientParam |= 32768;
                if (versionMeetsMinimum(4, 1, 1) || (this.protocolVersion > 9 && (this.serverCapabilities & 512) != 0)) {
                    int i4 = packLength;
                    secureAuth411((Buffer) null, packLength, user, password, database, true, false);
                    b = 9;
                } else {
                    secureAuth((Buffer) null, packLength, user, password, database, true);
                    int i5 = packLength;
                    b = 9;
                }
            } else {
                Buffer packet2 = new Buffer(packLength);
                long j3 = this.clientParam;
                if ((j3 & 16384) != 0) {
                    if (!versionMeetsMinimum(4, 1, 1)) {
                        b2 = 9;
                        if (this.protocolVersion <= 9 || (this.serverCapabilities & 512) == 0) {
                            packet2.writeLong(this.clientParam);
                            packet2.writeLong((long) this.maxThreeBytes);
                        }
                    } else {
                        b2 = 9;
                    }
                    packet2.writeLong(this.clientParam);
                    packet2.writeLong((long) this.maxThreeBytes);
                    packet2.writeByte((byte) 8);
                    packet2.writeBytesNoNull(new byte[23]);
                } else {
                    b2 = 9;
                    packet2.writeInt((int) j3);
                    packet2.writeLongInt(this.maxThreeBytes);
                }
                packet2.writeString(str, CODE_PAGE_1252, this.connection);
                if (this.protocolVersion > b2) {
                    packet2.writeString(Util.newCrypt(str2, this.seed, this.connection.getPasswordCharacterEncoding()), CODE_PAGE_1252, this.connection);
                } else {
                    packet2.writeString(Util.oldCrypt(str2, this.seed), CODE_PAGE_1252, this.connection);
                }
                if (this.useConnectWithDb) {
                    packet2.writeString(str3, CODE_PAGE_1252, this.connection);
                }
                send(packet2, packet2.getPosition());
                Buffer buffer2 = packet2;
                b = b2;
            }
            if (!versionMeetsMinimum(4, 1, 1) || this.protocolVersion <= b || (this.serverCapabilities & 512) == 0) {
                checkErrorPacket();
            }
            if ((this.serverCapabilities & 32) != 0 && this.connection.getUseCompression() && !(this.mysqlInput instanceof CompressedInputStream)) {
                this.deflater = new Deflater();
                this.useCompression = true;
                this.mysqlInput = new CompressedInputStream(this.connection, this.mysqlInput);
            }
            if (!this.useConnectWithDb) {
                changeDatabaseTo(str3);
            }
            try {
                this.mysqlConnection = this.socketFactory.afterHandshake();
            } catch (IOException e4) {
                throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, e4, getExceptionInterceptor());
            }
        } else {
            try {
                this.mysqlConnection.close();
            } catch (Exception e5) {
            }
            int errno = buf.readInt();
            String serverErrorMessage = buf.readString("ASCII", getExceptionInterceptor());
            String xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
            throw SQLError.createSQLException(SQLError.get(xOpen) + ", " + (Messages.getString("MysqlIO.10") + serverErrorMessage + "\""), xOpen, errno, getExceptionInterceptor());
        }
    }

    private void loadAuthenticationPlugins() throws SQLException {
        String defaultAuthenticationPlugin = this.connection.getDefaultAuthenticationPlugin();
        this.clientDefaultAuthenticationPlugin = defaultAuthenticationPlugin;
        if (defaultAuthenticationPlugin == null || "".equals(defaultAuthenticationPlugin.trim())) {
            throw SQLError.createSQLException(Messages.getString("Connection.BadDefaultAuthenticationPlugin", new Object[]{this.clientDefaultAuthenticationPlugin}), getExceptionInterceptor());
        }
        String disabledPlugins = this.connection.getDisabledAuthenticationPlugins();
        if (disabledPlugins != null && !"".equals(disabledPlugins)) {
            this.disabledAuthenticationPlugins = new ArrayList();
            for (String add : StringUtils.split(disabledPlugins, ",", true)) {
                this.disabledAuthenticationPlugins.add(add);
            }
        }
        this.authenticationPlugins = new HashMap();
        AuthenticationPlugin plugin = new MysqlOldPasswordPlugin();
        MySQLConnection mySQLConnection = this.connection;
        plugin.init(mySQLConnection, mySQLConnection.getProperties());
        boolean defaultIsFound = addAuthenticationPlugin(plugin);
        AuthenticationPlugin plugin2 = new MysqlNativePasswordPlugin();
        MySQLConnection mySQLConnection2 = this.connection;
        plugin2.init(mySQLConnection2, mySQLConnection2.getProperties());
        if (addAuthenticationPlugin(plugin2)) {
            defaultIsFound = true;
        }
        AuthenticationPlugin plugin3 = new MysqlClearPasswordPlugin();
        MySQLConnection mySQLConnection3 = this.connection;
        plugin3.init(mySQLConnection3, mySQLConnection3.getProperties());
        if (addAuthenticationPlugin(plugin3)) {
            defaultIsFound = true;
        }
        AuthenticationPlugin plugin4 = new Sha256PasswordPlugin();
        MySQLConnection mySQLConnection4 = this.connection;
        plugin4.init(mySQLConnection4, mySQLConnection4.getProperties());
        if (addAuthenticationPlugin(plugin4)) {
            defaultIsFound = true;
        }
        AuthenticationPlugin plugin5 = new CachingSha2PasswordPlugin();
        MySQLConnection mySQLConnection5 = this.connection;
        plugin5.init(mySQLConnection5, mySQLConnection5.getProperties());
        if (addAuthenticationPlugin(plugin5)) {
            defaultIsFound = true;
        }
        String authenticationPluginClasses = this.connection.getAuthenticationPlugins();
        if (authenticationPluginClasses != null && !"".equals(authenticationPluginClasses)) {
            MySQLConnection mySQLConnection6 = this.connection;
            Iterator i$ = Util.loadExtensions(mySQLConnection6, mySQLConnection6.getProperties(), authenticationPluginClasses, "Connection.BadAuthenticationPlugin", getExceptionInterceptor()).iterator();
            while (i$.hasNext()) {
                if (addAuthenticationPlugin((AuthenticationPlugin) i$.next())) {
                    defaultIsFound = true;
                }
            }
        }
        if (!defaultIsFound) {
            throw SQLError.createSQLException(Messages.getString("Connection.DefaultAuthenticationPluginIsNotListed", new Object[]{this.clientDefaultAuthenticationPlugin}), getExceptionInterceptor());
        }
    }

    private boolean addAuthenticationPlugin(AuthenticationPlugin plugin) throws SQLException {
        String pluginClassName = plugin.getClass().getName();
        String pluginProtocolName = plugin.getProtocolPluginName();
        List<String> list = this.disabledAuthenticationPlugins;
        boolean disabledByClassName = list != null && list.contains(pluginClassName);
        List<String> list2 = this.disabledAuthenticationPlugins;
        boolean disabledByMechanism = list2 != null && list2.contains(pluginProtocolName);
        if (!disabledByClassName && !disabledByMechanism) {
            this.authenticationPlugins.put(pluginProtocolName, plugin);
            if (!this.clientDefaultAuthenticationPlugin.equals(pluginClassName)) {
                return false;
            }
            this.clientDefaultAuthenticationPluginName = pluginProtocolName;
            return true;
        } else if (!this.clientDefaultAuthenticationPlugin.equals(pluginClassName)) {
            return false;
        } else {
            Object[] objArr = new Object[1];
            objArr[0] = disabledByClassName ? pluginClassName : pluginProtocolName;
            throw SQLError.createSQLException(Messages.getString("Connection.BadDisabledAuthenticationPlugin", objArr), getExceptionInterceptor());
        }
    }

    private AuthenticationPlugin getAuthenticationPlugin(String pluginName) throws SQLException {
        AuthenticationPlugin plugin = this.authenticationPlugins.get(pluginName);
        if (plugin == null || plugin.isReusable()) {
            return plugin;
        }
        try {
            plugin = (AuthenticationPlugin) plugin.getClass().newInstance();
            MySQLConnection mySQLConnection = this.connection;
            plugin.init(mySQLConnection, mySQLConnection.getProperties());
            return plugin;
        } catch (Throwable t) {
            SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.BadAuthenticationPlugin", new Object[]{plugin.getClass().getName()}), getExceptionInterceptor());
            sqlEx.initCause(t);
            throw sqlEx;
        }
    }

    private void checkConfidentiality(AuthenticationPlugin plugin) throws SQLException {
        if (plugin.requiresConfidentiality() && !isSSLEstablished()) {
            throw SQLError.createSQLException(Messages.getString("Connection.AuthenticationPluginRequiresSSL", new Object[]{plugin.getProtocolPluginName()}), getExceptionInterceptor());
        }
    }

    private void proceedHandshakeWithPluggableAuthentication(String user, String password, String database, Buffer challenge) throws SQLException {
        int counter;
        int userLength;
        Buffer fromServer;
        Buffer fromServer2;
        int databaseLength;
        int userLength2;
        int databaseLength2;
        byte b;
        String str;
        String str2 = user;
        String str3 = database;
        if (this.authenticationPlugins == null) {
            loadAuthenticationPlugins();
        }
        int passwordLength = 16;
        int userLength3 = str2 != null ? user.length() : 0;
        int databaseLength3 = str3 != null ? database.length() : 0;
        int packLength = ((userLength3 + 16 + databaseLength3) * 3) + 7 + 4 + 33;
        ArrayList<Buffer> toServer = new ArrayList<>();
        int counter2 = 100;
        boolean old_raw_challenge = false;
        boolean done = false;
        AuthenticationPlugin plugin = null;
        boolean skipPassword = false;
        Buffer challenge2 = challenge;
        while (true) {
            counter = counter2 - 1;
            if (counter2 <= 0) {
                int i = userLength3;
                int i2 = databaseLength3;
                Buffer buffer = challenge2;
                break;
            }
            int passwordLength2 = passwordLength;
            if (done) {
                userLength = userLength3;
                Buffer challenge3 = checkErrorPacket();
                old_raw_challenge = false;
                this.packetSequence = (byte) (this.packetSequence + 1);
                this.compressedPacketSequence = (byte) (this.compressedPacketSequence + 1);
                if (plugin == null) {
                    String str4 = this.serverDefaultAuthenticationPluginName;
                    if (str4 == null) {
                        str4 = this.clientDefaultAuthenticationPluginName;
                    }
                    plugin = getAuthenticationPlugin(str4);
                }
                if (challenge3.isOKPacket()) {
                    challenge3.newReadLength();
                    challenge3.newReadLength();
                    this.oldServerStatus = this.serverStatus;
                    this.serverStatus = challenge3.readInt();
                    plugin.destroy();
                    Buffer buffer2 = challenge3;
                    int i3 = databaseLength3;
                    break;
                } else if (challenge3.isAuthMethodSwitchRequestPacket()) {
                    String pluginName = challenge3.readString("ASCII", getExceptionInterceptor());
                    if (!plugin.getProtocolPluginName().equals(pluginName)) {
                        plugin.destroy();
                        plugin = getAuthenticationPlugin(pluginName);
                        if (plugin == null) {
                            throw SQLError.createSQLException(Messages.getString("Connection.BadAuthenticationPlugin", new Object[]{pluginName}), getExceptionInterceptor());
                        }
                    } else {
                        plugin.reset();
                    }
                    checkConfidentiality(plugin);
                    skipPassword = false;
                    fromServer = new Buffer(StringUtils.getBytes(challenge3.readString("ASCII", getExceptionInterceptor())));
                    fromServer2 = challenge3;
                } else if (versionMeetsMinimum(5, 5, 16)) {
                    fromServer = new Buffer(challenge3.getBytes(challenge3.getPosition(), challenge3.getBufLength() - challenge3.getPosition()));
                    fromServer2 = challenge3;
                } else {
                    old_raw_challenge = true;
                    fromServer = new Buffer(challenge3.getBytes(challenge3.getPosition() - 1, (challenge3.getBufLength() - challenge3.getPosition()) + 1));
                    fromServer2 = challenge3;
                }
            } else if (challenge2 == null) {
                userLength = userLength3;
                String str5 = this.serverDefaultAuthenticationPluginName;
                if (str5 == null) {
                    str5 = this.clientDefaultAuthenticationPluginName;
                }
                AuthenticationPlugin plugin2 = getAuthenticationPlugin(str5);
                checkConfidentiality(plugin2);
                plugin = plugin2;
                fromServer = new Buffer(StringUtils.getBytes(this.seed));
                fromServer2 = challenge2;
            } else if (!challenge2.isOKPacket()) {
                String str6 = "ASCII";
                this.clientParam |= 696833;
                if (this.connection.getAllowMultiQueries()) {
                    this.clientParam |= 65536;
                }
                if ((this.serverCapabilities & 4194304) != 0 && !this.connection.getDisconnectOnExpiredPasswords()) {
                    this.clientParam |= 4194304;
                }
                if ((this.serverCapabilities & 1048576) != 0 && !"none".equals(this.connection.getConnectionAttributes())) {
                    this.clientParam |= 1048576;
                }
                if ((this.serverCapabilities & 2097152) != 0) {
                    this.clientParam |= 2097152;
                }
                this.has41NewNewProt = true;
                this.use41Extensions = true;
                if (this.connection.getUseSSL()) {
                    negotiateSSLConnection(str2, password, str3, packLength);
                } else {
                    String str7 = password;
                }
                String pluginName2 = null;
                if ((this.serverCapabilities & 524288) != 0) {
                    if (versionMeetsMinimum(5, 5, 10)) {
                        userLength = userLength3;
                        if (!versionMeetsMinimum(5, 6, 0) || versionMeetsMinimum(5, 6, 2)) {
                            pluginName2 = challenge2.readString(str6, getExceptionInterceptor());
                        } else {
                            str = str6;
                        }
                    } else {
                        userLength = userLength3;
                        str = str6;
                    }
                    pluginName2 = challenge2.readString(str, getExceptionInterceptor(), this.authPluginDataLength);
                } else {
                    userLength = userLength3;
                }
                AuthenticationPlugin plugin3 = getAuthenticationPlugin(pluginName2);
                if (plugin3 == null) {
                    plugin3 = getAuthenticationPlugin(this.clientDefaultAuthenticationPluginName);
                } else if (pluginName2.equals(Sha256PasswordPlugin.PLUGIN_NAME) && !isSSLEstablished() && this.connection.getServerRSAPublicKeyFile() == null && !this.connection.getAllowPublicKeyRetrieval()) {
                    plugin3 = getAuthenticationPlugin(this.clientDefaultAuthenticationPluginName);
                    skipPassword = !this.clientDefaultAuthenticationPluginName.equals(pluginName2);
                }
                this.serverDefaultAuthenticationPluginName = plugin3.getProtocolPluginName();
                checkConfidentiality(plugin3);
                plugin = plugin3;
                fromServer = new Buffer(StringUtils.getBytes(this.seed));
                fromServer2 = challenge2;
            } else {
                throw SQLError.createSQLException(Messages.getString("Connection.UnexpectedAuthenticationApproval", new Object[]{plugin.getProtocolPluginName()}), getExceptionInterceptor());
            }
            try {
                plugin.setAuthenticationParameters(str2, skipPassword ? null : password);
                done = plugin.nextAuthenticationStep(fromServer, toServer);
                if (toServer.size() <= 0) {
                    challenge2 = fromServer2;
                    counter2 = counter;
                    passwordLength = passwordLength2;
                    databaseLength = databaseLength3;
                    userLength2 = userLength;
                } else if (fromServer2 == null) {
                    String enc = getEncodingForHandshake();
                    Buffer last_sent = new Buffer(packLength + 1);
                    last_sent.writeByte((byte) 17);
                    last_sent.writeString(str2, enc, this.connection);
                    if (toServer.get(0).getBufLength() < 256) {
                        b = 0;
                        last_sent.writeByte((byte) toServer.get(0).getBufLength());
                        databaseLength2 = databaseLength3;
                        last_sent.writeBytesNoNull(toServer.get(0).getByteBuffer(), 0, toServer.get(0).getBufLength());
                    } else {
                        databaseLength2 = databaseLength3;
                        b = 0;
                        last_sent.writeByte((byte) 0);
                    }
                    if (this.useConnectWithDb) {
                        last_sent.writeString(str3, enc, this.connection);
                    } else {
                        last_sent.writeByte(b);
                    }
                    appendCharsetByteForHandshake(last_sent, enc);
                    last_sent.writeByte(b);
                    if ((this.serverCapabilities & 524288) != 0) {
                        last_sent.writeString(plugin.getProtocolPluginName(), enc, this.connection);
                    }
                    if ((this.clientParam & 1048576) != 0) {
                        sendConnectionAttributes(last_sent, enc, this.connection);
                        last_sent.writeByte((byte) 0);
                    }
                    send(last_sent, last_sent.getPosition());
                    challenge2 = fromServer2;
                    counter2 = counter;
                    passwordLength = passwordLength2;
                    databaseLength = databaseLength2;
                    userLength2 = userLength;
                } else {
                    int databaseLength4 = databaseLength3;
                    if (fromServer2.isAuthMethodSwitchRequestPacket()) {
                        Buffer last_sent2 = new Buffer(toServer.get(0).getBufLength() + 4);
                        last_sent2.writeBytesNoNull(toServer.get(0).getByteBuffer(), 0, toServer.get(0).getBufLength());
                        send(last_sent2, last_sent2.getPosition());
                        challenge2 = fromServer2;
                        counter2 = counter;
                        passwordLength = passwordLength2;
                        databaseLength = databaseLength4;
                        userLength2 = userLength;
                    } else if (fromServer2.isRawPacket() || old_raw_challenge) {
                        Iterator i$ = toServer.iterator();
                        while (i$.hasNext()) {
                            Buffer buffer3 = i$.next();
                            Buffer last_sent3 = new Buffer(buffer3.getBufLength() + 4);
                            last_sent3.writeBytesNoNull(buffer3.getByteBuffer(), 0, toServer.get(0).getBufLength());
                            send(last_sent3, last_sent3.getPosition());
                            i$ = i$;
                        }
                        challenge2 = fromServer2;
                        counter2 = counter;
                        passwordLength = passwordLength2;
                        databaseLength = databaseLength4;
                        userLength2 = userLength;
                    } else {
                        String enc2 = getEncodingForHandshake();
                        Buffer last_sent4 = new Buffer(packLength);
                        last_sent4.writeLong(this.clientParam);
                        last_sent4.writeLong((long) this.maxThreeBytes);
                        appendCharsetByteForHandshake(last_sent4, enc2);
                        last_sent4.writeBytesNoNull(new byte[23]);
                        last_sent4.writeString(str2, enc2, this.connection);
                        if ((this.serverCapabilities & 2097152) != 0) {
                            last_sent4.writeLenBytes(toServer.get(0).getBytes(toServer.get(0).getBufLength()));
                        } else {
                            last_sent4.writeByte((byte) toServer.get(0).getBufLength());
                            last_sent4.writeBytesNoNull(toServer.get(0).getByteBuffer(), 0, toServer.get(0).getBufLength());
                        }
                        if (this.useConnectWithDb) {
                            last_sent4.writeString(str3, enc2, this.connection);
                        }
                        if ((this.serverCapabilities & 524288) != 0) {
                            last_sent4.writeString(plugin.getProtocolPluginName(), enc2, this.connection);
                        }
                        if ((this.clientParam & 1048576) != 0) {
                            sendConnectionAttributes(last_sent4, enc2, this.connection);
                        }
                        send(last_sent4, last_sent4.getPosition());
                        challenge2 = fromServer2;
                        counter2 = counter;
                        passwordLength = passwordLength2;
                        databaseLength = databaseLength4;
                        userLength2 = userLength;
                    }
                }
            } catch (SQLException e) {
                int i4 = databaseLength3;
                throw SQLError.createSQLException(e.getMessage(), e.getSQLState(), (Throwable) e, getExceptionInterceptor());
            }
        }
        if (counter != 0) {
            if ((this.serverCapabilities & 32) != 0 && this.connection.getUseCompression() && !(this.mysqlInput instanceof CompressedInputStream)) {
                this.deflater = new Deflater();
                this.useCompression = true;
                this.mysqlInput = new CompressedInputStream(this.connection, this.mysqlInput);
            }
            if (!this.useConnectWithDb) {
                changeDatabaseTo(str3);
            }
            try {
                this.mysqlConnection = this.socketFactory.afterHandshake();
            } catch (IOException e2) {
                throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, e2, getExceptionInterceptor());
            }
        } else {
            throw SQLError.createSQLException(Messages.getString("CommunicationsException.TooManyAuthenticationPluginNegotiations"), getExceptionInterceptor());
        }
    }

    private Properties getConnectionAttributesAsProperties(String atts) throws SQLException {
        Properties props = new Properties();
        if (atts != null) {
            for (String pair : atts.split(",")) {
                int keyEnd = pair.indexOf(":");
                if (keyEnd > 0 && keyEnd + 1 < pair.length()) {
                    props.setProperty(pair.substring(0, keyEnd), pair.substring(keyEnd + 1));
                }
            }
        }
        props.setProperty("_client_name", NonRegisteringDriver.NAME);
        props.setProperty("_client_version", NonRegisteringDriver.VERSION);
        props.setProperty("_runtime_vendor", NonRegisteringDriver.RUNTIME_VENDOR);
        props.setProperty("_runtime_version", NonRegisteringDriver.RUNTIME_VERSION);
        props.setProperty("_client_license", NonRegisteringDriver.LICENSE);
        return props;
    }

    private void sendConnectionAttributes(Buffer buf, String enc, MySQLConnection conn) throws SQLException {
        Buffer buffer = buf;
        String atts = conn.getConnectionAttributes();
        Buffer lb = new Buffer(100);
        try {
            Properties props = getConnectionAttributesAsProperties(atts);
            for (Object key : props.keySet()) {
                lb.writeLenString((String) key, enc, conn.getServerCharset(), (SingleByteCharsetConverter) null, conn.parserKnowsUnicode(), conn);
                lb.writeLenString(props.getProperty((String) key), enc, conn.getServerCharset(), (SingleByteCharsetConverter) null, conn.parserKnowsUnicode(), conn);
            }
        } catch (UnsupportedEncodingException e) {
        }
        buffer.writeByte((byte) (lb.getPosition() - 4));
        buffer.writeBytesNoNull(lb.getByteBuffer(), 4, lb.getBufLength() - 4);
    }

    private void changeDatabaseTo(String database) throws SQLException {
        String str = database;
        if (str != null && database.length() != 0) {
            try {
                sendCommand(2, database, (Buffer) null, false, (String) null, 0);
            } catch (Exception e) {
                Exception ex = e;
                if (this.connection.getCreateDatabaseIfNotExist()) {
                    sendCommand(3, "CREATE DATABASE IF NOT EXISTS " + str, (Buffer) null, false, (String) null, 0);
                    sendCommand(2, database, (Buffer) null, false, (String) null, 0);
                    return;
                }
                throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ex, getExceptionInterceptor());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public final ResultSetRow nextRow(Field[] fields, int columnCount, boolean isBinaryEncoded, int resultSetConcurrency, boolean useBufferRowIfPossible, boolean useBufferRowExplicit, boolean canReuseRowPacketForBufferRow, Buffer existingRowPacket) throws SQLException {
        Buffer rowPacket;
        if (this.useDirectRowUnpack && existingRowPacket == null && !isBinaryEncoded && !useBufferRowIfPossible && !useBufferRowExplicit) {
            return nextRowFast(fields, columnCount, isBinaryEncoded, resultSetConcurrency, useBufferRowIfPossible, useBufferRowExplicit, canReuseRowPacketForBufferRow);
        }
        if (existingRowPacket == null) {
            rowPacket = checkErrorPacket();
            if (!useBufferRowExplicit && useBufferRowIfPossible && rowPacket.getBufLength() > this.useBufferRowSizeThreshold) {
                useBufferRowExplicit = true;
            }
        } else {
            rowPacket = existingRowPacket;
            checkErrorPacket(existingRowPacket);
        }
        if (!isBinaryEncoded) {
            rowPacket.setPosition(rowPacket.getPosition() - 1);
            if ((!isEOFDeprecated() && rowPacket.isEOFPacket()) || (isEOFDeprecated() && rowPacket.isResultSetOKPacket())) {
                readServerStatusForResultSets(rowPacket);
                return null;
            } else if (resultSetConcurrency == 1008 || (!useBufferRowIfPossible && !useBufferRowExplicit)) {
                byte[][] rowData = new byte[columnCount][];
                for (int i = 0; i < columnCount; i++) {
                    rowData[i] = rowPacket.readLenByteArray(0);
                }
                return new ByteArrayRow(rowData, getExceptionInterceptor());
            } else {
                if (!canReuseRowPacketForBufferRow) {
                    this.reusablePacket = new Buffer(rowPacket.getBufLength());
                }
                return new BufferRow(rowPacket, fields, false, getExceptionInterceptor());
            }
        } else if ((!isEOFDeprecated() && rowPacket.isEOFPacket()) || (isEOFDeprecated() && rowPacket.isResultSetOKPacket())) {
            rowPacket.setPosition(rowPacket.getPosition() - 1);
            readServerStatusForResultSets(rowPacket);
            return null;
        } else if (resultSetConcurrency == 1008 || (!useBufferRowIfPossible && !useBufferRowExplicit)) {
            return unpackBinaryResultSetRow(fields, rowPacket, resultSetConcurrency);
        } else {
            if (!canReuseRowPacketForBufferRow) {
                this.reusablePacket = new Buffer(rowPacket.getBufLength());
            }
            return new BufferRow(rowPacket, fields, true, getExceptionInterceptor());
        }
    }

    /* access modifiers changed from: package-private */
    public final ResultSetRow nextRowFast(Field[] fields, int columnCount, boolean isBinaryEncoded, int resultSetConcurrency, boolean useBufferRowIfPossible, boolean useBufferRowExplicit, boolean canReuseRowPacket) throws SQLException {
        int i;
        int len;
        int i2;
        int remaining;
        int i3 = columnCount;
        try {
            int i4 = 4;
            int i5 = 0;
            int lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
            if (lengthRead >= 4) {
                byte[] bArr = this.packetHeaderBuf;
                int i6 = 255;
                byte b = 1;
                char c = 2;
                int packetLength = (bArr[0] & 255) + ((bArr[1] & 255) << 8) + ((bArr[2] & 255) << 16);
                if (packetLength == this.maxThreeBytes) {
                    reuseAndReadPacket(this.reusablePacket, packetLength);
                    return nextRow(fields, columnCount, isBinaryEncoded, resultSetConcurrency, useBufferRowIfPossible, useBufferRowExplicit, canReuseRowPacket, this.reusablePacket);
                } else if (packetLength > this.useBufferRowSizeThreshold) {
                    reuseAndReadPacket(this.reusablePacket, packetLength);
                    return nextRow(fields, columnCount, isBinaryEncoded, resultSetConcurrency, true, true, false, this.reusablePacket);
                } else {
                    int remaining2 = packetLength;
                    boolean firstTime = true;
                    byte[][] bArr2 = null;
                    byte[][] rowData = null;
                    int i7 = 0;
                    while (i7 < i3) {
                        int sw = this.mysqlInput.read() & i6;
                        remaining2--;
                        if (firstTime) {
                            if (sw == i6) {
                                Buffer errorPacket = new Buffer(packetLength + 4);
                                errorPacket.setPosition(i5);
                                errorPacket.writeByte(this.packetHeaderBuf[i5]);
                                errorPacket.writeByte(this.packetHeaderBuf[b]);
                                errorPacket.writeByte(this.packetHeaderBuf[c]);
                                errorPacket.writeByte(b);
                                errorPacket.writeByte((byte) sw);
                                readFully(this.mysqlInput, errorPacket.getByteBuffer(), 5, packetLength - 1);
                                errorPacket.setPosition(i4);
                                checkErrorPacket(errorPacket);
                            }
                            if (sw != 254 || packetLength >= 16777215) {
                                firstTime = false;
                                rowData = new byte[i3][];
                            } else if (!this.use41Extensions) {
                                return null;
                            } else {
                                if (isEOFDeprecated()) {
                                    int remaining3 = (remaining2 - skipLengthEncodedInteger(this.mysqlInput)) - skipLengthEncodedInteger(this.mysqlInput);
                                    this.oldServerStatus = this.serverStatus;
                                    this.serverStatus = (this.mysqlInput.read() & 255) | ((this.mysqlInput.read() & 255) << 8);
                                    checkTransactionState(this.oldServerStatus);
                                    int read = (this.mysqlInput.read() & 255) | ((255 & this.mysqlInput.read()) << 8);
                                    this.warningCount = read;
                                    remaining = (remaining3 - 2) - 2;
                                    if (read > 0) {
                                        this.hadWarnings = true;
                                    }
                                } else {
                                    int read2 = (this.mysqlInput.read() & 255) | ((this.mysqlInput.read() & 255) << 8);
                                    this.warningCount = read2;
                                    int remaining4 = remaining2 - 2;
                                    if (read2 > 0) {
                                        this.hadWarnings = true;
                                    }
                                    this.oldServerStatus = this.serverStatus;
                                    this.serverStatus = (this.mysqlInput.read() & 255) | ((255 & this.mysqlInput.read()) << 8);
                                    checkTransactionState(this.oldServerStatus);
                                    remaining = remaining4 - 2;
                                }
                                setServerSlowQueryFlags();
                                if (remaining <= 0) {
                                    return null;
                                }
                                skipFully(this.mysqlInput, (long) remaining);
                                return null;
                            }
                        } else {
                            byte b2 = b;
                        }
                        switch (sw) {
                            case 251:
                                i = 255;
                                len = -1;
                                break;
                            case MysqlDefs.FIELD_TYPE_BLOB:
                                i = 255;
                                len = (this.mysqlInput.read() & 255) | ((this.mysqlInput.read() & 255) << 8);
                                remaining2 -= 2;
                                break;
                            case 253:
                                len = (this.mysqlInput.read() & 255) | ((this.mysqlInput.read() & 255) << 8) | ((this.mysqlInput.read() & 255) << 16);
                                remaining2 -= 3;
                                i = 255;
                                break;
                            case 254:
                                len = (int) (((long) (this.mysqlInput.read() & 255)) | (((long) (this.mysqlInput.read() & 255)) << 8) | (((long) (this.mysqlInput.read() & 255)) << 16) | (((long) (this.mysqlInput.read() & 255)) << 24) | (((long) (this.mysqlInput.read() & 255)) << 32) | (((long) (this.mysqlInput.read() & 255)) << 40) | (((long) (this.mysqlInput.read() & 255)) << 48) | (((long) (this.mysqlInput.read() & 255)) << 56));
                                remaining2 -= 8;
                                i = 255;
                                break;
                            default:
                                i = 255;
                                len = sw;
                                break;
                        }
                        if (len == -1) {
                            rowData[i7] = null;
                        } else if (len == 0) {
                            rowData[i7] = Constants.EMPTY_BYTE_ARRAY;
                        } else {
                            rowData[i7] = new byte[len];
                            i2 = 0;
                            int bytesRead = readFully(this.mysqlInput, rowData[i7], 0, len);
                            if (bytesRead == len) {
                                remaining2 -= bytesRead;
                                i7++;
                                i5 = i2;
                                i6 = i;
                                i4 = 4;
                                b = 1;
                                c = 2;
                            } else {
                                int i8 = len;
                                int i9 = bytesRead;
                                int i10 = lengthRead;
                                throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException(Messages.getString("MysqlIO.43")), getExceptionInterceptor());
                            }
                        }
                        i2 = 0;
                        i7++;
                        i5 = i2;
                        i6 = i;
                        i4 = 4;
                        b = 1;
                        c = 2;
                    }
                    if (remaining2 > 0) {
                        skipFully(this.mysqlInput, (long) remaining2);
                    }
                    return new ByteArrayRow(rowData, getExceptionInterceptor());
                }
            } else {
                forceClose();
                throw new RuntimeException(Messages.getString("MysqlIO.43"));
            }
        } catch (IOException e) {
            throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, e, getExceptionInterceptor());
        }
    }

    /* access modifiers changed from: package-private */
    public final void quit() throws SQLException {
        try {
            if (!ExportControlled.isSSLEstablished(this.mysqlConnection) && !this.mysqlConnection.isClosed()) {
                try {
                    this.mysqlConnection.shutdownInput();
                } catch (UnsupportedOperationException e) {
                }
            }
        } catch (IOException e2) {
        }
        try {
            Buffer packet = new Buffer(6);
            this.packetSequence = -1;
            this.compressedPacketSequence = -1;
            packet.writeByte((byte) 1);
            send(packet, packet.getPosition());
        } finally {
            forceClose();
        }
    }

    /* access modifiers changed from: package-private */
    public Buffer getSharedSendPacket() {
        if (this.sharedSendPacket == null) {
            this.sharedSendPacket = new Buffer(1024);
        }
        return this.sharedSendPacket;
    }

    /* access modifiers changed from: package-private */
    public void closeStreamer(RowData streamer) throws SQLException {
        RowData rowData = this.streamingData;
        if (rowData == null) {
            throw SQLError.createSQLException(Messages.getString("MysqlIO.17") + streamer + Messages.getString("MysqlIO.18"), getExceptionInterceptor());
        } else if (streamer == rowData) {
            this.streamingData = null;
        } else {
            throw SQLError.createSQLException(Messages.getString("MysqlIO.19") + streamer + Messages.getString("MysqlIO.20") + Messages.getString("MysqlIO.21") + Messages.getString("MysqlIO.22"), getExceptionInterceptor());
        }
    }

    /* access modifiers changed from: package-private */
    public boolean tackOnMoreStreamingResults(ResultSetImpl addingTo) throws SQLException {
        if ((this.serverStatus & 8) == 0) {
            return false;
        }
        boolean firstTime = true;
        boolean moreRowSetsExist = true;
        ResultSetImpl currentResultSet = addingTo;
        while (true) {
            boolean z = true;
            if (!moreRowSetsExist || (!firstTime && currentResultSet.reallyResult())) {
                return true;
            }
            Buffer fieldPacket = checkErrorPacket();
            fieldPacket.setPosition(0);
            Statement owningStatement = addingTo.getStatement();
            Buffer buffer = fieldPacket;
            ResultSetImpl newResultSet = readResultsForQueryOrUpdate((StatementImpl) owningStatement, owningStatement.getMaxRows(), owningStatement.getResultSetType(), owningStatement.getResultSetConcurrency(), true, owningStatement.getConnection().getCatalog(), fieldPacket, addingTo.isBinaryEncoded, -1, (Field[]) null);
            currentResultSet.setNextResultSet(newResultSet);
            currentResultSet = newResultSet;
            if ((this.serverStatus & 8) == 0) {
                z = false;
            }
            moreRowSetsExist = z;
            if (!currentResultSet.reallyResult() && !moreRowSetsExist) {
                return false;
            }
            firstTime = false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public ResultSetImpl readAllResults(StatementImpl callingStatement, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Buffer resultPacket, boolean isBinaryEncoded, long preSentColumnCount, Field[] metadataFromCache) throws SQLException {
        resultPacket.setPosition(resultPacket.getPosition() - 1);
        ResultSetImpl topLevelResultSet = readResultsForQueryOrUpdate(callingStatement, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, resultPacket, isBinaryEncoded, preSentColumnCount, metadataFromCache);
        ResultSetImpl currentResultSet = topLevelResultSet;
        boolean z = false;
        boolean checkForMoreResults = (this.clientParam & 131072) != 0;
        boolean serverHasMoreResults = (this.serverStatus & 8) != 0;
        if (!serverHasMoreResults || !streamResults) {
            ResultSetImpl currentResultSet2 = currentResultSet;
            boolean moreRowSetsExist = checkForMoreResults & serverHasMoreResults;
            while (moreRowSetsExist) {
                Buffer fieldPacket = checkErrorPacket();
                fieldPacket.setPosition(z);
                Buffer buffer = fieldPacket;
                boolean z2 = z;
                ResultSetImpl newResultSet = readResultsForQueryOrUpdate(callingStatement, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, fieldPacket, isBinaryEncoded, preSentColumnCount, metadataFromCache);
                currentResultSet2.setNextResultSet(newResultSet);
                currentResultSet2 = newResultSet;
                moreRowSetsExist = (this.serverStatus & 8) != 0 ? true : z2;
                z = z2;
            }
            ResultSetImpl resultSetImpl = currentResultSet2;
            if (!streamResults) {
                clearInputStream();
            }
            reclaimLargeReusablePacket();
            return topLevelResultSet;
        }
        if (topLevelResultSet.getUpdateCount() != -1) {
            tackOnMoreStreamingResults(topLevelResultSet);
        }
        reclaimLargeReusablePacket();
        return topLevelResultSet;
    }

    /* access modifiers changed from: package-private */
    public void resetMaxBuf() {
        this.maxAllowedPacket = this.connection.getMaxAllowedPacket();
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0129, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x012a, code lost:
        preserveOldTransactionState();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0140, code lost:
        throw com.mysql.jdbc.SQLError.createCommunicationsException(r1.connection, r1.lastPacketSentTimeMs, r1.lastPacketReceivedTimeMs, r0, getExceptionInterceptor());
     */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0129 A[Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129, IOException -> 0x0129, SQLException -> 0x0123, all -> 0x0121 }, ExcHandler: IOException (r0v5 'e' java.io.IOException A[CUSTOM_DECLARE, Catch:{  }]), Splitter:B:9:0x0040] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final com.mysql.jdbc.Buffer sendCommand(int r22, java.lang.String r23, com.mysql.jdbc.Buffer r24, boolean r25, java.lang.String r26, int r27) throws java.sql.SQLException {
        /*
            r21 = this;
            r1 = r21
            r2 = r22
            r9 = r23
            r10 = r24
            r11 = r27
            int r0 = r1.commandCount
            r12 = 1
            int r0 = r0 + r12
            r1.commandCount = r0
            com.mysql.jdbc.MySQLConnection r0 = r1.connection
            boolean r0 = r0.getEnablePacketDebug()
            r1.enablePacketDebug = r0
            r0 = 0
            r1.readPacketSequence = r0
            r3 = 0
            if (r11 == 0) goto L_0x003f
            java.net.Socket r4 = r1.mysqlConnection     // Catch:{ SocketException -> 0x002c }
            int r4 = r4.getSoTimeout()     // Catch:{ SocketException -> 0x002c }
            r3 = r4
            java.net.Socket r4 = r1.mysqlConnection     // Catch:{ SocketException -> 0x002c }
            r4.setSoTimeout(r11)     // Catch:{ SocketException -> 0x002c }
            r13 = r3
            goto L_0x0040
        L_0x002c:
            r0 = move-exception
            r17 = r0
            com.mysql.jdbc.MySQLConnection r12 = r1.connection
            long r13 = r1.lastPacketSentTimeMs
            long r4 = r1.lastPacketReceivedTimeMs
            com.mysql.jdbc.ExceptionInterceptor r18 = r21.getExceptionInterceptor()
            r15 = r4
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createCommunicationsException(r12, r13, r15, r17, r18)
            throw r0
        L_0x003f:
            r13 = r3
        L_0x0040:
            r21.checkForOutstandingStreamingData()     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            int r3 = r1.serverStatus     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            r1.oldServerStatus = r3     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            r1.serverStatus = r0     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            r1.hadWarnings = r0     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            r1.warningCount = r0     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            r1.queryNoIndexUsed = r0     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            r1.queryBadIndexUsed = r0     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            r1.serverQueryWasSlow = r0     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            boolean r3 = r1.useCompression     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            if (r3 == 0) goto L_0x0065
            java.io.InputStream r3 = r1.mysqlInput     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            int r3 = r3.available()     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            if (r3 <= 0) goto L_0x0065
            java.io.InputStream r4 = r1.mysqlInput     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            long r5 = (long) r3     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            r4.skip(r5)     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
        L_0x0065:
            r21.clearInputStream()     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            r3 = -1
            if (r10 != 0) goto L_0x00c9
            r4 = 8
            if (r9 == 0) goto L_0x0074
            int r5 = r23.length()     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            goto L_0x0075
        L_0x0074:
            r5 = r0
        L_0x0075:
            int r4 = r4 + r5
            r5 = 2
            int r14 = r4 + 2
            com.mysql.jdbc.Buffer r4 = r1.sendPacket     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            if (r4 != 0) goto L_0x0084
            com.mysql.jdbc.Buffer r4 = new com.mysql.jdbc.Buffer     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            r4.<init>((int) r14)     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            r1.sendPacket = r4     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
        L_0x0084:
            r1.packetSequence = r3     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            r1.compressedPacketSequence = r3     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            r1.readPacketSequence = r0     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            r1.checkPacketSequence = r12     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            com.mysql.jdbc.Buffer r3 = r1.sendPacket     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            r3.clear()     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            com.mysql.jdbc.Buffer r3 = r1.sendPacket     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            byte r4 = (byte) r2     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            r3.writeByte(r4)     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            if (r2 == r5) goto L_0x00a0
            r3 = 3
            if (r2 == r3) goto L_0x00a0
            r3 = 22
            if (r2 != r3) goto L_0x00bf
        L_0x00a0:
            if (r26 != 0) goto L_0x00a8
            com.mysql.jdbc.Buffer r3 = r1.sendPacket     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            r3.writeStringNoNull(r9)     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            goto L_0x00bf
        L_0x00a8:
            com.mysql.jdbc.Buffer r3 = r1.sendPacket     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            com.mysql.jdbc.MySQLConnection r4 = r1.connection     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            java.lang.String r6 = r4.getServerCharset()     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            com.mysql.jdbc.MySQLConnection r4 = r1.connection     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            boolean r7 = r4.parserKnowsUnicode()     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            com.mysql.jdbc.MySQLConnection r8 = r1.connection     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            r4 = r23
            r5 = r26
            r3.writeStringNoNull(r4, r5, r6, r7, r8)     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
        L_0x00bf:
            com.mysql.jdbc.Buffer r3 = r1.sendPacket     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            int r4 = r3.getPosition()     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            r1.send(r3, r4)     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            goto L_0x00d4
        L_0x00c9:
            r1.packetSequence = r3     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            r1.compressedPacketSequence = r3     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            int r3 = r24.getPosition()     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
            r1.send(r10, r3)     // Catch:{ SQLException -> 0x011e, Exception -> 0x0109, IOException -> 0x0129 }
        L_0x00d4:
            r3 = 0
            if (r25 != 0) goto L_0x00e9
            r4 = 23
            if (r2 == r4) goto L_0x00e0
            r4 = 26
            if (r2 != r4) goto L_0x00e4
        L_0x00e0:
            r1.readPacketSequence = r0     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            r1.packetSequenceReset = r12     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
        L_0x00e4:
            com.mysql.jdbc.Buffer r0 = r21.checkErrorPacket((int) r22)     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            r3 = r0
        L_0x00e9:
            if (r11 == 0) goto L_0x0107
            java.net.Socket r0 = r1.mysqlConnection     // Catch:{ SocketException -> 0x00f2 }
            r0.setSoTimeout(r13)     // Catch:{ SocketException -> 0x00f2 }
            goto L_0x0107
        L_0x00f2:
            r0 = move-exception
            r19 = r0
            com.mysql.jdbc.MySQLConnection r14 = r1.connection
            long r3 = r1.lastPacketSentTimeMs
            long r5 = r1.lastPacketReceivedTimeMs
            com.mysql.jdbc.ExceptionInterceptor r20 = r21.getExceptionInterceptor()
            r15 = r3
            r17 = r5
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createCommunicationsException(r14, r15, r17, r19, r20)
            throw r0
        L_0x0107:
            r0 = r3
            return r3
        L_0x0109:
            r0 = move-exception
            r19 = r0
            com.mysql.jdbc.MySQLConnection r14 = r1.connection     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            long r3 = r1.lastPacketSentTimeMs     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            long r5 = r1.lastPacketReceivedTimeMs     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            com.mysql.jdbc.ExceptionInterceptor r20 = r21.getExceptionInterceptor()     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            r15 = r3
            r17 = r5
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createCommunicationsException(r14, r15, r17, r19, r20)     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
            throw r0     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
        L_0x011e:
            r0 = move-exception
            throw r0     // Catch:{ IOException -> 0x0129, SQLException -> 0x0123 }
        L_0x0121:
            r0 = move-exception
            goto L_0x0141
        L_0x0123:
            r0 = move-exception
            r21.preserveOldTransactionState()     // Catch:{ all -> 0x0121 }
            throw r0     // Catch:{ all -> 0x0121 }
        L_0x0129:
            r0 = move-exception
            r19 = r0
            r21.preserveOldTransactionState()     // Catch:{ all -> 0x0121 }
            com.mysql.jdbc.MySQLConnection r14 = r1.connection     // Catch:{ all -> 0x0121 }
            long r3 = r1.lastPacketSentTimeMs     // Catch:{ all -> 0x0121 }
            long r5 = r1.lastPacketReceivedTimeMs     // Catch:{ all -> 0x0121 }
            com.mysql.jdbc.ExceptionInterceptor r20 = r21.getExceptionInterceptor()     // Catch:{ all -> 0x0121 }
            r15 = r3
            r17 = r5
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createCommunicationsException(r14, r15, r17, r19, r20)     // Catch:{ all -> 0x0121 }
            throw r0     // Catch:{ all -> 0x0121 }
        L_0x0141:
            if (r11 == 0) goto L_0x015e
            java.net.Socket r3 = r1.mysqlConnection     // Catch:{ SocketException -> 0x0149 }
            r3.setSoTimeout(r13)     // Catch:{ SocketException -> 0x0149 }
            goto L_0x015e
        L_0x0149:
            r0 = move-exception
            r19 = r0
            com.mysql.jdbc.MySQLConnection r14 = r1.connection
            long r3 = r1.lastPacketSentTimeMs
            long r5 = r1.lastPacketReceivedTimeMs
            com.mysql.jdbc.ExceptionInterceptor r20 = r21.getExceptionInterceptor()
            r15 = r3
            r17 = r5
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createCommunicationsException(r14, r15, r17, r19, r20)
            throw r0
        L_0x015e:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.MysqlIO.sendCommand(int, java.lang.String, com.mysql.jdbc.Buffer, boolean, java.lang.String, int):com.mysql.jdbc.Buffer");
    }

    /* access modifiers changed from: protected */
    public boolean shouldIntercept() {
        return this.statementInterceptors != null;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x028d A[Catch:{ SQLException -> 0x045f, all -> 0x0459 }] */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x0330 A[Catch:{ SQLException -> 0x0457 }] */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0339 A[Catch:{ SQLException -> 0x0457 }] */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x03e8 A[Catch:{ SQLException -> 0x0457 }] */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x0428 A[Catch:{ SQLException -> 0x0457 }] */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x042f A[Catch:{ SQLException -> 0x0457 }] */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x043e  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x047b A[Catch:{ all -> 0x04ae }] */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x0489 A[Catch:{ all -> 0x04ae }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final com.mysql.jdbc.ResultSetInternalMethods sqlQueryDirect(com.mysql.jdbc.StatementImpl r40, java.lang.String r41, java.lang.String r42, com.mysql.jdbc.Buffer r43, int r44, int r45, int r46, boolean r47, java.lang.String r48, com.mysql.jdbc.Field[] r49) throws java.lang.Exception {
        /*
            r39 = this;
            r13 = r39
            r15 = r40
            r14 = r41
            int r0 = r13.statementExecutionDepth
            r28 = 1
            int r0 = r0 + 1
            r13.statementExecutionDepth = r0
            java.util.List<com.mysql.jdbc.StatementInterceptorV2> r0 = r13.statementInterceptors     // Catch:{ SQLException -> 0x0472, all -> 0x046d }
            r12 = 0
            if (r0 == 0) goto L_0x002e
            com.mysql.jdbc.ResultSetInternalMethods r0 = r13.invokeStatementInterceptorsPre(r14, r15, r12)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            if (r0 == 0) goto L_0x002e
            int r1 = r13.statementExecutionDepth
            int r1 = r1 + -1
            r13.statementExecutionDepth = r1
            r1 = r0
            return r0
        L_0x0022:
            r0 = move-exception
            r10 = r43
            r7 = r15
            goto L_0x04af
        L_0x0028:
            r0 = move-exception
            r10 = r43
            r7 = r15
            goto L_0x0476
        L_0x002e:
            com.mysql.jdbc.MySQLConnection r0 = r13.connection     // Catch:{ SQLException -> 0x0472, all -> 0x046d }
            java.lang.String r0 = r0.getStatementComment()     // Catch:{ SQLException -> 0x0472, all -> 0x046d }
            com.mysql.jdbc.MySQLConnection r1 = r13.connection     // Catch:{ SQLException -> 0x0472, all -> 0x046d }
            boolean r1 = r1.getIncludeThreadNamesAsStatementComment()     // Catch:{ SQLException -> 0x0472, all -> 0x046d }
            if (r1 == 0) goto L_0x0074
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            r1.<init>()     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            if (r0 == 0) goto L_0x0057
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            r2.<init>()     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            java.lang.StringBuilder r2 = r2.append(r0)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            java.lang.String r3 = ", "
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            java.lang.String r2 = r2.toString()     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            goto L_0x0059
        L_0x0057:
            java.lang.String r2 = ""
        L_0x0059:
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            java.lang.String r2 = "java thread: "
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            java.lang.Thread r2 = java.lang.Thread.currentThread()     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            java.lang.String r2 = r2.getName()     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            java.lang.String r1 = r1.toString()     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            r0 = r1
        L_0x0074:
            r16 = 2
            r10 = 3
            r11 = 5
            if (r14 == 0) goto L_0x011c
            int r1 = r41.length()     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            int r1 = r1 * r10
            int r1 = r1 + r11
            int r7 = r1 + 2
            r8 = 0
            if (r0 == 0) goto L_0x00a2
            r2 = 0
            com.mysql.jdbc.MySQLConnection r1 = r13.connection     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            java.lang.String r4 = r1.getServerCharset()     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            com.mysql.jdbc.MySQLConnection r1 = r13.connection     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            boolean r5 = r1.parserKnowsUnicode()     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            com.mysql.jdbc.ExceptionInterceptor r6 = r39.getExceptionInterceptor()     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            r1 = r0
            r3 = r42
            byte[] r1 = com.mysql.jdbc.StringUtils.getBytes((java.lang.String) r1, (com.mysql.jdbc.SingleByteCharsetConverter) r2, (java.lang.String) r3, (java.lang.String) r4, (boolean) r5, (com.mysql.jdbc.ExceptionInterceptor) r6)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            r8 = r1
            int r1 = r8.length     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            int r7 = r7 + r1
            int r7 = r7 + 6
        L_0x00a2:
            com.mysql.jdbc.Buffer r1 = r13.sendPacket     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            if (r1 != 0) goto L_0x00ae
            com.mysql.jdbc.Buffer r1 = new com.mysql.jdbc.Buffer     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            r1.<init>((int) r7)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            r13.sendPacket = r1     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            goto L_0x00b1
        L_0x00ae:
            r1.clear()     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
        L_0x00b1:
            com.mysql.jdbc.Buffer r1 = r13.sendPacket     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            r1.writeByte(r10)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            if (r8 == 0) goto L_0x00cb
            com.mysql.jdbc.Buffer r1 = r13.sendPacket     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            byte[] r2 = com.mysql.jdbc.Constants.SLASH_STAR_SPACE_AS_BYTES     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            r1.writeBytesNoNull(r2)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            com.mysql.jdbc.Buffer r1 = r13.sendPacket     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            r1.writeBytesNoNull(r8)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            com.mysql.jdbc.Buffer r1 = r13.sendPacket     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            byte[] r2 = com.mysql.jdbc.Constants.SPACE_STAR_SLASH_SPACE_AS_BYTES     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            r1.writeBytesNoNull(r2)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
        L_0x00cb:
            if (r42 == 0) goto L_0x0113
            boolean r1 = r13.platformDbCharsetMatches     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            if (r1 == 0) goto L_0x00e9
            com.mysql.jdbc.Buffer r1 = r13.sendPacket     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            com.mysql.jdbc.MySQLConnection r2 = r13.connection     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            java.lang.String r4 = r2.getServerCharset()     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            com.mysql.jdbc.MySQLConnection r2 = r13.connection     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            boolean r5 = r2.parserKnowsUnicode()     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            com.mysql.jdbc.MySQLConnection r6 = r13.connection     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            r2 = r41
            r3 = r42
            r1.writeStringNoNull(r2, r3, r4, r5, r6)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            goto L_0x0118
        L_0x00e9:
            java.lang.String r1 = "LOAD DATA"
            boolean r1 = com.mysql.jdbc.StringUtils.startsWithIgnoreCaseAndWs((java.lang.String) r14, (java.lang.String) r1)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            if (r1 == 0) goto L_0x00fb
            com.mysql.jdbc.Buffer r1 = r13.sendPacket     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            byte[] r2 = com.mysql.jdbc.StringUtils.getBytes((java.lang.String) r41)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            r1.writeBytesNoNull(r2)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            goto L_0x0118
        L_0x00fb:
            com.mysql.jdbc.Buffer r1 = r13.sendPacket     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            com.mysql.jdbc.MySQLConnection r2 = r13.connection     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            java.lang.String r4 = r2.getServerCharset()     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            com.mysql.jdbc.MySQLConnection r2 = r13.connection     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            boolean r5 = r2.parserKnowsUnicode()     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            com.mysql.jdbc.MySQLConnection r6 = r13.connection     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            r2 = r41
            r3 = r42
            r1.writeStringNoNull(r2, r3, r4, r5, r6)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            goto L_0x0118
        L_0x0113:
            com.mysql.jdbc.Buffer r1 = r13.sendPacket     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            r1.writeStringNoNull(r14)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
        L_0x0118:
            com.mysql.jdbc.Buffer r1 = r13.sendPacket     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            r9 = r1
            goto L_0x011e
        L_0x011c:
            r9 = r43
        L_0x011e:
            r1 = 0
            r2 = 0
            r3 = 0
            boolean r5 = r13.needToGrabQueryFromPacket     // Catch:{ SQLException -> 0x0469, all -> 0x0465 }
            if (r5 == 0) goto L_0x0144
            byte[] r5 = r9.getByteBuffer()     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            r1 = r5
            int r5 = r9.getPosition()     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            r2 = r5
            long r5 = r39.getCurrentTimeNanosOrMillis()     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            r3 = r5
            r8 = r1
            r7 = r2
            r29 = r3
            goto L_0x0148
        L_0x013a:
            r0 = move-exception
            r10 = r9
            r7 = r15
            goto L_0x04af
        L_0x013f:
            r0 = move-exception
            r10 = r9
            r7 = r15
            goto L_0x0476
        L_0x0144:
            r8 = r1
            r7 = r2
            r29 = r3
        L_0x0148:
            boolean r1 = r13.autoGenerateTestcaseScript     // Catch:{ SQLException -> 0x0469, all -> 0x0465 }
            if (r1 == 0) goto L_0x019b
            r1 = 0
            if (r14 == 0) goto L_0x0173
            if (r0 == 0) goto L_0x0170
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            r2.<init>()     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            java.lang.String r3 = "/* "
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            java.lang.StringBuilder r2 = r2.append(r0)     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            java.lang.String r3 = " */ "
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            java.lang.StringBuilder r2 = r2.append(r14)     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            java.lang.String r2 = r2.toString()     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            r1 = r2
            goto L_0x017a
        L_0x0170:
            r1 = r41
            goto L_0x017a
        L_0x0173:
            int r2 = r7 + -5
            java.lang.String r2 = com.mysql.jdbc.StringUtils.toString(r8, r11, r2)     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            r1 = r2
        L_0x017a:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            int r3 = r1.length()     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            int r3 = r3 + 32
            r2.<init>(r3)     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            com.mysql.jdbc.MySQLConnection r3 = r13.connection     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            r3.generateConnectionCommentBlock(r2)     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            r2.append(r1)     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            r3 = 59
            r2.append(r3)     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            com.mysql.jdbc.MySQLConnection r3 = r13.connection     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            java.lang.String r4 = r2.toString()     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            r3.dumpTestcaseQuery(r4)     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
        L_0x019b:
            r2 = 3
            r3 = 0
            r5 = 0
            r6 = 0
            r17 = 0
            r1 = r39
            r4 = r9
            r43 = r7
            r7 = r17
            com.mysql.jdbc.Buffer r1 = r1.sendCommand(r2, r3, r4, r5, r6, r7)     // Catch:{ SQLException -> 0x0469, all -> 0x0465 }
            r7 = r8
            r8 = r1
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            boolean r10 = r13.profileSql     // Catch:{ SQLException -> 0x0469, all -> 0x0465 }
            if (r10 != 0) goto L_0x01ca
            boolean r10 = r13.logSlowQueries     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            if (r10 == 0) goto L_0x01bd
            goto L_0x01ca
        L_0x01bd:
            r12 = r43
            r31 = r1
            r10 = r3
            r33 = r4
            r34 = r5
            r43 = r9
            goto L_0x025e
        L_0x01ca:
            long r18 = r39.getCurrentTimeNanosOrMillis()     // Catch:{ SQLException -> 0x0469, all -> 0x0465 }
            r5 = r18
            r10 = 0
            boolean r12 = r13.profileSql     // Catch:{ SQLException -> 0x0469, all -> 0x0465 }
            if (r12 == 0) goto L_0x01d9
            r10 = 1
            r20 = r1
            goto L_0x0203
        L_0x01d9:
            boolean r12 = r13.logSlowQueries     // Catch:{ SQLException -> 0x0469, all -> 0x0465 }
            if (r12 == 0) goto L_0x0201
            long r11 = r5 - r29
            r20 = r1
            boolean r1 = r13.useAutoSlowLog     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            if (r1 == 0) goto L_0x01ec
            com.mysql.jdbc.MySQLConnection r1 = r13.connection     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            boolean r1 = r1.isAbonormallyLongQuery(r11)     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            goto L_0x01fb
        L_0x01ec:
            com.mysql.jdbc.MySQLConnection r1 = r13.connection     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            int r1 = r1.getSlowQueryThresholdMillis()     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            long r1 = (long) r1
            int r1 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1))
            if (r1 <= 0) goto L_0x01fa
            r1 = r28
            goto L_0x01fb
        L_0x01fa:
            r1 = 0
        L_0x01fb:
            if (r1 == 0) goto L_0x0203
            r10 = 1
            r2 = 1
            r4 = r2
            goto L_0x0203
        L_0x0201:
            r20 = r1
        L_0x0203:
            if (r10 == 0) goto L_0x0252
            r1 = 0
            r2 = r43
            com.mysql.jdbc.MySQLConnection r11 = r13.connection     // Catch:{ SQLException -> 0x024a, all -> 0x0242 }
            int r11 = r11.getMaxQuerySizeToLog()     // Catch:{ SQLException -> 0x024a, all -> 0x0242 }
            r12 = r43
            if (r12 <= r11) goto L_0x021d
            com.mysql.jdbc.MySQLConnection r11 = r13.connection     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            int r11 = r11.getMaxQuerySizeToLog()     // Catch:{ SQLException -> 0x013f, all -> 0x013a }
            r19 = 5
            int r2 = r11 + 5
            r1 = 1
        L_0x021d:
            int r11 = r2 + -5
            r43 = r9
            r9 = 5
            java.lang.String r11 = com.mysql.jdbc.StringUtils.toString(r7, r9, r11)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            r3 = r11
            if (r1 == 0) goto L_0x0256
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            r11.<init>()     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            java.lang.StringBuilder r11 = r11.append(r3)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            java.lang.String r19 = "MysqlIO.25"
            java.lang.String r9 = com.mysql.jdbc.Messages.getString(r19)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            java.lang.StringBuilder r9 = r11.append(r9)     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            java.lang.String r9 = r9.toString()     // Catch:{ SQLException -> 0x0028, all -> 0x0022 }
            r3 = r9
            goto L_0x0256
        L_0x0242:
            r0 = move-exception
            r43 = r9
            r10 = r43
            r7 = r15
            goto L_0x04af
        L_0x024a:
            r0 = move-exception
            r43 = r9
            r10 = r43
            r7 = r15
            goto L_0x0476
        L_0x0252:
            r12 = r43
            r43 = r9
        L_0x0256:
            r1 = r5
            r31 = r1
            r10 = r3
            r33 = r4
            r34 = r5
        L_0x025e:
            r9 = 0
            r19 = -1
            r1 = r39
            r2 = r40
            r3 = r44
            r4 = r45
            r5 = r46
            r6 = r47
            r36 = r7
            r7 = r48
            r11 = r43
            r21 = 5
            r15 = r10
            r37 = r11
            r17 = 3
            r10 = r19
            r38 = r0
            r0 = r12
            r19 = 0
            r12 = r49
            com.mysql.jdbc.ResultSetImpl r18 = r1.readAllResults(r2, r3, r4, r5, r6, r7, r8, r9, r10, r12)     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
            if (r33 == 0) goto L_0x0330
            boolean r1 = r13.serverQueryWasSlow     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
            if (r1 != 0) goto L_0x0330
            com.mysql.jdbc.MySQLConnection r1 = r13.connection     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
            com.mysql.jdbc.profiler.ProfilerEventHandler r1 = r1.getProfilerEventHandlerInstance()     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
            r2 = 6
            com.mysql.jdbc.MySQLConnection r3 = r13.connection     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
            long r4 = r34 - r29
            int r4 = (int) r4     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
            long r4 = (long) r4     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
            java.lang.Throwable r21 = new java.lang.Throwable     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
            r21.<init>()     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
            java.lang.String r6 = "Protocol.SlowQuery"
            r7 = 4
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
            boolean r9 = r13.useAutoSlowLog     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
            if (r9 == 0) goto L_0x02b9
            java.lang.String r9 = " 95% of all queries "
            goto L_0x02bf
        L_0x02ab:
            r0 = move-exception
            r7 = r40
            r10 = r37
            goto L_0x04af
        L_0x02b2:
            r0 = move-exception
            r7 = r40
            r10 = r37
            goto L_0x0476
        L_0x02b9:
            long r9 = r13.slowQueryThreshold     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
            java.lang.String r9 = java.lang.String.valueOf(r9)     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
        L_0x02bf:
            r7[r19] = r9     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
            java.lang.String r9 = r13.queryTimingUnits     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
            r7[r28] = r9     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
            long r9 = r34 - r29
            java.lang.Long r9 = java.lang.Long.valueOf(r9)     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
            r7[r16] = r9     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
            r7[r17] = r15     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
            java.lang.String r22 = com.mysql.jdbc.Messages.getString(r6, r7)     // Catch:{ SQLException -> 0x045f, all -> 0x0459 }
            r14 = r1
            r7 = r40
            r9 = r15
            r15 = r2
            r16 = r3
            r17 = r40
            r19 = r4
            r14.processEvent(r15, r16, r17, r18, r19, r21, r22)     // Catch:{ SQLException -> 0x032d, all -> 0x032a }
            com.mysql.jdbc.MySQLConnection r1 = r13.connection     // Catch:{ SQLException -> 0x032d, all -> 0x032a }
            boolean r1 = r1.getExplainSlowQueries()     // Catch:{ SQLException -> 0x032d, all -> 0x032a }
            if (r1 == 0) goto L_0x0327
            r1 = 1048576(0x100000, float:1.469368E-39)
            if (r0 >= r1) goto L_0x02fa
            int r1 = r0 + -5
            r10 = r37
            r2 = 5
            byte[] r1 = r10.getBytes(r2, r1)     // Catch:{ SQLException -> 0x0457 }
            r13.explainSlowQuery(r1, r9)     // Catch:{ SQLException -> 0x0457 }
            goto L_0x0335
        L_0x02fa:
            r10 = r37
            com.mysql.jdbc.MySQLConnection r2 = r13.connection     // Catch:{ SQLException -> 0x0457 }
            com.mysql.jdbc.log.Log r2 = r2.getLog()     // Catch:{ SQLException -> 0x0457 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ SQLException -> 0x0457 }
            r3.<init>()     // Catch:{ SQLException -> 0x0457 }
            java.lang.String r4 = "MysqlIO.28"
            java.lang.String r4 = com.mysql.jdbc.Messages.getString(r4)     // Catch:{ SQLException -> 0x0457 }
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ SQLException -> 0x0457 }
            java.lang.StringBuilder r1 = r3.append(r1)     // Catch:{ SQLException -> 0x0457 }
            java.lang.String r3 = "MysqlIO.29"
            java.lang.String r3 = com.mysql.jdbc.Messages.getString(r3)     // Catch:{ SQLException -> 0x0457 }
            java.lang.StringBuilder r1 = r1.append(r3)     // Catch:{ SQLException -> 0x0457 }
            java.lang.String r1 = r1.toString()     // Catch:{ SQLException -> 0x0457 }
            r2.logWarn(r1)     // Catch:{ SQLException -> 0x0457 }
            goto L_0x0335
        L_0x0327:
            r10 = r37
            goto L_0x0335
        L_0x032a:
            r0 = move-exception
            goto L_0x045c
        L_0x032d:
            r0 = move-exception
            goto L_0x0462
        L_0x0330:
            r7 = r40
            r9 = r15
            r10 = r37
        L_0x0335:
            boolean r1 = r13.logSlowQueries     // Catch:{ SQLException -> 0x0457 }
            if (r1 == 0) goto L_0x03e4
            boolean r1 = r13.queryBadIndexUsed     // Catch:{ SQLException -> 0x0457 }
            if (r1 == 0) goto L_0x0372
            boolean r1 = r13.profileSql     // Catch:{ SQLException -> 0x0457 }
            if (r1 == 0) goto L_0x0372
            com.mysql.jdbc.MySQLConnection r1 = r13.connection     // Catch:{ SQLException -> 0x0457 }
            com.mysql.jdbc.profiler.ProfilerEventHandler r19 = r1.getProfilerEventHandlerInstance()     // Catch:{ SQLException -> 0x0457 }
            r20 = 6
            com.mysql.jdbc.MySQLConnection r1 = r13.connection     // Catch:{ SQLException -> 0x0457 }
            long r24 = r34 - r29
            java.lang.Throwable r26 = new java.lang.Throwable     // Catch:{ SQLException -> 0x0457 }
            r26.<init>()     // Catch:{ SQLException -> 0x0457 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ SQLException -> 0x0457 }
            r2.<init>()     // Catch:{ SQLException -> 0x0457 }
            java.lang.String r3 = "MysqlIO.33"
            java.lang.String r3 = com.mysql.jdbc.Messages.getString(r3)     // Catch:{ SQLException -> 0x0457 }
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ SQLException -> 0x0457 }
            java.lang.StringBuilder r2 = r2.append(r9)     // Catch:{ SQLException -> 0x0457 }
            java.lang.String r27 = r2.toString()     // Catch:{ SQLException -> 0x0457 }
            r21 = r1
            r22 = r40
            r23 = r18
            r19.processEvent(r20, r21, r22, r23, r24, r26, r27)     // Catch:{ SQLException -> 0x0457 }
        L_0x0372:
            boolean r1 = r13.queryNoIndexUsed     // Catch:{ SQLException -> 0x0457 }
            if (r1 == 0) goto L_0x03ab
            boolean r1 = r13.profileSql     // Catch:{ SQLException -> 0x0457 }
            if (r1 == 0) goto L_0x03ab
            com.mysql.jdbc.MySQLConnection r1 = r13.connection     // Catch:{ SQLException -> 0x0457 }
            com.mysql.jdbc.profiler.ProfilerEventHandler r19 = r1.getProfilerEventHandlerInstance()     // Catch:{ SQLException -> 0x0457 }
            r20 = 6
            com.mysql.jdbc.MySQLConnection r1 = r13.connection     // Catch:{ SQLException -> 0x0457 }
            long r24 = r34 - r29
            java.lang.Throwable r26 = new java.lang.Throwable     // Catch:{ SQLException -> 0x0457 }
            r26.<init>()     // Catch:{ SQLException -> 0x0457 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ SQLException -> 0x0457 }
            r2.<init>()     // Catch:{ SQLException -> 0x0457 }
            java.lang.String r3 = "MysqlIO.35"
            java.lang.String r3 = com.mysql.jdbc.Messages.getString(r3)     // Catch:{ SQLException -> 0x0457 }
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ SQLException -> 0x0457 }
            java.lang.StringBuilder r2 = r2.append(r9)     // Catch:{ SQLException -> 0x0457 }
            java.lang.String r27 = r2.toString()     // Catch:{ SQLException -> 0x0457 }
            r21 = r1
            r22 = r40
            r23 = r18
            r19.processEvent(r20, r21, r22, r23, r24, r26, r27)     // Catch:{ SQLException -> 0x0457 }
        L_0x03ab:
            boolean r1 = r13.serverQueryWasSlow     // Catch:{ SQLException -> 0x0457 }
            if (r1 == 0) goto L_0x03e4
            boolean r1 = r13.profileSql     // Catch:{ SQLException -> 0x0457 }
            if (r1 == 0) goto L_0x03e4
            com.mysql.jdbc.MySQLConnection r1 = r13.connection     // Catch:{ SQLException -> 0x0457 }
            com.mysql.jdbc.profiler.ProfilerEventHandler r19 = r1.getProfilerEventHandlerInstance()     // Catch:{ SQLException -> 0x0457 }
            r20 = 6
            com.mysql.jdbc.MySQLConnection r1 = r13.connection     // Catch:{ SQLException -> 0x0457 }
            long r24 = r34 - r29
            java.lang.Throwable r26 = new java.lang.Throwable     // Catch:{ SQLException -> 0x0457 }
            r26.<init>()     // Catch:{ SQLException -> 0x0457 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ SQLException -> 0x0457 }
            r2.<init>()     // Catch:{ SQLException -> 0x0457 }
            java.lang.String r3 = "MysqlIO.ServerSlowQuery"
            java.lang.String r3 = com.mysql.jdbc.Messages.getString(r3)     // Catch:{ SQLException -> 0x0457 }
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ SQLException -> 0x0457 }
            java.lang.StringBuilder r2 = r2.append(r9)     // Catch:{ SQLException -> 0x0457 }
            java.lang.String r27 = r2.toString()     // Catch:{ SQLException -> 0x0457 }
            r21 = r1
            r22 = r40
            r23 = r18
            r19.processEvent(r20, r21, r22, r23, r24, r26, r27)     // Catch:{ SQLException -> 0x0457 }
        L_0x03e4:
            boolean r1 = r13.profileSql     // Catch:{ SQLException -> 0x0457 }
            if (r1 == 0) goto L_0x0424
            com.mysql.jdbc.MySQLConnection r1 = r13.connection     // Catch:{ SQLException -> 0x0457 }
            com.mysql.jdbc.profiler.ProfilerEventHandler r19 = r1.getProfilerEventHandlerInstance()     // Catch:{ SQLException -> 0x0457 }
            r20 = 3
            com.mysql.jdbc.MySQLConnection r1 = r13.connection     // Catch:{ SQLException -> 0x0457 }
            long r24 = r34 - r29
            java.lang.Throwable r26 = new java.lang.Throwable     // Catch:{ SQLException -> 0x0457 }
            r26.<init>()     // Catch:{ SQLException -> 0x0457 }
            r21 = r1
            r22 = r40
            r23 = r18
            r27 = r9
            r19.processEvent(r20, r21, r22, r23, r24, r26, r27)     // Catch:{ SQLException -> 0x0457 }
            com.mysql.jdbc.MySQLConnection r1 = r13.connection     // Catch:{ SQLException -> 0x0457 }
            com.mysql.jdbc.profiler.ProfilerEventHandler r19 = r1.getProfilerEventHandlerInstance()     // Catch:{ SQLException -> 0x0457 }
            r20 = 5
            com.mysql.jdbc.MySQLConnection r1 = r13.connection     // Catch:{ SQLException -> 0x0457 }
            long r2 = r39.getCurrentTimeNanosOrMillis()     // Catch:{ SQLException -> 0x0457 }
            long r24 = r2 - r31
            java.lang.Throwable r26 = new java.lang.Throwable     // Catch:{ SQLException -> 0x0457 }
            r26.<init>()     // Catch:{ SQLException -> 0x0457 }
            r27 = 0
            r21 = r1
            r22 = r40
            r23 = r18
            r19.processEvent(r20, r21, r22, r23, r24, r26, r27)     // Catch:{ SQLException -> 0x0457 }
        L_0x0424:
            boolean r1 = r13.hadWarnings     // Catch:{ SQLException -> 0x0457 }
            if (r1 == 0) goto L_0x042b
            r39.scanForAndThrowDataTruncation()     // Catch:{ SQLException -> 0x0457 }
        L_0x042b:
            java.util.List<com.mysql.jdbc.StatementInterceptorV2> r1 = r13.statementInterceptors     // Catch:{ SQLException -> 0x0457 }
            if (r1 == 0) goto L_0x043e
            r5 = 0
            r6 = 0
            r1 = r39
            r2 = r41
            r3 = r40
            r4 = r18
            com.mysql.jdbc.ResultSetInternalMethods r1 = r1.invokeStatementInterceptorsPost(r2, r3, r4, r5, r6)     // Catch:{ SQLException -> 0x0457 }
            goto L_0x0440
        L_0x043e:
            r1 = r18
        L_0x0440:
            int r2 = r13.statementExecutionDepth
            int r2 = r2 + -1
            r13.statementExecutionDepth = r2
            r2 = r29
            r4 = r36
            r5 = r38
            r6 = r8
            r11 = r31
            r8 = r9
            r9 = r1
            r14 = r34
            r16 = r33
            return r1
        L_0x0457:
            r0 = move-exception
            goto L_0x0476
        L_0x0459:
            r0 = move-exception
            r7 = r40
        L_0x045c:
            r10 = r37
            goto L_0x04af
        L_0x045f:
            r0 = move-exception
            r7 = r40
        L_0x0462:
            r10 = r37
            goto L_0x0476
        L_0x0465:
            r0 = move-exception
            r10 = r9
            r7 = r15
            goto L_0x04af
        L_0x0469:
            r0 = move-exception
            r10 = r9
            r7 = r15
            goto L_0x0476
        L_0x046d:
            r0 = move-exception
            r7 = r15
            r10 = r43
            goto L_0x04af
        L_0x0472:
            r0 = move-exception
            r7 = r15
            r10 = r43
        L_0x0476:
            r8 = r0
            java.util.List<com.mysql.jdbc.StatementInterceptorV2> r0 = r13.statementInterceptors     // Catch:{ all -> 0x04ae }
            if (r0 == 0) goto L_0x0487
            r4 = 0
            r5 = 0
            r1 = r39
            r2 = r41
            r3 = r40
            r6 = r8
            r1.invokeStatementInterceptorsPost(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x04ae }
        L_0x0487:
            if (r7 == 0) goto L_0x04ac
            java.lang.Object r1 = r7.cancelTimeoutMutex     // Catch:{ all -> 0x04ae }
            monitor-enter(r1)     // Catch:{ all -> 0x04ae }
            boolean r0 = r7.wasCancelled     // Catch:{ all -> 0x04a9 }
            if (r0 == 0) goto L_0x04a7
            r0 = 0
            boolean r2 = r7.wasCancelledByTimeout     // Catch:{ all -> 0x04a9 }
            if (r2 == 0) goto L_0x049c
            com.mysql.jdbc.exceptions.MySQLTimeoutException r2 = new com.mysql.jdbc.exceptions.MySQLTimeoutException     // Catch:{ all -> 0x04a9 }
            r2.<init>()     // Catch:{ all -> 0x04a9 }
            r0 = r2
            goto L_0x04a2
        L_0x049c:
            com.mysql.jdbc.exceptions.MySQLStatementCancelledException r2 = new com.mysql.jdbc.exceptions.MySQLStatementCancelledException     // Catch:{ all -> 0x04a9 }
            r2.<init>()     // Catch:{ all -> 0x04a9 }
            r0 = r2
        L_0x04a2:
            r40.resetCancelledState()     // Catch:{ all -> 0x04a9 }
            throw r0     // Catch:{ all -> 0x04a9 }
        L_0x04a7:
            monitor-exit(r1)     // Catch:{ all -> 0x04a9 }
            goto L_0x04ac
        L_0x04a9:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x04a9 }
            throw r0     // Catch:{ all -> 0x04ae }
        L_0x04ac:
            throw r8     // Catch:{ all -> 0x04ae }
        L_0x04ae:
            r0 = move-exception
        L_0x04af:
            int r1 = r13.statementExecutionDepth
            int r1 = r1 + -1
            r13.statementExecutionDepth = r1
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.MysqlIO.sqlQueryDirect(com.mysql.jdbc.StatementImpl, java.lang.String, java.lang.String, com.mysql.jdbc.Buffer, int, int, int, boolean, java.lang.String, com.mysql.jdbc.Field[]):com.mysql.jdbc.ResultSetInternalMethods");
    }

    /* access modifiers changed from: package-private */
    public ResultSetInternalMethods invokeStatementInterceptorsPre(String sql, Statement interceptedStatement, boolean forceExecute) throws SQLException {
        ResultSetInternalMethods interceptedResultSet;
        ResultSetInternalMethods previousResultSet = null;
        int s = this.statementInterceptors.size();
        for (int i = 0; i < s; i++) {
            StatementInterceptorV2 interceptor = this.statementInterceptors.get(i);
            boolean executeTopLevelOnly = interceptor.executeTopLevelOnly();
            boolean shouldExecute = true;
            if ((!executeTopLevelOnly || (this.statementExecutionDepth != 1 && !forceExecute)) && executeTopLevelOnly) {
                shouldExecute = false;
            }
            if (shouldExecute && (interceptedResultSet = interceptor.preProcess(sql, interceptedStatement, this.connection)) != null) {
                previousResultSet = interceptedResultSet;
            }
        }
        return previousResultSet;
    }

    /* access modifiers changed from: package-private */
    public ResultSetInternalMethods invokeStatementInterceptorsPost(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, boolean forceExecute, SQLException statementException) throws SQLException {
        ResultSetInternalMethods interceptedResultSet;
        int s = this.statementInterceptors.size();
        ResultSetInternalMethods originalResultSet2 = originalResultSet;
        for (int i = 0; i < s; i++) {
            StatementInterceptorV2 interceptor = this.statementInterceptors.get(i);
            boolean executeTopLevelOnly = interceptor.executeTopLevelOnly();
            boolean shouldExecute = true;
            if ((!executeTopLevelOnly || (this.statementExecutionDepth != 1 && !forceExecute)) && executeTopLevelOnly) {
                shouldExecute = false;
            }
            if (shouldExecute && (interceptedResultSet = interceptor.postProcess(sql, interceptedStatement, originalResultSet2, this.connection, this.warningCount, this.queryNoIndexUsed, this.queryBadIndexUsed, statementException)) != null) {
                originalResultSet2 = interceptedResultSet;
            }
        }
        return originalResultSet2;
    }

    private void calculateSlowQueryThreshold() {
        this.slowQueryThreshold = (long) this.connection.getSlowQueryThresholdMillis();
        if (this.connection.getUseNanosForElapsedTime()) {
            long nanosThreshold = this.connection.getSlowQueryThresholdNanos();
            if (nanosThreshold != 0) {
                this.slowQueryThreshold = nanosThreshold;
            } else {
                this.slowQueryThreshold *= 1000000;
            }
        }
    }

    /* access modifiers changed from: protected */
    public long getCurrentTimeNanosOrMillis() {
        return this.useNanosForElapsedTime ? TimeUtil.getCurrentTimeNanosOrMillis() : System.currentTimeMillis();
    }

    /* access modifiers changed from: package-private */
    public String getHost() {
        return this.host;
    }

    /* access modifiers changed from: package-private */
    public boolean isVersion(int major, int minor, int subminor) {
        return major == getServerMajorVersion() && minor == getServerMinorVersion() && subminor == getServerSubMinorVersion();
    }

    /* access modifiers changed from: package-private */
    public boolean versionMeetsMinimum(int major, int minor, int subminor) {
        if (getServerMajorVersion() < major) {
            return false;
        }
        if (getServerMajorVersion() != major) {
            return true;
        }
        if (getServerMinorVersion() < minor) {
            return false;
        }
        if (getServerMinorVersion() != minor) {
            return true;
        }
        if (getServerSubMinorVersion() >= subminor) {
            return true;
        }
        return false;
    }

    private static final String getPacketDumpToLog(Buffer packetToDump, int packetLength) {
        if (packetLength < 1024) {
            return packetToDump.dump(packetLength);
        }
        StringBuilder packetDumpBuf = new StringBuilder(4096);
        packetDumpBuf.append(packetToDump.dump(1024));
        packetDumpBuf.append(Messages.getString("MysqlIO.36"));
        packetDumpBuf.append(1024);
        packetDumpBuf.append(Messages.getString("MysqlIO.37"));
        return packetDumpBuf.toString();
    }

    private final int readFully(InputStream in, byte[] b, int off, int len) throws IOException {
        if (len >= 0) {
            int n = 0;
            while (n < len) {
                int count = in.read(b, off + n, len - n);
                if (count >= 0) {
                    n += count;
                } else {
                    throw new EOFException(Messages.getString("MysqlIO.EOF", new Object[]{Integer.valueOf(len), Integer.valueOf(n)}));
                }
            }
            return n;
        }
        throw new IndexOutOfBoundsException();
    }

    private final long skipFully(InputStream in, long len) throws IOException {
        if (len >= 0) {
            long n = 0;
            while (n < len) {
                long count = in.skip(len - n);
                if (count >= 0) {
                    n += count;
                } else {
                    throw new EOFException(Messages.getString("MysqlIO.EOF", new Object[]{Long.valueOf(len), Long.valueOf(n)}));
                }
            }
            return n;
        }
        throw new IOException("Negative skip length not allowed");
    }

    private final int skipLengthEncodedInteger(InputStream in) throws IOException {
        switch (in.read() & 255) {
            case MysqlDefs.FIELD_TYPE_BLOB:
                return ((int) skipFully(in, 2)) + 1;
            case 253:
                return ((int) skipFully(in, 3)) + 1;
            case 254:
                return ((int) skipFully(in, 8)) + 1;
            default:
                return 1;
        }
    }

    /* access modifiers changed from: protected */
    public final ResultSetImpl readResultsForQueryOrUpdate(StatementImpl callingStatement, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Buffer resultPacket, boolean isBinaryEncoded, long preSentColumnCount, Field[] metadataFromCache) throws SQLException {
        String fileName;
        StatementImpl statementImpl = callingStatement;
        Buffer buffer = resultPacket;
        long columnCount = resultPacket.readFieldLength();
        if (columnCount == 0) {
            return buildResultSetWithUpdates(statementImpl, buffer);
        }
        if (columnCount != -1) {
            return getResultSet(callingStatement, columnCount, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, isBinaryEncoded, metadataFromCache);
        }
        String charEncoding = null;
        if (this.connection.getUseUnicode()) {
            charEncoding = this.connection.getEncoding();
        }
        if (this.platformDbCharsetMatches) {
            fileName = charEncoding != null ? buffer.readString(charEncoding, getExceptionInterceptor()) : resultPacket.readString();
        } else {
            fileName = resultPacket.readString();
        }
        return sendFileToServer(statementImpl, fileName);
    }

    private int alignPacketSize(int a, int l) {
        return ((a + l) - 1) & (~(l - 1));
    }

    private ResultSetImpl buildResultSetWithRows(StatementImpl callingStatement, String catalog, Field[] fields, RowData rows, int resultSetType, int resultSetConcurrency, boolean isBinaryEncoded) throws SQLException {
        ResultSetImpl rs;
        switch (resultSetConcurrency) {
            case 1007:
                rs = ResultSetImpl.getInstance(catalog, fields, rows, this.connection, callingStatement, false);
                if (isBinaryEncoded) {
                    rs.setBinaryEncoded();
                    break;
                }
                break;
            case 1008:
                rs = ResultSetImpl.getInstance(catalog, fields, rows, this.connection, callingStatement, true);
                break;
            default:
                return ResultSetImpl.getInstance(catalog, fields, rows, this.connection, callingStatement, false);
        }
        rs.setResultSetType(resultSetType);
        rs.setResultSetConcurrency(resultSetConcurrency);
        return rs;
    }

    private ResultSetImpl buildResultSetWithUpdates(StatementImpl callingStatement, Buffer resultPacket) throws SQLException {
        long updateID;
        long updateCount;
        String info = null;
        try {
            if (this.useNewUpdateCounts) {
                updateCount = resultPacket.newReadLength();
                updateID = resultPacket.newReadLength();
            } else {
                updateCount = resultPacket.readLength();
                updateID = resultPacket.readLength();
            }
            if (this.use41Extensions) {
                this.serverStatus = resultPacket.readInt();
                checkTransactionState(this.oldServerStatus);
                int readInt = resultPacket.readInt();
                this.warningCount = readInt;
                if (readInt > 0) {
                    this.hadWarnings = true;
                }
                resultPacket.readByte();
                setServerSlowQueryFlags();
            }
            if (this.connection.isReadInfoMsgEnabled()) {
                info = resultPacket.readString(this.connection.getErrorMessageEncoding(), getExceptionInterceptor());
            }
            ResultSetImpl instance = ResultSetImpl.getInstance(updateCount, updateID, this.connection, callingStatement);
            if (info != null) {
                instance.setServerInfo(info);
            }
            return instance;
        } catch (Exception ex) {
            SQLException sqlEx = SQLError.createSQLException(SQLError.get(SQLError.SQL_STATE_GENERAL_ERROR), SQLError.SQL_STATE_GENERAL_ERROR, -1, getExceptionInterceptor());
            sqlEx.initCause(ex);
            throw sqlEx;
        }
    }

    private void setServerSlowQueryFlags() {
        int i = this.serverStatus;
        boolean z = true;
        this.queryBadIndexUsed = (i & 16) != 0;
        this.queryNoIndexUsed = (i & 32) != 0;
        if ((i & 2048) == 0) {
            z = false;
        }
        this.serverQueryWasSlow = z;
    }

    private void checkForOutstandingStreamingData() throws SQLException {
        if (this.streamingData == null) {
            return;
        }
        if (this.connection.getClobberStreamingResults()) {
            this.streamingData.getOwner().realClose(false);
            clearInputStream();
            return;
        }
        throw SQLError.createSQLException(Messages.getString("MysqlIO.39") + this.streamingData + Messages.getString("MysqlIO.40") + Messages.getString("MysqlIO.41") + Messages.getString("MysqlIO.42"), getExceptionInterceptor());
    }

    private Buffer compressPacket(Buffer packet, int offset, int packetLen) throws SQLException {
        byte[] compressedBytes;
        int compressedLength = packetLen;
        int uncompressedLength = 0;
        int offsetWrite = offset;
        if (packetLen < 50) {
            compressedBytes = packet.getByteBuffer();
        } else {
            byte[] bytesToCompress = packet.getByteBuffer();
            compressedBytes = new byte[(bytesToCompress.length * 2)];
            if (this.deflater == null) {
                this.deflater = new Deflater();
            }
            this.deflater.reset();
            this.deflater.setInput(bytesToCompress, offset, packetLen);
            this.deflater.finish();
            compressedLength = this.deflater.deflate(compressedBytes);
            if (compressedLength > packetLen) {
                compressedBytes = packet.getByteBuffer();
                compressedLength = packetLen;
            } else {
                uncompressedLength = packetLen;
                offsetWrite = 0;
            }
        }
        Buffer compressedPacket = new Buffer(compressedLength + 7);
        compressedPacket.setPosition(0);
        compressedPacket.writeLongInt(compressedLength);
        compressedPacket.writeByte(this.compressedPacketSequence);
        compressedPacket.writeLongInt(uncompressedLength);
        compressedPacket.writeBytesNoNull(compressedBytes, offsetWrite, compressedLength);
        return compressedPacket;
    }

    private final void readServerStatusForResultSets(Buffer rowPacket) throws SQLException {
        if (this.use41Extensions) {
            rowPacket.readByte();
            if (isEOFDeprecated()) {
                rowPacket.newReadLength();
                rowPacket.newReadLength();
                this.oldServerStatus = this.serverStatus;
                this.serverStatus = rowPacket.readInt();
                checkTransactionState(this.oldServerStatus);
                int readInt = rowPacket.readInt();
                this.warningCount = readInt;
                if (readInt > 0) {
                    this.hadWarnings = true;
                }
                rowPacket.readByte();
                if (this.connection.isReadInfoMsgEnabled()) {
                    rowPacket.readString(this.connection.getErrorMessageEncoding(), getExceptionInterceptor());
                }
            } else {
                int readInt2 = rowPacket.readInt();
                this.warningCount = readInt2;
                if (readInt2 > 0) {
                    this.hadWarnings = true;
                }
                this.oldServerStatus = this.serverStatus;
                this.serverStatus = rowPacket.readInt();
                checkTransactionState(this.oldServerStatus);
            }
            setServerSlowQueryFlags();
        }
    }

    private SocketFactory createSocketFactory() throws SQLException {
        try {
            String str = this.socketFactoryClassName;
            if (str != null) {
                SocketFactory socketFactory2 = (SocketFactory) Class.forName(str).newInstance();
                SocketFactory socketFactory3 = socketFactory2;
                return socketFactory2;
            }
            throw SQLError.createSQLException(Messages.getString("MysqlIO.75"), SQLError.SQL_STATE_UNABLE_TO_CONNECT_TO_DATASOURCE, getExceptionInterceptor());
        } catch (Exception ex) {
            SQLException sqlEx = SQLError.createSQLException(Messages.getString("MysqlIO.76") + this.socketFactoryClassName + Messages.getString("MysqlIO.77"), SQLError.SQL_STATE_UNABLE_TO_CONNECT_TO_DATASOURCE, getExceptionInterceptor());
            sqlEx.initCause(ex);
            throw sqlEx;
        }
    }

    private void enqueuePacketForDebugging(boolean isPacketBeingSent, boolean isPacketReused, int sendLength, byte[] header, Buffer packet) throws SQLException {
        StringBuilder packetDump;
        int i = sendLength;
        Buffer buffer = packet;
        if (this.packetDebugRingBuffer.size() + 1 > this.connection.getPacketDebugBufferSize()) {
            this.packetDebugRingBuffer.removeFirst();
        }
        if (!isPacketBeingSent) {
            int bytesToDump = Math.min(1024, packet.getBufLength());
            Buffer packetToDump = new Buffer(bytesToDump + 4);
            packetToDump.setPosition(0);
            packetToDump.writeBytesNoNull(header);
            packetToDump.writeBytesNoNull(buffer.getBytes(0, bytesToDump));
            String packetPayload = packetToDump.dump(bytesToDump);
            packetDump = new StringBuilder(packetPayload.length() + 96);
            packetDump.append("Server ");
            packetDump.append(isPacketReused ? "(re-used) " : "(new) ");
            packetDump.append(packet.toSuperString());
            packetDump.append(" --------------------> Client\n");
            packetDump.append("\nPacket payload:\n\n");
            packetDump.append(packetPayload);
            if (bytesToDump == 1024) {
                packetDump.append("\nNote: Packet of " + packet.getBufLength() + " bytes truncated to " + 1024 + " bytes.\n");
            }
        } else {
            byte[] bArr = header;
            int bytesToDump2 = Math.min(1024, i);
            String packetPayload2 = buffer.dump(bytesToDump2);
            packetDump = new StringBuilder(packetPayload2.length() + 68);
            packetDump.append("Client ");
            packetDump.append(packet.toSuperString());
            packetDump.append("--------------------> Server\n");
            packetDump.append("\nPacket payload:\n\n");
            packetDump.append(packetPayload2);
            if (bytesToDump2 == 1024) {
                packetDump.append("\nNote: Packet of " + i + " bytes truncated to " + 1024 + " bytes.\n");
            }
        }
        this.packetDebugRingBuffer.addLast(packetDump);
    }

    private RowData readSingleRowSet(long columnCount, int maxRows, int resultSetConcurrency, boolean isBinaryEncoded, Field[] fields) throws SQLException {
        int rowCount;
        ResultSetRow row;
        long j = columnCount;
        int i = maxRows;
        ArrayList<ResultSetRow> rows = new ArrayList<>();
        boolean useBufferRowExplicit = useBufferRowExplicit(fields);
        ResultSetRow row2 = nextRow(fields, (int) j, isBinaryEncoded, resultSetConcurrency, false, useBufferRowExplicit, false, (Buffer) null);
        if (row2 != null) {
            rows.add(row2);
            row = row2;
            rowCount = 1;
        } else {
            row = row2;
            rowCount = 0;
        }
        while (row != null) {
            row = nextRow(fields, (int) j, isBinaryEncoded, resultSetConcurrency, false, useBufferRowExplicit, false, (Buffer) null);
            if (row != null && (i == -1 || rowCount < i)) {
                rows.add(row);
                rowCount++;
            }
        }
        return new RowDataStatic(rows);
    }

    public static boolean useBufferRowExplicit(Field[] fields) {
        if (fields == null) {
            return false;
        }
        int i = 0;
        while (i < fields.length) {
            switch (fields[i].getSQLType()) {
                case -4:
                case -1:
                case 2004:
                case 2005:
                    return true;
                default:
                    i++;
            }
        }
        return false;
    }

    private void reclaimLargeReusablePacket() {
        Buffer buffer = this.reusablePacket;
        if (buffer != null && buffer.getCapacity() > 1048576) {
            this.reusablePacket = new Buffer(1024);
        }
    }

    private final Buffer reuseAndReadPacket(Buffer reuse) throws SQLException {
        return reuseAndReadPacket(reuse, -1);
    }

    private final Buffer reuseAndReadPacket(Buffer reuse, int existingPacketLength) throws SQLException {
        int packetLength;
        try {
            reuse.setWasMultiPacket(false);
            if (existingPacketLength != -1) {
                packetLength = existingPacketLength;
            } else if (readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4) >= 4) {
                byte[] bArr = this.packetHeaderBuf;
                packetLength = (bArr[0] & 255) + ((bArr[1] & 255) << 8) + ((bArr[2] & 255) << 16);
            } else {
                forceClose();
                throw new IOException(Messages.getString("MysqlIO.43"));
            }
            if (this.traceProtocol != 0) {
                this.connection.getLog().logTrace(Messages.getString("MysqlIO.44") + packetLength + Messages.getString("MysqlIO.45") + StringUtils.dumpAsHex(this.packetHeaderBuf, 4));
            }
            byte multiPacketSeq = this.packetHeaderBuf[3];
            if (this.packetSequenceReset) {
                this.packetSequenceReset = false;
            } else if (this.enablePacketDebug && this.checkPacketSequence) {
                checkPacketSequencing(multiPacketSeq);
            }
            this.readPacketSequence = multiPacketSeq;
            reuse.setPosition(0);
            if (reuse.getByteBuffer().length <= packetLength) {
                reuse.setByteBuffer(new byte[(packetLength + 1)]);
            }
            reuse.setBufLength(packetLength);
            int numBytesRead = readFully(this.mysqlInput, reuse.getByteBuffer(), 0, packetLength);
            if (numBytesRead == packetLength) {
                if (this.traceProtocol) {
                    this.connection.getLog().logTrace(Messages.getString("MysqlIO.46") + getPacketDumpToLog(reuse, packetLength));
                }
                if (this.enablePacketDebug) {
                    enqueuePacketForDebugging(false, true, 0, this.packetHeaderBuf, reuse);
                }
                boolean isMultiPacket = false;
                int i = this.maxThreeBytes;
                if (packetLength == i) {
                    reuse.setPosition(i);
                    isMultiPacket = true;
                    packetLength = readRemainingMultiPackets(reuse, multiPacketSeq);
                }
                if (!isMultiPacket) {
                    reuse.getByteBuffer()[packetLength] = 0;
                }
                if (this.connection.getMaintainTimeStats()) {
                    this.lastPacketReceivedTimeMs = System.currentTimeMillis();
                }
                return reuse;
            }
            throw new IOException("Short read, expected " + packetLength + " bytes, only read " + numBytesRead);
        } catch (IOException e) {
            throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, e, getExceptionInterceptor());
        } catch (OutOfMemoryError oom) {
            try {
                clearInputStream();
            } catch (Exception e2) {
            }
            try {
                this.connection.realClose(false, false, true, oom);
            } catch (Exception e3) {
            }
            throw oom;
        }
    }

    private int readRemainingMultiPackets(Buffer reuse, byte multiPacketSeq) throws IOException, SQLException {
        Buffer buffer = reuse;
        Buffer multiPacket = null;
        byte multiPacketSeq2 = multiPacketSeq;
        while (readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4) >= 4) {
            byte[] bArr = this.packetHeaderBuf;
            int packetLength = (bArr[0] & 255) + ((bArr[1] & 255) << 8) + ((bArr[2] & 255) << 16);
            if (multiPacket == null) {
                multiPacket = new Buffer(packetLength);
            }
            if (this.useNewLargePackets || packetLength != 1) {
                multiPacketSeq2 = (byte) (multiPacketSeq2 + 1);
                if (multiPacketSeq2 == this.packetHeaderBuf[3]) {
                    multiPacket.setPosition(0);
                    multiPacket.setBufLength(packetLength);
                    byte[] byteBuf = multiPacket.getByteBuffer();
                    int lengthToWrite = packetLength;
                    int bytesRead = readFully(this.mysqlInput, byteBuf, 0, packetLength);
                    if (bytesRead == lengthToWrite) {
                        buffer.writeBytesNoNull(byteBuf, 0, lengthToWrite);
                        if (packetLength != this.maxThreeBytes) {
                        }
                    } else {
                        throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, SQLError.createSQLException(Messages.getString("MysqlIO.50") + lengthToWrite + Messages.getString("MysqlIO.51") + bytesRead + ".", getExceptionInterceptor()), getExceptionInterceptor());
                    }
                } else {
                    throw new IOException(Messages.getString("MysqlIO.49"));
                }
            } else {
                clearInputStream();
            }
            buffer.setPosition(0);
            buffer.setWasMultiPacket(true);
            return packetLength;
        }
        forceClose();
        throw new IOException(Messages.getString("MysqlIO.47"));
    }

    private void checkPacketSequencing(byte multiPacketSeq) throws SQLException {
        if (multiPacketSeq != Byte.MIN_VALUE || this.readPacketSequence == Byte.MAX_VALUE) {
            byte b = this.readPacketSequence;
            if (b == -1 && multiPacketSeq != 0) {
                throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException("Packets out of order, expected packet # -1, but received packet # " + multiPacketSeq), getExceptionInterceptor());
            } else if (multiPacketSeq != Byte.MIN_VALUE && b != -1 && multiPacketSeq != b + 1) {
                throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException("Packets out of order, expected packet # " + (this.readPacketSequence + 1) + ", but received packet # " + multiPacketSeq), getExceptionInterceptor());
            }
        } else {
            throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException("Packets out of order, expected packet # -128, but received packet # " + multiPacketSeq), getExceptionInterceptor());
        }
    }

    /* access modifiers changed from: package-private */
    public void enableMultiQueries() throws SQLException {
        Buffer buf = getSharedSendPacket();
        buf.clear();
        buf.writeByte((byte) 27);
        buf.writeInt(0);
        sendCommand(27, (String) null, buf, false, (String) null, 0);
        preserveOldTransactionState();
    }

    /* access modifiers changed from: package-private */
    public void disableMultiQueries() throws SQLException {
        Buffer buf = getSharedSendPacket();
        buf.clear();
        buf.writeByte((byte) 27);
        buf.writeInt(1);
        sendCommand(27, (String) null, buf, false, (String) null, 0);
        preserveOldTransactionState();
    }

    private final void send(Buffer packet, int packetLen) throws SQLException {
        int i;
        try {
            int i2 = this.maxAllowedPacket;
            if (i2 > 0) {
                if (packetLen > i2) {
                    throw new PacketTooBigException((long) packetLen, (long) this.maxAllowedPacket);
                }
            }
            if (this.serverMajorVersion < 4 || (packetLen - 4 < (i = this.maxThreeBytes) && (!this.useCompression || packetLen - 4 < i - 3))) {
                this.packetSequence = (byte) (this.packetSequence + 1);
                Buffer packetToSend = packet;
                packetToSend.setPosition(0);
                packetToSend.writeLongInt(packetLen - 4);
                packetToSend.writeByte(this.packetSequence);
                if (this.useCompression) {
                    this.compressedPacketSequence = (byte) (this.compressedPacketSequence + 1);
                    int originalPacketLen = packetLen;
                    packetToSend = compressPacket(packetToSend, 0, packetLen);
                    packetLen = packetToSend.getPosition();
                    if (this.traceProtocol) {
                        this.connection.getLog().logTrace(Messages.getString("MysqlIO.57") + getPacketDumpToLog(packetToSend, packetLen) + Messages.getString("MysqlIO.58") + getPacketDumpToLog(packet, originalPacketLen));
                    }
                } else if (this.traceProtocol) {
                    this.connection.getLog().logTrace(Messages.getString("MysqlIO.59") + "host: '" + this.host + "' threadId: '" + this.threadId + "'\n" + packetToSend.dump(packetLen));
                }
                this.mysqlOutput.write(packetToSend.getByteBuffer(), 0, packetLen);
                this.mysqlOutput.flush();
            } else {
                sendSplitPackets(packet, packetLen);
            }
            if (this.enablePacketDebug) {
                enqueuePacketForDebugging(true, false, packetLen + 5, this.packetHeaderBuf, packet);
            }
            if (packet == this.sharedSendPacket) {
                reclaimLargeSharedSendPacket();
            }
            if (this.connection.getMaintainTimeStats()) {
                this.lastPacketSentTimeMs = System.currentTimeMillis();
            }
        } catch (IOException e) {
            throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, e, getExceptionInterceptor());
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:56:0x011c A[Catch:{ IOException -> 0x0168 }, LOOP:0: B:54:0x0114->B:56:0x011c, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x012d A[SYNTHETIC, Splitter:B:57:0x012d] */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x01a0 A[Catch:{ all -> 0x01bf }] */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x01c2 A[SYNTHETIC, Splitter:B:86:0x01c2] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x01d8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final com.mysql.jdbc.ResultSetImpl sendFileToServer(com.mysql.jdbc.StatementImpl r18, java.lang.String r19) throws java.sql.SQLException {
        /*
            r17 = this;
            r1 = r17
            r2 = r18
            r3 = r19
            java.lang.String r4 = "'"
            java.lang.String r5 = "MysqlIO.65"
            java.lang.String r6 = "S1000"
            boolean r0 = r1.useCompression
            if (r0 == 0) goto L_0x0017
            byte r0 = r1.compressedPacketSequence
            int r0 = r0 + 1
            byte r0 = (byte) r0
            r1.compressedPacketSequence = r0
        L_0x0017:
            java.lang.ref.SoftReference<com.mysql.jdbc.Buffer> r0 = r1.loadFileBufRef
            if (r0 != 0) goto L_0x001d
            r0 = 0
            goto L_0x0023
        L_0x001d:
            java.lang.Object r0 = r0.get()
            com.mysql.jdbc.Buffer r0 = (com.mysql.jdbc.Buffer) r0
        L_0x0023:
            r7 = r0
            com.mysql.jdbc.MySQLConnection r0 = r1.connection
            int r0 = r0.getMaxAllowedPacket()
            int r0 = r0 + -12
            com.mysql.jdbc.MySQLConnection r8 = r1.connection
            int r8 = r8.getMaxAllowedPacket()
            int r8 = r8 + -16
            r9 = 4096(0x1000, float:5.74E-42)
            int r8 = r1.alignPacketSize(r8, r9)
            int r8 = r8 + -12
            int r8 = java.lang.Math.min(r0, r8)
            r10 = 1048576(0x100000, float:1.469368E-39)
            int r0 = r10 + -12
            int r11 = r10 + -16
            int r9 = r1.alignPacketSize(r11, r9)
            int r9 = r9 + -12
            int r9 = java.lang.Math.min(r0, r9)
            int r11 = java.lang.Math.min(r9, r8)
            if (r7 != 0) goto L_0x0091
            com.mysql.jdbc.Buffer r0 = new com.mysql.jdbc.Buffer     // Catch:{ OutOfMemoryError -> 0x0066 }
            int r12 = r11 + 4
            r0.<init>((int) r12)     // Catch:{ OutOfMemoryError -> 0x0066 }
            r7 = r0
            java.lang.ref.SoftReference r0 = new java.lang.ref.SoftReference     // Catch:{ OutOfMemoryError -> 0x0066 }
            r0.<init>(r7)     // Catch:{ OutOfMemoryError -> 0x0066 }
            r1.loadFileBufRef = r0     // Catch:{ OutOfMemoryError -> 0x0066 }
            goto L_0x0091
        L_0x0066:
            r0 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Could not allocate packet of "
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.StringBuilder r4 = r4.append(r11)
            java.lang.String r5 = " bytes required for LOAD DATA LOCAL INFILE operation."
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r5 = " Try increasing max heap allocation for JVM or decreasing server variable 'max_allowed_packet'"
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r4 = r4.toString()
            com.mysql.jdbc.ExceptionInterceptor r5 = r17.getExceptionInterceptor()
            java.lang.String r6 = "S1001"
            java.sql.SQLException r4 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r4, (java.lang.String) r6, (com.mysql.jdbc.ExceptionInterceptor) r5)
            throw r4
        L_0x0091:
            r7.clear()
            r12 = 0
            r1.send(r7, r12)
            byte[] r13 = new byte[r11]
            r14 = 0
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ IOException -> 0x016e, all -> 0x016a }
            boolean r0 = r0.getAllowLoadLocalInfile()     // Catch:{ IOException -> 0x016e, all -> 0x016a }
            if (r0 == 0) goto L_0x0157
            r0 = 0
            if (r2 == 0) goto L_0x00b6
            java.io.InputStream r15 = r18.getLocalInfileInputStream()     // Catch:{ IOException -> 0x00b1, all -> 0x00ac }
            r0 = r15
            goto L_0x00b7
        L_0x00ac:
            r0 = move-exception
            r16 = r8
            goto L_0x01c0
        L_0x00b1:
            r0 = move-exception
            r16 = r8
            goto L_0x0171
        L_0x00b6:
            r15 = r0
        L_0x00b7:
            if (r15 == 0) goto L_0x00c2
            java.io.BufferedInputStream r0 = new java.io.BufferedInputStream     // Catch:{ IOException -> 0x00b1, all -> 0x00ac }
            r0.<init>(r15)     // Catch:{ IOException -> 0x00b1, all -> 0x00ac }
            r14 = r0
            r16 = r8
            goto L_0x0113
        L_0x00c2:
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ IOException -> 0x016e, all -> 0x016a }
            boolean r0 = r0.getAllowUrlInLocalInfile()     // Catch:{ IOException -> 0x016e, all -> 0x016a }
            if (r0 != 0) goto L_0x00d8
            java.io.BufferedInputStream r0 = new java.io.BufferedInputStream     // Catch:{ IOException -> 0x00b1, all -> 0x00ac }
            java.io.FileInputStream r12 = new java.io.FileInputStream     // Catch:{ IOException -> 0x00b1, all -> 0x00ac }
            r12.<init>(r3)     // Catch:{ IOException -> 0x00b1, all -> 0x00ac }
            r0.<init>(r12)     // Catch:{ IOException -> 0x00b1, all -> 0x00ac }
            r14 = r0
            r16 = r8
            goto L_0x0113
        L_0x00d8:
            r0 = 58
            int r0 = r3.indexOf(r0)     // Catch:{ IOException -> 0x016e, all -> 0x016a }
            r12 = -1
            if (r0 == r12) goto L_0x0106
            java.net.URL r0 = new java.net.URL     // Catch:{ MalformedURLException -> 0x00f6 }
            r0.<init>(r3)     // Catch:{ MalformedURLException -> 0x00f6 }
            java.io.BufferedInputStream r12 = new java.io.BufferedInputStream     // Catch:{ MalformedURLException -> 0x00f6 }
            r16 = r8
            java.io.InputStream r8 = r0.openStream()     // Catch:{ MalformedURLException -> 0x00f4 }
            r12.<init>(r8)     // Catch:{ MalformedURLException -> 0x00f4 }
            r8 = r12
            r14 = r8
            goto L_0x0113
        L_0x00f4:
            r0 = move-exception
            goto L_0x00f9
        L_0x00f6:
            r0 = move-exception
            r16 = r8
        L_0x00f9:
            java.io.BufferedInputStream r8 = new java.io.BufferedInputStream     // Catch:{ IOException -> 0x0168 }
            java.io.FileInputStream r12 = new java.io.FileInputStream     // Catch:{ IOException -> 0x0168 }
            r12.<init>(r3)     // Catch:{ IOException -> 0x0168 }
            r8.<init>(r12)     // Catch:{ IOException -> 0x0168 }
            r0 = r8
            r14 = r0
            goto L_0x0113
        L_0x0106:
            r16 = r8
            java.io.BufferedInputStream r0 = new java.io.BufferedInputStream     // Catch:{ IOException -> 0x0168 }
            java.io.FileInputStream r8 = new java.io.FileInputStream     // Catch:{ IOException -> 0x0168 }
            r8.<init>(r3)     // Catch:{ IOException -> 0x0168 }
            r0.<init>(r8)     // Catch:{ IOException -> 0x0168 }
            r14 = r0
        L_0x0113:
            r0 = 0
        L_0x0114:
            int r8 = r14.read(r13)     // Catch:{ IOException -> 0x0168 }
            r0 = r8
            r12 = -1
            if (r8 == r12) goto L_0x012b
            r7.clear()     // Catch:{ IOException -> 0x0168 }
            r8 = 0
            r7.writeBytesNoNull(r13, r8, r0)     // Catch:{ IOException -> 0x0168 }
            int r8 = r7.getPosition()     // Catch:{ IOException -> 0x0168 }
            r1.send(r7, r8)     // Catch:{ IOException -> 0x0168 }
            goto L_0x0114
        L_0x012b:
            r14.close()     // Catch:{ Exception -> 0x0147 }
            r0 = 0
            r7.clear()
            int r4 = r7.getPosition()
            r1.send(r7, r4)
            com.mysql.jdbc.Buffer r4 = r17.checkErrorPacket()
            com.mysql.jdbc.ResultSetImpl r5 = r1.buildResultSetWithUpdates(r2, r4)
            return r5
        L_0x0147:
            r0 = move-exception
            r4 = r0
            r0 = r4
            java.lang.String r4 = com.mysql.jdbc.Messages.getString(r5)
            com.mysql.jdbc.ExceptionInterceptor r5 = r17.getExceptionInterceptor()
            java.sql.SQLException r4 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r4, (java.lang.String) r6, (java.lang.Throwable) r0, (com.mysql.jdbc.ExceptionInterceptor) r5)
            throw r4
        L_0x0157:
            r16 = r8
            java.lang.String r0 = "MysqlIO.LoadDataLocalNotAllowed"
            java.lang.String r0 = com.mysql.jdbc.Messages.getString(r0)     // Catch:{ IOException -> 0x0168 }
            com.mysql.jdbc.ExceptionInterceptor r8 = r17.getExceptionInterceptor()     // Catch:{ IOException -> 0x0168 }
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r0, (java.lang.String) r6, (com.mysql.jdbc.ExceptionInterceptor) r8)     // Catch:{ IOException -> 0x0168 }
            throw r0     // Catch:{ IOException -> 0x0168 }
        L_0x0168:
            r0 = move-exception
            goto L_0x0171
        L_0x016a:
            r0 = move-exception
            r16 = r8
            goto L_0x01c0
        L_0x016e:
            r0 = move-exception
            r16 = r8
        L_0x0171:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x01bf }
            java.lang.String r12 = "MysqlIO.60"
            java.lang.String r12 = com.mysql.jdbc.Messages.getString(r12)     // Catch:{ all -> 0x01bf }
            r8.<init>(r12)     // Catch:{ all -> 0x01bf }
            if (r3 == 0) goto L_0x018f
            com.mysql.jdbc.MySQLConnection r12 = r1.connection     // Catch:{ all -> 0x01bf }
            boolean r12 = r12.getParanoid()     // Catch:{ all -> 0x01bf }
            if (r12 != 0) goto L_0x018f
            r8.append(r4)     // Catch:{ all -> 0x01bf }
            r8.append(r3)     // Catch:{ all -> 0x01bf }
            r8.append(r4)     // Catch:{ all -> 0x01bf }
        L_0x018f:
            java.lang.String r4 = "MysqlIO.63"
            java.lang.String r4 = com.mysql.jdbc.Messages.getString(r4)     // Catch:{ all -> 0x01bf }
            r8.append(r4)     // Catch:{ all -> 0x01bf }
            com.mysql.jdbc.MySQLConnection r4 = r1.connection     // Catch:{ all -> 0x01bf }
            boolean r4 = r4.getParanoid()     // Catch:{ all -> 0x01bf }
            if (r4 != 0) goto L_0x01b0
            java.lang.String r4 = "MysqlIO.64"
            java.lang.String r4 = com.mysql.jdbc.Messages.getString(r4)     // Catch:{ all -> 0x01bf }
            r8.append(r4)     // Catch:{ all -> 0x01bf }
            java.lang.String r4 = com.mysql.jdbc.Util.stackTraceToString(r0)     // Catch:{ all -> 0x01bf }
            r8.append(r4)     // Catch:{ all -> 0x01bf }
        L_0x01b0:
            java.lang.String r4 = r8.toString()     // Catch:{ all -> 0x01bf }
            java.lang.String r12 = "S1009"
            com.mysql.jdbc.ExceptionInterceptor r15 = r17.getExceptionInterceptor()     // Catch:{ all -> 0x01bf }
            java.sql.SQLException r4 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r4, (java.lang.String) r12, (com.mysql.jdbc.ExceptionInterceptor) r15)     // Catch:{ all -> 0x01bf }
            throw r4     // Catch:{ all -> 0x01bf }
        L_0x01bf:
            r0 = move-exception
        L_0x01c0:
            if (r14 == 0) goto L_0x01d8
            r14.close()     // Catch:{ Exception -> 0x01c8 }
            r14 = 0
            goto L_0x01e5
        L_0x01c8:
            r0 = move-exception
            r4 = r0
            r0 = r4
            java.lang.String r4 = com.mysql.jdbc.Messages.getString(r5)
            com.mysql.jdbc.ExceptionInterceptor r5 = r17.getExceptionInterceptor()
            java.sql.SQLException r4 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r4, (java.lang.String) r6, (java.lang.Throwable) r0, (com.mysql.jdbc.ExceptionInterceptor) r5)
            throw r4
        L_0x01d8:
            r7.clear()
            int r4 = r7.getPosition()
            r1.send(r7, r4)
            r17.checkErrorPacket()
        L_0x01e5:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.MysqlIO.sendFileToServer(com.mysql.jdbc.StatementImpl, java.lang.String):com.mysql.jdbc.ResultSetImpl");
    }

    private Buffer checkErrorPacket(int command) throws SQLException {
        this.serverStatus = 0;
        try {
            Buffer resultPacket = reuseAndReadPacket(this.reusablePacket);
            checkErrorPacket(resultPacket);
            return resultPacket;
        } catch (SQLException sqlEx) {
            throw sqlEx;
        } catch (Exception e) {
            throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, e, getExceptionInterceptor());
        }
    }

    private void checkErrorPacket(Buffer resultPacket) throws SQLException {
        String xOpen;
        Buffer buffer = resultPacket;
        if (resultPacket.readByte() != -1) {
            return;
        }
        if (this.protocolVersion > 9) {
            int errno = resultPacket.readInt();
            String serverErrorMessage = buffer.readString(this.connection.getErrorMessageEncoding(), getExceptionInterceptor());
            if (serverErrorMessage.charAt(0) != '#') {
                xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
            } else if (serverErrorMessage.length() > 6) {
                xOpen = serverErrorMessage.substring(1, 6);
                serverErrorMessage = serverErrorMessage.substring(6);
                if (xOpen.equals(SQLError.SQL_STATE_CLI_SPECIFIC_CONDITION)) {
                    xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
                }
            } else {
                xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
            }
            clearInputStream();
            StringBuilder errorBuf = new StringBuilder();
            String xOpenErrorMessage = SQLError.get(xOpen);
            if (!this.connection.getUseOnlyServerErrorMessages() && xOpenErrorMessage != null) {
                errorBuf.append(xOpenErrorMessage);
                errorBuf.append(Messages.getString("MysqlIO.68"));
            }
            errorBuf.append(serverErrorMessage);
            if (!this.connection.getUseOnlyServerErrorMessages() && xOpenErrorMessage != null) {
                errorBuf.append("\"");
            }
            appendDeadlockStatusInformation(xOpen, errorBuf);
            if (xOpen == null || !xOpen.startsWith("22")) {
                throw SQLError.createSQLException(errorBuf.toString(), xOpen, errno, false, getExceptionInterceptor(), this.connection);
            }
            String str = xOpenErrorMessage;
            throw new MysqlDataTruncation(errorBuf.toString(), 0, true, false, 0, 0, errno);
        }
        String serverErrorMessage2 = buffer.readString(this.connection.getErrorMessageEncoding(), getExceptionInterceptor());
        clearInputStream();
        if (serverErrorMessage2.indexOf(Messages.getString("MysqlIO.70")) != -1) {
            throw SQLError.createSQLException(SQLError.get(SQLError.SQL_STATE_COLUMN_NOT_FOUND) + ", " + serverErrorMessage2, SQLError.SQL_STATE_COLUMN_NOT_FOUND, -1, false, getExceptionInterceptor(), this.connection);
        }
        throw SQLError.createSQLException(SQLError.get(SQLError.SQL_STATE_GENERAL_ERROR) + ", " + (Messages.getString("MysqlIO.72") + serverErrorMessage2 + "\""), SQLError.SQL_STATE_GENERAL_ERROR, -1, false, getExceptionInterceptor(), this.connection);
    }

    private void appendDeadlockStatusInformation(String xOpen, StringBuilder errorBuf) throws SQLException {
        if (this.connection.getIncludeInnodbStatusInDeadlockExceptions() && xOpen != null && ((xOpen.startsWith("40") || xOpen.startsWith("41")) && this.streamingData == null)) {
            ResultSet rs = null;
            try {
                ResultSet rs2 = sqlQueryDirect((StatementImpl) null, "SHOW ENGINE INNODB STATUS", this.connection.getEncoding(), (Buffer) null, -1, 1003, 1007, false, this.connection.getCatalog(), (Field[]) null);
                if (rs2.next()) {
                    errorBuf.append("\n\n");
                    errorBuf.append(rs2.getString("Status"));
                } else {
                    errorBuf.append("\n\n");
                    errorBuf.append(Messages.getString("MysqlIO.NoInnoDBStatusFound"));
                }
                if (rs2 != null) {
                    rs2.close();
                }
            } catch (Exception ex) {
                errorBuf.append("\n\n");
                errorBuf.append(Messages.getString("MysqlIO.InnoDBStatusFailed"));
                errorBuf.append("\n\n");
                errorBuf.append(Util.stackTraceToString(ex));
                if (rs != null) {
                    rs.close();
                }
            } catch (Throwable th) {
                if (rs != null) {
                    rs.close();
                }
                throw th;
            }
        }
        if (this.connection.getIncludeThreadDumpInDeadlockExceptions()) {
            errorBuf.append("\n\n*** Java threads running at time of deadlock ***\n\n");
            ThreadMXBean threadMBean = ManagementFactory.getThreadMXBean();
            ThreadInfo[] threads = threadMBean.getThreadInfo(threadMBean.getAllThreadIds(), Integer.MAX_VALUE);
            List<ThreadInfo> activeThreads = new ArrayList<>();
            for (ThreadInfo info : threads) {
                if (info != null) {
                    activeThreads.add(info);
                }
            }
            for (ThreadInfo threadInfo : activeThreads) {
                errorBuf.append(Typography.quote);
                errorBuf.append(threadInfo.getThreadName());
                errorBuf.append("\" tid=");
                errorBuf.append(threadInfo.getThreadId());
                errorBuf.append(" ");
                errorBuf.append(threadInfo.getThreadState());
                if (threadInfo.getLockName() != null) {
                    errorBuf.append(" on lock=" + threadInfo.getLockName());
                }
                if (threadInfo.isSuspended()) {
                    errorBuf.append(" (suspended)");
                }
                if (threadInfo.isInNative()) {
                    errorBuf.append(" (running in native)");
                }
                StackTraceElement[] stackTrace = threadInfo.getStackTrace();
                if (stackTrace.length > 0) {
                    errorBuf.append(" in ");
                    errorBuf.append(stackTrace[0].getClassName());
                    errorBuf.append(".");
                    errorBuf.append(stackTrace[0].getMethodName());
                    errorBuf.append("()");
                }
                errorBuf.append("\n");
                if (threadInfo.getLockOwnerName() != null) {
                    errorBuf.append("\t owned by " + threadInfo.getLockOwnerName() + " Id=" + threadInfo.getLockOwnerId());
                    errorBuf.append("\n");
                }
                for (int j = 0; j < stackTrace.length; j++) {
                    errorBuf.append("\tat " + stackTrace[j].toString());
                    errorBuf.append("\n");
                }
            }
        }
    }

    private final void sendSplitPackets(Buffer packet, int packetLen) throws SQLException {
        try {
            SoftReference<Buffer> softReference = this.splitBufRef;
            Buffer toCompress = null;
            Buffer packetToSend = softReference == null ? null : softReference.get();
            if (this.useCompression) {
                SoftReference<Buffer> softReference2 = this.compressBufRef;
                if (softReference2 != null) {
                    toCompress = softReference2.get();
                }
            }
            if (packetToSend == null) {
                packetToSend = new Buffer(this.maxThreeBytes + 4);
                this.splitBufRef = new SoftReference<>(packetToSend);
            }
            if (this.useCompression) {
                int cbuflen = (((packetLen / this.maxThreeBytes) + 1) * 4) + packetLen;
                if (toCompress == null) {
                    toCompress = new Buffer(cbuflen);
                    this.compressBufRef = new SoftReference<>(toCompress);
                } else if (toCompress.getBufLength() < cbuflen) {
                    toCompress.setPosition(toCompress.getBufLength());
                    toCompress.ensureCapacity(cbuflen - toCompress.getBufLength());
                }
            }
            int len = packetLen - 4;
            int splitSize = this.maxThreeBytes;
            int originalPacketPos = 4;
            byte[] origPacketBytes = packet.getByteBuffer();
            int toCompressPosition = 0;
            while (len >= 0) {
                this.packetSequence = (byte) (this.packetSequence + 1);
                if (len < splitSize) {
                    splitSize = len;
                }
                packetToSend.setPosition(0);
                packetToSend.writeLongInt(splitSize);
                packetToSend.writeByte(this.packetSequence);
                if (len > 0) {
                    System.arraycopy(origPacketBytes, originalPacketPos, packetToSend.getByteBuffer(), 4, splitSize);
                }
                if (this.useCompression) {
                    System.arraycopy(packetToSend.getByteBuffer(), 0, toCompress.getByteBuffer(), toCompressPosition, splitSize + 4);
                    toCompressPosition += splitSize + 4;
                } else {
                    this.mysqlOutput.write(packetToSend.getByteBuffer(), 0, splitSize + 4);
                    this.mysqlOutput.flush();
                }
                originalPacketPos += splitSize;
                len -= this.maxThreeBytes;
            }
            if (this.useCompression) {
                int len2 = toCompressPosition;
                int toCompressPosition2 = 0;
                int splitSize2 = this.maxThreeBytes - 3;
                while (len2 >= 0) {
                    this.compressedPacketSequence = (byte) (this.compressedPacketSequence + 1);
                    if (len2 < splitSize2) {
                        splitSize2 = len2;
                    }
                    Buffer compressedPacketToSend = compressPacket(toCompress, toCompressPosition2, splitSize2);
                    this.mysqlOutput.write(compressedPacketToSend.getByteBuffer(), 0, compressedPacketToSend.getPosition());
                    this.mysqlOutput.flush();
                    toCompressPosition2 += splitSize2;
                    len2 -= this.maxThreeBytes - 3;
                }
            }
        } catch (IOException e) {
            throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, e, getExceptionInterceptor());
        }
    }

    private void reclaimLargeSharedSendPacket() {
        Buffer buffer = this.sharedSendPacket;
        if (buffer != null && buffer.getCapacity() > 1048576) {
            this.sharedSendPacket = new Buffer(1024);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hadWarnings() {
        return this.hadWarnings;
    }

    /* access modifiers changed from: package-private */
    public void scanForAndThrowDataTruncation() throws SQLException {
        int i;
        if (this.streamingData == null && versionMeetsMinimum(4, 1, 0) && this.connection.getJdbcCompliantTruncation() && (i = this.warningCount) > 0) {
            int warningCountOld = this.warningCount;
            SQLError.convertShowWarningsToSQLWarnings(this.connection, i, true);
            this.warningCount = warningCountOld;
        }
    }

    private void secureAuth(Buffer packet, int packLength, String user, String password, String database, boolean writeClientParams) throws SQLException {
        Buffer packet2;
        int i = packLength;
        if (packet == null) {
            packet2 = new Buffer(i);
        } else {
            packet2 = packet;
        }
        if (writeClientParams) {
            if (!this.use41Extensions) {
                packet2.writeInt((int) this.clientParam);
                packet2.writeLongInt(this.maxThreeBytes);
            } else if (versionMeetsMinimum(4, 1, 1)) {
                packet2.writeLong(this.clientParam);
                packet2.writeLong((long) this.maxThreeBytes);
                packet2.writeByte((byte) 8);
                packet2.writeBytesNoNull(new byte[23]);
            } else {
                packet2.writeLong(this.clientParam);
                packet2.writeLong((long) this.maxThreeBytes);
            }
        }
        packet2.writeString(user, CODE_PAGE_1252, this.connection);
        if (password.length() != 0) {
            packet2.writeString(FALSE_SCRAMBLE, CODE_PAGE_1252, this.connection);
        } else {
            packet2.writeString("", CODE_PAGE_1252, this.connection);
        }
        if (this.useConnectWithDb) {
            packet2.writeString(database, CODE_PAGE_1252, this.connection);
        } else {
            String str = database;
        }
        send(packet2, packet2.getPosition());
        if (password.length() > 0) {
            Buffer b = readPacket();
            b.setPosition(0);
            byte[] replyAsBytes = b.getByteBuffer();
            if (replyAsBytes.length != 24 || replyAsBytes[0] == 0) {
                String str2 = password;
            } else if (replyAsBytes[0] != 42) {
                try {
                    byte[] buff = Security.passwordHashStage1(password);
                    byte[] passwordHash = new byte[buff.length];
                    System.arraycopy(buff, 0, passwordHash, 0, buff.length);
                    byte[] passwordHash2 = Security.passwordHashStage2(passwordHash, replyAsBytes);
                    byte[] packetDataAfterSalt = new byte[(replyAsBytes.length - 4)];
                    System.arraycopy(replyAsBytes, 4, packetDataAfterSalt, 0, replyAsBytes.length - 4);
                    byte[] mysqlScrambleBuff = new byte[20];
                    Security.xorString(packetDataAfterSalt, mysqlScrambleBuff, passwordHash2, 20);
                    Security.xorString(mysqlScrambleBuff, buff, buff, 20);
                    Buffer packet22 = new Buffer(25);
                    packet22.writeBytesNoNull(buff);
                    this.packetSequence = (byte) (this.packetSequence + 1);
                    send(packet22, 24);
                    String str3 = password;
                } catch (NoSuchAlgorithmException e) {
                    throw SQLError.createSQLException(Messages.getString("MysqlIO.91") + Messages.getString("MysqlIO.92"), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
                }
            } else {
                try {
                    byte[] passwordHash3 = Security.createKeyFromOldPassword(password);
                    byte[] netReadPos4 = new byte[(replyAsBytes.length - 4)];
                    System.arraycopy(replyAsBytes, 4, netReadPos4, 0, replyAsBytes.length - 4);
                    byte[] mysqlScrambleBuff2 = new byte[20];
                    Security.xorString(netReadPos4, mysqlScrambleBuff2, passwordHash3, 20);
                    try {
                        String scrambledPassword = Util.scramble(StringUtils.toString(mysqlScrambleBuff2), password);
                        Buffer packet23 = new Buffer(i);
                        byte[] bArr = passwordHash3;
                        packet23.writeString(scrambledPassword, CODE_PAGE_1252, this.connection);
                        this.packetSequence = (byte) (this.packetSequence + 1);
                        send(packet23, 24);
                    } catch (NoSuchAlgorithmException e2) {
                    }
                } catch (NoSuchAlgorithmException e3) {
                    String str4 = password;
                    throw SQLError.createSQLException(Messages.getString("MysqlIO.91") + Messages.getString("MysqlIO.92"), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
                }
            }
        } else {
            String str5 = password;
        }
    }

    /* access modifiers changed from: package-private */
    public void secureAuth411(Buffer packet, int packLength, String user, String password, String database, boolean writeClientParams, boolean forChangeUser) throws SQLException {
        String enc = getEncodingForHandshake();
        if (packet == null) {
            packet = new Buffer(packLength);
        }
        if (writeClientParams) {
            if (!this.use41Extensions) {
                packet.writeInt((int) this.clientParam);
                packet.writeLongInt(this.maxThreeBytes);
            } else if (versionMeetsMinimum(4, 1, 1)) {
                packet.writeLong(this.clientParam);
                packet.writeLong((long) this.maxThreeBytes);
                appendCharsetByteForHandshake(packet, enc);
                packet.writeBytesNoNull(new byte[23]);
            } else {
                packet.writeLong(this.clientParam);
                packet.writeLong((long) this.maxThreeBytes);
            }
        }
        if (user != null) {
            packet.writeString(user, enc, this.connection);
        }
        if (password.length() != 0) {
            packet.writeByte((byte) 20);
            try {
                packet.writeBytesNoNull(Security.scramble411(password, this.seed, this.connection.getPasswordCharacterEncoding()));
            } catch (NoSuchAlgorithmException e) {
                throw SQLError.createSQLException(Messages.getString("MysqlIO.91") + Messages.getString("MysqlIO.92"), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
            } catch (UnsupportedEncodingException e2) {
                throw SQLError.createSQLException(Messages.getString("MysqlIO.91") + Messages.getString("MysqlIO.92"), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
            }
        } else {
            packet.writeByte((byte) 0);
        }
        if (this.useConnectWithDb) {
            packet.writeString(database, enc, this.connection);
        } else if (forChangeUser) {
            packet.writeByte((byte) 0);
        }
        if ((this.serverCapabilities & 1048576) != 0) {
            sendConnectionAttributes(packet, enc, this.connection);
        }
        send(packet, packet.getPosition());
        byte savePacketSequence = this.packetSequence;
        this.packetSequence = (byte) (savePacketSequence + 1);
        if (checkErrorPacket().isAuthMethodSwitchRequestPacket()) {
            this.packetSequence = (byte) (savePacketSequence + 1);
            packet.clear();
            packet.writeString(Util.newCrypt(password, this.seed.substring(0, 8), this.connection.getPasswordCharacterEncoding()));
            send(packet, packet.getPosition());
            checkErrorPacket();
        }
        if (!this.useConnectWithDb) {
            changeDatabaseTo(database);
        }
    }

    private final ResultSetRow unpackBinaryResultSetRow(Field[] fields, Buffer binaryData, int resultSetConcurrency) throws SQLException {
        int numFields = fields.length;
        byte[][] unpackedRowData = new byte[numFields][];
        int nullMaskPos = binaryData.getPosition();
        binaryData.setPosition(nullMaskPos + ((numFields + 9) / 8));
        int bit = 4;
        for (int i = 0; i < numFields; i++) {
            if ((binaryData.readByte(nullMaskPos) & bit) != 0) {
                unpackedRowData[i] = null;
            } else if (resultSetConcurrency != 1008) {
                extractNativeEncodedColumn(binaryData, fields, i, unpackedRowData);
            } else {
                unpackNativeEncodedColumn(binaryData, fields, i, unpackedRowData);
            }
            int i2 = bit << 1;
            bit = i2;
            if ((i2 & 255) == 0) {
                bit = 1;
                nullMaskPos++;
            }
        }
        return new ByteArrayRow(unpackedRowData, getExceptionInterceptor());
    }

    private final void extractNativeEncodedColumn(Buffer binaryData, Field[] fields, int columnIndex, byte[][] unpackedRowData) throws SQLException {
        Field curField = fields[columnIndex];
        switch (curField.getMysqlType()) {
            case 0:
            case 15:
            case 16:
            case 245:
            case 246:
            case 249:
            case ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION:
            case 251:
            case MysqlDefs.FIELD_TYPE_BLOB:
            case 253:
            case 254:
            case 255:
                unpackedRowData[columnIndex] = binaryData.readLenByteArray(0);
                return;
            case 1:
                unpackedRowData[columnIndex] = new byte[]{binaryData.readByte()};
                return;
            case 2:
            case 13:
                unpackedRowData[columnIndex] = binaryData.getBytes(2);
                return;
            case 3:
            case 9:
                unpackedRowData[columnIndex] = binaryData.getBytes(4);
                return;
            case 4:
                unpackedRowData[columnIndex] = binaryData.getBytes(4);
                return;
            case 5:
                unpackedRowData[columnIndex] = binaryData.getBytes(8);
                return;
            case 6:
                return;
            case 7:
            case 12:
                unpackedRowData[columnIndex] = binaryData.getBytes((int) binaryData.readFieldLength());
                return;
            case 8:
                unpackedRowData[columnIndex] = binaryData.getBytes(8);
                return;
            case 10:
                unpackedRowData[columnIndex] = binaryData.getBytes((int) binaryData.readFieldLength());
                return;
            case 11:
                unpackedRowData[columnIndex] = binaryData.getBytes((int) binaryData.readFieldLength());
                return;
            default:
                throw SQLError.createSQLException(Messages.getString("MysqlIO.97") + curField.getMysqlType() + Messages.getString("MysqlIO.98") + columnIndex + Messages.getString("MysqlIO.99") + fields.length + Messages.getString("MysqlIO.100"), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
        }
    }

    private final void unpackNativeEncodedColumn(Buffer binaryData, Field[] fields, int columnIndex, byte[][] unpackedRowData) throws SQLException {
        Field[] fieldArr = fields;
        int i = columnIndex;
        Field curField = fieldArr[i];
        switch (curField.getMysqlType()) {
            case 0:
            case 15:
            case 16:
            case 245:
            case 246:
            case 249:
            case ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION:
            case 251:
            case MysqlDefs.FIELD_TYPE_BLOB:
            case 253:
            case 254:
                unpackedRowData[i] = binaryData.readLenByteArray(0);
                return;
            case 1:
                byte tinyVal = binaryData.readByte();
                if (!curField.isUnsigned()) {
                    unpackedRowData[i] = StringUtils.getBytes(String.valueOf(tinyVal));
                    Buffer buffer = binaryData;
                    return;
                }
                unpackedRowData[i] = StringUtils.getBytes(String.valueOf((short) (tinyVal & 255)));
                Buffer buffer2 = binaryData;
                return;
            case 2:
            case 13:
                int shortVal = (short) binaryData.readInt();
                if (!curField.isUnsigned()) {
                    unpackedRowData[i] = StringUtils.getBytes(String.valueOf(shortVal));
                    Buffer buffer3 = binaryData;
                    return;
                }
                unpackedRowData[i] = StringUtils.getBytes(String.valueOf(65535 & shortVal));
                Buffer buffer4 = binaryData;
                return;
            case 3:
            case 9:
                int intVal = (int) binaryData.readLong();
                if (!curField.isUnsigned()) {
                    unpackedRowData[i] = StringUtils.getBytes(String.valueOf(intVal));
                    Buffer buffer5 = binaryData;
                    return;
                }
                unpackedRowData[i] = StringUtils.getBytes(String.valueOf(((long) intVal) & 4294967295L));
                Buffer buffer6 = binaryData;
                return;
            case 4:
                unpackedRowData[i] = StringUtils.getBytes(String.valueOf(Float.intBitsToFloat(binaryData.readIntAsLong())));
                Buffer buffer7 = binaryData;
                return;
            case 5:
                unpackedRowData[i] = StringUtils.getBytes(String.valueOf(Double.longBitsToDouble(binaryData.readLongLong())));
                Buffer buffer8 = binaryData;
                return;
            case 6:
                Buffer buffer9 = binaryData;
                return;
            case 7:
            case 12:
                int length = (int) binaryData.readFieldLength();
                int year = 0;
                int month = 0;
                int day = 0;
                int hour = 0;
                int minute = 0;
                int seconds = 0;
                if (length != 0) {
                    year = binaryData.readInt();
                    month = binaryData.readByte();
                    day = binaryData.readByte();
                    if (length > 4) {
                        hour = binaryData.readByte();
                        minute = binaryData.readByte();
                        seconds = binaryData.readByte();
                    }
                }
                if (year == 0 && month == 0 && day == 0) {
                    if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior())) {
                        unpackedRowData[i] = null;
                        Buffer buffer10 = binaryData;
                        return;
                    } else if (!"exception".equals(this.connection.getZeroDateTimeBehavior())) {
                        year = 1;
                        month = 1;
                        day = 1;
                    } else {
                        throw SQLError.createSQLException("Value '0000-00-00' can not be represented as java.sql.Timestamp", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                    }
                }
                byte[] nanosAsBytes = StringUtils.getBytes(Integer.toString(0));
                int stringLength = 19 + nanosAsBytes.length + 1;
                byte[] datetimeAsBytes = new byte[stringLength];
                datetimeAsBytes[0] = (byte) Character.forDigit(year / 1000, 10);
                int after1000 = year % 1000;
                datetimeAsBytes[1] = (byte) Character.forDigit(after1000 / 100, 10);
                int after100 = after1000 % 100;
                int i2 = stringLength;
                datetimeAsBytes[2] = (byte) Character.forDigit(after100 / 10, 10);
                datetimeAsBytes[3] = (byte) Character.forDigit(after100 % 10, 10);
                datetimeAsBytes[4] = 45;
                datetimeAsBytes[5] = (byte) Character.forDigit(month / 10, 10);
                datetimeAsBytes[6] = (byte) Character.forDigit(month % 10, 10);
                datetimeAsBytes[7] = 45;
                datetimeAsBytes[8] = (byte) Character.forDigit(day / 10, 10);
                datetimeAsBytes[9] = (byte) Character.forDigit(day % 10, 10);
                datetimeAsBytes[10] = 32;
                datetimeAsBytes[11] = (byte) Character.forDigit(hour / 10, 10);
                datetimeAsBytes[12] = (byte) Character.forDigit(hour % 10, 10);
                datetimeAsBytes[13] = 58;
                datetimeAsBytes[14] = (byte) Character.forDigit(minute / 10, 10);
                datetimeAsBytes[15] = (byte) Character.forDigit(minute % 10, 10);
                datetimeAsBytes[16] = 58;
                datetimeAsBytes[17] = (byte) Character.forDigit(seconds / 10, 10);
                datetimeAsBytes[18] = (byte) Character.forDigit(seconds % 10, 10);
                datetimeAsBytes[19] = 46;
                int i3 = after100;
                System.arraycopy(nanosAsBytes, 0, datetimeAsBytes, 20, nanosAsBytes.length);
                unpackedRowData[i] = datetimeAsBytes;
                Buffer buffer11 = binaryData;
                return;
            case 8:
                long longVal = binaryData.readLongLong();
                if (!curField.isUnsigned()) {
                    unpackedRowData[i] = StringUtils.getBytes(String.valueOf(longVal));
                    Buffer buffer12 = binaryData;
                    return;
                }
                unpackedRowData[i] = StringUtils.getBytes(ResultSetImpl.convertLongToUlong(longVal).toString());
                Buffer buffer13 = binaryData;
                return;
            case 10:
                int year2 = 0;
                int month2 = 0;
                int day2 = 0;
                if (((int) binaryData.readFieldLength()) != 0) {
                    year2 = binaryData.readInt();
                    month2 = binaryData.readByte();
                    day2 = binaryData.readByte();
                }
                if (year2 == 0 && month2 == 0 && day2 == 0) {
                    if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior())) {
                        unpackedRowData[i] = null;
                        Buffer buffer14 = binaryData;
                        return;
                    } else if (!"exception".equals(this.connection.getZeroDateTimeBehavior())) {
                        year2 = 1;
                        month2 = 1;
                        day2 = 1;
                    } else {
                        throw SQLError.createSQLException("Value '0000-00-00' can not be represented as java.sql.Date", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                    }
                }
                int after10002 = year2 % 1000;
                int after1002 = after10002 % 100;
                unpackedRowData[i] = new byte[]{(byte) Character.forDigit(year2 / 1000, 10), (byte) Character.forDigit(after10002 / 100, 10), (byte) Character.forDigit(after1002 / 10, 10), (byte) Character.forDigit(after1002 % 10, 10), 45, (byte) Character.forDigit(month2 / 10, 10), (byte) Character.forDigit(month2 % 10, 10), 45, (byte) Character.forDigit(day2 / 10, 10), (byte) Character.forDigit(day2 % 10, 10)};
                Buffer buffer15 = binaryData;
                return;
            case 11:
                int length2 = (int) binaryData.readFieldLength();
                int hour2 = 0;
                int minute2 = 0;
                int seconds2 = 0;
                if (length2 != 0) {
                    binaryData.readByte();
                    binaryData.readLong();
                    hour2 = binaryData.readByte();
                    minute2 = binaryData.readByte();
                    seconds2 = binaryData.readByte();
                    if (length2 > 8) {
                        binaryData.readLong();
                    }
                }
                unpackedRowData[i] = new byte[]{(byte) Character.forDigit(hour2 / 10, 10), (byte) Character.forDigit(hour2 % 10, 10), 58, (byte) Character.forDigit(minute2 / 10, 10), (byte) Character.forDigit(minute2 % 10, 10), 58, (byte) Character.forDigit(seconds2 / 10, 10), (byte) Character.forDigit(seconds2 % 10, 10)};
                Buffer buffer16 = binaryData;
                return;
            default:
                Buffer buffer17 = binaryData;
                throw SQLError.createSQLException(Messages.getString("MysqlIO.97") + curField.getMysqlType() + Messages.getString("MysqlIO.98") + i + Messages.getString("MysqlIO.99") + fieldArr.length + Messages.getString("MysqlIO.100"), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
        }
    }

    private void negotiateSSLConnection(String user, String password, String database, int packLength) throws SQLException {
        if (ExportControlled.enabled()) {
            if ((this.serverCapabilities & 32768) != 0) {
                this.clientParam |= 32768;
            }
            this.clientParam |= 2048;
            Buffer packet = new Buffer(packLength);
            if (this.use41Extensions) {
                packet.writeLong(this.clientParam);
                packet.writeLong((long) this.maxThreeBytes);
                appendCharsetByteForHandshake(packet, getEncodingForHandshake());
                packet.writeBytesNoNull(new byte[23]);
            } else {
                packet.writeInt((int) this.clientParam);
            }
            send(packet, packet.getPosition());
            ExportControlled.transformSocketToSSLSocket(this);
            return;
        }
        throw new ConnectionFeatureNotAvailableException(this.connection, this.lastPacketSentTimeMs, (Exception) null);
    }

    public boolean isSSLEstablished() {
        return ExportControlled.enabled() && ExportControlled.isSSLEstablished(this.mysqlConnection);
    }

    /* access modifiers changed from: protected */
    public int getServerStatus() {
        return this.serverStatus;
    }

    /* access modifiers changed from: protected */
    public List<ResultSetRow> fetchRowsViaCursor(List<ResultSetRow> fetchedRows, long statementId, Field[] columnTypes, int fetchSize, boolean useBufferRowExplicit) throws SQLException {
        List<ResultSetRow> fetchedRows2;
        int i = fetchSize;
        if (fetchedRows == null) {
            fetchedRows2 = new ArrayList<>(i);
        } else {
            fetchedRows.clear();
            fetchedRows2 = fetchedRows;
        }
        this.sharedSendPacket.clear();
        this.sharedSendPacket.writeByte((byte) 28);
        this.sharedSendPacket.writeLong(statementId);
        this.sharedSendPacket.writeLong((long) i);
        sendCommand(28, (String) null, this.sharedSendPacket, true, (String) null, 0);
        while (true) {
            ResultSetRow nextRow = nextRow(columnTypes, columnTypes.length, true, 1007, false, useBufferRowExplicit, false, (Buffer) null);
            ResultSetRow row = nextRow;
            if (nextRow == null) {
                return fetchedRows2;
            }
            fetchedRows2.add(row);
        }
    }

    /* access modifiers changed from: protected */
    public long getThreadId() {
        return this.threadId;
    }

    /* access modifiers changed from: protected */
    public boolean useNanosForElapsedTime() {
        return this.useNanosForElapsedTime;
    }

    /* access modifiers changed from: protected */
    public long getSlowQueryThreshold() {
        return this.slowQueryThreshold;
    }

    public String getQueryTimingUnits() {
        return this.queryTimingUnits;
    }

    /* access modifiers changed from: protected */
    public int getCommandCount() {
        return this.commandCount;
    }

    private void checkTransactionState(int oldStatus) throws SQLException {
        boolean previouslyInTrans = (oldStatus & 1) != 0;
        boolean currentlyInTrans = inTransactionOnServer();
        if (previouslyInTrans && !currentlyInTrans) {
            this.connection.transactionCompleted();
        } else if (!previouslyInTrans && currentlyInTrans) {
            this.connection.transactionBegun();
        }
    }

    private void preserveOldTransactionState() {
        this.serverStatus |= this.oldServerStatus & 1;
    }

    /* access modifiers changed from: protected */
    public void setStatementInterceptors(List<StatementInterceptorV2> statementInterceptors2) {
        this.statementInterceptors = statementInterceptors2.isEmpty() ? null : statementInterceptors2;
    }

    /* access modifiers changed from: protected */
    public ExceptionInterceptor getExceptionInterceptor() {
        return this.exceptionInterceptor;
    }

    /* access modifiers changed from: protected */
    public void setSocketTimeout(int milliseconds) throws SQLException {
        try {
            Socket socket = this.mysqlConnection;
            if (socket != null) {
                socket.setSoTimeout(milliseconds);
            }
        } catch (SocketException e) {
            SQLException sqlEx = SQLError.createSQLException("Invalid socket timeout value or state", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            sqlEx.initCause(e);
            throw sqlEx;
        }
    }

    /* access modifiers changed from: protected */
    public void releaseResources() {
        Deflater deflater2 = this.deflater;
        if (deflater2 != null) {
            deflater2.end();
            this.deflater = null;
        }
    }

    /* access modifiers changed from: package-private */
    public String getEncodingForHandshake() {
        String enc = this.connection.getEncoding();
        if (enc == null) {
            return "UTF-8";
        }
        return enc;
    }

    private void appendCharsetByteForHandshake(Buffer packet, String enc) throws SQLException {
        int charsetIndex = 0;
        if (enc != null) {
            charsetIndex = CharsetMapping.getCollationIndexForJavaEncoding(enc, this.connection);
        }
        if (charsetIndex == 0) {
            charsetIndex = 33;
        }
        if (charsetIndex <= 255) {
            packet.writeByte((byte) charsetIndex);
            return;
        }
        throw SQLError.createSQLException("Invalid character set index for encoding: " + enc, SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
    }

    public boolean isEOFDeprecated() {
        return (this.clientParam & 16777216) != 0;
    }
}
