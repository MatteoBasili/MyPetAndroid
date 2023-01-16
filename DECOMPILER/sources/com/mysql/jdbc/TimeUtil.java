package com.mysql.jdbc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import kotlin.time.DurationKt;

public class TimeUtil {
    private static final TimeZone DEFAULT_TIMEZONE = TimeZone.getDefault();
    static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
    private static final String TIME_ZONE_MAPPINGS_RESOURCE = "/com/mysql/jdbc/TimeZoneMapping.properties";
    protected static final Method systemNanoTimeMethod;
    private static Properties timeZoneMappings = null;

    static {
        Method aMethod;
        try {
            Class[] clsArr = null;
            aMethod = System.class.getMethod("nanoTime", (Class[]) null);
        } catch (SecurityException e) {
            aMethod = null;
        } catch (NoSuchMethodException e2) {
            aMethod = null;
        }
        systemNanoTimeMethod = aMethod;
    }

    public static boolean nanoTimeAvailable() {
        return systemNanoTimeMethod != null;
    }

    public static final TimeZone getDefaultTimeZone(boolean useCache) {
        return (TimeZone) (useCache ? DEFAULT_TIMEZONE : TimeZone.getDefault()).clone();
    }

    public static long getCurrentTimeNanosOrMillis() {
        Method method = systemNanoTimeMethod;
        if (method != null) {
            try {
                Object[] objArr = null;
                return ((Long) method.invoke((Object) null, (Object[]) null)).longValue();
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            }
        }
        return System.currentTimeMillis();
    }

    public static Time changeTimezone(MySQLConnection conn, Calendar sessionCalendar, Calendar targetCalendar, Time t, TimeZone fromTz, TimeZone toTz, boolean rollForward) {
        long toTime;
        if (conn != null) {
            if (conn.getUseTimezone() && !conn.getNoTimezoneConversionForTimeType()) {
                Calendar fromCal = Calendar.getInstance(fromTz);
                fromCal.setTime(t);
                int fromOffset = fromCal.get(15) + fromCal.get(16);
                Calendar toCal = Calendar.getInstance(toTz);
                toCal.setTime(t);
                int offsetDiff = fromOffset - (toCal.get(15) + toCal.get(16));
                long toTime2 = toCal.getTime().getTime();
                if (rollForward) {
                    toTime = toTime2 + ((long) offsetDiff);
                } else {
                    toTime = toTime2 - ((long) offsetDiff);
                }
                return new Time(toTime);
            } else if (conn.getUseJDBCCompliantTimezoneShift() && targetCalendar != null) {
                return new Time(jdbcCompliantZoneShift(sessionCalendar, targetCalendar, t));
            }
        }
        return t;
    }

    public static Timestamp changeTimezone(MySQLConnection conn, Calendar sessionCalendar, Calendar targetCalendar, Timestamp tstamp, TimeZone fromTz, TimeZone toTz, boolean rollForward) {
        long toTime;
        if (conn != null) {
            if (conn.getUseTimezone()) {
                Calendar fromCal = Calendar.getInstance(fromTz);
                fromCal.setTime(tstamp);
                int fromOffset = fromCal.get(15) + fromCal.get(16);
                Calendar toCal = Calendar.getInstance(toTz);
                toCal.setTime(tstamp);
                int offsetDiff = fromOffset - (toCal.get(15) + toCal.get(16));
                long toTime2 = toCal.getTime().getTime();
                if (rollForward) {
                    toTime = toTime2 + ((long) offsetDiff);
                } else {
                    toTime = toTime2 - ((long) offsetDiff);
                }
                return new Timestamp(toTime);
            } else if (conn.getUseJDBCCompliantTimezoneShift() && targetCalendar != null) {
                Timestamp adjustedTimestamp = new Timestamp(jdbcCompliantZoneShift(sessionCalendar, targetCalendar, tstamp));
                adjustedTimestamp.setNanos(tstamp.getNanos());
                return adjustedTimestamp;
            }
        }
        return tstamp;
    }

    private static long jdbcCompliantZoneShift(Calendar sessionCalendar, Calendar targetCalendar, Date dt) {
        long time;
        if (sessionCalendar == null) {
            sessionCalendar = new GregorianCalendar();
        }
        synchronized (sessionCalendar) {
            Date origCalDate = targetCalendar.getTime();
            Date origSessionDate = sessionCalendar.getTime();
            try {
                sessionCalendar.setTime(dt);
                targetCalendar.set(1, sessionCalendar.get(1));
                targetCalendar.set(2, sessionCalendar.get(2));
                targetCalendar.set(5, sessionCalendar.get(5));
                targetCalendar.set(11, sessionCalendar.get(11));
                targetCalendar.set(12, sessionCalendar.get(12));
                targetCalendar.set(13, sessionCalendar.get(13));
                targetCalendar.set(14, sessionCalendar.get(14));
                time = targetCalendar.getTime().getTime();
            } finally {
                sessionCalendar.setTime(origSessionDate);
                targetCalendar.setTime(origCalDate);
            }
        }
        return time;
    }

    static final java.sql.Date fastDateCreate(boolean useGmtConversion, Calendar gmtCalIfNeeded, Calendar cal, int year, int month, int day) {
        Calendar dateCal;
        java.sql.Date date;
        Calendar dateCal2 = cal;
        if (useGmtConversion) {
            if (gmtCalIfNeeded == null) {
                gmtCalIfNeeded = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            }
            dateCal = gmtCalIfNeeded;
        } else {
            dateCal = dateCal2;
        }
        synchronized (dateCal) {
            Date origCalDate = dateCal.getTime();
            try {
                dateCal.clear();
                dateCal.set(14, 0);
                dateCal.set(year, month - 1, day, 0, 0, 0);
                date = new java.sql.Date(dateCal.getTimeInMillis());
            } finally {
                dateCal.setTime(origCalDate);
            }
        }
        return date;
    }

    static final java.sql.Date fastDateCreate(int year, int month, int day, Calendar targetCalendar) {
        java.sql.Date date;
        Calendar dateCal = targetCalendar == null ? new GregorianCalendar() : targetCalendar;
        synchronized (dateCal) {
            Date origCalDate = dateCal.getTime();
            try {
                dateCal.clear();
                dateCal.set(year, month - 1, day, 0, 0, 0);
                dateCal.set(14, 0);
                date = new java.sql.Date(dateCal.getTimeInMillis());
            } finally {
                dateCal.setTime(origCalDate);
            }
        }
        return date;
    }

    static final Time fastTimeCreate(Calendar cal, int hour, int minute, int second, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        Time time;
        if (hour < 0 || hour > 24) {
            throw SQLError.createSQLException("Illegal hour value '" + hour + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
        } else if (minute < 0 || minute > 59) {
            throw SQLError.createSQLException("Illegal minute value '" + minute + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
        } else if (second < 0 || second > 59) {
            throw SQLError.createSQLException("Illegal minute value '" + second + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
        } else {
            synchronized (cal) {
                Date origCalDate = cal.getTime();
                try {
                    cal.clear();
                    cal.set(1970, 0, 1, hour, minute, second);
                    time = new Time(cal.getTimeInMillis());
                } finally {
                    cal.setTime(origCalDate);
                }
            }
            return time;
        }
    }

    static final Time fastTimeCreate(int hour, int minute, int second, Calendar targetCalendar, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        Time time;
        if (hour < 0 || hour > 23) {
            throw SQLError.createSQLException("Illegal hour value '" + hour + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
        } else if (minute < 0 || minute > 59) {
            throw SQLError.createSQLException("Illegal minute value '" + minute + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
        } else if (second < 0 || second > 59) {
            throw SQLError.createSQLException("Illegal minute value '" + second + "' for java.sql.Time type in value '" + timeFormattedString(hour, minute, second) + ".", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, exceptionInterceptor);
        } else {
            Calendar cal = targetCalendar == null ? new GregorianCalendar() : targetCalendar;
            synchronized (cal) {
                Date origCalDate = cal.getTime();
                try {
                    cal.clear();
                    cal.set(1970, 0, 1, hour, minute, second);
                    time = new Time(cal.getTimeInMillis());
                } finally {
                    cal.setTime(origCalDate);
                }
            }
            return time;
        }
    }

    static final Timestamp fastTimestampCreate(boolean useGmtConversion, Calendar gmtCalIfNeeded, Calendar cal, int year, int month, int day, int hour, int minute, int seconds, int secondsPart) {
        Calendar gmtCalIfNeeded2;
        Calendar calendar = cal;
        int i = secondsPart;
        synchronized (cal) {
            try {
                Date origCalDate = cal.getTime();
                try {
                    cal.clear();
                    cal.set(year, month - 1, day, hour, minute, seconds);
                    int offsetDiff = 0;
                    if (useGmtConversion) {
                        int fromOffset = cal.get(15) + cal.get(16);
                        if (gmtCalIfNeeded == null) {
                            gmtCalIfNeeded2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                        } else {
                            gmtCalIfNeeded2 = gmtCalIfNeeded;
                        }
                        try {
                            gmtCalIfNeeded2.clear();
                            gmtCalIfNeeded2.setTimeInMillis(cal.getTimeInMillis());
                            offsetDiff = fromOffset - (gmtCalIfNeeded2.get(15) + gmtCalIfNeeded2.get(16));
                        } catch (Throwable th) {
                            th = th;
                            throw th;
                        }
                    }
                    if (i != 0) {
                        cal.set(14, i / DurationKt.NANOS_IN_MILLIS);
                    }
                    Timestamp ts = new Timestamp(((long) offsetDiff) + cal.getTimeInMillis());
                    ts.setNanos(i);
                    cal.setTime(origCalDate);
                    Timestamp timestamp = ts;
                    return ts;
                } catch (Throwable th2) {
                    th = th2;
                    Calendar calendar2 = gmtCalIfNeeded;
                    cal.setTime(origCalDate);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                Calendar calendar3 = gmtCalIfNeeded;
                throw th;
            }
        }
    }

    static final Timestamp fastTimestampCreate(TimeZone tz, int year, int month, int day, int hour, int minute, int seconds, int secondsPart) {
        Calendar gregorianCalendar;
        if (tz != null) {
            gregorianCalendar = new GregorianCalendar(tz);
        }
        Calendar cal = gregorianCalendar;
        cal.clear();
        cal.set(year, month - 1, day, hour, minute, seconds);
        Timestamp ts = new Timestamp(cal.getTimeInMillis());
        ts.setNanos(secondsPart);
        return ts;
    }

    public static String getCanonicalTimezone(String timezoneStr, ExceptionInterceptor exceptionInterceptor) throws SQLException {
        if (timezoneStr == null) {
            return null;
        }
        String timezoneStr2 = timezoneStr.trim();
        if (timezoneStr2.length() > 2 && ((timezoneStr2.charAt(0) == '+' || timezoneStr2.charAt(0) == '-') && Character.isDigit(timezoneStr2.charAt(1)))) {
            return "GMT" + timezoneStr2;
        }
        synchronized (TimeUtil.class) {
            if (timeZoneMappings == null) {
                loadTimeZoneMappings(exceptionInterceptor);
            }
        }
        String property = timeZoneMappings.getProperty(timezoneStr2);
        String canonicalTz = property;
        if (property != null) {
            return canonicalTz;
        }
        throw SQLError.createSQLException(Messages.getString("TimeUtil.UnrecognizedTimezoneId", new Object[]{timezoneStr2}), SQLError.SQL_STATE_INVALID_CONNECTION_ATTRIBUTE, exceptionInterceptor);
    }

    private static String timeFormattedString(int hours, int minutes, int seconds) {
        StringBuilder buf = new StringBuilder(8);
        if (hours < 10) {
            buf.append("0");
        }
        buf.append(hours);
        buf.append(":");
        if (minutes < 10) {
            buf.append("0");
        }
        buf.append(minutes);
        buf.append(":");
        if (seconds < 10) {
            buf.append("0");
        }
        buf.append(seconds);
        return buf.toString();
    }

    public static Timestamp adjustTimestampNanosPrecision(Timestamp ts, int fsp, boolean serverRoundFracSecs) throws SQLException {
        int nanos;
        if (fsp < 0 || fsp > 6) {
            throw SQLError.createSQLException("fsp value must be in 0 to 6 range.", SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
        }
        Timestamp res = (Timestamp) ts.clone();
        int nanos2 = res.getNanos();
        double tail = Math.pow(10.0d, (double) (9 - fsp));
        if (serverRoundFracSecs) {
            nanos = ((int) Math.round(((double) nanos2) / tail)) * ((int) tail);
            if (nanos > 999999999) {
                nanos %= 1000000000;
                res.setTime(res.getTime() + 1000);
            }
        } else {
            nanos = ((int) (((double) nanos2) / tail)) * ((int) tail);
        }
        res.setNanos(nanos);
        return res;
    }

    public static String formatNanos(int nanos, boolean serverSupportsFracSecs, int fsp) throws SQLException {
        int nanos2;
        if (nanos < 0 || nanos > 999999999) {
            throw SQLError.createSQLException("nanos value must be in 0 to 999999999 range but was " + nanos, SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
        } else if (fsp < 0 || fsp > 6) {
            throw SQLError.createSQLException("fsp value must be in 0 to 6 range but was " + fsp, SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (ExceptionInterceptor) null);
        } else if (!serverSupportsFracSecs || fsp == 0 || nanos == 0 || (nanos2 = (int) (((double) nanos) / Math.pow(10.0d, (double) (9 - fsp)))) == 0) {
            return "0";
        } else {
            String nanosString = Integer.toString(nanos2);
            String nanosString2 = "000000000".substring(0, fsp - nanosString.length()) + nanosString;
            int pos = fsp - 1;
            while (nanosString2.charAt(pos) == '0') {
                pos--;
            }
            return nanosString2.substring(0, pos + 1);
        }
    }

    private static void loadTimeZoneMappings(ExceptionInterceptor exceptionInterceptor) throws SQLException {
        Properties properties = new Properties();
        timeZoneMappings = properties;
        try {
            properties.load(TimeUtil.class.getResourceAsStream(TIME_ZONE_MAPPINGS_RESOURCE));
            for (String tz : TimeZone.getAvailableIDs()) {
                if (!timeZoneMappings.containsKey(tz)) {
                    timeZoneMappings.put(tz, tz);
                }
            }
        } catch (IOException e) {
            throw SQLError.createSQLException(Messages.getString("TimeUtil.LoadTimeZoneMappingError"), SQLError.SQL_STATE_INVALID_CONNECTION_ATTRIBUTE, exceptionInterceptor);
        }
    }

    public static Timestamp truncateFractionalSeconds(Timestamp timestamp) {
        Timestamp truncatedTimestamp = new Timestamp(timestamp.getTime());
        truncatedTimestamp.setNanos(0);
        return truncatedTimestamp;
    }

    public static SimpleDateFormat getSimpleDateFormat(SimpleDateFormat cachedSimpleDateFormat, String pattern, Calendar cal, TimeZone tz) {
        SimpleDateFormat sdf = cachedSimpleDateFormat != null ? cachedSimpleDateFormat : new SimpleDateFormat(pattern, Locale.US);
        if (cal != null) {
            sdf.setCalendar((Calendar) cal.clone());
        }
        if (tz != null) {
            sdf.setTimeZone(tz);
        }
        return sdf;
    }

    public static Calendar setProlepticIfNeeded(Calendar origCalendar, Calendar refCalendar) {
        if (origCalendar == null || refCalendar == null || !(origCalendar instanceof GregorianCalendar) || !(refCalendar instanceof GregorianCalendar) || ((GregorianCalendar) refCalendar).getGregorianChange().getTime() != Long.MIN_VALUE) {
            return origCalendar;
        }
        GregorianCalendar gregorianCalendar = (GregorianCalendar) origCalendar.clone();
        gregorianCalendar.setGregorianChange(new java.sql.Date(Long.MIN_VALUE));
        gregorianCalendar.clear();
        return gregorianCalendar;
    }
}
