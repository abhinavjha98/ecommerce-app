package com.w3engineers.ecommerce.bootic.ui.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.like.LikeButton;
import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BaseFragment;
import com.w3engineers.ecommerce.bootic.data.helper.base.ItemClickListener;
import com.w3engineers.ecommerce.bootic.data.helper.models.ProductModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.Slider;
import com.w3engineers.ecommerce.bootic.data.helper.models.SliderMain;
import com.w3engineers.ecommerce.bootic.data.helper.response.AddFavouriteResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.MainProductResponse;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserRegistrationResponse;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.Loader;
import com.w3engineers.ecommerce.bootic.data.util.UtilityClass;
import com.w3engineers.ecommerce.bootic.databinding.FragmentDashboardBinding;
import com.w3engineers.ecommerce.bootic.ui.hearderview.SliderMainAdapter;
import com.w3engineers.ecommerce.bootic.ui.prductGrid.ProductRecylerViewAdapter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class DashboardFragment extends BaseFragment<DashboardMvpView, DashBoardPresenter>
        implements DashboardMvpView, ItemClickListener<ProductModel>{

    private boolean isRegistered;
    private UserRegistrationResponse currentUser;
    private int pageNumber = 1;
    private ProductRecylerViewAdapter recentProductAdapter;
    private Loader mLoader;
    private boolean hasMore, isNotFirstLoad, isNetworkOkay, isSplashResponseOkay;
    private FeatureProductAdapter adapterFeature;
    private FeatureProductAdapter adapterPopular;
    private CategoryRecylerViewAdapter adapterCategory;
    private MainProductResponse productResponse;
    private MainProductResponse responseFromSplash;
    private ProductModel productModel;
    private View views;
    private String userID = "";
    private FragmentDashboardBinding mBinding;
    private Activity mActivity;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dashboard;
    }


    @Override
    protected void startUI() {
        mBinding = (FragmentDashboardBinding) getViewDataBinding();
        mActivity = getActivity();
        mLoader = new Loader(mActivity);
        isRegistered = CustomSharedPrefs.getUserStatus(mActivity);
        if (isRegistered) {
            currentUser = CustomSharedPrefs.getLoggedInUser(mActivity);
        }

        initView();
        responseFromSplash = CustomSharedPrefs.getMainResponse(mActivity);
        isSplashResponseOkay = responseFromSplash.isOkay;
        if (isSplashResponseOkay) {
            setDatatoView(responseFromSplash);
        } else {
            isNotFirstLoad = false;
            presenter.settingProductPagination(mActivity, pageNumber + "", userID, "");
        }

    }



    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(this::checkConnection, 2000);
    }

    @Override
    protected void stopUI() {

    }

    @Override
    protected DashBoardPresenter initPresenter() {
        return new DashBoardPresenter();
    }

    @Override
    public void onProductListSuccess(MainProductResponse response) {
        setDatatoView(response);
    }

    @Override
    public void onProductListError(String errorMessage) {
        mLoader.stopLoader();
        // Toast.makeText(mActivity, errorMessage, Toast.LENGTH_SHORT).show();
        setAllTitleGone();
    }

    @Override
    public void onFavSuccess(AddFavouriteResponse response) {
        if (response != null) {
            if (response.statusCode == HttpURLConnection.HTTP_OK) {
                if (productModel != null) {
                    productModel.isFavourite = 1;
                }
                ((LikeButton) views).setLiked(true);
            } else {
                ((LikeButton) views).setLiked(false);
            }
        }
        mLoader.stopLoader();
    }

    @Override
    public void onFavError(String errorMessage) {
        if (mLoader != null) {
            mLoader.stopLoader();
        }
        Toast.makeText(mActivity, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemoveFavSuccess(AddFavouriteResponse response) {
        if (response != null) {
            if (response.statusCode == HttpURLConnection.HTTP_OK) {
                if (productModel != null) {
                    productModel.isFavourite = 2;
                }
                ((LikeButton) views).setLiked(false);
            } else ((LikeButton) views).setLiked(true);
        }
        mLoader.stopLoader();
    }

    @Override
    public void onItemClick(View view, ProductModel item, int i) {
        if (item != null) {
            if (view.getId() == R.id.btn_favourite) {
                productModel = item;
                mLoader.show();
                views = view;
                if (item.isFavourite != 1) {
                    presenter.getAddFavouriteResponse(mActivity, "" + item.id, CustomSharedPrefs.getLoggedInUserId(mActivity));
                } else {
                    presenter.getRemoveFavouriteResponse(mActivity, "" + item.id, CustomSharedPrefs.getLoggedInUserId(mActivity));
                }
            }
        }
    }

    /**
     * this api is to check internet connection
     * on connectivity success, invoke settingProductPagination() to load items
     */
    private void checkConnection() {
        if (!isNetworkOkay && !isSplashResponseOkay) {
            new CompositeDisposable().add(ReactiveNetwork
                    .observeNetworkConnectivity(mActivity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(connectivity -> {
                        if (connectivity.state() == NetworkInfo.State.CONNECTED)
                            presenter.settingProductPagination(mActivity, pageNumber + "", userID, "");

                    }, throwable -> {
                    }));
        }
    }

    /**
     * initializing views and adapter
     */
    private void initView() {
        initProductRecyclerView();
        initPopularRecyclerView();
        initFeatureRecyclerView();
        settingHorizontalCategoryList();
        settingMainSlider();
        if (isRegistered) {
            userID = CustomSharedPrefs.getLoggedInUserId(mActivity);
        }

        loadMore();
    }

    /**
     * this api is used for pagination
     */
    private void loadMore() {
        mBinding.scrollView.getViewTreeObserver().
                addOnScrollChangedListener(() ->
                {
                    if (mBinding.scrollView.getChildAt(0).getBottom()
                            == (mBinding.scrollView.getHeight() + mBinding.scrollView.getScrollY())) {
                        if (hasMore) {
                            if (productResponse != null && productResponse.dataModel.recentProduct != null) {
                                callCounter();
                                pageNumber = pageNumber + 1;
                                String existing = UtilityClass.objectToString(productResponse.dataModel.existing);
                                presenter.settingProductPagination(mActivity, pageNumber + "", userID, existing);
                            } else hasMore = false;
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
                mBinding.progressBar.setVisibility(View.VISIBLE);
            }

            public void onFinish() {
                mBinding.progressBar.setVisibility(View.GONE);
            }
        }.start();
    }

    /**
     * this method is for sliding latest offer
     */
    private void settingMainSlider() {
        Slider sliderMainList=CustomSharedPrefs.getSlider(getActivity() );
        PagerAdapter sliderMainAdapter = new SliderMainAdapter(getFragmentManager(), sliderMainList.mSliderMains);
        mBinding.vpSliderMain.setAdapter(sliderMainAdapter);

        dots(0,sliderMainList.mSliderMains);

        mBinding.vpSliderMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                dots(position,sliderMainList.mSliderMains);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * init {@link FeatureProductAdapter} with related recycler view
     */
    private void initFeatureRecyclerView() {
        adapterFeature = new FeatureProductAdapter(new ArrayList<>(), mActivity);
        adapterFeature.setItemClickedListener(this);
        mBinding.rvFeatureProduct.setAdapter(adapterFeature);
        mBinding.rvFeatureProduct.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
    }

    /**
     * init {@link FeatureProductAdapter} with related recycler view
     */
    private void initPopularRecyclerView() {
        adapterPopular = new FeatureProductAdapter(new ArrayList<>(), mActivity);
        adapterPopular.setItemClickedListener(this);
        mBinding.rvPopularProduct.setAdapter(adapterPopular);
        mBinding.rvPopularProduct.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
    }

    /**
     * init {@link ProductRecylerViewAdapter} with related recycler view
     */
    private void initProductRecyclerView() {
        recentProductAdapter = new ProductRecylerViewAdapter(new ArrayList<>(), mActivity, false);
        mBinding.rvPDetailProductGrid.setHasFixedSize(false);
        mBinding.rvPDetailProductGrid.setLayoutManager(new GridLayoutManager(mActivity, 2));
        mBinding.rvPDetailProductGrid.setAdapter(recentProductAdapter);
        mBinding.rvPDetailProductGrid.setNestedScrollingEnabled(false);

        recentProductAdapter.setItemClickedListener(this);
        hasMore = true;
    }

    /**
     * init {@link CategoryRecylerViewAdapter} with related recycler view
     */
    public void settingHorizontalCategoryList() {
        adapterCategory = new CategoryRecylerViewAdapter(new ArrayList<>(), mActivity);
        mBinding.rvProductCategory.setAdapter(adapterCategory);
        mBinding.rvProductCategory.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));

    }

    /**
     * adding slider dots
     */
    private void dots(int current,List<SliderMain> sliderMainList) {
        mBinding.layoutSliderMainDots.removeAllViews();
        for (int i = 0; i < sliderMainList.size(); i++) {
            TextView dot = new TextView(mActivity);
            dot.setIncludeFontPadding(false);
            dot.setHeight((int) UtilityClass.convertDpToPixel(10, mActivity));
            dot.setWidth((int) UtilityClass.convertDpToPixel(10, mActivity));

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int marginAsDp = (int) UtilityClass.convertDpToPixel(4, mActivity);
            params.setMargins(marginAsDp, marginAsDp, marginAsDp, marginAsDp);
            dot.setLayoutParams(params);

            dot.setBackgroundResource(R.drawable.circle_border_bg_1);

            if (i == current) {
                dot.setBackgroundResource(R.drawable.circle_bg);
            }
            mBinding.layoutSliderMainDots.addView(dot);
        }
    }

    /**
     * this api is used to hide view when data not found
     */
    private void setAllTitleGone() {
        mBinding.textViewRecentProducts.setVisibility(View.GONE);
        mBinding.textViewCategory.setVisibility(View.GONE);
        mBinding.textViewPopularProduct.setVisibility(View.GONE);
        mBinding.textViewFeaturedProducts.setVisibility(View.GONE);
    }

    /**
     * init value with data
     *
     * @param response response
     */
    private void setDatatoView(MainProductResponse response) {
        if (response != null && response.statusCode == HttpURLConnection.HTTP_OK) {
            productResponse = response;
            isNetworkOkay = true;
            if (!isNotFirstLoad) {
                if (productResponse.dataModel.recentProduct != null) {
                    if (!productResponse.dataModel.recentProduct.isEmpty()) {
                        mBinding.textViewRecentProducts.setVisibility(View.VISIBLE);
                        recentProductAdapter.addItem(productResponse.dataModel.recentProduct);
                    } else {
                        mBinding.textViewRecentProducts.setVisibility(View.GONE);
                    }
                } else {
                    mBinding.textViewRecentProducts.setVisibility(View.GONE);
                }

                if (productResponse.dataModel.categoryModel != null) {
                    if (!productResponse.dataModel.categoryModel.isEmpty()) {
                        mBinding.textViewCategory.setVisibility(View.VISIBLE);
                        adapterCategory.addItem(productResponse.dataModel.categoryModel);
                    } else {
                        mBinding.textViewCategory.setVisibility(View.GONE);
                    }
                } else {
                    mBinding.textViewCategory.setVisibility(View.GONE);
                }


                if (productResponse.dataModel.popularProduct != null) {
                    if (!productResponse.dataModel.popularProduct.isEmpty()) {
                        mBinding.textViewPopularProduct.setVisibility(View.VISIBLE);
                        adapterPopular.addItem(productResponse.dataModel.popularProduct);
                    } else {
                        mBinding.textViewPopularProduct.setVisibility(View.GONE);
                    }
                } else {
                    mBinding.textViewPopularProduct.setVisibility(View.GONE);
                }

                if (productResponse.dataModel.featureProduct != null) {
                    if (!productResponse.dataModel.featureProduct.isEmpty()) {
                        mBinding.textViewFeaturedProducts.setVisibility(View.VISIBLE);
                        adapterFeature.addItem(productResponse.dataModel.featureProduct);
                    } else {
                        mBinding.textViewFeaturedProducts.setVisibility(View.GONE);
                    }
                } else {
                    mBinding.textViewFeaturedProducts.setVisibility(View.GONE);
                }

                isNotFirstLoad = true;
            } else {
                if (productResponse.dataModel.recentProduct != null) {
                    if (!productResponse.dataModel.recentProduct.isEmpty()) {
                        mBinding.textViewRecentProducts.setVisibility(View.VISIBLE);
                        recentProductAdapter.addItem(productResponse.dataModel.recentProduct);
                    } else {
                        mBinding.textViewRecentProducts.setVisibility(View.GONE);
                    }
                } else {
                    mBinding.textViewRecentProducts.setVisibility(View.GONE);
                }

                mBinding.progressBar.setVisibility(View.GONE);
            }
        } else {
            setAllTitleGone();
        }
    }

}
