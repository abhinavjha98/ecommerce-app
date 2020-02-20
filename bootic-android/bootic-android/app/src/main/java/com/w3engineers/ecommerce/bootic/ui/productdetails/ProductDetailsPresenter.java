package com.w3engineers.ecommerce.bootic.ui.productdetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BasePresenter;
import com.w3engineers.ecommerce.bootic.data.helper.database.DatabaseUtil;
import com.w3engineers.ecommerce.bootic.data.helper.models.AttributeValueModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.AttributeWithView;
import com.w3engineers.ecommerce.bootic.data.helper.models.CustomProductInventory;
import com.w3engineers.ecommerce.bootic.data.helper.models.InventoryModel;
import com.w3engineers.ecommerce.bootic.data.helper.response.AddFavouriteResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.CartModelResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.InventoryResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.ProductDetailsResponse;
import com.w3engineers.ecommerce.bootic.data.provider.retrofit.RetrofitClient;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;
import com.w3engineers.ecommerce.bootic.data.util.SharedPref;
import com.w3engineers.ecommerce.bootic.ui.addcart.CartActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsPresenter extends BasePresenter<ProductDetailsMvpView> {
    private String userId = "";
    private boolean colorFlag, sizeFlag, isAvailableProduct, hasInventory;
    private int counter_1 = 0, counter_2 = 0, productCounter = 0, quality;
    private List<CustomProductInventory> prevCartList;
    private CustomProductInventory currentProductInventory;
    private List<String> combinationArray;
    private boolean isFirstCombination;
    StringBuilder inventoryKey;
    String color, size;
    List<String> attributeTitle;
    String gsonTitle;

    /**
     * this api is used to get product details response
     *
     * @param productId  product Id
     * @param context context
     */
    public void getProductDetailsResponse(final String productId, final Context context) {

        userId = CustomSharedPrefs.getLoggedInUserId(context);
        Log.d("userID",userId);
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().getProductDetailsResponse(Constants.ServerUrl.API_TOKEN, productId, userId).enqueue(new Callback<ProductDetailsResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductDetailsResponse> call, @NonNull Response<ProductDetailsResponse> response) {
                    if (response.body() != null) {
                        getMvpView().onProductDetailsSuccess(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductDetailsResponse> call, @NonNull Throwable t) {
                    if (t.getMessage() !=null){
                        getMvpView().onProductDetailsError(t.getMessage());
                    }
                }
            });
        } else {
            getMvpView().onProductDetailsError(context.getResources().getString(R.string.could_not_connect));
        }
    }


    /***
     * Add product to server for cart
     * @param context context
     * @param productId product Id
     * @param inventoryId inventory Id
     */
    public void getAddProductToCartServerResponse(final Context context,String productId,String inventoryId,int track){
        userId = CustomSharedPrefs.getLoggedInUserId(context);
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().getAddProductToCartResponse(Constants.ServerUrl.API_TOKEN,inventoryId
                    , userId,productId).enqueue(new Callback<InventoryResponse>() {
                @Override
                public void onResponse(@NonNull Call<InventoryResponse> call, @NonNull Response<InventoryResponse> response) {
                    if (response.body() != null) {
                        getMvpView().onAddToCartServerSuccess(response.body(),track);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<InventoryResponse> call, @NonNull Throwable t) {
                    if (t.getMessage() !=null){
                        getMvpView().onAddToCartError(t.getMessage());
                    }
                }
            });
        } else {
            getMvpView().onProductDetailsError(context.getResources().getString(R.string.could_not_connect));
        }
    }









    /**
     * this api is used to make product favourite.
     *
     * @param context context
     * @param itemId item id
     * @param userId user id
     */
    public void getAddFavouriteResponse(Context context, String itemId, String userId) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().addFavourite(Constants.ServerUrl.API_TOKEN, itemId, userId).enqueue(new Callback<AddFavouriteResponse>() {
                @Override
                public void onResponse(@NonNull Call<AddFavouriteResponse> call, @NonNull Response<AddFavouriteResponse> response) {
                    if (response.body() != null) {
                        getMvpView().onFavSuccess(response.body());
                    }

                }

                @Override
                public void onFailure(@NonNull Call<AddFavouriteResponse> call, @NonNull Throwable t) {
                    getMvpView().onFavError(t.getMessage());
                }
            });
        } else {
            getMvpView().onFavError(context.getResources().getString(R.string.could_not_connect));
        }
    }

    /**
     * this api is used to remove favourite.
     *
     * @param context context
     * @param itemId item id
     * @param userId user id
     */
    public void getRemoveFavouriteResponse(Context context, String itemId, String userId) {
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().removeFavourite(Constants.ServerUrl.API_TOKEN, itemId, userId).enqueue(new Callback<AddFavouriteResponse>() {
                @Override
                public void onResponse(@NonNull Call<AddFavouriteResponse> call, @NonNull Response<AddFavouriteResponse> response) {
                    if (response.body() != null) {
                        getMvpView().onRemoveFavSuccess(response.body());
                    }

                }

                @Override
                public void onFailure(@NonNull Call<AddFavouriteResponse> call, @NonNull Throwable t) {
                    getMvpView().onFavError(t.getMessage());
                }
            });
        } else {
            getMvpView().onFavError(context.getResources().getString(R.string.could_not_connect));
        }
    }

    public void addCart(int track, Activity mActivity,
                        List<AttributeValueModel> selectedAttribute,
                        List<AttributeWithView> mainIdList,
                        List<InventoryModel> inventoryModels,
                        CustomProductInventory cartInventory, boolean isListEmpty) {

        combinationArray = new ArrayList<>();
        attributeTitle = new ArrayList<>();
        isFirstCombination = false;
        if (!selectedAttribute.isEmpty() && !mainIdList.isEmpty()) {
            for (int i = 0; i < mainIdList.size(); i++) {
                for (int j = 0; j < selectedAttribute.size(); j++) {
                    if (selectedAttribute.get(j).attribute == mainIdList.get(i).id) {
                        if (isFirstCombination) {
                            combinationArray.add("," + mainIdList.get(i).id + "-" + selectedAttribute.get(j).id);

                        } else {
                            combinationArray.add(mainIdList.get(i).id + "-" + selectedAttribute.get(j).id);
                            isFirstCombination = true;
                        }
                        attributeTitle.add(selectedAttribute.get(j).rootTitle + ": " + selectedAttribute.get(j).title + " ");
                    }
                }
            }

            gsonTitle = new Gson().toJson(attributeTitle);
            inventoryKey = new StringBuilder();
            for (int i = 0; i < combinationArray.size(); i++) {
                inventoryKey.append(combinationArray.get(i));
            }
        }
        if (inventoryModels.size() > 0) {
            if (!isListEmpty) {
                for (InventoryModel inventory : inventoryModels) {
                    if (inventory.attribute.contentEquals(inventoryKey)) {
                        if (inventory.quantity > 0 && counter_1 <= inventory.quantity) {
                            ++counter_1;
                            isAvailableProduct = true;
                            cartInventory.available_qty = inventory.quantity;
                            cartInventory.product_id = inventory.productId;
                            cartInventory.inventory_id = inventory.id;

                            /* cartInventory.color_name = color;
                            cartInventory.size_name = size;*/
                            cartInventory.attributeTitle = gsonTitle;
                        } else {
                            isAvailableProduct = false;
                        }
                    }
                }
            } else {
                for (InventoryModel inventory : inventoryModels) {
                    if (inventory.quantity > 0) {
                        isAvailableProduct = true;
                        cartInventory.available_qty = inventory.quantity;
                        cartInventory.attributeTitle = gsonTitle;
                    /*    cartInventory.color_name = "null";
                        cartInventory.size_name = "null";*/
                        cartInventory.product_id = inventory.productId;
                        cartInventory.inventory_id = inventory.id;
                    } else isAvailableProduct = false;
                }
            }
        } else {
            Toast.makeText(mActivity, "There is no inventory!", Toast.LENGTH_SHORT).show();
        }

        currentProductInventory = cartInventory;
        checkPreviousInventory(currentProductInventory, mActivity, track);


    }

    /**
     * if user has previous cart product. need to check total inventory
     *
     * @param inventoryModels  inventory Models
     * @param mActivity activity
     * @param track track
     */
    private void checkPreviousInventory(CustomProductInventory inventoryModels, Activity mActivity, int track) {
        productCounter = 0;
        AsyncTask.execute(() -> {
            prevCartList = DatabaseUtil.on().getAllCodes();
            for (CustomProductInventory customInventory : prevCartList) {
                if (inventoryModels.inventory_id == customInventory.inventory_id) {
                    productCounter = productCounter + customInventory.currentQuantity;
                }
            }
            //productCounter++;
            if (isAvailableProduct) {
                if (productCounter < inventoryModels.available_qty) {
                    insertToCart(currentProductInventory, mActivity, track);
                } else {
                    mActivity.runOnUiThread(() -> Toast.makeText(mActivity, "Inventory exceed. You can't add more!!", Toast.LENGTH_SHORT).show());
                }
            } else {
                mActivity.runOnUiThread(() -> Toast.makeText(mActivity, "Product is not available", Toast.LENGTH_SHORT).show());
            }
        });
    }

    /**
     * insert item to cart
     *
     * @param currentProductInventory current Product Inventory
     * @param mActivity activity
     * @param track track
     */
    private void insertToCart(CustomProductInventory currentProductInventory, Activity mActivity, int track) {
        if (isAvailableProduct) {
            AsyncTask.execute(() -> {
                boolean isRegistered = CustomSharedPrefs.getUserStatus(mActivity);
                if (isRegistered){
                    //hit to server for add cart
                    getAddProductToCartServerResponse(mActivity,""+currentProductInventory.product_id,
                            ""+currentProductInventory.inventory_id,track);

                    Gson gson=new Gson();
                    String currentProductInventorySave=gson.toJson(currentProductInventory);
                    SharedPref.getSharedPref(mActivity).write(Constants.Preferences.SAVE_CURRENT_DATA,currentProductInventorySave);

                }else {
                    DatabaseUtil.on().insertItem(currentProductInventory);
                    mActivity.runOnUiThread(() -> {
                            Toast.makeText(mActivity, "This Product added into cart!", Toast.LENGTH_SHORT).show();
                            ((ProductDetailsActivity) mActivity).updateMenu();

                    });
                    if (track == 1) {
                        Intent intent = new Intent(mActivity, CartActivity.class);
                        mActivity.startActivity(intent);
                    }
                }


            });
        } else {
            Toast.makeText(mActivity, "Product is not available", Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * getting data from shared pref
     */
    public void getDataFromSharePref(Activity mActivity, ConstraintLayout layoutAdView) {

        String bannerStatus = SharedPref.getSharedPref(mActivity).read(Constants.Preferences.BANNER_STATUS);
        String bannerUnitID = SharedPref.getSharedPref(mActivity).read(Constants.Preferences.BANNER_UNIT_ID);
        String bannerAppID = SharedPref.getSharedPref(mActivity).read(Constants.Preferences.BANNER_APP_ID);
        getMvpView().onCheckBannerAdViewStatus(bannerStatus);
        if (bannerStatus.equals(Constants.Preferences.STATUS_ON)) {
            if (bannerUnitID != null && bannerAppID != null) {
                try {
                    ApplicationInfo applicationInfo = mActivity.getPackageManager().getApplicationInfo(mActivity.getPackageName(), PackageManager.GET_META_DATA);
                    applicationInfo.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", bannerAppID);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                MobileAds.initialize(mActivity,
                        bannerAppID);
                AdView adView = new AdView(mActivity);
                adView.setAdSize(AdSize.SMART_BANNER);
                adView.setAdUnitId(bannerUnitID);
                layoutAdView.addView(adView);

                adPlay(adView, layoutAdView);
            }
        }
    }

    /**
     * play banner ad based on ad listener
     */
    private void adPlay(AdView adView, ConstraintLayout layoutAdview) {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                layoutAdview.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdClosed() {
            }
        });
    }
}
