package com.w3engineers.ecommerce.bootic.ui.myfavourite;

import android.os.CountDownTimer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.CustomMenuBaseActivity;
import com.w3engineers.ecommerce.bootic.data.helper.response.ProductGridResponse;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.Loader;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;
import com.w3engineers.ecommerce.bootic.data.util.UtilityClass;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class UserFavActivity extends CustomMenuBaseActivity<UserFavMvpView, UserFavPresenter> implements UserFavMvpView {

    TextView toobarTitle;
    Toolbar toolbar;
    ImageView toolbarLogo;
    private int pageNumber = 1;
    private ProductGridResponse mProductResponse;
    private boolean hasMore;
    private ProgressBar progressBar;
    private LinearLayout linearLayout,linearLayoutNoDataFound,linearLayoutNoInternet;

    RecyclerView recyclerViewProduct;
    private Loader mLoader;
    private FavProductRecylerViewAdapter mAdapter;
    private boolean isFirstTime;
    private Button mButtonRetry;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_fav;
    }

    @Override
    protected void startUI() {
        initToolbar();
        mLoader = new Loader(this);
        progressBar = findViewById(R.id.progress_bar);
        linearLayout = findViewById(R.id.layout_linear);
        mAdapter = new FavProductRecylerViewAdapter(new ArrayList<>(), this);
        recyclerViewProduct = findViewById(R.id.rv_user_fav_product_grid);
        linearLayoutNoDataFound = findViewById(R.id.layout_no_data);
        linearLayoutNoInternet = findViewById(R.id.linear_no_internet);
        mButtonRetry = findViewById(R.id.button_retry);
        settingRecylerView();
        mLoader.show();
        checkNetConnection();
        presenter.getAllFavouriteResponse(this, "" + pageNumber, CustomSharedPrefs.getLoggedInUserId(this));
        loadMore();
        setClickListener(mButtonRetry);
    }

    /**
     * check internet
     */
    private void checkNetConnection(){
        if (!NetworkHelper.hasNetworkAccess(this)){
            //no net
            mLoader.stopLoader();
            linearLayoutNoInternet.setVisibility(View.VISIBLE);
        }else {
            linearLayoutNoInternet.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.button_retry) {
            mLoader.show();
            presenter.getAllFavouriteResponse(this, "" + pageNumber, ""+CustomSharedPrefs.getLoggedInUserId(this));
        }
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
                            if (mProductResponse != null && mProductResponse.dataModel != null && mProductResponse.statusCode == HttpURLConnection.HTTP_OK) {
                                pageNumber = pageNumber + 1;
                                callCounter();
                                presenter.getAllFavouriteResponse(this, "" + pageNumber, CustomSharedPrefs.getLoggedInUserId(this));
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }

    /**
     * this api is used for hiding and showing loader
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
    protected UserFavPresenter initPresenter() {
        return new UserFavPresenter();
    }


    /**
     * setting recycler view with adapter
     */
    private void settingRecylerView() {
        recyclerViewProduct.setHasFixedSize(false);
        recyclerViewProduct.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewProduct.setNestedScrollingEnabled(false);
        recyclerViewProduct.setAdapter(mAdapter);
        hasMore = true;
    }


    @Override
    public void onGettingFavouriteSuccess(ProductGridResponse response) {
        if (response!= null) {
            mProductResponse = response;
            if (mProductResponse.dataModel != null && mProductResponse.statusCode == HttpURLConnection.HTTP_OK) {
                mAdapter.addItem(mProductResponse.dataModel);
                isFirstTime = true;
                linearLayoutNoDataFound.setVisibility(View.GONE);
            } else {
               if(!isFirstTime){
                   linearLayoutNoDataFound.setVisibility(View.VISIBLE);
               }
                hasMore = false;
            }
            progressBar.setVisibility(View.GONE);
            mLoader.stopLoader();
            linearLayoutNoInternet.setVisibility(View.GONE);
        }else {
            linearLayoutNoDataFound.setVisibility(View.VISIBLE);
            linearLayoutNoInternet.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGettingFavouriteError(String errorMessage,boolean isNetOn) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        if (mAdapter !=null){
            mLoader.stopLoader();
        }
        if (isNetOn){
            linearLayoutNoDataFound.setVisibility(View.VISIBLE);
        }else {
            linearLayoutNoDataFound.setVisibility(View.GONE);
        }
    }
}
