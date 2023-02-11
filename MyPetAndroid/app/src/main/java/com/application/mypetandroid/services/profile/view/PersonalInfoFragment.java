package com.application.mypetandroid.services.profile.view;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.application.mypetandroid.R;
import com.application.mypetandroid.registration.data.ProfileUserData;
import com.application.mypetandroid.services.HomeActivity;
import com.application.mypetandroid.services.profile.PersonalInfoPresenter;
import com.application.mypetandroid.services.profile.ProfileContract;
import com.application.mypetandroid.services.profile.data.PersonalInfo;
import com.application.mypetandroid.utils.factory_method_example.RegionsFactory;
import com.application.mypetandroid.utils.factory_method_example.regions.ProvincesBaseList;
import com.application.mypetandroid.utils.other.SpinnerAdapter;
import com.application.mypetandroid.utils.singleton_examples.KeyboardSingletonClass;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PersonalInfoFragment extends Fragment implements ProfileContract.PersonalInfoView {

    private static final Logger logger = Logger.getLogger(PersonalInfoFragment.class);
    private static final String ARG_PARAM1 = "param1";

    private EditText address;
    private EditText email;
    private EditText firstPetName;
    private ProgressBar loadProgressBar;
    private EditText name;
    private EditText phoneNumber;
    private Spinner provincesSpinner;
    private View provincesSpinnerView;
    private Spinner regionsSpinner;
    private Button save;
    private ProgressBar saveProgressBar;
    private ScrollView scrollView;
    private EditText surname;
    private SpinnerAdapter<String> regionsAdapter;
    private SpinnerAdapter<String> provincesAdapter;
    private String user;
    private RegionsFactory regionsFactory;
    private PersonalInfoPresenter presenter;
    private boolean loadSuccess;
    private String loadProvince;

    public static PersonalInfoFragment newInstance(String user2) {
        PersonalInfoFragment fragment = new PersonalInfoFragment();
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
        View personalInfoView = inflater.inflate(R.layout.fragment_personal_info, container, false);

        ImageView backButton = personalInfoView.findViewById(R.id.back_arrow);
        scrollView = personalInfoView.findViewById(R.id.scrollView);
        name = personalInfoView.findViewById(R.id.name_input);
        surname = personalInfoView.findViewById(R.id.surname_input);
        regionsSpinner = personalInfoView.findViewById(R.id.spinner_regions);
        provincesSpinner = personalInfoView.findViewById(R.id.spinner_provinces);
        address = personalInfoView.findViewById(R.id.address_input);
        email = personalInfoView.findViewById(R.id.email_input);
        phoneNumber = personalInfoView.findViewById(R.id.phone_numb_input);
        firstPetName = personalInfoView.findViewById(R.id.first_pet_name_input);
        loadProgressBar = personalInfoView.findViewById(R.id.load_progressBar);
        saveProgressBar = personalInfoView.findViewById(R.id.save_progressBar);
        save = personalInfoView.findViewById(R.id.save_button);
        provincesSpinnerView = personalInfoView.findViewById(R.id.spinner_provinces_view);

        presenter = new PersonalInfoPresenter(this);

        // Initialize spinners
        initializeSpinners();

        save.setOnClickListener(v -> saveInformations());
        backButton.setOnClickListener(v -> back());

        loadSuccess = false;
        loadInformations();

        return personalInfoView;
    }

    private void loadInformations() {
        presenter.loadInfo(user);
    }

    private void back() {
        ((HomeActivity) requireContext()).getSupportFragmentManager().popBackStackImmediate();
    }

    private void initializeSpinners() {
        regionsFactory = new RegionsFactory(getContext());
        // Initialize regions spinner
        // Convert array to a list
        List<String> regionsList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.regions)));
        // Initializing an ArrayAdapter
        regionsAdapter = new SpinnerAdapter<>(
                requireContext(),
                R.layout.custom_spinner_list,
                regionsList);

        // Set the drop down view resource
        regionsAdapter.setDropDownViewResource(R.layout.custom_spinner_list);

        // Spinner on item selected listener
        regionsSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent, View view,
                            int position, long id) {

                        // Initialize provinces spinner
                        if (position != 0) {
                            provincesSpinnerView.setVisibility(View.GONE);

                            try {
                                ProvincesBaseList provincesListBase = regionsFactory.createProvinceBaseList(position);
                                List<String> dynamicProvincesList = provincesListBase.createProvinceList();
                                provincesAdapter = new SpinnerAdapter<>(
                                        requireContext(),
                                        R.layout.custom_spinner_list,
                                        dynamicProvincesList);

                                provincesAdapter.setDropDownViewResource(R.layout.custom_spinner_list);
                                provincesSpinner.setAdapter(provincesAdapter);
                                if (loadSuccess) {
                                    provincesSpinner.setSelection(provincesAdapter.getPosition(loadProvince));
                                    loadSuccess = false;
                                }
                            } catch (Exception e) {
                                logger.error("Error: ", e);
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                        // do nothing
                    }
                });


        // Finally, data bind the spinner object with adapter
        regionsSpinner.setAdapter(regionsAdapter);
    }

    private void hideKeyboard() {
        KeyboardSingletonClass keyboardSingletonClass = KeyboardSingletonClass.getSingletonInstance();
        keyboardSingletonClass.hide(requireActivity());
    }

    private void saveInformations() {
        hideKeyboard();

        // For showing progress indicator
        ScrollView scrollView1 = this.scrollView;
        new Handler().postDelayed(() -> scrollView1.fullScroll(View.FOCUS_DOWN), 100);

        this.save.setEnabled(false);

        String nameInput = name.getText().toString().trim();
        String surnameInput = surname.getText().toString().trim();
        String regionInput = regionsSpinner.getSelectedItem().toString();
        String provinceInput = provincesSpinner.getSelectedItem().toString();
        String addressInput = address.getText().toString().trim();
        String phoneNumbInput = phoneNumber.getText().toString().trim();
        String emailInput = email.getText().toString().trim();
        String firstPetNameInput = firstPetName.getText().toString().trim();

        ProfileUserData profileUserData = new ProfileUserData();
        profileUserData.setAddress(addressInput);
        profileUserData.setName(nameInput);
        profileUserData.setPhoneNumb(phoneNumbInput);
        profileUserData.setProvince(provinceInput);
        profileUserData.setRegion(regionInput);
        profileUserData.setSurname(surnameInput);

        PersonalInfo personalInfo = new PersonalInfo();
        personalInfo.setFirstPetName(firstPetNameInput);
        personalInfo.setEmail(emailInput);
        personalInfo.setProfileUserData(profileUserData);

        presenter.saveInfo(user, personalInfo);
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
    public void onStoreInfoSuccess() {
        Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
        this.save.setEnabled(true);
    }

    @Override
    public void onLoadInfoSuccess(PersonalInfo personalInfo) {
        loadSuccess = true;
        // Load user informations
        this.name.setText(personalInfo.getProfileUserData().getName());
        this.surname.setText(personalInfo.getProfileUserData().getSurname());
        String regionText = personalInfo.getProfileUserData().getRegion();
        loadProvince = personalInfo.getProfileUserData().getProvince();
        this.regionsSpinner.setSelection(this.regionsAdapter.getPosition(regionText));
        this.address.setText(personalInfo.getProfileUserData().getAddress());
        this.email.setText(personalInfo.getEmail());
        this.phoneNumber.setText(personalInfo.getProfileUserData().getPhoneNumb());
        this.firstPetName.setText(personalInfo.getFirstPetName());

        // Unlock resources
        this.name.setEnabled(true);
        this.surname.setEnabled(true);
        this.regionsSpinner.setEnabled(true);
        this.provincesSpinner.setEnabled(true);
        this.address.setEnabled(true);
        this.email.setEnabled(true);
        this.phoneNumber.setEnabled(true);
        this.firstPetName.setEnabled(true);
        this.save.setEnabled(true);
    }

    @Override
    public void onStoreInfoFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        this.save.setEnabled(true);
    }

    @Override
    public void onLoadInfoFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
