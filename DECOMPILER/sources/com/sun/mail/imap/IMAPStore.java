package com.sun.mail.imap;

import com.sun.mail.iap.BadCommandException;
import com.sun.mail.iap.CommandFailedException;
import com.sun.mail.iap.ConnectionException;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.iap.ResponseHandler;
import com.sun.mail.imap.protocol.IMAPProtocol;
import com.sun.mail.imap.protocol.Namespaces;
import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Quota;
import javax.mail.QuotaAwareStore;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.StoreClosedException;
import javax.mail.URLName;
import kotlin.jvm.internal.CharCompanionObject;

public class IMAPStore extends Store implements QuotaAwareStore, ResponseHandler {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final int RESPONSE = 1000;
    private int appendBufferSize;
    private String authorizationID;
    private int blksize;
    private volatile boolean connected;
    private int defaultPort;
    private boolean disableAuthLogin;
    private boolean disableAuthPlain;
    private boolean enableImapEvents;
    private boolean enableSASL;
    private boolean enableStartTLS;
    private boolean forcePasswordRefresh;
    private String host;
    private boolean isSSL;
    private int minIdleTime;
    private String name;
    private Namespaces namespaces;
    private PrintStream out;
    private String password;
    private ConnectionPool pool;
    private int port;
    private String proxyAuthUser;
    private String[] saslMechanisms;
    private String saslRealm;
    private int statusCacheTimeout;
    private String user;

    static class ConnectionPool {
        private static final int ABORTING = 2;
        private static final int IDLE = 1;
        private static final int RUNNING = 0;
        /* access modifiers changed from: private */
        public Vector authenticatedConnections = new Vector();
        /* access modifiers changed from: private */
        public long clientTimeoutInterval = 45000;
        /* access modifiers changed from: private */
        public boolean debug = false;
        /* access modifiers changed from: private */
        public Vector folders;
        /* access modifiers changed from: private */
        public IMAPProtocol idleProtocol;
        /* access modifiers changed from: private */
        public int idleState = 0;
        /* access modifiers changed from: private */
        public long lastTimePruned;
        /* access modifiers changed from: private */
        public int poolSize = 1;
        /* access modifiers changed from: private */
        public long pruningInterval = 60000;
        /* access modifiers changed from: private */
        public boolean separateStoreConnection = false;
        /* access modifiers changed from: private */
        public long serverTimeoutInterval = 1800000;
        /* access modifiers changed from: private */
        public boolean storeConnectionInUse = false;

        ConnectionPool() {
        }
    }

    public IMAPStore(Session session, URLName url) {
        this(session, url, "imap", 143, false);
    }

    protected IMAPStore(Session session, URLName url, String name2, int defaultPort2, boolean isSSL2) {
        super(session, url);
        String s;
        this.name = "imap";
        this.defaultPort = 143;
        this.isSSL = false;
        this.port = -1;
        this.blksize = 16384;
        this.statusCacheTimeout = 1000;
        this.appendBufferSize = -1;
        this.minIdleTime = 10;
        this.disableAuthLogin = false;
        this.disableAuthPlain = false;
        this.enableStartTLS = false;
        this.enableSASL = false;
        this.forcePasswordRefresh = false;
        this.enableImapEvents = false;
        this.connected = false;
        this.pool = new ConnectionPool();
        name2 = url != null ? url.getProtocol() : name2;
        this.name = name2;
        this.defaultPort = defaultPort2;
        this.isSSL = isSSL2;
        this.pool.lastTimePruned = System.currentTimeMillis();
        this.debug = session.getDebug();
        PrintStream debugOut = session.getDebugOut();
        this.out = debugOut;
        if (debugOut == null) {
            this.out = System.out;
        }
        String s2 = session.getProperty("mail." + name2 + ".connectionpool.debug");
        if (s2 != null && s2.equalsIgnoreCase("true")) {
            this.pool.debug = true;
        }
        String s3 = session.getProperty("mail." + name2 + ".partialfetch");
        if (s3 == null || !s3.equalsIgnoreCase("false")) {
            String property = session.getProperty("mail." + name2 + ".fetchsize");
            String s4 = property;
            if (property != null) {
                this.blksize = Integer.parseInt(s4);
            }
            if (this.debug) {
                this.out.println("DEBUG: mail.imap.fetchsize: " + this.blksize);
            }
        } else {
            this.blksize = -1;
            if (this.debug) {
                this.out.println("DEBUG: mail.imap.partialfetch: false");
            }
        }
        String s5 = session.getProperty("mail." + name2 + ".statuscachetimeout");
        if (s5 != null) {
            this.statusCacheTimeout = Integer.parseInt(s5);
            if (this.debug) {
                this.out.println("DEBUG: mail.imap.statuscachetimeout: " + this.statusCacheTimeout);
            }
        }
        String s6 = session.getProperty("mail." + name2 + ".appendbuffersize");
        if (s6 != null) {
            this.appendBufferSize = Integer.parseInt(s6);
            if (this.debug) {
                this.out.println("DEBUG: mail.imap.appendbuffersize: " + this.appendBufferSize);
            }
        }
        String s7 = session.getProperty("mail." + name2 + ".minidletime");
        if (s7 != null) {
            this.minIdleTime = Integer.parseInt(s7);
            if (this.debug) {
                this.out.println("DEBUG: mail.imap.minidletime: " + this.minIdleTime);
            }
        }
        String s8 = session.getProperty("mail." + name2 + ".connectionpoolsize");
        if (s8 != null) {
            try {
                int size = Integer.parseInt(s8);
                if (size > 0) {
                    this.pool.poolSize = size;
                }
            } catch (NumberFormatException e) {
            }
            if (this.pool.debug) {
                this.out.println("DEBUG: mail.imap.connectionpoolsize: " + this.pool.poolSize);
            }
        }
        String s9 = session.getProperty("mail." + name2 + ".connectionpooltimeout");
        if (s9 != null) {
            try {
                int connectionPoolTimeout = Integer.parseInt(s9);
                if (connectionPoolTimeout > 0) {
                    this.pool.clientTimeoutInterval = (long) connectionPoolTimeout;
                }
            } catch (NumberFormatException e2) {
            }
            if (this.pool.debug) {
                this.out.println("DEBUG: mail.imap.connectionpooltimeout: " + this.pool.clientTimeoutInterval);
            }
        }
        String s10 = session.getProperty("mail." + name2 + ".servertimeout");
        if (s10 != null) {
            try {
                int serverTimeout = Integer.parseInt(s10);
                if (serverTimeout > 0) {
                    this.pool.serverTimeoutInterval = (long) serverTimeout;
                }
            } catch (NumberFormatException e3) {
            }
            if (this.pool.debug) {
                this.out.println("DEBUG: mail.imap.servertimeout: " + this.pool.serverTimeoutInterval);
            }
        }
        String s11 = session.getProperty("mail." + name2 + ".separatestoreconnection");
        if (s11 != null && s11.equalsIgnoreCase("true")) {
            if (this.pool.debug) {
                this.out.println("DEBUG: dedicate a store connection");
            }
            this.pool.separateStoreConnection = true;
        }
        String s12 = session.getProperty("mail." + name2 + ".proxyauth.user");
        if (s12 != null) {
            this.proxyAuthUser = s12;
            if (this.debug) {
                this.out.println("DEBUG: mail.imap.proxyauth.user: " + this.proxyAuthUser);
            }
        }
        String s13 = session.getProperty("mail." + name2 + ".auth.login.disable");
        if (s13 != null && s13.equalsIgnoreCase("true")) {
            if (this.debug) {
                this.out.println("DEBUG: disable AUTH=LOGIN");
            }
            this.disableAuthLogin = true;
        }
        String s14 = session.getProperty("mail." + name2 + ".auth.plain.disable");
        if (s14 != null && s14.equalsIgnoreCase("true")) {
            if (this.debug) {
                this.out.println("DEBUG: disable AUTH=PLAIN");
            }
            this.disableAuthPlain = true;
        }
        String s15 = session.getProperty("mail." + name2 + ".starttls.enable");
        if (s15 != null && s15.equalsIgnoreCase("true")) {
            if (this.debug) {
                this.out.println("DEBUG: enable STARTTLS");
            }
            this.enableStartTLS = true;
        }
        String s16 = session.getProperty("mail." + name2 + ".sasl.enable");
        if (s16 != null && s16.equalsIgnoreCase("true")) {
            if (this.debug) {
                this.out.println("DEBUG: enable SASL");
            }
            this.enableSASL = true;
        }
        if (this.enableSASL && (s = session.getProperty("mail." + name2 + ".sasl.mechanisms")) != null && s.length() > 0) {
            if (this.debug) {
                this.out.println("DEBUG: SASL mechanisms allowed: " + s);
            }
            Vector v = new Vector(5);
            StringTokenizer st = new StringTokenizer(s, " ,");
            while (st.hasMoreTokens()) {
                String m = st.nextToken();
                if (m.length() > 0) {
                    v.addElement(m);
                }
            }
            String[] strArr = new String[v.size()];
            this.saslMechanisms = strArr;
            v.copyInto(strArr);
        }
        String s17 = session.getProperty("mail." + name2 + ".sasl.authorizationid");
        if (s17 != null) {
            this.authorizationID = s17;
            if (this.debug) {
                this.out.println("DEBUG: mail.imap.sasl.authorizationid: " + this.authorizationID);
            }
        }
        String s18 = session.getProperty("mail." + name2 + ".sasl.realm");
        if (s18 != null) {
            this.saslRealm = s18;
            if (this.debug) {
                this.out.println("DEBUG: mail.imap.sasl.realm: " + this.saslRealm);
            }
        }
        String s19 = session.getProperty("mail." + name2 + ".forcepasswordrefresh");
        if (s19 != null && s19.equalsIgnoreCase("true")) {
            if (this.debug) {
                this.out.println("DEBUG: enable forcePasswordRefresh");
            }
            this.forcePasswordRefresh = true;
        }
        String s20 = session.getProperty("mail." + name2 + ".enableimapevents");
        if (s20 != null && s20.equalsIgnoreCase("true")) {
            if (this.debug) {
                this.out.println("DEBUG: enable IMAP events");
            }
            this.enableImapEvents = true;
        }
    }

    /* access modifiers changed from: protected */
    public synchronized boolean protocolConnect(String host2, int pport, String user2, String password2) throws MessagingException {
        boolean poolEmpty;
        IMAPProtocol protocol = null;
        if (host2 == null || password2 == null || user2 == null) {
            if (this.debug) {
                this.out.println("DEBUG: protocolConnect returning false, host=" + host2 + ", user=" + user2 + ", password=" + (password2 != null ? "<non-null>" : "<null>"));
            }
            return false;
        }
        if (pport != -1) {
            this.port = pport;
        } else {
            String portstring = this.session.getProperty("mail." + this.name + ".port");
            if (portstring != null) {
                this.port = Integer.parseInt(portstring);
            }
        }
        if (this.port == -1) {
            this.port = this.defaultPort;
        }
        try {
            synchronized (this.pool) {
                poolEmpty = this.pool.authenticatedConnections.isEmpty();
            }
            if (poolEmpty) {
                protocol = new IMAPProtocol(this.name, host2, this.port, this.session.getDebug(), this.session.getDebugOut(), this.session.getProperties(), this.isSSL);
                if (this.debug) {
                    this.out.println("DEBUG: protocolConnect login, host=" + host2 + ", user=" + user2 + ", password=<non-null>");
                }
                login(protocol, user2, password2);
                protocol.addResponseHandler(this);
                this.host = host2;
                this.user = user2;
                this.password = password2;
                synchronized (this.pool) {
                    this.pool.authenticatedConnections.addElement(protocol);
                }
            }
            this.connected = true;
            return true;
        } catch (CommandFailedException cex) {
            if (protocol != null) {
                protocol.disconnect();
            }
            throw new AuthenticationFailedException(cex.getResponse().getRest());
        } catch (ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        } catch (IOException ioex) {
            throw new MessagingException(ioex.getMessage(), ioex);
        }
    }

    private void login(IMAPProtocol p, String u, String pw) throws ProtocolException {
        String authzid;
        if (this.enableStartTLS && p.hasCapability("STARTTLS")) {
            p.startTLS();
            p.capability();
        }
        if (!p.isAuthenticated()) {
            p.getCapabilities().put("__PRELOGIN__", "");
            if (this.authorizationID != null) {
                authzid = this.authorizationID;
            } else if (this.proxyAuthUser != null) {
                authzid = this.proxyAuthUser;
            } else {
                authzid = u;
            }
            if (this.enableSASL) {
                p.sasllogin(this.saslMechanisms, this.saslRealm, authzid, u, pw);
            }
            if (!p.isAuthenticated()) {
                if (p.hasCapability("AUTH=PLAIN") && !this.disableAuthPlain) {
                    p.authplain(authzid, u, pw);
                } else if ((p.hasCapability("AUTH-LOGIN") || p.hasCapability("AUTH=LOGIN")) && !this.disableAuthLogin) {
                    p.authlogin(u, pw);
                } else if (!p.hasCapability("LOGINDISABLED")) {
                    p.login(u, pw);
                } else {
                    throw new ProtocolException("No login methods supported!");
                }
            }
            String str = this.proxyAuthUser;
            if (str != null) {
                p.proxyauth(str);
            }
            if (p.hasCapability("__PRELOGIN__")) {
                try {
                    p.capability();
                } catch (ConnectionException cex) {
                    throw cex;
                } catch (ProtocolException e) {
                }
            }
        }
    }

    public synchronized void setUsername(String user2) {
        this.user = user2;
    }

    public synchronized void setPassword(String password2) {
        this.password = password2;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0102 A[Catch:{ ProtocolException -> 0x0081, all -> 0x0089 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.sun.mail.imap.protocol.IMAPProtocol getProtocol(com.sun.mail.imap.IMAPFolder r12) throws javax.mail.MessagingException {
        /*
            r11 = this;
            r0 = 0
        L_0x0002:
            if (r0 == 0) goto L_0x0005
            return r0
        L_0x0005:
            com.sun.mail.imap.IMAPStore$ConnectionPool r1 = r11.pool
            monitor-enter(r1)
            com.sun.mail.imap.IMAPStore$ConnectionPool r2 = r11.pool     // Catch:{ all -> 0x0128 }
            java.util.Vector r2 = r2.authenticatedConnections     // Catch:{ all -> 0x0128 }
            boolean r2 = r2.isEmpty()     // Catch:{ all -> 0x0128 }
            if (r2 != 0) goto L_0x0093
            com.sun.mail.imap.IMAPStore$ConnectionPool r2 = r11.pool     // Catch:{ all -> 0x0128 }
            java.util.Vector r2 = r2.authenticatedConnections     // Catch:{ all -> 0x0128 }
            int r2 = r2.size()     // Catch:{ all -> 0x0128 }
            r3 = 1
            if (r2 != r3) goto L_0x0032
            com.sun.mail.imap.IMAPStore$ConnectionPool r2 = r11.pool     // Catch:{ all -> 0x0128 }
            boolean r2 = r2.separateStoreConnection     // Catch:{ all -> 0x0128 }
            if (r2 != 0) goto L_0x0093
            com.sun.mail.imap.IMAPStore$ConnectionPool r2 = r11.pool     // Catch:{ all -> 0x0128 }
            boolean r2 = r2.storeConnectionInUse     // Catch:{ all -> 0x0128 }
            if (r2 == 0) goto L_0x0032
            goto L_0x0093
        L_0x0032:
            boolean r2 = r11.debug     // Catch:{ all -> 0x0128 }
            if (r2 == 0) goto L_0x0054
            java.io.PrintStream r2 = r11.out     // Catch:{ all -> 0x0128 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0128 }
            java.lang.String r4 = "DEBUG: connection available -- size: "
            r3.<init>(r4)     // Catch:{ all -> 0x0128 }
            com.sun.mail.imap.IMAPStore$ConnectionPool r4 = r11.pool     // Catch:{ all -> 0x0128 }
            java.util.Vector r4 = r4.authenticatedConnections     // Catch:{ all -> 0x0128 }
            int r4 = r4.size()     // Catch:{ all -> 0x0128 }
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ all -> 0x0128 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0128 }
            r2.println(r3)     // Catch:{ all -> 0x0128 }
        L_0x0054:
            com.sun.mail.imap.IMAPStore$ConnectionPool r2 = r11.pool     // Catch:{ all -> 0x0128 }
            java.util.Vector r2 = r2.authenticatedConnections     // Catch:{ all -> 0x0128 }
            java.lang.Object r2 = r2.lastElement()     // Catch:{ all -> 0x0128 }
            com.sun.mail.imap.protocol.IMAPProtocol r2 = (com.sun.mail.imap.protocol.IMAPProtocol) r2     // Catch:{ all -> 0x0128 }
            r0 = r2
            com.sun.mail.imap.IMAPStore$ConnectionPool r2 = r11.pool     // Catch:{ all -> 0x0128 }
            java.util.Vector r2 = r2.authenticatedConnections     // Catch:{ all -> 0x0128 }
            r2.removeElement(r0)     // Catch:{ all -> 0x0128 }
            long r2 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0128 }
            long r4 = r0.getTimestamp()     // Catch:{ all -> 0x0128 }
            long r2 = r2 - r4
            com.sun.mail.imap.IMAPStore$ConnectionPool r4 = r11.pool     // Catch:{ all -> 0x0128 }
            long r4 = r4.serverTimeoutInterval     // Catch:{ all -> 0x0128 }
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 <= 0) goto L_0x008e
            r0.noop()     // Catch:{ ProtocolException -> 0x0081 }
            goto L_0x008e
        L_0x0081:
            r4 = move-exception
            r0.removeResponseHandler(r11)     // Catch:{ all -> 0x0089 }
            r0.disconnect()     // Catch:{ all -> 0x0089 }
            goto L_0x008a
        L_0x0089:
            r5 = move-exception
        L_0x008a:
            r0 = 0
            monitor-exit(r1)     // Catch:{ all -> 0x0128 }
            goto L_0x0002
        L_0x008e:
            r0.removeResponseHandler(r11)     // Catch:{ all -> 0x0128 }
            goto L_0x00fd
        L_0x0093:
            boolean r2 = r11.debug     // Catch:{ all -> 0x0128 }
            if (r2 == 0) goto L_0x009e
            java.io.PrintStream r2 = r11.out     // Catch:{ all -> 0x0128 }
            java.lang.String r3 = "DEBUG: no connections in the pool, creating a new one"
            r2.println(r3)     // Catch:{ all -> 0x0128 }
        L_0x009e:
            boolean r2 = r11.forcePasswordRefresh     // Catch:{ Exception -> 0x00f2 }
            if (r2 == 0) goto L_0x00c9
            java.lang.String r2 = r11.host     // Catch:{ UnknownHostException -> 0x00a9 }
            java.net.InetAddress r2 = java.net.InetAddress.getByName(r2)     // Catch:{ UnknownHostException -> 0x00a9 }
            goto L_0x00ac
        L_0x00a9:
            r2 = move-exception
            r3 = 0
            r2 = r3
        L_0x00ac:
            javax.mail.Session r3 = r11.session     // Catch:{ Exception -> 0x00f2 }
            int r5 = r11.port     // Catch:{ Exception -> 0x00f2 }
            java.lang.String r6 = r11.name     // Catch:{ Exception -> 0x00f2 }
            r7 = 0
            java.lang.String r8 = r11.user     // Catch:{ Exception -> 0x00f2 }
            r4 = r2
            javax.mail.PasswordAuthentication r3 = r3.requestPasswordAuthentication(r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x00f2 }
            if (r3 == 0) goto L_0x00c9
            java.lang.String r4 = r3.getUserName()     // Catch:{ Exception -> 0x00f2 }
            r11.user = r4     // Catch:{ Exception -> 0x00f2 }
            java.lang.String r4 = r3.getPassword()     // Catch:{ Exception -> 0x00f2 }
            r11.password = r4     // Catch:{ Exception -> 0x00f2 }
        L_0x00c9:
            com.sun.mail.imap.protocol.IMAPProtocol r2 = new com.sun.mail.imap.protocol.IMAPProtocol     // Catch:{ Exception -> 0x00f2 }
            java.lang.String r4 = r11.name     // Catch:{ Exception -> 0x00f2 }
            java.lang.String r5 = r11.host     // Catch:{ Exception -> 0x00f2 }
            int r6 = r11.port     // Catch:{ Exception -> 0x00f2 }
            javax.mail.Session r3 = r11.session     // Catch:{ Exception -> 0x00f2 }
            boolean r7 = r3.getDebug()     // Catch:{ Exception -> 0x00f2 }
            javax.mail.Session r3 = r11.session     // Catch:{ Exception -> 0x00f2 }
            java.io.PrintStream r8 = r3.getDebugOut()     // Catch:{ Exception -> 0x00f2 }
            javax.mail.Session r3 = r11.session     // Catch:{ Exception -> 0x00f2 }
            java.util.Properties r9 = r3.getProperties()     // Catch:{ Exception -> 0x00f2 }
            boolean r10 = r11.isSSL     // Catch:{ Exception -> 0x00f2 }
            r3 = r2
            r3.<init>(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x00f2 }
            r0 = r2
            java.lang.String r2 = r11.user     // Catch:{ Exception -> 0x00f2 }
            java.lang.String r3 = r11.password     // Catch:{ Exception -> 0x00f2 }
            r11.login(r0, r2, r3)     // Catch:{ Exception -> 0x00f2 }
            goto L_0x00fb
        L_0x00f2:
            r2 = move-exception
            if (r0 == 0) goto L_0x00fa
            r0.disconnect()     // Catch:{ Exception -> 0x00f9 }
            goto L_0x00fa
        L_0x00f9:
            r3 = move-exception
        L_0x00fa:
            r0 = 0
        L_0x00fb:
            if (r0 == 0) goto L_0x0120
        L_0x00fd:
            r11.timeoutConnections()     // Catch:{ all -> 0x0128 }
            if (r12 == 0) goto L_0x011d
            com.sun.mail.imap.IMAPStore$ConnectionPool r2 = r11.pool     // Catch:{ all -> 0x0128 }
            java.util.Vector r2 = r2.folders     // Catch:{ all -> 0x0128 }
            if (r2 != 0) goto L_0x0114
            com.sun.mail.imap.IMAPStore$ConnectionPool r2 = r11.pool     // Catch:{ all -> 0x0128 }
            java.util.Vector r3 = new java.util.Vector     // Catch:{ all -> 0x0128 }
            r3.<init>()     // Catch:{ all -> 0x0128 }
            r2.folders = r3     // Catch:{ all -> 0x0128 }
        L_0x0114:
            com.sun.mail.imap.IMAPStore$ConnectionPool r2 = r11.pool     // Catch:{ all -> 0x0128 }
            java.util.Vector r2 = r2.folders     // Catch:{ all -> 0x0128 }
            r2.addElement(r12)     // Catch:{ all -> 0x0128 }
        L_0x011d:
            monitor-exit(r1)     // Catch:{ all -> 0x0128 }
            goto L_0x0002
        L_0x0120:
            javax.mail.MessagingException r2 = new javax.mail.MessagingException     // Catch:{ all -> 0x0128 }
            java.lang.String r3 = "connection failure"
            r2.<init>(r3)     // Catch:{ all -> 0x0128 }
            throw r2     // Catch:{ all -> 0x0128 }
        L_0x0128:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0128 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPStore.getProtocol(com.sun.mail.imap.IMAPFolder):com.sun.mail.imap.protocol.IMAPProtocol");
    }

    /* access modifiers changed from: package-private */
    public IMAPProtocol getStoreProtocol() throws ProtocolException {
        IMAPProtocol p = null;
        while (p == null) {
            synchronized (this.pool) {
                waitIfIdle();
                if (this.pool.authenticatedConnections.isEmpty()) {
                    if (this.pool.debug) {
                        this.out.println("DEBUG: getStoreProtocol() - no connections in the pool, creating a new one");
                    }
                    try {
                        p = new IMAPProtocol(this.name, this.host, this.port, this.session.getDebug(), this.session.getDebugOut(), this.session.getProperties(), this.isSSL);
                        login(p, this.user, this.password);
                    } catch (Exception e) {
                        if (p != null) {
                            try {
                                p.logout();
                            } catch (Exception e2) {
                            }
                        }
                        p = null;
                    }
                    if (p != null) {
                        p.addResponseHandler(this);
                        this.pool.authenticatedConnections.addElement(p);
                    } else {
                        throw new ConnectionException("failed to create new store connection");
                    }
                } else {
                    if (this.pool.debug) {
                        this.out.println("DEBUG: getStoreProtocol() - connection available -- size: " + this.pool.authenticatedConnections.size());
                    }
                    p = (IMAPProtocol) this.pool.authenticatedConnections.firstElement();
                }
                if (this.pool.storeConnectionInUse) {
                    p = null;
                    try {
                        this.pool.wait();
                    } catch (InterruptedException e3) {
                    }
                } else {
                    this.pool.storeConnectionInUse = true;
                    if (this.pool.debug) {
                        this.out.println("DEBUG: getStoreProtocol() -- storeConnectionInUse");
                    }
                }
                timeoutConnections();
            }
        }
        return p;
    }

    /* access modifiers changed from: package-private */
    public boolean allowReadOnlySelect() {
        String s = this.session.getProperty("mail." + this.name + ".allowreadonlyselect");
        return s != null && s.equalsIgnoreCase("true");
    }

    /* access modifiers changed from: package-private */
    public boolean hasSeparateStoreConnection() {
        return this.pool.separateStoreConnection;
    }

    /* access modifiers changed from: package-private */
    public boolean getConnectionPoolDebug() {
        return this.pool.debug;
    }

    /* access modifiers changed from: package-private */
    public boolean isConnectionPoolFull() {
        boolean z;
        synchronized (this.pool) {
            if (this.pool.debug) {
                this.out.println("DEBUG: current size: " + this.pool.authenticatedConnections.size() + "   pool size: " + this.pool.poolSize);
            }
            z = this.pool.authenticatedConnections.size() >= this.pool.poolSize;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    public void releaseProtocol(IMAPFolder folder, IMAPProtocol protocol) {
        synchronized (this.pool) {
            if (protocol != null) {
                if (!isConnectionPoolFull()) {
                    protocol.addResponseHandler(this);
                    this.pool.authenticatedConnections.addElement(protocol);
                    if (this.debug) {
                        this.out.println("DEBUG: added an Authenticated connection -- size: " + this.pool.authenticatedConnections.size());
                    }
                } else {
                    if (this.debug) {
                        this.out.println("DEBUG: pool is full, not adding an Authenticated connection");
                    }
                    try {
                        protocol.logout();
                    } catch (ProtocolException e) {
                    }
                }
            }
            if (this.pool.folders != null) {
                this.pool.folders.removeElement(folder);
            }
            timeoutConnections();
        }
    }

    /* access modifiers changed from: package-private */
    public void releaseStoreProtocol(IMAPProtocol protocol) {
        if (protocol != null) {
            synchronized (this.pool) {
                this.pool.storeConnectionInUse = false;
                this.pool.notifyAll();
                if (this.pool.debug) {
                    this.out.println("DEBUG: releaseStoreProtocol()");
                }
                timeoutConnections();
            }
        }
    }

    private void emptyConnectionPool(boolean force) {
        synchronized (this.pool) {
            for (int index = this.pool.authenticatedConnections.size() - 1; index >= 0; index--) {
                try {
                    IMAPProtocol p = (IMAPProtocol) this.pool.authenticatedConnections.elementAt(index);
                    p.removeResponseHandler(this);
                    if (force) {
                        p.disconnect();
                    } else {
                        p.logout();
                    }
                } catch (ProtocolException e) {
                }
            }
            this.pool.authenticatedConnections.removeAllElements();
        }
        if (this.pool.debug) {
            this.out.println("DEBUG: removed all authenticated connections");
        }
    }

    private void timeoutConnections() {
        synchronized (this.pool) {
            if (System.currentTimeMillis() - this.pool.lastTimePruned > this.pool.pruningInterval && this.pool.authenticatedConnections.size() > 1) {
                if (this.pool.debug) {
                    this.out.println("DEBUG: checking for connections to prune: " + (System.currentTimeMillis() - this.pool.lastTimePruned));
                    this.out.println("DEBUG: clientTimeoutInterval: " + this.pool.clientTimeoutInterval);
                }
                for (int index = this.pool.authenticatedConnections.size() - 1; index > 0; index--) {
                    IMAPProtocol p = (IMAPProtocol) this.pool.authenticatedConnections.elementAt(index);
                    if (this.pool.debug) {
                        this.out.println("DEBUG: protocol last used: " + (System.currentTimeMillis() - p.getTimestamp()));
                    }
                    if (System.currentTimeMillis() - p.getTimestamp() > this.pool.clientTimeoutInterval) {
                        if (this.pool.debug) {
                            this.out.println("DEBUG: authenticated connection timed out");
                            this.out.println("DEBUG: logging out the connection");
                        }
                        p.removeResponseHandler(this);
                        this.pool.authenticatedConnections.removeElementAt(index);
                        try {
                            p.logout();
                        } catch (ProtocolException e) {
                        }
                    }
                }
                this.pool.lastTimePruned = System.currentTimeMillis();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int getFetchBlockSize() {
        return this.blksize;
    }

    /* access modifiers changed from: package-private */
    public Session getSession() {
        return this.session;
    }

    /* access modifiers changed from: package-private */
    public int getStatusCacheTimeout() {
        return this.statusCacheTimeout;
    }

    /* access modifiers changed from: package-private */
    public int getAppendBufferSize() {
        return this.appendBufferSize;
    }

    /* access modifiers changed from: package-private */
    public int getMinIdleTime() {
        return this.minIdleTime;
    }

    public synchronized boolean hasCapability(String capability) throws MessagingException {
        boolean hasCapability;
        try {
            IMAPProtocol p = getStoreProtocol();
            hasCapability = p.hasCapability(capability);
            releaseStoreProtocol(p);
        } catch (ProtocolException pex) {
            if (0 == 0) {
                cleanup();
            }
            throw new MessagingException(pex.getMessage(), pex);
        } catch (Throwable th) {
            releaseStoreProtocol((IMAPProtocol) null);
            throw th;
        }
        return hasCapability;
    }

    public synchronized boolean isConnected() {
        if (!this.connected) {
            super.setConnected(false);
            return false;
        }
        IMAPProtocol p = null;
        try {
            p = getStoreProtocol();
            p.noop();
        } catch (ProtocolException e) {
            if (p == null) {
                try {
                    cleanup();
                } catch (Throwable th) {
                    releaseStoreProtocol(p);
                    throw th;
                }
            }
        }
        releaseStoreProtocol(p);
        return super.isConnected();
    }

    public synchronized void close() throws MessagingException {
        IMAPProtocol protocol;
        boolean isEmpty;
        if (super.isConnected()) {
            protocol = null;
            try {
                synchronized (this.pool) {
                    isEmpty = this.pool.authenticatedConnections.isEmpty();
                }
                if (isEmpty) {
                    if (this.pool.debug) {
                        try {
                            this.out.println("DEBUG: close() - no connections ");
                        } catch (ProtocolException e) {
                            pex = e;
                        } catch (Throwable th) {
                            pex = th;
                            releaseStoreProtocol(protocol);
                            throw pex;
                        }
                    }
                    cleanup();
                    releaseStoreProtocol((IMAPProtocol) null);
                    return;
                }
                protocol = getStoreProtocol();
                synchronized (this.pool) {
                    this.pool.authenticatedConnections.removeElement(protocol);
                }
                protocol.logout();
                releaseStoreProtocol(protocol);
                return;
            } catch (ProtocolException e2) {
                pex = e2;
            }
        } else {
            return;
        }
        try {
            cleanup();
            throw new MessagingException(pex.getMessage(), pex);
        } catch (Throwable th2) {
            pex = th2;
            releaseStoreProtocol(protocol);
            throw pex;
        }
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        super.finalize();
        close();
    }

    private void cleanup() {
        cleanup(false);
    }

    private void cleanup(boolean force) {
        boolean done;
        if (this.debug) {
            this.out.println("DEBUG: IMAPStore cleanup, force " + force);
        }
        Vector foldersCopy = null;
        while (true) {
            synchronized (this.pool) {
                if (this.pool.folders != null) {
                    done = false;
                    foldersCopy = this.pool.folders;
                    this.pool.folders = null;
                } else {
                    done = true;
                }
            }
            if (done) {
                synchronized (this.pool) {
                    emptyConnectionPool(force);
                }
                this.connected = false;
                notifyConnectionListeners(3);
                if (this.debug) {
                    this.out.println("DEBUG: IMAPStore cleanup done");
                    return;
                }
                return;
            }
            int fsize = foldersCopy.size();
            for (int i = 0; i < fsize; i++) {
                IMAPFolder f = (IMAPFolder) foldersCopy.elementAt(i);
                if (force) {
                    try {
                        if (this.debug) {
                            this.out.println("DEBUG: force folder to close");
                        }
                        f.forceClose();
                    } catch (IllegalStateException | MessagingException e) {
                    }
                } else {
                    if (this.debug) {
                        this.out.println("DEBUG: close folder");
                    }
                    f.close(false);
                }
            }
        }
        while (true) {
        }
    }

    public synchronized Folder getDefaultFolder() throws MessagingException {
        checkConnected();
        return new DefaultFolder(this);
    }

    public synchronized Folder getFolder(String name2) throws MessagingException {
        checkConnected();
        return new IMAPFolder(name2, CharCompanionObject.MAX_VALUE, this);
    }

    public synchronized Folder getFolder(URLName url) throws MessagingException {
        checkConnected();
        return new IMAPFolder(url.getFile(), CharCompanionObject.MAX_VALUE, this);
    }

    public Folder[] getPersonalNamespaces() throws MessagingException {
        Namespaces ns = getNamespaces();
        if (ns == null || ns.personal == null) {
            return super.getPersonalNamespaces();
        }
        return namespaceToFolders(ns.personal, (String) null);
    }

    public Folder[] getUserNamespaces(String user2) throws MessagingException {
        Namespaces ns = getNamespaces();
        if (ns == null || ns.otherUsers == null) {
            return super.getUserNamespaces(user2);
        }
        return namespaceToFolders(ns.otherUsers, user2);
    }

    public Folder[] getSharedNamespaces() throws MessagingException {
        Namespaces ns = getNamespaces();
        if (ns == null || ns.shared == null) {
            return super.getSharedNamespaces();
        }
        return namespaceToFolders(ns.shared, (String) null);
    }

    private synchronized Namespaces getNamespaces() throws MessagingException {
        checkConnected();
        IMAPProtocol p = null;
        if (this.namespaces == null) {
            try {
                p = getStoreProtocol();
                this.namespaces = p.namespace();
                releaseStoreProtocol(p);
                if (p == null) {
                    cleanup();
                }
            } catch (BadCommandException e) {
                releaseStoreProtocol(p);
                if (p == null) {
                    cleanup();
                }
            } catch (ConnectionException cex) {
                throw new StoreClosedException(this, cex.getMessage());
            } catch (ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            } catch (Throwable th) {
                releaseStoreProtocol(p);
                if (p == null) {
                    cleanup();
                }
                throw th;
            }
        }
        return this.namespaces;
    }

    private Folder[] namespaceToFolders(Namespaces.Namespace[] ns, String user2) {
        Folder[] fa = new Folder[ns.length];
        for (int i = 0; i < fa.length; i++) {
            String name2 = ns[i].prefix;
            boolean z = false;
            if (user2 == null) {
                int len = name2.length();
                if (len > 0 && name2.charAt(len - 1) == ns[i].delimiter) {
                    name2 = name2.substring(0, len - 1);
                }
            } else {
                name2 = String.valueOf(name2) + user2;
            }
            char c = ns[i].delimiter;
            if (user2 == null) {
                z = true;
            }
            fa[i] = new IMAPFolder(name2, c, this, z);
        }
        return fa;
    }

    public synchronized Quota[] getQuota(String root) throws MessagingException {
        Quota[] qa;
        checkConnected();
        Quota[] quotaArr = null;
        try {
            IMAPProtocol p = getStoreProtocol();
            qa = p.getQuotaRoot(root);
            releaseStoreProtocol(p);
            if (p == null) {
                cleanup();
            }
        } catch (BadCommandException bex) {
            throw new MessagingException("QUOTA not supported", bex);
        } catch (ConnectionException cex) {
            throw new StoreClosedException(this, cex.getMessage());
        } catch (ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        } catch (Throwable th) {
            releaseStoreProtocol((IMAPProtocol) null);
            if (0 == 0) {
                cleanup();
            }
            throw th;
        }
        return qa;
    }

    public synchronized void setQuota(Quota quota) throws MessagingException {
        checkConnected();
        IMAPProtocol p = null;
        try {
            p = getStoreProtocol();
            p.setQuota(quota);
            releaseStoreProtocol(p);
            if (p == null) {
                cleanup();
            }
        } catch (BadCommandException bex) {
            throw new MessagingException("QUOTA not supported", bex);
        } catch (ConnectionException cex) {
            throw new StoreClosedException(this, cex.getMessage());
        } catch (ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        } catch (Throwable th) {
            releaseStoreProtocol(p);
            if (p == null) {
                cleanup();
            }
            throw th;
        }
    }

    private void checkConnected() {
        if (!Thread.holdsLock(this)) {
            throw new AssertionError();
        } else if (!this.connected) {
            super.setConnected(false);
            throw new IllegalStateException("Not connected");
        }
    }

    public void handleResponse(Response r) {
        if (r.isOK() || r.isNO() || r.isBAD() || r.isBYE()) {
            handleResponseCode(r);
        }
        if (r.isBYE()) {
            if (this.debug) {
                this.out.println("DEBUG: IMAPStore connection dead");
            }
            if (this.connected) {
                cleanup(r.isSynthetic());
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:135:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:136:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r2 = r0.readIdleResponse();
        r3 = r7.pool;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0034, code lost:
        monitor-enter(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0035, code lost:
        if (r2 == null) goto L_0x0053;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x003b, code lost:
        if (r0.processIdleResponse(r2) != false) goto L_0x003e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x003e, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0041, code lost:
        if (r7.enableImapEvents == false) goto L_0x002e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0047, code lost:
        if (r2.isUnTagged() == false) goto L_0x002e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0049, code lost:
        notifyStoreListeners(1000, r2.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
        com.sun.mail.imap.IMAPStore.ConnectionPool.access$20(r7.pool, 0);
        r7.pool.notifyAll();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x005e, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        r2 = getMinIdleTime();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0063, code lost:
        if (r2 <= 0) goto L_0x006b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:?, code lost:
        java.lang.Thread.sleep((long) r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x008b, code lost:
        r3 = r7.pool;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x008d, code lost:
        monitor-enter(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:?, code lost:
        com.sun.mail.imap.IMAPStore.ConnectionPool.access$18(r7.pool, (com.sun.mail.imap.protocol.IMAPProtocol) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0093, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0094, code lost:
        releaseStoreProtocol(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0097, code lost:
        if (r0 != null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0099, code lost:
        cleanup();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void idle() throws javax.mail.MessagingException {
        /*
            r7 = this;
            r0 = 0
            com.sun.mail.imap.IMAPStore$ConnectionPool r1 = r7.pool
            boolean r1 = java.lang.Thread.holdsLock(r1)
            if (r1 != 0) goto L_0x00df
            monitor-enter(r7)
            r7.checkConnected()     // Catch:{ all -> 0x00dc }
            monitor-exit(r7)     // Catch:{ all -> 0x00dc }
            r1 = 0
            com.sun.mail.imap.IMAPStore$ConnectionPool r2 = r7.pool     // Catch:{ BadCommandException -> 0x00be, ConnectionException -> 0x00b3, ProtocolException -> 0x00a8 }
            monitor-enter(r2)     // Catch:{ BadCommandException -> 0x00be, ConnectionException -> 0x00b3, ProtocolException -> 0x00a8 }
            com.sun.mail.imap.protocol.IMAPProtocol r3 = r7.getStoreProtocol()     // Catch:{ all -> 0x00a0 }
            r0 = r3
            com.sun.mail.imap.IMAPStore$ConnectionPool r3 = r7.pool     // Catch:{ all -> 0x00a0 }
            int r3 = r3.idleState     // Catch:{ all -> 0x00a0 }
            if (r3 != 0) goto L_0x0083
            r0.idleStart()     // Catch:{ all -> 0x00a0 }
            com.sun.mail.imap.IMAPStore$ConnectionPool r3 = r7.pool     // Catch:{ all -> 0x00a0 }
            r4 = 1
            r3.idleState = r4     // Catch:{ all -> 0x00a0 }
            com.sun.mail.imap.IMAPStore$ConnectionPool r3 = r7.pool     // Catch:{ all -> 0x00a0 }
            r3.idleProtocol = r0     // Catch:{ all -> 0x00a0 }
            monitor-exit(r2)     // Catch:{ all -> 0x00a0 }
        L_0x002e:
            com.sun.mail.iap.Response r2 = r0.readIdleResponse()     // Catch:{ BadCommandException -> 0x00be, ConnectionException -> 0x00b3, ProtocolException -> 0x00a8 }
            com.sun.mail.imap.IMAPStore$ConnectionPool r3 = r7.pool     // Catch:{ BadCommandException -> 0x00be, ConnectionException -> 0x00b3, ProtocolException -> 0x00a8 }
            monitor-enter(r3)     // Catch:{ BadCommandException -> 0x00be, ConnectionException -> 0x00b3, ProtocolException -> 0x00a8 }
            if (r2 == 0) goto L_0x0053
            boolean r4 = r0.processIdleResponse(r2)     // Catch:{ all -> 0x0080 }
            if (r4 != 0) goto L_0x003e
            goto L_0x0053
        L_0x003e:
            monitor-exit(r3)     // Catch:{ all -> 0x0080 }
            boolean r3 = r7.enableImapEvents     // Catch:{ BadCommandException -> 0x00be, ConnectionException -> 0x00b3, ProtocolException -> 0x00a8 }
            if (r3 == 0) goto L_0x002e
            boolean r3 = r2.isUnTagged()     // Catch:{ BadCommandException -> 0x00be, ConnectionException -> 0x00b3, ProtocolException -> 0x00a8 }
            if (r3 == 0) goto L_0x002e
            r3 = 1000(0x3e8, float:1.401E-42)
            java.lang.String r4 = r2.toString()     // Catch:{ BadCommandException -> 0x00be, ConnectionException -> 0x00b3, ProtocolException -> 0x00a8 }
            r7.notifyStoreListeners(r3, r4)     // Catch:{ BadCommandException -> 0x00be, ConnectionException -> 0x00b3, ProtocolException -> 0x00a8 }
            goto L_0x002e
        L_0x0053:
            com.sun.mail.imap.IMAPStore$ConnectionPool r4 = r7.pool     // Catch:{ all -> 0x0080 }
            r5 = 0
            r4.idleState = r5     // Catch:{ all -> 0x0080 }
            com.sun.mail.imap.IMAPStore$ConnectionPool r4 = r7.pool     // Catch:{ all -> 0x0080 }
            r4.notifyAll()     // Catch:{ all -> 0x0080 }
            monitor-exit(r3)     // Catch:{ all -> 0x0080 }
            int r2 = r7.getMinIdleTime()     // Catch:{ BadCommandException -> 0x00be, ConnectionException -> 0x00b3, ProtocolException -> 0x00a8 }
            if (r2 <= 0) goto L_0x006b
            long r3 = (long) r2
            java.lang.Thread.sleep(r3)     // Catch:{ InterruptedException -> 0x006a }
            goto L_0x006b
        L_0x006a:
            r3 = move-exception
        L_0x006b:
            com.sun.mail.imap.IMAPStore$ConnectionPool r2 = r7.pool
            monitor-enter(r2)
            com.sun.mail.imap.IMAPStore$ConnectionPool r3 = r7.pool     // Catch:{ all -> 0x007d }
            r3.idleProtocol = r1     // Catch:{ all -> 0x007d }
            monitor-exit(r2)     // Catch:{ all -> 0x007d }
            r7.releaseStoreProtocol(r0)
            if (r0 != 0) goto L_0x007c
            r7.cleanup()
        L_0x007c:
            return
        L_0x007d:
            r1 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x007d }
            throw r1
        L_0x0080:
            r4 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0080 }
            throw r4     // Catch:{ BadCommandException -> 0x00be, ConnectionException -> 0x00b3, ProtocolException -> 0x00a8 }
        L_0x0083:
            com.sun.mail.imap.IMAPStore$ConnectionPool r3 = r7.pool     // Catch:{ InterruptedException -> 0x0089 }
            r3.wait()     // Catch:{ InterruptedException -> 0x0089 }
            goto L_0x008a
        L_0x0089:
            r3 = move-exception
        L_0x008a:
            monitor-exit(r2)     // Catch:{ all -> 0x00a0 }
            com.sun.mail.imap.IMAPStore$ConnectionPool r3 = r7.pool
            monitor-enter(r3)
            com.sun.mail.imap.IMAPStore$ConnectionPool r2 = r7.pool     // Catch:{ all -> 0x009d }
            r2.idleProtocol = r1     // Catch:{ all -> 0x009d }
            monitor-exit(r3)     // Catch:{ all -> 0x009d }
            r7.releaseStoreProtocol(r0)
            if (r0 != 0) goto L_0x009c
            r7.cleanup()
        L_0x009c:
            return
        L_0x009d:
            r1 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x009d }
            throw r1
        L_0x00a0:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x00a0 }
            throw r3     // Catch:{ BadCommandException -> 0x00be, ConnectionException -> 0x00b3, ProtocolException -> 0x00a8 }
        L_0x00a3:
            r2 = move-exception
            r6 = r2
            r2 = r0
            r0 = r6
            goto L_0x00c7
        L_0x00a8:
            r2 = move-exception
            javax.mail.MessagingException r3 = new javax.mail.MessagingException     // Catch:{ all -> 0x00a3 }
            java.lang.String r4 = r2.getMessage()     // Catch:{ all -> 0x00a3 }
            r3.<init>(r4, r2)     // Catch:{ all -> 0x00a3 }
            throw r3     // Catch:{ all -> 0x00a3 }
        L_0x00b3:
            r2 = move-exception
            javax.mail.StoreClosedException r3 = new javax.mail.StoreClosedException     // Catch:{ all -> 0x00a3 }
            java.lang.String r4 = r2.getMessage()     // Catch:{ all -> 0x00a3 }
            r3.<init>(r7, r4)     // Catch:{ all -> 0x00a3 }
            throw r3     // Catch:{ all -> 0x00a3 }
        L_0x00be:
            r2 = move-exception
            javax.mail.MessagingException r3 = new javax.mail.MessagingException     // Catch:{ all -> 0x00a3 }
            java.lang.String r4 = "IDLE not supported"
            r3.<init>(r4, r2)     // Catch:{ all -> 0x00a3 }
            throw r3     // Catch:{ all -> 0x00a3 }
        L_0x00c7:
            com.sun.mail.imap.IMAPStore$ConnectionPool r3 = r7.pool
            monitor-enter(r3)
            com.sun.mail.imap.IMAPStore$ConnectionPool r4 = r7.pool     // Catch:{ all -> 0x00d9 }
            r4.idleProtocol = r1     // Catch:{ all -> 0x00d9 }
            monitor-exit(r3)     // Catch:{ all -> 0x00d9 }
            r7.releaseStoreProtocol(r2)
            if (r2 != 0) goto L_0x00d8
            r7.cleanup()
        L_0x00d8:
            throw r0
        L_0x00d9:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x00d9 }
            throw r0
        L_0x00dc:
            r1 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x00dc }
            throw r1
        L_0x00df:
            java.lang.AssertionError r1 = new java.lang.AssertionError
            r1.<init>()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPStore.idle():void");
    }

    private void waitIfIdle() throws ProtocolException {
        if (Thread.holdsLock(this.pool)) {
            while (this.pool.idleState != 0) {
                if (this.pool.idleState == 1) {
                    this.pool.idleProtocol.idleAbort();
                    this.pool.idleState = 2;
                }
                try {
                    this.pool.wait();
                } catch (InterruptedException e) {
                }
            }
            return;
        }
        throw new AssertionError();
    }

    /* access modifiers changed from: package-private */
    public void handleResponseCode(Response r) {
        String s = r.getRest();
        boolean isAlert = false;
        if (s.startsWith("[")) {
            int i = s.indexOf(93);
            if (i > 0 && s.substring(0, i + 1).equalsIgnoreCase("[ALERT]")) {
                isAlert = true;
            }
            s = s.substring(i + 1).trim();
        }
        if (isAlert) {
            notifyStoreListeners(1, s);
        } else if (r.isUnTagged() && s.length() > 0) {
            notifyStoreListeners(2, s);
        }
    }
}
