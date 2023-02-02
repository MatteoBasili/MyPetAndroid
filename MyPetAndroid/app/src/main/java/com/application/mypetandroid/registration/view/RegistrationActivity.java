package com.application.mypetandroid.registration.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.application.mypetandroid.R;
import com.application.mypetandroid.login.view.LoginActivity;
import com.application.mypetandroid.registration.RegistrationContract;
import com.application.mypetandroid.registration.RegistrationPresenter;
import com.application.mypetandroid.registration.data.PetSitCaredPets;
import com.application.mypetandroid.registration.data.PetSitServices;
import com.application.mypetandroid.registration.data.ProfileUserData;
import com.application.mypetandroid.registration.data.RegistrationCredentials;
import com.application.mypetandroid.registration.data.SystemUserData;
import com.application.mypetandroid.utils.factory_method_example.ProvincesFactory;
import com.application.mypetandroid.utils.factory_method_example.provinces.ProvincesBaseList;
import com.application.mypetandroid.utils.other.SpinnerAdapter;
import com.application.mypetandroid.utils.singleton_examples.KeyboardSingletonClass;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity implements RegistrationContract.RegistrationView {

    private static final Logger logger = Logger.getLogger(RegistrationActivity.class);

    private ScrollView scrollView;
    private Button registerButton;
    private EditText nameView;
    private EditText surnameView;
    private Spinner regionsSpinner;
    private Spinner provincesSpinner;
    private View provincesSpinnerView;
    private EditText addressView;
    private EditText phoneNumberView;
    private EditText usernameView;
    private EditText emailView;
    private EditText passwordView;
    private EditText passwordConfirmView;
    private EditText firstPetNameView;
    private CheckBox isPetSitter;
    private CheckBox isDog;
    private CheckBox isCat;
    private CheckBox isOtherPets;
    private CheckBox isService1;
    private CheckBox isService2;
    private CheckBox isService3;
    private CheckBox isService4;
    private CheckBox isService5;
    private EditText petSitterDescriptionView;
    private TextView caredPetsText;
    private TextView servicesText;
    private TextView descriptionText;
    private ProgressBar progressBar;
    private ImageView showPwdView;
    private ImageView showPwdConfirmView;
    private ImageView hidePwdView;
    private ImageView hidePwdConfirmView;
    private RegistrationPresenter presenter;
    private ProvincesFactory provincesFactory;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_registration);

        scrollView = findViewById(R.id.scrollView);
        nameView = findViewById(R.id.name_input);
        surnameView = findViewById(R.id.surname_input);
        regionsSpinner = findViewById(R.id.spinner_regions);
        provincesSpinner = findViewById(R.id.spinner_provinces);
        provincesSpinnerView = findViewById(R.id.spinner_provinces_view);
        addressView = findViewById(R.id.address_input);
        phoneNumberView = findViewById(R.id.phone_numb_input);
        usernameView = findViewById(R.id.username_input);
        emailView = findViewById(R.id.email_input);
        passwordView = findViewById(R.id.pwd_input);
        passwordConfirmView = findViewById(R.id.pwd_confirm_input);
        firstPetNameView = findViewById(R.id.first_pet_name_input);
        isPetSitter = findViewById(R.id.pet_sitter_checkBox);
        isDog = findViewById(R.id.cared_pets_checkBox1);
        isCat = findViewById(R.id.cared_pets_checkBox2);
        isOtherPets = findViewById(R.id.cared_pets_checkBox3);
        isService1 = findViewById(R.id.services_checkBox1);
        isService2 = findViewById(R.id.services_checkBox2);
        isService3 = findViewById(R.id.services_checkBox3);
        isService4 = findViewById(R.id.services_checkBox4);
        isService5 = findViewById(R.id.services_checkBox5);
        petSitterDescriptionView = findViewById(R.id.pet_sitter_desc_input);
        caredPetsText = findViewById(R.id.cared_pets);
        servicesText = findViewById(R.id.offered_services);
        descriptionText = findViewById(R.id.pet_sitter_desc);
        showPwdView = findViewById(R.id.show_pwd1);
        showPwdConfirmView = findViewById(R.id.show_pwd2);
        hidePwdView = findViewById(R.id.hide_pwd1);
        hidePwdConfirmView = findViewById(R.id.hide_pwd2);
        registerButton = findViewById(R.id.create_account_button);
        progressBar = findViewById(R.id.progressBar);
        ImageView backButton = findViewById(R.id.back_arrow);

        // Initialize spinners
        initializeSpinners();

        presenter = new RegistrationPresenter(this);

        provincesSpinnerView.setOnClickListener(v -> Toast.makeText(this, "First select the region", Toast.LENGTH_SHORT).show());
        
        // For back button
        backButton.setOnClickListener(v -> back());

        // Show/hide password
        showPwdView.setOnClickListener(v -> showPassword());
        hidePwdView.setOnClickListener(v -> hidePassword());
        showPwdConfirmView.setOnClickListener(v -> showPasswordConfirm());
        hidePwdConfirmView.setOnClickListener(v -> hidePasswordConfirm());

        // Lock/unlock pet sitter functionalities
        isPetSitter.setOnClickListener(v -> handlePetSitterCheckBox());

        // Initialize registration button
        registerButton.setOnClickListener(v -> register());
    }

    private void initializeSpinners() {
        provincesFactory = new ProvincesFactory(this);
        // Initialize regions spinner
        // Convert array to a list
        List<String> regionsList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.regions)));
        // Initializing an ArrayAdapter
        SpinnerAdapter<String> regionsAdapter = new SpinnerAdapter<>(
                getApplicationContext(),
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
                                ProvincesBaseList provincesListBase = provincesFactory.createProvinceBaseList(position);
                                List<String> dynamicProvincesList = provincesListBase.createProvinceList();
                                SpinnerAdapter<String> provincesAdapter = new SpinnerAdapter<>(
                                        getApplicationContext(),
                                        R.layout.custom_spinner_list,
                                        dynamicProvincesList);

                                provincesAdapter.setDropDownViewResource(R.layout.custom_spinner_list);
                                provincesSpinner.setAdapter(provincesAdapter);
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

    private void back() {
        View confirmView = LayoutInflater.from(this).inflate(R.layout.confirm_dialog, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(confirmView);
        AlertDialog dialog = dialogBuilder.show();
        (confirmView.findViewById(R.id.yes_button)).setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });
        (confirmView.findViewById(R.id.no_button)).setOnClickListener(v -> dialog.dismiss());
    }

    private void showPassword() {
        passwordView.setTransformationMethod(new HideReturnsTransformationMethod());
        passwordView.setSelection(passwordView.getText().length());
        hidePwdView.setVisibility(View.VISIBLE);
        showPwdView.setVisibility(View.GONE);
    }

    private void hidePassword() {
        passwordView.setTransformationMethod(new PasswordTransformationMethod());
        passwordView.setSelection(passwordView.getText().length());
        showPwdView.setVisibility(View.VISIBLE);
        hidePwdView.setVisibility(View.GONE);
    }

    private void showPasswordConfirm() {
        passwordConfirmView.setTransformationMethod(new HideReturnsTransformationMethod());
        passwordConfirmView.setSelection(passwordConfirmView.getText().length());
        hidePwdConfirmView.setVisibility(View.VISIBLE);
        showPwdConfirmView.setVisibility(View.GONE);
    }

    private void hidePasswordConfirm() {
        passwordConfirmView.setTransformationMethod(new PasswordTransformationMethod());
        passwordConfirmView.setSelection(passwordConfirmView.getText().length());
        showPwdConfirmView.setVisibility(View.VISIBLE);
        hidePwdConfirmView.setVisibility(View.GONE);
    }

    private void handlePetSitterCheckBox() {
        int color;
        int hintColor;
        boolean isClickable;
        Drawable descDrawable;
        if (isPetSitter.isChecked()) {
            color = ContextCompat.getColor(this, R.color.black);
            isClickable = true;
            descDrawable = ContextCompat.getDrawable(this, R.drawable.rounded_rectangle_input);
            hintColor = ContextCompat.getColor(this, R.color.disable_color);
        } else {
            color = ContextCompat.getColor(this, R.color.disable_color);
            isClickable = false;
            descDrawable = ContextCompat.getDrawable(this, R.drawable.rounded_rectangle_input_locked);
            hintColor = ContextCompat.getColor(this, R.color.disable_hint_color);
        }
        // Lock/unlock cared pets
        caredPetsText.setTextColor(color);
        isDog.setButtonTintList(ColorStateList.valueOf(color));
        isDog.setTextColor(color);
        isDog.setClickable(isClickable);
        isCat.setButtonTintList(ColorStateList.valueOf(color));
        isCat.setTextColor(color);
        isCat.setClickable(isClickable);
        isOtherPets.setButtonTintList(ColorStateList.valueOf(color));
        isOtherPets.setTextColor(color);
        isOtherPets.setClickable(isClickable);
        servicesText.setTextColor(color);
        // Lock/unlock services
        isService1.setButtonTintList(ColorStateList.valueOf(color));
        isService1.setTextColor(color);
        isService1.setClickable(isClickable);
        isService2.setButtonTintList(ColorStateList.valueOf(color));
        isService2.setTextColor(color);
        isService2.setClickable(isClickable);
        isService3.setButtonTintList(ColorStateList.valueOf(color));
        isService3.setTextColor(color);
        isService3.setClickable(isClickable);
        isService4.setButtonTintList(ColorStateList.valueOf(color));
        isService4.setTextColor(color);
        isService4.setClickable(isClickable);
        isService5.setButtonTintList(ColorStateList.valueOf(color));
        isService5.setTextColor(color);
        isService5.setClickable(isClickable);
        // Lock/unlock description
        descriptionText.setTextColor(color);
        petSitterDescriptionView.setBackground(descDrawable);
        petSitterDescriptionView.setEnabled(isClickable);
        petSitterDescriptionView.setHintTextColor(hintColor);
    }

    private void register() {
        hideKeyboard();

        // For showing progress indicator
        ScrollView scrollView1 = this.scrollView;
        new Handler().postDelayed(() -> scrollView1.fullScroll(View.FOCUS_DOWN), 100);

        registerButton.setEnabled(false);

        String nameInput = nameView.getText().toString().trim();
        String surnameInput = surnameView.getText().toString().trim();
        String regionInput = regionsSpinner.getSelectedItem().toString();
        String provinceInput;
        Object provinceInpObj = provincesSpinner.getSelectedItem();
        if (provinceInpObj == null) {
            provinceInput = null;
        } else {
            provinceInput = provinceInpObj.toString();
        }
        String addressInput = addressView.getText().toString().trim();
        String phoneNumberInput = phoneNumberView.getText().toString().trim();
        String usernameInput = usernameView.getText().toString().trim();
        String emailInput = emailView.getText().toString().trim();
        String passwordInput = passwordView.getText().toString();
        String passwordConfirmInput = passwordConfirmView.getText().toString();
        String firstPetNameInput = firstPetNameView.getText().toString().trim();
        boolean petSitterInput = isPetSitter.isChecked();
        boolean dogInput = isDog.isChecked();
        boolean catInput = isCat.isChecked();
        boolean otherPetsInput = isOtherPets.isChecked();
        boolean service1Input = isService1.isChecked();
        boolean service2Input = isService2.isChecked();
        boolean service3Input = isService3.isChecked();
        boolean service4Input = isService4.isChecked();
        boolean service5Input = isService5.isChecked();
        String petSitterDescriptionInput = petSitterDescriptionView.getText().toString().trim();

        SystemUserData systemUserData = new SystemUserData();
        systemUserData.setFirstPetName(firstPetNameInput);
        systemUserData.setEmail(emailInput);
        systemUserData.setPassword(passwordInput);
        systemUserData.setPasswordConfirm(passwordConfirmInput);
        systemUserData.setPetSitter(petSitterInput);
        systemUserData.setUsername(usernameInput);

        ProfileUserData profileUserData = new ProfileUserData();
        profileUserData.setAddress(addressInput);
        profileUserData.setName(nameInput);
        profileUserData.setPhoneNumb(phoneNumberInput);
        profileUserData.setProvince(provinceInput);
        profileUserData.setRegion(regionInput);
        profileUserData.setSurname(surnameInput);

        PetSitServices petSitServices = new PetSitServices();
        petSitServices.setDescription(petSitterDescriptionInput);
        petSitServices.setServ1(service1Input);
        petSitServices.setServ2(service2Input);
        petSitServices.setServ3(service3Input);
        petSitServices.setServ4(service4Input);
        petSitServices.setServ5(service5Input);

        PetSitCaredPets petSitCaredPets = new PetSitCaredPets();
        petSitCaredPets.setDog(dogInput);
        petSitCaredPets.setCat(catInput);
        petSitCaredPets.setOtherPets(otherPetsInput);

        RegistrationCredentials registrationCredentials = new RegistrationCredentials();
        registrationCredentials.setPetSitCaredPets(petSitCaredPets);
        registrationCredentials.setPetSitServices(petSitServices);
        registrationCredentials.setProfileUserData(profileUserData);
        registrationCredentials.setSystemUserData(systemUserData);

        presenter.registerUser(registrationCredentials);
    }

    private void hideKeyboard() {
        KeyboardSingletonClass keyboardSingletonClass = KeyboardSingletonClass.getSingletonInstance();
        keyboardSingletonClass.hide(this);
    }

    @Override
    public void hideProgressIndicator() {
        this.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showProgressIndicator() {
        this.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        this.registerButton.setEnabled(true);
    }

    @Override
    public void onSuccess() {
        presenter.sendEmail(this, usernameView.getText().toString().trim(), emailView.getText().toString().trim());

        // Wait for sending email
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }, 2000);
    }

}
