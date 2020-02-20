package com.w3engineers.ecommerce.bootic.data.util;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.w3engineers.ecommerce.bootic.R;


public class Loader extends AlertDialog {

    public Loader(@NonNull Context context) {
        super(context);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // getWindow().setLayout(20,20);
    }

    @Override
    public void show() {
        super.show();
        setContentView(R.layout.loader_layout);
    }

    @Override
    public void cancel() {
        super.cancel();
    }

    public void stopLoader() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, Constants.DefaultValue.LOADER_DELAY);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
