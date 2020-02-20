package com.w3engineers.ecommerce.bootic.ui.updatepassword;

import com.w3engineers.ecommerce.bootic.data.helper.base.MvpView;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserRegistrationResponse;

public interface UpdatePasswordMvpView extends MvpView {
    void onPasswordUpdateSuccess(UserRegistrationResponse user);
    void onPasswordUpdateError(String errorMessage);
}
