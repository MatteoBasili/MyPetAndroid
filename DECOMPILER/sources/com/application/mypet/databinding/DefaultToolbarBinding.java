package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;

public final class DefaultToolbarBinding implements ViewBinding {
    private final RelativeLayout rootView;
    public final TextView toolbarTitle;

    private DefaultToolbarBinding(RelativeLayout rootView2, TextView toolbarTitle2) {
        this.rootView = rootView2;
        this.toolbarTitle = toolbarTitle2;
    }

    public RelativeLayout getRoot() {
        return this.rootView;
    }

    public static DefaultToolbarBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static DefaultToolbarBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.default_toolbar, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static DefaultToolbarBinding bind(View rootView2) {
        TextView toolbarTitle2 = (TextView) ViewBindings.findChildViewById(rootView2, R.id.toolbar_title);
        if (toolbarTitle2 != null) {
            return new DefaultToolbarBinding((RelativeLayout) rootView2, toolbarTitle2);
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(R.id.toolbar_title)));
    }
}
