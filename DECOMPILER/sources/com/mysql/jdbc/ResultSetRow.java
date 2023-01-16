package com.mysql.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

public abstract class ResultSetRow {
    protected ExceptionInterceptor exceptionInterceptor;
    protected Field[] metadata;

    public abstract void closeOpenStreams();

    public abstract InputStream getBinaryInputStream(int i) throws SQLException;

    public abstract int getBytesSize();

    public abstract byte[] getColumnValue(int i) throws SQLException;

    public abstract Date getDateFast(int i, MySQLConnection mySQLConnection, ResultSetImpl resultSetImpl, Calendar calendar) throws SQLException;

    public abstract int getInt(int i) throws SQLException;

    public abstract long getLong(int i) throws SQLException;

    public abstract Date getNativeDate(int i, MySQLConnection mySQLConnection, ResultSetImpl resultSetImpl, Calendar calendar) throws SQLException;

    public abstract Object getNativeDateTimeValue(int i, Calendar calendar, int i2, int i3, TimeZone timeZone, boolean z, MySQLConnection mySQLConnection, ResultSetImpl resultSetImpl) throws SQLException;

    public abstract double getNativeDouble(int i) throws SQLException;

    public abstract float getNativeFloat(int i) throws SQLException;

    public abstract int getNativeInt(int i) throws SQLException;

    public abstract long getNativeLong(int i) throws SQLException;

    public abstract short getNativeShort(int i) throws SQLException;

    public abstract Time getNativeTime(int i, Calendar calendar, TimeZone timeZone, boolean z, MySQLConnection mySQLConnection, ResultSetImpl resultSetImpl) throws SQLException;

    public abstract Timestamp getNativeTimestamp(int i, Calendar calendar, TimeZone timeZone, boolean z, MySQLConnection mySQLConnection, ResultSetImpl resultSetImpl) throws SQLException;

    public abstract Reader getReader(int i) throws SQLException;

    public abstract String getString(int i, String str, MySQLConnection mySQLConnection) throws SQLException;

    public abstract Time getTimeFast(int i, Calendar calendar, TimeZone timeZone, boolean z, MySQLConnection mySQLConnection, ResultSetImpl resultSetImpl) throws SQLException;

    public abstract Timestamp getTimestampFast(int i, Calendar calendar, TimeZone timeZone, boolean z, MySQLConnection mySQLConnection, ResultSetImpl resultSetImpl, boolean z2, boolean z3) throws SQLException;

    public abstract boolean isFloatingPointNumber(int i) throws SQLException;

    public abstract boolean isNull(int i) throws SQLException;

    public abstract long length(int i) throws SQLException;

    public abstract void setColumnValue(int i, byte[] bArr) throws SQLException;

    protected ResultSetRow(ExceptionInterceptor exceptionInterceptor2) {
        this.exceptionInterceptor = exceptionInterceptor2;
    }

    /* access modifiers changed from: protected */
    public final Date getDateFast(int columnIndex, byte[] dateAsBytes, int offset, int length, MySQLConnection conn, ResultSetImpl rs, Calendar targetCalendar) throws SQLException {
        int day;
        int month;
        int year;
        int year2;
        byte[] bArr = dateAsBytes;
        int i = offset;
        int length2 = length;
        ResultSetImpl resultSetImpl = rs;
        Calendar calendar = targetCalendar;
        if (bArr == null) {
            return null;
        }
        boolean allZeroDate = true;
        boolean onlyTimePresent = false;
        int i2 = 0;
        while (true) {
            if (i2 >= length2) {
                break;
            }
            try {
                if (bArr[i + i2] == 58) {
                    onlyTimePresent = true;
                    break;
                }
                i2++;
            } catch (SQLException e) {
                sqlEx = e;
                throw sqlEx;
            } catch (Exception e2) {
                e = e2;
                SQLException sqlEx = SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[]{StringUtils.toString(dateAsBytes), Integer.valueOf(columnIndex + 1)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                sqlEx.initCause(e);
                throw sqlEx;
            }
        }
        int i3 = 0;
        while (true) {
            if (i3 < length2) {
                byte b = bArr[i + i3];
                if (b == 32 || b == 45 || b == 47) {
                    onlyTimePresent = false;
                }
                if (b != 48 && b != 32 && b != 58 && b != 45 && b != 47 && b != 46) {
                    allZeroDate = false;
                    break;
                }
                i3++;
            } else {
                break;
            }
        }
        int decimalIndex = -1;
        int i4 = 0;
        while (true) {
            if (i4 >= length2) {
                break;
            }
            int decimalIndex2 = decimalIndex;
            if (bArr[i + i4] == 46) {
                decimalIndex = i4;
                break;
            }
            i4++;
            decimalIndex = decimalIndex2;
        }
        if (decimalIndex > -1) {
            length2 = decimalIndex;
        }
        if (onlyTimePresent || !allZeroDate) {
            int i5 = decimalIndex;
            if (this.metadata[columnIndex].getMysqlType() == 7) {
                switch (length2) {
                    case 2:
                        int year3 = StringUtils.getInt(bArr, i + 0, i + 2);
                        if (year3 <= 69) {
                            year3 += 100;
                        }
                        return resultSetImpl.fastDateCreate(calendar, year3 + MysqlErrorNumbers.ER_SLAVE_SQL_THREAD_MUST_STOP, 1, 1);
                    case 4:
                        int year4 = StringUtils.getInt(bArr, i + 0, i + 4);
                        if (year4 <= 69) {
                            year4 += 100;
                        }
                        return resultSetImpl.fastDateCreate(calendar, year4 + MysqlErrorNumbers.ER_SLAVE_SQL_THREAD_MUST_STOP, StringUtils.getInt(bArr, i + 2, i + 4), 1);
                    case 6:
                    case 10:
                    case 12:
                        int year5 = StringUtils.getInt(bArr, i + 0, i + 2);
                        if (year5 <= 69) {
                            year5 += 100;
                        }
                        return resultSetImpl.fastDateCreate(calendar, year5 + MysqlErrorNumbers.ER_SLAVE_SQL_THREAD_MUST_STOP, StringUtils.getInt(bArr, i + 2, i + 4), StringUtils.getInt(bArr, i + 4, i + 6));
                    case 8:
                    case 14:
                        return resultSetImpl.fastDateCreate(calendar, StringUtils.getInt(bArr, i + 0, i + 4), StringUtils.getInt(bArr, i + 4, i + 6), StringUtils.getInt(bArr, i + 6, i + 8));
                    case 19:
                    case 21:
                    case 29:
                        return resultSetImpl.fastDateCreate(calendar, StringUtils.getInt(bArr, i + 0, i + 4), StringUtils.getInt(bArr, i + 5, i + 7), StringUtils.getInt(bArr, i + 8, i + 10));
                    default:
                        throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[]{StringUtils.toString(dateAsBytes), Integer.valueOf(columnIndex + 1)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                }
            } else if (this.metadata[columnIndex].getMysqlType() == 13) {
                if (length2 != 2) {
                    if (length2 != 1) {
                        year2 = StringUtils.getInt(bArr, i + 0, i + 4);
                        return resultSetImpl.fastDateCreate(calendar, year2, 1, 1);
                    }
                }
                int year6 = StringUtils.getInt(bArr, i, i + length2);
                if (year6 <= 69) {
                    year6 += 100;
                }
                year2 = year6 + MysqlErrorNumbers.ER_SLAVE_SQL_THREAD_MUST_STOP;
                return resultSetImpl.fastDateCreate(calendar, year2, 1, 1);
            } else if (this.metadata[columnIndex].getMysqlType() == 11) {
                return resultSetImpl.fastDateCreate(calendar, 1970, 1, 1);
            } else {
                if (length2 >= 10) {
                    if (length2 != 18) {
                        year = StringUtils.getInt(bArr, i + 0, i + 4);
                        month = StringUtils.getInt(bArr, i + 5, i + 7);
                        day = StringUtils.getInt(bArr, i + 8, i + 10);
                    } else {
                        StringTokenizer st = new StringTokenizer(StringUtils.toString(bArr, i, length2, "ISO8859_1"), "- ");
                        year = Integer.parseInt(st.nextToken());
                        month = Integer.parseInt(st.nextToken());
                        day = Integer.parseInt(st.nextToken());
                    }
                    return resultSetImpl.fastDateCreate(calendar, year, month, day);
                } else if (length2 == 8) {
                    return resultSetImpl.fastDateCreate(calendar, 1970, 1, 1);
                } else {
                    throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[]{StringUtils.toString(dateAsBytes), Integer.valueOf(columnIndex + 1)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                }
            }
        } else {
            try {
                if ("convertToNull".equals(conn.getZeroDateTimeBehavior())) {
                    return null;
                }
                if (!"exception".equals(conn.getZeroDateTimeBehavior())) {
                    return resultSetImpl.fastDateCreate(calendar, 1, 1, 1);
                }
                throw SQLError.createSQLException("Value '" + StringUtils.toString(dateAsBytes) + "' can not be represented as java.sql.Date", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
            } catch (SQLException e3) {
                sqlEx = e3;
                throw sqlEx;
            } catch (Exception e4) {
                e = e4;
                SQLException sqlEx2 = SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Date", new Object[]{StringUtils.toString(dateAsBytes), Integer.valueOf(columnIndex + 1)}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                sqlEx2.initCause(e);
                throw sqlEx2;
            }
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r1v4, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r2v4, types: [byte] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.sql.Date getNativeDate(int r7, byte[] r8, int r9, int r10, com.mysql.jdbc.MySQLConnection r11, com.mysql.jdbc.ResultSetImpl r12, java.util.Calendar r13) throws java.sql.SQLException {
        /*
            r6 = this;
            r0 = 0
            r1 = 0
            r2 = 0
            if (r10 == 0) goto L_0x001d
            int r3 = r9 + 0
            byte r3 = r8[r3]
            r3 = r3 & 255(0xff, float:3.57E-43)
            int r4 = r9 + 1
            byte r4 = r8[r4]
            r4 = r4 & 255(0xff, float:3.57E-43)
            int r4 = r4 << 8
            r0 = r3 | r4
            int r3 = r9 + 2
            byte r1 = r8[r3]
            int r3 = r9 + 3
            byte r2 = r8[r3]
        L_0x001d:
            if (r10 == 0) goto L_0x0025
            if (r0 != 0) goto L_0x0042
            if (r1 != 0) goto L_0x0042
            if (r2 != 0) goto L_0x0042
        L_0x0025:
            java.lang.String r3 = r11.getZeroDateTimeBehavior()
            java.lang.String r4 = "convertToNull"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x0033
            r3 = 0
            return r3
        L_0x0033:
            java.lang.String r3 = r11.getZeroDateTimeBehavior()
            java.lang.String r4 = "exception"
            boolean r3 = r4.equals(r3)
            if (r3 != 0) goto L_0x0058
            r0 = 1
            r1 = 1
            r2 = 1
        L_0x0042:
            boolean r3 = r12.useLegacyDatetimeCode
            if (r3 != 0) goto L_0x004b
            java.sql.Date r3 = com.mysql.jdbc.TimeUtil.fastDateCreate(r0, r1, r2, r13)
            return r3
        L_0x004b:
            if (r13 != 0) goto L_0x0052
            java.util.Calendar r3 = r12.getCalendarInstanceForSessionOrNew()
            goto L_0x0053
        L_0x0052:
            r3 = r13
        L_0x0053:
            java.sql.Date r3 = r12.fastDateCreate(r3, r0, r1, r2)
            return r3
        L_0x0058:
            com.mysql.jdbc.ExceptionInterceptor r3 = r6.exceptionInterceptor
            java.lang.String r4 = "Value '0000-00-00' can not be represented as java.sql.Date"
            java.lang.String r5 = "S1009"
            java.sql.SQLException r3 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r4, (java.lang.String) r5, (com.mysql.jdbc.ExceptionInterceptor) r3)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ResultSetRow.getNativeDate(int, byte[], int, int, com.mysql.jdbc.MySQLConnection, com.mysql.jdbc.ResultSetImpl, java.util.Calendar):java.sql.Date");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r6v14, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r6v9, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r7v14, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r7v9, types: [byte] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object getNativeDateTimeValue(int r30, byte[] r31, int r32, int r33, java.util.Calendar r34, int r35, int r36, java.util.TimeZone r37, boolean r38, com.mysql.jdbc.MySQLConnection r39, com.mysql.jdbc.ResultSetImpl r40) throws java.sql.SQLException {
        /*
            r29 = this;
            r0 = r29
            r1 = r33
            r11 = r34
            r10 = r37
            r9 = r38
            r8 = r40
            r2 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            if (r31 != 0) goto L_0x0017
            return r14
        L_0x0017:
            boolean r3 = r39.getUseJDBCCompliantTimezoneShift()
            if (r3 == 0) goto L_0x0022
            java.util.Calendar r3 = r39.getUtcCalendar()
            goto L_0x0026
        L_0x0022:
            java.util.Calendar r3 = r40.getCalendarInstanceForSessionOrNew()
        L_0x0026:
            r15 = 0
            switch(r36) {
                case 7: goto L_0x00a2;
                case 8: goto L_0x002a;
                case 9: goto L_0x002a;
                case 10: goto L_0x0062;
                case 11: goto L_0x003e;
                case 12: goto L_0x00a2;
                default: goto L_0x002a;
            }
        L_0x002a:
            r18 = r2
            r15 = 0
            r1 = r4
            r22 = r13
            r23 = r15
            r27 = r12
            r12 = r5
            r5 = r27
            r28 = r7
            r7 = r6
            r6 = r28
            goto L_0x0124
        L_0x003e:
            r15 = 1
            if (r1 == 0) goto L_0x004d
            int r16 = r32 + 5
            byte r6 = r31[r16]
            int r16 = r32 + 6
            byte r7 = r31[r16]
            int r16 = r32 + 7
            byte r12 = r31[r16]
        L_0x004d:
            r2 = 1970(0x7b2, float:2.76E-42)
            r4 = 1
            r5 = 1
            r1 = r4
            r22 = r13
            r23 = r15
            r27 = r12
            r12 = r5
            r5 = r27
            r28 = r7
            r7 = r6
            r6 = r28
            goto L_0x0124
        L_0x0062:
            r15 = 1
            if (r1 == 0) goto L_0x008f
            int r16 = r32 + 0
            byte r14 = r31[r16]
            r14 = r14 & 255(0xff, float:3.57E-43)
            int r16 = r32 + 1
            r18 = r2
            byte r2 = r31[r16]
            r2 = r2 & 255(0xff, float:3.57E-43)
            int r2 = r2 << 8
            r2 = r2 | r14
            int r14 = r32 + 2
            byte r4 = r31[r14]
            int r14 = r32 + 3
            byte r5 = r31[r14]
            r1 = r4
            r22 = r13
            r23 = r15
            r27 = r12
            r12 = r5
            r5 = r27
            r28 = r7
            r7 = r6
            r6 = r28
            goto L_0x0124
        L_0x008f:
            r18 = r2
            r1 = r4
            r22 = r13
            r23 = r15
            r27 = r12
            r12 = r5
            r5 = r27
            r28 = r7
            r7 = r6
            r6 = r28
            goto L_0x0124
        L_0x00a2:
            r18 = r2
            r15 = 1
            if (r1 == 0) goto L_0x0113
            int r2 = r32 + 0
            byte r2 = r31[r2]
            r2 = r2 & 255(0xff, float:3.57E-43)
            int r14 = r32 + 1
            byte r14 = r31[r14]
            r14 = r14 & 255(0xff, float:3.57E-43)
            int r14 = r14 << 8
            r2 = r2 | r14
            int r14 = r32 + 2
            byte r4 = r31[r14]
            int r14 = r32 + 3
            byte r5 = r31[r14]
            r14 = 4
            if (r1 <= r14) goto L_0x00cd
            int r14 = r32 + 4
            byte r6 = r31[r14]
            int r14 = r32 + 5
            byte r7 = r31[r14]
            int r14 = r32 + 6
            byte r12 = r31[r14]
        L_0x00cd:
            r14 = 7
            if (r1 <= r14) goto L_0x0103
            int r14 = r32 + 7
            byte r14 = r31[r14]
            r14 = r14 & 255(0xff, float:3.57E-43)
            int r16 = r32 + 8
            byte r1 = r31[r16]
            r1 = r1 & 255(0xff, float:3.57E-43)
            int r1 = r1 << 8
            r1 = r1 | r14
            int r14 = r32 + 9
            byte r14 = r31[r14]
            r14 = r14 & 255(0xff, float:3.57E-43)
            int r14 = r14 << 16
            r1 = r1 | r14
            int r14 = r32 + 10
            byte r14 = r31[r14]
            r14 = r14 & 255(0xff, float:3.57E-43)
            int r14 = r14 << 24
            r1 = r1 | r14
            int r13 = r1 * 1000
            r1 = r4
            r22 = r13
            r23 = r15
            r27 = r12
            r12 = r5
            r5 = r27
            r28 = r7
            r7 = r6
            r6 = r28
            goto L_0x0124
        L_0x0103:
            r1 = r4
            r22 = r13
            r23 = r15
            r27 = r12
            r12 = r5
            r5 = r27
            r28 = r7
            r7 = r6
            r6 = r28
            goto L_0x0124
        L_0x0113:
            r1 = r4
            r22 = r13
            r23 = r15
            r2 = r18
            r27 = r12
            r12 = r5
            r5 = r27
            r28 = r7
            r7 = r6
            r6 = r28
        L_0x0124:
            java.lang.String r4 = "S1009"
            java.lang.String r13 = "exception"
            java.lang.String r14 = "convertToNull"
            switch(r35) {
                case 91: goto L_0x0205;
                case 92: goto L_0x01c5;
                case 93: goto L_0x013b;
                default: goto L_0x012d;
            }
        L_0x012d:
            r15 = r5
            r16 = r12
            r12 = r8
            java.sql.SQLException r4 = new java.sql.SQLException
            java.lang.String r5 = "Internal error - conversion method doesn't support this type"
            java.lang.String r8 = "S1000"
            r4.<init>(r5, r8)
            throw r4
        L_0x013b:
            if (r23 == 0) goto L_0x01bb
            if (r2 != 0) goto L_0x0169
            if (r1 != 0) goto L_0x0169
            if (r12 != 0) goto L_0x0169
            java.lang.String r15 = r39.getZeroDateTimeBehavior()
            boolean r14 = r14.equals(r15)
            if (r14 == 0) goto L_0x014f
            r4 = 0
            return r4
        L_0x014f:
            java.lang.String r14 = r39.getZeroDateTimeBehavior()
            boolean r13 = r13.equals(r14)
            if (r13 != 0) goto L_0x0161
            r2 = 1
            r1 = 1
            r12 = 1
            r24 = r2
            r25 = r12
            goto L_0x016d
        L_0x0161:
            java.sql.SQLException r13 = new java.sql.SQLException
            java.lang.String r14 = "Value '0000-00-00' can not be represented as java.sql.Timestamp"
            r13.<init>(r14, r4)
            throw r13
        L_0x0169:
            r24 = r2
            r25 = r12
        L_0x016d:
            boolean r2 = r8.useLegacyDatetimeCode
            if (r2 != 0) goto L_0x0185
            r12 = r37
            r13 = r24
            r14 = r1
            r15 = r25
            r16 = r7
            r17 = r6
            r18 = r5
            r19 = r22
            java.sql.Timestamp r2 = com.mysql.jdbc.TimeUtil.fastTimestampCreate(r12, r13, r14, r15, r16, r17, r18, r19)
            return r2
        L_0x0185:
            boolean r26 = r39.getUseGmtMillisForDatetimes()
            java.util.Calendar r13 = r40.getCalendarInstanceForSessionOrNew()
            r12 = r40
            r14 = r24
            r15 = r1
            r16 = r25
            r17 = r7
            r18 = r6
            r19 = r5
            r20 = r22
            r21 = r26
            java.sql.Timestamp r12 = r12.fastTimestampCreate(r13, r14, r15, r16, r17, r18, r19, r20, r21)
            java.util.TimeZone r13 = r39.getServerTimezoneTZ()
            r2 = r39
            r4 = r34
            r15 = r5
            r5 = r12
            r14 = r6
            r6 = r13
            r13 = r7
            r7 = r37
            r16 = r12
            r12 = r8
            r8 = r38
            java.sql.Timestamp r2 = com.mysql.jdbc.TimeUtil.changeTimezone((com.mysql.jdbc.MySQLConnection) r2, (java.util.Calendar) r3, (java.util.Calendar) r4, (java.sql.Timestamp) r5, (java.util.TimeZone) r6, (java.util.TimeZone) r7, (boolean) r8)
            return r2
        L_0x01bb:
            r16 = r12
            r12 = r8
            int r4 = r30 + 1
            java.sql.Timestamp r4 = r12.getNativeTimestampViaParseConversion(r4, r11, r10, r9)
            return r4
        L_0x01c5:
            r15 = r5
            r14 = r6
            r13 = r7
            r16 = r12
            r12 = r8
            if (r23 == 0) goto L_0x01fa
            boolean r4 = r12.useLegacyDatetimeCode
            if (r4 != 0) goto L_0x01d8
            com.mysql.jdbc.ExceptionInterceptor r4 = r0.exceptionInterceptor
            java.sql.Time r4 = com.mysql.jdbc.TimeUtil.fastTimeCreate((int) r13, (int) r14, (int) r15, (java.util.Calendar) r11, (com.mysql.jdbc.ExceptionInterceptor) r4)
            return r4
        L_0x01d8:
            java.util.Calendar r4 = r40.getCalendarInstanceForSessionOrNew()
            com.mysql.jdbc.ExceptionInterceptor r5 = r0.exceptionInterceptor
            java.sql.Time r17 = com.mysql.jdbc.TimeUtil.fastTimeCreate((java.util.Calendar) r4, (int) r13, (int) r14, (int) r15, (com.mysql.jdbc.ExceptionInterceptor) r5)
            java.util.TimeZone r8 = r39.getServerTimezoneTZ()
            r4 = r39
            r5 = r3
            r6 = r34
            r7 = r17
            r0 = r9
            r9 = r37
            r18 = r3
            r3 = r10
            r10 = r38
            java.sql.Time r4 = com.mysql.jdbc.TimeUtil.changeTimezone((com.mysql.jdbc.MySQLConnection) r4, (java.util.Calendar) r5, (java.util.Calendar) r6, (java.sql.Time) r7, (java.util.TimeZone) r8, (java.util.TimeZone) r9, (boolean) r10)
            return r4
        L_0x01fa:
            r18 = r3
            r0 = r9
            r3 = r10
            int r4 = r30 + 1
            java.sql.Time r4 = r12.getNativeTimeViaParseConversion(r4, r11, r3, r0)
            return r4
        L_0x0205:
            r18 = r3
            r15 = r5
            r0 = r9
            r3 = r10
            r16 = r12
            r12 = r8
            r27 = r7
            r7 = r6
            r6 = r27
            if (r23 == 0) goto L_0x0250
            if (r2 != 0) goto L_0x023c
            if (r1 != 0) goto L_0x023c
            if (r16 != 0) goto L_0x023c
            java.lang.String r5 = r39.getZeroDateTimeBehavior()
            boolean r5 = r14.equals(r5)
            if (r5 == 0) goto L_0x0226
            r4 = 0
            return r4
        L_0x0226:
            java.lang.String r5 = r39.getZeroDateTimeBehavior()
            boolean r5 = r13.equals(r5)
            if (r5 != 0) goto L_0x0234
            r2 = 1
            r1 = 1
            r4 = 1
            goto L_0x023e
        L_0x0234:
            java.sql.SQLException r5 = new java.sql.SQLException
            java.lang.String r8 = "Value '0000-00-00' can not be represented as java.sql.Date"
            r5.<init>(r8, r4)
            throw r5
        L_0x023c:
            r4 = r16
        L_0x023e:
            boolean r5 = r12.useLegacyDatetimeCode
            if (r5 != 0) goto L_0x0247
            java.sql.Date r5 = com.mysql.jdbc.TimeUtil.fastDateCreate(r2, r1, r4, r11)
            return r5
        L_0x0247:
            java.util.Calendar r5 = r40.getCalendarInstanceForSessionOrNew()
            java.sql.Date r5 = r12.fastDateCreate(r5, r2, r1, r4)
            return r5
        L_0x0250:
            int r4 = r30 + 1
            java.sql.Date r4 = r12.getNativeDateViaParseConversion(r4)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ResultSetRow.getNativeDateTimeValue(int, byte[], int, int, java.util.Calendar, int, int, java.util.TimeZone, boolean, com.mysql.jdbc.MySQLConnection, com.mysql.jdbc.ResultSetImpl):java.lang.Object");
    }

    /* access modifiers changed from: protected */
    public double getNativeDouble(byte[] bits, int offset) {
        return Double.longBitsToDouble(((long) (bits[offset + 0] & 255)) | (((long) (bits[offset + 1] & 255)) << 8) | (((long) (bits[offset + 2] & 255)) << 16) | (((long) (bits[offset + 3] & 255)) << 24) | (((long) (bits[offset + 4] & 255)) << 32) | (((long) (bits[offset + 5] & 255)) << 40) | (((long) (bits[offset + 6] & 255)) << 48) | (((long) (bits[offset + 7] & 255)) << 56));
    }

    /* access modifiers changed from: protected */
    public float getNativeFloat(byte[] bits, int offset) {
        return Float.intBitsToFloat((bits[offset + 0] & 255) | ((bits[offset + 1] & 255) << 8) | ((bits[offset + 2] & 255) << 16) | ((bits[offset + 3] & 255) << 24));
    }

    /* access modifiers changed from: protected */
    public int getNativeInt(byte[] bits, int offset) {
        return (bits[offset + 0] & 255) | ((bits[offset + 1] & 255) << 8) | ((bits[offset + 2] & 255) << 16) | ((bits[offset + 3] & 255) << 24);
    }

    /* access modifiers changed from: protected */
    public long getNativeLong(byte[] bits, int offset) {
        return ((long) (bits[offset + 0] & 255)) | (((long) (bits[offset + 1] & 255)) << 8) | (((long) (bits[offset + 2] & 255)) << 16) | (((long) (bits[offset + 3] & 255)) << 24) | (((long) (bits[offset + 4] & 255)) << 32) | (((long) (bits[offset + 5] & 255)) << 40) | (((long) (bits[offset + 6] & 255)) << 48) | (((long) (bits[offset + 7] & 255)) << 56);
    }

    /* access modifiers changed from: protected */
    public short getNativeShort(byte[] bits, int offset) {
        return (short) ((bits[offset + 0] & 255) | ((bits[offset + 1] & 255) << 8));
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r1v2, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r2v2, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r3v2, types: [byte] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.sql.Time getNativeTime(int r16, byte[] r17, int r18, int r19, java.util.Calendar r20, java.util.TimeZone r21, boolean r22, com.mysql.jdbc.MySQLConnection r23, com.mysql.jdbc.ResultSetImpl r24) throws java.sql.SQLException {
        /*
            r15 = this;
            r0 = r15
            r1 = 0
            r2 = 0
            r3 = 0
            if (r19 == 0) goto L_0x0012
            int r4 = r18 + 5
            byte r1 = r17[r4]
            int r4 = r18 + 6
            byte r2 = r17[r4]
            int r4 = r18 + 7
            byte r3 = r17[r4]
        L_0x0012:
            r4 = r24
            boolean r5 = r4.useLegacyDatetimeCode
            if (r5 != 0) goto L_0x0021
            com.mysql.jdbc.ExceptionInterceptor r5 = r0.exceptionInterceptor
            r13 = r20
            java.sql.Time r5 = com.mysql.jdbc.TimeUtil.fastTimeCreate((int) r1, (int) r2, (int) r3, (java.util.Calendar) r13, (com.mysql.jdbc.ExceptionInterceptor) r5)
            return r5
        L_0x0021:
            r13 = r20
            java.util.Calendar r5 = r24.getCalendarInstanceForSessionOrNew()
            com.mysql.jdbc.ExceptionInterceptor r6 = r0.exceptionInterceptor
            java.sql.Time r14 = com.mysql.jdbc.TimeUtil.fastTimeCreate((java.util.Calendar) r5, (int) r1, (int) r2, (int) r3, (com.mysql.jdbc.ExceptionInterceptor) r6)
            java.util.TimeZone r10 = r23.getServerTimezoneTZ()
            r6 = r23
            r7 = r5
            r8 = r20
            r9 = r14
            r11 = r21
            r12 = r22
            java.sql.Time r6 = com.mysql.jdbc.TimeUtil.changeTimezone((com.mysql.jdbc.MySQLConnection) r6, (java.util.Calendar) r7, (java.util.Calendar) r8, (java.sql.Time) r9, (java.util.TimeZone) r10, (java.util.TimeZone) r11, (boolean) r12)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ResultSetRow.getNativeTime(int, byte[], int, int, java.util.Calendar, java.util.TimeZone, boolean, com.mysql.jdbc.MySQLConnection, com.mysql.jdbc.ResultSetImpl):java.sql.Time");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r4v17, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r5v8, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r6v6, types: [byte] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.sql.Timestamp getNativeTimestamp(byte[] r27, int r28, int r29, java.util.Calendar r30, java.util.TimeZone r31, boolean r32, com.mysql.jdbc.MySQLConnection r33, com.mysql.jdbc.ResultSetImpl r34) throws java.sql.SQLException {
        /*
            r26 = this;
            r0 = r29
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            if (r0 == 0) goto L_0x0066
            int r8 = r28 + 0
            byte r8 = r27[r8]
            r8 = r8 & 255(0xff, float:3.57E-43)
            int r9 = r28 + 1
            byte r9 = r27[r9]
            r9 = r9 & 255(0xff, float:3.57E-43)
            int r9 = r9 << 8
            r1 = r8 | r9
            int r8 = r28 + 2
            byte r2 = r27[r8]
            int r8 = r28 + 3
            byte r3 = r27[r8]
            r8 = 4
            if (r0 <= r8) goto L_0x0032
            int r8 = r28 + 4
            byte r4 = r27[r8]
            int r8 = r28 + 5
            byte r5 = r27[r8]
            int r8 = r28 + 6
            byte r6 = r27[r8]
        L_0x0032:
            r8 = 7
            if (r0 <= r8) goto L_0x005f
            int r8 = r28 + 7
            byte r8 = r27[r8]
            r8 = r8 & 255(0xff, float:3.57E-43)
            int r9 = r28 + 8
            byte r9 = r27[r9]
            r9 = r9 & 255(0xff, float:3.57E-43)
            int r9 = r9 << 8
            r8 = r8 | r9
            int r9 = r28 + 9
            byte r9 = r27[r9]
            r9 = r9 & 255(0xff, float:3.57E-43)
            int r9 = r9 << 16
            r8 = r8 | r9
            int r9 = r28 + 10
            byte r9 = r27[r9]
            r9 = r9 & 255(0xff, float:3.57E-43)
            int r9 = r9 << 24
            r8 = r8 | r9
            int r7 = r8 * 1000
            r14 = r4
            r15 = r5
            r16 = r6
            r17 = r7
            goto L_0x006c
        L_0x005f:
            r14 = r4
            r15 = r5
            r16 = r6
            r17 = r7
            goto L_0x006c
        L_0x0066:
            r14 = r4
            r15 = r5
            r16 = r6
            r17 = r7
        L_0x006c:
            if (r0 == 0) goto L_0x0074
            if (r1 != 0) goto L_0x0091
            if (r2 != 0) goto L_0x0091
            if (r3 != 0) goto L_0x0091
        L_0x0074:
            java.lang.String r4 = r33.getZeroDateTimeBehavior()
            java.lang.String r5 = "convertToNull"
            boolean r4 = r5.equals(r4)
            if (r4 == 0) goto L_0x0082
            r4 = 0
            return r4
        L_0x0082:
            java.lang.String r4 = r33.getZeroDateTimeBehavior()
            java.lang.String r5 = "exception"
            boolean r4 = r5.equals(r4)
            if (r4 != 0) goto L_0x00e1
            r1 = 1
            r2 = 1
            r3 = 1
        L_0x0091:
            r13 = r34
            boolean r4 = r13.useLegacyDatetimeCode
            if (r4 != 0) goto L_0x00a7
            r4 = r31
            r5 = r1
            r6 = r2
            r7 = r3
            r8 = r14
            r9 = r15
            r10 = r16
            r11 = r17
            java.sql.Timestamp r4 = com.mysql.jdbc.TimeUtil.fastTimestampCreate(r4, r5, r6, r7, r8, r9, r10, r11)
            return r4
        L_0x00a7:
            boolean r18 = r33.getUseGmtMillisForDatetimes()
            boolean r4 = r33.getUseJDBCCompliantTimezoneShift()
            if (r4 == 0) goto L_0x00b6
            java.util.Calendar r4 = r33.getUtcCalendar()
            goto L_0x00ba
        L_0x00b6:
            java.util.Calendar r4 = r34.getCalendarInstanceForSessionOrNew()
        L_0x00ba:
            r5 = r4
            r4 = r34
            r6 = r1
            r7 = r2
            r8 = r3
            r9 = r14
            r10 = r15
            r11 = r16
            r12 = r17
            r13 = r18
            java.sql.Timestamp r4 = r4.fastTimestampCreate(r5, r6, r7, r8, r9, r10, r11, r12, r13)
            java.util.TimeZone r23 = r33.getServerTimezoneTZ()
            r19 = r33
            r20 = r5
            r21 = r30
            r22 = r4
            r24 = r31
            r25 = r32
            java.sql.Timestamp r6 = com.mysql.jdbc.TimeUtil.changeTimezone((com.mysql.jdbc.MySQLConnection) r19, (java.util.Calendar) r20, (java.util.Calendar) r21, (java.sql.Timestamp) r22, (java.util.TimeZone) r23, (java.util.TimeZone) r24, (boolean) r25)
            return r6
        L_0x00e1:
            r4 = r26
            com.mysql.jdbc.ExceptionInterceptor r5 = r4.exceptionInterceptor
            java.lang.String r6 = "Value '0000-00-00' can not be represented as java.sql.Timestamp"
            java.lang.String r7 = "S1009"
            java.sql.SQLException r5 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r6, (java.lang.String) r7, (com.mysql.jdbc.ExceptionInterceptor) r5)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ResultSetRow.getNativeTimestamp(byte[], int, int, java.util.Calendar, java.util.TimeZone, boolean, com.mysql.jdbc.MySQLConnection, com.mysql.jdbc.ResultSetImpl):java.sql.Timestamp");
    }

    /* access modifiers changed from: protected */
    public String getString(String encoding, MySQLConnection conn, byte[] value, int offset, int length) throws SQLException {
        if (conn == null || !conn.getUseUnicode()) {
            return StringUtils.toAsciiString(value, offset, length);
        }
        if (encoding == null) {
            try {
                return StringUtils.toString(value);
            } catch (UnsupportedEncodingException e) {
                throw SQLError.createSQLException(Messages.getString("ResultSet.Unsupported_character_encoding____101") + encoding + "'.", "0S100", this.exceptionInterceptor);
            }
        } else {
            SingleByteCharsetConverter converter = conn.getCharsetConverter(encoding);
            if (converter != null) {
                return converter.toString(value, offset, length);
            }
            return StringUtils.toString(value, offset, length, encoding);
        }
    }

    /* access modifiers changed from: protected */
    public Time getTimeFast(int columnIndex, byte[] timeAsBytes, int offset, int fullLength, Calendar targetCalendar, TimeZone tz, boolean rollForward, MySQLConnection conn, ResultSetImpl rs) throws SQLException {
        boolean z;
        int decimalIndex;
        boolean allZeroTime;
        boolean onlyTimePresent;
        boolean z2;
        boolean z3;
        int nanos;
        int length;
        int nanos2;
        int min;
        int sec;
        int hr;
        int min2;
        int hr2;
        int hr3;
        int i = columnIndex;
        byte[] bArr = timeAsBytes;
        int i2 = fullLength;
        Calendar targetCalendar2 = targetCalendar;
        ResultSetImpl resultSetImpl = rs;
        boolean z4 = false;
        if (bArr == null) {
            return null;
        }
        boolean onlyTimePresent2 = false;
        int i3 = 0;
        while (true) {
            if (i3 >= i2) {
                z = z4;
                break;
            }
            try {
                z = z4;
                if (bArr[offset + i3] == 58) {
                    onlyTimePresent2 = true;
                    break;
                }
                i3++;
                z4 = z;
            } catch (RuntimeException e) {
                ex = e;
                boolean z5 = z4;
                SQLException sqlEx = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                sqlEx.initCause(ex);
                throw sqlEx;
            }
        }
        int i4 = 0;
        while (true) {
            if (i4 >= i2) {
                decimalIndex = -1;
                break;
            }
            try {
                if (bArr[offset + i4] == 46) {
                    decimalIndex = i4;
                    break;
                }
                i4++;
            } catch (RuntimeException e2) {
                ex = e2;
                boolean z6 = z;
                SQLException sqlEx2 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                sqlEx2.initCause(ex);
                throw sqlEx2;
            }
        }
        int i5 = 0;
        while (true) {
            if (i5 >= i2) {
                allZeroTime = true;
                onlyTimePresent = onlyTimePresent2;
                break;
            }
            try {
                byte b = bArr[offset + i5];
                if (b == 32 || b == 45 || b == 47) {
                    onlyTimePresent2 = false;
                }
                if (b != 48 && b != 32 && b != 58 && b != 45 && b != 47) {
                    if (b != 46) {
                        allZeroTime = false;
                        onlyTimePresent = onlyTimePresent2;
                        break;
                    }
                }
                i5++;
            } catch (RuntimeException e3) {
                ex = e3;
                int i6 = decimalIndex;
                boolean z7 = z;
                SQLException sqlEx22 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                sqlEx22.initCause(ex);
                throw sqlEx22;
            }
        }
        if (onlyTimePresent || !allZeroTime) {
            try {
                Field timeColField = this.metadata[i];
                int length2 = fullLength;
                if (decimalIndex != -1) {
                    int length3 = decimalIndex;
                    if (decimalIndex + 2 <= i2) {
                        int nanos3 = StringUtils.getInt(bArr, offset + decimalIndex + 1, offset + i2);
                        int numDigits = i2 - (decimalIndex + 1);
                        if (numDigits < 9) {
                            z3 = false;
                            z2 = false;
                            boolean z8 = allZeroTime;
                            int i7 = numDigits;
                            try {
                                nanos3 *= (int) Math.pow(10.0d, (double) (9 - numDigits));
                            } catch (RuntimeException e4) {
                                ex = e4;
                                int i8 = decimalIndex;
                                boolean z9 = z;
                                SQLException sqlEx222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                                sqlEx222.initCause(ex);
                                throw sqlEx222;
                            }
                        } else {
                            int i9 = numDigits;
                            z3 = false;
                            z2 = false;
                        }
                        nanos = nanos3;
                        length = length3;
                    } else {
                        throw new IllegalArgumentException();
                    }
                } else {
                    boolean z10 = allZeroTime;
                    z3 = false;
                    z2 = false;
                    nanos = 0;
                    length = length2;
                }
                try {
                    if (timeColField.getMysqlType() == 7) {
                        switch (length) {
                            case 10:
                                hr2 = StringUtils.getInt(bArr, offset + 6, offset + 8);
                                try {
                                    min2 = StringUtils.getInt(bArr, offset + 8, offset + 10);
                                    sec = 0;
                                    break;
                                } catch (RuntimeException e5) {
                                    ex = e5;
                                    boolean z11 = z3;
                                    int i10 = decimalIndex;
                                    boolean z12 = z2;
                                    int i11 = nanos;
                                    SQLException sqlEx2222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                                    sqlEx2222.initCause(ex);
                                    throw sqlEx2222;
                                }
                            case 12:
                            case 14:
                                hr2 = StringUtils.getInt(bArr, (offset + length) - 6, (offset + length) - 4);
                                min2 = StringUtils.getInt(bArr, (offset + length) - 4, (offset + length) - 2);
                                sec = StringUtils.getInt(bArr, (offset + length) - 2, offset + length);
                                break;
                            case 19:
                                try {
                                    hr2 = StringUtils.getInt(bArr, (offset + length) - 8, (offset + length) - 6);
                                    try {
                                        min2 = StringUtils.getInt(bArr, (offset + length) - 5, (offset + length) - 3);
                                        try {
                                            sec = StringUtils.getInt(bArr, (offset + length) - 2, offset + length);
                                            break;
                                        } catch (RuntimeException e6) {
                                            ex = e6;
                                            int i12 = nanos;
                                            int i13 = decimalIndex;
                                            boolean z13 = z2;
                                            SQLException sqlEx22222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                                            sqlEx22222.initCause(ex);
                                            throw sqlEx22222;
                                        }
                                    } catch (RuntimeException e7) {
                                        ex = e7;
                                        int i14 = nanos;
                                        boolean z14 = z3;
                                        int i15 = decimalIndex;
                                        boolean z15 = z2;
                                        SQLException sqlEx222222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                                        sqlEx222222.initCause(ex);
                                        throw sqlEx222222;
                                    }
                                } catch (RuntimeException e8) {
                                    ex = e8;
                                    int i16 = nanos;
                                    boolean z16 = z3;
                                    int i17 = decimalIndex;
                                    boolean z17 = z;
                                    boolean z18 = z2;
                                    SQLException sqlEx2222222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                                    sqlEx2222222.initCause(ex);
                                    throw sqlEx2222222;
                                }
                            default:
                                nanos2 = nanos;
                                try {
                                    throw SQLError.createSQLException(Messages.getString("ResultSet.Timestamp_too_small_to_convert_to_Time_value_in_column__257") + (i + 1) + "(" + timeColField + ").", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                                } catch (RuntimeException e9) {
                                    ex = e9;
                                    boolean z19 = z3;
                                    int i18 = decimalIndex;
                                    boolean z20 = z;
                                    boolean z21 = z2;
                                    int i19 = nanos2;
                                    break;
                                }
                        }
                        try {
                            nanos2 = nanos;
                            try {
                                hr3 = hr2;
                            } catch (RuntimeException e10) {
                                ex = e10;
                                int i20 = hr2;
                                int i21 = sec;
                                int sec2 = decimalIndex;
                                int i22 = nanos2;
                                SQLException sqlEx22222222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                                sqlEx22222222.initCause(ex);
                                throw sqlEx22222222;
                            }
                        } catch (RuntimeException e11) {
                            ex = e11;
                            int i23 = hr2;
                            int i24 = sec;
                            int sec3 = decimalIndex;
                            int i25 = nanos;
                            SQLException sqlEx222222222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                            sqlEx222222222.initCause(ex);
                            throw sqlEx222222222;
                        }
                        try {
                            new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_TIMESTAMP_to_Time_with_getTime()_on_column__261") + i + "(" + timeColField + ").");
                            min = min2;
                            hr = hr3;
                        } catch (RuntimeException e12) {
                            ex = e12;
                            int i26 = sec;
                            int sec4 = decimalIndex;
                            int i27 = hr3;
                            int i28 = nanos2;
                            SQLException sqlEx2222222222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                            sqlEx2222222222.initCause(ex);
                            throw sqlEx2222222222;
                        }
                    } else {
                        nanos2 = nanos;
                        if (timeColField.getMysqlType() == 12) {
                            int hr4 = StringUtils.getInt(bArr, offset + 11, offset + 13);
                            try {
                                int min3 = StringUtils.getInt(bArr, offset + 14, offset + 16);
                                try {
                                    int sec5 = StringUtils.getInt(bArr, offset + 17, offset + 19);
                                    try {
                                        int sec6 = sec5;
                                        try {
                                            new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_DATETIME_to_Time_with_getTime()_on_column__264") + (i + 1) + "(" + timeColField + ").");
                                            hr = hr4;
                                            min = min3;
                                            sec = sec6;
                                        } catch (RuntimeException e13) {
                                            ex = e13;
                                            int i29 = decimalIndex;
                                            int i30 = sec6;
                                            int i31 = nanos2;
                                            SQLException sqlEx22222222222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                                            sqlEx22222222222.initCause(ex);
                                            throw sqlEx22222222222;
                                        }
                                    } catch (RuntimeException e14) {
                                        ex = e14;
                                        int i32 = decimalIndex;
                                        int i33 = sec5;
                                        int i34 = nanos2;
                                        SQLException sqlEx222222222222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                                        sqlEx222222222222.initCause(ex);
                                        throw sqlEx222222222222;
                                    }
                                } catch (RuntimeException e15) {
                                    ex = e15;
                                    int i35 = decimalIndex;
                                    boolean z22 = z2;
                                    int i36 = nanos2;
                                    SQLException sqlEx2222222222222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                                    sqlEx2222222222222.initCause(ex);
                                    throw sqlEx2222222222222;
                                }
                            } catch (RuntimeException e16) {
                                ex = e16;
                                boolean z23 = z3;
                                int i37 = decimalIndex;
                                boolean z24 = z2;
                                int i38 = nanos2;
                                SQLException sqlEx22222222222222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                                sqlEx22222222222222.initCause(ex);
                                throw sqlEx22222222222222;
                            }
                        } else if (timeColField.getMysqlType() == 10) {
                            return resultSetImpl.fastTimeCreate((Calendar) null, 0, 0, 0);
                        } else {
                            int sec7 = 0;
                            if (length != 5) {
                                if (length != 8) {
                                    throw SQLError.createSQLException(Messages.getString("ResultSet.Bad_format_for_Time____267") + StringUtils.toString(timeAsBytes) + Messages.getString("ResultSet.___in_column__268") + (i + 1), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                                }
                            }
                            hr = StringUtils.getInt(bArr, offset + 0, offset + 2);
                            try {
                                int min4 = StringUtils.getInt(bArr, offset + 3, offset + 5);
                                if (length != 5) {
                                    try {
                                        sec7 = StringUtils.getInt(bArr, offset + 6, offset + 8);
                                    } catch (RuntimeException e17) {
                                        ex = e17;
                                        int i39 = hr;
                                        int i40 = decimalIndex;
                                        boolean z25 = z2;
                                        int i41 = nanos2;
                                        SQLException sqlEx222222222222222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                                        sqlEx222222222222222.initCause(ex);
                                        throw sqlEx222222222222222;
                                    }
                                }
                                sec = sec7;
                                min = min4;
                            } catch (RuntimeException e18) {
                                ex = e18;
                                int i42 = hr;
                                boolean z26 = z3;
                                int i43 = decimalIndex;
                                boolean z27 = z2;
                                int i44 = nanos2;
                                SQLException sqlEx2222222222222222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                                sqlEx2222222222222222.initCause(ex);
                                throw sqlEx2222222222222222;
                            }
                        }
                    }
                } catch (RuntimeException e19) {
                    ex = e19;
                    boolean z28 = z3;
                    int i45 = decimalIndex;
                    boolean z29 = z;
                    boolean z30 = z2;
                    int i46 = nanos;
                    SQLException sqlEx22222222222222222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                    sqlEx22222222222222222.initCause(ex);
                    throw sqlEx22222222222222222;
                }
                try {
                    Calendar sessionCalendar = rs.getCalendarInstanceForSessionOrNew();
                    if (!resultSetImpl.useLegacyDatetimeCode) {
                        if (targetCalendar2 == null) {
                            try {
                                try {
                                    targetCalendar2 = Calendar.getInstance(tz, Locale.US);
                                } catch (RuntimeException e20) {
                                    ex = e20;
                                    int i47 = hr;
                                    int i48 = sec;
                                    int i49 = min;
                                    int sec8 = decimalIndex;
                                    int i50 = nanos2;
                                    SQLException sqlEx222222222222222222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                                    sqlEx222222222222222222.initCause(ex);
                                    throw sqlEx222222222222222222;
                                }
                            } catch (RuntimeException e21) {
                                ex = e21;
                                TimeZone timeZone = tz;
                                int i51 = hr;
                                int i52 = sec;
                                int i53 = min;
                                int sec9 = decimalIndex;
                                int i54 = nanos2;
                                SQLException sqlEx2222222222222222222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                                sqlEx2222222222222222222.initCause(ex);
                                throw sqlEx2222222222222222222;
                            }
                        } else {
                            TimeZone timeZone2 = tz;
                        }
                        try {
                            return resultSetImpl.fastTimeCreate(targetCalendar2, hr, min, sec);
                        } catch (RuntimeException e22) {
                            ex = e22;
                            int i55 = hr;
                            int i56 = sec;
                            int i57 = min;
                            int sec10 = decimalIndex;
                            int i58 = nanos2;
                            SQLException sqlEx22222222222222222222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                            sqlEx22222222222222222222.initCause(ex);
                            throw sqlEx22222222222222222222;
                        }
                    } else {
                        TimeZone timeZone3 = tz;
                        Calendar calendar = sessionCalendar;
                        int sec11 = sec;
                        Field field = timeColField;
                        try {
                            return TimeUtil.changeTimezone(conn, sessionCalendar, targetCalendar, resultSetImpl.fastTimeCreate(sessionCalendar, hr, min, sec), conn.getServerTimezoneTZ(), tz, rollForward);
                        } catch (RuntimeException e23) {
                            ex = e23;
                            int i59 = hr;
                            int i60 = decimalIndex;
                            int i61 = sec11;
                            int i62 = min;
                            int i63 = nanos2;
                            SQLException sqlEx222222222222222222222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                            sqlEx222222222222222222222.initCause(ex);
                            throw sqlEx222222222222222222222;
                        }
                    }
                } catch (RuntimeException e24) {
                    ex = e24;
                    int i64 = sec;
                    int i65 = hr;
                    int sec12 = decimalIndex;
                    int i66 = i64;
                    int i67 = min;
                    int i68 = nanos2;
                    SQLException sqlEx2222222222222222222222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                    sqlEx2222222222222222222222.initCause(ex);
                    throw sqlEx2222222222222222222222;
                }
            } catch (RuntimeException e25) {
                ex = e25;
                int i69 = decimalIndex;
                boolean z31 = z;
                SQLException sqlEx22222222222222222222222 = SQLError.createSQLException(ex.toString(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
                sqlEx22222222222222222222222.initCause(ex);
                throw sqlEx22222222222222222222222;
            }
        } else if ("convertToNull".equals(conn.getZeroDateTimeBehavior())) {
            return null;
        } else {
            if (!"exception".equals(conn.getZeroDateTimeBehavior())) {
                return resultSetImpl.fastTimeCreate(targetCalendar2, 0, 0, 0);
            }
            throw SQLError.createSQLException("Value '" + StringUtils.toString(timeAsBytes) + "' can not be represented as java.sql.Time", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v2, resolved type: com.mysql.jdbc.ResultSetImpl} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v3, resolved type: com.mysql.jdbc.ResultSetImpl} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v4, resolved type: com.mysql.jdbc.ResultSetImpl} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v1, resolved type: com.mysql.jdbc.ResultSetImpl} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v6, resolved type: com.mysql.jdbc.ResultSetImpl} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v11, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v12, resolved type: com.mysql.jdbc.ResultSetImpl} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v13, resolved type: com.mysql.jdbc.ResultSetImpl} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v15, resolved type: com.mysql.jdbc.ResultSetImpl} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v19, resolved type: com.mysql.jdbc.ResultSetImpl} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v20, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v21, resolved type: com.mysql.jdbc.ResultSetImpl} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v22, resolved type: java.lang.String} */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:163:0x0408, code lost:
        if (r44.useLegacyDatetimeCode != false) goto L_0x041f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:167:0x041e, code lost:
        return com.mysql.jdbc.TimeUtil.fastTimestampCreate(r41, r16, r18, r20, r21, r22, r24, r23);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x041f, code lost:
        r25 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:171:0x044b, code lost:
        return com.mysql.jdbc.TimeUtil.changeTimezone(r43, r17, r40, r44.fastTimestampCreate(r17, r16, r18, r20, r21, r22, r24, r23, r45), r43.getServerTimezoneTZ(), r41, r42);
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.sql.Timestamp getTimestampFast(int r36, byte[] r37, int r38, int r39, java.util.Calendar r40, java.util.TimeZone r41, boolean r42, com.mysql.jdbc.MySQLConnection r43, com.mysql.jdbc.ResultSetImpl r44, boolean r45, boolean r46) throws java.sql.SQLException {
        /*
            r35 = this;
            r1 = r35
            r2 = r36
            r3 = r37
            r4 = r38
            r5 = r39
            r15 = r44
            java.lang.String r14 = "S1009"
            if (r46 == 0) goto L_0x001a
            java.util.Calendar r0 = r43.getUtcCalendar()     // Catch:{ RuntimeException -> 0x0015 }
            goto L_0x001e
        L_0x0015:
            r0 = move-exception
            r15 = r14
        L_0x0017:
            r14 = r5
            goto L_0x0487
        L_0x001a:
            java.util.Calendar r0 = r44.getCalendarInstanceForSessionOrNew()     // Catch:{ RuntimeException -> 0x0483 }
        L_0x001e:
            r13 = r40
            java.util.Calendar r17 = com.mysql.jdbc.TimeUtil.setProlepticIfNeeded(r0, r13)     // Catch:{ RuntimeException -> 0x0483 }
            r0 = 1
            r6 = 0
            r7 = 0
        L_0x0027:
            r8 = 58
            if (r7 >= r5) goto L_0x0036
            int r9 = r4 + r7
            byte r9 = r3[r9]     // Catch:{ RuntimeException -> 0x0015 }
            if (r9 != r8) goto L_0x0033
            r6 = 1
            goto L_0x0036
        L_0x0033:
            int r7 = r7 + 1
            goto L_0x0027
        L_0x0036:
            r7 = 0
        L_0x0037:
            r10 = 45
            if (r7 >= r5) goto L_0x0063
            int r11 = r4 + r7
            byte r11 = r3[r11]     // Catch:{ RuntimeException -> 0x0015 }
            r12 = 47
            r9 = 32
            if (r11 == r9) goto L_0x0049
            if (r11 == r10) goto L_0x0049
            if (r11 != r12) goto L_0x004a
        L_0x0049:
            r6 = 0
        L_0x004a:
            r12 = 48
            if (r11 == r12) goto L_0x0060
            if (r11 == r9) goto L_0x0060
            if (r11 == r8) goto L_0x0060
            if (r11 == r10) goto L_0x0060
            r9 = 47
            if (r11 == r9) goto L_0x0060
            r9 = 46
            if (r11 == r9) goto L_0x0060
            r0 = 0
            r26 = r6
            goto L_0x0065
        L_0x0060:
            int r7 = r7 + 1
            goto L_0x0037
        L_0x0063:
            r26 = r6
        L_0x0065:
            if (r26 != 0) goto L_0x00ef
            if (r0 == 0) goto L_0x00ef
            java.lang.String r6 = "convertToNull"
            java.lang.String r7 = r43.getZeroDateTimeBehavior()     // Catch:{ RuntimeException -> 0x00eb }
            boolean r6 = r6.equals(r7)     // Catch:{ RuntimeException -> 0x00eb }
            if (r6 == 0) goto L_0x0077
            r6 = 0
            return r6
        L_0x0077:
            java.lang.String r6 = "exception"
            java.lang.String r7 = r43.getZeroDateTimeBehavior()     // Catch:{ RuntimeException -> 0x00eb }
            boolean r6 = r6.equals(r7)     // Catch:{ RuntimeException -> 0x00eb }
            if (r6 != 0) goto L_0x00bd
            boolean r6 = r15.useLegacyDatetimeCode     // Catch:{ RuntimeException -> 0x00eb }
            if (r6 != 0) goto L_0x009c
            r19 = 1
            r20 = 1
            r21 = 1
            r22 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r18 = r41
            java.sql.Timestamp r6 = com.mysql.jdbc.TimeUtil.fastTimestampCreate(r18, r19, r20, r21, r22, r23, r24, r25)     // Catch:{ RuntimeException -> 0x0015 }
            return r6
        L_0x009c:
            r7 = 0
            r8 = 1
            r9 = 1
            r10 = 1
            r11 = 0
            r12 = 0
            r16 = 0
            r18 = 0
            r6 = r44
            r13 = r16
            r27 = r14
            r14 = r18
            r5 = r15
            r15 = r45
            java.sql.Timestamp r6 = r6.fastTimestampCreate(r7, r8, r9, r10, r11, r12, r13, r14, r15)     // Catch:{ RuntimeException -> 0x00b6 }
            return r6
        L_0x00b6:
            r0 = move-exception
            r14 = r39
            r15 = r27
            goto L_0x0487
        L_0x00bd:
            r27 = r14
            r5 = r15
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x00e6 }
            r6.<init>()     // Catch:{ RuntimeException -> 0x00e6 }
            java.lang.String r7 = "Value '"
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch:{ RuntimeException -> 0x00e6 }
            java.lang.String r7 = com.mysql.jdbc.StringUtils.toString(r37)     // Catch:{ RuntimeException -> 0x00e6 }
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch:{ RuntimeException -> 0x00e6 }
            java.lang.String r7 = "' can not be represented as java.sql.Timestamp"
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch:{ RuntimeException -> 0x00e6 }
            java.lang.String r6 = r6.toString()     // Catch:{ RuntimeException -> 0x00e6 }
            com.mysql.jdbc.ExceptionInterceptor r7 = r1.exceptionInterceptor     // Catch:{ RuntimeException -> 0x00e6 }
            r15 = r27
            java.sql.SQLException r6 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r6, (java.lang.String) r15, (com.mysql.jdbc.ExceptionInterceptor) r7)     // Catch:{ RuntimeException -> 0x0481 }
            throw r6     // Catch:{ RuntimeException -> 0x0481 }
        L_0x00e6:
            r0 = move-exception
            r15 = r27
            goto L_0x0485
        L_0x00eb:
            r0 = move-exception
            r5 = r15
            goto L_0x0484
        L_0x00ef:
            r5 = r15
            r15 = r14
            com.mysql.jdbc.Field[] r6 = r1.metadata     // Catch:{ RuntimeException -> 0x0481 }
            r6 = r6[r2]     // Catch:{ RuntimeException -> 0x0481 }
            int r6 = r6.getMysqlType()     // Catch:{ RuntimeException -> 0x0481 }
            r7 = 13
            if (r6 != r7) goto L_0x0144
            boolean r6 = r5.useLegacyDatetimeCode     // Catch:{ RuntimeException -> 0x0481 }
            r7 = 4
            if (r6 != 0) goto L_0x0119
            int r19 = com.mysql.jdbc.StringUtils.getInt(r3, r4, r7)     // Catch:{ RuntimeException -> 0x0481 }
            r20 = 1
            r21 = 1
            r22 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            r18 = r41
            java.sql.Timestamp r6 = com.mysql.jdbc.TimeUtil.fastTimestampCreate(r18, r19, r20, r21, r22, r23, r24, r25)     // Catch:{ RuntimeException -> 0x0481 }
            return r6
        L_0x0119:
            int r18 = com.mysql.jdbc.StringUtils.getInt(r3, r4, r7)     // Catch:{ RuntimeException -> 0x0481 }
            r19 = 1
            r20 = 1
            r21 = 0
            r22 = 0
            r23 = 0
            r24 = 0
            r16 = r44
            r25 = r45
            java.sql.Timestamp r9 = r16.fastTimestampCreate(r17, r18, r19, r20, r21, r22, r23, r24, r25)     // Catch:{ RuntimeException -> 0x0481 }
            java.util.TimeZone r10 = r43.getServerTimezoneTZ()     // Catch:{ RuntimeException -> 0x0481 }
            r6 = r43
            r7 = r17
            r8 = r40
            r11 = r41
            r12 = r42
            java.sql.Timestamp r6 = com.mysql.jdbc.TimeUtil.changeTimezone((com.mysql.jdbc.MySQLConnection) r6, (java.util.Calendar) r7, (java.util.Calendar) r8, (java.sql.Timestamp) r9, (java.util.TimeZone) r10, (java.util.TimeZone) r11, (boolean) r12)     // Catch:{ RuntimeException -> 0x0481 }
            return r6
        L_0x0144:
            r6 = 0
            r7 = 0
            r9 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r18 = -1
            r19 = 0
            r8 = r19
        L_0x0151:
            r5 = r39
            if (r8 >= r5) goto L_0x0170
            int r19 = r4 + r8
            byte r10 = r3[r19]     // Catch:{ RuntimeException -> 0x016d }
            r19 = r0
            r0 = 46
            if (r10 != r0) goto L_0x0164
            r18 = r8
            r0 = r18
            goto L_0x0174
        L_0x0164:
            int r8 = r8 + 1
            r5 = r44
            r0 = r19
            r10 = 45
            goto L_0x0151
        L_0x016d:
            r0 = move-exception
            goto L_0x0017
        L_0x0170:
            r19 = r0
            r0 = r18
        L_0x0174:
            int r8 = r4 + r5
            int r8 = r8 + -1
            r10 = -1
            if (r0 != r8) goto L_0x0185
            int r5 = r5 + -1
            r18 = r6
            r22 = r9
            r23 = r14
            r14 = r5
            goto L_0x01cf
        L_0x0185:
            if (r0 == r10) goto L_0x01c7
            int r8 = r0 + 2
            if (r8 > r5) goto L_0x01bd
            int r8 = r4 + r0
            int r8 = r8 + 1
            int r10 = r4 + r5
            int r8 = com.mysql.jdbc.StringUtils.getInt(r3, r8, r10)     // Catch:{ RuntimeException -> 0x0481 }
            int r10 = r0 + 1
            int r10 = r5 - r10
            r14 = 9
            if (r10 >= r14) goto L_0x01b0
            r18 = r6
            r5 = 4621819117588971520(0x4024000000000000, double:10.0)
            int r14 = 9 - r10
            r22 = r9
            r23 = r10
            double r9 = (double) r14     // Catch:{ RuntimeException -> 0x0481 }
            double r5 = java.lang.Math.pow(r5, r9)     // Catch:{ RuntimeException -> 0x0481 }
            int r5 = (int) r5     // Catch:{ RuntimeException -> 0x0481 }
            int r8 = r8 * r5
            r14 = r8
            goto L_0x01b7
        L_0x01b0:
            r18 = r6
            r22 = r9
            r23 = r10
            r14 = r8
        L_0x01b7:
            r5 = r0
            r23 = r14
            r14 = r5
            goto L_0x01cf
        L_0x01bd:
            r18 = r6
            r22 = r9
            java.lang.IllegalArgumentException r5 = new java.lang.IllegalArgumentException     // Catch:{ RuntimeException -> 0x0481 }
            r5.<init>()     // Catch:{ RuntimeException -> 0x0481 }
            throw r5     // Catch:{ RuntimeException -> 0x0481 }
        L_0x01c7:
            r18 = r6
            r22 = r9
            r23 = r14
            r14 = r39
        L_0x01cf:
            r5 = 69
            switch(r14) {
                case 2: goto L_0x03e8;
                case 3: goto L_0x01d4;
                case 4: goto L_0x03c0;
                case 5: goto L_0x01d4;
                case 6: goto L_0x0395;
                case 7: goto L_0x01d4;
                case 8: goto L_0x032a;
                case 9: goto L_0x01d4;
                case 10: goto L_0x02a3;
                case 11: goto L_0x01d4;
                case 12: goto L_0x025c;
                case 13: goto L_0x01d4;
                case 14: goto L_0x021b;
                case 15: goto L_0x01d4;
                case 16: goto L_0x01d4;
                case 17: goto L_0x01d4;
                case 18: goto L_0x01d4;
                case 19: goto L_0x01da;
                case 20: goto L_0x01da;
                case 21: goto L_0x01da;
                case 22: goto L_0x01da;
                case 23: goto L_0x01da;
                case 24: goto L_0x01da;
                case 25: goto L_0x01da;
                case 26: goto L_0x01da;
                case 27: goto L_0x01d4;
                case 28: goto L_0x01d4;
                case 29: goto L_0x01da;
                default: goto L_0x01d4;
            }
        L_0x01d4:
            r25 = r14
            java.sql.SQLException r5 = new java.sql.SQLException     // Catch:{ RuntimeException -> 0x047d }
            goto L_0x0450
        L_0x01da:
            int r5 = r4 + 0
            int r6 = r4 + 4
            int r5 = com.mysql.jdbc.StringUtils.getInt(r3, r5, r6)     // Catch:{ RuntimeException -> 0x03e5 }
            int r6 = r4 + 5
            int r8 = r4 + 7
            int r6 = com.mysql.jdbc.StringUtils.getInt(r3, r6, r8)     // Catch:{ RuntimeException -> 0x03e5 }
            int r7 = r4 + 8
            int r8 = r4 + 10
            int r7 = com.mysql.jdbc.StringUtils.getInt(r3, r7, r8)     // Catch:{ RuntimeException -> 0x03e5 }
            int r8 = r4 + 11
            int r9 = r4 + 13
            int r8 = com.mysql.jdbc.StringUtils.getInt(r3, r8, r9)     // Catch:{ RuntimeException -> 0x03e5 }
            r11 = r8
            int r8 = r4 + 14
            int r9 = r4 + 16
            int r8 = com.mysql.jdbc.StringUtils.getInt(r3, r8, r9)     // Catch:{ RuntimeException -> 0x03e5 }
            r12 = r8
            int r8 = r4 + 17
            int r9 = r4 + 19
            int r8 = com.mysql.jdbc.StringUtils.getInt(r3, r8, r9)     // Catch:{ RuntimeException -> 0x03e5 }
            r13 = r8
            r16 = r5
            r18 = r6
            r20 = r7
            r21 = r11
            r22 = r12
            r24 = r13
            goto L_0x0404
        L_0x021b:
            int r5 = r4 + 0
            int r6 = r4 + 4
            int r5 = com.mysql.jdbc.StringUtils.getInt(r3, r5, r6)     // Catch:{ RuntimeException -> 0x03e5 }
            int r6 = r4 + 4
            int r8 = r4 + 6
            int r6 = com.mysql.jdbc.StringUtils.getInt(r3, r6, r8)     // Catch:{ RuntimeException -> 0x03e5 }
            int r7 = r4 + 6
            int r8 = r4 + 8
            int r7 = com.mysql.jdbc.StringUtils.getInt(r3, r7, r8)     // Catch:{ RuntimeException -> 0x03e5 }
            int r8 = r4 + 8
            int r9 = r4 + 10
            int r8 = com.mysql.jdbc.StringUtils.getInt(r3, r8, r9)     // Catch:{ RuntimeException -> 0x03e5 }
            r11 = r8
            int r8 = r4 + 10
            int r9 = r4 + 12
            int r8 = com.mysql.jdbc.StringUtils.getInt(r3, r8, r9)     // Catch:{ RuntimeException -> 0x03e5 }
            r12 = r8
            int r8 = r4 + 12
            int r9 = r4 + 14
            int r8 = com.mysql.jdbc.StringUtils.getInt(r3, r8, r9)     // Catch:{ RuntimeException -> 0x03e5 }
            r13 = r8
            r16 = r5
            r18 = r6
            r20 = r7
            r21 = r11
            r22 = r12
            r24 = r13
            goto L_0x0404
        L_0x025c:
            int r6 = r4 + 0
            int r8 = r4 + 2
            int r6 = com.mysql.jdbc.StringUtils.getInt(r3, r6, r8)     // Catch:{ RuntimeException -> 0x03e5 }
            if (r6 > r5) goto L_0x0268
            int r6 = r6 + 100
        L_0x0268:
            int r5 = r6 + 1900
            int r6 = r4 + 2
            int r8 = r4 + 4
            int r6 = com.mysql.jdbc.StringUtils.getInt(r3, r6, r8)     // Catch:{ RuntimeException -> 0x03e5 }
            int r7 = r4 + 4
            int r8 = r4 + 6
            int r7 = com.mysql.jdbc.StringUtils.getInt(r3, r7, r8)     // Catch:{ RuntimeException -> 0x03e5 }
            int r8 = r4 + 6
            int r9 = r4 + 8
            int r8 = com.mysql.jdbc.StringUtils.getInt(r3, r8, r9)     // Catch:{ RuntimeException -> 0x03e5 }
            r11 = r8
            int r8 = r4 + 8
            int r9 = r4 + 10
            int r8 = com.mysql.jdbc.StringUtils.getInt(r3, r8, r9)     // Catch:{ RuntimeException -> 0x03e5 }
            r12 = r8
            int r8 = r4 + 10
            int r9 = r4 + 12
            int r8 = com.mysql.jdbc.StringUtils.getInt(r3, r8, r9)     // Catch:{ RuntimeException -> 0x03e5 }
            r13 = r8
            r16 = r5
            r18 = r6
            r20 = r7
            r21 = r11
            r22 = r12
            r24 = r13
            goto L_0x0404
        L_0x02a3:
            r6 = 0
            r8 = 0
        L_0x02a5:
            if (r8 >= r14) goto L_0x02b4
            int r9 = r4 + r8
            byte r9 = r3[r9]     // Catch:{ RuntimeException -> 0x03e5 }
            r10 = 45
            if (r9 != r10) goto L_0x02b1
            r6 = 1
            goto L_0x02b4
        L_0x02b1:
            int r8 = r8 + 1
            goto L_0x02a5
        L_0x02b4:
            com.mysql.jdbc.Field[] r8 = r1.metadata     // Catch:{ RuntimeException -> 0x03e5 }
            r8 = r8[r2]     // Catch:{ RuntimeException -> 0x03e5 }
            int r8 = r8.getMysqlType()     // Catch:{ RuntimeException -> 0x03e5 }
            r9 = 10
            if (r8 == r9) goto L_0x0301
            if (r6 == 0) goto L_0x02c3
            goto L_0x0301
        L_0x02c3:
            int r8 = r4 + 0
            int r9 = r4 + 2
            int r8 = com.mysql.jdbc.StringUtils.getInt(r3, r8, r9)     // Catch:{ RuntimeException -> 0x03e5 }
            if (r8 > r5) goto L_0x02cf
            int r8 = r8 + 100
        L_0x02cf:
            int r5 = r4 + 2
            int r9 = r4 + 4
            int r5 = com.mysql.jdbc.StringUtils.getInt(r3, r5, r9)     // Catch:{ RuntimeException -> 0x03e5 }
            int r7 = r4 + 4
            int r9 = r4 + 6
            int r7 = com.mysql.jdbc.StringUtils.getInt(r3, r7, r9)     // Catch:{ RuntimeException -> 0x03e5 }
            int r9 = r4 + 6
            int r10 = r4 + 8
            int r9 = com.mysql.jdbc.StringUtils.getInt(r3, r9, r10)     // Catch:{ RuntimeException -> 0x03e5 }
            r11 = r9
            int r9 = r4 + 8
            int r10 = r4 + 10
            int r9 = com.mysql.jdbc.StringUtils.getInt(r3, r9, r10)     // Catch:{ RuntimeException -> 0x03e5 }
            r12 = r9
            int r8 = r8 + 1900
            r18 = r5
            r20 = r7
            r16 = r8
            r21 = r11
            r22 = r12
            r24 = r13
            goto L_0x0404
        L_0x0301:
            int r5 = r4 + 0
            int r8 = r4 + 4
            int r5 = com.mysql.jdbc.StringUtils.getInt(r3, r5, r8)     // Catch:{ RuntimeException -> 0x03e5 }
            int r8 = r4 + 5
            int r9 = r4 + 7
            int r8 = com.mysql.jdbc.StringUtils.getInt(r3, r8, r9)     // Catch:{ RuntimeException -> 0x03e5 }
            r7 = r8
            int r8 = r4 + 8
            int r9 = r4 + 10
            int r8 = com.mysql.jdbc.StringUtils.getInt(r3, r8, r9)     // Catch:{ RuntimeException -> 0x03e5 }
            r11 = 0
            r12 = 0
            r16 = r5
            r18 = r7
            r20 = r8
            r21 = r11
            r22 = r12
            r24 = r13
            goto L_0x0404
        L_0x032a:
            r5 = 0
            r6 = 0
        L_0x032c:
            if (r6 >= r14) goto L_0x033b
            int r8 = r4 + r6
            byte r8 = r3[r8]     // Catch:{ RuntimeException -> 0x03e5 }
            r9 = 58
            if (r8 != r9) goto L_0x0338
            r5 = 1
            goto L_0x033b
        L_0x0338:
            int r6 = r6 + 1
            goto L_0x032c
        L_0x033b:
            if (r5 == 0) goto L_0x036a
            int r6 = r4 + 0
            int r8 = r4 + 2
            int r6 = com.mysql.jdbc.StringUtils.getInt(r3, r6, r8)     // Catch:{ RuntimeException -> 0x03e5 }
            r11 = r6
            int r6 = r4 + 3
            int r8 = r4 + 5
            int r6 = com.mysql.jdbc.StringUtils.getInt(r3, r6, r8)     // Catch:{ RuntimeException -> 0x03e5 }
            r12 = r6
            int r6 = r4 + 6
            int r8 = r4 + 8
            int r6 = com.mysql.jdbc.StringUtils.getInt(r3, r6, r8)     // Catch:{ RuntimeException -> 0x03e5 }
            r13 = r6
            r6 = 1970(0x7b2, float:2.76E-42)
            r7 = 1
            r8 = 1
            r16 = r6
            r18 = r7
            r20 = r8
            r21 = r11
            r22 = r12
            r24 = r13
            goto L_0x0404
        L_0x036a:
            int r6 = r4 + 0
            int r8 = r4 + 4
            int r6 = com.mysql.jdbc.StringUtils.getInt(r3, r6, r8)     // Catch:{ RuntimeException -> 0x03e5 }
            int r8 = r4 + 4
            int r9 = r4 + 6
            int r8 = com.mysql.jdbc.StringUtils.getInt(r3, r8, r9)     // Catch:{ RuntimeException -> 0x03e5 }
            r7 = r8
            int r8 = r4 + 6
            int r9 = r4 + 8
            int r8 = com.mysql.jdbc.StringUtils.getInt(r3, r8, r9)     // Catch:{ RuntimeException -> 0x03e5 }
            int r6 = r6 + -1900
            r9 = -1
            int r7 = r7 + r9
            r16 = r6
            r18 = r7
            r20 = r8
            r21 = r11
            r22 = r12
            r24 = r13
            goto L_0x0404
        L_0x0395:
            int r6 = r4 + 0
            int r8 = r4 + 2
            int r6 = com.mysql.jdbc.StringUtils.getInt(r3, r6, r8)     // Catch:{ RuntimeException -> 0x03e5 }
            if (r6 > r5) goto L_0x03a1
            int r6 = r6 + 100
        L_0x03a1:
            int r5 = r6 + 1900
            int r6 = r4 + 2
            int r8 = r4 + 4
            int r6 = com.mysql.jdbc.StringUtils.getInt(r3, r6, r8)     // Catch:{ RuntimeException -> 0x03e5 }
            int r7 = r4 + 4
            int r8 = r4 + 6
            int r7 = com.mysql.jdbc.StringUtils.getInt(r3, r7, r8)     // Catch:{ RuntimeException -> 0x03e5 }
            r16 = r5
            r18 = r6
            r20 = r7
            r21 = r11
            r22 = r12
            r24 = r13
            goto L_0x0404
        L_0x03c0:
            int r6 = r4 + 0
            int r8 = r4 + 2
            int r6 = com.mysql.jdbc.StringUtils.getInt(r3, r6, r8)     // Catch:{ RuntimeException -> 0x03e5 }
            if (r6 > r5) goto L_0x03ce
            int r6 = r6 + 100
            r5 = r6
            goto L_0x03cf
        L_0x03ce:
            r5 = r6
        L_0x03cf:
            int r6 = r4 + 2
            int r8 = r4 + 4
            int r6 = com.mysql.jdbc.StringUtils.getInt(r3, r6, r8)     // Catch:{ RuntimeException -> 0x03e5 }
            r7 = 1
            r16 = r5
            r18 = r6
            r20 = r7
            r21 = r11
            r22 = r12
            r24 = r13
            goto L_0x0404
        L_0x03e5:
            r0 = move-exception
            goto L_0x0487
        L_0x03e8:
            int r6 = r4 + 0
            int r8 = r4 + 2
            int r6 = com.mysql.jdbc.StringUtils.getInt(r3, r6, r8)     // Catch:{ RuntimeException -> 0x044c }
            if (r6 > r5) goto L_0x03f4
            int r6 = r6 + 100
        L_0x03f4:
            int r5 = r6 + 1900
            r6 = 1
            r7 = 1
            r16 = r5
            r18 = r6
            r20 = r7
            r21 = r11
            r22 = r12
            r24 = r13
        L_0x0404:
            r13 = r44
            boolean r5 = r13.useLegacyDatetimeCode     // Catch:{ RuntimeException -> 0x044c }
            if (r5 != 0) goto L_0x041f
            r27 = r41
            r28 = r16
            r29 = r18
            r30 = r20
            r31 = r21
            r32 = r22
            r33 = r24
            r34 = r23
            java.sql.Timestamp r5 = com.mysql.jdbc.TimeUtil.fastTimestampCreate(r27, r28, r29, r30, r31, r32, r33, r34)     // Catch:{ RuntimeException -> 0x03e5 }
            return r5
        L_0x041f:
            r5 = r44
            r6 = r17
            r7 = r16
            r8 = r18
            r9 = r20
            r10 = r21
            r11 = r22
            r12 = r24
            r13 = r23
            r25 = r14
            r14 = r45
            java.sql.Timestamp r8 = r5.fastTimestampCreate(r6, r7, r8, r9, r10, r11, r12, r13, r14)     // Catch:{ RuntimeException -> 0x047d }
            java.util.TimeZone r9 = r43.getServerTimezoneTZ()     // Catch:{ RuntimeException -> 0x047d }
            r5 = r43
            r6 = r17
            r7 = r40
            r10 = r41
            r11 = r42
            java.sql.Timestamp r5 = com.mysql.jdbc.TimeUtil.changeTimezone((com.mysql.jdbc.MySQLConnection) r5, (java.util.Calendar) r6, (java.util.Calendar) r7, (java.sql.Timestamp) r8, (java.util.TimeZone) r9, (java.util.TimeZone) r10, (boolean) r11)     // Catch:{ RuntimeException -> 0x047d }
            return r5
        L_0x044c:
            r0 = move-exception
            r25 = r14
            goto L_0x0487
        L_0x0450:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x047d }
            r6.<init>()     // Catch:{ RuntimeException -> 0x047d }
            java.lang.String r8 = "Bad format for Timestamp '"
            java.lang.StringBuilder r6 = r6.append(r8)     // Catch:{ RuntimeException -> 0x047d }
            java.lang.String r8 = com.mysql.jdbc.StringUtils.toString(r37)     // Catch:{ RuntimeException -> 0x047d }
            java.lang.StringBuilder r6 = r6.append(r8)     // Catch:{ RuntimeException -> 0x047d }
            java.lang.String r8 = "' in column "
            java.lang.StringBuilder r6 = r6.append(r8)     // Catch:{ RuntimeException -> 0x047d }
            int r8 = r2 + 1
            java.lang.StringBuilder r6 = r6.append(r8)     // Catch:{ RuntimeException -> 0x047d }
            java.lang.String r8 = "."
            java.lang.StringBuilder r6 = r6.append(r8)     // Catch:{ RuntimeException -> 0x047d }
            java.lang.String r6 = r6.toString()     // Catch:{ RuntimeException -> 0x047d }
            r5.<init>(r6, r15)     // Catch:{ RuntimeException -> 0x047d }
            throw r5     // Catch:{ RuntimeException -> 0x047d }
        L_0x047d:
            r0 = move-exception
            r14 = r25
            goto L_0x0487
        L_0x0481:
            r0 = move-exception
            goto L_0x0485
        L_0x0483:
            r0 = move-exception
        L_0x0484:
            r15 = r14
        L_0x0485:
            r14 = r39
        L_0x0487:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Cannot convert value '"
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r6 = "ISO8859_1"
            r7 = r43
            java.lang.String r6 = r1.getString(r2, r6, r7)
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r6 = "' from column "
            java.lang.StringBuilder r5 = r5.append(r6)
            int r6 = r2 + 1
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r6 = " to TIMESTAMP."
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r5 = r5.toString()
            com.mysql.jdbc.ExceptionInterceptor r6 = r1.exceptionInterceptor
            java.sql.SQLException r5 = com.mysql.jdbc.SQLError.createSQLException((java.lang.String) r5, (java.lang.String) r15, (com.mysql.jdbc.ExceptionInterceptor) r6)
            r5.initCause(r0)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mysql.jdbc.ResultSetRow.getTimestampFast(int, byte[], int, int, java.util.Calendar, java.util.TimeZone, boolean, com.mysql.jdbc.MySQLConnection, com.mysql.jdbc.ResultSetImpl, boolean, boolean):java.sql.Timestamp");
    }

    public ResultSetRow setMetadata(Field[] f) throws SQLException {
        this.metadata = f;
        return this;
    }
}
