package com.application.mypet.services.profile.petsitter.view;

import com.application.mypet.services.profile.petsitter.CaredPetsPresenter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class CaredPetsFragment$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ CaredPetsPresenter f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ CaredPetsFragment$$ExternalSyntheticLambda1(CaredPetsPresenter caredPetsPresenter, String str) {
        this.f$0 = caredPetsPresenter;
        this.f$1 = str;
    }

    public final void run() {
        this.f$0.loadCaredPets(this.f$1);
    }
}
