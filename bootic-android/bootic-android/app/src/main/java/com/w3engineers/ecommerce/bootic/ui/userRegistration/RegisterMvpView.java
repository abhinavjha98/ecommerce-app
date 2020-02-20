package com.w3engineers.ecommerce.bootic.ui.userRegistration;

import com.w3engineers.ecommerce.bootic.data.helper.base.MvpView;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserRegistrationResponse;

public interface RegisterMvpView extends MvpView {
    void onSignUpSuccess(UserRegistrationResponse user);
    void onSignUpError(String errorMessage);
}
