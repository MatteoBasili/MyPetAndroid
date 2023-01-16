package com.mysql.jdbc;

import androidx.recyclerview.widget.ItemTouchHelper;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class BufferRow extends ResultSetRow {
    private int homePosition = 0;
    private boolean isBinaryEncoded;
    private boolean[] isNull;
    private int lastRequestedIndex = -1;
    private int lastRequestedPos;
    private Field[] metadata;
    private List<InputStream> openStreams;
    private int preNullBitmaskHomePosition = 0;
    private Buffer rowFromServer;

    public BufferRow(Buffer buf, Field[] fields, boolean isBinaryEncoded2, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        super(exceptionInterceptor);
        this.rowFromServer = buf;
        this.metadata = fields;
        this.isBinaryEncoded = isBinaryEncoded2;
        int position = buf.getPosition();
        this.homePosition = position;
        this.preNullBitmaskHomePosition = position;
        if (fields != null) {
            setMetadata(fields);
        }
    }

    public synchronized void closeOpenStreams() {
        List<InputStream> list = this.openStreams;
        if (list != null) {
            for (InputStream close : list) {
                try {
                    close.close();
                } catch (IOException e) {
                }
            }
            this.openStreams.clear();
        }
    }

    private int findAndSeekToOffset(int index) throws SQLException {
        if (this.isBinaryEncoded) {
            return findAndSeekToOffsetForBinaryEncoding(index);
        }
        if (index == 0) {
            this.lastRequestedIndex = 0;
            int i = this.homePosition;
            this.lastRequestedPos = i;
            this.rowFromServer.setPosition(i);
            return 0;
        }
        int i2 = this.lastRequestedIndex;
        if (index == i2) {
            this.rowFromServer.setPosition(this.lastRequestedPos);
            return this.lastRequestedPos;
        }
        int startingIndex = 0;
        if (index > i2) {
            if (i2 >= 0) {
                startingIndex = this.lastRequestedIndex;
            } else {
                startingIndex = 0;
            }
            this.rowFromServer.setPosition(this.lastRequestedPos);
        } else {
            this.rowFromServer.setPosition(this.homePosition);
        }
        for (int i3 = startingIndex; i3 < index; i3++) {
            this.rowFromServer.fastSkipLenByteArray();
        }
        this.lastRequestedIndex = index;
        int position = this.rowFromServer.getPosition();
        this.lastRequestedPos = position;
        return position;
    }

    private int findAndSeekToOffsetForBinaryEncoding(int index) throws SQLException {
        if (index == 0) {
            this.lastRequestedIndex = 0;
            int i = this.homePosition;
            this.lastRequestedPos = i;
            this.rowFromServer.setPosition(i);
            return 0;
        }
        int i2 = this.lastRequestedIndex;
        if (index == i2) {
            this.rowFromServer.setPosition(this.lastRequestedPos);
            return this.lastRequestedPos;
        }
        int startingIndex = 0;
        if (index > i2) {
            if (i2 >= 0) {
                startingIndex = this.lastRequestedIndex;
            } else {
                this.lastRequestedPos = this.homePosition;
                startingIndex = 0;
            }
            this.rowFromServer.setPosition(this.lastRequestedPos);
        } else {
            this.rowFromServer.setPosition(this.homePosition);
        }
        for (int i3 = startingIndex; i3 < index; i3++) {
            if (!this.isNull[i3]) {
                int curPosition = this.rowFromServer.getPosition();
                switch (this.metadata[i3].getMysqlType()) {
                    case 0:
                    case 15:
                    case 16:
                    case 245:
                    case 246:
                    case 249:
                    case ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION:
                    case 251:
                    case MysqlDefs.FIELD_TYPE_BLOB /*252*/:
                    case 253:
                    case 254:
                    case 255:
                        this.rowFromServer.fastSkipLenByteArray();
                        break;
                    case 1:
                        this.rowFromServer.setPosition(curPosition + 1);
                        break;
                    case 2:
                    case 13:
                        this.rowFromServer.setPosition(curPosition + 2);
                        break;
                    case 3:
                    case 9:
                        this.rowFromServer.setPosition(curPosition + 4);
                        break;
                    case 4:
                        this.rowFromServer.setPosition(curPosition + 4);
                        break;
                    case 5:
                        this.rowFromServer.setPosition(curPosition + 8);
                        break;
                    case 6:
                        break;
                    case 7:
                    case 12:
                        this.rowFromServer.fastSkipLenByteArray();
                        break;
                    case 8:
                        this.rowFromServer.setPosition(curPosition + 8);
                        break;
                    case 10:
                        this.rowFromServer.fastSkipLenByteArray();
                        break;
                    case 11:
                        this.rowFromServer.fastSkipLenByteArray();
                        break;
                    default:
                        throw SQLError.createSQLException(Messages.getString("MysqlIO.97") + this.metadata[i3].getMysqlType() + Messages.getString("MysqlIO.98") + (i3 + 1) + Messages.getString("MysqlIO.99") + this.metadata.length + Messages.getString("MysqlIO.100"), SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
                }
            }
        }
        this.lastRequestedIndex = index;
        int position = this.rowFromServer.getPosition();
        this.lastRequestedPos = position;
        return position;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003d, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized java.io.InputStream getBinaryInputStream(int r7) throws java.sql.SQLException {
        /*
            r6 = this;
            monitor-enter(r6)
            boolean r0 = r6.isBinaryEncoded     // Catch:{ all -> 0x003e }
            r1 = 0
            if (r0 == 0) goto L_0x000e
            boolean r0 = r6.isNull(r7)     // Catch:{ all -> 0x003e }
            if (r0 == 0) goto L_0x000e
            monitor-exit(r6)
            return r1
        L_0x000e:
            r6.findAndSeekToOffset(r7)     // Catch:{ all -> 0x003e }
            com.mysql.jdbc.Buffer r0 = r6.rowFromServer     // Catch:{ all -> 0x003e }
            long r2 = r0.readFieldLength()     // Catch:{ all -> 0x003e }
            com.mysql.jdbc.Buffer r0 = r6.rowFromServer     // Catch:{ all -> 0x003e }
            int r0 = r0.getPosition()     // Catch:{ all -> 0x003e }
            r4 = -1
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 != 0) goto L_0x0025
            monitor-exit(r6)
            return r1
        L_0x0025:
            java.io.ByteArrayInputStream r1 = new java.io.ByteArrayInputStream     // Catch:{ all -> 0x003e }
            com.mysql.jdbc.Buffer r4 = r6.rowFromServer     // Catch:{ all -> 0x003e }
            byte[] r4 = r4.getByteBuffer()     // Catch:{ all -> 0x003e }
            int r5 = (int) r2     // Catch:{ all -> 0x003e }
            r1.<init>(r4, r0, r5)     // Catch:{ all -> 0x003e }
            java.util.List<java.io.InputStream> r4 = r6.openStreams     // Catch:{ all -> 0x003e }
            if (r4 != 0) goto L_0x003c
            java.util.LinkedList r4 = new java.util.LinkedList     // Catch:{ all -> 0x003e }
            r4.<init>()     // Catch:{ all -> 0x003e }
            r6.openStreams = r4     // Catch:{ all -> 0x003e }
        L_0x003c:
            monitor-exit(r6)
            return r1
        L_0x003e:
            r7 = move-exception
            monitor-exit(r6)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.BufferRow.getBinaryInputStream(int):java.io.InputStream");
    }

    public byte[] getColumnValue(int index) throws SQLException {
        findAndSeekToOffset(index);
        if (!this.isBinaryEncoded) {
            return this.rowFromServer.readLenByteArray(0);
        }
        if (this.isNull[index]) {
            return null;
        }
        switch (this.metadata[index].getMysqlType()) {
            case 0:
            case 7:
            case 10:
            case 11:
            case 12:
            case 15:
            case 16:
            case 245:
            case 246:
            case 249:
            case ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION:
            case 251:
            case MysqlDefs.FIELD_TYPE_BLOB /*252*/:
            case 253:
            case 254:
            case 255:
                return this.rowFromServer.readLenByteArray(0);
            case 1:
                return new byte[]{this.rowFromServer.readByte()};
            case 2:
            case 13:
                return this.rowFromServer.getBytes(2);
            case 3:
            case 9:
                return this.rowFromServer.getBytes(4);
            case 4:
                return this.rowFromServer.getBytes(4);
            case 5:
                return this.rowFromServer.getBytes(8);
            case 6:
                return null;
            case 8:
                return this.rowFromServer.getBytes(8);
            default:
                throw SQLError.createSQLException(Messages.getString("MysqlIO.97") + this.metadata[index].getMysqlType() + Messages.getString("MysqlIO.98") + (index + 1) + Messages.getString("MysqlIO.99") + this.metadata.length + Messages.getString("MysqlIO.100"), SQLError.SQL_STATE_GENERAL_ERROR, this.exceptionInterceptor);
        }
    }

    public int getInt(int columnIndex) throws SQLException {
        findAndSeekToOffset(columnIndex);
        long length = this.rowFromServer.readFieldLength();
        int offset = this.rowFromServer.getPosition();
        if (length == -1) {
            return 0;
        }
        return StringUtils.getInt(this.rowFromServer.getByteBuffer(), offset, ((int) length) + offset);
    }

    public long getLong(int columnIndex) throws SQLException {
        findAndSeekToOffset(columnIndex);
        long length = this.rowFromServer.readFieldLength();
        int offset = this.rowFromServer.getPosition();
        if (length == -1) {
            return 0;
        }
        return StringUtils.getLong(this.rowFromServer.getByteBuffer(), offset, ((int) length) + offset);
    }

    public double getNativeDouble(int columnIndex) throws SQLException {
        if (isNull(columnIndex)) {
            return 0.0d;
        }
        findAndSeekToOffset(columnIndex);
        return getNativeDouble(this.rowFromServer.getByteBuffer(), this.rowFromServer.getPosition());
    }

    public float getNativeFloat(int columnIndex) throws SQLException {
        if (isNull(columnIndex)) {
            return 0.0f;
        }
        findAndSeekToOffset(columnIndex);
        return getNativeFloat(this.rowFromServer.getByteBuffer(), this.rowFromServer.getPosition());
    }

    public int getNativeInt(int columnIndex) throws SQLException {
        if (isNull(columnIndex)) {
            return 0;
        }
        findAndSeekToOffset(columnIndex);
        return getNativeInt(this.rowFromServer.getByteBuffer(), this.rowFromServer.getPosition());
    }

    public long getNativeLong(int columnIndex) throws SQLException {
        if (isNull(columnIndex)) {
            return 0;
        }
        findAndSeekToOffset(columnIndex);
        return getNativeLong(this.rowFromServer.getByteBuffer(), this.rowFromServer.getPosition());
    }

    public short getNativeShort(int columnIndex) throws SQLException {
        if (isNull(columnIndex)) {
            return 0;
        }
        findAndSeekToOffset(columnIndex);
        return getNativeShort(this.rowFromServer.getByteBuffer(), this.rowFromServer.getPosition());
    }

    public Timestamp getNativeTimestamp(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException {
        if (isNull(columnIndex)) {
            return null;
        }
        findAndSeekToOffset(columnIndex);
        long length = this.rowFromServer.readFieldLength();
        return getNativeTimestamp(this.rowFromServer.getByteBuffer(), this.rowFromServer.getPosition(), (int) length, targetCalendar, tz, rollForward, conn, rs);
    }

    public Reader getReader(int columnIndex) throws SQLException {
        InputStream stream = getBinaryInputStream(columnIndex);
        if (stream == null) {
            return null;
        }
        try {
            return new InputStreamReader(stream, this.metadata[columnIndex].getEncoding());
        } catch (UnsupportedEncodingException e) {
            SQLException sqlEx = SQLError.createSQLException("", this.exceptionInterceptor);
            sqlEx.initCause(e);
            throw sqlEx;
        }
    }

    public String getString(int columnIndex, String encoding, MySQLConnection conn) throws SQLException {
        if (this.isBinaryEncoded && isNull(columnIndex)) {
            return null;
        }
        findAndSeekToOffset(columnIndex);
        long length = this.rowFromServer.readFieldLength();
        if (length == -1) {
            return null;
        }
        if (length == 0) {
            return "";
        }
        return getString(encoding, conn, this.rowFromServer.getByteBuffer(), this.rowFromServer.getPosition(), (int) length);
    }

    public Time getTimeFast(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException {
        if (isNull(columnIndex)) {
            return null;
        }
        findAndSeekToOffset(columnIndex);
        long length = this.rowFromServer.readFieldLength();
        return getTimeFast(columnIndex, this.rowFromServer.getByteBuffer(), this.rowFromServer.getPosition(), (int) length, targetCalendar, tz, rollForward, conn, rs);
    }

    public Timestamp getTimestampFast(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs, boolean useGmtMillis, boolean useJDBCCompliantTimezoneShift) throws SQLException {
        if (isNull(columnIndex)) {
            return null;
        }
        findAndSeekToOffset(columnIndex);
        long length = this.rowFromServer.readFieldLength();
        return getTimestampFast(columnIndex, this.rowFromServer.getByteBuffer(), this.rowFromServer.getPosition(), (int) length, targetCalendar, tz, rollForward, conn, rs, useGmtMillis, useJDBCCompliantTimezoneShift);
    }

    public boolean isFloatingPointNumber(int index) throws SQLException {
        if (this.isBinaryEncoded) {
            switch (this.metadata[index].getSQLType()) {
                case 2:
                case 3:
                case 6:
                case 8:
                    return true;
                default:
                    return false;
            }
        } else {
            findAndSeekToOffset(index);
            long length = this.rowFromServer.readFieldLength();
            if (length == -1 || length == 0) {
                return false;
            }
            int offset = this.rowFromServer.getPosition();
            byte[] buffer = this.rowFromServer.getByteBuffer();
            for (int i = 0; i < ((int) length); i++) {
                char c = (char) buffer[offset + i];
                if (c == 'e' || c == 'E') {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean isNull(int index) throws SQLException {
        if (this.isBinaryEncoded) {
            return this.isNull[index];
        }
        findAndSeekToOffset(index);
        return this.rowFromServer.readFieldLength() == -1;
    }

    public long length(int index) throws SQLException {
        findAndSeekToOffset(index);
        long length = this.rowFromServer.readFieldLength();
        if (length == -1) {
            return 0;
        }
        return length;
    }

    public void setColumnValue(int index, byte[] value) throws SQLException {
        throw new OperationNotSupportedException();
    }

    public ResultSetRow setMetadata(Field[] f) throws SQLException {
        super.setMetadata(f);
        if (this.isBinaryEncoded) {
            setupIsNullBitmask();
        }
        return this;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v0, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: byte} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setupIsNullBitmask() throws java.sql.SQLException {
        /*
            r7 = this;
            boolean[] r0 = r7.isNull
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            com.mysql.jdbc.Buffer r0 = r7.rowFromServer
            int r1 = r7.preNullBitmaskHomePosition
            r0.setPosition(r1)
            com.mysql.jdbc.Field[] r0 = r7.metadata
            int r0 = r0.length
            int r0 = r0 + 9
            int r0 = r0 / 8
            byte[] r1 = new byte[r0]
            r2 = 0
        L_0x0016:
            if (r2 >= r0) goto L_0x0023
            com.mysql.jdbc.Buffer r3 = r7.rowFromServer
            byte r3 = r3.readByte()
            r1[r2] = r3
            int r2 = r2 + 1
            goto L_0x0016
        L_0x0023:
            com.mysql.jdbc.Buffer r2 = r7.rowFromServer
            int r2 = r2.getPosition()
            r7.homePosition = r2
            com.mysql.jdbc.Field[] r2 = r7.metadata
            int r2 = r2.length
            boolean[] r2 = new boolean[r2]
            r7.isNull = r2
            r2 = 0
            r3 = 4
            r4 = 0
        L_0x0035:
            com.mysql.jdbc.Field[] r5 = r7.metadata
            int r5 = r5.length
            if (r4 >= r5) goto L_0x0053
            boolean[] r5 = r7.isNull
            byte r6 = r1[r2]
            r6 = r6 & r3
            if (r6 == 0) goto L_0x0043
            r6 = 1
            goto L_0x0044
        L_0x0043:
            r6 = 0
        L_0x0044:
            r5[r4] = r6
            int r5 = r3 << 1
            r3 = r5
            r5 = r5 & 255(0xff, float:3.57E-43)
            if (r5 != 0) goto L_0x0050
            r3 = 1
            int r2 = r2 + 1
        L_0x0050:
            int r4 = r4 + 1
            goto L_0x0035
        L_0x0053:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.BufferRow.setupIsNullBitmask():void");
    }

    public Date getDateFast(int columnIndex, MySQLConnection conn, ResultSetImpl rs, Calendar targetCalendar) throws SQLException {
        if (isNull(columnIndex)) {
            return null;
        }
        findAndSeekToOffset(columnIndex);
        long length = this.rowFromServer.readFieldLength();
        return getDateFast(columnIndex, this.rowFromServer.getByteBuffer(), this.rowFromServer.getPosition(), (int) length, conn, rs, targetCalendar);
    }

    public Date getNativeDate(int columnIndex, MySQLConnection conn, ResultSetImpl rs, Calendar cal) throws SQLException {
        if (isNull(columnIndex)) {
            return null;
        }
        findAndSeekToOffset(columnIndex);
        long length = this.rowFromServer.readFieldLength();
        return getNativeDate(columnIndex, this.rowFromServer.getByteBuffer(), this.rowFromServer.getPosition(), (int) length, conn, rs, cal);
    }

    public Object getNativeDateTimeValue(int columnIndex, Calendar targetCalendar, int jdbcType, int mysqlType, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException {
        if (isNull(columnIndex)) {
            return null;
        }
        findAndSeekToOffset(columnIndex);
        long length = this.rowFromServer.readFieldLength();
        return getNativeDateTimeValue(columnIndex, this.rowFromServer.getByteBuffer(), this.rowFromServer.getPosition(), (int) length, targetCalendar, jdbcType, mysqlType, tz, rollForward, conn, rs);
    }

    public Time getNativeTime(int columnIndex, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException {
        if (isNull(columnIndex)) {
            return null;
        }
        findAndSeekToOffset(columnIndex);
        long length = this.rowFromServer.readFieldLength();
        return getNativeTime(columnIndex, this.rowFromServer.getByteBuffer(), this.rowFromServer.getPosition(), (int) length, targetCalendar, tz, rollForward, conn, rs);
    }

    public int getBytesSize() {
        return this.rowFromServer.getBufLength();
    }
}
