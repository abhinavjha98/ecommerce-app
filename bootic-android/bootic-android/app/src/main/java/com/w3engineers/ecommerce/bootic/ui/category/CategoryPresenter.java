package com.w3engineers.ecommerce.bootic.ui.category;

import android.content.Context;
import androidx.annotation.NonNull;
import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BasePresenter;
import com.w3engineers.ecommerce.bootic.data.helper.response.AllCategoryResponse;
import com.w3engineers.ecommerce.bootic.data.provider.retrofit.RetrofitClient;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class CategoryPresenter extends BasePresenter<CategoryMvpView> {


    public void getCategories(Context context, final String page) {

        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().getAllCategory(Constants.ServerUrl.API_TOKEN,page).enqueue(new Callback<AllCategoryResponse>() {
                @Override
                public void onResponse(@NonNull Call<AllCategoryResponse> call,@NonNull Response<AllCategoryResponse> response) {
                    if(response.body() != null){
                        getMvpView().onCategoryListSuccess(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AllCategoryResponse> call,@NonNull Throwable t) {
                    getMvpView().onCategoryListError(t.getMessage(),true);
                }
            });
        }else {
            getMvpView().onCategoryListError(context.getResources().getString(R.string.could_not_connect),false);
        }
    }
}
