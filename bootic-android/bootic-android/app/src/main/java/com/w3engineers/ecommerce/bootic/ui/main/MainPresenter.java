package com.w3engineers.ecommerce.bootic.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import com.w3engineers.ecommerce.bootic.data.helper.base.BasePresenter;
import com.w3engineers.ecommerce.bootic.data.helper.response.AdMobResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.SettingsResponse;
import com.w3engineers.ecommerce.bootic.data.provider.retrofit.RetrofitClient;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;
import com.w3engineers.ecommerce.bootic.data.util.SharedPref;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPresenter extends BasePresenter<MainMvpView> {

    /**
     * getting admob credential from server
     *
     * @param context
     */
    public void getAdMobCredential(final Activity context) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().getAdMobResponse(Constants.ServerUrl.API_TOKEN).enqueue(new Callback<AdMobResponse>() {
                @Override
                public void onResponse(@NonNull Call<AdMobResponse> call, @NonNull Response<AdMobResponse> response) {
                    if (response.body() != null) {
                        if (response.body().adMobModel != null) {
                            SharedPref.getSharedPref(context).write(Constants.Preferences.BANNER_STATUS, response.body().adMobModel.bannerStatus);
                            SharedPref.getSharedPref(context).write(Constants.Preferences.BANNER_APP_ID, response.body().adMobModel.bannerId);
                            SharedPref.getSharedPref(context).write(Constants.Preferences.BANNER_UNIT_ID, response.body().adMobModel.bannerUnitId);
                            SharedPref.getSharedPref(context).write(Constants.Preferences.INTERSTITIAL_STATUS, response.body().adMobModel.interstitialStatus);
                            SharedPref.getSharedPref(context).write(Constants.Preferences.INTERSTITIAL_APP_ID, response.body().adMobModel.interstitialId);
                            SharedPref.getSharedPref(context).write(Constants.Preferences.INTERSTITIAL_UNIT_ID, response.body().adMobModel.interstitialUnitId);
                            setInterstitialApp(context);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AdMobResponse> call, @NonNull Throwable t) {
                }
            });

        } else Toast.makeText(context, "Could not connect to internet.", Toast.LENGTH_SHORT).show();
    }

    private InterstitialAd mInterstitialAd;

    /**
     * setting interstitial ad property
     *
     * @param activity
     */
    private void setInterstitialApp(Activity activity) {
        String status = SharedPref.getSharedPref(activity).read(Constants.Preferences.INTERSTITIAL_STATUS);
        String AppID = SharedPref.getSharedPref(activity).read(Constants.Preferences.INTERSTITIAL_APP_ID);
        if (status.equals(Constants.Preferences.STATUS_ON)) {
            if (AppID != null) {
                try {
                    ApplicationInfo applicationInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
                    applicationInfo.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", AppID);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                MobileAds.initialize(activity,
                        AppID);
                prepareAd(activity);
                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.scheduleAtFixedRate(new Runnable() {
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                if (mInterstitialAd.isLoaded()) {
                                    mInterstitialAd.show();
                                } else {
                                    Log.d("TAG", " Interstitial not loaded");
                                }
                                prepareAd(activity);
                            }
                        });
                    }
                }, 10, 10, TimeUnit.MINUTES);
            }
        }
    }

    /**
     * init interstitial ad
     *
     * @param activity activity
     */
    private void prepareAd(Activity activity) {
        String unitID = SharedPref.getSharedPref(activity).read(Constants.Preferences.INTERSTITIAL_UNIT_ID);
        mInterstitialAd = new InterstitialAd(activity);

        mInterstitialAd.setAdUnitId(unitID);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }



}
