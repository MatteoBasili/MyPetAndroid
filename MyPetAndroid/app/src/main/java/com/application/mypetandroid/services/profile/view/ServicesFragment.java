package com.application.mypetandroid.services.profile.view;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.application.mypetandroid.R;
import com.application.mypetandroid.registration.data.PetSitServices;
import com.application.mypetandroid.services.HomeActivity;
import com.application.mypetandroid.services.profile.ProfileContract;
import com.application.mypetandroid.services.profile.ServicesPresenter;
import com.application.mypetandroid.utils.singleton_examples.KeyboardSingletonClass;

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
    private ServicesPresenter presenter;

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

        ImageView backButton = servicesView.findViewById(R.id.back_arrow);
        scrollView = servicesView.findViewById(R.id.scrollView);
        service1 = servicesView.findViewById(R.id.services_checkBox1);
        service2 = servicesView.findViewById(R.id.services_checkBox2);
        service3 = servicesView.findViewById(R.id.services_checkBox3);
        service4 = servicesView.findViewById(R.id.services_checkBox4);
        service5 = servicesView.findViewById(R.id.services_checkBox5);
        description = servicesView.findViewById(R.id.pet_sitter_desc_input);
        loadProgressBar = servicesView.findViewById(R.id.load_progressBar);
        saveProgressBar = servicesView.findViewById(R.id.save_progressBar);
        save = servicesView.findViewById(R.id.save_button);

        presenter = new ServicesPresenter(this);

        save.setOnClickListener(v -> saveServices());
        backButton.setOnClickListener(v -> back());

        loadServices();

        return servicesView;
    }

    private void loadServices() {
        presenter.loadServices(user);
    }

    private void back() {
        ((HomeActivity) requireContext()).getSupportFragmentManager().popBackStackImmediate();
    }

    private void hideKeyboard() {
        KeyboardSingletonClass keyboardSingletonClass = KeyboardSingletonClass.getSingletonInstance();
        keyboardSingletonClass.hide(requireActivity());
    }

    private void saveServices() {
        hideKeyboard();

        // For showing progress indicator
        ScrollView scrollView1 = this.scrollView;
        new Handler().postDelayed(() -> scrollView1.fullScroll(View.FOCUS_DOWN), 100);

        this.save.setEnabled(false);

        boolean serv1 = service1.isChecked();
        boolean serv2 = service2.isChecked();
        boolean serv3 = service3.isChecked();
        boolean serv4 = service4.isChecked();
        boolean serv5 = service5.isChecked();
        String petSitterDesc = description.getText().toString().trim();

        PetSitServices petSitServices = new PetSitServices();
        petSitServices.setServ1(serv1);
        petSitServices.setServ2(serv2);
        petSitServices.setServ3(serv3);
        petSitServices.setServ4(serv4);
        petSitServices.setServ5(serv5);
        petSitServices.setDescription(petSitterDesc);

        presenter.saveServices(user, petSitServices);
    }

    @Override
    public void showSaveProgressIndicator() {
        this.saveProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSaveProgressIndicator() {
        this.saveProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void hideLoadProgressIndicator() {
        this.loadProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStoreServicesSuccess() {
        Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
        this.save.setEnabled(true);
    }

    @Override
    public void onLoadServicesSuccess(PetSitServices petSitServices) {
        // Load resources
        this.service1.setChecked(petSitServices.isServ1());
        this.service2.setChecked(petSitServices.isServ2());
        this.service3.setChecked(petSitServices.isServ3());
        this.service4.setChecked(petSitServices.isServ4());
        this.service5.setChecked(petSitServices.isServ5());
        this.description.setText(petSitServices.getDescription());

        // Unlock resources
        this.service1.setClickable(true);
        this.service2.setClickable(true);
        this.service3.setClickable(true);
        this.service4.setClickable(true);
        this.service5.setClickable(true);
        this.description.setEnabled(true);
        this.save.setEnabled(true);
    }

    @Override
    public void onStoreServicesFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        this.save.setEnabled(true);
    }

    @Override
    public void onLoadServicesFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
