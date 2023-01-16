package com.sun.mail.imap;

import com.sun.mail.iap.ByteArray;
import com.sun.mail.iap.ConnectionException;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.util.FolderClosedIOException;
import com.sun.mail.util.MessageRemovedIOException;
import java.io.IOException;
import java.io.InputStream;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.MessagingException;

public class IMAPInputStream extends InputStream {
    private static final int slop = 64;
    private int blksize;
    private byte[] buf;
    private int bufcount;
    private int bufpos;
    private int max;
    private IMAPMessage msg;
    private boolean peek;
    private int pos = 0;
    private ByteArray readbuf;
    private String section;

    public IMAPInputStream(IMAPMessage msg2, String section2, int max2, boolean peek2) {
        this.msg = msg2;
        this.section = section2;
        this.max = max2;
        this.peek = peek2;
        this.blksize = msg2.getFetchBlockSize();
    }

    private void forceCheckExpunged() throws MessageRemovedIOException, FolderClosedIOException {
        synchronized (this.msg.getMessageCacheLock()) {
            try {
                this.msg.getProtocol().noop();
            } catch (ConnectionException cex) {
                throw new FolderClosedIOException(this.msg.getFolder(), cex.getMessage());
            } catch (FolderClosedException fex) {
                throw new FolderClosedIOException(fex.getFolder(), fex.getMessage());
            } catch (ProtocolException e) {
            }
        }
        if (this.msg.isExpunged()) {
            throw new MessageRemovedIOException();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0052 A[Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x005f A[Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x006f A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void fill() throws java.io.IOException {
        /*
            r12 = this;
            int r0 = r12.max
            r1 = -1
            if (r0 == r1) goto L_0x0012
            int r2 = r12.pos
            if (r2 < r0) goto L_0x0012
            if (r2 != 0) goto L_0x000e
            r12.checkSeen()
        L_0x000e:
            r0 = 0
            r12.readbuf = r0
            return
        L_0x0012:
            r0 = 0
            com.sun.mail.iap.ByteArray r2 = r12.readbuf
            if (r2 != 0) goto L_0x0022
            com.sun.mail.iap.ByteArray r2 = new com.sun.mail.iap.ByteArray
            int r3 = r12.blksize
            int r3 = r3 + 64
            r2.<init>(r3)
            r12.readbuf = r2
        L_0x0022:
            com.sun.mail.imap.IMAPMessage r2 = r12.msg
            java.lang.Object r2 = r2.getMessageCacheLock()
            monitor-enter(r2)
            com.sun.mail.imap.IMAPMessage r3 = r12.msg     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            com.sun.mail.imap.protocol.IMAPProtocol r4 = r3.getProtocol()     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            com.sun.mail.imap.IMAPMessage r3 = r12.msg     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            boolean r3 = r3.isExpunged()     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            if (r3 != 0) goto L_0x00a4
            com.sun.mail.imap.IMAPMessage r3 = r12.msg     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            int r5 = r3.getSequenceNumber()     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            int r3 = r12.blksize     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            r6 = r3
            int r7 = r12.max     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            if (r7 == r1) goto L_0x004d
            int r1 = r12.pos     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            int r3 = r3 + r1
            if (r3 <= r7) goto L_0x004d
            int r6 = r7 - r1
            r1 = r6
            goto L_0x004e
        L_0x004d:
            r1 = r6
        L_0x004e:
            boolean r3 = r12.peek     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            if (r3 == 0) goto L_0x005f
            java.lang.String r6 = r12.section     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            int r7 = r12.pos     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            com.sun.mail.iap.ByteArray r9 = r12.readbuf     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            r8 = r1
            com.sun.mail.imap.protocol.BODY r3 = r4.peekBody(r5, r6, r7, r8, r9)     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            r0 = r3
            goto L_0x006d
        L_0x005f:
            java.lang.String r8 = r12.section     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            int r9 = r12.pos     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            com.sun.mail.iap.ByteArray r11 = r12.readbuf     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            r6 = r4
            r7 = r5
            r10 = r1
            com.sun.mail.imap.protocol.BODY r3 = r6.fetchBody(r7, r8, r9, r10, r11)     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            r0 = r3
        L_0x006d:
            if (r0 == 0) goto L_0x0099
            com.sun.mail.iap.ByteArray r1 = r0.getByteArray()     // Catch:{ all -> 0x00ac }
            r3 = r1
            if (r1 == 0) goto L_0x0099
            monitor-exit(r2)     // Catch:{ all -> 0x00ac }
            int r1 = r12.pos
            if (r1 != 0) goto L_0x007e
            r12.checkSeen()
        L_0x007e:
            byte[] r1 = r3.getBytes()
            r12.buf = r1
            int r1 = r3.getStart()
            r12.bufpos = r1
            int r1 = r3.getCount()
            int r2 = r12.bufpos
            int r2 = r2 + r1
            r12.bufcount = r2
            int r2 = r12.pos
            int r2 = r2 + r1
            r12.pos = r2
            return
        L_0x0099:
            r12.forceCheckExpunged()     // Catch:{ all -> 0x00ac }
            java.io.IOException r1 = new java.io.IOException     // Catch:{ all -> 0x00ac }
            java.lang.String r3 = "No content"
            r1.<init>(r3)     // Catch:{ all -> 0x00ac }
            throw r1     // Catch:{ all -> 0x00ac }
        L_0x00a4:
            com.sun.mail.util.MessageRemovedIOException r1 = new com.sun.mail.util.MessageRemovedIOException     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            java.lang.String r3 = "No content for expunged message"
            r1.<init>(r3)     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
            throw r1     // Catch:{ ProtocolException -> 0x00bd, FolderClosedException -> 0x00ae }
        L_0x00ac:
            r1 = move-exception
            goto L_0x00cb
        L_0x00ae:
            r1 = move-exception
            com.sun.mail.util.FolderClosedIOException r3 = new com.sun.mail.util.FolderClosedIOException     // Catch:{ all -> 0x00ac }
            javax.mail.Folder r4 = r1.getFolder()     // Catch:{ all -> 0x00ac }
            java.lang.String r5 = r1.getMessage()     // Catch:{ all -> 0x00ac }
            r3.<init>(r4, r5)     // Catch:{ all -> 0x00ac }
            throw r3     // Catch:{ all -> 0x00ac }
        L_0x00bd:
            r1 = move-exception
            r12.forceCheckExpunged()     // Catch:{ all -> 0x00ac }
            java.io.IOException r3 = new java.io.IOException     // Catch:{ all -> 0x00ac }
            java.lang.String r4 = r1.getMessage()     // Catch:{ all -> 0x00ac }
            r3.<init>(r4)     // Catch:{ all -> 0x00ac }
            throw r3     // Catch:{ all -> 0x00ac }
        L_0x00cb:
            monitor-exit(r2)     // Catch:{ all -> 0x00ac }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPInputStream.fill():void");
    }

    public synchronized int read() throws IOException {
        if (this.bufpos >= this.bufcount) {
            fill();
            if (this.bufpos >= this.bufcount) {
                return -1;
            }
        }
        byte[] bArr = this.buf;
        int i = this.bufpos;
        this.bufpos = i + 1;
        return bArr[i] & 255;
    }

    public synchronized int read(byte[] b, int off, int len) throws IOException {
        int avail = this.bufcount - this.bufpos;
        if (avail <= 0) {
            fill();
            avail = this.bufcount - this.bufpos;
            if (avail <= 0) {
                return -1;
            }
        }
        int cnt = avail < len ? avail : len;
        System.arraycopy(this.buf, this.bufpos, b, off, cnt);
        this.bufpos += cnt;
        return cnt;
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public synchronized int available() throws IOException {
        return this.bufcount - this.bufpos;
    }

    private void checkSeen() {
        if (!this.peek) {
            try {
                Folder f = this.msg.getFolder();
                if (f != null && f.getMode() != 1 && !this.msg.isSet(Flags.Flag.SEEN)) {
                    this.msg.setFlag(Flags.Flag.SEEN, true);
                }
            } catch (MessagingException e) {
            }
        }
    }
}
