package com.application.mypetandroid.services.profile.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.application.mypetandroid.R;
import com.application.mypetandroid.services.HomeActivity;
import com.application.mypetandroid.services.profile.PersonalFavPetSitPresenter;
import com.application.mypetandroid.services.profile.ProfileContract;
import com.application.mypetandroid.services.search.view.PetSitterResultsAdapter;
import com.application.mypetandroid.utils.singleton_examples.PetSitterResultsSingletonClass;

public class FavoritesPetSitFragment extends Fragment implements ProfileContract.LoadFavPetView {
    private static final String ARG_PARAM1 = "param1";
    private static final String FAV_PET_SIT_FRAGMENT_TAG = "favPetSitFragmentTag";
    private ProgressBar loadProgressBar;
    private TextView noFavText;
    private ListView petSitListView;
    private String user;
    private PersonalFavPetSitPresenter personalFavPetSitPresenter;

    public static FavoritesPetSitFragment newInstance(String user2) {
        FavoritesPetSitFragment fragment = new FavoritesPetSitFragment();
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
        View petSitFavView = inflater.inflate(R.layout.fragment_favorites_pet_sitters, container, false);

        noFavText = petSitFavView.findViewById(R.id.textNoFav);
        ImageView favPetsIcon = petSitFavView.findViewById(R.id.favorite_pets);
        ImageView backButton = petSitFavView.findViewById(R.id.back_arrow);
        petSitListView = petSitFavView.findViewById(R.id.petSitListView);
        loadProgressBar = petSitFavView.findViewById(R.id.load_progressBar);

        personalFavPetSitPresenter = new PersonalFavPetSitPresenter(this);

        favPetsIcon.setOnClickListener(v -> showFavoritePets());
        backButton.setOnClickListener(v -> back());

        loadFavorites();

        return petSitFavView;
    }

    private void loadFavorites() {
        personalFavPetSitPresenter.loadFavorites(user);
    }

    private void showFavoritePets() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, FavoritesPetFragment.newInstance())
                .addToBackStack(FAV_PET_SIT_FRAGMENT_TAG).commit();
    }

    private void back() {
        ((HomeActivity) requireContext()).getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void hideProgressIndicator() {
        this.loadProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoadFavoritesSuccess(PetSitterResultsSingletonClass results) {
        if (results.getResultsNumber() != 0) {
            this.petSitListView.setAdapter(new PetSitterResultsAdapter((AppCompatActivity) getContext(), this.user,
                    results.getUsernames()));
        } else {
            this.noFavText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoadFavoritesFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
