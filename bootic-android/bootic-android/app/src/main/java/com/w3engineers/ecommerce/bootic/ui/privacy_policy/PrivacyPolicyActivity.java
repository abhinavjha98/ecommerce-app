package com.w3engineers.ecommerce.bootic.ui.privacy_policy;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;
import android.widget.TextView;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BaseActivity;
import com.w3engineers.ecommerce.bootic.data.util.Constants;

import androidx.appcompat.widget.Toolbar;

public class PrivacyPolicyActivity extends BaseActivity<PrivacyPolicyMvpView,PrivacyPolicyPresenter> {


    /**
     * Run Activity
     * @param context mActivity
     */
    public static void runActivity(Context context) {
        Intent intent = new Intent(context, PrivacyPolicyActivity.class);
        runCurrentActivity(context, intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_privacy_policy;
    }

    @Override
    protected void startUI() {

        WebView webview = (WebView) findViewById(R.id.web_view);
        webview.loadUrl(Constants.ServerUrl.PRIVACY_POLICY);

        initToolbar();

    }

    private void initToolbar() {
        TextView toobarTitle = findViewById(R.id.toolbar_title);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(this.getString(R.string.title_privacy_policy));
        toobarTitle.setText("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void stopUI() {

    }

    @Override
    protected PrivacyPolicyPresenter initPresenter() {
        return new PrivacyPolicyPresenter();
    }
}
