package com.w3engineers.ecommerce.bootic.ui.updatepassword;

import android.content.Context;
import androidx.annotation.NonNull;
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

public class UpdatePasswordPresenter extends BasePresenter<UpdatePasswordMvpView> {
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
     * hitting server to update password
     *
     * @param email email
     * @param verifyCode verify Code
     * @param password password
     * @param context context
     */
    public void updatePassword(final String email, final String verifyCode, final String password, final Context context) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().updatePassword(Constants.ServerUrl.API_TOKEN, email,verifyCode,password)
                    .enqueue(new Callback<UserRegistrationResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<UserRegistrationResponse> call,@NonNull Response<UserRegistrationResponse> response) {
                            if(response.body() != null){
                                getMvpView().onPasswordUpdateSuccess(response.body());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<UserRegistrationResponse> call,@NonNull Throwable t) {
                            getMvpView().onPasswordUpdateError(t.getMessage());
                        }
                    });
        } else {
            getMvpView().onPasswordUpdateError(context.getResources().getString(R.string.check_net_connection));
        }


    }
}
