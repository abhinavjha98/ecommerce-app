package com.w3engineers.ecommerce.bootic.data.util;

public class Constants {

    public interface SharedPrefCredential {
        public String SHARED_PREF_USER = "sharedPreferencesUserBootic";
        public String SHARED_PREF_LOGGEDIN_USER = "sharedPreferencesLoggedInUserBootic";
        public String SHARED_PREF_CURRENCY = "sharedPreferencesCurrencyBootic";
        public String SHARED_PREF_CURRENCY_IN = "sharedPreferencesCurrencyInBootic";
        public String SHARED_PREF_EMPTY_CART = "sharedPreferencesEmptyCartBootic";
        public String SHARED_PREF_EMPTY_CART_IN = "sharedPreferencesEmptyCartInBootic";
        public String SHARED_PREF_PROFILEIMAGE = "sharedPreferencesProfileImageBootic";
        public String SHARED_PREF_PROFILEIMAGE_IN = "sharedPreferencesProfileImageInBootic";
        public String PRODUCT_DETAIL_INTENT = "productDetailIntentBootic";
        public String INTENT_CATEGORY_ID = "category_id";
        public String MAIN_RESPONSE = "main_response";
    }


    public interface Preferences {
        String USER_REGISTRATION = "registered";

        String CURRENCY = "currency";
        String CURRENCY_FONT = "font";

        String BANNER_STATUS = "banner_status";
        String BANNER_UNIT_ID = "banner_unit_id";
        String BANNER_APP_ID = "banner_app_id";
        String INTERSTITIAL_STATUS = "interstitial_status";
        String INTERSTITIAL_UNIT_ID = "interstitial_unit_id";
        String INTERSTITIAL_APP_ID = "interstitial_app_id";
        String STATUS_ON = "1";
        String USER_ADDRESS = "address";
        String USER_PROFILE_IMAGE = "pro_image";
        String TAX = "tax";
        String ENVIRONMENT = "envo";
        String MERCHANT_ID = "m_id";
        String PUBLIC_KEY = "pub_id";
        String PRIVATE_KEY = "pri_id";
        String SAVE_CURRENT_DATA="current_inventory";

        String TRANSACTION_ID="transaction_id";
        String INVOICE_TAX="invoice_tax";
        String INVOICE_SUB_TOTAL_AMOUNT="invoice_sub_total_amount";
        String INVOICE_TOTAL_AMOUNT="invoice_total_amount";
        String CURRENT_DATE="current_date";
        String COMPANY_NAME="company_name";
        String COMPANY_ADDRESS="address_name";
        String INVOICE_PRODUCT_LIST="invoice_product_list";
        String CURRENCY_POSITION="currency_type";
        String FAV_UPDATED_ID_AND_STATUS="updated_id_and_status";
        String ADAPTER_NAME="adapter_name";
        String LANGUAGE_KEY="language_key";
    }


    public interface EncryptionKey{
        String ENCRYPTION_KEY="fedcba9876543210";
        String ENCRYPTION_IV="0123456789abcdef";
    }

    public interface IntentKey {
        String PAYMENT_RESPONSE = "pay";
        String TOTAL_AMOUNT = "amount";
        String PAYMENT_METHOD = "payment_method";
        String FLAG_ORDER = "success";
        String FLAG_REVIEW_GIVEN="review_given";
        public String INTENT_SLIDER_ID = "category_id";
        String ITEM_ID = "item_id";
        String IS_ORDERED = "ordered";
    }

    public interface ServerUrl {
        String API_TOKEN = "www";
      //  String MAIN_URL = "http://192.168.2.71/bootic/public/";
        String MAIN_URL = "http://devrepo.in/public/`";
        String ROOT_URL = MAIN_URL + "api/";
        String FULL_IMAGE_URL = MAIN_URL + "uploads/";
        String THUMB_IMAGE_URL = MAIN_URL + "uploads/thumb/";
        String PRIVACY_POLICY = MAIN_URL + "privacy_policy.html";
        String ABOUT_US = MAIN_URL + "about_us.html";

        //end url
        String LOGIN_URL = "user/login.php";
        String SIGNUP_URL = "user/register.php";
        String EMAIL_VERIFICATION = "user/email-verification.php";
        String RESEND_CODE = "user/send-code.php";
        String UPDATE_PASSWORD = "user/update-password.php";
        String ADMOB_URL = "admobs/admob.php";
        String SETTINGS_URL = "settings/setting.php";
        String MAIN_PRODUCT_URL = "product/home.php";
        String PRODUCT_DETAILS_URL = "product/one.php";
        String ADD_TO_CART="cart/add.php";
        String CART_BY_USER="cart/by-user.php";
        String REMOVE_FROM_CART="cart/remove.php";


        String CATEGORY_URL = "category/all.php";
        String PRODUCT_GRID_URL = "product/by-category.php";
        String USER_ADDRESS = "address/add.php";
        String UPLOAD_PROFILE_IMAGE = "user/upload-profile-image.php";
        String ADD_REVIEW = "review/add.php";
        String SHOW_REVIEW = "review/by-item.php";

        String BRAIN_TREE = "order/braintree.php";
        String ORDER_URL = "order/add-encrypted.php";
        String GET_ALL_ADDRESS_URL = "address/by-user.php";
        String REMOVE_ADDRESS_URL = "address/remove.php";

    }

    public interface DefaultValue {
        int WELCOME_DELAY = 1000;
        String REMEMBER_ME = "yes";
        int DELAY_INTERVAL_VISIBILITY = 60000;
        int DELAY_INTERVAL = 1000;
        int MINIMUM_LENGTH_PASS = 6;
        int PIN_CODE_LIMIT = 4;
        int MAXIMUM_LENGTH = 20;
        long REFREASH_LOCATION = 300000;
        long DISTANCE_LOCATION = 500;
        int MINIMUM_LENGTH_TEXT = 2;
        int LOADER_DELAY = 500;
        String STATUS_ON = "1";
        float RATING_ZERO = 0;
        String CURRENCY_APPEND="1";
        String CURRENCY_PREPEND="2";
        String FEATURE="FEATURE";
        String RECENT="RECENT";
        String POPULAR="POPULAR";


    }

    public interface RegistrationType {
        int MANUAL_SIGN_UP = 1;
        int FACEBOOK_SIGN_UP = 2;
        int GOOGLE_SIGN_UP = 3;

    }

    public interface DateFormat {
        String GMT = "GMT";
        String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        String DATE_FORMAT_VALIDITY = "dd MMM, yyyy";
        String DATE_WITH_MONTH_FIRST = "MMMM dd, yyyy";
        String DATE_FORMAT_TIME = "h:mm a, dd MMM, yyyy";
    }

}
