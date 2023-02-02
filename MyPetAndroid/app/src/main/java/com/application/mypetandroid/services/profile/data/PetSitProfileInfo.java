package com.application.mypetandroid.services.profile.data;

import android.graphics.Bitmap;

public class PetSitProfileInfo {

    private Bitmap image;
    private int numDislikes;
    private int numLikes;

    public Bitmap getImage() {
        return this.image;
    }

    public int getNumLikes() {
        return this.numLikes;
    }

    public int getNumDislikes() {
        return this.numDislikes;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setNumDislikes(int numDislikes) {
        this.numDislikes = numDislikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }
}
