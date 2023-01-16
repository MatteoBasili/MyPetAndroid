package com.application.mypet.registration.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.application.mypet.R;
import com.application.mypet.login.view.LoginActivity;
import com.application.mypet.registration.RegistrationContract;
import com.application.mypet.registration.RegistrationPresenter;
import com.application.mypet.registration.data.PetSitterCaredPetsCredentials;
import com.application.mypet.registration.data.PetSitterServicesCredentials;
import com.application.mypet.registration.data.ProfileCredentials;
import com.application.mypet.registration.data.RegistrationCredentials;
import com.application.mypet.registration.data.SystemCredentials;
import com.application.mypet.utils.email.SendMail;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class RegistrationActivity extends AppCompatActivity implements RegistrationContract.RegistrationView {
    /* access modifiers changed from: private */
    public ArrayAdapter<String> adapterProvinces;
    private String email;
    private ProgressBar progressBar;
    /* access modifiers changed from: private */
    public List<String> provinceList;
    /* access modifiers changed from: private */
    public View provincesSpinnerView;
    private Button register;
    private ScrollView scrollView;
    private String username;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_account_registration);
        AtomicInteger colorForPetSitter = new AtomicInteger();
        AtomicInteger hintColor = new AtomicInteger();
        AtomicBoolean clickable = new AtomicBoolean();
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        RegistrationPresenter presenter = new RegistrationPresenter(this);
        EditText passwordView = (EditText) findViewById(R.id.pwd_input);
        EditText passwordConfirmView = (EditText) findViewById(R.id.pwd_confirm_input);
        CheckBox petSitterCheck = (CheckBox) findViewById(R.id.pet_sitter_checkBox);
        CheckBox pet1 = (CheckBox) findViewById(R.id.cared_pets_checkBox1);
        CheckBox pet2 = (CheckBox) findViewById(R.id.cared_pets_checkBox2);
        CheckBox pet3 = (CheckBox) findViewById(R.id.cared_pets_checkBox3);
        CheckBox service1 = (CheckBox) findViewById(R.id.services_checkBox1);
        CheckBox service2 = (CheckBox) findViewById(R.id.services_checkBox2);
        CheckBox service3 = (CheckBox) findViewById(R.id.services_checkBox3);
        CheckBox service4 = (CheckBox) findViewById(R.id.services_checkBox4);
        CheckBox service5 = (CheckBox) findViewById(R.id.services_checkBox5);
        EditText description = (EditText) findViewById(R.id.pet_sitter_desc_input);
        this.register = (Button) findViewById(R.id.create_account_button);
        this.scrollView = (ScrollView) findViewById(R.id.scrollView);
        this.provincesSpinnerView = findViewById(R.id.spinner_provinces_view);
        Spinner spinnerRegions = (Spinner) findViewById(R.id.spinner_regions);
        Spinner spinnerProvinces = (Spinner) findViewById(R.id.spinner_provinces);
        final Spinner spinner = spinnerProvinces;
        List<String> arrayList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.regions)));
        ArrayList arrayList2 = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.provinces_abruzzo)));
        ArrayList arrayList3 = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.provinces_basilicata)));
        List<String> list = arrayList;
        List<String> provincesCalabria = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_calabria)));
        Spinner spinnerRegions2 = spinnerRegions;
        List<String> provincesCampania = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_campania)));
        CheckBox petSitterCheck2 = petSitterCheck;
        final List<String> provincesEmiliaRomagna = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_emilia_romagna)));
        List<String> list2 = list;
        ImageView hidePwd2 = (ImageView) findViewById(R.id.hide_pwd2);
        final List<String> provincesFriuliVeneziaGiulia = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_friuli_venezia_giulia)));
        Spinner spinner2 = spinnerRegions2;
        ImageView showPwd2 = (ImageView) findViewById(R.id.show_pwd2);
        final List<String> provincesLazio = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_lazio)));
        List<String> list3 = list;
        CheckBox checkBox = petSitterCheck2;
        ImageView hidePwd1 = (ImageView) findViewById(R.id.hide_pwd1);
        final List<String> provincesLiguria = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_liguria)));
        List<String> list4 = list;
        Spinner spinner3 = spinnerRegions2;
        ImageView imageView = hidePwd2;
        ImageView showPwd1 = (ImageView) findViewById(R.id.show_pwd1);
        final List<String> provincesLombardia = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_lombardia)));
        ArrayList arrayList4 = arrayList2;
        Spinner spinnerRegions3 = spinnerRegions2;
        CheckBox checkBox2 = petSitterCheck2;
        ImageView imageView2 = showPwd2;
        EditText passwordConfirmView2 = passwordConfirmView;
        final List<String> provincesMarche = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_marche)));
        CheckBox petSitterCheck3 = petSitterCheck2;
        ImageView imageView3 = hidePwd2;
        ImageView imageView4 = hidePwd1;
        EditText passwordView2 = passwordView;
        final List<String> provincesMolise = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_molise)));
        ImageView imageView5 = showPwd1;
        ImageView hidePwd22 = hidePwd2;
        ImageView imageView6 = showPwd2;
        ImageView back = (ImageView) findViewById(R.id.back_arrow);
        final List<String> provincesPiemonte = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_piemonte)));
        EditText editText = passwordConfirmView2;
        ImageView showPwd22 = showPwd2;
        ImageView imageView7 = hidePwd1;
        ArrayList arrayList5 = arrayList3;
        final List<String> provincesPuglia = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_puglia)));
        ImageView imageView8 = showPwd1;
        ImageView hidePwd12 = hidePwd1;
        EditText passwordView3 = passwordView2;
        ImageView showPwd12 = showPwd1;
        EditText passwordConfirmView3 = passwordConfirmView2;
        final List<String> provincesSardegna = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_sardegna)));
        ImageView back2 = back;
        List<String> provincesCampania2 = provincesCampania;
        final List<String> provincesSicilia = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_sicilia)));
        final List<String> provincesToscana = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_toscana)));
        final List<String> provincesTrentinoAltoAdige = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_trentino_alto_adige)));
        final List<String> provincesUmbria = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_umbria)));
        final List<String> provincesValleDAosta = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_valle_d_Aosta)));
        final List<String> provincesVeneto = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_veneto)));
        this.provinceList = new ArrayList();
        ArrayAdapter<String> adapterRegions = newInstanceAdapter(getApplicationContext(), R.layout.custom_spinner_list, list4);
        adapterRegions.setDropDownViewResource(R.layout.custom_spinner_list);
        spinnerRegions3.setAdapter(adapterRegions);
        Spinner spinnerRegions4 = spinnerRegions3;
        AnonymousClass1 r99 = r0;
        final ArrayList arrayList6 = arrayList4;
        final List<String> provincesCalabria2 = provincesCalabria;
        final List<String> provincesCampania3 = provincesCampania2;
        List<String> provincesAbruzzo = list4;
        ArrayList arrayList7 = arrayList5;
        ArrayAdapter<String> arrayAdapter = adapterRegions;
        final ArrayList arrayList8 = arrayList7;
        AnonymousClass1 r0 = new AdapterView.OnItemSelectedListener(this) {
            final /* synthetic */ RegistrationActivity this$0;

            {
                this.this$0 = this$0;
            }

            public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int position, long id) {
                if (position != 0) {
                    this.this$0.provincesSpinnerView.setVisibility(8);
                }
                switch (position) {
                    case 1:
                        List unused = this.this$0.provinceList = arrayList6;
                        break;
                    case 2:
                        List unused2 = this.this$0.provinceList = arrayList8;
                        break;
                    case 3:
                        List unused3 = this.this$0.provinceList = provincesCalabria2;
                        break;
                    case 4:
                        List unused4 = this.this$0.provinceList = provincesCampania3;
                        break;
                    case 5:
                        List unused5 = this.this$0.provinceList = provincesEmiliaRomagna;
                        break;
                    case 6:
                        List unused6 = this.this$0.provinceList = provincesFriuliVeneziaGiulia;
                        break;
                    case 7:
                        List unused7 = this.this$0.provinceList = provincesLazio;
                        break;
                    case 8:
                        List unused8 = this.this$0.provinceList = provincesLiguria;
                        break;
                    case 9:
                        List unused9 = this.this$0.provinceList = provincesLombardia;
                        break;
                    case 10:
                        List unused10 = this.this$0.provinceList = provincesMarche;
                        break;
                    case 11:
                        List unused11 = this.this$0.provinceList = provincesMolise;
                        break;
                    case 12:
                        List unused12 = this.this$0.provinceList = provincesPiemonte;
                        break;
                    case 13:
                        List unused13 = this.this$0.provinceList = provincesPuglia;
                        break;
                    case 14:
                        List unused14 = this.this$0.provinceList = provincesSardegna;
                        break;
                    case 15:
                        List unused15 = this.this$0.provinceList = provincesSicilia;
                        break;
                    case 16:
                        List unused16 = this.this$0.provinceList = provincesToscana;
                        break;
                    case 17:
                        List unused17 = this.this$0.provinceList = provincesTrentinoAltoAdige;
                        break;
                    case 18:
                        List unused18 = this.this$0.provinceList = provincesUmbria;
                        break;
                    case 19:
                        List unused19 = this.this$0.provinceList = provincesValleDAosta;
                        break;
                    case 20:
                        List unused20 = this.this$0.provinceList = provincesVeneto;
                        break;
                }
                RegistrationActivity registrationActivity = this.this$0;
                ArrayAdapter unused21 = registrationActivity.adapterProvinces = registrationActivity.newInstanceAdapter(registrationActivity.getApplicationContext(), R.layout.custom_spinner_list, this.this$0.provinceList);
                this.this$0.adapterProvinces.setDropDownViewResource(R.layout.custom_spinner_list);
                spinner.setAdapter(this.this$0.adapterProvinces);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };
        spinnerRegions4.setOnItemSelectedListener(r99);
        ArrayList arrayList9 = arrayList6;
        this.provincesSpinnerView.setOnClickListener(new RegistrationActivity$$ExternalSyntheticLambda8(this));
        back2.setOnClickListener(new RegistrationActivity$$ExternalSyntheticLambda9(this));
        ImageView hidePwd13 = hidePwd12;
        EditText passwordView4 = passwordView3;
        ArrayList arrayList10 = arrayList8;
        ImageView showPwd13 = showPwd12;
        showPwd13.setOnClickListener(new RegistrationActivity$$ExternalSyntheticLambda10(passwordView4, hidePwd13, showPwd13));
        hidePwd13.setOnClickListener(new RegistrationActivity$$ExternalSyntheticLambda11(passwordView4, showPwd13, hidePwd13));
        ImageView showPwd23 = showPwd22;
        EditText passwordConfirmView4 = passwordConfirmView3;
        ImageView imageView9 = hidePwd22;
        ImageView hidePwd23 = hidePwd13;
        ImageView hidePwd14 = imageView9;
        showPwd23.setOnClickListener(new RegistrationActivity$$ExternalSyntheticLambda12(passwordConfirmView4, hidePwd14, showPwd23));
        hidePwd14.setOnClickListener(new RegistrationActivity$$ExternalSyntheticLambda1(passwordConfirmView4, showPwd23, hidePwd14));
        CheckBox petSitterCheck4 = petSitterCheck3;
        petSitterCheck4.setOnClickListener(new RegistrationActivity$$ExternalSyntheticLambda2(this, petSitterCheck, colorForPetSitter, clickable, description, hintColor, (TextView) findViewById(R.id.cared_pets), pet1, pet2, pet3, (TextView) findViewById(R.id.offered_services), service1, service2, service3, service4, service5, (TextView) findViewById(R.id.pet_sitter_desc)));
        this.register.setOnClickListener(new RegistrationActivity$$ExternalSyntheticLambda3(this, (EditText) findViewById(R.id.name_input), (EditText) findViewById(R.id.surname_input), (EditText) findViewById(R.id.username_input), (EditText) findViewById(R.id.email_input), passwordView, passwordConfirmView, spinnerRegions, spinnerProvinces, (EditText) findViewById(R.id.address_input), (EditText) findViewById(R.id.phone_numb_input), (EditText) findViewById(R.id.first_pet_name_input), petSitterCheck, pet1, pet2, pet3, service1, service2, service3, service4, service5, description, presenter));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$0$com-application-mypet-registration-view-RegistrationActivity  reason: not valid java name */
    public /* synthetic */ void m110lambda$onCreate$0$comapplicationmypetregistrationviewRegistrationActivity(View view) {
        Toast.makeText(this, "First select the region", 0).show();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$3$com-application-mypet-registration-view-RegistrationActivity  reason: not valid java name */
    public /* synthetic */ void m112lambda$onCreate$3$comapplicationmypetregistrationviewRegistrationActivity(View view) {
        View confirmView = LayoutInflater.from(this).inflate(R.layout.confirm_dialog, (ViewGroup) null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(confirmView);
        AlertDialog dialog = dialogBuilder.show();
        ((Button) confirmView.findViewById(R.id.yes_button)).setOnClickListener(new RegistrationActivity$$ExternalSyntheticLambda5(this));
        ((Button) confirmView.findViewById(R.id.no_button)).setOnClickListener(new RegistrationActivity$$ExternalSyntheticLambda6(dialog));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$1$com-application-mypet-registration-view-RegistrationActivity  reason: not valid java name */
    public /* synthetic */ void m111lambda$onCreate$1$comapplicationmypetregistrationviewRegistrationActivity(View view1) {
        finish();
    }

    static /* synthetic */ void lambda$onCreate$4(EditText passwordView, ImageView hidePwd1, ImageView showPwd1, View view) {
        passwordView.setTransformationMethod(new HideReturnsTransformationMethod());
        passwordView.setSelection(passwordView.getText().length());
        hidePwd1.setVisibility(0);
        showPwd1.setVisibility(4);
    }

    static /* synthetic */ void lambda$onCreate$5(EditText passwordView, ImageView showPwd1, ImageView hidePwd1, View view) {
        passwordView.setTransformationMethod(new PasswordTransformationMethod());
        passwordView.setSelection(passwordView.getText().length());
        showPwd1.setVisibility(0);
        hidePwd1.setVisibility(4);
    }

    static /* synthetic */ void lambda$onCreate$6(EditText passwordConfirmView, ImageView hidePwd2, ImageView showPwd2, View view) {
        passwordConfirmView.setTransformationMethod(new HideReturnsTransformationMethod());
        passwordConfirmView.setSelection(passwordConfirmView.getText().length());
        hidePwd2.setVisibility(0);
        showPwd2.setVisibility(4);
    }

    static /* synthetic */ void lambda$onCreate$7(EditText passwordConfirmView, ImageView showPwd2, ImageView hidePwd2, View view) {
        passwordConfirmView.setTransformationMethod(new PasswordTransformationMethod());
        passwordConfirmView.setSelection(passwordConfirmView.getText().length());
        showPwd2.setVisibility(0);
        hidePwd2.setVisibility(4);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$8$com-application-mypet-registration-view-RegistrationActivity  reason: not valid java name */
    public /* synthetic */ void m113lambda$onCreate$8$comapplicationmypetregistrationviewRegistrationActivity(CheckBox petSitterCheck, AtomicInteger colorForPetSitter, AtomicBoolean clickable, EditText description, AtomicInteger hintColor, TextView caredPets, CheckBox pet1, CheckBox pet2, CheckBox pet3, TextView offeredServices, CheckBox service1, CheckBox service2, CheckBox service3, CheckBox service4, CheckBox service5, TextView petSitterDescription, View view) {
        AtomicInteger atomicInteger = colorForPetSitter;
        AtomicBoolean atomicBoolean = clickable;
        EditText editText = description;
        AtomicInteger atomicInteger2 = hintColor;
        CheckBox checkBox = pet1;
        CheckBox checkBox2 = pet2;
        CheckBox checkBox3 = pet3;
        CheckBox checkBox4 = service1;
        CheckBox checkBox5 = service2;
        CheckBox checkBox6 = service3;
        CheckBox checkBox7 = service4;
        CheckBox checkBox8 = service5;
        if (petSitterCheck.isChecked()) {
            atomicInteger.set(R.color.black);
            atomicBoolean.set(true);
            editText.setBackgroundResource(R.drawable.rounded_rectangle_input);
            atomicInteger2.set(R.color.disable_color);
        } else {
            atomicInteger.set(R.color.disable_color);
            atomicBoolean.set(false);
            editText.setBackgroundResource(R.drawable.rounded_rectangle_input_locked);
            atomicInteger2.set(R.color.disable_hint_color);
        }
        caredPets.setTextColor(getColor(colorForPetSitter.get()));
        checkBox.setButtonTintList(ColorStateList.valueOf(getColor(colorForPetSitter.get())));
        checkBox.setTextColor(getColor(colorForPetSitter.get()));
        checkBox.setClickable(clickable.get());
        checkBox2.setButtonTintList(ColorStateList.valueOf(getColor(colorForPetSitter.get())));
        checkBox2.setTextColor(getColor(colorForPetSitter.get()));
        checkBox2.setClickable(clickable.get());
        checkBox3.setButtonTintList(ColorStateList.valueOf(getColor(colorForPetSitter.get())));
        checkBox3.setTextColor(getColor(colorForPetSitter.get()));
        checkBox3.setClickable(clickable.get());
        offeredServices.setTextColor(getColor(colorForPetSitter.get()));
        checkBox4.setButtonTintList(ColorStateList.valueOf(getColor(colorForPetSitter.get())));
        checkBox4.setTextColor(getColor(colorForPetSitter.get()));
        checkBox4.setClickable(clickable.get());
        checkBox5.setButtonTintList(ColorStateList.valueOf(getColor(colorForPetSitter.get())));
        checkBox5.setTextColor(getColor(colorForPetSitter.get()));
        checkBox5.setClickable(clickable.get());
        checkBox6.setButtonTintList(ColorStateList.valueOf(getColor(colorForPetSitter.get())));
        checkBox6.setTextColor(getColor(colorForPetSitter.get()));
        checkBox6.setClickable(clickable.get());
        checkBox7.setButtonTintList(ColorStateList.valueOf(getColor(colorForPetSitter.get())));
        checkBox7.setTextColor(getColor(colorForPetSitter.get()));
        checkBox7.setClickable(clickable.get());
        checkBox8.setButtonTintList(ColorStateList.valueOf(getColor(colorForPetSitter.get())));
        checkBox8.setTextColor(getColor(colorForPetSitter.get()));
        checkBox8.setClickable(clickable.get());
        petSitterDescription.setTextColor(getColor(colorForPetSitter.get()));
        editText.setEnabled(clickable.get());
        editText.setHintTextColor(getColor(hintColor.get()));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$9$com-application-mypet-registration-view-RegistrationActivity  reason: not valid java name */
    public /* synthetic */ void m114lambda$onCreate$9$comapplicationmypetregistrationviewRegistrationActivity(EditText nameView, EditText surnameView, EditText usernameView, EditText emailView, EditText passwordView, EditText passwordConfirmView, Spinner spinnerRegions, Spinner spinnerProvinces, EditText addressView, EditText phoneView, EditText firstPetNameView, CheckBox petSitterCheck, CheckBox pet1, CheckBox pet2, CheckBox pet3, CheckBox service1, CheckBox service2, CheckBox service3, CheckBox service4, CheckBox service5, EditText description, RegistrationPresenter presenter, View view) {
        String provinceInput;
        this.register.setEnabled(false);
        closeKeyboard();
        String name = nameView.getText().toString().trim();
        String surname = surnameView.getText().toString().trim();
        this.username = usernameView.getText().toString().trim();
        this.email = emailView.getText().toString().trim();
        String password = passwordView.getText().toString();
        String passwordConfirm = passwordConfirmView.getText().toString();
        String region = spinnerRegions.getSelectedItem().toString();
        Object provinceInpObj = spinnerProvinces.getSelectedItem();
        if (provinceInpObj == null) {
            provinceInput = null;
        } else {
            provinceInput = provinceInpObj.toString();
        }
        String address = addressView.getText().toString().trim();
        String phoneNumber = phoneView.getText().toString().trim();
        String firstPetName = firstPetNameView.getText().toString().trim();
        boolean petSitter = petSitterCheck.isChecked();
        boolean dog = pet1.isChecked();
        boolean cat = pet2.isChecked();
        boolean otherPets = pet3.isChecked();
        boolean otherPets2 = otherPets;
        boolean cat2 = cat;
        boolean dog2 = dog;
        boolean z = dog2;
        boolean z2 = cat2;
        boolean z3 = otherPets2;
        RegistrationPresenter registrationPresenter = presenter;
        registrationPresenter.start(new RegistrationCredentials(new SystemCredentials(this.username, this.email, password, passwordConfirm, firstPetName, petSitter), new ProfileCredentials(name, surname, region, provinceInput, address, phoneNumber), new PetSitterCaredPetsCredentials(dog2, cat2, otherPets2), new PetSitterServicesCredentials(service1.isChecked(), service2.isChecked(), service3.isChecked(), service4.isChecked(), service5.isChecked(), description.getText().toString().trim())));
    }

    public ArrayAdapter<String> newInstanceAdapter(Context context, int layout, List<String> objList) {
        final Context context2 = context;
        return new ArrayAdapter<String>(context, layout, objList) {
            public boolean isEnabled(int position) {
                return position != 0;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(context2.getResources().getColor(R.color.disable_color));
                } else {
                    tv.setTextColor(context2.getResources().getColor(R.color.black));
                }
                return view;
            }
        };
    }

    public void closeKeyboard() {
        try {
            ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 2);
        } catch (NullPointerException e) {
            Log.e("Null Pointer Error: ", e.getMessage());
        }
    }

    public void showProgressbar() {
        new Handler().postDelayed(new RegistrationActivity$$ExternalSyntheticLambda7(this.scrollView), 100);
        this.progressBar.setVisibility(0);
    }

    public void hideProgressbar() {
        this.progressBar.setVisibility(8);
    }

    public void onSuccess() {
        new SendMail(this, this.email, "Registration Completed!", "Hi " + this.username + ", welcome to MyPet!\nWe are very happy that you are part of our community!\n\nEnter now to take advantage of our services, our puppies are waiting for you!", "Registration completed").execute(new Void[0]);
        this.register.setEnabled(true);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void onFailed(String message) {
        Toast.makeText(this, message, 0).show();
        this.register.setEnabled(true);
    }

    public void onBackPressed() {
        View confirmView = LayoutInflater.from(this).inflate(R.layout.confirm_dialog, (ViewGroup) null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(confirmView);
        AlertDialog dialog = dialogBuilder.show();
        ((Button) confirmView.findViewById(R.id.yes_button)).setOnClickListener(new RegistrationActivity$$ExternalSyntheticLambda0(this));
        ((Button) confirmView.findViewById(R.id.no_button)).setOnClickListener(new RegistrationActivity$$ExternalSyntheticLambda4(dialog));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onBackPressed$11$com-application-mypet-registration-view-RegistrationActivity  reason: not valid java name */
    public /* synthetic */ void m109lambda$onBackPressed$11$comapplicationmypetregistrationviewRegistrationActivity(View view1) {
        finish();
    }
}
