package com.w3engineers.ecommerce.bootic.ui.ordercomplete;

import com.w3engineers.ecommerce.bootic.data.helper.base.MvpView;
import com.w3engineers.ecommerce.bootic.data.helper.response.OrderListResponse;

public interface OrderCompleteMvpView extends MvpView {
    void onGettingOrderInfoSuccess(OrderListResponse response);
    void onGettingOrderInfoError(String errorMessage,boolean isNetOn);
}
