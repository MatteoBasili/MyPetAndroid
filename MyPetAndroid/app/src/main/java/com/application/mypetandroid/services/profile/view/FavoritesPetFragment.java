package com.application.mypetandroid.services.profile.view;

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

import com.application.mypetandroid.R;
import com.application.mypetandroid.services.HomeActivity;

public class FavoritesPetFragment extends Fragment {
    private static final String FAV_PET_SIT_FRAGMENT_TAG = "favPetSitFragmentTag";

    private ProgressBar progressBar;
    private TextView noFavoritesText;

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

        progressBar = petFavView.findViewById(R.id.load_progressBar);
        noFavoritesText = petFavView.findViewById(R.id.textNoFav);
        ImageView backButton = petFavView.findViewById(R.id.back_arrow);
        ImageView favoritesPetSitters = petFavView.findViewById(R.id.favorite_pet_sitters);

        backButton.setOnClickListener(v -> back());
        favoritesPetSitters.setOnClickListener(v -> showFavoritesPetSitters());

        // Display no results
        new Handler().postDelayed(this::loadFavorites, 500);

        return petFavView;
    }

    private void loadFavorites() {
        progressBar.setVisibility(View.GONE);
        noFavoritesText.setVisibility(View.VISIBLE);
    }

    private void showFavoritesPetSitters() {
        ((HomeActivity) requireContext()).getSupportFragmentManager().popBackStackImmediate();
    }

    private void back() {
        FragmentManager fragmentManager = ((HomeActivity) requireContext()).getSupportFragmentManager();
        fragmentManager.popBackStack(FAV_PET_SIT_FRAGMENT_TAG, 1);
        fragmentManager.popBackStack();
    }
}
