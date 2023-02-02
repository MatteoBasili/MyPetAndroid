package com.application.mypetandroid.services.profile.view;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.application.mypetandroid.R;
import com.application.mypetandroid.registration.data.PetSitCaredPets;
import com.application.mypetandroid.services.HomeActivity;
import com.application.mypetandroid.services.profile.CaredPetsPresenter;
import com.application.mypetandroid.services.profile.ProfileContract;
import com.application.mypetandroid.utils.singleton_examples.KeyboardSingletonClass;

public class CaredPetsFragment extends Fragment implements ProfileContract.CaredPetsView {
    private static final String ARG_PARAM1 = "param1";
    private CheckBox caredPets1;
    private CheckBox caredPets2;
    private CheckBox caredPets3;
    private ProgressBar loadProgressBar;
    private Button save;
    private ProgressBar saveProgressBar;
    private ScrollView scrollView;
    private String user;
    private CaredPetsPresenter presenter;

    public static CaredPetsFragment newInstance(String user2) {
        CaredPetsFragment fragment = new CaredPetsFragment();
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
        View caredPetsView = inflater.inflate(R.layout.fragment_cared_pets, container, false);

        ImageView backButton = caredPetsView.findViewById(R.id.back_arrow);
        save = caredPetsView.findViewById(R.id.save_button);
        scrollView = caredPetsView.findViewById(R.id.scrollView);
        caredPets1 = caredPetsView.findViewById(R.id.cared_pets_checkBox1);
        caredPets2 = caredPetsView.findViewById(R.id.cared_pets_checkBox2);
        caredPets3 = caredPetsView.findViewById(R.id.cared_pets_checkBox3);
        loadProgressBar = caredPetsView.findViewById(R.id.load_progressBar);
        saveProgressBar = caredPetsView.findViewById(R.id.save_progressBar);

        presenter = new CaredPetsPresenter(this);

        save.setOnClickListener(v -> saveCaredPets());
        backButton.setOnClickListener(v -> back());

        loadCaredPets();

        return caredPetsView;
    }

    private void loadCaredPets() {
        presenter.loadCaredPets(user);
    }

    private void back() {
        ((HomeActivity) requireContext()).getSupportFragmentManager().popBackStackImmediate();
    }

    private void hideKeyboard() {
        KeyboardSingletonClass keyboardSingletonClass = KeyboardSingletonClass.getSingletonInstance();
        keyboardSingletonClass.hide(requireActivity());
    }

    private void saveCaredPets() {
        hideKeyboard();
        new Handler().postDelayed(() -> {
            scrollView.fullScroll(View.FOCUS_DOWN);   // For showing progress indicator
        }, 100);
        this.save.setEnabled(false);

        boolean isDog = caredPets1.isChecked();
        boolean isCat = caredPets2.isChecked();
        boolean isOtherPets = caredPets3.isChecked();

        PetSitCaredPets petSitCaredPets = new PetSitCaredPets();
        petSitCaredPets.setDog(isDog);
        petSitCaredPets.setCat(isCat);
        petSitCaredPets.setOtherPets(isOtherPets);

        presenter.saveCaredPets(user, petSitCaredPets);
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
    public void onStorePetsSuccess() {
        Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
        this.save.setEnabled(true);
    }

    @Override
    public void onLoadPetsSuccess(PetSitCaredPets petSitCaredPets) {
        // Load cared pets
        this.caredPets1.setChecked(petSitCaredPets.isDog());
        this.caredPets2.setChecked(petSitCaredPets.isCat());
        this.caredPets3.setChecked(petSitCaredPets.isOtherPets());

        // Unlock resources
        this.caredPets1.setClickable(true);
        this.caredPets2.setClickable(true);
        this.caredPets3.setClickable(true);
        this.save.setEnabled(true);
    }

    @Override
    public void onStorePetsFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        this.save.setEnabled(true);
    }

    @Override
    public void onLoadPetsFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
