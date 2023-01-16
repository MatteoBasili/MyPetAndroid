package com.application.mypet.utils.factory;

import android.content.Context;
import com.application.mypet.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalabriaProvinces implements ProvincesBaseList {
    private final Context context;

    public CalabriaProvinces(Context current) {
        this.context = current;
    }

    public List<String> createProvinceList() {
        return new ArrayList(Arrays.asList(this.context.getResources().getStringArray(R.array.provinces_calabria)));
    }
}
