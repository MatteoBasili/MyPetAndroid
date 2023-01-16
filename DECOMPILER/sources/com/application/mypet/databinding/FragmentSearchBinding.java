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

public final class FragmentSearchBinding implements ViewBinding {
    public final ImageView findPetImage;
    public final ImageView findPetSitterImage;
    public final TextView findPetSitterText;
    public final View findPetSitterView;
    public final TextView findPetText;
    public final View findPetView;
    public final ImageView findShopImage;
    public final TextView findShopText;
    public final ImageView findVetImage;
    public final TextView findVetText;
    public final ConstraintLayout intConstLayout;
    private final ConstraintLayout rootView;
    public final ConstraintLayout searchFragment;
    public final View shopsView;
    public final TextView textView;
    public final DefaultToolbarBinding toolbar;
    public final View veterinariansView;

    private FragmentSearchBinding(ConstraintLayout rootView2, ImageView findPetImage2, ImageView findPetSitterImage2, TextView findPetSitterText2, View findPetSitterView2, TextView findPetText2, View findPetView2, ImageView findShopImage2, TextView findShopText2, ImageView findVetImage2, TextView findVetText2, ConstraintLayout intConstLayout2, ConstraintLayout searchFragment2, View shopsView2, TextView textView2, DefaultToolbarBinding toolbar2, View veterinariansView2) {
        this.rootView = rootView2;
        this.findPetImage = findPetImage2;
        this.findPetSitterImage = findPetSitterImage2;
        this.findPetSitterText = findPetSitterText2;
        this.findPetSitterView = findPetSitterView2;
        this.findPetText = findPetText2;
        this.findPetView = findPetView2;
        this.findShopImage = findShopImage2;
        this.findShopText = findShopText2;
        this.findVetImage = findVetImage2;
        this.findVetText = findVetText2;
        this.intConstLayout = intConstLayout2;
        this.searchFragment = searchFragment2;
        this.shopsView = shopsView2;
        this.textView = textView2;
        this.toolbar = toolbar2;
        this.veterinariansView = veterinariansView2;
    }

    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static FragmentSearchBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static FragmentSearchBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.fragment_search, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static FragmentSearchBinding bind(View rootView2) {
        View view = rootView2;
        int id = R.id.find_pet_image;
        ImageView findPetImage2 = (ImageView) ViewBindings.findChildViewById(view, R.id.find_pet_image);
        if (findPetImage2 != null) {
            id = R.id.find_pet_sitter_image;
            ImageView findPetSitterImage2 = (ImageView) ViewBindings.findChildViewById(view, R.id.find_pet_sitter_image);
            if (findPetSitterImage2 != null) {
                id = R.id.find_pet_sitter_text;
                TextView findPetSitterText2 = (TextView) ViewBindings.findChildViewById(view, R.id.find_pet_sitter_text);
                if (findPetSitterText2 != null) {
                    id = R.id.find_pet_sitter_view;
                    View findPetSitterView2 = ViewBindings.findChildViewById(view, R.id.find_pet_sitter_view);
                    if (findPetSitterView2 != null) {
                        id = R.id.find_pet_text;
                        TextView findPetText2 = (TextView) ViewBindings.findChildViewById(view, R.id.find_pet_text);
                        if (findPetText2 != null) {
                            id = R.id.find_pet_view;
                            View findPetView2 = ViewBindings.findChildViewById(view, R.id.find_pet_view);
                            if (findPetView2 != null) {
                                id = R.id.find_shop_image;
                                ImageView findShopImage2 = (ImageView) ViewBindings.findChildViewById(view, R.id.find_shop_image);
                                if (findShopImage2 != null) {
                                    id = R.id.find_shop_text;
                                    TextView findShopText2 = (TextView) ViewBindings.findChildViewById(view, R.id.find_shop_text);
                                    if (findShopText2 != null) {
                                        id = R.id.find_vet_image;
                                        ImageView findVetImage2 = (ImageView) ViewBindings.findChildViewById(view, R.id.find_vet_image);
                                        if (findVetImage2 != null) {
                                            id = R.id.find_vet_text;
                                            TextView findVetText2 = (TextView) ViewBindings.findChildViewById(view, R.id.find_vet_text);
                                            if (findVetText2 != null) {
                                                id = R.id.int_const_layout;
                                                ConstraintLayout intConstLayout2 = (ConstraintLayout) ViewBindings.findChildViewById(view, R.id.int_const_layout);
                                                if (intConstLayout2 != null) {
                                                    ConstraintLayout searchFragment2 = (ConstraintLayout) view;
                                                    id = R.id.shops_view;
                                                    View shopsView2 = ViewBindings.findChildViewById(view, R.id.shops_view);
                                                    if (shopsView2 != null) {
                                                        id = R.id.textView;
                                                        TextView textView2 = (TextView) ViewBindings.findChildViewById(view, R.id.textView);
                                                        if (textView2 != null) {
                                                            id = R.id.toolbar;
                                                            View toolbar2 = ViewBindings.findChildViewById(view, R.id.toolbar);
                                                            if (toolbar2 != null) {
                                                                DefaultToolbarBinding binding_toolbar = DefaultToolbarBinding.bind(toolbar2);
                                                                id = R.id.veterinarians_view;
                                                                View veterinariansView2 = ViewBindings.findChildViewById(view, R.id.veterinarians_view);
                                                                if (veterinariansView2 != null) {
                                                                    return new FragmentSearchBinding((ConstraintLayout) view, findPetImage2, findPetSitterImage2, findPetSitterText2, findPetSitterView2, findPetText2, findPetView2, findShopImage2, findShopText2, findVetImage2, findVetText2, intConstLayout2, searchFragment2, shopsView2, textView2, binding_toolbar, veterinariansView2);
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
