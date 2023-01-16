package com.mysql.jdbc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.sql.SQLException;

public class Clob implements java.sql.Clob, OutputStreamWatcher, WriterWatcher {
    private String charData;
    private ExceptionInterceptor exceptionInterceptor;

    Clob(ExceptionInterceptor exceptionInterceptor2) {
        this.charData = "";
        this.exceptionInterceptor = exceptionInterceptor2;
    }

    Clob(String charDataInit, ExceptionInterceptor exceptionInterceptor2) {
        this.charData = charDataInit;
        this.exceptionInterceptor = exceptionInterceptor2;
    }

    public InputStream getAsciiStream() throws SQLException {
        if (this.charData != null) {
            return new ByteArrayInputStream(StringUtils.getBytes(this.charData));
        }
        return null;
    }

    public Reader getCharacterStream() throws SQLException {
        if (this.charData != null) {
            return new StringReader(this.charData);
        }
        return null;
    }

    public String getSubString(long startPos, int length) throws SQLException {
        if (startPos >= 1) {
            int adjustedStartPos = ((int) startPos) - 1;
            int adjustedEndIndex = adjustedStartPos + length;
            String str = this.charData;
            if (str == null) {
                return null;
            }
            if (adjustedEndIndex <= str.length()) {
                return this.charData.substring(adjustedStartPos, adjustedEndIndex);
            }
            throw SQLError.createSQLException(Messages.getString("Clob.7"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        }
        throw SQLError.createSQLException(Messages.getString("Clob.6"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
    }

    public long length() throws SQLException {
        String str = this.charData;
        if (str != null) {
            return (long) str.length();
        }
        return 0;
    }

    public long position(java.sql.Clob arg0, long arg1) throws SQLException {
        return position(arg0.getSubString(1, (int) arg0.length()), arg1);
    }

    public long position(String stringToFind, long startPos) throws SQLException {
        if (startPos >= 1) {
            String str = this.charData;
            if (str == null) {
                return -1;
            }
            if (startPos - 1 <= ((long) str.length())) {
                int pos = this.charData.indexOf(stringToFind, (int) (startPos - 1));
                if (pos == -1) {
                    return -1;
                }
                return (long) (pos + 1);
            }
            throw SQLError.createSQLException(Messages.getString("Clob.10"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        }
        throw SQLError.createSQLException(Messages.getString("Clob.8") + startPos + Messages.getString("Clob.9"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
    }

    public OutputStream setAsciiStream(long indexToWriteAt) throws SQLException {
        if (indexToWriteAt >= 1) {
            WatchableOutputStream bytesOut = new WatchableOutputStream();
            bytesOut.setWatcher(this);
            if (indexToWriteAt > 0) {
                bytesOut.write(StringUtils.getBytes(this.charData), 0, (int) (indexToWriteAt - 1));
            }
            return bytesOut;
        }
        throw SQLError.createSQLException(Messages.getString("Clob.0"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
    }

    public Writer setCharacterStream(long indexToWriteAt) throws SQLException {
        if (indexToWriteAt >= 1) {
            WatchableWriter writer = new WatchableWriter();
            writer.setWatcher(this);
            if (indexToWriteAt > 1) {
                writer.write(this.charData, 0, (int) (indexToWriteAt - 1));
            }
            return writer;
        }
        throw SQLError.createSQLException(Messages.getString("Clob.1"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
    }

    public int setString(long pos, String str) throws SQLException {
        if (pos < 1) {
            throw SQLError.createSQLException(Messages.getString("Clob.2"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } else if (str != null) {
            StringBuilder charBuf = new StringBuilder(this.charData);
            long pos2 = pos - 1;
            int strLength = str.length();
            charBuf.replace((int) pos2, (int) (((long) strLength) + pos2), str);
            this.charData = charBuf.toString();
            return strLength;
        } else {
            throw SQLError.createSQLException(Messages.getString("Clob.3"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        }
    }

    public int setString(long pos, String str, int offset, int len) throws SQLException {
        if (pos < 1) {
            throw SQLError.createSQLException(Messages.getString("Clob.4"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        } else if (str != null) {
            StringBuilder charBuf = new StringBuilder(this.charData);
            long pos2 = pos - 1;
            try {
                String replaceString = str.substring(offset, offset + len);
                charBuf.replace((int) pos2, (int) (((long) replaceString.length()) + pos2), replaceString);
                this.charData = charBuf.toString();
                return len;
            } catch (StringIndexOutOfBoundsException e) {
                throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (Throwable) e, this.exceptionInterceptor);
            }
        } else {
            throw SQLError.createSQLException(Messages.getString("Clob.5"), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, this.exceptionInterceptor);
        }
    }

    public void streamClosed(WatchableOutputStream out) {
        int streamSize = out.size();
        if (streamSize < this.charData.length()) {
            try {
                out.write(StringUtils.getBytes(this.charData, (String) null, (String) null, false, (MySQLConnection) null, this.exceptionInterceptor), streamSize, this.charData.length() - streamSize);
            } catch (SQLException e) {
            }
        }
        this.charData = StringUtils.toAsciiString(out.toByteArray());
    }

    public void truncate(long length) throws SQLException {
        if (length <= ((long) this.charData.length())) {
            this.charData = this.charData.substring(0, (int) length);
            return;
        }
        throw SQLError.createSQLException(Messages.getString("Clob.11") + this.charData.length() + Messages.getString("Clob.12") + length + Messages.getString("Clob.13"), this.exceptionInterceptor);
    }

    public void writerClosed(char[] charDataBeingWritten) {
        this.charData = new String(charDataBeingWritten);
    }

    public void writerClosed(WatchableWriter out) {
        int dataLength = out.size();
        if (dataLength < this.charData.length()) {
            String str = this.charData;
            out.write(str, dataLength, str.length() - dataLength);
        }
        this.charData = out.toString();
    }

    public void free() throws SQLException {
        this.charData = null;
    }

    public Reader getCharacterStream(long pos, long length) throws SQLException {
        return new StringReader(getSubString(pos, (int) length));
    }
}
