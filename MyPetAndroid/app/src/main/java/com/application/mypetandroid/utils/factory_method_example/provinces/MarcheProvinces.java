package com.application.mypetandroid.utils.factory_method_example.provinces;

import android.content.Context;

import com.application.mypetandroid.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MarcheProvinces implements ProvincesBaseList {
    private final Context context;

    public MarcheProvinces(Context current) {
        this.context = current;
    }

    public List<String> createProvinceList() {
        return new ArrayList<>(Arrays.asList(this.context.getResources().getStringArray(R.array.provinces_marche)));
    }
}
