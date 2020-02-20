package com.w3engineers.ecommerce.bootic.ui.shippingaddress;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BasePresenter;
import com.w3engineers.ecommerce.bootic.data.helper.models.AddressModel;
import com.w3engineers.ecommerce.bootic.data.helper.response.AvailableInventoryResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.PaymentResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserAddressResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserMultipleAddressResponse;
import com.w3engineers.ecommerce.bootic.data.provider.retrofit.RetrofitClient;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;
import com.w3engineers.ecommerce.bootic.data.util.SharedPref;
import com.w3engineers.ecommerce.bootic.data.util.UtilityClass;

import androidx.annotation.NonNull;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShippingAddressPresenter extends BasePresenter<ShippingAddressMvpView> {
    /**
     * updating address to server
     *
     * @param context  context
     * @param address1 address1
     * @param address2 address2
     * @param city     city
     * @param zip      zip
     * @param state    state
     * @param country  country
     */

    public void updateAddress(final Context context
            , final String address1
            , final String address2
            , final String city
            , final String zip
            , final String state
            , final String country) {


        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().getUserAddressResponse(Constants.ServerUrl.API_TOKEN,
                    CustomSharedPrefs.getLoggedInUserId(context), address1, address2, city, zip, state, country, "").enqueue(new Callback<UserAddressResponse>() {
                @Override
                public void onResponse(@NonNull Call<UserAddressResponse> call, @NonNull Response<UserAddressResponse> response) {
                    if (response.body() != null) {
                        getMvpView().onGetAvailableAddressSuccess(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserAddressResponse> call, @NonNull Throwable t) {
                    getMvpView().onGetAvailableAddressError(t.getMessage());
                }
            });
        } else {
            getMvpView().onGetAvailableAddressError(context.getResources().getString(R.string.check_net_connection));
        }
    }

    /**
     * this api is used to get all address of user from server
     *
     * @param context context
     * @param userId  user id
     */
    public void getAllAddress(Context context, String userId) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().getAllAddress(Constants.ServerUrl.API_TOKEN, userId).enqueue(new Callback<UserMultipleAddressResponse>() {
                @Override
                public void onResponse(@NonNull Call<UserMultipleAddressResponse> call, @NonNull Response<UserMultipleAddressResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            getMvpView().onGettingAllAddressSuccess(response.body());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserMultipleAddressResponse> call, @NonNull Throwable t) {
                    if (t.getMessage() !=null){
                        getMvpView().onGetAvailableAddressError(t.getMessage());
                    }
                }
            });
        } else {
            getMvpView().onGetAvailableAddressError(context.getResources().getString(R.string.check_net_connection));
        }
    }

    /**
     * getting token for braintree payment
     *
     * @param context context
     */
    public void getClientTokenFromServer(Context context) {
        String sendBox = SharedPref.getSharedPref(context).read(Constants.Preferences.ENVIRONMENT);
        String merchant = SharedPref.getSharedPref(context).read(Constants.Preferences.MERCHANT_ID);
        String publicID = SharedPref.getSharedPref(context).read(Constants.Preferences.PUBLIC_KEY);
        String privateID = SharedPref.getSharedPref(context).read(Constants.Preferences.PRIVATE_KEY);
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(context.getResources().getString(R.string.wait_dialog));
        dialog.show();
        if (NetworkHelper.hasNetworkAccess(context)) {

            RetrofitClient.getApiService().paymentNonce("", "", sendBox,
                    merchant, publicID, privateID).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    dialog.dismiss();

                    if (response.body() != null) {
                        // dialog.dismiss();
                        try {
                            getMvpView().onBrainTreeClientTokenSuccess(response.body().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    dialog.dismiss();
                    getMvpView().onBrainTreeClientTokenError(t.getMessage());
                }
            });
        } else {
            dialog.dismiss();
            getMvpView().onBrainTreeClientTokenError(context.getResources().getString(R.string.check_net_connection));
        }
    }


    /**
     * saving address to shared pref
     *
     * @param addressModel addressModel
     * @param context      context
     */
    public void saveAddressData(AddressModel addressModel, Context context) {
        if (addressModel != null) {
            String address = UtilityClass.objectToString(addressModel);
            SharedPref.getSharedPref(context).write(Constants.Preferences.USER_ADDRESS, address);
        }
    }

    /**
     * getting available inventory of product
     *
     * @param context     context
     * @param inventories inventories
     */
    public void getAvailableInventory(Context context, String inventories) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().getAvailableInventory(Constants.ServerUrl.API_TOKEN, inventories)
                    .enqueue(new Callback<AvailableInventoryResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<AvailableInventoryResponse> call, @NonNull Response<AvailableInventoryResponse> response) {
                            if (response.body() != null) {
                                getMvpView().onAvailableInventorySuccess(response.body());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<AvailableInventoryResponse> call, @NonNull Throwable t) {
                            getMvpView().onAvailableInventoryError(t.getMessage());
                        }
                    });

        }else {
            getMvpView().onAvailableInventoryError(context.getResources().getString(R.string.check_net_connection));
        }
    }



}
