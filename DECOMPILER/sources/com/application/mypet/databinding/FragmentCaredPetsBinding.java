package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;

public final class FragmentCaredPetsBinding implements ViewBinding {
    public final CheckBox caredPetsCheckBox1;
    public final CheckBox caredPetsCheckBox2;
    public final CheckBox caredPetsCheckBox3;
    public final ConstraintLayout intConstLayout;
    public final ProgressBar loadProgressBar;
    private final ConstraintLayout rootView;
    public final Button saveButton;
    public final ProgressBar saveProgressBar;
    public final ScrollView scrollView;
    public final TextView title;
    public final ToolbarWithBackBinding toolbar;

    private FragmentCaredPetsBinding(ConstraintLayout rootView2, CheckBox caredPetsCheckBox12, CheckBox caredPetsCheckBox22, CheckBox caredPetsCheckBox32, ConstraintLayout intConstLayout2, ProgressBar loadProgressBar2, Button saveButton2, ProgressBar saveProgressBar2, ScrollView scrollView2, TextView title2, ToolbarWithBackBinding toolbar2) {
        this.rootView = rootView2;
        this.caredPetsCheckBox1 = caredPetsCheckBox12;
        this.caredPetsCheckBox2 = caredPetsCheckBox22;
        this.caredPetsCheckBox3 = caredPetsCheckBox32;
        this.intConstLayout = intConstLayout2;
        this.loadProgressBar = loadProgressBar2;
        this.saveButton = saveButton2;
        this.saveProgressBar = saveProgressBar2;
        this.scrollView = scrollView2;
        this.title = title2;
        this.toolbar = toolbar2;
    }

    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static FragmentCaredPetsBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static FragmentCaredPetsBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.fragment_cared_pets, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static FragmentCaredPetsBinding bind(View rootView2) {
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
                    id = R.id.int_const_layout;
                    ConstraintLayout intConstLayout2 = (ConstraintLayout) ViewBindings.findChildViewById(view, R.id.int_const_layout);
                    if (intConstLayout2 != null) {
                        id = R.id.load_progressBar;
                        ProgressBar loadProgressBar2 = (ProgressBar) ViewBindings.findChildViewById(view, R.id.load_progressBar);
                        if (loadProgressBar2 != null) {
                            id = R.id.save_button;
                            Button saveButton2 = (Button) ViewBindings.findChildViewById(view, R.id.save_button);
                            if (saveButton2 != null) {
                                id = R.id.save_progressBar;
                                ProgressBar saveProgressBar2 = (ProgressBar) ViewBindings.findChildViewById(view, R.id.save_progressBar);
                                if (saveProgressBar2 != null) {
                                    id = R.id.scrollView;
                                    ScrollView scrollView2 = (ScrollView) ViewBindings.findChildViewById(view, R.id.scrollView);
                                    if (scrollView2 != null) {
                                        id = R.id.title;
                                        TextView title2 = (TextView) ViewBindings.findChildViewById(view, R.id.title);
                                        if (title2 != null) {
                                            id = R.id.toolbar;
                                            View toolbar2 = ViewBindings.findChildViewById(view, R.id.toolbar);
                                            if (toolbar2 != null) {
                                                return new FragmentCaredPetsBinding((ConstraintLayout) view, caredPetsCheckBox12, caredPetsCheckBox22, caredPetsCheckBox32, intConstLayout2, loadProgressBar2, saveButton2, saveProgressBar2, scrollView2, title2, ToolbarWithBackBinding.bind(toolbar2));
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
