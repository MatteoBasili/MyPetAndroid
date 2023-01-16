package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import com.application.mypet.R;

public final class CustomSpinnerListBinding implements ViewBinding {
    private final TextView rootView;

    private CustomSpinnerListBinding(TextView rootView2) {
        this.rootView = rootView2;
    }

    public TextView getRoot() {
        return this.rootView;
    }

    public static CustomSpinnerListBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static CustomSpinnerListBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.custom_spinner_list, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static CustomSpinnerListBinding bind(View rootView2) {
        if (rootView2 != null) {
            return new CustomSpinnerListBinding((TextView) rootView2);
        }
        throw new NullPointerException("rootView");
    }
}
