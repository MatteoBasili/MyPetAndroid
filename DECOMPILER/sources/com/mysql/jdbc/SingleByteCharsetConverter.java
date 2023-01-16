package com.mysql.jdbc;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import kotlin.jvm.internal.ByteCompanionObject;

public class SingleByteCharsetConverter {
    private static final int BYTE_RANGE = 256;
    private static final Map<String, SingleByteCharsetConverter> CONVERTER_MAP = new HashMap();
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static byte[] allBytes = new byte[256];
    private static byte[] unknownCharsMap = new byte[65536];
    private char[] byteToChars = new char[256];
    private byte[] charToByteMap = new byte[65536];

    static {
        for (int i = -128; i <= 127; i++) {
            allBytes[i + 128] = (byte) i;
        }
        int i2 = 0;
        while (true) {
            byte[] bArr = unknownCharsMap;
            if (i2 < bArr.length) {
                bArr[i2] = 63;
                i2++;
            } else {
                return;
            }
        }
    }

    public static synchronized SingleByteCharsetConverter getInstance(String encodingName, Connection conn) throws UnsupportedEncodingException, SQLException {
        SingleByteCharsetConverter instance;
        synchronized (SingleByteCharsetConverter.class) {
            instance = CONVERTER_MAP.get(encodingName);
            if (instance == null) {
                instance = initCharset(encodingName);
            }
        }
        return instance;
    }

    public static SingleByteCharsetConverter initCharset(String javaEncodingName) throws UnsupportedEncodingException, SQLException {
        try {
            if (CharsetMapping.isMultibyteCharset(javaEncodingName)) {
                return null;
            }
            SingleByteCharsetConverter converter = new SingleByteCharsetConverter(javaEncodingName);
            CONVERTER_MAP.put(javaEncodingName, converter);
            return converter;
        } catch (RuntimeException ex) {
            SQLException sqlEx = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
            sqlEx.initCause(ex);
            throw sqlEx;
        }
    }

    public static String toStringDefaultEncoding(byte[] buffer, int startPos, int length) {
        return new String(buffer, startPos, length);
    }

    private SingleByteCharsetConverter(String encodingName) throws UnsupportedEncodingException {
        String allBytesString = new String(allBytes, 0, 256, encodingName);
        int allBytesLen = allBytesString.length();
        byte[] bArr = unknownCharsMap;
        byte[] bArr2 = this.charToByteMap;
        System.arraycopy(bArr, 0, bArr2, 0, bArr2.length);
        int i = 0;
        while (i < 256 && i < allBytesLen) {
            char c = allBytesString.charAt(i);
            this.byteToChars[i] = c;
            this.charToByteMap[c] = allBytes[i];
            i++;
        }
    }

    public final byte[] toBytes(char[] c) {
        if (c == null) {
            return null;
        }
        int length = c.length;
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = this.charToByteMap[c[i]];
        }
        return bytes;
    }

    public final byte[] toBytesWrapped(char[] c, char beginWrap, char endWrap) {
        if (c == null) {
            return null;
        }
        int length = c.length + 2;
        int charLength = c.length;
        byte[] bytes = new byte[length];
        bytes[0] = this.charToByteMap[beginWrap];
        for (int i = 0; i < charLength; i++) {
            bytes[i + 1] = this.charToByteMap[c[i]];
        }
        bytes[length - 1] = this.charToByteMap[endWrap];
        return bytes;
    }

    public final byte[] toBytes(char[] chars, int offset, int length) {
        if (chars == null) {
            return null;
        }
        if (length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = this.charToByteMap[chars[i + offset]];
        }
        return bytes;
    }

    public final byte[] toBytes(String s) {
        if (s == null) {
            return null;
        }
        int length = s.length();
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = this.charToByteMap[s.charAt(i)];
        }
        return bytes;
    }

    public final byte[] toBytesWrapped(String s, char beginWrap, char endWrap) {
        if (s == null) {
            return null;
        }
        int stringLength = s.length();
        int length = stringLength + 2;
        byte[] bytes = new byte[length];
        bytes[0] = this.charToByteMap[beginWrap];
        for (int i = 0; i < stringLength; i++) {
            bytes[i + 1] = this.charToByteMap[s.charAt(i)];
        }
        bytes[length - 1] = this.charToByteMap[endWrap];
        return bytes;
    }

    public final byte[] toBytes(String s, int offset, int length) {
        if (s == null) {
            return null;
        }
        if (length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = this.charToByteMap[s.charAt(i + offset)];
        }
        return bytes;
    }

    public final String toString(byte[] buffer) {
        return toString(buffer, 0, buffer.length);
    }

    public final String toString(byte[] buffer, int startPos, int length) {
        char[] charArray = new char[length];
        int readpoint = startPos;
        for (int i = 0; i < length; i++) {
            charArray[i] = this.byteToChars[buffer[readpoint] + ByteCompanionObject.MIN_VALUE];
            readpoint++;
        }
        return new String(charArray);
    }
}
