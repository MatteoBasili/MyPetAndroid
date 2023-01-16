package com.application.mypet.services.search.view;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.application.mypet.R;
import com.application.mypet.services.HomeActivity;
import com.application.mypet.services.search.FavoritesContract;
import com.application.mypet.services.search.FavoritesPresenter;
import com.application.mypet.services.search.PetSitSearchPresenter;
import com.application.mypet.services.search.RatingContract;
import com.application.mypet.services.search.SearchContract;
import com.application.mypet.services.search.data.PetSitDetailsInfo;
import com.application.mypet.services.search.data.PetSitSearchInteractor;

public class PetSitterDetailsFragment extends Fragment implements SearchContract.PetSitterDetailsView, FavoritesContract.AddPetSitToFavView, RatingContract.RatePetSitView {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String RATING_TEXT = "Rated";
    private ImageView addFav;
    private CheckBox cat;
    private TextView description;
    private ImageView dislikeImage;
    private TextView dislikeNumb;
    private CheckBox dog;
    private TextView email;
    private boolean favorite = false;
    private ImageView likeImage;
    private TextView likeNumb;
    private ProgressBar loadProgressBar;
    private TextView name;
    private ImageView noFav;
    private CheckBox otherPets;
    private String petSitter;
    private TextView phone;
    private TextView place;
    private int position;
    private ImageView profileImage;
    private PetSitSearchInteractor.Rating rating;
    private CheckBox serv1;
    private CheckBox serv2;
    private CheckBox serv3;
    private CheckBox serv4;
    private CheckBox serv5;
    private String user;

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
        PetSitSearchPresenter petSitDetailsPresenter = new PetSitSearchPresenter((SearchContract.PetSitterDetailsView) this);
        PetSitSearchPresenter petSitRatingPresenter = new PetSitSearchPresenter((RatingContract.RatePetSitView) this);
        FavoritesPresenter favoritesPresenter = new FavoritesPresenter((FavoritesContract.AddPetSitToFavView) this);
        this.loadProgressBar = (ProgressBar) petSitDetailsView.findViewById(R.id.load_progressBar);
        this.profileImage = (ImageView) petSitDetailsView.findViewById(R.id.photo_profile);
        this.addFav = (ImageView) petSitDetailsView.findViewById(R.id.favorites_clicked);
        this.noFav = (ImageView) petSitDetailsView.findViewById(R.id.favorites_view);
        this.likeImage = (ImageView) petSitDetailsView.findViewById(R.id.likes);
        this.dislikeImage = (ImageView) petSitDetailsView.findViewById(R.id.dislikes);
        this.likeNumb = (TextView) petSitDetailsView.findViewById(R.id.likes_numb);
        this.dislikeNumb = (TextView) petSitDetailsView.findViewById(R.id.dislikes_numb);
        this.name = (TextView) petSitDetailsView.findViewById(R.id.name);
        this.place = (TextView) petSitDetailsView.findViewById(R.id.place);
        this.description = (TextView) petSitDetailsView.findViewById(R.id.description);
        this.dog = (CheckBox) petSitDetailsView.findViewById(R.id.cared_pets_checkBox1);
        this.cat = (CheckBox) petSitDetailsView.findViewById(R.id.cared_pets_checkBox2);
        this.otherPets = (CheckBox) petSitDetailsView.findViewById(R.id.cared_pets_checkBox3);
        this.serv1 = (CheckBox) petSitDetailsView.findViewById(R.id.services_checkBox1);
        this.serv2 = (CheckBox) petSitDetailsView.findViewById(R.id.services_checkBox2);
        this.serv3 = (CheckBox) petSitDetailsView.findViewById(R.id.services_checkBox3);
        this.serv4 = (CheckBox) petSitDetailsView.findViewById(R.id.services_checkBox4);
        this.serv5 = (CheckBox) petSitDetailsView.findViewById(R.id.services_checkBox5);
        this.phone = (TextView) petSitDetailsView.findViewById(R.id.phone);
        this.email = (TextView) petSitDetailsView.findViewById(R.id.email);
        String userUsername = this.user;
        int pos = this.position;
        ((TextView) petSitDetailsView.findViewById(R.id.title)).setText(this.petSitter);
        new Handler().postDelayed(new PetSitterDetailsFragment$$ExternalSyntheticLambda0(this, petSitDetailsPresenter, userUsername), 10);
        this.profileImage.setOnClickListener(new PetSitterDetailsFragment$$ExternalSyntheticLambda1(this));
        this.noFav.setOnClickListener(new PetSitterDetailsFragment$$ExternalSyntheticLambda2(this, favoritesPresenter, userUsername, pos));
        this.addFav.setOnClickListener(new PetSitterDetailsFragment$$ExternalSyntheticLambda3(this, favoritesPresenter, userUsername, pos));
        this.likeImage.setOnClickListener(new PetSitterDetailsFragment$$ExternalSyntheticLambda4(this, petSitRatingPresenter, userUsername));
        this.dislikeImage.setOnClickListener(new PetSitterDetailsFragment$$ExternalSyntheticLambda5(this, petSitRatingPresenter, userUsername));
        ((ImageView) petSitDetailsView.findViewById(R.id.back_arrow)).setOnClickListener(new PetSitterDetailsFragment$$ExternalSyntheticLambda6(this));
        return petSitDetailsView;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$0$com-application-mypet-services-search-view-PetSitterDetailsFragment  reason: not valid java name */
    public /* synthetic */ void m8lambda$onCreateView$0$comapplicationmypetservicessearchviewPetSitterDetailsFragment(PetSitSearchPresenter petSitDetailsPresenter, String userUsername) {
        petSitDetailsPresenter.loadDetails(userUsername, this.petSitter);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$2$com-application-mypet-services-search-view-PetSitterDetailsFragment  reason: not valid java name */
    public /* synthetic */ void m9lambda$onCreateView$2$comapplicationmypetservicessearchviewPetSitterDetailsFragment(View view) {
        if (this.profileImage.getDrawable().getConstantState() != requireContext().getResources().getDrawable(R.drawable.user).getConstantState()) {
            View zoomImage = LayoutInflater.from(getContext()).inflate(R.layout.zoom_image, (ViewGroup) null);
            ((ImageView) zoomImage.findViewById(R.id.image)).setImageDrawable(this.profileImage.getDrawable());
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
            dialogBuilder.setView(zoomImage);
            ((ImageView) zoomImage.findViewById(R.id.close)).setOnClickListener(new PetSitterDetailsFragment$$ExternalSyntheticLambda7(dialogBuilder.show()));
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$3$com-application-mypet-services-search-view-PetSitterDetailsFragment  reason: not valid java name */
    public /* synthetic */ void m10lambda$onCreateView$3$comapplicationmypetservicessearchviewPetSitterDetailsFragment(FavoritesPresenter favoritesPresenter, String userUsername, int pos, View v) {
        if (!this.favorite) {
            this.addFav.setVisibility(0);
            this.noFav.setVisibility(8);
        }
        favoritesPresenter.addToFav(userUsername, this.petSitter, pos);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$4$com-application-mypet-services-search-view-PetSitterDetailsFragment  reason: not valid java name */
    public /* synthetic */ void m11lambda$onCreateView$4$comapplicationmypetservicessearchviewPetSitterDetailsFragment(FavoritesPresenter favoritesPresenter, String userUsername, int pos, View v) {
        if (this.favorite) {
            this.noFav.setVisibility(0);
            this.addFav.setVisibility(8);
        }
        favoritesPresenter.addToFav(userUsername, this.petSitter, pos);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$5$com-application-mypet-services-search-view-PetSitterDetailsFragment  reason: not valid java name */
    public /* synthetic */ void m12lambda$onCreateView$5$comapplicationmypetservicessearchviewPetSitterDetailsFragment(PetSitSearchPresenter petSitRatingPresenter, String userUsername, View v) {
        switch (AnonymousClass1.$SwitchMap$com$application$mypet$services$search$data$PetSitSearchInteractor$Rating[this.rating.ordinal()]) {
            case 1:
            case 4:
                this.likeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                this.rating = PetSitSearchInteractor.Rating.FROM_LIKE_TO_NONE;
                break;
            case 2:
            case 3:
                this.likeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.like_color)));
                this.dislikeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                this.rating = PetSitSearchInteractor.Rating.FROM_DISLIKE_TO_LIKE;
                break;
            case 5:
            case 6:
            case 7:
                this.likeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.like_color)));
                this.rating = PetSitSearchInteractor.Rating.LIKE;
                break;
        }
        petSitRatingPresenter.ratePetSitter(userUsername, this.petSitter, this.rating);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$6$com-application-mypet-services-search-view-PetSitterDetailsFragment  reason: not valid java name */
    public /* synthetic */ void m13lambda$onCreateView$6$comapplicationmypetservicessearchviewPetSitterDetailsFragment(PetSitSearchPresenter petSitRatingPresenter, String userUsername, View v) {
        switch (AnonymousClass1.$SwitchMap$com$application$mypet$services$search$data$PetSitSearchInteractor$Rating[this.rating.ordinal()]) {
            case 1:
            case 4:
                this.likeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                this.dislikeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.dislike_color)));
                this.rating = PetSitSearchInteractor.Rating.FROM_LIKE_TO_DISLIKE;
                break;
            case 2:
            case 3:
                this.dislikeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                this.rating = PetSitSearchInteractor.Rating.FROM_DISLIKE_TO_NONE;
                break;
            case 5:
            case 6:
            case 7:
                this.dislikeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.dislike_color)));
                this.rating = PetSitSearchInteractor.Rating.DISLIKE;
                break;
        }
        petSitRatingPresenter.ratePetSitter(userUsername, this.petSitter, this.rating);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$7$com-application-mypet-services-search-view-PetSitterDetailsFragment  reason: not valid java name */
    public /* synthetic */ void m14lambda$onCreateView$7$comapplicationmypetservicessearchviewPetSitterDetailsFragment(View view) {
        ((HomeActivity) requireContext()).getSupportFragmentManager().popBackStackImmediate();
    }

    public void hideLoadProgressbar() {
        this.loadProgressBar.setVisibility(8);
    }

    public void onLoadDetailsSuccess(PetSitDetailsInfo petSitDetailsInfo) {
        try {
            this.noFav.setClickable(true);
            this.likeImage.setClickable(true);
            this.dislikeImage.setClickable(true);
            if (petSitDetailsInfo.getLoadProfileInfo().getImage() != null) {
                this.profileImage.setImageBitmap(petSitDetailsInfo.getLoadProfileInfo().getImage());
            } else {
                this.profileImage.setImageDrawable(getResources().getDrawable(R.drawable.user));
            }
            this.likeNumb.setText(String.valueOf(petSitDetailsInfo.getLoadProfileInfo().getNumLikes()));
            this.dislikeNumb.setText(String.valueOf(petSitDetailsInfo.getLoadProfileInfo().getNumDislikes()));
            this.name.setText(petSitDetailsInfo.getProfileCredentials().getName() + " " + petSitDetailsInfo.getProfileCredentials().getSurname());
            this.place.setText(petSitDetailsInfo.getProfileCredentials().getProvince());
            this.description.setText(petSitDetailsInfo.getPetSitterServicesCredentials().getDescription());
            this.email.setText(petSitDetailsInfo.getEmail());
            this.phone.setText(petSitDetailsInfo.getProfileCredentials().getPhoneNumb());
            if (petSitDetailsInfo.getPetSitRating().isFavorite()) {
                this.addFav.setVisibility(0);
                this.noFav.setVisibility(8);
                this.favorite = true;
            }
            if (petSitDetailsInfo.getPetSitRating().getRating() == 1) {
                this.likeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.like_color)));
                this.rating = PetSitSearchInteractor.Rating.LIKE;
            } else if (petSitDetailsInfo.getPetSitRating().getRating() == 2) {
                this.dislikeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.dislike_color)));
                this.rating = PetSitSearchInteractor.Rating.DISLIKE;
            } else {
                this.rating = PetSitSearchInteractor.Rating.NO_RATE;
            }
            this.dog.setChecked(petSitDetailsInfo.getPetSitterCaredPetsCredentials().isDog());
            this.cat.setChecked(petSitDetailsInfo.getPetSitterCaredPetsCredentials().isCat());
            this.otherPets.setChecked(petSitDetailsInfo.getPetSitterCaredPetsCredentials().isOtherPets());
            this.serv1.setChecked(petSitDetailsInfo.getPetSitterServicesCredentials().isServ1());
            this.serv2.setChecked(petSitDetailsInfo.getPetSitterServicesCredentials().isServ2());
            this.serv3.setChecked(petSitDetailsInfo.getPetSitterServicesCredentials().isServ3());
            this.serv4.setChecked(petSitDetailsInfo.getPetSitterServicesCredentials().isServ4());
            this.serv5.setChecked(petSitDetailsInfo.getPetSitterServicesCredentials().isServ5());
        } catch (IllegalStateException e) {
        }
    }

    public void onLoadDetailsFailed(String message) {
        Toast.makeText(getContext(), message, 0).show();
    }

    public void onAddToFavSuccess(String message, int pos) {
        Toast.makeText(getContext(), message, 0).show();
        this.favorite = !this.favorite;
    }

    public void onAddToFavFailed(String message) {
        Toast.makeText(getContext(), message, 0).show();
    }

    /* renamed from: com.application.mypet.services.search.view.PetSitterDetailsFragment$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$application$mypet$services$search$data$PetSitSearchInteractor$Rating;

        static {
            int[] iArr = new int[PetSitSearchInteractor.Rating.values().length];
            $SwitchMap$com$application$mypet$services$search$data$PetSitSearchInteractor$Rating = iArr;
            try {
                iArr[PetSitSearchInteractor.Rating.LIKE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$application$mypet$services$search$data$PetSitSearchInteractor$Rating[PetSitSearchInteractor.Rating.DISLIKE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$application$mypet$services$search$data$PetSitSearchInteractor$Rating[PetSitSearchInteractor.Rating.FROM_LIKE_TO_DISLIKE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$application$mypet$services$search$data$PetSitSearchInteractor$Rating[PetSitSearchInteractor.Rating.FROM_DISLIKE_TO_LIKE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$application$mypet$services$search$data$PetSitSearchInteractor$Rating[PetSitSearchInteractor.Rating.FROM_LIKE_TO_NONE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$application$mypet$services$search$data$PetSitSearchInteractor$Rating[PetSitSearchInteractor.Rating.FROM_DISLIKE_TO_NONE.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$application$mypet$services$search$data$PetSitSearchInteractor$Rating[PetSitSearchInteractor.Rating.NO_RATE.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    public void onRateSuccess() {
        String message;
        switch (AnonymousClass1.$SwitchMap$com$application$mypet$services$search$data$PetSitSearchInteractor$Rating[this.rating.ordinal()]) {
            case 1:
                TextView textView = this.likeNumb;
                textView.setText(String.valueOf(Integer.parseInt(textView.getText().toString()) + 1));
                message = RATING_TEXT;
                break;
            case 2:
                TextView textView2 = this.dislikeNumb;
                textView2.setText(String.valueOf(Integer.parseInt(textView2.getText().toString()) + 1));
                message = RATING_TEXT;
                break;
            case 3:
                TextView textView3 = this.dislikeNumb;
                textView3.setText(String.valueOf(Integer.parseInt(textView3.getText().toString()) + 1));
                TextView textView4 = this.likeNumb;
                textView4.setText(String.valueOf(Integer.parseInt(textView4.getText().toString()) - 1));
                message = RATING_TEXT;
                break;
            case 4:
                TextView textView5 = this.likeNumb;
                textView5.setText(String.valueOf(Integer.parseInt(textView5.getText().toString()) + 1));
                TextView textView6 = this.dislikeNumb;
                textView6.setText(String.valueOf(Integer.parseInt(textView6.getText().toString()) - 1));
                message = RATING_TEXT;
                break;
            case 5:
                TextView textView7 = this.likeNumb;
                textView7.setText(String.valueOf(Integer.parseInt(textView7.getText().toString()) - 1));
                message = "Rating cancelled";
                break;
            case 6:
                TextView textView8 = this.dislikeNumb;
                textView8.setText(String.valueOf(Integer.parseInt(textView8.getText().toString()) - 1));
                message = "Rating cancelled";
                break;
            default:
                message = RATING_TEXT;
                break;
        }
        Toast.makeText(getContext(), message, 0).show();
    }

    public void onRateFailed(String message) {
        Toast.makeText(getContext(), message, 0).show();
        switch (AnonymousClass1.$SwitchMap$com$application$mypet$services$search$data$PetSitSearchInteractor$Rating[this.rating.ordinal()]) {
            case 1:
                this.likeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                this.rating = PetSitSearchInteractor.Rating.NO_RATE;
                return;
            case 2:
                this.dislikeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                this.rating = PetSitSearchInteractor.Rating.NO_RATE;
                return;
            case 3:
                this.dislikeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                this.likeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.like_color)));
                this.rating = PetSitSearchInteractor.Rating.LIKE;
                return;
            case 4:
                this.likeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                this.dislikeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.dislike_color)));
                this.rating = PetSitSearchInteractor.Rating.DISLIKE;
                return;
            case 5:
                this.likeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.like_color)));
                this.rating = PetSitSearchInteractor.Rating.LIKE;
                return;
            case 6:
                this.dislikeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.dislike_color)));
                this.rating = PetSitSearchInteractor.Rating.DISLIKE;
                return;
            case 7:
                this.dislikeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                this.likeImage.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                return;
            default:
                return;
        }
    }
}
