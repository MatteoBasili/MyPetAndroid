package com.application.mypetandroid.pwd_recovery;

public interface PwdRecoveryContract {

    interface PWDRecoveryListener {
        void onFailed(String message);

        void onSuccess(String password);
    }

    interface PWDRecoveryView {
        void hideProgressIndicator();

        void onFailed(String message);

        void onSuccess(String password);

        void showProgressIndicator();
    }

}
