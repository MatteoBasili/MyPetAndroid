package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;

public final class ActivityLoginBinding implements ViewBinding {
    public final Button enterButton;
    public final RelativeLayout facebookLayout;
    public final TextView forgotPwd;
    public final RelativeLayout googleLayout;
    public final ImageView hidePwd;
    public final ImageView imageView;
    public final ImageView imageView4;
    public final DefaultToolbarBinding include;
    public final ConstraintLayout intConstLayout;
    public final ConstraintLayout loginActivity;
    public final ProgressBar progressBar;
    public final EditText pwd;
    public final CheckBox remMe;
    private final ConstraintLayout rootView;
    public final ImageView showPwd;
    public final Button signInButton;
    public final TextView textView;
    public final TextView textView10;
    public final TextView textView11;
    public final TextView textView6;
    public final TextView textView7;
    public final TextView textView8;
    public final EditText username;

    private ActivityLoginBinding(ConstraintLayout rootView2, Button enterButton2, RelativeLayout facebookLayout2, TextView forgotPwd2, RelativeLayout googleLayout2, ImageView hidePwd2, ImageView imageView2, ImageView imageView42, DefaultToolbarBinding include2, ConstraintLayout intConstLayout2, ConstraintLayout loginActivity2, ProgressBar progressBar2, EditText pwd2, CheckBox remMe2, ImageView showPwd2, Button signInButton2, TextView textView2, TextView textView102, TextView textView112, TextView textView62, TextView textView72, TextView textView82, EditText username2) {
        this.rootView = rootView2;
        this.enterButton = enterButton2;
        this.facebookLayout = facebookLayout2;
        this.forgotPwd = forgotPwd2;
        this.googleLayout = googleLayout2;
        this.hidePwd = hidePwd2;
        this.imageView = imageView2;
        this.imageView4 = imageView42;
        this.include = include2;
        this.intConstLayout = intConstLayout2;
        this.loginActivity = loginActivity2;
        this.progressBar = progressBar2;
        this.pwd = pwd2;
        this.remMe = remMe2;
        this.showPwd = showPwd2;
        this.signInButton = signInButton2;
        this.textView = textView2;
        this.textView10 = textView102;
        this.textView11 = textView112;
        this.textView6 = textView62;
        this.textView7 = textView72;
        this.textView8 = textView82;
        this.username = username2;
    }

    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static ActivityLoginBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static ActivityLoginBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.activity_login, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ActivityLoginBinding bind(View rootView2) {
        View view = rootView2;
        int id = R.id.enter_button;
        Button enterButton2 = (Button) ViewBindings.findChildViewById(view, R.id.enter_button);
        if (enterButton2 != null) {
            id = R.id.facebookLayout;
            RelativeLayout facebookLayout2 = (RelativeLayout) ViewBindings.findChildViewById(view, R.id.facebookLayout);
            if (facebookLayout2 != null) {
                id = R.id.forgot_pwd;
                TextView forgotPwd2 = (TextView) ViewBindings.findChildViewById(view, R.id.forgot_pwd);
                if (forgotPwd2 != null) {
                    id = R.id.googleLayout;
                    RelativeLayout googleLayout2 = (RelativeLayout) ViewBindings.findChildViewById(view, R.id.googleLayout);
                    if (googleLayout2 != null) {
                        id = R.id.hide_pwd;
                        ImageView hidePwd2 = (ImageView) ViewBindings.findChildViewById(view, R.id.hide_pwd);
                        if (hidePwd2 != null) {
                            id = R.id.imageView;
                            ImageView imageView2 = (ImageView) ViewBindings.findChildViewById(view, R.id.imageView);
                            if (imageView2 != null) {
                                id = R.id.imageView4;
                                ImageView imageView42 = (ImageView) ViewBindings.findChildViewById(view, R.id.imageView4);
                                if (imageView42 != null) {
                                    id = R.id.include;
                                    View include2 = ViewBindings.findChildViewById(view, R.id.include);
                                    if (include2 != null) {
                                        DefaultToolbarBinding binding_include = DefaultToolbarBinding.bind(include2);
                                        id = R.id.int_const_layout;
                                        ConstraintLayout intConstLayout2 = (ConstraintLayout) ViewBindings.findChildViewById(view, R.id.int_const_layout);
                                        if (intConstLayout2 != null) {
                                            ConstraintLayout loginActivity2 = (ConstraintLayout) view;
                                            id = R.id.progressBar;
                                            ProgressBar progressBar2 = (ProgressBar) ViewBindings.findChildViewById(view, R.id.progressBar);
                                            if (progressBar2 != null) {
                                                id = R.id.pwd;
                                                EditText pwd2 = (EditText) ViewBindings.findChildViewById(view, R.id.pwd);
                                                if (pwd2 != null) {
                                                    id = R.id.rem_me;
                                                    CheckBox remMe2 = (CheckBox) ViewBindings.findChildViewById(view, R.id.rem_me);
                                                    if (remMe2 != null) {
                                                        id = R.id.show_pwd;
                                                        ImageView showPwd2 = (ImageView) ViewBindings.findChildViewById(view, R.id.show_pwd);
                                                        if (showPwd2 != null) {
                                                            id = R.id.sign_in_button;
                                                            Button signInButton2 = (Button) ViewBindings.findChildViewById(view, R.id.sign_in_button);
                                                            if (signInButton2 != null) {
                                                                id = R.id.textView;
                                                                TextView textView2 = (TextView) ViewBindings.findChildViewById(view, R.id.textView);
                                                                if (textView2 != null) {
                                                                    id = R.id.textView10;
                                                                    TextView textView102 = (TextView) ViewBindings.findChildViewById(view, R.id.textView10);
                                                                    if (textView102 != null) {
                                                                        id = R.id.textView11;
                                                                        TextView textView112 = (TextView) ViewBindings.findChildViewById(view, R.id.textView11);
                                                                        if (textView112 != null) {
                                                                            id = R.id.textView6;
                                                                            TextView textView62 = (TextView) ViewBindings.findChildViewById(view, R.id.textView6);
                                                                            if (textView62 != null) {
                                                                                id = R.id.textView7;
                                                                                TextView textView72 = (TextView) ViewBindings.findChildViewById(view, R.id.textView7);
                                                                                if (textView72 != null) {
                                                                                    id = R.id.textView8;
                                                                                    TextView textView82 = (TextView) ViewBindings.findChildViewById(view, R.id.textView8);
                                                                                    if (textView82 != null) {
                                                                                        id = R.id.username;
                                                                                        EditText username2 = (EditText) ViewBindings.findChildViewById(view, R.id.username);
                                                                                        if (username2 != null) {
                                                                                            return new ActivityLoginBinding((ConstraintLayout) view, enterButton2, facebookLayout2, forgotPwd2, googleLayout2, hidePwd2, imageView2, imageView42, binding_include, intConstLayout2, loginActivity2, progressBar2, pwd2, remMe2, showPwd2, signInButton2, textView2, textView102, textView112, textView62, textView72, textView82, username2);
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(id)));
    }
}
