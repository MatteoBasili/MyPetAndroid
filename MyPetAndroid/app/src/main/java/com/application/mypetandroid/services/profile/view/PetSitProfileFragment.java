package com.application.mypetandroid.services.profile.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.application.mypetandroid.R;
import com.application.mypetandroid.login.view.LoginActivity;
import com.application.mypetandroid.services.profile.PetSitProfilePresenter;
import com.application.mypetandroid.services.profile.ProfileContract;
import com.application.mypetandroid.services.profile.data.PetSitProfileInfo;
import com.application.mypetandroid.utils.singleton_examples.ZoomSingletonClass;

import org.apache.log4j.Logger;

import java.io.IOException;

public class PetSitProfileFragment extends Fragment implements ProfileContract.PetSitProfileView {
    private static final String ARG_PARAM1 = "User";
    private static final Logger logger = Logger.getLogger(PetSitProfileFragment.class);
    private static final String PET_SIT_PROFILE_FRAGMENT_TAG = "petSitProfileFragmentTag";
    private ImageView deletePhoto;
    private TextView dislikes;
    private ImageView editPhoto;
    private TextView likes;
    private ProgressBar loadProgressBar;
    private ImageView profileImage;
    private ImageView defaultProfileImage;
    private ImageView savePhoto;
    private ProgressBar saveProgressBar;
    private String user;
    private PetSitProfilePresenter presenter;
    private ActivityResultLauncher<Intent> galleryActivity;
    private Bitmap currentImage;

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

        View personalInfo = profileView.findViewById(R.id.personal_info_view);
        View favorites = profileView.findViewById(R.id.favorites_view);
        View ads = profileView.findViewById(R.id.ads_view);
        View caredPets = profileView.findViewById(R.id.cared_pets_view);
        View services = profileView.findViewById(R.id.services_view);
        saveProgressBar = profileView.findViewById(R.id.save_progressBar);
        loadProgressBar = profileView.findViewById(R.id.load_progressBar);
        profileImage = profileView.findViewById(R.id.photo_profile);
        defaultProfileImage = profileView.findViewById(R.id.default_photo_profile);
        editPhoto = profileView.findViewById(R.id.change_photo);
        savePhoto = profileView.findViewById(R.id.save_photo);
        deletePhoto = profileView.findViewById(R.id.delete_photo);
        likes = profileView.findViewById(R.id.likes_numb);
        dislikes = profileView.findViewById(R.id.dislikes_numb);
        ImageView logoutIcon = profileView.findViewById(R.id.logout);
        TextView usernameView = profileView.findViewById(R.id.user);

        // Set the user username
        usernameView.setText(this.user);

        presenter = new PetSitProfilePresenter(this);

        profileImage.setOnClickListener(v -> zoomImage());
        editPhoto.setOnClickListener(v -> editPhoto());
        savePhoto.setOnClickListener(v -> savePhoto());
        deletePhoto.setOnClickListener(v -> showDeletePhotoConfirmation());
        personalInfo.setOnClickListener(v -> showPersonalInformations());
        favorites.setOnClickListener(v -> showFavorites());
        ads.setOnClickListener(v -> showUserAds());
        caredPets.setOnClickListener(v -> showCaredPets());
        services.setOnClickListener(v -> showServices());
        logoutIcon.setOnClickListener(v -> logout());

        // To take photo from gallery
        galleryActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == -1 && result.getData() != null) {
                        try {
                        Intent data = result.getData();
                        Uri imageUri = data.getData();
                        currentImage = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                        profileImage.setImageBitmap(currentImage);
                        profileImage.setVisibility(View.VISIBLE);
                        defaultProfileImage.setVisibility(View.GONE);
                        } catch (IOException e) {
                            logger.error("IO Error: ", e);
                        }
                    }
                });

        currentImage = null;
        // Load profile
        loadProfile();

        return profileView;
    }

    private void loadProfile() {
        presenter.loadProfile(user);
    }

    private void zoomImage() {
        ZoomSingletonClass zoom = ZoomSingletonClass.getSingletonInstance();
        zoom.setContext(getContext());
        zoom.setImage(currentImage);
        zoom.zoomImage();
    }

    private void editPhoto() {
        Intent iGallery = new Intent("android.intent.action.PICK");
        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryActivity.launch(iGallery);
    }

    private void savePhoto() {
        presenter.savePhoto(user, currentImage);
    }

    private void showDeletePhotoConfirmation() {
        View confirmView = LayoutInflater.from(getContext()).inflate(R.layout.delete_photo_confirm, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        dialogBuilder.setView(confirmView);
        AlertDialog dialog = dialogBuilder.show();
        (confirmView.findViewById(R.id.yes_button)).setOnClickListener(v -> {
            deletePhoto();
            dialog.dismiss();
        });
        (confirmView.findViewById(R.id.no_button)).setOnClickListener(v -> dialog.dismiss());
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void deletePhoto() {
        this.defaultProfileImage.setVisibility(View.VISIBLE);
        this.profileImage.setVisibility(View.GONE);
        currentImage = null;
    }

    private void showPersonalInformations() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, PersonalInfoFragment.newInstance(this.user))
                .addToBackStack(PET_SIT_PROFILE_FRAGMENT_TAG)
                .commit();
    }

    private void showFavorites() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, FavoritesPetSitFragment.newInstance(this.user))
                .addToBackStack(PET_SIT_PROFILE_FRAGMENT_TAG)
                .commit();
    }

    private void showUserAds() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, PersonalAdsFragment.newInstance())
                .addToBackStack(PET_SIT_PROFILE_FRAGMENT_TAG)
                .commit();
    }

    private void showCaredPets() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, CaredPetsFragment.newInstance(this.user))
                .addToBackStack(PET_SIT_PROFILE_FRAGMENT_TAG)
                .commit();
    }

    private void showServices() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, ServicesFragment.newInstance(this.user))
                .addToBackStack(PET_SIT_PROFILE_FRAGMENT_TAG)
                .commit();
    }

    private void logout() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
        requireActivity().finish();
    }

    @Override
    public void showSaveProgressIndicator() {
        this.saveProgressBar.setVisibility(View.VISIBLE);
        this.savePhoto.setVisibility(View.GONE);
    }

    @Override
    public void hideSaveProgressIndicator() {
        this.saveProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void hideLoadProgressIndicator() {
        this.loadProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStorePhotoSuccess() {
        Toast.makeText(getContext(), "Profile Image saved", Toast.LENGTH_SHORT).show();
        this.savePhoto.setVisibility(View.VISIBLE);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onLoadProfileSuccess(PetSitProfileInfo petSitProfileInfo) {
        try {
            if (petSitProfileInfo.getImage() != null) {
                this.profileImage.setImageBitmap(petSitProfileInfo.getImage());
                this.profileImage.setVisibility(View.VISIBLE);
                currentImage = petSitProfileInfo.getImage();
            } else {
                this.defaultProfileImage.setVisibility(View.VISIBLE);
                currentImage = null;
            }

            // Show buttons
            this.editPhoto.setVisibility(View.VISIBLE);
            this.savePhoto.setVisibility(View.VISIBLE);
            this.deletePhoto.setVisibility(View.VISIBLE);
            this.likes.setText(String.valueOf(petSitProfileInfo.getNumLikes()));
            this.dislikes.setText(String.valueOf(petSitProfileInfo.getNumDislikes()));
        } catch (IllegalStateException ignored) {
            // ignore
        }
    }

    @Override
    public void onStorePhotoFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        this.savePhoto.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadProfileFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
