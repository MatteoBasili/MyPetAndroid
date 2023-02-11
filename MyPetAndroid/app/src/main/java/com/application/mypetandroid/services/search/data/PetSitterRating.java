package com.application.mypetandroid.services.search.data;

public class PetSitterRating {

    private boolean isFavorite;
    private int rating;

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
