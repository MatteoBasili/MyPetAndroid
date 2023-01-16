package com.mysql.jdbc.jdbc2.optional;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MysqlErrorNumbers;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.Util;
import com.mysql.jdbc.log.Log;
import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class MysqlXAConnection extends MysqlPooledConnection implements XAConnection, XAResource {
    private static final Constructor<?> JDBC_4_XA_CONNECTION_WRAPPER_CTOR;
    private static final int MAX_COMMAND_LENGTH = 300;
    private static final Map<Integer, Integer> MYSQL_ERROR_CODES_TO_XA_ERROR_CODES;
    private Log log;
    protected boolean logXaCommands;
    private Connection underlyingConnection;

    static {
        HashMap<Integer, Integer> temp = new HashMap<>();
        temp.put(Integer.valueOf(MysqlErrorNumbers.ER_XAER_NOTA), -4);
        temp.put(Integer.valueOf(MysqlErrorNumbers.ER_XAER_INVAL), -5);
        temp.put(Integer.valueOf(MysqlErrorNumbers.ER_XAER_RMFAIL), -7);
        temp.put(Integer.valueOf(MysqlErrorNumbers.ER_XAER_OUTSIDE), -9);
        temp.put(Integer.valueOf(MysqlErrorNumbers.ER_XA_RMERR), -3);
        temp.put(Integer.valueOf(MysqlErrorNumbers.ER_XA_RBROLLBACK), 100);
        temp.put(Integer.valueOf(MysqlErrorNumbers.ER_XAER_DUPID), -8);
        temp.put(Integer.valueOf(MysqlErrorNumbers.ER_XA_RBTIMEOUT), 106);
        temp.put(Integer.valueOf(MysqlErrorNumbers.ER_XA_RBDEADLOCK), 102);
        MYSQL_ERROR_CODES_TO_XA_ERROR_CODES = Collections.unmodifiableMap(temp);
        if (Util.isJdbc4()) {
            try {
                JDBC_4_XA_CONNECTION_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4MysqlXAConnection").getConstructor(new Class[]{Connection.class, Boolean.TYPE});
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e2) {
                throw new RuntimeException(e2);
            } catch (ClassNotFoundException e3) {
                throw new RuntimeException(e3);
            }
        } else {
            JDBC_4_XA_CONNECTION_WRAPPER_CTOR = null;
        }
    }

    protected static MysqlXAConnection getInstance(Connection mysqlConnection, boolean logXaCommands2) throws SQLException {
        if (!Util.isJdbc4()) {
            return new MysqlXAConnection(mysqlConnection, logXaCommands2);
        }
        return (MysqlXAConnection) Util.handleNewInstance(JDBC_4_XA_CONNECTION_WRAPPER_CTOR, new Object[]{mysqlConnection, Boolean.valueOf(logXaCommands2)}, mysqlConnection.getExceptionInterceptor());
    }

    public MysqlXAConnection(Connection connection, boolean logXaCommands2) throws SQLException {
        super(connection);
        this.underlyingConnection = connection;
        this.log = connection.getLog();
        this.logXaCommands = logXaCommands2;
    }

    public XAResource getXAResource() throws SQLException {
        return this;
    }

    public int getTransactionTimeout() throws XAException {
        return 0;
    }

    public boolean setTransactionTimeout(int arg0) throws XAException {
        return false;
    }

    public boolean isSameRM(XAResource xares) throws XAException {
        if (xares instanceof MysqlXAConnection) {
            return this.underlyingConnection.isSameResource(((MysqlXAConnection) xares).underlyingConnection);
        }
        return false;
    }

    public Xid[] recover(int flag) throws XAException {
        return recover(this.underlyingConnection, flag);
    }

    /* JADX WARNING: type inference failed for: r1v11, types: [java.lang.Throwable, com.mysql.jdbc.jdbc2.optional.MysqlXAException] */
    /* JADX WARNING: type inference failed for: r1v12, types: [java.lang.Throwable, com.mysql.jdbc.jdbc2.optional.MysqlXAException] */
    protected static Xid[] recover(java.sql.Connection c, int flag) throws XAException {
        boolean startRscan = (flag & 16777216) > 0;
        boolean endRscan = (flag & 8388608) > 0;
        if (!startRscan && !endRscan && flag != 0) {
            throw new MysqlXAException(-5, Messages.getString("MysqlXAConnection.001"), (String) null);
        } else if (!startRscan) {
            return new Xid[0];
        } else {
            ResultSet rs = null;
            Statement stmt = null;
            List<MysqlXid> recoveredXidList = new ArrayList<>();
            try {
                Statement stmt2 = c.createStatement();
                ResultSet rs2 = stmt2.executeQuery("XA RECOVER");
                while (rs2.next()) {
                    int formatId = rs2.getInt(1);
                    int gtridLength = rs2.getInt(2);
                    int bqualLength = rs2.getInt(3);
                    byte[] gtridAndBqual = rs2.getBytes(4);
                    byte[] gtrid = new byte[gtridLength];
                    byte[] bqual = new byte[bqualLength];
                    if (gtridAndBqual.length == gtridLength + bqualLength) {
                        System.arraycopy(gtridAndBqual, 0, gtrid, 0, gtridLength);
                        System.arraycopy(gtridAndBqual, gtridLength, bqual, 0, bqualLength);
                        recoveredXidList.add(new MysqlXid(gtrid, bqual, formatId));
                    } else {
                        throw new MysqlXAException(105, Messages.getString("MysqlXAConnection.002"), (String) null);
                    }
                }
                if (rs2 != null) {
                    try {
                        rs2.close();
                    } catch (SQLException e) {
                        throw mapXAExceptionFromSQLException(e);
                    }
                }
                if (stmt2 != null) {
                    try {
                        stmt2.close();
                    } catch (SQLException e2) {
                        throw mapXAExceptionFromSQLException(e2);
                    }
                }
                int numXids = recoveredXidList.size();
                Xid[] asXids = new Xid[numXids];
                Object[] asObjects = recoveredXidList.toArray();
                for (int i = 0; i < numXids; i++) {
                    asXids[i] = (Xid) asObjects[i];
                }
                return asXids;
            } catch (SQLException sqlEx) {
                throw mapXAExceptionFromSQLException(sqlEx);
            } catch (Throwable th) {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e3) {
                        throw mapXAExceptionFromSQLException(e3);
                    }
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e4) {
                        throw mapXAExceptionFromSQLException(e4);
                    }
                }
                throw th;
            }
        }
    }

    public int prepare(Xid xid) throws XAException {
        StringBuilder commandBuf = new StringBuilder(MAX_COMMAND_LENGTH);
        commandBuf.append("XA PREPARE ");
        appendXid(commandBuf, xid);
        dispatchCommand(commandBuf.toString());
        return 0;
    }

    public void forget(Xid xid) throws XAException {
    }

    public void rollback(Xid xid) throws XAException {
        StringBuilder commandBuf = new StringBuilder(MAX_COMMAND_LENGTH);
        commandBuf.append("XA ROLLBACK ");
        appendXid(commandBuf, xid);
        try {
            dispatchCommand(commandBuf.toString());
        } finally {
            this.underlyingConnection.setInGlobalTx(false);
        }
    }

    public void end(Xid xid, int flags) throws XAException {
        StringBuilder commandBuf = new StringBuilder(MAX_COMMAND_LENGTH);
        commandBuf.append("XA END ");
        appendXid(commandBuf, xid);
        switch (flags) {
            case 33554432:
                commandBuf.append(" SUSPEND");
                break;
            case 67108864:
            case 536870912:
                break;
            default:
                throw new XAException(-5);
        }
        dispatchCommand(commandBuf.toString());
    }

    public void start(Xid xid, int flags) throws XAException {
        StringBuilder commandBuf = new StringBuilder(MAX_COMMAND_LENGTH);
        commandBuf.append("XA START ");
        appendXid(commandBuf, xid);
        switch (flags) {
            case 0:
                break;
            case 2097152:
                commandBuf.append(" JOIN");
                break;
            case 134217728:
                commandBuf.append(" RESUME");
                break;
            default:
                throw new XAException(-5);
        }
        dispatchCommand(commandBuf.toString());
        this.underlyingConnection.setInGlobalTx(true);
    }

    public void commit(Xid xid, boolean onePhase) throws XAException {
        StringBuilder commandBuf = new StringBuilder(MAX_COMMAND_LENGTH);
        commandBuf.append("XA COMMIT ");
        appendXid(commandBuf, xid);
        if (onePhase) {
            commandBuf.append(" ONE PHASE");
        }
        try {
            dispatchCommand(commandBuf.toString());
        } finally {
            this.underlyingConnection.setInGlobalTx(false);
        }
    }

    private ResultSet dispatchCommand(String command) throws XAException {
        Statement stmt = null;
        try {
            if (this.logXaCommands) {
                this.log.logDebug("Executing XA statement: " + command);
            }
            Statement stmt2 = this.underlyingConnection.createStatement();
            stmt2.execute(command);
            ResultSet resultSet = stmt2.getResultSet();
            if (stmt2 != null) {
                try {
                    stmt2.close();
                } catch (SQLException e) {
                }
            }
            ResultSet resultSet2 = resultSet;
            return resultSet;
        } catch (SQLException sqlEx) {
            throw mapXAExceptionFromSQLException(sqlEx);
        } catch (Throwable th) {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e2) {
                }
            }
            throw th;
        }
    }

    protected static XAException mapXAExceptionFromSQLException(SQLException sqlEx) {
        Integer xaCode = MYSQL_ERROR_CODES_TO_XA_ERROR_CODES.get(Integer.valueOf(sqlEx.getErrorCode()));
        if (xaCode != null) {
            return new MysqlXAException(xaCode.intValue(), sqlEx.getMessage(), (String) null).initCause(sqlEx);
        }
        return new MysqlXAException(-7, Messages.getString("MysqlXAConnection.003"), (String) null).initCause(sqlEx);
    }

    private static void appendXid(StringBuilder builder, Xid xid) {
        byte[] gtrid = xid.getGlobalTransactionId();
        byte[] btrid = xid.getBranchQualifier();
        if (gtrid != null) {
            StringUtils.appendAsHex(builder, gtrid);
        }
        builder.append(',');
        if (btrid != null) {
            StringUtils.appendAsHex(builder, btrid);
        }
        builder.append(',');
        StringUtils.appendAsHex(builder, xid.getFormatId());
    }

    public synchronized java.sql.Connection getConnection() throws SQLException {
        return getConnection(false, true);
    }
}
