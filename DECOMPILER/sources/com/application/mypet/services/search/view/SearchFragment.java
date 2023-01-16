package com.application.mypet.services.search.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.application.mypet.R;
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
        petSittersSearch.setOnClickListener(new SearchFragment$$ExternalSyntheticLambda0(this));
        petSearch.setOnClickListener(new SearchFragment$$ExternalSyntheticLambda1(this));
        veterinarians.setOnClickListener(new SearchFragment$$ExternalSyntheticLambda2(this));
        shops.setOnClickListener(new SearchFragment$$ExternalSyntheticLambda3(this));
        return searchView;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$0$com-application-mypet-services-search-view-SearchFragment  reason: not valid java name */
    public /* synthetic */ void m15lambda$onCreateView$0$comapplicationmypetservicessearchviewSearchFragment(View view) {
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, StartPetSitSearchFragment.newInstance(getUser())).addToBackStack(SEARCH_FRAGMENT_TAG).commit();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$1$com-application-mypet-services-search-view-SearchFragment  reason: not valid java name */
    public /* synthetic */ void m16lambda$onCreateView$1$comapplicationmypetservicessearchviewSearchFragment(View view) {
        Toast.makeText(getContext(), "The service is currently unavailable", 0).show();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$2$com-application-mypet-services-search-view-SearchFragment  reason: not valid java name */
    public /* synthetic */ void m17lambda$onCreateView$2$comapplicationmypetservicessearchviewSearchFragment(View view) {
        ((BottomNavigationView) requireActivity().findViewById(R.id.bottomNavigationView)).setSelectedItemId(R.id.map_screen);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$3$com-application-mypet-services-search-view-SearchFragment  reason: not valid java name */
    public /* synthetic */ void m18lambda$onCreateView$3$comapplicationmypetservicessearchviewSearchFragment(View view) {
        ((BottomNavigationView) requireActivity().findViewById(R.id.bottomNavigationView)).setSelectedItemId(R.id.map_screen);
    }

    public String getUser() {
        return this.user;
    }
}
