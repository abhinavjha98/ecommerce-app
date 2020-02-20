package com.w3engineers.ecommerce.bootic.ui.splashScreen;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.droidnet.DroidListener;
import com.droidnet.DroidNet;
import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BaseActivity;
import com.w3engineers.ecommerce.bootic.data.helper.models.Slider;
import com.w3engineers.ecommerce.bootic.data.helper.response.MainProductResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.SettingsResponse;
import com.w3engineers.ecommerce.bootic.data.provider.retrofit.RetrofitClient;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;
import com.w3engineers.ecommerce.bootic.data.util.SharedPref;
import com.w3engineers.ecommerce.bootic.ui.main.MainActivity;

import java.net.HttpURLConnection;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashActivity extends BaseActivity<SplashMvpView, SplashPresenter> implements SplashMvpView, DroidListener {

    private ImageView ivSplashImage;
    private Intent mainIntent;
    private long startTime, endTime;
    private DroidNet mDroidNet;
    private LottieAnimationView mLottieAnimationView;
    private ConstraintLayout mLayoutNoNetConnectionText;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void startUI() {
        mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        ivSplashImage = findViewById(R.id.image_view_logo);
        mLayoutNoNetConnectionText = findViewById(R.id.layout_no_net_connection_text);
        mLottieAnimationView = findViewById(R.id.view_loading_star);
        Animation transition = AnimationUtils.loadAnimation(this, R.anim.transition_alfa);
        ivSplashImage.startAnimation(transition);
        startTime = System.currentTimeMillis();
      // getSettingCredential(this);
        mDroidNet = DroidNet.getInstance();
        mDroidNet.addInternetConnectivityListener(this);
    }

    /**
     * getting setting credential
     *
     * @param context context
     */
    public void getSettingCredential(final Context context) {

        if (NetworkHelper.hasNetworkAccess(context)) {

            RetrofitClient.getApiService().hitSetting(Constants.ServerUrl.API_TOKEN).enqueue(new Callback<SettingsResponse>() {
                @Override
                public void onResponse(@NonNull Call<SettingsResponse> call, @NonNull Response<SettingsResponse> response) {
                    if(response.body() != null){
                        presenter.settingProductPagination(SplashActivity.this, "1", CustomSharedPrefs.getLoggedInUserId(SplashActivity.this), "");
                        Log.d("currency",response.body().settingsModel.currencyFont);
                        CustomSharedPrefs.setCurrency(context, response.body().settingsModel.currencyFont);

                        SharedPref.getSharedPref(context).write(Constants.Preferences.TAX, response.body().settingsModel.tax);

                        SharedPref.getSharedPref(context).write(Constants.Preferences.MERCHANT_ID,
                                response.body().settingsModel.paymentModel.merchantId);
                        SharedPref.getSharedPref(context).write(Constants.Preferences.ENVIRONMENT,
                                response.body().settingsModel.paymentModel.environment);
                        SharedPref.getSharedPref(context).write(Constants.Preferences.PUBLIC_KEY,
                                response.body().settingsModel.paymentModel.publicKey);
                        SharedPref.getSharedPref(context).write(Constants.Preferences.PRIVATE_KEY,
                                response.body().settingsModel.paymentModel.privateKey);

                        SharedPref.getSharedPref(context).write(Constants.Preferences.COMPANY_NAME,
                                response.body().settingsModel.addressModel.companyName);

                        String companyAddress="";

                        if (response.body().settingsModel.addressModel.addressLine2==null){
                            companyAddress=response.body().settingsModel.addressModel.addressLine1+context.getResources().getString(R.string.coma)+
                                    response.body().settingsModel.addressModel.city+context.getResources().getString(R.string.coma)+
                                    response.body().settingsModel.addressModel.zipCode+context.getResources().getString(R.string.coma)+
                                    response.body().settingsModel.addressModel.state+context.getResources().getString(R.string.coma)+
                                    response.body().settingsModel.addressModel.country;

                        }else {
                            companyAddress=response.body().settingsModel.addressModel.addressLine1+context.getResources().getString(R.string.coma)+
                                    response.body().settingsModel.addressModel.addressLine1+context.getResources().getString(R.string.coma)+
                                    response.body().settingsModel.addressModel.city+context.getResources().getString(R.string.coma)+
                                    response.body().settingsModel.addressModel.zipCode+context.getResources().getString(R.string.coma)+
                                    response.body().settingsModel.addressModel.state+context.getResources().getString(R.string.coma)+
                                    response.body().settingsModel.addressModel.country;
                        }

                        SharedPref.getSharedPref(context).write(Constants.Preferences.COMPANY_ADDRESS,companyAddress);
                        SharedPref.getSharedPref(context).write(Constants.Preferences.CURRENCY_POSITION,
                                response.body().settingsModel.currencyPosition);

                    }
                }

                @Override
                public void onFailure(@NonNull Call<SettingsResponse> call,@NonNull Throwable t) {
                    Toast.makeText(context, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    protected void stopUI() {

    }

    @Override
    protected SplashPresenter initPresenter() {
        return new SplashPresenter();
    }

    @Override
    public void onProductListSuccess(MainProductResponse response) {
        endTime = System.currentTimeMillis();
        if (response.statusCode == HttpURLConnection.HTTP_OK) {
            response.isOkay = response.dataModel != null;
        } else {
            response.isOkay = false;
        }
        CustomSharedPrefs.setMainResponse(this, response);

        if (response.dataModel.mSliderMains!=null){
            Slider slider=new Slider();
            slider.mSliderMains=response.dataModel.mSliderMains;
            CustomSharedPrefs.setSlider(this,slider );
        }



        checkTimeToFinish(endTime);
    }

    private void checkTimeToFinish(long endTime) {
        double differenceInSec = (double) (endTime - startTime) / 1000;
        if (differenceInSec >= 2.5) {
            startActivity(mainIntent);
            finish();
        } else if (differenceInSec >= 2 && differenceInSec < 2.5) {
            new Handler().postDelayed(() -> {
                startActivity(mainIntent);
                finish();
            }, 1000);
        } else if (differenceInSec >= 0 && differenceInSec <= 1.9) {
            new Handler().postDelayed(() -> {
                startActivity(mainIntent);
                finish();
            }, 2000);
        }
    }

    @Override
    public void onProductListError(String errorMessage) {
        MainProductResponse response = new MainProductResponse();
        response.isOkay = false;
        CustomSharedPrefs.setMainResponse(this, response);
        endTime = System.currentTimeMillis();
        checkTimeToFinish(endTime);
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (isConnected) {
            //do Stuff with internet
            netIsOn();
        } else {
            //no internet
            netIsOff();
        }
    }

    private void netIsOn(){
        mLayoutNoNetConnectionText.setVisibility(View.GONE);
        mLottieAnimationView.setVisibility(View.VISIBLE);
        getSettingCredential(this);
    }

    private void netIsOff(){
        mLayoutNoNetConnectionText.setVisibility(View.VISIBLE);
        mLottieAnimationView.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
