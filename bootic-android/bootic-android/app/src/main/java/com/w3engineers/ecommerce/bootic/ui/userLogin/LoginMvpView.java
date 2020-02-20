package com.w3engineers.ecommerce.bootic.ui.userLogin;

import com.w3engineers.ecommerce.bootic.data.helper.base.MvpView;
import com.w3engineers.ecommerce.bootic.data.helper.response.CartModelResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.InventoryResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserRegistrationResponse;

public interface LoginMvpView extends MvpView {
    void onEmailSignUpSuccess(UserRegistrationResponse user);
    void onEmailSignUpError(String errorMessage);

    void onSocialSignUpSuccess(UserRegistrationResponse user);
    void onSocialSignUpError(String errorMessage);

    void onCartDataSuccess(CartModelResponse body);
    void onCartDataError(String message);

    void onGettingAddCartDataIntoServerSuccessResponse(InventoryResponse body);

    void onGettingAddCartDataIntoServerErrorResponse(String message);
}
