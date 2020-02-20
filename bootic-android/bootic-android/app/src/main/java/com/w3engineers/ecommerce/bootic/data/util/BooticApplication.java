package com.w3engineers.ecommerce.bootic.data.util;

import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.multidex.MultiDexApplication;

import com.droidnet.DroidNet;
import com.facebook.FacebookSdk;
import com.google.firebase.messaging.FirebaseMessaging;
import com.w3engineers.ecommerce.bootic.data.helper.database.DatabaseUtil;

import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;

public class BooticApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseMessaging.getInstance().subscribeToTopic("message");
        FacebookSdk.sdkInitialize(getApplicationContext());
        DatabaseUtil.init(getApplicationContext());
        DroidNet.init(this);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        setLocale();
    }

    private void setLocale() {
        final Resources resources = getResources();
        final Configuration configuration = resources.getConfiguration();

        String currentLang = LocaleHelper.getLanguage();

        Locale locale = new Locale(currentLang);

        if (!configuration.locale.equals(locale)) {

            resources.updateConfiguration(configuration, null);
        }
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        DroidNet.getInstance().removeAllInternetConnectivityChangeListeners();
    }
}
