package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;

public final class FragmentNormUserProfileBinding implements ViewBinding {
    public final TextView ads;
    public final TextView adsArrow;
    public final View adsView;
    public final TextView favorites;
    public final TextView favoritesArrow;
    public final View favoritesView;
    public final ToolbarWithLogoutBinding include;
    public final ConstraintLayout intConstLayout;
    public final ConstraintLayout normUserProfileFragment;
    public final TextView personalInfo;
    public final TextView personalInfoArrow;
    public final View personalInfoView;
    public final ImageView photoProfile;
    public final RelativeLayout photoProfileView;
    private final ConstraintLayout rootView;
    public final TextView title;
    public final TextView user;

    private FragmentNormUserProfileBinding(ConstraintLayout rootView2, TextView ads2, TextView adsArrow2, View adsView2, TextView favorites2, TextView favoritesArrow2, View favoritesView2, ToolbarWithLogoutBinding include2, ConstraintLayout intConstLayout2, ConstraintLayout normUserProfileFragment2, TextView personalInfo2, TextView personalInfoArrow2, View personalInfoView2, ImageView photoProfile2, RelativeLayout photoProfileView2, TextView title2, TextView user2) {
        this.rootView = rootView2;
        this.ads = ads2;
        this.adsArrow = adsArrow2;
        this.adsView = adsView2;
        this.favorites = favorites2;
        this.favoritesArrow = favoritesArrow2;
        this.favoritesView = favoritesView2;
        this.include = include2;
        this.intConstLayout = intConstLayout2;
        this.normUserProfileFragment = normUserProfileFragment2;
        this.personalInfo = personalInfo2;
        this.personalInfoArrow = personalInfoArrow2;
        this.personalInfoView = personalInfoView2;
        this.photoProfile = photoProfile2;
        this.photoProfileView = photoProfileView2;
        this.title = title2;
        this.user = user2;
    }

    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static FragmentNormUserProfileBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static FragmentNormUserProfileBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.fragment_norm_user_profile, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static FragmentNormUserProfileBinding bind(View rootView2) {
        View view = rootView2;
        int id = R.id.ads;
        TextView ads2 = (TextView) ViewBindings.findChildViewById(view, R.id.ads);
        if (ads2 != null) {
            id = R.id.ads_arrow;
            TextView adsArrow2 = (TextView) ViewBindings.findChildViewById(view, R.id.ads_arrow);
            if (adsArrow2 != null) {
                id = R.id.ads_view;
                View adsView2 = ViewBindings.findChildViewById(view, R.id.ads_view);
                if (adsView2 != null) {
                    id = R.id.favorites;
                    TextView favorites2 = (TextView) ViewBindings.findChildViewById(view, R.id.favorites);
                    if (favorites2 != null) {
                        id = R.id.favorites_arrow;
                        TextView favoritesArrow2 = (TextView) ViewBindings.findChildViewById(view, R.id.favorites_arrow);
                        if (favoritesArrow2 != null) {
                            id = R.id.favorites_view;
                            View favoritesView2 = ViewBindings.findChildViewById(view, R.id.favorites_view);
                            if (favoritesView2 != null) {
                                id = R.id.include;
                                View include2 = ViewBindings.findChildViewById(view, R.id.include);
                                if (include2 != null) {
                                    ToolbarWithLogoutBinding binding_include = ToolbarWithLogoutBinding.bind(include2);
                                    id = R.id.int_const_layout;
                                    ConstraintLayout intConstLayout2 = (ConstraintLayout) ViewBindings.findChildViewById(view, R.id.int_const_layout);
                                    if (intConstLayout2 != null) {
                                        ConstraintLayout normUserProfileFragment2 = (ConstraintLayout) view;
                                        id = R.id.personal_info;
                                        TextView personalInfo2 = (TextView) ViewBindings.findChildViewById(view, R.id.personal_info);
                                        if (personalInfo2 != null) {
                                            id = R.id.personal_info_arrow;
                                            TextView personalInfoArrow2 = (TextView) ViewBindings.findChildViewById(view, R.id.personal_info_arrow);
                                            if (personalInfoArrow2 != null) {
                                                id = R.id.personal_info_view;
                                                View personalInfoView2 = ViewBindings.findChildViewById(view, R.id.personal_info_view);
                                                if (personalInfoView2 != null) {
                                                    id = R.id.photo_profile;
                                                    ImageView photoProfile2 = (ImageView) ViewBindings.findChildViewById(view, R.id.photo_profile);
                                                    if (photoProfile2 != null) {
                                                        id = R.id.photo_profile_view;
                                                        RelativeLayout photoProfileView2 = (RelativeLayout) ViewBindings.findChildViewById(view, R.id.photo_profile_view);
                                                        if (photoProfileView2 != null) {
                                                            id = R.id.title;
                                                            TextView title2 = (TextView) ViewBindings.findChildViewById(view, R.id.title);
                                                            if (title2 != null) {
                                                                id = R.id.user;
                                                                TextView user2 = (TextView) ViewBindings.findChildViewById(view, R.id.user);
                                                                if (user2 != null) {
                                                                    return new FragmentNormUserProfileBinding((ConstraintLayout) view, ads2, adsArrow2, adsView2, favorites2, favoritesArrow2, favoritesView2, binding_include, intConstLayout2, normUserProfileFragment2, personalInfo2, personalInfoArrow2, personalInfoView2, photoProfile2, photoProfileView2, title2, user2);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(id)));
    }
}
