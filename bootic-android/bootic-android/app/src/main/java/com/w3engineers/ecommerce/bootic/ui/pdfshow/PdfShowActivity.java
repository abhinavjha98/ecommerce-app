package com.w3engineers.ecommerce.bootic.ui.pdfshow;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BaseActivity;
import com.w3engineers.ecommerce.bootic.data.helper.models.ProductDetailsImageModel;
import com.w3engineers.ecommerce.bootic.databinding.ActivityPdfShowBinding;
import com.w3engineers.ecommerce.bootic.ui.ImageReviewSlider.ImageReviewSliderActivity;
import com.w3engineers.ecommerce.bootic.ui.invoice.InvoiceActivity;
import com.w3engineers.ecommerce.bootic.ui.main.MainActivity;

import java.io.File;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

public class PdfShowActivity extends BaseActivity<PdfShowMvpView,PdfShowPresenter> {
    ActivityPdfShowBinding mBinding;
    private static File mFileUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pdf_show;
    }

    /**
     * Run Activity
     * @param context mActivity
     */
    public static void runActivity(Context context, File fileLink) {
        Intent intent = new Intent(context, PdfShowActivity.class);
        mFileUrl=fileLink;
        runCurrentActivity(context, intent);
    }


    @Override
    protected void startUI() {
        mBinding= (ActivityPdfShowBinding) getViewDataBinding();
        initToolbar();


        Uri uri = FileProvider.getUriForFile(PdfShowActivity.this,
                this.getApplicationContext().getPackageName()+ ".provider", mFileUrl);
        showNotification(this.getResources().getString(R.string.pdf_com),this.getResources().getString(R.string.success));
        mBinding.pdfView.fromUri(uri).pages(0, 2, 1, 3, 3, 3)
                .enableSwipe(true)
                .enableDoubletap(true)
                .defaultPage(1)
                .enableAnnotationRendering(false)
                .password(null)
                .load();
    }



    public void showNotification(String title, String message) {

        if (getOpenFileIntent()!=null){
            Intent intent = getOpenFileIntent();
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            String channelId ="1111";
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }else {
            String channelId ="1111";
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }



    }

    public  Intent getOpenFileIntent() {
        Intent intent=null;
        PackageManager packageManager = getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = FileProvider.getUriForFile(PdfShowActivity.this,
                    this.getApplicationContext().getPackageName()+ ".provider", mFileUrl);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            return intent;
        } else {
            Toast.makeText(this, getResources().getString(R.string.download_pdf_viewer), Toast.LENGTH_SHORT).show();
            return null;
        }
    }



    private void initToolbar() {
        TextView toobarTitle = findViewById(R.id.toolbar_title);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(this.getString(R.string.invoice_pdf));
        toobarTitle.setText("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    @Override
    protected void stopUI() {

    }

    @Override
    protected PdfShowPresenter initPresenter() {
        return new PdfShowPresenter();
    }

}
