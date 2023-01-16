package com.application.mypet.services.profile.normaluser.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.application.mypet.R;
import com.application.mypet.login.view.LoginActivity;
import com.application.mypet.services.profile.view.FavoritesPetSitFragment;
import com.application.mypet.services.profile.view.PersonalAdsFragment;
import com.application.mypet.services.profile.view.PersonalInfoFragment;

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
        ((TextView) profileView.findViewById(R.id.user)).setText(this.user);
        personalInfo.setOnClickListener(new NormUserProfileFragment$$ExternalSyntheticLambda0(this));
        favorites.setOnClickListener(new NormUserProfileFragment$$ExternalSyntheticLambda1(this));
        ads.setOnClickListener(new NormUserProfileFragment$$ExternalSyntheticLambda2(this));
        ((ImageView) profileView.findViewById(R.id.logout)).setOnClickListener(new NormUserProfileFragment$$ExternalSyntheticLambda3(this));
        return profileView;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$0$com-application-mypet-services-profile-normaluser-view-NormUserProfileFragment  reason: not valid java name */
    public /* synthetic */ void m25lambda$onCreateView$0$comapplicationmypetservicesprofilenormaluserviewNormUserProfileFragment(View view) {
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, PersonalInfoFragment.newInstance(this.user)).addToBackStack(NORM_USER_PROFILE_FRAGMENT_TAG).commit();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$1$com-application-mypet-services-profile-normaluser-view-NormUserProfileFragment  reason: not valid java name */
    public /* synthetic */ void m26lambda$onCreateView$1$comapplicationmypetservicesprofilenormaluserviewNormUserProfileFragment(View view) {
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, FavoritesPetSitFragment.newInstance(this.user)).addToBackStack(NORM_USER_PROFILE_FRAGMENT_TAG).commit();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$2$com-application-mypet-services-profile-normaluser-view-NormUserProfileFragment  reason: not valid java name */
    public /* synthetic */ void m27lambda$onCreateView$2$comapplicationmypetservicesprofilenormaluserviewNormUserProfileFragment(View view) {
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, PersonalAdsFragment.newInstance()).addToBackStack(NORM_USER_PROFILE_FRAGMENT_TAG).commit();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$3$com-application-mypet-services-profile-normaluser-view-NormUserProfileFragment  reason: not valid java name */
    public /* synthetic */ void m28lambda$onCreateView$3$comapplicationmypetservicesprofilenormaluserviewNormUserProfileFragment(View view) {
        startActivity(new Intent(getActivity(), LoginActivity.class));
        requireActivity().finish();
    }
}
