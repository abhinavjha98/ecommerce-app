package com.w3engineers.ecommerce.bootic.ui.productdetails;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.net.Uri;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.CustomMenuBaseActivity;
import com.w3engineers.ecommerce.bootic.data.helper.base.ItemClickListener;
import com.w3engineers.ecommerce.bootic.data.helper.database.DatabaseUtil;
import com.w3engineers.ecommerce.bootic.data.helper.models.AttributeValueModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.AttributeWithView;
import com.w3engineers.ecommerce.bootic.data.helper.models.CustomProductInventory;
import com.w3engineers.ecommerce.bootic.data.helper.models.DetailsAttributeModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.InterestedProductModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.InventoryModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.ProductDetailsDataModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.ProductDetailsImageModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.RecentReviewModel;
import com.w3engineers.ecommerce.bootic.data.helper.response.AddFavouriteResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.InventoryResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.ProductDetailsResponse;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.Loader;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;
import com.w3engineers.ecommerce.bootic.data.util.SharedPref;
import com.w3engineers.ecommerce.bootic.data.util.UIHelper;
import com.w3engineers.ecommerce.bootic.data.util.UtilityClass;
import com.w3engineers.ecommerce.bootic.databinding.ActivityProductDetailsNewBinding;
import com.w3engineers.ecommerce.bootic.ui.ImageReviewSlider.ImageReviewSliderActivity;
import com.w3engineers.ecommerce.bootic.ui.addcart.CartActivity;
import com.w3engineers.ecommerce.bootic.ui.dashboard.UpdateFavourite;
import com.w3engineers.ecommerce.bootic.ui.reviewdetails.ReviewDetailsActivity;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductDetailsActivity extends CustomMenuBaseActivity<ProductDetailsMvpView, ProductDetailsPresenter> implements
        ProductDetailsMvpView, ItemClickListener<AttributeValueModel>, InterestItemClick,ItemClickReviewImage {
    private ReviewImageAdapter mImageAdapter;
    private InterestProductAdapter mProductAdapter;
    private Loader mLoader;
    private ActivityProductDetailsNewBinding mBinding;
    private ViewPagerAdapter mViewPagerAdapter;
    private ChooseColorAdapter mColorAdapter;
    private ChooseSizeAdapter mSizeAdapter;
    private List<InventoryModel> inventoryModels;
    private CustomProductInventory cartInventory;
    private AlertDialog dialogColor, dialogSize;
    private String productId, intentValue;
    private boolean isListEmpty, isCheckListEmpty = true;
    private ProductDetailsDataModel mProductModel;
    private List<AttributeWithView> attribuiteIdList = new ArrayList<>();
    private int ordered, sizeOfList;
    public static ProductDetailsActivity productDetailsActivity;
    public static boolean isFromReview;
    private List<ChooseColorAdapter> mAdapterList = new ArrayList<>(Arrays.asList(new ChooseColorAdapter[100]));
    private List<DetailsAttributeModel> attributeModels;
    private List<AttributeValueModel> selectedModelList = new ArrayList<>();
    private boolean isApapterTriggered;
    List<ProductDetailsImageModel> mImageReviewSliderList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_product_details_new;
    }

    @Override
    protected void startUI() {
        initViews();
        initToolbar();
        initReviewRecycler();
        initInterestProductRecycler();
            presenter.getDataFromSharePref(this, mBinding.adView);
        productDetailsActivity = this;
        setClickListener(mBinding.layoutIncludeNoNet.buttonRetry);
        checkNetConnection();

    }

    private void initViews() {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId());
        mLoader = new Loader(this);
        Intent intent = getIntent();
        if (intent != null) {
            mLoader.show();
            intentValue = intent.getStringExtra(Constants.SharedPrefCredential.PRODUCT_DETAIL_INTENT);
            presenter.getProductDetailsResponse(intentValue, this);
        }

        cartInventory = new CustomProductInventory();

        mColorAdapter = new ChooseColorAdapter(new ArrayList<>(), this);
        mSizeAdapter = new ChooseSizeAdapter(new ArrayList<>(), this);
        mImageAdapter = new ReviewImageAdapter(new ArrayList<>(), ProductDetailsActivity.this,this);
        mProductAdapter = new InterestProductAdapter(new ArrayList<>(), this);
        mColorAdapter.serOnClickListener(this);
        mSizeAdapter.serOnClickListener(this);
        setClickListener(mBinding.fabCart, mBinding.layoutReviewProduct.textViewSeemore,
                mBinding.layoutImage.imageViewFavourite, mBinding.layoutBuy.buttonBuyNow);


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
        updateMenu();
  /*      if (isFromReview)
            presenter.getProductDetailsResponse(intentValue, this);*/

    }

    @Override
    public void onCheckBannerAdViewStatus(String status) {
        if (status.equals(Constants.Preferences.STATUS_ON)){
            mBinding.adView.setVisibility(View.VISIBLE);
        }else{
            mBinding.adView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAddToCartServerSuccess(InventoryResponse body,int track) {

        Toast.makeText(this, "This Product added into cart!", Toast.LENGTH_SHORT).show();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String data=SharedPref.getSharedPref(ProductDetailsActivity.this).read(Constants.Preferences.SAVE_CURRENT_DATA);
                if (data !=null){
                    Gson gson=new Gson();
                    CustomProductInventory customProductInventory = gson.fromJson(data, CustomProductInventory.class);
                    DatabaseUtil.on().insertItem(customProductInventory);
                    updateMenu();
                    if (track == 1) {
                        Intent intent = new Intent(ProductDetailsActivity.this, CartActivity.class);
                        startActivity(intent);
                    }
                }

            }
        });

    }

    @Override
    public void onAddToCartError(String message) {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.text_view_seemore:
                ReviewDetailsActivity.runActivity(this, productId, ordered);
                // finish();
                break;

            case R.id.image_view_favourite:
                if (CustomSharedPrefs.getUserStatus(this)) {
                    if (mProductModel.isFav != 1) {
                        mBinding.layoutImage.imageViewFavourite.setClickable(false);
                        presenter.getAddFavouriteResponse(this, mProductModel.id, CustomSharedPrefs.getLoggedInUserId(this));

                    } else {
                        mBinding.layoutImage.imageViewFavourite.setClickable(false);
                        presenter.getRemoveFavouriteResponse(this, mProductModel.id, CustomSharedPrefs.getLoggedInUserId(this));
                    }
                    mLoader.show();
                } else {
                    UIHelper.openSignInPopUp(this);
                }
                break;

            case R.id.button_buy_now:
                if (selectedModelList != null && attributeModels != null) {
                    if (selectedModelList.size() != attributeModels.size()) {
                        isCheckListEmpty = true;
                    } else {
                        isCheckListEmpty = false;
                    }
                }
                if (!isListEmpty) {
                    if (!isCheckListEmpty) {
                        Collections.sort(attribuiteIdList, new Comparator<AttributeWithView>() {
                            @Override
                            public int compare(AttributeWithView o1, AttributeWithView o2) {
                                return Integer.compare(o1.id, o2.id);
                            }
                        });
                        presenter.addCart(1, this, selectedModelList, attribuiteIdList, inventoryModels, cartInventory, isListEmpty);
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.select_all_attributes), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    presenter.addCart(1, this, selectedModelList, attribuiteIdList, inventoryModels, cartInventory, isListEmpty);
                }
                break;
            case R.id.fab_cart:

                if (selectedModelList != null && attributeModels != null) {
                    if (selectedModelList.size() != attributeModels.size()) {
                        isCheckListEmpty = true;
                    } else {
                        isCheckListEmpty = false;
                    }
                }

                if (!isListEmpty) {
                    if (!isCheckListEmpty) {
                        Collections.sort(attribuiteIdList, new Comparator<AttributeWithView>() {
                            @Override
                            public int compare(AttributeWithView o1, AttributeWithView o2) {
                                return Integer.compare(o1.id, o2.id);
                            }
                        });
                        presenter.addCart(2, this, selectedModelList, attribuiteIdList, inventoryModels, cartInventory, isListEmpty);
                        // need to call here
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.select_all_attributes), Toast.LENGTH_SHORT).show();
                        //openChooseColorAlert(mFinalI,attributeModels.get(mFinalI).title);
                    }
                } else {
                    presenter.addCart(2, this, selectedModelList, attribuiteIdList, inventoryModels, cartInventory, isListEmpty);
                    //need to call here
                }


                break;

                case R.id.button_retry:
                    mLoader.show();
                    presenter.getProductDetailsResponse(intentValue, this);
                    break;

        }

    }


    /**
     * this api is used to open color choose pop up
     */
    private void openChooseColorAlert(int adapterPosition,String title) {
        isApapterTriggered = false;
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_choose_color, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(true);
        dialogColor = alertDialogBuilder.create();
        dialogColor.show();
        TextView textTitle = dialogColor.findViewById(R.id.text_view_title);
        textTitle.setText(getResources().getString(R.string.select_text)+title);
        Button buttonOk = dialogColor.findViewById(R.id.button_ok);
        Button buttonNo = dialogColor.findViewById(R.id.button_cancel);
        buttonOk.setOnClickListener(v -> dialogColor.dismiss());
        buttonNo.setOnClickListener(v -> {
            if(isApapterTriggered) {
                if (!attribuiteIdList.isEmpty()) {
                    for (int i = 0; i < attribuiteIdList.size(); i++) {
                        if (i == adapterPosition) {
                            ((TextView) attribuiteIdList.get(i).view.findViewById(R.id.text_view_select2)).setText("Select");
                            selectedModelList.remove(i);
                            break;
                        }
                    }
                }
            }
            dialogColor.dismiss();

        });

        RecyclerView recyclerViewColor = dialogColor.findViewById(R.id.recycler_view_choose_color);
        recyclerViewColor.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        for (int i = 0; i < mAdapterList.size(); i++) {
            if (i == adapterPosition) {
                mAdapterList.get(i).serOnClickListener(this);
                recyclerViewColor.setAdapter(mAdapterList.get(i));
                break;
            }
        }
    }

    /**
     * initialization of {@link InterestProductAdapter } with recycler view
     */
    private void initInterestProductRecycler() {
        mBinding.layoutInterestProduct.recyclerViewInterest.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.layoutInterestProduct.recyclerViewInterest.setAdapter(mProductAdapter);
        mProductAdapter.setClickListener(this);

    }

    /**
     * initialization of {@link ReviewImageAdapter } with recycler view
     */
    private void initReviewRecycler() {
        mBinding.layoutReviewProduct.recyclerViewReview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.layoutReviewProduct.recyclerViewReview.setAdapter(mImageAdapter);

    }

    /**
     * init toolbar
     */
    private void initToolbar() {
        TextView toobarTitle = findViewById(R.id.toolbar_title);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toobarTitle.setText(this.getString(R.string.title_product_detail));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void stopUI() {
        if (productDetailsActivity != null)
            productDetailsActivity = null;
    }

    @Override
    protected ProductDetailsPresenter initPresenter() {
        return new ProductDetailsPresenter();
    }

    @Override
    public void onProductDetailsSuccess(ProductDetailsResponse detailsResponse) {
        if (detailsResponse != null) {
            if (detailsResponse.statusCode == HttpURLConnection.HTTP_OK) {
                initViewWithResponse(detailsResponse);
            }
            if (mLoader !=null){
                mLoader.stopLoader();
            }
        }
        mBinding.layoutIncludeNoNet.linearNoInternet.setVisibility(View.GONE);
        mBinding.scrollView.setVisibility(View.VISIBLE);
    }

    /**
     * init view with server response
     *
     * @param detailsResponse : ProductDetailsResponse
     */
    private void initViewWithResponse(ProductDetailsResponse detailsResponse) {
        prepareImageList(detailsResponse.detailsDataModel.imageList);

        mProductModel = detailsResponse.detailsDataModel;
        if (detailsResponse.detailsDataModel != null) {
            ordered = mProductModel.ordered;
            initTopView();
            initImageLayoutAndReview();
            checkAttributeAvailability(detailsResponse.detailsDataModel.attribute);

            if (detailsResponse.detailsDataModel.inventory != null) {
                inventoryModels = detailsResponse.detailsDataModel.inventory;
            }
            userReviewImage(detailsResponse.detailsDataModel.reviewImages);
            mImageReviewSliderList= detailsResponse.detailsDataModel.reviewImages;
            userReviewInfo(detailsResponse.detailsDataModel.recentReview, detailsResponse.detailsDataModel.ratingCount);
            if (detailsResponse.detailsDataModel.interestedProduct != null) {
                mProductAdapter.addItem(detailsResponse.detailsDataModel.interestedProduct);
            } else mBinding.layoutInterestProduct.layoutInterestedProduct.setVisibility(View.GONE);
        }
    }

    /**
     * this method used to see attribute availability and firing action on ite
     *
     * @param attribute : List<DetailsAttributeModel>
     */

    @SuppressLint("ResourceType")
    private void checkAttributeAvailability(List<DetailsAttributeModel> attribute) {
        if (attribute != null && !attribute.isEmpty()) {
            attributeModels = attribute;
            LinearLayout rl = findViewById(R.id.linear_layout);
            LayoutInflater layoutInflater = (LayoutInflater)
                    this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < attribute.size(); i++) {
                View to_add = layoutInflater.inflate(R.layout.layout_size,
                        rl, false);
                to_add.setId(i);
                mAdapterList.set(i, new ChooseColorAdapter(new ArrayList<>(), this));
                mAdapterList.get(i).addItem(attribute.get(i).attributeList);
                attribuiteIdList.add(new AttributeWithView(to_add, attribute.get(i).id));
                ((TextView) to_add.findViewById(R.id.text_view_size_title)).setText("Select " + attribute.get(i).title);
                rl.addView(to_add);
                int mFinalI = i;

                to_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openChooseColorAlert(mFinalI,attribute.get(mFinalI).title);
                    }
                });

            }
        } else {
            isListEmpty = true;
        }
    }


    /**
     * init view with server response
     */
    private void initImageLayoutAndReview() {
        if (mProductModel != null) {
            mBinding.layoutBuy.textViewTitle.setText(mProductModel.title);
            mBinding.layoutBuy.textViewRating.setText("" + mProductModel.avgRating);
            mBinding.layoutBuy.ratingBarReview.setRating(mProductModel.avgRating);
            int count = mProductModel.ratingCount;
            mBinding.layoutBuy.textViewReviewCount.setText(count <= 1 ? count + " "+getResources().getString(R.string.review_text) : count + " "+getResources().getString(R.string.reviews_text));
            productId = "" + mProductModel.id;
            String productName = "" + mProductModel.title;
            String productDes = "" + mProductModel.description;

            //String text1 = "Product ID : <font color='#218B95'>" + productId + "</font>";
            String text2 = getResources().getString(R.string.product_name_font_color) + productName + "</font>";
            String text3 = getResources().getString(R.string.product_description_font_color) + productDes + "</font>";
            //mBinding.layoutDetails.textViewProductId.setText(Html.fromHtml(text1), TextView.BufferType.SPANNABLE);
           // mBinding.layoutDetails.textViewProductName.setText(Html.fromHtml(text2), TextView.BufferType.SPANNABLE);
            mBinding.layoutDetails.textViewProductDescription.setText(Html.fromHtml(text3), TextView.BufferType.SPANNABLE);
        }
    }


    /**
     * init view with server response
     */
    private void initTopView() {
        if (mProductModel != null) {
            cartInventory.productName = mProductModel.title;
            cartInventory.price = mProductModel.currentPrice;
            cartInventory.newPrice = mProductModel.currentPrice;
            cartInventory.imageUri = mProductModel.imageUri;
            cartInventory.currentQuantity = 1;

            if (mProductModel.isFav == 1) {
                mBinding.layoutImage.imageViewFavourite.setImageResource(R.drawable.ic_fav_fill);
            }
            mBinding.layoutImage.textViewFavCount.setText("" + mProductModel.favouriteCount);
            String currency = CustomSharedPrefs.getCurrency(this);
            mBinding.layoutImage.textViewPrice.setText(""+UtilityClass.getCurrencySymbolAndAmount(this,mProductModel.currentPrice));
        }
    }


    /**
     * init view with server response
     */
    private void userReviewInfo(RecentReviewModel recentReview, int count) {
        if (recentReview != null) {
            UIHelper.setThumbImageUriInView(mBinding.layoutReviewProduct.imageViewProfile, recentReview.imageUri);
            mBinding.layoutReviewProduct.textViewUsername.setText(recentReview.username);
            if (count != 0)
                mBinding.layoutReviewProduct.textViewReviewCount.setText("(" + count + ")");
            mBinding.layoutReviewProduct.ratingBarReview.setRating(recentReview.rating);
            mBinding.layoutReviewProduct.textViewReviewText.setText(recentReview.reviewMessage);
            mBinding.layoutReviewProduct.textViewDate.setText(UtilityClass.getDateStringFromDateValue(recentReview.time,
                    Constants.DateFormat.DATE_FORMAT_TIME));
            mBinding.layoutReviewProduct.layout2.setVisibility(View.VISIBLE);
        } else {
            mBinding.layoutReviewProduct.layout2.setVisibility(View.GONE);
        }
    }


    /**
     * init view with server response
     */
    private void userReviewImage(List<ProductDetailsImageModel> reviewImages) {
        if (reviewImages != null && reviewImages.size() != 0) {
            mImageAdapter.initItem(productId, ordered);
            mImageAdapter.addItem(reviewImages);
            mBinding.layoutReviewProduct.recyclerViewReview.setVisibility(View.VISIBLE);
         //   mBinding.layoutReviewProduct.lineView1.setVisibility(View.VISIBLE);
            mBinding.layoutReviewProduct.lineView.setVisibility(View.VISIBLE);
        } else {
            mBinding.layoutReviewProduct.recyclerViewReview.setVisibility(View.GONE);
          //  mBinding.layoutReviewProduct.lineView1.setVisibility(View.GONE);
            mBinding.layoutReviewProduct.lineView.setVisibility(View.GONE);

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
     * init view with server response
     */
    private void initViewPager(List<ProductDetailsImageModel> imgList) {
        mViewPagerAdapter = new ViewPagerAdapter(imgList, this);
        mBinding.layoutImage.viewPager.setAdapter(mViewPagerAdapter);
        mBinding.layoutImage.pagerIndicator.setViewPager(mBinding.layoutImage.viewPager);
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

    @Override
    public void onProductDetailsError(String errorMessage) {
        if (mLoader !=null){
            mLoader.stopLoader();
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavSuccess(AddFavouriteResponse response) {
        if (response != null) {
            if (response.statusCode == HttpURLConnection.HTTP_OK) {
                mBinding.layoutImage.imageViewFavourite.setImageResource(R.drawable.ic_fav_fill);
                mProductModel.isFav = 1;
                mBinding.layoutImage.imageViewFavourite.setClickable(true);
                mProductModel.favouriteCount = mProductModel.favouriteCount + 1;
                mBinding.layoutImage.textViewFavCount.setText("" + mProductModel.favouriteCount);

            }
            mLoader.stopLoader();
        }
    }

    @Override
    public void onFavError(String errorMessage) {
        mLoader.stopLoader();
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemoveFavSuccess(AddFavouriteResponse response) {
        if (response != null) {
            if (response.statusCode == HttpURLConnection.HTTP_OK) {
                mBinding.layoutImage.imageViewFavourite.setImageResource(R.drawable.ic_favorite_white);
                mProductModel.isFav = 5;
                mBinding.layoutImage.imageViewFavourite.setClickable(true);
                mProductModel.favouriteCount = mProductModel.favouriteCount - 1;
                mBinding.layoutImage.textViewFavCount.setText("" + mProductModel.favouriteCount);
            }
            mLoader.stopLoader();
        }
    }

    @Override
    public void onItemClick(View view, AttributeValueModel item, int i) {
        if (item != null && !attribuiteIdList.isEmpty()) {
            for (int j = 0; j < attribuiteIdList.size(); j++) {
                isApapterTriggered = true;
                if (item.attribute == attribuiteIdList.get(j).id) {
                    ((TextView) attribuiteIdList.get(j).view.findViewById(R.id.text_view_select2)).setText(item.title);
                    break;
                }
            }

            boolean isMatched = false;
            int position = -1;
            if (!selectedModelList.isEmpty()) {
                for (int j = 0; j < selectedModelList.size(); j++) {
                    if (item.attribute != selectedModelList.get(j).attribute) {
                        isMatched = false;
                    } else {
                        isMatched = true;
                        position = j;
                        break;
                    }
                }
                if (isMatched) {
                    if (position != -1)
                        selectedModelList.set(position, item);
                } else {
                    selectedModelList.add(item);
                }
            } else {
                selectedModelList.add(item);
            }
        }
    }


    @Override
    public void onClickListener(InterestedProductModel mModel, int position) {
        if (mModel != null) {
            Intent intent = new Intent(this, ProductDetailsActivity.class);
            intent.putExtra(Constants.SharedPrefCredential.PRODUCT_DETAIL_INTENT, "" + mModel.id);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onItemClick(View v,ProductDetailsImageModel item, int i) {
        if (v.getId()==R.id.image_view){
            ImageReviewSliderActivity.runActivity(this,mImageReviewSliderList);
        }

    }

}
