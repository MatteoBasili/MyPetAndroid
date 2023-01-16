package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;

public final class FragmentPetSitProfileBinding implements ViewBinding {
    public final TextView ads;
    public final TextView adsArrow;
    public final View adsView;
    public final TextView caredPets;
    public final TextView caredPetsArrow;
    public final View caredPetsView;
    public final ImageView changePhoto;
    public final ImageView deletePhoto;
    public final ImageView dislikes;
    public final TextView dislikesNumb;
    public final TextView favorites;
    public final TextView favoritesArrow;
    public final View favoritesView;
    public final ToolbarWithLogoutBinding include;
    public final ConstraintLayout intConstLayout;
    public final ImageView likes;
    public final TextView likesNumb;
    public final ProgressBar loadProgressBar;
    public final TextView personalInfo;
    public final TextView personalInfoArrow;
    public final View personalInfoView;
    public final ConstraintLayout petSitProfileFragment;
    public final ImageView photoProfile;
    public final RelativeLayout photoProfileView;
    private final ConstraintLayout rootView;
    public final ImageView savePhoto;
    public final ProgressBar saveProgressBar;
    public final TextView services;
    public final TextView servicesArrow;
    public final View servicesView;
    public final TextView title;
    public final TextView user;

    private FragmentPetSitProfileBinding(ConstraintLayout rootView2, TextView ads2, TextView adsArrow2, View adsView2, TextView caredPets2, TextView caredPetsArrow2, View caredPetsView2, ImageView changePhoto2, ImageView deletePhoto2, ImageView dislikes2, TextView dislikesNumb2, TextView favorites2, TextView favoritesArrow2, View favoritesView2, ToolbarWithLogoutBinding include2, ConstraintLayout intConstLayout2, ImageView likes2, TextView likesNumb2, ProgressBar loadProgressBar2, TextView personalInfo2, TextView personalInfoArrow2, View personalInfoView2, ConstraintLayout petSitProfileFragment2, ImageView photoProfile2, RelativeLayout photoProfileView2, ImageView savePhoto2, ProgressBar saveProgressBar2, TextView services2, TextView servicesArrow2, View servicesView2, TextView title2, TextView user2) {
        this.rootView = rootView2;
        this.ads = ads2;
        this.adsArrow = adsArrow2;
        this.adsView = adsView2;
        this.caredPets = caredPets2;
        this.caredPetsArrow = caredPetsArrow2;
        this.caredPetsView = caredPetsView2;
        this.changePhoto = changePhoto2;
        this.deletePhoto = deletePhoto2;
        this.dislikes = dislikes2;
        this.dislikesNumb = dislikesNumb2;
        this.favorites = favorites2;
        this.favoritesArrow = favoritesArrow2;
        this.favoritesView = favoritesView2;
        this.include = include2;
        this.intConstLayout = intConstLayout2;
        this.likes = likes2;
        this.likesNumb = likesNumb2;
        this.loadProgressBar = loadProgressBar2;
        this.personalInfo = personalInfo2;
        this.personalInfoArrow = personalInfoArrow2;
        this.personalInfoView = personalInfoView2;
        this.petSitProfileFragment = petSitProfileFragment2;
        this.photoProfile = photoProfile2;
        this.photoProfileView = photoProfileView2;
        this.savePhoto = savePhoto2;
        this.saveProgressBar = saveProgressBar2;
        this.services = services2;
        this.servicesArrow = servicesArrow2;
        this.servicesView = servicesView2;
        this.title = title2;
        this.user = user2;
    }

    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static FragmentPetSitProfileBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static FragmentPetSitProfileBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.fragment_pet_sit_profile, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static FragmentPetSitProfileBinding bind(View rootView2) {
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
                    id = R.id.cared_pets;
                    TextView caredPets2 = (TextView) ViewBindings.findChildViewById(view, R.id.cared_pets);
                    if (caredPets2 != null) {
                        id = R.id.cared_pets_arrow;
                        TextView caredPetsArrow2 = (TextView) ViewBindings.findChildViewById(view, R.id.cared_pets_arrow);
                        if (caredPetsArrow2 != null) {
                            id = R.id.cared_pets_view;
                            View caredPetsView2 = ViewBindings.findChildViewById(view, R.id.cared_pets_view);
                            if (caredPetsView2 != null) {
                                id = R.id.change_photo;
                                ImageView changePhoto2 = (ImageView) ViewBindings.findChildViewById(view, R.id.change_photo);
                                if (changePhoto2 != null) {
                                    id = R.id.delete_photo;
                                    ImageView deletePhoto2 = (ImageView) ViewBindings.findChildViewById(view, R.id.delete_photo);
                                    if (deletePhoto2 != null) {
                                        id = R.id.dislikes;
                                        ImageView dislikes2 = (ImageView) ViewBindings.findChildViewById(view, R.id.dislikes);
                                        if (dislikes2 != null) {
                                            id = R.id.dislikes_numb;
                                            TextView dislikesNumb2 = (TextView) ViewBindings.findChildViewById(view, R.id.dislikes_numb);
                                            if (dislikesNumb2 != null) {
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
                                                                    id = R.id.likes;
                                                                    ImageView likes2 = (ImageView) ViewBindings.findChildViewById(view, R.id.likes);
                                                                    if (likes2 != null) {
                                                                        id = R.id.likes_numb;
                                                                        TextView likesNumb2 = (TextView) ViewBindings.findChildViewById(view, R.id.likes_numb);
                                                                        if (likesNumb2 != null) {
                                                                            id = R.id.load_progressBar;
                                                                            ProgressBar loadProgressBar2 = (ProgressBar) ViewBindings.findChildViewById(view, R.id.load_progressBar);
                                                                            if (loadProgressBar2 != null) {
                                                                                id = R.id.personal_info;
                                                                                TextView personalInfo2 = (TextView) ViewBindings.findChildViewById(view, R.id.personal_info);
                                                                                if (personalInfo2 != null) {
                                                                                    id = R.id.personal_info_arrow;
                                                                                    TextView personalInfoArrow2 = (TextView) ViewBindings.findChildViewById(view, R.id.personal_info_arrow);
                                                                                    if (personalInfoArrow2 != null) {
                                                                                        id = R.id.personal_info_view;
                                                                                        View personalInfoView2 = ViewBindings.findChildViewById(view, R.id.personal_info_view);
                                                                                        if (personalInfoView2 != null) {
                                                                                            ConstraintLayout petSitProfileFragment2 = (ConstraintLayout) view;
                                                                                            id = R.id.photo_profile;
                                                                                            ImageView photoProfile2 = (ImageView) ViewBindings.findChildViewById(view, R.id.photo_profile);
                                                                                            if (photoProfile2 != null) {
                                                                                                id = R.id.photo_profile_view;
                                                                                                RelativeLayout photoProfileView2 = (RelativeLayout) ViewBindings.findChildViewById(view, R.id.photo_profile_view);
                                                                                                if (photoProfileView2 != null) {
                                                                                                    id = R.id.save_photo;
                                                                                                    ImageView savePhoto2 = (ImageView) ViewBindings.findChildViewById(view, R.id.save_photo);
                                                                                                    if (savePhoto2 != null) {
                                                                                                        id = R.id.save_progressBar;
                                                                                                        ProgressBar saveProgressBar2 = (ProgressBar) ViewBindings.findChildViewById(view, R.id.save_progressBar);
                                                                                                        if (saveProgressBar2 != null) {
                                                                                                            id = R.id.services;
                                                                                                            TextView services2 = (TextView) ViewBindings.findChildViewById(view, R.id.services);
                                                                                                            if (services2 != null) {
                                                                                                                id = R.id.services_arrow;
                                                                                                                TextView servicesArrow2 = (TextView) ViewBindings.findChildViewById(view, R.id.services_arrow);
                                                                                                                if (servicesArrow2 != null) {
                                                                                                                    id = R.id.services_view;
                                                                                                                    View servicesView2 = ViewBindings.findChildViewById(view, R.id.services_view);
                                                                                                                    if (servicesView2 != null) {
                                                                                                                        id = R.id.title;
                                                                                                                        TextView title2 = (TextView) ViewBindings.findChildViewById(view, R.id.title);
                                                                                                                        if (title2 != null) {
                                                                                                                            id = R.id.user;
                                                                                                                            TextView user2 = (TextView) ViewBindings.findChildViewById(view, R.id.user);
                                                                                                                            if (user2 != null) {
                                                                                                                                return new FragmentPetSitProfileBinding((ConstraintLayout) view, ads2, adsArrow2, adsView2, caredPets2, caredPetsArrow2, caredPetsView2, changePhoto2, deletePhoto2, dislikes2, dislikesNumb2, favorites2, favoritesArrow2, favoritesView2, binding_include, intConstLayout2, likes2, likesNumb2, loadProgressBar2, personalInfo2, personalInfoArrow2, personalInfoView2, petSitProfileFragment2, photoProfile2, photoProfileView2, savePhoto2, saveProgressBar2, services2, servicesArrow2, servicesView2, title2, user2);
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
