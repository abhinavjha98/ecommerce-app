package com.w3engineers.ecommerce.bootic.ui.userfeedback;

import android.graphics.Bitmap;

import com.w3engineers.ecommerce.bootic.data.helper.base.MvpView;
import com.w3engineers.ecommerce.bootic.data.helper.response.FeedBackResponse;

public interface FeedbackMvpView extends MvpView {
    void onGettingBitmap(Bitmap bitmap);
    void onGettingImagePath(String imagePath);

    void onGettingReviewSuccess(FeedBackResponse feedBackResponse);

    void onGettingReviewError(String error);
}
