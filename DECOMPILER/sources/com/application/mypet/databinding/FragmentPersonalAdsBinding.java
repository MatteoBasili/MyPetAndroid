package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;

public final class FragmentPersonalAdsBinding implements ViewBinding {
    public final ConstraintLayout intConstLayout;
    public final ProgressBar loadProgressBar;
    private final ConstraintLayout rootView;
    public final ScrollView scrollView;
    public final TextView textNoAds;
    public final TextView title;
    public final PersonalAdsToolbarBinding toolbar;

    private FragmentPersonalAdsBinding(ConstraintLayout rootView2, ConstraintLayout intConstLayout2, ProgressBar loadProgressBar2, ScrollView scrollView2, TextView textNoAds2, TextView title2, PersonalAdsToolbarBinding toolbar2) {
        this.rootView = rootView2;
        this.intConstLayout = intConstLayout2;
        this.loadProgressBar = loadProgressBar2;
        this.scrollView = scrollView2;
        this.textNoAds = textNoAds2;
        this.title = title2;
        this.toolbar = toolbar2;
    }

    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static FragmentPersonalAdsBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static FragmentPersonalAdsBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.fragment_personal_ads, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static FragmentPersonalAdsBinding bind(View rootView2) {
        View view = rootView2;
        int id = R.id.int_const_layout;
        ConstraintLayout intConstLayout2 = (ConstraintLayout) ViewBindings.findChildViewById(view, R.id.int_const_layout);
        if (intConstLayout2 != null) {
            id = R.id.load_progressBar;
            ProgressBar loadProgressBar2 = (ProgressBar) ViewBindings.findChildViewById(view, R.id.load_progressBar);
            if (loadProgressBar2 != null) {
                id = R.id.scrollView;
                ScrollView scrollView2 = (ScrollView) ViewBindings.findChildViewById(view, R.id.scrollView);
                if (scrollView2 != null) {
                    id = R.id.textNoAds;
                    TextView textNoAds2 = (TextView) ViewBindings.findChildViewById(view, R.id.textNoAds);
                    if (textNoAds2 != null) {
                        id = R.id.title;
                        TextView title2 = (TextView) ViewBindings.findChildViewById(view, R.id.title);
                        if (title2 != null) {
                            id = R.id.toolbar;
                            View toolbar2 = ViewBindings.findChildViewById(view, R.id.toolbar);
                            if (toolbar2 != null) {
                                return new FragmentPersonalAdsBinding((ConstraintLayout) view, intConstLayout2, loadProgressBar2, scrollView2, textNoAds2, title2, PersonalAdsToolbarBinding.bind(toolbar2));
                            }
                        }
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(id)));
    }
}
