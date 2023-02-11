package com.application.mypetandroid.services.search.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.application.mypetandroid.R;
import com.application.mypetandroid.services.HomeActivity;
import com.application.mypetandroid.utils.singleton_examples.PetSitterResultsSingletonClass;

public class PetSitResultsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private TextView noResText;
    private ListView petSitListView;
    private String user;
    private PetSitterResultsSingletonClass results;

    public static PetSitResultsFragment newInstance(String user2) {
        PetSitResultsFragment fragment = new PetSitResultsFragment();
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
        View petSitResultsView = inflater.inflate(R.layout.fragment_pet_sitter_results, container, false);

        ImageView backIcon = petSitResultsView.findViewById(R.id.back_arrow);
        petSitListView = petSitResultsView.findViewById(R.id.petSitListView);
        noResText = petSitResultsView.findViewById(R.id.textNoResults);

        results = PetSitterResultsSingletonClass.getSingletonInstance();

        backIcon.setOnClickListener(v -> back());

        loadResults();

        return petSitResultsView;
    }

    private void back() {
        ((HomeActivity) requireContext()).getSupportFragmentManager().popBackStackImmediate();
    }

    private void loadResults() {
        if (results.getResultsNumber() != 0) {
            this.petSitListView.setAdapter(new PetSitterResultsAdapter((AppCompatActivity) getContext(), this.user,
                    results.getUsernames()));
        } else {
            this.noResText.setVisibility(View.VISIBLE);
        }
    }

}
