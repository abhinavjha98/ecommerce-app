package com.w3engineers.ecommerce.bootic.ui.myfavourite;

import com.w3engineers.ecommerce.bootic.data.helper.base.MvpView;
import com.w3engineers.ecommerce.bootic.data.helper.response.ProductGridResponse;

public interface UserFavMvpView extends MvpView {
    void onGettingFavouriteSuccess(ProductGridResponse response);
    void onGettingFavouriteError(String errorMessage,boolean isNetOn);
}
