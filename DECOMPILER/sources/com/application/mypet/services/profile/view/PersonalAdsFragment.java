package com.application.mypet.services.profile.view;

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
import com.application.mypet.R;
import com.application.mypet.services.HomeActivity;

public class PersonalAdsFragment extends Fragment {
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
        new Handler().postDelayed(new PersonalAdsFragment$$ExternalSyntheticLambda0((ProgressBar) personalAdsView.findViewById(R.id.load_progressBar), (TextView) personalAdsView.findViewById(R.id.textNoAds)), 500);
        ((ImageView) personalAdsView.findViewById(R.id.back_arrow)).setOnClickListener(new PersonalAdsFragment$$ExternalSyntheticLambda1(this));
        ((ImageView) personalAdsView.findViewById(R.id.new_ad)).setOnClickListener(new PersonalAdsFragment$$ExternalSyntheticLambda2(this));
        return personalAdsView;
    }

    static /* synthetic */ void lambda$onCreateView$0(ProgressBar progressBar, TextView noAdsText) {
        progressBar.setVisibility(8);
        noAdsText.setVisibility(0);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$1$com-application-mypet-services-profile-view-PersonalAdsFragment  reason: not valid java name */
    public /* synthetic */ void m59lambda$onCreateView$1$comapplicationmypetservicesprofileviewPersonalAdsFragment(View view) {
        ((HomeActivity) requireContext()).getSupportFragmentManager().popBackStackImmediate();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$2$com-application-mypet-services-profile-view-PersonalAdsFragment  reason: not valid java name */
    public /* synthetic */ void m60lambda$onCreateView$2$comapplicationmypetservicesprofileviewPersonalAdsFragment(View view) {
        Toast.makeText(getContext(), "Sorry, but the service is currently unavailable", 0).show();
    }
}
