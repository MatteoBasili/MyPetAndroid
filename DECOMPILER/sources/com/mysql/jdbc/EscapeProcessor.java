package com.mysql.jdbc;

import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

class EscapeProcessor {
    private static Map<String, String> JDBC_CONVERT_TO_MYSQL_TYPE_MAP;
    private static Map<String, String> JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP;

    EscapeProcessor() {
    }

    static {
        Map<String, String> tempMap = new HashMap<>();
        tempMap.put("BIGINT", "0 + ?");
        tempMap.put("BINARY", "BINARY");
        tempMap.put("BIT", "0 + ?");
        tempMap.put("CHAR", "CHAR");
        tempMap.put("DATE", "DATE");
        tempMap.put("DECIMAL", "0.0 + ?");
        tempMap.put("DOUBLE", "0.0 + ?");
        tempMap.put("FLOAT", "0.0 + ?");
        tempMap.put("INTEGER", "0 + ?");
        tempMap.put("LONGVARBINARY", "BINARY");
        tempMap.put("LONGVARCHAR", "CONCAT(?)");
        tempMap.put("REAL", "0.0 + ?");
        tempMap.put("SMALLINT", "CONCAT(?)");
        tempMap.put("TIME", "TIME");
        tempMap.put("TIMESTAMP", "DATETIME");
        tempMap.put("TINYINT", "CONCAT(?)");
        tempMap.put("VARBINARY", "BINARY");
        tempMap.put("VARCHAR", "CONCAT(?)");
        JDBC_CONVERT_TO_MYSQL_TYPE_MAP = Collections.unmodifiableMap(tempMap);
        Map<String, String> tempMap2 = new HashMap<>(JDBC_CONVERT_TO_MYSQL_TYPE_MAP);
        tempMap2.put("BINARY", "CONCAT(?)");
        tempMap2.put("CHAR", "CONCAT(?)");
        tempMap2.remove("DATE");
        tempMap2.put("LONGVARBINARY", "CONCAT(?)");
        tempMap2.remove("TIME");
        tempMap2.remove("TIMESTAMP");
        tempMap2.put("VARBINARY", "CONCAT(?)");
        JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP = Collections.unmodifiableMap(tempMap2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:34:0x00bf A[SYNTHETIC, Splitter:B:34:0x00bf] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00ed  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final java.lang.Object escapeSQL(java.lang.String r23, boolean r24, com.mysql.jdbc.MySQLConnection r25) throws java.sql.SQLException {
        /*
            r1 = r23
            r2 = r24
            r3 = r25
            java.lang.String r4 = "-"
            java.lang.String r5 = "'"
            r0 = 0
            r6 = 0
            if (r1 != 0) goto L_0x0010
            r4 = 0
            return r4
        L_0x0010:
            r7 = 123(0x7b, float:1.72E-43)
            int r8 = r1.indexOf(r7)
            r9 = 125(0x7d, float:1.75E-43)
            r10 = -1
            if (r8 != r10) goto L_0x001d
            r11 = r10
            goto L_0x0021
        L_0x001d:
            int r11 = r1.indexOf(r9, r8)
        L_0x0021:
            if (r11 != r10) goto L_0x0024
            return r1
        L_0x0024:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            com.mysql.jdbc.EscapeTokenizer r13 = new com.mysql.jdbc.EscapeTokenizer
            r13.<init>(r1)
            r14 = 0
            r15 = 0
            r22 = r6
            r6 = r0
            r0 = r14
            r14 = r22
        L_0x0036:
            boolean r16 = r13.hasMoreTokens()
            r9 = 0
            if (r16 == 0) goto L_0x027b
            java.lang.String r10 = r13.nextToken()
            int r17 = r10.length()
            if (r17 == 0) goto L_0x026c
            char r1 = r10.charAt(r9)
            if (r1 != r7) goto L_0x0266
            java.lang.String r1 = "}"
            boolean r1 = r10.endsWith(r1)
            if (r1 == 0) goto L_0x024a
            int r1 = r10.length()
            r9 = 2
            if (r1 <= r9) goto L_0x00ac
            int r1 = r10.indexOf(r7, r9)
            r9 = -1
            if (r1 == r9) goto L_0x00a5
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r17 = r1
            r1 = 1
            r7 = 0
            java.lang.String r7 = r10.substring(r7, r1)
            r9.<init>(r7)
            r7 = r9
            int r9 = r10.length()
            int r9 = r9 - r1
            java.lang.String r9 = r10.substring(r1, r9)
            java.lang.Object r1 = escapeSQL(r9, r2, r3)
            r9 = 0
            r18 = r8
            boolean r8 = r1 instanceof java.lang.String
            if (r8 == 0) goto L_0x008a
            r8 = r1
            java.lang.String r8 = (java.lang.String) r8
            goto L_0x0097
        L_0x008a:
            r8 = r1
            com.mysql.jdbc.EscapeProcessorResult r8 = (com.mysql.jdbc.EscapeProcessorResult) r8
            java.lang.String r8 = r8.escapedSql
            r9 = 1
            if (r0 == r9) goto L_0x0097
            r9 = r1
            com.mysql.jdbc.EscapeProcessorResult r9 = (com.mysql.jdbc.EscapeProcessorResult) r9
            byte r0 = r9.usesVariables
        L_0x0097:
            r7.append(r8)
            r9 = 125(0x7d, float:1.75E-43)
            r7.append(r9)
            java.lang.String r10 = r7.toString()
            r1 = r0
            goto L_0x00b1
        L_0x00a5:
            r17 = r1
            r18 = r8
            r9 = 125(0x7d, float:1.75E-43)
            goto L_0x00b0
        L_0x00ac:
            r18 = r8
            r9 = 125(0x7d, float:1.75E-43)
        L_0x00b0:
            r1 = r0
        L_0x00b1:
            java.lang.String r7 = removeWhitespace(r10)
            java.lang.String r0 = "{escape"
            boolean r0 = com.mysql.jdbc.StringUtils.startsWithIgnoreCase(r7, r0)
            r8 = 3
            if (r0 == 0) goto L_0x00ed
            java.util.StringTokenizer r0 = new java.util.StringTokenizer     // Catch:{ NoSuchElementException -> 0x00e5 }
            java.lang.String r9 = " '"
            r0.<init>(r10, r9)     // Catch:{ NoSuchElementException -> 0x00e5 }
            r0.nextToken()     // Catch:{ NoSuchElementException -> 0x00e5 }
            java.lang.String r9 = r0.nextToken()     // Catch:{ NoSuchElementException -> 0x00e5 }
            r14 = r9
            int r9 = r14.length()     // Catch:{ NoSuchElementException -> 0x00e5 }
            if (r9 >= r8) goto L_0x00d8
            r12.append(r10)     // Catch:{ NoSuchElementException -> 0x00e5 }
            goto L_0x00e9
        L_0x00d8:
            int r8 = r14.length()     // Catch:{ NoSuchElementException -> 0x00e5 }
            r9 = 1
            int r8 = r8 - r9
            java.lang.String r8 = r14.substring(r9, r8)     // Catch:{ NoSuchElementException -> 0x00e5 }
            r6 = 1
            r14 = r8
            goto L_0x00e9
        L_0x00e5:
            r0 = move-exception
            r12.append(r10)
        L_0x00e9:
            r17 = r1
            goto L_0x0247
        L_0x00ed:
            java.lang.String r0 = "{fn"
            boolean r0 = com.mysql.jdbc.StringUtils.startsWithIgnoreCase(r7, r0)
            if (r0 == 0) goto L_0x0124
            java.lang.String r0 = r10.toLowerCase()
            java.lang.String r9 = "fn "
            int r0 = r0.indexOf(r9)
            int r0 = r0 + r8
            int r8 = r10.length()
            r9 = 1
            int r8 = r8 - r9
            java.lang.String r9 = r10.substring(r0, r8)
            r16 = r0
            java.lang.String r0 = "convert"
            boolean r0 = com.mysql.jdbc.StringUtils.startsWithIgnoreCaseAndWs((java.lang.String) r9, (java.lang.String) r0)
            if (r0 == 0) goto L_0x011d
            java.lang.String r0 = processConvertToken(r9, r2, r3)
            r12.append(r0)
            goto L_0x0120
        L_0x011d:
            r12.append(r9)
        L_0x0120:
            r17 = r1
            goto L_0x0247
        L_0x0124:
            java.lang.String r0 = "{d"
            boolean r0 = com.mysql.jdbc.StringUtils.startsWithIgnoreCase(r7, r0)
            if (r0 == 0) goto L_0x01b8
            r0 = 39
            int r8 = r10.indexOf(r0)
            r9 = 1
            int r8 = r8 + r9
            int r9 = r10.lastIndexOf(r0)
            r17 = r1
            r1 = -1
            if (r8 == r1) goto L_0x01b3
            if (r9 != r1) goto L_0x0141
            goto L_0x01b3
        L_0x0141:
            java.lang.String r1 = r10.substring(r8, r9)
            java.util.StringTokenizer r0 = new java.util.StringTokenizer     // Catch:{ NoSuchElementException -> 0x0190 }
            java.lang.String r2 = " -"
            r0.<init>(r1, r2)     // Catch:{ NoSuchElementException -> 0x0190 }
            java.lang.String r2 = r0.nextToken()     // Catch:{ NoSuchElementException -> 0x0190 }
            java.lang.String r16 = r0.nextToken()     // Catch:{ NoSuchElementException -> 0x0190 }
            r19 = r16
            java.lang.String r16 = r0.nextToken()     // Catch:{ NoSuchElementException -> 0x0190 }
            r20 = r16
            r16 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ NoSuchElementException -> 0x0190 }
            r0.<init>()     // Catch:{ NoSuchElementException -> 0x0190 }
            java.lang.StringBuilder r0 = r0.append(r5)     // Catch:{ NoSuchElementException -> 0x0190 }
            java.lang.StringBuilder r0 = r0.append(r2)     // Catch:{ NoSuchElementException -> 0x0190 }
            java.lang.StringBuilder r0 = r0.append(r4)     // Catch:{ NoSuchElementException -> 0x0190 }
            r21 = r2
            r2 = r19
            java.lang.StringBuilder r0 = r0.append(r2)     // Catch:{ NoSuchElementException -> 0x0190 }
            java.lang.StringBuilder r0 = r0.append(r4)     // Catch:{ NoSuchElementException -> 0x0190 }
            r19 = r2
            r2 = r20
            java.lang.StringBuilder r0 = r0.append(r2)     // Catch:{ NoSuchElementException -> 0x0190 }
            java.lang.StringBuilder r0 = r0.append(r5)     // Catch:{ NoSuchElementException -> 0x0190 }
            java.lang.String r0 = r0.toString()     // Catch:{ NoSuchElementException -> 0x0190 }
            r12.append(r0)     // Catch:{ NoSuchElementException -> 0x0190 }
            goto L_0x01b6
        L_0x0190:
            r0 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "Syntax error for DATE escape sequence '"
            java.lang.StringBuilder r2 = r2.append(r4)
            java.lang.StringBuilder r2 = r2.append(r1)
            java.lang.StringBuilder r2 = r2.append(r5)
            java.lang.String r2 = r2.toString()
            com.mysql.jdbc.ExceptionInterceptor r4 = r25.getExceptionInterceptor()
            java.lang.String r5 = "42000"
            java.sql.SQLException r2 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r2, (java.lang.String) r5, (com.mysql.jdbc.ExceptionInterceptor) r4)
            throw r2
        L_0x01b3:
            r12.append(r10)
        L_0x01b6:
            goto L_0x0247
        L_0x01b8:
            r17 = r1
            java.lang.String r0 = "{ts"
            boolean r0 = com.mysql.jdbc.StringUtils.startsWithIgnoreCase(r7, r0)
            if (r0 == 0) goto L_0x01c8
            processTimestampToken(r3, r12, r10)
            goto L_0x0247
        L_0x01c8:
            java.lang.String r0 = "{t"
            boolean r0 = com.mysql.jdbc.StringUtils.startsWithIgnoreCase(r7, r0)
            if (r0 == 0) goto L_0x01d6
            processTimeToken(r3, r12, r10)
            goto L_0x0247
        L_0x01d6:
            java.lang.String r0 = "{call"
            boolean r0 = com.mysql.jdbc.StringUtils.startsWithIgnoreCase(r7, r0)
            java.lang.String r1 = "{?=call"
            if (r0 != 0) goto L_0x01fa
            boolean r0 = com.mysql.jdbc.StringUtils.startsWithIgnoreCase(r7, r1)
            if (r0 == 0) goto L_0x01e9
            goto L_0x01fa
        L_0x01e9:
            java.lang.String r0 = "{oj"
            boolean r0 = com.mysql.jdbc.StringUtils.startsWithIgnoreCase(r7, r0)
            if (r0 == 0) goto L_0x01f6
            r12.append(r10)
            goto L_0x0247
        L_0x01f6:
            r12.append(r10)
            goto L_0x0247
        L_0x01fa:
            java.lang.String r0 = "CALL"
            int r0 = com.mysql.jdbc.StringUtils.indexOfIgnoreCase(r10, r0)
            int r0 = r0 + 5
            int r2 = r10.length()
            r8 = 1
            int r2 = r2 - r8
            boolean r1 = com.mysql.jdbc.StringUtils.startsWithIgnoreCase(r7, r1)
            if (r1 == 0) goto L_0x021d
            r1 = 1
            java.lang.String r8 = "SELECT "
            r12.append(r8)
            java.lang.String r8 = r10.substring(r0, r2)
            r12.append(r8)
            r15 = r1
            goto L_0x022b
        L_0x021d:
            r1 = 0
            java.lang.String r8 = "CALL "
            r12.append(r8)
            java.lang.String r8 = r10.substring(r0, r2)
            r12.append(r8)
            r15 = r1
        L_0x022b:
            int r1 = r2 + -1
        L_0x022d:
            if (r1 < r0) goto L_0x0246
            char r8 = r10.charAt(r1)
            boolean r9 = java.lang.Character.isWhitespace(r8)
            if (r9 == 0) goto L_0x023d
            int r1 = r1 + -1
            goto L_0x022d
        L_0x023d:
            r9 = 41
            if (r8 == r9) goto L_0x0246
            java.lang.String r9 = "()"
            r12.append(r9)
        L_0x0246:
        L_0x0247:
            r0 = r17
            goto L_0x026e
        L_0x024a:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Not a valid escape sequence: "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.StringBuilder r1 = r1.append(r10)
            java.lang.String r1 = r1.toString()
            com.mysql.jdbc.ExceptionInterceptor r2 = r25.getExceptionInterceptor()
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException(r1, r2)
            throw r1
        L_0x0266:
            r18 = r8
            r12.append(r10)
            goto L_0x026e
        L_0x026c:
            r18 = r8
        L_0x026e:
            r1 = r23
            r2 = r24
            r8 = r18
            r7 = 123(0x7b, float:1.72E-43)
            r9 = 125(0x7d, float:1.75E-43)
            r10 = -1
            goto L_0x0036
        L_0x027b:
            r18 = r8
            java.lang.String r1 = r12.toString()
            if (r6 == 0) goto L_0x02b7
            r2 = r1
        L_0x0284:
            int r4 = r2.indexOf(r14)
            r5 = -1
            if (r4 == r5) goto L_0x02b6
            int r4 = r2.indexOf(r14)
            r7 = 0
            java.lang.String r8 = r2.substring(r7, r4)
            int r7 = r4 + 1
            int r9 = r2.length()
            java.lang.String r7 = r2.substring(r7, r9)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.StringBuilder r9 = r9.append(r8)
            java.lang.String r10 = "\\"
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.StringBuilder r9 = r9.append(r7)
            java.lang.String r2 = r9.toString()
            goto L_0x0284
        L_0x02b6:
            r1 = r2
        L_0x02b7:
            com.mysql.jdbc.EscapeProcessorResult r2 = new com.mysql.jdbc.EscapeProcessorResult
            r2.<init>()
            r2.escapedSql = r1
            r2.callingStoredFunction = r15
            r4 = 1
            if (r0 == r4) goto L_0x02cf
            boolean r5 = r13.sawVariableUse()
            if (r5 == 0) goto L_0x02cc
            r2.usesVariables = r4
            goto L_0x02cf
        L_0x02cc:
            r4 = 0
            r2.usesVariables = r4
        L_0x02cf:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.EscapeProcessor.escapeSQL(java.lang.String, boolean, com.mysql.jdbc.MySQLConnection):java.lang.Object");
    }

    private static void processTimeToken(MySQLConnection conn, StringBuilder newSql, String token) throws SQLException {
        StringBuilder sb = newSql;
        String str = token;
        int startPos = str.indexOf(39) + 1;
        int endPos = str.lastIndexOf(39);
        if (startPos == -1) {
        } else if (endPos == -1) {
            int i = endPos;
        } else {
            String argument = str.substring(startPos, endPos);
            try {
                StringTokenizer st = new StringTokenizer(argument, " :.");
                String hour = st.nextToken();
                String minute = st.nextToken();
                String second = st.nextToken();
                boolean serverSupportsFractionalSecond = false;
                String fractionalSecond = "";
                if (st.hasMoreTokens()) {
                    int i2 = endPos;
                    try {
                        if (conn.versionMeetsMinimum(5, 6, 4)) {
                            serverSupportsFractionalSecond = true;
                            fractionalSecond = "." + st.nextToken();
                        }
                    } catch (NumberFormatException e) {
                        throw SQLError.createSQLException("Syntax error in TIMESTAMP escape sequence '" + str + "'.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, conn.getExceptionInterceptor());
                    } catch (NoSuchElementException e2) {
                        throw SQLError.createSQLException("Syntax error for escape sequence '" + argument + "'", SQLError.SQL_STATE_SYNTAX_ERROR, conn.getExceptionInterceptor());
                    }
                } else {
                    MySQLConnection mySQLConnection = conn;
                }
                if (conn.getUseTimezone()) {
                    if (conn.getUseLegacyDatetimeCode()) {
                        Calendar sessionCalendar = conn.getCalendarInstanceForSessionOrNew();
                        int hourInt = Integer.parseInt(hour);
                        Time inServerTimezone = TimeUtil.changeTimezone(conn, sessionCalendar, (Calendar) null, TimeUtil.fastTimeCreate(sessionCalendar, hourInt, Integer.parseInt(minute), Integer.parseInt(second), conn.getExceptionInterceptor()), sessionCalendar.getTimeZone(), conn.getServerTimezoneTZ(), false);
                        sb.append("'");
                        int i3 = hourInt;
                        sb.append(inServerTimezone.toString());
                        if (serverSupportsFractionalSecond) {
                            sb.append(fractionalSecond);
                        }
                        sb.append("'");
                        return;
                    }
                }
                sb.append("'");
                sb.append(hour);
                sb.append(":");
                sb.append(minute);
                sb.append(":");
                sb.append(second);
                sb.append(fractionalSecond);
                sb.append("'");
                return;
            } catch (NoSuchElementException e3) {
                int i4 = endPos;
                throw SQLError.createSQLException("Syntax error for escape sequence '" + argument + "'", SQLError.SQL_STATE_SYNTAX_ERROR, conn.getExceptionInterceptor());
            }
        }
        newSql.append(token);
    }

    /* JADX WARNING: Removed duplicated region for block: B:44:0x00ee A[SYNTHETIC, Splitter:B:44:0x00ee] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0163  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0180  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x01c9 A[Catch:{ NumberFormatException -> 0x01de }] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01d4 A[Catch:{ NumberFormatException -> 0x01de }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void processTimestampToken(com.mysql.jdbc.MySQLConnection r39, java.lang.StringBuilder r40, java.lang.String r41) throws java.sql.SQLException {
        /*
            r8 = r39
            r9 = r40
            r10 = r41
            java.lang.String r0 = ":"
            java.lang.String r1 = "-"
            java.lang.String r11 = "42000"
            java.lang.String r12 = "Syntax error for TIMESTAMP escape sequence '"
            java.lang.String r13 = "'"
            r2 = 39
            int r3 = r10.indexOf(r2)
            r4 = 1
            int r14 = r3 + 1
            int r15 = r10.lastIndexOf(r2)
            r7 = -1
            if (r14 == r7) goto L_0x026f
            if (r15 != r7) goto L_0x0024
            goto L_0x026f
        L_0x0024:
            java.lang.String r6 = r10.substring(r14, r15)
            boolean r3 = r39.getUseLegacyDatetimeCode()     // Catch:{ IllegalArgumentException -> 0x024c }
            r5 = 4
            r7 = 5
            r2 = 0
            r4 = 6
            if (r3 != 0) goto L_0x007f
            java.sql.Timestamp r0 = java.sql.Timestamp.valueOf(r6)     // Catch:{ IllegalArgumentException -> 0x007b }
            boolean r1 = r39.isServerTruncatesFracSecs()     // Catch:{ IllegalArgumentException -> 0x007b }
            if (r1 != 0) goto L_0x003e
            r1 = 1
            goto L_0x003f
        L_0x003e:
            r1 = 0
        L_0x003f:
            java.sql.Timestamp r1 = com.mysql.jdbc.TimeUtil.adjustTimestampNanosPrecision(r0, r4, r1)     // Catch:{ IllegalArgumentException -> 0x007b }
            r0 = r1
            java.lang.String r1 = "''yyyy-MM-dd HH:mm:ss"
            java.util.TimeZone r3 = r39.getServerTimezoneTZ()     // Catch:{ IllegalArgumentException -> 0x007b }
            java.text.SimpleDateFormat r1 = com.mysql.jdbc.TimeUtil.getSimpleDateFormat(r2, r1, r2, r3)     // Catch:{ IllegalArgumentException -> 0x007b }
            java.lang.String r2 = r1.format(r0)     // Catch:{ IllegalArgumentException -> 0x007b }
            r9.append(r2)     // Catch:{ IllegalArgumentException -> 0x007b }
            int r2 = r0.getNanos()     // Catch:{ IllegalArgumentException -> 0x007b }
            if (r2 <= 0) goto L_0x0072
            boolean r2 = r8.versionMeetsMinimum(r7, r4, r5)     // Catch:{ IllegalArgumentException -> 0x007b }
            if (r2 == 0) goto L_0x0072
            r2 = 46
            r9.append(r2)     // Catch:{ IllegalArgumentException -> 0x007b }
            int r2 = r0.getNanos()     // Catch:{ IllegalArgumentException -> 0x007b }
            r3 = 1
            java.lang.String r2 = com.mysql.jdbc.TimeUtil.formatNanos(r2, r3, r4)     // Catch:{ IllegalArgumentException -> 0x007b }
            r9.append(r2)     // Catch:{ IllegalArgumentException -> 0x007b }
        L_0x0072:
            r2 = 39
            r9.append(r2)     // Catch:{ IllegalArgumentException -> 0x007b }
            r38 = r6
            goto L_0x01dc
        L_0x007b:
            r0 = move-exception
            r2 = r6
            goto L_0x024e
        L_0x007f:
            java.util.StringTokenizer r3 = new java.util.StringTokenizer     // Catch:{ IllegalArgumentException -> 0x024c }
            java.lang.String r2 = " .-:"
            r3.<init>(r6, r2)     // Catch:{ IllegalArgumentException -> 0x024c }
            r19 = r3
            java.lang.String r2 = r19.nextToken()     // Catch:{ NoSuchElementException -> 0x0223, IllegalArgumentException -> 0x0220 }
            r3 = r2
            java.lang.String r2 = r19.nextToken()     // Catch:{ NoSuchElementException -> 0x0223, IllegalArgumentException -> 0x0220 }
            java.lang.String r20 = r19.nextToken()     // Catch:{ NoSuchElementException -> 0x0223, IllegalArgumentException -> 0x0220 }
            r21 = r20
            java.lang.String r20 = r19.nextToken()     // Catch:{ NoSuchElementException -> 0x0223, IllegalArgumentException -> 0x0220 }
            r22 = r20
            java.lang.String r20 = r19.nextToken()     // Catch:{ NoSuchElementException -> 0x0223, IllegalArgumentException -> 0x0220 }
            r23 = r20
            java.lang.String r20 = r19.nextToken()     // Catch:{ NoSuchElementException -> 0x0223, IllegalArgumentException -> 0x0220 }
            r24 = r20
            r20 = 0
            java.lang.String r25 = ""
            boolean r26 = r19.hasMoreTokens()     // Catch:{ NoSuchElementException -> 0x0223, IllegalArgumentException -> 0x0220 }
            java.lang.String r4 = "."
            if (r26 == 0) goto L_0x00e4
            r26 = r6
            r6 = 6
            boolean r5 = r8.versionMeetsMinimum(r7, r6, r5)     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            if (r5 == 0) goto L_0x00e6
            r20 = 1
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            r5.<init>()     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            java.lang.StringBuilder r5 = r5.append(r4)     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            java.lang.String r6 = r19.nextToken()     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            java.lang.String r5 = r5.toString()     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            r25 = r5
            r7 = r25
            goto L_0x00e8
        L_0x00da:
            r0 = move-exception
            r2 = r26
            goto L_0x024e
        L_0x00df:
            r0 = move-exception
            r38 = r26
            goto L_0x0226
        L_0x00e4:
            r26 = r6
        L_0x00e6:
            r7 = r25
        L_0x00e8:
            boolean r5 = r39.getUseTimezone()     // Catch:{ NoSuchElementException -> 0x021c, IllegalArgumentException -> 0x0218 }
            if (r5 != 0) goto L_0x0139
            boolean r5 = r39.getUseJDBCCompliantTimezoneShift()     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            if (r5 != 0) goto L_0x0139
            java.lang.StringBuilder r4 = r9.append(r13)     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            java.lang.StringBuilder r4 = r4.append(r3)     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            java.lang.StringBuilder r4 = r4.append(r1)     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            java.lang.StringBuilder r4 = r4.append(r2)     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            java.lang.StringBuilder r1 = r4.append(r1)     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            r6 = r21
            java.lang.StringBuilder r1 = r1.append(r6)     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            java.lang.String r4 = " "
            java.lang.StringBuilder r1 = r1.append(r4)     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            r5 = r22
            java.lang.StringBuilder r1 = r1.append(r5)     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            java.lang.StringBuilder r1 = r1.append(r0)     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            r4 = r23
            java.lang.StringBuilder r1 = r1.append(r4)     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            java.lang.StringBuilder r0 = r1.append(r0)     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            r1 = r24
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            java.lang.StringBuilder r0 = r0.append(r7)     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            r0.append(r13)     // Catch:{ NoSuchElementException -> 0x00df, IllegalArgumentException -> 0x00da }
            r38 = r26
            goto L_0x01db
        L_0x0139:
            r6 = r21
            r5 = r22
            r21 = r23
            r1 = r24
            java.util.Calendar r29 = r39.getCalendarInstanceForSessionOrNew()     // Catch:{ NoSuchElementException -> 0x021c, IllegalArgumentException -> 0x0218 }
            int r30 = java.lang.Integer.parseInt(r3)     // Catch:{ NumberFormatException -> 0x01e0 }
            int r31 = java.lang.Integer.parseInt(r2)     // Catch:{ NumberFormatException -> 0x01e0 }
            int r32 = java.lang.Integer.parseInt(r6)     // Catch:{ NumberFormatException -> 0x01e0 }
            int r33 = java.lang.Integer.parseInt(r5)     // Catch:{ NumberFormatException -> 0x01e0 }
            int r34 = java.lang.Integer.parseInt(r21)     // Catch:{ NumberFormatException -> 0x01e0 }
            int r35 = java.lang.Integer.parseInt(r1)     // Catch:{ NumberFormatException -> 0x01e0 }
            boolean r0 = r39.getUseGmtMillisForDatetimes()     // Catch:{ NumberFormatException -> 0x01e0 }
            if (r0 == 0) goto L_0x0180
            java.lang.String r17 = "GMT"
            java.util.TimeZone r17 = java.util.TimeZone.getTimeZone(r17)     // Catch:{ NumberFormatException -> 0x0170 }
            java.util.Calendar r17 = java.util.Calendar.getInstance(r17)     // Catch:{ NumberFormatException -> 0x0170 }
            r28 = r17
            goto L_0x0182
        L_0x0170:
            r0 = move-exception
            r25 = r1
            r18 = r2
            r27 = r3
            r17 = r5
            r22 = r6
            r8 = r7
            r38 = r26
            goto L_0x01ee
        L_0x0180:
            r28 = 0
        L_0x0182:
            r36 = 0
            r27 = r0
            java.sql.Timestamp r17 = com.mysql.jdbc.TimeUtil.fastTimestampCreate(r27, r28, r29, r30, r31, r32, r33, r34, r35, r36)     // Catch:{ NumberFormatException -> 0x01e0 }
            r37 = r4
            r4 = r17
            r17 = 0
            java.util.TimeZone r22 = r29.getTimeZone()     // Catch:{ NumberFormatException -> 0x01e0 }
            java.util.TimeZone r23 = r39.getServerTimezoneTZ()     // Catch:{ NumberFormatException -> 0x01e0 }
            r24 = 0
            r25 = r1
            r1 = r39
            r18 = r2
            r2 = r29
            r27 = r3
            r3 = r17
            r17 = r5
            r5 = r22
            r22 = r6
            r38 = r26
            r6 = r23
            r16 = r0
            r8 = r7
            r0 = -1
            r7 = r24
            java.sql.Timestamp r1 = com.mysql.jdbc.TimeUtil.changeTimezone((com.mysql.jdbc.MySQLConnection) r1, (java.util.Calendar) r2, (java.util.Calendar) r3, (java.sql.Timestamp) r4, (java.util.TimeZone) r5, (java.util.TimeZone) r6, (boolean) r7)     // Catch:{ NumberFormatException -> 0x01de }
            r9.append(r13)     // Catch:{ NumberFormatException -> 0x01de }
            java.lang.String r2 = r1.toString()     // Catch:{ NumberFormatException -> 0x01de }
            r3 = r37
            int r3 = r2.indexOf(r3)     // Catch:{ NumberFormatException -> 0x01de }
            if (r3 == r0) goto L_0x01cf
            r0 = 0
            java.lang.String r0 = r2.substring(r0, r3)     // Catch:{ NumberFormatException -> 0x01de }
            r2 = r0
        L_0x01cf:
            r9.append(r2)     // Catch:{ NumberFormatException -> 0x01de }
            if (r20 == 0) goto L_0x01d7
            r9.append(r8)     // Catch:{ NumberFormatException -> 0x01de }
        L_0x01d7:
            r9.append(r13)     // Catch:{ NumberFormatException -> 0x01de }
        L_0x01db:
        L_0x01dc:
            goto L_0x0272
        L_0x01de:
            r0 = move-exception
            goto L_0x01ee
        L_0x01e0:
            r0 = move-exception
            r25 = r1
            r18 = r2
            r27 = r3
            r17 = r5
            r22 = r6
            r8 = r7
            r38 = r26
        L_0x01ee:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ NoSuchElementException -> 0x0216, IllegalArgumentException -> 0x0212 }
            r1.<init>()     // Catch:{ NoSuchElementException -> 0x0216, IllegalArgumentException -> 0x0212 }
            java.lang.String r2 = "Syntax error in TIMESTAMP escape sequence '"
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ NoSuchElementException -> 0x0216, IllegalArgumentException -> 0x0212 }
            java.lang.StringBuilder r1 = r1.append(r10)     // Catch:{ NoSuchElementException -> 0x0216, IllegalArgumentException -> 0x0212 }
            java.lang.String r2 = "'."
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ NoSuchElementException -> 0x0216, IllegalArgumentException -> 0x0212 }
            java.lang.String r1 = r1.toString()     // Catch:{ NoSuchElementException -> 0x0216, IllegalArgumentException -> 0x0212 }
            java.lang.String r2 = "S1009"
            com.mysql.jdbc.ExceptionInterceptor r3 = r39.getExceptionInterceptor()     // Catch:{ NoSuchElementException -> 0x0216, IllegalArgumentException -> 0x0212 }
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r2, (com.mysql.jdbc.ExceptionInterceptor) r3)     // Catch:{ NoSuchElementException -> 0x0216, IllegalArgumentException -> 0x0212 }
            throw r1     // Catch:{ NoSuchElementException -> 0x0216, IllegalArgumentException -> 0x0212 }
        L_0x0212:
            r0 = move-exception
            r2 = r38
            goto L_0x024e
        L_0x0216:
            r0 = move-exception
            goto L_0x0226
        L_0x0218:
            r0 = move-exception
            r2 = r26
            goto L_0x024e
        L_0x021c:
            r0 = move-exception
            r38 = r26
            goto L_0x0226
        L_0x0220:
            r0 = move-exception
            r2 = r6
            goto L_0x024e
        L_0x0223:
            r0 = move-exception
            r38 = r6
        L_0x0226:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ IllegalArgumentException -> 0x0248 }
            r1.<init>()     // Catch:{ IllegalArgumentException -> 0x0248 }
            java.lang.StringBuilder r1 = r1.append(r12)     // Catch:{ IllegalArgumentException -> 0x0248 }
            r2 = r38
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ IllegalArgumentException -> 0x0246 }
            java.lang.StringBuilder r1 = r1.append(r13)     // Catch:{ IllegalArgumentException -> 0x0246 }
            java.lang.String r1 = r1.toString()     // Catch:{ IllegalArgumentException -> 0x0246 }
            com.mysql.jdbc.ExceptionInterceptor r3 = r39.getExceptionInterceptor()     // Catch:{ IllegalArgumentException -> 0x0246 }
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r11, (com.mysql.jdbc.ExceptionInterceptor) r3)     // Catch:{ IllegalArgumentException -> 0x0246 }
            throw r1     // Catch:{ IllegalArgumentException -> 0x0246 }
        L_0x0246:
            r0 = move-exception
            goto L_0x024e
        L_0x0248:
            r0 = move-exception
            r2 = r38
            goto L_0x024e
        L_0x024c:
            r0 = move-exception
            r2 = r6
        L_0x024e:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.StringBuilder r1 = r1.append(r12)
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.StringBuilder r1 = r1.append(r13)
            java.lang.String r1 = r1.toString()
            com.mysql.jdbc.ExceptionInterceptor r3 = r39.getExceptionInterceptor()
            java.sql.SQLException r1 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r1, (java.lang.String) r11, (com.mysql.jdbc.ExceptionInterceptor) r3)
            r1.initCause(r0)
            throw r1
        L_0x026f:
            r40.append(r41)
        L_0x0272:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.EscapeProcessor.processTimestampToken(com.mysql.jdbc.MySQLConnection, java.lang.StringBuilder, java.lang.String):void");
    }

    private static String processConvertToken(String functionToken, boolean serverSupportsConvertFn, MySQLConnection conn) throws SQLException {
        String newType;
        int firstIndexOfParen = functionToken.indexOf("(");
        if (firstIndexOfParen != -1) {
            int indexOfComma = functionToken.lastIndexOf(",");
            if (indexOfComma != -1) {
                int indexOfCloseParen = functionToken.indexOf(41, indexOfComma);
                if (indexOfCloseParen != -1) {
                    String expression = functionToken.substring(firstIndexOfParen + 1, indexOfComma);
                    String type = functionToken.substring(indexOfComma + 1, indexOfCloseParen);
                    String trimmedType = type.trim();
                    if (StringUtils.startsWithIgnoreCase(trimmedType, "SQL_")) {
                        trimmedType = trimmedType.substring(4, trimmedType.length());
                    }
                    if (serverSupportsConvertFn) {
                        newType = JDBC_CONVERT_TO_MYSQL_TYPE_MAP.get(trimmedType.toUpperCase(Locale.ENGLISH));
                    } else {
                        newType = JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP.get(trimmedType.toUpperCase(Locale.ENGLISH));
                        if (newType == null) {
                            throw SQLError.createSQLException("Can't find conversion re-write for type '" + type + "' that is applicable for this server version while processing escape tokens.", SQLError.SQL_STATE_GENERAL_ERROR, conn.getExceptionInterceptor());
                        }
                    }
                    if (newType != null) {
                        int replaceIndex = newType.indexOf("?");
                        if (replaceIndex != -1) {
                            return newType.substring(0, replaceIndex) + expression + newType.substring(replaceIndex + 1, newType.length());
                        }
                        return "CAST(" + expression + " AS " + newType + ")";
                    }
                    throw SQLError.createSQLException("Unsupported conversion type '" + type.trim() + "' found while processing escape token.", SQLError.SQL_STATE_GENERAL_ERROR, conn.getExceptionInterceptor());
                }
                throw SQLError.createSQLException("Syntax error while processing {fn convert (... , ...)} token, missing closing parenthesis in token '" + functionToken + "'.", SQLError.SQL_STATE_SYNTAX_ERROR, conn.getExceptionInterceptor());
            }
            throw SQLError.createSQLException("Syntax error while processing {fn convert (... , ...)} token, missing comma in token '" + functionToken + "'.", SQLError.SQL_STATE_SYNTAX_ERROR, conn.getExceptionInterceptor());
        }
        throw SQLError.createSQLException("Syntax error while processing {fn convert (... , ...)} token, missing opening parenthesis in token '" + functionToken + "'.", SQLError.SQL_STATE_SYNTAX_ERROR, conn.getExceptionInterceptor());
    }

    private static String removeWhitespace(String toCollapse) {
        if (toCollapse == null) {
            return null;
        }
        int length = toCollapse.length();
        StringBuilder collapsed = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = toCollapse.charAt(i);
            if (!Character.isWhitespace(c)) {
                collapsed.append(c);
            }
        }
        return collapsed.toString();
    }
}
