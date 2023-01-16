package com.application.mypet.services.profile.petsitter.data;

import android.graphics.Bitmap;

public class LoadProfileInfo {
    private Bitmap image;
    private int numDislikes;
    private int numLikes;

    public LoadProfileInfo(Bitmap image2, int numLikes2, int numDislikes2) {
        this.image = image2;
        this.numLikes = numLikes2;
        this.numDislikes = numDislikes2;
    }

    public Bitmap getImage() {
        return this.image;
    }

    public int getNumLikes() {
        return this.numLikes;
    }

    public int getNumDislikes() {
        return this.numDislikes;
    }
}
