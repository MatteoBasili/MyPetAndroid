package com.sun.mail.imap.protocol;

import java.io.PrintStream;
import java.util.Properties;

public class IMAPSaslAuthenticator implements SaslAuthenticator {
    /* access modifiers changed from: private */
    public boolean debug;
    private String host;
    private String name;
    /* access modifiers changed from: private */
    public PrintStream out;
    private IMAPProtocol pr;
    private Properties props;

    public IMAPSaslAuthenticator(IMAPProtocol pr2, String name2, Properties props2, boolean debug2, PrintStream out2, String host2) {
        this.pr = pr2;
        this.name = name2;
        this.props = props2;
        this.debug = debug2;
        this.out = out2;
        this.host = host2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:114:0x0252, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:115:0x0253, code lost:
        r19 = r7;
        r21 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x0259, code lost:
        if (r1.debug != false) goto L_0x025b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:117:0x025b, code lost:
        r1.out.println("IMAP SASL DEBUG: AUTHENTICATE Exception: " + r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:119:0x0270, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x006f, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0102, code lost:
        return false;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x0242 A[Catch:{ Exception -> 0x0252 }] */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x0283 A[Catch:{ Exception -> 0x0252 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean authenticate(java.lang.String[] r23, java.lang.String r24, java.lang.String r25, java.lang.String r26, java.lang.String r27) throws com.sun.mail.iap.ProtocolException {
        /*
            r22 = this;
            r1 = r22
            r8 = r23
            com.sun.mail.imap.protocol.IMAPProtocol r9 = r1.pr
            monitor-enter(r9)
            java.util.Vector r0 = new java.util.Vector     // Catch:{ all -> 0x029a }
            r0.<init>()     // Catch:{ all -> 0x029a }
            r10 = r0
            r11 = 0
            r12 = 0
            r13 = 0
            boolean r0 = r1.debug     // Catch:{ all -> 0x029a }
            if (r0 == 0) goto L_0x003e
            java.io.PrintStream r0 = r1.out     // Catch:{ all -> 0x029a }
            java.lang.String r2 = "IMAP SASL DEBUG: Mechanisms:"
            r0.print(r2)     // Catch:{ all -> 0x029a }
            r0 = 0
        L_0x001c:
            int r2 = r8.length     // Catch:{ all -> 0x029a }
            if (r0 < r2) goto L_0x0025
            java.io.PrintStream r0 = r1.out     // Catch:{ all -> 0x029a }
            r0.println()     // Catch:{ all -> 0x029a }
            goto L_0x003e
        L_0x0025:
            java.io.PrintStream r2 = r1.out     // Catch:{ all -> 0x029a }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x029a }
            java.lang.String r4 = " "
            r3.<init>(r4)     // Catch:{ all -> 0x029a }
            r4 = r8[r0]     // Catch:{ all -> 0x029a }
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ all -> 0x029a }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x029a }
            r2.print(r3)     // Catch:{ all -> 0x029a }
            int r0 = r0 + 1
            goto L_0x001c
        L_0x003e:
            r14 = r24
            r15 = r26
            r6 = r27
            com.sun.mail.imap.protocol.IMAPSaslAuthenticator$1 r7 = new com.sun.mail.imap.protocol.IMAPSaslAuthenticator$1     // Catch:{ all -> 0x029a }
            r7.<init>(r15, r6, r14)     // Catch:{ all -> 0x029a }
            r5 = 0
            java.lang.String r4 = r1.name     // Catch:{ SaslException -> 0x0278 }
            java.lang.String r0 = r1.host     // Catch:{ SaslException -> 0x0278 }
            java.util.Properties r3 = r1.props     // Catch:{ SaslException -> 0x0278 }
            r2 = r23
            r16 = r3
            r3 = r25
            r8 = r5
            r5 = r0
            r17 = r6
            r6 = r16
            javax.security.sasl.SaslClient r0 = javax.security.sasl.Sasl.createSaslClient(r2, r3, r4, r5, r6, r7)     // Catch:{ SaslException -> 0x0272 }
            r2 = r0
            if (r2 != 0) goto L_0x0070
            boolean r0 = r1.debug     // Catch:{ all -> 0x029a }
            if (r0 == 0) goto L_0x006e
            java.io.PrintStream r0 = r1.out     // Catch:{ all -> 0x029a }
            java.lang.String r3 = "IMAP SASL DEBUG: No SASL support"
            r0.println(r3)     // Catch:{ all -> 0x029a }
        L_0x006e:
            monitor-exit(r9)     // Catch:{ all -> 0x029a }
            return r8
        L_0x0070:
            boolean r0 = r1.debug     // Catch:{ all -> 0x029a }
            if (r0 == 0) goto L_0x008c
            java.io.PrintStream r0 = r1.out     // Catch:{ all -> 0x029a }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x029a }
            java.lang.String r4 = "IMAP SASL DEBUG: SASL client "
            r3.<init>(r4)     // Catch:{ all -> 0x029a }
            java.lang.String r4 = r2.getMechanismName()     // Catch:{ all -> 0x029a }
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ all -> 0x029a }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x029a }
            r0.println(r3)     // Catch:{ all -> 0x029a }
        L_0x008c:
            com.sun.mail.imap.protocol.IMAPProtocol r0 = r1.pr     // Catch:{ Exception -> 0x0252 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0252 }
            java.lang.String r4 = "AUTHENTICATE "
            r3.<init>(r4)     // Catch:{ Exception -> 0x0252 }
            java.lang.String r4 = r2.getMechanismName()     // Catch:{ Exception -> 0x0252 }
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ Exception -> 0x0252 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0252 }
            r4 = 0
            java.lang.String r0 = r0.writeCommand(r3, r4)     // Catch:{ Exception -> 0x0252 }
            r3 = r0
            com.sun.mail.imap.protocol.IMAPProtocol r0 = r1.pr     // Catch:{ all -> 0x029a }
            java.io.OutputStream r0 = r0.getIMAPOutputStream()     // Catch:{ all -> 0x029a }
            r5 = r0
            java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x029a }
            r0.<init>()     // Catch:{ all -> 0x029a }
            r6 = r0
            r0 = 2
            byte[] r0 = new byte[r0]     // Catch:{ all -> 0x029a }
            r11 = 13
            r0[r8] = r11     // Catch:{ all -> 0x029a }
            r11 = 10
            r16 = 1
            r0[r16] = r11     // Catch:{ all -> 0x029a }
            r11 = r0
            java.lang.String r0 = r2.getMechanismName()     // Catch:{ all -> 0x029a }
            java.lang.String r4 = "XGWTRUSTEDAPP"
            boolean r0 = r0.equals(r4)     // Catch:{ all -> 0x029a }
            r4 = r0
        L_0x00cf:
            if (r13 == 0) goto L_0x0120
            boolean r0 = r2.isComplete()     // Catch:{ all -> 0x029a }
            if (r0 == 0) goto L_0x0106
            java.lang.String r0 = "javax.security.sasl.qop"
            java.lang.Object r0 = r2.getNegotiatedProperty(r0)     // Catch:{ all -> 0x029a }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ all -> 0x029a }
            if (r0 == 0) goto L_0x0104
            java.lang.String r8 = "auth-int"
            boolean r8 = r0.equalsIgnoreCase(r8)     // Catch:{ all -> 0x029a }
            if (r8 != 0) goto L_0x00f1
            java.lang.String r8 = "auth-conf"
            boolean r8 = r0.equalsIgnoreCase(r8)     // Catch:{ all -> 0x029a }
            if (r8 == 0) goto L_0x0106
        L_0x00f1:
            boolean r8 = r1.debug     // Catch:{ all -> 0x029a }
            if (r8 == 0) goto L_0x00ff
            java.io.PrintStream r8 = r1.out     // Catch:{ all -> 0x029a }
            r18 = r0
            java.lang.String r0 = "IMAP SASL DEBUG: Mechanism requires integrity or confidentiality"
            r8.println(r0)     // Catch:{ all -> 0x029a }
            goto L_0x0101
        L_0x00ff:
            r18 = r0
        L_0x0101:
            monitor-exit(r9)     // Catch:{ all -> 0x029a }
            r8 = 0
            return r8
        L_0x0104:
            r18 = r0
        L_0x0106:
            int r0 = r10.size()     // Catch:{ all -> 0x029a }
            com.sun.mail.iap.Response[] r0 = new com.sun.mail.iap.Response[r0]     // Catch:{ all -> 0x029a }
            r10.copyInto(r0)     // Catch:{ all -> 0x029a }
            com.sun.mail.imap.protocol.IMAPProtocol r8 = r1.pr     // Catch:{ all -> 0x029a }
            r8.notifyResponseHandlers(r0)     // Catch:{ all -> 0x029a }
            com.sun.mail.imap.protocol.IMAPProtocol r8 = r1.pr     // Catch:{ all -> 0x029a }
            r8.handleResult(r12)     // Catch:{ all -> 0x029a }
            com.sun.mail.imap.protocol.IMAPProtocol r8 = r1.pr     // Catch:{ all -> 0x029a }
            r8.setCapabilities(r12)     // Catch:{ all -> 0x029a }
            monitor-exit(r9)     // Catch:{ all -> 0x029a }
            return r16
        L_0x0120:
            com.sun.mail.imap.protocol.IMAPProtocol r0 = r1.pr     // Catch:{ Exception -> 0x0237 }
            com.sun.mail.iap.Response r0 = r0.readResponse()     // Catch:{ Exception -> 0x0237 }
            r12 = r0
            boolean r0 = r12.isContinuation()     // Catch:{ Exception -> 0x0237 }
            if (r0 == 0) goto L_0x01fd
            r8 = 0
            r0 = r8
            byte[] r0 = (byte[]) r0     // Catch:{ Exception -> 0x0237 }
            r0 = r8
            boolean r18 = r2.isComplete()     // Catch:{ Exception -> 0x0237 }
            if (r18 != 0) goto L_0x0197
            com.sun.mail.iap.ByteArray r18 = r12.readByteArray()     // Catch:{ Exception -> 0x0237 }
            byte[] r18 = r18.getNewBytes()     // Catch:{ Exception -> 0x0237 }
            r0 = r18
            int r8 = r0.length     // Catch:{ Exception -> 0x0237 }
            if (r8 <= 0) goto L_0x0154
            byte[] r8 = com.sun.mail.util.BASE64DecoderStream.decode(r0)     // Catch:{ Exception -> 0x014b }
            r0 = r8
            goto L_0x0154
        L_0x014b:
            r0 = move-exception
            r19 = r7
            r20 = r13
            r21 = r14
            goto L_0x023e
        L_0x0154:
            boolean r8 = r1.debug     // Catch:{ Exception -> 0x0237 }
            if (r8 == 0) goto L_0x018b
            java.io.PrintStream r8 = r1.out     // Catch:{ Exception -> 0x0237 }
            r19 = r7
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0184 }
            r20 = r13
            java.lang.String r13 = "IMAP SASL DEBUG: challenge: "
            r7.<init>(r13)     // Catch:{ Exception -> 0x017f }
            int r13 = r0.length     // Catch:{ Exception -> 0x017f }
            r21 = r14
            r14 = 0
            java.lang.String r13 = com.sun.mail.util.ASCIIUtility.toString(r0, r14, r13)     // Catch:{ Exception -> 0x0235 }
            java.lang.StringBuilder r7 = r7.append(r13)     // Catch:{ Exception -> 0x0235 }
            java.lang.String r13 = " :"
            java.lang.StringBuilder r7 = r7.append(r13)     // Catch:{ Exception -> 0x0235 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0235 }
            r8.println(r7)     // Catch:{ Exception -> 0x0235 }
            goto L_0x0191
        L_0x017f:
            r0 = move-exception
            r21 = r14
            goto L_0x023e
        L_0x0184:
            r0 = move-exception
            r20 = r13
            r21 = r14
            goto L_0x023e
        L_0x018b:
            r19 = r7
            r20 = r13
            r21 = r14
        L_0x0191:
            byte[] r7 = r2.evaluateChallenge(r0)     // Catch:{ Exception -> 0x0235 }
            r0 = r7
            goto L_0x019d
        L_0x0197:
            r19 = r7
            r20 = r13
            r21 = r14
        L_0x019d:
            if (r0 != 0) goto L_0x01b5
            boolean r7 = r1.debug     // Catch:{ Exception -> 0x0235 }
            if (r7 == 0) goto L_0x01aa
            java.io.PrintStream r7 = r1.out     // Catch:{ Exception -> 0x0235 }
            java.lang.String r8 = "IMAP SASL DEBUG: no response"
            r7.println(r8)     // Catch:{ Exception -> 0x0235 }
        L_0x01aa:
            r5.write(r11)     // Catch:{ Exception -> 0x0235 }
            r5.flush()     // Catch:{ Exception -> 0x0235 }
            r6.reset()     // Catch:{ Exception -> 0x0235 }
            goto L_0x022c
        L_0x01b5:
            boolean r7 = r1.debug     // Catch:{ Exception -> 0x0235 }
            if (r7 == 0) goto L_0x01d9
            java.io.PrintStream r7 = r1.out     // Catch:{ Exception -> 0x0235 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0235 }
            java.lang.String r13 = "IMAP SASL DEBUG: response: "
            r8.<init>(r13)     // Catch:{ Exception -> 0x0235 }
            int r13 = r0.length     // Catch:{ Exception -> 0x0235 }
            r14 = 0
            java.lang.String r13 = com.sun.mail.util.ASCIIUtility.toString(r0, r14, r13)     // Catch:{ Exception -> 0x0235 }
            java.lang.StringBuilder r8 = r8.append(r13)     // Catch:{ Exception -> 0x0235 }
            java.lang.String r13 = " :"
            java.lang.StringBuilder r8 = r8.append(r13)     // Catch:{ Exception -> 0x0235 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x0235 }
            r7.println(r8)     // Catch:{ Exception -> 0x0235 }
        L_0x01d9:
            byte[] r7 = com.sun.mail.util.BASE64EncoderStream.encode(r0)     // Catch:{ Exception -> 0x0235 }
            r0 = r7
            if (r4 == 0) goto L_0x01e9
            java.lang.String r7 = "XGWTRUSTEDAPP "
            byte[] r7 = r7.getBytes()     // Catch:{ Exception -> 0x0235 }
            r6.write(r7)     // Catch:{ Exception -> 0x0235 }
        L_0x01e9:
            r6.write(r0)     // Catch:{ Exception -> 0x0235 }
            r6.write(r11)     // Catch:{ Exception -> 0x0235 }
            byte[] r7 = r6.toByteArray()     // Catch:{ Exception -> 0x0235 }
            r5.write(r7)     // Catch:{ Exception -> 0x0235 }
            r5.flush()     // Catch:{ Exception -> 0x0235 }
            r6.reset()     // Catch:{ Exception -> 0x0235 }
            goto L_0x022c
        L_0x01fd:
            r19 = r7
            r20 = r13
            r21 = r14
            boolean r0 = r12.isTagged()     // Catch:{ Exception -> 0x0235 }
            if (r0 == 0) goto L_0x021b
            java.lang.String r0 = r12.getTag()     // Catch:{ Exception -> 0x0235 }
            boolean r0 = r0.equals(r3)     // Catch:{ Exception -> 0x0235 }
            if (r0 == 0) goto L_0x021b
            r13 = 1
            r7 = r19
            r14 = r21
            r8 = 0
            goto L_0x00cf
        L_0x021b:
            boolean r0 = r12.isBYE()     // Catch:{ Exception -> 0x0235 }
            if (r0 == 0) goto L_0x0229
            r13 = 1
            r7 = r19
            r14 = r21
            r8 = 0
            goto L_0x00cf
        L_0x0229:
            r10.addElement(r12)     // Catch:{ Exception -> 0x0235 }
        L_0x022c:
            r7 = r19
            r13 = r20
            r14 = r21
            r8 = 0
            goto L_0x00cf
        L_0x0235:
            r0 = move-exception
            goto L_0x023e
        L_0x0237:
            r0 = move-exception
            r19 = r7
            r20 = r13
            r21 = r14
        L_0x023e:
            boolean r7 = r1.debug     // Catch:{ all -> 0x029a }
            if (r7 == 0) goto L_0x0245
            r0.printStackTrace()     // Catch:{ all -> 0x029a }
        L_0x0245:
            com.sun.mail.iap.Response r7 = com.sun.mail.iap.Response.byeResponse(r0)     // Catch:{ all -> 0x029a }
            r12 = r7
            r13 = 1
            r7 = r19
            r14 = r21
            r8 = 0
            goto L_0x00cf
        L_0x0252:
            r0 = move-exception
            r19 = r7
            r21 = r14
            boolean r3 = r1.debug     // Catch:{ all -> 0x029a }
            if (r3 == 0) goto L_0x026f
            java.io.PrintStream r3 = r1.out     // Catch:{ all -> 0x029a }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x029a }
            java.lang.String r5 = "IMAP SASL DEBUG: AUTHENTICATE Exception: "
            r4.<init>(r5)     // Catch:{ all -> 0x029a }
            java.lang.StringBuilder r4 = r4.append(r0)     // Catch:{ all -> 0x029a }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x029a }
            r3.println(r4)     // Catch:{ all -> 0x029a }
        L_0x026f:
            monitor-exit(r9)     // Catch:{ all -> 0x029a }
            r3 = 0
            return r3
        L_0x0272:
            r0 = move-exception
            r19 = r7
            r21 = r14
            goto L_0x027f
        L_0x0278:
            r0 = move-exception
            r17 = r6
            r19 = r7
            r21 = r14
        L_0x027f:
            boolean r2 = r1.debug     // Catch:{ all -> 0x029a }
            if (r2 == 0) goto L_0x0297
            java.io.PrintStream r2 = r1.out     // Catch:{ all -> 0x029a }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x029a }
            java.lang.String r4 = "IMAP SASL DEBUG: Failed to create SASL client: "
            r3.<init>(r4)     // Catch:{ all -> 0x029a }
            java.lang.StringBuilder r3 = r3.append(r0)     // Catch:{ all -> 0x029a }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x029a }
            r2.println(r3)     // Catch:{ all -> 0x029a }
        L_0x0297:
            monitor-exit(r9)     // Catch:{ all -> 0x029a }
            r2 = 0
            return r2
        L_0x029a:
            r0 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x029a }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.protocol.IMAPSaslAuthenticator.authenticate(java.lang.String[], java.lang.String, java.lang.String, java.lang.String, java.lang.String):boolean");
    }
}
