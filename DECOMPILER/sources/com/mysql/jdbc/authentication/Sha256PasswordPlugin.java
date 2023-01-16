package com.mysql.jdbc.authentication;

import com.mysql.jdbc.AuthenticationPlugin;
import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.ExportControlled;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.Security;
import com.mysql.jdbc.StringUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class Sha256PasswordPlugin implements AuthenticationPlugin {
    public static String PLUGIN_NAME = "sha256_password";
    protected Connection connection;
    protected String password = null;
    protected boolean publicKeyRequested = false;
    protected String publicKeyString = null;
    protected String seed = null;

    public void init(Connection conn, Properties props) throws SQLException {
        this.connection = conn;
        String pkURL = conn.getServerRSAPublicKeyFile();
        if (pkURL != null) {
            this.publicKeyString = readRSAKey(this.connection, pkURL);
        }
    }

    public void destroy() {
        this.password = null;
        this.seed = null;
        this.publicKeyRequested = false;
    }

    public String getProtocolPluginName() {
        return PLUGIN_NAME;
    }

    public boolean requiresConfidentiality() {
        return false;
    }

    public boolean isReusable() {
        return true;
    }

    public void setAuthenticationParameters(String user, String password2) {
        this.password = password2;
    }

    public boolean nextAuthenticationStep(Buffer fromServer, List<Buffer> toServer) throws SQLException {
        toServer.clear();
        String str = this.password;
        if (str == null || str.length() == 0 || fromServer == null) {
            toServer.add(new Buffer(new byte[]{0}));
        } else if (((MySQLConnection) this.connection).getIO().isSSLEstablished()) {
            try {
                Buffer bresp = new Buffer(StringUtils.getBytes(this.password, this.connection.getPasswordCharacterEncoding()));
                bresp.setPosition(bresp.getBufLength());
                int oldBufLength = bresp.getBufLength();
                bresp.writeByte((byte) 0);
                bresp.setBufLength(oldBufLength + 1);
                bresp.setPosition(0);
                toServer.add(bresp);
            } catch (UnsupportedEncodingException e) {
                throw SQLError.createSQLException(Messages.getString("Sha256PasswordPlugin.3", new Object[]{this.connection.getPasswordCharacterEncoding()}), SQLError.SQL_STATE_GENERAL_ERROR, (ExceptionInterceptor) null);
            }
        } else if (this.connection.getServerRSAPublicKeyFile() != null) {
            this.seed = fromServer.readString();
            toServer.add(new Buffer(encryptPassword()));
        } else if (!this.connection.getAllowPublicKeyRetrieval()) {
            throw SQLError.createSQLException(Messages.getString("Sha256PasswordPlugin.2"), SQLError.SQL_STATE_UNABLE_TO_CONNECT_TO_DATASOURCE, this.connection.getExceptionInterceptor());
        } else if (!this.publicKeyRequested || fromServer.getBufLength() <= 20) {
            this.seed = fromServer.readString();
            toServer.add(new Buffer(new byte[]{1}));
            this.publicKeyRequested = true;
        } else {
            this.publicKeyString = fromServer.readString();
            toServer.add(new Buffer(encryptPassword()));
            this.publicKeyRequested = false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public byte[] encryptPassword() throws SQLException {
        return encryptPassword("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
    }

    /* access modifiers changed from: protected */
    public byte[] encryptPassword(String transformation) throws SQLException {
        byte[] bArr;
        try {
            String str = this.password;
            if (str != null) {
                bArr = StringUtils.getBytesNullTerminated(str, this.connection.getPasswordCharacterEncoding());
            } else {
                bArr = new byte[]{0};
            }
            byte[] input = bArr;
            byte[] mysqlScrambleBuff = new byte[input.length];
            Security.xorString(input, mysqlScrambleBuff, this.seed.getBytes(), input.length);
            return ExportControlled.encryptWithRSAPublicKey(mysqlScrambleBuff, ExportControlled.decodeRSAPublicKey(this.publicKeyString, this.connection.getExceptionInterceptor()), transformation, this.connection.getExceptionInterceptor());
        } catch (UnsupportedEncodingException e) {
            throw SQLError.createSQLException(Messages.getString("Sha256PasswordPlugin.3", new Object[]{this.connection.getPasswordCharacterEncoding()}), SQLError.SQL_STATE_GENERAL_ERROR, (ExceptionInterceptor) null);
        }
    }

    private static String readRSAKey(Connection connection2, String pkPath) throws SQLException {
        byte[] fileBuf = new byte[2048];
        BufferedInputStream fileIn = null;
        try {
            BufferedInputStream fileIn2 = new BufferedInputStream(new FileInputStream(new File(pkPath).getCanonicalPath()));
            StringBuilder sb = new StringBuilder();
            while (true) {
                int read = fileIn2.read(fileBuf);
                int bytesRead = read;
                if (read != -1) {
                    sb.append(StringUtils.toAsciiString(fileBuf, 0, bytesRead));
                } else {
                    String res = sb.toString();
                    try {
                        fileIn2.close();
                        return res;
                    } catch (Exception ex) {
                        throw SQLError.createSQLException(Messages.getString("Sha256PasswordPlugin.1"), SQLError.SQL_STATE_GENERAL_ERROR, (Throwable) ex, connection2.getExceptionInterceptor());
                    }
                }
            }
        } catch (IOException ioEx) {
            if (connection2.getParanoid()) {
                throw SQLError.createSQLException(Messages.getString("Sha256PasswordPlugin.0", new Object[]{""}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, connection2.getExceptionInterceptor());
            }
            throw SQLError.createSQLException(Messages.getString("Sha256PasswordPlugin.0", new Object[]{"'" + pkPath + "'"}), SQLError.SQL_STATE_ILLEGAL_ARGUMENT, (Throwable) ioEx, connection2.getExceptionInterceptor());
        } catch (Throwable th) {
            if (fileIn != null) {
                try {
                    fileIn.close();
                } catch (Exception ex2) {
                    throw SQLError.createSQLException(Messages.getString("Sha256PasswordPlugin.1"), SQLError.SQL_STATE_GENERAL_ERROR, (Throwable) ex2, connection2.getExceptionInterceptor());
                }
            }
            throw th;
        }
    }

    public void reset() {
    }
}
