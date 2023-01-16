package com.mysql.jdbc.authentication;

import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.Security;
import com.mysql.jdbc.StringUtils;
import java.io.UnsupportedEncodingException;
import java.security.DigestException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class CachingSha2PasswordPlugin extends Sha256PasswordPlugin {
    public static String PLUGIN_NAME = "caching_sha2_password";
    private AuthStage stage = AuthStage.FAST_AUTH_SEND_SCRAMBLE;

    public enum AuthStage {
        FAST_AUTH_SEND_SCRAMBLE,
        FAST_AUTH_READ_RESULT,
        FAST_AUTH_COMPLETE,
        FULL_AUTH
    }

    public void init(Connection conn, Properties props) throws SQLException {
        super.init(conn, props);
        this.stage = AuthStage.FAST_AUTH_SEND_SCRAMBLE;
    }

    public void destroy() {
        this.stage = AuthStage.FAST_AUTH_SEND_SCRAMBLE;
        super.destroy();
    }

    public String getProtocolPluginName() {
        return PLUGIN_NAME;
    }

    public boolean nextAuthenticationStep(Buffer buffer, List<Buffer> list) throws SQLException {
        list.clear();
        if (this.password == null || this.password.length() == 0 || buffer == null) {
            list.add(new Buffer(new byte[]{0}));
        } else if (this.stage == AuthStage.FAST_AUTH_SEND_SCRAMBLE) {
            this.seed = buffer.readString();
            try {
                list.add(new Buffer(Security.scrambleCachingSha2(StringUtils.getBytes(this.password, this.connection.getPasswordCharacterEncoding()), this.seed.getBytes())));
                this.stage = AuthStage.FAST_AUTH_READ_RESULT;
                return true;
            } catch (DigestException e) {
                throw SQLError.createSQLException(e.getMessage(), SQLError.SQL_STATE_GENERAL_ERROR, (Throwable) e, (ExceptionInterceptor) null);
            } catch (UnsupportedEncodingException e2) {
                throw SQLError.createSQLException(e2.getMessage(), SQLError.SQL_STATE_GENERAL_ERROR, (Throwable) e2, (ExceptionInterceptor) null);
            }
        } else {
            if (this.stage == AuthStage.FAST_AUTH_READ_RESULT) {
                switch (buffer.getByteBuffer()[0]) {
                    case 3:
                        this.stage = AuthStage.FAST_AUTH_COMPLETE;
                        return true;
                    case 4:
                        this.stage = AuthStage.FULL_AUTH;
                        break;
                    default:
                        throw SQLError.createSQLException("Unknown server response after fast auth.", SQLError.SQL_STATE_UNABLE_TO_CONNECT_TO_DATASOURCE, this.connection.getExceptionInterceptor());
                }
            }
            if (((MySQLConnection) this.connection).getIO().isSSLEstablished()) {
                try {
                    Buffer buffer2 = new Buffer(StringUtils.getBytes(this.password, this.connection.getPasswordCharacterEncoding()));
                    buffer2.setPosition(buffer2.getBufLength());
                    int bufLength = buffer2.getBufLength();
                    buffer2.writeByte((byte) 0);
                    buffer2.setBufLength(bufLength + 1);
                    buffer2.setPosition(0);
                    list.add(buffer2);
                } catch (UnsupportedEncodingException e3) {
                    throw SQLError.createSQLException(Messages.getString("Sha256PasswordPlugin.3", new Object[]{this.connection.getPasswordCharacterEncoding()}), SQLError.SQL_STATE_GENERAL_ERROR, (ExceptionInterceptor) null);
                }
            } else if (this.connection.getServerRSAPublicKeyFile() != null) {
                list.add(new Buffer(encryptPassword()));
            } else if (!this.connection.getAllowPublicKeyRetrieval()) {
                throw SQLError.createSQLException(Messages.getString("Sha256PasswordPlugin.2"), SQLError.SQL_STATE_UNABLE_TO_CONNECT_TO_DATASOURCE, this.connection.getExceptionInterceptor());
            } else if (!this.publicKeyRequested || buffer.getBufLength() <= 20) {
                list.add(new Buffer(new byte[]{2}));
                this.publicKeyRequested = true;
            } else {
                this.publicKeyString = buffer.readString();
                list.add(new Buffer(encryptPassword()));
                this.publicKeyRequested = false;
            }
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public byte[] encryptPassword() throws SQLException {
        if (this.connection.versionMeetsMinimum(8, 0, 5)) {
            return super.encryptPassword();
        }
        return super.encryptPassword("RSA/ECB/PKCS1Padding");
    }

    public void reset() {
        this.stage = AuthStage.FAST_AUTH_SEND_SCRAMBLE;
    }
}
