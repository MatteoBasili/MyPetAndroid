package com.application.mypet.login.data;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.util.PatternsCompat;
import com.application.mypet.exceptions.ConnectionFailedException;
import com.application.mypet.exceptions.InvalidInputException;
import com.application.mypet.login.LoginContract;
import com.application.mypet.utils.db.DBConnection;
import com.mysql.jdbc.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class PasswordRecoveryInteractor {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String SQL_EXCEPTION = "SQL Error: ";
    private DBConnection dbConnection;
    private final LoginContract.PasswordRecoveryListener passwordRecoveryListener;

    public PasswordRecoveryInteractor(LoginContract.PasswordRecoveryListener passwordRecoveryListener2) {
        this.passwordRecoveryListener = passwordRecoveryListener2;
    }

    public boolean isValidInput(PasswordRecoveryCredentials passwordRecoveryCredentials) {
        return !hasInputError(passwordRecoveryCredentials);
    }

    public void recoverPassword(PasswordRecoveryCredentials passwordRecoveryCredentials) {
        new Handler().postDelayed(new PasswordRecoveryInteractor$$ExternalSyntheticLambda0(this, passwordRecoveryCredentials), 1500);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$recoverPassword$0$com-application-mypet-login-data-PasswordRecoveryInteractor  reason: not valid java name */
    public /* synthetic */ void m24lambda$recoverPassword$0$comapplicationmypetlogindataPasswordRecoveryInteractor(PasswordRecoveryCredentials passwordRecoveryCredentials) {
        DBConnection dBConnection = new DBConnection();
        this.dbConnection = dBConnection;
        Connection connection = dBConnection.getConnection();
        if (connection != null) {
            String userPassword = takePwdFromDB(passwordRecoveryCredentials, connection);
            this.dbConnection.closeConnection(connection);
            if (userPassword == null) {
                this.passwordRecoveryListener.onFailed("Invalid credentials!");
            } else {
                this.passwordRecoveryListener.onSuccess(userPassword);
            }
        } else {
            try {
                throw new ConnectionFailedException();
            } catch (ConnectionFailedException e) {
                this.passwordRecoveryListener.onFailed(e.getMessage());
            }
        }
    }

    private boolean hasInputError(PasswordRecoveryCredentials passwordRecoveryCredentials) {
        String email = passwordRecoveryCredentials.getEmail();
        String petName = passwordRecoveryCredentials.getPetName();
        try {
            if (TextUtils.isEmpty(email)) {
                throw new InvalidInputException("The email is empty");
            } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
                throw new InvalidInputException("The email is invalid");
            } else if (!TextUtils.isEmpty(petName)) {
                return false;
            } else {
                throw new InvalidInputException("The pet name is empty");
            }
        } catch (InvalidInputException e) {
            this.passwordRecoveryListener.onFailed(e.getMessage());
            return true;
        }
    }

    private String takePwdFromDB(PasswordRecoveryCredentials passwordRecoveryCredentials, Connection connection) {
        String email = passwordRecoveryCredentials.getEmail();
        String petName = passwordRecoveryCredentials.getPetName();
        CallableStatement stmt = null;
        String password = null;
        try {
            stmt = connection.prepareCall("{ call recover_password(?, ?, ?) }");
            stmt.setString(1, email);
            stmt.setString(2, petName);
            stmt.registerOutParameter(3, 12);
            stmt.execute();
            password = stmt.getString(3);
            if (stmt == null) {
                throw new AssertionError();
            }
        } catch (SQLException e) {
            Log.e(SQL_EXCEPTION, e.getMessage());
            if (0 == 0) {
                throw new AssertionError();
            }
        } catch (Throwable th) {
            if (0 == 0) {
                throw new AssertionError();
            }
            this.dbConnection.closeStatement((CallableStatement) null);
            throw th;
        }
        this.dbConnection.closeStatement(stmt);
        return password;
    }
}
