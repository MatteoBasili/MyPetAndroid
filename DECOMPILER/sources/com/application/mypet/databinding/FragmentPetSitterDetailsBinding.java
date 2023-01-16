package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;

public final class FragmentPetSitterDetailsBinding implements ViewBinding {
    public final CheckBox caredPetsCheckBox1;
    public final CheckBox caredPetsCheckBox2;
    public final CheckBox caredPetsCheckBox3;
    public final TextView caredPetsTitle;
    public final TextView contactDetailsTitle;
    public final TextView description;
    public final TextView descriptionTitle;
    public final ImageView dislikes;
    public final TextView dislikesNumb;
    public final TextView email;
    public final TextView emailTitle;
    public final ImageView favoritesClicked;
    public final ImageView favoritesView;
    public final ToolbarWithBackBinding include;
    public final ConstraintLayout intConstLayout;
    public final ImageView likes;
    public final TextView likesNumb;
    public final View line1;
    public final View line2;
    public final View line3;
    public final View line4;
    public final ProgressBar loadProgressBar;
    public final TextView name;
    public final TextView nameTitle;
    public final TextView phone;
    public final TextView phoneTitle;
    public final ImageView photoProfile;
    public final RelativeLayout photoProfileView;
    public final TextView place;
    public final TextView placeTitle;
    private final ConstraintLayout rootView;
    public final CheckBox servicesCheckBox1;
    public final CheckBox servicesCheckBox2;
    public final CheckBox servicesCheckBox3;
    public final CheckBox servicesCheckBox4;
    public final CheckBox servicesCheckBox5;
    public final TextView servicesTitle;
    public final TextView title;

    private FragmentPetSitterDetailsBinding(ConstraintLayout rootView2, CheckBox caredPetsCheckBox12, CheckBox caredPetsCheckBox22, CheckBox caredPetsCheckBox32, TextView caredPetsTitle2, TextView contactDetailsTitle2, TextView description2, TextView descriptionTitle2, ImageView dislikes2, TextView dislikesNumb2, TextView email2, TextView emailTitle2, ImageView favoritesClicked2, ImageView favoritesView2, ToolbarWithBackBinding include2, ConstraintLayout intConstLayout2, ImageView likes2, TextView likesNumb2, View line12, View line22, View line32, View line42, ProgressBar loadProgressBar2, TextView name2, TextView nameTitle2, TextView phone2, TextView phoneTitle2, ImageView photoProfile2, RelativeLayout photoProfileView2, TextView place2, TextView placeTitle2, CheckBox servicesCheckBox12, CheckBox servicesCheckBox22, CheckBox servicesCheckBox32, CheckBox servicesCheckBox42, CheckBox servicesCheckBox52, TextView servicesTitle2, TextView title2) {
        this.rootView = rootView2;
        this.caredPetsCheckBox1 = caredPetsCheckBox12;
        this.caredPetsCheckBox2 = caredPetsCheckBox22;
        this.caredPetsCheckBox3 = caredPetsCheckBox32;
        this.caredPetsTitle = caredPetsTitle2;
        this.contactDetailsTitle = contactDetailsTitle2;
        this.description = description2;
        this.descriptionTitle = descriptionTitle2;
        this.dislikes = dislikes2;
        this.dislikesNumb = dislikesNumb2;
        this.email = email2;
        this.emailTitle = emailTitle2;
        this.favoritesClicked = favoritesClicked2;
        this.favoritesView = favoritesView2;
        this.include = include2;
        this.intConstLayout = intConstLayout2;
        this.likes = likes2;
        this.likesNumb = likesNumb2;
        this.line1 = line12;
        this.line2 = line22;
        this.line3 = line32;
        this.line4 = line42;
        this.loadProgressBar = loadProgressBar2;
        this.name = name2;
        this.nameTitle = nameTitle2;
        this.phone = phone2;
        this.phoneTitle = phoneTitle2;
        this.photoProfile = photoProfile2;
        this.photoProfileView = photoProfileView2;
        this.place = place2;
        this.placeTitle = placeTitle2;
        this.servicesCheckBox1 = servicesCheckBox12;
        this.servicesCheckBox2 = servicesCheckBox22;
        this.servicesCheckBox3 = servicesCheckBox32;
        this.servicesCheckBox4 = servicesCheckBox42;
        this.servicesCheckBox5 = servicesCheckBox52;
        this.servicesTitle = servicesTitle2;
        this.title = title2;
    }

    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static FragmentPetSitterDetailsBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static FragmentPetSitterDetailsBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.fragment_pet_sitter_details, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static FragmentPetSitterDetailsBinding bind(View rootView2) {
        View view = rootView2;
        int id = R.id.cared_pets_checkBox1;
        CheckBox caredPetsCheckBox12 = (CheckBox) ViewBindings.findChildViewById(view, R.id.cared_pets_checkBox1);
        if (caredPetsCheckBox12 != null) {
            id = R.id.cared_pets_checkBox2;
            CheckBox caredPetsCheckBox22 = (CheckBox) ViewBindings.findChildViewById(view, R.id.cared_pets_checkBox2);
            if (caredPetsCheckBox22 != null) {
                id = R.id.cared_pets_checkBox3;
                CheckBox caredPetsCheckBox32 = (CheckBox) ViewBindings.findChildViewById(view, R.id.cared_pets_checkBox3);
                if (caredPetsCheckBox32 != null) {
                    id = R.id.cared_pets_title;
                    TextView caredPetsTitle2 = (TextView) ViewBindings.findChildViewById(view, R.id.cared_pets_title);
                    if (caredPetsTitle2 != null) {
                        id = R.id.contact_details_title;
                        TextView contactDetailsTitle2 = (TextView) ViewBindings.findChildViewById(view, R.id.contact_details_title);
                        if (contactDetailsTitle2 != null) {
                            id = R.id.description;
                            TextView description2 = (TextView) ViewBindings.findChildViewById(view, R.id.description);
                            if (description2 != null) {
                                id = R.id.description_title;
                                TextView descriptionTitle2 = (TextView) ViewBindings.findChildViewById(view, R.id.description_title);
                                if (descriptionTitle2 != null) {
                                    id = R.id.dislikes;
                                    ImageView dislikes2 = (ImageView) ViewBindings.findChildViewById(view, R.id.dislikes);
                                    if (dislikes2 != null) {
                                        id = R.id.dislikes_numb;
                                        TextView dislikesNumb2 = (TextView) ViewBindings.findChildViewById(view, R.id.dislikes_numb);
                                        if (dislikesNumb2 != null) {
                                            id = R.id.email;
                                            TextView email2 = (TextView) ViewBindings.findChildViewById(view, R.id.email);
                                            if (email2 != null) {
                                                id = R.id.email_title;
                                                TextView emailTitle2 = (TextView) ViewBindings.findChildViewById(view, R.id.email_title);
                                                if (emailTitle2 != null) {
                                                    id = R.id.favorites_clicked;
                                                    ImageView favoritesClicked2 = (ImageView) ViewBindings.findChildViewById(view, R.id.favorites_clicked);
                                                    if (favoritesClicked2 != null) {
                                                        id = R.id.favorites_view;
                                                        ImageView favoritesView2 = (ImageView) ViewBindings.findChildViewById(view, R.id.favorites_view);
                                                        if (favoritesView2 != null) {
                                                            id = R.id.include;
                                                            View include2 = ViewBindings.findChildViewById(view, R.id.include);
                                                            if (include2 != null) {
                                                                ToolbarWithBackBinding binding_include = ToolbarWithBackBinding.bind(include2);
                                                                id = R.id.int_const_layout;
                                                                ConstraintLayout intConstLayout2 = (ConstraintLayout) ViewBindings.findChildViewById(view, R.id.int_const_layout);
                                                                if (intConstLayout2 != null) {
                                                                    id = R.id.likes;
                                                                    ImageView likes2 = (ImageView) ViewBindings.findChildViewById(view, R.id.likes);
                                                                    if (likes2 != null) {
                                                                        id = R.id.likes_numb;
                                                                        TextView likesNumb2 = (TextView) ViewBindings.findChildViewById(view, R.id.likes_numb);
                                                                        if (likesNumb2 != null) {
                                                                            id = R.id.line1;
                                                                            View line12 = ViewBindings.findChildViewById(view, R.id.line1);
                                                                            if (line12 != null) {
                                                                                id = R.id.line2;
                                                                                View line22 = ViewBindings.findChildViewById(view, R.id.line2);
                                                                                if (line22 != null) {
                                                                                    id = R.id.line3;
                                                                                    View line32 = ViewBindings.findChildViewById(view, R.id.line3);
                                                                                    if (line32 != null) {
                                                                                        id = R.id.line4;
                                                                                        View line42 = ViewBindings.findChildViewById(view, R.id.line4);
                                                                                        if (line42 != null) {
                                                                                            id = R.id.load_progressBar;
                                                                                            ProgressBar loadProgressBar2 = (ProgressBar) ViewBindings.findChildViewById(view, R.id.load_progressBar);
                                                                                            if (loadProgressBar2 != null) {
                                                                                                id = R.id.name;
                                                                                                TextView name2 = (TextView) ViewBindings.findChildViewById(view, R.id.name);
                                                                                                if (name2 != null) {
                                                                                                    id = R.id.name_title;
                                                                                                    TextView nameTitle2 = (TextView) ViewBindings.findChildViewById(view, R.id.name_title);
                                                                                                    if (nameTitle2 != null) {
                                                                                                        id = R.id.phone;
                                                                                                        TextView phone2 = (TextView) ViewBindings.findChildViewById(view, R.id.phone);
                                                                                                        if (phone2 != null) {
                                                                                                            id = R.id.phone_title;
                                                                                                            TextView phoneTitle2 = (TextView) ViewBindings.findChildViewById(view, R.id.phone_title);
                                                                                                            if (phoneTitle2 != null) {
                                                                                                                id = R.id.photo_profile;
                                                                                                                ImageView photoProfile2 = (ImageView) ViewBindings.findChildViewById(view, R.id.photo_profile);
                                                                                                                if (photoProfile2 != null) {
                                                                                                                    id = R.id.photo_profile_view;
                                                                                                                    RelativeLayout photoProfileView2 = (RelativeLayout) ViewBindings.findChildViewById(view, R.id.photo_profile_view);
                                                                                                                    if (photoProfileView2 != null) {
                                                                                                                        id = R.id.place;
                                                                                                                        TextView place2 = (TextView) ViewBindings.findChildViewById(view, R.id.place);
                                                                                                                        if (place2 != null) {
                                                                                                                            id = R.id.place_title;
                                                                                                                            TextView placeTitle2 = (TextView) ViewBindings.findChildViewById(view, R.id.place_title);
                                                                                                                            if (placeTitle2 != null) {
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
                                                                                                                                                    id = R.id.services_title;
                                                                                                                                                    TextView servicesTitle2 = (TextView) ViewBindings.findChildViewById(view, R.id.services_title);
                                                                                                                                                    if (servicesTitle2 != null) {
                                                                                                                                                        id = R.id.title;
                                                                                                                                                        TextView title2 = (TextView) ViewBindings.findChildViewById(view, R.id.title);
                                                                                                                                                        if (title2 != null) {
                                                                                                                                                            return new FragmentPetSitterDetailsBinding((ConstraintLayout) view, caredPetsCheckBox12, caredPetsCheckBox22, caredPetsCheckBox32, caredPetsTitle2, contactDetailsTitle2, description2, descriptionTitle2, dislikes2, dislikesNumb2, email2, emailTitle2, favoritesClicked2, favoritesView2, binding_include, intConstLayout2, likes2, likesNumb2, line12, line22, line32, line42, loadProgressBar2, name2, nameTitle2, phone2, phoneTitle2, photoProfile2, photoProfileView2, place2, placeTitle2, servicesCheckBox12, servicesCheckBox22, servicesCheckBox32, servicesCheckBox42, servicesCheckBox52, servicesTitle2, title2);
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
