package com.application.mypetandroid.services;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.application.mypetandroid.R;
import com.application.mypetandroid.services.app_info.AppInfoFragment;
import com.application.mypetandroid.services.map.MapFragment;
import com.application.mypetandroid.services.profile.view.NormUserProfileFragment;
import com.application.mypetandroid.services.profile.view.PetSitProfileFragment;
import com.application.mypetandroid.services.search.view.SearchFragment;
import com.application.mypetandroid.utils.singleton_examples.UserSingletonClass;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private static final String INFO_FRAGMENT_TAG = "infoFragmentTag";
    private static final String MAP_FRAGMENT_TAG = "mapFragmentTag";
    private static final String NORM_USER_PROFILE_FRAGMENT_TAG = "NormUserProfileFragmentTag";
    private static final String PET_SIT_PROFILE_FRAGMENT_TAG = "petSitProfileFragmentTag";
    private static final String SEARCH_FRAGMENT_TAG = "searchFragmentTag";

    private SearchFragment searchFragment;
    private MapFragment mapFragment;
    private AppInfoFragment appInfoFragment;
    private Fragment profileFragment;
    private UserSingletonClass user;

    @SuppressLint("NonConstantResourceId")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        user = UserSingletonClass.getSingletonInstance();

        searchFragment = SearchFragment.newInstance(user.getUsername());
        mapFragment = MapFragment.newInstance();
        appInfoFragment = AppInfoFragment.newInstance();
        initializeProfileFragment(user.getRole());

        // Show main screen
        replaceFragment(searchFragment, SEARCH_FRAGMENT_TAG);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.search_screen -> {
                    replaceFragment(searchFragment, SEARCH_FRAGMENT_TAG);
                    return true;
                }
                case R.id.map_screen -> {
                    replaceFragment(mapFragment, MAP_FRAGMENT_TAG);
                    return true;
                }
                case R.id.profile_screen -> {
                    if (user.getRole() == 1) {
                        replaceFragment(profileFragment, NORM_USER_PROFILE_FRAGMENT_TAG);
                    } else {
                        replaceFragment(profileFragment, PET_SIT_PROFILE_FRAGMENT_TAG);
                    }
                    return true;
                }
                case R.id.app_info_screen -> {
                    replaceFragment(appInfoFragment, INFO_FRAGMENT_TAG);
                    return true;
                }
            }
            return false;
        });

    }

    private void initializeProfileFragment(int role) {
        if (role == 1) {
            profileFragment = NormUserProfileFragment.newInstance(user.getUsername());
        } else {
            profileFragment = PetSitProfileFragment.newInstance(user.getUsername());
        }
    }

    private void replaceFragment(Fragment fragment, String fragmentTag) {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }
        if (!fragment.isInLayout()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment, fragmentTag).commit();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() <= 0 || this.searchFragment.isVisible() || this.mapFragment.isVisible() || this.profileFragment.isVisible() || this.appInfoFragment.isVisible()) {
            // Exit the app
            View confirmView = LayoutInflater.from(this).inflate(R.layout.exit_app_confirm, null);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setView(confirmView);
            AlertDialog dialog = dialogBuilder.show();
            (confirmView.findViewById(R.id.yes_button)).setOnClickListener(v -> finish());
            (confirmView.findViewById(R.id.no_button)).setOnClickListener(v -> dialog.dismiss());
            return;
        }
        fragmentManager.popBackStackImmediate();
    }

}
