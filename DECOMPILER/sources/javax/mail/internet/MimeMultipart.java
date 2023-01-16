package javax.mail.internet;

import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.LineOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.MessageAware;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.MultipartDataSource;

public class MimeMultipart extends Multipart {
    private static boolean bmparse;
    private static boolean ignoreMissingBoundaryParameter;
    private static boolean ignoreMissingEndBoundary;
    private boolean complete;
    protected DataSource ds;
    protected boolean parsed;
    private String preamble;

    /* JADX WARNING: Removed duplicated region for block: B:14:0x002c A[Catch:{ SecurityException -> 0x0044 }] */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x002e A[Catch:{ SecurityException -> 0x0044 }] */
    static {
        /*
            r0 = 1
            ignoreMissingEndBoundary = r0
            ignoreMissingBoundaryParameter = r0
            bmparse = r0
            java.lang.String r1 = "mail.mime.multipart.ignoremissingendboundary"
            java.lang.String r1 = java.lang.System.getProperty(r1)     // Catch:{ SecurityException -> 0x0044 }
            r2 = 0
            java.lang.String r3 = "false"
            if (r1 == 0) goto L_0x001a
            boolean r4 = r1.equalsIgnoreCase(r3)     // Catch:{ SecurityException -> 0x0044 }
            if (r4 == 0) goto L_0x001a
            r4 = r2
            goto L_0x001b
        L_0x001a:
            r4 = r0
        L_0x001b:
            ignoreMissingEndBoundary = r4     // Catch:{ SecurityException -> 0x0044 }
            java.lang.String r4 = "mail.mime.multipart.ignoremissingboundaryparameter"
            java.lang.String r4 = java.lang.System.getProperty(r4)     // Catch:{ SecurityException -> 0x0044 }
            r1 = r4
            if (r1 == 0) goto L_0x002e
            boolean r4 = r1.equalsIgnoreCase(r3)     // Catch:{ SecurityException -> 0x0044 }
            if (r4 == 0) goto L_0x002e
            r4 = r2
            goto L_0x002f
        L_0x002e:
            r4 = r0
        L_0x002f:
            ignoreMissingBoundaryParameter = r4     // Catch:{ SecurityException -> 0x0044 }
            java.lang.String r4 = "mail.mime.multipart.bmparse"
            java.lang.String r4 = java.lang.System.getProperty(r4)     // Catch:{ SecurityException -> 0x0044 }
            r1 = r4
            if (r1 == 0) goto L_0x0041
            boolean r3 = r1.equalsIgnoreCase(r3)     // Catch:{ SecurityException -> 0x0044 }
            if (r3 == 0) goto L_0x0041
            r0 = r2
        L_0x0041:
            bmparse = r0     // Catch:{ SecurityException -> 0x0044 }
            goto L_0x0045
        L_0x0044:
            r0 = move-exception
        L_0x0045:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: javax.mail.internet.MimeMultipart.<clinit>():void");
    }

    public MimeMultipart() {
        this("mixed");
    }

    public MimeMultipart(String subtype) {
        this.ds = null;
        this.parsed = true;
        this.complete = true;
        this.preamble = null;
        String boundary = UniqueValue.getUniqueBoundaryValue();
        ContentType cType = new ContentType("multipart", subtype, (ParameterList) null);
        cType.setParameter("boundary", boundary);
        this.contentType = cType.toString();
    }

    public MimeMultipart(DataSource ds2) throws MessagingException {
        this.ds = null;
        this.parsed = true;
        this.complete = true;
        this.preamble = null;
        if (ds2 instanceof MessageAware) {
            setParent(((MessageAware) ds2).getMessageContext().getPart());
        }
        if (ds2 instanceof MultipartDataSource) {
            setMultipartDataSource((MultipartDataSource) ds2);
            return;
        }
        this.parsed = false;
        this.ds = ds2;
        this.contentType = ds2.getContentType();
    }

    public synchronized void setSubType(String subtype) throws MessagingException {
        ContentType cType = new ContentType(this.contentType);
        cType.setSubType(subtype);
        this.contentType = cType.toString();
    }

    public synchronized int getCount() throws MessagingException {
        parse();
        return super.getCount();
    }

    public synchronized BodyPart getBodyPart(int index) throws MessagingException {
        parse();
        return super.getBodyPart(index);
    }

    public synchronized BodyPart getBodyPart(String CID) throws MessagingException {
        parse();
        int count = getCount();
        for (int i = 0; i < count; i++) {
            MimeBodyPart part = (MimeBodyPart) getBodyPart(i);
            String s = part.getContentID();
            if (s != null && s.equals(CID)) {
                return part;
            }
        }
        return null;
    }

    public boolean removeBodyPart(BodyPart part) throws MessagingException {
        parse();
        return super.removeBodyPart(part);
    }

    public void removeBodyPart(int index) throws MessagingException {
        parse();
        super.removeBodyPart(index);
    }

    public synchronized void addBodyPart(BodyPart part) throws MessagingException {
        parse();
        super.addBodyPart(part);
    }

    public synchronized void addBodyPart(BodyPart part, int index) throws MessagingException {
        parse();
        super.addBodyPart(part, index);
    }

    public synchronized boolean isComplete() throws MessagingException {
        parse();
        return this.complete;
    }

    public synchronized String getPreamble() throws MessagingException {
        parse();
        return this.preamble;
    }

    public synchronized void setPreamble(String preamble2) throws MessagingException {
        this.preamble = preamble2;
    }

    /* access modifiers changed from: protected */
    public void updateHeaders() throws MessagingException {
        for (int i = 0; i < this.parts.size(); i++) {
            ((MimeBodyPart) this.parts.elementAt(i)).updateHeaders();
        }
    }

    public synchronized void writeTo(OutputStream os) throws IOException, MessagingException {
        parse();
        String boundary = "--" + new ContentType(this.contentType).getParameter("boundary");
        LineOutputStream los = new LineOutputStream(os);
        String str = this.preamble;
        if (str != null) {
            byte[] pb = ASCIIUtility.getBytes(str);
            los.write(pb);
            if (!(pb.length <= 0 || pb[pb.length - 1] == 13 || pb[pb.length - 1] == 10)) {
                los.writeln();
            }
        }
        for (int i = 0; i < this.parts.size(); i++) {
            los.writeln(boundary);
            ((MimeBodyPart) this.parts.elementAt(i)).writeTo(os);
            los.writeln();
        }
        los.writeln(String.valueOf(boundary) + "--");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:100:0x0112, code lost:
        r19 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:103:0x0116, code lost:
        r20 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:106:0x011d, code lost:
        throw new javax.mail.MessagingException("missing multipart end boundary");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:107:0x011e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:108:0x011f, code lost:
        r23 = r8;
        r24 = r9;
        r4 = r17;
        r6 = r20;
        r8 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:109:0x012a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x012b, code lost:
        r23 = r8;
        r24 = r9;
        r4 = r17;
        r6 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x0100, code lost:
        if (ignoreMissingEndBoundary == false) goto L_0x0112;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x0102, code lost:
        r19 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:?, code lost:
        r1.complete = false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void parse() throws javax.mail.MessagingException {
        /*
            r33 = this;
            r1 = r33
            monitor-enter(r33)
            boolean r0 = r1.parsed     // Catch:{ all -> 0x0431 }
            if (r0 == 0) goto L_0x0009
            monitor-exit(r33)
            return
        L_0x0009:
            boolean r0 = bmparse     // Catch:{ all -> 0x0431 }
            if (r0 == 0) goto L_0x0012
            r33.parsebm()     // Catch:{ all -> 0x0431 }
            monitor-exit(r33)
            return
        L_0x0012:
            r2 = 0
            r3 = 0
            r4 = 0
            r6 = 0
            javax.activation.DataSource r0 = r1.ds     // Catch:{ Exception -> 0x0426 }
            java.io.InputStream r0 = r0.getInputStream()     // Catch:{ Exception -> 0x0426 }
            r2 = r0
            boolean r0 = r2 instanceof java.io.ByteArrayInputStream     // Catch:{ Exception -> 0x0426 }
            if (r0 != 0) goto L_0x0038
            boolean r0 = r2 instanceof java.io.BufferedInputStream     // Catch:{ Exception -> 0x0033 }
            if (r0 != 0) goto L_0x0038
            boolean r0 = r2 instanceof javax.mail.internet.SharedInputStream     // Catch:{ Exception -> 0x0033 }
            if (r0 != 0) goto L_0x0038
            java.io.BufferedInputStream r0 = new java.io.BufferedInputStream     // Catch:{ Exception -> 0x0033 }
            r0.<init>(r2)     // Catch:{ Exception -> 0x0033 }
            r2 = r0
            goto L_0x0038
        L_0x0033:
            r0 = move-exception
            r17 = r4
            goto L_0x0429
        L_0x0038:
            boolean r0 = r2 instanceof javax.mail.internet.SharedInputStream     // Catch:{ all -> 0x0431 }
            if (r0 == 0) goto L_0x0040
            r0 = r2
            javax.mail.internet.SharedInputStream r0 = (javax.mail.internet.SharedInputStream) r0     // Catch:{ all -> 0x0431 }
            r3 = r0
        L_0x0040:
            javax.mail.internet.ContentType r0 = new javax.mail.internet.ContentType     // Catch:{ all -> 0x0431 }
            java.lang.String r8 = r1.contentType     // Catch:{ all -> 0x0431 }
            r0.<init>(r8)     // Catch:{ all -> 0x0431 }
            r8 = r0
            r0 = 0
            java.lang.String r9 = "boundary"
            java.lang.String r9 = r8.getParameter(r9)     // Catch:{ all -> 0x0431 }
            if (r9 == 0) goto L_0x0062
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x0431 }
            java.lang.String r11 = "--"
            r10.<init>(r11)     // Catch:{ all -> 0x0431 }
            java.lang.StringBuilder r10 = r10.append(r9)     // Catch:{ all -> 0x0431 }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x0431 }
            r0 = r10
            goto L_0x0067
        L_0x0062:
            boolean r10 = ignoreMissingBoundaryParameter     // Catch:{ all -> 0x0431 }
            if (r10 == 0) goto L_0x0418
            r10 = r0
        L_0x0067:
            com.sun.mail.util.LineInputStream r0 = new com.sun.mail.util.LineInputStream     // Catch:{ IOException -> 0x0401, all -> 0x03f8 }
            r0.<init>(r2)     // Catch:{ IOException -> 0x0401, all -> 0x03f8 }
            r11 = r0
            r0 = 0
            r12 = 0
            r13 = r12
            r12 = r0
        L_0x0071:
            java.lang.String r0 = r11.readLine()     // Catch:{ IOException -> 0x0401, all -> 0x03f8 }
            r14 = r0
            r15 = 1
            if (r0 != 0) goto L_0x007c
            r17 = r4
            goto L_0x00bd
        L_0x007c:
            int r0 = r14.length()     // Catch:{ IOException -> 0x0401, all -> 0x03f8 }
            int r0 = r0 - r15
            r15 = r0
        L_0x0082:
            if (r15 >= 0) goto L_0x0087
            r17 = r4
            goto L_0x0096
        L_0x0087:
            char r0 = r14.charAt(r15)     // Catch:{ IOException -> 0x0401, all -> 0x03f8 }
            r17 = r4
            r4 = 32
            if (r0 == r4) goto L_0x03db
            r4 = 9
            if (r0 == r4) goto L_0x03d0
        L_0x0096:
            int r0 = r15 + 1
            r4 = 0
            java.lang.String r0 = r14.substring(r4, r0)     // Catch:{ IOException -> 0x03c8, all -> 0x03bf }
            r14 = r0
            if (r10 == 0) goto L_0x00b3
            boolean r0 = r14.equals(r10)     // Catch:{ IOException -> 0x00d2, all -> 0x00c8 }
            if (r0 == 0) goto L_0x00a7
            goto L_0x00bd
        L_0x00a7:
            r23 = r8
            r24 = r9
            r25 = r11
            r26 = r12
            r27 = r13
            goto L_0x036b
        L_0x00b3:
            java.lang.String r0 = "--"
            boolean r0 = r14.startsWith(r0)     // Catch:{ IOException -> 0x03c8, all -> 0x03bf }
            if (r0 == 0) goto L_0x0361
            r10 = r14
        L_0x00bd:
            if (r14 == 0) goto L_0x033e
            if (r12 == 0) goto L_0x00db
            java.lang.String r0 = r12.toString()     // Catch:{ IOException -> 0x00d2, all -> 0x00c8 }
            r1.preamble = r0     // Catch:{ IOException -> 0x00d2, all -> 0x00c8 }
            goto L_0x00db
        L_0x00c8:
            r0 = move-exception
            r23 = r8
            r24 = r9
            r4 = r17
            r8 = r0
            goto L_0x0412
        L_0x00d2:
            r0 = move-exception
            r23 = r8
            r24 = r9
            r4 = r17
            goto L_0x0408
        L_0x00db:
            byte[] r0 = com.sun.mail.util.ASCIIUtility.getBytes((java.lang.String) r10)     // Catch:{ IOException -> 0x0333, all -> 0x0327 }
            int r4 = r0.length     // Catch:{ IOException -> 0x0333, all -> 0x0327 }
            r5 = 0
        L_0x00e2:
            if (r5 == 0) goto L_0x00e5
            goto L_0x0108
        L_0x00e5:
            r15 = 0
            if (r3 == 0) goto L_0x0153
            long r19 = r3.getPosition()     // Catch:{ IOException -> 0x0148, all -> 0x013c }
            r17 = r19
        L_0x00ee:
            java.lang.String r19 = r11.readLine()     // Catch:{ IOException -> 0x0148, all -> 0x013c }
            r14 = r19
            if (r19 == 0) goto L_0x00fc
            int r19 = r14.length()     // Catch:{ IOException -> 0x00d2, all -> 0x00c8 }
            if (r19 > 0) goto L_0x00ee
        L_0x00fc:
            if (r14 != 0) goto L_0x0135
            boolean r16 = ignoreMissingEndBoundary     // Catch:{ IOException -> 0x0148, all -> 0x013c }
            if (r16 == 0) goto L_0x0112
            r19 = r5
            r5 = 0
            r1.complete = r5     // Catch:{ IOException -> 0x00d2, all -> 0x00c8 }
        L_0x0108:
            r2.close()     // Catch:{ IOException -> 0x010c }
            goto L_0x010d
        L_0x010c:
            r0 = move-exception
        L_0x010d:
            r4 = 1
            r1.parsed = r4     // Catch:{ all -> 0x0431 }
            monitor-exit(r33)
            return
        L_0x0112:
            r19 = r5
            javax.mail.MessagingException r5 = new javax.mail.MessagingException     // Catch:{ IOException -> 0x0148, all -> 0x013c }
            r20 = r6
            java.lang.String r6 = "missing multipart end boundary"
            r5.<init>(r6)     // Catch:{ IOException -> 0x012a, all -> 0x011e }
            throw r5     // Catch:{ IOException -> 0x012a, all -> 0x011e }
        L_0x011e:
            r0 = move-exception
            r23 = r8
            r24 = r9
            r4 = r17
            r6 = r20
            r8 = r0
            goto L_0x0412
        L_0x012a:
            r0 = move-exception
            r23 = r8
            r24 = r9
            r4 = r17
            r6 = r20
            goto L_0x0408
        L_0x0135:
            r19 = r5
            r20 = r6
            r5 = r17
            goto L_0x015e
        L_0x013c:
            r0 = move-exception
            r20 = r6
            r23 = r8
            r24 = r9
            r4 = r17
            r8 = r0
            goto L_0x0412
        L_0x0148:
            r0 = move-exception
            r20 = r6
            r23 = r8
            r24 = r9
            r4 = r17
            goto L_0x0408
        L_0x0153:
            r19 = r5
            r20 = r6
            javax.mail.internet.InternetHeaders r5 = r1.createInternetHeaders(r2)     // Catch:{ IOException -> 0x031a, all -> 0x030c }
            r15 = r5
            r5 = r17
        L_0x015e:
            boolean r7 = r2.markSupported()     // Catch:{ IOException -> 0x0300, all -> 0x02f3 }
            if (r7 == 0) goto L_0x02ca
            r7 = 0
            if (r3 != 0) goto L_0x0186
            java.io.ByteArrayOutputStream r17 = new java.io.ByteArrayOutputStream     // Catch:{ IOException -> 0x017c, all -> 0x0171 }
            r17.<init>()     // Catch:{ IOException -> 0x017c, all -> 0x0171 }
            r7 = r17
            r17 = r20
            goto L_0x018a
        L_0x0171:
            r0 = move-exception
            r4 = r5
            r23 = r8
            r24 = r9
            r6 = r20
            r8 = r0
            goto L_0x0412
        L_0x017c:
            r0 = move-exception
            r4 = r5
            r23 = r8
            r24 = r9
            r6 = r20
            goto L_0x0408
        L_0x0186:
            long r17 = r3.getPosition()     // Catch:{ IOException -> 0x0300, all -> 0x02f3 }
        L_0x018a:
            r20 = 1
            r21 = -1
            r22 = -1
            r23 = r8
            r24 = r9
            r25 = r11
            r8 = r17
            r11 = r22
            r22 = r10
            r10 = r21
        L_0x019e:
            r17 = r14
            if (r20 == 0) goto L_0x0225
            int r14 = r4 + 4
            int r14 = r14 + 1000
            r2.mark(r14)     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            r14 = 0
        L_0x01aa:
            if (r14 < r4) goto L_0x01b1
            r26 = r12
            r27 = r13
            goto L_0x01c0
        L_0x01b1:
            r26 = r12
            int r12 = r2.read()     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            r27 = r13
            byte r13 = r0[r14]     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            r13 = r13 & 255(0xff, float:3.57E-43)
            if (r12 == r13) goto L_0x021c
        L_0x01c0:
            if (r14 != r4) goto L_0x0207
            int r12 = r2.read()     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            r13 = 45
            if (r12 != r13) goto L_0x01db
            int r13 = r2.read()     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            r28 = r0
            r0 = 45
            if (r13 != r0) goto L_0x01dd
            r13 = 1
            r1.complete = r13     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            r0 = 1
            r13 = 0
            goto L_0x023b
        L_0x01db:
            r28 = r0
        L_0x01dd:
            r13 = 32
            if (r12 == r13) goto L_0x0201
            r0 = 9
            if (r12 == r0) goto L_0x0201
            r0 = 10
            if (r12 != r0) goto L_0x01ea
            goto L_0x01fd
        L_0x01ea:
            r0 = 13
            if (r12 != r0) goto L_0x0209
            r13 = 1
            r2.mark(r13)     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            int r0 = r2.read()     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            r13 = 10
            if (r0 == r13) goto L_0x01fd
            r2.reset()     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
        L_0x01fd:
            r0 = r19
            r13 = 0
            goto L_0x023b
        L_0x0201:
            int r0 = r2.read()     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            r12 = r0
            goto L_0x01dd
        L_0x0207:
            r28 = r0
        L_0x0209:
            r2.reset()     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            if (r7 == 0) goto L_0x022b
            r0 = -1
            if (r10 == r0) goto L_0x022b
            r7.write(r10)     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            if (r11 == r0) goto L_0x0219
            r7.write(r11)     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
        L_0x0219:
            r11 = r0
            r10 = r0
            goto L_0x022b
        L_0x021c:
            r28 = r0
            int r14 = r14 + 1
            r12 = r26
            r13 = r27
            goto L_0x01aa
        L_0x0225:
            r28 = r0
            r26 = r12
            r27 = r13
        L_0x022b:
            int r0 = r2.read()     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            r12 = r0
            if (r0 >= 0) goto L_0x026f
            boolean r0 = ignoreMissingEndBoundary     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            if (r0 == 0) goto L_0x0267
            r13 = 0
            r1.complete = r13     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            r0 = 1
        L_0x023b:
            if (r3 == 0) goto L_0x0246
            java.io.InputStream r12 = r3.newStream(r5, r8)     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            javax.mail.internet.MimeBodyPart r12 = r1.createMimeBodyPart(r12)     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            goto L_0x024e
        L_0x0246:
            byte[] r12 = r7.toByteArray()     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            javax.mail.internet.MimeBodyPart r12 = r1.createMimeBodyPart(r15, r12)     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
        L_0x024e:
            super.addBodyPart(r12)     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            r14 = r17
            r10 = r22
            r11 = r25
            r12 = r26
            r13 = r27
            r17 = r5
            r6 = r8
            r8 = r23
            r9 = r24
            r5 = r0
            r0 = r28
            goto L_0x00e2
        L_0x0267:
            javax.mail.MessagingException r0 = new javax.mail.MessagingException     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            java.lang.String r13 = "missing multipart end boundary"
            r0.<init>(r13)     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            throw r0     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
        L_0x026f:
            r13 = 0
            r0 = 13
            if (r12 == r0) goto L_0x0281
            r0 = 10
            if (r12 != r0) goto L_0x0279
            goto L_0x0281
        L_0x0279:
            r20 = 0
            if (r7 == 0) goto L_0x02b1
            r7.write(r12)     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            goto L_0x02b1
        L_0x0281:
            r20 = 1
            if (r3 == 0) goto L_0x028f
            long r29 = r3.getPosition()     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            r31 = 1
            long r29 = r29 - r31
            r8 = r29
        L_0x028f:
            r10 = r12
            r0 = 13
            if (r12 != r0) goto L_0x02b0
            r14 = 1
            r2.mark(r14)     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            int r0 = r2.read()     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            r12 = r0
            r13 = 10
            if (r0 != r13) goto L_0x02ac
            r11 = r12
            r14 = r17
            r12 = r26
            r13 = r27
            r0 = r28
            goto L_0x019e
        L_0x02ac:
            r2.reset()     // Catch:{ IOException -> 0x02c3, all -> 0x02bb }
            goto L_0x02b1
        L_0x02b0:
            r14 = 1
        L_0x02b1:
            r14 = r17
            r12 = r26
            r13 = r27
            r0 = r28
            goto L_0x019e
        L_0x02bb:
            r0 = move-exception
            r4 = r5
            r6 = r8
            r10 = r22
            r8 = r0
            goto L_0x0412
        L_0x02c3:
            r0 = move-exception
            r4 = r5
            r6 = r8
            r10 = r22
            goto L_0x0408
        L_0x02ca:
            r28 = r0
            r23 = r8
            r24 = r9
            r22 = r10
            r25 = r11
            r26 = r12
            r27 = r13
            r17 = r14
            javax.mail.MessagingException r0 = new javax.mail.MessagingException     // Catch:{ IOException -> 0x02eb, all -> 0x02e2 }
            java.lang.String r7 = "Stream doesn't support mark"
            r0.<init>(r7)     // Catch:{ IOException -> 0x02eb, all -> 0x02e2 }
            throw r0     // Catch:{ IOException -> 0x02eb, all -> 0x02e2 }
        L_0x02e2:
            r0 = move-exception
            r8 = r0
            r4 = r5
            r6 = r20
            r10 = r22
            goto L_0x0412
        L_0x02eb:
            r0 = move-exception
            r4 = r5
            r6 = r20
            r10 = r22
            goto L_0x0408
        L_0x02f3:
            r0 = move-exception
            r23 = r8
            r24 = r9
            r22 = r10
            r8 = r0
            r4 = r5
            r6 = r20
            goto L_0x0412
        L_0x0300:
            r0 = move-exception
            r23 = r8
            r24 = r9
            r22 = r10
            r4 = r5
            r6 = r20
            goto L_0x0408
        L_0x030c:
            r0 = move-exception
            r23 = r8
            r24 = r9
            r22 = r10
            r8 = r0
            r4 = r17
            r6 = r20
            goto L_0x0412
        L_0x031a:
            r0 = move-exception
            r23 = r8
            r24 = r9
            r22 = r10
            r4 = r17
            r6 = r20
            goto L_0x0408
        L_0x0327:
            r0 = move-exception
            r23 = r8
            r24 = r9
            r22 = r10
            r8 = r0
            r4 = r17
            goto L_0x0412
        L_0x0333:
            r0 = move-exception
            r23 = r8
            r24 = r9
            r22 = r10
            r4 = r17
            goto L_0x0408
        L_0x033e:
            r23 = r8
            r24 = r9
            r22 = r10
            r25 = r11
            r26 = r12
            r27 = r13
            javax.mail.MessagingException r0 = new javax.mail.MessagingException     // Catch:{ IOException -> 0x035a, all -> 0x0352 }
            java.lang.String r4 = "Missing start boundary"
            r0.<init>(r4)     // Catch:{ IOException -> 0x035a, all -> 0x0352 }
            throw r0     // Catch:{ IOException -> 0x035a, all -> 0x0352 }
        L_0x0352:
            r0 = move-exception
            r8 = r0
            r4 = r17
            r10 = r22
            goto L_0x0412
        L_0x035a:
            r0 = move-exception
            r4 = r17
            r10 = r22
            goto L_0x0408
        L_0x0361:
            r23 = r8
            r24 = r9
            r25 = r11
            r26 = r12
            r27 = r13
        L_0x036b:
            int r0 = r14.length()     // Catch:{ IOException -> 0x03bb, all -> 0x03b5 }
            if (r0 <= 0) goto L_0x03a7
            if (r27 != 0) goto L_0x0383
            java.lang.String r0 = "line.separator"
            java.lang.String r4 = "\n"
            java.lang.String r0 = java.lang.System.getProperty(r0, r4)     // Catch:{ SecurityException -> 0x037e }
            r13 = r0
            goto L_0x0385
        L_0x037e:
            r0 = move-exception
            java.lang.String r4 = "\n"
            r13 = r4
            goto L_0x0385
        L_0x0383:
            r13 = r27
        L_0x0385:
            if (r26 != 0) goto L_0x0394
            java.lang.StringBuffer r0 = new java.lang.StringBuffer     // Catch:{ IOException -> 0x03bb, all -> 0x03b5 }
            int r4 = r14.length()     // Catch:{ IOException -> 0x03bb, all -> 0x03b5 }
            int r4 = r4 + 2
            r0.<init>(r4)     // Catch:{ IOException -> 0x03bb, all -> 0x03b5 }
            r12 = r0
            goto L_0x0396
        L_0x0394:
            r12 = r26
        L_0x0396:
            java.lang.StringBuffer r0 = r12.append(r14)     // Catch:{ IOException -> 0x03bb, all -> 0x03b5 }
            r0.append(r13)     // Catch:{ IOException -> 0x03bb, all -> 0x03b5 }
            r4 = r17
            r8 = r23
            r9 = r24
            r11 = r25
            goto L_0x0071
        L_0x03a7:
            r4 = r17
            r8 = r23
            r9 = r24
            r11 = r25
            r12 = r26
            r13 = r27
            goto L_0x0071
        L_0x03b5:
            r0 = move-exception
            r8 = r0
            r4 = r17
            goto L_0x0412
        L_0x03bb:
            r0 = move-exception
            r4 = r17
            goto L_0x0408
        L_0x03bf:
            r0 = move-exception
            r23 = r8
            r24 = r9
            r8 = r0
            r4 = r17
            goto L_0x0412
        L_0x03c8:
            r0 = move-exception
            r23 = r8
            r24 = r9
            r4 = r17
            goto L_0x0408
        L_0x03d0:
            r23 = r8
            r24 = r9
            r25 = r11
            r26 = r12
            r27 = r13
            goto L_0x03e7
        L_0x03db:
            r23 = r8
            r24 = r9
            r25 = r11
            r26 = r12
            r27 = r13
            r4 = 9
        L_0x03e7:
            r5 = 1
            int r15 = r15 + -1
            r4 = r17
            r8 = r23
            r9 = r24
            r11 = r25
            r12 = r26
            r13 = r27
            goto L_0x0082
        L_0x03f8:
            r0 = move-exception
            r17 = r4
            r23 = r8
            r24 = r9
            r8 = r0
            goto L_0x0412
        L_0x0401:
            r0 = move-exception
            r17 = r4
            r23 = r8
            r24 = r9
        L_0x0408:
            javax.mail.MessagingException r8 = new javax.mail.MessagingException     // Catch:{ all -> 0x0410 }
            java.lang.String r9 = "IO Error"
            r8.<init>(r9, r0)     // Catch:{ all -> 0x0410 }
            throw r8     // Catch:{ all -> 0x0410 }
        L_0x0410:
            r0 = move-exception
            r8 = r0
        L_0x0412:
            r2.close()     // Catch:{ IOException -> 0x0416 }
            goto L_0x0417
        L_0x0416:
            r0 = move-exception
        L_0x0417:
            throw r8     // Catch:{ all -> 0x0431 }
        L_0x0418:
            r17 = r4
            r23 = r8
            r24 = r9
            javax.mail.MessagingException r4 = new javax.mail.MessagingException     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "Missing boundary parameter"
            r4.<init>(r5)     // Catch:{ all -> 0x0431 }
            throw r4     // Catch:{ all -> 0x0431 }
        L_0x0426:
            r0 = move-exception
            r17 = r4
        L_0x0429:
            javax.mail.MessagingException r4 = new javax.mail.MessagingException     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = "No inputstream from datasource"
            r4.<init>(r5, r0)     // Catch:{ all -> 0x0431 }
            throw r4     // Catch:{ all -> 0x0431 }
        L_0x0431:
            r0 = move-exception
            monitor-exit(r33)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: javax.mail.internet.MimeMultipart.parse():void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:131:0x01b6, code lost:
        if (ignoreMissingEndBoundary == false) goto L_0x01d6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x01b8, code lost:
        if (r3 == null) goto L_0x01c0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x01ba, code lost:
        r17 = r3.getPosition();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x01c0, code lost:
        r29 = r13;
        r1.complete = false;
        r35 = r4;
        r31 = r5;
        r21 = r12;
        r19 = true;
        r30 = r15;
        r4 = r17;
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x01d6, code lost:
        r29 = r13;
        r30 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:136:0x01e1, code lost:
        throw new javax.mail.MessagingException("missing multipart end boundary");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x029d, code lost:
        r35 = r4;
        r21 = r5;
        r12 = r15;
        r4 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x046b, code lost:
        if (r4 > 0) goto L_0x046e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x046e, code lost:
        r4 = r4 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:?, code lost:
        r30[r4] = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:0x04f9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x04fa, code lost:
        r8 = r0;
        r4 = r17;
        r6 = r20;
        r10 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x0503, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x0504, code lost:
        r4 = r17;
        r6 = r20;
        r10 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x0111, code lost:
        if (ignoreMissingEndBoundary == false) goto L_0x0123;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x0113, code lost:
        r19 = r6;
        r1.complete = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x0123, code lost:
        r19 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x012e, code lost:
        throw new javax.mail.MessagingException("missing multipart end boundary");
     */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x012f  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x0238 A[Catch:{ IOException -> 0x01eb, all -> 0x01e2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0244 A[Catch:{ IOException -> 0x01eb, all -> 0x01e2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0252  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x025f A[Catch:{ IOException -> 0x032c, all -> 0x0323 }] */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0277 A[Catch:{ IOException -> 0x032c, all -> 0x0323 }] */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x0288 A[Catch:{ IOException -> 0x032c, all -> 0x0323 }] */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x0310  */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x010f A[EDGE_INSN: B:364:0x010f->B:85:0x010f ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0287 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:377:0x0283 A[EDGE_INSN: B:377:0x0283->B:187:0x0283 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void parsebm() throws javax.mail.MessagingException {
        /*
            r37 = this;
            r1 = r37
            monitor-enter(r37)
            boolean r0 = r1.parsed     // Catch:{ all -> 0x05eb }
            if (r0 == 0) goto L_0x0009
            monitor-exit(r37)
            return
        L_0x0009:
            r2 = 0
            r3 = 0
            r4 = 0
            r6 = 0
            javax.activation.DataSource r0 = r1.ds     // Catch:{ Exception -> 0x05de }
            java.io.InputStream r0 = r0.getInputStream()     // Catch:{ Exception -> 0x05de }
            r2 = r0
            boolean r0 = r2 instanceof java.io.ByteArrayInputStream     // Catch:{ Exception -> 0x05de }
            if (r0 != 0) goto L_0x0031
            boolean r0 = r2 instanceof java.io.BufferedInputStream     // Catch:{ Exception -> 0x002a }
            if (r0 != 0) goto L_0x0031
            boolean r0 = r2 instanceof javax.mail.internet.SharedInputStream     // Catch:{ Exception -> 0x002a }
            if (r0 != 0) goto L_0x0031
            java.io.BufferedInputStream r0 = new java.io.BufferedInputStream     // Catch:{ Exception -> 0x002a }
            r0.<init>(r2)     // Catch:{ Exception -> 0x002a }
            r2 = r0
            goto L_0x0031
        L_0x002a:
            r0 = move-exception
            r17 = r4
            r20 = r6
            goto L_0x05e3
        L_0x0031:
            boolean r0 = r2 instanceof javax.mail.internet.SharedInputStream     // Catch:{ all -> 0x05eb }
            if (r0 == 0) goto L_0x0039
            r0 = r2
            javax.mail.internet.SharedInputStream r0 = (javax.mail.internet.SharedInputStream) r0     // Catch:{ all -> 0x05eb }
            r3 = r0
        L_0x0039:
            javax.mail.internet.ContentType r0 = new javax.mail.internet.ContentType     // Catch:{ all -> 0x05eb }
            java.lang.String r8 = r1.contentType     // Catch:{ all -> 0x05eb }
            r0.<init>(r8)     // Catch:{ all -> 0x05eb }
            r8 = r0
            r0 = 0
            java.lang.String r9 = "boundary"
            java.lang.String r9 = r8.getParameter(r9)     // Catch:{ all -> 0x05eb }
            if (r9 == 0) goto L_0x005b
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x05eb }
            java.lang.String r11 = "--"
            r10.<init>(r11)     // Catch:{ all -> 0x05eb }
            java.lang.StringBuilder r10 = r10.append(r9)     // Catch:{ all -> 0x05eb }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x05eb }
            r0 = r10
            goto L_0x0060
        L_0x005b:
            boolean r10 = ignoreMissingBoundaryParameter     // Catch:{ all -> 0x05eb }
            if (r10 == 0) goto L_0x05ce
            r10 = r0
        L_0x0060:
            com.sun.mail.util.LineInputStream r0 = new com.sun.mail.util.LineInputStream     // Catch:{ IOException -> 0x05b5, all -> 0x05aa }
            r0.<init>(r2)     // Catch:{ IOException -> 0x05b5, all -> 0x05aa }
            r11 = r0
            r0 = 0
            r12 = 0
            r13 = r12
            r12 = r0
        L_0x006a:
            java.lang.String r0 = r11.readLine()     // Catch:{ IOException -> 0x05b5, all -> 0x05aa }
            r14 = r0
            r15 = 1
            if (r0 != 0) goto L_0x0075
            r17 = r4
            goto L_0x00b8
        L_0x0075:
            int r0 = r14.length()     // Catch:{ IOException -> 0x05b5, all -> 0x05aa }
            int r0 = r0 - r15
            r15 = r0
        L_0x007b:
            if (r15 >= 0) goto L_0x0080
            r17 = r4
            goto L_0x008f
        L_0x0080:
            char r0 = r14.charAt(r15)     // Catch:{ IOException -> 0x05b5, all -> 0x05aa }
            r17 = r4
            r4 = 32
            if (r0 == r4) goto L_0x0589
            r4 = 9
            if (r0 == r4) goto L_0x0589
        L_0x008f:
            int r0 = r15 + 1
            r4 = 0
            java.lang.String r0 = r14.substring(r4, r0)     // Catch:{ IOException -> 0x057f, all -> 0x0574 }
            r14 = r0
            if (r10 == 0) goto L_0x00ae
            boolean r0 = r14.equals(r10)     // Catch:{ IOException -> 0x00cd, all -> 0x00c3 }
            if (r0 == 0) goto L_0x00a0
            goto L_0x00b8
        L_0x00a0:
            r20 = r6
            r22 = r8
            r23 = r9
            r25 = r11
            r27 = r12
            r29 = r13
            goto L_0x0518
        L_0x00ae:
            java.lang.String r0 = "--"
            boolean r0 = r14.startsWith(r0)     // Catch:{ IOException -> 0x057f, all -> 0x0574 }
            if (r0 == 0) goto L_0x050c
            r10 = r14
        L_0x00b8:
            if (r14 == 0) goto L_0x04e3
            if (r12 == 0) goto L_0x00d6
            java.lang.String r0 = r12.toString()     // Catch:{ IOException -> 0x00cd, all -> 0x00c3 }
            r1.preamble = r0     // Catch:{ IOException -> 0x00cd, all -> 0x00c3 }
            goto L_0x00d6
        L_0x00c3:
            r0 = move-exception
            r22 = r8
            r23 = r9
            r4 = r17
            r8 = r0
            goto L_0x05c8
        L_0x00cd:
            r0 = move-exception
            r22 = r8
            r23 = r9
            r4 = r17
            goto L_0x05be
        L_0x00d6:
            byte[] r0 = com.sun.mail.util.ASCIIUtility.getBytes((java.lang.String) r10)     // Catch:{ IOException -> 0x04d6, all -> 0x04c8 }
            int r4 = r0.length     // Catch:{ IOException -> 0x04d6, all -> 0x04c8 }
            r5 = 256(0x100, float:3.59E-43)
            int[] r5 = new int[r5]     // Catch:{ IOException -> 0x04d6, all -> 0x04c8 }
            r15 = 0
        L_0x00e0:
            if (r15 < r4) goto L_0x0498
            int[] r15 = new int[r4]     // Catch:{ IOException -> 0x04d6, all -> 0x04c8 }
            r19 = r4
            r20 = r6
            r6 = r19
        L_0x00ea:
            if (r6 > 0) goto L_0x0452
            int r6 = r4 + -1
            r7 = 1
            r15[r6] = r7     // Catch:{ IOException -> 0x0445, all -> 0x0437 }
            r6 = 0
        L_0x00f3:
            if (r6 == 0) goto L_0x00f6
            goto L_0x0119
        L_0x00f6:
            r7 = 0
            if (r3 == 0) goto L_0x0151
            long r22 = r3.getPosition()     // Catch:{ IOException -> 0x0146, all -> 0x013a }
            r17 = r22
        L_0x00ff:
            java.lang.String r19 = r11.readLine()     // Catch:{ IOException -> 0x0146, all -> 0x013a }
            r14 = r19
            if (r19 == 0) goto L_0x010d
            int r19 = r14.length()     // Catch:{ IOException -> 0x0146, all -> 0x013a }
            if (r19 > 0) goto L_0x00ff
        L_0x010d:
            if (r14 != 0) goto L_0x012f
            boolean r16 = ignoreMissingEndBoundary     // Catch:{ IOException -> 0x0146, all -> 0x013a }
            if (r16 == 0) goto L_0x0123
            r19 = r6
            r6 = 0
            r1.complete = r6     // Catch:{ IOException -> 0x0146, all -> 0x013a }
        L_0x0119:
            r2.close()     // Catch:{ IOException -> 0x011d }
            goto L_0x011e
        L_0x011d:
            r0 = move-exception
        L_0x011e:
            r4 = 1
            r1.parsed = r4     // Catch:{ all -> 0x05eb }
            monitor-exit(r37)
            return
        L_0x0123:
            r19 = r6
            javax.mail.MessagingException r6 = new javax.mail.MessagingException     // Catch:{ IOException -> 0x0146, all -> 0x013a }
            r22 = r7
            java.lang.String r7 = "missing multipart end boundary"
            r6.<init>(r7)     // Catch:{ IOException -> 0x0146, all -> 0x013a }
            throw r6     // Catch:{ IOException -> 0x0146, all -> 0x013a }
        L_0x012f:
            r19 = r6
            r22 = r7
            r23 = r9
            r22 = r8
            r8 = r17
            goto L_0x0160
        L_0x013a:
            r0 = move-exception
            r22 = r8
            r23 = r9
            r4 = r17
            r6 = r20
            r8 = r0
            goto L_0x05c8
        L_0x0146:
            r0 = move-exception
            r22 = r8
            r23 = r9
            r4 = r17
            r6 = r20
            goto L_0x05be
        L_0x0151:
            r19 = r6
            r22 = r7
            javax.mail.internet.InternetHeaders r6 = r1.createInternetHeaders(r2)     // Catch:{ IOException -> 0x0445, all -> 0x0437 }
            r7 = r6
            r22 = r8
            r23 = r9
            r8 = r17
        L_0x0160:
            boolean r6 = r2.markSupported()     // Catch:{ IOException -> 0x042c, all -> 0x0420 }
            if (r6 == 0) goto L_0x03f2
            r6 = 0
            if (r3 != 0) goto L_0x0180
            java.io.ByteArrayOutputStream r17 = new java.io.ByteArrayOutputStream     // Catch:{ IOException -> 0x017a, all -> 0x0173 }
            r17.<init>()     // Catch:{ IOException -> 0x017a, all -> 0x0173 }
            r6 = r17
            r17 = r20
            goto L_0x0184
        L_0x0173:
            r0 = move-exception
            r4 = r8
            r6 = r20
            r8 = r0
            goto L_0x05c8
        L_0x017a:
            r0 = move-exception
            r4 = r8
            r6 = r20
            goto L_0x05be
        L_0x0180:
            long r17 = r3.getPosition()     // Catch:{ IOException -> 0x042c, all -> 0x0420 }
        L_0x0184:
            r24 = r10
            byte[] r10 = new byte[r4]     // Catch:{ IOException -> 0x03e7, all -> 0x03db }
            r20 = r10
            byte[] r10 = new byte[r4]     // Catch:{ IOException -> 0x03e7, all -> 0x03db }
            r21 = 0
            r25 = 0
            r26 = 1
            r36 = r11
            r11 = r10
            r10 = r20
            r20 = r26
            r26 = r14
            r14 = r25
            r25 = r36
        L_0x019f:
            r27 = r12
            int r12 = r4 + 4
            int r12 = r12 + 1000
            r2.mark(r12)     // Catch:{ IOException -> 0x03e7, all -> 0x03db }
            r12 = 0
            r28 = r12
            r12 = 0
            int r29 = readFully(r2, r10, r12, r4)     // Catch:{ IOException -> 0x03e7, all -> 0x03db }
            r12 = r29
            if (r12 >= r4) goto L_0x01f3
            boolean r21 = ignoreMissingEndBoundary     // Catch:{ IOException -> 0x01eb, all -> 0x01e2 }
            if (r21 == 0) goto L_0x01d6
            if (r3 == 0) goto L_0x01c0
            long r29 = r3.getPosition()     // Catch:{ IOException -> 0x01eb, all -> 0x01e2 }
            r17 = r29
        L_0x01c0:
            r29 = r13
            r13 = 0
            r1.complete = r13     // Catch:{ IOException -> 0x01eb, all -> 0x01e2 }
            r13 = 1
            r35 = r4
            r31 = r5
            r21 = r12
            r19 = r13
            r30 = r15
            r4 = r17
            r12 = r28
            goto L_0x02a4
        L_0x01d6:
            r29 = r13
            javax.mail.MessagingException r13 = new javax.mail.MessagingException     // Catch:{ IOException -> 0x01eb, all -> 0x01e2 }
            r30 = r15
            java.lang.String r15 = "missing multipart end boundary"
            r13.<init>(r15)     // Catch:{ IOException -> 0x01eb, all -> 0x01e2 }
            throw r13     // Catch:{ IOException -> 0x01eb, all -> 0x01e2 }
        L_0x01e2:
            r0 = move-exception
            r4 = r8
            r6 = r17
            r10 = r24
            r8 = r0
            goto L_0x05c8
        L_0x01eb:
            r0 = move-exception
            r4 = r8
            r6 = r17
            r10 = r24
            goto L_0x05be
        L_0x01f3:
            r29 = r13
            r30 = r15
            int r13 = r4 + -1
        L_0x01f9:
            if (r13 >= 0) goto L_0x01fe
            r31 = r5
            goto L_0x0207
        L_0x01fe:
            byte r15 = r10[r13]     // Catch:{ IOException -> 0x03e7, all -> 0x03db }
            r31 = r5
            byte r5 = r0[r13]     // Catch:{ IOException -> 0x03e7, all -> 0x03db }
            if (r15 == r5) goto L_0x03c5
        L_0x0207:
            if (r13 >= 0) goto L_0x0334
            r5 = 0
            if (r20 != 0) goto L_0x0232
            int r28 = r14 + -1
            byte r28 = r11[r28]     // Catch:{ IOException -> 0x01eb, all -> 0x01e2 }
            r32 = r28
            r15 = r32
            r32 = r5
            r5 = 13
            if (r15 == r5) goto L_0x021f
            r5 = 10
            if (r15 != r5) goto L_0x0234
            goto L_0x0221
        L_0x021f:
            r5 = 10
        L_0x0221:
            r32 = 1
            if (r15 != r5) goto L_0x0234
            r5 = 2
            if (r14 < r5) goto L_0x0234
            int r5 = r14 + -2
            byte r5 = r11[r5]     // Catch:{ IOException -> 0x01eb, all -> 0x01e2 }
            r15 = 13
            if (r5 != r15) goto L_0x0234
            r15 = 2
            goto L_0x0236
        L_0x0232:
            r32 = r5
        L_0x0234:
            r15 = r32
        L_0x0236:
            if (r20 != 0) goto L_0x0242
            if (r15 <= 0) goto L_0x023b
            goto L_0x0242
        L_0x023b:
            r35 = r4
            r34 = r13
            r13 = r12
            goto L_0x0308
        L_0x0242:
            if (r3 == 0) goto L_0x0252
            long r32 = r3.getPosition()     // Catch:{ IOException -> 0x01eb, all -> 0x01e2 }
            r5 = r12
            r34 = r13
            long r12 = (long) r4
            long r32 = r32 - r12
            long r12 = (long) r15
            long r32 = r32 - r12
            goto L_0x0257
        L_0x0252:
            r5 = r12
            r34 = r13
            r32 = r17
        L_0x0257:
            int r12 = r2.read()     // Catch:{ IOException -> 0x032c, all -> 0x0323 }
            r13 = 45
            if (r12 != r13) goto L_0x0277
            int r13 = r2.read()     // Catch:{ IOException -> 0x032c, all -> 0x0323 }
            r18 = r12
            r12 = 45
            if (r13 != r12) goto L_0x0279
            r12 = 1
            r1.complete = r12     // Catch:{ IOException -> 0x032c, all -> 0x0323 }
            r12 = 1
            r35 = r4
            r21 = r5
            r19 = r12
            r12 = r15
            r4 = r32
            goto L_0x02a4
        L_0x0277:
            r18 = r12
        L_0x0279:
            r12 = r18
        L_0x027b:
            r13 = 32
            if (r12 == r13) goto L_0x0310
            r13 = 9
            if (r12 == r13) goto L_0x0310
            r13 = 10
            if (r12 != r13) goto L_0x0288
            goto L_0x029d
        L_0x0288:
            r13 = 13
            if (r12 != r13) goto L_0x0301
            r13 = 1
            r2.mark(r13)     // Catch:{ IOException -> 0x032c, all -> 0x0323 }
            int r13 = r2.read()     // Catch:{ IOException -> 0x032c, all -> 0x0323 }
            r17 = r12
            r12 = 10
            if (r13 == r12) goto L_0x029d
            r2.reset()     // Catch:{ IOException -> 0x032c, all -> 0x0323 }
        L_0x029d:
            r35 = r4
            r21 = r5
            r12 = r15
            r4 = r32
        L_0x02a4:
            if (r3 == 0) goto L_0x02c1
            java.io.InputStream r13 = r3.newStream(r8, r4)     // Catch:{ IOException -> 0x02ba, all -> 0x02b2 }
            javax.mail.internet.MimeBodyPart r13 = r1.createMimeBodyPart(r13)     // Catch:{ IOException -> 0x02ba, all -> 0x02b2 }
            r15 = r13
            r13 = r21
            goto L_0x02e2
        L_0x02b2:
            r0 = move-exception
            r6 = r4
            r4 = r8
            r10 = r24
            r8 = r0
            goto L_0x05c8
        L_0x02ba:
            r0 = move-exception
            r6 = r4
            r4 = r8
            r10 = r24
            goto L_0x05be
        L_0x02c1:
            int r13 = r14 - r12
            if (r13 <= 0) goto L_0x02cb
            int r13 = r14 - r12
            r15 = 0
            r6.write(r11, r15, r13)     // Catch:{ IOException -> 0x02ba, all -> 0x02b2 }
        L_0x02cb:
            boolean r13 = r1.complete     // Catch:{ IOException -> 0x02ba, all -> 0x02b2 }
            if (r13 != 0) goto L_0x02d8
            if (r21 <= 0) goto L_0x02d8
            r13 = r21
            r15 = 0
            r6.write(r10, r15, r13)     // Catch:{ IOException -> 0x02ba, all -> 0x02b2 }
            goto L_0x02da
        L_0x02d8:
            r13 = r21
        L_0x02da:
            byte[] r15 = r6.toByteArray()     // Catch:{ IOException -> 0x02ba, all -> 0x02b2 }
            javax.mail.internet.MimeBodyPart r15 = r1.createMimeBodyPart(r7, r15)     // Catch:{ IOException -> 0x02ba, all -> 0x02b2 }
        L_0x02e2:
            super.addBodyPart(r15)     // Catch:{ IOException -> 0x02ba, all -> 0x02b2 }
            r20 = r4
            r17 = r8
            r6 = r19
            r8 = r22
            r9 = r23
            r10 = r24
            r11 = r25
            r14 = r26
            r12 = r27
            r13 = r29
            r15 = r30
            r5 = r31
            r4 = r35
            goto L_0x00f3
        L_0x0301:
            r35 = r4
            r13 = r5
            r17 = r12
            r17 = r32
        L_0x0308:
            r4 = 0
            r12 = r15
            r36 = r13
            r13 = r4
            r4 = r36
            goto L_0x033b
        L_0x0310:
            r35 = r4
            r4 = r5
            r17 = r12
            r12 = 10
            r13 = 13
            int r5 = r2.read()     // Catch:{ IOException -> 0x032c, all -> 0x0323 }
            r12 = r5
            r5 = r4
            r4 = r35
            goto L_0x027b
        L_0x0323:
            r0 = move-exception
            r4 = r8
            r10 = r24
            r6 = r32
            r8 = r0
            goto L_0x05c8
        L_0x032c:
            r0 = move-exception
            r4 = r8
            r10 = r24
            r6 = r32
            goto L_0x05be
        L_0x0334:
            r35 = r4
            r4 = r12
            r34 = r13
            r12 = r28
        L_0x033b:
            int r5 = r13 + 1
            byte r15 = r10[r13]     // Catch:{ IOException -> 0x03e7, all -> 0x03db }
            r15 = r15 & 127(0x7f, float:1.78E-43)
            r15 = r31[r15]     // Catch:{ IOException -> 0x03e7, all -> 0x03db }
            int r5 = r5 - r15
            r15 = r30[r13]     // Catch:{ IOException -> 0x03e7, all -> 0x03db }
            int r5 = java.lang.Math.max(r5, r15)     // Catch:{ IOException -> 0x03e7, all -> 0x03db }
            r15 = 2
            if (r5 >= r15) goto L_0x0385
            if (r3 != 0) goto L_0x035b
            r15 = 1
            if (r14 <= r15) goto L_0x035b
            int r15 = r14 + -1
            r21 = r4
            r4 = 0
            r6.write(r11, r4, r15)     // Catch:{ IOException -> 0x01eb, all -> 0x01e2 }
            goto L_0x035d
        L_0x035b:
            r21 = r4
        L_0x035d:
            r2.reset()     // Catch:{ IOException -> 0x03e7, all -> 0x03db }
            r4 = r7
            r32 = r8
            r7 = 1
            r1.skipFully(r2, r7)     // Catch:{ IOException -> 0x03bc, all -> 0x03b2 }
            r7 = 1
            if (r14 < r7) goto L_0x037b
            int r7 = r14 + -1
            byte r7 = r11[r7]     // Catch:{ IOException -> 0x03bc, all -> 0x03b2 }
            r8 = 0
            r11[r8] = r7     // Catch:{ IOException -> 0x03bc, all -> 0x03b2 }
            byte r7 = r10[r8]     // Catch:{ IOException -> 0x03bc, all -> 0x03b2 }
            r8 = 1
            r11[r8] = r7     // Catch:{ IOException -> 0x03bc, all -> 0x03b2 }
            r7 = 2
            r14 = r7
            r7 = 0
            goto L_0x03a1
        L_0x037b:
            r8 = r7
            r7 = 0
            byte r9 = r10[r7]     // Catch:{ IOException -> 0x03bc, all -> 0x03b2 }
            r11[r7] = r9     // Catch:{ IOException -> 0x03bc, all -> 0x03b2 }
            r7 = 1
            r14 = r7
            r7 = 0
            goto L_0x03a1
        L_0x0385:
            r21 = r4
            r4 = r7
            r32 = r8
            r8 = 1
            if (r14 <= 0) goto L_0x0394
            if (r3 != 0) goto L_0x0394
            r7 = 0
            r6.write(r11, r7, r14)     // Catch:{ IOException -> 0x03bc, all -> 0x03b2 }
            goto L_0x0395
        L_0x0394:
            r7 = 0
        L_0x0395:
            r9 = r5
            r2.reset()     // Catch:{ IOException -> 0x03bc, all -> 0x03b2 }
            long r14 = (long) r9     // Catch:{ IOException -> 0x03bc, all -> 0x03b2 }
            r1.skipFully(r2, r14)     // Catch:{ IOException -> 0x03bc, all -> 0x03b2 }
            r14 = r10
            r10 = r11
            r11 = r14
            r14 = r9
        L_0x03a1:
            r20 = 0
            r7 = r4
            r12 = r27
            r13 = r29
            r15 = r30
            r5 = r31
            r8 = r32
            r4 = r35
            goto L_0x019f
        L_0x03b2:
            r0 = move-exception
            r8 = r0
            r6 = r17
            r10 = r24
            r4 = r32
            goto L_0x05c8
        L_0x03bc:
            r0 = move-exception
            r6 = r17
            r10 = r24
            r4 = r32
            goto L_0x05be
        L_0x03c5:
            r35 = r4
            r4 = r7
            r32 = r8
            r21 = r12
            r34 = r13
            r7 = 0
            r8 = 1
            int r13 = r34 + -1
            r7 = r4
            r5 = r31
            r8 = r32
            r4 = r35
            goto L_0x01f9
        L_0x03db:
            r0 = move-exception
            r32 = r8
            r8 = r0
            r6 = r17
            r10 = r24
            r4 = r32
            goto L_0x05c8
        L_0x03e7:
            r0 = move-exception
            r32 = r8
            r6 = r17
            r10 = r24
            r4 = r32
            goto L_0x05be
        L_0x03f2:
            r35 = r4
            r31 = r5
            r4 = r7
            r32 = r8
            r24 = r10
            r25 = r11
            r27 = r12
            r29 = r13
            r26 = r14
            r30 = r15
            javax.mail.MessagingException r5 = new javax.mail.MessagingException     // Catch:{ IOException -> 0x0417, all -> 0x040d }
            java.lang.String r6 = "Stream doesn't support mark"
            r5.<init>(r6)     // Catch:{ IOException -> 0x0417, all -> 0x040d }
            throw r5     // Catch:{ IOException -> 0x0417, all -> 0x040d }
        L_0x040d:
            r0 = move-exception
            r8 = r0
            r6 = r20
            r10 = r24
            r4 = r32
            goto L_0x05c8
        L_0x0417:
            r0 = move-exception
            r6 = r20
            r10 = r24
            r4 = r32
            goto L_0x05be
        L_0x0420:
            r0 = move-exception
            r32 = r8
            r24 = r10
            r8 = r0
            r6 = r20
            r4 = r32
            goto L_0x05c8
        L_0x042c:
            r0 = move-exception
            r32 = r8
            r24 = r10
            r6 = r20
            r4 = r32
            goto L_0x05be
        L_0x0437:
            r0 = move-exception
            r22 = r8
            r23 = r9
            r24 = r10
            r8 = r0
            r4 = r17
            r6 = r20
            goto L_0x05c8
        L_0x0445:
            r0 = move-exception
            r22 = r8
            r23 = r9
            r24 = r10
            r4 = r17
            r6 = r20
            goto L_0x05be
        L_0x0452:
            r35 = r4
            r31 = r5
            r22 = r8
            r23 = r9
            r24 = r10
            r25 = r11
            r27 = r12
            r29 = r13
            r30 = r15
            r7 = 0
            r8 = 1
            int r4 = r35 + -1
        L_0x0468:
            if (r4 >= r6) goto L_0x0473
        L_0x046b:
            if (r4 > 0) goto L_0x046e
            goto L_0x0482
        L_0x046e:
            int r4 = r4 + -1
            r30[r4] = r6     // Catch:{ IOException -> 0x0503, all -> 0x04f9 }
            goto L_0x046b
        L_0x0473:
            byte r5 = r0[r4]     // Catch:{ IOException -> 0x0503, all -> 0x04f9 }
            int r9 = r4 - r6
            byte r9 = r0[r9]     // Catch:{ IOException -> 0x0503, all -> 0x04f9 }
            if (r5 != r9) goto L_0x0482
            int r5 = r4 + -1
            r30[r5] = r6     // Catch:{ IOException -> 0x0503, all -> 0x04f9 }
            int r4 = r4 + -1
            goto L_0x0468
        L_0x0482:
            int r6 = r6 + -1
            r8 = r22
            r9 = r23
            r10 = r24
            r11 = r25
            r12 = r27
            r13 = r29
            r15 = r30
            r5 = r31
            r4 = r35
            goto L_0x00ea
        L_0x0498:
            r35 = r4
            r31 = r5
            r20 = r6
            r22 = r8
            r23 = r9
            r24 = r10
            r25 = r11
            r27 = r12
            r29 = r13
            r7 = 0
            r8 = 1
            byte r4 = r0[r15]     // Catch:{ IOException -> 0x0503, all -> 0x04f9 }
            int r5 = r15 + 1
            r31[r4] = r5     // Catch:{ IOException -> 0x0503, all -> 0x04f9 }
            int r15 = r15 + 1
            r6 = r20
            r8 = r22
            r9 = r23
            r10 = r24
            r11 = r25
            r12 = r27
            r13 = r29
            r5 = r31
            r4 = r35
            goto L_0x00e0
        L_0x04c8:
            r0 = move-exception
            r20 = r6
            r22 = r8
            r23 = r9
            r24 = r10
            r8 = r0
            r4 = r17
            goto L_0x05c8
        L_0x04d6:
            r0 = move-exception
            r20 = r6
            r22 = r8
            r23 = r9
            r24 = r10
            r4 = r17
            goto L_0x05be
        L_0x04e3:
            r20 = r6
            r22 = r8
            r23 = r9
            r24 = r10
            r25 = r11
            r27 = r12
            r29 = r13
            javax.mail.MessagingException r0 = new javax.mail.MessagingException     // Catch:{ IOException -> 0x0503, all -> 0x04f9 }
            java.lang.String r4 = "Missing start boundary"
            r0.<init>(r4)     // Catch:{ IOException -> 0x0503, all -> 0x04f9 }
            throw r0     // Catch:{ IOException -> 0x0503, all -> 0x04f9 }
        L_0x04f9:
            r0 = move-exception
            r8 = r0
            r4 = r17
            r6 = r20
            r10 = r24
            goto L_0x05c8
        L_0x0503:
            r0 = move-exception
            r4 = r17
            r6 = r20
            r10 = r24
            goto L_0x05be
        L_0x050c:
            r20 = r6
            r22 = r8
            r23 = r9
            r25 = r11
            r27 = r12
            r29 = r13
        L_0x0518:
            int r0 = r14.length()     // Catch:{ IOException -> 0x056e, all -> 0x0566 }
            if (r0 <= 0) goto L_0x0556
            if (r29 != 0) goto L_0x0530
            java.lang.String r0 = "line.separator"
            java.lang.String r4 = "\n"
            java.lang.String r0 = java.lang.System.getProperty(r0, r4)     // Catch:{ SecurityException -> 0x052b }
            r13 = r0
            goto L_0x0532
        L_0x052b:
            r0 = move-exception
            java.lang.String r4 = "\n"
            r13 = r4
            goto L_0x0532
        L_0x0530:
            r13 = r29
        L_0x0532:
            if (r27 != 0) goto L_0x0541
            java.lang.StringBuffer r0 = new java.lang.StringBuffer     // Catch:{ IOException -> 0x056e, all -> 0x0566 }
            int r4 = r14.length()     // Catch:{ IOException -> 0x056e, all -> 0x0566 }
            r5 = 2
            int r4 = r4 + r5
            r0.<init>(r4)     // Catch:{ IOException -> 0x056e, all -> 0x0566 }
            r12 = r0
            goto L_0x0543
        L_0x0541:
            r12 = r27
        L_0x0543:
            java.lang.StringBuffer r0 = r12.append(r14)     // Catch:{ IOException -> 0x056e, all -> 0x0566 }
            r0.append(r13)     // Catch:{ IOException -> 0x056e, all -> 0x0566 }
            r4 = r17
            r6 = r20
            r8 = r22
            r9 = r23
            r11 = r25
            goto L_0x006a
        L_0x0556:
            r4 = r17
            r6 = r20
            r8 = r22
            r9 = r23
            r11 = r25
            r12 = r27
            r13 = r29
            goto L_0x006a
        L_0x0566:
            r0 = move-exception
            r8 = r0
            r4 = r17
            r6 = r20
            goto L_0x05c8
        L_0x056e:
            r0 = move-exception
            r4 = r17
            r6 = r20
            goto L_0x05be
        L_0x0574:
            r0 = move-exception
            r20 = r6
            r22 = r8
            r23 = r9
            r8 = r0
            r4 = r17
            goto L_0x05c8
        L_0x057f:
            r0 = move-exception
            r20 = r6
            r22 = r8
            r23 = r9
            r4 = r17
            goto L_0x05be
        L_0x0589:
            r20 = r6
            r22 = r8
            r23 = r9
            r25 = r11
            r27 = r12
            r29 = r13
            r5 = 2
            r7 = 0
            r8 = 1
            int r15 = r15 + -1
            r4 = r17
            r6 = r20
            r8 = r22
            r9 = r23
            r11 = r25
            r12 = r27
            r13 = r29
            goto L_0x007b
        L_0x05aa:
            r0 = move-exception
            r17 = r4
            r20 = r6
            r22 = r8
            r23 = r9
            r8 = r0
            goto L_0x05c8
        L_0x05b5:
            r0 = move-exception
            r17 = r4
            r20 = r6
            r22 = r8
            r23 = r9
        L_0x05be:
            javax.mail.MessagingException r8 = new javax.mail.MessagingException     // Catch:{ all -> 0x05c6 }
            java.lang.String r9 = "IO Error"
            r8.<init>(r9, r0)     // Catch:{ all -> 0x05c6 }
            throw r8     // Catch:{ all -> 0x05c6 }
        L_0x05c6:
            r0 = move-exception
            r8 = r0
        L_0x05c8:
            r2.close()     // Catch:{ IOException -> 0x05cc }
            goto L_0x05cd
        L_0x05cc:
            r0 = move-exception
        L_0x05cd:
            throw r8     // Catch:{ all -> 0x05eb }
        L_0x05ce:
            r17 = r4
            r20 = r6
            r22 = r8
            r23 = r9
            javax.mail.MessagingException r4 = new javax.mail.MessagingException     // Catch:{ all -> 0x05eb }
            java.lang.String r5 = "Missing boundary parameter"
            r4.<init>(r5)     // Catch:{ all -> 0x05eb }
            throw r4     // Catch:{ all -> 0x05eb }
        L_0x05de:
            r0 = move-exception
            r17 = r4
            r20 = r6
        L_0x05e3:
            javax.mail.MessagingException r4 = new javax.mail.MessagingException     // Catch:{ all -> 0x05eb }
            java.lang.String r5 = "No inputstream from datasource"
            r4.<init>(r5, r0)     // Catch:{ all -> 0x05eb }
            throw r4     // Catch:{ all -> 0x05eb }
        L_0x05eb:
            r0 = move-exception
            monitor-exit(r37)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: javax.mail.internet.MimeMultipart.parsebm():void");
    }

    private static int readFully(InputStream in, byte[] buf, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        int total = 0;
        while (len > 0) {
            int bsize = in.read(buf, off, len);
            if (bsize <= 0) {
                break;
            }
            off += bsize;
            total += bsize;
            len -= bsize;
        }
        if (total > 0) {
            return total;
        }
        return -1;
    }

    private void skipFully(InputStream in, long offset) throws IOException {
        while (offset > 0) {
            long cur = in.skip(offset);
            if (cur > 0) {
                offset -= cur;
            } else {
                throw new EOFException("can't skip");
            }
        }
    }

    /* access modifiers changed from: protected */
    public InternetHeaders createInternetHeaders(InputStream is) throws MessagingException {
        return new InternetHeaders(is);
    }

    /* access modifiers changed from: protected */
    public MimeBodyPart createMimeBodyPart(InternetHeaders headers, byte[] content) throws MessagingException {
        return new MimeBodyPart(headers, content);
    }

    /* access modifiers changed from: protected */
    public MimeBodyPart createMimeBodyPart(InputStream is) throws MessagingException {
        return new MimeBodyPart(is);
    }
}
