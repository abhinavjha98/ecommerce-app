package com.w3engineers.ecommerce.bootic.ui.splashScreen;

import android.content.Context;
import androidx.annotation.NonNull;

import com.w3engineers.ecommerce.bootic.data.helper.base.BasePresenter;
import com.w3engineers.ecommerce.bootic.data.helper.response.MainProductResponse;
import com.w3engineers.ecommerce.bootic.data.provider.retrofit.RetrofitClient;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashPresenter extends BasePresenter<SplashMvpView> {
    /**
     * getting all product from server
     *
     * @param context context
     * @param pageNumber page Number
     * @param userID user ID
     * @param existing existing
     */
    public void settingProductPagination(final Context context, final String pageNumber, final String userID, final String existing) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().getProductMain(Constants.ServerUrl.API_TOKEN, pageNumber, existing, userID).enqueue(new Callback<MainProductResponse>() {
                @Override
                public void onResponse(@NonNull Call<MainProductResponse> call, @NonNull Response<MainProductResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (getMvpView() !=null){
                                getMvpView().onProductListSuccess(response.body());
                            }

                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MainProductResponse> call, @NonNull Throwable t) {
                    getMvpView().onProductListError(t.getMessage());
                }
            });

        }
    }

}
