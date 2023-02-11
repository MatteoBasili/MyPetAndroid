package com.application.mypetandroid.utils.factory_method_example.regions;

import android.content.Context;

import com.application.mypetandroid.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmiliaRomagna implements ProvincesBaseList {
    private final Context context;

    public EmiliaRomagna(Context current) {
        this.context = current;
    }

    public List<String> createProvinceList() {
        return new ArrayList<>(Arrays.asList(this.context.getResources().getStringArray(R.array.provinces_emilia_romagna)));
    }
}
