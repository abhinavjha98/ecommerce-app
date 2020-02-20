package com.w3engineers.ecommerce.bootic.ui.emailverification;

import com.w3engineers.ecommerce.bootic.data.helper.base.MvpView;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserRegistrationResponse;

public interface EmailVerificationMvpView extends MvpView {
    void onEmailVerificationSuccess(UserRegistrationResponse user);

    void onEmailVeridicationError(String errorMessage);

    void onResendCodeSuccess(UserRegistrationResponse user);

    void onResendCodeError(String errorMessage);
}
