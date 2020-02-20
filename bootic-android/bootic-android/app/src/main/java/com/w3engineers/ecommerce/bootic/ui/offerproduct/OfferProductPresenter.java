package com.w3engineers.ecommerce.bootic.ui.offerproduct;

import android.content.Context;
import androidx.annotation.NonNull;
import android.widget.Toast;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BasePresenter;
import com.w3engineers.ecommerce.bootic.data.helper.response.AddFavouriteResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.ProductGridResponse;
import com.w3engineers.ecommerce.bootic.data.provider.retrofit.RetrofitClient;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfferProductPresenter extends BasePresenter<OfferProductMvpView> {


    public void getOfferProduct(Context context, String tag, String pageNumber) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().getOfferProductList(Constants.ServerUrl.API_TOKEN,
                    tag, pageNumber).enqueue(new Callback<ProductGridResponse>() {
                @Override
                public void onResponse(@NonNull  Call<ProductGridResponse> call,
                                       @NonNull Response<ProductGridResponse> response) {
                    if (response.body() != null && !response.body().dataModel.isEmpty()) {
                        getMvpView().onOfferProductSuccess(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductGridResponse> call,@NonNull Throwable t) {
                    getMvpView().onOfferProductError(t.getMessage());
                }
            });
        }else {
            getMvpView().onOfferProductError(context.getResources().getString(R.string.check_net_connection));
        }



    }

    /**
     * add favourite to product
     *
     * @param context context
     * @param itemId item id
     * @param userId user id
     */
    public void getAddFavouriteResponse(Context context, String itemId, String userId) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().addFavourite(Constants.ServerUrl.API_TOKEN, itemId, userId).enqueue(new Callback<AddFavouriteResponse>() {
                @Override
                public void onResponse(@NonNull Call<AddFavouriteResponse> call, @NonNull Response<AddFavouriteResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
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
     * @param itemId item id
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
