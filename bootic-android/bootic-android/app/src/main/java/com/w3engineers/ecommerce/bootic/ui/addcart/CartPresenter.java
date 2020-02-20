package com.w3engineers.ecommerce.bootic.ui.addcart;

import android.content.Context;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BasePresenter;
import com.w3engineers.ecommerce.bootic.data.helper.response.CartModelResponse;
import com.w3engineers.ecommerce.bootic.data.provider.retrofit.RetrofitClient;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartPresenter extends BasePresenter<CartMvpView> {
    private String userId = "";
    /***
     * Get Data from server for cart
     * @param context context
     */
    public void getAddProductToCartServerResponse(final Context context){
        userId = CustomSharedPrefs.getLoggedInUserId(context);
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().getCartByUserResponse(Constants.ServerUrl.API_TOKEN
                    , userId).enqueue(new Callback<CartModelResponse>() {
                @Override
                public void onResponse(@NonNull Call<CartModelResponse> call, @NonNull Response<CartModelResponse> response) {
                    if (response.body() != null) {
                        getMvpView().onCartDataSuccess(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CartModelResponse> call, @NonNull Throwable t) {
                    if (t.getMessage() !=null){
                        getMvpView().onCartDataError(t.getMessage());
                    }
                }
            });
        } else {
            getMvpView().onCartDataError(context.getResources().getString(R.string.could_not_connect));
        }
    }
}
