package com.w3engineers.ecommerce.bootic.data.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import java.util.Locale;

public class LocaleHelper {

    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";

    public static Context onAttach(Context context) {

        return setLocale(context, getLanguage());
    }

    public static Context onAttach(Context context, String defaultLanguage) {

        String lang = getLanguage();


        if (lang.isEmpty())
            lang = defaultLanguage;

        return setLocale(context, lang);
    }

    public static String getLanguage() {

        return Locale.getDefault().getLanguage();
    }


    public static Context setLocale(Context context, String language) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }

        return updateResourcesLegacy(context, language);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {

        Log.e("updateResources", " Passed Language " + language);

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        String s = getLanguage();

        Log.e("updateResources", " Saved Language " + s);


        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());


        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());


        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {

        Log.e("updateResourcesLegacy", " Passed Language " + language);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        String s = getLanguage();

        Log.e("updateResourcesLegacy", " Saved Language " + s);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context.createConfigurationContext(configuration);
    }
}
