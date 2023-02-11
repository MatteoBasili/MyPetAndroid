package com.application.mypetandroid.services.search.view;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.application.mypetandroid.R;
import com.application.mypetandroid.services.HomeActivity;
import com.application.mypetandroid.services.search.PetSitterSearchContract;
import com.application.mypetandroid.services.search.PetSitterSearchPresenter;
import com.application.mypetandroid.services.search.data.PetSitSearchFilters;
import com.application.mypetandroid.utils.factory_method_example.RegionsFactory;
import com.application.mypetandroid.utils.factory_method_example.regions.ProvincesBaseList;
import com.application.mypetandroid.utils.other.SpinnerAdapter;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StartPetSitSearchFragment extends Fragment implements PetSitterSearchContract.PetSitterSearchView {

    private static final Logger logger = Logger.getLogger(StartPetSitSearchFragment.class);
    private static final String ARG_PARAM1 = "param1";
    private static final String PET_SITTER_SEARCH_FRAGMENT_TAG = "petSitSearchFragmentTag";

    private Button findButton;
    private ProgressBar progressBar;
    private View provincesSpinnerView;
    private ScrollView scrollView;
    private CheckBox dogBox;
    private CheckBox catBox;
    private CheckBox otherPetsBox;
    private Spinner regionsSpinner;
    private Spinner provincesSpinner;
    private String user;
    private RegionsFactory regionsFactory;
    private PetSitterSearchPresenter presenter;

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

        dogBox = initPetSitSearchView.findViewById(R.id.cared_pets_checkBox1);
        catBox = initPetSitSearchView.findViewById(R.id.cared_pets_checkBox2);
        otherPetsBox = initPetSitSearchView.findViewById(R.id.cared_pets_checkBox3);
        regionsSpinner = initPetSitSearchView.findViewById(R.id.spinner_regions);
        provincesSpinner = initPetSitSearchView.findViewById(R.id.spinner_provinces);
        findButton = initPetSitSearchView.findViewById(R.id.find_button);
        progressBar = initPetSitSearchView.findViewById(R.id.progressBar);
        scrollView = initPetSitSearchView.findViewById(R.id.scrollView);
        ImageView back = initPetSitSearchView.findViewById(R.id.back_arrow);
        provincesSpinnerView = initPetSitSearchView.findViewById(R.id.spinner_provinces_view);

        // Initialize spinners
        initializeSpinners();

        presenter = new PetSitterSearchPresenter(this);

        findButton.setOnClickListener(v -> findPetSitter());
        back.setOnClickListener(v -> back());
        provincesSpinnerView.setOnClickListener(v -> Toast.makeText(getContext(), "First select the region", Toast.LENGTH_SHORT).show());

        return initPetSitSearchView;
    }

    private void initializeSpinners() {
        regionsFactory = new RegionsFactory(getContext());
        // Initialize regions spinner
        // Convert array to a list
        List<String> regionsList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.regions)));
        // Initializing an ArrayAdapter
        SpinnerAdapter<String> regionsAdapter = new SpinnerAdapter<>(
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
                                SpinnerAdapter<String> provincesAdapter = new SpinnerAdapter<>(
                                        requireContext(),
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
        ((HomeActivity) requireContext()).getSupportFragmentManager().popBackStackImmediate();
    }

    private void findPetSitter() {
        // For showing progress indicator
        ScrollView scrollView1 = this.scrollView;
        new Handler().postDelayed(() -> scrollView1.fullScroll(View.FOCUS_DOWN), 100);

        this.findButton.setEnabled(false);

        boolean isDog = dogBox.isChecked();
        boolean isCat = catBox.isChecked();
        boolean isOtherPets = otherPetsBox.isChecked();
        String regionInput = regionsSpinner.getSelectedItem().toString();
        String provinceInput;
        Object provinceInpObj = provincesSpinner.getSelectedItem();
        if (provinceInpObj == null) {
            provinceInput = null;
        } else {
            provinceInput = provinceInpObj.toString();
        }

        PetSitSearchFilters petSitSearchFilters = new PetSitSearchFilters();
        petSitSearchFilters.setDog(isDog);
        petSitSearchFilters.setCat(isCat);
        petSitSearchFilters.setOtherPets(isOtherPets);
        petSitSearchFilters.setRegion(regionInput);
        petSitSearchFilters.setProvince(provinceInput);

        presenter.findPetSitters(user, petSitSearchFilters);
    }

    @Override
    public void showProgressIndicator() {
        this.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressIndicator() {
        this.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFindResultsSuccess() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, PetSitResultsFragment.newInstance(this.user))
                .addToBackStack(PET_SITTER_SEARCH_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onFindResultsFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        this.findButton.setEnabled(true);
    }
}
