package com.sun.mail.imap;

import com.sun.mail.iap.BadCommandException;
import com.sun.mail.iap.CommandFailedException;
import com.sun.mail.iap.ConnectionException;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.iap.ResponseHandler;
import com.sun.mail.imap.protocol.FetchResponse;
import com.sun.mail.imap.protocol.IMAPProtocol;
import com.sun.mail.imap.protocol.IMAPResponse;
import com.sun.mail.imap.protocol.ListInfo;
import com.sun.mail.imap.protocol.Status;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.Quota;
import javax.mail.StoreClosedException;
import javax.mail.UIDFolder;
import javax.mail.search.FlagTerm;

public class IMAPFolder extends Folder implements UIDFolder, ResponseHandler {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int ABORTING = 2;
    private static final int IDLE = 1;
    private static final int RUNNING = 0;
    protected static final char UNKNOWN_SEPARATOR = '￿';
    protected String[] attributes;
    protected Flags availableFlags;
    private Status cachedStatus;
    private long cachedStatusTime;
    private boolean connectionPoolDebug;
    private boolean debug;
    private boolean doExpungeNotification;
    protected boolean exists;
    protected String fullName;
    /* access modifiers changed from: private */
    public int idleState;
    protected boolean isNamespace;
    protected Vector messageCache;
    protected Object messageCacheLock;
    protected String name;
    private boolean opened;
    private PrintStream out;
    protected Flags permanentFlags;
    protected IMAPProtocol protocol;
    private int realTotal;
    private boolean reallyClosed;
    private int recent;
    protected char separator;
    private int total;
    protected int type;
    protected Hashtable uidTable;
    private long uidnext;
    private long uidvalidity;

    public interface ProtocolCommand {
        Object doCommand(IMAPProtocol iMAPProtocol) throws ProtocolException;
    }

    public static class FetchProfileItem extends FetchProfile.Item {
        public static final FetchProfileItem HEADERS = new FetchProfileItem("HEADERS");
        public static final FetchProfileItem SIZE = new FetchProfileItem("SIZE");

        protected FetchProfileItem(String name) {
            super(name);
        }
    }

    protected IMAPFolder(String fullName2, char separator2, IMAPStore store) {
        super(store);
        int i;
        this.exists = false;
        this.isNamespace = false;
        this.opened = false;
        this.reallyClosed = true;
        this.idleState = 0;
        this.total = -1;
        this.recent = -1;
        this.realTotal = -1;
        this.uidvalidity = -1;
        this.uidnext = -1;
        this.doExpungeNotification = true;
        this.cachedStatus = null;
        this.cachedStatusTime = 0;
        this.debug = false;
        if (fullName2 != null) {
            this.fullName = fullName2;
            this.separator = separator2;
            this.messageCacheLock = new Object();
            this.debug = store.getSession().getDebug();
            this.connectionPoolDebug = store.getConnectionPoolDebug();
            PrintStream debugOut = store.getSession().getDebugOut();
            this.out = debugOut;
            if (debugOut == null) {
                this.out = System.out;
            }
            this.isNamespace = false;
            if (separator2 != 65535 && separator2 != 0 && (i = this.fullName.indexOf(separator2)) > 0 && i == this.fullName.length() - 1) {
                this.fullName = this.fullName.substring(0, i);
                this.isNamespace = true;
                return;
            }
            return;
        }
        throw new NullPointerException("Folder name is null");
    }

    protected IMAPFolder(String fullName2, char separator2, IMAPStore store, boolean isNamespace2) {
        this(fullName2, separator2, store);
        this.isNamespace = isNamespace2;
    }

    protected IMAPFolder(ListInfo li, IMAPStore store) {
        this(li.name, li.separator, store);
        if (li.hasInferiors) {
            this.type |= 2;
        }
        if (li.canOpen) {
            this.type |= 1;
        }
        this.exists = true;
        this.attributes = li.attrs;
    }

    private void checkExists() throws MessagingException {
        if (!this.exists && !exists()) {
            throw new FolderNotFoundException((Folder) this, String.valueOf(this.fullName) + " not found");
        }
    }

    private void checkClosed() {
        if (this.opened) {
            throw new IllegalStateException("This operation is not allowed on an open folder");
        }
    }

    private void checkOpened() throws FolderClosedException {
        if (!Thread.holdsLock(this)) {
            throw new AssertionError();
        } else if (this.opened) {
        } else {
            if (this.reallyClosed) {
                throw new IllegalStateException("This operation is not allowed on a closed folder");
            }
            throw new FolderClosedException(this, "Lost folder connection to server");
        }
    }

    private void checkRange(int msgno) throws MessagingException {
        if (msgno < 1) {
            throw new IndexOutOfBoundsException();
        } else if (msgno > this.total) {
            synchronized (this.messageCacheLock) {
                try {
                    keepConnectionAlive(false);
                } catch (ConnectionException cex) {
                    throw new FolderClosedException(this, cex.getMessage());
                } catch (ProtocolException pex) {
                    throw new MessagingException(pex.getMessage(), pex);
                }
            }
            if (msgno > this.total) {
                throw new IndexOutOfBoundsException();
            }
        }
    }

    private void checkFlags(Flags flags) throws MessagingException {
        if (!Thread.holdsLock(this)) {
            throw new AssertionError();
        } else if (this.mode != 2) {
            throw new IllegalStateException("Cannot change flags on READ_ONLY folder: " + this.fullName);
        }
    }

    public synchronized String getName() {
        if (this.name == null) {
            try {
                String str = this.fullName;
                this.name = str.substring(str.lastIndexOf(getSeparator()) + 1);
            } catch (MessagingException e) {
            }
        }
        return this.name;
    }

    public synchronized String getFullName() {
        return this.fullName;
    }

    public synchronized Folder getParent() throws MessagingException {
        char c = getSeparator();
        int lastIndexOf = this.fullName.lastIndexOf(c);
        int index = lastIndexOf;
        if (lastIndexOf != -1) {
            return new IMAPFolder(this.fullName.substring(0, index), c, (IMAPStore) this.store);
        }
        return new DefaultFolder((IMAPStore) this.store);
    }

    public synchronized boolean exists() throws MessagingException {
        final String lname;
        ListInfo[] listInfoArr = null;
        if (!this.isNamespace || this.separator == 0) {
            lname = this.fullName;
        } else {
            lname = String.valueOf(this.fullName) + this.separator;
        }
        ListInfo[] li = (ListInfo[]) doCommand(new ProtocolCommand() {
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                return p.list("", lname);
            }
        });
        if (li != null) {
            int i = findName(li, lname);
            this.fullName = li[i].name;
            this.separator = li[i].separator;
            int len = this.fullName.length();
            if (this.separator != 0 && len > 0 && this.fullName.charAt(len - 1) == this.separator) {
                this.fullName = this.fullName.substring(0, len - 1);
            }
            this.type = 0;
            if (li[i].hasInferiors) {
                this.type |= 2;
            }
            if (li[i].canOpen) {
                this.type |= 1;
            }
            this.exists = true;
            this.attributes = li[i].attrs;
        } else {
            this.exists = this.opened;
            this.attributes = null;
        }
        return this.exists;
    }

    private int findName(ListInfo[] li, String lname) {
        int i = 0;
        while (i < li.length && !li[i].name.equals(lname)) {
            i++;
        }
        if (i >= li.length) {
            return 0;
        }
        return i;
    }

    public Folder[] list(String pattern) throws MessagingException {
        return doList(pattern, false);
    }

    public Folder[] listSubscribed(String pattern) throws MessagingException {
        return doList(pattern, true);
    }

    private synchronized Folder[] doList(final String pattern, final boolean subscribed) throws MessagingException {
        checkExists();
        if (!isDirectory()) {
            return new Folder[0];
        }
        final char c = getSeparator();
        ListInfo[] li = (ListInfo[]) doCommandIgnoreFailure(new ProtocolCommand() {
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                if (subscribed) {
                    return p.lsub("", String.valueOf(IMAPFolder.this.fullName) + c + pattern);
                }
                return p.list("", String.valueOf(IMAPFolder.this.fullName) + c + pattern);
            }
        });
        if (li == null) {
            return new Folder[0];
        }
        int start = 0;
        if (li.length > 0 && li[0].name.equals(String.valueOf(this.fullName) + c)) {
            start = 1;
        }
        IMAPFolder[] folders = new IMAPFolder[(li.length - start)];
        for (int i = start; i < li.length; i++) {
            folders[i - start] = new IMAPFolder(li[i], (IMAPStore) this.store);
        }
        return folders;
    }

    public synchronized char getSeparator() throws MessagingException {
        if (this.separator == 65535) {
            ListInfo[] listInfoArr = null;
            ListInfo[] li = (ListInfo[]) doCommand(new ProtocolCommand() {
                public Object doCommand(IMAPProtocol p) throws ProtocolException {
                    if (p.isREV1()) {
                        return p.list(IMAPFolder.this.fullName, "");
                    }
                    return p.list("", IMAPFolder.this.fullName);
                }
            });
            if (li != null) {
                this.separator = li[0].separator;
            } else {
                this.separator = '/';
            }
        }
        return this.separator;
    }

    public synchronized int getType() throws MessagingException {
        if (!this.opened) {
            checkExists();
        } else if (this.attributes == null) {
            exists();
        }
        return this.type;
    }

    public synchronized boolean isSubscribed() {
        final String lname;
        ListInfo[] li = null;
        ListInfo[] listInfoArr = null;
        if (!this.isNamespace || this.separator == 0) {
            lname = this.fullName;
        } else {
            lname = String.valueOf(this.fullName) + this.separator;
        }
        try {
            li = (ListInfo[]) doProtocolCommand(new ProtocolCommand() {
                public Object doCommand(IMAPProtocol p) throws ProtocolException {
                    return p.lsub("", lname);
                }
            });
        } catch (ProtocolException e) {
        }
        if (li == null) {
            return false;
        }
        return li[findName(li, lname)].canOpen;
    }

    public synchronized void setSubscribed(final boolean subscribe) throws MessagingException {
        doCommandIgnoreFailure(new ProtocolCommand() {
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                if (subscribe) {
                    p.subscribe(IMAPFolder.this.fullName);
                    return null;
                }
                p.unsubscribe(IMAPFolder.this.fullName);
                return null;
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0025, code lost:
        return r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean create(final int r6) throws javax.mail.MessagingException {
        /*
            r5 = this;
            monitor-enter(r5)
            r0 = 0
            r1 = r6 & 1
            if (r1 != 0) goto L_0x000b
            char r1 = r5.getSeparator()     // Catch:{ all -> 0x0026 }
            r0 = r1
        L_0x000b:
            r1 = r0
            com.sun.mail.imap.IMAPFolder$6 r2 = new com.sun.mail.imap.IMAPFolder$6     // Catch:{ all -> 0x0026 }
            r2.<init>(r6, r1)     // Catch:{ all -> 0x0026 }
            java.lang.Object r2 = r5.doCommandIgnoreFailure(r2)     // Catch:{ all -> 0x0026 }
            if (r2 != 0) goto L_0x001a
            r3 = 0
            monitor-exit(r5)
            return r3
        L_0x001a:
            boolean r3 = r5.exists()     // Catch:{ all -> 0x0026 }
            if (r3 == 0) goto L_0x0024
            r4 = 1
            r5.notifyFolderListeners(r4)     // Catch:{ all -> 0x0026 }
        L_0x0024:
            monitor-exit(r5)
            return r3
        L_0x0026:
            r6 = move-exception
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPFolder.create(int):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x002f, code lost:
        r1 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean hasNewMessages() throws javax.mail.MessagingException {
        /*
            r4 = this;
            monitor-enter(r4)
            boolean r0 = r4.opened     // Catch:{ all -> 0x0049 }
            r1 = 0
            if (r0 == 0) goto L_0x0031
            java.lang.Object r0 = r4.messageCacheLock     // Catch:{ all -> 0x0049 }
            monitor-enter(r0)     // Catch:{ all -> 0x0049 }
            r2 = 1
            r4.keepConnectionAlive(r2)     // Catch:{ ConnectionException -> 0x0022, ProtocolException -> 0x0017 }
            int r3 = r4.recent     // Catch:{ all -> 0x0015 }
            if (r3 <= 0) goto L_0x0012
            r1 = r2
        L_0x0012:
            monitor-exit(r0)     // Catch:{ all -> 0x0015 }
            monitor-exit(r4)
            return r1
        L_0x0015:
            r1 = move-exception
            goto L_0x002d
        L_0x0017:
            r1 = move-exception
            javax.mail.MessagingException r2 = new javax.mail.MessagingException     // Catch:{ all -> 0x0015 }
            java.lang.String r3 = r1.getMessage()     // Catch:{ all -> 0x0015 }
            r2.<init>(r3, r1)     // Catch:{ all -> 0x0015 }
            throw r2     // Catch:{ all -> 0x0015 }
        L_0x0022:
            r1 = move-exception
            javax.mail.FolderClosedException r2 = new javax.mail.FolderClosedException     // Catch:{ all -> 0x0015 }
            java.lang.String r3 = r1.getMessage()     // Catch:{ all -> 0x0015 }
            r2.<init>(r4, r3)     // Catch:{ all -> 0x0015 }
            throw r2     // Catch:{ all -> 0x0015 }
        L_0x002d:
            monitor-exit(r0)     // Catch:{ all -> 0x002f }
            throw r1     // Catch:{ all -> 0x0049 }
        L_0x002f:
            r1 = move-exception
            goto L_0x002d
        L_0x0031:
            r4.checkExists()     // Catch:{ all -> 0x0049 }
            com.sun.mail.imap.IMAPFolder$7 r0 = new com.sun.mail.imap.IMAPFolder$7     // Catch:{ all -> 0x0049 }
            r0.<init>()     // Catch:{ all -> 0x0049 }
            java.lang.Object r0 = r4.doCommandIgnoreFailure(r0)     // Catch:{ all -> 0x0049 }
            java.lang.Boolean r0 = (java.lang.Boolean) r0     // Catch:{ all -> 0x0049 }
            if (r0 != 0) goto L_0x0043
            monitor-exit(r4)
            return r1
        L_0x0043:
            boolean r1 = r0.booleanValue()     // Catch:{ all -> 0x0049 }
            monitor-exit(r4)
            return r1
        L_0x0049:
            r0 = move-exception
            monitor-exit(r4)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPFolder.hasNewMessages():boolean");
    }

    public Folder getFolder(String name2) throws MessagingException {
        if (this.attributes == null || isDirectory()) {
            char c = getSeparator();
            return new IMAPFolder(String.valueOf(this.fullName) + c + name2, c, (IMAPStore) this.store);
        }
        throw new MessagingException("Cannot contain subfolders");
    }

    public synchronized boolean delete(boolean recurse) throws MessagingException {
        checkClosed();
        if (recurse) {
            Folder[] f = list();
            for (Folder delete : f) {
                delete.delete(recurse);
            }
        }
        if (doCommandIgnoreFailure(new ProtocolCommand() {
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                p.delete(IMAPFolder.this.fullName);
                return Boolean.TRUE;
            }
        }) == null) {
            return false;
        }
        this.exists = false;
        this.attributes = null;
        notifyFolderListeners(2);
        return true;
    }

    public synchronized boolean renameTo(final Folder f) throws MessagingException {
        checkClosed();
        checkExists();
        if (f.getStore() != this.store) {
            throw new MessagingException("Can't rename across Stores");
        } else if (doCommandIgnoreFailure(new ProtocolCommand() {
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                p.rename(IMAPFolder.this.fullName, f.getFullName());
                return Boolean.TRUE;
            }
        }) == null) {
            return false;
        } else {
            this.exists = false;
            this.attributes = null;
            notifyFolderRenamedListeners(f);
            return true;
        }
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
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:598)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:12:0x0030=Splitter:B:12:0x0030, B:34:0x0065=Splitter:B:34:0x0065, B:49:0x00be=Splitter:B:49:0x00be} */
    public synchronized void open(int r11) throws javax.mail.MessagingException {
        /*
            r10 = this;
            monitor-enter(r10)
            r10.checkClosed()     // Catch:{ all -> 0x00ff }
            r0 = 0
            javax.mail.Store r1 = r10.store     // Catch:{ all -> 0x00ff }
            com.sun.mail.imap.IMAPStore r1 = (com.sun.mail.imap.IMAPStore) r1     // Catch:{ all -> 0x00ff }
            com.sun.mail.imap.protocol.IMAPProtocol r1 = r1.getProtocol(r10)     // Catch:{ all -> 0x00ff }
            r10.protocol = r1     // Catch:{ all -> 0x00ff }
            r1 = 0
            java.lang.Object r2 = r10.messageCacheLock     // Catch:{ all -> 0x00ff }
            monitor-enter(r2)     // Catch:{ all -> 0x00ff }
            com.sun.mail.imap.protocol.IMAPProtocol r3 = r10.protocol     // Catch:{ all -> 0x00fc }
            r3.addResponseHandler(r10)     // Catch:{ all -> 0x00fc }
            r3 = 0
            r4 = 0
            r5 = 1
            if (r11 != r5) goto L_0x0027
            com.sun.mail.imap.protocol.IMAPProtocol r6 = r10.protocol     // Catch:{ CommandFailedException -> 0x00cd, ProtocolException -> 0x00b4 }
            java.lang.String r7 = r10.fullName     // Catch:{ CommandFailedException -> 0x00cd, ProtocolException -> 0x00b4 }
            com.sun.mail.imap.protocol.MailboxInfo r6 = r6.examine(r7)     // Catch:{ CommandFailedException -> 0x00cd, ProtocolException -> 0x00b4 }
            r0 = r6
            goto L_0x0030
        L_0x0027:
            com.sun.mail.imap.protocol.IMAPProtocol r6 = r10.protocol     // Catch:{ CommandFailedException -> 0x00cd, ProtocolException -> 0x00b4 }
            java.lang.String r7 = r10.fullName     // Catch:{ CommandFailedException -> 0x00cd, ProtocolException -> 0x00b4 }
            com.sun.mail.imap.protocol.MailboxInfo r6 = r6.select(r7)     // Catch:{ CommandFailedException -> 0x00cd, ProtocolException -> 0x00b4 }
            r0 = r6
        L_0x0030:
            int r6 = r0.mode     // Catch:{ all -> 0x00b2 }
            if (r6 == r11) goto L_0x006f
            r6 = 2
            if (r11 != r6) goto L_0x0046
            int r6 = r0.mode     // Catch:{ all -> 0x00b2 }
            if (r6 != r5) goto L_0x0046
            javax.mail.Store r6 = r10.store     // Catch:{ all -> 0x00b2 }
            com.sun.mail.imap.IMAPStore r6 = (com.sun.mail.imap.IMAPStore) r6     // Catch:{ all -> 0x00b2 }
            boolean r6 = r6.allowReadOnlySelect()     // Catch:{ all -> 0x00b2 }
            if (r6 == 0) goto L_0x0046
            goto L_0x006f
        L_0x0046:
            com.sun.mail.imap.protocol.IMAPProtocol r6 = r10.protocol     // Catch:{ ProtocolException -> 0x0051 }
            r6.close()     // Catch:{ ProtocolException -> 0x0051 }
            r10.releaseProtocol(r5)     // Catch:{ ProtocolException -> 0x0051 }
            goto L_0x0065
        L_0x004f:
            r4 = move-exception
            goto L_0x0065
        L_0x0051:
            r5 = move-exception
            com.sun.mail.imap.protocol.IMAPProtocol r6 = r10.protocol     // Catch:{ ProtocolException -> 0x0061, all -> 0x005b }
            r6.logout()     // Catch:{ ProtocolException -> 0x0061, all -> 0x005b }
            r10.releaseProtocol(r4)     // Catch:{ all -> 0x004f }
            goto L_0x0065
        L_0x005b:
            r6 = move-exception
            r10.releaseProtocol(r4)     // Catch:{ all -> 0x004f }
            throw r6     // Catch:{ all -> 0x004f }
        L_0x0061:
            r6 = move-exception
            r10.releaseProtocol(r4)     // Catch:{ all -> 0x004f }
        L_0x0065:
            r10.protocol = r3     // Catch:{ all -> 0x00b2 }
            javax.mail.ReadOnlyFolderException r3 = new javax.mail.ReadOnlyFolderException     // Catch:{ all -> 0x00b2 }
            java.lang.String r4 = "Cannot open in desired mode"
            r3.<init>(r10, r4)     // Catch:{ all -> 0x00b2 }
            throw r3     // Catch:{ all -> 0x00b2 }
        L_0x006f:
            r10.opened = r5     // Catch:{ all -> 0x00b2 }
            r10.reallyClosed = r4     // Catch:{ all -> 0x00b2 }
            int r4 = r0.mode     // Catch:{ all -> 0x00b2 }
            r10.mode = r4     // Catch:{ all -> 0x00b2 }
            javax.mail.Flags r4 = r0.availableFlags     // Catch:{ all -> 0x00b2 }
            r10.availableFlags = r4     // Catch:{ all -> 0x00b2 }
            javax.mail.Flags r4 = r0.permanentFlags     // Catch:{ all -> 0x00b2 }
            r10.permanentFlags = r4     // Catch:{ all -> 0x00b2 }
            int r4 = r0.total     // Catch:{ all -> 0x00b2 }
            r10.realTotal = r4     // Catch:{ all -> 0x00b2 }
            r10.total = r4     // Catch:{ all -> 0x00b2 }
            int r4 = r0.recent     // Catch:{ all -> 0x00b2 }
            r10.recent = r4     // Catch:{ all -> 0x00b2 }
            long r6 = r0.uidvalidity     // Catch:{ all -> 0x00b2 }
            r10.uidvalidity = r6     // Catch:{ all -> 0x00b2 }
            long r6 = r0.uidnext     // Catch:{ all -> 0x00b2 }
            r10.uidnext = r6     // Catch:{ all -> 0x00b2 }
            java.util.Vector r4 = new java.util.Vector     // Catch:{ all -> 0x00b2 }
            int r6 = r10.total     // Catch:{ all -> 0x00b2 }
            r4.<init>(r6)     // Catch:{ all -> 0x00b2 }
            r10.messageCache = r4     // Catch:{ all -> 0x00b2 }
            r4 = 0
        L_0x009b:
            int r6 = r10.total     // Catch:{ all -> 0x00b2 }
            if (r4 < r6) goto L_0x00a1
            monitor-exit(r2)     // Catch:{ all -> 0x00b2 }
            goto L_0x00d5
        L_0x00a1:
            java.util.Vector r6 = r10.messageCache     // Catch:{ all -> 0x00b2 }
            com.sun.mail.imap.IMAPMessage r7 = new com.sun.mail.imap.IMAPMessage     // Catch:{ all -> 0x00b2 }
            int r8 = r4 + 1
            int r9 = r4 + 1
            r7.<init>(r10, r8, r9)     // Catch:{ all -> 0x00b2 }
            r6.addElement(r7)     // Catch:{ all -> 0x00b2 }
            int r4 = r4 + 1
            goto L_0x009b
        L_0x00b2:
            r3 = move-exception
            goto L_0x00fd
        L_0x00b4:
            r5 = move-exception
            com.sun.mail.imap.protocol.IMAPProtocol r6 = r10.protocol     // Catch:{ ProtocolException -> 0x00bd, all -> 0x00bb }
            r6.logout()     // Catch:{ ProtocolException -> 0x00bd, all -> 0x00bb }
            goto L_0x00be
        L_0x00bb:
            r6 = move-exception
            goto L_0x00be
        L_0x00bd:
            r6 = move-exception
        L_0x00be:
            r10.releaseProtocol(r4)     // Catch:{ all -> 0x00b2 }
            r10.protocol = r3     // Catch:{ all -> 0x00b2 }
            javax.mail.MessagingException r3 = new javax.mail.MessagingException     // Catch:{ all -> 0x00b2 }
            java.lang.String r4 = r5.getMessage()     // Catch:{ all -> 0x00b2 }
            r3.<init>(r4, r5)     // Catch:{ all -> 0x00b2 }
            throw r3     // Catch:{ all -> 0x00b2 }
        L_0x00cd:
            r4 = move-exception
            r10.releaseProtocol(r5)     // Catch:{ all -> 0x00fc }
            r10.protocol = r3     // Catch:{ all -> 0x00fc }
            r1 = r4
            monitor-exit(r2)     // Catch:{ all -> 0x00fc }
        L_0x00d5:
            if (r1 == 0) goto L_0x00f1
            r10.checkExists()     // Catch:{ all -> 0x00ff }
            int r2 = r10.type     // Catch:{ all -> 0x00ff }
            r2 = r2 & r5
            if (r2 != 0) goto L_0x00e7
            javax.mail.MessagingException r2 = new javax.mail.MessagingException     // Catch:{ all -> 0x00ff }
            java.lang.String r3 = "folder cannot contain messages"
            r2.<init>(r3)     // Catch:{ all -> 0x00ff }
            throw r2     // Catch:{ all -> 0x00ff }
        L_0x00e7:
            javax.mail.MessagingException r2 = new javax.mail.MessagingException     // Catch:{ all -> 0x00ff }
            java.lang.String r3 = r1.getMessage()     // Catch:{ all -> 0x00ff }
            r2.<init>(r3, r1)     // Catch:{ all -> 0x00ff }
            throw r2     // Catch:{ all -> 0x00ff }
        L_0x00f1:
            r10.exists = r5     // Catch:{ all -> 0x00ff }
            r10.attributes = r3     // Catch:{ all -> 0x00ff }
            r10.type = r5     // Catch:{ all -> 0x00ff }
            r10.notifyConnectionListeners(r5)     // Catch:{ all -> 0x00ff }
            monitor-exit(r10)
            return
        L_0x00fc:
            r3 = move-exception
        L_0x00fd:
            monitor-exit(r2)     // Catch:{ all -> 0x00fc }
            throw r3     // Catch:{ all -> 0x00ff }
        L_0x00ff:
            r11 = move-exception
            monitor-exit(r10)
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPFolder.open(int):void");
    }

    public synchronized void fetch(Message[] msgs, FetchProfile fp) throws MessagingException {
        checkOpened();
        IMAPMessage.fetch(this, msgs, fp);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0042, code lost:
        r1 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void setFlags(javax.mail.Message[] r6, javax.mail.Flags r7, boolean r8) throws javax.mail.MessagingException {
        /*
            r5 = this;
            monitor-enter(r5)
            r5.checkOpened()     // Catch:{ all -> 0x0044 }
            r5.checkFlags(r7)     // Catch:{ all -> 0x0044 }
            int r0 = r6.length     // Catch:{ all -> 0x0044 }
            if (r0 != 0) goto L_0x000c
            monitor-exit(r5)
            return
        L_0x000c:
            java.lang.Object r0 = r5.messageCacheLock     // Catch:{ all -> 0x0044 }
            monitor-enter(r0)     // Catch:{ all -> 0x0044 }
            com.sun.mail.imap.protocol.IMAPProtocol r1 = r5.getProtocol()     // Catch:{ ConnectionException -> 0x0035, ProtocolException -> 0x002a }
            r2 = 0
            com.sun.mail.imap.protocol.MessageSet[] r2 = com.sun.mail.imap.Utility.toMessageSet(r6, r2)     // Catch:{ ConnectionException -> 0x0035, ProtocolException -> 0x002a }
            if (r2 == 0) goto L_0x0020
            r1.storeFlags((com.sun.mail.imap.protocol.MessageSet[]) r2, (javax.mail.Flags) r7, (boolean) r8)     // Catch:{ ConnectionException -> 0x0035, ProtocolException -> 0x002a }
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            monitor-exit(r5)
            return
        L_0x0020:
            javax.mail.MessageRemovedException r3 = new javax.mail.MessageRemovedException     // Catch:{ ConnectionException -> 0x0035, ProtocolException -> 0x002a }
            java.lang.String r4 = "Messages have been removed"
            r3.<init>(r4)     // Catch:{ ConnectionException -> 0x0035, ProtocolException -> 0x002a }
            throw r3     // Catch:{ ConnectionException -> 0x0035, ProtocolException -> 0x002a }
        L_0x0028:
            r1 = move-exception
            goto L_0x0040
        L_0x002a:
            r1 = move-exception
            javax.mail.MessagingException r2 = new javax.mail.MessagingException     // Catch:{ all -> 0x0028 }
            java.lang.String r3 = r1.getMessage()     // Catch:{ all -> 0x0028 }
            r2.<init>(r3, r1)     // Catch:{ all -> 0x0028 }
            throw r2     // Catch:{ all -> 0x0028 }
        L_0x0035:
            r1 = move-exception
            javax.mail.FolderClosedException r2 = new javax.mail.FolderClosedException     // Catch:{ all -> 0x0028 }
            java.lang.String r3 = r1.getMessage()     // Catch:{ all -> 0x0028 }
            r2.<init>(r5, r3)     // Catch:{ all -> 0x0028 }
            throw r2     // Catch:{ all -> 0x0028 }
        L_0x0040:
            monitor-exit(r0)     // Catch:{ all -> 0x0042 }
            throw r1     // Catch:{ all -> 0x0044 }
        L_0x0042:
            r1 = move-exception
            goto L_0x0040
        L_0x0044:
            r6 = move-exception
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPFolder.setFlags(javax.mail.Message[], javax.mail.Flags, boolean):void");
    }

    public synchronized void close(boolean expunge) throws MessagingException {
        close(expunge, false);
    }

    public synchronized void forceClose() throws MessagingException {
        close(false, true);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0099, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void close(boolean r6, boolean r7) throws javax.mail.MessagingException {
        /*
            r5 = this;
            boolean r0 = java.lang.Thread.holdsLock(r5)
            if (r0 == 0) goto L_0x00b3
            java.lang.Object r0 = r5.messageCacheLock
            monitor-enter(r0)
            boolean r1 = r5.opened     // Catch:{ all -> 0x00b0 }
            if (r1 != 0) goto L_0x001a
            boolean r2 = r5.reallyClosed     // Catch:{ all -> 0x00b0 }
            if (r2 != 0) goto L_0x0012
            goto L_0x001a
        L_0x0012:
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException     // Catch:{ all -> 0x00b0 }
            java.lang.String r2 = "This operation is not allowed on a closed folder"
            r1.<init>(r2)     // Catch:{ all -> 0x00b0 }
            throw r1     // Catch:{ all -> 0x00b0 }
        L_0x001a:
            r2 = 1
            r5.reallyClosed = r2     // Catch:{ all -> 0x00b0 }
            if (r1 != 0) goto L_0x0021
            monitor-exit(r0)     // Catch:{ all -> 0x00b0 }
            return
        L_0x0021:
            r5.waitIfIdle()     // Catch:{ ProtocolException -> 0x009c }
            if (r7 == 0) goto L_0x004e
            boolean r1 = r5.debug     // Catch:{ ProtocolException -> 0x009c }
            if (r1 == 0) goto L_0x0046
            java.io.PrintStream r1 = r5.out     // Catch:{ ProtocolException -> 0x009c }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ ProtocolException -> 0x009c }
            java.lang.String r4 = "DEBUG: forcing folder "
            r3.<init>(r4)     // Catch:{ ProtocolException -> 0x009c }
            java.lang.String r4 = r5.fullName     // Catch:{ ProtocolException -> 0x009c }
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ ProtocolException -> 0x009c }
            java.lang.String r4 = " to close"
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ ProtocolException -> 0x009c }
            java.lang.String r3 = r3.toString()     // Catch:{ ProtocolException -> 0x009c }
            r1.println(r3)     // Catch:{ ProtocolException -> 0x009c }
        L_0x0046:
            com.sun.mail.imap.protocol.IMAPProtocol r1 = r5.protocol     // Catch:{ ProtocolException -> 0x009c }
            if (r1 == 0) goto L_0x0091
            r1.disconnect()     // Catch:{ ProtocolException -> 0x009c }
            goto L_0x0091
        L_0x004e:
            javax.mail.Store r1 = r5.store     // Catch:{ ProtocolException -> 0x009c }
            com.sun.mail.imap.IMAPStore r1 = (com.sun.mail.imap.IMAPStore) r1     // Catch:{ ProtocolException -> 0x009c }
            boolean r1 = r1.isConnectionPoolFull()     // Catch:{ ProtocolException -> 0x009c }
            if (r1 == 0) goto L_0x0072
            boolean r1 = r5.debug     // Catch:{ ProtocolException -> 0x009c }
            if (r1 == 0) goto L_0x0063
            java.io.PrintStream r1 = r5.out     // Catch:{ ProtocolException -> 0x009c }
            java.lang.String r3 = "DEBUG: pool is full, not adding an Authenticated connection"
            r1.println(r3)     // Catch:{ ProtocolException -> 0x009c }
        L_0x0063:
            if (r6 == 0) goto L_0x006a
            com.sun.mail.imap.protocol.IMAPProtocol r1 = r5.protocol     // Catch:{ ProtocolException -> 0x009c }
            r1.close()     // Catch:{ ProtocolException -> 0x009c }
        L_0x006a:
            com.sun.mail.imap.protocol.IMAPProtocol r1 = r5.protocol     // Catch:{ ProtocolException -> 0x009c }
            if (r1 == 0) goto L_0x0091
            r1.logout()     // Catch:{ ProtocolException -> 0x009c }
            goto L_0x0091
        L_0x0072:
            if (r6 != 0) goto L_0x008a
            int r1 = r5.mode     // Catch:{ ProtocolException -> 0x009c }
            r3 = 2
            if (r1 != r3) goto L_0x008a
            com.sun.mail.imap.protocol.IMAPProtocol r1 = r5.protocol     // Catch:{ ProtocolException -> 0x0082 }
            java.lang.String r3 = r5.fullName     // Catch:{ ProtocolException -> 0x0082 }
            com.sun.mail.imap.protocol.MailboxInfo r1 = r1.examine(r3)     // Catch:{ ProtocolException -> 0x0082 }
            goto L_0x008a
        L_0x0082:
            r1 = move-exception
            com.sun.mail.imap.protocol.IMAPProtocol r3 = r5.protocol     // Catch:{ ProtocolException -> 0x009c }
            if (r3 == 0) goto L_0x008a
            r3.disconnect()     // Catch:{ ProtocolException -> 0x009c }
        L_0x008a:
            com.sun.mail.imap.protocol.IMAPProtocol r1 = r5.protocol     // Catch:{ ProtocolException -> 0x009c }
            if (r1 == 0) goto L_0x0091
            r1.close()     // Catch:{ ProtocolException -> 0x009c }
        L_0x0091:
            boolean r1 = r5.opened     // Catch:{ all -> 0x00b0 }
            if (r1 == 0) goto L_0x0098
            r5.cleanup(r2)     // Catch:{ all -> 0x00b0 }
        L_0x0098:
            monitor-exit(r0)     // Catch:{ all -> 0x00b0 }
            return
        L_0x009a:
            r1 = move-exception
            goto L_0x00a7
        L_0x009c:
            r1 = move-exception
            javax.mail.MessagingException r3 = new javax.mail.MessagingException     // Catch:{ all -> 0x009a }
            java.lang.String r4 = r1.getMessage()     // Catch:{ all -> 0x009a }
            r3.<init>(r4, r1)     // Catch:{ all -> 0x009a }
            throw r3     // Catch:{ all -> 0x009a }
        L_0x00a7:
            boolean r3 = r5.opened     // Catch:{ all -> 0x00b0 }
            if (r3 == 0) goto L_0x00ae
            r5.cleanup(r2)     // Catch:{ all -> 0x00b0 }
        L_0x00ae:
            throw r1     // Catch:{ all -> 0x00b0 }
        L_0x00b0:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00b0 }
            throw r1
        L_0x00b3:
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPFolder.close(boolean, boolean):void");
    }

    private void cleanup(boolean returnToPool) {
        releaseProtocol(returnToPool);
        this.protocol = null;
        this.messageCache = null;
        this.uidTable = null;
        this.exists = false;
        this.attributes = null;
        this.opened = false;
        this.idleState = 0;
        notifyConnectionListeners(3);
    }

    public synchronized boolean isOpen() {
        synchronized (this.messageCacheLock) {
            if (this.opened) {
                try {
                    keepConnectionAlive(false);
                } catch (ProtocolException e) {
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            }
            try {
            } catch (Throwable th2) {
                th = th2;
                throw th;
            }
        }
        return this.opened;
    }

    public synchronized Flags getPermanentFlags() {
        return (Flags) this.permanentFlags.clone();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001a, code lost:
        throw new javax.mail.MessagingException(r0.getMessage(), r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0027, code lost:
        throw new javax.mail.StoreClosedException(r5.store, r0.getMessage());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0029, code lost:
        r1 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        r1 = getStoreProtocol();
        r2 = r1.examine(r5.fullName);
        r1.close();
        r3 = r2.total;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        releaseStoreProtocol(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x003e, code lost:
        return r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x003f, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0041, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x004b, code lost:
        throw new javax.mail.MessagingException(r2.getMessage(), r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        releaseStoreProtocol(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x004f, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x005e, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0068, code lost:
        throw new javax.mail.MessagingException(r1.getMessage(), r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0069, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0073, code lost:
        throw new javax.mail.FolderClosedException(r5, r1.getMessage());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0010, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:5:0x0008, B:18:0x002a, B:26:0x0042, B:35:0x0054] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int getMessageCount() throws javax.mail.MessagingException {
        /*
            r5 = this;
            monitor-enter(r5)
            boolean r0 = r5.opened     // Catch:{ all -> 0x0076 }
            if (r0 != 0) goto L_0x0050
            r5.checkExists()     // Catch:{ all -> 0x0076 }
            com.sun.mail.imap.protocol.Status r0 = r5.getStatus()     // Catch:{ BadCommandException -> 0x0028, ConnectionException -> 0x001b, ProtocolException -> 0x0010 }
            int r1 = r0.total     // Catch:{ BadCommandException -> 0x0028, ConnectionException -> 0x001b, ProtocolException -> 0x0010 }
            monitor-exit(r5)
            return r1
        L_0x0010:
            r0 = move-exception
            javax.mail.MessagingException r1 = new javax.mail.MessagingException     // Catch:{ all -> 0x0076 }
            java.lang.String r2 = r0.getMessage()     // Catch:{ all -> 0x0076 }
            r1.<init>(r2, r0)     // Catch:{ all -> 0x0076 }
            throw r1     // Catch:{ all -> 0x0076 }
        L_0x001b:
            r0 = move-exception
            javax.mail.StoreClosedException r1 = new javax.mail.StoreClosedException     // Catch:{ all -> 0x0076 }
            javax.mail.Store r2 = r5.store     // Catch:{ all -> 0x0076 }
            java.lang.String r3 = r0.getMessage()     // Catch:{ all -> 0x0076 }
            r1.<init>(r2, r3)     // Catch:{ all -> 0x0076 }
            throw r1     // Catch:{ all -> 0x0076 }
        L_0x0028:
            r0 = move-exception
            r1 = 0
            com.sun.mail.imap.protocol.IMAPProtocol r2 = r5.getStoreProtocol()     // Catch:{ ProtocolException -> 0x0041 }
            r1 = r2
            java.lang.String r2 = r5.fullName     // Catch:{ ProtocolException -> 0x0041 }
            com.sun.mail.imap.protocol.MailboxInfo r2 = r1.examine(r2)     // Catch:{ ProtocolException -> 0x0041 }
            r1.close()     // Catch:{ ProtocolException -> 0x0041 }
            int r3 = r2.total     // Catch:{ ProtocolException -> 0x0041 }
            r5.releaseStoreProtocol(r1)     // Catch:{ all -> 0x0076 }
            monitor-exit(r5)
            return r3
        L_0x003f:
            r2 = move-exception
            goto L_0x004c
        L_0x0041:
            r2 = move-exception
            javax.mail.MessagingException r3 = new javax.mail.MessagingException     // Catch:{ all -> 0x003f }
            java.lang.String r4 = r2.getMessage()     // Catch:{ all -> 0x003f }
            r3.<init>(r4, r2)     // Catch:{ all -> 0x003f }
            throw r3     // Catch:{ all -> 0x003f }
        L_0x004c:
            r5.releaseStoreProtocol(r1)     // Catch:{ all -> 0x0076 }
            throw r2     // Catch:{ all -> 0x0076 }
        L_0x0050:
            java.lang.Object r0 = r5.messageCacheLock     // Catch:{ all -> 0x0076 }
            monitor-enter(r0)     // Catch:{ all -> 0x0076 }
            r1 = 1
            r5.keepConnectionAlive(r1)     // Catch:{ ConnectionException -> 0x0069, ProtocolException -> 0x005e }
            int r1 = r5.total     // Catch:{ ConnectionException -> 0x0069, ProtocolException -> 0x005e }
            monitor-exit(r0)     // Catch:{ all -> 0x005c }
            monitor-exit(r5)
            return r1
        L_0x005c:
            r1 = move-exception
            goto L_0x0074
        L_0x005e:
            r1 = move-exception
            javax.mail.MessagingException r2 = new javax.mail.MessagingException     // Catch:{ all -> 0x005c }
            java.lang.String r3 = r1.getMessage()     // Catch:{ all -> 0x005c }
            r2.<init>(r3, r1)     // Catch:{ all -> 0x005c }
            throw r2     // Catch:{ all -> 0x005c }
        L_0x0069:
            r1 = move-exception
            javax.mail.FolderClosedException r2 = new javax.mail.FolderClosedException     // Catch:{ all -> 0x005c }
            java.lang.String r3 = r1.getMessage()     // Catch:{ all -> 0x005c }
            r2.<init>(r5, r3)     // Catch:{ all -> 0x005c }
            throw r2     // Catch:{ all -> 0x005c }
        L_0x0074:
            monitor-exit(r0)     // Catch:{ all -> 0x005c }
            throw r1     // Catch:{ all -> 0x0076 }
        L_0x0076:
            r0 = move-exception
            monitor-exit(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPFolder.getMessageCount():int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001a, code lost:
        throw new javax.mail.MessagingException(r0.getMessage(), r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0027, code lost:
        throw new javax.mail.StoreClosedException(r5.store, r0.getMessage());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0029, code lost:
        r1 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        r1 = getStoreProtocol();
        r2 = r1.examine(r5.fullName);
        r1.close();
        r3 = r2.recent;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        releaseStoreProtocol(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x003e, code lost:
        return r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x003f, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0041, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x004b, code lost:
        throw new javax.mail.MessagingException(r2.getMessage(), r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        releaseStoreProtocol(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x004f, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x005e, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0068, code lost:
        throw new javax.mail.MessagingException(r1.getMessage(), r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0069, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0073, code lost:
        throw new javax.mail.FolderClosedException(r5, r1.getMessage());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0010, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:5:0x0008, B:18:0x002a, B:26:0x0042, B:35:0x0054] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int getNewMessageCount() throws javax.mail.MessagingException {
        /*
            r5 = this;
            monitor-enter(r5)
            boolean r0 = r5.opened     // Catch:{ all -> 0x0076 }
            if (r0 != 0) goto L_0x0050
            r5.checkExists()     // Catch:{ all -> 0x0076 }
            com.sun.mail.imap.protocol.Status r0 = r5.getStatus()     // Catch:{ BadCommandException -> 0x0028, ConnectionException -> 0x001b, ProtocolException -> 0x0010 }
            int r1 = r0.recent     // Catch:{ BadCommandException -> 0x0028, ConnectionException -> 0x001b, ProtocolException -> 0x0010 }
            monitor-exit(r5)
            return r1
        L_0x0010:
            r0 = move-exception
            javax.mail.MessagingException r1 = new javax.mail.MessagingException     // Catch:{ all -> 0x0076 }
            java.lang.String r2 = r0.getMessage()     // Catch:{ all -> 0x0076 }
            r1.<init>(r2, r0)     // Catch:{ all -> 0x0076 }
            throw r1     // Catch:{ all -> 0x0076 }
        L_0x001b:
            r0 = move-exception
            javax.mail.StoreClosedException r1 = new javax.mail.StoreClosedException     // Catch:{ all -> 0x0076 }
            javax.mail.Store r2 = r5.store     // Catch:{ all -> 0x0076 }
            java.lang.String r3 = r0.getMessage()     // Catch:{ all -> 0x0076 }
            r1.<init>(r2, r3)     // Catch:{ all -> 0x0076 }
            throw r1     // Catch:{ all -> 0x0076 }
        L_0x0028:
            r0 = move-exception
            r1 = 0
            com.sun.mail.imap.protocol.IMAPProtocol r2 = r5.getStoreProtocol()     // Catch:{ ProtocolException -> 0x0041 }
            r1 = r2
            java.lang.String r2 = r5.fullName     // Catch:{ ProtocolException -> 0x0041 }
            com.sun.mail.imap.protocol.MailboxInfo r2 = r1.examine(r2)     // Catch:{ ProtocolException -> 0x0041 }
            r1.close()     // Catch:{ ProtocolException -> 0x0041 }
            int r3 = r2.recent     // Catch:{ ProtocolException -> 0x0041 }
            r5.releaseStoreProtocol(r1)     // Catch:{ all -> 0x0076 }
            monitor-exit(r5)
            return r3
        L_0x003f:
            r2 = move-exception
            goto L_0x004c
        L_0x0041:
            r2 = move-exception
            javax.mail.MessagingException r3 = new javax.mail.MessagingException     // Catch:{ all -> 0x003f }
            java.lang.String r4 = r2.getMessage()     // Catch:{ all -> 0x003f }
            r3.<init>(r4, r2)     // Catch:{ all -> 0x003f }
            throw r3     // Catch:{ all -> 0x003f }
        L_0x004c:
            r5.releaseStoreProtocol(r1)     // Catch:{ all -> 0x0076 }
            throw r2     // Catch:{ all -> 0x0076 }
        L_0x0050:
            java.lang.Object r0 = r5.messageCacheLock     // Catch:{ all -> 0x0076 }
            monitor-enter(r0)     // Catch:{ all -> 0x0076 }
            r1 = 1
            r5.keepConnectionAlive(r1)     // Catch:{ ConnectionException -> 0x0069, ProtocolException -> 0x005e }
            int r1 = r5.recent     // Catch:{ ConnectionException -> 0x0069, ProtocolException -> 0x005e }
            monitor-exit(r0)     // Catch:{ all -> 0x005c }
            monitor-exit(r5)
            return r1
        L_0x005c:
            r1 = move-exception
            goto L_0x0074
        L_0x005e:
            r1 = move-exception
            javax.mail.MessagingException r2 = new javax.mail.MessagingException     // Catch:{ all -> 0x005c }
            java.lang.String r3 = r1.getMessage()     // Catch:{ all -> 0x005c }
            r2.<init>(r3, r1)     // Catch:{ all -> 0x005c }
            throw r2     // Catch:{ all -> 0x005c }
        L_0x0069:
            r1 = move-exception
            javax.mail.FolderClosedException r2 = new javax.mail.FolderClosedException     // Catch:{ all -> 0x005c }
            java.lang.String r3 = r1.getMessage()     // Catch:{ all -> 0x005c }
            r2.<init>(r5, r3)     // Catch:{ all -> 0x005c }
            throw r2     // Catch:{ all -> 0x005c }
        L_0x0074:
            monitor-exit(r0)     // Catch:{ all -> 0x005c }
            throw r1     // Catch:{ all -> 0x0076 }
        L_0x0076:
            r0 = move-exception
            monitor-exit(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPFolder.getNewMessageCount():int");
    }

    public synchronized int getUnreadMessageCount() throws MessagingException {
        int length;
        if (!this.opened) {
            checkExists();
            try {
                return getStatus().unseen;
            } catch (BadCommandException e) {
                return -1;
            } catch (ConnectionException cex) {
                throw new StoreClosedException(this.store, cex.getMessage());
            } catch (ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            }
        } else {
            Flags f = new Flags();
            f.add(Flags.Flag.SEEN);
            try {
                synchronized (this.messageCacheLock) {
                    length = getProtocol().search(new FlagTerm(f, false)).length;
                }
                return length;
            } catch (ConnectionException cex2) {
                throw new FolderClosedException(this, cex2.getMessage());
            } catch (ProtocolException pex2) {
                throw new MessagingException(pex2.getMessage(), pex2);
            }
        }
    }

    public synchronized int getDeletedMessageCount() throws MessagingException {
        int length;
        if (!this.opened) {
            checkExists();
            return -1;
        }
        Flags f = new Flags();
        f.add(Flags.Flag.DELETED);
        try {
            synchronized (this.messageCacheLock) {
                length = getProtocol().search(new FlagTerm(f, true)).length;
            }
            return length;
        } catch (ConnectionException cex) {
            throw new FolderClosedException(this, cex.getMessage());
        } catch (ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
    }

    /* access modifiers changed from: private */
    public Status getStatus() throws ProtocolException {
        int statusCacheTimeout = ((IMAPStore) this.store).getStatusCacheTimeout();
        if (statusCacheTimeout > 0 && this.cachedStatus != null && System.currentTimeMillis() - this.cachedStatusTime < ((long) statusCacheTimeout)) {
            return this.cachedStatus;
        }
        IMAPProtocol p = null;
        try {
            p = getStoreProtocol();
            Status s = p.status(this.fullName, (String[]) null);
            if (statusCacheTimeout > 0) {
                this.cachedStatus = s;
                this.cachedStatusTime = System.currentTimeMillis();
            }
            return s;
        } finally {
            releaseStoreProtocol(p);
        }
    }

    public synchronized Message getMessage(int msgnum) throws MessagingException {
        checkOpened();
        checkRange(msgnum);
        return (Message) this.messageCache.elementAt(msgnum - 1);
    }

    public synchronized void appendMessages(Message[] msgs) throws MessagingException {
        checkExists();
        int maxsize = ((IMAPStore) this.store).getAppendBufferSize();
        for (Message m : msgs) {
            try {
                final MessageLiteral mos = new MessageLiteral(m, m.getSize() > maxsize ? 0 : maxsize);
                Date d = m.getReceivedDate();
                if (d == null) {
                    d = m.getSentDate();
                }
                final Date dd = d;
                final Flags f = m.getFlags();
                doCommand(new ProtocolCommand() {
                    public Object doCommand(IMAPProtocol p) throws ProtocolException {
                        p.append(IMAPFolder.this.fullName, f, dd, mos);
                        return null;
                    }
                });
            } catch (IOException ex) {
                throw new MessagingException("IOException while appending messages", ex);
            } catch (MessageRemovedException e) {
            }
        }
    }

    public synchronized AppendUID[] appendUIDMessages(Message[] msgs) throws MessagingException {
        AppendUID[] uids;
        checkExists();
        int maxsize = ((IMAPStore) this.store).getAppendBufferSize();
        uids = new AppendUID[msgs.length];
        for (int i = 0; i < msgs.length; i++) {
            Message m = msgs[i];
            try {
                final MessageLiteral mos = new MessageLiteral(m, m.getSize() > maxsize ? 0 : maxsize);
                Date d = m.getReceivedDate();
                if (d == null) {
                    d = m.getSentDate();
                }
                final Date dd = d;
                final Flags f = m.getFlags();
                uids[i] = (AppendUID) doCommand(new ProtocolCommand() {
                    public Object doCommand(IMAPProtocol p) throws ProtocolException {
                        return p.appenduid(IMAPFolder.this.fullName, f, dd, mos);
                    }
                });
            } catch (IOException ex) {
                throw new MessagingException("IOException while appending messages", ex);
            } catch (MessageRemovedException e) {
            }
        }
        return uids;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: javax.mail.internet.MimeMessage[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized javax.mail.Message[] addMessages(javax.mail.Message[] r9) throws javax.mail.MessagingException {
        /*
            r8 = this;
            monitor-enter(r8)
            r8.checkOpened()     // Catch:{ all -> 0x002a }
            int r0 = r9.length     // Catch:{ all -> 0x002a }
            javax.mail.internet.MimeMessage[] r0 = new javax.mail.internet.MimeMessage[r0]     // Catch:{ all -> 0x002a }
            com.sun.mail.imap.AppendUID[] r1 = r8.appendUIDMessages(r9)     // Catch:{ all -> 0x002a }
            r2 = 0
        L_0x000c:
            int r3 = r1.length     // Catch:{ all -> 0x002a }
            if (r2 < r3) goto L_0x0011
            monitor-exit(r8)
            return r0
        L_0x0011:
            r3 = r1[r2]     // Catch:{ all -> 0x002a }
            if (r3 == 0) goto L_0x0027
            long r4 = r3.uidvalidity     // Catch:{ all -> 0x002a }
            long r6 = r8.uidvalidity     // Catch:{ all -> 0x002a }
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 != 0) goto L_0x0027
            long r4 = r3.uid     // Catch:{ MessagingException -> 0x0026 }
            javax.mail.Message r4 = r8.getMessageByUID(r4)     // Catch:{ MessagingException -> 0x0026 }
            r0[r2] = r4     // Catch:{ MessagingException -> 0x0026 }
            goto L_0x0027
        L_0x0026:
            r4 = move-exception
        L_0x0027:
            int r2 = r2 + 1
            goto L_0x000c
        L_0x002a:
            r9 = move-exception
            monitor-exit(r8)
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPFolder.addMessages(javax.mail.Message[]):javax.mail.Message[]");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0080, code lost:
        r1 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0086, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void copyMessages(javax.mail.Message[] r6, javax.mail.Folder r7) throws javax.mail.MessagingException {
        /*
            r5 = this;
            monitor-enter(r5)
            r5.checkOpened()     // Catch:{ all -> 0x0087 }
            int r0 = r6.length     // Catch:{ all -> 0x0087 }
            if (r0 != 0) goto L_0x0009
            monitor-exit(r5)
            return
        L_0x0009:
            javax.mail.Store r0 = r7.getStore()     // Catch:{ all -> 0x0087 }
            javax.mail.Store r1 = r5.store     // Catch:{ all -> 0x0087 }
            if (r0 != r1) goto L_0x0082
            java.lang.Object r0 = r5.messageCacheLock     // Catch:{ all -> 0x0087 }
            monitor-enter(r0)     // Catch:{ all -> 0x0087 }
            com.sun.mail.imap.protocol.IMAPProtocol r1 = r5.getProtocol()     // Catch:{ CommandFailedException -> 0x0048, ConnectionException -> 0x003d, ProtocolException -> 0x0032 }
            r2 = 0
            com.sun.mail.imap.protocol.MessageSet[] r2 = com.sun.mail.imap.Utility.toMessageSet(r6, r2)     // Catch:{ CommandFailedException -> 0x0048, ConnectionException -> 0x003d, ProtocolException -> 0x0032 }
            if (r2 == 0) goto L_0x0028
            java.lang.String r3 = r7.getFullName()     // Catch:{ CommandFailedException -> 0x0048, ConnectionException -> 0x003d, ProtocolException -> 0x0032 }
            r1.copy((com.sun.mail.imap.protocol.MessageSet[]) r2, (java.lang.String) r3)     // Catch:{ CommandFailedException -> 0x0048, ConnectionException -> 0x003d, ProtocolException -> 0x0032 }
            monitor-exit(r0)     // Catch:{ all -> 0x0030 }
            goto L_0x0085
        L_0x0028:
            javax.mail.MessageRemovedException r3 = new javax.mail.MessageRemovedException     // Catch:{ CommandFailedException -> 0x0048, ConnectionException -> 0x003d, ProtocolException -> 0x0032 }
            java.lang.String r4 = "Messages have been removed"
            r3.<init>(r4)     // Catch:{ CommandFailedException -> 0x0048, ConnectionException -> 0x003d, ProtocolException -> 0x0032 }
            throw r3     // Catch:{ CommandFailedException -> 0x0048, ConnectionException -> 0x003d, ProtocolException -> 0x0032 }
        L_0x0030:
            r1 = move-exception
            goto L_0x007e
        L_0x0032:
            r1 = move-exception
            javax.mail.MessagingException r2 = new javax.mail.MessagingException     // Catch:{ all -> 0x0030 }
            java.lang.String r3 = r1.getMessage()     // Catch:{ all -> 0x0030 }
            r2.<init>(r3, r1)     // Catch:{ all -> 0x0030 }
            throw r2     // Catch:{ all -> 0x0030 }
        L_0x003d:
            r1 = move-exception
            javax.mail.FolderClosedException r2 = new javax.mail.FolderClosedException     // Catch:{ all -> 0x0030 }
            java.lang.String r3 = r1.getMessage()     // Catch:{ all -> 0x0030 }
            r2.<init>(r5, r3)     // Catch:{ all -> 0x0030 }
            throw r2     // Catch:{ all -> 0x0030 }
        L_0x0048:
            r1 = move-exception
            java.lang.String r2 = r1.getMessage()     // Catch:{ all -> 0x0030 }
            java.lang.String r3 = "TRYCREATE"
            int r2 = r2.indexOf(r3)     // Catch:{ all -> 0x0030 }
            r3 = -1
            if (r2 == r3) goto L_0x0074
            javax.mail.FolderNotFoundException r2 = new javax.mail.FolderNotFoundException     // Catch:{ all -> 0x0030 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0030 }
            java.lang.String r4 = r7.getFullName()     // Catch:{ all -> 0x0030 }
            java.lang.String r4 = java.lang.String.valueOf(r4)     // Catch:{ all -> 0x0030 }
            r3.<init>(r4)     // Catch:{ all -> 0x0030 }
            java.lang.String r4 = " does not exist"
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ all -> 0x0030 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0030 }
            r2.<init>((javax.mail.Folder) r7, (java.lang.String) r3)     // Catch:{ all -> 0x0030 }
            throw r2     // Catch:{ all -> 0x0030 }
        L_0x0074:
            javax.mail.MessagingException r2 = new javax.mail.MessagingException     // Catch:{ all -> 0x0030 }
            java.lang.String r3 = r1.getMessage()     // Catch:{ all -> 0x0030 }
            r2.<init>(r3, r1)     // Catch:{ all -> 0x0030 }
            throw r2     // Catch:{ all -> 0x0030 }
        L_0x007e:
            monitor-exit(r0)     // Catch:{ all -> 0x0080 }
            throw r1     // Catch:{ all -> 0x0087 }
        L_0x0080:
            r1 = move-exception
            goto L_0x007e
        L_0x0082:
            super.copyMessages(r6, r7)     // Catch:{ all -> 0x0087 }
        L_0x0085:
            monitor-exit(r5)
            return
        L_0x0087:
            r6 = move-exception
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPFolder.copyMessages(javax.mail.Message[], javax.mail.Folder):void");
    }

    public synchronized Message[] expunge() throws MessagingException {
        return expunge((Message[]) null);
    }

    public synchronized Message[] expunge(Message[] msgs) throws MessagingException {
        Message[] rmsgs;
        checkOpened();
        Vector v = new Vector();
        if (msgs != null) {
            FetchProfile fp = new FetchProfile();
            fp.add((FetchProfile.Item) UIDFolder.FetchProfileItem.UID);
            fetch(msgs, fp);
        }
        synchronized (this.messageCacheLock) {
            this.doExpungeNotification = false;
            try {
                IMAPProtocol p = getProtocol();
                if (msgs != null) {
                    p.uidexpunge(Utility.toUIDSet(msgs));
                } else {
                    p.expunge();
                }
                this.doExpungeNotification = true;
                int i = 0;
                while (i < this.messageCache.size()) {
                    IMAPMessage m = (IMAPMessage) this.messageCache.elementAt(i);
                    if (m.isExpunged()) {
                        v.addElement(m);
                        this.messageCache.removeElementAt(i);
                        if (this.uidTable != null) {
                            long uid = m.getUID();
                            if (uid != -1) {
                                this.uidTable.remove(new Long(uid));
                            }
                        }
                    } else {
                        m.setMessageNumber(m.getSequenceNumber());
                        i++;
                    }
                }
            } catch (CommandFailedException cfx) {
                if (this.mode != 2) {
                    throw new IllegalStateException("Cannot expunge READ_ONLY folder: " + this.fullName);
                }
                throw new MessagingException(cfx.getMessage(), cfx);
            } catch (ConnectionException cex) {
                throw new FolderClosedException(this, cex.getMessage());
            } catch (ProtocolException pex) {
                throw new MessagingException(pex.getMessage(), pex);
            } catch (Throwable th) {
                this.doExpungeNotification = true;
                throw th;
            }
        }
        this.total = this.messageCache.size();
        rmsgs = new Message[v.size()];
        v.copyInto(rmsgs);
        if (rmsgs.length > 0) {
            notifyMessageRemovedListeners(true, rmsgs);
        }
        return rmsgs;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x002f, code lost:
        r2 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized javax.mail.Message[] search(javax.mail.search.SearchTerm r6) throws javax.mail.MessagingException {
        /*
            r5 = this;
            monitor-enter(r5)
            r5.checkOpened()     // Catch:{ all -> 0x0055 }
            r0 = 0
            r1 = r0
            javax.mail.Message[] r1 = (javax.mail.Message[]) r1     // Catch:{ CommandFailedException -> 0x004e, SearchException -> 0x0047, ConnectionException -> 0x003c, ProtocolException -> 0x0031 }
            java.lang.Object r1 = r5.messageCacheLock     // Catch:{ CommandFailedException -> 0x004e, SearchException -> 0x0047, ConnectionException -> 0x003c, ProtocolException -> 0x0031 }
            monitor-enter(r1)     // Catch:{ CommandFailedException -> 0x004e, SearchException -> 0x0047, ConnectionException -> 0x003c, ProtocolException -> 0x0031 }
            com.sun.mail.imap.protocol.IMAPProtocol r2 = r5.getProtocol()     // Catch:{ all -> 0x002c }
            int[] r2 = r2.search(r6)     // Catch:{ all -> 0x002c }
            if (r2 == 0) goto L_0x0029
            int r3 = r2.length     // Catch:{ all -> 0x002c }
            com.sun.mail.imap.IMAPMessage[] r3 = new com.sun.mail.imap.IMAPMessage[r3]     // Catch:{ all -> 0x002c }
            r0 = r3
            r3 = 0
        L_0x001a:
            int r4 = r2.length     // Catch:{ all -> 0x002c }
            if (r3 < r4) goto L_0x001e
            goto L_0x0029
        L_0x001e:
            r4 = r2[r3]     // Catch:{ all -> 0x002c }
            com.sun.mail.imap.IMAPMessage r4 = r5.getMessageBySeqNumber(r4)     // Catch:{ all -> 0x002c }
            r0[r3] = r4     // Catch:{ all -> 0x002c }
            int r3 = r3 + 1
            goto L_0x001a
        L_0x0029:
            monitor-exit(r1)     // Catch:{ all -> 0x002c }
            monitor-exit(r5)
            return r0
        L_0x002c:
            r2 = move-exception
        L_0x002d:
            monitor-exit(r1)     // Catch:{ all -> 0x002f }
            throw r2     // Catch:{ CommandFailedException -> 0x004e, SearchException -> 0x0047, ConnectionException -> 0x003c, ProtocolException -> 0x0031 }
        L_0x002f:
            r2 = move-exception
            goto L_0x002d
        L_0x0031:
            r0 = move-exception
            javax.mail.MessagingException r1 = new javax.mail.MessagingException     // Catch:{ all -> 0x0055 }
            java.lang.String r2 = r0.getMessage()     // Catch:{ all -> 0x0055 }
            r1.<init>(r2, r0)     // Catch:{ all -> 0x0055 }
            throw r1     // Catch:{ all -> 0x0055 }
        L_0x003c:
            r0 = move-exception
            javax.mail.FolderClosedException r1 = new javax.mail.FolderClosedException     // Catch:{ all -> 0x0055 }
            java.lang.String r2 = r0.getMessage()     // Catch:{ all -> 0x0055 }
            r1.<init>(r5, r2)     // Catch:{ all -> 0x0055 }
            throw r1     // Catch:{ all -> 0x0055 }
        L_0x0047:
            r0 = move-exception
            javax.mail.Message[] r1 = super.search(r6)     // Catch:{ all -> 0x0055 }
            monitor-exit(r5)
            return r1
        L_0x004e:
            r0 = move-exception
            javax.mail.Message[] r1 = super.search(r6)     // Catch:{ all -> 0x0055 }
            monitor-exit(r5)
            return r1
        L_0x0055:
            r6 = move-exception
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPFolder.search(javax.mail.search.SearchTerm):javax.mail.Message[]");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0043, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized javax.mail.Message[] search(javax.mail.search.SearchTerm r8, javax.mail.Message[] r9) throws javax.mail.MessagingException {
        /*
            r7 = this;
            monitor-enter(r7)
            r7.checkOpened()     // Catch:{ all -> 0x0069 }
            int r0 = r9.length     // Catch:{ all -> 0x0069 }
            if (r0 != 0) goto L_0x0009
            monitor-exit(r7)
            return r9
        L_0x0009:
            r0 = 0
            r1 = r0
            javax.mail.Message[] r1 = (javax.mail.Message[]) r1     // Catch:{ CommandFailedException -> 0x0062, SearchException -> 0x005b, ConnectionException -> 0x0050, ProtocolException -> 0x0045 }
            r1 = r0
            java.lang.Object r2 = r7.messageCacheLock     // Catch:{ CommandFailedException -> 0x0062, SearchException -> 0x005b, ConnectionException -> 0x0050, ProtocolException -> 0x0045 }
            monitor-enter(r2)     // Catch:{ CommandFailedException -> 0x0062, SearchException -> 0x005b, ConnectionException -> 0x0050, ProtocolException -> 0x0045 }
            com.sun.mail.imap.protocol.IMAPProtocol r3 = r7.getProtocol()     // Catch:{ all -> 0x0040 }
            com.sun.mail.imap.protocol.MessageSet[] r0 = com.sun.mail.imap.Utility.toMessageSet(r9, r0)     // Catch:{ all -> 0x0040 }
            if (r0 == 0) goto L_0x0038
            int[] r4 = r3.search((com.sun.mail.imap.protocol.MessageSet[]) r0, (javax.mail.search.SearchTerm) r8)     // Catch:{ all -> 0x0040 }
            if (r4 == 0) goto L_0x0035
            int r5 = r4.length     // Catch:{ all -> 0x0040 }
            com.sun.mail.imap.IMAPMessage[] r5 = new com.sun.mail.imap.IMAPMessage[r5]     // Catch:{ all -> 0x0040 }
            r1 = r5
            r5 = 0
        L_0x0026:
            int r6 = r4.length     // Catch:{ all -> 0x0040 }
            if (r5 < r6) goto L_0x002a
            goto L_0x0035
        L_0x002a:
            r6 = r4[r5]     // Catch:{ all -> 0x0040 }
            com.sun.mail.imap.IMAPMessage r6 = r7.getMessageBySeqNumber(r6)     // Catch:{ all -> 0x0040 }
            r1[r5] = r6     // Catch:{ all -> 0x0040 }
            int r5 = r5 + 1
            goto L_0x0026
        L_0x0035:
            monitor-exit(r2)     // Catch:{ all -> 0x0040 }
            monitor-exit(r7)
            return r1
        L_0x0038:
            javax.mail.MessageRemovedException r4 = new javax.mail.MessageRemovedException     // Catch:{ all -> 0x0040 }
            java.lang.String r5 = "Messages have been removed"
            r4.<init>(r5)     // Catch:{ all -> 0x0040 }
            throw r4     // Catch:{ all -> 0x0040 }
        L_0x0040:
            r0 = move-exception
        L_0x0041:
            monitor-exit(r2)     // Catch:{ all -> 0x0043 }
            throw r0     // Catch:{ CommandFailedException -> 0x0062, SearchException -> 0x005b, ConnectionException -> 0x0050, ProtocolException -> 0x0045 }
        L_0x0043:
            r0 = move-exception
            goto L_0x0041
        L_0x0045:
            r0 = move-exception
            javax.mail.MessagingException r1 = new javax.mail.MessagingException     // Catch:{ all -> 0x0069 }
            java.lang.String r2 = r0.getMessage()     // Catch:{ all -> 0x0069 }
            r1.<init>(r2, r0)     // Catch:{ all -> 0x0069 }
            throw r1     // Catch:{ all -> 0x0069 }
        L_0x0050:
            r0 = move-exception
            javax.mail.FolderClosedException r1 = new javax.mail.FolderClosedException     // Catch:{ all -> 0x0069 }
            java.lang.String r2 = r0.getMessage()     // Catch:{ all -> 0x0069 }
            r1.<init>(r7, r2)     // Catch:{ all -> 0x0069 }
            throw r1     // Catch:{ all -> 0x0069 }
        L_0x005b:
            r0 = move-exception
            javax.mail.Message[] r1 = super.search(r8, r9)     // Catch:{ all -> 0x0069 }
            monitor-exit(r7)
            return r1
        L_0x0062:
            r0 = move-exception
            javax.mail.Message[] r1 = super.search(r8, r9)     // Catch:{ all -> 0x0069 }
            monitor-exit(r7)
            return r1
        L_0x0069:
            r8 = move-exception
            monitor-exit(r7)
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPFolder.search(javax.mail.search.SearchTerm, javax.mail.Message[]):javax.mail.Message[]");
    }

    public synchronized long getUIDValidity() throws MessagingException {
        if (this.opened) {
            return this.uidvalidity;
        }
        Status status = null;
        try {
            IMAPProtocol p = getStoreProtocol();
            status = p.status(this.fullName, new String[]{"UIDVALIDITY"});
            releaseStoreProtocol(p);
        } catch (BadCommandException bex) {
            throw new MessagingException("Cannot obtain UIDValidity", bex);
        } catch (ConnectionException cex) {
            throwClosedException(cex);
            releaseStoreProtocol((IMAPProtocol) null);
        } catch (ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        } catch (Throwable th) {
            releaseStoreProtocol((IMAPProtocol) null);
            throw th;
        }
        return status.uidvalidity;
    }

    public synchronized long getUIDNext() throws MessagingException {
        if (this.opened) {
            return this.uidnext;
        }
        Status status = null;
        try {
            IMAPProtocol p = getStoreProtocol();
            status = p.status(this.fullName, new String[]{"UIDNEXT"});
            releaseStoreProtocol(p);
        } catch (BadCommandException bex) {
            throw new MessagingException("Cannot obtain UIDNext", bex);
        } catch (ConnectionException cex) {
            throwClosedException(cex);
            releaseStoreProtocol((IMAPProtocol) null);
        } catch (ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        } catch (Throwable th) {
            releaseStoreProtocol((IMAPProtocol) null);
            throw th;
        }
        return status.uidnext;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0047, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x004b, code lost:
        r2 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized javax.mail.Message getMessageByUID(long r7) throws javax.mail.MessagingException {
        /*
            r6 = this;
            monitor-enter(r6)
            r6.checkOpened()     // Catch:{ all -> 0x0063 }
            r0 = 0
            java.lang.Object r1 = r6.messageCacheLock     // Catch:{ ConnectionException -> 0x0058, ProtocolException -> 0x004d }
            monitor-enter(r1)     // Catch:{ ConnectionException -> 0x0058, ProtocolException -> 0x004d }
            java.lang.Long r2 = new java.lang.Long     // Catch:{ all -> 0x0048 }
            r2.<init>(r7)     // Catch:{ all -> 0x0048 }
            java.util.Hashtable r3 = r6.uidTable     // Catch:{ all -> 0x0048 }
            if (r3 == 0) goto L_0x001d
            java.lang.Object r3 = r3.get(r2)     // Catch:{ all -> 0x0048 }
            com.sun.mail.imap.IMAPMessage r3 = (com.sun.mail.imap.IMAPMessage) r3     // Catch:{ all -> 0x0048 }
            r0 = r3
            if (r0 == 0) goto L_0x0024
            monitor-exit(r1)     // Catch:{ all -> 0x0048 }
            monitor-exit(r6)
            return r0
        L_0x001d:
            java.util.Hashtable r3 = new java.util.Hashtable     // Catch:{ all -> 0x0048 }
            r3.<init>()     // Catch:{ all -> 0x0048 }
            r6.uidTable = r3     // Catch:{ all -> 0x0048 }
        L_0x0024:
            com.sun.mail.imap.protocol.IMAPProtocol r3 = r6.getProtocol()     // Catch:{ all -> 0x0048 }
            com.sun.mail.imap.protocol.UID r3 = r3.fetchSequenceNumber(r7)     // Catch:{ all -> 0x0048 }
            if (r3 == 0) goto L_0x0045
            int r4 = r3.seqnum     // Catch:{ all -> 0x0048 }
            int r5 = r6.total     // Catch:{ all -> 0x0048 }
            if (r4 > r5) goto L_0x0045
            int r4 = r3.seqnum     // Catch:{ all -> 0x0048 }
            com.sun.mail.imap.IMAPMessage r4 = r6.getMessageBySeqNumber(r4)     // Catch:{ all -> 0x0048 }
            r0 = r4
            long r4 = r3.uid     // Catch:{ all -> 0x0048 }
            r0.setUID(r4)     // Catch:{ all -> 0x0048 }
            java.util.Hashtable r4 = r6.uidTable     // Catch:{ all -> 0x0048 }
            r4.put(r2, r0)     // Catch:{ all -> 0x0048 }
        L_0x0045:
            monitor-exit(r1)     // Catch:{ all -> 0x0048 }
            monitor-exit(r6)
            return r0
        L_0x0048:
            r2 = move-exception
        L_0x0049:
            monitor-exit(r1)     // Catch:{ all -> 0x004b }
            throw r2     // Catch:{ ConnectionException -> 0x0058, ProtocolException -> 0x004d }
        L_0x004b:
            r2 = move-exception
            goto L_0x0049
        L_0x004d:
            r1 = move-exception
            javax.mail.MessagingException r2 = new javax.mail.MessagingException     // Catch:{ all -> 0x0063 }
            java.lang.String r3 = r1.getMessage()     // Catch:{ all -> 0x0063 }
            r2.<init>(r3, r1)     // Catch:{ all -> 0x0063 }
            throw r2     // Catch:{ all -> 0x0063 }
        L_0x0058:
            r1 = move-exception
            javax.mail.FolderClosedException r2 = new javax.mail.FolderClosedException     // Catch:{ all -> 0x0063 }
            java.lang.String r3 = r1.getMessage()     // Catch:{ all -> 0x0063 }
            r2.<init>(r6, r3)     // Catch:{ all -> 0x0063 }
            throw r2     // Catch:{ all -> 0x0063 }
        L_0x0063:
            r7 = move-exception
            monitor-exit(r6)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPFolder.getMessageByUID(long):javax.mail.Message");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0049, code lost:
        r1 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized javax.mail.Message[] getMessagesByUID(long r10, long r12) throws javax.mail.MessagingException {
        /*
            r9 = this;
            monitor-enter(r9)
            r9.checkOpened()     // Catch:{ all -> 0x0061 }
            java.lang.Object r0 = r9.messageCacheLock     // Catch:{ ConnectionException -> 0x0056, ProtocolException -> 0x004b }
            monitor-enter(r0)     // Catch:{ ConnectionException -> 0x0056, ProtocolException -> 0x004b }
            java.util.Hashtable r1 = r9.uidTable     // Catch:{ all -> 0x0046 }
            if (r1 != 0) goto L_0x0012
            java.util.Hashtable r1 = new java.util.Hashtable     // Catch:{ all -> 0x0046 }
            r1.<init>()     // Catch:{ all -> 0x0046 }
            r9.uidTable = r1     // Catch:{ all -> 0x0046 }
        L_0x0012:
            com.sun.mail.imap.protocol.IMAPProtocol r1 = r9.getProtocol()     // Catch:{ all -> 0x0046 }
            com.sun.mail.imap.protocol.UID[] r1 = r1.fetchSequenceNumbers(r10, r12)     // Catch:{ all -> 0x0046 }
            int r2 = r1.length     // Catch:{ all -> 0x0046 }
            javax.mail.Message[] r2 = new javax.mail.Message[r2]     // Catch:{ all -> 0x0046 }
            r3 = 0
        L_0x001e:
            int r4 = r1.length     // Catch:{ all -> 0x0046 }
            if (r3 < r4) goto L_0x0024
            monitor-exit(r0)     // Catch:{ all -> 0x0046 }
            monitor-exit(r9)
            return r2
        L_0x0024:
            r4 = r1[r3]     // Catch:{ all -> 0x0046 }
            int r4 = r4.seqnum     // Catch:{ all -> 0x0046 }
            com.sun.mail.imap.IMAPMessage r4 = r9.getMessageBySeqNumber(r4)     // Catch:{ all -> 0x0046 }
            r5 = r1[r3]     // Catch:{ all -> 0x0046 }
            long r5 = r5.uid     // Catch:{ all -> 0x0046 }
            r4.setUID(r5)     // Catch:{ all -> 0x0046 }
            r2[r3] = r4     // Catch:{ all -> 0x0046 }
            java.util.Hashtable r5 = r9.uidTable     // Catch:{ all -> 0x0046 }
            java.lang.Long r6 = new java.lang.Long     // Catch:{ all -> 0x0046 }
            r7 = r1[r3]     // Catch:{ all -> 0x0046 }
            long r7 = r7.uid     // Catch:{ all -> 0x0046 }
            r6.<init>(r7)     // Catch:{ all -> 0x0046 }
            r5.put(r6, r4)     // Catch:{ all -> 0x0046 }
            int r3 = r3 + 1
            goto L_0x001e
        L_0x0046:
            r1 = move-exception
        L_0x0047:
            monitor-exit(r0)     // Catch:{ all -> 0x0049 }
            throw r1     // Catch:{ ConnectionException -> 0x0056, ProtocolException -> 0x004b }
        L_0x0049:
            r1 = move-exception
            goto L_0x0047
        L_0x004b:
            r0 = move-exception
            javax.mail.MessagingException r1 = new javax.mail.MessagingException     // Catch:{ all -> 0x0061 }
            java.lang.String r2 = r0.getMessage()     // Catch:{ all -> 0x0061 }
            r1.<init>(r2, r0)     // Catch:{ all -> 0x0061 }
            throw r1     // Catch:{ all -> 0x0061 }
        L_0x0056:
            r0 = move-exception
            javax.mail.FolderClosedException r1 = new javax.mail.FolderClosedException     // Catch:{ all -> 0x0061 }
            java.lang.String r2 = r0.getMessage()     // Catch:{ all -> 0x0061 }
            r1.<init>(r9, r2)     // Catch:{ all -> 0x0061 }
            throw r1     // Catch:{ all -> 0x0061 }
        L_0x0061:
            r10 = move-exception
            monitor-exit(r9)
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPFolder.getMessagesByUID(long, long):javax.mail.Message[]");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:43:0x009d, code lost:
        r1 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized javax.mail.Message[] getMessagesByUID(long[] r10) throws javax.mail.MessagingException {
        /*
            r9 = this;
            monitor-enter(r9)
            r9.checkOpened()     // Catch:{ all -> 0x00b5 }
            java.lang.Object r0 = r9.messageCacheLock     // Catch:{ ConnectionException -> 0x00aa, ProtocolException -> 0x009f }
            monitor-enter(r0)     // Catch:{ ConnectionException -> 0x00aa, ProtocolException -> 0x009f }
            r1 = r10
            java.util.Hashtable r2 = r9.uidTable     // Catch:{ all -> 0x009a }
            if (r2 == 0) goto L_0x0045
            java.util.Vector r2 = new java.util.Vector     // Catch:{ all -> 0x009a }
            r2.<init>()     // Catch:{ all -> 0x009a }
            r3 = 0
        L_0x0012:
            int r4 = r10.length     // Catch:{ all -> 0x009a }
            if (r3 < r4) goto L_0x002f
            int r3 = r2.size()     // Catch:{ all -> 0x009a }
            long[] r4 = new long[r3]     // Catch:{ all -> 0x009a }
            r1 = r4
            r4 = 0
        L_0x001d:
            if (r4 < r3) goto L_0x0020
            goto L_0x004c
        L_0x0020:
            java.lang.Object r5 = r2.elementAt(r4)     // Catch:{ all -> 0x009a }
            java.lang.Long r5 = (java.lang.Long) r5     // Catch:{ all -> 0x009a }
            long r5 = r5.longValue()     // Catch:{ all -> 0x009a }
            r1[r4] = r5     // Catch:{ all -> 0x009a }
            int r4 = r4 + 1
            goto L_0x001d
        L_0x002f:
            java.util.Hashtable r4 = r9.uidTable     // Catch:{ all -> 0x009a }
            java.lang.Long r5 = new java.lang.Long     // Catch:{ all -> 0x009a }
            r6 = r10[r3]     // Catch:{ all -> 0x009a }
            r5.<init>(r6)     // Catch:{ all -> 0x009a }
            r6 = r5
            boolean r4 = r4.containsKey(r5)     // Catch:{ all -> 0x009a }
            if (r4 != 0) goto L_0x0042
            r2.addElement(r6)     // Catch:{ all -> 0x009a }
        L_0x0042:
            int r3 = r3 + 1
            goto L_0x0012
        L_0x0045:
            java.util.Hashtable r2 = new java.util.Hashtable     // Catch:{ all -> 0x009a }
            r2.<init>()     // Catch:{ all -> 0x009a }
            r9.uidTable = r2     // Catch:{ all -> 0x009a }
        L_0x004c:
            int r2 = r1.length     // Catch:{ all -> 0x009a }
            if (r2 <= 0) goto L_0x007c
            com.sun.mail.imap.protocol.IMAPProtocol r2 = r9.getProtocol()     // Catch:{ all -> 0x009a }
            com.sun.mail.imap.protocol.UID[] r2 = r2.fetchSequenceNumbers(r1)     // Catch:{ all -> 0x009a }
            r3 = 0
        L_0x0058:
            int r4 = r2.length     // Catch:{ all -> 0x009a }
            if (r3 < r4) goto L_0x005c
            goto L_0x007c
        L_0x005c:
            r4 = r2[r3]     // Catch:{ all -> 0x009a }
            int r4 = r4.seqnum     // Catch:{ all -> 0x009a }
            com.sun.mail.imap.IMAPMessage r4 = r9.getMessageBySeqNumber(r4)     // Catch:{ all -> 0x009a }
            r5 = r2[r3]     // Catch:{ all -> 0x009a }
            long r5 = r5.uid     // Catch:{ all -> 0x009a }
            r4.setUID(r5)     // Catch:{ all -> 0x009a }
            java.util.Hashtable r5 = r9.uidTable     // Catch:{ all -> 0x009a }
            java.lang.Long r6 = new java.lang.Long     // Catch:{ all -> 0x009a }
            r7 = r2[r3]     // Catch:{ all -> 0x009a }
            long r7 = r7.uid     // Catch:{ all -> 0x009a }
            r6.<init>(r7)     // Catch:{ all -> 0x009a }
            r5.put(r6, r4)     // Catch:{ all -> 0x009a }
            int r3 = r3 + 1
            goto L_0x0058
        L_0x007c:
            int r2 = r10.length     // Catch:{ all -> 0x009a }
            javax.mail.Message[] r2 = new javax.mail.Message[r2]     // Catch:{ all -> 0x009a }
            r3 = 0
        L_0x0080:
            int r4 = r10.length     // Catch:{ all -> 0x009a }
            if (r3 < r4) goto L_0x0086
            monitor-exit(r0)     // Catch:{ all -> 0x009a }
            monitor-exit(r9)
            return r2
        L_0x0086:
            java.util.Hashtable r4 = r9.uidTable     // Catch:{ all -> 0x009a }
            java.lang.Long r5 = new java.lang.Long     // Catch:{ all -> 0x009a }
            r6 = r10[r3]     // Catch:{ all -> 0x009a }
            r5.<init>(r6)     // Catch:{ all -> 0x009a }
            java.lang.Object r4 = r4.get(r5)     // Catch:{ all -> 0x009a }
            javax.mail.Message r4 = (javax.mail.Message) r4     // Catch:{ all -> 0x009a }
            r2[r3] = r4     // Catch:{ all -> 0x009a }
            int r3 = r3 + 1
            goto L_0x0080
        L_0x009a:
            r1 = move-exception
        L_0x009b:
            monitor-exit(r0)     // Catch:{ all -> 0x009d }
            throw r1     // Catch:{ ConnectionException -> 0x00aa, ProtocolException -> 0x009f }
        L_0x009d:
            r1 = move-exception
            goto L_0x009b
        L_0x009f:
            r0 = move-exception
            javax.mail.MessagingException r1 = new javax.mail.MessagingException     // Catch:{ all -> 0x00b5 }
            java.lang.String r2 = r0.getMessage()     // Catch:{ all -> 0x00b5 }
            r1.<init>(r2, r0)     // Catch:{ all -> 0x00b5 }
            throw r1     // Catch:{ all -> 0x00b5 }
        L_0x00aa:
            r0 = move-exception
            javax.mail.FolderClosedException r1 = new javax.mail.FolderClosedException     // Catch:{ all -> 0x00b5 }
            java.lang.String r2 = r0.getMessage()     // Catch:{ all -> 0x00b5 }
            r1.<init>(r9, r2)     // Catch:{ all -> 0x00b5 }
            throw r1     // Catch:{ all -> 0x00b5 }
        L_0x00b5:
            r10 = move-exception
            monitor-exit(r9)
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPFolder.getMessagesByUID(long[]):javax.mail.Message[]");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0066, code lost:
        r2 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized long getUID(javax.mail.Message r9) throws javax.mail.MessagingException {
        /*
            r8 = this;
            monitor-enter(r8)
            javax.mail.Folder r0 = r9.getFolder()     // Catch:{ all -> 0x0070 }
            if (r0 != r8) goto L_0x0068
            r8.checkOpened()     // Catch:{ all -> 0x0070 }
            r0 = r9
            com.sun.mail.imap.IMAPMessage r0 = (com.sun.mail.imap.IMAPMessage) r0     // Catch:{ all -> 0x0070 }
            long r1 = r0.getUID()     // Catch:{ all -> 0x0070 }
            r3 = r1
            r5 = -1
            int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r1 == 0) goto L_0x001a
            monitor-exit(r8)
            return r3
        L_0x001a:
            java.lang.Object r1 = r8.messageCacheLock     // Catch:{ all -> 0x0070 }
            monitor-enter(r1)     // Catch:{ all -> 0x0070 }
            com.sun.mail.imap.protocol.IMAPProtocol r2 = r8.getProtocol()     // Catch:{ ConnectionException -> 0x0059, ProtocolException -> 0x004e }
            r0.checkExpunged()     // Catch:{ ConnectionException -> 0x0059, ProtocolException -> 0x004e }
            int r5 = r0.getSequenceNumber()     // Catch:{ ConnectionException -> 0x0059, ProtocolException -> 0x004e }
            com.sun.mail.imap.protocol.UID r5 = r2.fetchUID(r5)     // Catch:{ ConnectionException -> 0x0059, ProtocolException -> 0x004e }
            if (r5 == 0) goto L_0x0049
            long r6 = r5.uid     // Catch:{ ConnectionException -> 0x0059, ProtocolException -> 0x004e }
            r3 = r6
            r0.setUID(r3)     // Catch:{ ConnectionException -> 0x0059, ProtocolException -> 0x004e }
            java.util.Hashtable r6 = r8.uidTable     // Catch:{ ConnectionException -> 0x0059, ProtocolException -> 0x004e }
            if (r6 != 0) goto L_0x003f
            java.util.Hashtable r6 = new java.util.Hashtable     // Catch:{ ConnectionException -> 0x0059, ProtocolException -> 0x004e }
            r6.<init>()     // Catch:{ ConnectionException -> 0x0059, ProtocolException -> 0x004e }
            r8.uidTable = r6     // Catch:{ ConnectionException -> 0x0059, ProtocolException -> 0x004e }
        L_0x003f:
            java.util.Hashtable r6 = r8.uidTable     // Catch:{ ConnectionException -> 0x0059, ProtocolException -> 0x004e }
            java.lang.Long r7 = new java.lang.Long     // Catch:{ ConnectionException -> 0x0059, ProtocolException -> 0x004e }
            r7.<init>(r3)     // Catch:{ ConnectionException -> 0x0059, ProtocolException -> 0x004e }
            r6.put(r7, r0)     // Catch:{ ConnectionException -> 0x0059, ProtocolException -> 0x004e }
        L_0x0049:
            monitor-exit(r1)     // Catch:{ all -> 0x004c }
            monitor-exit(r8)
            return r3
        L_0x004c:
            r2 = move-exception
            goto L_0x0064
        L_0x004e:
            r2 = move-exception
            javax.mail.MessagingException r5 = new javax.mail.MessagingException     // Catch:{ all -> 0x004c }
            java.lang.String r6 = r2.getMessage()     // Catch:{ all -> 0x004c }
            r5.<init>(r6, r2)     // Catch:{ all -> 0x004c }
            throw r5     // Catch:{ all -> 0x004c }
        L_0x0059:
            r2 = move-exception
            javax.mail.FolderClosedException r5 = new javax.mail.FolderClosedException     // Catch:{ all -> 0x004c }
            java.lang.String r6 = r2.getMessage()     // Catch:{ all -> 0x004c }
            r5.<init>(r8, r6)     // Catch:{ all -> 0x004c }
            throw r5     // Catch:{ all -> 0x004c }
        L_0x0064:
            monitor-exit(r1)     // Catch:{ all -> 0x0066 }
            throw r2     // Catch:{ all -> 0x0070 }
        L_0x0066:
            r2 = move-exception
            goto L_0x0064
        L_0x0068:
            java.util.NoSuchElementException r0 = new java.util.NoSuchElementException     // Catch:{ all -> 0x0070 }
            java.lang.String r1 = "Message does not belong to this folder"
            r0.<init>(r1)     // Catch:{ all -> 0x0070 }
            throw r0     // Catch:{ all -> 0x0070 }
        L_0x0070:
            r9 = move-exception
            monitor-exit(r8)
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPFolder.getUID(javax.mail.Message):long");
    }

    public Quota[] getQuota() throws MessagingException {
        return (Quota[]) doOptionalCommand("QUOTA not supported", new ProtocolCommand() {
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                return p.getQuotaRoot(IMAPFolder.this.fullName);
            }
        });
    }

    public void setQuota(final Quota quota) throws MessagingException {
        doOptionalCommand("QUOTA not supported", new ProtocolCommand() {
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                p.setQuota(quota);
                return null;
            }
        });
    }

    public ACL[] getACL() throws MessagingException {
        return (ACL[]) doOptionalCommand("ACL not supported", new ProtocolCommand() {
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                return p.getACL(IMAPFolder.this.fullName);
            }
        });
    }

    public void addACL(ACL acl) throws MessagingException {
        setACL(acl, 0);
    }

    public void removeACL(final String name2) throws MessagingException {
        doOptionalCommand("ACL not supported", new ProtocolCommand() {
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                p.deleteACL(IMAPFolder.this.fullName, name2);
                return null;
            }
        });
    }

    public void addRights(ACL acl) throws MessagingException {
        setACL(acl, '+');
    }

    public void removeRights(ACL acl) throws MessagingException {
        setACL(acl, '-');
    }

    public Rights[] listRights(final String name2) throws MessagingException {
        return (Rights[]) doOptionalCommand("ACL not supported", new ProtocolCommand() {
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                return p.listRights(IMAPFolder.this.fullName, name2);
            }
        });
    }

    public Rights myRights() throws MessagingException {
        return (Rights) doOptionalCommand("ACL not supported", new ProtocolCommand() {
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                return p.myRights(IMAPFolder.this.fullName);
            }
        });
    }

    private void setACL(final ACL acl, final char mod) throws MessagingException {
        doOptionalCommand("ACL not supported", new ProtocolCommand() {
            public Object doCommand(IMAPProtocol p) throws ProtocolException {
                p.setACL(IMAPFolder.this.fullName, mod, acl);
                return null;
            }
        });
    }

    public String[] getAttributes() throws MessagingException {
        if (this.attributes == null) {
            exists();
        }
        return (String[]) this.attributes.clone();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r1 = r4.messageCacheLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0028, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0029, code lost:
        if (r0 == null) goto L_0x0038;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        r2 = r4.protocol;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x002d, code lost:
        if (r2 == null) goto L_0x0038;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0033, code lost:
        if (r2.processIdleResponse(r0) != false) goto L_0x0036;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0036, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0038, code lost:
        r4.idleState = 0;
        r4.messageCacheLock.notifyAll();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0040, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0041, code lost:
        r0 = ((com.sun.mail.imap.IMAPStore) r4.store).getMinIdleTime();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0049, code lost:
        if (r0 <= 0) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:?, code lost:
        java.lang.Thread.sleep((long) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0055, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x005f, code lost:
        throw new javax.mail.MessagingException(r1.getMessage(), r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0060, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0061, code lost:
        throwClosedException(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0020, code lost:
        r0 = r4.protocol.readIdleResponse();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void idle() throws javax.mail.MessagingException {
        /*
            r4 = this;
            boolean r0 = java.lang.Thread.holdsLock(r4)
            if (r0 != 0) goto L_0x0068
            monitor-enter(r4)
            r4.checkOpened()     // Catch:{ all -> 0x0065 }
            java.lang.String r0 = "IDLE not supported"
            com.sun.mail.imap.IMAPFolder$19 r1 = new com.sun.mail.imap.IMAPFolder$19     // Catch:{ all -> 0x0065 }
            r1.<init>()     // Catch:{ all -> 0x0065 }
            java.lang.Object r0 = r4.doOptionalCommand(r0, r1)     // Catch:{ all -> 0x0065 }
            java.lang.Boolean r0 = (java.lang.Boolean) r0     // Catch:{ all -> 0x0065 }
            boolean r1 = r0.booleanValue()     // Catch:{ all -> 0x0065 }
            if (r1 != 0) goto L_0x001f
            monitor-exit(r4)     // Catch:{ all -> 0x0065 }
            return
        L_0x001f:
            monitor-exit(r4)     // Catch:{ all -> 0x0065 }
        L_0x0020:
            com.sun.mail.imap.protocol.IMAPProtocol r0 = r4.protocol
            com.sun.mail.iap.Response r0 = r0.readIdleResponse()
            java.lang.Object r1 = r4.messageCacheLock     // Catch:{ ConnectionException -> 0x0060, ProtocolException -> 0x0055 }
            monitor-enter(r1)     // Catch:{ ConnectionException -> 0x0060, ProtocolException -> 0x0055 }
            if (r0 == 0) goto L_0x0038
            com.sun.mail.imap.protocol.IMAPProtocol r2 = r4.protocol     // Catch:{ all -> 0x0052 }
            if (r2 == 0) goto L_0x0038
            boolean r2 = r2.processIdleResponse(r0)     // Catch:{ all -> 0x0052 }
            if (r2 != 0) goto L_0x0036
            goto L_0x0038
        L_0x0036:
            monitor-exit(r1)     // Catch:{ all -> 0x0052 }
            goto L_0x0020
        L_0x0038:
            r2 = 0
            r4.idleState = r2     // Catch:{ all -> 0x0052 }
            java.lang.Object r2 = r4.messageCacheLock     // Catch:{ all -> 0x0052 }
            r2.notifyAll()     // Catch:{ all -> 0x0052 }
            monitor-exit(r1)     // Catch:{ all -> 0x0052 }
            javax.mail.Store r0 = r4.store
            com.sun.mail.imap.IMAPStore r0 = (com.sun.mail.imap.IMAPStore) r0
            int r0 = r0.getMinIdleTime()
            if (r0 <= 0) goto L_0x0051
            long r1 = (long) r0
            java.lang.Thread.sleep(r1)     // Catch:{ InterruptedException -> 0x0050 }
            goto L_0x0051
        L_0x0050:
            r1 = move-exception
        L_0x0051:
            return
        L_0x0052:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0052 }
            throw r2     // Catch:{ ConnectionException -> 0x0060, ProtocolException -> 0x0055 }
        L_0x0055:
            r1 = move-exception
            javax.mail.MessagingException r2 = new javax.mail.MessagingException
            java.lang.String r3 = r1.getMessage()
            r2.<init>(r3, r1)
            throw r2
        L_0x0060:
            r1 = move-exception
            r4.throwClosedException(r1)
            goto L_0x0020
        L_0x0065:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0065 }
            throw r0
        L_0x0068:
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPFolder.idle():void");
    }

    /* access modifiers changed from: package-private */
    public void waitIfIdle() throws ProtocolException {
        if (Thread.holdsLock(this.messageCacheLock)) {
            while (true) {
                int i = this.idleState;
                if (i != 0) {
                    if (i == 1) {
                        this.protocol.idleAbort();
                        this.idleState = 2;
                    }
                    try {
                        this.messageCacheLock.wait();
                    } catch (InterruptedException e) {
                    }
                } else {
                    return;
                }
            }
        } else {
            throw new AssertionError();
        }
    }

    public void handleResponse(Response r) {
        IMAPMessage msg;
        if (Thread.holdsLock(this.messageCacheLock)) {
            if (r.isOK() || r.isNO() || r.isBAD() || r.isBYE()) {
                ((IMAPStore) this.store).handleResponseCode(r);
            }
            if (r.isBYE()) {
                if (this.opened) {
                    cleanup(false);
                }
            } else if (r.isOK() || !r.isUnTagged()) {
            } else {
                if (!(r instanceof IMAPResponse)) {
                    this.out.println("UNEXPECTED RESPONSE : " + r.toString());
                    this.out.println("CONTACT javamail@sun.com");
                    return;
                }
                IMAPResponse ir = (IMAPResponse) r;
                if (ir.keyEquals("EXISTS")) {
                    int exists2 = ir.getNumber();
                    int i = this.realTotal;
                    if (exists2 > i) {
                        int count = exists2 - i;
                        Message[] msgs = new Message[count];
                        for (int i2 = 0; i2 < count; i2++) {
                            int i3 = this.total + 1;
                            this.total = i3;
                            int i4 = this.realTotal + 1;
                            this.realTotal = i4;
                            IMAPMessage msg2 = new IMAPMessage(this, i3, i4);
                            msgs[i2] = msg2;
                            this.messageCache.addElement(msg2);
                        }
                        notifyMessageAddedListeners(msgs);
                    }
                } else if (ir.keyEquals("EXPUNGE")) {
                    IMAPMessage msg3 = getMessageBySeqNumber(ir.getNumber());
                    msg3.setExpunged(true);
                    for (int i5 = msg3.getMessageNumber(); i5 < this.total; i5++) {
                        IMAPMessage m = (IMAPMessage) this.messageCache.elementAt(i5);
                        if (!m.isExpunged()) {
                            m.setSequenceNumber(m.getSequenceNumber() - 1);
                        }
                    }
                    this.realTotal--;
                    if (this.doExpungeNotification) {
                        notifyMessageRemovedListeners(false, new Message[]{msg3});
                    }
                } else if (ir.keyEquals("FETCH")) {
                    if (ir instanceof FetchResponse) {
                        FetchResponse f = (FetchResponse) ir;
                        Flags flags = (Flags) f.getItem(Flags.class);
                        if (flags != null && (msg = getMessageBySeqNumber(f.getNumber())) != null) {
                            msg._setFlags(flags);
                            notifyMessageChangedListeners(1, msg);
                            return;
                        }
                        return;
                    }
                    throw new AssertionError("!ir instanceof FetchResponse");
                } else if (ir.keyEquals("RECENT")) {
                    this.recent = ir.getNumber();
                }
            }
        } else {
            throw new AssertionError();
        }
    }

    /* access modifiers changed from: package-private */
    public void handleResponses(Response[] r) {
        for (int i = 0; i < r.length; i++) {
            if (r[i] != null) {
                handleResponse(r[i]);
            }
        }
    }

    /* access modifiers changed from: protected */
    public synchronized IMAPProtocol getStoreProtocol() throws ProtocolException {
        if (this.connectionPoolDebug) {
            this.out.println("DEBUG: getStoreProtocol() - borrowing a connection");
        }
        return ((IMAPStore) this.store).getStoreProtocol();
    }

    private synchronized void throwClosedException(ConnectionException cex) throws FolderClosedException, StoreClosedException {
        if ((this.protocol == null || cex.getProtocol() != this.protocol) && (this.protocol != null || this.reallyClosed)) {
            throw new StoreClosedException(this.store, cex.getMessage());
        }
        throw new FolderClosedException(this, cex.getMessage());
    }

    private IMAPProtocol getProtocol() throws ProtocolException {
        if (Thread.holdsLock(this.messageCacheLock)) {
            waitIfIdle();
            return this.protocol;
        }
        throw new AssertionError();
    }

    public Object doCommand(ProtocolCommand cmd) throws MessagingException {
        try {
            return doProtocolCommand(cmd);
        } catch (ConnectionException cex) {
            throwClosedException(cex);
            return null;
        } catch (ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
    }

    public Object doOptionalCommand(String err, ProtocolCommand cmd) throws MessagingException {
        try {
            return doProtocolCommand(cmd);
        } catch (BadCommandException bex) {
            throw new MessagingException(err, bex);
        } catch (ConnectionException cex) {
            throwClosedException(cex);
            return null;
        } catch (ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
    }

    public Object doCommandIgnoreFailure(ProtocolCommand cmd) throws MessagingException {
        try {
            return doProtocolCommand(cmd);
        } catch (CommandFailedException e) {
            return null;
        } catch (ConnectionException cex) {
            throwClosedException(cex);
            return null;
        } catch (ProtocolException pex) {
            throw new MessagingException(pex.getMessage(), pex);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0021, code lost:
        r0 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        r0 = getStoreProtocol();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x002e, code lost:
        return r3.doCommand(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x002f, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0030, code lost:
        releaseStoreProtocol(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0033, code lost:
        throw r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object doProtocolCommand(com.sun.mail.imap.IMAPFolder.ProtocolCommand r3) throws com.sun.mail.iap.ProtocolException {
        /*
            r2 = this;
            monitor-enter(r2)
            boolean r0 = r2.opened     // Catch:{ all -> 0x0034 }
            if (r0 == 0) goto L_0x0020
            javax.mail.Store r0 = r2.store     // Catch:{ all -> 0x0034 }
            com.sun.mail.imap.IMAPStore r0 = (com.sun.mail.imap.IMAPStore) r0     // Catch:{ all -> 0x0034 }
            boolean r0 = r0.hasSeparateStoreConnection()     // Catch:{ all -> 0x0034 }
            if (r0 != 0) goto L_0x0020
            java.lang.Object r0 = r2.messageCacheLock     // Catch:{ all -> 0x0034 }
            monitor-enter(r0)     // Catch:{ all -> 0x0034 }
            com.sun.mail.imap.protocol.IMAPProtocol r1 = r2.getProtocol()     // Catch:{ all -> 0x001d }
            java.lang.Object r1 = r3.doCommand(r1)     // Catch:{ all -> 0x001d }
            monitor-exit(r0)     // Catch:{ all -> 0x001d }
            monitor-exit(r2)     // Catch:{ all -> 0x0034 }
            return r1
        L_0x001d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x001d }
            throw r1     // Catch:{ all -> 0x0034 }
        L_0x0020:
            monitor-exit(r2)     // Catch:{ all -> 0x0034 }
            r0 = 0
            com.sun.mail.imap.protocol.IMAPProtocol r1 = r2.getStoreProtocol()     // Catch:{ all -> 0x002f }
            r0 = r1
            java.lang.Object r1 = r3.doCommand(r0)     // Catch:{ all -> 0x002f }
            r2.releaseStoreProtocol(r0)
            return r1
        L_0x002f:
            r1 = move-exception
            r2.releaseStoreProtocol(r0)
            throw r1
        L_0x0034:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0034 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sun.mail.imap.IMAPFolder.doProtocolCommand(com.sun.mail.imap.IMAPFolder$ProtocolCommand):java.lang.Object");
    }

    /* access modifiers changed from: protected */
    public synchronized void releaseStoreProtocol(IMAPProtocol p) {
        if (p != this.protocol) {
            ((IMAPStore) this.store).releaseStoreProtocol(p);
        }
    }

    private void releaseProtocol(boolean returnToPool) {
        IMAPProtocol iMAPProtocol = this.protocol;
        if (iMAPProtocol != null) {
            iMAPProtocol.removeResponseHandler(this);
            if (returnToPool) {
                ((IMAPStore) this.store).releaseProtocol(this, this.protocol);
            } else {
                ((IMAPStore) this.store).releaseProtocol(this, (IMAPProtocol) null);
            }
        }
    }

    private void keepConnectionAlive(boolean keepStoreAlive) throws ProtocolException {
        if (System.currentTimeMillis() - this.protocol.getTimestamp() > 1000) {
            waitIfIdle();
            this.protocol.noop();
        }
        if (keepStoreAlive && ((IMAPStore) this.store).hasSeparateStoreConnection()) {
            IMAPProtocol p = null;
            try {
                p = ((IMAPStore) this.store).getStoreProtocol();
                if (System.currentTimeMillis() - p.getTimestamp() > 1000) {
                    p.noop();
                }
            } finally {
                ((IMAPStore) this.store).releaseStoreProtocol(p);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public IMAPMessage getMessageBySeqNumber(int seqnum) {
        for (int i = seqnum - 1; i < this.total; i++) {
            IMAPMessage msg = (IMAPMessage) this.messageCache.elementAt(i);
            if (msg.getSequenceNumber() == seqnum) {
                return msg;
            }
        }
        return null;
    }

    private boolean isDirectory() {
        return (this.type & 2) != 0;
    }
}
