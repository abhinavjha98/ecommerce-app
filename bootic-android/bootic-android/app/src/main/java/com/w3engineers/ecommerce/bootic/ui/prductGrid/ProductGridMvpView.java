package com.w3engineers.ecommerce.bootic.ui.prductGrid;

import com.w3engineers.ecommerce.bootic.data.helper.base.MvpView;
import com.w3engineers.ecommerce.bootic.data.helper.response.AddFavouriteResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.ProductGridResponse;

public interface ProductGridMvpView extends MvpView {
    void onProductListSuccess(ProductGridResponse products);
    void onProductListError(String errorMessage);

    void onFavSuccess(AddFavouriteResponse response);
    void onFavError(String errorMessage);

    void onRemoveFavSuccess(AddFavouriteResponse response);
}
