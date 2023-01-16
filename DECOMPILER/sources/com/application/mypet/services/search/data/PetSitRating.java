package com.application.mypet.services.search.data;

public class PetSitRating {
    private boolean favorite;
    private int rating;

    public PetSitRating(boolean favorite2, int rating2) {
        this.favorite = favorite2;
        this.rating = rating2;
    }

    public boolean isFavorite() {
        return this.favorite;
    }

    public int getRating() {
        return this.rating;
    }
}
