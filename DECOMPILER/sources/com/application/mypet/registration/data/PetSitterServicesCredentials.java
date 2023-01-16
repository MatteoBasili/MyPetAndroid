package com.application.mypet.registration.data;

public class PetSitterServicesCredentials {
    private String description;
    private boolean serv1;
    private boolean serv2;
    private boolean serv3;
    private boolean serv4;
    private boolean serv5;

    public PetSitterServicesCredentials(boolean serv12, boolean serv22, boolean serv32, boolean serv42, boolean serv52, String description2) {
        this.serv1 = serv12;
        this.serv2 = serv22;
        this.serv3 = serv32;
        this.serv4 = serv42;
        this.serv5 = serv52;
        this.description = description2;
    }

    public boolean isServ1() {
        return this.serv1;
    }

    public boolean isServ2() {
        return this.serv2;
    }

    public boolean isServ3() {
        return this.serv3;
    }

    public boolean isServ4() {
        return this.serv4;
    }

    public boolean isServ5() {
        return this.serv5;
    }

    public String getDescription() {
        return this.description;
    }
}
