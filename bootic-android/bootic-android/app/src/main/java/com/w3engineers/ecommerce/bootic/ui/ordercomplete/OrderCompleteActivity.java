package com.w3engineers.ecommerce.bootic.ui.ordercomplete;

import android.os.CountDownTimer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BaseActivity;
import com.w3engineers.ecommerce.bootic.data.helper.response.OrderListResponse;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.Loader;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;
import com.w3engineers.ecommerce.bootic.databinding.ActivityOrderCompleteBinding;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class OrderCompleteActivity extends BaseActivity<OrderCompleteMvpView, OrderCompletePresenter>
        implements OrderCompleteMvpView {

    private ActivityOrderCompleteBinding mBinding;
    private OrderMainAdapter mAdapter;
    private Loader mLoader;
    private OrderListResponse orderListResponse;
    private int pageNumber = 1;
    private boolean hasMore;
    private String userID;
    private boolean isFirstLoad;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_complete;
    }

    @Override
    protected void startUI() {
        mBinding = (ActivityOrderCompleteBinding) getViewDataBinding();
        initToolbar();
        mLoader = new Loader(this);
        userID = CustomSharedPrefs.getLoggedInUserId(this);
        Log.d("UserID",userID);
        initRecycler();
        mLoader.show();
        checkNetConnection();
        presenter.getOrderedList(this, userID, "" + pageNumber);
        loadMore();
        setClickListener(mBinding.layoutIncludeNoNet.buttonRetry);
    }

    /**
     * check internet
     */
    private void checkNetConnection() {
        if (!NetworkHelper.hasNetworkAccess(this)) {
            //no net
            mLoader.stopLoader();
            mBinding.layoutIncludeNoNet.linearNoInternet.setVisibility(View.VISIBLE);
        } else {
            mBinding.layoutIncludeNoNet.linearNoInternet.setVisibility(View.GONE);

        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.button_retry) {
            presenter.getOrderedList(this, userID, "" + pageNumber);
        }
    }

    /**
     * this api is used for pagination purpose
     */
    private void loadMore() {
        mBinding.recyclerViewMainOrder.getViewTreeObserver().
                addOnScrollChangedListener(() ->
                {
                    if (mBinding.recyclerViewMainOrder.getChildAt(0).getBottom()
                            == (mBinding.recyclerViewMainOrder.getHeight() + mBinding.recyclerViewMainOrder.getScrollY())) {
                        if (hasMore) {
                            if (orderListResponse != null && orderListResponse.orderModels != null && orderListResponse.statusCode == HttpURLConnection.HTTP_OK) {
                                pageNumber = pageNumber + 1;
                                callCounter();
                                presenter.getOrderedList(this, userID, "" + pageNumber);
                            }
                        } else {
                            mBinding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    /**
     * start a counter for stooping loader
     */
    private void callCounter() {
        new CountDownTimer(Constants.DefaultValue.DELAY_INTERVAL, 1) {
            public void onTick(long millisUntilFinished) {
                mBinding.progressBar.setVisibility(View.VISIBLE);
            }

            public void onFinish() {
                mBinding.progressBar.setVisibility(View.GONE);
            }
        }.start();
    }

    /**
     * initializing recycler view
     */
    private void initRecycler() {
        mAdapter = new OrderMainAdapter(new ArrayList<>(), this);
        mBinding.recyclerViewMainOrder.setAdapter(mAdapter);
        mBinding.recyclerViewMainOrder.setLayoutManager(new LinearLayoutManager(this));
        hasMore = true;
    }

    /**
     * toolbar initialization
     */
    private void initToolbar() {
        TextView toobarTitle = findViewById(R.id.toolbar_title);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toobarTitle.setText(getString(R.string.order_complete));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void stopUI() {

    }

    @Override
    protected OrderCompletePresenter initPresenter() {
        return new OrderCompletePresenter();
    }

    @Override
    public void onGettingOrderInfoSuccess(OrderListResponse response) {
        if (response != null) {
            orderListResponse = response;
            if (response.statusCode == HttpURLConnection.HTTP_OK) {
                isFirstLoad = true;
                mBinding.textViewEmpty.setVisibility(View.INVISIBLE);
                mBinding.layoutNoData.setVisibility(View.GONE);
                mAdapter.addItem(orderListResponse.orderModels);
            } else {
                hasMore = false;
                if(!isFirstLoad){
                    mBinding.layoutNoData.setVisibility(View.VISIBLE);
                }
            }
            mBinding.progressBar.setVisibility(View.GONE);
            mLoader.stopLoader();
            mBinding.layoutIncludeNoNet.linearNoInternet.setVisibility(View.GONE);
        }


    }

    @Override
    public void onGettingOrderInfoError(String errorMessage,boolean isNetON) {
        if (mLoader !=null){
            mLoader.stopLoader();
        }
        if (isNetON){
            mBinding.layoutNoData.setVisibility(View.VISIBLE);
            mBinding.layoutIncludeNoNet.linearNoInternet.setVisibility(View.GONE);
        }else {
            mBinding.layoutNoData.setVisibility(View.GONE);
            mBinding.layoutIncludeNoNet.linearNoInternet.setVisibility(View.VISIBLE);
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
