package com.sun.mail.imap;

import java.io.IOException;
import java.io.OutputStream;

/* compiled from: IMAPFolder */
class LengthCounter extends OutputStream {
    private byte[] buf = new byte[8192];
    private int maxsize;
    private int size = 0;

    public LengthCounter(int maxsize2) {
        this.maxsize = maxsize2;
    }

    public void write(int b) {
        int i = this.size;
        int newsize = i + 1;
        byte[] bArr = this.buf;
        if (bArr != null) {
            int i2 = this.maxsize;
            if (newsize > i2 && i2 >= 0) {
                this.buf = null;
            } else if (newsize > bArr.length) {
                byte[] newbuf = new byte[Math.max(bArr.length << 1, newsize)];
                System.arraycopy(this.buf, 0, newbuf, 0, this.size);
                this.buf = newbuf;
                newbuf[this.size] = (byte) b;
            } else {
                bArr[i] = (byte) b;
            }
        }
        this.size = newsize;
    }

    public void write(byte[] b, int off, int len) {
        if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        } else if (len != 0) {
            int i = this.size;
            int newsize = i + len;
            byte[] bArr = this.buf;
            if (bArr != null) {
                int i2 = this.maxsize;
                if (newsize > i2 && i2 >= 0) {
                    this.buf = null;
                } else if (newsize > bArr.length) {
                    byte[] newbuf = new byte[Math.max(bArr.length << 1, newsize)];
                    System.arraycopy(this.buf, 0, newbuf, 0, this.size);
                    this.buf = newbuf;
                    System.arraycopy(b, off, newbuf, this.size, len);
                } else {
                    System.arraycopy(b, off, bArr, i, len);
                }
            }
            this.size = newsize;
        }
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public int getSize() {
        return this.size;
    }

    public byte[] getBytes() {
        return this.buf;
    }
}
