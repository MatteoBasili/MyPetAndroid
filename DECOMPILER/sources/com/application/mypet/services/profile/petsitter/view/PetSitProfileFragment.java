package com.application.mypet.services.profile.petsitter.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.application.mypet.R;
import com.application.mypet.login.view.LoginActivity;
import com.application.mypet.services.profile.ProfileContract;
import com.application.mypet.services.profile.petsitter.PetSitProfilePresenter;
import com.application.mypet.services.profile.petsitter.data.LoadProfileInfo;
import com.application.mypet.services.profile.petsitter.data.SavePhotoInfo;
import com.application.mypet.services.profile.view.FavoritesPetSitFragment;
import com.application.mypet.services.profile.view.PersonalAdsFragment;
import com.application.mypet.services.profile.view.PersonalInfoFragment;
import java.io.IOException;

public class PetSitProfileFragment extends Fragment implements ProfileContract.ProfileView {
    private static final String ARG_PARAM1 = "User";
    private static final int GALLERY_REQ_CODE = 1000;
    private static final String PET_SIT_PROFILE_FRAGMENT_TAG = "petSitProfileFragmentTag";
    private ImageView deletePhoto;
    private TextView dislikes;
    private ImageView editPhoto;
    private Uri imageUri;
    private TextView likes;
    private ProgressBar loadProgressBar;
    private ImageView profileImage;
    private ImageView savePhoto;
    private ProgressBar saveProgressBar;
    private String user;

    public static PetSitProfileFragment newInstance(String user2) {
        PetSitProfileFragment fragment = new PetSitProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, user2);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.user = getArguments().getString(ARG_PARAM1);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View profileView = inflater.inflate(R.layout.fragment_pet_sit_profile, container, false);
        PetSitProfilePresenter presenter = new PetSitProfilePresenter(this);
        View personalInfo = profileView.findViewById(R.id.personal_info_view);
        View favorites = profileView.findViewById(R.id.favorites_view);
        View ads = profileView.findViewById(R.id.ads_view);
        View caredPets = profileView.findViewById(R.id.cared_pets_view);
        View services = profileView.findViewById(R.id.services_view);
        this.saveProgressBar = (ProgressBar) profileView.findViewById(R.id.save_progressBar);
        this.loadProgressBar = (ProgressBar) profileView.findViewById(R.id.load_progressBar);
        this.profileImage = (ImageView) profileView.findViewById(R.id.photo_profile);
        this.editPhoto = (ImageView) profileView.findViewById(R.id.change_photo);
        this.savePhoto = (ImageView) profileView.findViewById(R.id.save_photo);
        this.deletePhoto = (ImageView) profileView.findViewById(R.id.delete_photo);
        this.likes = (TextView) profileView.findViewById(R.id.likes_numb);
        this.dislikes = (TextView) profileView.findViewById(R.id.dislikes_numb);
        ((TextView) profileView.findViewById(R.id.user)).setText(this.user);
        new Handler().postDelayed(new PetSitProfileFragment$$ExternalSyntheticLambda7(this, presenter), 500);
        this.profileImage.setOnClickListener(new PetSitProfileFragment$$ExternalSyntheticLambda9(this));
        this.editPhoto.setOnClickListener(new PetSitProfileFragment$$ExternalSyntheticLambda10(this));
        this.savePhoto.setOnClickListener(new PetSitProfileFragment$$ExternalSyntheticLambda11(this, presenter));
        this.deletePhoto.setOnClickListener(new PetSitProfileFragment$$ExternalSyntheticLambda12(this));
        personalInfo.setOnClickListener(new PetSitProfileFragment$$ExternalSyntheticLambda13(this));
        favorites.setOnClickListener(new PetSitProfileFragment$$ExternalSyntheticLambda1(this));
        ads.setOnClickListener(new PetSitProfileFragment$$ExternalSyntheticLambda2(this));
        caredPets.setOnClickListener(new PetSitProfileFragment$$ExternalSyntheticLambda3(this));
        services.setOnClickListener(new PetSitProfileFragment$$ExternalSyntheticLambda4(this));
        ((ImageView) profileView.findViewById(R.id.logout)).setOnClickListener(new PetSitProfileFragment$$ExternalSyntheticLambda8(this));
        return profileView;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$0$com-application-mypet-services-profile-petsitter-view-PetSitProfileFragment  reason: not valid java name */
    public /* synthetic */ void m31lambda$onCreateView$0$comapplicationmypetservicesprofilepetsitterviewPetSitProfileFragment(PetSitProfilePresenter presenter) {
        presenter.loadPhoto(this.user);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$2$com-application-mypet-services-profile-petsitter-view-PetSitProfileFragment  reason: not valid java name */
    public /* synthetic */ void m36lambda$onCreateView$2$comapplicationmypetservicesprofilepetsitterviewPetSitProfileFragment(View view) {
        if (this.profileImage.getDrawable().getConstantState() != getResources().getDrawable(R.drawable.user).getConstantState()) {
            View zoomImage = LayoutInflater.from(getContext()).inflate(R.layout.zoom_image, (ViewGroup) null);
            ((ImageView) zoomImage.findViewById(R.id.image)).setImageDrawable(this.profileImage.getDrawable());
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
            dialogBuilder.setView(zoomImage);
            ((ImageView) zoomImage.findViewById(R.id.close)).setOnClickListener(new PetSitProfileFragment$$ExternalSyntheticLambda6(dialogBuilder.show()));
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$3$com-application-mypet-services-profile-petsitter-view-PetSitProfileFragment  reason: not valid java name */
    public /* synthetic */ void m37lambda$onCreateView$3$comapplicationmypetservicesprofilepetsitterviewPetSitProfileFragment(View view) {
        Intent iGallery = new Intent("android.intent.action.PICK");
        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(iGallery, 1000);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$4$com-application-mypet-services-profile-petsitter-view-PetSitProfileFragment  reason: not valid java name */
    public /* synthetic */ void m38lambda$onCreateView$4$comapplicationmypetservicesprofilepetsitterviewPetSitProfileFragment(PetSitProfilePresenter presenter, View view) {
        Bitmap imageBitmap = null;
        if (getImageUri() != null) {
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), getImageUri());
            } catch (IOException e) {
                Log.e("IO Error: ", e.getMessage());
            }
        }
        presenter.savePhoto(new SavePhotoInfo(this.user, imageBitmap));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$7$com-application-mypet-services-profile-petsitter-view-PetSitProfileFragment  reason: not valid java name */
    public /* synthetic */ void m40lambda$onCreateView$7$comapplicationmypetservicesprofilepetsitterviewPetSitProfileFragment(View view) {
        View confirmView = LayoutInflater.from(getContext()).inflate(R.layout.delete_photo_confirm, (ViewGroup) null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        dialogBuilder.setView(confirmView);
        AlertDialog dialog = dialogBuilder.show();
        ((Button) confirmView.findViewById(R.id.yes_button)).setOnClickListener(new PetSitProfileFragment$$ExternalSyntheticLambda0(this, dialog));
        ((Button) confirmView.findViewById(R.id.no_button)).setOnClickListener(new PetSitProfileFragment$$ExternalSyntheticLambda5(dialog));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$5$com-application-mypet-services-profile-petsitter-view-PetSitProfileFragment  reason: not valid java name */
    public /* synthetic */ void m39lambda$onCreateView$5$comapplicationmypetservicesprofilepetsitterviewPetSitProfileFragment(AlertDialog dialog, View view1) {
        this.profileImage.setImageDrawable(getResources().getDrawable(R.drawable.user));
        setImageUri((Uri) null);
        dialog.dismiss();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$8$com-application-mypet-services-profile-petsitter-view-PetSitProfileFragment  reason: not valid java name */
    public /* synthetic */ void m41lambda$onCreateView$8$comapplicationmypetservicesprofilepetsitterviewPetSitProfileFragment(View view) {
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, PersonalInfoFragment.newInstance(this.user)).addToBackStack(PET_SIT_PROFILE_FRAGMENT_TAG).commit();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$9$com-application-mypet-services-profile-petsitter-view-PetSitProfileFragment  reason: not valid java name */
    public /* synthetic */ void m42lambda$onCreateView$9$comapplicationmypetservicesprofilepetsitterviewPetSitProfileFragment(View view) {
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, FavoritesPetSitFragment.newInstance(this.user)).addToBackStack(PET_SIT_PROFILE_FRAGMENT_TAG).commit();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$10$com-application-mypet-services-profile-petsitter-view-PetSitProfileFragment  reason: not valid java name */
    public /* synthetic */ void m32lambda$onCreateView$10$comapplicationmypetservicesprofilepetsitterviewPetSitProfileFragment(View view) {
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, PersonalAdsFragment.newInstance()).addToBackStack(PET_SIT_PROFILE_FRAGMENT_TAG).commit();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$11$com-application-mypet-services-profile-petsitter-view-PetSitProfileFragment  reason: not valid java name */
    public /* synthetic */ void m33lambda$onCreateView$11$comapplicationmypetservicesprofilepetsitterviewPetSitProfileFragment(View view) {
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, CaredPetsFragment.newInstance(this.user)).addToBackStack(PET_SIT_PROFILE_FRAGMENT_TAG).commit();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$12$com-application-mypet-services-profile-petsitter-view-PetSitProfileFragment  reason: not valid java name */
    public /* synthetic */ void m34lambda$onCreateView$12$comapplicationmypetservicesprofilepetsitterviewPetSitProfileFragment(View view) {
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, ServicesFragment.newInstance(this.user)).addToBackStack(PET_SIT_PROFILE_FRAGMENT_TAG).commit();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreateView$13$com-application-mypet-services-profile-petsitter-view-PetSitProfileFragment  reason: not valid java name */
    public /* synthetic */ void m35lambda$onCreateView$13$comapplicationmypetservicesprofilepetsitterviewPetSitProfileFragment(View view) {
        startActivity(new Intent(getActivity(), LoginActivity.class));
        requireActivity().finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 1000 && data != null) {
            setImageUri(data.getData());
            this.profileImage.setImageURI(getImageUri());
        }
    }

    public void showSaveProgressbar() {
        this.saveProgressBar.setVisibility(0);
        this.savePhoto.setVisibility(8);
    }

    public void hideSaveProgressbar() {
        this.saveProgressBar.setVisibility(8);
    }

    public void hideLoadProgressbar() {
        this.loadProgressBar.setVisibility(8);
    }

    public void onStoreSuccess() {
        Toast.makeText(getContext(), "Profile Image saved", 0).show();
        this.savePhoto.setVisibility(0);
    }

    public void onLoadProfileSuccess(LoadProfileInfo loadProfileInfo) {
        try {
            if (loadProfileInfo.getImage() != null) {
                this.profileImage.setImageBitmap(loadProfileInfo.getImage());
            } else {
                this.profileImage.setImageDrawable(getResources().getDrawable(R.drawable.user));
            }
            this.editPhoto.setVisibility(0);
            this.savePhoto.setVisibility(0);
            this.deletePhoto.setVisibility(0);
            this.likes.setText(String.valueOf(loadProfileInfo.getNumLikes()));
            this.dislikes.setText(String.valueOf(loadProfileInfo.getNumDislikes()));
        } catch (IllegalStateException e) {
        }
    }

    public void onStoreFailed(String message) {
        Toast.makeText(getContext(), message, 0).show();
        this.savePhoto.setVisibility(0);
    }

    public void onLoadFailed(String message) {
        Toast.makeText(getContext(), message, 0).show();
    }

    public Uri getImageUri() {
        return this.imageUri;
    }

    public void setImageUri(Uri imageUri2) {
        this.imageUri = imageUri2;
    }
}
