package com.mysql.jdbc;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.sql.SQLException;

public class Buffer {
    static final int MAX_BYTES_TO_DUMP = 512;
    static final int NO_LENGTH_LIMIT = -1;
    static final long NULL_LENGTH = -1;
    public static final short TYPE_ID_AUTH_SWITCH = 254;
    public static final short TYPE_ID_EOF = 254;
    public static final short TYPE_ID_ERROR = 255;
    public static final short TYPE_ID_LOCAL_INFILE = 251;
    public static final short TYPE_ID_OK = 0;
    private int bufLength = 0;
    private byte[] byteBuffer;
    private int position = 0;
    protected boolean wasMultiPacket = false;

    public Buffer(byte[] buf) {
        this.byteBuffer = buf;
        setBufLength(buf.length);
    }

    Buffer(int size) {
        byte[] bArr = new byte[size];
        this.byteBuffer = bArr;
        setBufLength(bArr.length);
        this.position = 4;
    }

    /* access modifiers changed from: package-private */
    public final void clear() {
        this.position = 4;
    }

    /* access modifiers changed from: package-private */
    public final void dump() {
        dump(getBufLength());
    }

    /* access modifiers changed from: package-private */
    public final String dump(int numBytes) {
        return StringUtils.dumpAsHex(getBytes(0, numBytes > getBufLength() ? getBufLength() : numBytes), numBytes > getBufLength() ? getBufLength() : numBytes);
    }

    /* access modifiers changed from: package-private */
    public final String dumpClampedBytes(int numBytes) {
        int numBytesToDump = 512;
        if (numBytes < 512) {
            numBytesToDump = numBytes;
        }
        String dumped = StringUtils.dumpAsHex(getBytes(0, numBytesToDump > getBufLength() ? getBufLength() : numBytesToDump), numBytesToDump > getBufLength() ? getBufLength() : numBytesToDump);
        if (numBytesToDump < numBytes) {
            return dumped + " ....(packet exceeds max. dump length)";
        }
        return dumped;
    }

    /* access modifiers changed from: package-private */
    public final void dumpHeader() {
        for (int i = 0; i < 4; i++) {
            String hexVal = Integer.toHexString(readByte(i) & 255);
            if (hexVal.length() == 1) {
                hexVal = "0" + hexVal;
            }
            System.out.print(hexVal + " ");
        }
    }

    /* access modifiers changed from: package-private */
    public final void dumpNBytes(int start, int nBytes) {
        StringBuilder asciiBuf = new StringBuilder();
        int i = start;
        while (i < start + nBytes && i < getBufLength()) {
            String hexVal = Integer.toHexString(readByte(i) & 255);
            if (hexVal.length() == 1) {
                hexVal = "0" + hexVal;
            }
            System.out.print(hexVal + " ");
            if (readByte(i) <= 32 || readByte(i) >= Byte.MAX_VALUE) {
                asciiBuf.append(".");
            } else {
                asciiBuf.append((char) readByte(i));
            }
            asciiBuf.append(" ");
            i++;
        }
        System.out.println("    " + asciiBuf.toString());
    }

    /* access modifiers changed from: package-private */
    public final void ensureCapacity(int additionalData) throws SQLException {
        if (this.position + additionalData > getBufLength()) {
            int i = this.position + additionalData;
            byte[] bArr = this.byteBuffer;
            if (i < bArr.length) {
                setBufLength(bArr.length);
                return;
            }
            int newLength = (int) (((double) bArr.length) * 1.25d);
            if (newLength < bArr.length + additionalData) {
                newLength = bArr.length + ((int) (((double) additionalData) * 1.25d));
            }
            if (newLength < bArr.length) {
                newLength = bArr.length + additionalData;
            }
            byte[] newBytes = new byte[newLength];
            System.arraycopy(bArr, 0, newBytes, 0, bArr.length);
            this.byteBuffer = newBytes;
            setBufLength(newBytes.length);
        }
    }

    public int fastSkipLenString() {
        long len = readFieldLength();
        this.position = (int) (((long) this.position) + len);
        return (int) len;
    }

    public void fastSkipLenByteArray() {
        long len = readFieldLength();
        if (len != -1 && len != 0) {
            this.position = (int) (((long) this.position) + len);
        }
    }

    /* access modifiers changed from: protected */
    public final byte[] getBufferSource() {
        return this.byteBuffer;
    }

    public int getBufLength() {
        return this.bufLength;
    }

    public byte[] getByteBuffer() {
        return this.byteBuffer;
    }

    /* access modifiers changed from: package-private */
    public final byte[] getBytes(int len) {
        byte[] b = new byte[len];
        System.arraycopy(this.byteBuffer, this.position, b, 0, len);
        this.position += len;
        return b;
    }

    /* access modifiers changed from: package-private */
    public byte[] getBytes(int offset, int len) {
        byte[] dest = new byte[len];
        System.arraycopy(this.byteBuffer, offset, dest, 0, len);
        return dest;
    }

    /* access modifiers changed from: package-private */
    public int getCapacity() {
        return this.byteBuffer.length;
    }

    public ByteBuffer getNioBuffer() {
        throw new IllegalArgumentException(Messages.getString("ByteArrayBuffer.0"));
    }

    public int getPosition() {
        return this.position;
    }

    /* access modifiers changed from: package-private */
    public final boolean isEOFPacket() {
        return (this.byteBuffer[0] & 255) == 254 && getBufLength() <= 5;
    }

    /* access modifiers changed from: package-private */
    public final boolean isAuthMethodSwitchRequestPacket() {
        return (this.byteBuffer[0] & 255) == 254;
    }

    /* access modifiers changed from: package-private */
    public final boolean isOKPacket() {
        return (this.byteBuffer[0] & 255) == 0;
    }

    /* access modifiers changed from: package-private */
    public final boolean isResultSetOKPacket() {
        return (this.byteBuffer[0] & 255) == 254 && getBufLength() < 16777215;
    }

    /* access modifiers changed from: package-private */
    public final boolean isRawPacket() {
        return (this.byteBuffer[0] & 255) == 1;
    }

    /* access modifiers changed from: package-private */
    public final long newReadLength() {
        byte[] bArr = this.byteBuffer;
        int i = this.position;
        this.position = i + 1;
        int sw = bArr[i] & 255;
        switch (sw) {
            case 251:
                return 0;
            case MysqlDefs.FIELD_TYPE_BLOB /*252*/:
                return (long) readInt();
            case 253:
                return (long) readLongInt();
            case 254:
                return readLongLong();
            default:
                return (long) sw;
        }
    }

    /* access modifiers changed from: package-private */
    public final byte readByte() {
        byte[] bArr = this.byteBuffer;
        int i = this.position;
        this.position = i + 1;
        return bArr[i];
    }

    /* access modifiers changed from: package-private */
    public final byte readByte(int readAt) {
        return this.byteBuffer[readAt];
    }

    /* access modifiers changed from: package-private */
    public final long readFieldLength() {
        byte[] bArr = this.byteBuffer;
        int i = this.position;
        this.position = i + 1;
        int sw = bArr[i] & 255;
        switch (sw) {
            case 251:
                return -1;
            case MysqlDefs.FIELD_TYPE_BLOB /*252*/:
                return (long) readInt();
            case 253:
                return (long) readLongInt();
            case 254:
                return readLongLong();
            default:
                return (long) sw;
        }
    }

    /* access modifiers changed from: package-private */
    public final int readInt() {
        byte[] b = this.byteBuffer;
        int i = this.position;
        int i2 = i + 1;
        this.position = i2;
        this.position = i2 + 1;
        return (b[i] & 255) | ((b[i2] & 255) << 8);
    }

    /* access modifiers changed from: package-private */
    public final int readIntAsLong() {
        byte[] b = this.byteBuffer;
        int i = this.position;
        int i2 = i + 1;
        this.position = i2;
        int i3 = i2 + 1;
        this.position = i3;
        byte b2 = (b[i] & 255) | ((b[i2] & 255) << 8);
        int i4 = i3 + 1;
        this.position = i4;
        byte b3 = b2 | ((b[i3] & 255) << 16);
        this.position = i4 + 1;
        return b3 | ((b[i4] & 255) << 24);
    }

    /* access modifiers changed from: package-private */
    public final byte[] readLenByteArray(int offset) {
        long len = readFieldLength();
        if (len == -1) {
            return null;
        }
        if (len == 0) {
            return Constants.EMPTY_BYTE_ARRAY;
        }
        this.position += offset;
        return getBytes((int) len);
    }

    /* access modifiers changed from: package-private */
    public final long readLength() {
        byte[] bArr = this.byteBuffer;
        int i = this.position;
        this.position = i + 1;
        int sw = bArr[i] & 255;
        switch (sw) {
            case 251:
                return 0;
            case MysqlDefs.FIELD_TYPE_BLOB /*252*/:
                return (long) readInt();
            case 253:
                return (long) readLongInt();
            case 254:
                return readLong();
            default:
                return (long) sw;
        }
    }

    /* access modifiers changed from: package-private */
    public final long readLong() {
        byte[] b = this.byteBuffer;
        int i = this.position;
        int i2 = i + 1;
        this.position = i2;
        int i3 = i2 + 1;
        this.position = i3;
        int i4 = i3 + 1;
        this.position = i4;
        this.position = i4 + 1;
        return (((long) b[i]) & 255) | ((255 & ((long) b[i2])) << 8) | (((long) (b[i3] & 255)) << 16) | (((long) (b[i4] & 255)) << 24);
    }

    /* access modifiers changed from: package-private */
    public final int readLongInt() {
        byte[] b = this.byteBuffer;
        int i = this.position;
        int i2 = i + 1;
        this.position = i2;
        int i3 = i2 + 1;
        this.position = i3;
        byte b2 = (b[i] & 255) | ((b[i2] & 255) << 8);
        this.position = i3 + 1;
        return b2 | ((b[i3] & 255) << 16);
    }

    /* access modifiers changed from: package-private */
    public final long readLongLong() {
        byte[] b = this.byteBuffer;
        int i = this.position;
        int i2 = i + 1;
        this.position = i2;
        long j = (long) (b[i] & 255);
        int i3 = i2 + 1;
        this.position = i3;
        int i4 = i3 + 1;
        this.position = i4;
        int i5 = i4 + 1;
        this.position = i5;
        long j2 = j | (((long) (b[i2] & 255)) << 8) | (((long) (b[i3] & 255)) << 16) | (((long) (b[i4] & 255)) << 24);
        int i6 = i5 + 1;
        this.position = i6;
        int i7 = i6 + 1;
        this.position = i7;
        long j3 = j2 | (((long) (b[i5] & 255)) << 32) | (((long) (b[i6] & 255)) << 40);
        int i8 = i7 + 1;
        this.position = i8;
        this.position = i8 + 1;
        return j3 | (((long) (b[i7] & 255)) << 48) | (((long) (b[i8] & 255)) << 56);
    }

    /* access modifiers changed from: package-private */
    public final int readnBytes() {
        byte[] bArr = this.byteBuffer;
        int i = this.position;
        int i2 = i + 1;
        this.position = i2;
        switch (bArr[i] & 255) {
            case 1:
                this.position = i2 + 1;
                return bArr[i2] & 255;
            case 2:
                return readInt();
            case 3:
                return readLongInt();
            case 4:
                return (int) readLong();
            default:
                return 255;
        }
    }

    public final String readString() {
        int i = this.position;
        int len = 0;
        int maxLen = getBufLength();
        while (i < maxLen && this.byteBuffer[i] != 0) {
            len++;
            i++;
        }
        String s = StringUtils.toString(this.byteBuffer, this.position, len);
        this.position += len + 1;
        return s;
    }

    /* access modifiers changed from: package-private */
    public final String readString(String encoding, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        int i = this.position;
        int len = 0;
        int maxLen = getBufLength();
        while (i < maxLen && this.byteBuffer[i] != 0) {
            len++;
            i++;
        }
        try {
            String stringUtils = StringUtils.toString(this.byteBuffer, this.position, len, encoding);
            this.position += len + 1;
            return stringUtils;
        } catch (UnsupportedEncodingException e) {
            throw SQLError.createSQLException(Messages.getString("ByteArrayBuffer.1") + encoding + "'", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
        } catch (Throwable th) {
            this.position += len + 1;
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    public final String readString(String encoding, ExceptionInterceptor exceptionInterceptor, int expectedLength) throws SQLException {
        if (this.position + expectedLength <= getBufLength()) {
            try {
                String stringUtils = StringUtils.toString(this.byteBuffer, this.position, expectedLength, encoding);
                this.position += expectedLength;
                return stringUtils;
            } catch (UnsupportedEncodingException e) {
                throw SQLError.createSQLException(Messages.getString("ByteArrayBuffer.1") + encoding + "'", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
            } catch (Throwable th) {
                this.position += expectedLength;
                throw th;
            }
        } else {
            throw SQLError.createSQLException(Messages.getString("ByteArrayBuffer.2"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
        }
    }

    public void setBufLength(int bufLengthToSet) {
        this.bufLength = bufLengthToSet;
    }

    public void setByteBuffer(byte[] byteBufferToSet) {
        this.byteBuffer = byteBufferToSet;
    }

    public void setPosition(int positionToSet) {
        this.position = positionToSet;
    }

    public void setWasMultiPacket(boolean flag) {
        this.wasMultiPacket = flag;
    }

    public String toString() {
        return dumpClampedBytes(getPosition());
    }

    public String toSuperString() {
        return super.toString();
    }

    public boolean wasMultiPacket() {
        return this.wasMultiPacket;
    }

    public final void writeByte(byte b) throws SQLException {
        ensureCapacity(1);
        byte[] bArr = this.byteBuffer;
        int i = this.position;
        this.position = i + 1;
        bArr[i] = b;
    }

    public final void writeBytesNoNull(byte[] bytes) throws SQLException {
        int len = bytes.length;
        ensureCapacity(len);
        System.arraycopy(bytes, 0, this.byteBuffer, this.position, len);
        this.position += len;
    }

    /* access modifiers changed from: package-private */
    public final void writeBytesNoNull(byte[] bytes, int offset, int length) throws SQLException {
        ensureCapacity(length);
        System.arraycopy(bytes, offset, this.byteBuffer, this.position, length);
        this.position += length;
    }

    /* access modifiers changed from: package-private */
    public final void writeDouble(double d) throws SQLException {
        writeLongLong(Double.doubleToLongBits(d));
    }

    /* access modifiers changed from: package-private */
    public final void writeFieldLength(long length) throws SQLException {
        if (length < 251) {
            writeByte((byte) ((int) length));
        } else if (length < 65536) {
            ensureCapacity(3);
            writeByte((byte) -4);
            writeInt((int) length);
        } else if (length < 16777216) {
            ensureCapacity(4);
            writeByte((byte) -3);
            writeLongInt((int) length);
        } else {
            ensureCapacity(9);
            writeByte((byte) -2);
            writeLongLong(length);
        }
    }

    /* access modifiers changed from: package-private */
    public final void writeFloat(float f) throws SQLException {
        ensureCapacity(4);
        int i = Float.floatToIntBits(f);
        byte[] b = this.byteBuffer;
        int i2 = this.position;
        int i3 = i2 + 1;
        this.position = i3;
        b[i2] = (byte) (i & 255);
        int i4 = i3 + 1;
        this.position = i4;
        b[i3] = (byte) (i >>> 8);
        int i5 = i4 + 1;
        this.position = i5;
        b[i4] = (byte) (i >>> 16);
        this.position = i5 + 1;
        b[i5] = (byte) (i >>> 24);
    }

    /* access modifiers changed from: package-private */
    public final void writeInt(int i) throws SQLException {
        ensureCapacity(2);
        byte[] b = this.byteBuffer;
        int i2 = this.position;
        int i3 = i2 + 1;
        this.position = i3;
        b[i2] = (byte) (i & 255);
        this.position = i3 + 1;
        b[i3] = (byte) (i >>> 8);
    }

    /* access modifiers changed from: package-private */
    public final void writeLenBytes(byte[] b) throws SQLException {
        int len = b.length;
        ensureCapacity(len + 9);
        writeFieldLength((long) len);
        System.arraycopy(b, 0, this.byteBuffer, this.position, len);
        this.position += len;
    }

    /* access modifiers changed from: package-private */
    public final void writeLenString(String s, String encoding, String serverEncoding, SingleByteCharsetConverter converter, boolean parserKnowsUnicode, MySQLConnection conn) throws UnsupportedEncodingException, SQLException {
        byte[] b;
        if (converter != null) {
            b = converter.toBytes(s);
        } else {
            b = StringUtils.getBytes(s, encoding, serverEncoding, parserKnowsUnicode, conn, conn.getExceptionInterceptor());
        }
        int len = b.length;
        ensureCapacity(len + 9);
        writeFieldLength((long) len);
        System.arraycopy(b, 0, this.byteBuffer, this.position, len);
        this.position += len;
    }

    /* access modifiers changed from: package-private */
    public final void writeLong(long i) throws SQLException {
        ensureCapacity(4);
        byte[] b = this.byteBuffer;
        int i2 = this.position;
        int i3 = i2 + 1;
        this.position = i3;
        b[i2] = (byte) ((int) (255 & i));
        int i4 = i3 + 1;
        this.position = i4;
        b[i3] = (byte) ((int) (i >>> 8));
        int i5 = i4 + 1;
        this.position = i5;
        b[i4] = (byte) ((int) (i >>> 16));
        this.position = i5 + 1;
        b[i5] = (byte) ((int) (i >>> 24));
    }

    /* access modifiers changed from: package-private */
    public final void writeLongInt(int i) throws SQLException {
        ensureCapacity(3);
        byte[] b = this.byteBuffer;
        int i2 = this.position;
        int i3 = i2 + 1;
        this.position = i3;
        b[i2] = (byte) (i & 255);
        int i4 = i3 + 1;
        this.position = i4;
        b[i3] = (byte) (i >>> 8);
        this.position = i4 + 1;
        b[i4] = (byte) (i >>> 16);
    }

    /* access modifiers changed from: package-private */
    public final void writeLongLong(long i) throws SQLException {
        ensureCapacity(8);
        byte[] b = this.byteBuffer;
        int i2 = this.position;
        int i3 = i2 + 1;
        this.position = i3;
        b[i2] = (byte) ((int) (255 & i));
        int i4 = i3 + 1;
        this.position = i4;
        b[i3] = (byte) ((int) (i >>> 8));
        int i5 = i4 + 1;
        this.position = i5;
        b[i4] = (byte) ((int) (i >>> 16));
        int i6 = i5 + 1;
        this.position = i6;
        b[i5] = (byte) ((int) (i >>> 24));
        int i7 = i6 + 1;
        this.position = i7;
        b[i6] = (byte) ((int) (i >>> 32));
        int i8 = i7 + 1;
        this.position = i8;
        b[i7] = (byte) ((int) (i >>> 40));
        int i9 = i8 + 1;
        this.position = i9;
        b[i8] = (byte) ((int) (i >>> 48));
        this.position = i9 + 1;
        b[i9] = (byte) ((int) (i >>> 56));
    }

    /* access modifiers changed from: package-private */
    public final void writeString(String s) throws SQLException {
        ensureCapacity((s.length() * 3) + 1);
        writeStringNoNull(s);
        byte[] bArr = this.byteBuffer;
        int i = this.position;
        this.position = i + 1;
        bArr[i] = 0;
    }

    /* access modifiers changed from: package-private */
    public final void writeString(String s, String encoding, MySQLConnection conn) throws SQLException {
        ensureCapacity((s.length() * 3) + 1);
        try {
            writeStringNoNull(s, encoding, encoding, false, conn);
            byte[] bArr = this.byteBuffer;
            int i = this.position;
            this.position = i + 1;
            bArr[i] = 0;
        } catch (UnsupportedEncodingException ue) {
            throw new SQLException(ue.toString(), SQLError.SQL_STATE_GENERAL_ERROR);
        }
    }

    /* access modifiers changed from: package-private */
    public final void writeStringNoNull(String s) throws SQLException {
        int len = s.length();
        ensureCapacity(len * 3);
        System.arraycopy(StringUtils.getBytes(s), 0, this.byteBuffer, this.position, len);
        this.position += len;
    }

    /* access modifiers changed from: package-private */
    public final void writeStringNoNull(String s, String encoding, String serverEncoding, boolean parserKnowsUnicode, MySQLConnection conn) throws UnsupportedEncodingException, SQLException {
        byte[] b = StringUtils.getBytes(s, encoding, serverEncoding, parserKnowsUnicode, conn, conn.getExceptionInterceptor());
        int len = b.length;
        ensureCapacity(len);
        System.arraycopy(b, 0, this.byteBuffer, this.position, len);
        this.position += len;
    }
}
