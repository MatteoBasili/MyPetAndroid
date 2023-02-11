package com.application.mypetandroid.utils.email;

import com.application.mypetandroid.BuildConfig;

public class SystemEmailConfig {

    private static final String EMAIL = BuildConfig.EMAIL_USERNAME;
    private static final String WATCHWORD = BuildConfig.EMAIL_WATCHWORD;

    public String getEmail() {
        return EMAIL;
    }

    public String getWatchword() {
        return WATCHWORD;
    }

}
