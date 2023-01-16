package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;

public final class ToolbarWithLogoutBinding implements ViewBinding {
    public final ImageView logout;
    private final RelativeLayout rootView;
    public final TextView toolbarTitle;

    private ToolbarWithLogoutBinding(RelativeLayout rootView2, ImageView logout2, TextView toolbarTitle2) {
        this.rootView = rootView2;
        this.logout = logout2;
        this.toolbarTitle = toolbarTitle2;
    }

    public RelativeLayout getRoot() {
        return this.rootView;
    }

    public static ToolbarWithLogoutBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static ToolbarWithLogoutBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.toolbar_with_logout, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ToolbarWithLogoutBinding bind(View rootView2) {
        int id = R.id.logout;
        ImageView logout2 = (ImageView) ViewBindings.findChildViewById(rootView2, R.id.logout);
        if (logout2 != null) {
            id = R.id.toolbar_title;
            TextView toolbarTitle2 = (TextView) ViewBindings.findChildViewById(rootView2, R.id.toolbar_title);
            if (toolbarTitle2 != null) {
                return new ToolbarWithLogoutBinding((RelativeLayout) rootView2, logout2, toolbarTitle2);
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(id)));
    }
}
