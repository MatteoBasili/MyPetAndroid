package com.application.mypetandroid.services.search.view;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.application.mypetandroid.R;
import com.application.mypetandroid.services.HomeActivity;
import com.application.mypetandroid.services.search.FavoritesPetSitPresenter;
import com.application.mypetandroid.services.search.PetSitterDetailsPresenter;
import com.application.mypetandroid.services.search.PetSitterRatingPresenter;
import com.application.mypetandroid.services.search.PetSitterSearchContract.PetSitterDetailsView;
import com.application.mypetandroid.services.search.PetSitterSearchContract.PetSitterFavoritesView;
import com.application.mypetandroid.services.search.PetSitterSearchContract.PetSitterRatingView;
import com.application.mypetandroid.services.search.data.PetSitterDetails;
import com.application.mypetandroid.services.search.data.Rating;
import com.application.mypetandroid.utils.singleton_examples.PetSitterResultsSingletonClass;
import com.application.mypetandroid.utils.singleton_examples.ZoomSingletonClass;

public class PetSitterDetailsFragment extends Fragment implements PetSitterFavoritesView, PetSitterDetailsView, PetSitterRatingView {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private ImageView addFavIcon;
    private CheckBox cat;
    private TextView description;
    private ImageView dislikeImage;
    private ImageView noDislikeImage;
    private TextView dislikeNumb;
    private CheckBox dog;
    private TextView email;
    private ImageView likeImage;
    private ImageView noLikeImage;
    private TextView likeNumb;
    private ProgressBar loadProgressBar;
    private TextView name;
    private ImageView noFavIcon;
    private CheckBox otherPets;
    private TextView phone;
    private TextView place;
    private ImageView profileImage;
    private ImageView defaultProfileImage;
    private CheckBox serv1;
    private CheckBox serv2;
    private CheckBox serv3;
    private CheckBox serv4;
    private CheckBox serv5;
    private String user;
    private String petSitter;
    private int position;
    private FavoritesPetSitPresenter favoritesPresenter;
    private PetSitterDetailsPresenter detailsPresenter;
    private PetSitterRatingPresenter ratingPresenter;
    private PetSitterResultsSingletonClass resultList;
    private Bitmap currentImage;
    private Rating ratingState;

    public static PetSitterDetailsFragment newInstance(String user2, String petSitter2, int position2) {
        PetSitterDetailsFragment fragment = new PetSitterDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, user2);
        args.putString(ARG_PARAM2, petSitter2);
        args.putInt(ARG_PARAM3, position2);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.user = getArguments().getString(ARG_PARAM1);
            this.petSitter = getArguments().getString(ARG_PARAM2);
            this.position = getArguments().getInt(ARG_PARAM3);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View petSitDetailsView = inflater.inflate(R.layout.fragment_pet_sitter_details, container, false);

        ImageView backIcon = petSitDetailsView.findViewById(R.id.back_arrow);
        TextView usernameView = petSitDetailsView.findViewById(R.id.title);
        addFavIcon = petSitDetailsView.findViewById(R.id.favorites_clicked);
        noFavIcon = petSitDetailsView.findViewById(R.id.favorites_view);
        profileImage = petSitDetailsView.findViewById(R.id.photo_profile);
        defaultProfileImage = petSitDetailsView.findViewById(R.id.default_photo_profile);
        loadProgressBar = petSitDetailsView.findViewById(R.id.load_progressBar);
        noLikeImage = petSitDetailsView.findViewById(R.id.no_like);
        noDislikeImage = petSitDetailsView.findViewById(R.id.no_dislike);
        likeImage = petSitDetailsView.findViewById(R.id.like);
        dislikeImage = petSitDetailsView.findViewById(R.id.dislike);
        likeNumb = petSitDetailsView.findViewById(R.id.likes_numb);
        dislikeNumb = petSitDetailsView.findViewById(R.id.dislikes_numb);
        name = petSitDetailsView.findViewById(R.id.name);
        place = petSitDetailsView.findViewById(R.id.place);
        description = petSitDetailsView.findViewById(R.id.description);
        dog = petSitDetailsView.findViewById(R.id.cared_pets_checkBox1);
        cat = petSitDetailsView.findViewById(R.id.cared_pets_checkBox2);
        otherPets = petSitDetailsView.findViewById(R.id.cared_pets_checkBox3);
        serv1 = petSitDetailsView.findViewById(R.id.services_checkBox1);
        serv2 = petSitDetailsView.findViewById(R.id.services_checkBox2);
        serv3 = petSitDetailsView.findViewById(R.id.services_checkBox3);
        serv4 = petSitDetailsView.findViewById(R.id.services_checkBox4);
        serv5 = petSitDetailsView.findViewById(R.id.services_checkBox5);
        phone = petSitDetailsView.findViewById(R.id.phone);
        email = petSitDetailsView.findViewById(R.id.email);

        // Set pet sitter username
        usernameView.setText(petSitter);

        resultList = PetSitterResultsSingletonClass.getSingletonInstance();
        favoritesPresenter = new FavoritesPetSitPresenter(this);
        detailsPresenter = new PetSitterDetailsPresenter(this);
        ratingPresenter = new PetSitterRatingPresenter(this);

        // Set click listeners
        backIcon.setOnClickListener(v -> back());
        profileImage.setOnClickListener(v -> zoomImage());
        addFavIcon.setOnClickListener(v -> setFavorite(position, addFavIcon, noFavIcon));
        noFavIcon.setOnClickListener(v -> setFavorite(position, addFavIcon, noFavIcon));

        // For pet sitter rating
        likeImage.setOnClickListener(v -> {
            ratingState = Rating.FROM_LIKE_TO_NONE;
            ratePetSitter();
        });
        dislikeImage.setOnClickListener(v -> {
            ratingState = Rating.FROM_DISLIKE_TO_NONE;
            ratePetSitter();
        });
        noLikeImage.setOnClickListener(v -> {
            switch (ratingState) {
                case NO_RATE, FROM_DISLIKE_TO_NONE, FROM_LIKE_TO_NONE -> ratingState = Rating.LIKE;
                case DISLIKE, FROM_LIKE_TO_DISLIKE -> ratingState = Rating.FROM_DISLIKE_TO_LIKE;
                default -> {
                }
            }
            ratePetSitter();
        });
        noDislikeImage.setOnClickListener(v -> {
            switch (ratingState) {
                case NO_RATE, FROM_DISLIKE_TO_NONE, FROM_LIKE_TO_NONE -> ratingState = Rating.DISLIKE;
                case LIKE, FROM_DISLIKE_TO_LIKE -> ratingState = Rating.FROM_LIKE_TO_DISLIKE;
                default -> {
                }
            }
            ratePetSitter();
        });

        currentImage = null;
        // Load profile
        loadProfile();

        return petSitDetailsView;
    }

    private void loadProfile() {
        detailsPresenter.loadPetSitterDetails(user, resultList.getUsernames().get(position));
    }

    private void back() {
        ((HomeActivity) requireContext()).getSupportFragmentManager().popBackStackImmediate();
    }

    private void zoomImage() {
        ZoomSingletonClass zoom = ZoomSingletonClass.getSingletonInstance();
        zoom.setContext(getContext());
        zoom.setImage(currentImage);
        zoom.zoomImage();
    }

    private void setFavorite(int position, ImageView favIcon, ImageView noFavIcon) {
        favoritesPresenter.setFavorite(this.user, this.resultList.getUsernames().get(position), position, favIcon, noFavIcon);
    }

    private void ratePetSitter(){
        ratingPresenter.ratePetSitter(this.user, resultList.getUsernames().get(position), ratingState.ordinal());
    }

    @Override
    public void hideProgressIndicator() {
        this.loadProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSetFavoriteSuccess(int pos, ImageView favIcon, ImageView noFavIcon) {
        if (resultList.getFavorites().get(pos)) {
            resultList.getFavorites().set(pos, false);
            noFavIcon.setVisibility(View.VISIBLE);
            favIcon.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
        } else {
            resultList.getFavorites().set(pos, true);
            favIcon.setVisibility(View.VISIBLE);
            noFavIcon.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSetFavoriteFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadDetailsSuccess(PetSitterDetails petSitterDetails) {
        try {
            // Set details
            if (petSitterDetails.getPetSitProfileInfo().getImage() != null) {
                this.profileImage.setImageBitmap(petSitterDetails.getPetSitProfileInfo().getImage());
                this.profileImage.setVisibility(View.VISIBLE);
                currentImage = petSitterDetails.getPetSitProfileInfo().getImage();
            } else {
                this.defaultProfileImage.setVisibility(View.VISIBLE);
                currentImage = null;
            }
            this.likeNumb.setText(String.valueOf(petSitterDetails.getPetSitProfileInfo().getNumLikes()));
            this.dislikeNumb.setText(String.valueOf(petSitterDetails.getPetSitProfileInfo().getNumDislikes()));
            this.name.setText(petSitterDetails.getProfileUserData().getName() + " " + petSitterDetails.getProfileUserData().getSurname());
            this.place.setText(petSitterDetails.getProfileUserData().getProvince());
            this.description.setText(petSitterDetails.getPetSitServices().getDescription());
            this.email.setText(petSitterDetails.getEmail());
            this.phone.setText(petSitterDetails.getProfileUserData().getPhoneNumb());

            // Set cared pets
            this.dog.setChecked(petSitterDetails.getPetSitCaredPets().isDog());
            this.cat.setChecked(petSitterDetails.getPetSitCaredPets().isCat());
            this.otherPets.setChecked(petSitterDetails.getPetSitCaredPets().isOtherPets());

            // Set services
            this.serv1.setChecked(petSitterDetails.getPetSitServices().isServ1());
            this.serv2.setChecked(petSitterDetails.getPetSitServices().isServ2());
            this.serv3.setChecked(petSitterDetails.getPetSitServices().isServ3());
            this.serv4.setChecked(petSitterDetails.getPetSitServices().isServ4());
            this.serv5.setChecked(petSitterDetails.getPetSitServices().isServ5());

            // Set rating
            if (petSitterDetails.getPetSitterRating().getRating() == 1) {
                this.likeImage.setVisibility(View.VISIBLE);
                this.noLikeImage.setVisibility(View.GONE);
                this.ratingState = Rating.LIKE;
            } else if (petSitterDetails.getPetSitterRating().getRating() == 2) {
                this.dislikeImage.setVisibility(View.VISIBLE);
                this.noDislikeImage.setVisibility(View.GONE);
                this.ratingState = Rating.DISLIKE;
            } else {
                this.ratingState = Rating.NO_RATE;
            }

            // Set favorite
            if (petSitterDetails.getPetSitterRating().isFavorite()) {
                this.addFavIcon.setVisibility(View.VISIBLE);
                this.noFavIcon.setVisibility(View.GONE);
            }

            // Unlock resources
            this.noFavIcon.setClickable(true);
            this.noLikeImage.setClickable(true);
            this.noDislikeImage.setClickable(true);

        } catch (IllegalStateException ignored) {
        }
    }

    @Override
    public void onLoadDetailsFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRateSuccess() {
        switch (ratingState) {
            case LIKE -> {
                likeImage.setVisibility(View.VISIBLE);
                noLikeImage.setVisibility(View.GONE);
                likeNumb.setText(String.valueOf(Integer.parseInt(likeNumb.getText().toString()) + 1));
            }
            case DISLIKE -> {
                dislikeImage.setVisibility(View.VISIBLE);
                noDislikeImage.setVisibility(View.GONE);
                dislikeNumb.setText(String.valueOf(Integer.parseInt(dislikeNumb.getText().toString()) + 1));
            }
            case FROM_LIKE_TO_DISLIKE -> {
                dislikeImage.setVisibility(View.VISIBLE);
                noLikeImage.setVisibility(View.VISIBLE);
                noDislikeImage.setVisibility(View.GONE);
                likeImage.setVisibility(View.GONE);
                likeNumb.setText(String.valueOf(Integer.parseInt(likeNumb.getText().toString()) - 1));
                dislikeNumb.setText(String.valueOf(Integer.parseInt(dislikeNumb.getText().toString()) + 1));
            }
            case FROM_DISLIKE_TO_LIKE -> {
                likeImage.setVisibility(View.VISIBLE);
                noDislikeImage.setVisibility(View.VISIBLE);
                noLikeImage.setVisibility(View.GONE);
                dislikeImage.setVisibility(View.GONE);
                likeNumb.setText(String.valueOf(Integer.parseInt(likeNumb.getText().toString()) + 1));
                dislikeNumb.setText(String.valueOf(Integer.parseInt(dislikeNumb.getText().toString()) - 1));
            }
            case FROM_LIKE_TO_NONE -> {
                noLikeImage.setVisibility(View.VISIBLE);
                likeImage.setVisibility(View.GONE);
                likeNumb.setText(String.valueOf(Integer.parseInt(likeNumb.getText().toString()) - 1));
            }
            case FROM_DISLIKE_TO_NONE -> {
                noDislikeImage.setVisibility(View.VISIBLE);
                dislikeImage.setVisibility(View.GONE);
                dislikeNumb.setText(String.valueOf(Integer.parseInt(dislikeNumb.getText().toString()) - 1));
            }
            default -> {
            }
        }

        // View the change of likes also in the list
        resultList.getPetSitProfileInfo().get(position).setNumLikes(Integer.parseInt(likeNumb.getText().toString()));
        resultList.getPetSitProfileInfo().get(position).setNumDislikes(Integer.parseInt(dislikeNumb.getText().toString()));
    }

    @Override
    public void onRateFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
