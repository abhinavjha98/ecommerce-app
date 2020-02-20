package com.w3engineers.ecommerce.bootic.ui.category;

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
import com.w3engineers.ecommerce.bootic.data.helper.response.AllCategoryResponse;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.Loader;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;


import java.net.HttpURLConnection;
import java.util.ArrayList;

public class CategoryActivity extends CustomMenuBaseActivity<CategoryMvpView, CategoryPresenter>
        implements CategoryMvpView {

    TextView toobarTitle;
    Toolbar toolbar;
    ImageView toolbarLogo;
    RecyclerView recyclerViewCategory;
    private Loader mLoader;
    private boolean hasMore;
    private ProgressBar progressBar;
    private AllCategoryResponse mCategoryResponse;
    private int pageNumber = 1;
    private CategoryRecylerViewGridAdapter mAdapter;
    private boolean isFirstTime;
    private LinearLayout linearLayoutNoDataFound, linearLayout1,linearLayoutNoInternet;
    private Button mButtonRetry;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_category;
    }

    @Override
    protected void startUI() {
        initToolbar();
        initViews();
        mLoader = new Loader(this);
        mLoader.show();
        checkNetConnection();
        mAdapter = new CategoryRecylerViewGridAdapter(new ArrayList<>(), this);
        settingRecylerView();
        presenter.getCategories(this, "" + pageNumber);
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
            presenter.getCategories(this, "" + pageNumber);
        }
    }


    private void initViews() {
        recyclerViewCategory = findViewById(R.id.rv_product_category_grid);
        progressBar = findViewById(R.id.progress_bar);
        linearLayoutNoDataFound = findViewById(R.id.layout_no_data);
        linearLayout1 = findViewById(R.id.linear);
        linearLayoutNoInternet = findViewById(R.id.linear_no_internet);
        mButtonRetry = findViewById(R.id.button_retry);
    }

    @Override
    protected void stopUI() {

    }

    private void loadMore() {
        linearLayout1.getViewTreeObserver().addOnScrollChangedListener(() ->
        {
            if (linearLayout1.getChildAt(0).getBottom()
                    == (linearLayout1.getHeight() + linearLayout1.getScrollY())) {
                if (hasMore) {
                    if (mCategoryResponse != null && mCategoryResponse.dataList != null && mCategoryResponse.statusCode == HttpURLConnection.HTTP_OK) {
                        pageNumber = pageNumber + 1;
                        callCounter();
                        presenter.getCategories(CategoryActivity.this, pageNumber + "");
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

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

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toobarTitle = findViewById(R.id.toolbar_title);
        toolbarLogo = findViewById(R.id.toolbar_logo);
        toolbar.setTitle("");
        toobarTitle.setText(this.getString(R.string.app_name));
        toolbarLogo.setVisibility(View.VISIBLE);
        toobarTitle.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void settingRecylerView() {
        recyclerViewCategory.setHasFixedSize(false);
        recyclerViewCategory.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewCategory.setNestedScrollingEnabled(false);
        recyclerViewCategory.setAdapter(mAdapter);
        hasMore = true;
    }

    @Override
    protected CategoryPresenter initPresenter() {
        return new CategoryPresenter();
    }


    @Override
    public void onCategoryListSuccess(AllCategoryResponse categoryResponse) {
        mCategoryResponse = categoryResponse;
        if (mCategoryResponse.dataList != null && mCategoryResponse.statusCode == HttpURLConnection.HTTP_OK) {
            mAdapter.addItem(mCategoryResponse.dataList);
            isFirstTime = true;
            linearLayoutNoDataFound.setVisibility(View.GONE);
        } else {
            hasMore = false;
            if (!isFirstTime) {
                linearLayoutNoDataFound.setVisibility(View.VISIBLE);
            }
        }
        progressBar.setVisibility(View.GONE);
        linearLayoutNoInternet.setVisibility(View.GONE);
        if(mLoader !=null){
            mLoader.stopLoader();
        }

    }

    @Override
    public void onCategoryListError(String message,boolean isNetOn) {
        if (mLoader!=null){
            mLoader.stopLoader();
        }
        if (isNetOn){
            linearLayoutNoInternet.setVisibility(View.GONE);
        }else {
            linearLayoutNoInternet.setVisibility(View.VISIBLE);
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
