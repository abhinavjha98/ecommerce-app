package com.w3engineers.ecommerce.bootic.ui.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.CustomMenuBaseActivity;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.LocaleHelper;
import com.w3engineers.ecommerce.bootic.data.util.SharedPref;
import com.w3engineers.ecommerce.bootic.databinding.ActivitySettingBinding;
import com.w3engineers.ecommerce.bootic.ui.main.MainActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

public class SettingActivity extends CustomMenuBaseActivity<SettingMvpView, SettingPresenter> implements SettingMvpView {

    private ActivitySettingBinding mBinding;
    private Toolbar toolbar;
    boolean isChecked;
    /**
     * Run Activity
     *
     * @param context mActivity
     */
    public static void runActivity(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        runCurrentActivity(context, intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void startUI() {
        mBinding= (ActivitySettingBinding) getViewDataBinding();

        initToolbar();
        String language= SharedPref.getSharedPref(SettingActivity.this).read(Constants.Preferences.LANGUAGE_KEY);
        if (language!=null&& !language.equals("")){
            if (language.equals("en")){
                mBinding.rbEnglish.setChecked(true);
                LocaleHelper.setLocale(SettingActivity.this, "en");
            }else {
                mBinding.rbFrench.setChecked(true);
                LocaleHelper.setLocale(SettingActivity.this, "fr");
            }
        }else {
            mBinding.rbEnglish.setChecked(true);
            LocaleHelper.setLocale(SettingActivity.this, "en");
        }


        mBinding.rgLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                 isChecked = mBinding.rbEnglish.isChecked();

            }
        });


        setClickListener( mBinding.rbEnglish,mBinding.rbFrench);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.rb_english:
            case R.id.rb_french:
                showDialogPopUp(isChecked);
                break;

        }
    }



    /**
     * log out pop up alert
     */
    private void showDialogPopUp(boolean isChecked) {
        new AlertDialog.Builder(SettingActivity.this)
                .setTitle(getResources().getString(R.string.setting))
                .setMessage(getResources().getString(R.string.change_lan_dia))
                .setPositiveButton(Html.fromHtml("<font color='#ff0000'>Ok</font>"),
                        (dialog, whichButton) -> {
                            if (isChecked) //english
                            {
                                //set the english language and save shared
                                LocaleHelper.setLocale(SettingActivity.this, "en");
                                SharedPref.getSharedPref(SettingActivity.this).write(Constants.Preferences.LANGUAGE_KEY,"en");
                                runActivityMain();

                            }else {
                                LocaleHelper.setLocale(SettingActivity.this, "fr");
                                SharedPref.getSharedPref(SettingActivity.this).write(Constants.Preferences.LANGUAGE_KEY,"fr");
                                runActivityMain();
                            }

                        })
                .setNegativeButton(Html.fromHtml("<font color='#444444'>Cancel</font>"),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (isChecked){ //english
                                    mBinding.rbEnglish.setChecked(false);
                                }else {
                                    mBinding.rbEnglish.setChecked(true);
                                }
                            }
                        }).show();
    }



    /**
     * init toolbar
     */
    private void initToolbar() {
        TextView toobarTitle = findViewById(R.id.toolbar_title);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toobarTitle.setText(this.getString(R.string.setting));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void runActivityMain(){
        Intent refresh = new Intent(this, MainActivity.class);
        finish();
        startActivity(refresh);

    }



    @Override
    protected void stopUI() {

    }

    @Override
    protected SettingPresenter initPresenter() {
        return new SettingPresenter();
    }

}
