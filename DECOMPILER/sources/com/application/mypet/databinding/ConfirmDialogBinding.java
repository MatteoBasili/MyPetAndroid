package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;

public final class ConfirmDialogBinding implements ViewBinding {
    public final LinearLayout bottomSheetExitLinear;
    public final Button noButton;
    public final TextView question;
    private final LinearLayout rootView;
    public final Button yesButton;

    private ConfirmDialogBinding(LinearLayout rootView2, LinearLayout bottomSheetExitLinear2, Button noButton2, TextView question2, Button yesButton2) {
        this.rootView = rootView2;
        this.bottomSheetExitLinear = bottomSheetExitLinear2;
        this.noButton = noButton2;
        this.question = question2;
        this.yesButton = yesButton2;
    }

    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ConfirmDialogBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static ConfirmDialogBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.confirm_dialog, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ConfirmDialogBinding bind(View rootView2) {
        LinearLayout bottomSheetExitLinear2 = (LinearLayout) rootView2;
        int id = R.id.no_button;
        Button noButton2 = (Button) ViewBindings.findChildViewById(rootView2, R.id.no_button);
        if (noButton2 != null) {
            id = R.id.question;
            TextView question2 = (TextView) ViewBindings.findChildViewById(rootView2, R.id.question);
            if (question2 != null) {
                Button yesButton2 = (Button) ViewBindings.findChildViewById(rootView2, R.id.yes_button);
                if (yesButton2 != null) {
                    return new ConfirmDialogBinding((LinearLayout) rootView2, bottomSheetExitLinear2, noButton2, question2, yesButton2);
                }
                id = R.id.yes_button;
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(id)));
    }
}
