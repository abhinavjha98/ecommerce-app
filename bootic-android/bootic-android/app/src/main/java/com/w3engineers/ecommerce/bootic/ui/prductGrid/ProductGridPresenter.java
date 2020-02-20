package com.w3engineers.ecommerce.bootic.ui.prductGrid;

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

public class ProductGridPresenter extends BasePresenter<ProductGridMvpView> {

    /**
     * getting product list by category from server
     *
     * @param categoryId category Id
     * @param context context
     * @param userId user Id
     * @param page page number
     */
    public void getProductList(final String categoryId,
                               final Context context,
                               final String userId,
                               final String page) {

        if (NetworkHelper.hasNetworkAccess(context)) {

            RetrofitClient.getApiService().getProductGridResponse(Constants.ServerUrl.API_TOKEN, categoryId,
                    userId, page).enqueue(new Callback<ProductGridResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductGridResponse> call, @NonNull Response<ProductGridResponse> response) {
                    if (response.body() != null) {
                        getMvpView().onProductListSuccess(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductGridResponse> call, @NonNull Throwable t) {
                    if (t.getMessage() !=null){
                        if (getMvpView()!=null){
                            getMvpView().onProductListError(t.getMessage());
                        }
                    }
                }
            });
        } else {
            getMvpView().onProductListError(context.getResources().getString(R.string.could_not_connect));
        }
    }

    /**
     * add favourite to product
     *
     * @param context context
     * @param itemId item Id
     * @param userId user Id
     */
    public void getAddFavouriteResponse(Context context, String itemId, String userId) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().addFavourite(Constants.ServerUrl.API_TOKEN, itemId, userId).enqueue(new Callback<AddFavouriteResponse>() {
                @Override
                public void onResponse(@NonNull Call<AddFavouriteResponse> call, @NonNull retrofit2.Response<AddFavouriteResponse> response) {
                    if (response.body() != null) {
                        getMvpView().onFavSuccess(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AddFavouriteResponse> call, @NonNull Throwable t) {
                    getMvpView().onFavError(t.getMessage());
                }
            });
        } else {
            getMvpView().onFavError(context.getResources().getString(R.string.could_not_connect));
        }
    }

    /**
     * remove favourite
     *
     * @param context context
     * @param itemId item Id
     * @param userId user Id
     */
    public void getRemoveFavouriteResponse(Context context, String itemId, String userId) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().removeFavourite(Constants.ServerUrl.API_TOKEN, itemId, userId).enqueue(new Callback<AddFavouriteResponse>() {
                @Override
                public void onResponse(@NonNull Call<AddFavouriteResponse> call, @NonNull retrofit2.Response<AddFavouriteResponse> response) {
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
            getMvpView().onFavError(context.getResources().getString(R.string.could_not_connect));
        }
    }
}
