package com.application.mypetandroid.utils.singleton_examples;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardSingletonClass {

    private static KeyboardSingletonClass instance = null;

    protected KeyboardSingletonClass() {
        // void constructor
    }

    public static synchronized KeyboardSingletonClass getSingletonInstance() {
        if (KeyboardSingletonClass.instance == null)
            KeyboardSingletonClass.instance = new KeyboardSingletonClass();
        return instance;
    }

    public void hide(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
