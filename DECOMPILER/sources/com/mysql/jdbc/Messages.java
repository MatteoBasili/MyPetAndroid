package com.mysql.jdbc;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
    private static final String BUNDLE_NAME = "com.mysql.jdbc.LocalizedErrorMessages";
    private static final ResourceBundle RESOURCE_BUNDLE;

    static {
        ResourceBundle temp;
        try {
            temp = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault(), Messages.class.getClassLoader());
        } catch (Throwable t2) {
            try {
                RuntimeException rt = new RuntimeException("Can't load resource bundle due to underlying exception " + t.toString());
                rt.initCause(t2);
                throw rt;
            } catch (Throwable t22) {
                RESOURCE_BUNDLE = null;
                throw t22;
            }
        }
        RESOURCE_BUNDLE = temp;
    }

    public static String getString(String key) {
        ResourceBundle resourceBundle = RESOURCE_BUNDLE;
        if (resourceBundle == null) {
            throw new RuntimeException("Localized messages from resource bundle 'com.mysql.jdbc.LocalizedErrorMessages' not loaded during initialization of driver.");
        } else if (key != null) {
            try {
                String message = resourceBundle.getString(key);
                if (message == null) {
                    return "Missing error message for key '" + key + "'";
                }
                return message;
            } catch (MissingResourceException e) {
                return '!' + key + '!';
            }
        } else {
            throw new IllegalArgumentException("Message key can not be null");
        }
    }

    public static String getString(String key, Object[] args) {
        return MessageFormat.format(getString(key), args);
    }

    private Messages() {
    }
}
