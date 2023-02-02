package com.application.mypetandroid.services.profile.view;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.application.mypetandroid.R;
import com.application.mypetandroid.services.HomeActivity;

public class PersonalAdsFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView noAdsText;

    public static PersonalAdsFragment newInstance() {
        PersonalAdsFragment fragment = new PersonalAdsFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View personalAdsView = inflater.inflate(R.layout.fragment_personal_ads, container, false);

        ImageView backButton = personalAdsView.findViewById(R.id.back_arrow);
        ImageView newAdButton = personalAdsView.findViewById(R.id.new_ad);
        progressBar = personalAdsView.findViewById(R.id.load_progressBar);
        noAdsText = personalAdsView.findViewById(R.id.textNoAds);

        backButton.setOnClickListener(v -> back());
        newAdButton.setOnClickListener(v -> showErrorMessage());

        // Display no results
        new Handler().postDelayed(this::loadAds, 500);

        return personalAdsView;
    }

    private void loadAds() {
        progressBar.setVisibility(View.GONE);
        noAdsText.setVisibility(View.VISIBLE);
    }

    private void back() {
        ((HomeActivity) requireContext()).getSupportFragmentManager().popBackStackImmediate();
    }

    private void showErrorMessage() {
        Toast.makeText(getContext(), "Sorry, the service is currently unavailable", Toast.LENGTH_SHORT).show();
    }
}
