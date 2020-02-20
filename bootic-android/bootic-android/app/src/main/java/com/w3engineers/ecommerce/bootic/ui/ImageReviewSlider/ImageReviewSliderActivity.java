package com.w3engineers.ecommerce.bootic.ui.ImageReviewSlider;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BaseActivity;
import com.w3engineers.ecommerce.bootic.data.helper.models.ProductDetailsImageModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.ShowReviewImage;
import com.w3engineers.ecommerce.bootic.databinding.ActivityImageReviewSliderBinding;


import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;

public class ImageReviewSliderActivity extends BaseActivity<ImageReviewSliderMvpView,ImageReviewSliderPresenter>
        implements ImageReviewSliderMvpView {
    private ImageReviewSliderAdapter mAdapter;
    private ImageReviewDetailsSliderPagerAdapter mImageReviewDetailsSliderPagerAdapter;
    public static List<ProductDetailsImageModel> mImageList ;
    public static List<ShowReviewImage> sShowReviewImageList ;
    /**
     * Run Activity
     * @param context mActivity
     */
    public static void runActivity(Context context,List<ProductDetailsImageModel> productDetailsImageModel) {
        Intent intent = new Intent(context, ImageReviewSliderActivity.class);
        mImageList=productDetailsImageModel;
        runCurrentActivity(context, intent);
    }


    public static void runActivity(List<ShowReviewImage> showReviewImageList, Context context) {
        Intent intent = new Intent(context, ImageReviewSliderActivity.class);
        sShowReviewImageList=showReviewImageList;
        runCurrentActivity(context, intent);
    }

    private ActivityImageReviewSliderBinding mBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_image_review_slider;
    }

    @Override
    protected void startUI() {
        mBinding= (ActivityImageReviewSliderBinding) getViewDataBinding();
        initToolbar();

        if (mImageList !=null){
            prepareImageList(mImageList);
        }

        if (sShowReviewImageList !=null){
            initReviewDetailsViewPager(sShowReviewImageList);
        }

    }


    /**
     * init view with server response
     */
    private void prepareImageList(List<ProductDetailsImageModel> imageList) {
        if (imageList == null) {
            ProductDetailsImageModel imageDetails = new ProductDetailsImageModel();
            imageDetails.imageUri = getURLForResource(R.drawable.place_holder);
            List<ProductDetailsImageModel> imgList = new ArrayList<>();
            imgList.add(imageDetails);
            initViewPager(imgList);
        } else {
            initViewPager(imageList);
        }
    }


    /**
     * this api is for getting default placeholder's path
     *
     * @param resourceId resource Id
     * @return placeholder's uri
     */
    public String getURLForResource(int resourceId) {
        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resourceId).toString();
    }



    /**
     * init view with server response
     */
    private void initViewPager(List<ProductDetailsImageModel> imgList) {
        mAdapter = new ImageReviewSliderAdapter(imgList, this);
        mBinding.viewPagerReviewSlider.setAdapter(mAdapter);
    }


    /**
     * init view with server response
     */
    private void initReviewDetailsViewPager(List<ShowReviewImage> imgList) {
        mImageReviewDetailsSliderPagerAdapter = new ImageReviewDetailsSliderPagerAdapter( imgList,this);
        mBinding.viewPagerReviewSlider.setAdapter(mImageReviewDetailsSliderPagerAdapter);
    }

    private void initToolbar() {
        TextView toobarTitle = findViewById(R.id.toolbar_title);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.review_slider));
        toobarTitle.setText("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void stopUI() {

    }

    @Override
    protected ImageReviewSliderPresenter initPresenter() {
        return new ImageReviewSliderPresenter();
    }

}
