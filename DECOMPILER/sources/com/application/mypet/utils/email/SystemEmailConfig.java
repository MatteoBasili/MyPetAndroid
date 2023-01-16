package com.application.mypet.utils.email;

import com.application.mypet.BuildConfig;

public class SystemEmailConfig {
    private final String email = BuildConfig.EMAIL_USERNAME;
    private final String watchword = BuildConfig.EMAIL_WATCHWORD;

    public String getEmail() {
        return this.email;
    }

    public String getWatchword() {
        return this.watchword;
    }
}
