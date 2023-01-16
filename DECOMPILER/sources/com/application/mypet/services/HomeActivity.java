package com.application.mypet.services;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.application.mypet.R;
import com.application.mypet.databinding.ActivityHomeBinding;
import com.application.mypet.services.info.view.AppInfoFragment;
import com.application.mypet.services.map.view.MapFragment;
import com.application.mypet.services.profile.normaluser.view.NormUserProfileFragment;
import com.application.mypet.services.profile.petsitter.view.PetSitProfileFragment;
import com.application.mypet.services.search.view.SearchFragment;

public class HomeActivity extends AppCompatActivity {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String INFO_FRAGMENT_TAG = "infoFragmentTag";
    private static final String MAP_FRAGMENT_TAG = "mapFragmentTag";
    private static final String NORM_USER_PROFILE_FRAGMENT_TAG = "NormUserProfileFragmentTag";
    private static final String PET_SIT_PROFILE_FRAGMENT_TAG = "petSitProfileFragmentTag";
    private static final String SEARCH_FRAGMENT_TAG = "searchFragmentTag";
    private AppInfoFragment appInfoFragment;
    private MapFragment mapFragment;
    private Fragment profileFragment;
    private int role;
    private SearchFragment searchFragment;
    private String user;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.user = getIntent().getStringExtra("LoggedUser");
        this.role = getIntent().getIntExtra("UserRole", 1);
        ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView((View) binding.getRoot());
        SearchFragment newInstance = SearchFragment.newInstance(this.user);
        this.searchFragment = newInstance;
        replaceFragment(newInstance, SEARCH_FRAGMENT_TAG);
        binding.bottomNavigationView.setOnItemSelectedListener(new HomeActivity$$ExternalSyntheticLambda0(this, savedInstanceState));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$0$com-application-mypet-services-HomeActivity  reason: not valid java name */
    public /* synthetic */ boolean m76lambda$onCreate$0$comapplicationmypetservicesHomeActivity(Bundle savedInstanceState, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_info_screen /*2131361878*/:
                AppInfoFragment appInfoFragment2 = new AppInfoFragment();
                this.appInfoFragment = appInfoFragment2;
                replaceFragment(appInfoFragment2, INFO_FRAGMENT_TAG);
                break;
            case R.id.map_screen /*2131362105*/:
                MapFragment mapFragment2 = new MapFragment();
                this.mapFragment = mapFragment2;
                replaceFragment(mapFragment2, MAP_FRAGMENT_TAG);
                break;
            case R.id.profile_screen /*2131362225*/:
                Fragment selectProfileFragment = selectProfileFragment(this.role, savedInstanceState);
                this.profileFragment = selectProfileFragment;
                if (this.role != 1) {
                    replaceFragment(selectProfileFragment, PET_SIT_PROFILE_FRAGMENT_TAG);
                    break;
                } else {
                    replaceFragment(selectProfileFragment, NORM_USER_PROFILE_FRAGMENT_TAG);
                    break;
                }
            case R.id.search_screen /*2131362270*/:
                if (savedInstanceState != null) {
                    this.searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag(SEARCH_FRAGMENT_TAG);
                } else if (this.searchFragment == null) {
                    this.searchFragment = new SearchFragment();
                }
                SearchFragment searchFragment2 = this.searchFragment;
                if (searchFragment2 != null) {
                    replaceFragment(searchFragment2, SEARCH_FRAGMENT_TAG);
                    break;
                } else {
                    throw new AssertionError();
                }
        }
        return true;
    }

    private Fragment selectProfileFragment(int role2, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (role2 == 1) {
                return getSupportFragmentManager().findFragmentByTag(NORM_USER_PROFILE_FRAGMENT_TAG);
            }
            return getSupportFragmentManager().findFragmentByTag(PET_SIT_PROFILE_FRAGMENT_TAG);
        } else if (role2 == 1) {
            return NormUserProfileFragment.newInstance(this.user);
        } else {
            return PetSitProfileFragment.newInstance(this.user);
        }
    }

    private void replaceFragment(Fragment fragment, String fragmentTag) {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }
        if (fragment.isInLayout() == 0) {
            getSupportFragmentManager().beginTransaction().replace((int) R.id.frame_layout, fragment, fragmentTag).commit();
        }
    }

    public void onBackPressed() {
        if (this.mapFragment == null) {
            this.mapFragment = new MapFragment();
        }
        if (this.profileFragment == null) {
            if (this.role == 1) {
                this.profileFragment = new NormUserProfileFragment();
            } else {
                this.profileFragment = new PetSitProfileFragment();
            }
        }
        if (this.appInfoFragment == null) {
            this.appInfoFragment = new AppInfoFragment();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() <= 0 || this.searchFragment.isVisible() || this.mapFragment.isVisible() || this.profileFragment.isVisible() || this.appInfoFragment.isVisible()) {
            View confirmView = LayoutInflater.from(this).inflate(R.layout.exit_app_confirm, (ViewGroup) null);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setView(confirmView);
            AlertDialog dialog = dialogBuilder.show();
            ((Button) confirmView.findViewById(R.id.yes_button)).setOnClickListener(new HomeActivity$$ExternalSyntheticLambda1(this));
            ((Button) confirmView.findViewById(R.id.no_button)).setOnClickListener(new HomeActivity$$ExternalSyntheticLambda2(dialog));
            return;
        }
        fragmentManager.popBackStackImmediate();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onBackPressed$1$com-application-mypet-services-HomeActivity  reason: not valid java name */
    public /* synthetic */ void m75lambda$onBackPressed$1$comapplicationmypetservicesHomeActivity(View view1) {
        finish();
    }
}
