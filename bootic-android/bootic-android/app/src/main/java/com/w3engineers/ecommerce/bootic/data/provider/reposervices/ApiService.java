package com.w3engineers.ecommerce.bootic.data.provider.reposervices;


import com.w3engineers.ecommerce.bootic.data.helper.response.AdMobResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.AddFavouriteResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.AllCategoryResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.AvailableInventoryResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.CartModelResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.InventoryResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.MainProductResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.OrderListResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.PaymentResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.ProductDetailsResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.ProductGridResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.FeedBackResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.SettingsResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.UploadImageResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserAddressResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserMultipleAddressResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserRegistrationResponse;
import com.w3engineers.ecommerce.bootic.data.util.Constants;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @Multipart
    @POST("user/upload-profile-image.php")
    Call<UploadImageResponse> uploadImage(@Part("api_token") RequestBody token,
                                          @Part("id") RequestBody userId,
                                          @Part("email") RequestBody email,
                                          @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("favourite/add.php")
    Call<AddFavouriteResponse> addFavourite(@Field("api_token") String apiToken,
                                            @Field("item_id") String itemID,
                                            @Field("user_id") String userID);

    @FormUrlEncoded
    @POST("favourite/remove.php")
    Call<AddFavouriteResponse> removeFavourite(@Field("api_token") String apiToken,
                                               @Field("item_id") String itemID,
                                               @Field("user_id") String userID);

    @FormUrlEncoded
    @POST("favourite/by-user.php")
    Call<ProductGridResponse> allFavourite(@Field("api_token") String apiToken,
                                           @Field("page") String page,
                                           @Field("user_id") String userID);


    @FormUrlEncoded
    @POST("product/search.php")
    Call<ProductGridResponse> searchResult(@Field("api_token") String apiToken,
                                           @Field("page") String page,
                                           @Field("user_id") String userID,
                                           @Field("search") String searchText);

    @FormUrlEncoded
    @POST("product/inventory.php")
    Call<AvailableInventoryResponse> getAvailableInventory(@Field("api_token") String apiToken,
                                                           @Field("inventory") String inventories);


    /**
     * Add review to server
     *
     * @param userId user id
     * @param itemId item id
     * @param rating rating
     * @param review comment
     * @param token  token
     * @param files  image files
     * @return object of FeedBackResponse
     */
    @Multipart
    @POST(Constants.ServerUrl.ADD_REVIEW)
    Call<FeedBackResponse> addReviewToServer(@Part("user_id") RequestBody userId,
                                             @Part("item_id") RequestBody itemId,
                                             @Part("rating") RequestBody rating,
                                             @Part("review") RequestBody review,
                                             @Part("api_token") RequestBody token,
                                             @Part MultipartBody.Part[] files);

    /***
     * Show all review of an item
     * @param apiToken apiToken
     * @param itemId item id
     * @return object of FeedBackResponse
     */
    @FormUrlEncoded
    @POST(Constants.ServerUrl.SHOW_REVIEW)
    Call<FeedBackResponse> showReview(@Field("api_token") String apiToken,
                                      @Field("item_id") String itemId);


    @FormUrlEncoded
    @POST("order/by-user.php")
    Call<OrderListResponse> getOrderList(@Field("api_token") String apiToken,
                                         @Field("user_id") String userID,
                                         @Field("page") String page);


    @FormUrlEncoded
    @POST("product/by-tag.php")
    Call<ProductGridResponse> getOfferProductList(@Field("api_token") String apiToken,
                                                  @Field("tag") String tag,
                                                  @Field("page") String page);


    @FormUrlEncoded
    @POST("user/register.php")
    Call<UserRegistrationResponse> socialRegistration(@Field("api_token") String apiToken,
                                                      @Field("type") String type,
                                                      @Field("social_id") String id,
                                                      @Field("username") String name,
                                                      @Field("email") String email,
                                                      @Field("password") String pass);

    @FormUrlEncoded
    @POST(Constants.ServerUrl.BRAIN_TREE)
    Call<ResponseBody> paymentNonce(@Field("NONCE") String nonce,
                                    @Field("amount") String amount,
                                    @Field("environment") String envo,
                                    @Field("merchantId") String mar,
                                    @Field("publicKey") String pub,
                                    @Field("privateKey") String pri);


    @FormUrlEncoded
    @POST(Constants.ServerUrl.ORDER_URL)
    Call<PaymentResponse> doPayment(@Field("secret_data") String secretData);


    @FormUrlEncoded
    @POST(Constants.ServerUrl.SETTINGS_URL)
    Call<SettingsResponse> hitSetting(@Field("api_token") String apiToken);

    @FormUrlEncoded
    @POST(Constants.ServerUrl.EMAIL_VERIFICATION)
    Call<UserRegistrationResponse> emailVerification(@Field("api_token") String apiToken,
                                                     @Field("email") String email,
                                                     @Field("verification_token") String verifyToken);

    @FormUrlEncoded
    @POST(Constants.ServerUrl.RESEND_CODE)
    Call<UserRegistrationResponse> resendCode(@Field("api_token") String apiToken,
                                              @Field("email") String email);


    @FormUrlEncoded
    @POST(Constants.ServerUrl.MAIN_PRODUCT_URL)
    Call<MainProductResponse> getProductMain(@Field("api_token") String apiToken,
                                             @Field("page") String page,
                                             @Field("existing") String existing,
                                             @Field("user_id") String userID);


    @FormUrlEncoded
    @POST(Constants.ServerUrl.ADMOB_URL)
    Call<AdMobResponse> getAdMobResponse(@Field("api_token") String apiToken);

    @FormUrlEncoded
    @POST(Constants.ServerUrl.PRODUCT_GRID_URL)
    Call<ProductGridResponse> getProductGridResponse(@Field("api_token") String apiToken,
                                                     @Field("category_id") String categoryID,
                                                     @Field("user_id") String userID,
                                                     @Field("page") String page);

    @FormUrlEncoded
    @POST(Constants.ServerUrl.PRODUCT_DETAILS_URL)
    Call<ProductDetailsResponse> getProductDetailsResponse(@Field("api_token") String apiToken,
                                                           @Field("id") String categoryID,
                                                           @Field("user_id") String userID);

    @FormUrlEncoded
    @POST(Constants.ServerUrl.ADD_TO_CART)
    Call<InventoryResponse> getAddProductToCartResponse(@Field("api_token") String apiToken,
                                                        @Field("inventory_id") String inventoryId,
                                                        @Field("user_id") String userID,
                                                        @Field("product_id") String productId
                                                        );


    @FormUrlEncoded
    @POST(Constants.ServerUrl.CART_BY_USER)
    Call<CartModelResponse> getCartByUserResponse(@Field("api_token") String apiToken,
                                                  @Field("user_id") String userID
                                                        );



    @FormUrlEncoded
    @POST(Constants.ServerUrl.REMOVE_FROM_CART)
    Call<InventoryResponse> getRemovedFromCartResponse(@Field("api_token") String apiToken,
                                                        @Field("inventory_id") String inventoryId,
                                                        @Field("user_id") String userID,
                                                        @Field("product_id") String productId);



    @FormUrlEncoded
    @POST(Constants.ServerUrl.USER_ADDRESS)
    Call<UserAddressResponse> getUserAddressResponse(@Field("api_token") String apiToken,
                                                     @Field("user_id") String userID,
                                                     @Field("line_1") String address1,
                                                     @Field("line_2") String address2,
                                                     @Field("city") String city,
                                                     @Field("zip") String zip,
                                                     @Field("state") String state,
                                                     @Field("country") String country,
                                                     @Field("id") String id);

    @FormUrlEncoded
    @POST(Constants.ServerUrl.CATEGORY_URL)
    Call<AllCategoryResponse> getAllCategory(@Field("api_token") String apiToken,
                                             @Field("page") String page);


    @FormUrlEncoded
    @POST(Constants.ServerUrl.UPDATE_PASSWORD)
    Call<UserRegistrationResponse> updatePassword(@Field("api_token") String apiToken,
                                                  @Field("email") String email,
                                                  @Field("verification_token") String verifyToken,
                                                  @Field("password") String password);

    @FormUrlEncoded
    @POST(Constants.ServerUrl.LOGIN_URL)
    Call<UserRegistrationResponse> loginEmail(@Field("api_token") String apiToken,
                                              @Field("email") String email,
                                              @Field("password") String password);

    @FormUrlEncoded
    @POST(Constants.ServerUrl.GET_ALL_ADDRESS_URL)
    Call<UserMultipleAddressResponse> getAllAddress(@Field("api_token") String apiToken,
                                                    @Field("user_id") String email);


    @FormUrlEncoded
    @POST(Constants.ServerUrl.REMOVE_ADDRESS_URL)
    Call<UserAddressResponse> removeAddress(@Field("api_token") String apiToken,
                                            @Field("id") String id);

}

