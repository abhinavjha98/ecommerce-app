package com.w3engineers.ecommerce.bootic.ui.addcart;

import com.w3engineers.ecommerce.bootic.data.helper.base.MvpView;
import com.w3engineers.ecommerce.bootic.data.helper.response.CartModelResponse;

public interface CartMvpView extends MvpView {
    void onCartDataSuccess(CartModelResponse body);
    void onCartDataError(String message);
}
