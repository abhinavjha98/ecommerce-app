package com.w3engineers.ecommerce.bootic.ui.userRegistration;

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

public class RegisterPresenter extends BasePresenter<RegisterMvpView> {


    /**
     * checking name validation
     */
    public boolean nameValidity(String text) {
        return text.length() >= Constants.DefaultValue.MINIMUM_LENGTH_TEXT
                && text.length() <= Constants.DefaultValue.MAXIMUM_LENGTH;
    }

    /**
     * checking email validation
     */
    public boolean emailValidity(String text) {
        return Patterns.EMAIL_ADDRESS.matcher(text).matches();
    }

    /**
     * checking password validation
     */
    public boolean passwordValidity(String text) {
        return text.length() >= Constants.DefaultValue.MINIMUM_LENGTH_PASS
                && text.length() <= Constants.DefaultValue.MAXIMUM_LENGTH;
    }

    /**
     * checking password and confirm password validation
     */
    public boolean isConfirmPasswordMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    /**
     * this api is used  to sign up with email
     * @param name name
     * @param email email
     * @param password password
     * @param context context
     */
    public void userRegistration(final String name
            , final String email
            , final String password
            , final Context context) {



        if(NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().socialRegistration(Constants.ServerUrl.API_TOKEN,
                    "" + Constants.RegistrationType.MANUAL_SIGN_UP, "", name, email, password).enqueue(new Callback<UserRegistrationResponse>() {
                @Override
                public void onResponse(@NonNull Call<UserRegistrationResponse> call, @NonNull Response<UserRegistrationResponse> response) {
                    if (response.body() != null)
                        getMvpView().onSignUpSuccess(response.body());
                }

                @Override
                public void onFailure(@NonNull Call<UserRegistrationResponse> call,@NonNull Throwable t) {
                    getMvpView().onSignUpError(t.getMessage());
                }
            });

        }else{
            getMvpView().onSignUpError(context.getResources().getString(R.string.check_net_connection));
        }
    }
}
