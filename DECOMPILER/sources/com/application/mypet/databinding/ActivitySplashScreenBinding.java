package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;

public final class ActivitySplashScreenBinding implements ViewBinding {
    public final ImageView imageView3;
    public final ImageView imageView7;
    private final ConstraintLayout rootView;
    public final ConstraintLayout splashScreen;
    public final TextView splashScreenTitle;
    public final TextView textView2;
    public final TextView textView4;

    private ActivitySplashScreenBinding(ConstraintLayout rootView2, ImageView imageView32, ImageView imageView72, ConstraintLayout splashScreen2, TextView splashScreenTitle2, TextView textView22, TextView textView42) {
        this.rootView = rootView2;
        this.imageView3 = imageView32;
        this.imageView7 = imageView72;
        this.splashScreen = splashScreen2;
        this.splashScreenTitle = splashScreenTitle2;
        this.textView2 = textView22;
        this.textView4 = textView42;
    }

    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static ActivitySplashScreenBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static ActivitySplashScreenBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.activity_splash_screen, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ActivitySplashScreenBinding bind(View rootView2) {
        View view = rootView2;
        int id = R.id.imageView3;
        ImageView imageView32 = (ImageView) ViewBindings.findChildViewById(view, R.id.imageView3);
        if (imageView32 != null) {
            id = R.id.imageView7;
            ImageView imageView72 = (ImageView) ViewBindings.findChildViewById(view, R.id.imageView7);
            if (imageView72 != null) {
                ConstraintLayout splashScreen2 = (ConstraintLayout) view;
                id = R.id.splashScreenTitle;
                TextView splashScreenTitle2 = (TextView) ViewBindings.findChildViewById(view, R.id.splashScreenTitle);
                if (splashScreenTitle2 != null) {
                    id = R.id.textView2;
                    TextView textView22 = (TextView) ViewBindings.findChildViewById(view, R.id.textView2);
                    if (textView22 != null) {
                        id = R.id.textView4;
                        TextView textView42 = (TextView) ViewBindings.findChildViewById(view, R.id.textView4);
                        if (textView42 != null) {
                            return new ActivitySplashScreenBinding((ConstraintLayout) view, imageView32, imageView72, splashScreen2, splashScreenTitle2, textView22, textView42);
                        }
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(id)));
    }
}
