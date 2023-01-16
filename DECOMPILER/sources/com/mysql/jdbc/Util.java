package com.mysql.jdbc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import kotlinx.coroutines.internal.LockFreeTaskQueueCore;

public class Util {
    private static final String MYSQL_JDBC_PACKAGE_ROOT;
    private static Util enclosingInstance = new Util();
    private static final ConcurrentMap<Class<?>, Class<?>[]> implementedInterfacesCache = new ConcurrentHashMap();
    private static boolean isColdFusion;
    private static boolean isJdbc4;
    private static boolean isJdbc42;
    private static final ConcurrentMap<Class<?>, Boolean> isJdbcInterfaceCache = new ConcurrentHashMap();
    private static int jvmUpdateNumber;
    private static int jvmVersion;

    class RandStructcture {
        long maxValue;
        double maxValueDbl;
        long seed1;
        long seed2;

        RandStructcture() {
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0036 A[LOOP:0: B:12:0x0036->B:15:0x0046, LOOP_START, PHI: r5 
      PHI: (r5v6 'endPos' int) = (r5v0 'endPos' int), (r5v7 'endPos' int) binds: [B:11:0x0034, B:15:0x0046] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0071 A[LOOP:1: B:29:0x0071->B:32:0x0081, LOOP_START, PHI: r5 
      PHI: (r5v4 'endPos' int) = (r5v2 'endPos' int), (r5v5 'endPos' int) binds: [B:28:0x006f, B:32:0x0081] A[DONT_GENERATE, DONT_INLINE]] */
    static {
        /*
            com.mysql.jdbc.Util r0 = new com.mysql.jdbc.Util
            r0.<init>()
            enclosingInstance = r0
            r0 = -1
            jvmVersion = r0
            jvmUpdateNumber = r0
            r1 = 0
            isColdFusion = r1
            r2 = 1
            java.lang.String r3 = "java.sql.NClob"
            java.lang.Class.forName(r3)     // Catch:{ ClassNotFoundException -> 0x0018 }
            isJdbc4 = r2     // Catch:{ ClassNotFoundException -> 0x0018 }
            goto L_0x001b
        L_0x0018:
            r3 = move-exception
            isJdbc4 = r1
        L_0x001b:
            java.lang.String r3 = "java.sql.JDBCType"
            java.lang.Class.forName(r3)     // Catch:{ all -> 0x0023 }
            isJdbc42 = r2     // Catch:{ all -> 0x0023 }
            goto L_0x0026
        L_0x0023:
            r3 = move-exception
            isJdbc42 = r1
        L_0x0026:
            java.lang.String r3 = "java.version"
            java.lang.String r3 = java.lang.System.getProperty(r3)
            r4 = 46
            int r4 = r3.indexOf(r4)
            int r5 = r4 + 1
            if (r4 == r0) goto L_0x0049
        L_0x0036:
            char r6 = r3.charAt(r5)
            boolean r6 = java.lang.Character.isDigit(r6)
            if (r6 == 0) goto L_0x0049
            int r5 = r5 + 1
            int r6 = r3.length()
            if (r5 >= r6) goto L_0x0049
            goto L_0x0036
        L_0x0049:
            int r4 = r4 + r2
            if (r5 <= r4) goto L_0x0057
            java.lang.String r6 = r3.substring(r4, r5)
            int r6 = java.lang.Integer.parseInt(r6)
            jvmVersion = r6
            goto L_0x0067
        L_0x0057:
            boolean r6 = isJdbc42
            if (r6 == 0) goto L_0x005e
            r6 = 8
            goto L_0x0065
        L_0x005e:
            boolean r6 = isJdbc4
            if (r6 == 0) goto L_0x0064
            r6 = 6
            goto L_0x0065
        L_0x0064:
            r6 = 5
        L_0x0065:
            jvmVersion = r6
        L_0x0067:
            java.lang.String r6 = "_"
            int r4 = r3.indexOf(r6)
            int r5 = r4 + 1
            if (r4 == r0) goto L_0x0084
        L_0x0071:
            char r6 = r3.charAt(r5)
            boolean r6 = java.lang.Character.isDigit(r6)
            if (r6 == 0) goto L_0x0084
            int r5 = r5 + 1
            int r6 = r3.length()
            if (r5 >= r6) goto L_0x0084
            goto L_0x0071
        L_0x0084:
            int r4 = r4 + r2
            if (r5 <= r4) goto L_0x0091
            java.lang.String r6 = r3.substring(r4, r5)
            int r6 = java.lang.Integer.parseInt(r6)
            jvmUpdateNumber = r6
        L_0x0091:
            java.lang.Throwable r6 = new java.lang.Throwable
            r6.<init>()
            java.lang.String r6 = stackTraceToString(r6)
            if (r6 == 0) goto L_0x00a9
            java.lang.String r7 = "coldfusion"
            int r7 = r6.indexOf(r7)
            if (r7 == r0) goto L_0x00a5
            goto L_0x00a6
        L_0x00a5:
            r2 = r1
        L_0x00a6:
            isColdFusion = r2
            goto L_0x00ab
        L_0x00a9:
            isColdFusion = r1
        L_0x00ab:
            java.util.concurrent.ConcurrentHashMap r0 = new java.util.concurrent.ConcurrentHashMap
            r0.<init>()
            isJdbcInterfaceCache = r0
            java.lang.Class<com.mysql.jdbc.MultiHostConnectionProxy> r0 = com.mysql.jdbc.MultiHostConnectionProxy.class
            java.lang.String r0 = getPackageName(r0)
            java.lang.String r2 = "jdbc"
            int r2 = r0.indexOf(r2)
            int r2 = r2 + 4
            java.lang.String r1 = r0.substring(r1, r2)
            MYSQL_JDBC_PACKAGE_ROOT = r1
            java.util.concurrent.ConcurrentHashMap r0 = new java.util.concurrent.ConcurrentHashMap
            r0.<init>()
            implementedInterfacesCache = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.Util.<clinit>():void");
    }

    public static boolean isJdbc4() {
        return isJdbc4;
    }

    public static boolean isJdbc42() {
        return isJdbc42;
    }

    public static int getJVMVersion() {
        return jvmVersion;
    }

    public static boolean jvmMeetsMinimum(int version, int updateNumber) {
        return getJVMVersion() > version || (getJVMVersion() == version && getJVMUpdateNumber() >= updateNumber);
    }

    public static int getJVMUpdateNumber() {
        return jvmUpdateNumber;
    }

    public static boolean isColdFusion() {
        return isColdFusion;
    }

    public static boolean isCommunityEdition(String serverVersion) {
        return !isEnterpriseEdition(serverVersion);
    }

    public static boolean isEnterpriseEdition(String serverVersion) {
        return serverVersion.contains("enterprise") || serverVersion.contains("commercial") || serverVersion.contains("advanced");
    }

    public static String newCrypt(String password, String seed, String encoding) {
        String str = password;
        if (str == null || password.length() == 0) {
            return str;
        }
        long[] pw = newHash(seed.getBytes());
        long[] msg = hashPre41Password(str, encoding);
        long seed1 = (pw[0] ^ msg[0]) % LockFreeTaskQueueCore.HEAD_MASK;
        long seed2 = (pw[1] ^ msg[1]) % LockFreeTaskQueueCore.HEAD_MASK;
        char[] chars = new char[seed.length()];
        int i = 0;
        while (i < seed.length()) {
            seed1 = ((3 * seed1) + seed2) % LockFreeTaskQueueCore.HEAD_MASK;
            seed2 = ((seed1 + seed2) + 33) % LockFreeTaskQueueCore.HEAD_MASK;
            chars[i] = (char) ((byte) ((int) Math.floor((31.0d * (((double) seed1) / ((double) LockFreeTaskQueueCore.HEAD_MASK))) + 64.0d)));
            i++;
            String str2 = encoding;
            pw = pw;
        }
        long seed12 = ((3 * seed1) + seed2) % LockFreeTaskQueueCore.HEAD_MASK;
        long j = ((seed12 + seed2) + 33) % LockFreeTaskQueueCore.HEAD_MASK;
        byte b = (byte) ((int) Math.floor(31.0d * (((double) seed12) / ((double) LockFreeTaskQueueCore.HEAD_MASK))));
        for (int i2 = 0; i2 < seed.length(); i2++) {
            chars[i2] = (char) (chars[i2] ^ ((char) b));
        }
        return new String(chars);
    }

    public static long[] hashPre41Password(String password, String encoding) {
        try {
            return newHash(password.replaceAll("\\s", "").getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            return new long[0];
        }
    }

    public static long[] hashPre41Password(String password) {
        return hashPre41Password(password, Charset.defaultCharset().name());
    }

    static long[] newHash(byte[] password) {
        long nr = 1345345333;
        long add = 7;
        long nr2 = 305419889;
        for (byte b : password) {
            long tmp = (long) (b & 255);
            nr ^= (((63 & nr) + add) * tmp) + (nr << 8);
            nr2 += (nr2 << 8) ^ nr;
            add += tmp;
        }
        return new long[]{nr & 2147483647L, nr2 & 2147483647L};
    }

    public static String oldCrypt(String password, String seed) {
        if (password == null || password.length() == 0) {
            return password;
        }
        long hp = oldHash(seed);
        long nr = (hp ^ oldHash(password)) % 33554431;
        long s1 = nr;
        long s2 = nr / 2;
        char[] chars = new char[seed.length()];
        int i = 0;
        while (i < seed.length()) {
            s1 = ((3 * s1) + s2) % 33554431;
            s2 = ((s1 + s2) + 33) % 33554431;
            chars[i] = (char) ((byte) ((int) Math.floor((31.0d * (((double) s1) / ((double) 33554431))) + 64.0d)));
            i++;
            hp = hp;
        }
        return new String(chars);
    }

    static long oldHash(String password) {
        long nr = 1345345333;
        long nr2 = 7;
        for (int i = 0; i < password.length(); i++) {
            if (!(password.charAt(i) == ' ' || password.charAt(i) == 9)) {
                long tmp = (long) password.charAt(i);
                nr ^= (((63 & nr) + nr2) * tmp) + (nr << 8);
                nr2 += tmp;
            }
        }
        return 2147483647L & nr;
    }

    private static RandStructcture randomInit(long seed1, long seed2) {
        Util util = enclosingInstance;
        util.getClass();
        RandStructcture randStruct = new RandStructcture();
        randStruct.maxValue = LockFreeTaskQueueCore.HEAD_MASK;
        randStruct.maxValueDbl = (double) randStruct.maxValue;
        randStruct.seed1 = seed1 % randStruct.maxValue;
        randStruct.seed2 = seed2 % randStruct.maxValue;
        return randStruct;
    }

    private static double rnd(RandStructcture randStruct) {
        randStruct.seed1 = ((randStruct.seed1 * 3) + randStruct.seed2) % randStruct.maxValue;
        randStruct.seed2 = ((randStruct.seed1 + randStruct.seed2) + 33) % randStruct.maxValue;
        return ((double) randStruct.seed1) / randStruct.maxValueDbl;
    }

    public static String scramble(String message, String password) {
        byte[] to = new byte[8];
        String message2 = message.substring(0, 8);
        if (password == null || password.length() <= 0) {
            return "";
        }
        long[] hashPass = hashPre41Password(password);
        long[] hashMessage = newHash(message2.getBytes());
        RandStructcture randStruct = randomInit(hashPass[0] ^ hashMessage[0], hashPass[1] ^ hashMessage[1]);
        int toPos = 0;
        int msgLength = message2.length();
        int toPos2 = 0;
        while (true) {
            int msgPos = toPos + 1;
            if (toPos >= msgLength) {
                break;
            }
            to[toPos2] = (byte) ((int) (Math.floor(rnd(randStruct) * 31.0d) + 64.0d));
            toPos2++;
            toPos = msgPos;
        }
        byte extra = (byte) ((int) Math.floor(rnd(randStruct) * 31.0d));
        for (int i = 0; i < to.length; i++) {
            to[i] = (byte) (to[i] ^ extra);
        }
        return StringUtils.toString(to);
    }

    public static String stackTraceToString(Throwable ex) {
        StringBuilder traceBuf = new StringBuilder();
        traceBuf.append(Messages.getString("Util.1"));
        if (ex != null) {
            traceBuf.append(ex.getClass().getName());
            String message = ex.getMessage();
            if (message != null) {
                traceBuf.append(Messages.getString("Util.2"));
                traceBuf.append(message);
            }
            StringWriter out = new StringWriter();
            ex.printStackTrace(new PrintWriter(out));
            traceBuf.append(Messages.getString("Util.3"));
            traceBuf.append(out.toString());
        }
        traceBuf.append(Messages.getString("Util.4"));
        return traceBuf.toString();
    }

    public static Object getInstance(String className, Class<?>[] argTypes, Object[] args, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        try {
            return handleNewInstance(Class.forName(className).getConstructor(argTypes), args, exceptionInterceptor);
        } catch (SecurityException e) {
            throw SQLError.createSQLException("Can't instantiate required class", SQLError.SQL_STATE_GENERAL_ERROR, (Throwable) e, exceptionInterceptor);
        } catch (NoSuchMethodException e2) {
            throw SQLError.createSQLException("Can't instantiate required class", SQLError.SQL_STATE_GENERAL_ERROR, (Throwable) e2, exceptionInterceptor);
        } catch (ClassNotFoundException e3) {
            throw SQLError.createSQLException("Can't instantiate required class", SQLError.SQL_STATE_GENERAL_ERROR, (Throwable) e3, exceptionInterceptor);
        }
    }

    public static final Object handleNewInstance(Constructor<?> ctor, Object[] args, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        try {
            return ctor.newInstance(args);
        } catch (IllegalArgumentException e) {
            throw SQLError.createSQLException("Can't instantiate required class", SQLError.SQL_STATE_GENERAL_ERROR, (Throwable) e, exceptionInterceptor);
        } catch (InstantiationException e2) {
            throw SQLError.createSQLException("Can't instantiate required class", SQLError.SQL_STATE_GENERAL_ERROR, (Throwable) e2, exceptionInterceptor);
        } catch (IllegalAccessException e3) {
            throw SQLError.createSQLException("Can't instantiate required class", SQLError.SQL_STATE_GENERAL_ERROR, (Throwable) e3, exceptionInterceptor);
        } catch (InvocationTargetException e4) {
            Throwable target = e4.getTargetException();
            if (!(target instanceof SQLException)) {
                if (target instanceof ExceptionInInitializerError) {
                    target = ((ExceptionInInitializerError) target).getException();
                }
                throw SQLError.createSQLException(target.toString(), SQLError.SQL_STATE_GENERAL_ERROR, target, exceptionInterceptor);
            }
            throw ((SQLException) target);
        }
    }

    public static boolean interfaceExists(String hostname) {
        try {
            Class<?> networkInterfaceClass = Class.forName("java.net.NetworkInterface");
            Class[] clsArr = null;
            if (networkInterfaceClass.getMethod("getByName", (Class[]) null).invoke(networkInterfaceClass, new Object[]{hostname}) != null) {
                return true;
            }
            return false;
        } catch (Throwable th) {
            return false;
        }
    }

    public static Map<Object, Object> calculateDifferences(Map<?, ?> map1, Map<?, ?> map2) {
        Number value2;
        Number value1;
        Map<Object, Object> diffMap = new HashMap<>();
        for (Map.Entry<?, ?> entry : map1.entrySet()) {
            Object key = entry.getKey();
            if (entry.getValue() instanceof Number) {
                value1 = (Number) entry.getValue();
                value2 = (Number) map2.get(key);
            } else {
                try {
                    value1 = new Double(entry.getValue().toString());
                    value2 = new Double(map2.get(key).toString());
                } catch (NumberFormatException e) {
                }
            }
            if (!value1.equals(value2)) {
                if (value1 instanceof Byte) {
                    diffMap.put(key, Byte.valueOf((byte) (((Byte) value2).byteValue() - ((Byte) value1).byteValue())));
                } else if (value1 instanceof Short) {
                    diffMap.put(key, Short.valueOf((short) (((Short) value2).shortValue() - ((Short) value1).shortValue())));
                } else if (value1 instanceof Integer) {
                    diffMap.put(key, Integer.valueOf(((Integer) value2).intValue() - ((Integer) value1).intValue()));
                } else if (value1 instanceof Long) {
                    diffMap.put(key, Long.valueOf(((Long) value2).longValue() - ((Long) value1).longValue()));
                } else if (value1 instanceof Float) {
                    diffMap.put(key, Float.valueOf(((Float) value2).floatValue() - ((Float) value1).floatValue()));
                } else if (value1 instanceof Double) {
                    diffMap.put(key, Double.valueOf((double) (((Double) value2).shortValue() - ((Double) value1).shortValue())));
                } else if (value1 instanceof BigDecimal) {
                    diffMap.put(key, ((BigDecimal) value2).subtract((BigDecimal) value1));
                } else if (value1 instanceof BigInteger) {
                    diffMap.put(key, ((BigInteger) value2).subtract((BigInteger) value1));
                }
            }
        }
        return diffMap;
    }

    public static List<Extension> loadExtensions(Connection conn, Properties props, String extensionClassNames, String errorMessageKey, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        List<Extension> extensionList = new LinkedList<>();
        List<String> interceptorsToCreate = StringUtils.split(extensionClassNames, ",", true);
        try {
            int s = interceptorsToCreate.size();
            for (int i = 0; i < s; i++) {
                Extension extensionInstance = (Extension) Class.forName(interceptorsToCreate.get(i)).newInstance();
                extensionInstance.init(conn, props);
                extensionList.add(extensionInstance);
            }
            return extensionList;
        } catch (Throwable t) {
            SQLException sqlEx = SQLError.createSQLException(Messages.getString(errorMessageKey, new Object[]{null}), exceptionInterceptor);
            sqlEx.initCause(t);
            throw sqlEx;
        }
    }

    public static boolean isJdbcInterface(Class<?> clazz) {
        ConcurrentMap<Class<?>, Boolean> concurrentMap = isJdbcInterfaceCache;
        if (concurrentMap.containsKey(clazz)) {
            return ((Boolean) concurrentMap.get(clazz)).booleanValue();
        }
        if (clazz.isInterface()) {
            try {
                if (isJdbcPackage(getPackageName(clazz))) {
                    concurrentMap.putIfAbsent(clazz, true);
                    return true;
                }
            } catch (Exception e) {
            }
        }
        for (Class<?> iface : clazz.getInterfaces()) {
            if (isJdbcInterface(iface)) {
                isJdbcInterfaceCache.putIfAbsent(clazz, true);
                return true;
            }
        }
        if (clazz.getSuperclass() == null || !isJdbcInterface(clazz.getSuperclass())) {
            isJdbcInterfaceCache.putIfAbsent(clazz, false);
            return false;
        }
        isJdbcInterfaceCache.putIfAbsent(clazz, true);
        return true;
    }

    public static boolean isJdbcPackage(String packageName) {
        return packageName != null && (packageName.startsWith("java.sql") || packageName.startsWith("javax.sql") || packageName.startsWith(MYSQL_JDBC_PACKAGE_ROOT));
    }

    public static Class<?>[] getImplementedInterfaces(Class<?> clazz) {
        Class<? super Object> superclass;
        Class<?>[] implementedInterfaces = (Class[]) implementedInterfacesCache.get(clazz);
        if (implementedInterfaces != null) {
            return implementedInterfaces;
        }
        Set<Class<?>> interfaces = new LinkedHashSet<>();
        Class<?> superClass = clazz;
        do {
            Collections.addAll(interfaces, superClass.getInterfaces());
            superclass = superClass.getSuperclass();
            superClass = superclass;
        } while (superclass != null);
        Class<?>[] implementedInterfaces2 = (Class[]) interfaces.toArray(new Class[interfaces.size()]);
        Class<?>[] oldValue = (Class[]) implementedInterfacesCache.putIfAbsent(clazz, implementedInterfaces2);
        if (oldValue != null) {
            return oldValue;
        }
        return implementedInterfaces2;
    }

    public static long secondsSinceMillis(long timeInMillis) {
        return (System.currentTimeMillis() - timeInMillis) / 1000;
    }

    public static int truncateAndConvertToInt(long longValue) {
        if (longValue > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        if (longValue < -2147483648L) {
            return Integer.MIN_VALUE;
        }
        return (int) longValue;
    }

    public static int[] truncateAndConvertToInt(long[] longArray) {
        int[] intArray = new int[longArray.length];
        for (int i = 0; i < longArray.length; i++) {
            intArray[i] = longArray[i] > 2147483647L ? Integer.MAX_VALUE : longArray[i] < -2147483648L ? Integer.MIN_VALUE : (int) longArray[i];
        }
        return intArray;
    }

    public static String getPackageName(Class<?> clazz) {
        String fqcn = clazz.getName();
        int classNameStartsAt = fqcn.lastIndexOf(46);
        if (classNameStartsAt > 0) {
            return fqcn.substring(0, classNameStartsAt);
        }
        return "";
    }
}
