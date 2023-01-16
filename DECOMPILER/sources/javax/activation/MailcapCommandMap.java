package javax.activation;

import com.sun.activation.registries.LogSupport;
import com.sun.activation.registries.MailcapFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MailcapCommandMap extends CommandMap {
    private static final int PROG = 0;
    private static MailcapFile defDB = null;
    private MailcapFile[] DB;

    public MailcapCommandMap() {
        MailcapFile mf;
        List dbv = new ArrayList(5);
        dbv.add((Object) null);
        LogSupport.log("MailcapCommandMap: load HOME");
        try {
            String user_home = System.getProperty("user.home");
            if (!(user_home == null || (mf = loadFile(String.valueOf(user_home) + File.separator + ".mailcap")) == null)) {
                dbv.add(mf);
            }
        } catch (SecurityException e) {
        }
        LogSupport.log("MailcapCommandMap: load SYS");
        try {
            MailcapFile mf2 = loadFile(String.valueOf(System.getProperty("java.home")) + File.separator + "lib" + File.separator + "mailcap");
            if (mf2 != null) {
                dbv.add(mf2);
            }
        } catch (SecurityException e2) {
        }
        LogSupport.log("MailcapCommandMap: load JAR");
        loadAllResources(dbv, "mailcap");
        LogSupport.log("MailcapCommandMap: load DEF");
        synchronized (MailcapCommandMap.class) {
            if (defDB == null) {
                defDB = loadResource("mailcap.default");
            }
        }
        MailcapFile mailcapFile = defDB;
        if (mailcapFile != null) {
            dbv.add(mailcapFile);
        }
        MailcapFile[] mailcapFileArr = new MailcapFile[dbv.size()];
        this.DB = mailcapFileArr;
        this.DB = (MailcapFile[]) dbv.toArray(mailcapFileArr);
    }

    private MailcapFile loadResource(String name) {
        InputStream clis = null;
        try {
            clis = SecuritySupport.getResourceAsStream(getClass(), name);
            if (clis != null) {
                MailcapFile mailcapFile = new MailcapFile(clis);
                if (LogSupport.isLoggable()) {
                    LogSupport.log("MailcapCommandMap: successfully loaded mailcap file: " + name);
                }
                if (clis != null) {
                    try {
                        clis.close();
                    } catch (IOException e) {
                    }
                }
                return mailcapFile;
            }
            if (LogSupport.isLoggable()) {
                LogSupport.log("MailcapCommandMap: not loading mailcap file: " + name);
            }
            if (clis == null) {
                return null;
            }
            try {
                clis.close();
                return null;
            } catch (IOException e2) {
                return null;
            }
        } catch (IOException e3) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("MailcapCommandMap: can't load " + name, e3);
            }
            if (clis == null) {
                return null;
            }
            clis.close();
            return null;
        } catch (SecurityException sex) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("MailcapCommandMap: can't load " + name, sex);
            }
            if (clis == null) {
                return null;
            }
            clis.close();
            return null;
        } catch (Throwable th) {
            if (clis != null) {
                try {
                    clis.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
    }

    private void loadAllResources(List v, String name) {
        URL[] urls;
        InputStream clis;
        boolean anyLoaded = false;
        try {
            ClassLoader cld = SecuritySupport.getContextClassLoader();
            if (cld == null) {
                cld = getClass().getClassLoader();
            }
            if (cld != null) {
                urls = SecuritySupport.getResources(cld, name);
            } else {
                urls = SecuritySupport.getSystemResources(name);
            }
            if (urls != null) {
                if (LogSupport.isLoggable()) {
                    LogSupport.log("MailcapCommandMap: getResources");
                }
                for (URL url : urls) {
                    clis = null;
                    if (LogSupport.isLoggable()) {
                        LogSupport.log("MailcapCommandMap: URL " + url);
                    }
                    try {
                        InputStream clis2 = SecuritySupport.openStream(url);
                        if (clis2 != null) {
                            v.add(new MailcapFile(clis2));
                            anyLoaded = true;
                            if (LogSupport.isLoggable()) {
                                LogSupport.log("MailcapCommandMap: successfully loaded mailcap file from URL: " + url);
                            }
                        } else if (LogSupport.isLoggable()) {
                            LogSupport.log("MailcapCommandMap: not loading mailcap file from URL: " + url);
                        }
                        if (clis2 != null) {
                            try {
                                clis2.close();
                            } catch (IOException e) {
                            }
                        }
                    } catch (IOException ioex) {
                        if (LogSupport.isLoggable()) {
                            LogSupport.log("MailcapCommandMap: can't load " + url, ioex);
                        }
                        if (clis != null) {
                            clis.close();
                        }
                    } catch (SecurityException sex) {
                        if (LogSupport.isLoggable()) {
                            LogSupport.log("MailcapCommandMap: can't load " + url, sex);
                        }
                        if (clis != null) {
                            clis.close();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("MailcapCommandMap: can't load " + name, ex);
            }
        } catch (Throwable th) {
            if (clis != null) {
                try {
                    clis.close();
                } catch (IOException e2) {
                }
            }
            throw th;
        }
        if (!anyLoaded) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("MailcapCommandMap: !anyLoaded");
            }
            MailcapFile mf = loadResource("/" + name);
            if (mf != null) {
                v.add(mf);
            }
        }
    }

    private MailcapFile loadFile(String name) {
        try {
            return new MailcapFile(name);
        } catch (IOException e) {
            return null;
        }
    }

    public MailcapCommandMap(String fileName) throws IOException {
        this();
        if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: load PROG from " + fileName);
        }
        MailcapFile[] mailcapFileArr = this.DB;
        if (mailcapFileArr[0] == null) {
            mailcapFileArr[0] = new MailcapFile(fileName);
        }
    }

    public MailcapCommandMap(InputStream is) {
        this();
        LogSupport.log("MailcapCommandMap: load PROG");
        MailcapFile[] mailcapFileArr = this.DB;
        if (mailcapFileArr[0] == null) {
            try {
                mailcapFileArr[0] = new MailcapFile(is);
            } catch (IOException e) {
            }
        }
    }

    public synchronized CommandInfo[] getPreferredCommands(String mimeType) {
        List cmdList;
        cmdList = new ArrayList();
        if (mimeType != null) {
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);
        }
        int i = 0;
        while (true) {
            MailcapFile[] mailcapFileArr = this.DB;
            if (i >= mailcapFileArr.length) {
                break;
            }
            MailcapFile mailcapFile = mailcapFileArr[i];
            if (mailcapFile != null) {
                Map cmdMap = mailcapFile.getMailcapList(mimeType);
                if (cmdMap != null) {
                    appendPrefCmdsToList(cmdMap, cmdList);
                }
            }
            i++;
        }
        int i2 = 0;
        while (true) {
            MailcapFile[] mailcapFileArr2 = this.DB;
            if (i2 >= mailcapFileArr2.length) {
            } else {
                MailcapFile mailcapFile2 = mailcapFileArr2[i2];
                if (mailcapFile2 != null) {
                    Map cmdMap2 = mailcapFile2.getMailcapFallbackList(mimeType);
                    if (cmdMap2 != null) {
                        appendPrefCmdsToList(cmdMap2, cmdList);
                    }
                }
                i2++;
            }
        }
        return (CommandInfo[]) cmdList.toArray(new CommandInfo[cmdList.size()]);
    }

    private void appendPrefCmdsToList(Map cmdHash, List cmdList) {
        for (String verb : cmdHash.keySet()) {
            if (!checkForVerb(cmdList, verb)) {
                cmdList.add(new CommandInfo(verb, (String) ((List) cmdHash.get(verb)).get(0)));
            }
        }
    }

    private boolean checkForVerb(List cmdList, String verb) {
        Iterator ee = cmdList.iterator();
        while (ee.hasNext()) {
            if (((CommandInfo) ee.next()).getCommandName().equals(verb)) {
                return true;
            }
        }
        return false;
    }

    public synchronized CommandInfo[] getAllCommands(String mimeType) {
        List cmdList;
        cmdList = new ArrayList();
        if (mimeType != null) {
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);
        }
        int i = 0;
        while (true) {
            MailcapFile[] mailcapFileArr = this.DB;
            if (i >= mailcapFileArr.length) {
                break;
            }
            MailcapFile mailcapFile = mailcapFileArr[i];
            if (mailcapFile != null) {
                Map cmdMap = mailcapFile.getMailcapList(mimeType);
                if (cmdMap != null) {
                    appendCmdsToList(cmdMap, cmdList);
                }
            }
            i++;
        }
        int i2 = 0;
        while (true) {
            MailcapFile[] mailcapFileArr2 = this.DB;
            if (i2 >= mailcapFileArr2.length) {
            } else {
                MailcapFile mailcapFile2 = mailcapFileArr2[i2];
                if (mailcapFile2 != null) {
                    Map cmdMap2 = mailcapFile2.getMailcapFallbackList(mimeType);
                    if (cmdMap2 != null) {
                        appendCmdsToList(cmdMap2, cmdList);
                    }
                }
                i2++;
            }
        }
        return (CommandInfo[]) cmdList.toArray(new CommandInfo[cmdList.size()]);
    }

    private void appendCmdsToList(Map typeHash, List cmdList) {
        for (String verb : typeHash.keySet()) {
            for (String cmd : (List) typeHash.get(verb)) {
                cmdList.add(new CommandInfo(verb, cmd));
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0015, code lost:
        r1 = r5.DB;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0018, code lost:
        if (r0 < r1.length) goto L_0x001d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x001c, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r1 = r1[r0];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x001f, code lost:
        if (r1 != null) goto L_0x0022;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0022, code lost:
        r1 = r1.getMailcapFallbackList(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0026, code lost:
        if (r1 == null) goto L_0x003f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0028, code lost:
        r2 = (java.util.List) r1.get(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x002e, code lost:
        if (r2 == null) goto L_0x003f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0030, code lost:
        r4 = (java.lang.String) r2.get(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0036, code lost:
        if (r4 == null) goto L_0x003f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x003e, code lost:
        return new javax.activation.CommandInfo(r7, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x003f, code lost:
        r0 = r0 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0014, code lost:
        r0 = 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized javax.activation.CommandInfo getCommand(java.lang.String r6, java.lang.String r7) {
        /*
            r5 = this;
            monitor-enter(r5)
            if (r6 == 0) goto L_0x000d
            java.util.Locale r0 = java.util.Locale.ENGLISH     // Catch:{ all -> 0x000b }
            java.lang.String r0 = r6.toLowerCase(r0)     // Catch:{ all -> 0x000b }
            r6 = r0
            goto L_0x000d
        L_0x000b:
            r6 = move-exception
            goto L_0x0067
        L_0x000d:
            r0 = 0
        L_0x000e:
            com.sun.activation.registries.MailcapFile[] r1 = r5.DB     // Catch:{ all -> 0x000b }
            int r2 = r1.length     // Catch:{ all -> 0x000b }
            r3 = 0
            if (r0 < r2) goto L_0x0042
            r0 = 0
        L_0x0015:
            com.sun.activation.registries.MailcapFile[] r1 = r5.DB     // Catch:{ all -> 0x000b }
            int r2 = r1.length     // Catch:{ all -> 0x000b }
            if (r0 < r2) goto L_0x001d
            r0 = 0
            monitor-exit(r5)
            return r0
        L_0x001d:
            r1 = r1[r0]     // Catch:{ all -> 0x000b }
            if (r1 != 0) goto L_0x0022
            goto L_0x003f
        L_0x0022:
            java.util.Map r1 = r1.getMailcapFallbackList(r6)     // Catch:{ all -> 0x000b }
            if (r1 == 0) goto L_0x003f
            java.lang.Object r2 = r1.get(r7)     // Catch:{ all -> 0x000b }
            java.util.List r2 = (java.util.List) r2     // Catch:{ all -> 0x000b }
            if (r2 == 0) goto L_0x003f
            java.lang.Object r4 = r2.get(r3)     // Catch:{ all -> 0x000b }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ all -> 0x000b }
            if (r4 == 0) goto L_0x003f
            javax.activation.CommandInfo r3 = new javax.activation.CommandInfo     // Catch:{ all -> 0x000b }
            r3.<init>(r7, r4)     // Catch:{ all -> 0x000b }
            monitor-exit(r5)
            return r3
        L_0x003f:
            int r0 = r0 + 1
            goto L_0x0015
        L_0x0042:
            r1 = r1[r0]     // Catch:{ all -> 0x000b }
            if (r1 != 0) goto L_0x0047
            goto L_0x0064
        L_0x0047:
            java.util.Map r1 = r1.getMailcapList(r6)     // Catch:{ all -> 0x000b }
            if (r1 == 0) goto L_0x0064
            java.lang.Object r2 = r1.get(r7)     // Catch:{ all -> 0x000b }
            java.util.List r2 = (java.util.List) r2     // Catch:{ all -> 0x000b }
            if (r2 == 0) goto L_0x0064
            java.lang.Object r3 = r2.get(r3)     // Catch:{ all -> 0x000b }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x000b }
            if (r3 == 0) goto L_0x0064
            javax.activation.CommandInfo r4 = new javax.activation.CommandInfo     // Catch:{ all -> 0x000b }
            r4.<init>(r7, r3)     // Catch:{ all -> 0x000b }
            monitor-exit(r5)
            return r4
        L_0x0064:
            int r0 = r0 + 1
            goto L_0x000e
        L_0x0067:
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: javax.activation.MailcapCommandMap.getCommand(java.lang.String, java.lang.String):javax.activation.CommandInfo");
    }

    public synchronized void addMailcap(String mail_cap) {
        LogSupport.log("MailcapCommandMap: add to PROG");
        MailcapFile[] mailcapFileArr = this.DB;
        if (mailcapFileArr[0] == null) {
            mailcapFileArr[0] = new MailcapFile();
        }
        this.DB[0].appendToMailcap(mail_cap);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0029, code lost:
        r0 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002a, code lost:
        r1 = r6.DB;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002d, code lost:
        if (r0 < r1.length) goto L_0x0032;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0031, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0034, code lost:
        if (r1[r0] != null) goto L_0x0037;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x003b, code lost:
        if (com.sun.activation.registries.LogSupport.isLoggable() == false) goto L_0x004f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x003d, code lost:
        com.sun.activation.registries.LogSupport.log("  search fallback DB #" + r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x004f, code lost:
        r1 = r6.DB[r0].getMailcapFallbackList(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0057, code lost:
        if (r1 == null) goto L_0x0071;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0059, code lost:
        r2 = (java.util.List) r1.get("content-handler");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0061, code lost:
        if (r2 == null) goto L_0x0071;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0063, code lost:
        r5 = getDataContentHandler((java.lang.String) r2.get(0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x006d, code lost:
        if (r5 == null) goto L_0x0071;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0070, code lost:
        return r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0071, code lost:
        r0 = r0 + 1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized javax.activation.DataContentHandler createDataContentHandler(java.lang.String r7) {
        /*
            r6 = this;
            monitor-enter(r6)
            boolean r0 = com.sun.activation.registries.LogSupport.isLoggable()     // Catch:{ all -> 0x00b7 }
            if (r0 == 0) goto L_0x0019
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b7 }
            java.lang.String r1 = "MailcapCommandMap: createDataContentHandler for "
            r0.<init>(r1)     // Catch:{ all -> 0x00b7 }
            java.lang.StringBuilder r0 = r0.append(r7)     // Catch:{ all -> 0x00b7 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x00b7 }
            com.sun.activation.registries.LogSupport.log(r0)     // Catch:{ all -> 0x00b7 }
        L_0x0019:
            if (r7 == 0) goto L_0x0022
            java.util.Locale r0 = java.util.Locale.ENGLISH     // Catch:{ all -> 0x00b7 }
            java.lang.String r0 = r7.toLowerCase(r0)     // Catch:{ all -> 0x00b7 }
            r7 = r0
        L_0x0022:
            r0 = 0
        L_0x0023:
            com.sun.activation.registries.MailcapFile[] r1 = r6.DB     // Catch:{ all -> 0x00b7 }
            int r2 = r1.length     // Catch:{ all -> 0x00b7 }
            r3 = 0
            if (r0 < r2) goto L_0x0074
            r0 = 0
        L_0x002a:
            com.sun.activation.registries.MailcapFile[] r1 = r6.DB     // Catch:{ all -> 0x00b7 }
            int r2 = r1.length     // Catch:{ all -> 0x00b7 }
            if (r0 < r2) goto L_0x0032
            r0 = 0
            monitor-exit(r6)
            return r0
        L_0x0032:
            r1 = r1[r0]     // Catch:{ all -> 0x00b7 }
            if (r1 != 0) goto L_0x0037
            goto L_0x0071
        L_0x0037:
            boolean r1 = com.sun.activation.registries.LogSupport.isLoggable()     // Catch:{ all -> 0x00b7 }
            if (r1 == 0) goto L_0x004f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b7 }
            java.lang.String r2 = "  search fallback DB #"
            r1.<init>(r2)     // Catch:{ all -> 0x00b7 }
            java.lang.StringBuilder r1 = r1.append(r0)     // Catch:{ all -> 0x00b7 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x00b7 }
            com.sun.activation.registries.LogSupport.log(r1)     // Catch:{ all -> 0x00b7 }
        L_0x004f:
            com.sun.activation.registries.MailcapFile[] r1 = r6.DB     // Catch:{ all -> 0x00b7 }
            r1 = r1[r0]     // Catch:{ all -> 0x00b7 }
            java.util.Map r1 = r1.getMailcapFallbackList(r7)     // Catch:{ all -> 0x00b7 }
            if (r1 == 0) goto L_0x0071
            java.lang.String r2 = "content-handler"
            java.lang.Object r2 = r1.get(r2)     // Catch:{ all -> 0x00b7 }
            java.util.List r2 = (java.util.List) r2     // Catch:{ all -> 0x00b7 }
            if (r2 == 0) goto L_0x0071
            java.lang.Object r4 = r2.get(r3)     // Catch:{ all -> 0x00b7 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ all -> 0x00b7 }
            javax.activation.DataContentHandler r5 = r6.getDataContentHandler(r4)     // Catch:{ all -> 0x00b7 }
            if (r5 == 0) goto L_0x0071
            monitor-exit(r6)
            return r5
        L_0x0071:
            int r0 = r0 + 1
            goto L_0x002a
        L_0x0074:
            r1 = r1[r0]     // Catch:{ all -> 0x00b7 }
            if (r1 != 0) goto L_0x0079
            goto L_0x00b3
        L_0x0079:
            boolean r1 = com.sun.activation.registries.LogSupport.isLoggable()     // Catch:{ all -> 0x00b7 }
            if (r1 == 0) goto L_0x0091
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b7 }
            java.lang.String r2 = "  search DB #"
            r1.<init>(r2)     // Catch:{ all -> 0x00b7 }
            java.lang.StringBuilder r1 = r1.append(r0)     // Catch:{ all -> 0x00b7 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x00b7 }
            com.sun.activation.registries.LogSupport.log(r1)     // Catch:{ all -> 0x00b7 }
        L_0x0091:
            com.sun.activation.registries.MailcapFile[] r1 = r6.DB     // Catch:{ all -> 0x00b7 }
            r1 = r1[r0]     // Catch:{ all -> 0x00b7 }
            java.util.Map r1 = r1.getMailcapList(r7)     // Catch:{ all -> 0x00b7 }
            if (r1 == 0) goto L_0x00b3
            java.lang.String r2 = "content-handler"
            java.lang.Object r2 = r1.get(r2)     // Catch:{ all -> 0x00b7 }
            java.util.List r2 = (java.util.List) r2     // Catch:{ all -> 0x00b7 }
            if (r2 == 0) goto L_0x00b3
            java.lang.Object r3 = r2.get(r3)     // Catch:{ all -> 0x00b7 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x00b7 }
            javax.activation.DataContentHandler r4 = r6.getDataContentHandler(r3)     // Catch:{ all -> 0x00b7 }
            if (r4 == 0) goto L_0x00b3
            monitor-exit(r6)
            return r4
        L_0x00b3:
            int r0 = r0 + 1
            goto L_0x0023
        L_0x00b7:
            r7 = move-exception
            monitor-exit(r6)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: javax.activation.MailcapCommandMap.createDataContentHandler(java.lang.String):javax.activation.DataContentHandler");
    }

    private DataContentHandler getDataContentHandler(String name) {
        Class cl;
        if (LogSupport.isLoggable()) {
            LogSupport.log("    got content-handler");
        }
        if (LogSupport.isLoggable()) {
            LogSupport.log("      class " + name);
        }
        try {
            ClassLoader cld = SecuritySupport.getContextClassLoader();
            if (cld == null) {
                cld = getClass().getClassLoader();
            }
            try {
                cl = cld.loadClass(name);
            } catch (Exception e) {
                cl = Class.forName(name);
            }
            if (cl != null) {
                return (DataContentHandler) cl.newInstance();
            }
            return null;
        } catch (IllegalAccessException e2) {
            if (!LogSupport.isLoggable()) {
                return null;
            }
            LogSupport.log("Can't load DCH " + name, e2);
            return null;
        } catch (ClassNotFoundException e3) {
            if (!LogSupport.isLoggable()) {
                return null;
            }
            LogSupport.log("Can't load DCH " + name, e3);
            return null;
        } catch (InstantiationException e4) {
            if (!LogSupport.isLoggable()) {
                return null;
            }
            LogSupport.log("Can't load DCH " + name, e4);
            return null;
        }
    }

    public synchronized String[] getMimeTypes() {
        List mtList;
        mtList = new ArrayList();
        int i = 0;
        while (true) {
            MailcapFile[] mailcapFileArr = this.DB;
            if (i >= mailcapFileArr.length) {
            } else {
                MailcapFile mailcapFile = mailcapFileArr[i];
                if (mailcapFile != null) {
                    String[] ts = mailcapFile.getMimeTypes();
                    if (ts != null) {
                        for (int j = 0; j < ts.length; j++) {
                            if (!mtList.contains(ts[j])) {
                                mtList.add(ts[j]);
                            }
                        }
                    }
                }
                i++;
            }
        }
        return (String[]) mtList.toArray(new String[mtList.size()]);
    }

    public synchronized String[] getNativeCommands(String mimeType) {
        List cmdList;
        cmdList = new ArrayList();
        if (mimeType != null) {
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);
        }
        int i = 0;
        while (true) {
            MailcapFile[] mailcapFileArr = this.DB;
            if (i >= mailcapFileArr.length) {
            } else {
                MailcapFile mailcapFile = mailcapFileArr[i];
                if (mailcapFile != null) {
                    String[] cmds = mailcapFile.getNativeCommands(mimeType);
                    if (cmds != null) {
                        for (int j = 0; j < cmds.length; j++) {
                            if (!cmdList.contains(cmds[j])) {
                                cmdList.add(cmds[j]);
                            }
                        }
                    }
                }
                i++;
            }
        }
        return (String[]) cmdList.toArray(new String[cmdList.size()]);
    }
}
