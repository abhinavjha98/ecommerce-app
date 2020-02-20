package com.w3engineers.ecommerce.bootic.data.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.w3engineers.ecommerce.bootic.data.helper.models.AddressModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.EncryptionModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.InventoryServerModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.InvoiceModel;
import com.w3engineers.ecommerce.bootic.ui.main.MainActivity;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserRegistrationResponse;

import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class UtilityClass {

    public static final String TOOLBAR_TITLE = "toolbar_title";
    public static boolean IS_PRODUCT_MAKE_FAVORITE = false;
    public static boolean IS_PRODUCT_MAKE_FAVORITE_OFFER = false;
    public static void textViewScaleIconLeft(Context context, View v, int imageId, double opacity) {

        int alpha = (int) (255 * opacity);

        EditText conatiner = (EditText) v;
        int imageResource = imageId;
        Drawable drawable = ContextCompat.getDrawable(context, imageResource);
        drawable.setAlpha(alpha);
        int pixelDrawableSize = (int) Math.round(conatiner.getLineHeight() * 1); // Or the percentage you like (0.8, 0.9, etc.)
        drawable.setBounds(0, 0, pixelDrawableSize, pixelDrawableSize); // setBounds(int left, int top, int right, int bottom), in this case, drawable is a square image
        conatiner.setCompoundDrawables(
                drawable,//left
                null, //top
                null, //right
                null //bottom
        );
    }

    public static void buttonScaleIconLeft(Context context, View v, int imageId, double opacity) {
        int alpha = (int) (255 * opacity);

        Button conatiner = (Button) v;
        int imageResource = imageId;
        Drawable drawable = ContextCompat.getDrawable(context, imageResource);
        drawable.setAlpha(alpha);
        int pixelDrawableSize = (int) Math.round(conatiner.getLineHeight() * 1); // Or the percentage you like (0.8, 0.9, etc.)
        drawable.setBounds(0, 0, pixelDrawableSize, pixelDrawableSize); // setBounds(int left, int top, int right, int bottom), in this case, drawable is a square image
        conatiner.setCompoundDrawables(
                drawable,//left
                null, //top
                null, //right
                null //bottom
        );
    }

    public static void buttonScaleIconRight(Context context, View v, int imageId, double opacity, double size) {
        int alpha = (int) (255 * opacity);

        Button conatiner = (Button) v;
        int imageResource = imageId;
        Drawable drawable = ContextCompat.getDrawable(context, imageResource);
        drawable.setAlpha(alpha);
        int pixelDrawableSize = (int) Math.round(conatiner.getLineHeight() * size); // Or the percentage you like (0.8, 0.9, etc.)
        drawable.setBounds(0, 0, pixelDrawableSize, pixelDrawableSize); // setBounds(int left, int top, int right, int bottom), in this case, drawable is a square image
        conatiner.setCompoundDrawables(
                null,//left
                null, //top
                drawable, //right
                null //bottom
        );
    }


    public static String getNumberFormat(Context context, double price) {
        String price2;
        if (price % 1 == 0) {
            DecimalFormat df = new DecimalFormat("#0.0");
            df.setRoundingMode(RoundingMode.DOWN);
            price2 = df.format(price);

        } else {
            DecimalFormat df = new DecimalFormat("#0.00");
            price2 = String.valueOf(df.format(price));
        }
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        //String currency = String.valueOf(nf.format(23.2).charAt(0));
        String currency = CustomSharedPrefs.getCurrency(context);
        return String.valueOf(currency + price2);
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static Date stringToDate(String dateASstring) throws ParseException {
        //String string = "January 2, 2010";
        DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        Date date = format.parse(dateASstring);
        return date;
    }

    /*public static void removeFavProduct(int productId, Context mActivity){
        DataSource dataSource = new DataSource(mActivity);
        dataSource.removeProductById(productId);

    }*/



    public static String objectToString(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static String objectToStrings(List<InventoryServerModel> obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static String objectToStringsEncryptionModel(EncryptionModel obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }


    public static AddressModel stringToAddress(String jsonProduct) {
        Gson gson = new Gson();
        return gson.fromJson(jsonProduct, AddressModel.class);
    }

    public static AddressModel stringToAddressModel(String jsonProduct) {
        Gson gson = new Gson();
        return gson.fromJson(jsonProduct, AddressModel.class);
    }

    public static <T> Object stringToObject(String jsonProduct, Class<T> obj) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<T>(){}.getType();
        return gson.fromJson(jsonProduct, collectionType);
    }

    public static String userToJson(UserRegistrationResponse user) {
        Gson gson = new Gson();
        return gson.toJson(user);
    }

    public static UserRegistrationResponse jsonToUser(String jsonUser) {
        Gson gson = new Gson();
        return gson.fromJson(jsonUser, UserRegistrationResponse.class);
    }


    public static String inventoryListToJson(InvoiceModel invoiceModel) {
        Gson gson = new Gson();
        return gson.toJson(invoiceModel);
    }

    public static InvoiceModel jsonToInvoice(String jsonInvoice) {
        Gson gson = new Gson();
        return gson.fromJson(jsonInvoice, InvoiceModel.class);
    }

    public static String capFirstLetter(String value) {

        return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static void cartEmptyRedirect(Context context) {
        if (CustomSharedPrefs.getCartEmpty(context)) {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
    }

    public static void signOutFB(Context context) {
        LoginManager.getInstance().logOut();
        CustomSharedPrefs.removeLoggedInUser(context);
    }

    public static void signOutGoogle(GoogleApiClient gApiClient, Context context) {
        Auth.GoogleSignInApi.signOut(gApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Log.d("emailGoogl", "Loggedout");
            }
        });
        CustomSharedPrefs.removeLoggedInUser(context);
    }

    public static void signOutEmail(Context context) {
        CustomSharedPrefs.setLoggedInUser(context, null);
        CustomSharedPrefs.removeLoggedInUser(context);
    }

    /***
     * Get local time
     * @param formServerDateValue from server
     * @param dateFormat given from local
     * @return local date
     */
    public static String getDateStringFromDateValue(String formServerDateValue, String dateFormat) {
        String dateString = "";
        try {
            String pattern = "yyyy-MM-dd hh:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = simpleDateFormat.parse(formServerDateValue);


            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(dateFormat);
            dateString = simpleDateFormat2.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return dateString;//DateFormat.format(dateFormat, getTimestampInMili(dateValue)).toString();
    }

    /**
     * Convert time to local time
     * @param dateFromServer date From Server
     * @return String local time
     */
    public static String convertTimeToLocalTime(String dateFromServer){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = null;
        try {
            date = df.parse(dateFromServer);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        df.setTimeZone(TimeZone.getDefault());
        return df.format(date);


    }

    /**
     * Get today date
     * @return current date
     */
    public static String getTodayDate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(Constants.DateFormat.DATE_FORMAT_TIME);
        String formattedDate = df.format(c);
        return formattedDate;
    }

    /**
     * Get currency symbol and price also
     * @param context context
     * @param amount amount
     */
    public static String getCurrencySymbolAndAmount(Context context,float amount){
        String currencyPosition= SharedPref.getSharedPref(context).read(Constants.Preferences.CURRENCY_POSITION);
        String currencySymbol = CustomSharedPrefs.getCurrency(context);
        String ammount="";
        if (currencyPosition.equals(Constants.DefaultValue.CURRENCY_APPEND)){
            ammount=amount+" "+currencySymbol;
            return ammount;
        }
        if (currencyPosition.equals(Constants.DefaultValue.CURRENCY_PREPEND)){

            ammount=currencySymbol+amount;
            return ammount;
        }

        return ammount;
    }

    /**
     * Hide keyboard
     * @param activity activity
     */
    public static void hideSoftInput(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view == null) view = new View(activity);
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



}
