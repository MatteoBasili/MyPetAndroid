package com.application.mypet.services.profile.view;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.application.mypet.R;
import com.application.mypet.services.HomeActivity;

public class FavoritesPetFragment extends Fragment {
    private static final String FAV_PET_SIT_FRAGMENT_TAG = "favPetSitFragmentTag";

    public static FavoritesPetFragment newInstance() {
        FavoritesPetFragment fragment = new FavoritesPetFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View petFavView = inflater.inflate(R.layout.fragment_favorites_pet, container, false);
        new Handler().postDelayed(new FavoritesPetFragment$$ExternalSyntheticLambda0((ProgressBar) petFavView.findViewById(R.id.load_progressBar), (TextView) petFavView.findViewById(R.id.textNoFav)), 500);
        ((ImageView) petFavView.findViewById(R.id.favorite_pet_sitters)).setOnClickListener(new FavoritesPetFragment$$ExternalSyntheticLambda1(this));
        ((ImageView) petFavView.findViewById(R.id.back_arrow)).setOnClickListener(new FavoritesPetFragment$$ExternalSyntheticLambda2(this));
        return petFavView;
    }

    static /* synthetic */ void lambda$onCreateView$0(ProgressBar progressBar, TextView noFavText) {
        progressBar.setVisibility(8);
        noFavText.setVisibility(0);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$1$com-application-mypet-services-profile-view-FavoritesPetFragment  reason: not valid java name */
    public /* synthetic */ void m54lambda$onCreateView$1$comapplicationmypetservicesprofileviewFavoritesPetFragment(View view) {
        ((HomeActivity) requireContext()).getSupportFragmentManager().popBackStackImmediate();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$2$com-application-mypet-services-profile-view-FavoritesPetFragment  reason: not valid java name */
    public /* synthetic */ void m55lambda$onCreateView$2$comapplicationmypetservicesprofileviewFavoritesPetFragment(View view) {
        FragmentManager fragmentManager = ((HomeActivity) requireContext()).getSupportFragmentManager();
        fragmentManager.popBackStack(FAV_PET_SIT_FRAGMENT_TAG, 1);
        fragmentManager.popBackStack();
    }
}
