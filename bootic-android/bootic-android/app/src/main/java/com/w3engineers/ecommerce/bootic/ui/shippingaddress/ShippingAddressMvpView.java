package com.w3engineers.ecommerce.bootic.ui.shippingaddress;

import com.w3engineers.ecommerce.bootic.data.helper.base.MvpView;
import com.w3engineers.ecommerce.bootic.data.helper.response.AvailableInventoryResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.PaymentResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserAddressResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserMultipleAddressResponse;

public interface ShippingAddressMvpView extends MvpView {
    void onGetAvailableAddressSuccess(UserAddressResponse addressResponse);
    void onGetAvailableAddressError(String errorMessage);
    void onGettingAllAddressSuccess(UserMultipleAddressResponse response);

    void onAvailableInventorySuccess(AvailableInventoryResponse response);
    void onAvailableInventoryError(String errorMessage);

    void onBrainTreeClientTokenSuccess(String tokenResponse);
    void onBrainTreeClientTokenError(String errorMessage);

    void allPaymentSuccess(PaymentResponse response);
    void allPaymentError(String errorMessage);
}
