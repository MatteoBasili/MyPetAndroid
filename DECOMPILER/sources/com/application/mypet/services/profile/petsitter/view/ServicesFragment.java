package com.application.mypet.services.profile.petsitter.view;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.application.mypet.R;
import com.application.mypet.registration.data.PetSitterServicesCredentials;
import com.application.mypet.services.HomeActivity;
import com.application.mypet.services.profile.ProfileContract;
import com.application.mypet.services.profile.petsitter.ServicesPresenter;

public class ServicesFragment extends Fragment implements ProfileContract.ServicesView {
    private static final String ARG_PARAM1 = "param1";
    private EditText description;
    private ProgressBar loadProgressBar;
    private Button save;
    private ProgressBar saveProgressBar;
    private ScrollView scrollView;
    private CheckBox service1;
    private CheckBox service2;
    private CheckBox service3;
    private CheckBox service4;
    private CheckBox service5;
    private String user;

    public static ServicesFragment newInstance(String user2) {
        ServicesFragment fragment = new ServicesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, user2);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.user = getArguments().getString(ARG_PARAM1);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View servicesView = inflater.inflate(R.layout.fragment_services, container, false);
        ServicesPresenter presenter = new ServicesPresenter(this);
        this.scrollView = (ScrollView) servicesView.findViewById(R.id.scrollView);
        this.service1 = (CheckBox) servicesView.findViewById(R.id.services_checkBox1);
        this.service2 = (CheckBox) servicesView.findViewById(R.id.services_checkBox2);
        this.service3 = (CheckBox) servicesView.findViewById(R.id.services_checkBox3);
        this.service4 = (CheckBox) servicesView.findViewById(R.id.services_checkBox4);
        this.service5 = (CheckBox) servicesView.findViewById(R.id.services_checkBox5);
        this.description = (EditText) servicesView.findViewById(R.id.pet_sitter_desc_input);
        this.loadProgressBar = (ProgressBar) servicesView.findViewById(R.id.load_progressBar);
        this.saveProgressBar = (ProgressBar) servicesView.findViewById(R.id.save_progressBar);
        this.save = (Button) servicesView.findViewById(R.id.save_button);
        String username = this.user;
        new Handler().postDelayed(new ServicesFragment$$ExternalSyntheticLambda1(presenter, username), 10);
        ((ImageView) servicesView.findViewById(R.id.back_arrow)).setOnClickListener(new ServicesFragment$$ExternalSyntheticLambda2(this));
        this.save.setOnClickListener(new ServicesFragment$$ExternalSyntheticLambda3(this, presenter, username));
        return servicesView;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$1$com-application-mypet-services-profile-petsitter-view-ServicesFragment  reason: not valid java name */
    public /* synthetic */ void m43lambda$onCreateView$1$comapplicationmypetservicesprofilepetsitterviewServicesFragment(View view) {
        ((HomeActivity) requireContext()).getSupportFragmentManager().popBackStackImmediate();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$2$com-application-mypet-services-profile-petsitter-view-ServicesFragment  reason: not valid java name */
    public /* synthetic */ void m44lambda$onCreateView$2$comapplicationmypetservicesprofilepetsitterviewServicesFragment(ServicesPresenter presenter, String username, View view) {
        String petSitterDesc;
        this.save.setEnabled(false);
        try {
            ((InputMethodManager) requireActivity().getSystemService("input_method")).hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 2);
        } catch (NullPointerException e) {
            Log.e("Null Pointer Error: ", e.getMessage());
        }
        boolean serv1 = this.service1.isChecked();
        boolean serv2 = this.service2.isChecked();
        boolean serv3 = this.service3.isChecked();
        boolean serv4 = this.service4.isChecked();
        boolean serv5 = this.service5.isChecked();
        String petSitterDesc2 = this.description.getText().toString().trim();
        if (petSitterDesc2.length() == 0) {
            petSitterDesc = null;
        } else {
            petSitterDesc = petSitterDesc2;
        }
        presenter.saveServices(username, new PetSitterServicesCredentials(serv1, serv2, serv3, serv4, serv5, petSitterDesc));
    }

    public void showSaveProgressbar() {
        new Handler().postDelayed(new ServicesFragment$$ExternalSyntheticLambda0(this.scrollView), 100);
        this.saveProgressBar.setVisibility(0);
    }

    public void hideSaveProgressbar() {
        this.saveProgressBar.setVisibility(8);
    }

    public void hideLoadProgressbar() {
        this.loadProgressBar.setVisibility(8);
    }

    public void onStoreSuccess() {
        Toast.makeText(getContext(), "Saved", 0).show();
        this.save.setEnabled(true);
    }

    public void onLoadServicesSuccess(PetSitterServicesCredentials petSitterServicesCredentials) {
        this.service1.setChecked(petSitterServicesCredentials.isServ1());
        this.service2.setChecked(petSitterServicesCredentials.isServ2());
        this.service3.setChecked(petSitterServicesCredentials.isServ3());
        this.service4.setChecked(petSitterServicesCredentials.isServ4());
        this.service5.setChecked(petSitterServicesCredentials.isServ5());
        this.description.setText(petSitterServicesCredentials.getDescription());
        this.service1.setClickable(true);
        this.service2.setClickable(true);
        this.service3.setClickable(true);
        this.service4.setClickable(true);
        this.service5.setClickable(true);
        this.description.setEnabled(true);
        this.save.setEnabled(true);
    }

    public void onStoreFailed(String message) {
        Toast.makeText(getContext(), message, 0).show();
        this.save.setEnabled(true);
    }

    public void onLoadFailed(String message) {
        Toast.makeText(getContext(), message, 0).show();
    }
}
