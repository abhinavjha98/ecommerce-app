package com.w3engineers.ecommerce.bootic.ui.splashScreen;

import com.w3engineers.ecommerce.bootic.data.helper.base.MvpView;
import com.w3engineers.ecommerce.bootic.data.helper.response.MainProductResponse;

public interface SplashMvpView extends MvpView {
    void onProductListSuccess(MainProductResponse response);
    void onProductListError(String errorMessage);
}
