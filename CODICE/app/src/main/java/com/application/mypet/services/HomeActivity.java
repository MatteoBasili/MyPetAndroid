package com.application.mypet.services;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.application.mypet.R;
import com.application.mypet.databinding.ActivityHomeBinding;
import com.application.mypet.services.profile.view.ProfileFragment;

public class HomeActivity extends AppCompatActivity {

    private SearchFragment searchFragment;
    private MapFragment mapFragment;
    private ProfileFragment profileFragment;
    private AppInfoFragment appInfoFragment;
    private final String SEARCH_FRAGMENT_TAG = "searchFragmentTag";
    private final String MAP_FRAGMENT_TAG = "mapFragmentTag";
    private final String PROFILE_FRAGMENT_TAG = "profileFragmentTag";
    private final String INFO_FRAGMENT_TAG = "infoFragmentTag";

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String user = getIntent().getStringExtra("LoggedUser");

        com.application.mypet.databinding.ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Show the main fragment
        searchFragment = new SearchFragment();
        replaceFragment(searchFragment, SEARCH_FRAGMENT_TAG);
        // set Fragment class Arguments
        //searchFragment.setArguments(bundle);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){

                case R.id.search_screen:
                    if (savedInstanceState != null) { // saved instance state, fragment may exist
                        // look up the instance that already exists by tag
                        searchFragment = (SearchFragment)
                                getSupportFragmentManager().findFragmentByTag(SEARCH_FRAGMENT_TAG);
                    } else if (searchFragment == null) {
                        // only create fragment if they haven't been instantiated already
                        searchFragment = new SearchFragment();
                        //searchFragment.setArguments(bundle);
                    }

                    assert searchFragment != null;
                    replaceFragment(searchFragment, SEARCH_FRAGMENT_TAG);
                    break;

                case R.id.map_screen:
                    if (savedInstanceState != null) { // saved instance state, fragment may exist
                        // look up the instance that already exists by tag
                        mapFragment = (MapFragment)
                                getSupportFragmentManager().findFragmentByTag(MAP_FRAGMENT_TAG);
                    } else if (mapFragment == null) {
                        // only create fragment if they haven't been instantiated already
                        mapFragment = new MapFragment();
                        //mapFragment.setArguments(bundle);

                    }

                    assert mapFragment != null;
                    replaceFragment(mapFragment, MAP_FRAGMENT_TAG);
                    break;
                case R.id.profile_screen:
                    if (savedInstanceState != null) { // saved instance state, fragment may exist
                        // look up the instance that already exists by tag
                        profileFragment = (ProfileFragment)
                                getSupportFragmentManager().findFragmentByTag(PROFILE_FRAGMENT_TAG);
                    } else if (profileFragment == null) {
                        // only create fragment if they haven't been instantiated already
                        profileFragment = ProfileFragment.newInstance(user);
                    }

                    assert profileFragment != null;
                    replaceFragment(profileFragment, PROFILE_FRAGMENT_TAG);
                    break;
                case R.id.app_info_screen:
                    if (savedInstanceState != null) { // saved instance state, fragment may exist
                        // look up the instance that already exists by tag
                        appInfoFragment = (AppInfoFragment)
                                getSupportFragmentManager().findFragmentByTag(INFO_FRAGMENT_TAG);
                    } else if (appInfoFragment == null) {
                        // only create fragment if they haven't been instantiated already
                        appInfoFragment = new AppInfoFragment();
                        //appInfoFragment.setArguments(bundle);
                    }

                    assert appInfoFragment != null;
                    replaceFragment(appInfoFragment, INFO_FRAGMENT_TAG);
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment, String fragmentTag){
        if (!fragment.isInLayout()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment, fragmentTag)
                    .commit();
        }
    }

    // Function when android system button is clicked
    @SuppressLint("InflateParams")
    @Override
    public void onBackPressed()
    {
        View confirmView = LayoutInflater.from(this).inflate(R.layout.exit_app_confirm, null);
        Button yes = confirmView.findViewById(R.id.yes_button);
        Button no = confirmView.findViewById(R.id.no_button);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(confirmView);
        AlertDialog dialog = dialogBuilder.show();

        // If Yes, exit registration
        yes.setOnClickListener(view1 -> finish());
        // If No, continue
        no.setOnClickListener(view1 -> dialog.dismiss());
    }

}