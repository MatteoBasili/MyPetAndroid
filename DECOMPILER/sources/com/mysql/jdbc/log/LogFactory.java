package com.mysql.jdbc.log;

public class LogFactory {
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0018, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r3 = java.lang.Class.forName(com.mysql.jdbc.Util.getPackageName(com.mysql.jdbc.log.Log.class) + "." + r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0058, code lost:
        r1 = com.mysql.jdbc.SQLError.createSQLException("Logger class '" + r8 + "' does not implement the '" + com.mysql.jdbc.log.Log.class.getName() + "' interface", com.mysql.jdbc.SQLError.SQL_STATE_ILLEGAL_ARGUMENT, r10);
        r1.initCause(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0088, code lost:
        throw r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0089, code lost:
        r1 = com.mysql.jdbc.SQLError.createSQLException("Unable to instantiate logger class '" + r8 + "', constructor not public", com.mysql.jdbc.SQLError.SQL_STATE_ILLEGAL_ARGUMENT, r10);
        r1.initCause(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x00a7, code lost:
        throw r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x00a8, code lost:
        r0 = com.mysql.jdbc.SQLError.createSQLException("Unable to instantiate logger class '" + r8 + "', exception in constructor?", com.mysql.jdbc.SQLError.SQL_STATE_ILLEGAL_ARGUMENT, r10);
        r0.initCause(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x00c4, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x00c5, code lost:
        r0 = com.mysql.jdbc.SQLError.createSQLException("Unable to instantiate logger class '" + r8 + "', exception in constructor?", com.mysql.jdbc.SQLError.SQL_STATE_ILLEGAL_ARGUMENT, r10);
        r0.initCause(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00e1, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00e2, code lost:
        r1 = com.mysql.jdbc.SQLError.createSQLException("Logger class does not have a single-arg constructor that takes an instance name", com.mysql.jdbc.SQLError.SQL_STATE_ILLEGAL_ARGUMENT, r10);
        r1.initCause(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00eb, code lost:
        throw r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0011, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0013, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0015, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x0018 A[ExcHandler: InstantiationException (r3v5 'inse' java.lang.InstantiationException A[CUSTOM_DECLARE]), Splitter:B:4:0x000b] */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x001b A[ExcHandler: NoSuchMethodException (r0v6 'nsme' java.lang.NoSuchMethodException A[CUSTOM_DECLARE]), Splitter:B:4:0x000b] */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0011 A[ExcHandler: ClassCastException (r0v14 'cce' java.lang.ClassCastException A[CUSTOM_DECLARE]), Splitter:B:4:0x000b] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0013 A[ExcHandler: IllegalAccessException (r0v13 'iae' java.lang.IllegalAccessException A[CUSTOM_DECLARE]), Splitter:B:4:0x000b] */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0015 A[ExcHandler: InvocationTargetException (r3v6 'ite' java.lang.reflect.InvocationTargetException A[CUSTOM_DECLARE]), Splitter:B:4:0x000b] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.mysql.jdbc.log.Log getLogger(java.lang.String r8, java.lang.String r9, com.mysql.jdbc.ExceptionInterceptor r10) throws java.sql.SQLException {
        /*
            java.lang.String r0 = "', exception in constructor?"
            java.lang.String r1 = "Unable to instantiate logger class '"
            java.lang.String r2 = "S1009"
            if (r8 == 0) goto L_0x0115
            if (r9 == 0) goto L_0x010e
            r3 = 0
            java.lang.Class r4 = java.lang.Class.forName(r8)     // Catch:{ ClassNotFoundException -> 0x001e, NoSuchMethodException -> 0x001b, InstantiationException -> 0x0018, InvocationTargetException -> 0x0015, IllegalAccessException -> 0x0013, ClassCastException -> 0x0011 }
            r3 = r4
            goto L_0x0041
        L_0x0011:
            r0 = move-exception
            goto L_0x0058
        L_0x0013:
            r0 = move-exception
            goto L_0x0089
        L_0x0015:
            r3 = move-exception
            goto L_0x00a8
        L_0x0018:
            r3 = move-exception
            goto L_0x00c5
        L_0x001b:
            r0 = move-exception
            goto L_0x00e2
        L_0x001e:
            r4 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ ClassNotFoundException -> 0x00ec, NoSuchMethodException -> 0x001b, InstantiationException -> 0x0018, InvocationTargetException -> 0x0015, IllegalAccessException -> 0x0013, ClassCastException -> 0x0011 }
            r5.<init>()     // Catch:{ ClassNotFoundException -> 0x00ec, NoSuchMethodException -> 0x001b, InstantiationException -> 0x0018, InvocationTargetException -> 0x0015, IllegalAccessException -> 0x0013, ClassCastException -> 0x0011 }
            java.lang.Class<com.mysql.jdbc.log.Log> r6 = com.mysql.jdbc.log.Log.class
            java.lang.String r6 = com.mysql.jdbc.Util.getPackageName(r6)     // Catch:{ ClassNotFoundException -> 0x00ec, NoSuchMethodException -> 0x001b, InstantiationException -> 0x0018, InvocationTargetException -> 0x0015, IllegalAccessException -> 0x0013, ClassCastException -> 0x0011 }
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ ClassNotFoundException -> 0x00ec, NoSuchMethodException -> 0x001b, InstantiationException -> 0x0018, InvocationTargetException -> 0x0015, IllegalAccessException -> 0x0013, ClassCastException -> 0x0011 }
            java.lang.String r6 = "."
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ ClassNotFoundException -> 0x00ec, NoSuchMethodException -> 0x001b, InstantiationException -> 0x0018, InvocationTargetException -> 0x0015, IllegalAccessException -> 0x0013, ClassCastException -> 0x0011 }
            java.lang.StringBuilder r5 = r5.append(r8)     // Catch:{ ClassNotFoundException -> 0x00ec, NoSuchMethodException -> 0x001b, InstantiationException -> 0x0018, InvocationTargetException -> 0x0015, IllegalAccessException -> 0x0013, ClassCastException -> 0x0011 }
            java.lang.String r5 = r5.toString()     // Catch:{ ClassNotFoundException -> 0x00ec, NoSuchMethodException -> 0x001b, InstantiationException -> 0x0018, InvocationTargetException -> 0x0015, IllegalAccessException -> 0x0013, ClassCastException -> 0x0011 }
            java.lang.Class r5 = java.lang.Class.forName(r5)     // Catch:{ ClassNotFoundException -> 0x00ec, NoSuchMethodException -> 0x001b, InstantiationException -> 0x0018, InvocationTargetException -> 0x0015, IllegalAccessException -> 0x0013, ClassCastException -> 0x0011 }
            r3 = r5
        L_0x0041:
            r4 = 1
            java.lang.Class[] r5 = new java.lang.Class[r4]     // Catch:{ ClassNotFoundException -> 0x00ec, NoSuchMethodException -> 0x001b, InstantiationException -> 0x0018, InvocationTargetException -> 0x0015, IllegalAccessException -> 0x0013, ClassCastException -> 0x0011 }
            java.lang.Class<java.lang.String> r6 = java.lang.String.class
            r7 = 0
            r5[r7] = r6     // Catch:{ ClassNotFoundException -> 0x00ec, NoSuchMethodException -> 0x001b, InstantiationException -> 0x0018, InvocationTargetException -> 0x0015, IllegalAccessException -> 0x0013, ClassCastException -> 0x0011 }
            java.lang.reflect.Constructor r5 = r3.getConstructor(r5)     // Catch:{ ClassNotFoundException -> 0x00ec, NoSuchMethodException -> 0x001b, InstantiationException -> 0x0018, InvocationTargetException -> 0x0015, IllegalAccessException -> 0x0013, ClassCastException -> 0x0011 }
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ ClassNotFoundException -> 0x00ec, NoSuchMethodException -> 0x001b, InstantiationException -> 0x0018, InvocationTargetException -> 0x0015, IllegalAccessException -> 0x0013, ClassCastException -> 0x0011 }
            r4[r7] = r9     // Catch:{ ClassNotFoundException -> 0x00ec, NoSuchMethodException -> 0x001b, InstantiationException -> 0x0018, InvocationTargetException -> 0x0015, IllegalAccessException -> 0x0013, ClassCastException -> 0x0011 }
            java.lang.Object r4 = r5.newInstance(r4)     // Catch:{ ClassNotFoundException -> 0x00ec, NoSuchMethodException -> 0x001b, InstantiationException -> 0x0018, InvocationTargetException -> 0x0015, IllegalAccessException -> 0x0013, ClassCastException -> 0x0011 }
            com.mysql.jdbc.log.Log r4 = (com.mysql.jdbc.log.Log) r4     // Catch:{ ClassNotFoundException -> 0x00ec, NoSuchMethodException -> 0x001b, InstantiationException -> 0x0018, InvocationTargetException -> 0x0015, IllegalAccessException -> 0x0013, ClassCastException -> 0x0011 }
            return r4
        L_0x0058:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "Logger class '"
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.StringBuilder r1 = r1.append(r8)
            java.lang.String r3 = "' does not implement the '"
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.Class<com.mysql.jdbc.log.Log> r3 = com.mysql.jdbc.log.Log.class
            java.lang.String r3 = r3.getName()
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.String r3 = "' interface"
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.String r1 = r1.toString()
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (com.mysql.jdbc.ExceptionInterceptor) r10)
            r1.initCause(r0)
            throw r1
        L_0x0089:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.StringBuilder r1 = r3.append(r1)
            java.lang.StringBuilder r1 = r1.append(r8)
            java.lang.String r3 = "', constructor not public"
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.String r1 = r1.toString()
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (com.mysql.jdbc.ExceptionInterceptor) r10)
            r1.initCause(r0)
            throw r1
        L_0x00a8:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.StringBuilder r1 = r4.append(r1)
            java.lang.StringBuilder r1 = r1.append(r8)
            java.lang.StringBuilder r0 = r1.append(r0)
            java.lang.String r0 = r0.toString()
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r0, (java.lang.String) r2, (com.mysql.jdbc.ExceptionInterceptor) r10)
            r0.initCause(r3)
            throw r0
        L_0x00c5:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.StringBuilder r1 = r4.append(r1)
            java.lang.StringBuilder r1 = r1.append(r8)
            java.lang.StringBuilder r0 = r1.append(r0)
            java.lang.String r0 = r0.toString()
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r0, (java.lang.String) r2, (com.mysql.jdbc.ExceptionInterceptor) r10)
            r0.initCause(r3)
            throw r0
        L_0x00e2:
            java.lang.String r1 = "Logger class does not have a single-arg constructor that takes an instance name"
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (com.mysql.jdbc.ExceptionInterceptor) r10)
            r1.initCause(r0)
            throw r1
        L_0x00ec:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "Unable to load class for logger '"
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.StringBuilder r1 = r1.append(r8)
            java.lang.String r3 = "'"
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.String r1 = r1.toString()
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (com.mysql.jdbc.ExceptionInterceptor) r10)
            r1.initCause(r0)
            throw r1
        L_0x010e:
            java.lang.String r0 = "Logger instance name can not be NULL"
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r0, (java.lang.String) r2, (com.mysql.jdbc.ExceptionInterceptor) r10)
            throw r0
        L_0x0115:
            java.lang.String r0 = "Logger class can not be NULL"
            java.sql.SQLException r0 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r0, (java.lang.String) r2, (com.mysql.jdbc.ExceptionInterceptor) r10)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.log.LogFactory.getLogger(java.lang.String, java.lang.String, com.mysql.jdbc.ExceptionInterceptor):com.mysql.jdbc.log.Log");
    }
}
