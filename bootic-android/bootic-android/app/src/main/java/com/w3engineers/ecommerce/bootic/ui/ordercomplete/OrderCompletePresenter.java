package com.w3engineers.ecommerce.bootic.ui.ordercomplete;

import android.content.Context;
import androidx.annotation.NonNull;
import android.widget.Toast;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BasePresenter;
import com.w3engineers.ecommerce.bootic.data.helper.response.OrderListResponse;
import com.w3engineers.ecommerce.bootic.data.provider.retrofit.RetrofitClient;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderCompletePresenter extends BasePresenter<OrderCompleteMvpView> {

    public void getOrderedList(Context context, String userID, String page) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService()
                    .getOrderList(Constants.ServerUrl.API_TOKEN, userID, page).enqueue(new Callback<OrderListResponse>() {
                @Override
                public void onResponse(@NonNull Call<OrderListResponse> call, @NonNull Response<OrderListResponse> response) {
                    if (response.body() != null) {
                        getMvpView().onGettingOrderInfoSuccess(response.body());
                    }
                }
                @Override
                public void onFailure(@NonNull Call<OrderListResponse> call, @NonNull Throwable t) {
                    getMvpView().onGettingOrderInfoError(t.getMessage(),true);
                }
            });
        } else {
            getMvpView().onGettingOrderInfoError(context.getResources().getString(R.string.check_net_connection),false);
        }
    }
}
