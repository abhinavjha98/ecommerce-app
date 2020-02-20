package com.w3engineers.ecommerce.bootic.ui.reviewdetails;

import com.w3engineers.ecommerce.bootic.data.helper.base.MvpView;
import com.w3engineers.ecommerce.bootic.data.helper.models.FeedBackModel;

public interface ReviewDetailsMvpView extends MvpView {
    void onGettingShowReviewSuccess(FeedBackModel feedBackModel);

    void onGettingShowReviewError(String error,boolean isNetOn);
}
