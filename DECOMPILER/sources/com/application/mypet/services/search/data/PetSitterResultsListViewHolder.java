package com.application.mypet.services.search.data;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.mypet.R;

public class PetSitterResultsListViewHolder {
    ImageView addFav;
    ImageView info;
    ImageView itemImage;
    TextView itemNumDislikes;
    TextView itemNumLikes;
    TextView itemPlace;
    TextView itemUsername;
    ImageView noFav;

    PetSitterResultsListViewHolder(View v) {
        this.itemImage = (ImageView) v.findViewById(R.id.photo_profile);
        this.itemUsername = (TextView) v.findViewById(R.id.username);
        this.itemPlace = (TextView) v.findViewById(R.id.place);
        this.itemNumLikes = (TextView) v.findViewById(R.id.likes_numb);
        this.itemNumDislikes = (TextView) v.findViewById(R.id.dislikes_numb);
        this.addFav = (ImageView) v.findViewById(R.id.favorites_clicked);
        this.noFav = (ImageView) v.findViewById(R.id.favorites_view);
        this.info = (ImageView) v.findViewById(R.id.info);
    }
}
