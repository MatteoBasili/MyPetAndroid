package com.application.mypet.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import com.application.mypet.R;

public class LoadingDialogBar {
    Context context;
    Dialog dialog;

    public LoadingDialogBar(Context context2) {
        this.context = context2;
    }

    public void showDialog() {
        Dialog dialog2 = new Dialog(this.context);
        this.dialog = dialog2;
        dialog2.setContentView(R.layout.loading_dialog);
        this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        this.dialog.create();
        this.dialog.show();
    }

    public void hideDialog() {
        this.dialog.dismiss();
    }
}
