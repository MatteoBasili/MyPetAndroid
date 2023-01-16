package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;

public final class ActivityPasswordRecoveryBinding implements ViewBinding {
    public final ToolbarWithBackBinding include2;
    public final ConstraintLayout intConstLayout;
    public final ProgressBar progressBar;
    private final ConstraintLayout rootView;
    public final ScrollView scrollView;
    public final Button sendButton;
    public final TextView textView;
    public final TextView textView6;
    public final TextView textView7;
    public final TextView textView8;
    public final EditText userEmail;
    public final EditText userPet;

    private ActivityPasswordRecoveryBinding(ConstraintLayout rootView2, ToolbarWithBackBinding include22, ConstraintLayout intConstLayout2, ProgressBar progressBar2, ScrollView scrollView2, Button sendButton2, TextView textView2, TextView textView62, TextView textView72, TextView textView82, EditText userEmail2, EditText userPet2) {
        this.rootView = rootView2;
        this.include2 = include22;
        this.intConstLayout = intConstLayout2;
        this.progressBar = progressBar2;
        this.scrollView = scrollView2;
        this.sendButton = sendButton2;
        this.textView = textView2;
        this.textView6 = textView62;
        this.textView7 = textView72;
        this.textView8 = textView82;
        this.userEmail = userEmail2;
        this.userPet = userPet2;
    }

    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static ActivityPasswordRecoveryBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static ActivityPasswordRecoveryBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.activity_password_recovery, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ActivityPasswordRecoveryBinding bind(View rootView2) {
        View view = rootView2;
        int id = R.id.include2;
        View include22 = ViewBindings.findChildViewById(view, R.id.include2);
        if (include22 != null) {
            ToolbarWithBackBinding binding_include2 = ToolbarWithBackBinding.bind(include22);
            id = R.id.int_const_layout;
            ConstraintLayout intConstLayout2 = (ConstraintLayout) ViewBindings.findChildViewById(view, R.id.int_const_layout);
            if (intConstLayout2 != null) {
                id = R.id.progressBar;
                ProgressBar progressBar2 = (ProgressBar) ViewBindings.findChildViewById(view, R.id.progressBar);
                if (progressBar2 != null) {
                    id = R.id.scrollView;
                    ScrollView scrollView2 = (ScrollView) ViewBindings.findChildViewById(view, R.id.scrollView);
                    if (scrollView2 != null) {
                        id = R.id.send_button;
                        Button sendButton2 = (Button) ViewBindings.findChildViewById(view, R.id.send_button);
                        if (sendButton2 != null) {
                            id = R.id.textView;
                            TextView textView2 = (TextView) ViewBindings.findChildViewById(view, R.id.textView);
                            if (textView2 != null) {
                                id = R.id.textView6;
                                TextView textView62 = (TextView) ViewBindings.findChildViewById(view, R.id.textView6);
                                if (textView62 != null) {
                                    id = R.id.textView7;
                                    TextView textView72 = (TextView) ViewBindings.findChildViewById(view, R.id.textView7);
                                    if (textView72 != null) {
                                        id = R.id.textView8;
                                        TextView textView82 = (TextView) ViewBindings.findChildViewById(view, R.id.textView8);
                                        if (textView82 != null) {
                                            id = R.id.user_email;
                                            EditText userEmail2 = (EditText) ViewBindings.findChildViewById(view, R.id.user_email);
                                            if (userEmail2 != null) {
                                                id = R.id.user_pet;
                                                EditText userPet2 = (EditText) ViewBindings.findChildViewById(view, R.id.user_pet);
                                                if (userPet2 != null) {
                                                    return new ActivityPasswordRecoveryBinding((ConstraintLayout) view, binding_include2, intConstLayout2, progressBar2, scrollView2, sendButton2, textView2, textView62, textView72, textView82, userEmail2, userPet2);
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
