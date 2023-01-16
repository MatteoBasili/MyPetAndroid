package com.sun.mail.pop3;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

public class POP3Store extends Store {
    private int defaultPort;
    boolean disableTop;
    boolean forgetTopHeaders;
    private String host;
    private boolean isSSL;
    Constructor messageConstructor;
    private String name;
    private String passwd;
    private Protocol port;
    private int portNum;
    private POP3Folder portOwner;
    boolean rsetBeforeQuit;
    private String user;

    public POP3Store(Session session, URLName url) {
        this(session, url, "pop3", 110, false);
    }

    public POP3Store(Session session, URLName url, String name2, int defaultPort2, boolean isSSL2) {
        super(session, url);
        Class messageClass;
        this.name = "pop3";
        this.defaultPort = 110;
        this.isSSL = false;
        this.port = null;
        this.portOwner = null;
        this.host = null;
        this.portNum = -1;
        this.user = null;
        this.passwd = null;
        this.rsetBeforeQuit = false;
        this.disableTop = false;
        this.forgetTopHeaders = false;
        this.messageConstructor = null;
        name2 = url != null ? url.getProtocol() : name2;
        this.name = name2;
        this.defaultPort = defaultPort2;
        this.isSSL = isSSL2;
        String s = session.getProperty("mail." + name2 + ".rsetbeforequit");
        if (s != null && s.equalsIgnoreCase("true")) {
            this.rsetBeforeQuit = true;
        }
        String s2 = session.getProperty("mail." + name2 + ".disabletop");
        if (s2 != null && s2.equalsIgnoreCase("true")) {
            this.disableTop = true;
        }
        String s3 = session.getProperty("mail." + name2 + ".forgettopheaders");
        if (s3 != null && s3.equalsIgnoreCase("true")) {
            this.forgetTopHeaders = true;
        }
        String s4 = session.getProperty("mail." + name2 + ".message.class");
        if (s4 != null) {
            if (session.getDebug()) {
                session.getDebugOut().println("DEBUG: POP3 message class: " + s4);
            }
            try {
                try {
                    messageClass = getClass().getClassLoader().loadClass(s4);
                } catch (ClassNotFoundException e) {
                    messageClass = Class.forName(s4);
                }
                this.messageConstructor = messageClass.getConstructor(new Class[]{Folder.class, Integer.TYPE});
            } catch (Exception ex) {
                if (session.getDebug()) {
                    session.getDebugOut().println("DEBUG: failed to load POP3 message class: " + ex);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public synchronized boolean protocolConnect(String host2, int portNum2, String user2, String passwd2) throws MessagingException {
        if (host2 == null || passwd2 == null || user2 == null) {
            return false;
        }
        if (portNum2 == -1) {
            try {
                String portstring = this.session.getProperty("mail." + this.name + ".port");
                if (portstring != null) {
                    portNum2 = Integer.parseInt(portstring);
                }
            } catch (EOFException eex) {
                throw new AuthenticationFailedException(eex.getMessage());
            } catch (IOException ioex) {
                throw new MessagingException("Connect failed", ioex);
            } catch (Throwable th) {
                throw th;
            }
        }
        if (portNum2 == -1) {
            portNum2 = this.defaultPort;
        }
        this.host = host2;
        this.portNum = portNum2;
        this.user = user2;
        this.passwd = passwd2;
        this.port = getPort((POP3Folder) null);
        return true;
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        	at java.util.ArrayList.get(ArrayList.java:435)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:598)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:24:0x0028=Splitter:B:24:0x0028, B:14:0x001a=Splitter:B:14:0x001a} */
    public synchronized boolean isConnected() {
        /*
            r3 = this;
            monitor-enter(r3)
            boolean r0 = super.isConnected()     // Catch:{ all -> 0x002f }
            r1 = 0
            if (r0 != 0) goto L_0x000a
            monitor-exit(r3)
            return r1
        L_0x000a:
            monitor-enter(r3)     // Catch:{ all -> 0x002f }
            com.sun.mail.pop3.Protocol r0 = r3.port     // Catch:{ IOException -> 0x0020 }
            if (r0 != 0) goto L_0x0017
            r0 = 0
            com.sun.mail.pop3.Protocol r0 = r3.getPort(r0)     // Catch:{ IOException -> 0x0020 }
            r3.port = r0     // Catch:{ IOException -> 0x0020 }
            goto L_0x001a
        L_0x0017:
            r0.noop()     // Catch:{ IOException -> 0x0020 }
        L_0x001a:
            monitor-exit(r3)     // Catch:{ all -> 0x001e }
            r0 = 1
            monitor-exit(r3)
            return r0
        L_0x001e:
            r0 = move-exception
            goto L_0x002b
        L_0x0020:
            r0 = move-exception
            super.close()     // Catch:{ MessagingException -> 0x0027, all -> 0x0025 }
            goto L_0x0028
        L_0x0025:
            r2 = move-exception
            goto L_0x0028
        L_0x0027:
            r2 = move-exception
        L_0x0028:
            monitor-exit(r3)     // Catch:{ all -> 0x001e }
            monitor-exit(r3)
            return r1
        L_0x002b:
            monitor-exit(r3)     // Catch:{ all -> 0x002d }
            throw r0     // Catch:{ all -> 0x002f }
        L_0x002d:
            r0 = move-exception
            goto L_0x002b
        L_0x002f:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.pop3.POP3Store.isConnected():boolean");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005a, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized com.sun.mail.pop3.Protocol getPort(com.sun.mail.pop3.POP3Folder r10) throws java.io.IOException {
        /*
            r9 = this;
            monitor-enter(r9)
            com.sun.mail.pop3.Protocol r0 = r9.port     // Catch:{ all -> 0x0068 }
            if (r0 == 0) goto L_0x000d
            com.sun.mail.pop3.POP3Folder r1 = r9.portOwner     // Catch:{ all -> 0x0068 }
            if (r1 != 0) goto L_0x000d
            r9.portOwner = r10     // Catch:{ all -> 0x0068 }
            monitor-exit(r9)
            return r0
        L_0x000d:
            com.sun.mail.pop3.Protocol r8 = new com.sun.mail.pop3.Protocol     // Catch:{ all -> 0x0068 }
            java.lang.String r1 = r9.host     // Catch:{ all -> 0x0068 }
            int r2 = r9.portNum     // Catch:{ all -> 0x0068 }
            javax.mail.Session r0 = r9.session     // Catch:{ all -> 0x0068 }
            boolean r3 = r0.getDebug()     // Catch:{ all -> 0x0068 }
            javax.mail.Session r0 = r9.session     // Catch:{ all -> 0x0068 }
            java.io.PrintStream r4 = r0.getDebugOut()     // Catch:{ all -> 0x0068 }
            javax.mail.Session r0 = r9.session     // Catch:{ all -> 0x0068 }
            java.util.Properties r5 = r0.getProperties()     // Catch:{ all -> 0x0068 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0068 }
            java.lang.String r6 = "mail."
            r0.<init>(r6)     // Catch:{ all -> 0x0068 }
            java.lang.String r6 = r9.name     // Catch:{ all -> 0x0068 }
            java.lang.StringBuilder r0 = r0.append(r6)     // Catch:{ all -> 0x0068 }
            java.lang.String r6 = r0.toString()     // Catch:{ all -> 0x0068 }
            boolean r7 = r9.isSSL     // Catch:{ all -> 0x0068 }
            r0 = r8
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)     // Catch:{ all -> 0x0068 }
            r0 = r8
            r1 = 0
            java.lang.String r2 = r9.user     // Catch:{ all -> 0x0068 }
            java.lang.String r3 = r9.passwd     // Catch:{ all -> 0x0068 }
            java.lang.String r2 = r0.login(r2, r3)     // Catch:{ all -> 0x0068 }
            r1 = r2
            if (r2 != 0) goto L_0x005b
            com.sun.mail.pop3.Protocol r2 = r9.port     // Catch:{ all -> 0x0068 }
            if (r2 != 0) goto L_0x0053
            if (r10 == 0) goto L_0x0053
            r9.port = r0     // Catch:{ all -> 0x0068 }
            r9.portOwner = r10     // Catch:{ all -> 0x0068 }
        L_0x0053:
            com.sun.mail.pop3.POP3Folder r2 = r9.portOwner     // Catch:{ all -> 0x0068 }
            if (r2 != 0) goto L_0x0059
            r9.portOwner = r10     // Catch:{ all -> 0x0068 }
        L_0x0059:
            monitor-exit(r9)
            return r0
        L_0x005b:
            r0.quit()     // Catch:{ IOException -> 0x0061, all -> 0x005f }
            goto L_0x0062
        L_0x005f:
            r2 = move-exception
            goto L_0x0062
        L_0x0061:
            r2 = move-exception
        L_0x0062:
            java.io.EOFException r2 = new java.io.EOFException     // Catch:{ all -> 0x0068 }
            r2.<init>(r1)     // Catch:{ all -> 0x0068 }
            throw r2     // Catch:{ all -> 0x0068 }
        L_0x0068:
            r10 = move-exception
            monitor-exit(r9)
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.pop3.POP3Store.getPort(com.sun.mail.pop3.POP3Folder):com.sun.mail.pop3.Protocol");
    }

    /* access modifiers changed from: package-private */
    public synchronized void closePort(POP3Folder owner) {
        if (this.portOwner == owner) {
            this.port = null;
            this.portOwner = null;
        }
    }

    public synchronized void close() throws MessagingException {
        try {
            Protocol protocol = this.port;
            if (protocol != null) {
                try {
                    protocol.quit();
                } catch (IOException e) {
                } catch (Throwable th) {
                    th = th;
                    this.port = null;
                    super.close();
                    throw th;
                }
            }
            this.port = null;
            super.close();
        } catch (IOException e2) {
            this.port = null;
            super.close();
        } catch (Throwable th2) {
            throw th2;
        }
    }

    public Folder getDefaultFolder() throws MessagingException {
        checkConnected();
        return new DefaultFolder(this);
    }

    public Folder getFolder(String name2) throws MessagingException {
        checkConnected();
        return new POP3Folder(this, name2);
    }

    public Folder getFolder(URLName url) throws MessagingException {
        checkConnected();
        return new POP3Folder(this, url.getFile());
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        super.finalize();
        if (this.port != null) {
            close();
        }
    }

    private void checkConnected() throws MessagingException {
        if (!super.isConnected()) {
            throw new MessagingException("Not connected");
        }
    }
}
