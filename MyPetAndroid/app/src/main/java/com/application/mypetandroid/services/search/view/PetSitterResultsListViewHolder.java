package com.application.mypetandroid.services.search.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.mypetandroid.R;

public class PetSitterResultsListViewHolder {
    private final ImageView addFav;
    private final ImageView info;
    private final ImageView itemImage;
    private final ImageView defaultItemImage;
    private final TextView itemNumDislikes;
    private final TextView itemNumLikes;
    private final TextView itemRegion;
    private final TextView itemProvince;
    private final TextView itemUsername;
    private final ImageView noFav;

    public PetSitterResultsListViewHolder(View v) {
        this.itemImage = v.findViewById(R.id.photo_profile);
        this.defaultItemImage = v.findViewById(R.id.default_photo_profile);
        this.itemUsername = v.findViewById(R.id.username);
        this.itemRegion = v.findViewById(R.id.region);
        this.itemProvince = v.findViewById(R.id.province);
        this.itemNumLikes = v.findViewById(R.id.likes_numb);
        this.itemNumDislikes = v.findViewById(R.id.dislikes_numb);
        this.addFav = v.findViewById(R.id.favorites_clicked);
        this.noFav = v.findViewById(R.id.favorites_view);
        this.info = v.findViewById(R.id.info);
    }

    public ImageView getAddFav() {
        return addFav;
    }

    public ImageView getInfo() {
        return info;
    }

    public ImageView getItemImage() {
        return itemImage;
    }
    public ImageView getDefaultItemImage() {
        return defaultItemImage;
    }

    public TextView getItemNumDislikes() {
        return itemNumDislikes;
    }

    public TextView getItemNumLikes() {
        return itemNumLikes;
    }

    public TextView getItemRegion() {
        return itemRegion;
    }

    public TextView getItemProvince() {
        return itemProvince;
    }

    public TextView getItemUsername() {
        return itemUsername;
    }

    public ImageView getNoFav() {
        return noFav;
    }
}
