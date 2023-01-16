package com.sun.mail.smtp;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.LineInputStream;
import com.sun.mail.util.SocketFetcher;
import com.sun.mail.util.TraceInputStream;
import com.sun.mail.util.TraceOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import javax.mail.internet.ParseException;

public class SMTPTransport extends Transport {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final byte[] CRLF = {13, 10};
    private static final String UNKNOWN = "UNKNOWN";
    private static char[] hexchar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final String[] ignoreList = {"Bcc", "Content-Length"};
    private Address[] addresses;
    private SMTPOutputStream dataStream;
    private int defaultPort;
    private MessagingException exception;
    private Hashtable extMap;
    private Address[] invalidAddr;
    private boolean isSSL;
    private int lastReturnCode;
    private String lastServerResponse;
    private LineInputStream lineInputStream;
    private String localHostName;
    private DigestMD5 md5support;
    private MimeMessage message;
    private String name;
    private PrintStream out;
    private boolean quitWait;
    private boolean reportSuccess;
    private String saslRealm;
    private boolean sendPartiallyFailed;
    private BufferedInputStream serverInput;
    private OutputStream serverOutput;
    private Socket serverSocket;
    private boolean useRset;
    private boolean useStartTLS;
    private Address[] validSentAddr;
    private Address[] validUnsentAddr;

    public SMTPTransport(Session session, URLName urlname) {
        this(session, urlname, "smtp", 25, false);
    }

    protected SMTPTransport(Session session, URLName urlname, String name2, int defaultPort2, boolean isSSL2) {
        super(session, urlname);
        this.name = "smtp";
        this.defaultPort = 25;
        boolean z = false;
        this.isSSL = false;
        this.sendPartiallyFailed = false;
        this.quitWait = false;
        this.saslRealm = UNKNOWN;
        name2 = urlname != null ? urlname.getProtocol() : name2;
        this.name = name2;
        this.defaultPort = defaultPort2;
        this.isSSL = isSSL2;
        this.out = session.getDebugOut();
        String s = session.getProperty("mail." + name2 + ".quitwait");
        this.quitWait = s == null || s.equalsIgnoreCase("true");
        String s2 = session.getProperty("mail." + name2 + ".reportsuccess");
        this.reportSuccess = s2 != null && s2.equalsIgnoreCase("true");
        String s3 = session.getProperty("mail." + name2 + ".starttls.enable");
        this.useStartTLS = s3 != null && s3.equalsIgnoreCase("true");
        String s4 = session.getProperty("mail." + name2 + ".userset");
        if (s4 != null && s4.equalsIgnoreCase("true")) {
            z = true;
        }
        this.useRset = z;
    }

    public synchronized String getLocalHost() {
        try {
            String str = this.localHostName;
            if (str == null || str.length() <= 0) {
                this.localHostName = this.session.getProperty("mail." + this.name + ".localhost");
            }
            String str2 = this.localHostName;
            if (str2 == null || str2.length() <= 0) {
                this.localHostName = this.session.getProperty("mail." + this.name + ".localaddress");
            }
            String str3 = this.localHostName;
            if (str3 == null || str3.length() <= 0) {
                InetAddress localHost = InetAddress.getLocalHost();
                String hostName = localHost.getHostName();
                this.localHostName = hostName;
                if (hostName == null) {
                    this.localHostName = "[" + localHost.getHostAddress() + "]";
                }
            }
        } catch (UnknownHostException e) {
        }
        return this.localHostName;
    }

    public synchronized void setLocalHost(String localhost) {
        this.localHostName = localhost;
    }

    public synchronized void connect(Socket socket) throws MessagingException {
        this.serverSocket = socket;
        super.connect();
    }

    public synchronized String getSASLRealm() {
        if (this.saslRealm == UNKNOWN) {
            String property = this.session.getProperty("mail." + this.name + ".sasl.realm");
            this.saslRealm = property;
            if (property == null) {
                this.saslRealm = this.session.getProperty("mail." + this.name + ".saslrealm");
            }
        }
        return this.saslRealm;
    }

    public synchronized void setSASLRealm(String saslRealm2) {
        this.saslRealm = saslRealm2;
    }

    public synchronized boolean getReportSuccess() {
        return this.reportSuccess;
    }

    public synchronized void setReportSuccess(boolean reportSuccess2) {
        this.reportSuccess = reportSuccess2;
    }

    public synchronized boolean getStartTLS() {
        return this.useStartTLS;
    }

    public synchronized void setStartTLS(boolean useStartTLS2) {
        this.useStartTLS = useStartTLS2;
    }

    public synchronized boolean getUseRset() {
        return this.useRset;
    }

    public synchronized void setUseRset(boolean useRset2) {
        this.useRset = useRset2;
    }

    public synchronized String getLastServerResponse() {
        return this.lastServerResponse;
    }

    public synchronized int getLastReturnCode() {
        return this.lastReturnCode;
    }

    private synchronized DigestMD5 getMD5() {
        if (this.md5support == null) {
            this.md5support = new DigestMD5(this.debug ? this.out : null);
        }
        return this.md5support;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x01f9 A[Catch:{ all -> 0x0215 }] */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x021b  */
    /* JADX WARNING: Removed duplicated region for block: B:148:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:149:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean protocolConnect(java.lang.String r20, int r21, java.lang.String r22, java.lang.String r23) throws javax.mail.MessagingException {
        /*
            r19 = this;
            r1 = r19
            javax.mail.Session r0 = r1.session
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            java.lang.String r3 = "mail."
            r2.<init>(r3)
            java.lang.String r4 = r1.name
            java.lang.StringBuilder r2 = r2.append(r4)
            java.lang.String r4 = ".ehlo"
            java.lang.StringBuilder r2 = r2.append(r4)
            java.lang.String r2 = r2.toString()
            java.lang.String r8 = r0.getProperty(r2)
            r10 = 0
            if (r8 == 0) goto L_0x002c
            java.lang.String r0 = "false"
            boolean r0 = r8.equalsIgnoreCase(r0)
            if (r0 == 0) goto L_0x002c
            r0 = r10
            goto L_0x002d
        L_0x002c:
            r0 = 1
        L_0x002d:
            r11 = r0
            javax.mail.Session r0 = r1.session
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>(r3)
            java.lang.String r4 = r1.name
            java.lang.StringBuilder r2 = r2.append(r4)
            java.lang.String r4 = ".auth"
            java.lang.StringBuilder r2 = r2.append(r4)
            java.lang.String r2 = r2.toString()
            java.lang.String r12 = r0.getProperty(r2)
            if (r12 == 0) goto L_0x0056
            java.lang.String r0 = "true"
            boolean r0 = r12.equalsIgnoreCase(r0)
            if (r0 == 0) goto L_0x0056
            r0 = 1
            goto L_0x0057
        L_0x0056:
            r0 = r10
        L_0x0057:
            r13 = r0
            boolean r0 = r1.debug
            if (r0 == 0) goto L_0x007a
            java.io.PrintStream r0 = r1.out
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            java.lang.String r4 = "DEBUG SMTP: useEhlo "
            r2.<init>(r4)
            java.lang.StringBuilder r2 = r2.append(r11)
            java.lang.String r4 = ", useAuth "
            java.lang.StringBuilder r2 = r2.append(r4)
            java.lang.StringBuilder r2 = r2.append(r13)
            java.lang.String r2 = r2.toString()
            r0.println(r2)
        L_0x007a:
            if (r13 == 0) goto L_0x0081
            if (r22 == 0) goto L_0x0080
            if (r23 != 0) goto L_0x0081
        L_0x0080:
            return r10
        L_0x0081:
            r0 = -1
            r2 = r21
            if (r2 != r0) goto L_0x00ad
            javax.mail.Session r0 = r1.session
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>(r3)
            java.lang.String r3 = r1.name
            java.lang.StringBuilder r3 = r4.append(r3)
            java.lang.String r4 = ".port"
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.String r0 = r0.getProperty(r3)
            if (r0 == 0) goto L_0x00a9
            int r2 = java.lang.Integer.parseInt(r0)
            r14 = r2
            goto L_0x00ae
        L_0x00a9:
            int r2 = r1.defaultPort
            r14 = r2
            goto L_0x00ae
        L_0x00ad:
            r14 = r2
        L_0x00ae:
            if (r20 == 0) goto L_0x00ba
            int r0 = r20.length()
            if (r0 != 0) goto L_0x00b7
            goto L_0x00ba
        L_0x00b7:
            r15 = r20
            goto L_0x00bd
        L_0x00ba:
            java.lang.String r0 = "localhost"
            r15 = r0
        L_0x00bd:
            r0 = 0
            java.net.Socket r2 = r1.serverSocket
            if (r2 == 0) goto L_0x00c6
            r19.openServer()
            goto L_0x00c9
        L_0x00c6:
            r1.openServer(r15, r14)
        L_0x00c9:
            if (r11 == 0) goto L_0x00d6
            java.lang.String r2 = r19.getLocalHost()
            boolean r0 = r1.ehlo(r2)
            r16 = r0
            goto L_0x00d8
        L_0x00d6:
            r16 = r0
        L_0x00d8:
            if (r16 != 0) goto L_0x00e1
            java.lang.String r0 = r19.getLocalHost()
            r1.helo(r0)
        L_0x00e1:
            boolean r0 = r1.useStartTLS
            if (r0 == 0) goto L_0x00f7
            java.lang.String r0 = "STARTTLS"
            boolean r0 = r1.supportsExtension(r0)
            if (r0 == 0) goto L_0x00f7
            r19.startTLS()
            java.lang.String r0 = r19.getLocalHost()
            r1.ehlo(r0)
        L_0x00f7:
            if (r13 != 0) goto L_0x00fd
            if (r22 == 0) goto L_0x0282
            if (r23 == 0) goto L_0x0282
        L_0x00fd:
            java.lang.String r0 = "AUTH"
            boolean r0 = r1.supportsExtension(r0)
            java.lang.String r2 = "AUTH=LOGIN"
            if (r0 != 0) goto L_0x010d
            boolean r0 = r1.supportsExtension(r2)
            if (r0 == 0) goto L_0x0282
        L_0x010d:
            boolean r0 = r1.debug
            java.lang.String r3 = "LOGIN"
            if (r0 == 0) goto L_0x012d
            java.io.PrintStream r0 = r1.out
            java.lang.String r4 = "DEBUG SMTP: Attempt to authenticate"
            r0.println(r4)
            boolean r0 = r1.supportsAuthentication(r3)
            if (r0 != 0) goto L_0x012d
            boolean r0 = r1.supportsExtension(r2)
            if (r0 == 0) goto L_0x012d
            java.io.PrintStream r0 = r1.out
            java.lang.String r4 = "DEBUG SMTP: use AUTH=LOGIN hack"
            r0.println(r4)
        L_0x012d:
            boolean r0 = r1.supportsAuthentication(r3)
            r3 = 2147483647(0x7fffffff, float:NaN)
            r7 = 334(0x14e, float:4.68E-43)
            r6 = 235(0xeb, float:3.3E-43)
            if (r0 != 0) goto L_0x0225
            boolean r0 = r1.supportsExtension(r2)
            if (r0 == 0) goto L_0x0144
            r2 = r6
            r0 = r7
            goto L_0x0227
        L_0x0144:
            java.lang.String r0 = "PLAIN"
            boolean r0 = r1.supportsAuthentication(r0)
            if (r0 == 0) goto L_0x018e
            java.lang.String r0 = "AUTH PLAIN"
            int r2 = r1.simpleCommand((java.lang.String) r0)
            java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream     // Catch:{ IOException -> 0x018a, all -> 0x0185 }
            r0.<init>()     // Catch:{ IOException -> 0x018a, all -> 0x0185 }
            com.sun.mail.util.BASE64EncoderStream r4 = new com.sun.mail.util.BASE64EncoderStream     // Catch:{ IOException -> 0x018a, all -> 0x0185 }
            r4.<init>(r0, r3)     // Catch:{ IOException -> 0x018a, all -> 0x0185 }
            r3 = r4
            if (r2 != r7) goto L_0x017f
            r3.write(r10)     // Catch:{ IOException -> 0x018a, all -> 0x0185 }
            byte[] r4 = com.sun.mail.util.ASCIIUtility.getBytes((java.lang.String) r22)     // Catch:{ IOException -> 0x018a, all -> 0x0185 }
            r3.write(r4)     // Catch:{ IOException -> 0x018a, all -> 0x0185 }
            r3.write(r10)     // Catch:{ IOException -> 0x018a, all -> 0x0185 }
            byte[] r4 = com.sun.mail.util.ASCIIUtility.getBytes((java.lang.String) r23)     // Catch:{ IOException -> 0x018a, all -> 0x0185 }
            r3.write(r4)     // Catch:{ IOException -> 0x018a, all -> 0x0185 }
            r3.flush()     // Catch:{ IOException -> 0x018a, all -> 0x0185 }
            byte[] r4 = r0.toByteArray()     // Catch:{ IOException -> 0x018a, all -> 0x0185 }
            int r4 = r1.simpleCommand((byte[]) r4)     // Catch:{ IOException -> 0x018a, all -> 0x0185 }
            r2 = r4
        L_0x017f:
            if (r2 == r6) goto L_0x0282
        L_0x0181:
            r19.closeConnection()
            return r10
        L_0x0185:
            r0 = move-exception
            if (r2 == r6) goto L_0x0189
            goto L_0x0181
        L_0x0189:
            throw r0
        L_0x018a:
            r0 = move-exception
            if (r2 == r6) goto L_0x0282
            goto L_0x0181
        L_0x018e:
            java.lang.String r0 = "DIGEST-MD5"
            boolean r0 = r1.supportsAuthentication(r0)
            if (r0 == 0) goto L_0x0282
            com.sun.mail.smtp.DigestMD5 r0 = r19.getMD5()
            r5 = r0
            if (r0 == 0) goto L_0x0223
            java.lang.String r0 = "AUTH DIGEST-MD5"
            int r4 = r1.simpleCommand((java.lang.String) r0)
            if (r4 != r7) goto L_0x021c
            java.lang.String r0 = r19.getSASLRealm()     // Catch:{ Exception -> 0x01f1, all -> 0x01eb }
            java.lang.String r3 = r1.lastServerResponse     // Catch:{ Exception -> 0x01f1, all -> 0x01eb }
            r2 = r5
            r17 = r3
            r3 = r15
            r18 = r4
            r4 = r22
            r9 = r5
            r5 = r23
            r6 = r0
            r0 = r7
            r7 = r17
            byte[] r2 = r2.authClient(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x01e7, all -> 0x01e1 }
            int r3 = r1.simpleCommand((byte[]) r2)     // Catch:{ Exception -> 0x01e7, all -> 0x01e1 }
            r4 = r3
            if (r4 != r0) goto L_0x01de
            java.lang.String r0 = r1.lastServerResponse     // Catch:{ Exception -> 0x01dc }
            boolean r0 = r9.authServer(r0)     // Catch:{ Exception -> 0x01dc }
            if (r0 != 0) goto L_0x01d2
            r4 = -1
            r2 = 235(0xeb, float:3.3E-43)
            goto L_0x0220
        L_0x01d2:
            byte[] r0 = new byte[r10]     // Catch:{ Exception -> 0x01dc }
            int r0 = r1.simpleCommand((byte[]) r0)     // Catch:{ Exception -> 0x01dc }
            r4 = r0
            r2 = 235(0xeb, float:3.3E-43)
            goto L_0x0220
        L_0x01dc:
            r0 = move-exception
            goto L_0x01f5
        L_0x01de:
            r2 = 235(0xeb, float:3.3E-43)
            goto L_0x0220
        L_0x01e1:
            r0 = move-exception
            r4 = r18
            r2 = 235(0xeb, float:3.3E-43)
            goto L_0x0218
        L_0x01e7:
            r0 = move-exception
            r4 = r18
            goto L_0x01f5
        L_0x01eb:
            r0 = move-exception
            r18 = r4
            r9 = r5
            r2 = r6
            goto L_0x0218
        L_0x01f1:
            r0 = move-exception
            r18 = r4
            r9 = r5
        L_0x01f5:
            boolean r2 = r1.debug     // Catch:{ all -> 0x0215 }
            if (r2 == 0) goto L_0x020d
            java.io.PrintStream r2 = r1.out     // Catch:{ all -> 0x0215 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0215 }
            java.lang.String r5 = "DEBUG SMTP: DIGEST-MD5: "
            r3.<init>(r5)     // Catch:{ all -> 0x0215 }
            java.lang.StringBuilder r3 = r3.append(r0)     // Catch:{ all -> 0x0215 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0215 }
            r2.println(r3)     // Catch:{ all -> 0x0215 }
        L_0x020d:
            r2 = 235(0xeb, float:3.3E-43)
            if (r4 == r2) goto L_0x0282
        L_0x0211:
            r19.closeConnection()
            return r10
        L_0x0215:
            r0 = move-exception
            r2 = 235(0xeb, float:3.3E-43)
        L_0x0218:
            if (r4 == r2) goto L_0x021b
            goto L_0x0211
        L_0x021b:
            throw r0
        L_0x021c:
            r18 = r4
            r9 = r5
            r2 = r6
        L_0x0220:
            if (r4 == r2) goto L_0x0282
            goto L_0x0211
        L_0x0223:
            r9 = r5
            goto L_0x0282
        L_0x0225:
            r2 = r6
            r0 = r7
        L_0x0227:
            java.lang.String r4 = "AUTH LOGIN"
            int r5 = r1.simpleCommand((java.lang.String) r4)
            r6 = 530(0x212, float:7.43E-43)
            if (r5 != r6) goto L_0x0238
            r19.startTLS()
            int r5 = r1.simpleCommand((java.lang.String) r4)
        L_0x0238:
            java.io.ByteArrayOutputStream r4 = new java.io.ByteArrayOutputStream     // Catch:{ IOException -> 0x027e, all -> 0x0279 }
            r4.<init>()     // Catch:{ IOException -> 0x027e, all -> 0x0279 }
            com.sun.mail.util.BASE64EncoderStream r6 = new com.sun.mail.util.BASE64EncoderStream     // Catch:{ IOException -> 0x027e, all -> 0x0279 }
            r6.<init>(r4, r3)     // Catch:{ IOException -> 0x027e, all -> 0x0279 }
            r3 = r6
            if (r5 != r0) goto L_0x025b
            byte[] r6 = com.sun.mail.util.ASCIIUtility.getBytes((java.lang.String) r22)     // Catch:{ IOException -> 0x027e, all -> 0x0279 }
            r3.write(r6)     // Catch:{ IOException -> 0x027e, all -> 0x0279 }
            r3.flush()     // Catch:{ IOException -> 0x027e, all -> 0x0279 }
            byte[] r6 = r4.toByteArray()     // Catch:{ IOException -> 0x027e, all -> 0x0279 }
            int r6 = r1.simpleCommand((byte[]) r6)     // Catch:{ IOException -> 0x027e, all -> 0x0279 }
            r5 = r6
            r4.reset()     // Catch:{ IOException -> 0x027e, all -> 0x0279 }
        L_0x025b:
            if (r5 != r0) goto L_0x0273
            byte[] r0 = com.sun.mail.util.ASCIIUtility.getBytes((java.lang.String) r23)     // Catch:{ IOException -> 0x027e, all -> 0x0279 }
            r3.write(r0)     // Catch:{ IOException -> 0x027e, all -> 0x0279 }
            r3.flush()     // Catch:{ IOException -> 0x027e, all -> 0x0279 }
            byte[] r0 = r4.toByteArray()     // Catch:{ IOException -> 0x027e, all -> 0x0279 }
            int r0 = r1.simpleCommand((byte[]) r0)     // Catch:{ IOException -> 0x027e, all -> 0x0279 }
            r5 = r0
            r4.reset()     // Catch:{ IOException -> 0x027e, all -> 0x0279 }
        L_0x0273:
            if (r5 == r2) goto L_0x0282
        L_0x0275:
            r19.closeConnection()
            return r10
        L_0x0279:
            r0 = move-exception
            if (r5 == r2) goto L_0x027d
            goto L_0x0275
        L_0x027d:
            throw r0
        L_0x027e:
            r0 = move-exception
            if (r5 == r2) goto L_0x0282
            goto L_0x0275
        L_0x0282:
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.smtp.SMTPTransport.protocolConnect(java.lang.String, int, java.lang.String, java.lang.String):boolean");
    }

    public synchronized void sendMessage(Message message2, Address[] addresses2) throws MessagingException, SendFailedException {
        boolean use8bit;
        Message message3 = message2;
        Address[] addressArr = addresses2;
        synchronized (this) {
            checkConnected();
            if (!(message3 instanceof MimeMessage)) {
                if (this.debug) {
                    this.out.println("DEBUG SMTP: Can only send RFC822 msgs");
                }
                throw new MessagingException("SMTP can only send RFC822 messages");
            }
            int i = 0;
            while (i < addressArr.length) {
                if (addressArr[i] instanceof InternetAddress) {
                    i++;
                } else {
                    throw new MessagingException(addressArr[i] + " is not an InternetAddress");
                }
            }
            this.message = (MimeMessage) message3;
            this.addresses = addressArr;
            this.validUnsentAddr = addressArr;
            expandGroups();
            boolean use8bit2 = false;
            if (message3 instanceof SMTPMessage) {
                use8bit2 = ((SMTPMessage) message3).getAllow8bitMIME();
            }
            if (!use8bit2) {
                String ebStr = this.session.getProperty("mail." + this.name + ".allow8bitmime");
                use8bit = ebStr != null && ebStr.equalsIgnoreCase("true");
            } else {
                use8bit = use8bit2;
            }
            if (this.debug) {
                this.out.println("DEBUG SMTP: use8bit " + use8bit);
            }
            if (use8bit && supportsExtension("8BITMIME") && convertTo8Bit(this.message)) {
                try {
                    this.message.saveChanges();
                } catch (MessagingException e) {
                }
            }
            try {
                mailFrom();
                rcptTo();
                this.message.writeTo(data(), ignoreList);
                finishData();
                if (this.sendPartiallyFailed) {
                    if (this.debug) {
                        this.out.println("DEBUG SMTP: Sending partially failed because of invalid destination addresses");
                    }
                    notifyTransportListeners(3, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
                    throw new SMTPSendFailedException(".", this.lastReturnCode, this.lastServerResponse, this.exception, this.validSentAddr, this.validUnsentAddr, this.invalidAddr);
                }
                notifyTransportListeners(1, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
                this.invalidAddr = null;
                this.validUnsentAddr = null;
                this.validSentAddr = null;
                this.addresses = null;
                this.message = null;
                this.exception = null;
                this.sendPartiallyFailed = false;
            } catch (MessagingException mex) {
                if (this.debug) {
                    mex.printStackTrace(this.out);
                }
                notifyTransportListeners(2, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
                throw mex;
            } catch (IOException e2) {
                IOException ex = e2;
                if (this.debug) {
                    ex.printStackTrace(this.out);
                }
                try {
                    closeConnection();
                } catch (MessagingException e3) {
                }
                notifyTransportListeners(2, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
                throw new MessagingException("IOException while sending message", ex);
            } catch (Throwable th) {
                this.invalidAddr = null;
                this.validUnsentAddr = null;
                this.validSentAddr = null;
                this.addresses = null;
                this.message = null;
                this.exception = null;
                this.sendPartiallyFailed = false;
                throw th;
            }
        }
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:19:0x0038=Splitter:B:19:0x0038, B:24:0x003e=Splitter:B:24:0x003e} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void close() throws javax.mail.MessagingException {
        /*
            r4 = this;
            monitor-enter(r4)
            boolean r0 = super.isConnected()     // Catch:{ all -> 0x0042 }
            if (r0 != 0) goto L_0x0009
            monitor-exit(r4)
            return
        L_0x0009:
            java.net.Socket r0 = r4.serverSocket     // Catch:{ all -> 0x003d }
            if (r0 == 0) goto L_0x0038
            java.lang.String r0 = "QUIT"
            r4.sendCommand((java.lang.String) r0)     // Catch:{ all -> 0x0036 }
            boolean r0 = r4.quitWait     // Catch:{ all -> 0x0036 }
            if (r0 == 0) goto L_0x0038
            int r0 = r4.readServerResponse()     // Catch:{ all -> 0x0036 }
            r1 = 221(0xdd, float:3.1E-43)
            if (r0 == r1) goto L_0x0038
            r1 = -1
            if (r0 == r1) goto L_0x0038
            java.io.PrintStream r1 = r4.out     // Catch:{ all -> 0x0036 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0036 }
            java.lang.String r3 = "DEBUG SMTP: QUIT failed with "
            r2.<init>(r3)     // Catch:{ all -> 0x0036 }
            java.lang.StringBuilder r2 = r2.append(r0)     // Catch:{ all -> 0x0036 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0036 }
            r1.println(r2)     // Catch:{ all -> 0x0036 }
            goto L_0x0038
        L_0x0036:
            r0 = move-exception
            goto L_0x003e
        L_0x0038:
            r4.closeConnection()     // Catch:{ all -> 0x0042 }
            monitor-exit(r4)
            return
        L_0x003d:
            r0 = move-exception
        L_0x003e:
            r4.closeConnection()     // Catch:{ all -> 0x0042 }
            throw r0     // Catch:{ all -> 0x0042 }
        L_0x0042:
            r0 = move-exception
            monitor-exit(r4)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.smtp.SMTPTransport.close():void");
    }

    private void closeConnection() throws MessagingException {
        try {
            Socket socket = this.serverSocket;
            if (socket != null) {
                socket.close();
            }
            this.serverSocket = null;
            this.serverOutput = null;
            this.serverInput = null;
            this.lineInputStream = null;
            if (super.isConnected()) {
                super.close();
            }
        } catch (IOException ioex) {
            throw new MessagingException("Server Close Failed", ioex);
        } catch (Throwable th) {
            this.serverSocket = null;
            this.serverOutput = null;
            this.serverInput = null;
            this.lineInputStream = null;
            if (super.isConnected()) {
                super.close();
            }
            throw th;
        }
    }

    public synchronized boolean isConnected() {
        if (!super.isConnected()) {
            return false;
        }
        try {
            if (this.useRset) {
                sendCommand("RSET");
            } else {
                sendCommand("NOOP");
            }
            int resp = readServerResponse();
            if (resp >= 0 && resp != 421) {
                return true;
            }
            try {
                closeConnection();
            } catch (MessagingException e) {
            }
        } catch (Exception e2) {
            try {
                closeConnection();
            } catch (MessagingException e3) {
            }
            return false;
        }
        return false;
    }

    private void expandGroups() {
        Vector groups = null;
        int i = 0;
        while (true) {
            Address[] addressArr = this.addresses;
            if (i >= addressArr.length) {
                break;
            }
            InternetAddress a = (InternetAddress) addressArr[i];
            if (a.isGroup()) {
                if (groups == null) {
                    Vector groups2 = new Vector();
                    for (int k = 0; k < i; k++) {
                        groups2.addElement(this.addresses[k]);
                    }
                    groups = groups2;
                }
                try {
                    InternetAddress[] ia = a.getGroup(true);
                    if (ia != null) {
                        for (InternetAddress addElement : ia) {
                            groups.addElement(addElement);
                        }
                    } else {
                        groups.addElement(a);
                    }
                } catch (ParseException e) {
                    groups.addElement(a);
                }
            } else if (groups != null) {
                groups.addElement(a);
            }
            i++;
        }
        if (groups != null) {
            InternetAddress[] newa = new InternetAddress[groups.size()];
            groups.copyInto(newa);
            this.addresses = newa;
        }
    }

    private boolean convertTo8Bit(MimePart part) {
        boolean changed = false;
        try {
            if (part.isMimeType("text/*")) {
                String enc = part.getEncoding();
                if (enc == null) {
                    return false;
                }
                if ((!enc.equalsIgnoreCase("quoted-printable") && !enc.equalsIgnoreCase("base64")) || !is8Bit(part.getInputStream())) {
                    return false;
                }
                part.setContent(part.getContent(), part.getContentType());
                part.setHeader("Content-Transfer-Encoding", "8bit");
                return true;
            } else if (!part.isMimeType("multipart/*")) {
                return false;
            } else {
                MimeMultipart mp = (MimeMultipart) part.getContent();
                int count = mp.getCount();
                for (int i = 0; i < count; i++) {
                    if (convertTo8Bit((MimePart) mp.getBodyPart(i))) {
                        changed = true;
                    }
                }
                return changed;
            }
        } catch (IOException | MessagingException e) {
            return false;
        }
    }

    private boolean is8Bit(InputStream is) {
        int linelen = 0;
        boolean need8bit = false;
        while (true) {
            try {
                int read = is.read();
                int b = read;
                if (read < 0) {
                    if (this.debug && need8bit) {
                        this.out.println("DEBUG SMTP: found an 8bit part");
                    }
                    return need8bit;
                }
                int b2 = b & 255;
                if (b2 == 13 || b2 == 10) {
                    linelen = 0;
                } else if (b2 == 0 || (linelen = linelen + 1) > 998) {
                    return false;
                }
                if (b2 > 127) {
                    need8bit = true;
                }
            } catch (IOException e) {
                return false;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        super.finalize();
        try {
            closeConnection();
        } catch (MessagingException e) {
        }
    }

    /* access modifiers changed from: protected */
    public void helo(String domain) throws MessagingException {
        if (domain != null) {
            issueCommand("HELO " + domain, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
        } else {
            issueCommand("HELO", ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
        }
    }

    /* access modifiers changed from: protected */
    public boolean ehlo(String domain) throws MessagingException {
        String cmd;
        if (domain != null) {
            cmd = "EHLO " + domain;
        } else {
            cmd = "EHLO";
        }
        sendCommand(cmd);
        int resp = readServerResponse();
        if (resp == 250) {
            BufferedReader rd = new BufferedReader(new StringReader(this.lastServerResponse));
            this.extMap = new Hashtable();
            boolean first = true;
            while (true) {
                try {
                    String readLine = rd.readLine();
                    String line = readLine;
                    if (readLine == null) {
                        break;
                    } else if (first) {
                        first = false;
                    } else if (line.length() >= 5) {
                        String line2 = line.substring(4);
                        int i = line2.indexOf(32);
                        String arg = "";
                        if (i > 0) {
                            arg = line2.substring(i + 1);
                            line2 = line2.substring(0, i);
                        }
                        if (this.debug) {
                            this.out.println("DEBUG SMTP: Found extension \"" + line2 + "\", arg \"" + arg + "\"");
                        }
                        this.extMap.put(line2.toUpperCase(Locale.ENGLISH), arg);
                    }
                } catch (IOException e) {
                }
            }
        }
        if (resp == 250) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0054  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x017c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void mailFrom() throws javax.mail.MessagingException {
        /*
            r8 = this;
            r0 = 0
            javax.mail.internet.MimeMessage r1 = r8.message
            boolean r2 = r1 instanceof com.sun.mail.smtp.SMTPMessage
            if (r2 == 0) goto L_0x000d
            com.sun.mail.smtp.SMTPMessage r1 = (com.sun.mail.smtp.SMTPMessage) r1
            java.lang.String r0 = r1.getEnvelopeFrom()
        L_0x000d:
            java.lang.String r1 = "mail."
            if (r0 == 0) goto L_0x0017
            int r2 = r0.length()
            if (r2 > 0) goto L_0x0032
        L_0x0017:
            javax.mail.Session r2 = r8.session
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>(r1)
            java.lang.String r4 = r8.name
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r4 = ".from"
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.String r0 = r2.getProperty(r3)
        L_0x0032:
            if (r0 == 0) goto L_0x003a
            int r2 = r0.length()
            if (r2 > 0) goto L_0x005b
        L_0x003a:
            javax.mail.internet.MimeMessage r2 = r8.message
            if (r2 == 0) goto L_0x004c
            javax.mail.Address[] r2 = r2.getFrom()
            r3 = r2
            if (r2 == 0) goto L_0x004c
            int r2 = r3.length
            if (r2 <= 0) goto L_0x004c
            r2 = 0
            r2 = r3[r2]
            goto L_0x0052
        L_0x004c:
            javax.mail.Session r2 = r8.session
            javax.mail.internet.InternetAddress r2 = javax.mail.internet.InternetAddress.getLocalAddress(r2)
        L_0x0052:
            if (r2 == 0) goto L_0x017c
            r3 = r2
            javax.mail.internet.InternetAddress r3 = (javax.mail.internet.InternetAddress) r3
            java.lang.String r0 = r3.getAddress()
        L_0x005b:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            java.lang.String r3 = "MAIL FROM:"
            r2.<init>(r3)
            java.lang.String r3 = r8.normalizeAddress(r0)
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "DSN"
            boolean r3 = r8.supportsExtension(r3)
            if (r3 == 0) goto L_0x00b9
            r3 = 0
            javax.mail.internet.MimeMessage r4 = r8.message
            boolean r5 = r4 instanceof com.sun.mail.smtp.SMTPMessage
            if (r5 == 0) goto L_0x0083
            com.sun.mail.smtp.SMTPMessage r4 = (com.sun.mail.smtp.SMTPMessage) r4
            java.lang.String r3 = r4.getDSNRet()
        L_0x0083:
            if (r3 != 0) goto L_0x00a0
            javax.mail.Session r4 = r8.session
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>(r1)
            java.lang.String r6 = r8.name
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r6 = ".dsn.ret"
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r5 = r5.toString()
            java.lang.String r3 = r4.getProperty(r5)
        L_0x00a0:
            if (r3 == 0) goto L_0x00b9
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            java.lang.String r5 = java.lang.String.valueOf(r2)
            r4.<init>(r5)
            java.lang.String r5 = " RET="
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.StringBuilder r4 = r4.append(r3)
            java.lang.String r2 = r4.toString()
        L_0x00b9:
            java.lang.String r3 = "AUTH"
            boolean r3 = r8.supportsExtension(r3)
            if (r3 == 0) goto L_0x012d
            r3 = 0
            javax.mail.internet.MimeMessage r4 = r8.message
            boolean r5 = r4 instanceof com.sun.mail.smtp.SMTPMessage
            if (r5 == 0) goto L_0x00ce
            com.sun.mail.smtp.SMTPMessage r4 = (com.sun.mail.smtp.SMTPMessage) r4
            java.lang.String r3 = r4.getSubmitter()
        L_0x00ce:
            if (r3 != 0) goto L_0x00eb
            javax.mail.Session r4 = r8.session
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>(r1)
            java.lang.String r6 = r8.name
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r6 = ".submitter"
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r5 = r5.toString()
            java.lang.String r3 = r4.getProperty(r5)
        L_0x00eb:
            if (r3 == 0) goto L_0x012d
            java.lang.String r4 = xtext(r3)     // Catch:{ IllegalArgumentException -> 0x010a }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x010a }
            java.lang.String r6 = java.lang.String.valueOf(r2)     // Catch:{ IllegalArgumentException -> 0x010a }
            r5.<init>(r6)     // Catch:{ IllegalArgumentException -> 0x010a }
            java.lang.String r6 = " AUTH="
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ IllegalArgumentException -> 0x010a }
            java.lang.StringBuilder r5 = r5.append(r4)     // Catch:{ IllegalArgumentException -> 0x010a }
            java.lang.String r5 = r5.toString()     // Catch:{ IllegalArgumentException -> 0x010a }
            r2 = r5
            goto L_0x012d
        L_0x010a:
            r4 = move-exception
            boolean r5 = r8.debug
            if (r5 == 0) goto L_0x012d
            java.io.PrintStream r5 = r8.out
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            java.lang.String r7 = "DEBUG SMTP: ignoring invalid submitter: "
            r6.<init>(r7)
            java.lang.StringBuilder r6 = r6.append(r3)
            java.lang.String r7 = ", Exception: "
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.StringBuilder r6 = r6.append(r4)
            java.lang.String r6 = r6.toString()
            r5.println(r6)
        L_0x012d:
            r3 = 0
            javax.mail.internet.MimeMessage r4 = r8.message
            boolean r5 = r4 instanceof com.sun.mail.smtp.SMTPMessage
            if (r5 == 0) goto L_0x013a
            com.sun.mail.smtp.SMTPMessage r4 = (com.sun.mail.smtp.SMTPMessage) r4
            java.lang.String r3 = r4.getMailExtension()
        L_0x013a:
            if (r3 != 0) goto L_0x0157
            javax.mail.Session r4 = r8.session
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>(r1)
            java.lang.String r1 = r8.name
            java.lang.StringBuilder r1 = r5.append(r1)
            java.lang.String r5 = ".mailextension"
            java.lang.StringBuilder r1 = r1.append(r5)
            java.lang.String r1 = r1.toString()
            java.lang.String r3 = r4.getProperty(r1)
        L_0x0157:
            if (r3 == 0) goto L_0x0176
            int r1 = r3.length()
            if (r1 <= 0) goto L_0x0176
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            java.lang.String r4 = java.lang.String.valueOf(r2)
            r1.<init>(r4)
            java.lang.String r4 = " "
            java.lang.StringBuilder r1 = r1.append(r4)
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.String r2 = r1.toString()
        L_0x0176:
            r1 = 250(0xfa, float:3.5E-43)
            r8.issueSendCommand(r2, r1)
            return
        L_0x017c:
            javax.mail.MessagingException r1 = new javax.mail.MessagingException
            java.lang.String r3 = "can't determine local email address"
            r1.<init>(r3)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.smtp.SMTPTransport.mailFrom():void");
    }

    /* access modifiers changed from: protected */
    public void rcptTo() throws MessagingException {
        boolean sendPartial;
        String notify;
        String notify2;
        boolean sendFailed;
        String lsr;
        int retCode;
        String str;
        InternetAddress ia;
        String cmd;
        MessagingException sfex;
        Vector valid = new Vector();
        Vector validUnsent = new Vector();
        Vector invalid = new Vector();
        boolean sendFailed2 = false;
        this.invalidAddr = null;
        this.validUnsentAddr = null;
        this.validSentAddr = null;
        boolean sendPartial2 = false;
        MimeMessage mimeMessage = this.message;
        if (mimeMessage instanceof SMTPMessage) {
            sendPartial2 = ((SMTPMessage) mimeMessage).getSendPartial();
        }
        boolean z = true;
        if (!sendPartial2) {
            String sp = this.session.getProperty("mail." + this.name + ".sendpartial");
            sendPartial = sp != null && sp.equalsIgnoreCase("true");
        } else {
            sendPartial = sendPartial2;
        }
        if (this.debug && sendPartial) {
            this.out.println("DEBUG SMTP: sendPartial set");
        }
        String notify3 = null;
        if (supportsExtension("DSN")) {
            MimeMessage mimeMessage2 = this.message;
            if (mimeMessage2 instanceof SMTPMessage) {
                notify3 = ((SMTPMessage) mimeMessage2).getDSNNotify();
            }
            if (notify3 == null) {
                notify3 = this.session.getProperty("mail." + this.name + ".dsn.notify");
            }
            if (notify3 != null) {
                notify = notify3;
                notify2 = 1;
            } else {
                notify = notify3;
                notify2 = null;
            }
        } else {
            notify = null;
            notify2 = null;
        }
        int i = 0;
        int retCode2 = -1;
        MessagingException mex = null;
        while (true) {
            Address[] addressArr = this.addresses;
            if (i >= addressArr.length) {
                if (!sendPartial || valid.size() != 0) {
                    sendFailed = sendFailed2;
                } else {
                    sendFailed = true;
                }
                if (sendFailed) {
                    Address[] addressArr2 = new Address[invalid.size()];
                    this.invalidAddr = addressArr2;
                    invalid.copyInto(addressArr2);
                    this.validUnsentAddr = new Address[(valid.size() + validUnsent.size())];
                    int i2 = 0;
                    int j = 0;
                    while (j < valid.size()) {
                        this.validUnsentAddr[i2] = (Address) valid.elementAt(j);
                        j++;
                        i2++;
                    }
                    int j2 = 0;
                    while (j2 < validUnsent.size()) {
                        this.validUnsentAddr[i2] = (Address) validUnsent.elementAt(j2);
                        j2++;
                        i2++;
                    }
                } else if (this.reportSuccess || (sendPartial && (invalid.size() > 0 || validUnsent.size() > 0))) {
                    this.sendPartiallyFailed = z;
                    this.exception = mex;
                    Address[] addressArr3 = new Address[invalid.size()];
                    this.invalidAddr = addressArr3;
                    invalid.copyInto(addressArr3);
                    Address[] addressArr4 = new Address[validUnsent.size()];
                    this.validUnsentAddr = addressArr4;
                    validUnsent.copyInto(addressArr4);
                    Address[] addressArr5 = new Address[valid.size()];
                    this.validSentAddr = addressArr5;
                    valid.copyInto(addressArr5);
                } else {
                    this.validSentAddr = this.addresses;
                }
                if (this.debug) {
                    Address[] addressArr6 = this.validSentAddr;
                    if (addressArr6 != null && addressArr6.length > 0) {
                        this.out.println("DEBUG SMTP: Verified Addresses");
                        for (int l = 0; l < this.validSentAddr.length; l++) {
                            this.out.println("DEBUG SMTP:   " + this.validSentAddr[l]);
                        }
                    }
                    Address[] addressArr7 = this.validUnsentAddr;
                    if (addressArr7 != null && addressArr7.length > 0) {
                        this.out.println("DEBUG SMTP: Valid Unsent Addresses");
                        for (int j3 = 0; j3 < this.validUnsentAddr.length; j3++) {
                            this.out.println("DEBUG SMTP:   " + this.validUnsentAddr[j3]);
                        }
                    }
                    Address[] addressArr8 = this.invalidAddr;
                    if (addressArr8 != null && addressArr8.length > 0) {
                        this.out.println("DEBUG SMTP: Invalid Addresses");
                        for (int k = 0; k < this.invalidAddr.length; k++) {
                            this.out.println("DEBUG SMTP:   " + this.invalidAddr[k]);
                        }
                    }
                }
                if (sendFailed) {
                    if (this.debug) {
                        this.out.println("DEBUG SMTP: Sending failed because of invalid destination addresses");
                    }
                    String str2 = "RSET";
                    notifyTransportListeners(2, this.validSentAddr, this.validUnsentAddr, this.invalidAddr, this.message);
                    lsr = this.lastServerResponse;
                    int lrc = this.lastReturnCode;
                    try {
                        if (this.serverSocket != null) {
                            issueCommand(str2, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
                        }
                    } catch (MessagingException e) {
                        MessagingException messagingException = e;
                        try {
                            close();
                        } catch (MessagingException e2) {
                            MessagingException ex2 = e2;
                            if (this.debug) {
                                ex2.printStackTrace(this.out);
                            }
                        } catch (Throwable th) {
                            th = th;
                            int i3 = retCode2;
                            retCode = lrc;
                        }
                    }
                    this.lastServerResponse = lsr;
                    this.lastReturnCode = lrc;
                    int i4 = retCode2;
                    int retCode3 = lrc;
                    throw new SendFailedException("Invalid Addresses", mex, this.validSentAddr, this.validUnsentAddr, this.invalidAddr);
                }
                return;
            }
            str = "RSET";
            int i5 = retCode2;
            ia = (InternetAddress) addressArr[i];
            cmd = "RCPT TO:" + normalizeAddress(ia.getAddress());
            if (notify2 != null) {
                cmd = String.valueOf(cmd) + " NOTIFY=" + notify;
            }
            sendCommand(cmd);
            retCode2 = readServerResponse();
            switch (retCode2) {
                case ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION:
                case 251:
                    valid.addElement(ia);
                    if (this.reportSuccess) {
                        sfex = new SMTPAddressSucceededException(ia, cmd, retCode2, this.lastServerResponse);
                        if (mex != null) {
                            mex.setNextException(sfex);
                            break;
                        } else {
                            mex = sfex;
                            break;
                        }
                    } else {
                        sfex = null;
                        break;
                    }
                case 450:
                case 451:
                case 452:
                case 552:
                    if (!sendPartial) {
                        sendFailed2 = true;
                    }
                    validUnsent.addElement(ia);
                    sfex = new SMTPAddressFailedException(ia, cmd, retCode2, this.lastServerResponse);
                    if (mex != null) {
                        mex.setNextException(sfex);
                        break;
                    } else {
                        mex = sfex;
                        break;
                    }
                case TypedValues.PositionType.TYPE_TRANSITION_EASING:
                case TypedValues.PositionType.TYPE_PERCENT_WIDTH:
                case 550:
                case 551:
                case 553:
                    if (!sendPartial) {
                        sendFailed2 = true;
                    }
                    invalid.addElement(ia);
                    sfex = new SMTPAddressFailedException(ia, cmd, retCode2, this.lastServerResponse);
                    if (mex != null) {
                        mex.setNextException(sfex);
                        break;
                    } else {
                        mex = sfex;
                        break;
                    }
                default:
                    if (retCode2 >= 400 && retCode2 <= 499) {
                        validUnsent.addElement(ia);
                    } else if (retCode2 < 500 || retCode2 > 599) {
                        break;
                    } else {
                        invalid.addElement(ia);
                    }
                    if (!sendPartial) {
                        sendFailed2 = true;
                    }
                    sfex = new SMTPAddressFailedException(ia, cmd, retCode2, this.lastServerResponse);
                    if (mex != null) {
                        mex.setNextException(sfex);
                        break;
                    } else {
                        mex = sfex;
                        break;
                    }
                    break;
            }
            i++;
            MessagingException messagingException2 = sfex;
            z = true;
        }
        if (this.debug) {
            boolean z2 = sendFailed2;
            int i6 = i;
            this.out.println("DEBUG SMTP: got response code " + retCode2 + ", with response: " + this.lastServerResponse);
        } else {
            int i7 = i;
        }
        String _lsr = this.lastServerResponse;
        int _lrc = this.lastReturnCode;
        if (this.serverSocket != null) {
            issueCommand(str, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
        }
        this.lastServerResponse = _lsr;
        this.lastReturnCode = _lrc;
        throw new SMTPAddressFailedException(ia, cmd, retCode2, _lsr);
        this.lastServerResponse = lsr;
        this.lastReturnCode = retCode;
        throw th;
    }

    /* access modifiers changed from: protected */
    public OutputStream data() throws MessagingException {
        if (Thread.holdsLock(this)) {
            issueSendCommand("DATA", 354);
            SMTPOutputStream sMTPOutputStream = new SMTPOutputStream(this.serverOutput);
            this.dataStream = sMTPOutputStream;
            return sMTPOutputStream;
        }
        throw new AssertionError();
    }

    /* access modifiers changed from: protected */
    public void finishData() throws IOException, MessagingException {
        if (Thread.holdsLock(this)) {
            this.dataStream.ensureAtBOL();
            issueSendCommand(".", ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
            return;
        }
        throw new AssertionError();
    }

    /* access modifiers changed from: protected */
    public void startTLS() throws MessagingException {
        issueCommand("STARTTLS", 220);
        try {
            this.serverSocket = SocketFetcher.startTLS(this.serverSocket, this.session.getProperties(), "mail." + this.name);
            initStreams();
        } catch (IOException ioex) {
            closeConnection();
            throw new MessagingException("Could not convert socket to TLS", ioex);
        }
    }

    private void openServer(String server, int port) throws MessagingException {
        if (this.debug) {
            this.out.println("DEBUG SMTP: trying to connect to host \"" + server + "\", port " + port + ", isSSL " + this.isSSL);
        }
        try {
            Socket socket = SocketFetcher.getSocket(server, port, this.session.getProperties(), "mail." + this.name, this.isSSL);
            this.serverSocket = socket;
            port = socket.getPort();
            initStreams();
            int readServerResponse = readServerResponse();
            int r = readServerResponse;
            if (readServerResponse != 220) {
                this.serverSocket.close();
                this.serverSocket = null;
                this.serverOutput = null;
                this.serverInput = null;
                this.lineInputStream = null;
                if (this.debug) {
                    this.out.println("DEBUG SMTP: could not connect to host \"" + server + "\", port: " + port + ", response: " + r + "\n");
                }
                throw new MessagingException("Could not connect to SMTP host: " + server + ", port: " + port + ", response: " + r);
            } else if (this.debug) {
                this.out.println("DEBUG SMTP: connected to host \"" + server + "\", port: " + port + "\n");
            }
        } catch (UnknownHostException uhex) {
            throw new MessagingException("Unknown SMTP host: " + server, uhex);
        } catch (IOException ioe) {
            throw new MessagingException("Could not connect to SMTP host: " + server + ", port: " + port, ioe);
        }
    }

    private void openServer() throws MessagingException {
        int port = -1;
        String server = UNKNOWN;
        try {
            port = this.serverSocket.getPort();
            server = this.serverSocket.getInetAddress().getHostName();
            if (this.debug) {
                this.out.println("DEBUG SMTP: starting protocol to host \"" + server + "\", port " + port);
            }
            initStreams();
            int readServerResponse = readServerResponse();
            int r = readServerResponse;
            if (readServerResponse != 220) {
                this.serverSocket.close();
                this.serverSocket = null;
                this.serverOutput = null;
                this.serverInput = null;
                this.lineInputStream = null;
                if (this.debug) {
                    this.out.println("DEBUG SMTP: got bad greeting from host \"" + server + "\", port: " + port + ", response: " + r + "\n");
                }
                throw new MessagingException("Got bad greeting from SMTP host: " + server + ", port: " + port + ", response: " + r);
            } else if (this.debug) {
                this.out.println("DEBUG SMTP: protocol started to host \"" + server + "\", port: " + port + "\n");
            }
        } catch (IOException ioe) {
            throw new MessagingException("Could not start protocol to SMTP host: " + server + ", port: " + port, ioe);
        }
    }

    private void initStreams() throws IOException {
        Properties props = this.session.getProperties();
        PrintStream out2 = this.session.getDebugOut();
        boolean debug = this.session.getDebug();
        String s = props.getProperty("mail.debug.quote");
        boolean quote = s != null && s.equalsIgnoreCase("true");
        TraceInputStream traceInput = new TraceInputStream(this.serverSocket.getInputStream(), out2);
        traceInput.setTrace(debug);
        traceInput.setQuote(quote);
        TraceOutputStream traceOutput = new TraceOutputStream(this.serverSocket.getOutputStream(), out2);
        traceOutput.setTrace(debug);
        traceOutput.setQuote(quote);
        this.serverOutput = new BufferedOutputStream(traceOutput);
        this.serverInput = new BufferedInputStream(traceInput);
        this.lineInputStream = new LineInputStream(this.serverInput);
    }

    public synchronized void issueCommand(String cmd, int expect) throws MessagingException {
        sendCommand(cmd);
        if (readServerResponse() != expect) {
            throw new MessagingException(this.lastServerResponse);
        }
    }

    private void issueSendCommand(String cmd, int expect) throws MessagingException {
        sendCommand(cmd);
        int readServerResponse = readServerResponse();
        int ret = readServerResponse;
        if (readServerResponse != expect) {
            Address[] addressArr = this.validSentAddr;
            int vsl = addressArr == null ? 0 : addressArr.length;
            Address[] addressArr2 = this.validUnsentAddr;
            int vul = addressArr2 == null ? 0 : addressArr2.length;
            Address[] valid = new Address[(vsl + vul)];
            if (vsl > 0) {
                System.arraycopy(addressArr, 0, valid, 0, vsl);
            }
            if (vul > 0) {
                System.arraycopy(this.validUnsentAddr, 0, valid, vsl, vul);
            }
            this.validSentAddr = null;
            this.validUnsentAddr = valid;
            if (this.debug) {
                this.out.println("DEBUG SMTP: got response code " + ret + ", with response: " + this.lastServerResponse);
            }
            String _lsr = this.lastServerResponse;
            int _lrc = this.lastReturnCode;
            if (this.serverSocket != null) {
                issueCommand("RSET", ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
            }
            this.lastServerResponse = _lsr;
            this.lastReturnCode = _lrc;
            throw new SMTPSendFailedException(cmd, ret, this.lastServerResponse, this.exception, this.validSentAddr, this.validUnsentAddr, this.invalidAddr);
        }
    }

    public synchronized int simpleCommand(String cmd) throws MessagingException {
        sendCommand(cmd);
        return readServerResponse();
    }

    /* access modifiers changed from: protected */
    public int simpleCommand(byte[] cmd) throws MessagingException {
        if (Thread.holdsLock(this)) {
            sendCommand(cmd);
            return readServerResponse();
        }
        throw new AssertionError();
    }

    /* access modifiers changed from: protected */
    public void sendCommand(String cmd) throws MessagingException {
        sendCommand(ASCIIUtility.getBytes(cmd));
    }

    private void sendCommand(byte[] cmdBytes) throws MessagingException {
        if (Thread.holdsLock(this)) {
            try {
                this.serverOutput.write(cmdBytes);
                this.serverOutput.write(CRLF);
                this.serverOutput.flush();
            } catch (IOException ex) {
                throw new MessagingException("Can't send command to SMTP host", ex);
            }
        } else {
            throw new AssertionError();
        }
    }

    /* access modifiers changed from: protected */
    public int readServerResponse() throws MessagingException {
        String line;
        int returnCode;
        if (Thread.holdsLock(this)) {
            StringBuffer buf = new StringBuffer(100);
            do {
                try {
                    line = this.lineInputStream.readLine();
                    if (line == null) {
                        String serverResponse = buf.toString();
                        if (serverResponse.length() == 0) {
                            serverResponse = "[EOF]";
                        }
                        this.lastServerResponse = serverResponse;
                        this.lastReturnCode = -1;
                        if (this.debug) {
                            this.out.println("DEBUG SMTP: EOF: " + serverResponse);
                        }
                        return -1;
                    }
                    buf.append(line);
                    buf.append("\n");
                } catch (IOException ioex) {
                    if (this.debug) {
                        this.out.println("DEBUG SMTP: exception reading response: " + ioex);
                    }
                    this.lastServerResponse = "";
                    this.lastReturnCode = 0;
                    throw new MessagingException("Exception reading response", ioex);
                }
            } while (isNotLastLine(line));
            String serverResponse2 = buf.toString();
            if (serverResponse2 == null || serverResponse2.length() < 3) {
                returnCode = -1;
            } else {
                try {
                    returnCode = Integer.parseInt(serverResponse2.substring(0, 3));
                } catch (NumberFormatException e) {
                    try {
                        close();
                    } catch (MessagingException mex) {
                        if (this.debug) {
                            mex.printStackTrace(this.out);
                        }
                    }
                    returnCode = -1;
                } catch (StringIndexOutOfBoundsException e2) {
                    try {
                        close();
                    } catch (MessagingException mex2) {
                        if (this.debug) {
                            mex2.printStackTrace(this.out);
                        }
                    }
                    returnCode = -1;
                }
            }
            if (returnCode == -1 && this.debug) {
                this.out.println("DEBUG SMTP: bad server response: " + serverResponse2);
            }
            this.lastServerResponse = serverResponse2;
            this.lastReturnCode = returnCode;
            return returnCode;
        }
        throw new AssertionError();
    }

    /* access modifiers changed from: protected */
    public void checkConnected() {
        if (!super.isConnected()) {
            throw new IllegalStateException("Not connected");
        }
    }

    private boolean isNotLastLine(String line) {
        return line != null && line.length() >= 4 && line.charAt(3) == '-';
    }

    private String normalizeAddress(String addr) {
        if (addr.startsWith("<") || addr.endsWith(">")) {
            return addr;
        }
        return "<" + addr + ">";
    }

    public boolean supportsExtension(String ext) {
        Hashtable hashtable = this.extMap;
        return (hashtable == null || hashtable.get(ext.toUpperCase(Locale.ENGLISH)) == null) ? false : true;
    }

    public String getExtensionParameter(String ext) {
        Hashtable hashtable = this.extMap;
        if (hashtable == null) {
            return null;
        }
        return (String) hashtable.get(ext.toUpperCase(Locale.ENGLISH));
    }

    /* access modifiers changed from: protected */
    public boolean supportsAuthentication(String auth) {
        String a;
        if (Thread.holdsLock(this)) {
            Hashtable hashtable = this.extMap;
            if (hashtable == null || (a = (String) hashtable.get("AUTH")) == null) {
                return false;
            }
            StringTokenizer st = new StringTokenizer(a);
            while (st.hasMoreTokens()) {
                if (st.nextToken().equalsIgnoreCase(auth)) {
                    return true;
                }
            }
            return false;
        }
        throw new AssertionError();
    }

    protected static String xtext(String s) {
        StringBuffer sb = null;
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c < 128) {
                if (c < '!' || c > '~' || c == '+' || c == '=') {
                    if (sb == null) {
                        sb = new StringBuffer(s.length() + 4);
                        sb.append(s.substring(0, i));
                    }
                    sb.append('+');
                    sb.append(hexchar[(c & 240) >> 4]);
                    sb.append(hexchar[c & 15]);
                } else if (sb != null) {
                    sb.append(c);
                }
                i++;
            } else {
                throw new IllegalArgumentException("Non-ASCII character in SMTP submitter: " + s);
            }
        }
        return sb != null ? sb.toString() : s;
    }
}
