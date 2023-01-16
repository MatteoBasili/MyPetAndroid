package com.application.mypet.services.profile.view;

import android.os.Bundle;
import android.os.Handler;
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
import com.application.mypet.R;
import com.application.mypet.exceptions.NoResultsFoundException;
import com.application.mypet.services.HomeActivity;
import com.application.mypet.services.search.FavoritesContract;
import com.application.mypet.services.search.FavoritesPresenter;
import com.application.mypet.services.search.data.PetSitterResultList;
import com.application.mypet.services.search.data.PetSitterResultsAdapter;

public class FavoritesPetSitFragment extends Fragment implements FavoritesContract.LoadPetSitFavView {
    private static final String ARG_PARAM1 = "param1";
    private static final String FAV_PET_SIT_FRAGMENT_TAG = "favPetSitFragmentTag";
    private ProgressBar loadProgressBar;
    private TextView noFavText;
    private ListView petSitListView;
    private String user;

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
        FavoritesPresenter presenter = new FavoritesPresenter((FavoritesContract.LoadPetSitFavView) this);
        this.petSitListView = (ListView) petSitFavView.findViewById(R.id.petSitListView);
        this.loadProgressBar = (ProgressBar) petSitFavView.findViewById(R.id.load_progressBar);
        this.noFavText = (TextView) petSitFavView.findViewById(R.id.textNoFav);
        new Handler().postDelayed(new FavoritesPetSitFragment$$ExternalSyntheticLambda0(this, presenter), 500);
        ((ImageView) petSitFavView.findViewById(R.id.favorite_pets)).setOnClickListener(new FavoritesPetSitFragment$$ExternalSyntheticLambda1(this));
        ((ImageView) petSitFavView.findViewById(R.id.back_arrow)).setOnClickListener(new FavoritesPetSitFragment$$ExternalSyntheticLambda2(this));
        return petSitFavView;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$0$com-application-mypet-services-profile-view-FavoritesPetSitFragment  reason: not valid java name */
    public /* synthetic */ void m56lambda$onCreateView$0$comapplicationmypetservicesprofileviewFavoritesPetSitFragment(FavoritesPresenter presenter) {
        presenter.loadFav(this.user);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$1$com-application-mypet-services-profile-view-FavoritesPetSitFragment  reason: not valid java name */
    public /* synthetic */ void m57lambda$onCreateView$1$comapplicationmypetservicesprofileviewFavoritesPetSitFragment(View view) {
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, FavoritesPetFragment.newInstance()).addToBackStack(FAV_PET_SIT_FRAGMENT_TAG).commit();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$2$com-application-mypet-services-profile-view-FavoritesPetSitFragment  reason: not valid java name */
    public /* synthetic */ void m58lambda$onCreateView$2$comapplicationmypetservicesprofileviewFavoritesPetSitFragment(View view) {
        ((HomeActivity) requireContext()).getSupportFragmentManager().popBackStackImmediate();
    }

    public void hideLoadProgressbar() {
        this.loadProgressBar.setVisibility(8);
    }

    public void onLoadFavSuccess(PetSitterResultList petSitterResultList) {
        try {
            if (!petSitterResultList.getUsernames().isEmpty()) {
                this.petSitListView.setAdapter(new PetSitterResultsAdapter((AppCompatActivity) getContext(), this.user, petSitterResultList.getImages(), petSitterResultList.getUsernames(), petSitterResultList.getPlaces(), petSitterResultList.getNumLikes(), petSitterResultList.getNumDislikes(), petSitterResultList.getFavorites()));
                return;
            }
            throw new NoResultsFoundException("No favorites");
        } catch (NullPointerException e) {
        } catch (NoResultsFoundException e2) {
            this.noFavText.setVisibility(0);
            this.noFavText.setText(e2.getMessage());
        }
    }

    public void onLoadFavFailed(String message) {
        Toast.makeText(getContext(), message, 0).show();
    }
}
