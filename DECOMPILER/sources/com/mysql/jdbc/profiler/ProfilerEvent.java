package com.mysql.jdbc.profiler;

import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.log.LogUtils;
import java.util.Date;

public class ProfilerEvent {
    public static final byte NA = -1;
    public static final byte TYPE_EXECUTE = 4;
    public static final byte TYPE_FETCH = 5;
    public static final byte TYPE_OBJECT_CREATION = 1;
    public static final byte TYPE_PREPARE = 2;
    public static final byte TYPE_QUERY = 3;
    public static final byte TYPE_SLOW_QUERY = 6;
    public static final byte TYPE_USAGE = 0;
    public static final byte TYPE_WARN = 0;
    protected String catalog;
    public int catalogIndex;
    protected long connectionId;
    protected String durationUnits;
    protected String eventCreationPointDesc;
    public int eventCreationPointIndex;
    protected long eventCreationTime;
    protected long eventDuration;
    protected byte eventType;
    protected String hostName;
    public int hostNameIndex;
    protected String message;
    protected int resultSetId;
    protected int statementId;

    public ProfilerEvent(byte eventType2, String hostName2, String catalog2, long connectionId2, int statementId2, int resultSetId2, long eventDuration2, String durationUnits2, Throwable eventCreationPoint, String message2) {
        this(eventType2, hostName2, catalog2, connectionId2, statementId2, resultSetId2, System.currentTimeMillis(), eventDuration2, durationUnits2, LogUtils.findCallingClassAndMethod(eventCreationPoint), message2, -1, -1, -1);
    }

    private ProfilerEvent(byte eventType2, String hostName2, String catalog2, long connectionId2, int statementId2, int resultSetId2, long eventCreationTime2, long eventDuration2, String durationUnits2, String eventCreationPointDesc2, String message2, int hostNameIndex2, int catalogIndex2, int eventCreationPointIndex2) {
        this.eventType = eventType2;
        String str = "";
        this.hostName = hostName2 == null ? str : hostName2;
        this.catalog = catalog2 == null ? str : catalog2;
        this.connectionId = connectionId2;
        this.statementId = statementId2;
        this.resultSetId = resultSetId2;
        this.eventCreationTime = eventCreationTime2;
        this.eventDuration = eventDuration2;
        this.durationUnits = durationUnits2 == null ? str : durationUnits2;
        this.eventCreationPointDesc = eventCreationPointDesc2 == null ? str : eventCreationPointDesc2;
        this.message = message2 != null ? message2 : str;
        this.hostNameIndex = hostNameIndex2;
        this.catalogIndex = catalogIndex2;
        this.eventCreationPointIndex = eventCreationPointIndex2;
    }

    public byte getEventType() {
        return this.eventType;
    }

    public String getHostName() {
        return this.hostName;
    }

    public String getCatalog() {
        return this.catalog;
    }

    public long getConnectionId() {
        return this.connectionId;
    }

    public int getStatementId() {
        return this.statementId;
    }

    public int getResultSetId() {
        return this.resultSetId;
    }

    public long getEventCreationTime() {
        return this.eventCreationTime;
    }

    public long getEventDuration() {
        return this.eventDuration;
    }

    public String getDurationUnits() {
        return this.durationUnits;
    }

    public String getEventCreationPointAsString() {
        return this.eventCreationPointDesc;
    }

    public String getMessage() {
        return this.message;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("[");
        switch (getEventType()) {
            case 0:
                buf.append("USAGE ADVISOR");
                break;
            case 1:
                buf.append("CONSTRUCT");
                break;
            case 2:
                buf.append("PREPARE");
                break;
            case 3:
                buf.append("QUERY");
                break;
            case 4:
                buf.append("EXECUTE");
                break;
            case 5:
                buf.append("FETCH");
                break;
            case 6:
                buf.append("SLOW QUERY");
                break;
            default:
                buf.append("UNKNOWN");
                break;
        }
        buf.append("] ");
        buf.append(this.message);
        buf.append(" [Created on: ");
        buf.append(new Date(this.eventCreationTime));
        buf.append(", duration: ");
        buf.append(this.eventDuration);
        buf.append(", connection-id: ");
        buf.append(this.connectionId);
        buf.append(", statement-id: ");
        buf.append(this.statementId);
        buf.append(", resultset-id: ");
        buf.append(this.resultSetId);
        buf.append(",");
        buf.append(this.eventCreationPointDesc);
        buf.append(", hostNameIndex: ");
        buf.append(this.hostNameIndex);
        buf.append(", catalogIndex: ");
        buf.append(this.catalogIndex);
        buf.append(", eventCreationPointIndex: ");
        buf.append(this.eventCreationPointIndex);
        buf.append("]");
        return buf.toString();
    }

    public static ProfilerEvent unpack(byte[] buf) throws Exception {
        byte[] bArr = buf;
        int pos = 0 + 1;
        byte pos2 = bArr[0];
        byte[] host = readBytes(bArr, pos);
        int pos3 = pos + host.length + 4;
        byte[] db = readBytes(bArr, pos3);
        int pos4 = pos3 + db.length + 4;
        int pos5 = pos4 + 8;
        int pos6 = pos5 + 4;
        int pos7 = pos6 + 4;
        int pos8 = pos7 + 8;
        int pos9 = pos8 + 8;
        byte[] eventDurationUnits = readBytes(bArr, pos9);
        int pos10 = pos9 + eventDurationUnits.length + 4;
        byte[] eventCreationAsBytes = readBytes(bArr, pos10);
        byte b = pos2;
        int pos11 = pos10 + eventCreationAsBytes.length + 4;
        byte[] message2 = readBytes(bArr, pos11);
        int pos12 = pos11 + message2.length + 4;
        int pos13 = pos12 + 4;
        int pos14 = pos13 + 4;
        int i = pos14 + 4;
        byte[] eventDurationUnits2 = eventDurationUnits;
        byte[] bArr2 = db;
        byte[] bArr3 = host;
        byte[] bArr4 = eventDurationUnits2;
        return new ProfilerEvent(pos2, StringUtils.toString(host, "ISO8859_1"), StringUtils.toString(db, "ISO8859_1"), readLong(bArr, pos4), readInt(bArr, pos5), readInt(bArr, pos6), readLong(bArr, pos7), readLong(bArr, pos8), StringUtils.toString(eventDurationUnits2, "ISO8859_1"), StringUtils.toString(eventCreationAsBytes, "ISO8859_1"), StringUtils.toString(message2, "ISO8859_1"), readInt(bArr, pos12), readInt(bArr, pos13), readInt(bArr, pos14));
    }

    public byte[] pack() throws Exception {
        byte[] hostNameAsBytes = StringUtils.getBytes(this.hostName, "ISO8859_1");
        byte[] dbAsBytes = StringUtils.getBytes(this.catalog, "ISO8859_1");
        byte[] durationUnitsAsBytes = StringUtils.getBytes(this.durationUnits, "ISO8859_1");
        byte[] eventCreationAsBytes = StringUtils.getBytes(this.eventCreationPointDesc, "ISO8859_1");
        byte[] messageAsBytes = StringUtils.getBytes(this.message, "ISO8859_1");
        byte[] buf = new byte[(hostNameAsBytes.length + 4 + 1 + dbAsBytes.length + 4 + 8 + 4 + 4 + 8 + 8 + durationUnitsAsBytes.length + 4 + eventCreationAsBytes.length + 4 + messageAsBytes.length + 4 + 4 + 4 + 4)];
        buf[0] = this.eventType;
        int pos = writeInt(this.eventCreationPointIndex, buf, writeInt(this.catalogIndex, buf, writeInt(this.hostNameIndex, buf, writeBytes(messageAsBytes, buf, writeBytes(eventCreationAsBytes, buf, writeBytes(durationUnitsAsBytes, buf, writeLong(this.eventDuration, buf, writeLong(this.eventCreationTime, buf, writeInt(this.resultSetId, buf, writeInt(this.statementId, buf, writeLong(this.connectionId, buf, writeBytes(dbAsBytes, buf, writeBytes(hostNameAsBytes, buf, 0 + 1)))))))))))));
        return buf;
    }

    private static int writeInt(int i, byte[] buf, int pos) {
        int pos2 = pos + 1;
        buf[pos] = (byte) (i & 255);
        int pos3 = pos2 + 1;
        buf[pos2] = (byte) (i >>> 8);
        int pos4 = pos3 + 1;
        buf[pos3] = (byte) (i >>> 16);
        int pos5 = pos4 + 1;
        buf[pos4] = (byte) (i >>> 24);
        return pos5;
    }

    private static int writeLong(long l, byte[] buf, int pos) {
        int pos2 = pos + 1;
        buf[pos] = (byte) ((int) (255 & l));
        int pos3 = pos2 + 1;
        buf[pos2] = (byte) ((int) (l >>> 8));
        int pos4 = pos3 + 1;
        buf[pos3] = (byte) ((int) (l >>> 16));
        int pos5 = pos4 + 1;
        buf[pos4] = (byte) ((int) (l >>> 24));
        int pos6 = pos5 + 1;
        buf[pos5] = (byte) ((int) (l >>> 32));
        int pos7 = pos6 + 1;
        buf[pos6] = (byte) ((int) (l >>> 40));
        int pos8 = pos7 + 1;
        buf[pos7] = (byte) ((int) (l >>> 48));
        int pos9 = pos8 + 1;
        buf[pos8] = (byte) ((int) (l >>> 56));
        return pos9;
    }

    private static int writeBytes(byte[] msg, byte[] buf, int pos) {
        int pos2 = writeInt(msg.length, buf, pos);
        System.arraycopy(msg, 0, buf, pos2, msg.length);
        return msg.length + pos2;
    }

    private static int readInt(byte[] buf, int pos) {
        int pos2 = pos + 1;
        int pos3 = pos2 + 1;
        byte b = (buf[pos] & 255) | ((buf[pos2] & 255) << 8);
        int pos4 = pos3 + 1;
        byte b2 = b | ((buf[pos3] & 255) << 16);
        int i = pos4 + 1;
        return b2 | ((buf[pos4] & 255) << 24);
    }

    private static long readLong(byte[] buf, int pos) {
        int pos2 = pos + 1;
        long j = (long) (buf[pos] & 255);
        int pos3 = pos2 + 1;
        int pos4 = pos3 + 1;
        int pos5 = pos4 + 1;
        long j2 = j | (((long) (buf[pos2] & 255)) << 8) | (((long) (buf[pos3] & 255)) << 16) | (((long) (buf[pos4] & 255)) << 24);
        int pos6 = pos5 + 1;
        int pos7 = pos6 + 1;
        long j3 = j2 | (((long) (buf[pos5] & 255)) << 32) | (((long) (buf[pos6] & 255)) << 40);
        int pos8 = pos7 + 1;
        int i = pos8 + 1;
        return j3 | (((long) (buf[pos7] & 255)) << 48) | (((long) (buf[pos8] & 255)) << 56);
    }

    private static byte[] readBytes(byte[] buf, int pos) {
        int length = readInt(buf, pos);
        byte[] msg = new byte[length];
        System.arraycopy(buf, pos + 4, msg, 0, length);
        return msg;
    }
}
