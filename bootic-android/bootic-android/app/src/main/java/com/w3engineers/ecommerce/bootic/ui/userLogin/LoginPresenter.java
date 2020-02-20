package com.w3engineers.ecommerce.bootic.ui.userLogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Patterns;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BasePresenter;
import com.w3engineers.ecommerce.bootic.data.helper.response.CartModelResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.InventoryResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserRegistrationResponse;
import com.w3engineers.ecommerce.bootic.data.provider.retrofit.RetrofitClient;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter extends BasePresenter<LoginMvpView> {

    private GoogleSignInClient mGoogleSignInClient;

    /**
     * checking email validation
     */
    public boolean emailValidity(String text) {
        return Patterns.EMAIL_ADDRESS.matcher(text).matches();
    }

    /**
     * checking password validation
     */
    public boolean passwordValidity(String text) {
        return text.length() >= Constants.DefaultValue.MINIMUM_LENGTH_PASS
                && text.length() <= Constants.DefaultValue.MAXIMUM_LENGTH;
    }

    public boolean isEmpty(String text) {
        return text.equals("");
    }

    /**
     * this api is called for logging with email
     *
     * @param email    email
     * @param password password
     * @param context  context
     */
    public void loginWithEmail(final String email, final String password, final Context context) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().loginEmail(Constants.ServerUrl.API_TOKEN, email, password)
                    .enqueue(new Callback<UserRegistrationResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<UserRegistrationResponse> call,
                                               @NonNull Response<UserRegistrationResponse> response) {
                            if (response.body() != null) {
                                getMvpView().onEmailSignUpSuccess(response.body());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<UserRegistrationResponse> call, @NonNull Throwable t) {
                            getMvpView().onEmailSignUpError(t.getMessage());
                        }
                    });

        } else {
            getMvpView().onEmailSignUpError(context.getResources().getString(R.string.check_net_connection));
        }
    }


    /***
     * Add product to server for cart
     * @param context context
     * @param productId product Id
     * @param inventoryId inventory Id
     */
    public void getAddProductToCartServerResponse(final Context context,String productId,String inventoryId){
        String userId = CustomSharedPrefs.getLoggedInUserId(context);
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().getAddProductToCartResponse(Constants.ServerUrl.API_TOKEN,inventoryId
                    , userId,productId).enqueue(new Callback<InventoryResponse>() {
                @Override
                public void onResponse(@NonNull Call<InventoryResponse> call, @NonNull Response<InventoryResponse> response) {
                    if (response.body() != null&& response.body().inventoryModel !=null) {
                        getMvpView().onGettingAddCartDataIntoServerSuccessResponse(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<InventoryResponse> call, @NonNull Throwable t) {
                    if (t.getMessage() !=null){
                        getMvpView().onGettingAddCartDataIntoServerErrorResponse(t.getMessage());
                    }
                }
            });
        } else {
           // getMvpView().onProductDetailsError(context.getResources().getString(R.string.could_not_connect));
        }
    }



    /**
     * this api is used to sign up with fb and google
     *
     * @param name     name
     * @param email    email
     * @param password password
     * @param type     type
     * @param socialId social id
     * @param context  context
     */
    public void userRegistration(final String name
            , final String email
            , final String password
            , final String type
            , final String socialId
            , final Context context) {

        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().socialRegistration(Constants.ServerUrl.API_TOKEN, type, socialId, name, email, password).enqueue(new Callback<UserRegistrationResponse>() {
                @Override
                public void onResponse(@NonNull Call<UserRegistrationResponse> call, @NonNull Response<UserRegistrationResponse> response) {
                    if (response.body() != null)
                        getMvpView().onSocialSignUpSuccess(response.body());
                }

                @Override
                public void onFailure(@NonNull Call<UserRegistrationResponse> call, @NonNull Throwable t) {
                    getMvpView().onSocialSignUpError(t.getMessage());
                }
            });
        } else {
            getMvpView().onSocialSignUpError(context.getResources().getString(R.string.check_net_connection));
        }

    }


    /**
     * getting all credential for fb log in
     *
     * @param object json object
     * @param context context
     */
    public void getFBData(JSONObject object, Context context) {
        String profileUrl = "";
        try {
            URL profilePicture = new URL("https://graph.facebook.com/" + object.getString("id") + "/picture?width=200&height=200");
            String id = object.getString("id");
            if (profilePicture.toString() != null) {
                profileUrl = profilePicture.toString();
            }
            String firstName = object.getString("first_name");
            String lastName = object.getString("last_name");
            if (id != null) {
                userRegistration(firstName, "a", "a",
                        "" + Constants.RegistrationType.FACEBOOK_SIGN_UP, id, context);


                //  CustomSharedPrefs.setProfileUrl(mActivity, profileUrl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * getting data for google log in
     *
     * @param googleSignInResult google Sign In Result
     * @param context context
     */
    public void getGoogleData(GoogleSignInResult googleSignInResult, Context context) {
        if (googleSignInResult.isSuccess()) {
            GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
            String profileUrl = null;
            String id = googleSignInAccount.getId();
            String email = googleSignInAccount.getEmail();
            String name = googleSignInAccount.getDisplayName();
            if (googleSignInAccount.getPhotoUrl() != null) {
                profileUrl = googleSignInAccount.getPhotoUrl().toString();
            }
            if (id != null) {
                userRegistration(name, email, "",
                        "" + Constants.RegistrationType.GOOGLE_SIGN_UP, id, context);


                CustomSharedPrefs.setProfileUrl(context, profileUrl);
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * initialized Google client for google sign up
     *
     * @param activity activity
     */
    void initGoogleClient(Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, LoginActivity.RC_SIGN_IN);
    }

    /***
     * Get Data from server for cart
     * @param context context
     */
    public void getAddProductToCartServerResponse(final Context context){
        String userId = "";
        userId = CustomSharedPrefs.getLoggedInUserId(context);
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().getCartByUserResponse(Constants.ServerUrl.API_TOKEN
                    , userId).enqueue(new Callback<CartModelResponse>() {
                @Override
                public void onResponse(@NonNull Call<CartModelResponse> call, @NonNull Response<CartModelResponse> response) {
                    if (response.isSuccessful()){
                        if (response.body() != null && !response.body().mCartModelList.isEmpty()  ) {
                            getMvpView().onCartDataSuccess(response.body());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CartModelResponse> call, @NonNull Throwable t) {
                    if (t.getMessage() !=null){
                        getMvpView().onCartDataError(t.getMessage());
                    }
                }
            });
        } else {
            getMvpView().onCartDataError(context.getResources().getString(R.string.could_not_connect));
        }
    }
}
