package com.w3engineers.ecommerce.bootic.ui.reviewdetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BaseActivity;
import com.w3engineers.ecommerce.bootic.data.helper.models.FeedBackModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.ReviewImage;
import com.w3engineers.ecommerce.bootic.data.helper.models.StartRatingModel;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.Loader;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;
import com.w3engineers.ecommerce.bootic.databinding.ActivityReviewDetailsBinding;
import com.w3engineers.ecommerce.bootic.ui.productdetails.ProductDetailsActivity;
import com.w3engineers.ecommerce.bootic.ui.userfeedback.FeedbackActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public class ReviewDetailsActivity extends BaseActivity<ReviewDetailsMvpView, ReviewDetailsPresenter> implements ReviewDetailsMvpView {
    private RecyclerView recyclerViewReview;
    private ReviewDetailsAdapter mAdapter;
    private ActivityReviewDetailsBinding mBinding;
    String itemId;
    private boolean isRegistered;
    int rvSize, isOrdered;
    private Loader mLoader;
    public static boolean isAlreadyReviewed;
    List<ReviewImage> mReviewImageList;


    /**
     * Run Activity
     *
     * @param context mActivity
     * @param itemId  itemId
     */
    public static void runActivity(Context context, String itemId, int ordered) {
        Intent intent = new Intent(context, ReviewDetailsActivity.class);
        intent.putExtra(Constants.IntentKey.ITEM_ID, itemId);
        intent.putExtra(Constants.IntentKey.IS_ORDERED, ordered);
        runCurrentActivity(context, intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_review_details;
    }

    @Override
    protected void startUI() {
        mBinding = (ActivityReviewDetailsBinding) getViewDataBinding();
        initToolbar();
        isAlreadyReviewed = false;
        initRecyclerView();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            itemId = bundle.getString(Constants.IntentKey.ITEM_ID);
            isOrdered = bundle.getInt(Constants.IntentKey.IS_ORDERED);
        }

        setClickListener(mBinding.fabAddReview,mBinding.layoutIncludeNoNet.buttonRetry);
        mLoader = new Loader(this);
        isRegistered = CustomSharedPrefs.getUserStatus(this);
        checkNetConnection();
        getIntentDataAndHitToServer();


    }

    /**
     * check internet
     */
    private void checkNetConnection() {
        if (!NetworkHelper.hasNetworkAccess(this)) {
            //no net
            mLoader.stopLoader();
            mBinding.layoutIncludeNoNet.linearNoInternet.setVisibility(View.VISIBLE);
            mBinding.scrollView.setVisibility(View.GONE);
        } else {
            mBinding.layoutIncludeNoNet.linearNoInternet.setVisibility(View.GONE);
            mBinding.scrollView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ProductDetailsActivity.isFromReview)
            getIntentDataAndHitToServer();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add_review:
                if (isOrdered == 1) {
                    if (!isAlreadyReviewed) {
                        FeedbackActivity.runActivity(this, itemId);
                    } else {
                        Toast.makeText(this, getString(R.string.review_given), Toast.LENGTH_LONG).show();
                    }
                } else {
                    openDialog();
                }

                break;
            case R.id.button_retry:
                mLoader.show();
                presenter.showReviewFromServer(this, itemId);
                break;
        }
    }

    /**
     * this methos is used for creating a dialog for adding product to cart
     */

    private void openDialog() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.need_order))
                .setIcon(R.drawable.ic_logout)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    finish();
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    /**
     * Get intent data of item id
     */
    private void getIntentDataAndHitToServer() {
        mLoader.show();
        presenter.showReviewFromServer(this, itemId);
    }


    private void initToolbar() {
        TextView toobarTitle = findViewById(R.id.toolbar_title);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toobarTitle.setText(getString(R.string.title_review_details));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    private void initRecyclerView() {
        recyclerViewReview = findViewById(R.id.recycler_view_review_details);
        mAdapter = new ReviewDetailsAdapter(this);
        recyclerViewReview.setAdapter(mAdapter);
        recyclerViewReview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void stopUI() {
    }

    @Override
    protected ReviewDetailsPresenter initPresenter() {
        return new ReviewDetailsPresenter();
    }


    @Override
    public void onGettingShowReviewSuccess(FeedBackModel feedBackModel) {
        if (feedBackModel != null) {
            if (feedBackModel.getReviewImageList().isEmpty()) {
                mBinding.layoutHeader.setVisibility(View.GONE);
                mBinding.layoutNoData.setVisibility(View.VISIBLE);
            } else {
                mAdapter.clearList();
                mAdapter.addItem(feedBackModel.getReviewImageList());
                mReviewImageList=feedBackModel.getReviewImageList();
                mBinding.layoutHeader.setVisibility(View.VISIBLE);
                mBinding.layoutNoData.setVisibility(View.GONE);
            }
            setData(feedBackModel);
            mBinding.layoutIncludeNoNet.linearNoInternet.setVisibility(View.GONE);
            mBinding.scrollView.setVisibility(View.VISIBLE);
        } else {
            mBinding.layoutHeader.setVisibility(View.GONE);
            mBinding.layoutNoData.setVisibility(View.VISIBLE);
        }
        if (mLoader!=null){
            mLoader.stopLoader();
        }

    }

    /**
     * Set data in the activity xml
     *
     * @param feedBackModel feedBackModel
     */
    private void setData(FeedBackModel feedBackModel) {
        if (feedBackModel != null) {
            mBinding.textViewTotalRating.setText(feedBackModel.getTotalRatingCount() + " Ratings");
            mBinding.textViewReviewBg.setText(String.valueOf(feedBackModel.getAvgRating()));
            mBinding.ratingBarAvgRating.setRating(feedBackModel.getAvgRating());
            setDataInFiveRatingTextView(feedBackModel.mStartRatingModel);
        }
    }

    /**
     * Set data in five rating text view
     */
    private void setDataInFiveRatingTextView(StartRatingModel startRatingModel) {

        mBinding.textViewRatingCount5.setText(""+startRatingModel.ratingOne);

        mBinding.textViewRatingCount4.setText(""+startRatingModel.ratingTwo);

        mBinding.textViewRatingCount3.setText(""+startRatingModel.ratingThree);

        mBinding.textViewRatingCount2.setText(""+startRatingModel.ratingFour);

        mBinding.textViewRatingCount1.setText(""+startRatingModel.ratingFive);


    }


    @Override
    public void onGettingShowReviewError(String error,boolean isNetOn) {
        mBinding.layoutHeader.setVisibility(View.GONE);

        if (mLoader !=null){
            mLoader.stopLoader();
        }

        if (isNetOn){
            mBinding.layoutNoData.setVisibility(View.VISIBLE);
            mBinding.scrollView.setVisibility(View.VISIBLE);
        }else {
            mBinding.layoutIncludeNoNet.linearNoInternet.setVisibility(View.VISIBLE);
            mBinding.scrollView.setVisibility(View.GONE);
        }

    }
}
