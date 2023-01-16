package com.application.mypet.services.profile.petsitter.view;

import com.application.mypet.services.profile.petsitter.ServicesPresenter;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class ServicesFragment$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ServicesPresenter f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ ServicesFragment$$ExternalSyntheticLambda1(ServicesPresenter servicesPresenter, String str) {
        this.f$0 = servicesPresenter;
        this.f$1 = str;
    }

    public final void run() {
        this.f$0.loadServices(this.f$1);
    }
}
