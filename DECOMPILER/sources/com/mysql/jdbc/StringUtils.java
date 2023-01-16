package com.mysql.jdbc;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

public class StringUtils {
    private static final int BYTE_RANGE = 256;
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final int NON_COMMENTS_MYSQL_VERSION_REF_LENGTH = 5;
    public static final Set<SearchMode> SEARCH_MODE__ALL = Collections.unmodifiableSet(EnumSet.allOf(SearchMode.class));
    public static final Set<SearchMode> SEARCH_MODE__BSESC_COM_WS = Collections.unmodifiableSet(EnumSet.of(SearchMode.ALLOW_BACKSLASH_ESCAPE, SearchMode.SKIP_BLOCK_COMMENTS, SearchMode.SKIP_LINE_COMMENTS, SearchMode.SKIP_WHITE_SPACE));
    public static final Set<SearchMode> SEARCH_MODE__BSESC_MRK_WS = Collections.unmodifiableSet(EnumSet.of(SearchMode.ALLOW_BACKSLASH_ESCAPE, SearchMode.SKIP_BETWEEN_MARKERS, SearchMode.SKIP_WHITE_SPACE));
    public static final Set<SearchMode> SEARCH_MODE__COM_WS = Collections.unmodifiableSet(EnumSet.of(SearchMode.SKIP_BLOCK_COMMENTS, SearchMode.SKIP_LINE_COMMENTS, SearchMode.SKIP_WHITE_SPACE));
    public static final Set<SearchMode> SEARCH_MODE__MRK_COM_WS = Collections.unmodifiableSet(EnumSet.of(SearchMode.SKIP_BETWEEN_MARKERS, SearchMode.SKIP_BLOCK_COMMENTS, SearchMode.SKIP_LINE_COMMENTS, SearchMode.SKIP_WHITE_SPACE));
    public static final Set<SearchMode> SEARCH_MODE__MRK_WS = Collections.unmodifiableSet(EnumSet.of(SearchMode.SKIP_BETWEEN_MARKERS, SearchMode.SKIP_WHITE_SPACE));
    public static final Set<SearchMode> SEARCH_MODE__NONE = Collections.unmodifiableSet(EnumSet.noneOf(SearchMode.class));
    private static final String VALID_ID_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789$_#@";
    static final char WILDCARD_ESCAPE = '\\';
    static final char WILDCARD_MANY = '%';
    static final char WILDCARD_ONE = '_';
    private static final int WILD_COMPARE_CONTINUE_WITH_WILD = 1;
    private static final int WILD_COMPARE_MATCH = 0;
    private static final int WILD_COMPARE_NO_MATCH = -1;
    private static byte[] allBytes = new byte[256];
    private static char[] byteToChars = new char[256];
    private static final ConcurrentHashMap<String, Charset> charsetsByAlias = new ConcurrentHashMap<>();
    private static final String platformEncoding = System.getProperty("file.encoding");
    private static Method toPlainStringMethod;

    public enum SearchMode {
        ALLOW_BACKSLASH_ESCAPE,
        SKIP_BETWEEN_MARKERS,
        SKIP_BLOCK_COMMENTS,
        SKIP_LINE_COMMENTS,
        SKIP_WHITE_SPACE
    }

    static {
        for (int i = -128; i <= 127; i++) {
            allBytes[i + 128] = (byte) i;
        }
        String allBytesString = new String(allBytes, 0, 255);
        int allBytesStringLen = allBytesString.length();
        int i2 = 0;
        while (i2 < 255 && i2 < allBytesStringLen) {
            byteToChars[i2] = allBytesString.charAt(i2);
            i2++;
        }
        try {
            toPlainStringMethod = BigDecimal.class.getMethod("toPlainString", new Class[0]);
        } catch (NoSuchMethodException e) {
        }
    }

    static Charset findCharset(String alias) throws UnsupportedEncodingException {
        try {
            ConcurrentHashMap<String, Charset> concurrentHashMap = charsetsByAlias;
            Charset cs = concurrentHashMap.get(alias);
            if (cs != null) {
                return cs;
            }
            Charset cs2 = Charset.forName(alias);
            Charset oldCs = concurrentHashMap.putIfAbsent(alias, cs2);
            if (oldCs != null) {
                return oldCs;
            }
            return cs2;
        } catch (UnsupportedCharsetException e) {
            throw new UnsupportedEncodingException(alias);
        } catch (IllegalCharsetNameException e2) {
            throw new UnsupportedEncodingException(alias);
        } catch (IllegalArgumentException e3) {
            throw new UnsupportedEncodingException(alias);
        }
    }

    public static String consistentToString(BigDecimal decimal) {
        if (decimal == null) {
            return null;
        }
        Method method = toPlainStringMethod;
        if (method != null) {
            try {
                Object[] objArr = null;
                return (String) method.invoke(decimal, (Object[]) null);
            } catch (IllegalAccessException | InvocationTargetException e) {
            }
        }
        return decimal.toString();
    }

    public static String dumpAsHex(byte[] byteBuffer, int length) {
        int i = length;
        StringBuilder outputBuilder = new StringBuilder(i * 4);
        int p = 0;
        int rows = i / 8;
        for (int i2 = 0; i2 < rows && p < i; i2++) {
            int ptemp = p;
            for (int j = 0; j < 8; j++) {
                String hexVal = Integer.toHexString(byteBuffer[ptemp] & 255);
                if (hexVal.length() == 1) {
                    hexVal = "0" + hexVal;
                }
                outputBuilder.append(hexVal + " ");
                ptemp++;
            }
            outputBuilder.append("    ");
            for (int j2 = 0; j2 < 8; j2++) {
                int b = byteBuffer[p] & 255;
                if (b <= 32 || b >= 127) {
                    outputBuilder.append(". ");
                } else {
                    outputBuilder.append(((char) b) + " ");
                }
                p++;
            }
            outputBuilder.append("\n");
        }
        int n = 0;
        for (int i3 = p; i3 < i; i3++) {
            String hexVal2 = Integer.toHexString(byteBuffer[i3] & 255);
            if (hexVal2.length() == 1) {
                hexVal2 = "0" + hexVal2;
            }
            outputBuilder.append(hexVal2 + " ");
            n++;
        }
        for (int i4 = n; i4 < 8; i4++) {
            outputBuilder.append("   ");
        }
        outputBuilder.append("    ");
        for (int i5 = p; i5 < i; i5++) {
            int b2 = byteBuffer[i5] & 255;
            if (b2 > 32) {
                if (b2 < 127) {
                    outputBuilder.append(((char) b2) + " ");
                }
            }
            outputBuilder.append(". ");
        }
        outputBuilder.append("\n");
        return outputBuilder.toString();
    }

    private static boolean endsWith(byte[] dataFrom, String suffix) {
        for (int i = 1; i <= suffix.length(); i++) {
            if (dataFrom[dataFrom.length - i] != suffix.charAt(suffix.length() - i)) {
                return false;
            }
        }
        return true;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v4, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v9, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v6, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v11, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v12, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v13, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v14, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v7, resolved type: byte} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static byte[] escapeEasternUnicodeByteStream(byte[] r8, java.lang.String r9) {
        /*
            if (r8 != 0) goto L_0x0004
            r0 = 0
            return r0
        L_0x0004:
            int r0 = r8.length
            if (r0 != 0) goto L_0x000b
            r0 = 0
            byte[] r0 = new byte[r0]
            return r0
        L_0x000b:
            int r0 = r8.length
            r1 = 0
            r2 = 0
            java.io.ByteArrayOutputStream r3 = new java.io.ByteArrayOutputStream
            r3.<init>(r0)
        L_0x0013:
            char r4 = r9.charAt(r2)
            r5 = 92
            if (r4 != r5) goto L_0x0024
            int r4 = r1 + 1
            byte r1 = r8[r1]
            r3.write(r1)
            r1 = r4
            goto L_0x0064
        L_0x0024:
            byte r4 = r8[r1]
            if (r4 >= 0) goto L_0x002a
            int r4 = r4 + 256
        L_0x002a:
            r3.write(r4)
            r6 = 128(0x80, float:1.794E-43)
            if (r4 < r6) goto L_0x0048
            int r6 = r0 + -1
            if (r1 >= r6) goto L_0x0062
            int r6 = r1 + 1
            byte r6 = r8[r6]
            if (r6 >= 0) goto L_0x003d
            int r6 = r6 + 256
        L_0x003d:
            r3.write(r6)
            int r1 = r1 + 1
            if (r6 != r5) goto L_0x0047
            r3.write(r6)
        L_0x0047:
            goto L_0x0062
        L_0x0048:
            if (r4 != r5) goto L_0x0062
            int r6 = r0 + -1
            if (r1 >= r6) goto L_0x0062
            int r6 = r1 + 1
            byte r6 = r8[r6]
            if (r6 >= 0) goto L_0x0056
            int r6 = r6 + 256
        L_0x0056:
            r7 = 98
            if (r6 != r7) goto L_0x0062
            r3.write(r5)
            r3.write(r7)
            int r1 = r1 + 1
        L_0x0062:
            int r1 = r1 + 1
        L_0x0064:
            if (r1 < r0) goto L_0x006c
            byte[] r4 = r3.toByteArray()
            return r4
        L_0x006c:
            int r2 = r2 + 1
            goto L_0x0013
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.StringUtils.escapeEasternUnicodeByteStream(byte[], java.lang.String):byte[]");
    }

    public static char firstNonWsCharUc(String searchIn) {
        return firstNonWsCharUc(searchIn, 0);
    }

    public static char firstNonWsCharUc(String searchIn, int startAt) {
        if (searchIn == null) {
            return 0;
        }
        int length = searchIn.length();
        for (int i = startAt; i < length; i++) {
            char c = searchIn.charAt(i);
            if (!Character.isWhitespace(c)) {
                return Character.toUpperCase(c);
            }
        }
        return 0;
    }

    public static char firstAlphaCharUc(String searchIn, int startAt) {
        if (searchIn == null) {
            return 0;
        }
        int length = searchIn.length();
        for (int i = startAt; i < length; i++) {
            char c = searchIn.charAt(i);
            if (Character.isLetter(c)) {
                return Character.toUpperCase(c);
            }
        }
        return 0;
    }

    public static String fixDecimalExponent(String dString) {
        char maybeMinusChar;
        int ePos = dString.indexOf(69);
        if (ePos == -1) {
            ePos = dString.indexOf(101);
        }
        if (ePos == -1 || dString.length() <= ePos + 1 || (maybeMinusChar = dString.charAt(ePos + 1)) == '-' || maybeMinusChar == '+') {
            return dString;
        }
        StringBuilder strBuilder = new StringBuilder(dString.length() + 1);
        strBuilder.append(dString.substring(0, ePos + 1));
        strBuilder.append('+');
        strBuilder.append(dString.substring(ePos + 1, dString.length()));
        return strBuilder.toString();
    }

    public static byte[] getBytes(char[] c, SingleByteCharsetConverter converter, String encoding, String serverEncoding, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        if (converter != null) {
            try {
                return converter.toBytes(c);
            } catch (UnsupportedEncodingException e) {
                throw SQLError.createSQLException(Messages.getString("StringUtils.0") + encoding + Messages.getString("StringUtils.1"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
            }
        } else if (encoding == null) {
            return getBytes(c);
        } else {
            byte[] b = getBytes(c, encoding);
            if (parserKnowsUnicode || !CharsetMapping.requiresEscapeEasternUnicode(encoding) || encoding.equalsIgnoreCase(serverEncoding)) {
                return b;
            }
            return escapeEasternUnicodeByteStream(b, new String(c));
        }
    }

    public static byte[] getBytes(char[] c, SingleByteCharsetConverter converter, String encoding, String serverEncoding, int offset, int length, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        if (converter != null) {
            try {
                return converter.toBytes(c, offset, length);
            } catch (UnsupportedEncodingException e) {
                throw SQLError.createSQLException(Messages.getString("StringUtils.0") + encoding + Messages.getString("StringUtils.1"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
            }
        } else if (encoding == null) {
            return getBytes(c, offset, length);
        } else {
            byte[] b = getBytes(c, offset, length, encoding);
            if (parserKnowsUnicode || !CharsetMapping.requiresEscapeEasternUnicode(encoding) || encoding.equalsIgnoreCase(serverEncoding)) {
                return b;
            }
            return escapeEasternUnicodeByteStream(b, new String(c, offset, length));
        }
    }

    public static byte[] getBytes(char[] c, String encoding, String serverEncoding, boolean parserKnowsUnicode, MySQLConnection conn, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        SingleByteCharsetConverter converter;
        if (conn != null) {
            try {
                converter = conn.getCharsetConverter(encoding);
            } catch (UnsupportedEncodingException e) {
                throw SQLError.createSQLException(Messages.getString("StringUtils.0") + encoding + Messages.getString("StringUtils.1"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
            }
        } else {
            converter = SingleByteCharsetConverter.getInstance(encoding, (Connection) null);
        }
        return getBytes(c, converter, encoding, serverEncoding, parserKnowsUnicode, exceptionInterceptor);
    }

    public static byte[] getBytes(String s, SingleByteCharsetConverter converter, String encoding, String serverEncoding, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        if (converter != null) {
            try {
                return converter.toBytes(s);
            } catch (UnsupportedEncodingException e) {
                throw SQLError.createSQLException(Messages.getString("StringUtils.5") + encoding + Messages.getString("StringUtils.6"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
            }
        } else if (encoding == null) {
            return getBytes(s);
        } else {
            byte[] b = getBytes(s, encoding);
            if (parserKnowsUnicode || !CharsetMapping.requiresEscapeEasternUnicode(encoding) || encoding.equalsIgnoreCase(serverEncoding)) {
                return b;
            }
            return escapeEasternUnicodeByteStream(b, s);
        }
    }

    public static byte[] getBytes(String s, SingleByteCharsetConverter converter, String encoding, String serverEncoding, int offset, int length, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        if (converter != null) {
            try {
                return converter.toBytes(s, offset, length);
            } catch (UnsupportedEncodingException e) {
                throw SQLError.createSQLException(Messages.getString("StringUtils.5") + encoding + Messages.getString("StringUtils.6"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
            }
        } else if (encoding == null) {
            return getBytes(s, offset, length);
        } else {
            String s2 = s.substring(offset, offset + length);
            byte[] b = getBytes(s2, encoding);
            if (parserKnowsUnicode || !CharsetMapping.requiresEscapeEasternUnicode(encoding) || encoding.equalsIgnoreCase(serverEncoding)) {
                return b;
            }
            return escapeEasternUnicodeByteStream(b, s2);
        }
    }

    public static byte[] getBytes(String s, String encoding, String serverEncoding, boolean parserKnowsUnicode, MySQLConnection conn, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        SingleByteCharsetConverter converter;
        if (conn != null) {
            try {
                converter = conn.getCharsetConverter(encoding);
            } catch (UnsupportedEncodingException e) {
                throw SQLError.createSQLException(Messages.getString("StringUtils.5") + encoding + Messages.getString("StringUtils.6"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
            }
        } else {
            converter = SingleByteCharsetConverter.getInstance(encoding, (Connection) null);
        }
        return getBytes(s, converter, encoding, serverEncoding, parserKnowsUnicode, exceptionInterceptor);
    }

    public static final byte[] getBytes(String s, String encoding, String serverEncoding, int offset, int length, boolean parserKnowsUnicode, MySQLConnection conn, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        SingleByteCharsetConverter converter;
        String str = encoding;
        MySQLConnection mySQLConnection = conn;
        if (mySQLConnection != null) {
            try {
                converter = mySQLConnection.getCharsetConverter(encoding);
            } catch (UnsupportedEncodingException e) {
                throw SQLError.createSQLException(Messages.getString("StringUtils.5") + encoding + Messages.getString("StringUtils.6"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
            }
        } else {
            converter = SingleByteCharsetConverter.getInstance(encoding, (Connection) null);
        }
        return getBytes(s, converter, encoding, serverEncoding, offset, length, parserKnowsUnicode, exceptionInterceptor);
    }

    public static byte[] getBytesWrapped(String s, char beginWrap, char endWrap, SingleByteCharsetConverter converter, String encoding, String serverEncoding, boolean parserKnowsUnicode, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        if (converter != null) {
            try {
                return converter.toBytesWrapped(s, beginWrap, endWrap);
            } catch (UnsupportedEncodingException e) {
                throw SQLError.createSQLException(Messages.getString("StringUtils.10") + encoding + Messages.getString("StringUtils.11"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
            }
        } else if (encoding == null) {
            StringBuilder strBuilder = new StringBuilder(s.length() + 2);
            strBuilder.append(beginWrap);
            strBuilder.append(s);
            strBuilder.append(endWrap);
            return getBytes(strBuilder.toString());
        } else {
            StringBuilder strBuilder2 = new StringBuilder(s.length() + 2);
            strBuilder2.append(beginWrap);
            strBuilder2.append(s);
            strBuilder2.append(endWrap);
            String s2 = strBuilder2.toString();
            byte[] b = getBytes(s2, encoding);
            if (parserKnowsUnicode || !CharsetMapping.requiresEscapeEasternUnicode(encoding) || encoding.equalsIgnoreCase(serverEncoding)) {
                return b;
            }
            return escapeEasternUnicodeByteStream(b, s2);
        }
    }

    public static int getInt(byte[] buf) throws NumberFormatException {
        return getInt(buf, 0, buf.length);
    }

    public static int getInt(byte[] buf, int offset, int endPos) throws NumberFormatException {
        char c;
        int s = offset;
        while (s < endPos && Character.isWhitespace((char) buf[s])) {
            s++;
        }
        if (s != endPos) {
            boolean negative = false;
            if (((char) buf[s]) == '-') {
                negative = true;
                s++;
            } else if (((char) buf[s]) == '+') {
                s++;
            }
            int save = s;
            int cutoff = Integer.MAX_VALUE / 10;
            int cutlim = Integer.MAX_VALUE % 10;
            if (negative) {
                cutlim++;
            }
            boolean overflow = false;
            int i = 0;
            while (s < endPos) {
                char c2 = (char) buf[s];
                if (!Character.isDigit(c2)) {
                    if (!Character.isLetter(c2)) {
                        break;
                    }
                    c = (char) ((Character.toUpperCase(c2) - 'A') + 10);
                } else {
                    c = (char) (c2 - '0');
                }
                if (c >= 10) {
                    break;
                }
                if (i > cutoff || (i == cutoff && c > cutlim)) {
                    overflow = true;
                } else {
                    i = (i * 10) + c;
                }
                s++;
            }
            if (s == save) {
                throw new NumberFormatException(toString(buf));
            } else if (!overflow) {
                return negative ? -i : i;
            } else {
                throw new NumberFormatException(toString(buf));
            }
        } else {
            throw new NumberFormatException(toString(buf));
        }
    }

    public static long getLong(byte[] buf) throws NumberFormatException {
        return getLong(buf, 0, buf.length);
    }

    public static long getLong(byte[] buf, int offset, int endpos) throws NumberFormatException {
        char c;
        int i = endpos;
        int s = offset;
        while (s < i && Character.isWhitespace((char) buf[s])) {
            s++;
        }
        if (s != i) {
            boolean negative = false;
            if (((char) buf[s]) == '-') {
                negative = true;
                s++;
            } else if (((char) buf[s]) == '+') {
                s++;
            }
            int save = s;
            long cutoff = Long.MAX_VALUE / ((long) 10);
            long cutlim = (long) ((int) (Long.MAX_VALUE % ((long) 10)));
            if (negative) {
                cutlim++;
            }
            boolean overflow = false;
            long i2 = 0;
            while (s < i) {
                char c2 = (char) buf[s];
                if (!Character.isDigit(c2)) {
                    if (!Character.isLetter(c2)) {
                        break;
                    }
                    c = (char) ((Character.toUpperCase(c2) - 'A') + 10);
                } else {
                    c = (char) (c2 - '0');
                }
                if (c >= 10) {
                    break;
                }
                if (i2 > cutoff || (i2 == cutoff && ((long) c) > cutlim)) {
                    overflow = true;
                } else {
                    i2 = (i2 * ((long) 10)) + ((long) c);
                }
                s++;
            }
            if (s == save) {
                throw new NumberFormatException(toString(buf));
            } else if (!overflow) {
                return negative ? -i2 : i2;
            } else {
                throw new NumberFormatException(toString(buf));
            }
        } else {
            throw new NumberFormatException(toString(buf));
        }
    }

    public static short getShort(byte[] buf) throws NumberFormatException {
        return getShort(buf, 0, buf.length);
    }

    public static short getShort(byte[] buf, int offset, int endpos) throws NumberFormatException {
        char c;
        int s = offset;
        while (s < endpos && Character.isWhitespace((char) buf[s])) {
            s++;
        }
        if (s != endpos) {
            boolean negative = false;
            if (((char) buf[s]) == '-') {
                negative = true;
                s++;
            } else if (((char) buf[s]) == '+') {
                s++;
            }
            int save = s;
            short cutoff = (short) (32767 / 10);
            short cutlim = (short) (32767 % 10);
            if (negative) {
                cutlim = (short) (cutlim + 1);
            }
            boolean overflow = false;
            short i = 0;
            while (s < endpos) {
                char c2 = (char) buf[s];
                if (!Character.isDigit(c2)) {
                    if (!Character.isLetter(c2)) {
                        break;
                    }
                    c = (char) ((Character.toUpperCase(c2) - 'A') + 10);
                } else {
                    c = (char) (c2 - '0');
                }
                if (c >= 10) {
                    break;
                }
                if (i > cutoff || (i == cutoff && c > cutlim)) {
                    overflow = true;
                } else {
                    i = (short) (((short) (i * 10)) + c);
                }
                s++;
            }
            if (s == save) {
                throw new NumberFormatException(toString(buf));
            } else if (!overflow) {
                return negative ? (short) (-i) : i;
            } else {
                throw new NumberFormatException(toString(buf));
            }
        } else {
            throw new NumberFormatException(toString(buf));
        }
    }

    public static int indexOfIgnoreCase(String searchIn, String searchFor) {
        return indexOfIgnoreCase(0, searchIn, searchFor);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0006, code lost:
        r1 = r9.length();
        r2 = r10.length();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int indexOfIgnoreCase(int r8, java.lang.String r9, java.lang.String r10) {
        /*
            r0 = -1
            if (r9 == 0) goto L_0x0048
            if (r10 != 0) goto L_0x0006
            goto L_0x0048
        L_0x0006:
            int r1 = r9.length()
            int r2 = r10.length()
            int r3 = r1 - r2
            if (r8 > r3) goto L_0x0047
            if (r2 != 0) goto L_0x0015
            goto L_0x0047
        L_0x0015:
            r4 = 0
            char r5 = r10.charAt(r4)
            char r5 = java.lang.Character.toUpperCase(r5)
            char r4 = r10.charAt(r4)
            char r4 = java.lang.Character.toLowerCase(r4)
            r6 = r8
        L_0x0027:
            if (r6 > r3) goto L_0x0046
            boolean r7 = isCharAtPosNotEqualIgnoreCase(r9, r6, r5, r4)
            if (r7 == 0) goto L_0x003a
        L_0x002f:
            int r6 = r6 + 1
            if (r6 > r3) goto L_0x003a
            boolean r7 = isCharAtPosNotEqualIgnoreCase(r9, r6, r5, r4)
            if (r7 == 0) goto L_0x003a
            goto L_0x002f
        L_0x003a:
            if (r6 > r3) goto L_0x0043
            boolean r7 = startsWithIgnoreCase(r9, r6, r10)
            if (r7 == 0) goto L_0x0043
            return r6
        L_0x0043:
            int r6 = r6 + 1
            goto L_0x0027
        L_0x0046:
            return r0
        L_0x0047:
            return r0
        L_0x0048:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.StringUtils.indexOfIgnoreCase(int, java.lang.String, java.lang.String):int");
    }

    public static int indexOfIgnoreCase(int startingPosition, String searchIn, String[] searchForSequence, String openingMarkers, String closingMarkers, Set<SearchMode> searchMode) {
        Set<SearchMode> searchMode2;
        int wc;
        String str = searchIn;
        String[] strArr = searchForSequence;
        Set<SearchMode> set = searchMode;
        if (str == null) {
            int i = startingPosition;
        } else if (strArr == null) {
            int i2 = startingPosition;
        } else {
            int searchInLength = searchIn.length();
            int searchForLength = 0;
            for (String searchForPart : searchForSequence) {
                searchForLength += searchForPart.length();
            }
            if (searchForLength == 0) {
                return -1;
            }
            int searchForWordsCount = strArr.length;
            char c = 0;
            int stopSearchingAt = searchInLength - (searchForLength + (searchForWordsCount > 0 ? searchForWordsCount - 1 : 0));
            if (startingPosition > stopSearchingAt) {
                return -1;
            }
            if (!set.contains(SearchMode.SKIP_BETWEEN_MARKERS) || !(openingMarkers == null || closingMarkers == null || openingMarkers.length() != closingMarkers.length())) {
                if (!Character.isWhitespace(strArr[0].charAt(0)) || !set.contains(SearchMode.SKIP_WHITE_SPACE)) {
                    searchMode2 = set;
                } else {
                    Set<SearchMode> copyOf = EnumSet.copyOf(searchMode);
                    copyOf.remove(SearchMode.SKIP_WHITE_SPACE);
                    searchMode2 = copyOf;
                }
                Set<SearchMode> searchMode22 = EnumSet.of(SearchMode.SKIP_WHITE_SPACE);
                searchMode22.addAll(searchMode2);
                searchMode22.remove(SearchMode.SKIP_BETWEEN_MARKERS);
                int positionOfFirstWord = startingPosition;
                while (positionOfFirstWord <= stopSearchingAt) {
                    int i3 = positionOfFirstWord;
                    Set<SearchMode> searchMode23 = searchMode22;
                    int positionOfFirstWord2 = indexOfIgnoreCase(positionOfFirstWord, searchIn, strArr[c], openingMarkers, closingMarkers, searchMode2);
                    if (positionOfFirstWord2 == -1) {
                        int i4 = positionOfFirstWord2;
                        Set<SearchMode> set2 = searchMode2;
                    } else if (positionOfFirstWord2 > stopSearchingAt) {
                        int i5 = positionOfFirstWord2;
                        Set<SearchMode> set3 = searchMode2;
                    } else {
                        int wc2 = 0;
                        int startingPositionForNextWord = strArr[c].length() + positionOfFirstWord2;
                        boolean match = true;
                        while (true) {
                            wc = wc2 + 1;
                            if (wc >= searchForWordsCount || !match) {
                                int i6 = startingPositionForNextWord;
                                int positionOfFirstWord3 = positionOfFirstWord2;
                                Set<SearchMode> searchMode3 = searchMode2;
                            } else {
                                int wc3 = wc;
                                int startingPositionForNextWord2 = startingPositionForNextWord;
                                int positionOfFirstWord4 = positionOfFirstWord2;
                                Set<SearchMode> searchMode4 = searchMode2;
                                int positionOfNextWord = indexOfNextChar(startingPositionForNextWord, searchInLength - 1, searchIn, (String) null, (String) null, (String) null, searchMode23);
                                if (startingPositionForNextWord2 == positionOfNextWord || !startsWithIgnoreCase(str, positionOfNextWord, strArr[wc3])) {
                                    match = false;
                                    startingPositionForNextWord = startingPositionForNextWord2;
                                } else {
                                    startingPositionForNextWord = strArr[wc3].length() + positionOfNextWord;
                                }
                                positionOfFirstWord2 = positionOfFirstWord4;
                                searchMode2 = searchMode4;
                                wc2 = wc3;
                            }
                        }
                        int i62 = startingPositionForNextWord;
                        int positionOfFirstWord32 = positionOfFirstWord2;
                        Set<SearchMode> searchMode32 = searchMode2;
                        if (match) {
                            return positionOfFirstWord32;
                        }
                        positionOfFirstWord = positionOfFirstWord32 + 1;
                        searchMode22 = searchMode23;
                        searchMode2 = searchMode32;
                        c = 0;
                    }
                    return -1;
                }
                return -1;
            }
            throw new IllegalArgumentException(Messages.getString("StringUtils.15", new String[]{openingMarkers, closingMarkers}));
        }
        return -1;
    }

    public static int indexOfIgnoreCase(int startingPosition, String searchIn, String searchFor, String openingMarkers, String closingMarkers, Set<SearchMode> searchMode) {
        return indexOfIgnoreCase(startingPosition, searchIn, searchFor, openingMarkers, closingMarkers, "", searchMode);
    }

    public static int indexOfIgnoreCase(int startingPosition, String searchIn, String searchFor, String openingMarkers, String closingMarkers, String overridingMarkers, Set<SearchMode> searchMode) {
        int i;
        Set<SearchMode> searchMode2;
        String str = searchIn;
        String str2 = searchFor;
        String str3 = openingMarkers;
        Set<SearchMode> searchMode3 = searchMode;
        if (str == null) {
            int i2 = startingPosition;
        } else if (str2 == null) {
            int i3 = startingPosition;
        } else {
            int searchInLength = searchIn.length();
            int searchForLength = searchFor.length();
            int stopSearchingAt = searchInLength - searchForLength;
            if (startingPosition > stopSearchingAt || searchForLength == 0) {
                return -1;
            }
            if (!searchMode3.contains(SearchMode.SKIP_BETWEEN_MARKERS)) {
                i = 0;
            } else if (str3 == null || closingMarkers == null || openingMarkers.length() != closingMarkers.length()) {
                throw new IllegalArgumentException(Messages.getString("StringUtils.15", new String[]{str3, closingMarkers}));
            } else if (overridingMarkers != null) {
                char[] arr$ = overridingMarkers.toCharArray();
                int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    if (str3.indexOf(arr$[i$]) != -1) {
                        i$++;
                    } else {
                        throw new IllegalArgumentException(Messages.getString("StringUtils.16", new String[]{overridingMarkers, str3}));
                    }
                }
                i = 0;
            } else {
                throw new IllegalArgumentException(Messages.getString("StringUtils.16", new String[]{overridingMarkers, str3}));
            }
            char firstCharOfSearchForUc = Character.toUpperCase(str2.charAt(i));
            char firstCharOfSearchForLc = Character.toLowerCase(str2.charAt(i));
            if (!Character.isWhitespace(firstCharOfSearchForLc) || !searchMode3.contains(SearchMode.SKIP_WHITE_SPACE)) {
                searchMode2 = searchMode3;
            } else {
                Set<SearchMode> searchMode4 = EnumSet.copyOf(searchMode);
                searchMode4.remove(SearchMode.SKIP_WHITE_SPACE);
                searchMode2 = searchMode4;
            }
            int i4 = startingPosition;
            while (i4 <= stopSearchingAt) {
                int i5 = i4;
                char firstCharOfSearchForLc2 = firstCharOfSearchForLc;
                int i6 = indexOfNextChar(i4, stopSearchingAt, searchIn, openingMarkers, closingMarkers, overridingMarkers, searchMode2);
                if (i6 == -1) {
                    return -1;
                }
                char firstCharOfSearchForLc3 = firstCharOfSearchForLc2;
                if (isCharEqualIgnoreCase(str.charAt(i6), firstCharOfSearchForUc, firstCharOfSearchForLc3) && startsWithIgnoreCase(str, i6, str2)) {
                    return i6;
                }
                i4 = i6 + 1;
                firstCharOfSearchForLc = firstCharOfSearchForLc3;
            }
            return -1;
        }
        return -1;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:137:0x01f5, code lost:
        if (r11 != 0) goto L_0x01fd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:141:0x01ff, code lost:
        if (r9 == '#') goto L_0x0201;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int indexOfNextChar(int r20, int r21, java.lang.String r22, java.lang.String r23, java.lang.String r24, java.lang.String r25, java.util.Set<com.mysql.jdbc.StringUtils.SearchMode> r26) {
        /*
            r0 = r20
            r1 = r21
            r2 = r22
            r3 = r23
            r4 = r24
            r5 = r25
            r6 = r26
            r7 = -1
            if (r2 != 0) goto L_0x0012
            return r7
        L_0x0012:
            int r8 = r22.length()
            if (r0 < r8) goto L_0x0019
            return r7
        L_0x0019:
            r9 = 0
            char r10 = r2.charAt(r0)
            int r11 = r0 + 1
            if (r11 >= r8) goto L_0x0029
            int r11 = r0 + 1
            char r11 = r2.charAt(r11)
            goto L_0x002a
        L_0x0029:
            r11 = 0
        L_0x002a:
            r13 = r20
        L_0x002c:
            if (r13 > r1) goto L_0x0281
            r9 = r10
            r10 = r11
            int r14 = r13 + 2
            if (r14 >= r8) goto L_0x003b
            int r14 = r13 + 2
            char r14 = r2.charAt(r14)
            goto L_0x003c
        L_0x003b:
            r14 = 0
        L_0x003c:
            r11 = r14
            r14 = 0
            r15 = -1
            com.mysql.jdbc.StringUtils$SearchMode r12 = com.mysql.jdbc.StringUtils.SearchMode.ALLOW_BACKSLASH_ESCAPE
            boolean r12 = r6.contains(r12)
            r7 = 92
            r16 = 1
            if (r12 == 0) goto L_0x0060
            if (r9 != r7) goto L_0x0060
            int r13 = r13 + 1
            r7 = r11
            int r10 = r13 + 2
            if (r10 >= r8) goto L_0x005b
            int r10 = r13 + 2
            char r10 = r2.charAt(r10)
            goto L_0x005c
        L_0x005b:
            r10 = 0
        L_0x005c:
            r11 = r10
            r10 = r7
            goto L_0x0273
        L_0x0060:
            com.mysql.jdbc.StringUtils$SearchMode r12 = com.mysql.jdbc.StringUtils.SearchMode.SKIP_BETWEEN_MARKERS
            boolean r12 = r6.contains(r12)
            if (r12 == 0) goto L_0x0147
            int r12 = r3.indexOf(r9)
            r15 = r12
            r7 = -1
            if (r12 == r7) goto L_0x0147
            r12 = 0
            r17 = r9
            char r7 = r4.charAt(r15)
            r0 = r17
            r17 = r12
            int r12 = r5.indexOf(r0)
            r18 = r14
            r14 = -1
            if (r12 == r14) goto L_0x0087
            r12 = r16
            goto L_0x0088
        L_0x0087:
            r12 = 0
        L_0x0088:
            int r13 = r13 + 1
            if (r13 > r1) goto L_0x012a
            char r14 = r2.charAt(r13)
            r9 = r14
            if (r14 != r7) goto L_0x0095
            if (r17 == 0) goto L_0x012a
        L_0x0095:
            if (r12 != 0) goto L_0x00f3
            int r14 = r5.indexOf(r9)
            r5 = -1
            if (r14 == r5) goto L_0x00f3
            int r5 = r3.indexOf(r9)
            r14 = 0
            r19 = r9
            char r3 = r4.charAt(r5)
        L_0x00a9:
            int r13 = r13 + 1
            if (r13 > r1) goto L_0x00e8
            char r4 = r2.charAt(r13)
            r9 = r4
            if (r4 != r3) goto L_0x00b6
            if (r14 == 0) goto L_0x00ec
        L_0x00b6:
            r4 = r19
            if (r9 != r4) goto L_0x00c1
            int r14 = r14 + 1
            r19 = r4
            r4 = r24
            goto L_0x00a9
        L_0x00c1:
            if (r9 != r3) goto L_0x00ca
            int r14 = r14 + -1
            r19 = r4
            r4 = r24
            goto L_0x00a9
        L_0x00ca:
            r19 = r3
            com.mysql.jdbc.StringUtils$SearchMode r3 = com.mysql.jdbc.StringUtils.SearchMode.ALLOW_BACKSLASH_ESCAPE
            boolean r3 = r6.contains(r3)
            if (r3 == 0) goto L_0x00e1
            r3 = 92
            if (r9 != r3) goto L_0x00e1
            int r13 = r13 + 1
            r3 = r19
            r19 = r4
            r4 = r24
            goto L_0x00a9
        L_0x00e1:
            r3 = r19
            r19 = r4
            r4 = r24
            goto L_0x00a9
        L_0x00e8:
            r4 = r19
            r19 = r3
        L_0x00ec:
            r3 = r23
            r4 = r24
            r5 = r25
            goto L_0x0088
        L_0x00f3:
            if (r9 != r0) goto L_0x00fe
            int r17 = r17 + 1
            r3 = r23
            r4 = r24
            r5 = r25
            goto L_0x0088
        L_0x00fe:
            if (r9 != r7) goto L_0x010a
            int r17 = r17 + -1
            r3 = r23
            r4 = r24
            r5 = r25
            goto L_0x0088
        L_0x010a:
            com.mysql.jdbc.StringUtils$SearchMode r3 = com.mysql.jdbc.StringUtils.SearchMode.ALLOW_BACKSLASH_ESCAPE
            boolean r3 = r6.contains(r3)
            if (r3 == 0) goto L_0x0120
            r3 = 92
            if (r9 != r3) goto L_0x0122
            int r13 = r13 + 1
            r3 = r23
            r4 = r24
            r5 = r25
            goto L_0x0088
        L_0x0120:
            r3 = 92
        L_0x0122:
            r3 = r23
            r4 = r24
            r5 = r25
            goto L_0x0088
        L_0x012a:
            int r3 = r13 + 1
            if (r3 >= r8) goto L_0x0135
            int r3 = r13 + 1
            char r3 = r2.charAt(r3)
            goto L_0x0136
        L_0x0135:
            r3 = 0
        L_0x0136:
            int r4 = r13 + 2
            if (r4 >= r8) goto L_0x0141
            int r4 = r13 + 2
            char r4 = r2.charAt(r4)
            goto L_0x0142
        L_0x0141:
            r4 = 0
        L_0x0142:
            r0 = r4
            r11 = r0
            r10 = r3
            goto L_0x0273
        L_0x0147:
            r18 = r14
            com.mysql.jdbc.StringUtils$SearchMode r0 = com.mysql.jdbc.StringUtils.SearchMode.SKIP_BLOCK_COMMENTS
            boolean r0 = r6.contains(r0)
            r3 = 42
            r4 = 47
            if (r0 == 0) goto L_0x01b7
            if (r9 != r4) goto L_0x01b7
            if (r10 != r3) goto L_0x01b7
            r0 = 33
            if (r11 == r0) goto L_0x017b
            int r13 = r13 + 1
        L_0x015f:
            int r13 = r13 + 1
            if (r13 > r1) goto L_0x0178
            char r0 = r2.charAt(r13)
            if (r0 != r3) goto L_0x0177
            int r0 = r13 + 1
            if (r0 >= r8) goto L_0x0174
            int r0 = r13 + 1
            char r0 = r2.charAt(r0)
            goto L_0x0175
        L_0x0174:
            r0 = 0
        L_0x0175:
            if (r0 == r4) goto L_0x0178
        L_0x0177:
            goto L_0x015f
        L_0x0178:
            int r13 = r13 + 1
            goto L_0x019b
        L_0x017b:
            int r13 = r13 + 1
            int r13 = r13 + 1
            r0 = 1
        L_0x0180:
            r3 = 5
            if (r0 > r3) goto L_0x0197
            int r4 = r13 + r0
            if (r4 >= r8) goto L_0x0197
            int r4 = r13 + r0
            char r4 = r2.charAt(r4)
            boolean r4 = java.lang.Character.isDigit(r4)
            if (r4 != 0) goto L_0x0194
            goto L_0x0197
        L_0x0194:
            int r0 = r0 + 1
            goto L_0x0180
        L_0x0197:
            if (r0 != r3) goto L_0x019b
            int r13 = r13 + 5
        L_0x019b:
            int r0 = r13 + 1
            if (r0 >= r8) goto L_0x01a6
            int r0 = r13 + 1
            char r0 = r2.charAt(r0)
            goto L_0x01a7
        L_0x01a6:
            r0 = 0
        L_0x01a7:
            int r3 = r13 + 2
            if (r3 >= r8) goto L_0x01b2
            int r3 = r13 + 2
            char r3 = r2.charAt(r3)
            goto L_0x01b3
        L_0x01b2:
            r3 = 0
        L_0x01b3:
            r10 = r0
            r11 = r3
            goto L_0x0273
        L_0x01b7:
            com.mysql.jdbc.StringUtils$SearchMode r0 = com.mysql.jdbc.StringUtils.SearchMode.SKIP_BLOCK_COMMENTS
            boolean r0 = r6.contains(r0)
            if (r0 == 0) goto L_0x01d6
            if (r9 != r3) goto L_0x01d6
            if (r10 != r4) goto L_0x01d6
            int r13 = r13 + 1
            r0 = r11
            int r3 = r13 + 2
            if (r3 >= r8) goto L_0x01d1
            int r3 = r13 + 2
            char r3 = r2.charAt(r3)
            goto L_0x01d2
        L_0x01d1:
            r3 = 0
        L_0x01d2:
            r10 = r0
            r11 = r3
            goto L_0x0273
        L_0x01d6:
            com.mysql.jdbc.StringUtils$SearchMode r0 = com.mysql.jdbc.StringUtils.SearchMode.SKIP_LINE_COMMENTS
            boolean r0 = r6.contains(r0)
            if (r0 == 0) goto L_0x0262
            r0 = 45
            if (r9 != r0) goto L_0x01fb
            if (r10 != r0) goto L_0x01fb
            boolean r0 = java.lang.Character.isWhitespace(r11)
            if (r0 != 0) goto L_0x01f8
            r0 = 59
            if (r11 != r0) goto L_0x01f1
            r0 = r16
            goto L_0x01f2
        L_0x01f1:
            r0 = 0
        L_0x01f2:
            r14 = r0
            if (r0 != 0) goto L_0x0201
            if (r11 == 0) goto L_0x0201
            goto L_0x01fd
        L_0x01f8:
            r14 = r18
            goto L_0x0201
        L_0x01fb:
            r14 = r18
        L_0x01fd:
            r0 = 35
            if (r9 != r0) goto L_0x0264
        L_0x0201:
            if (r14 == 0) goto L_0x0222
            int r13 = r13 + 1
            int r13 = r13 + 1
            int r0 = r13 + 1
            if (r0 >= r8) goto L_0x0212
            int r0 = r13 + 1
            char r0 = r2.charAt(r0)
            goto L_0x0213
        L_0x0212:
            r0 = 0
        L_0x0213:
            int r3 = r13 + 2
            if (r3 >= r8) goto L_0x021e
            int r3 = r13 + 2
            char r3 = r2.charAt(r3)
            goto L_0x021f
        L_0x021e:
            r3 = 0
        L_0x021f:
            r10 = r0
            r11 = r3
            goto L_0x0273
        L_0x0222:
            int r13 = r13 + 1
            r0 = 13
            r3 = 10
            if (r13 > r1) goto L_0x0234
            char r4 = r2.charAt(r13)
            r9 = r4
            if (r4 == r3) goto L_0x0234
            if (r9 == r0) goto L_0x0234
            goto L_0x0222
        L_0x0234:
            int r4 = r13 + 1
            if (r4 >= r8) goto L_0x023f
            int r4 = r13 + 1
            char r4 = r2.charAt(r4)
            goto L_0x0240
        L_0x023f:
            r4 = 0
        L_0x0240:
            if (r9 != r0) goto L_0x0253
            if (r4 != r3) goto L_0x0253
            int r13 = r13 + 1
            int r0 = r13 + 1
            if (r0 >= r8) goto L_0x0251
            int r0 = r13 + 1
            char r0 = r2.charAt(r0)
            goto L_0x0252
        L_0x0251:
            r0 = 0
        L_0x0252:
            r4 = r0
        L_0x0253:
            int r0 = r13 + 2
            if (r0 >= r8) goto L_0x025e
            int r0 = r13 + 2
            char r0 = r2.charAt(r0)
            goto L_0x025f
        L_0x025e:
            r0 = 0
        L_0x025f:
            r11 = r0
            r10 = r4
            goto L_0x0273
        L_0x0262:
            r14 = r18
        L_0x0264:
            com.mysql.jdbc.StringUtils$SearchMode r0 = com.mysql.jdbc.StringUtils.SearchMode.SKIP_WHITE_SPACE
            boolean r0 = r6.contains(r0)
            if (r0 == 0) goto L_0x0280
            boolean r0 = java.lang.Character.isWhitespace(r9)
            if (r0 != 0) goto L_0x0273
            goto L_0x0280
        L_0x0273:
            int r13 = r13 + 1
            r0 = r20
            r3 = r23
            r4 = r24
            r5 = r25
            r7 = -1
            goto L_0x002c
        L_0x0280:
            return r13
        L_0x0281:
            r0 = -1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.StringUtils.indexOfNextChar(int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Set):int");
    }

    private static boolean isCharAtPosNotEqualIgnoreCase(String searchIn, int pos, char firstCharOfSearchForUc, char firstCharOfSearchForLc) {
        return (Character.toLowerCase(searchIn.charAt(pos)) == firstCharOfSearchForLc || Character.toUpperCase(searchIn.charAt(pos)) == firstCharOfSearchForUc) ? false : true;
    }

    private static boolean isCharEqualIgnoreCase(char charToCompare, char compareToCharUC, char compareToCharLC) {
        return Character.toLowerCase(charToCompare) == compareToCharLC || Character.toUpperCase(charToCompare) == compareToCharUC;
    }

    public static List<String> split(String stringToSplit, String delimiter, boolean trim) {
        if (stringToSplit == null) {
            return new ArrayList();
        }
        if (delimiter != null) {
            StringTokenizer tokenizer = new StringTokenizer(stringToSplit, delimiter, false);
            List<String> splitTokens = new ArrayList<>(tokenizer.countTokens());
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (trim) {
                    token = token.trim();
                }
                splitTokens.add(token);
            }
            return splitTokens;
        }
        throw new IllegalArgumentException();
    }

    public static List<String> split(String stringToSplit, String delimiter, String openingMarkers, String closingMarkers, boolean trim) {
        return split(stringToSplit, delimiter, openingMarkers, closingMarkers, "", trim);
    }

    public static List<String> split(String stringToSplit, String delimiter, String openingMarkers, String closingMarkers, String overridingMarkers, boolean trim) {
        if (stringToSplit == null) {
            return new ArrayList();
        }
        if (delimiter != null) {
            int currentPos = 0;
            List<String> splitTokens = new ArrayList<>();
            while (true) {
                int indexOfIgnoreCase = indexOfIgnoreCase(currentPos, stringToSplit, delimiter, openingMarkers, closingMarkers, overridingMarkers, SEARCH_MODE__MRK_COM_WS);
                int delimPos = indexOfIgnoreCase;
                if (indexOfIgnoreCase == -1) {
                    break;
                }
                String token = stringToSplit.substring(currentPos, delimPos);
                if (trim) {
                    token = token.trim();
                }
                splitTokens.add(token);
                currentPos = delimPos + 1;
            }
            if (currentPos < stringToSplit.length()) {
                String token2 = stringToSplit.substring(currentPos);
                if (trim) {
                    token2 = token2.trim();
                }
                splitTokens.add(token2);
            }
            return splitTokens;
        }
        throw new IllegalArgumentException();
    }

    private static boolean startsWith(byte[] dataFrom, String chars) {
        int charsLength = chars.length();
        if (dataFrom.length < charsLength) {
            return false;
        }
        for (int i = 0; i < charsLength; i++) {
            if (dataFrom[i] != chars.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public static boolean startsWithIgnoreCase(String searchIn, int startAt, String searchFor) {
        return searchIn.regionMatches(true, startAt, searchFor, 0, searchFor.length());
    }

    public static boolean startsWithIgnoreCase(String searchIn, String searchFor) {
        return startsWithIgnoreCase(searchIn, 0, searchFor);
    }

    public static boolean startsWithIgnoreCaseAndNonAlphaNumeric(String searchIn, String searchFor) {
        if (searchIn == null) {
            return searchFor == null;
        }
        int beginPos = 0;
        int inLength = searchIn.length();
        while (beginPos < inLength && !Character.isLetterOrDigit(searchIn.charAt(beginPos))) {
            beginPos++;
        }
        return startsWithIgnoreCase(searchIn, beginPos, searchFor);
    }

    public static boolean startsWithIgnoreCaseAndWs(String searchIn, String searchFor) {
        return startsWithIgnoreCaseAndWs(searchIn, searchFor, 0);
    }

    public static boolean startsWithIgnoreCaseAndWs(String searchIn, String searchFor, int beginPos) {
        if (searchIn == null) {
            return searchFor == null;
        }
        int inLength = searchIn.length();
        while (beginPos < inLength && Character.isWhitespace(searchIn.charAt(beginPos))) {
            beginPos++;
        }
        return startsWithIgnoreCase(searchIn, beginPos, searchFor);
    }

    public static int startsWithIgnoreCaseAndWs(String searchIn, String[] searchFor) {
        for (int i = 0; i < searchFor.length; i++) {
            if (startsWithIgnoreCaseAndWs(searchIn, searchFor[i], 0)) {
                return i;
            }
        }
        return -1;
    }

    public static byte[] stripEnclosure(byte[] source, String prefix, String suffix) {
        if (source.length < prefix.length() + suffix.length() || !startsWith(source, prefix) || !endsWith(source, suffix)) {
            return source;
        }
        byte[] enclosed = new byte[(source.length - (prefix.length() + suffix.length()))];
        System.arraycopy(source, prefix.length(), enclosed, 0, enclosed.length);
        return enclosed;
    }

    public static String toAsciiString(byte[] buffer) {
        return toAsciiString(buffer, 0, buffer.length);
    }

    public static String toAsciiString(byte[] buffer, int startPos, int length) {
        char[] charArray = new char[length];
        int readpoint = startPos;
        for (int i = 0; i < length; i++) {
            charArray[i] = (char) buffer[readpoint];
            readpoint++;
        }
        return new String(charArray);
    }

    public static boolean wildCompareIgnoreCase(String searchIn, String searchFor) {
        return wildCompareInternal(searchIn, searchFor) == 0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x006c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int wildCompareInternal(java.lang.String r12, java.lang.String r13) {
        /*
            r0 = -1
            if (r12 == 0) goto L_0x00e1
            if (r13 != 0) goto L_0x0007
            goto L_0x00e1
        L_0x0007:
            java.lang.String r1 = "%"
            boolean r1 = r13.equals(r1)
            r2 = 0
            if (r1 == 0) goto L_0x0011
            return r2
        L_0x0011:
            r1 = 0
            int r3 = r13.length()
            r4 = 0
            int r5 = r12.length()
            r6 = -1
        L_0x001c:
            r7 = 1
            if (r1 == r3) goto L_0x00dd
        L_0x001f:
            char r8 = r13.charAt(r1)
            r9 = 92
            r10 = 37
            r11 = 95
            if (r8 == r10) goto L_0x0063
            char r8 = r13.charAt(r1)
            if (r8 == r11) goto L_0x0063
            char r8 = r13.charAt(r1)
            if (r8 != r9) goto L_0x003d
            int r8 = r1 + 1
            if (r8 == r3) goto L_0x003d
            int r1 = r1 + 1
        L_0x003d:
            if (r4 == r5) goto L_0x0062
            int r8 = r1 + 1
            char r1 = r13.charAt(r1)
            char r1 = java.lang.Character.toUpperCase(r1)
            int r9 = r4 + 1
            char r4 = r12.charAt(r4)
            char r4 = java.lang.Character.toUpperCase(r4)
            if (r1 == r4) goto L_0x0058
            r1 = r8
            r4 = r9
            goto L_0x0062
        L_0x0058:
            if (r8 != r3) goto L_0x005e
            if (r9 == r5) goto L_0x005d
            r2 = r7
        L_0x005d:
            return r2
        L_0x005e:
            r6 = 1
            r1 = r8
            r4 = r9
            goto L_0x001f
        L_0x0062:
            return r7
        L_0x0063:
            char r8 = r13.charAt(r1)
            if (r8 != r11) goto L_0x007b
        L_0x0069:
            if (r4 != r5) goto L_0x006c
            return r6
        L_0x006c:
            int r4 = r4 + 1
            int r1 = r1 + 1
            if (r1 >= r3) goto L_0x0078
            char r8 = r13.charAt(r1)
            if (r8 == r11) goto L_0x0069
        L_0x0078:
            if (r1 != r3) goto L_0x007b
            goto L_0x00dd
        L_0x007b:
            char r8 = r13.charAt(r1)
            if (r8 != r10) goto L_0x001c
            int r1 = r1 + r7
        L_0x0082:
            if (r1 == r3) goto L_0x0099
            char r8 = r13.charAt(r1)
            if (r8 != r10) goto L_0x008b
            goto L_0x0096
        L_0x008b:
            char r8 = r13.charAt(r1)
            if (r8 != r11) goto L_0x0099
            if (r4 != r5) goto L_0x0094
            return r0
        L_0x0094:
            int r4 = r4 + 1
        L_0x0096:
            int r1 = r1 + 1
            goto L_0x0082
        L_0x0099:
            if (r1 != r3) goto L_0x009c
            return r2
        L_0x009c:
            if (r4 != r5) goto L_0x009f
            return r0
        L_0x009f:
            char r2 = r13.charAt(r1)
            r8 = r2
            if (r2 != r9) goto L_0x00b0
            int r2 = r1 + 1
            if (r2 == r3) goto L_0x00b0
            int r1 = r1 + 1
            char r8 = r13.charAt(r1)
        L_0x00b0:
            int r7 = r7 + r1
        L_0x00b1:
            if (r4 == r5) goto L_0x00c4
            char r1 = r12.charAt(r4)
            char r1 = java.lang.Character.toUpperCase(r1)
            char r2 = java.lang.Character.toUpperCase(r8)
            if (r1 == r2) goto L_0x00c4
            int r4 = r4 + 1
            goto L_0x00b1
        L_0x00c4:
            int r1 = r4 + 1
            if (r4 != r5) goto L_0x00c9
            return r0
        L_0x00c9:
            java.lang.String r2 = r12.substring(r1)
            java.lang.String r4 = r13.substring(r7)
            int r2 = wildCompareInternal(r2, r4)
            if (r2 > 0) goto L_0x00d8
            return r2
        L_0x00d8:
            if (r1 != r5) goto L_0x00db
            return r0
        L_0x00db:
            r4 = r1
            goto L_0x00b1
        L_0x00dd:
            if (r4 == r5) goto L_0x00e0
            r2 = r7
        L_0x00e0:
            return r2
        L_0x00e1:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.StringUtils.wildCompareInternal(java.lang.String, java.lang.String):int");
    }

    static byte[] s2b(String s, MySQLConnection conn) throws SQLException {
        if (s == null) {
            return null;
        }
        if (conn == null || !conn.getUseUnicode()) {
            return s.getBytes();
        }
        try {
            String encoding = conn.getEncoding();
            if (encoding == null) {
                return s.getBytes();
            }
            SingleByteCharsetConverter converter = conn.getCharsetConverter(encoding);
            if (converter != null) {
                return converter.toBytes(s);
            }
            return s.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            return s.getBytes();
        }
    }

    public static int lastIndexOf(byte[] s, char c) {
        if (s == null) {
            return -1;
        }
        for (int i = s.length - 1; i >= 0; i--) {
            if (s[i] == c) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(byte[] s, char c) {
        if (s == null) {
            return -1;
        }
        int length = s.length;
        for (int i = 0; i < length; i++) {
            if (s[i] == c) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isNullOrEmpty(String toTest) {
        return toTest == null || toTest.length() == 0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:69:0x00c2 A[Catch:{ IOException -> 0x00e4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x00d6 A[Catch:{ IOException -> 0x00e4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x001f A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String stripComments(java.lang.String r16, java.lang.String r17, java.lang.String r18, boolean r19, boolean r20, boolean r21, boolean r22) {
        /*
            r1 = r16
            if (r1 != 0) goto L_0x0006
            r0 = 0
            return r0
        L_0x0006:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            int r2 = r16.length()
            r0.<init>(r2)
            r2 = r0
            java.io.StringReader r0 = new java.io.StringReader
            r0.<init>(r1)
            r3 = r0
            r0 = 0
            r4 = 0
            r5 = -1
            r6 = 0
            r7 = 0
            r8 = r7
            r7 = r6
            r6 = r5
            r5 = r0
        L_0x001f:
            int r0 = r3.read()     // Catch:{ IOException -> 0x00eb }
            r8 = r0
            r9 = -1
            if (r0 == r9) goto L_0x00e6
            if (r6 == r9) goto L_0x003f
            r10 = r18
            char r0 = r10.charAt(r6)     // Catch:{ IOException -> 0x003a }
            if (r8 != r0) goto L_0x0041
            if (r4 != 0) goto L_0x0041
            r0 = 0
            r5 = -1
            r11 = r17
            r6 = r5
            r5 = r0
            goto L_0x0051
        L_0x003a:
            r0 = move-exception
            r11 = r17
            goto L_0x00f0
        L_0x003f:
            r10 = r18
        L_0x0041:
            r11 = r17
            int r0 = r11.indexOf(r8)     // Catch:{ IOException -> 0x00e4 }
            r7 = r0
            if (r0 == r9) goto L_0x0051
            if (r4 != 0) goto L_0x0051
            if (r5 != 0) goto L_0x0051
            r0 = r7
            r5 = r8
            r6 = r0
        L_0x0051:
            r0 = 13
            r12 = 10
            if (r5 != 0) goto L_0x009f
            r13 = 47
            if (r8 != r13) goto L_0x009f
            if (r20 != 0) goto L_0x005f
            if (r19 == 0) goto L_0x009f
        L_0x005f:
            int r14 = r3.read()     // Catch:{ IOException -> 0x00e4 }
            r8 = r14
            r14 = 42
            if (r8 != r14) goto L_0x008f
            if (r19 == 0) goto L_0x008f
            r9 = 0
        L_0x006b:
            int r15 = r3.read()     // Catch:{ IOException -> 0x00e4 }
            r8 = r15
            if (r15 != r13) goto L_0x0074
            if (r9 == r14) goto L_0x001f
        L_0x0074:
            if (r8 != r0) goto L_0x0083
            int r15 = r3.read()     // Catch:{ IOException -> 0x00e4 }
            r8 = r15
            if (r8 != r12) goto L_0x008a
            int r15 = r3.read()     // Catch:{ IOException -> 0x00e4 }
            r8 = r15
            goto L_0x008a
        L_0x0083:
            if (r8 != r12) goto L_0x008a
            int r15 = r3.read()     // Catch:{ IOException -> 0x00e4 }
            r8 = r15
        L_0x008a:
            if (r8 >= 0) goto L_0x008d
            goto L_0x001f
        L_0x008d:
            r9 = r8
            goto L_0x006b
        L_0x008f:
            if (r8 != r13) goto L_0x00dc
            if (r20 == 0) goto L_0x00dc
        L_0x0093:
            int r13 = r3.read()     // Catch:{ IOException -> 0x00e4 }
            r8 = r13
            if (r13 == r12) goto L_0x00dc
            if (r8 == r0) goto L_0x00dc
            if (r8 < 0) goto L_0x00dc
            goto L_0x0093
        L_0x009f:
            if (r5 != 0) goto L_0x00b3
            r13 = 35
            if (r8 != r13) goto L_0x00b3
            if (r21 == 0) goto L_0x00b3
        L_0x00a7:
            int r13 = r3.read()     // Catch:{ IOException -> 0x00e4 }
            r8 = r13
            if (r13 == r12) goto L_0x00dc
            if (r8 == r0) goto L_0x00dc
            if (r8 < 0) goto L_0x00dc
            goto L_0x00a7
        L_0x00b3:
            if (r5 != 0) goto L_0x00dc
            r13 = 45
            if (r8 != r13) goto L_0x00dc
            if (r22 == 0) goto L_0x00dc
            int r14 = r3.read()     // Catch:{ IOException -> 0x00e4 }
            r8 = r14
            if (r8 == r9) goto L_0x00d1
            if (r8 == r13) goto L_0x00c5
            goto L_0x00d1
        L_0x00c5:
            int r13 = r3.read()     // Catch:{ IOException -> 0x00e4 }
            r8 = r13
            if (r13 == r12) goto L_0x00dc
            if (r8 == r0) goto L_0x00dc
            if (r8 < 0) goto L_0x00dc
            goto L_0x00c5
        L_0x00d1:
            r2.append(r13)     // Catch:{ IOException -> 0x00e4 }
            if (r8 == r9) goto L_0x001f
            char r0 = (char) r8     // Catch:{ IOException -> 0x00e4 }
            r2.append(r0)     // Catch:{ IOException -> 0x00e4 }
            goto L_0x001f
        L_0x00dc:
            if (r8 == r9) goto L_0x001f
            char r0 = (char) r8     // Catch:{ IOException -> 0x00e4 }
            r2.append(r0)     // Catch:{ IOException -> 0x00e4 }
            goto L_0x001f
        L_0x00e4:
            r0 = move-exception
            goto L_0x00f0
        L_0x00e6:
            r11 = r17
            r10 = r18
            goto L_0x00f0
        L_0x00eb:
            r0 = move-exception
            r11 = r17
            r10 = r18
        L_0x00f0:
            java.lang.String r0 = r2.toString()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.StringUtils.stripComments(java.lang.String, java.lang.String, java.lang.String, boolean, boolean, boolean, boolean):java.lang.String");
    }

    public static String sanitizeProcOrFuncName(String src) {
        if (src == null || src.equals("%")) {
            return null;
        }
        return src;
    }

    public static List<String> splitDBdotName(String source, String catalog, String quoteId, boolean isNoBslashEscSet) {
        int dotIndex;
        String entityName;
        if (source == null || source.equals("%")) {
            return Collections.emptyList();
        }
        if (" ".equals(quoteId)) {
            dotIndex = source.indexOf(".");
        } else {
            dotIndex = indexOfIgnoreCase(0, source, ".", quoteId, quoteId, isNoBslashEscSet ? SEARCH_MODE__MRK_WS : SEARCH_MODE__BSESC_MRK_WS);
        }
        String database = catalog;
        if (dotIndex != -1) {
            database = unQuoteIdentifier(source.substring(0, dotIndex), quoteId);
            entityName = unQuoteIdentifier(source.substring(dotIndex + 1), quoteId);
        } else {
            entityName = unQuoteIdentifier(source, quoteId);
        }
        return Arrays.asList(new String[]{database, entityName});
    }

    public static boolean isEmptyOrWhitespaceOnly(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String escapeQuote(String src, String quotChar) {
        if (src == null) {
            return null;
        }
        String src2 = toString(stripEnclosure(src.getBytes(), quotChar, quotChar));
        int lastNdx = src2.indexOf(quotChar);
        String tmpSrc = src2.substring(0, lastNdx) + quotChar + quotChar;
        String tmpRest = src2.substring(lastNdx + 1, src2.length());
        int lastNdx2 = tmpRest.indexOf(quotChar);
        while (lastNdx2 > -1) {
            tmpSrc = (tmpSrc + tmpRest.substring(0, lastNdx2)) + quotChar + quotChar;
            tmpRest = tmpRest.substring(lastNdx2 + 1, tmpRest.length());
            lastNdx2 = tmpRest.indexOf(quotChar);
        }
        return tmpSrc + tmpRest;
    }

    public static String quoteIdentifier(String identifier, String quoteChar, boolean isPedantic) {
        if (identifier == null) {
            return null;
        }
        String identifier2 = identifier.trim();
        int quoteCharLength = quoteChar.length();
        if (quoteCharLength == 0 || " ".equals(quoteChar)) {
            return identifier2;
        }
        if (!isPedantic && identifier2.startsWith(quoteChar) && identifier2.endsWith(quoteChar)) {
            String identifierQuoteTrimmed = identifier2.substring(quoteCharLength, identifier2.length() - quoteCharLength);
            int quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar);
            while (quoteCharPos >= 0) {
                int quoteCharNextExpectedPos = quoteCharPos + quoteCharLength;
                int quoteCharNextPosition = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextExpectedPos);
                if (quoteCharNextPosition != quoteCharNextExpectedPos) {
                    break;
                }
                quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextPosition + quoteCharLength);
            }
            if (quoteCharPos < 0) {
                return identifier2;
            }
        }
        return quoteChar + identifier2.replaceAll(quoteChar, quoteChar + quoteChar) + quoteChar;
    }

    public static String quoteIdentifier(String identifier, boolean isPedantic) {
        return quoteIdentifier(identifier, "`", isPedantic);
    }

    public static String unQuoteIdentifier(String identifier, String quoteChar) {
        if (identifier == null) {
            return null;
        }
        String identifier2 = identifier.trim();
        int quoteCharLength = quoteChar.length();
        if (quoteCharLength == 0 || " ".equals(quoteChar) || !identifier2.startsWith(quoteChar) || !identifier2.endsWith(quoteChar)) {
            return identifier2;
        }
        String identifierQuoteTrimmed = identifier2.substring(quoteCharLength, identifier2.length() - quoteCharLength);
        int quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar);
        while (quoteCharPos >= 0) {
            int quoteCharNextExpectedPos = quoteCharPos + quoteCharLength;
            int quoteCharNextPosition = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextExpectedPos);
            if (quoteCharNextPosition != quoteCharNextExpectedPos) {
                return identifier2;
            }
            quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextPosition + quoteCharLength);
        }
        return identifier2.substring(quoteCharLength, identifier2.length() - quoteCharLength).replaceAll(quoteChar + quoteChar, quoteChar);
    }

    public static int indexOfQuoteDoubleAware(String searchIn, String quoteChar, int startFrom) {
        if (searchIn == null || quoteChar == null || quoteChar.length() == 0 || startFrom > searchIn.length()) {
            return -1;
        }
        int lastIndex = searchIn.length() - 1;
        int beginPos = startFrom;
        int pos = -1;
        boolean next = true;
        while (next) {
            pos = searchIn.indexOf(quoteChar, beginPos);
            if (pos == -1 || pos == lastIndex || !searchIn.startsWith(quoteChar, pos + 1)) {
                next = false;
            } else {
                beginPos = pos + 2;
            }
        }
        return pos;
    }

    public static String toString(byte[] value, int offset, int length, String encoding) throws UnsupportedEncodingException {
        return findCharset(encoding).decode(ByteBuffer.wrap(value, offset, length)).toString();
    }

    public static String toString(byte[] value, String encoding) throws UnsupportedEncodingException {
        return findCharset(encoding).decode(ByteBuffer.wrap(value)).toString();
    }

    public static String toString(byte[] value, int offset, int length) {
        try {
            return findCharset(platformEncoding).decode(ByteBuffer.wrap(value, offset, length)).toString();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String toString(byte[] value) {
        try {
            return findCharset(platformEncoding).decode(ByteBuffer.wrap(value)).toString();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static byte[] getBytes(char[] value) {
        try {
            return getBytes(value, 0, value.length, platformEncoding);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static byte[] getBytes(char[] value, int offset, int length) {
        try {
            return getBytes(value, offset, length, platformEncoding);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static byte[] getBytes(char[] value, String encoding) throws UnsupportedEncodingException {
        return getBytes(value, 0, value.length, encoding);
    }

    public static byte[] getBytes(char[] value, int offset, int length, String encoding) throws UnsupportedEncodingException {
        ByteBuffer buf = findCharset(encoding).encode(CharBuffer.wrap(value, offset, length));
        int encodedLen = buf.limit();
        byte[] asBytes = new byte[encodedLen];
        buf.get(asBytes, 0, encodedLen);
        return asBytes;
    }

    public static byte[] getBytes(String value) {
        try {
            return getBytes(value, 0, value.length(), platformEncoding);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static byte[] getBytes(String value, int offset, int length) {
        try {
            return getBytes(value, offset, length, platformEncoding);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static byte[] getBytes(String value, String encoding) throws UnsupportedEncodingException {
        return getBytes(value, 0, value.length(), encoding);
    }

    public static byte[] getBytes(String value, int offset, int length, String encoding) throws UnsupportedEncodingException {
        if (Util.isJdbc4()) {
            ByteBuffer buf = findCharset(encoding).encode(CharBuffer.wrap(value.toCharArray(), offset, length));
            int encodedLen = buf.limit();
            byte[] asBytes = new byte[encodedLen];
            buf.get(asBytes, 0, encodedLen);
            return asBytes;
        } else if (offset == 0 && length == value.length()) {
            return value.getBytes(encoding);
        } else {
            return value.substring(offset, offset + length).getBytes(encoding);
        }
    }

    public static final boolean isValidIdChar(char c) {
        return VALID_ID_CHARS.indexOf(c) != -1;
    }

    public static void appendAsHex(StringBuilder builder, byte[] bytes) {
        builder.append("0x");
        for (byte b : bytes) {
            char[] cArr = HEX_DIGITS;
            builder.append(cArr[(b >>> 4) & 15]).append(cArr[b & 15]);
        }
    }

    public static void appendAsHex(StringBuilder builder, int value) {
        if (value == 0) {
            builder.append("0x0");
            return;
        }
        int shift = 32;
        boolean nonZeroFound = false;
        builder.append("0x");
        do {
            shift -= 4;
            byte nibble = (byte) ((value >>> shift) & 15);
            if (nonZeroFound) {
                builder.append(HEX_DIGITS[nibble]);
                continue;
            } else if (nibble != 0) {
                builder.append(HEX_DIGITS[nibble]);
                nonZeroFound = true;
                continue;
            } else {
                continue;
            }
        } while (shift != 0);
    }

    public static byte[] getBytesNullTerminated(String value, String encoding) throws UnsupportedEncodingException {
        ByteBuffer buf = findCharset(encoding).encode(value);
        int encodedLen = buf.limit();
        byte[] asBytes = new byte[(encodedLen + 1)];
        buf.get(asBytes, 0, encodedLen);
        asBytes[encodedLen] = 0;
        return asBytes;
    }

    public static boolean isStrictlyNumeric(CharSequence cs) {
        if (cs == null || cs.length() == 0) {
            return false;
        }
        for (int i = 0; i < cs.length(); i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
