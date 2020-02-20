package com.w3engineers.ecommerce.bootic.ui.myfavourite;

import android.content.Context;
import androidx.annotation.NonNull;
import android.widget.Toast;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BasePresenter;
import com.w3engineers.ecommerce.bootic.data.helper.response.ProductGridResponse;
import com.w3engineers.ecommerce.bootic.data.provider.retrofit.RetrofitClient;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFavPresenter extends BasePresenter<UserFavMvpView> {

    /**
     * getting all favourite items of user
     * @param context context
     * @param page page
     * @param userId user id
     */
    public void getAllFavouriteResponse(Context context, String page, String userId) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().allFavourite(Constants.ServerUrl.API_TOKEN, page, userId).enqueue(new Callback<ProductGridResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductGridResponse> call, @NonNull Response<ProductGridResponse> response) {
                    if (response.body() != null) {
                        getMvpView().onGettingFavouriteSuccess(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductGridResponse> call, @NonNull Throwable t) {
                    getMvpView().onGettingFavouriteError(t.getMessage(),true);
                }
            });
        } else {
            getMvpView().onGettingFavouriteError(context.getResources().getString(R.string.check_net_connection),false);
        }
    }

}
