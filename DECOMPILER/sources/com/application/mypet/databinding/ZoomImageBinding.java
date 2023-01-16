package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.application.mypet.R;

public final class ZoomImageBinding implements ViewBinding {
    public final LinearLayout bottomSheetExitLinear;
    public final ImageView close;
    public final ImageView image;
    private final LinearLayout rootView;

    private ZoomImageBinding(LinearLayout rootView2, LinearLayout bottomSheetExitLinear2, ImageView close2, ImageView image2) {
        this.rootView = rootView2;
        this.bottomSheetExitLinear = bottomSheetExitLinear2;
        this.close = close2;
        this.image = image2;
    }

    public LinearLayout getRoot() {
        return this.rootView;
    }

    public static ZoomImageBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static ZoomImageBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.zoom_image, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    public static ZoomImageBinding bind(View rootView2) {
        LinearLayout bottomSheetExitLinear2 = (LinearLayout) rootView2;
        int id = R.id.close;
        ImageView close2 = (ImageView) ViewBindings.findChildViewById(rootView2, R.id.close);
        if (close2 != null) {
            id = R.id.image;
            ImageView image2 = (ImageView) ViewBindings.findChildViewById(rootView2, R.id.image);
            if (image2 != null) {
                return new ZoomImageBinding((LinearLayout) rootView2, bottomSheetExitLinear2, close2, image2);
            }
        }
        throw new NullPointerException("Missing required view with ID: ".concat(rootView2.getResources().getResourceName(id)));
    }
}
