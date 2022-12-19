package com.application.mypet.services;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.application.mypet.R;
import com.application.mypet.login.view.LoginActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "User";

    private final int GALLERY_REQ_CODE = 1000;
    ImageView profileImage;
    ImageView defaultProfileImage;

    // TODO: Rename and change types of parameters
    private String user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user Parameter 1.
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance(String user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View profileView = inflater.inflate(R.layout.fragment_profile, container, false);
        View logout = profileView.findViewById(R.id.logout_view);

        TextView username = profileView.findViewById(R.id.user);
        username.setText(user);
        defaultProfileImage = profileView.findViewById(R.id.default_photo);
        profileImage = profileView.findViewById(R.id.photo_profile);
        ImageView editPhoto = profileView.findViewById(R.id.change_photo);
        ImageView deletePhoto = profileView.findViewById(R.id.delete_photo);

        // Function to zoom the profile image when clicked
        profileImage.setOnClickListener(view -> {
            View zoomImage = LayoutInflater.from(getContext()).inflate(R.layout.zoom_image, null);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"})
            ImageView image = zoomImage.findViewById(R.id.image);
            ImageView close = zoomImage.findViewById(R.id.close);
            image.setImageDrawable(profileImage.getDrawable());

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
            dialogBuilder.setView(zoomImage);
            AlertDialog dialog = dialogBuilder.show();

            close.setOnClickListener(view1 -> dialog.dismiss());

        });

        // Function when android system button is clicked
        deletePhoto.setOnClickListener(view -> {
            View confirmView = LayoutInflater.from(getContext()).inflate(R.layout.delete_photo_confirm, null);
            Button yes = confirmView.findViewById(R.id.yes_button);
            Button no = confirmView.findViewById(R.id.no_button);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
            dialogBuilder.setView(confirmView);
            AlertDialog dialog = dialogBuilder.show();

            // If Yes, delete photo
            yes.setOnClickListener(view1 -> {
                defaultProfileImage.setVisibility(View.VISIBLE);
                profileImage.setVisibility(View.GONE);
                dialog.dismiss();
            });
            // If No, continue
            no.setOnClickListener(view1 -> dialog.dismiss());
        });

        editPhoto.setOnClickListener(view -> {

            Intent iGallery = new Intent(Intent.ACTION_PICK);
            iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(iGallery, GALLERY_REQ_CODE);

        });

        logout.setOnClickListener(view -> {

            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();

        });

        return profileView;

    }

    // Function to upload the profile image
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == GALLERY_REQ_CODE) {
                // for Gallery

                assert data != null;
                profileImage.setImageURI(data.getData());
                profileImage.setVisibility(View.VISIBLE);
                defaultProfileImage.setVisibility(View.GONE);

            }

        }
    }

}