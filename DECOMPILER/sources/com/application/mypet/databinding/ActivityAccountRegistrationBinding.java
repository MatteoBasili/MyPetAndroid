package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;

public final class ActivityAccountRegistrationBinding implements ViewBinding {
    public final TextView address;
    public final EditText addressInput;
    public final TextView caredPets;
    public final CheckBox caredPetsCheckBox1;
    public final CheckBox caredPetsCheckBox2;
    public final CheckBox caredPetsCheckBox3;
    public final Button createAccountButton;
    public final TextView email;
    public final EditText emailInput;
    public final TextView firstPetName;
    public final EditText firstPetNameInput;
    public final ImageView hidePwd1;
    public final ImageView hidePwd2;
    public final ConstraintLayout intConstLayout;
    public final TextView name;
    public final EditText nameInput;
    public final TextView offeredServices;
    public final CheckBox petSitterCheckBox;
    public final TextView petSitterDesc;
    public final EditText petSitterDescInput;
    public final TextView phoneNumb;
    public final EditText phoneNumbInput;
    public final ProgressBar progressBar;
    public final TextView province;
    public final TextView pwd;
    public final TextView pwdConfirm;
    public final EditText pwdConfirmInput;
    public final EditText pwdInput;
    public final TextView region;
    private final ConstraintLayout rootView;
    public final ScrollView scrollView;
    public final CheckBox servicesCheckBox1;
    public final CheckBox servicesCheckBox2;
    public final CheckBox servicesCheckBox3;
    public final CheckBox servicesCheckBox4;
    public final CheckBox servicesCheckBox5;
    public final ImageView showPwd1;
    public final ImageView showPwd2;
    public final Spinner spinnerProvinces;
    public final View spinnerProvincesView;
    public final Spinner spinnerRegions;
    public final TextView subtitle;
    public final TextView surname;
    public final EditText surnameInput;
    public final TextView title;
    public final ToolbarWithBackBinding toolbar;
    public final TextView username;
    public final EditText usernameInput;

    private ActivityAccountRegistrationBinding(ConstraintLayout rootView2, TextView address2, EditText addressInput2, TextView caredPets2, CheckBox caredPetsCheckBox12, CheckBox caredPetsCheckBox22, CheckBox caredPetsCheckBox32, Button createAccountButton2, TextView email2, EditText emailInput2, TextView firstPetName2, EditText firstPetNameInput2, ImageView hidePwd12, ImageView hidePwd22, ConstraintLayout intConstLayout2, TextView name2, EditText nameInput2, TextView offeredServices2, CheckBox petSitterCheckBox2, TextView petSitterDesc2, EditText petSitterDescInput2, TextView phoneNumb2, EditText phoneNumbInput2, ProgressBar progressBar2, TextView province2, TextView pwd2, TextView pwdConfirm2, EditText pwdConfirmInput2, EditText pwdInput2, TextView region2, ScrollView scrollView2, CheckBox servicesCheckBox12, CheckBox servicesCheckBox22, CheckBox servicesCheckBox32, CheckBox servicesCheckBox42, CheckBox servicesCheckBox52, ImageView showPwd12, ImageView showPwd22, Spinner spinnerProvinces2, View spinnerProvincesView2, Spinner spinnerRegions2, TextView subtitle2, TextView surname2, EditText surnameInput2, TextView title2, ToolbarWithBackBinding toolbar2, TextView username2, EditText usernameInput2) {
        this.rootView = rootView2;
        this.address = address2;
        this.addressInput = addressInput2;
        this.caredPets = caredPets2;
        this.caredPetsCheckBox1 = caredPetsCheckBox12;
        this.caredPetsCheckBox2 = caredPetsCheckBox22;
        this.caredPetsCheckBox3 = caredPetsCheckBox32;
        this.createAccountButton = createAccountButton2;
        this.email = email2;
        this.emailInput = emailInput2;
        this.firstPetName = firstPetName2;
        this.firstPetNameInput = firstPetNameInput2;
        this.hidePwd1 = hidePwd12;
        this.hidePwd2 = hidePwd22;
        this.intConstLayout = intConstLayout2;
        this.name = name2;
        this.nameInput = nameInput2;
        this.offeredServices = offeredServices2;
        this.petSitterCheckBox = petSitterCheckBox2;
        this.petSitterDesc = petSitterDesc2;
        this.petSitterDescInput = petSitterDescInput2;
        this.phoneNumb = phoneNumb2;
        this.phoneNumbInput = phoneNumbInput2;
        this.progressBar = progressBar2;
        this.province = province2;
        this.pwd = pwd2;
        this.pwdConfirm = pwdConfirm2;
        this.pwdConfirmInput = pwdConfirmInput2;
        this.pwdInput = pwdInput2;
        this.region = region2;
        this.scrollView = scrollView2;
        this.servicesCheckBox1 = servicesCheckBox12;
        this.servicesCheckBox2 = servicesCheckBox22;
        this.servicesCheckBox3 = servicesCheckBox32;
        this.servicesCheckBox4 = servicesCheckBox42;
        this.servicesCheckBox5 = servicesCheckBox52;
        this.showPwd1 = showPwd12;
        this.showPwd2 = showPwd22;
        this.spinnerProvinces = spinnerProvinces2;
        this.spinnerProvincesView = spinnerProvincesView2;
        this.spinnerRegions = spinnerRegions2;
        this.subtitle = subtitle2;
        this.surname = surname2;
        this.surnameInput = surnameInput2;
        this.title = title2;
        this.toolbar = toolbar2;
        this.username = username2;
        this.usernameInput = usernameInput2;
    }

    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static ActivityAccountRegistrationBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static ActivityAccountRegistrationBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.activity_account_registration, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ActivityAccountRegistrationBinding bind(View rootView2) {
        View view = rootView2;
        int id = R.id.address;
        TextView address2 = (TextView) ViewBindings.findChildViewById(view, R.id.address);
        if (address2 != null) {
            id = R.id.address_input;
            EditText addressInput2 = (EditText) ViewBindings.findChildViewById(view, R.id.address_input);
            if (addressInput2 != null) {
                id = R.id.cared_pets;
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
                                id = R.id.create_account_button;
                                Button createAccountButton2 = (Button) ViewBindings.findChildViewById(view, R.id.create_account_button);
                                if (createAccountButton2 != null) {
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
                                                    id = R.id.hide_pwd1;
                                                    ImageView hidePwd12 = (ImageView) ViewBindings.findChildViewById(view, R.id.hide_pwd1);
                                                    if (hidePwd12 != null) {
                                                        id = R.id.hide_pwd2;
                                                        ImageView hidePwd22 = (ImageView) ViewBindings.findChildViewById(view, R.id.hide_pwd2);
                                                        if (hidePwd22 != null) {
                                                            id = R.id.int_const_layout;
                                                            ConstraintLayout intConstLayout2 = (ConstraintLayout) ViewBindings.findChildViewById(view, R.id.int_const_layout);
                                                            if (intConstLayout2 != null) {
                                                                id = R.id.name;
                                                                TextView name2 = (TextView) ViewBindings.findChildViewById(view, R.id.name);
                                                                if (name2 != null) {
                                                                    id = R.id.name_input;
                                                                    EditText nameInput2 = (EditText) ViewBindings.findChildViewById(view, R.id.name_input);
                                                                    if (nameInput2 != null) {
                                                                        id = R.id.offered_services;
                                                                        TextView offeredServices2 = (TextView) ViewBindings.findChildViewById(view, R.id.offered_services);
                                                                        if (offeredServices2 != null) {
                                                                            id = R.id.pet_sitter_checkBox;
                                                                            CheckBox petSitterCheckBox2 = (CheckBox) ViewBindings.findChildViewById(view, R.id.pet_sitter_checkBox);
                                                                            if (petSitterCheckBox2 != null) {
                                                                                id = R.id.pet_sitter_desc;
                                                                                TextView petSitterDesc2 = (TextView) ViewBindings.findChildViewById(view, R.id.pet_sitter_desc);
                                                                                if (petSitterDesc2 != null) {
                                                                                    id = R.id.pet_sitter_desc_input;
                                                                                    EditText petSitterDescInput2 = (EditText) ViewBindings.findChildViewById(view, R.id.pet_sitter_desc_input);
                                                                                    if (petSitterDescInput2 != null) {
                                                                                        id = R.id.phone_numb;
                                                                                        TextView phoneNumb2 = (TextView) ViewBindings.findChildViewById(view, R.id.phone_numb);
                                                                                        if (phoneNumb2 != null) {
                                                                                            id = R.id.phone_numb_input;
                                                                                            EditText phoneNumbInput2 = (EditText) ViewBindings.findChildViewById(view, R.id.phone_numb_input);
                                                                                            if (phoneNumbInput2 != null) {
                                                                                                id = R.id.progressBar;
                                                                                                ProgressBar progressBar2 = (ProgressBar) ViewBindings.findChildViewById(view, R.id.progressBar);
                                                                                                if (progressBar2 != null) {
                                                                                                    id = R.id.province;
                                                                                                    TextView province2 = (TextView) ViewBindings.findChildViewById(view, R.id.province);
                                                                                                    if (province2 != null) {
                                                                                                        id = R.id.pwd;
                                                                                                        TextView pwd2 = (TextView) ViewBindings.findChildViewById(view, R.id.pwd);
                                                                                                        if (pwd2 != null) {
                                                                                                            id = R.id.pwd_confirm;
                                                                                                            TextView pwdConfirm2 = (TextView) ViewBindings.findChildViewById(view, R.id.pwd_confirm);
                                                                                                            if (pwdConfirm2 != null) {
                                                                                                                id = R.id.pwd_confirm_input;
                                                                                                                EditText pwdConfirmInput2 = (EditText) ViewBindings.findChildViewById(view, R.id.pwd_confirm_input);
                                                                                                                if (pwdConfirmInput2 != null) {
                                                                                                                    id = R.id.pwd_input;
                                                                                                                    EditText pwdInput2 = (EditText) ViewBindings.findChildViewById(view, R.id.pwd_input);
                                                                                                                    if (pwdInput2 != null) {
                                                                                                                        id = R.id.region;
                                                                                                                        TextView region2 = (TextView) ViewBindings.findChildViewById(view, R.id.region);
                                                                                                                        if (region2 != null) {
                                                                                                                            id = R.id.scrollView;
                                                                                                                            ScrollView scrollView2 = (ScrollView) ViewBindings.findChildViewById(view, R.id.scrollView);
                                                                                                                            if (scrollView2 != null) {
                                                                                                                                id = R.id.services_checkBox1;
                                                                                                                                CheckBox servicesCheckBox12 = (CheckBox) ViewBindings.findChildViewById(view, R.id.services_checkBox1);
                                                                                                                                if (servicesCheckBox12 != null) {
                                                                                                                                    id = R.id.services_checkBox2;
                                                                                                                                    CheckBox servicesCheckBox22 = (CheckBox) ViewBindings.findChildViewById(view, R.id.services_checkBox2);
                                                                                                                                    if (servicesCheckBox22 != null) {
                                                                                                                                        id = R.id.services_checkBox3;
                                                                                                                                        CheckBox servicesCheckBox32 = (CheckBox) ViewBindings.findChildViewById(view, R.id.services_checkBox3);
                                                                                                                                        if (servicesCheckBox32 != null) {
                                                                                                                                            id = R.id.services_checkBox4;
                                                                                                                                            CheckBox servicesCheckBox42 = (CheckBox) ViewBindings.findChildViewById(view, R.id.services_checkBox4);
                                                                                                                                            if (servicesCheckBox42 != null) {
                                                                                                                                                id = R.id.services_checkBox5;
                                                                                                                                                CheckBox servicesCheckBox52 = (CheckBox) ViewBindings.findChildViewById(view, R.id.services_checkBox5);
                                                                                                                                                if (servicesCheckBox52 != null) {
                                                                                                                                                    id = R.id.show_pwd1;
                                                                                                                                                    ImageView showPwd12 = (ImageView) ViewBindings.findChildViewById(view, R.id.show_pwd1);
                                                                                                                                                    if (showPwd12 != null) {
                                                                                                                                                        id = R.id.show_pwd2;
                                                                                                                                                        ImageView showPwd22 = (ImageView) ViewBindings.findChildViewById(view, R.id.show_pwd2);
                                                                                                                                                        if (showPwd22 != null) {
                                                                                                                                                            id = R.id.spinner_provinces;
                                                                                                                                                            Spinner spinnerProvinces2 = (Spinner) ViewBindings.findChildViewById(view, R.id.spinner_provinces);
                                                                                                                                                            if (spinnerProvinces2 != null) {
                                                                                                                                                                id = R.id.spinner_provinces_view;
                                                                                                                                                                View spinnerProvincesView2 = ViewBindings.findChildViewById(view, R.id.spinner_provinces_view);
                                                                                                                                                                if (spinnerProvincesView2 != null) {
                                                                                                                                                                    id = R.id.spinner_regions;
                                                                                                                                                                    Spinner spinnerRegions2 = (Spinner) ViewBindings.findChildViewById(view, R.id.spinner_regions);
                                                                                                                                                                    if (spinnerRegions2 != null) {
                                                                                                                                                                        id = R.id.subtitle;
                                                                                                                                                                        TextView subtitle2 = (TextView) ViewBindings.findChildViewById(view, R.id.subtitle);
                                                                                                                                                                        if (subtitle2 != null) {
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
                                                                                                                                                                                            ToolbarWithBackBinding binding_toolbar = ToolbarWithBackBinding.bind(toolbar2);
                                                                                                                                                                                            id = R.id.username;
                                                                                                                                                                                            TextView username2 = (TextView) ViewBindings.findChildViewById(view, R.id.username);
                                                                                                                                                                                            if (username2 != null) {
                                                                                                                                                                                                id = R.id.username_input;
                                                                                                                                                                                                EditText usernameInput2 = (EditText) ViewBindings.findChildViewById(view, R.id.username_input);
                                                                                                                                                                                                if (usernameInput2 != null) {
                                                                                                                                                                                                    return new ActivityAccountRegistrationBinding((ConstraintLayout) view, address2, addressInput2, caredPets2, caredPetsCheckBox12, caredPetsCheckBox22, caredPetsCheckBox32, createAccountButton2, email2, emailInput2, firstPetName2, firstPetNameInput2, hidePwd12, hidePwd22, intConstLayout2, name2, nameInput2, offeredServices2, petSitterCheckBox2, petSitterDesc2, petSitterDescInput2, phoneNumb2, phoneNumbInput2, progressBar2, province2, pwd2, pwdConfirm2, pwdConfirmInput2, pwdInput2, region2, scrollView2, servicesCheckBox12, servicesCheckBox22, servicesCheckBox32, servicesCheckBox42, servicesCheckBox52, showPwd12, showPwd22, spinnerProvinces2, spinnerProvincesView2, spinnerRegions2, subtitle2, surname2, surnameInput2, title2, binding_toolbar, username2, usernameInput2);
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
