package com.mysql.jdbc;

import androidx.core.view.InputDeviceCompat;
import com.mysql.jdbc.ConnectionPropertiesImpl;
import com.mysql.jdbc.log.Log;
import com.mysql.jdbc.log.NullLogger;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

class CompressedInputStream extends InputStream {
    private byte[] buffer;
    private InputStream in;
    private Inflater inflater;
    private Log log;
    private byte[] packetHeaderBuffer = new byte[7];
    private int pos = 0;
    private ConnectionPropertiesImpl.BooleanConnectionProperty traceProtocol;

    public CompressedInputStream(Connection conn, InputStream streamFromServer) {
        this.traceProtocol = ((ConnectionPropertiesImpl) conn).traceProtocol;
        try {
            this.log = conn.getLog();
        } catch (SQLException e) {
            this.log = new NullLogger((String) null);
        }
        this.in = streamFromServer;
        this.inflater = new Inflater();
    }

    public int available() throws IOException {
        byte[] bArr = this.buffer;
        if (bArr == null) {
            return this.in.available();
        }
        return (bArr.length - this.pos) + this.in.available();
    }

    public void close() throws IOException {
        this.in.close();
        this.buffer = null;
        this.inflater.end();
        this.inflater = null;
        this.traceProtocol = null;
        this.log = null;
    }

    private void getNextPacketFromServer() throws IOException {
        byte[] uncompressedData;
        if (readFully(this.packetHeaderBuffer, 0, 7) >= 7) {
            byte[] bArr = this.packetHeaderBuffer;
            int compressedPacketLength = (bArr[0] & 255) + ((bArr[1] & 255) << 8) + ((bArr[2] & 255) << 16);
            int uncompressedLength = (bArr[4] & 255) + ((bArr[5] & 255) << 8) + ((bArr[6] & 255) << 16);
            boolean doTrace = this.traceProtocol.getValueAsBoolean();
            if (doTrace) {
                this.log.logTrace("Reading compressed packet of length " + compressedPacketLength + " uncompressed to " + uncompressedLength);
            }
            if (uncompressedLength > 0) {
                uncompressedData = new byte[uncompressedLength];
                byte[] compressedBuffer = new byte[compressedPacketLength];
                readFully(compressedBuffer, 0, compressedPacketLength);
                this.inflater.reset();
                this.inflater.setInput(compressedBuffer);
                try {
                    this.inflater.inflate(uncompressedData);
                } catch (DataFormatException e) {
                    throw new IOException("Error while uncompressing packet from server.");
                }
            } else {
                if (doTrace) {
                    this.log.logTrace("Packet didn't meet compression threshold, not uncompressing...");
                }
                uncompressedLength = compressedPacketLength;
                uncompressedData = new byte[uncompressedLength];
                readFully(uncompressedData, 0, uncompressedLength);
            }
            if (doTrace) {
                if (uncompressedLength > 1024) {
                    this.log.logTrace("Uncompressed packet: \n" + StringUtils.dumpAsHex(uncompressedData, 256));
                    byte[] tempData = new byte[256];
                    System.arraycopy(uncompressedData, uncompressedLength + InputDeviceCompat.SOURCE_ANY, tempData, 0, 256);
                    this.log.logTrace("Uncompressed packet: \n" + StringUtils.dumpAsHex(tempData, 256));
                    this.log.logTrace("Large packet dump truncated. Showing first and last 256 bytes.");
                } else {
                    this.log.logTrace("Uncompressed packet: \n" + StringUtils.dumpAsHex(uncompressedData, uncompressedLength));
                }
            }
            byte[] bArr2 = this.buffer;
            if (bArr2 != null && this.pos < bArr2.length) {
                if (doTrace) {
                    this.log.logTrace("Combining remaining packet with new: ");
                }
                byte[] bArr3 = this.buffer;
                int length = bArr3.length;
                int i = this.pos;
                int remaining = length - i;
                byte[] newBuffer = new byte[(uncompressedData.length + remaining)];
                System.arraycopy(bArr3, i, newBuffer, 0, remaining);
                System.arraycopy(uncompressedData, 0, newBuffer, remaining, uncompressedData.length);
                uncompressedData = newBuffer;
            }
            this.pos = 0;
            this.buffer = uncompressedData;
            return;
        }
        throw new IOException("Unexpected end of input stream");
    }

    private void getNextPacketIfRequired(int numBytes) throws IOException {
        byte[] bArr = this.buffer;
        if (bArr == null || this.pos + numBytes > bArr.length) {
            getNextPacketFromServer();
        }
    }

    public int read() throws IOException {
        try {
            getNextPacketIfRequired(1);
            byte[] bArr = this.buffer;
            int i = this.pos;
            this.pos = i + 1;
            return bArr[i] & 255;
        } catch (IOException e) {
            return -1;
        }
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        } else if (len <= 0) {
            return 0;
        } else {
            try {
                getNextPacketIfRequired(len);
                int consummedBytesLength = Math.min(this.buffer.length - this.pos, len);
                System.arraycopy(this.buffer, this.pos, b, off, consummedBytesLength);
                this.pos += consummedBytesLength;
                return consummedBytesLength;
            } catch (IOException e) {
                return -1;
            }
        }
    }

    private final int readFully(byte[] b, int off, int len) throws IOException {
        if (len >= 0) {
            int n = 0;
            while (n < len) {
                int count = this.in.read(b, off + n, len - n);
                if (count >= 0) {
                    n += count;
                } else {
                    throw new EOFException();
                }
            }
            return n;
        }
        throw new IndexOutOfBoundsException();
    }

    public long skip(long n) throws IOException {
        long count = 0;
        for (long i = 0; i < n && read() != -1; i++) {
            count++;
        }
        return count;
    }
}
