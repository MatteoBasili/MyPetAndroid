package com.application.mypet.services.info.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.application.mypet.R;

public class AppInfoFragment extends Fragment {
    public static AppInfoFragment newInstance() {
        AppInfoFragment fragment = new AppInfoFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_info, container, false);
    }
}
