package com.mysql.jdbc.util;

public class VersionFSHierarchyMaker {
    /* JADX WARNING: Removed duplicated region for block: B:24:0x015d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void main(java.lang.String[] r21) throws java.lang.Exception {
        /*
            r1 = r21
            int r0 = r1.length
            r2 = 1
            r3 = 3
            if (r0 >= r3) goto L_0x000d
            usage()
            java.lang.System.exit(r2)
        L_0x000d:
            r0 = 0
            java.lang.String r3 = "java.version"
            java.lang.String r3 = java.lang.System.getProperty(r3)
            java.lang.String r3 = removeWhitespaceChars(r3)
            java.lang.String r4 = "java.vendor"
            java.lang.String r4 = java.lang.System.getProperty(r4)
            java.lang.String r4 = removeWhitespaceChars(r4)
            java.lang.String r5 = "os.name"
            java.lang.String r5 = java.lang.System.getProperty(r5)
            java.lang.String r5 = removeWhitespaceChars(r5)
            java.lang.String r6 = "os.arch"
            java.lang.String r6 = java.lang.System.getProperty(r6)
            java.lang.String r6 = removeWhitespaceChars(r6)
            java.lang.String r7 = "os.version"
            java.lang.String r7 = java.lang.System.getProperty(r7)
            java.lang.String r7 = removeWhitespaceChars(r7)
            java.lang.String r8 = "com.mysql.jdbc.testsuite.url"
            java.lang.String r8 = java.lang.System.getProperty(r8)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r9 = "MySQL"
            java.lang.StringBuilder r0 = r0.append(r9)
            r9 = 2
            r9 = r1[r9]
            java.lang.StringBuilder r0 = r0.append(r9)
            java.lang.String r9 = "_"
            java.lang.StringBuilder r0 = r0.append(r9)
            java.lang.String r9 = r0.toString()
            java.util.Properties r0 = new java.util.Properties     // Catch:{ all -> 0x00a1 }
            r0.<init>()     // Catch:{ all -> 0x00a1 }
            java.lang.String r10 = "allowPublicKeyRetrieval"
            java.lang.String r11 = "true"
            r0.setProperty(r10, r11)     // Catch:{ all -> 0x00a1 }
            com.mysql.jdbc.NonRegisteringDriver r10 = new com.mysql.jdbc.NonRegisteringDriver     // Catch:{ all -> 0x00a1 }
            r10.<init>()     // Catch:{ all -> 0x00a1 }
            java.sql.Connection r10 = r10.connect(r8, r0)     // Catch:{ all -> 0x00a1 }
            java.sql.Statement r11 = r10.createStatement()     // Catch:{ all -> 0x00a1 }
            java.lang.String r12 = "SELECT VERSION()"
            java.sql.ResultSet r11 = r11.executeQuery(r12)     // Catch:{ all -> 0x00a1 }
            r11.next()     // Catch:{ all -> 0x00a1 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a1 }
            r12.<init>()     // Catch:{ all -> 0x00a1 }
            java.lang.StringBuilder r12 = r12.append(r9)     // Catch:{ all -> 0x00a1 }
            java.lang.String r13 = r11.getString(r2)     // Catch:{ all -> 0x00a1 }
            java.lang.String r13 = removeWhitespaceChars(r13)     // Catch:{ all -> 0x00a1 }
            java.lang.StringBuilder r12 = r12.append(r13)     // Catch:{ all -> 0x00a1 }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x00a1 }
            r0 = r12
            r9 = r0
            goto L_0x00bd
        L_0x00a1:
            r0 = move-exception
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.StringBuilder r10 = r10.append(r9)
            java.lang.String r11 = "no-server-running-on-"
            java.lang.StringBuilder r10 = r10.append(r11)
            java.lang.String r11 = removeWhitespaceChars(r8)
            java.lang.StringBuilder r10 = r10.append(r11)
            java.lang.String r9 = r10.toString()
        L_0x00bd:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.StringBuilder r0 = r0.append(r4)
            java.lang.String r10 = "-"
            java.lang.StringBuilder r0 = r0.append(r10)
            java.lang.StringBuilder r0 = r0.append(r3)
            java.lang.String r11 = r0.toString()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.StringBuilder r0 = r0.append(r5)
            java.lang.StringBuilder r0 = r0.append(r10)
            java.lang.StringBuilder r0 = r0.append(r6)
            java.lang.StringBuilder r0 = r0.append(r10)
            java.lang.StringBuilder r0 = r0.append(r7)
            java.lang.String r10 = r0.toString()
            java.io.File r0 = new java.io.File
            r12 = 0
            r12 = r1[r12]
            r0.<init>(r12)
            r12 = r0
            java.io.File r0 = new java.io.File
            r0.<init>(r12, r9)
            r13 = r0
            java.io.File r0 = new java.io.File
            r0.<init>(r13, r10)
            r14 = r0
            java.io.File r0 = new java.io.File
            r0.<init>(r14, r11)
            r15 = r0
            r15.mkdirs()
            r16 = 0
            r0 = r1[r2]     // Catch:{ all -> 0x015a }
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ all -> 0x015a }
            r2.<init>(r0)     // Catch:{ all -> 0x015a }
            java.lang.String r16 = r12.getAbsolutePath()     // Catch:{ all -> 0x0156 }
            r18 = r16
            java.lang.String r16 = r15.getAbsolutePath()     // Catch:{ all -> 0x0156 }
            r19 = r16
            r20 = r0
            r1 = r18
            r0 = r19
            boolean r16 = r0.startsWith(r1)     // Catch:{ all -> 0x0156 }
            if (r16 == 0) goto L_0x0141
            int r16 = r1.length()     // Catch:{ all -> 0x0156 }
            r18 = r1
            r17 = 1
            int r1 = r16 + 1
            java.lang.String r1 = r0.substring(r1)     // Catch:{ all -> 0x0156 }
            r19 = r1
            goto L_0x0145
        L_0x0141:
            r18 = r1
            r19 = r0
        L_0x0145:
            byte[] r0 = r19.getBytes()     // Catch:{ all -> 0x0156 }
            r2.write(r0)     // Catch:{ all -> 0x0156 }
            r2.flush()
            r2.close()
            return
        L_0x0156:
            r0 = move-exception
            r16 = r2
            goto L_0x015b
        L_0x015a:
            r0 = move-exception
        L_0x015b:
            if (r16 == 0) goto L_0x0163
            r16.flush()
            r16.close()
        L_0x0163:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.util.VersionFSHierarchyMaker.main(java.lang.String[]):void");
    }

    public static String removeWhitespaceChars(String input) {
        if (input == null) {
            return input;
        }
        int strLen = input.length();
        StringBuilder output = new StringBuilder(strLen);
        for (int i = 0; i < strLen; i++) {
            char c = input.charAt(i);
            if (Character.isDigit(c) || Character.isLetter(c)) {
                output.append(c);
            } else if (Character.isWhitespace(c)) {
                output.append("_");
            } else {
                output.append(".");
            }
        }
        return output.toString();
    }

    private static void usage() {
        System.err.println("Creates a fs hierarchy representing MySQL version, OS version and JVM version.");
        System.err.println("Stores the full path as 'outputDirectory' property in file 'directoryPropPath'");
        System.err.println();
        System.err.println("Usage: java VersionFSHierarchyMaker baseDirectory directoryPropPath jdbcUrlIter");
    }
}
