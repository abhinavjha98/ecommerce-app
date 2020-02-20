package com.w3engineers.ecommerce.bootic.ui.emailverification;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Patterns;
import android.widget.Toast;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BasePresenter;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserRegistrationResponse;
import com.w3engineers.ecommerce.bootic.data.provider.retrofit.RetrofitClient;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;



import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailVerificationPresenter extends BasePresenter<EmailVerificationMvpView> {

    /**
     * checking email validation
     */
    public boolean emailValidity(String text) {
        return Patterns.EMAIL_ADDRESS.matcher(text).matches();
    }

    /**
     * getting email varification response from server
     *
     * @param email email
     * @param pinCode pin Code
     * @param context context
     */
    public void doEmailVerification(final String email
            , final String pinCode
            , final Context context) {


        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().emailVerification(Constants.ServerUrl.API_TOKEN, email, pinCode).enqueue(new Callback<UserRegistrationResponse>() {
                @Override
                public void onResponse(@NonNull Call<UserRegistrationResponse> call, @NonNull Response<UserRegistrationResponse> response) {
                    if (response.body() != null) {
                        getMvpView().onEmailVerificationSuccess(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserRegistrationResponse> call, @NonNull Throwable t) {
                    getMvpView().onEmailVeridicationError(t.getMessage());
                }
            });
        } else {
            getMvpView().onEmailVeridicationError(context.getResources().getString(R.string.check_net_connection));
        }
    }

    /**
     * resend code to get pin number
     *
     * @param email email
     * @param context context
     */
    public void resendCode(final String email, final Context context) {

        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().resendCode(Constants.ServerUrl.API_TOKEN, email).enqueue(new Callback<UserRegistrationResponse>() {
                @Override
                public void onResponse(@NonNull Call<UserRegistrationResponse> call, @NonNull Response<UserRegistrationResponse> response) {
                    if (response.body() != null) {
                        getMvpView().onResendCodeSuccess(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserRegistrationResponse> call, @NonNull Throwable t) {
                    getMvpView().onResendCodeError(t.getMessage());
                }
            });
        } else {
            getMvpView().onResendCodeError(context.getResources().getString(R.string.check_net_connection));
        }


    }
}
