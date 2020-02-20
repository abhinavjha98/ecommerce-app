package com.w3engineers.ecommerce.bootic.ui.checkout;

import com.w3engineers.ecommerce.bootic.data.helper.base.MvpView;
import com.w3engineers.ecommerce.bootic.data.helper.response.PaymentResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.SettingsResponse;

public interface CheckOutMvpView extends MvpView {
    void onPaymentNonceSuccess(String response);
    void  onPaymentNonceError(String errorMessage);

    void allPaymentSuccess(PaymentResponse response);
    void allPaymentError(String errorMessage);

    void onSettingCredentialSuccess(SettingsResponse settingsResponse);
    void onSettingCredentialError(String error);

}
