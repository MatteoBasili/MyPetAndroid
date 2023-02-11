package com.application.mypetandroid.utils.factory_method_example;

import android.content.Context;

import com.application.mypetandroid.utils.factory_method_example.regions.*;

public class RegionsFactory {
    private final Context context;

    public RegionsFactory(Context current) {
        this.context = current;
    }

    public ProvincesBaseList createProvinceBaseList(int type) throws Exception {
        return switch (type) {
            case 1 -> new Abruzzo(this.context);
            case 2 -> new Basilicata(this.context);
            case 3 -> new Calabria(this.context);
            case 4 -> new Campania(this.context);
            case 5 -> new EmiliaRomagna(this.context);
            case 6 -> new FriuliVeneziaGiulia(this.context);
            case 7 -> new Lazio(this.context);
            case 8 -> new Liguria(this.context);
            case 9 -> new Lombardia(this.context);
            case 10 -> new Marche(this.context);
            case 11 -> new Molise(this.context);
            case 12 -> new Piemonte(this.context);
            case 13 -> new Puglia(this.context);
            case 14 -> new Sardegna(this.context);
            case 15 -> new Sicilia(this.context);
            case 16 -> new Toscana(this.context);
            case 17 -> new TrentinoAltoAdige(this.context);
            case 18 -> new Umbria(this.context);
            case 19 -> new ValleDAosta(this.context);
            case 20 -> new Veneto(this.context);
            default -> throw new Exception("Invalid type: " + type);
        };
    }
}
