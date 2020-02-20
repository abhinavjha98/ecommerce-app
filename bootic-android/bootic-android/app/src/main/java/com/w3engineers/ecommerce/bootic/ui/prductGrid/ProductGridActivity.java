package com.w3engineers.ecommerce.bootic.ui.prductGrid;

import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.droidnet.DroidListener;
import com.like.LikeButton;
import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.CustomMenuBaseActivity;
import com.w3engineers.ecommerce.bootic.data.helper.base.ItemClickListener;
import com.w3engineers.ecommerce.bootic.data.helper.models.ProductModel;
import com.w3engineers.ecommerce.bootic.data.helper.response.AddFavouriteResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.ProductGridResponse;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.Loader;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;
import com.w3engineers.ecommerce.bootic.data.util.SharedPref;
import com.w3engineers.ecommerce.bootic.data.util.UIHelper;
import com.w3engineers.ecommerce.bootic.data.util.UtilityClass;
import com.w3engineers.ecommerce.bootic.ui.productdetails.ProductDetailsActivity;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ProductGridActivity extends CustomMenuBaseActivity<ProductGridMvpView, ProductGridPresenter>
        implements ProductGridMvpView, ItemClickListener<ProductModel>, DroidListener {

    TextView toobarTitle;
    Toolbar toolbar;
    ImageView toolbarLogo;

    private Loader mLoader;
    RecyclerView recyclerViewProduct;
    private String userID = "";
    private String categoryId = "";
    private int pageNumber = 1;
    private ProductRecylerViewAdapter mAdapter;
    private ProductGridResponse mProductResponse;
    private ProgressBar progressBar;
    private boolean hasMore, isFirstTime;
    private LinearLayout linearLayout, linearLayoutNoDataFound, linearLayoutNoInternet;
    private ProductModel productModel;
    private View views;
    private Button mButtonRetry;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_product_grid;
    }

    @Override
    protected void startUI() {
        recyclerViewProduct = findViewById(R.id.rv_product_grid);
        progressBar = findViewById(R.id.progress_bar);
        linearLayout = findViewById(R.id.layout_total);
        linearLayoutNoDataFound = findViewById(R.id.layout_no_data);
        linearLayoutNoInternet = findViewById(R.id.linear_no_internet);
        mButtonRetry = findViewById(R.id.button_retry);
        initToolbar();
        Intent intent = getIntent();
        if (CustomSharedPrefs.getUserStatus(this)) {
            userID = CustomSharedPrefs.getLoggedInUserId(this);
        }
        if (intent != null) {
            categoryId = "" + intent.getIntExtra(Constants.SharedPrefCredential.INTENT_CATEGORY_ID, -1);
        }

        mAdapter = new ProductRecylerViewAdapter(new ArrayList<>(), this, false);
        mAdapter.setItemClickedListener(this);
        presenter.getProductList(categoryId, this, userID, "" + pageNumber);
        Log.d("UserInfo",categoryId + " "+userID);
        mLoader = new Loader(this);
        mLoader.show();
        checkNetConnection();
        settingRecylerView();
        loadMore();
        setClickListener(mButtonRetry);
    }


    /**
     * check internet
     */
    private void checkNetConnection() {
        if (!NetworkHelper.hasNetworkAccess(this)) {
            //no net
            mLoader.stopLoader();
            linearLayoutNoInternet.setVisibility(View.VISIBLE);
        } else {
            linearLayoutNoInternet.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.button_retry:
                presenter.getProductList(categoryId, this, userID, "" + pageNumber);
                break;
        }
    }

    /**
     * init toolbar
     */
    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toobarTitle = findViewById(R.id.toolbar_title);
        toolbarLogo = findViewById(R.id.toolbar_logo);
        toolbar.setTitle("");
        toolbarLogo.setVisibility(View.VISIBLE);
        toobarTitle.setVisibility(View.GONE);
        toobarTitle.setText(this.getString(R.string.app_name));
        String toolbarTitle = getIntent().getStringExtra(UtilityClass.TOOLBAR_TITLE);
        if (toolbarTitle != null) {
            toobarTitle.setText(toolbarTitle);
            toolbarLogo.setVisibility(View.GONE);
            toobarTitle.setVisibility(View.VISIBLE);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void stopUI() {

    }

    /**
     * setting pagination
     */
    private void loadMore() {
        linearLayout.getViewTreeObserver().
                addOnScrollChangedListener(() -> {
                    if (linearLayout.getChildAt(0).getBottom()
                            == (linearLayout.getHeight() + linearLayout.getScrollY())) {
                        if (hasMore) {
                            if (mProductResponse != null && mProductResponse.dataModel != null &&
                                    mProductResponse.statusCode == HttpURLConnection.HTTP_OK) {
                                pageNumber = pageNumber + 1;
                                callCounter();
                                presenter.getProductList(categoryId, this, userID, "" + pageNumber);
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    /**
     * hiding and showing loader based on counter
     */
    private void callCounter() {
        new CountDownTimer(Constants.DefaultValue.DELAY_INTERVAL, 1) {
            public void onTick(long millisUntilFinished) {
                progressBar.setVisibility(View.VISIBLE);
            }

            public void onFinish() {
                progressBar.setVisibility(View.GONE);
            }
        }.start();
    }


    @Override
    protected ProductGridPresenter initPresenter() {
        return new ProductGridPresenter();
    }

    /**
     * setting recycler with adapter
     */
    private void settingRecylerView() {
        recyclerViewProduct.setHasFixedSize(false);
        recyclerViewProduct.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewProduct.setNestedScrollingEnabled(false);
        recyclerViewProduct.setAdapter(mAdapter);
        hasMore = true;
    }

    @Override
    public void onItemClick(View view, ProductModel item, int i) {
        if (item != null) {
            if (view.getId() == R.id.btn_favourite) {
                if (CustomSharedPrefs.getUserStatus(this)) {
                    productModel = item;
                    views = view;
                    mLoader.show();
                    if (item.isFavourite != 1) {
                        presenter.getAddFavouriteResponse(this, "" + item.id, CustomSharedPrefs.getLoggedInUserId(this));
                    } else {
                        presenter.getRemoveFavouriteResponse(this, "" + item.id, CustomSharedPrefs.getLoggedInUserId(this));
                    }
                }
            }
        } else {
            UIHelper.openSignInPopUp(this);
        }
    }

    @Override
    public void onFavSuccess(AddFavouriteResponse response) {
        mLoader.stopLoader();
        if (response != null) {
            if (response.statusCode == HttpURLConnection.HTTP_OK) {
                if (productModel != null) {
                    productModel.isFavourite = 1;
                }
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
                ((LikeButton) views).setLiked(true);
            } else {
                ((LikeButton) views).setLiked(false);
            }
        }
    }

    @Override
    public void onFavError(String errorMessage) {
        mLoader.stopLoader();
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemoveFavSuccess(AddFavouriteResponse response) {
        mLoader.stopLoader();
        if (response != null) {
            if (response.statusCode == HttpURLConnection.HTTP_OK) {
                if (productModel != null) {
                    productModel.isFavourite = 2;
                }
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show();
                ((LikeButton) views).setLiked(false);
            } else ((LikeButton) views).setLiked(true);
        }
    }

    @Override
    public void onProductListSuccess(ProductGridResponse products) {
        mProductResponse = products;
        if (mProductResponse.dataModel != null && mProductResponse.statusCode == HttpURLConnection.HTTP_OK
                && !mProductResponse.dataModel.isEmpty()) {
            mAdapter.addItem(mProductResponse.dataModel);
            linearLayoutNoDataFound.setVisibility(View.GONE);
            isFirstTime = true;
        } else {
            if (!isFirstTime)
                linearLayoutNoDataFound.setVisibility(View.VISIBLE);
            // Toast.makeText(this, mProductResponse.message, Toast.LENGTH_SHORT).show();
            hasMore = false;
        }
        linearLayoutNoInternet.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        mLoader.stopLoader();
    }

    @Override
    public void onProductListError(String errorMessage) {
        if (mLoader != null) {
            mLoader.show();
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {

    }


}
