package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;

public final class LoadingDialogBinding implements ViewBinding {
    public final ProgressBar progress;
    private final ConstraintLayout rootView;
    public final TextView textView;

    private LoadingDialogBinding(ConstraintLayout rootView2, ProgressBar progress2, TextView textView2) {
        this.rootView = rootView2;
        this.progress = progress2;
        this.textView = textView2;
    }

    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static LoadingDialogBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static LoadingDialogBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.loading_dialog, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static LoadingDialogBinding bind(View rootView2) {
        int id = R.id.progress;
        ProgressBar progress2 = (ProgressBar) ViewBindings.findChildViewById(rootView2, R.id.progress);
        if (progress2 != null) {
            id = R.id.textView;
            TextView textView2 = (TextView) ViewBindings.findChildViewById(rootView2, R.id.textView);
            if (textView2 != null) {
                return new LoadingDialogBinding((ConstraintLayout) rootView2, progress2, textView2);
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(id)));
    }
}
