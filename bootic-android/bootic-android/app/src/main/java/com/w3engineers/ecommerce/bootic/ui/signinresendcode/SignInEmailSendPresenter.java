package com.w3engineers.ecommerce.bootic.ui.signinresendcode;

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

public class SignInEmailSendPresenter extends BasePresenter<SignInEmailSendMvpView> {

    /**
     * checking email validation
     */
    public boolean emailValidity(String text) {
        return Patterns.EMAIL_ADDRESS.matcher(text).matches();
    }

    /**
     * resend code to email for getting pin code
     * @param email email
     * @param context context
     */
    public void resendCode(final String email, final Context context) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().resendCode(Constants.ServerUrl.API_TOKEN, email).enqueue(new Callback<UserRegistrationResponse>() {
                @Override
                public void onResponse(@NonNull Call<UserRegistrationResponse> call, @NonNull Response<UserRegistrationResponse> response) {
                    if (response.body() != null) {
                        getMvpView().onGetCodeSuccess(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserRegistrationResponse> call, @NonNull Throwable t) {
                    getMvpView().onGetCodeError(t.getMessage());
                }
            });
        } else {
            getMvpView().onGetCodeError(context.getResources().getString(R.string.check_net_connection));
        }

    }
}
