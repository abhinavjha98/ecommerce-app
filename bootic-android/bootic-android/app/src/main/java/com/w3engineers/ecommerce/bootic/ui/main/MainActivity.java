package com.w3engineers.ecommerce.bootic.ui.main;

import android.content.Intent;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.widget.Toolbar;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BaseFragment;
import com.w3engineers.ecommerce.bootic.data.helper.base.CustomMenuBaseActivity;
import com.w3engineers.ecommerce.bootic.data.helper.database.DatabaseUtil;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserRegistrationResponse;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.SharedPref;
import com.w3engineers.ecommerce.bootic.data.util.UIHelper;
import com.w3engineers.ecommerce.bootic.data.util.UtilityClass;
import com.w3engineers.ecommerce.bootic.databinding.ActivityMainBinding;
import com.w3engineers.ecommerce.bootic.ui.aboutus.AboutUsActivity;
import com.w3engineers.ecommerce.bootic.ui.category.CategoryActivity;
import com.w3engineers.ecommerce.bootic.ui.dashboard.DashboardFragment;
import com.w3engineers.ecommerce.bootic.ui.myfavourite.UserFavActivity;
import com.w3engineers.ecommerce.bootic.ui.ordercomplete.OrderCompleteActivity;
import com.w3engineers.ecommerce.bootic.ui.privacy_policy.PrivacyPolicyActivity;
import com.w3engineers.ecommerce.bootic.ui.userLogin.LoginActivity;
import com.w3engineers.ecommerce.bootic.ui.userProfile.ProfileActivity;


public class MainActivity extends CustomMenuBaseActivity<MainMvpView, MainPresenter> implements MainMvpView {

    private TextView toobarTitle, tvEmail, tvName;
    private ImageView toobarLogo, ivProfileImage;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private boolean isRegistered;
    private ActivityMainBinding mBinding;
    private UserRegistrationResponse currentUser;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRegistered = CustomSharedPrefs.getUserStatus(this);
        currentUser = CustomSharedPrefs.getLoggedInUser(this);
        updateMenu();
        setLoginInfo();
        UIHelper.setThumbImageUriInView(ivProfileImage, SharedPref.getSharedPref(this).read(Constants.Preferences.USER_PROFILE_IMAGE));

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected void startUI() {
        mBinding = (ActivityMainBinding) getViewDataBinding();
        isRegistered = CustomSharedPrefs.getUserStatus(this);
        settingToolBar();
        settingNavDrawer();
        loadDefaultFragment();
        presenter.getAdMobCredential(this);
      //  presenter.getSettingCredential(this);
    }

    private void loadDefaultFragment() {
        BaseFragment mFragment = new DashboardFragment();
        commitFragment(R.id.fragment_container, mFragment);
    }


    @Override
    protected void stopUI() {

    }


    @Override
    protected MainPresenter initPresenter() {
        return new MainPresenter();
    }

    /**
     * init toolbar with logo image
     */

    public void settingToolBar() {
        toolbar = findViewById(getToolbarId());
        toobarTitle = findViewById(R.id.toolbar_title);
        toobarLogo = findViewById(R.id.toolbar_logo);
        toolbar.setTitle("");
        toobarTitle.setText(this.getString(R.string.app_name));
        toobarLogo.setVisibility(View.VISIBLE);
        toobarTitle.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * setting navigation drawer
     */
    private void settingNavDrawer() {

        drawerLayout = findViewById(R.id.dl_navigation_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        navigationView = findViewById(R.id.nv_main_nav);
        View header = navigationView.getHeaderView(0);
        tvName = header.findViewById(R.id.menu_profile_name);
        tvEmail = header.findViewById(R.id.menu_profile_email);
        setLoginInfo();

        ivProfileImage = header.findViewById(R.id.iv_menu_profile_image);
        UIHelper.setThumbImageUriInView(ivProfileImage, SharedPref.getSharedPref(this).read(Constants.Preferences.USER_PROFILE_IMAGE));

        header.setOnClickListener(v -> {
            if (!isRegistered) {
                Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intentLogin);
            } else {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
            overridePendingTransition(R.anim.enter, R.anim.exit);
            drawerLayout.closeDrawers();
        });

        setLoginInfo();
        getNavigationItemClick(navigationView);
    }

    /**
     * getting click event of navigation drawer
     *
     * @param nvMainNav
     */
    private void getNavigationItemClick(NavigationView nvMainNav) {
        nvMainNav.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawers();
            switch (item.getItemId()) {
                case R.id.nav_category:
                    startActivity(new Intent(MainActivity.this, CategoryActivity.class));
                    break;
                case R.id.nav_orders:
                    if (!isRegistered) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, OrderCompleteActivity.class));
                    }
                    break;
                case R.id.nav_favourites:
                    if (!isRegistered) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, UserFavActivity.class));
                    }
                    break;
                case R.id.nav_logout:
                    if (isRegistered) {
                        showLogoutPopUp();
                    } else {
                        item.setTitle(getString(R.string.string_login));
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                    break;
                case R.id.nav_about_us:
                    AboutUsActivity.runActivity(this);
                    break;
                case R.id.nav_privacy_policy:
                    PrivacyPolicyActivity.runActivity(this);
                    break;
            }

            overridePendingTransition(R.anim.enter, R.anim.exit);
            return false;
        });
    }

    /**
     * setting login logout text
     */
    private void setLoginInfo() {
        String name = "", email = "";
        if (isRegistered) {
            UserRegistrationResponse user = CustomSharedPrefs.getLoggedInUser(this);
            if (user != null) {
                if (user.userRegistrationInfo != null) {
                    name = user.userRegistrationInfo.username;
                    email = user.userRegistrationInfo.email;
                }
                tvName.setText(name);
                tvEmail.setText(email);

            } else {
                tvName.setText(getString(R.string.sign_in));
                tvEmail.setText("");
            }
        } else {
            tvName.setText(getString(R.string.sign_in));
            tvEmail.setText("");
        }
        MenuItem loggedOut = navigationView.getMenu().findItem(R.id.nav_logout);
        if (!CustomSharedPrefs.getUserStatus(MainActivity.this))
            loggedOut.setTitle(getString(R.string.string_login));
        else loggedOut.setTitle(getString(R.string.string_logout));
    }

    /**
     * log out pop up alert
     */
    private void showLogoutPopUp() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.string_logout))
                .setMessage(getString(R.string.want_to_logout))
                .setIcon(R.drawable.ic_logout)
                .setPositiveButton(Html.fromHtml("<font color='#ff0000'>Ok</font>"),
                        (dialog, whichButton) -> {
                    setLoginInfo();
                    CustomSharedPrefs.removeLoggedInUser(MainActivity.this);
                    isRegistered = false;
                    SharedPref.getSharedPref(MainActivity.this).write(Constants.Preferences.USER_PROFILE_IMAGE, "");
                    AsyncTask.execute(new Runnable() {
                                          @Override
                                          public void run() {
                                              DatabaseUtil.on().deleteAll();
                                          }
                                      }
                    );
                    UtilityClass.signOutFB(MainActivity.this);
                    UtilityClass.signOutEmail(MainActivity.this);
                    Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                })
                .setNegativeButton(Html.fromHtml("<font color='#444444'>Cancel</font>"),
                        null).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("On _result"," OnActivity result called at activity");
        super.onActivityResult(requestCode, resultCode, data);
    }
}
