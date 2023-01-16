package com.mysql.jdbc.log;

import com.mysql.jdbc.Util;

public class LogUtils {
    public static final String CALLER_INFORMATION_NOT_AVAILABLE = "Caller information not available";
    private static final String LINE_SEPARATOR;
    private static final int LINE_SEPARATOR_LENGTH;

    static {
        String property = System.getProperty("line.separator");
        LINE_SEPARATOR = property;
        LINE_SEPARATOR_LENGTH = property.length();
    }

    public static String findCallingClassAndMethod(Throwable t) {
        int endOfLine;
        String stackTraceAsString = Util.stackTraceToString(t);
        String callingClassAndMethod = CALLER_INFORMATION_NOT_AVAILABLE;
        int endInternalMethods = stackTraceAsString.lastIndexOf("com.mysql.jdbc");
        if (endInternalMethods != -1) {
            int compliancePackage = stackTraceAsString.indexOf("com.mysql.jdbc.compliance", endInternalMethods);
            if (compliancePackage != -1) {
                endOfLine = compliancePackage - LINE_SEPARATOR_LENGTH;
            } else {
                endOfLine = stackTraceAsString.indexOf(LINE_SEPARATOR, endInternalMethods);
            }
            if (endOfLine != -1) {
                String str = LINE_SEPARATOR;
                int i = LINE_SEPARATOR_LENGTH;
                int nextEndOfLine = stackTraceAsString.indexOf(str, endOfLine + i);
                callingClassAndMethod = nextEndOfLine != -1 ? stackTraceAsString.substring(i + endOfLine, nextEndOfLine) : stackTraceAsString.substring(i + endOfLine);
            }
        }
        if (callingClassAndMethod.startsWith("\tat ") || callingClassAndMethod.startsWith("at ")) {
            return callingClassAndMethod;
        }
        return "at " + callingClassAndMethod;
    }
}
