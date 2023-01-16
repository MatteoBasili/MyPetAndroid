package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;

public final class FragmentStartPetSitSearchBinding implements ViewBinding {
    public final TextView caredPets;
    public final CheckBox caredPetsCheckBox1;
    public final CheckBox caredPetsCheckBox2;
    public final CheckBox caredPetsCheckBox3;
    public final Button findButton;
    public final ConstraintLayout intConstLayout;
    public final ProgressBar progressBar;
    public final TextView province;
    public final TextView region;
    private final ConstraintLayout rootView;
    public final ScrollView scrollView;
    public final Spinner spinnerProvinces;
    public final View spinnerProvincesView;
    public final Spinner spinnerRegions;
    public final ConstraintLayout startPetSitSearch;
    public final TextView subtitle;
    public final TextView title;
    public final ToolbarWithBackBinding toolbar;

    private FragmentStartPetSitSearchBinding(ConstraintLayout rootView2, TextView caredPets2, CheckBox caredPetsCheckBox12, CheckBox caredPetsCheckBox22, CheckBox caredPetsCheckBox32, Button findButton2, ConstraintLayout intConstLayout2, ProgressBar progressBar2, TextView province2, TextView region2, ScrollView scrollView2, Spinner spinnerProvinces2, View spinnerProvincesView2, Spinner spinnerRegions2, ConstraintLayout startPetSitSearch2, TextView subtitle2, TextView title2, ToolbarWithBackBinding toolbar2) {
        this.rootView = rootView2;
        this.caredPets = caredPets2;
        this.caredPetsCheckBox1 = caredPetsCheckBox12;
        this.caredPetsCheckBox2 = caredPetsCheckBox22;
        this.caredPetsCheckBox3 = caredPetsCheckBox32;
        this.findButton = findButton2;
        this.intConstLayout = intConstLayout2;
        this.progressBar = progressBar2;
        this.province = province2;
        this.region = region2;
        this.scrollView = scrollView2;
        this.spinnerProvinces = spinnerProvinces2;
        this.spinnerProvincesView = spinnerProvincesView2;
        this.spinnerRegions = spinnerRegions2;
        this.startPetSitSearch = startPetSitSearch2;
        this.subtitle = subtitle2;
        this.title = title2;
        this.toolbar = toolbar2;
    }

    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static FragmentStartPetSitSearchBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static FragmentStartPetSitSearchBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.fragment_start_pet_sit_search, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static FragmentStartPetSitSearchBinding bind(View rootView2) {
        View view = rootView2;
        int id = R.id.cared_pets;
        TextView caredPets2 = (TextView) ViewBindings.findChildViewById(view, R.id.cared_pets);
        if (caredPets2 != null) {
            id = R.id.cared_pets_checkBox1;
            CheckBox caredPetsCheckBox12 = (CheckBox) ViewBindings.findChildViewById(view, R.id.cared_pets_checkBox1);
            if (caredPetsCheckBox12 != null) {
                id = R.id.cared_pets_checkBox2;
                CheckBox caredPetsCheckBox22 = (CheckBox) ViewBindings.findChildViewById(view, R.id.cared_pets_checkBox2);
                if (caredPetsCheckBox22 != null) {
                    id = R.id.cared_pets_checkBox3;
                    CheckBox caredPetsCheckBox32 = (CheckBox) ViewBindings.findChildViewById(view, R.id.cared_pets_checkBox3);
                    if (caredPetsCheckBox32 != null) {
                        id = R.id.find_button;
                        Button findButton2 = (Button) ViewBindings.findChildViewById(view, R.id.find_button);
                        if (findButton2 != null) {
                            id = R.id.int_const_layout;
                            ConstraintLayout intConstLayout2 = (ConstraintLayout) ViewBindings.findChildViewById(view, R.id.int_const_layout);
                            if (intConstLayout2 != null) {
                                id = R.id.progressBar;
                                ProgressBar progressBar2 = (ProgressBar) ViewBindings.findChildViewById(view, R.id.progressBar);
                                if (progressBar2 != null) {
                                    id = R.id.province;
                                    TextView province2 = (TextView) ViewBindings.findChildViewById(view, R.id.province);
                                    if (province2 != null) {
                                        id = R.id.region;
                                        TextView region2 = (TextView) ViewBindings.findChildViewById(view, R.id.region);
                                        if (region2 != null) {
                                            id = R.id.scrollView;
                                            ScrollView scrollView2 = (ScrollView) ViewBindings.findChildViewById(view, R.id.scrollView);
                                            if (scrollView2 != null) {
                                                id = R.id.spinner_provinces;
                                                Spinner spinnerProvinces2 = (Spinner) ViewBindings.findChildViewById(view, R.id.spinner_provinces);
                                                if (spinnerProvinces2 != null) {
                                                    id = R.id.spinner_provinces_view;
                                                    View spinnerProvincesView2 = ViewBindings.findChildViewById(view, R.id.spinner_provinces_view);
                                                    if (spinnerProvincesView2 != null) {
                                                        id = R.id.spinner_regions;
                                                        Spinner spinnerRegions2 = (Spinner) ViewBindings.findChildViewById(view, R.id.spinner_regions);
                                                        if (spinnerRegions2 != null) {
                                                            ConstraintLayout startPetSitSearch2 = (ConstraintLayout) view;
                                                            id = R.id.subtitle;
                                                            TextView subtitle2 = (TextView) ViewBindings.findChildViewById(view, R.id.subtitle);
                                                            if (subtitle2 != null) {
                                                                id = R.id.title;
                                                                TextView title2 = (TextView) ViewBindings.findChildViewById(view, R.id.title);
                                                                if (title2 != null) {
                                                                    id = R.id.toolbar;
                                                                    View toolbar2 = ViewBindings.findChildViewById(view, R.id.toolbar);
                                                                    if (toolbar2 != null) {
                                                                        return new FragmentStartPetSitSearchBinding((ConstraintLayout) view, caredPets2, caredPetsCheckBox12, caredPetsCheckBox22, caredPetsCheckBox32, findButton2, intConstLayout2, progressBar2, province2, region2, scrollView2, spinnerProvinces2, spinnerProvincesView2, spinnerRegions2, startPetSitSearch2, subtitle2, title2, ToolbarWithBackBinding.bind(toolbar2));
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
