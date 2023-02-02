package com.application.mypetandroid.services.profile.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.application.mypetandroid.R;
import com.application.mypetandroid.login.view.LoginActivity;

public class NormUserProfileFragment extends Fragment {
    private static final String ARG_PARAM1 = "User";
    private static final String NORM_USER_PROFILE_FRAGMENT_TAG = "normUserProfileFragmentTag";
    private String user;

    public static NormUserProfileFragment newInstance(String user2) {
        NormUserProfileFragment fragment = new NormUserProfileFragment();
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
        View profileView = inflater.inflate(R.layout.fragment_norm_user_profile, container, false);

        View personalInfo = profileView.findViewById(R.id.personal_info_view);
        View favorites = profileView.findViewById(R.id.favorites_view);
        View ads = profileView.findViewById(R.id.ads_view);
        ImageView logoutIcon = profileView.findViewById(R.id.logout);
        TextView usernameView = profileView.findViewById(R.id.user);

        // Set the user username
        usernameView.setText(this.user);

        personalInfo.setOnClickListener(v -> showPersonalInformations());
        favorites.setOnClickListener(v -> showFavorites());
        ads.setOnClickListener(v -> showUserAds());
        logoutIcon.setOnClickListener(v -> logout());

        return profileView;
    }

    private void showPersonalInformations() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, PersonalInfoFragment.newInstance(this.user))
                .addToBackStack(NORM_USER_PROFILE_FRAGMENT_TAG)
                .commit();
    }

    private void showFavorites() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, FavoritesPetSitFragment.newInstance(this.user))
                .addToBackStack(NORM_USER_PROFILE_FRAGMENT_TAG)
                .commit();
    }

    private void showUserAds() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, PersonalAdsFragment.newInstance())
                .addToBackStack(NORM_USER_PROFILE_FRAGMENT_TAG)
                .commit();
    }

    private void logout() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
        requireActivity().finish();
    }
}
