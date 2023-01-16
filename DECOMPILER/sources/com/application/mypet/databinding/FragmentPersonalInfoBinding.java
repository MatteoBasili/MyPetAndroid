package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;

public final class FragmentPersonalInfoBinding implements ViewBinding {
    public final TextView address;
    public final EditText addressInput;
    public final TextView email;
    public final EditText emailInput;
    public final TextView firstPetName;
    public final EditText firstPetNameInput;
    public final ConstraintLayout intConstLayout;
    public final ProgressBar loadProgressBar;
    public final TextView name;
    public final EditText nameInput;
    public final TextView phoneNumb;
    public final EditText phoneNumbInput;
    public final TextView province;
    public final TextView region;
    private final ConstraintLayout rootView;
    public final Button saveButton;
    public final ProgressBar saveProgressBar;
    public final ScrollView scrollView;
    public final Spinner spinnerProvinces;
    public final View spinnerProvincesView;
    public final Spinner spinnerRegions;
    public final TextView surname;
    public final EditText surnameInput;
    public final TextView title;
    public final ToolbarWithBackBinding toolbar;

    private FragmentPersonalInfoBinding(ConstraintLayout rootView2, TextView address2, EditText addressInput2, TextView email2, EditText emailInput2, TextView firstPetName2, EditText firstPetNameInput2, ConstraintLayout intConstLayout2, ProgressBar loadProgressBar2, TextView name2, EditText nameInput2, TextView phoneNumb2, EditText phoneNumbInput2, TextView province2, TextView region2, Button saveButton2, ProgressBar saveProgressBar2, ScrollView scrollView2, Spinner spinnerProvinces2, View spinnerProvincesView2, Spinner spinnerRegions2, TextView surname2, EditText surnameInput2, TextView title2, ToolbarWithBackBinding toolbar2) {
        this.rootView = rootView2;
        this.address = address2;
        this.addressInput = addressInput2;
        this.email = email2;
        this.emailInput = emailInput2;
        this.firstPetName = firstPetName2;
        this.firstPetNameInput = firstPetNameInput2;
        this.intConstLayout = intConstLayout2;
        this.loadProgressBar = loadProgressBar2;
        this.name = name2;
        this.nameInput = nameInput2;
        this.phoneNumb = phoneNumb2;
        this.phoneNumbInput = phoneNumbInput2;
        this.province = province2;
        this.region = region2;
        this.saveButton = saveButton2;
        this.saveProgressBar = saveProgressBar2;
        this.scrollView = scrollView2;
        this.spinnerProvinces = spinnerProvinces2;
        this.spinnerProvincesView = spinnerProvincesView2;
        this.spinnerRegions = spinnerRegions2;
        this.surname = surname2;
        this.surnameInput = surnameInput2;
        this.title = title2;
        this.toolbar = toolbar2;
    }

    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static FragmentPersonalInfoBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static FragmentPersonalInfoBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.fragment_personal_info, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static FragmentPersonalInfoBinding bind(View rootView2) {
        View view = rootView2;
        int id = R.id.address;
        TextView address2 = (TextView) ViewBindings.findChildViewById(view, R.id.address);
        if (address2 != null) {
            id = R.id.address_input;
            EditText addressInput2 = (EditText) ViewBindings.findChildViewById(view, R.id.address_input);
            if (addressInput2 != null) {
                id = R.id.email;
                TextView email2 = (TextView) ViewBindings.findChildViewById(view, R.id.email);
                if (email2 != null) {
                    id = R.id.email_input;
                    EditText emailInput2 = (EditText) ViewBindings.findChildViewById(view, R.id.email_input);
                    if (emailInput2 != null) {
                        id = R.id.first_pet_name;
                        TextView firstPetName2 = (TextView) ViewBindings.findChildViewById(view, R.id.first_pet_name);
                        if (firstPetName2 != null) {
                            id = R.id.first_pet_name_input;
                            EditText firstPetNameInput2 = (EditText) ViewBindings.findChildViewById(view, R.id.first_pet_name_input);
                            if (firstPetNameInput2 != null) {
                                id = R.id.int_const_layout;
                                ConstraintLayout intConstLayout2 = (ConstraintLayout) ViewBindings.findChildViewById(view, R.id.int_const_layout);
                                if (intConstLayout2 != null) {
                                    id = R.id.load_progressBar;
                                    ProgressBar loadProgressBar2 = (ProgressBar) ViewBindings.findChildViewById(view, R.id.load_progressBar);
                                    if (loadProgressBar2 != null) {
                                        id = R.id.name;
                                        TextView name2 = (TextView) ViewBindings.findChildViewById(view, R.id.name);
                                        if (name2 != null) {
                                            id = R.id.name_input;
                                            EditText nameInput2 = (EditText) ViewBindings.findChildViewById(view, R.id.name_input);
                                            if (nameInput2 != null) {
                                                id = R.id.phone_numb;
                                                TextView phoneNumb2 = (TextView) ViewBindings.findChildViewById(view, R.id.phone_numb);
                                                if (phoneNumb2 != null) {
                                                    id = R.id.phone_numb_input;
                                                    EditText phoneNumbInput2 = (EditText) ViewBindings.findChildViewById(view, R.id.phone_numb_input);
                                                    if (phoneNumbInput2 != null) {
                                                        id = R.id.province;
                                                        TextView province2 = (TextView) ViewBindings.findChildViewById(view, R.id.province);
                                                        if (province2 != null) {
                                                            id = R.id.region;
                                                            TextView region2 = (TextView) ViewBindings.findChildViewById(view, R.id.region);
                                                            if (region2 != null) {
                                                                id = R.id.save_button;
                                                                Button saveButton2 = (Button) ViewBindings.findChildViewById(view, R.id.save_button);
                                                                if (saveButton2 != null) {
                                                                    id = R.id.save_progressBar;
                                                                    ProgressBar saveProgressBar2 = (ProgressBar) ViewBindings.findChildViewById(view, R.id.save_progressBar);
                                                                    if (saveProgressBar2 != null) {
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
                                                                                        id = R.id.surname;
                                                                                        TextView surname2 = (TextView) ViewBindings.findChildViewById(view, R.id.surname);
                                                                                        if (surname2 != null) {
                                                                                            id = R.id.surname_input;
                                                                                            EditText surnameInput2 = (EditText) ViewBindings.findChildViewById(view, R.id.surname_input);
                                                                                            if (surnameInput2 != null) {
                                                                                                id = R.id.title;
                                                                                                TextView title2 = (TextView) ViewBindings.findChildViewById(view, R.id.title);
                                                                                                if (title2 != null) {
                                                                                                    id = R.id.toolbar;
                                                                                                    View toolbar2 = ViewBindings.findChildViewById(view, R.id.toolbar);
                                                                                                    if (toolbar2 != null) {
                                                                                                        return new FragmentPersonalInfoBinding((ConstraintLayout) view, address2, addressInput2, email2, emailInput2, firstPetName2, firstPetNameInput2, intConstLayout2, loadProgressBar2, name2, nameInput2, phoneNumb2, phoneNumbInput2, province2, region2, saveButton2, saveProgressBar2, scrollView2, spinnerProvinces2, spinnerProvincesView2, spinnerRegions2, surname2, surnameInput2, title2, ToolbarWithBackBinding.bind(toolbar2));
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
