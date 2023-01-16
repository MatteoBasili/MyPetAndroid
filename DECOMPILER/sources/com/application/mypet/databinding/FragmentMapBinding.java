package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;

public final class FragmentMapBinding implements ViewBinding {
    public final ImageView imageViewCS;
    public final ImageView imageViewMap;
    public final ConstraintLayout intConstLayout;
    public final ConstraintLayout mapFragment;
    private final ConstraintLayout rootView;
    public final ScrollView scrollView;
    public final TextView title;
    public final DefaultToolbarBinding toolbar;

    private FragmentMapBinding(ConstraintLayout rootView2, ImageView imageViewCS2, ImageView imageViewMap2, ConstraintLayout intConstLayout2, ConstraintLayout mapFragment2, ScrollView scrollView2, TextView title2, DefaultToolbarBinding toolbar2) {
        this.rootView = rootView2;
        this.imageViewCS = imageViewCS2;
        this.imageViewMap = imageViewMap2;
        this.intConstLayout = intConstLayout2;
        this.mapFragment = mapFragment2;
        this.scrollView = scrollView2;
        this.title = title2;
        this.toolbar = toolbar2;
    }

    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static FragmentMapBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static FragmentMapBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.fragment_map, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static FragmentMapBinding bind(View rootView2) {
        View view = rootView2;
        int id = R.id.imageViewCS;
        ImageView imageViewCS2 = (ImageView) ViewBindings.findChildViewById(view, R.id.imageViewCS);
        if (imageViewCS2 != null) {
            id = R.id.imageViewMap;
            ImageView imageViewMap2 = (ImageView) ViewBindings.findChildViewById(view, R.id.imageViewMap);
            if (imageViewMap2 != null) {
                id = R.id.int_const_layout;
                ConstraintLayout intConstLayout2 = (ConstraintLayout) ViewBindings.findChildViewById(view, R.id.int_const_layout);
                if (intConstLayout2 != null) {
                    ConstraintLayout mapFragment2 = (ConstraintLayout) view;
                    id = R.id.scrollView;
                    ScrollView scrollView2 = (ScrollView) ViewBindings.findChildViewById(view, R.id.scrollView);
                    if (scrollView2 != null) {
                        id = R.id.title;
                        TextView title2 = (TextView) ViewBindings.findChildViewById(view, R.id.title);
                        if (title2 != null) {
                            id = R.id.toolbar;
                            View toolbar2 = ViewBindings.findChildViewById(view, R.id.toolbar);
                            if (toolbar2 != null) {
                                return new FragmentMapBinding((ConstraintLayout) view, imageViewCS2, imageViewMap2, intConstLayout2, mapFragment2, scrollView2, title2, DefaultToolbarBinding.bind(toolbar2));
                            }
                        }
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(id)));
    }
}
