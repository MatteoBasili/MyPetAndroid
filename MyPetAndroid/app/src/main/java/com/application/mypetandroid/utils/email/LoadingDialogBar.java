package com.application.mypetandroid.utils.email;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import com.application.mypetandroid.R;

public class LoadingDialogBar {
    private final Context context;
    private Dialog dialog;

    public LoadingDialogBar(Context context) {
        this.context = context;
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
        try {
            if ((this.dialog != null) && this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
        } catch (final Exception e) {
            // ignore
        } finally {
            this.dialog = null;
        }
    }
}
