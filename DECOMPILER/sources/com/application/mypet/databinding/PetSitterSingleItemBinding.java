package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;

public final class PetSitterSingleItemBinding implements ViewBinding {
    public final ImageView dislikes;
    public final TextView dislikesNumb;
    public final ImageView favoritesClicked;
    public final ImageView favoritesView;
    public final ImageView info;
    public final ImageView likes;
    public final TextView likesNumb;
    public final ImageView photoProfile;
    public final RelativeLayout photoProfileView;
    public final TextView place;
    private final LinearLayout rootView;
    public final TextView username;

    private PetSitterSingleItemBinding(LinearLayout rootView2, ImageView dislikes2, TextView dislikesNumb2, ImageView favoritesClicked2, ImageView favoritesView2, ImageView info2, ImageView likes2, TextView likesNumb2, ImageView photoProfile2, RelativeLayout photoProfileView2, TextView place2, TextView username2) {
        this.rootView = rootView2;
        this.dislikes = dislikes2;
        this.dislikesNumb = dislikesNumb2;
        this.favoritesClicked = favoritesClicked2;
        this.favoritesView = favoritesView2;
        this.info = info2;
        this.likes = likes2;
        this.likesNumb = likesNumb2;
        this.photoProfile = photoProfile2;
        this.photoProfileView = photoProfileView2;
        this.place = place2;
        this.username = username2;
    }

    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static PetSitterSingleItemBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static PetSitterSingleItemBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.pet_sitter_single_item, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static PetSitterSingleItemBinding bind(View rootView2) {
        View view = rootView2;
        int id = R.id.dislikes;
        ImageView dislikes2 = (ImageView) ViewBindings.findChildViewById(view, R.id.dislikes);
        if (dislikes2 != null) {
            id = R.id.dislikes_numb;
            TextView dislikesNumb2 = (TextView) ViewBindings.findChildViewById(view, R.id.dislikes_numb);
            if (dislikesNumb2 != null) {
                id = R.id.favorites_clicked;
                ImageView favoritesClicked2 = (ImageView) ViewBindings.findChildViewById(view, R.id.favorites_clicked);
                if (favoritesClicked2 != null) {
                    id = R.id.favorites_view;
                    ImageView favoritesView2 = (ImageView) ViewBindings.findChildViewById(view, R.id.favorites_view);
                    if (favoritesView2 != null) {
                        id = R.id.info;
                        ImageView info2 = (ImageView) ViewBindings.findChildViewById(view, R.id.info);
                        if (info2 != null) {
                            id = R.id.likes;
                            ImageView likes2 = (ImageView) ViewBindings.findChildViewById(view, R.id.likes);
                            if (likes2 != null) {
                                id = R.id.likes_numb;
                                TextView likesNumb2 = (TextView) ViewBindings.findChildViewById(view, R.id.likes_numb);
                                if (likesNumb2 != null) {
                                    id = R.id.photo_profile;
                                    ImageView photoProfile2 = (ImageView) ViewBindings.findChildViewById(view, R.id.photo_profile);
                                    if (photoProfile2 != null) {
                                        id = R.id.photo_profile_view;
                                        RelativeLayout photoProfileView2 = (RelativeLayout) ViewBindings.findChildViewById(view, R.id.photo_profile_view);
                                        if (photoProfileView2 != null) {
                                            id = R.id.place;
                                            TextView place2 = (TextView) ViewBindings.findChildViewById(view, R.id.place);
                                            if (place2 != null) {
                                                id = R.id.username;
                                                TextView username2 = (TextView) ViewBindings.findChildViewById(view, R.id.username);
                                                if (username2 != null) {
                                                    return new PetSitterSingleItemBinding((LinearLayout) view, dislikes2, dislikesNumb2, favoritesClicked2, favoritesView2, info2, likes2, likesNumb2, photoProfile2, photoProfileView2, place2, username2);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(id)));
    }
}
