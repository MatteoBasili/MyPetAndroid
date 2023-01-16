package com.sun.mail.imap.protocol;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;

public class BASE64MailboxEncoder {
    private static final char[] pem_array = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', ','};
    protected byte[] buffer = new byte[4];
    protected int bufsize = 0;
    protected Writer out = null;
    protected boolean started = false;

    public static String encode(String original) {
        BASE64MailboxEncoder base64stream = null;
        boolean changedString = false;
        CharArrayWriter writer = new CharArrayWriter(length);
        for (char current : original.toCharArray()) {
            if (current < ' ' || current > '~') {
                if (base64stream == null) {
                    base64stream = new BASE64MailboxEncoder(writer);
                    changedString = true;
                }
                base64stream.write(current);
            } else {
                if (base64stream != null) {
                    base64stream.flush();
                }
                if (current == '&') {
                    changedString = true;
                    writer.write(38);
                    writer.write(45);
                } else {
                    writer.write(current);
                }
            }
        }
        if (base64stream != null) {
            base64stream.flush();
        }
        if (changedString) {
            return writer.toString();
        }
        return original;
    }

    public BASE64MailboxEncoder(Writer what) {
        this.out = what;
    }

    public void write(int c) {
        try {
            if (!this.started) {
                this.started = true;
                this.out.write(38);
            }
            byte[] bArr = this.buffer;
            int i = this.bufsize;
            int i2 = i + 1;
            this.bufsize = i2;
            bArr[i] = (byte) (c >> 8);
            int i3 = i2 + 1;
            this.bufsize = i3;
            bArr[i2] = (byte) (c & 255);
            if (i3 >= 3) {
                encode();
                this.bufsize -= 3;
            }
        } catch (IOException e) {
        }
    }

    public void flush() {
        try {
            if (this.bufsize > 0) {
                encode();
                this.bufsize = 0;
            }
            if (this.started) {
                this.out.write(45);
                this.started = false;
            }
        } catch (IOException e) {
        }
    }

    /* access modifiers changed from: protected */
    public void encode() throws IOException {
        int i = this.bufsize;
        if (i == 1) {
            byte a = this.buffer[0];
            Writer writer = this.out;
            char[] cArr = pem_array;
            writer.write(cArr[(a >>> 2) & 63]);
            this.out.write(cArr[((a << 4) & 48) + ((0 >>> 4) & 15)]);
        } else if (i == 2) {
            byte[] bArr = this.buffer;
            byte a2 = bArr[0];
            byte b = bArr[1];
            Writer writer2 = this.out;
            char[] cArr2 = pem_array;
            writer2.write(cArr2[(a2 >>> 2) & 63]);
            this.out.write(cArr2[((a2 << 4) & 48) + ((b >>> 4) & 15)]);
            this.out.write(cArr2[((b << 2) & 60) + (3 & (0 >>> 6))]);
            byte c = a2;
        } else {
            byte[] bArr2 = this.buffer;
            byte a3 = bArr2[0];
            byte b2 = bArr2[1];
            byte c2 = bArr2[2];
            Writer writer3 = this.out;
            char[] cArr3 = pem_array;
            writer3.write(cArr3[(a3 >>> 2) & 63]);
            this.out.write(cArr3[((a3 << 4) & 48) + ((b2 >>> 4) & 15)]);
            this.out.write(cArr3[((b2 << 2) & 60) + ((c2 >>> 6) & 3)]);
            this.out.write(cArr3[c2 & 63]);
            if (this.bufsize == 4) {
                byte[] bArr3 = this.buffer;
                bArr3[0] = bArr3[3];
            }
            byte b3 = c2;
            byte c3 = a3;
        }
    }
}
