package com.application.mypet.utils.factory;

import android.content.Context;

public class FactoryProvinces {
    private Context context;

    public FactoryProvinces(Context current) {
        this.context = current;
    }

    public ProvincesBaseList createProvinceBaseList(int type) throws Exception {
        switch (type) {
            case 1:
                return new AbruzzoProvinces(this.context);
            case 2:
                return new BasilicataProvinces(this.context);
            case 3:
                return new CalabriaProvinces(this.context);
            case 4:
                return new CampaniaProvinces(this.context);
            case 5:
                return new EmiliaRomagnaProvinces(this.context);
            case 6:
                return new FriuliVeneziaGiuliaProvinces(this.context);
            case 7:
                return new LazioProvinces(this.context);
            case 8:
                return new LiguriaProvinces(this.context);
            case 9:
                return new LombardiaProvinces(this.context);
            case 10:
                return new MarcheProvinces(this.context);
            case 11:
                return new MoliseProvinces(this.context);
            case 12:
                return new PiemonteProvinces(this.context);
            case 13:
                return new PugliaProvinces(this.context);
            case 14:
                return new SardegnaProvinces(this.context);
            case 15:
                return new SiciliaProvinces(this.context);
            case 16:
                return new ToscanaProvinces(this.context);
            case 17:
                return new TrentinoAltoAdigeProvinces(this.context);
            case 18:
                return new UmbriaProvinces(this.context);
            case 19:
                return new ValleDAostaProvinces(this.context);
            case 20:
                return new VenetoProvinces(this.context);
            default:
                throw new Exception("Invalid type : " + type);
        }
    }
}
