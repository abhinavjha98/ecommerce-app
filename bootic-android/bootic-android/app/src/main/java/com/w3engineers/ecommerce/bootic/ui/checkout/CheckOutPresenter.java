package com.w3engineers.ecommerce.bootic.ui.checkout;

import android.content.Context;
import androidx.annotation.NonNull;

import android.widget.Toast;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BasePresenter;
import com.w3engineers.ecommerce.bootic.data.helper.response.PaymentResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.SettingsResponse;
import com.w3engineers.ecommerce.bootic.data.provider.retrofit.RetrofitClient;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;
import com.w3engineers.ecommerce.bootic.data.util.SharedPref;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOutPresenter extends BasePresenter<CheckOutMvpView> {

    /**
     * Getting payment nonce from server
     *
     * @param context context
     * @param paymentNonce payment Nonce
     * @param Amounts Amounts
     */
    void sendPaymentNonceToServer(Context context, String paymentNonce, String Amounts) {

        String sandBox = SharedPref.getSharedPref(context).read(Constants.Preferences.ENVIRONMENT);
        String merchant = SharedPref.getSharedPref(context).read(Constants.Preferences.MERCHANT_ID);
        String publicID = SharedPref.getSharedPref(context).read(Constants.Preferences.PUBLIC_KEY);
        String privateID = SharedPref.getSharedPref(context).read(Constants.Preferences.PRIVATE_KEY);
        if (NetworkHelper.hasNetworkAccess(context)) {
        RetrofitClient.getApiService().paymentNonce(paymentNonce, Amounts, sandBox, merchant, publicID, privateID).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,@NonNull Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        getMvpView().onPaymentNonceSuccess(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call,@NonNull Throwable t) {
                getMvpView().onPaymentNonceError(t.getMessage());

            }
        });  } else {
            getMvpView().onPaymentNonceError(context.getResources().getString(R.string.check_net_connection));
        }
    }

    /**
     *  this api is used to confirm payment
     * @param context context
     * @param secretData secretData
     */
    public void doPayment(final Context context, final String secretData) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().doPayment( secretData).enqueue(new Callback<PaymentResponse>() {

                @Override
                public void onResponse(@NonNull Call<PaymentResponse> call, @NonNull Response<PaymentResponse> response) {
                    if (response.body() != null) {
                        getMvpView().allPaymentSuccess(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PaymentResponse> call, @NonNull Throwable t) {
                    getMvpView().allPaymentError(t.getMessage());
                }
            });
        } else {
            getMvpView().allPaymentError(context.getResources().getString(R.string.check_net_connection));
        }
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
                public void onResponse(@NonNull  Call<SettingsResponse> call,@NonNull Response<SettingsResponse> response) {
                    if(response.body() != null){
                        if (getMvpView() != null){
                            getMvpView().onSettingCredentialSuccess(response.body());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<SettingsResponse> call,@NonNull Throwable t) {
                   getMvpView().onSettingCredentialError(t.getMessage());
                }
            });

        } else {
            getMvpView().onSettingCredentialError(context.getResources().getString(R.string.check_net_connection));
        }
    }
}
