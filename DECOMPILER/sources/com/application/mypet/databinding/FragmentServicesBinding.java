package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;

public final class FragmentServicesBinding implements ViewBinding {
    public final ConstraintLayout intConstLayout;
    public final ProgressBar loadProgressBar;
    public final TextView petSitterDesc;
    public final EditText petSitterDescInput;
    private final ConstraintLayout rootView;
    public final Button saveButton;
    public final ProgressBar saveProgressBar;
    public final ScrollView scrollView;
    public final CheckBox servicesCheckBox1;
    public final CheckBox servicesCheckBox2;
    public final CheckBox servicesCheckBox3;
    public final CheckBox servicesCheckBox4;
    public final CheckBox servicesCheckBox5;
    public final TextView title;
    public final ToolbarWithBackBinding toolbar;

    private FragmentServicesBinding(ConstraintLayout rootView2, ConstraintLayout intConstLayout2, ProgressBar loadProgressBar2, TextView petSitterDesc2, EditText petSitterDescInput2, Button saveButton2, ProgressBar saveProgressBar2, ScrollView scrollView2, CheckBox servicesCheckBox12, CheckBox servicesCheckBox22, CheckBox servicesCheckBox32, CheckBox servicesCheckBox42, CheckBox servicesCheckBox52, TextView title2, ToolbarWithBackBinding toolbar2) {
        this.rootView = rootView2;
        this.intConstLayout = intConstLayout2;
        this.loadProgressBar = loadProgressBar2;
        this.petSitterDesc = petSitterDesc2;
        this.petSitterDescInput = petSitterDescInput2;
        this.saveButton = saveButton2;
        this.saveProgressBar = saveProgressBar2;
        this.scrollView = scrollView2;
        this.servicesCheckBox1 = servicesCheckBox12;
        this.servicesCheckBox2 = servicesCheckBox22;
        this.servicesCheckBox3 = servicesCheckBox32;
        this.servicesCheckBox4 = servicesCheckBox42;
        this.servicesCheckBox5 = servicesCheckBox52;
        this.title = title2;
        this.toolbar = toolbar2;
    }

    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static FragmentServicesBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static FragmentServicesBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.fragment_services, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static FragmentServicesBinding bind(View rootView2) {
        View view = rootView2;
        int id = R.id.int_const_layout;
        ConstraintLayout intConstLayout2 = (ConstraintLayout) ViewBindings.findChildViewById(view, R.id.int_const_layout);
        if (intConstLayout2 != null) {
            id = R.id.load_progressBar;
            ProgressBar loadProgressBar2 = (ProgressBar) ViewBindings.findChildViewById(view, R.id.load_progressBar);
            if (loadProgressBar2 != null) {
                id = R.id.pet_sitter_desc;
                TextView petSitterDesc2 = (TextView) ViewBindings.findChildViewById(view, R.id.pet_sitter_desc);
                if (petSitterDesc2 != null) {
                    id = R.id.pet_sitter_desc_input;
                    EditText petSitterDescInput2 = (EditText) ViewBindings.findChildViewById(view, R.id.pet_sitter_desc_input);
                    if (petSitterDescInput2 != null) {
                        id = R.id.save_button;
                        Button saveButton2 = (Button) ViewBindings.findChildViewById(view, R.id.save_button);
                        if (saveButton2 != null) {
                            id = R.id.save_progressBar;
                            ProgressBar saveProgressBar2 = (ProgressBar) ViewBindings.findChildViewById(view, R.id.save_progressBar);
                            if (saveProgressBar2 != null) {
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
                                                        id = R.id.title;
                                                        TextView title2 = (TextView) ViewBindings.findChildViewById(view, R.id.title);
                                                        if (title2 != null) {
                                                            id = R.id.toolbar;
                                                            View toolbar2 = ViewBindings.findChildViewById(view, R.id.toolbar);
                                                            if (toolbar2 != null) {
                                                                return new FragmentServicesBinding((ConstraintLayout) view, intConstLayout2, loadProgressBar2, petSitterDesc2, petSitterDescInput2, saveButton2, saveProgressBar2, scrollView2, servicesCheckBox12, servicesCheckBox22, servicesCheckBox32, servicesCheckBox42, servicesCheckBox52, title2, ToolbarWithBackBinding.bind(toolbar2));
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
