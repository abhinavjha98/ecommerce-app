package com.w3engineers.ecommerce.bootic.ui.category;

import com.w3engineers.ecommerce.bootic.data.helper.base.MvpView;
import com.w3engineers.ecommerce.bootic.data.helper.response.AllCategoryResponse;

public interface CategoryMvpView extends MvpView {
    void onCategoryListSuccess(AllCategoryResponse productCategories);
    void onCategoryListError(String message,boolean isNetOn);
}
