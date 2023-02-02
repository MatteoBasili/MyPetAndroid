package com.application.mypetandroid.registration.data;

public class PetSitServices {

    private String description;
    private boolean serv1;
    private boolean serv2;
    private boolean serv3;
    private boolean serv4;
    private boolean serv5;

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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setServ1(boolean serv1) {
        this.serv1 = serv1;
    }

    public void setServ2(boolean serv2) {
        this.serv2 = serv2;
    }

    public void setServ3(boolean serv3) {
        this.serv3 = serv3;
    }

    public void setServ4(boolean serv4) {
        this.serv4 = serv4;
    }

    public void setServ5(boolean serv5) {
        this.serv5 = serv5;
    }
}
