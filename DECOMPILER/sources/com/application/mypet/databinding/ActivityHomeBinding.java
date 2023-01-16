package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public final class ActivityHomeBinding implements ViewBinding {
    public final BottomNavigationView bottomNavigationView;
    public final FrameLayout frameLayout;
    private final ConstraintLayout rootView;

    private ActivityHomeBinding(ConstraintLayout rootView2, BottomNavigationView bottomNavigationView2, FrameLayout frameLayout2) {
        this.rootView = rootView2;
        this.bottomNavigationView = bottomNavigationView2;
        this.frameLayout = frameLayout2;
    }

    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static ActivityHomeBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static ActivityHomeBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.activity_home, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ActivityHomeBinding bind(View rootView2) {
        int id = R.id.bottomNavigationView;
        BottomNavigationView bottomNavigationView2 = (BottomNavigationView) ViewBindings.findChildViewById(rootView2, R.id.bottomNavigationView);
        if (bottomNavigationView2 != null) {
            id = R.id.frame_layout;
            FrameLayout frameLayout2 = (FrameLayout) ViewBindings.findChildViewById(rootView2, R.id.frame_layout);
            if (frameLayout2 != null) {
                return new ActivityHomeBinding((ConstraintLayout) rootView2, bottomNavigationView2, frameLayout2);
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(id)));
    }
}
