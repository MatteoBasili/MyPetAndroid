package com.application.mypetandroid.utils.factory_method_example;

import android.content.Context;

import com.application.mypetandroid.utils.factory_method_example.provinces.*;

public class ProvincesFactory {
    private final Context context;

    public ProvincesFactory(Context current) {
        this.context = current;
    }

    public ProvincesBaseList createProvinceBaseList(int type) throws Exception {
        return switch (type) {
            case 1 -> new AbruzzoProvinces(this.context);
            case 2 -> new BasilicataProvinces(this.context);
            case 3 -> new CalabriaProvinces(this.context);
            case 4 -> new CampaniaProvinces(this.context);
            case 5 -> new EmiliaRomagnaProvinces(this.context);
            case 6 -> new FriuliVeneziaGiuliaProvinces(this.context);
            case 7 -> new LazioProvinces(this.context);
            case 8 -> new LiguriaProvinces(this.context);
            case 9 -> new LombardiaProvinces(this.context);
            case 10 -> new MarcheProvinces(this.context);
            case 11 -> new MoliseProvinces(this.context);
            case 12 -> new PiemonteProvinces(this.context);
            case 13 -> new PugliaProvinces(this.context);
            case 14 -> new SardegnaProvinces(this.context);
            case 15 -> new SiciliaProvinces(this.context);
            case 16 -> new ToscanaProvinces(this.context);
            case 17 -> new TrentinoAltoAdigeProvinces(this.context);
            case 18 -> new UmbriaProvinces(this.context);
            case 19 -> new ValleDAostaProvinces(this.context);
            case 20 -> new VenetoProvinces(this.context);
            default -> throw new Exception("Invalid type: " + type);
        };
    }
}
