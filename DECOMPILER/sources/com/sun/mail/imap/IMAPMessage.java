package com.sun.mail.imap;

import com.sun.mail.iap.ConnectionException;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.protocol.BODY;
import com.sun.mail.imap.protocol.BODYSTRUCTURE;
import com.sun.mail.imap.protocol.ENVELOPE;
import com.sun.mail.imap.protocol.FetchResponse;
import com.sun.mail.imap.protocol.IMAPProtocol;
import com.sun.mail.imap.protocol.INTERNALDATE;
import com.sun.mail.imap.protocol.Item;
import com.sun.mail.imap.protocol.RFC822DATA;
import com.sun.mail.imap.protocol.RFC822SIZE;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.IllegalWriteException;
import javax.mail.Message;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

public class IMAPMessage extends MimeMessage {
    private static String EnvelopeCmd = "ENVELOPE INTERNALDATE RFC822.SIZE";
    protected BODYSTRUCTURE bs;
    private String description;
    protected ENVELOPE envelope;
    private boolean headersLoaded = false;
    private Hashtable loadedHeaders;
    private boolean peek;
    private Date receivedDate;
    protected String sectionId;
    private int seqnum;
    /* access modifiers changed from: private */
    public int size = -1;
    private String subject;
    private String type;
    private long uid = -1;

    protected IMAPMessage(IMAPFolder folder, int msgnum, int seqnum2) {
        super((Folder) folder, msgnum);
        this.seqnum = seqnum2;
        this.flags = null;
    }

    protected IMAPMessage(Session session) {
        super(session);
    }

    /* access modifiers changed from: protected */
    public IMAPProtocol getProtocol() throws ProtocolException, FolderClosedException {
        ((IMAPFolder) this.folder).waitIfIdle();
        IMAPProtocol p = ((IMAPFolder) this.folder).protocol;
        if (p != null) {
            return p;
        }
        throw new FolderClosedException(this.folder);
    }

    /* access modifiers changed from: protected */
    public boolean isREV1() throws FolderClosedException {
        IMAPProtocol p = ((IMAPFolder) this.folder).protocol;
        if (p != null) {
            return p.isREV1();
        }
        throw new FolderClosedException(this.folder);
    }

    /* access modifiers changed from: protected */
    public Object getMessageCacheLock() {
        return ((IMAPFolder) this.folder).messageCacheLock;
    }

    /* access modifiers changed from: protected */
    public int getSequenceNumber() {
        return this.seqnum;
    }

    /* access modifiers changed from: protected */
    public void setSequenceNumber(int seqnum2) {
        this.seqnum = seqnum2;
    }

    /* access modifiers changed from: protected */
    public void setMessageNumber(int msgnum) {
        super.setMessageNumber(msgnum);
    }

    /* access modifiers changed from: protected */
    public long getUID() {
        return this.uid;
    }

    /* access modifiers changed from: protected */
    public void setUID(long uid2) {
        this.uid = uid2;
    }

    /* access modifiers changed from: protected */
    public void setExpunged(boolean set) {
        super.setExpunged(set);
        this.seqnum = -1;
    }

    /* access modifiers changed from: protected */
    public void checkExpunged() throws MessageRemovedException {
        if (this.expunged) {
            throw new MessageRemovedException();
        }
    }

    /* access modifiers changed from: protected */
    public void forceCheckExpunged() throws MessageRemovedException, FolderClosedException {
        synchronized (getMessageCacheLock()) {
            try {
                getProtocol().noop();
            } catch (ConnectionException cex) {
                throw new FolderClosedException(this.folder, cex.getMessage());
            } catch (ProtocolException e) {
            }
        }
        if (this.expunged) {
            throw new MessageRemovedException();
        }
    }

    /* access modifiers changed from: protected */
    public int getFetchBlockSize() {
        return ((IMAPStore) this.folder.getStore()).getFetchBlockSize();
    }

    public Address[] getFrom() throws MessagingException {
        checkExpunged();
        loadEnvelope();
        return aaclone(this.envelope.from);
    }

    public void setFrom(Address address) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    public void addFrom(Address[] addresses) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    public Address getSender() throws MessagingException {
        checkExpunged();
        loadEnvelope();
        if (this.envelope.sender != null) {
            return this.envelope.sender[0];
        }
        return null;
    }

    public void setSender(Address address) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    public Address[] getRecipients(Message.RecipientType type2) throws MessagingException {
        checkExpunged();
        loadEnvelope();
        if (type2 == Message.RecipientType.TO) {
            return aaclone(this.envelope.to);
        }
        if (type2 == Message.RecipientType.CC) {
            return aaclone(this.envelope.cc);
        }
        if (type2 == Message.RecipientType.BCC) {
            return aaclone(this.envelope.bcc);
        }
        return super.getRecipients(type2);
    }

    public void setRecipients(Message.RecipientType type2, Address[] addresses) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    public void addRecipients(Message.RecipientType type2, Address[] addresses) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    public Address[] getReplyTo() throws MessagingException {
        checkExpunged();
        loadEnvelope();
        return aaclone(this.envelope.replyTo);
    }

    public void setReplyTo(Address[] addresses) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    public String getSubject() throws MessagingException {
        checkExpunged();
        String str = this.subject;
        if (str != null) {
            return str;
        }
        loadEnvelope();
        if (this.envelope.subject == null) {
            return null;
        }
        try {
            this.subject = MimeUtility.decodeText(this.envelope.subject);
        } catch (UnsupportedEncodingException e) {
            this.subject = this.envelope.subject;
        }
        return this.subject;
    }

    public void setSubject(String subject2, String charset) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    public Date getSentDate() throws MessagingException {
        checkExpunged();
        loadEnvelope();
        if (this.envelope.date == null) {
            return null;
        }
        return new Date(this.envelope.date.getTime());
    }

    public void setSentDate(Date d) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    public Date getReceivedDate() throws MessagingException {
        checkExpunged();
        loadEnvelope();
        if (this.receivedDate == null) {
            return null;
        }
        return new Date(this.receivedDate.getTime());
    }

    public int getSize() throws MessagingException {
        checkExpunged();
        if (this.size == -1) {
            loadEnvelope();
        }
        return this.size;
    }

    public int getLineCount() throws MessagingException {
        checkExpunged();
        loadBODYSTRUCTURE();
        return this.bs.lines;
    }

    public String[] getContentLanguage() throws MessagingException {
        checkExpunged();
        loadBODYSTRUCTURE();
        if (this.bs.language != null) {
            return (String[]) this.bs.language.clone();
        }
        return null;
    }

    public void setContentLanguage(String[] languages) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    public String getInReplyTo() throws MessagingException {
        checkExpunged();
        loadEnvelope();
        return this.envelope.inReplyTo;
    }

    public String getContentType() throws MessagingException {
        checkExpunged();
        if (this.type == null) {
            loadBODYSTRUCTURE();
            this.type = new ContentType(this.bs.type, this.bs.subtype, this.bs.cParams).toString();
        }
        return this.type;
    }

    public String getDisposition() throws MessagingException {
        checkExpunged();
        loadBODYSTRUCTURE();
        return this.bs.disposition;
    }

    public void setDisposition(String disposition) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    public String getEncoding() throws MessagingException {
        checkExpunged();
        loadBODYSTRUCTURE();
        return this.bs.encoding;
    }

    public String getContentID() throws MessagingException {
        checkExpunged();
        loadBODYSTRUCTURE();
        return this.bs.id;
    }

    public void setContentID(String cid) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    public String getContentMD5() throws MessagingException {
        checkExpunged();
        loadBODYSTRUCTURE();
        return this.bs.md5;
    }

    public void setContentMD5(String md5) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    public String getDescription() throws MessagingException {
        checkExpunged();
        String str = this.description;
        if (str != null) {
            return str;
        }
        loadBODYSTRUCTURE();
        if (this.bs.description == null) {
            return null;
        }
        try {
            this.description = MimeUtility.decodeText(this.bs.description);
        } catch (UnsupportedEncodingException e) {
            this.description = this.bs.description;
        }
        return this.description;
    }

    public void setDescription(String description2, String charset) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    public String getMessageID() throws MessagingException {
        checkExpunged();
        loadEnvelope();
        return this.envelope.messageId;
    }

    public String getFileName() throws MessagingException {
        checkExpunged();
        String filename = null;
        loadBODYSTRUCTURE();
        if (this.bs.dParams != null) {
            filename = this.bs.dParams.get("filename");
        }
        if (filename != null || this.bs.cParams == null) {
            return filename;
        }
        return this.bs.cParams.get("name");
    }

    public void setFileName(String filename) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0070, code lost:
        if (r0 == null) goto L_0x0073;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0072, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x007a, code lost:
        throw new javax.mail.MessagingException("No content");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.io.InputStream getContentStream() throws javax.mail.MessagingException {
        /*
            r8 = this;
            r0 = 0
            boolean r1 = r8.getPeek()
            java.lang.Object r2 = r8.getMessageCacheLock()
            monitor-enter(r2)
            com.sun.mail.imap.protocol.IMAPProtocol r3 = r8.getProtocol()     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
            r8.checkExpunged()     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
            boolean r4 = r3.isREV1()     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
            if (r4 == 0) goto L_0x0031
            int r4 = r8.getFetchBlockSize()     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
            r5 = -1
            if (r4 == r5) goto L_0x0031
            com.sun.mail.imap.IMAPInputStream r4 = new com.sun.mail.imap.IMAPInputStream     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
            java.lang.String r6 = "TEXT"
            java.lang.String r6 = r8.toSection(r6)     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
            com.sun.mail.imap.protocol.BODYSTRUCTURE r7 = r8.bs     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
            if (r7 == 0) goto L_0x002c
            int r5 = r7.size     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
        L_0x002c:
            r4.<init>(r8, r6, r5, r1)     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
            monitor-exit(r2)     // Catch:{ all -> 0x007b }
            return r4
        L_0x0031:
            boolean r4 = r3.isREV1()     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
            if (r4 == 0) goto L_0x005e
            if (r1 == 0) goto L_0x0048
            int r4 = r8.getSequenceNumber()     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
            java.lang.String r5 = "TEXT"
            java.lang.String r5 = r8.toSection(r5)     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
            com.sun.mail.imap.protocol.BODY r4 = r3.peekBody(r4, r5)     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
            goto L_0x0056
        L_0x0048:
            int r4 = r8.getSequenceNumber()     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
            java.lang.String r5 = "TEXT"
            java.lang.String r5 = r8.toSection(r5)     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
            com.sun.mail.imap.protocol.BODY r4 = r3.fetchBody(r4, r5)     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
        L_0x0056:
            if (r4 == 0) goto L_0x006f
            java.io.ByteArrayInputStream r5 = r4.getByteArrayInputStream()     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
            r0 = r5
            goto L_0x006f
        L_0x005e:
            int r4 = r8.getSequenceNumber()     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
            java.lang.String r5 = "TEXT"
            com.sun.mail.imap.protocol.RFC822DATA r4 = r3.fetchRFC822(r4, r5)     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
            if (r4 == 0) goto L_0x006f
            java.io.ByteArrayInputStream r5 = r4.getByteArrayInputStream()     // Catch:{ ConnectionException -> 0x008b, ProtocolException -> 0x007d }
            r0 = r5
        L_0x006f:
            monitor-exit(r2)     // Catch:{ all -> 0x007b }
            if (r0 == 0) goto L_0x0073
            return r0
        L_0x0073:
            javax.mail.MessagingException r2 = new javax.mail.MessagingException
            java.lang.String r3 = "No content"
            r2.<init>(r3)
            throw r2
        L_0x007b:
            r3 = move-exception
            goto L_0x0098
        L_0x007d:
            r3 = move-exception
            r8.forceCheckExpunged()     // Catch:{ all -> 0x007b }
            javax.mail.MessagingException r4 = new javax.mail.MessagingException     // Catch:{ all -> 0x007b }
            java.lang.String r5 = r3.getMessage()     // Catch:{ all -> 0x007b }
            r4.<init>(r5, r3)     // Catch:{ all -> 0x007b }
            throw r4     // Catch:{ all -> 0x007b }
        L_0x008b:
            r3 = move-exception
            javax.mail.FolderClosedException r4 = new javax.mail.FolderClosedException     // Catch:{ all -> 0x007b }
            javax.mail.Folder r5 = r8.folder     // Catch:{ all -> 0x007b }
            java.lang.String r6 = r3.getMessage()     // Catch:{ all -> 0x007b }
            r4.<init>(r5, r6)     // Catch:{ all -> 0x007b }
            throw r4     // Catch:{ all -> 0x007b }
        L_0x0098:
            monitor-exit(r2)     // Catch:{ all -> 0x007b }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPMessage.getContentStream():java.io.InputStream");
    }

    public synchronized DataHandler getDataHandler() throws MessagingException {
        checkExpunged();
        if (this.dh == null) {
            loadBODYSTRUCTURE();
            if (this.type == null) {
                this.type = new ContentType(this.bs.type, this.bs.subtype, this.bs.cParams).toString();
            }
            if (this.bs.isMulti()) {
                this.dh = new DataHandler((DataSource) new IMAPMultipartDataSource(this, this.bs.bodies, this.sectionId, this));
            } else if (this.bs.isNested() && isREV1()) {
                this.dh = new DataHandler(new IMAPNestedMessage(this, this.bs.bodies[0], this.bs.envelope, this.sectionId == null ? "1" : String.valueOf(this.sectionId) + ".1"), this.type);
            }
        }
        return super.getDataHandler();
    }

    public void setDataHandler(DataHandler content) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    public void writeTo(OutputStream os) throws IOException, MessagingException {
        BODY b;
        InputStream is = null;
        boolean pk = getPeek();
        synchronized (getMessageCacheLock()) {
            try {
                IMAPProtocol p = getProtocol();
                checkExpunged();
                if (p.isREV1()) {
                    if (pk) {
                        b = p.peekBody(getSequenceNumber(), this.sectionId);
                    } else {
                        b = p.fetchBody(getSequenceNumber(), this.sectionId);
                    }
                    if (b != null) {
                        is = b.getByteArrayInputStream();
                    }
                } else {
                    RFC822DATA rd = p.fetchRFC822(getSequenceNumber(), (String) null);
                    if (rd != null) {
                        is = rd.getByteArrayInputStream();
                    }
                }
            } catch (ConnectionException cex) {
                throw new FolderClosedException(this.folder, cex.getMessage());
            } catch (ProtocolException pex) {
                forceCheckExpunged();
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
        if (is != null) {
            byte[] bytes = new byte[1024];
            while (true) {
                int read = is.read(bytes);
                int count = read;
                if (read != -1) {
                    os.write(bytes, 0, count);
                } else {
                    return;
                }
            }
        } else {
            throw new MessagingException("No content");
        }
    }

    public String[] getHeader(String name) throws MessagingException {
        checkExpunged();
        if (isHeaderLoaded(name)) {
            return this.headers.getHeader(name);
        }
        InputStream is = null;
        synchronized (getMessageCacheLock()) {
            try {
                IMAPProtocol p = getProtocol();
                checkExpunged();
                if (p.isREV1()) {
                    BODY b = p.peekBody(getSequenceNumber(), toSection("HEADER.FIELDS (" + name + ")"));
                    if (b != null) {
                        is = b.getByteArrayInputStream();
                    }
                } else {
                    RFC822DATA rd = p.fetchRFC822(getSequenceNumber(), "HEADER.LINES (" + name + ")");
                    if (rd != null) {
                        is = rd.getByteArrayInputStream();
                    }
                }
            } catch (ConnectionException cex) {
                throw new FolderClosedException(this.folder, cex.getMessage());
            } catch (ProtocolException pex) {
                forceCheckExpunged();
                throw new MessagingException(pex.getMessage(), pex);
            }
        }
        if (is == null) {
            return null;
        }
        if (this.headers == null) {
            this.headers = new InternetHeaders();
        }
        this.headers.load(is);
        setHeaderLoaded(name);
        return this.headers.getHeader(name);
    }

    public String getHeader(String name, String delimiter) throws MessagingException {
        checkExpunged();
        if (getHeader(name) == null) {
            return null;
        }
        return this.headers.getHeader(name, delimiter);
    }

    public void setHeader(String name, String value) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    public void addHeader(String name, String value) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    public void removeHeader(String name) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    public Enumeration getAllHeaders() throws MessagingException {
        checkExpunged();
        loadHeaders();
        return super.getAllHeaders();
    }

    public Enumeration getMatchingHeaders(String[] names) throws MessagingException {
        checkExpunged();
        loadHeaders();
        return super.getMatchingHeaders(names);
    }

    public Enumeration getNonMatchingHeaders(String[] names) throws MessagingException {
        checkExpunged();
        loadHeaders();
        return super.getNonMatchingHeaders(names);
    }

    public void addHeaderLine(String line) throws MessagingException {
        throw new IllegalWriteException("IMAPMessage is read-only");
    }

    public Enumeration getAllHeaderLines() throws MessagingException {
        checkExpunged();
        loadHeaders();
        return super.getAllHeaderLines();
    }

    public Enumeration getMatchingHeaderLines(String[] names) throws MessagingException {
        checkExpunged();
        loadHeaders();
        return super.getMatchingHeaderLines(names);
    }

    public Enumeration getNonMatchingHeaderLines(String[] names) throws MessagingException {
        checkExpunged();
        loadHeaders();
        return super.getNonMatchingHeaderLines(names);
    }

    public synchronized Flags getFlags() throws MessagingException {
        checkExpunged();
        loadFlags();
        return super.getFlags();
    }

    public synchronized boolean isSet(Flags.Flag flag) throws MessagingException {
        checkExpunged();
        loadFlags();
        return super.isSet(flag);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0033, code lost:
        r1 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void setFlags(javax.mail.Flags r6, boolean r7) throws javax.mail.MessagingException {
        /*
            r5 = this;
            monitor-enter(r5)
            java.lang.Object r0 = r5.getMessageCacheLock()     // Catch:{ all -> 0x0035 }
            monitor-enter(r0)     // Catch:{ all -> 0x0035 }
            com.sun.mail.imap.protocol.IMAPProtocol r1 = r5.getProtocol()     // Catch:{ ConnectionException -> 0x0024, ProtocolException -> 0x0019 }
            r5.checkExpunged()     // Catch:{ ConnectionException -> 0x0024, ProtocolException -> 0x0019 }
            int r2 = r5.getSequenceNumber()     // Catch:{ ConnectionException -> 0x0024, ProtocolException -> 0x0019 }
            r1.storeFlags((int) r2, (javax.mail.Flags) r6, (boolean) r7)     // Catch:{ ConnectionException -> 0x0024, ProtocolException -> 0x0019 }
            monitor-exit(r0)     // Catch:{ all -> 0x0017 }
            monitor-exit(r5)
            return
        L_0x0017:
            r1 = move-exception
            goto L_0x0031
        L_0x0019:
            r1 = move-exception
            javax.mail.MessagingException r2 = new javax.mail.MessagingException     // Catch:{ all -> 0x0017 }
            java.lang.String r3 = r1.getMessage()     // Catch:{ all -> 0x0017 }
            r2.<init>(r3, r1)     // Catch:{ all -> 0x0017 }
            throw r2     // Catch:{ all -> 0x0017 }
        L_0x0024:
            r1 = move-exception
            javax.mail.FolderClosedException r2 = new javax.mail.FolderClosedException     // Catch:{ all -> 0x0017 }
            javax.mail.Folder r3 = r5.folder     // Catch:{ all -> 0x0017 }
            java.lang.String r4 = r1.getMessage()     // Catch:{ all -> 0x0017 }
            r2.<init>(r3, r4)     // Catch:{ all -> 0x0017 }
            throw r2     // Catch:{ all -> 0x0017 }
        L_0x0031:
            monitor-exit(r0)     // Catch:{ all -> 0x0033 }
            throw r1     // Catch:{ all -> 0x0035 }
        L_0x0033:
            r1 = move-exception
            goto L_0x0031
        L_0x0035:
            r6 = move-exception
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPMessage.setFlags(javax.mail.Flags, boolean):void");
    }

    public synchronized void setPeek(boolean peek2) {
        this.peek = peek2;
    }

    public synchronized boolean getPeek() {
        return this.peek;
    }

    public synchronized void invalidateHeaders() {
        this.headersLoaded = false;
        this.loadedHeaders = null;
        this.envelope = null;
        this.bs = null;
        this.receivedDate = null;
        this.size = -1;
        this.type = null;
        this.subject = null;
        this.description = null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00bf, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00c0, code lost:
        r17 = r3;
        r19 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x00dc, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x00dd, code lost:
        r17 = r3;
        r19 = r5;
        r20 = r10;
        r21 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x00e7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x00f1, code lost:
        throw new javax.mail.MessagingException(r0.getMessage(), r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x010a, code lost:
        return;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:54:0x00bd, B:63:0x00d0, B:65:0x00d2] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x0264 A[Catch:{ all -> 0x0287, all -> 0x02a5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x0269 A[Catch:{ all -> 0x0287, all -> 0x02a5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00e7 A[ExcHandler: ProtocolException (r0v33 'pex' com.sun.mail.iap.ProtocolException A[CUSTOM_DECLARE]), Splitter:B:63:0x00d0] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00f2 A[Catch:{ ConnectionException -> 0x00dc, CommandFailedException -> 0x00f2, ProtocolException -> 0x00e7, all -> 0x00bf }, ExcHandler: CommandFailedException (e com.sun.mail.iap.CommandFailedException), Splitter:B:63:0x00d0] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void fetch(com.sun.mail.imap.IMAPFolder r25, javax.mail.Message[] r26, javax.mail.FetchProfile r27) throws javax.mail.MessagingException {
        /*
            r1 = r25
            r2 = r27
            java.lang.StringBuffer r0 = new java.lang.StringBuffer
            r0.<init>()
            r3 = r0
            r0 = 1
            r4 = 0
            javax.mail.FetchProfile$Item r5 = javax.mail.FetchProfile.Item.ENVELOPE
            boolean r5 = r2.contains((javax.mail.FetchProfile.Item) r5)
            if (r5 == 0) goto L_0x001a
            java.lang.String r5 = EnvelopeCmd
            r3.append(r5)
            r0 = 0
        L_0x001a:
            javax.mail.FetchProfile$Item r5 = javax.mail.FetchProfile.Item.FLAGS
            boolean r5 = r2.contains((javax.mail.FetchProfile.Item) r5)
            if (r5 == 0) goto L_0x002d
            if (r0 == 0) goto L_0x0027
            java.lang.String r5 = "FLAGS"
            goto L_0x0029
        L_0x0027:
            java.lang.String r5 = " FLAGS"
        L_0x0029:
            r3.append(r5)
            r0 = 0
        L_0x002d:
            javax.mail.FetchProfile$Item r5 = javax.mail.FetchProfile.Item.CONTENT_INFO
            boolean r5 = r2.contains((javax.mail.FetchProfile.Item) r5)
            if (r5 == 0) goto L_0x0040
            if (r0 == 0) goto L_0x003a
            java.lang.String r5 = "BODYSTRUCTURE"
            goto L_0x003c
        L_0x003a:
            java.lang.String r5 = " BODYSTRUCTURE"
        L_0x003c:
            r3.append(r5)
            r0 = 0
        L_0x0040:
            javax.mail.UIDFolder$FetchProfileItem r5 = javax.mail.UIDFolder.FetchProfileItem.UID
            boolean r5 = r2.contains((javax.mail.FetchProfile.Item) r5)
            if (r5 == 0) goto L_0x0053
            if (r0 == 0) goto L_0x004d
            java.lang.String r5 = "UID"
            goto L_0x004f
        L_0x004d:
            java.lang.String r5 = " UID"
        L_0x004f:
            r3.append(r5)
            r0 = 0
        L_0x0053:
            com.sun.mail.imap.IMAPFolder$FetchProfileItem r5 = com.sun.mail.imap.IMAPFolder.FetchProfileItem.HEADERS
            boolean r5 = r2.contains((javax.mail.FetchProfile.Item) r5)
            if (r5 == 0) goto L_0x007a
            r4 = 1
            com.sun.mail.imap.protocol.IMAPProtocol r5 = r1.protocol
            boolean r5 = r5.isREV1()
            if (r5 == 0) goto L_0x006f
            if (r0 == 0) goto L_0x0069
            java.lang.String r5 = "BODY.PEEK[HEADER]"
            goto L_0x006b
        L_0x0069:
            java.lang.String r5 = " BODY.PEEK[HEADER]"
        L_0x006b:
            r3.append(r5)
            goto L_0x0079
        L_0x006f:
            if (r0 == 0) goto L_0x0074
            java.lang.String r5 = "RFC822.HEADER"
            goto L_0x0076
        L_0x0074:
            java.lang.String r5 = " RFC822.HEADER"
        L_0x0076:
            r3.append(r5)
        L_0x0079:
            r0 = 0
        L_0x007a:
            com.sun.mail.imap.IMAPFolder$FetchProfileItem r5 = com.sun.mail.imap.IMAPFolder.FetchProfileItem.SIZE
            boolean r5 = r2.contains((javax.mail.FetchProfile.Item) r5)
            if (r5 == 0) goto L_0x008f
            if (r0 == 0) goto L_0x0087
            java.lang.String r5 = "RFC822.SIZE"
            goto L_0x0089
        L_0x0087:
            java.lang.String r5 = " RFC822.SIZE"
        L_0x0089:
            r3.append(r5)
            r0 = 0
            r5 = r0
            goto L_0x0090
        L_0x008f:
            r5 = r0
        L_0x0090:
            r0 = 0
            r6 = r0
            java.lang.String[] r6 = (java.lang.String[]) r6
            if (r4 != 0) goto L_0x00ad
            java.lang.String[] r6 = r27.getHeaderNames()
            int r7 = r6.length
            if (r7 <= 0) goto L_0x00ad
            if (r5 != 0) goto L_0x00a4
            java.lang.String r7 = " "
            r3.append(r7)
        L_0x00a4:
            com.sun.mail.imap.protocol.IMAPProtocol r7 = r1.protocol
            java.lang.String r7 = craftHeaderCmd(r7, r6)
            r3.append(r7)
        L_0x00ad:
            com.sun.mail.imap.IMAPMessage$1FetchProfileCondition r7 = new com.sun.mail.imap.IMAPMessage$1FetchProfileCondition
            r7.<init>(r2)
            java.lang.Object r8 = r1.messageCacheLock
            monitor-enter(r8)
            r9 = r26
            com.sun.mail.imap.protocol.MessageSet[] r10 = com.sun.mail.imap.Utility.toMessageSet(r9, r7)     // Catch:{ all -> 0x029e }
            if (r10 != 0) goto L_0x00c6
            monitor-exit(r8)     // Catch:{ all -> 0x00bf }
            return
        L_0x00bf:
            r0 = move-exception
            r17 = r3
            r19 = r5
            goto L_0x02a3
        L_0x00c6:
            r11 = r0
            com.sun.mail.iap.Response[] r11 = (com.sun.mail.iap.Response[]) r11     // Catch:{ all -> 0x029e }
            r11 = r0
            java.util.Vector r0 = new java.util.Vector     // Catch:{ all -> 0x029e }
            r0.<init>()     // Catch:{ all -> 0x029e }
            r12 = r0
            com.sun.mail.imap.protocol.IMAPProtocol r0 = r1.protocol     // Catch:{ ConnectionException -> 0x028b, CommandFailedException -> 0x00f2, ProtocolException -> 0x00e7 }
            java.lang.String r13 = r3.toString()     // Catch:{ ConnectionException -> 0x00dc, CommandFailedException -> 0x00f2, ProtocolException -> 0x00e7 }
            com.sun.mail.iap.Response[] r0 = r0.fetch((com.sun.mail.imap.protocol.MessageSet[]) r10, (java.lang.String) r13)     // Catch:{ ConnectionException -> 0x00dc, CommandFailedException -> 0x00f2, ProtocolException -> 0x00e7 }
            r11 = r0
            goto L_0x00f3
        L_0x00dc:
            r0 = move-exception
            r17 = r3
            r19 = r5
            r20 = r10
            r21 = r12
            goto L_0x0294
        L_0x00e7:
            r0 = move-exception
            javax.mail.MessagingException r13 = new javax.mail.MessagingException     // Catch:{ all -> 0x00bf }
            java.lang.String r14 = r0.getMessage()     // Catch:{ all -> 0x00bf }
            r13.<init>(r14, r0)     // Catch:{ all -> 0x00bf }
            throw r13     // Catch:{ all -> 0x00bf }
        L_0x00f2:
            r0 = move-exception
        L_0x00f3:
            if (r11 != 0) goto L_0x00f7
            monitor-exit(r8)     // Catch:{ all -> 0x00bf }
            return
        L_0x00f7:
            r0 = 0
        L_0x00f8:
            int r13 = r11.length     // Catch:{ all -> 0x029e }
            if (r0 < r13) goto L_0x010b
            int r0 = r12.size()     // Catch:{ all -> 0x00bf }
            if (r0 == 0) goto L_0x0109
            com.sun.mail.iap.Response[] r13 = new com.sun.mail.iap.Response[r0]     // Catch:{ all -> 0x00bf }
            r12.copyInto(r13)     // Catch:{ all -> 0x00bf }
            r1.handleResponses(r13)     // Catch:{ all -> 0x00bf }
        L_0x0109:
            monitor-exit(r8)     // Catch:{ all -> 0x00bf }
            return
        L_0x010b:
            r13 = r11[r0]     // Catch:{ all -> 0x029e }
            if (r13 != 0) goto L_0x0112
            r17 = r3
            goto L_0x0147
        L_0x0112:
            r13 = r11[r0]     // Catch:{ all -> 0x029e }
            boolean r13 = r13 instanceof com.sun.mail.imap.protocol.FetchResponse     // Catch:{ all -> 0x029e }
            if (r13 != 0) goto L_0x0120
            r13 = r11[r0]     // Catch:{ all -> 0x00bf }
            r12.addElement(r13)     // Catch:{ all -> 0x00bf }
            r17 = r3
            goto L_0x0147
        L_0x0120:
            r13 = r11[r0]     // Catch:{ all -> 0x029e }
            com.sun.mail.imap.protocol.FetchResponse r13 = (com.sun.mail.imap.protocol.FetchResponse) r13     // Catch:{ all -> 0x029e }
            int r14 = r13.getNumber()     // Catch:{ all -> 0x029e }
            com.sun.mail.imap.IMAPMessage r14 = r1.getMessageBySeqNumber(r14)     // Catch:{ all -> 0x029e }
            int r15 = r13.getItemCount()     // Catch:{ all -> 0x029e }
            r16 = 0
            r17 = 0
            r24 = r17
            r17 = r3
            r3 = r24
        L_0x013a:
            if (r3 < r15) goto L_0x014c
            if (r16 == 0) goto L_0x0147
            r12.addElement(r13)     // Catch:{ all -> 0x0142 }
            goto L_0x0147
        L_0x0142:
            r0 = move-exception
            r19 = r5
            goto L_0x02a3
        L_0x0147:
            int r0 = r0 + 1
            r3 = r17
            goto L_0x00f8
        L_0x014c:
            com.sun.mail.imap.protocol.Item r18 = r13.getItem((int) r3)     // Catch:{ all -> 0x0287 }
            r19 = r18
            r18 = r0
            r0 = r19
            r19 = r5
            boolean r5 = r0 instanceof javax.mail.Flags     // Catch:{ all -> 0x02a5 }
            if (r5 == 0) goto L_0x017f
            javax.mail.FetchProfile$Item r5 = javax.mail.FetchProfile.Item.FLAGS     // Catch:{ all -> 0x02a5 }
            boolean r5 = r2.contains((javax.mail.FetchProfile.Item) r5)     // Catch:{ all -> 0x02a5 }
            if (r5 == 0) goto L_0x0174
            if (r14 != 0) goto L_0x0167
            goto L_0x0174
        L_0x0167:
            r5 = r0
            javax.mail.Flags r5 = (javax.mail.Flags) r5     // Catch:{ all -> 0x02a5 }
            r14.flags = r5     // Catch:{ all -> 0x02a5 }
            r20 = r10
            r22 = r11
            r21 = r12
            goto L_0x026d
        L_0x0174:
            r5 = 1
            r16 = r5
            r20 = r10
            r22 = r11
            r21 = r12
            goto L_0x026d
        L_0x017f:
            boolean r5 = r0 instanceof com.sun.mail.imap.protocol.ENVELOPE     // Catch:{ all -> 0x02a5 }
            if (r5 == 0) goto L_0x0190
            r5 = r0
            com.sun.mail.imap.protocol.ENVELOPE r5 = (com.sun.mail.imap.protocol.ENVELOPE) r5     // Catch:{ all -> 0x02a5 }
            r14.envelope = r5     // Catch:{ all -> 0x02a5 }
            r20 = r10
            r22 = r11
            r21 = r12
            goto L_0x026d
        L_0x0190:
            boolean r5 = r0 instanceof com.sun.mail.imap.protocol.INTERNALDATE     // Catch:{ all -> 0x02a5 }
            if (r5 == 0) goto L_0x01a5
            r5 = r0
            com.sun.mail.imap.protocol.INTERNALDATE r5 = (com.sun.mail.imap.protocol.INTERNALDATE) r5     // Catch:{ all -> 0x02a5 }
            java.util.Date r5 = r5.getDate()     // Catch:{ all -> 0x02a5 }
            r14.receivedDate = r5     // Catch:{ all -> 0x02a5 }
            r20 = r10
            r22 = r11
            r21 = r12
            goto L_0x026d
        L_0x01a5:
            boolean r5 = r0 instanceof com.sun.mail.imap.protocol.RFC822SIZE     // Catch:{ all -> 0x02a5 }
            if (r5 == 0) goto L_0x01b8
            r5 = r0
            com.sun.mail.imap.protocol.RFC822SIZE r5 = (com.sun.mail.imap.protocol.RFC822SIZE) r5     // Catch:{ all -> 0x02a5 }
            int r5 = r5.size     // Catch:{ all -> 0x02a5 }
            r14.size = r5     // Catch:{ all -> 0x02a5 }
            r20 = r10
            r22 = r11
            r21 = r12
            goto L_0x026d
        L_0x01b8:
            boolean r5 = r0 instanceof com.sun.mail.imap.protocol.BODYSTRUCTURE     // Catch:{ all -> 0x02a5 }
            if (r5 == 0) goto L_0x01c9
            r5 = r0
            com.sun.mail.imap.protocol.BODYSTRUCTURE r5 = (com.sun.mail.imap.protocol.BODYSTRUCTURE) r5     // Catch:{ all -> 0x02a5 }
            r14.bs = r5     // Catch:{ all -> 0x02a5 }
            r20 = r10
            r22 = r11
            r21 = r12
            goto L_0x026d
        L_0x01c9:
            boolean r5 = r0 instanceof com.sun.mail.imap.protocol.UID     // Catch:{ all -> 0x02a5 }
            if (r5 == 0) goto L_0x01f3
            r5 = r0
            com.sun.mail.imap.protocol.UID r5 = (com.sun.mail.imap.protocol.UID) r5     // Catch:{ all -> 0x02a5 }
            r20 = r10
            long r9 = r5.uid     // Catch:{ all -> 0x02a5 }
            r14.uid = r9     // Catch:{ all -> 0x02a5 }
            java.util.Hashtable r9 = r1.uidTable     // Catch:{ all -> 0x02a5 }
            if (r9 != 0) goto L_0x01e1
            java.util.Hashtable r9 = new java.util.Hashtable     // Catch:{ all -> 0x02a5 }
            r9.<init>()     // Catch:{ all -> 0x02a5 }
            r1.uidTable = r9     // Catch:{ all -> 0x02a5 }
        L_0x01e1:
            java.util.Hashtable r9 = r1.uidTable     // Catch:{ all -> 0x02a5 }
            java.lang.Long r10 = new java.lang.Long     // Catch:{ all -> 0x02a5 }
            r22 = r11
            r21 = r12
            long r11 = r5.uid     // Catch:{ all -> 0x02a5 }
            r10.<init>(r11)     // Catch:{ all -> 0x02a5 }
            r9.put(r10, r14)     // Catch:{ all -> 0x02a5 }
            goto L_0x026d
        L_0x01f3:
            r20 = r10
            r22 = r11
            r21 = r12
            boolean r5 = r0 instanceof com.sun.mail.imap.protocol.RFC822DATA     // Catch:{ all -> 0x02a5 }
            if (r5 != 0) goto L_0x0201
            boolean r5 = r0 instanceof com.sun.mail.imap.protocol.BODY     // Catch:{ all -> 0x02a5 }
            if (r5 == 0) goto L_0x026d
        L_0x0201:
            boolean r5 = r0 instanceof com.sun.mail.imap.protocol.RFC822DATA     // Catch:{ all -> 0x02a5 }
            if (r5 == 0) goto L_0x020e
            r5 = r0
            com.sun.mail.imap.protocol.RFC822DATA r5 = (com.sun.mail.imap.protocol.RFC822DATA) r5     // Catch:{ all -> 0x02a5 }
            java.io.ByteArrayInputStream r5 = r5.getByteArrayInputStream()     // Catch:{ all -> 0x02a5 }
            goto L_0x0216
        L_0x020e:
            r5 = r0
            com.sun.mail.imap.protocol.BODY r5 = (com.sun.mail.imap.protocol.BODY) r5     // Catch:{ all -> 0x02a5 }
            java.io.ByteArrayInputStream r5 = r5.getByteArrayInputStream()     // Catch:{ all -> 0x02a5 }
        L_0x0216:
            javax.mail.internet.InternetHeaders r9 = new javax.mail.internet.InternetHeaders     // Catch:{ all -> 0x02a5 }
            r9.<init>()     // Catch:{ all -> 0x02a5 }
            r9.load(r5)     // Catch:{ all -> 0x02a5 }
            javax.mail.internet.InternetHeaders r10 = r14.headers     // Catch:{ all -> 0x02a5 }
            if (r10 == 0) goto L_0x025e
            if (r4 == 0) goto L_0x0227
            r23 = r0
            goto L_0x0260
        L_0x0227:
            java.util.Enumeration r10 = r9.getAllHeaders()     // Catch:{ all -> 0x02a5 }
        L_0x022c:
            boolean r11 = r10.hasMoreElements()     // Catch:{ all -> 0x02a5 }
            if (r11 != 0) goto L_0x0235
            r23 = r0
            goto L_0x0262
        L_0x0235:
            java.lang.Object r11 = r10.nextElement()     // Catch:{ all -> 0x02a5 }
            javax.mail.Header r11 = (javax.mail.Header) r11     // Catch:{ all -> 0x02a5 }
            java.lang.String r12 = r11.getName()     // Catch:{ all -> 0x02a5 }
            boolean r12 = r14.isHeaderLoaded(r12)     // Catch:{ all -> 0x02a5 }
            if (r12 != 0) goto L_0x0259
            javax.mail.internet.InternetHeaders r12 = r14.headers     // Catch:{ all -> 0x02a5 }
            r23 = r0
            java.lang.String r0 = r11.getName()     // Catch:{ all -> 0x02a5 }
            java.lang.String r2 = r11.getValue()     // Catch:{ all -> 0x02a5 }
            r12.addHeader(r0, r2)     // Catch:{ all -> 0x02a5 }
            r2 = r27
            r0 = r23
            goto L_0x022c
        L_0x0259:
            r23 = r0
            r2 = r27
            goto L_0x022c
        L_0x025e:
            r23 = r0
        L_0x0260:
            r14.headers = r9     // Catch:{ all -> 0x02a5 }
        L_0x0262:
            if (r4 == 0) goto L_0x0269
            r0 = 1
            r14.setHeadersLoaded(r0)     // Catch:{ all -> 0x02a5 }
            goto L_0x026d
        L_0x0269:
            r0 = 0
        L_0x026a:
            int r2 = r6.length     // Catch:{ all -> 0x02a5 }
            if (r0 < r2) goto L_0x027f
        L_0x026d:
            int r3 = r3 + 1
            r9 = r26
            r2 = r27
            r0 = r18
            r5 = r19
            r10 = r20
            r12 = r21
            r11 = r22
            goto L_0x013a
        L_0x027f:
            r2 = r6[r0]     // Catch:{ all -> 0x02a5 }
            r14.setHeaderLoaded(r2)     // Catch:{ all -> 0x02a5 }
            int r0 = r0 + 1
            goto L_0x026a
        L_0x0287:
            r0 = move-exception
            r19 = r5
            goto L_0x02a3
        L_0x028b:
            r0 = move-exception
            r17 = r3
            r19 = r5
            r20 = r10
            r21 = r12
        L_0x0294:
            javax.mail.FolderClosedException r2 = new javax.mail.FolderClosedException     // Catch:{ all -> 0x02a5 }
            java.lang.String r3 = r0.getMessage()     // Catch:{ all -> 0x02a5 }
            r2.<init>(r1, r3)     // Catch:{ all -> 0x02a5 }
            throw r2     // Catch:{ all -> 0x02a5 }
        L_0x029e:
            r0 = move-exception
            r17 = r3
            r19 = r5
        L_0x02a3:
            monitor-exit(r8)     // Catch:{ all -> 0x02a5 }
            throw r0
        L_0x02a5:
            r0 = move-exception
            goto L_0x02a3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPMessage.fetch(com.sun.mail.imap.IMAPFolder, javax.mail.Message[], javax.mail.FetchProfile):void");
    }

    private synchronized void loadEnvelope() throws MessagingException {
        if (this.envelope == null) {
            Response[] responseArr = null;
            synchronized (getMessageCacheLock()) {
                try {
                    IMAPProtocol p = getProtocol();
                    checkExpunged();
                    int seqnum2 = getSequenceNumber();
                    Response[] r = p.fetch(seqnum2, EnvelopeCmd);
                    for (int i = 0; i < r.length; i++) {
                        if (r[i] != null && (r[i] instanceof FetchResponse)) {
                            if (((FetchResponse) r[i]).getNumber() == seqnum2) {
                                FetchResponse f = (FetchResponse) r[i];
                                int count = f.getItemCount();
                                for (int j = 0; j < count; j++) {
                                    Item item = f.getItem(j);
                                    if (item instanceof ENVELOPE) {
                                        this.envelope = (ENVELOPE) item;
                                    } else if (item instanceof INTERNALDATE) {
                                        this.receivedDate = ((INTERNALDATE) item).getDate();
                                    } else if (item instanceof RFC822SIZE) {
                                        this.size = ((RFC822SIZE) item).size;
                                    }
                                }
                            }
                        }
                    }
                    p.notifyResponseHandlers(r);
                    p.handleResult(r[r.length - 1]);
                } catch (ConnectionException cex) {
                    throw new FolderClosedException(this.folder, cex.getMessage());
                } catch (ProtocolException pex) {
                    forceCheckExpunged();
                    throw new MessagingException(pex.getMessage(), pex);
                }
            }
            if (this.envelope == null) {
                throw new MessagingException("Failed to load IMAP envelope");
            }
        }
    }

    private static String craftHeaderCmd(IMAPProtocol p, String[] hdrs) {
        StringBuffer sb;
        if (p.isREV1()) {
            sb = new StringBuffer("BODY.PEEK[HEADER.FIELDS (");
        } else {
            sb = new StringBuffer("RFC822.HEADER.LINES (");
        }
        for (int i = 0; i < hdrs.length; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(hdrs[i]);
        }
        if (p.isREV1() != 0) {
            sb.append(")]");
        } else {
            sb.append(")");
        }
        return sb.toString();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x004c, code lost:
        r1 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void loadBODYSTRUCTURE() throws javax.mail.MessagingException {
        /*
            r5 = this;
            monitor-enter(r5)
            com.sun.mail.imap.protocol.BODYSTRUCTURE r0 = r5.bs     // Catch:{ all -> 0x004e }
            if (r0 == 0) goto L_0x0007
            monitor-exit(r5)
            return
        L_0x0007:
            java.lang.Object r0 = r5.getMessageCacheLock()     // Catch:{ all -> 0x004e }
            monitor-enter(r0)     // Catch:{ all -> 0x004e }
            com.sun.mail.imap.protocol.IMAPProtocol r1 = r5.getProtocol()     // Catch:{ ConnectionException -> 0x003d, ProtocolException -> 0x002f }
            r5.checkExpunged()     // Catch:{ ConnectionException -> 0x003d, ProtocolException -> 0x002f }
            int r2 = r5.getSequenceNumber()     // Catch:{ ConnectionException -> 0x003d, ProtocolException -> 0x002f }
            com.sun.mail.imap.protocol.BODYSTRUCTURE r2 = r1.fetchBodyStructure(r2)     // Catch:{ ConnectionException -> 0x003d, ProtocolException -> 0x002f }
            r5.bs = r2     // Catch:{ ConnectionException -> 0x003d, ProtocolException -> 0x002f }
            if (r2 == 0) goto L_0x0022
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            monitor-exit(r5)
            return
        L_0x0022:
            r5.forceCheckExpunged()     // Catch:{ all -> 0x002d }
            javax.mail.MessagingException r1 = new javax.mail.MessagingException     // Catch:{ all -> 0x002d }
            java.lang.String r2 = "Unable to load BODYSTRUCTURE"
            r1.<init>(r2)     // Catch:{ all -> 0x002d }
            throw r1     // Catch:{ all -> 0x002d }
        L_0x002d:
            r1 = move-exception
            goto L_0x004a
        L_0x002f:
            r1 = move-exception
            r5.forceCheckExpunged()     // Catch:{ all -> 0x002d }
            javax.mail.MessagingException r2 = new javax.mail.MessagingException     // Catch:{ all -> 0x002d }
            java.lang.String r3 = r1.getMessage()     // Catch:{ all -> 0x002d }
            r2.<init>(r3, r1)     // Catch:{ all -> 0x002d }
            throw r2     // Catch:{ all -> 0x002d }
        L_0x003d:
            r1 = move-exception
            javax.mail.FolderClosedException r2 = new javax.mail.FolderClosedException     // Catch:{ all -> 0x002d }
            javax.mail.Folder r3 = r5.folder     // Catch:{ all -> 0x002d }
            java.lang.String r4 = r1.getMessage()     // Catch:{ all -> 0x002d }
            r2.<init>(r3, r4)     // Catch:{ all -> 0x002d }
            throw r2     // Catch:{ all -> 0x002d }
        L_0x004a:
            monitor-exit(r0)     // Catch:{ all -> 0x004c }
            throw r1     // Catch:{ all -> 0x004e }
        L_0x004c:
            r1 = move-exception
            goto L_0x004a
        L_0x004e:
            r0 = move-exception
            monitor-exit(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPMessage.loadBODYSTRUCTURE():void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0048, code lost:
        if (r0 == null) goto L_0x0056;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
        r6.headers = new javax.mail.internet.InternetHeaders(r0);
        r6.headersLoaded = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0055, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x005d, code lost:
        throw new javax.mail.MessagingException("Cannot load header");
     */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:39:0x0061=Splitter:B:39:0x0061, B:27:0x0047=Splitter:B:27:0x0047} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void loadHeaders() throws javax.mail.MessagingException {
        /*
            r6 = this;
            monitor-enter(r6)
            boolean r0 = r6.headersLoaded     // Catch:{ all -> 0x007d }
            if (r0 == 0) goto L_0x0007
            monitor-exit(r6)
            return
        L_0x0007:
            r0 = 0
            java.lang.Object r1 = r6.getMessageCacheLock()     // Catch:{ all -> 0x007d }
            monitor-enter(r1)     // Catch:{ all -> 0x007d }
            com.sun.mail.imap.protocol.IMAPProtocol r2 = r6.getProtocol()     // Catch:{ ConnectionException -> 0x006e, ProtocolException -> 0x0060 }
            r6.checkExpunged()     // Catch:{ ConnectionException -> 0x006e, ProtocolException -> 0x0060 }
            boolean r3 = r2.isREV1()     // Catch:{ ConnectionException -> 0x006e, ProtocolException -> 0x0060 }
            if (r3 == 0) goto L_0x0036
            int r3 = r6.getSequenceNumber()     // Catch:{ ConnectionException -> 0x0034, ProtocolException -> 0x0032, all -> 0x0030 }
            java.lang.String r4 = "HEADER"
            java.lang.String r4 = r6.toSection(r4)     // Catch:{ ConnectionException -> 0x0034, ProtocolException -> 0x0032, all -> 0x0030 }
            com.sun.mail.imap.protocol.BODY r3 = r2.peekBody(r3, r4)     // Catch:{ ConnectionException -> 0x0034, ProtocolException -> 0x0032, all -> 0x0030 }
            if (r3 == 0) goto L_0x0047
            java.io.ByteArrayInputStream r4 = r3.getByteArrayInputStream()     // Catch:{ ConnectionException -> 0x0034, ProtocolException -> 0x0032, all -> 0x0030 }
            r0 = r4
            goto L_0x0047
        L_0x0030:
            r2 = move-exception
            goto L_0x007b
        L_0x0032:
            r2 = move-exception
            goto L_0x0061
        L_0x0034:
            r2 = move-exception
            goto L_0x006f
        L_0x0036:
            int r3 = r6.getSequenceNumber()     // Catch:{ ConnectionException -> 0x006e, ProtocolException -> 0x0060 }
            java.lang.String r4 = "HEADER"
            com.sun.mail.imap.protocol.RFC822DATA r3 = r2.fetchRFC822(r3, r4)     // Catch:{ ConnectionException -> 0x006e, ProtocolException -> 0x0060 }
            if (r3 == 0) goto L_0x0047
            java.io.ByteArrayInputStream r4 = r3.getByteArrayInputStream()     // Catch:{ ConnectionException -> 0x0034, ProtocolException -> 0x0032, all -> 0x0030 }
            r0 = r4
        L_0x0047:
            monitor-exit(r1)     // Catch:{ all -> 0x005e }
            if (r0 == 0) goto L_0x0056
            javax.mail.internet.InternetHeaders r1 = new javax.mail.internet.InternetHeaders     // Catch:{ all -> 0x007d }
            r1.<init>(r0)     // Catch:{ all -> 0x007d }
            r6.headers = r1     // Catch:{ all -> 0x007d }
            r1 = 1
            r6.headersLoaded = r1     // Catch:{ all -> 0x007d }
            monitor-exit(r6)
            return
        L_0x0056:
            javax.mail.MessagingException r1 = new javax.mail.MessagingException     // Catch:{ all -> 0x007d }
            java.lang.String r2 = "Cannot load header"
            r1.<init>(r2)     // Catch:{ all -> 0x007d }
            throw r1     // Catch:{ all -> 0x007d }
        L_0x005e:
            r2 = move-exception
            goto L_0x007b
        L_0x0060:
            r2 = move-exception
        L_0x0061:
            r6.forceCheckExpunged()     // Catch:{ all -> 0x005e }
            javax.mail.MessagingException r3 = new javax.mail.MessagingException     // Catch:{ all -> 0x005e }
            java.lang.String r4 = r2.getMessage()     // Catch:{ all -> 0x005e }
            r3.<init>(r4, r2)     // Catch:{ all -> 0x005e }
            throw r3     // Catch:{ all -> 0x005e }
        L_0x006e:
            r2 = move-exception
        L_0x006f:
            javax.mail.FolderClosedException r3 = new javax.mail.FolderClosedException     // Catch:{ all -> 0x005e }
            javax.mail.Folder r4 = r6.folder     // Catch:{ all -> 0x005e }
            java.lang.String r5 = r2.getMessage()     // Catch:{ all -> 0x005e }
            r3.<init>(r4, r5)     // Catch:{ all -> 0x005e }
            throw r3     // Catch:{ all -> 0x005e }
        L_0x007b:
            monitor-exit(r1)     // Catch:{ all -> 0x005e }
            throw r2     // Catch:{ all -> 0x007d }
        L_0x007d:
            r0 = move-exception
            monitor-exit(r6)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPMessage.loadHeaders():void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x003f, code lost:
        r1 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void loadFlags() throws javax.mail.MessagingException {
        /*
            r5 = this;
            monitor-enter(r5)
            javax.mail.Flags r0 = r5.flags     // Catch:{ all -> 0x0041 }
            if (r0 == 0) goto L_0x0007
            monitor-exit(r5)
            return
        L_0x0007:
            java.lang.Object r0 = r5.getMessageCacheLock()     // Catch:{ all -> 0x0041 }
            monitor-enter(r0)     // Catch:{ all -> 0x0041 }
            com.sun.mail.imap.protocol.IMAPProtocol r1 = r5.getProtocol()     // Catch:{ ConnectionException -> 0x0030, ProtocolException -> 0x0022 }
            r5.checkExpunged()     // Catch:{ ConnectionException -> 0x0030, ProtocolException -> 0x0022 }
            int r2 = r5.getSequenceNumber()     // Catch:{ ConnectionException -> 0x0030, ProtocolException -> 0x0022 }
            javax.mail.Flags r2 = r1.fetchFlags(r2)     // Catch:{ ConnectionException -> 0x0030, ProtocolException -> 0x0022 }
            r5.flags = r2     // Catch:{ ConnectionException -> 0x0030, ProtocolException -> 0x0022 }
            monitor-exit(r0)     // Catch:{ all -> 0x0020 }
            monitor-exit(r5)
            return
        L_0x0020:
            r1 = move-exception
            goto L_0x003d
        L_0x0022:
            r1 = move-exception
            r5.forceCheckExpunged()     // Catch:{ all -> 0x0020 }
            javax.mail.MessagingException r2 = new javax.mail.MessagingException     // Catch:{ all -> 0x0020 }
            java.lang.String r3 = r1.getMessage()     // Catch:{ all -> 0x0020 }
            r2.<init>(r3, r1)     // Catch:{ all -> 0x0020 }
            throw r2     // Catch:{ all -> 0x0020 }
        L_0x0030:
            r1 = move-exception
            javax.mail.FolderClosedException r2 = new javax.mail.FolderClosedException     // Catch:{ all -> 0x0020 }
            javax.mail.Folder r3 = r5.folder     // Catch:{ all -> 0x0020 }
            java.lang.String r4 = r1.getMessage()     // Catch:{ all -> 0x0020 }
            r2.<init>(r3, r4)     // Catch:{ all -> 0x0020 }
            throw r2     // Catch:{ all -> 0x0020 }
        L_0x003d:
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            throw r1     // Catch:{ all -> 0x0041 }
        L_0x003f:
            r1 = move-exception
            goto L_0x003d
        L_0x0041:
            r0 = move-exception
            monitor-exit(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPMessage.loadFlags():void");
    }

    /* access modifiers changed from: private */
    public synchronized boolean areHeadersLoaded() {
        return this.headersLoaded;
    }

    private synchronized void setHeadersLoaded(boolean loaded) {
        this.headersLoaded = loaded;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0019, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean isHeaderLoaded(java.lang.String r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            boolean r0 = r2.headersLoaded     // Catch:{ all -> 0x001a }
            if (r0 == 0) goto L_0x0008
            r0 = 1
            monitor-exit(r2)
            return r0
        L_0x0008:
            java.util.Hashtable r0 = r2.loadedHeaders     // Catch:{ all -> 0x001a }
            if (r0 == 0) goto L_0x0017
            java.util.Locale r1 = java.util.Locale.ENGLISH     // Catch:{ all -> 0x001a }
            java.lang.String r1 = r3.toUpperCase(r1)     // Catch:{ all -> 0x001a }
            boolean r0 = r0.containsKey(r1)     // Catch:{ all -> 0x001a }
            goto L_0x0018
        L_0x0017:
            r0 = 0
        L_0x0018:
            monitor-exit(r2)
            return r0
        L_0x001a:
            r3 = move-exception
            monitor-exit(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPMessage.isHeaderLoaded(java.lang.String):boolean");
    }

    private synchronized void setHeaderLoaded(String name) {
        if (this.loadedHeaders == null) {
            this.loadedHeaders = new Hashtable(1);
        }
        this.loadedHeaders.put(name.toUpperCase(Locale.ENGLISH), name);
    }

    private String toSection(String what) {
        if (this.sectionId == null) {
            return what;
        }
        return String.valueOf(this.sectionId) + "." + what;
    }

    private InternetAddress[] aaclone(InternetAddress[] aa) {
        if (aa == null) {
            return null;
        }
        return (InternetAddress[]) aa.clone();
    }

    /* access modifiers changed from: private */
    public Flags _getFlags() {
        return this.flags;
    }

    /* access modifiers changed from: private */
    public ENVELOPE _getEnvelope() {
        return this.envelope;
    }

    /* access modifiers changed from: private */
    public BODYSTRUCTURE _getBodyStructure() {
        return this.bs;
    }

    /* access modifiers changed from: package-private */
    public void _setFlags(Flags flags) {
        this.flags = flags;
    }

    /* access modifiers changed from: package-private */
    public Session _getSession() {
        return this.session;
    }
}
