package com.w3engineers.ecommerce.bootic.ui.signinresendcode;

import com.w3engineers.ecommerce.bootic.data.helper.base.MvpView;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserRegistrationResponse;

public interface SignInEmailSendMvpView extends MvpView {
   void onGetCodeSuccess(UserRegistrationResponse user);
   void onGetCodeError(String errorMessage);
}
