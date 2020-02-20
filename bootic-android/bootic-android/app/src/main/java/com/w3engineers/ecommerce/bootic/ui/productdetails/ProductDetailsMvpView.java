package com.w3engineers.ecommerce.bootic.ui.productdetails;

import com.w3engineers.ecommerce.bootic.data.helper.base.MvpView;
import com.w3engineers.ecommerce.bootic.data.helper.response.AddFavouriteResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.CartModelResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.InventoryResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.ProductDetailsResponse;

public interface ProductDetailsMvpView extends MvpView {
    /**
     * this interface is used to create callback to pass server response
     */


    void onProductDetailsSuccess(ProductDetailsResponse detailsResponse);

    void onProductDetailsError(String errorMessage);

    void onFavSuccess(AddFavouriteResponse response);

    void onFavError(String errorMessage);

    void onRemoveFavSuccess(AddFavouriteResponse response);

    void onCheckBannerAdViewStatus(String status);


    void onAddToCartServerSuccess(InventoryResponse body,int comingFromBuyNow);

    void onAddToCartError(String message);

}
