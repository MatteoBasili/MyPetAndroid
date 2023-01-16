package com.application.mypet.services.search.data;

import android.graphics.Bitmap;
import java.util.List;

public class PetSitterResultList {
    private List<Boolean> favorites;
    private List<Bitmap> images;
    private List<Integer> numDislikes;
    private List<Integer> numLikes;
    private List<String> places;
    private List<String> usernames;

    public PetSitterResultList(List<Bitmap> images2, List<String> usernames2, List<String> places2, List<Integer> numLikes2, List<Integer> numDislikes2, List<Boolean> favorites2) {
        this.images = images2;
        this.usernames = usernames2;
        this.places = places2;
        this.numLikes = numLikes2;
        this.numDislikes = numDislikes2;
        this.favorites = favorites2;
    }

    public int getResNumber() {
        return getUsernames().size();
    }

    public List<Bitmap> getImages() {
        return this.images;
    }

    public List<String> getUsernames() {
        return this.usernames;
    }

    public List<String> getPlaces() {
        return this.places;
    }

    public List<Integer> getNumLikes() {
        return this.numLikes;
    }

    public List<Integer> getNumDislikes() {
        return this.numDislikes;
    }

    public List<Boolean> getFavorites() {
        return this.favorites;
    }
}
