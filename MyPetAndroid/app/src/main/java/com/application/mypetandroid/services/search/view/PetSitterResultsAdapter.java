package com.application.mypetandroid.services.search.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.application.mypetandroid.R;
import com.application.mypetandroid.services.search.FavoritesPetSitPresenter;
import com.application.mypetandroid.services.search.PetSitterSearchContract;
import com.application.mypetandroid.services.search.data.PetSitterResultsListViewHolder;
import com.application.mypetandroid.utils.singleton_examples.PetSitterResultsSingletonClass;
import com.application.mypetandroid.utils.singleton_examples.ZoomSingletonClass;

import java.util.List;

public class PetSitterResultsAdapter extends ArrayAdapter<String> implements PetSitterSearchContract.PetSitterFavoritesView {
    private static final String PET_SITTER_RESULTS_FRAGMENT_TAG = "petSitResFragmentTag";
    private final AppCompatActivity context;
    private final String user;
    private final PetSitterResultsSingletonClass resultList;
    private final FavoritesPetSitPresenter presenter;


    public PetSitterResultsAdapter(AppCompatActivity context2, String user2, List<String> usernames2) {
        super(context2, R.layout.pet_sitter_single_item, R.id.username, usernames2);
        this.context = context2;
        this.user = user2;
        resultList = PetSitterResultsSingletonClass.getSingletonInstance();
        presenter = new FavoritesPetSitPresenter(this);
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {

        View singleItem = convertView;
        PetSitterResultsListViewHolder holder;
        if (singleItem == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            singleItem = layoutInflater.inflate(R.layout.pet_sitter_single_item, parent, false);
            holder = new PetSitterResultsListViewHolder(singleItem);
            singleItem.setTag(holder);
        } else {
            holder = (PetSitterResultsListViewHolder) singleItem.getTag();
        }

        // Set single items
        if (resultList.getPetSitProfileInfo().get(position).getImage() != null) {
            holder.getItemImage().setImageBitmap(resultList.getPetSitProfileInfo().get(position).getImage());
            holder.getItemImage().setVisibility(View.VISIBLE);
            holder.getDefaultItemImage().setVisibility(View.GONE);
        } else {
            holder.getDefaultItemImage().setVisibility(View.VISIBLE);
            holder.getItemImage().setVisibility(View.GONE);
        }
        holder.getItemUsername().setText(resultList.getUsernames().get(position));
        holder.getItemRegion().setText(resultList.getRegions().get(position));
        holder.getItemProvince().setText(resultList.getProvinces().get(position));
        holder.getItemNumLikes().setText(String.valueOf(resultList.getPetSitProfileInfo().get(position).getNumLikes()));
        holder.getItemNumDislikes().setText(String.valueOf(resultList.getPetSitProfileInfo().get(position).getNumDislikes()));
        if (resultList.getFavorites().get(position)) {
            holder.getAddFav().setVisibility(View.VISIBLE);
            holder.getNoFav().setVisibility(View.GONE);
        } else {
            holder.getNoFav().setVisibility(View.VISIBLE);
            holder.getAddFav().setVisibility(View.GONE);
        }

        // Initialize click listeners
        holder.getItemImage().setOnClickListener(v -> zoomImage(position));
        holder.getAddFav().setOnClickListener(v -> setFavorite(position, holder.getAddFav(), holder.getNoFav()));
        holder.getNoFav().setOnClickListener(v -> setFavorite(position, holder.getAddFav(), holder.getNoFav()));
        holder.getInfo().setOnClickListener(v -> showPetSitterDetails(position));

        return singleItem;
    }

    private void zoomImage(int pos) {
        ZoomSingletonClass zoom = ZoomSingletonClass.getSingletonInstance();
        zoom.setContext(context);
        zoom.setImage(resultList.getPetSitProfileInfo().get(pos).getImage());
        zoom.zoomImage();
    }

    private void setFavorite(int position, ImageView favIcon, ImageView noFavIcon) {
        presenter.setFavorite(this.user, this.resultList.getUsernames().get(position), position, favIcon, noFavIcon);
    }

    private void showPetSitterDetails(int pos) {
        context.getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, PetSitterDetailsFragment.newInstance(this.user, pos))
                .addToBackStack(PET_SITTER_RESULTS_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onSetFavoriteSuccess(int pos, ImageView favIcon, ImageView noFavIcon) {
        if (resultList.getFavorites().get(pos)) {
            resultList.getFavorites().set(pos, false);
            noFavIcon.setVisibility(View.VISIBLE);
            favIcon.setVisibility(View.GONE);
            Toast.makeText(this.context, "Removed from favorites", Toast.LENGTH_SHORT).show();
        } else {
            resultList.getFavorites().set(pos, true);
            favIcon.setVisibility(View.VISIBLE);
            noFavIcon.setVisibility(View.GONE);
            Toast.makeText(this.context, "Added to favorites", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSetFavoriteFailed(String message) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
    }
}
