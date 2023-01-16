package com.application.mypet.services.search.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.application.mypet.R;
import com.application.mypet.registration.data.PetSitterCaredPetsCredentials;
import com.application.mypet.services.HomeActivity;
import com.application.mypet.services.search.PetSitSearchPresenter;
import com.application.mypet.services.search.SearchContract;
import com.application.mypet.services.search.data.PetSitSearchFilters;
import com.application.mypet.utils.factory.FactoryProvinces;
import com.application.mypet.utils.factory.ProvincesBaseList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StartPetSitSearchFragment extends Fragment implements SearchContract.PetSitSearchInputView {
    private static final String ARG_PARAM1 = "param1";
    private static final String PET_SITTER_SEARCH_FRAGMENT_TAG = "petSitSearchFragmentTag";
    /* access modifiers changed from: private */
    public ArrayAdapter<String> adapterProvinces;
    private Button findButton;
    private PetSitSearchFilters petSitSearchFilters;
    private ProgressBar progressBar;
    /* access modifiers changed from: private */
    public List<String> provinceList;
    /* access modifiers changed from: private */
    public View provincesSpinnerView;
    private ScrollView scrollView;
    private String user;

    public static StartPetSitSearchFragment newInstance(String user2) {
        StartPetSitSearchFragment fragment = new StartPetSitSearchFragment();
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
        View initPetSitSearchView = inflater.inflate(R.layout.fragment_start_pet_sit_search, container, false);
        PetSitSearchPresenter petSitSearchPresenter = new PetSitSearchPresenter((SearchContract.PetSitSearchInputView) this);
        Spinner region = (Spinner) initPetSitSearchView.findViewById(R.id.spinner_regions);
        final Spinner province = (Spinner) initPetSitSearchView.findViewById(R.id.spinner_provinces);
        this.findButton = (Button) initPetSitSearchView.findViewById(R.id.find_button);
        this.progressBar = (ProgressBar) initPetSitSearchView.findViewById(R.id.progressBar);
        this.scrollView = (ScrollView) initPetSitSearchView.findViewById(R.id.scrollView);
        ImageView back = (ImageView) initPetSitSearchView.findViewById(R.id.back_arrow);
        this.provincesSpinnerView = initPetSitSearchView.findViewById(R.id.spinner_provinces_view);
        ArrayList arrayList = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.regions)));
        this.provinceList = new ArrayList();
        ArrayAdapter<String> adapterRegions = newInstanceAdapter(requireActivity().getApplicationContext(), R.layout.custom_spinner_list, arrayList);
        adapterRegions.setDropDownViewResource(R.layout.custom_spinner_list);
        region.setAdapter(adapterRegions);
        region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private ProvincesBaseList provincesBaseList;

            public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int position, long id) {
                FactoryProvinces factoryProvinces = new FactoryProvinces(StartPetSitSearchFragment.this.getContext());
                if (position != 0) {
                    StartPetSitSearchFragment.this.provincesSpinnerView.setVisibility(8);
                }
                try {
                    this.provincesBaseList = factoryProvinces.createProvinceBaseList(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                List unused = StartPetSitSearchFragment.this.provinceList = this.provincesBaseList.createProvinceList();
                StartPetSitSearchFragment startPetSitSearchFragment = StartPetSitSearchFragment.this;
                ArrayAdapter unused2 = startPetSitSearchFragment.adapterProvinces = startPetSitSearchFragment.newInstanceAdapter(startPetSitSearchFragment.requireActivity().getApplicationContext(), R.layout.custom_spinner_list, StartPetSitSearchFragment.this.provinceList);
                StartPetSitSearchFragment.this.adapterProvinces.setDropDownViewResource(R.layout.custom_spinner_list);
                province.setAdapter(StartPetSitSearchFragment.this.adapterProvinces);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.provincesSpinnerView.setOnClickListener(new StartPetSitSearchFragment$$ExternalSyntheticLambda1(this));
        back.setOnClickListener(new StartPetSitSearchFragment$$ExternalSyntheticLambda2(this));
        Button button = this.findButton;
        StartPetSitSearchFragment$$ExternalSyntheticLambda3 startPetSitSearchFragment$$ExternalSyntheticLambda3 = r0;
        ArrayAdapter<String> arrayAdapter = adapterRegions;
        ArrayList arrayList2 = arrayList;
        ImageView imageView = back;
        Spinner spinner = province;
        StartPetSitSearchFragment$$ExternalSyntheticLambda3 startPetSitSearchFragment$$ExternalSyntheticLambda32 = new StartPetSitSearchFragment$$ExternalSyntheticLambda3(this, (CheckBox) initPetSitSearchView.findViewById(R.id.cared_pets_checkBox1), (CheckBox) initPetSitSearchView.findViewById(R.id.cared_pets_checkBox2), (CheckBox) initPetSitSearchView.findViewById(R.id.cared_pets_checkBox3), region, province, petSitSearchPresenter);
        button.setOnClickListener(startPetSitSearchFragment$$ExternalSyntheticLambda3);
        return initPetSitSearchView;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$0$com-application-mypet-services-search-view-StartPetSitSearchFragment  reason: not valid java name */
    public /* synthetic */ void m19lambda$onCreateView$0$comapplicationmypetservicessearchviewStartPetSitSearchFragment(View view) {
        Toast.makeText(getContext(), "First select the region", 0).show();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$1$com-application-mypet-services-search-view-StartPetSitSearchFragment  reason: not valid java name */
    public /* synthetic */ void m20lambda$onCreateView$1$comapplicationmypetservicessearchviewStartPetSitSearchFragment(View view) {
        ((HomeActivity) requireContext()).getSupportFragmentManager().popBackStackImmediate();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$2$com-application-mypet-services-search-view-StartPetSitSearchFragment  reason: not valid java name */
    public /* synthetic */ void m21lambda$onCreateView$2$comapplicationmypetservicessearchviewStartPetSitSearchFragment(CheckBox petCheck1, CheckBox petCheck2, CheckBox petCheck3, Spinner region, Spinner province, PetSitSearchPresenter petSitSearchPresenter, View view) {
        String provinceInput;
        this.findButton.setEnabled(false);
        boolean dog = petCheck1.isChecked();
        boolean cat = petCheck2.isChecked();
        boolean otherPets = petCheck3.isChecked();
        String regionInput = region.getSelectedItem().toString();
        Object provinceInpObj = province.getSelectedItem();
        if (provinceInpObj == null) {
            provinceInput = null;
        } else {
            provinceInput = provinceInpObj.toString();
        }
        PetSitSearchFilters petSitSearchFilters2 = new PetSitSearchFilters(new PetSitterCaredPetsCredentials(dog, cat, otherPets), regionInput, provinceInput);
        this.petSitSearchFilters = petSitSearchFilters2;
        petSitSearchPresenter.isValidInput(petSitSearchFilters2);
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

    public void showProgressbar() {
        new Handler().postDelayed(new StartPetSitSearchFragment$$ExternalSyntheticLambda0(this.scrollView), 100);
        this.progressBar.setVisibility(0);
    }

    public void hideProgressbar() {
        this.progressBar.setVisibility(8);
    }

    public void onInputSearchSuccess() {
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, PetSitResultsFragment.newInstance(this.petSitSearchFilters, this.user)).addToBackStack(PET_SITTER_SEARCH_FRAGMENT_TAG).commit();
    }

    public void onInputSearchFailed(String message) {
        Toast.makeText(getContext(), message, 0).show();
        this.findButton.setEnabled(true);
    }
}
