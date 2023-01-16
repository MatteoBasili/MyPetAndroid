package com.mysql.jdbc.util;

import com.mysql.jdbc.log.Log;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ReadAheadInputStream extends InputStream {
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private byte[] buf;
    protected int currentPosition;
    protected boolean doDebug;
    protected int endOfCurrentData;
    protected Log log;
    private InputStream underlyingStream;

    private void fill(int readAtLeastTheseManyBytes) throws IOException {
        checkClosed();
        this.currentPosition = 0;
        this.endOfCurrentData = 0;
        int bytesToRead = Math.min(this.buf.length - 0, readAtLeastTheseManyBytes);
        int bytesAvailable = this.underlyingStream.available();
        if (bytesAvailable > bytesToRead) {
            bytesToRead = Math.min(this.buf.length - this.currentPosition, bytesAvailable);
        }
        if (this.doDebug) {
            StringBuilder debugBuf = new StringBuilder();
            debugBuf.append("  ReadAheadInputStream.fill(");
            debugBuf.append(readAtLeastTheseManyBytes);
            debugBuf.append("), buffer_size=");
            debugBuf.append(this.buf.length);
            debugBuf.append(", current_position=");
            debugBuf.append(this.currentPosition);
            debugBuf.append(", need to read ");
            debugBuf.append(Math.min(this.buf.length - this.currentPosition, readAtLeastTheseManyBytes));
            debugBuf.append(" bytes to fill request,");
            if (bytesAvailable > 0) {
                debugBuf.append(" underlying InputStream reports ");
                debugBuf.append(bytesAvailable);
                debugBuf.append(" total bytes available,");
            }
            debugBuf.append(" attempting to read ");
            debugBuf.append(bytesToRead);
            debugBuf.append(" bytes.");
            Log log2 = this.log;
            if (log2 != null) {
                log2.logTrace(debugBuf.toString());
            } else {
                System.err.println(debugBuf.toString());
            }
        }
        int n = this.underlyingStream.read(this.buf, this.currentPosition, bytesToRead);
        if (n > 0) {
            this.endOfCurrentData = this.currentPosition + n;
        }
    }

    private int readFromUnderlyingStreamIfNecessary(byte[] b, int off, int len) throws IOException {
        checkClosed();
        int avail = this.endOfCurrentData - this.currentPosition;
        if (this.doDebug) {
            StringBuilder debugBuf = new StringBuilder();
            debugBuf.append("ReadAheadInputStream.readIfNecessary(");
            debugBuf.append(Arrays.toString(b));
            debugBuf.append(",");
            debugBuf.append(off);
            debugBuf.append(",");
            debugBuf.append(len);
            debugBuf.append(")");
            if (avail <= 0) {
                debugBuf.append(" not all data available in buffer, must read from stream");
                if (len >= this.buf.length) {
                    debugBuf.append(", amount requested > buffer, returning direct read() from stream");
                }
            }
            Log log2 = this.log;
            if (log2 != null) {
                log2.logTrace(debugBuf.toString());
            } else {
                System.err.println(debugBuf.toString());
            }
        }
        if (avail <= 0) {
            if (len >= this.buf.length) {
                return this.underlyingStream.read(b, off, len);
            }
            fill(len);
            avail = this.endOfCurrentData - this.currentPosition;
            if (avail <= 0) {
                return -1;
            }
        }
        int bytesActuallyRead = avail < len ? avail : len;
        System.arraycopy(this.buf, this.currentPosition, b, off, bytesActuallyRead);
        this.currentPosition += bytesActuallyRead;
        return bytesActuallyRead;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0032, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int read(byte[] r4, int r5, int r6) throws java.io.IOException {
        /*
            r3 = this;
            monitor-enter(r3)
            r3.checkClosed()     // Catch:{ all -> 0x003a }
            r0 = r5 | r6
            int r1 = r5 + r6
            r0 = r0 | r1
            int r1 = r4.length     // Catch:{ all -> 0x003a }
            int r2 = r5 + r6
            int r1 = r1 - r2
            r0 = r0 | r1
            if (r0 < 0) goto L_0x0034
            if (r6 != 0) goto L_0x0015
            r0 = 0
            monitor-exit(r3)
            return r0
        L_0x0015:
            r0 = 0
        L_0x0016:
            int r1 = r5 + r0
            int r2 = r6 - r0
            int r1 = r3.readFromUnderlyingStreamIfNecessary(r4, r1, r2)     // Catch:{ all -> 0x003a }
            if (r1 > 0) goto L_0x0024
            if (r0 != 0) goto L_0x0031
            r0 = r1
            goto L_0x0031
        L_0x0024:
            int r0 = r0 + r1
            if (r0 < r6) goto L_0x0028
            goto L_0x0031
        L_0x0028:
            java.io.InputStream r2 = r3.underlyingStream     // Catch:{ all -> 0x003a }
            int r2 = r2.available()     // Catch:{ all -> 0x003a }
            if (r2 > 0) goto L_0x0033
        L_0x0031:
            monitor-exit(r3)
            return r0
        L_0x0033:
            goto L_0x0016
        L_0x0034:
            java.lang.IndexOutOfBoundsException r0 = new java.lang.IndexOutOfBoundsException     // Catch:{ all -> 0x003a }
            r0.<init>()     // Catch:{ all -> 0x003a }
            throw r0     // Catch:{ all -> 0x003a }
        L_0x003a:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.util.ReadAheadInputStream.read(byte[], int, int):int");
    }

    public int read() throws IOException {
        checkClosed();
        if (this.currentPosition >= this.endOfCurrentData) {
            fill(1);
            if (this.currentPosition >= this.endOfCurrentData) {
                return -1;
            }
        }
        byte[] bArr = this.buf;
        int i = this.currentPosition;
        this.currentPosition = i + 1;
        return bArr[i] & 255;
    }

    public int available() throws IOException {
        checkClosed();
        return this.underlyingStream.available() + (this.endOfCurrentData - this.currentPosition);
    }

    private void checkClosed() throws IOException {
        if (this.buf == null) {
            throw new IOException("Stream closed");
        }
    }

    public ReadAheadInputStream(InputStream toBuffer, boolean debug, Log logTo) {
        this(toBuffer, 4096, debug, logTo);
    }

    public ReadAheadInputStream(InputStream toBuffer, int bufferSize, boolean debug, Log logTo) {
        this.doDebug = false;
        this.underlyingStream = toBuffer;
        this.buf = new byte[bufferSize];
        this.doDebug = debug;
        this.log = logTo;
    }

    public void close() throws IOException {
        InputStream inputStream = this.underlyingStream;
        if (inputStream != null) {
            try {
                inputStream.close();
            } finally {
                this.underlyingStream = null;
                this.buf = null;
                this.log = null;
            }
        }
    }

    public boolean markSupported() {
        return false;
    }

    public long skip(long n) throws IOException {
        checkClosed();
        if (n <= 0) {
            return 0;
        }
        long bytesAvailInBuffer = (long) (this.endOfCurrentData - this.currentPosition);
        if (bytesAvailInBuffer <= 0) {
            fill((int) n);
            bytesAvailInBuffer = (long) (this.endOfCurrentData - this.currentPosition);
            if (bytesAvailInBuffer <= 0) {
                return 0;
            }
        }
        long bytesSkipped = bytesAvailInBuffer < n ? bytesAvailInBuffer : n;
        this.currentPosition = (int) (((long) this.currentPosition) + bytesSkipped);
        return bytesSkipped;
    }
}
