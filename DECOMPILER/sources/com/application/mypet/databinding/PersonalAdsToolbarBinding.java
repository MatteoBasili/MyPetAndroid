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

public final class PersonalAdsToolbarBinding implements ViewBinding {
    public final ImageView backArrow;
    public final ImageView newAd;
    private final RelativeLayout rootView;
    public final TextView toolbarTitle;

    private PersonalAdsToolbarBinding(RelativeLayout rootView2, ImageView backArrow2, ImageView newAd2, TextView toolbarTitle2) {
        this.rootView = rootView2;
        this.backArrow = backArrow2;
        this.newAd = newAd2;
        this.toolbarTitle = toolbarTitle2;
    }

    public RelativeLayout getRoot() {
        return this.rootView;
    }

    public static PersonalAdsToolbarBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static PersonalAdsToolbarBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.personal_ads_toolbar, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static PersonalAdsToolbarBinding bind(View rootView2) {
        int id = R.id.back_arrow;
        ImageView backArrow2 = (ImageView) ViewBindings.findChildViewById(rootView2, R.id.back_arrow);
        if (backArrow2 != null) {
            id = R.id.new_ad;
            ImageView newAd2 = (ImageView) ViewBindings.findChildViewById(rootView2, R.id.new_ad);
            if (newAd2 != null) {
                id = R.id.toolbar_title;
                TextView toolbarTitle2 = (TextView) ViewBindings.findChildViewById(rootView2, R.id.toolbar_title);
                if (toolbarTitle2 != null) {
                    return new PersonalAdsToolbarBinding((RelativeLayout) rootView2, backArrow2, newAd2, toolbarTitle2);
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(id)));
    }
}
