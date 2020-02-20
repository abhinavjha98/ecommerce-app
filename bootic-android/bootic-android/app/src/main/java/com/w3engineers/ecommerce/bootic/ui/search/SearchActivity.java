package com.w3engineers.ecommerce.bootic.ui.search;

import android.app.Activity;
import android.os.CountDownTimer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BaseActivity;
import com.w3engineers.ecommerce.bootic.data.helper.base.ItemClickListener;
import com.w3engineers.ecommerce.bootic.data.helper.models.ProductModel;
import com.w3engineers.ecommerce.bootic.data.helper.response.AddFavouriteResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.ProductGridResponse;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.UIHelper;
import com.w3engineers.ecommerce.bootic.ui.prductGrid.ProductRecylerViewAdapter;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class SearchActivity extends BaseActivity<SearchMvpView, SearchPresenter> implements SearchMvpView, ItemClickListener<ProductModel>, TextView.OnEditorActionListener {

    Toolbar toolbar;
    ImageButton btnSearch;
    EditText etSearch;
    TextView tvNoProducts;
    RecyclerView rvSerachProductGrid;
    RelativeLayout pbarContainer;
    private int pageNumber = 1;
    private String prevText = "";
    ProductRecylerViewAdapter mAdapter;
    private boolean hasMore, isFirst, isNotPagination;
    private ProgressBar progressBar;
    private LinearLayout linearLayout, linearLayoutNoDataFound;
    private ProductGridResponse mProductResponse;
    private String searchText = "";
    private ProductModel productModel;
    private View views;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void startUI() {
        pbarContainer = findViewById(R.id.pbar_container);
        pbarContainer.setVisibility(View.GONE);
        tvNoProducts = findViewById(R.id.tv_no_products);
        btnSearch = findViewById(R.id.btn_search);
        etSearch = findViewById(R.id.et_seach_product);
        progressBar = findViewById(R.id.progress_bar);
        linearLayout = findViewById(R.id.layout_linear);
        linearLayoutNoDataFound = findViewById(R.id.layout_no_data);

        settingToolBar();

        rvSerachProductGrid = findViewById(R.id.rv_serach_product_grid);
        mAdapter = new ProductRecylerViewAdapter(new ArrayList<>(), this, true);
        mAdapter.setItemClickedListener(this);
        getSearchText();
        settingProductsRV();
        loadMore();
        etSearch.setOnEditorActionListener(this);
    }

    /**
     * setting pagination
     */
    private void loadMore() {
        linearLayout.getViewTreeObserver().
                addOnScrollChangedListener(() ->
                {
                    if (linearLayout.getChildAt(0).getBottom()
                            == (linearLayout.getHeight() + linearLayout.getScrollY())) {
                        if (hasMore) {
                            if (mProductResponse != null && mProductResponse.dataModel != null && mProductResponse.statusCode == HttpURLConnection.HTTP_OK) {
                                pageNumber = pageNumber + 1;
                                callCounter();
                                isNotPagination = false;
                                presenter.makeSearch(this, searchText, "" + pageNumber, "" + CustomSharedPrefs.getLoggedInUserId(SearchActivity.this));
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            pbarContainer.setVisibility(View.GONE);
                        }
                    }
                });
    }

    /**
     * show and hide loader based on counter
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
     * getting search text from listener and make call to server
     */
    private void getSearchText() {

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                new CountDownTimer(Constants.DefaultValue.DELAY_INTERVAL, 1) {
                    public void onTick(long millisUntilFinished) {
                        pbarContainer.setVisibility(View.VISIBLE);
                        if (!prevText.equals(s.toString())) {
                            prevText = s.toString();
                            searchText = s.toString();
                            isNotPagination = true;
                            presenter.makeSearch(SearchActivity.this, s.toString(), "" + pageNumber, "" + CustomSharedPrefs.getLoggedInUserId(SearchActivity.this));
                        }
                    }

                    public void onFinish() {
                        pbarContainer.setVisibility(View.GONE);
                    }
                }.start();


            }
        });
    }

    @Override
    protected void stopUI() {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.btn_search) {
            /*String seacrhedtext = etSearch.getText().toString().trim();
            if (!seacrhedtext.equals("")) {
                pbarContainer.setVisibility(View.VISIBLE);
                presenter.makeSearch(SearchActivity.this, seacrhedtext);*/

        }
    }

    @Override
    protected SearchPresenter initPresenter() {
        return new SearchPresenter();
    }

    /**
     * setting recycler and adapter
     */
    private void settingProductsRV() {
        rvSerachProductGrid.setHasFixedSize(false);
        rvSerachProductGrid.setLayoutManager(new GridLayoutManager(this, 2));
        rvSerachProductGrid.setNestedScrollingEnabled(false);
        rvSerachProductGrid.setAdapter(mAdapter);
        hasMore = true;
    }

    /**
     * setting toolbar
     */
    public void settingToolBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onSearchSuccess(ProductGridResponse response) {
        mProductResponse = response;
        if (isNotPagination)
            mAdapter.clearList();
        if (mProductResponse.dataModel != null) {
            isFirst = true;
            if (mProductResponse.statusCode == HttpURLConnection.HTTP_OK) {
                mAdapter.addItem(mProductResponse.dataModel);
                //tvNoProducts.setVisibility(View.GONE);
                linearLayoutNoDataFound.setVisibility(View.GONE);
            } else {
                hasMore = false;
            }
            progressBar.setVisibility(View.GONE);
            pbarContainer.setVisibility(View.GONE);
        } else {
            if (isNotPagination) {
                //  tvNoProducts.setVisibility(View.VISIBLE);
                linearLayoutNoDataFound.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onSearchError(String errorMessage) {
        pbarContainer.setVisibility(View.GONE);
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        linearLayoutNoDataFound.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFavSuccess(AddFavouriteResponse response) {
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
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemoveFavSuccess(AddFavouriteResponse response) {
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
    public void onItemClick(View view, ProductModel item, int i) {
        if (item != null) {
            if (CustomSharedPrefs.getUserStatus(this)) {
                if (view.getId() == R.id.btn_favourite) {
                    productModel = item;
                    views = view;
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
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            String searchText = etSearch.getText().toString().trim();
            hideKeyboard(this);
            return true;
        }
        return false;
    }

    /**
     * Hide keyboard
     *
     * @param activity activity
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
