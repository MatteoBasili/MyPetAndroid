package com.application.mypet.services.profile.petsitter.data;

import android.graphics.Bitmap;

public class SavePhotoInfo {
    private Bitmap photo;
    private String user;

    public SavePhotoInfo(String user2, Bitmap photo2) {
        this.user = user2;
        this.photo = photo2;
    }

    public String getUser() {
        return this.user;
    }

    public Bitmap getPhoto() {
        return this.photo;
    }
}
