package com.application.mypetandroid.utils.singleton_examples;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.application.mypetandroid.R;

public class ZoomSingletonClass {

    @SuppressLint("StaticFieldLeak")
    private static ZoomSingletonClass instance = null;

    private Context context;
    private Bitmap image;

    protected ZoomSingletonClass() {

    }

    public static synchronized ZoomSingletonClass getSingletonInstance() {
        if (ZoomSingletonClass.instance == null)
            ZoomSingletonClass.instance = new ZoomSingletonClass();
        return instance;
    }

    public void zoomImage() {
        View zoomImage = LayoutInflater.from(getContext()).inflate(R.layout.zoom_image, null);
        ((ImageView) zoomImage.findViewById(R.id.image)).setImageBitmap(getImage());
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(zoomImage);
        AlertDialog dialog = dialogBuilder.show();
        (zoomImage.findViewById(R.id.close)).setOnClickListener(v -> dialog.dismiss());
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
