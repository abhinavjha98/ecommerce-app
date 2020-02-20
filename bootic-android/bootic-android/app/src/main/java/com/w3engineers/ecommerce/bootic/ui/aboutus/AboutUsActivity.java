package com.w3engineers.ecommerce.bootic.ui.aboutus;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.widget.Toolbar;

import android.webkit.WebView;
import android.widget.TextView;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BaseActivity;
import com.w3engineers.ecommerce.bootic.data.util.Constants;

public class AboutUsActivity extends BaseActivity<AboutUsMvpView,AboutUsPresenter> implements AboutUsMvpView {

    /**
     * Run Activity
     * @param context mActivity
     */
    public static void runActivity(Context context) {
        Intent intent = new Intent(context, AboutUsActivity.class);
        runCurrentActivity(context, intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_us;
    }

    @Override
    protected void startUI() {
        WebView webView = (WebView) findViewById(R.id.web_view);
        webView.loadUrl(Constants.ServerUrl.ABOUT_US);
        initToolbar();
    }


    private void initToolbar() {
        TextView toobarTitle = findViewById(R.id.toolbar_title);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(this.getString(R.string.title_about_us));
        toobarTitle.setText("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    protected void stopUI() {

    }

    @Override
    protected AboutUsPresenter initPresenter() {
        return new AboutUsPresenter();
    }
}
