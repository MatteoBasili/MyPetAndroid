package com.application.mypet.services.profile.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.application.mypet.R;
import com.application.mypet.registration.data.ProfileCredentials;
import com.application.mypet.services.HomeActivity;
import com.application.mypet.services.profile.PersonalInfoPresenter;
import com.application.mypet.services.profile.ProfileContract;
import com.application.mypet.services.profile.data.PersonalInformations;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PersonalInfoFragment extends Fragment implements ProfileContract.PersonalInfoView {
    private static final String ARG_PARAM1 = "param1";
    /* access modifiers changed from: private */
    public ArrayAdapter<String> adapterProvinces;
    private ArrayAdapter<String> adapterRegions;
    private EditText address;
    private EditText email;
    private EditText firstPetName;
    /* access modifiers changed from: private */
    public boolean isLoadSuccess;
    private ProgressBar loadProgressBar;
    private EditText name;
    private EditText phoneNumber;
    /* access modifiers changed from: private */
    public Spinner province;
    /* access modifiers changed from: private */
    public List<String> provinceList;
    /* access modifiers changed from: private */
    public String provinceText;
    /* access modifiers changed from: private */
    public View provincesSpinnerView;
    private Spinner region;
    private Button save;
    private ProgressBar saveProgressBar;
    private ScrollView scrollView;
    private EditText surname;
    private String user;

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
        PersonalInfoPresenter presenter = new PersonalInfoPresenter(this);
        this.scrollView = (ScrollView) personalInfoView.findViewById(R.id.scrollView);
        this.name = (EditText) personalInfoView.findViewById(R.id.name_input);
        this.surname = (EditText) personalInfoView.findViewById(R.id.surname_input);
        this.region = (Spinner) personalInfoView.findViewById(R.id.spinner_regions);
        this.province = (Spinner) personalInfoView.findViewById(R.id.spinner_provinces);
        this.address = (EditText) personalInfoView.findViewById(R.id.address_input);
        this.email = (EditText) personalInfoView.findViewById(R.id.email_input);
        this.phoneNumber = (EditText) personalInfoView.findViewById(R.id.phone_numb_input);
        this.firstPetName = (EditText) personalInfoView.findViewById(R.id.first_pet_name_input);
        this.loadProgressBar = (ProgressBar) personalInfoView.findViewById(R.id.load_progressBar);
        this.saveProgressBar = (ProgressBar) personalInfoView.findViewById(R.id.save_progressBar);
        this.save = (Button) personalInfoView.findViewById(R.id.save_button);
        this.provincesSpinnerView = personalInfoView.findViewById(R.id.spinner_provinces_view);
        ArrayList arrayList = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.regions)));
        List<String> provincesAbruzzo = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_abruzzo)));
        List<String> provincesBasilicata = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_basilicata)));
        List<String> provincesCalabria = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_calabria)));
        final ArrayList arrayList2 = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.provinces_campania)));
        final ArrayList arrayList3 = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.provinces_emilia_romagna)));
        final List<String> provincesFriuliVeneziaGiulia = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_friuli_venezia_giulia)));
        ArrayList arrayList4 = arrayList;
        final List<String> provincesLazio = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_lazio)));
        ImageView back = (ImageView) personalInfoView.findViewById(R.id.back_arrow);
        final List<String> provincesLiguria = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_liguria)));
        PersonalInfoPresenter presenter2 = presenter;
        final List<String> provincesLombardia = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_lombardia)));
        ArrayList arrayList5 = arrayList4;
        View personalInfoView2 = personalInfoView;
        final List<String> provincesMarche = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_marche)));
        ImageView imageView = back;
        final List<String> provincesMolise = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_molise)));
        ArrayList arrayList6 = arrayList4;
        PersonalInfoPresenter personalInfoPresenter = presenter2;
        final List<String> provincesPiemonte = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_piemonte)));
        ArrayList arrayList7 = arrayList4;
        ImageView imageView2 = back;
        View personalInfoView3 = personalInfoView2;
        final List<String> provincesPuglia = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_puglia)));
        ArrayList arrayList8 = arrayList4;
        PersonalInfoPresenter presenter3 = presenter2;
        List<String> provincesAbruzzo2 = provincesAbruzzo;
        ArrayList arrayList9 = arrayList4;
        ImageView back2 = back;
        final List<String> provincesSardegna = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_sardegna)));
        List<String> provincesCalabria2 = provincesCalabria;
        final List<String> provincesSicilia = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_sicilia)));
        final List<String> provincesToscana = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_toscana)));
        final List<String> provincesTrentinoAltoAdige = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_trentino_alto_adige)));
        final List<String> provincesUmbria = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_umbria)));
        final List<String> provincesValleDAosta = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_valle_d_Aosta)));
        final List<String> provincesVeneto = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.provinces_veneto)));
        this.provinceList = new ArrayList();
        ArrayAdapter<String> newInstanceAdapter = newInstanceAdapter(requireActivity().getApplicationContext(), R.layout.custom_spinner_list, arrayList9);
        this.adapterRegions = newInstanceAdapter;
        newInstanceAdapter.setDropDownViewResource(R.layout.custom_spinner_list);
        this.region.setAdapter(this.adapterRegions);
        Spinner spinner = this.region;
        AnonymousClass1 r32 = r0;
        ImageView back3 = back2;
        final List<String> provincesBasilicata2 = provincesBasilicata;
        final List<String> provincesCalabria3 = provincesCalabria2;
        ArrayList arrayList10 = arrayList9;
        final List<String> provincesAbruzzo3 = provincesAbruzzo2;
        AnonymousClass1 r0 = new AdapterView.OnItemSelectedListener(this) {
            final /* synthetic */ PersonalInfoFragment this$0;

            {
                this.this$0 = this$0;
            }

            public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int position, long id) {
                if (position != 0) {
                    this.this$0.provincesSpinnerView.setVisibility(8);
                }
                switch (position) {
                    case 1:
                        List unused = this.this$0.provinceList = provincesAbruzzo3;
                        break;
                    case 2:
                        List unused2 = this.this$0.provinceList = provincesBasilicata2;
                        break;
                    case 3:
                        List unused3 = this.this$0.provinceList = provincesCalabria3;
                        break;
                    case 4:
                        List unused4 = this.this$0.provinceList = arrayList2;
                        break;
                    case 5:
                        List unused5 = this.this$0.provinceList = arrayList3;
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
                PersonalInfoFragment personalInfoFragment = this.this$0;
                ArrayAdapter unused21 = personalInfoFragment.adapterProvinces = personalInfoFragment.newInstanceAdapter(personalInfoFragment.requireActivity().getApplicationContext(), R.layout.custom_spinner_list, this.this$0.provinceList);
                this.this$0.adapterProvinces.setDropDownViewResource(R.layout.custom_spinner_list);
                this.this$0.province.setAdapter(this.this$0.adapterProvinces);
                if (this.this$0.isLoadSuccess) {
                    this.this$0.province.setSelection(this.this$0.adapterProvinces.getPosition(this.this$0.provinceText));
                    boolean unused22 = this.this$0.isLoadSuccess = false;
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };
        spinner.setOnItemSelectedListener(r32);
        PersonalInfoPresenter presenter4 = presenter3;
        ArrayList arrayList11 = arrayList2;
        ArrayList arrayList12 = arrayList3;
        new Handler().postDelayed(new PersonalInfoFragment$$ExternalSyntheticLambda0(presenter4, this.user), 10);
        this.provincesSpinnerView.setOnClickListener(new PersonalInfoFragment$$ExternalSyntheticLambda1(this));
        back3.setOnClickListener(new PersonalInfoFragment$$ExternalSyntheticLambda2(this));
        this.save.setOnClickListener(new PersonalInfoFragment$$ExternalSyntheticLambda3(this, presenter4));
        return personalInfoView3;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$1$com-application-mypet-services-profile-view-PersonalInfoFragment  reason: not valid java name */
    public /* synthetic */ void m61lambda$onCreateView$1$comapplicationmypetservicesprofileviewPersonalInfoFragment(View view) {
        Toast.makeText(getContext(), "First select the region", 0).show();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$2$com-application-mypet-services-profile-view-PersonalInfoFragment  reason: not valid java name */
    public /* synthetic */ void m62lambda$onCreateView$2$comapplicationmypetservicesprofileviewPersonalInfoFragment(View view) {
        ((HomeActivity) requireContext()).getSupportFragmentManager().popBackStackImmediate();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$3$com-application-mypet-services-profile-view-PersonalInfoFragment  reason: not valid java name */
    public /* synthetic */ void m63lambda$onCreateView$3$comapplicationmypetservicesprofileviewPersonalInfoFragment(PersonalInfoPresenter presenter, View view) {
        String addressInput;
        this.save.setEnabled(false);
        try {
            ((InputMethodManager) requireActivity().getSystemService("input_method")).hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 2);
        } catch (NullPointerException e) {
            Log.e("Null Pointer Error: ", e.getMessage());
        }
        String nameInput = this.name.getText().toString().trim();
        String surnameInput = this.surname.getText().toString().trim();
        String regionInput = this.region.getSelectedItem().toString();
        String provinceInput = this.province.getSelectedItem().toString();
        String addressInput2 = this.address.getText().toString().trim();
        if (addressInput2.length() == 0) {
            addressInput = null;
        } else {
            addressInput = addressInput2;
        }
        String str = nameInput;
        PersonalInfoPresenter personalInfoPresenter = presenter;
        personalInfoPresenter.saveInfo(this.user, new PersonalInformations(new ProfileCredentials(nameInput, surnameInput, regionInput, provinceInput, addressInput, this.phoneNumber.getText().toString().trim()), this.email.getText().toString().trim(), this.firstPetName.getText().toString().trim()));
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

    public void showSaveProgressbar() {
        new Handler().postDelayed(new PersonalInfoFragment$$ExternalSyntheticLambda4(this.scrollView), 100);
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

    public void onLoadPersonalInfoSuccess(PersonalInformations personalInformations) {
        this.isLoadSuccess = true;
        this.name.setText(personalInformations.getProfileCredentials().getName());
        this.surname.setText(personalInformations.getProfileCredentials().getSurname());
        String regionText = personalInformations.getProfileCredentials().getRegion();
        this.provinceText = personalInformations.getProfileCredentials().getProvince();
        this.region.setSelection(this.adapterRegions.getPosition(regionText));
        this.address.setText(personalInformations.getProfileCredentials().getAddress());
        this.email.setText(personalInformations.getEmail());
        this.phoneNumber.setText(personalInformations.getProfileCredentials().getPhoneNumb());
        this.firstPetName.setText(personalInformations.getFirstPetName());
        this.name.setEnabled(true);
        this.surname.setEnabled(true);
        this.region.setEnabled(true);
        this.province.setEnabled(true);
        this.address.setEnabled(true);
        this.email.setEnabled(true);
        this.phoneNumber.setEnabled(true);
        this.firstPetName.setEnabled(true);
        this.save.setEnabled(true);
    }

    public void onStoreFailed(String message) {
        Toast.makeText(getContext(), message, 0).show();
        this.save.setEnabled(true);
    }

    public void onLoadFailed(String message) {
        this.isLoadSuccess = false;
        Toast.makeText(getContext(), message, 0).show();
    }
}
