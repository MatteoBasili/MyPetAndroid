package com.application.mypetandroid.utils.input;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidator {

    public boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public boolean isValidEmail(String email) {
        Pattern p = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9._]*@[a-zA-Z0-9]+[.][a-zA-Z]++");
        Matcher m = p.matcher(email);
        return m.find() && m.group().equals(email);
    }

    public boolean isValidPassword(String password) {
        return password.length() >= 8 && password.length() <= 32;
    }

}
