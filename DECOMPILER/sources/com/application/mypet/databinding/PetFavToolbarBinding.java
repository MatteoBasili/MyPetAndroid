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

public final class PetFavToolbarBinding implements ViewBinding {
    public final ImageView backArrow;
    public final ImageView favoritePetSitters;
    private final RelativeLayout rootView;
    public final TextView toolbarTitle;

    private PetFavToolbarBinding(RelativeLayout rootView2, ImageView backArrow2, ImageView favoritePetSitters2, TextView toolbarTitle2) {
        this.rootView = rootView2;
        this.backArrow = backArrow2;
        this.favoritePetSitters = favoritePetSitters2;
        this.toolbarTitle = toolbarTitle2;
    }

    public RelativeLayout getRoot() {
        return this.rootView;
    }

    public static PetFavToolbarBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static PetFavToolbarBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.pet_fav_toolbar, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static PetFavToolbarBinding bind(View rootView2) {
        int id = R.id.back_arrow;
        ImageView backArrow2 = (ImageView) ViewBindings.findChildViewById(rootView2, R.id.back_arrow);
        if (backArrow2 != null) {
            id = R.id.favorite_pet_sitters;
            ImageView favoritePetSitters2 = (ImageView) ViewBindings.findChildViewById(rootView2, R.id.favorite_pet_sitters);
            if (favoritePetSitters2 != null) {
                id = R.id.toolbar_title;
                TextView toolbarTitle2 = (TextView) ViewBindings.findChildViewById(rootView2, R.id.toolbar_title);
                if (toolbarTitle2 != null) {
                    return new PetFavToolbarBinding((RelativeLayout) rootView2, backArrow2, favoritePetSitters2, toolbarTitle2);
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(id)));
    }
}
