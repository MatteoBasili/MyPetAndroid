package com.application.mypet.services.search.view;

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
import com.application.mypet.services.search.PetSitSearchPresenter;
import com.application.mypet.services.search.SearchContract;
import com.application.mypet.services.search.data.PetSitSearchFilters;
import com.application.mypet.services.search.data.PetSitterResultList;
import com.application.mypet.services.search.data.PetSitterResultsAdapter;

public class PetSitResultsFragment extends Fragment implements SearchContract.PetSitterSearchView {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView noResText;
    private ListView petSitListView;
    private PetSitSearchFilters petSitSearchFilters;
    private ProgressBar progressBar;
    private String user;

    public static PetSitResultsFragment newInstance(PetSitSearchFilters petSitSearchFilters2, String user2) {
        PetSitResultsFragment fragment = new PetSitResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, petSitSearchFilters2);
        args.putString(ARG_PARAM2, user2);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.petSitSearchFilters = (PetSitSearchFilters) getArguments().getSerializable(ARG_PARAM1);
            this.user = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View petSitResultsView = inflater.inflate(R.layout.fragment_pet_sitter_results, container, false);
        PetSitSearchPresenter petSitSearchPresenter = new PetSitSearchPresenter((SearchContract.PetSitterSearchView) this);
        this.petSitListView = (ListView) petSitResultsView.findViewById(R.id.petSitListView);
        this.noResText = (TextView) petSitResultsView.findViewById(R.id.textNoResults);
        this.progressBar = (ProgressBar) petSitResultsView.findViewById(R.id.load_progressBar);
        new Handler().postDelayed(new PetSitResultsFragment$$ExternalSyntheticLambda0(this, petSitSearchPresenter, this.petSitSearchFilters), 500);
        ((ImageView) petSitResultsView.findViewById(R.id.back_arrow)).setOnClickListener(new PetSitResultsFragment$$ExternalSyntheticLambda1(this));
        return petSitResultsView;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$0$com-application-mypet-services-search-view-PetSitResultsFragment  reason: not valid java name */
    public /* synthetic */ void m6lambda$onCreateView$0$comapplicationmypetservicessearchviewPetSitResultsFragment(PetSitSearchPresenter petSitSearchPresenter, PetSitSearchFilters searchInput) {
        petSitSearchPresenter.loadResults(this.user, searchInput);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$1$com-application-mypet-services-search-view-PetSitResultsFragment  reason: not valid java name */
    public /* synthetic */ void m7lambda$onCreateView$1$comapplicationmypetservicessearchviewPetSitResultsFragment(View view) {
        ((HomeActivity) requireContext()).getSupportFragmentManager().popBackStackImmediate();
    }

    public void hideProgressbar() {
        this.progressBar.setVisibility(8);
    }

    public void onLoadResultsSuccess(PetSitterResultList petSitterResultList) {
        try {
            if (petSitterResultList.getResNumber() != 0) {
                this.petSitListView.setAdapter(new PetSitterResultsAdapter((AppCompatActivity) getContext(), this.user, petSitterResultList.getImages(), petSitterResultList.getUsernames(), petSitterResultList.getPlaces(), petSitterResultList.getNumLikes(), petSitterResultList.getNumDislikes(), petSitterResultList.getFavorites()));
                return;
            }
            throw new NoResultsFoundException("No results found");
        } catch (NullPointerException e) {
        } catch (NoResultsFoundException e2) {
            this.noResText.setVisibility(0);
            this.noResText.setText(e2.getMessage());
        }
    }

    public void onLoadResultsFailed(String message) {
        Toast.makeText(getContext(), message, 0).show();
    }
}
