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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.application.mypet.R;
import com.application.mypet.registration.data.PetSitterCaredPetsCredentials;
import com.application.mypet.services.HomeActivity;
import com.application.mypet.services.profile.ProfileContract;
import com.application.mypet.services.profile.petsitter.CaredPetsPresenter;

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
        CaredPetsPresenter presenter = new CaredPetsPresenter(this);
        this.scrollView = (ScrollView) caredPetsView.findViewById(R.id.scrollView);
        this.caredPets1 = (CheckBox) caredPetsView.findViewById(R.id.cared_pets_checkBox1);
        this.caredPets2 = (CheckBox) caredPetsView.findViewById(R.id.cared_pets_checkBox2);
        this.caredPets3 = (CheckBox) caredPetsView.findViewById(R.id.cared_pets_checkBox3);
        this.loadProgressBar = (ProgressBar) caredPetsView.findViewById(R.id.load_progressBar);
        this.saveProgressBar = (ProgressBar) caredPetsView.findViewById(R.id.save_progressBar);
        this.save = (Button) caredPetsView.findViewById(R.id.save_button);
        String username = this.user;
        new Handler().postDelayed(new CaredPetsFragment$$ExternalSyntheticLambda1(presenter, username), 10);
        ((ImageView) caredPetsView.findViewById(R.id.back_arrow)).setOnClickListener(new CaredPetsFragment$$ExternalSyntheticLambda2(this));
        this.save.setOnClickListener(new CaredPetsFragment$$ExternalSyntheticLambda3(this, presenter, username));
        return caredPetsView;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$1$com-application-mypet-services-profile-petsitter-view-CaredPetsFragment  reason: not valid java name */
    public /* synthetic */ void m29lambda$onCreateView$1$comapplicationmypetservicesprofilepetsitterviewCaredPetsFragment(View view) {
        ((HomeActivity) requireContext()).getSupportFragmentManager().popBackStackImmediate();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$2$com-application-mypet-services-profile-petsitter-view-CaredPetsFragment  reason: not valid java name */
    public /* synthetic */ void m30lambda$onCreateView$2$comapplicationmypetservicesprofilepetsitterviewCaredPetsFragment(CaredPetsPresenter presenter, String username, View view) {
        this.save.setEnabled(false);
        try {
            ((InputMethodManager) requireActivity().getSystemService("input_method")).hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 2);
        } catch (NullPointerException e) {
            Log.e("Null Pointer Error: ", e.getMessage());
        }
        presenter.saveCaredPets(username, new PetSitterCaredPetsCredentials(this.caredPets1.isChecked(), this.caredPets2.isChecked(), this.caredPets3.isChecked()));
    }

    public void showSaveProgressbar() {
        new Handler().postDelayed(new CaredPetsFragment$$ExternalSyntheticLambda0(this.scrollView), 100);
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

    public void onLoadCaredPetsSuccess(PetSitterCaredPetsCredentials petSitterCaredPetsCredentials) {
        this.caredPets1.setChecked(petSitterCaredPetsCredentials.isDog());
        this.caredPets2.setChecked(petSitterCaredPetsCredentials.isCat());
        this.caredPets3.setChecked(petSitterCaredPetsCredentials.isOtherPets());
        this.caredPets1.setClickable(true);
        this.caredPets2.setClickable(true);
        this.caredPets3.setClickable(true);
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
