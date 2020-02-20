package com.w3engineers.ecommerce.bootic.ui.dashboard;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BasePresenter;
import com.w3engineers.ecommerce.bootic.data.helper.response.AddFavouriteResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.MainProductResponse;
import com.w3engineers.ecommerce.bootic.data.provider.retrofit.RetrofitClient;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashBoardPresenter extends BasePresenter<DashboardMvpView> {
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
                    if (response.body() != null) {
                        getMvpView().onProductListSuccess(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MainProductResponse> call, @NonNull Throwable t) {
                    getMvpView().onProductListError(t.getMessage());
                }
            });

        } else {
            getMvpView().onProductListError("");
        }
    }

    /**
     * add favourite to clicked product
     *
     * @param context context
     * @param itemId item id
     * @param userId user id
     */
    public void getAddFavouriteResponse(Context context, String itemId, String userId) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().addFavourite(Constants.ServerUrl.API_TOKEN, itemId, userId).enqueue(new Callback<AddFavouriteResponse>() {
                @Override
                public void onResponse(@NonNull Call<AddFavouriteResponse> call, @NonNull retrofit2.Response<AddFavouriteResponse> response) {
                    if (response.body() != null) {
                        Log.i("rr", response.body().toString());
                        getMvpView().onFavSuccess(response.body());
                    }

                }

                @Override
                public void onFailure(@NonNull Call<AddFavouriteResponse> call, @NonNull Throwable t) {
                    getMvpView().onFavError(t.getMessage());
                }
            });
        } else {
            getMvpView().onFavError(context.getResources().getString(R.string.check_net_connection));
        }
    }

    /**
     * remove favourite
     *
     * @param context context
     * @param itemId  item id
     * @param userId user id
     */
    public void getRemoveFavouriteResponse(Context context, String itemId, String userId) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().removeFavourite(Constants.ServerUrl.API_TOKEN, itemId, userId).enqueue(new Callback<AddFavouriteResponse>() {
                @Override
                public void onResponse(@NonNull Call<AddFavouriteResponse> call, @NonNull Response<AddFavouriteResponse> response) {
                    if (response.body() != null) {
                        getMvpView().onRemoveFavSuccess(response.body());
                    }

                }

                @Override
                public void onFailure(@NonNull Call<AddFavouriteResponse> call, @NonNull Throwable t) {
                    getMvpView().onFavError(t.getMessage());
                }
            });
        } else {
            getMvpView().onFavError(context.getResources().getString(R.string.check_net_connection));
        }
    }
}
