package com.w3engineers.ecommerce.bootic.ui.dashboard;

import com.w3engineers.ecommerce.bootic.data.helper.base.MvpView;
import com.w3engineers.ecommerce.bootic.data.helper.response.AddFavouriteResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.MainProductResponse;

public interface DashboardMvpView extends MvpView {
   void onProductListSuccess(MainProductResponse response);
    void onProductListError(String errorMessage);

    void onFavSuccess(AddFavouriteResponse response);
    void onFavError(String errorMessage);

    void onRemoveFavSuccess(AddFavouriteResponse response);
}
