package com.sun.mail.iap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResponseInputStream {
    private static final int incrementSlop = 16;
    private static final int maxIncrement = 262144;
    private static final int minIncrement = 256;
    private BufferedInputStream bin;

    public ResponseInputStream(InputStream in) {
        this.bin = new BufferedInputStream(in, 2048);
    }

    public ByteArray readResponse() throws IOException {
        return readResponse((ByteArray) null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x004b  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00a0 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.sun.mail.iap.ByteArray readResponse(com.sun.mail.iap.ByteArray r10) throws java.io.IOException {
        /*
            r9 = this;
            if (r10 != 0) goto L_0x000d
            com.sun.mail.iap.ByteArray r0 = new com.sun.mail.iap.ByteArray
            r1 = 128(0x80, float:1.794E-43)
            byte[] r2 = new byte[r1]
            r3 = 0
            r0.<init>(r2, r3, r1)
            r10 = r0
        L_0x000d:
            byte[] r0 = r10.getBytes()
            r1 = 0
        L_0x0012:
            r2 = 0
            r3 = 0
        L_0x0015:
            r4 = -1
            if (r3 != 0) goto L_0x0049
            java.io.BufferedInputStream r5 = r9.bin
            int r5 = r5.read()
            r2 = r5
            if (r5 != r4) goto L_0x0022
            goto L_0x0049
        L_0x0022:
            switch(r2) {
                case 10: goto L_0x0026;
                default: goto L_0x0025;
            }
        L_0x0025:
            goto L_0x0031
        L_0x0026:
            if (r1 <= 0) goto L_0x0031
            int r4 = r1 + -1
            byte r4 = r0[r4]
            r5 = 13
            if (r4 != r5) goto L_0x0031
            r3 = 1
        L_0x0031:
            int r4 = r0.length
            if (r1 < r4) goto L_0x0042
            int r4 = r0.length
            r5 = 262144(0x40000, float:3.67342E-40)
            if (r4 <= r5) goto L_0x003b
            r4 = 262144(0x40000, float:3.67342E-40)
        L_0x003b:
            r10.grow(r4)
            byte[] r0 = r10.getBytes()
        L_0x0042:
            int r4 = r1 + 1
            byte r5 = (byte) r2
            r0[r1] = r5
            r1 = r4
            goto L_0x0015
        L_0x0049:
            if (r2 == r4) goto L_0x00a0
            r4 = 5
            if (r1 < r4) goto L_0x009c
            int r4 = r1 + -3
            byte r4 = r0[r4]
            r5 = 125(0x7d, float:1.75E-43)
            if (r4 == r5) goto L_0x0057
            goto L_0x009c
        L_0x0057:
            int r4 = r1 + -4
        L_0x0059:
            if (r4 >= 0) goto L_0x005c
            goto L_0x0063
        L_0x005c:
            byte r5 = r0[r4]
            r6 = 123(0x7b, float:1.72E-43)
            if (r5 != r6) goto L_0x0099
        L_0x0063:
            if (r4 >= 0) goto L_0x0066
            goto L_0x009c
        L_0x0066:
            r5 = 0
            int r6 = r4 + 1
            int r7 = r1 + -3
            int r6 = com.sun.mail.util.ASCIIUtility.parseInt(r0, r6, r7)     // Catch:{ NumberFormatException -> 0x0097 }
            r5 = r6
            if (r5 <= 0) goto L_0x0012
            int r6 = r0.length
            int r6 = r6 - r1
            int r7 = r5 + 16
            if (r7 <= r6) goto L_0x008b
            int r7 = r5 + 16
            int r7 = r7 - r6
            r8 = 256(0x100, float:3.59E-43)
            if (r8 <= r7) goto L_0x0080
            goto L_0x0084
        L_0x0080:
            int r7 = r5 + 16
            int r8 = r7 - r6
        L_0x0084:
            r10.grow(r8)
            byte[] r0 = r10.getBytes()
        L_0x008b:
            if (r5 > 0) goto L_0x008e
            goto L_0x0012
        L_0x008e:
            java.io.BufferedInputStream r7 = r9.bin
            int r7 = r7.read(r0, r1, r5)
            int r5 = r5 - r7
            int r1 = r1 + r7
            goto L_0x008b
        L_0x0097:
            r6 = move-exception
            goto L_0x009c
        L_0x0099:
            int r4 = r4 + -1
            goto L_0x0059
        L_0x009c:
            r10.setCount(r1)
            return r10
        L_0x00a0:
            java.io.IOException r4 = new java.io.IOException
            r4.<init>()
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.iap.ResponseInputStream.readResponse(com.sun.mail.iap.ByteArray):com.sun.mail.iap.ByteArray");
    }
}
