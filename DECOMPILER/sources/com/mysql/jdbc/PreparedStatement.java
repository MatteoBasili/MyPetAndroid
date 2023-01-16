package com.mysql.jdbc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

public class PreparedStatement extends StatementImpl implements java.sql.PreparedStatement {
    private static final byte[] HEX_DIGITS = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};
    private static final Constructor<?> JDBC_4_PSTMT_2_ARG_CTOR;
    private static final Constructor<?> JDBC_4_PSTMT_3_ARG_CTOR;
    private static final Constructor<?> JDBC_4_PSTMT_4_ARG_CTOR;
    protected int batchCommandIndex;
    protected boolean batchHasPlainStatements = false;
    protected String batchedValuesClause;
    private CharsetEncoder charsetEncoder;
    private boolean compensateForOnDuplicateKeyUpdate;
    private DatabaseMetaData dbmd = null;
    private SimpleDateFormat ddf;
    private boolean doPingInstead;
    protected char firstCharOfStmt = 0;
    protected boolean isLoadDataQuery = false;
    protected boolean[] isNull = null;
    private boolean[] isStream = null;
    protected int numberOfExecutions = 0;
    protected String originalSql = null;
    protected int parameterCount;
    protected MysqlParameterMetadata parameterMetaData;
    private InputStream[] parameterStreams = null;
    protected int[] parameterTypes;
    private byte[][] parameterValues;
    protected ParseInfo parseInfo;
    private ResultSetMetaData pstmtResultMetaData;
    protected int rewrittenBatchSize;
    protected boolean serverSupportsFracSecs;
    private byte[][] staticSqlStrings;
    private byte[] streamConvertBuf;
    private int[] streamLengths;
    private SimpleDateFormat tdf;
    private SimpleDateFormat tsdf;
    protected boolean useTrueBoolean;
    protected boolean usingAnsiMode;

    interface BatchVisitor {
        BatchVisitor append(byte[] bArr);

        BatchVisitor decrement();

        BatchVisitor increment();

        BatchVisitor merge(byte[] bArr, byte[] bArr2);

        BatchVisitor mergeWithLast(byte[] bArr);
    }

    static {
        if (Util.isJdbc4()) {
            try {
                String jdbc4ClassName = Util.isJdbc42() ? "com.mysql.jdbc.JDBC42PreparedStatement" : "com.mysql.jdbc.JDBC4PreparedStatement";
                JDBC_4_PSTMT_2_ARG_CTOR = Class.forName(jdbc4ClassName).getConstructor(new Class[]{MySQLConnection.class, String.class});
                JDBC_4_PSTMT_3_ARG_CTOR = Class.forName(jdbc4ClassName).getConstructor(new Class[]{MySQLConnection.class, String.class, String.class});
                JDBC_4_PSTMT_4_ARG_CTOR = Class.forName(jdbc4ClassName).getConstructor(new Class[]{MySQLConnection.class, String.class, String.class, ParseInfo.class});
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e2) {
                throw new RuntimeException(e2);
            } catch (ClassNotFoundException e3) {
                throw new RuntimeException(e3);
            }
        } else {
            JDBC_4_PSTMT_2_ARG_CTOR = null;
            JDBC_4_PSTMT_3_ARG_CTOR = null;
            JDBC_4_PSTMT_4_ARG_CTOR = null;
        }
    }

    public class BatchParams {
        public boolean[] isNull = null;
        public boolean[] isStream = null;
        public InputStream[] parameterStreams = null;
        public byte[][] parameterStrings;
        public int[] streamLengths;

        BatchParams(byte[][] strings, InputStream[] streams, boolean[] isStreamFlags, int[] lengths, boolean[] isNullFlags) {
            byte[][] bArr = null;
            this.parameterStrings = null;
            this.streamLengths = null;
            byte[][] bArr2 = new byte[strings.length][];
            this.parameterStrings = bArr2;
            this.parameterStreams = new InputStream[streams.length];
            this.isStream = new boolean[isStreamFlags.length];
            this.streamLengths = new int[lengths.length];
            this.isNull = new boolean[isNullFlags.length];
            System.arraycopy(strings, 0, bArr2, 0, strings.length);
            System.arraycopy(streams, 0, this.parameterStreams, 0, streams.length);
            System.arraycopy(isStreamFlags, 0, this.isStream, 0, isStreamFlags.length);
            System.arraycopy(lengths, 0, this.streamLengths, 0, lengths.length);
            System.arraycopy(isNullFlags, 0, this.isNull, 0, isNullFlags.length);
        }
    }

    class EndPoint {
        int begin;
        int end;

        EndPoint(int b, int e) {
            this.begin = b;
            this.end = e;
        }
    }

    public static final class ParseInfo {
        private ParseInfo batchHead;
        private ParseInfo batchODKUClause;
        private ParseInfo batchValues;
        boolean canRewriteAsMultiValueInsert;
        String charEncoding;
        char firstStmtChar;
        boolean foundLoadData;
        boolean hasPlaceholders;
        boolean isOnDuplicateKeyUpdate;
        long lastUsed;
        int locationOfOnDuplicateKeyUpdate;
        int numberOfQueries;
        boolean parametersInDuplicateKeyClause;
        int statementLength;
        int statementStartPos;
        byte[][] staticSql;
        String valuesClause;

        ParseInfo(String sql, MySQLConnection conn, DatabaseMetaData dbmd, String encoding, SingleByteCharsetConverter converter) throws SQLException {
            this(sql, conn, dbmd, encoding, converter, true);
        }

        public ParseInfo(String sql, MySQLConnection conn, DatabaseMetaData dbmd, String encoding, SingleByteCharsetConverter converter, boolean buildRewriteInfo) throws SQLException {
            char quotedIdentifierChar;
            int i;
            char c;
            char quoteChar;
            int j;
            String str = sql;
            String str2 = encoding;
            boolean z = false;
            this.firstStmtChar = 0;
            this.foundLoadData = false;
            this.lastUsed = 0;
            this.statementLength = 0;
            this.statementStartPos = 0;
            this.canRewriteAsMultiValueInsert = false;
            byte[][] bArr = null;
            this.staticSql = null;
            this.hasPlaceholders = false;
            this.numberOfQueries = 1;
            this.isOnDuplicateKeyUpdate = false;
            int i2 = -1;
            this.locationOfOnDuplicateKeyUpdate = -1;
            this.parametersInDuplicateKeyClause = false;
            if (str != null) {
                try {
                    this.charEncoding = str2;
                    this.lastUsed = System.currentTimeMillis();
                    String quotedIdentifierString = dbmd.getIdentifierQuoteString();
                    if (quotedIdentifierString == null || quotedIdentifierString.equals(" ") || quotedIdentifierString.length() <= 0) {
                        quotedIdentifierChar = 0;
                    } else {
                        quotedIdentifierChar = quotedIdentifierString.charAt(0);
                    }
                    this.statementLength = sql.length();
                    ArrayList arrayList = new ArrayList();
                    boolean noBackslashEscapes = conn.isNoBackslashEscapesSet();
                    int i3 = StatementImpl.findStartOfStatement(sql);
                    this.statementStartPos = i3;
                    boolean inQuotes = false;
                    char quoteChar2 = 0;
                    boolean inQuotedId = false;
                    int lastParmEnd = 0;
                    while (i < this.statementLength) {
                        char c2 = str.charAt(i);
                        if (this.firstStmtChar == 0 && Character.isLetter(c2)) {
                            char upperCase = Character.toUpperCase(c2);
                            this.firstStmtChar = upperCase;
                            if (upperCase == 'I') {
                                int onDuplicateKeyLocation = StatementImpl.getOnDuplicateKeyLocation(str, conn.getDontCheckOnDuplicateKeyUpdateInSQL(), conn.getRewriteBatchedStatements(), conn.isNoBackslashEscapesSet());
                                this.locationOfOnDuplicateKeyUpdate = onDuplicateKeyLocation;
                                this.isOnDuplicateKeyUpdate = onDuplicateKeyLocation != i2 ? true : z;
                            }
                        }
                        if (noBackslashEscapes || c2 != '\\' || i >= this.statementLength - 1) {
                            if (!inQuotes && quotedIdentifierChar != 0 && c2 == quotedIdentifierChar) {
                                inQuotedId = !inQuotedId ? true : z;
                            } else if (!inQuotedId) {
                                if (!inQuotes) {
                                    if (c2 != '#') {
                                        if (c2 != '-' || i + 1 >= this.statementLength || str.charAt(i + 1) != '-') {
                                            if (c2 == '/' && i + 1 < this.statementLength) {
                                                char c3 = '*';
                                                if (str.charAt(i + 1) == '*') {
                                                    i += 2;
                                                    int j2 = i;
                                                    while (true) {
                                                        if (j2 >= this.statementLength) {
                                                            break;
                                                        }
                                                        i++;
                                                        if (str.charAt(j2) == c3 && j2 + 1 < this.statementLength && str.charAt(j2 + 1) == '/') {
                                                            i++;
                                                            if (i < this.statementLength) {
                                                                c2 = str.charAt(i);
                                                            }
                                                        } else {
                                                            j2++;
                                                            c3 = '*';
                                                        }
                                                    }
                                                }
                                            } else if (c2 == '\'' || c2 == '\"') {
                                                inQuotes = true;
                                                quoteChar2 = c2;
                                            }
                                        }
                                    }
                                    int endOfStmt = this.statementLength - 1;
                                    while (true) {
                                        if (i >= endOfStmt) {
                                            break;
                                        }
                                        char c4 = str.charAt(i);
                                        if (c4 == 13) {
                                            break;
                                        } else if (c4 == 10) {
                                            break;
                                        } else {
                                            i++;
                                        }
                                    }
                                } else if ((c2 == '\'' || c2 == '\"') && c2 == quoteChar2) {
                                    if (i >= this.statementLength - 1 || str.charAt(i + 1) != quoteChar2) {
                                        inQuotes = !inQuotes ? true : z;
                                        quoteChar2 = 0;
                                    } else {
                                        i++;
                                    }
                                } else if ((c2 == '\'' || c2 == '\"') && c2 == quoteChar2) {
                                    inQuotes = !inQuotes ? true : z;
                                    quoteChar2 = 0;
                                }
                            }
                            if (!inQuotes && !inQuotedId) {
                                if (c2 == '?') {
                                    arrayList.add(new int[]{lastParmEnd, i});
                                    int lastParmEnd2 = i + 1;
                                    if (this.isOnDuplicateKeyUpdate && i > this.locationOfOnDuplicateKeyUpdate) {
                                        this.parametersInDuplicateKeyClause = true;
                                    }
                                    lastParmEnd = lastParmEnd2;
                                } else if (c2 == ';' && (j = i + 1) < this.statementLength) {
                                    while (true) {
                                        if (j >= this.statementLength) {
                                            break;
                                        } else if (!Character.isWhitespace(str.charAt(j))) {
                                            break;
                                        } else {
                                            j++;
                                        }
                                    }
                                    if (j < this.statementLength) {
                                        this.numberOfQueries++;
                                    }
                                    i = j - 1;
                                }
                            }
                        } else {
                            i++;
                        }
                        i3 = i + 1;
                        z = false;
                        i2 = -1;
                    }
                    if (this.firstStmtChar != 'L') {
                        c = 0;
                        this.foundLoadData = false;
                    } else if (StringUtils.startsWithIgnoreCaseAndWs(str, "LOAD DATA")) {
                        this.foundLoadData = true;
                        c = 0;
                    } else {
                        c = 0;
                        this.foundLoadData = false;
                    }
                    int[] iArr = new int[2];
                    iArr[c] = lastParmEnd;
                    iArr[1] = this.statementLength;
                    arrayList.add(iArr);
                    byte[][] bArr2 = new byte[arrayList.size()][];
                    this.staticSql = bArr2;
                    this.hasPlaceholders = bArr2.length > 1;
                    int i4 = 0;
                    while (i4 < this.staticSql.length) {
                        int[] ep = (int[]) arrayList.get(i4);
                        int end = ep[1];
                        int begin = ep[0];
                        int len = end - begin;
                        if (this.foundLoadData) {
                            this.staticSql[i4] = StringUtils.getBytes(str, begin, len);
                            quoteChar = quoteChar2;
                        } else if (str2 == null) {
                            byte[] buf = new byte[len];
                            for (int j3 = 0; j3 < len; j3++) {
                                buf[j3] = (byte) str.charAt(begin + j3);
                            }
                            this.staticSql[i4] = buf;
                            quoteChar = quoteChar2;
                        } else if (converter != null) {
                            int i5 = len;
                            int i6 = begin;
                            quoteChar = quoteChar2;
                            this.staticSql[i4] = StringUtils.getBytes(sql, converter, encoding, conn.getServerCharset(), begin, len, conn.parserKnowsUnicode(), conn.getExceptionInterceptor());
                        } else {
                            quoteChar = quoteChar2;
                            this.staticSql[i4] = StringUtils.getBytes(sql, encoding, conn.getServerCharset(), begin, len, conn.parserKnowsUnicode(), conn, conn.getExceptionInterceptor());
                        }
                        i4++;
                        quoteChar2 = quoteChar;
                    }
                    if (buildRewriteInfo) {
                        boolean z2 = this.numberOfQueries == 1 && !this.parametersInDuplicateKeyClause && PreparedStatement.canRewrite(str, this.isOnDuplicateKeyUpdate, this.locationOfOnDuplicateKeyUpdate, this.statementStartPos);
                        this.canRewriteAsMultiValueInsert = z2;
                        if (z2 && conn.getRewriteBatchedStatements()) {
                            buildRewriteBatchedParams(sql, conn, dbmd, encoding, converter);
                        }
                    }
                } catch (StringIndexOutOfBoundsException oobEx) {
                    SQLException sqlEx = new SQLException("Parse error for " + str);
                    sqlEx.initCause(oobEx);
                    throw sqlEx;
                }
            } else {
                throw SQLError.createSQLException(Messages.getString("PreparedStatement.61"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, conn.getExceptionInterceptor());
            }
        }

        private void buildRewriteBatchedParams(String sql, MySQLConnection conn, DatabaseMetaData metadata, String encoding, SingleByteCharsetConverter converter) throws SQLException {
            String headSql;
            String str = sql;
            this.valuesClause = extractValuesClause(str, conn.getMetaData().getIdentifierQuoteString());
            String odkuClause = this.isOnDuplicateKeyUpdate ? str.substring(this.locationOfOnDuplicateKeyUpdate) : null;
            if (this.isOnDuplicateKeyUpdate) {
                headSql = str.substring(0, this.locationOfOnDuplicateKeyUpdate);
            } else {
                headSql = sql;
            }
            this.batchHead = new ParseInfo(headSql, conn, metadata, encoding, converter, false);
            this.batchValues = new ParseInfo("," + this.valuesClause, conn, metadata, encoding, converter, false);
            this.batchODKUClause = null;
            if (odkuClause != null && odkuClause.length() > 0) {
                this.batchODKUClause = new ParseInfo("," + this.valuesClause + " " + odkuClause, conn, metadata, encoding, converter, false);
            }
        }

        private String extractValuesClause(String sql, String quoteCharStr) throws SQLException {
            int indexOfValues = -1;
            int valuesSearchStart = this.statementStartPos;
            while (indexOfValues == -1) {
                if (quoteCharStr.length() > 0) {
                    indexOfValues = StringUtils.indexOfIgnoreCase(valuesSearchStart, sql, "VALUE", quoteCharStr, quoteCharStr, StringUtils.SEARCH_MODE__MRK_COM_WS);
                } else {
                    indexOfValues = StringUtils.indexOfIgnoreCase(valuesSearchStart, sql, "VALUE");
                }
                if (indexOfValues <= 0) {
                    break;
                }
                char c = sql.charAt(indexOfValues - 1);
                if (Character.isWhitespace(c) || c == ')' || c == '`') {
                    char c2 = sql.charAt(indexOfValues + 6);
                    if (!Character.isWhitespace(c2) && c2 != '(') {
                        int valuesSearchStart2 = indexOfValues + 6;
                        indexOfValues = -1;
                        valuesSearchStart = valuesSearchStart2;
                    }
                } else {
                    int valuesSearchStart3 = indexOfValues + 6;
                    indexOfValues = -1;
                    valuesSearchStart = valuesSearchStart3;
                }
            }
            if (indexOfValues == -1) {
                return null;
            }
            int indexOfFirstParen = sql.indexOf(40, indexOfValues + ((sql.length() <= indexOfValues + 5 || Character.toUpperCase(sql.charAt(indexOfValues + 5)) != 'S') ? 5 : 6));
            if (indexOfFirstParen == -1) {
                return null;
            }
            return sql.substring(indexOfFirstParen, this.isOnDuplicateKeyUpdate ? this.locationOfOnDuplicateKeyUpdate : sql.length());
        }

        /* access modifiers changed from: package-private */
        public synchronized ParseInfo getParseInfoForBatch(int numBatch) {
            AppendingBatchVisitor apv;
            apv = new AppendingBatchVisitor();
            buildInfoForBatch(numBatch, apv);
            return new ParseInfo(apv.getStaticSqlStrings(), this.firstStmtChar, this.foundLoadData, this.isOnDuplicateKeyUpdate, this.locationOfOnDuplicateKeyUpdate, this.statementLength, this.statementStartPos);
        }

        /* access modifiers changed from: package-private */
        public String getSqlForBatch(int numBatch) throws UnsupportedEncodingException {
            return getSqlForBatch(getParseInfoForBatch(numBatch));
        }

        /* access modifiers changed from: package-private */
        public String getSqlForBatch(ParseInfo batchInfo) throws UnsupportedEncodingException {
            int size = 0;
            byte[][] sqlStrings = batchInfo.staticSql;
            for (byte[] length : sqlStrings) {
                size = size + length.length + 1;
            }
            StringBuilder buf = new StringBuilder(size);
            for (int i = 0; i < sqlStringsLength - 1; i++) {
                buf.append(StringUtils.toString(sqlStrings[i], this.charEncoding));
                buf.append("?");
            }
            buf.append(StringUtils.toString(sqlStrings[sqlStringsLength - 1]));
            return buf.toString();
        }

        private void buildInfoForBatch(int numBatch, BatchVisitor visitor) {
            int i = numBatch;
            BatchVisitor batchVisitor = visitor;
            if (this.hasPlaceholders) {
                byte[][] headStaticSql = this.batchHead.staticSql;
                int headStaticSqlLength = headStaticSql.length;
                byte[] endOfHead = headStaticSql[headStaticSqlLength - 1];
                for (int i2 = 0; i2 < headStaticSqlLength - 1; i2++) {
                    batchVisitor.append(headStaticSql[i2]).increment();
                }
                int numValueRepeats = i - 1;
                if (this.batchODKUClause != null) {
                    numValueRepeats--;
                }
                byte[][] valuesStaticSql = this.batchValues.staticSql;
                int valuesStaticSqlLength = valuesStaticSql.length;
                byte[] beginOfValues = valuesStaticSql[0];
                byte[] endOfValues = valuesStaticSql[valuesStaticSqlLength - 1];
                for (int i3 = 0; i3 < numValueRepeats; i3++) {
                    batchVisitor.merge(endOfValues, beginOfValues).increment();
                    for (int j = 1; j < valuesStaticSqlLength - 1; j++) {
                        batchVisitor.append(valuesStaticSql[j]).increment();
                    }
                }
                ParseInfo parseInfo = this.batchODKUClause;
                if (parseInfo != null) {
                    byte[][] batchOdkuStaticSql = parseInfo.staticSql;
                    int batchOdkuStaticSqlLength = batchOdkuStaticSql.length;
                    byte[] beginOfOdku = batchOdkuStaticSql[0];
                    byte[] endOfOdku = batchOdkuStaticSql[batchOdkuStaticSqlLength - 1];
                    if (i > 1) {
                        batchVisitor.merge(numValueRepeats > 0 ? endOfValues : endOfHead, beginOfOdku).increment();
                        int i4 = 1;
                        while (i4 < batchOdkuStaticSqlLength) {
                            batchVisitor.append(batchOdkuStaticSql[i4]).increment();
                            i4++;
                        }
                        return;
                    }
                    batchVisitor.append(endOfOdku).increment();
                    return;
                }
                batchVisitor.append(endOfHead);
            } else if (i == 1) {
                batchVisitor.append(this.staticSql[0]);
            } else {
                batchVisitor.append(this.batchHead.staticSql[0]).increment();
                int numValueRepeats2 = i - 1;
                if (this.batchODKUClause != null) {
                    numValueRepeats2--;
                }
                byte[] valuesStaticSql2 = this.batchValues.staticSql[0];
                for (int i5 = 0; i5 < numValueRepeats2; i5++) {
                    batchVisitor.mergeWithLast(valuesStaticSql2).increment();
                }
                ParseInfo parseInfo2 = this.batchODKUClause;
                if (parseInfo2 != null) {
                    batchVisitor.mergeWithLast(parseInfo2.staticSql[0]).increment();
                }
            }
        }

        private ParseInfo(byte[][] staticSql2, char firstStmtChar2, boolean foundLoadData2, boolean isOnDuplicateKeyUpdate2, int locationOfOnDuplicateKeyUpdate2, int statementLength2, int statementStartPos2) {
            this.firstStmtChar = 0;
            this.foundLoadData = false;
            this.lastUsed = 0;
            this.statementLength = 0;
            this.statementStartPos = 0;
            this.canRewriteAsMultiValueInsert = false;
            byte[][] bArr = null;
            this.staticSql = null;
            this.hasPlaceholders = false;
            this.numberOfQueries = 1;
            this.isOnDuplicateKeyUpdate = false;
            this.locationOfOnDuplicateKeyUpdate = -1;
            this.parametersInDuplicateKeyClause = false;
            this.firstStmtChar = firstStmtChar2;
            this.foundLoadData = foundLoadData2;
            this.isOnDuplicateKeyUpdate = isOnDuplicateKeyUpdate2;
            this.locationOfOnDuplicateKeyUpdate = locationOfOnDuplicateKeyUpdate2;
            this.statementLength = statementLength2;
            this.statementStartPos = statementStartPos2;
            this.staticSql = staticSql2;
        }
    }

    static class AppendingBatchVisitor implements BatchVisitor {
        LinkedList<byte[]> statementComponents = new LinkedList<>();

        AppendingBatchVisitor() {
        }

        public BatchVisitor append(byte[] values) {
            this.statementComponents.addLast(values);
            return this;
        }

        public BatchVisitor increment() {
            return this;
        }

        public BatchVisitor decrement() {
            this.statementComponents.removeLast();
            return this;
        }

        public BatchVisitor merge(byte[] front, byte[] back) {
            byte[] merged = new byte[(front.length + back.length)];
            System.arraycopy(front, 0, merged, 0, front.length);
            System.arraycopy(back, 0, merged, front.length, back.length);
            this.statementComponents.addLast(merged);
            return this;
        }

        public BatchVisitor mergeWithLast(byte[] values) {
            if (this.statementComponents.isEmpty()) {
                return append(values);
            }
            return merge(this.statementComponents.removeLast(), values);
        }

        public byte[][] getStaticSqlStrings() {
            byte[][] asBytes = new byte[this.statementComponents.size()][];
            this.statementComponents.toArray(asBytes);
            return asBytes;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            Iterator i$ = this.statementComponents.iterator();
            while (i$.hasNext()) {
                sb.append(StringUtils.toString((byte[]) i$.next()));
            }
            return sb.toString();
        }
    }

    protected static int readFully(Reader reader, char[] buf, int length) throws IOException {
        int numCharsRead = 0;
        while (numCharsRead < length) {
            int count = reader.read(buf, numCharsRead, length - numCharsRead);
            if (count < 0) {
                break;
            }
            numCharsRead += count;
        }
        return numCharsRead;
    }

    protected static PreparedStatement getInstance(MySQLConnection conn, String catalog) throws SQLException {
        if (!Util.isJdbc4()) {
            return new PreparedStatement(conn, catalog);
        }
        return (PreparedStatement) Util.handleNewInstance(JDBC_4_PSTMT_2_ARG_CTOR, new Object[]{conn, catalog}, conn.getExceptionInterceptor());
    }

    protected static PreparedStatement getInstance(MySQLConnection conn, String sql, String catalog) throws SQLException {
        if (!Util.isJdbc4()) {
            return new PreparedStatement(conn, sql, catalog);
        }
        return (PreparedStatement) Util.handleNewInstance(JDBC_4_PSTMT_3_ARG_CTOR, new Object[]{conn, sql, catalog}, conn.getExceptionInterceptor());
    }

    protected static PreparedStatement getInstance(MySQLConnection conn, String sql, String catalog, ParseInfo cachedParseInfo) throws SQLException {
        if (!Util.isJdbc4()) {
            return new PreparedStatement(conn, sql, catalog, cachedParseInfo);
        }
        return (PreparedStatement) Util.handleNewInstance(JDBC_4_PSTMT_4_ARG_CTOR, new Object[]{conn, sql, catalog, cachedParseInfo}, conn.getExceptionInterceptor());
    }

    public PreparedStatement(MySQLConnection conn, String catalog) throws SQLException {
        super(conn, catalog);
        byte[][] bArr = null;
        this.parameterValues = null;
        this.parameterTypes = null;
        this.staticSqlStrings = null;
        this.streamConvertBuf = null;
        this.streamLengths = null;
        this.tsdf = null;
        this.useTrueBoolean = false;
        this.compensateForOnDuplicateKeyUpdate = false;
        this.batchCommandIndex = -1;
        this.rewrittenBatchSize = 0;
        detectFractionalSecondsSupport();
        this.compensateForOnDuplicateKeyUpdate = this.connection.getCompensateOnDuplicateKeyUpdateCounts();
    }

    /* access modifiers changed from: protected */
    public void detectFractionalSecondsSupport() throws SQLException {
        this.serverSupportsFracSecs = this.connection != null && this.connection.versionMeetsMinimum(5, 6, 4);
    }

    public PreparedStatement(MySQLConnection conn, String sql, String catalog) throws SQLException {
        super(conn, catalog);
        byte[][] bArr = null;
        this.parameterValues = null;
        this.parameterTypes = null;
        this.staticSqlStrings = null;
        this.streamConvertBuf = null;
        this.streamLengths = null;
        this.tsdf = null;
        this.useTrueBoolean = false;
        this.compensateForOnDuplicateKeyUpdate = false;
        this.batchCommandIndex = -1;
        this.rewrittenBatchSize = 0;
        if (sql != null) {
            detectFractionalSecondsSupport();
            this.originalSql = sql;
            this.doPingInstead = sql.startsWith("/* ping */");
            this.dbmd = this.connection.getMetaData();
            this.useTrueBoolean = this.connection.versionMeetsMinimum(3, 21, 23);
            this.parseInfo = new ParseInfo(sql, this.connection, this.dbmd, this.charEncoding, this.charConverter);
            initializeFromParseInfo();
            this.compensateForOnDuplicateKeyUpdate = this.connection.getCompensateOnDuplicateKeyUpdateCounts();
            if (conn.getRequiresEscapingEncoder()) {
                this.charsetEncoder = Charset.forName(conn.getEncoding()).newEncoder();
                return;
            }
            return;
        }
        throw SQLError.createSQLException(Messages.getString("PreparedStatement.0"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
    }

    public PreparedStatement(MySQLConnection conn, String sql, String catalog, ParseInfo cachedParseInfo) throws SQLException {
        super(conn, catalog);
        byte[][] bArr = null;
        this.parameterValues = null;
        this.parameterTypes = null;
        this.staticSqlStrings = null;
        this.streamConvertBuf = null;
        this.streamLengths = null;
        this.tsdf = null;
        this.useTrueBoolean = false;
        this.compensateForOnDuplicateKeyUpdate = false;
        this.batchCommandIndex = -1;
        this.rewrittenBatchSize = 0;
        if (sql != null) {
            detectFractionalSecondsSupport();
            this.originalSql = sql;
            this.dbmd = this.connection.getMetaData();
            this.useTrueBoolean = this.connection.versionMeetsMinimum(3, 21, 23);
            this.parseInfo = cachedParseInfo;
            this.usingAnsiMode = !this.connection.useAnsiQuotedIdentifiers();
            initializeFromParseInfo();
            this.compensateForOnDuplicateKeyUpdate = this.connection.getCompensateOnDuplicateKeyUpdateCounts();
            if (conn.getRequiresEscapingEncoder()) {
                this.charsetEncoder = Charset.forName(conn.getEncoding()).newEncoder();
                return;
            }
            return;
        }
        throw SQLError.createSQLException(Messages.getString("PreparedStatement.1"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
    }

    public void addBatch() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.batchedArgs == null) {
                this.batchedArgs = new ArrayList();
            }
            int i = 0;
            while (true) {
                byte[][] bArr = this.parameterValues;
                if (i < bArr.length) {
                    checkAllParametersSet(bArr[i], this.parameterStreams[i], i);
                    i++;
                } else {
                    this.batchedArgs.add(new BatchParams(this.parameterValues, this.parameterStreams, this.isStream, this.streamLengths, this.isNull));
                }
            }
        }
    }

    public void addBatch(String sql) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            this.batchHasPlainStatements = true;
            super.addBatch(sql);
        }
    }

    public String asSql() throws SQLException {
        return asSql(false);
    }

    public String asSql(boolean quoteStreamsAndUnknowns) throws SQLException {
        String sb;
        byte[] val;
        boolean isStreamParam;
        synchronized (checkClosed().getConnectionMutex()) {
            StringBuilder buf = new StringBuilder();
            try {
                int realParameterCount = this.parameterCount + getParameterIndexOffset();
                Object batchArg = null;
                if (this.batchCommandIndex != -1) {
                    batchArg = this.batchedArgs.get(this.batchCommandIndex);
                }
                for (int i = 0; i < realParameterCount; i++) {
                    if (this.charEncoding != null) {
                        buf.append(StringUtils.toString(this.staticSqlStrings[i], this.charEncoding));
                    } else {
                        buf.append(StringUtils.toString(this.staticSqlStrings[i]));
                    }
                    if (batchArg == null || !(batchArg instanceof String)) {
                        if (this.batchCommandIndex == -1) {
                            val = this.parameterValues[i];
                        } else {
                            val = ((BatchParams) batchArg).parameterStrings[i];
                        }
                        if (this.batchCommandIndex == -1) {
                            isStreamParam = this.isStream[i];
                        } else {
                            isStreamParam = ((BatchParams) batchArg).isStream[i];
                        }
                        if (val == null && !isStreamParam) {
                            if (quoteStreamsAndUnknowns) {
                                buf.append("'");
                            }
                            buf.append("** NOT SPECIFIED **");
                            if (quoteStreamsAndUnknowns) {
                                buf.append("'");
                            }
                        } else if (isStreamParam) {
                            if (quoteStreamsAndUnknowns) {
                                buf.append("'");
                            }
                            buf.append("** STREAM DATA **");
                            if (quoteStreamsAndUnknowns) {
                                buf.append("'");
                            }
                        } else if (this.charConverter != null) {
                            buf.append(this.charConverter.toString(val));
                        } else if (this.charEncoding != null) {
                            buf.append(new String(val, this.charEncoding));
                        } else {
                            buf.append(StringUtils.toAsciiString(val));
                        }
                    } else {
                        buf.append((String) batchArg);
                    }
                }
                if (this.charEncoding != null) {
                    buf.append(StringUtils.toString(this.staticSqlStrings[this.parameterCount + getParameterIndexOffset()], this.charEncoding));
                } else {
                    buf.append(StringUtils.toAsciiString(this.staticSqlStrings[this.parameterCount + getParameterIndexOffset()]));
                }
                sb = buf.toString();
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(Messages.getString("PreparedStatement.32") + this.charEncoding + Messages.getString("PreparedStatement.33"));
            }
        }
        return sb;
    }

    public void clearBatch() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            this.batchHasPlainStatements = false;
            super.clearBatch();
        }
    }

    public void clearParameters() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            int i = 0;
            while (true) {
                byte[][] bArr = this.parameterValues;
                if (i < bArr.length) {
                    bArr[i] = null;
                    this.parameterStreams[i] = null;
                    this.isStream[i] = false;
                    this.isNull[i] = false;
                    this.parameterTypes[i] = 0;
                    i++;
                }
            }
        }
    }

    private final void escapeblockFast(byte[] buf, Buffer packet, int size) throws SQLException {
        int lastwritten = 0;
        for (int i = 0; i < size; i++) {
            byte b = buf[i];
            if (b == 0) {
                if (i > lastwritten) {
                    packet.writeBytesNoNull(buf, lastwritten, i - lastwritten);
                }
                packet.writeByte((byte) 92);
                packet.writeByte((byte) 48);
                lastwritten = i + 1;
            } else if (b == 92 || b == 39 || (!this.usingAnsiMode && b == 34)) {
                if (i > lastwritten) {
                    packet.writeBytesNoNull(buf, lastwritten, i - lastwritten);
                }
                packet.writeByte((byte) 92);
                lastwritten = i;
            }
        }
        if (lastwritten < size) {
            packet.writeBytesNoNull(buf, lastwritten, size - lastwritten);
        }
    }

    private final void escapeblockFast(byte[] buf, ByteArrayOutputStream bytesOut, int size) {
        int lastwritten = 0;
        for (int i = 0; i < size; i++) {
            byte b = buf[i];
            int i2 = 92;
            if (b == 0) {
                if (i > lastwritten) {
                    bytesOut.write(buf, lastwritten, i - lastwritten);
                }
                bytesOut.write(92);
                bytesOut.write(48);
                lastwritten = i + 1;
            } else if (b == 39) {
                if (i > lastwritten) {
                    bytesOut.write(buf, lastwritten, i - lastwritten);
                }
                if (this.connection.isNoBackslashEscapesSet()) {
                    i2 = 39;
                }
                bytesOut.write(i2);
                lastwritten = i;
            } else if (b == 92 || (!this.usingAnsiMode && b == 34)) {
                if (i > lastwritten) {
                    bytesOut.write(buf, lastwritten, i - lastwritten);
                }
                bytesOut.write(92);
                lastwritten = i;
            }
        }
        if (lastwritten < size) {
            bytesOut.write(buf, lastwritten, size - lastwritten);
        }
    }

    /* access modifiers changed from: protected */
    public boolean checkReadOnlySafeStatement() throws SQLException {
        boolean z;
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.firstCharOfStmt != 'S') {
                if (this.connection.isReadOnly()) {
                    z = false;
                }
            }
            z = true;
        }
        return z;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:63:0x011d, code lost:
        return r18;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean execute() throws java.sql.SQLException {
        /*
            r19 = this;
            r8 = r19
            com.mysql.jdbc.MySQLConnection r0 = r19.checkClosed()
            java.lang.Object r9 = r0.getConnectionMutex()
            monitor-enter(r9)
            com.mysql.jdbc.MySQLConnection r0 = r8.connection     // Catch:{ all -> 0x011e }
            boolean r1 = r8.doPingInstead     // Catch:{ all -> 0x011e }
            if (r1 != 0) goto L_0x0040
            boolean r1 = r19.checkReadOnlySafeStatement()     // Catch:{ all -> 0x011e }
            if (r1 == 0) goto L_0x0018
            goto L_0x0040
        L_0x0018:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x011e }
            r1.<init>()     // Catch:{ all -> 0x011e }
            java.lang.String r2 = "PreparedStatement.20"
            java.lang.String r2 = com.mysql.jdbc.Messages.getString(r2)     // Catch:{ all -> 0x011e }
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x011e }
            java.lang.String r2 = "PreparedStatement.21"
            java.lang.String r2 = com.mysql.jdbc.Messages.getString(r2)     // Catch:{ all -> 0x011e }
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x011e }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x011e }
            java.lang.String r2 = "S1009"
            com.mysql.jdbc.ExceptionInterceptor r3 = r19.getExceptionInterceptor()     // Catch:{ all -> 0x011e }
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (com.mysql.jdbc.ExceptionInterceptor) r3)     // Catch:{ all -> 0x011e }
            throw r1     // Catch:{ all -> 0x011e }
        L_0x0040:
            r10 = 0
            r11 = 0
            r8.lastQueryIsOnDupKeyUpdate = r11     // Catch:{ all -> 0x011e }
            boolean r1 = r8.retrieveGeneratedKeys     // Catch:{ all -> 0x011e }
            if (r1 == 0) goto L_0x004e
            boolean r1 = r19.containsOnDuplicateKeyUpdateInSQL()     // Catch:{ all -> 0x011e }
            r8.lastQueryIsOnDupKeyUpdate = r1     // Catch:{ all -> 0x011e }
        L_0x004e:
            r12 = 0
            r8.batchedGeneratedKeys = r12     // Catch:{ all -> 0x011e }
            r19.resetCancelledState()     // Catch:{ all -> 0x011e }
            r19.implicitlyCloseAllOpenResults()     // Catch:{ all -> 0x011e }
            r19.clearWarnings()     // Catch:{ all -> 0x011e }
            boolean r1 = r8.doPingInstead     // Catch:{ all -> 0x011e }
            r13 = 1
            if (r1 == 0) goto L_0x0064
            r19.doPingInstead()     // Catch:{ all -> 0x011e }
            monitor-exit(r9)     // Catch:{ all -> 0x011e }
            return r13
        L_0x0064:
            r8.setupStreamingTimeout(r0)     // Catch:{ all -> 0x011e }
            com.mysql.jdbc.Buffer r3 = r19.fillSendPacket()     // Catch:{ all -> 0x011e }
            r1 = 0
            java.lang.String r2 = r0.getCatalog()     // Catch:{ all -> 0x011e }
            java.lang.String r4 = r8.currentCatalog     // Catch:{ all -> 0x011e }
            boolean r2 = r2.equals(r4)     // Catch:{ all -> 0x011e }
            if (r2 != 0) goto L_0x0084
            java.lang.String r2 = r0.getCatalog()     // Catch:{ all -> 0x011e }
            r1 = r2
            java.lang.String r2 = r8.currentCatalog     // Catch:{ all -> 0x011e }
            r0.setCatalog(r2)     // Catch:{ all -> 0x011e }
            r14 = r1
            goto L_0x0085
        L_0x0084:
            r14 = r1
        L_0x0085:
            r1 = 0
            boolean r2 = r0.getCacheResultSetMetadata()     // Catch:{ all -> 0x011e }
            if (r2 == 0) goto L_0x0095
            java.lang.String r2 = r8.originalSql     // Catch:{ all -> 0x011e }
            com.mysql.jdbc.CachedResultSetMetaData r2 = r0.getCachedMetaData(r2)     // Catch:{ all -> 0x011e }
            r1 = r2
            r15 = r1
            goto L_0x0096
        L_0x0095:
            r15 = r1
        L_0x0096:
            r1 = 0
            if (r15 == 0) goto L_0x009f
            com.mysql.jdbc.Field[] r2 = r15.fields     // Catch:{ all -> 0x011e }
            r1 = r2
            r16 = r1
            goto L_0x00a1
        L_0x009f:
            r16 = r1
        L_0x00a1:
            r1 = 0
            boolean r2 = r8.retrieveGeneratedKeys     // Catch:{ all -> 0x011e }
            if (r2 == 0) goto L_0x00b0
            boolean r2 = r0.isReadInfoMsgEnabled()     // Catch:{ all -> 0x011e }
            r1 = r2
            r0.setReadInfoMsgEnabled(r13)     // Catch:{ all -> 0x011e }
            r7 = r1
            goto L_0x00b1
        L_0x00b0:
            r7 = r1
        L_0x00b1:
            char r1 = r8.firstCharOfStmt     // Catch:{ all -> 0x011e }
            r2 = 83
            if (r1 != r2) goto L_0x00ba
            int r1 = r8.maxRows     // Catch:{ all -> 0x011e }
            goto L_0x00bb
        L_0x00ba:
            r1 = -1
        L_0x00bb:
            r0.setSessionMaxRows(r1)     // Catch:{ all -> 0x011e }
            int r4 = r8.maxRows     // Catch:{ all -> 0x011e }
            boolean r5 = r19.createStreamingResultSet()     // Catch:{ all -> 0x011e }
            char r1 = r8.firstCharOfStmt     // Catch:{ all -> 0x011e }
            if (r1 != r2) goto L_0x00ca
            r6 = r13
            goto L_0x00cb
        L_0x00ca:
            r6 = r11
        L_0x00cb:
            r17 = 0
            r1 = r19
            r2 = r4
            r4 = r5
            r5 = r6
            r6 = r16
            r11 = r7
            r7 = r17
            com.mysql.jdbc.ResultSetInternalMethods r1 = r1.executeInternal(r2, r3, r4, r5, r6, r7)     // Catch:{ all -> 0x011e }
            if (r15 == 0) goto L_0x00e3
            java.lang.String r2 = r8.originalSql     // Catch:{ all -> 0x011e }
            r0.initializeResultsMetadataFromCache(r2, r15, r1)     // Catch:{ all -> 0x011e }
            goto L_0x00f4
        L_0x00e3:
            boolean r2 = r1.reallyResult()     // Catch:{ all -> 0x011e }
            if (r2 == 0) goto L_0x00f4
            boolean r2 = r0.getCacheResultSetMetadata()     // Catch:{ all -> 0x011e }
            if (r2 == 0) goto L_0x00f4
            java.lang.String r2 = r8.originalSql     // Catch:{ all -> 0x011e }
            r0.initializeResultsMetadataFromCache(r2, r12, r1)     // Catch:{ all -> 0x011e }
        L_0x00f4:
            boolean r2 = r8.retrieveGeneratedKeys     // Catch:{ all -> 0x011e }
            if (r2 == 0) goto L_0x0100
            r0.setReadInfoMsgEnabled(r11)     // Catch:{ all -> 0x011e }
            char r2 = r8.firstCharOfStmt     // Catch:{ all -> 0x011e }
            r1.setFirstCharOfQuery(r2)     // Catch:{ all -> 0x011e }
        L_0x0100:
            if (r14 == 0) goto L_0x0105
            r0.setCatalog(r14)     // Catch:{ all -> 0x011e }
        L_0x0105:
            if (r1 == 0) goto L_0x010f
            long r4 = r1.getUpdateID()     // Catch:{ all -> 0x011e }
            r8.lastInsertId = r4     // Catch:{ all -> 0x011e }
            r8.results = r1     // Catch:{ all -> 0x011e }
        L_0x010f:
            if (r1 == 0) goto L_0x011a
            boolean r2 = r1.reallyResult()     // Catch:{ all -> 0x011e }
            if (r2 == 0) goto L_0x011a
            r18 = r13
            goto L_0x011c
        L_0x011a:
            r18 = 0
        L_0x011c:
            monitor-exit(r9)     // Catch:{ all -> 0x011e }
            return r18
        L_0x011e:
            r0 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x011e }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.PreparedStatement.execute():boolean");
    }

    /* access modifiers changed from: protected */
    public long[] executeBatchInternal() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.connection.isReadOnly()) {
                if (this.batchedArgs != null) {
                    if (this.batchedArgs.size() != 0) {
                        int batchTimeout = this.timeoutInMillis;
                        this.timeoutInMillis = 0;
                        resetCancelledState();
                        try {
                            statementBegins();
                            clearWarnings();
                            if (!this.batchHasPlainStatements && this.connection.getRewriteBatchedStatements()) {
                                if (canRewriteAsMultiValueInsertAtSqlLevel()) {
                                    long[] executeBatchedInserts = executeBatchedInserts(batchTimeout);
                                    return executeBatchedInserts;
                                } else if (this.connection.versionMeetsMinimum(4, 1, 0) && !this.batchHasPlainStatements && this.batchedArgs != null && this.batchedArgs.size() > 3) {
                                    long[] executePreparedBatchAsMultiStatement = executePreparedBatchAsMultiStatement(batchTimeout);
                                    this.statementExecuting.set(false);
                                    clearBatch();
                                    return executePreparedBatchAsMultiStatement;
                                }
                            }
                            long[] executeBatchSerially = executeBatchSerially(batchTimeout);
                            this.statementExecuting.set(false);
                            clearBatch();
                            return executeBatchSerially;
                        } finally {
                            this.statementExecuting.set(false);
                            clearBatch();
                        }
                    }
                }
                long[] jArr = new long[0];
                return jArr;
            }
            throw new SQLException(Messages.getString("PreparedStatement.25") + Messages.getString("PreparedStatement.26"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT);
        }
    }

    public boolean canRewriteAsMultiValueInsertAtSqlLevel() throws SQLException {
        return this.parseInfo.canRewriteAsMultiValueInsert;
    }

    /* access modifiers changed from: protected */
    public int getLocationOfOnDuplicateKeyUpdate() throws SQLException {
        return this.parseInfo.locationOfOnDuplicateKeyUpdate;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: com.mysql.jdbc.StatementImpl$CancelTask} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v5, resolved type: com.mysql.jdbc.StatementImpl$CancelTask} */
    /* JADX WARNING: type inference failed for: r6v0 */
    /* JADX WARNING: type inference failed for: r6v2 */
    /* JADX WARNING: type inference failed for: r6v3, types: [com.mysql.jdbc.StatementImpl$CancelTask] */
    /* JADX WARNING: type inference failed for: r6v4 */
    /* JADX WARNING: type inference failed for: r6v6 */
    /* JADX WARNING: type inference failed for: r6v7 */
    /* JADX WARNING: type inference failed for: r6v8 */
    /* JADX WARNING: type inference failed for: r6v17 */
    /* JADX WARNING: type inference failed for: r6v18 */
    /* JADX WARNING: type inference failed for: r6v19 */
    /* JADX WARNING: type inference failed for: r6v20 */
    /* JADX WARNING: type inference failed for: r6v21 */
    /* JADX WARNING: type inference failed for: r6v22 */
    /* JADX WARNING: type inference failed for: r6v23 */
    /* JADX WARNING: type inference failed for: r6v24 */
    /* JADX WARNING: type inference failed for: r6v25 */
    /* JADX WARNING: type inference failed for: r6v26 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x0222 A[SYNTHETIC, Splitter:B:153:0x0222] */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x024a A[Catch:{ all -> 0x0228, all -> 0x0047 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long[] executePreparedBatchAsMultiStatement(int r21) throws java.sql.SQLException {
        /*
            r20 = this;
            r1 = r20
            r2 = r21
            com.mysql.jdbc.MySQLConnection r0 = r20.checkClosed()
            java.lang.Object r3 = r0.getConnectionMutex()
            monitor-enter(r3)
            java.lang.String r0 = r1.batchedValuesClause     // Catch:{ all -> 0x0270 }
            if (r0 != 0) goto L_0x0028
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0270 }
            r0.<init>()     // Catch:{ all -> 0x0270 }
            java.lang.String r4 = r1.originalSql     // Catch:{ all -> 0x0270 }
            java.lang.StringBuilder r0 = r0.append(r4)     // Catch:{ all -> 0x0270 }
            java.lang.String r4 = ";"
            java.lang.StringBuilder r0 = r0.append(r4)     // Catch:{ all -> 0x0270 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0270 }
            r1.batchedValuesClause = r0     // Catch:{ all -> 0x0270 }
        L_0x0028:
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ all -> 0x0270 }
            r4 = r0
            boolean r0 = r4.getAllowMultiQueries()     // Catch:{ all -> 0x0270 }
            r5 = r0
            r6 = 0
            r20.clearWarnings()     // Catch:{ all -> 0x0250 }
            java.util.List r0 = r1.batchedArgs     // Catch:{ all -> 0x0250 }
            int r0 = r0.size()     // Catch:{ all -> 0x0250 }
            r7 = r0
            boolean r0 = r1.retrieveGeneratedKeys     // Catch:{ all -> 0x0250 }
            if (r0 == 0) goto L_0x004a
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x0047 }
            r0.<init>(r7)     // Catch:{ all -> 0x0047 }
            r1.batchedGeneratedKeys = r0     // Catch:{ all -> 0x0047 }
            goto L_0x004a
        L_0x0047:
            r0 = move-exception
            goto L_0x0253
        L_0x004a:
            int r0 = r1.computeBatchSize(r7)     // Catch:{ all -> 0x0250 }
            if (r7 >= r0) goto L_0x0053
            r0 = r7
            r8 = r0
            goto L_0x0054
        L_0x0053:
            r8 = r0
        L_0x0054:
            r9 = 0
            r10 = 1
            r11 = 0
            r12 = 0
            r13 = 0
            com.mysql.jdbc.PreparedStatement$ParseInfo r0 = r1.parseInfo     // Catch:{ all -> 0x0250 }
            int r0 = r0.numberOfQueries     // Catch:{ all -> 0x0250 }
            int r0 = r0 * r7
            long[] r0 = new long[r0]     // Catch:{ all -> 0x0250 }
            r14 = r0
            r15 = 0
            if (r5 != 0) goto L_0x006f
            com.mysql.jdbc.MysqlIO r0 = r4.getIO()     // Catch:{ all -> 0x006c }
            r0.enableMultiQueries()     // Catch:{ all -> 0x006c }
            goto L_0x006f
        L_0x006c:
            r0 = move-exception
            goto L_0x0248
        L_0x006f:
            boolean r0 = r1.retrieveGeneratedKeys     // Catch:{ all -> 0x0241 }
            r16 = r6
            r6 = 1
            if (r0 == 0) goto L_0x008f
            java.lang.String r0 = r1.generateMultiStatementForBatch(r8)     // Catch:{ all -> 0x008a }
            java.sql.PreparedStatement r0 = r4.prepareStatement(r0, r6)     // Catch:{ all -> 0x008a }
            com.mysql.jdbc.Wrapper r0 = (com.mysql.jdbc.Wrapper) r0     // Catch:{ all -> 0x008a }
            java.lang.Class<java.sql.PreparedStatement> r6 = java.sql.PreparedStatement.class
            java.lang.Object r0 = r0.unwrap(r6)     // Catch:{ all -> 0x008a }
            java.sql.PreparedStatement r0 = (java.sql.PreparedStatement) r0     // Catch:{ all -> 0x008a }
            r9 = r0
            goto L_0x00a2
        L_0x008a:
            r0 = move-exception
            r6 = r16
            goto L_0x0248
        L_0x008f:
            java.lang.String r0 = r1.generateMultiStatementForBatch(r8)     // Catch:{ all -> 0x0239 }
            java.sql.PreparedStatement r0 = r4.prepareStatement(r0)     // Catch:{ all -> 0x0239 }
            com.mysql.jdbc.Wrapper r0 = (com.mysql.jdbc.Wrapper) r0     // Catch:{ all -> 0x0239 }
            java.lang.Class<java.sql.PreparedStatement> r6 = java.sql.PreparedStatement.class
            java.lang.Object r0 = r0.unwrap(r6)     // Catch:{ all -> 0x0239 }
            java.sql.PreparedStatement r0 = (java.sql.PreparedStatement) r0     // Catch:{ all -> 0x0239 }
            r9 = r0
        L_0x00a2:
            boolean r0 = r4.getEnableQueryTimeouts()     // Catch:{ all -> 0x0239 }
            if (r0 == 0) goto L_0x00cf
            if (r2 == 0) goto L_0x00cf
            r0 = 5
            r6 = 0
            boolean r0 = r4.versionMeetsMinimum(r0, r6, r6)     // Catch:{ all -> 0x0239 }
            if (r0 == 0) goto L_0x00cf
            com.mysql.jdbc.StatementImpl$CancelTask r0 = new com.mysql.jdbc.StatementImpl$CancelTask     // Catch:{ all -> 0x0239 }
            r6 = r9
            com.mysql.jdbc.StatementImpl r6 = (com.mysql.jdbc.StatementImpl) r6     // Catch:{ all -> 0x0239 }
            r0.<init>(r6)     // Catch:{ all -> 0x0239 }
            r6 = r0
            java.util.Timer r0 = r4.getCancelTimer()     // Catch:{ all -> 0x00c8 }
            r17 = r10
            r18 = r11
            long r10 = (long) r2
            r0.schedule(r6, r10)     // Catch:{ all -> 0x0233 }
            goto L_0x00d5
        L_0x00c8:
            r0 = move-exception
            r17 = r10
            r18 = r11
            goto L_0x0248
        L_0x00cf:
            r17 = r10
            r18 = r11
            r6 = r16
        L_0x00d5:
            if (r7 >= r8) goto L_0x00da
            r0 = r7
            r11 = r0
            goto L_0x00dd
        L_0x00da:
            int r0 = r7 / r8
            r11 = r0
        L_0x00dd:
            int r10 = r11 * r8
            r0 = 0
            r16 = r15
            r15 = r13
            r13 = r12
            r12 = r0
        L_0x00e5:
            if (r12 >= r10) goto L_0x0140
            if (r12 == 0) goto L_0x010e
            int r0 = r12 % r8
            if (r0 != 0) goto L_0x010e
            r9.execute()     // Catch:{ SQLException -> 0x00f1 }
            goto L_0x00fc
        L_0x00f1:
            r0 = move-exception
            r18 = r0
            r0 = r18
            java.sql.SQLException r18 = r1.handleExceptionForBatch(r13, r8, r14, r0)     // Catch:{ all -> 0x0144 }
            r16 = r18
        L_0x00fc:
            r0 = r9
            com.mysql.jdbc.StatementImpl r0 = (com.mysql.jdbc.StatementImpl) r0     // Catch:{ all -> 0x0144 }
            int r0 = r1.processMultiCountsAndKeys(r0, r15, r14)     // Catch:{ all -> 0x0144 }
            r15 = r0
            r9.clearParameters()     // Catch:{ all -> 0x0144 }
            r0 = 1
            r17 = r16
            r16 = r15
            r15 = r0
            goto L_0x0116
        L_0x010e:
            r19 = r16
            r16 = r15
            r15 = r17
            r17 = r19
        L_0x0116:
            java.util.List r0 = r1.batchedArgs     // Catch:{ all -> 0x0137 }
            int r18 = r13 + 1
            java.lang.Object r0 = r0.get(r13)     // Catch:{ all -> 0x012d }
            int r0 = r1.setOneBatchedParameterSet(r9, r15, r0)     // Catch:{ all -> 0x012d }
            int r12 = r12 + 1
            r15 = r16
            r16 = r17
            r13 = r18
            r17 = r0
            goto L_0x00e5
        L_0x012d:
            r0 = move-exception
            r10 = r15
            r13 = r16
            r15 = r17
            r12 = r18
            goto L_0x0248
        L_0x0137:
            r0 = move-exception
            r12 = r13
            r10 = r15
            r13 = r16
            r15 = r17
            goto L_0x0248
        L_0x0140:
            r9.execute()     // Catch:{ SQLException -> 0x014d }
            goto L_0x0158
        L_0x0144:
            r0 = move-exception
            r12 = r13
            r13 = r15
            r15 = r16
            r10 = r17
            goto L_0x0248
        L_0x014d:
            r0 = move-exception
            r12 = r0
            r0 = r12
            int r12 = r13 + -1
            java.sql.SQLException r12 = r1.handleExceptionForBatch(r12, r8, r14, r0)     // Catch:{ all -> 0x0144 }
            r16 = r12
        L_0x0158:
            r0 = r9
            com.mysql.jdbc.StatementImpl r0 = (com.mysql.jdbc.StatementImpl) r0     // Catch:{ all -> 0x0144 }
            int r0 = r1.processMultiCountsAndKeys(r0, r15, r14)     // Catch:{ all -> 0x0144 }
            r12 = r0
            r9.clearParameters()     // Catch:{ all -> 0x0228 }
            int r8 = r7 - r13
            if (r9 == 0) goto L_0x016c
            r9.close()     // Catch:{ all -> 0x0047 }
            r9 = 0
        L_0x016c:
            if (r8 <= 0) goto L_0x01ce
            boolean r0 = r1.retrieveGeneratedKeys     // Catch:{ all -> 0x01cc }
            if (r0 == 0) goto L_0x017f
            java.lang.String r0 = r1.generateMultiStatementForBatch(r8)     // Catch:{ all -> 0x01cc }
            r10 = 1
            java.sql.PreparedStatement r0 = r4.prepareStatement(r0, r10)     // Catch:{ all -> 0x01cc }
            r9 = r0
            goto L_0x0188
        L_0x017f:
            java.lang.String r0 = r1.generateMultiStatementForBatch(r8)     // Catch:{ all -> 0x01cc }
            java.sql.PreparedStatement r0 = r4.prepareStatement(r0)     // Catch:{ all -> 0x01cc }
            r9 = r0
        L_0x0188:
            if (r6 == 0) goto L_0x018f
            r0 = r9
            com.mysql.jdbc.StatementImpl r0 = (com.mysql.jdbc.StatementImpl) r0     // Catch:{ all -> 0x01cc }
            r6.toCancel = r0     // Catch:{ all -> 0x01cc }
        L_0x018f:
            r0 = 1
            r10 = r0
        L_0x0191:
            if (r13 >= r7) goto L_0x01a8
            java.util.List r0 = r1.batchedArgs     // Catch:{ all -> 0x01ac }
            int r15 = r13 + 1
            java.lang.Object r0 = r0.get(r13)     // Catch:{ all -> 0x01a2 }
            int r0 = r1.setOneBatchedParameterSet(r9, r10, r0)     // Catch:{ all -> 0x01a2 }
            r10 = r0
            r13 = r15
            goto L_0x0191
        L_0x01a2:
            r0 = move-exception
            r17 = r10
            r13 = r15
            goto L_0x0220
        L_0x01a8:
            r9.execute()     // Catch:{ SQLException -> 0x01b1 }
            goto L_0x01bc
        L_0x01ac:
            r0 = move-exception
            r17 = r10
            goto L_0x0220
        L_0x01b1:
            r0 = move-exception
            r15 = r0
            r0 = r15
            int r15 = r13 + -1
            java.sql.SQLException r15 = r1.handleExceptionForBatch(r15, r8, r14, r0)     // Catch:{ all -> 0x01ac }
            r16 = r15
        L_0x01bc:
            r0 = r9
            com.mysql.jdbc.StatementImpl r0 = (com.mysql.jdbc.StatementImpl) r0     // Catch:{ all -> 0x01ac }
            int r0 = r1.processMultiCountsAndKeys(r0, r12, r14)     // Catch:{ all -> 0x01ac }
            r12 = r0
            r9.clearParameters()     // Catch:{ all -> 0x01ac }
            r17 = r10
            r10 = r16
            goto L_0x01d0
        L_0x01cc:
            r0 = move-exception
            goto L_0x0220
        L_0x01ce:
            r10 = r16
        L_0x01d0:
            if (r6 == 0) goto L_0x01ea
            java.sql.SQLException r0 = r6.caughtWhileCancelling     // Catch:{ all -> 0x01e6 }
            if (r0 != 0) goto L_0x01e3
            r6.cancel()     // Catch:{ all -> 0x01e6 }
            java.util.Timer r0 = r4.getCancelTimer()     // Catch:{ all -> 0x01e6 }
            r0.purge()     // Catch:{ all -> 0x01e6 }
            r0 = 0
            r6 = r0
            goto L_0x01ea
        L_0x01e3:
            java.sql.SQLException r0 = r6.caughtWhileCancelling     // Catch:{ all -> 0x01e6 }
            throw r0     // Catch:{ all -> 0x01e6 }
        L_0x01e6:
            r0 = move-exception
            r16 = r10
            goto L_0x0220
        L_0x01ea:
            if (r10 != 0) goto L_0x0217
            if (r9 == 0) goto L_0x01f2
            r9.close()     // Catch:{ all -> 0x0047 }
        L_0x01f2:
            if (r6 == 0) goto L_0x0200
            r6.cancel()     // Catch:{ all -> 0x0270 }
            java.util.Timer r0 = r4.getCancelTimer()     // Catch:{ all -> 0x0270 }
            r0.purge()     // Catch:{ all -> 0x0270 }
        L_0x0200:
            r20.resetCancelledState()     // Catch:{ all -> 0x0270 }
            if (r5 != 0) goto L_0x020c
            com.mysql.jdbc.MysqlIO r0 = r4.getIO()     // Catch:{ all -> 0x0270 }
            r0.disableMultiQueries()     // Catch:{ all -> 0x0270 }
        L_0x020c:
            r20.clearBatch()     // Catch:{ all -> 0x0270 }
            r0 = r7
            r7 = r10
            r10 = r12
            r12 = r14
            r15 = r17
            monitor-exit(r3)     // Catch:{ all -> 0x0270 }
            return r14
        L_0x0217:
            com.mysql.jdbc.ExceptionInterceptor r0 = r20.getExceptionInterceptor()     // Catch:{ all -> 0x01e6 }
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createBatchUpdateException(r10, r14, r0)     // Catch:{ all -> 0x01e6 }
            throw r0     // Catch:{ all -> 0x01e6 }
        L_0x0220:
            if (r9 == 0) goto L_0x0225
            r9.close()     // Catch:{ all -> 0x0047 }
        L_0x0225:
            throw r0     // Catch:{ all -> 0x0047 }
        L_0x0228:
            r0 = move-exception
            r15 = r16
            r10 = r17
            r19 = r13
            r13 = r12
            r12 = r19
            goto L_0x0248
        L_0x0233:
            r0 = move-exception
            r10 = r17
            r11 = r18
            goto L_0x0248
        L_0x0239:
            r0 = move-exception
            r17 = r10
            r18 = r11
            r6 = r16
            goto L_0x0248
        L_0x0241:
            r0 = move-exception
            r16 = r6
            r17 = r10
            r18 = r11
        L_0x0248:
            if (r9 == 0) goto L_0x024e
            r9.close()     // Catch:{ all -> 0x0047 }
            r9 = 0
        L_0x024e:
            throw r0     // Catch:{ all -> 0x0047 }
        L_0x0250:
            r0 = move-exception
            r16 = r6
        L_0x0253:
            if (r6 == 0) goto L_0x025f
            r6.cancel()     // Catch:{ all -> 0x0270 }
            java.util.Timer r7 = r4.getCancelTimer()     // Catch:{ all -> 0x0270 }
            r7.purge()     // Catch:{ all -> 0x0270 }
        L_0x025f:
            r20.resetCancelledState()     // Catch:{ all -> 0x0270 }
            if (r5 != 0) goto L_0x026b
            com.mysql.jdbc.MysqlIO r7 = r4.getIO()     // Catch:{ all -> 0x0270 }
            r7.disableMultiQueries()     // Catch:{ all -> 0x0270 }
        L_0x026b:
            r20.clearBatch()     // Catch:{ all -> 0x0270 }
            throw r0     // Catch:{ all -> 0x0270 }
        L_0x0270:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0270 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.PreparedStatement.executePreparedBatchAsMultiStatement(int):long[]");
    }

    private String generateMultiStatementForBatch(int numBatches) throws SQLException {
        String sb;
        synchronized (checkClosed().getConnectionMutex()) {
            StringBuilder newStatementSql = new StringBuilder((this.originalSql.length() + 1) * numBatches);
            newStatementSql.append(this.originalSql);
            for (int i = 0; i < numBatches - 1; i++) {
                newStatementSql.append(';');
                newStatementSql.append(this.originalSql);
            }
            sb = newStatementSql.toString();
        }
        return sb;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v9, resolved type: com.mysql.jdbc.MySQLConnection} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v27, resolved type: com.mysql.jdbc.MySQLConnection} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v29, resolved type: com.mysql.jdbc.MySQLConnection} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v30, resolved type: com.mysql.jdbc.MySQLConnection} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v38, resolved type: int} */
    /* JADX WARNING: type inference failed for: r2v1, types: [com.mysql.jdbc.MySQLConnection] */
    /* JADX WARNING: type inference failed for: r2v26 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0151  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x0155  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0185 A[SYNTHETIC, Splitter:B:136:0x0185] */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0190 A[SYNTHETIC, Splitter:B:140:0x0190] */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x01a8 A[Catch:{ all -> 0x019a, all -> 0x01ae }] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x007e  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0081 A[SYNTHETIC, Splitter:B:37:0x0081] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x008a  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x00fe A[SYNTHETIC, Splitter:B:77:0x00fe] */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x010c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long[] executeBatchedInserts(int r24) throws java.sql.SQLException {
        /*
            r23 = this;
            r1 = r23
            r2 = r24
            com.mysql.jdbc.MySQLConnection r0 = r23.checkClosed()
            java.lang.Object r3 = r0.getConnectionMutex()
            monitor-enter(r3)
            java.lang.String r0 = r23.getValuesClause()     // Catch:{ all -> 0x01c3 }
            r4 = r0
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ all -> 0x01c3 }
            r5 = r0
            if (r4 != 0) goto L_0x001d
            long[] r0 = r23.executeBatchSerially(r24)     // Catch:{ all -> 0x01c3 }
            monitor-exit(r3)     // Catch:{ all -> 0x01c3 }
            return r0
        L_0x001d:
            java.util.List r0 = r1.batchedArgs     // Catch:{ all -> 0x01c3 }
            int r0 = r0.size()     // Catch:{ all -> 0x01c3 }
            r6 = r0
            boolean r0 = r1.retrieveGeneratedKeys     // Catch:{ all -> 0x01c3 }
            if (r0 == 0) goto L_0x002f
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x01c3 }
            r0.<init>(r6)     // Catch:{ all -> 0x01c3 }
            r1.batchedGeneratedKeys = r0     // Catch:{ all -> 0x01c3 }
        L_0x002f:
            int r0 = r1.computeBatchSize(r6)     // Catch:{ all -> 0x01c3 }
            if (r6 >= r0) goto L_0x0038
            r0 = r6
            r7 = r0
            goto L_0x0039
        L_0x0038:
            r7 = r0
        L_0x0039:
            r8 = 0
            r9 = 1
            r10 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            long[] r0 = new long[r6]     // Catch:{ all -> 0x01c3 }
            r16 = r0
            com.mysql.jdbc.PreparedStatement r0 = r1.prepareBatchedInsertSQL(r5, r7)     // Catch:{ all -> 0x01a0 }
            r8 = r0
            boolean r0 = r5.getEnableQueryTimeouts()     // Catch:{ all -> 0x01a0 }
            r17 = r4
            r4 = 0
            if (r0 == 0) goto L_0x007a
            if (r2 == 0) goto L_0x007a
            r0 = 5
            boolean r0 = r5.versionMeetsMinimum(r0, r4, r4)     // Catch:{ all -> 0x0074 }
            if (r0 == 0) goto L_0x007a
            com.mysql.jdbc.StatementImpl$CancelTask r0 = new com.mysql.jdbc.StatementImpl$CancelTask     // Catch:{ all -> 0x0074 }
            r0.<init>(r8)     // Catch:{ all -> 0x0074 }
            r14 = r0
            java.util.Timer r0 = r5.getCancelTimer()     // Catch:{ all -> 0x0074 }
            r18 = r5
            long r4 = (long) r2
            r0.schedule(r14, r4)     // Catch:{ all -> 0x006d }
            goto L_0x007c
        L_0x006d:
            r0 = move-exception
            r4 = r16
            r2 = r18
            goto L_0x01a6
        L_0x0074:
            r0 = move-exception
            r2 = r5
            r4 = r16
            goto L_0x01a6
        L_0x007a:
            r18 = r5
        L_0x007c:
            if (r6 >= r7) goto L_0x0081
            r0 = r6
            r12 = r0
            goto L_0x0084
        L_0x0081:
            int r0 = r6 / r7
            r12 = r0
        L_0x0084:
            int r4 = r12 * r7
            r0 = 0
            r5 = r0
        L_0x0088:
            if (r5 >= r4) goto L_0x00dc
            if (r5 == 0) goto L_0x00bb
            int r0 = r5 % r7
            if (r0 != 0) goto L_0x00bb
            long r20 = r8.executeLargeUpdate()     // Catch:{ SQLException -> 0x009b }
            long r10 = r10 + r20
            r20 = r4
            r4 = r16
            goto L_0x00ab
        L_0x009b:
            r0 = move-exception
            r20 = r0
            r0 = r20
            int r2 = r13 + -1
            r20 = r4
            r4 = r16
            java.sql.SQLException r2 = r1.handleExceptionForBatch(r2, r7, r4, r0)     // Catch:{ all -> 0x00e7 }
            r15 = r2
        L_0x00ab:
            r1.getBatchedGeneratedKeys((java.sql.Statement) r8)     // Catch:{ all -> 0x00e7 }
            r8.clearParameters()     // Catch:{ all -> 0x00e7 }
            r0 = 1
            r9 = r0
            goto L_0x00bf
        L_0x00b4:
            r0 = move-exception
            r4 = r16
            r2 = r18
            goto L_0x01a6
        L_0x00bb:
            r20 = r4
            r4 = r16
        L_0x00bf:
            java.util.List r0 = r1.batchedArgs     // Catch:{ all -> 0x00e7 }
            int r2 = r13 + 1
            java.lang.Object r0 = r0.get(r13)     // Catch:{ all -> 0x00d6 }
            int r0 = r1.setOneBatchedParameterSet(r8, r9, r0)     // Catch:{ all -> 0x00d6 }
            r9 = r0
            int r5 = r5 + 1
            r13 = r2
            r16 = r4
            r4 = r20
            r2 = r24
            goto L_0x0088
        L_0x00d6:
            r0 = move-exception
            r13 = r2
            r2 = r18
            goto L_0x01a6
        L_0x00dc:
            r20 = r4
            r4 = r16
            long r21 = r8.executeLargeUpdate()     // Catch:{ SQLException -> 0x00ec }
            long r10 = r10 + r21
            goto L_0x00f6
        L_0x00e7:
            r0 = move-exception
            r2 = r18
            goto L_0x01a6
        L_0x00ec:
            r0 = move-exception
            r2 = r0
            r0 = r2
            int r2 = r13 + -1
            java.sql.SQLException r2 = r1.handleExceptionForBatch(r2, r7, r4, r0)     // Catch:{ all -> 0x0196 }
            r15 = r2
        L_0x00f6:
            r1.getBatchedGeneratedKeys((java.sql.Statement) r8)     // Catch:{ all -> 0x0196 }
            int r7 = r6 - r13
            if (r8 == 0) goto L_0x0108
            r8.close()     // Catch:{ all -> 0x0103 }
            r8 = 0
            goto L_0x0108
        L_0x0103:
            r0 = move-exception
            r2 = r18
            goto L_0x01b2
        L_0x0108:
            if (r7 <= 0) goto L_0x0151
            r2 = r18
            com.mysql.jdbc.PreparedStatement r0 = r1.prepareBatchedInsertSQL(r2, r7)     // Catch:{ all -> 0x014f }
            r5 = r0
            if (r14 == 0) goto L_0x011c
            r14.toCancel = r5     // Catch:{ all -> 0x0118 }
            goto L_0x011c
        L_0x0118:
            r0 = move-exception
            r8 = r5
            goto L_0x018e
        L_0x011c:
            r0 = 1
            r8 = r0
        L_0x011e:
            if (r13 >= r6) goto L_0x0134
            java.util.List r0 = r1.batchedArgs     // Catch:{ all -> 0x013b }
            int r9 = r13 + 1
            java.lang.Object r0 = r0.get(r13)     // Catch:{ all -> 0x012f }
            int r0 = r1.setOneBatchedParameterSet(r5, r8, r0)     // Catch:{ all -> 0x012f }
            r8 = r0
            r13 = r9
            goto L_0x011e
        L_0x012f:
            r0 = move-exception
            r13 = r9
            r9 = r8
            r8 = r5
            goto L_0x018e
        L_0x0134:
            long r20 = r5.executeLargeUpdate()     // Catch:{ SQLException -> 0x013f }
            long r10 = r10 + r20
            goto L_0x0149
        L_0x013b:
            r0 = move-exception
            r9 = r8
            r8 = r5
            goto L_0x018e
        L_0x013f:
            r0 = move-exception
            r9 = r0
            r0 = r9
            int r9 = r13 + -1
            java.sql.SQLException r9 = r1.handleExceptionForBatch(r9, r7, r4, r0)     // Catch:{ all -> 0x013b }
            r15 = r9
        L_0x0149:
            r1.getBatchedGeneratedKeys((java.sql.Statement) r5)     // Catch:{ all -> 0x013b }
            r9 = r8
            r8 = r5
            goto L_0x0153
        L_0x014f:
            r0 = move-exception
            goto L_0x018e
        L_0x0151:
            r2 = r18
        L_0x0153:
            if (r15 != 0) goto L_0x0185
            r0 = 1
            if (r6 <= r0) goto L_0x0169
            r18 = 0
            int r0 = (r10 > r18 ? 1 : (r10 == r18 ? 0 : -1))
            if (r0 <= 0) goto L_0x0160
            r18 = -2
        L_0x0160:
            r0 = 0
        L_0x0161:
            if (r0 >= r6) goto L_0x0168
            r4[r0] = r18     // Catch:{ all -> 0x014f }
            int r0 = r0 + 1
            goto L_0x0161
        L_0x0168:
            goto L_0x016c
        L_0x0169:
            r5 = 0
            r4[r5] = r10     // Catch:{ all -> 0x014f }
        L_0x016c:
            if (r8 == 0) goto L_0x0172
            r8.close()     // Catch:{ all -> 0x01ae }
        L_0x0172:
            if (r14 == 0) goto L_0x0180
            r14.cancel()     // Catch:{ all -> 0x01c3 }
            java.util.Timer r0 = r2.getCancelTimer()     // Catch:{ all -> 0x01c3 }
            r0.purge()     // Catch:{ all -> 0x01c3 }
        L_0x0180:
            r23.resetCancelledState()     // Catch:{ all -> 0x01c3 }
            monitor-exit(r3)     // Catch:{ all -> 0x01c3 }
            return r4
        L_0x0185:
            com.mysql.jdbc.ExceptionInterceptor r0 = r23.getExceptionInterceptor()     // Catch:{ all -> 0x014f }
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createBatchUpdateException(r15, r4, r0)     // Catch:{ all -> 0x014f }
            throw r0     // Catch:{ all -> 0x014f }
        L_0x018e:
            if (r8 == 0) goto L_0x0193
            r8.close()     // Catch:{ all -> 0x01ae }
        L_0x0193:
            throw r0     // Catch:{ all -> 0x01ae }
        L_0x0196:
            r0 = move-exception
            r2 = r18
            goto L_0x01a6
        L_0x019a:
            r0 = move-exception
            r4 = r16
            r2 = r18
            goto L_0x01a6
        L_0x01a0:
            r0 = move-exception
            r17 = r4
            r2 = r5
            r4 = r16
        L_0x01a6:
            if (r8 == 0) goto L_0x01b0
            r8.close()     // Catch:{ all -> 0x01ae }
            r5 = 0
            r8 = r5
            goto L_0x01b0
        L_0x01ae:
            r0 = move-exception
            goto L_0x01b2
        L_0x01b0:
            throw r0     // Catch:{ all -> 0x01ae }
        L_0x01b2:
            if (r14 == 0) goto L_0x01be
            r14.cancel()     // Catch:{ all -> 0x01c3 }
            java.util.Timer r5 = r2.getCancelTimer()     // Catch:{ all -> 0x01c3 }
            r5.purge()     // Catch:{ all -> 0x01c3 }
        L_0x01be:
            r23.resetCancelledState()     // Catch:{ all -> 0x01c3 }
            throw r0     // Catch:{ all -> 0x01c3 }
        L_0x01c3:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x01c3 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.PreparedStatement.executeBatchedInserts(int):long[]");
    }

    /* access modifiers changed from: protected */
    public String getValuesClause() throws SQLException {
        return this.parseInfo.valuesClause;
    }

    /* access modifiers changed from: protected */
    public int computeBatchSize(int numBatchedArgs) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            long[] combinedValues = computeMaxParameterSetSizeAndBatchSize(numBatchedArgs);
            long maxSizeOfParameterSet = combinedValues[0];
            long sizeOfEntireBatch = combinedValues[1];
            int maxAllowedPacket = this.connection.getMaxAllowedPacket();
            if (sizeOfEntireBatch < ((long) (maxAllowedPacket - this.originalSql.length()))) {
                return numBatchedArgs;
            }
            int max = (int) Math.max(1, ((long) (maxAllowedPacket - this.originalSql.length())) / maxSizeOfParameterSet);
            return max;
        }
    }

    /* access modifiers changed from: protected */
    public long[] computeMaxParameterSetSizeAndBatchSize(int numBatchedArgs) throws SQLException {
        long[] jArr;
        long sizeOfParameterSet;
        boolean[] isStreamBatch;
        boolean[] isNullBatch;
        synchronized (checkClosed().getConnectionMutex()) {
            long sizeOfEntireBatch = 0;
            long maxSizeOfParameterSet = 0;
            for (int i = 0; i < numBatchedArgs; i++) {
                BatchParams paramArg = (BatchParams) this.batchedArgs.get(i);
                boolean[] isNullBatch2 = paramArg.isNull;
                boolean[] isStreamBatch2 = paramArg.isStream;
                long sizeOfParameterSet2 = 0;
                int j = 0;
                while (j < isNullBatch2.length) {
                    if (isNullBatch2[j]) {
                        isNullBatch = isNullBatch2;
                        isStreamBatch = isStreamBatch2;
                        sizeOfParameterSet2 += 4;
                    } else if (isStreamBatch2[j]) {
                        int streamLength = paramArg.streamLengths[j];
                        if (streamLength != -1) {
                            sizeOfParameterSet2 += (long) (streamLength * 2);
                            isNullBatch = isNullBatch2;
                            isStreamBatch = isStreamBatch2;
                        } else {
                            isNullBatch = isNullBatch2;
                            isStreamBatch = isStreamBatch2;
                            sizeOfParameterSet2 += (long) paramArg.parameterStrings[j].length;
                        }
                    } else {
                        isNullBatch = isNullBatch2;
                        isStreamBatch = isStreamBatch2;
                        sizeOfParameterSet2 += (long) paramArg.parameterStrings[j].length;
                    }
                    j++;
                    isNullBatch2 = isNullBatch;
                    isStreamBatch2 = isStreamBatch;
                    int i2 = numBatchedArgs;
                }
                boolean[] zArr = isStreamBatch2;
                if (getValuesClause() != null) {
                    sizeOfParameterSet = sizeOfParameterSet2 + ((long) (getValuesClause().length() + 1));
                } else {
                    sizeOfParameterSet = sizeOfParameterSet2 + ((long) (this.originalSql.length() + 1));
                }
                sizeOfEntireBatch += sizeOfParameterSet;
                if (sizeOfParameterSet > maxSizeOfParameterSet) {
                    maxSizeOfParameterSet = sizeOfParameterSet;
                }
            }
            jArr = new long[]{maxSizeOfParameterSet, sizeOfEntireBatch};
        }
        return jArr;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x01c7  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x011e  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0129 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long[] executeBatchSerially(int r23) throws java.sql.SQLException {
        /*
            r22 = this;
            r8 = r22
            r9 = r23
            com.mysql.jdbc.MySQLConnection r0 = r22.checkClosed()
            java.lang.Object r10 = r0.getConnectionMutex()
            monitor-enter(r10)
            com.mysql.jdbc.MySQLConnection r0 = r8.connection     // Catch:{ all -> 0x01df }
            r11 = r0
            if (r11 != 0) goto L_0x0015
            r22.checkClosed()     // Catch:{ all -> 0x01df }
        L_0x0015:
            r0 = 0
            java.util.List r1 = r8.batchedArgs     // Catch:{ all -> 0x01df }
            r12 = 0
            if (r1 == 0) goto L_0x01d6
            java.util.List r1 = r8.batchedArgs     // Catch:{ all -> 0x01df }
            int r1 = r1.size()     // Catch:{ all -> 0x01df }
            r13 = r1
            long[] r1 = new long[r13]     // Catch:{ all -> 0x01df }
            r14 = r1
            r0 = 0
        L_0x0026:
            r15 = -3
            if (r0 >= r13) goto L_0x002f
            r14[r0] = r15     // Catch:{ all -> 0x01df }
            int r0 = r0 + 1
            goto L_0x0026
        L_0x002f:
            r1 = 0
            r2 = 0
            boolean r0 = r11.getEnableQueryTimeouts()     // Catch:{ NullPointerException -> 0x019e, all -> 0x019b }
            if (r0 == 0) goto L_0x0051
            if (r9 == 0) goto L_0x0051
            r0 = 5
            boolean r0 = r11.versionMeetsMinimum(r0, r12, r12)     // Catch:{ NullPointerException -> 0x019e, all -> 0x019b }
            if (r0 == 0) goto L_0x0051
            com.mysql.jdbc.StatementImpl$CancelTask r0 = new com.mysql.jdbc.StatementImpl$CancelTask     // Catch:{ NullPointerException -> 0x019e, all -> 0x019b }
            r0.<init>(r8)     // Catch:{ NullPointerException -> 0x019e, all -> 0x019b }
            r2 = r0
            java.util.Timer r0 = r11.getCancelTimer()     // Catch:{ NullPointerException -> 0x019e, all -> 0x019b }
            long r3 = (long) r9     // Catch:{ NullPointerException -> 0x019e, all -> 0x019b }
            r0.schedule(r2, r3)     // Catch:{ NullPointerException -> 0x019e, all -> 0x019b }
            r17 = r2
            goto L_0x0053
        L_0x0051:
            r17 = r2
        L_0x0053:
            boolean r0 = r8.retrieveGeneratedKeys     // Catch:{ NullPointerException -> 0x0196, all -> 0x0191 }
            if (r0 == 0) goto L_0x006b
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ NullPointerException -> 0x0065, all -> 0x005f }
            r0.<init>(r13)     // Catch:{ NullPointerException -> 0x0065, all -> 0x005f }
            r8.batchedGeneratedKeys = r0     // Catch:{ NullPointerException -> 0x0065, all -> 0x005f }
            goto L_0x006b
        L_0x005f:
            r0 = move-exception
            r2 = r17
            r3 = -1
            goto L_0x01c3
        L_0x0065:
            r0 = move-exception
            r2 = r17
            r3 = -1
            goto L_0x01a0
        L_0x006b:
            r8.batchCommandIndex = r12     // Catch:{ NullPointerException -> 0x0196, all -> 0x0191 }
            r6 = r1
        L_0x006e:
            int r0 = r8.batchCommandIndex     // Catch:{ NullPointerException -> 0x018a, all -> 0x0183 }
            if (r0 >= r13) goto L_0x0156
            java.util.List r0 = r8.batchedArgs     // Catch:{ NullPointerException -> 0x014f, all -> 0x0148 }
            int r1 = r8.batchCommandIndex     // Catch:{ NullPointerException -> 0x014f, all -> 0x0148 }
            java.lang.Object r0 = r0.get(r1)     // Catch:{ NullPointerException -> 0x014f, all -> 0x0148 }
            r5 = r0
            r4 = 1
            boolean r0 = r5 instanceof java.lang.String     // Catch:{ SQLException -> 0x0102 }
            if (r0 == 0) goto L_0x00c1
            int r0 = r8.batchCommandIndex     // Catch:{ SQLException -> 0x00ba }
            r1 = r5
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ SQLException -> 0x00ba }
            boolean r2 = r8.retrieveGeneratedKeys     // Catch:{ SQLException -> 0x00ba }
            long r1 = r8.executeUpdateInternal(r1, r4, r2)     // Catch:{ SQLException -> 0x00ba }
            r14[r0] = r1     // Catch:{ SQLException -> 0x00ba }
            com.mysql.jdbc.ResultSetInternalMethods r0 = r8.results     // Catch:{ SQLException -> 0x00ba }
            char r0 = r0.getFirstCharOfQuery()     // Catch:{ SQLException -> 0x00ba }
            r1 = 73
            if (r0 != r1) goto L_0x00a2
            r0 = r5
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ SQLException -> 0x00ba }
            boolean r0 = r8.containsOnDuplicateKeyInString(r0)     // Catch:{ SQLException -> 0x00ba }
            if (r0 == 0) goto L_0x00a2
            r0 = r4
            goto L_0x00a3
        L_0x00a2:
            r0 = r12
        L_0x00a3:
            r8.getBatchedGeneratedKeys((int) r0)     // Catch:{ SQLException -> 0x00ba }
            r18 = r4
            r20 = r5
            r12 = r6
            goto L_0x00f7
        L_0x00ac:
            r0 = move-exception
            r1 = r6
            r2 = r17
            r3 = -1
            goto L_0x01c3
        L_0x00b3:
            r0 = move-exception
            r1 = r6
            r2 = r17
            r3 = -1
            goto L_0x01a0
        L_0x00ba:
            r0 = move-exception
            r18 = r4
            r20 = r5
            r12 = r6
            goto L_0x0108
        L_0x00c1:
            r0 = r5
            com.mysql.jdbc.PreparedStatement$BatchParams r0 = (com.mysql.jdbc.PreparedStatement.BatchParams) r0     // Catch:{ SQLException -> 0x0102 }
            int r3 = r8.batchCommandIndex     // Catch:{ SQLException -> 0x0102 }
            byte[][] r2 = r0.parameterStrings     // Catch:{ SQLException -> 0x0102 }
            java.io.InputStream[] r1 = r0.parameterStreams     // Catch:{ SQLException -> 0x0102 }
            boolean[] r4 = r0.isStream     // Catch:{ SQLException -> 0x00fb }
            int[] r7 = r0.streamLengths     // Catch:{ SQLException -> 0x00fb }
            boolean[] r12 = r0.isNull     // Catch:{ SQLException -> 0x00fb }
            r19 = 1
            r20 = r1
            r1 = r22
            r21 = r3
            r3 = r20
            r18 = 1
            r20 = r5
            r5 = r7
            r7 = r6
            r6 = r12
            r12 = r7
            r7 = r19
            long r1 = r1.executeUpdateInternal(r2, r3, r4, r5, r6, r7)     // Catch:{ SQLException -> 0x00f9 }
            r14[r21] = r1     // Catch:{ SQLException -> 0x00f9 }
            boolean r1 = r22.containsOnDuplicateKeyUpdateInSQL()     // Catch:{ SQLException -> 0x00f9 }
            if (r1 == 0) goto L_0x00f3
            r4 = r18
            goto L_0x00f4
        L_0x00f3:
            r4 = 0
        L_0x00f4:
            r8.getBatchedGeneratedKeys((int) r4)     // Catch:{ SQLException -> 0x00f9 }
        L_0x00f7:
            r6 = r12
            goto L_0x0120
        L_0x00f9:
            r0 = move-exception
            goto L_0x0108
        L_0x00fb:
            r0 = move-exception
            r20 = r5
            r12 = r6
            r18 = 1
            goto L_0x0108
        L_0x0102:
            r0 = move-exception
            r18 = r4
            r20 = r5
            r12 = r6
        L_0x0108:
            int r1 = r8.batchCommandIndex     // Catch:{ NullPointerException -> 0x0141, all -> 0x013a }
            r14[r1] = r15     // Catch:{ NullPointerException -> 0x0141, all -> 0x013a }
            boolean r1 = r8.continueBatchOnError     // Catch:{ NullPointerException -> 0x0141, all -> 0x013a }
            if (r1 == 0) goto L_0x0129
            boolean r1 = r0 instanceof com.mysql.jdbc.exceptions.MySQLTimeoutException     // Catch:{ NullPointerException -> 0x0141, all -> 0x013a }
            if (r1 != 0) goto L_0x0129
            boolean r1 = r0 instanceof com.mysql.jdbc.exceptions.MySQLStatementCancelledException     // Catch:{ NullPointerException -> 0x0141, all -> 0x013a }
            if (r1 != 0) goto L_0x0129
            boolean r1 = r8.hasDeadlockOrTimeoutRolledBackTx(r0)     // Catch:{ NullPointerException -> 0x0141, all -> 0x013a }
            if (r1 != 0) goto L_0x0129
            r1 = r0
            r6 = r1
        L_0x0120:
            int r0 = r8.batchCommandIndex     // Catch:{ NullPointerException -> 0x00b3, all -> 0x00ac }
            int r0 = r0 + 1
            r8.batchCommandIndex = r0     // Catch:{ NullPointerException -> 0x00b3, all -> 0x00ac }
            r12 = 0
            goto L_0x006e
        L_0x0129:
            int r1 = r8.batchCommandIndex     // Catch:{ NullPointerException -> 0x0141, all -> 0x013a }
            long[] r2 = new long[r1]     // Catch:{ NullPointerException -> 0x0141, all -> 0x013a }
            r3 = 0
            java.lang.System.arraycopy(r14, r3, r2, r3, r1)     // Catch:{ NullPointerException -> 0x0141, all -> 0x013a }
            com.mysql.jdbc.ExceptionInterceptor r1 = r22.getExceptionInterceptor()     // Catch:{ NullPointerException -> 0x0141, all -> 0x013a }
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createBatchUpdateException(r0, r2, r1)     // Catch:{ NullPointerException -> 0x0141, all -> 0x013a }
            throw r1     // Catch:{ NullPointerException -> 0x0141, all -> 0x013a }
        L_0x013a:
            r0 = move-exception
            r1 = r12
            r2 = r17
            r3 = -1
            goto L_0x01c3
        L_0x0141:
            r0 = move-exception
            r1 = r12
            r2 = r17
            r3 = -1
            goto L_0x01a0
        L_0x0148:
            r0 = move-exception
            r12 = r6
            r1 = r12
            r2 = r17
            r3 = -1
            goto L_0x0189
        L_0x014f:
            r0 = move-exception
            r12 = r6
            r1 = r12
            r2 = r17
            r3 = -1
            goto L_0x0190
        L_0x0156:
            r12 = r6
            if (r12 != 0) goto L_0x016f
            r3 = -1
            r8.batchCommandIndex = r3     // Catch:{ all -> 0x01df }
            if (r17 == 0) goto L_0x0169
            r17.cancel()     // Catch:{ all -> 0x01df }
            java.util.Timer r0 = r11.getCancelTimer()     // Catch:{ all -> 0x01df }
            r0.purge()     // Catch:{ all -> 0x01df }
        L_0x0169:
            r22.resetCancelledState()     // Catch:{ all -> 0x01df }
            r0 = r14
            goto L_0x01d6
        L_0x016f:
            r3 = -1
            com.mysql.jdbc.ExceptionInterceptor r0 = r22.getExceptionInterceptor()     // Catch:{ NullPointerException -> 0x017e, all -> 0x0179 }
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createBatchUpdateException(r12, r14, r0)     // Catch:{ NullPointerException -> 0x017e, all -> 0x0179 }
            throw r0     // Catch:{ NullPointerException -> 0x017e, all -> 0x0179 }
        L_0x0179:
            r0 = move-exception
            r1 = r12
            r2 = r17
            goto L_0x01c3
        L_0x017e:
            r0 = move-exception
            r1 = r12
            r2 = r17
            goto L_0x01a0
        L_0x0183:
            r0 = move-exception
            r12 = r6
            r3 = -1
            r1 = r12
            r2 = r17
        L_0x0189:
            goto L_0x01c3
        L_0x018a:
            r0 = move-exception
            r12 = r6
            r3 = -1
            r1 = r12
            r2 = r17
        L_0x0190:
            goto L_0x01a0
        L_0x0191:
            r0 = move-exception
            r3 = -1
            r2 = r17
            goto L_0x01c3
        L_0x0196:
            r0 = move-exception
            r3 = -1
            r2 = r17
            goto L_0x01a0
        L_0x019b:
            r0 = move-exception
            r3 = -1
            goto L_0x01c3
        L_0x019e:
            r0 = move-exception
            r3 = -1
        L_0x01a0:
            r4 = r0
            r22.checkClosed()     // Catch:{ SQLException -> 0x01a9 }
            throw r4     // Catch:{ all -> 0x01a7 }
        L_0x01a7:
            r0 = move-exception
            goto L_0x01c3
        L_0x01a9:
            r0 = move-exception
            r5 = r0
            r0 = r5
            int r5 = r8.batchCommandIndex     // Catch:{ all -> 0x01a7 }
            r14[r5] = r15     // Catch:{ all -> 0x01a7 }
            int r5 = r8.batchCommandIndex     // Catch:{ all -> 0x01a7 }
            long[] r5 = new long[r5]     // Catch:{ all -> 0x01a7 }
            int r6 = r8.batchCommandIndex     // Catch:{ all -> 0x01a7 }
            r7 = 0
            java.lang.System.arraycopy(r14, r7, r5, r7, r6)     // Catch:{ all -> 0x01a7 }
            com.mysql.jdbc.ExceptionInterceptor r6 = r22.getExceptionInterceptor()     // Catch:{ all -> 0x01a7 }
            java.sql.SQLException r6 = com.mysql.jdbc.SQLError.createBatchUpdateException(r0, r5, r6)     // Catch:{ all -> 0x01a7 }
            throw r6     // Catch:{ all -> 0x01a7 }
        L_0x01c3:
            r8.batchCommandIndex = r3     // Catch:{ all -> 0x01df }
            if (r2 == 0) goto L_0x01d1
            r2.cancel()     // Catch:{ all -> 0x01df }
            java.util.Timer r3 = r11.getCancelTimer()     // Catch:{ all -> 0x01df }
            r3.purge()     // Catch:{ all -> 0x01df }
        L_0x01d1:
            r22.resetCancelledState()     // Catch:{ all -> 0x01df }
            throw r0     // Catch:{ all -> 0x01df }
        L_0x01d6:
            if (r0 == 0) goto L_0x01da
            r1 = r0
            goto L_0x01dd
        L_0x01da:
            r1 = 0
            long[] r1 = new long[r1]     // Catch:{ all -> 0x01df }
        L_0x01dd:
            monitor-exit(r10)     // Catch:{ all -> 0x01df }
            return r1
        L_0x01df:
            r0 = move-exception
            monitor-exit(r10)     // Catch:{ all -> 0x01df }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.PreparedStatement.executeBatchSerially(int):long[]");
    }

    public String getDateTime(String pattern) {
        return TimeUtil.getSimpleDateFormat((SimpleDateFormat) null, pattern, (Calendar) null, (TimeZone) null).format(new Date());
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00c9 A[SYNTHETIC, Splitter:B:71:0x00c9] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00d1 A[Catch:{ NullPointerException -> 0x00e0 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.mysql.jdbc.ResultSetInternalMethods executeInternal(int r18, com.mysql.jdbc.Buffer r19, boolean r20, boolean r21, com.mysql.jdbc.Field[] r22, boolean r23) throws java.sql.SQLException {
        /*
            r17 = this;
            r12 = r17
            com.mysql.jdbc.MySQLConnection r0 = r17.checkClosed()
            java.lang.Object r13 = r0.getConnectionMutex()
            monitor-enter(r13)
            com.mysql.jdbc.MySQLConnection r0 = r12.connection     // Catch:{ NullPointerException -> 0x00e0 }
            r14 = r0
            int r0 = r12.numberOfExecutions     // Catch:{ NullPointerException -> 0x00e0 }
            int r0 = r0 + 1
            r12.numberOfExecutions = r0     // Catch:{ NullPointerException -> 0x00e0 }
            r1 = 0
            r15 = 0
            r16 = 0
            boolean r0 = r14.getEnableQueryTimeouts()     // Catch:{ all -> 0x00c4 }
            if (r0 == 0) goto L_0x003b
            int r0 = r12.timeoutInMillis     // Catch:{ all -> 0x00c4 }
            if (r0 == 0) goto L_0x003b
            r0 = 5
            boolean r0 = r14.versionMeetsMinimum(r0, r15, r15)     // Catch:{ all -> 0x00c4 }
            if (r0 == 0) goto L_0x003b
            com.mysql.jdbc.StatementImpl$CancelTask r0 = new com.mysql.jdbc.StatementImpl$CancelTask     // Catch:{ all -> 0x00c4 }
            r0.<init>(r12)     // Catch:{ all -> 0x00c4 }
            r1 = r0
            java.util.Timer r0 = r14.getCancelTimer()     // Catch:{ all -> 0x00c4 }
            int r2 = r12.timeoutInMillis     // Catch:{ all -> 0x00c4 }
            long r2 = (long) r2     // Catch:{ all -> 0x00c4 }
            r0.schedule(r1, r2)     // Catch:{ all -> 0x00c4 }
            r11 = r1
            goto L_0x003c
        L_0x003b:
            r11 = r1
        L_0x003c:
            if (r23 != 0) goto L_0x0048
            r17.statementBegins()     // Catch:{ all -> 0x0042 }
            goto L_0x0048
        L_0x0042:
            r0 = move-exception
            r2 = r16
            r1 = r11
            goto L_0x00c7
        L_0x0048:
            r3 = 0
            int r6 = r12.resultSetType     // Catch:{ all -> 0x00be }
            int r7 = r12.resultSetConcurrency     // Catch:{ all -> 0x00be }
            java.lang.String r9 = r12.currentCatalog     // Catch:{ all -> 0x00be }
            r1 = r14
            r2 = r17
            r4 = r18
            r5 = r19
            r8 = r20
            r10 = r22
            r15 = r11
            r11 = r23
            com.mysql.jdbc.ResultSetInternalMethods r0 = r1.execSQL(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ all -> 0x00b9 }
            r2 = r0
            if (r15 == 0) goto L_0x007b
            r15.cancel()     // Catch:{ all -> 0x0078 }
            java.util.Timer r0 = r14.getCancelTimer()     // Catch:{ all -> 0x0078 }
            r0.purge()     // Catch:{ all -> 0x0078 }
            java.sql.SQLException r0 = r15.caughtWhileCancelling     // Catch:{ all -> 0x0078 }
            if (r0 != 0) goto L_0x0075
            r0 = 0
            r1 = r0
            goto L_0x007c
        L_0x0075:
            java.sql.SQLException r0 = r15.caughtWhileCancelling     // Catch:{ all -> 0x0078 }
            throw r0     // Catch:{ all -> 0x0078 }
        L_0x0078:
            r0 = move-exception
            r1 = r15
            goto L_0x00c7
        L_0x007b:
            r1 = r15
        L_0x007c:
            java.lang.Object r3 = r12.cancelTimeoutMutex     // Catch:{ all -> 0x00b7 }
            monitor-enter(r3)     // Catch:{ all -> 0x00b7 }
            boolean r0 = r12.wasCancelled     // Catch:{ all -> 0x00b4 }
            if (r0 == 0) goto L_0x009a
            r0 = 0
            boolean r4 = r12.wasCancelledByTimeout     // Catch:{ all -> 0x00b4 }
            if (r4 == 0) goto L_0x008f
            com.mysql.jdbc.exceptions.MySQLTimeoutException r4 = new com.mysql.jdbc.exceptions.MySQLTimeoutException     // Catch:{ all -> 0x00b4 }
            r4.<init>()     // Catch:{ all -> 0x00b4 }
            r0 = r4
            goto L_0x0095
        L_0x008f:
            com.mysql.jdbc.exceptions.MySQLStatementCancelledException r4 = new com.mysql.jdbc.exceptions.MySQLStatementCancelledException     // Catch:{ all -> 0x00b4 }
            r4.<init>()     // Catch:{ all -> 0x00b4 }
            r0 = r4
        L_0x0095:
            r17.resetCancelledState()     // Catch:{ all -> 0x00b4 }
            throw r0     // Catch:{ all -> 0x00b4 }
        L_0x009a:
            monitor-exit(r3)     // Catch:{ all -> 0x00b4 }
            if (r23 != 0) goto L_0x00a4
            java.util.concurrent.atomic.AtomicBoolean r0 = r12.statementExecuting     // Catch:{ NullPointerException -> 0x00e0 }
            r3 = 0
            r0.set(r3)     // Catch:{ NullPointerException -> 0x00e0 }
        L_0x00a4:
            if (r1 == 0) goto L_0x00b0
            r1.cancel()     // Catch:{ NullPointerException -> 0x00e0 }
            java.util.Timer r0 = r14.getCancelTimer()     // Catch:{ NullPointerException -> 0x00e0 }
            r0.purge()     // Catch:{ NullPointerException -> 0x00e0 }
        L_0x00b0:
            monitor-exit(r13)     // Catch:{ all -> 0x00de }
            return r2
        L_0x00b4:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x00b4 }
            throw r0     // Catch:{ all -> 0x00b7 }
        L_0x00b7:
            r0 = move-exception
            goto L_0x00c7
        L_0x00b9:
            r0 = move-exception
            r2 = r16
            r1 = r15
            goto L_0x00c7
        L_0x00be:
            r0 = move-exception
            r15 = r11
            r2 = r16
            r1 = r15
            goto L_0x00c7
        L_0x00c4:
            r0 = move-exception
            r2 = r16
        L_0x00c7:
            if (r23 != 0) goto L_0x00cf
            java.util.concurrent.atomic.AtomicBoolean r3 = r12.statementExecuting     // Catch:{ NullPointerException -> 0x00e0 }
            r4 = 0
            r3.set(r4)     // Catch:{ NullPointerException -> 0x00e0 }
        L_0x00cf:
            if (r1 == 0) goto L_0x00db
            r1.cancel()     // Catch:{ NullPointerException -> 0x00e0 }
            java.util.Timer r3 = r14.getCancelTimer()     // Catch:{ NullPointerException -> 0x00e0 }
            r3.purge()     // Catch:{ NullPointerException -> 0x00e0 }
        L_0x00db:
            throw r0     // Catch:{ NullPointerException -> 0x00e0 }
        L_0x00de:
            r0 = move-exception
            goto L_0x00e6
        L_0x00e0:
            r0 = move-exception
            r17.checkClosed()     // Catch:{ all -> 0x00de }
            throw r0     // Catch:{ all -> 0x00de }
        L_0x00e6:
            monitor-exit(r13)     // Catch:{ all -> 0x00de }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.PreparedStatement.executeInternal(int, com.mysql.jdbc.Buffer, boolean, boolean, com.mysql.jdbc.Field[], boolean):com.mysql.jdbc.ResultSetInternalMethods");
    }

    public ResultSet executeQuery() throws SQLException {
        String oldCatalog;
        CachedResultSetMetaData cachedMetadata;
        Field[] metadataFromCache;
        synchronized (checkClosed().getConnectionMutex()) {
            MySQLConnection locallyScopedConn = this.connection;
            checkForDml(this.originalSql, this.firstCharOfStmt);
            this.batchedGeneratedKeys = null;
            resetCancelledState();
            implicitlyCloseAllOpenResults();
            clearWarnings();
            if (this.doPingInstead) {
                doPingInstead();
                ResultSetInternalMethods resultSetInternalMethods = this.results;
                return resultSetInternalMethods;
            }
            setupStreamingTimeout(locallyScopedConn);
            Buffer sendPacket = fillSendPacket();
            if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
                String oldCatalog2 = locallyScopedConn.getCatalog();
                locallyScopedConn.setCatalog(this.currentCatalog);
                oldCatalog = oldCatalog2;
            } else {
                oldCatalog = null;
            }
            if (locallyScopedConn.getCacheResultSetMetadata()) {
                cachedMetadata = locallyScopedConn.getCachedMetaData(this.originalSql);
            } else {
                cachedMetadata = null;
            }
            if (cachedMetadata != null) {
                metadataFromCache = cachedMetadata.fields;
            } else {
                metadataFromCache = null;
            }
            locallyScopedConn.setSessionMaxRows(this.maxRows);
            this.results = executeInternal(this.maxRows, sendPacket, createStreamingResultSet(), true, metadataFromCache, false);
            if (oldCatalog != null) {
                locallyScopedConn.setCatalog(oldCatalog);
            }
            if (cachedMetadata != null) {
                locallyScopedConn.initializeResultsMetadataFromCache(this.originalSql, cachedMetadata, this.results);
            } else if (locallyScopedConn.getCacheResultSetMetadata()) {
                locallyScopedConn.initializeResultsMetadataFromCache(this.originalSql, (CachedResultSetMetaData) null, this.results);
            }
            this.lastInsertId = this.results.getUpdateID();
            ResultSetInternalMethods resultSetInternalMethods2 = this.results;
            return resultSetInternalMethods2;
        }
    }

    public int executeUpdate() throws SQLException {
        return Util.truncateAndConvertToInt(executeLargeUpdate());
    }

    /* access modifiers changed from: protected */
    public long executeUpdateInternal(boolean clearBatchedGeneratedKeysAndWarnings, boolean isBatch) throws SQLException {
        long executeUpdateInternal;
        synchronized (checkClosed().getConnectionMutex()) {
            if (clearBatchedGeneratedKeysAndWarnings) {
                clearWarnings();
                this.batchedGeneratedKeys = null;
            }
            executeUpdateInternal = executeUpdateInternal(this.parameterValues, this.parameterStreams, this.isStream, this.streamLengths, this.isNull, isBatch);
        }
        return executeUpdateInternal;
    }

    /* access modifiers changed from: protected */
    public long executeUpdateInternal(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths, boolean[] batchedIsNull, boolean isReallyBatch) throws SQLException {
        String oldCatalog;
        boolean oldInfoMsgState;
        long j;
        synchronized (checkClosed().getConnectionMutex()) {
            MySQLConnection locallyScopedConn = this.connection;
            if (!locallyScopedConn.isReadOnly(false)) {
                if (this.firstCharOfStmt == 'S') {
                    if (isSelectQuery()) {
                        throw SQLError.createSQLException(Messages.getString("PreparedStatement.37"), SQLError.SQL_STATE_NO_ROWS_UPDATED_OR_DELETED, getExceptionInterceptor());
                    }
                }
                resetCancelledState();
                implicitlyCloseAllOpenResults();
                Buffer sendPacket = fillSendPacket(batchedParameterStrings, batchedParameterStreams, batchedIsStream, batchedStreamLengths);
                if (!locallyScopedConn.getCatalog().equals(this.currentCatalog)) {
                    String oldCatalog2 = locallyScopedConn.getCatalog();
                    locallyScopedConn.setCatalog(this.currentCatalog);
                    oldCatalog = oldCatalog2;
                } else {
                    oldCatalog = null;
                }
                locallyScopedConn.setSessionMaxRows(-1);
                if (this.retrieveGeneratedKeys) {
                    locallyScopedConn.setReadInfoMsgEnabled(true);
                    oldInfoMsgState = locallyScopedConn.isReadInfoMsgEnabled();
                } else {
                    oldInfoMsgState = false;
                }
                ResultSetInternalMethods rs = executeInternal(-1, sendPacket, false, false, (Field[]) null, isReallyBatch);
                if (this.retrieveGeneratedKeys) {
                    locallyScopedConn.setReadInfoMsgEnabled(oldInfoMsgState);
                    rs.setFirstCharOfQuery(this.firstCharOfStmt);
                }
                if (oldCatalog != null) {
                    locallyScopedConn.setCatalog(oldCatalog);
                }
                this.results = rs;
                this.updateCount = rs.getUpdateCount();
                if (containsOnDuplicateKeyUpdateInSQL() && this.compensateForOnDuplicateKeyUpdate && (this.updateCount == 2 || this.updateCount == 0)) {
                    this.updateCount = 1;
                }
                this.lastInsertId = rs.getUpdateID();
                j = this.updateCount;
            } else {
                throw SQLError.createSQLException(Messages.getString("PreparedStatement.34") + Messages.getString("PreparedStatement.35"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
        return j;
    }

    /* access modifiers changed from: protected */
    public boolean containsOnDuplicateKeyUpdateInSQL() {
        return this.parseInfo.isOnDuplicateKeyUpdate;
    }

    /* access modifiers changed from: protected */
    public Buffer fillSendPacket() throws SQLException {
        Buffer fillSendPacket;
        synchronized (checkClosed().getConnectionMutex()) {
            fillSendPacket = fillSendPacket(this.parameterValues, this.parameterStreams, this.isStream, this.streamLengths);
        }
        return fillSendPacket;
    }

    /* access modifiers changed from: protected */
    public Buffer fillSendPacket(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths) throws SQLException {
        Buffer sendPacket;
        byte[] commentAsBytes;
        String statementComment;
        byte[] commentAsBytes2;
        byte[][] bArr = batchedParameterStrings;
        synchronized (checkClosed().getConnectionMutex()) {
            sendPacket = this.connection.getIO().getSharedSendPacket();
            sendPacket.clear();
            sendPacket.writeByte((byte) 3);
            boolean useStreamLengths = this.connection.getUseStreamLengthsInPrepStmts();
            int ensurePacketSize = 0;
            String statementComment2 = this.connection.getStatementComment();
            if (statementComment2 != null) {
                if (this.charConverter != null) {
                    commentAsBytes2 = this.charConverter.toBytes(statementComment2);
                } else {
                    commentAsBytes2 = StringUtils.getBytes(statementComment2, this.charConverter, this.charEncoding, this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
                }
                ensurePacketSize = 0 + commentAsBytes2.length + 6;
                commentAsBytes = commentAsBytes2;
            } else {
                commentAsBytes = null;
            }
            int ensurePacketSize2 = ensurePacketSize;
            for (int i = 0; i < bArr.length; i++) {
                if (batchedIsStream[i] && useStreamLengths) {
                    ensurePacketSize2 += batchedStreamLengths[i];
                }
            }
            if (ensurePacketSize2 != 0) {
                sendPacket.ensureCapacity(ensurePacketSize2);
            }
            if (commentAsBytes != null) {
                sendPacket.writeBytesNoNull(Constants.SLASH_STAR_SPACE_AS_BYTES);
                sendPacket.writeBytesNoNull(commentAsBytes);
                sendPacket.writeBytesNoNull(Constants.SPACE_STAR_SLASH_SPACE_AS_BYTES);
            }
            int i2 = 0;
            while (i2 < bArr.length) {
                checkAllParametersSet(bArr[i2], batchedParameterStreams[i2], i2);
                sendPacket.writeBytesNoNull(this.staticSqlStrings[i2]);
                if (batchedIsStream[i2]) {
                    statementComment = statementComment2;
                    streamToBytes(sendPacket, batchedParameterStreams[i2], true, batchedStreamLengths[i2], useStreamLengths);
                } else {
                    statementComment = statementComment2;
                    sendPacket.writeBytesNoNull(bArr[i2]);
                }
                i2++;
                statementComment2 = statementComment;
            }
            sendPacket.writeBytesNoNull(this.staticSqlStrings[bArr.length]);
        }
        return sendPacket;
    }

    private void checkAllParametersSet(byte[] parameterString, InputStream parameterStream, int columnIndex) throws SQLException {
        if (parameterString == null && parameterStream == null) {
            throw SQLError.createSQLException(Messages.getString("PreparedStatement.40") + (columnIndex + 1), SQLError.SQL_STATE_WRONG_NO_OF_PARAMETERS, getExceptionInterceptor());
        }
    }

    /* access modifiers changed from: protected */
    public PreparedStatement prepareBatchedInsertSQL(MySQLConnection localConn, int numBatches) throws SQLException {
        PreparedStatement pstmt;
        synchronized (checkClosed().getConnectionMutex()) {
            pstmt = new PreparedStatement(localConn, "Rewritten batch of: " + this.originalSql, this.currentCatalog, this.parseInfo.getParseInfoForBatch(numBatches));
            pstmt.setRetrieveGeneratedKeys(this.retrieveGeneratedKeys);
            pstmt.rewrittenBatchSize = numBatches;
        }
        return pstmt;
    }

    /* access modifiers changed from: protected */
    public void setRetrieveGeneratedKeys(boolean flag) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            this.retrieveGeneratedKeys = flag;
        }
    }

    public int getRewrittenBatchSize() {
        return this.rewrittenBatchSize;
    }

    public String getNonRewrittenSql() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            int indexOfBatch = this.originalSql.indexOf(" of: ");
            if (indexOfBatch != -1) {
                String substring = this.originalSql.substring(indexOfBatch + 5);
                return substring;
            }
            String str = this.originalSql;
            return str;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0048, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public byte[] getBytesRepresentation(int r7) throws java.sql.SQLException {
        /*
            r6 = this;
            com.mysql.jdbc.MySQLConnection r0 = r6.checkClosed()
            java.lang.Object r0 = r0.getConnectionMutex()
            monitor-enter(r0)
            boolean[] r1 = r6.isStream     // Catch:{ all -> 0x0049 }
            boolean r1 = r1[r7]     // Catch:{ all -> 0x0049 }
            r2 = 0
            if (r1 == 0) goto L_0x0024
            java.io.InputStream[] r1 = r6.parameterStreams     // Catch:{ all -> 0x0049 }
            r1 = r1[r7]     // Catch:{ all -> 0x0049 }
            int[] r3 = r6.streamLengths     // Catch:{ all -> 0x0049 }
            r3 = r3[r7]     // Catch:{ all -> 0x0049 }
            com.mysql.jdbc.MySQLConnection r4 = r6.connection     // Catch:{ all -> 0x0049 }
            boolean r4 = r4.getUseStreamLengthsInPrepStmts()     // Catch:{ all -> 0x0049 }
            byte[] r1 = r6.streamToBytes(r1, r2, r3, r4)     // Catch:{ all -> 0x0049 }
            monitor-exit(r0)     // Catch:{ all -> 0x0049 }
            return r1
        L_0x0024:
            byte[][] r1 = r6.parameterValues     // Catch:{ all -> 0x0049 }
            r1 = r1[r7]     // Catch:{ all -> 0x0049 }
            if (r1 != 0) goto L_0x002d
            r2 = 0
            monitor-exit(r0)     // Catch:{ all -> 0x0049 }
            return r2
        L_0x002d:
            byte r3 = r1[r2]     // Catch:{ all -> 0x0049 }
            r4 = 39
            if (r3 != r4) goto L_0x0047
            int r3 = r1.length     // Catch:{ all -> 0x0049 }
            r5 = 1
            int r3 = r3 - r5
            byte r3 = r1[r3]     // Catch:{ all -> 0x0049 }
            if (r3 != r4) goto L_0x0047
            int r3 = r1.length     // Catch:{ all -> 0x0049 }
            int r3 = r3 + -2
            byte[] r3 = new byte[r3]     // Catch:{ all -> 0x0049 }
            int r4 = r1.length     // Catch:{ all -> 0x0049 }
            int r4 = r4 + -2
            java.lang.System.arraycopy(r1, r5, r3, r2, r4)     // Catch:{ all -> 0x0049 }
            monitor-exit(r0)     // Catch:{ all -> 0x0049 }
            return r3
        L_0x0047:
            monitor-exit(r0)     // Catch:{ all -> 0x0049 }
            return r1
        L_0x0049:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0049 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.PreparedStatement.getBytesRepresentation(int):byte[]");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x008a, code lost:
        return r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public byte[] getBytesRepresentationForBatch(int r9, int r10) throws java.sql.SQLException {
        /*
            r8 = this;
            com.mysql.jdbc.MySQLConnection r0 = r8.checkClosed()
            java.lang.Object r0 = r0.getConnectionMutex()
            monitor-enter(r0)
            java.util.List r1 = r8.batchedArgs     // Catch:{ all -> 0x008b }
            java.lang.Object r1 = r1.get(r10)     // Catch:{ all -> 0x008b }
            boolean r2 = r1 instanceof java.lang.String     // Catch:{ all -> 0x008b }
            if (r2 == 0) goto L_0x0048
            r2 = r1
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ UnsupportedEncodingException -> 0x001e }
            java.lang.String r3 = r8.charEncoding     // Catch:{ UnsupportedEncodingException -> 0x001e }
            byte[] r2 = com.mysql.jdbc.StringUtils.getBytes((java.lang.String) r2, (java.lang.String) r3)     // Catch:{ UnsupportedEncodingException -> 0x001e }
            monitor-exit(r0)     // Catch:{ all -> 0x008b }
            return r2
        L_0x001e:
            r2 = move-exception
            java.lang.RuntimeException r3 = new java.lang.RuntimeException     // Catch:{ all -> 0x008b }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x008b }
            r4.<init>()     // Catch:{ all -> 0x008b }
            java.lang.String r5 = "PreparedStatement.32"
            java.lang.String r5 = com.mysql.jdbc.Messages.getString(r5)     // Catch:{ all -> 0x008b }
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch:{ all -> 0x008b }
            java.lang.String r5 = r8.charEncoding     // Catch:{ all -> 0x008b }
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch:{ all -> 0x008b }
            java.lang.String r5 = "PreparedStatement.33"
            java.lang.String r5 = com.mysql.jdbc.Messages.getString(r5)     // Catch:{ all -> 0x008b }
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch:{ all -> 0x008b }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x008b }
            r3.<init>(r4)     // Catch:{ all -> 0x008b }
            throw r3     // Catch:{ all -> 0x008b }
        L_0x0048:
            r2 = r1
            com.mysql.jdbc.PreparedStatement$BatchParams r2 = (com.mysql.jdbc.PreparedStatement.BatchParams) r2     // Catch:{ all -> 0x008b }
            boolean[] r3 = r2.isStream     // Catch:{ all -> 0x008b }
            boolean r3 = r3[r9]     // Catch:{ all -> 0x008b }
            r4 = 0
            if (r3 == 0) goto L_0x0066
            java.io.InputStream[] r3 = r2.parameterStreams     // Catch:{ all -> 0x008b }
            r3 = r3[r9]     // Catch:{ all -> 0x008b }
            int[] r5 = r2.streamLengths     // Catch:{ all -> 0x008b }
            r5 = r5[r9]     // Catch:{ all -> 0x008b }
            com.mysql.jdbc.MySQLConnection r6 = r8.connection     // Catch:{ all -> 0x008b }
            boolean r6 = r6.getUseStreamLengthsInPrepStmts()     // Catch:{ all -> 0x008b }
            byte[] r3 = r8.streamToBytes(r3, r4, r5, r6)     // Catch:{ all -> 0x008b }
            monitor-exit(r0)     // Catch:{ all -> 0x008b }
            return r3
        L_0x0066:
            byte[][] r3 = r2.parameterStrings     // Catch:{ all -> 0x008b }
            r3 = r3[r9]     // Catch:{ all -> 0x008b }
            if (r3 != 0) goto L_0x006f
            r4 = 0
            monitor-exit(r0)     // Catch:{ all -> 0x008b }
            return r4
        L_0x006f:
            byte r5 = r3[r4]     // Catch:{ all -> 0x008b }
            r6 = 39
            if (r5 != r6) goto L_0x0089
            int r5 = r3.length     // Catch:{ all -> 0x008b }
            r7 = 1
            int r5 = r5 - r7
            byte r5 = r3[r5]     // Catch:{ all -> 0x008b }
            if (r5 != r6) goto L_0x0089
            int r5 = r3.length     // Catch:{ all -> 0x008b }
            int r5 = r5 + -2
            byte[] r5 = new byte[r5]     // Catch:{ all -> 0x008b }
            int r6 = r3.length     // Catch:{ all -> 0x008b }
            int r6 = r6 + -2
            java.lang.System.arraycopy(r3, r7, r5, r4, r6)     // Catch:{ all -> 0x008b }
            monitor-exit(r0)     // Catch:{ all -> 0x008b }
            return r5
        L_0x0089:
            monitor-exit(r0)     // Catch:{ all -> 0x008b }
            return r3
        L_0x008b:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x008b }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.PreparedStatement.getBytesRepresentationForBatch(int, int):byte[]");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:77:0x0205, code lost:
        if (r25 != false) goto L_0x020a;
     */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x0213  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x021c  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0232  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0234  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0237 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final java.lang.String getDateTimePattern(java.lang.String r24, boolean r25) throws java.lang.Exception {
        /*
            r23 = this;
            r0 = r23
            r1 = r24
            r2 = 0
            java.lang.Integer r3 = java.lang.Integer.valueOf(r2)
            if (r1 == 0) goto L_0x0010
            int r4 = r24.length()
            goto L_0x0011
        L_0x0010:
            r4 = r2
        L_0x0011:
            r5 = 8
            r6 = 2
            if (r4 < r5) goto L_0x003e
            r5 = 10
            if (r4 > r5) goto L_0x003e
            r5 = 0
            r7 = 1
            r8 = 0
        L_0x001d:
            if (r8 >= r4) goto L_0x0036
            char r9 = r1.charAt(r8)
            boolean r10 = java.lang.Character.isDigit(r9)
            r11 = 45
            if (r10 != 0) goto L_0x002f
            if (r9 == r11) goto L_0x002f
            r7 = 0
            goto L_0x0036
        L_0x002f:
            if (r9 != r11) goto L_0x0033
            int r5 = r5 + 1
        L_0x0033:
            int r8 = r8 + 1
            goto L_0x001d
        L_0x0036:
            if (r7 == 0) goto L_0x003e
            if (r5 != r6) goto L_0x003e
            java.lang.String r2 = "yyyy-MM-dd"
            return r2
        L_0x003e:
            r5 = 1
            r7 = 0
        L_0x0040:
            if (r7 >= r4) goto L_0x0055
            char r8 = r1.charAt(r7)
            boolean r9 = java.lang.Character.isDigit(r8)
            if (r9 != 0) goto L_0x0052
            r9 = 58
            if (r8 == r9) goto L_0x0052
            r5 = 0
            goto L_0x0055
        L_0x0052:
            int r7 = r7 + 1
            goto L_0x0040
        L_0x0055:
            if (r5 == 0) goto L_0x005a
            java.lang.String r2 = "HH:mm:ss"
            return r2
        L_0x005a:
            java.io.StringReader r7 = new java.io.StringReader
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.StringBuilder r8 = r8.append(r1)
            java.lang.String r9 = " "
            java.lang.StringBuilder r8 = r8.append(r9)
            java.lang.String r8 = r8.toString()
            r7.<init>(r8)
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            r10 = 3
            java.lang.Object[] r11 = new java.lang.Object[r10]
            r12 = 121(0x79, float:1.7E-43)
            java.lang.Character r12 = java.lang.Character.valueOf(r12)
            r11[r2] = r12
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r13 = 1
            r11[r13] = r12
            r11[r6] = r3
            r8.add(r11)
            r12 = 104(0x68, float:1.46E-43)
            if (r25 == 0) goto L_0x00ac
            java.lang.Object[] r11 = new java.lang.Object[r10]
            java.lang.Character r14 = java.lang.Character.valueOf(r12)
            r11[r2] = r14
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r11[r13] = r14
            r11[r6] = r3
            r8.add(r11)
        L_0x00ac:
            int r3 = r7.read()
            r14 = r3
            r15 = -1
            if (r3 == r15) goto L_0x01d1
            char r3 = (char) r14
            int r15 = r8.size()
            r17 = 0
            r12 = r17
        L_0x00bd:
            if (r12 >= r15) goto L_0x01b1
            java.lang.Object r18 = r8.get(r12)
            r10 = r18
            java.lang.Object[] r10 = (java.lang.Object[]) r10
            r18 = r10[r6]
            java.lang.Integer r18 = (java.lang.Integer) r18
            int r6 = r18.intValue()
            r18 = r10[r2]
            java.lang.Character r18 = (java.lang.Character) r18
            char r13 = r18.charValue()
            char r13 = r0.getSuccessor(r13, r6)
            boolean r18 = java.lang.Character.isLetterOrDigit(r3)
            if (r18 != 0) goto L_0x0113
            r18 = 0
            r22 = r10[r18]
            java.lang.Character r22 = (java.lang.Character) r22
            char r2 = r22.charValue()
            if (r13 != r2) goto L_0x00f6
            r2 = 83
            if (r13 == r2) goto L_0x00f6
            r9.add(r10)
            goto L_0x01a7
        L_0x00f6:
            r2 = 1
            r22 = r10[r2]
            r2 = r22
            java.lang.StringBuilder r2 = (java.lang.StringBuilder) r2
            r2.append(r3)
            r2 = 88
            if (r13 == r2) goto L_0x0108
            r2 = 89
            if (r13 != r2) goto L_0x01a7
        L_0x0108:
            r2 = 4
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r18 = 2
            r10[r18] = r2
            goto L_0x01a7
        L_0x0113:
            r2 = 88
            if (r13 != r2) goto L_0x0147
            r13 = 121(0x79, float:1.7E-43)
            r2 = 3
            java.lang.Object[] r11 = new java.lang.Object[r2]
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r18 = 1
            r20 = r10[r18]
            java.lang.StringBuilder r20 = (java.lang.StringBuilder) r20
            java.lang.String r1 = r20.toString()
            r2.<init>(r1)
            r1 = 77
            java.lang.StringBuilder r2 = r2.append(r1)
            r11[r18] = r2
            java.lang.Character r1 = java.lang.Character.valueOf(r1)
            r2 = 0
            r11[r2] = r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r18)
            r2 = 2
            r11[r2] = r1
            r8.add(r11)
            r18 = 1
            goto L_0x017b
        L_0x0147:
            r1 = 89
            if (r13 != r1) goto L_0x0179
            r13 = 77
            r1 = 3
            java.lang.Object[] r11 = new java.lang.Object[r1]
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r18 = 1
            r19 = r10[r18]
            java.lang.StringBuilder r19 = (java.lang.StringBuilder) r19
            java.lang.String r1 = r19.toString()
            r2.<init>(r1)
            r1 = 100
            java.lang.StringBuilder r2 = r2.append(r1)
            r11[r18] = r2
            java.lang.Character r2 = java.lang.Character.valueOf(r1)
            r1 = 0
            r11[r1] = r2
            java.lang.Integer r1 = java.lang.Integer.valueOf(r18)
            r2 = 2
            r11[r2] = r1
            r8.add(r11)
            goto L_0x017b
        L_0x0179:
            r18 = 1
        L_0x017b:
            r1 = r10[r18]
            java.lang.StringBuilder r1 = (java.lang.StringBuilder) r1
            r1.append(r13)
            r1 = 0
            r2 = r10[r1]
            java.lang.Character r2 = (java.lang.Character) r2
            char r1 = r2.charValue()
            if (r13 != r1) goto L_0x0197
            int r1 = r6 + 1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2 = 2
            r10[r2] = r1
            goto L_0x01a7
        L_0x0197:
            r2 = 2
            java.lang.Character r1 = java.lang.Character.valueOf(r13)
            r18 = 0
            r10[r18] = r1
            r1 = 1
            java.lang.Integer r18 = java.lang.Integer.valueOf(r1)
            r10[r2] = r18
        L_0x01a7:
            int r12 = r12 + 1
            r1 = r24
            r2 = 0
            r6 = 2
            r10 = 3
            r13 = 1
            goto L_0x00bd
        L_0x01b1:
            int r1 = r9.size()
            r2 = 0
        L_0x01b6:
            if (r2 >= r1) goto L_0x01c4
            java.lang.Object r6 = r9.get(r2)
            java.lang.Object[] r6 = (java.lang.Object[]) r6
            r8.remove(r6)
            int r2 = r2 + 1
            goto L_0x01b6
        L_0x01c4:
            r9.clear()
            r1 = r24
            r2 = 0
            r6 = 2
            r10 = 3
            r12 = 104(0x68, float:1.46E-43)
            r13 = 1
            goto L_0x00ac
        L_0x01d1:
            int r1 = r8.size()
            r2 = 0
        L_0x01d6:
            if (r2 >= r1) goto L_0x0243
            java.lang.Object r3 = r8.get(r2)
            java.lang.Object[] r3 = (java.lang.Object[]) r3
            r6 = 0
            r10 = r3[r6]
            java.lang.Character r10 = (java.lang.Character) r10
            char r6 = r10.charValue()
            r10 = 2
            r12 = r3[r10]
            java.lang.Integer r12 = (java.lang.Integer) r12
            int r12 = r12.intValue()
            char r13 = r0.getSuccessor(r6, r12)
            if (r13 == r6) goto L_0x01f8
            r13 = 1
            goto L_0x01f9
        L_0x01f8:
            r13 = 0
        L_0x01f9:
            r10 = 115(0x73, float:1.61E-43)
            if (r6 == r10) goto L_0x0208
            r10 = 109(0x6d, float:1.53E-43)
            if (r6 == r10) goto L_0x0208
            r10 = 104(0x68, float:1.46E-43)
            if (r6 != r10) goto L_0x020f
            if (r25 == 0) goto L_0x020f
            goto L_0x020a
        L_0x0208:
            r10 = 104(0x68, float:1.46E-43)
        L_0x020a:
            if (r13 == 0) goto L_0x020f
            r16 = 1
            goto L_0x0211
        L_0x020f:
            r16 = 0
        L_0x0211:
            if (r13 == 0) goto L_0x021c
            r10 = 100
            if (r6 != r10) goto L_0x021e
            if (r25 != 0) goto L_0x021e
            r17 = 1
            goto L_0x0220
        L_0x021c:
            r10 = 100
        L_0x021e:
            r17 = 0
        L_0x0220:
            r19 = 1
            r21 = r3[r19]
            java.lang.StringBuilder r21 = (java.lang.StringBuilder) r21
            java.lang.String r10 = r21.toString()
            r0 = 87
            int r0 = r10.indexOf(r0)
            if (r0 == r15) goto L_0x0234
            r0 = 1
            goto L_0x0235
        L_0x0234:
            r0 = 0
        L_0x0235:
            if (r16 != 0) goto L_0x0239
            if (r17 == 0) goto L_0x023b
        L_0x0239:
            if (r0 == 0) goto L_0x023e
        L_0x023b:
            r9.add(r3)
        L_0x023e:
            int r2 = r2 + 1
            r0 = r23
            goto L_0x01d6
        L_0x0243:
            int r0 = r9.size()
            r1 = 0
        L_0x0248:
            if (r1 >= r0) goto L_0x0254
            java.lang.Object r2 = r9.get(r1)
            r8.remove(r2)
            int r1 = r1 + 1
            goto L_0x0248
        L_0x0254:
            r9.clear()
            r1 = 0
            java.lang.Object r1 = r8.get(r1)
            java.lang.Object[] r1 = (java.lang.Object[]) r1
            r2 = 1
            r3 = r1[r2]
            java.lang.StringBuilder r3 = (java.lang.StringBuilder) r3
            int r6 = r3.length()
            int r6 = r6 - r2
            r3.setLength(r6)
            java.lang.String r2 = r3.toString()
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.PreparedStatement.getDateTimePattern(java.lang.String, boolean):java.lang.String");
    }

    /* JADX INFO: finally extract failed */
    public ResultSetMetaData getMetaData() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!isSelectQuery()) {
                return null;
            }
            PreparedStatement mdStmt = null;
            ResultSet mdRs = null;
            if (this.pstmtResultMetaData == null) {
                try {
                    PreparedStatement mdStmt2 = new PreparedStatement(this.connection, this.originalSql, this.currentCatalog, this.parseInfo);
                    mdStmt2.setMaxRows(1);
                    int paramCount = this.parameterValues.length;
                    for (int i = 1; i <= paramCount; i++) {
                        mdStmt2.setString(i, (String) null);
                    }
                    if (mdStmt2.execute()) {
                        mdRs = mdStmt2.getResultSet();
                        this.pstmtResultMetaData = mdRs.getMetaData();
                    } else {
                        this.pstmtResultMetaData = new ResultSetMetaData(new Field[0], this.connection.getUseOldAliasMetadataBehavior(), this.connection.getYearIsDateType(), getExceptionInterceptor());
                    }
                    SQLException sqlExRethrow = null;
                    if (mdRs != null) {
                        try {
                            mdRs.close();
                        } catch (SQLException sqlEx) {
                            sqlExRethrow = sqlEx;
                        }
                    }
                    try {
                        mdStmt2.close();
                    } catch (SQLException sqlEx2) {
                        sqlExRethrow = sqlEx2;
                    }
                    if (sqlExRethrow != null) {
                        throw sqlExRethrow;
                    }
                } catch (Throwable sqlExRethrow2) {
                    SQLException sqlExRethrow3 = null;
                    if (mdRs != null) {
                        try {
                            mdRs.close();
                        } catch (SQLException sqlEx3) {
                            sqlExRethrow3 = sqlEx3;
                        }
                    }
                    if (mdStmt != null) {
                        try {
                            mdStmt.close();
                        } catch (SQLException sqlEx4) {
                            sqlExRethrow3 = sqlEx4;
                        }
                    }
                    if (sqlExRethrow3 != null) {
                        throw sqlExRethrow3;
                    }
                    throw sqlExRethrow2;
                }
            }
            ResultSetMetaData resultSetMetaData = this.pstmtResultMetaData;
            return resultSetMetaData;
        }
    }

    /* access modifiers changed from: protected */
    public boolean isSelectQuery() throws SQLException {
        boolean startsWithIgnoreCaseAndWs;
        synchronized (checkClosed().getConnectionMutex()) {
            startsWithIgnoreCaseAndWs = StringUtils.startsWithIgnoreCaseAndWs(StringUtils.stripComments(this.originalSql, "'\"", "'\"", true, false, true, true), "SELECT");
        }
        return startsWithIgnoreCaseAndWs;
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        MysqlParameterMetadata mysqlParameterMetadata;
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.parameterMetaData == null) {
                if (this.connection.getGenerateSimpleParameterMetadata()) {
                    this.parameterMetaData = new MysqlParameterMetadata(this.parameterCount);
                } else {
                    this.parameterMetaData = new MysqlParameterMetadata((Field[]) null, this.parameterCount, getExceptionInterceptor());
                }
            }
            mysqlParameterMetadata = this.parameterMetaData;
        }
        return mysqlParameterMetadata;
    }

    /* access modifiers changed from: package-private */
    public ParseInfo getParseInfo() {
        return this.parseInfo;
    }

    private final char getSuccessor(char c, int n) {
        if (c == 'y' && n == 2) {
            return 'X';
        }
        if (c == 'y' && n < 4) {
            return 'y';
        }
        if (c != 'y') {
            if (c == 'M' && n == 2) {
                return 'Y';
            }
            if (c != 'M' || n >= 3) {
                if (c == 'M' || (c == 'd' && n < 2)) {
                    return 'd';
                }
                if (c == 'd' || (c == 'H' && n < 2)) {
                    return 'H';
                }
                if (c == 'H' || (c == 'm' && n < 2)) {
                    return 'm';
                }
                if (c == 'm') {
                    return 's';
                }
                return (c != 's' || n >= 2) ? 'W' : 's';
            }
        }
        return 'M';
    }

    private final void hexEscapeBlock(byte[] buf, Buffer packet, int size) throws SQLException {
        for (int i = 0; i < size; i++) {
            byte b = buf[i];
            byte[] bArr = HEX_DIGITS;
            packet.writeByte(bArr[(b & 255) / 16]);
            packet.writeByte(bArr[(b & 255) % 16]);
        }
    }

    private void initializeFromParseInfo() throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            this.staticSqlStrings = this.parseInfo.staticSql;
            this.isLoadDataQuery = this.parseInfo.foundLoadData;
            this.firstCharOfStmt = this.parseInfo.firstStmtChar;
            int length = this.staticSqlStrings.length - 1;
            this.parameterCount = length;
            this.parameterValues = new byte[length][];
            this.parameterStreams = new InputStream[length];
            this.isStream = new boolean[length];
            this.streamLengths = new int[length];
            this.isNull = new boolean[length];
            this.parameterTypes = new int[length];
            clearParameters();
            for (int j = 0; j < this.parameterCount; j++) {
                this.isStream[j] = false;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isNull(int paramIndex) throws SQLException {
        boolean z;
        synchronized (checkClosed().getConnectionMutex()) {
            z = this.isNull[paramIndex];
        }
        return z;
    }

    private final int readblock(InputStream i, byte[] b) throws SQLException {
        try {
            return i.read(b);
        } catch (Throwable ex) {
            SQLException sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.56") + ex.getClass().getName(), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
            sqlEx.initCause(ex);
            throw sqlEx;
        }
    }

    private final int readblock(InputStream i, byte[] b, int length) throws SQLException {
        int lengthToRead = length;
        try {
            if (lengthToRead > b.length) {
                lengthToRead = b.length;
            }
            return i.read(b, 0, lengthToRead);
        } catch (Throwable ex) {
            SQLException sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.56") + ex.getClass().getName(), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
            sqlEx.initCause(ex);
            throw sqlEx;
        }
    }

    /* access modifiers changed from: protected */
    public void realClose(boolean calledExplicitly, boolean closeOpenResults) throws SQLException {
        MySQLConnection locallyScopedConn = this.connection;
        if (locallyScopedConn != null) {
            synchronized (locallyScopedConn.getConnectionMutex()) {
                if (!this.isClosed) {
                    if (this.useUsageAdvisor && this.numberOfExecutions <= 1) {
                        this.connection.getProfilerEventHandlerInstance().processEvent((byte) 0, this.connection, this, (ResultSetInternalMethods) null, 0, new Throwable(), Messages.getString("PreparedStatement.43"));
                    }
                    super.realClose(calledExplicitly, closeOpenResults);
                    this.dbmd = null;
                    this.originalSql = null;
                    byte[][] bArr = null;
                    this.staticSqlStrings = null;
                    byte[][] bArr2 = null;
                    this.parameterValues = null;
                    this.parameterStreams = null;
                    this.isStream = null;
                    this.streamLengths = null;
                    this.isNull = null;
                    this.streamConvertBuf = null;
                    this.parameterTypes = null;
                }
            }
        }
    }

    public void setArray(int i, Array x) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        if (x == null) {
            setNull(parameterIndex, 12);
        } else {
            setBinaryStream(parameterIndex, x, length);
        }
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        if (x == null) {
            setNull(parameterIndex, 3);
            return;
        }
        setInternal(parameterIndex, StringUtils.fixDecimalExponent(StringUtils.consistentToString(x)));
        this.parameterTypes[(parameterIndex - 1) + getParameterIndexOffset()] = 3;
    }

    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (x == null) {
                setNull(parameterIndex, -2);
            } else {
                int parameterIndexOffset = getParameterIndexOffset();
                if (parameterIndex < 1 || parameterIndex > this.staticSqlStrings.length) {
                    throw SQLError.createSQLException(Messages.getString("PreparedStatement.2") + parameterIndex + Messages.getString("PreparedStatement.3") + this.staticSqlStrings.length + Messages.getString("PreparedStatement.4"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                }
                if (parameterIndexOffset == -1) {
                    if (parameterIndex == 1) {
                        throw SQLError.createSQLException("Can't set IN parameter for return value of stored function call.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                    }
                }
                this.parameterStreams[(parameterIndex - 1) + parameterIndexOffset] = x;
                this.isStream[(parameterIndex - 1) + parameterIndexOffset] = true;
                this.streamLengths[(parameterIndex - 1) + parameterIndexOffset] = length;
                this.isNull[(parameterIndex - 1) + parameterIndexOffset] = false;
                this.parameterTypes[(parameterIndex - 1) + getParameterIndexOffset()] = 2004;
            }
        }
    }

    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        setBinaryStream(parameterIndex, inputStream, (int) length);
    }

    public void setBlob(int i, Blob x) throws SQLException {
        if (x == null) {
            setNull(i, 2004);
            return;
        }
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        bytesOut.write(39);
        escapeblockFast(x.getBytes(1, (int) x.length()), bytesOut, (int) x.length());
        bytesOut.write(39);
        setInternal(i, bytesOut.toByteArray());
        this.parameterTypes[(i - 1) + getParameterIndexOffset()] = 2004;
    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        if (this.useTrueBoolean) {
            setInternal(parameterIndex, x ? "1" : "0");
            return;
        }
        setInternal(parameterIndex, x ? "'t'" : "'f'");
        this.parameterTypes[(parameterIndex - 1) + getParameterIndexOffset()] = 16;
    }

    public void setByte(int parameterIndex, byte x) throws SQLException {
        setInternal(parameterIndex, String.valueOf(x));
        this.parameterTypes[(parameterIndex - 1) + getParameterIndexOffset()] = -6;
    }

    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        setBytes(parameterIndex, x, true, true);
        if (x != null) {
            this.parameterTypes[(parameterIndex - 1) + getParameterIndexOffset()] = -2;
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00d1, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setBytes(int r16, byte[] r17, boolean r18, boolean r19) throws java.sql.SQLException {
        /*
            r15 = this;
            r1 = r15
            r2 = r16
            r3 = r17
            com.mysql.jdbc.MySQLConnection r0 = r15.checkClosed()
            java.lang.Object r4 = r0.getConnectionMutex()
            monitor-enter(r4)
            if (r3 != 0) goto L_0x0016
            r0 = -2
            r15.setNull(r2, r0)     // Catch:{ all -> 0x0123 }
            goto L_0x00d0
        L_0x0016:
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ all -> 0x0123 }
            java.lang.String r0 = r0.getEncoding()     // Catch:{ all -> 0x0123 }
            r5 = r0
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            boolean r0 = r0.isNoBackslashEscapesSet()     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            r6 = 39
            if (r0 != 0) goto L_0x00d2
            if (r19 == 0) goto L_0x003b
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            boolean r0 = r0.getUseUnicode()     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            if (r0 == 0) goto L_0x003b
            if (r5 == 0) goto L_0x003b
            boolean r0 = com.mysql.jdbc.CharsetMapping.isMultibyteCharset(r5)     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            if (r0 == 0) goto L_0x003b
            goto L_0x00d2
        L_0x003b:
            int r0 = r3.length     // Catch:{ all -> 0x0123 }
            r7 = 2
            r8 = 0
            r9 = 1
            if (r18 == 0) goto L_0x004c
            com.mysql.jdbc.MySQLConnection r10 = r1.connection     // Catch:{ all -> 0x0123 }
            r11 = 4
            boolean r10 = r10.versionMeetsMinimum(r11, r9, r8)     // Catch:{ all -> 0x0123 }
            if (r10 == 0) goto L_0x004c
            r8 = r9
        L_0x004c:
            if (r8 == 0) goto L_0x0050
            int r7 = r7 + 7
        L_0x0050:
            java.io.ByteArrayOutputStream r9 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x0123 }
            int r10 = r0 + r7
            r9.<init>(r10)     // Catch:{ all -> 0x0123 }
            r10 = 114(0x72, float:1.6E-43)
            r11 = 110(0x6e, float:1.54E-43)
            if (r8 == 0) goto L_0x007c
            r12 = 95
            r9.write(r12)     // Catch:{ all -> 0x0123 }
            r12 = 98
            r9.write(r12)     // Catch:{ all -> 0x0123 }
            r12 = 105(0x69, float:1.47E-43)
            r9.write(r12)     // Catch:{ all -> 0x0123 }
            r9.write(r11)     // Catch:{ all -> 0x0123 }
            r12 = 97
            r9.write(r12)     // Catch:{ all -> 0x0123 }
            r9.write(r10)     // Catch:{ all -> 0x0123 }
            r12 = 121(0x79, float:1.7E-43)
            r9.write(r12)     // Catch:{ all -> 0x0123 }
        L_0x007c:
            r9.write(r6)     // Catch:{ all -> 0x0123 }
            r12 = 0
        L_0x0080:
            if (r12 >= r0) goto L_0x00c6
            byte r13 = r3[r12]     // Catch:{ all -> 0x0123 }
            r14 = 92
            switch(r13) {
                case 0: goto L_0x00bb;
                case 10: goto L_0x00b4;
                case 13: goto L_0x00ad;
                case 26: goto L_0x00a4;
                case 34: goto L_0x009b;
                case 39: goto L_0x0094;
                case 92: goto L_0x008d;
                default: goto L_0x0089;
            }     // Catch:{ all -> 0x0123 }
        L_0x0089:
            r9.write(r13)     // Catch:{ all -> 0x0123 }
            goto L_0x00c3
        L_0x008d:
            r9.write(r14)     // Catch:{ all -> 0x0123 }
            r9.write(r14)     // Catch:{ all -> 0x0123 }
            goto L_0x00c3
        L_0x0094:
            r9.write(r14)     // Catch:{ all -> 0x0123 }
            r9.write(r6)     // Catch:{ all -> 0x0123 }
            goto L_0x00c3
        L_0x009b:
            r9.write(r14)     // Catch:{ all -> 0x0123 }
            r14 = 34
            r9.write(r14)     // Catch:{ all -> 0x0123 }
            goto L_0x00c3
        L_0x00a4:
            r9.write(r14)     // Catch:{ all -> 0x0123 }
            r14 = 90
            r9.write(r14)     // Catch:{ all -> 0x0123 }
            goto L_0x00c3
        L_0x00ad:
            r9.write(r14)     // Catch:{ all -> 0x0123 }
            r9.write(r10)     // Catch:{ all -> 0x0123 }
            goto L_0x00c3
        L_0x00b4:
            r9.write(r14)     // Catch:{ all -> 0x0123 }
            r9.write(r11)     // Catch:{ all -> 0x0123 }
            goto L_0x00c3
        L_0x00bb:
            r9.write(r14)     // Catch:{ all -> 0x0123 }
            r14 = 48
            r9.write(r14)     // Catch:{ all -> 0x0123 }
        L_0x00c3:
            int r12 = r12 + 1
            goto L_0x0080
        L_0x00c6:
            r9.write(r6)     // Catch:{ all -> 0x0123 }
            byte[] r6 = r9.toByteArray()     // Catch:{ all -> 0x0123 }
            r15.setInternal((int) r2, (byte[]) r6)     // Catch:{ all -> 0x0123 }
        L_0x00d0:
            monitor-exit(r4)     // Catch:{ all -> 0x0123 }
            return
        L_0x00d2:
            java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            int r7 = r3.length     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            int r7 = r7 * 2
            int r7 = r7 + 3
            r0.<init>(r7)     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            r7 = 120(0x78, float:1.68E-43)
            r0.write(r7)     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            r0.write(r6)     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            r7 = 0
        L_0x00e5:
            int r8 = r3.length     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            if (r7 >= r8) goto L_0x0103
            byte r8 = r3[r7]     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            r8 = r8 & 255(0xff, float:3.57E-43)
            int r8 = r8 / 16
            byte r9 = r3[r7]     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            r9 = r9 & 255(0xff, float:3.57E-43)
            int r9 = r9 % 16
            byte[] r10 = HEX_DIGITS     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            byte r11 = r10[r8]     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            r0.write(r11)     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            byte r10 = r10[r9]     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            r0.write(r10)     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            int r7 = r7 + 1
            goto L_0x00e5
        L_0x0103:
            r0.write(r6)     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            byte[] r6 = r0.toByteArray()     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            r15.setInternal((int) r2, (byte[]) r6)     // Catch:{ SQLException -> 0x0120, RuntimeException -> 0x010f }
            monitor-exit(r4)     // Catch:{ all -> 0x0123 }
            return
        L_0x010f:
            r0 = move-exception
            java.lang.String r6 = r0.toString()     // Catch:{ all -> 0x0123 }
            java.lang.String r7 = "S1009"
            r8 = 0
            java.sql.SQLException r6 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r6, (java.lang.String) r7, (com.mysql.jdbc.ExceptionInterceptor) r8)     // Catch:{ all -> 0x0123 }
            r6.initCause(r0)     // Catch:{ all -> 0x0123 }
            throw r6     // Catch:{ all -> 0x0123 }
        L_0x0120:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x0123 }
        L_0x0123:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0123 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.PreparedStatement.setBytes(int, byte[], boolean, boolean):void");
    }

    /* access modifiers changed from: protected */
    public void setBytesNoEscape(int parameterIndex, byte[] parameterAsBytes) throws SQLException {
        byte[] parameterWithQuotes = new byte[(parameterAsBytes.length + 2)];
        parameterWithQuotes[0] = 39;
        System.arraycopy(parameterAsBytes, 0, parameterWithQuotes, 1, parameterAsBytes.length);
        parameterWithQuotes[parameterAsBytes.length + 1] = 39;
        setInternal(parameterIndex, parameterWithQuotes);
    }

    /* access modifiers changed from: protected */
    public void setBytesNoEscapeNoQuotes(int parameterIndex, byte[] parameterAsBytes) throws SQLException {
        setInternal(parameterIndex, parameterAsBytes);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0061, code lost:
        throw com.mysql.jdbc.SQLError.createSQLException("Unsupported character encoding " + r5, com.mysql.jdbc.SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00bc, code lost:
        throw com.mysql.jdbc.SQLError.createSQLException("Unsupported character encoding " + r5, com.mysql.jdbc.SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00bf, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00ce, code lost:
        throw com.mysql.jdbc.SQLError.createSQLException(r1.toString(), com.mysql.jdbc.SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:4:0x000c, B:12:0x0035, B:24:0x0081] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setCharacterStream(int r11, java.io.Reader r12, int r13) throws java.sql.SQLException {
        /*
            r10 = this;
            com.mysql.jdbc.MySQLConnection r0 = r10.checkClosed()
            java.lang.Object r0 = r0.getConnectionMutex()
            monitor-enter(r0)
            r1 = -1
            if (r12 != 0) goto L_0x0011
            r10.setNull(r11, r1)     // Catch:{ IOException -> 0x00bf }
            goto L_0x009b
        L_0x0011:
            r2 = 0
            r3 = 0
            com.mysql.jdbc.MySQLConnection r4 = r10.connection     // Catch:{ IOException -> 0x00bf }
            boolean r4 = r4.getUseStreamLengthsInPrepStmts()     // Catch:{ IOException -> 0x00bf }
            com.mysql.jdbc.MySQLConnection r5 = r10.connection     // Catch:{ IOException -> 0x00bf }
            java.lang.String r5 = r5.getClobCharacterEncoding()     // Catch:{ IOException -> 0x00bf }
            r6 = 0
            if (r4 == 0) goto L_0x0062
            if (r13 == r1) goto L_0x0062
            char[] r1 = new char[r13]     // Catch:{ IOException -> 0x00bf }
            int r2 = readFully(r12, r1, r13)     // Catch:{ IOException -> 0x00bf }
            if (r5 != 0) goto L_0x0035
            java.lang.String r7 = new java.lang.String     // Catch:{ IOException -> 0x00bf }
            r7.<init>(r1, r6, r2)     // Catch:{ IOException -> 0x00bf }
            r10.setString(r11, r7)     // Catch:{ IOException -> 0x00bf }
            goto L_0x0042
        L_0x0035:
            java.lang.String r7 = new java.lang.String     // Catch:{ UnsupportedEncodingException -> 0x0043 }
            r7.<init>(r1, r6, r2)     // Catch:{ UnsupportedEncodingException -> 0x0043 }
            byte[] r6 = com.mysql.jdbc.StringUtils.getBytes((java.lang.String) r7, (java.lang.String) r5)     // Catch:{ UnsupportedEncodingException -> 0x0043 }
            r10.setBytes(r11, r6)     // Catch:{ UnsupportedEncodingException -> 0x0043 }
        L_0x0042:
            goto L_0x008e
        L_0x0043:
            r6 = move-exception
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00bf }
            r7.<init>()     // Catch:{ IOException -> 0x00bf }
            java.lang.String r8 = "Unsupported character encoding "
            java.lang.StringBuilder r7 = r7.append(r8)     // Catch:{ IOException -> 0x00bf }
            java.lang.StringBuilder r7 = r7.append(r5)     // Catch:{ IOException -> 0x00bf }
            java.lang.String r7 = r7.toString()     // Catch:{ IOException -> 0x00bf }
            java.lang.String r8 = "S1009"
            com.mysql.jdbc.ExceptionInterceptor r9 = r10.getExceptionInterceptor()     // Catch:{ IOException -> 0x00bf }
            java.sql.SQLException r7 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r7, (java.lang.String) r8, (com.mysql.jdbc.ExceptionInterceptor) r9)     // Catch:{ IOException -> 0x00bf }
            throw r7     // Catch:{ IOException -> 0x00bf }
        L_0x0062:
            r7 = 4096(0x1000, float:5.74E-42)
            char[] r7 = new char[r7]     // Catch:{ IOException -> 0x00bf }
            r2 = r7
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00bf }
            r7.<init>()     // Catch:{ IOException -> 0x00bf }
        L_0x006c:
            int r8 = r12.read(r2)     // Catch:{ IOException -> 0x00bf }
            r3 = r8
            if (r8 == r1) goto L_0x0077
            r7.append(r2, r6, r3)     // Catch:{ IOException -> 0x00bf }
            goto L_0x006c
        L_0x0077:
            if (r5 != 0) goto L_0x0081
            java.lang.String r1 = r7.toString()     // Catch:{ IOException -> 0x00bf }
            r10.setString(r11, r1)     // Catch:{ IOException -> 0x00bf }
            goto L_0x008d
        L_0x0081:
            java.lang.String r1 = r7.toString()     // Catch:{ UnsupportedEncodingException -> 0x009e }
            byte[] r1 = com.mysql.jdbc.StringUtils.getBytes((java.lang.String) r1, (java.lang.String) r5)     // Catch:{ UnsupportedEncodingException -> 0x009e }
            r10.setBytes(r11, r1)     // Catch:{ UnsupportedEncodingException -> 0x009e }
        L_0x008d:
            r1 = r2
        L_0x008e:
            int[] r2 = r10.parameterTypes     // Catch:{ IOException -> 0x00bf }
            int r6 = r11 + -1
            int r7 = r10.getParameterIndexOffset()     // Catch:{ IOException -> 0x00bf }
            int r6 = r6 + r7
            r7 = 2005(0x7d5, float:2.81E-42)
            r2[r6] = r7     // Catch:{ IOException -> 0x00bf }
        L_0x009b:
            monitor-exit(r0)     // Catch:{ all -> 0x00bd }
            return
        L_0x009e:
            r1 = move-exception
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00bf }
            r6.<init>()     // Catch:{ IOException -> 0x00bf }
            java.lang.String r8 = "Unsupported character encoding "
            java.lang.StringBuilder r6 = r6.append(r8)     // Catch:{ IOException -> 0x00bf }
            java.lang.StringBuilder r6 = r6.append(r5)     // Catch:{ IOException -> 0x00bf }
            java.lang.String r6 = r6.toString()     // Catch:{ IOException -> 0x00bf }
            java.lang.String r8 = "S1009"
            com.mysql.jdbc.ExceptionInterceptor r9 = r10.getExceptionInterceptor()     // Catch:{ IOException -> 0x00bf }
            java.sql.SQLException r6 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r6, (java.lang.String) r8, (com.mysql.jdbc.ExceptionInterceptor) r9)     // Catch:{ IOException -> 0x00bf }
            throw r6     // Catch:{ IOException -> 0x00bf }
        L_0x00bd:
            r1 = move-exception
            goto L_0x00cf
        L_0x00bf:
            r1 = move-exception
            java.lang.String r2 = r1.toString()     // Catch:{ all -> 0x00bd }
            java.lang.String r3 = "S1000"
            com.mysql.jdbc.ExceptionInterceptor r4 = r10.getExceptionInterceptor()     // Catch:{ all -> 0x00bd }
            java.sql.SQLException r2 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r2, (java.lang.String) r3, (com.mysql.jdbc.ExceptionInterceptor) r4)     // Catch:{ all -> 0x00bd }
            throw r2     // Catch:{ all -> 0x00bd }
        L_0x00cf:
            monitor-exit(r0)     // Catch:{ all -> 0x00bd }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.PreparedStatement.setCharacterStream(int, java.io.Reader, int):void");
    }

    public void setClob(int i, Clob x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (x == null) {
                setNull(i, 2005);
            } else {
                String forcedEncoding = this.connection.getClobCharacterEncoding();
                if (forcedEncoding == null) {
                    setString(i, x.getSubString(1, (int) x.length()));
                } else {
                    try {
                        setBytes(i, StringUtils.getBytes(x.getSubString(1, (int) x.length()), forcedEncoding));
                    } catch (UnsupportedEncodingException e) {
                        throw SQLError.createSQLException("Unsupported character encoding " + forcedEncoding, SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                    }
                }
                this.parameterTypes[(i - 1) + getParameterIndexOffset()] = 2005;
            }
        }
    }

    public void setDate(int parameterIndex, java.sql.Date x) throws SQLException {
        setDate(parameterIndex, x, (Calendar) null);
    }

    public void setDate(int parameterIndex, java.sql.Date x, Calendar cal) throws SQLException {
        if (x == null) {
            setNull(parameterIndex, 91);
        } else if (!this.useLegacyDatetimeCode) {
            newSetDateInternal(parameterIndex, x, cal);
        } else {
            synchronized (checkClosed().getConnectionMutex()) {
                SimpleDateFormat simpleDateFormat = TimeUtil.getSimpleDateFormat(this.ddf, "''yyyy-MM-dd''", cal, (TimeZone) null);
                this.ddf = simpleDateFormat;
                setInternal(parameterIndex, simpleDateFormat.format(x));
                this.parameterTypes[(parameterIndex - 1) + getParameterIndexOffset()] = 91;
            }
        }
    }

    public void setDouble(int parameterIndex, double x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (!this.connection.getAllowNanAndInf()) {
                if (x == Double.POSITIVE_INFINITY || x == Double.NEGATIVE_INFINITY || Double.isNaN(x)) {
                    throw SQLError.createSQLException("'" + x + "' is not a valid numeric or approximate numeric value", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                }
            }
            setInternal(parameterIndex, StringUtils.fixDecimalExponent(String.valueOf(x)));
            this.parameterTypes[(parameterIndex - 1) + getParameterIndexOffset()] = 8;
        }
    }

    public void setFloat(int parameterIndex, float x) throws SQLException {
        setInternal(parameterIndex, StringUtils.fixDecimalExponent(String.valueOf(x)));
        this.parameterTypes[(parameterIndex - 1) + getParameterIndexOffset()] = 6;
    }

    public void setInt(int parameterIndex, int x) throws SQLException {
        setInternal(parameterIndex, String.valueOf(x));
        this.parameterTypes[(parameterIndex - 1) + getParameterIndexOffset()] = 4;
    }

    /* access modifiers changed from: protected */
    public final void setInternal(int paramIndex, byte[] val) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            int parameterIndexOffset = getParameterIndexOffset();
            checkBounds(paramIndex, parameterIndexOffset);
            this.isStream[(paramIndex - 1) + parameterIndexOffset] = false;
            this.isNull[(paramIndex - 1) + parameterIndexOffset] = false;
            this.parameterStreams[(paramIndex - 1) + parameterIndexOffset] = null;
            this.parameterValues[(paramIndex - 1) + parameterIndexOffset] = val;
        }
    }

    /* access modifiers changed from: protected */
    public void checkBounds(int paramIndex, int parameterIndexOffset) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (paramIndex >= 1) {
                try {
                    if (paramIndex > this.parameterCount) {
                        throw SQLError.createSQLException(Messages.getString("PreparedStatement.51") + paramIndex + Messages.getString("PreparedStatement.52") + this.parameterValues.length + Messages.getString("PreparedStatement.53"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                    } else if (parameterIndexOffset == -1) {
                        if (paramIndex == 1) {
                            throw SQLError.createSQLException("Can't set IN parameter for return value of stored function call.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                throw SQLError.createSQLException(Messages.getString("PreparedStatement.49") + paramIndex + Messages.getString("PreparedStatement.50"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            }
        }
    }

    /* access modifiers changed from: protected */
    public final void setInternal(int paramIndex, String val) throws SQLException {
        byte[] parameterAsBytes;
        synchronized (checkClosed().getConnectionMutex()) {
            if (this.charConverter != null) {
                parameterAsBytes = this.charConverter.toBytes(val);
            } else {
                parameterAsBytes = StringUtils.getBytes(val, this.charConverter, this.charEncoding, this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
            }
            setInternal(paramIndex, parameterAsBytes);
        }
    }

    public void setLong(int parameterIndex, long x) throws SQLException {
        setInternal(parameterIndex, String.valueOf(x));
        this.parameterTypes[(parameterIndex - 1) + getParameterIndexOffset()] = -5;
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            setInternal(parameterIndex, "null");
            this.isNull[(parameterIndex - 1) + getParameterIndexOffset()] = true;
            this.parameterTypes[(parameterIndex - 1) + getParameterIndexOffset()] = 0;
        }
    }

    public void setNull(int parameterIndex, int sqlType, String arg) throws SQLException {
        setNull(parameterIndex, sqlType);
        this.parameterTypes[(parameterIndex - 1) + getParameterIndexOffset()] = 0;
    }

    private void setNumericObject(int parameterIndex, Object parameterObj, int targetSqlType, int scale) throws SQLException {
        Number parameterAsNum;
        BigDecimal scaledBigDecimal;
        int i = 1;
        if (parameterObj instanceof Boolean) {
            if (!((Boolean) parameterObj).booleanValue()) {
                i = 0;
            }
            parameterAsNum = i;
        } else if (parameterObj instanceof String) {
            switch (targetSqlType) {
                case -7:
                    if (!"1".equals(parameterObj) && !"0".equals(parameterObj)) {
                        if (!"true".equalsIgnoreCase((String) parameterObj)) {
                            i = 0;
                        }
                        parameterAsNum = i;
                        break;
                    } else {
                        parameterAsNum = Integer.valueOf((String) parameterObj);
                        break;
                    }
                    break;
                case -6:
                case 4:
                case 5:
                    parameterAsNum = Integer.valueOf((String) parameterObj);
                    break;
                case -5:
                    parameterAsNum = Long.valueOf((String) parameterObj);
                    break;
                case 6:
                case 8:
                    parameterAsNum = Double.valueOf((String) parameterObj);
                    break;
                case 7:
                    parameterAsNum = Float.valueOf((String) parameterObj);
                    break;
                default:
                    parameterAsNum = new BigDecimal((String) parameterObj);
                    break;
            }
        } else {
            parameterAsNum = (Number) parameterObj;
        }
        switch (targetSqlType) {
            case -7:
            case -6:
            case 4:
            case 5:
                setInt(parameterIndex, parameterAsNum.intValue());
                return;
            case -5:
                setLong(parameterIndex, parameterAsNum.longValue());
                return;
            case 2:
            case 3:
                if (parameterAsNum instanceof BigDecimal) {
                    try {
                        scaledBigDecimal = ((BigDecimal) parameterAsNum).setScale(scale);
                    } catch (ArithmeticException e) {
                        try {
                            scaledBigDecimal = ((BigDecimal) parameterAsNum).setScale(scale, 4);
                        } catch (ArithmeticException e2) {
                            throw SQLError.createSQLException("Can't set scale of '" + scale + "' for DECIMAL argument '" + parameterAsNum + "'", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                        }
                    }
                    setBigDecimal(parameterIndex, scaledBigDecimal);
                    return;
                } else if (parameterAsNum instanceof BigInteger) {
                    setBigDecimal(parameterIndex, new BigDecimal((BigInteger) parameterAsNum, scale));
                    return;
                } else {
                    setBigDecimal(parameterIndex, new BigDecimal(parameterAsNum.doubleValue()));
                    return;
                }
            case 6:
            case 8:
                setDouble(parameterIndex, parameterAsNum.doubleValue());
                return;
            case 7:
                setFloat(parameterIndex, parameterAsNum.floatValue());
                return;
            default:
                return;
        }
    }

    public void setObject(int parameterIndex, Object parameterObj) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (parameterObj == null) {
                setNull(parameterIndex, MysqlErrorNumbers.ER_INVALID_GROUP_FUNC_USE);
            } else if (parameterObj instanceof Byte) {
                setInt(parameterIndex, ((Byte) parameterObj).intValue());
            } else if (parameterObj instanceof String) {
                setString(parameterIndex, (String) parameterObj);
            } else if (parameterObj instanceof BigDecimal) {
                setBigDecimal(parameterIndex, (BigDecimal) parameterObj);
            } else if (parameterObj instanceof Short) {
                setShort(parameterIndex, ((Short) parameterObj).shortValue());
            } else if (parameterObj instanceof Integer) {
                setInt(parameterIndex, ((Integer) parameterObj).intValue());
            } else if (parameterObj instanceof Long) {
                setLong(parameterIndex, ((Long) parameterObj).longValue());
            } else if (parameterObj instanceof Float) {
                setFloat(parameterIndex, ((Float) parameterObj).floatValue());
            } else if (parameterObj instanceof Double) {
                setDouble(parameterIndex, ((Double) parameterObj).doubleValue());
            } else if (parameterObj instanceof byte[]) {
                setBytes(parameterIndex, (byte[]) parameterObj);
            } else if (parameterObj instanceof java.sql.Date) {
                setDate(parameterIndex, (java.sql.Date) parameterObj);
            } else if (parameterObj instanceof Time) {
                setTime(parameterIndex, (Time) parameterObj);
            } else if (parameterObj instanceof Timestamp) {
                setTimestamp(parameterIndex, (Timestamp) parameterObj);
            } else if (parameterObj instanceof Boolean) {
                setBoolean(parameterIndex, ((Boolean) parameterObj).booleanValue());
            } else if (parameterObj instanceof InputStream) {
                setBinaryStream(parameterIndex, (InputStream) parameterObj, -1);
            } else if (parameterObj instanceof Blob) {
                setBlob(parameterIndex, (Blob) parameterObj);
            } else if (parameterObj instanceof Clob) {
                setClob(parameterIndex, (Clob) parameterObj);
            } else if (this.connection.getTreatUtilDateAsTimestamp() && (parameterObj instanceof Date)) {
                setTimestamp(parameterIndex, new Timestamp(((Date) parameterObj).getTime()));
            } else if (parameterObj instanceof BigInteger) {
                setString(parameterIndex, parameterObj.toString());
            } else {
                setSerializableObject(parameterIndex, parameterObj);
            }
        }
    }

    public void setObject(int parameterIndex, Object parameterObj, int targetSqlType) throws SQLException {
        if (!(parameterObj instanceof BigDecimal)) {
            setObject(parameterIndex, parameterObj, targetSqlType, 0);
        } else {
            setObject(parameterIndex, parameterObj, targetSqlType, ((BigDecimal) parameterObj).scale());
        }
    }

    public void setObject(int parameterIndex, Object parameterObj, int targetSqlType, int scale) throws SQLException {
        Date parameterAsDate;
        synchronized (checkClosed().getConnectionMutex()) {
            if (parameterObj == null) {
                setNull(parameterIndex, MysqlErrorNumbers.ER_INVALID_GROUP_FUNC_USE);
            } else {
                boolean z = true;
                switch (targetSqlType) {
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
                        setNumericObject(parameterIndex, parameterObj, targetSqlType, scale);
                        break;
                    case -4:
                    case -3:
                    case -2:
                    case 2004:
                        if (!(parameterObj instanceof byte[])) {
                            if (!(parameterObj instanceof Blob)) {
                                setBytes(parameterIndex, StringUtils.getBytes(parameterObj.toString(), this.charConverter, this.charEncoding, this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), getExceptionInterceptor()));
                                break;
                            } else {
                                setBlob(parameterIndex, (Blob) parameterObj);
                                break;
                            }
                        } else {
                            setBytes(parameterIndex, (byte[]) parameterObj);
                            break;
                        }
                    case -1:
                    case 1:
                    case 12:
                        if (!(parameterObj instanceof BigDecimal)) {
                            setString(parameterIndex, parameterObj.toString());
                            break;
                        } else {
                            setString(parameterIndex, StringUtils.fixDecimalExponent(StringUtils.consistentToString((BigDecimal) parameterObj)));
                            break;
                        }
                    case 16:
                        if (parameterObj instanceof Boolean) {
                            setBoolean(parameterIndex, ((Boolean) parameterObj).booleanValue());
                            break;
                        } else if (parameterObj instanceof String) {
                            if (!"true".equalsIgnoreCase((String) parameterObj)) {
                                if (!"Y".equalsIgnoreCase((String) parameterObj)) {
                                    if (!"false".equalsIgnoreCase((String) parameterObj)) {
                                        if (!"N".equalsIgnoreCase((String) parameterObj)) {
                                            if (((String) parameterObj).matches("-?\\d+\\.?\\d*")) {
                                                if (((String) parameterObj).matches("-?[0]+[.]*[0]*")) {
                                                    z = false;
                                                }
                                                setBoolean(parameterIndex, z);
                                                break;
                                            } else {
                                                throw SQLError.createSQLException("No conversion from " + parameterObj + " to Types.BOOLEAN possible.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                                            }
                                        }
                                    }
                                    setBoolean(parameterIndex, false);
                                    break;
                                }
                            }
                            setBoolean(parameterIndex, true);
                            break;
                        } else if (parameterObj instanceof Number) {
                            if (((Number) parameterObj).intValue() == 0) {
                                z = false;
                            }
                            setBoolean(parameterIndex, z);
                            break;
                        } else {
                            throw SQLError.createSQLException("No conversion from " + parameterObj.getClass().getName() + " to Types.BOOLEAN possible.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
                        }
                    case 91:
                    case 93:
                        if (parameterObj instanceof String) {
                            parameterAsDate = TimeUtil.getSimpleDateFormat((SimpleDateFormat) null, getDateTimePattern((String) parameterObj, false), (Calendar) null, (TimeZone) null).parse((String) parameterObj, new ParsePosition(0));
                        } else {
                            parameterAsDate = (Date) parameterObj;
                        }
                        switch (targetSqlType) {
                            case 91:
                                if (!(parameterAsDate instanceof java.sql.Date)) {
                                    setDate(parameterIndex, new java.sql.Date(parameterAsDate.getTime()));
                                    break;
                                } else {
                                    setDate(parameterIndex, (java.sql.Date) parameterAsDate);
                                    break;
                                }
                            case 93:
                                if (!(parameterAsDate instanceof Timestamp)) {
                                    setTimestamp(parameterIndex, new Timestamp(parameterAsDate.getTime()));
                                    break;
                                } else {
                                    setTimestamp(parameterIndex, (Timestamp) parameterAsDate);
                                    break;
                                }
                        }
                        break;
                    case 92:
                        if (!(parameterObj instanceof String)) {
                            if (!(parameterObj instanceof Timestamp)) {
                                setTime(parameterIndex, (Time) parameterObj);
                                break;
                            } else {
                                setTime(parameterIndex, new Time(((Timestamp) parameterObj).getTime()));
                                break;
                            }
                        } else {
                            setTime(parameterIndex, new Time(TimeUtil.getSimpleDateFormat((SimpleDateFormat) null, getDateTimePattern((String) parameterObj, true), (Calendar) null, (TimeZone) null).parse((String) parameterObj).getTime()));
                            break;
                        }
                    case MysqlErrorNumbers.ER_INVALID_GROUP_FUNC_USE:
                        setSerializableObject(parameterIndex, parameterObj);
                        break;
                    case 2005:
                        try {
                            if (!(parameterObj instanceof Clob)) {
                                setString(parameterIndex, parameterObj.toString());
                                break;
                            } else {
                                setClob(parameterIndex, (Clob) parameterObj);
                                break;
                            }
                        } catch (Exception ex) {
                            if (ex instanceof SQLException) {
                                throw ((SQLException) ex);
                            }
                            SQLException sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.17") + parameterObj.getClass().toString() + Messages.getString("PreparedStatement.18") + ex.getClass().getName() + Messages.getString("PreparedStatement.19") + ex.getMessage(), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
                            sqlEx.initCause(ex);
                            throw sqlEx;
                        }
                    default:
                        throw SQLError.createSQLException(Messages.getString("PreparedStatement.16"), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public int setOneBatchedParameterSet(java.sql.PreparedStatement batchedStatement, int batchedParamIndex, Object paramSet) throws SQLException {
        BatchParams paramArg = (BatchParams) paramSet;
        boolean[] isNullBatch = paramArg.isNull;
        boolean[] isStreamBatch = paramArg.isStream;
        for (int j = 0; j < isNullBatch.length; j++) {
            if (isNullBatch[j]) {
                batchedStatement.setNull(batchedParamIndex, 0);
                batchedParamIndex++;
            } else if (isStreamBatch[j]) {
                batchedStatement.setBinaryStream(batchedParamIndex, paramArg.parameterStreams[j], paramArg.streamLengths[j]);
                batchedParamIndex++;
            } else {
                ((PreparedStatement) batchedStatement).setBytesNoEscapeNoQuotes(batchedParamIndex, paramArg.parameterStrings[j]);
                batchedParamIndex++;
            }
        }
        return batchedParamIndex;
    }

    public void setRef(int i, Ref x) throws SQLException {
        throw SQLError.createSQLFeatureNotSupportedException();
    }

    private final void setSerializableObject(int parameterIndex, Object parameterObj) throws SQLException {
        try {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            ObjectOutputStream objectOut = new ObjectOutputStream(bytesOut);
            objectOut.writeObject(parameterObj);
            objectOut.flush();
            objectOut.close();
            bytesOut.flush();
            bytesOut.close();
            byte[] buf = bytesOut.toByteArray();
            setBinaryStream(parameterIndex, (InputStream) new ByteArrayInputStream(buf), buf.length);
            this.parameterTypes[(parameterIndex - 1) + getParameterIndexOffset()] = -2;
        } catch (Exception ex) {
            SQLException sqlEx = SQLError.createSQLException(Messages.getString("PreparedStatement.54") + ex.getClass().getName(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, getExceptionInterceptor());
            sqlEx.initCause(ex);
            throw sqlEx;
        }
    }

    public void setShort(int parameterIndex, short x) throws SQLException {
        setInternal(parameterIndex, String.valueOf(x));
        this.parameterTypes[(parameterIndex - 1) + getParameterIndexOffset()] = 5;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x009b, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x019b, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setString(int r21, java.lang.String r22) throws java.sql.SQLException {
        /*
            r20 = this;
            r1 = r20
            r2 = r21
            r9 = r22
            com.mysql.jdbc.MySQLConnection r0 = r20.checkClosed()
            java.lang.Object r10 = r0.getConnectionMutex()
            monitor-enter(r10)
            r0 = 1
            if (r9 != 0) goto L_0x0017
            r1.setNull(r2, r0)     // Catch:{ all -> 0x019c }
            goto L_0x019a
        L_0x0017:
            r20.checkClosed()     // Catch:{ all -> 0x019c }
            int r3 = r22.length()     // Catch:{ all -> 0x019c }
            r11 = r3
            com.mysql.jdbc.MySQLConnection r3 = r1.connection     // Catch:{ all -> 0x019c }
            boolean r3 = r3.isNoBackslashEscapesSet()     // Catch:{ all -> 0x019c }
            r4 = 39
            if (r3 == 0) goto L_0x009c
            boolean r0 = r1.isEscapeNeededForString(r9, r11)     // Catch:{ all -> 0x019c }
            if (r0 != 0) goto L_0x0073
            r3 = 0
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x019c }
            int r6 = r22.length()     // Catch:{ all -> 0x019c }
            int r6 = r6 + 2
            r5.<init>(r6)     // Catch:{ all -> 0x019c }
            r5.append(r4)     // Catch:{ all -> 0x019c }
            r5.append(r9)     // Catch:{ all -> 0x019c }
            r5.append(r4)     // Catch:{ all -> 0x019c }
            boolean r4 = r1.isLoadDataQuery     // Catch:{ all -> 0x019c }
            if (r4 != 0) goto L_0x0066
            java.lang.String r12 = r5.toString()     // Catch:{ all -> 0x019c }
            com.mysql.jdbc.SingleByteCharsetConverter r13 = r1.charConverter     // Catch:{ all -> 0x019c }
            java.lang.String r14 = r1.charEncoding     // Catch:{ all -> 0x019c }
            com.mysql.jdbc.MySQLConnection r4 = r1.connection     // Catch:{ all -> 0x019c }
            java.lang.String r15 = r4.getServerCharset()     // Catch:{ all -> 0x019c }
            com.mysql.jdbc.MySQLConnection r4 = r1.connection     // Catch:{ all -> 0x019c }
            boolean r16 = r4.parserKnowsUnicode()     // Catch:{ all -> 0x019c }
            com.mysql.jdbc.ExceptionInterceptor r17 = r20.getExceptionInterceptor()     // Catch:{ all -> 0x019c }
            byte[] r4 = com.mysql.jdbc.StringUtils.getBytes((java.lang.String) r12, (com.mysql.jdbc.SingleByteCharsetConverter) r13, (java.lang.String) r14, (java.lang.String) r15, (boolean) r16, (com.mysql.jdbc.ExceptionInterceptor) r17)     // Catch:{ all -> 0x019c }
            r3 = r4
            goto L_0x006f
        L_0x0066:
            java.lang.String r4 = r5.toString()     // Catch:{ all -> 0x019c }
            byte[] r4 = com.mysql.jdbc.StringUtils.getBytes((java.lang.String) r4)     // Catch:{ all -> 0x019c }
            r3 = r4
        L_0x006f:
            r1.setInternal((int) r2, (byte[]) r3)     // Catch:{ all -> 0x019c }
            goto L_0x009a
        L_0x0073:
            r12 = 0
            boolean r3 = r1.isLoadDataQuery     // Catch:{ all -> 0x019c }
            if (r3 != 0) goto L_0x0093
            com.mysql.jdbc.SingleByteCharsetConverter r4 = r1.charConverter     // Catch:{ all -> 0x019c }
            java.lang.String r5 = r1.charEncoding     // Catch:{ all -> 0x019c }
            com.mysql.jdbc.MySQLConnection r3 = r1.connection     // Catch:{ all -> 0x019c }
            java.lang.String r6 = r3.getServerCharset()     // Catch:{ all -> 0x019c }
            com.mysql.jdbc.MySQLConnection r3 = r1.connection     // Catch:{ all -> 0x019c }
            boolean r7 = r3.parserKnowsUnicode()     // Catch:{ all -> 0x019c }
            com.mysql.jdbc.ExceptionInterceptor r8 = r20.getExceptionInterceptor()     // Catch:{ all -> 0x019c }
            r3 = r22
            byte[] r3 = com.mysql.jdbc.StringUtils.getBytes((java.lang.String) r3, (com.mysql.jdbc.SingleByteCharsetConverter) r4, (java.lang.String) r5, (java.lang.String) r6, (boolean) r7, (com.mysql.jdbc.ExceptionInterceptor) r8)     // Catch:{ all -> 0x019c }
            goto L_0x0097
        L_0x0093:
            byte[] r3 = com.mysql.jdbc.StringUtils.getBytes((java.lang.String) r22)     // Catch:{ all -> 0x019c }
        L_0x0097:
            r1.setBytes(r2, r3)     // Catch:{ all -> 0x019c }
        L_0x009a:
            monitor-exit(r10)     // Catch:{ all -> 0x019c }
            return
        L_0x009c:
            r3 = r22
            r5 = 1
            boolean r6 = r1.isLoadDataQuery     // Catch:{ all -> 0x019c }
            if (r6 != 0) goto L_0x00a9
            boolean r6 = r1.isEscapeNeededForString(r9, r11)     // Catch:{ all -> 0x019c }
            if (r6 == 0) goto L_0x0142
        L_0x00a9:
            r5 = 0
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x019c }
            int r7 = r22.length()     // Catch:{ all -> 0x019c }
            double r7 = (double) r7     // Catch:{ all -> 0x019c }
            r12 = 4607632778762754458(0x3ff199999999999a, double:1.1)
            double r7 = r7 * r12
            int r7 = (int) r7     // Catch:{ all -> 0x019c }
            r6.<init>(r7)     // Catch:{ all -> 0x019c }
            r6.append(r4)     // Catch:{ all -> 0x019c }
            r7 = 0
        L_0x00bf:
            if (r7 >= r11) goto L_0x0138
            char r8 = r9.charAt(r7)     // Catch:{ all -> 0x019c }
            r12 = 92
            switch(r8) {
                case 0: goto L_0x012b;
                case 10: goto L_0x0122;
                case 13: goto L_0x0119;
                case 26: goto L_0x0110;
                case 34: goto L_0x0103;
                case 39: goto L_0x00fa;
                case 92: goto L_0x00f3;
                case 165: goto L_0x00ce;
                case 8361: goto L_0x00ce;
                default: goto L_0x00ca;
            }     // Catch:{ all -> 0x019c }
        L_0x00ca:
            r6.append(r8)     // Catch:{ all -> 0x019c }
            goto L_0x0133
        L_0x00ce:
            java.nio.charset.CharsetEncoder r13 = r1.charsetEncoder     // Catch:{ all -> 0x019c }
            if (r13 == 0) goto L_0x00ef
            java.nio.CharBuffer r13 = java.nio.CharBuffer.allocate(r0)     // Catch:{ all -> 0x019c }
            java.nio.ByteBuffer r14 = java.nio.ByteBuffer.allocate(r0)     // Catch:{ all -> 0x019c }
            r13.put(r8)     // Catch:{ all -> 0x019c }
            r15 = 0
            r13.position(r15)     // Catch:{ all -> 0x019c }
            java.nio.charset.CharsetEncoder r4 = r1.charsetEncoder     // Catch:{ all -> 0x019c }
            r4.encode(r13, r14, r0)     // Catch:{ all -> 0x019c }
            byte r4 = r14.get(r15)     // Catch:{ all -> 0x019c }
            if (r4 != r12) goto L_0x00ef
            r6.append(r12)     // Catch:{ all -> 0x019c }
        L_0x00ef:
            r6.append(r8)     // Catch:{ all -> 0x019c }
            goto L_0x0133
        L_0x00f3:
            r6.append(r12)     // Catch:{ all -> 0x019c }
            r6.append(r12)     // Catch:{ all -> 0x019c }
            goto L_0x0133
        L_0x00fa:
            r6.append(r12)     // Catch:{ all -> 0x019c }
            r4 = 39
            r6.append(r4)     // Catch:{ all -> 0x019c }
            goto L_0x0133
        L_0x0103:
            boolean r4 = r1.usingAnsiMode     // Catch:{ all -> 0x019c }
            if (r4 == 0) goto L_0x010a
            r6.append(r12)     // Catch:{ all -> 0x019c }
        L_0x010a:
            r4 = 34
            r6.append(r4)     // Catch:{ all -> 0x019c }
            goto L_0x0133
        L_0x0110:
            r6.append(r12)     // Catch:{ all -> 0x019c }
            r4 = 90
            r6.append(r4)     // Catch:{ all -> 0x019c }
            goto L_0x0133
        L_0x0119:
            r6.append(r12)     // Catch:{ all -> 0x019c }
            r4 = 114(0x72, float:1.6E-43)
            r6.append(r4)     // Catch:{ all -> 0x019c }
            goto L_0x0133
        L_0x0122:
            r6.append(r12)     // Catch:{ all -> 0x019c }
            r4 = 110(0x6e, float:1.54E-43)
            r6.append(r4)     // Catch:{ all -> 0x019c }
            goto L_0x0133
        L_0x012b:
            r6.append(r12)     // Catch:{ all -> 0x019c }
            r4 = 48
            r6.append(r4)     // Catch:{ all -> 0x019c }
        L_0x0133:
            int r7 = r7 + 1
            r4 = 39
            goto L_0x00bf
        L_0x0138:
            r0 = 39
            r6.append(r0)     // Catch:{ all -> 0x019c }
            java.lang.String r0 = r6.toString()     // Catch:{ all -> 0x019c }
            r3 = r0
        L_0x0142:
            r0 = 0
            boolean r4 = r1.isLoadDataQuery     // Catch:{ all -> 0x019c }
            if (r4 != 0) goto L_0x0185
            if (r5 == 0) goto L_0x016a
            r13 = 39
            r14 = 39
            com.mysql.jdbc.SingleByteCharsetConverter r15 = r1.charConverter     // Catch:{ all -> 0x019c }
            java.lang.String r4 = r1.charEncoding     // Catch:{ all -> 0x019c }
            com.mysql.jdbc.MySQLConnection r6 = r1.connection     // Catch:{ all -> 0x019c }
            java.lang.String r17 = r6.getServerCharset()     // Catch:{ all -> 0x019c }
            com.mysql.jdbc.MySQLConnection r6 = r1.connection     // Catch:{ all -> 0x019c }
            boolean r18 = r6.parserKnowsUnicode()     // Catch:{ all -> 0x019c }
            com.mysql.jdbc.ExceptionInterceptor r19 = r20.getExceptionInterceptor()     // Catch:{ all -> 0x019c }
            r12 = r3
            r16 = r4
            byte[] r4 = com.mysql.jdbc.StringUtils.getBytesWrapped(r12, r13, r14, r15, r16, r17, r18, r19)     // Catch:{ all -> 0x019c }
            r0 = r4
            goto L_0x018a
        L_0x016a:
            com.mysql.jdbc.SingleByteCharsetConverter r13 = r1.charConverter     // Catch:{ all -> 0x019c }
            java.lang.String r14 = r1.charEncoding     // Catch:{ all -> 0x019c }
            com.mysql.jdbc.MySQLConnection r4 = r1.connection     // Catch:{ all -> 0x019c }
            java.lang.String r15 = r4.getServerCharset()     // Catch:{ all -> 0x019c }
            com.mysql.jdbc.MySQLConnection r4 = r1.connection     // Catch:{ all -> 0x019c }
            boolean r16 = r4.parserKnowsUnicode()     // Catch:{ all -> 0x019c }
            com.mysql.jdbc.ExceptionInterceptor r17 = r20.getExceptionInterceptor()     // Catch:{ all -> 0x019c }
            r12 = r3
            byte[] r4 = com.mysql.jdbc.StringUtils.getBytes((java.lang.String) r12, (com.mysql.jdbc.SingleByteCharsetConverter) r13, (java.lang.String) r14, (java.lang.String) r15, (boolean) r16, (com.mysql.jdbc.ExceptionInterceptor) r17)     // Catch:{ all -> 0x019c }
            r0 = r4
            goto L_0x018a
        L_0x0185:
            byte[] r4 = com.mysql.jdbc.StringUtils.getBytes((java.lang.String) r3)     // Catch:{ all -> 0x019c }
            r0 = r4
        L_0x018a:
            r1.setInternal((int) r2, (byte[]) r0)     // Catch:{ all -> 0x019c }
            int[] r4 = r1.parameterTypes     // Catch:{ all -> 0x019c }
            int r6 = r2 + -1
            int r7 = r20.getParameterIndexOffset()     // Catch:{ all -> 0x019c }
            int r6 = r6 + r7
            r7 = 12
            r4[r6] = r7     // Catch:{ all -> 0x019c }
        L_0x019a:
            monitor-exit(r10)     // Catch:{ all -> 0x019c }
            return
        L_0x019c:
            r0 = move-exception
            monitor-exit(r10)     // Catch:{ all -> 0x019c }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.PreparedStatement.setString(int, java.lang.String):void");
    }

    private boolean isEscapeNeededForString(String x, int stringLength) {
        boolean needsHexEscape = false;
        for (int i = 0; i < stringLength; i++) {
            switch (x.charAt(i)) {
                case 0:
                    needsHexEscape = true;
                    break;
                case 10:
                    needsHexEscape = true;
                    break;
                case 13:
                    needsHexEscape = true;
                    break;
                case 26:
                    needsHexEscape = true;
                    break;
                case '\"':
                    needsHexEscape = true;
                    break;
                case '\'':
                    needsHexEscape = true;
                    break;
                case '\\':
                    needsHexEscape = true;
                    break;
            }
            if (needsHexEscape) {
                return needsHexEscape;
            }
        }
        return needsHexEscape;
    }

    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            setTimeInternal(parameterIndex, x, cal, cal.getTimeZone(), true);
        }
    }

    public void setTime(int parameterIndex, Time x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            setTimeInternal(parameterIndex, x, (Calendar) null, this.connection.getDefaultTimeZone(), false);
        }
    }

    private void setTimeInternal(int parameterIndex, Time x, Calendar targetCalendar, TimeZone tz, boolean rollForward) throws SQLException {
        if (x == null) {
            setNull(parameterIndex, 92);
            return;
        }
        checkClosed();
        if (!this.useLegacyDatetimeCode) {
            newSetTimeInternal(parameterIndex, x, targetCalendar);
        } else {
            Calendar sessionCalendar = getCalendarInstanceForSessionOrNew();
            setInternal(parameterIndex, "'" + TimeUtil.changeTimezone(this.connection, sessionCalendar, targetCalendar, x, tz, this.connection.getServerTimezoneTZ(), rollForward).toString() + "'");
        }
        this.parameterTypes[(parameterIndex - 1) + getParameterIndexOffset()] = 92;
    }

    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            int fractLen = -1;
            if (this.sendFractionalSeconds) {
                if (this.serverSupportsFracSecs) {
                    MysqlParameterMetadata mysqlParameterMetadata = this.parameterMetaData;
                    if (mysqlParameterMetadata != null && mysqlParameterMetadata.metadata != null && this.parameterMetaData.metadata.fields != null && parameterIndex <= this.parameterMetaData.metadata.fields.length && parameterIndex >= 0 && this.parameterMetaData.metadata.getField(parameterIndex).getDecimals() > 0) {
                        fractLen = this.parameterMetaData.metadata.getField(parameterIndex).getDecimals();
                    }
                    setTimestampInternal(parameterIndex, x, cal, cal.getTimeZone(), true, fractLen, this.connection.getUseSSPSCompatibleTimezoneShift());
                }
            }
            fractLen = 0;
            setTimestampInternal(parameterIndex, x, cal, cal.getTimeZone(), true, fractLen, this.connection.getUseSSPSCompatibleTimezoneShift());
        }
    }

    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            int fractLen = -1;
            if (this.sendFractionalSeconds) {
                if (this.serverSupportsFracSecs) {
                    MysqlParameterMetadata mysqlParameterMetadata = this.parameterMetaData;
                    if (!(mysqlParameterMetadata == null || mysqlParameterMetadata.metadata == null || this.parameterMetaData.metadata.fields == null || parameterIndex > this.parameterMetaData.metadata.fields.length || parameterIndex < 0)) {
                        fractLen = this.parameterMetaData.metadata.getField(parameterIndex).getDecimals();
                    }
                    setTimestampInternal(parameterIndex, x, (Calendar) null, this.connection.getDefaultTimeZone(), false, fractLen, this.connection.getUseSSPSCompatibleTimezoneShift());
                }
            }
            fractLen = 0;
            setTimestampInternal(parameterIndex, x, (Calendar) null, this.connection.getDefaultTimeZone(), false, fractLen, this.connection.getUseSSPSCompatibleTimezoneShift());
        }
    }

    /* access modifiers changed from: protected */
    public void setTimestampInternal(int parameterIndex, Timestamp x, Calendar targetCalendar, TimeZone tz, boolean rollForward, int fractionalLength, boolean useSSPSCompatibleTimezoneShift) throws SQLException {
        int fractionalLength2;
        int nanos;
        int i = parameterIndex;
        Calendar calendar = targetCalendar;
        if (x == null) {
            setNull(parameterIndex, 93);
            Timestamp timestamp = x;
            int i2 = fractionalLength;
            return;
        }
        checkClosed();
        Timestamp x2 = (Timestamp) x.clone();
        if (!this.serverSupportsFracSecs || (!this.sendFractionalSeconds && fractionalLength == 0)) {
            x2 = TimeUtil.truncateFractionalSeconds(x2);
        }
        if (fractionalLength < 0) {
            fractionalLength2 = 6;
        } else {
            fractionalLength2 = fractionalLength;
        }
        Timestamp x3 = TimeUtil.adjustTimestampNanosPrecision(x2, fractionalLength2, !this.connection.isServerTruncatesFracSecs());
        if (!this.useLegacyDatetimeCode) {
            newSetTimestampInternal(parameterIndex, x3, calendar);
        } else {
            Timestamp x4 = TimeUtil.changeTimezone(this.connection, TimeUtil.setProlepticIfNeeded(this.connection.getUseJDBCCompliantTimezoneShift() ? this.connection.getUtcCalendar() : getCalendarInstanceForSessionOrNew(), calendar), targetCalendar, x3, tz, this.connection.getServerTimezoneTZ(), rollForward);
            if (useSSPSCompatibleTimezoneShift) {
                doSSPSCompatibleTimezoneShift(parameterIndex, x4, fractionalLength2, calendar);
            } else {
                synchronized (this) {
                    SimpleDateFormat simpleDateFormat = TimeUtil.getSimpleDateFormat(this.tsdf, "''yyyy-MM-dd HH:mm:ss", (Calendar) null, (TimeZone) null);
                    this.tsdf = simpleDateFormat;
                    Calendar adjCal = TimeUtil.setProlepticIfNeeded(simpleDateFormat.getCalendar(), calendar);
                    if (this.tsdf.getCalendar() != adjCal) {
                        this.tsdf.setCalendar(adjCal);
                    }
                    StringBuffer buf = new StringBuffer();
                    buf.append(this.tsdf.format(x4));
                    if (fractionalLength2 > 0 && (nanos = x4.getNanos()) != 0) {
                        buf.append('.');
                        buf.append(TimeUtil.formatNanos(nanos, this.serverSupportsFracSecs, fractionalLength2));
                    }
                    buf.append('\'');
                    setInternal(parameterIndex, buf.toString());
                }
            }
            Timestamp timestamp2 = x4;
        }
        this.parameterTypes[(i - 1) + getParameterIndexOffset()] = 93;
    }

    private void newSetTimestampInternal(int parameterIndex, Timestamp x, Calendar targetCalendar) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            this.tsdf = TimeUtil.getSimpleDateFormat(this.tsdf, "''yyyy-MM-dd HH:mm:ss", targetCalendar, targetCalendar != null ? null : this.connection.getServerTimezoneTZ());
            StringBuffer buf = new StringBuffer();
            buf.append(this.tsdf.format(x));
            buf.append('.');
            buf.append(TimeUtil.formatNanos(x.getNanos(), this.serverSupportsFracSecs, 6));
            buf.append('\'');
            setInternal(parameterIndex, buf.toString());
        }
    }

    private void newSetTimeInternal(int parameterIndex, Time x, Calendar targetCalendar) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            SimpleDateFormat simpleDateFormat = TimeUtil.getSimpleDateFormat(this.tdf, "''HH:mm:ss''", targetCalendar, targetCalendar != null ? null : this.connection.getServerTimezoneTZ());
            this.tdf = simpleDateFormat;
            setInternal(parameterIndex, simpleDateFormat.format(x));
        }
    }

    private void newSetDateInternal(int parameterIndex, java.sql.Date x, Calendar targetCalendar) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            SimpleDateFormat simpleDateFormat = TimeUtil.getSimpleDateFormat(this.ddf, "''yyyy-MM-dd''", targetCalendar, targetCalendar != null ? null : this.connection.getNoTimezoneConversionForDateType() ? this.connection.getDefaultTimeZone() : this.connection.getServerTimezoneTZ());
            this.ddf = simpleDateFormat;
            setInternal(parameterIndex, simpleDateFormat.format(x));
        }
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
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:598)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    private void doSSPSCompatibleTimezoneShift(int r17, java.sql.Timestamp r18, int r19, java.util.Calendar r20) throws java.sql.SQLException {
        /*
            r16 = this;
            r1 = r16
            com.mysql.jdbc.MySQLConnection r0 = r16.checkClosed()
            java.lang.Object r2 = r0.getConnectionMutex()
            monitor-enter(r2)
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ all -> 0x00ee }
            boolean r0 = r0.getUseJDBCCompliantTimezoneShift()     // Catch:{ all -> 0x00ee }
            if (r0 == 0) goto L_0x001a
            com.mysql.jdbc.MySQLConnection r0 = r1.connection     // Catch:{ all -> 0x00ee }
            java.util.Calendar r0 = r0.getUtcCalendar()     // Catch:{ all -> 0x00ee }
            goto L_0x001e
        L_0x001a:
            java.util.Calendar r0 = r16.getCalendarInstanceForSessionOrNew()     // Catch:{ all -> 0x00ee }
        L_0x001e:
            r3 = r20
            java.util.Calendar r4 = com.mysql.jdbc.TimeUtil.setProlepticIfNeeded(r0, r3)     // Catch:{ all -> 0x00e6 }
            monitor-enter(r4)     // Catch:{ all -> 0x00e6 }
            java.util.Date r0 = r4.getTime()     // Catch:{ all -> 0x00db }
            r5 = r0
            r6 = r18
            r4.setTime(r6)     // Catch:{ all -> 0x00d1 }
            r0 = 1
            int r7 = r4.get(r0)     // Catch:{ all -> 0x00d1 }
            r8 = 2
            int r8 = r4.get(r8)     // Catch:{ all -> 0x00d1 }
            int r8 = r8 + r0
            r0 = 5
            int r0 = r4.get(r0)     // Catch:{ all -> 0x00d1 }
            r9 = 11
            int r9 = r4.get(r9)     // Catch:{ all -> 0x00d1 }
            r10 = 12
            int r10 = r4.get(r10)     // Catch:{ all -> 0x00d1 }
            r11 = 13
            int r11 = r4.get(r11)     // Catch:{ all -> 0x00d1 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d1 }
            r12.<init>()     // Catch:{ all -> 0x00d1 }
            r13 = 39
            r12.append(r13)     // Catch:{ all -> 0x00d1 }
            r12.append(r7)     // Catch:{ all -> 0x00d1 }
            java.lang.String r14 = "-"
            r12.append(r14)     // Catch:{ all -> 0x00d1 }
            r14 = 48
            r15 = 10
            if (r8 >= r15) goto L_0x006c
            r12.append(r14)     // Catch:{ all -> 0x00d1 }
        L_0x006c:
            r12.append(r8)     // Catch:{ all -> 0x00d1 }
            r13 = 45
            r12.append(r13)     // Catch:{ all -> 0x00d1 }
            if (r0 >= r15) goto L_0x0079
            r12.append(r14)     // Catch:{ all -> 0x00d1 }
        L_0x0079:
            r12.append(r0)     // Catch:{ all -> 0x00d1 }
            r13 = 32
            r12.append(r13)     // Catch:{ all -> 0x00d1 }
            if (r9 >= r15) goto L_0x0086
            r12.append(r14)     // Catch:{ all -> 0x00d1 }
        L_0x0086:
            r12.append(r9)     // Catch:{ all -> 0x00d1 }
            r13 = 58
            r12.append(r13)     // Catch:{ all -> 0x00d1 }
            if (r10 >= r15) goto L_0x0093
            r12.append(r14)     // Catch:{ all -> 0x00d1 }
        L_0x0093:
            r12.append(r10)     // Catch:{ all -> 0x00d1 }
            r12.append(r13)     // Catch:{ all -> 0x00d1 }
            if (r11 >= r15) goto L_0x009e
            r12.append(r14)     // Catch:{ all -> 0x00d1 }
        L_0x009e:
            r12.append(r11)     // Catch:{ all -> 0x00d1 }
            r13 = 46
            r12.append(r13)     // Catch:{ all -> 0x00d1 }
            int r13 = r18.getNanos()     // Catch:{ all -> 0x00d1 }
            boolean r14 = r1.serverSupportsFracSecs     // Catch:{ all -> 0x00d1 }
            r15 = r19
            java.lang.String r13 = com.mysql.jdbc.TimeUtil.formatNanos(r13, r14, r15)     // Catch:{ all -> 0x00cd }
            r12.append(r13)     // Catch:{ all -> 0x00cd }
            r13 = 39
            r12.append(r13)     // Catch:{ all -> 0x00cd }
            java.lang.String r13 = r12.toString()     // Catch:{ all -> 0x00cd }
            r14 = r17
            r1.setInternal((int) r14, (java.lang.String) r13)     // Catch:{ all -> 0x00cb }
            r4.setTime(r5)     // Catch:{ all -> 0x00e4 }
            monitor-exit(r4)     // Catch:{ all -> 0x00e4 }
            monitor-exit(r2)     // Catch:{ all -> 0x00f9 }
            return
        L_0x00cb:
            r0 = move-exception
            goto L_0x00d6
        L_0x00cd:
            r0 = move-exception
            r14 = r17
            goto L_0x00d6
        L_0x00d1:
            r0 = move-exception
            r14 = r17
            r15 = r19
        L_0x00d6:
            r4.setTime(r5)     // Catch:{ all -> 0x00e4 }
            throw r0     // Catch:{ all -> 0x00e4 }
        L_0x00db:
            r0 = move-exception
            r14 = r17
            r6 = r18
            r15 = r19
        L_0x00e2:
            monitor-exit(r4)     // Catch:{ all -> 0x00e4 }
            throw r0     // Catch:{ all -> 0x00f9 }
        L_0x00e4:
            r0 = move-exception
            goto L_0x00e2
        L_0x00e6:
            r0 = move-exception
            r14 = r17
            r6 = r18
            r15 = r19
            goto L_0x00f7
        L_0x00ee:
            r0 = move-exception
            r14 = r17
            r6 = r18
            r15 = r19
            r3 = r20
        L_0x00f7:
            monitor-exit(r2)     // Catch:{ all -> 0x00f9 }
            throw r0
        L_0x00f9:
            r0 = move-exception
            goto L_0x00f7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.PreparedStatement.doSSPSCompatibleTimezoneShift(int, java.sql.Timestamp, int, java.util.Calendar):void");
    }

    @Deprecated
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        if (x == null) {
            setNull(parameterIndex, 12);
            return;
        }
        setBinaryStream(parameterIndex, x, length);
        this.parameterTypes[(parameterIndex - 1) + getParameterIndexOffset()] = 2005;
    }

    public void setURL(int parameterIndex, URL arg) throws SQLException {
        if (arg != null) {
            setString(parameterIndex, arg.toString());
            this.parameterTypes[(parameterIndex - 1) + getParameterIndexOffset()] = 70;
            return;
        }
        setNull(parameterIndex, 1);
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:49:0x00aa=Splitter:B:49:0x00aa, B:56:0x00ba=Splitter:B:56:0x00ba, B:71:0x00dd=Splitter:B:71:0x00dd} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void streamToBytes(com.mysql.jdbc.Buffer r10, java.io.InputStream r11, boolean r12, int r13, boolean r14) throws java.sql.SQLException {
        /*
            r9 = this;
            com.mysql.jdbc.MySQLConnection r0 = r9.checkClosed()
            java.lang.Object r0 = r0.getConnectionMutex()
            monitor-enter(r0)
            byte[] r1 = r9.streamConvertBuf     // Catch:{ all -> 0x00cd }
            if (r1 != 0) goto L_0x0013
            r1 = 4096(0x1000, float:5.74E-42)
            byte[] r1 = new byte[r1]     // Catch:{ all -> 0x00cd }
            r9.streamConvertBuf = r1     // Catch:{ all -> 0x00cd }
        L_0x0013:
            com.mysql.jdbc.MySQLConnection r1 = r9.connection     // Catch:{ all -> 0x00cd }
            java.lang.String r1 = r1.getEncoding()     // Catch:{ all -> 0x00cd }
            r2 = 0
            com.mysql.jdbc.MySQLConnection r3 = r9.connection     // Catch:{ RuntimeException -> 0x00bc }
            boolean r3 = r3.isNoBackslashEscapesSet()     // Catch:{ RuntimeException -> 0x00bc }
            if (r3 != 0) goto L_0x003a
            com.mysql.jdbc.MySQLConnection r3 = r9.connection     // Catch:{ RuntimeException -> 0x00bc }
            boolean r3 = r3.getUseUnicode()     // Catch:{ RuntimeException -> 0x00bc }
            if (r3 == 0) goto L_0x003b
            if (r1 == 0) goto L_0x003b
            boolean r3 = com.mysql.jdbc.CharsetMapping.isMultibyteCharset(r1)     // Catch:{ RuntimeException -> 0x00bc }
            if (r3 == 0) goto L_0x003b
            com.mysql.jdbc.MySQLConnection r3 = r9.connection     // Catch:{ RuntimeException -> 0x00bc }
            boolean r3 = r3.parserKnowsUnicode()     // Catch:{ RuntimeException -> 0x00bc }
            if (r3 != 0) goto L_0x003b
        L_0x003a:
            r2 = 1
        L_0x003b:
            r3 = -1
            if (r13 != r3) goto L_0x0040
            r14 = 0
        L_0x0040:
            r3 = -1
            if (r14 == 0) goto L_0x004b
            byte[] r4 = r9.streamConvertBuf     // Catch:{ all -> 0x00cd }
            int r4 = r9.readblock(r11, r4, r13)     // Catch:{ all -> 0x00cd }
            r3 = r4
            goto L_0x0052
        L_0x004b:
            byte[] r4 = r9.streamConvertBuf     // Catch:{ all -> 0x00cd }
            int r4 = r9.readblock(r11, r4)     // Catch:{ all -> 0x00cd }
            r3 = r4
        L_0x0052:
            int r4 = r13 - r3
            r5 = 0
            if (r2 == 0) goto L_0x005e
            java.lang.String r6 = "x"
            r10.writeStringNoNull(r6)     // Catch:{ all -> 0x00cd }
            goto L_0x0071
        L_0x005e:
            com.mysql.jdbc.MySQLConnection r6 = r9.connection     // Catch:{ all -> 0x00cd }
            com.mysql.jdbc.MysqlIO r6 = r6.getIO()     // Catch:{ all -> 0x00cd }
            r7 = 4
            r8 = 1
            boolean r6 = r6.versionMeetsMinimum(r7, r8, r5)     // Catch:{ all -> 0x00cd }
            if (r6 == 0) goto L_0x0071
            java.lang.String r6 = "_binary"
            r10.writeStringNoNull(r6)     // Catch:{ all -> 0x00cd }
        L_0x0071:
            r6 = 39
            if (r12 == 0) goto L_0x0078
            r10.writeByte(r6)     // Catch:{ all -> 0x00cd }
        L_0x0078:
            if (r3 <= 0) goto L_0x00a4
            if (r2 == 0) goto L_0x0082
            byte[] r7 = r9.streamConvertBuf     // Catch:{ all -> 0x00cd }
            r9.hexEscapeBlock(r7, r10, r3)     // Catch:{ all -> 0x00cd }
            goto L_0x008f
        L_0x0082:
            if (r12 == 0) goto L_0x008a
            byte[] r7 = r9.streamConvertBuf     // Catch:{ all -> 0x00cd }
            r9.escapeblockFast((byte[]) r7, (com.mysql.jdbc.Buffer) r10, (int) r3)     // Catch:{ all -> 0x00cd }
            goto L_0x008f
        L_0x008a:
            byte[] r7 = r9.streamConvertBuf     // Catch:{ all -> 0x00cd }
            r10.writeBytesNoNull(r7, r5, r3)     // Catch:{ all -> 0x00cd }
        L_0x008f:
            if (r14 == 0) goto L_0x009c
            byte[] r7 = r9.streamConvertBuf     // Catch:{ all -> 0x00cd }
            int r7 = r9.readblock(r11, r7, r4)     // Catch:{ all -> 0x00cd }
            r3 = r7
            if (r3 <= 0) goto L_0x0078
            int r4 = r4 - r3
            goto L_0x0078
        L_0x009c:
            byte[] r7 = r9.streamConvertBuf     // Catch:{ all -> 0x00cd }
            int r7 = r9.readblock(r11, r7)     // Catch:{ all -> 0x00cd }
            r3 = r7
            goto L_0x0078
        L_0x00a4:
            if (r12 == 0) goto L_0x00a9
            r10.writeByte(r6)     // Catch:{ all -> 0x00cd }
        L_0x00a9:
            com.mysql.jdbc.MySQLConnection r1 = r9.connection     // Catch:{ all -> 0x00de }
            boolean r1 = r1.getAutoClosePStmtStreams()     // Catch:{ all -> 0x00de }
            if (r1 == 0) goto L_0x00b8
            r11.close()     // Catch:{ IOException -> 0x00b6 }
            goto L_0x00b7
        L_0x00b6:
            r1 = move-exception
        L_0x00b7:
            r11 = 0
        L_0x00b8:
            monitor-exit(r0)     // Catch:{ all -> 0x00de }
            return
        L_0x00bc:
            r3 = move-exception
            java.lang.String r4 = r3.toString()     // Catch:{ all -> 0x00cd }
            java.lang.String r5 = "S1009"
            r6 = 0
            java.sql.SQLException r4 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r4, (java.lang.String) r5, (com.mysql.jdbc.ExceptionInterceptor) r6)     // Catch:{ all -> 0x00cd }
            r4.initCause(r3)     // Catch:{ all -> 0x00cd }
            throw r4     // Catch:{ all -> 0x00cd }
        L_0x00cd:
            r1 = move-exception
            com.mysql.jdbc.MySQLConnection r2 = r9.connection     // Catch:{ all -> 0x00de }
            boolean r2 = r2.getAutoClosePStmtStreams()     // Catch:{ all -> 0x00de }
            if (r2 == 0) goto L_0x00dc
            r11.close()     // Catch:{ IOException -> 0x00da }
            goto L_0x00db
        L_0x00da:
            r2 = move-exception
        L_0x00db:
            r11 = 0
        L_0x00dc:
            throw r1     // Catch:{ all -> 0x00de }
        L_0x00de:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00de }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.PreparedStatement.streamToBytes(com.mysql.jdbc.Buffer, java.io.InputStream, boolean, int, boolean):void");
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:51:0x00b6=Splitter:B:51:0x00b6, B:58:0x00c6=Splitter:B:58:0x00c6, B:44:0x00ad=Splitter:B:44:0x00ad, B:37:0x009e=Splitter:B:37:0x009e} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final byte[] streamToBytes(java.io.InputStream r10, boolean r11, int r12, boolean r13) throws java.sql.SQLException {
        /*
            r9 = this;
            com.mysql.jdbc.MySQLConnection r0 = r9.checkClosed()
            java.lang.Object r0 = r0.getConnectionMutex()
            monitor-enter(r0)
            r1 = 2147483647(0x7fffffff, float:NaN)
            r10.mark(r1)     // Catch:{ all -> 0x00c7 }
            byte[] r1 = r9.streamConvertBuf     // Catch:{ all -> 0x00af }
            if (r1 != 0) goto L_0x0019
            r1 = 4096(0x1000, float:5.74E-42)
            byte[] r1 = new byte[r1]     // Catch:{ all -> 0x00af }
            r9.streamConvertBuf = r1     // Catch:{ all -> 0x00af }
        L_0x0019:
            r1 = -1
            if (r12 != r1) goto L_0x001d
            r13 = 0
        L_0x001d:
            java.io.ByteArrayOutputStream r1 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x00af }
            r1.<init>()     // Catch:{ all -> 0x00af }
            r2 = -1
            if (r13 == 0) goto L_0x002d
            byte[] r3 = r9.streamConvertBuf     // Catch:{ all -> 0x00af }
            int r3 = r9.readblock(r10, r3, r12)     // Catch:{ all -> 0x00af }
            r2 = r3
            goto L_0x0034
        L_0x002d:
            byte[] r3 = r9.streamConvertBuf     // Catch:{ all -> 0x00af }
            int r3 = r9.readblock(r10, r3)     // Catch:{ all -> 0x00af }
            r2 = r3
        L_0x0034:
            int r3 = r12 - r2
            r4 = 39
            r5 = 0
            if (r11 == 0) goto L_0x006b
            com.mysql.jdbc.MySQLConnection r6 = r9.connection     // Catch:{ all -> 0x00af }
            r7 = 4
            r8 = 1
            boolean r6 = r6.versionMeetsMinimum(r7, r8, r5)     // Catch:{ all -> 0x00af }
            if (r6 == 0) goto L_0x0068
            r6 = 95
            r1.write(r6)     // Catch:{ all -> 0x00af }
            r6 = 98
            r1.write(r6)     // Catch:{ all -> 0x00af }
            r6 = 105(0x69, float:1.47E-43)
            r1.write(r6)     // Catch:{ all -> 0x00af }
            r6 = 110(0x6e, float:1.54E-43)
            r1.write(r6)     // Catch:{ all -> 0x00af }
            r6 = 97
            r1.write(r6)     // Catch:{ all -> 0x00af }
            r6 = 114(0x72, float:1.6E-43)
            r1.write(r6)     // Catch:{ all -> 0x00af }
            r6 = 121(0x79, float:1.7E-43)
            r1.write(r6)     // Catch:{ all -> 0x00af }
        L_0x0068:
            r1.write(r4)     // Catch:{ all -> 0x00af }
        L_0x006b:
            if (r2 <= 0) goto L_0x008f
            if (r11 == 0) goto L_0x0075
            byte[] r6 = r9.streamConvertBuf     // Catch:{ all -> 0x00af }
            r9.escapeblockFast((byte[]) r6, (java.io.ByteArrayOutputStream) r1, (int) r2)     // Catch:{ all -> 0x00af }
            goto L_0x007a
        L_0x0075:
            byte[] r6 = r9.streamConvertBuf     // Catch:{ all -> 0x00af }
            r1.write(r6, r5, r2)     // Catch:{ all -> 0x00af }
        L_0x007a:
            if (r13 == 0) goto L_0x0087
            byte[] r6 = r9.streamConvertBuf     // Catch:{ all -> 0x00af }
            int r6 = r9.readblock(r10, r6, r3)     // Catch:{ all -> 0x00af }
            r2 = r6
            if (r2 <= 0) goto L_0x006b
            int r3 = r3 - r2
            goto L_0x006b
        L_0x0087:
            byte[] r6 = r9.streamConvertBuf     // Catch:{ all -> 0x00af }
            int r6 = r9.readblock(r10, r6)     // Catch:{ all -> 0x00af }
            r2 = r6
            goto L_0x006b
        L_0x008f:
            if (r11 == 0) goto L_0x0094
            r1.write(r4)     // Catch:{ all -> 0x00af }
        L_0x0094:
            byte[] r4 = r1.toByteArray()     // Catch:{ all -> 0x00af }
            r10.reset()     // Catch:{ IOException -> 0x009d }
            goto L_0x009e
        L_0x009d:
            r5 = move-exception
        L_0x009e:
            com.mysql.jdbc.MySQLConnection r5 = r9.connection     // Catch:{ all -> 0x00c7 }
            boolean r5 = r5.getAutoClosePStmtStreams()     // Catch:{ all -> 0x00c7 }
            if (r5 == 0) goto L_0x00ac
            r10.close()     // Catch:{ IOException -> 0x00aa }
            goto L_0x00ab
        L_0x00aa:
            r5 = move-exception
        L_0x00ab:
            r10 = 0
        L_0x00ac:
            monitor-exit(r0)     // Catch:{ all -> 0x00c7 }
            return r4
        L_0x00af:
            r1 = move-exception
            r10.reset()     // Catch:{ IOException -> 0x00b5 }
            goto L_0x00b6
        L_0x00b5:
            r2 = move-exception
        L_0x00b6:
            com.mysql.jdbc.MySQLConnection r2 = r9.connection     // Catch:{ all -> 0x00c7 }
            boolean r2 = r2.getAutoClosePStmtStreams()     // Catch:{ all -> 0x00c7 }
            if (r2 == 0) goto L_0x00c4
            r10.close()     // Catch:{ IOException -> 0x00c2 }
            goto L_0x00c3
        L_0x00c2:
            r2 = move-exception
        L_0x00c3:
            r10 = 0
        L_0x00c4:
            throw r1     // Catch:{ all -> 0x00c7 }
        L_0x00c7:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00c7 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.PreparedStatement.streamToBytes(java.io.InputStream, boolean, int, boolean):byte[]");
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append(": ");
        try {
            buf.append(asSql());
        } catch (SQLException sqlEx) {
            buf.append("EXCEPTION: " + sqlEx.toString());
        }
        return buf.toString();
    }

    /* access modifiers changed from: protected */
    public int getParameterIndexOffset() {
        return 0;
    }

    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        setAsciiStream(parameterIndex, x, -1);
    }

    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        setAsciiStream(parameterIndex, x, (int) length);
        this.parameterTypes[(parameterIndex - 1) + getParameterIndexOffset()] = 2005;
    }

    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        setBinaryStream(parameterIndex, x, -1);
    }

    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        setBinaryStream(parameterIndex, x, (int) length);
    }

    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        setBinaryStream(parameterIndex, inputStream);
    }

    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        setCharacterStream(parameterIndex, reader, -1);
    }

    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        setCharacterStream(parameterIndex, reader, (int) length);
    }

    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        setCharacterStream(parameterIndex, reader);
    }

    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        setCharacterStream(parameterIndex, reader, length);
    }

    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        setNCharacterStream(parameterIndex, value, -1);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00db, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setNString(int r11, java.lang.String r12) throws java.sql.SQLException {
        /*
            r10 = this;
            com.mysql.jdbc.MySQLConnection r0 = r10.checkClosed()
            java.lang.Object r0 = r0.getConnectionMutex()
            monitor-enter(r0)
            java.lang.String r1 = r10.charEncoding     // Catch:{ all -> 0x00e1 }
            java.lang.String r2 = "UTF-8"
            boolean r1 = r1.equalsIgnoreCase(r2)     // Catch:{ all -> 0x00e1 }
            if (r1 != 0) goto L_0x00dc
            java.lang.String r1 = r10.charEncoding     // Catch:{ all -> 0x00e1 }
            java.lang.String r2 = "utf8"
            boolean r1 = r1.equalsIgnoreCase(r2)     // Catch:{ all -> 0x00e1 }
            if (r1 == 0) goto L_0x0020
            goto L_0x00dc
        L_0x0020:
            if (r12 != 0) goto L_0x0028
            r1 = 1
            r10.setNull(r11, r1)     // Catch:{ all -> 0x00e1 }
            goto L_0x00da
        L_0x0028:
            int r1 = r12.length()     // Catch:{ all -> 0x00e1 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00e1 }
            int r3 = r12.length()     // Catch:{ all -> 0x00e1 }
            double r3 = (double) r3     // Catch:{ all -> 0x00e1 }
            r5 = 4607632778762754458(0x3ff199999999999a, double:1.1)
            double r3 = r3 * r5
            r5 = 4616189618054758400(0x4010000000000000, double:4.0)
            double r3 = r3 + r5
            int r3 = (int) r3     // Catch:{ all -> 0x00e1 }
            r2.<init>(r3)     // Catch:{ all -> 0x00e1 }
            java.lang.String r3 = "_utf8"
            r2.append(r3)     // Catch:{ all -> 0x00e1 }
            r3 = 39
            r2.append(r3)     // Catch:{ all -> 0x00e1 }
            r4 = 0
        L_0x004b:
            if (r4 >= r1) goto L_0x009b
            char r5 = r12.charAt(r4)     // Catch:{ all -> 0x00e1 }
            r6 = 92
            switch(r5) {
                case 0: goto L_0x0090;
                case 10: goto L_0x0087;
                case 13: goto L_0x007e;
                case 26: goto L_0x0075;
                case 34: goto L_0x0068;
                case 39: goto L_0x0061;
                case 92: goto L_0x005a;
                default: goto L_0x0056;
            }     // Catch:{ all -> 0x00e1 }
        L_0x0056:
            r2.append(r5)     // Catch:{ all -> 0x00e1 }
            goto L_0x0098
        L_0x005a:
            r2.append(r6)     // Catch:{ all -> 0x00e1 }
            r2.append(r6)     // Catch:{ all -> 0x00e1 }
            goto L_0x0098
        L_0x0061:
            r2.append(r6)     // Catch:{ all -> 0x00e1 }
            r2.append(r3)     // Catch:{ all -> 0x00e1 }
            goto L_0x0098
        L_0x0068:
            boolean r7 = r10.usingAnsiMode     // Catch:{ all -> 0x00e1 }
            if (r7 == 0) goto L_0x006f
            r2.append(r6)     // Catch:{ all -> 0x00e1 }
        L_0x006f:
            r6 = 34
            r2.append(r6)     // Catch:{ all -> 0x00e1 }
            goto L_0x0098
        L_0x0075:
            r2.append(r6)     // Catch:{ all -> 0x00e1 }
            r6 = 90
            r2.append(r6)     // Catch:{ all -> 0x00e1 }
            goto L_0x0098
        L_0x007e:
            r2.append(r6)     // Catch:{ all -> 0x00e1 }
            r6 = 114(0x72, float:1.6E-43)
            r2.append(r6)     // Catch:{ all -> 0x00e1 }
            goto L_0x0098
        L_0x0087:
            r2.append(r6)     // Catch:{ all -> 0x00e1 }
            r6 = 110(0x6e, float:1.54E-43)
            r2.append(r6)     // Catch:{ all -> 0x00e1 }
            goto L_0x0098
        L_0x0090:
            r2.append(r6)     // Catch:{ all -> 0x00e1 }
            r6 = 48
            r2.append(r6)     // Catch:{ all -> 0x00e1 }
        L_0x0098:
            int r4 = r4 + 1
            goto L_0x004b
        L_0x009b:
            r2.append(r3)     // Catch:{ all -> 0x00e1 }
            java.lang.String r3 = r2.toString()     // Catch:{ all -> 0x00e1 }
            r9 = 0
            boolean r4 = r10.isLoadDataQuery     // Catch:{ all -> 0x00e1 }
            if (r4 != 0) goto L_0x00c6
            com.mysql.jdbc.MySQLConnection r4 = r10.connection     // Catch:{ all -> 0x00e1 }
            java.lang.String r5 = "UTF-8"
            com.mysql.jdbc.SingleByteCharsetConverter r4 = r4.getCharsetConverter(r5)     // Catch:{ all -> 0x00e1 }
            java.lang.String r5 = "UTF-8"
            com.mysql.jdbc.MySQLConnection r6 = r10.connection     // Catch:{ all -> 0x00e1 }
            java.lang.String r6 = r6.getServerCharset()     // Catch:{ all -> 0x00e1 }
            com.mysql.jdbc.MySQLConnection r7 = r10.connection     // Catch:{ all -> 0x00e1 }
            boolean r7 = r7.parserKnowsUnicode()     // Catch:{ all -> 0x00e1 }
            com.mysql.jdbc.ExceptionInterceptor r8 = r10.getExceptionInterceptor()     // Catch:{ all -> 0x00e1 }
            byte[] r4 = com.mysql.jdbc.StringUtils.getBytes((java.lang.String) r3, (com.mysql.jdbc.SingleByteCharsetConverter) r4, (java.lang.String) r5, (java.lang.String) r6, (boolean) r7, (com.mysql.jdbc.ExceptionInterceptor) r8)     // Catch:{ all -> 0x00e1 }
            goto L_0x00ca
        L_0x00c6:
            byte[] r4 = com.mysql.jdbc.StringUtils.getBytes((java.lang.String) r3)     // Catch:{ all -> 0x00e1 }
        L_0x00ca:
            r10.setInternal((int) r11, (byte[]) r4)     // Catch:{ all -> 0x00e1 }
            int[] r5 = r10.parameterTypes     // Catch:{ all -> 0x00e1 }
            int r6 = r11 + -1
            int r7 = r10.getParameterIndexOffset()     // Catch:{ all -> 0x00e1 }
            int r6 = r6 + r7
            r7 = -9
            r5[r6] = r7     // Catch:{ all -> 0x00e1 }
        L_0x00da:
            monitor-exit(r0)     // Catch:{ all -> 0x00e1 }
            return
        L_0x00dc:
            r10.setString(r11, r12)     // Catch:{ all -> 0x00e1 }
            monitor-exit(r0)     // Catch:{ all -> 0x00e1 }
            return
        L_0x00e1:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00e1 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.PreparedStatement.setNString(int, java.lang.String):void");
    }

    public void setNCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        synchronized (checkClosed().getConnectionMutex()) {
            if (reader == null) {
                try {
                    setNull(parameterIndex, -1);
                } catch (IOException ioEx) {
                    throw SQLError.createSQLException(ioEx.toString(), SQLError.SQL_STATE_GENERAL_ERROR, getExceptionInterceptor());
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                if (!this.connection.getUseStreamLengthsInPrepStmts() || length == -1) {
                    char[] c = new char[4096];
                    StringBuilder buf = new StringBuilder();
                    while (true) {
                        int read = reader.read(c);
                        int len = read;
                        if (read == -1) {
                            break;
                        }
                        buf.append(c, 0, len);
                    }
                    setNString(parameterIndex, buf.toString());
                    char[] cArr = c;
                } else {
                    char[] c2 = new char[((int) length)];
                    setNString(parameterIndex, new String(c2, 0, readFully(reader, c2, (int) length)));
                }
                this.parameterTypes[(parameterIndex - 1) + getParameterIndexOffset()] = 2011;
            }
        }
    }

    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        setNCharacterStream(parameterIndex, reader);
    }

    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        if (reader == null) {
            setNull(parameterIndex, -1);
        } else {
            setNCharacterStream(parameterIndex, reader, length);
        }
    }

    public ParameterBindings getParameterBindings() throws SQLException {
        EmulatedPreparedStatementBindings emulatedPreparedStatementBindings;
        synchronized (checkClosed().getConnectionMutex()) {
            emulatedPreparedStatementBindings = new EmulatedPreparedStatementBindings();
        }
        return emulatedPreparedStatementBindings;
    }

    class EmulatedPreparedStatementBindings implements ParameterBindings {
        private ResultSetImpl bindingsAsRs;
        private boolean[] parameterIsNull;

        EmulatedPreparedStatementBindings() throws SQLException {
            int charsetIndex;
            List<ResultSetRow> rows = new ArrayList<>();
            this.parameterIsNull = new boolean[PreparedStatement.this.parameterCount];
            System.arraycopy(PreparedStatement.this.isNull, 0, this.parameterIsNull, 0, PreparedStatement.this.parameterCount);
            byte[][] rowData = new byte[PreparedStatement.this.parameterCount][];
            Field[] typeMetadata = new Field[PreparedStatement.this.parameterCount];
            for (int i = 0; i < PreparedStatement.this.parameterCount; i++) {
                if (PreparedStatement.this.batchCommandIndex == -1) {
                    rowData[i] = PreparedStatement.this.getBytesRepresentation(i);
                } else {
                    rowData[i] = PreparedStatement.this.getBytesRepresentationForBatch(i, PreparedStatement.this.batchCommandIndex);
                }
                if (PreparedStatement.this.parameterTypes[i] == -2 || PreparedStatement.this.parameterTypes[i] == 2004) {
                    charsetIndex = 63;
                } else {
                    try {
                        charsetIndex = CharsetMapping.getCollationIndexForJavaEncoding(PreparedStatement.this.connection.getEncoding(), PreparedStatement.this.connection);
                    } catch (SQLException ex) {
                        throw ex;
                    } catch (RuntimeException ex2) {
                        SQLException sqlEx = SQLError.createSQLException(ex2.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
                        sqlEx.initCause(ex2);
                        throw sqlEx;
                    }
                }
                Field parameterMetadata = new Field((String) null, "parameter_" + (i + 1), charsetIndex, PreparedStatement.this.parameterTypes[i], rowData[i].length);
                parameterMetadata.setConnection(PreparedStatement.this.connection);
                typeMetadata[i] = parameterMetadata;
            }
            rows.add(new ByteArrayRow(rowData, PreparedStatement.this.getExceptionInterceptor()));
            ResultSetImpl resultSetImpl = new ResultSetImpl(PreparedStatement.this.connection.getCatalog(), typeMetadata, new RowDataStatic(rows), PreparedStatement.this.connection, (StatementImpl) null);
            this.bindingsAsRs = resultSetImpl;
            resultSetImpl.next();
        }

        public Array getArray(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getArray(parameterIndex);
        }

        public InputStream getAsciiStream(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getAsciiStream(parameterIndex);
        }

        public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getBigDecimal(parameterIndex);
        }

        public InputStream getBinaryStream(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getBinaryStream(parameterIndex);
        }

        public Blob getBlob(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getBlob(parameterIndex);
        }

        public boolean getBoolean(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getBoolean(parameterIndex);
        }

        public byte getByte(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getByte(parameterIndex);
        }

        public byte[] getBytes(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getBytes(parameterIndex);
        }

        public Reader getCharacterStream(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getCharacterStream(parameterIndex);
        }

        public Clob getClob(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getClob(parameterIndex);
        }

        public java.sql.Date getDate(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getDate(parameterIndex);
        }

        public double getDouble(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getDouble(parameterIndex);
        }

        public float getFloat(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getFloat(parameterIndex);
        }

        public int getInt(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getInt(parameterIndex);
        }

        public long getLong(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getLong(parameterIndex);
        }

        public Reader getNCharacterStream(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getCharacterStream(parameterIndex);
        }

        public Reader getNClob(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getCharacterStream(parameterIndex);
        }

        public Object getObject(int parameterIndex) throws SQLException {
            PreparedStatement.this.checkBounds(parameterIndex, 0);
            if (this.parameterIsNull[parameterIndex - 1]) {
                return null;
            }
            switch (PreparedStatement.this.parameterTypes[parameterIndex - 1]) {
                case -6:
                    return Byte.valueOf(getByte(parameterIndex));
                case -5:
                    return Long.valueOf(getLong(parameterIndex));
                case 4:
                    return Integer.valueOf(getInt(parameterIndex));
                case 5:
                    return Short.valueOf(getShort(parameterIndex));
                case 6:
                    return Float.valueOf(getFloat(parameterIndex));
                case 8:
                    return Double.valueOf(getDouble(parameterIndex));
                default:
                    return this.bindingsAsRs.getObject(parameterIndex);
            }
        }

        public Ref getRef(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getRef(parameterIndex);
        }

        public short getShort(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getShort(parameterIndex);
        }

        public String getString(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getString(parameterIndex);
        }

        public Time getTime(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getTime(parameterIndex);
        }

        public Timestamp getTimestamp(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getTimestamp(parameterIndex);
        }

        public URL getURL(int parameterIndex) throws SQLException {
            return this.bindingsAsRs.getURL(parameterIndex);
        }

        public boolean isNull(int parameterIndex) throws SQLException {
            PreparedStatement.this.checkBounds(parameterIndex, 0);
            return this.parameterIsNull[parameterIndex - 1];
        }
    }

    public String getPreparedSql() {
        try {
            synchronized (checkClosed().getConnectionMutex()) {
                if (this.rewrittenBatchSize == 0) {
                    String str = this.originalSql;
                    return str;
                }
                try {
                    ParseInfo parseInfo2 = this.parseInfo;
                    String sqlForBatch = parseInfo2.getSqlForBatch(parseInfo2);
                    return sqlForBatch;
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e2) {
            throw new RuntimeException(e2);
        }
    }

    public int getUpdateCount() throws SQLException {
        int count = super.getUpdateCount();
        if (!containsOnDuplicateKeyUpdateInSQL() || !this.compensateForOnDuplicateKeyUpdate) {
            return count;
        }
        if (count == 2 || count == 0) {
            return 1;
        }
        return count;
    }

    protected static boolean canRewrite(String sql, boolean isOnDuplicateKeyUpdate, int locationOfOnDuplicateKeyUpdate, int statementStartPos) {
        int updateClausePos;
        if (StringUtils.startsWithIgnoreCaseAndWs(sql, "INSERT", statementStartPos)) {
            if (StringUtils.indexOfIgnoreCase(statementStartPos, sql, "SELECT", "\"'`", "\"'`", StringUtils.SEARCH_MODE__MRK_COM_WS) != -1) {
                return false;
            }
            if (!isOnDuplicateKeyUpdate || (updateClausePos = StringUtils.indexOfIgnoreCase(locationOfOnDuplicateKeyUpdate, sql, " UPDATE ")) == -1) {
                return true;
            }
            if (StringUtils.indexOfIgnoreCase(updateClausePos, sql, "LAST_INSERT_ID", "\"'`", "\"'`", StringUtils.SEARCH_MODE__MRK_COM_WS) == -1) {
                return true;
            }
            return false;
        }
        if (StringUtils.startsWithIgnoreCaseAndWs(sql, "REPLACE", statementStartPos)) {
            if (StringUtils.indexOfIgnoreCase(statementStartPos, sql, "SELECT", "\"'`", "\"'`", StringUtils.SEARCH_MODE__MRK_COM_WS) == -1) {
                return true;
            }
        }
        return false;
    }

    public long executeLargeUpdate() throws SQLException {
        return executeUpdateInternal(true, false);
    }
}
