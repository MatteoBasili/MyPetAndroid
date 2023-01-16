package com.application.mypet.login.data;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import com.application.mypet.exceptions.ConnectionFailedException;
import com.application.mypet.exceptions.InvalidInputException;
import com.application.mypet.login.LoginContract;
import com.application.mypet.utils.db.DBConnection;
import com.mysql.jdbc.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginInteractor {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String SQL_EXCEPTION = "SQL Error: ";
    private DBConnection dbConnection;
    private final LoginContract.LoginListener loginListener;

    public LoginInteractor(LoginContract.LoginListener loginListener2) {
        this.loginListener = loginListener2;
    }

    public boolean isValidInput(LoginCredentials loginCredentials) {
        return !hasInputError(loginCredentials);
    }

    public void login(LoginCredentials loginCredentials) {
        new Handler().postDelayed(new LoginInteractor$$ExternalSyntheticLambda0(this, loginCredentials), 1500);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$login$0$com-application-mypet-login-data-LoginInteractor  reason: not valid java name */
    public /* synthetic */ void m23lambda$login$0$comapplicationmypetlogindataLoginInteractor(LoginCredentials loginCredentials) {
        DBConnection dBConnection = new DBConnection();
        this.dbConnection = dBConnection;
        Connection connection = dBConnection.getConnection();
        if (connection != null) {
            int role = checkDB(loginCredentials, connection);
            this.dbConnection.closeConnection(connection);
            if (role == 1 || role == 2) {
                this.loginListener.onSuccess(role);
            } else {
                this.loginListener.onFailed("Invalid credentials!");
            }
        } else {
            try {
                throw new ConnectionFailedException();
            } catch (ConnectionFailedException e) {
                this.loginListener.onFailed(e.getMessage());
            }
        }
    }

    private boolean hasInputError(LoginCredentials loginCredentials) {
        String username = loginCredentials.getUsername();
        String password = loginCredentials.getPassword();
        try {
            if (TextUtils.isEmpty(username)) {
                throw new InvalidInputException("The username is empty");
            } else if (TextUtils.isEmpty(password)) {
                throw new InvalidInputException("The password is empty");
            } else if (password.length() >= 8 && password.length() <= 32) {
                return false;
            } else {
                throw new InvalidInputException("The password must be between 8 and 32 characters");
            }
        } catch (InvalidInputException e) {
            this.loginListener.onFailed(e.getMessage());
            return true;
        }
    }

    private int checkDB(LoginCredentials loginCredentials, Connection connection) {
        String username = loginCredentials.getUsername();
        String password = loginCredentials.getPassword();
        CallableStatement stmt = null;
        int role = 0;
        try {
            stmt = connection.prepareCall("{ call login(?, ?, ?) }");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.registerOutParameter(3, 4);
            stmt.execute();
            role = stmt.getInt(3);
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
        return role;
    }
}
