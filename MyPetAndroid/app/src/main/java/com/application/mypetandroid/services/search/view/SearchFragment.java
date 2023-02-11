package com.application.mypetandroid.services.search.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.application.mypetandroid.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SearchFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String SEARCH_FRAGMENT_TAG = "searchFragmentTag";
    private String user;

    public static SearchFragment newInstance(String user2) {
        SearchFragment fragment = new SearchFragment();
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
        View searchView = inflater.inflate(R.layout.fragment_search, container, false);

        View petSittersSearch = searchView.findViewById(R.id.find_pet_sitter_view);
        View petSearch = searchView.findViewById(R.id.find_pet_view);
        View veterinarians = searchView.findViewById(R.id.veterinarians_view);
        View shops = searchView.findViewById(R.id.shops_view);

        petSittersSearch.setOnClickListener(v -> showPetSitterSearchMenu());
        petSearch.setOnClickListener(v -> showErrorMessage());
        veterinarians.setOnClickListener(v -> showMap());
        shops.setOnClickListener(v -> showMap());

        return searchView;
    }

    private void showPetSitterSearchMenu() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, StartPetSitSearchFragment.newInstance(user))
                .addToBackStack(SEARCH_FRAGMENT_TAG).commit();
    }

    private void showErrorMessage() {
        Toast.makeText(getContext(), "Sorry, the service is currently unavailable", Toast.LENGTH_SHORT).show();
    }

    private void showMap() {
        ((BottomNavigationView) requireActivity().findViewById(R.id.bottomNavigationView)).setSelectedItemId(R.id.map_screen);
    }

}
